/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.qsar;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomException;


public class ZagrebIndexDescriptor implements Descriptor {
	
	
	public ZagrebIndexDescriptor() { }


	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 0) {
			throw new CDKException("ZagrebIndexDescriptor expects not parameters");
		}
	}

	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		return params;
	}

	public Object calculate(AtomContainer ac) throws CDKException{
		double zagreb = 0;
		Atom[] atoms = ac.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			int atomDegree = 0;
			Atom[] neighboors = ac.getConnectedAtoms(atoms[i]);
			for (int a = 0; a < neighboors.length; a++) {
				if (!neighboors[a].getSymbol().equals("H")) {
					atomDegree += 1;
				}
			}
			zagreb += (atomDegree * atomDegree);
		}
		return new Double(zagreb);
	}
	
	
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "...";
		return params;
	}


	
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[1];
		paramTypes[0] = new Double(0);
		return paramTypes;
	}
}

