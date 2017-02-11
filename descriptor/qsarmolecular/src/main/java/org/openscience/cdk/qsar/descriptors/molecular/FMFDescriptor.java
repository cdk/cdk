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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fragment.MurckoFragmenter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.DoubleResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * An implementation of the FMF descriptor characterizing complexity of a molecule.
 * 
 * The descriptor is described in {@cdk.cite YANG2010} and is an approach to
 * characterizing molecular complexity based on the Murcko framework present
 * in the molecule. The descriptor is the ratio of heavy atoms in the framework to the
 * total number of heavy atoms in the molecule. By definition, acyclic molecules
 * which have no frameworks, will have a value of 0.
 *
 * Note that the authors consider an isolated ring system to be a framework (even
 * though there is no linker).
 *
 * This descriptor returns a single double value, labeled as "FMF"
 *
 * @author Rajarshi Guha
 * @cdk.module qsarmolecular
 * @cdk.dictref qsar-descriptors:FMF
 * @cdk.githash
 * @see org.openscience.cdk.fragment.MurckoFragmenter
 */
public class FMFDescriptor extends AbstractMolecularDescriptor implements IMolecularDescriptor {

    public FMFDescriptor() {}

    /**
     * Calculates the FMF descriptor value for the given {@link IAtomContainer}.
     *
     * @param container An {@link org.openscience.cdk.interfaces.IAtomContainer} for which this descriptor
     *                  should be calculated
     * @return An object of {@link org.openscience.cdk.qsar.DescriptorValue} that contains the
     *         calculated FMF descriptor value as well as specification details
     */
    @Override
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
            } else
                result = new DoubleResult(0.0);
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
     * 
     * <p>Additionally, the length indicated by the result type must match the actual
     * length of a descriptor calculated with the current parameters. Typically, the
     * length of array result types vary with the values of the parameters. See
     * {@link org.openscience.cdk.qsar.IDescriptor} for more details.
     *
     * @return an instance of the {@link org.openscience.cdk.qsar.result.DoubleResultType}
     */
    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResultType();
    }

    /**
     * Returns a <code>Map</code> which specifies which descriptor
     * is implemented by this class.
     * 
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
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification("http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#fmf",
                this.getClass().getName(), "The Chemistry Development Kit");
    }

    /**
     * Returns the names of the parameters for this descriptor.
     *
     * Since this descriptor takes no parameters, null is returned
     *
     * @return null, since there are no parameters
     */
    @Override
    public String[] getParameterNames() {
        return null;
    }

    /**
     * Returns a class matching that of the parameter with the given name.
     *
     * Since this descriptor has no parameters, null is always returned
     *
     * @param name The name of the parameter whose type is requested
     * @return null, since this descriptor has no parameters
     */
    @Override
    public Object getParameterType(String name) {
        return null;
    }

    /**
     * Sets the parameters for this descriptor.
     * 
     * This method does nothing, since the descriptor has no parameters
     *
     * @param params An array of Object containing the parameters for this descriptor
     * @throws org.openscience.cdk.exception.CDKException
     *          if invalid number of type of parameters are passed to it
     * @see #getParameters
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {}

    /**
     * Returns the current parameter values.
     *
     * null is returned since the descriptor has no parameters
     * @return null, since there are no parameters
     * @see #setParameters
     */
    @Override
    public Object[] getParameters() {
        return null;
    }

    /**
     * Returns an array of names for each descriptor value calculated.
     * 
     * Since this descriptor returns a single value, the array has a single element,
     * viz., "FMF"
     * @return A 1-element string array, with the value "FMF"
     */
    @Override
    public String[] getDescriptorNames() {
        return new String[]{"FMF"};
    }
}
