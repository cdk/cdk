/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.graph.invariant;

import java.util.Stack;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.SetOfAtomContainers;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 *@author        kaihartmann
 *@cdk.created   2004-09-17
 *@cdk.module    extra
 *
 *@cdk.todo add negatively charged atoms (e.g. O-) to the pi system
 */
public class ConjugatedPiSystemsDetector {

    /**
     *  Detect all conjugated pi systems in an AtomContainer. This method returns a SetOfAtomContainers
     *  with Atom and Bond objects from the original AtomContainer. The aromaticity has to be known 
     *  before calling this method.
     *
     *  <p>An example for detection of Radical Allyl:
     *  <pre>
     *	Atom a0 = new Atom("C"); mol.addAtom(a0);
     *	Atom a1 = new Atom("C"); mol.addAtom(a1);
     *	Atom a2 = new Atom("C"); mol.addAtom(a2);
     *	Atom h1 = new Atom("H"); mol.addAtom(h1);
     *	Atom h2 = new Atom("H"); mol.addAtom(h2);
     *	Atom h3 = new Atom("H"); mol.addAtom(h3);
     *	Atom h4 = new Atom("H"); mol.addAtom(h4);
     *	Atom h5 = new Atom("H"); mol.addAtom(h5);
     *	mol.addBond(0, 1, 2);
     *	mol.addBond(1, 2, 1);
     *	mol.addBond(0, 3, 1);
     *	mol.addBond(0, 4, 1);
     *	mol.addBond(1, 5, 1);
     *	mol.addBond(2, 6, 1);
     *	mol.addBond(2, 7, 1);
     *	SingleElectron se = new SingleElectron(a2);
     *	mol.addElectronContainer(se);
     *  </pre>
     *
     *@param  ac  The AtomContainer for which to detect conjugated pi systems
     *@return     The set of AtomContainers with conjugated pi systems
     */
    @SuppressWarnings({"unchecked"})
	public static SetOfAtomContainers detect(org.openscience.cdk.interfaces.IAtomContainer ac) {
        SetOfAtomContainers piSystemSet = new SetOfAtomContainers();

        for (int i = 0; i < ac.getAtomCount(); i++) {
        	org.openscience.cdk.interfaces.IAtom atom = ac.getAtomAt(i);
            atom.setFlag(CDKConstants.VISITED, false);
        }

        for (int i = 0; i < ac.getAtomCount(); i++) {
        	IAtom firstAtom = ac.getAtomAt(i);
            // if this atom was already visited in a previous DFS, continue
            if (firstAtom.getFlag(CDKConstants.VISITED) || checkAtom(ac, firstAtom) == -1) {
                continue;
            }
            AtomContainer piSystem = new org.openscience.cdk.AtomContainer();
            Stack stack = new Stack();

            piSystem.addAtom(firstAtom);
            stack.push(firstAtom);
            firstAtom.setFlag(CDKConstants.VISITED, true);
            // Start DFS from firstAtom
            while (!stack.empty()) {
                boolean addAtom = false;
                Atom currentAtom = (Atom) stack.pop();
                Vector atoms = ac.getConnectedAtomsVector(currentAtom);
                Vector bonds = ac.getConnectedBondsVector(currentAtom);

                for (int j = 0; j < atoms.size(); j++) {
                    Atom atom = (Atom) atoms.get(j);
                    Bond bond = (Bond) bonds.get(j);
                    if (!atom.getFlag(CDKConstants.VISITED)) {
                        int check = checkAtom(ac, atom);
                        if (check == 1) {
                            piSystem.addAtom(atom);
                            piSystem.addBond(bond);
                            continue;
                            // do not mark atom as visited if cumulative double bond
                        } else if (check == 0) {
                            piSystem.addAtom(atom);
                            piSystem.addBond(bond);
                            stack.push(atom);
                        }
                        atom.setFlag(CDKConstants.VISITED, true);
                    }
                    // close rings with one bond
                    else if (!piSystem.contains(bond) && piSystem.contains(atom)) {
                        piSystem.addBond(bond);
                    }
                }
            }

            if (piSystem.getAtomCount() > 2) {
                piSystemSet.addAtomContainer(piSystem);
            }
        }

        return piSystemSet;
    }


    /**
     *  Check an Atom whether it may be conjugated or not.
     *
     *@param  ac           The AtomContainer containing currentAtom
     *@param  currentAtom  The Atom to check
     *@return              -1 if isolated, 0 if conjugated, 1 if cumulative db
     */
    private static int checkAtom(IAtomContainer ac, IAtom currentAtom) {
        int check = -1;
        Vector atoms = ac.getConnectedAtomsVector(currentAtom);
        Vector bonds = ac.getConnectedBondsVector(currentAtom);
        if (currentAtom.getFlag(CDKConstants.ISAROMATIC)) {
            check = 0;
        } else if (currentAtom.getFormalCharge() == 1 /*&& currentAtom.getSymbol().equals("C")*/) {
            check = 0;
        } else if (currentAtom.getFormalCharge() == -1) {
			//// NEGATIVE CHARGES WITH A NEIGHBOOR PI BOND //////////////
		    int counterOfPi = 0;
	            for(int n = 0; n < atoms.size(); n++) {
					Atom atom = (Atom) atoms.get(n);
					if(ac.getMaximumBondOrder(atom) > 1.0) { counterOfPi ++; }
	            }
		    if(counterOfPi > 0) check = 0;
        }else { 
			int se = ac.getSingleElectronSum(currentAtom);
			if (se == 1) {
				check = 0;  //// DETECTION of radicals
			}else if (ac.getLonePairCount(currentAtom) > 0 
				/*&& (currentAtom.getSymbol().equals("N")*/) {
				check = 0;  //// DETECTION of  lone pair
			}else {
			    int singleBondCount = 0;
			    int highOrderBondCount = 0;
			    for (int j = 0; j < atoms.size(); j++) {
					Bond bond = (Bond) bonds.get(j);
					if (bond == null || bond.getOrder() > 1) {
					    highOrderBondCount++;
					} else {
					    singleBondCount++;
					}
			    }
			    if (highOrderBondCount == 1) {
			    	check = 0;
			    } else if (highOrderBondCount > 1) {
			    	check = 1;
			    }
			}
        }
        return check;
    }
}

