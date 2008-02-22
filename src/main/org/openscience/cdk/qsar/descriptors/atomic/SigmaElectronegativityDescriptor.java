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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  Sigma electronegativity is given by X = a + bq + c(q*q)
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
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:sigmaElectronegativity
 * @cdk.bug     1860497
 */
@TestClass(value="org.openscience.cdk.test.qsar.descriptors.atomic.SigmaElectronegativityDescriptorTest")
public class SigmaElectronegativityDescriptor implements IAtomicDescriptor {
	/**Number of maximum iterations*/
    private int maxIterations = 0;
    private GasteigerMarsiliPartialCharges peoe = null;
    private IAtomContainer oldac = null;
    private double[] gasteigerFactors = null;


    /**
     *  Constructor for the SigmaElectronegativityDescriptor object
     */
    public SigmaElectronegativityDescriptor() {
    peoe = new GasteigerMarsiliPartialCharges();
  }


    /**
     *  Gets the specification attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     *@return    The specification value
     */
    @TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#sigmaElectronegativity",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     *@param  params            1: max iterations (optional, defaults to 20)
     *@exception  CDKException  Description of the Exception
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
     *  Gets the parameters attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     *@return    The parameters value
     */
    @TestMethod(value="testGetParameters")
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = maxIterations;
        return params;
    }


    /**
     *  The method calculates the sigma electronegativity of a given atom
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
     *@return                   return the sigma electronegativity
     *@exception  CDKException  Possible Exceptions
     */
    @TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer ac) throws CDKException {
        double sigmaElectronegativity = 0;
        int atomPosition =ac.getAtomNumber(atom);
        try {
	      //long starttime=System.currentTimeMillis();
	      if(oldac!=ac){
	    	  if(maxIterations != 0)
	    		  peoe.setMaxGasteigerIters(maxIterations);
	    	  peoe.assignGasteigerMarsiliSigmaPartialCharges(ac, true);
	    	  gasteigerFactors = peoe.assignGasteigerSigmaMarsiliFactors(ac);
	    	  oldac=ac;
	      }
	      int stepSize = peoe.getStepSize();
	            int start = (stepSize * (atomPosition) + atomPosition);
	            sigmaElectronegativity = ((gasteigerFactors[start]) + (ac.getAtom(atomPosition).getCharge() * gasteigerFactors[start + 1]) + (gasteigerFactors[start + 2] * ((ac.getAtom(atomPosition).getCharge() * ac.getAtom(atomPosition).getCharge()))));
	      return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(sigmaElectronegativity));
        } catch (Exception ex1) {
        	ex1.printStackTrace();
            throw new CDKException("Problems with GasteigerMarsiliPartialCharges due to " + ex1.toString(), ex1);
        }
    }


    /**
     *  Gets the parameterNames attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "maxIterations";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        return 0; 
    }
}

