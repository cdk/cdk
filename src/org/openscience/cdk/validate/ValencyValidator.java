/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.Isotope;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.ValencyChecker;
import org.openscience.cdk.tools.ValencyCheckerInterface;

/**
 * Validator which tests a the valencies of atoms using the
 * ValencyChecker.
 *
 * @cdk.module experimental
 *
 * @author   Egon Willighagen
 * @created  2004-01-14
 */ 
public class ValencyValidator implements ValidatorInterface {

    private static LoggingTool logger;
    
    static {
        logger = new LoggingTool("org.openscience.cdk.validate.ValencyValidator");
    }
    
    public ValencyValidator() {
    }
    
    public ValidationReport validateAtom(Atom subject) {
        return new ValidationReport();
    };
    public ValidationReport validateAtomContainer(AtomContainer subject) {
        return new ValidationReport();
    };
    public ValidationReport validateAtomType(AtomType subject) {
        return new ValidationReport();
    };
    public ValidationReport validateBond(Bond subject) {
        return new ValidationReport();
    };
    public ValidationReport validateChemFile(ChemFile subject) {
        return new ValidationReport();
    };
    public ValidationReport validateChemModel(ChemModel subject) {
        return new ValidationReport();
    };
    public ValidationReport validateChemObject(ChemObject subject) {
        return new ValidationReport();
    };
    public ValidationReport validateChemSequence(ChemSequence subject) {
        return new ValidationReport();
    };
    public ValidationReport validateCrystal(Crystal subject) {
        return new ValidationReport();
    };
    public ValidationReport validateElectronContainer(ElectronContainer subject) {
        return new ValidationReport();
    };
    public ValidationReport validateElement(Element subject) {
        return new ValidationReport();
    };
    public ValidationReport validateIsotope(Isotope subject) {
        return new ValidationReport();
    };
    public ValidationReport validateMolecule(Molecule subject) {
        ValidationReport report = new ValidationReport();
        Atom[] atoms = subject.getAtoms();
        for (int i=0; i<atoms.length; i++) {
            report.addReport(validateAtomValency(atoms[i], subject));
        }
        return report;
    };
    public ValidationReport validateReaction(Reaction subject) {
        return new ValidationReport();
    };
    public ValidationReport validateSetOfMolecules(SetOfMolecules subject) {
        return new ValidationReport();
    };
    public ValidationReport validateSetOfReactions(SetOfReactions subject) {
        return new ValidationReport();
    };
    
    // the Molecule tests

    private static ValidationReport validateAtomValency(Atom atom, Molecule molecule) {
        ValidationReport report = new ValidationReport();
        ValidationTest checkValency = new ValidationTest(atom,
            "The atom has an unfulfilled valency."
        );
        try {
            ValencyCheckerInterface saturationChecker = new ValencyChecker();
            if (!saturationChecker.isSaturated(atom, molecule)) {
                checkValency.setDetails("Atom " + atom.getSymbol() + " fails");
                report.addError(checkValency);
            } else {
                report.addOK(checkValency);
            }
        } catch (Exception exception) {
            System.err.println("Error while performing atom valency validation: " + exception.toString());
        }
        return report;
    }

}
