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
 
package com.bitplan.storage.jdbc;

import java.util.Date;


/**
 * a DataSource - wrapper to hide style of DataSource
 * @author wf
 *
 */
public interface DataSource  {
	// accessor methods
	String getString(String string) throws Exception;
	int getInt(String string) throws Exception;
	long getLong(String string) throws Exception;
	Date getTimestamp(String string) throws Exception;
	Date getDate(String string) throws Exception;
	short getShort(String string) throws Exception;
	boolean getBoolean(String string) throws Exception;
	double getDouble(String string) throws Exception;
	char getChar(String string) throws Exception;
	
	boolean next() throws Exception;
	
	// lifecycle methods
	void close() throws Exception;
}
