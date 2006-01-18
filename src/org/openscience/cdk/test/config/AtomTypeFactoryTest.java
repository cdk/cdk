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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.test.CDKTestCase;


/**
 * Checks the funcitonality of the AtomTypeFactory
 *
 * @cdk.module test
 */
public class AtomTypeFactoryTest extends CDKTestCase {

    AtomTypeFactory atf = null;
    
	public AtomTypeFactoryTest(String name) {
		super(name);
	}
	
	public void setUp() {
		try {
			atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
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
        assertNotSame(new Integer(0), new Integer(atf.getSize()));
    }
    
    public void testGetInstance_InputStream_String_IChemObjectBuilder() {
        try {
            String configFile = "org/openscience/cdk/config/data/structgen_atomtypes.xml";
            InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
            AtomTypeFactory atf = AtomTypeFactory.getInstance(ins, "xml", new ChemObject().getBuilder());
            assertNotNull(atf);
            assertNotSame(new Integer(0),new Integer( atf.getSize()));
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testGetInstance_String_IChemObjectBuilder() {
        try {
            String configFile = "org/openscience/cdk/config/data/structgen_atomtypes.xml";
            AtomTypeFactory atf = AtomTypeFactory.getInstance(configFile, new ChemObject().getBuilder());
            assertNotNull(atf);
            assertNotSame(new Integer(0),new Integer( atf.getSize()));
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testGetInstance_IChemObjectBuilder() {
        try {
            AtomTypeFactory atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
            assertNotNull(atf);
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testGetSize() {
        try {
            AtomTypeFactory atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
            assertNotSame(new Integer(0), new Integer(atf.getSize()));
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testGetAllAtomTypes() {
        try {
            AtomTypeFactory atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
            IAtomType[] types = atf.getAllAtomTypes();
            assertNotNull(types);
            assertNotSame(new Integer(0), new Integer(types.length));
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testGetAtomType_String() {
		IAtomType atomType = null;
		try {
			atomType = atf.getAtomType("C4");
		} catch(Exception exc) {
			fail("Problem getting AtomType for 'structgen.C4' from AtomTypeFactory: "  +  exc.getMessage());
		}
		
        assertNotNull(atomType);
        assertEquals("C", atomType.getSymbol());
        assertEquals("C4", atomType.getAtomTypeName());
		assertEquals(4.0, atomType.getBondOrderSum(), 0.001);
		assertEquals(3.0, atomType.getMaxBondOrder(), 0.0001);
	}

    public void testGetAtomTypes_String() {
		IAtomType[] atomTypes = null;
		try {
			atomTypes = atf.getAtomTypes("C");
		} catch(Exception exc) {
			fail("Problem getting AtomType for 'C' from AtomTypeFactory: "  +  exc.getMessage());
		}
		
        assertNotNull(atomTypes);
        assertTrue(0 < atomTypes.length);
        assertEquals("C", atomTypes[0].getSymbol());
	}

    public void testGetAtomTypeFromValency() {
		IAtomType atomType = null;
		try {
            AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/valency_atomtypes.xml", new ChemObject().getBuilder());
			atomType = factory.getAtomType("Oplus");
		} catch(Exception exc) {
			fail("Problem getting AtomType for 'valency:O+' from AtomTypeFactory: "  +  exc.getMessage());
		}
		
        assertNotNull(atomType);
        assertEquals("O", atomType.getSymbol());
        assertEquals("Oplus", atomType.getAtomTypeName());
		assertEquals(1, atomType.getFormalCharge());
		assertEquals(3.0, atomType.getBondOrderSum(), 0.0001);
		assertEquals(2.0, atomType.getMaxBondOrder(), 0.0001);
	}

    public void testGetAtomTypeFromHybrid() {
		IAtomType atomType = null;
		try {
            AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/hybridization_atomtypes.xml", new ChemObject().getBuilder());
			atomType = factory.getAtomType("C.sp2");
		} catch(Exception exc) {
			fail("Problem getting AtomType for 'hybridization:C.sp2' from AtomTypeFactory: "  +  exc.getMessage());
		}
		
        assertNotNull(atomType);
        assertEquals("C", atomType.getSymbol());
        assertEquals("C.sp2", atomType.getAtomTypeName());
		assertEquals(0, atomType.getFormalCharge());
		assertEquals(4.0, atomType.getBondOrderSum(), 0.0001);
		assertEquals(2.0, atomType.getMaxBondOrder(), 0.0001);
		assertEquals(3, atomType.getFormalNeighbourCount());
		assertEquals(CDKConstants.HYBRIDIZATION_SP2, atomType.getHybridization());
	}

    public void testGetAtomTypeFromPDB() {
		IAtomType atomType = null;
		try {
            AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/pdb_atomtypes.xml", 
                new ChemObject().getBuilder());
			atomType = factory.getAtomType("ALA.CA");
		} catch(Exception exc) {
			fail("Problem getting AtomType for 'hybridization:ALA.CA' from AtomTypeFactory: "  +  exc.getMessage());
		}
		
        assertNotNull(atomType);
        assertEquals("C", atomType.getSymbol());
        assertEquals("ALA.CA", atomType.getAtomTypeName());
	}

    public void testConfigure_IAtom() {
		IAtomType atomType = null;
        IAtom atom = new org.openscience.cdk.Atom("X");
        atom.setAtomTypeName("C.ar");
		try {
            AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mol2_atomtypes.xml", new ChemObject().getBuilder());
			atomType = factory.configure(atom);
			assertNotNull(atomType);
		} catch(Exception exc) {
			fail("Problem getting AtomType for 'mol2:C.ar' from AtomTypeFactory: "  +  exc.getMessage());
		}
		
        assertEquals("C", atom.getSymbol());
	}
    
    /**
     * Test reading from a XML config file with content like:
     * <pre>
     *   <atomType id="C">
     *    <!-- for example in CC-->
     *    <atom elementType="C" formalCharge="0">
     *      <scalar dataType="xsd:double" dictRef="cdk:maxBondOrder">1.0</scalar>
     *      <scalar dataType="xsd:double" dictRef="cdk:bondOrderSum">4.0</scalar>
     *      <scalar dataType="xsd:integer" dictRef="cdk:formalNeighbourCount">4</scalar>
     *      <scalar dataType="xsd:integer" dictRef="cdk:valency">4</scalar>
     *    </atom>
     *    <scalar dataType="xsd:string" dictRef="cdk:hybridization">sp3</scalar>
     *    <scalar dataType="xsd:string" dictRef="cdk:DA">-</scalar>
     *    <scalar dataType="xsd:string" dictRef="cdk:sphericalMatcher">[CSP]-[0-4][-]?+;[A-Za-z\+\-&amp;&amp;[^=%]]{0,6}[(].*+</scalar>
     *  </atomType>
     * </pre>
     *
     */
    public void testGetAtomTypeFromMM2() {
    	AtomTypeFactory factory = null;
		try {
            factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mm2_atomtypes.xml", 
                new ChemObject().getBuilder());

    		IAtomType atomType = factory.getAtomType("C");
            assertNotNull(atomType);
            assertEquals("C", atomType.getSymbol());
            assertEquals("C", atomType.getAtomTypeName());
            assertEquals("[CSP]-[0-4][-]?+;[A-Za-z\\+\\-&&[^=%]]{0,6}[(].*+", (String)atomType.getProperty(CDKConstants.SPHERICAL_MATCHER));
            assertEquals(CDKConstants.HYBRIDIZATION_SP3, atomType.getHybridization());
            
            atomType = factory.getAtomType("Sthi");
            assertNotNull(atomType);
            assertEquals("S", atomType.getSymbol());
            assertEquals("Sthi", atomType.getAtomTypeName());
            assertEquals("S-[2];[H]{0,3}+=C.*+", (String)atomType.getProperty(CDKConstants.SPHERICAL_MATCHER));
            assertEquals(CDKConstants.HYBRIDIZATION_SP2, atomType.getHybridization());
            assertTrue(atomType.getFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR));
            assertEquals(new Integer(5), atomType.getProperty(CDKConstants.PART_OF_RING_OF_SIZE));
            		
		} catch(Exception exc) {
			fail("Problem getting AtomType from AtomTypeFactory: "  +  exc.getMessage());
		}    
    }
    
}
