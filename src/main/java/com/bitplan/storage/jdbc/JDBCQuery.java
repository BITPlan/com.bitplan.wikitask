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
package com.bitplan.storage.jdbc;

import java.sql.ResultSet;
/*
 * Copyright (C) 2011 BITPlan GmbH

 Pater-Delp-Str. 1
 D-47877 Willich-Schiefbahn

 http://www.bitplan.com

 $HeadURL$
 $LastChangedDate$
 $LastChangedRevision$
 $LastChangedBy$
 $Id$
 */

/**
 * JDBC Query 
 * @author wf
 *
 */
public class JDBCQuery   {
	
	String entityName;
	
	/**
	 * create me for the given entityName
	 * @param entityName
	 */
	public JDBCQuery(String entityName) {
		this.entityName=entityName;
	}


	String sql;

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}
	
}
