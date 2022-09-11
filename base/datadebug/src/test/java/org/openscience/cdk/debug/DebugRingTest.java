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
package org.openscience.cdk.debug;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.interfaces.AbstractRingTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRing;

/**
 * Checks the functionality of the {@link DebugRing}.
 *
 * @cdk.module test-datadebug
 */
class DebugRingTest extends AbstractRingTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(DebugRing::new);
    }

    @Test
    void testDebugRing_int_String() {
        IRing r = new DebugRing(5, "C");
        Assertions.assertEquals(5, r.getAtomCount());
        Assertions.assertEquals(5, r.getBondCount());
    }

    @Test
    void testDebugRing_int() {
        IRing r = new DebugRing(5);
        Assertions.assertEquals(0, r.getAtomCount());
        Assertions.assertEquals(0, r.getBondCount());
    }

    @Test
    void testDebugRing() {
        IRing ring = new DebugRing();
        Assertions.assertNotNull(ring);
        Assertions.assertEquals(0, ring.getAtomCount());
        Assertions.assertEquals(0, ring.getBondCount());
    }

    @Test
    void testDebugRing_IAtomContainer() {
        IAtomContainer container = newChemObject().getBuilder().newInstance(IAtomContainer.class);
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));

        IRing ring = new DebugRing(container);
        Assertions.assertNotNull(ring);
        Assertions.assertEquals(2, ring.getAtomCount());
        Assertions.assertEquals(0, ring.getBondCount());
    }
}
