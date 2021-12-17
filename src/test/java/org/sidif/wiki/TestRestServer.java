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
package org.sidif.wiki;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sidif.wiki.rest.WikiTaskServer;

import com.bitplan.rest.RestServer;

/**
 * extend the TestRestserver for specific WikiTaskServer tests
 * @author wf
 *
 */
public class TestRestServer extends com.bitplan.rest.test.TestRestServer {
  // if debugging is on logging information may be written
  public static boolean debug = false;
  
  // prepare a LOGGER
  protected static Logger LOGGER = Logger.getLogger("org.sidif.wiki");


  @Override
  public RestServer createServer() throws Exception {
    RestServer result = new WikiTaskServer();
    return result;
  }

  public static void shutdownServer() throws Exception {
    if (debug) {
      LOGGER.log(Level.INFO, "Stopping server");
    }
    rs.stop();
  }

}
