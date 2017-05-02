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

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.ringsearch.RingSearch;

import javax.vecmath.Point2d;
import java.util.Arrays;
import java.util.Map;

/**
 * Correct double-bond configuration depiction in 2D to be correct for it's
 * specified {@link org.openscience.cdk.interfaces.IDoubleBondStereochemistry}. Ideally double-bond adjustment
 * should be done in when generating a structure diagram (and consider
 * overlaps). This method finds double bonds with incorrect depicted
 * configuration and reflects one side to correct the configuration.
 * <b>IMPORTANT: should be invoked before labelling up/down bonds. Cyclic
 * double-bonds with a configuration can not be corrected (error logged).</b>
 *
 * @author John May
 * @cdk.module sdg
 */
final class CorrectGeometricConfiguration {

    /** The structure we are assigning labels to. */
    private final IAtomContainer      container;

    /** Adjacency list graph representation of the structure. */
    private final int[][]             graph;

    /** Lookup atom index (avoid IAtomContainer). */
    private final Map<IAtom, Integer> atomToIndex;

    /** Test if a bond is cyclic. */
    private final RingSearch          ringSearch;

    /** Visited flags when atoms are being reflected. */
    private final boolean[]           visited;

    /**
     * Adjust all double bond elements in the provided structure. <b>IMPORTANT:
     * up/down labels should be adjusted before adjust double-bond
     * configurations. coordinates are reflected by this method which can lead
     * to incorrect tetrahedral specification.</b>
     *
     * @param container the structure to adjust
     * @throws IllegalArgumentException an atom had unset coordinates
     */
    public static IAtomContainer correct(IAtomContainer container) {
        if (!Iterables.isEmpty(container.stereoElements())) new CorrectGeometricConfiguration(container);
        return container;
    }

    /**
     * Adjust all double bond elements in the provided structure.
     *
     * @param container the structure to adjust
     * @throws IllegalArgumentException an atom had unset coordinates
     */
    CorrectGeometricConfiguration(IAtomContainer container) {
        this(container, GraphUtil.toAdjList(container));
    }

    /**
     * Adjust all double bond elements in the provided structure.
     *
     * @param container the structure to adjust
     * @param graph     the adjacency list representation of the structure
     * @throws IllegalArgumentException an atom had unset coordinates
     */
    CorrectGeometricConfiguration(IAtomContainer container, int[][] graph) {
        this.container = container;
        this.graph = graph;
        this.visited = new boolean[graph.length];
        this.atomToIndex = Maps.newHashMapWithExpectedSize(container.getAtomCount());
        this.ringSearch = new RingSearch(container, graph);

        for (int i = 0; i < container.getAtomCount(); i++) {
            IAtom atom = container.getAtom(i);
            atomToIndex.put(atom, i);
            if (atom.getPoint2d() == null) throw new IllegalArgumentException("atom " + i + " had unset coordinates");
        }

        for (IStereoElement element : container.stereoElements()) {
            if (element instanceof IDoubleBondStereochemistry) {
                adjust((IDoubleBondStereochemistry) element);
            }
        }
    }

    /**
     * Adjust the configuration of the {@code dbs} element (if required).
     *
     * @param dbs double-bond stereochemistry element
     */
    private void adjust(IDoubleBondStereochemistry dbs) {

        IBond db = dbs.getStereoBond();
        IBond[] bonds = dbs.getBonds();

        IAtom left = db.getBegin();
        IAtom right = db.getEnd();

        int p = parity(dbs);
        int q = parity(getAtoms(left, bonds[0].getOther(left), right))
                * parity(getAtoms(right, bonds[1].getOther(right), left));

        // configuration is unspecified? then we add an unspecified bond.
        // note: IDoubleBondStereochemistry doesn't indicate this yet
        if (p == 0) {
            for (IBond bond : container.getConnectedBondsList(left))
                bond.setStereo(IBond.Stereo.NONE);
            for (IBond bond : container.getConnectedBondsList(right))
                bond.setStereo(IBond.Stereo.NONE);
            bonds[0].setStereo(IBond.Stereo.UP_OR_DOWN);
            return;
        }

        // configuration is already correct
        if (p == q) return;

        Arrays.fill(visited, false);
        visited[atomToIndex.get(left)] = true;

        // XXX: bad but correct layout
        if (ringSearch.cyclic(atomToIndex.get(left), atomToIndex.get(right))) {
            Arrays.fill(visited, true);
            for (int w : graph[atomToIndex.get(right)]) {
                reflect(w, db);
            }
            return;
        }

        for (int w : graph[atomToIndex.get(right)]) {
            if (!visited[w]) reflect(w, db);
        }
    }

    /**
     * Create an array of three atoms for a side of the double bond. This is
     * used to determine the 'winding' of one side of the double bond.
     *
     * @param focus       a double bonded atom
     * @param substituent the substituent we know the configuration of
     * @param otherFocus  the other focus (i.e. the atom focus is double bonded
     *                    to)
     * @return 3 atoms arranged as, substituent, other substituent and other
     *         focus. if the focus atom has an implicit hydrogen the other
     *         substituent is the focus.
     */
    private IAtom[] getAtoms(IAtom focus, IAtom substituent, IAtom otherFocus) {
        IAtom otherSubstituent = focus;
        for (int w : graph[atomToIndex.get(focus)]) {
            IAtom atom = container.getAtom(w);
            if (atom != substituent && atom != otherFocus) otherSubstituent = atom;
        }
        return new IAtom[]{substituent, otherSubstituent, otherFocus};
    }

    /**
     * Access the parity (odd/even) parity of the double bond configuration (
     * together/opposite).
     *
     * @param element double bond element
     * @return together = -1, opposite = +1
     */
    private static int parity(IDoubleBondStereochemistry element) {
        switch (element.getStereo()) {
            case TOGETHER:
                return -1;
            case OPPOSITE:
                return +1;
            default:
                return 0;
        }
    }

    /**
     * Determine the parity (odd/even) of the triangle formed by the 3 atoms.
     *
     * @param atoms array of 3 atoms
     * @return the parity of the triangle formed by 3 points, odd = -1, even =
     *         +1
     */
    private static int parity(IAtom[] atoms) {
        return parity(atoms[0].getPoint2d(), atoms[1].getPoint2d(), atoms[2].getPoint2d());
    }

    /**
     * Determine the parity of the triangle formed by the 3 coordinates a, b and
     * c.
     *
     * @param a point 1
     * @param b point 2
     * @param c point 3
     * @return the parity of the triangle formed by 3 points
     */
    private static int parity(Point2d a, Point2d b, Point2d c) {
        double det = (a.x - c.x) * (b.y - c.y) - (a.y - c.y) * (b.x - c.x);
        return (int) Math.signum(det);
    }

    /**
     * Reflect the atom at index {@code v} and any then reflect any unvisited
     * neighbors.
     *
     * @param v    index of the atom to reflect
     * @param bond bond
     */
    private void reflect(int v, IBond bond) {
        visited[v] = true;
        IAtom atom = container.getAtom(v);
        atom.setPoint2d(reflect(atom.getPoint2d(), bond));
        for (int w : graph[v]) {
            if (!visited[w]) reflect(w, bond);
        }
    }

    /**
     * Reflect the point {@code p} over the {@code bond}.
     *
     * @param p    the point to reflect
     * @param bond bond
     * @return the reflected point
     */
    private Point2d reflect(Point2d p, IBond bond) {
        IAtom a = bond.getBegin();
        IAtom b = bond.getEnd();
        return reflect(p, a.getPoint2d().x, a.getPoint2d().y, b.getPoint2d().x, b.getPoint2d().y);
    }

    /**
     * Reflect the point {@code p} in the line (x0,y0 - x1,y1).
     *
     * @param p  the point to reflect
     * @param x0 plane x start
     * @param y0 plane y end
     * @param x1 plane x start
     * @param y1 plane y end
     * @return the reflected point
     */
    private Point2d reflect(Point2d p, double x0, double y0, double x1, double y1) {

        double dx, dy, a, b;

        dx = (x1 - x0);
        dy = (y1 - y0);

        a = (dx * dx - dy * dy) / (dx * dx + dy * dy);
        b = 2 * dx * dy / (dx * dx + dy * dy);

        return new Point2d(a * (p.x - x0) + b * (p.y - y0) + x0, b * (p.x - x0) - a * (p.y - y0) + y0);
    }
}
