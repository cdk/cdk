/* Copyright (C) 2011-2015  Egon Willighagen <egonw@users.sf.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.substance;

import org.openscience.cdk.interfaces.ISubstance;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * Classes that implement this interface are QSAR substance calculators.
 *
 * @cdk.githash
 */
public interface ISubstanceDescriptor extends IDescriptor {

    /**
     * Calculates the descriptor value for the given {@link ISubstance}.
     *
     * @param substance An {@link ISubstance} for which this descriptor
     *                  should be calculated
     * @return An object of {@link DescriptorValue} that contain the
     *         calculated value as well as specification details
     */
    public DescriptorValue calculate(ISubstance substance);

    /**
     * Returns the specific type of the DescriptorResult object.
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     * 
     * <p>Additionally, the length indicated by the result type must match the actual
     * length of a descriptor calculated with the current parameters. Typically, the
     * length of array result types vary with the values of the parameters. See
     * {@link IDescriptor} for more details.  
     *
     * @return an object that implements the {@link IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link DescriptorValue} object
     */
    public IDescriptorResult getDescriptorResultType();

}
