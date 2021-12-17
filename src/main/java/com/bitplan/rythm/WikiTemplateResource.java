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
package com.bitplan.rythm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.rythmengine.resource.ITemplateResource;
import org.rythmengine.resource.TemplateResourceBase;
import org.rythmengine.resource.ToStringTemplateResource;
import org.sidif.wiki.Reference;
import org.sidif.wiki.ReferenceManager;
import org.sidif.wiki.Source;

import com.bitplan.mediawiki.japi.MediawikiApi;

/**
 * 
 * @author wf
 *
 */
public class WikiTemplateResource extends TemplateResourceBase implements
    ITemplateResource {
  /**
   * 
   */
  private static final long serialVersionUID = 67049021377257267L;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.rythm");
  public static boolean debug=false;

  Reference reference;
  ReferenceManager referenceManager;
  String key;

  static int classCount = 1;
  static int errCount = 1;
  
  String pageContent;

  /**
   * 
   * @param wikiTemplateResourceLoader
   * @param tmplName
   * @param wiki
   * @throws Exception
   */
  public WikiTemplateResource(
      WikiTemplateResourceLoader wikiTemplateResourceLoader, String tmplName,
      MediawikiApi wiki) throws Exception {
    super(wikiTemplateResourceLoader);
    key = tmplName;
    String link = tmplName.replace("wiki.", "");
    link = link.replace(".", "#");
    referenceManager = wikiTemplateResourceLoader.getReferenceManager();
    reference = referenceManager.getReference(wiki, link);
    referenceManager.addSources(reference);
  }

  /**
   * construct me as an error Resource
   * 
   * @param wikiTemplateResourceLoader
   * @param th - the reason for this error
   */
  public WikiTemplateResource(
      WikiTemplateResourceLoader wikiTemplateResourceLoader, Throwable th) {
    super(wikiTemplateResourceLoader);
    key = "Err_" + (errCount++);
    setError(th);
  }

  @Override
  public Object getKey() {
    return key;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  protected long defCheckInterval() {
    // 5 minutes?
    long result = 1000 * 300;
    return result;
  }

  @Override
  protected long lastModified() {
    return 0;
  }
  
  @Override
  public String asTemplateContent() {
    // no caching ...
    return reload();
  }

  @Override
  protected String reload() {
    // check whether this is an Error Resource
    if (this.getError()!= null) {
      String errMsg=this.getError().getMessage();
      if (debug) {
        LOGGER.log(Level.INFO,"reload fetches error "+this.key+": "+errMsg);   
      }
      return "";
    } else {
      if (pageContent == null) {
        Source source;
        try {
          source = referenceManager.getSource(reference);
          if (source == null) {
            throw new Exception("reload could not getSource for "
                + reference.getLink());
          }
          pageContent = source.getSource();
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
      }
      return pageContent;
    }
  }

  @Override
  public String getSuggestedClassName() {
    return "C_" + (classCount++) + "_" + key;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj instanceof ToStringTemplateResource) {
      WikiTemplateResource that = (WikiTemplateResource) obj;
      return that.getKey().equals(getKey());
    }
    return false;
  }
}
