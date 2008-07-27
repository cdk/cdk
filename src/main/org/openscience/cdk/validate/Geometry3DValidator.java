/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-04-19 15:20:48 +0200 (Wed, 19 Apr 2006) $
 * $Revision: 6012 $
 * 
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.interfaces.IBond;

/**
 * Validates the 3D geometry of the model.
 *
 * @cdk.module  extra
 * @cdk.svnrev  $Revision: 9162 $
 *
 * @cdk.created 2006-05-11
 */ 
public class Geometry3DValidator extends AbstractValidator {

    public Geometry3DValidator() {}

    // assumes 1 unit in the coordinate system is one angstrom
    public ValidationReport validateBond(IBond subject) {
    	ValidationReport report = new ValidationReport();
    	// only consider two atom bonds
    	if (subject.getAtomCount() == 2) {
    		double distance = subject.getAtom(0).getPoint3d().distance(
    			subject.getAtom(2).getPoint3d()
    		);
    		if (distance > 3.0) { // should really depend on the elements
    			ValidationTest badBondLengthError = new ValidationTest(subject,
                    "Bond length cannot exceed 3 Angstroms.",
                    "A bond length typically is between 0.5 and 3.0 Angstroms."
                );
    			report.addError(badBondLengthError);
    		}
    	}
    	return report;
    }
}
