/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.wikitask open source project
 *
 * Copyright 2015-2022 BITPlan GmbH https://github.com/BITPlan
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sidif.wiki.Reference.ReferenceType;

import com.bitplan.mediawiki.japi.MediawikiApi;

/**
 * manager for Sources - it keeps track of sources from different pages and
 * ids/anchors
 * 
 * @author wf
 *
 */
public class ReferenceManager {
	boolean debug = true;

	// map Sources for the given PageTitles
	Map<String, Map<String, Source>> sourceById;
	Map<String, Map<String, Source>> sourceBySection;
	public Map<String, Reference> referenceByReferenceId = new LinkedHashMap<String, Reference>();
	/**
	 * reference Manager per Wiki
	 */
	static Map<String, ReferenceManager> referenceManagerMap = new LinkedHashMap<String, ReferenceManager>();

	private PageCache pageCache;

	private SSLWiki sslWiki;
	protected static Logger LOGGER = Logger.getLogger("org.sidif.wiki");

	/**
	 * @return the pageCache
	 */
	public PageCache getPageCache() {
		return pageCache;
	}

	/**
	 * @param pageCache
	 *          the pageCache to set
	 */
	public void setPageCache(PageCache pageCache) {
		this.pageCache = pageCache;
	}

	/**
	 * @return the sslWiki
	 */
	public SSLWiki getSslWiki() {
		return sslWiki;
	}

	/**
	 * @param sslWiki
	 *          the sslWiki to set
	 */
	public void setSslWiki(SSLWiki sslWiki) {
		this.sslWiki = sslWiki;
	}

	/**
	 * @return the sourceById
	 */
	public Map<String, Map<String, Source>> getSourceById() {
		return sourceById;
	}

	/**
	 * @param sourceById
	 *          the sourceById to set
	 */
	public void setSourceById(Map<String, Map<String, Source>> sourceById) {
		this.sourceById = sourceById;
	}

	/**
	 * @return the sourceBySection
	 */
	public Map<String, Map<String, Source>> getSourceBySection() {
		return sourceBySection;
	}

	/**
	 * @param sourceBySection
	 *          the sourceBySection to set
	 */
	public void setSourceBySection(
			Map<String, Map<String, Source>> sourceBySection) {
		this.sourceBySection = sourceBySection;
	}

	/**
	 * construct a reference manager
	 * 
	 * @param sslWiki
	 */
	private ReferenceManager(SSLWiki sslWiki) {
		this.sslWiki = sslWiki;
		sourceById = new HashMap<String, Map<String, Source>>();
		sourceBySection = new HashMap<String, Map<String, Source>>();
		File cacheRoot = new File(WikiTask.getWikiTaskHome() + "pagecache");
		pageCache = new PageCache(cacheRoot, sslWiki);
		// FIXME with debugging of some time
		PageCache.debug = true;
	}

	/**
	 * add sources for all references
	 * 
	 * @throws Exception
	 */
	public synchronized void addSources() throws Exception {
		// avoid java.util.ConcurrentModificationException by copying list
		Collection<Reference> references = this.referenceByReferenceId.values();
		List<Reference> referenceList = new ArrayList<Reference>();
		referenceList.addAll(references);
		// now loop over references which might modify referenceByReferenceId
		for (Reference reference : referenceList) {
			if (!reference.isTarget) {
				addSources(reference);
			}
		}
	}

	/**
	 * add Sources from the given wiki with the given reference
	 * 
	 * @param reference
	 * @throws Exception
   */
	public synchronized void addSources(Reference reference) throws Exception {
		if (reference.referenceType != ReferenceType.INVALID) {
			if (reference.isAvailable()) {
				LOGGER.log(Level.INFO, "addSources already done for "
						+ reference.pageTitle);
			} else {
				// reread reference
				SourceExtractor sourceExtractor = new SourceExtractor(reference);
				Map<String, Source> sources = sourceExtractor.extractSourceTagContent();
				sourceById.put(sourceExtractor.reference.pageTitle, sources);
				// should we cache this reference?
				boolean docache = true;
				// if there is a single non-cached source we won't cache the whole
				// reference!
				if (sources == null) {
					LOGGER.log(Level.WARNING, "addSources find null sources with reference "
							+ reference.referenceType.toString() + " for " + reference.pageTitle);
				} else {
					for (Source source : sources.values()) {
						if (!source.cache) {
							docache = false;
						}
					}
					// FIXME - cache by session and or timeout?
					reference.setAvailable(true);
				}
			}
		} else {
			LOGGER.log(Level.WARNING, "addSources called with invalid reference "
					+ reference.referenceType.toString() + " for " + reference.pageTitle);
		}
	}

	/**
	 * get the section for the given reference
	 * 
	 * @param reference
	 * @return the Section
	 * @throws Exception
	 */
	public Section getSection(Reference reference) throws Exception {
		Section section = pageCache.getSectionByAnchor(reference.pageTitle,
				reference.normalizedAnchor);
		return section;
	}

	/**
	 * get the source for the given reference
	 * 
	 * @param reference
	 * @return the source
	 * @throws Exception
	 */
	public synchronized Source getSource(Reference reference) throws Exception {
		Source result = null;
		if (reference != null) {
			switch (reference.referenceType) {
			case ANCHOR:
				Section section = getSection(reference);
				if (section != null && section.sectionIndex > 0) {
					reference = new Reference(ReferenceType.CONTENT, section.sectionText);
					SourceExtractor sectionSourceExtractor = new SourceExtractor(
							reference);
					Map<String, Source> sourceTagContent = sectionSourceExtractor
							.extractSourceTagContent();
					if (sourceTagContent.size() != 1) {
						throw new IllegalArgumentException(reference.link + " has "
								+ sourceTagContent.size() + " <source> tag(s)");
					}
					Entry<String, Source> sourceEntry = sourceTagContent.entrySet()
							.iterator().next();
					result = sourceEntry.getValue();
					if (result == null) {
						throw new IllegalArgumentException(reference.link
								+ " has no section with anchor " + reference.anchor);
					} // if
					LOGGER.log(Level.INFO, "caching source " + result.id);
					Map<String, Source> cachedSourceBySectionMap = this.sourceBySection
							.get(reference.pageTitle);
					if (cachedSourceBySectionMap == null) {
						sourceBySection.put(reference.pageTitle, sourceTagContent);
					} else {
						cachedSourceBySectionMap.put(result.id, result);
					}
				} // if
				break;
			case ID:
				result = this.sourceById.get(reference.pageTitle).get(reference.id);
				break;
			default:
				LOGGER.log(Level.SEVERE, "getSource called for " + reference.id
						+ " with referenceType " + reference.referenceType
						+ " that is not implemented yet");
				// ignore
			} // switch
		}
		return result;
	}

	/**
	 * add the given reference
	 * 
	 * @param reference
	 */
	public void addReference(Reference reference) {
		this.referenceByReferenceId.put(reference.getReferenceId(), reference);
	}

	/**
	 * get or create the given Reference
	 * 
	 * @param wiki
	 * @param link
	 * @return the reference
	 * @throws Exception
	 */
	public Reference getReference(MediawikiApi wiki, String link)
			throws Exception {
		Reference reference = getReference(wiki, link, null);
		return reference;
	}

	/**
	 * get the reference for the given parameters
	 * 
	 * @param wiki
	 * @param pageTitle
	 * @param pageLink
	 * @return the Reference
	 * @throws Exception
	 */
	public Reference getReference(MediawikiApi wiki, String pageTitle,
			Reference pageLink) throws Exception {
		if (pageTitle == null || "".equals(pageTitle)) {
			return null;
		}
		Reference reference = new Reference(wiki, pageTitle, pageLink);
		Reference result = getReference(reference);
		return result;
	}

	/**
	 * get the given reference e.g. from "Topic#sidif"
	 * 
	 * @param link
	 * @return the Reference
	 * @throws Exception
	 */
	public Reference getReference(String link) throws Exception {
		Reference result = this.getReference(this.sslWiki, link);
		return result;
	}

	/**
	 * get the Reference by the given id
	 * 
	 * @param id
	 * @return the Reference for the given id
	 */
	public Reference getReferenceById(String id) {
		Reference result = this.referenceByReferenceId.get(id);
		return result;
	}

	/**
	 * get the reference from the cache
	 * 
	 * @param reference
	 * @return
	 * @throws Exception
	 */
	private Reference getReference(Reference reference) throws Exception {
		// try looking up the reference
		Reference result = this.referenceByReferenceId.get(reference
				.getReferenceId());
		if (result == null) {
			result = reference;
			switch (result.referenceType) {
			case ANCHOR:
			case PAGE:
				LOGGER.log(Level.INFO, "getting page content for " + result.pageTitle
						+ " from " + result.wiki.getSiteurl());
				// result.content = result.wiki.getPageContent(result.pageTitle);
				// FIXME activate page cache
				result.content = pageCache.getPageContent(result.pageTitle);
				break;
			default:
			}
			this.addReference(result);
		} else {
			LOGGER.log(Level.INFO, "getting reference " + reference.getReferenceId()
					+ " from ReferenceManager");
		}
		return result;
	}

	/**
	 * get the singleton reference manager
	 * 
	 * @param sslWiki
	 * 
	 * @return the ReferenceManager for the given sslWiki
	 */
	public static ReferenceManager get(SSLWiki sslWiki) {
		ReferenceManager rm = referenceManagerMap.get(sslWiki.getWikiid());
		if (rm == null) {
			rm = new ReferenceManager(sslWiki);
			referenceManagerMap.put(sslWiki.getWikiid(), rm);
		}
		return rm;
	}

	/**
	 * get the reference Manager by the given wiki id
	 * 
	 * @param wikiid
	 * @return the reference manager for the wiki with the given id
	 * @throws Exception
	 */
	public static ReferenceManager getByWikiId(String wikiid) throws Exception {
		ReferenceManager rm = referenceManagerMap.get(wikiid);
		if (rm == null) {
			LOGGER.log(Level.WARNING, "ReferenceManager for wikiid " + wikiid
					+ " not found - loading it from SSLWiki");
			SSLWiki lSslWiki = SSLWiki.ofId(wikiid);
			lSslWiki.login();
			rm = get(lSslWiki);
		}
		return rm;
	}

	/**
	 * reset the given SSL Wiki
	 * 
	 * @param sslWiki
	 * @return the ReferenceManager for which the reset has been done
	 */
	public static ReferenceManager reset(SSLWiki sslWiki) {
		// check the current reference Manager
		ReferenceManager rm = referenceManagerMap.get(sslWiki.getWikiid());
		// if there is one
		if (rm != null) {
			// remove it from the map
			referenceManagerMap.remove(sslWiki.getWikiid());
		}
		// get a new one
		rm = get(sslWiki);
		return rm;
	}

}
