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


import java.io.PrintWriter;
import java.io.StringWriter;

import co.bledo.Util;
import co.bledo.gitmin.VelocityResponse;
import co.bledo.mvc.BledoServlet;
import co.bledo.mvc.HttpError404;
import co.bledo.mvc.Request;
import co.bledo.mvc.response.Response;

public class BaseServlet extends BledoServlet
{
	private static final long serialVersionUID = 1L;
	
	/*
	@Override
	protected Response processRequest(Request request) throws Exception
	{
		Response resp = super.processRequest(request);
		return resp;
	}
	*/
	
	@Override
	protected Response processRequestError(Request request, Exception e) throws Exception
	{
		VelocityResponse vr = null;
		try
		{
			throw e;
		}
		catch(HttpError404 ex)
		{
			vr = VelocityResponse.newInstance(request, "_404.vm");
			vr.assign("ERROR", ex);
		}
		catch (Exception ex)
		{
			vr = VelocityResponse.newInstance(request, "_error.vm");
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			vr.assign("ERROR", sw.toString());
			Util.closeQuietly(pw);
			Util.closeQuietly(sw);
		}
		return vr;
	}
	
}
