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
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.AtomTypeFactory;
import java.util.Vector;

/**
 * Tool to validate the chemical semantics for an Molecule.
 *
 * @author   Egon Willighagen
 * @created  2003-03-28
 *
 * @see      org.openscience.cdk.Molecule
 * @keyword atom, chemical validation
 */ 
public class MoleculeValidator {

    public static Vector validate(Molecule molecule) {
        Vector errors = new Vector();
        Atom[] atoms = molecule.getAtoms();
        if (atoms.length < 1) {
            errors.add(
              new ValidationWarning(molecule, "Molecule does not contain any atom")
            );
        }
        boolean foundMassCalcProblem = false;
        for (int i=0; i<atoms.length; i++) {
            if (atoms[i] instanceof PseudoAtom) {
                
            }
            errors.addAll(validateAtomValency(atoms[i], molecule));
            errors.addAll(validateBondOrderSum(atoms[i], molecule));
        }
        errors.add(
            new ValidationWarning(molecule, "Molecule contains PseudoAtom's. Won't be able to calculate some properties, like molecular mass.")
        );
        return errors;
    }
    
    private static Vector validateAtomValency(Atom atom, Molecule molecule) {
        Vector errors = new Vector();
        try {
            SaturationChecker saturationChecker = new SaturationChecker();
            if (!saturationChecker.isSaturated(atom, molecule)) {
                String error = "Atom " + atom.getSymbol() + " has an unfulfilled valency.";
                errors.add(new SeriousValidationError(atom, error));
            }
        } catch (Exception exception) {
            System.err.println("Error while performing atom valency validation: " + exception.toString());
        }
        return errors;
    }

    private static Vector validateBondOrderSum(Atom atom, Molecule molecule) {
        Vector errors = new Vector();
        try {
            AtomTypeFactory atf = new AtomTypeFactory();
            int bos = (int)molecule.getBondOrderSum(atom);
            Atom copy = (Atom)atom.clone();
            atf.configure(copy);
            if (copy.getMaxBondOrderSum() != 0 &&
                bos > copy.getMaxBondOrderSum()) {
                String error = "Atom's total bond order is too high.";
                errors.add(new SeriousValidationError(atom, error));
            }
        } catch (Exception exception) {
            System.err.println("Error while performing atom bos validation: " + exception.toString());
        }
        return errors;
    }

}
