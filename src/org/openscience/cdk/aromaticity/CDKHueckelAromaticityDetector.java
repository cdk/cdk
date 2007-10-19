/* $Revision: 8373 $ $Author: rajarshi $ $Date: 2007-06-22 21:29:09 +0200 (Fri, 22 Jun 2007) $
 *
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.LoggingTool;

/**
 * This aromaticity detector detects the aromaticity based on the H&uuml;ckel
 * 4n+2 pi-electrons rule applied to isolated ring systems. It assumes
 * CDK atom types to be perceived.
 *
 * @author         egonw
 * @cdk.module     experimental
 * @cdk.created    2007-10-05
 * 
 * @see org.openscience.cdk.CDKConstants
 */
public class CDKHueckelAromaticityDetector {

	static LoggingTool logger = new LoggingTool(CDKHueckelAromaticityDetector.class);

	public static boolean detectAromaticity(IAtomContainer atomContainer) throws CDKException {
		SpanningTree spanningTree = new SpanningTree(atomContainer);
		IAtomContainer ringSystems = spanningTree.getCyclicFragmentsContainer();
		if (ringSystems.getAtomCount() == 0) {
			// If there are no rings, then there cannot be any aromaticity
			return false;
		}
		// FIXME: should not really mark them here
		Iterator<IAtom> atoms = ringSystems.atoms();
		while (atoms.hasNext()) atoms.next().setFlag(CDKConstants.ISINRING, true);
		Iterator<IBond> bonds = ringSystems.bonds();
		while (bonds.hasNext()) bonds.next().setFlag(CDKConstants.ISINRING, true);		
		
		boolean foundSomeAromaticity = false;
		Iterator<IAtomContainer> isolatedRingSystems = ConnectivityChecker.partitionIntoMolecules(ringSystems).atomContainers();
		while (isolatedRingSystems.hasNext()) {
			IAtomContainer isolatedSystem = isolatedRingSystems.next();
			Iterator<IAtomContainer> singleRings = new SSSRFinder(isolatedSystem).findSSSR().atomContainers();
			while (singleRings.hasNext()) {
				IAtomContainer singleRing = singleRings.next();
				if (isRingSystemSproutedWithNonRingDoubleBonds(atomContainer, singleRing)) {
//					OK, this ring is not aromatic
				} else {
					// FIXME: ring might be aromatic -> check that
					// OK, all atoms must be sp2, or sp3 with a lone pair
					Iterator<IAtom> ringAtoms = singleRing.atoms();
					boolean allAtomsSuitable = true;
					while (ringAtoms.hasNext()) {
						IAtom ringAtom = ringAtoms.next();
						if (ringAtom.getHybridization() == CDKConstants.HYBRIDIZATION_SP2) {
							// for example, a carbon
							// note: the double bond is in the ring, that has been tested earlier
							// FIXME: this does assume bond orders to be resolved too, when detecting
							// sprouting double bonds
						} else if (ringAtom.getHybridization() == CDKConstants.HYBRIDIZATION_SP3 &&
								   getLonePairCount(ringAtom) > 0) {
							// for example, a nitrogen or oxygen
						} else {
							// OK, not aromatic, need to break the while loop now
							allAtomsSuitable = false;
						}
					}
					if (allAtomsSuitable) {
						foundSomeAromaticity = true;
						markRingAtomsAndBondsAromatic(singleRing);
					}
				}
			}
		}
		
		return foundSomeAromaticity;
	}
	
	/**
	 * Determines if the isolatedRingSystem has attached double bonds, which are not part of the ring system itself,
	 * and not part of any other ring system.
	 */
	private static boolean isRingSystemSproutedWithNonRingDoubleBonds(IAtomContainer fullContainer, IAtomContainer isolatedRingSystem) {
		Iterator<IAtom> atoms = isolatedRingSystem.atoms();
		while (atoms.hasNext()) {
			Iterator<IBond> neighborBonds = fullContainer.getConnectedBondsList(atoms.next()).iterator();
			while (neighborBonds.hasNext()) {
				IBond neighborBond = neighborBonds.next();
				if (!neighborBond.getFlag(CDKConstants.ISINRING) &&
					neighborBond.getOrder() == CDKConstants.BONDORDER_DOUBLE ||
					neighborBond.getOrder() == CDKConstants.BONDORDER_TRIPLE) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static int getLonePairCount(IAtom atom) {
		Integer count = (Integer)atom.getProperty(CDKConstants.LONE_PAIR_COUNT);
		if (count == null) {
			return 0;
		} else {
			return count.intValue();
		}
	}
	
	private static void markRingAtomsAndBondsAromatic(IAtomContainer container) {
		Iterator<IAtom> atoms = container.atoms();
		while (atoms.hasNext()) atoms.next().setFlag(CDKConstants.ISAROMATIC, true);
		Iterator<IBond> bonds = container.bonds();
		while (bonds.hasNext()) bonds.next().setFlag(CDKConstants.ISAROMATIC, true);
	}
}

