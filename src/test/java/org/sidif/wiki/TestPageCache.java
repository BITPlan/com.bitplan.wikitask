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
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.sidif.triple.Triple;
import org.sidif.triple.TripleQuery;
import org.sidif.triple.TripleStore;
import org.sidif.wiki.PageCache.SectionList;

import com.bitplan.mediawiki.japi.api.Ii;
import com.bitplan.mediawiki.japi.api.Page;
import com.bitplan.mediawiki.japi.api.Rev;

/**
 * test the PageCache implementation
 * 
 * @author wf
 *
 */
public class TestPageCache extends BaseTest {

  static PageCache pageCache = null;
  private static File cacheRoot;
  static SSLWiki wiki;
  static String testWikiId = "wiki";

  /**
   * get the test wiki
   * 
   * @return
   * @throws Exception
   */
  public static SSLWiki getWiki() throws Exception {
    if (wiki == null) {
      wiki = SSLWiki.ofId(testWikiId);
    }
    return wiki;
  }

  @Test
  public void testgetPageTitleChunks() {
    List<String> pageTitles = new ArrayList<String>();
    String abc = "abcdefghijklmnopqrstuvwyzABCDEFGHIJKLMNOPQRSTUVXYZ0123456789";
    Random random = new Random();
    // number of chunks we want to produce
    int chunks = random.nextInt(20) + 3;
    int sum = 0;
    while (sum < chunks * PageCache.LIMIT_REQUEST_LINE) {
      String pageTitle = abc.substring(0, random.nextInt(abc.length() - 5) + 5);
      pageTitles.add(pageTitle);
      sum += pageTitle.length() + PageCache.PAGE_TITLE_EXTRA;
    }
    List<List<String>> chunkList = PageCache.getPageTitleChunks(pageTitles);
    // System.out.println(""+chunks+" chunks "+pageTitles.size()+" pages split
    // to "+chunkList.size()+" chunks");
    assertEquals(chunks, chunkList.size());
  }

  @Test
  public void testFileSystemAdapt() {
    String badFilename = "/?%*:|<>. \\äöüÄÖÜßéô✓";
    String goodFilename = PageCache.filesystemadapt(badFilename);
    String expected = "%2F%3F%25*%3A%7C%3C%3E.+%5C%C3%A4%C3%B6%C3%BC%C3%84%C3%96%C3%9C%C3%9F%C3%A9%C3%B4%E2%9C%93";
    if (debug)
      System.out.println(goodFilename);
    assertEquals(expected, goodFilename);
  }

  /**
   * get the PageCache
   * 
   * @throws Exception
   */
  public static PageCache getPageCache() throws Exception {
    if (pageCache == null) {
      cacheRoot = new File(WikiTask.getWikiTaskHome() + "pagecache");
      pageCache = new PageCache(cacheRoot, getWiki());
      PageCache.debug = true;
    }
    return pageCache;
  }

  /**
   * test the Cache handling with the transferPages
   * 
   * @throws Exception
   */
  @Test
  public void testCache() throws Exception {
    if (this.isTravis())
      return;
    String localSettingsFilename = WikiManager.getLocalSettings("capri");
    TripleStore tripleStore = SMW_Triples.fromWiki(localSettingsFilename);
    // debug = true;
    if (debug)
      System.out.println("triple store size is " + tripleStore.size());
    TripleQuery transferPagesQuery = tripleStore.query().query(null,
        "Property:TransferPage_page", null);
    if (debug)
      System.out
          .println("transferPages: " + transferPagesQuery.getTriples().size());

    List<String> titleList = new ArrayList<String>();
    for (Triple transferPageTriple : transferPagesQuery.getTriples()) {
      titleList.add(transferPageTriple.getObject().toString());
    }
    PageCache lPageCache = getPageCache();
    assertTrue("cacheRoot " + cacheRoot.getAbsolutePath() + " should exist",
        cacheRoot.isDirectory());

    Date start = new Date();
    wiki = getWiki();
    if (debug)
      System.out.println(wiki.getIsoTimeStamp());
    Collection<Page> pages = lPageCache.getPages(titleList);

    if (debug) {
      lPageCache.showDebug(pages);
    }
    Date stop1 = new Date();
    if (debug)
      System.out.println(wiki.getIsoTimeStamp());
    pages = lPageCache.getPages(titleList);

    if (debug) {
      lPageCache.showDebug(pages);
    }
    Date stop2 = new Date();
    long time1 = stop1.getTime() - start.getTime();
    long time2 = stop2.getTime() - stop1.getTime();
    if (debug) {
      System.out.println(wiki.getIsoTimeStamp());
      System.out.println(time1);
      System.out.println(time2);
    }
    // time 1 should be at least 100 times higher than time 2
    assertTrue(time1 > 100 * time2);
  }

  /**
   * test caching sections
   * 
   * @throws Exception
   */
  @Test
  public void testCacheRefresh() throws Exception {
    if (isTravis()) return;
    wiki = getWiki();
    PageCache lpageCache = getPageCache();
    String pageTitles[] = { "PageCacheTest" };
    List<String> pageTitleList = Arrays.asList(pageTitles);
    List<Page> pages = lpageCache.getPages(pageTitleList);
    assertEquals(1, pages.size());
    Page page = pages.get(0);
    Rev rev = PageCache.getPageRevision(page);
    if (rev == null) {
      fail("could not get revision for page " + page.getTitle());
    }
    Integer revid = rev.getRevid();
    for (String pageTitle : pageTitles) {
      lpageCache.login();
      wiki.edit(pageTitle, wiki.getIsoTimeStamp(),
          "modified by testCacheRefesh at" + wiki.getIsoTimeStamp());
    }
    lpageCache.check(false);
    pages = lpageCache.getPages(pageTitleList);
    Integer newrevid = pages.get(0).getRevisions().get(0).getRevid();
    assertTrue("" + newrevid + " should be > " + revid, newrevid > revid);
  }

  /**
   * test caching sections
   * 
   * @throws Exception
   */
  @Test
  public void testSectionCache() throws Exception {
    PageCache lpageCache = getPageCache();
    String topicGeneratorPage = "TopicGenerator";
    lpageCache.getPageContent(topicGeneratorPage);
    SectionList sectionList = lpageCache.getSections(topicGeneratorPage);
    if (debug) {
      System.out.println(sectionList.sectionlist.size());
    }
    assertEquals(2, sectionList.sectionlist.size());
  }

  @Test
  public void testGetImageInfo() throws Exception {
    Ii imageInfo = getWiki().getImageInfo("File:Registry.png");
    if (debug)
      System.out.println(imageInfo.getUrl());
    assertEquals("http://wiki.bitplan.com/images/wiki/d/d2/Registry.png",
        imageInfo.getUrl());
  }
}
