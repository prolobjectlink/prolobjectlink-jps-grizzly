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
import java.net.URI;

import javax.servlet.ServletRegistration;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.prolobjectlink.web.servlet.HomeServlet;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
public abstract class AbstractGrizzlyServer extends AbstractWebServer implements GrizzlyWebServer {

	private final HttpServer server;

	public AbstractGrizzlyServer(int serverPort) {
		super(serverPort);
		URI baseUri = URI.create("http://localhost:" + serverPort + "/");
		WebappContext context = new WebappContext("GrizzlyContext", baseUri.getRawPath());
		server = GrizzlyHttpServerFactory.createHttpServer(baseUri);
		HomeServlet home = new HomeServlet();
		ServletRegistration servletRegistration = context.addServlet(home.getClass().getName(), home);
		servletRegistration.addMapping("/*");
		context.deploy(server);
	}

	public final String getVersion() {
		return server.getServerConfiguration().getHttpServerVersion();
	}

	public final String getName() {
		return server.getServerConfiguration().getHttpServerName();
	}

	public final void start() {
		try {
			if (!server.isStarted()) {
				server.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final void stop() {
		server.shutdown();
	}

}
