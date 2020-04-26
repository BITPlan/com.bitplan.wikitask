/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.wikitask open source project
 *
 * Copyright 2015-2020 BITPlan GmbH https://github.com/BITPlan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *
 *  http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.topic;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sidif.triple.TripleStore;
import org.sidif.wiki.Reference;
import org.sidif.wiki.ReferenceManager;
import org.sidif.wiki.SMW_Triples;
import org.sidif.wiki.TripleStoreManager;
import org.sidif.wiki.WikiManager;

import com.bitplan.topic.TopicStatic.Context;
import com.bitplan.topic.TopicStatic.ContextManager;

/**
 * the context factory
 * 
 * @author wf
 *
 */
public class ContextFactory {
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.topic");
	/**
	 * cache for contextManagers
	 */
	public static Map<String, ContextManager> contextManagerByTripleStoreId = new TreeMap<String, ContextManager>();
	public ContextManager mContextManager = null;
	private static boolean reset = false;

	public static final String[] MetaModelTopics={"Context","Topic","TopicLink","Property","SMW_Type","Action"};
	/**
	 * get the SQL where selection for the given topics
	 * @param topics
	 * @return the SQL where string
	 */
	public static String getWhere (String[] topics) {
		/**
		 * given the topic list Context,Topic,TopicLink,Property,SMW_Type,Action the result should be:
		 *  WHERE predicate LIKE 'Property:IsA' \n"
			+ "OR predicate LIKE 'Property:Context_%' \n"
			+ "OR predicate LIKE 'Property:Topic_%' \n"
			+ "OR predicate LIKE 'Property:TopicLink_%'\n"
			+ "OR predicate LIKE 'Property:Property_%'\n"
			+ "OR predicate LIKE 'Property:SMW_Type_%'\n"
			+ "OR predicate LIKE 'Property:Action_%'";
		 */
		String where = " WHERE predicate LIKE 'Property:IsA' ";
		for (String topicName:topics) {
			where+="\nOR predicate LIKE 'Property:"+topicName+"_%' ";
		}
		return where;
	}

	/**
	 * clear my cache
	 */
	public static void reset() {
		LOGGER.log(Level.INFO, "clearing contextManagerByTripleStoreId");
		reset = true;
		contextManagerByTripleStoreId.clear();
	}

	/**
	 * get the tripleStore
	 * 
	 * @return
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	private TripleStore getSMWTripleStore(ContextSetting cs) throws Exception {
		String wikiId = cs.getWikiId();
		TripleStore smwTripleStore = getSMWTripleStore(wikiId,MetaModelTopics);
		return smwTripleStore;
	}
	
	/**
	 * get an SMW TripleStore based on the given wikiId and list of topicNames
	 * @param wikiId
	 * @param topicNames - the topicNames to filter by creating an SQL whereClause
	 * @return the tripleStore
	 * @throws Exception
	 */
	public static TripleStore getSMWTripleStore(String wikiId,String[] topicNames) throws Exception  {
		String localSettings = WikiManager.getLocalSettings(wikiId);
		LOGGER.log(Level.INFO, "getting Triples from mySQL for wiki " + wikiId);
		String where=getWhere(topicNames);
		TripleStore smwTripleStore = SMW_Triples.fromWiki(localSettings,
				SMW_Triples.getQuery(where));
		return smwTripleStore;
	}

	/**
	 * get the SiDIF TripleStore for the given tripleStoreId
	 * 
	 * @param cs
	 * @return
	 * @throws Exception
	 */
	private TripleStore getSiDIFTripleStore(ContextSetting cs) throws Exception {
		ReferenceManager rm = ReferenceManager.getByWikiId(cs.getWikiId());
		if (reset) {
			boolean memoryCache = false;
			rm.getPageCache().check(memoryCache);
			reset = false;
		}
		String sidifInput = cs.getSidifInput();
		if (sidifInput == null)
			throw new IllegalArgumentException(
					"cs.getSidifInput may not be null for tripleStoreMode SiDIF");
		Reference sidifInputReference = rm.getReference(sidifInput);
		TripleStoreManager tm = TripleStoreManager.getInstance();
		TripleStore tripleStore = tm.getByReference(rm, sidifInputReference);
		return tripleStore;
	}

	/**
	 * get the context Manager
	 * 
	 * @param wikiid
	 * @param from
	 * @return
	 */
	public ContextManager getContextManager(ContextSetting cs) {
		TripleStoreManager tm = TripleStoreManager.getInstance();
		String tripleStoreId = cs.getTripleStoreId();
		if (tripleStoreId == null)
			throw new IllegalArgumentException("tripleStoreId may not be null");
		TripleStore tripleStore = tm.getById(tripleStoreId);
		if (tripleStore == null) {
			try {
				switch (cs.getTripleStoreMode()) {
				case SiDIF:
					tripleStore = getSiDIFTripleStore(cs);
					break;
				default:
					tripleStore = getSMWTripleStore(cs);
				}
			} catch (Throwable th) {
				// FIXME error handling
				th.printStackTrace();
				throw new RuntimeException(th);
			}
			tm.add(tripleStoreId, tripleStore);
		}
		ContextManager contextManager = contextManagerByTripleStoreId
				.get(tripleStoreId);
		if (contextManager == null) {
			LOGGER.log(Level.INFO, "loading ContextManager for triplestoreid "
					+ tripleStoreId + " from triples");
			contextManager = new ContextManager(tripleStore);
			contextManagerByTripleStoreId.put(tripleStoreId, contextManager);
		} else {
			LOGGER.log(Level.INFO, "found ContextManager for triplestoreid "
					+ tripleStoreId + " in cache");
		}
		return contextManager;
	}

	/**
	 * get the context with the given name
	 * 
	 * @param contextName
	 * @return
	 */
	public Context getContext(ContextSetting cs) {
		ContextManager contextManager = this.getContextManager(cs);
		String contextName = cs.getContextName();
		Context context = contextManager.mContextMap.get(contextName);
		if (context == null)
			throw new IllegalArgumentException("context " + contextName
					+ " is missing");
		/*
		 * here we could do bootstrapping if needed ... commented out as of
		 * 2015-07-15 if (contextName.equals("MetaModel") && context==null) {
		 * LOGGER.log(Level.INFO,"loading ContextManager from bootstrap SiDIF");
		 * TripleStore tripleStore=TripleStoreBuilder.fromSiDIFText(bootsTrapSiDIF);
		 * ContextManager contextManager=new ContextManager(tripleStore); context =
		 * contextManager.mContextMap.get(contextName); }
		 */
		return context;
	}

	// private constructor to force singleton access
	private ContextFactory() {
	};

	static ContextFactory instance = null;

	/**
	 * singleton access to the context Factory
	 * 
	 * @return
	 */
	public static ContextFactory getInstance() {
		if (instance == null) {
			instance = new ContextFactory();
		}
		return instance;
	}

}
