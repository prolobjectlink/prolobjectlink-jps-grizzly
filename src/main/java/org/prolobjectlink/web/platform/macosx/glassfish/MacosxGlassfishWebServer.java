/*-
 * #%L
 * prolobjectlink-jds-glassfish
 * %%
 * Copyright (C) 2019 Prolobjectlink Project
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.prolobjectlink.web.platform.macosx.glassfish;

import org.prolobjectlink.web.platform.AbstractGlassfishServer;
import org.prolobjectlink.web.platform.GlassfishWebServer;

public class MacosxGlassfishWebServer extends AbstractGlassfishServer implements GlassfishWebServer {

	public MacosxGlassfishWebServer(int serverPort) {
		super(serverPort);
	}

}
