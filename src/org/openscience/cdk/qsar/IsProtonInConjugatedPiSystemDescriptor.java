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
import org.openscience.cdk.AtomContainer;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.graph.invariant.ConjugatedPiSystemsDetector;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.SetOfAtomContainers;
import java.util.Map;
import java.util.Hashtable;
import org.openscience.cdk.Molecule;

/**
 *  Description of the Class
 *
 *@author     mfe4
 *@cdk.created    2004-11-03
 * @cdk.module qsar
 */
public class IsProtonInConjugatedPiSystemDescriptor implements Descriptor {

	private int atomPosition = 0;
	private boolean checkAromaticity = false;


	/**
	 *  Constructor for the IsProtonInConjugatedPiSystemDescriptor object
	 */
	public IsProtonInConjugatedPiSystemDescriptor() { }


	/**
	 *  Gets the specification attribute of the
	 *  IsProtonInConjugatedPiSystemDescriptor object
	 *
	 *@return    The specification value
	 */
	public Map getSpecification() {
		Hashtable specs = new Hashtable();
		specs.put("Specification-Reference", "http://qsar.sourceforge.net/dicts/qsar-descriptors:isProtonInConjugatedPiSystem");
		specs.put("Implementation-Title", this.getClass().getName());
		specs.put("Implementation-Identifier", "$Id$");
		// added by CVS
		specs.put("Implementation-Vendor", "The Chemistry Development Kit");
		return specs;
	}


	/**
	 *  Sets the parameters attribute of the IsProtonInConjugatedPiSystemDescriptor
	 *  object
	 *
	 *@param  params            Parameters are an integer (heavy atom position) and a boolean (true if is needed a checkAromaticity)
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 2) {
			throw new CDKException("IsProtonInConjugatedPiSystemDescriptor only expects two parameters");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The first parameter must be of type Integer");
		}
		if (!(params[1] instanceof Boolean)) {
			throw new CDKException("The second parameter must be of type Boolean");
		}
		atomPosition = ((Integer) params[0]).intValue();
		checkAromaticity = ((Boolean) params[1]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the IsProtonInConjugatedPiSystemDescriptor
	 *  object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[2];
		params[0] = new Integer(atomPosition);
		params[1] = new Boolean(checkAromaticity);
		return params;
	}


	/**
	 *  The method is a proton descriptor that evaluate if protons of a given atom are bonded to a conjugated system
	 *
	 *@param  ac                AtomContainer
	 *@return                   true if the proton is bonded to an heavy atom involved in a conjugated system
	 *@exception  CDKException  Possible Exceptions
	 */
	public Object calculate(AtomContainer ac) throws CDKException {
		HydrogenAdder hAdder = new HydrogenAdder();
		try {
			boolean isProtonInPiSystem = false;
			int counter = 0;
			Molecule mol = new Molecule(ac);
			if (checkAromaticity) {
				HueckelAromaticityDetector.detectAromaticity(mol);
			}
			SetOfAtomContainers acSet = ConjugatedPiSystemsDetector.detect(mol);
			if (mol.contains(ac.getAtomAt(atomPosition))) {
				Atom[] neighboors = mol.getConnectedAtoms(mol.getAtomAt(atomPosition));
				for (int i = 0; i < neighboors.length; i++) {
					if (neighboors[i].getSymbol().equals("H")) {
						counter += 1;
					} else if (mol.getAtomAt(atomPosition).getHydrogenCount() > 0) {
						counter += mol.getAtomAt(atomPosition).getHydrogenCount();
					} else {
						counter += 0;
					}
				}
			}

			if (counter > 0) {
				isProtonInPiSystem = true;
			}
			return new Boolean(isProtonInPiSystem);
		} catch (Exception ex1) {
			throw new CDKException("Problems with HydrogenAdder due to " + ex1.toString());
		}
	}


	/**
	 *  Gets the parameterNames attribute of the
	 *  IsProtonInConjugatedPiSystemDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[2];
		params[0] = "The position of the atom bonded to this proton";
		params[1] = "False if the aromaticity has been already checked";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the
	 *  IsProtonInConjugatedPiSystemDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[2];
		paramTypes[0] = new Integer(1);
		paramTypes[1] = new Boolean(true);
		return paramTypes;
	}
}

