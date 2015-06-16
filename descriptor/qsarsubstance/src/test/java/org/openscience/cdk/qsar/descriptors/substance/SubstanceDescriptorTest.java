/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Substance;
import org.openscience.cdk.interfaces.ISubstance;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IDescriptorResult;

public abstract class SubstanceDescriptorTest {

	ISubstanceDescriptor descriptor;
    
    @SuppressWarnings("rawtypes")
    public void setDescriptor(Class descriptorClass) throws Exception {
        if (descriptor == null) {
            Object descriptor = descriptorClass.newInstance();
            if (!(descriptor instanceof ISubstanceDescriptor)) {
                throw new Exception(
                    "The passed descriptor class must be a ISubstanceDescriptor"
                );
            }
            this.descriptor = (ISubstanceDescriptor)descriptor;
        }
    }
    
    @Test
    public void testCalculate_Empty() throws Exception {
        ISubstance material = new Substance();
        DescriptorValue value = descriptor.calculate(material);
        Assert.assertNotNull(value);
        IDescriptorResult result = value.getValue();
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
    }
    
    @Test
    public void testCalculate_Null() throws Exception {
        DescriptorValue value = descriptor.calculate(null);
        Assert.assertNotNull(value);
        IDescriptorResult result = value.getValue();
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
    }

}
