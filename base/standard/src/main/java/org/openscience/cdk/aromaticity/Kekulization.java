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

package org.openscience.cdk.aromaticity;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.Matching;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.BitSet;

import static org.openscience.cdk.CDKConstants.ISAROMATIC;
import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;
import static org.openscience.cdk.interfaces.IBond.Order.DOUBLE;
import static org.openscience.cdk.interfaces.IBond.Order.SINGLE;
import static org.openscience.cdk.interfaces.IBond.Order.UNSET;

/**
 * Assign a Kekulé representation to the aromatic systems of a compound. Input
 * from some file-formats provides some bonds as aromatic / delocalised bond
 * types. This method localises the electrons and assigns single and double
 * bonds. Different atom and bond orderings may produce distinct but valid
 * Kekulé forms. Only bond orders are adjusted and any aromatic flags will
 * remain untouched.
 * 
 *
 * The procedure requires that all atoms have defined implicit hydrogens counts
 * and formal charges. If this information is not present it should be assigned
 * first. 
 *
 * For some inputs it may not be possible to assign a Kekulé form. In general
 * theses cases are rare but usually occur for one of two reasons.
 * 1) Missing / ambiguous implicit hydrogens, this is fundamental to determining the
 * Kekulé form and if guessed may be wrong. Some formats (e.g. molfile) can not
 * include the exact number of implicit hydrogens attached to atom whilst others
 * may omit it or optionally skip encoding. The typical example is found in the
 * example for 1H-pyrrole, a correct SMILES encoding should include the hydrogen
 * on the aromatic nitrogen '[nH]1cccc1' (not: 'n1cccc1').
 * 2) The aromaticity perception algorithm has allowed atoms with abnormal
 * valence. This usually happens when a non-convalent bond has be <i>upgraded</i>
 * to a sigma bond during format conversion. 
 *
 * @author John May
 * @cdk.keyword kekule
 * @cdk.keyword kekulize
 * @cdk.keyword dearomatize
 * @cdk.keyword aromatic
 * @cdk.keyword fix bond orders
 * @cdk.keyword deduce bond orders
 */
public final class Kekulization {

    /**
     * Assign a Kekulé representation to the aromatic systems of a compound.
     *
     * @param ac structural representation
     * @throws CDKException a Kekulé form could not be assigned
     */
    public static void kekulize(final IAtomContainer ac) throws CDKException {

        // storage of pairs of atoms that have pi-bonded
        final Matching matching = Matching.withCapacity(ac.getAtomCount());

        // exract data structures for efficient access
        final IAtom[] atoms = AtomContainerManipulator.getAtomArray(ac);
        final EdgeToBondMap bonds = EdgeToBondMap.withSpaceFor(ac);
        final int[][] graph = GraphUtil.toAdjList(ac, bonds);

        // determine which atoms are available to have a pi bond placed
        final BitSet available = available(graph, atoms, bonds);

        // attempt to find a perfect matching such that a pi bond is placed
        // next to each available atom. if not found the solution is ambiguous
        if (!matching.perfect(graph, available))
            throw new CDKException("Cannot assign Kekulé structure without randomly creating radicals.");

        // propegate bond order information from the matching
        for (final IBond bond : ac.bonds()) {
            if (bond.getOrder() == UNSET && bond.isAromatic()) bond.setOrder(SINGLE);
        }
        for (int v = available.nextSetBit(0); v >= 0; v = available.nextSetBit(v + 1)) {
            final int w = matching.other(v);
            final IBond bond = bonds.get(v, w);

            // sanity check, something wrong if this happens
            if (bond.getOrder().numeric() > 1)
                throw new CDKException(
                        "Cannot assign Kekulé structure, non-sigma bond order has already been assigned?");

            bond.setOrder(IBond.Order.DOUBLE);
            available.clear(w);
        }
    }

    /**
     * Determine the set of atoms that are available to have a double-bond.
     *
     * @param graph adjacent list representation
     * @param atoms array of atoms
     * @param bonds map of atom indices to bonds
     * @return atoms that can require a double-bond
     */
    private static BitSet available(int[][] graph, IAtom[] atoms, EdgeToBondMap bonds) {

        final BitSet available = new BitSet();

        // for all atoms, select those that require a double-bond
        ATOMS: for (int i = 0; i < atoms.length; i++) {

            final IAtom atom = atoms[i];

            // preconditions
            if (atom.getAtomicNumber() == null)
                throw new IllegalArgumentException("atom " + (i + 1) + " had unset atomic number");
            if (atom.getFormalCharge() == null)
                throw new IllegalArgumentException("atom " + (i + 1) + " had unset formal charge");
            if (atom.getImplicitHydrogenCount() == null)
                throw new IllegalArgumentException("atom " + (i + 1) + " had unset implicit hydrogen count");

            if (!atom.getFlag(ISAROMATIC)) continue;

            // count preexisting pi-bonds, a higher bond order causes a skip
            int nPiBonds = 0;
            for (final int w : graph[i]) {
                IBond.Order order = bonds.get(i, w).getOrder();
                if (order == DOUBLE) {
                    nPiBonds++;
                } else if (order.numeric() > 2) {
                    continue ATOMS;
                }
            }

            // check if a pi bond can be assigned
            final int element = atom.getAtomicNumber();
            final int charge = atom.getFormalCharge();
            final int valence = graph[i].length + atom.getImplicitHydrogenCount() + nPiBonds;

            if (available(element, charge, valence)) {
                available.set(i);
            }
        }

        return available;
    }

    /**
     * Determine if the specified element with the provided charge and valance
     * requires a pi bond?
     *
     * @param element atomic number >= 0
     * @param charge  formal charge
     * @param valence bonded electrons
     * @return a double-bond is required
     */
    private static boolean available(final int element, final int charge, final int valence) {

        // higher atomic number elements aren't likely to be found but
        // we have them for rare corner cases (tellurium).
        // Germanium, Silicon, Tin and Antimony are a bit bonkers...
        switch (Elements.ofNumber(element)) {
            case Boron:
                if (charge == 0 && valence <= 2) return true;
                if (charge == -1 && valence <= 3) return true;
                break;
            case Carbon:
            case Silicon:
            case Germanium:
            case Tin:
                if (charge == 0 && valence <= 3) return true;
                break;
            case Nitrogen:
            case Phosphorus:
            case Arsenic:
            case Antimony:
                if (charge == 0) return valence <= 2 || valence == 4;
                if (charge == 1) return valence <= 3;
                break;
            case Oxygen:
            case Sulfur:
            case Selenium:
            case Tellurium:
                // valence of three or five are really only for sulphur but
                // are applied generally to all of group eight for simplicity
                if (charge == 0) return valence <= 1 || valence == 3 || valence == 5;
                if (charge == 1) return valence <= 2 || valence == 4;
                break;
        }

        return false;
    }
}
