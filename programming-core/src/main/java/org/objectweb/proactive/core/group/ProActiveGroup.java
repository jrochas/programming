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
package org.objectweb.proactive.core.group;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.body.future.FutureProxy;
import org.objectweb.proactive.core.descriptor.data.VirtualNodeInternal;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.mop.ConstructionOfProxyObjectFailedException;
import org.objectweb.proactive.core.mop.ConstructionOfReifiedObjectFailedException;
import org.objectweb.proactive.core.mop.InvalidProxyClassException;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.Proxy;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * This class provides static methods to manage objects representing a Group (<b>typed group</b>).<br><br>
 *
 * The ProActiveGroup class provides a set of static services through static method calls.
 * It is the main entry point for users of ProActive Group Communication as they will call methods of this class
 * to create group of object or to synchronize them.<br><br>
 *
 * The main role of ProActiveGroup is to provide methods to create typed group. It is possible to create a typed
 * group (empty or not) through instantiation using one of the version of newActive.
 * It is also possible to create an active typed group from an existing using using the turnActive methods.<br>
 * The default behavior is a broadcast call, with a unique serialization of parameters.<br><br>
 * <b>Warning !!!</b> When a typed group is turned active, it looses the ability to switch to the
 * <code>Group</code> representation. So an active typed group acquire the abilites of any active object
 * (remote reference, migration, ...) but is no more able to evolve : no modification of the membership is possible.<br>
 * (This feature will may appear in a later version of ProActive group communication).
 *
 * @author The ProActive Team
 */
@SuppressWarnings({ "deprecation", "unchecked" })
public class ProActiveGroup {

    /** The logger for the Class */
    protected static final Logger logger = ProActiveLogger.getLogger(Loggers.GROUPS);

    /** The name of the default proxy for group communication */
    public static final Class<?> DEFAULT_PROXYFORGROUP_CLASS = org.objectweb.proactive.core.group.ProxyForGroup.class;

    /** The name of the default proxy for group communication */
    public static final String DEFAULT_PROXYFORGROUP_CLASS_NAME = "org.objectweb.proactive.core.group.ProxyForGroup";

    /** This constructor with a private acces permits the javadoc to hide the default constructor method in the html file */
    private ProActiveGroup() {
    }

    /**
     * Returns the <code>Group</code> for typed group <code>o</code>. Returns null if <code>o</code> is not a typed group.
     * @param o - the typed group.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#getGroup(Object)} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static Group getGroup(Object o) {
        return ProActiveGroup.findProxyForGroup(o);
    }

    /**
     * Returns the name class of the typed group.
     * If the parameter is not a typed group, returns the name of Class<?> of the parameter.
     * @param o the typed group for wich we want the name of the type (Class<?>).
     * @return the name class of the typed group
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#getType(Object)} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static String getType(Object o) {
        ProxyForGroup tmp = ProActiveGroup.findProxyForGroup(o);
        if (tmp != null) {
            return tmp.getTypeName();
        } else {
            return o.getClass().getName();
        }
    }

    /**
     * Creates an object representing a group (a typed group) and creates members on the default node.
     * @param className the name of the (upper) class of the group's members.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String)} instead
     */
    @Deprecated
    public static Object newGroup(String className) throws ClassNotFoundException, ClassNotReifiableException {
        return ProActiveGroup.newGroup(className, (Class<?>[]) null);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members on the default node.
     * @param className the name of the (upper) class of the group's members.
     * @param params the array that contain the parameters used to build the group's members.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Object[][])} instead
     */
    @Deprecated
    public static Object newGroup(String className, Object[][] params)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = NodeFactory.getDefaultNode();

        return ProActiveGroup.newGroup(className, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates all members with params on the node.
     * @param className the name of the (upper) class of the group's members.
     * @param params the array that contain the parameters used to build the group's members.
     * @param nodeName the name (String) of the node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Object[][],String)} instead
     */
    @Deprecated
    public static Object newGroup(String className, Object[][] params, String nodeName)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = NodeFactory.getNode(nodeName);
        return ProActiveGroup.newGroup(className, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with params cycling on nodeList.
     * @param className the name of the (upper) class of the group's members.
     * @param params the array that contain the parameters used to build the group's members.
     * @param nodeListString the names of the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Object[][],String[])} instead
     */
    @Deprecated
    public static Object newGroup(String className, Object[][] params, String[] nodeListString)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[nodeListString.length];
        for (int i = 0; i < nodeListString.length; i++)
            nodeList[i] = NodeFactory.getNode(nodeListString[i]);
        return ProActiveGroup.newGroup(className, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates all members with params on the node.
     * @param className the name of the (upper) class of the group's members.
     * @param params the array that contain the parameters used to build the group's members.
     * @param node the node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Object[][],Node)} instead
     */
    @Deprecated
    public static Object newGroup(String className, Object[][] params, Node node)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = node;
        return ProActiveGroup.newGroup(className, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with params cycling on nodeList.
     * @param className the name of the (upper) class of the group's members.
     * @param genericParameters genericParameters parameterizing types
     * @param params the array that contain the parameters used to build the group's members.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param nodeList the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Class<?>[],Object[][],Node[])} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static Object newGroup(String className, Class<?>[] genericParameters, Object[][] params, Node[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Object result = ProActiveGroup.newGroup(className, genericParameters);
        Group g = ProActiveGroup.getGroup(result);

        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                g.add(PAActiveObject.newActive(className, params[i], nodeList[i % nodeList.length]));
            }
        }

        return result;
    }

    /**
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Class<?>[],Object[][],Node[])} instead
     */
    @Deprecated
    public static Object newGroup(String className, Object[][] params, Node[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return ProActiveGroup.newGroup(className, (Class<?>[]) null, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with params cycling on the nodes of the virtual node.
     * @param className the name of the (upper) class of the group's members.
     * @param params the array that contain the parameters used to build the group's members.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param virtualNode the virtual where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Object[][],VirtualNodeInternal)} instead
     */
    @Deprecated
    public static Object newGroup(String className, Object[][] params, VirtualNodeInternal virtualNode)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return ProActiveGroup.newGroup(className, params, virtualNode.getNodes());
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on nodeList.
     * @param className the name of the (upper) class of the group's members.
     * @param params the parameters used to build all the group's members.
     * @param nodeList the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Object[],Node[])} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static Object newGroup(String className, Object[] params, Node[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Object result = ProActiveGroup.newGroup(className, (Class<?>[]) null);
        Group g = ProActiveGroup.getGroup(result);

        if (params != null) {
            for (int i = 0; i < nodeList.length; i++) {
                g.add(PAActiveObject.newActive(className, params, nodeList[i % nodeList.length]));
            }
        }

        return result;
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on nodeList.
     * @param className the name of the (upper) class of the group's members.
     * @param params the parameters used to build all the group's members.
     * @param virtualNode the virtual node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Object[],VirtualNodeInternal)} instead
     */
    @Deprecated
    public static Object newGroup(String className, Object[] params, VirtualNodeInternal virtualNode)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return ProActiveGroup.newGroup(className, params, virtualNode.getNodes());
    }

    /**
     * Creates an object representing a group (a typed group) and creates all members with the same params on the node.
     * @param className the name of the (upper) class of the group's members.
     * @param params the parameters used to build all the group's members.
     * @param node the node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Object[],Node)} instead
     */
    @Deprecated
    public static Object newGroup(String className, Object[] params, Node node)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = node;
        return ProActiveGroup.newGroup(className, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates all members with the same params on the node.
     * @param className the name of the (upper) class of the group's members.
     * @param params the parameters used to build all the group's members.
     * @param nodeName the name of the node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Object[],String)} instead
     */
    @Deprecated
    public static Object newGroup(String className, Object[] params, String nodeName)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = NodeFactory.getNode(nodeName);
        return ProActiveGroup.newGroup(className, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on nodeList.
     * @param className the name of the (upper) class of the group's members.
     * @param params the parameters used to build all the group's members.
     * @param nodeListString the names of the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Object[],String[])} instead
     */
    @Deprecated
    public static Object newGroup(String className, Object[] params, String[] nodeListString)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[nodeListString.length];
        for (int i = 0; i < nodeListString.length; i++)
            nodeList[i] = NodeFactory.getNode(nodeListString[i]);
        return ProActiveGroup.newGroup(className, params, nodeList);
    }

    /**
     * Turns the target object (a typed group) into an ActiveObject (an active typed group) attached to a default
     * node in the local JVM.
     * @param ogroup the typed group to turn active.
     * @return a reference on the active object produced.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#turnActiveGroup(Object)} instead
     */
    @Deprecated
    public static Object turnActiveGroup(Object ogroup)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return PAActiveObject.turnActive(ogroup, null, ProActiveGroup.getType(ogroup), (Node) null, null, null);
    }

    /**
     * Turns the target object (a typed group) into an ActiveObject (an active typed group) attached to a specified node.
     * @param ogroup the typed group to turn active.
     * @param node the node where to create the active object on. If <code>null</code>,
     * the active object is created localy on a default node
     * @return a reference (possibly remote) on the active object produced.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the specified node can not be reached.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#turnActiveGroup(Object,Node)} instead
     */
    @Deprecated
    public static Object turnActiveGroup(Object ogroup, Node node)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return PAActiveObject.turnActive(ogroup, null, ProActiveGroup.getType(ogroup), node, null, null);
    }

    /**
     * Turns the target object (a typed group) into an ActiveObject (an active typed group) attached to a specified node.
     * @param ogroup the typed group to turn active.
     * @param nodeName the name of the node where to create the active object on.
     * @return a reference (possibly remote) on the active object produced.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the specified node can not be reached.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#turnActiveGroup(Object,String)} instead
     */
    @Deprecated
    public static Object turnActiveGroup(Object ogroup, String nodeName)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return PAActiveObject.turnActive(ogroup,
                                         null,
                                         ProActiveGroup.getType(ogroup),
                                         NodeFactory.getNode(nodeName),
                                         null,
                                         null);
    }

    // -------------------------------------------------------------------------
    // Start Deprecated
    // -------------------------------------------------------------------------

    /**
     * @deprecated use newGroupInParallel
     * Creates an object representing a group (a typed group) and creates members on the default node.
     * @param className the name of the (upper) class of the group's member.
     * @param params the array that contain the parameters used to build the group's member.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated  use newGroupInParallel
     */
    @Deprecated
    public static Object newGroupBuiltWithMultithreading(String className, Object[][] params)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = NodeFactory.getDefaultNode();

        return ProActiveGroup.newGroupBuiltWithMultithreading(className, params, nodeList);
    }

    /**
     * @deprecated use newGroupInParallel
     * Creates an object representing a group (a typed group) and creates members with params cycling on nodeList.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the array that contain the parameters used to build the group's member.
     * @param nodeList the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated  use newGroupInParallel
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static Object newGroupBuiltWithMultithreading(String className, Object[][] params, Node[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Object result = ProActiveGroup.newGroup(className, (Class<?>[]) null);
        ProxyForGroup proxy = (org.objectweb.proactive.core.group.ProxyForGroup) ProActiveGroup.getGroup(result);

        proxy.createMemberWithMultithread(className, null, params, nodeList);

        return result;
    }

    /**
     * @deprecated use newGroupInParallel
     * Creates an object representing a group (a typed group) and creates members with params cycling on nodeList.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the array that contain the parameters used to build the group's member.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param nodeList the names of the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated  use newGroupInParallel
     */
    @Deprecated
    public static Object newGroupBuiltWithMultithreading(String className, Object[][] params, String[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeListString = new Node[nodeList.length];
        for (int i = 0; i < nodeList.length; i++)
            nodeListString[i] = NodeFactory.getNode(nodeList[i]);
        return ProActiveGroup.newGroupBuiltWithMultithreading(className, params, nodeListString);
    }

    /**
     * @deprecated use newGroupInParallel
     * Creates an object representing a group (a typed group) and creates members with params cycling on the nodes of the vitual node.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the array that contain the parameters used to build the group's member.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param virtualNode the virtual node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated  use newGroupInParallel
     */
    @Deprecated
    public static Object newGroupBuiltWithMultithreading(String className, Object[][] params,
            VirtualNodeInternal virtualNode)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return ProActiveGroup.newGroupBuiltWithMultithreading(className, params, virtualNode.getNodes());
    }

    /**
     * @deprecated use newGroupInParallel
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on nodeList.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's members.
     * @param params the parameters used to build all the group's members.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param nodeList the names of the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated  use newGroupInParallel
     */
    @Deprecated
    public static Object newGroupBuiltWithMultithreading(String className, Object[] params, String[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeListString = new Node[nodeList.length];
        for (int i = 0; i < nodeList.length; i++)
            nodeListString[i] = NodeFactory.getNode(nodeList[i]);
        return ProActiveGroup.newGroupBuiltWithMultithreading(className, params, nodeListString);
    }

    /**
     * @deprecated use newGroupInParallel
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on nodeList.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the parameters used to build all the group's member.
     * @param nodeList the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated  use newGroupInParallel
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static Object newGroupBuiltWithMultithreading(String className, Object[] params, Node[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Object result = ProActiveGroup.newGroup(className, (Class<?>[]) null);
        ProxyForGroup proxy = (org.objectweb.proactive.core.group.ProxyForGroup) ProActiveGroup.getGroup(result);

        proxy.createMemberWithMultithread(className, null, params, nodeList);

        return result;
    }

    /**
     * @deprecated use newGroupInParallel
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on the nodes of the vitual node.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the parameters used to build all the group's member.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param virtualNode the virtual node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated  use newGroupInParallel
     */
    @Deprecated
    public static Object newGroupBuiltWithMultithreading(String className, Object[] params,
            VirtualNodeInternal virtualNode)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return ProActiveGroup.newGroupBuiltWithMultithreading(className, params, virtualNode.getNodes());
    }

    /**
     * Creates an object representing a group (a typed group) and creates members on the default node.
     * @param className the name of the (upper) class of the group's member.
     * @param params the array that contain the parameters used to build the group's member.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Object[][])} instead
     */
    @Deprecated
    public static Object newGroupInParallel(String className, Object[][] params)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return newGroupInParallel(className, null, params);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with params cycling on nodeList.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the array that contain the parameters used to build the group's member.
     * @param nodeList the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Object[][],Node[])} instead
     */
    @Deprecated
    public static Object newGroupInParallel(String className, Object[][] params, Node[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return newGroupInParallel(className, null, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with params cycling on nodeList.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the array that contain the parameters used to build the group's member.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param nodeList the names of the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Object[][],String[])} instead
     */
    @Deprecated
    public static Object newGroupInParallel(String className, Object[][] params, String[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return newGroupInParallel(className, null, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with params cycling on the nodes of the vitual node.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the array that contain the parameters used to build the group's member.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param virtualNode the virtual node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Object[][],VirtualNodeInternal)} instead
     */
    @Deprecated
    public static Object newGroupInParallel(String className, Object[][] params, VirtualNodeInternal virtualNode)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return newGroupInParallel(className, null, params, virtualNode);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on nodeList.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's members.
     * @param params the parameters used to build all the group's members.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param nodeList the names of the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Object[],String[])} instead
     */
    @Deprecated
    public static Object newGroupInParallel(String className, Object[] params, String[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return newGroupInParallel(className, null, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on nodeList.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the parameters used to build all the group's member.
     * @param nodeList the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Object[],Node[])} instead
     */
    @Deprecated
    public static Object newGroupInParallel(String className, Object[] params, Node[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return newGroupInParallel(className, null, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on the nodes of the vitual node.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the parameters used to build all the group's member.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param virtualNode the virtual node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Object[],VirtualNodeInternal)} instead
     */
    @Deprecated
    public static Object newGroupInParallel(String className, Object[] params, VirtualNodeInternal virtualNode)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return ProActiveGroup.newGroupInParallel(className, null, params, virtualNode);
    }

    //// generic methods
    /**
     * Creates an object representing a group (a typed group) and creates members on the default node.
     * @param className the name of the (upper) class of the group's member.
     * @param params the array that contain the parameters used to build the group's member.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Class<?>[],Object[][])} instead
     */
    @Deprecated
    public static Object newGroupInParallel(String className, Class<?>[] genericParameters, Object[][] params)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = NodeFactory.getDefaultNode();

        return ProActiveGroup.newGroupInParallel(className, genericParameters, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with params cycling on nodeList.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the array that contain the parameters used to build the group's member.
     * @param nodeList the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Class<?>[],Object[][],Node[])} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static Object newGroupInParallel(String className, Class<?>[] genericParameters, Object[][] params,
            Node[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Object result = ProActiveGroup.newGroup(className, genericParameters);
        ProxyForGroup proxy = (org.objectweb.proactive.core.group.ProxyForGroup) ProActiveGroup.getGroup(result);

        proxy.createMemberWithMultithread(className, genericParameters, params, nodeList);

        return result;
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with params cycling on nodeList.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the array that contain the parameters used to build the group's member.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param nodeList the names of the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Class<?>[],Object[][],String[])} instead
     */
    @Deprecated
    public static Object newGroupInParallel(String className, Class<?>[] genericParameters, Object[][] params,
            String[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeListString = new Node[nodeList.length];
        for (int i = 0; i < nodeList.length; i++)
            nodeListString[i] = NodeFactory.getNode(nodeList[i]);
        return ProActiveGroup.newGroupInParallel(className, genericParameters, params, nodeListString);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with params cycling on the nodes of the vitual node.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the array that contain the parameters used to build the group's member.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param virtualNode the virtual node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Class<?>[],Object[][],VirtualNodeInternal)} instead
     */
    @Deprecated
    public static Object newGroupInParallel(String className, Class<?>[] genericParameters, Object[][] params,
            VirtualNodeInternal virtualNode)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return ProActiveGroup.newGroupInParallel(className, genericParameters, params, virtualNode.getNodes());
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on nodeList.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's members.
     * @param params the parameters used to build all the group's members.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param nodeList the names of the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Class<?>[],Object[],String[])} instead
     */
    @Deprecated
    public static Object newGroupInParallel(String className, Class<?>[] genericParameters, Object[] params,
            String[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeListString = new Node[nodeList.length];
        for (int i = 0; i < nodeList.length; i++)
            nodeListString[i] = NodeFactory.getNode(nodeList[i]);
        return ProActiveGroup.newGroupInParallel(className, genericParameters, params, nodeListString);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on nodeList.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the parameters used to build all the group's member.
     * @param nodeList the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Class<?>[],Object[],Node[])} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static Object newGroupInParallel(String className, Class<?>[] genericParameters, Object[] params,
            Node[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Object result = ProActiveGroup.newGroup(className, genericParameters);
        ProxyForGroup proxy = (org.objectweb.proactive.core.group.ProxyForGroup) ProActiveGroup.getGroup(result);

        proxy.createMemberWithMultithread(className, genericParameters, params, nodeList);

        return result;
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on the nodes of the vitual node.
     * Threads are used to build the group's members. This methods returns when all members were created.
     * @param className the name of the (upper) class of the group's member.
     * @param params the parameters used to build all the group's member.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param virtualNode the virtual node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroupInParallel(String,Class<?>[],Object[],VirtualNodeInternal)} instead
     */
    @Deprecated
    public static Object newGroupInParallel(String className, Class<?>[] genericParameters, Object[] params,
            VirtualNodeInternal virtualNode)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return ProActiveGroup.newGroupInParallel(className, genericParameters, params, virtualNode.getNodes());
    }

    /**
     * Creates an object representing an empty group specifying the upper class of members.
     * @param className the name of the (upper) class of the group's members.
     * @param genericParameters genericParameters parameterizing types
     * @return an empty group of type <code>className</code>.
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Class<?>[])} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static Object newGroup(String className, Class<?>[] genericParameters)
            throws ClassNotFoundException, ClassNotReifiableException {
        MOP.checkClassIsReifiable(MOP.forName(className));

        Object result = null;

        try {
            result = MOP.newInstance(className, genericParameters, null, DEFAULT_PROXYFORGROUP_CLASS_NAME, null);

            ProxyForGroup proxy = (org.objectweb.proactive.core.group.ProxyForGroup) ((StubObject) result).getProxy();
            proxy.className = className;
            proxy.setStub((StubObject) result);
        } catch (ClassNotReifiableException e) {
            logger.error("**** ClassNotReifiableException ****");
        } catch (InvalidProxyClassException e) {
            logger.error("**** InvalidProxyClassException ****");
        } catch (ConstructionOfProxyObjectFailedException e) {
            logger.error("**** ConstructionOfProxyObjectFailedException ****");
        } catch (ConstructionOfReifiedObjectFailedException e) {
            logger.error("**** ConstructionOfReifiedObjectFailedException ****");
        }

        return result;
    }

    /**
     * Creates an object representing a group (a typed group) and creates members on the default node.
     * @param className the name of the (upper) class of the group's members.
     * @param genericParameters genericParameters parameterizing types
     * @param params the array that contain the parameters used to build the group's members.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Class<?>[],Object[][])} instead
     */
    @Deprecated
    public static Object newGroup(String className, Class<?>[] genericParameters, Object[][] params)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = NodeFactory.getDefaultNode();

        return ProActiveGroup.newGroup(className, genericParameters, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates all members with params on the node.
     * @param className the name of the (upper) class of the group's members.
     * @param genericParameters genericParameters parameterizing types
     * @param params the array that contain the parameters used to build the group's members.
     * @param nodeName the name (String) of the node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Class<?>[],Object[][],String)} instead
     */
    @Deprecated
    public static Object newGroup(String className, Class<?>[] genericParameters, Object[][] params, String nodeName)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = NodeFactory.getNode(nodeName);
        return ProActiveGroup.newGroup(className, genericParameters, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with params cycling on nodeList.
     * @param className the name of the (upper) class of the group's members.
     * @param genericParameters genericParameters parameterizing types
     * @param params the array that contain the parameters used to build the group's members.
     * @param nodeListString the names of the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Class<?>[],Object[][],String[])} instead
     */
    @Deprecated
    public static Object newGroup(String className, Class<?>[] genericParameters, Object[][] params,
            String[] nodeListString)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[nodeListString.length];
        for (int i = 0; i < nodeListString.length; i++)
            nodeList[i] = NodeFactory.getNode(nodeListString[i]);
        return ProActiveGroup.newGroup(className, genericParameters, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates all members with params on the node.
     * @param className the name of the (upper) class of the group's members.
     * @param genericParameters genericParameters parameterizing types
     * @param params the array that contain the parameters used to build the group's members.
     * @param node the node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Class<?>[],Object[][],Node)} instead
     */
    @Deprecated
    public static Object newGroup(String className, Class<?>[] genericParameters, Object[][] params, Node node)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = node;
        return ProActiveGroup.newGroup(className, genericParameters, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with params cycling on the nodes of the virtual node.
     * @param className the name of the (upper) class of the group's members.
     * @param genericParameters genericParameters parameterizing types
     * @param params the array that contain the parameters used to build the group's members.
     * If <code>params</code> is <code>null</code>, builds an empty group.
     * @param virtualNode the virtual where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Class<?>[],Object[][],VirtualNodeInternal)} instead
     */
    @Deprecated
    public static Object newGroup(String className, Class<?>[] genericParameters, Object[][] params,
            VirtualNodeInternal virtualNode)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return ProActiveGroup.newGroup(className, genericParameters, params, virtualNode.getNodes());
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on nodeList.
     * @param className the name of the (upper) class of the group's members.
     * @param genericParameters genericParameters parameterizing types
     * @param params the parameters used to build all the group's members.
     * @param nodeList the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Class<?>[],Object[],Node[])} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static Object newGroup(String className, Class<?>[] genericParameters, Object[] params, Node[] nodeList)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Object result = ProActiveGroup.newGroup(className, genericParameters);
        Group g = ProActiveGroup.getGroup(result);

        if (params != null) {
            for (int i = 0; i < nodeList.length; i++) {
                g.add(PAActiveObject.newActive(className, genericParameters, params, nodeList[i % nodeList.length]));
            }
        }

        return result;
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on nodeList.
     * @param className the name of the (upper) class of the group's members.
     * @param genericParameters genericParameters parameterizing types
     * @param params the parameters used to build all the group's members.
     * @param virtualNode the virtual node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Class<?>[],Object[],VirtualNodeInternal)} instead
     */
    @Deprecated
    public static Object newGroup(String className, Class<?>[] genericParameters, Object[] params,
            VirtualNodeInternal virtualNode)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return ProActiveGroup.newGroup(className, genericParameters, params, virtualNode.getNodes());
    }

    /**
     * Creates an object representing a group (a typed group) and creates all members with the same params on the node.
     * @param className the name of the (upper) class of the group's members.
     * @param genericParameters genericParameters parameterizing types
     * @param params the parameters used to build all the group's members.
     * @param node the node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Class<?>[],Object[],Node)} instead
     */
    @Deprecated
    public static Object newGroup(String className, Class<?>[] genericParameters, Object[] params, Node node)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = node;
        return ProActiveGroup.newGroup(className, genericParameters, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates all members with the same params on the node.
     * @param className the name of the (upper) class of the group's members.
     * @param genericParameters genericParameters parameterizing types
     * @param params the parameters used to build all the group's members.
     * @param nodeName the name of the node where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Class<?>[],Object[],String)} instead
     */
    @Deprecated
    public static Object newGroup(String className, Class<?>[] genericParameters, Object[] params, String nodeName)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[1];
        nodeList[0] = NodeFactory.getNode(nodeName);
        return ProActiveGroup.newGroup(className, genericParameters, params, nodeList);
    }

    /**
     * Creates an object representing a group (a typed group) and creates members with the same params cycling on nodeList.
     * @param className the name of the (upper) class of the group's members.
     * @param genericParameters genericParameters parameterizing types
     * @param params the parameters used to build all the group's members.
     * @param nodeListString the names of the nodes where the members are created.
     * @return a typed group with its members.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#newGroup(String,Class<?>[],Object[],String[])} instead
     */
    @Deprecated
    public static Object newGroup(String className, Class<?>[] genericParameters, Object[] params,
            String[] nodeListString)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        Node[] nodeList = new Node[nodeListString.length];
        for (int i = 0; i < nodeListString.length; i++)
            nodeList[i] = NodeFactory.getNode(nodeListString[i]);
        return ProActiveGroup.newGroup(className, genericParameters, params, nodeList);
    }

    /**
     * Turns the target object (a typed group) into an ActiveObject (an active typed group) attached to a default
     * node in the local JVM.
     * @param ogroup the typed group to turn active.
     * @return a reference on the active object produced.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the node was null and that the DefaultNode cannot be created.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#turnActiveGroup(Object,Class<?>[])} instead
     */
    @Deprecated
    public static Object turnActiveGroup(Object ogroup, Class<?>[] genericParameters)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return PAActiveObject.turnActive(ogroup,
                                         genericParameters,
                                         ProActiveGroup.getType(ogroup),
                                         (Node) null,
                                         null,
                                         null);
    }

    /**
     * Turns the target object (a typed group) into an ActiveObject (an active typed group) attached to a specified node.
     * @param ogroup the typed group to turn active.
     * @param node the node where to create the active object on. If <code>null</code>,
     * the active object is created localy on a default node
     * @return a reference (possibly remote) on the active object produced.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the specified node can not be reached.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#turnActiveGroup(Object,Class<?>[],Node)} instead
     */
    @Deprecated
    public static Object turnActiveGroup(Object ogroup, Class<?>[] genericParameters, Node node)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return PAActiveObject.turnActive(ogroup, genericParameters, ProActiveGroup.getType(ogroup), node, null, null);
    }

    /**
     * Turns the target object (a typed group) into an ActiveObject (an active typed group) attached to a specified node.
     * @param ogroup the typed group to turn active.
     * @param nodeName the name of the node where to create the active object on.
     * @return a reference (possibly remote) on the active object produced.
     * @throws ActiveObjectCreationException if a problem occur while creating the stub or the body
     * @throws ClassNotFoundException if the Class<?> corresponding to <code>className</code> can't be found.
     * @throws ClassNotReifiableException if the Class<?> corresponding to <code>className</code> can't be reify.
     * @throws NodeException if the specified node can not be reached.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#turnActiveGroup(Object,Class<?>[],String)} instead
     */
    @Deprecated
    public static Object turnActiveGroup(Object ogroup, Class<?>[] genericParameters, String nodeName)
            throws ClassNotFoundException, ClassNotReifiableException, ActiveObjectCreationException, NodeException {
        return PAActiveObject.turnActive(ogroup,
                                         genericParameters,
                                         ProActiveGroup.getType(ogroup),
                                         NodeFactory.getNode(nodeName),
                                         null,
                                         null);
    }

    /**
     * Gives a view of the group
     * @param ogroup - a typed group
     * @return a typed group, the view of the group
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#captureView(Object)} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static Object captureView(Object ogroup) {
        Object result = null;

        try {
            result = ProActiveGroup.newGroup(ProActiveGroup.getType(ogroup), (Class<?>[]) null);
        } catch (ClassNotReifiableException e) {
            logger.error("**** ClassNotReifiableException ****");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            logger.error("**** ClassNotFoundException ****");
            e.printStackTrace();
        }

        Group go = ProActiveGroup.getGroup(ogroup);
        Group gr = ProActiveGroup.getGroup(result);

        Iterator it = go.iterator();
        while (it.hasNext()) {
            gr.add(it.next());
        }

        return result;
    }

    /**
     * Waits for all the futures are arrived.
     * @param o a typed group.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#waitAll(Object)} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static void waitAll(Object o) {
        if (MOP.isReifiedObject(o)) {
            org.objectweb.proactive.core.mop.Proxy theProxy = ProActiveGroup.findProxyForGroup(o);

            // If the object represents a group, we use the proxyForGroup's method
            if (theProxy != null) {
                ((org.objectweb.proactive.core.group.ProxyForGroup) theProxy).waitAll();
            }

            // Else the "standard waitFor" method has been used in the findProxyForGroup method
        }
    }

    /**
     * Waits for (at least) one future is arrived.
     * @param o a typed group.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#waitOne(Object)} instead
     */
    @Deprecated
    public static void waitOne(Object o) {
        if (MOP.isReifiedObject(o)) {
            org.objectweb.proactive.core.mop.Proxy theProxy = ProActiveGroup.findProxyForGroup(o);

            // If the object represents a group, we use the proxyForGroup's method
            if (theProxy != null) {
                ((org.objectweb.proactive.core.group.ProxyForGroup) theProxy).waitOne();
            }

            // Else the "standard waitFor" method has been used in the findProxyForGroup method
        }
    }

    /**
     * Waits n futures are arrived.
     * @param o a typed group.
     * @param n the number of awaited members.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#waitN(Object,int)} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static void waitN(Object o, int n) {
        if (MOP.isReifiedObject(o)) {
            org.objectweb.proactive.core.mop.Proxy theProxy = ProActiveGroup.findProxyForGroup(o);

            // If the object represents a group, we use the proxyForGroup's method
            if (theProxy != null) {
                ((org.objectweb.proactive.core.group.ProxyForGroup) theProxy).waitN(n);
            }

            // Else the "standard waitFor" method has been used in the findProxyForGroup method
        }
    }

    /**
     * Tests if all the members of the object <code>o</code> representing a group are awaited or not.
     * Always returns <code>false</code> if <code>o</code> is not a reified object (future or group).
     * @param o a typed group.
     * @return <code>true</code> if all the members of <code>o</code> are awaited.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#allAwaited(Object)} instead
     */
    @Deprecated
    public static boolean allAwaited(Object o) {
        // If the object is not reified, it cannot be a future (or a group of future)
        if (!(MOP.isReifiedObject(o))) {
            return false;
        } else {
            org.objectweb.proactive.core.mop.Proxy theProxy = ProActiveGroup.findProxyForGroup(o);

            // If the object represents a group, we use the proxyForGroup's method
            if (theProxy != null) {
                return ((org.objectweb.proactive.core.group.ProxyForGroup) theProxy).allAwaited();
            }
            // Else the "standard waitFor" method has been used in the findProxyForGroup method so the future is arrived
            else {
                return false;
            }
        }
    }

    /**
     * Tests if all the member of the object <code>o</code> representing a group are arrived or not.
     * Always returns <code>true</code> if <code>o</code> is not a reified object (future or group).
     * @param o a typed group.
     * @return <code>true</code> if all the members of <code>o</code> are arrived.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#allArrived(Object)} instead
     */
    @Deprecated
    public static boolean allArrived(Object o) {
        // If the object is not reified, it cannot be a future (or a group of future)
        if (!(MOP.isReifiedObject(o))) {
            return true;
        } else {
            org.objectweb.proactive.core.mop.Proxy theProxy = ProActiveGroup.findProxyForGroup(o);

            // If the object represents a group, we use the proxyForGroup's method
            if (theProxy != null) {
                return ((org.objectweb.proactive.core.group.ProxyForGroup) theProxy).allArrived();
            }
            // Else the "standard waitFor" method has been used in the findProxyForGroup method so the future is arrived
            else {
                return true;
            }
        }
    }

    /**
     * Waits for one future is arrived and get it.
     * @param o a typed group.
     * @return a member of <code>o</code>.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#waitAndGetOne(Object)} instead
     */
    @Deprecated
    public static Object waitAndGetOne(Object o) {
        if (MOP.isReifiedObject(o)) {
            org.objectweb.proactive.core.mop.Proxy theProxy = ProActiveGroup.findProxyForGroup(o);

            // If the object represents a group, we use the proxyForGroup's method
            if (theProxy != null) {
                return ((org.objectweb.proactive.core.group.ProxyForGroup) theProxy).waitAndGetOne();
            }
            // Else the "standard waitFor" method has been used in the findProxyForGroup method so the future is arrived, just return it
            else {
                return o;
            }
        }
        // if o is not a reified object just return it
        else {
            return o;
        }
    }

    /**
     * Waits for one future is arrived and returns it (removes it from the typed group).
     * @param o a typed group.
     * @return a member of <code>o</code>. (<code>o</code> is removed from the typed group)
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#waitAndGetOneThenRemoveIt(Object)} instead
     */
    @Deprecated
    public static Object waitAndGetOneThenRemoveIt(Object o) {
        if (MOP.isReifiedObject(o)) {
            org.objectweb.proactive.core.mop.Proxy theProxy = ProActiveGroup.findProxyForGroup(o);

            // If the object represents a group, we use the proxyForGroup's method
            if (theProxy != null) {
                return ((org.objectweb.proactive.core.group.ProxyForGroup) theProxy).waitAndGetOneThenRemoveIt();
            }
            // Else the "standard waitFor" method has been used in the findProxyForGroup method so the future is arrived, just return it
            else {
                return o;
            }
        }
        // if o is not a reified object just return it
        else {
            return o;
        }
    }

    /**
     * Waitd for the N-th future in the list is arrived.
     * @param o a typed group.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#waitTheNth(Object,int)} instead
     */
    @Deprecated
    public static void waitTheNth(Object o, int n) {
        if (MOP.isReifiedObject(o)) {
            org.objectweb.proactive.core.mop.Proxy theProxy = ProActiveGroup.findProxyForGroup(o);

            // If the object represents a group, we use the proxyForGroup's method
            if (theProxy != null) {
                ((org.objectweb.proactive.core.group.ProxyForGroup) theProxy).waitTheNth(n);
            }

            // Else the "standard waitFor" method has been used in the findProxyForGroup method
        }
    }

    /**
     * Waits for the N-th future is arrived and get it.
     * @param o a typed group.
     * @param n the rank of the awaited member.
     * @return the <code>n</code>-th member of th typed group <code>o</code>.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#waitAndGetTheNth(Object,int)} instead
     */
    @Deprecated
    public static Object waitAndGetTheNth(Object o, int n) {
        if (MOP.isReifiedObject(o)) {
            org.objectweb.proactive.core.mop.Proxy theProxy = ProActiveGroup.findProxyForGroup(o);

            // If the object represents a group, we use the proxyForGroup's method
            if (theProxy != null) {
                return ((org.objectweb.proactive.core.group.ProxyForGroup) theProxy).waitAndGetTheNth(n);
            }
            // Else the "standard waitFor" method has been used in the findProxyForGroup method so the future is arrived, just return it
            else {
                return o;
            }
        }
        // if o is not a reified object just return it
        else {
            return o;
        }
    }

    /**
     * Waits that at least one member is arrived and returns its index.
     * @param o a typed group.
     * @return the index of a non-awaited member of the Group, -1 if <code>o</code> is not a reified object.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#waitOneAndGetIndex(Object)} instead
     */
    @Deprecated
    public static int waitOneAndGetIndex(Object o) {
        if (MOP.isReifiedObject(o)) {
            org.objectweb.proactive.core.mop.Proxy theProxy = ProActiveGroup.findProxyForGroup(o);

            // If the object represents a group, we use the proxyForGroup's method
            if (theProxy != null) {
                return ((org.objectweb.proactive.core.group.ProxyForGroup) theProxy).waitOneAndGetIndex();
            }
            // Else return 0
            else {
                return 0;
            }
        }
        // if o is not a reified object, return -1
        else {
            return -1;
        }
    }

    /**
     * Returns the number of members of the object representing a Group.
     * Throws an IllegalArgumentException if <code>o</code> doesn't represent a Group.
     * @param o a typed group.
     * @return the number of member of the typed group <code>o</code>.
     * @throws IllegalArgumentException if the parameter doesn't represent a group
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#size(Object)} instead
     */
    @Deprecated
    public static int size(Object o) {
        ProxyForGroup theProxy = ProActiveGroup.findProxyForGroup(o);
        if (theProxy == null) {
            throw new java.lang.IllegalArgumentException("Parameter doesn't represent a group");
        } else {
            return theProxy.size();
        }
    }

    /**
     * Returns the member at the specified index of the object representing a Group.
     * Returns <code>null</code> if <code>obj</code> doesn't represent a Group.
     * @param o a typed group.
     * @param n the rank of the wanted member.
     * @return the member of the typed group at the rank <code>n</code>
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#get(Object,int)} instead
     */
    @Deprecated
    public static Object get(Object o, int n) {
        org.objectweb.proactive.core.mop.Proxy theProxy = ProActiveGroup.findProxyForGroup(o);
        if (theProxy == null) {
            return null;
        } else {
            return ((org.objectweb.proactive.core.group.ProxyForGroup) theProxy).get(n);
        }
    }

    /**
     * Checks if the object <code>o</code> is an object representing a Group (future or not).
     * @param o the Object to check.
     * @return <code>true</code> if <code>o</code> is a typed group.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#isGroup(Object)} instead
     */
    @Deprecated
    public static boolean isGroup(Object o) {
        return (ProActiveGroup.findProxyForGroup(o) != null);
    }

    /**
     * Allows the typed group to dispatch parameters
     * @param ogroup the typed group who will change his semantic of communication.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#setScatterGroup(Object)} instead
     */
    @Deprecated
    public static void setScatterGroup(Object ogroup) {
        Proxy proxytmp = ProActiveGroup.findProxyForGroup(ogroup);
        if (proxytmp != null) {
            ((ProxyForGroup) proxytmp).setDispatchingOn();
        }
    }

    /**
     * By default, when a rendez-vous fails an exception is thrown. Instead,
     * when the automatic purge is enabled, failing objects are removed from
     * the group
     * @param ogroup the typed group having its behavior changed
     * @param autoPurge the new behavior
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#setAutomaticPurge(Object)} instead
     */
    @Deprecated
    public static void setAutomaticPurge(Object ogroup, boolean autoPurge) {
        ProxyForGroup proxytmp = ProActiveGroup.findProxyForGroup(ogroup);
        if (proxytmp == null) {
            throw new IllegalArgumentException("argument " + ogroup.getClass().getName() + " is not a group");
        }
        proxytmp.setAutomaticPurge(autoPurge);
    }

    /**
     * Allows the typed group to broadcast parameters
     * @param ogroup the typed group who will change his semantic of communication.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#unsetScatterGroup(Object)} instead
     */
    @Deprecated
    public static void unsetScatterGroup(Object ogroup) {
        Proxy proxytmp = ProActiveGroup.findProxyForGroup(ogroup);
        if (proxytmp != null) {
            ((ProxyForGroup) proxytmp).setDispatchingOff();
        }
    }

    /**
     * Allows the typed group to make an unique serialization of parameters when a broadcast call occurs.
     * @param ogroup the typed group who will change his semantic of communication.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#setUniqueSerialization(Object)} instead
     */
    @Deprecated
    public static void setUniqueSerialization(Object ogroup) {
        Proxy proxytmp = ProActiveGroup.findProxyForGroup(ogroup);
        if (proxytmp != null) {
            ((ProxyForGroup) proxytmp).setUniqueSerializationOn();
        }
    }

    /**
     * Removes the ability of a typed group to make an unique serialization
     * @param ogroup the typed group who will change his semantic of communication.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#unsetUniqueSerialization(Object)} instead
     */
    @Deprecated
    public static void unsetUniqueSerialization(Object ogroup) {
        Proxy proxytmp = ProActiveGroup.findProxyForGroup(ogroup);
        if (proxytmp != null) {
            ((ProxyForGroup) proxytmp).setUniqueSerializationOff();
        }
    }

    /**
     * Checks the semantic of communication of the typed group <code>ogroup</code>.
     * @param ogroup a typed group.
     * @return <code>true</code> if the "scatter option" is enabled for the typed group <code>ogroup</code>.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#isScatterGroupOn(Object)} instead
     */
    @Deprecated
    public static boolean isScatterGroupOn(Object ogroup) {
        Proxy proxytmp = ProActiveGroup.findProxyForGroup(ogroup);
        if (proxytmp != null) {
            return ((ProxyForGroup) proxytmp).isDispatchingOn();
        } else {
            return false;
        }
    }

    /**
     * Returns the ProxyForGroup of the typed group <code>ogroup</code>.
     * @param ogroup the typed group.
     * @return the <code>ProxyForGroup</code> of the typed group <code>ogroup</code>.
     * <code>null</code> if <code>ogroup</code> does not represent a Group.
     * @deprecated Use {@link org.objectweb.proactive.api.PAGroup#findProxyForGroup(Object)} instead
     */
    @Deprecated
    static ProxyForGroup findProxyForGroup(Object ogroup) {
        if (!(MOP.isReifiedObject(ogroup))) {
            return null;
        } else {
            Proxy tmp = ((StubObject) ogroup).getProxy();

            // obj is an object representing a Group (and not a future)
            if (tmp instanceof org.objectweb.proactive.core.group.ProxyForGroup) {
                return (org.objectweb.proactive.core.group.ProxyForGroup) tmp;
            }

            // obj is a future ... but may be a future-Group
            while (tmp instanceof org.objectweb.proactive.core.body.future.FutureProxy)
                // future of future ...
                if (MOP.isReifiedObject(((FutureProxy) tmp).getResult())) {
                    tmp = ((StubObject) ((FutureProxy) tmp).getResult()).getProxy();
                }
                // future of standard object (or group proxy in case of multicast interfaces)
                else {
                    if (((FutureProxy) tmp).getResult() instanceof ProxyForGroup) {
                        return (ProxyForGroup) ((FutureProxy) tmp).getResult();
                    }
                    return null;
                }

            // future-Group
            if (tmp instanceof org.objectweb.proactive.core.group.ProxyForGroup) {
                return (org.objectweb.proactive.core.group.ProxyForGroup) tmp;
            }
            // future of an active object
            else {
                return null;
            }
        }
    }
}
