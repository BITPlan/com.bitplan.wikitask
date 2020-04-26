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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.sidif.triple.TripleStore;
import org.sidif.triple.impl.TripleImpl;

import com.bitplan.storage.jdbc.DataSource;
import com.bitplan.storage.jdbc.JDBCConnectionHolder;
import com.bitplan.storage.jdbc.JDBCQuery;
import com.bitplan.storage.jdbc.JDBCStorage;
import com.bitplan.storage.jdbc.MySQLConnection;

/**
 * direct access to SMW Triples via JDBC
 * @author wf
 *
 */
public class SMW_Triples {
	protected static Logger LOGGER = Logger.getLogger("org.sidif.wiki");
  public static String localSettingsFilename;
  
  static Pattern nameValue = 
      Pattern.compile("\\s*\\$([^= \t\r\n\f]*)\\s*=\\s*\"([^=\\s]*)\"\\s*;");
      // Pattern.compile("^.*\\$([^=]+?).*=.*\"([^\"]+?)\".*$");
  
  static final String tripleSQLQuery="select * from smw_triples_ns";
  
  /**
   * get the name value map of variable settings
   * @param settings
   * @return
   */
  public static Map<String,String> getVariables(String settings) {
    Map<String,String> result=new HashMap<String,String>();
    Matcher m=nameValue.matcher(settings);
    while(m.find()){
      String name=m.group(1);
      String value=m.group(2);
      // System.out.println(name+"="+value);
      result.put(name,value);
    }
    return result;
  }
  
  /**
   * get the given variable from the given settings
   * @param varname
   * @param settings
   */
  public static String getVariable(String varname,Map<String,String> settings) {
    String result=settings.get(varname);
    return result;
  }
  
  /**
   * get the tripleStore from this wiki (default is including namespace info)
   * @return - the tripleStore
   * @throws Exception
   */
  public static TripleStore fromWikiId(String wikiId) throws Exception {
    TripleStore result=fromQuery(wikiId,tripleSQLQuery);
    return result;
  }
  
  /**
   * get the query with the given where clause
   * @param where
   * @return
   */
  public static String getQuery(String where) {
		return tripleSQLQuery+" "+where;
	}
  
  /**
   * get the tripleStore from the given query
   * @param query
   * @return
   * @throws Exception
   */
  public static TripleStore fromQuery(String wikiId,String query) throws Exception {
    localSettingsFilename=WikiManager.getLocalSettings(wikiId);
    TripleStore result=fromWiki(localSettingsFilename,query);
    return result;
  }
  
  /**
   * get the tripleStore from the given localSettingsFilename
   * @param localSettingsFilename
   * @return
   * @throws Exception
   */
  public static TripleStore fromWiki(String localSettingsFilename) throws Exception {
    if (localSettingsFilename==null) {
      throw new IllegalArgumentException("localSettingsFilename may not be null");
    }
    TripleStore result=fromWiki(localSettingsFilename,tripleSQLQuery);
    return result;
  }
  
  /**
   * get the TripleStore from the given Wiki
   * @param localSettings 
   * @return
   * @throws Exception 
   */
  public static TripleStore fromWiki(String localSettingsFilename, String query) throws Exception {
    String localSettings=FileUtils.readFileToString(new File(localSettingsFilename));
    Map<String, String> settings = getVariables(localSettings);
    String host=getVariable("wgDBserver",settings);
    String database=getVariable("wgDBname",settings);
    String user=getVariable("wgDBuser",settings);
    String password=getVariable("wgDBpassword",settings);
    TripleStore tripleStore=SMW_Triples.fromJDBC(host, database,user,password,query);
    return tripleStore;
  }

  /**
   * get the tripleStore from the given JDBC Source
   * @param host
   * @param database
   * @param user 
   * @param password 
   * @param query - the query to execute
   * @return
   * @throws Exception 
   */
  public static TripleStore fromJDBC(String host,String database, String user, String password, String query) throws Exception {
  	LOGGER.log(Level.INFO, "getting TripleStore from database "+host+":"+database);
    TripleStore result=new TripleStore();
    JDBCConnectionHolder connection=new MySQLConnection();
    boolean connected=connection.connect(host,database,user,password);
    if (!connected) {
      throw new Exception("connection to mysql database "+database+" on "+host+" with user "+user+" failed!");
    }
    JDBCStorage storage=new JDBCStorage(connection);
    JDBCQuery jquery = storage.createQuery("smw_triple", query);
    DataSource rs = storage.executeQuery(jquery);
    while (rs.next()) {
      String subject=rs.getString("subject");
      String predicate=rs.getString("predicate");
      String object=rs.getString("object");
      TripleImpl triple=new TripleImpl(subject,predicate,object);
      result.add(triple);
    }
    return result;
  }

	
   
}
