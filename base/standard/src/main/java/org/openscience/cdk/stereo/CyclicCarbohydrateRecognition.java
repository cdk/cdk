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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.stereo;

import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.ringsearch.RingSearch;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import static org.openscience.cdk.stereo.CyclicCarbohydrateRecognition.Turn.Left;
import static org.openscience.cdk.stereo.CyclicCarbohydrateRecognition.Turn.Right;

/**
 * Recognise stereochemistry of Haworth, Chair, and Boat (not yet implemented)
 * projections. These projections are a common way of depicting closed-chain
 * (furanose and pyranose) carbohydrates and require special treatment to
 * interpret stereo conformation. <p/>
 * 
 * The methods used are described by {@cdk.cite batchelor13}. <p/> 
 * 
 * @author John May
 * @cdk.githash
 * @see <a href="http://en.wikipedia.org/wiki/Haworth_projection">Haworth projection (Wikipedia)</a>
 * @see <a href="http://en.wikipedia.org/wiki/Chair_conformation">Chair conformation (Wikipedia)</a>
 */
final class CyclicCarbohydrateRecognition {

    /**
     * The threshold at which to snap bonds to the cardinal direction. The
     * threshold allows bonds slightly of absolute directions to be interpreted.
     * The tested vector is of unit length and so the threshold is simply the
     * angle (in radians).
     */
    public static final double CARDINALITY_THRESHOLD = Math.toRadians(5);
    
    public static final double QUART_CARDINALITY_THRESHOLD = CARDINALITY_THRESHOLD / 4;
    
    private final IAtomContainer container;
    private final int[][]        graph;
    private final EdgeToBondMap  bonds;
    private final Stereocenters  stereocenters;

    /**
     * Required information to recognise stereochemistry.
     *
     * @param container     input structure
     * @param graph         adjacency list representation
     * @param bonds         edge to bond index
     * @param stereocenters location and type of asymmetries
     */
    CyclicCarbohydrateRecognition(IAtomContainer container,
                                  int[][] graph, 
                                  EdgeToBondMap bonds,
                                  Stereocenters stereocenters) {
        this.container = container;
        this.graph = graph;
        this.bonds = bonds;
        this.stereocenters = stereocenters;
    }

    /**
     * Recognise the cyclic carbohydrate projections.
     *
     * @param projections the types of projections to recognise
     * @return recognised stereocenters
     */
    List<IStereoElement> recognise(Set<Projection> projections) {

        if (!projections.contains(Projection.Haworth) && !projections.contains(Projection.Chair))
            return Collections.emptyList();

        List<IStereoElement> elements = new ArrayList<IStereoElement>();

        RingSearch ringSearch = new RingSearch(container, graph);
        for (int[] isolated : ringSearch.isolated()) {

            if (isolated.length < 5 || isolated.length > 7)
                continue;

            int[] cycle = Arrays.copyOf(GraphUtil.cycle(graph, isolated),
                                        isolated.length);

            Point2d[]       points     = coordinatesOfCycle(cycle, container);
            Turn[]          turns      = turns(points); 
            WoundProjection projection = WoundProjection.ofTurns(turns);

            if (!projections.contains(projection.projection))
                continue;
            
            // ring is not aligned correctly for haworth
            if (projection.projection == Projection.Haworth && !checkHaworthAlignment(points))
                continue;
            
            final Point2d horizontalXy = horizontalOffset(points, turns, projection.projection);

            // near vertical, should also flag as potentially ambiguous 
            if (1 - Math.abs(horizontalXy.y) < QUART_CARDINALITY_THRESHOLD)
                continue;
                
            int[] above = cycle.clone();
            int[] below = cycle.clone();

            if (!assignSubstituents(cycle, above, below, projection, horizontalXy))
                continue;

            elements.addAll(newTetrahedralCenters(cycle, above, below, projection));
        }

        return elements;
    }

    /**
     * Determine the turns in the polygon formed of the provided coordinates.
     *
     * @param points polygon points
     * @return array of turns (left, right) or null if a parallel line was found
     */
    static Turn[] turns(Point2d[] points) {

        final Turn[] turns = new Turn[points.length];

        // cycle of size 6 is [1,2,3,4,5,6] not closed
        for (int i = 1; i <= points.length; i++) {
            Point2d prevXy = points[i - 1];
            Point2d currXy = points[i % points.length];
            Point2d nextXy = points[(i + 1) % points.length];
            int parity = (int) Math.signum(det(prevXy.x, prevXy.y,
                                               currXy.x, currXy.y,
                                               nextXy.x, nextXy.y));
            if (parity == 0) return null;
            turns[i % points.length] = parity < 0 ? Right : Turn.Left;
        }

        return turns;
    }

    /**
     * Given a projected cycle, assign the exocyclic substituents to being above
     * of below the projection. For Haworth projections, the substituents must
     * be directly up or down (within some threshold).
     *
     * @param cycle        vertices that form a cycle
     * @param above        vertices that will be above the cycle (filled by
     *                     method)
     * @param below        vertices that will be below the cycle (filled by
     *                     method)
     * @param projection   the type of projection
     * @param horizontalXy offset from the horizontal axis                  
     * @return assignment okay (true), not okay (false)
     */
    private boolean assignSubstituents(int[] cycle,
                                       int[] above,
                                       int[] below,
                                       WoundProjection projection,
                                       Point2d horizontalXy) {

        boolean haworth = projection.projection == Projection.Haworth;

        int found = 0;
        
        for (int i = 1; i <= cycle.length; i++) {

            int j = i % cycle.length;

            int prev = cycle[i - 1];
            int curr = cycle[j];
            int next = cycle[(i + 1) % cycle.length];

            // get the substituents not in the ring (i.e. excl. prev and next)
            int[] ws = filter(graph[curr], prev, next);

            if (ws.length > 2 || ws.length < 1)
                continue;

            Point2d centerXy = container.getAtom(curr).getPoint2d();

            // determine the direction of each substituent 
            for (final int w : ws) {
                Point2d otherXy = container.getAtom(w).getPoint2d();
                Direction direction = direction(centerXy, otherXy, horizontalXy, haworth);

                switch (direction) {
                    case Up:
                        if (above[j] != curr) return false;
                        above[j] = w;
                        break;
                    case Down:
                        if (below[j] != curr) return false;
                        below[j] = w;
                        break;
                    case Other:
                        return false;
                }
            }

            if (above[j] != curr || below[j] != curr)
              found++;
        }
        
        // must have at least 2 that look projected for Haworth
        return found > 1 || projection.projection != Projection.Haworth;
    }

    /**
     * Create the tetrahedral stereocenters for the provided cycle.
     *
     * @param cycle vertices in projected cycle
     * @param above vertices above the cycle
     * @param below vertices below the cycle
     * @param type  type of projection
     * @return zero of more stereocenters
     */
    private List<ITetrahedralChirality> newTetrahedralCenters(int[] cycle, int[] above, int[] below, WoundProjection type) {

        List<ITetrahedralChirality> centers = new ArrayList<ITetrahedralChirality>(cycle.length);

        for (int i = 1; i <= cycle.length; i++) {
            final int prev = cycle[i - 1];
            final int curr = cycle[i % cycle.length];
            final int next = cycle[(i + 1) % cycle.length];

            final int up = above[i % cycle.length];
            final int down = below[i % cycle.length];

            if (!stereocenters.isStereocenter(curr))
                continue;

            // Any wedge or hatch bond causes us to exit, this may still be
            // a valid projection. Currently it can cause a collision with
            // one atom have two tetrahedral stereo elements. 
            if (!isPlanarSigmaBond(bonds.get(curr, prev))
                    || !isPlanarSigmaBond(bonds.get(curr, next))
                    || (up != curr && !isPlanarSigmaBond(bonds.get(curr, up)))
                    || (down != curr && !isPlanarSigmaBond(bonds.get(curr, down))))
                return Collections.emptyList();

            centers.add(new TetrahedralChirality(container.getAtom(curr),
                                                 new IAtom[]{container.getAtom(up),
                                                             container.getAtom(prev),
                                                             container.getAtom(down),
                                                             container.getAtom(next)},
                                                 type.winding
            ));
        }

        return centers;
    }

    /**
     * Obtain the coordinates of atoms in a cycle.
     *
     * @param cycle     vertices that form a cycles
     * @param container structure representation
     * @return coordinates of the cycle
     */
    private static Point2d[] coordinatesOfCycle(int[] cycle, IAtomContainer container) {
        Point2d[] points = new Point2d[cycle.length];
        for (int i = 0; i < cycle.length; i++) {
            points[i] = container.getAtom(cycle[i]).getPoint2d();
        }
        return points;
    }

    /**
     * Filter an array, excluding two provided values. These values must be
     * present in the input.
     *
     * @param org   input array
     * @param skip1 skip this item
     * @param skip2 skip this item also
     * @return array without skip1 and skip2
     */
    private static int[] filter(int[] org, int skip1, int skip2) {
        int n = 0;
        int[] dest = new int[org.length - 2];
        for (int w : org) {
            if (w != skip1 && w != skip2) dest[n++] = w;
        }
        return dest;
    }

    /**
     * Obtain the direction of a substituent relative to the center location. In
     * a Haworth projection the substituent must be directly above or below
     * (with threshold) the center.
     *
     * @param centerXy      location of center
     * @param substituentXy location fo substituent
     * @param horizontalXy  horizontal offset, x > 0                      
     * @param haworth       is Haworth project (substituent must be directly up
     *                      or down)
     * @return the direction (up, down, other)
     */
    private static Direction direction(Point2d centerXy, Point2d substituentXy, Point2d horizontalXy, boolean haworth) {
        double deltaX = substituentXy.x - centerXy.x;
        double deltaY = substituentXy.y - centerXy.y;

        // normalise vector length so threshold is independent of length 
        double mag = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        deltaX /= mag;
        deltaY /= mag;

        // account for an offset horizontal reference and re-normalise,
        // we presume no vertical chairs and use the deltaX +ve or -ve to
        // determine direction, the horizontal offset should be deltaX > 0.
        if (deltaX > 0) {
            deltaX -= horizontalXy.x;
            deltaY -= horizontalXy.y;
        } else {
            deltaX += horizontalXy.x;
            deltaY += horizontalXy.y;
        }
        mag = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        deltaX /= mag;
        deltaY /= mag;

        if (haworth && Math.abs(deltaX) > CARDINALITY_THRESHOLD)
            return Direction.Other;
        
        return deltaY > 0 ? Direction.Up : Direction.Down;
    }

    /**
     * Ensures at least one cyclic bond is horizontal.
     * 
     * @param points the points of atoms in the ring
     * @return whether the Haworth alignment is correct
     */
    private boolean checkHaworthAlignment(Point2d[] points) {
        
        for (int i = 0; i < points.length; i++) {
            Point2d curr = points[i];
            Point2d next = points[(i+1) % points.length];
            
            double deltaY = curr.y - next.y;
            
            if (Math.abs(deltaY) < CARDINALITY_THRESHOLD)
                return true;
        }
        
        return false;
    }

    /**
     * Determine the horizontal offset of the projection. This allows
     * projections that are drawn at angle to be correctly interpreted. 
     * Currently only projections of chair conformations are considered.
     *
     * @param points     points of the cycle
     * @param turns      the turns in the cycle (left/right)
     * @param projection the type of projection
     * @return the horizontal offset
     */
    private Point2d horizontalOffset(Point2d[] points, Turn[] turns, Projection projection) {
        
        // Haworth must currently be drawn vertically, I have seen them drawn
        // slanted but it's difficult to determine which way the projection
        // is relative
        if (projection != Projection.Chair)
            return new Point2d(0, 0);        
                              
        // the atoms either side of a central atom are our reference
        int offset = chairCenterOffset(turns);
        int prev = (offset + 5) % 6;
        int next = (offset + 7) % 6;

        // and the axis formed by these atoms is our horizontal reference which
        // we normalise
        double deltaX = points[prev].x - points[next].x;
        double deltaY = points[prev].y - points[next].y;
        double mag = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        deltaX /= mag;
        deltaY /= mag;
        
        // we now ensure the reference always points left to right (presumes no
        // vertical chairs) 
        if (deltaX < 0) {
            deltaX = -deltaX;
            deltaY = -deltaY;
        }

        // horizontal = <1,0> so the offset if the difference from this 
        return new Point2d(1 - deltaX, deltaY);
    }

    /**
     * Determines the center index offset for the chair projection. The center
     * index is that of the two atoms with opposite turns (fewest). For, LLRLLR
     * the two centers are R and the index is 2 (first is in position 2). 
     * 
     * @param turns calculated turns in the chair projection
     * @return the offset
     */
    private static int chairCenterOffset(Turn[] turns) {
        if (turns[1] == turns[2]) {
            return 0;
        } else if (turns[0] == turns[2]) {
            return 1;
        } else {
            return 2;
        }
    }

    // 3x3 determinant helper for a constant third column
    private static double det(double xa, double ya, double xb, double yb, double xc, double yc) {
        return (xa - xc) * (yb - yc) - (ya - yc) * (xb - xc);
    }

    /**
     * Helper method determines if a bond is defined (not null) and whether it
     * is a sigma (single) bond with no stereo attribute (wedge/hatch).
     *
     * @param bond the bond to test
     * @return the bond is a planar sigma bond
     */
    private static boolean isPlanarSigmaBond(IBond bond) {
        return bond != null &&
                IBond.Order.SINGLE.equals(bond.getOrder()) &&
                IBond.Stereo.NONE.equals(bond.getStereo());
    }

    /**
     * Direction of substituent relative to ring atom.
     */
    enum Direction {
        Up,
        Down,
        Other
    }

    /**
     * Turns, recorded when walking around the cycle.
     */
    enum Turn {
        Left,
        Right
    }

    /**
     * Pairing of Projection + Winding. The wound projection is determined
     * from an array of turns.
     */
    private enum WoundProjection {

        HaworthClockwise(Projection.Haworth, Stereo.CLOCKWISE),
        HaworthAnticlockwise(Projection.Haworth, Stereo.ANTI_CLOCKWISE),
        ChairClockwise(Projection.Chair, Stereo.CLOCKWISE),
        ChairAnticlockwise(Projection.Chair, Stereo.ANTI_CLOCKWISE),
        BoatClockwise(null, Stereo.CLOCKWISE),
        BoatAnticlockwise(null, Stereo.ANTI_CLOCKWISE),
        Other(null, null);

        private final Projection projection;
        private final Stereo     winding;
        private final static Map<Key, WoundProjection> map = new HashMap<Key, WoundProjection>();

        static {
            // Haworth |V| = 5
            map.put(new Key(Left, Left, Left, Left, Left), HaworthAnticlockwise);
            map.put(new Key(Right, Right, Right, Right, Right), HaworthClockwise);

            // Haworth |V| = 6
            map.put(new Key(Left, Left, Left, Left, Left, Left), HaworthAnticlockwise);
            map.put(new Key(Right, Right, Right, Right, Right, Right), HaworthClockwise);

            // Haworth |V| = 7
            map.put(new Key(Left, Left, Left, Left, Left, Left, Left), HaworthAnticlockwise);
            map.put(new Key(Right, Right, Right, Right, Right, Right, Right), HaworthClockwise);

            // Chair
            map.put(new Key(Left, Right, Right, Left, Right, Right), ChairClockwise);
            map.put(new Key(Right, Left, Right, Right, Left, Right), ChairClockwise);
            map.put(new Key(Right, Right, Left, Right, Right, Left), ChairClockwise);
            map.put(new Key(Right, Left, Left, Right, Left, Left), ChairAnticlockwise);
            map.put(new Key(Left, Right, Left, Left, Right, Left), ChairAnticlockwise);
            map.put(new Key(Left, Left, Right, Left, Left, Right), ChairAnticlockwise);

            // Boat
            map.put(new Key(Right, Right, Left, Left, Left, Left), BoatAnticlockwise);
            map.put(new Key(Right, Left, Left, Left, Left, Right), BoatAnticlockwise);
            map.put(new Key(Left, Left, Left, Left, Right, Right), BoatAnticlockwise);
            map.put(new Key(Left, Left, Left, Right, Right, Left), BoatAnticlockwise);
            map.put(new Key(Left, Left, Right, Right, Left, Left), BoatAnticlockwise);
            map.put(new Key(Left, Right, Right, Left, Left, Left), BoatAnticlockwise);
            map.put(new Key(Left, Left, Right, Right, Right, Right), BoatClockwise);
            map.put(new Key(Left, Right, Right, Right, Right, Left), BoatClockwise);
            map.put(new Key(Right, Right, Right, Right, Left, Left), BoatClockwise);
            map.put(new Key(Right, Right, Right, Left, Left, Right), BoatClockwise);
            map.put(new Key(Right, Right, Left, Left, Right, Right), BoatClockwise);
            map.put(new Key(Right, Left, Left, Right, Right, Right), BoatClockwise);
        }

        WoundProjection(Projection projection, Stereo winding) {
            this.projection = projection;
            this.winding = winding;
        }

        static WoundProjection ofTurns(Turn[] turns) {
            if (turns == null) return Other;
            WoundProjection type = map.get(new Key(turns));
            return type != null ? type : Other;
        }

        private static final class Key {
            private final Turn[] turns;

            private Key(Turn... turns) {
                this.turns = turns;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Key key = (Key) o;

                return Arrays.equals(turns, key.turns);
            }

            @Override
            public int hashCode() {
                return turns != null ? Arrays.hashCode(turns) : 0;
            }
        }
    }
}
