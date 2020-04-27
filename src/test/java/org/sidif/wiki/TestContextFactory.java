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
package org.sidif.wiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.bitplan.topic.ContextFactory;
import com.bitplan.topic.ContextSetting;
import com.bitplan.topic.ContextSetting.TripleStoreMode;
import com.bitplan.topic.TopicStatic.Context;
import com.bitplan.topic.TopicStatic.ContextManager;
import com.bitplan.topic.TopicStatic.Property;
import com.bitplan.topic.TopicStatic.Topic;
import com.bitplan.topic.TopicStatic.YTManager;
import com.bitplan.topic.WikiStatic;

/**
 * test the context Factory code
 * 
 * @author wf
 *
 */
public class TestContextFactory extends BaseTest {
  private ContextManager cm;

  @Test
  public void testYTs() throws Exception {
    YTManager ytm = new YTManager();
    String json = ytm.toJson();
    if (debug) {
      System.out.println(json);
    }
    YTManager ytm2 = YTManager.fromJson(json);
    String json2 = ytm2.toJson();
    assertEquals(json, json2);
  }

  @Test
  public void testWikis() throws Exception {
    if (this.isTravis()) return;
    WikiStatic.Wiki lwiki = WikiManager.getWiki("wiki");
    if (debug) {
      System.out.println("id: " + lwiki.getId());
      System.out.println("siteurl:" + lwiki.getSiteurl());
      System.out.println("lang: " + lwiki.getLanguage());
    }
    assertEquals("wiki", lwiki.getId());
  }

  /**
   * prettyprint the given json
   * 
   * @param json
   * @return
   */
  public String pretty(String json) {
    json = json.replace("{", "\n{").replace("\",\"", "\",\n\"");
    return json;
  }

  /**
   * get the ContextManager of the master Context
   * 
   * @return
   */
  public ContextManager getMasterContextManager() {
    if (cm == null) {
      ContextFactory contextFactory = ContextFactory.getInstance();
      ContextSetting cs = ContextSetting.fromParams(
          "wikiId:master,tripleStoreMode:SMW,contextName:MetaModel,maintopicName:Topic");
      cm = contextFactory.getContextManager(cs);
    }
    return cm;
  }

  @Test
  public void testContextFactory() throws Exception {
    if (this.isTravis()) return;
    ContextManager cm = getMasterContextManager();
    String json = cm.toJson();
    String sidif = cm.asSiDIF();
    // debug=true;
    if (debug) {
      System.out.println(pretty(json));
      System.out.println(sidif);
    }

    ContextManager cmr = ContextManager.fromJson(json);
    // debug=true;
    String json2 = cmr.toJson();
    // if (!json.equals(json2) && debug)
    // DiffHelper.showDiff(pretty(json), pretty(json2));
    assertEquals(json, json2);
    assertTrue(cmr.mContexts.size() > 0);
    if (debug) {
      for (Context context : cmr.mContexts) {
        for (Topic topic : context.getTopics()) {
          for (Property property : topic.getProperties()) {
            System.out.println(context.name + "::" + topic.name + "::"
                + property.name + " mandatory=" + property.mandatory);
          }
        }
      }
    }
  }

  /**
   * test the helper functions to check properties by their values
   */
  @Test
  public void testPropertiesBy() {
    if (this.isTravis())
      return;
    ContextManager cm = this.getMasterContextManager();
    Context mm = cm.mContextMap.get("MetaModel");
    assertNotNull(mm);
    String topicNames[] = { "Topic", "Property" };
    int expectedSorted[] = { 1, 1 };
    int index = 0;
    for (String topicName : topicNames) {
      Topic topic = mm.topics.mTopicMap.get(topicName);
      List<Property> propsByIndex = topic.propertiesByIndex();
      List<Property> sortProperties = topic.sortProperties();
      // debug = true;
      if (debug) {
        System.out.println(String.format("%s by index:", topicName));
        for (Property prop : propsByIndex) {
          System.out.println(String.format("%2s(%1s):%s", prop.index==null?" ":prop.index,
              prop.sortPos == null ? " " : prop.sortPos, prop.getName()));
        }
        System.out.println("sortpos:");
        for (Property prop : sortProperties) {
          System.out.println(prop.sortPos + ":" + prop.getName());
        }
      }
      // check the list of properties as of 2016-11-11 it would be:
      /*
       * 1:name 2:pluralName 3:icon 4:iconUrl 5:documentation
       * 6:wikiDocumentation 7:defaultstoremode 8:listLimit 9:cargo
       * 10:headerTabs 11:context
       */
      assertNotNull(propsByIndex);
      Property nameProp = propsByIndex.get(0);
      assertEquals("name", nameProp.getName());
      assertNotNull(sortProperties);
      assertEquals(expectedSorted[index], sortProperties.size());
      for (Property prop : sortProperties) {
        assertTrue(prop.sortAscending());
      }
      index++;
    }
  }

  @Test
  public void testSmartRQM() {
    if (this.isTravis())
      return;
    // TestSSL.setUpCredentials();
    ContextSetting cs = ContextSetting.fromParams(
        "wikiId:sg,sidifInput:SmartRQM#sidif,contextName:SmartRQM,maintopicName:RQProfile");
    Context smartRQM = ContextFactory.getInstance().getContext(cs);
    assertNotNull(smartRQM);
    assertTrue("there should be multiple topics",
        smartRQM.getTopics().size() > 0);
    debug = true;
    for (Topic topic : smartRQM.getTopics()) {
      if (debug) {
        String icon = "";
        if (topic.getIcon() != null)
          icon = topic.getIcon();
        System.out.println(topic.getName() + " " + icon);
      }
      for (Property property : topic.getProperties()) {
        assertTrue(topic.getProperties().size() > 0);
        if (debug) {
          System.out.println("\t" + property.getName());
        }
      }
    }
  }

  @Test
  public void testContextSetting() {
    if (this.isTravis()) return;
    ContextSetting cs = ContextSetting.fromParams(
        "wikiId:capri,tripleStoreMode:SMW,contextName:MetaModel,maintopicName:Topic");
    String json = cs.toJson();
    debug = true;
    if (debug)
      System.out.println(json);
    assertEquals("Topic", cs.getMaintopicName());
    assertEquals("MetaModel", cs.getContextName());
    assertEquals(TripleStoreMode.SMW, cs.getTripleStoreMode());
    assertEquals("capri::smw", cs.getTripleStoreId());
    cs = ContextSetting.fromParams(
        "wikiId:capri,sidifInput:Topic#sidif,contextName:MetaModel,maintopicName:Topic");
    json = cs.toJson();
    if (debug)
      System.out.println(json);
    assertEquals("Topic", cs.getMaintopicName());
    assertEquals("MetaModel", cs.getContextName());
    assertEquals(TripleStoreMode.SiDIF, cs.getTripleStoreMode());
    assertEquals("capri:Topic#sidif", cs.getTripleStoreId());
  }

  @Test
  public void testSiDIF_Difference() throws Exception {
    if (this.isTravis())
      return;
    // TestSSL.setUpCredentials();
    // ContextFactory.reset();
    ContextSetting csSiDIF = ContextSetting.fromParams(
        "wikiId:media,sidifInput:Topic#sidif,contextName:MetaModel,maintopicName:Topic");
    Context sidifMeta = ContextFactory.getInstance().getContext(csSiDIF);
    ;
    Context smwMeta = ContextFactory.getInstance()
        .getContext(csSiDIF.asSMWContextSetting());
    String smwSiDIF = smwMeta.asSiDIF();
    String siDIF = sidifMeta.asSiDIF();
    // debug=true;
    // if (!smwSiDIF.equals(siDIF) && debug)
    // DiffHelper.showDiff("SMW\n" + smwSiDIF, "SiDIF\n" + siDIF);
    if (debug) {
      System.out.println(smwSiDIF);
      System.out.println(siDIF);
    }
    assertFalse(siDIF.contains("Context_Context_name"));
  }

}
