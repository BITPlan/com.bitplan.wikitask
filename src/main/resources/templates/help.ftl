<#--
  --
  --   Copyright (C) 2015-2020 BITPlan GmbH
  -- 
  --   Pater-Delp-Str. -- 1
  --   D-47877 -- Willich-Schiefbahn
  -- 
  --   http://www.bitplan.com
  --
  -- 
  -- Freemarker Template for help page
-->
<#import "./frame.ftl" as frame>
<@frame.header title="${title}" comment="help"/>
<@frame.body title="${title}"/>
  <h2>WikiTask help</h3>
  This is just a short overview. For more details, e.g. using different template engines see
  <a href='${wikiTask.server}${wikiTask.scriptpath}/index.php/Template:Wikitask'>Wikitask Template description.</a>
  <h3>help</h3>
  <pre>{{Wikitask|cmd=help}}</pre>
  will show this help
  <h3>debug</h3>
  <pre>{{Wikitask|cmd=debug}}</pre>
  will show your current wiki page wiki markup and debug information for the WikiTask
  <h3>reset</h3>
  <pre>{{Wikitask|cmd=reset}}</pre>
  will reset the cache used for templates, input and dialog references
  <h3>runtemplate</h3>
  will run a template on the given input
  <h4>parameters</h4>
  <ul>
    <li>input - can be
    <ul>
       <li>a reference to a source tag by id e.g 'dadlovesmom' expects <pre><source ..id='dadlovesmom'></pre>
        on the same page to include SiDIF content
       <li>a reference to a section using an anchor e.g. 'Sourcepage#section' expects a page 
       'Sourcepage' with a section 'section' to include one <pre><source..></pre>tag to include SiDIF content
       <li>a  http:// or https:// reference URL to any type of input
     </ul>
     <li>template - the template to use
     <li>engine - the engine to use - may be 
     <ul>
     <li>Rythm (default)
     <li>Freemarker
     </ul>  
     <li>dialog - the dialog to be displayed while the wikiTask runs
     <li>targetpage - where to put the results - can be a page or a section of a page if an anchor is used
  </ul>
  <h4>example</h4>
  <pre>
{{wikitask|cmd=runtemplate|input=dadlovesmum|template=triplegraph|targetpage=Dad loves mum 2015-03-14}}
</pre>
  Some <a href='http://www.sidif.org'>Simple Data Interchange Format</a> input is made available
  in a source tag and the id is set to "dadlovesmum":
  <pre>
    <#noparse>
&lt;source lang='xml' id='dadlovesmum'&gt;
  dad loves mum
  kid loves mum
  kid loves dad
  kid likes granny
&lt;/source&gt;
    </#noparse>
  </pre>
  A <a href='http://www.freemarker.org'>Freemarker template</a> is made available in a source tag and the id
  is set to "triplegraph":
  <pre>
    <#noparse>
&lt;source lang='java' id='triplegraph' &gt;
<#assign rankdir="TB"/>
<#assign triples=tripleStore.getTriples()/>
== Triple graph ==
&lt;graphviz format='svg'&gt;
 digraph  {
   rankdir="${rankdir}";
  <#list triples as triple>
    "${triple.subject}" -> "${triple.object}" [label="${triple.predicate}"];
  &lt;/#list&gt;
  }
&lt;/graphviz&gt;
&lt;/source&gt;
    </#noparse>
  </pre>
  now the WikiTask cmd "runtemplate" will load the sidif content into a tripleStore and make it available
  to the Freemarker template. The template processing result will be put in the specified targetpage.
<pre>
{{wikitask|cmd=runtemplate|input=dadlovesmum|template=triplegraph|targetpage=Dad loves mum 2015-03-14}}
</pre>
will therefore create the targetpage "Dad loves mum 2015-03-14". The resulting page is opened in a new tab.
<@frame.footer/>