/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 1997-2006  Egon Willighagen <egonw@users.sf.net>
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
 *
 */
package org.openscience.cdk.io.cml;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.openscience.cdk.io.cml.cdopi.IChemicalDocumentObject;
import org.xml.sax.Attributes;

/**
 * Implements the PDB convention used by PDB2CML.
 *
 * <p>This is a lousy implementation, though. Problems that will arise:
 * <ul>
 *   <li>when this new convention is adopted in the root element no
 *     currentFrame was set. This is done when <list sequence=""> is found
 *   <li>multiple sequences are not yet supported
 *   <li>the frame is now added when the doc is ended, which will result in problems
 *     but work for one sequence files made by PDB2CML v.??
 * <ul>
 *
 * <p>What is does:
 * <ul>
 *   <li>work for now
 *   <li>give an idea on the API of the plugable CML import filter
 *     (a real one will be made)
 *   <li>read CML files generated with Steve Zara's PDB 2 CML converter
 *     (of which version 1999 produces invalid CML 1.0)
 * </ul>
 *
 * @cdk.module io
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */
public class PDBConvention extends CMLCoreModule {

    private boolean connectionTable;
    private boolean isELSYM;
    private boolean isBond;
    private String connect_root;
	private boolean hasScalar;
	private String idValue = "";
	private ArrayList altLocV;
	private ArrayList chainIDV;
	private ArrayList hetAtomV;
	private ArrayList iCodeV;
	private ArrayList nameV;
	private ArrayList oxtV;
	private ArrayList recordV;
	private ArrayList resNameV;
	private ArrayList resSeqV;
	private ArrayList segIDV;
	private ArrayList serialV;
	private ArrayList tempFactorV;
	
    public PDBConvention(IChemicalDocumentObject cdo) {
        super(cdo);
    }
    
    public PDBConvention(ICMLModule conv) {
        super(conv);
    }

    public IChemicalDocumentObject returnCDO() {
        return this.cdo;
    }
    
    public void startDocument() {
        super.startDocument();
    }
    
    public void endDocument() {
        storeData();
        super.endDocument();
    }
    
    public void startElement(CMLStack xpath, String uri, String local, String raw, 
                              Attributes atts) {
        String name = raw;
        isELSYM = false;
        if ("molecule".equals(name)) {
        	
            for (int j = 0; j < atts.getLength(); j++) {
            	logger.debug("StartElement");
                currentChars = "";
                
                BUILTIN = "";
                DICTREF = "";

                for (int i=0; i<atts.getLength(); i++) {
                    String qname = atts.getQName(i);
                    if (qname.equals("builtin")) {
                        BUILTIN = atts.getValue(i);
                        logger.debug(name, "->BUILTIN found: ", atts.getValue(i));
                    } else if (qname.equals("dictRef")) {
                        DICTREF = atts.getValue(i);
                        logger.debug(name, "->DICTREF found: ", atts.getValue(i));
                    } else if (qname.equals("title")) {
                        elementTitle = atts.getValue(i);
                        logger.debug(name, "->TITLE found: ", atts.getValue(i));
                    } else {
                        logger.debug("Qname: ", qname);
                    }
                }
                if (atts.getQName(j).equals("convention") && atts.getValue(j).equals("PDB")) {
                    cdo.startObject("PDBPolymer");
                }else if (atts.getQName(j).equals("dictRef") && atts.getValue(j).equals("pdb:sequence")) {
                    
                    newSequence();
                    BUILTIN = "";
                    for (int i = 0; i < atts.getLength(); i++) {
                        if (atts.getQName(i).equals("id")) {
                            cdo.setObjectProperty("Molecule", "id", atts.getValue(i));
                        } else if (atts.getQName(i).equals("dictRef")) {
                        	cdo.setObjectProperty("Molecule", "dictRef", atts.getValue(i));
                        }
                    }
                	
                } else if (atts.getQName(j).equals("title") && 
                         atts.getValue(j).equals("connections")) {
                    // assume that Atom's have been read
                    logger.debug("Assuming that Atom's have been read: storing them");
                    super.storeAtomData();
                    connectionTable = true;
                    logger.debug("Start Connection Table");
                } else if (atts.getQName(j).equals("title") && 
                         atts.getValue(j).equals("connect")) {
                    logger.debug("New connection");
                    isBond = true;
                } else if (atts.getQName(j).equals("id") && isBond) {
                    connect_root = atts.getValue(j);
                }

                // ignore other list items at this moment
            }
        }else if ("scalar".equals(name)) {
        	hasScalar = true;
        	for (int i = 0; i < atts.getLength(); i++) {
                if(atts.getQName(i).equals("dictRef"))
                	idValue = atts.getValue(i);
        	}
        }else if ("label".equals(name)) {
        	hasScalar = true;
        	for (int i = 0; i < atts.getLength(); i++) {
                if(atts.getQName(i).equals("dictRef"))
                	idValue = atts.getValue(i);
        	}
        }else if ("atom".equals(name)) {
        	super.startElement(xpath, uri, local, raw, atts);
        }
    }
    
    public void newSequence(){
    	altLocV = new ArrayList();
    	chainIDV = new ArrayList();
    	hetAtomV = new ArrayList();
    	iCodeV = new ArrayList();
    	nameV = new ArrayList();
    	oxtV = new ArrayList();
    	recordV = new ArrayList();
    	resNameV = new ArrayList();
    	resSeqV = new ArrayList();
    	segIDV = new ArrayList();
    	serialV = new ArrayList();
    	tempFactorV = new ArrayList();
    	
    }
    public void endElement(CMLStack xpath, String uri, String local, String raw) {
        String name = raw;

        // OLD +++++++++++++++++++++++++++++++++++++++++++++
        if (name.equals("list") && connectionTable && !isBond) {
            logger.debug("End Connection Table");
            connectionTable = false;
        // OLD +++++++++++++++++++++++++++++++++++++++++++++

         
        } else if(name.equals("molecule")){
        	storeData();
            if(xpath.sp == 1){
	        	cdo.endObject("Molecule");
        	}
        }
        isELSYM = false;
        isBond = false;
        
    }

    public void characterData(CMLStack xpath, char[] ch, int start, int length) {

        String s = new String(ch, start, length).trim();
        StringTokenizer st1 = new StringTokenizer(s);
        String dictpdb = "";
        while (st1.hasMoreElements()) {
        	
            dictpdb += (String)st1.nextElement();
            if(st1.hasMoreElements())
            	dictpdb += " ";
            else if(idValue.equals("pdb:record"))
            	dictpdb += " ";
        }

        if(idValue.equals("pdb:altLoc"))
            altLocV.add(dictpdb);
        else if(idValue.equals("pdb:chainID"))
        	chainIDV.add(dictpdb);
        else if(idValue.equals("pdb:hetAtom"))
        	hetAtomV.add(dictpdb);
        else if(idValue.equals("pdb:iCode"))
        	iCodeV.add(dictpdb);
        else if(idValue.equals("pdb:name"))
        	nameV.add(dictpdb);
        else if(idValue.equals("pdb:oxt"))
        	oxtV.add(dictpdb);
        else if(idValue.equals("pdb:record"))
        	recordV.add(dictpdb);
        else if(idValue.equals("pdb:resName"))
        	resNameV.add(dictpdb);
        else if(idValue.equals("pdb:resSeq"))
        	resSeqV.add(dictpdb);
        else if(idValue.equals("pdb:segID"))
        	segIDV.add(dictpdb);
        else if(idValue.equals("pdb:serial"))
        	serialV.add(dictpdb);
        else if(idValue.equals("pdb:tempFactor"))
        	tempFactorV.add(dictpdb);
        
        idValue = "";
        
        if (isELSYM) {
            elsym.add(s);
        } else if (isBond) {
            logger.debug("CD (bond): " + s);
            if (connect_root.length() > 0) {
                StringTokenizer st = new StringTokenizer(s);
                while (st.hasMoreElements()) {
                    String atom = (String)st.nextElement();
                    if (!atom.equals("0")) {
                        logger.debug("new bond: " + connect_root + "-" + 
                                     atom);
                        cdo.startObject("Bond");
                        int atom1 = Integer.parseInt(connect_root) - 1;
                        int atom2 = Integer.parseInt(atom) - 1;
                        cdo.setObjectProperty("Bond", "atom1", 
                                              (new Integer(atom1)).toString());
                        cdo.setObjectProperty("Bond", "atom2", 
                                              (new Integer(atom2)).toString());
                        cdo.setObjectProperty("Bond", "order", "1");
                        cdo.endObject("Bond");
                    }
                }
            }
        } 
    }
    
    
    protected void storeData() {
        if (inchi != null) {
            cdo.setObjectProperty("Molecule", "inchi", inchi);
        }
        storeAtomData();
        storeBondData();
    }

    protected void storeAtomData() {
        logger.debug("No atoms: ", atomCounter);
        if (atomCounter == 0) {
            return;
        }

        boolean hasID = false;
        boolean has3D = false;
        boolean has3Dfract = false;
        boolean has2D = false;
        boolean hasFormalCharge = false;
        boolean hasPartialCharge = false;
        boolean hasHCounts = false;
        boolean hasSymbols = false;
        boolean hasTitles = false;
        boolean hasIsotopes = false;
        boolean hasDictRefs = false;
        boolean hasSpinMultiplicities = false;
        boolean hasOccupancies = false;

        if (elid.size() == atomCounter) {
            hasID = true;
        } else {
            logger.debug("No atom ids: " + elid.size(), " != " + atomCounter);
        }

        if (elsym.size() == atomCounter) {
            hasSymbols = true;
        } else {
            logger.debug(
                    "No atom symbols: " + elsym.size(), " != " + atomCounter);
        }

        if (eltitles.size() == atomCounter) {
            hasTitles = true;
        } else {
            logger.debug(
                    "No atom titles: " + eltitles.size(), " != " + atomCounter);
        }

        if ((x3.size() == atomCounter) && (y3.size() == atomCounter) && 
            (z3.size() == atomCounter)) {
            has3D = true;
        } else {
            logger.debug(
                    "No 3D info: " + x3.size(), " " + y3.size(), " " + 
                    z3.size(), " != " + atomCounter);
        }

        if ((xfract.size() == atomCounter) && (yfract.size() == atomCounter) && 
            (zfract.size() == atomCounter)) {
            has3Dfract = true;
        } else {
            logger.debug(
                    "No 3D fractional info: " + xfract.size(), " " + yfract.size(), " " + 
                    zfract.size(), " != " + atomCounter);
        }

        if ((x2.size() == atomCounter) && (y2.size() == atomCounter)) {
            has2D = true;
        } else {
            logger.debug(
                    "No 2D info: " + x2.size(), " " + y2.size(), " != " + 
                    atomCounter);
        }

        if (formalCharges.size() == atomCounter) {
            hasFormalCharge = true;
        } else {
            logger.debug(
                    "No formal Charge info: " + formalCharges.size(), 
                    " != " + atomCounter);
        }

        if (partialCharges.size() == atomCounter) {
            hasPartialCharge = true;
        } else {
            logger.debug(
                    "No partial Charge info: " + partialCharges.size(),
                    " != " + atomCounter);
        }

        if (hCounts.size() == atomCounter) {
            hasHCounts = true;
        } else {
            logger.debug(
                    "No hydrogen Count info: " + hCounts.size(), 
                    " != " + atomCounter);
        }

        if (spinMultiplicities.size() == atomCounter) {
            hasSpinMultiplicities = true;
        } else {
            logger.debug(
                    "No spinMultiplicity info: " + spinMultiplicities.size(),
                    " != " + atomCounter);
        }

        if (occupancies.size() == atomCounter) {
            hasOccupancies = true;
        } else {
            logger.debug(
                    "No occupancy info: " + occupancies.size(),
                    " != " + atomCounter);
        }

        if (atomDictRefs.size() == atomCounter) {
            hasDictRefs = true;
        } else {
            logger.debug(
                    "No dictRef info: " + atomDictRefs.size(),
                    " != " + atomCounter);
        }

        if (isotope.size() == atomCounter) {
            hasIsotopes = true;
        } else {
            logger.debug(
                    "No isotope info: " + isotope.size(),
                    " != " + atomCounter);
        }
        if(atomCounter > 0){
        	cdo.startObject("PDBMonomer");
        }

		for (int i = 0; i < atomCounter; i++) {
            logger.info("Storing atom: ", i);
            cdo.startObject("PDBAtom");
            if (hasID) {
                cdo.setObjectProperty("Atom", "id", (String)elid.get(i));
            }
            if (hasTitles) {
                if (hasSymbols) {
                    String symbol = (String)elsym.get(i);
                    if (symbol.equals("Du") || symbol.equals("Dummy")) {
                        cdo.setObjectProperty("PseudoAtom", "label", (String)eltitles.get(i));
                    } else {
                        cdo.setObjectProperty("Atom", "title", (String)eltitles.get(i));
                    }
                } else {
                    cdo.setObjectProperty("Atom", "title", (String)eltitles.get(i));
                }
            }

            // store optional atom properties
            if (hasSymbols) {
                String symbol = (String)elsym.get(i);
                if (symbol.equals("Du") || symbol.equals("Dummy")) {
                    symbol = "R";
                }
                cdo.setObjectProperty("Atom", "type", symbol);
            }

            if (has3D) {
                cdo.setObjectProperty("Atom", "x3", (String)x3.get(i));
                cdo.setObjectProperty("Atom", "y3", (String)y3.get(i));
                cdo.setObjectProperty("Atom", "z3", (String)z3.get(i));
            }

            if (has3Dfract) {
                // ok, need to convert fractional into eucledian coordinates
                cdo.setObjectProperty("Atom", "xFract", (String)xfract.get(i));
                cdo.setObjectProperty("Atom", "yFract", (String)yfract.get(i));
                cdo.setObjectProperty("Atom", "zFract", (String)zfract.get(i));
            }

            if (hasFormalCharge) {
                cdo.setObjectProperty("Atom", "formalCharge", 
                                      (String)formalCharges.get(i));
            }

            if (hasPartialCharge) {
                logger.debug("Storing partial atomic charge...");
                cdo.setObjectProperty("Atom", "partialCharge", 
                                      (String)partialCharges.get(i));
            }

            if (hasHCounts) {
                cdo.setObjectProperty("Atom", "hydrogenCount", (String)hCounts.get(i));
            }

            if (has2D) {
                if (x2.get(i) != null)
                    cdo.setObjectProperty("Atom", "x2", (String)x2.get(i));
                if (y2.get(i) != null)
                    cdo.setObjectProperty("Atom", "y2", (String)y2.get(i));
            }
            
            if (hasDictRefs) {
                cdo.setObjectProperty("Atom", "dictRef", (String)atomDictRefs.get(i));
            }

            if (hasSpinMultiplicities && spinMultiplicities.get(i) != null) {
                cdo.setObjectProperty("Atom", "spinMultiplicity", (String)spinMultiplicities.get(i));
            }

            if (hasOccupancies && occupancies.get(i) != null) {
                cdo.setObjectProperty("PDBAtom", "occupancy", (String)occupancies.get(i));
            }

            if (hasIsotopes) {
                cdo.setObjectProperty("Atom", "massNumber", (String)isotope.get(i));
            }
            if(hasScalar){
                cdo.setObjectProperty("PDBAtom", "altLoc", altLocV.get(i).toString());
                cdo.setObjectProperty("PDBAtom", "chainID", chainIDV.get(i).toString());
                cdo.setObjectProperty("PDBAtom", "hetAtom", hetAtomV.get(i).toString());
                cdo.setObjectProperty("PDBAtom", "iCode", iCodeV.get(i).toString());
                cdo.setObjectProperty("PDBAtom", "name", nameV.get(i).toString());
                cdo.setObjectProperty("PDBAtom", "oxt", oxtV.get(i).toString());
                cdo.setObjectProperty("PDBAtom", "resSeq", resSeqV.get(i).toString());
                cdo.setObjectProperty("PDBAtom", "record", recordV.get(i).toString());
                cdo.setObjectProperty("PDBAtom", "resName", resNameV.get(i).toString());
                cdo.setObjectProperty("PDBAtom", "segID", segIDV.get(i).toString());
                cdo.setObjectProperty("PDBAtom", "serial", serialV.get(i).toString());
                cdo.setObjectProperty("PDBAtom", "tempFactor", tempFactorV.get(i).toString());
            }
            
            cdo.endObject("PDBAtom");
        }
		cdo.endObject("PDBMonomer");
        if (elid.size() > 0) {
            // assume this is the current working list
            bondElid = elid;
        }
        newAtomData();
    }
    
    
}
