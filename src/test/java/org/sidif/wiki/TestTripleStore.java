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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sidif.triple.Triple;
import org.sidif.triple.TripleQuery;
import org.sidif.triple.TripleStore;
import org.sidif.util.TripleStoreBuilder;
import org.sidif.util.TripleStoreDumper;

import com.alibaba.fastjson.JSON;
import com.bitplan.topic.TopicStatic.Topic;
import com.bitplan.topic.TopicStatic.TopicManager;

/**
 * test the tripleStore handling by the Template Code
 * 
 * @author wf
 *
 */
public class TestTripleStore {
  boolean debug=false;
  String sidif = "Property is maintopic of SiDIF\n"
      + "Topic isA Topic\n"
      + "\"Topic\" is name of it\n"
      + "\"Topics\" is pluralName of it\n"
      + "\"A Topic is a Concept/Class/Thing\" is wikiDocumentation of it\n"
      + "\"A Topic is a Concept/Class/Thing\" is documentation of it \n"
      + "\"property\" is defaultstoremode of it \n"
      + "Topic_name addsTo it\n"
      + "pluralName addsTo it\n"
      + "wikiDocumentation addsTo it\n"
      + "documentation addsTo it\n"
      + "defaultstoremode addsTo it\n"
      + "Topic_name isA Property\n"
      + "\"name\" is name of it\n"
      + "\"Text\" is type of it\n"
      + "\"name\" is label of it\n"
      + "true is primaryKey of it\n"
      + "true is mandatory of it\n"
      + "false is uploadable of it\n"
      + "80 is size of it \n"
      + "\"Topic\" is topic of it\n"
      + "SMW_Type isA Topic\n"
      + "\"SMW_Type\" is name of it\n"
      + "\"see \n"
      + "* https://semantic-mediawiki.org/wiki/Special:Types \n"
      + "* https://semantic-mediawiki.org/wiki/Help:List_of_datatypes\" is documentation of it\n"
      + "\"subobject\" is defaultstoremode of it \n"
      + "type addsTo it\n"
      + "typepage addsTo it\n"
      + "documentation addsTo it\n"
      + "helppage addsTo it\n"
      + "Property isA Topic\n"
      + "\"Property\" is name of it\n"
      + "\"Properties\" is pluralName of it\n"
      + "\"see\n"
      + " \n"
      + "    https://semantic-mediawiki.org/wiki/Help:Properties_and_types\n"
      + "    https://semantic-mediawiki.org/wiki/Help:Special_properties\" is wikiDocumentation of it\n"
      + " \n"
      + "\"a Property is a Feature/Attribute\" is documentation of it \n"
      + "\"property\" is defaultstoremode of it \n"
      + "name addsTo it\n"
      + "documentation addsTo it\n"
      + "type addsTo it\n"
      + "label addsTo it\n"
      + "primaryKey addsTo it\n"
      + "mandatory addsTo it\n"
      + "uploadable addsTo it\n"
      + "size addsTo it\n"
      + "defaultValue addsTo it\n"
      + "inputType addsTo it\n"
      + "allowedValues addsTo it\n"
      + "TopicLink isA Topic\n"
      + "\"TopicLink\" is name of it\n"
      + "name addsTo it\n"
      + "sourceRole addsTo it\n"
      + "sourceMultiple addsTo it\n"
      + "source addsTo it\n"
      + "targetRole addsTo it\n"
      + "targetMultiple addsTo it\n"
      + "target addsTo it\n"
      + "\"subobject\" is defaultstoremode of it \n"
      + "TopicLinks_containedProperties isA TopicLink\n"
      + "\"containedProperties\" is name of it\n"
      + "\"topic\" is sourceRole of it\n"
      + "false is sourceMultiple of it\n"
      + "\"Topic\" is source of it\n"
      + "\"properties\" is targetRole of it\n"
      + "true is targetMultiple of it\n"
      + "\"Property\" is target of it \n"
      + "# data\n"
      + "Annotation_URI isA SMW_Type\n"
      + "\"Annotation URI\" is type of it\n"
      + "\"Special:Types/Annotation_URI\" is typepage of it\n"
      + "\"Holds URIs, but has some technical differences during export compared to the 'URL' type\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_Annotation_URI\" is helppage of it\n"
      + "Boolean isA SMW_Type\n"
      + "\"Boolean\" is type of it\n"
      + "\"Special:Types/Boolean\" is typepage of it\n"
      + "\"Holds boolean (true/false) values\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_Boolean\" is helppage of it\n"
      + "Code isA SMW_Type\n"
      + "\"Code\" is type of it\n"
      + "\"Special:Types/Code\" is typepage of it\n"
      + "\"Holds technical, pre-formatted texts (similar to type Text)\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_Code\" is helppage of it\n"
      + "Date isA SMW_Type\n"
      + "\"Date\" is type of it\n"
      + "\"Special:Types/Date\" is typepage of it\n"
      + "\"Holds particular points in time\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_Date\" is helppage of it\n"
      + "Email isA SMW_Type\n"
      + "\"Email\" is type of it\n"
      + "\"Special:Types/Email\" is typepage of it\n"
      + "\"Holds e-mail addresses\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_Email\" is helppage of it\n"
      + "Geographic_coordinate isA SMW_Type\n"
      + "\"Geographic coordinate\" is type of it\n"
      + "\"Special:Types/Geographic_coordinate\" is typepage of it\n"
      + "\"Holds coordinates describing geographic locations\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_Geographic_coordinate\" is helppage of it\n"
      + "Number isA SMW_Type\n"
      + "\"Number\" is type of it\n"
      + "\"Special:Types/Number\" is typepage of it\n"
      + "\"Holds integer and decimal numbers, with an optional exponent\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_Number\" is helppage of it\n"
      + "Page isA SMW_Type\n"
      + "\"Page\" is type of it\n"
      + "\"Special:Types/Page\" is typepage of it\n"
      + "\"Holds names of wiki pages, and displays them as a link\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_Page\" is helppage of it\n"
      + "Quantity isA SMW_Type\n"
      + "\"Quantity\" is type of it\n"
      + "\"Special:Types/Quantity\" is typepage of it\n"
      + "\"Holds values that describe quantities, containing both a number and a unit\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_Quantity\" is helppage of it\n"
      + "Record isA SMW_Type\n"
      + "\"Record\" is type of it\n"
      + "\"Special:Types/Record\" is typepage of it\n"
      + "\"Allows saving compound property values that consist of a short list of values with fixed type and order\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_Record\" is helppage of it\n"
      + "Telephone_number isA SMW_Type\n"
      + "\"Telephone number\" is type of it\n"
      + "\"Special:Types/Telephone_number\" is typepage of it\n"
      + "\"Holds international telephone numbers based on the [https://tools.ietf.org/html/rfc3966 RFC 3966 standard]\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_Telephone_number\" is helppage of it\n"
      + "Temperature isA SMW_Type\n"
      + "\"Temperature\" is type of it\n"
      + "\"Special:Types/Temperature\" is typepage of it\n"
      + "\"Holds temperature values (similar to type Quantity)\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_Temperature\" is helppage of it\n"
      + "Text isA SMW_Type\n"
      + "\"Text\" is type of it\n"
      + "\"Special:Types/Text\" is typepage of it\n"
      + "\"Holds text of arbitrary length\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_Text\" is helppage of it\n"
      + "URL isA SMW_Type\n"
      + "\"URL\" is type of it\n"
      + "\"Special:Types/URL\" is typepage of it\n"
      + "\"Holds URIs, URNs and URLs\" is documentation of it\n"
      + "\"https://semantic-mediawiki.org/wiki/Help:Type_URL\" is helppage of it";

  TripleStore tripleStore;

  /**
   * read the tripleStore from the given sidif
   * 
   * @param sidif
   * @throws Exception
   */
  public void doit(String sidif) throws Exception {
    tripleStore = TripleStoreBuilder.fromSiDIFText(sidif);
    // global query variable
    // which selects all triples in the triplestore
    TripleQuery query = tripleStore.query();
    // query which selects only topics
    TopicManager mTopicManager = new TopicManager(query);
    mTopicManager.configureTopics();

    Triple mainTriple = query.selectSingle(null, "maintopic", null);
    Topic maintopic = null;
    String maintopicName = null;
    assertNotNull(mainTriple);
    maintopicName = mainTriple.getObject().toString();
    maintopic = mTopicManager.byName(maintopicName);
    assertNotNull(maintopic);
  }
  
  @Test
  public void testTripleStore() throws Exception {
    doit(sidif);
    debug=TestSuite.debug;
    if (debug)
      TripleStoreDumper.dump(tripleStore);
  }

}
