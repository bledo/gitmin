package co.bledo.gitmin;
/*
 *
 * Copyright 2012 The ClickPro.com LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/


import co.bledo.Util;
import co.bledo.gitmin.db.DbException;
import co.bledo.gitmin.db.NotFoundException;
import co.bledo.gitmin.db.User;
import co.bledo.logger.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GitminStorage
{
	public Logger log = Logger.getLogger(GitminStorage.class);
	
	private boolean __init = false;
	private synchronized void _init() throws ClassNotFoundException
	{
		if (!__init)
		{
			log.info("initializing database...");
			Class.forName( GitminConfig.getDbDriver() );

			Connection conn = null;
			try {
				String connUrl = GitminConfig.getDbUrl();
				log.debug("connecting to {0}", connUrl);
				conn = DriverManager.getConnection( connUrl );
				log.info("running initial DB queriies...");
				for (String sql : GitminConfig.getDbInitQueries())
				{
					log.debug("...query : {0}", sql);
					PreparedStatement stmt = conn.prepareStatement(sql);
					stmt.execute();
					Util.closeQuietly(stmt);
				}
			} catch (SQLException e) {
				log.error("{0}", e);
			}
			finally
			{
				Util.closeQuietly(conn);
			}
			__init = true;
		}
	}

	private Connection getConnection() throws ClassNotFoundException, SQLException
	{
		if (__init == false) { _init(); }
		Connection conn = DriverManager.getConnection( GitminConfig.getDbUrl() );
		return conn;
	}

	public User userAuth(String user, String pass) throws NotFoundException, DbException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet res = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM user WHERE (username = ? or email = ?) AND `password` = ?");
			stmt.setString(1, user);
			stmt.setString(2, user);
			stmt.setString(3, Util.md5( pass ) );

			if ( !stmt.execute() ) {
				throw new NotFoundException( "Record not found" );
			}

			res = stmt.getResultSet();
			User usr = new User(res);
			return usr;
		}
		catch(Exception e)
		{
			throw new DbException(e);
		}
		finally
		{
			Util.closeQuietly(stmt);
			Util.closeQuietly(conn);
		}
	}
}
