/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2003  Jmol Project
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.tools.LoggingTool;

import java.io.BufferedReader;
import java.io.CharArrayReader;
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
    
    public ReaderFactory() {}
    
    /**
     * Creates a String of the Class name of the ChemObject reader
     * for this file format. The input is read line-by-line
     * until a line containing an identifying string is
     * found.
     *
     * <p>The ReaderFactory detects more formats than the CDK
     * has Readers for.
     *
     * @throws IOException  if an I/O error occurs
     * @throws IllegalArgumentException if the input is null
     */
    public String guessFormat(Reader input) throws IOException {
        return createReader(input).getClass().getName();
    }
    
    public ChemObjectReader createReader(Reader input) throws IOException {
        LoggingTool logger = new LoggingTool(this.getClass().getName());
        
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null");
        }

        int bufferSize = 65536;
        BufferedReader originalBuffer = new BufferedReader(input, bufferSize);
        char[] header = new char[bufferSize];
        if (!originalBuffer.markSupported()) {
            logger.error("Mark not supported");
            throw new IllegalArgumentException("input must support mark");
        }
        originalBuffer.mark(bufferSize);
        originalBuffer.read(header, 0, bufferSize);
        originalBuffer.reset();
        
        BufferedReader buffer = new BufferedReader(new CharArrayReader(header));
        
        /* Search file for a line containing an identifying keyword */
        String line = buffer.readLine();
        int lineNumber = 1;
        while (buffer.ready() && (line != null)) {
            logger.debug(line);
            if (line.indexOf("Gaussian(R) 98") >= 0 ||
                line.indexOf("Gaussian 98") >= 0) {
                logger.info("Gaussian98 format detected");
                return new org.openscience.cdk.io.Gaussian98Reader(originalBuffer);
            } else if (line.indexOf("Gaussian(R) 03") >= 0) {
                logger.info("Gaussian03 format detected");
                return new org.openscience.cdk.io.Gaussian03Reader(originalBuffer);
            } else if (line.indexOf("Gaussian 95") >= 0) {
                logger.info("Gaussian95 format detected");
                return new org.openscience.cdk.io.Gaussian95Reader(originalBuffer);
            } else if (line.indexOf("Gaussian 94") >= 0) {
                logger.info("Gaussian94 format detected");
                return new org.openscience.cdk.io.Gaussian94Reader(originalBuffer);
            } else if (line.indexOf("Gaussian 92") >= 0) {
                logger.info("Gaussian92 format detected");
                return new org.openscience.cdk.io.Gaussian92Reader(originalBuffer);
            } else if (line.indexOf("Gaussian G90") >= 0) {
                logger.info("Gaussian90 format detected");
                return new org.openscience.cdk.io.Gaussian90Reader(originalBuffer);
            } else if (line.indexOf("GAMESS") >= 0) {
                logger.info("Gamess format detected");
                return new org.openscience.cdk.io.GamessReader(originalBuffer);
            } else if (lineNumber == 4 && (line.indexOf("v2000") >= 0 ||
                                           line.indexOf("V3000") >= 0 ||
                                           line.indexOf("V2000") >= 0)) {
                logger.info("MDL mol/sdf file format detected");
                return new org.openscience.cdk.io.MDLReader(originalBuffer);
            } else if (line.startsWith("M  END")) {
                logger.info("MDL mol/sdf file format detected");
                return new org.openscience.cdk.io.MDLReader(originalBuffer);
            } else if (line.startsWith("$RXN")) {
                logger.info("MDL rxn file format detected");
                return new org.openscience.cdk.io.MDLRXNReader(originalBuffer);
            } else if (line.startsWith("$RDFILE ")) {
                logger.info("MACiE file format detected");
                return new org.openscience.cdk.io.MACiEReader(originalBuffer);
            } else if (line.indexOf("ACES2") >= 0) {
                logger.info("Aces2 format detected");
                return new org.openscience.cdk.io.Aces2Reader(originalBuffer);
            } else if (line.indexOf("<TRIPOS>") >= 0) {
                logger.info("Mol2 format detected");
                return new org.openscience.cdk.io.Mol2Reader(originalBuffer);
            } else if (line.indexOf("Amsterdam Density Functional") >= 0) {
                logger.info("ADF format detected");
                return new org.openscience.cdk.io.ADFReader(originalBuffer);
            } else if (line.indexOf("DALTON") >= 0) {
                logger.info("Dalton format detected");
                return new org.openscience.cdk.io.DaltonReader(originalBuffer);
            } else if (line.indexOf("Jaguar") >= 0) {
                logger.info("Jaguar format detected");
                return new org.openscience.cdk.io.JaguarReader(originalBuffer);
            } else if (line.indexOf("MOPAC:  VERSION  7.00") >= 0) {
                logger.info("Mopac7 format detected");
                return new org.openscience.cdk.io.MOPAC7Reader(originalBuffer);
            } else if ((line.indexOf("MOPAC  97.00") >= 0) ||
                       (line.indexOf("MOPAC2002") >= 0)) {
                logger.info("Mopac97 format detected");
                return new org.openscience.cdk.io.MOPAC97Reader(originalBuffer);
            } else if (line.startsWith("molstruct")) {
                logger.info("CAChe format detected");
                return new org.openscience.cdk.io.CACheReader(originalBuffer);
            } else if (line.indexOf("NCLASS=") >= 0) {
                logger.info("VASP format detected");
                return new org.openscience.cdk.io.VASPReader(originalBuffer);
            } else if (line.indexOf("mm1gp") >= 0) {
                logger.info("GhemicalMM format detected");
                return new org.openscience.cdk.io.GhemicalMMReader(originalBuffer);
            } else if (line.indexOf("natom") >= 0 ||
                       line.indexOf("ABINIT") >= 0) {
                logger.info("ABINIT format detected");
                return new org.openscience.cdk.io.ABINITReader(originalBuffer);
            } else if (line.startsWith("HEADER") || line.startsWith("ATOM  ")) {
                logger.info("PDB format detected");
                return new org.openscience.cdk.io.PDBReader(originalBuffer);
            } else if ((line.indexOf("<atom") != -1) ||
                       (line.indexOf("<molecule") != -1) ||
                       (line.indexOf("<reaction") != -1) ||
                       (line.indexOf("<cml") != -1) ||
                       (line.indexOf("<bond") != -1)) {
                logger.info("CML format detected");
                return new org.openscience.cdk.io.CMLReader(originalBuffer);
            } else if (line.indexOf("<identifier") != -1) {
                logger.info("IChI format detected");
                return new org.openscience.cdk.io.IChIReader(originalBuffer);
            } else if (line.startsWith("%%Header Start")) {
                logger.info("PolyMorph Predictor format detected");
                return new org.openscience.cdk.io.PMPReader(originalBuffer);
            } else if (line.startsWith("ZERR ") ||
                       line.startsWith("TITL ")) {
                logger.info("ShelX format detected");
                return new org.openscience.cdk.io.ShelXReader(originalBuffer);
            } else if (lineNumber == 4 && line.length()>7) {
                // possibly a MDL mol file
                try {
                    String atomCountString = line.substring(0, 3).trim();
                    String bondCountString = line.substring(3, 6).trim();
                    new Integer(atomCountString);
                    new Integer(bondCountString);
                    boolean mdlFile = true;
                    if (line.length() > 6) {
                        String remainder = line.substring(6).trim();
                        for (int i = 0; i < remainder.length(); ++i) {
                            char c = remainder.charAt(i);
                            if (!(Character.isDigit(c) || Character.isWhitespace(c))) {
                                mdlFile = false;
                            }
                        }
                    }
                    // all tests succeeded, likely to be a MDL file
                    if (mdlFile) {
                        return new org.openscience.cdk.io.MDLReader(originalBuffer);
                    }
                } catch (NumberFormatException nfe) {
                    // Integers not found on fourth line; therefore not a MDL file
                }
            }
            line = buffer.readLine();
            lineNumber++;
        }

        logger.warn("Now come to tricky and more difficult ones....");
        buffer = new BufferedReader(new CharArrayReader(header));
        
        line = buffer.readLine();
        // is it a XYZ file?
        StringTokenizer tokenizer = new StringTokenizer(line.trim());
        try {
            int tokenCount = tokenizer.countTokens();
            if (tokenCount == 1) {
                new Integer(tokenizer.nextToken());
                // if not failed, then it is a XYZ file
                return new org.openscience.cdk.io.XYZReader(originalBuffer);
            } else if (tokenCount == 2) {
                new Integer(tokenizer.nextToken());
                if ("Bohr".equalsIgnoreCase(tokenizer.nextToken())) {
                    return new org.openscience.cdk.io.XYZReader(originalBuffer);
                }
            }
        } catch (NumberFormatException exception) {}
        // is it a SMILES file?
        try {
            SmilesParser sp = new SmilesParser();
            Molecule m = sp.parseSmiles(line);
            return new org.openscience.cdk.io.SMILESReader(originalBuffer);
        } catch (InvalidSmilesException ise) {
            // no, it is not
        }

        logger.warn("File format undetermined");
        return null;
    }

}
