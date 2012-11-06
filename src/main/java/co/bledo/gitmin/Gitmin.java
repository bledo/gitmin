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


import co.bledo.mvc.Request;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.http.HttpSession;
//import bledo.logger.Logger;

public class Gitmin
{
	//private static final Logger log = Logger.getLogger(Gitmin.class);
	
	public static String _(Request req, String key, Object...args)
	{
		ResourceBundle rb = (ResourceBundle) req.getAttribute( Keys.req_attr_resource_bundle );
		if (rb == null) {
			rb = ResourceBundle.getBundle(Keys.resource_bundle, req.getLocale());
			req.setAttribute(Keys.req_attr_resource_bundle, rb);
		}
		
		String pattern = rb.getString(key);
		return MessageFormat.format(pattern, args);
	}
	
	private static void _setAlertMsg(Request req, String type, String msg)
	{
		HttpSession sess = req.getSession(true);
		@SuppressWarnings("unchecked")
		Map<String, String> msgs = (Map<String, String>) sess.getAttribute( Keys.session_alert );
		if (msgs == null)
		{
			msgs = new HashMap<String, String>();
			sess.setAttribute( Keys.session_alert, msgs);
		}
		msgs.put(msg, type);
	}
	
	public static void alertError(Request req, String msg) {
		_setAlertMsg(req, Keys.alert_error, msg);
	}
	
	public static void alertWarning(Request req, String msg) {
		_setAlertMsg(req, Keys.alert_warning, msg);
	}
	
	public static void alertInfo(Request req, String msg) {
		_setAlertMsg(req, Keys.alert_info, msg);
	}
	
	public static void alertSuccess(Request req, String msg) {
		_setAlertMsg(req, Keys.alert_success, msg);
	}
	
	
	public static GitminSession session = new GitminSession();
	public static GitminStorage storage = new GitminStorage();
	public static GitminConfig config = new GitminConfig();
}








