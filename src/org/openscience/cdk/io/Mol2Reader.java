/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.config.AtomTypeFactory;

/**
 * Reads a molecule from an Mol2 file, such as written by Sybyl.
 *
 * @cdk.module io
 *
 * @author     Egon Willighagen
 * @cdk.created    2003-08-21
 *
 * @cdk.keyword    file format, Mol2
 */
public class Mol2Reader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private org.openscience.cdk.tools.LoggingTool logger = null;

    /**
     * Contructs a new MDLReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public Mol2Reader(Reader in) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        input = new BufferedReader(in);
    }

    public Mol2Reader() {
        this(new StringReader(""));
    }
    
    public String getFormatName() {
        return "Mol2 (Sybyl)";
    }

    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    public boolean matches(int lineNumber, String line) {
        if (line.indexOf("<TRIPOS>") >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Takes an object which subclasses ChemObject, e.g.Molecule, and will read
     * this (from file, database, internet etc). If the specific implementation
     * does not support a specific ChemObject it will throw an Exception.
     *
     * @param  object                              The object that subclasses
     *      ChemObject
     * @return                                     The ChemObject read
     * @exception  CDKException
     */
     public ChemObject read(ChemObject object) throws CDKException {
         if (object instanceof ChemFile) {
             ChemFile file = new ChemFile();
             ChemSequence sequence = new ChemSequence();
             ChemModel model = new ChemModel();
             SetOfMolecules moleculeSet = new SetOfMolecules();
             moleculeSet.addMolecule(readMolecule());
             model.setSetOfMolecules(moleculeSet);
             sequence.addChemModel(model);
             file.addChemSequence(sequence);
             return file;
         } else if (object instanceof ChemModel) {
             ChemModel model = new ChemModel();
             SetOfMolecules moleculeSet = new SetOfMolecules();
             moleculeSet.addMolecule(readMolecule());
             model.setSetOfMolecules(moleculeSet);
             return model;
         } else {
             throw new CDKException("Only supported is ChemModel, and not " +
                 object.getClass().getName() + "."
             );
         }
     }
     
     public boolean accepts(ChemObject object) {
         if (object instanceof ChemFile) {
         } else if (object instanceof ChemModel) {
             return true;
         }
         return false;
     }


    /**
     * Read a Reaction from a file in MDL RXN format
     *
     * @return  The Reaction that was read from the MDL file.
     */
    private Molecule readMolecule() throws CDKException {
        Molecule molecule = new Molecule();
        
        AtomTypeFactory atFactory = null;
        try {
            atFactory = AtomTypeFactory.getInstance(
                "org/openscience/cdk/config/mol2_atomtypes.txt"
            );
        } catch (Exception exception) {
            String error = "Could not instantiate an AtomTypeFactory";
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error);
        }
        try {
            String line = input.readLine();
            int atomCount = 0;
            int bondCount = 0;
            while (line != null) {
                if (line.startsWith("@<TRIPOS>MOLECULE")) {
                    logger.info("Reading molecule block");
                    // second line has atom/bond counts?
                    String name = input.readLine();
                    String counts = input.readLine();
                    String atomCountStr = counts.substring(0,5).trim();
                    String bondCountStr = counts.substring(6,11).trim();
                    try {
                        atomCount = Integer.parseInt(atomCountStr);
                        bondCount = Integer.parseInt(bondCountStr);
                        logger.info("Reading #atoms: " + atomCount);
                        logger.info("Reading #bonds: " + bondCount);
                    } catch (NumberFormatException nfExc) {
                        String error = "Error while reading atom and bond counts";
                        logger.error(error);
                        logger.debug(nfExc);
                        throw new CDKException(error);
                    }
                    
                    logger.warn("Not reading molecule qualifiers");
                } else if (line.startsWith("@<TRIPOS>ATOM")) {
                    logger.info("Reading atom block");
                    for (int i=0; i<atomCount; i++) {
                        line = input.readLine();
                        String idStr = line.substring(8, 15).trim();
                        String xStr = line.substring(17, 26).trim();
                        String yStr = line.substring(27, 36).trim();
                        String zStr = line.substring(37, 46).trim();
                        String atomTypeStr = line.substring(47, 52).trim();
                        AtomType atomType = atFactory.getAtomType(atomTypeStr);
                        if (atomType == null) {
                            atomType = atFactory.getAtomType("X");
                            logger.error("Could not find specified atom type: " +atomTypeStr);
                        }
                        Atom atom = new Atom("X");
                        atom.setID(idStr);
                        atom.setAtomTypeName(atomTypeStr);
                        atFactory.configure(atom);
                        try {
                            double x = Double.parseDouble(xStr);
                            double y = Double.parseDouble(yStr);
                            double z = Double.parseDouble(zStr);
                            atom.setPoint3d(new Point3d(x, y, z));
                        } catch (NumberFormatException nfExc) {
                            String error = "Error while reading atom coordinates";
                            logger.error(error);
                            logger.debug(nfExc);
                            throw new CDKException(error);
                        }
                        molecule.addAtom(atom);
                    }
                } else if (line.startsWith("@<TRIPOS>BOND")) {
                    logger.info("Reading bond block");
                    for (int i=0; i<bondCount; i++) {
                        line = input.readLine();
                        String atom1Str = line.substring(7, 11).trim();
                        String atom2Str = line.substring(12, 16).trim();
                        String orderStr = line.substring(17).trim();
                        try {
                            int atom1 = Integer.parseInt(atom1Str);
                            int atom2 = Integer.parseInt(atom2Str);
                            int order = Integer.parseInt(orderStr);
                            molecule.addBond(atom1-1, atom2-1, (double)order);
                        } catch (NumberFormatException nfExc) {
                            String error = "Error while reading bond information";
                            logger.error(error);
                            logger.debug(nfExc);
                            throw new CDKException(error);
                        }
                    }
                }
                line = input.readLine();
            }
        } catch (IOException exception) {
            String error = "Error while reading general structure";
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error);
        }
        return molecule;
    }
    
    public void close() throws IOException {
        input.close();
    }
}

