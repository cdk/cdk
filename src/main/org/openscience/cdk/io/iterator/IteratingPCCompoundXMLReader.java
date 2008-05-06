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
package org.openscience.cdk.io.iterator;

import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

import org.kxml2.io.KXmlParser;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.SMILESFormat;
import org.openscience.cdk.tools.LoggingTool;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Iterating PubChem PCCompound ASN.1 XML reader.
 *
 * @cdk.module io
 * @cdk.svnrev $Revision$
 *
 * @see org.openscience.cdk.io.PCCompoundASNReader
 * 
 * @author       Egon Willighagen <egonw@users.sf.net>
 * @cdk.created  2008-05-05
 *
 * @cdk.keyword  file format, ASN
 * @cdk.keyword  PubChem
 */
public class IteratingPCCompoundXMLReader extends DefaultIteratingChemObjectReader {

	private final static String NS_PUBCHEM = "http://www.ncbi.nlm.nih.gov";
	
	private final static String EL_PCCOMPOUND = "PC-Compound";		
	private final static String EL_ATOMBLOCK = "PC-Atoms";
	private final static String EL_BONDBLOCK = "PC-Bonds";
	
	private Reader primarySource;
    private KXmlParser parser;
    private LoggingTool logger;
    private IChemObjectBuilder builder;
    
    private boolean nextAvailableIsKnown;
    private boolean hasNext;
    private IMolecule nextMolecule;
    
    /**
     * Constructs a new IteratingPCCompoundXMLReader that can read Molecule from a given InputStream and IChemObjectBuilder.
     *
     * @param in      The input stream
     * @param builder The builder
     */
    public IteratingPCCompoundXMLReader(Reader in, IChemObjectBuilder builder) throws Exception {
        logger = new LoggingTool(this);
        this.builder = builder;
        
        // initiate the pull parser
        parser = new KXmlParser();
        primarySource = in;
        parser.setInput(primarySource);

        nextMolecule = null;
        nextAvailableIsKnown = false;
        hasNext = false;
    }


    public IResourceFormat getFormat() {
        return SMILESFormat.getInstance();
    }

    public boolean hasNext() {
        if (!nextAvailableIsKnown) {
            hasNext = false;
            
            try {
                if (parser.next() == XmlPullParser.END_DOCUMENT) return false;
                
            	while (parser.next() != XmlPullParser.END_DOCUMENT) {
            		if (parser.getEventType() == XmlPullParser.START_TAG) {
                		System.out.println("start: '" + parser.getName() + "'");
            			if (parser.getName().equals("PC-Compound")) {
            				
            				System.out.println("xml: ");
            				hasNext = true;
            				break;
            			}
            		}
            	}
            	if (hasNext) {
            		nextMolecule = parseMolecule(parser, builder);            		
            	}
            	
			} catch (Exception e) {
				hasNext = false;
			}
            
            if (!hasNext) nextMolecule = null;
            nextAvailableIsKnown = true;
        }
        return hasNext;
    }
    
	public Object next() {
        if (!nextAvailableIsKnown) {
            hasNext();
        }
        nextAvailableIsKnown = false;
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        return nextMolecule;
    }
    
    public void close() throws IOException {
    	primarySource.close();
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public IMolecule parseMolecule(XmlPullParser parser, IChemObjectBuilder builder) throws Exception {
    	IMolecule molecule = builder.newMolecule();
    	// assume the current element is PC-Compound
    	if (!parser.getName().equals("PC-Compound")) {
    		return null;
    	}

    	while (parser.next() != XmlPullParser.END_DOCUMENT) {
    		if (parser.getEventType() == XmlPullParser.END_TAG) {
    			System.out.println("end: '" + parser.getName() + "'");
    			if (EL_PCCOMPOUND.equals(parser.getName())) {
    				// done parsing the molecule
    				break;
    			} else if (EL_ATOMBLOCK.equals(parser.getName())) {
    				System.out.println("end atom clock");
    			}
    		} else if (parser.getEventType() == XmlPullParser.START_TAG) {
    			System.out.println("start: '" + parser.getName() + "'");
    			if (EL_ATOMBLOCK.equals(parser.getName())) {
    				System.out.println("XX");
    			}
    		}
    	}
		return molecule;
    }

}

