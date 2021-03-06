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
package io.github.prolobjectlink.web.platform;

import org.glassfish.grizzly.servlet.WebappContext;

public class GrizzlyWebContext extends WebappContext {

	public GrizzlyWebContext() {
		super("GrizzlyContext");
	}

	GrizzlyWebContext(String displayName) {
		super(displayName);
	}

	GrizzlyWebContext(String displayName, String contextPath) {
		super(displayName, contextPath);
	}

	GrizzlyWebContext(String displayName, String contextPath, String basePath) {
		super(displayName, contextPath, basePath);
	}

}
