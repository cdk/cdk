/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.layout;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;

import javax.vecmath.Point2d;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class IdentityTemplateLibraryTest {

    @Test
    public void decodeCoordinates() throws Exception {
        Point2d[] points = IdentityTemplateLibrary.decodeCoordinates("12.5, 5.5, 4, 2");
        assertThat(points.length, is(2));
        assertThat(points[0].x, closeTo(12.5, 0.01));
        assertThat(points[0].y, closeTo(5.5, 0.01));
        assertThat(points[1].x, closeTo(4, 0.01));
        assertThat(points[1].y, closeTo(2, 0.01));
    }

    @Test
    public void encodeCoordinates() throws Exception {
        Point2d[] points = new Point2d[]{new Point2d(12.5f, 5.5f), new Point2d(4f, 2f)};
        String str = IdentityTemplateLibrary.encodeCoordinates(points);
        assertThat(str, is("|(12.5,5.5,;4.0,2.0,)|"));

    }

    @Test
    public void encodeEntry() {
        String smiles = "CO";
        Point2d[] points = new Point2d[]{new Point2d(12.5f, 5.5f), new Point2d(4f, 2f)};
        String encoded = IdentityTemplateLibrary.encodeEntry(new SimpleEntry<String, Point2d[]>(smiles, points));
        Map.Entry<String, Point2d[]> entry = IdentityTemplateLibrary.decodeEntry(encoded);
        assertThat(encoded, is("CO |(12.5,5.5,;4.0,2.0,)|"));
    }

    @Test
    public void decodeEntry() {
        String encode = "CO 12.500, 5.500, 4.000, 2.000";
        Map.Entry<String, Point2d[]> entry = IdentityTemplateLibrary.decodeEntry(encode);
        assertThat(entry.getKey(), is("CO"));
        assertThat(entry.getValue(), is(new Point2d[]{new Point2d(12.5f, 5.5f), new Point2d(4f, 2f)}));
    }

    @Test
    public void assignEthanolNoEntry() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(new Atom("O"));
        container.addAtom(new Atom("C"));
        container.addAtom(new Atom("C"));
        container.getAtom(0).setImplicitHydrogenCount(0);
        container.getAtom(1).setImplicitHydrogenCount(0);
        container.getAtom(2).setImplicitHydrogenCount(0);
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(1, 2, IBond.Order.SINGLE);

        assertFalse(IdentityTemplateLibrary.empty().assignLayout(container));
    }

    @Test
    public void assignEthanol() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(new Atom("O"));
        container.addAtom(new Atom("C"));
        container.addAtom(new Atom("C"));
        container.getAtom(0).setImplicitHydrogenCount(0);
        container.getAtom(1).setImplicitHydrogenCount(0);
        container.getAtom(2).setImplicitHydrogenCount(0);
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(1, 2, IBond.Order.SINGLE);

        IdentityTemplateLibrary lib = IdentityTemplateLibrary.empty();
        lib.add(IdentityTemplateLibrary.decodeEntry("OCC 4, 5, 2, 3, 0, 1"));
        assertTrue(lib.assignLayout(container));
        assertThat(container.getAtom(0).getPoint2d().x, closeTo(4, 0.01));
        assertThat(container.getAtom(0).getPoint2d().y, closeTo(5, 0.01));
        assertThat(container.getAtom(1).getPoint2d().x, closeTo(2, 0.01));
        assertThat(container.getAtom(1).getPoint2d().y, closeTo(3, 0.01));
        assertThat(container.getAtom(2).getPoint2d().x, closeTo(0, 0.01));
        assertThat(container.getAtom(2).getPoint2d().y, closeTo(1, 0.01));
    }

    @Test
    public void store() throws IOException {
        IdentityTemplateLibrary lib = IdentityTemplateLibrary.empty();
        lib.add(IdentityTemplateLibrary.decodeEntry("[C][C][O] 0, 1, 2, 3, 4, 5"));
        lib.add(IdentityTemplateLibrary.decodeEntry("[C][C] 0, 1, 2, 3"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        lib.store(baos);
        baos.close();
        assertThat(new String(baos.toByteArray()),
                is("[C][C][O] |(.0,1.0,;2.0,3.0,;4.0,5.0,)|\n[C][C] |(.0,1.0,;2.0,3.0,)|\n"));
    }
}
