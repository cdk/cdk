/*
 *  $RCSfile$
 *  $Author: miguelrojasch $
 *  $Date: 2006-05-06 00:21:46 +0200 (Sa, 06 Mai 2006) $
 *  $Revision: 6185 $
 *
 *  Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.charges.GasteigerPEPEPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.ISetOfAtomContainers;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;

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
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:piElectronegativity
 */
public class PiElectronegativityDescriptor implements IAtomicDescriptor {

	/**Number of maximum iterations*/
    private int maxIterations = -1;
    private GasteigerPEPEPartialCharges pepe = null;
	private IAtomicDescriptor  descriptor;


    /**
     *  Constructor for the PiElectronegativityDescriptor object
     */
    public PiElectronegativityDescriptor() {
    	pepe = new GasteigerPEPEPartialCharges();
    	descriptor = new PartialPiChargeDescriptor();
    }


    /**
     *  Gets the specification attribute of the PiElectronegativityDescriptor
     *  object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#piElectronegativity",
            this.getClass().getName(),
            "$Id: PiElectronegativityDescriptor.java 6185 2006-05-05 22:21:46Z miguelrojasch $",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the PiElectronegativityDescriptor
     *  object
     *
     *@param  params            The number of maximum iterations
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("PiElectronegativityDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter 1 must be of type Integer");
        }
        maxIterations = ((Integer) params[0]).intValue();
    }


    /**
     *  Gets the parameters attribute of the PiElectronegativityDescriptor
     *  object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = new Integer(maxIterations);
        return params;
    }


    /**
     *  The method calculates the pi electronegativity of a given atom
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
     *@return                   return the pi electronegativity
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtom atom, IAtomContainer ac) throws CDKException {
        double piElectronegativity = 0.0;
        try {
        	double q = 0.0;
        	if(maxIterations != -1){
        		Integer[] params = new Integer[1];
        		params[0] = new Integer(maxIterations);
        		descriptor.setParameters(params);
        	}
        	q = ((DoubleResult)descriptor.calculate(atom,ac).getValue()).doubleValue();
			
    	  ISetOfAtomContainers iSet = ac.getBuilder().newSetOfAtomContainers();
    	  iSet.addAtomContainer(ac);/*2 times*/
    	  iSet.addAtomContainer(ac);
    	  double[][] gasteigerFactors = pepe.assignrPiMarsilliFactors(iSet);
    	  
    	  int stepSize = pepe.getStepSize();
	      int atomPosition = ac.getAtomNumber(atom);
	      int start = (stepSize * (atomPosition) + atomPosition);
	      if(ac.getLonePairCount(ac.getAtomAt(atomPosition)) > 0 ||
					ac.getMaximumBondOrder(ac.getAtomAt(atomPosition)) >1 ||
					ac.getAtomAt(atomPosition).getFormalCharge() != 0)
	    	  piElectronegativity = ((gasteigerFactors[1][start]) + (q * gasteigerFactors[1][start + 1]) + (gasteigerFactors[1][start + 2] * (q * q)));
//	      System.out.println(ac.getAtomAt(atomPosition).getSymbol()+" - "+piElectronegativity+"="+q+" a("+gasteigerFactors[1][start]+")+b("+gasteigerFactors[1][start+1]+")+c"+gasteigerFactors[1][start+2]+")");
	      return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(piElectronegativity));
        } catch (Exception ex1) {
        	ex1.printStackTrace();
            throw new CDKException("Problems with GasteigerPEPEPartialCharges due to " + ex1.toString(), ex1);
        }
    }


    /**
     *  Gets the parameterNames attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "maxIterations";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return new Integer(0); 
    }
}

