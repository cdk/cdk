/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.qsar;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;


public class RotatableBondsCountDescriptor implements Descriptor {

	public RotatableBondsCountDescriptor() { }


	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 2) {
			throw new CDKException("RotatableBondsCount only expects less than two parameters");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The first parameter must be of type Boolean");
		}
		if (!(params[1] instanceof RingSet)) {
			throw new CDKException("The parameter must be of type RingSet");
		}
		// ok, all should be fine
		includeTerminals = (Boolean) params[0];
		ringSet = (RingSet) params[1];
	}


	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = includeTerminals;
		params[1] = ringSet;
		return params;
	}

	public Object calculate(AtomContainer ac, RingSet ringSet, boolean includeTerminals) throws NoSuchAtomException {
		int rotatableBondsCount = 0;
		Bond[] bonds = ac.getBonds();
		Vector ringsWithThisBond = null;
		int degree0 = null;
		int degree1 = null;

		for (int f = 0; f < bonds.length; f++) {
			ringsWithThisBond = ringSet.getRings(bonds[f]);
			if (ringsWithThisBond.size() > 0) {
				bonds[f].setFlag(CDKConstants.ISINRING, true);
			}
		}
		for (int i = 0; i < bonds.length; i++) {

			Atom[] atoms = ac.getBondAt(i).getAtoms();
			if (bonds[i].getOrder() == CDKConstants.BONDORDER_SINGLE) {
				if ((ac.getMaximumBondOrder(atoms[0]) < 3.0) && (ac.getMaximumBondOrder(atoms[1]) < 3.0)) {
					if (bonds[i].getFlag(CDKConstants.ISINRING) == false) {
						int degree0 = ac.getBondCount(atoms[0]);
						int degree1 = ac.getBondCount(atoms[1]);
						if ((degree0 == 1) || (degree1 == 1)) {
							if (includeTerminals == true) {
								rotatableBondsCount += 1;
							}
							if (includeTerminals == false) {
								rotatableBondsCount += 0;
							}
						} else {
							rotatableBondsCount += 1;
						}
					}
				}
			}
		}
		return rotatableBondsCount;
	}

	public Object calculate(AtomContainer ac, boolean includeTerminals) throws NoSuchAtomException {
		int rotatableBondsCount = 0;
		Bond[] bonds = ac.getBonds();
		Vector ringsWithThisBond = null;
		int degree0 = null;
		int degree1 = null;
		RingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		ringSet = arf.findAllRings(ac);
		Bond[] bonds = ac.getBonds();
		for (int f = 0; f < bonds.length; f++) {
			Vector ringsWithThisBond = ringSet.getRings(bonds[f]);
			if (ringsWithThisBond.size() > 0) {
				bonds[f].setFlag(CDKConstants.ISINRING, true);
			}
		}
		for (int i = 0; i < bonds.length; i++) {
			Atom[] atoms = ac.getBondAt(i).getAtoms();
			if (bonds[i].getOrder() == CDKConstants.BONDORDER_SINGLE) {
				if ((ac.getMaximumBondOrder(atoms[0]) < 3.0) && (ac.getMaximumBondOrder(atoms[1]) < 3.0)) {
					if (bonds[i].getFlag(CDKConstants.ISINRING) == false) {
						degree0 = ac.getBondCount(atoms[0]);
						degree1 = ac.getBondCount(atoms[1]);
						if ((degree0 == 1) || (degree1 == 1)) {
							if (includeTerminals == true) {
								rotatableBondsCount += 1;
							}
							if (includeTerminals == false) {
								rotatableBondsCount += 0;
							}
						} else {
							rotatableBondsCount += 1;
						}
					}
				}
			}
		}
		return rotatableBondsCount;
	}
	
	
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "Include Terminal Bonds in the count";
		params[1] = "A predefined RingSet";
		return params;
	}


	
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[1];
		paramTypes[0] = new Boolean();
		paramTypes[1] = new RingSet();
		return paramTypes;
	}
}

