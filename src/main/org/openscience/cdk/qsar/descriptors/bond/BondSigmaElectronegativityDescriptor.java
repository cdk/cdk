/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.qsar.descriptors.bond;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.charges.Electronegativity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  The calculation of bond-Polarizability is calculated determining the
 *  difference the Sigma electronegativity on atoms A and B of a bond.
 *  <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>bondPosition</td>
 *     <td>0</td>
 *     <td>The position of the target bond</td>
 *   </tr>
 * </table>
 *
 *
 * @author      Miguel Rojas
 * @cdk.created 2006-05-08
 * @cdk.module  qsarbond
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:bondSigmaElectronegativity
 * @cdk.bug     1860497
 * @see Electronegativity
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.bond.BondSigmaElectronegativityDescriptorTest")
public class BondSigmaElectronegativityDescriptor implements IBondDescriptor {

	/**Number of maximum iterations*/
    private int maxIterations = 6;
    
	private Electronegativity electronegativity;

    private static final String[] descriptorNames = {"elecSigB"};
    /**
     *  Constructor for the BondSigmaElectronegativityDescriptor object
     */
    public BondSigmaElectronegativityDescriptor() { 
    	electronegativity = new Electronegativity();
    }


    /**
     *  Gets the specification attribute of the BondSigmaElectronegativityDescriptor
     *  object
     *
     *@return    The specification value
     */
    @TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#bondSigmaElectronegativity",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }

    /**
     * This descriptor does have any parameter.
     */
    @TestMethod(value="testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("SigmaElectronegativityDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Integer) ){
            throw new CDKException("The parameter must be of type Integer");
        }
        if(params.length==0)
        	return;
        maxIterations = (Integer) params[0];
    }

    /**
     *  Gets the parameters attribute of the BondSigmaElectronegativityDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    @TestMethod(value="testGetParameters")
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = maxIterations;
        return params;
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
     *  The method calculates the sigma electronegativity of a given bond
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  atomContainer                AtomContainer
     *@return                   return the sigma electronegativity
     */
    @TestMethod(value="testCalculate_IBond_IAtomContainer,testBondSigmaElectronegativityDescriptor,testBondSigmaElectronegativityDescriptor_Methyl_chloride")
    public DescriptorValue calculate(IBond aBond, IAtomContainer atomContainer) {
        IAtomContainer ac;
        IBond bond;

        try {
            ac = (IAtomContainer) atomContainer.clone();
            bond = ac.getBond(atomContainer.getBondNumber(aBond));
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
        } catch (CDKException e) {
            return getDummyDescriptorValue(e);
        } catch (CloneNotSupportedException e) {
            return getDummyDescriptorValue(e);
        }

        if(maxIterations != -1 && maxIterations != 0) electronegativity.setMaxIterations(maxIterations);
	    
	    double electroAtom1 = electronegativity.calculateSigmaElectronegativity(ac, bond.getAtom(0));
	    double electroAtom2 = electronegativity.calculateSigmaElectronegativity(ac, bond.getAtom(1));

	    return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new DoubleResult(Math.abs(electroAtom1 - electroAtom2)),descriptorNames);
	    
    }
	 /**
     * Gets the parameterNames attribute of the BondSigmaElectronegativityDescriptor object.
     *
     * @return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
    	String[] params = new String[1];
        params[0] = "maxIterations";
        return params;
    }


    /**
     * Gets the parameterType attribute of the BondSigmaElectronegativityDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        return 0; 
    }
}

