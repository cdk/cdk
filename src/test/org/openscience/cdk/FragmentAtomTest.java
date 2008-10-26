/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 2002-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IFragmentAtom;

/**
 * Checks the functionality of the FragmentAtom.
 *
 * @cdk.module test-data
 */
<<<<<<< HEAD:src/test/org/openscience/cdk/FragmentAtomTest.java
public class FragmentAtomTest extends PseudoAtomTest {
=======
public class FragmentAtomTest extends CDKTestCase {
>>>>>>> bbc19522071c1b78697779bddcd7509e9314667e:src/test/org/openscience/cdk/FragmentAtomTest.java

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    /**
     * Method to test the Atom(String symbol) method.
     */
    @Test public void testFragmentAtom() {
        IFragmentAtom a = builder.newFragmentAtom();
        Assert.assertNotNull(a);
    }
    
    @Test public void testGetFragment() {
    	IFragmentAtom a = builder.newFragmentAtom();
    	// make sure that we start with a not-null, but empty container
    	Assert.assertNotNull(a.getFragment());
    	Assert.assertEquals(0, a.getFragment().getAtomCount());
    	Assert.assertEquals(0, a.getFragment().getBondCount());
    }
    
    @Test public void testIsExpanded() {
    	IFragmentAtom a = builder.newFragmentAtom();
    	Assert.assertNotNull(a);
    	Assert.assertFalse(a.isExpanded()); // test the default state
    }
    
    @Test public void testSetExpanded_boolean() {
    	IFragmentAtom a = builder.newFragmentAtom();
    	Assert.assertNotNull(a);
        a.setExpanded(true);
        Assert.assertTrue(a.isExpanded());
        a.setExpanded(false);
        Assert.assertFalse(a.isExpanded());
    }
    
    @Test public void testSetFragment_IAtomContainer() {
    	IFragmentAtom a = builder.newFragmentAtom();
    	Assert.assertNotNull(a);
    	IAtomContainer container = builder.newAtomContainer();
    	container.addAtom(builder.newAtom("N"));
    	container.addAtom(builder.newAtom("C"));
    	container.addBond(0, 1, IBond.Order.TRIPLE);
    	a.setFragment(container);
    	Assert.assertEquals(container, a.getFragment());
    }
    
    @Test public void testGetExactMass() {
    	IFragmentAtom a = builder.newFragmentAtom();
    	Assert.assertNotNull(a);
    	IAtomContainer container = builder.newAtomContainer();
    	container.addAtom(builder.newAtom("N"));
    	container.getAtom(0).setExactMass(5.5);
    	container.addAtom(builder.newAtom("C"));
    	container.getAtom(1).setExactMass(3.5);
    	container.addBond(0, 1, IBond.Order.TRIPLE);
    	a.setFragment(container);
    	Assert.assertEquals(9.0, a.getExactMass(), 0.0001);
    }

    /** Test for RFC #9 */
    @Test public void testToString() {
        IFragmentAtom bond = builder.newFragmentAtom();
        String description = bond.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }
}
