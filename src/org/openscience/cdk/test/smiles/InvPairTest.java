/* $Revision: 7635 $ $Author: egonw $ $Date: 2007-01-04 18:32:54 +0100 (Thu, 04 Jan 2007) $
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
package org.openscience.cdk.test.smiles;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.smiles.InvPair;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module     test-standard
 */
public class InvPairTest extends CDKTestCase {

    public InvPairTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(InvPairTest.class);
    }

    public void testInvPair() {
    	InvPair pair = new InvPair();
    	assertNotNull(pair);
    }
    
    public void testInvPair_long_IAtom() {
    	IAtom atom = new Atom(Elements.CARBON);
    	InvPair pair = new InvPair(5l, atom);
    	assertNotNull(pair);
    	assertEquals(5l, pair.getCurr());
    	assertEquals(atom, pair.getAtom());
    }
    
    public void testEquals_Object() {
    	IAtom atom = new Atom(Elements.CARBON);
    	InvPair pair = new InvPair(5l, atom);
    	assertTrue(pair.equals(pair));
    	assertFalse(pair.equals("NotSame"));
    	assertFalse(pair.equals(new InvPair()));
    }
    
    public void testToString() {
    	IAtom atom = new Atom(Elements.CARBON);
    	InvPair pair = new InvPair(5l, atom);
    	assertNotNull(pair.toString());
    	assertTrue(pair.toString().length() > 0);
    }
    
    public void testSetAtom_IAtom() {
    	IAtom atom = new Atom(Elements.CARBON);
    	InvPair pair = new InvPair();
    	assertNotSame(atom, pair.getAtom());
    	pair.setAtom(atom);
    	assertEquals(atom, pair.getAtom());
    }
    
    public void testGetAtom() {
    	InvPair pair = new InvPair();
    	assertNull(pair.getAtom());
    	pair.setAtom(new Atom(Elements.CARBON));
    	assertNotNull(pair.getAtom());
    }
    
    public void testGetPrime() {
    	IAtom atom = new Atom(Elements.CARBON);
    	InvPair pair = new InvPair(5l, atom);
    	pair.setPrime();
    	int prime = pair.getPrime();
    	pair.setPrime();
    	assertEquals("The prime should not change when curr is not changed",
    		prime, pair.getPrime());
    	pair.setCurr(6l);
    	assertNotSame(new Integer(prime), new Integer(pair.getPrime()));
    }
    
    public void testSetPrime() {
    	InvPair pair = new InvPair();
    	try {
    		pair.setPrime();
    		fail("should have failed with an ArrayIndexOutOfBounds exception");
    	} catch (Exception e) {
			// OK, is apparently expected to happen
		}
    }
    
    public void testComit() {
    	IAtom atom = new Atom(Elements.CARBON);
    	InvPair pair = new InvPair(5l, atom);
    	pair.comit();
    	assertNotNull(atom.getProperty(InvPair.CANONICAL_LABEL));
    	assertEquals(new Long(5l), (Long)atom.getProperty(InvPair.CANONICAL_LABEL));
    }
    
    public void testSetCurr_long() {
    	IAtom atom = new Atom(Elements.CARBON);
    	InvPair pair = new InvPair(5l, atom);
    	assertEquals(5l, pair.getCurr());
    	pair.setCurr(4l);
    	assertEquals(4l, pair.getCurr());
    }

    public void testGetCurr() {
    	IAtom atom = new Atom(Elements.CARBON);
    	InvPair pair = new InvPair(5l, atom);
    	assertEquals(5l, pair.getCurr());
    }

    public void testSetLast_long() {
    	IAtom atom = new Atom(Elements.CARBON);
    	InvPair pair = new InvPair(5l, atom);
    	assertEquals(0l, pair.getLast());
    	pair.setLast(4l);
    	assertEquals(4l, pair.getLast());
    }

    public void testGetLast() {
    	IAtom atom = new Atom(Elements.CARBON);
    	InvPair pair = new InvPair(5l, atom);
    	assertEquals(0l, pair.getLast());
    }
}
