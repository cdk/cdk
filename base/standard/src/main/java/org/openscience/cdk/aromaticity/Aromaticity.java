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

package org.openscience.cdk.aromaticity;

import com.google.common.collect.Sets;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.CycleFinder;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.ringsearch.RingSearch;

import java.util.Arrays;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.openscience.cdk.CDKConstants.ISAROMATIC;
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
 * of the electron donation model use one of the factory methods,
 * {@link ElectronDonation#cdk()}, {@link ElectronDonation#cdkAllowingExocyclic()},
 * {@link ElectronDonation#daylight()} or {@link ElectronDonation#piBonds()}.
 *
 * <br>
 * <br>
 * <b>Recommended Usage:</b><br>
 * Which model/cycles to use depends on the situation but a good general
 * purpose configuration is shown below:
 * <blockquote><pre>
 * ElectronDonation model       = ElectronDonation.daylight();
 * CycleFinder      cycles      = Cycles.or(Cycles.all(), Cycles.all(6));
 * Aromaticity      aromaticity = new Aromaticity(model, cycles);
 *
 * // apply our configured model to each molecule
 * for (IAtomContainer molecule : molecules) {
 *     aromaticity.apply(molecule);
 * }
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module standard
 * @cdk.githash
 * @see <a href="http://en.wikipedia.org/wiki/H%C3%BCckel's_rule">Hückel's
 *      rule</a>
 * @see <a href="http://www.slideshare.net/NextMoveSoftware/cheminformatics-toolkits-a-personal-perspective">Cheminformatics Toolkits: A Personal Perspective, Roger Sayle</a>
 * @see <a href="http://blueobelisk.shapado.com/questions/aromaticity-perception-differences">Aromaticity Perception Differences, Blue Obelisk</a>
 */
public final class Aromaticity {

    /** Find how many electrons each atom contributes. */
    private final ElectronDonation model;

    /** The method to find cycles which will be tested for aromaticity. */
    private final CycleFinder      cycles;

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
     * @param model
     * @param cycles
     * @see ElectronDonation
     * @see org.openscience.cdk.graph.Cycles
     */
    public Aromaticity(ElectronDonation model, CycleFinder cycles) {
        this.model = checkNotNull(model);
        this.cycles = checkNotNull(cycles);
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
        final RingSearch ringSearch = new RingSearch(molecule, graph);
        final int[] electrons = model.contribution(molecule, ringSearch);

        final Set<IBond> bonds = Sets.newHashSetWithExpectedSize(molecule.getBondCount());

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

        Set<IBond> bonds = findBonds(molecule);

        // clear existing flags
        molecule.setFlag(ISAROMATIC, false);
        for (IBond bond : molecule.bonds())
            bond.setIsAromatic(false);
        for (IAtom atom : molecule.atoms())
            atom.setIsAromatic(false);

        // set the new flags
        for (final IBond bond : bonds) {
            bond.setIsAromatic(true);
            bond.getBegin().setIsAromatic(true);
            bond.getEnd().setIsAromatic(true);
        }

        molecule.setFlag(ISAROMATIC, !bonds.isEmpty());

        return !bonds.isEmpty();
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
        return validSum(electronSum(cycle, contributions, subset));
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
    static boolean validSum(final int sum) {
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

    /** Replicates CDKHueckelAromaticityDetector. */
    private static final Aromaticity CDK_LEGACY = new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet());

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
     */
    public static Aromaticity cdkLegacy() {
        return CDK_LEGACY;
    }
}
