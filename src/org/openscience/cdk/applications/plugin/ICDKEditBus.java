/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *  Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.applications.plugin;

import java.io.Reader;

import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;

/**
 * Interface that applications must have an implementation for in
 * order to have plugins perform operations in the application.
 * For example, Jmol's DisplayControl class implements this interface.
 *
 * @cdk.module applications
 *
 * @version $Revision$
 */
public interface ICDKEditBus {

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
    public void showChemFile(IChemFile file);
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
    public void showChemModel(IChemModel model);

    /**
     * Asks the application to return the active ChemModel.
     * It may return a <code>NoSuchMethodError</code> when the
     * application does not implement this method.
     *
     * @see org.openscience.cdk.ChemFile
     */
    public IChemModel getChemModel();
    /**
     * Asks the application to return the active ChemFile.
     * It may return a <code>NoSuchMethodError</code> when the
     * application does not implement this method.
     *
     * @see org.openscience.cdk.ChemModel
     */
    public IChemFile getChemFile();
    
    /**
     * Runs a script in the application implementing this EditBus. The
     * script language is indicated by its MIME type, e.g. Rasmol scripts
     * are indicated with the MIME type application/x-rasmol.
     */
    public void runScript(String mimeType, String script);
    
}


