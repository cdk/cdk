/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io.inchi;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
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
 * @cdk.module extra
 * @cdk.svnrev  $Revision$
 *
 * @see org.openscience.cdk.io.INChIReader
 *
 * @cdk.require java1.4+
 */
public class INChIHandler extends DefaultHandler {

    private LoggingTool logger;
    private INChIContentProcessorTool inchiTool;

    private ChemFile chemFile;
    private ChemSequence chemSequence;
    private ChemModel chemModel;
    private MoleculeSet setOfMolecules;
    private Molecule tautomer;

    /** Used to store all chars between two tags */
    private String currentChars;

    /**
     * Constructor for the IChIHandler.
     **/
    public INChIHandler() {
        logger = new LoggingTool(this);
        inchiTool = new INChIContentProcessorTool();
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
        setOfMolecules = new MoleculeSet();
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
                chemModel.setMoleculeSet(setOfMolecules);
                chemSequence.addChemModel(chemModel);
            }
        } else if ("formula".equals(local)) {
            if (tautomer != null) {
                logger.info("Parsing <formula> chars: ", currentChars);
                tautomer = new Molecule(inchiTool.processFormula(
                	setOfMolecules.getBuilder().newAtomContainer(), currentChars
                ));
            } else {
                logger.warn("Cannot set atom info for empty tautomer");
            }
        } else if ("connections".equals(local)) {
            if (tautomer != null) {
                logger.info("Parsing <connections> chars: ", currentChars);
                inchiTool.processConnections(currentChars, tautomer, -1);
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
    
}
