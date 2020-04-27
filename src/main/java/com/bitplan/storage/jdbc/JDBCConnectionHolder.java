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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * JDBC ConnectionHolder
 * @author wf
 *
 */
public interface JDBCConnectionHolder {
	
	/**
	 * @return the connection
	 */
	public Connection getConnection();

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(Connection connection);
	
	/**
	 * @return the port
	 */
	public int getPort();

	/**
	 * @param port the port to set
	 */
	public void setPort(int port);

	
	/**
	 * connect
	 * @param connectionString
	 * @return true if connect was successful
	 * @throws Exception
	 */
	public boolean connect(String connectionString) throws Exception;

	/**
	 * connect
	 * @param host
	 * @param dbname
	 * @param user
	 * @param password
	 * @return  true if connect was successful
	 * @throws Exception
	 */
	public boolean connect(String host, String dbname, String user,
			String password) throws Exception;
	
	
	/**
	 * close the connection
	 * @throws SQLException
	 */
	public void close() throws SQLException;

}
