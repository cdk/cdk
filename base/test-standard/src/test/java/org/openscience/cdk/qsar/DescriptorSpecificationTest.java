/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.CDK;
import org.openscience.cdk.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class DescriptorSpecificationTest extends CDKTestCase {

    public DescriptorSpecificationTest() {
        super();
    }

    private final static String DESC_REF         = "bla";
    private final static String DESC_IMPL_TITLE  = "bla2";
    private final static String DESC_IMPL_VENDOR = "bla3";
    private final static String DESC_IMPL_ID     = "bla4";

    @Test
    public void testDescriptorSpecification_String_String_String_String() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        Assert.assertNotNull(spec);
    }

    @Test
    public void testDescriptorSpecification_String_String_String() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_VENDOR);
        Assert.assertNotNull(spec);
        Assert.assertEquals(CDK.getVersion(), spec.getImplementationIdentifier());
    }

    @Test
    public void testGetImplementationVendor() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        Assert.assertEquals(DESC_IMPL_VENDOR, spec.getImplementationVendor());
    }

    @Test
    public void testGetSpecificationReference() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        Assert.assertEquals(DESC_REF, spec.getSpecificationReference());
    }

    @Test
    public void testGetImplementationIdentifier() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        Assert.assertEquals(DESC_IMPL_ID, spec.getImplementationIdentifier());
    }

    @Test
    public void testGetImplementationTitle() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        Assert.assertEquals(DESC_IMPL_TITLE, spec.getImplementationTitle());
    }

}
