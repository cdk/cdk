/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io.inchi;

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
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX2 implementation for INChI XML fragment parsing.
 *
 * <p>The supported elements are: identifier, formula and
 * connections. All other elements are not parsed (at this moment).
 * This parser is written based on the INChI files in data/ichi
 * for version 1.1Beta.
 *
 * <p>The returned ChemFile contains a ChemSequence in
 * which the ChemModel represents the molecule.
 *
 * @cdk.module experimental
 *
 * @see org.openscience.cdk.io.INChIReader
 *
 * @cdk.require java1.4
 */
public class INChIHandler extends DefaultHandler {

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
    public INChIHandler() {
        logger = new LoggingTool(this);
    }

    public void doctypeDecl(String name, String publicId, String systemId)
        throws Exception {
        logger.info("DocType root element: " + name);
        logger.info("DocType root PUBLIC: " + publicId);
        logger.info("DocType root SYSTEM: " + systemId);
    }

    public void startDocument() {
        chemFile = new ChemFile();
        chemSequence = new ChemSequence();
        chemModel = new ChemModel();
        setOfMolecules = new SetOfMolecules();
    }

    public void endDocument() {
        chemFile.addChemSequence(chemSequence);
    }

    public void endElement(String uri, String local, String raw) {
        logger.debug("end element: ", raw);
        if ("identifier".equals(local)) {
            if (tautomer != null) {
                // ok, add tautomer
                setOfMolecules.addMolecule(tautomer);
                chemModel.setSetOfMolecules(setOfMolecules);
                chemSequence.addChemModel(chemModel);
            }
        } else if ("formula".equals(local)) {
            if (tautomer != null) {
                logger.info("Parsing <formula> chars: ", currentChars);
                analyseAtomsEncoding(currentChars);
            } else {
                logger.warn("Cannot set atom info for empty tautomer");
            }
        } else if ("connections".equals(local)) {
            if (tautomer != null) {
                logger.info("Parsing <connections> chars: ", currentChars);
                analyseBondsEncoding(currentChars, -1);
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
        logger.debug("startElement: ", raw);
        logger.debug("uri: ", uri);
        logger.debug("local: ", local);
        logger.debug("raw: ", raw);
        if ("INChI".equals(local)) {
            // check version
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("version"))
                    logger.info("INChI version: ", atts.getValue(i));
            }
        } else if ("structure".equals(local)) {
            tautomer = new Molecule();
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

    private void analyseAtomsEncoding(String atomsEncoding) {
        logger.debug("Parsing atom data: ", atomsEncoding);

        Atom atomToAdd = null;
        Pattern pattern = Pattern.compile("([A-Z][a-z]?)(\\d+)?(.*)");
        String remainder = atomsEncoding;
        while (remainder.length() > 0) {
            logger.debug("Remaining: ", remainder);
            Matcher matcher = pattern.matcher(remainder);
            if (matcher.matches()) {
                String symbol = matcher.group(1);
                logger.debug("Atom symbol: ", symbol);
                if (symbol.equals("H")) {
                    // don't add explicit hydrogens
                } else {
                    String occurenceStr = matcher.group(2);
                    int occurence = 1;
                    if (occurenceStr != null) {
                        occurence = Integer.parseInt(occurenceStr);
                    }
                    logger.debug("  occurence: ", occurence);
                    for (int i=1; i<=occurence; i++) {
                        tautomer.addAtom(new Atom(symbol));
                    }
                }
                remainder = matcher.group(3);
                if (remainder == null) remainder = "";
                logger.debug("  Remaining: ", remainder);
            } else {
                logger.error("No match found!");
                remainder = "";
            }
            logger.debug("NO atoms: ", tautomer.getAtomCount());
        }
        return;
    }

    /**
     * @param source the atom to build the path upon. If -1, then start new path
     */
    private void analyseBondsEncoding(String bondsEncoding, int source){
        logger.debug("Parsing bond data: ", bondsEncoding);

        int atoms = tautomer.getAtomCount();

        Bond bondToAdd = null;
        /* Fixme: treatment of branching is too limited! */
        String remainder = bondsEncoding;
        while (remainder.length() > 0) {
            logger.debug("Bond part: ", remainder);
            if (remainder.charAt(0) == '(') {
                String branch = chopBranch(remainder);
                analyseBondsEncoding(branch, source);
                if (branch.length()+2 <= remainder.length()) {
                    remainder = remainder.substring(branch.length()+2);
                } else {
                    remainder = "";
                }
            } else {
                Pattern pattern = Pattern.compile("^(\\d+)-?(.*)");
                Matcher matcher = pattern.matcher(remainder);
                if (matcher.matches()) {
                    String targetStr = matcher.group(1);
                    int target = Integer.parseInt(targetStr);
                    logger.debug("Source atom: ", source);
                    logger.debug("Target atom: ", targetStr);
                    Atom targetAtom = tautomer.getAtomAt(target-1);
                    if (source != -1) {
                        Atom sourceAtom = tautomer.getAtomAt(source-1);
                        bondToAdd = new Bond(sourceAtom, targetAtom, 1.0);
                        tautomer.addBond(bondToAdd);
                    }
                    remainder = matcher.group(2);
                    source = target;
                    logger.debug("  remainder: ", remainder);
                } else {
                    logger.error("Could not get next bond info part");
                    return;
                }
            }
        }
        return;
    }

    /**
     * Extracts the first full branch. It extracts everything between the first
     * '(' and the corresponding ')' char.
     */
    private String chopBranch(String remainder) {
        boolean doChop = false;
        int branchLevel = 0;
        StringBuffer choppedString = new StringBuffer();
        for (int i=0; i<remainder.length(); i++) {
            char currentChar = remainder.charAt(i);
            if (currentChar == '(') {
                if (doChop) choppedString.append(currentChar);
                doChop = true;
                branchLevel++;
            } else if (currentChar == ')') {
                branchLevel--;
                if (branchLevel == 0) doChop = false;
                if (doChop) choppedString.append(currentChar);
            } else if (doChop) {
                choppedString.append(currentChar);
            }
        }
        return choppedString.toString();
    }
    
}
