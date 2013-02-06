package co.bledo.gitmin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import co.bledo.gitmin.db.User;

public class GitminReview {

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(GitminReview.class);

	public static List<String> getReviewList(User user, String refFrom, String refTo) throws AmbiguousObjectException, IOException, NoHeadException, GitAPIException
	{
		log.entry(user, refFrom, refTo);
		
		
		List<String> list = new ArrayList<String>();
		RepositoryBuilder builder = new RepositoryBuilder();
		Repository repository = builder.setGitDir(new File(""))
					.readEnvironment()
					.findGitDir()
					.build();
		/*
		//FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("/srv/git/webtd.git"))
		  .readEnvironment() // scan environment GIT_* variables
		  .findGitDir() // scan up the file system tree
		  .build();
		*/
		
		Git git = new Git(repository);
		
		for (RevCommit commit : git.log().call())
		{
			list.add( commit.getName() );
		}
		
		RevWalk walk = new RevWalk(repository);
		ObjectId from = repository.resolve(refFrom);  // refs/heads/master
		ObjectId to = repository.resolve(refTo); //refs/remotes/origin/master
		
		/*
		if (to == null)
		{
			log.info("to: ", to);
		}
		if (from == null)
		{
			log.info("from: ", from);
		}

		walk.markStart(walk.parseCommit(from));
		walk.markUninteresting(walk.parseCommit(to));
		*/
		
		for (RevCommit commit : walk)
		{
			list.add( commit.getName() );
		}
		
		walk.dispose();
		
		return log.exit(list);
	}

}







