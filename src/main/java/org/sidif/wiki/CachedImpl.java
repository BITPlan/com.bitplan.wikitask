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

import java.util.Date;
import java.util.logging.Logger;

/**
 * default implementation for cached items
 * @author wf
 *
 */
public class CachedImpl implements Cached {
	protected static Logger LOGGER = Logger.getLogger("org.sidif.wiki");
	Date lastestRead; // when was the reference read
	boolean available = false;
	
	/**
	 * @return the available
	 */
	public synchronized boolean isAvailable() {
		return available;
	}

	/**
	 * @param available
	 *          the available to set
	 */
	public synchronized void setAvailable(boolean available) {
		this.available = available;
	}
	
	/**
	 * remember when i was born
	 */
	public CachedImpl() {
		this.lastestRead=new Date();
	}

  //how old is the reference
	public long getAgeMillisecs() {
		Date now=new Date();
		long result=now.getTime()-this.lastestRead.getTime();
		return result;
	}
	
	/**
	 * get the age with unit
	 * @return
	 */
	public String getAge() {
		long age=getAgeMillisecs();
		String result=""+age+" msecs";
		if (age>1000) {
			age=age/1000;
			result=""+age+" secs";
			if (age>60) {
				age=age/60;
				result=""+age+" mins";
				if (age>60) {
			  	age=age/60;
				  result=""+age+" hours";
				}
				if (age>24) {
			  	age=age/24;
				  result=""+age+" days";
				}
			}
		}
		return result;
	}
	
}
