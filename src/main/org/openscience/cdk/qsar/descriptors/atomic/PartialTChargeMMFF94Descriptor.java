/* $Revision: 5855 $ $Author: egonw $ $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 * 
 * Copyright (C) 2006-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.charges.MMFF94PartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  The calculation of total partial charges of an heavy atom is based on MMFF94 model.
 *  
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
 * @cdk.module  builder3d
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:partialTChargeMMFF94
 * @cdk.bug     1628461
 * @see MMFF94PartialCharges
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.PartialTChargeMMFF94DescriptorTest")
public class PartialTChargeMMFF94Descriptor extends AbstractAtomicDescriptor {

    private static final String[] names = {"partialTCMMFF94"};

    private MMFF94PartialCharges mmff;


    /**
     *  Constructor for the PartialTChargeMMFF94Descriptor object
     */
    public PartialTChargeMMFF94Descriptor() { 
    	mmff = new MMFF94PartialCharges();
    }


    /**
     *  Gets the specification attribute of the PartialTChargeMMFF94Descriptor  object
     *
     *@return    The specification value
     */
    @TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#partialTChargeMMFF94",
            this.getClass().getName(),
            "$Id: PartialTChargeMMFF94Descriptor.java 5855 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) egonw $",
            "The Chemistry Development Kit");
    }


    /**
     * This descriptor does not have any parameter to be set.
     */
    @TestMethod(value="testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
    	// no parameters
    }


    /**
     *  Gets the parameters attribute of the PartialTChargeMMFF94Descriptor
     *  object
     *
     *@return    The parameters value
     *@see #setParameters
     */
    @TestMethod(value="testGetParameters")
    public Object[] getParameters() {
        return null;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }


    /**
     *  The method returns partial charges assigned to an heavy atom through MMFF94 method.
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  atom             The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
     *@return                   an array of doubles with partial charges of [heavy, proton_1 ... proton_n]
     */
    @TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer ac) {
        String originalAtomtypeName = atom.getAtomTypeName();
        Integer originalNeighborCount = atom.getFormalNeighbourCount();
        Integer originalValency = atom.getValency();
        IAtomType.Hybridization originalHybridization = atom.getHybridization();

        DoubleResult aphaPartialCharge = null;
        try {
            mmff.assignMMFF94PartialCharges(ac);
            aphaPartialCharge = new DoubleResult((Double) atom.getProperty("MMFF94charge"));
        } catch (Exception exception) {
            aphaPartialCharge = new DoubleResult(Double.NaN);
        }
        // restore original props
        atom.setAtomTypeName(originalAtomtypeName);
        atom.setFormalNeighbourCount(originalNeighborCount);
        atom.setValency(originalValency);
        atom.setHybridization(originalHybridization);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                aphaPartialCharge, names);
    }


    /**
     *  Gets the parameterNames attribute of the PartialTChargeMMFF94Descriptor
     *  object
     *
     * @return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        return new String[0];
    }


    /**
     *  Gets the parameterType attribute of the PartialTChargeMMFF94Descriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
    	 return null;
    }
}

