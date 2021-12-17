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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bitplan.storage.jaxb.XMLHelper;


/**
 * handle sources
 * 
 * @author wf
 *
 */
public class SourceExtractor {
  protected static Logger LOGGER = Logger.getLogger("org.sidif.wiki");

  protected boolean debug = false;
  Reference reference;
  private static final String SOURCETAG_BEGIN = "<source";
  private static final String SOURCE_ATTRIBUTE_PATTERN = "[\\s](id|lang|cache|line)(\\s?=?\'([^\']*)\')?";
  private static final String SOURCETAG_PATTERN = SOURCETAG_BEGIN + "("
      + SOURCE_ATTRIBUTE_PATTERN + ")*" + "[\\s]?>";
  static final Pattern SOURCETAG_REGEX = Pattern.compile(SOURCETAG_PATTERN);

  private static final String SOURCETAG_END = "</source>";

  /**
   * construct a SourceExtractor for the given reference
   * 
   * @param reference
   * @throws Exception
   */
  public SourceExtractor(Reference reference) throws Exception {
    this.reference = reference;
  }

  /**
   * extract a Source from the given SourceTag by extracting the attribute
   * information
   * 
   * @param sourceTag - the sourceTag information
   * @return the Source
   * @throws Exception
   */
  public static Source extractSourceTag(String sourceTag) throws Exception {
    sourceTag=sourceTag.replace("line","");
    String xml = "<?xml version=\"1.0\"?>" + sourceTag + "</source>";
    XMLHelper xh = new XMLHelper();
    Source source = (Source) xh.fromXMLString(xml, Source.class);
    return source;
  }

  /**
   * debug the groups of the given matcher
   * 
   * @param matcher
   *          - the matcher do debug
   */
  public void debugGroups(Matcher matcher) {
    int groups = matcher.groupCount();
    for (int i = 0; i < groups; i++) {
      LOGGER.log(Level.INFO, "group " + i + ":" + matcher.group(i));
    }
  }

  /**
   * get all Source Tag Contents for
   * 
   * @return a a Map with all source tags in this page
   * @throws Exception
   */
  public Map<String, Source> extractSourceTagContent() throws Exception {
    if (reference == null) {
      throw new IllegalStateException(
          "extractSourceTagContent called with null reference");
    }
    if (reference.content == null) {
      String hint="?";
      switch (reference.referenceType) {
        case PAGE:
          hint=reference.pageTitle;
        break;
        default:
      }
      throw new IllegalStateException(
          "extractSourceTagContent called for null content ("+hint+") with referenceType "
              + reference.referenceType.toString());
    }

    final Map<String, Source> sources = new LinkedHashMap<String, Source>();
    int sourcePos;
    String lPageContent = reference.content;
    // loop over the content and look for SOURCETAG_BEGIN tags
    while ((sourcePos = lPageContent.indexOf(SOURCETAG_BEGIN)) >= 0) {
      lPageContent = lPageContent.substring(sourcePos);
      final Matcher matcher = SOURCETAG_REGEX.matcher(lPageContent);
      if (!matcher.find()) {
        break;
      } else {
        int matchend = matcher.end();
        int matchstart= matcher.start();
        // int pageContentLen=lPageContent.length();
        // int matchlen = matchend-matchstart;
        String sourceTag = lPageContent.substring(matchstart, matchend);
        Source source = extractSourceTag(sourceTag);
        if (source == null) {
          break;
        } else {
          sources.put(source.id, source);
        } // if
        int sourceEndPos = lPageContent.indexOf(SOURCETAG_END);
        if (sourceEndPos > 0 && sourceEndPos>=matchend) {
          source.source = lPageContent.substring(matchend, sourceEndPos);
          // remove leading and trailing whitespace
          source.source = source.source.trim();
          source.pageTitle = reference.pageTitle;
        } else {
          // this is invalid the source tag is not closed
          String msg="source tag somehow garbled. sourceEnd versus matchEnd mismatch with matchend=" + matcher.end()
              + " sourceEndPos=" + sourceEndPos + " for id " + source.id;
          LOGGER.log(Level.SEVERE,msg );
          throw new Exception(msg);
          // return null;
        }
        lPageContent = lPageContent.substring(sourceEndPos
            + SOURCETAG_END.length());
      } // if matcher find
    } // while
    return sources;
  }

}
