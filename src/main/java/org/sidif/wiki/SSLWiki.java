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
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bitplan.rest.Crypt;
import com.bitplan.rest.CryptImpl;

/**
 * an SSL wiki
 * 
 * @author wf http://stackoverflow.com/questions/2703161/how-to-ignore-ssl-
 *         certificate -errors-in-apache-httpclient-4-0
 */
public class SSLWiki extends com.bitplan.mediawiki.japi.SSLWiki {
  public static boolean debug=false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.mediawiki.japi.SSLWiki");
  
	/**
	 * constructor
	 * 
	 * @param url
	 * @throws Exception
	 */
	public SSLWiki(String url) throws Exception {
		super(url);
	}

	/**
	 * construct me from an url and scriptPath
	 * 
	 * @param url
	 * @param scriptPath
	 * @throws Exception
	 */
	public SSLWiki(String url, String scriptPath) throws Exception {
		super(url, scriptPath);
	}

	/**
	 * constructor with three params
	 * 
	 * @param url
	 * @param scriptPath
	 * @param wikiid
	 * @throws Exception
	 */
	public SSLWiki(String url, String scriptPath, String wikiid) throws Exception {
		super(url, scriptPath,wikiid);
	}
	
	/**
   * get the path to the initialization files
   * 
   * @return the path
   */
  public static File getIniPath() {
    File iniPath = new File(System.getProperty("user.home") + "/.sslwiki");
    if (!iniPath.isDirectory()) {
      iniPath.mkdirs();
    }
    return iniPath;
  }
  /**
   * get the encryption
   * @return
   */
  public static Crypt getCrypt(String prefix) {
    Crypt pcf = new CryptImpl(prefix+"KBKMhZb57ljt5pR3rC271w9w7V1NWdojRa", "Z3A0VBSR");
    return pcf;
  }
  
  public static void initSSLCredentials() {
    File propertyFile = new File(getIniPath(), System.getProperty("user.name")  + "_ssl.ini");
    Crypt crypt=getCrypt(propertyFile.getName());
    Properties jproperties = new Properties();
    try {
      jproperties.load(new FileInputStream(propertyFile));
      for (String key : jproperties.stringPropertyNames()) {
        String value=jproperties.getProperty(key);
        if (key.endsWith("Password")) {
          value=crypt.decrypt(value);
          // System.out.println(key+"="+crypt.encrypt(value));
        }
        System.setProperty(key, value);
      }
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "error " + e.getMessage()
          + " for propertyFile " + propertyFile.getAbsolutePath());
    }
    if (debug) {
      System.setProperty("javax.net.debug","ssl");  // very verbose debug
    }
  }
  
}
