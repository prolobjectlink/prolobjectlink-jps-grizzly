/*-
 * #%L
 * prolobjectlink-jps-grizzly
 * %%
 * Copyright (C) 2012 - 2019 Prolobjectlink Project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
package org.prolobjectlink.web.servlet.admin;

import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prolobjectlink.logging.LoggerConstants;
import org.prolobjectlink.logging.LoggerUtils;
import org.prolobjectlink.web.function.AssetFunction;
import org.prolobjectlink.web.function.PathFunction;
import org.prolobjectlink.web.servlet.AbstractServlet;

import io.marioslab.basis.template.Template;
import io.marioslab.basis.template.TemplateContext;
import io.marioslab.basis.template.TemplateLoader;
import io.marioslab.basis.template.TemplateLoader.ClasspathTemplateLoader;

@WebServlet
public class RestartServlet extends AbstractServlet implements Servlet {

	private static final long serialVersionUID = 7313381101418470194L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Runtime runtime = Runtime.getRuntime();
		try {

			String lib = getLibDirectory().getCanonicalPath();
			String console = "org.prolobjectlink.db.prolog.jpl.swi.SwiPrologDatabaseConsole";
			runtime.exec("java -classpath " + lib + "/* " + console + " -m");
			runtime.exec("java -classpath " + lib + "/* " + console + " -j " + lib + "/prolobjectlink-jpx-model.jar");

			// reload prolobjectlink-jpx-model.jar
			// restart database

		} catch (IOException e) {
			LoggerUtils.error(getClass(), LoggerConstants.IO, e);
		}

		// request information
		String protocol = req.getScheme();
		String host = req.getHeader("host");

		// Engine and context
		TemplateLoader loader = new ClasspathTemplateLoader();
		Template template = loader.load("/org/prolobjectlink/web/html/manager.html");
		TemplateContext context = new TemplateContext();

		// java virtual machine
		context.set("jvm", System.getProperty("java.vm.name"));
		context.set("jvendor", System.getProperty("java.vendor"));
		context.set("jversion", System.getProperty("java.version"));

		// operating system
		context.set("osname", System.getProperty("os.name"));
		context.set("osversion", System.getProperty("os.version"));
		context.set("osarch", System.getProperty("os.arch"));

		// prolog engine
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("Prolog");
		ScriptEngineFactory factory = engine.getFactory();
		context.set("plversion", factory.getParameter(ScriptEngine.ENGINE_VERSION));
		context.set("plname", factory.getParameter(ScriptEngine.NAME));

		// servlet container
		context.set("serverversion", "4.1.1");
		context.set("servername", "Grizzly");

		// functions
		context.set("path", new PathFunction("pas", protocol, host));
		context.set("asset", new AssetFunction(protocol, host));

		// render
		template.render(context, resp.getOutputStream());

		// response
		resp.setStatus(HttpServletResponse.SC_OK);

		// response
		resp.setStatus(HttpServletResponse.SC_OK);
	}

}
