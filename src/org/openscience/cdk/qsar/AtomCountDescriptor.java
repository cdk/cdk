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

/**
 *  Descriptor based on the number of atoms of a certain element type. It is
 *  possible to use the wild card symbol * as element type to get the count of
 *  all atoms.
 *
 *@author     mfe4
 *@created    2004-11-13
 */
public class AtomCountDescriptor implements Descriptor {

	private String elementName = null;


	/**
	 *  Constructor for the AtomCountDescriptor object
	 */
	public AtomCountDescriptor() { }


	/**
	 *  Sets the parameters attribute of the AtomCountDescriptor object
	 *
	 *@param  params            The new parameters value
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("AtomCount only expects one parameter");
		}
		if (!(params[0] instanceof String)) {
			throw new CDKException("The parameter must be of type String");
		}
		// ok, all should be fine
		elementName = (String) params[0];
	}


	/**
	 *  Gets the parameters attribute of the AtomCountDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = elementName;
		return params;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  container  Parameter is the atom container.
	 *@return            Number of atoms of a certain type is returned.
	 */
	 
	 // it could be interesting to accept as elementName a SMARTS atom, to get the frequency of this atom
	 // this could be useful for other descriptors like polar surface area...
	public Object calculate(AtomContainer container) {
		int atomCount = 0;
		Atom[] atoms = container.getAtoms();
		if (elementName == "*") {
			atomcount += container.getHydrogenCount();
			atomcount += atoms.length;
		} else {
			for (int i = 0; i < atoms.length; i++) {
				if (container.getAtomAt(i).getSymbol().equals(elementName)) {
					atomCount += 1;
				}
			}
			if (elementName == "H") {
				atomcount += container.getHydrogenCount();
			}
			return new Integer(atomCount);
		}
	}


	/**
	 *  Gets the parameterNames attribute of the AtomCountDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "Element Symbol";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the AtomCountDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[1];
		paramTypes[0] = new String();
		return paramTypes;
	}
}

