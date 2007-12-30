/* $Revision: 8418 $ $Author: egonw $ $Date: 2007-06-25 22:05:44 +0200 (Mon, 25 Jun 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.qsar.descriptors;

import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Tests for molecular descriptors.
 *
 * @cdk.module test-qsar
 */
public abstract class DescriptorTest extends CDKTestCase {
	
	protected IDescriptor descriptor;

	public DescriptorTest() {}
	
	public DescriptorTest(String name) {
		super(name);
	}
	
	/**
	 * Makes sure that the extending class has set the super.descriptor.
	 * Each extending class should have this bit of code (JUnit3 formalism):
	 * <pre>
	 * public void setUp() {
	 *   super.descriptor = new SomeDescriptor();
	 * }
	 * 
	 * <p>The unit tests in the extending class may use this instance, but
	 * are not required.
	 * 
	 * </pre>
	 */
	public void testHasSetSuperDotDescriptor() {
    	assertNotNull("The extending class must set the super.descriptor in its seUp() method.", descriptor);    	
	}
	
	/**
	 * Checks if the parameterization is consistent.
	 * 
	 * @throws Exception 
	 */
    public void testGetParameterNames() throws Exception {
        String[] paramNames = descriptor.getParameterNames();
//        FIXME: the next would be nice, but not currently agreed-upon policy
//        assertNotNull(
//        	"The method getParameterNames() must return a non-null value, possible a zero length String[] array",
//        	paramNames
//        );
//        FIXME: so instead:
        if (paramNames == null) paramNames = new String[0];
        for (int i=0; i<paramNames.length; i++) {
        	assertNotNull(
        		"A parameter name must not be null.",
        		paramNames[i]
        	);
        	assertNotSame(
            	"A parameter name String must not be empty.",
            	0, paramNames[i].length()
            );
        }
    }
    
    public void testGetParameters() {
        Object[] params = descriptor.getParameters();
//      FIXME: the next would be nice, but not currently agreed-upon policy
//      assertNotNull(
//      	"The method getParameters() must return a non-null value, possible a zero length Object[] array",
//      	paramNames
//      );
//      FIXME: so instead:
        if (params == null) params = new Object[0];
        for (int i=0; i<params.length; i++) {
        	assertNotNull(
        		"A parameter default must not be null.",
        		params[i]
        	);
        }
    }
    
    public void testGetParameterType_String() {
        String[] paramNames = descriptor.getParameterNames();
//      FIXME: see testGetParameterNames() comment on the same line 
        if (paramNames == null) paramNames = new String[0];
        Object[] params = descriptor.getParameters();
//      FIXME: see testGetParameters() comment on the same line
        if (params == null) params = new Object[0];

        for (int i=0; i<paramNames.length; i++) {
        	Object type = descriptor.getParameterType(paramNames[i]);
        	assertEquals(
        		"The getParameterType(String) return type is not consistent " +
        		"with the getParameters() types for parameter " + i,
        		type.getClass().getName(),
        		params[i].getClass().getName()
        	);
        }
    }
    
    public void testParameterConsistency() {
        String[] paramNames = descriptor.getParameterNames();
//      FIXME: see testGetParameterNames() comment on the same line 
        if (paramNames == null) paramNames = new String[0];
        Object[] params = descriptor.getParameters();
//      FIXME: see testGetParameters() comment on the same line
        if (params == null) params = new Object[0];
        
        assertEquals(
        	"The number of returned parameter names must equate the number of returned parameters",
        	paramNames.length, params.length
        );
    }

    public void testGetSpecification() {
    	DescriptorSpecification spec = descriptor.getSpecification();
    	assertNotNull(
    		"The descriptor specification returned must not be null.",
    		spec
    	);

    	assertNotNull(
    		"The specification identifier must not be null.",
    		spec.getImplementationIdentifier()
    	);
    	assertNotSame(
       		"The specification identifier must not be empty.",
       		0, spec.getImplementationIdentifier().length()
       	);

    	assertNotNull(
       		"The specification title must not be null.",
       		spec.getImplementationTitle()
    	);
    	assertNotSame(
    		"The specification title must not be empty.",
    		0, spec.getImplementationTitle().length()
    	);

    	assertNotNull(
       		"The specification vendor must not be null.",
       		spec.getImplementationVendor()
    	);
    	assertNotSame(
    		"The specification vendor must not be empty.",
    		0, spec.getImplementationVendor().length()
    	);

    	assertNotNull(
       		"The specification reference must not be null.",
       		spec.getSpecificationReference()
    	);
    	assertNotSame(
    		"The specification reference must not be empty.",
    		0, spec.getSpecificationReference().length()
    	);
    }
    
    public void testSetParameters_arrayObject() throws Exception {
    	Object[] defaultParams = descriptor.getParameters();
    	descriptor.setParameters(defaultParams);
    }
    
}
