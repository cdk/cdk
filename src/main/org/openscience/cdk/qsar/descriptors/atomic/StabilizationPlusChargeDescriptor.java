/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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
import org.openscience.cdk.charges.StabilizationCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  The stabilization of the positive charge 
 *  (e.g.) obtained in the polar breaking of a bond is calculated from the sigma- and 
 *  lone pair-electronegativity values of the atoms that are in conjugation to the atoms 
 *  obtaining the charges. The method is based following {@cdk.cite Saller85}.
 *  The value is calculated looking for resonance structures which can stabilize the charge.
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>maxIterations</td>
 *     <td>0</td>
 *     <td>Number of maximum iterations</td>
 *   </tr>
 * </table>
 *
 * @author         Miguel Rojas Cherto
 * @cdk.created    2008-104-31
 * @cdk.module     qsaratomic
 * @cdk.set        qsar-descriptors
 * @see StabilizationCharges
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.StabilizationPlusChargeDescriptorTest")
public class StabilizationPlusChargeDescriptor implements IAtomicDescriptor {
	
    private static final String[] descriptorNames = {"stabilPlusC"};
    
	private StabilizationCharges stabil;

    /**
     *  Constructor for the StabilizationPlusChargeDescriptor object
     */
    public StabilizationPlusChargeDescriptor() {
    	stabil = new StabilizationCharges();
  }


    /**
     *  Gets the specification attribute of the StabilizationPlusChargeDescriptor
     *  object
     *
     *@return    The specification value
     */
    @TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#stabilizationPlusCharge",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the StabilizationPlusChargeDescriptor
     *  object
     *
     *@param  params            1: max iterations (optional, defaults to 20)
     *@exception  CDKException  Description of the Exception
     */
    @TestMethod(value="testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        
    }


    /**
     *  Gets the parameters attribute of the StabilizationPlusChargeDescriptor
     *  object
     *
     *@return    The parameters value
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
     *  The method calculates the stabilization of charge of a given atom
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  container         AtomContainer
     *@return                   return the stabilization value
     */
    @TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer container) {

        IAtomContainer clone;
        IAtom localAtom;
        try {
            clone = (IAtomContainer) container.clone();
            localAtom = clone.getAtom(container.getAtomNumber(atom));
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(clone);
        } catch (CDKException e) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new DoubleResult(Double.NaN), descriptorNames, e);
        } catch (CloneNotSupportedException e) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new DoubleResult(Double.NaN), descriptorNames, e);
        }

        double result = stabil.calculatePositive(clone, localAtom);
	    
	    return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new DoubleResult(result),descriptorNames);
    }


    /**
     *  Gets the parameterNames attribute of the StabilizationPlusChargeDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        return null;
    }


    /**
     *  Gets the parameterType attribute of the StabilizationPlusChargeDescriptor
     *  object
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        return null; 
    }
}

