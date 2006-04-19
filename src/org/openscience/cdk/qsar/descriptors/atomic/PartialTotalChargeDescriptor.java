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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.charges.MMFF94PartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;

/**
 *  The calculation of partial charges of an heavy atom is based on MMFF94.
 *  
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
 * @cdk.created 2006-04-11
 * @cdk.module  builder3d
 * @x.module  qsar
 * @x.set     qsar-descriptors
 * @x.dictref qsar-descriptors:PartialCharge
 * @see MMFF94PartialCharges
 */
public class PartialTotalChargeDescriptor implements IDescriptor {

    private int atomPosition = 0;
	private MMFF94PartialCharges mmff;


    /**
     *  Constructor for the PartialTotalChargeDescriptor object
     */
    public PartialTotalChargeDescriptor() { 
    	mmff = new MMFF94PartialCharges();
    }


    /**
     *  Gets the specification attribute of the PartialTotalChargeDescriptor  object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#partialCharge",
            this.getClass().getName(),
            "$Id: PartialTotalChargeDescriptor.java 5855 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) egonw $",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the PartialTotalChargeDescriptor
     *  object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("PartialTotalChargeDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter must be of type Integer");
        }
        atomPosition = ((Integer) params[0]).intValue();
    }


    /**
     *  Gets the parameters attribute of the PartialTotalChargeDescriptor
     *  object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = new Integer(atomPosition);
        return params;
    }


    /**
     *  The method returns partial charges assigned to an heavy atom through MMFF94 method.
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  ac                AtomContainer
     *@return                   an array of doubles with partial charges of [heavy, proton_1 ... proton_n]
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtomContainer ac) throws CDKException {
    	Molecule mol = new Molecule(ac);
        try {
        	mmff.assignMMFF94PartialCharges(mol);
        } catch (Exception ex1) {
            throw new CDKException("Problems with assignMMFF94PartialCharges due to " + ex1.toString(), ex1);
        }
        IAtom target = mol.getAtomAt(atomPosition);
        DoubleResult aphaPartialCharge = new DoubleResult(((Double)target.getProperty("MMFF94charge")).doubleValue());
        
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), aphaPartialCharge);
    }


    /**
     *  Gets the parameterNames attribute of the PartialTotalChargeDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "atomPosition";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the PartialTotalChargeDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return new Integer(0);
    }
}

