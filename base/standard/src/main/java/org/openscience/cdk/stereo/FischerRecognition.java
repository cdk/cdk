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

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.ringsearch.RingSearch;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openscience.cdk.config.Elements.Carbon;
import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo.ANTI_CLOCKWISE;
import static org.openscience.cdk.stereo.Stereocenters.Type.Tetracoordinate;

/**
 * Recognize the configuration of tetrahedral stereocenters depicted as
 * Fischer projection. Fischer projection is a convenient means of depicting
 * 3D geometry commonly used in depicting carbohydrates. 
 * 
 * Fischer projection depicts tetrahedral stereocenters as though they were 
 * coplanar with the four substituents at cardinal directions (up,right,down, 
 * and left). The horizontal bonds (right and left) are interpreted as pointing
 * out of the plane towards the viewer; They are not depicted with non-planar
 * wedge bonds. 
 * 
 * This class provides the recognition of Fischer projections. Each asymmetric
 * carbon is checked as to whether it's 2D depiction is coplanar with cardinal
 * directions. All of these bonds must be planar (i.e. not wedge or hatch) and
 * sigma bonds. In a hydrogen suppressed representation, one of the left or 
 * right bonds (to the implied hydrogen) may be omitted but can be correctly
 * interpreted.
 * 
 * @author John May
 * @cdk.githash
 * @see <a href="http://en.wikipedia.org/wiki/Fischer_projection">Fischer 
 *      projection (Wikipedia)</a>
 */
final class FischerRecognition {

    /**
     * The threshold at which to snap bonds to the cardinal direction. The
     * threshold allows bonds slightly of absolute directions to be interpreted.
     * The tested vector is of unit length and so the threshold is simply the
     * angle (in radians).
     */
    public static final double CARDINALITY_THRESHOLD = Math.toRadians(5);

    /** Cardinal direction, North index. */
    public static final int NORTH = 0;

    /** Cardinal direction, East index. */
    public static final int EAST = 1;

    /** Cardinal direction, South index. */
    public static final int SOUTH = 2;

    /** Cardinal direction, West index. */
    public static final int WEST = 3;

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
    FischerRecognition(IAtomContainer container,
                       int[][]        graph,
                       EdgeToBondMap  bonds,
                       Stereocenters  stereocenters) {
        this.container = container;
        this.graph = graph;
        this.bonds = bonds;
        this.stereocenters = stereocenters;
    }
    
    

    /**
     * Recognise the tetrahedral stereochemistry in the provided structure.
     *
     * @param projections allowed projection types
     * @return zero of more stereo elements
     */
    List<IStereoElement> recognise(Set<Projection> projections) {
        
        if (!projections.contains(Projection.Fischer))
            return Collections.emptyList();

        // build atom index and only recognize 2D depictions
        Map<IAtom,Integer> atomToIndex = new HashMap<IAtom, Integer>();
        for (IAtom atom : container.atoms()) {
            if (atom.getPoint2d() == null)
                return Collections.emptyList();
            atomToIndex.put(atom, atomToIndex.size());
        }
        
        RingSearch ringSearch = new RingSearch(container, graph);
        
        final List<IStereoElement> elements = new ArrayList<IStereoElement>(5);

        for (int v = 0; v < container.getAtomCount(); v++) {

            IAtom    focus = container.getAtom(v);
            Elements elem  = Elements.ofNumber(focus.getAtomicNumber());

            if (elem != Carbon)
                continue;
            if (ringSearch.cyclic(v))
                continue;
            if (stereocenters.elementType(v) != Tetracoordinate)
                continue;
            if (!stereocenters.isStereocenter(v))
                continue;

            ITetrahedralChirality element = newTetrahedralCenter(focus,
                                                                 neighbors(v, graph, bonds));

            if (element == null)
                continue;

            // east/west bonds must be to terminal atoms
            IAtom east = element.getLigands()[EAST];
            IAtom west = element.getLigands()[WEST];
            
            if (!east.equals(focus) && !isTerminal(east, atomToIndex))
                continue;
            if (!west.equals(focus) && !isTerminal(west, atomToIndex))
                continue;
            
            elements.add(element);
        }

        return elements;
    }


    /**
     * Create a new tetrahedral stereocenter of the given focus and neighboring
     * bonds. This is an internal method and is presumed the atom can support
     * tetrahedral stereochemistry and it has three or four explicit neighbors. 
     * 
     * The stereo element is only created if the local arrangement looks like
     * a Fischer projection. 
     * 
     * @param focus central atom
     * @param bonds adjacent bonds
     * @return a stereo element, or null if one could not be created
     */
    static ITetrahedralChirality newTetrahedralCenter(IAtom focus, IBond[] bonds) {

        // obtain the bonds of a centre arranged by cardinal direction 
        IBond[] cardinalBonds = cardinalBonds(focus, bonds);
        
        if (cardinalBonds == null)
            return null;
        
        // vertical bonds must be present and be sigma and planar (no wedge/hatch)
        if (!isPlanarSigmaBond(cardinalBonds[NORTH]) || !isPlanarSigmaBond(cardinalBonds[SOUTH]))
            return null;

        // one of the horizontal bonds can be missing but not both
        if (cardinalBonds[EAST] == null && cardinalBonds[WEST] == null)
            return null;

        // the neighbors of our tetrahedral centre, the EAST or WEST may
        // be missing so we initialise these with the implicit (focus)
        IAtom[] neighbors = new IAtom[]{cardinalBonds[NORTH].getOther(focus),
                                        focus,
                                        cardinalBonds[SOUTH].getOther(focus),
                                        focus};


        // fill in the EAST/WEST bonds, if they are define, single and planar we add the
        // connected atom. else if bond is defined (but not single or planar) or we
        // have 4 neighbours something is wrong and we skip this atom                
        if (isPlanarSigmaBond(cardinalBonds[EAST])) {
            neighbors[EAST] = cardinalBonds[EAST].getOther(focus);
        }
        else if (cardinalBonds[EAST] != null || bonds.length == 4) {
            return null;
        }

        if (isPlanarSigmaBond(cardinalBonds[WEST])) {
            neighbors[WEST] = cardinalBonds[WEST].getOther(focus);
        }
        else if (cardinalBonds[WEST] != null || bonds.length == 4) {
            return null;
        }
        
        return new TetrahedralChirality(focus, neighbors, ANTI_CLOCKWISE);
    }

    /**
     * Arrange the bonds adjacent to an atom (focus) in cardinal direction. The
     * cardinal directions are that of a compass. Bonds are checked as to
     * whether they are horizontal or vertical within a predefined threshold.
     *
     * @param focus an atom
     * @param bonds bonds adjacent to the atom
     * @return array of bonds organised (N,E,S,W), or null if a bond was found
     * that exceeded the threshold
     */
    static IBond[] cardinalBonds(IAtom focus, IBond[] bonds) {

        final Point2d centerXy = focus.getPoint2d();
        final IBond[] cardinal = new IBond[4];

        for (final IBond bond : bonds) {

            IAtom   other   = bond.getOther(focus);
            Point2d otherXy = other.getPoint2d();

            double deltaX = otherXy.x - centerXy.x;
            double deltaY = otherXy.y - centerXy.y;

            // normalise vector length so thresholds are independent 
            double mag = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            deltaX /= mag;
            deltaY /= mag;

            double absDeltaX = Math.abs(deltaX);
            double absDeltaY = Math.abs(deltaY);

            // assign the bond to the cardinal direction
            if (absDeltaX < CARDINALITY_THRESHOLD
                    && absDeltaY > CARDINALITY_THRESHOLD) {
                cardinal[deltaY > 0 ? NORTH : SOUTH] = bond;
            }
            else if (absDeltaX > CARDINALITY_THRESHOLD
                    && absDeltaY < CARDINALITY_THRESHOLD) {
                cardinal[deltaX > 0 ? EAST : WEST] = bond;
            }
            else {
                return null;
            }
        }

        return cardinal;
    }

    /**
     * Is the atom terminal having only one connection.
     *
     * @param atom        an atom
     * @param atomToIndex a map of atoms to index
     * @return the atom is terminal
     */
    private boolean isTerminal(IAtom atom, Map<IAtom, Integer> atomToIndex) {
        return graph[atomToIndex.get(atom)].length == 1;
    }

    /**
     * Helper method determines if a bond is defined (not null) and whether
     * it is a sigma (single) bond with no stereo attribute (wedge/hatch).
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
     * Helper method to obtain the neighbouring bonds from an adjacency list
     * graph and edge->bond map.
     *
     * @param v       vertex
     * @param g       graph (adj list)
     * @param bondMap map of edges to bonds
     * @return neighboring bonds
     */
    private static IBond[] neighbors(int v, int[][] g, EdgeToBondMap bondMap) {
        int[]   ws    = g[v];
        IBond[] bonds = new IBond[ws.length];
        for (int i = 0; i < ws.length; i++) {
            bonds[i] = bondMap.get(v, ws[i]);
        }
        return bonds;
    }
}
