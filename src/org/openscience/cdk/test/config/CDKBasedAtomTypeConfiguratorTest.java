/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
 * 
 */
package org.openscience.cdk.test.config;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.CDKBasedAtomTypeConfigurator;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of the TXTBasedAtomTypeConfigurator.
 *
 * @cdk.module test
 */
public class CDKBasedAtomTypeConfiguratorTest extends CDKTestCase
{

    public CDKBasedAtomTypeConfiguratorTest(String name) {
        super(name);
    }
    
    public void setUp() {}
    
    public static Test suite() {
        return new TestSuite(CDKBasedAtomTypeConfiguratorTest.class);
    }

    public void testCDKBasedAtomTypeConfigurator() throws Exception {
        CDKBasedAtomTypeConfigurator configurator = new CDKBasedAtomTypeConfigurator();
        assertNotNull(configurator);
    }
    
    public void testReadAtomTypes_IChemObjectBuilder() throws Exception {
        CDKBasedAtomTypeConfigurator configurator = new CDKBasedAtomTypeConfigurator();
        String configFile = "org/openscience/cdk/config/data/structgen_atomtypes.xml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
        configurator.setInputStream(ins);
        assertNotSame(new Integer(0), new Integer(
            configurator.readAtomTypes(new ChemObject().getBuilder()).size())
        );
    }
    
    public void testSetInputStream_InputStream() throws Exception {
        String configFile = "org/openscience/cdk/config/data/structgen_atomtypes.xml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
        CDKBasedAtomTypeConfigurator configurator = new CDKBasedAtomTypeConfigurator();
        configurator.setInputStream(ins);
    }
    
}
