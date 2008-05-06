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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.kxml2.io.KXmlParser;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.SMILESFormat;
import org.xmlpull.v1.XmlPullParser;

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

	// general elements
	private final static String EL_PCCOMPOUND = "PC-Compound";
	
	// atom block elements
	private final static String EL_ATOMBLOCK = "PC-Atoms";
	private final static String EL_ATOMSELEMENT = "PC-Atoms_element";
	private final static String EL_ELEMENT = "PC-Element";
	
	// bond block elements
	private final static String EL_BONDBLOCK = "PC-Bonds";
	private final static String EL_BONDID1 = "PC-Bonds_aid1";
	private final static String EL_BONDID2 = "PC-Bonds_aid2";
	private final static String EL_BONDORDER = "PC-Bonds_order";
	
	private Reader primarySource;
    private KXmlParser parser;
    private IChemObjectBuilder builder;
    private IsotopeFactory factory;
    
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
        this.builder = builder;
        factory = IsotopeFactory.getInstance(builder);
        
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
//                		System.out.println("start: '" + parser.getName() + "'");
            			if (parser.getName().equals("PC-Compound")) {
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
    			if (EL_PCCOMPOUND.equals(parser.getName())) {
    				break; // done parsing the molecule
    			}
    		} else if (parser.getEventType() == XmlPullParser.START_TAG) {
    			if (EL_ATOMBLOCK.equals(parser.getName())) {
    				parserAtomBlock(parser, molecule);
    			} else if (EL_BONDBLOCK.equals(parser.getName())) {
    				parserBondBlock(parser, molecule);
    			}
    		}
    	}
		return molecule;
    }


	private void parserBondBlock(XmlPullParser parser2, IMolecule molecule) throws Exception {
		List<String> id1s = new ArrayList<String>();
		List<String> id2s = new ArrayList<String>();
		List<String> orders = new ArrayList<String>();
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
    			if (EL_BONDBLOCK.equals(parser.getName())) {
    				break; // done parsing the atom block
    			}
    		} else if (parser.getEventType() == XmlPullParser.START_TAG) {
    			if (EL_BONDID1.equals(parser.getName())) {
    				id1s = parseValues(parser, EL_BONDID1, "PC-Bonds_aid1_E");
    			} else if (EL_BONDID2.equals(parser.getName())) {
    				id2s = parseValues(parser, EL_BONDID2, "PC-Bonds_aid2_E");
    			} else if (EL_BONDORDER.equals(parser.getName())) {
    				orders = parseValues(parser, EL_BONDORDER, "PC-BondType");
    			}
    		}
		}
		// aggregate information
		if (id1s.size() != id2s.size()) {
			throw new CDKException("Inequal number of atom identifier in bond block.");
		}
		if (id1s.size() != orders.size()) {
			throw new CDKException("Number of bond orders does not match number of bonds in bond block.");
		}
		for (int i=0; i<id1s.size(); i++) {
			IAtom atom1 = molecule.getAtom(Integer.parseInt(id1s.get(i))-1);
			IAtom atom2 = molecule.getAtom(Integer.parseInt(id2s.get(i))-1);
			IBond bond = molecule.getBuilder().newBond(atom1, atom2);
			int order = Integer.parseInt(orders.get(i));
			if (order == 1) {
				bond.setOrder(IBond.Order.SINGLE);
				molecule.addBond(bond);
			} else if (order == 2) {
				bond.setOrder(IBond.Order.DOUBLE);
				molecule.addBond(bond);
			} if (order == 3) {
				bond.setOrder(IBond.Order.TRIPLE);
				molecule.addBond(bond);
			} else {
				// unknown bond order, skip
			}
		}
	}

	private List<String> parseValues(XmlPullParser parser, String endTag, String fieldTag) throws Exception {
		List<String> values = new ArrayList<String>();
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
    			if (endTag.equals(parser.getName())) {
    				// done parsing the values
    				break;
    			}
    		} else if (parser.getEventType() == XmlPullParser.START_TAG) {
    			if (fieldTag.equals(parser.getName())) {
    				String value = parser.nextText();
    				values.add(value);
    			}
    		}
		}
		return values;
	}


	private void parserAtomBlock(XmlPullParser parser2, IMolecule molecule) throws Exception {
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
    			if (EL_ATOMBLOCK.equals(parser.getName())) {
    				break; // done parsing the atom block
    			}
    		} else if (parser.getEventType() == XmlPullParser.START_TAG) {
    			if (EL_ATOMSELEMENT.equals(parser.getName())) {
    				parseAtomElements(parser, molecule);
    			}
    		}
		}
	}

	private void parseAtomElements(XmlPullParser parser2, IMolecule molecule) throws Exception {
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.END_TAG) {
    			if (EL_ATOMSELEMENT.equals(parser.getName())) {
    				break; // done parsing the atom elements
    			}
    		} else if (parser.getEventType() == XmlPullParser.START_TAG) {
    			if (EL_ELEMENT.equals(parser.getName())) {
    				int atomicNumber = Integer.parseInt(parser.nextText());
    				IElement element = factory.getElement(atomicNumber);
    				IAtom atom = molecule.getBuilder().newAtom(element.getSymbol());
    				atom.setAtomicNumber(element.getAtomicNumber());
    				molecule.addAtom(atom);
    			}
    		}
		}
	}

}

