package co.bledo.gitmin.db;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import co.bledo.BledoProperties;
import co.bledo.Util;
import co.bledo.gitmin.GitminConfig;
/*
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
*/

public class Db
{
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(Db.class);
	
	
	public static Storage getStorageAdapter() throws DbException
	{
		log.entry();
		try {
			Storage storage = (Storage) Util.loadClass(GitminConfig.getStorageClass(), new Object[]{});
			BledoProperties props = GitminConfig.getStorageProps();
			for (String key : props.getRootKeys())
			{
				String val = props.get(key);
				log.info("Storage config {} : {}", key, val);
				Method method;
				try {
					method = storage.getClass().getMethod(key, String.class);
				} catch (Exception e) {
					log.catching(e);
					continue;
				}
				
				if (method == null) { continue; }
				try {
					method.invoke(storage, val);
				} catch (Exception e) {
					log.catching(e);
					continue;
				}
			}
			return log.exit(storage);
			
		} catch (Exception e) { throw log.throwing(new DbException(e)); }
	}
	
	/*

	
	private static DataSource _ds = null;
	
	private static synchronized DataSource _getDs() throws IOException
	{
		
		if (_ds != null) { return _ds; }

		MysqlDataSource vds = new MysqlConnectionPoolDataSource();
		vds.setServerName( GitminConfig.getDbHost() );
		vds.setUser( GitminConfig.getDbUser() );
		vds.setPassword( GitminConfig.getDbPassword() );
		vds.setDatabaseName(  GitminConfig.getDbDatabase() );

		_ds = vds;
		return _ds;
	}

	
	public static DataSource getDs() throws DbException
	{
		if (_ds != null) {
			return _ds;
		}
		try {
			return _getDs();
		} catch (IOException e) {
			throw log.throwing( new DbException(e) );
		}
	}

	
	public static Connection getConnection() throws DbException
	{
		try {
			return getDs().getConnection();
		} catch (SQLException e) {
			throw log.throwing(new DbException(e));
		} catch (DbException e) {
			throw e;
		}
	}
	*/
	
}











