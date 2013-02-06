package co.bledo.gitmin;

import static org.junit.Assert.*;

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
	public void test() throws AmbiguousObjectException, NoHeadException, IOException, GitAPIException {
		//fail("Not yet implemented");
		
		List<String> list = GitminReview.getReviewList(
			new User(),
			"refs/heads/test",
			"refs/heads/master"
		);
		
		Assert.assertNotNull(list);
		Assert.assertTrue(!list.isEmpty());
		
		for (String str : list)
		{
			System.out.println(str);
		}
	}

}
