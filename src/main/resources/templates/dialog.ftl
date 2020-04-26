<#--
  --
  -- 	Copyright (C) 2015-2020 BITPlan GmbH
  -- 
  -- 	Pater-Delp-Str. -- 1
  -- 	D-47877 -- Willich-Schiefbahn
  -- 
  -- 	http://www.bitplan.com
  -- 
  -- Freemarker Template for debug page
-->
<#import "./frame.ftl" as frame>
<@frame.jquery/>
<html>
  <head>
    <meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>
    <title>${title}</title>
    <style>
    body { height:100% }
    #container {
background-color: #f0f0f0; /* hellgrauer Hintergrund */
width: 50%; /* Container auf halbe Breite */
margin: auto; /* Den Container zusätzlich zentrieren */
min-height: 100%; /* Mindesthöhe auf 100 % (bei modernen Browsern) */
height: auto !important; /* important Behel (bei modernen Browsern */
height: 100%; /* IE soll wie gewünscht interpretieren */
overflow: hidden !important; /* Firefox Scrollleiste */
     }
    </style>
 </head>
 <body>
    <header>
    </header>
  <div id='container' class='container'>
	  <div id="dialog" title="${title}" >
	  <progress max="100" value="80"></progress>
<pre>
${wikiTask.getServer()}
${wikiTask.getScriptpath()}
${wikiTask.getPageTitle()}
</pre>
    </div>
  	<script type="text/javascript">
		$(document).ready(function() {
			var wWidth = $(window).width();
			var wHeight = $(window).height();
			var dTopy = wHeight * 0.25; // top at 25% of window height
			var dTopx = wWidth * 0.25; // left at 25% of window width
			var dWidth = 'auto'; //this will make the dialog 98% of the window size
			var dHeight = 'auto';
			$("#dialog").dialog({
				
				// position : [ dTopx, dTopy ],
				width : dWidth,
				height : dHeight,
				modal : true,
				// title: ""+wWidth+"x"+wHeight+" "+dTopx+":"+dTopy,
				my: "center",
		         at: "center",
		         of: window,
				show : {
					effect : "blind",
					duration : 500
				},
				hide : {
					effect : "fade",
					duration : 500
				}
			});
		});
	    </script>
	  </div>
    <footer>
    </footer>
  </body>
</html>
