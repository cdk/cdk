/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.isomorphism.DfPattern;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

/**
 * This matches recursive smarts atoms.
 *
 * @cdk.module smarts
 * @cdk.githash
 * @cdk.keyword SMARTS
 */
@Deprecated
public final class RecursiveSmartsAtom extends SMARTSAtom {

    /** The IQueryAtomContainer created by parsing the recursive smarts */
    private final IQueryAtomContainer                  query;

    private final DfPattern ptrn;

    /**
     * Creates a new instance
     *
     * @param query
     */
    public RecursiveSmartsAtom(final IQueryAtomContainer query) {
        super(query.getBuilder());
        this.query = query;
        this.ptrn = DfPattern.findSubstructure(query);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org
     * .openscience.cdk.interfaces.IAtom)
     */
    @Override
    public boolean matches(IAtom atom) {
        if (!((IQueryAtom) query.getAtom(0)).matches(atom)) return false;
        if (query.getAtomCount() == 1) return true;
        return ptrn.matchesRoot(atom);
    }
}
