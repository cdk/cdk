/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */

package org.openscience.cdk.io.ichi;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * SAX2 implementation for IChI XML fragment parsing.
 *
 * <p>The supported elements are: identifier, basic and
 * dbond. 
 * All other elements are not parsed (at this moment).
 *
 * <p>Not found in the original documentation is the use
 * of ";" in the &lt;basic> element, which is supported. 
 * See bug #642429.
 *
 * <p>The returned ChemFile contains a ChemSequence in
 * which the ChemModel represents the molecule.
 *
 * @see org.openscience.cdk.io.IChIReader
 */
public class IChIHandler extends DefaultHandler {

    private LoggingTool logger;

    private ChemFile chemFile;
    private ChemSequence chemSequence;
    private ChemModel chemModel;
    private SetOfMolecules setOfMolecules;
    private Molecule tautomer;

    /** Used to store all chars between two tags */
    private String currentChars;

    /**
     * Constructor for the CMLHandler.
     *
     * @param cdo The Chemical Document Object in which data is stored
     **/
    public IChIHandler() {
        logger = new LoggingTool(this.getClass().getName());
    }

    public void doctypeDecl(String name, String publicId, String systemId)
        throws Exception {
        logger.info("DocType root element: " + name);
        logger.info("DocType root PUBLIC: " + publicId);
        logger.info("DocType root SYSTEM: " + systemId);
    }

    /**
     * Implementation of the endDocument() procedure overwriting the
     * DefaultHandler interface.
     */
    public void startDocument() {
        chemFile = new ChemFile();
        chemSequence = new ChemSequence();
    }

    /**
     * Implementation of the endDocument() procedure overwriting the
     * DefaultHandler interface.
     */
    public void endDocument() {
        chemFile.addChemSequence(chemSequence);
    }

    /**
     * Implementation of the endElement() procedure overwriting the
     * DefaultHandler interface.
     *
     * @param uri       the Universal Resource Identifier
     * @param local     the local name (without namespace part)
     * @param raw       the complete element name (with namespace part)
     */
    public void endElement(String uri, String local, String raw) {
        logger.debug("end element: " + raw);
        if ("identifier".equals(local)) {
            if (tautomer != null) {
                // ok, add tautomer
                setOfMolecules.addMolecule(tautomer);
                chemModel.setSetOfMolecules(setOfMolecules);
                chemSequence.addChemModel(chemModel);
            }
        } else if ("basic".equals(local)) {
            if (tautomer != null) {
                logger.info("Parsing <basic> chars: " + currentChars);
                if (currentChars.indexOf(";") != 0) {
                    logger.debug("Multifragment molecule detected.");
                    /* structure consists of more than one fragment !
                       This feature is not mentioned in the documentation
                       for 0.9beta, but requested in bug #642429 */
                    StringTokenizer st = new StringTokenizer(currentChars, ";");
                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        parseOneMolecule(token);
                        if (st.hasMoreTokens()) {
                            setOfMolecules.addMolecule(tautomer);
                            tautomer = new Molecule();
                        }
                    }
                } else {
                    logger.debug("Only one molecule detected.");
                    parseOneMolecule(currentChars);
                }
            }
        } else if ("dbond".equals(local)) {
            if (tautomer != null) {
                logger.info("Parsing <dbond> chars: " + currentChars);
                StringTokenizer st = new StringTokenizer(currentChars, " ");
                if (st.hasMoreTokens()) {
                    String dbondEncoding =  st.nextToken();
                    analyseDBondEncoding(dbondEncoding);
                    while (st.hasMoreTokens()) {
                        dbondEncoding =  st.nextToken();
                        analyseDBondEncoding(dbondEncoding);
                    }
                } else {
                    logger.warn("Expected dbond data missing!");
                }
            }
        } else {
            // skip all other elements
        }
    }

    /**
     * Implementation of the startElement() procedure overwriting the
     * DefaultHandler interface.
     *
     * @param uri       the Universal Resource Identifier
     * @param local     the local name (without namespace part)
     * @param raw       the complete element name (with namespace part)
     * @param atts      the attributes of this element
     */
    public void startElement(String uri, String local, 
                             String raw, Attributes atts) {
        currentChars = "";
        logger.debug("startElement: " + raw);
        logger.debug("uri: " + uri);
        logger.debug("local: " + local);
        logger.debug("raw: " + raw);
        if ("IChI".equals(local)) {
            // check version
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("version"))
                    logger.info("IChI version: " + atts.getValue(i));
            }
        } else if ("identifier".equals(local)) {
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("tautomeric")) {
                    if (atts.getValue(i).equals("0")) {
                        // ok, start new Molecule
                        chemModel = new ChemModel();
                        setOfMolecules = new SetOfMolecules();
                        tautomer = new Molecule();
                    } else {
                        logger.info("Skipping tautomers.");
                        tautomer = null;
                    }
                }
            }
        } else {
            // skip all other elements
        }
    }

    /**
     * Implementation of the characters() procedure overwriting the
     * DefaultHandler interface.
     *
     * @param ch        characters to handle
     */
    public void characters(char ch[], int start, int length) {
        logger.debug("character data");
        currentChars += new String(ch, start, length);
    }

    public ChemFile getChemFile() {
        return chemFile;
    }

    // private methods

    /**
     * Results of parsing is stored in global tautomer variable 
     */
    private void parseOneMolecule(String currentChars) {
        logger.debug("Parsing one molecule: " + currentChars);
        StringTokenizer st = new StringTokenizer(currentChars, ",");
        if (st.hasMoreTokens()) {
            String atomsEncoding =  st.nextToken();
            analyseAtomsEncoding(atomsEncoding);
        } else {
            logger.warn("Expected atom data missing!");
        }
        if (st.hasMoreTokens()) {
            String bondsEncodings =  st.nextToken();
            StringTokenizer st2 = new StringTokenizer(bondsEncodings, " ");
            while (st2.hasMoreTokens()) {
                String bondsEncoding = st2.nextToken();
                analyseBondsEncoding(bondsEncoding);
            }
        } else {
            logger.warn("Expected bond data missing!");
        }
    }

    private void analyseAtomsEncoding(String atomsEncoding){
        logger.debug("Parsing atom data: " + atomsEncoding);

        char thisChar; /* Buffer for */
        String symbol = new String();

        Atom atomToAdd = null;
        for (int f = 0; f < atomsEncoding.length(); f++) {
            thisChar = atomsEncoding.charAt(f);
            if (thisChar >= 'A' && thisChar <= 'Z'){
                /* New Element begins */
                symbol = String.valueOf(thisChar);
                if ((f < (atomsEncoding.length()-1))) {
                    // Check for two-letter symbol
                    char nextChar = atomsEncoding.charAt(f+1);
                    if ((nextChar >= 'a' && nextChar<= 'z')) {
                        /* Two-letter Element */
                        symbol += nextChar;
                        f++;
                    }
                }
                logger.debug("Atom symbol: " + symbol);
                // add previous atom?
                if (atomToAdd != null) tautomer.addAtom(atomToAdd);
                atomToAdd = new Atom(symbol);
            } else if (thisChar >= '0' && thisChar<= '9') {
                /* Hydrogen count */
                atomToAdd.setHydrogenCount(Integer.parseInt(String.valueOf(thisChar)));
            } else if (thisChar == '*' && (f < (atomsEncoding.length()-1))) {
                /* atom occurence */
                char nextChar = atomsEncoding.charAt(++f);
                int occurence = Integer.parseInt(String.valueOf(nextChar));
                logger.debug("Adding copies: " + occurence);
                for (int i=1; i<=occurence; i++) {
                    Atom copy = (Atom)atomToAdd.clone();
                    tautomer.addAtom(copy);
                }
                // all atoms are added, thus:
                atomToAdd = null;
            } else {
                logger.error("Cannot parse atoms encoding: " + atomsEncoding);
                return;
            }
        }
        if (atomToAdd != null) tautomer.addAtom(atomToAdd);
        logger.debug("NO atoms: " + tautomer.getAtomCount());
        return;
    }

    private void analyseBondsEncoding(String bondsEncoding){
        logger.debug("Parsing bond data: " + bondsEncoding);

        int atoms = tautomer.getAtomCount();

        Bond bondToAdd = null;
        StringTokenizer st = new StringTokenizer(bondsEncoding, "-");
        if (!st.hasMoreTokens()) {
            logger.error("Cannot parse bonds encoding: " + bondsEncoding);
            return;
        }
        int source = Integer.parseInt(st.nextToken()); // at least one token
        while (st.hasMoreTokens()) {
            int target = Integer.parseInt(st.nextToken());
            // should better check if atom exists!
            Atom sourceAtom = tautomer.getAtomAt(source-1);
            Atom targetAtom = tautomer.getAtomAt(target-1);
            bondToAdd = new Bond(sourceAtom, targetAtom, 1.0);
            tautomer.addBond(bondToAdd);
        }
        return;
    }

    private void analyseDBondEncoding(String dbondEncoding){
        logger.debug("Parsing double bond data: " + dbondEncoding);

        StringTokenizer st = new StringTokenizer(dbondEncoding, "-");
        if (!st.hasMoreTokens()) {
            logger.error("Cannot parse bonds encoding: " + dbondEncoding);
            return;
        }
        int source = Integer.parseInt(st.nextToken()); // at least one token
        if (!st.hasMoreTokens()) {
            logger.error("Cannot parse bonds encoding: " + dbondEncoding);
            return;
        }
        int target = Integer.parseInt(st.nextToken());
        // should better check if atom exists!
        Atom sourceAtom = tautomer.getAtomAt(source-1);
        Atom targetAtom = tautomer.getAtomAt(target-1);
        
        Bond bond = tautomer.getBond(sourceAtom, targetAtom);
        bond.setOrder(CDKConstants.BONDORDER_DOUBLE);
        return;
    }

}
