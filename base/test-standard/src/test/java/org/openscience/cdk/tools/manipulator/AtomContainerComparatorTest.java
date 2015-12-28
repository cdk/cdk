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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IBond.Order;

/**
 * @cdk.module test-standard
 */
public class AtomContainerComparatorTest extends CDKTestCase {

    public AtomContainerComparatorTest() {
        super();
    }

    @Test
    public void testCompare_Null_IAtomContainer() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IRing cycloPentane = builder.newInstance(IRing.class, 5, "C");

        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        // Assert.assert correct comparison
        Assert.assertEquals("null <-> cycloPentane", 1, comparator.compare(null, cycloPentane));
    }

    @Test
    public void testCompare_Null_Null() {
        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        // Assert.assert correct comparison
        Assert.assertEquals("null <-> null", 0, comparator.compare(null, null));
    }

    @Test
    public void testCompare_Atom_PseudoAtom() {
        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        IAtomContainer atomContainer1 = new AtomContainer();

        atomContainer1.addAtom(new Atom("C"));

        IAtomContainer atomContainer2 = new AtomContainer();

        atomContainer2.addAtom(new PseudoAtom("*"));

        Assert.assertEquals(atomContainer1 + " <-> " + atomContainer2, 1,
                comparator.compare(atomContainer1, atomContainer2));
    }

    @Test
    public void testCompare_IAtomContainer_Null() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IRing cycloPentane = builder.newInstance(IRing.class, 5, "C");

        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        // Assert.assert correct comparison
        Assert.assertEquals("cycloPentane <-> null", -1, comparator.compare(cycloPentane, null));
    }

    @Test
    public void testCompare_RingSize() {
        // Create some IAtomContainers
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IRing cycloPentane = builder.newInstance(IRing.class, 5, "C");
        IRing cycloHexane = builder.newInstance(IRing.class, 6, "C");

        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        Assert.assertEquals("cycloPentane <-> cycloHexane", -1, comparator.compare(cycloPentane, cycloHexane));
        Assert.assertEquals("cycloPentane <-> cycloPentane", 0, comparator.compare(cycloPentane, cycloPentane));
        Assert.assertEquals("cycloHexane <-> cycloPentane", 1, comparator.compare(cycloHexane, cycloPentane));
    }

    @Test
    public void testCompare_Ring_NonRing() {
        // Create some IAtomContainers
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IRing cycloHexane = builder.newInstance(IRing.class, 6, "C");
        IAtomContainer hexaneNitrogen = builder.newInstance(IRing.class, 6, "N");
        hexaneNitrogen.removeBond(0);

        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        Assert.assertEquals("cycloHexane <-> hexaneNitrogen", -1, comparator.compare(cycloHexane, hexaneNitrogen));
        Assert.assertEquals("cycloHexane <-> cycloHexane", 0, comparator.compare(cycloHexane, cycloHexane));
        Assert.assertEquals("hexaneNitrogen <-> cycloHexane", 1, comparator.compare(hexaneNitrogen, cycloHexane));
    }

    @Test
    public void testCompare_Ring_NonRing2() {
        // Create some IAtomContainers
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer hexaneNitrogen = builder.newInstance(IRing.class, 6, "N");
        hexaneNitrogen.removeBond(0);
        IRing cycloHexaneNitrogen = builder.newInstance(IRing.class, 6, "N");

        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        Assert.assertEquals("hexaneNitrogen <-> cycloHexaneNitrogen", -1,
                comparator.compare(hexaneNitrogen, cycloHexaneNitrogen));
        Assert.assertEquals("hexaneNitrogen <-> hexaneNitrogen", 0, comparator.compare(hexaneNitrogen, hexaneNitrogen));
        Assert.assertEquals("cycloHexaneNitrogen <-> hexaneNitrogen", 1,
                comparator.compare(cycloHexaneNitrogen, hexaneNitrogen));
    }

    @Test
    public void testCompare_BondOrder() {
        // Create some IAtomContainers
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IRing cycloHexaneNitrogen = builder.newInstance(IRing.class, 6, "N");
        IRing cycloHexeneNitrogen = builder.newInstance(IRing.class, 6, "N");
        cycloHexeneNitrogen.getBond(0).setOrder(Order.DOUBLE);

        // Instantiate the comparator
        Comparator<IAtomContainer> comparator = new AtomContainerComparator();

        Assert.assertEquals("cycloHexaneNitrogen <-> cycloHexeneNitrogen", -1,
                comparator.compare(cycloHexaneNitrogen, cycloHexeneNitrogen));
        Assert.assertEquals("cycloHexaneNitrogen <-> cycloHexaneNitrogen", 0,
                comparator.compare(cycloHexaneNitrogen, cycloHexaneNitrogen));
        Assert.assertEquals("cycloHexeneNitrogen <-> cycloHexeneNitrogen", 0,
                comparator.compare(cycloHexeneNitrogen, cycloHexeneNitrogen));
        Assert.assertEquals("cycloHexeneNitrogen <-> cycloHexaneNitrogen", 1,
                comparator.compare(cycloHexeneNitrogen, cycloHexaneNitrogen));
    }

}
