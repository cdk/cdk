/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.aromaticity;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.Ring;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.LoggingTool;

/**
 * The HueckelAromaticityDetector detects the aromaticity based on
 * the Hueckel 4n+2 pi-electrons Rule. This is done by one of the
 * detectAromaticity methods. They set the aromaticity flags of
 * appropriate Atoms, Bonds and Rings. After the detection, you
 * can use getFlag(CDKConstants.ISAROMATIC) on these ChemObjects.
 *
 * @cdk.module standard
 *
 * @author      steinbeck
 * @author      kaihartmann
 * @cdk.created 2001-09-04
 */
public class HueckelAromaticityDetector {
    
	static LoggingTool logger = new LoggingTool("org.openscience.cdk.aromaticity.HueckelAromaticityDetector");
	
	
	/**
	 * Retrieves the set of all rings and performs an aromaticity detection
	 * based on Hueckels 4n + 2 rule.
	 *
	 * @return  True if molecule is aromatic
	 */
	public static boolean detectAromaticity(AtomContainer atomContainer) throws org.openscience.cdk.exception.NoSuchAtomException {
		return (detectAromaticity(atomContainer, true));
	}
	
	
	/**
	 * Uses precomputed set of ALL rings and performs an aromaticity detection
	 * based on Hueckels 4n + 2 rule.
	 *
	 * @param   ringSet  set of ALL rings
	 * @return  True if molecule is aromatic
	 */
	public static boolean detectAromaticity(AtomContainer atomContainer, RingSet ringSet) throws org.openscience.cdk.exception.NoSuchAtomException {
		return (detectAromaticity(atomContainer, ringSet, true));
	}
	
	
	/**
	 * Retrieves the set of all rings and performs an aromaticity detection
	 * based on Hueckels 4n + 2 rule.
	 *
	 * @param   removeAromatictyFlags  Leaves ChemObjects that are already marked as aromatic as they are
	 * @return                         True if molecule is aromatic
	 */
	public static boolean detectAromaticity(AtomContainer atomContainer, boolean removeAromatictyFlags) throws org.openscience.cdk.exception.NoSuchAtomException {
		logger.debug("Entered Aromaticity Detection");
		logger.debug("Starting AllRingsFinder");
		long before = System.currentTimeMillis();
		RingSet ringSet = new AllRingsFinder().findAllRings(atomContainer);
		long after = System.currentTimeMillis();
		logger.debug("time for finding all rings: " + (after - before) + " milliseconds");
		logger.debug("Finished AllRingsFinder");
		if (ringSet.size() > 0) {
			return detectAromaticity(atomContainer, ringSet, removeAromatictyFlags);
		}
		return false;
	}
	
	
	/**
	 * Uses precomputed set of ALL rings and performs an aromaticity detection
	 * based on Hueckels 4n + 2 rule.
	 *
	 * @param  ringSet                 set of ALL rings
	 * @param  removeAromaticityFlags  Leaves ChemObjects that are already marked as aromatic as they are
	 * @return                         True if molecule is aromatic
	 */
	public static boolean detectAromaticity(AtomContainer atomContainer, RingSet ringSet, boolean removeAromaticityFlags) {
		boolean foundSomething = false;
		if (removeAromaticityFlags) {
			for (int f = 0; f < atomContainer.getAtomCount(); f++) {
				atomContainer.getAtomAt(f).setFlag(CDKConstants.ISAROMATIC, false);
			}
			for (int f = 0; f < atomContainer.getElectronContainerCount(); f++) {
				ElectronContainer electronContainer = atomContainer.getElectronContainerAt(f);
				if (electronContainer instanceof Bond) {
					electronContainer.setFlag(CDKConstants.ISAROMATIC, false);
				}
			}
			for (int f = 0; f < ringSet.size(); f++) {
				((Ring) ringSet.get(f)).setFlag(CDKConstants.ISAROMATIC, false);
			}
		}
		
		Ring ring = null;
		ringSet.sort();
		for (int f = 0; f < ringSet.size(); f++) {
			ring = (Ring) ringSet.elementAt(f);
			logger.debug("Testing for aromaticity in ring no ", f);
			if (AromaticityCalculator.isAromatic(ring, atomContainer)) {
				ring.setFlag(CDKConstants.ISAROMATIC, true);
				
				for (int g = 0; g < ring.getAtomCount(); g++) {
					ring.getAtomAt(g).setFlag(CDKConstants.ISAROMATIC, true);
				}
				
				for (int g = 0; g < ring.getElectronContainerCount(); g++) {
					ElectronContainer electronContainer = ring.getElectronContainerAt(g);
					if (electronContainer instanceof Bond) {
						electronContainer.setFlag(CDKConstants.ISAROMATIC, true);
					}
				}
				
				foundSomething = true;
				logger.debug("This ring is aromatic: ", f);
			} else {
				logger.debug("This ring is *not* aromatic: ", f);
			}
		}
		return foundSomething;
	}
	
	
	/**
	 * This method sets the aromaticity flags for a RingSet from the Atom flags.
	 * It can be used after the aromaticity detection to set the appropriate flags
	 * for a RingSet from the SSSR search.
	 *
	 * @param  ringset                 the RingSet to set the flags for
	 */
	static public void setRingFlags(RingSet ringset) {
		for (int i = 0; i < ringset.size(); i++) {
			boolean aromatic = true;
			Ring ring = (Ring)ringset.get(i);
			for (int j = 0; j < ring.getAtomCount(); j++) {
				if (ring.getAtomAt(j).getFlag(CDKConstants.ISAROMATIC) != true) {
					aromatic = false;
					break;
				}
			}
			if (aromatic) {
				ring.setFlag(CDKConstants.ISAROMATIC, true);
			} else {
				ring.setFlag(CDKConstants.ISAROMATIC, false);
			}
		}
	}
	
	/*
	 *  public static boolean isAromatic(AtomContainer ac, Ring ring)
	 *  {
	 *  return AromaticityCalculator.isAromatic(ring, ac);
	 *  }
	 *  *	public static boolean isAromatic(AtomContainer ac, Ring ring)
	 *  {
	 *  int piElectronCount = 0;
	 *  int freeElectronPairCount = 0;
	 *  Atom atom = null;
	 *  Bond bond = null;
	 *  int aromaCounter = 0;
	 *  if (debug) System.out.println("isAromatic() -> ring.size(): " + ring.getAtomCount());
	 *  for (int g = 0; g < ring.getAtomCount(); g++)
	 *  {
	 *  atom = ring.getAtomAt(g);
	 *  if ("O-N-S-P".indexOf(atom.getSymbol()) > -1)
	 *  {
	 *  freeElectronPairCount += 1;
	 *  }
	 *  if (atom.getFlag(CDKConstants.ISAROMATIC))
	 *  {
	 *  aromaCounter ++;
	 *  }
	 *  }
	 *  for (int g = 0; g < ring.getElectronContainerCount(); g++) {
	 *  ElectronContainer ec = ring.getElectronContainerAt(g);
	 *  if (ec instanceof Bond) {
	 *  bond = (Bond)ec;
	 *  if (bond.getOrder() > 1) {
	 *  piElectronCount += 2*(bond.getOrder()-1);
	 *  }
	 *  }
	 *  }
	 *  for (int f = 0; f < ((ring.getAtomCount() - 2)/4) + 2; f ++)
	 *  {
	 *  if (debug) System.out.println("isAromatic() -> freeElectronPairCount: " + freeElectronPairCount);
	 *  if (debug) System.out.println("isAromatic() -> piElectronCount: " + piElectronCount);
	 *  if (debug) System.out.println("isAromatic() -> f: " + f);
	 *  if (debug) System.out.println("isAromatic() -> (4 * f) + 2: " + ((4 * f) + 2));
	 *  if (debug) System.out.println("isAromatic() -> ring.size(): " + ring.getAtomCount());
	 *  if (debug) System.out.println("isAromatic() -> aromaCounter: " + aromaCounter);
	 *  if (aromaCounter == ring.getAtomCount()) return true;
	 *  else if ((piElectronCount == ring.getAtomCount())&&((4 * f) + 2 == piElectronCount)) return true;
	 *  else if ((4 * f) + 2 == piElectronCount + (freeElectronPairCount * 2) && ring.getAtomCount() < piElectronCount + (freeElectronPairCount * 2)) return true;
	 *  }
	 *  return false;
	 *  }
	 */
}

