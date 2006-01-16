/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.interfaces.AtomType;
import org.openscience.cdk.atomtype.HybridizationStateATMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 *  This class returns the hybridization of an atom.
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>targetPosition</td>
 *     <td>0</td>
 *     <td>The position of the target atom</td>
 *   </tr>
 * </table>
 *
 *@author         mfe4
 *@cdk.created    2004-11-13
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:atomHybridization
 */
public class AtomHybridizationDescriptor implements IDescriptor {

	private int targetPosition = 0;
	AtomTypeManipulator atman = null;
	HybridizationStateATMatcher atm = null;
	org.openscience.cdk.interfaces.Atom atom = null;
	AtomType matched = null;
	
	/**
	 *  Constructor for the AtomHybridizationDescriptor object
	 */
	public AtomHybridizationDescriptor() {}


	/**
	 *  Gets the specification attribute of the AtomHybridizationDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#atomHybridization",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the AtomHybridizationDescriptor object
	 *
	 *@param  params            The parameter is the atom position
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("AtomHybridizationDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The parameter must be of type Integer");
		}
		targetPosition = ((Integer) params[0]).intValue();
	}


	/**
	 *  Gets the parameters attribute of the AtomHybridizationDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		Object[] params = new Object[1];
		params[0] = new Integer(targetPosition);
		return params;
	}


	/**
	 *  This method calculates the hybridization of an atom.
	 *
	 *@param  container         Parameter is the atom container.
	 *@return                   The hybridization
	 *@exception  CDKException  Description of the Exception
	 */

	public DescriptorValue calculate(AtomContainer container) throws CDKException {
		atom = container.getAtomAt(targetPosition);
		
		atm = new HybridizationStateATMatcher();
		matched = atm.findMatchingAtomType(container, atom);
                if (matched == null) {
                    throw new CDKException("The matched atom type was null");
                }
		AtomTypeManipulator.configure(atom, matched);

		int atomHybridization = atom.getHybridization();
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(atomHybridization));
	}


	/**
	 *  Gets the parameterNames attribute of the AtomHybridizationDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "targetPosition";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the AtomHybridizationDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new Integer(1);
	}
}

