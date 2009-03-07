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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.NoSuchElementException;

import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.PubChemSubstancesXMLFormat;
import org.openscience.cdk.io.pubchemxml.PubChemXMLHelper;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Iterating PubChem PC-Substances ASN.1 XML reader.
 *
 * @cdk.module   io
 * @cdk.svnrev   $Revision$
 * 
 * @author       Egon Willighagen <egonw@users.sf.net>
 * @cdk.created  2008-05-05
 *
 * @cdk.keyword  file format, ASN
 * @cdk.keyword  PubChem
 */
public class IteratingPCSubstancesXMLReader extends DefaultIteratingChemObjectReader {

	private Reader primarySource;
    private XmlPullParser parser;
    private PubChemXMLHelper parserHelper;
    
    private boolean nextAvailableIsKnown;
    private boolean hasNext;
    private IChemModel nextSubstance;
    
    /**
     * Constructs a new IteratingPCSubstancesXMLReader that can read Molecule from a given Reader and IChemObjectBuilder.
     *
     * @param in      The input stream
     * @param builder The builder
     * @throws java.io.IOException if there is error in getting the {@link IsotopeFactory}
     * @throws org.xmlpull.v1.XmlPullParserException if there is an error isn setting up the XML parser
     */
    public IteratingPCSubstancesXMLReader(Reader in, IChemObjectBuilder builder) throws IOException, XmlPullParserException {
        parserHelper = new PubChemXMLHelper(builder);
        
        // initiate the pull parser
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance(
                System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null
        );
        factory.setNamespaceAware(true);
        parser = factory.newPullParser();
        primarySource = in;
        parser.setInput(primarySource);

        nextSubstance = null;
        nextAvailableIsKnown = false;
        hasNext = false;
    }

    /**
     * Constructs a new IteratingPCSubstancesXMLReader that can read Molecule from a given InputStream and IChemObjectBuilder.
     *
     * @param in The input stream
     * @param builder The builder. In general, use {@link org.openscience.cdk.DefaultChemObjectBuilder}
     * @throws Exception if there is a problem creating an InputStreamReader
     */
    public IteratingPCSubstancesXMLReader(InputStream in, IChemObjectBuilder builder) throws Exception {
        this(new InputStreamReader(in), builder);
    }


    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return PubChemSubstancesXMLFormat.getInstance();
    }

    public boolean hasNext() {
        if (!nextAvailableIsKnown) {
            hasNext = false;
            
            try {
                if (parser.next() == XmlPullParser.END_DOCUMENT) return false;
                
            	while (parser.next() != XmlPullParser.END_DOCUMENT) {
            		if (parser.getEventType() == XmlPullParser.START_TAG) {
            			if (PubChemXMLHelper.EL_PCSUBSTANCE.equals(parser.getName())) {
            				hasNext = true;
            				break;
            			}
            		}
            	}
            	if (hasNext) {
            		nextSubstance = parserHelper.parseSubstance(parser); 
            	}
            	
			} catch (Exception e) {
				if (mode == Mode.STRICT) {
					throw new RuntimeException("Error while parsing the XML: " + e.getMessage(), e);
				}
				hasNext = false;
			}
            
            if (!hasNext) nextSubstance = null;
            nextAvailableIsKnown = true;
        }
        return hasNext;
    }
    
	public IChemObject next() {
        if (!nextAvailableIsKnown) {
            hasNext();
        }
        nextAvailableIsKnown = false;
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        return nextSubstance;
    }
    
	@TestMethod("testClose")
  public void close() throws IOException {
    	primarySource.close();
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }

	@TestMethod("testSetReader_Reader")
    public void setReader(Reader reader) throws CDKException {
		primarySource = reader;
        try {
	        parser.setInput(primarySource);
        } catch (XmlPullParserException e) {
	        throw new CDKException("Error while opening the input:" + e.getMessage(), e);
        }
        nextSubstance = null;
        nextAvailableIsKnown = false;
        hasNext = false;
    }

	@TestMethod("testSetReader_InputStream")
    public void setReader(InputStream reader) throws CDKException {
	    setReader(new InputStreamReader(reader));
    }
}

