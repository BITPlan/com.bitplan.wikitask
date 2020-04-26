<#--
  --
  -- 	Copyright (C) 2015-2020 BITPlan GmbH
  -- 
  -- 	Pater-Delp-Str. -- 1
  -- 	D-47877 -- Willich-Schiefbahn
  -- 
  -- 	http://www.bitplan.com
  --
  -- 
  -- Freemarker Template for debug page
-->
<#import "./frame.ftl" as frame>
<@frame.header title="${title}" comment="debug"/>
<@frame.body title="${title}"/>
<h3>wikiTask debug</h3>
<pre>
${wikiTask}
pageurl: ${wikiTask.getPageUrl()}
</pre>
<h3>${pageTitle}</h3>
<pre>
${pageContent?html}
</pre>
<@frame.footer/>