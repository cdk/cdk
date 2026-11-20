/* Copyright (c) 2026 The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.smarts;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Determine which elements a molecule must contain in order to be matched by a
 * SMARTS query. This is a <em>necessary</em> condition and not a sufficient
 * one: a molecule that lacks one of the returned elements can never match the
 * query, a molecule that has them all may or may not. That makes it suitable
 * as a cheap pre-filter before running a (much more expensive) substructure
 * match.
 *
 * <p>The SMARTS is read with the normal {@link Smarts} parser and the
 * resulting {@link Expr} trees of the query atoms are analysed, the rules are
 * deliberately conservative:</p>
 * <ul>
 *   <li>An element primitive ({@code C}, {@code c}, {@code Cl}, {@code [#6]},
 *   {@code [13C]}, {@code [H]}...) requires that element.</li>
 *   <li>{@code AND} (implicit, {@code &} or {@code ;}) requires everything
 *   either side requires.</li>
 *   <li>{@code OR} ({@code ,}) only requires what <em>both</em> sides require:
 *   {@code [C,c]} requires carbon but {@code [C,N]} requires neither.</li>
 *   <li>{@code NOT} ({@code !}) requires nothing, in particular
 *   {@code [C;!$(C=O)]} requires carbon but not oxygen.</li>
 *   <li>Recursive SMARTS ({@code $(...)}) require everything required by any
 *   atom in the recursive query, when it is not negated or part of an
 *   {@code OR} as above.</li>
 *   <li>Everything else ({@code *}, {@code a}, {@code A}, ring, degree,
 *   charge, hydrogen count... primitives) requires no particular element.</li>
 * </ul>
 *
 * <p>Elements are reported by atomic number and are those of <em>atoms</em>:
 * {@code [H]} requires an explicit hydrogen atom, and implicit hydrogens do
 * not count. The set is a presence test, how many atoms of each element are
 * needed is not reported.</p>
 *
 * <pre>{@code
 * SmartsElements.requiredElements("CC(=O)O");          // {6, 8}
 * SmartsElements.requiredElements("[C,c]N");           // {6, 7}
 * SmartsElements.requiredElements("[Cl,Br,I]");        // {}
 * SmartsElements.requiredElements("[C;!$(C=O)]");      // {6}
 * }</pre>
 */
public final class SmartsElements {

    // cannot be instantiated as object
    private SmartsElements() {}

    /**
     * Elements, as atomic numbers in ascending order, that a molecule must
     * contain for the SMARTS to possibly match it.
     *
     * @param smarts the SMARTS string
     * @return the required atomic numbers (read-only), possibly empty
     * @throws IllegalArgumentException the SMARTS was null or could not be
     *                                  parsed
     */
    public static SortedSet<Integer> requiredElements(String smarts) {
        if (smarts == null)
            throw new IllegalArgumentException("No SMARTS provided");
        IAtomContainer query = new QueryAtomContainer(null);
        SmartsResult result = Smarts.parseToResult(query, smarts);
        if (!result.ok())
            throw new IllegalArgumentException("Could not parse SMARTS: " +
                                               smarts + "\n" +
                                               result.getMessage() + "\n" +
                                               result.displayErrorLocation());
        return Collections.unmodifiableSortedSet(new TreeSet<>(required(query)));
    }

    // every atom of the query must be matched, so we need the union
    private static Set<Integer> required(IAtomContainer query) {
        Set<Integer> elements = new HashSet<>();
        for (IAtom atom : query.atoms())
            elements.addAll(required(expression(atom)));
        return elements;
    }

    private static Expr expression(IAtom atom) {
        return ((QueryAtom) AtomRef.deref(atom)).getExpression();
    }

    private static Set<Integer> required(Expr expr) {
        switch (expr.type()) {
            case ELEMENT:
            case ALIPHATIC_ELEMENT:
            case AROMATIC_ELEMENT:
                return Collections.singleton(expr.value());
            case AND: {
                Set<Integer> res = new HashSet<>(required(expr.left()));
                res.addAll(required(expr.right()));
                return res;
            }
            case OR: {
                // one side or the other matches, only what both need is certain
                Set<Integer> res = new HashSet<>(required(expr.left()));
                res.retainAll(required(expr.right()));
                return res;
            }
            case RECURSIVE:
                return required(expr.subquery());
            default:
                // NOT and every primitive that doesn't pin down an element
                return Collections.emptySet();
        }
    }
}
