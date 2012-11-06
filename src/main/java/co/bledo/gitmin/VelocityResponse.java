/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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


import co.bledo.Util;
import co.bledo.VelocityTplParser;
import co.bledo.mvc.Request;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author rxr
 */
public class VelocityResponse extends co.bledo.mvc.response.VelocityResponse
{
	public static VelocityResponse newInstance(Request req, String view)
	{
		// Get View
		String layout = "co/bledo/gitmin/servlet/view/template.vm";
		VelocityResponse resp = new VelocityResponse(layout, "co/bledo/gitmin/servlet/view/" + view);
		return resp;
	}

	public static VelocityResponse newInstance(Request req, Class<?> cls)
	{
		// Get View
		String view = null;
		String clsName = cls.getName();
		if (cls != null) {
			String[] clsNameParts = clsName.split("\\.");
			if ( clsNameParts.length > 0 )
			{
				view = "co/bledo/gitmin/servlet/view/" + clsNameParts[ clsNameParts.length - 1 ] + "." + req.getAction() + ".vm";
			}
		}

		String layout = "co/bledo/gitmin/servlet/view/template.vm";
		VelocityResponse resp = new VelocityResponse(layout, view);
		return resp;
	}

	@Override
	public void printBody(Request req, HttpServletResponse resp) throws Exception
	{
		_CONTENTTOKEN = "CONTENT";
		
		// Menu bar
		Boolean logged_in = (Boolean) req.getSession().getAttribute("logged_in");
		if (logged_in == null) { logged_in = false; }
		VelocityTplParser vp = new VelocityTplParser();
		if (logged_in) {
			assign("NAVBAR", vp.fetch("co/bledo/gitmin/servlet/view/_nav_bar.vm"));
		} else {
			assign("NAVBAR", vp.fetch("co/bledo/gitmin/servlet/view/_nav_bar_login.vm"));
		}
		
		// Alert messages
		Map<String, String> alert_messages = Gitmin.session.getAlertMessages(req);
		vp.assign("_alert_messages", alert_messages);
		assign("ALERT_MESSAGES", vp.fetch("co/bledo/gitmin/servlet/view/_alert_messages.vm"));
		
		
		assign("TITLE", "Gitmin");
		assign("request", req);
		
		//assign("auth", req.getSession().getAttribute("is_logged"));
		//assign("base", req.getContextPath());
		assign("util", Util.class);
		assign("gitmin", Gitmin.class);
		//String user = (String) req.getSession().getAttribute("username");
		//assign("username", user);
		//assign("mytickets", req.getContextPath() + "/Index/mytickets" );
		super.printBody(req, resp);
	}

	public VelocityResponse(String layout, String view) { super(layout, view); }
}
