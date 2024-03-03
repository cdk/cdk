/* Copyright (C) 2007  Andreas Schueller <archvile18@users.sf.net>
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
package org.openscience.cdk.tools.manipulator;

import java.util.Comparator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IBond.Order;

/**
 * @cdk.module test-standard
 */
class AtomContainerComparatorTest extends CDKTestCase {

    AtomContainerComparatorTest() {
        super();
    }

    @Test
    void testCompare_Null_IAtomContainer() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IRing cycloPentane = builder.newInstance(IRing.class, 5, "C");

        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        // Assert.assert correct comparison
        Assertions.assertEquals(1, comparator.compare(null, cycloPentane), "null <-> cycloPentane");
    }

    @Test
    void testCompare_Null_Null() {
        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        // Assert.assert correct comparison
        Assertions.assertEquals(0, comparator.compare(null, null), "null <-> null");
    }

    @Test
    void testCompare_Atom_PseudoAtom() {
        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        IAtomContainer atomContainer1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        atomContainer1.addAtom(new Atom("C"));

        IAtomContainer atomContainer2 = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        atomContainer2.addAtom(new PseudoAtom("*"));

        Assertions.assertEquals(1, comparator.compare(atomContainer1, atomContainer2), atomContainer1 + " <-> " + atomContainer2);
    }

    @Test
    void testCompare_IAtomContainer_Null() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IRing cycloPentane = builder.newInstance(IRing.class, 5, "C");

        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        // Assert.assert correct comparison
        Assertions.assertEquals(-1, comparator.compare(cycloPentane, null), "cycloPentane <-> null");
    }

    @Test
    void testCompare_RingSize() {
        // Create some IAtomContainers
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IRing cycloPentane = builder.newInstance(IRing.class, 5, "C");
        IRing cycloHexane = builder.newInstance(IRing.class, 6, "C");

        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        Assertions.assertEquals(-1, comparator.compare(cycloPentane, cycloHexane), "cycloPentane <-> cycloHexane");
        Assertions.assertEquals(0, comparator.compare(cycloPentane, cycloPentane), "cycloPentane <-> cycloPentane");
        Assertions.assertEquals(1, comparator.compare(cycloHexane, cycloPentane), "cycloHexane <-> cycloPentane");
    }

    @Test
    void testCompare_Ring_NonRing() {
        // Create some IAtomContainers
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IRing cycloHexane = builder.newInstance(IRing.class, 6, "C");
        IAtomContainer hexaneNitrogen = builder.newInstance(IRing.class, 6, "N");
        hexaneNitrogen.removeBond(0);

        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        Assertions.assertEquals(-1, comparator.compare(cycloHexane, hexaneNitrogen), "cycloHexane <-> hexaneNitrogen");
        Assertions.assertEquals(0, comparator.compare(cycloHexane, cycloHexane), "cycloHexane <-> cycloHexane");
        Assertions.assertEquals(1, comparator.compare(hexaneNitrogen, cycloHexane), "hexaneNitrogen <-> cycloHexane");
    }

    @Test
    void testCompare_Ring_NonRing2() {
        // Create some IAtomContainers
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer hexaneNitrogen = builder.newInstance(IRing.class, 6, "N");
        hexaneNitrogen.removeBond(0);
        IRing cycloHexaneNitrogen = builder.newInstance(IRing.class, 6, "N");

        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        Assertions.assertEquals(-1, comparator.compare(hexaneNitrogen, cycloHexaneNitrogen), "hexaneNitrogen <-> cycloHexaneNitrogen");
        Assertions.assertEquals(0, comparator.compare(hexaneNitrogen, hexaneNitrogen), "hexaneNitrogen <-> hexaneNitrogen");
        Assertions.assertEquals(1, comparator.compare(cycloHexaneNitrogen, hexaneNitrogen), "cycloHexaneNitrogen <-> hexaneNitrogen");
    }

    @Test
    void testCompare_BondOrder() {
        // Create some IAtomContainers
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IRing cycloHexaneNitrogen = builder.newInstance(IRing.class, 6, "N");
        IRing cycloHexeneNitrogen = builder.newInstance(IRing.class, 6, "N");
        cycloHexeneNitrogen.getBond(0).setOrder(Order.DOUBLE);

        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        Assertions.assertEquals(-1, comparator.compare(cycloHexaneNitrogen, cycloHexeneNitrogen), "cycloHexaneNitrogen <-> cycloHexeneNitrogen");
        Assertions.assertEquals(0, comparator.compare(cycloHexaneNitrogen, cycloHexaneNitrogen), "cycloHexaneNitrogen <-> cycloHexaneNitrogen");
        Assertions.assertEquals(0, comparator.compare(cycloHexeneNitrogen, cycloHexeneNitrogen), "cycloHexeneNitrogen <-> cycloHexeneNitrogen");
        Assertions.assertEquals(1, comparator.compare(cycloHexeneNitrogen, cycloHexaneNitrogen), "cycloHexeneNitrogen <-> cycloHexaneNitrogen");
    }

}
