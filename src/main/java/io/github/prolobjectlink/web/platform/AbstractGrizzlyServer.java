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
package io.github.prolobjectlink.web.platform;

import java.io.IOException;
import java.net.BindException;
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
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import io.github.prolobjectlink.db.DatabaseDriver;
import io.github.prolobjectlink.db.DatabaseDriverFactory;
import io.github.prolobjectlink.db.DatabaseServer;
import io.github.prolobjectlink.db.jpa.spi.JPAPersistenceUnitInfo;
import io.github.prolobjectlink.db.server.HSQLServer;
import io.github.prolobjectlink.db.util.JavaReflect;
import io.github.prolobjectlink.logging.LoggerConstants;
import io.github.prolobjectlink.logging.LoggerUtils;
import io.github.prolobjectlink.web.application.ControllerGenerator;
import io.github.prolobjectlink.web.application.GrizzlyControllerGenerator;
import io.github.prolobjectlink.web.application.GrizzlyMiscellaneousLoader;
import io.github.prolobjectlink.web.application.GrizzlyModelGenerator;
import io.github.prolobjectlink.web.application.GrizzlyResourceLoader;
import io.github.prolobjectlink.web.application.ModelGenerator;
import io.github.prolobjectlink.web.application.ResourceLoader;
import io.github.prolobjectlink.web.application.ServletUrlMapping;
import io.github.prolobjectlink.web.servlet.admin.AboutServlet;
import io.github.prolobjectlink.web.servlet.admin.ApplicationServlet;
import io.github.prolobjectlink.web.servlet.admin.ConfigurationServlet;
import io.github.prolobjectlink.web.servlet.admin.ConnectorsServlet;
import io.github.prolobjectlink.web.servlet.admin.DatabaseServlet;
import io.github.prolobjectlink.web.servlet.admin.DeleteApplicationServlet;
import io.github.prolobjectlink.web.servlet.admin.DeleteDatabaseServlet;
import io.github.prolobjectlink.web.servlet.admin.DocumentsServlet;
import io.github.prolobjectlink.web.servlet.admin.ExportApplicationServlet;
import io.github.prolobjectlink.web.servlet.admin.ExportDatabaseServlet;
import io.github.prolobjectlink.web.servlet.admin.InformationServlet;
import io.github.prolobjectlink.web.servlet.admin.LogoutServlet;
import io.github.prolobjectlink.web.servlet.admin.LogsServlet;
import io.github.prolobjectlink.web.servlet.admin.ManagerServlet;
import io.github.prolobjectlink.web.servlet.admin.ModelServlet;
import io.github.prolobjectlink.web.servlet.admin.NotFoundServlet;
import io.github.prolobjectlink.web.servlet.admin.PersistenceServlet;
import io.github.prolobjectlink.web.servlet.admin.ProlobjectlinkServlet;
import io.github.prolobjectlink.web.servlet.admin.ProviderServlet;
import io.github.prolobjectlink.web.servlet.admin.RestartServlet;
import io.github.prolobjectlink.web.servlet.admin.RootServlet;
import io.github.prolobjectlink.web.servlet.admin.UploadApplicationServlet;
import io.github.prolobjectlink.web.servlet.admin.UploadDatabaseServlet;
import io.github.prolobjectlink.web.servlet.admin.UserServlet;
import io.github.prolobjectlink.web.servlet.admin.WelcomeServlet;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
public abstract class AbstractGrizzlyServer extends AbstractWebServer implements GrizzlyWebServer {

	private HttpServer httpserver;
	private DatabaseServer databaseServer = new HSQLServer();

	public AbstractGrizzlyServer(int serverPort) {
		super(serverPort);
		try {

			databaseServer.startup();

			InetAddress localHost = InetAddress.getLocalHost();
			String localHostAddr = localHost.getHostAddress();
			// PortRange portRange = new PortRange(serverPort, 9220);
			NetworkListener localHostListener = new NetworkListener("localhost", localHostAddr, serverPort);
			InetAddress loopback = InetAddress.getLoopbackAddress();
			String loopbackAddr = loopback.getHostAddress();
			NetworkListener loopbackListener = new NetworkListener("loopback", loopbackAddr, serverPort);
			URI baseUri = URI.create("http://" + localHostAddr + ":" + serverPort);
			httpserver = GrizzlyHttpServerFactory.createHttpServer(baseUri);
			httpserver.addListener(localHostListener);
			httpserver.addListener(loopbackListener);
			
			// Web context initialization
			GrizzlyWebContext context = new GrizzlyWebContext();

			// Manager Servlets Initialization
			RootServlet root = new RootServlet();
			context.addServlet(root.getClass().getName(), root).addMapping("/");
			WelcomeServlet home = new WelcomeServlet();
			context.addServlet(home.getClass().getName(), home).addMapping("/welcome");
			NotFoundServlet notFoundServlet = new NotFoundServlet();
			context.addServlet(notFoundServlet.getClass().getName(), notFoundServlet).addMapping("/notfound");
			ApplicationServlet app = new ApplicationServlet();
			context.addServlet(app.getClass().getName(), app).addMapping("/pas/applications");
			DatabaseServlet db = new DatabaseServlet();
			context.addServlet(db.getClass().getName(), db).addMapping("/pas/databases");
			ModelServlet m = new ModelServlet();
			context.addServlet(m.getClass().getName(), m).addMapping("/pas/models");
			ConfigurationServlet etc = new ConfigurationServlet();
			context.addServlet(etc.getClass().getName(), etc).addMapping("/pas/configuration");
			ConnectorsServlet conn = new ConnectorsServlet();
			context.addServlet(conn.getClass().getName(), conn).addMapping("/pas/connector");
			PersistenceServlet pers = new PersistenceServlet();
			context.addServlet(pers.getClass().getName(), pers).addMapping("/pas/persistence");
			ProviderServlet pro = new ProviderServlet();
			context.addServlet(pro.getClass().getName(), pro).addMapping("/pas/provider");
			LogsServlet logs = new LogsServlet();
			context.addServlet(logs.getClass().getName(), logs).addMapping("/pas/logs");
			ManagerServlet man = new ManagerServlet(databaseServer, this);
			context.addServlet(man.getClass().getName(), man).addMapping("/pas/manager");
			DocumentsServlet doc = new DocumentsServlet();
			context.addServlet(doc.getClass().getName(), doc).addMapping("/doc");
			ProlobjectlinkServlet pas = new ProlobjectlinkServlet(databaseServer, this);
			context.addServlet(pas.getClass().getName(), pas).addMapping("/pas/information");
			AboutServlet about = new AboutServlet();
			context.addServlet(about.getClass().getName(), about).addMapping("/pas/about");
			UserServlet users = new UserServlet();
			context.addServlet(users.getClass().getName(), users).addMapping("/pas/users");

			// operations on manager
			DeleteApplicationServlet delapp = new DeleteApplicationServlet();
			context.addServlet(delapp.getClass().getName(), delapp).addMapping("/pas/application/delete/*");
			DeleteDatabaseServlet deldb = new DeleteDatabaseServlet();
			context.addServlet(deldb.getClass().getName(), deldb).addMapping("/pas/database/delete/*");
			ExportApplicationServlet exapp = new ExportApplicationServlet();
			context.addServlet(exapp.getClass().getName(), exapp).addMapping("/pas/application/export/*");
			ExportDatabaseServlet exdb = new ExportDatabaseServlet();
			context.addServlet(exdb.getClass().getName(), exdb).addMapping("/pas/database/export/*");
			UploadApplicationServlet uploadapp = new UploadApplicationServlet();
			context.addServlet(uploadapp.getClass().getName(), uploadapp).addMapping("/pas/uploadapp/*");
			UploadDatabaseServlet uploaddb = new UploadDatabaseServlet();
			context.addServlet(uploaddb.getClass().getName(), uploaddb).addMapping("/pas/uploaddb/*");
			InformationServlet showdb = new InformationServlet();
			context.addServlet(showdb.getClass().getName(), showdb).addMapping("/pas/database/show/*");
			LogoutServlet logout = new LogoutServlet();
			context.addServlet(logout.getClass().getName(), logout).addMapping("/pas/logout");

			// server misc
			ResourceLoader miscGenerator = new GrizzlyMiscellaneousLoader();
			List<ServletUrlMapping> miscPaths = miscGenerator.getMappings();
			for (ServletUrlMapping servletUrlMapping : miscPaths) {
				Servlet servlet = servletUrlMapping.getServlet();
				context.addServlet(servletUrlMapping.getMappingUrl(), servlet)
						.addMapping(servletUrlMapping.getMappingUrl());
			}

			// server resources
			ResourceLoader resourcesGenerator = new GrizzlyResourceLoader();
			List<ServletUrlMapping> resourcespaths = resourcesGenerator.getMappings();
			for (ServletUrlMapping servletUrlMapping : resourcespaths) {
				Servlet servlet = servletUrlMapping.getServlet();
				context.addServlet(servletUrlMapping.getMappingUrl(), servlet)
						.addMapping(servletUrlMapping.getMappingUrl());
			}

			// server resources
			ResourceLoader documentsGenerator = new GrizzlyResourceLoader();
			List<ServletUrlMapping> documentspaths = documentsGenerator.getMappings();
			for (ServletUrlMapping servletUrlMapping : documentspaths) {
				Servlet servlet = servletUrlMapping.getServlet();
				context.addServlet(servletUrlMapping.getMappingUrl(), servlet)
						.addMapping(servletUrlMapping.getMappingUrl());
			}

			// applications models
			ModelGenerator modelGenerator = new GrizzlyModelGenerator();
			try {
				List<PersistenceUnitInfo> units = modelGenerator.getPersistenceUnits();
				for (PersistenceUnitInfo unit : units) {
					DatabaseDriver databaseDriver = DatabaseDriverFactory.createDriver(unit);
					if (!databaseDriver.getDatabasePing()) {
						databaseDriver.createDatabase();
						JPAPersistenceUnitInfo jpaUnit = (JPAPersistenceUnitInfo) unit;
						String name = jpaUnit.getPersistenceProviderClassName();
						Class<?> cls = JavaReflect.classForName(name);
						Object object = JavaReflect.newInstance(cls);
						PersistenceProvider provider = (PersistenceProvider) object;
						provider.generateSchema(unit, unit.getProperties());
					}
				}
			} catch (SQLException e) {
				// do nothing
			}

			// applications controllers
			ControllerGenerator controllerGenerator = new GrizzlyControllerGenerator();
			List<ServletUrlMapping> mappings = controllerGenerator.getMappings();
			for (ServletUrlMapping servletUrlMapping : mappings) {
				Servlet servlet = context.createServlet(servletUrlMapping.getServlet().getClass());
				context.addServlet(servletUrlMapping.getServlet().getClass().getName(), servlet)
						.addMapping(servletUrlMapping.getMappingUrl());
			}

			// restart servlet
			RestartServlet restart = new RestartServlet(modelGenerator, controllerGenerator, context, databaseServer,
					this);
			context.addServlet(restart.getClass().getName(), restart).addMapping("/pas/restart");

			context.deploy(httpserver);

		} catch (UnknownHostException e) {
			LoggerUtils.error(getClass(), LoggerConstants.UNKNOWN_HOST, e);
		} catch (ServletException e) {
			LoggerUtils.error(getClass(), LoggerConstants.SERVLET_ERROR, e);
		} catch (ClassNotFoundException e) {
			LoggerUtils.error(getClass(), LoggerConstants.CLASS_NOT_FOUND, e);
		} catch (IOException e) {
			LoggerUtils.error(getClass(), LoggerConstants.IO, e);
		} catch (Exception e) {
			if (!(e instanceof BindException)) {
				LoggerUtils.error(getClass(), LoggerConstants.RUNTIME_ERROR, e);
			}
		}

	}

	public final String getVersion() {
		return httpserver.getServerConfiguration().getHttpServerVersion();
	}

	public final String getName() {
		return httpserver.getServerConfiguration().getHttpServerName();
	}

	public final void start() {
		try {
			if (!httpserver.isStarted()) {
				httpserver.start();
			}
		} catch (IOException e) {
			LoggerUtils.error(getClass(), LoggerConstants.IO, e);
		}
	}

	public final void stop() {
		httpserver.shutdownNow();
	}

}
