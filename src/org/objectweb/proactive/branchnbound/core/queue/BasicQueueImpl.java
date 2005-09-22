/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.branchnbound.core.queue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Vector;

import org.objectweb.proactive.branchnbound.core.Task;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;


public class BasicQueueImpl implements TaskQueue {
    private Vector queue = new Vector();
    private int hungryLevel;
    private Task rootTask = null;

    public BasicQueueImpl() {
    }

    /**
     * @see org.objectweb.proactive.branchnbound.core.queue.TaskQueue#addAll(java.util.Collection)
     */
    public void addAll(Collection tasks) {
        if (tasks.size() > 0) {
            queue.addAll(tasks);
            logger.info("Task provider just received and added " +
                tasks.size());
        }
    }

    /**
     * @see org.objectweb.proactive.branchnbound.core.queue.TaskQueue#size()
     */
    public IntWrapper size() {
        return new IntWrapper(this.queue.size());
    }

    /**
     * @see org.objectweb.proactive.branchnbound.core.queue.TaskQueue#hasNext()
     */
    public BooleanWrapper hasNext() {
        return new BooleanWrapper(this.queue.size() > 0);
    }

    /**
     * @see org.objectweb.proactive.branchnbound.core.queue.TaskQueue#next()
     */
    public Task next() {
        return (Task) this.queue.remove(0);
    }

    public void flushAll() {
        this.queue.removeAllElements();
    }

    public BooleanWrapper isHungry() {
        if (logger.isInfoEnabled()) {
            logger.info("Queue size is " + this.queue.size() +
                " - Hungry level is " + this.hungryLevel);
        }
        return new BooleanWrapper(this.queue.size() < this.hungryLevel);
    }

    public void setHungryLevel(int level) {
        this.hungryLevel = level;
    }

    public void backupTasks(Task rootTask) {
        File currentBck = new File(backupTaskFile);
        File oldBck = new File(backupTaskFile + "~");
        if (currentBck.exists()) {
            oldBck.delete();
            currentBck.renameTo(oldBck);
        }
        currentBck = new File(backupTaskFile);
        try {
            FileOutputStream fos = new FileOutputStream(currentBck);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(rootTask);
            for (int i = 0; i < this.queue.size(); i++) {
                oos.writeObject(this.queue.get(i));
            }
            fos.close();
            oos.close();
        } catch (FileNotFoundException e) {
            logger.warn("Backup tasks failed", e);
        } catch (IOException e) {
            logger.warn("Backup tasks failed", e);
        }
    }

    public void loadTasks(File taskFile) {
        try {
            FileInputStream fis = new FileInputStream(taskFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.rootTask = (Task) ois.readObject();
            while (ois.available() > 0) {
                this.queue.add((Task) ois.readObject());
            }
            ois.close();
            fis.close();
        } catch (Exception e) {
            logger.fatal("Failed to read tasks", e);
            throw new ProActiveRuntimeException(e);
        }
    }

    public Task getRootTaskFromBackup() {
        return this.rootTask;
    }
}
