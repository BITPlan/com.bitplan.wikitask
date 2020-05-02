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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sidif.triple.TripleStore;
import org.sidif.triple.impl.TripleImpl;

import com.bitplan.storage.jdbc.Credentials;
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
  
  static final String tripleSQLQuery="select * from smw_triples_ns";
   
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
   * @return the query with the where clause appended
   */
  public static String getQuery(String where) {
		return tripleSQLQuery+" "+where;
	}
  
  /**
   * get the tripleStore from the given query
   * @param query
   * @return the TripleStore
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
   * @return the TripleStore
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
   * @param localSettingsFilename
   * @param query
   * @return the TripleStore
   * @throws Exception 
   */
  public static TripleStore fromWiki(String localSettingsFilename, String query) throws Exception {
    Credentials c=WikiManager.getDBCredentials(localSettingsFilename); 
    TripleStore tripleStore=SMW_Triples.fromJDBC(c,query);
    return tripleStore;
  }

  /**
   * get the tripleStore from the given JDBC Source
   * @param c - the credentials to use
   * @param query - the query to execute
   * @return the TripleStore
   * @throws Exception 
   */
  public static TripleStore fromJDBC(Credentials c, String query) throws Exception {
  	LOGGER.log(Level.INFO, "getting TripleStore from database "+c.getHost()+":"+c.getDatabase());
    TripleStore result=new TripleStore();
    JDBCConnectionHolder connection=new MySQLConnection();
    boolean connected=connection.connect(c.getHost(),c.getDatabase(),c.getUser(),c.getPassword());
    if (!connected) {
      throw new Exception("connection to mysql database "+c.getDatabase()+" on "+c.getHost()+" with user "+c.getUser()+" failed!");
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
