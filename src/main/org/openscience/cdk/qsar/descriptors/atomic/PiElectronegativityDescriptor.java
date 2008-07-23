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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.charges.Electronegativity;
import org.openscience.cdk.charges.PiElectronegativity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  Pi electronegativity is given by X = a + bq + c(q*q)
 *
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
 * @author      Miguel Rojas
 * @cdk.created 2006-05-17
 * @cdk.module  qsaratomic
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:piElectronegativity
 * @cdk.bug     1558660
 * @cdk.bug     1701065
 * @see Electronegativity
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.PiElectronegativityDescriptorTest")
public class PiElectronegativityDescriptor implements IAtomicDescriptor {
    
	/**Number of maximum iterations*/
	private int maxIterations = -1;
    /**Number of maximum resonance structures*/
	private int maxResonStruc = -1;
	/** make a lone pair electron checker. Default true*/
	private boolean lpeChecker = true;
	
    private static final String[] descriptorNames = {"elecPiA"};
	private PiElectronegativity electronegativity;

    /**
     *  Constructor for the PiElectronegativityDescriptor object
     */
    public PiElectronegativityDescriptor() {
    	electronegativity = new PiElectronegativity();
    }

    /**
     *  Gets the specification attribute of the PiElectronegativityDescriptor
     *  object
     *
     *@return    The specification value
     */
    @TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#piElectronegativity",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the PiElectronegativityDescriptor
     *  object
     *
     *@param  params            The number of maximum iterations. 1= maxIterations. 2= maxResonStruc.
     *@exception  CDKException  Description of the Exception
     */
    @TestMethod(value="testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
    	if (params.length > 3) 
            throw new CDKException("PartialPiChargeDescriptor only expects three parameter");
        
        if (!(params[0] instanceof Integer) )
                throw new CDKException("The parameter must be of type Integer");
	        maxIterations = (Integer) params[0];
	        
	    if(params.length > 1 && params[1] != null){
        	if (!(params[1] instanceof Boolean) )
                throw new CDKException("The parameter must be of type Boolean");
        	lpeChecker = (Boolean) params[1];
        }
	    
	    if(params.length > 2 && params[2] != null){
        	if (!(params[2] instanceof Integer) )
                throw new CDKException("The parameter must be of type Integer");
        	maxResonStruc = (Integer) params[2];
        }
    }


    /**
     *  Gets the parameters attribute of the PiElectronegativityDescriptor
     *  object
     *
     *@return    The parameters value
     */
    @TestMethod(value="testGetParameters")
    public Object[] getParameters() {
    	 // return the parameters as used for the descriptor calculation
        Object[] params = new Object[3];
        params[0] = maxIterations;
        params[1] = lpeChecker;
        params[2] = maxResonStruc;
        return params;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return descriptorNames;
    }


    /**
     *  The method calculates the pi electronegativity of a given atom
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  atomContainer                AtomContainer
     *@return                   return the pi electronegativity
     */
    @TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer atomContainer) {
        IAtomContainer clone;
        IAtom localAtom;
        try {
            clone = (IAtomContainer) atomContainer.clone();
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(clone);
            if (lpeChecker) {
                LonePairElectronChecker lpcheck = new LonePairElectronChecker();
                lpcheck.saturate(atomContainer);
            }
            localAtom = clone.getAtom(atomContainer.getAtomNumber(atom));
        } catch (CloneNotSupportedException e) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new DoubleResult(Double.NaN), descriptorNames, null);
        } catch (CDKException e) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    new DoubleResult(Double.NaN), descriptorNames, null);
        }


        if (maxIterations != -1 && maxIterations != 0) electronegativity.setMaxIterations(maxIterations);
        if (maxResonStruc != -1 && maxResonStruc != 0) electronegativity.setMaxResonStruc(maxResonStruc);

        double result = electronegativity.calculatePiElectronegativity(clone, localAtom);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new DoubleResult(result), descriptorNames);
    }


    /**
     *  Gets the parameterNames attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
    	String[] params = new String[3];
        params[0] = "maxIterations";
        params[1] = "lpeChecker";
        params[2] = "maxResonStruc";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
    	if ("maxIterations".equals(name)) return Integer.MAX_VALUE;
    	if ("lpeChecker".equals(name)) return Boolean.TRUE;
    	if ("maxResonStruc".equals(name)) return Integer.MAX_VALUE;
        return null;
    }
}

