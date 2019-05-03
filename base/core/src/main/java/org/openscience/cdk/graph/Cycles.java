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

package org.openscience.cdk.graph;

import org.openscience.cdk.exception.Intractable;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.RingSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;

/**
 * A utility class for storing and computing the cycles of a chemical graph.
 * Utilities are also provided for converting the cycles to {@link IRing}s. A
 * brief description of each cycle set is given below - for a more comprehensive
 * review please see - {@cdk.cite Berger04}.
 *
 * <ul> <li>{@link #all()} - all simple cycles in the graph, the number of
 * cycles generated may be very large and may not be feasible for some
 * molecules, such as, fullerene.</li> <li>{@link #mcb()} (aka. SSSR) - minimum
 * cycle basis (MCB) of a graph, these cycles are linearly independent and can
 * be used to generate all of cycles in the graph. It is important to note the
 * MCB is not unique and a that there may be multiple equally valid MCBs. The
 * smallest set of smallest rings (SSSR) is often used to refer to the MCB but
 * originally SSSR was defined as a strictly fundamental cycle basis {@cdk.cite
 * Berger04}. Not every graph has a strictly fundamental cycle basis the
 * definition has come to mean the MCB. Due to the non-uniqueness of the
 * MCB/SSSR its use is discouraged.</li> <li>{@link #relevant()} - relevant
 * cycles of a graph, the smallest set of uniquely defined short cycles. If a
 * graph has a single MCB then the relevant cycles and MCB are the same. If the
 * graph has multiple MCB then the relevant cycles is the union of all MCBs. The
 * number of relevant cycles may be exponential but it is possible to determine
 * how many relevant cycles there are in polynomial time without generating
 * them. For chemical graphs the number of relevant cycles is usually within
 * manageable bounds. </li> <li>{@link #essential()} - essential cycles of a
 * graph. Similar to the relevant cycles the set is unique for a graph. If a
 * graph has a single MCB then the essential cycles and MCB are the same. If the
 * graph has multiple MCB then the essential cycles is the intersect of all
 * MCBs. That is the cycles which appear in every MCB. This means that is is
 * possible to have no essential cycles in a molecule which clearly has cycles
 * (e.g. bridged system like bicyclo[2.2.2]octane). </li> <li> {@link
 * #tripletShort()} - the triple short cycles are the shortest cycle through
 * each triple of vertices. This allows one to generate the envelope rings of
 * some molecules (e.g. naphthalene) without generating all cycles. The cycles
 * are primarily useful for the CACTVS Substructure Keys (PubChem fingerprint).
 * </li> <li> {@link #vertexShort()} - the shortest cycles through each vertex.
 * Unlike the MCB, linear independence is not checked and it may not be possible
 * to generate all other cycles from this set. In practice the vertex/edge short
 * cycles are similar to MCB. </li> <li> {@link #edgeShort()} - the shortest
 * cycles through each edge. Unlike the MCB, linear independence is not checked
 * and it may not be possible to generate all other cycles from this set. In
 * practice the vertex/edge short cycles are similar to MCB. </li> </ul>
 *
 * @author John May
 * @cdk.module core
 * @cdk.githash
 */
public final class Cycles {

    /** Vertex paths for each cycle. */
    private final int[][]        paths;

    /** The input container - allows us to create 'Ring' objects. */
    private final IAtomContainer container;

    /** Mapping for quick lookup of bond mapping. */
    private final EdgeToBondMap  bondMap;

    /**
     * Internal constructor - may change in future but currently just takes the
     * cycle paths and the container from which they came.
     *
     * @param paths     the cycle paths (closed vertex walks)
     * @param container the input container
     */
    private Cycles(int[][] paths, IAtomContainer container, EdgeToBondMap bondMap) {
        this.paths = paths;
        this.container = container;
        this.bondMap = bondMap;
    }

    /**
     * How many cycles are stored.
     *
     * @return number of cycles
     */
    public int numberOfCycles() {
        return paths.length;
    }

    public int[][] paths() {
        int[][] cpy = new int[paths.length][];
        for (int i = 0; i < paths.length; i++)
            cpy[i] = paths[i].clone();
        return cpy;
    }

    /**
     * Convert the cycles to a {@link IRingSet} containing the {@link IAtom}s
     * and {@link IBond}s of the input molecule.
     *
     * @return ringset for the cycles
     */
    public IRingSet toRingSet() {
        return toRingSet(container, paths, bondMap);
    }

    /**
     * Create a cycle finder which will compute all simple cycles in a molecule.
     * The threshold values can not be tuned and is set at a value which will
     * complete in reasonable time for most molecules. To change the threshold
     * values please use the stand-alone {@link AllCycles} or {@link
     * org.openscience.cdk.ringsearch.AllRingsFinder}. All cycles is every
     * possible simple cycle (i.e. non-repeating vertices) in the chemical
     * graph. As an example - all simple cycles of anthracene includes, 3 cycles
     * of length 6, 2 of length 10 and 1 of length 14.
     *
     * <blockquote>
     * <pre>
     * CycleFinder cf = Cycles.all();
     * for (IAtomContainer m : ms) {
     *     try {
     *         Cycles   cycles = cf.find(m);
     *         IRingSet rings  = cycles.toRingSet();
     *     } catch (Intractable e) {
     *         // handle error - note it is common that finding all simple
     *         // cycles in chemical graphs is intractable
     *     }
     * }
     * </pre>
     * </blockquote>
     *
     * @return finder for all simple cycles
     * @see #all(org.openscience.cdk.interfaces.IAtomContainer)
     * @see AllCycles
     * @see org.openscience.cdk.ringsearch.AllRingsFinder
     */
    public static CycleFinder all() {
        return CycleComputation.ALL;
    }

    /**
     * All cycles of smaller than or equal to the specified length. If a length
     * is also provided to {@link CycleFinder#find(IAtomContainer, int)} the
     * minimum of the two limits is used.
     *
     * @param length maximum size or cycle to find
     * @return cycle finder
     */
    public static CycleFinder all(int length) {
        return new AllUpToLength(length);
    }

    /**
     * Create a cycle finder which will compute the minimum cycle basis (MCB) of
     * a molecule.
     *
     * <blockquote>
     * <pre>
     * CycleFinder cf = Cycles.mcb();
     * for (IAtomContainer m : ms) {
     *     try {
     *         Cycles   cycles = cf.find(m);
     *         IRingSet rings  = cycles.toRingSet();
     *     } catch (Intractable e) {
     *         // ignore error - MCB should never be intractable
     *     }
     * }
     * </pre>
     * </blockquote>
     *
     * @return finder for all simple cycles
     * @see #mcb(org.openscience.cdk.interfaces.IAtomContainer)
     * @see MinimumCycleBasis
     */
    public static CycleFinder mcb() {
        return CycleComputation.MCB;
    }

    /**
     * Create a cycle finder which will compute the relevant cycle basis (RC) of
     * a molecule.
     *
     * <blockquote>
     * <pre>
     * CycleFinder cf = Cycles.relevant();
     * for (IAtomContainer m : ms) {
     *     try {
     *         Cycles   cycles = cf.find(m);
     *         IRingSet rings  = cycles.toRingSet();
     *     } catch (Intractable e) {
     *         // ignore error - there may be an exponential number of cycles
     *         // but this is not currently checked
     *     }
     * }
     * </pre>
     * </blockquote>
     *
     * @return finder for relevant cycles
     * @see #relevant(org.openscience.cdk.interfaces.IAtomContainer)
     * @see RelevantCycles
     */
    public static CycleFinder relevant() {
        return CycleComputation.RELEVANT;
    }

    /**
     * Create a cycle finder which will compute the essential cycles of a
     * molecule.
     *
     * <blockquote>
     * <pre>
     * CycleFinder cf = Cycles.essential();
     * for (IAtomContainer m : ms) {
     *     try {
     *         Cycles   cycles = cf.find(m);
     *         IRingSet rings  = cycles.toRingSet();
     *     } catch (Intractable e) {
     *         // ignore error - essential cycles do not check tractability
     *     }
     * }
     * </pre>
     * </blockquote>
     *
     * @return finder for essential cycles
     * @see #relevant(org.openscience.cdk.interfaces.IAtomContainer)
     * @see RelevantCycles
     */
    public static CycleFinder essential() {
        return CycleComputation.ESSENTIAL;
    }

    /**
     * Create a cycle finder which will compute the triplet short cycles of a
     * molecule. These cycles are the shortest through each triplet of vertices
     * are utilised in the generation of CACTVS Substructure Keys (PubChem
     * Fingerprint). Currently the triplet cycles are non-canonical (which in
     * this algorithms case means unique). For finer tuning of options please
     * use the {@link TripletShortCycles}.
     *
     * <blockquote>
     * <pre>
     * CycleFinder cf = Cycles.tripletShort();
     * for (IAtomContainer m : ms) {
     *     try {
     *         Cycles   cycles = cf.find(m);
     *         IRingSet rings  = cycles.toRingSet();
     *     } catch (Intractable e) {
     *         // ignore error - triple short cycles do not check tractability
     *     }
     * }
     * </pre>
     * </blockquote>
     *
     * @return finder for triplet short cycles
     * @see #tripletShort(org.openscience.cdk.interfaces.IAtomContainer)
     * @see TripletShortCycles
     */
    public static CycleFinder tripletShort() {
        return CycleComputation.TRIPLET_SHORT;
    }

    /**
     * Create a cycle finder which will compute the shortest cycles of each
     * vertex in a molecule. Unlike the SSSR/MCB computation linear independence
     * is not required and provides some performance gain. In practise typical
     * chemical graphs are small and the linear independence check is relatively
     * fast.
     *
     * <blockquote>
     * <pre>
     * CycleFinder cf = Cycles.vertexShort();
     * for (IAtomContainer m : ms) {
     *     try {
     *         Cycles   cycles = cf.find(m);
     *         IRingSet rings  = cycles.toRingSet();
     *     } catch (Intractable e) {
     *         // ignore error - vertex short cycles do not check tractability
     *     }
     * }
     * </pre>
     * </blockquote>
     *
     * @return finder for vertex short cycles
     * @see #vertexShort(org.openscience.cdk.interfaces.IAtomContainer)
     */
    public static CycleFinder vertexShort() {
        return CycleComputation.VERTEX_SHORT;
    }

    /**
     * Create a cycle finder which will compute the shortest cycles of each
     * vertex in a molecule. Unlike the SSSR/MCB computation linear independence
     * is not required and provides some performance gain. In practise typical
     * chemical graphs are small and the linear independence check is relatively
     * fast.
     *
     * <blockquote>
     * <pre>
     * CycleFinder cf = Cycles.edgeShort();
     * for (IAtomContainer m : ms) {
     *     try {
     *         Cycles   cycles = cf.find(m);
     *         IRingSet rings  = cycles.toRingSet();
     *     } catch (Intractable e) {
     *         // ignore error - edge short cycles do not check tractability
     *     }
     * }
     * </pre>
     * </blockquote>
     *
     * @return finder for edge short cycles
     * @see #edgeShort(org.openscience.cdk.interfaces.IAtomContainer)
     */
    public static CycleFinder edgeShort() {
        return CycleComputation.EDGE_SHORT;
    }

    /**
     * Create a cycle finder which will compute a set of cycles traditionally
     * used by the CDK to test for aromaticity. This set of cycles is the
     * MCB/SSSR and {@link #all} cycles for fused systems with 3 or less rings.
     * This allows on to test aromaticity of envelope rings in compounds such as
     * azulene without generating an huge number of cycles for large fused
     * systems (e.g. fullerenes). The use case was that computation of all
     * cycles previously took a long time and ring systems with more than 2
     * rings were too difficult. However it is now more efficient to simply
     * check all cycles/rings without using the MCB/SSSR. This computation will
     * fail for complex fused systems but the failure is fast and one can easily
     * 'fall back' to a smaller set of cycles after catching the exception.
     *
     * <blockquote>
     * <pre>
     * CycleFinder cf = Cycles.cdkAromaticSet();
     * for (IAtomContainer m : ms) {
     *     try {
     *         Cycles   cycles = cf.find(m);
     *         IRingSet rings  = cycles.toRingSet();
     *     } catch (Intractable e) {
     *         // ignore error - edge short cycles do not check tractability
     *     }
     * }
     * </pre>
     * </blockquote>
     *
     * @return finder for cdk aromatic cycles
     * @see #edgeShort(org.openscience.cdk.interfaces.IAtomContainer)
     */
    public static CycleFinder cdkAromaticSet() {
        return CycleComputation.CDK_AROMATIC;
    }

    /**
     * Find all cycles in a fused system or if there were too many cycles
     * fallback and use the shortest cycles through each vertex. Typically the
     * types of molecules which the vertex short cycles are provided for are
     * fullerenes. This cycle finder is well suited to aromaticity.
     *
     * <blockquote>
     * <pre>
     * CycleFinder cf = Cycles.allOrVertexShort();
     * for (IAtomContainer m : ms) {
     *     try {
     *         Cycles   cycles = cf.find(m);
     *         IRingSet rings  = cycles.toRingSet();
     *     } catch (Intractable e) {
     *         // ignore error - edge short cycles do not check tractability
     *     }
     * }
     * </pre>
     * </blockquote>
     *
     * @return a cycle finder which computes all cycles if possible or provides
     * the vertex short cycles
     * @deprecated use {@link #or} to define a custom fall-back
     */
    @Deprecated
    public static CycleFinder allOrVertexShort() {
        return or(all(), vertexShort());
    }


    /**
     * Find and mark all cyclic atoms and bonds in the provided molecule.
     *
     * @param mol molecule
     * @see IBond#isInRing()
     * @see IAtom#isInRing()
     * @return Number of rings found (circuit rank)
     * @see <a href="https://en.wikipedia.org/wiki/Circuit_rank">Circuit Rank</a>
     */
    public static int markRingAtomsAndBonds(IAtomContainer mol) {
        EdgeToBondMap bonds = EdgeToBondMap.withSpaceFor(mol);
        return markRingAtomsAndBonds(mol, GraphUtil.toAdjList(mol, bonds), bonds);
    }

    /**
     * Find and mark all cyclic atoms and bonds in the provided molecule. This optimised version
     * allows the caller to optionally provided indexed fast access structure which would otherwise
     * be created.
     *
     * @param mol molecule
     * @see IBond#isInRing()
     * @see IAtom#isInRing()
     * @return Number of rings found (circuit rank)
     * @see <a href="https://en.wikipedia.org/wiki/Circuit_rank">Circuit Rank</a>
     */
    public static int markRingAtomsAndBonds(IAtomContainer mol, int[][] adjList, EdgeToBondMap bondMap) {
        RingSearch ringSearch = new RingSearch(mol, adjList);
        for (int v = 0; v < mol.getAtomCount(); v++) {
            mol.getAtom(v).setIsInRing(false);
            for (int w : adjList[v]) {
                // note we only mark the bond on second visit (first v < w) and
                // clear flag on first visit (or if non-cyclic)
                if (v > w && ringSearch.cyclic(v, w)) {
                    bondMap.get(v, w).setIsInRing(true);
                    mol.getAtom(v).setIsInRing(true);
                    mol.getAtom(w).setIsInRing(true);
                } else {
                    bondMap.get(v, w).setIsInRing(false);
                }
            }
        }
        return ringSearch.numRings();
    }

    /**
     * Use an auxiliary cycle finder if the primary method was intractable.
     *
     * <blockquote><pre>{@code
     * // all cycles or all cycles size <= 6
     * CycleFinder cf = Cycles.or(Cycles.all(), Cycles.all(6));
     * }</pre></blockquote>
     *
     * It is possible to nest multiple levels.
     *
     * <blockquote><pre>
     * // all cycles or relevant or essential
     * CycleFinder cf = Cycles.or(Cycles.all(),
     *                            Cycles.or(Cycles.relevant(),
     *                                      Cycles.essential()));
     * </pre></blockquote>
     *
     * @param primary   primary cycle finding method
     * @param auxiliary auxiliary cycle finding method if the primary failed
     * @return a new cycle finder
     */
    public static CycleFinder or(CycleFinder primary, CycleFinder auxiliary) {
        return new Fallback(primary, auxiliary);
    }

    /**
     * Find all simple cycles in a molecule. The threshold values can not be
     * tuned and is set at a value which will complete in reasonable time for
     * most molecules. To change the threshold values please use the stand-alone
     * {@link AllCycles} or {@link org.openscience.cdk.ringsearch.AllRingsFinder}.
     * All cycles is every possible simple cycle (i.e. non-repeating vertices)
     * in the chemical graph. As an example - all simple cycles of anthracene
     * includes, 3 cycles of length 6, 2 of length 10 and 1 of length 14.
     *
     * <blockquote>
     * <pre>
     * for (IAtomContainer m : ms) {
     *     try {
     *         Cycles   cycles = Cycles.all(m);
     *         IRingSet rings  = cycles.toRingSet();
     *     } catch (Intractable e) {
     *         // handle error - note it is common that finding all simple
     *         // cycles in chemical graphs is intractable
     *     }
     * }
     * </pre>
     * </blockquote>
     *
     * @return all simple cycles
     * @throws Intractable the algorithm reached a limit which caused it to
     *                     abort in reasonable time
     * @see #all()
     * @see AllCycles
     * @see org.openscience.cdk.ringsearch.AllRingsFinder
     */
    public static Cycles all(IAtomContainer container) throws Intractable {
        return all().find(container, container.getAtomCount());
    }

    /**
     * All cycles of smaller than or equal to the specified length.
     *
     * @param container input container
     * @param length    maximum size or cycle to find
     * @return all cycles
     * @throws Intractable computation was not feasible
     */
    public static Cycles all(IAtomContainer container, int length) throws Intractable {
        return all().find(container, length);
    }

    /**
     * Find the minimum cycle basis (MCB) of a molecule.
     *
     * <blockquote>
     * <pre>
     * for (IAtomContainer m : ms) {
     *     Cycles   cycles = Cycles.mcb(m);
     *     IRingSet rings  = cycles.toRingSet();
     * }
     * </pre>
     * </blockquote>
     *
     * @return cycles belonging to the minimum cycle basis
     * @see #mcb()
     * @see MinimumCycleBasis
     */
    public static Cycles mcb(IAtomContainer container) {
        return _invoke(mcb(), container);
    }

    /**
     * Find the smallest set of smallest rings (SSSR) - aka minimum cycle basis
     * (MCB) of a molecule.
     *
     * <blockquote>
     * <pre>
     * for (IAtomContainer m : ms) {
     *     Cycles   cycles = Cycles.sssr(m);
     *     IRingSet rings  = cycles.toRingSet();
     * }
     * </pre>
     * </blockquote>
     *
     * @return cycles belonging to the minimum cycle basis
     * @see #mcb()
     * @see #mcb(org.openscience.cdk.interfaces.IAtomContainer)
     * @see MinimumCycleBasis
     */
    public static Cycles sssr(IAtomContainer container) {
        return mcb(container);
    }

    /**
     * Find the relevant cycles of a molecule.
     *
     * <blockquote>
     * <pre>
     * for (IAtomContainer m : ms) {
     *     Cycles   cycles = Cycles.relevant(m);
     *     IRingSet rings  = cycles.toRingSet();
     * }
     * </pre>
     * </blockquote>
     *
     * @return relevant cycles
     * @see #relevant()
     * @see RelevantCycles
     */
    public static Cycles relevant(IAtomContainer container) {
        return _invoke(relevant(), container);
    }

    /**
     * Find the essential cycles of a molecule.
     *
     * <blockquote>
     * <pre>
     * for (IAtomContainer m : ms) {
     *     Cycles   cycles = Cycles.essential(m);
     *     IRingSet rings  = cycles.toRingSet();
     * }
     * </pre>
     * </blockquote>
     *
     * @return essential cycles
     * @see #relevant()
     * @see RelevantCycles
     */
    public static Cycles essential(IAtomContainer container) {
        return _invoke(essential(), container);
    }

    /**
     * Find the triplet short cycles of a molecule.
     *
     * <blockquote>
     * <pre>
     * for (IAtomContainer m : ms) {
     *     Cycles   cycles = Cycles.tripletShort(m);
     *     IRingSet rings  = cycles.toRingSet();
     * }
     * </pre>
     * </blockquote>
     *
     * @return triplet short cycles
     * @see #tripletShort()
     * @see TripletShortCycles
     */
    public static Cycles tripletShort(IAtomContainer container) {
        return _invoke(tripletShort(), container);
    }

    /**
     * Find the vertex short cycles of a molecule.
     *
     * <blockquote>
     * <pre>
     * for (IAtomContainer m : ms) {
     *     Cycles   cycles = Cycles.vertexShort(m);
     *     IRingSet rings  = cycles.toRingSet();
     * }
     * </pre>
     * </blockquote>
     *
     * @return triplet short cycles
     * @see #vertexShort()
     * @see VertexShortCycles
     */
    public static Cycles vertexShort(IAtomContainer container) {
        return _invoke(vertexShort(), container);
    }

    /**
     * Find the edge short cycles of a molecule.
     *
     * <blockquote>
     * <pre>
     * for (IAtomContainer m : ms) {
     *     Cycles   cycles = Cycles.edgeShort(m);
     *     IRingSet rings  = cycles.toRingSet();
     * }
     * </pre>
     * </blockquote>
     *
     * @return edge short cycles
     * @see #edgeShort()
     * @see EdgeShortCycles
     */
    public static Cycles edgeShort(IAtomContainer container) {
        return _invoke(edgeShort(), container);
    }

    /**
     * Derive a new cycle finder that only provides cycles without a chord.
     *
     * @param original find the initial cycles before filtering
     * @return cycles or the original without chords
     */
    public static CycleFinder unchorded(CycleFinder original) {
        return new Unchorded(original);
    }

    /**
     * Internal method to wrap cycle computations which <i>should</i> be
     * tractable. That is they currently won't throw the exception - if the
     * method does throw an exception an internal error is triggered as a sanity
     * check.
     *
     * @param finder    the cycle finding method
     * @param container the molecule to find the cycles of
     * @return the cycles of the molecule
     */
    private static Cycles _invoke(CycleFinder finder, IAtomContainer container) {
        return _invoke(finder, container, container.getAtomCount());
    }

    /**
     * Internal method to wrap cycle computations which <i>should</i> be
     * tractable. That is they currently won't throw the exception - if the
     * method does throw an exception an internal error is triggered as a sanity
     * check.
     *
     * @param finder    the cycle finding method
     * @param container the molecule to find the cycles of
     * @param length    maximum size or cycle to find
     * @return the cycles of the molecule
     */
    private static Cycles _invoke(CycleFinder finder, IAtomContainer container, int length) {
        try {
            return finder.find(container, length);
        } catch (Intractable e) {
            throw new RuntimeException("Cycle computation should not be intractable: ", e);
        }
    }

    /** Interbank enumeration of cycle finders. */
    private static enum CycleComputation implements CycleFinder {
        MCB {

            /** {@inheritDoc} */
            @Override
            int[][] apply(int[][] graph, int length) {
                InitialCycles ic = InitialCycles.ofBiconnectedComponent(graph, length);
                return new MinimumCycleBasis(ic, true).paths();
            }
        },
        ESSENTIAL {

            /** {@inheritDoc} */
            @Override
            int[][] apply(int[][] graph, int length) {
                InitialCycles ic = InitialCycles.ofBiconnectedComponent(graph, length);
                RelevantCycles rc = new RelevantCycles(ic);
                return new EssentialCycles(rc, ic).paths();
            }
        },
        RELEVANT {

            /** {@inheritDoc} */
            @Override
            int[][] apply(int[][] graph, int length) {
                InitialCycles ic = InitialCycles.ofBiconnectedComponent(graph, length);
                return new RelevantCycles(ic).paths();
            }
        },
        ALL {

            /** {@inheritDoc} */
            @Override
            int[][] apply(int[][] graph, int length) throws Intractable {
                final int threshold = 684; // see. AllRingsFinder.Threshold.Pubchem_99
                AllCycles ac = new AllCycles(graph, Math.min(length, graph.length), threshold);
                if (!ac.completed())
                    throw new Intractable("A large number of cycles were being generated and the"
                            + " computation was aborted. Please use AllCycles/AllRingsFinder with"
                            + " and specify a larger threshold or use a CycleFinder with a fall-back"
                            + " to a set unique cycles: e.g. Cycles.allOrVertexShort().");
                return ac.paths();
            }
        },
        TRIPLET_SHORT {

            /** {@inheritDoc} */
            @Override
            int[][] apply(int[][] graph, int length) throws Intractable {
                InitialCycles ic = InitialCycles.ofBiconnectedComponent(graph, length);
                return new TripletShortCycles(new MinimumCycleBasis(ic, true), false).paths();
            }
        },
        VERTEX_SHORT {

            /** {@inheritDoc} */
            @Override
            int[][] apply(int[][] graph, int length) throws Intractable {
                InitialCycles ic = InitialCycles.ofBiconnectedComponent(graph, length);
                return new VertexShortCycles(ic).paths();
            }
        },
        EDGE_SHORT {

            /** {@inheritDoc} */
            @Override
            int[][] apply(int[][] graph, int length) throws Intractable {
                InitialCycles ic = InitialCycles.ofBiconnectedComponent(graph, length);
                return new EdgeShortCycles(ic).paths();
            }
        },
        CDK_AROMATIC {

            /** {@inheritDoc} */
            @Override
            int[][] apply(int[][] graph, int length) throws Intractable {

                InitialCycles ic = InitialCycles.ofBiconnectedComponent(graph, length);
                MinimumCycleBasis mcb = new MinimumCycleBasis(ic, true);

                // As per the old aromaticity detector if the MCB/SSSR is made
                // of 2 or 3 rings we check all rings for aromaticity - otherwise
                // we just check the MCB/SSSR
                if (mcb.size() > 3) {
                    return mcb.paths();
                } else {
                    return ALL.apply(graph, length);
                }
            }
        },
        ALL_OR_VERTEX_SHORT {

            /** {@inheritDoc} */
            @Override
            int[][] apply(int[][] graph, int length) throws Intractable {
                final int threshold = 684; // see. AllRingsFinder.Threshold.Pubchem_99
                AllCycles ac = new AllCycles(graph, Math.min(length, graph.length), threshold);

                return ac.completed() ? ac.paths() : VERTEX_SHORT.apply(graph, length);
            }
        };

        /**
         * Apply cycle perception to the graph (g) - graph is expeced to be
         * biconnected.
         *
         * @param graph the graph (adjacency list)
         * @return the cycles of the graph
         * @throws Intractable the computation reached a set limit
         */
        abstract int[][] apply(int[][] graph, int length) throws Intractable;

        /**{@inheritDoc} */
        @Override
        public Cycles find(IAtomContainer molecule) throws Intractable {
            return find(molecule, molecule.getAtomCount());
        }

        /** {@inheritDoc} */
        @Override
        public Cycles find(IAtomContainer molecule, int length) throws Intractable {

            EdgeToBondMap bondMap = EdgeToBondMap.withSpaceFor(molecule);
            int[][] graph = GraphUtil.toAdjList(molecule, bondMap);
            RingSearch ringSearch = new RingSearch(molecule, graph);

            List<int[]> walks = new ArrayList<int[]>(6);

            // all isolated cycles are relevant - all we need to do is walk around
            // the vertices in the subset 'isolated'
            for (int[] isolated : ringSearch.isolated()) {
                if (isolated.length <= length) walks.add(GraphUtil.cycle(graph, isolated));
            }

            // each biconnected component which isn't an isolated cycle is processed
            // separately as a subgraph.
            for (int[] fused : ringSearch.fused()) {

                // make a subgraph and 'apply' the cycle computation - the walk
                // (path) is then lifted to the original graph
                for (int[] cycle : apply(GraphUtil.subgraph(graph, fused), length)) {
                    walks.add(lift(cycle, fused));
                }
            }

            return new Cycles(walks.toArray(new int[walks.size()][0]), molecule, bondMap);
        }

        /**{@inheritDoc} */
        @Override
        public Cycles find(IAtomContainer molecule, int[][] graph, int length) throws Intractable {

            RingSearch ringSearch = new RingSearch(molecule, graph);

            List<int[]> walks = new ArrayList<int[]>(6);

            // all isolated cycles are relevant - all we need to do is walk around
            // the vertices in the subset 'isolated'
            for (int[] isolated : ringSearch.isolated()) {
                walks.add(GraphUtil.cycle(graph, isolated));
            }

            // each biconnected component which isn't an isolated cycle is processed
            // separately as a subgraph.
            for (int[] fused : ringSearch.fused()) {

                // make a subgraph and 'apply' the cycle computation - the walk
                // (path) is then lifted to the original graph
                for (int[] cycle : apply(GraphUtil.subgraph(graph, fused), length)) {
                    walks.add(lift(cycle, fused));
                }
            }

            return new Cycles(walks.toArray(new int[walks.size()][0]), molecule, null);
        }
    }

    /**
     * Internal - lifts a path in a subgraph back to the original graph.
     *
     * @param path    a path
     * @param mapping vertex mapping
     * @return the lifted path
     */
    private static int[] lift(int[] path, int[] mapping) {
        for (int i = 0; i < path.length; i++) {
            path[i] = mapping[path[i]];
        }
        return path;
    }

    /**
     * Internal - convert a set of cycles to an ring set.
     *
     * @param container molecule
     * @param cycles    a cycle of the chemical graph
     * @param bondMap   mapping of the edges (int,int) to the bonds of the
     *                  container
     * @return the ring set
     */
    private static IRingSet toRingSet(IAtomContainer container, int[][] cycles, EdgeToBondMap bondMap) {

        // note currently no way to say the size of the RingSet
        // even through we know it
        IChemObjectBuilder builder = container.getBuilder();
        IRingSet rings = builder.newInstance(IRingSet.class);

        for (int[] cycle : cycles) {
            rings.addAtomContainer(toRing(container, cycle, bondMap));
        }

        return rings;
    }

    /**
     * Internal - convert a set of cycles to a ring.
     *
     * @param container molecule
     * @param cycle     a cycle of the chemical graph
     * @param bondMap   mapping of the edges (int,int) to the bonds of the
     *                  container
     * @return the ring for the specified cycle
     */
    private static IRing toRing(IAtomContainer container, int[] cycle, EdgeToBondMap bondMap) {

        IAtom[] atoms = new IAtom[cycle.length - 1];
        IBond[] bonds = new IBond[cycle.length - 1];

        for (int i = 1; i < cycle.length; i++) {
            int v = cycle[i];
            int u = cycle[i - 1];
            atoms[i - 1] = container.getAtom(u);
            bonds[i - 1] = getBond(container, bondMap, u, v);
        }

        IChemObjectBuilder builder = container.getBuilder();
        IAtomContainer ring = builder.newInstance(IAtomContainer.class, 0, 0, 0, 0);
        ring.setAtoms(atoms);
        ring.setBonds(bonds);

        return builder.newInstance(IRing.class, ring);
    }

    /**
     * Obtain the bond between the atoms at index 'u' and 'v'. If the 'bondMap'
     * is non-null it is used for direct lookup otherwise the slower linear
     * lookup in 'container' is used.
     *
     * @param container a structure
     * @param bondMap   optimised map of atom indices to bond instances
     * @param u         an atom index
     * @param v         an atom index (connected to u)
     * @return the bond between u and v
     */
    private static IBond getBond(IAtomContainer container, EdgeToBondMap bondMap, int u, int v) {
        if (bondMap != null) return bondMap.get(u, v);
        return container.getBond(container.getAtom(u), container.getAtom(v));
    }

    /**
     * All cycles smaller than or equal to a specified length.
     */
    private static final class AllUpToLength implements CycleFinder {

        private final int predefinedLength;

        // see. AllRingsFinder.Threshold.Pubchem_99
        private final int threshold = 684;

        private AllUpToLength(int length) {
            this.predefinedLength = length;
        }

        /**{@inheritDoc} */
        @Override
        public Cycles find(IAtomContainer molecule) throws Intractable {
            return find(molecule, molecule.getAtomCount());
        }

        /**{@inheritDoc} */
        @Override
        public Cycles find(IAtomContainer molecule, int length) throws Intractable {
            return find(molecule, GraphUtil.toAdjList(molecule), length);
        }

        /**{@inheritDoc} */
        @Override
        public Cycles find(IAtomContainer molecule, int[][] graph, int length) throws Intractable {
            RingSearch ringSearch = new RingSearch(molecule, graph);

            if (this.predefinedLength < length) length = this.predefinedLength;

            List<int[]> walks = new ArrayList<int[]>(6);

            // all isolated cycles are relevant - all we need to do is walk around
            // the vertices in the subset 'isolated'
            for (int[] isolated : ringSearch.isolated()) {
                if (isolated.length <= length) walks.add(GraphUtil.cycle(graph, isolated));
            }

            // each biconnected component which isn't an isolated cycle is processed
            // separately as a subgraph.
            for (int[] fused : ringSearch.fused()) {

                // make a subgraph and 'apply' the cycle computation - the walk
                // (path) is then lifted to the original graph
                for (int[] cycle : findInFused(GraphUtil.subgraph(graph, fused), length)) {
                    walks.add(lift(cycle, fused));
                }
            }

            return new Cycles(walks.toArray(new int[walks.size()][0]), molecule, null);
        }

        /**
         * Find rings in a biconnected component.
         *
         * @param g adjacency list
         * @return
         * @throws Intractable computation was not feasible
         */
        private int[][] findInFused(int[][] g, int length) throws Intractable {
            AllCycles allCycles = new AllCycles(g, Math.min(g.length, length), threshold);
            if (!allCycles.completed())
                throw new Intractable("A large number of cycles were being generated and the"
                        + " computation was aborted. Please us AllCycles/AllRingsFinder with"
                        + " and specify a larger threshold or an alternative cycle set.");
            return allCycles.paths();
        }
    }

    /**
     * Find cycles using a primary cycle finder, if the computation was
     * intractable fallback to an auxiliary cycle finder.
     */
    private static final class Fallback implements CycleFinder {

        private CycleFinder primary, auxiliary;

        /**
         * Create a fallback for two cycle finders.
         *
         * @param primary   the primary cycle finder
         * @param auxiliary the auxiliary cycle finder
         */
        private Fallback(CycleFinder primary, CycleFinder auxiliary) {
            this.primary = primary;
            this.auxiliary = auxiliary;
        }

        /**{@inheritDoc} */
        @Override
        public Cycles find(IAtomContainer molecule) throws Intractable {
            return find(molecule, molecule.getAtomCount());
        }

        /**{@inheritDoc} */
        @Override
        public Cycles find(IAtomContainer molecule, int length) throws Intractable {
            return find(molecule, GraphUtil.toAdjList(molecule), length);
        }

        /**{@inheritDoc} */
        @Override
        public Cycles find(IAtomContainer molecule, int[][] graph, int length) throws Intractable {
            try {
                return primary.find(molecule, graph, length);
            } catch (Intractable e) {
                // auxiliary may still thrown an exception
                return auxiliary.find(molecule, graph, length);
            }
        }
    }

    /**
     * Remove cycles with a chord from an existing set of cycles.
     */
    private static final class Unchorded implements CycleFinder {

        private CycleFinder primary;

        /**
         * Filter any cycles produced by the {@code primary} cycle finder and
         * only allow those without a chord.
         *
         * @param primary the primary cycle finder
         */
        private Unchorded(CycleFinder primary) {
            this.primary = primary;
        }

        /**{@inheritDoc} */
        @Override
        public Cycles find(IAtomContainer molecule) throws Intractable {
            return find(molecule, molecule.getAtomCount());
        }

        /**{@inheritDoc} */
        @Override
        public Cycles find(IAtomContainer molecule, int length) throws Intractable {
            return find(molecule, GraphUtil.toAdjList(molecule), length);
        }

        /**{@inheritDoc} */
        @Override
        public Cycles find(IAtomContainer molecule, int[][] graph, int length) throws Intractable {

            Cycles inital = primary.find(molecule, graph, length);

            int[][] filtered = new int[inital.numberOfCycles()][0];
            int n = 0;

            for (int[] path : inital.paths) {
                if (accept(path, graph)) filtered[n++] = path;
            }

            return new Cycles(Arrays.copyOf(filtered, n), inital.container, inital.bondMap);
        }

        /**
         * The cycle path is accepted if it does not have chord.
         *
         * @param path  a path
         * @param graph the adjacency of atoms
         * @return accept the path as unchorded
         */
        private boolean accept(int[] path, int[][] graph) {
            BitSet vertices = new BitSet();

            for (int v : path)
                vertices.set(v);

            for (int j = 1; j < path.length; j++) {
                int v = path[j];
                int prev = path[j - 1];
                int next = path[(j + 1) % (path.length - 1)];

                for (int w : graph[v]) {
                    // chord found
                    if (w != prev && w != next && vertices.get(w)) return false;
                }

            }

            return true;
        }
    }
}
