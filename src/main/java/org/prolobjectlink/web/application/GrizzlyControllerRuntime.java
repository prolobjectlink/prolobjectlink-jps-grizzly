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
package org.prolobjectlink.web.application;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GrizzlyControllerRuntime {

	public static void run(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String contextPath = req.getContextPath();
		String application = contextPath.substring(1);
		String servletPath = req.getServletPath();
		String procedure = servletPath.substring(1);

		String pathInfo = req.getPathInfo();
		Object[] arguments = new Object[0];
		if (pathInfo != null && !pathInfo.isEmpty()) {
			pathInfo = pathInfo.substring(1);
			arguments = pathInfo.split("/");
		}

		ServletOutputStream out = resp.getOutputStream();
		ControllerRuntime.run(application, procedure, arguments, out);
		resp.setStatus(HttpServletResponse.SC_OK);

	}

	private GrizzlyControllerRuntime() {
	}

}
