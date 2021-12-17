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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

/**
 * JDBC Storage adapter mostly used with MySQL
 * 
 * @author wf
 * 
 */
public class JDBCStorage implements JDBCConnectionHolder {
	@Inject
	protected Logger logger;
	protected JDBCConnectionHolder connectionHolder;
	protected String catalog = null;
	protected String tableNamePattern = null;
	
	protected static final boolean debug = false;

	/**
	 * set the connection holder
	 * 
	 * @param pConnectionHolder
	 */
	public JDBCStorage(JDBCConnectionHolder pConnectionHolder) {
		connectionHolder = pConnectionHolder;
	}

	/**
	 * close the given results
	 * 
	 * @param results
	 * @throws Exception
	 */
	public void close(DataSource results) throws Exception {
		results.close();
	}

	/**
	 * execute the given Query
	 * 
	 * @param sql
	 * @return the ResultSet
	 * @throws Exception
	 */
	public ResultSet executeQuery(String sql) throws Exception {
		// Statement resource handling!
		Statement stmt = getConnection().createStatement();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			return rs;
		} catch (Exception e) {
			if (logger!=null)
				logger.log(Level.SEVERE, "executeQuery failed for '" + sql + "'", e);
			throw e;
		}
	}

	/**
	 * execute the given query
	 * 
	 * @param squery the SQL query to execute
	 * @return the DataSource
	 * @throws Exception
	 */
	public DataSource executeQuery(JDBCQuery squery) throws Exception {
		String sql = squery.getSql();
		ResultSet rs = executeQuery(sql);
		JDBCDataSource result = new JDBCDataSource(rs);
		return result;
	}

	/**
	 * close
	 * 
	 * @throws Exception
	 */
	public void close() throws SQLException {
		getConnection().close();
	}

	/**
	 * execute the given update/insert statement
	 * @param sql
	 * @return -1 on failure else the statement.execUpdate result
	 * @throws Exception
	 */
	public int executeUpdate(String sql) throws Exception {
		int result = -1;
		try {
			Statement statement;
			statement = getConnection().createStatement();
			result = statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			throw new Exception("sql: '" + sql + "' failed - " + e.getMessage());
		}
		return result;
	}

	/**
	 * create the query
	 * 
	 * @param entityClassname
	 * @param sql
	 * @return the JDBCQuery
	 */
	public JDBCQuery createQuery(String entityClassname, String sql) {
		JDBCQuery query = new JDBCQuery(entityClassname);
		query.setSql(sql);
		return query;
	}

	/**
	 * create a query that looks for "records" that have the given entity type
	 * where the attribute with the given name has the given value if like is
	 * true than wildcard matching should be used
	 * 
	 * @param entityClassname
	 * @param name
	 * @param value
	 * @param like
	 * @return the JDBCQuery
	 */
	public JDBCQuery createQuery(String entityClassname, String name,
			String value, boolean like) {
		String sql = "select ";
		JDBCQuery result = createQuery(entityClassname, sql);
		return result;
	}

	/**
	 * get the Value for the insert String for the given columnType and value
	 * 
	 * @param columnType
	 * @param value
	 * @return the value
	 */
	public String getInsertValue(String columnType, Object value) {
		String valueString="null";
		if (value != null) {
			valueString = value.toString();
			if (value instanceof Boolean) {
        Boolean v=(Boolean)value;
        if (v.booleanValue()) {
        	valueString="1";
        } else {
        	valueString="0";
        }
			} else if (columnType.equals("date") || columnType.equals("datetime") || value instanceof Date) {
				String mysqlDateFormat = "yyyy-MM-dd HH:mm:ss";
				@SuppressWarnings("unused")
				// http://www.karaszi.com/SQLServer/info_datetime.asp
				// FIXME use format correctly
				String sqlServerFormat = "yyyyMMdd HH:mm:ss";
				SimpleDateFormat formatter = new SimpleDateFormat(mysqlDateFormat);
				valueString = "'" + formatter.format((Date) value) + "'";
			}	else	if (columnType.equals("text") || columnType.equals("varchar")) {
					valueString = "'" + valueString + "'";
			} else if (columnType.equals("numeric()identity")) {
				value = null;
			}
			if ((value instanceof Long) && (value.equals(Long.MIN_VALUE)))
				valueString = null;
		}
		return valueString;
	}

	@Override
	public Connection getConnection() {
		Connection result=this.connectionHolder.getConnection();
		if (result==null)
			throw new RuntimeException("no connection set");
		return result;
	}

	@Override
	public void setConnection(Connection connection) {
		this.connectionHolder.setConnection(connection);
	}

	@Override
	public boolean connect(String connectionString) throws Exception {
		boolean result = connectionHolder.connect(connectionString);
		return result;
	}

	@Override
	public boolean connect(String host, String dbname, String user,
			String password) throws Exception {
		boolean result = connectionHolder.connect(host, dbname, user, password);
		return result;
	}

	@Override
	public int getPort() {
		return connectionHolder.getPort();
	}

	@Override
	public void setPort(int port) {
		connectionHolder.setPort(port);
	}

}
