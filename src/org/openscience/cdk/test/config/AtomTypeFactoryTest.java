/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;


/**
 * Checks the funcitonality of the AtomTypeFactory
 *
 * @cdk.module test
 */
public class AtomTypeFactoryTest extends TestCase {

    AtomTypeFactory atf = null;
    
	public AtomTypeFactoryTest(String name) {
		super(name);
	}
	
	public void setUp() {
		try {
			atf = AtomTypeFactory.getInstance();
		} catch(Exception exc) {
            System.out.println("AtomTypeFactoryTest.setup: ");
            exc.printStackTrace();
			fail("Problem instantiating AtomTypeFactory: " +  exc.getMessage());
		}
    }
	
	public static Test suite() {
		return new TestSuite(AtomTypeFactoryTest.class);
	}

	public void testAtomTypeFactory() {
        assertTrue(atf != null);
		assertTrue(atf.getSize() > 0);
    }
    
    public void testGetInstanceInputStream() {
        try {
            String configFile = "org/openscience/cdk/config/data/structgen_atomtypes.xml";
            InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
            AtomTypeFactory atf = AtomTypeFactory.getInstance(ins, "xml");
            assertNotNull(atf);
            assertTrue(atf.getSize() > 0);
        } catch (Exception exception) {
            fail();
        }
    }
    
    public void testGetInstance() {
        try {
            AtomTypeFactory atf = AtomTypeFactory.getInstance();
            assertNotNull(atf);
            assertTrue(atf.getSize() > 0);
        } catch (Exception exception) {
            fail();
        }
    }
    
    public void testGetAtomType() {
		AtomType atomType = null;
		try {
			atomType = atf.getAtomType("C4");
		} catch(Exception exc) {
			fail("Problem getting AtomType for 'structgen.C4' from AtomTypeFactory: "  +  exc.getMessage());
		}
		
		
        assertEquals("C", atomType.getSymbol());
        assertEquals("C4", atomType.getAtomTypeName());
		assertEquals(4.0, atomType.getBondOrderSum(), 0.001);
		assertEquals(3.0, atomType.getMaxBondOrder(), 0.0001);
	}

    public void testGetAtomTypeFromValency() {
		AtomType atomType = null;
		try {
            AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/valency_atomtypes.xml");
			atomType = factory.getAtomType("Oplus");
		} catch(Exception exc) {
			fail("Problem getting AtomType for 'valency:O+' from AtomTypeFactory: "  +  exc.getMessage());
		}
		
        assertEquals("O", atomType.getSymbol());
        assertEquals("Oplus", atomType.getAtomTypeName());
		assertEquals(1, atomType.getFormalCharge());
		assertEquals(3.0, atomType.getBondOrderSum(), 0.0001);
		assertEquals(2.0, atomType.getMaxBondOrder(), 0.0001);
	}

    public void testGetAtomTypeFromHybrid() {
		AtomType atomType = null;
		try {
            AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/hybridization_atomtypes.xml");
			atomType = factory.getAtomType("C.sp2");
		} catch(Exception exc) {
			fail("Problem getting AtomType for 'hybridization:C.sp2' from AtomTypeFactory: "  +  exc.getMessage());
		}
		
        assertEquals("C", atomType.getSymbol());
        assertEquals("C.sp2", atomType.getAtomTypeName());
		assertEquals(0, atomType.getFormalCharge());
		assertEquals(4.0, atomType.getBondOrderSum(), 0.0001);
		assertEquals(2.0, atomType.getMaxBondOrder(), 0.0001);
		assertEquals(CDKConstants.HYBRIDIZATION_SP2, atomType.getHybridization());
	}

    public void testConfigure_Atom() {
		AtomType atomType = null;
        Atom atom = new Atom("X");
        atom.setAtomTypeName("C.ar");
		try {
            AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mol2_atomtypes.xml");
			atomType = factory.configure(atom);
		} catch(Exception exc) {
			fail("Problem getting AtomType for 'mol2:C.ar' from AtomTypeFactory: "  +  exc.getMessage());
		}
		
        assertEquals("C", atom.getSymbol());
	}
}
