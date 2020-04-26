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
  <h2>Error: ${error}</h3>
  <img src='/stockicons/48x48/shadow/hand_red_card.png' alt='red card' title='error'>
  <#if msg??><span style="color:red"><pre>${msg}</pre></span></#if>
  <#if stacktrace??>
     <h3>Stacktrace</h3>
     <pre>${stacktrace}
     </pre>
  </#if>
  <#if wikiTask.server??><#if wikiTask.scriptpath??>
  See <a href='${wikiTask.server}${wikiTask.scriptpath}/index.php/Template:Wikitask'>Wikitask Template description.</a> for the correct usage.
  </#if></#if>
<@frame.footer/>