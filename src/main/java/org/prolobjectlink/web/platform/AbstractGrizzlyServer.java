/*-
 * #%L
 * prolobjectlink-jps-grizzly
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
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.prolobjectlink.db.DatabaseDriver;
import org.prolobjectlink.db.DatabaseDriverFactory;
import org.prolobjectlink.db.jpa.spi.JPAPersistenceUnitInfo;
import org.prolobjectlink.db.util.JavaReflect;
import org.prolobjectlink.logging.LoggerConstants;
import org.prolobjectlink.logging.LoggerUtils;
import org.prolobjectlink.web.application.ControllerGenerator;
import org.prolobjectlink.web.application.GrizzlyControllerGenerator;
import org.prolobjectlink.web.application.GrizzlyModelGenerator;
import org.prolobjectlink.web.application.ModelGenerator;
import org.prolobjectlink.web.application.ServletUrlMapping;
import org.prolobjectlink.web.servlet.DatabaseServlet;
import org.prolobjectlink.web.servlet.ManagerServlet;
import org.prolobjectlink.web.servlet.WelcomeServlet;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
public abstract class AbstractGrizzlyServer extends AbstractWebServer implements GrizzlyWebServer {

	private HttpServer server;

	public AbstractGrizzlyServer(int serverPort) {
		super(serverPort);
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			String localHostAddr = localHost.getHostAddress();
			NetworkListener localHostListener = new NetworkListener("localhost", localHostAddr, serverPort);
			InetAddress loopback = InetAddress.getLoopbackAddress();
			String loopbackAddr = loopback.getHostAddress();
			NetworkListener loopbackListener = new NetworkListener("loopback", loopbackAddr, serverPort);
			URI baseUri = URI.create("http://" + localHostAddr + ":" + serverPort);
			WebappContext context = new WebappContext("GrizzlyContext");
			server = GrizzlyHttpServerFactory.createHttpServer(baseUri);
			server.addListener(localHostListener);
			server.addListener(loopbackListener);
			WelcomeServlet home = new WelcomeServlet();
			context.addServlet(home.getClass().getName(), home).addMapping("/welcome");
			DatabaseServlet db = new DatabaseServlet();
			context.addServlet(db.getClass().getName(), db).addMapping("/databases");
			ManagerServlet man = new ManagerServlet();
			context.addServlet(man.getClass().getName(), man).addMapping("/manager");

			// applications models
			ModelGenerator modelGenerator = new GrizzlyModelGenerator();
			List<PersistenceUnitInfo> units = modelGenerator.getPersistenceUnits();
			for (PersistenceUnitInfo unit : units) {
				try {
					DatabaseDriver databaseDriver = DatabaseDriverFactory.createDriver(unit);
					databaseDriver.createDatabase();
				} catch (SQLException e) {
					LoggerUtils.error(getClass(), LoggerConstants.SQL_ERROR, e);
				}
				JPAPersistenceUnitInfo jpaUnit = (JPAPersistenceUnitInfo) unit;
				String name = jpaUnit.getPersistenceProviderClassName();
				Class<?> cls = JavaReflect.classForName(name);
				Object object = JavaReflect.newInstance(cls);
				PersistenceProvider provider = (PersistenceProvider) object;
				provider.generateSchema(unit, unit.getProperties());
			}

			// applications controllers
			ControllerGenerator controllerGenerator = new GrizzlyControllerGenerator();
			List<ServletUrlMapping> mappings = controllerGenerator.getMappings();
			for (ServletUrlMapping servletUrlMapping : mappings) {
				Servlet servlet = context.createServlet(servletUrlMapping.getServlet().getClass());
				context.addServlet(servletUrlMapping.getServlet().getClass().getName(), servlet)
						.addMapping(servletUrlMapping.getMappingUrl());
			}

			context.deploy(server);
		} catch (UnknownHostException e) {
			LoggerUtils.error(getClass(), LoggerConstants.UNKNOWN_HOST, e);
		} catch (ServletException e) {
			LoggerUtils.error(getClass(), LoggerConstants.SERVLET_ERROR, e);
		}

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
			LoggerUtils.error(getClass(), LoggerConstants.IO, e);
		}
	}

	public final void stop() {
		server.shutdown();
	}

}
