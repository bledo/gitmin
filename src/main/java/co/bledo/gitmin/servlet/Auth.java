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
import co.bledo.gitmin.GitminSession;
import co.bledo.gitmin.GitminStorage;
import static co.bledo.gitmin.Gitmin._;
import co.bledo.gitmin.VelocityResponse;
import co.bledo.gitmin.db.DbException;
import co.bledo.gitmin.db.NotFoundException;
import co.bledo.gitmin.db.User;
import co.bledo.mvc.Cookie;
import co.bledo.mvc.Request;
import co.bledo.mvc.response.Redirect;
import co.bledo.mvc.response.Response;
import javax.servlet.annotation.WebServlet;



@WebServlet(name = "Auth", urlPatterns = {"/Auth/*"})
public class Auth extends BaseServlet
{
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(Auth.class);
	
	private static final long serialVersionUID = 1L;
	
	//private static final Logger log = Logger.getLogger(Auth.class);
	
	public Response index(Request req)
	{
		log.entry();

		Response resp = login(req);

		return log.exit(resp);
	}
	
	public final Response login(Request req)
	{
		log.entry();

		VelocityResponse resp = VelocityResponse.newInstance(req, "Auth.login.vm");
		
		resp.assign("username", req.getParam("username",""));
		
		String remember = req.getCookie("login_remember","0");
		if ("1".equals(remember)) {
			resp.assign("checked", "checked");
		} else {
			resp.assign("checked", "unchecked");
		}
		
		return log.exit(resp);
	}
	
	public final Response dologin(Request req)
	{
		log.entry();

		
		Boolean isLogged = GitminSession.isLogged(req);
		if (isLogged)
		{
			return log.exit(new Redirect(req.getContextPath() + "/Index"));
		}
		
		User user;
		try {
			String username = req.getParam("username");
			String password = req.getParam("password");
			String remember = req.getParam("login_remember", "0");
			user = GitminStorage.userAuth(username, password);
			/*
			if (!user.active)
			{
				log.warn("user {0} is inactive", user.email);
				Gitmin.alertError(req, _(req, "login.inactive.account"));
				return login(req);
			} else {
			}
			*/
			
			log.info("login of {} successful", user.email);
			Redirect resp = new Redirect( GitminSession.getWelcomeUrl(req) ); 
			GitminSession.login(req, user);  // login
			
			// remember button is clicked
			if ("1".equals(remember))
			{
				Cookie cookie = new Cookie("login_remember", "1");
				cookie.setPath(req.getContextPath() + "/Login/");
				resp.addCookie(cookie);
			}
			return log.exit( resp );
		} catch (DbException e) {
			log.catching(e);
			Gitmin.alertError(req, _(req, "login.auth.internal.error"));
			return log.exit( login(req) );
		} catch (NotFoundException e) {
			log.catching(e);
			Gitmin.alertError(req, _(req, "login.auth.error"));
			return log.exit( login(req) );
		}
	}
}
