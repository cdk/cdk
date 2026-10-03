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

import org.openscience.cdk.BondRef;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

/**
 * Matches ordinary bonds by strict order and aromaticity, or evaluates a
 * directional source bond query predicate. Two aromatic bonds match regardless
 * of assigned orders; aromatic and non-aromatic bonds remain distinct when
 * ordinary bond matching is enabled. Reference-wrapped query bonds retain
 * their predicates, including when ordinary bond matching is disabled.
 *
 * <p>Matching does not prepare aromaticity or apply mapping-wide stereo filters.
 * Target bonds retain the references and chemical properties needed by query
 * predicates. Mutable matcher instances are not thread-safe.</p>
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated This class is part of SMSD and either duplicates functionality elsewhere in the CDK or provides public
 *             access to internal implementation details. SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class DefaultBondMatcher implements BondMatcher {

    static final long serialVersionUID = -7861469841127328812L;
    private IBond queryBond;
    private boolean shouldMatchBonds;

    /**
     * Constructs a matcher with ordinary bond matching disabled.
     */
    public DefaultBondMatcher() {
    }

    /**
     * Constructs a matcher for an ordinary bond or an explicit query predicate.
     * @param queryMol legacy query container parameter, unused by this constructor
     * @param queryBond borrowed source bond or predicate, with reference wrappers resolved
     * @param shouldMatchBonds whether ordinary bonds must have compatible order and aromaticity
     */
    public DefaultBondMatcher(IAtomContainer queryMol, IBond queryBond, boolean shouldMatchBonds) {
        this.queryBond = BondRef.deref(queryBond);
        setBondMatchFlag(shouldMatchBonds);
    }

    /**
     * Constructs a matcher that evaluates a directional bond query predicate.
     * @param queryBond borrowed source bond predicate
     */
    public DefaultBondMatcher(IQueryBond queryBond) {
        this.queryBond = BondRef.deref(queryBond);
    }

    /**
     * Checks one target bond against the stored source bond.
     * @param targetContainer legacy target graph parameter, unused by this matcher
     * @param targetBond target bond with properties required by the source predicate
     * @return whether the predicate accepts the target, or the requested ordinary bond comparison succeeds
     * @throws NullPointerException if strict ordinary bond comparison receives a null source or target bond
     */
    @Override
    public boolean matches(IAtomContainer targetContainer, IBond targetBond) {
        return DefaultMatcher.matchesBond(queryBond, targetBond, shouldMatchBonds);
    }

    /**
     * Reports whether ordinary bond order and aromaticity are compared.
     * @return whether strict ordinary bond comparison is enabled
     */
    public boolean isBondMatchFlag() {
        return shouldMatchBonds;
    }

    /**
     * Enables or disables strict ordinary bond comparison.
     *
     * Explicit query bond predicates are evaluated regardless of this flag.
     * @param shouldMatchBonds whether to compare ordinary bond order and aromaticity
     */
    public final void setBondMatchFlag(boolean shouldMatchBonds) {
        this.shouldMatchBonds = shouldMatchBonds;
    }
}
