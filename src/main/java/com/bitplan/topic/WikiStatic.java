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
package com.bitplan.topic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.sidif.triple.Triple;
import org.sidif.triple.TripleQuery;

import com.alibaba.fastjson.JSON;
import com.bitplan.topic.TopicStatic.TopicBase;

/**
 * wiki static code
 * @author wf
 *
 */
public class WikiStatic {
	 /**
	  * Wiki
	  * A wiki is an application, typically a web application, which allows collaborative modification, extension, or deletion of its content and structure. In a typical wiki, text is written using a simplified markup language (known as 'wiki markup') or a rich-text editor. While a wiki is a type of content management system, it differs from a blog or most other such systems in that the content is created without any defined owner or leader, and wikis have little implicit structure, allowing structure to emerge according to the needs of the users.
	  */
	  public static class Wiki extends TopicBase {
	    String pageid;
	    String id;
	    String siteurl;
	    String scriptPath;
	    String language;
	 
	    public String getId() { return id; }
	    public void setId(String pId) { id=pId; }
	    public String getSiteurl() { return siteurl; }
	    public void setSiteurl(String pSiteurl) { siteurl=pSiteurl; }
	    public String getScriptPath() { return scriptPath; }
	    public void setScriptPath(String pScriptPath) { scriptPath=pScriptPath; }
	    public String getLanguage() { return language; }
	    public void setLanguage(String pLanguage) { language=pLanguage; }
	    /**
	     * convert this Wiki to a JSON string
	     */
	    public String toJson() { return JSON.toJSONString(this); }
	 
	    /**  
	     * get the pageid for this topic
	     */
	    public String getPageid() { return pageid; };
	 
	    /**
	     * default constructor for Wiki
	     */
	    public Wiki() {}
	 
	    /**
	     * construct a Wiki from the given Triple
	     * @param query - the TripleQuery to get the triples from
	     * @param pWikiTriple - the triple to construct me from
	     */
	    public Wiki(TripleQuery query,Triple pWikiTriple) {
	      this(query,pWikiTriple.getSubject().toString());
	    } // constructor
	 
	    /**
	     * construct a Wiki from the given pageId
	     * @param query - the TripleQuery to get the triples from
	     * @param pageid - pageid
	     */
	    public Wiki(TripleQuery query,String pageid) {
	      this.pageid=pageid;
	      Triple idTriple=query.selectSingle(pageid,"id",null);
	      if (idTriple==null)
	        idTriple=query.selectSingle(pageid,"Property:Wiki_id",null);
	      if (idTriple!=null) 
	        id=idTriple.getObject().toString();
	      Triple siteurlTriple=query.selectSingle(pageid,"siteurl",null);
	      if (siteurlTriple==null)
	        siteurlTriple=query.selectSingle(pageid,"Property:Wiki_siteurl",null);
	      if (siteurlTriple!=null) 
	        siteurl=siteurlTriple.getObject().toString();
	      Triple scriptPathTriple=query.selectSingle(pageid,"scriptPath",null);
	      if (scriptPathTriple==null)
	        scriptPathTriple=query.selectSingle(pageid,"Property:Wiki_scriptPath",null);
	      if (scriptPathTriple!=null) 
	        scriptPath=scriptPathTriple.getObject().toString();
	      Triple languageTriple=query.selectSingle(pageid,"language",null);
	      if (languageTriple==null)
	        languageTriple=query.selectSingle(pageid,"Property:Wiki_language",null);
	      if (languageTriple!=null) 
	        language=languageTriple.getObject().toString();
	      init(query);
	    } // constructor for Wiki
	 
	    // >>>{user defined topic code}{Wiki}{Wiki}
	    // <<<{user defined topic code}{Wiki}{Wiki}
	  } // class Wiki
	  /**
	   * Manager for Wiki
	   */
	  public static class WikiManager extends TopicBase {
	 
	    public String topicName="Wiki";
	    public List<Wiki> mWikis=new ArrayList<Wiki>();
	    public Map<String,Wiki> mWikiMap=new LinkedHashMap<String,Wiki>();
	 
	    /**
	     *  add a new Wiki 
	     */
	    public Wiki add(Wiki pWiki) {
	      mWikis.add(pWiki);
	      mWikiMap.put(pWiki.getPageid(),pWiki);
	      return pWiki;
	    }
	    
	    /**
	     * reinitialize my Map
	     */
	    public void reinit() {
	    	mWikiMap.clear();
	    	for (Wiki lWiki:mWikis) {
	    		 mWikiMap.put(lWiki.id,lWiki);
	    	}
	    }
	 
	    /**
	     *  add a new Wiki from the given triple
	     */
	    public Wiki add(TripleQuery query,Triple pWikiTriple) {
	      Wiki lWiki=new Wiki(query,pWikiTriple);
	      add(lWiki);
	      return lWiki;
	    }
	 
	    // convert this manager to json format 
	    public String toJson() { return JSON.toJSONString(this); }
	    
	    // get a new manager from the given json string
	  	public static WikiManager fromJson(
					String json) {
				WikiManager result=JSON.parseObject(json, WikiStatic.WikiManager.class);
				result.reinit();
				return result;
			}
	 
	    // default constructor 
	    public WikiManager() {}
	 
	    // construct me from the given triple Query query
	    public WikiManager(TripleQuery query) {
	      // first query the SiDIF bases triplestore
	      TripleQuery lWikiQuery=query.query(null,"isA","Wiki");
	      for (Triple lWikiTriple:lWikiQuery.getTriples()) {
	        add(query,lWikiTriple);
	      }
	      // then the SMW triplestore
	      lWikiQuery=query.query(null,"Property:IsA","Wiki");
	      for (Triple lWikiTriple:lWikiQuery.getTriples()) {
	        add(query,lWikiTriple);
	      }
	      init(query);
	    } // constructor for Wiki Manager

		
	 
	    // >>>{user defined topicmanager code}{Wiki}{Wiki}
	    // <<<{user defined topicmanager code}{Wiki}{Wiki}
	  } // class Wiki Manager
	 /**
	  * TransferPage
	  * 
	  */
	  static class TransferPage extends TopicBase {
	    String pageid;
	    String page;
	    String wiki;
	 
	    public String getPage() { return page; }
	    public void setPage(String pPage) { page=pPage; }
	    public String getWiki() { return wiki; }
	    public void setWiki(String pWiki) { wiki=pWiki; }
	    /**
	     * convert this TransferPage to a JSON string
	     */
	    public String toJson() { return JSON.toJSONString(this); }
	 
	    
	    /**  
	     * get the pageid for this topic
	     */
	    public String getPageid() { return pageid; };
	 
	    /**
	     * default constructor for TransferPage
	     */
	    public TransferPage() {}
	 
	    /**
	     * construct a TransferPage from the given Triple
	     * @param query - the TripleQuery to get the triples from
	     * @param pTransferPageTriple - the triple to construct me from
	     */
	    public TransferPage(TripleQuery query,Triple pTransferPageTriple) {
	      this(query,pTransferPageTriple.getSubject().toString());
	    } // constructor
	 
	    /**
	     * construct a TransferPage from the given pageId
	     * @param query - the TripleQuery to get the triples from
	     * @param pageid - pageid
	     */
	    public TransferPage(TripleQuery query,String pageid) {
	      this.pageid=pageid;
	      Triple pageTriple=query.selectSingle(pageid,"page",null);
	      if (pageTriple==null)
	        pageTriple=query.selectSingle(pageid,"Property:TransferPage_page",null);
	      if (pageTriple!=null) 
	        page=pageTriple.getObject().toString();
	      Triple wikiTriple=query.selectSingle(pageid,"wiki",null);
	      if (wikiTriple==null)
	        wikiTriple=query.selectSingle(pageid,"Property:TransferPage_wiki",null);
	      if (wikiTriple!=null) 
	        wiki=wikiTriple.getObject().toString();
	      init(query);
	    } // constructor for TransferPage
	 
	    // >>>{user defined topic code}{TransferPage}{TransferPage}
	    // <<<{user defined topic code}{TransferPage}{TransferPage}
	  } // class TransferPage
	  /**
	   * Manager for TransferPage
	   */
	  public static class TransferPageManager extends TopicBase {
	 
	    public String topicName="TransferPage";
	    public List<TransferPage> mTransferPages=new ArrayList<TransferPage>();
	    public Map<String,TransferPage> mTransferPageMap=new LinkedHashMap<String,TransferPage>();
	 
	    /**
	     *  add a new TransferPage 
	     */
	    public TransferPage add(TransferPage pTransferPage) {
	      mTransferPages.add(pTransferPage);
	      mTransferPageMap.put(pTransferPage.getPageid(),pTransferPage);
	      return pTransferPage;
	    }
	 
	    /**
	     *  add a new TransferPage from the given triple
	     */
	    public TransferPage add(TripleQuery query,Triple pTransferPageTriple) {
	      TransferPage lTransferPage=new TransferPage(query,pTransferPageTriple);
	      add(lTransferPage);
	      return lTransferPage;
	    }
	 
	    // convert this manager to json format 
	    public String toJson() { return JSON.toJSONString(this); }
	 
	    // default constructor 
	    public TransferPageManager() {}
	 
	    // construct me from the given triple Query query
	    public TransferPageManager(TripleQuery query) {
	      // first query the SiDIF bases triplestore
	      TripleQuery lTransferPageQuery=query.query(null,"isA","TransferPage");
	      for (Triple lTransferPageTriple:lTransferPageQuery.getTriples()) {
	        add(query,lTransferPageTriple);
	      }
	      // then the SMW triplestore
	      lTransferPageQuery=query.query(null,"Property:IsA","TransferPage");
	      for (Triple lTransferPageTriple:lTransferPageQuery.getTriples()) {
	        add(query,lTransferPageTriple);
	      }
	      init(query);
	    } // constructor for TransferPage Manager
	 
	    // >>>{user defined topicmanager code}{TransferPage}{TransferPage}
	    // <<<{user defined topicmanager code}{TransferPage}{TransferPage}
	  } // class TransferPage Manager
	 
	}

