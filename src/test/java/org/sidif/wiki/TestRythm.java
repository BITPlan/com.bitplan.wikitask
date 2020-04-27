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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;

import com.bitplan.rythm.WikiTemplateResourceLoader;

/**
 * test Rhythm see http://rythmengine.org/
 * 
 * @author wf
 *
 */
public class TestRythm extends BaseTest {
 
  /**
   * check with optional debug output
   * 
   * @param expected
   * @param result
   */
  public void check(String expected, String result) {
    if (debug)
      System.out.println(result);
    assertEquals(expected, result);
  }

  @Test
  public void testRythm() throws Exception {
    String template = "hello @who!";
    String result = Rythm.render(template, "world");
    check("hello world!", result);

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("who", "World");
    result = Rythm.render("@args String who\nHello @who", params);
    check("Hello World", result);
  }

  public String getFoo() {
    return "foo";
  }

  @Test
  public void testRythmMap() {
    Map<String, Object> rootMap = new HashMap<String, Object>();
    rootMap.put("test1", "test");
    rootMap.put("test2", this);
    String result = Rythm
        .render(
            "@args String test1,org.sidif.wiki.TestRythm test2\n@test1\n@test2.getFoo()",
            rootMap);
    check("test\n" + "foo", result);
  }

  @Test
  public void testCompact() {
     String template="no whitespace here\n" + 
         "@for (int i=0;i<2;i++) \n" + 
         "{  please keep the whitespace\n}" + 
         "\nno whitespace here\n"  ;
     Map<String, Object> conf = new HashMap<String, Object>();
     conf.put("codegen.compact.enabled",false);
     Rythm.shutdown();
     Rythm.init(conf);
     Map<String, Object> rootMap = new HashMap<String, Object>();
     String result=Rythm.render(template,rootMap);
     check("no whitespace here\n" + 
         "  please keep the whitespace\n" + 
         "  please keep the whitespace\n" + 
         "no whitespace here\n",result);
  }
  
  @Test
  public void testRythmArgs() {
    Map<String, Object> rootMap = new HashMap<String, Object>();
    rootMap.put("test", this);
    rootMap.put("test2", this);
    String argvariants[] = {
        "@args org.sidif.wiki.TestRythm test,org.sidif.wiki.TestRythm test2\n",
        "@args() {\norg.sidif.wiki.TestRythm test\norg.sidif.wiki.TestRythm test2\n}" };
    for (String args : argvariants) {
      String result = Rythm.render(args
          + "@test.getClass().getName()\n@test2.getFoo()", rootMap);
      check("org.sidif.wiki.TestRythm\n" + "foo", result);
    }
  }

  @Ignore
  public void testLocalClassAsDefParam() {
    String template = "@{ class SomeClass {\n" + "  String someField;\n"
        + "}\n" + "SomeClass someInstance=new SomeClass();\n"
        + "someInstance.someField=\"someValue\" \n" + "}\n"
        + "@def func(SomeClass c) {\n" + "@c.someField\n" + "}\n"
        + "@func(someInstance)\n";
    String result = Rythm.render(template, "noargs");
    check("test", result);
  }
  
  @Test
  public void testWhiteSpace() {
    String template="<pre>\n</pre>\n";
    String result=Rythm.render(template,"noargs");
    System.out.println(result);
  }
  
  /**
   * get the given resource loader Engine
   * @return
   * @throws Exception
   */
  public RythmEngine getResourceLoaderEngine() throws Exception {
    WikiTask wikiTask = TestWikiTask.getWikiTask();
    wikiTask.checkPageCache();
    Map<String, Object> conf = new HashMap<String, Object>();
    conf.put("codegen.compact.enabled", false);
    //  resource.loader.impls
    RythmEngine engine=new RythmEngine(conf);
    WikiTemplateResourceLoader wikiTemplateResourceLoader = new WikiTemplateResourceLoader(wikiTask.wiki);
    wikiTemplateResourceLoader.setEngine(engine);
    engine.registerResourceLoader(wikiTemplateResourceLoader);
    return engine;    
    
  }
  /**
   * tests the resource loader handling
   * @throws Exception 
   */
  @Test
  public void testResourceLoader() throws Exception {
    String template="@include(wiki.ExampleRythm.template1)\n@wiki.ExampleRythm.template1(\"rythm\")";
    RythmEngine engine = getResourceLoaderEngine();
    String result=engine.render(template,"noargs");
    String expected="Ok noargs - here we go\n" + 
        "Ok rythm - here we go";
    assertTrue(result.startsWith(expected));
  }
  
  @Test
  public void testInclude() throws Exception {
    String template="@include(wiki.ExampleRythm.defs)\n@wiki.ExampleRythm.showtopic(\"rythm\")";
    RythmEngine engine = getResourceLoaderEngine();
    String result=engine.render(template,"noargs");
    assertEquals("testTopic=testTopic",result.trim());
  }
  
  @Test 
  public void testDefLocalParams() throws Exception {
    String template="@def class Hello {\n" + 
        "String hello=\"Hello\";\n" + 
        "}\n" + 
        "\n" + 
        "@{\n" + 
        "Hello hello=new Hello();\n" + 
        "}\n" + 
        "@def sayHello(Hello hello) {\n" + 
        "@hello.hello\n" + 
        "}\n" + 
        "@hello.hello\n" + 
        "@sayHello(hello)";
    String result=Rythm.render(template,"noargs");
    if (debug) {
      System.out.println(result);
    }
  }
  
  /**
   * test 
   * @throws Exception
   */
  @Test
  public void testStaticClass() throws Exception {
   String template="@def static {\n" + 
   		"static class MyInnerClass {\n" + 
   		"    String foo() {\n" + 
   		"        return \"hello foo\";\n" + 
   		"    }\n" + 
   		"}\n" + 
   		"}\n" + 
   		"\n" + 
   		"@{\n" + 
   		"MyInnerClass inst = new MyInnerClass();\n" + 
   		"}\n" + 
   		"\n" + 
   		"@inst.foo()";
    String result=Rythm.render(template,"noargs");
    // debug=true;
    if (debug) {
      System.out.println(result);
    }
    assertTrue(result.contains("hello foo"));
  }

}
