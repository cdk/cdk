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


import org.openscience.cdk.qsar.result.*;

/**
 * Class that is used to store descriptor values as IChemObject properties.
 *
 * @cdk.module standard
 */
public class DescriptorValue {

    private DescriptorSpecification specification;
    private String[] parameterNames;
    private Object[] parameterSettings;
    private IDescriptorResult value;
    private String[] descriptorNames;

    /**
     * Constructor.
     *
     * This constructor should not be used in new descriptor code, since it does
     * not allow you to specify a set of names.
     *
     * @deprecated 
     * @param specification
     * @param parameterNames
     * @param parameterSettings
     * @param value
     */
    public DescriptorValue(DescriptorSpecification specification,
                           String[] parameterNames,
                           Object[] parameterSettings,
                           IDescriptorResult value) {
        this.specification = specification;
        this.parameterNames = parameterNames;
        this.parameterSettings = parameterSettings;
        this.value = value;
    }

    public DescriptorValue(DescriptorSpecification specification,
                           String[] parameterNames,
                           Object[] parameterSettings,
                           IDescriptorResult value,
                           String[] descriptorNames) {
        this.specification = specification;
        this.parameterNames = parameterNames;
        this.parameterSettings = parameterSettings;
        this.value = value;
        this.descriptorNames = descriptorNames;
    }

    public DescriptorSpecification getSpecification() {
        return this.specification;
    }

    public Object[] getParameters() {
        return this.parameterSettings;
    }

    public String[] getParameterNames() {
        return this.parameterNames;
    }

    public IDescriptorResult getValue() {
        return this.value;
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
     * Note that by default if a descriptor returns a single value (such as {@link org.openscience.cdk.qsar.descriptors.molecular.ALOGP}
     * the return array will have a single element
     * <p/>
     * In case a descriptor creates a <code>DescriptorValue</code> object with no names, this
     * method will generate a set of names based on the {@link DescriptorSpecification} object
     * supplied at instantiation.
     *
     * @return An array of descriptor names.
     */
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
                    ndesc = ((DoubleArrayResult) value).length();
                } else if (value instanceof IntegerArrayResult) {
                    ndesc = ((IntegerArrayResult)value).length();
                }
                descriptorNames = new String[ndesc];
                for (int i = 1; i < ndesc+1; i++) descriptorNames[i] = title+i;
            }
        }
        return descriptorNames;
    }

}

