/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2004-2007  Kai Hartmann <kaihartmann@users.sf.net>
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;

import java.util.List;
import java.util.Stack;

/**
 * @author       kaihartmann
 * @cdk.svnrev   $Revision$
 * @cdk.created  2004-09-17
 * @cdk.module   reaction
 *
 * @cdk.todo add negatively charged atoms (e.g. O-) to the pi system
 */
@TestClass("org.openscience.cdk.graph.invariant.ConjugatedPiSystemsDetectorTest")
public class ConjugatedPiSystemsDetector {

    /**
     *  Detect all conjugated pi systems in an AtomContainer. This method returns a AtomContainerSet
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
     *	mol.addBond(0, 1, IBond.Order.DOUBLE);
     *	mol.addBond(1, 2, IBond.Order.SINGLE);
     *	mol.addBond(0, 3, IBond.Order.SINGLE);
     *	mol.addBond(0, 4, IBond.Order.SINGLE);
     *	mol.addBond(1, 5, IBond.Order.SINGLE);
     *	mol.addBond(2, 6, IBond.Order.SINGLE);
     *	mol.addBond(2, 7, IBond.Order.SINGLE);
     *	SingleElectron se = new SingleElectron(a2);
     *	mol.addElectronContainer(se);
     *  </pre>
     *
     *@param  ac  The AtomContainer for which to detect conjugated pi systems
     *@return     The set of AtomContainers with conjugated pi systems
     */
    @TestMethod("testDetectButadiene,test3Aminomethane_cation,testPiSystemWithCarbokation,testCyanoallene")
    public static IAtomContainerSet detect(IAtomContainer ac) {
        IAtomContainerSet piSystemSet = ac.getBuilder().newAtomContainerSet();

        for (int i = 0; i < ac.getAtomCount(); i++) {
        	IAtom atom = ac.getAtom(i);
            atom.setFlag(CDKConstants.VISITED, false);
        }

        for (int i = 0; i < ac.getAtomCount(); i++) {
        	IAtom firstAtom = ac.getAtom(i);
            // if this atom was already visited in a previous DFS, continue
            if (firstAtom.getFlag(CDKConstants.VISITED) || checkAtom(ac, firstAtom) == -1) {
                continue;
            }
            IAtomContainer piSystem = ac.getBuilder().newAtomContainer();
            Stack<IAtom> stack = new Stack<IAtom>();

            piSystem.addAtom(firstAtom);
            stack.push(firstAtom);
            firstAtom.setFlag(CDKConstants.VISITED, true);
            // Start DFS from firstAtom
            while (!stack.empty()) {
                //boolean addAtom = false;
                IAtom currentAtom = stack.pop();
                List<IAtom> atoms = ac.getConnectedAtomsList(currentAtom);
                List<IBond> bonds = ac.getConnectedBondsList(currentAtom);

                for (int j = 0; j < atoms.size(); j++) {
                    IAtom atom = atoms.get(j);
                    IBond bond = bonds.get(j);
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
        List<IAtom> atoms = ac.getConnectedAtomsList(currentAtom);
        List<IBond> bonds = ac.getConnectedBondsList(currentAtom);
        if (currentAtom.getFlag(CDKConstants.ISAROMATIC)) {
            check = 0;
        } else if (currentAtom.getFormalCharge() == 1 /*&& currentAtom.getSymbol().equals("C")*/) {
            check = 0;
        } else if (currentAtom.getFormalCharge() == -1) {
			//// NEGATIVE CHARGES WITH A NEIGHBOOR PI BOND //////////////
		    int counterOfPi = 0;
            for (IAtom atom : atoms) {
                if (ac.getMaximumBondOrder(atom) != IBond.Order.SINGLE) {
                    counterOfPi++;
                }
            }
		    if(counterOfPi > 0) check = 0;
        }else { 
			int se = ac.getConnectedSingleElectronsCount(currentAtom);
			if (se == 1) {
				check = 0;  //// DETECTION of radicals
			}else if (ac.getConnectedLonePairsCount(currentAtom) > 0 
				/*&& (currentAtom.getSymbol().equals("N")*/) {
				check = 0;  //// DETECTION of  lone pair
			}else {
                int highOrderBondCount = 0;
			    for (int j = 0; j < atoms.size(); j++) {
					IBond bond = bonds.get(j);
					if (bond == null || bond.getOrder() != IBond.Order.SINGLE) {
					    highOrderBondCount++;
					} else {
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

