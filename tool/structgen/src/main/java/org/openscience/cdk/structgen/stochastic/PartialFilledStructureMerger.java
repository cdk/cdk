/* Copyright (C) 1997-2009  Christoph Steinbeck, Stefan Kuhn <shk3@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.structgen.stochastic;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * Randomly generates a single, connected, correctly bonded structure from
 * a number of fragments.
 * <p>Assign hydrogen counts to each heavy atom. The hydrogens should not be
 * in the atom pool but should be assigned implicitly to the heavy atoms in
 * order to reduce computational cost.
 *
 * @author     steinbeck
 * @cdk.created    2001-09-04
 * @cdk.module     structgen
 * @cdk.githash
 */
public class PartialFilledStructureMerger {

    private ILoggingTool logger = LoggingToolFactory.createLoggingTool(PartialFilledStructureMerger.class);

    SaturationChecker    satCheck;

    /**
     * Constructor for the PartialFilledStructureMerger object.
     */
    public PartialFilledStructureMerger() {
        satCheck = new SaturationChecker();
    }

    /**
     * Randomly generates a single, connected, correctly bonded structure from
     * a number of fragments.  IMPORTANT: The AtomContainers in the set must be
     * connected. If an AtomContainer is disconnected, no valid result will
     * be formed
     * @param atomContainers The fragments to generate for.
     * @return The newly formed structure.
     * @throws CDKException No valid result could be formed.
     */
    public IAtomContainer generate(IAtomContainerSet atomContainers) throws CDKException {
        int iteration = 0;
        boolean structureFound = false;
        do {
            iteration++;
            boolean bondFormed;
            do {
                bondFormed = false;
                for (IAtomContainer ac : atomContainers.atomContainers()) {
                    for (IAtom atom : AtomContainerManipulator.getAtomArray(ac)) {
                        if (!satCheck.isSaturated(atom, ac)) {
                            IAtom partner = getAnotherUnsaturatedNode(atom, atomContainers);
                            if (partner != null) {
                                IAtomContainer toadd = AtomContainerSetManipulator.getRelevantAtomContainer(
                                        atomContainers, partner);
                                double cmax1 = satCheck.getCurrentMaxBondOrder(atom, ac);
                                double cmax2 = satCheck.getCurrentMaxBondOrder(partner, toadd);
                                double max = Math.min(cmax1, cmax2);
                                double order = Math.min(Math.max(1.0, max), 3.0);//(double)Math.round(Math.random() * max)
                                logger.debug("cmax1, cmax2, max, order: " + cmax1 + ", " + cmax2 + ", " + max + ", "
                                        + order);
                                if (toadd != ac) {
                                    atomContainers.removeAtomContainer(toadd);
                                    ac.add(toadd);
                                }
                                ac.addBond(ac.getBuilder().newInstance(IBond.class, atom, partner,
                                        BondManipulator.createBondOrder(order)));
                                bondFormed = true;
                            }
                        }
                    }
                }
            } while (bondFormed);
            if (atomContainers.getAtomContainerCount() == 1
                    && satCheck.allSaturated(atomContainers.getAtomContainer(0))) {
                structureFound = true;
            }
        } while (!structureFound && iteration < 5);
        if (atomContainers.getAtomContainerCount() == 1 && satCheck.allSaturated(atomContainers.getAtomContainer(0))) {
            structureFound = true;
        }
        if (!structureFound)
            throw new CDKException("Could not combine the fragments to combine a valid, satured structure");
        return atomContainers.getAtomContainer(0);
    }

    /**
     *  Gets a randomly selected unsaturated atom from the set. If there are any, it will be from another
     *  container than exclusionAtom.
     *
     * @return  The unsaturated atom.
     */
    private IAtom getAnotherUnsaturatedNode(IAtom exclusionAtom, IAtomContainerSet atomContainers) throws CDKException {
        IAtom atom;

        for (IAtomContainer ac : atomContainers.atomContainers()) {
            if (!ac.contains(exclusionAtom)) {
                int next = 0;//(int) (Math.random() * ac.getAtomCount());
                for (int f = next; f < ac.getAtomCount(); f++) {
                    atom = ac.getAtom(f);
                    if (!satCheck.isSaturated(atom, ac) && exclusionAtom != atom
                            && !ac.getConnectedAtomsList(exclusionAtom).contains(atom)) {
                        return atom;
                    }
                }
            }
        }
        for (IAtomContainer ac : atomContainers.atomContainers()) {
            int next = ac.getAtomCount();//(int) (Math.random() * ac.getAtomCount());
            for (int f = 0; f < next; f++) {
                atom = ac.getAtom(f);
                if (!satCheck.isSaturated(atom, ac) && exclusionAtom != atom
                        && !ac.getConnectedAtomsList(exclusionAtom).contains(atom)) {
                    return atom;
                }
            }
        }
        return null;
    }

}
