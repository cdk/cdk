/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * TestSuite that runs all tests for the DescriptorEngine.
 *
 * @cdk.module test-qsar
 */
public class DescriptorExceptionTest extends CDKTestCase {

    @Test
    public void testConstructor() {
        DescriptorException exception = new DescriptorException("Message");
        Assert.assertNotNull(exception);
        Assert.assertEquals("Message", exception.getMessage());

        exception = new DescriptorException("Name", "Message");
        Assert.assertNotNull(exception);
        Assert.assertEquals("Name: Message", exception.getMessage());
    }

}
