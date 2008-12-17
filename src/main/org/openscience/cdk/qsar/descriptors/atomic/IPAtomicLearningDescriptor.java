/* $Revision: 10995 $ $Author: miguelrojasch $ $Date: 2008-05-14 16:38:21 +0200 (Wed, 14 May 2008) $
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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.IonizationPotentialTool;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  This class returns the ionization potential of an atom containing lone 
 *  pair electrons. It is 
 *  based on a decision tree which is extracted from Weka(J48) from 
 *  experimental values. Up to now is only possible predict for 
 *  Cl,Br,I,N,P,O,S Atoms and they are not belong to
 *  conjugated system or not adjacent to an double bond.
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
 * @author       Miguel Rojas
 * @cdk.created  2006-05-26
 * @cdk.module   qsarionpot
 * @cdk.svnrev   $Revision: 10995 $
 * @cdk.set      qsar-descriptors
 * @cdk.dictref  qsar-descriptors:ionizationPotential
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.IPAtomicLearningDescriptorTest")
public class IPAtomicLearningDescriptor extends AbstractAtomicDescriptor {
	    
    private static final String[] descriptorNames = {"ipAtomicLearning"};
	
	/**
	 *  Constructor for the IPAtomicLearningDescriptor object.
	 */
	public IPAtomicLearningDescriptor() {
	}
	/**
	 *  Gets the specification attribute of the IPAtomicLearningDescriptor object
	 *
	 *@return    The specification value
	 */
	@TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ionizationPotential",
				this.getClass().getName(),
				"$Id: IPAtomicLearningDescriptor.java 10995 2008-05-14 14:38:21Z miguelrojasch $",
				"The Chemistry Development Kit");
	}
    /**
     * This descriptor does have any parameter.
     */
    @TestMethod(value="testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
    }


    /**
     *  Gets the parameters attribute of the IPAtomicLearningDescriptor object.
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

    /**
	 *  This method calculates the ionization potential of an atom.
	 *
	 *@param  atom          The IAtom to ionize.
	 *@param  container         Parameter is the IAtomContainer.
	 *@return                   The ionization potential. Not possible the ionization.
	 */
	@TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer container) {
        double value = 0;
    	// FIXME: for now I'll cache a few modified atomic properties, and restore them at the end of this method
    	String originalAtomtypeName = atom.getAtomTypeName();
    	Integer originalNeighborCount = atom.getFormalNeighbourCount();
    	Integer originalValency = atom.getValency();
        IAtomType.Hybridization originalHybrid = atom.getHybridization();

        if (!isCachedAtomContainer(container)) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);

                LonePairElectronChecker lpcheck = new LonePairElectronChecker();
                lpcheck.saturate(container);
            } catch (CDKException e) {
                return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                        new DoubleResult(Double.NaN), getDescriptorNames(), e);

            }
        }

        try {
            value = IonizationPotentialTool.predictIP(container,atom);
        } catch (CDKException e) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                        new DoubleResult(Double.NaN), getDescriptorNames(), e);
        }
    	// restore original props
    	atom.setAtomTypeName(originalAtomtypeName);
    	atom.setFormalNeighbourCount(originalNeighborCount);
    	atom.setValency(originalValency);
        atom.setHybridization(originalHybrid);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new DoubleResult(value), getDescriptorNames());
		
	}
	/**
     * Gets the parameterNames attribute of the IPAtomicLearningDescriptor object.
     *
     * @return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        return new String[0];
    }

    /**
     * Gets the parameterType attribute of the IPAtomicLearningDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        return null;
    }
}

