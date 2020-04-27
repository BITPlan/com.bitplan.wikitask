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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sidif.wiki.WikiTask;

import com.alibaba.fastjson.JSON;
import com.bitplan.topic.TopicStatic.Context;
import com.bitplan.topic.TopicStatic.Topic;

/**
 * settings for a context
 * 
 * @author wf
 *
 */
public class ContextSetting {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.topic");

  public static enum TripleStoreMode {
    SMW, SiDIF
  };

  private String wikiId;
  private String contextName;
  private TripleStoreMode tripleStoreMode = null;

  private String sidifInput;
  public String maintopicName = null;

  /**
   * @return the contextName
   */
  public String getContextName() {
    return contextName;
  }

  /**
   * @param contextName
   *          the contextName to set
   */
  public void setContextName(String contextName) {
    this.contextName = contextName;
  }

  /**
   * @return the tripleStoreMode
   */
  public TripleStoreMode getTripleStoreMode() {
    return tripleStoreMode;
  }

  /**
   * @param tripleStoreMode
   *          the tripleStoreMode to set
   */
  public void setTripleStoreMode(TripleStoreMode tripleStoreMode) {
    this.tripleStoreMode = tripleStoreMode;
  }

  /**
   * @return the wikiId
   */
  public String getWikiId() {
    return wikiId;
  }

  /**
   * @param wikiId
   *          the wikiId to set
   */
  public void setWikiId(String wikiId) {
    this.wikiId = wikiId;
  }

  /**
   * @return the tripleStoreId
   */
  public String getTripleStoreId() {
    String tripleStoreId = null;
    if (tripleStoreMode==null)
    		throw new IllegalStateException("tripleStoreMode is null for context "+this.contextName);
    switch (this.tripleStoreMode) {
    case SMW:
      tripleStoreId = this.wikiId + "::smw";
      break;
    case SiDIF:
      tripleStoreId = this.wikiId + ":" + this.sidifInput;
      break;

    }
    return tripleStoreId;
  }

  /**
   * @return the maintopicName
   */
  public String getMaintopicName() {
    return maintopicName;
  }

  /**
   * @param maintopicName
   *          the maintopicName to set
   */
  public void setMaintopicName(String maintopicName) {
    this.maintopicName = maintopicName;
  }

  /**
   * @return the sidifInput
   */
  public String getSidifInput() {
    return sidifInput;
  }

  /**
   * @param sidifInput
   *          the sidifInput to set
   */
  public void setSidifInput(String sidifInput) {
    this.sidifInput = sidifInput;
  }

  /**
   * default constructor
   */
  public ContextSetting() {
  }

  // convert this reference to json format
  public String toJson() {
    return JSON.toJSONString(this);
  }

  // get a new ContextSetting from the given json string
  public static ContextSetting fromJson(String json) {
    ContextSetting result = JSON.parseObject(json, ContextSetting.class);
    if (result.sidifInput != null && result.tripleStoreMode==null) {
      result.setTripleStoreMode(TripleStoreMode.SiDIF);
    }
    return result;
  }

  /**
   * initialize me from the given comma separated parameterlist
   * 
   * @param params
   *          - almost json parameter list e.g.
   *          wikiId:capri,tripleStoreMode:SMW,contextName:MetaModel,
   *          maintopicName:Topic
   * 
   * @throws Exception
   */
  public static ContextSetting fromParams(String params) {
    LOGGER.log(Level.INFO, "getting contextsettings from params '" + params
        + "'");
    String json = params.trim();
    // is this a simplified json for the wiki?
    if (!json.startsWith("{")) {
      json = "{";
      // convert params to json
      String[] paramparts = params.split(",");
      String delim = "";
      for (String param : paramparts) {
        String[] namevalue = param.split(":");
        if (namevalue.length == 2) {
          json += delim + "\"" + namevalue[0] + "\":\"" + namevalue[1] + "\"";
          delim = ",";
        }
      }
      json += "}";
    }
    ContextSetting cs = fromJson(json);
    return cs;
  }

  /**
   * create a SMWContextSetting from me
   * 
   * @return the ContextSetting
   */
  public ContextSetting asSMWContextSetting() {
    String json = this.toJson();
    ContextSetting result = ContextSetting.fromJson(json);
    result.setSidifInput(null);
    result.tripleStoreMode = TripleStoreMode.SMW;
    return result;
  }

  public Topic maintopic = null;

  /**
   * @return the maintopic
   * @throws Exception
   */
  public Topic getMaintopic() {
    if (maintopic == null && this.maintopicName != null) {
      Context context = ContextFactory.getInstance().getContext(this);
      maintopic = context.byName(this.maintopicName);
    }
    return maintopic;
  }

  /**
   * @param maintopic
   *          the maintopic to set
   */
  public void setMaintopic(Topic maintopic) {
    this.maintopic = maintopic;
  }

  /**
   * get the context Setting for this WikiTask
   * 
   * @return the ContextSetting
   */
  public static ContextSetting fromWikiTask(WikiTask wikiTask) {
    ContextSetting cs = ContextSetting.fromParams(wikiTask.getParams());
    cs.setWikiId(wikiTask.getWikiId());
    return cs;
  }
  
  /**
   * get the context setting for the given WikiTask as SiDIF
   * @param wikiTask
   * @return the ContextSetting for the given wikiTask
   */
  public static ContextSetting fromWikiTaskAsSiDIF(WikiTask wikiTask) {
    ContextSetting cs=fromWikiTask(wikiTask);
    cs.setTripleStoreMode(TripleStoreMode.SiDIF);
    return cs;
  }
}
