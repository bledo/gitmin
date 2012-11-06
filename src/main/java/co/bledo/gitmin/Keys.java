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


public class Keys
{
	
	public static String git_repositories_paths = "git.repositories.path";
	public static String git_export_all = "git.export.all";
		
	/**
	 * Resource bundle name.  The name of the resource bundle properties file
	 */
	public static String default_welcome_url = "/Index";
	
	/**
	 * Resource bundle name.  The name of the resource bundle properties file
	 */
	public static String resource_bundle = "Gitmin_messages";
	
	/**
	 * key to store resource bundle in request attributes
	 */
	public static String req_attr_resource_bundle = "resource_bundle";
	
	/**
	 * Session key to store welcome URL when user tries to 
	 * access URL without a valid session
	 */
	public static String session_welcomepage = "gitmin.welcomepage";
	
	/**
	 * Session key for user object
	 */
	public static String session_user = "user";
	
	/**
	 * Session key for logged flag
	 */
	public static String session_is_logged = "is_logged";
	
	
	/**
	 * Session key to store alert messages
	 */
	public static String session_alert = "alert-messages";
	
	public static String alert_error = "alert-error";
	public static String alert_warning = "alert-warning";
	public static String alert_info = "alert-info";
	public static String alert_success = "alert-success";
	public static String gitmin_db_river = "gitmin.db.driver";
	public static String gitmin_db_url = "gitmin.db.url";
}
