/* Copyright (C) 2007,2012  Egon Willighagen <egonw@users.sf.net>
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

import java.util.Iterator;

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
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.ringsearch.AllRingsFinder;

/**
 * This aromaticity detector detects the aromaticity based on the H&uuml;ckel
 * 4n+2 pi-electrons rule applied to isolated ring systems. It assumes
 * CDK atom types to be perceived with the {@link CDKAtomTypeMatcher} or with
 * any compatible class.
 *
 * <p>But unlike the {@link CDKHueckelAromaticityDetector}, this one accepts
 * ring atoms with double bonds pointing out of the ring.
 *
 * @author         egonw
 * @cdk.module     standard
 * @cdk.githash
 *
 * @see org.openscience.cdk.CDKConstants
 * @see CDKHueckelAromaticityDetector
 * @deprecated use {@link Aromaticity} with the {@link org.openscience.cdk.aromaticity.ElectronDonation#cdkAllowingExocyclic()} model
 */
@Deprecated
public class DoubleBondAcceptingAromaticityDetector {

    private static AtomTypeFactory factory = null;

    public static boolean detectAromaticity(IAtomContainer atomContainer) throws CDKException {
        SpanningTree spanningTree = new SpanningTree(atomContainer);
        IAtomContainer ringSystems = spanningTree.getCyclicFragmentsContainer();
        if (ringSystems.getAtomCount() == 0) {
            // If there are no rings, then there cannot be any aromaticity
            return false;
        }
        // disregard all atoms we know that cannot be aromatic anyway
        for (IAtom atom : ringSystems.atoms())
            if (!atomIsPotentiallyAromatic(atom)) ringSystems.removeAtom(atom);

        // FIXME: should not really mark them here
        Iterator<IAtom> atoms = ringSystems.atoms().iterator();
        while (atoms.hasNext())
            atoms.next().setFlag(CDKConstants.ISINRING, true);
        Iterator<IBond> bonds = ringSystems.bonds().iterator();
        while (bonds.hasNext())
            bonds.next().setFlag(CDKConstants.ISINRING, true);

        boolean foundSomeAromaticity = false;
        Iterator<IAtomContainer> isolatedRingSystems = ConnectivityChecker.partitionIntoMolecules(ringSystems)
                .atomContainers().iterator();
        while (isolatedRingSystems.hasNext()) {
            IAtomContainer isolatedSystem = isolatedRingSystems.next();
            IRingSet singleRings = Cycles.sssr(isolatedSystem).toRingSet();
            Iterator<IAtomContainer> singleRingsIterator = singleRings.atomContainers().iterator();
            int maxRingSize = 20;
            boolean allRingsAreAromatic = true;
            // test single rings in SSSR
            while (singleRingsIterator.hasNext()) {
                IAtomContainer singleRing = singleRingsIterator.next();
                if (singleRing.getAtomCount() > maxRingSize) maxRingSize = singleRing.getAtomCount();
                // possibly aromatic
                boolean ringIsAromatic = isHueckelValid(singleRing);
                foundSomeAromaticity |= ringIsAromatic;
                allRingsAreAromatic &= ringIsAromatic;
                if (ringIsAromatic) markRingAtomsAndBondsAromatic(singleRing);
            }
            // OK, what about the one larger ring (if no aromaticity found in SSSR)?
            if (!allRingsAreAromatic && singleRings.getAtomContainerCount() <= 3) {
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
