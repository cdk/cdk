/* Copyright (C) 2004-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.qsar;

import org.openscience.cdk.IImplementationSpecification;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * Classes that implement this interface are QSAR descriptor calculators.
 * The architecture provides a few subinterfaces such as the
 * <code>IMolecularDescriptor</code>, <code>IAtomicDescriptor</code> and
 * <code>IBondDescriptor</code>.
 *
 * <p><b>Calculated results</b><br>
 * The results calculated by the descriptor can have various types, which
 * extend the IDescriptorResult, and is embedded in a
 * <code>DescriptorValue</code>. Currently, there are five result types:
 * <ul>
 *   <li>BooleanResultType</li>
 *   <li>DoubleResultType</li>
 *   <li>IntegerResultType</li>
 *   <li>DoubleArrayResultType</li>
 *   <li>IntegerArrayResultType</li>
 * </ul>
 * But the DescriptorValue will hold an actual value using one of the
 * following five classes:
 * <ul>
 *   <li>BooleanResult</li>
 *   <li>DoubleResult</li>
 *   <li>IntegerResult</li>
 *   <li>DoubleArrayResult</li>
 *   <li>IntegerArrayResult</li>
 * </ul>
 *
 * <p>The length of the first of these three result types is fixed at
 * 1. However, the length of the array result types varies, depending
 * on the used descriptor parameters. The length must not depend on the
 * IAtomContainer, but only on the parameters.
 *
 * <p><b>Parameters</b><br>
 * A descriptor may have parameters that specify how the descriptor
 * is calculated, or to what level of detail. For example, the atom
 * count descriptor may calculate counts for all elements, or just
 * the specified element. As an effect, the DescriptorValue results
 * may vary in length too.
 *
 * <p>Each descriptor <b>must</b> provide default parameters, which
 * allow descriptors to be calculated without having to set parameter
 * values.
 *
 * <p>To interactively query which parameters are available, one can
 * use the methods <code>getParameterNames()</code> to see how many
 * and which parameters are available. To determine what object is
 * used to set the parameter, the method <code>getParameterType(String)</code>
 * is used, where the parameter name is used as identifier.
 *
 * <p>The default values are retrieved using the <code>getParameters()</code>
 * method of a freshly instantiated <code>IDescriptor</code>. After use
 * of <code>setParameters()</code>, the current parameter values are
 * returned.
 *
 * @cdk.module qsar
 * @cdk.githash
 *
 * @see        DescriptorValue
 * @see        IDescriptorResult
 */
public interface IDescriptor {

    /**
     * Initialise the descriptor with the specified chem object builder. This
     * allows descriptors that required domain objects, such as for SMARTS queries
     * to initialise correctly. If you do not need domain objects then this method
     * does not need to be implemented.
     *
     * @param builder chem object builder to use with this descriptor
     */
    void initialise(IChemObjectBuilder builder);

    /**
    * Returns a <code>IImplementationSpecification</code> which specifies which descriptor
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
    public IImplementationSpecification getSpecification();

    /**
     * Returns the names of the parameters for this descriptor. The method
     * returns null or a zero-length Object[] array if the descriptor
     *  does not have any parameters.
     *
     * @return An array of String containing the names of the parameters
     *         that this descriptor can accept.
     */
    public String[] getParameterNames();

    /**
     * Returns a class matching that of the parameter with the given name. May
     * only return null for when 'name' does not match any parameters returned
     * by the getParameters() method.
     *
     * @param name The name of the parameter whose type is requested
     * @return An Object of the class corresponding to the parameter with the supplied name
     */
    public Object getParameterType(String name);

    /**
     * Sets the parameters for this descriptor.
     *
     * Must be done before calling
     * calculate as the parameters influence the calculation outcome.
     *
     * @param params An array of Object containing the parameters for this descriptor
     * @throws CDKException if invalid number of type of parameters are passed to it
     * @see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException;

    /**
     * Returns the current parameter values. If not parameters have been set,
     * it must return the default parameters. The method returns null or a
     * zero-length Object[] array if the descriptor does not have any
     * parameters.
     *
     * @return An array of Object containing the parameter default values
     * @see #setParameters
     * */
    public Object[] getParameters();

    /**
     * Returns an array of names for each descriptor value calculated.
     * 
     * Many descriptors return multiple values. In general it is useful for the
     * descriptor to indicate the names for each value.
     * 
     * In many cases, these names can be as simple as X1, X2, ..., XN where X is a prefix
     * and 1, 2, ..., N are the indices. On the other hand it is also possible to return
     * other arbitrary names, which should be documented in the Javadocs for the descriptor
     * (e.g., the CPSA descriptor).
     * 
     * Note that by default if a descriptor returns a single value
     * (such as {@link org.openscience.cdk.qsar.descriptors.molecular.ALOGPDescriptor}
     * the return array will have a single element
     * 
     *
     *
     * @return An array of descriptor names, equal
     * in length to the number of descriptor calculated..
     */
    public String[] getDescriptorNames();

}
