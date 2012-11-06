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


import co.bledo.gitmin.Gitmin;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "Auth", urlPatterns = {"/git/*"})
public class Git extends org.eclipse.jgit.http.server.GitServlet
{
	private static final long serialVersionUID = 1L;
	
	//base-path
	@Override
	public void init(final ServletConfig config) throws ServletException
	{
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
						return Gitmin.config.getGitRepositoriesPath();
					}
					else if ("eport-all".equals(arg0))
					{
						return Gitmin.config.getGitExportAll();
					}
					
					return config.getInitParameter(arg0);
				}
			}
		);
	}
}
