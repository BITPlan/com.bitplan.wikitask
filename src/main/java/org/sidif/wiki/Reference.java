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

import com.bitplan.mediawiki.japi.MediawikiApi;

/**
 * I am a reference to some content references can be like
 * http://somedomain/somepath page#section id or by directly specifying the
 * content
 * 
 * @author wf
 *
 */
public class Reference extends CachedImpl {

  public static boolean debug = true;

  // modal style of reference
  // refactor to subclasses if necessary
  public enum ReferenceType {
    PAGE, ANCHOR, URL, ID, CONTENT, INVALID
  };

  ReferenceType referenceType;
  MediawikiApi wiki; // the referenced wiki (may be null for CONTENT and URL
                     // referenceType)
  String pageTitle; // the pageTitle
  String id; // the source id (not null for referenceType=ID)
  String content; // the full page or content of the reference
  String anchor; // the anchor
  String link;
  String url; // the url of the reference
  String normalizedAnchor;

  boolean isTarget = false; // the target page may not exist

  /**
   * @return the anchor
   */
  public String getAnchor() {
    return anchor;
  }

  /**
   * @param anchor
   *          the anchor to set
   */
  public void setAnchor(String anchor) {
    this.anchor = anchor;
  }

  /**
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * @param content
   *          the content to set
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * @return the link
   */
  public String getLink() {
    return link;
  }

  /**
   * @param link
   *          the link to set
   */
  public void setLink(String link) {
    this.link = link;
  }

  /**
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url
   *          the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return the isTarget
   */
  public boolean isTarget() {
    return isTarget;
  }

  /**
   * @param isTarget
   *          the isTarget to set
   */
  public void setTarget(boolean isTarget) {
    this.isTarget = isTarget;
  }

  /**
   * @return the referenceType
   */
  public ReferenceType getReferenceType() {
    return referenceType;
  }

  /**
   * @param referenceType
   *          the referenceType to set
   */
  public void setReferenceType(ReferenceType referenceType) {
    this.referenceType = referenceType;
  }

  /**
   * create a Reference for the given content
   * 
   * @param referenceType
   * @param content
   */
  public Reference(ReferenceType referenceType, String content) {
    this.referenceType = referenceType;
    this.content = content;
  }


  /**
   * get the page Url for the given pageTitle
   * 
   * @param pageTitle
   * @return the pageUrl
   */
  public String getPageUrl(String pageTitle) {
    String siteurl=this.wiki.getSiteurl();
    String scriptPath=this.wiki.getScriptPath();
    if (!scriptPath.endsWith("/")) {
      scriptPath+="/";
      LOGGER.log(Level.WARNING,"scriptpath for "+wiki.getSiteurl()+" does not end with / - have added it");
    }
    String result = siteurl + scriptPath + "index.php/" + pageTitle;
    return result;
  }

  /**
   * create a a reference with the given wiki, link and pageTitle
   * 
   * @param wiki
   * @param link
   *          can be like http://somedomain/somepath or page#section or id
   * @param sourcePageLink
   *          if only an id is specified get the pageTitle and pageContent from
   *          this reference
   * @throws Exception
   */
  public Reference(MediawikiApi wiki, String link, Reference sourcePageLink)
      throws Exception {
    this.wiki = wiki;
    this.link = link;
    // do we have a default pageLink?
    if (sourcePageLink != null) {
      this.pageTitle = sourcePageLink.pageTitle;
      this.content = sourcePageLink.content;
    }
    this.referenceType = ReferenceType.INVALID;
    if (link != null) {
      if (link.startsWith("http:") || link.startsWith("https:")) {
        referenceType = ReferenceType.URL;
        this.url = link;
        // FIXME - load content here?
      } else {
        String[] idparts = link.split("#");
        if (idparts.length > 2) {
          throw new IllegalArgumentException(link
              + " has more than one anchor #");
        }
        if (idparts.length == 2) {
          anchor = idparts[1];
          pageTitle = idparts[0];
          normalizedAnchor = Section.normalizeAnchor(anchor);
          referenceType = ReferenceType.ANCHOR;
          url = getPageUrl(pageTitle + "#" + normalizedAnchor);
        } else {
          if (sourcePageLink != null) {
            id = link;
            referenceType = ReferenceType.ID;
            url = getPageUrl(pageTitle);
          } else {
            pageTitle = link;
            url = getPageUrl(pageTitle);
            referenceType = ReferenceType.PAGE;
          } // if sourcePageLink != null
        } // if idparts.length == 2
      } // if link startsWith http
    } // if link !=null
  }

  /**
   * get the reference id for this reference
   * 
   * @return - the id string
   */
  public String getReferenceId() {
    String result = "?";
    switch (referenceType) {
    case CONTENT:
      result = "" + this.hashCode();
      break;
    case URL:
      result = link;
      break;
    case ID:
      result = pageTitle + "@id@" + id;
      break;
    case ANCHOR:
      result = link;
      break;
    case PAGE:
      result = pageTitle;
      break;
    case INVALID:
      result = "" + this.hashCode();
      break;
    }
    if (debug)
      LOGGER.log(Level.INFO, "reference id is " + result);
    return result;
  }

}
