/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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


import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.qsar.result.*;

/**
 * Class that is used to store descriptor values as IChemObject properties.
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.qsar.DescriptorValueTest")
public class DescriptorValue {

    private DescriptorSpecification specification;
    private String[] parameterNames;
    private Object[] parameterSettings;
    private IDescriptorResult value;
    private String[] descriptorNames;
    private Exception exception;

    /**
     * Constrct a descriptor value object, representing the numeric values as well as parameters and provenance.
     *
     * This constructor should be used when there has been no error during the descriptor calculation
     *
     * @param specification The specification
     * @param parameterNames The parameter names for the decriptors
     * @param parameterSettings  The parameter settings
     * @param value  The actual values
     * @param descriptorNames The names of the values
     */
    public DescriptorValue(DescriptorSpecification specification,
                           String[] parameterNames,
                           Object[] parameterSettings,
                           IDescriptorResult value,
                           String[] descriptorNames) {
        this(specification, parameterNames, parameterSettings, value, descriptorNames, null);

    }

    /**
     * Constrct a descriptor value object, representing the numeric values as well as parameters and provenance.
     *
     * This constructor should be used when there has been an error during the descriptor calculation
     *
     * @param specification The specification
     * @param parameterNames The parameter names for the decriptors
     * @param parameterSettings  The parameter settings
     * @param value  The actual values
     * @param descriptorNames The names of the values
     * @param exception The exception object that should have been caught if an error occured during decriptor
     * calculation
     */
    public DescriptorValue(DescriptorSpecification specification,
                           String[] parameterNames,
                           Object[] parameterSettings,
                           IDescriptorResult value,
                           String[] descriptorNames,
                           Exception exception) {
        this.specification = specification;
        this.parameterNames = parameterNames;
        this.parameterSettings = parameterSettings;
        this.value = value;
        this.descriptorNames = descriptorNames;
        this.exception = exception;
    }

    @TestMethod("testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return this.specification;
    }

    @TestMethod("testGetParameters")
    public Object[] getParameters() {
        return this.parameterSettings;
    }

    @TestMethod("testGetParameterNames")
    public String[] getParameterNames() {
        return this.parameterNames;
    }

    @TestMethod("testGetValue")
    public IDescriptorResult getValue() {
        return this.value;
    }

    @TestMethod("testGetException")
    public Exception getException() {
        return exception;
    }

    /**
     * Returns an array of names for each descriptor value calculated.
     * <p/>
     * Many descriptors return multiple values. In general it is useful for the
     * descriptor to indicate the names for each value. When a descriptor creates
     * a <code>DescriptorValue</code> object, it should supply an array of names equal
     * in length to the number of descriptor calculated.
     * <p/>
     * In many cases, these names can be as simple as X1, X2, ..., XN where X is a prefix
     * and 1, 2, ..., N are the indices. On the other hand it is also possible to return
     * other arbitrary names, which should be documented in the Javadocs for the decsriptor
     * (e.g., the CPSA descriptor).
     * <p/>
     * Note that by default if a descriptor returns a single value (such as {@link org.openscience.cdk.qsar.descriptors.molecular.ALOGPDescriptor}
     * the return array will have a single element
     * <p/>
     * In case a descriptor creates a <code>DescriptorValue</code> object with no names, this
     * method will generate a set of names based on the {@link DescriptorSpecification} object
     * supplied at instantiation.
     *
     * @return An array of descriptor names.
     */
    @TestMethod("testGetNames")
    public String[] getNames() {
        if (descriptorNames == null || descriptorNames.length == 0) {
            String title = specification.getImplementationTitle();
            if (value instanceof BooleanResult ||
                    value instanceof DoubleResult ||
                    value instanceof IntegerResult) {
                descriptorNames = new String[1];
                descriptorNames[0] = title;
            } else {
                int ndesc = 0;
                if (value instanceof DoubleArrayResult) {
                    ndesc = value.length();
                } else if (value instanceof IntegerArrayResult) {
                    ndesc = value.length();
                }
                descriptorNames = new String[ndesc];
                for (int i = 1; i < ndesc+1; i++) descriptorNames[i] = title+i;
            }
        }
        return descriptorNames;
    }

}

