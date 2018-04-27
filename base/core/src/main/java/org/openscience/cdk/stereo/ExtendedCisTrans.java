/*
 * Copyright (c) 2018 John Mayfield <jwmay@users.sf.net>
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

package org.openscience.cdk.stereo;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;

import java.util.List;

import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;

/**
 * Extended Cis/Trans double bond configuration. This stereo element is
 * used to represent configurations of odd numbers of double bonds:
 * <pre>
 *                  C
 *                 /
 *   C = C = C = C
 *  /
 * C
 * </pre>
 * @see ExtendedTetrahedral
 * @author John Mayfield
 */
public final class ExtendedCisTrans
    extends AbstractStereo<IBond,IBond>  {

    public ExtendedCisTrans(IBond focus, IBond[] peripherals, int config) {
        super(focus, peripherals, CU | (CFG_MASK & config));
    }

    // internal, find a neighbor connected to 'atom' that is not 'other'
    private static IAtom getOtherAtom(IAtomContainer mol, IAtom atom, IAtom other) {
        List<IBond> bonds = mol.getConnectedBondsList(atom);
        if (bonds.size() != 2)
            return null;
        if (bonds.get(0).contains(other))
            return bonds.get(1).getOrder() == IBond.Order.DOUBLE
                   ? bonds.get(1).getOther(atom) : null;
        return bonds.get(0).getOrder() == IBond.Order.DOUBLE
                    ? bonds.get(0).getOther(atom) : null;
    }

    /**
     * Helper method to locate two terminal atoms in a container for this
     * extended Cis/Trans element. The atoms are ordered such that the first
     * atom is closer to first carrier.
     *
     * @param container structure representation
     * @return the terminal atoms (ordered)
     */
    public static IAtom[] findTerminalAtoms(IAtomContainer container, IBond focus) {
        IAtom a = focus.getBegin();
        IAtom b = focus.getEnd();
        IAtom aPrev = a, bPrev = b;
        IAtom aNext, bNext;
        aNext = getOtherAtom(container, a, b);
        bNext = getOtherAtom(container, b, a);
        while (aNext != null && bNext != null) {
            IAtom tmp = getOtherAtom(container, aNext, aPrev);
            aPrev = aNext;
            aNext = tmp;
            tmp = getOtherAtom(container, bNext, bPrev);
            bPrev = bNext;
            bNext = tmp;
        }
        if (aPrev != null && bPrev != null)
            return new IAtom[]{aPrev, bPrev};
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IStereoElement<IBond, IBond> create(IBond focus, List<IBond> carriers,
                                                  int cfg) {
        return new ExtendedCisTrans(focus, carriers.toArray(new IBond[2]), cfg);
    }
}
