/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
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
 * 
 */
package org.openscience.cdk.test.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.test.CDKTestCase;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Checks the funcitonality of the IsotopeFactory
 *
 * @cdk.module test-core
 */
public class IsotopeFactoryTest extends CDKTestCase
{
	boolean standAlone = false;

    final static AtomTypeFactory atf = AtomTypeFactory.getInstance(new ChemObject().getBuilder());

	private static final String JAXP_SCHEMA_LANGUAGE =
	    "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	private static final String W3C_XML_SCHEMA =
	    "http://www.w3.org/2001/XMLSchema"; 
	
    static File tmpCMLSchema;
    
    static {
    	try {
			InputStream in = AtomTypeFactoryTest.class.getClassLoader().getResourceAsStream(
		       	"org/openscience/cdk/io/cml/data/cml25b1.xsd"
		    );
			tmpCMLSchema = copyFileToTmp("cml2.5.b1", ".xsd", in, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	public IsotopeFactoryTest(String name) {
		super(name);
	}
	
	public void setUp() {}
	
	public static Test suite() {
		return new TestSuite(IsotopeFactoryTest.class);
	}

    public void testGetInstance_IChemObjectBuilder() throws Exception {
        IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        assertNotNull(isofac);
    }
    
	public void testGetSize() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
		assertTrue(isofac.getSize() > 0);
    }
	
	public void testConfigure_IAtom() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
		Atom atom = new Atom("H");
        isofac.configure(atom);
        assertEquals(1, atom.getAtomicNumber());
    }
	
	public void testConfigure_IAtom_IIsotope() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
		Atom atom = new Atom("H");
        IIsotope isotope = new org.openscience.cdk.Isotope("H", 2);
        isofac.configure(atom, isotope);
        assertEquals(2, atom.getMassNumber());
    }
	
	public void testGetMajorIsotope_String() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        IIsotope isotope = isofac.getMajorIsotope("Te");
        if (standAlone) System.out.println("Isotope: " + isotope);
		assertEquals(129.9062244, isotope.getExactMass(), 0.0001);
	}
    
	public void testGetMajorIsotope_int() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        IIsotope isotope = isofac.getMajorIsotope(17);
		assertEquals("Cl", isotope.getSymbol());
	}
    
    public void testGetElement_String() throws Exception {
		IsotopeFactory elfac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        IElement element = elfac.getElement("Br");
		assertEquals(35, element.getAtomicNumber());
	}    

    public void testGetElement_int() throws Exception {
		IsotopeFactory elfac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        IElement element = elfac.getElement(6);
		assertEquals("C", element.getSymbol());
	}    

    public void testGetElementSymbol_int() throws Exception {
		IsotopeFactory elfac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        String symbol = elfac.getElementSymbol(8);
		assertEquals("O", symbol);
	}    

    public void testGetIsotopes_String() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        IIsotope[] list = isofac.getIsotopes("He");
		assertEquals(8, list.length);
	}    

    public void testIsElement_String() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
		assertTrue(isofac.isElement("C"));
	}
    
    public void testConfigureAtoms_IAtomContainer() throws Exception {
        AtomContainer container = new org.openscience.cdk.AtomContainer();
        container.addAtom(new Atom("C"));
        container.addAtom(new Atom("H"));
        container.addAtom(new Atom("N"));
        container.addAtom(new Atom("O"));
        container.addAtom(new Atom("F"));
        container.addAtom(new Atom("Cl"));
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        isofac.configureAtoms(container);
        for (int i=0; i<container.getAtomCount(); i++) {
            assertTrue(0 < container.getAtom(i).getAtomicNumber());
        }
    }

    public void testXMLValidityHybrid() throws Exception {
    	assertValidCML("org/openscience/cdk/config/data/isotopes.xml", "Isotopes");
    }

    private void assertValidCML(String atomTypeList, String shortcut) throws Exception {    	
    	InputStream cmlSchema = new FileInputStream(tmpCMLSchema);
    	DocumentBuilderFactory factory =
    		DocumentBuilderFactory.newInstance();
    	factory.setNamespaceAware(true);
    	factory.setValidating(true);
    	assertNotNull("Could not find the CML schema", cmlSchema);
    	factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
    	factory.setAttribute(JAXP_SCHEMA_LANGUAGE, cmlSchema);
    	factory.setFeature("http://apache.org/xml/features/validation/schema", true);
    	
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
    		atomTypeList
    	);
    	File tmpInput = copyFileToTmp(shortcut, ".cmlinput", ins,
    		"../../io/cml/data/cml25b1.xsd", "file://" + tmpCMLSchema.getAbsolutePath()
    	);
    	assertNotNull("Could not find the atom type list CML source", ins);

    	DocumentBuilder parser = factory.newDocumentBuilder();
    	parser.setErrorHandler(new SAXValidityErrorHandler(shortcut));
    	parser.parse(new FileInputStream(tmpInput));
    }

    public void testCanReadCMLSchema() throws Exception {
    	InputStream cmlSchema = new FileInputStream(tmpCMLSchema);
    	assertNotNull("Could not find the CML schema", cmlSchema);

    	DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    	// make sure the schema is read 
    	Document schemaDoc = parser.parse(cmlSchema);
    	assertNotNull(schemaDoc.getFirstChild());
    	assertEquals("xsd:schema", schemaDoc.getFirstChild().getNodeName());
    }
    
    /**
     * Copies a file to TMP (whatever that is on your platform), and optionally
     * replaces a String on the fly. The temporary file will be named prefix+suffix
     * 
     * @param prefix      Prefix of the temporary file name
     * @param suffix      Suffix of the temporary file name
     * @param in          InputStream to copy from
     * @param toReplace   String to replace. Null, if nothing needs to be replaced.
     * @param replaceWith String that replaces the toReplace. Null, if nothing needs to be replaced.
     * 
     * @return            The temporary file/
     * @throws IOException
     */
    private static File copyFileToTmp(String prefix, String suffix, InputStream in,
    		String toReplace, String replaceWith) throws IOException {
    	File tmpFile = File.createTempFile(prefix, suffix);
    	FileOutputStream out= new FileOutputStream(tmpFile);
    	byte[] buf = new byte[4096];
    	int i = 0;
    	while((i=in.read(buf)) != -1) {
    		if (toReplace != null && replaceWith != null && i >= toReplace.length() &&
    			new String(buf).contains(toReplace)) {
    			// a replacement has been defined
    			String newString = new String(buf).replaceAll(toReplace, replaceWith);
    			out.write(newString.getBytes());
    		} else {
    			// no replacement needs to be done
    			out.write(buf, 0, i);
    		}
    	}
    	in.close();
    	out.close();
    	tmpFile.deleteOnExit();
    	return tmpFile;
    }

    class SAXValidityErrorHandler implements ErrorHandler {

    	private String atomTypeList;
    	
    	public SAXValidityErrorHandler(String atomTypeList) {
			this.atomTypeList = atomTypeList;
		}
    	
		public void error(SAXParseException arg0) throws SAXException {
			fail(atomTypeList + " is not valid on line " + arg0.getLineNumber() + ": " + arg0.getMessage());
		}

		public void fatalError(SAXParseException arg0) throws SAXException {
			fail(atomTypeList + " is not valid on line " + arg0.getLineNumber() + ": " + arg0.getMessage());
		}

		public void warning(SAXParseException arg0) throws SAXException {
			// warnings are fine			
		}
    	
    }

}
