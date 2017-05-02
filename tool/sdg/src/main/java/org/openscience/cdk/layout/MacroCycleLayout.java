/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
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

import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openscience.cdk.CDKConstants.ISPLACED;

/**
 * A class for helping layout macrocycles.
 */
final class MacroCycleLayout {

    // Macrocycle templates
    private static IdentityTemplateLibrary TEMPLATES            = IdentityTemplateLibrary.loadFromResource("macro.smi");

    // Hint for placing substituents
    public static  String                  MACROCYCLE_ATOM_HINT = "layout.macrocycle.atom.hint";

    // (counter)clockwise
    private static final int CW  = -1;
    private static final int CCW = +1;

    // molecule representations
    private final IAtomContainer mol;
    private final int[][]        adjList;
    private final Map<IAtom, Integer> idxs = new HashMap<>();

    /**
     * Create a new helper for the provided molecule.
     *
     * @param mol molecule
     */
    public MacroCycleLayout(IAtomContainer mol) {
        this.mol = mol;
        this.adjList = GraphUtil.toAdjList(mol);
        for (IAtom atom : mol.atoms())
            idxs.put(atom, idxs.size());
    }

    /**
     * Layout a macro cycle (the rest of the ring set is untouched).
     *
     * @param macrocycle the macrocycle
     * @param ringset    the ring set the macrocycle belongs to (may only be it's self)
     * @return layout was successfully, if false caller fall-back to regular polygons
     */
    boolean layout(IRing macrocycle, IRingSet ringset) {

        final IAtomContainer anon = roundUpIfNeeded(AtomContainerManipulator.anonymise(macrocycle));
        final Collection<Point2d[]> coords = TEMPLATES.getCoordinates(anon);

        if (coords.isEmpty())
            return false;

        Point2d[] best = new Point2d[anon.getAtomCount()];
        int bestOffset = selectCoords(coords, best, macrocycle, ringset);

        for (int i = 0; i < macrocycle.getAtomCount(); i++) {
            macrocycle.getAtom(i).setPoint2d(best[(bestOffset + i) % macrocycle.getAtomCount()]);
            macrocycle.getAtom(i).setFlag(ISPLACED, true);
            macrocycle.getAtom(i).setProperty(MACROCYCLE_ATOM_HINT, true);
        }
        macrocycle.setFlag(ISPLACED, true);

        return true;
    }

    /**
     * Select the best scoring template + offset for the given macrocycle.
     *
     * @param macrocycle macrocycle
     * @param ringset entire ring system
     * @param wind winding of ring CW/CCW
     * @param winding winding of each turn in the ring
     * @return the best scoring configuration
     */
    private MacroScore bestScore(IRing macrocycle, IRingSet ringset, int wind, int[] winding) {

        final int numAtoms = macrocycle.getAtomCount();

        List<Integer> heteroIdxs = new ArrayList<>();
        List<List<Integer>> ringAttachs = new ArrayList<>();

        // hetero atoms
        for (int i = 0; i < numAtoms; i++) {
            if (macrocycle.getAtom(i).getAtomicNumber() != 6)
                heteroIdxs.add(i);
        }
        for (IAtomContainer other : ringset.atomContainers()) {
            if (other == macrocycle)
                continue;
            IAtomContainer shared = AtomContainerManipulator.getIntersection(macrocycle, other);

            if (shared.getAtomCount() >= 2 && shared.getAtomCount() <= 4)
                ringAttachs.add(getAttachedInOrder(macrocycle, shared));
        }

        // convex and concave are relative
        final int convex = wind;
        final int concave = -wind;

        MacroScore best = null;

        for (int i = 0; i < winding.length; i++) {

            // score ring attachs
            int nRingClick = 0;
            for (List<Integer> ringAttach : ringAttachs) {
                int r1, r2, r3, r4;
                switch (ringAttach.size()) {
                    case 2:
                        r1 = (ringAttach.get(0) + i) % numAtoms;
                        r2 = (ringAttach.get(1) + i) % numAtoms;
                        if (winding[r1] == winding[r2]) {
                            if (winding[r1] == convex)
                                nRingClick += 5;
                            else
                                nRingClick++;
                        }
                        break;
                    case 3:
                        r1 = (ringAttach.get(0) + i) % numAtoms;
                        r2 = (ringAttach.get(1) + i) % numAtoms;
                        r3 = (ringAttach.get(2) + i) % numAtoms;
                        if (winding[r1] == convex &&
                            winding[r2] == concave &&
                            winding[r3] == convex)
                            nRingClick += 5;
                        else if (winding[r1] == concave &&
                                 winding[r2] == convex &&
                                 winding[r3] == concave)
                            nRingClick++;
                        break;
                    case 4:
                        r1 = (ringAttach.get(0) + i) % numAtoms;
                        r2 = (ringAttach.get(1) + i) % numAtoms;
                        r3 = (ringAttach.get(2) + i) % numAtoms;
                        r4 = (ringAttach.get(3) + i) % numAtoms;
                        if (winding[r1] == convex &&
                            winding[r2] == concave &&
                            winding[r3] == concave &&
                            winding[r4] == convex)
                            nRingClick++;
                        else if (winding[r1] == concave &&
                                 winding[r2] == convex &&
                                 winding[r3] == convex &&
                                 winding[r4] == concave)
                            nRingClick++;
                        break;
                }
            }

            // score hetero atoms in concave positions
            int nConcaveHetero = 0;
            for (int heteroIdx : heteroIdxs) {
                int k = (heteroIdx + i) % numAtoms;
                if (winding[k] == concave)
                    nConcaveHetero++;
            }

            MacroScore score = new MacroScore(i,
                                              nConcaveHetero,
                                              nRingClick);
            if (score.compareTo(best) < 0) {
                best = score;
            }
        }

        return best;
    }

    /**
     * Get the shared indices of a macrocycle and atoms shared with another ring.
     *
     * @param macrocycle macrocycle ring
     * @param shared shared atoms
     * @return the integers
     */
    private List<Integer> getAttachedInOrder(IRing macrocycle, IAtomContainer shared) {
        List<Integer> ringAttach = new ArrayList<>();
        Set<IAtom> visit = new HashSet<>();
        IAtom atom = shared.getAtom(0);
        while (atom != null) {
            visit.add(atom);
            ringAttach.add(macrocycle.indexOf(atom));
            List<IAtom> connected = shared.getConnectedAtomsList(atom);
            atom = null;
            for (IAtom neighbor : connected) {
                if (!visit.contains(neighbor)) {
                    atom = neighbor;
                    break;
                }
            }
        }
        return ringAttach;
    }

    /**
     * Select the best coordinates
     *
     * @param ps template points
     * @param coords best coordinates (updated by this method)
     * @param macrocycle the macrocycle
     * @param ringset rest of the ring system
     * @return offset into the coordinates
     */
    private int selectCoords(Collection<Point2d[]> ps, Point2d[] coords, IRing macrocycle, IRingSet ringset) {
        assert ps.size() != 0;
        final int[] winding = new int[coords.length];

        MacroScore best = null;
        for (Point2d[] p : ps) {
            final int wind = winding(p, winding);
            MacroScore score = bestScore(macrocycle, ringset, wind, winding);
            if (score.compareTo(best) < 0) {
                best = score;
                System.arraycopy(p, 0, coords, 0, p.length);
            }
        }

        // never null
        return best != null ? best.offset : 0;
    }

    /**
     * Determine the overall winding and the vertex of a ring template.
     *
     * @param coords ring coordinates
     * @param winding winding result for each atom (cw/ccw)
     * @return global winding
     */
    private static int winding(final Point2d[] coords, final int[] winding) {
        int cw = 0, ccw = 0;

        Point2d prev = coords[coords.length - 1];
        for (int i = 0; i < coords.length; i++) {
            Point2d curr = coords[i];
            Point2d next = coords[(i + 1) % coords.length];
            winding[i] = winding(prev, curr, next);

            if (winding[i] < 0)
                cw++;
            else if (winding[i] > 0)
                ccw++;
            else
                return 0;

            prev = curr;
        }

        if (cw == ccw)
            return 0;

        return cw > ccw ? CW : CCW;
    }

    /**
     * Determine the winding of three points using the determinant.
     *
     * @param a first point
     * @param b second point
     * @param c third point
     * @return < 0 = clockwise, 0 = linear, > 0 anti-clockwise
     */
    private static int winding(Point2d a, Point2d b, Point2d c) {
        return (int) Math.signum((b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x));
    }

    /**
     * Helper class for storing/ranking macrocycle templates.
     */
    private static final class MacroScore implements Comparable<MacroScore> {
        final int offset;
        final int nConcaveHetero;
        final int nRingClick;

        public MacroScore(int offset, int nConcaveHetero, int nRingClick) {
            this.offset = offset;
            this.nConcaveHetero = nConcaveHetero;
            this.nRingClick = nRingClick;
        }

        @Override
        public int compareTo(MacroScore o) {
            if (o == null)
                return -1;
            int cmp = 0;
            cmp = -Integer.compare(this.nRingClick, o.nRingClick);
            if (cmp != 0)
                return cmp;
            cmp = -Integer.compare(this.nConcaveHetero, o.nConcaveHetero);
            return cmp;
        }
    }

    /**
     * Make a ring one atom bigger if it's of an odd size.
     *
     * @param anon ring
     * @return 'anon' returned of chaining convenience
     */
    private static IAtomContainer roundUpIfNeeded(IAtomContainer anon) {
        IChemObjectBuilder bldr = anon.getBuilder();
        if ((anon.getAtomCount() & 0x1) != 0) {
            IBond bond = anon.removeBond(anon.getBondCount() - 1);
            IAtom dummy = bldr.newInstance(IAtom.class, "C");
            anon.addAtom(dummy);
            anon.addBond(bldr.newInstance(IBond.class, bond.getBegin(), dummy, IBond.Order.SINGLE));
            anon.addBond(bldr.newInstance(IBond.class, dummy, bond.getEnd(), IBond.Order.SINGLE));
        }
        return anon;
    }
}
