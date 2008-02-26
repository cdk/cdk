/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.config;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.atomtypes.AtomTypeReader;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * AtomType resource that reads the atom type configuration from an XML file.
 * The format is required to be in the STMML format {@cdk.cite PMR2002}; examples
 * can be found in CVS in the src/org/openscience/cdk/config/data directory.
 *
 * @cdk.module core
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.config.CDKBasedAtomTypeConfiguratorTest")
public class CDKBasedAtomTypeConfigurator implements IAtomTypeConfigurator {

    private String configFile = "org.openscience.cdk.config.data.structgen_atomtypes.xml";
    private InputStream ins = null;
    
    private LoggingTool logger;
    
    public CDKBasedAtomTypeConfigurator() {
        logger = new LoggingTool(this);
    }

    @TestMethod("testSetInputStream_InputStream")
    public void setInputStream(InputStream ins) {
        this.ins = ins;
    }
    
    /**
     * Reads the atom types from the CDK based atom type list.
     * 
     * @param builder IChemObjectBuilder used to construct the IAtomType's.
     * @throws        IOException when a problem occured with reading from the InputStream
     * @return        A List with read IAtomType's.
     */
    @TestMethod("testReadAtomTypes_IChemObjectBuilder")
    public List<IAtomType> readAtomTypes(IChemObjectBuilder builder) throws IOException {
        List<IAtomType> atomTypes;
        if (ins == null) {
            try {
                ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
            } catch(Exception exc) {
                logger.error(exc.getMessage());
                logger.debug(exc);
                throw new IOException("There was a problem getting a stream for " + configFile +
                                      " with getClass.getClassLoader.getResourceAsStream");
            }
            if (ins == null) {
                try {
                    ins = this.getClass().getResourceAsStream(configFile);
                } catch(Exception exc) {
                    logger.error(exc.getMessage());
                    logger.debug(exc);
                    throw new IOException("There was a problem getting a stream for " + configFile +
                                          " with getClass.getResourceAsStream");
                }
            }
        }
        if (ins == null) throw new IOException("There was a problem getting an input stream");
        AtomTypeReader reader = new AtomTypeReader(new InputStreamReader(ins));
        atomTypes = reader.readAtomTypes(builder);
        for (IAtomType atomType : atomTypes) {
            if (atomType == null) {
                logger.debug("Expecting an object but found null!");
            }
        }
        return atomTypes;
    }
   
}
