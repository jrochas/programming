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

import static org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers.GCMD_LOGGER;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeProvider;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfo;
import org.objectweb.proactive.extensions.gcmdeployment.PathElement;


public class CommandBuilderExecutable implements CommandBuilder {

    /** List of providers to be used */
    private List<NodeProvider> providers;

    private String command;

    /** The path to the command */
    private PathElement path;

    /** The arguments */
    private List<String> args;

    public enum Instances {
        onePerHost,
        onePerVM,
        onePerCapacity;
    }

    private Instances instances;

    public CommandBuilderExecutable() {
        providers = new ArrayList<NodeProvider>();
        args = new ArrayList<String>();
        instances = Instances.onePerHost;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setPath(PathElement pe) {
        path = pe;
    }

    public void addArg(String arg) {
        if (arg != null) {
            GCMD_LOGGER.trace(" Added " + arg + " to args");
            args.addAll(CommandBuilderHelper.parseArg(arg));
        }
    }

    public void addNodeProvider(NodeProvider nodeProvider) {
        providers.add(nodeProvider);
    }

    @Override
    public List<List<String>> buildCommandLocal(HostInfo hostInfo, GCMApplicationInternal gcma) {
        ArrayList<String> cmd = new ArrayList<String>();
        if (path != null) {
            cmd.add(PathElement.appendPath(path.getFullPath(hostInfo, this), command, hostInfo));
        } else {
            cmd.add(command);
        }
        for (String arg : args) {
            cmd.add(arg);
        }
        int nbCmd = 0;
        switch (instances) {
            case onePerCapacity:
                nbCmd = hostInfo.getHostCapacity() * hostInfo.getVmCapacity();
                break;
            case onePerVM:
                nbCmd = hostInfo.getHostCapacity();
                break;
            case onePerHost:
                nbCmd = 1;
                break;
        }

        ArrayList<List<String>> commandList = new ArrayList<List<String>>();
        for (int i = 0; i < nbCmd; i++) {
            commandList.add(cmd);
        }

        GCMD_LOGGER.trace(commandList);
        return commandList;
    }

    public String buildCommand(HostInfo hostInfo, GCMApplicationInternal gcma) {
        StringBuilder sb = new StringBuilder();
        if (path != null) {
            sb.append(PathElement.appendPath(path.getFullPath(hostInfo, this), command, hostInfo));
        } else {
            sb.append(command);
        }

        for (String arg : args) {
            sb.append(" " + arg);
        }

        int nbCmd = 0;
        switch (instances) {
            case onePerCapacity:
                nbCmd = hostInfo.getHostCapacity() * hostInfo.getVmCapacity();
                break;
            case onePerVM:
                nbCmd = hostInfo.getHostCapacity();
                break;
            case onePerHost:
                nbCmd = 1;
                break;
        }

        StringBuilder ret = new StringBuilder();
        switch (hostInfo.getOS()) {
            case unix:
                for (int i = 0; i < nbCmd; i++) {
                    ret.append(sb);
                    ret.append(" &");
                }
                ret.deleteCharAt(ret.length() - 1);
                break;

            case windows:
                if (nbCmd > 1) {
                    throw new IllegalStateException("Multiple command per machine is not yet supported on windows");
                } else {
                    ret.append(sb);
                }
                break;
        }

        return ret.toString();
    }

    public String getPath(HostInfo hostInfo) {
        if (path != null) {
            return path.getFullPath(hostInfo, this);
        } else {
            return "";
        }
    }

    public void setInstances(String instancesValue) {
        instances = Instances.valueOf(instancesValue);
    }
}
