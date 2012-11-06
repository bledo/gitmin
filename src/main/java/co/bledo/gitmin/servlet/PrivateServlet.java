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
import co.bledo.mvc.Request;
import co.bledo.mvc.response.Redirect;
import co.bledo.mvc.response.Response;

public class PrivateServlet extends BaseServlet
{
	private static final long serialVersionUID = 1L;
	
	private static final co.bledo.logger.Logger log = co.bledo.logger.Logger.getLogger(PrivateServlet.class);

	@Override
	protected Response processRequest(Request req) throws Exception
	{
		Response resp = null;
		Boolean isLogged = Gitmin.session.isLogged(req);
		if (!isLogged)
		{
			log.error("{0} requires authentication...redirected to Auth/login", req.getUri());
			Gitmin.session.setWelcomeUrl(req, req.getUri());
			Gitmin.alertError(req, Gitmin._(req, "auth.required"));
			resp = new Redirect(req.getContextPath() + "/Auth");
		}
		else
		{
			resp = super.processRequest(req);
		}
		
		return resp;
	}
	
}
