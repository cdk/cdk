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
 * The calculation of partial charges of an heavy atom and its protons is based on Gasteiger Marsili (PEOE).
 *
 * This descriptor has no parameters. The result of this descriptor is a vector of 5 values, corresponding
 * to a maximum of four protons for any given atom. If an atom has fewer than four protons, the remaining values
 * are set to Double.NaN. Also note that the values for the neighbors are not returned in a particular order
 * (though the order is fixed for multiple runs for the same atom).
 *
 * @author mfe4
 * @cdk.created 2004-11-03
 * @cdk.module qsaratomic
 * @cdk.svnrev $Revision$
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:protonPartialCharge
 */
@TestClass(value="org.openscience.cdk.qsar.descriptors.atomic.ProtonTotalPartialChargeDescriptorTest")
public class ProtonTotalPartialChargeDescriptor implements IAtomicDescriptor {

    private GasteigerMarsiliPartialCharges peoe = null;
    private List<IAtom> neighboors;
    private final int MAX_PROTON_COUNT = 5;


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
       String[] labels = new String[MAX_PROTON_COUNT];
        for (int i = 0; i < MAX_PROTON_COUNT; i++) {
            labels[i] = "protonTotalPartialCharge" + (i+1);
        }
        return labels;
    }


    private DescriptorValue getDummyDescriptorValue(Exception e) {
        DoubleArrayResult result = new DoubleArrayResult(MAX_PROTON_COUNT);
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
        try {
            clone = (IAtomContainer) ac.clone();
        } catch (CloneNotSupportedException e) {
            return getDummyDescriptorValue(e);
        }

        try {
            peoe = new GasteigerMarsiliPartialCharges();
            peoe.setMaxGasteigerIters(6);
            //	HydrogenAdder hAdder = new HydrogenAdder();
            //	hAdder.addExplicitHydrogensToSatisfyValency(mol);
            peoe.assignGasteigerMarsiliSigmaPartialCharges(clone, true);
        } catch (Exception exception) {
            return getDummyDescriptorValue(exception);
        }

        IAtom localAtom = clone.getAtom(ac.getAtomNumber(atom));
        neighboors = clone.getConnectedAtomsList(localAtom);

        // we assume that an atom has a mxa number of protons = MAX_PROTON_COUNT
        // if it has less, we pad with NaN
        DoubleArrayResult protonPartialCharge = new DoubleArrayResult(MAX_PROTON_COUNT);
        assert (neighboors.size() < MAX_PROTON_COUNT);


        protonPartialCharge.add(localAtom.getCharge());
        for (IAtom neighboor : neighboors) {
            System.out.println("neighboor.getSymbol() = " + neighboor.getSymbol());
            if (neighboor.getSymbol().equals("H")) {
                protonPartialCharge.add(neighboor.getCharge());
            }
        }
        int remainder = MAX_PROTON_COUNT - neighboors.size() + 1;
        for (int i = 0; i < remainder; i++) protonPartialCharge.add(Double.NaN);

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

