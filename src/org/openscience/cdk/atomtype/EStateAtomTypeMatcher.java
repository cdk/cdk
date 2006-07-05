/* $RCSfile: $
 * $Author: egonw $
 * $Date: 2006-05-04 19:29:58 +0000 (Thu, 04 May 2006) $
 * $Revision: 6171 $
 *
 * Copyright (C) 2006  Todd Martin (Environmental Protection Agency)
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
package org.openscience.cdk.atomtype;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;

/**
 * Determines the EState atom types.
 * 
 * @author     Todd Martin
 * @cdk.module standard
 */
public class EStateAtomTypeMatcher  implements IAtomTypeMatcher {

	public IAtomType findMatchingAtomType(IAtomContainer atomContainer, IAtom atom) {
		IAtomType atomType = null;
		
		try {
			String fragment = "";
			
			AllRingsFinder arf = new AllRingsFinder();
			IRingSet rs = arf.findAllRings(atomContainer);
			
			int NumSingleBonds2 = 0;
			int NumDoubleBonds2 = 0;
			int NumTripleBonds2 = 0;
			int NumAromaticBonds2 = 0;
			int NumAromaticBondsTotal2 = 0;
			
			String element = atom.getSymbol();
			
			IAtom[] AttachedAtoms = atomContainer.getConnectedAtoms(atom);
			
			for (int j = 0; j <= AttachedAtoms.length - 1; j++) {
				
				IBond b = atomContainer.getBond(atom, AttachedAtoms[j]);
				
				if (atom.getFlag(CDKConstants.ISAROMATIC)
						&& AttachedAtoms[j]
						                 .getFlag(CDKConstants.ISAROMATIC)) {
					
					boolean SameRing = inSameAromaticRing(atomContainer, atom,
							AttachedAtoms[j], rs);
					
					if (SameRing) {
						NumAromaticBonds2++;
						if (element.equals("N")) {
							if (b.getOrder() == 1)
								NumAromaticBondsTotal2++;
							if (b.getOrder() == 2)
								NumAromaticBondsTotal2 = NumAromaticBondsTotal2 + 2;
						}
					} else {
						if (b.getOrder() == 1)
							NumSingleBonds2++;
						if (b.getOrder() == 2)
							NumDoubleBonds2++;
						if (b.getOrder() == 3)
							NumTripleBonds2++;
					}
					
				} else {
					
					if (b.getOrder() == 1)
						NumSingleBonds2++;
					if (b.getOrder() == 2)
						NumDoubleBonds2++;
					if (b.getOrder() == 3)
						NumTripleBonds2++;
				}
			}
			
			// assign frag here
			fragment = "S";
			
			for (int j = 0; j <= NumTripleBonds2 - 1; j++) {
				fragment += "t";
			}
			
			for (int j = 0; j <= NumDoubleBonds2 - 1; j++) {
				fragment += "d";
			}
			
			for (int j = 0; j <= NumSingleBonds2 - 1; j++) {
				fragment += "s";
			}
			
			for (int j = 0; j <= NumAromaticBonds2 - 1; j++) {
				fragment += "a";
			}
			
			fragment += element;
			
			if (atom.getFormalCharge() == 1) {
				fragment += "p";
			} else if (atom.getFormalCharge() == -1) {
				fragment += "m";
			}
			
			fragment += "*";// flag it as unassigned fragment
			
			// for now set #H = 0 TODO- add routine to figure out #Hs for
			// possible valence states
			atom.setHydrogenCount(0);

			atomType = atom.getBuilder().newAtomType(fragment, atom.getSymbol());
			atomType.setFormalCharge(atom.getFormalCharge());
			if (atom.getFlag(CDKConstants.ISAROMATIC))
				atomType.setFlag(CDKConstants.ISAROMATIC, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return atomType;
	}

	public static boolean inSameAromaticRing(IAtomContainer m, IAtom atom1,
			IAtom atom2, IRingSet rs) {
		
		for (int i = 0; i <= rs.getAtomContainerCount() - 1; i++) {
			IRing r = (IRing) rs.getAtomContainer(i);
			
			if (!r.getFlag(CDKConstants.ISAROMATIC))
				continue;
			
			// ArrayList al=new ArrayList();
			
			boolean HaveOne = false;
			boolean HaveTwo = false;
			
			for (int j = 0; j <= r.getAtomCount() - 1; j++) {
				if (m.getAtomNumber(r.getAtomAt(j)) == m.getAtomNumber(atom1))
					HaveOne = true;
				if (m.getAtomNumber(r.getAtomAt(j)) == m.getAtomNumber(atom2))
					HaveTwo = true;
			}
			
			if (HaveOne && HaveTwo) {
				return true;
			}
			
		} // end ring for loop
		
		return false;
	}
	
	
}
