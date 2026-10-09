/* Copyright (C) 2026  Syed Asad Rahman <s9asad@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.isomorphism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.RelevantCycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

/**
 * The query and target of an {@link MCS} search as int graphs, with the atom
 * and bond pairs that may be mapped onto each other. A query atom or bond
 * with an expression ({@link IQueryAtom}, {@link IQueryBond}) is matched by
 * that expression. Any other atom is matched by its symbol, and any other
 * bond by its class. The molecules are only read.
 *
 * @author Syed Asad Rahman
 */
final class MCSGraph {

    /** The number of bond classes: aromatic of any order, without an order, and one for each order, UNSET included. */
    static final int BOND_CLASSES = 2 + IBond.Order.values().length;
    /** The number of radii, from 0, at which atom signatures are compared. */
    static final int RADII = 4;
    /** The bits a bond class, with its ring status, takes in a bond label. */
    private static final int BOND_CLASS_BITS = 5;
    /** The most relevant rings of a ring system that complete rings check. Every ring is checked at each state. */
    private static final int MAX_RINGS = 1024;
    /** The cap on the bound of the relevant rings, below which {@link RelevantCycles} counts them exactly. */
    private static final long RING_BOUND_CAP = 1L << 31;
    /** The carriers of a configuration: the neighbours of a centre, or a double bond's ends and a neighbour of each. */
    private static final int CARRIERS = 4;
    /** The odd constant that mixes the signature of an atom with those of its neighbours. */
    private static final long GOLDEN_GAMMA = 0x9e3779b97f4a7c15L;

    final Side query, target;
    /** The number of symbol classes of both molecules, twice as many with complete rings. */
    final int atomClasses;
    /** Whether the classes decide every pair, as the query has no query atom or bond. */
    final boolean plainQuery;
    /** The compatible bond pairs, bit f of row e, in rows of {@code bondWords} longs. */
    private final int bondWords;
    private long[] bondPairs;
    /**
     * Scratch of {@link #stereoConsistent}: a union-find over the configuration
     * groups of both molecules. Each group has a parent, and a parity of 1
     * when it is inverted against its parent. Group 0 holds the absolute
     * configurations.
     */
    private final int[] groupParent, groupParity;
    /** Scratch of {@link #centre}: the target carrier place of each query carrier, or -1. */
    private final int[] carrierPlaces;

    /**
     * Prepares the search of {@code query} in {@code target}. {@code original}
     * has the atoms of the query, in order, as plain atoms: its symbols guide
     * the search order, and with {@code stereo} its stereo elements are
     * checked. With {@code rings}, ring atoms and bonds pair only with ring
     * ones, chain with chain. {@link #compatiblePairs} works out the pairs.
     */
    MCSGraph(IAtomContainer query, IAtomContainer original, IAtomContainer target, boolean rings,
             boolean stereo) {
        Map<String, Integer> symbols = new HashMap<>();
        this.query = new Side(query, original, rings, symbols, stereo, 1);
        int queryGroups = stereo ? this.query.stereo.groupCount : 0;
        this.target = new Side(target, target, rings, symbols, stereo, 1 + queryGroups);
        atomClasses = rings ? 2 * symbols.size() : symbols.size();
        bondWords = words(this.target.bondCount);
        boolean plain = true;
        for (IAtom atom : this.query.atoms)
            plain &= !(atom instanceof IQueryAtom);
        for (IBond bond : this.query.bonds)
            plain &= !(bond instanceof IQueryBond);
        plainQuery = plain;
        int groups = stereo ? 1 + queryGroups + this.target.stereo.groupCount : 0;
        groupParent = stereo ? new int[groups] : null;
        groupParity = stereo ? new int[groups] : null;
        carrierPlaces = stereo ? new int[CARRIERS] : null;
    }

    static int words(int bits) {
        return (bits + 63) >>> 6;
    }

    /**
     * Packs a number of atoms and a number of bonds into a score, which
     * compares by atoms first or, with {@code bondsFirst}, by bonds first.
     */
    static long score(int atoms, int bonds, boolean bondsFirst) {
        return bondsFirst ? (long) bonds << 32 | atoms : (long) atoms << 32 | bonds;
    }

    static int atoms(long score, boolean bondsFirst) {
        return (int) (bondsFirst ? score : score >>> 32);
    }

    static int bonds(long score, boolean bondsFirst) {
        return (int) (bondsFirst ? score >>> 32 : score);
    }

    /** Whether query bond {@code e} may be mapped onto target bond {@code f}, once {@link #compatiblePairs} has run. */
    boolean bondsMatch(int e, int f) {
        return (bondPairs[e * bondWords + (f >>> 6)] & 1L << f) != 0;
    }

    /** The compatible atom pairs, bit t of row q. This also works out the bond pairs that {@link #bondsMatch} reads. */
    long[] compatiblePairs(Clock clock) {
        // With complete rings the lowest bit of each class is the ring status, also for an atom without a symbol.
        int ringBit = query.rings != null ? 1 : 0;
        bondPairs = compatibility(query.bonds, target.bonds, query.bondClass, target.bondClass,
                                  BOND_CLASSES << ringBit, ringBit, clock);
        return compatibility(query.atoms, target.atoms, query.atomClass, target.atomClass, atomClasses, ringBit,
                             clock);
    }

    /**
     * The target atoms or bonds that each query atom or bond matches, a row
     * each. A row is a copy of the row of its class or, for an expression, the
     * items it matches whose ring status in {@code ringBit} agrees.
     */
    private static long[] compatibility(IChemObject[] queryItems, IChemObject[] targetItems, int[] queryClass,
                                        int[] targetClass, int classes, int ringBit, Clock clock) {
        int words = words(targetItems.length);
        long[] rows = new long[queryItems.length * words], byClass = new long[classes * words];
        for (int j = 0; j < targetItems.length; j++) {
            if (targetClass[j] >= 0)
                byClass[targetClass[j] * words + (j >>> 6)] |= 1L << j;
        }
        for (int i = 0; i < queryItems.length; i++) {
            IChemObject item = queryItems[i];
            if (item instanceof IQueryAtom || item instanceof IQueryBond) {
                // A recursive expression can cost a search of its own for each target atom.
                clock.poll();
                for (int j = 0; j < targetItems.length; j++) {
                    clock.tick();
                    if (((queryClass[i] ^ targetClass[j]) & ringBit) == 0
                        && (item instanceof IQueryAtom ? ((IQueryAtom) item).matches((IAtom) targetItems[j])
                                                       : ((IQueryBond) item).matches((IBond) targetItems[j])))
                        rows[i * words + (j >>> 6)] |= 1L << j;
                }
            } else if (queryClass[i] >= 0) {
                clock.tick(words);
                System.arraycopy(byClass, queryClass[i] * words, rows, i * words, words);
            }
        }
        return rows;
    }

    /**
     * The label bound under the default matching, as a {@link #score} by
     * atoms first, or -1 when the query has expressions. No mapping has more
     * atoms than the sum, over the symbols, of the fewer atoms of each in
     * either molecule. Nor has it more common bonds than the same sum over the
     * bond labels, which are the symbols of the ends and the bond class. A
     * {@code connected} mapping is also capped by the largest component of
     * either molecule without the bonds whose label the other lacks. McSplit
     * prunes with the same count of atom labels {@cdk.cite McCreesh17}. It is
     * the label-frequency bound {@cdk.cite SMSDPro2026}, used to show that a
     * mapping is a maximum one.
     */
    long labelBound(boolean connected) {
        if (!plainQuery)
            return -1;
        int[] queryCount = new int[atomClasses], targetCount = new int[atomClasses];
        for (int c : query.atomClass) {
            if (c >= 0)
                queryCount[c]++;
        }
        for (int c : target.atomClass) {
            if (c >= 0)
                targetCount[c]++;
        }
        long mostAtoms = 0, mostBonds = 0;
        for (int c = 0; c < atomClasses; c++)
            mostAtoms += Math.min(queryCount[c], targetCount[c]);
        long[] queryLabels = query.bondLabels(atomClasses), targetLabels = target.bondLabels(atomClasses);
        for (int i = 0, j = 0; i < queryLabels.length && j < targetLabels.length; ) {
            if (queryLabels[i] < targetLabels[j]) {
                i++;
            } else if (queryLabels[i] > targetLabels[j]) {
                j++;
            } else {
                mostBonds++;
                i++;
                j++;
            }
        }
        if (connected) {
            long queryPart = query.componentBound(atomClasses, targetLabels);
            long targetPart = target.componentBound(atomClasses, queryLabels);
            mostAtoms = Math.min(mostAtoms, Math.min(atoms(queryPart, false), atoms(targetPart, false)));
            mostBonds = Math.min(mostBonds, Math.min(bonds(queryPart, false), bonds(targetPart, false)));
        }
        return score((int) mostAtoms, (int) mostBonds, false);
    }

    /**
     * Whether the mapping keeps the configurations both molecules specify, as
     * far as the mapped atoms fix them, see {@link MCS#withStereochemistry()}.
     * A mapping that fails also fails with any pair added.
     */
    boolean stereoConsistent(int[] queryToTarget, Clock clock) {
        if (query.stereo == null)
            return true;
        clock.tick(query.stereo.count);
        for (int g = 0; g < groupParent.length; g++) {
            groupParent[g] = g;
            groupParity[g] = 0;
        }
        for (int i = 0; i < query.stereo.count; i++) {
            int x = query.stereo.specified[i], y = image(queryToTarget, x);
            if (y < 0 || target.stereo.config[y] == 0)
                continue;
            int parity = x < query.atomCount ? centre(queryToTarget, x, y) : doubleBond(queryToTarget, x, y);
            if (parity >= 0 && !join(query.stereo.group[x], target.stereo.group[y], parity))
                return false;
        }
        return true;
    }

    /**
     * The target index at the image of query index {@code x}: the image of a
     * centre, or the bond between the images of the ends of a double bond. It
     * is -1 if there is none.
     */
    private int image(int[] queryToTarget, int x) {
        if (x < query.atomCount)
            return queryToTarget[x];
        int v1 = queryToTarget[query.stereo.carrier(x, Stereo.FIRST_END)];
        int v2 = queryToTarget[query.stereo.carrier(x, Stereo.SECOND_END)];
        int f = v1 < 0 || v2 < 0 ? -1 : target.bondBetween(v1, v2);
        return f < 0 ? -1 : target.atomCount + f;
    }

    /**
     * Compares query centre {@code u} with target centre {@code v}, its image,
     * through the permutation that pairs their carriers. The result is 1 when
     * they disagree and 0 when they agree. It is -1 when fewer than three
     * carriers pair up.
     */
    private int centre(int[] queryToTarget, int u, int v) {
        int paired = 0, taken = 0;
        for (int i = 0; i < CARRIERS; i++) {
            int c = query.stereo.carrier(u, i);
            int x = c == u ? -1 : queryToTarget[c];
            carrierPlaces[i] = -1;
            for (int j = 0; x >= 0 && j < CARRIERS; j++) {
                if (target.stereo.carrier(v, j) == x)
                    carrierPlaces[i] = j;
            }
            if (carrierPlaces[i] >= 0) {
                paired++;
                taken |= 1 << carrierPlaces[i];
            }
        }
        if (paired < CARRIERS - 1)
            return -1;
        // The one carrier left on each side pairs with the other, at the place no carrier has taken.
        int left = 0;
        while ((taken & 1 << left) != 0)
            left++;
        int parity = query.stereo.config[u] == target.stereo.config[v] ? 0 : 1;
        for (int i = 0; i < CARRIERS; i++) {
            if (carrierPlaces[i] < 0)
                carrierPlaces[i] = left;
            for (int j = 0; j < i; j++) {
                if (carrierPlaces[j] > carrierPlaces[i])
                    parity ^= 1;
            }
        }
        return parity;
    }

    /**
     * Compares query double bond {@code x} with target double bond {@code y}
     * between the images of its ends, as {@link #centre} does for a centre. A
     * mapped neighbour of an end that is not the stored one reads the
     * configuration inverted.
     */
    private int doubleBond(int[] queryToTarget, int x, int y) {
        int u1 = query.stereo.carrier(x, Stereo.FIRST_END), u2 = query.stereo.carrier(x, Stereo.SECOND_END);
        int v1 = queryToTarget[u1], v2 = queryToTarget[u2];
        // The place of the stored neighbour at target end v1.
        int first = target.stereo.carrier(y, Stereo.FIRST_END) == v1 ? 0 : 1;
        int a = end(queryToTarget, u1, u2, v1, query.stereo.carrier(x, 0), target.stereo.carrier(y, first));
        int b = end(queryToTarget, u2, u1, v2, query.stereo.carrier(x, 1), target.stereo.carrier(y, 1 - first));
        if (a < 0 || b < 0)
            return -1;
        return a ^ b ^ (query.stereo.config[x] == target.stereo.config[y] ? 0 : 1);
    }

    /**
     * Compares the stored neighbours at query end {@code u} and its image,
     * target end {@code v}. They are compared through a neighbour of
     * {@code u}, other than {@code w}, whose image is next to {@code v}. The
     * result is 1 when it is the stored one on one side only, 0 when on both
     * or neither, and -1 when there is no such neighbour.
     */
    private int end(int[] queryToTarget, int u, int w, int v, int queryStored, int targetStored) {
        for (int i = query.start[u]; i < query.start[u + 1]; i++) {
            int x = query.neighbour[i], y = x == w ? -1 : queryToTarget[x];
            if (y >= 0 && target.bondBetween(v, y) >= 0)
                return (x == queryStored) == (y == targetStored) ? 0 : 1;
        }
        return -1;
    }

    /**
     * Whether groups {@code a} and {@code b} can each be kept or inverted so
     * that one more pair of configurations agrees, given the pairs compared
     * before. The {@code parity} is 1 when the two disagree as they are.
     */
    private boolean join(int a, int b, int parity) {
        while (groupParent[a] != a) {
            parity ^= groupParity[a];
            a = groupParent[a];
        }
        while (groupParent[b] != b) {
            parity ^= groupParity[b];
            b = groupParent[b];
        }
        if (a == b)
            return parity == 0;
        groupParent[a] = b;
        groupParity[a] = parity;
        return true;
    }

    /**
     * Colour refinement {@cdk.cite MOR65} from dense colours. Cells are split
     * by the sorted ranks of the neighbours until none splits. With
     * {@code individualise}, the atom of lowest index in the lowest shared cell
     * is then put before its peers and the cells split again. This goes on
     * until each atom has a rank of its own, breaking ties as in
     * {@cdk.cite WEI89}. A rank is where the cell starts. A round sorts only
     * the neighbours of atoms that changed cell, and the largest part of a
     * split cell keeps its name. A long chain then does not cost a full sort
     * each round. Not {@code Canon}, which the time limit or an interrupt
     * cannot stop.
     */
    static int[] refine(int[] start, int[] neighbour, int[] colour, boolean individualise, Clock clock) {
        return new Refinement(start, neighbour, colour, clock).ranks(individualise);
    }

    /**
     * One colour refinement, see {@link #refine}. The atoms are kept in rank
     * order, and each cell is a run of places in that order.
     */
    private static final class Refinement {

        private final int[] start, neighbour;
        private final Clock clock;
        private final int n;
        /** The atoms in rank order, and the place of each atom in it. */
        private final int[] order, place;
        /** The cell of each atom, and the place where each cell starts and its size. */
        private final int[] cell, cellStart, cellSize;
        private int cells;
        /** The sorted ranks of the neighbours of each atom, at its places in the neighbour list. */
        private final int[] ranks;
        /** The atoms to sort in a round, the first {@link #toSortCount}, and the last round each was listed for. */
        private final int[] toSort, listed;
        private int toSortCount;
        /** The atoms that changed cell in a round, the first {@link #movedCount}. */
        private final int[] moved;
        private int movedCount;
        private int round = 1;
        /** Room to sort the atoms of one cell, and the atoms to sort as keys by cell. */
        private final Integer[] sorting;
        private final long[] byCell;
        /** Orders atoms by the sorted ranks of their neighbours. */
        private final Comparator<Integer> byNeighbours;

        /** Starts with a cell for each colour, and every atom listed to sort. */
        Refinement(int[] start, int[] neighbour, int[] colour, Clock clock) {
            this.start = start;
            this.neighbour = neighbour;
            this.clock = clock;
            n = colour.length;
            order = new int[n];
            place = new int[n];
            cell = new int[n];
            cellStart = new int[n];
            cellSize = new int[n];
            ranks = new int[neighbour.length];
            toSort = new int[n];
            listed = new int[n];
            moved = new int[n];
            sorting = new Integer[n];
            byCell = new long[n];
            byNeighbours = (a, b) -> {
                int c = Integer.compare(start[a + 1] - start[a], start[b + 1] - start[b]);
                for (int i = start[a], j = start[b]; c == 0 && i < start[a + 1]; i++, j++)
                    c = Integer.compare(ranks[i], ranks[j]);
                return c;
            };
            for (int c : colour)
                cellSize[c]++;
            while (cells < n && cellSize[cells] > 0) {
                cellStart[cells] = cells == 0 ? 0 : cellStart[cells - 1] + cellSize[cells - 1];
                cells++;
            }
            int[] filled = new int[n];
            for (int a = 0; a < n; a++) {
                cell[a] = colour[a];
                place[a] = cellStart[cell[a]] + filled[cell[a]]++;
                order[place[a]] = a;
                toSort[a] = a;
            }
            toSortCount = n;
            Arrays.fill(listed, round);
        }

        /** Refines the cells, and gives the rank of each atom. */
        int[] ranks(boolean individualise) {
            for (int firstShared = 0; ; round++) {
                movedCount = 0;
                int shared = sortByCell();
                // Every rank is read before any cell splits.
                for (int i = 0, j; i < shared; i = j) {
                    j = cellEnd(i, shared);
                    readRanks(i, j);
                }
                for (int i = 0, j; i < shared; i = j) {
                    j = cellEnd(i, shared);
                    splitCell(i, j);
                }
                if (movedCount == 0) {
                    while (firstShared < n && cellSize[cell[order[firstShared]]] == 1)
                        firstShared++;
                    if (!individualise || firstShared == n)
                        break;
                    individualise(firstShared);
                }
                listNeighbours();
            }
            int[] rank = new int[n];
            for (int a = 0; a < n; a++)
                rank[a] = cellStart[cell[a]];
            return rank;
        }

        /**
         * Keeps the atoms to sort whose cell has other atoms, in order of
         * cell and then of atom, and gives their number.
         */
        private int sortByCell() {
            int shared = 0;
            for (int i = 0; i < toSortCount; i++) {
                int a = toSort[i];
                if (cellSize[cell[a]] > 1)
                    byCell[shared++] = (long) cell[a] << 32 | a;
            }
            Arrays.sort(byCell, 0, shared);
            for (int i = 0; i < shared; i++)
                toSort[i] = (int) byCell[i];
            return shared;
        }

        /** The end of the atoms to sort from place {@code i} that are in its cell. */
        private int cellEnd(int i, int shared) {
            int j = i + 1;
            while (j < shared && cell[toSort[j]] == cell[toSort[i]])
                j++;
            return j;
        }

        /**
         * Sorts the neighbour ranks of the atoms to sort from {@code from} to
         * {@code to - 1}, all of one cell, and of one other atom of the cell
         * if there is one. That atom stands for the rest, which have the same
         * ranks.
         */
        private void readRanks(int from, int to) {
            for (int i = from; i < to; i++)
                sortNeighbourRanks(toSort[i]);
            int standIn = unlisted(cell[toSort[from]]);
            if (standIn >= 0)
                sortNeighbourRanks(standIn);
        }

        /** Puts the sorted ranks of the neighbours of atom {@code a} at its places in {@link #ranks}. */
        private void sortNeighbourRanks(int a) {
            clock.tick(1 + start[a + 1] - start[a]);
            for (int j = start[a]; j < start[a + 1]; j++)
                ranks[j] = cellStart[cell[neighbour[j]]];
            Arrays.sort(ranks, start[a], start[a + 1]);
        }

        /** An atom of cell {@code c} that is not listed for this round, or -1 if there is none. */
        private int unlisted(int c) {
            for (int p = cellStart[c]; p < cellStart[c] + cellSize[c]; p++) {
                if (listed[order[p]] != round)
                    return order[p];
            }
            return -1;
        }

        /**
         * Splits the cell of the atoms to sort from {@code from} to
         * {@code to - 1} into runs of equal neighbour ranks. The unlisted
         * atoms of the cell form one run, that of their stand-in. Each run
         * becomes a cell, and the largest keeps the name of the cell.
         */
        private void splitCell(int from, int to) {
            int c = cell[toSort[from]], standIn = unlisted(c), unlistedCount = cellSize[c] - (to - from);
            int count = 0;
            for (int i = from; i < to; i++)
                sorting[count++] = toSort[i];
            if (standIn >= 0)
                sorting[count++] = standIn;
            Arrays.sort(sorting, 0, count, byNeighbours);
            clock.tick(count);
            if (runEnd(0, count) == count)
                return;
            int largestRun = 0, largestSize = 0, standInRun = -1;
            for (int x = 0, y; x < count; x = y) {
                y = runEnd(x, count);
                int size = runSize(x, y, standIn, unlistedCount);
                if (size > largestSize) {
                    largestRun = x;
                    largestSize = size;
                }
                for (int z = x; z < y; z++) {
                    if (sorting[z] == standIn)
                        standInRun = x;
                }
            }
            // The listed atoms move to their places in run order, around the run of the stand-in. That run then
            // holds the unlisted atoms.
            int renamed = -1;
            for (int x = 0, y, p = cellStart[c]; x < count; x = y) {
                y = runEnd(x, count);
                int size = runSize(x, y, standIn, unlistedCount);
                int name = x == largestRun ? c : cells++;
                cellStart[name] = p;
                cellSize[name] = size;
                if (x == standInRun) {
                    if (name != c)
                        renamed = name;
                } else {
                    for (int z = x; z < y; z++) {
                        moveTo(sorting[z], p + z - x);
                        if (name != c)
                            changeCell(sorting[z], name);
                    }
                }
                p += size;
            }
            if (renamed >= 0) {
                for (int p = cellStart[renamed]; p < cellStart[renamed] + cellSize[renamed]; p++)
                    changeCell(order[p], renamed);
            }
        }

        /** The end of the run of sorted atoms from {@code x} with equal neighbour ranks. */
        private int runEnd(int x, int count) {
            int y = x + 1;
            while (y < count && byNeighbours.compare(sorting[x], sorting[y]) == 0)
                y++;
            return y;
        }

        /**
         * The atoms in the run of sorted atoms from {@code x} to {@code y - 1}.
         * The stand-in counts for every unlisted atom.
         */
        private int runSize(int x, int y, int standIn, int unlistedCount) {
            int size = 0;
            for (int z = x; z < y; z++)
                size += sorting[z] == standIn ? unlistedCount : 1;
            return size;
        }

        /**
         * Puts the atom of lowest index in the shared cell that starts at place
         * {@code first} in a cell of its own, before its peers.
         */
        private void individualise(int first) {
            int c = cell[order[first]], pick = first;
            clock.tick(cellSize[c]);
            for (int p = first + 1; p < first + cellSize[c]; p++) {
                if (order[p] < order[pick])
                    pick = p;
            }
            int a = order[pick];
            moveTo(a, first);
            cellStart[cells] = first;
            cellSize[cells] = 1;
            cellStart[c]++;
            cellSize[c]--;
            changeCell(a, cells++);
        }

        /** Puts atom {@code a} at place {@code p}, and the atom that was there at the old place of a. */
        private void moveTo(int a, int p) {
            int b = order[p];
            order[place[a]] = b;
            place[b] = place[a];
            order[p] = a;
            place[a] = p;
        }

        private void changeCell(int a, int c) {
            cell[a] = c;
            moved[movedCount++] = a;
        }

        /** Lists the neighbours of the atoms that changed cell, to sort in the next round. */
        private void listNeighbours() {
            toSortCount = 0;
            for (int i = 0; i < movedCount; i++) {
                int a = moved[i];
                clock.tick(1 + start[a + 1] - start[a]);
                for (int j = start[a]; j < start[a + 1]; j++) {
                    if (listed[neighbour[j]] != round + 1) {
                        listed[neighbour[j]] = round + 1;
                        toSort[toSortCount++] = neighbour[j];
                    }
                }
            }
        }
    }

    /** The rank of each key among the distinct keys, from 0. */
    static int[] denseRanks(long[] keys) {
        long[] sorted = keys.clone();
        Arrays.sort(sorted);
        int size = 0;
        for (int i = 0; i < sorted.length; i++) {
            if (i == 0 || sorted[i] != sorted[i - 1])
                sorted[size++] = sorted[i];
        }
        int[] ranks = new int[keys.length];
        for (int i = 0; i < keys.length; i++)
            ranks[i] = Arrays.binarySearch(sorted, 0, size, keys[i]);
        return ranks;
    }

    /** Mixes the bits of {@code x}, with the finaliser of SplitMix64. */
    private static long mix(long x) {
        x = (x ^ x >>> 30) * 0xbf58476d1ce4e5b9L;
        x = (x ^ x >>> 27) * 0x94d049bb133111ebL;
        return x ^ x >>> 31;
    }

    /** One molecule of the search as an int graph. */
    static final class Side {

        final int atomCount, bondCount;
        /** The neighbours of atom a, and the bonds to them, are at places start[a] to start[a + 1] - 1. */
        final int[] start, neighbour, neighbourBond;
        /** The hash of the symbol of each atom of the original molecule, 0 for none, for the search order and seed. */
        final int[] symbolHash;
        /** The symbol class of each atom, negative for an atom without a symbol, which matches nothing. */
        final int[] atomClass;
        final int[] bondClass;
        /** The ring atoms, bonds and rings, for complete rings, or null. */
        final Rings rings;
        /** The configurations, or null unless stereochemistry is checked. */
        final Stereo stereo;
        final IAtom[] atoms;
        final IBond[] bonds;

        /**
         * The graph of {@code molecule}, whose atoms {@code original} has as
         * plain atoms. Symbols are numbered across both molecules. With
         * {@code withStereo} the configuration groups are numbered from
         * {@code firstGroup}.
         */
        Side(IAtomContainer molecule, IAtomContainer original, boolean withRings, Map<String, Integer> symbols,
             boolean withStereo, int firstGroup) {
            atomCount = molecule.getAtomCount();
            bondCount = molecule.getBondCount();
            atoms = new IAtom[atomCount];
            for (int i = 0; i < atomCount; i++)
                atoms[i] = molecule.getAtom(i);
            bonds = new IBond[bondCount];
            for (int i = 0; i < bondCount; i++)
                bonds[i] = molecule.getBond(i);
            int[][] lists = adjacency(ends(atoms, bonds), atomCount);
            start = lists[0];
            neighbour = lists[1];
            neighbourBond = lists[2];
            rings = withRings ? new Rings(start, neighbour, neighbourBond, bondCount) : null;
            symbolHash = new int[atomCount];
            atomClass = new int[atomCount];
            for (int i = 0; i < atomCount; i++) {
                String label = original.getAtom(i).getSymbol(), symbol = atoms[i].getSymbol();
                symbolHash[i] = label == null ? 0 : label.hashCode();
                atomClass[i] = symbol == null ? -1 : symbols.computeIfAbsent(symbol, s -> symbols.size());
                if (withRings)
                    atomClass[i] = 2 * atomClass[i] + (rings.ringAtom[i] ? 1 : 0);
            }
            bondClass = new int[bondCount];
            for (int i = 0; i < bondCount; i++) {
                IBond.Order order = bonds[i].getOrder();
                bondClass[i] = bonds[i].isAromatic() ? 0 : order == null ? 1 : 2 + order.ordinal();
                if (withRings)
                    bondClass[i] = 2 * bondClass[i] + (rings.ringBond[i] ? 1 : 0);
            }
            stereo = withStereo ? new Stereo(original, start, neighbour, firstGroup) : null;
        }

        int degree(int a) {
            return start[a + 1] - start[a];
        }

        int maxDegree() {
            int max = 0;
            for (int a = 0; a < atomCount; a++)
                max = Math.max(max, degree(a));
            return max;
        }

        /**
         * A bound on the bonds among at most {@code kept} atoms. Leaving out
         * one atom loses at least the smallest degree. Leaving out two loses
         * at least the sum of the two smallest degrees, or one less if two
         * bonded atoms have that sum. With more atoms left out, every bond is
         * counted.
         */
        int bondBound(int kept, Clock clock) {
            if (kept < 2)
                return 0;
            int omitted = atomCount - kept;
            if (omitted < 1 || omitted > 2)
                return bondCount;
            int first = Integer.MAX_VALUE, second = Integer.MAX_VALUE;
            for (int a = 0; a < atomCount; a++) {
                clock.tick();
                int degree = degree(a);
                if (degree < first) {
                    second = first;
                    first = degree;
                } else if (degree < second) {
                    second = degree;
                }
            }
            if (omitted == 1)
                return bondCount - first;
            int sum = first + second;
            for (int a = 0; a < atomCount; a++) {
                clock.tick(1 + degree(a));
                for (int i = start[a]; i < start[a + 1]; i++) {
                    if (degree(a) + degree(neighbour[i]) == sum)
                        return bondCount - sum + 1;
                }
            }
            return bondCount - sum;
        }

        /** The bond between atoms {@code a} and {@code b}, or -1 if there is none. */
        int bondBetween(int a, int b) {
            for (int i = start[a]; i < start[a + 1]; i++) {
                if (neighbour[i] == b)
                    return neighbourBond[i];
            }
            return -1;
        }

        /**
         * Whether a configuration reads atom {@code a}, as a centre or carrier,
         * or as an end of a double bond or next to one. Mapping or swapping
         * such an atom can change what {@link MCSGraph#stereoConsistent} finds.
         * False unless stereochemistry is checked.
         */
        boolean configured(int a) {
            return stereo != null && stereo.reads[a];
        }

        /** The dense ranks of the atoms by their number of choices, then their degree, then their symbol. */
        int[] invariants(int[] choices) {
            long[] keys = new long[atomCount];
            for (int a = 0; a < atomCount; a++)
                keys[a] = symbolHash[a];
            int[] symbol = denseRanks(keys);
            long degrees = maxDegree() + 1;
            for (int a = 0; a < atomCount; a++)
                keys[a] = (choices[a] * degrees + degree(a)) * atomCount + symbol[a];
            return denseRanks(keys);
        }

        /** Atom signatures by radius: symbol and degree at 0, then each mixed with the sum over its neighbours. */
        long[][] signatures(Clock clock) {
            long[][] signature = new long[RADII][atomCount];
            for (int a = 0; a < atomCount; a++)
                signature[0][a] = mix(symbolHash[a] * 31L + degree(a));
            for (int r = 1; r < RADII; r++) {
                for (int a = 0; a < atomCount; a++) {
                    clock.tick(1 + degree(a));
                    long sum = 0;
                    for (int i = start[a]; i < start[a + 1]; i++)
                        sum += mix(signature[r - 1][neighbour[i]]);
                    signature[r][a] = mix(signature[r - 1][a] * GOLDEN_GAMMA + sum);
                }
            }
            return signature;
        }

        /** The sorted labels of the bonds whose ends have symbols, see {@link MCSGraph#labelBound}. */
        long[] bondLabels(int atomClasses) {
            long[] labels = new long[bondCount];
            int size = 0;
            for (int a = 0; a < atomCount; a++) {
                for (int i = start[a]; i < start[a + 1]; i++) {
                    int b = neighbour[i];
                    if (b > a && atomClass[a] >= 0 && atomClass[b] >= 0)
                        labels[size++] = bondLabel(a, b, neighbourBond[i], atomClasses);
                }
            }
            labels = Arrays.copyOf(labels, size);
            Arrays.sort(labels);
            return labels;
        }

        /** The most atoms and bonds of a component once the bonds whose label the other molecule lacks are removed. */
        long componentBound(int atomClasses, long[] otherLabels) {
            boolean[] seen = new boolean[atomCount];
            int[] queue = new int[atomCount];
            int mostAtoms = 0, mostBonds = 0;
            for (int root = 0; root < atomCount; root++) {
                if (seen[root] || atomClass[root] < 0)
                    continue;
                int size = 1, links = 0;
                queue[0] = root;
                seen[root] = true;
                for (int k = 0; k < size; k++) {
                    int a = queue[k];
                    for (int i = start[a]; i < start[a + 1]; i++) {
                        int b = neighbour[i];
                        if (atomClass[b] < 0
                            || Arrays.binarySearch(otherLabels, bondLabel(a, b, neighbourBond[i], atomClasses)) < 0)
                            continue;
                        links++;
                        if (!seen[b]) {
                            seen[b] = true;
                            queue[size++] = b;
                        }
                    }
                }
                mostAtoms = Math.max(mostAtoms, size);
                // Each bond is met from both ends.
                mostBonds = Math.max(mostBonds, links / 2);
            }
            return score(mostAtoms, mostBonds, false);
        }

        /** The label of the bond between atoms {@code a} and {@code b}: the symbols of its ends and its class. */
        long bondLabel(int a, int b, int bond, int atomClasses) {
            int x = atomClass[a], y = atomClass[b];
            return ((long) Math.min(x, y) * atomClasses + Math.max(x, y)) << BOND_CLASS_BITS | bondClass[bond];
        }
    }

    /** The atoms of each bond, its begin and then its end. */
    private static int[] ends(IAtom[] atoms, IBond[] bonds) {
        int[] ends = new int[2 * bonds.length];
        Map<IAtom, Integer> index = null;
        for (int e = 0; e < ends.length; e++) {
            IAtom atom = (e & 1) == 0 ? bonds[e >> 1].getBegin() : bonds[e >> 1].getEnd();
            int i = atom != null ? atom.getIndex() : -1;
            if (atom != null && (i < 0 || i >= atoms.length || atoms[i] != atom)) {
                // The atom does not know its index. Look up its underlying atom by identity, in a map built once.
                if (index == null) {
                    index = new IdentityHashMap<>();
                    for (int a = 0; a < atoms.length; a++)
                        index.put(AtomRef.deref(atoms[a]), a);
                }
                Integer known = index.get(AtomRef.deref(atom));
                i = known != null ? known : -1;
            }
            if (i < 0)
                throw new IllegalArgumentException("A bond joins an atom that is not in the molecule");
            ends[e] = i;
        }
        return ends;
    }

    /**
     * The start of each atom in the neighbour lists, the neighbours and the
     * bonds to them. A bond from an atom to itself, or a second bond between
     * the same atoms, is refused, as it is not a molecule.
     */
    private static int[][] adjacency(int[] ends, int atoms) {
        int[] start = new int[atoms + 1];
        for (int end : ends)
            start[end + 1]++;
        for (int a = 0; a < atoms; a++)
            start[a + 1] += start[a];
        int[] next = Arrays.copyOf(start, atoms), neighbour = new int[ends.length], bond = new int[ends.length];
        for (int e = 0; e < ends.length; e++) {
            int a = ends[e];
            neighbour[next[a]] = ends[e ^ 1];
            bond[next[a]++] = e >> 1;
        }
        int[] seen = new int[atoms];
        for (int a = 0; a < atoms; a++) {
            for (int i = start[a]; i < start[a + 1]; i++) {
                if (neighbour[i] == a)
                    throw new IllegalArgumentException("A bond joins an atom to itself");
                if (seen[neighbour[i]] == a + 1)
                    throw new IllegalArgumentException("Two bonds join the same atoms");
                seen[neighbour[i]] = a + 1;
            }
        }
        return new int[][]{start, neighbour, bond};
    }

    /**
     * The ring atoms and bonds of a molecule, and its relevant rings
     * {@cdk.cite Vismara97}. They are found from the bonds as given, not from
     * the ring flags. Each ring is stored as its atoms in ring order, followed
     * by the bond from each atom to the next.
     */
    static final class Rings {

        final boolean[] ringAtom, ringBond;
        final int[][] rings;
        /**
         * The atoms of a ring system with too many relevant rings, or 0. The
         * rings of that system and of later ones are left out.
         */
        final int refusedAtoms;

        Rings(int[] start, int[] neighbour, int[] neighbourBond, int bondCount) {
            int n = start.length - 1, refused = 0;
            ringBond = ringBonds(start, neighbour, neighbourBond, bondCount);
            ringAtom = new boolean[n];
            for (int a = 0; a < n; a++) {
                for (int i = start[a]; i < start[a + 1]; i++) {
                    if (ringBond[neighbourBond[i]])
                        ringAtom[a] = true;
                }
            }
            // The ring systems are the atoms joined by ring bonds.
            List<int[]> found = new ArrayList<>();
            int[] place = new int[n];
            Arrays.fill(place, -1);
            for (int s = 0; s < n && refused == 0; s++) {
                if (!ringAtom[s] || place[s] >= 0)
                    continue;
                int[] members = members(s, start, neighbour, neighbourBond, place);
                int[][] system = new int[members.length][];
                int bonds = 0;
                for (int k = 0; k < members.length; k++) {
                    system[k] = neighbours(members[k], start, neighbour, neighbourBond, place);
                    bonds += system[k].length;
                }
                bonds /= 2;
                // Each ring as a closed path, with its first atom again at the end.
                int[][] paths = new int[0][];
                if (bonds == members.length) {
                    // As many bonds as atoms is a single ring.
                    paths = new int[][]{GraphUtil.cycle(system, IntStream.range(0, members.length).toArray())};
                } else if (bonds - members.length + 1 > MAX_RINGS) {
                    // There are at least as many relevant rings as the cyclomatic number. They need not be found.
                    refused = members.length;
                } else {
                    RelevantCycles relevant = ringBound(system) < RING_BOUND_CAP ? new RelevantCycles(system) : null;
                    if (relevant == null || relevant.size() > MAX_RINGS)
                        refused = members.length;
                    else
                        paths = relevant.paths();
                }
                for (int[] path : paths)
                    found.add(ring(path, members, start, neighbour, neighbourBond));
            }
            rings = found.toArray(new int[0][]);
            this.refusedAtoms = refused;
        }

        /**
         * The ring bonds, found by low-link without the recursion of
         * {@code RingSearch}. A tree bond is a ring bond when the atoms below
         * it reach above it, and any other bond is a ring bond. The bond to
         * the parent is skipped by its index. A bond on no ring splits its
         * part of the molecule in two.
         */
        static boolean[] ringBonds(int[] start, int[] neighbour, int[] neighbourBond, int bondCount) {
            int n = start.length - 1, time = 0;
            boolean[] ringBond = new boolean[bondCount];
            int[] visited = new int[n], low = new int[n], via = new int[n], next = start.clone(), stack = new int[n];
            for (int r = 0, top = 0; r < n; r++) {
                if (visited[r] > 0)
                    continue;
                visited[r] = low[r] = ++time;
                via[r] = -1;
                stack[top++] = r;
                while (top > 0) {
                    int a = stack[top - 1], i = next[a]++;
                    if (i == start[a + 1]) {
                        if (--top > 0) {
                            int p = stack[top - 1];
                            low[p] = Math.min(low[p], low[a]);
                            if (low[a] <= visited[p])
                                ringBond[via[a]] = true;
                        }
                    } else if (visited[neighbour[i]] == 0) {
                        visited[neighbour[i]] = low[neighbour[i]] = ++time;
                        via[neighbour[i]] = neighbourBond[i];
                        stack[top++] = neighbour[i];
                    } else if (neighbourBond[i] != via[a]) {
                        low[a] = Math.min(low[a], visited[neighbour[i]]);
                        ringBond[neighbourBond[i]] = true;
                    }
                }
            }
            return ringBond;
        }

        /** The atoms of the ring system of atom {@code s}, in the order reached, with the index of each in place. */
        private int[] members(int s, int[] start, int[] neighbour, int[] neighbourBond, int[] place) {
            int[] members = new int[ringAtom.length];
            int size = 1;
            members[0] = s;
            place[s] = 0;
            for (int k = 0; k < size; k++) {
                for (int i = start[members[k]]; i < start[members[k] + 1]; i++) {
                    if (ringBond[neighbourBond[i]] && place[neighbour[i]] < 0) {
                        place[neighbour[i]] = size;
                        members[size++] = neighbour[i];
                    }
                }
            }
            return Arrays.copyOf(members, size);
        }

        /** The places of the neighbours of atom {@code a} through ring bonds. */
        private int[] neighbours(int a, int[] start, int[] neighbour, int[] neighbourBond, int[] place) {
            int[] row = new int[start[a + 1] - start[a]];
            int d = 0;
            for (int i = start[a]; i < start[a + 1]; i++) {
                if (ringBond[neighbourBond[i]])
                    row[d++] = place[neighbour[i]];
            }
            return Arrays.copyOf(row, d);
        }

        /** A closed path of places as the atoms of a ring, followed by the bond from each atom to the next. */
        private static int[] ring(int[] path, int[] members, int[] start, int[] neighbour, int[] neighbourBond) {
            int len = path.length - 1;
            int[] ring = new int[2 * len];
            for (int k = 0; k < len; k++) {
                int a = members[path[k]], b = members[path[k + 1]], i = start[a];
                while (neighbour[i] != b)
                    i++;
                ring[k] = a;
                ring[len + k] = neighbourBond[i];
            }
            return ring;
        }

        /**
         * Whether each mapped ring atom and each common ring bond lies on a
         * ring whose atoms are all mapped or open, and whose bonds between two
         * mapped atoms are all common.
         */
        boolean covers(boolean[] mapped, boolean[] open, boolean[] common) {
            boolean[] atomCovered = new boolean[ringAtom.length], bondCovered = new boolean[ringBond.length];
            for (int[] ring : rings) {
                int len = ring.length / 2;
                if (!coversRing(ring, mapped, open, common))
                    continue;
                for (int k = 0; k < len; k++)
                    atomCovered[ring[k]] = bondCovered[ring[len + k]] = true;
            }
            for (int a = 0; a < ringAtom.length; a++) {
                if (ringAtom[a] && mapped[a] && !atomCovered[a])
                    return false;
            }
            for (int e = 0; e < ringBond.length; e++) {
                if (ringBond[e] && common[e] && !bondCovered[e])
                    return false;
            }
            return true;
        }

        private static boolean coversRing(int[] ring, boolean[] mapped, boolean[] open, boolean[] common) {
            int len = ring.length / 2;
            for (int k = 0; k < len; k++) {
                int a = ring[k], b = ring[(k + 1) % len];
                if (!mapped[a] && !open[a] || mapped[a] && mapped[b] && !common[ring[len + k]])
                    return false;
            }
            return true;
        }
    }

    /**
     * A bound on the relevant rings of a ring system, capped at
     * {@link #RING_BOUND_CAP}. From each of its atoms, a relevant ring is two
     * shortest paths that meet at a bond or an atom. Summed over the atoms,
     * the pairs of such paths count each ring at least three times.
     */
    private static long ringBound(int[][] graph) {
        long bound = 0;
        int[] dist = new int[graph.length], queue = new int[graph.length];
        long[] paths = new long[graph.length];
        for (int r = 0; r < graph.length && bound < RING_BOUND_CAP; r++) {
            Arrays.fill(dist, -1);
            dist[r] = 0;
            paths[r] = 1;
            queue[0] = r;
            // The shortest paths to an atom are all counted once it leaves the queue.
            for (int head = 0, tail = 1; head < tail; head++) {
                int a = queue[head];
                long before = 0;
                for (int b : graph[a]) {
                    if (dist[b] < 0) {
                        dist[b] = dist[a] + 1;
                        paths[b] = 0;
                        queue[tail++] = b;
                    }
                    if (dist[b] > dist[a])
                        paths[b] = Math.min(paths[b] + paths[a], RING_BOUND_CAP);
                    else if (dist[b] == dist[a] && b < a)
                        bound = Math.min(bound + paths[a] * paths[b], RING_BOUND_CAP);
                    else if (dist[b] < dist[a]) {
                        bound = Math.min(bound + before * paths[b], RING_BOUND_CAP);
                        before = Math.min(before + paths[b], RING_BOUND_CAP);
                    }
                }
            }
        }
        return bound;
    }

    /**
     * The configurations of a molecule. Index x is a tetrahedral centre when
     * it is below the atom count, and otherwise the double bond x minus the
     * atom count. A configuration is 1 or 2, or 0 for none. The carriers of a
     * centre are its four neighbours, where the centre itself stands for an
     * implicit hydrogen. A double bond with an end of three or more other
     * neighbours is left out, as one neighbour does not stand for its side.
     */
    private static final class Stereo {

        /** The carrier places of the ends of a double bond, whose stored neighbours are at places 0 and 1. */
        static final int FIRST_END = 2, SECOND_END = 3;
        /** The configuration at each index, 0 for none. */
        final int[] config;
        /** The group of each configuration, 0 for absolute, numbered across both molecules from 1. */
        final int[] group;
        /** The number of racemic and relative groups of the molecule. */
        final int groupCount;
        /** The carriers of each configuration, four places for each index. */
        final int[] carriers;
        /** The indices with a configuration, the first {@link #count} of the array. */
        final int[] specified;
        /** Whether a configuration reads each atom, see {@link Side#configured}. */
        final boolean[] reads;
        int count;

        /** Reads the stereo elements, and numbers the racemic and relative groups from {@code firstGroup}. */
        Stereo(IAtomContainer molecule, int[] start, int[] neighbour, int firstGroup) {
            int atoms = molecule.getAtomCount();
            config = new int[atoms + molecule.getBondCount()];
            group = new int[config.length];
            carriers = new int[CARRIERS * config.length];
            specified = new int[config.length];
            reads = new boolean[atoms];
            Map<Integer, Integer> groups = new HashMap<>();
            for (IStereoElement<?, ?> element : molecule.stereoElements()) {
                int x, order = element.getConfigOrder();
                if (element.getConfigClass() == IStereoElement.TH) {
                    x = molecule.indexOf((IAtom) element.getFocus());
                    for (int i = 0; i < CARRIERS; i++)
                        carriers[CARRIERS * x + i] = molecule.indexOf((IAtom) element.getCarriers().get(i));
                } else if (element.getConfigClass() == IStereoElement.CT) {
                    IBond bond = (IBond) element.getFocus();
                    int u = molecule.indexOf(bond.getBegin()), v = molecule.indexOf(bond.getEnd());
                    if (start[u + 1] - start[u] > 3 || start[v + 1] - start[v] > 3)
                        continue;
                    x = atoms + molecule.indexOf(bond);
                    carriers[CARRIERS * x + FIRST_END] = u;
                    carriers[CARRIERS * x + SECOND_END] = v;
                    // The stored neighbour of an end is the other atom of the carrier bond there.
                    for (Object carrier : element.getCarriers()) {
                        int a = molecule.indexOf(((IBond) carrier).getBegin());
                        int b = molecule.indexOf(((IBond) carrier).getEnd());
                        carriers[CARRIERS * x + (a == u || b == u ? 0 : 1)] = a == u || a == v ? b : a;
                    }
                } else {
                    continue;
                }
                config[x] = order == 1 || order == 2 ? order : 0;
                int info = element.getGroupInfo();
                group[x] = info == 0 ? 0 : firstGroup + groups.computeIfAbsent(info, g -> groups.size());
            }
            groupCount = groups.size();
            for (int x = 0; x < config.length; x++) {
                if (config[x] == 0)
                    continue;
                specified[count++] = x;
                // A centre reads itself and its carriers. A double bond reads its ends and all their neighbours,
                // as any neighbour stands in for the stored one when it alone is mapped.
                for (int i = 0; i < CARRIERS; i++)
                    reads[carriers[CARRIERS * x + i]] = true;
                if (x < atoms) {
                    reads[x] = true;
                } else {
                    for (int u = FIRST_END; u <= SECOND_END; u++) {
                        int end = carriers[CARRIERS * x + u];
                        for (int i = start[end]; i < start[end + 1]; i++)
                            reads[neighbour[i]] = true;
                    }
                }
            }
        }

        int carrier(int x, int place) {
            return carriers[CARRIERS * x + place];
        }
    }

    /** Asks whether to stop every {@link #POLL_INTERVAL} units of work ticked, or at once with {@link #poll()}. */
    static final class Clock {

        private static final int POLL_INTERVAL = 1024;
        private final BooleanSupplier stop;
        /** The units of work ticked since the last check. */
        private int work;

        Clock(BooleanSupplier stop) {
            this.stop = stop;
        }

        void tick() {
            tick(1);
        }

        void tick(int units) {
            work += units;
            if (work >= POLL_INTERVAL) {
                work = 0;
                poll();
            }
        }

        /** Checks whether to stop now, and throws {@link Stop} if so. */
        void poll() {
            if (stop.getAsBoolean())
                throw new Stop();
        }
    }

    /** Ends a search: the time limit was reached or the thread was interrupted. */
    static final class Stop extends RuntimeException {

        private static final long serialVersionUID = 1L;

        /** A signal, so no message and no stack trace. */
        Stop() {
            super(null, null, false, false);
        }
    }
}
