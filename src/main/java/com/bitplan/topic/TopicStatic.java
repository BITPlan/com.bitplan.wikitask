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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.TreeMap;

import org.sidif.triple.Triple;
import org.sidif.triple.TripleQuery;
import org.sidif.triple.TripleStore;

import com.alibaba.fastjson.JSON;

/**
 * generated Java code from MetaModel context
 * 
 * @author wf
 *
 */
public class TopicStatic {

  /**
   * Base class
   */
  public static abstract class TopicBase {
    protected static Logger LOGGER = Logger.getLogger("org.sidif.wiki");
    
    // each Topic has a pageid - for non subobject thats the pagename
    public String pageid;

    /**
     * get a WikiSon version of the given name value
     * 
     * @param name
     * @param value
     * @return - the string representation
     */
    public String toWikiSon(String name, String value) {
      String result = "<!-- " + name + " is null-->\n";
      if (value != null)
        result = "|" + name + "=" + value + "\n";
      return result;
    }

    /**
     * get the SiDIF representation of the given property
     * 
     * @param name - the name of the property
     * @param value - the value of the property
     * @param type - the type of the property
     * @return - the SiDIF Sting representation of the property
     */
    public static String propertySiDIF(String name, String value, String type) {
      // default is a comment line which can be filled by uncommenting
      String result = String.format("# is is %s of it\n",name);;
      // if the value is not empty
      if ((value != null) && (!("".equals(value.trim())))) {
        // do we need to quote the result?
        String quote = "";
        // this depends on the Type
        if ("Text".equals(type)) {
          quote = "\"";
        }
        // create a SIDIF Property line like
        // "John" is lastname of it
        // convert double quotes to single quotes - FIXME - should we escape instead?
        value=value.replace("\"","'");
        result = String.format("%s%s%s is %s of it\n",quote,value,quote,name);
      }
      // return the SiDIF property line
      return result;
    }

    /**
     * get me as a String
     * 
     * @param name
     * @param value
     * @return the SiDIF representation for the given name and value
     */
    public static String propertySiDIF(String name, String value) {
      String result = propertySiDIF(name, value, "Text");
      return result;
    }

    /**
     * check if the given boolean String value is true
     * 
     * @param value
     * @return true if the value is not null and has true/TRUE as it's string
     *         content
     */
    public boolean isTrue(String value) {
      boolean result = false;
      if (value != null && value.toLowerCase().equals("true")) {
        result = true;
      }
      return result;
    }

    /**
     * initialize
     */
    public void init(TripleQuery query) {
    }
  } // TopicBase

  public static class YTManager {
    List<YT> yTs;

    /**
     * @param yts
     *          the yts to set
     */
    public void setYTs(List<YT> yts) {
      this.yTs = yts;
    }

    /**
     * get the technical aspects
     * 
     * @return the list of YTs
     */
    public List<YT> getYTs() {
      if (yTs == null) {
        yTs = Arrays.asList(YT.yts);
      }
      return yTs;
    }

    /**
     * convert this YT to a JSON string
     */
    public String toJson() {
      return JSON.toJSONString(this);
    }

    // get a new manager from the given json string
    public static YTManager fromJson(String json) {
      YTManager result = JSON.parseObject(json, YTManager.class);
      return result;
    }
  }

  /**
   * technical aspects of Y-Principle
   * 
   * @author wf
   *
   */
  public static class YT {

    // Y-Principle technical aspects
    public static YT yts[] = {
        new YT("Category", "Category", "category",
            "/images/d/d6/Category_Icon.png"),
        new YT("Concept", "Concept", "concept", "/images/2/25/Concept_Icon.png"),
        new YT("Form", "Form", "form", "/images/e/e5/Form_icon.jpg"),
        new YT("Help", "Help", "help", "/images/7/7a/Help_Icon.png"),
        new YT("Listof", "List of", "listof", "/images/7/7f/List_Icon.png"),
        new YT("Template", "Template", "template",
            "/images/6/61/Icon_template.png"),
        new YT("Properties", "Properties", "properties",
            "/images/6/6a/Element_into_input.png"),
        new YT("Java", "Java", "javacodegen", "/images/3/38/Java_icon.png") };

    /**
     * get the YT with the given name
     * 
     * @param ytname
     */
    public static YT getYT(String ytname) {
      for (YT yt : yts) {
        if (yt.name.equals(ytname)) {
          return yt;
        }
      }
      return null;
    }

    public String name;
    public String label;
    public String iconUrl;
    public String template;

    // default constructor for json
    public YT() {
    };

    /**
     * construct me with name label and icon url
     * 
     * @param name
     * @param label
     * @param iconUrl
     */
    public YT(String name, String label, String template, String iconUrl) {
      super();
      this.name = name;
      this.label = label;
      this.template = template;
      this.iconUrl = iconUrl;
    }

    /**
     * a part where name and label is different
     * 
     * @param name
     * @param label
     */
    public YT(String name, String label) {
      this(name, label, name.toLowerCase(), "File:" + name + ".png");
    }

    /**
     * a part where name and label is the same
     * 
     * @param name
     */
    public YT(String name) {
      this(name, name);
    }

    /**
     * convert this YT to a JSON string
     */
    public String toJson() {
      return JSON.toJSONString(this);
    }

    /**
     * get the page Title for the given topic
     * 
     * @param topic
     * @return the page title
     */
    public String getPageTitle(Topic topic) {
      String result = this.name + ":" + topic.name;
      if ("Listof".equals(name)) {
        result = "List_of_" + topic.pluralName.replace(" ", "_");
      }
      if ("Properties".equals(name)) {
        result = "Concept:" + topic.name.replace(" ", "_") + "/Properties";
      }
      if ("Java".equals(name)) {
        result = "Concept:" + topic.name.replace(" ", "_") + "/Java";
      }
      return result;
    }
  }

  /**
   * Context
   * A Context groups some topics like a Namespace/Package
   */
   public static class Context extends TopicBase {
   
     public String name;
     public String since;
     public String master;
     public String copyright;

     public String getName() { return name; }
     public void setName(String pName) { name=pName; }
     public String getSince() { return since; }
     public void setSince(String pSince) { since=pSince; }
     public String getMaster() { return master; }
     public void setMaster(String pMaster) { master=pMaster; }
     public String getCopyright() { return copyright; }
     public void setCopyright(String pCopyright) { copyright=pCopyright; }
     /**
      * convert this Context to a JSON string
      */
     public String toJson() { return JSON.toJSONString(this); }

     /**
      * convert this Context to a WikiSon string
      * @return the WikiSon representation of this Context
      */
     public String toWikiSon() {
       String wikison= "{{Context\n";
       wikison+=toWikiSon("name",name);
       wikison+=toWikiSon("since",since);
       wikison+=toWikiSon("master",master);
       wikison+=toWikiSon("copyright",copyright);
       wikison+="}}\n";
       return wikison;
     }

     /**
      * convert this Context to a SiDIF string
      * @return the SiDIF representation of this Context
      */
     public String toSiDIF() {
       String siDIF = String.format("%s isA Context\n",this.pageid);
       siDIF+=propertySiDIF("name",name,"Text");
       siDIF+=propertySiDIF("since",since,"Date");
       siDIF+=propertySiDIF("master",master,"URL");
       siDIF+=propertySiDIF("copyright",copyright,"Text");
       return siDIF;
     }
  
     /**  
      * get the pageid for this topic
      */
     public String getPageid() { return pageid; };

     /**
      * default constructor for Context
      */
     public Context() {}

     /**
      * construct a Context from the given Triple
      * @param query - the TripleQuery to get the triples from
      * @param pContextTriple - the triple to construct me from
      */
     public Context(TripleQuery query,Triple pContextTriple) {
       this(query,pContextTriple.getSubject().toString());
     } // constructor

     /**
      * construct a Context from the given pageId
      * @param query - the TripleQuery to get the triples from
      * @param pageid - pageid
      */
     public Context(TripleQuery query,String pageid) {
       this.pageid=pageid;
       Triple nameTriple=query.selectSingle(pageid,"name",null);
       if (nameTriple==null)
         nameTriple=query.selectSingle(pageid,"Property:Context_name",null);
       if (nameTriple!=null) 
         name=nameTriple.getObject().toString();
       Triple sinceTriple=query.selectSingle(pageid,"since",null);
       if (sinceTriple==null)
         sinceTriple=query.selectSingle(pageid,"Property:Context_since",null);
       if (sinceTriple!=null) 
         since=sinceTriple.getObject().toString();
       Triple masterTriple=query.selectSingle(pageid,"master",null);
       if (masterTriple==null)
         masterTriple=query.selectSingle(pageid,"Property:Context_master",null);
       if (masterTriple!=null) 
         master=masterTriple.getObject().toString();
       Triple copyrightTriple=query.selectSingle(pageid,"copyright",null);
       if (copyrightTriple==null)
         copyrightTriple=query.selectSingle(pageid,"Property:Context_copyright",null);
       if (copyrightTriple!=null) 
         copyright=copyrightTriple.getObject().toString();
       init(query);
     } // constructor for Context

    // >>>{user defined topic code}{Context}{Context}
    /**
     * initialize
     */
    public void init(TripleQuery query) {
      if (this.name == null) {
        this.name = this.pageid;
      }
    }

    /**
     * return me as a SiDIF string
     * 
     * @return my SiDIF representation
     */
    public String asSiDIF() {
      String result = "";
      result += "#\n";
      result += "# Context:" + getName() + "\n";
      result += "#\n";
      result += getPageid() + " isA Context\n";
      result += propertySiDIF("name", name);
      for (Entry<String, Topic> topicEntry : this.topics.mTopicMap.entrySet()) {
        result += topicEntry.getValue().asSiDIF();
      }
      return result;
    }

    // 1:n relation to topics
    public TopicManager topics = new TopicManager();

    /**
     * accessor for 1:n relation to topics
     * 
     * @return the list of Topics
     */
    public List<Topic> getTopics() {
      return topics.mTopics;
    }

    /**
     * recreate topics from a given list
     * 
     * @param pTopics
     */
    public void setTopics(List<Topic> pTopics) {
      topics = new TopicManager();
      topics.mTopics = pTopics;
      for (Topic topic : pTopics) {
        topics.mTopicMap.put(topic.getName(), topic);
      }
    }

    /**
     * get the Topic by the given topicName
     * 
     * @param topicName
     * @return the Topic for the given topicName
     */
    public Topic byName(String topicName) {
      Topic result = this.topics.byName(topicName);
      return result;
    }
    // <<<{user defined topic code}{Context}{Context}
  } // class Context

  /**
   * Manager for Context
   */
  public static class ContextManager extends TopicBase {

    public String topicName = "Context";
    public transient List<Context> mContexts = new ArrayList<Context>();
    public transient Map<String, Context> mContextMap = new TreeMap<String, Context>();

    /**
     * add a new Context
     */
    public Context add(Context pContext) {
      mContexts.add(pContext);
      mContextMap.put(pContext.getName(), pContext);
      return pContext;
    }

    /**
     * add a new Context from the given triple
     */
    public Context add(TripleQuery query, Triple pContextTriple) {
      Context lContext = new Context(query, pContextTriple);
      add(lContext);
      return lContext;
    }

    // reinitialize my mContext map
    public void reinit() {
      mContextMap.clear();
      for (Context lContext : mContexts) {
        mContextMap.put(lContext.getName(), lContext);
      }
    }

    // convert this manager to json format
    public String toJson() {
      return JSON.toJSONString(this);
    }

    // get a new manager from the given json string
    public static ContextManager fromJson(String json) {
      ContextManager result = JSON.parseObject(json, ContextManager.class);
      result.reinit();
      return result;
    }

    // default constructor
    public ContextManager() {
    }

    // construct me from the given triple Query query
    public ContextManager(TripleQuery query) {
      // first query the SiDIF bases triplestore
      TripleQuery lContextQuery = query.query(null, "isA", "Context");
      for (Triple lContextTriple : lContextQuery.getTriples()) {
        add(query, lContextTriple);
      }
      // then the SMW triplestore
      lContextQuery = query.query(null, "Property:IsA", "Context");
      for (Triple lContextTriple : lContextQuery.getTriples()) {
        add(query, lContextTriple);
      }
      init(query);
    } // constructor for Context Manager

    // >>>{user defined topicmanager code}{Context}{Context}
    public transient TopicManager mTopicManager;
    public transient PropertyManager mPropertyManager;

    /**
     * run pass 2 to link topics to context and properties to topics
     * 
     * @return a list of error messages
     */
    public List<String> pass2() {
      List<String> result = new ArrayList<String>();
      for (Topic topic : mTopicManager.mTopics) {
        if (topic.context == null) {
          LOGGER.log(Level.SEVERE, "topic " + topic.getName() + " pageid "
              + topic.getPageid() + " has a null context");
        } else {
          Context context = this.mContextMap.get(topic.context);
          if (context != null) {
            context.topics.add(topic);
          } else {
            result.add("Context '" + topic.context + "' not found");
          }
        }
      }
      // FIXME - do we really need to do this?
      for (Property property : mPropertyManager.mPropertys) {
        String propertyHint = "property " + property.getName() + " pageid "
            + property.getPageid();
        if (property.topic == null) {
          LOGGER.log(Level.SEVERE, propertyHint + " has a null topic");
        } else {
          // FIXME ... remove non name parts ...
          Topic topic = mTopicManager.mTopicMap.get(property.topic);
          if (topic != null) {
            if (!topic.properties.mPropertyMap.containsKey(property.getName())) {
              result.add("property '" + propertyHint + "  not found in topic "
                  + topic.getName());
            } else {
              topic.properties.add(property);
            }
          } else {
            result.add("Topic '" + property.topic + "' for " + propertyHint
                + "  not found in ");
          }
        }
      }
      return result;
    }

    /**
     * construct me from a tripleStore
     * 
     * @param tripleStore
     */
    public ContextManager(TripleStore tripleStore) {
      this(tripleStore.query());
      mTopicManager = new TopicManager(tripleStore.query());
      mPropertyManager = new PropertyManager(tripleStore.query());
      mTopicManager.configureTopics();
      pass2();
    }

    /**
     * get my contexts
     * 
     * @return the list of Contexts
     */
    public List<Context> getContexts() {
      List<Context> result = this.mContexts;
      return result;
    }

    /**
     * get the given context by name
     * 
     * @param name
     * @return the Context for the given name
     */
    public Context byName(String name) {
      Context result = this.mContextMap.get(name);
      return result;
    }

    /**
     * return me as a SiDIF representation
     * 
     * @return my SiDIF representation
     */
    public String asSiDIF() {
      String result = "";
      for (Entry<String, Context> contextEntry : this.mContextMap.entrySet()) {
        result += contextEntry.getValue().asSiDIF();
      }
      return result;
    }
    // <<<{user defined topicmanager code}{Context}{Context}
  } // class Context Manager

  /**
   * Topic
   * A Topic is a Concept/Class/Thing/Entity
   */
  public static class Topic extends TopicBase {
    public String name;
    public String pluralName;
    public String icon;
    public String iconUrl;
    public String documentation;
    public String wikiDocumentation;
    public String defaultstoremode;
    public String listLimit;
    public String cargo;
    public String context;
    public String headerTabs;

    public String getName() {
      return name;
    }

    public void setName(String pName) {
      name = pName;
    }

    public String getPluralName() {
      return pluralName;
    }

    public void setPluralName(String pPluralName) {
      pluralName = pPluralName;
    }

    public String getIcon() {
      return icon;
    }

    public void setIcon(String pIcon) {
      icon = pIcon;
    }

    public String getIconUrl() {
      return iconUrl;
    }

    public void setIconUrl(String pIconUrl) {
      iconUrl = pIconUrl;
    }

    public String getDocumentation() {
      return documentation;
    }

    public void setDocumentation(String pDocumentation) {
      documentation = pDocumentation;
    }

    public String getWikiDocumentation() {
      return wikiDocumentation;
    }

    public void setWikiDocumentation(String pWikiDocumentation) {
      wikiDocumentation = pWikiDocumentation;
    }

    public String getDefaultstoremode() {
      return defaultstoremode;
    }

    public void setDefaultstoremode(String pDefaultstoremode) {
      defaultstoremode = pDefaultstoremode;
    }

    public String getListLimit() {
      return listLimit;
    }

    public void setListLimit(String pListLimit) {
      listLimit = pListLimit;
    }

    public String getCargo() {
      return cargo;
    }

    public void setCargo(String pCargo) {
      cargo = pCargo;
    }

    public String getContext() {
      return context;
    }

    public void setContext(String pContext) {
      context = pContext;
    }

    public String getHeaderTabs() {
      return headerTabs;
    }

    public void setHeaderTabs(String pHeaderTabs) {
      headerTabs = pHeaderTabs;
    }

    /**
     * convert this Topic to a JSON string
     */
    public String toJson() {
      return JSON.toJSONString(this);
    }

    /**
     * get the pageid for this topic
     */
    public String getPageid() {
      return pageid;
    };

    /**
     * default constructor for Topic
     */
    public Topic() {
    }

    /**
     * construct a Topic from the given Triple
     * 
     * @param query
     *          - the TripleQuery to get the triples from
     * @param pTopicTriple
     *          - the triple to construct me from
     */
    public Topic(TripleQuery query, Triple pTopicTriple) {
      this(query, pTopicTriple.getSubject().toString());
    } // constructor

    /**
     * construct a Topic from the given pageId
     * 
     * @param query
     *          - the TripleQuery to get the triples from
     * @param pageid
     *          - pageid
     */
    public Topic(TripleQuery query, String pageid) {
      this.pageid = pageid;
      Triple pluralNameTriple = query.selectSingle(pageid, "pluralName", null);
      if (pluralNameTriple == null)
        pluralNameTriple = query.selectSingle(pageid,
            "Property:Topic_pluralName", null);
      if (pluralNameTriple != null)
        pluralName = pluralNameTriple.getObject().toString();
      Triple cargoTriple = query.selectSingle(pageid, "cargo", null);
      if (cargoTriple == null)
        cargoTriple = query.selectSingle(pageid, "Property:Topic_cargo", null);
      if (cargoTriple != null)
        cargo = cargoTriple.getObject().toString();
      Triple documentationTriple = query.selectSingle(pageid, "documentation",
          null);
      if (documentationTriple == null)
        documentationTriple = query.selectSingle(pageid,
            "Property:Topic_documentation", null);
      if (documentationTriple != null)
        documentation = documentationTriple.getObject().toString();
      Triple wikiDocumentationTriple = query.selectSingle(pageid,
          "wikiDocumentation", null);
      if (wikiDocumentationTriple == null)
        wikiDocumentationTriple = query.selectSingle(pageid,
            "Property:Topic_wikiDocumentation", null);
      if (wikiDocumentationTriple != null)
        wikiDocumentation = wikiDocumentationTriple.getObject().toString();
      Triple defaultstoremodeTriple = query.selectSingle(pageid,
          "defaultstoremode", null);
      if (defaultstoremodeTriple == null)
        defaultstoremodeTriple = query.selectSingle(pageid,
            "Property:Topic_defaultstoremode", null);
      if (defaultstoremodeTriple != null)
        defaultstoremode = defaultstoremodeTriple.getObject().toString();
      Triple nameTriple = query.selectSingle(pageid, "name", null);
      if (nameTriple == null)
        nameTriple = query.selectSingle(pageid, "Property:Topic_name", null);
      if (nameTriple != null)
        name = nameTriple.getObject().toString();
      Triple iconTriple = query.selectSingle(pageid, "icon", null);
      if (iconTriple == null)
        iconTriple = query.selectSingle(pageid, "Property:Topic_icon", null);
      if (iconTriple != null)
        icon = iconTriple.getObject().toString();
      Triple contextTriple = query.selectSingle(pageid, "context", null);
      if (contextTriple == null)
        contextTriple = query.selectSingle(pageid, "Property:Topic_context",
            null);
      if (contextTriple != null)
        context = contextTriple.getObject().toString();
      Triple iconUrlTriple = query.selectSingle(pageid, "iconUrl", null);
      if (iconUrlTriple == null)
        iconUrlTriple = query.selectSingle(pageid, "Property:Topic_iconUrl",
            null);
      if (iconUrlTriple != null)
        iconUrl = iconUrlTriple.getObject().toString();
      Triple headerTabsTriple = query.selectSingle(pageid, "headerTabs", null);
      if (headerTabsTriple == null)
        headerTabsTriple = query.selectSingle(pageid,
            "Property:Topic_headerTabs", null);
      if (headerTabsTriple != null)
        headerTabs = headerTabsTriple.getObject().toString();
      init(query);
    } // constructor for Topic

    // >>>{user defined topic code}{Topic}{Topic}
    PropertyManager properties = new PropertyManager();
    transient public TopicLinkManager sourceTopicLinks = new TopicLinkManager();
    transient public TopicLinkManager targetTopicLinks = new TopicLinkManager();
    transient public Property conceptProperty; // the Property to be used when
                                               // selecting a

    // Concept

    /**
     * get the Properties
     * 
     * @return the list of Properties
     */
    public List<Property> getProperties() {
      return properties.mPropertys;
    }

    /**
     * add Properties from the given query
     * 
     * @param propertyQuery
     * @param query
     */
    public void addProperties(TripleQuery propertyQuery, TripleQuery query) {
      if (propertyQuery != null) {
        for (Triple property : propertyQuery.getTriples()) {
          properties.add(query, property);
        }
      }
    }

    /**
     * initialize me with the given query
     * @param query - the TripleQuery to use for initialization
     */
    public void init(TripleQuery query) {
      // pass2 does this in a query indepent way ...
      // SiDIF style property query ...
      TripleQuery propertyQuery = query.query(null, "addsTo", pageid);
      addProperties(propertyQuery, query);
      // pass2 does this in a reverse way - this could also be done but then
      // propertyNames would have to be fixed ...
      propertyQuery = query.query(null, "Property:Property_topic", pageid);
      addProperties(propertyQuery, query);

      addTopicLinks(query, sourceTopicLinks, "source");
      addTopicLinks(query, targetTopicLinks, "target");

      if (name == null) {
        name = pageid;
      }
      if (pluralName == null) {
        pluralName = name + "s";
      }

      // add Properties for targetTopicLinks
      // this is the referenced to the "neighbour" Topic
      for (TopicLink topicLink : targetTopicLinks.mTopicLinks) {
        Property property = new Property();
        property.pageid = topicLink.sourceRole;
        property.documentation = topicLink.sourceDocumentation;
        property.name = property.pageid;
        property.label = property.name;
        property.type = "Page";
        property.isLink = "true";
        // make sure the form shows the neighbour concept as an option
        String sourceName = topicLink.source;
        // remove "Concept:" prefix !!! FIXME - this is language dependent
        // and a horrible work around for the time being
        sourceName = sourceName.replace("Concept:", "");
        sourceName = sourceName.replace("Konzept:", "");
        property.values_from = "concept=" + sourceName;
        property.inputType = "combobox";
        properties.mPropertys.add(property);
      }
    }

    /**
     * add the topicLinks
     * 
     * @param query
     *          - the TripleQuery to get the triples from
     * @param tm
     *          - the TopicLinkManager to add the topic links to
     * @param predicate
     *          - the predicate to be used for linking
     */
    public void addTopicLinks(TripleQuery query, TopicLinkManager tm,
        String predicate) {
      TripleQuery linkQuery = query.query(null, predicate, pageid);
      if (linkQuery != null) {
        for (Triple linkTriple : linkQuery.getTriples()) {
          String link = linkTriple.getSubject().toString();
          TopicLink topiclink = new TopicLink(query, link);
          tm.mTopicLinks.add(topiclink);
        }
      }
    }

    /**
     * check my source TopicLinks and set the targetTopic by looking it up
     */
    public void linkTargetTopics(TopicManager tm) {
      for (TopicLink sourceTopicLink : sourceTopicLinks.mTopicLinks) {
        String topicTarget = sourceTopicLink.target; // e.g. Concept:Property;
        topicTarget = topicTarget.replace("Concept:", ""); // remove Concept
                                                           // part
        topicTarget = topicTarget.replace("Koncept:", ""); // remove Concept
                                                           // part
        Topic targetTopic = tm.byName(topicTarget);
        if (targetTopic != null) {
          sourceTopicLink.targetTopic = targetTopic;
        }
      }
    }

    /**
     * set the concept Property for the given target Property
     */
    public void setConceptProperty() {
      for (Property property : properties.mPropertys) {
        if ("true".equals(property.mandatory)
            || "true".equals(property.primaryKey)) {
          conceptProperty = property;
          break;
        }
      }
    }

    /**
     * get the Properties sorted by index
     * 
     * @return the List of properties sorted by Index
     */
    public List<Property> propertiesByIndex() {
      List<Property> properties = new ArrayList<Property>(
          this.properties.mPropertys);
      Collections.sort(properties, new Comparator<Property>() {
        public int compare(Property p1, Property p2) {
          Integer p1i = Integer.MAX_VALUE;
          Integer p2i = Integer.MAX_VALUE;
          try {
            p1i = Integer.parseInt(p1.index);
          } catch (NumberFormatException nfe) {
          }
          ;
          try {
            p2i = Integer.parseInt(p2.index);
          } catch (NumberFormatException nfe) {
          }
          ;
          int result = p1i.compareTo(p2i);
          return result;
        }
      });
      return properties;
    }

    /**
     * get the list of sort Properties in the order of sorting
     * 
     * @return the list of sortProperties
     */
    public List<Property> sortProperties() {
      Map<Integer, Property> propMap = new HashMap<Integer, Property>();
      int maxIndex = 0;
      for (Property property : this.properties.mPropertys) {
        if (property.sortPos != null) {
          try {
            int sp = property.getSortPosition();
            propMap.put(sp, property);
            maxIndex = Math.max(sp, maxIndex);
          } catch (NumberFormatException nfe) {
          }
        }
      }
      List<Property> properties = new ArrayList<Property>();
      for (int i = 1; i <= maxIndex; i++) {
        Property sortProp = propMap.get(i);
        if (sortProp != null) {
          properties.add(sortProp);
        }
      }
      return properties;
    }
    
    /**
     * is this Topic to be generated with HeaderTabs?
     * @return true if 
     */
    public boolean withHeaderTabs() {
      String ht=this.getHeaderTabs();
      boolean result=true; 
      if (ht!=null) {
        result=ht.trim().toLowerCase().equals("true");
      }
      return result;
    }

    /**
     * return me as a SiDIF string
     * 
     * @return my SiDIF representation
     */
    public String asSiDIF() {
      String result = "";
      result += "#\n";
      result += "#" + getName() + "\n";
      result += "#\n";
      result += name + " isA Topic\n";
      result += propertySiDIF("name", name);
      result += propertySiDIF("pluralName", pluralName);
      result += propertySiDIF("documentation", documentation);
      result += propertySiDIF("wikiDocumentation", wikiDocumentation);
      result += propertySiDIF("icon", icon);
      result += propertySiDIF("iconUrl", iconUrl);
      result += propertySiDIF("defaultstoremode", defaultstoremode);
      result += propertySiDIF("listLimit", this.listLimit, "Boolean");
      result += propertySiDIF("cargo", cargo, "Boolean");
      result += propertySiDIF("headerTabs", this.headerTabs, "Boolean");
      result += propertySiDIF("context", this.context);
      List<Property> propertiesByIndex = this.propertiesByIndex();
      for (Property property : propertiesByIndex) {
        if (!isTrue(property.isLink))
          result += getName() + "_" + property.getName() + " addsTo it\n";
      }
      int pindex = 0;
      result += "# properties of " + name + "\n";
      ;
      for (Property property : propertiesByIndex) {
        if (!isTrue(property.isLink)) {
          pindex++;
          result += "# property " + property.getName() + "\n";
          result += getName() + "_" + property.getName() + " isA Property\n";
          result += propertySiDIF("name", property.name);
          result += propertySiDIF("label", property.label);
          result += propertySiDIF("type", property.type);
          if (property.index == null)
            property.index = "" + pindex;
          result += propertySiDIF("index", property.index, "Number");
          result += propertySiDIF("sortPos", property.sortPos, "Number");
          result += propertySiDIF("primaryKey", property.primaryKey, "Boolean");
          result += propertySiDIF("mandatory", property.mandatory, "Boolean");
          result += propertySiDIF("namespace", property.namespace);
          result += propertySiDIF("size", property.size, "Number");
          result += propertySiDIF("uploadable", property.uploadable, "Boolean");
          result += propertySiDIF("defaultValue", property.defaultValue);
          result += propertySiDIF("inputType", property.inputType);
          result += propertySiDIF("allowedValues", property.allowedValues);
          result += propertySiDIF("documentation", property.documentation);
          result += propertySiDIF("values_from", property.values_from);
          result += propertySiDIF("showInGrid", property.showInGrid, "Boolean");
          result += propertySiDIF("isLink", property.isLink, "Boolean");
          // FIXME maybe use later again
          // result += propertySiDIF("pageid", property.pageid);

          result += propertySiDIF("topic", getName());
        }
      }
      return result;
    }
    // <<<{user defined topic code}{Topic}{Topic}
  } // class Topic

  /**
   * Manager for Topic
   */
  public static class TopicManager extends TopicBase {

    public String topicName = "Topic";
    public List<Topic> mTopics = new ArrayList<Topic>();
    public transient Map<String, Topic> mTopicMap = new TreeMap<String, Topic>();

    /**
     * add a new Topic
     */
    public Topic add(Topic pTopic) {
      mTopics.add(pTopic);
      mTopicMap.put(pTopic.getName(), pTopic);
      return pTopic;
    }

    /**
     * add a new Topic from the given triple
     */
    public Topic add(TripleQuery query, Triple pTopicTriple) {
      Topic lTopic = new Topic(query, pTopicTriple);
      add(lTopic);
      return lTopic;
    }

    // convert this manager to json format
    public String toJson() {
      return JSON.toJSONString(this);
    }

    // default constructor
    public TopicManager() {
    }

    // construct me from the given triple Query query
    public TopicManager(TripleQuery query) {
      // first query the SiDIF bases triplestore
      TripleQuery lTopicQuery = query.query(null, "isA", "Topic");
      for (Triple lTopicTriple : lTopicQuery.getTriples()) {
        add(query, lTopicTriple);
      }
      // then the SMW triplestore
      lTopicQuery = query.query(null, "Property:IsA", "Topic");
      for (Triple lTopicTriple : lTopicQuery.getTriples()) {
        add(query, lTopicTriple);
      }
      init(query);
    } // constructor for Topic Manager

    // >>>{user defined topicmanager code}{Topic}{Topic}
    /**
     * get a topic by the given name
     * 
     * @param topicName
     *          - the name of the topic to get FIXME - speedup?
     */
    public Topic byName(String topicName) {
      Topic result = null;
      for (Topic topic : mTopics) {
        if (topicName.equals(topic.name)) {
          result = topic;
        }
      }
      return result;
    } // byName

    /**
     * configure Topics: link TargetTopics set Concept Properties
     */
    public void configureTopics() {
      for (Topic topic : mTopics) {
        topic.setConceptProperty();
        topic.linkTargetTopics(this);
      }
    }
    // <<<{user defined topicmanager code}{Topic}{Topic}
  } // class Topic Manager

  /**
   * SMW_Type
   * an SMW_Type is a data type which determines the possible values for that
   * type e.g. a Boolean can hold true/fals values while a Number can hold
   * 3.1459 or 20. A Page can hold the name of a Wiki page see
   * https://semantic-mediawiki.org/wiki/Help:List_of_datatypes
   */
  public static class SMW_Type extends TopicBase {
    public String documentation;
    public String type;
    public String typepage;
    public String helppage;
    public String javaType;
    public String usedByProperties;

    public String getDocumentation() {
      return documentation;
    }

    public void setDocumentation(String pDocumentation) {
      documentation = pDocumentation;
    }

    public String getType() {
      return type;
    }

    public void setType(String pType) {
      type = pType;
    }

    public String getTypepage() {
      return typepage;
    }

    public void setTypepage(String pTypepage) {
      typepage = pTypepage;
    }

    public String getHelppage() {
      return helppage;
    }

    public void setHelppage(String pHelppage) {
      helppage = pHelppage;
    }

    public String getJavaType() {
      return javaType;
    }

    public void setJavaType(String pJavaType) {
      javaType = pJavaType;
    }

    public String getUsedByProperties() {
      return usedByProperties;
    }

    public void setUsedByProperties(String pUsedByProperties) {
      usedByProperties = pUsedByProperties;
    }

    /**
     * convert this SMW_Type to a JSON string
     */
    public String toJson() {
      return JSON.toJSONString(this);
    }

    /**
     * get the pageid for this topic
     */
    public String getPageid() {
      return pageid;
    };

    /**
     * default constructor for SMW_Type
     */
    public SMW_Type() {
    }

    /**
     * construct a SMW_Type from the given Triple
     * 
     * @param query
     *          - the TripleQuery to get the triples from
     * @param pSMW_TypeTriple
     *          - the triple to construct me from
     */
    public SMW_Type(TripleQuery query, Triple pSMW_TypeTriple) {
      this(query, pSMW_TypeTriple.getSubject().toString());
    } // constructor

    /**
     * construct a SMW_Type from the given pageId
     * 
     * @param query
     *          - the TripleQuery to get the triples from
     * @param pageid
     *          - pageid
     */
    public SMW_Type(TripleQuery query, String pageid) {
      this.pageid = pageid;
      Triple documentationTriple = query.selectSingle(pageid, "documentation",
          null);
      if (documentationTriple == null)
        documentationTriple = query.selectSingle(pageid,
            "Property:SMW_Type_documentation", null);
      if (documentationTriple != null)
        documentation = documentationTriple.getObject().toString();
      Triple typeTriple = query.selectSingle(pageid, "type", null);
      if (typeTriple == null)
        typeTriple = query.selectSingle(pageid, "Property:SMW_Type_type", null);
      if (typeTriple != null)
        type = typeTriple.getObject().toString();
      Triple typepageTriple = query.selectSingle(pageid, "typepage", null);
      if (typepageTriple == null)
        typepageTriple = query.selectSingle(pageid,
            "Property:SMW_Type_typepage", null);
      if (typepageTriple != null)
        typepage = typepageTriple.getObject().toString();
      Triple helppageTriple = query.selectSingle(pageid, "helppage", null);
      if (helppageTriple == null)
        helppageTriple = query.selectSingle(pageid,
            "Property:SMW_Type_helppage", null);
      if (helppageTriple != null)
        helppage = helppageTriple.getObject().toString();
      Triple javaTypeTriple = query.selectSingle(pageid, "javaType", null);
      if (javaTypeTriple == null)
        javaTypeTriple = query.selectSingle(pageid,
            "Property:SMW_Type_javaType", null);
      if (javaTypeTriple != null)
        javaType = javaTypeTriple.getObject().toString();
      Triple usedByPropertiesTriple = query.selectSingle(pageid,
          "usedByProperties", null);
      if (usedByPropertiesTriple == null)
        usedByPropertiesTriple = query.selectSingle(pageid,
            "Property:SMW_Type_usedByProperties", null);
      if (usedByPropertiesTriple != null)
        usedByProperties = usedByPropertiesTriple.getObject().toString();
      init(query);
    } // constructor for SMW_Type

    // >>>{user defined topic code}{SMW_Type}{SMW_Type}
    // <<<{user defined topic code}{SMW_Type}{SMW_Type}
  } // class SMW_Type

  /**
   * Manager for SMW_Type
   */
  public static class SMW_TypeManager extends TopicBase {

    public String topicName = "SMW_Type";
    public List<SMW_Type> mSMW_Types = new ArrayList<SMW_Type>();
    public Map<String, SMW_Type> mSMW_TypeMap = new TreeMap<String, SMW_Type>();

    /**
     * add a new SMW_Type
     */
    public SMW_Type add(SMW_Type pSMW_Type) {
      mSMW_Types.add(pSMW_Type);
      mSMW_TypeMap.put(pSMW_Type.getPageid(), pSMW_Type);
      return pSMW_Type;
    }

    /**
     * add a new SMW_Type from the given triple
     */
    public SMW_Type add(TripleQuery query, Triple pSMW_TypeTriple) {
      SMW_Type lSMW_Type = new SMW_Type(query, pSMW_TypeTriple);
      add(lSMW_Type);
      return lSMW_Type;
    }

    // convert this manager to json format
    public String toJson() {
      return JSON.toJSONString(this);
    }

    // default constructor
    public SMW_TypeManager() {
    }

    // construct me from the given triple Query query
    public SMW_TypeManager(TripleQuery query) {
      // first query the SiDIF bases triplestore
      TripleQuery lSMW_TypeQuery = query.query(null, "isA", "SMW_Type");
      for (Triple lSMW_TypeTriple : lSMW_TypeQuery.getTriples()) {
        add(query, lSMW_TypeTriple);
      }
      // then the SMW triplestore
      lSMW_TypeQuery = query.query(null, "Property:IsA", "SMW_Type");
      for (Triple lSMW_TypeTriple : lSMW_TypeQuery.getTriples()) {
        add(query, lSMW_TypeTriple);
      }
      init(query);
    } // constructor for SMW_Type Manager

    // >>>{user defined topicmanager code}{SMW_Type}{SMW_Type}
    // <<<{user defined topicmanager code}{SMW_Type}{SMW_Type}
  } // class SMW_Type Manager

  /**
   * Property
   * a Property is a Feature/Attribute
   */
  public static class Property extends TopicBase {
    public String inputType;
    public String defaultValue;
    public String mandatory;
    public String allowedValues;
    public String uploadable;
    public String index;
    public String sortPos;
    public String showInGrid;
    public String values_from;
    public String isLink;
    public String size;
    public String topic;
    public String label;
    public String name;
    public String primaryKey;
    public String documentation;
    public String namespace;
    public String type;

    public String getInputType() {
      return inputType;
    }

    public void setInputType(String pInputType) {
      inputType = pInputType;
    }

    public String getDefaultValue() {
      return defaultValue;
    }

    public void setDefaultValue(String pDefaultValue) {
      defaultValue = pDefaultValue;
    }

    public String getMandatory() {
      return mandatory;
    }

    public void setMandatory(String pMandatory) {
      mandatory = pMandatory;
    }

    public String getAllowedValues() {
      return allowedValues;
    }

    public void setAllowedValues(String pAllowedValues) {
      allowedValues = pAllowedValues;
    }

    public String getUploadable() {
      return uploadable;
    }

    public void setUploadable(String pUploadable) {
      uploadable = pUploadable;
    }

    public String getIndex() {
      return index;
    }

    public void setIndex(String pIndex) {
      index = pIndex;
    }

    public String getSortPos() {
      return sortPos;
    }

    public void setSortPos(String pSortPos) {
      sortPos = pSortPos;
    }

    public String getShowInGrid() {
      return showInGrid;
    }

    public void setShowInGrid(String pShowInGrid) {
      showInGrid = pShowInGrid;
    }

    public String getValues_from() {
      return values_from;
    }

    public void setValues_from(String pValues_from) {
      values_from = pValues_from;
    }

    public String getIsLink() {
      return isLink;
    }

    public void setIsLink(String pIsLink) {
      isLink = pIsLink;
    }

    public String getSize() {
      return size;
    }

    public void setSize(String pSize) {
      size = pSize;
    }

    public String getTopic() {
      return topic;
    }

    public void setTopic(String pTopic) {
      topic = pTopic;
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(String pLabel) {
      label = pLabel;
    }

    public String getName() {
      return name;
    }

    public void setName(String pName) {
      name = pName;
    }

    public String getPrimaryKey() {
      return primaryKey;
    }

    public void setPrimaryKey(String pPrimaryKey) {
      primaryKey = pPrimaryKey;
    }

    public String getDocumentation() {
      return documentation;
    }

    public void setDocumentation(String pDocumentation) {
      documentation = pDocumentation;
    }

    public String getNamespace() {
      return namespace;
    }

    public void setNamespace(String pNamespace) {
      namespace = pNamespace;
    }

    public String getType() {
      return type;
    }

    public void setType(String pType) {
      type = pType;
    }

    /**
     * convert this Property to a JSON string
     */
    public String toJson() {
      return JSON.toJSONString(this);
    }

    /**
     * get the pageid for this topic
     */
    public String getPageid() {
      return pageid;
    };

    /**
     * default constructor for Property
     */
    public Property() {
    }

    /**
     * construct a Property from the given Triple
     * 
     * @param query
     *          - the TripleQuery to get the triples from
     * @param pPropertyTriple
     *          - the triple to construct me from
     */
    public Property(TripleQuery query, Triple pPropertyTriple) {
      this(query, pPropertyTriple.getSubject().toString());
    } // constructor

    /**
     * construct a Property from the given pageId
     * 
     * @param query
     *          - the TripleQuery to get the triples from
     * @param pageid
     *          - pageid
     */
    public Property(TripleQuery query, String pageid) {
      this.pageid = pageid;
      Triple inputTypeTriple = query.selectSingle(pageid, "inputType", null);
      if (inputTypeTriple == null)
        inputTypeTriple = query.selectSingle(pageid,
            "Property:Property_inputType", null);
      if (inputTypeTriple != null)
        inputType = inputTypeTriple.getObject().toString();
      Triple defaultValueTriple = query.selectSingle(pageid, "defaultValue",
          null);
      if (defaultValueTriple == null)
        defaultValueTriple = query.selectSingle(pageid,
            "Property:Property_defaultValue", null);
      if (defaultValueTriple != null)
        defaultValue = defaultValueTriple.getObject().toString();
      Triple mandatoryTriple = query.selectSingle(pageid, "mandatory", null);
      if (mandatoryTriple == null)
        mandatoryTriple = query.selectSingle(pageid,
            "Property:Property_mandatory", null);
      if (mandatoryTriple != null)
        mandatory = mandatoryTriple.getObject().toString();
      Triple allowedValuesTriple = query.selectSingle(pageid, "allowedValues",
          null);
      if (allowedValuesTriple == null)
        allowedValuesTriple = query.selectSingle(pageid,
            "Property:Property_allowedValues", null);
      if (allowedValuesTriple != null)
        allowedValues = allowedValuesTriple.getObject().toString();
      Triple uploadableTriple = query.selectSingle(pageid, "uploadable", null);
      if (uploadableTriple == null)
        uploadableTriple = query.selectSingle(pageid,
            "Property:Property_uploadable", null);
      if (uploadableTriple != null)
        uploadable = uploadableTriple.getObject().toString();
      Triple indexTriple = query.selectSingle(pageid, "index", null);
      if (indexTriple == null)
        indexTriple = query.selectSingle(pageid, "Property:Property_index",
            null);
      if (indexTriple != null)
        index = indexTriple.getObject().toString();
      Triple sortPosTriple = query.selectSingle(pageid, "sortPos", null);
      if (sortPosTriple == null)
        sortPosTriple = query.selectSingle(pageid, "Property:Property_sortPos",
            null);
      if (sortPosTriple != null)
        sortPos = sortPosTriple.getObject().toString();
      Triple showInGridTriple = query.selectSingle(pageid, "showInGrid", null);
      if (showInGridTriple == null)
        showInGridTriple = query.selectSingle(pageid,
            "Property:Property_showInGrid", null);
      if (showInGridTriple != null)
        showInGrid = showInGridTriple.getObject().toString();
      Triple values_fromTriple = query
          .selectSingle(pageid, "values_from", null);
      if (values_fromTriple == null)
        values_fromTriple = query.selectSingle(pageid,
            "Property:Property_values_from", null);
      if (values_fromTriple != null)
        values_from = values_fromTriple.getObject().toString();
      Triple isLinkTriple = query.selectSingle(pageid, "isLink", null);
      if (isLinkTriple == null)
        isLinkTriple = query.selectSingle(pageid, "Property:Property_isLink",
            null);
      if (isLinkTriple != null)
        isLink = isLinkTriple.getObject().toString();
      Triple sizeTriple = query.selectSingle(pageid, "size", null);
      if (sizeTriple == null)
        sizeTriple = query.selectSingle(pageid, "Property:Property_size", null);
      if (sizeTriple != null)
        size = sizeTriple.getObject().toString();
      Triple topicTriple = query.selectSingle(pageid, "topic", null);
      if (topicTriple == null)
        topicTriple = query.selectSingle(pageid, "Property:Property_topic",
            null);
      if (topicTriple != null)
        topic = topicTriple.getObject().toString();
      Triple labelTriple = query.selectSingle(pageid, "label", null);
      if (labelTriple == null)
        labelTriple = query.selectSingle(pageid, "Property:Property_label",
            null);
      if (labelTriple != null)
        label = labelTriple.getObject().toString();
      Triple nameTriple = query.selectSingle(pageid, "name", null);
      if (nameTriple == null)
        nameTriple = query.selectSingle(pageid, "Property:Property_name", null);
      if (nameTriple != null)
        name = nameTriple.getObject().toString();
      Triple primaryKeyTriple = query.selectSingle(pageid, "primaryKey", null);
      if (primaryKeyTriple == null)
        primaryKeyTriple = query.selectSingle(pageid,
            "Property:Property_primaryKey", null);
      if (primaryKeyTriple != null)
        primaryKey = primaryKeyTriple.getObject().toString();
      Triple documentationTriple = query.selectSingle(pageid, "documentation",
          null);
      if (documentationTriple == null)
        documentationTriple = query.selectSingle(pageid,
            "Property:Property_documentation", null);
      if (documentationTriple != null)
        documentation = documentationTriple.getObject().toString();
      Triple namespaceTriple = query.selectSingle(pageid, "namespace", null);
      if (namespaceTriple == null)
        namespaceTriple = query.selectSingle(pageid,
            "Property:Property_namespace", null);
      if (namespaceTriple != null)
        namespace = namespaceTriple.getObject().toString();
      Triple typeTriple = query.selectSingle(pageid, "type", null);
      if (typeTriple == null)
        typeTriple = query.selectSingle(pageid, "Property:Property_type", null);
      if (typeTriple != null)
        type = typeTriple.getObject().toString();
      init(query);
    } // constructor for Property

    // >>>{user defined topic code}{Property}{Property}

    /**
     * get the sort Position value in a safe way that will not throw an exception - log any problems
     * @return the sort position value
     */
    public int getSortPosValue() {
      int result = 0;
      if (sortPos != null) {
        try {
          result = Integer.parseInt(sortPos.trim());
        } catch (NumberFormatException nfe) {
          LOGGER.log(Level.WARNING,
              "sortPos:" + sortPos + "-> error: " + nfe.getMessage());
        }
      }
      return result;
    }

    /**
     * return whether this property is Ascending
     * 
     * @return true if property is sorting ascending
     */
    public boolean sortAscending() {
      boolean result = getSortPosValue() >= 0;
      return result;
    }

    /**
     * get the sort Position of this property
     * 
     * @return the sort position of the property as a positive integer
     */
    public int getSortPosition() {
      int result = Math.abs(getSortPosValue());
      return result;
    }

    @Override
    public void init(TripleQuery query) {
      if (isLink == null) {
        isLink = "false";
      }
      if (name == null) {
        // use page id e.g. Concept_name
        String idparts[] = pageid.split("_");
        name = pageid.replaceFirst(idparts[0] + "_", "");
      }
      if (label == null) {
        label = name;
      }
      if (type == null) {
        type = "Text";
      }
      if (type.startsWith("Special:Types/")) {
        type = type.replace("Special:Types/", "");
      }
    }
    // <<<{user defined topic code}{Property}{Property}
  } // class Property

  /**
   * Manager for Property
   */
  public static class PropertyManager extends TopicBase {

    public String topicName = "Property";
    public transient List<Property> mPropertys = new ArrayList<Property>();
    public transient Map<String, Property> mPropertyMap = new LinkedHashMap<String, Property>();

    /**
     * add a new Property
     */
    public Property add(Property pProperty) {
      if (mPropertyMap.containsKey(pProperty.getName())) {
        LOGGER.log(Level.WARNING, "duplicate property " + pProperty.getName()
            + " from pageid " + pProperty.getPageid());
      } else {
        mPropertys.add(pProperty);
        mPropertyMap.put(pProperty.getName(), pProperty);
      }
      return pProperty;
    }

    /**
     * add a new Property from the given triple
     */
    public Property add(TripleQuery query, Triple pPropertyTriple) {
      Property lProperty = new Property(query, pPropertyTriple);
      add(lProperty);
      return lProperty;
    }

    // convert this manager to json format
    public String toJson() {
      return JSON.toJSONString(this);
    }

    // default constructor
    public PropertyManager() {
    }

    // construct me from the given triple Query query
    public PropertyManager(TripleQuery query) {
      // first query the SiDIF based triplestore
      TripleQuery lPropertyQuery = query.query(null, "isA", "Property");
      for (Triple lPropertyTriple : lPropertyQuery.getTriples()) {
        add(query, lPropertyTriple);
      }
      // then the SMW triplestore
      lPropertyQuery = query.query(null, "Property:IsA", "Property");
      for (Triple lPropertyTriple : lPropertyQuery.getTriples()) {
        add(query, lPropertyTriple);
      }
      init(query);
    } // constructor for Property Manager

    // >>>{user defined topicmanager code}{Property}{Property}
    // <<<{user defined topicmanager code}{Property}{Property}
  } // class Property Manager

  /**
   * TopicLink
   * 
   */
  public static class TopicLink extends TopicBase {
    public String name;
    public String sourceRole;
    public String sourceMultiple;
    public String source;
    public String sourceDocumentation;
    public String targetRole;
    public String targetMultiple;
    public String target;
    public String masterDetail;
    public String targetDocumentation;

    public String getName() {
      return name;
    }

    public void setName(String pName) {
      name = pName;
    }

    public String getSourceRole() {
      return sourceRole;
    }

    public void setSourceRole(String pSourceRole) {
      sourceRole = pSourceRole;
    }

    public String getSourceMultiple() {
      return sourceMultiple;
    }

    public void setSourceMultiple(String pSourceMultiple) {
      sourceMultiple = pSourceMultiple;
    }

    public String getSource() {
      return source;
    }

    public void setSource(String pSource) {
      source = pSource;
    }

    public String getSourceDocumentation() {
      return sourceDocumentation;
    }

    public void setSourceDocumentation(String pSourceDocumentation) {
      sourceDocumentation = pSourceDocumentation;
    }

    public String getTargetRole() {
      return targetRole;
    }

    public void setTargetRole(String pTargetRole) {
      targetRole = pTargetRole;
    }

    public String getTargetMultiple() {
      return targetMultiple;
    }

    public void setTargetMultiple(String pTargetMultiple) {
      targetMultiple = pTargetMultiple;
    }

    public String getTarget() {
      return target;
    }

    public void setTarget(String pTarget) {
      target = pTarget;
    }

    public String getMasterDetail() {
      return masterDetail;
    }

    public void setMasterDetail(String pMasterDetail) {
      masterDetail = pMasterDetail;
    }

    public String getTargetDocumentation() {
      return targetDocumentation;
    }

    public void setTargetDocumentation(String pTargetDocumentation) {
      targetDocumentation = pTargetDocumentation;
    }

    /**
     * convert this TopicLink to a JSON string
     */
    public String toJson() {
      return JSON.toJSONString(this);
    }

    /**
     * get the pageid for this topic
     */
    public String getPageid() {
      return pageid;
    };

    /**
     * default constructor for TopicLink
     */
    public TopicLink() {
    }

    /**
     * construct a TopicLink from the given Triple
     * 
     * @param query
     *          - the TripleQuery to get the triples from
     * @param pTopicLinkTriple
     *          - the triple to construct me from
     */
    public TopicLink(TripleQuery query, Triple pTopicLinkTriple) {
      this(query, pTopicLinkTriple.getSubject().toString());
    } // constructor

    /**
     * construct a TopicLink from the given pageId
     * 
     * @param query
     *          - the TripleQuery to get the triples from
     * @param pageid
     *          - pageid
     */
    public TopicLink(TripleQuery query, String pageid) {
      this.pageid = pageid;
      Triple nameTriple = query.selectSingle(pageid, "name", null);
      if (nameTriple == null)
        nameTriple = query
            .selectSingle(pageid, "Property:TopicLink_name", null);
      if (nameTriple != null)
        name = nameTriple.getObject().toString();
      Triple sourceRoleTriple = query.selectSingle(pageid, "sourceRole", null);
      if (sourceRoleTriple == null)
        sourceRoleTriple = query.selectSingle(pageid,
            "Property:TopicLink_sourceRole", null);
      if (sourceRoleTriple != null)
        sourceRole = sourceRoleTriple.getObject().toString();
      Triple sourceMultipleTriple = query.selectSingle(pageid,
          "sourceMultiple", null);
      if (sourceMultipleTriple == null)
        sourceMultipleTriple = query.selectSingle(pageid,
            "Property:TopicLink_sourceMultiple", null);
      if (sourceMultipleTriple != null)
        sourceMultiple = sourceMultipleTriple.getObject().toString();
      Triple sourceTriple = query.selectSingle(pageid, "source", null);
      if (sourceTriple == null)
        sourceTriple = query.selectSingle(pageid, "Property:TopicLink_source",
            null);
      if (sourceTriple != null)
        source = sourceTriple.getObject().toString();
      Triple sourceDocumentationTriple = query.selectSingle(pageid,
          "sourceDocumentation", null);
      if (sourceDocumentationTriple == null)
        sourceDocumentationTriple = query.selectSingle(pageid,
            "Property:TopicLink_sourceDocumentation", null);
      if (sourceDocumentationTriple != null)
        sourceDocumentation = sourceDocumentationTriple.getObject().toString();
      Triple targetRoleTriple = query.selectSingle(pageid, "targetRole", null);
      if (targetRoleTriple == null)
        targetRoleTriple = query.selectSingle(pageid,
            "Property:TopicLink_targetRole", null);
      if (targetRoleTriple != null)
        targetRole = targetRoleTriple.getObject().toString();
      Triple targetMultipleTriple = query.selectSingle(pageid,
          "targetMultiple", null);
      if (targetMultipleTriple == null)
        targetMultipleTriple = query.selectSingle(pageid,
            "Property:TopicLink_targetMultiple", null);
      if (targetMultipleTriple != null)
        targetMultiple = targetMultipleTriple.getObject().toString();
      Triple targetTriple = query.selectSingle(pageid, "target", null);
      if (targetTriple == null)
        targetTriple = query.selectSingle(pageid, "Property:TopicLink_target",
            null);
      if (targetTriple != null)
        target = targetTriple.getObject().toString();
      Triple masterDetailTriple = query.selectSingle(pageid, "masterDetail",
          null);
      if (masterDetailTriple == null)
        masterDetailTriple = query.selectSingle(pageid,
            "Property:TopicLink_masterDetail", null);
      if (masterDetailTriple != null)
        masterDetail = masterDetailTriple.getObject().toString();
      Triple targetDocumentationTriple = query.selectSingle(pageid,
          "targetDocumentation", null);
      if (targetDocumentationTriple == null)
        targetDocumentationTriple = query.selectSingle(pageid,
            "Property:TopicLink_targetDocumentation", null);
      if (targetDocumentationTriple != null)
        targetDocumentation = targetDocumentationTriple.getObject().toString();
      init(query);
    } // constructor for TopicLink

    // >>>{user defined topic code}{TopicLink}{TopicLink}
    public transient Topic sourceTopic = null;
    public transient Topic targetTopic = null;
    // <<<{user defined topic code}{TopicLink}{TopicLink}
  } // class TopicLink

  /**
   * Manager for TopicLink
   */
  public static class TopicLinkManager extends TopicBase {

    public String topicName = "TopicLink";
    public List<TopicLink> mTopicLinks = new ArrayList<TopicLink>();
    public Map<String, TopicLink> mTopicLinkMap = new TreeMap<String, TopicLink>();

    /**
     * add a new TopicLink
     */
    public TopicLink add(TopicLink pTopicLink) {
      mTopicLinks.add(pTopicLink);
      mTopicLinkMap.put(pTopicLink.getPageid(), pTopicLink);
      return pTopicLink;
    }

    /**
     * add a new TopicLink from the given triple
     */
    public TopicLink add(TripleQuery query, Triple pTopicLinkTriple) {
      TopicLink lTopicLink = new TopicLink(query, pTopicLinkTriple);
      add(lTopicLink);
      return lTopicLink;
    }

    // convert this manager to json format
    public String toJson() {
      return JSON.toJSONString(this);
    }

    // default constructor
    public TopicLinkManager() {
    }

    // construct me from the given triple Query query
    public TopicLinkManager(TripleQuery query) {
      // first query the SiDIF bases triplestore
      TripleQuery lTopicLinkQuery = query.query(null, "isA", "TopicLink");
      for (Triple lTopicLinkTriple : lTopicLinkQuery.getTriples()) {
        add(query, lTopicLinkTriple);
      }
      // then the SMW triplestore
      lTopicLinkQuery = query.query(null, "Property:IsA", "TopicLink");
      for (Triple lTopicLinkTriple : lTopicLinkQuery.getTriples()) {
        add(query, lTopicLinkTriple);
      }
      init(query);
    } // constructor for TopicLink Manager

    // >>>{user defined topicmanager code}{TopicLink}{TopicLink}
    // <<<{user defined topicmanager code}{TopicLink}{TopicLink}
  } // class TopicLink Manager

}