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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.bitplan.storage.jdbc.Credentials;
import com.bitplan.topic.WikiStatic;
import com.bitplan.topic.WikiStatic.Wiki;

/**
 * as Wiki Manager
 * 
 * @author wf
 *
 */
public class WikiManager extends WikiStatic.WikiManager {

  static Pattern nameValue = Pattern
      .compile("\\s*\\$([^= \t\r\n\f]*)\\s*=\\s*\"([^=\\s]*)\"\\s*;");
  // Pattern.compile("^.*\\$([^=]+?).*=.*\"([^\"]+?)\".*$");

  /**
   * get the local Settings for the given wikiid
   * 
   * @param wikiid
   * @return local settings for the given wikiid
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static String getLocalSettings(String wikiid)
      throws FileNotFoundException, IOException {
    File wikiPropertyFile = WikiTask.getPropertyFile("wikis");
    Properties props = new Properties();
    props.load(new FileReader(wikiPropertyFile));
    String localSettings = props.getProperty(wikiid);
    if (localSettings == null) {
      throw new IllegalArgumentException(
          "LocalSettings for " + wikiid + " not configured in wikis.ini");
    }
    return localSettings;
  }

  /**
   * get the given variable from the given settings
   * 
   * @param varname
   * @param settings
   */
  public static String getVariable(String varname,
      Map<String, String> settings) {
    String result = settings.get(varname);
    return result;
  }

  /**
   * get the name value map of variable settings
   * 
   * @param settings
   * @return the name/value Map
   */
  public static Map<String, String> getVariables(String settings) {
    Map<String, String> result = new HashMap<String, String>();
    Matcher m = nameValue.matcher(settings);
    while (m.find()) {
      String name = m.group(1);
      String value = m.group(2);
      // System.out.println(name+"="+value);
      result.put(name, value);
    }
    return result;
  }

  /**
   * get the SQL database credentials for the given wikiId
   * @param wikiId
   * @return the credentials
   * @throws Exception
   */
  public static Credentials getDBCredentialsForWikiId(String wikiId) throws Exception {
    String localSettingsFilename = getLocalSettings(wikiId);
    Credentials c = getDBCredentials(localSettingsFilename);
    return c;
  }

  /**
   * get the database credentials for the given localSettings Filename
   * @param localSettingsFilename
   * @return the db credentials
   * @throws Exception
   */
  public static Credentials getDBCredentials(String localSettingsFilename) throws Exception {
    Credentials c = new Credentials();
    String localSettings = FileUtils
        .readFileToString(new File(localSettingsFilename), "utf-8");
    Map<String, String> settings = getVariables(localSettings);
    c.setHost(getVariable("wgDBserver", settings));
    c.setDatabase(getVariable("wgDBname", settings));
    c.setUser(getVariable("wgDBuser", settings));
    c.setPassword(getVariable("wgDBpassword", settings));
    return c;
  }

  /**
   * get the wiki for the given id
   * 
   * @param wikiid
   * @return Wiki
   * @throws Exception
   */
  public static WikiStatic.Wiki getWiki(String wikiid) throws Exception {
    File wikiJsonfile = WikiTask.getPropertyFile("wikis", ".json");
    String json = FileUtils.readFileToString(wikiJsonfile, "utf-8");
    com.bitplan.topic.WikiStatic.WikiManager wm = com.bitplan.topic.WikiStatic.WikiManager
        .fromJson(json);

    Wiki wiki = wm.mWikiMap.get(wikiid);
    if (wiki == null) {
      throw new IllegalArgumentException("wiki with id '" + wikiid
          + "' not configured in " + wikiJsonfile.getPath());
    }

    return wiki;
  }
}
