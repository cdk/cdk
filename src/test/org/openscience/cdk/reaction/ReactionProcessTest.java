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
package org.openscience.cdk.reaction;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;

import org.openscience.cdk.NewCDKTestCase;

/**
 * Tests for IReactionProcess implementations.
 *
 * @cdk.module test-reaction
 */
public abstract class ReactionProcessTest extends NewCDKTestCase {
	
	protected static IReactionProcess reaction;
	
	public static void setReaction(Class<?> descriptorClass) throws Exception {
		if (ReactionProcessTest.reaction == null) {
			Object descriptor = (Object)descriptorClass.newInstance();
			if (!(descriptor instanceof IReactionProcess)) {
				throw new CDKException("The passed reaction class must be a IReactionProcess");
			}
			ReactionProcessTest.reaction = (IReactionProcess)descriptor;
		}
	}
	
	/**
	 * Makes sure that the extending class has set the super.descriptor.
	 * Each extending class should have this bit of code (JUnit3 formalism):
	 * <pre>
	 * public void setUp() {
	 *   // Pass a Class, not an Object!
	 *   setDescriptor(SomeDescriptor.class);
	 * }
	 * 
	 * <p>The unit tests in the extending class may use this instance, but
	 * are not required.
	 * 
	 * </pre>
	 */
	@Test public void testHasSetSuperDotDescriptor() {
		Assert.assertNotNull("The extending class must set the super.descriptor in its seUp() method.", reaction);    	
	}
	
	/**
	 * Checks if the parameterization is consistent.
	 * 
	 * @throws Exception 
	 */
	@Test public void testGetParameterNames() throws Exception {
        String[] paramNames = reaction.getParameterNames();
//        FIXME: the next would be nice, but not currently agreed-upon policy
//        assertNotNull(
//        	"The method getParameterNames() must return a non-null value, possible a zero length String[] array",
//        	paramNames
//        );
//        FIXME: so instead:
        if (paramNames == null) paramNames = new String[0];
        for (int i=0; i<paramNames.length; i++) {
        	Assert.assertNotNull(
        		"A parameter name must not be null.",
        		paramNames[i]
        	);
        	Assert.assertNotSame(
            	"A parameter name String must not be empty.",
            	0, paramNames[i].length()
            );
        }
    }
    
	@Test public void testGetParameters() {
        Object[] params = reaction.getParameters();
//      FIXME: the next would be nice, but not currently agreed-upon policy
//      Assert.assertNotNull(
//      	"The method getParameters() must return a non-null value, possible a zero length Object[] array",
//      	paramNames
//      );
//      FIXME: so instead:
        if (params == null) params = new Object[0];
        for (int i=0; i<params.length; i++) {
        	Assert.assertNotNull(
        		"A parameter default must not be null.",
        		params[i]
        	);
        }
    }
    
	@Test public void testGetParameterType_String() {
        String[] paramNames = reaction.getParameterNames();
//      FIXME: see testGetParameterNames() comment on the same line 
        if (paramNames == null) paramNames = new String[0];
        Object[] params = reaction.getParameters();
//      FIXME: see testGetParameters() comment on the same line
        if (params == null) params = new Object[0];

        for (int i=0; i<paramNames.length; i++) {
        	Object type = reaction.getParameterType(paramNames[i]);
        	Assert.assertNotNull(
        		"The getParameterType(String) return type is null for the " +
        		"parameter: " + paramNames[i],
        		type
        	);
        	Assert.assertEquals(
        		"The getParameterType(String) return type is not consistent " +
        		"with the getParameters() types for parameter " + i,
        		type.getClass().getName(),
        		params[i].getClass().getName()
        	);
        }
    }
    
	@Test public void testParameterConsistency() {
        String[] paramNames = reaction.getParameterNames();
//      FIXME: see testGetParameterNames() comment on the same line 
        if (paramNames == null) paramNames = new String[0];
        Object[] params = reaction.getParameters();
//      FIXME: see testGetParameters() comment on the same line
        if (params == null) params = new Object[0];
        
        Assert.assertEquals(
        	"The number of returned parameter names must equate the number of returned parameters",
        	paramNames.length, params.length
        );
    }

	@Test public void testGetSpecification() {
    	ReactionSpecification spec = reaction.getSpecification();
    	Assert.assertNotNull(
    		"The descriptor specification returned must not be null.",
    		spec
    	);

    	Assert.assertNotNull(
    		"The specification identifier must not be null.",
    		spec.getImplementationIdentifier()
    	);
    	Assert.assertNotSame(
       		"The specification identifier must not be empty.",
       		0, spec.getImplementationIdentifier().length()
       	);

    	Assert.assertNotNull(
       		"The specification title must not be null.",
       		spec.getImplementationTitle()
    	);
    	Assert.assertNotSame(
    		"The specification title must not be empty.",
    		0, spec.getImplementationTitle().length()
    	);

    	Assert.assertNotNull(
       		"The specification vendor must not be null.",
       		spec.getImplementationVendor()
    	);
    	Assert.assertNotSame(
    		"The specification vendor must not be empty.",
    		0, spec.getImplementationVendor().length()
    	);

    	Assert.assertNotNull(
       		"The specification reference must not be null.",
       		spec.getSpecificationReference()
    	);
    	Assert.assertNotSame(
    		"The specification reference must not be empty.",
    		0, spec.getSpecificationReference().length()
    	);
    }
    
	@Test public void testSetParameters_arrayObject() throws Exception {
    	Object[] defaultParams = reaction.getParameters();
    	reaction.setParameters(defaultParams);
    }    
}
