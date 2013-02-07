package co.bledo.gitmin;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.junit.Test;

import co.bledo.gitmin.db.User;

public class GitTest {

	@Test
	public void testClone() throws GitAPIException
	{
		//GitminReview.cloneRepo("https://github.com/bledo/gitmin.git", "gitmin");
	}
	
	@Test
	public void test() throws AmbiguousObjectException, NoHeadException, IOException, GitAPIException {
		//fail("Not yet implemented");
		
		List<String> list = GitminReview.getReviewList(
			new User(),
			"refs/heads/master",
			"refs/remotes/origin/master"
		);
		
		Assert.assertNotNull(list);
		//Assert.assertTrue(!list.isEmpty());
		
		for (String str : list)
		{
			System.out.println(str);
		}
	}

}
