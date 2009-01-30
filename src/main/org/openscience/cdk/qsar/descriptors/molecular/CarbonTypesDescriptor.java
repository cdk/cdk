/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerArrayResultType;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.BondManipulator;

import java.util.List;

/**
 * Topological descriptor characterizing the carbon connectivity.
 * <p/>
 * The class calculates 9 descriptors in the following order
 * <ul>
 * <li>C1SP1 triply hound carbon bound to one other carbon
 * <li>C2SP1	triply bound carbon bound to two other carbons
 * <li>C1SP2	doubly hound carbon bound to one other carbon
 * <li>C2SP2	doubly bound carbon bound to two other carbons
 * <li>C3SP2	doubly bound carbon bound to three other carbons
 * <li>C1SP3	singly bound carbon bound to one other carbon
 * <li>C2SP3	singly bound carbon bound to two other carbons
 * <li>C3SP3	singly bound carbon bound to three other carbons
 * <li>C4SP3	singly bound carbon bound to four other carbons
 * </ul>
 * <p>This descriptor uses these parameters:
 * <table border="1">
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td></td>
 * <td></td>
 * <td>no parameters</td>
 * </tr>
 * </table>
 *
 * @author Rajarshi Guha
 * @cdk.created 2007-09-28
 * @cdk.module qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:carbonTypes
 * @cdk.keyword topological bond order ctypes
 * @cdk.keyword descriptor
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.CarbonTypesDescriptorTest")
public class CarbonTypesDescriptor implements IMolecularDescriptor {


    private final static String[] names = {
            "C1SP1", "C2SP1",
            "C1SP2", "C2SP2", "C3SP2",
            "C1SP3", "C2SP3", "C3SP3", "C4SP3"
    };
    
    public CarbonTypesDescriptor() {
        LoggingTool logger = new LoggingTool(this);
    }

    @TestMethod("testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#carbonTypes",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     * Sets the parameters attribute of the GravitationalIndexDescriptor object.
     *
     * @param params The new parameters value
     * @throws org.openscience.cdk.exception.CDKException
     *          Description of the Exception
     * @see #getParameters
     */
    @TestMethod("testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }

    /**
     * Gets the parameters attribute of the GravitationalIndexDescriptor object.
     *
     * @return The parameters value
     * @see #setParameters
     */
    @TestMethod("testGetParameters")
    public Object[] getParameters() {
        // no parameters to return
        return (null);
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }

    /**
     * Gets the parameterNames attribute of the GravitationalIndexDescriptor object.
     *
     * @return The parameterNames value
     */
    @TestMethod("testGetParameterNames")
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }


    /**
     * Gets the parameterType attribute of the GravitationalIndexDescriptor object.
     *
     * @param name Description of the Parameter
     * @return The parameterType value
     */
    @TestMethod("testGetParameterType_String")
    public Object getParameterType(String name) {
        return (null);
    }

    /**
     * Calculates the 9 carbon types descriptors
     *
     * @param container Parameter is the atom container.
     * @return An ArrayList containing 9 elements in the order described above
     */
    @TestMethod("testCalculate_IAtomContainer,testButane,testComplex1,testComplex2")
    public DescriptorValue calculate(IAtomContainer container) {
        int c1sp1 = 0;
        int c2sp1 = 0;
        int c1sp2 = 0;
        int c2sp2 = 0;
        int c3sp2 = 0;
        int c1sp3 = 0;
        int c2sp3 = 0;
        int c3sp3 = 0;
        int c4sp3 = 0;

        for (IAtom atom : container.atoms()) {
            if (!atom.getSymbol().equals("C") && !atom.getSymbol().equals("c")) continue;
            List<IAtom> connectedAtoms = container.getConnectedAtomsList(atom);

            int cc = 0;
            for (IAtom connectedAtom : connectedAtoms) {
                if (connectedAtom.getSymbol().equals("C") || connectedAtom.getSymbol().equals("c")) cc++;
            }

            IBond.Order maxBondOrder = getHighestBondOrder(container, atom);

            if (maxBondOrder == IBond.Order.TRIPLE && cc == 1) c1sp1++;
            else if (maxBondOrder == IBond.Order.TRIPLE && cc == 2) c2sp1++;
            else if (maxBondOrder == IBond.Order.DOUBLE && cc == 1) c1sp2++;
            else if (maxBondOrder == IBond.Order.DOUBLE && cc == 2) c2sp2++;
            else if (maxBondOrder == IBond.Order.DOUBLE && cc == 3) c3sp2++;
            else if (maxBondOrder == IBond.Order.SINGLE && cc == 1) c1sp3++;
            else if (maxBondOrder == IBond.Order.SINGLE && cc == 2) c2sp3++;
            else if (maxBondOrder == IBond.Order.SINGLE && cc == 3) c3sp3++;
            else if (maxBondOrder == IBond.Order.SINGLE && cc == 4) c4sp3++;
        }

        IntegerArrayResult retval = new IntegerArrayResult(9);
        retval.add(c1sp1);
        retval.add(c2sp1);
        retval.add(c1sp2);
        retval.add(c2sp2);
        retval.add(c3sp2);
        retval.add(c1sp3);
        retval.add(c2sp3);
        retval.add(c3sp3);
        retval.add(c4sp3);


        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                retval, getDescriptorNames());
    }

    private IBond.Order getHighestBondOrder(IAtomContainer container, IAtom atom) {
        List<IBond> bonds = container.getConnectedBondsList(atom);
        IBond.Order maxOrder = IBond.Order.SINGLE;
        for (IBond bond : bonds) {
            if (BondManipulator.isHigherOrder(bond.getOrder(), maxOrder))
            	maxOrder = bond.getOrder();
        }
        return maxOrder;
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     * <p/>
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    @TestMethod("testGetDescriptorResultType")
    public IDescriptorResult getDescriptorResultType() {
        return new IntegerArrayResultType(9);
    }

}
