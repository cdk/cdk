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

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.tools.SaturationChecker;
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
        for (int i=0; i<atoms.length; i++) {
            errors.addAll(validateAtomValency(atoms[i], molecule));
        }
        return errors;
    }
    
    private static Vector validateAtomValency(Atom atom, Molecule molecule) {
        Vector errors = new Vector();
        try {
            SaturationChecker saturationChecker = new SaturationChecker();
            if (!saturationChecker.isSaturated(atom, molecule)) {
                String error = "Atom " + atom.getSymbol() + " has an unfulfilled valency";
                errors.add(new ValidationError(atom, error));
            }
        } catch (Exception exception) {
            System.err.println("Error while performing atom valency validation: " + exception.toString());
        }
        return errors;
    }
}
