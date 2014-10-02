/*  Copyright (C) 2004-2007  Christian Hoppe
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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  Class that returns the complexity of a system. The complexity is defined as {@cdk.cite Nilakantan06}:
 *  <pre>
 *  C=abs(B^2-A^2+A)+H/100
 *  </pre>
 *  where C=complexity, A=number of non-hydrogen atoms, B=number of bonds and H=number of heteroatoms
 *
 * <p>This descriptor uses no parameters.
 *
 * @author      chhoppe from EUROSCREEN
 * @cdk.created 2006-8-22
 * @cdk.module  qsarmolecular
 * @cdk.githash
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:NilaComplexity
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.FragmentComplexityDescriptorTest")
public class FragmentComplexityDescriptor extends AbstractMolecularDescriptor implements IMolecularDescriptor {

    private static final String[] names = {"fragC"};

    /**
     *  Constructor for the FragmentComplexityDescriptor object.
     */
    public FragmentComplexityDescriptor() {}

    /**
     * Returns a <code>Map</code> which specifies which descriptor
     * is implemented by this class.
     *
     * These fields are used in the map:
     * <ul>
     * <li>Specification-Reference: refers to an entry in a unique dictionary
     * <li>Implementation-Title: anything
     * <li>Implementation-Identifier: a unique identifier for this version of
     *  this class
     * <li>Implementation-Vendor: CDK, JOELib, or anything else
     * </ul>
     *
     * @return An object containing the descriptor specification
     */
    @TestMethod("testGetSpecification")
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#NilaComplexity", this.getClass()
                        .getName(), "The Chemistry Development Kit");
    }

    /**
     *  Sets the parameters attribute of the FragmentComplexityDescriptor object.
     *
     * This descriptor takes no parameter.
     *
     * @param  params            The new parameters value
     * @exception  CDKException if more than one parameter or a non-Boolean parameter is specified
     * @see #getParameters
     */
    @TestMethod("testSetParameters_arrayObject")
    @Override
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 0) {
            throw new CDKException("FragmentComplexityDescriptor expects no parameter");
        }
    }

    /**
     *  Gets the parameters attribute of the FragmentComplexityDescriptor object.
     *
     * @return    The parameters value
     * @see #setParameters
     */
    @TestMethod("testGetParameters")
    @Override
    public Object[] getParameters() {
        return new Object[0];
        // return the parameters as used for the descriptor calculation
    }

    @TestMethod(value = "testNamesConsistency")
    @Override
    public String[] getDescriptorNames() {
        return names;
    }

    /**
     * Calculate the complexity in the supplied {@link IAtomContainer}.
     *
     *@param  container  The {@link IAtomContainer} for which this descriptor is to be calculated
     *@return                   the complexity
     *@see #setParameters
     */
    @TestMethod("testCalculate_IAtomContainer")
    @Override
    public DescriptorValue calculate(IAtomContainer container) {
        //System.out.println("FragmentComplexityDescriptor");
        int a = 0;
        double h = 0;
        for (int i = 0; i < container.getAtomCount(); i++) {
            if (!container.getAtom(i).getSymbol().equals("H")) {
                a++;
            }
            if (!container.getAtom(i).getSymbol().equals("H") & !container.getAtom(i).getSymbol().equals("C")) {
                h++;
            }
        }
        int b = container.getBondCount() + AtomContainerManipulator.getImplicitHydrogenCount(container);
        double c = Math.abs(b * b - a * a + a) + (h / 100);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(c),
                getDescriptorNames());
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
    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResult(0.0);
    }

    /**
     *  Gets the parameterNames attribute of the FragmentComplexityDescriptor object.
     *
     *@return    The parameterNames value
     */
    @TestMethod("testGetParameterNames")
    @Override
    public String[] getParameterNames() {
        return null;
    }

    /**
     *  Gets the parameterType attribute of the FragmentComplexityDescriptor object.
     *
     *@param  name  Description of the Parameter
     *@return       An Object of class equal to that of the parameter being requested
     */
    @TestMethod("testGetParameterType_String")
    @Override
    public Object getParameterType(String name) {
        return null;
    }
}
