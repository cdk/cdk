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
 * @author   Egon Willighagen
 * @created  2003-08-10
 */ 
public class IsotopeValidator {

    public static Vector validate(Isotope isotope) {
        Vector errors = new Vector();
        try {
            IsotopeFactory isotopeFac = IsotopeFactory.getInstance();
            Isotope[] isotopes = isotopeFac.getIsotopes(isotope.getSymbol());
            if (isotope.getAtomicMass() != 0) {
                boolean foundKnownIsotope = false;
                for (int i=0; i<isotopes.length; i++) {
                    if (isotopes[i].getAtomicMass() == isotope.getAtomicMass()) {
                        foundKnownIsotope = true;
                    }
                }
                if (!foundKnownIsotope) {
                    errors.add(
                        new ValidationError(isotope, 
                            "Isotope with this mass number is not known for this element."
                        )
                    );
                }
            } else {
                // isotopic number is not set
            }
        } catch (Exception exception) {
            // too bad...
        }
        return errors;
    }
}
