/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.qsar;

import java.lang.reflect.Constructor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.IImplementationSpecification;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.IDescriptor;

/**
 * Tests for molecular descriptors.
 *
 */
public abstract class DescriptorTest<T extends IDescriptor> extends CDKTestCase {

    protected T descriptor;

    public DescriptorTest() {}

    public void setDescriptor(Class<? extends T> descriptorClass) throws Exception {
        if (descriptor == null) {
            Constructor<? extends T> defaultConstructor = descriptorClass.getConstructor();
            this.descriptor = defaultConstructor.newInstance();
        }
    }

    public void setDescriptor(Class<? extends T> descriptorClass,
                              IChemObjectBuilder builder) throws Exception {
        if (descriptor == null) {
            Constructor<? extends T> defaultConstructor = descriptorClass.getConstructor();
            this.descriptor = defaultConstructor.newInstance();
            this.descriptor.initialise(builder);
        }
    }

    /**
     * Makes sure that the extending class has set the super.descriptor.
     * Each extending class should have this bit of code (JUnit3 formalism):
     * <pre>{@code
     * @Test public void setUp() {
     *   // Pass a Class, not an Object!
     *   setDescriptor(SomeDescriptor.class);
     * }}
     * </pre>
     *
     * <p>The unit tests in the extending class may use this instance, but
     * are not required.
     */
    @Test
    public void testHasSetSuperDotDescriptor() {
        Assertions.assertNotNull(descriptor, "The extending class must set the super.descriptor in its setUp() method.");
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
            Assertions.assertNotNull(paramName, "A parameter name must not be null.");
            Assertions.assertNotSame(0, paramName.length(), "A parameter name String must not be empty.");
        }
    }

    /**
     * @cdk.bug 1862137
     */
    @Test
    public void testGetParameters() {
        Object[] params = descriptor.getParameters();
        if (params == null) {
            Assertions.assertEquals(0, descriptor.getParameterNames() == null ? 0 : descriptor.getParameterNames().length, "For all parameters a default or actual value must be returned.");
            params = new Object[0];
        }
        for (Object param : params) {
            Assertions.assertNotNull(param, "A parameter default must not be null.");
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
            Assertions.assertNotNull(type, "The getParameterType(String) return type is null for the " + "parameter: "
                    + paramNames[i]);
            Assertions.assertEquals(type.getClass().getName(), params[i]
            .getClass().getName(), "The getParameterType(String) return type is not consistent "
                    + "with the getParameters() types for parameter " + i);
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

        Assertions.assertEquals(paramNames.length, params.length, "The number of returned parameter names must equate the number of returned parameters");
    }

    @Test
    public void testGetSpecification() {
        IImplementationSpecification spec = descriptor.getSpecification();
        Assertions.assertNotNull(spec, "The descriptor specification returned must not be null.");

        Assertions.assertNotNull(spec.getImplementationIdentifier(), "The specification identifier must not be null.");
        Assertions.assertNotSame(0, spec.getImplementationIdentifier()
                                        .length(), "The specification identifier must not be empty.");

        Assertions.assertNotNull(spec.getImplementationTitle(), "The specification title must not be null.");
        Assertions.assertNotSame(0, spec.getImplementationTitle().length(), "The specification title must not be empty.");

        Assertions.assertNotNull(spec.getImplementationVendor(), "The specification vendor must not be null.");
        Assertions.assertNotSame(0, spec.getImplementationVendor().length(), "The specification vendor must not be empty.");

        Assertions.assertNotNull(spec.getSpecificationReference(), "The specification reference must not be null.");
        Assertions.assertNotSame(0, spec.getSpecificationReference()
                                        .length(), "The specification reference must not be empty.");
    }

    /**
     * Tests that the specification no longer gives an empty CVS identifier,
     * but one based on a repository blob or commit.
     */
    @Test
    public void testGetSpecification_IdentifierNonDefault() {
        IImplementationSpecification spec = descriptor.getSpecification();
        Assertions.assertNotSame("$Id$", spec.getImplementationIdentifier());
    }

    @Test
    public void testSetParameters_arrayObject() throws Exception {
        Object[] defaultParams = descriptor.getParameters();
        descriptor.setParameters(defaultParams);
    }

    @Test
    public void testGetDescriptorNames() {
        String[] descNames = descriptor.getDescriptorNames();
        Assertions.assertNotNull(descNames);
        Assertions.assertTrue(descNames.length >= 1, "One or more descriptor names must be provided");
        for (String s : descNames) {
            Assertions.assertTrue(s.length() != 0, "Descriptor name must be non-zero length");
        }
    }

}
