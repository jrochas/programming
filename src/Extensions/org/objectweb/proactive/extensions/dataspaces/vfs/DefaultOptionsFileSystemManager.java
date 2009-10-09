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
package org.objectweb.proactive.extensions.dataspaces.vfs;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;


/**
 * Extension of DefaultFileSystemManager, providing default FileSystemOptions capability.
 * <p>
 * If some file is resolved without FileSystemOptions explicitly given and there is no baseFile
 * configured on manager that could be used as source of this options, resolve uses provided default
 * options for this manager.
 */
public class DefaultOptionsFileSystemManager extends DefaultFileSystemManager {
    private volatile FileSystemOptions defaultOptions;

    /**
     * Creates VFS manager with provided default options.
     *
     * @param defaultOptions
     *            default options to use in case options are not explicitly given in resolve or
     *            implicitly acquired from baseFile. May be <code>null</code>
     */
    public DefaultOptionsFileSystemManager(final FileSystemOptions defaultOptions) {
        super();
        this.defaultOptions = defaultOptions;
    }

    /**
     * @return default options this manager use in case options are not explicitly given in resolve
     *         or implicitly acquired from baseFile. May be <code>null</code>
     */
    public FileSystemOptions getDefaultOptions() {
        return defaultOptions;
    }

    /**
     * @param defaultOptions
     *            default options to use in case options are not explicitly given in resolve or
     *            implicitly acquired from baseFile. May be <code>null</code>
     */
    public void setDefaultOptions(FileSystemOptions defaultOptions) {
        this.defaultOptions = defaultOptions;
    }

    @Override
    public FileObject resolveFile(FileObject baseFile, String uri) throws FileSystemException {
        final FileSystemOptions options;
        if (baseFile == null)
            options = defaultOptions;
        else
            options = baseFile.getFileSystem().getFileSystemOptions();
        return resolveFile(baseFile, uri, options);
    }
}