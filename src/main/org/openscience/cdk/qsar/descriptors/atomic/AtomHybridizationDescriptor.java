/* $Revision$ $Author$ $Date$
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
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
 * @cdk.module     qsaratomic
 * @cdk.svnrev  $Revision$
 * @cdk.set        qsar-descriptors
 * @cdk.dictref    qsar-descriptors:atomHybridization
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.AtomHybridizationDescriptorTest")
public class AtomHybridizationDescriptor implements IAtomicDescriptor {

	AtomTypeManipulator atman = null;
	IAtom atom = null;
	IAtomType matched = null;
	
	/**
	 *  Gets the specification attribute of the AtomHybridizationDescriptor object
	 *
	 *@return    The specification value
	 */
	@TestMethod(value="testGetSpecification")
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
    @TestMethod(value="testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
    }


    /**
     *  Gets the parameters attribute of the VdWRadiusDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    @TestMethod(value="testGetParameters")
    public Object[] getParameters() {
        return null;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return new String[]{"aHyb"};
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        return new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(), new IntegerResult((int) Double.NaN), getDescriptorNames(), e);
    }

    /**
	 *  This method calculates the hybridization of an atom.
	 *
	 *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  container         Parameter is the atom container.
	 *@return                   The hybridization
	 */

	@TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer container) {
        try {
            matched = CDKAtomTypeMatcher.getInstance(atom.getBuilder()).findMatchingAtomType(container, atom);
        } catch (CDKException e) {
            return getDummyDescriptorValue(e);
        }
        if (matched == null) {
            int atnum = container.getAtomNumber(atom);
            return getDummyDescriptorValue(new CDKException("The matched atom type was null (atom number "+atnum+") "+atom.getSymbol()));
		}
		Hybridization atomHybridization = matched.getHybridization();
		IntegerResult result = new IntegerResult(atomHybridization == null ? 0 : atomHybridization.ordinal());
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                result, getDescriptorNames());
	}

    /**
     *  Gets the parameterNames attribute of the VdWRadiusDescriptor object.
     *
     *@return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        return new String[0];
    }

    /**
     * Gets the parameterType attribute of the VdWRadiusDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        return null;
    }
}

