/*
 *  Copyright (C) 2010  Rajarshi Guha <rajarshi.guha@gmail.com>
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
import org.openscience.cdk.fragment.MurckoFragmenter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.DoubleResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * An implementation of the FMF descriptor characterizing complexity of a molecule.
 * <p/>
 * The descriptor is described in {@cdk.cite YANG2010} and is an approach to
 * characterizing molecular complexity based on the Murcko framework present
 * in the molecule. The descriptor is the ratio of heavy atoms in the framework to the
 * total number of heavy atoms in the molecule. By definition, acyclic molecules
 * which have no frameworks, will have a value of 0.
 *
 * Note that the authors consider an isolated ring system to be a framework (even
 * though there is no linker).
 *
 * @author Rajarshi Guha
 * @cdk.module qsarmolecular
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:FMF
 * @see org.openscience.cdk.fragment.MurckoFragmenter
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.FMFDescriptorTest")
public class FMFDescriptor implements IMolecularDescriptor {

    public FMFDescriptor() {
    }


    /**
     * Calculates the FMF descriptor value for the given {@link IAtomContainer}.
     *
     * @param container An {@link org.openscience.cdk.interfaces.IAtomContainer} for which this descriptor
     *                  should be calculated
     * @return An object of {@link org.openscience.cdk.qsar.DescriptorValue} that contains the
     *         calculated FMF descriptor value as well as specification details
     */
    @TestMethod("testCarbinoxamine,testIsamoltane,testPirenperone")
    public DescriptorValue calculate(IAtomContainer container) {
        MurckoFragmenter fragmenter = new MurckoFragmenter(true, 3);
        DoubleResult result;
        try {
            fragmenter.generateFragments(container);
            IAtomContainer[] framework = fragmenter.getFrameworksAsContainers();
            IAtomContainer[] ringSystems = fragmenter.getRingSystemsAsContainers();
            if (framework.length == 1) {
                result = new DoubleResult(framework[0].getAtomCount() / (double) container.getAtomCount());
            } else if (framework.length == 0 && ringSystems.length == 1) {
                result = new DoubleResult(ringSystems[0].getAtomCount() / (double) container.getAtomCount());
            } else result = new DoubleResult(0.0);
        } catch (CDKException e) {
            result = new DoubleResult(Double.NaN);
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), result,
                getDescriptorNames());

    }

    /**
     * Returns the specific type of the FMF descriptor value.
     *
     * The FMF descriptor is a single, double value.
     *
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     * <p/>
     * <p>Additionally, the length indicated by the result type must match the actual
     * length of a descriptor calculated with the current parameters. Typically, the
     * length of array result types vary with the values of the parameters. See
     * {@link org.openscience.cdk.qsar.IDescriptor} for more details.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResultType();
    }

    /**
     * Returns a <code>Map</code> which specifies which descriptor
     * is implemented by this class.
     * <p/>
     * These fields are used in the map:
     * <ul>
     * <li>Specification-Reference: refers to an entry in a unique dictionary
     * <li>Implementation-Title: anything
     * <li>Implementation-Identifier: a unique identifier for this version of
     * this class
     * <li>Implementation-Vendor: CDK, JOELib, or anything else
     * </ul>
     *
     * @return An object containing the descriptor specification
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#fmf",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }

    /**
     * Returns the names of the parameters for this descriptor.
     *
     * The method returns null or a zero-length Object[] array if the descriptor
     * does not have any parameters.
     *
     * @return An array of String containing the names of the parameters
     *         that this descriptor can accept.
     */
    public String[] getParameterNames() {
        return null;
    }

    /**
     * Returns a class matching that of the parameter with the given name.
     *
     * May only return null for when 'name' does not match any parameters returned
     * by the getParameters() method.
     *
     * @param name The name of the parameter whose type is requested
     * @return An Object of the class corresponding to the parameter with the supplied name
     */
    public Object getParameterType(String name) {
        return null;
    }

    /**
     * Sets the parameters for this descriptor.
     * <p/>
     * Must be done before calling
     * calculate as the parameters influence the calculation outcome.
     *
     * @param params An array of Object containing the parameters for this descriptor
     * @throws org.openscience.cdk.exception.CDKException
     *          if invalid number of type of parameters are passed to it
     * @see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
    }

    /**
     * Returns the current parameter values. If not parameters have been set,
     * it must return the default parameters. The method returns null or a
     * zero-length Object[] array if the descriptor does not have any
     * parameters.
     *
     * @return An array of Object containing the parameter default values
     * @see #setParameters
     */
    public Object[] getParameters() {
        return null;
    }

    /**
     * Returns an array of names for each descriptor value calculated.
     * <p/>
     * Many descriptors return multiple values. In general it is useful for the
     * descriptor to indicate the names for each value.
     * <p/>
     * In many cases, these names can be as simple as X1, X2, ..., XN where X is a prefix
     * and 1, 2, ..., N are the indices. On the other hand it is also possible to return
     * other arbitrary names, which should be documented in the Javadocs for the decsriptor
     * (e.g., the CPSA descriptor).
     * <p/>
     * Note that by default if a descriptor returns a single value
     * (such as {@link ALOGPDescriptor}
     * the return array will have a single element
     * <p/>
     *
     * @return An array of descriptor names, equal
     *         in length to the number of descriptor calculated..
     */
    public String[] getDescriptorNames() {
        return new String[]{"FMF"};
    }
}
