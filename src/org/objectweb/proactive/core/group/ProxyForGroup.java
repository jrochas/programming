package org.objectweb.proactive.core.group;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.proxy.AbstractProxy;
import org
	.objectweb
	.proactive
	.core
	.mop
	.ConstructionOfReifiedObjectFailedException;
import org.objectweb.proactive.core.mop.ConstructorCall;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.Proxy;
import org.objectweb.proactive.core.mop.StubObject;

import org.objectweb.proactive.core.node.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Collections;
import java.lang.reflect.InvocationTargetException;

/**
 * This proxy class manages the semantic of group communication and implements the Group Interface.
 *
 * @author Laurent Baduel - INRIA
 * @see org.objectweb.proactive.core.mop.Proxy
 *
 */
public class ProxyForGroup
	extends AbstractProxy
	implements org.objectweb.proactive.core.mop.Proxy, Group, java.io.Serializable {

	/** The name of the Class : all members of the group are "className" assignable */
	protected String className;

	/** The list of member */
	protected List memberListStub;

	/** The list of the proxies of members */
	protected List memberListProxy;

	/** Unique identificator for body (avoid infinite loop in some hierarchicals groups) */ // NOT YET IMPLEMENTED !!!
	protected UniqueID proxyForGroupID;

	/** Number of awaited call of method on the group's member : The Semantic is that we wait all call are done before continuing */
	protected int waited = 0;

	/** Use to monitor futures */
	protected FutureListGroup futureList;

	/* ----------------------- CONSTRUCTORS ----------------------- */

	public ProxyForGroup(String nameOfClass)
		throws ConstructionOfReifiedObjectFailedException {
		className = nameOfClass;
		memberListStub = Collections.synchronizedList(new ArrayList());
		memberListProxy = Collections.synchronizedList(new ArrayList());
		futureList =
			new FutureListGroup(
				memberListStub,
				((AbstractBody) ProActive.getBodyOnThis()).getFuturePool());
		proxyForGroupID = new UniqueID();
	}

	public ProxyForGroup(String nameOfClass, Integer size)
		throws ConstructionOfReifiedObjectFailedException {
		className = nameOfClass;
		memberListStub = Collections.synchronizedList(new ArrayList());
		memberListProxy = Collections.synchronizedList(new ArrayList());
		for (int i = 0; i < size.intValue(); i++) {
			memberListStub.add(null);
			memberListProxy.add(null);
		}
		futureList =
			new FutureListGroup(
				memberListStub,
				((AbstractBody) ProActive.getBodyOnThis()).getFuturePool());
		proxyForGroupID = new UniqueID();
	}

	public ProxyForGroup() throws ConstructionOfReifiedObjectFailedException {
		memberListStub = Collections.synchronizedList(new ArrayList());
		memberListProxy = Collections.synchronizedList(new ArrayList());
		futureList =
			new FutureListGroup(
				memberListStub,
				((AbstractBody) ProActive.getBodyOnThis()).getFuturePool());
		proxyForGroupID = new UniqueID();
	}

	public ProxyForGroup(ConstructorCall c, Object[] p)
		throws ConstructionOfReifiedObjectFailedException {
		memberListStub = Collections.synchronizedList(new ArrayList());
		memberListProxy = Collections.synchronizedList(new ArrayList());
		futureList =
			new FutureListGroup(
				memberListStub,
				((AbstractBody) ProActive.getBodyOnThis()).getFuturePool());
		proxyForGroupID = new UniqueID();
	}

	/* ------------------------ THE PROXY'S METHOD ------------------------ */

	/** The proxy's method, implements the semantic of communication */
	public Object reify(MethodCall c) throws InvocationTargetException {

		/* result will be a stub on a proxy for group representing the group of results */
		Object result = null;

		/* A barrier of synchronisation (facultative) to be sur that all eventual previous calls are done */
		//this.waitForAllCallsDone();

		/* if OneWay : do not construct result */
		if (AbstractProxy.isOneWayCall(c)) {
			System.out.println(" APPEL ONEWAY : " + c.getName());
			oneWayCallOnGroup(c);
		}

		/* if the call is asynchronous the group of result will be a group a future */
		else // if (AbstractProxy.isAsynchronousCall (c))                // SYNC == ASYNC !!!!
			{
			System.out.println(" APPEL ASYNCHRONE : " + c.getName());
			result = asynchronousCallOnGroup(c);
		}

		/* A barrier of synchronisation to be sur that all calls are done before continuing the execution */
		this.waitForAllCallsDone();

		return result;
	}

	/* -------------------------- METHOD FOR THREAD CREATION AND BARRIER OF SYNCHRONISATION -------------------------- */

	/* ------------- GENERAL ------------- */

	/** Wait until the method has been apply to all the member */
	private synchronized void waitForAllCallsDone() {
		System.out.println("On bloque : il reste " + waited + " threads");
		while (this.waited != 0) {
			try {
				wait();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("On debloque");
	}

	private synchronized void decrementWaitedAndNotifyAll() {
		waited--;
		System.out.println("une thread libere, reste " + waited);
		notifyAll();
	}

	/* ------------ FOR ASYNCHRONOUS CALL ------------ */

	/**
	 * Create and initialize (and return) the group of result, then launch threads for asynchronous call of each member
	 */
	protected Object asynchronousCallOnGroup(MethodCall c) {
		Object result;
		int size = memberListProxy.size();
		/* Creates a stub + ProxyForGroup for representing the result */
		try {
			Object[] paramProxy = new Object[0];
			result =
				MOP.newInstance(
					c.getReifiedMethod().getReturnType().getName(),
					null,
					"org.objectweb.proactive.core.group.ProxyForGroup",
					paramProxy);
			((ProxyForGroup) ((StubObject) result).getProxy()).className =
				c.getReifiedMethod().getReturnType().getName();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		// Init the lists of result with null value to permit the "set(index)" operation
		List memberListStubOfResult =
			((ProxyForGroup) ((StubObject) result).getProxy()).memberListStub;
		List memberListProxyOfResult =
			((ProxyForGroup) ((StubObject) result).getProxy()).memberListProxy;
		for (int i = 0; i < size; i++) {
			memberListStubOfResult.add(null);
			memberListProxyOfResult.add(null);
		}

		// Creating Threads
		for (int i = 0; i < size; i++) {
			(
				(ProxyForGroup) ((StubObject) result)
					.getProxy())
					.createThreadForAsync(
				this.memberListProxy,
				memberListProxyOfResult,
				memberListStubOfResult,
				i,
				c);
		}
		return result;
	}

	private synchronized void createThreadForAsync(
		List memberListProxyOfThis,
		List memberListProxyOfResult,
		List memberListStubOfResult,
		int index,
		MethodCall mc) {
		new Thread(
			new MyProcessForGroupAsync(
				memberListProxyOfThis,
				memberListProxyOfResult,
				memberListStubOfResult,
				index,
				mc))
			.start();
		waited++;
	}

	private synchronized void addToListOfResult(
		List arrayProxy,
		List arrayStub,
		Object o,
		int index) {
		arrayStub.set(index, o);
		arrayProxy.set(index, ((StubObject) o).getProxy());
		decrementWaitedAndNotifyAll();
	}

	/* -------------------- FOR ONEWAY CALL ---------------------- */

	/**
	 * Launch threads for OneWay call of each member
	 */
	protected void oneWayCallOnGroup(MethodCall c) {
		int size = memberListProxy.size();

		// Creating Threads
		for (int i = 0; i < size; i++)
			this.createThreadForOneWay(this.memberListProxy, i, c);
	}

	private synchronized void createThreadForOneWay(
		List memberListProxyOfThis,
		int index,
		MethodCall mc) {
		new Thread(
			new MyProcessForGroupOneWay(
				this,
				memberListProxyOfThis,
				index,
				mc))
			.start();
		waited++;
	}

	/* -------------------- INNER CLASS ----------------- */

	private class MyProcessForGroupAsync implements Runnable {
		List listOfFather;
		List listOfResultProxy;
		List listOfResultStub;
		int index;
		MethodCall mc;

		MyProcessForGroupAsync(
			List father,
			List resultProxy,
			List resultStub,
			int index,
			MethodCall mc) {
			this.listOfFather = father;
			this.listOfResultProxy = resultProxy;
			this.listOfResultStub = resultStub;
			this.index = index;
			this.mc = mc;
		}

		public void run() {
			try {

				Object tmp = ((Proxy) (listOfFather.get(index))).reify(mc);
				addToListOfResult(
					listOfResultProxy,
					listOfResultStub,
					tmp,
					index);

			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private class MyProcessForGroupOneWay implements Runnable {
		List listOfFather;
		int index;
		MethodCall mc;
		ProxyForGroup papa;

		MyProcessForGroupOneWay(
			ProxyForGroup pere,
			List father,
			int index,
			MethodCall mc) {
			this.listOfFather = father;
			this.index = index;
			this.mc = mc;
			this.papa = pere;
		}

		public void run() {
			try {
				((Proxy) (listOfFather.get(index))).reify(mc);
				this.papa.decrementWaitedAndNotifyAll();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	/* --------------- THE GROUP'S METHOD ------------------ */

	/** If o is a reified object and if it is "assignableFrom" the class of the group, add it into the group 
	if o is a group merge it into the group 
	if o is not a reified object nor a group : do nothing */
	public void add(Object o) {
		try {

			/* if o is an reified object and if it is "assignableFrom" the class of the group, ... add it into the group */
			if ((MOP.isReifiedObject(o))
				&& ((MOP.forName(this.className))
					.isAssignableFrom(o.getClass()))) {
				memberListStub.add(o);
				memberListProxy.add(((StubObject) o).getProxy());
			}

			/* if o is a Group */
			else if (
				o
					instanceof org.objectweb.proactive.core.group.ProxyForGroup) {
				/* like an addMerge call */
				memberListStub.addAll(
					(
						(
							org
								.objectweb
								.proactive
								.core
								.group
								.ProxyForGroup) o)
								.memberListStub);
				memberListProxy.addAll(
					(
						(
							org
								.objectweb
								.proactive
								.core
								.group
								.ProxyForGroup) o)
								.memberListProxy);
			}
		} catch (java.lang.ClassNotFoundException e) {
			System.err.println("Unknown class : " + this.className);
		}
	}

	/** Add all member of the group oGroup into the Group */
	public void addMerge(Object oGroup) {
		try {
			/* check oGroup is an Reified Object and if it is "assignableFrom" the class of the group */
			if ((MOP.isReifiedObject(oGroup))
				&& ((MOP.forName(this.className))
					.isAssignableFrom(oGroup.getClass()))) {

				/* check oGroup is an object representing a group */
				if (((StubObject) oGroup).getProxy()
					instanceof org.objectweb.proactive.core.group.ProxyForGroup) {

					memberListStub.addAll(
						(
							(ProxyForGroup) ((StubObject) oGroup)
								.getProxy())
								.memberListStub);
					memberListProxy.addAll(
						(
							(ProxyForGroup) ((StubObject) oGroup)
								.getProxy())
								.memberListProxy);

				}

				/* if oGroup is a Standard Active Object (but not a group), just add it */
				else
					this.add(oGroup);

			}
			/* if oGroup is a Group */
			else if (
				oGroup
					instanceof org.objectweb.proactive.core.group.ProxyForGroup) {
				memberListStub.addAll(
					(
						(
							org
								.objectweb
								.proactive
								.core
								.group
								.ProxyForGroup) oGroup)
								.memberListStub);
				memberListProxy.addAll(
					(
						(
							org
								.objectweb
								.proactive
								.core
								.group
								.ProxyForGroup) oGroup)
								.memberListProxy);
			}
		} catch (java.lang.ClassNotFoundException e) {
			System.err.println("Unknown class : " + this.className);
		}
	}

	/**
	 * Returns the index of the first occurence of the specified Object <code>obj</code>
	 * Returns -1 if the list does not contain this object.
	 */
	public synchronized int indexOf(Object obj) {
		return memberListStub.indexOf(obj);
	}

	/**
	 * Removes the element at the specified position.
	 */
	public synchronized void remove(int index) {
		memberListStub.remove(index);
		memberListProxy.remove(index);
	}

	/**
	 * Returns the i-th member of the group
	 */
	public Object get(int i) {
		this.waitForAllCallsDone();
		return memberListStub.get(i);
	}

	/**
	 * Returns the number of member in the group
	 */
	public int size() {
		return memberListProxy.size();
	}

	/**
	 * Returns a ListIterator of the member in the group
	 */
	public ListIterator iterator() {
		return memberListStub.listIterator();
	}

	/** Return the ("higher") Class of group's member */
	public Class getType() throws java.lang.ClassNotFoundException {
		return MOP.forName(className);
	}

	/** Return the full name of ("higher") Class of group's member */
	public String getTypeName() {
		return className;
	}

	/** Return an Object representing the group */
	public Object getGrouped() {

		Object result;

		try { // an unuseful proxy is generated
			result =
				MOP.newInstance(
					this.className,
					null,
					"org.objectweb.proactive.group.ProxyForGroup",
					null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		((ProxyForGroup) ((StubObject) result).getProxy()).memberListProxy =
			this.memberListProxy;
		((ProxyForGroup) ((StubObject) result).getProxy()).className =
			this.className;
		((ProxyForGroup) ((StubObject) result).getProxy()).proxyForGroupID =
			this.proxyForGroupID;
		return result;
	}

	//  This is the best thing to do, but createStubObject has a private acces !!!!	

	// 	// Instanciates the stub object
	// 	StubObject stub = MOP.createStubObject(this.className, MOP.forName(this.className));

	// 	// Connects the proxy to the stub
	// 	stub.setProxy(this);
	// 	return stub;
	//  }

	//  An other way is to "store" the stub and return it when asked

	/** To debug, display all members and there position */
	public void testClass() {
		System.out.println(" Nombre d'elements : " + memberListStub.size());
		for (int i = 0; i < memberListStub.size(); i++) {
			System.out.println(
				i + " : " + memberListStub.get(i).getClass().getName());
		}
	}

	/* ------------------------ METHODS FOR SYNCHRONOUS CREATION OF A TYPED GROUP ------------------------ */

	public int thewaitedCreation;
	public synchronized void incrementTheWaitedCreation() {
		thewaitedCreation++;
	}
	public synchronized void decrementTheWaitedCreation() {
		thewaitedCreation--;
		if (thewaitedCreation == 0) {
			notifyAll();
		}
	}

	public synchronized void waitForTheWaitedCreation() {
		while (thewaitedCreation != 0) {
			try {
				wait();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void createThreadCreation(
		String className,
		Object[] param,
		String leNode) {

		new Thread(
			new MyProcessForGroupCreation(this, className, param, leNode))
			.start();
		incrementTheWaitedCreation();
	}

	private class MyProcessForGroupCreation implements Runnable {
		Group theGroup;
		String classeName;
		Object[] leParam;
		String leNode;

		MyProcessForGroupCreation(
			Group pere,
			String cN,
			Object[] param,
			String node) {
			this.theGroup = pere;
			this.classeName = cN;
			this.leParam = param;
			this.leNode = node;
		}

		public void run() {
			try {

				theGroup.add(ProActive.newActive(classeName, leParam, leNode));
				((org.objectweb.proactive.core.group.ProxyForGroup) theGroup)
					.decrementTheWaitedCreation();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/* ------------------------ PRIVATE METHODS FOR SERIALIZATION --------------------- */

	private void writeObject(java.io.ObjectOutputStream out)
		throws java.io.IOException {
		this.waitForAllCallsDone();
		// Now that all the results are available, we can copy the group (of future)
		out.defaultWriteObject();

	}

	//for the moment, we set the value of migration to false here
	private void readObject(java.io.ObjectInputStream in)
		throws java.io.IOException, ClassNotFoundException {
		in.defaultReadObject();
	}

}
