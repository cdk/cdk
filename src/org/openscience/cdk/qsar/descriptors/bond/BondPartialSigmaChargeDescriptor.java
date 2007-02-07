/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 *  $Revision: 5855 $
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

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PartialSigmaChargeDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 *  The calculation of bond-sigma Partial charge is calculated 
 *  determining the difference the Partial Sigma Charge on atoms 
 *  A and B of a bond. Based in Gasteiger Charge.
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
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:bondPartialSigmaCharge
 * @see PartialSigmaChargeDescriptor
 */
public class BondPartialSigmaChargeDescriptor implements IBondDescriptor {

	private PartialSigmaChargeDescriptor  descriptor;


    /**
     *  Constructor for the BondPartialSigmaChargeDescriptor object
     */
    public BondPartialSigmaChargeDescriptor() {  
    	descriptor  = new PartialSigmaChargeDescriptor() ;
    }


    /**
     *  Gets the specification attribute of the BondPartialSigmaChargeDescriptor
     *  object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#bondPartialSigmaCharge",
            this.getClass().getName(),
            "$Id: BondPartialSigmaChargeDescriptor.java 5855 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) egonw $",
            "The Chemistry Development Kit");
    }

    /**
     * This descriptor does have any parameter.
     */
    public void setParameters(Object[] params) throws CDKException {
    }


    /**
     *  Gets the parameters attribute of the BondPartialSigmaChargeDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        return null;
    }


    /**
     *  The method calculates the bond-sigma Partial charge of a given bond
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  ac                AtomContainer
     *@return                   return the sigma electronegativity
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IBond bond, IAtomContainer ac) throws CDKException {
        Molecule mol = new Molecule(ac);
        IAtom[] atoms = BondManipulator.getAtomArray(bond);
        double[] results = new double[2];
        
    	Integer[] params = new Integer[1];
    	for(int i = 0 ; i < 2 ; i++){
    		params[0] = new Integer(6);
	        descriptor.setParameters(params);
	        results[i] = ((DoubleResult)descriptor.calculate(atoms[i],mol).getValue()).doubleValue();
    	}
    	
        double result = Math.abs(results[0] - results[1]);
        
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(result));
    }

	 /**
    * Gets the parameterNames attribute of the BondPartialSigmaChargeDescriptor object.
    *
    * @return    The parameterNames value
    */
   public String[] getParameterNames() {
       return new String[0];
   }


   /**
    * Gets the parameterType attribute of the BondPartialSigmaChargeDescriptor object.
    *
    * @param  name  Description of the Parameter
    * @return       An Object of class equal to that of the parameter being requested
    */
   public Object getParameterType(String name) {
       return null;
   }
}

