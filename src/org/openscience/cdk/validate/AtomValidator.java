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
import org.openscience.cdk.tools.IsotopeFactory;
import java.util.Vector;

/**
 * Tool to validate the chemical semantics for an Atom. Keep in mind
 * that Atom's in Molecule's can also give rise to errors for the Atom.
 * Those errors are checked in the MoleculeValidator.
 *
 * @author   Egon Willighagen
 * @created  2003-03-28
 *
 * @see      org.openscience.cdk.Atom
 * @keyword atom, chemical validation
 */ 
public class AtomValidator {

    public static Vector validate(Atom atom) {
        Vector errors = new Vector();
        errors.addAll(validateCharge(atom));
        errors.addAll(validatePseudoAtom(atom));
        return errors;
    }
    
    private static Vector validateCharge(Atom atom) {
        Vector errors = new Vector();
        if (atom.getSymbol().equals("O") || atom.getSymbol().equals("N") ||
            atom.getSymbol().equals("C") || atom.getSymbol().equals("H")) {
            if (atom.getFormalCharge() < -1) {
                errors.add(new ValidationWarning(atom, "Atom has an unlikely large negative charge"));
            }
            if (atom.getFormalCharge() > 1) {
                errors.add(new ValidationWarning(atom, "Atom has an unlikely large positive charge"));
            }
        }
        return errors;
    }

    private static Vector validatePseudoAtom(Atom atom) {
        Vector errors = new Vector();
        if (atom instanceof PseudoAtom) {
            // that's fine
        } else {
            // check wether atom is really an element
            try {
                IsotopeFactory isotopeFactory = IsotopeFactory.getInstance();
                Atom copy = isotopeFactory.configure(atom);
            } catch (NullPointerException exception) {
                errors.add(
                  new CDKError(atom, "Non-element atom must be of class PseudoAtom.")
                );
            } catch (Exception exception) {
                // well... don't throw an error then
            }
        }
        return errors;
    }
}
