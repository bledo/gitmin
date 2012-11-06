/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.bledo.gitmin.servlet;

//========================================================================
//Copyright 2006 Mort Bay Consulting Pty. Ltd.
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================


import co.bledo.logger.Logger;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

//-----------------------------------------------------------------------------
/**
 * CGI Servlet.
 *
 * The cgi bin directory can be set with the "cgibinResourceBase" init parameter
 * or it will default to the resource base of the context.
 *
 * The "commandPrefix" init parameter may be used to set a prefix to all
 * commands passed to exec. This can be used on systems that need assistance to
 * execute a particular file type. For example on windows this can be set to
 * "perl" so that perl scripts are executed.
 *
 * The "Path" init param is passed to the exec environment as PATH. Note: Must
 * be run unpacked somewhere in the filesystem.
 *
 * Any initParameter that starts with ENV_ is used to set an environment
 * variable with the name stripped of the leading ENV_ and using the init
 * parameter value.
 *
 * @author Julian Gosnell
 * @author Thanassis Papathanasiou - Some minor modifications for Jetty6 port
 */
@WebServlet(name="G", urlPatterns={"/g/*"})
public class G extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(G.class);

	private String _gitBackend = "/usr/lib/git-core/git-http-backend";
	private String _gitProjectRoot = "/home/ricardo/dev";
	private String _gitExportAll = "";


	private String _cmdPrefix;
	private EnvList _env;
	private boolean _ignoreExitState = true;
	
	/* ------------------------------------------------------------ */
	public void init() throws ServletException
	{
		_env=new EnvList();
		_cmdPrefix=getInitParameter("commandPrefix");
		
		
		File dir=new File(_gitProjectRoot);
		if (!dir.exists())
		{
			log.warn("CGI: CGI bin does not exist - "+dir);
			return;
		}
		
		if (!dir.canRead())
		{
			log.warn("CGI: CGI bin is not readable - "+dir);
			return;
		}
		
		if (!dir.isDirectory())
		{
			log.warn("CGI: CGI bin is not a directory - "+dir);
			return;
		}
		
		
		
		Enumeration<?> e=getInitParameterNames();
		while (e.hasMoreElements())
		{
			String n=(String)e.nextElement();
			if (n!=null&&n.startsWith("ENV_"))
				_env.set(n.substring(4),getInitParameter(n));
		}
		if(!_env.envMap.containsKey("SystemRoot"))
		{
			String os = System.getProperty("os.name");
			if (os!=null && os.toLowerCase().indexOf("windows")!=-1)
			{
				String windir = System.getProperty("windir");
				_env.set("SystemRoot", windir!=null ? windir : "C:\\WINDOWS");
			}
		}
	}
	
	/* ------------------------------------------------------------ */
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		File exe = new File(_gitBackend);
		
		if (!exe.exists()||exe.isDirectory())
		{
			res.sendError(404);
			res.getWriter().print("<h1>git backend not found</h1>");
		}
		else
		{
			req.setAttribute( "XXX", req.getMethod() + " " + req.getRequestURI() + "?" + req.getQueryString());
			log.debug("####### REQUEST " + req.getAttribute("XXX"));
			exec(exe,req,res);
			//System.out.println("####### END REQUEST " + req.getRequestURI());
		}
	}
	
	/* ------------------------------------------------------------ */
	/*
	 * @param root @param path @param req @param res @exception IOException
	 */
	private void exec(File command, HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		String pathInfo = req.getPathInfo();
		String path=command.getAbsolutePath();
		File dir=command.getParentFile();
		String scriptName = req.getRequestURI().substring(0,req.getRequestURI().length() - pathInfo.length());
		String scriptPath=getServletContext().getRealPath(scriptName);
		String pathTranslated=req.getPathTranslated();

		
		int len=req.getContentLength();
		if (len<0) { len=0; }
		if ((pathTranslated==null)||(pathTranslated.length()==0))
		{
			pathTranslated=path;
		}
		
		EnvList env=new EnvList(_env);
		// these ones are from "The WWW Common Gateway Interface Version 1.1"
		// look at :
		// http://Web.Golux.Com/coar/cgi/draft-coar-cgi-v11-03-clean.html#6.1.1
		env.set("AUTH_TYPE",req.getAuthType());
		env.set("CONTENT_LENGTH",Integer.toString(len));
		env.set("CONTENT_TYPE",req.getContentType());
		env.set("GATEWAY_INTERFACE","CGI/1.1");
		if ((pathInfo!=null)&&(pathInfo.length()>0))
		{
			env.set("PATH_INFO",pathInfo);
		}

		log.info("path:{0}; path_translated:{1}; path_info:{2};", new Object[]{path, pathTranslated, pathInfo});

		env.set("PATH_TRANSLATED",pathTranslated);
		env.set("QUERY_STRING",req.getQueryString());
		env.set("REMOTE_ADDR",req.getRemoteAddr());
		env.set("REMOTE_HOST",req.getRemoteHost());
		// The identity information reported about the connection by a
		// RFC 1413 [11] request to the remote agent, if
		// available. Servers MAY choose not to support this feature, or
		// not to request the data for efficiency reasons.
		// "REMOTE_IDENT" => "NYI"
		env.set("REMOTE_USER",req.getRemoteUser());
		env.set("REQUEST_METHOD",req.getMethod());
		env.set("SCRIPT_NAME",scriptName);
		env.set("SCRIPT_FILENAME",scriptPath);
		env.set("SERVER_NAME",req.getServerName());
		env.set("SERVER_PORT",Integer.toString(req.getServerPort()));
		env.set("SERVER_PROTOCOL",req.getProtocol());
		env.set("SERVER_SOFTWARE",getServletContext().getServerInfo());

		env.set("SERVER_SOFTWARE",getServletContext().getServerInfo());



		env.set("GIT_PROJECT_ROOT", _gitProjectRoot);
		env.set("GIT_HTTP_EXPORT_ALL",_gitExportAll);

		
		Enumeration<?> enm=req.getHeaderNames();
		while (enm.hasMoreElements())
		{
			String name=(String)enm.nextElement();
			String value=req.getHeader(name);
			env.set("HTTP_"+name.toUpperCase().replace('-','_'),value);
		}
		
		// these extra ones were from printenv on www.dev.nomura.co.uk
		env.set("HTTPS",(req.isSecure()?"ON":"OFF"));
		// "DOCUMENT_ROOT" => root + "/docs",
		// "SERVER_URL" => "NYI - http://us0245",
		// "TZ" => System.getProperty("user.timezone"),



		//System.out.println(env.toString());
		
		// are we meant to decode args here ? or does the script get them
		// via PATH_INFO ? if we are, they should be decoded and passed
		// into exec here...
		String execCmd=path;
		if ((execCmd.charAt(0)!='"')&&(execCmd.indexOf(" ")>=0))
		{
			execCmd="\""+execCmd+"\"";
		}
		if (_cmdPrefix!=null)
		{
			execCmd=_cmdPrefix+" "+execCmd;
		}
		
		//log.warning("exec : " + execCmd);
		final Process p = Runtime.getRuntime().exec(execCmd, env.getEnvArray(), dir);
		
		// hook processes input to browser's output (async)
		final InputStream inFromReq=req.getInputStream();
		final OutputStream outToCgi=p.getOutputStream();
		

		MultiOutputStream mIn = new MultiOutputStream();
		mIn.add(outToCgi);
		mIn.add(System.out);

		
		IOUtils.copy(inFromReq, mIn);
		IOUtils.closeQuietly(outToCgi);
		/*
		new Thread(new Runnable()
		{
			@Override public void run()
			{
				if (inLength>0)
				{
					try {
						//IOUtils.copy(inFromReq, outToCgi);
						IOUtils.copy(inFromReq, outToCgi);
					} catch (IOException ex) {
						Logger.getLogger(G.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				IOUtils.closeQuietly(outToCgi);
			}
		}).start();
		* */

		// hook processes output to browser's input (sync)
		// if browser closes stream, we should detect it and kill process...
		res.reset();
		OutputStream os = null;
		MultiOutputStream  mOut = new MultiOutputStream();
		try
		{
			//IOUtils.copy(p.getInputStream(), System.out);
			//IOUtils.copy(p.getErrorStream(), System.err);
			// read any headers off the top of our input stream
			// NOTE: Multiline header items not supported!
			String line=null;
			InputStream inFromCgi=p.getInputStream();
			
			//br=new BufferedReader(new InputStreamReader(inFromCgi));
			//while ((line=br.readLine())!=null)
			while( (line = getTextLineFromStream( inFromCgi )).length() > 0 )
			{
				if (!line.startsWith("HTTP"))
				{
					int k=line.indexOf(':');
					if (k>0)
					{
						String key=line.substring(0,k).trim();
						String value = line.substring(k+1).trim();
						if ("Location".equals(key))
						{
							res.sendRedirect(value);
						}
						else if ("Status".equals(key))
						{
							String[] token = value.split( " " );
							int status=Integer.parseInt(token[0]);
							res.setStatus(status);
						}
						else
						{
							// add remaining header items to our response header
							res.addHeader(key,value);
						}
					}
				}
			}
			// copy cgi content to response stream...
			os = res.getOutputStream();
			mOut.add(os);
			mOut.add(System.out);
			IOUtils.copy(inFromCgi, mOut);
			//System.out.println("## ERR : ");
			StringWriter sw = new StringWriter();
			IOUtils.copy(p.getErrorStream(), sw);
			sw.flush();
			log.debug("####  " + req.getAttribute("XXX") + ":::\n" + sw.toString());
			p.waitFor();
			
			if (!_ignoreExitState)
			{
				int exitValue=p.exitValue();
				if (0!=exitValue)
				{
					log.warn("Non-zero exit status ({0}) from CGI program: {1}", new Object[]{exitValue, path});
					if (!res.isCommitted()) {
						res.sendError(500,"Failed to exec CGI");
					}
				}
			}
		}
		catch (IOException e)
		{
			// browser has probably closed its input stream - we
			// terminate and clean up...
			log.debug("CGI: Client closed connection!");
		}
		catch (InterruptedException ie)
		{
			log.debug("CGI: interrupted!");
		}
		finally
		{
			if( os != null )
			{
				try
				{
					os.close();
				}
				catch(Exception e)
				{
					log.error("{0}", e);
				}
			}
			os = null;
			p.destroy();
			// Log.debug("CGI: terminated!");
		}
	}
	
	/**
	 * Utility method to get a line of text from the input stream.
	 * @param is the input stream
	 * @return the line of text
	 * @throws IOException
	 */
	private String getTextLineFromStream( InputStream is ) throws IOException {
		StringBuffer buffer = new StringBuffer();
		int b;
		
		while( (b = is.read()) != -1 && b != (int) '\n' ) {
			buffer.append( (char) b );
		}
		return buffer.toString().trim();
	}
	/* ------------------------------------------------------------ */
	/**
	 * private utility class that manages the Environment passed to exec.
	 */
	private static class EnvList
	{
		private Map<String, String> envMap;
		
		EnvList()
		{
			envMap=new HashMap<String, String>();
		}
		
		EnvList(EnvList l)
		{
			envMap=new HashMap<String, String>(l.envMap);
		}
		
		/**
		 * Set a name/value pair, null values will be treated as an empty String
		 */
		public void set(String name, String value)
		{
			envMap.put(name,name+"="+StringUtils.trimToEmpty(value));
		}
		
		/** Get representation suitable for passing to exec. */
		public String[] getEnvArray()
		{
			return (String[])envMap.values().toArray(new String[envMap.size()]);
		}
		
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			for (String key : envMap.keySet())
			{
				sb.append(key);
				sb.append("=");
				sb.append(envMap.get(key));
				sb.append("\n");
			}
			return sb.toString();
		}
	}

	public static class MultiOutputStream extends OutputStream
	{
		List<OutputStream> streams = new ArrayList<OutputStream>();

		public void add(OutputStream s)
		{
			streams.add(s);
		}

		@Override
		public void write(int i) throws IOException {
			for (OutputStream s : streams)
			{
				s.write(i);
			}
		}
	}
}
