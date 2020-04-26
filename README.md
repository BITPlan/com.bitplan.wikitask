### com.bitplan.wikitask
[WikiTask Server for Semantic MediaWiki based ProfiWiki page generation](http://wiki.bitplan.com/index.php/com.bitplan.wikitask)

[![Travis (.org)](https://img.shields.io/travis/BITPlan/com.bitplan.wikitask.svg)](https://travis-ci.org/BITPlan/com.bitplan.wikitask)
[![Maven Central](https://img.shields.io/maven-central/v/com.bitplan/com.bitplan.wikitask.svg)](https://search.maven.org/artifact/com.bitplan/com.bitplan.wikitask/0.0.1/jar)
[![GitHub issues](https://img.shields.io/github/issues/BITPlan/com.bitplan.wikitask.svg)](https://github.com/BITPlan/com.bitplan.wikitask/issues)
[![GitHub issues](https://img.shields.io/github/issues-closed/BITPlan/com.bitplan.wikitask.svg)](https://github.com/BITPlan/com.bitplan.wikitask/issues/?q=is%3Aissue+is%3Aclosed)
[![GitHub](https://img.shields.io/github/license/BITPlan/com.bitplan.wikitask.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![BITPlan](http://wiki.bitplan.com/images/wiki/thumb/3/38/BITPlanLogoFontLessTransparent.png/198px-BITPlanLogoFontLessTransparent.png)](http://www.bitplan.com)

### Documentation
* [Wiki](http://wiki.bitplan.com/index.php/com.bitplan.wikitask)
* [com.bitplan.wikitask Project pages](https://BITPlan.github.io/com.bitplan.wikitask)
* [Javadoc](https://BITPlan.github.io/com.bitplan.wikitask/apidocs/index.html)
* [Test-Report](https://BITPlan.github.io/com.bitplan.wikitask/surefire-report.html)
### Maven dependency

Maven dependency
```xml
<!-- Java API for AVM wikitask Homeautomation http://wiki.bitplan.com/index.php/wikitask-java-api -->
<dependency>
  <groupId>com.bitplan</groupId>
  <artifactId>com.bitplan.wikitask</artifactId>
  <version>0.0.1</version>
</dependency>
```

[Current release at repo1.maven.org](http://repo1.maven.org/maven2/com/bitplan/com.bitplan.wikitask/0.0.5/)

### How to build
```
git clone https://github.com/BITPlan/com.bitplan.wikitask
cd com.bitplan.wikitask
mvn install
```
## Version history
* 0.0.1: 2018-08-04 First release via GitHub / Maven central
* 0.0.2: 2018-08-07 adds call list option
* 0.0.3: 2018-08-07 adds wikitaskSessionBuilder and wikitaskImpl.getInstance()
* 0.0.4: 2018-08-10 adds ainForName lookup
* 0.0.5: 2018-08-24 connect/close for session handling to avoid timeouts - close session for every command
