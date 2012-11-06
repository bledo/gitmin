package co.bledo.gitmin;
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


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import co.bledo.mvc.Request;
import co.bledo.gitmin.db.User;

public class GitminSession
{

	public void login(Request req, User user)
	{
		HttpSession sess = req.getSession(true);
		sess.setAttribute(Keys.session_user, user);
		sess.setAttribute(Keys.session_is_logged, new Boolean(true));
	}

	public void logout(Request req)
	{
		if (!isLogged(req)) { return; } // not logged, return
		req.getSession(true).setAttribute("is_logged", false);
	}

	public Boolean isLogged(Request req)
	{
		HttpSession sess =  req.getSession(true);
		Boolean logged = (Boolean) sess.getAttribute("is_logged");
		if (logged == null) {
			return false;
		}
		return logged;
	}

	public void setWelcomeUrl(Request req, String uri)
	{
		HttpSession sess = req.getSession(true);
		sess.setAttribute(Keys.session_welcomepage, uri);
	}

	public String getWelcomeUrl(Request req)
	{
		HttpSession sess = req.getSession(true);
		String uri = (String) sess.getAttribute(Keys.session_welcomepage);
		if (uri == null) {
			uri = req.getContextPath() + Keys.default_welcome_url;
		} else {
			sess.setAttribute(Keys.session_welcomepage, null);
		}
		return uri;
	}

	public Map<String, String> getAlertMessages(Request req)
	{
		HttpSession sess = req.getSession(true);

		@SuppressWarnings("unchecked")
		Map<String, String> msgs = (Map<String, String>) sess.getAttribute( Keys.session_alert );
		if (msgs == null)
		{
			msgs = new HashMap<String, String>();
		}
		sess.setAttribute(Keys.session_alert, new HashMap<String, String>());

		return msgs;
	}
}




