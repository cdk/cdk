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
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.CycleFinder;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.ringsearch.RingSearch;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.openscience.cdk.CDKConstants.ISAROMATIC;
import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;

/**
 * A configurable model to perceive aromatic systems. Aromaticity is useful as
 * both a chemical property indicating stronger stabilisation and a way to treat
 * different resonance forms as equivalent. The later has implications in
 * similarity, depiction and storage. Several simplified computation models have
 * evolved to fit-these use cases. General the models loosely follow <a
 * href="http://en.wikipedia.org/wiki/H%C3%BCckel's_rule">Hückel's rule</a> for
 * determining aromatic systems. Except for checking that atoms are Sp2
 * hybridised - planarity is not generally considered. As such, non-planar
 * molecules like cyclodeca-1,3,5,7,9-pentaene will be incorrectly identified as
 * aromatic. <p/>
 * Although there is no correct model there are models which are
 * better suited for a specific use. Although some models get more complicated
 * (e.g. considering tautomers) two reasons for differences are: 
 * <ul> 
 *     <li>the atoms allowed and how many electrons each contributes</li>
 *     <li>which rings/cycles are tested</li>
 * </ul>
 * <p/>
 * This implementation allows configuration of these properties via an 
 * {@link ElectronDonation} model and {@link CycleFinder}. The 
 * {@link ElectronDonation} models may have prerequisites which should be set
 * before invoking.
 *
 * <blockquote><pre>
 * // mimics the old CDKHuckelAromaticityDetector which uses the CDK atom types
 * Aromaticity aromaticity = new Aromaticity(ElectronDonation.cdkAtomTypes(false),
 *                                           Cycles.cdkAromaticSet());
 *
 * // apply our configured model to each molecule, the CDK model
 * // requires that atom types are perceived
 * for (IAtomContainer molecule : molecules) {
 *     AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
 *     aromaticity.apply(molecule);
 * }
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module standard
 * @see <a href="http://en.wikipedia.org/wiki/H%C3%BCckel's_rule">Hückel's
 *      rule</a>
 */
@TestClass("org.openscience.cdk.aromaticity.AromaticityTest")
public final class Aromaticity {

    /** Find how many electrons each atom contributes. */
    private final ElectronDonation model;

    /** The method to find cycles which will be tested for aromaticity. */
    private final CycleFinder cycles;

    /**
     * Create an aromaticity model using the specified electron donation {@code
     * model} which is tested on the {@code cycles}. The {@code model} defines
     * how many π-electrons each atom may contribute to an aromatic system. The
     * {@code cycles} defines the {@link CycleFinder} which is used to find
     * cycles in a molecule. The total electron donation from each atom in each
     * cycle is counted and checked. If the electron contribution is equal to
     * {@code 4n + 2} for a {@code n >= 0} then the cycle is considered
     * aromatic. <p/> Changing the electron contribution model or which cycles
     * are tested affects which atoms/bonds are found to be aromatic. There are
     * several {@link ElectronDonation} models and {@link
     * org.openscience.cdk.graph.Cycles} available. A good choice for the cycles
     * is to use {@link org.openscience.cdk.graph.Cycles#all()} falling back to
     * {@link org.openscience.cdk.graph.Cycles#mcb()}. Using all cycles is very
     * fast but may produce an exponential number of cycles. As such it is not
     * feasible for complex fused systems in which case an exception is thrown.
     * In such cases the aromaticity can either be skipped or a simpler
     * polynomial cycle set {@link org.openscience.cdk.graph.Cycles#mcb()}
     * used.
     *
     *
     * <blockquote><pre>
     *
     * // mimics the CDKHuckelAromaticityDetector
     * Aromaticity aromaticity = new Aromaticity(ElectronDonation.cdkAtomTypes(false),
     *                                           Cycles.cdkAromaticSet());
     *
     * // mimics the DoubleBondAcceptingAromaticityDetector
     * Aromaticity aromaticity = new Aromaticity(ElectronDonation.cdkAtomTypes(true),
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
    public Aromaticity(ElectronDonation model,
                       CycleFinder      cycles) {
        this.model  = checkNotNull(model);
        this.cycles = checkNotNull(cycles);
    }

    /**
     * Find the bonds of a {@code molecule} which this model determined were
     * aromatic.
     *
     * <blockquote><pre>{@code
     * Aromaticity aromaticity = new Aromaticity(ElectronDonation.cdkAtomTypes(true),
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
    @TestMethod("benzene,furan,quinone")
    public Set<IBond> findBonds(IAtomContainer molecule) throws CDKException {

        // build graph data-structures for fast cycle perception
        final EdgeToBondMap bondMap = EdgeToBondMap.withSpaceFor(molecule);
        final int[][]       graph   = GraphUtil.toAdjList(molecule, bondMap);

        // initial ring/cycle search and get the contribution from each atom 
        final RingSearch ringSearch = new RingSearch(molecule, graph);
        final int[]      electrons  = model.contribution(molecule, ringSearch);

        final Set<IBond> bonds = Sets.newHashSetWithExpectedSize(molecule.getBondCount());

        // for each cycle if the electron sum is valid add the bonds of the 
        // cycle to the set or aromatic bonds
        for (final int[] cycle : cycles.find(molecule).paths()) {
            if (checkElectronSum(cycle, electrons)) {
                for (int i = 1; i < cycle.length; i++) {
                    bonds.add(bondMap.get(cycle[i], cycle[i-1]));
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
     * flags. <p/>
     *
     * <blockquote><pre>
     * Aromaticity aromaticity = new Aromaticity(ElectronDonation.cdkAtomTypes(true),
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
    @TestMethod("clearFlags_quinone")
    public boolean apply(IAtomContainer molecule) throws CDKException {

        Set<IBond> bonds = findBonds(molecule);

        // clear existing flags
        molecule.setFlag(ISAROMATIC, false);
        for (IBond bond : molecule.bonds())
            bond.setFlag(ISAROMATIC, false);
        for (IAtom atom : molecule.atoms())
            atom.setFlag(ISAROMATIC, false);

        // set the new flags
        for (final IBond bond : bonds) {
            bond.setFlag(ISAROMATIC, true);
            bond.getAtom(0).setFlag(ISAROMATIC, true);
            bond.getAtom(1).setFlag(ISAROMATIC, true);
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
    private static boolean checkElectronSum(final int[] cycle, final int[] contributions) {
        return validSum(electronSum(cycle, contributions));
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
    @TestMethod("electronSum,electronSum_negative")
    static int electronSum(final int[] cycle, final int[] contributions) {
        int sum = 0;
        for (int i = 1; i < cycle.length; i++) {
            if (contributions[cycle[i]] < 0)
                return 0;
            sum += contributions[cycle[i]];
        }
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
    @TestMethod("validSum")
    static boolean validSum(final int sum) {
        return (sum - 2) % 4 == 0;
    }
}
