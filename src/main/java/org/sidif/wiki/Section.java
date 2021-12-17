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

import com.bitplan.mediawiki.japi.api.S;

/**
 * helper class for a Section
 * 
 * @author wf
 */
public class Section {
  protected static Logger LOGGER = Logger.getLogger("org.sidif.wiki");

  static boolean debug=false;
  String pageTitle;
  String anchor;
  String sectionNumber = null; // new / undefined Section
  int sectionIndex = -1;
  String sectionText;
  String sectionTitle;
  //allows checking the revision id
  Integer revid;

  /**
   * create the given Section from the given PageTitle
   * 
   * @param pageTitle
   */
  public Section(S s,String pageTitle, Integer revid) {
    this.pageTitle = pageTitle;
    this.revid=revid;
    if (debug) {
      LOGGER.log(Level.INFO, pageTitle + "#" + s.getAnchor()
          + " -> " + s.getIndex());
    }
    anchor = s.getAnchor();
    sectionNumber = s.getNumber();
    try {
      sectionIndex = Integer.parseInt(s.getIndex());
      sectionTitle = " " + s.getLine() + " ";
      for (int i = 1; i <= s.getLevel(); i++) {
        sectionTitle = "=" + sectionTitle + "=";
      }
    } catch (NumberFormatException nfe) {
      LOGGER.log(Level.WARNING, "ignored section "
          + sectionNumber + " " + sectionIndex + " "
          + anchor);
    }
  }
  
  /**
   * normalize the given anchor
   * 
   * @param anchor
   * @return the normalized anchor
   */
  public static String normalizeAnchor(String anchor) {
    // FIXME - anchors with special characters?
    String result = anchor.replace(" ", "_");
    return result;
  }
}
