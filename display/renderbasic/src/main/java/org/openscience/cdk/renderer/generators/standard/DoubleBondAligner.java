/*  Copyright (C) 2014, 2026 John Mayfield
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
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
package org.openscience.cdk.renderer.generators.standard;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openscience.cdk.interfaces.IBond.Order.DOUBLE;
import static org.openscience.cdk.renderer.generators.standard.VecmathUtil.newPerpendicularVector;
import static org.openscience.cdk.renderer.generators.standard.VecmathUtil.newUnitVector;

/**
 * Internal: Algorithms for automatic alignment of double bonds in depiction.
 * <p/>
 * Used by the {@link StandardBondGenerator}, which presumes a double bond is
 * centered by default.
 *
 * @author John Mayfield
 */
final class DoubleBondAligner {

    private final BondComparator comparator;
    private final AtomSymbol[] symbols;

    // do not allow instance creation
    DoubleBondAligner(IAtomContainer container,
                      AtomSymbol[] symbols) {
        this.comparator =  new BondComparator(container);
        this.symbols = symbols;
    }

    /**
     * Determine the alignment of a double bond.
     *
     * @param bond the bond to determine
     * @return the position
     */
    DoubleBondAlign alignment(IBond bond) {

        IAtomContainer container = bond.getContainer();
        if (container == null)
            throw new IllegalArgumentException("Bond is not in a container!");

        if (bond.getOrder() != DOUBLE ||
            bond.getDisplay() == IBond.Display.Crossed ||
            bond.getDisplay() == IBond.Display.Wavy)
            return DoubleBondAlign.Center;

        // acyclic bonds with a symbol at both ends
        if (!bond.isInRing() &&
            symbols[bond.getBegin().getIndex()] != null &&
            symbols[bond.getEnd().getIndex()] != null)
            return DoubleBondAlign.Center;

        List<IBond> bonds1 = container.getConnectedBondsList(bond.getBegin());
        List<IBond> bonds2 = container.getConnectedBondsList(bond.getEnd());
        bonds1.remove(bond);
        bonds2.remove(bond);

        // terminal double bond, i.e. sticking out of a ring
        if (bonds1.isEmpty() && bonds2.size() != 1)
            return DoubleBondAlign.Center;
        if (bonds2.isEmpty() && bonds1.size() != 1)
            return DoubleBondAlign.Center;

        // acyclic and one end has only one other bond this determines which
        // side we go
        if (!bond.isInRing()) {
            if (bonds1.size() == 1 && bonds2.size() != 1) {
                return winding(bonds1.get(0), bond) <= 0 ? DoubleBondAlign.Right
                                                         : DoubleBondAlign.Left;
            } else if (bonds2.size() == 1) {
                return winding(bonds2.get(0), bond) <= 0 ? DoubleBondAlign.Left
                                                         : DoubleBondAlign.Right;
            } else if (bonds1.size() > 1 && bonds2.size() > 1)
                return DoubleBondAlign.Center;
        }

        // Macrocycle -> we place the bond inside the polygon of the ring
        if (bond.isInRing() && Cycles.smallRingSize(bond, 8) == 0) {
            Path2D path = comparator.getRingInfo(bond).getAwtPath();
            Point2d p1 = bond.getBegin().getPoint2d();
            Point2d p2 = bond.getEnd().getPoint2d();
            final Vector2d unit = newUnitVector(p1, p2);
            Vector2d perp = newPerpendicularVector(unit);
            perp.scale(0.1);
            Point2d mid = VecmathUtil.midpoint(p1, p2);
            if (path.contains(new Point2D.Double(mid.x + perp.x,
                                                 mid.y + perp.y))) {
                return DoubleBondAlign.Left;
            } else {
                assert path.contains(new Point2D.Double(mid.x - perp.x,
                                                        mid.y - perp.y));
                return DoubleBondAlign.Right;
            }
        }

        // pick the best adjacent bond based on the ring preferences
        IBond best = null;
        for (IBond b : bonds1)
            if (comparator.compare(b, best) < 0)
                best = b;
        for (IBond b : bonds2)
            if (comparator.compare(b, best) < 0)
                best = b;

        assert best != null;

        return winding(best, bond) <= 0 ? DoubleBondAlign.Right
                                        : DoubleBondAlign.Left;
    }

    /**
     * This help class is used to sort rings by their priority to help decided
     * where a double bond is positioned.
     */
    private static final class RingInfo implements Comparable<RingInfo> {

        private final int size;
        private int numDouble;
        private int numMetals;
        private int numHetero;
        private final IRing ring;
        private Path2D polygon;

        public RingInfo(IRing ring) {
            this.ring = ring;
            this.size = ring.getBondCount();
            init(ring);
        }

        /**
         * Get the AWT Path2D polygon for this ring.
         * @return the path
         */
        Path2D getAwtPath() {
            if (polygon == null) {
                polygon = new Path2D.Double();
                polygon.moveTo(ring.getAtom(0).getPoint2d().x,
                               ring.getAtom(0).getPoint2d().y);
                for (int i = 1; i < ring.getAtomCount(); i++) {
                    Point2d p = ring.getAtom(i).getPoint2d();
                    polygon.lineTo(p.x, p.y);
                }
                polygon.closePath();
            }
            return polygon;
        }

        /**
         * Initialize
         *
         * @param ring the ring
         */
        void init(IRing ring) {
            for (IBond bond : ring.bonds()) {
                if (bond.getOrder() == DOUBLE)
                    numDouble++;
            }
            for (IAtom atom : ring.atoms()) {
                if (Elements.isMetal(atom))
                    numMetals++;
                if (atom.getAtomicNumber() != IElement.C)
                    numHetero++;
            }
        }

        int getSizeRank() {
            // 6 < 5 < 7 < 4 < 3 < rest
            switch (size) {
                case 6:
                    return 0;
                case 5:
                    return 1;
                case 7:
                    return 2;
                case 4:
                    return 3;
                case 3:
                    return 4;
                default:
                    return size;
            }
        }

        @Override
        public int compareTo(RingInfo o) {
            // 1. fewer metals
            int cmp = Integer.compare(numMetals, o.numMetals);
            if (cmp != 0)
                return cmp;
            // 2. size preference
            cmp = Integer.compare(getSizeRank(), o.getSizeRank());
            if (cmp != 0)
                return cmp;
            // 3. more double bonds
            cmp = -Integer.compare(numDouble, o.numDouble);
            if (cmp != 0)
                return cmp;
            // 4. less hetero
            return Integer.compare(numHetero, o.numHetero);
        }

        public Iterable<IBond> bonds() {
            return ring.bonds();
        }

        @Override
        public String toString() {
            return "RingInfo{" +
                   "size=" + size +
                   ", num_double=" + numDouble +
                   ", num_metal=" + numMetals +
                   ", num_hetero=" + numHetero +
                   '}';
        }
    }

    /**
     * A comparator sorts bonds to determine how we place the second line of
     * a double bond (DoublerPosition). Ring bonds are preferred over chain
     * bonds and then the {@link RingInfo} is used to split ties.
     */
    static final class BondComparator implements Comparator<IBond> {

        private final IAtomContainer mol;
        private Map<IBond, RingInfo> ringInfo;


        BondComparator(IAtomContainer mol) {
            this.mol = mol;
        }

        /**
         * Lazily get the ring info for a given. This is the highest priority
         * ring (based on the internal ordering of RingInfo).
         *
         * @param b the bond
         * @return the ring info
         */
        RingInfo getRingInfo(IBond b) {
            if (ringInfo == null) {
                ringInfo = new HashMap<>();
                List<RingInfo> tmp = new ArrayList<>();
                IRingSet ringSet = Cycles.edgeShort(mol).toRingSet();
                for (IAtomContainer ring : ringSet)
                    tmp.add(new RingInfo((IRing) ring));
                Collections.sort(tmp);
                for (RingInfo ringPref : tmp) {
                    for (IBond bond : ringPref.bonds())
                        ringInfo.putIfAbsent(bond, ringPref);
                }
            }
            return ringInfo.get(b);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(IBond a, IBond b) {
            // non-null is preferred to null (obviously)
            if (a != null && b == null)
                return -1;
            else if (a == null && b != null)
                return +1;
            assert a != null;
            // ring bonds preferred to chain bonds
            if (a.isInRing() && !b.isInRing())
                return -1;
            else if (!a.isInRing() && b.isInRing())
                return +1;
            else if (!a.isInRing() && !b.isInRing())
                return 0;
            // we need to actually check some stats
            return getRingInfo(a).compareTo(getRingInfo(b));
        }
    }

    /**
     * Determine the winding of two bonds. The winding is > 0 for
     * anti-clockwise and < 0 for clockwise and is relative to bond 1.
     *
     * @param bond1 first bond
     * @param bond2 second bond
     * @return winding relative to bond
     * @throws IllegalArgumentException bonds share no atoms
     */
    private static int winding(IBond bond1, IBond bond2) {
        IAtom b = bond1.getConnectedAtom(bond2);
        if (b == null)
            throw new IllegalArgumentException("Bonds do not share any atoms");
        final IAtom a = bond1.getOther(b);
        final IAtom c = bond2.getOther(b);
        return winding(a.getPoint2d(),
                       b.getPoint2d(),
                       c.getPoint2d());
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
}
