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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk.test.config;

import java.io.InputStream;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.*;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.config.TXTBasedAtomTypeConfigurator;

/**
 * Checks the functionality of the TXTBasedAtomTypeConfigurator.
 *
 * @cdk.module test
 */
public class TXTBasedAtomTypeConfiguratorTest extends CDKTestCase
{

    public TXTBasedAtomTypeConfiguratorTest(String name) {
        super(name);
    }
    
    public void setUp() {}
    
    public static Test suite() {
        return new TestSuite(TXTBasedAtomTypeConfiguratorTest.class);
    }

    public void testTXTBasedAtomTypeConfigurator() throws Exception {
        TXTBasedAtomTypeConfigurator configurator = new TXTBasedAtomTypeConfigurator();
        assertNotNull(configurator);
    }
    
    public void testReadAtomTypes() throws Exception {
        String configFile = "org/openscience/cdk/config/data/jmol_atomtypes.xml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
        TXTBasedAtomTypeConfigurator configurator = new TXTBasedAtomTypeConfigurator();
        configurator.setInputStream(ins);
        assertNotSame(new Integer(0), new Integer(configurator.readAtomTypes().size()));
    }
    
    public void testSetInputStream_InputStream() throws Exception {
        String configFile = "org/openscience/cdk/config/data/jmol_atomtypes.xml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
        TXTBasedAtomTypeConfigurator configurator = new TXTBasedAtomTypeConfigurator();
        configurator.setInputStream(ins);
    }
    
}
