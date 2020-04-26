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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.bitplan.topic.WikiStatic;
import com.bitplan.topic.WikiStatic.Wiki;

/**
 * as Wiki Manager
 * @author wf
 *
 */
public class WikiManager extends WikiStatic.WikiManager {
	 /**
   * get the local Settings for the given wikiid
   * @param wikiid
   * @return
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static String getLocalSettings(String wikiid) throws FileNotFoundException, IOException {
    File wikiPropertyFile=WikiTask.getPropertyFile("wikis");
    Properties props = new Properties();
    props.load(new FileReader(wikiPropertyFile));
    String localSettings = props.getProperty(wikiid);
    if (localSettings==null) {
      throw new IllegalArgumentException("LocalSettings for "+wikiid+" not configured in wikis.ini");
    }
    return localSettings;
  }
  
  /**
   * get the wiki for the given id
   * @param wikiid
   * @return
   * @throws Exception
   */
  public static WikiStatic.Wiki getWiki(String wikiid) throws Exception {
  	File wikiJsonfile=WikiTask.getPropertyFile("wikis",".json");
  	String json=FileUtils.readFileToString(wikiJsonfile,"utf-8");
  	com.bitplan.topic.WikiStatic.WikiManager wm = com.bitplan.topic.WikiStatic.WikiManager.fromJson(json);
  			
  	Wiki wiki = wm.mWikiMap.get(wikiid);
    if (wiki==null) {
      throw new IllegalArgumentException("wiki with id '"+wikiid+"' not configured in "+wikiJsonfile.getPath());
    }

  	return wiki;
  }
}
