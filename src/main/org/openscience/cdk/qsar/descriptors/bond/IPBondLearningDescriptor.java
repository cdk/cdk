/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.bond;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.AbstractBondDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.IonizationPotentialTool;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  
 *  This class returns the ionization potential of a Bond. It is
 *  based on a function which is extracted from Weka(J48) from 
 *  experimental values (NIST data). 
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
 * @author      Miguel Rojas
 * @cdk.created 2006-05-26
 * @cdk.module  qsarbond
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:ionizationPotential
 * @cdk.bug     1860497
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.bond.IPBondLearningDescriptorTest")
public class IPBondLearningDescriptor extends AbstractBondDescriptor {
	private static final String[] descriptorNames = {"ipBondLearning"};
	
	/**
	 *  Constructor for the IPBondLearningDescriptor object
	 */
	public IPBondLearningDescriptor() {
	}
	/**
	 *  Gets the specification attribute of the IPBondLearningDescriptor object
	 *
	 *@return    The specification value
	 */
	@TestMethod(value="testGetSpecification")
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ionizationPotential",
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
     *  Gets the parameters attribute of the IPBondLearningDescriptor object.
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
        return descriptorNames;
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new DoubleResult(Double.NaN), descriptorNames, e);
    }

    /**
	 *  This method calculates the ionization potential of a bond.
	 *
	 *@param  atomContainer         Parameter is the IAtomContainer.
	 *@return                   The ionization potential
	 */
    @TestMethod(value="testCalculate_IBond_IAtomContainer")
	public DescriptorValue calculate(IBond bond, IAtomContainer atomContainer) {
		double value = 0;

        if (!isCachedAtomContainer(atomContainer)) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
                LonePairElectronChecker lpcheck = new LonePairElectronChecker();
                lpcheck.saturate(atomContainer);
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }

        }
        if (!bond.getOrder().equals(IBond.Order.SINGLE))
            try {
                value = IonizationPotentialTool.predictIP(atomContainer, bond);
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new DoubleResult(value),descriptorNames);
	}

	
	 /**
     * Gets the parameterNames attribute of the IPBondLearningDescriptor object.
     *
     * @return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        return new String[0];
    }


    /**
     * Gets the parameterType attribute of the IPBondLearningDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        return null;
    }
}

