/*
 * Copyright (C) 2018 NextMove Software
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk.isomorphism;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.*;

import java.util.Collections;

import static org.openscience.cdk.isomorphism.matchers.Expr.Type.*;

/**
 * The depth-first (DF) backtracking sub-structure matching algorithm so named
 * because it matches the molecule in a depth-first manner (bond by bond). The
 * algorithm is a simple but elegant backtracking search iterating over the
 * bonds of a query. Like the popular VF2 the algorithm, it uses linear memory
 * but unlike VF2 bonded atoms are selected from the neighbor lists of already
 * mapped atoms.
 * <br><br>
 * In practice VF2 take O(N<sup>2</sup>) to match a linear chain against it's
 * self whilst this algorithm is O(N).
 * <br><br>
 * Usage:
 * <pre>{@code
 * DfPattern ptrn = DfPattern.findSubstructure(query);
 *
 * // has match?
 * if (ptrn.matches(mol)) {
 *
 * }
 *
 * // get lazy mapping iterator
 * Mappings mappings = ptrn.matchAll(mol);
 * for (int[] amap : mappings) {
 *
 * }
 *
 * // test if pattern matches at a given atom
 * for (IAtom atom : mol.atoms()) {
 *   if (ptrn.matchesRoot(atom)) {
 *
 *   }
 * }
 * }</pre>
 * <b>References</b>
 * <ul>
 *     <li>{@cdk.cite Ray57}</li>
 *     <li>{@cdk.cite Ullmann76}</li>
 *     <li>{@cdk.cite Cordella04}</li>
 *     <li>{@cdk.cite Jeliazkova18}</li>
 * </ul>
 *
 * @author John Mayfield
 * @see DfPattern
 * @see Mappings
 */
public class DfPattern extends Pattern {

    private final IAtomContainer src;
    private final IAtomContainer query;
    private final DfState state;

    private DfPattern(IAtomContainer src, IAtomContainer query) {
        this.src = src;
        this.query = query;
        determineFilters(query);
        state = new DfState(query);
    }

    private static void checkCompatibleAPI(IAtom atom) {
        if (atom.getContainer() == null) {
            throw new IllegalArgumentException(
                    "This API can only be used with the option " +
                            "CdkUseLegacyAtomContainer=false (default). The atoms in " +
                            "the molecule provided do not know about their parent " +
                            "molecule"
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] match(IAtomContainer target) {
        return matchAll(target).first();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(IAtomContainer target) {
        return matchAll(target).atLeast(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mappings matchAll(IAtomContainer mol) {
        if (mol.getAtomCount() < query.getAtomCount())
            return new Mappings(src, mol, Collections.<int[]>emptySet());
        if (mol.getAtomCount() > 0)
            checkCompatibleAPI(mol.getAtom(0));
        DfState local = new DfState(state);
        local.setMol(mol);
        return filter(new Mappings(src, mol, local), query, mol);
    }

    /**
     * Match the pattern at the provided root.
     *
     * @param root the root atom of the molecule
     * @return mappings
     * @see Mappings
     */
    Mappings matchRoot(IAtom root) {
        checkCompatibleAPI(root);
        IAtomContainer mol = root.getContainer();
        if (query.getAtomCount() > 0 && ((IQueryAtom) query.getAtom(0)).matches(root)) {
            DfState local = new DfState(state);
            local.setRoot(root);
            return filter(new Mappings(query, mol, local), query, mol);
        } else {
            return new Mappings(query, mol, Collections.<int[]>emptySet());
        }
    }

    /**
     * Test whether the pattern matches at the provided atom.
     *
     * @param root the root atom of the molecule
     * @return the pattern matches
     */
    public boolean matchesRoot(IAtom root) {
        return matchRoot(root).atLeast(1);
    }

    /**
     * Create a pattern which can be used to find molecules which contain the
     * {@code query} structure. If a 'real' molecule is provided is is converted
     * with {@link QueryAtomContainer#create(IAtomContainer, Expr.Type...)}
     * matching elements, aromaticity status, and bond orders.
     *
     * @param query the substructure to find
     * @return a pattern for finding the {@code query}
     * @see QueryAtomContainer#create(IAtomContainer, Expr.Type...)
     */
    public static DfPattern findSubstructure(IAtomContainer query) {
        // if one or more atoms/bonds is not a query atom/bond we need to convert
        // out input to a query molecule using some sensible defaults. Note any existing
        // query atom/bonds will be copied
        if (!isCompleteQuery(query)) {
            return new DfPattern(query,
                                 QueryAtomContainer.create(query,
                                                           ALIPHATIC_ELEMENT,
                                                           AROMATIC_ELEMENT,
                                                           SINGLE_OR_AROMATIC,
                                                           ALIPHATIC_ORDER,
                                                           STEREOCHEMISTRY));
        } else {
            return new DfPattern(query, query);
        }
    }

    private static boolean isCompleteQuery(IAtomContainer query) {
        for (IAtom atom : query.atoms())
            if (!(atom instanceof IQueryAtom))
                return false;
        for (IBond bond : query.bonds())
            if (!(bond instanceof IQueryBond))
                return false;
        return true;
    }
}
