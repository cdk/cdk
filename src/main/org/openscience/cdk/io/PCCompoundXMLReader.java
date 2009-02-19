/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.PubChemSubstanceXMLFormat;
import org.openscience.cdk.io.pubchemxml.PubChemXMLHelper;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Reads an object from ASN.1 XML formated input for PubChem Compound entries.
 * The following bits are supported: atoms.aid, atoms.element, atoms.2d,
 * atoms.3d, bonds.aid1, bonds.aid2.
 *
 * @cdk.module  io
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword file format, PubChem Compound XML
 */
@TestClass("org.openscience.cdk.io.PCCompounsXMLReaderTest")
public class PCCompoundXMLReader extends DefaultChemObjectReader {

	private Reader input;
    private XmlPullParser parser;
    private PubChemXMLHelper parserHelper;
    private IChemObjectBuilder builder;

    IMolecule molecule = null;

    /**
     * Construct a new reader from a Reader type object.
     *
     * @param input reader from which input is read
     */
    public PCCompoundXMLReader(Reader input) throws Exception {
        setReader(input);
    }

    public PCCompoundXMLReader(InputStream input) throws Exception {
        setReader(input);
    }
    
    public PCCompoundXMLReader() throws Exception {
        this(new StringReader(""));
    }
    
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return PubChemSubstanceXMLFormat.getInstance();
    }
    
    @TestMethod("testSetReader_Reader")
    public void setReader(Reader input) throws CDKException {
    	try {
    		XmlPullParserFactory factory = XmlPullParserFactory.newInstance(
    				System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null
    		);
    		factory.setNamespaceAware(true);
    		parser = factory.newPullParser();
    		this.input = input;
    		parser.setInput(input);
    	} catch (Exception exception) {
    		throw new CDKException("Error while creating reader: " + exception.getMessage(), exception);
    	}
    }

    @TestMethod("testSetReader_InputStream")
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

	@TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IMolecule.class.equals(interfaces[i])) return true;
		}
		return false;
	}

    public IChemObject read(IChemObject object) throws CDKException {        
        if (object instanceof IMolecule) {
        	try {
            	parserHelper = new PubChemXMLHelper(object.getBuilder());
            	builder = object.getBuilder();
        		return readMolecule();
        	} catch (IOException e) {
        		throw new CDKException("An IO Exception occured while reading the file.", e);
        	} catch (CDKException e) {
        		throw e;
        	} catch (Exception e) {
        		throw new CDKException("An error occured: " + e.getMessage(), e);
        	}
        } else {
            throw new CDKException("Only supported is reading of IMolecule objects.");
        }
    }

    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
    }

    // private procedures

    private IMolecule readMolecule() throws Exception {
    	boolean foundCompound = false;
    	while (parser.next() != XmlPullParser.END_DOCUMENT) {
    		if (parser.getEventType() == XmlPullParser.START_TAG) {
    			if (parser.getName().equals("PC-Compound")) {
    				foundCompound = true;
    				break;
    			}
    		}
    	}
    	if (foundCompound) {
    		return parserHelper.parseMolecule(parser, builder);            		
    	}
    	return null;
    }

}
