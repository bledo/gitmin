package co.bledo.gitmin.servlet;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import co.bledo.Util;
import co.bledo.gitmin.GitminConfig;
import co.bledo.gitmin.GitminStorage;
import co.bledo.gitmin.VelocityResponse;
import co.bledo.gitmin.db.Repo;
import co.bledo.gitmin.git.CommitInfo;
import co.bledo.gitmin.git.GitListItem;
import co.bledo.mvc.Request;
import co.bledo.mvc.response.Response;


public class Review extends PrivateServlet
{
	private static final long serialVersionUID = 1L;
	
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(Review.class);

	public Response index(Request request) throws Exception
	{
		
		log.entry(request);
		
		DateFormat dformat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		
		List<Repo> repos = GitminStorage.getRepos();
		Map<String, List<GitListItem>> lists = new HashMap<String, List<GitListItem>>();
		for (Repo repo : repos)
		{
			Git git = Git.open(new File(GitminConfig.getGitRepositoriesPath() + "/" + repo.name));
			Iterable<RevCommit> commits = git.log().setMaxCount(20).call();
			List<GitListItem> list = new ArrayList<GitListItem>();
			for (RevCommit commit : commits)
			{
				log.debug(commit);
				GitListItem item = new GitListItem();
				item.email = commit.getAuthorIdent().getEmailAddress();
				item.name = commit.getAuthorIdent().getName();
				item.subject = commit.getShortMessage();
				item.gravatar = "http://www.gravatar.com/avatar/" + Util.md5( Util.trim(item.email).toLowerCase() ) + "?s=40";
				item.hash = commit.getName();
				item.date = dformat.format(new Date(commit.getCommitTime()));
				list.add(item);
			}
			
			lists.put(repo.name, list);
		}
		
		VelocityResponse resp = VelocityResponse.newInstance(request, this);
		resp.assign("lists", lists);
		
		
		return log.exit(resp);
	}
	
	public Response commit(Request req) throws IOException, GitAPIException
	{
		String repoName = req.getParam("repo", "");
		String hash = req.getParam("hash", "");
		
		Git git = Git.open(new File(GitminConfig.getGitRepositoriesPath() + "/" + repoName));
		
		/*
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		List<DiffEntry> diffTest = git.diff().setOutputStream(baos1)
				.setOldTree(getTreeIterator(git.getRepository(), hash))
				.setNewTree(getTreeIterator(git.getRepository(), hash + "^"))
				.call();
		System.out.println(baos1.toString());
		*/
		
		
		
		RepositoryBuilder builder = new RepositoryBuilder();
		Repository repo = builder.setGitDir(new File(GitminConfig.getGitRepositoriesPath() + "/" + repoName))
				.readEnvironment()
				.findGitDir()
				.build();
		
		
		
		
		RevWalk rw = new RevWalk(repo);
		
		ObjectId hashOid = repo.resolve(hash);
		
		RevCommit commit = rw.parseCommit(hashOid);
		RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
		
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DiffFormatter df = new DiffFormatter(baos);
		df.setRepository(repo);
		df.setDiffComparator(RawTextComparator.DEFAULT);
		df.setDetectRenames(true);
		List<DiffEntry> diffs = df.scan(parent, commit);
		List<CommitInfo> commitInfos = new ArrayList<CommitInfo>();
		for (DiffEntry diff : diffs) {
			
			CommitInfo nfo = new CommitInfo();
			
		    df.format(diff);
			
			nfo.diff = baos.toString();
			nfo.oldContents = getFileContent(repo, parent, diff.getOldPath());
			nfo.newContents = getFileContent(repo, parent, diff.getNewPath());
			nfo.newFile = diff.getNewPath();
			nfo.oldFile = diff.getOldPath();
			
		    commitInfos.add(nfo);
		}
		
		VelocityResponse resp = VelocityResponse.newInstance(req, this);
		resp.assign("nfolist", commitInfos);
		return log.exit(resp);
	}
	
	
	private String getFileContent(Repository repo, RevCommit commit, String path) throws MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException
	{
		// and using commit's tree find the path
		RevTree tree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repo);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(PathFilter.create(path));
		if (!treeWalk.next()) {
		  return "";
		}
		ObjectId objectId = treeWalk.getObjectId(0);
		ObjectLoader loader = repo.open(objectId);

		// and then one can use either
		//InputStream in = loader.openStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		loader.copyTo(out);
		return out.toString();
	}
	
	
	private AbstractTreeIterator getTreeIterator(Repository repo, String name)
			throws IOException {
		final ObjectId id = repo.resolve(name);
		if (id == null)
			throw new IllegalArgumentException(name);
		final CanonicalTreeParser p = new CanonicalTreeParser();
		final ObjectReader or = repo.newObjectReader();
		try {
			p.reset(or, new RevWalk(repo).parseTree(id));
			return p;
		} finally {
			or.release();
		}
	}
}














