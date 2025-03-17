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
import org.openscience.cdk.test.interfaces.AbstractStrandTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IStrand;

/**
 * Checks the functionality of the {@link DebugStrand}.
 *
 */
class DebugStrandTest extends AbstractStrandTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(DebugStrand::new);
    }

    @Test
    void testDebugStrand() {
        IStrand oStrand = new DebugStrand();
        Assertions.assertNotNull(oStrand);
        Assertions.assertEquals(oStrand.getMonomerCount(), 0);

        IMonomer oMono1 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IMonomer oMono3 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono3.setMonomerName("GLYA16");
        IAtom oAtom1 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom4 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom5 = oStrand.getBuilder().newInstance(IAtom.class, "C");

        oStrand.addAtom(oAtom1);
        oStrand.addAtom(oAtom2);
        oStrand.addAtom(oAtom3, oMono1);
        oStrand.addAtom(oAtom4, oMono2);
        oStrand.addAtom(oAtom5, oMono3);
        Assertions.assertNotNull(oStrand.getAtom(0));
        Assertions.assertNotNull(oStrand.getAtom(1));
        Assertions.assertNotNull(oStrand.getAtom(2));
        Assertions.assertNotNull(oStrand.getAtom(3));
        Assertions.assertNotNull(oStrand.getAtom(4));
        Assertions.assertEquals(oAtom1, oStrand.getAtom(0));
        Assertions.assertEquals(oAtom2, oStrand.getAtom(1));
        Assertions.assertEquals(oAtom3, oStrand.getAtom(2));
        Assertions.assertEquals(oAtom4, oStrand.getAtom(3));
        Assertions.assertEquals(oAtom5, oStrand.getAtom(4));

        Assertions.assertNull(oStrand.getMonomer("0815"));
        Assertions.assertNotNull(oStrand.getMonomer(""));
        Assertions.assertNotNull(oStrand.getMonomer("TRP279"));
        Assertions.assertEquals(oMono1, oStrand.getMonomer("TRP279"));
        Assertions.assertEquals(oStrand.getMonomer("TRP279").getAtomCount(), 1);
        Assertions.assertNotNull(oStrand.getMonomer("HOH"));
        Assertions.assertEquals(oMono2, oStrand.getMonomer("HOH"));
        Assertions.assertEquals(oStrand.getMonomer("HOH").getAtomCount(), 1);
        Assertions.assertEquals(oStrand.getMonomer("").getAtomCount(), 2);
        Assertions.assertEquals(oStrand.getAtomCount(), 5);
        Assertions.assertEquals(oStrand.getMonomerCount(), 3);
    }
}
