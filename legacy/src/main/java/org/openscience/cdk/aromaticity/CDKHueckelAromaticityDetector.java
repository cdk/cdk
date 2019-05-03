/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.aromaticity;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This aromaticity detector detects the aromaticity based on the H&uuml;ckel
 * 4n+2 pi-electrons rule applied to isolated ring systems. It assumes
 * CDK atom types to be perceived with the {@link CDKAtomTypeMatcher} or with
 * any compatible class. For example:
 * <pre>
 * Molecule molecule = MoleculeFactory.makePyridineOxide();
 * AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
 * Aromaticity.cdkLegacy().apply(molecule);
 * </pre>
 *
 * <p>Important to note is that this aromaticity detector does not allow aromatic
 * ring atoms to have double bonds pointing outwards from the ring, and thus not
 * having pi electrons shared with other ring atoms. If such double bonds are not
 * important, have a look at the {@link DoubleBondAcceptingAromaticityDetector}.
 *
 * @author         egonw
 * @cdk.module     standard
 * @cdk.githash
 * @cdk.created    2007-10-05
 *
 * @see org.openscience.cdk.CDKConstants
 * @see DoubleBondAcceptingAromaticityDetector
 * @deprecated use {@link Aromaticity} with the {@link ElectronDonation#cdk()} model
 */
@Deprecated
public class CDKHueckelAromaticityDetector {

    private static AtomTypeFactory factory = null;

    public static boolean detectAromaticity(IAtomContainer atomContainer) throws CDKException {
        SpanningTree spanningTree = new SpanningTree(atomContainer);
        IAtomContainer ringSystems = spanningTree.getCyclicFragmentsContainer();
        if (ringSystems.getAtomCount() == 0) {
            // If there are no rings, then there cannot be any aromaticity
            return false;
        }

        // FIXME: should not really mark them here
        Iterator<IAtom> atoms = ringSystems.atoms().iterator();
        while (atoms.hasNext())
            atoms.next().setFlag(CDKConstants.ISINRING, true);
        Iterator<IBond> bonds = ringSystems.bonds().iterator();
        while (bonds.hasNext())
            bonds.next().setFlag(CDKConstants.ISINRING, true);

        // disregard all atoms we know that cannot be aromatic anyway
        Set<IAtom> disregard = new HashSet<IAtom>();
        for (IAtom atom : ringSystems.atoms())
            if (!atomIsPotentiallyAromatic(atom)) disregard.add(atom);

        for (IAtom atom : disregard)
            ringSystems.removeAtom(atom);

        boolean foundSomeAromaticity = false;
        Iterator<IAtomContainer> isolatedRingSystems = ConnectivityChecker.partitionIntoMolecules(ringSystems)
                .atomContainers().iterator();
        while (isolatedRingSystems.hasNext()) {
            IAtomContainer isolatedSystem = isolatedRingSystems.next();
            IRingSet singleRings = Cycles.sssr(isolatedSystem).toRingSet();
            Iterator<IAtomContainer> singleRingsIterator = singleRings.atomContainers().iterator();
            int maxRingSize = 20;
            boolean atLeastOneRingIsSprouted = false;
            boolean allRingsAreAromatic = true;
            // test single rings in SSSR
            while (singleRingsIterator.hasNext()) {
                IAtomContainer singleRing = singleRingsIterator.next();
                if (singleRing.getAtomCount() > maxRingSize) maxRingSize = singleRing.getAtomCount();
                if (isRingSystemSproutedWithNonRingDoubleBonds(atomContainer, singleRing)) {
                    //					OK, this ring is not aromatic
                    atLeastOneRingIsSprouted = true;
                    allRingsAreAromatic = false;
                } else {
                    // possibly aromatic
                    boolean ringIsAromatic = isHueckelValid(singleRing);
                    foundSomeAromaticity |= ringIsAromatic;
                    allRingsAreAromatic &= ringIsAromatic;
                    if (ringIsAromatic) markRingAtomsAndBondsAromatic(singleRing);
                }
            }
            // OK, what about the one larger ring (if no aromaticity found in SSSR)?
            if (!allRingsAreAromatic && !atLeastOneRingIsSprouted && singleRings.getAtomContainerCount() <= 3) {
                // every ring system consisting of more than two rings is too difficult
                Iterator<IAtomContainer> allRingsIterator = new AllRingsFinder()
                        .findAllRingsInIsolatedRingSystem(isolatedSystem).atomContainers().iterator();
                while (allRingsIterator.hasNext()) {
                    // there should be exactly three rings, of which only one has a size larger
                    // than the two previous ones
                    IAtomContainer ring = allRingsIterator.next();
                    if (ring.getAtomCount() <= maxRingSize) {
                        // possibly aromatic
                        boolean ringIsAromatic = isHueckelValid(ring);
                        foundSomeAromaticity |= ringIsAromatic;
                        if (ringIsAromatic) markRingAtomsAndBondsAromatic(ring);
                    }
                }
            }
        }

        return foundSomeAromaticity;
    }

    /**
     * Tests if the electron count matches the H&uuml;ckel 4n+2 rule.
     */
    private static boolean isHueckelValid(IAtomContainer singleRing) throws CDKException {
        int electronCount = 0;
        for (IAtom ringAtom : singleRing.atoms()) {
            if (ringAtom.getHybridization() != CDKConstants.UNSET && (ringAtom.getHybridization() == Hybridization.SP2)
                    || ringAtom.getHybridization() == Hybridization.PLANAR3) {
                // for example, a carbon
                // note: the double bond is in the ring, that has been tested earlier
                // FIXME: this does assume bond orders to be resolved too, when detecting
                // sprouting double bonds
                if ("N.planar3".equals(ringAtom.getAtomTypeName())) {
                    electronCount += 2;
                } else if ("N.minus.planar3".equals(ringAtom.getAtomTypeName())) {
                    electronCount += 2;
                } else if ("N.amide".equals(ringAtom.getAtomTypeName())) {
                    electronCount += 2;
                } else if ("S.2".equals(ringAtom.getAtomTypeName())) {
                    electronCount += 2;
                } else if ("S.planar3".equals(ringAtom.getAtomTypeName())) {
                    electronCount += 2;
                } else if ("C.minus.planar".equals(ringAtom.getAtomTypeName())) {
                    electronCount += 2;
                } else if ("O.planar3".equals(ringAtom.getAtomTypeName())) {
                    electronCount += 2;
                } else if ("N.sp2.3".equals(ringAtom.getAtomTypeName())) {
                    electronCount += 1;
                } else {
                    if (factory == null) {
                        factory = AtomTypeFactory.getInstance("org/openscience/cdk/dict/data/cdk-atom-types.owl",
                                ringAtom.getBuilder());
                    }
                    IAtomType type = factory.getAtomType(ringAtom.getAtomTypeName());
                    Object property = type.getProperty(CDKConstants.PI_BOND_COUNT);
                    if (property != null && property instanceof Integer) {
                        electronCount += ((Integer) property).intValue();
                    }
                }
            } else if (ringAtom.getHybridization() != null && ringAtom.getHybridization() == Hybridization.SP3
                    && getLonePairCount(ringAtom) > 0) {
                // for example, a nitrogen or oxygen
                electronCount += 2;
            }
        }
        return (electronCount % 4 == 2) && (electronCount > 2);
    }

    private static boolean atomIsPotentiallyAromatic(IAtom atom) {
        if (atom.getHybridization() == Hybridization.SP2) return true;
        if (atom.getHybridization() == Hybridization.PLANAR3) return true;
        if (atom.getHybridization() == Hybridization.SP3 && getLonePairCount(atom) > 0) return true;
        return false;
    }

    /**
     * Determines if the isolatedRingSystem has attached double bonds, which are not part of the ring system itself,
     * and not part of any other ring system. Exceptions: a N.sp2.3 nitrogen with a double ring to an
     * oxygen outwards.
     */
    private static boolean isRingSystemSproutedWithNonRingDoubleBonds(IAtomContainer fullContainer,
            IAtomContainer isolatedRingSystem) {
        Iterator<IAtom> atoms = isolatedRingSystem.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            Iterator<IBond> neighborBonds = fullContainer.getConnectedBondsList(atom).iterator();
            while (neighborBonds.hasNext()) {
                IBond neighborBond = neighborBonds.next();
                if (!neighborBond.getFlag(CDKConstants.ISINRING)
                        && neighborBond.getOrder() == Order.DOUBLE
                        || neighborBond.getOrder() == Order.TRIPLE) {
                    if (!("N.sp2.3".equals(atom.getAtomTypeName()) && "O.sp2".equals(neighborBond
                                                                                         .getOther(atom).getAtomTypeName()))) return true;
                }
            }
        }
        return false;
    }

    private static int getLonePairCount(IAtom atom) {
        Integer count = (Integer) atom.getProperty(CDKConstants.LONE_PAIR_COUNT);
        if (count == null) {
            return 0;
        } else {
            return count;
        }
    }

    private static void markRingAtomsAndBondsAromatic(IAtomContainer container) {
        for (IAtom atom : container.atoms())
            atom.setFlag(CDKConstants.ISAROMATIC, true);
        for (IBond bond : container.bonds())
            bond.setFlag(CDKConstants.ISAROMATIC, true);
    }
}
