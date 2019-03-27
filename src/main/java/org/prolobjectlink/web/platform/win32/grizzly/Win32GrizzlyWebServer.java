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
package org.prolobjectlink.web.platform.win32.grizzly;

import org.prolobjectlink.web.platform.AbstractGrizzlyServer;
import org.prolobjectlink.web.platform.GrizzlyWebServer;

public class Win32GrizzlyWebServer extends AbstractGrizzlyServer implements GrizzlyWebServer {

	public Win32GrizzlyWebServer(int serverPort) {
		super(serverPort);
	}

}
