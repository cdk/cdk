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
import org.openscience.cdk.tools.AtomTypeFactory;
import java.util.Vector;

/**
 * Tool to validate the chemical semantics for an Bond. Keep in mind
 * that Bond's in Molecule's can also give rise to errors for the Bond.
 * Those errors are checked in the MoleculeValidator.
 *
 * @author   Egon Willighagen
 * @created  2003-08-10
 *
 * @see      org.openscience.cdk.Bond
 * @keyword  bond, chemical validation
 */ 
public class BondValidator {

    public static Vector validate(Bond bond) {
        Vector errors = new Vector();
        errors.addAll(validateStereoChemistry(bond));
        return errors;
    }
    
    private static Vector validateStereoChemistry(Bond bond) {
        Vector errors = new Vector();
        if (bond.getStereo() != CDKConstants.STEREO_BOND_NONE) {
            errors.add(new ValidationWarning(bond,
              "Defining stereochemistry on bonds is not save. Use atom based stereochemistry instead."
            ));
        }
        return errors;
    }
    
    private static Vector validateMaxBondOrder(Bond bond) {
        Vector errors = new Vector();
        try {
            AtomTypeFactory atf = AtomTypeFactory.getInstance();
            for (int i=0; i<bond.getAtomCount(); i++) {
                Atom atom = bond.getAtomAt(i);
                Atom copy = (Atom)atom.clone();
                atf.configure(copy);
                if (bond.getOrder() > copy.getMaxBondOrder()) {
                    String error ="Bond order exceeds the maximum for Atom " +
                        atom.getSymbol();
                    errors.add(new SeriousValidationError(atom, error));
                }
            }
        } catch (Exception exception) {
            System.err.println("Error while performing atom bos validation: " + exception.toString());
        }
        return errors;
    }
    
}
