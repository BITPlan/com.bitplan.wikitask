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
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.bitplan.ssh.SSH;
import com.bitplan.storage.jdbc.Credentials;
import com.bitplan.wikitask.SQLWiki;

/**
 * test remote SSH based access
 * 
 * @author wf
 *
 */
public class TestSSH extends BaseTest {

  @Test
  /**
   * test SSH connection
   * 
   * @throws Exception
   */
  public void testSSH() throws Exception {
    if (!super.isTravis()) {
      SQLWiki.debug = true;
      String wikiId = "wiki";
      SQLWiki sqlWiki = SQLWiki.fromWikiId(wikiId);
      sqlWiki.open();
      Credentials c = WikiManager.getDBCredentialsForWikiId(wikiId);
      sqlWiki.sqlConnect(c, 3309);
      List<Map<String, Object>> categories = sqlWiki.query("category",
          "select * from category");
      assertTrue(categories.size()>50);
      if (debug) {
        for (Map<String, Object> categoryRow : categories) {
          for (Entry<String, Object> categoryEntry : categoryRow.entrySet()) {
            System.out.println(String.format("%s=%s", categoryEntry.getKey(),
                categoryEntry.getValue()));
          }
        }
      }
      sqlWiki.close();
    }
  }

  @Test
  public void testCommand() throws Exception {
    if (!super.isTravis()) {
      SQLWiki.debug = true;
      String wikiId = "wiki";
      SQLWiki sqlWiki = SQLWiki.fromWikiId(wikiId);
      sqlWiki.open();
      SSH ssh = sqlWiki.getSsh();
      String cmdOutput = ssh.execute("uname");
      assertEquals("Linux\n", cmdOutput);
      // File test = new File("/tmp/test.txt");
      // sqlWiki.getSsh().scp("test.txt", new FileOutputStream(test));
      sqlWiki.close();
    }
  }

}
