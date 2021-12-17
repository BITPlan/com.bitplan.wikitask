/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.wikitask open source project
 *
 * Copyright 2015-2022 BITPlan GmbH https://github.com/BITPlan
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
package org.sidif.wiki;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sidif.triple.TripleStore;
import org.sidif.util.TripleStoreBuilder;

/**
 * Manager for triple Stores
 * 
 * @author wf
 *
 */
public class TripleStoreManager {
	private static TripleStoreManager instance = null;
	Map<String, TripleStore> tripleStoresById = new TreeMap<String, TripleStore>();
	protected static Logger LOGGER = Logger.getLogger("org.sidif.wiki");

	/**
	 * get the TripleStore with the given id
	 * 
	 * @param id
	 * @return TripleStore
	 */
	public TripleStore getById(String id) {
		LOGGER.log(Level.INFO, "getting triplestore with id " + id);
		TripleStore tripleStore = this.tripleStoresById.get(id);
		return tripleStore;
	}

	/**
	 * add the given TripleStore with the given id
	 * 
	 * @param id
	 * @param tripleStore
	 */
	public void add(String id, TripleStore tripleStore) {
		tripleStoresById.put(id, tripleStore);
	}

	/**
	 * singleton access
	 * 
	 * @return TripleStoreManager
	 */
	public static TripleStoreManager getInstance() {
		if (instance == null) {
			instance = new TripleStoreManager();
		}
		return instance;
	}

	/**
	 * add the given TripleStore by id from the given SIDIF Text
	 * 
	 * @param id
	 * @param sidifText
	 * @return TripleStore
	 * @throws Exception
	 */
	public TripleStore addBySiDIF(String id, String sidifText) throws Exception {
		TripleStore tripleStore = TripleStoreBuilder.fromSiDIFText(sidifText);
		add(id, tripleStore);
		return tripleStore;
	}

	/**
	 * get the tripleStore by the given reference
	 * 
	 * @param referenceManager
	 * @param inputLink
	 * @return TripleStore
	 * @throws Exception
	 */
	public TripleStore getByReference(ReferenceManager referenceManager,
			Reference inputLink) throws Exception {
		SSLWiki sslWiki = referenceManager.getSslWiki();
		if (inputLink == null) {
			throw new IllegalStateException("inputLink may not be null");
		}
		String tripleStoreId = sslWiki.getWikiid() + "::"
				+ inputLink.getReferenceId();
		TripleStore tripleStore = getById(tripleStoreId);
		if (tripleStore == null) {
			Source sidifSource = referenceManager.getSource(inputLink);
			if (sidifSource != null) {
				tripleStore = addBySiDIF(tripleStoreId, sidifSource.source);
			} else {
				throw new IllegalArgumentException("getting SiDIF from inputLink "
						+ inputLink.getReferenceId() + " with tripleStoreId "
						+ tripleStoreId + " failed");
			}
		}
		return tripleStore;
	}

}
