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
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
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
public class ReactionValidator {

    public static Vector validate(Reaction reaction) {
        Vector errors = new Vector();
        AtomContainer container1 = new AtomContainer();
        Molecule[] reactants = reaction.getReactants();
        for (int i=0; i<reactants.length; i++) {
            container1.add(reactants[i]);
        }
        AtomContainer container2 = new AtomContainer();
        Molecule[] products = reaction.getProducts();
        for (int i=0; i<products.length; i++) {
            container2.add(products[i]);
        }
        errors.addAll(validateAtomCountConservation(reaction, container1, container2));
        errors.addAll(validateChargeConservation(reaction, container1, container2));
        return errors;
    }
    
    private static Vector validateAtomCountConservation(Reaction reaction,
                                                        AtomContainer reactants,
                                                        AtomContainer products) {
        Vector errors = new Vector();
        if (reactants.getAtomCount() < products.getAtomCount()) {
            errors.add(new ValidationError(reaction, 
                "Atom count mismatch for reaction: the products have more atoms than the reactants."));
        } else if (reactants.getAtomCount() > products.getAtomCount()) {
            errors.add(new ValidationError(reaction, 
                "Atom count mismatch for reaction: the reactants have more atoms than the products."));
        }
        return errors;
    }

    private static Vector validateChargeConservation(Reaction reaction,
                                                     AtomContainer reactants,
                                                     AtomContainer products) {
        Vector errors = new Vector();
        Atom[] atoms1 = reactants.getAtoms();
        int totalCharge1 = 0;
        for (int i=0;i<atoms1.length; i++) {
            totalCharge1 =+ atoms1[i].getFormalCharge();
        }
        Atom[] atoms2 = products.getAtoms();
        int totalCharge2 = 0;
        for (int i=0;i<atoms2.length; i++) {
            totalCharge2 =+ atoms2[i].getFormalCharge();
        }
        if (totalCharge1 != totalCharge2) {
            errors.add(new ValidationError(reaction, 
                "Total formal charge is not preserved during the reaction"));
        }
        return errors;
    }
}
