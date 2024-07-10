/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John Mayfield
 *               2024 John Mayfield
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

package org.openscience.cdk.aromaticity;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.Intractable;
import org.openscience.cdk.graph.CycleFinder;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.openscience.cdk.aromaticity.Aromaticity.*;
import static org.openscience.cdk.interfaces.IChemObject.AROMATIC;
import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;

/**
 * A configurable model to perceive aromatic systems. Aromaticity is useful as
 * both a chemical property indicating stronger stabilisation and as a way to
 * treat different resonance forms as equivalent. Each has its own implications
 * the first in physicochemical attributes and the second in similarity,
 * depiction and storage.
 *
 * To address the resonance forms, several simplified (sometimes conflicting)
 * models have arisen. Generally the models <b>loosely</b> follow
 * <a href="http://en.wikipedia.org/wiki/H%C3%BCckel's_rule">Hückel's rule</a>
 * for determining aromaticity. A common omission being that planarity is not
 * tested and chemical compounds which are non-planar can be perceived
 * as aromatic. An example of one such compound is, cyclodeca-1,3,5,7,9-pentaene.
 *
 * Although there is not a single universally accepted model there are models
 * which may better suited for a specific use (<a href="http://www.slideshare.net/NextMoveSoftware/cheminformatics-toolkits-a-personal-perspective">Cheminformatics Toolkits: A Personal Perspective, Roger Sayle</a>).
 * The different models are often ill-defined or unpublished but it is important
 * to acknowledge that there are differences.
 *
 * Although models may get more complicated (e.g. considering tautomers)
 * normally the reasons for differences are:
 * <ul>
 *     <li>the atoms allowed and how many electrons each contributes</li>
 *     <li>the rings/cycles are tested</li>
 * </ul>
 *
 * This implementation allows configuration of these via an {@link
 * ElectronDonation} model and {@link CycleFinder}. To obtain an instance
 * of the electron donation model use one of the constants:
 * <ul>
 *     <li>{@link Model#Mdl}</li>
 *     <li>{@link Model#Daylight}</li>
 *     <li>{@link Model#OpenSmiles}</li>
 *     <li>{@link Model#CDK_1x}</li>
 *     <li>{@link Model#CDK_2x}</li>
 * </ul>
 *
 * <br>
 * <br>
 * <b>Recommended Usage:</b><br>
 * Which model/cycles to use depends on the situation but a good general
 * purpose configuration is shown below which applies the model to all
 * cycles/rings in a molecule using a backtracking search:
 * <blockquote><pre>{@code
 * for (IAtomContainer molecule : molecules) {
 *     Cycles.markRingAtomsAndBonds(molecule);
 *     if (!Aromaticity.apply(Aromaticity.Model.CDX_2x, molecule)) {
 *         // molecule has v. complex rings! but aromaitcity
 *         // will still have been marked on small rings
 *     }
 * }
 * }</pre></blockquote>
 *
 * Alternative usage which throws an exception when the molecule is too
 * complex:
 *
 * <blockquote><pre>{@code
 * ElectronDonation model       = ElectronDonation.CDK_2x;
 * CycleFinder      cycles      = Cycles.all();
 * Aromaticity      aromaticity = new Aromaticity(model, cycles);
 * for (IAtomContainer molecule : molecules) {
 *   try {
 *      aromaticity.apply(molecule);
 *   } catch (CDKException ex) {
 *      // aromaticity failed, molecule to complex
 *   }
 * }
 * }</pre></blockquote>
 *
 * @author John Mayfield
 * @cdk.module standard
 * @cdk.githash
 * @see <a href="http://en.wikipedia.org/wiki/H%C3%BCckel's_rule">Hückel's
 *      rule</a>
 * @see <a href="http://www.slideshare.net/NextMoveSoftware/cheminformatics-toolkits-a-personal-perspective">Cheminformatics Toolkits: A Personal Perspective, Roger Sayle</a>
 * @see <a href="http://blueobelisk.shapado.com/questions/aromaticity-perception-differences">Aromaticity Perception Differences, Blue Obelisk</a>
 */
public final class Aromaticity {

    /**
     * Container for aromaticity models.
     */
    public static class Model {
        private Model() {}

        /**
         * A model similar to what Daylight used for SMILES/SMARTS. If you are
         * using SMILES/SMARTS then this is a good model to use.
         */
        public static ElectronDonation Daylight      = new AromaticTypeModel(AromaticTypeModel.DAYLIGHT);
        /**
         * Any atom connected to a single pi bond in a ring contributes 1
         * electron.
         */
        public static ElectronDonation PiBonds       = new PiBondModel();
        /**
         * A model similar to what MDL/Symyx used. This is similar to the PiBonds
         * model but only allows certain atom types. Exo cyclic pi bonds are not
         * allowed.
         */
        public static ElectronDonation Mdl           = new AromaticTypeModel(AromaticTypeModel.MDL);
        /**
         * Similar to the Daylight model but also allows boron and some arsenic
         * variants as well as charge separated sulfinyl/seleninyl.
         */
        public static ElectronDonation OpenSmiles    = new AromaticTypeModel(AromaticTypeModel.OPEN_SMILES);
        /**
         * Somewhere in between Mdl/Daylight, allows indole/pyrrole/furan but
         * does not allow exo-cyclic bonds.
         */
        public static ElectronDonation CDK_1x        = new AromaticTypeModel(AromaticTypeModel.CDK_1x);
        /**
         * Similar to the Daylight model but also allows boron, tellurium and
         * some arsenic variants as well not allowing sulfinyl/seleninyl since
         * these are Sp3.
         */
        public static ElectronDonation CDK_2x        = new AromaticTypeModel(AromaticTypeModel.CDK_2x);
        /**
         * The old aromatic bond based on CDK atom types, this model requires
         * atom types have been assigned before calling. Generally the CDK_1x
         * should be indistinguishable and run faster.
         */
        public static ElectronDonation CDK_AtomTypes = new AtomTypeModel(false);
    }

    // Backtracking search limit, avoids hangs
    private static final int ALLOWED_STATE_COUNT = 1048576;

    /** Find how many electrons each atom contributes. */
    private final ElectronDonation model;

    /** The method to find cycles which will be tested for aromaticity. */
    private final CycleFinder      cycles;

    /**
     * Flag to short circuit the cycle finder and do a backtracking
     * search.
     */
    private final boolean backtracking;
    private int maxRingSize;

    /**
     * Create an aromaticity model using the specified electron donation {@code
     * model} which is tested on the {@code cycles}. The {@code model} defines
     * how many π-electrons each atom may contribute to an aromatic system. The
     * {@code cycles} defines the {@link CycleFinder} which is used to find
     * cycles in a molecule. The total electron donation from each atom in each
     * cycle is counted and checked. If the electron contribution is equal to
     * {@code 4n + 2} for a {@code n >= 0} then the cycle is considered
     * aromatic.  Changing the electron contribution model or which cycles
     * are tested affects which atoms/bonds are found to be aromatic. There are
     * several {@link ElectronDonation} models and {@link
     * org.openscience.cdk.graph.Cycles} available. A good choice for the cycles
     * is to use {@link org.openscience.cdk.graph.Cycles#all()} falling back to
     * {@link org.openscience.cdk.graph.Cycles#relevant()} on failure. Finding all cycles is very
     * fast but may produce an exponential number of cycles. It is therefore not
     * feasible for complex fused systems and an exception is thrown.
     * In such cases the aromaticity can either be skipped or a simpler
     * polynomial cycle set {@link org.openscience.cdk.graph.Cycles#relevant()}
     * used.
     *
     * <blockquote><pre>
     *
     * // mimics the CDKHuckelAromaticityDetector
     * Aromaticity aromaticity = new Aromaticity(ElectronDonation.cdk(),
     *                                           Cycles.cdkAromaticSet());
     *
     * // mimics the DoubleBondAcceptingAromaticityDetector
     * Aromaticity aromaticity = new Aromaticity(ElectronDonation.cdkAllowingExocyclic(),
     *                                           Cycles.cdkAromaticSet());
     *
     * // a good model for writing SMILES
     * Aromaticity aromaticity = new Aromaticity(ElectronDonation.daylight(),
     *                                           Cycles.all());
     *
     * // a good model for writing MDL/Mol2
     * Aromaticity aromaticity = new Aromaticity(ElectronDonation.piBonds(),
     *                                           Cycles.all());
     *
     * </pre></blockquote>
     *
     * @param model the model
     * @param cycles the cycles to apply it to
     * @see ElectronDonation
     * @see org.openscience.cdk.graph.Cycles
     */
    public Aromaticity(ElectronDonation model, CycleFinder cycles) {
        this.model = Objects.requireNonNull(model);
        this.cycles = Objects.requireNonNull(cycles);

        // this reflection give users faster speeds without them having to
        // change their code
        if (this.cycles instanceof Enum && ((Enum<?>) this.cycles).name().equals("ALL")) {
            this.backtracking = true;
        } else if (this.cycles.getClass().getName().equals("org.openscience.cdk.graph.Cycles$AllUpToLength")) {
            this.backtracking = true;
            try {
                Class<?> cls = this.cycles.getClass();
                Field fld = cls.getDeclaredField("predefinedLength");
                fld.setAccessible(true);
                this.maxRingSize = fld.getInt(this.cycles);
            } catch (Exception ex) {
                LoggingToolFactory.createLoggingTool(Aromaticity.class)
                                  .error("Warning: could not access Cycles.all({length}) use aromaticity.applyAllCycles(,{length})");
                // ignored
            }
        } else {
            this.backtracking = false;
        }
    }

    private static boolean isFusionOrBridgeAtom(IAtom atom, int[] contrib) {
        if (atom.getBondCount() < 3 || contrib[atom.getIndex()] < 0)
            return false;
        int count = 0;
        for (IBond bond : atom.bonds()) {
            if (!bond.isInRing())
                continue;
            if (contrib[bond.getOther(atom).getIndex()] != -1)
                count++;
        }
        return count >= 3;
    }

    private static void pruneTerminalAromaticAtoms(int[] contrib, IAtom atom)
    {
        while (atom != null){
            IAtom next = null;
            for (IBond bond : atom.bonds()) {
                if (!bond.isInRing())
                    continue;
                IAtom nbor = bond.getOther(atom);
                if (contrib[nbor.getIndex()] == -1)
                    continue;
                if (next != null)
                    return; // D2
                next = nbor;
            }
            contrib[atom.getIndex()] = -1;
            atom = next;
        }
    }

    private static void pruneTerminalAromaticAtoms(int[] contrib, IAtomContainer mol)
    {
        for (IAtom atom : mol.atoms())
            if (contrib[atom.getIndex()] != -1)
                pruneTerminalAromaticAtoms(contrib, atom);
    }

    private static void floodFill(int[] visit, IAtom atom, IBond prev, int label)
    {
        visit[atom.getIndex()] = label;
        for (IBond bond : atom.bonds()) {
            if (bond == prev || !bond.isInRing())
                continue;
            final IAtom nbor = bond.getOther(atom);
            if (visit[nbor.getIndex()] == 0)
                floodFill(visit, nbor, bond, label);
        }
    }

    /**
     * Mark a bond and both end atoms as aromatic.
     * @param bond the bond
     */
    private static void makeAromatic(final IBond bond) {
        bond.set(AROMATIC);
        bond.getBegin().set(AROMATIC);
        bond.getEnd().set(AROMATIC);
    }

    /**
     * Mark a molecule is aromatic if it has any aromatic atoms.
     * @param molecule the molecule
     */
    private static void updateMolFlag(final IAtomContainer molecule) {
        for (IAtom atom : molecule.atoms())
            if (atom.getFlag(AROMATIC)) {
                molecule.set(AROMATIC);
                break;
            }
    }


    private static final class AllCyclesState {
        private final int[] visit;
        private final int[] contrib;
        private int numStates = 0;
        private int maxStates = 0;
        private boolean error = false;

        private AllCyclesState(int[] visit, int[] contrib) {
            this.visit = visit;
            this.contrib = contrib;
        }

        private void setMaxStates(int limit) {
            this.maxStates = limit;
        }

        private boolean markPath(IAtom atom, IBond prev, int limit, int pi, int depth) {
            if (limit != 0 && depth > limit)
                return false;
            if (maxStates != 0 && ++numStates >= maxStates)
                return false;
            pi += contrib[atom.getIndex()];
            visit[atom.getIndex()] = 1;
            for (IBond bond : atom.bonds()) {
                if (bond == prev || !bond.isInRing())
                    continue;
                IAtom nbor = bond.getOther(atom);
                if (visit[nbor.getIndex()] == 2) {
                    if (checkHuckelSum(pi)) {
                        makeAromatic(bond);
                        visit[atom.getIndex()] = 0;
                        return true;
                    }
                } else if (visit[nbor.getIndex()] == 0) {
                    if (markPath(nbor, bond, limit, pi, depth + 1)) {
                        makeAromatic(bond);
                        visit[atom.getIndex()] = 0;
                        return true;
                    }
                }
            }
            visit[atom.getIndex()] = 0;
            return false;
        }

        private void processComplexRings(List<IAtom> d3Atoms, int limit) {
            for (final IAtom atom : d3Atoms) {
                int pi = contrib[atom.getIndex()];
                visit[atom.getIndex()] = 2;
                for (IBond bond : atom.bonds()) {
                    if (bond.isAromatic() || !bond.isInRing())
                        continue;
                    final IAtom nbor = bond.getOther(atom);
                    if (visit[nbor.getIndex()] != 0)
                        continue;

                    numStates = 0;

                    if (markPath(nbor, bond, limit, pi, 2))
                        makeAromatic(bond);

                    if (maxStates != 0 && numStates >= maxStates) {
                        error = true;
                        return;
                    }
                }
                visit[atom.getIndex()] = 0;
            }
        }
    }



    private static void processSimpleRings(IAtomContainer molecule,
                                           int[] visit,
                                           int[] contrib) {
        final List<IBond> ring = new ArrayList<>(molecule.getBondCount());
        for (IAtom atom : molecule.atoms()) {
            if (visit[atom.getIndex()] != 0)
                continue;
            ring.clear();
            int sum = 0;
            IBond prev = null;
            while (atom != null) {
                IAtom next = null;
                visit[atom.getIndex()] = 1;
                sum += contrib[atom.getIndex()];
                for (IBond bond : atom.bonds()) {
                    if (bond == prev || !bond.isInRing())
                        continue;
                    final IAtom nbor = bond.getOther(atom);
                    if (visit[nbor.getIndex()] != 0)
                        continue;
                    ring.add(bond);
                    prev = bond;
                    next = nbor;
                }
                atom = next;
            }
            if (!checkHuckelSum(sum))
                continue;
            for (IBond bond : ring)
                makeAromatic(bond);
        }
    }

    /**
     * Apply an aromaticity model to all cycles/rings in a molecule up to the
     * specified {@code maxRingSize}. This includes rings round the outside of a
     * fused/bridged cycles (e.g. azulene).
     *
     * <pre>{@code
     * Cycles.markRingAtomsAndBonds(molecule); // required!!
     * if (!Aromaticity.apply(Aromaticity.Model.Daylight, molecule)) {
     *     // molecule had some v. complex rings in it!
     * }
     * }</pre>
     *
     * The return value indicates if the model was applied full or not.
     * Even if false is returned small rings will have been marked as aromatic.
     * If is more an indication that there are some bonds of which it is not
     * possible to know for sure if they should be marked as aromatic. Typical
     * these molecules are often complex cage like systems with some saturated
     * atoms, fully unsaturated molecules like fullerene actually run quickly.
     * Setting a max ring size to something reasonable like 14 (3x fused 6
     * membered rings) avoids this issue.
     *
     * @param model the aromaticity model
     * @param molecule the molecule
     * @return if the model could be fully applied (true) or if the computation
     *         was not possible (false)
     */
    public static boolean apply(ElectronDonation model, IAtomContainer molecule) {
        return apply(model, molecule, Math.max(3, molecule.getAtomCount()));
    }

    /**
     * Apply an aromaticity model to all cycles/rings in a molecule up to the
     * specified {@code maxRingSize}. This includes rings round the outside of a
     * fused/bridged cycles (e.g. azulene).
     *
     * <pre>{@code
     * Cycles.markRingAtomsAndBonds(molecule); // required!!
     * if (!Aromaticity.apply(Aromaticity.Model.Daylight, molecule)) {
     *     // molecule had some v. complex rings in it!
     * }
     * }</pre>
     *
     * The return value indicates if the model was applied full or not.
     * Even if false is returned small rings will have been marked as aromatic.
     * If is more an indication that there are some bonds of which it is not
     * possible to know for sure if they should be marked as aromatic. Typical
     * these molecules are often complex cage like systems with some saturated
     * atoms, fully unsaturated molecules like fullerene actually run quickly.
     * Setting a max ring size to something reasonable like 14 (3x fused 6
     * membered rings) avoids this issue.
     *
     * @param model the aromaticity model
     * @param molecule the molecule
     * @param maxRingSize max ring size to check (optional)
     * @return if the model could be fully applied (true) or if the computation
     *         was not possible (false)
     */
    public static boolean apply(final ElectronDonation model,
                                final IAtomContainer molecule,
                                final int maxRingSize) {
        clear(molecule);

        if (maxRingSize < 3)
            throw new IllegalArgumentException("maxRingSize="
                                               + maxRingSize
                                               + " <3 doesn't make sense");

        // initial ring/cycle search and get the contribution from each atom
        final int[] contrib = model.contribution(molecule);

        // from the initial contribution assignment prune atoms which are only
        // connected to < 2 other aromatic atoms these can't be aromatic as
        // a ring/cycle can not be made, we mark these as having a -1
        // contribution
        pruneTerminalAromaticAtoms(contrib, molecule);

        int[] visit = new int[molecule.getAtomCount()];
        for (int i = 0; i < contrib.length; i++)
            if (contrib[i] < 0) visit[i] = 1;

        // mark all the "complex" ring systems, these have D3+ aromatic atom
        // label any atom in a system with a d3 as visit=2, we will process
        // handle these after first sorting out the simple rings
        List<IAtom> d3Atoms = new ArrayList<>();
        for (IAtom atom : molecule.atoms()) {
            if (isFusionOrBridgeAtom(atom, contrib)) {
                d3Atoms.add(atom);
                floodFill(visit, atom, null, 2);
            }
        }

        // process the simple cycles (all D2 atoms), we can do this with a
        // simple non-recursive search, basic benzene, pyrrole rings etc
        processSimpleRings(molecule, visit, contrib);

        if (d3Atoms.isEmpty())
            return true;

        // reset the visit=2 to visit=0, we will now process these, we can
        // skip this but the
        for (int i=0; i<visit.length; i++)
            if (visit[i] == 2) visit[i] = 0;

        AllCyclesState state = new AllCyclesState(visit, contrib);

        // now the "hard" work, an iteratively deepening DFS from the D3+
        // atoms, if the limit it more than 6 we run a 6 member search first to
        // catch common cases, e.g. benzene's in fulerene. Like wise size 10 is
        // the outside of napthalene like rings etc.
        state.setMaxStates(0);
        if (maxRingSize > 6)
            state.processComplexRings(d3Atoms, 6);
        if (maxRingSize > 10)
            state.processComplexRings(d3Atoms, 10);

        state.setMaxStates(ALLOWED_STATE_COUNT);
        state.processComplexRings(d3Atoms, maxRingSize);

        return !state.error;
    }

    /**
     * Find the bonds of a {@code molecule} which this model determined were
     * aromatic.
     *
     * <blockquote><pre>{@code
     * Aromaticity aromaticity = new Aromaticity(ElectronDonation.cdk(),
     *                                           Cycles.all());
     * IAtomContainer container = ...;
     * try {
     *     Set<IBond> bonds          = aromaticity.findBonds(container);
     *     int        nAromaticBonds = bonds.size();
     * } catch (CDKException e) {
     *     // cycle computation was intractable
     * }
     * }</pre></blockquote>
     *
     * @param molecule the molecule to apply the model to
     * @return the set of bonds which are aromatic
     * @throws CDKException a problem occurred with the cycle perception - one
     *                      can retry with a simpler cycle set
     */
    public Set<IBond> findBonds(IAtomContainer molecule) throws CDKException {

        // build graph data-structures for fast cycle perception
        final EdgeToBondMap bondMap = EdgeToBondMap.withSpaceFor(molecule);
        final int[][] graph = GraphUtil.toAdjList(molecule, bondMap);

        // initial ring/cycle search and get the contribution from each atom
        Cycles.markRingAtomsAndBonds(molecule);
        final int[] electrons = model.contribution(molecule);

        final Set<IBond> bonds = new HashSet<>(2*molecule.getBondCount());

        // obtain the subset of electron contributions which are >= 0 (i.e.
        // allowed to be aromatic) - we then find the cycles in this subgraph
        // and 'lift' the indices back to the original graph using the subset
        // as a lookup
        final int[] subset = subset(electrons);
        final int[][] subgraph = GraphUtil.subgraph(graph, subset);

        // for each cycle if the electron sum is valid add the bonds of the
        // cycle to the set or aromatic bonds
        for (final int[] cycle : cycles.find(molecule, subgraph, subgraph.length).paths()) {
            if (checkElectronSum(cycle, electrons, subset)) {
                for (int i = 1; i < cycle.length; i++) {
                    bonds.add(bondMap.get(subset[cycle[i]], subset[cycle[i - 1]]));
                }
            }
        }

        return bonds;
    }

    /**
     * Apply this aromaticity model to a molecule. Any existing aromaticity
     * flags are removed - even if no aromatic bonds were found. This follows
     * the idea of <i>applying</i> an aromaticity model to a molecule such that
     * the result is the same irrespective of existing aromatic flags. If you
     * require aromatic flags to be preserved the {@link
     * #findBonds(IAtomContainer)} can be used to find bonds without setting any
     * flags.
     *
     * <blockquote><pre>
     * Aromaticity aromaticity = new Aromaticity(ElectronDonation.cdk(),
     *                                           Cycles.all());
     * IAtomContainer container = ...;
     * try {
     *     if (aromaticity.apply(container)) {
     *         //
     *     }
     * } catch (CDKException e) {
     *     // cycle computation was intractable
     * }
     * </pre></blockquote>
     *
     * @param molecule the molecule to apply the model to
     * @return the model found the molecule was aromatic
     */
    public boolean apply(IAtomContainer molecule) throws CDKException {
        if (backtracking) {
            Cycles.markRingAtomsAndBonds(molecule);
            if (!apply(model, molecule, maxRingSize == 0 ? Math.max(molecule.getAtomCount(),3) : maxRingSize)) {
                throw new Intractable("Molecule is too complex to fully verify aromaticity of all bonds, " +
                                      "smaller cycles are been marked");
            }
            updateMolFlag(molecule);
        } else {
            clear(molecule);
            Set<IBond> bonds = findBonds(molecule);
            for (final IBond bond : bonds)
                makeAromatic(bond);
            if (!bonds.isEmpty())
                molecule.set(AROMATIC);
        }
        return molecule.getFlag(AROMATIC);
    }

    /**
     * Check if the number electrons in the {@code cycle} could delocalise. The
     * {@code contributions} array indicates how many π-electrons each atom can
     * contribute.
     *
     * @param cycle         closed walk (last and first vertex the same) of
     *                      vertices which form a cycle
     * @param contributions π-electron contribution from each atom
     * @return the number of electrons indicate they could delocalise
     */
    private static boolean checkElectronSum(final int[] cycle, final int[] contributions, final int[] subset) {
        return checkHuckelSum(electronSum(cycle, contributions, subset));
    }

    /**
     * Count the number electrons in the {@code cycle}. The {@code
     * contributions} array indicates how many π-electrons each atom can
     * contribute. When the contribution of an atom is less than 0 the sum for
     * the cycle is always 0.
     *
     * @param cycle         closed walk (last and first vertex the same) of
     *                      vertices which form a cycle
     * @param contributions π-electron contribution from each atom
     * @return the total sum of π-electrons contributed by the {@code cycle}
     */
    static int electronSum(final int[] cycle, final int[] contributions, final int[] subset) {
        int sum = 0;
        for (int i = 1; i < cycle.length; i++)
            sum += contributions[subset[cycle[i]]];
        return sum;
    }

    /**
     * Given the number of pi electrons verify that {@code sum = 4n + 2} for
     * {@code n >= 0}.
     *
     * @param sum π-electron sum
     * @return there is an {@code n} such that {@code 4n + 2} is equal to the
     *         provided {@code sum}.
     */
    static boolean checkHuckelSum(final int sum) {
        return (sum - 2) % 4 == 0;
    }

    /**
     * Obtain a subset of the vertices which can contribute {@code electrons}
     * and are allowed to be involved in an aromatic system.
     *
     * @param electrons electron contribution
     * @return vertices which can be involved in an aromatic system
     */
    private static int[] subset(final int[] electrons) {
        int[] vs = new int[electrons.length];
        int n = 0;

        for (int i = 0; i < electrons.length; i++)
            if (electrons[i] >= 0) vs[n++] = i;

        return Arrays.copyOf(vs, n);
    }

    /**
     * Clear all aromatic flags from the atoms/bonds and molecule.
     *
     * @param mol the molecule
     */
    public static void clear(IAtomContainer mol) {
        mol.clear(AROMATIC);
        for (IBond bond : mol.bonds())
            bond.clear(AROMATIC);
        for (IAtom atom : mol.atoms())
            atom.clear(AROMATIC);
    }

    /** Replicates CDKHueckelAromaticityDetector. */
    private static final Aromaticity CDK_LEGACY = new Aromaticity(ElectronDonation.cdk(),
                                                                  Cycles.cdkAromaticSet());

    /**
     * Access an aromaticity instance that replicates the previously utilised -
     * CDKHueckelAromaticityDetector. It has the following configuration:
     *
     * <pre>{@code
     * new Aromaticity(ElectronDonation.cdk(),
     *                 Cycles.cdkAromaticSet());
     * }</pre>
     *
     * <p>
     * This model is not necessarily bad (or really considered legacy) but
     * should <b>not</b> be considered a gold standard model that covers all
     * possible cases. It was however the primary method used in previous
     * versions of the CDK (1.4).
     * </p>
     *
     * <p>
     * This factory method is provided for convenience for
     * those wishing to replicate aromaticity perception used in previous
     * versions. The same electron donation model can be used to test
     * aromaticity of more cycles. For instance, the following configuration
     * will identify more bonds in a some structures as aromatic:
     * </p>
     *
     * <pre>{@code
     * new Aromaticity(ElectronDonation.cdk(),
     *                 Cycles.or(Cycles.all(), Cycles.relevant()));
     * }</pre>
     *
     * @return aromaticity instance that is configured to perform identically
     *         to the primary aromaticity model in version 1.4.
     * @deprecated Use a better aromaticity model
     */
    @Deprecated
    public static Aromaticity cdkLegacy() {
        return CDK_LEGACY;
    }
}
