/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) Project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.SetOfAtomContainers;

/**
 * Class that provides methods to give unique IDs to ChemObjects.
 *
 * @author   Egon Willighagen
 * @created  2003-04-01
 *
 * @keyword atom id, creation
 */
public class IDCreator {

    /**
     * Labels the Atom's and Bond's in the AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML. It will not set an id for the AtomContainer.
     *
     * @see createAtomContainerAndAtomAndBondIDs(SetOfAtomContainers)
     */
    public static void createAtomAndBondIDs(AtomContainer container) {
        IDCreator.createAtomAndBondIDs(container, 0, 0);
    }

    /**
     * Labels the Atom's and Bond's in the AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML. It will not set an id for the AtomContainer.
     *
     * <p>An offset can be used to start numbering at, for example, a3 instead
     * of a1 using an offset = 2.
     *
     * @param atomOffset  Lowest ID number to be used for the Atoms
     * @param bondOffset  Lowest ID number to be used for the Bonds
     *
     * @see createAtomContainerAndAtomAndBondIDs(SetOfAtomContainers)
     */
    public static void createAtomAndBondIDs(AtomContainer container,
                                            int atomOffset, int bondOffset) {
        Atom[] atoms = container.getAtoms();
        for (int i=0; i<atoms.length; i++) {
            atoms[i].setID("a" + (i+1+atomOffset));
        }
        Bond[] bonds = container.getBonds();
        for (int i=0; i<bonds.length; i++) {
            bonds[i].setID("b" + (i+1+bondOffset));
        }
    }

    /**
     * Labels the Atom's and Bond's in each AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML. It will also set id's for all AtomContainers, naming
     * them m1, m2, etc.
     * It will not the SetOfAtomContainers itself.
     *
     * @see createAtomAndBondIDs(SetOfAtomContainers)
     */
    public static void createAtomContainerAndAtomAndBondIDs(SetOfAtomContainers containerSet) {
        IDCreator.createAtomContainerAndAtomAndBondIDs(containerSet, 0, 0, 0);
    }

    /**
     * Labels the Atom's and Bond's in each AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML. It will also set id's for all AtomContainers, naming
     * them m1, m2, etc.
     * It will not the SetOfAtomContainers itself.
     *
     * @param containerOffset  Lowest ID number to be used for the AtomContainers
     * @param atomOffset  Lowest ID number to be used for the Atoms
     * @param bondOffset  Lowest ID number to be used for the Bonds
     *
     * @see createAtomAndBondIDs(SetOfAtomContainers)
     */
    public static void createAtomContainerAndAtomAndBondIDs(SetOfAtomContainers containerSet,
                           int containerOffset, int atomOffset, int bondOffset) {
        AtomContainer[] containers = containerSet.getAtomContainers();
        int atomCount = atomOffset;
        int bondCount = bondOffset;
        for (int i=0; i<containers.length; i++) {
            AtomContainer container = containers[i];
            container.setID("m" + (i+1+containerOffset));
            IDCreator.createAtomAndBondIDs(container, atomCount, bondCount);
            atomCount += container.getAtomCount();
            bondCount += container.getBondCount();
        }
    }
}
