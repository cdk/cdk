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
package org.openscience.cdk.qsar.descriptors;

import java.lang.reflect.Constructor;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.IImplementationSpecification;
import org.openscience.cdk.qsar.IDescriptor;

/**
 * Tests for molecular descriptors.
 *
 * @cdk.module test-qsar
 */
public abstract class DescriptorTest<T extends IDescriptor> extends CDKTestCase {

    protected T descriptor;

    public DescriptorTest() {}

    public void setDescriptor(Class<? extends T> descriptorClass) throws Exception {
        if (descriptor == null) {
            Constructor<? extends T> defaultConstructor = descriptorClass.getConstructor();
            this.descriptor = defaultConstructor.newInstance();
            this.descriptor.initialise(DefaultChemObjectBuilder.getInstance());
        }
    }

    /**
     * Makes sure that the extending class has set the super.descriptor.
     * Each extending class should have this bit of code (JUnit3 formalism):
     * <pre>
     * @Test public void setUp() {
     *   // Pass a Class, not an Object!
     *   setDescriptor(SomeDescriptor.class);
     * }
     *
     * <p>The unit tests in the extending class may use this instance, but
     * are not required.
     *
     * </pre>
     */
    @Test
    public void testHasSetSuperDotDescriptor() {
        Assert.assertNotNull("The extending class must set the super.descriptor in its setUp() method.", descriptor);
    }

    /**
     * Checks if the parameterization is consistent.
     *
     * @throws Exception
     */
    @Test
    public void testGetParameterNames() throws Exception {
        String[] paramNames = descriptor.getParameterNames();
        if (paramNames == null) paramNames = new String[0];
        for (String paramName : paramNames) {
            Assert.assertNotNull("A parameter name must not be null.", paramName);
            Assert.assertNotSame("A parameter name String must not be empty.", 0, paramName.length());
        }
    }

    /**
     * @cdk.bug 1862137
     */
    @Test
    public void testGetParameters() {
        Object[] params = descriptor.getParameters();
        if (params == null) {
            Assert.assertEquals("For all parameters a default or actual value must be returned.", 0,
                    descriptor.getParameterNames() == null ? 0 : descriptor.getParameterNames().length);
            params = new Object[0];
        }
        for (Object param : params) {
            Assert.assertNotNull("A parameter default must not be null.", param);
        }
    }

    /**
     * @cdk.bug 1862137
     */
    @Test
    public void testGetParameterType_String() {
        String[] paramNames = descriptor.getParameterNames();
        if (paramNames == null) paramNames = new String[0];
        Object[] params = descriptor.getParameters();
        if (params == null) params = new Object[0];

        for (int i = 0; i < paramNames.length; i++) {
            Object type = descriptor.getParameterType(paramNames[i]);
            Assert.assertNotNull("The getParameterType(String) return type is null for the " + "parameter: "
                    + paramNames[i], type);
            Assert.assertEquals("The getParameterType(String) return type is not consistent "
                    + "with the getParameters() types for parameter " + i, type.getClass().getName(), params[i]
                    .getClass().getName());
        }
    }

    @Test
    public void testParameterConsistency() {
        String[] paramNames = descriptor.getParameterNames();
        //      FIXME: see testGetParameterNames() comment on the same line
        if (paramNames == null) paramNames = new String[0];
        Object[] params = descriptor.getParameters();
        //      FIXME: see testGetParameters() comment on the same line
        if (params == null) params = new Object[0];

        Assert.assertEquals("The number of returned parameter names must equate the number of returned parameters",
                paramNames.length, params.length);
    }

    @Test
    public void testGetSpecification() {
        IImplementationSpecification spec = descriptor.getSpecification();
        Assert.assertNotNull("The descriptor specification returned must not be null.", spec);

        Assert.assertNotNull("The specification identifier must not be null.", spec.getImplementationIdentifier());
        Assert.assertNotSame("The specification identifier must not be empty.", 0, spec.getImplementationIdentifier()
                .length());

        Assert.assertNotNull("The specification title must not be null.", spec.getImplementationTitle());
        Assert.assertNotSame("The specification title must not be empty.", 0, spec.getImplementationTitle().length());

        Assert.assertNotNull("The specification vendor must not be null.", spec.getImplementationVendor());
        Assert.assertNotSame("The specification vendor must not be empty.", 0, spec.getImplementationVendor().length());

        Assert.assertNotNull("The specification reference must not be null.", spec.getSpecificationReference());
        Assert.assertNotSame("The specification reference must not be empty.", 0, spec.getSpecificationReference()
                .length());
    }

    /**
     * Tests that the specification no longer gives an empty CVS identifier,
     * but one based on a repository blob or commit.
     */
    @Test
    public void testGetSpecification_IdentifierNonDefault() {
        IImplementationSpecification spec = descriptor.getSpecification();
        Assert.assertNotSame("$Id$", spec.getImplementationIdentifier());
    }

    @Test
    public void testSetParameters_arrayObject() throws Exception {
        Object[] defaultParams = descriptor.getParameters();
        descriptor.setParameters(defaultParams);
    }

    @Test
    public void testGetDescriptorNames() {
        String[] descNames = descriptor.getDescriptorNames();
        Assert.assertNotNull(descNames);
        Assert.assertTrue("One or more descriptor names must be provided", descNames.length >= 1);
        for (String s : descNames) {
            Assert.assertTrue("Descriptor name must be non-zero length", s.length() != 0);
        }
    }

}
