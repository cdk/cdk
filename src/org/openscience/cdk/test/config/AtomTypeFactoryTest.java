/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.config;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.test.CDKTestCase;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Checks the functionality of the AtomTypeFactory.
 *
 * @cdk.module test-core
 */
public class AtomTypeFactoryTest extends CDKTestCase {

	private static final String JAXP_SCHEMA_LANGUAGE =
	    "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	private static final String W3C_XML_SCHEMA =
	    "http://www.w3.org/2001/XMLSchema"; 
	
    AtomTypeFactory atf = null;
    
	public AtomTypeFactoryTest(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
    }
	
	public static Test suite() {
		return new TestSuite(AtomTypeFactoryTest.class);
	}

	public void testAtomTypeFactory() {
        assertNotNull(atf);
        assertNotSame(atf.getSize(), 0);
    }
    
    public void testGetInstance_InputStream_String_IChemObjectBuilder() throws Exception {
    	String configFile = "org/openscience/cdk/config/data/structgen_atomtypes.xml";
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
    	AtomTypeFactory atf = AtomTypeFactory.getInstance(ins, "xml", new ChemObject().getBuilder());
    	assertNotNull(atf);
    	assertNotSame(0, atf.getSize());
    }
    
    public void testGetInstance_String_IChemObjectBuilder() throws Exception {
    	String configFile = "org/openscience/cdk/config/data/structgen_atomtypes.xml";
    	AtomTypeFactory atf = AtomTypeFactory.getInstance(configFile, new ChemObject().getBuilder());
    	assertNotNull(atf);
    	assertNotSame(0, atf.getSize());
    }
    
    public void testGetInstance_IChemObjectBuilder() throws Exception {
    	AtomTypeFactory atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
    	assertNotNull(atf);
    }
    
    public void testGetSize() throws Exception {
    	AtomTypeFactory atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
    	assertNotSame(0, atf.getSize());
    }
    
    public void testGetAllAtomTypes() throws Exception {
    	AtomTypeFactory atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
    	IAtomType[] types = atf.getAllAtomTypes();
    	assertNotNull(types);
    	assertNotSame(0, types.length);
    }
    
    public void testGetAtomType_String() throws Exception {
		IAtomType atomType = atf.getAtomType("C4");
        assertNotNull(atomType);
        assertEquals("C", atomType.getSymbol());
        assertEquals("C4", atomType.getAtomTypeName());
		assertEquals(4.0, atomType.getBondOrderSum(), 0.001);
		assertEquals(3.0, atomType.getMaxBondOrder(), 0.0001);
	}

    public void testGetAtomTypes_String() throws Exception {
		IAtomType[] atomTypes = atf.getAtomTypes("C");
		
        assertNotNull(atomTypes);
        assertTrue(0 < atomTypes.length);
        assertEquals("C", atomTypes[0].getSymbol());
	}

    public void testGetAtomTypeFromValency() throws Exception {
        AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/valency_atomtypes.xml", new ChemObject().getBuilder());
		IAtomType atomType = factory.getAtomType("Oplus");
		
        assertNotNull(atomType);
        assertEquals("O", atomType.getSymbol());
        assertEquals("Oplus", atomType.getAtomTypeName());
		assertEquals(1, atomType.getFormalCharge().intValue());
		assertEquals(3.0, atomType.getBondOrderSum(), 0.0001);
		assertEquals(3.0, atomType.getMaxBondOrder(), 0.0001);
	}

    public void testGetAtomTypeFromHybrid() throws Exception {
        AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/hybridization_atomtypes.xml", new ChemObject().getBuilder());
		IAtomType atomType = factory.getAtomType("C.sp2");
		
        assertNotNull(atomType);
        assertEquals("C", atomType.getSymbol());
        assertEquals("C.sp2", atomType.getAtomTypeName());
		assertEquals(0, atomType.getFormalCharge().intValue());
		assertEquals(4.0, atomType.getBondOrderSum(), 0.0001);
		assertEquals(2.0, atomType.getMaxBondOrder(), 0.0001);
		assertEquals(3, (int) atomType.getFormalNeighbourCount());
		assertEquals(CDKConstants.HYBRIDIZATION_SP2, (int) atomType.getHybridization());
	}

    public void testGetAtomTypeFromPDB() throws Exception {
        AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/pdb_atomtypes.xml", 
                new ChemObject().getBuilder());
		IAtomType atomType = factory.getAtomType("ALA.CA");
		
        assertNotNull(atomType);
        assertEquals("C", atomType.getSymbol());
        assertEquals("ALA.CA", atomType.getAtomTypeName());
	}

    public void testConfigure_IAtom() throws Exception {
		IAtomType atomType;
        IAtom atom = new org.openscience.cdk.Atom("X");
        atom.setAtomTypeName("C.ar");
        AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mol2_atomtypes.xml", new ChemObject().getBuilder());
        atomType = factory.configure(atom);
        assertNotNull(atomType);
		
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
     * @throws Exception if the atom typ info cannot be loaded  
     */
    public void testGetAtomTypeFromMM2() throws Exception {
    	AtomTypeFactory factory;
    	factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/mm2_atomtypes.xml", 
    			new ChemObject().getBuilder());

    	IAtomType atomType = factory.getAtomType("C");
    	assertNotNull(atomType);
    	assertEquals("C", atomType.getSymbol());
    	assertEquals("C", atomType.getAtomTypeName());
    	assertEquals("[CSP]-[0-4][-]?+;[A-Za-z\\+\\-&&[^=%]]{0,6}[(].*+", (String)atomType.getProperty(CDKConstants.SPHERICAL_MATCHER));
    	assertEquals(CDKConstants.HYBRIDIZATION_SP3, (int) atomType.getHybridization());

    	atomType = factory.getAtomType("Sthi");
    	assertNotNull(atomType);
    	assertEquals("S", atomType.getSymbol());
    	assertEquals("Sthi", atomType.getAtomTypeName());
    	assertEquals("S-[2];[H]{0,3}+=C.*+", (String)atomType.getProperty(CDKConstants.SPHERICAL_MATCHER));
    	assertEquals(CDKConstants.HYBRIDIZATION_SP2, (int) atomType.getHybridization());
    	assertTrue(atomType.getFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR));
    	assertEquals(5, atomType.getProperty(CDKConstants.PART_OF_RING_OF_SIZE));
    }
    
    public void testXMLValidityHybrid() throws Exception {
    	assertValidCML("org/openscience/cdk/config/data/hybridization_atomtypes.xml");
    }
        
    public void testXMLValidityMM2() throws Exception {
    	assertValidCML("org/openscience/cdk/config/data/mm2_atomtypes.xml");
    }
        
    public void testXMLValidityMMFF94() throws Exception {
    	assertValidCML("org/openscience/cdk/config/data/mmff95_atomtypes.xml");
    }
        
    public void testXMLValidityMol2() throws Exception {
    	assertValidCML("org/openscience/cdk/config/data/mol2_atomtypes.xml");
    }
        
    public void testXMLValidityPDB() throws Exception {
    	assertValidCML("org/openscience/cdk/config/data/pdb_atomtypes.xml");
    }
        
    public void testXMLValidityStructGen() throws Exception {
    	assertValidCML("org/openscience/cdk/config/data/structgen_atomtypes.xml");
    }
        
    public void testXMLValidityValency() throws Exception {
    	assertValidCML("org/openscience/cdk/config/data/valency_atomtypes.xml");
    }
        
    public void testXMLValidityValency2() throws Exception {
    	assertValidCML("org/openscience/cdk/config/data/valency2_atomtypes.xml");
    }
        
    private void assertValidCML(String atomTypeList) throws Exception {    	
    	DocumentBuilderFactory factory =
    		DocumentBuilderFactory.newInstance();
    	factory.setNamespaceAware(true);
    	factory.setValidating(true);
    	InputStream cmlSchema = this.getClass().getClassLoader().getResourceAsStream(
       		"org/openscience/cdk/io/cml/data/cml25b1.xsd"
   		);
    	assertNotNull("Could not find the CML schema", cmlSchema);
    	factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
    	factory.setAttribute(JAXP_SCHEMA_LANGUAGE, cmlSchema);
    	factory.setFeature("http://apache.org/xml/features/validation/schema", true);
    	
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
    		atomTypeList
    	);
    	assertNotNull("Could not find the atom type list CML source", ins);
    	DocumentBuilder parser = factory.newDocumentBuilder();
    	parser.setErrorHandler(new SAXValidityErrorHandler("MM2"));
    	parser.parse(ins);    	
    }
    
    class SAXValidityErrorHandler implements ErrorHandler {

    	private String atomTypeList;
    	
    	public SAXValidityErrorHandler(String atomTypeList) {
			this.atomTypeList = atomTypeList;
		}
    	
		public void error(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			fail(atomTypeList + " is not valid: " + arg0.getMessage());
		}

		public void fatalError(SAXParseException arg0) throws SAXException {
			arg0.printStackTrace();
			fail(atomTypeList + " is not valid: " + arg0.getMessage());
		}

		public void warning(SAXParseException arg0) throws SAXException {
			// warnings are fine			
		}
    	
    }
    
}
