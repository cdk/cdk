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

import org.openscience.cdk.graph.AllPairsShortestPaths;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An overlap resolver that tries to resolve overlaps by rotating (reflecting),
 * bending, and stretching bonds. <p/>
 * 
 * The RBS (rotate, bend, stretch) algorithm is first described by {@cdk.cite Shelley83},
 * and later in more detail by {@cdk.cite HEL99}.
 * <p/>
 * Essentially we have a measure of {@link Congestion}. From that we find 
 * un-bonded atoms that contribute significantly (i.e. overlap). To resolve
 * that overlap we try resolving the overlap by changing (acyclic) bonds in the
 * shortest path between the congested pair. Operations, from most to least 
 * favourable, are:
 * <ul>
 *     <li>Rotation (or reflection), {@link #rotate(Collection)}</li>
 *     <li>Inversion (not described in lit), {@link #invert(Collection)}</li>
 *     <li>Stretch, {@link #stretch(AtomPair, IntStack, Point2d[])}</li>
 *     <li>Bend, {@link #bend(AtomPair, IntStack, Point2d[])}</li>
 * </ul>
 */
final class LayoutRefiner {

    /**
     * These value are constants but could be parametrised in future.
     */

    // bond length should be changeable
    private static final double BOND_LENGTH = 1.5;

    // Min dist between un-bonded atoms, making the denominator smaller means
    // we want to spread atoms out more
    private static final double MIN_DIST = BOND_LENGTH / 2;

    // Min score is derived from the min distance
    private static final double MIN_SCORE = 1 / (MIN_DIST * MIN_DIST);

    // How much do we add to a bond when making it longer.
    private static final double STRETCH_STEP = 0.32 * BOND_LENGTH;

    // How much we bend bonds by
    private static final double BEND_STEP = Math.toRadians(10);

    // Ensure we don't stretch bonds too long.
    private static final double MAX_BOND_LENGTH = 2 * BOND_LENGTH;

    // Only accept if improvement is >= 2%. I don't like this because
    // huge structures will have less improvement even though the overlap
    // was resolved.
    public static final double IMPROVEMENT_PERC_THRESHOLD = 0.02;

    // Rotation (reflection) is always good if it improves things
    // since we're not distorting the layout. Rather than use the
    // percentage based threshold we accept an modification if
    // the improvement is this much better.
    public static final int ROTATE_DELTA_THRESHOLD = 5;


    // Maximum number of iterations whilst improving
    private static final int MAX_ITERATIONS = 10;

    // fast lookup structures
    private final IAtomContainer          mol;
    private final Map<IAtom, Integer>     idxs;
    private final int[][]                 adjList;
    private final GraphUtil.EdgeToBondMap bondMap;
    private final IAtom[]                 atoms;

    // measuring and finding congestion
    private final Congestion            congestion;
    private final AllPairsShortestPaths apsp;

    // buffers where we can store and restore different solutions
    private final Point2d[] buffer1, buffer2, backup;
    private final IntStack  stackBackup;
    private final boolean[] visited;

    // ring system index, allows us to quickly tell if two atoms are
    // in the same ring system
    private final int[] ringsystems;

    private final Set<IAtom> afix;
    private final Set<IBond> bfix;

    /**
     * Create a new layout refiner for the provided molecule.
     * 
     * @param mol molecule to refine
     */
     LayoutRefiner(IAtomContainer mol, Set<IAtom> afix, Set<IBond> bfix) {
        this.mol = mol;
        this.afix = afix;
        this.bfix = bfix;
        this.bondMap = GraphUtil.EdgeToBondMap.withSpaceFor(mol);
        this.adjList = GraphUtil.toAdjList(mol, bondMap);
        this.idxs = new HashMap<>();
        for (IAtom atom : mol.atoms())
            idxs.put(atom, idxs.size());
        this.atoms = AtomContainerManipulator.getAtomArray(mol);

        // buffers for storing coordinates
        this.buffer1 = new Point2d[atoms.length];
        this.buffer2 = new Point2d[atoms.length];
        this.backup = new Point2d[atoms.length];
        for (int i = 0; i < buffer1.length; i++) {
            buffer1[i] = new Point2d();
            buffer2[i] = new Point2d();
            backup[i] = new Point2d();
        }
        this.stackBackup = new IntStack(atoms.length);
        this.visited = new boolean[atoms.length];

        this.congestion = new Congestion(mol, adjList);

        // note, this is lazy so only does the shortest path when needed
        // and does |V| search at maximum
        this.apsp = new AllPairsShortestPaths(mol);

        // index ring systems, idx -> ring system number (rnum)
        int rnum = 1;
        this.ringsystems = new int[atoms.length];
        for (int i = 0; i < atoms.length; i++) {
            if (atoms[i].isInRing() && ringsystems[i] == 0)
                traverseRing(ringsystems, i, rnum++);
        }
    }

    /**
     * Simple method for marking ring systems with a flood-fill.
     *
     * @param ringSystem ring system vector
     * @param v          start atom
     * @param rnum       the number to mark atoms of this ring
     */
    private void traverseRing(int[] ringSystem, int v, int rnum) {
        ringSystem[v] = rnum;
        for (int w : adjList[v]) {
            if (ringSystem[w] == 0 && bondMap.get(v, w).isInRing())
                traverseRing(ringSystem, w, rnum);
        }
    }

    /**
     * Find all pairs of un-bonded atoms that are congested.
     *
     * @return pairs of congested atoms
     */
    List<AtomPair> findCongestedPairs() {

        List<AtomPair> pairs = new ArrayList<>();

        // only add a single pair between each ring system, otherwise we
        // may have many pairs that are actually all part of the same
        // congestion
        Set<IntTuple> ringpairs = new HashSet<>();

        // score at which to check for crossing bonds
        final double maybeCrossed = 1 / (2 * 2);

        final int numAtoms = mol.getAtomCount();
        for (int u = 0; u < numAtoms; u++) {
            for (int v = u + 1; v < numAtoms; v++) {
                double contribution = congestion.contribution(u, v);
                // <0 = bonded
                if (contribution <= 0)
                    continue;

                // we don't modify ring bonds with the class to when the atoms
                // same ring systems we can't reduce the congestion
                if (ringsystems[u] > 0 && ringsystems[u] == ringsystems[v])
                    continue;

                // an un-bonded atom pair is congested if they're and with a certain distance
                // or any of their bonds are crossing
                if (contribution >= MIN_SCORE || contribution >= maybeCrossed && haveCrossingBonds(u, v)) {

                    int uWeight = mol.getAtom(u).getProperty(AtomPlacer.PRIORITY);
                    int vWeight = mol.getAtom(v).getProperty(AtomPlacer.PRIORITY);

                    int[] path = uWeight > vWeight ? apsp.from(u).pathTo(v)
                                                   : apsp.from(v).pathTo(u);

                    // something not right here if the len is < 3
                    int len = path.length;
                    if (len < 3) continue;

                    // build the seqAt and bndAt lists from shortest path
                    int[] seqAt = new int[len - 2];
                    IBond[] bndAt = new IBond[len - 1];
                    makeAtmBndQueues(path, seqAt, bndAt);

                    // we already know about this collision between these ring systems
                    // so dont add the pair
                    if (ringsystems[u] > 0 && ringsystems[v] > 0 &&
                            !ringpairs.add(new IntTuple(ringsystems[u], ringsystems[v])))
                        continue;

                    // add to pairs to overlap
                    pairs.add(new AtomPair(u, v, seqAt, bndAt));
                }
            }
        }

        // sort the pairs to attempt consistent overlap resolution (order independent)
        Collections.sort(pairs, new Comparator<AtomPair>() {
            @Override
            public int compare(AtomPair a, AtomPair b) {
                int a1 = atoms[a.fst].getProperty(AtomPlacer.PRIORITY);
                int a2 = atoms[a.snd].getProperty(AtomPlacer.PRIORITY);
                int b1 = atoms[b.fst].getProperty(AtomPlacer.PRIORITY);
                int b2 = atoms[b.snd].getProperty(AtomPlacer.PRIORITY);
                int amin, amax;
                int bmin, bmax;
                if (a1 < a2) {
                    amin = a1;
                    amax = a2;
                }
                else {
                    amin = a2;
                    amax = a1;
                }
                if (b1 < b2) {
                    bmin = a1;
                    bmax = a2;
                }
                else {
                    bmin = a2;
                    bmax = a1;
                }
                int cmp = Integer.compare(amin, bmin);
                if (cmp != 0) return cmp;
                return Integer.compare(amax, bmax);
            }
        });

        return pairs;
    }

    /**
     * Check if two bonds are crossing.
     *
     * @param beg1 first atom of first bond
     * @param end1 second atom of first bond
     * @param beg2 first atom of second bond
     * @param end2 first atom of second bond
     * @return bond is crossing
     */
    private boolean isCrossed(Point2d beg1, Point2d end1, Point2d beg2, Point2d end2) {
        return Line2D.linesIntersect(beg1.x, beg1.y, end1.x, end1.y, beg2.x, beg2.y, end2.x, end2.y);
    }

    /**
     * Check if any of the bonds adjacent to u, v (not bonded) are crossing.
     *
     * @param u an atom (idx)
     * @param v another atom (idx)
     * @return there are crossing bonds
     */
    private boolean haveCrossingBonds(int u, int v) {
        int[] us = adjList[u];
        int[] vs = adjList[v];
        for (int u1 : us) {
            for (int v1 : vs) {
                if (u1 == v || v1 == u || u1 == v1)
                    continue;
                if (isCrossed(atoms[u].getPoint2d(), atoms[u1].getPoint2d(), atoms[v].getPoint2d(), atoms[v1].getPoint2d()))
                    return true;
            }
        }
        return false;
    }

    /** Set of rotatable bonds we've explored and found are probably symmetric. */
    private final Set<IBond> probablySymmetric = new HashSet<>();

    /**
     * Attempt to reduce congestion through rotation of flippable bonds between
     * congest pairs.
     *
     * @param pairs congested pairs of atoms
     */
    void rotate(Collection<AtomPair> pairs) {

        // bond has already been tried in this phase so
        // don't need to test again
        Set<IBond> tried = new HashSet<>();

        Pair:
        for (AtomPair pair : pairs) {
            for (IBond bond : pair.bndAt) {

                // only try each bond once per phase and skip
                if (!tried.add(bond))
                    continue;
                if (bfix.contains(bond))
                    continue;

                // those we have found to probably be symmetric
                if (probablySymmetric.contains(bond))
                    continue;

                // can't rotate these
                if (bond.getOrder() != IBond.Order.SINGLE || bond.isInRing())
                    continue;

                final IAtom beg = bond.getAtom(0);
                final IAtom end = bond.getAtom(1);
                final int begIdx = idxs.get(beg);
                final int endIdx = idxs.get(end);

                // terminal
                if (adjList[begIdx].length == 1 || adjList[endIdx].length == 1)
                    continue;

                int begPriority = beg.getProperty(AtomPlacer.PRIORITY);
                int endPriority = end.getProperty(AtomPlacer.PRIORITY);

                Arrays.fill(visited, false);
                if (begPriority < endPriority) {
                    stackBackup.len = visitAdj(visited, stackBackup.xs, begIdx, endIdx);

                    // avoid moving fixed atoms
                    if (!afix.isEmpty()) {
                        final int begCnt = numFixedMoved(stackBackup.xs, stackBackup.len);
                        if (begCnt > 0) {
                            Arrays.fill(visited, false);
                            stackBackup.len = visitAdj(visited, stackBackup.xs, endIdx, begIdx);
                            final int endCnt = numFixedMoved(stackBackup.xs, stackBackup.len);
                            if (endCnt > 0)
                                continue;
                        }
                    }

                }
                else {
                    stackBackup.len = visitAdj(visited, stackBackup.xs, endIdx, begIdx);

                    // avoid moving fixed atoms
                    if (!afix.isEmpty()) {
                        final int endCnt = numFixedMoved(stackBackup.xs, stackBackup.len);
                        if (endCnt > 0) {
                            Arrays.fill(visited, false);
                            stackBackup.len = visitAdj(visited, stackBackup.xs, begIdx, endIdx);
                            final int begCnt = numFixedMoved(stackBackup.xs, stackBackup.len);
                            if (begCnt > 0)
                                continue;
                        }
                    }
                }

                double min = congestion.score();

                backupCoords(backup, stackBackup);
                reflect(stackBackup, beg, end);
                congestion.update(visited, stackBackup.xs, stackBackup.len);

                double delta = min - congestion.score();

                // keep if decent improvement or improvement and resolves this overlap
                if (delta > ROTATE_DELTA_THRESHOLD ||
                    (delta > 1 && congestion.contribution(pair.fst, pair.snd) < MIN_SCORE)) {
                    continue Pair;
                } else {

                    // almost no difference from flipping... bond is probably symmetric
                    // mark to avoid in future iterations
                    if (Math.abs(delta) < 0.1)
                        probablySymmetric.add(bond);

                    // restore
                    restoreCoords(stackBackup, backup);
                    congestion.update(visited, stackBackup.xs, stackBackup.len);
                    congestion.score = min;
                }
            }
        }
    }

    private int numFixedMoved(final int[] xs, final int len) {
        int cnt = 0;
        Set<IAtom> amoved = new HashSet<>();
        for (int i = 0; i < len; i++) {
            amoved.add(mol.getAtom(xs[i]));
        }
        for (IBond bond : bfix) {
            if (amoved.contains(bond.getAtom(0)) && amoved.contains(bond.getAtom(1)))
                cnt++;
        }
        return cnt;
    }

    /**
     * Special case congestion minimisation, rotate terminals bonds around ring
     * systems so they are inside the ring.
     *
     * @param pairs congested atom pairs
     */
    void invert(Collection<AtomPair> pairs) {
        for (AtomPair pair : pairs) {
            if (congestion.contribution(pair.fst, pair.snd) < MIN_SCORE)
                continue;
            if (fusionPointInversion(pair))
                continue;
            if (macroCycleInversion(pair))
                continue;
        }
    }

    // For substituents attached to macrocycles we may be able to point these in/out
    // of the ring
    private boolean macroCycleInversion(AtomPair pair) {

        for (int v : pair.seqAt) {
            IAtom atom = mol.getAtom(v);
            if (!atom.isInRing() || adjList[v].length == 2)
                continue;
            if (atom.getProperty(MacroCycleLayout.MACROCYCLE_ATOM_HINT) == null)
                continue;
            final List<IBond> acyclic = new ArrayList<>(2);
            final List<IBond> cyclic = new ArrayList<>(2);
            for (int w : adjList[v]) {
                IBond bond = bondMap.get(v, w);
                if (bond.isInRing())
                    cyclic.add(bond);
                else
                    acyclic.add(bond);
            }
            if (cyclic.size() > 2)
                continue;

            for (IBond bond : acyclic) {
                if (bfix.contains(bond))
                    continue;
                Arrays.fill(visited, false);
                stackBackup.len = visit(visited, stackBackup.xs, v, idxs.get(bond.getConnectedAtom(atom)), 0);

                Point2d a = atom.getPoint2d();
                Point2d b = bond.getConnectedAtom(atom).getPoint2d();

                Vector2d perp = new Vector2d(b.x - a.x, b.y - a.y);
                perp.normalize();
                double score = congestion.score();
                backupCoords(backup, stackBackup);

                reflect(stackBackup, new Point2d(a.x - perp.y, a.y + perp.x), new Point2d(a.x + perp.y, a.y - perp.x));
                congestion.update(visited, stackBackup.xs, stackBackup.len);

                if (percDiff(score, congestion.score()) >= IMPROVEMENT_PERC_THRESHOLD) {
                    return true;
                }

                restoreCoords(stackBackup, backup);
            }
        }

        return false;
    }

    private boolean fusionPointInversion(AtomPair pair) {
        // not candidates for inversion
        // > 3 bonds
        if (pair.bndAt.length != 3)
            return false;
        if (bfix.contains(pair.bndAt[0]) || bfix.contains(pair.bndAt[2]))
            return false;
        // we want *!@*@*!@*
        if (!pair.bndAt[0].isInRing() || pair.bndAt[1].isInRing() || pair.bndAt[2].isInRing())
            return false;
        // non-terminals
        if (adjList[pair.fst].length > 1 || adjList[pair.snd].length > 1)
            return false;


        IAtom fst = atoms[pair.fst];

        // choose which one to invert, preffering hydrogens
        stackBackup.clear();
        if (fst.getAtomicNumber() == 1)
            stackBackup.push(pair.fst);
        else
            stackBackup.push(pair.snd);

        reflect(stackBackup, pair.bndAt[0].getAtom(0), pair.bndAt[0].getAtom(1));
        congestion.update(stackBackup.xs, stackBackup.len);
        return true;
    }

    /**
     * Bend all bonds in the shortest path between a pair of atoms in an attempt
     * to resolve the overlap. The bend that produces the minimum congestion is
     * stored in the provided stack and coords with the congestion score
     * returned.
     *
     * @param pair   congested atom pair
     * @param stack  best result vertices
     * @param coords best result coords
     * @param firstVisit visit map to avoid repeating work
     * @return congestion score of best result
     */
    private double bend(AtomPair pair, IntStack stack, Point2d[] coords, Map<IBond,AtomPair> firstVisit) {

        stackBackup.clear();

        assert stack.len == 0;
        final double score = congestion.score();
        double min = score;

        // special case: if we have an even length path where the two
        // most central bonds are cyclic but the next two aren't we bend away
        // from each other
        if (pair.bndAt.length > 4 && (pair.bndAtCode & 0b11111) == 0b00110) {

            final IBond bndA = pair.bndAt[2];
            final IBond bndB = pair.bndAt[3];

            if (bfix.contains(bndA) || bfix.contains(bndB))
                return Integer.MAX_VALUE;

            final IAtom pivotA = getCommon(bndA, pair.bndAt[1]);
            final IAtom pivotB = getCommon(bndB, pair.bndAt[0]);

            if (pivotA == null || pivotB == null)
                return Integer.MAX_VALUE;

            Arrays.fill(visited, false);
            int split = visit(visited, stack.xs, idxs.get(pivotA), idxs.get(bndA.getConnectedAtom(pivotA)), 0);
            stack.len = visit(visited, stack.xs, idxs.get(pivotB), idxs.get(bndB.getConnectedAtom(pivotB)), split);

            // perform bend one way
            backupCoords(backup, stack);
            bend(stack.xs, 0, split, pivotA, BEND_STEP);
            bend(stack.xs, split, stack.len, pivotB, -BEND_STEP);

            congestion.update(stack.xs, stack.len);

            if (percDiff(score, congestion.score()) >= IMPROVEMENT_PERC_THRESHOLD) {
                backupCoords(coords, stack);
                stackBackup.copyFrom(stack);
                min = congestion.score();
            }

            // now bend the other way
            restoreCoords(stack, backup);
            bend(stack.xs, 0, split, pivotA, -BEND_STEP);
            bend(stack.xs, split, stack.len, pivotB, BEND_STEP);
            congestion.update(stack.xs, stack.len);
            if (percDiff(score, congestion.score()) >= IMPROVEMENT_PERC_THRESHOLD && congestion.score() < min) {
                backupCoords(coords, stack);
                stackBackup.copyFrom(stack);
                min = congestion.score();
            }

            // restore original coordinates and reset score
            restoreCoords(stack, backup);
            congestion.update(stack.xs, stack.len);
            congestion.score = score;
        }
        // general case: try bending acyclic bonds in the shortest
        // path from inside out
        else {

            // try bending all bonds and accept the best one
            for (IBond bond : pair.bndAt) {
                if (bond.isInRing()) continue;
                if (bfix.contains(bond)) continue;

                // has this bond already been tested as part of another pair
                AtomPair first = firstVisit.get(bond);
                if (first == null)
                    firstVisit.put(bond, first = pair);
                if (first != pair)
                    continue;

                final IAtom beg = bond.getAtom(0);
                final IAtom end = bond.getAtom(1);
                final int begPriority = beg.getProperty(AtomPlacer.PRIORITY);
                final int endPriority = end.getProperty(AtomPlacer.PRIORITY);

                Arrays.fill(visited, false);
                if (begPriority < endPriority)
                    stack.len = visit(visited, stack.xs, idxs.get(beg), idxs.get(end), 0);
                else
                    stack.len = visit(visited, stack.xs, idxs.get(end), idxs.get(beg), 0);

                backupCoords(backup, stack);

                // bend one way
                if (begPriority < endPriority)
                    bend(stack.xs, 0, stack.len, beg, pair.attempt * BEND_STEP);
                else
                    bend(stack.xs, 0, stack.len, end, pair.attempt * BEND_STEP);
                congestion.update(visited, stack.xs, stack.len);

                if (percDiff(score, congestion.score()) >= IMPROVEMENT_PERC_THRESHOLD &&
                    congestion.score() < min) {
                    backupCoords(coords, stack);
                    stackBackup.copyFrom(stack);
                    min = congestion.score();
                }

                // bend other way
                if (begPriority < endPriority)
                    bend(stack.xs, 0, stack.len, beg, pair.attempt * -BEND_STEP);
                else
                    bend(stack.xs, 0, stack.len, end, pair.attempt * -BEND_STEP);
                congestion.update(visited, stack.xs, stack.len);

                if (percDiff(score, congestion.score()) >= IMPROVEMENT_PERC_THRESHOLD && congestion.score() < min) {
                    backupCoords(coords, stack);
                    stackBackup.copyFrom(stack);
                    min = congestion.score();
                }

                restoreCoords(stack, backup);
                congestion.update(visited, stack.xs, stack.len);
                congestion.score = score;
            }
        }

        stack.copyFrom(stackBackup);

        return min;
    }

    /**
     * Stretch all bonds in the shortest path between a pair of atoms in an
     * attempt to resolve the overlap. The stretch that produces the minimum
     * congestion is stored in the provided stack and coords with the congestion
     * score returned.
     *
     * @param pair   congested atom pair
     * @param stack  best result vertices
     * @param coords best result coords
     * @param firstVisit visit map to avoid repeating work
     * @return congestion score of best result
     */
    private double stretch(AtomPair pair, IntStack stack, Point2d[] coords, Map<IBond,AtomPair> firstVisit) {

        stackBackup.clear();

        final double score = congestion.score();
        double min = score;

        for (IBond bond : pair.bndAt) {

            // don't stretch ring bonds
            if (bond.isInRing())
                continue;
            if (bfix.contains(bond)) continue;

            // has this bond already been tested as part of another pair
            AtomPair first = firstVisit.get(bond);
            if (first == null)
                firstVisit.put(bond, first = pair);
            if (first != pair)
                continue;

            final IAtom beg = bond.getAtom(0);
            final IAtom end = bond.getAtom(1);
            final int begIdx = idxs.get(beg);
            final int endIdx = idxs.get(end);
            int begPriority = beg.getProperty(AtomPlacer.PRIORITY);
            int endPriority = end.getProperty(AtomPlacer.PRIORITY);

            Arrays.fill(visited, false);
            if (begPriority < endPriority)
                stack.len = visit(visited, stack.xs, endIdx, begIdx, 0);
            else
                stack.len = visit(visited, stack.xs, begIdx, endIdx, 0);

            backupCoords(backup, stack);
            if (begPriority < endPriority)
                stretch(stack, end, beg, pair.attempt * STRETCH_STEP);
            else
                stretch(stack, beg, end, pair.attempt * STRETCH_STEP);

            congestion.update(visited, stack.xs, stack.len);

            if (percDiff(score, congestion.score()) >= IMPROVEMENT_PERC_THRESHOLD && congestion.score() < min) {
                backupCoords(coords, stack);
                min = congestion.score();
                stackBackup.copyFrom(stack);
            }

            restoreCoords(stack, backup);
            congestion.update(visited, stack.xs, stack.len);
            congestion.score = score;
        }

        stack.copyFrom(stackBackup);

        return min;
    }

    /**
     * Resolves conflicts either by bending bonds or stretching bonds in the
     * shortest path between an overlapping pair. Bending and stretch are tried
     * for each pair and the best resolution is used.
     *
     * @param pairs pairs
     */
    private void bendOrStretch(Collection<AtomPair> pairs) {

        // without checking which bonds have been bent/stretch already we
        // could end up repeating a lot of repeated work to no avail
        Map<IBond,AtomPair> bendVisit    = new HashMap<>();
        Map<IBond,AtomPair> stretchVisit = new HashMap<>();

        IntStack bendStack = new IntStack(atoms.length);
        IntStack stretchStack = new IntStack(atoms.length);

        for (AtomPair pair : pairs) {

            double score = congestion.score();

            // each attempt will be more aggressive/distorting
            for (pair.attempt = 1; pair.attempt <= 3; pair.attempt++) {

                bendStack.clear();
                stretchStack.clear();

                // attempt both bending and stretching storing the
                // best result in the provided buffer
                double bendScore    = bend(pair, bendStack, buffer1, bendVisit);
                double stretchScore = stretch(pair, stretchStack, buffer2, stretchVisit);

                // bending is better than stretching
                if (bendScore < stretchScore && bendScore < score) {
                    restoreCoords(bendStack, buffer1);
                    congestion.update(bendStack.xs, bendStack.len);
                    break;
                }

                // stretching is better than bending
                else if (bendScore > stretchScore && stretchScore < score) {
                    restoreCoords(stretchStack, buffer2);
                    congestion.update(stretchStack.xs, stretchStack.len);
                    break;
                }

            }
        }
    }

    /**
     * Refine the 2D coordinates of a layout to reduce overlap and congestion.
     */
    public void refine() {
        for (int i = 1; i <= MAX_ITERATIONS; i++) {
            final List<AtomPair> pairs = findCongestedPairs();

            if (pairs.isEmpty())
                break;

            final double min = congestion.score();

            // rotation: flipping around sigma bonds
            rotate(pairs);

            // rotation improved, so try more rotation, we may have caused
            // new conflicts that can be resolved through more rotations
            if (congestion.score() < min)
                continue;

            // inversion: terminal atoms can be placed inside rings
            // which is preferable to bending or stretching
            invert(pairs);

            if (congestion.score() < min)
                continue;

            // bending or stretching: least favourable but sometimes
            // the only way. We try either and use the best
            bendOrStretch(pairs);

            if (congestion.score() < min)
                continue;

            break;
        }
    }

    /**
     * Backup the coordinates of atoms (idxs) in the stack to the provided
     * destination.
     *
     * @param dest  destination
     * @param stack atom indexes to backup
     */
    private void backupCoords(Point2d[] dest, IntStack stack) {
        for (int i = 0; i < stack.len; i++) {
            int v = stack.xs[i];
            dest[v].x = atoms[v].getPoint2d().x;
            dest[v].y = atoms[v].getPoint2d().y;
        }
    }

    /**
     * Restore the coordinates of atoms (idxs) in the stack to the provided
     * source.
     *
     * @param stack atom indexes to backup
     * @param src   source of coordinates
     */
    private void restoreCoords(IntStack stack, Point2d[] src) {
        for (int i = 0; i < stack.len; i++) {
            int v = stack.xs[i];
            atoms[v].getPoint2d().x = src[v].x;
            atoms[v].getPoint2d().y = src[v].y;
        }
    }

    /**
     * Reflect all atoms (indexes) int he provided stack around the line formed
     * of the beg and end atoms.
     *
     * @param stack atom indexes to reflect
     * @param beg   beg atom of a bond
     * @param end   end atom of a bond
     */
    private void reflect(IntStack stack, IAtom beg, IAtom end) {
        Point2d begP = beg.getPoint2d();
        Point2d endP = end.getPoint2d();
        reflect(stack, begP, endP);
    }

    private void reflect(IntStack stack, Tuple2d begP, Tuple2d endP) {
        double dx = endP.x - begP.x;
        double dy = endP.y - begP.y;

        double a = (dx * dx - dy * dy) / (dx * dx + dy * dy);
        double b = 2 * dx * dy / (dx * dx + dy * dy);

        for (int i = 0; i < stack.len; i++) {
            reflect(atoms[stack.xs[i]].getPoint2d(), begP, a, b);
        }
    }

    /**
     * Reflect a point (p) in a line formed of 'base', 'a', and 'b'.
     *
     * @param p    point to reflect
     * @param base base of the refection source
     * @param a    a reflection coef
     * @param b    b reflection coef
     */
    private static void reflect(Tuple2d p, Tuple2d base, double a, double b) {
        double x = a * (p.x - base.x) + b * (p.y - base.y) + base.x;
        double y = b * (p.x - base.x) - a * (p.y - base.y) + base.y;
        p.x = x;
        p.y = y;
    }


    /**
     * Bend select atoms around a provided pivot by the specified amount (r).
     *
     * @param indexes  array of atom indexes
     * @param from     start offset into the array (inclusive)
     * @param to       end offset into the array (exclusive)
     * @param pivotAtm the point about which we are pivoting
     * @param r        radians to bend by
     */
    private void bend(int[] indexes, int from, int to, IAtom pivotAtm, double r) {
        double s = Math.sin(r);
        double c = Math.cos(r);
        Point2d pivot = pivotAtm.getPoint2d();
        for (int i = from; i < to; i++) {
            Point2d p = mol.getAtom(indexes[i]).getPoint2d();
            double x = p.x - pivot.x;
            double y = p.y - pivot.y;
            double nx = x * c + y * s;
            double ny = -x * s + y * c;
            p.x = nx + pivot.x;
            p.y = ny + pivot.y;
        }
    }

    /**
     * Stretch the distance between beg and end, moving all atoms provided in
     * the stack.
     *
     * @param stack  atoms to be moved
     * @param beg    begin atom of a bond
     * @param end    end atom of a bond
     * @param amount amount to try stretching by (absolute)
     */
    private void stretch(IntStack stack, IAtom beg, IAtom end, double amount) {
        Point2d begPoint = beg.getPoint2d();
        Point2d endPoint = end.getPoint2d();

        if (begPoint.distance(endPoint) + amount > MAX_BOND_LENGTH)
            return;

        Vector2d vector = new Vector2d(endPoint.x - begPoint.x, endPoint.y - begPoint.y);
        vector.normalize();
        vector.scale(amount);

        for (int i = 0; i < stack.len; i++)
            atoms[stack.xs[i]].getPoint2d().add(vector);
    }


    /**
     * Internal - makes atom (seq) and bond priority queues for resolving
     * overlap. Only (acyclic - but not really) atoms and bonds in the shortest
     * path between the two atoms can resolve an overlap. We create prioritised
     * sequences of atoms/bonds where the more central in the shortest path.
     *
     * @param path  shortest path between atoms
     * @param seqAt prioritised atoms, first atom is the middle of the path
     * @param bndAt prioritised bonds, first bond is the middle of the path
     */
    private void makeAtmBndQueues(int[] path, int[] seqAt, IBond[] bndAt) {
        int len = path.length;
        int i = (len - 1) / 2;
        int j = i + 1;
        int nSeqAt = 0;
        int nBndAt = 0;
        if (isOdd((path.length))) {
            seqAt[nSeqAt++] = path[i--];
            bndAt[nBndAt++] = bondMap.get(path[j], path[j - 1]);
        }
        bndAt[nBndAt++] = bondMap.get(path[i], path[i + 1]);
        while (i > 0 && j < len - 1) {
            seqAt[nSeqAt++] = path[i--];
            seqAt[nSeqAt++] = path[j++];
            bndAt[nBndAt++] = bondMap.get(path[i], path[i + 1]);
            bndAt[nBndAt++] = bondMap.get(path[j], path[j - 1]);
        }
    }

    // is a number odd
    private static boolean isOdd(int len) {
        return (len & 0x1) != 0;
    }

    // percentage difference
    private static double percDiff(double prev, double curr) {
        return (prev - curr) / prev;
    }

    /**
     * Recursively visit 'v' and all vertices adjacent to it (excluding 'p')
     * adding all except 'v' to the result array.
     *
     * @param visited visit flags array, should be cleared before search
     * @param result  visited vertices
     * @param p       previous vertex
     * @param v       start vertex
     * @return number of visited vertices
     */
    private int visitAdj(boolean[] visited, int[] result, int p, int v) {
        int n = 0;
        Arrays.fill(visited, false);
        visited[v] = true;
        for (int w : adjList[v]) {
            if (w != p && !visited[w]) {
                n = visit(visited, result, v, w, n);
            }
        }
        visited[v] = false;
        return n;
    }

    /**
     * Recursively visit 'v' and all vertices adjacent to it (excluding 'p')
     * adding them to the result array.
     *
     * @param visited visit flags array, should be cleared before search
     * @param result  visited vertices
     * @param p       previous vertex
     * @param v       start vertex
     * @param n       current number of visited vertices
     * @return new number of visited vertices
     */
    private int visit(boolean[] visited, int[] result, int p, int v, int n) {
        visited[v] = true;
        result[n++] = v;
        for (int w : adjList[v]) {
            if (w != p && !visited[w]) {
                n = visit(visited, result, v, w, n);
            }
        }
        return n;
    }


    /**
     * Access the common atom shared by two bonds.
     *
     * @param bndA first bond
     * @param bndB second bond
     * @return common atom or null if non exists
     */
    private static IAtom getCommon(IBond bndA, IBond bndB) {
        IAtom beg = bndA.getAtom(0);
        IAtom end = bndA.getAtom(1);
        if (bndB.contains(beg))
            return beg;
        else if (bndB.contains(end))
            return end;
        else
            return null;
    }

    /**
     * Congested pair of un-bonded atoms, described by the index of the atoms
     * (fst, snd). The atoms (seqAt) and bonds (bndAt) in the shortest path
     * between the pair are stored as well as a bndAtCode for checking special
     * case ring bond patterns.
     */
    private static final class AtomPair {
        final int fst, snd;
        final int[]   seqAt;
        final IBond[] bndAt;
        final int     bndAtCode;

        /**
         * Which attempt are we trying to resolve this overlap with.
         */
        int attempt = 1;

        public AtomPair(int fst, int snd, int[] seqAt, IBond[] bndAt) {
            this.fst = fst;
            this.snd = snd;
            this.seqAt = seqAt;
            this.bndAt = bndAt;
            this.bndAtCode = bndCode(bndAt);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AtomPair pair = (AtomPair) o;

            return (fst == pair.fst && snd == pair.snd) || (fst == pair.snd && snd == pair.fst);
        }

        @Override
        public int hashCode() {
            return fst ^ snd;
        }

        /**
         * Create the bond code bit mask, lowest bit is whether the path is
         * odd/even then the other bits are whether the bonds are in a ring or
         * not.
         *
         * @param bonds bonds to encode
         * @return the bond code
         */
        static int bndCode(IBond[] bonds) {
            int code = bonds.length & 0x1;
            for (int i = 0; i < bonds.length; i++) {
                if (bonds[i].isInRing()) {
                    code |= 0x1 << (i + 1);
                }
            }
            return code;
        }
    }

    /**
     * Internal - fixed size integer stack.
     */
    private static final class IntStack {
        private final int[] xs;
        private       int   len;

        public IntStack(int cap) {
            this.xs = new int[cap];
        }

        void push(int x) {
            xs[len++] = x;
        }

        void clear() {
            this.len = 0;
        }

        void copyFrom(IntStack stack) {
            System.arraycopy(stack.xs, 0, xs, 0, stack.len);
            this.len = stack.len;
        }

        @Override
        public String toString() {
            return Arrays.toString(Arrays.copyOf(xs, len));
        }
    }

    /**
     * Internal - A hashable tuple of integers, allows to check for previously
     * seen pairs.
     */
    private static final class IntTuple {
        private final int fst, snd;

        public IntTuple(int fst, int snd) {
            this.fst = fst;
            this.snd = snd;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IntTuple that = (IntTuple) o;


            return (this.fst == that.fst && this.snd == that.snd) ||
                    (this.fst == that.snd && this.snd == that.fst);

        }

        @Override
        public int hashCode() {
            return fst ^ snd;
        }
    }
}
