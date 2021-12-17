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

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * MySQL Storage adapter
 * 
 * @author wf
 * 
 */
public class MySQLConnection extends JDBCConnection  {
	

	/**
	 * construct with default port 3306
	 */
	public MySQLConnection() {
		super();
		setPort(3306);
	}

	@Override
	public boolean connect(String connectionString) throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// fix me - make available via Rest ...
			setConnection(DriverManager.getConnection(connectionString));
		} catch (SQLException ex) {
			// Fehler behandeln
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}
		return true;
	}

	@Override
	public boolean connect(String host, String dbname, String user,
			String password) throws Exception {
		// avoid Cannot convert value '0000-00-00 00:00:00' from column 3 to TIMESTAMP 
		String connectionString = "jdbc:mysql://" + host + ":"+getPort()+ "/" + dbname
				+ "?user=" + user + "&password=" + password+"&useFastDateParsing=false&zeroDateTimeBehavior=convertToNull&autoReconnect=true&useSSL=false";
		return connect(connectionString);
	}

}
