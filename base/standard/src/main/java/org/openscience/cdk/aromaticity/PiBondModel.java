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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.ringsearch.RingSearch;

import static org.openscience.cdk.interfaces.IBond.Order.DOUBLE;

/**
 * A simple aromatic model which only allows cyclic pi-bonds to contribute to an
 * aromatic system. Lone pairs are not considered and as such molecules like
 * furan and pyrrole are non-aromatic. This model is suitable for storing
 * aromaticity in the MDL/Mol2 file formats.
 *
 * @author John May
 * @cdk.module standard
 */
final class PiBondModel extends ElectronDonation {

    /**{@inheritDoc} */
    @Override
    int[] contribution(IAtomContainer container, RingSearch ringSearch) {

        int n = container.getAtomCount();
        int[] electrons = new int[n];
        int[] piBonds = new int[n];

        // count number of cyclic pi bonds
        for (IBond bond : container.bonds()) {
            int u = container.getAtomNumber(bond.getAtom(0));
            int v = container.getAtomNumber(bond.getAtom(1));

            if (bond.getOrder() == DOUBLE && ringSearch.cyclic(u, v)) {
                piBonds[u]++;
                piBonds[v]++;
            }
        }

        // any atom which is adjacent to one (and only one) cyclic
        // pi bond contributes 1 electron
        for (int i = 0; i < n; i++) {
            electrons[i] = piBonds[i] == 1 ? 1 : -1;
        }

        return electrons;
    }
}
