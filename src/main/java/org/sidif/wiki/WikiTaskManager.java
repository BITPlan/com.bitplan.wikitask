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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

/**
 * Launcher for WikiTasks
 * 
 * @author wf
 *
 */
public class WikiTaskManager {
	protected static Logger LOGGER = Logger.getLogger("org.sidif.wiki");
	// create a cached Thread pool
	// http://stackoverflow.com/questions/17957382/fixedthreadpool-vs-cachedthreadpool-the-lesser-of-two-evils
	// http://stackoverflow.com/questions/2733356/killing-thread-after-some-specified-time-limit-in-java
	// protected static ExecutorService executor = Executors.newCachedThreadPool();
	protected static ExecutorService executor = Executors.newSingleThreadExecutor();

	/**
	 * launcher for a Wiki Task
	 * 
	 * @param wikiTask
	 * @return the WikiTaskResult
	 * @throws Exception
	 */
	public WikiTaskResult run(WikiTask wikiTask) throws Exception {

    // execute the wikiTask in background
		Future<?> future = executor.submit(new FutureTask(wikiTask));
		while (!future.isDone()) {
			// Sleep 40 milliseconds - thats about TV refresh rate ...
			Thread.sleep(40);
	    // check whether there is a dialog available
	    if (wikiTask.dialogLink != null && wikiTask.dialogLink.isAvailable()) {
	      WikiTaskResult dialogResult = new WikiTaskResult(wikiTask);
	      try {
	        dialogResult.html = wikiTask.processTemplate(wikiTask.dialogLink,
	            "WikiTask");
	      } catch (Throwable th) {
	        dialogResult.throwable=th;
	      }
	      return dialogResult;
	    }
		}
		return wikiTask.result;
	}
	
	static WikiTaskManager instance=null;
	/**
	 * construct this wikiTask Manager
	 */
	private WikiTaskManager() {
		instance=this;
	}
	
	/**
	 * get the singleton for the WikiTask Manager
	 * @return the WikiTaskManager singleton
	 */
	public static WikiTaskManager getInstance() {
		if (instance==null) {
			instance=new WikiTaskManager();
		}
		return instance;
	}

}
