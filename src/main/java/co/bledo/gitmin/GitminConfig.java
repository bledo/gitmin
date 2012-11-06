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


import co.bledo.BledoProperties;
import co.bledo.Util;
import co.bledo.logger.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GitminConfig
{
	public static Logger log = Logger.getLogger(GitminStorage.class);


	private static BledoProperties _props = null;
	private static synchronized void _init()
	{
		if (_props == null)
		{
			Properties props = null;
			try {
				props = Util.loadProperties("gitmin");
			} catch (IOException e) {
				props = new Properties();
				log.error("{0}", e);
			}
			_props = new BledoProperties(props);
		}
	}

	private static BledoProperties getProps()
	{
		if (_props == null) { _init(); }
		return _props;
	}

	private static String getProp(String key)
	{
		if (_props == null) { _init(); }
		return _props.get(key);
	}

	public static String getGitRepositoriesPath()
	{
		return getProp(Keys.git_repositories_paths);
	}

	public static int getGitSshPort()
	{
		return 2222;
	}

	public static String getGitExportAll()
	{
		return getProp(Keys.git_export_all);
	}

	public static String getDbDriver()
	{
		return getProp("gitmin.db.driver");
	}

	public static String getDbUrl()
	{
		return getProp("gitmin.db.url");
	}

	public static String getDbAuthQuery()
	{
		return getProp("gitmin.db.auth.query");
	}

	public static List<String> getDbInitQueries()
	{
		List<String> list = new ArrayList<String>();
		BledoProperties initProps = getProps().propPath("gitmin.db.auth.init");
		for (String k : initProps.getRootKeys())
		{
			String val = initProps.get(k);

			log.debug("pulling db init query {0}", k, val);
			list.add( val );
		}
		return list;
	}
};
