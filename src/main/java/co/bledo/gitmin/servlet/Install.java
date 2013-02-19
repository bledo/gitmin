package co.bledo.gitmin.servlet;

import javax.servlet.annotation.WebServlet;

import co.bledo.gitmin.GitminStorage;
import co.bledo.mvc.Request;
import co.bledo.mvc.response.Response;

@WebServlet(name = "Install", urlPatterns = {"/Install/*"})
public class Install extends PrivateServlet
{
	public Response index(Request req)
	{
		/*
		GitminStorage.query("DROP TABLE users" );
		GitminStorage.query("CREATE TABLE users (user_id integer primary key autoincrement, email string)" );
		GitminStorage.query("DROP TABLE commits" );
		GitminStorage.query("CREATE TABLE commits (commit_id integer primary key autoincrement, reviewed integer, hash string, author string, created date)" );
		GitminStorage.query("CREATE UNIQUE INDEX commits_hash_idx ON commits (hash)");
		GitminStorage.query("CREATE INDEX commits_reviewed_idx ON commits (reviewed)");
		
		GitminStorage.query("DROP TABLE reviews");
		GitminStorage.query("CREATE TABLE reviews (review_id integer primary key autoincrement, commit_id integer, user_id integer, note string, rate int, created date)");
		GitminStorage.query("CREATE INDEX reviews_commit_id_idx ON reviews (commit_id)");
	
	
		GitminStorage.query("DROP TABLE notes");
		GitminStorage.query("CREATE TABLE notes (note_id integer primary key autoincrement, commit_id integer, file string, user_id integer, note string, rate int, created date)");
		GitminStorage.query("CREATE INDEX notes_commit_id_rdx ON notes (commit_id)");
		return null;
		*/
		
		//git log --all --reverse --no-merges --pretty="INSERT INTO commits (hash, reviewed, author, created) VALUES ('%H', 1, '%ae', datetime(%ct, 'unixepoch'));" | sqlite3 data.sqlite3
		//#for i in `git log --all --pretty="%H"`; do
		//#       echo $i
		//#done
		return null;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


}
