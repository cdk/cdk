/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 * 
 */
package org.openscience.cdk.validate;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.ValencyChecker;
import org.openscience.cdk.tools.IValencyChecker;

/**
 * Validator which tests a the valencies of atoms using the
 * ValencyChecker.
 *
 * @cdk.module experimental
 *
 * @author   Egon Willighagen
 * @cdk.created  2004-01-14
 */ 
public class ValencyValidator extends AbstractValidator {

    private static LoggingTool logger;
    
    static {
        logger = new LoggingTool(ValencyValidator.class);
    }
    
    public ValencyValidator() {
    }
    
    public ValidationReport validateMolecule(Molecule subject) {
        ValidationReport report = new ValidationReport();
        org.openscience.cdk.interfaces.IAtom[] atoms = subject.getAtoms();
        for (int i=0; i<atoms.length; i++) {
            if (!(atoms[i] instanceof PseudoAtom)) {
                report.addReport(validateAtomValency(atoms[i], subject));
            }
        }
        return report;
    }
    
    // the Molecule tests

    private static ValidationReport validateAtomValency(org.openscience.cdk.interfaces.IAtom atom, Molecule molecule) {
        ValidationReport report = new ValidationReport();
        ValidationTest checkValency = new ValidationTest(atom,
            "The atom has an unfulfilled valency."
        );
        try {
            IValencyChecker saturationChecker = new ValencyChecker();
            if (!saturationChecker.isSaturated(atom, molecule)) {
                checkValency.setDetails("Atom " + atom.getSymbol() + " fails");
                report.addError(checkValency);
            } else {
                report.addOK(checkValency);
            }
        } catch (Exception exception) {
            String error = "Error while performing atom valency validation: " + exception.getMessage();
            logger.error(error);
            checkValency.setDetails(error);
            report.addCDKError(checkValency);
        }
        return report;
    }

}
