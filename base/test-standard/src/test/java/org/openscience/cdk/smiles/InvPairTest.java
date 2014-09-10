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
package org.openscience.cdk.smiles;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;

/**
 * @cdk.module     test-standard
 */
public class InvPairTest extends CDKTestCase {

    public InvPairTest() {
        super();
    }

    @Test
    public void testInvPair() {
        InvPair pair = new InvPair();
        Assert.assertNotNull(pair);
    }

    @Test
    public void testInvPair_long_IAtom() {
        IAtom atom = new Atom(Elements.CARBON);
        InvPair pair = new InvPair(5l, atom);
        Assert.assertNotNull(pair);
        Assert.assertEquals(5l, pair.getCurr());
        Assert.assertEquals(atom, pair.getAtom());
    }

    @Test
    public void testEquals_Object() {
        IAtom atom = new Atom(Elements.CARBON);
        InvPair pair = new InvPair(5l, atom);
        Assert.assertEquals(pair, pair);
        Assert.assertNotSame("NotSame", pair);
        Assert.assertNotSame(new InvPair(), pair);
    }

    @Test
    public void testToString() {
        IAtom atom = new Atom(Elements.CARBON);
        InvPair pair = new InvPair(5l, atom);
        Assert.assertNotNull(pair.toString());
        Assert.assertTrue(pair.toString().length() > 0);
    }

    @Test
    public void testSetAtom_IAtom() {
        IAtom atom = new Atom(Elements.CARBON);
        InvPair pair = new InvPair();
        Assert.assertNotSame(atom, pair.getAtom());
        pair.setAtom(atom);
        Assert.assertEquals(atom, pair.getAtom());
    }

    @Test
    public void testGetAtom() {
        InvPair pair = new InvPair();
        Assert.assertNull(pair.getAtom());
        pair.setAtom(new Atom(Elements.CARBON));
        Assert.assertNotNull(pair.getAtom());
    }

    /**
     * @cdk.bug 2045574
     */
    @Test
    public void testGetPrime() {
        IAtom atom = new Atom(Elements.CARBON);
        InvPair pair = new InvPair(5l, atom);
        pair.setPrime();
        int prime = pair.getPrime();
        pair.setPrime();
        Assert.assertEquals("The prime should not change when curr is not changed", prime, pair.getPrime());
        pair.setCurr(6l);
        pair.setPrime();
        Assert.assertNotSame(prime, pair.getPrime());
    }

    @Test
    public void testSetPrime() {
        InvPair pair = new InvPair();
        try {
            pair.setPrime();
            Assert.fail("should have failed with an ArrayIndexOutOfBounds exception");
        } catch (Exception e) {
            // OK, is apparently expected to happen
        }
    }

    @Test
    public void testCommit() {
        IAtom atom = new Atom(Elements.CARBON);
        InvPair pair = new InvPair(5l, atom);
        pair.commit();
        Assert.assertNotNull(atom.getProperty(InvPair.CANONICAL_LABEL));
        Assert.assertEquals(5l, ((Long) atom.getProperty(InvPair.CANONICAL_LABEL)).longValue());
    }

    @Test
    public void testSetCurr_long() {
        IAtom atom = new Atom(Elements.CARBON);
        InvPair pair = new InvPair(5l, atom);
        Assert.assertEquals(5l, pair.getCurr());
        pair.setCurr(4l);
        Assert.assertEquals(4l, pair.getCurr());
    }

    @Test
    public void testGetCurr() {
        IAtom atom = new Atom(Elements.CARBON);
        InvPair pair = new InvPair(5l, atom);
        Assert.assertEquals(5l, pair.getCurr());
    }

    @Test
    public void testSetLast_long() {
        IAtom atom = new Atom(Elements.CARBON);
        InvPair pair = new InvPair(5l, atom);
        Assert.assertEquals(0l, pair.getLast());
        pair.setLast(4l);
        Assert.assertEquals(4l, pair.getLast());
    }

    @Test
    public void testGetLast() {
        IAtom atom = new Atom(Elements.CARBON);
        InvPair pair = new InvPair(5l, atom);
        Assert.assertEquals(0l, pair.getLast());
    }
}
