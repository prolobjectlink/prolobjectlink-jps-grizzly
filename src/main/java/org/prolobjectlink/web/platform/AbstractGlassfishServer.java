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
package org.prolobjectlink.web.platform;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
public abstract class AbstractGlassfishServer extends AbstractWebServer implements JettyWebServer {

	public AbstractGlassfishServer(int serverPort) {
		super(serverPort);
	}

	public final String getLicense() {
		// TODO Auto-generated method stub
		return null;
	}

	public final String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public final String getName() {
		return GLASSFISH;
	}

	public final void start() {
		// TODO Auto-generated method stub
	}

	public final void stop() {
		// TODO Auto-generated method stub
	}

}
