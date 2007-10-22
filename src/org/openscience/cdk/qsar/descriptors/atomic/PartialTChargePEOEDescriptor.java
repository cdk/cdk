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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.charges.GasteigerPEPEPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  <p>The calculation of total partial charges of an heavy atom is based on 
 *  Partial Equalization of Electronegativity method (PEOE-PEPE) from Gasteiger. </p>
 *  <p>They are obtained by summation of the results of the calculations on 
 *  sigma- and pi-charges. </p>
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
 *
 * @author      Miguel Rojas
 * @cdk.created 2006-04-11
 * @cdk.module  qsar
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:PartialTChargePEOE
 * @cdk.bug     1558660
 * @see         GasteigerMarsiliPartialCharges
 * @see         GasteigerPEPEPartialCharges
 */
public class PartialTChargePEOEDescriptor extends AbstractAtomicDescriptor {

	private AbstractAtomicDescriptor sigmaCharge;
	private AbstractAtomicDescriptor piCharge;


    /**
     *  Constructor for the PartialTChargePEOEDescriptor object
     */
    public PartialTChargePEOEDescriptor() {
        sigmaCharge = new PartialSigmaChargeDescriptor();
        piCharge = new PartialPiChargeDescriptor();
    }


    /**
     *  Gets the specification attribute of the PartialTChargePEOEDescriptor  object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#PartialTChargePEOE",
            this.getClass().getName(),
            "$Id: PartialTChargePEOEDescriptor.java 5855 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) egonw $",
            "The Chemistry Development Kit");
    }


    /**
     * This descriptor does not have any parameter to be set.
     */
    public void setParameters(Object[] params) throws CDKException {
    	// no parameters
    }


    /**
     *  Gets the parameters attribute of the PartialTChargePEOEDescriptor
     *  object
     *
     *@return    The parameters value
     *@see #setParameters
     */
    public Object[] getParameters() {
        return null;
    }


    /**
     *  The method returns partial total charges assigned to an heavy atom through PEOE method.
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     * @param  atom              The IAtom for which the DescriptorValue is requested
     * @param  ac                AtomContainer
     * @return                   an array of doubles with partial charges of [heavy, proton_1 ... proton_n]
     * @exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtom atom, IAtomContainer ac) throws CDKException {
    		

        double piRC= ((DoubleResult)piCharge.calculate(atom, ac).getValue()).doubleValue();
    	double sigmaRC= ((DoubleResult)sigmaCharge.calculate(atom, ac).getValue()).doubleValue();
	
	
    	double sum = sigmaRC + piRC;
    	DoubleResult result = new DoubleResult(sum);
    
    	return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), result);
    }


    /**
     *  Gets the parameterNames attribute of the PartialTChargePEOEDescriptor
     *  object
     *
     * @return    The parameterNames value
     */
    public String[] getParameterNames() {
        return new String[0];
    }


    /**
     *  Gets the parameterType attribute of the PartialTChargePEOEDescriptor
     *  object
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return null;
    }
}

