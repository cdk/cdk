/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.layout;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;

import javax.vecmath.Point2d;

import static org.junit.Assert.assertEquals;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.OPPOSITE;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.TOGETHER;

/**
 * @author John May
 * @cdk.module test-sdg
 */
public class CorrectGeometricConfigurationTest {

    // C/C=C/CCC
    @Test
    public void cis() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("C", 3, -0.74d, 5.00d));
        m.addAtom(atom("C", 1, -1.49d, 3.70d));
        m.addAtom(atom("C", 1, -0.74d, 2.40d));
        m.addAtom(atom("C", 2, -1.49d, 1.10d));
        m.addAtom(atom("C", 3, -0.74d, -0.20d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addStereoElement(new DoubleBondStereochemistry(m.getBond(1), new IBond[]{m.getBond(0), m.getBond(2)},
                TOGETHER));
        CorrectGeometricConfiguration.correct(m);
        assertPoint(m.getAtom(0), -0.74, 5.0, 0.1);
        assertPoint(m.getAtom(1), -1.49, 3.7, 0.1);
        assertPoint(m.getAtom(2), -0.74, 2.4, 0.1);
        assertPoint(m.getAtom(3), 0.76, 2.4, 0.1);
        assertPoint(m.getAtom(4), 1.51, 1.10, 0.1);
    }

    // C/C=C\CCC
    @Test
    public void trans() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("C", 3, -0.74d, 5.00d));
        m.addAtom(atom("C", 1, -1.49d, 3.70d));
        m.addAtom(atom("C", 1, -0.74d, 2.40d));
        m.addAtom(atom("C", 2, 0.76d, 2.40d));
        m.addAtom(atom("C", 3, 1.51d, 1.10d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addStereoElement(new DoubleBondStereochemistry(m.getBond(1), new IBond[]{m.getBond(0), m.getBond(2)},
                OPPOSITE));
        CorrectGeometricConfiguration.correct(m);
        assertPoint(m.getAtom(0), -0.74, 5.0, 0.1);
        assertPoint(m.getAtom(1), -1.49, 3.7, 0.1);
        assertPoint(m.getAtom(2), -0.74d, 2.40d, 0.1);
        assertPoint(m.getAtom(3), -1.49d, 1.10d, 0.1);
        assertPoint(m.getAtom(4), -0.74d, -0.20d, 0.1);
    }

    static void assertPoint(IAtom a, double x, double y, double epsilon) {
        Point2d p = a.getPoint2d();
        assertEquals(p.x, x, epsilon);
        assertEquals(p.y, y, epsilon);
    }

    static IAtom atom(String symbol, int hCount, double x, double y) {
        IAtom a = new Atom(symbol);
        a.setImplicitHydrogenCount(hCount);
        a.setPoint2d(new Point2d(x, y));
        return a;
    }
}
