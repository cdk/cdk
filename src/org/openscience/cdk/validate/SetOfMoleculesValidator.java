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
import java.util.Vector;

/**
 * Tool to validate the chemical semantics for an SetOfMolecules.
 *
 * @author   Egon Willighagen <egonw@sci.kun.nl>
 * @created  2003-07-14
 *
 * @see      org.openscience.cdk.SetOfMolecules
 */ 
public class SetOfMoleculesValidator {

    public static Vector validate(SetOfMolecules moleculeSet) {
        Vector errors = new Vector();
        Molecule[] molecules = moleculeSet.getMolecules();
        for (int i=0; i < molecules.length; i++) {
            errors.addAll(MoleculeValidator.validate(molecules[i]));
        }
        return errors;
    }
    
}
