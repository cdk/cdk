/* Copyright (C) 2010  Rajarshi Guha <rajarshi.guha@gmail.com>
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
 */
package org.openscience.cdk.fragment;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods for fragmentation algorithms.
 * 
 * Most of these methods are specific to the fragmentation algorithms
 * in this package and so are protected. In general, these methods will
 * not be used by the rest of the API or by other users of the library.
 *
 * @author Rajarshi Guha
 * @cdk.module fragment
 */
public class FragmentUtils {

    /**
     * Non destructively split a molecule into two parts at the specified bond.
     *
     * Note that if a ring bond is specified, the resultant list will contain
     * teh opened ring twice.
     *
     * @param atomContainer The molecule to split
     * @param bond The bond to split at
     * @return A list containing the two parts of the molecule
     */
    protected static List<IAtomContainer> splitMolecule(IAtomContainer atomContainer, IBond bond) {
        List<IAtomContainer> ret = new ArrayList<IAtomContainer>();

        for (IAtom atom : bond.atoms()) {

            // later on we'll want to make sure that the fragment doesn't contain
            // the bond joining the current atom and the atom that is on the other side
            IAtom excludedAtom;
            if (atom.equals(bond.getBegin()))
                excludedAtom = bond.getEnd();
            else
                excludedAtom = bond.getBegin();

            List<IBond> part = new ArrayList<IBond>();
            part.add(bond);
            part = traverse(atomContainer, atom, part);

            // at this point we have a partion which contains the bond we
            // split. This partition should actually 2 partitions:
            // - one with the splitting bond
            // - one without the splitting bond
            // note that this will lead to repeated fragments when we  do this
            // with adjacent bonds, so when we gather all the fragments we need
            // to check for repeats
            IAtomContainer partContainer;
            partContainer = makeAtomContainer(atom, part, excludedAtom);

            // by checking for more than 2 atoms, we exclude single bond fragments
            // also if a fragment has the same number of atoms as the parent molecule,
            // it is the parent molecule, so we exclude it.
            if (partContainer.getAtomCount() > 2 && partContainer.getAtomCount() != atomContainer.getAtomCount())
                ret.add(partContainer);

            part.remove(0);
            partContainer = makeAtomContainer(atom, part, excludedAtom);
            if (partContainer.getAtomCount() > 2 && partContainer.getAtomCount() != atomContainer.getAtomCount())
                ret.add(partContainer);
        }
        return ret;
    }

    // Given a list of bonds representing a fragment obtained by splitting the molecule
    // at a bond, we need to create an IAtomContainer from it, containing *one* of the atoms
    // of the splitting bond. In addition, the new IAtomContainer should not contain the
    // splitting bond itself
    protected static IAtomContainer makeAtomContainer(IAtom atom, List<IBond> parts, IAtom excludedAtom) {
        IAtomContainer partContainer = atom.getBuilder().newInstance(IAtomContainer.class);
        partContainer.addAtom(atom);
        for (IBond aBond : parts) {
            for (IAtom bondedAtom : aBond.atoms()) {
                if (!bondedAtom.equals(excludedAtom) && !partContainer.contains(bondedAtom))
                    partContainer.addAtom(bondedAtom);
            }
            if (!aBond.contains(excludedAtom)) partContainer.addBond(aBond);
        }
        return partContainer;
    }

    protected static List<IBond> traverse(IAtomContainer atomContainer, IAtom atom, List<IBond> bondList) {
        List<IBond> connectedBonds = atomContainer.getConnectedBondsList(atom);
        for (IBond aBond : connectedBonds) {
            if (bondList.contains(aBond)) continue;
            bondList.add(aBond);
            IAtom nextAtom = aBond.getOther(atom);
            if (atomContainer.getConnectedAtomsCount(nextAtom) == 1) continue;
            traverse(atomContainer, nextAtom, bondList);
        }
        return bondList;
    }

}
