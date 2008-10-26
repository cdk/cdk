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
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMonomer;

/**
 * TestCase for the Monomer class.
 *
 * @cdk.module test-data
 *
 * @author  Edgar Luttman <edgar@uni-paderborn.de>
 * @cdk.created 2001-08-09
 */
<<<<<<< HEAD:src/test/org/openscience/cdk/MonomerTest.java
public class MonomerTest extends AtomContainerTest {
=======
public class MonomerTest extends CDKTestCase {
>>>>>>> bbc19522071c1b78697779bddcd7509e9314667e:src/test/org/openscience/cdk/MonomerTest.java

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

	@Test public void testMonomer() {
		IMonomer oMonomer = builder.newMonomer();
        Assert.assertTrue(oMonomer != null);
	}
	
	@Test public void testSetMonomerName_String() {
        IMonomer m = builder.newMonomer();
        m.setMonomerName(new String("TRP279"));
        Assert.assertEquals(new String("TRP279"), m.getMonomerName());
	}
    @Test public void testGetMonomerName() {
        testSetMonomerName_String();
    }
    
    @Test public void testSetMonomerType_String() {
        IMonomer oMonomer = builder.newMonomer();
        oMonomer.setMonomerType(new String("TRP"));
        Assert.assertEquals(new String("TRP"), oMonomer.getMonomerType());
    }
    @Test public void testGetMonomerType() {
        testSetMonomerType_String();
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test public void testToString() {
        IMonomer oMonomer = builder.newMonomer();
        oMonomer.setMonomerType(new String("TRP"));
        String description = oMonomer.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }
    }

    @Test public void testClone() throws Exception {
        IMonomer oMonomer = builder.newMonomer();
        Object clone = oMonomer.clone();
        Assert.assertTrue(clone instanceof IMonomer);
        Assert.assertNotSame(oMonomer, clone);
    }
}
