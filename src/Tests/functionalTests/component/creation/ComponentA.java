/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.component.creation;

import java.io.Serializable;

import org.objectweb.proactive.api.PAActiveObject;


/**
 * @author The ProActive Team
 *
 */
public class ComponentA implements Serializable, ComponentInfo {

    /**
     * 
     */
    private static final long serialVersionUID = 42L;
    /**
     *
     */
    String name;

    public ComponentA() {
    }

    public ComponentA(String name) {
        this.name = name;
    }

    public void sayHello() {
        System.out.println("Hello");
    }

    public String getName() {
        return name;
    }

    public String getNodeUrl() {
        return PAActiveObject.getBodyOnThis().getNodeURL();
    }

    /*
     * (non-Javadoc)
     * 
     * @see functionalTests.component.creation.ComponentInfo#sayHello2()
     */
    public String sayHello2() {
        System.out.println("Hello");
        return "hello";
    }
}
