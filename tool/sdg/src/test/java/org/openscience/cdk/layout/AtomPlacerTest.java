/* Copyright (C) 2011 Gilleain Torrance <gilleain.torrance@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.layout;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.AtomContainer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

/**
 * @author maclean
 * @cdk.module test-sdg
 */
public class AtomPlacerTest extends CDKTestCase {

    @Test
    public void emptyAtomsListTest() {
        List<IAtom> atoms = new ArrayList<IAtom>();
        // switch on debugging, to see if NPE is thrown
        AtomPlacer placer = new AtomPlacer();
        boolean npeThrown = false;
        try {
            placer.populatePolygonCorners(atoms, new Point2d(0, 0), 0, 10, 10);
        } catch (NullPointerException npe) {
            npeThrown = true;
        }
        Assert.assertFalse("Null pointer for empty atoms list", npeThrown);
    }

    @Test
    public void triangleTest() {
        List<IAtom> atoms = new ArrayList<IAtom>();
        atoms.add(new Atom("C"));
        atoms.add(new Atom("C"));
        atoms.add(new Atom("C"));
        AtomPlacer placer = new AtomPlacer();
        placer.populatePolygonCorners(atoms, new Point2d(0, 0), 0, 10, 10);
        for (IAtom atom : atoms) {
            Assert.assertNotNull(atom.getPoint2d());
        }
    }

    @Test
    public void cumulated_x2() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("C", 3));
        m.addAtom(atom("C", 1));
        m.addAtom(atom("C", 0));
        m.addAtom(atom("C", 1));
        m.addAtom(atom("C", 3));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.getAtom(0).setPoint2d(new Point2d(0, 0));
        m.getAtom(0).setFlag(CDKConstants.ISPLACED, true);

        AtomPlacer atomPlacer = new AtomPlacer();
        atomPlacer.setMolecule(m);
        atomPlacer.placeLinearChain(m, new Vector2d(0, 1.5), 1.5);

        Point2d p1 = m.getAtom(1).getPoint2d();
        Point2d p2 = m.getAtom(2).getPoint2d();
        Point2d p3 = m.getAtom(3).getPoint2d();

        Vector2d p2p1 = new Vector2d(p1.x - p2.x, p1.y - p2.y);
        Vector2d p2p3 = new Vector2d(p3.x - p2.x, p3.y - p2.y);

        p2p1.normalize();
        p2p3.normalize();

        double theta = Math.acos(p2p1.x * p2p3.x + p2p1.y * p2p3.y);

        assertThat(theta, closeTo(Math.PI, 0.05));
    }

    @Test
    public void cumulated_x3() {
        IAtomContainer m = new AtomContainer(6, 4, 0, 0);
        m.addAtom(atom("C", 3));
        m.addAtom(atom("C", 1));
        m.addAtom(atom("C", 0));
        m.addAtom(atom("C", 0));
        m.addAtom(atom("C", 1));
        m.addAtom(atom("C", 3));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.DOUBLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.getAtom(0).setPoint2d(new Point2d(0, 0));
        m.getAtom(0).setFlag(CDKConstants.ISPLACED, true);

        AtomPlacer atomPlacer = new AtomPlacer();
        atomPlacer.setMolecule(m);
        atomPlacer.placeLinearChain(m, new Vector2d(0, 1.5), 1.5);

        Point2d p1 = m.getAtom(1).getPoint2d();
        Point2d p2 = m.getAtom(2).getPoint2d();
        Point2d p3 = m.getAtom(3).getPoint2d();
        Point2d p4 = m.getAtom(4).getPoint2d();

        Vector2d p2p1 = new Vector2d(p1.x - p2.x, p1.y - p2.y);
        Vector2d p2p3 = new Vector2d(p3.x - p2.x, p3.y - p2.y);
        Vector2d p3p2 = new Vector2d(p2.x - p3.x, p2.y - p3.y);
        Vector2d p3p4 = new Vector2d(p4.x - p3.x, p4.y - p3.y);

        p2p1.normalize();
        p2p3.normalize();
        p3p2.normalize();
        p3p4.normalize();

        assertThat(Math.acos(p2p1.x * p2p3.x + p2p1.y * p2p3.y), closeTo(Math.PI, 0.05));
        assertThat(Math.acos(p3p2.x * p3p4.x + p3p2.y * p3p4.y), closeTo(Math.PI, 0.05));

    }

    static IAtom atom(String symbol, int hCount) {
        IAtom a = new Atom(symbol);
        a.setImplicitHydrogenCount(hCount);
        return a;
    }
}
