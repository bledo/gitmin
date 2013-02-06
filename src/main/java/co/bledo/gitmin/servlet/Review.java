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

import java.io.IOException;

import javax.servlet.annotation.WebServlet;

import co.bledo.gitmin.GitminReview;
import co.bledo.gitmin.GitminSession;
import co.bledo.gitmin.VelocityResponse;
import co.bledo.mvc.Request;
import co.bledo.mvc.response.Response;


@WebServlet(name = "Repo", urlPatterns = {"/repo/*"})
public class Review extends PrivateServlet
{
	private static final long serialVersionUID = 1L;
	
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(Review.class);

	public Response index(Request request) throws Exception
	{
		log.entry(request);
		
		VelocityResponse resp = VelocityResponse.newInstance(request, this);
		
		resp.assign(
			"LIST_OPEN",
			GitminReview.getReviewList(
				GitminSession.getUser(request),
				"refs/heads/master",
				"refs/remotes/origin/master"
			)
		);
		
		return log.exit(resp);
	}
}














