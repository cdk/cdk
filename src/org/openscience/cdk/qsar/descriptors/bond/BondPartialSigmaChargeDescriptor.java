/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 *  $Revision: 5855 $
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
package org.openscience.cdk.qsar.descriptors.bond;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PartialSigmaChargeDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;

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
public class BondPartialSigmaChargeDescriptor implements IMolecularDescriptor {

    private int bondPosition = 0;
	private IMolecularDescriptor  descriptor;


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
     *  Sets the parameters attribute of the BondPartialSigmaChargeDescriptor object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
    	if (params.length > 2) {
            throw new CDKException("BondPartialSigmaChargeDescriptor only expects two parameter");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter 1 must be of type Integer");
        }
        bondPosition = ((Integer) params[0]).intValue();
    }


    /**
     *  Gets the parameters attribute of the BondPartialSigmaChargeDescriptor object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = new Integer(bondPosition);
        return params;
    }


    /**
     *  The method calculates the bond-sigma Partial charge of a given bond
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  ac                AtomContainer
     *@return                   return the sigma electronegativity
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtomContainer ac) throws CDKException {
        Molecule mol = new Molecule(ac);
        IAtom[] atoms = mol.getBondAt(bondPosition).getAtoms();
        double[] results = new double[2];
        
    	Integer[] params = new Integer[2];
    	for(int i = 0 ; i < 2 ; i++){
			params[0] = new Integer(mol.getAtomNumber(atoms[i]));
    		params[1] = new Integer(6);
	        descriptor.setParameters(params);
	        results[i] = ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
    	}
    	
        double result = Math.abs(results[0] - results[1]);
        
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(result));
    }


    /**
     *  Gets the parameterNames attribute of the BondPartialSigmaChargeDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "bondPosition";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the BondPartialSigmaChargeDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        // since both params are of Integer type, we don't need to check
        return new Integer(0); 
    }
}

