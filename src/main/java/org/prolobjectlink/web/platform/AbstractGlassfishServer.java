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

import java.io.File;

import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
public abstract class AbstractGlassfishServer extends AbstractWebServer implements GlassfishWebServer {

	private GlassFish server;
	private Deployer deployer;
	private final GlassFishProperties properties;

	public AbstractGlassfishServer(int serverPort) {
		super(serverPort);
		properties = new GlassFishProperties();
		properties.setProperty("http-listener", "" + serverPort + "");
		try {
			server = GlassFishRuntime.bootstrap().newGlassFish(properties);
		} catch (GlassFishException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public final String getName() {
		return GLASSFISH;
	}

	public final void start() {
		try {
			server.start();
			deployer = server.getDeployer();
			deployer.deploy(new File("warFileGoHere"));
		} catch (GlassFishException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final void stop() {
		try {
			deployer.undeploy("warFileGoHere");
			server.stop();
			server.dispose();
		} catch (GlassFishException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
