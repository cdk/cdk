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
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk.validate;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.Element;
import org.openscience.cdk.EnzymeResidueLocator;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.io.MACiEReader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.tools.ChemFileManipulator;
import org.openscience.cdk.tools.ChemModelManipulator;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Validates the existence of references to dictionaries.
 *
 * @cdk.module experimental
 *
 * @author   Egon Willighagen
 * @cdk.created  2003-08-27
 *
 * @since Java 1.4
 */ 
public class PDBValidator implements ValidatorInterface {

    private static LoggingTool logger;
    
    static {
        logger = new LoggingTool("org.openscience.cdk.validate.PDBValidator");
    }

    private static String prefix = "http://www.rcsb.org/pdb/cgi/export.cgi/?format=PDB&pdbId=";
    private static String postfix = "&compression=None";

    public PDBValidator() {}

    public ValidationReport validateChemObject(ChemObject subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateAtom(Atom subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateAtomContainer(AtomContainer subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateAtomType(AtomType subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateBond(Bond subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateChemFile(ChemFile subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateChemModel(ChemModel subject) {
        ValidationReport report = new ValidationReport();
        Object PDBcodeObject = subject.getProperty(MACiEReader.PDBCode);
        if (PDBcodeObject != null) {
            String PDB = PDBcodeObject.toString();
            logger.info("Validating against PDB code: " + PDB);
            ChemFile file = null;
            try {
                URL pdbQuery = new URL(prefix + PDB + postfix);
                logger.info("Downloading PDB file from: " + pdbQuery.toString());
                URLConnection connection = pdbQuery.openConnection();
                PDBReader reader = new PDBReader(new InputStreamReader(connection.getInputStream()));
                file = (ChemFile)reader.read(new ChemFile());
            } catch (Exception exception) {
                logger.error("Could not download or parse PDB entry");
                logger.debug(exception);
                return report;
            }
            logger.info("Successvully download PDB entry");
            
            // ok, now make a hash with all residueLocator in the PDB file
            Vector residues = new Vector();
            AtomContainer allPDBAtoms = ChemFileManipulator.getAllInOneContainer(file);
            Atom[] atoms = allPDBAtoms.getAtoms();
            logger.info("Found in PDB file, #atoms: " + atoms.length);
            for (int i=0; i< atoms.length; i++) {
                String resName = (String)atoms[i].getProperty("pdb.resName");
                String resSeq = (String)atoms[i].getProperty("pdb.resSeq");
                String resLocator = resName + resSeq;
                if (!residues.contains(resLocator.toLowerCase())) {
                    logger.debug("Found new residueLocator: " + resLocator);
                    residues.add(resLocator.toLowerCase());
                }
            }
            
            // now see if the model undergoing validation has bad locators
            AtomContainer allAtoms = ChemModelManipulator.getAllInOneContainer(subject);
            Atom[] validateAtoms = allAtoms.getAtoms();
            for (int i=0; i<validateAtoms.length; i++) {
                // only testing PseudoAtom's
                Atom validateAtom = validateAtoms[i];
                if (validateAtom instanceof EnzymeResidueLocator) {
                    ValidationTest badResidueLocator = new ValidationTest(validateAtom,
                        "ResidueLocator does not exist in PDB entry."
                    );
                    String label = ((PseudoAtom)validateAtom).getLabel();
                    if (residues.contains(label.toLowerCase())) {
                        // yes, not problem
                        report.addOK(badResidueLocator);
                    } else {
                        badResidueLocator.setDetails(
                            "Could not find " + label + " in PDB entry for " + PDB
                        );
                        report.addError(badResidueLocator);
                    }
                } else {
                    // ok, then don't test
                }
            }
        }
        return report;
    };
    public ValidationReport validateChemSequence(ChemSequence subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateCrystal(Crystal subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateElectronContainer(ElectronContainer subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateElement(Element subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateIsotope(Isotope subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateMolecule(Molecule subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateReaction(Reaction subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateSetOfMolecules(SetOfMolecules subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateSetOfReactions(SetOfReactions subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    
}
