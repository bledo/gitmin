package co.bledo.gitmin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import co.bledo.gitmin.db.User;

public class GitminReview {

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(GitminReview.class);

	public static List<String> getReviewList(User user, String refFrom, String refTo)
	{
		log.entry(user, refFrom, refTo);
		Repository repo = null;
		List<String> list = new ArrayList<String>();
		try
		{
			RepositoryBuilder builder = new RepositoryBuilder();
			repo = builder.setGitDir(new File("/home/ricardo/dev/gitmin/.git"))
						.readEnvironment()
						.findGitDir()
						.build();
			
			Git git = new Git(repo);
			
			RevWalk walk = new RevWalk(repo);
			ObjectId from = repo.resolve(refFrom);  // refs/heads/master
			ObjectId to = repo.resolve(refTo); //refs/remotes/origin/master
			
			walk.markStart(walk.parseCommit(from));
			walk.markUninteresting(walk.parseCommit(to));
			
			for (RevCommit commit : walk)
			{
				list.add( commit.getName() );
			}
			
			walk.dispose();
			
			return log.exit(list);
			
		} catch (MissingObjectException e) {
			log.catching(e);
		} catch (IncorrectObjectTypeException e) {
			log.catching(e);
		} catch (IOException e) {
			log.catching(e);
		}
		finally
		{
			if (repo != null)
			{
				repo.close();
			}
		}
		return log.exit(list);
	}
	
	public static void cloneRepo(String url, String name) throws GitAPIException
	{
		log.entry(url, name);
		Repository repo = null;
		try
		{
			Git git = Git.cloneRepository()
				.setCloneAllBranches(true)
				.setBare(true)
				.setDirectory(new File( GitminConfig.getGitRepositoriesPath() + "/" + name))
				.setURI(url)
				.call();
				
		} catch (InvalidRemoteException e) {
			throw log.throwing(e);
		} catch (TransportException e) {
			throw log.throwing(e);
		} catch (GitAPIException e) {
			throw log.throwing(e);
		}
	}
}







