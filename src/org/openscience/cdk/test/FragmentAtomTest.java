/* $Revision: 7037 $ $Author: egonw $ $Date: 2006-09-24 11:13:13 +0200 (Sun, 24 Sep 2006) $    
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
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.FragmentAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Checks the functionality of the FragmentAtom.
 *
 * @cdk.module test-data
 */
public class FragmentAtomTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public FragmentAtomTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(FragmentAtomTest.class);
    }

    /**
     * Method to test the Atom(String symbol) method.
     */
    public void testFragmentAtom() {
        FragmentAtom a = new FragmentAtom();
        assertNotNull(a);
    }
    
    public void testGetFragment() {
    	FragmentAtom a = new FragmentAtom();
    	// make sure that we start with a not-null, but empty container
        assertNotNull(a.getFragment());
        assertEquals(0, a.getFragment().getAtomCount());
        assertEquals(0, a.getFragment().getBondCount());
    }
    
    public void testIsExpanded() {
    	FragmentAtom a = new FragmentAtom();
        assertNotNull(a);
        assertFalse(a.isExpanded()); // test the default state
    }
    
    public void testSetExpanded_boolean() {
    	FragmentAtom a = new FragmentAtom();
        assertNotNull(a);
        a.setExpanded(true);
        assertTrue(a.isExpanded());
        a.setExpanded(false);
        assertFalse(a.isExpanded());
    }
    
    public void testSetFragment_IAtomContainer() {
    	FragmentAtom a = new FragmentAtom();
        assertNotNull(a);
    	IAtomContainer container = new AtomContainer();
    	container.addAtom(new Atom("N"));
    	container.addAtom(new Atom("C"));
    	container.addBond(0, 1, 3);
    	a.setFragment(container);
    	assertEquals(container, a.getFragment());
    }
    

}
