/*-
 * #%L
 * prolobjectlink-jps-grizzly
 * %%
 * Copyright (C) 2020 Prolobjectlink Project
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
package io.github.prolobjectlink.web.faces.http;

import javax.servlet.http.HttpSession;

import org.glassfish.grizzly.http.server.Session;
import org.glassfish.grizzly.servlet.HttpSessionImpl;
import org.glassfish.grizzly.servlet.WebappContext;

public class FacesSession extends HttpSessionImpl implements HttpSession {

	public FacesSession(WebappContext contextImpl) {
		super(contextImpl, null);
	}

	public FacesSession(WebappContext contextImpl, Session session) {
		super(contextImpl, session);
	}

}
