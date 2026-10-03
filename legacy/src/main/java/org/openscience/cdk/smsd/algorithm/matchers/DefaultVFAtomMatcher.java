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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;

/**
 * Matches ordinary atoms by element or evaluates the source atom query predicate.
 * Reference-wrapped query atoms retain their predicates. Ordinary matching does
 * not compare atom aromaticity, charge, isotope, radicals or stereochemistry.
 *
 * <p>Symbol overrides and the optional maximum target-degree bound apply only
 * to ordinary atoms. Query predicates are directional and take precedence over
 * those settings. Callers supply the chemical properties and target adjacency
 * required by their predicates; no chemical preparation or mapping-wide stereo
 * filtering is performed. Mutable matcher instances are not thread-safe.</p>
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated This class is part of SMSD and either duplicates functionality elsewhere in the CDK or provides public
 *             access to internal implementation details. SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class DefaultVFAtomMatcher implements VFAtomMatcher {

    static final long  serialVersionUID = -7861469841127327812L;
    private int        maximumNeighbors = -1;
    private String     symbol;
    private IAtom      queryAtom;
    private boolean    symbolOverride;
    private boolean    shouldMatchBonds = false;

    /**
     * Reports whether a configured ordinary-atom degree bound should be applied.
     * @return the bond-match flag controlling optional degree checks
     */
    public boolean isBondMatchFlag() {
        return shouldMatchBonds;
    }

    /**
     * Enables or disables the optional ordinary-atom degree bound.
     * @param shouldMatchBonds whether to apply a configured degree bound; query predicates are unaffected
     */
    public final void setBondMatchFlag(boolean shouldMatchBonds) {
        this.shouldMatchBonds = shouldMatchBonds;
    }

    /**
     * Constructs an unconfigured matcher.
     *
     * Set a symbol before using this form; an unset symbol does not match ordinary atoms.
     */
    public DefaultVFAtomMatcher() {
    }

    /**
     * Constructs a directional matcher for the supplied query atom.
     * @param queryContainer legacy query container parameter, unused by this constructor
     * @param atom borrowed query atom or query predicate, with reference wrappers resolved
     * @param shouldMatchBonds whether to apply an explicitly configured ordinary-atom degree bound
     */
    public DefaultVFAtomMatcher(IAtomContainer queryContainer, IAtom atom, boolean shouldMatchBonds) {
        this.queryAtom = AtomRef.deref(atom);
        setBondMatchFlag(shouldMatchBonds);
    }

    /**
     * Constructs a matcher that evaluates a query atom predicate.
     * @param smartQueryAtom borrowed directional atom predicate
     * @param container legacy query container parameter, unused by this constructor
     */
    public DefaultVFAtomMatcher(IQueryAtom smartQueryAtom, IQueryAtomContainer container) {
        this(container, smartQueryAtom, false);
    }

    /**
     * Constructs a matcher with a derived maximum target-degree bound.
     *
     * An unset implicit hydrogen count contributes zero. Explicit query predicates ignore the derived bound.
     * @param queryContainer container used to count bonds connected to the template
     * @param template borrowed query atom or query predicate
     * @param blockedPositions positions subtracted from template implicit hydrogens plus connected bonds
     * @param shouldMatchBonds whether to apply the derived bound to ordinary atoms
     * @throws NullPointerException if the template or container required to compute the bound is null
     */
    public DefaultVFAtomMatcher(IAtomContainer queryContainer, IAtom template, int blockedPositions,
            boolean shouldMatchBonds) {
        this(queryContainer, template, shouldMatchBonds);
        this.maximumNeighbors = countImplicitHydrogens(template) + queryContainer.getConnectedBondsCount(template)
                - blockedPositions;
    }

    /**
     * Sets the optional maximum target-degree bound for ordinary atoms.
     *
     * Other values are used literally. The bound is applied only when the bond-match flag is enabled and the query atom is ordinary.
     * @param maximum maximum number of target bonds; -1 disables the bound
     */
    public void setMaximumNeighbors(int maximum) {
        this.maximumNeighbors = maximum;
    }

    /**
     * Overrides the element comparison with an ordinary-atom symbol comparison.
     *
     * Query atom predicates remain authoritative and ignore this override.
     * @param symbol case-sensitive target symbol to accept; null rejects ordinary atoms
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
        this.symbolOverride = true;
    }

    private boolean matchElement(IAtom atom) {
        if (symbolOverride || queryAtom == null) {
            return symbol != null && symbol.equals(atom.getSymbol());
        }
        return DefaultMatcher.matchesAtom(queryAtom, atom);
    }

    private boolean matchMaximumNeighbors(TargetProperties targetContainer, IAtom targetAtom) {
        if (maximumNeighbors == -1 || !isBondMatchFlag()) {
            return true;
        }

        int maximumTargetNeighbors = targetContainer.countNeighbors(targetAtom);
        return maximumTargetNeighbors <= maximumNeighbors;
    }

    private int countImplicitHydrogens(IAtom atom) {
        return (atom.getImplicitHydrogenCount() == null) ? 0 : atom.getImplicitHydrogenCount();
    }

    /**
     * Checks the query atom against one target atom.
     * @param targetContainer target graph used by the optional ordinary-atom degree check
     * @param targetAtom target atom retaining the adjacency and chemical properties needed by predicates
     * @return whether the directional predicate or ordinary element/symbol and degree checks accept the atom
     * @throws NullPointerException if ordinary element matching receives a null atom or an unset non-pseudo atomic number, or an enabled degree check receives a null target graph
     */
    @Override
    public boolean matches(TargetProperties targetContainer, IAtom targetAtom) {
        if (queryAtom instanceof IQueryAtom) {
            return DefaultMatcher.matchesAtom(queryAtom, targetAtom);
        }
        return matchElement(targetAtom) && matchMaximumNeighbors(targetContainer, targetAtom);
    }
}
