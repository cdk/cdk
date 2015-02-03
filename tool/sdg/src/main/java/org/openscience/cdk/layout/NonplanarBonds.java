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

import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.tools.LoggingToolFactory;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.openscience.cdk.interfaces.IBond.Order.DOUBLE;
import static org.openscience.cdk.interfaces.IBond.Order.SINGLE;
import static org.openscience.cdk.interfaces.IBond.Stereo.DOWN;
import static org.openscience.cdk.interfaces.IBond.Stereo.E_OR_Z;
import static org.openscience.cdk.interfaces.IBond.Stereo.NONE;
import static org.openscience.cdk.interfaces.IBond.Stereo.UP;
import static org.openscience.cdk.interfaces.IBond.Stereo.UP_OR_DOWN;
import static org.openscience.cdk.interfaces.IBond.Stereo.UP_OR_DOWN_INVERTED;

/**
 * Assigns non-planar labels (wedge/hatch) to the tetrahedral and extended tetrahedral 
 * stereocentres in a 2D depiction. Labels are assigned to atoms using the following priority. <ol> <li>bond to non-stereo atoms</li> <li>acyclic
 * bonds</li> <li>bonds to atoms with lower degree (i.e. terminal)</li> <li>lower atomic number</li>
 * </ol>
 * <p/>
 * Unspecified bonds are also marked.
 * 
 * @author John May
 * @cdk.module sdg
 */
final class NonplanarBonds {

    /** The structure we are assigning labels to. */
    private final IAtomContainer container;

    /** Adjacency list graph representation of the structure. */
    private final int[][] graph;

    /** Search for cyclic atoms. */
    private final RingSearch ringSearch;

    /** Tetrahedral elements indexed by central atom. */
    private final ITetrahedralChirality[] tetrahedralElements;

    /** Double-bond elements indexed by end atoms. */
    private final IDoubleBondStereochemistry[] doubleBondElements;

    /** Lookup atom index (avoid IAtomContainer). */
    private final Map<IAtom, Integer> atomToIndex;

    /** Quick lookup of a bond give the atom index of it's atoms. */
    private final GraphUtil.EdgeToBondMap edgeToBond;

    /**
     * Assign non-planar, up and down labels to indicate tetrahedral configuration. Currently all
     * existing directional labels are removed before assigning new labels.
     *
     * @param container the structure to assign labels to
     * @return a container with assigned labels (currently the same as the input)
     * @throws IllegalArgumentException an atom had no 2D coordinates or labels could not be
     *                                  assigned to a tetrahedral centre
     */
    public static IAtomContainer assign(final IAtomContainer container) {
        GraphUtil.EdgeToBondMap edgeToBond = GraphUtil.EdgeToBondMap.withSpaceFor(container);
        new NonplanarBonds(container, GraphUtil.toAdjList(container, edgeToBond), edgeToBond);
        return container;
    }

    /**
     * Assign non-planar bonds to the tetrahedral stereocenters in the {@code container}.
     *
     * @param container structure
     * @param g         graph adjacency list representation
     * @throws IllegalArgumentException an atom had no 2D coordinates or labels could not be
     *                                  assigned to a tetrahedral centre
     */
    NonplanarBonds(final IAtomContainer container, final int[][] g, final GraphUtil.EdgeToBondMap edgeToBond) {

        this.container = container;
        this.tetrahedralElements = new ITetrahedralChirality[container.getAtomCount()];
        this.doubleBondElements = new IDoubleBondStereochemistry[container.getAtomCount()];
        this.graph = g;
        this.atomToIndex = Maps.newHashMapWithExpectedSize(container.getAtomCount());
        this.edgeToBond = edgeToBond;
        this.ringSearch = new RingSearch(container, graph);

        // clear existing up/down labels to avoid collision, this isn't strictly
        // needed if the atom positions weren't adjusted but we can't guarantee
        // that so it's safe to clear them
        for (IBond bond : container.bonds()) {
            switch (bond.getStereo()) {
                case UP:
                case UP_INVERTED:
                case DOWN:
                case DOWN_INVERTED:
                    bond.setStereo(NONE);
            }
        }

        for (int i = 0; i < container.getAtomCount(); i++) {
            IAtom atom = container.getAtom(i);
            atomToIndex.put(atom, i);
            if (atom.getPoint2d() == null)
                throw new IllegalArgumentException("atom " + i + " had unset coordinates");
        }

        // index the tetrahedral elements by their focus
        Integer[] foci = new Integer[container.getAtomCount()];
        int n = 0;
        for (IStereoElement element : container.stereoElements()) {
            if (element instanceof ITetrahedralChirality) {
                ITetrahedralChirality tc = (ITetrahedralChirality) element;
                int focus = atomToIndex.get(tc.getChiralAtom());
                tetrahedralElements[focus] = tc;
                foci[n++] = focus;
            }
            else if (element instanceof IDoubleBondStereochemistry) {
                IBond doubleBond = ((IDoubleBondStereochemistry) element).getStereoBond();
                doubleBondElements[atomToIndex.get(doubleBond.getAtom(0))] =
                        doubleBondElements[atomToIndex.get(doubleBond.getAtom(1))] = (IDoubleBondStereochemistry) element;
            }
        }

        // prioritise to highly-congested tetrahedral centres first
        Arrays.sort(foci, 0, n, new Comparator<Integer>() {

            @Override
            public int compare(Integer i, Integer j) {
                return -Ints.compare(nAdjacentCentres(i), nAdjacentCentres(j));
            }
        });

        // Tetrahedral labels
        for (int i = 0; i < n; i++) {
            label(tetrahedralElements[foci[i]]);
        }

        // Extended tetrahedral labels
        for (IStereoElement se : container.stereoElements()) {
            if (se instanceof ExtendedTetrahedral) {
                label((ExtendedTetrahedral) se);
            }
        }

        // Unspecified double bond, indicated with an up/down wavy bond
        for (IBond bond : findUnspecifiedDoubleBonds()) {
            labelUnspecified(bond);
        }
    }

    /**
     * Assign non-planar labels (wedge/hatch) to the bonds of extended
     * tetrahedral elements to correctly represent its stereochemistry.
     *
     * @param element a extended tetrahedral element
     */
    private void label(final ExtendedTetrahedral element) {

        final IAtom focus = element.focus();
        final IAtom[] atoms = element.peripherals();
        final IBond[] bonds = new IBond[4];

        int p = parity(element.winding());

        List<IBond> focusBonds = container.getConnectedBondsList(focus);

        if (focusBonds.size() != 2) {
            LoggingToolFactory.createLoggingTool(getClass()).warn(
                    "Non-cumulated carbon presented as the focus of extended tetrahedral stereo configuration");
            return;
        }

        IAtom[] terminals = element.findTerminalAtoms(container);

        IAtom left = terminals[0];
        IAtom right = terminals[1];

        // some bonds may be null if, this happens when an implicit atom
        // is present and one or more 'atoms' is a terminal atom
        bonds[0] = container.getBond(left, atoms[0]);
        bonds[1] = container.getBond(left, atoms[1]);
        bonds[2] = container.getBond(right, atoms[2]);
        bonds[3] = container.getBond(right, atoms[3]);

        // find the clockwise ordering (in the plane of the page) by sorting by
        // polar corodinates
        int[] rank = new int[4];
        for (int i = 0; i < 4; i++)
            rank[i] = i;
        p *= sortClockwise(rank, focus, atoms, 4);

        // assign all up/down labels to an auxiliary array
        IBond.Stereo[] labels = new IBond.Stereo[4];
        for (int i = 0; i < 4; i++) {
            int v = rank[i];
            p *= -1;
            labels[v] = p > 0 ? UP : DOWN;
        }

        int[] priority = new int[]{5, 5, 5, 5};

        // set the label for the highest priority and available bonds on one side
        // of the cumulated system, setting both sides doesn't make sense
        int i = 0;
        for (int v : priority(atomToIndex.get(focus), atoms, 4)) {
            IBond bond = bonds[v];
            if (bond == null) continue;
            if (bond.getStereo() == NONE && bond.getOrder() == SINGLE) priority[v] = i++;
        }

        // we now check which side was more favourable and assign two labels
        // to that side only
        if (priority[0] + priority[1] < priority[2] + priority[3]) {
            if (priority[0] < 5) {
                bonds[0].setAtoms(new IAtom[]{left, atoms[0]});
                bonds[0].setStereo(labels[0]);
            }
            if (priority[1] < 5) {
                bonds[1].setAtoms(new IAtom[]{left, atoms[1]});
                bonds[1].setStereo(labels[1]);
            }
        } else {
            if (priority[2] < 5) {
                bonds[2].setAtoms(new IAtom[]{right, atoms[2]});
                bonds[2].setStereo(labels[2]);
            }
            if (priority[3] < 5) {
                bonds[3].setAtoms(new IAtom[]{right, atoms[3]});
                bonds[3].setStereo(labels[3]);
            }
        }

    }

    /**
     * Assign labels to the bonds of tetrahedral element to correctly represent
     * its stereo configuration.
     *
     * @param element a tetrahedral element
     * @throws IllegalArgumentException the labels could not be assigned
     */
    private void label(final ITetrahedralChirality element) {

        final IAtom focus = element.getChiralAtom();
        final IAtom[] atoms = element.getLigands();
        final IBond[] bonds = new IBond[4];

        int p = parity(element.getStereo());
        int n = 0;

        // unspecified centre, no need to assign labels
        if (p == 0) return;

        for (int i = 0; i < 4; i++) {
            if (atoms[i] == focus) {
                p *= parity(i); // implicit H, adjust parity
            } else {
                bonds[n] = container.getBond(focus, atoms[i]);
                atoms[n] = atoms[i];
                n++;
            }
        }

        // sort coordinates and adjust parity (rank gives us the sorted order)
        int[] rank = new int[n];
        for (int i = 0; i < n; i++)
            rank[i] = i;
        p *= sortClockwise(rank, focus, atoms, n);

        // special case when there are three neighbors are acute and an implicit
        // hydrogen is opposite all three neighbors. The central label needs to
        // be inverted, atoms could be laid out like this automatically, consider
        // CC1C[C@H]2CC[C@@H]1C2
        int invert = -1;
        if (n == 3) {
            // find a triangle of non-sequential neighbors (sorted clockwise)
            // which has anti-clockwise winding
            for (int i = 0; i < n; i++) {
                Point2d a = atoms[rank[i]].getPoint2d();
                Point2d b = focus.getPoint2d();
                Point2d c = atoms[rank[(i + 2) % n]].getPoint2d();
                double det = (a.x - c.x) * (b.y - c.y) - (a.y - c.y) * (b.x - c.x);
                if (det > 0) {
                    invert = rank[(i + 1) % n];
                    break;
                }
            }
        }

        // assign all up/down labels to an auxiliary array
        IBond.Stereo[] labels = new IBond.Stereo[n];
        for (int i = 0; i < n; i++) {
            int v = rank[i];

            // 4 neighbors (invert every other one)
            if (n == 4) p *= -1;

            labels[v] = invert == v ? p > 0 ? DOWN : UP : p > 0 ? UP : DOWN;
        }

        // set the label for the highest priority and available bond
        for (int v : priority(atomToIndex.get(focus), atoms, n)) {
            IBond bond = bonds[v];
            if (bond.getStereo() == NONE && bond.getOrder() == SINGLE) {
                bond.setAtoms(new IAtom[]{focus, atoms[v]}); // avoids UP_INVERTED/DOWN_INVERTED
                bond.setStereo(labels[v]);
                return;
            }
        }

        // it should be possible to always assign labels somewhere -> unchecked exception
        throw new IllegalArgumentException("could not assign non-planar (up/down) labels");
    }

    /**
     * Obtain the parity of a value x. The parity is -1 if the value is odd or
     * +1 if the value is even.
     *
     * @param x a value
     * @return the parity
     */
    private int parity(int x) {
        return (x & 0x1) == 1 ? -1 : +1;
    }

    /**
     * Obtain the parity (winding) of a tetrahedral element. The parity is -1
     * for clockwise (odd), +1 for anticlockwise (even) and 0 for unspecified.
     *
     * @param stereo configuration
     * @return the parity
     */
    private int parity(ITetrahedralChirality.Stereo stereo) {
        switch (stereo) {
            case CLOCKWISE:
                return -1;
            case ANTI_CLOCKWISE:
                return +1;
            default:
                return 0;
        }
    }

    /**
     * Obtain the number of centres adjacent to the atom at the index, i.
     *
     * @param i atom index
     * @return number of adjacent centres
     */
    private int nAdjacentCentres(int i) {
        int n = 0;
        for (IAtom atom : tetrahedralElements[i].getLigands())
            if (tetrahedralElements[atomToIndex.get(atom)] != null) n++;
        return n;
    }

    /**
     * Obtain a prioritised array where the indices 0 to n which correspond to
     * the provided {@code atoms}.
     *
     * @param focus focus of the tetrahedral atom
     * @param atoms the atom
     * @param n     number of atoms
     * @return prioritised indices
     */
    private int[] priority(int focus, IAtom[] atoms, int n) {
        int[] rank = new int[n];
        for (int i = 0; i < n; i++)
            rank[i] = i;
        for (int j = 1; j < n; j++) {
            int v = rank[j];
            int i = j - 1;
            while ((i >= 0) && hasPriority(focus, atomToIndex.get(atoms[v]), atomToIndex.get(atoms[rank[i]]))) {
                rank[i + 1] = rank[i--];
            }
            rank[i + 1] = v;
        }
        return rank;
    }

    /**
     * Does the atom at index {@code i} have priority over the atom at index
     * {@code j} for the tetrahedral atom {@code focus}.
     *
     * @param focus tetrahedral centre (or -1 if double bond)
     * @param i     adjacent atom index
     * @param j     adjacent atom index
     * @return whether atom i has priority
     */
    boolean hasPriority(int focus, int i, int j) {

        // prioritise bonds to non-centres
        if (tetrahedralElements[i] == null && tetrahedralElements[j] != null) return true;
        if (tetrahedralElements[i] != null && tetrahedralElements[j] == null) return false;
        if (doubleBondElements[i] == null && doubleBondElements[j] != null) return true;
        if (doubleBondElements[i] != null && doubleBondElements[j] == null) return false;

        // prioritise acyclic bonds
        boolean iCyclic = focus >= 0 ? ringSearch.cyclic(focus, i) : ringSearch.cyclic(i);
        boolean jCyclic = focus >= 0 ? ringSearch.cyclic(focus, j) : ringSearch.cyclic(j);
        if (!iCyclic && jCyclic) return true;
        if (iCyclic && !jCyclic) return false;

        // prioritise atoms with fewer neighbors
        if (graph[i].length < graph[j].length) return true;
        if (graph[i].length > graph[j].length) return false;

        // prioritise by atomic number
        if (container.getAtom(i).getAtomicNumber() < container.getAtom(j).getAtomicNumber())
            return true;
        if (container.getAtom(i).getAtomicNumber() > container.getAtom(j).getAtomicNumber())
            return false;

        return false;
    }

    /**
     * Sort the {@code indices}, which correspond to an index in the {@code atoms} array in
     * clockwise order.
     *
     * @param indices indices, 0 to n
     * @param focus   the central atom
     * @param atoms   the neighbors of the focus
     * @param n       the number of neighbors
     * @return the permutation parity of the sort
     */
    private int sortClockwise(int[] indices, IAtom focus, IAtom[] atoms, int n) {
        int x = 0;
        for (int j = 1; j < n; j++) {
            int v = indices[j];
            int i = j - 1;
            while ((i >= 0) && less(v, indices[i], atoms, focus.getPoint2d())) {
                indices[i + 1] = indices[i--];
                x++;
            }
            indices[i + 1] = v;
        }
        return parity(x);
    }

    /**
     * Is index {@code i}, to the left of index {@code j} when sorting clockwise around the {@code
     * centre}.
     *
     * @param i      an index in {@code atoms}
     * @param j      an index in {@code atoms}
     * @param atoms  atoms
     * @param center central point
     * @return atom i is before j
     * @see <a href="http://stackoverflow.com/a/6989383">Sort points in clockwise order, ciamej</a>
     */
    static boolean less(int i, int j, IAtom[] atoms, Point2d center) {

        Point2d a = atoms[i].getPoint2d();
        Point2d b = atoms[j].getPoint2d();

        if (a.x - center.x >= 0 && b.x - center.x < 0) return true;
        if (a.x - center.x < 0 && b.x - center.x >= 0) return false;
        if (a.x - center.x == 0 && b.x - center.x == 0) {
            if (a.y - center.y >= 0 || b.y - center.y >= 0) return a.y > b.y;
            return b.y > a.y;
        }

        // compute the cross product of vectors (center -> a) x (center -> b)
        double det = (a.x - center.x) * (b.y - center.y) - (b.x - center.x) * (a.y - center.y);
        if (det < 0) return true;
        if (det > 0) return false;

        // points a and b are on the same line from the center
        // check which point is closer to the center
        double d1 = (a.x - center.x) * (a.x - center.x) + (a.y - center.y) * (a.y - center.y);
        double d2 = (b.x - center.x) * (b.x - center.x) + (b.y - center.y) * (b.y - center.y);
        return d1 > d2;
    }

    /**
     * Labels a double bond as unspecified either by marking an adjacent bond as
     * wavy (up/down) or if that's not possible (e.g. it's conjugated with other double bonds
     * that have a conformation), setting the bond to a crossed double bond.
     * 
     * @param doubleBond the bond to mark as unspecified
     */
    private void labelUnspecified(IBond doubleBond) {

        final IAtom aBeg = doubleBond.getAtom(0);
        final IAtom aEnd = doubleBond.getAtom(1);

        final int beg = atomToIndex.get(aBeg);
        final int end = atomToIndex.get(aEnd);

        int nAdj = 0;
        final IAtom[] focus = new IAtom[4];
        final IAtom[] adj = new IAtom[4];

        // build up adj list of all potential atoms
        for (int neighbor : graph[beg]) {
            IBond bond = edgeToBond.get(beg, neighbor);
            if (bond.getOrder() == SINGLE) {
                if (nAdj == 4) return; // more than 4? not a stereo-dbond
                focus[nAdj] = aBeg;
                adj[nAdj++] = container.getAtom(neighbor);
            }
            // conjugated and someone else has marked it as unspecified
            if (bond.getStereo() == UP_OR_DOWN || bond.getStereo() == UP_OR_DOWN_INVERTED) {
                return;
            }
        }
        for (int neighbor : graph[end]) {
            IBond bond = edgeToBond.get(end, neighbor);
            if (bond.getOrder() == SINGLE) {
                if (nAdj == 4) return; // more than 4? not a stereo-dbond
                focus[nAdj] = aEnd;
                adj[nAdj++] = container.getAtom(neighbor);
            }
            // conjugated and someone else has marked it as unspecified
            if (bond.getStereo() == UP_OR_DOWN || bond.getStereo() == UP_OR_DOWN_INVERTED) {
                return;
            }
        }

        int[] rank = priority(-1, adj, nAdj);

        // set the bond to up/down wavy to mark unspecified stereochemistry taking care not
        // to accidentally mark another stereocentre as unspecified
        for (int i = 0; i < nAdj; i++) {
            if (doubleBondElements[atomToIndex.get(adj[rank[i]])] == null &&
                    tetrahedralElements[atomToIndex.get(adj[rank[i]])] == null) {
                edgeToBond.get(atomToIndex.get(focus[rank[i]]),
                               atomToIndex.get(adj[rank[i]])).setStereo(UP_OR_DOWN);
                return;
            }
        }

        // we got here an no bond was marked, fortunately we have a fallback and can use 
        // crossed bond
        doubleBond.setStereo(E_OR_Z);
    }

    /**
     * Locates double bonds to mark as unspecified stereochemistry.
     * 
     * @return set of double bonds
     */
    private List<IBond> findUnspecifiedDoubleBonds() {
        List<IBond> unspecifiedDoubleBonds = new ArrayList<>();
        for (IBond bond : container.bonds()) {
            // non-double bond, ignore it
            if (bond.getOrder() != DOUBLE)
                continue;

            final IAtom aBeg = bond.getAtom(0);
            final IAtom aEnd = bond.getAtom(1);

            final int beg = atomToIndex.get(aBeg);
            final int end = atomToIndex.get(aEnd);

            // cyclic bond, ignore it (FIXME may be a cis/trans bond in macro cycle |V| > 7)
            if (ringSearch.cyclic(beg, end))
                continue;

            // stereo bond, ignore it depiction is correct
            if ((doubleBondElements[beg] != null && doubleBondElements[beg].getStereoBond() == bond) ||
                    (doubleBondElements[end] != null && doubleBondElements[end].getStereoBond() == bond))
                continue;

            // is actually a tetrahedral centre
            if (tetrahedralElements[beg] != null || tetrahedralElements[end] != null)
                continue;
            
            if (!hasOnlyPlainBonds(beg, bond) || !hasOnlyPlainBonds(end, bond))
                continue;
            
            unspecifiedDoubleBonds.add(bond);
        }
        return unspecifiedDoubleBonds;
    }


    /**
     * Check that an atom (v:index) is only adjacent to plain single bonds (may be a bold or
     * hashed wedged - e.g. at fat end) with the single exception being the allowed double bond
     * passed as an argument. 
     * 
     * @param v atom index
     * @param allowedDoubleBond a double bond that is allowed
     * @return the atom is adjacent to one or more plain single bonds
     */
    private boolean hasOnlyPlainBonds(int v, IBond allowedDoubleBond) {
        int count = 0;
        for (int neighbor : graph[v]) {
            IBond adjBond = edgeToBond.get(v, neighbor);
            // non single bonds
            if (adjBond.getOrder().numeric() > 1) {
                if (allowedDoubleBond != adjBond) {
                    return false;
                }
            }
            // single bonds
            else { 
                if (adjBond.getStereo() == UP_OR_DOWN || adjBond.getStereo() == UP_OR_DOWN_INVERTED) {
                    return false;
                }
                count++;
            }            
        }
        return count > 0;
    }
}
