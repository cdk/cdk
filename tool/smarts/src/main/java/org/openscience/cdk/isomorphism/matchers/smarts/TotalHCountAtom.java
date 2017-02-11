/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *               2013       European Bioinformatics Institute
 *                         John May
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
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * SMARTS query atom for matching the total hydrogen count. This count is
 * specified in SMARTS using {@code H<NUMBER>}.
 *
 * @cdk.module  smarts
 * @cdk.keyword SMARTS
 * @cdk.githash
 */
public final class TotalHCountAtom extends SMARTSAtom {

    /** The total hydrogen count to match. */
    private final int totalHCount;

    public TotalHCountAtom(int totalHCount, IChemObjectBuilder builder) {
        super(builder);
        this.totalHCount = totalHCount;
    }

    /**
     * Check if the total hydrogen count of the {@code atom} is equal to the
     * query.
     *
     * @param atom the atom to match
     * @return the hydrogen count matches
     */
    @Override
    public boolean matches(final IAtom atom) {
        return invariants(atom).totalHydrogenCount() == totalHCount;
    }

    /**{@inheritDoc} */
    @Override
    public String toString() {
        return "H" + totalHCount;
    }
}
