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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * ResultSetDelegate (aka Adapter) that implements the DataSource interface
 * goal: avoid dependency to java.sql in package
 * 
 * See <a href="http://warren.mayocchi.com/2006/10/13/jdbc-resultset-mapper/">Warren Mayocchi Blog - JDBC ResultSet Mapper</a>
 * See <a href="http://resultsetmapper.sourceforge.net">ResultSetMapper on sourceforge</a>
 * 
 * modified by:
 *  
 * @author wf
 */
public class JDBCDataSource implements DataSource {
	protected ResultSet rs;

	/**
	 * construct me from the given resultSet
	 * @param rs
	 */
	public JDBCDataSource(ResultSet rs) {
		this.rs = rs;
	}

	@Override
	public String getString(String column) throws Exception {
		return rs.getString(column);
	}

	@Override
	public short getShort(String column) throws Exception {
		return rs.getShort(column);
	}

	@Override
	public int getInt(String column) throws Exception {
		return rs.getInt(column);
	}

	@Override
	public long getLong(String column) throws Exception {
		return rs.getLong(column);
	}

	@Override
	public Date getTimestamp(String column) throws Exception {
		Timestamp dt = rs.getTimestamp(column);
		Date result = null;
		if (dt != null)
			result = new Date(dt.getTime());
		return result;
	}

	@Override
	public Date getDate(String column) throws Exception {
		Date result = rs.getDate(column);
		return result;
	}

	@Override
	public boolean getBoolean(String column) throws SQLException {
		boolean result = rs.getBoolean(column);
		return result;
	}

	@Override
	public double getDouble(String column) throws Exception {
		double result = rs.getDouble(column);
		return result;
	}

	@Override
	public char getChar(String column) throws Exception {
		String s = rs.getString(column);
		char result = 0;
		if (s != null)
			result = s.charAt(0);
		return result;
	}
	
	@Override
	public void close() throws Exception {
		rs.getStatement().close();
		rs.close();
	}

	@Override
	public boolean next() throws Exception {
		boolean result=rs.next();
		return result;
	}
	
	/**
	 * get the column keys
	 * @return - the keys
	 * @throws Exception
	 */
	public List<String> keys() throws Exception {
	  List<String> result=new ArrayList<String>();
	  ResultSetMetaData md = rs.getMetaData();
	  for (int col=1;col<=md.getColumnCount();col++) {
	    result.add(md.getColumnName(col));
	  }
	  return result;
	}

}
