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


import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.transport.ServiceMayNotContinueException;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.eclipse.jgit.util.Base64;

import co.bledo.gitmin.GitminConfig;
import co.bledo.gitmin.GitminStorage;
import co.bledo.gitmin.db.DbException;
import co.bledo.gitmin.db.NotFoundException;
import co.bledo.gitmin.db.User;

public class Git extends org.eclipse.jgit.http.server.GitServlet
{
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(Git.class);
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		log.entry(req, res);
		String authhead = req.getHeader("Authorization");
		if (authhead != null)
		{
			String usernpass = new String(Base64.decode( authhead.substring(6) ));
			String username = usernpass.substring(0,usernpass.indexOf(":"));
			String password = usernpass.substring(usernpass.indexOf(":")+1);
			
			try {
				User user = GitminStorage.userAuth(username, password);
				
				log.info("calling parent service(req, res)");
				super.service(req, res);
			}
			catch (NotFoundException e)
			{
			
				//***We weren't sent a valid username/password in the header, so ask for one***
				//res.getWriter().println("<html><body><h1>Access Denied</h1></body></html>");
				res.setHeader("WWW-Authenticate","Basic realm=\"Authentication\"");
				res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "");
			} catch (DbException e) {
				throw log.throwing(new ServletException(e));
			}
		}
		else
		{
			//***We weren't sent a valid username/password in the header, so ask for one***
			res.setHeader("WWW-Authenticate","Basic realm=\"Authentication\"");
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "");
			//res.getWriter().println("<html><body><h1>Access Denied</h1></body></html>");
		}
		log.exit();
	}
	
	//base-path
	@Override
	public void init(final ServletConfig config) throws ServletException
	{
		log.entry(config);
		
		setRepositoryResolver(new RepositoryResolver<HttpServletRequest>() {
			
			protected RepositoryBuilder builder = new RepositoryBuilder();
			
			@Override
			public Repository open(HttpServletRequest arg0, String arg1) throws RepositoryNotFoundException, ServiceNotAuthorizedException, ServiceNotEnabledException, ServiceMayNotContinueException
			{
				log.debug("RESOLVING REPO: {}", arg1);
				
				File path = new File(GitminConfig.getGitRepositoriesPath() + "/" + arg1);
				Repository repo;
				try {
					repo = builder.setGitDir(path)
						.readEnvironment()
						.findGitDir()
						.build();
				} catch (IOException e) {
					log.catching(e);
					throw log.throwing(new RepositoryNotFoundException(path));
				}
				
				return log.exit(repo);
			}
		});
		
		
		super.init(
			new ServletConfig() {
				@Override
				public String getServletName() {
					return config.getServletName();
				}

				@Override
				public ServletContext getServletContext() {
					return config.getServletContext();
				}

				@Override
				public Enumeration<String> getInitParameterNames() {
					return config.getInitParameterNames();
				}

				@Override
				public String getInitParameter(String arg0) {
					if ("base-path".equals(arg0))
					{
						return GitminConfig.getGitRepositoriesPath();
					}
					else if ("eport-all".equals(arg0))
					{
						return GitminConfig.getGitExportAll();
					}

					return config.getInitParameter(arg0);
				}
			}
		);
		
		
		log.exit();
	}
}
