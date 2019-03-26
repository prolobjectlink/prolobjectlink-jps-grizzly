/*-
 * #%L
 * prolobjectlink-jps-glassfish
 * %%
 * Copyright (C) 2019 Prolobjectlink Project
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
package org.prolobjectlink.web.platform;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.prolobjectlink.web.servlet.HomeServlet;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
public abstract class AbstractGlassfishServer extends AbstractWebServer implements GlassfishWebServer {

	WebappContext context;
	HttpServer httpServer;
	static URI BASE_URI = URI.create("http://localhost:8080/");

	public AbstractGlassfishServer(int serverPort) {
		super(serverPort);
		context = new WebappContext("Test", BASE_URI.getRawPath());
//		context.addListener(new WidowAnalyzeServletContextListener());
		FilterRegistration registration = context.addFilter("BaseFilter", new Filter() {

			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				PrintWriter out = response.getWriter();
				out.print("<h1>Hello World</h1>");
			}
		});
		registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE,
				DispatcherType.FORWARD, DispatcherType.ERROR), true, "/*");

		HomeServlet home = new HomeServlet();
		ServletRegistration servletRegistration = context.addServlet(home.getClass().getName(), home);
		servletRegistration.addMapping("/*");
		httpServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI);
		ServerConfiguration config = httpServer.getServerConfiguration();
		context.deploy(httpServer);
	}

	public final String getVersion() {
		return httpServer.getServerConfiguration().getHttpServerVersion();
	}

	public final String getName() {
		return httpServer.getServerConfiguration().getHttpServerName();
	}

	public final void start() {
		try {
			httpServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final void stop() {
		httpServer.shutdown();
	}

}
