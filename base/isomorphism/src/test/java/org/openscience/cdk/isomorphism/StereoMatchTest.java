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

package org.openscience.cdk.isomorphism;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Some simple isolated tests on functionality.
 *
 * @author John May
 * @cdk.module test-isomorphism
 */
public class StereoMatchTest {

    /* target does not have an element */
    @Test
    public void tetrahedral_missingInTarget() {
        IAtomContainer query = dimethylpropane();
        IAtomContainer target = dimethylpropane();
        query.addStereoElement(new TetrahedralChirality(query.getAtom(0), new IAtom[]{query.getAtom(1),
                query.getAtom(2), query.getAtom(3), query.getAtom(4)}, ITetrahedralChirality.Stereo.CLOCKWISE));
        assertFalse(new StereoMatch(query, target).apply(new int[]{0, 1, 2, 3, 4}));
    }

    /*
     * Query does not have an element but the target does - the query therefore
     * is a valid mapping.
     */
    @Test
    public void tetrahedral_missingInQuery() {
        IAtomContainer query = dimethylpropane();
        IAtomContainer target = dimethylpropane();
        target.addStereoElement(new TetrahedralChirality(target.getAtom(0), new IAtom[]{target.getAtom(1),
                target.getAtom(2), target.getAtom(3), target.getAtom(4)}, ITetrahedralChirality.Stereo.CLOCKWISE));
        assertTrue(new StereoMatch(query, target).apply(new int[]{0, 1, 2, 3, 4}));
    }

    @Test
    public void tetrahedral_match() {
        IAtomContainer query = dimethylpropane();
        IAtomContainer target = dimethylpropane();
        query.addStereoElement(new TetrahedralChirality(query.getAtom(0), new IAtom[]{query.getAtom(1),
                query.getAtom(2), query.getAtom(3), query.getAtom(4)}, ITetrahedralChirality.Stereo.CLOCKWISE));
        target.addStereoElement(new TetrahedralChirality(target.getAtom(0), new IAtom[]{target.getAtom(1),
                target.getAtom(2), target.getAtom(3), target.getAtom(4)}, ITetrahedralChirality.Stereo.CLOCKWISE));
        assertTrue(new StereoMatch(query, target).apply(new int[]{0, 1, 2, 3, 4}));
    }

    @Test
    public void tetrahedral_mismatch() {
        IAtomContainer query = dimethylpropane();
        IAtomContainer target = dimethylpropane();
        query.addStereoElement(new TetrahedralChirality(query.getAtom(0), new IAtom[]{query.getAtom(1),
                query.getAtom(2), query.getAtom(3), query.getAtom(4)}, ITetrahedralChirality.Stereo.CLOCKWISE));
        target.addStereoElement(new TetrahedralChirality(target.getAtom(0), new IAtom[]{target.getAtom(1),
                target.getAtom(2), target.getAtom(3), target.getAtom(4)}, ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        assertFalse(new StereoMatch(query, target).apply(new int[]{0, 1, 2, 3, 4}));
    }

    /*
     * Map to different atom order which means the clockwise and anticlockwise
     * match
     */
    @Test
    public void tetrahedral_match_swap() {
        IAtomContainer query = dimethylpropane();
        IAtomContainer target = dimethylpropane();
        query.addStereoElement(new TetrahedralChirality(query.getAtom(0), new IAtom[]{query.getAtom(1),
                query.getAtom(2), query.getAtom(3), query.getAtom(4)}, ITetrahedralChirality.Stereo.CLOCKWISE));
        target.addStereoElement(new TetrahedralChirality(target.getAtom(0), new IAtom[]{target.getAtom(1),
                target.getAtom(2), target.getAtom(3), target.getAtom(4)}, ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        assertTrue(new StereoMatch(query, target).apply(new int[]{0, 1, 3, 2, 4}));
    }

    /* These don't match because we don't map the atoms in order. */
    @Test
    public void tetrahedral_mismatch_swap() {
        IAtomContainer query = dimethylpropane();
        IAtomContainer target = dimethylpropane();
        query.addStereoElement(new TetrahedralChirality(query.getAtom(0), new IAtom[]{query.getAtom(1),
                query.getAtom(2), query.getAtom(3), query.getAtom(4)}, ITetrahedralChirality.Stereo.CLOCKWISE));
        target.addStereoElement(new TetrahedralChirality(target.getAtom(0), new IAtom[]{target.getAtom(1),
                target.getAtom(2), target.getAtom(3), target.getAtom(4)}, ITetrahedralChirality.Stereo.CLOCKWISE));
        assertFalse(new StereoMatch(query, target).apply(new int[]{0, 1, 3, 2, 4}));
    }

    @Test
    public void geometric_match_together() {
        IAtomContainer query = but2ene();
        IAtomContainer target = but2ene();
        query.addStereoElement(new DoubleBondStereochemistry(query.getBond(0), new IBond[]{query.getBond(1),
                query.getBond(2)}, IDoubleBondStereochemistry.Conformation.TOGETHER));
        target.addStereoElement(new DoubleBondStereochemistry(target.getBond(0), new IBond[]{target.getBond(1),
                target.getBond(2)}, IDoubleBondStereochemistry.Conformation.TOGETHER));
        assertTrue(new StereoMatch(query, target).apply(new int[]{0, 1, 2, 3}));
    }

    @Test
    public void geometric_match_opposite() {
        IAtomContainer query = but2ene();
        IAtomContainer target = but2ene();
        query.addStereoElement(new DoubleBondStereochemistry(query.getBond(0), new IBond[]{query.getBond(1),
                query.getBond(2)}, IDoubleBondStereochemistry.Conformation.OPPOSITE));
        target.addStereoElement(new DoubleBondStereochemistry(target.getBond(0), new IBond[]{target.getBond(1),
                target.getBond(2)}, IDoubleBondStereochemistry.Conformation.OPPOSITE));
        assertTrue(new StereoMatch(query, target).apply(new int[]{0, 1, 2, 3}));
    }

    @Test
    public void geometric_mismatch_together() {
        IAtomContainer query = but2ene();
        IAtomContainer target = but2ene();
        query.addStereoElement(new DoubleBondStereochemistry(query.getBond(0), new IBond[]{query.getBond(1),
                query.getBond(2)}, IDoubleBondStereochemistry.Conformation.TOGETHER));
        target.addStereoElement(new DoubleBondStereochemistry(target.getBond(0), new IBond[]{target.getBond(1),
                target.getBond(2)}, IDoubleBondStereochemistry.Conformation.OPPOSITE));
        assertFalse(new StereoMatch(query, target).apply(new int[]{0, 1, 2, 3}));
    }

    @Test
    public void geometric_mismatch_opposite() {
        IAtomContainer query = but2ene();
        IAtomContainer target = but2ene();
        query.addStereoElement(new DoubleBondStereochemistry(query.getBond(0), new IBond[]{query.getBond(1),
                query.getBond(2)}, IDoubleBondStereochemistry.Conformation.OPPOSITE));
        target.addStereoElement(new DoubleBondStereochemistry(target.getBond(0), new IBond[]{target.getBond(1),
                target.getBond(2)}, IDoubleBondStereochemistry.Conformation.TOGETHER));
        assertFalse(new StereoMatch(query, target).apply(new int[]{0, 1, 2, 3}));
    }

    @Test
    public void geometric_missingInQuery() {
        IAtomContainer query = but2ene();
        IAtomContainer target = but2ene();
        target.addStereoElement(new DoubleBondStereochemistry(target.getBond(0), new IBond[]{target.getBond(1),
                target.getBond(2)}, IDoubleBondStereochemistry.Conformation.TOGETHER));
        assertTrue(new StereoMatch(query, target).apply(new int[]{0, 1, 2, 3}));
    }

    @Test
    public void geometric_missingInTarget() {
        IAtomContainer query = but2ene();
        IAtomContainer target = but2ene();
        query.addStereoElement(new DoubleBondStereochemistry(query.getBond(0), new IBond[]{query.getBond(1),
                query.getBond(2)}, IDoubleBondStereochemistry.Conformation.OPPOSITE));
        assertFalse(new StereoMatch(query, target).apply(new int[]{0, 1, 2, 3}));
    }

    static IAtomContainer dimethylpropane() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(atom("C", 0));
        container.addAtom(atom("C", 3));
        container.addAtom(atom("C", 3));
        container.addAtom(atom("C", 3));
        container.addAtom(atom("C", 3));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(0, 2, IBond.Order.SINGLE);
        container.addBond(0, 3, IBond.Order.SINGLE);
        container.addBond(0, 4, IBond.Order.SINGLE);
        return container;
    }

    static IAtomContainer but2ene() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(atom("C", 1));
        container.addAtom(atom("C", 1));
        container.addAtom(atom("C", 3));
        container.addAtom(atom("C", 3));
        container.addBond(0, 1, IBond.Order.DOUBLE);
        container.addBond(0, 2, IBond.Order.SINGLE);
        container.addBond(1, 3, IBond.Order.SINGLE);
        return container;
    }

    static IAtom atom(String symbol, int hCount) {
        IAtom atom = new Atom(symbol);
        atom.setImplicitHydrogenCount(hCount);
        return atom;
    }
}
