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


import java.util.List;

import co.bledo.Util;
import co.bledo.gitmin.db.Db;
import co.bledo.gitmin.db.DbException;
import co.bledo.gitmin.db.NotFoundException;
import co.bledo.gitmin.db.Repo;
import co.bledo.gitmin.db.Storage;
import co.bledo.gitmin.db.User;
/*
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
*/


public class GitminStorage
{
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(GitminStorage.class);
	
	private static Storage _store = null;
	private synchronized static void _init() throws DbException
	{
		log.entry();
		if (_store != null)
		{
			log.exit();
			return;
		}
		_store = Db.getStorageAdapter();
		log.exit();
	}

	public static User userAuth(String user, String pass) throws DbException, NotFoundException
	{
		_init();
		
		log.entry();
		if (pass == null) { pass = ""; }
		
		// password did not match
		User usr = _store.userFetch(user);
		if( !Util.md5(pass).equals(usr.password) )
		{
			log.debug("md5 {} != {}", usr.password, Util.md5(pass));
			throw log.throwing( new NotFoundException( "Record not found" ) );
		}
		
		return log.exit(usr);
	}

	public static List<Repo> getRepos() throws DbException
	{
		log.entry();
		
		List<Repo> repos = repos = _store.repositoryFetchAll();
		
		return log.exit(repos);
	}
}






