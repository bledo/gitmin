package co.bledo.gitmin.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.bledo.Util;

public class Sqlite implements Storage
{
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(Sqlite.class);
	
	
	private String _connStr = "";
	
	public Sqlite() throws ClassNotFoundException
	{
		 Class.forName("org.sqlite.JDBC");
	}
	
	
	public void setUrl(String url)
	{
		this._connStr = url;
	}
	
	private Connection getConn() throws DbException
	{
		try {
			Connection conn = DriverManager.getConnection( _connStr );
			
			return conn;
		} catch (SQLException e) {
			throw log.throwing(new DbException(e));
		}
	}
	
	
	
	public void create(Object m) throws DbException
	{
		Connection conn = null;
		try
		{
			conn = getConn();
			
		} finally {
			Util.closeQuietly(conn);
		}
	}

	@Override
	public void userSave(User user) throws DbException
	{
		log.entry(user);
		
		Connection conn = null;
		PreparedStatement stmt = null;
		try
		{
			conn = getConn();
			stmt = conn.prepareStatement("SELECT * FROM user email = ?");
			stmt.setString(1, user.email);
			if ( stmt.execute() )
			{
				// update
				Util.closeQuietly(stmt);
				
				stmt = conn.prepareStatement("UPDATE user SET email = ?, pass = ? WHERE email = ?");
				stmt.setString(1, user.email);
				stmt.setString(2, user.password);
				stmt.setString(3, user.email);
				stmt.execute();
			}
			else
			{
				// insert
				Util.closeQuietly(stmt);
				
				stmt = conn.prepareStatement("INSERT INTO user (email, pass) VALUES (?, ?)");
				stmt.setString(1, user.email);
				stmt.setString(2, user.password);
				stmt.execute();
			}
		}
		catch(Exception e)
		{
			throw log.throwing(new DbException(e));
		}
		finally
		{
			Util.closeQuietly(stmt);
			Util.closeQuietly(conn);
		}
		
		log.exit();
	}

	@Override
	public User userFetch(String username) throws DbException, NotFoundException
	{
		log.entry(username);
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet res = null;
		try
		{
			conn = getConn();
			stmt = conn.prepareStatement("SELECT * FROM user WHERE email = ?");
			stmt.setString(1, username);
			if ( !stmt.execute() )
			{
				throw log.throwing(new NotFoundException());
			}
			
			res = stmt.getResultSet();
			if (!res.next())
			{
				throw new NotFoundException("User not found");
			}
			User user = new User();
			user.email = res.getString("email");
			user.password = res.getString("pass");
			return log.exit(user);
		}
		catch (SQLException e) {
			throw log.throwing(new DbException(e));
		}
		finally
		{
			Util.closeQuietly(stmt);
			Util.closeQuietly(conn);
		}
	}

	@Override
	public List<Repo> repositoryFetchAll() throws DbException
	{
		log.entry();
		
		List<Repo> list = new ArrayList<Repo>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet res = null;
		try
		{
			conn = getConn();
			stmt = conn.prepareStatement("SELECT * FROM repository");
			if ( stmt.execute() )
			{
				res = stmt.getResultSet();
				while (res != null && res.next())
				{
					Repo repo = new Repo();
					repo.name = res.getString("name");
					repo.url = res.getString("url");
					list.add(repo);
				}
			}
			
			return log.exit(list);
		}
		catch (SQLException e) {
			throw log.throwing(new DbException(e));
		}
		finally
		{
			Util.closeQuietly(stmt);
			Util.closeQuietly(conn);
		}
	}
}






