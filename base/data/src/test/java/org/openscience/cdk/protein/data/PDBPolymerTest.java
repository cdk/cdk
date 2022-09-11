/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.protein.data;

import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.interfaces.AbstractPDBPolymerTest;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.interfaces.IPDBPolymer;
import org.openscience.cdk.interfaces.IStrand;

/**
 * Checks the functionality of the PDBPolymer class.
 *
 * @cdk.module test-data
 *
 * @see PDBPolymer
 */
class PDBPolymerTest extends AbstractPDBPolymerTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(PDBPolymer::new);
    }

    @Test
    void testPDBPolymer() {
        IPDBPolymer pdbPolymer = new PDBPolymer();
        Assertions.assertNotNull(pdbPolymer);
        Assertions.assertEquals(pdbPolymer.getMonomerCount(), 0);

        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IStrand oStrand2 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand2.setStrandName("B");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IMonomer oMono3 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono3.setMonomerName("GLYA16");
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom3 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom4 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom5 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");

        pdbPolymer.addAtom(oPDBAtom1);
        pdbPolymer.addAtom(oPDBAtom2, oStrand1);
        pdbPolymer.addAtom(oPDBAtom3, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom4, oMono2, oStrand2);
        pdbPolymer.addAtom(oPDBAtom5, oMono3, oStrand2);
        Assertions.assertNotNull(pdbPolymer.getAtom(0));
        Assertions.assertNotNull(pdbPolymer.getAtom(1));
        Assertions.assertNotNull(pdbPolymer.getAtom(2));
        Assertions.assertNotNull(pdbPolymer.getAtom(3));
        Assertions.assertNotNull(pdbPolymer.getAtom(4));
        Assertions.assertEquals(oPDBAtom1, pdbPolymer.getAtom(0));
        Assertions.assertEquals(oPDBAtom2, pdbPolymer.getAtom(1));
        Assertions.assertEquals(oPDBAtom3, pdbPolymer.getAtom(2));
        Assertions.assertEquals(oPDBAtom4, pdbPolymer.getAtom(3));
        Assertions.assertEquals(oPDBAtom5, pdbPolymer.getAtom(4));

        Assertions.assertNull(pdbPolymer.getMonomer("0815", "A"));
        Assertions.assertNull(pdbPolymer.getMonomer("0815", "B"));
        Assertions.assertNull(pdbPolymer.getMonomer("0815", ""));
        Assertions.assertNull(pdbPolymer.getStrand(""));
        Assertions.assertNotNull(pdbPolymer.getMonomer("TRP279", "A"));
        Assertions.assertEquals(oMono1, pdbPolymer.getMonomer("TRP279", "A"));
        Assertions.assertEquals(pdbPolymer.getMonomer("TRP279", "A").getAtomCount(), 1);
        Assertions.assertNotNull(pdbPolymer.getMonomer("HOH", "B"));
        Assertions.assertEquals(oMono2, pdbPolymer.getMonomer("HOH", "B"));
        Assertions.assertEquals(pdbPolymer.getMonomer("HOH", "B").getAtomCount(), 1);
        Assertions.assertEquals(pdbPolymer.getStrand("B").getAtomCount(), 2);
        Assertions.assertEquals(pdbPolymer.getStrand("B").getMonomerCount(), 2);
        Assertions.assertNull(pdbPolymer.getStrand("C"));
        Assertions.assertNotNull(pdbPolymer.getStrand("B"));
    }

    @Test
    void testGetMonomerNamesInSequentialOrder() {
        PDBPolymer pdbPolymer = new PDBPolymer();
        Assertions.assertEquals(0, pdbPolymer.getMonomerNames().size());

        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("CYS280");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom3 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom2, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom3, oMono2, oStrand1);
        Assertions.assertNotNull(pdbPolymer.getAtom(0));
        Assertions.assertNotNull(pdbPolymer.getAtom(1));
        Assertions.assertEquals(oPDBAtom2, pdbPolymer.getAtom(0));
        Assertions.assertEquals(oPDBAtom3, pdbPolymer.getAtom(1));

        Iterator<String> monomers = pdbPolymer.getMonomerNamesInSequentialOrder().iterator();
        Assertions.assertEquals("TRP279", monomers.next());
        Assertions.assertEquals("CYS280", monomers.next());
    }

}
