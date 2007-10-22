/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.atomtype.HybridizationStateATMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
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
 *     <td></td>
 *     <td></td>
 *     <td>no parameters</td>
 *   </tr>
 * </table>
 *
 * @author         mfe4
 * @cdk.created    2004-11-13
 * @cdk.module     qsar
 * @cdk.svnrev  $Revision$
 * @cdk.set        qsar-descriptors
 * @cdk.dictref    qsar-descriptors:atomHybridization
 * @cdk.bug        1558660
 */
public class AtomHybridizationDescriptor implements IAtomicDescriptor {

	AtomTypeManipulator atman = null;
	HybridizationStateATMatcher atm = null;
	IAtom atom = null;
	IAtomType matched = null;
	
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
     * This descriptor does have any parameter.
     */
    public void setParameters(Object[] params) throws CDKException {
    }


    /**
     *  Gets the parameters attribute of the VdWRadiusDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        return null;
    }

	/**
	 *  This method calculates the hybridization of an atom.
	 *
	 *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  container         Parameter is the atom container.
	 *@return                   The hybridization
	 *@exception  CDKException  Description of the Exception
	 */

	public DescriptorValue calculate(IAtom atom, IAtomContainer container) throws CDKException {
		atm = new HybridizationStateATMatcher();
		matched = atm.findMatchingAtomType(container, atom);
		if (matched == null) {
            int atnum = container.getAtomNumber(atom);
            throw new CDKException("The matched atom type was null (atom number "+atnum+") "+atom.getSymbol());
		}
		AtomTypeManipulator.configure(atom, matched);

		int atomHybridization = atom.getHybridization();
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(atomHybridization));
	}

    /**
     *  Gets the parameterNames attribute of the VdWRadiusDescriptor object.
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        return new String[0];
    }

    /**
     * Gets the parameterType attribute of the VdWRadiusDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return null;
    }
}

