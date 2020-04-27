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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.rythmengine.RythmEngine;
import org.sidif.triple.TripleStore;
import org.sidif.wiki.Reference.ReferenceType;
import org.sidif.wiki.rest.AuthFilter;
import org.sidif.wiki.rest.AuthFilter.AccessRight;

import com.bitplan.mediawiki.japi.MediawikiApi;
import com.bitplan.rest.freemarker.FreeMarkerConfiguration;
import com.bitplan.rythm.WikiTemplateResourceLoader;
import com.bitplan.topic.ContextFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * SiDIF Command handler
 * 
 * @author wf
 *
 */
public class WikiTask implements Callable<WikiTaskResult>,Cloneable {
  protected static Logger LOGGER = Logger.getLogger("org.sidif.wiki");

  protected SSLWiki wiki;
  protected boolean debug = false;

  public enum TemplateEngine {
    freemarker, rythm
  };

  public static final String protectionMarker = "THIS FILE IS PROTECTED - smartGENERATOR WILL NOT OVERWRITE IT";

  public String freemarkerTemplatePath = "/templates";

  TemplateEngine templateEngine = TemplateEngine.freemarker;
  String server;
  String cmd;
  String scriptpath;
  String contentlanguage;
  String pageTitle;

  String wikiid;
  String id;
  String input;
  String params;
  String dialog;
  String template;
  String targetpage;

  private String logo;

  private HttpHeaders httpHeaders;

  SourceExtractor source;
  String sidifText;
  String templateText;

  private RythmEngine engine;

  public ReferenceManager referenceManager;

  /**
   * These are Wiki or URL references
   */
  Reference pageLink;
  Reference targetLink;
  Reference templateLink;
  Reference inputLink;
  Reference dialogLink;

  Date startTime;
  Date endTime;

  WikiTaskResult result;
  WikiTaskManager manager;

  private TripleStore tripleStore;

  /**
   * @return the cmd
   */
  public String getCmd() {
    return cmd;
  }

  /**
   * @param cmd the cmd to set
   */
  public void setCmd(String cmd) {
    this.cmd = cmd;
  }

  /**
   * @return the wiki
   */
  public MediawikiApi getWiki() {
    return wiki;
  }

  /**
   * @param wiki
   *          the wiki to set
   */
  public void setWiki(SSLWiki wiki) {
    this.wiki = wiki;
    wiki.setProtectionMarker(protectionMarker);
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the input
   */
  public String getInput() {
    return input;
  }

  /**
   * @param input
   *          the input to set
   */
  public void setInput(String input) {
    this.input = input;
  }

  /**
   * @return the params
   */
  public String getParams() {
    return params;
  }

  /**
   * @param params
   *          the params to set
   */
  public void setParams(String params) {
    this.params = params;
  }

  /**
   * @return the dialog
   */
  public String getDialog() {
    return dialog;
  }

  /**
   * @param dialog
   *          the dialog to set
   */
  public void setDialog(String dialog) {
    this.dialog = dialog;
  }

  /**
   * @return the server
   */
  public String getServer() {
    return server;
  }

  /**
   * @param server
   *          the server to set
   */
  public void setServer(String server) {
    this.server = server;
  }

  /**
   * @return the scriptpath
   */
  public String getScriptpath() {
    return scriptpath;
  }

  /**
   * @param scriptpath
   *          the scriptpath to set
   */
  public void setScriptpath(String scriptpath) {
    this.scriptpath = scriptpath;
  }

  /**
   * @return the contentlanguage
   */
  public String getContentlanguage() {
    return contentlanguage;
  }

  /**
   * @param contentlanguage
   *          the contentlanguage to set
   */
  public void setContentlanguage(String contentlanguage) {
    this.contentlanguage = contentlanguage;
  }

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
   * @return the template
   */
  public String getTemplate() {
    return template;
  }

  /**
   * @param template
   *          the template to set
   */
  public void setTemplate(String template) {
    this.template = template;
  }

  /**
   * @return the targetpage
   */
  public String getTargetpage() {
    return targetpage;
  }

  /**
   * @param targetpage
   *          the targetpage to set
   */
  public void setTargetpage(String targetpage) {
    this.targetpage = targetpage;
  }

  /**
   * @return the targetLink
   */
  public Reference getTargetLink() {
    return targetLink;
  }

  /**
   * @param targetLink
   *          the targetLink to set
   */
  public void setTargetLink(Reference targetLink) {
    this.targetLink = targetLink;
  }

  /**
   * @return the httpHeaders
   */
  public javax.ws.rs.core.HttpHeaders getHttpHeaders() {
    return httpHeaders;
  }

  /**
   * @return the templateEngine
   */
  public TemplateEngine getTemplateEngine() {
    return templateEngine;
  }

  /**
   * @param templateEngine
   *          the templateEngine to set
   */
  public void setTemplateEngine(TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  /**
   * @return the sidifText
   */
  public String getSidifText() {
    return sidifText;
  }

  /**
   * @param sidifText
   *          the sidifText to set
   */
  public void setSidifText(String sidifText) {
    this.sidifText = sidifText;
  }

  /**
   * @return the templateText
   */
  public String getTemplateText() {
    return templateText;
  }

  /**
   * @param templateText
   *          the templateText to set
   */
  public void setTemplateText(String templateText) {
    this.templateText = templateText;
  }

  /**
   * get the wiki id of this WikiTask
   * 
   * @return the wikiid
   */
  public String getWikiId() {
    return wikiid;
  }

  /**
   * @return the manager
   */
  public WikiTaskManager getManager() {
    return manager;
  }

  /**
   * @param manager
   *          the manager to set
   */
  public void setManager(WikiTaskManager manager) {
    this.manager = manager;
  }

  /**
   * get the duration of this wikiTask
   * 
   * @return - the number of milliSeconds this task has been running
   */
  public long getDuration() {
    if (endTime != null) {
      return endTime.getTime() - startTime.getTime();
    } else {
      Date now = new Date();
      return now.getTime() - startTime.getTime();
    }
  }

  /**
   * check the parameters for this wiki
   * 
   * @return null if no errors - otherwise the html encoded error page
   * @throws Exception
   */
  private String checkParams() throws Exception {
    if ((this.server == null) || (this.scriptpath == null)) {
      return "The query parameters 'server' and 'scriptpath' need to be set. See Wikitask description for examples.";
    }
    if (this.pageTitle == null) {
      return "The query parameter 'page' needs to be set";
    }
    return null;
  }

  /**
   * create a html formatted error message
   * 
   * @param msg
   * @param stacktrace
   * @param msg2
   * @return the error message
   * @throws Exception
   */
  public String error(String error, String msg, String stacktrace)
      throws Exception {
    Map<String, Object> rootMap = getRootMap("WikiTask error");
    this.templateEngine = WikiTask.TemplateEngine.freemarker;
    rootMap.put("error", error);
    rootMap.put("msg", msg);
    rootMap.put("stacktrace", stacktrace);
    String result = processTemplate(rootMap, "error.ftl");
    return result;
  }

  // the SSL Wiki for this WikiTask
  private SSLWiki sslWiki;

  /**
   * prepare the wiki
   * 
   * @throws Exception
   */
  public void prepareWiki() throws Exception {
    if (sslWiki == null) {
      sslWiki = SSLWiki.ofId(wikiid);
      sslWiki.login();
    }
    referenceManager = ReferenceManager.get(sslWiki);
    // TODO - does this help?
    this.checkPageCache();
    setWiki(sslWiki);
  }

  /**
   * Returns a pseudo-random number between min and max, inclusive. The
   * difference between min and max can be at most
   * <code>Integer.MAX_VALUE - 1</code>.
   *
   * @param min
   *          Minimum value
   * @param max
   *          Maximum value. Must be greater than min.
   * @return Integer between min and max, inclusive.
   * @see java.util.Random#nextInt(int)
   */
  public int randomInteger(int min, int max) {

    // NOTE: Usually this should be a field rather than a method
    // variable so that it is not re-seeded every call.
    Random rand = new Random();

    // nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive
    int randomNum = rand.nextInt((max - min) + 1) + min;

    return randomNum;
  }
  
  /**
   * clone me
   */
  public WikiTask clone() {
    WikiTask lWikiTask=null;
    try {
      lWikiTask = (WikiTask) super.clone();
      // important - make sure we use a different result!
      lWikiTask.result= new WikiTaskResult(lWikiTask);
    } catch (CloneNotSupportedException e) {
      // ignore;
    }
    return lWikiTask;
  }

  /**
   * run a WikiTask with the given parameters
   * 
   * @param httpHeaders
   * @param server
   * @param scriptpath
   * @param contentlanguage
   * @param pageTitle
   * @param cmd
   * @param input
   * @param params
   * @param dialog
   * @param template
   * @param pTemplateEngine
   * @param targetpage
   * @param logo
   * @throws Exception
   */
  public WikiTask(HttpHeaders httpHeaders, String server, String scriptpath,
      String contentlanguage, String pageTitle, String cmd, String input,
      String params, String dialog, String template, String pTemplateEngine,
      String targetpage, String logo) throws Exception {
    // remember the startTime of this Wiki
    this.startTime = new Date();
    result = new WikiTaskResult(this);
    this.httpHeaders = httpHeaders;
    this.server = server;
    if (server != null) {
      LOGGER.log(Level.INFO, "WikiTask for server " + server);
      URL serverurl = new URL(server);
      String hostname = serverurl.getHost();
      String[] parts = hostname.split("\\.");
      // get the wikiid from the url
      if (parts.length > 0) {
        this.wikiid = parts[0];
      } else {
        throw new IllegalArgumentException("can't extract wikiid from server "
            + server);
      }
    }
    this.scriptpath = scriptpath;
    // is there a scriptpath different from "/"
    if (!"/".equals(scriptpath.trim())) {
      String idPart2=scriptpath.replace("/", "");
      // check that the idpart is non empty!
      if (!idPart2.trim().equals(""))
        wikiid+="_"+idPart2;
    }
    this.contentlanguage = contentlanguage;
    this.pageTitle = pageTitle;
    this.cmd = cmd;
    this.input = input;
    this.params = params;
    this.dialog = dialog;
    this.template = template;
    if (pTemplateEngine != null && !pTemplateEngine.trim().equals("")) {
      this.templateEngine = TemplateEngine.valueOf(pTemplateEngine.toLowerCase());
    }
    this.targetpage = targetpage;
    this.logo = logo;
  }

  /**
   * get the root map for this WikiTask
   * 
   * @param title
   *          - the title to use
   * @return - a preset root map with title and logo
   */
  public Map<String, Object> getRootMap(String title) {
    Map<String, Object> rootMap = new HashMap<String, Object>();
    rootMap.put("title", title);
    rootMap.put("logo", logo);
    rootMap.put("wikiTask", this);
    return rootMap;
  }

  /**
   * get help
   * 
   * @return the help string
   * @throws Exception
   */
  public String help() throws Exception {
    Map<String, Object> rootMap = getRootMap("WikiTask help");
    String result = processTemplate(rootMap, "help.ftl");
    return result;
  }

  /**
   * debug the page
   * 
   * @return
   * @throws Exception
   */
  private String debug() throws Exception {
    String pageContent = wiki.getPageContent(this.pageTitle);
    Map<String, Object> rootMap = getRootMap("WikiTask debug");
    rootMap.put("pageTitle", pageTitle);
    rootMap.put("pageContent", pageContent);
    String result = processTemplate(rootMap, "debug.ftl");
    return result;
  }

  /**
   * get the WikiTask Home
   * 
   * @return
   */
  public static String getWikiTaskHome() {
    String wikiTaskHome = System.getProperty("user.home") + "/.wikitask/";
    return wikiTaskHome;
  }

  /**
   * get a property File for the given Kind
   * 
   * @param propertyKind
   * @return the property File
   */
  public static File getPropertyFile(String propertyKind) {
    File propFile = getPropertyFile(propertyKind, ".ini");
    return propFile;
  }

  /**
   * get the property File for the given Kind and extension
   * 
   * @param propertyKind
   * @param extension
   * @return the property File
   */
  public static File getPropertyFile(String propertyKind, String extension) {
    String propertyFileName = getWikiTaskHome() + propertyKind + extension;
    File propFile = new File(propertyFileName);
    return propFile;
  }

  /**
   * get the access property file
   * 
   * @return the property File
   */
  public static File getAccessPropertyFile() {
    File result = getPropertyFile("access");
    return result;
  }

  /**
   * get the LocalSettings File for this WikiTask
   * 
   * @return the local settings
   * @throws FileNotFoundException
   * @throws IOException
   */
  public String getLocalSettings() throws FileNotFoundException, IOException {
    String localSettings = WikiManager.getLocalSettings(this.wikiid);
    return localSettings;
  }

  /**
   * get the right for the given name
   * 
   * @param name
   * @return
   * @throws Exception
   */
  public AccessRight getRight(String name) throws Exception {
    name = name.replace(" ", "_");
    // urlencode might also garble spaces ...
    name = name.replace("+", "_");
    Properties props = new Properties();
    props.load(new FileReader(getAccessPropertyFile()));
    String right = props.getProperty(name);
    AccessRight result = AccessRight.none;
    if (right != null) {
      result = AccessRight.valueOf(right);
    }
    return result;
  }

  /**
   * log me in
   * 
   * @param result
   * @throws Exception
   */
  private void login(WikiTaskResult result) throws Exception {
    String addr = "?"; // @TODO get remote addr / url
    String user = this.input.trim();
    result.html = "access denied for " + user;
    AccessRight right = getRight(user);
    switch (right) {
    case read:
    case write: {
      AuthFilter.grant(addr, AuthFilter.AccessRight.write);
      result.html = "access " + right + " granted for " + user + " from "
          + addr;
    }
      break;
    default:
    }
  }

  /**
   * get the root
   * 
   * @param rootMap
   * @param result
   * @throws Exception
   */
  public void fillRootMap(Map<String, Object> rootMap, WikiTaskResult result)
      throws Exception {
    Source templateSource = referenceManager.getSource(templateLink);
    if (templateSource == null) {
      result.throwable = new Exception("template " + template
          + " not found in " + this.pageTitle+" wiki "+this.wikiid+"/"+this.wiki.getWikiid()+":"+this.getWiki().getSiteurl());
      return;
    }
    templateText = templateSource.source;
    // is the input from a URL?
    if ("none".equals(input) || input.startsWith("http:")
        || input.startsWith("https:")) {
      // do nothing but continue
    } else {
      fillRootMapBySiDIF(rootMap, result);
    }
  }

  /**
   * get the tripleStore
   * 
   * @return the TripleStore
   * @throws Exception
   */
  public TripleStore getTripleStore() throws Exception {
    if (tripleStore == null) {
      if (inputLink==null)
        throw new IllegalStateException("get TripleStore called with null inputLink");
      LOGGER.log(Level.INFO, "getting tripleStore for inputLink "
          + inputLink.getReferenceId());
      TripleStoreManager tm = TripleStoreManager.getInstance();
      tripleStore=tm.getByReference(this.referenceManager,inputLink);
      if (tripleStore == null) {
        result.throwable = new Exception("SiDIF input " + input
            + " not found in " + this.pageTitle);
        return null;
      }
    }
    return tripleStore;
  }

  /**
   * fill the rootMap with the given SiDIF input
   * 
   * @param rootMap
   * @param result
   * @throws Exception
   */
  public void fillRootMapBySiDIF(Map<String, Object> rootMap,
      WikiTaskResult result) throws Exception {

    TripleStore tripleStore = this.getTripleStore();
    rootMap.put("tripleStore", tripleStore);
  }

  /**
   * get the
   * 
   * @throws Exception
   */
  public void getLinksAndSources() throws Exception {
    // pageLink needs to be created first to make it non null for referencing
    // links below
    pageLink = referenceManager.getReference(wiki, pageTitle);
    // first make dialog available
    dialogLink = referenceManager.getReference(wiki, dialog, pageLink);
    // get the input
    inputLink = referenceManager.getReference(wiki, input, pageLink);
    // get the template
    templateLink = referenceManager.getReference(wiki, template, pageLink);
    // get the target
    targetLink = referenceManager.getReference(wiki, targetpage);
    if (targetLink!=null)
      targetLink.setTarget(true);
    referenceManager.addSources();
  }

  /**
   * process the template for the given reference with the given title
   * 
   * @param reference
   * @param title
   * @return
   * @throws Exception
   */
  public synchronized String processTemplate(Reference reference, String title)
      throws Exception {
    if (reference == null) {
      throw new IllegalArgumentException("reference may not be null");
    }
    Source source = referenceManager.getSource(reference);
    if (source == null) {
      throw new Exception("referenceManager returned null source for "
          + reference.getReferenceId() + "(title='" + title + "')");
    }
    String result = processTemplate(source, title);
    return result;
  }

  /**
   * process the template from the given source with the given title
   * 
   * @param source
   * @param title
   * @return - the template result
   * @throws Exception
   */
  public synchronized String processTemplate(Source source, String title)
      throws Exception {
    Map<String, Object> rootMap = this.getRootMap(title);
    if (source == null) {
      throw new IllegalArgumentException("source may not be null");
    }
    String result = this.processTemplateFromString(rootMap, source.id,
        source.source);
    return result;
  }

  /**
   * run the template as specified by the constructor
   * 
   * @param result
   * 
   * @throws Exception
   */
  protected void runtemplate(WikiTaskResult result) {
    try {
      // prepare the rootMap
      Map<String, Object> rootMap = this.getRootMap(this.pageTitle);

      // get Links and Sources
      getLinksAndSources();
      // fill the RootMap from the given input
      fillRootMap(rootMap, result);
      // if there was a problem do not continue
      if (result.throwable != null) {
        return;
      }

      // get the template Result
      String templateResult = this.processTemplateFromString(rootMap, template,
          templateText);
      String summary = "created by WikiTask " + wiki.getIsoTimeStamp();
      // decide where to put the result
      if (targetLink.referenceType == ReferenceType.ANCHOR) {
        boolean minor = false;
        boolean bot = true;
        // check if the section already exists
        // set defaults for new section
        String sectionPageTitle = targetLink.pageTitle;
        int sectionIndex = -1; // new section
        String sectionAnchor = targetLink.anchor;
        // lookup the target link section
        Section section = referenceManager.getSection(targetLink);
        // if the section exists use other values
        if (section != null) {
          sectionPageTitle = section.pageTitle;
          sectionIndex = section.sectionIndex;
          sectionAnchor = section.anchor;
        }
        // read section title if it's replaced
        if (section != null && section.sectionIndex > 0) {
          templateResult = section.sectionTitle + "\n" + templateResult;
        }
        // now edit or create the section
        wiki.edit(sectionPageTitle, templateResult, summary, minor, bot,
            sectionIndex, sectionAnchor, null);
      } else {
        wiki.edit(targetpage, templateResult, summary);
      }
      URI targetURIForRedirection = getPageUri(targetpage);
      result.response = Response.seeOther(targetURIForRedirection).build();
    } catch (Throwable th) {
      result.throwable = th;
    }

  }

  /**
   * get the PageUrl of the given page
   * 
   * @return
   */
  public String getPageUrl() {
    String url;
    try {
      url = getPageUri(this.pageTitle).toASCIIString();
    } catch (URISyntaxException e) {
      url = "invalid: " + this.pageTitle + " " + e.getMessage();
    }
    return url;
  }

  /**
   * get an encoded page name for the given targetpage
   * 
   * @param targetpage
   * @return the encoded pagename according to
   *         https://www.mediawiki.org/wiki/Manual:PAGENAMEE_encoding
   */
  public static String getEncodedPagename(String targetpage) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < targetpage.length(); i++) {
      char c = targetpage.charAt(i);
      String enc = "" + c;
      switch (c) {
      case '"':
        enc = "&#34";
        break;
      case '&':
        enc = "&#38";
        break;
      case '\'':
        enc = "&#39";
        break;
      case ' ':
        enc = "_";
        break;
      case '%':
        enc = "%25";
        break;
      case '+':
        enc = "%2B";
        break;
      case '=':
        enc = "%3D";
        break;
      case '?':
        enc = "%3F";
        break;
      case '\\':
        enc = "%5C";
        break;
      case '^':
        enc = "%5E";
        break;
      case '`':
        enc = "%60";
        break;
      case '~':
        enc = "%7E";
        break;

      }
      sb.append(enc);
    }
    return sb.toString();
  }

  /**
   * get the URI for a given targetpage
   * 
   * @param targetpage
   * @return
   * @throws URISyntaxException
   */
  public URI getPageUri(String targetpage) throws URISyntaxException {
    String encodedPage = getEncodedPagename(targetpage);
    LOGGER.log(Level.INFO, "getting target uri for " + targetpage + " ("
        + encodedPage + ") server=" + server + " scriptpath=" + scriptpath);
    String uri = server + scriptpath + "/index.php/" + encodedPage;
    URI result = new URI(uri);
    return result;
  }

  /**
   * process the given template
   * 
   * @param rootMap
   * @param templateName
   * @return the result of processing the template
   * @throws Exception
   */
  protected String processTemplate(Map<String, Object> rootMap,
      String templateName) throws Exception {
    String result = "?";
    switch (templateEngine) {
    case freemarker:
      // tell Freemarker to use my class path and therefore find templates in
      // main/resources/templates
      FreeMarkerConfiguration.addTemplateClass(WikiTask.class,
          freemarkerTemplatePath);
      // process the template with the given name
      if (debug)
        LOGGER.log(Level.INFO, "processing template " + templateName
            + " from path " + freemarkerTemplatePath);
      result = FreeMarkerConfiguration.doProcessTemplate(templateName, rootMap);
      break;
    case rythm:
      throw new Exception("processing from templatename not implemented yet "
          + templateName);
    }
    return result;
  }

  /**
   * process a template from the given templateString with Freemarker
   * 
   * @param rootMap
   * @param templateName
   * @param templateStr
   * @return the result of Freemarker- processing the given rootMap with the
   *         template from templateStr that has the name templateName
   * @throws Exception
   */
  protected synchronized String processTemplateFromString(
      Map<String, Object> rootMap, String templateName, String templateStr)
      throws Exception {
    String result = "?";
    switch (templateEngine) {
    case freemarker:
      Configuration freeMarkerConfiguration = FreeMarkerConfiguration
          .getFreemarkerConfiguration();
      Template template = new Template(templateName, new StringReader(
          templateStr), freeMarkerConfiguration);
      StringWriter htmlWriter = new StringWriter();
      template.process(rootMap, htmlWriter);
      result = htmlWriter.toString();
      break;
    case rythm:
      RythmEngine engine = getEngine();
      result = engine.render(templateStr, rootMap);
      // remove superfluous whitespace
      result = result.trim();
      break;
    }
    return result;
  }

  /**
   * get the Rythm engine
   * 
   * @return
   */
  private RythmEngine getEngine() {
    if (engine == null) {
      Map<String, Object> conf = new HashMap<String, Object>();
      conf.put("codegen.compact.enabled", false);
      engine = new RythmEngine(conf);
      WikiTemplateResourceLoader wikiTemplateResourceLoader = new WikiTemplateResourceLoader(
          wiki);
      wikiTemplateResourceLoader.setEngine(engine);
      engine.registerResourceLoader(wikiTemplateResourceLoader);
    }
    return engine;
  }

  /**
   * return me as a String;
   */
  public String toString() {
    String result = "server:" + server + "\nscriptpath: " + scriptpath
        + "\ncontentlanguage: " + contentlanguage + "\npage:" + pageTitle
        + "\nlogo:" + logo + "\ninput:" + input + "\ntemplate:" + template
        + "\ntargetpage:" + targetpage + "\nengine:" + templateEngine
        + "\ncmd:" + cmd;
    return result;
  }
  
  /**
   * get a stack trace for the given throwable
   * 
   * @param t
   *          - the throwable to get the stack trace for
   * @return - the stacktrace as a string
   */
  public String getStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    t.printStackTrace(new PrintWriter(sw));
    return sw.toString();
  }

  /**
   * call this wiki Task
   */
  public WikiTaskResult call() {
    try {
      this.startTime = new Date();
      String paramCheck = checkParams();
      if (paramCheck != null) {
        result.html = error("missing parameter", paramCheck, null);
      } else {
        prepareWiki();
        if ("help".equals(cmd)) {
          result.html = help();
        } else if ("debug".equals(cmd)) {
          result.html = debug();
        } else if ("reset".equals(cmd)) {
          runReset(result);
        } else if ("refreshRef".equals(cmd)) {
          refreshRef(result);
        } else if ("dialog".equals(cmd)) {
          runDialog(result);
        } else if ("login".equals(cmd)) {
          login(result);
        } else if ("runtemplate".equals(cmd)) {
          runtemplate(result);
        } else {
          throw new Exception("unknown WikiTask Command " + cmd);
        }
      }
    } catch (Exception e) {
      result.throwable = e;
    }
    endTime = new Date();
    if (result.throwable!=null) {
      Throwable th=result.throwable;
      LOGGER.log(Level.SEVERE,"WikiTask failed "+th.getMessage());
      LOGGER.log(Level.SEVERE,this.getStackTrace(th));
      if (th.getCause()!=null) {
        LOGGER.log(Level.SEVERE,"cause "+th.getCause().getMessage());
        LOGGER.log(Level.SEVERE,this.getStackTrace(th.getCause()));
      }
    }
    return result;
  }

  /**
   * refresh the given reference
   * 
   * @param result
   * @throws Exception
   */
  private void refreshRef(WikiTaskResult result) throws Exception {
    // update Cache from disk
    referenceManager.getPageCache().check(false);
    Reference reference = referenceManager.getReferenceById(this.params);
    if (reference == null) {
      LOGGER.log(Level.WARNING, "invalid reference id " + this.params);
    } else {
      reference.setAvailable(false);
    }
  }
  
  /**
   * check the Cache
   * @throws Exception
   */
  public void checkPageCache() throws Exception {
    // check all pages in the cache
    referenceManager.getPageCache().check(false);
  }

  /**
   * reset cache and login state
   * 
   * @param result
   *          -the result of the reset
   * @throws Exception
   */
  public void runReset(WikiTaskResult result) throws Exception {
    checkPageCache();

    // force new login by resetting the Wiki
    sslWiki = null;
    // get the Wiki back
    prepareWiki();

    // clear cache by getting a referenceManager.getManager
    referenceManager = ReferenceManager.reset(sslWiki);
    
    // clear contextFactory cache
    ContextFactory.reset();

    Map<String, Object> rootMap = this.getRootMap("WikiTask reset");
    result.html = processTemplate(rootMap, "reset.ftl");
  }

  /**
   * test dialog handling
   * 
   * @param result
   *          -the result of the dialog handling
   * @throws Exception
   */
  public void runDialog(WikiTaskResult result) throws Exception {
    // FIXME this is deprecated and only for development
    Map<String, Object> rootMap = this.getRootMap("WikiTask dialog");
    result.html = processTemplate(rootMap, "dialog.ftl");
  }

}
