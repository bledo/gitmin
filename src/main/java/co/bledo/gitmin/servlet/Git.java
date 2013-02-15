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


import co.bledo.gitmin.GitminConfig;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "Git", urlPatterns = {"/git/*"})
public class Git extends org.eclipse.jgit.http.server.GitServlet
{
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(Git.class);
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		String user = req.getRemoteUser();
		if (user == null)
		{
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			res.getWriter().println("<html><body><h1>Access Denied</h1></body></html>");
		}
		else
		{
			super.service(req, res);
		}
	}
	
	//base-path
	@Override
	public void init(final ServletConfig config) throws ServletException
	{
		log.entry(config);
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
