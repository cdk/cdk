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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;

/**
 * Checks if a bond is matching between query and target molecules. A query bond
 * ({@link IQueryBond}) is matched by its predicate. Otherwise, when bond matching is
 * enabled, both bonds must be aromatic, or both non-aromatic with the same order, as in
 * {@link org.openscience.cdk.isomorphism.BondMatcher#forStrictOrder()}; when it is
 * disabled any bond matches.
 *
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated This class is part of SMSD and either duplicates functionality elsewhere in the CDK or provides public
 *             access to internal implementation details. SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class DefaultVFBondMatcher implements VFBondMatcher {

    static final long  serialVersionUID = -7861469841127328812L;
    private IBond      queryBond        = null;
    private boolean    shouldMatchBonds;
    private IQueryBond smartQueryBond   = null;

    /**
     * Bond type flag
     */
    /**
     * Constructor
     */
    public DefaultVFBondMatcher() {
        this.queryBond = null;
        shouldMatchBonds = false;
    }

    /**
     * Creates a matcher for a bond of the query molecule. If the bond is an
     * {@link IQueryBond} it is matched by its predicate and {@code shouldMatchBonds}
     * has no effect.
     *
     * @param queryMol query molecule (not used)
     * @param queryBond query bond
     * @param shouldMatchBonds whether bond order and aromaticity must match
     */
    public DefaultVFBondMatcher(IAtomContainer queryMol, IBond queryBond, boolean shouldMatchBonds) {
        super();
        if (queryBond instanceof IQueryBond) {
            this.smartQueryBond = (IQueryBond) queryBond;
        } else {
            this.queryBond = queryBond;
        }
        setBondMatchFlag(shouldMatchBonds);
    }

    /**
     * Constructor
     * @param queryBond query bond
     */
    public DefaultVFBondMatcher(IQueryBond queryBond) {
        super();
        this.smartQueryBond = queryBond;
    }

    /** {@inheritDoc}
     *
     * @param targetConatiner target container
     * @param targetBond target bond
     * @return true if bonds match
     */
    @Override
    public boolean matches(TargetProperties targetConatiner, IBond targetBond) {
        if (this.smartQueryBond != null) {
            return smartQueryBond.matches(targetBond);
        }
        return !isBondMatchFlag() || isBondTypeMatch(targetBond);
    }

    /**
     * Return true if a bond is matched between query and target
     * @param targetBond
     * @return
     */
    private boolean isBondTypeMatch(IBond targetBond) {
        IBond.Order queryOrder = queryBond.getOrder();
        IBond.Order targetOrder = targetBond.getOrder();
        if ((queryBond.getFlag(IChemObject.AROMATIC) == targetBond.getFlag(IChemObject.AROMATIC))
                && (queryOrder == targetOrder)) {
            return true;
        } else if (queryBond.getFlag(IChemObject.AROMATIC) && targetBond.getFlag(IChemObject.AROMATIC)) {
            return true;
        }
        return false;
    }

    /**
     * @return the shouldMatchBonds
     */
    public boolean isBondMatchFlag() {
        return shouldMatchBonds;
    }

    /**
     * @param shouldMatchBonds the shouldMatchBonds to set
     */
    public final void setBondMatchFlag(boolean shouldMatchBonds) {
        this.shouldMatchBonds = shouldMatchBonds;
    }
}
