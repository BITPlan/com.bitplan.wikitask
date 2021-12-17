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
package com.bitplan.wikitask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bitplan.mediawiki.japi.user.WikiUser;
import com.bitplan.ssh.SSH;
import com.bitplan.storage.jdbc.Credentials;
import com.bitplan.storage.jdbc.DataSource;
import com.bitplan.storage.jdbc.JDBCConnectionHolder;
import com.bitplan.storage.jdbc.JDBCQuery;
import com.bitplan.storage.jdbc.JDBCStorage;
import com.bitplan.storage.jdbc.MySQLConnection;

/**
 * SQL based MediaWiki access
 * 
 * @author wf
 *
 */
public class SQLWiki {
  public static boolean debug = false;
  public static int remotePort=3306;
  private WikiUser wikiUser;
  private String user;
  private String host;
  private SSH ssh;
  JDBCConnectionHolder connection;
  private JDBCStorage storage;
  private int forwardPort;

  /**
   * create an SQL wiki access
   * 
   * @param wikiId
   *          - the wiki to connect to
   */
  public SQLWiki(String wikiId) {
    wikiUser = WikiUser.getUser("wiki");

    user = wikiUser.getUsername();
    host = wikiUser.getUrl().replaceAll("(http|https)://", "");
  }

  /**
   * open the SQL Wiki
   * 
   * @throws Exception
   */
  public void open() throws Exception {
    setSsh(new SSH(user, host));
    SSH.debug = SQLWiki.debug;
    getSsh().createSession();
    getSsh().connect();
  }

  /**
   * connect to the database
   * @param c 
   * 
   * @throws Exception
   */
  public void sqlConnect(Credentials c, int localPort) throws Exception {
    forwardPort = getSsh().forward(remotePort,"localhost", localPort);
    connection = new MySQLConnection();
    connection.setPort(forwardPort);
    boolean connected=connection.connect("localhost",c.getDatabase(),c.getUser(),c.getPassword());
    if (!connected) {
      throw new Exception("connection to mysql database "+c.getDatabase()+" on "+c.getHost()+" with user "+c.getUser()+" failed!");
    }
    storage = new JDBCStorage(connection);
  }
  
  /**
   * run the given query and return a list of Maps
   * @param entityName
   * @param sql
   * @return the list of maps
   * @throws Exception
   */
  public List<Map<String, Object>> query(String entityName,String sql) throws Exception {
    if (storage==null)
      throw new IllegalStateException("storage is null you might want to call sqlConnect before query");
    JDBCQuery jquery = storage.createQuery(entityName, sql);
    DataSource rs = storage.executeQuery(jquery);
    List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
    while (rs.next()) {
      Map<String,Object> row=new HashMap<String,Object>();
      for (String key:rs.keys()) {
        row.put(key, rs.getString(key));
      }
      result.add(row);
    }
    return result;
  }

  /**
   * close the connection
   */
  public void close() {
    getSsh().disconnect();
  }

  /**
   * create SQL based wik access for the given wiki id
   * 
   * @param wikiId
   * @return - the SQL based Wiki access
   */
  public static SQLWiki fromWikiId(String wikiId) {
    SQLWiki sqlWiki = new SQLWiki(wikiId);
    return sqlWiki;
  }

  /**
   * @return the ssh
   */
  public SSH getSsh() {
    return ssh;
  }

  /**
   * @param ssh the ssh to set
   */
  public void setSsh(SSH ssh) {
    this.ssh = ssh;
  }
}
