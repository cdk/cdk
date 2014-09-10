/*
 *  Copyright (C) 2009  Mark Rijnbeek <markrynbeek@gmail.com>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.tools.manipulator;

import java.util.Comparator;

import javax.vecmath.Point2d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Test class for {@link org.openscience.cdk.tools.manipulator.AtomContainerComparatorBy2DCenter}
 * @cdk.module test-standard
 */
public class AtomContainerComparatorBy2DCenterTest extends CDKTestCase {

    public AtomContainerComparatorBy2DCenterTest() {
        super();
    }

    @Test
    public void testCompare_Null_Null() {
        Comparator<IAtomContainer> comparator = new AtomContainerComparatorBy2DCenter();
        Assert.assertEquals("null <-> null", 0, comparator.compare(null, null));
    }

    @Test
    public void testCompare_Null_2DCoordinates() {
        IAtomContainer atomContainer = new AtomContainer();
        atomContainer.addAtom(new Atom("N"));
        Comparator<IAtomContainer> comparator = new AtomContainerComparatorBy2DCenter();
        Assert.assertEquals("null 2d Coords<-> null 2d coords", 0, comparator.compare(atomContainer, atomContainer));
    }

    @Test
    public void testCompare_self_valid_2DCoordinates() {
        IAtomContainer atomContainer = new AtomContainer();
        IAtom atom = new Atom("N");
        atom.setPoint2d(new Point2d(10, 10));
        atomContainer.addAtom(atom);

        Comparator<IAtomContainer> comparator = new AtomContainerComparatorBy2DCenter();
        Assert.assertEquals("self 2d Coords<-> self 2d coords", 0, comparator.compare(atomContainer, atomContainer));
    }

    @Test
    public void testCompare_minusOne() {
        IAtomContainer atomContainer = new AtomContainer();
        IAtom atom = new Atom("N");
        atom.setPoint2d(new Point2d(10, 10));
        atomContainer.addAtom(atom);

        IAtomContainer atomContainer2 = new AtomContainer();
        IAtom atom2 = new Atom("P");
        atom2.setPoint2d(new Point2d(20, 10));
        atomContainer2.addAtom(atom2);

        Comparator<IAtomContainer> comparator = new AtomContainerComparatorBy2DCenter();
        Assert.assertEquals("(10,10)<-> (20,10)", -1, comparator.compare(atomContainer, atomContainer2));
    }

    @Test
    public void testCompare_plusOne() {
        IAtomContainer atomContainer = new AtomContainer();
        IAtom atom = new Atom("N");
        atom.setPoint2d(new Point2d(20, 10));
        atomContainer.addAtom(atom);

        IAtomContainer atomContainer2 = new AtomContainer();
        IAtom atom2 = new Atom("P");
        atom2.setPoint2d(new Point2d(20, 5));
        atomContainer2.addAtom(atom2);

        Comparator<IAtomContainer> comparator = new AtomContainerComparatorBy2DCenter();
        Assert.assertEquals("(20,10)<-> (20,5)", 1, comparator.compare(atomContainer, atomContainer2));
    }

}
