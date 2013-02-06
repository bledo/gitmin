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

import java.io.File;
import java.io.IOException;
/*
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
*/

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;


public class GitminStorage
{
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(GitminStorage.class);
	
	public static void query(String sql)
	{
	}
	
	/*
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
	*/
	
	
	private static Ini _ini = null;
	private static synchronized Ini getIniDb() throws InvalidFileFormatException, IOException
	{
		log.entry();
		if (_ini == null)
		{
			String filename = GitminConfig.getIniDbFile();
			File file = new File(filename);
			if (file.createNewFile())
			{
				log.info("No database ini file ... creating a default one");
				// new file
				_ini = new Ini( file );
				_ini.add("user", "admin", Util.md5("admin"));
				_ini.store();
			}
			else
			{
				// existing file
				_ini = new Ini( file );
			}

		}
		
		
		return log.exit(_ini);
	}

	public static User userAuth(String user, String pass) throws DbException, NotFoundException
	{
		log.entry();
		User usr = new User();
		try
		{
			Ini ini = getIniDb();
			
			String passInDb = ini.get("user", user);
			
			// record not found
			if (passInDb == null)
			{
					throw log.throwing( new NotFoundException( "Record not found" ) );
			}
			
			// password did not match
			if (!passInDb.equals(Util.md5(pass)))
			{
					throw log.throwing( new NotFoundException( "Record not found" ) );
			}
			
			usr.email = user;
			usr.password = passInDb;
		}
		catch (InvalidFileFormatException e) {
			throw log.throwing(new DbException(e));
		}
		catch (IOException e) {
			throw log.throwing(new DbException(e));
		}
		
		return log.exit(usr);
	}
}
