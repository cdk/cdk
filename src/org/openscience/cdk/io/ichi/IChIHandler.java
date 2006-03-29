/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io.ichi;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

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
 * @cdk.module experimental
 *
 * @see org.openscience.cdk.io.IChIReader
 *
 * @cdk.require java1.4+
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
     * Constructor for the IChIHandler.
     **/
    public IChIHandler() {
        logger = new LoggingTool(this);
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
            } else {
                logger.warn("Cannot set dbond info for empty tautomer");
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
                } else if (atts.getQName(i).equals("version")) {
                    if (!atts.getValue(i).equals("0.932Beta")) {
                        logger.warn("The IChIReader only supports verion 0.932Beta. " +
                            "The outcome of the reading is undefined");
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
                analyseBondsEncoding(bondsEncoding, -1);
            }
        } else {
            logger.warn("Expected bond data missing!");
        }
    }

    private void analyseAtomsEncoding(String atomsEncoding) {
        logger.debug("Parsing atom data: " + atomsEncoding);

        Pattern pattern = Pattern.compile("([A-Z][a-z]?)(\\d+)?(.*)");
        String remainder = atomsEncoding;
        while (remainder.length() > 0) {
            logger.debug("Remaining: " + remainder);
            Matcher matcher = pattern.matcher(remainder);
            if (matcher.matches()) {
                String symbol = matcher.group(1);
                logger.debug("Atom symbol: " + symbol);
                if (symbol.equals("H")) {
                    // don't add explicit hydrogens
                } else {
                    String occurenceStr = matcher.group(2);
                    int occurence = 1;
                    if (occurenceStr != null) {
                        occurence = Integer.parseInt(occurenceStr);
                    }
                    logger.debug("  occurence: " + occurence);
                    for (int i=1; i<=occurence; i++) {
                        tautomer.addAtom(new Atom(symbol));
                    }
                }
                remainder = matcher.group(3);
                if (remainder == null) remainder = "";
                logger.debug("  Remaining: " + remainder);
            } else {
                logger.error("No match found!");
                remainder = "";
            }
            logger.debug("NO atoms: " + tautomer.getAtomCount());
        }
        return;
    }

    /**
     * @param source the atom to build the path upon. If -1, then start new path
     */
    private void analyseBondsEncoding(String bondsEncoding, int source){
        logger.debug("Parsing bond data: " + bondsEncoding);

        Bond bondToAdd = null;
        Pattern pattern = Pattern.compile("^(\\d+)(H?)(\\d?)(\\([^)]*\\))?-?(.*)");
        String remainder = bondsEncoding;
        while (remainder.length() > 0) {
            logger.debug("Bond part: " + remainder);
            Matcher matcher = pattern.matcher(remainder);
            if (matcher.matches()) {
                String targetStr = matcher.group(1);
                int target = Integer.parseInt(targetStr);
                logger.debug("Target atom: " + targetStr);
                IAtom targetAtom = tautomer.getAtomAt(target-1);
                String hStr = matcher.group(2);
                logger.debug(" hStr: " + hStr);
                String hCountStr = matcher.group(3);
                if (hStr != null) {
                    int hCount = 1;
                    if (hCountStr != null && hCountStr.length() > 0) {
                        hCount = Integer.parseInt(hCountStr);
                    }
                    targetAtom.setHydrogenCount(hCount);
                }
                if (source != -1) {
                	IAtom sourceAtom = tautomer.getAtomAt(source-1);
                    bondToAdd = new Bond(sourceAtom, targetAtom, 1.0);
                    tautomer.addBond(bondToAdd);
                }
                String branch = matcher.group(4);
                if (branch != null) {
                    analyseBondsEncoding(branch.substring(1,branch.length()-1), target); // make branch from target
                }
                source = target;
                remainder = matcher.group(5);
            } else {
                logger.error("Could not get next bond info part");
                return;
            }
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
        IAtom sourceAtom = tautomer.getAtomAt(source-1);
        IAtom targetAtom = tautomer.getAtomAt(target-1);
        
        IBond bond = tautomer.getBond(sourceAtom, targetAtom);
        bond.setOrder(CDKConstants.BONDORDER_DOUBLE);
        return;
    }

}
