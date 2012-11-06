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


import co.bledo.gitmin.VelocityResponse;
import co.bledo.mvc.Request;
import co.bledo.mvc.response.Response;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;


@WebServlet(name = "Index", urlPatterns = {"/Index/*"})
public class Index extends PrivateServlet
{
	private static final long serialVersionUID = 1L;

	public Response index(Request request) throws IOException
	{
		VelocityResponse resp = VelocityResponse.newInstance(request, getClass());
		return resp;
	}
}
