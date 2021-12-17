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
package org.sidif.wiki.rest;

import org.kohsuke.args4j.Option;
import org.sidif.wiki.rest.AuthFilter.AccessRight;

import com.bitplan.rest.RestServerImpl;
import java.util.logging.Logger;

/**
 * start a WikiServer
 * @author wf
 *
 */
public class WikiTaskServer extends RestServerImpl {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.wikitask.rest");

  /**
   * construct WikiTaskServer
   * setting defaults
   * @throws Exception 
   */
  public WikiTaskServer() throws Exception {
    super();
    settings.setHost("0.0.0.0");
    settings.setPort(9089);
    String packages="org.sidif.wiki.resources;org.sidif.wiki.rest;";
    // com.bitplan.resthelper.resources;
    settings.setContextPath("/wikiserver".toLowerCase());
    // add a static handler
    settings.addClassPathHandler("/", "com/bitplan/wikitask/webcontent/");
    // BITPlan specific parts - see org.sidif.wiki for Intranet use
    //settings.addClassPathHandler("/resthtmlview", "com/bitplan/resthtmlview/webcontent/");
    //settings.addClassPathHandler("/stockicons","com/bitplan/icons/");
    // settings.addClassPathHandler("/fileicon", "com/bitplan/clientutils/rest/icons/");
    settings.setPackages(packages);
    // super.useFastJson=false;
    // Authentication
    // useServlet=true;
    String[] requestFilters={"org.sidif.wiki.rest.AuthFilter"};
    settings.setContainerRequestFilters(requestFilters);
    // default access rights for localhost
    AuthFilter.accessRights.put("127.0.0.1", AccessRight.write);
    AuthFilter.accessRights.put("fe80:0:0:0:0:0:0:1%1",AccessRight.write);
    AuthFilter.accessRights.put("0:0:0:0:0:0:0:1",AccessRight.write);
  }
  
  
  /**
   * start Server
   * 
   * @param args
   * @throws Exception
   */
   public static void main(String[] args) throws Exception {
     WikiTaskServer rs=new WikiTaskServer();
     rs.settings.parseArguments(args);
     rs.startWebServer();
   } // main
}
