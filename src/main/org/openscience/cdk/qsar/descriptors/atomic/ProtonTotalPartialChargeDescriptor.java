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
import org.openscience.cdk.qsar.result.DoubleArrayResult;

import java.util.List;

/**
 *  The calculation of partial charges of an heavy atom and its protons is based on Gasteiger Marsili (PEOE)
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
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsaratomic
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:protonPartialCharge
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.ProtonTotalPartialChargeDescriptorTest")
public class ProtonTotalPartialChargeDescriptor implements IAtomicDescriptor {

    private GasteigerMarsiliPartialCharges peoe = null;
    private List<IAtom> neighboors;


    /**
     *  Constructor for the ProtonTotalPartialChargeDescriptor object
     */
    public ProtonTotalPartialChargeDescriptor() { }


    /**
     *  Gets the specification attribute of the ProtonTotalPartialChargeDescriptor
     *  object
     *
     *@return    The specification value
     */
    @TestMethod(value="testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#protonPartialCharge",
            this.getClass().getName(),
            "$Id$",
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
     *  Gets the parameters attribute of the ProtonTotalPartialChargeDescriptor
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
       String[] labels = new String[(neighboors == null ? 0 : neighboors.size()) +1];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = "protonTotalPartialCharge" + (i+1);
        }
        return labels;
    }


    private DescriptorValue getDummyDescriptorValue(Exception e) {
        DoubleArrayResult result = new DoubleArrayResult(neighboors.size() + 1);
        for (int i = 0; i < neighboors.size() + 1; i++) result.add(Double.NaN);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                result, getDescriptorNames(), e);
    }

    /**
     *  The method returns partial charges assigned to an heavy atom and its protons through Gasteiger Marsili
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
     *@return                   an array of doubles with partial charges of [heavy, proton_1 ... proton_n]
     */
    @TestMethod(value="testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtom atom, IAtomContainer ac) {
        neighboors = ac.getConnectedAtomsList(atom);

        IAtomContainer clone;
        IAtom localAtom;
        try {
            clone = (IAtomContainer) ac.clone();
            localAtom = clone.getAtom(ac.getAtomNumber(atom));
        } catch (CloneNotSupportedException e) {
            return getDummyDescriptorValue(e);
        }
        neighboors = clone.getConnectedAtomsList(localAtom);

        try {

            peoe = new GasteigerMarsiliPartialCharges();
            peoe.setMaxGasteigerIters(6);
            //	HydrogenAdder hAdder = new HydrogenAdder();
            //	hAdder.addExplicitHydrogensToSatisfyValency(mol);
            peoe.assignGasteigerMarsiliSigmaPartialCharges(clone, true);
        } catch (Exception exception) {
            DoubleArrayResult result = new DoubleArrayResult(neighboors.size() + 1);
            for (int i = 0; i < neighboors.size() + 1; i++) result.add(Double.NaN);
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                    result, getDescriptorNames(), exception);
        }

        DoubleArrayResult protonPartialCharge = new DoubleArrayResult(neighboors.size() + 1);
        protonPartialCharge.add( localAtom.getCharge() );
        for (IAtom neighboor : neighboors) {
            if (neighboor.getSymbol().equals("H")) {
                protonPartialCharge.add(neighboor.getCharge());
            }
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                protonPartialCharge, getDescriptorNames());
    }


    /**
     *  Gets the parameterNames attribute of the ProtonTotalPartialChargeDescriptor
     *  object
     *
     * @return    The parameterNames value
     */
    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        return new String[0];
    }


    /**
     *  Gets the parameterType attribute of the ProtonTotalPartialChargeDescriptor
     *  object
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        return null;
    }
}

