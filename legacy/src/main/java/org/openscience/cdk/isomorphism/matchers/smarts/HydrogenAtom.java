/* Copyright (C) 2004-2007  Egon Willighagen <egonw@users.sf.net>
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

import java.util.List;

/**
 * This matches Hydrogen atoms.
 *
 * @cdk.module smarts
 * @cdk.githash
 * @cdk.keyword SMARTS
 */
@Deprecated
public class HydrogenAtom extends SMARTSAtom {

    /** Creates a new instance. */
    public HydrogenAtom(IChemObjectBuilder builder) {
        super(builder);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org
     * .openscience.cdk.interfaces.IAtom)
     */
    @Override
    public boolean matches(IAtom atom) {
        if (!atom.getSymbol().equals("H")) {
            return false;
        }

        if (atom.getFormalCharge() == 1) { // proton matches
            return true;
        }

        // hydrogens connected to other hydrogens, e.g., molecular hydrogen
        List<IAtom> list = invariants(atom).target().getConnectedAtomsList(atom);
        for (IAtom connAtom : list) {
            if (connAtom.getSymbol().equals("H")) {
                return true;
            }
        }

        // hydrogens connected to other than one other atom, e.g., bridging hydrogens
        if (invariants(atom).degree() > 1) {
            return true;
        }

        //isotopic hydrogen specifications, e.g. deuterium [2H] or tritium etc
        if (atom.getMassNumber() != null) {
            if (getMassNumber().intValue() == atom.getMassNumber().intValue()) return true;
        } else {
            // target atom is [H], so make sure query atom has mass number = 1
            if (getMassNumber() == 1) return true;
        }

        return false;
    }
}
