/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.*;

/**
 * This Validator tests the internal datastructures, and
 * tries to detect inconsistencies in it.
 *
 * @author   Egon Willighagen
 * @created  2003-08-22
 */ 
public class CDKValidator implements ValidatorInterface {

    public CDKValidator() {
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
    public ValidationReport validateChemModel(ChemModel subject) {
        return new ValidationReport();
    };
    public ValidationReport validateChemFile(ChemFile subject) {
        return validateChemFileNulls(subject);
    };
    public ValidationReport validateChemObject(ChemObject subject) {
        return new ValidationReport();
    };
    public ValidationReport validateChemSequence(ChemSequence subject) {
        return validateChemSequenceNulls(subject);
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
        return new ValidationReport();
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


    public ValidationReport validateChemFileNulls(ChemFile chemFile) {
        ValidationReport report = new ValidationReport();
        ChemSequence[] sequences = chemFile.getChemSequences();
        ValidationTest hasNulls = new ValidationTest(chemFile,
            "ChemFile contains a null ChemSequence."
        );
        for (int i=0; i < chemFile.getChemSequenceCount(); i++) { // DIRTY !!!! FIXME !!!!!
            // but it does not seem to work on 1.4.2 otherwise....
            if (sequences[i] == null) {
                report.addError(hasNulls);
            } else {
                report.addOK(hasNulls);
            }
        }
        return report;
    }
        
    public ValidationReport validateChemSequenceNulls(ChemSequence sequence) {
        ValidationReport report = new ValidationReport();
        ChemModel[] models = sequence.getChemModels();
        ValidationTest hasNulls = new ValidationTest(sequence,
            "ChemSequence contains a null ChemModel."
        );
        for (int i=0; i < sequence.getChemModelCount(); i++) { // DIRTY !!!! FIXME !!!!!
            // but it does not seem to work on 1.4.2 otherwise....
            if (models[i] == null) {
                report.addError(hasNulls);
            } else {
                report.addOK(hasNulls);
            }
        }
        return report;
    }

}
