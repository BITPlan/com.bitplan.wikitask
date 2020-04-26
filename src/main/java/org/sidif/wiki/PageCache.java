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
import java.io.FileFilter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
import org.glassfish.grizzly.http.util.URLDecoder;

import com.bitplan.mediawiki.japi.PageInfo;
import com.bitplan.mediawiki.japi.SiteInfo;
import com.bitplan.mediawiki.japi.SiteInfoImpl;
import com.bitplan.mediawiki.japi.api.General;
import com.bitplan.mediawiki.japi.api.Ns;
import com.bitplan.mediawiki.japi.api.Page;
import com.bitplan.mediawiki.japi.api.Rev;
import com.bitplan.mediawiki.japi.api.S;
import com.bitplan.mediawiki.japi.jaxb.JaxbFactory;

/**
 * the page cache
 * 
 * @author wf
 *
 */
public class PageCache {
  public static boolean debug = true;
  protected static Logger LOGGER = Logger.getLogger("org.sidif.wiki");

  private SSLWiki wiki;
  private File cacheRoot;
  private JaxbFactory<Page> pageJaxbFactory;
  private SiteInfo siteinfo;

  // the memory cache of Pages by canonical page title or canonical anchor
  Map<String, Page> cachedPages = new TreeMap<String, Page>();
  Map<String, SectionList> cachedSections = new TreeMap<String, SectionList>();
  Map<String, Map<String, Section>> sectionByAnchor = new TreeMap<String, Map<String, Section>>();

  /**
   * @return the cachedPages
   */
  public Map<String, Page> getCachedPages() {
    return cachedPages;
  }

  /**
   * construct a page cache for the given cache root and wiki
   * 
   * @param cacheRoot
   * @param wiki
   */
  public PageCache(File cacheRoot, SSLWiki wiki) {
    this.wiki = wiki;
    this.cacheRoot = cacheRoot;
    this.pageJaxbFactory = new JaxbFactory<Page>(Page.class);
  }

  @XmlRootElement(name = "namespacelist")
  public static class NameSpaceList {
    static final transient JaxbFactory<NameSpaceList> jaxbFactory = new JaxbFactory<NameSpaceList>(
        NameSpaceList.class);
    List<Ns> namespacelist = new ArrayList<Ns>();

    /**
     * @return the namespacelist
     */
    public List<Ns> getNamespacelist() {
      return namespacelist;
    }

    /**
     * @param namespacelist
     *          the namespacelist to set
     */
    public void setNamespacelist(List<Ns> namespacelist) {
      this.namespacelist = namespacelist;
    }

    // make JAXB happy with a zero argument constructor
    public NameSpaceList() {
    };

    public NameSpaceList(Collection<Ns> values) {
      for (Ns ns : values) {
        namespacelist.add(ns);
      }
    }

    /**
     * restore a namespace list from the given namespace list file
     * 
     * @param namespaceListFile
     * @return
     * @throws Exception
     */
    static NameSpaceList restore(File namespaceListFile) throws Exception {
      String xml = FileUtils.readFileToString(namespaceListFile);
      NameSpaceList result = jaxbFactory.fromXML(xml);
      return result;
    }

    /**
     * store me to the given file
     * 
     * @param namespaceListFile
     * @throws Exception
     */
    public void store(File namespaceListFile) throws Exception {
      String xml = jaxbFactory.asXML(this);
      FileUtils.write(namespaceListFile, xml);
    }
  }

  @XmlRootElement(name = "sectionlist")
  public static class SectionList {
    static final transient JaxbFactory<SectionList> jaxbFactory = new JaxbFactory<SectionList>(
        SectionList.class);
    List<S> sectionlist = new ArrayList<S>();
    private String pageTitle;
    private Integer revid;
    private String canonicalTitle;

    /**
     * @return the pageTitle
     */
    public String getPageTitle() {
      return pageTitle;
    }

    /**
     * @param pageTitle
     *          the pageTitle to set
     */
    public void setPageTitle(String pageTitle) {
      this.pageTitle = pageTitle;
    }

    /**
     * @return the revid
     */
    public Integer getRevid() {
      return revid;
    }

    /**
     * @param revid
     *          the revid to set
     */
    public void setRevid(Integer revid) {
      this.revid = revid;
    }

    /**
     * @return the canonicalTitle
     */
    public String getCanonicalTitle() {
      return canonicalTitle;
    }

    /**
     * @param canonicalTitle
     *          the canonicalTitle to set
     */
    public void setCanonicalTitle(String canonicalTitle) {
      this.canonicalTitle = canonicalTitle;
    }

    /**
     * @return the sectionlist
     */
    public List<S> getSectionlist() {
      return sectionlist;
    }

    /**
     * @param Sectionlist
     *          the sectionlist to set
     */
    public void setSectionlist(List<S> sectionlist) {
      this.sectionlist = sectionlist;
    }

    // make JAXB happy with a zero argument coStructor
    public SectionList() {
    };

    /**
     * create a Section List from the given values
     * 
     * @param values
     */
    public SectionList(PageInfo pageInfo, Page p, Collection<S> values) {
      this.canonicalTitle = pageInfo.getCanonicalPageTitle();
      this.pageTitle = p.getTitle();
      Rev rev = PageCache.getPageRevision(p);
      revid = rev.getRevid();
      for (S s : values) {
        sectionlist.add(s);
      }
    }

    /**
     * restore a section list from the given section list file
     * 
     * @param namespaceListFile
     * @return
     * @throws Exception
     */
    static SectionList restore(File sectionListFile) throws Exception {
      String xml = FileUtils.readFileToString(sectionListFile);
      SectionList result = jaxbFactory.fromXML(xml);
      return result;
    }

    /**
     * store me to the given file
     * 
     * @param sectionListFile
     * @throws Exception
     */
    public void store(File sectionListFile) throws Exception {
      String xml = jaxbFactory.asXML(this);
      FileUtils.write(sectionListFile, xml);
    }
  }

  /**
   * login if necessary
   * 
   * @throws Exception
   */
  void login() throws Exception {
    if (!wiki.isLoggedIn()) {
      wiki.login();
    }
  }

  /**
   * get the siteinfo for the wiki
   * 
   * @return
   * @throws Exception
   */
  public SiteInfo getSiteInfo() throws Exception {
    // is the siteinfo available
    if (siteinfo == null) {
      // first try to get it from the cache
      File generalXmlFile = new File(this.getWikiCacheDirectory(),
          "general.xml");
      File namespaceListFile = new File(this.getWikiCacheDirectory(),
          "namespacelist.xml");
      JaxbFactory<General> generalJaxbFactory = new JaxbFactory<General>(
          General.class);

      // are the Cache XML files readable?
      if (generalXmlFile.canRead() && namespaceListFile.canRead()) {
        // get the Java concepts from XML using JaxB
        String generalXml = FileUtils.readFileToString(generalXmlFile);
        General general = generalJaxbFactory.fromXML(generalXml);
        NameSpaceList nslist = NameSpaceList.restore(namespaceListFile);
        // now reassemble the siteinfo from theses
        siteinfo = new SiteInfoImpl(general, nslist.namespacelist);
      } else {
        // if things are not in the cache we have to ask the wiki ...
        login();
        siteinfo = wiki.getSiteInfo();
        // get the general siteinfo and the namespacelist and cache them in XML
        // files using JAXB
        General general = siteinfo.getGeneral();
        FileUtils.write(generalXmlFile, generalJaxbFactory.asXML(general));
        NameSpaceList nslist = new NameSpaceList(
            siteinfo.getNamespaces().values());
        nslist.store(namespaceListFile);
      }
    }
    return siteinfo;
  }

  /**
   * get the canonical page Title for the given pageTitle
   * 
   * @param pageTitle
   * @return a canonical version of the pageTitle
   * @throws Exception
   */
  public PageInfo canonicalTitle(String pageTitle) throws Exception {
    PageInfo pageInfo = new PageInfo(pageTitle, getSiteInfo());
    // System.out.println("PageTitle=" + pageTitle + "->" + canonical);
    return pageInfo;
  }

  /**
   * get the sections for the given page
   * 
   * @param pageTitle
   * @return the sections
   * @throws Exception
   */
  public SectionList getSections(String pageTitle) throws Exception {
    PageInfo pageInfo = canonicalTitle(pageTitle);
    SectionList result = getSections(pageInfo);
    return result;
  }

  /**
   * getSections
   * 
   * @param pageInfo
   * @return the Section List
   * @throws Exception
   */
  private SectionList getSections(PageInfo pageInfo) throws Exception {
    // get the Page for the given Page Info
    Page page = this.getPage(pageInfo);
    Rev rev = getPageRevision(page);
    LOGGER.log(Level.INFO,
        "using " + rev.getRevid() + " timestamp " + rev.getTimestamp());
    // first check memory cache
    SectionList sectionList = cachedSections
        .get(pageInfo.getCanonicalPageTitle());
    // if their was no hit or the wrong revision id
    File sectionCacheFile = null;
    if (sectionList == null || sectionList.revid != rev.getRevid()) {
      String exts[] = { "sections" };
      boolean fromCache = false;
      if (rev != null) {
        Map<String, File> cacheFiles = getPageCacheFiles(page, exts, rev);
        sectionCacheFile = cacheFiles.get(exts[0]);
        // are the sections available in the file cache?
        if (sectionCacheFile.canRead()) {
          fromCache = true;
        }
      }
      if (fromCache) {
        // get them
        sectionList = SectionList.restore(sectionCacheFile);
      } else {
        // unfortunately we have to login to the wiki to get the stuff
        login();
        List<S> pageSections = wiki
            .getSections(pageInfo.getCanonicalPageTitle());
        sectionList = new SectionList(pageInfo, page, pageSections);
        sectionList.store(sectionCacheFile);
      }
      // now keep stuff in memory to not have to ask again
      cachedSections.put(pageInfo.getCanonicalPageTitle(), sectionList);
    }
    return sectionList;
  }

  /**
   * get a new Section by anchor map for the given pageTitle but don't fetch any
   * content yet
   * 
   * @return the section by Anchor Map
   * @throws Exception
   */
  public Map<String, Section> getSectionByAnchorMap(String pageTitle)
      throws Exception {
    PageInfo pageInfo = this.canonicalTitle(pageTitle);
    Map<String, Section> result = this.getSectionByAnchorMap(pageInfo);
    return result;
  }

  /**
   * get a new Section by anchor map for the given pageTitle but don't fetch any
   * content yet
   * 
   * @return the section by Anchor Map
   */
  public Map<String, Section> getSectionByAnchorMap(PageInfo pageInfo) {
    Map<String, Section> result = new LinkedHashMap<String, Section>();
    if (debug) {
      // wiki.setDebug(true);
    }
    // get all sections of the given page
    try {
      LOGGER.log(Level.INFO, "getting sections for " + pageInfo.dual());
      SectionList sectionList = getSections(pageInfo.getCanonicalPageTitle());
      for (S s : sectionList.sectionlist) {
        Section section = new Section(s, pageInfo.getCanonicalPageTitle(),
            sectionList.revid);
        LOGGER.log(Level.INFO, "\tsection " + section.anchor);
        result.put(Section.normalizeAnchor(section.anchor), section);
      } // for
    } catch (Exception ex) {
      LOGGER.log(Level.SEVERE, "pagetitle for section search " + pageInfo.dual()
          + " probably invalid " + ex.getMessage());
    }

    return result;
  }

  /**
   * get the given section by anchor
   * 
   * @param pageTitle
   * @param normalizedAnchor
   * @return
   * @throws Exception
   */
  public Section getSectionByAnchor(String pageTitle, String normalizedAnchor)
      throws Exception {
    PageInfo pageInfo = canonicalTitle(pageTitle);
    Section result = this.getSectionByAnchor(pageInfo, normalizedAnchor);
    return result;
  }

  /**
   * get the given section by anchor
   * 
   * @param pageTitle
   * @param normalizedAnchor
   * @return
   * @throws Exception
   */
  public Section getSectionByAnchor(PageInfo pageInfo, String normalizedAnchor)
      throws Exception {
    Section section = null;
    // first look for the sectionMap in Memory
    String canonicalTitle = pageInfo.getCanonicalPageTitle();
    Map<String, Section> sectionsByAnchorForPage = this.sectionByAnchor
        .get(canonicalTitle);
    if (sectionsByAnchorForPage != null) {
      LOGGER.log(Level.INFO, "getting Section " + canonicalTitle + "#"
          + normalizedAnchor + " from memory");
      section = sectionsByAnchorForPage.get(normalizedAnchor);
    } else {
      sectionsByAnchorForPage = this
          .getSectionByAnchorMap(pageInfo.getCanonicalPageTitle());
      this.sectionByAnchor.put(pageInfo.getCanonicalPageTitle(),
          sectionsByAnchorForPage);
      this.getSectionByAnchor(pageInfo.getCanonicalPageTitle(),
          normalizedAnchor);
      section = sectionsByAnchorForPage.get(normalizedAnchor);
    }
    if (section == null) {
      String msg = "section " + normalizedAnchor + " for pageTitle "
          + pageInfo.dual()
          + " not found in memory cache, nor disk cache and could also not be retrieved from server ";
      LOGGER.log(Level.WARNING, msg);
    }
    if (section != null && section.sectionText == null) {
      // FIXME - this is the culprit - we try to re get the section text here
      // over and over
      String msg = "getting section " + pageInfo.dual() + "#"
          + normalizedAnchor;
      LOGGER.log(Level.INFO, msg);
      section.sectionText = getSectionText(pageInfo, section);
    }
    return section;
  }

  /**
   * get the SectionText for the given pageTitle and sectionIndex
   * 
   * @param pageTitle
   * @param sectio
   * @return
   * @throws Exception
   */
  public String getSectionText(String pageTitle, Section section)
      throws Exception {
    PageInfo pageInfo = canonicalTitle(pageTitle);
    String result = getSectionText(pageInfo, section);
    return result;
  }

  /**
   * get the SectionText for the given pageInfo and sectionIndex
   * 
   * @param pageInfo
   * @param section
   * @return
   * @throws Exception
   */
  private String getSectionText(PageInfo pageInfo, Section section)
      throws Exception {
    String sectionContent = wiki
        .getSectionText(pageInfo.getCanonicalPageTitle(), section.sectionIndex);
    return sectionContent;
  }

  /**
   * get the Pages for the given title List
   * 
   * @param titleList
   * @return
   * @throws Exception
   */
  public List<Page> getPages(List<String> titleList) throws Exception {
    List<PageInfo> canonicalTitles = new ArrayList<PageInfo>();
    for (String title : titleList) {
      PageInfo pageInfo = canonicalTitle(title);
      canonicalTitles.add(pageInfo);
    }
    List<Page> result = getPagesFromPageInfoList(canonicalTitles);
    return result;
  }

  /**
   * get pages from the given pageInfo list
   * 
   * @param pageInfoList
   * @return
   * @throws Exception
   */
  private List<Page> getPagesFromPageInfoList(List<PageInfo> pageInfoList)
      throws Exception {
    checkCachedPages(pageInfoList);
    List<String> neededTitles = new ArrayList<String>();
    for (PageInfo pageInfo : pageInfoList) {
      if (!cachedPages.containsKey(pageInfo.getCanonicalPageTitle())) {
        neededTitles.add(pageInfo.getCanonicalPageTitle());
      }
    }
    // neededTitles are canonical
    if (neededTitles.size() > 0) {
      LOGGER.log(Level.INFO, "getting " + neededTitles.size() + " pages from "
          + wiki.getSiteurl() + wiki.getScriptPath());
      login();
      // wiki.setDebug(true);
      List<Page> pages = wiki.getPages(neededTitles);
      for (Page page : pages) {
        PageInfo pageInfo = cache(page);

        LOGGER.log(Level.INFO, "caching page " + pageInfo.dual());
        cachedPages.put(pageInfo.getCanonicalPageTitle(), page);
      }
      // check the caching
      for (String pageTitle : neededTitles) {
        PageInfo pageInfo = canonicalTitle(pageTitle);
        if (!cachedPages.containsKey(pageInfo.getCanonicalPageTitle())) {
          LOGGER.log(Level.WARNING, "page " + pageInfo.dual() + " not cached");
        }
      }
    }
    List<Page> result = new ArrayList<Page>();
    for (PageInfo pageInfo : pageInfoList) {
      Page page = this.cachedPages.get(pageInfo.getCanonicalPageTitle());
      if (page != null) {
        result.add(page);
      } else {
        LOGGER.log(Level.SEVERE,
            "pageTitle '" + pageInfo.dual() + "' missing in memory cache");
      }
    }
    return result;
  }

  /**
   * get the lastModified File from the given directory with the given extension
   * 
   * @param dir
   * @param ext
   * @return
   */
  public static File lastFileModified(File fl, final String ext) {
    File[] files = fl.listFiles(new FileFilter() {
      public boolean accept(File file) {
        return file.isFile() & file.getName().endsWith(ext);
      }
    });
    long lastMod = Long.MIN_VALUE;
    File choice = null;
    if (files != null) {
      for (File file : files) {
        if (file.lastModified() > lastMod) {
          choice = file;
          lastMod = file.lastModified();
        }
      }
    }
    return choice;
  }

  /**
   * recache the given pageFrom the file cache
   * 
   * @param pageTitle
   * @return
   * @throws Exception
   */
  public Page recachePageFromFileCache(PageInfo pageInfo) throws Exception {
    // get the path / directory where the revisions of this pageTitle are
    // kept
    File pageDirectory = getPageDirectory(pageInfo);
    Page page = null;
    boolean found = false;
    // check that the directory exists
    if (pageDirectory.exists()) {
      // look for the most recent xml entry
      File xmlFile = lastFileModified(pageDirectory, ".xml");
      // did we find one?
      if (xmlFile != null) {
        // read the xml string
        String xml = FileUtils.readFileToString(xmlFile);
        // convert it back to a page
        page = this.pageJaxbFactory.fromXML(xml);
        // this is is "good" page
        cachedPages.put(pageInfo.getCanonicalPageTitle(), page);
        found = true;
      }
    } // if
    if (debug && !found) {
      LOGGER.log(Level.INFO,
          "page " + pageInfo.dual() + " not found in file based cache");
    }
    return page;
  }

  /**
   * get cached pages
   * 
   * @param pageInfoList
   *          a list of pageTitles
   * @return a map that delivers pages by their canonical page title
   * @throws Exception
   */
  private void checkCachedPages(List<PageInfo> pageInfoList) throws Exception {
    // loop over all pages
    for (PageInfo pageInfo : pageInfoList) {
      // always work on the canonical version of a title
      String pageTitle = pageInfo.getCanonicalPageTitle();
      if (!this.cachedPages.containsKey(pageTitle)) {
        this.recachePageFromFileCache(pageInfo);
      }
    } // for
  }

  /**
   * the the wiki Cache Directory
   * 
   * @return the wiki Cache Directory
   */
  private File getWikiCacheDirectory() {
    File wikiCacheDirectory = new File(this.cacheRoot, this.wiki.getWikiid());
    if (!wikiCacheDirectory.isDirectory()) {
      wikiCacheDirectory.mkdirs();
    }
    return wikiCacheDirectory;
  }

  /**
   * get the pageDirectory for the given pageTitle
   * 
   * @param pageTitle
   * @return
   * @throws Exception
   */
  private File getPageDirectory(PageInfo pageInfo) throws Exception {
    String canonicalTitle = pageInfo.getCanonicalPageTitle();
    File pageDirectory = new File(getWikiCacheDirectory(),
        filesystemadapt(canonicalTitle));
    return pageDirectory;
  }

  /**
   * get the directory for the given page
   * 
   * @param page
   * @return - the directory
   * @throws Exception
   */
  private File getPageDirectory(Page page) throws Exception {
    PageInfo pageInfo = canonicalTitle(page.getTitle());
    File pageDirectory = getPageDirectory(pageInfo);
    if (!pageDirectory.isDirectory()) {
      pageDirectory.mkdirs();
    }
    return pageDirectory;
  }

  /**
   * get the cached Files for the given extensions
   * 
   * @param page
   * @param exts
   *          - the extensions
   * @param rev
   * @return
   * @throws Exception
   */
  private Map<String, File> getPageCacheFiles(Page page, String[] exts, Rev rev)
      throws Exception {
    File pageDirectory = this.getPageDirectory(page);
    Map<String, File> result = new LinkedHashMap<String, File>();
    for (String ext : exts) {
      File revisionFile = new File(pageDirectory, rev.getRevid() + "." + ext);
      result.put(ext, revisionFile);
    }
    return result;
  }

  /**
   * cache the given page
   * 
   * @param page
   * @throws Exception
   * @return the canonical title of the page
   */
  private PageInfo cache(Page page) throws Exception {
    String exts[] = { "wiki", "json", "xml" };
    Rev rev = getPageRevision(page);
    if (rev != null) {
      Map<String, File> cacheFiles = getPageCacheFiles(page, exts, rev);
      String json = this.pageJaxbFactory.asJson(page);
      FileUtils.write(cacheFiles.get("json"), json);
      String xml = this.pageJaxbFactory.asXML(page);
      FileUtils.write(cacheFiles.get("xml"), xml);
      FileUtils.write(cacheFiles.get("wiki"), rev.getValue());
    }
    PageInfo pageInfo = canonicalTitle(page.getTitle());
    return pageInfo;
  }

  /**
   * adapt the pageTitle to be file system compatible
   * 
   * @param pageTitle
   *          - the page title to get a file name for
   * @return - a filename that can be used for the pagetitle
   */
  public static String filesystemadapt(String pageTitle) {
    @SuppressWarnings("deprecation")
    // some reserved characters and words
    // https://en.wikipedia.org/wiki/Filename#Reserved_characters_and_words
    // just urlencode the filename for simplicity
    String result = URLEncoder.encode(pageTitle);
    return result;
  }

  /**
   * get the page for the given title
   * 
   * @param pageTitle
   * @return
   * @throws Exception
   */
  public Page getPage(String pageTitle) throws Exception {
    PageInfo pageInfo = canonicalTitle(pageTitle);
    Page result = getPage(pageInfo);
    return result;
  }

  /**
   * get the page for the given pageInfo
   * 
   * @param pageInfo
   * @return
   * @throws Exception
   */
  private Page getPage(PageInfo pageInfo) throws Exception {
    List<String> pageTitles = new ArrayList<String>();
    pageTitles.add(pageInfo.getCanonicalPageTitle());
    Collection<Page> pages = this.getPages(pageTitles);
    if (pages.size() != 1) {
      throw new Exception("found " + pages.size() + " pages for pageTitle '"
          + pageInfo.dual() + "' but expected 1");
    }
    Page page = pages.iterator().next();
    return page;
  }

  /**
   * get the content of the given page
   * 
   * @param page
   * @return the content of the page
   */
  public static String getContent(Page page) {
    Rev rev = getPageRevision(page);
    String result = rev.getValue();
    return result;
  }

  /**
   * get the pageRevision of the given page
   * 
   * @param page
   * @return
   */
  public static Rev getPageRevision(Page page) {
    List<Rev> revs = page.getRevisions();
    if (revs == null || revs.size() == 0) {
      LOGGER.log(Level.SEVERE,
          "page revision null or empty for " + page.getTitle());
      return null;
    }
    return revs.get(0);
  }

  /**
   * get the Content of a single page
   * 
   * @param pageTitle
   * @return
   * @throws Exception
   */
  public String getPageContent(String pageTitle) throws Exception {
    Page page = this.getPage(pageTitle);
    Rev rev = getPageRevision(page);
    if (rev != null)
      return rev.getValue();
    else
      return "";
  }

  /**
   * dump my content
   */
  public void showDebug() {
    this.showDebug(this.cachedPages.values());
  }

  /**
   * show a debug dump of the page cache
   */
  public void showDebug(Collection<Page> pages) {
    long total = 0;
    for (Page page : pages) {
      Rev rev = getPageRevision(page);
      if (rev != null) {
        long len = rev.getValue().length();
        System.out.println(page.getTitle() + " rev: " + rev.getRevid() + " ts:"
            + rev.getTimestamp() + " len:" + len);
        total += len;
      }
    }
    System.out.println("total: " + total);
  }

  /**
   * check the Pa
   * 
   * @throws Exception
   */
  public void check(boolean memoryCache) throws Exception {
    // these pages are canonical by definition
    List<String> pageTitles = new ArrayList<String>();
    // shall we check the memory cache?
    if (memoryCache) {
      pageTitles = new ArrayList<String>(this.cachedPages.keySet());
    } else {
      // get all the available pages
      File cacheDirectory = getWikiCacheDirectory();
      for (File pageFile : cacheDirectory.listFiles()) {
        if (pageFile.isDirectory()) {
          String pageTitle = URLDecoder.decode(pageFile.getName());
          pageTitles.add(pageTitle);
        }
      }
    }
    updatePageStatus(pageTitles, true);
  }

  /**
   * get the page Status for the given pageTitles
   * 
   * @param pageTitles
   * @return a map of pageTitles with the given revision
   * @throws Exception
   */
  public Map<String, Page> updatePageStatus(List<String> pageTitles,
      boolean expectNewPages) throws Exception {
    Map<String, Page> result = new TreeMap<String, Page>();
    if (debug) {
      LOGGER.log(Level.INFO,
          "check for " + pageTitles.size() + " cached pages");
    }
    List<PageInfo> pageInfos = new ArrayList<PageInfo>();
    for (String pageTitle : pageTitles) {
      pageInfos.add(canonicalTitle(pageTitle));
    }
    if (pageTitles.size() > 0) {
      String rvprop = "ids|timestamp";
      login();
      // we have to loop over the pageTitles in chunks that can be handled
      // without a "Request-URI Too Long message ..."
      List<List<String>> pageTitleChunks = getPageTitleChunks(pageTitles);
      for (List<String> pageTitlesChunk : pageTitleChunks) {
        List<Page> pages = wiki.getPages(pageTitlesChunk, rvprop);
        List<String> neededRefreshTitles = new ArrayList<String>();
        for (Page page : pages) {
          PageInfo pageInfo = canonicalTitle(page.getTitle());
          Page cachedPage = this.cachedPages
              .get(pageInfo.getCanonicalPageTitle());
          if (cachedPage == null) {
            cachedPage = this.recachePageFromFileCache(pageInfo);
          }
          if (cachedPage != null) {
            Rev rev = getPageRevision(page);
            Rev cachedRev = getPageRevision(cachedPage);
            if ((rev == null || cachedRev == null)
                || (rev.getRevid() > cachedRev.getRevid())) {
              neededRefreshTitles.add(page.getTitle());
            }
          } else {
            if (expectNewPages) {
              neededRefreshTitles.add(page.getTitle());
            } else {
              LOGGER.log(Level.SEVERE,
                  "check couldn't find " + pageInfo.dual() + " in cache");
            }
          }
        }
        if (neededRefreshTitles.size() > 0) {
          LOGGER.log(Level.INFO,
              "refreshing " + neededRefreshTitles.size() + " pages");
          List<Page> refreshedPages = wiki.getPages(neededRefreshTitles);
          for (Page page : refreshedPages) {
            PageInfo pageInfo = canonicalTitle(page.getTitle());
            this.cache(page);
            cachedPages.put(pageInfo.getCanonicalPageTitle(), page);
          }
        }
        for (PageInfo pageInfo : pageInfos) {
          if (cachedPages.containsKey(pageInfo.getCanonicalPageTitle())) {
            Page page = cachedPages.get(pageInfo.getCanonicalPageTitle());
            result.put(pageInfo.getCanonicalPageTitle(), page);
          } else {
            result.put(pageInfo.getCanonicalPageTitle(), null);
          }
        }
      }
    }
    return result;
  }

  // https://stackoverflow.com/a/2891598/1497139
  public static int LIMIT_REQUEST_LINE=8190;
  public static int PAGE_TITLE_EXTRA=8; // Heuristic - 5 did not work 
  
  /**
   * split the list of pageTitles in managable chunks
   * @param pageTitles - the list of page titles
   * @return a list of chunks
   */
  public static List<List<String>> getPageTitleChunks(List<String> pageTitles) {
    List<List<String>> chunkList=new ArrayList<List<String>>();
    int sum=0;
    List<String> chunkTitles=new ArrayList<String>();
    chunkList.add(chunkTitles);
    for (String pageTitle:pageTitles) {
      sum+=pageTitle.length()+PAGE_TITLE_EXTRA; // security factor
      if (sum>=LIMIT_REQUEST_LINE) {
        sum=0;
        chunkTitles=new ArrayList<String>();
        chunkList.add(chunkTitles);
      }
      chunkTitles.add(pageTitle);
    }
    return chunkList;
  }

  /**
   * remove the cache Entry for the given page
   * 
   * @param pageTitle
   * @throws Exception
   */
  public void dropCacheEntry(String pageTitle) throws Exception {
    PageInfo pageInfo = canonicalTitle(pageTitle);
    if (this.cachedPages.containsKey(pageInfo.getCanonicalPageTitle())) {
      this.cachedPages.remove(pageInfo.getCanonicalPageTitle());
    }
    if (this.cachedSections.containsKey(pageInfo.getCanonicalPageTitle())) {
      this.cachedSections.remove(pageInfo.getCanonicalPageTitle());
    }
    File pageDirectory = this.getPageDirectory(pageInfo);
    if (pageDirectory.exists()) {
      LOGGER.log(Level.WARNING, "dropping cache Entry for pageTitle '"
          + pageInfo.dual() + "'" + pageDirectory.getPath());
      FileUtils.cleanDirectory(pageDirectory);
    }
  }

}
