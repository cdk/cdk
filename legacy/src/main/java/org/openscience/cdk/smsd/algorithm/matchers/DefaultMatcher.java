/* Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * MX Cheminformatics Tools for Java
 *
 * Copyright (c) 2007-2009 Metamolecular, LLC
 *
 * http://metamolecular.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.openscience.cdk.smsd.algorithm.matchers;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

/**
 * Provides shared atom and bond compatibility helpers for SMSD matchers.
 * Ordinary atoms use the CDK element matcher and ordinary bonds use strict
 * order/aromaticity matching. Source query predicates are directional and
 * remain authoritative through reference wrappers.
 *
 * <p>These helpers do not prepare molecules or apply mapping-wide
 * stereochemistry, component-group or reaction-map filters.</p>
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated This class is part of SMSD and either duplicates functionality elsewhere in the CDK or provides public
 *             access to internal implementation details. SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class DefaultMatcher {

    /**
     * Constructs the legacy matcher helper facade.
     */
    public DefaultMatcher() {
    }

    private static final org.openscience.cdk.isomorphism.AtomMatcher ELEMENT_MATCHER =
            org.openscience.cdk.isomorphism.AtomMatcher.forElement();
    private static final org.openscience.cdk.isomorphism.BondMatcher ORDER_MATCHER =
            org.openscience.cdk.isomorphism.BondMatcher.forStrictOrder();

    /** Query predicates are directional; ordinary atoms match by element. */
    static boolean matchesAtom(IAtom queryAtom, IAtom targetAtom) {
        queryAtom = AtomRef.deref(queryAtom);
        return queryAtom instanceof IQueryAtom
                ? ((IQueryAtom) queryAtom).matches(targetAtom)
                : ELEMENT_MATCHER.matches(queryAtom, targetAtom);
    }

    /** Explicit predicates also apply when ordinary bond orders are ignored. */
    static boolean matchesBond(IBond queryBond, IBond targetBond, boolean matchBonds) {
        queryBond = BondRef.deref(queryBond);
        return queryBond instanceof IQueryBond
                ? ((IQueryBond) queryBond).matches(targetBond)
                : !matchBonds || ORDER_MATCHER.matches(queryBond, targetBond);
    }

    /**
     * Delegates target-bond compatibility to the supplied matcher.
     * @param bondMatcher configured source bond matcher
     * @param ac2 target container passed to the matcher
     * @param bondA2 target bond to check
     * @param shouldMatchBonds legacy compatibility parameter; the matcher controls bond comparison
     * @return whether the configured matcher accepts the target bond
     * @throws NullPointerException if the matcher is null
     */
    public static boolean isBondMatch(BondMatcher bondMatcher, IAtomContainer ac2, IBond bondA2,
            boolean shouldMatchBonds) {
        return bondMatcher.matches(ac2, bondA2);
    }

    /**
     * Checks whether source endpoint matchers accept either orientation of a target bond.
     *
     * Seeded extensions must additionally verify the actual mapped orientation to preserve query direction.
     * @param atomMatcher1 directional matcher for the first source endpoint
     * @param atomMatcher2 directional matcher for the second source endpoint
     * @param ac2 target container passed to each matcher
     * @param bondA2 target bond whose endpoints are checked
     * @param shouldMatchBonds legacy compatibility parameter; each matcher controls its own checks
     * @return whether either target endpoint orientation is accepted by both matchers
     * @throws NullPointerException if a required matcher or target bond is null
     */
    public static boolean isAtomMatch(AtomMatcher atomMatcher1, AtomMatcher atomMatcher2, IAtomContainer ac2,
            IBond bondA2, boolean shouldMatchBonds) {
        return atomMatcher1.matches(ac2, bondA2.getBegin()) && atomMatcher2.matches(ac2, bondA2.getEnd())
                || atomMatcher1.matches(ac2, bondA2.getEnd()) && atomMatcher2.matches(ac2, bondA2.getBegin());
    }

}
