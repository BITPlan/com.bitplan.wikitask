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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * a Source
 * 
 * @author wf
 *
 */
@XmlRootElement(name = "source")
public class Source extends CachedImpl {
  // the id
	String id;
	// true if this source should be cached
	boolean cache;
	String lang;
	String source;
	String pageTitle;

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
	 * @return the id
	 */
	@XmlAttribute
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
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *          the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the cache
	 */
	@XmlAttribute
	public boolean isCache() {
		return cache;
	}

	/**
	 * @param cache
	 *          the cache to set
	 */
	public void setCache(boolean cache) {
		this.cache = cache;
	}

	/**
	 * @return the lang
	 */
	@XmlAttribute
	public String getLang() {
		return lang;
	}

	/**
	 * @param lang
	 *          the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}
}
