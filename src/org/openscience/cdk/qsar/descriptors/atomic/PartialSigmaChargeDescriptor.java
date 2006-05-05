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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  The calculation of sigma partial charges in sigma-bonded systems (PEOE) of an heavy atom is based on Gasteiger Marsili
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>atomPosition</td>
 *     <td>0</td>
 *     <td>The position of the target atom</td>
 *   </tr>
 * </table>
 *
 *
 * @author      Miguel Rojas
 * @cdk.created 2006-04-15
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:PartialCharge
 * @see GasteigerMarsiliPartialCharges
 */
public class PartialSigmaChargeDescriptor implements IMolecularDescriptor {

    private int atomPosition = 0;
    private GasteigerMarsiliPartialCharges peoe = null;
	private int maxIterations;


    /**
     *  Constructor for the PartialSigmaChargeDescriptor object
     */
    public PartialSigmaChargeDescriptor() { 
        peoe = new GasteigerMarsiliPartialCharges();
    }


    /**
     *  Gets the specification attribute of the PartialSigmaChargeDescriptor
     *  object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#partialCharge",
            this.getClass().getName(),
            "$Id: PartialSigmaChargeDescriptor.java 5855 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) egonw $",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the PartialSigmaChargeDescriptor
     *  object
     *
     *@param  params            1: Atom position and 2: max iterations
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 2) {
            throw new CDKException("PartialSigmaChargeDescriptor only expects two parameter");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter 1 must be of type Integer");
        }
        atomPosition = ((Integer) params[0]).intValue();
        

        if((params.length > 1)&& params[1] != null ){
            if (!(params[1] instanceof Integer) ){
                throw new CDKException("The parameter 2 must be of type Integer");
            }
            maxIterations = ((Integer) params[1]).intValue();
        }
    }


    /**
     *  Gets the parameters attribute of the PartialSigmaChargeDescriptor object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[2];
        params[0] = new Integer(atomPosition);
        params[1] = new Integer(maxIterations);
        return params;
    }


    /**
     *  The method returns apha partial charges assigned to an heavy atom through Gasteiger Marsili
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *  For this method will be only possible if the heavy atom has single bond.
     *
     *@param  ac                AtomContainer
     *@return                   Value of the alpha partial charge
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtomContainer ac) throws CDKException {
        Molecule mol = new Molecule(ac);
        try {
        	if(maxIterations != 0)
        		peoe.setMaxGasteigerIters(maxIterations);
            peoe.assignGasteigerMarsiliSigmaPartialCharges(mol, true);
        } catch (Exception ex1) {
            throw new CDKException("Problems with assignGasteigerMarsiliSigmaPartialCharges due to " + ex1.toString(), ex1);
        }
        IAtom target = mol.getAtomAt(atomPosition);
        DoubleResult sigmaPartialCharge = new DoubleResult(target.getCharge());
        
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), sigmaPartialCharge);
    }


    /**
     *  Gets the parameterNames attribute of the PartialSigmaChargeDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[2];
        params[0] = "atomPosition";
        params[1] = "maxIterations";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the PartialSigmaChargeDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
    	Integer[] object = {new Integer(0), new Integer(0)};
        return object;
    }
}

