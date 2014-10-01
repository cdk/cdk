/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.AbstractRingTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the Ring class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.Ring
 */
public class RingTest extends AbstractRingTest {

    @BeforeClass
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new Ring();
            }
        });
    }

    @Test
    public void testRing_int_String() {
        IRing r = new Ring(5, "C");
        Assert.assertEquals(5, r.getAtomCount());
        Assert.assertEquals(5, r.getBondCount());
    }

    @Test
    public void testRing_int() {
        IRing r = new Ring(5); // This does not create a ring!
        Assert.assertEquals(0, r.getAtomCount());
        Assert.assertEquals(0, r.getBondCount());
    }

    @Test
    public void testRing() {
        IRing ring = new Ring();
        Assert.assertNotNull(ring);
        Assert.assertEquals(0, ring.getAtomCount());
        Assert.assertEquals(0, ring.getBondCount());
    }

    @Test
    public void testRing_IAtomContainer() {
        IAtomContainer container = new org.openscience.cdk.AtomContainer();
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));

        IRing ring = new Ring(container);
        Assert.assertNotNull(ring);
        Assert.assertEquals(2, ring.getAtomCount());
        Assert.assertEquals(0, ring.getBondCount());
    }

}
