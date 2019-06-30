/*-
 * #%L
 * prolobjectlink-jps-glassfish
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
package org.prolobjectlink.web.platform;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.prolobjectlink.db.DatabaseServer;
import org.prolobjectlink.db.platform.linux.LinuxDatabaseServer;
import org.prolobjectlink.db.platform.macosx.MacosxDatabaseServer;
import org.prolobjectlink.db.platform.win32.Win32DatabaseServer;
import org.prolobjectlink.web.platform.linux.grizzly.LinuxGrizzlyWebServer;
import org.prolobjectlink.web.platform.macosx.grizzly.MacosxGrizzlyWebServer;
import org.prolobjectlink.web.platform.win32.grizzly.Win32GrizzlyWebServer;

public class GrizzlyServerControl extends AbstractWebControl implements WebServerControl {

	public GrizzlyServerControl(WebServer webServer, DatabaseServer databaseServer) {
		super(webServer, databaseServer);
	}

	public static void main(String[] args) {

		int port = 8085;

		DatabaseServer database = null;
		GrizzlyWebServer server = null;

		if (WebPlatformUtil.runOnWindows()) {
			database = new Win32DatabaseServer();
			server = new Win32GrizzlyWebServer(port);
		} else if (WebPlatformUtil.runOnOsX()) {
			database = new MacosxDatabaseServer();
			server = new MacosxGrizzlyWebServer(port);
		} else if (WebPlatformUtil.runOnLinux()) {
			database = new LinuxDatabaseServer();
			server = new LinuxGrizzlyWebServer(port);
		} else {
			Logger.getLogger(GrizzlyServerControl.class.getName()).log(Level.SEVERE, null, "Not supported platform");
			System.exit(1);
		}

		new GrizzlyServerControl(server, database).run(args);
	}

}
