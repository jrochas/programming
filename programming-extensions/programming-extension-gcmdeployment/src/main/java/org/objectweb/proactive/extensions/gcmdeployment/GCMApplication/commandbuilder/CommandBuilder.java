/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder;

import java.util.List;

import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfo;


public interface CommandBuilder {

    /**
     * Build the command to start the application, this applies only to local executions where using List<String> is preferred
     * over simple String
     * 
     *
     * @param hostInfo
     *            Host information to customize the command according to this host type
     * @param gcma
     *            configuration of the GCMApplication
     * @return A list of command to be used to start several nodes. Each command is expressed via a list of Strings
     */
    public List<List<String>> buildCommandLocal(HostInfo hostInfo, GCMApplicationInternal gcma);

    /**
     * Build the command to start the application
     *
     *
     * @param hostInfo
     *            Host information to customize the command according to this host type
     * @return The command to be used to start the application
     */
    public String buildCommand(HostInfo hostInfo, GCMApplicationInternal gcma);

    /**
     * Returns the base path associated to this command builder
     * 
     * Since the base path is always relative to the home directory a HostInfo must be passed as
     * parameter.
     * 
     * @return the base path if the base path is specified otherwise null is returned
     */
    public String getPath(HostInfo hostInfo);
}
