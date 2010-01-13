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
package org.objectweb.proactive.extensions.calcium.muscle;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.extensions.calcium.system.SkeletonSystem;


/**
 * This interface is used to execute a function on a parameter and return the result.
 *
 * This interface is used in {@link org.objectweb.proactive.extensions.calcium.skeletons.Skeleton}s such as: {@link org.objectweb.proactive.extensions.calcium.skeletons.Seq}, {@link org.objectweb.proactive.extensions.calcium.skeletons.Farm}, {@link org.objectweb.proactive.extensions.calcium.skeletons.Pipe}, etc...
 *
 * @author The ProActive Team
 */
@PublicAPI
public interface Execute<P, R> extends Muscle<P, R> {
    public R execute(P param, SkeletonSystem system) throws Exception;
}
