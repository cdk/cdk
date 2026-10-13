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
 * and bond pairs that may be mapped onto each other. Each molecule is a
 * {@link Side}: its neighbour lists in arrays, a symbol class for each atom, a
 * class for each bond and, with the options, its relevant rings
 * ({@link Rings}) and its configurations ({@link Stereo}). A query atom or
 * bond with an expression ({@link IQueryAtom}, {@link IQueryBond}) is matched
 * by that expression. Any other atom is matched by its symbol, and any other
 * bond by its class: aromatic, or by order. {@link #compatiblePairs} gives the
 * atom pairs as bit rows and works out the bond pairs that {@link #bondsMatch}
 * reads. The graph also gives the search its label bound, the check that a
 * mapping keeps the configurations, and colour refinement for the search
 * order. A {@link Clock} counts the work of every loop and throws
 * {@link Stop} when the search is to end. The molecules are only read.
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

    /** The graphs of the query and of the target. */
    final Side query, target;
    /** The number of symbol classes of both molecules, twice as many with complete rings. */
    final int atomClasses;
    /** Whether the classes decide every pair, as the query has no query atom or bond. */
    final boolean plainQuery;
    /** The number of longs in a row of target bonds. */
    private final int bondWords;
    /**
     * The compatible bond pairs: a row of {@link #bondWords} longs for each
     * query bond, with a bit for each target bond. Null until
     * {@link #compatiblePairs} has run.
     */
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

    /** The number of longs that hold {@code bits} bits. */
    static int words(int bits) {
        return (bits + 63) >>> 6;
    }

    /**
     * Packs a number of atoms and a number of bonds into a score,
     * {@code atoms << 32 | bonds}, which compares by atoms first or, with
     * {@code bondsFirst}, {@code bonds << 32 | atoms}, which compares by bonds
     * first. Both counts are non-negative, so scores compare as longs.
     */
    static long score(int atoms, int bonds, boolean bondsFirst) {
        return bondsFirst ? (long) bonds << 32 | atoms : (long) atoms << 32 | bonds;
    }

    /** The atoms of a score packed with the same {@code bondsFirst}. */
    static int atoms(long score, boolean bondsFirst) {
        return (int) (bondsFirst ? score : score >>> 32);
    }

    /** The bonds of a score packed with the same {@code bondsFirst}. */
    static int bonds(long score, boolean bondsFirst) {
        return (int) (bondsFirst ? score >>> 32 : score);
    }

    /**
     * Whether query bond {@code queryBond} may be mapped onto target bond
     * {@code targetBond}, once {@link #compatiblePairs} has run.
     */
    boolean bondsMatch(int queryBond, int targetBond) {
        return (bondPairs[queryBond * bondWords + (targetBond >>> 6)] & 1L << targetBond) != 0;
    }

    /**
     * The compatible atom pairs: a row for each query atom, with a bit for each
     * target atom. This also works out the bond pairs that {@link #bondsMatch} reads.
     */
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
        for (int targetIndex = 0; targetIndex < targetItems.length; targetIndex++) {
            if (targetClass[targetIndex] >= 0)
                byClass[targetClass[targetIndex] * words + (targetIndex >>> 6)] |= 1L << targetIndex;
        }
        for (int queryIndex = 0; queryIndex < queryItems.length; queryIndex++) {
            IChemObject item = queryItems[queryIndex];
            if (item instanceof IQueryAtom || item instanceof IQueryBond) {
                // A recursive expression can cost a search of its own for each target atom.
                clock.poll();
                for (int targetIndex = 0; targetIndex < targetItems.length; targetIndex++) {
                    clock.tick();
                    if (((queryClass[queryIndex] ^ targetClass[targetIndex]) & ringBit) == 0
                        && (item instanceof IQueryAtom ? ((IQueryAtom) item).matches((IAtom) targetItems[targetIndex])
                                                       : ((IQueryBond) item).matches((IBond) targetItems[targetIndex])))
                        rows[queryIndex * words + (targetIndex >>> 6)] |= 1L << targetIndex;
                }
            } else if (queryClass[queryIndex] >= 0) {
                clock.tick(words);
                System.arraycopy(byClass, queryClass[queryIndex] * words, rows, queryIndex * words, words);
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
        int[] queryAtomsByClass = new int[atomClasses], targetAtomsByClass = new int[atomClasses];
        for (int atomClass : query.atomClass) {
            if (atomClass >= 0)
                queryAtomsByClass[atomClass]++;
        }
        for (int atomClass : target.atomClass) {
            if (atomClass >= 0)
                targetAtomsByClass[atomClass]++;
        }
        long mostAtoms = 0, mostBonds = 0;
        for (int atomClass = 0; atomClass < atomClasses; atomClass++)
            mostAtoms += Math.min(queryAtomsByClass[atomClass], targetAtomsByClass[atomClass]);
        long[] queryLabels = query.bondLabels(atomClasses), targetLabels = target.bondLabels(atomClasses);
        for (int queryIndex = 0, targetIndex = 0;
             queryIndex < queryLabels.length && targetIndex < targetLabels.length; ) {
            if (queryLabels[queryIndex] < targetLabels[targetIndex]) {
                queryIndex++;
            } else if (queryLabels[queryIndex] > targetLabels[targetIndex]) {
                targetIndex++;
            } else {
                mostBonds++;
                queryIndex++;
                targetIndex++;
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
        for (int group = 0; group < groupParent.length; group++) {
            groupParent[group] = group;
            groupParity[group] = 0;
        }
        for (int position = 0; position < query.stereo.count; position++) {
            int queryIndex = query.stereo.specified[position], targetIndex = image(queryToTarget, queryIndex);
            if (targetIndex < 0 || target.stereo.config[targetIndex] == 0)
                continue;
            int parity = queryIndex < query.atomCount ? centre(queryToTarget, queryIndex, targetIndex)
                                                      : doubleBond(queryToTarget, queryIndex, targetIndex);
            if (parity >= 0 && !join(query.stereo.group[queryIndex], target.stereo.group[targetIndex], parity))
                return false;
        }
        return true;
    }

    /**
     * The target index at the image of query index {@code queryIndex}: the image of a
     * centre, or the bond between the images of the ends of a double bond. It
     * is -1 if there is none.
     */
    private int image(int[] queryToTarget, int queryIndex) {
        if (queryIndex < query.atomCount)
            return queryToTarget[queryIndex];
        int firstEnd = queryToTarget[query.stereo.carrier(queryIndex, Stereo.FIRST_END)];
        int secondEnd = queryToTarget[query.stereo.carrier(queryIndex, Stereo.SECOND_END)];
        int targetBond = firstEnd < 0 || secondEnd < 0 ? -1 : target.bondBetween(firstEnd, secondEnd);
        return targetBond < 0 ? -1 : target.atomCount + targetBond;
    }

    /**
     * Compares query centre {@code queryCentre} with target centre {@code targetCentre}, its image,
     * through the permutation that pairs their carriers. The result is 1 when
     * they disagree and 0 when they agree. It is -1 when fewer than three
     * carriers pair up.
     */
    private int centre(int[] queryToTarget, int queryCentre, int targetCentre) {
        int paired = 0, taken = 0;
        for (int queryPlace = 0; queryPlace < CARRIERS; queryPlace++) {
            int carrier = query.stereo.carrier(queryCentre, queryPlace);
            int carrierImage = carrier == queryCentre ? -1 : queryToTarget[carrier];
            carrierPlaces[queryPlace] = -1;
            for (int targetPlace = 0; carrierImage >= 0 && targetPlace < CARRIERS; targetPlace++) {
                if (target.stereo.carrier(targetCentre, targetPlace) == carrierImage)
                    carrierPlaces[queryPlace] = targetPlace;
            }
            if (carrierPlaces[queryPlace] >= 0) {
                paired++;
                taken |= 1 << carrierPlaces[queryPlace];
            }
        }
        if (paired < CARRIERS - 1)
            return -1;
        // The one carrier left on each side pairs with the other, at the place no carrier has taken.
        int left = 0;
        while ((taken & 1 << left) != 0)
            left++;
        int parity = query.stereo.config[queryCentre] == target.stereo.config[targetCentre] ? 0 : 1;
        for (int queryPlace = 0; queryPlace < CARRIERS; queryPlace++) {
            if (carrierPlaces[queryPlace] < 0)
                carrierPlaces[queryPlace] = left;
            for (int earlierPlace = 0; earlierPlace < queryPlace; earlierPlace++) {
                if (carrierPlaces[earlierPlace] > carrierPlaces[queryPlace])
                    parity ^= 1;
            }
        }
        return parity;
    }

    /**
     * Compares query double bond {@code queryIndex} with target double bond {@code targetIndex}
     * between the images of its ends, as {@link #centre} does for a centre. A
     * mapped neighbour of an end that is not the stored one reads the
     * configuration inverted.
     */
    private int doubleBond(int[] queryToTarget, int queryIndex, int targetIndex) {
        int queryFirstEnd = query.stereo.carrier(queryIndex, Stereo.FIRST_END);
        int querySecondEnd = query.stereo.carrier(queryIndex, Stereo.SECOND_END);
        int targetFirstEnd = queryToTarget[queryFirstEnd], targetSecondEnd = queryToTarget[querySecondEnd];
        // The place of the stored neighbour at the first target end.
        int first = target.stereo.carrier(targetIndex, Stereo.FIRST_END) == targetFirstEnd ? 0 : 1;
        int firstParity = end(queryToTarget, queryFirstEnd, querySecondEnd, targetFirstEnd,
                              query.stereo.carrier(queryIndex, 0), target.stereo.carrier(targetIndex, first));
        int secondParity = end(queryToTarget, querySecondEnd, queryFirstEnd, targetSecondEnd,
                               query.stereo.carrier(queryIndex, 1), target.stereo.carrier(targetIndex, 1 - first));
        if (firstParity < 0 || secondParity < 0)
            return -1;
        return firstParity ^ secondParity
               ^ (query.stereo.config[queryIndex] == target.stereo.config[targetIndex] ? 0 : 1);
    }

    /**
     * Compares the stored neighbours at query end {@code queryEnd} and its image,
     * target end {@code targetEnd}. They are compared through a neighbour of
     * {@code queryEnd}, other than {@code otherEnd}, whose image is next to {@code targetEnd}. The
     * result is 1 when it is the stored one on one side only, 0 when on both
     * or neither, and -1 when there is no such neighbour.
     */
    private int end(int[] queryToTarget, int queryEnd, int otherEnd, int targetEnd, int queryStored, int targetStored) {
        for (int place = query.start[queryEnd]; place < query.start[queryEnd + 1]; place++) {
            int queryNeighbour = query.neighbour[place];
            int neighbourImage = queryNeighbour == otherEnd ? -1 : queryToTarget[queryNeighbour];
            if (neighbourImage >= 0 && target.bondBetween(targetEnd, neighbourImage) >= 0)
                return (queryNeighbour == queryStored) == (neighbourImage == targetStored) ? 0 : 1;
        }
        return -1;
    }

    /**
     * Whether groups {@code queryGroup} and {@code targetGroup} can each be kept or inverted so
     * that one more pair of configurations agrees, given the pairs compared
     * before. The {@code parity} is 1 when the two disagree as they are.
     */
    private boolean join(int queryGroup, int targetGroup, int parity) {
        while (groupParent[queryGroup] != queryGroup) {
            parity ^= groupParity[queryGroup];
            queryGroup = groupParent[queryGroup];
        }
        while (groupParent[targetGroup] != targetGroup) {
            parity ^= groupParity[targetGroup];
            targetGroup = groupParent[targetGroup];
        }
        if (queryGroup == targetGroup)
            return parity == 0;
        groupParent[queryGroup] = targetGroup;
        groupParity[queryGroup] = parity;
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

        /** The neighbour lists, as in {@link Side#start} and {@link Side#neighbour}. */
        private final int[] start, neighbour;
        /** Counts the work of each round. */
        private final Clock clock;
        /** The number of atoms. */
        private final int atomCount;
        /** The atoms in rank order, and the place of each atom in it. */
        private final int[] order, place;
        /** The cell of each atom, and the place where each cell starts and its size. */
        private final int[] cell, cellStart, cellSize;
        /** The number of cells. */
        private int cells;
        /** The sorted ranks of the neighbours of each atom, at its places in the neighbour list. */
        private final int[] ranks;
        /** The atoms to sort in a round, the first {@link #toSortCount}, and the last round each was listed for. */
        private final int[] toSort, listed;
        /** The number of atoms to sort. */
        private int toSortCount;
        /** The atoms that changed cell in a round, the first {@link #movedCount}. */
        private final int[] moved;
        /** The number of atoms that changed cell in the round. */
        private int movedCount;
        /** The round being run, from 1, which stamps {@link #listed}. */
        private int round = 1;
        /** Room to sort the atoms of one cell. */
        private final Integer[] sorting;
        /** Room for the atoms to sort as keys by cell, see {@link #sortByCell}. */
        private final long[] byCell;
        /** Orders atoms by the sorted ranks of their neighbours. */
        private final Comparator<Integer> byNeighbours;

        /** Starts with a cell for each colour, and every atom listed to sort. */
        Refinement(int[] start, int[] neighbour, int[] colour, Clock clock) {
            this.start = start;
            this.neighbour = neighbour;
            this.clock = clock;
            atomCount = colour.length;
            order = new int[atomCount];
            place = new int[atomCount];
            cell = new int[atomCount];
            cellStart = new int[atomCount];
            cellSize = new int[atomCount];
            ranks = new int[neighbour.length];
            toSort = new int[atomCount];
            listed = new int[atomCount];
            moved = new int[atomCount];
            sorting = new Integer[atomCount];
            byCell = new long[atomCount];
            byNeighbours = (left, right) -> {
                int comparison = Integer.compare(start[left + 1] - start[left], start[right + 1] - start[right]);
                for (int leftPlace = start[left], rightPlace = start[right];
                     comparison == 0 && leftPlace < start[left + 1]; leftPlace++, rightPlace++)
                    comparison = Integer.compare(ranks[leftPlace], ranks[rightPlace]);
                return comparison;
            };
            for (int atomColour : colour)
                cellSize[atomColour]++;
            while (cells < atomCount && cellSize[cells] > 0) {
                cellStart[cells] = cells == 0 ? 0 : cellStart[cells - 1] + cellSize[cells - 1];
                cells++;
            }
            int[] filled = new int[atomCount];
            for (int atom = 0; atom < atomCount; atom++) {
                cell[atom] = colour[atom];
                place[atom] = cellStart[cell[atom]] + filled[cell[atom]]++;
                order[place[atom]] = atom;
                toSort[atom] = atom;
            }
            toSortCount = atomCount;
            Arrays.fill(listed, round);
        }

        /** Refines the cells, and gives the rank of each atom. */
        int[] ranks(boolean individualise) {
            for (int firstShared = 0; ; round++) {
                movedCount = 0;
                int shared = sortByCell();
                // Every rank is read before any cell splits.
                for (int from = 0, to; from < shared; from = to) {
                    to = cellEnd(from, shared);
                    readRanks(from, to);
                }
                for (int from = 0, to; from < shared; from = to) {
                    to = cellEnd(from, shared);
                    splitCell(from, to);
                }
                if (movedCount == 0) {
                    while (firstShared < atomCount && cellSize[cell[order[firstShared]]] == 1)
                        firstShared++;
                    if (!individualise || firstShared == atomCount)
                        break;
                    individualise(firstShared);
                }
                listNeighbours();
            }
            int[] rank = new int[atomCount];
            for (int atom = 0; atom < atomCount; atom++)
                rank[atom] = cellStart[cell[atom]];
            return rank;
        }

        /**
         * Keeps the atoms to sort whose cell has other atoms, in order of
         * cell and then of atom, and gives their number.
         */
        private int sortByCell() {
            int shared = 0;
            for (int i = 0; i < toSortCount; i++) {
                int atom = toSort[i];
                if (cellSize[cell[atom]] > 1)
                    byCell[shared++] = (long) cell[atom] << 32 | atom;
            }
            Arrays.sort(byCell, 0, shared);
            for (int i = 0; i < shared; i++)
                toSort[i] = (int) byCell[i];
            return shared;
        }

        /** The end of the atoms to sort from place {@code from} that are in its cell. */
        private int cellEnd(int from, int shared) {
            int to = from + 1;
            while (to < shared && cell[toSort[to]] == cell[toSort[from]])
                to++;
            return to;
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

        /** Puts the sorted ranks of the neighbours of {@code atom} at its places in {@link #ranks}. */
        private void sortNeighbourRanks(int atom) {
            clock.tick(1 + start[atom + 1] - start[atom]);
            for (int neighbourPlace = start[atom]; neighbourPlace < start[atom + 1]; neighbourPlace++)
                ranks[neighbourPlace] = cellStart[cell[neighbour[neighbourPlace]]];
            Arrays.sort(ranks, start[atom], start[atom + 1]);
        }

        /** An atom of cell {@code cellName} that is not listed for this round, or -1 if there is none. */
        private int unlisted(int cellName) {
            for (int position = cellStart[cellName]; position < cellStart[cellName] + cellSize[cellName]; position++) {
                if (listed[order[position]] != round)
                    return order[position];
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
            int cellName = cell[toSort[from]], standIn = unlisted(cellName);
            int unlistedCount = cellSize[cellName] - (to - from);
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
            for (int runFrom = 0, runTo; runFrom < count; runFrom = runTo) {
                runTo = runEnd(runFrom, count);
                int size = runSize(runFrom, runTo, standIn, unlistedCount);
                if (size > largestSize) {
                    largestRun = runFrom;
                    largestSize = size;
                }
                for (int sortedPlace = runFrom; sortedPlace < runTo; sortedPlace++) {
                    if (sorting[sortedPlace] == standIn)
                        standInRun = runFrom;
                }
            }
            // The listed atoms move to their places in run order, around the run of the stand-in. That run then
            // holds the unlisted atoms.
            int renamed = -1;
            for (int runFrom = 0, runTo, position = cellStart[cellName]; runFrom < count; runFrom = runTo) {
                runTo = runEnd(runFrom, count);
                int size = runSize(runFrom, runTo, standIn, unlistedCount);
                int runCell = runFrom == largestRun ? cellName : cells++;
                cellStart[runCell] = position;
                cellSize[runCell] = size;
                if (runFrom == standInRun) {
                    if (runCell != cellName)
                        renamed = runCell;
                } else {
                    for (int sortedPlace = runFrom; sortedPlace < runTo; sortedPlace++) {
                        moveTo(sorting[sortedPlace], position + sortedPlace - runFrom);
                        if (runCell != cellName)
                            changeCell(sorting[sortedPlace], runCell);
                    }
                }
                position += size;
            }
            if (renamed >= 0) {
                for (int position = cellStart[renamed]; position < cellStart[renamed] + cellSize[renamed]; position++)
                    changeCell(order[position], renamed);
            }
        }

        /** The end of the run of sorted atoms from {@code runFrom} with equal neighbour ranks. */
        private int runEnd(int runFrom, int count) {
            int runTo = runFrom + 1;
            while (runTo < count && byNeighbours.compare(sorting[runFrom], sorting[runTo]) == 0)
                runTo++;
            return runTo;
        }

        /**
         * The atoms in the run of sorted atoms from {@code runFrom} to {@code runTo - 1}.
         * The stand-in counts for every unlisted atom.
         */
        private int runSize(int runFrom, int runTo, int standIn, int unlistedCount) {
            int size = 0;
            for (int sortedPlace = runFrom; sortedPlace < runTo; sortedPlace++)
                size += sorting[sortedPlace] == standIn ? unlistedCount : 1;
            return size;
        }

        /**
         * Puts the atom of lowest index in the shared cell that starts at place
         * {@code first} in a cell of its own, before its peers.
         */
        private void individualise(int first) {
            int cellName = cell[order[first]], pick = first;
            clock.tick(cellSize[cellName]);
            for (int position = first + 1; position < first + cellSize[cellName]; position++) {
                if (order[position] < order[pick])
                    pick = position;
            }
            int atom = order[pick];
            moveTo(atom, first);
            cellStart[cells] = first;
            cellSize[cells] = 1;
            cellStart[cellName]++;
            cellSize[cellName]--;
            changeCell(atom, cells++);
        }

        /** Puts {@code atom} at place {@code newPlace}, and the atom that was there at its old place. */
        private void moveTo(int atom, int newPlace) {
            int displaced = order[newPlace];
            order[place[atom]] = displaced;
            place[displaced] = place[atom];
            order[newPlace] = atom;
            place[atom] = newPlace;
        }

        /** Puts {@code atom} in cell {@code cellName}, and notes that it moved. */
        private void changeCell(int atom, int cellName) {
            cell[atom] = cellName;
            moved[movedCount++] = atom;
        }

        /** Lists the neighbours of the atoms that changed cell, to sort in the next round. */
        private void listNeighbours() {
            toSortCount = 0;
            for (int movedIndex = 0; movedIndex < movedCount; movedIndex++) {
                int atom = moved[movedIndex];
                clock.tick(1 + start[atom + 1] - start[atom]);
                for (int neighbourPlace = start[atom]; neighbourPlace < start[atom + 1]; neighbourPlace++) {
                    if (listed[neighbour[neighbourPlace]] != round + 1) {
                        listed[neighbour[neighbourPlace]] = round + 1;
                        toSort[toSortCount++] = neighbour[neighbourPlace];
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

    /** Mixes the bits of {@code value}, with the finaliser of SplitMix64. */
    private static long mix(long value) {
        value = (value ^ value >>> 30) * 0xbf58476d1ce4e5b9L;
        value = (value ^ value >>> 27) * 0x94d049bb133111ebL;
        return value ^ value >>> 31;
    }

    /** One molecule of the search as an int graph, with the atoms and bonds numbered as in the molecule. */
    static final class Side {

        /** The numbers of atoms and bonds. */
        final int atomCount, bondCount;
        /** The neighbours of an atom, and the bonds to them, are at places start[atom] to start[atom + 1] - 1. */
        final int[] start, neighbour, neighbourBond;
        /** The hash of the symbol of each atom of the original molecule, 0 for none, for the search order and seed. */
        final int[] symbolHash;
        /** The symbol class of each atom, negative for an atom without a symbol, which matches nothing. */
        final int[] atomClass;
        /**
         * The class of each bond: 0 for aromatic, 1 for no order, and 2 plus
         * the ordinal of the order otherwise. With complete rings it is
         * doubled, plus 1 for a ring bond, as the atom classes are.
         */
        final int[] bondClass;
        /** The ring atoms, bonds and rings, for complete rings, or null. */
        final Rings rings;
        /** The configurations, or null unless stereochemistry is checked. */
        final Stereo stereo;
        /** The atoms by index, for the expressions of a query. */
        final IAtom[] atoms;
        /** The bonds by index, for the expressions of a query. */
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
            for (int atom = 0; atom < atomCount; atom++) {
                String label = original.getAtom(atom).getSymbol(), symbol = atoms[atom].getSymbol();
                symbolHash[atom] = label == null ? 0 : label.hashCode();
                atomClass[atom] = symbol == null ? -1 : symbols.computeIfAbsent(symbol, key -> symbols.size());
                if (withRings)
                    atomClass[atom] = 2 * atomClass[atom] + (rings.ringAtom[atom] ? 1 : 0);
            }
            bondClass = new int[bondCount];
            for (int bond = 0; bond < bondCount; bond++) {
                IBond.Order order = bonds[bond].getOrder();
                bondClass[bond] = bonds[bond].isAromatic() ? 0 : order == null ? 1 : 2 + order.ordinal();
                if (withRings)
                    bondClass[bond] = 2 * bondClass[bond] + (rings.ringBond[bond] ? 1 : 0);
            }
            stereo = withStereo ? new Stereo(original, start, neighbour, firstGroup) : null;
        }

        /** The number of bonds at {@code atom}. */
        int degree(int atom) {
            return start[atom + 1] - start[atom];
        }

        /** The most bonds at any atom. */
        int maxDegree() {
            int max = 0;
            for (int atom = 0; atom < atomCount; atom++)
                max = Math.max(max, degree(atom));
            return max;
        }

        /**
         * Whether each bond is on a ring. With complete rings this is the
         * array held by {@link #rings}, so callers must not change it.
         */
        boolean[] ringBonds() {
            return rings != null ? rings.ringBond : Rings.ringBonds(start, neighbour, neighbourBond, bondCount);
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
            for (int atom = 0; atom < atomCount; atom++) {
                clock.tick();
                int degree = degree(atom);
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
            for (int atom = 0; atom < atomCount; atom++) {
                clock.tick(1 + degree(atom));
                for (int i = start[atom]; i < start[atom + 1]; i++) {
                    if (degree(atom) + degree(neighbour[i]) == sum)
                        return bondCount - sum + 1;
                }
            }
            return bondCount - sum;
        }

        /** The bond between atoms {@code atom} and {@code other}, or -1 if there is none. */
        int bondBetween(int atom, int other) {
            for (int i = start[atom]; i < start[atom + 1]; i++) {
                if (neighbour[i] == other)
                    return neighbourBond[i];
            }
            return -1;
        }

        /**
         * Whether a configuration reads {@code atom}, as a centre or carrier,
         * or as an end of a double bond or next to one. Mapping or swapping
         * such an atom can change what {@link MCSGraph#stereoConsistent} finds.
         * False unless stereochemistry is checked.
         */
        boolean configured(int atom) {
            return stereo != null && stereo.reads[atom];
        }

        /** The dense ranks of the atoms by their number of choices, then their degree, then their symbol. */
        int[] invariants(int[] choices) {
            long[] keys = new long[atomCount];
            for (int atom = 0; atom < atomCount; atom++)
                keys[atom] = symbolHash[atom];
            int[] symbol = denseRanks(keys);
            long degrees = maxDegree() + 1;
            for (int atom = 0; atom < atomCount; atom++)
                keys[atom] = (choices[atom] * degrees + degree(atom)) * atomCount + symbol[atom];
            return denseRanks(keys);
        }

        /** Atom signatures by radius: symbol and degree at 0, then each mixed with the sum over its neighbours. */
        long[][] signatures(Clock clock) {
            long[][] signature = new long[RADII][atomCount];
            for (int atom = 0; atom < atomCount; atom++)
                signature[0][atom] = mix(symbolHash[atom] * 31L + degree(atom));
            for (int radius = 1; radius < RADII; radius++) {
                for (int atom = 0; atom < atomCount; atom++) {
                    clock.tick(1 + degree(atom));
                    long sum = 0;
                    for (int i = start[atom]; i < start[atom + 1]; i++)
                        sum += mix(signature[radius - 1][neighbour[i]]);
                    signature[radius][atom] = mix(signature[radius - 1][atom] * GOLDEN_GAMMA + sum);
                }
            }
            return signature;
        }

        /** The sorted labels of the bonds whose ends have symbols, see {@link MCSGraph#labelBound}. */
        long[] bondLabels(int atomClasses) {
            long[] labels = new long[bondCount];
            int size = 0;
            for (int atom = 0; atom < atomCount; atom++) {
                for (int i = start[atom]; i < start[atom + 1]; i++) {
                    int other = neighbour[i];
                    if (other > atom && atomClass[atom] >= 0 && atomClass[other] >= 0)
                        labels[size++] = bondLabel(atom, other, neighbourBond[i], atomClasses);
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
                for (int head = 0; head < size; head++) {
                    int atom = queue[head];
                    for (int place = start[atom]; place < start[atom + 1]; place++) {
                        int other = neighbour[place];
                        if (atomClass[other] < 0
                            || Arrays.binarySearch(otherLabels,
                                                   bondLabel(atom, other, neighbourBond[place], atomClasses)) < 0)
                            continue;
                        links++;
                        if (!seen[other]) {
                            seen[other] = true;
                            queue[size++] = other;
                        }
                    }
                }
                mostAtoms = Math.max(mostAtoms, size);
                // Each bond is met from both ends.
                mostBonds = Math.max(mostBonds, links / 2);
            }
            return score(mostAtoms, mostBonds, false);
        }

        /** The label of the bond from {@code atom} to {@code other}: the symbols of its ends and its class. */
        long bondLabel(int atom, int other, int bond, int atomClasses) {
            int firstClass = atomClass[atom], secondClass = atomClass[other];
            return ((long) Math.min(firstClass, secondClass) * atomClasses + Math.max(firstClass, secondClass))
                   << BOND_CLASS_BITS | bondClass[bond];
        }
    }

    /** The atoms of each bond, its begin and then its end. */
    private static int[] ends(IAtom[] atoms, IBond[] bonds) {
        int[] ends = new int[2 * bonds.length];
        Map<IAtom, Integer> index = null;
        for (int endIndex = 0; endIndex < ends.length; endIndex++) {
            IAtom atom = (endIndex & 1) == 0 ? bonds[endIndex >> 1].getBegin() : bonds[endIndex >> 1].getEnd();
            int atomIndex = atom != null ? atom.getIndex() : -1;
            if (atom != null && (atomIndex < 0 || atomIndex >= atoms.length || atoms[atomIndex] != atom)) {
                // The atom does not know its index. Look up its underlying atom by identity, in a map built once.
                if (index == null) {
                    index = new IdentityHashMap<>();
                    for (int position = 0; position < atoms.length; position++)
                        index.put(AtomRef.deref(atoms[position]), position);
                }
                Integer known = index.get(AtomRef.deref(atom));
                atomIndex = known != null ? known : -1;
            }
            if (atomIndex < 0)
                throw new IllegalArgumentException("A bond joins an atom that is not in the molecule");
            ends[endIndex] = atomIndex;
        }
        return ends;
    }

    /**
     * The start of each atom in the neighbour lists, the neighbours and the
     * bonds to them. A bond from an atom to itself, or a second bond between
     * the same atoms, is refused, as it is not a molecule.
     */
    private static int[][] adjacency(int[] ends, int atomCount) {
        int[] start = new int[atomCount + 1];
        for (int end : ends)
            start[end + 1]++;
        for (int atom = 0; atom < atomCount; atom++)
            start[atom + 1] += start[atom];
        int[] next = Arrays.copyOf(start, atomCount), neighbour = new int[ends.length], bond = new int[ends.length];
        for (int endIndex = 0; endIndex < ends.length; endIndex++) {
            int atom = ends[endIndex];
            neighbour[next[atom]] = ends[endIndex ^ 1];
            bond[next[atom]++] = endIndex >> 1;
        }
        int[] seen = new int[atomCount];
        for (int atom = 0; atom < atomCount; atom++) {
            for (int place = start[atom]; place < start[atom + 1]; place++) {
                if (neighbour[place] == atom)
                    throw new IllegalArgumentException("A bond joins an atom to itself");
                if (seen[neighbour[place]] == atom + 1)
                    throw new IllegalArgumentException("Two bonds join the same atoms");
                seen[neighbour[place]] = atom + 1;
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

        /** Whether each atom, and each bond, is on a ring. */
        final boolean[] ringAtom, ringBond;
        /** The relevant rings, each as its atoms in ring order followed by the bond from each atom to the next. */
        final int[][] rings;
        /**
         * The atoms of a ring system with too many relevant rings, or 0. The
         * rings of that system and of later ones are left out.
         */
        final int refusedAtoms;

        /**
         * Finds the ring bonds and atoms of the graph given as neighbour
         * lists, then the relevant rings of each ring system, the atoms joined
         * by ring bonds, until a system has too many.
         */
        Rings(int[] start, int[] neighbour, int[] neighbourBond, int bondCount) {
            int atomCount = start.length - 1, refused = 0;
            ringBond = ringBonds(start, neighbour, neighbourBond, bondCount);
            ringAtom = new boolean[atomCount];
            for (int atom = 0; atom < atomCount; atom++) {
                for (int i = start[atom]; i < start[atom + 1]; i++) {
                    if (ringBond[neighbourBond[i]])
                        ringAtom[atom] = true;
                }
            }
            List<int[]> found = new ArrayList<>();
            int[] memberPlace = new int[atomCount];
            Arrays.fill(memberPlace, -1);
            for (int origin = 0; origin < atomCount; origin++) {
                if (!ringAtom[origin] || memberPlace[origin] >= 0)
                    continue;
                int[] members = members(origin, start, neighbour, neighbourBond, memberPlace);
                int[][] system = new int[members.length][];
                for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
                    system[memberIndex] = neighbours(members[memberIndex], start, neighbour, neighbourBond,
                                                     memberPlace);
                }
                int[][] paths = relevantPaths(system);
                if (paths == null) {
                    refused = members.length;
                    break;
                }
                for (int[] path : paths)
                    found.add(ring(path, members, start, neighbour, neighbourBond));
            }
            rings = found.toArray(new int[0][]);
            this.refusedAtoms = refused;
        }

        /**
         * The relevant rings of a ring system as closed paths of its places,
         * each with its first place again at the end, or null if there are
         * more than {@link #MAX_RINGS}. A system with as many bonds as atoms
         * is a single ring. Otherwise there are at least as many relevant rings
         * as the cyclomatic number, bonds less atoms plus one, and they are
         * found only when the bound on their number is below the cap.
         */
        private static int[][] relevantPaths(int[][] system) {
            int bonds = 0;
            for (int[] row : system)
                bonds += row.length;
            bonds /= 2;
            if (bonds == system.length)
                return new int[][]{GraphUtil.cycle(system, IntStream.range(0, system.length).toArray())};
            if (bonds - system.length + 1 > MAX_RINGS)
                return null;
            RelevantCycles relevant = ringBound(system) < RING_BOUND_CAP ? new RelevantCycles(system) : null;
            return relevant == null || relevant.size() > MAX_RINGS ? null : relevant.paths();
        }

        /**
         * The ring bonds, found by low-link with an explicit stack of atoms, so
         * that a long chain cannot overflow the call stack. A tree bond is a
         * ring bond when the atoms below it reach above it, and any other bond
         * is a ring bond. The bond to the parent is skipped by its index. A
         * bond on no ring splits its part of the molecule in two.
         */
        static boolean[] ringBonds(int[] start, int[] neighbour, int[] neighbourBond, int bondCount) {
            int atomCount = start.length - 1, time = 0;
            boolean[] ringBond = new boolean[bondCount];
            int[] visited = new int[atomCount], low = new int[atomCount], via = new int[atomCount];
            int[] next = start.clone(), stack = new int[atomCount];
            for (int root = 0, top = 0; root < atomCount; root++) {
                if (visited[root] > 0)
                    continue;
                visited[root] = low[root] = ++time;
                via[root] = -1;
                stack[top++] = root;
                while (top > 0) {
                    int atom = stack[top - 1], place = next[atom]++;
                    if (place == start[atom + 1]) {
                        if (--top > 0) {
                            int parent = stack[top - 1];
                            low[parent] = Math.min(low[parent], low[atom]);
                            if (low[atom] <= visited[parent])
                                ringBond[via[atom]] = true;
                        }
                    } else if (visited[neighbour[place]] == 0) {
                        visited[neighbour[place]] = low[neighbour[place]] = ++time;
                        via[neighbour[place]] = neighbourBond[place];
                        stack[top++] = neighbour[place];
                    } else if (neighbourBond[place] != via[atom]) {
                        low[atom] = Math.min(low[atom], visited[neighbour[place]]);
                        ringBond[neighbourBond[place]] = true;
                    }
                }
            }
            return ringBond;
        }

        /**
         * The atoms of the ring system of {@code origin}, in the order reached. The
         * place of each in that order is set in {@code memberPlace}.
         */
        private int[] members(int origin, int[] start, int[] neighbour, int[] neighbourBond, int[] memberPlace) {
            int[] members = new int[ringAtom.length];
            int size = 1;
            members[0] = origin;
            memberPlace[origin] = 0;
            for (int head = 0; head < size; head++) {
                for (int place = start[members[head]]; place < start[members[head] + 1]; place++) {
                    if (ringBond[neighbourBond[place]] && memberPlace[neighbour[place]] < 0) {
                        memberPlace[neighbour[place]] = size;
                        members[size++] = neighbour[place];
                    }
                }
            }
            return Arrays.copyOf(members, size);
        }

        /** The places of the neighbours of {@code atom} through ring bonds. */
        private int[] neighbours(int atom, int[] start, int[] neighbour, int[] neighbourBond, int[] memberPlace) {
            int[] row = new int[start[atom + 1] - start[atom]];
            int count = 0;
            for (int i = start[atom]; i < start[atom + 1]; i++) {
                if (ringBond[neighbourBond[i]])
                    row[count++] = memberPlace[neighbour[i]];
            }
            return Arrays.copyOf(row, count);
        }

        /** A closed path of places as the atoms of a ring, followed by the bond from each atom to the next. */
        private static int[] ring(int[] path, int[] members, int[] start, int[] neighbour, int[] neighbourBond) {
            int len = path.length - 1;
            int[] ring = new int[2 * len];
            for (int step = 0; step < len; step++) {
                int atom = members[path[step]], nextAtom = members[path[step + 1]], place = start[atom];
                while (neighbour[place] != nextAtom)
                    place++;
                ring[step] = atom;
                ring[len + step] = neighbourBond[place];
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
                for (int step = 0; step < len; step++)
                    atomCovered[ring[step]] = bondCovered[ring[len + step]] = true;
            }
            for (int atom = 0; atom < ringAtom.length; atom++) {
                if (ringAtom[atom] && mapped[atom] && !atomCovered[atom])
                    return false;
            }
            for (int bond = 0; bond < ringBond.length; bond++) {
                if (ringBond[bond] && common[bond] && !bondCovered[bond])
                    return false;
            }
            return true;
        }

        /** Whether every atom of {@code ring} is mapped or open, and every bond between two mapped atoms common. */
        private static boolean coversRing(int[] ring, boolean[] mapped, boolean[] open, boolean[] common) {
            int len = ring.length / 2;
            for (int step = 0; step < len; step++) {
                int atom = ring[step], nextAtom = ring[(step + 1) % len];
                if (!mapped[atom] && !open[atom] || mapped[atom] && mapped[nextAtom] && !common[ring[len + step]])
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
        for (int root = 0; root < graph.length && bound < RING_BOUND_CAP; root++) {
            Arrays.fill(dist, -1);
            dist[root] = 0;
            paths[root] = 1;
            queue[0] = root;
            // The shortest paths to an atom are all counted once it leaves the queue.
            for (int head = 0, tail = 1; head < tail; head++) {
                int atom = queue[head];
                long before = 0;
                for (int other : graph[atom]) {
                    if (dist[other] < 0) {
                        dist[other] = dist[atom] + 1;
                        paths[other] = 0;
                        queue[tail++] = other;
                    }
                    if (dist[other] > dist[atom])
                        paths[other] = Math.min(paths[other] + paths[atom], RING_BOUND_CAP);
                    else if (dist[other] == dist[atom] && other < atom)
                        bound = Math.min(bound + paths[atom] * paths[other], RING_BOUND_CAP);
                    else if (dist[other] < dist[atom]) {
                        bound = Math.min(bound + before * paths[other], RING_BOUND_CAP);
                        before = Math.min(before + paths[other], RING_BOUND_CAP);
                    }
                }
            }
        }
        return bound;
    }

    /**
     * The configurations of a molecule. An index below the atom count is a
     * tetrahedral centre, and otherwise it is the atom count plus the index of
     * a double bond. A configuration is 1 or 2, or 0 for none. The carriers of a
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
        /** The number of indices with a configuration. */
        int count;

        /**
         * Reads the tetrahedral centres and double bonds of {@code molecule},
         * whose neighbour lists are given, and numbers its racemic and relative
         * groups from {@code firstGroup}. Other stereo elements are left out.
         */
        Stereo(IAtomContainer molecule, int[] start, int[] neighbour, int firstGroup) {
            int atomCount = molecule.getAtomCount();
            config = new int[atomCount + molecule.getBondCount()];
            group = new int[config.length];
            carriers = new int[CARRIERS * config.length];
            specified = new int[config.length];
            reads = new boolean[atomCount];
            Map<Integer, Integer> groups = new HashMap<>();
            for (IStereoElement<?, ?> element : molecule.stereoElements()) {
                int index = element.getConfigClass() == IStereoElement.TH ? readCentre(molecule, element)
                            : element.getConfigClass() == IStereoElement.CT ? readDoubleBond(molecule, element, start)
                            : -1;
                if (index < 0)
                    continue;
                int order = element.getConfigOrder();
                config[index] = order == 1 || order == 2 ? order : 0;
                int info = element.getGroupInfo();
                group[index] = info == 0 ? 0 : firstGroup + groups.computeIfAbsent(info, key -> groups.size());
            }
            groupCount = groups.size();
            for (int index = 0; index < config.length; index++) {
                if (config[index] != 0) {
                    specified[count++] = index;
                    markReads(index, atomCount, start, neighbour);
                }
            }
        }

        /** Reads a tetrahedral centre, whose carriers are its four neighbours, and gives its atom index. */
        private int readCentre(IAtomContainer molecule, IStereoElement<?, ?> element) {
            int index = molecule.indexOf((IAtom) element.getFocus());
            for (int i = 0; i < CARRIERS; i++)
                carriers[CARRIERS * index + i] = molecule.indexOf((IAtom) element.getCarriers().get(i));
            return index;
        }

        /**
         * Reads a double bond and gives its index, the atom count plus the
         * bond index, or -1 if an end has three or more other neighbours. The
         * ends go to the places {@link #FIRST_END} and {@link #SECOND_END}, and
         * the stored neighbour of each end, the other atom of the carrier bond
         * there, to places 0 and 1.
         */
        private int readDoubleBond(IAtomContainer molecule, IStereoElement<?, ?> element, int[] start) {
            IBond bond = (IBond) element.getFocus();
            int beginAtom = molecule.indexOf(bond.getBegin()), endAtom = molecule.indexOf(bond.getEnd());
            if (start[beginAtom + 1] - start[beginAtom] > 3 || start[endAtom + 1] - start[endAtom] > 3)
                return -1;
            int index = molecule.getAtomCount() + molecule.indexOf(bond);
            carriers[CARRIERS * index + FIRST_END] = beginAtom;
            carriers[CARRIERS * index + SECOND_END] = endAtom;
            for (Object carrier : element.getCarriers()) {
                int carrierBegin = molecule.indexOf(((IBond) carrier).getBegin());
                int carrierEnd = molecule.indexOf(((IBond) carrier).getEnd());
                carriers[CARRIERS * index + (carrierBegin == beginAtom || carrierEnd == beginAtom ? 0 : 1)]
                        = carrierBegin == beginAtom || carrierBegin == endAtom ? carrierEnd : carrierBegin;
            }
            return index;
        }

        /**
         * Marks the atoms that the configuration at {@code index} reads. A
         * centre reads itself and its carriers. A double bond reads its ends
         * and all their neighbours, as any neighbour stands in for the stored
         * one when it alone is mapped.
         */
        private void markReads(int index, int atomCount, int[] start, int[] neighbour) {
            for (int i = 0; i < CARRIERS; i++)
                reads[carriers[CARRIERS * index + i]] = true;
            if (index < atomCount) {
                reads[index] = true;
                return;
            }
            for (int endPlace = FIRST_END; endPlace <= SECOND_END; endPlace++) {
                int end = carriers[CARRIERS * index + endPlace];
                for (int i = start[end]; i < start[end + 1]; i++)
                    reads[neighbour[i]] = true;
            }
        }

        /** The carrier at {@code place} of the configuration at {@code index}, see {@link #carriers}. */
        int carrier(int index, int place) {
            return carriers[CARRIERS * index + place];
        }
    }

    /**
     * Counts the work of the search in units, about one for each step of an
     * inner loop, and asks whether to stop every {@link #POLL_INTERVAL} units,
     * or at once with {@link #poll()}. The search ends with {@link Stop} when
     * the answer is yes, so every loop of the search ticks the clock.
     */
    static final class Clock {

        /** The units of work between two checks. */
        private static final int POLL_INTERVAL = 1024;
        /** Whether to stop now: the time limit was reached or the thread was interrupted. */
        private final BooleanSupplier stop;
        /** The units of work ticked since the last check. */
        private int work;

        /** A clock that stops the search once {@code stop} says so. */
        Clock(BooleanSupplier stop) {
            this.stop = stop;
        }

        /** Counts one unit of work. */
        void tick() {
            tick(1);
        }

        /** Counts {@code units} of work, and checks whether to stop once the interval is reached. */
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

        /** The serial version, as for any exception. */
        private static final long serialVersionUID = 1L;

        /** A signal, so no message and no stack trace. */
        Stop() {
            super(null, null, false, false);
        }
    }
}
