/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-04-19 15:20:48 +0200 (Wed, 19 Apr 2006) $
 * $Revision: 6012 $
 * 
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.EnzymeResidueLocator;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.io.MACiEReader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * Validates the 3D geometry of the model.
 *
 * @cdk.module  experimental
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
    		double distance = subject.getAtomAt(0).getPoint3d().distance(
    			subject.getAtomAt(2).getPoint3d()
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
