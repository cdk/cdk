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
import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import java.util.Map;
import java.util.Hashtable;

/**
 *  Descriptor based on the weight of atoms of a certain element type. If no
 *  element is specified, the returned value is the Molecular Weight. molecular
 *  weight.
 *
 *@author     mfe4
 *@created    2004-11-13
 * @cdk.module qsar
 */
public class WeightDescriptor implements Descriptor {

	private String elementName = null;


	/**
	 *  Constructor for the AtomCountDescriptor object
	 */
	public WeightDescriptor() { }

	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:weight",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
    };

	/**
	 *  Sets the parameters attribute of the AtomCountDescriptor object
	 *
	 *@param  params            The new parameters value
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("weight only expects one parameter");
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

	public Object calculate(AtomContainer container) {
		double weight = 0;
		Atom[] atoms = container.getAtoms();
		if (elementName == "") {
			for (int i = 0; i < atoms.length; i++) {
				weight += container.getAtomAt(i).getExactMass();
				weight += (container.getAtomAt(i).getHydrogenCount() * 1.00782504);
			}
			
		} 
		else if (elementName == "H") {
			for (int i = 0; i < atoms.length; i++) {
				if (container.getAtomAt(i).getSymbol().equals(elementName)) {
					weight += container.getAtomAt(i).getExactMass();
				}
				else {
					weight += (container.getAtomAt(i).getHydrogenCount() * 1.00782504);
				}
			}
		}
		else {
			for (int i = 0; i < atoms.length; i++) {
				if (container.getAtomAt(i).getSymbol().equals(elementName)) {
					weight += container.getAtomAt(i).getExactMass();
				}
			}
		}
		return new Double(weight);
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

