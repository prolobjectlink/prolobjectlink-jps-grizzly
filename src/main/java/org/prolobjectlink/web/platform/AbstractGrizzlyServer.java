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
import org.prolobjectlink.web.servlet.BootstrapCSSMinMapServlet;
import org.prolobjectlink.web.servlet.BootstrapCSSMinServlet;
import org.prolobjectlink.web.servlet.BootstrapCSSServlet;
import org.prolobjectlink.web.servlet.BootstrapJSMinMapServlet;
import org.prolobjectlink.web.servlet.BootstrapJSMinServlet;
import org.prolobjectlink.web.servlet.BootstrapJSServlet;
import org.prolobjectlink.web.servlet.ChartJSMinServlet;
import org.prolobjectlink.web.servlet.ChartJSServlet;
import org.prolobjectlink.web.servlet.DatabaseServlet;
import org.prolobjectlink.web.servlet.DocumentsServlet;
import org.prolobjectlink.web.servlet.FontawesomeCSSMinServlet;
import org.prolobjectlink.web.servlet.FontawesomeCSSServlet;
import org.prolobjectlink.web.servlet.JQueryMinMapServlet;
import org.prolobjectlink.web.servlet.JQueryMinServlet;
import org.prolobjectlink.web.servlet.JQueryServlet;
import org.prolobjectlink.web.servlet.ManagerServlet;
import org.prolobjectlink.web.servlet.PopperMinServlet;
import org.prolobjectlink.web.servlet.PopperServlet;
import org.prolobjectlink.web.servlet.ProlobjectlinkCSSMinServlet;
import org.prolobjectlink.web.servlet.ProlobjectlinkCSSServlet;
import org.prolobjectlink.web.servlet.ProlobjectlinkJSMinServlet;
import org.prolobjectlink.web.servlet.ProlobjectlinkJSServlet;
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
			DocumentsServlet doc = new DocumentsServlet();
			context.addServlet(doc.getClass().getName(), doc).addMapping("/doc");

			// jquery.js
			JQueryServlet jquery = new JQueryServlet();
			context.addServlet(jquery.getClass().getName(), jquery).addMapping("/js/jquery-3.4.1.js");
			JQueryMinServlet jquerymin = new JQueryMinServlet();
			context.addServlet(jquerymin.getClass().getName(), jquerymin).addMapping("/js/jquery-3.4.1.min.js");
			JQueryMinMapServlet jqueryminmap = new JQueryMinMapServlet();
			context.addServlet(jqueryminmap.getClass().getName(), jqueryminmap)
					.addMapping("/js/jquery-3.4.1.min.js.map");

			// propper.js
			PopperServlet proper = new PopperServlet();
			context.addServlet(proper.getClass().getName(), proper).addMapping("/js/popper.js");
			PopperMinServlet propermin = new PopperMinServlet();
			context.addServlet(propermin.getClass().getName(), propermin).addMapping("/js/popper.min.js");

			// chart.js
			ChartJSServlet chartjs = new ChartJSServlet();
			context.addServlet(chartjs.getClass().getName(), chartjs).addMapping("/js/chart.js");
			ChartJSMinServlet chartjsmin = new ChartJSMinServlet();
			context.addServlet(chartjsmin.getClass().getName(), chartjsmin).addMapping("/js/chart.min.js");

			// bootstrap.js
			BootstrapJSServlet bootstrapjs = new BootstrapJSServlet();
			context.addServlet(bootstrapjs.getClass().getName(), bootstrapjs).addMapping("/js/bootstrap.js");
			BootstrapJSMinServlet bootstrapjsmin = new BootstrapJSMinServlet();
			context.addServlet(bootstrapjsmin.getClass().getName(), bootstrapjsmin).addMapping("/js/bootstrap.min.js");
			BootstrapJSMinMapServlet bootstrapjsminmap = new BootstrapJSMinMapServlet();
			context.addServlet(bootstrapjsminmap.getClass().getName(), bootstrapjsminmap)
					.addMapping("/js/bootstrap.min.js.map");

			// prolobjectlink.js
			ProlobjectlinkJSServlet prolobjectlinkjs = new ProlobjectlinkJSServlet();
			context.addServlet(prolobjectlinkjs.getClass().getName(), prolobjectlinkjs)
					.addMapping("/js/prolobjectlink.js");
			ProlobjectlinkJSMinServlet prolobjectlinkjsmin = new ProlobjectlinkJSMinServlet();
			context.addServlet(prolobjectlinkjsmin.getClass().getName(), prolobjectlinkjsmin)
					.addMapping("/js/prolobjectlink.min.js");

			// bootstrap.css
			BootstrapCSSServlet bootstrapcss = new BootstrapCSSServlet();
			context.addServlet(bootstrapcss.getClass().getName(), bootstrapcss).addMapping("/css/bootstrap.css");
			BootstrapCSSMinServlet bootstrapcssmin = new BootstrapCSSMinServlet();
			context.addServlet(bootstrapcssmin.getClass().getName(), bootstrapcssmin)
					.addMapping("/css/bootstrap.min.css");
			BootstrapCSSMinMapServlet bootstrapcssminmap = new BootstrapCSSMinMapServlet();
			context.addServlet(bootstrapcssminmap.getClass().getName(), bootstrapcssminmap)
					.addMapping("/css/bootstrap.min.css.map");

			// prolobjectlink.css
			ProlobjectlinkCSSServlet prolobjectlinkcss = new ProlobjectlinkCSSServlet();
			context.addServlet(prolobjectlinkcss.getClass().getName(), prolobjectlinkcss)
					.addMapping("/css/prolobjectlink.css");
			ProlobjectlinkCSSMinServlet prolobjectlinkcssmin = new ProlobjectlinkCSSMinServlet();
			context.addServlet(prolobjectlinkcssmin.getClass().getName(), prolobjectlinkcssmin)
					.addMapping("/css/prolobjectlink.min.css");

			// fontawesome.css
			FontawesomeCSSServlet fontawesomecss = new FontawesomeCSSServlet();
			context.addServlet(fontawesomecss.getClass().getName(), fontawesomecss)
					.addMapping("/css/fontawesome-all.css");
			FontawesomeCSSMinServlet fontawesomecssmin = new FontawesomeCSSMinServlet();
			context.addServlet(fontawesomecssmin.getClass().getName(), fontawesomecssmin)
					.addMapping("/css/fontawesome-all.min.css");

			// // server misc
			// MiscGenerator miscGenerator = new GrizzlyMiscGenerator();
			// List<ServletUrlMapping> paths = miscGenerator.getMappings();
			// for (ServletUrlMapping servletUrlMapping : paths) {
			// Servlet servlet = servletUrlMapping.getServlet();
			// context.addServlet(servletUrlMapping.getServlet().getClass().getName(),
			// servlet)
			// .addMapping(servletUrlMapping.getMappingUrl());
			// }

			// applications models
			try {
				ModelGenerator modelGenerator = new GrizzlyModelGenerator();
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
