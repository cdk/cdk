/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2003  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.io;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.exception.InvalidSmilesException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import java.util.StringTokenizer;
import java.util.Vector;


/**
 * A factory for creating ChemObjectReaders. The type of reader 
 * created is determined from the content of the input.
 *
 * <p>The ReaderFactory does not properly rewind (please fix this!),
 * so the resulting ChemObject reader <b>cannot</b> be used.
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 * @author  Bradley A. Smith <bradley@baysmith.com>
 */
public class ReaderFactory {
    
    private Vector headerBuffer = null;
    private int currentLine = 0;
    private BufferedReader buffer = null;
    
    public ReaderFactory() {
        headerBuffer = new Vector();
        currentLine = 0;
    }
    
    public String guessFormat(Reader input) throws IOException {
        ChemObjectReader reader = createReader(input);
        if (reader == null) {
            return "unknown";
        } else {
            return reader.getClass().getName();
        }
    }
    
    /**
     * Creates a ChemObjectReader of the type determined by
     * reading the input. The input is read line-by-line
     * until a line containing an identifying string is
     * found.
     *
     * @return  If the input type is determined, a
     *   ChemFileReader subclass is returned; otherwise,
     *   null is returned.
     * @throws IOException  if an I/O error occurs
     * @throws IllegalArgumentException if the input is null
     */
    public ChemObjectReader createReader(Reader input) throws IOException {
        org.openscience.cdk.tools.LoggingTool logger = 
            new org.openscience.cdk.tools.LoggingTool(
                "org.openscience.cdk.io.ReaderFactory"
            );
        
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null");
        }

        buffer = new BufferedReader(input);
        
        if (!buffer.markSupported()) {
            logger.error("Mark not supported");
            throw new IllegalArgumentException("input must support mark");
        }
        buffer.mark(1 << 16); /* at least 10 lines */
        
        /* Search file for a line containing an identifying keyword */
        String line = buffer.readLine();
        int lineNumber = 0;
        while (buffer.ready() && (line != null) && lineNumber < 25) {
            logger.debug(line);
            if (line.startsWith("HEADER") || line.startsWith("ATOM  ")) {
                logger.info("PDB format detected");
                buffer.reset();
                return new PDBReader(input);
            } else if ((line.indexOf("<atom") != -1) ||
                       (line.indexOf("<molecule") != -1) ||
                       (line.indexOf("<reaction") != -1) ||
                       (line.indexOf("<cml") != -1) ||
                       (line.indexOf("<bond") != -1)) {
                logger.info("CML format detected");
                buffer.reset();
                return new CMLReader(buffer);
            } else if (line.indexOf("<identifier") != -1) {
                logger.info("IChI format detected");
                buffer.reset();
                return new IChIReader(buffer);
            } else if (line.startsWith("%%Header Start")) {
                logger.info("PolyMorph Predictor format detected");
                buffer.reset();
                return new PMPReader(buffer);
            } else if (line.startsWith("ZERR ") ||
                       line.startsWith("TITL ")) {
                logger.info("ShelX format detected");
                buffer.reset();
                return new ShelXReader(buffer);
            }
            line = buffer.readLine();
            lineNumber++;
        }
        buffer.reset();

        if (isMDLMolfile(buffer)) {
            logger.info("MDL Molfile format detected");
            return new MDLReader(buffer);
        }

        if (isXYZfile(buffer)) {
            logger.info("XYZ format detected");
            return new XYZReader(buffer);
        }
        
        if (isSMILESfile(buffer)) {
            logger.info("SMILES format detected");
            return new SMILESReader(buffer);
        }

        logger.warn("File format undetermined");

        return null;
    }
    
    private boolean isMDLMolfile(BufferedReader buffer)
                                         throws IOException
    {
        buffer.reset();
        buffer.readLine();
        buffer.readLine();
        buffer.readLine();
        String line4 = buffer.readLine();
        buffer.reset();
        
        // If the fourth line contains the MDL Ctab version tag or
        // contains two integers in the first 6 characters and the
        // rest of the line only contains whitespace and digits,
        // the file is identified as an MDL file
        boolean mdlFile = false;
        if (line4 != null) {
            if (line4.trim().endsWith("V2000") || 
                line4.trim().endsWith("V3000")) {
                mdlFile = true;
            } else if (line4.length() >= 6) {
                try {
                    String atomCountString = line4.substring(0, 3).trim();
                    String bondCountString = line4.substring(3, 6).trim();
                    new Integer(atomCountString);
                    new Integer(bondCountString);
                    mdlFile = true;
                    if (line4.length() > 6) {
                        String remainder = line4.substring(6).trim();
                        for (int i = 0; i < remainder.length(); ++i) {
                            char c = remainder.charAt(i);
                            if (!(Character.isDigit(c) || Character.isWhitespace(c))) {
                                mdlFile = false;
                            }
                        }
                    }
                } catch (NumberFormatException nfe) {
                    // Integer not found on first line; therefore not a MDL file
                }
            }
        }
        return mdlFile;
    }
    
    private boolean isXYZfile(BufferedReader buffer)
                                         throws IOException
    {
        // An integer on the first line is a special test for XYZ files
        buffer.reset();
        String line = buffer.readLine();
        boolean xyzFile = false;
        if (line != null) {
            StringTokenizer tokenizer = new StringTokenizer(line.trim());
            try {
                int tokenCount = tokenizer.countTokens();
                if (tokenCount == 1) {
                    new Integer(tokenizer.nextToken());
                    xyzFile = true;
                } else if (tokenCount == 2) {
                    new Integer(tokenizer.nextToken());
                    if ("Bohr".equalsIgnoreCase(tokenizer.nextToken())) {
                        xyzFile = true;
                    }
                }
            } catch (NumberFormatException nfe) {
                // Integer not found on first line; therefore not a XYZ file
                xyzFile = false;
            }
        }
        buffer.reset();
        return xyzFile;
    }

    private boolean isSMILESfile(BufferedReader buffer)
                                         throws IOException
    {
        // If the first line is a parsable SMILES string then the file
        // is a SMILES file
        buffer.reset();
        String line = buffer.readLine();
        boolean smilesFile = false;
        try {
            SmilesParser sp = new SmilesParser();
            Molecule m = sp.parseSmiles(line);
            smilesFile = true;
        } catch (InvalidSmilesException ise) {
        }
        buffer.reset();
        return smilesFile;
    }
}
