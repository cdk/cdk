/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 * 
 */
package org.openscience.cdk.test.debug;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.debug.DebugAtomParity;
import org.openscience.cdk.debug.DebugChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.test.AtomParityTest;

/**
 * Checks the funcitonality of the AtomContainer.
 *
 * @cdk.module test-datadebug
 */
public class DebugAtomParityTest extends AtomParityTest {

    @BeforeClass public static void setUp() {
    	AtomParityTest.builder = DebugChemObjectBuilder.getInstance();
    }
    
    @Test public void testCorrectInstance() {
    	IAtomParity parity = builder.newAtomParity(builder.newAtom(), builder.newAtom(), builder.newAtom(), builder.newAtom(), builder.newAtom(), 1); 
    	Assert.assertTrue(
    		"Object not instance of DebugAtomParity, but: " + parity.getClass().getName(),
    		parity instanceof DebugAtomParity
    	);
    }

}
