/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.component.controller;

import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.proactive.annotation.PublicAPI;


/**
 * This interface defines an extension of the @see org.objectweb.fractal.api.control.LifeCycleController, which
 * is able to handle prioritized requests.
 *<p>
 * (Under development)
 * </p>
 *
 * @see org.objectweb.fractal.api.control.LifeCycleController
 *
 * @author The ProActive Team
 *
 */
@PublicAPI
public interface ProActiveLifeCycleController extends LifeCycleController {

    /**
     * @see org.objectweb.fractal.api.control.LifeCycleController#getFcState()
     */
    public String getFcState(short priority);

    /**
     * @see org.objectweb.fractal.api.control.LifeCycleController#startFc()
     */
    public void startFc(short priority) throws IllegalLifeCycleException;

    /**
     * @see org.objectweb.fractal.api.control.LifeCycleController#stopFc()
     */
    public void stopFc(short priority) throws IllegalLifeCycleException;
}
