/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.cdk.applications.plugin;

import java.io.Reader;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;

/**
 * Interface that applications must have an implementation for in
 * order to have plugins perform operations in the application.
 * For example, Jmol's DisplayControl class implements this interface.
 *
 * @cdk.module applications
 *
 * @version $Revision$
 */
public interface CDKEditBus {

    /**
     * Should return the version of the implemented CDKEditBus API.
     * This can be used by plugins to see what features are available.
     */
    public String getAPIVersion();

    /**
     * Tells the application in which the plugin is loaded to
     * show the given ChemFile.
     *
     * @see org.openscience.cdk.ChemFile
     */
    public void showChemFile(ChemFile file);
    /**
     * Tells the application in which the plugin is loaded to
     * show the chemistry in the given String.
     */
    public void showChemFile(Reader file);
    /**
     * Tells the application in which the plugin is loaded to
     * show the given ChemModel.
     *
     * @see org.openscience.cdk.ChemModel
     */
    public void showChemModel(ChemModel model);

    /**
     * Asks the application to return the active ChemModel.
     * It may return a <code>NoSuchMethodError</code> when the
     * application does not implement this method.
     *
     * @see org.openscience.cdk.ChemFile
     */
    public ChemModel getChemModel();
    /**
     * Asks the application to return the active ChemFile.
     * It may return a <code>NoSuchMethodError</code> when the
     * application does not implement this method.
     *
     * @see org.openscience.cdk.ChemModel
     */
    public ChemFile getChemFile();
    
}


