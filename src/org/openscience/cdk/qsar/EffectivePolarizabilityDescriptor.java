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
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.charges.Polarizability;
import java.util.Map;
import java.util.Hashtable;
import java.util.ArrayList;

/**
 *  Effective polarizability of an heavy atom and its protons
 * 
 *
 *@author         mfe4
 *@cdk.created    2004-11-03
 * @cdk.module qsar
 */
public class EffectivePolarizabilityDescriptor implements Descriptor {

	private int atomPosition = 0;


	/**
	 *  Constructor for the EffectivePolarizabilityDescriptor object
	 */
	public EffectivePolarizabilityDescriptor() { }


	/**
	 *  Gets the specification attribute of the EffectivePolarizabilityDescriptor
	 *  object
	 *
	 *@return    The specification value
	 */
	public Map getSpecification() {
		Hashtable specs = new Hashtable();
		specs.put("Specification-Reference", "http://qsar.sourceforge.net/dicts/qsar-descriptors:effectivePolarizability");
		specs.put("Implementation-Title", this.getClass().getName());
		specs.put("Implementation-Identifier", "$Id$");
		// added by CVS
		specs.put("Implementation-Vendor", "The Chemistry Development Kit");
		return specs;
	}


	/**
	 *  Sets the parameters attribute of the EffectivePolarizabilityDescriptor
	 *  object
	 *
	 *@param  params            The parameter is the atom position
	 *@exception  CDKException  Possible Exceptions
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("EffectivePolarizabilityDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The parameter must be of type Integer");
		}
		atomPosition = ((Integer) params[0]).intValue();
	}


	/**
	 *  Gets the parameters attribute of the EffectivePolarizabilityDescriptor
	 *  object
	 *
	 *@return    an arrayList with the effective polarizability of an heavy atom and its protons
	 */
	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = new Integer(atomPosition);
		return params;
	}


	/**
	 *  The method returns effective polarizabilities assigned to an heavy atom and its
	 *  protons by Polarizability class.
	 *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
	 *
	 *@param  ac                AtomContainer
	 *@return                   an array of doubles with polarizabilities of [heavy,
	 *      proton_1 ... proton_n]
	 *@exception  CDKException  Possible Exceptions
	 */
	public Object calculate(AtomContainer ac) throws CDKException {
		Molecule mol = new Molecule(ac);
		Polarizability pol = new Polarizability();
		
		Atom target = mol.getAtomAt(atomPosition);
		Atom[] neighboors = mol.getConnectedAtoms(target);
		ArrayList effectivePolarizability = new ArrayList(4);
		effectivePolarizability.add(new Double(pol.calculateGHEffectiveAtomPolarizability(mol, target, 1000)));
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("H")) {
				effectivePolarizability.add(new Double(pol.calculateGHEffectiveAtomPolarizability(mol, neighboors[i], 1000)));
			}
		}
		return new ArrayList(effectivePolarizability);
	}


	/**
	 *  Gets the parameterNames attribute of the ProtonTotalPartialChargeDescriptor
	 *  object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "The position of the atom whose protons calculate total partial charge";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the ProtonTotalPartialChargeDescriptor
	 *  object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[1];
		paramTypes[0] = new Integer(1);
		return paramTypes;
	}
}

