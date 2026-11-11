/* Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
 *
 *  Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.charges;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.graph.ShortestPaths;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;

/**
 * Calculation of the polarizability of a molecule by the method of Kang and
 * Jhon and Gasteiger based on {@cdk.cite KJ81} and {@cdk.cite GH82}
 * Limitations in parameterization of atoms:
 * H, Csp3, Csp2, Csp2arom, Csp3, Nsp3, Nsp2, Nsp3,
 * P, Osp3 and Osp2. Aromaticity and implicit hydrogen counts must be
 * calculated beforehand. Unset (null) implicit hydrogen counts are treated
 * as zero; atom types and hydrogen counts are not perceived here. Implicit
 * hydrogens are counted without modifying the input molecule.
 *
 * @author         chhoppe
 * @cdk.created    2004-11-03
 * @cdk.keyword polarizability
 */
public class Polarizability {

    private static final double HYDROGEN_POLARIZABILITY = 0.387;

    /**
     * Constructor for the Polarizability object.
     */
    public Polarizability() {}

    /**
     *  Gets the polarizabilitiyFactorForAtom.
     *
     *@param  atomContainer    AtomContainer
     *@param  atom  atom for which the factor should become known
     *@return       The polarizabilitiyFactorForAtom value
     */
    public double getPolarizabilitiyFactorForAtom(IAtomContainer atomContainer, IAtom atom) {
        return getKJPolarizabilityFactor(atomContainer, atom, true);
    }

    /**
     *  calculates the mean molecular polarizability as described in paper of Kang and Jhorn.
     *
     *@param  atomContainer  AtomContainer
     *@return     polarizabilitiy
     */
    public double calculateKJMeanMolecularPolarizability(IAtomContainer atomContainer) {
        double polarizabilitiy = 0;
        for (IAtom atom : atomContainer.atoms()) {
            polarizabilitiy += getKJPolarizabilityFactor(atomContainer, atom, true);
            polarizabilitiy += getImplicitHydrogenCount(atom) * HYDROGEN_POLARIZABILITY;
        }
        return polarizabilitiy;
    }

    /**
     *  calculate effective atom polarizability.
     *
     * @param  atomContainer                     IAtomContainer
     * @param  atom                   atom for which effective atom polarizability should be calculated
     * @param  influenceSphereCutOff  maximum distance searched; atoms beyond the cutoff or disconnected from the
     *                               target retain the historical distance of -1 and its weight of 4
     * @param addExplicitH if true, include implicit hydrogens without adding atoms; otherwise only explicit
     *                     hydrogens are counted
     * @return polarizabilitiy
     */
    public double calculateGHEffectiveAtomPolarizability(IAtomContainer atomContainer, IAtom atom,
            int influenceSphereCutOff, boolean addExplicitH) {
        double polarizabilitiy = getKJPolarizabilityFactor(atomContainer, atom, addExplicitH);
        ShortestPaths paths = new ShortestPaths(atomContainer, atom);
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            IAtom current = atomContainer.getAtom(i);
            int distance = paths.distanceTo(i);
            if (!current.equals(atom)) {
                int bond = distance == Integer.MAX_VALUE || distance > influenceSphereCutOff ? -1 : distance;
                polarizabilitiy += getDistanceWeight(bond)
                        * getKJPolarizabilityFactor(atomContainer, current, addExplicitH);
            }
            if (addExplicitH) {
                // An implicit H is one bond beyond its parent, including at the cutoff.
                int bond = distance == Integer.MAX_VALUE || distance >= influenceSphereCutOff ? -1 : distance + 1;
                polarizabilitiy += getDistanceWeight(bond) * HYDROGEN_POLARIZABILITY
                        * getImplicitHydrogenCount(current);
            }
        }
        return polarizabilitiy;
    }

    /**
     * calculate effective atom polarizability.
     *
     * @param atomContainer         IAtomContainer
     * @param atom                  atom for which effective atom polarizability should be calculated
     * @param addExplicitH          if true, include implicit hydrogens without adding atoms; otherwise only explicit
     *                              hydrogens are counted
     * @param distanceMatrix        a non-null matrix of topological distances indexed by the atoms in atomContainer,
     *                              in their current order, including any explicit hydrogens already present. For
     *                              addExplicitH=true, implicit hydrogens need no rows or columns: their distances are
     *                              their parents' distances plus one, except that -1 remains -1 for unreachable atoms
     *                              and hydrogens, preserving its weight of 4. With an all-heavy-atom input, a heavy-atom
     *                              matrix is sufficient. A matrix from an explicit-hydrogen copy is also accepted if the
     *                              original atom indices are preserved; extra rows and columns are ignored.
     * @return polarizabilitiy
     */
    public double calculateGHEffectiveAtomPolarizability(IAtomContainer atomContainer, IAtom atom,
            boolean addExplicitH, int[][] distanceMatrix) {
        int atomIndex = atomContainer.indexOf(atom);
        double polarizabilitiy = getKJPolarizabilityFactor(atomContainer, atom, addExplicitH);
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            IAtom current = atomContainer.getAtom(i);
            double distance = current.equals(atom) ? 0 : distanceMatrix[atomIndex][i];
            if (!current.equals(atom)) {
                polarizabilitiy += getDistanceWeight(distance)
                        * getKJPolarizabilityFactor(atomContainer, current, addExplicitH);
            }
            if (addExplicitH) {
                double hydrogenDistance = distance == -1 ? -1 : distance + 1;
                polarizabilitiy += getDistanceWeight(hydrogenDistance) * HYDROGEN_POLARIZABILITY
                        * getImplicitHydrogenCount(current);
            }
        }
        return polarizabilitiy;
    }

    /**
     *  calculate bond polarizability.
     *
     *@param  atomContainer    AtomContainer
     *@param  bond  Bond bond for which the polarizabilitiy should be calculated
     *@return       polarizabilitiy
     */
    public double calculateBondPolarizability(IAtomContainer atomContainer, IBond bond) {
        double polarizabilitiy = 0;
        if (bond.getAtomCount() == 2) {
            polarizabilitiy += getKJPolarizabilityFactor(atomContainer, bond.getBegin(), true);
            polarizabilitiy += getKJPolarizabilityFactor(atomContainer, bond.getEnd(), true);
        }
        return (polarizabilitiy / 2);
    }

    private double getDistanceWeight(double distance) {
        return distance == 1 ? 1 : Math.pow(0.5, distance - 1);
    }

    private int getImplicitHydrogenCount(IAtom atom) {
        Integer count = atom.getImplicitHydrogenCount();
        return count == null ? 0 : count;
    }

    /**
     *  Method which assigns the polarizabilitiyFactors.
     *
     *@param  atomContainer    AtomContainer
     *@param  atom  Atom
     *@param  includeImplicitH  whether implicit hydrogens contribute to atom environments
     *@return       double polarizabilitiyFactor
     */
    private double getKJPolarizabilityFactor(IAtomContainer atomContainer, IAtom atom, boolean includeImplicitH) {
        double polarizabilitiyFactor = 0;
        String AtomSymbol;
        AtomSymbol = atom.getSymbol();
        switch (AtomSymbol) {
            case "H":
                polarizabilitiyFactor = HYDROGEN_POLARIZABILITY;
                break;
            case "C":
                if (atom.getFlag(IChemObject.AROMATIC)) {
                    polarizabilitiyFactor = 1.230;
                } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.SINGLE) {
                    polarizabilitiyFactor = 1.064;/* 1.064 */
                } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
                    if (getNumberOfHydrogen(atomContainer, atom, includeImplicitH) == 0) {
                        polarizabilitiyFactor = 1.382;
                    } else {
                        polarizabilitiyFactor = 1.37;
                    }
                } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.TRIPLE
                           || atomContainer.getMaximumBondOrder(atom) == IBond.Order.QUADRUPLE) {
                    polarizabilitiyFactor = 1.279;
                }
                break;
            case "N":
                if (atom.getCharge() != CDKConstants.UNSET && atom.getCharge() < 0) {
                    polarizabilitiyFactor = 1.090;
                } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.SINGLE) {
                    polarizabilitiyFactor = 1.094;
                } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
                    polarizabilitiyFactor = 1.030;
                } else {
                    polarizabilitiyFactor = 0.852;
                }
                break;
            case "O":
                if (atom.getCharge() != CDKConstants.UNSET && atom.getCharge() == -1) {
                    polarizabilitiyFactor = 1.791;
                } else if (atom.getCharge() != CDKConstants.UNSET && atom.getCharge() == 1) {
                    polarizabilitiyFactor = 0.422;
                } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.SINGLE) {
                    polarizabilitiyFactor = 0.664;
                } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
                    polarizabilitiyFactor = 0.460;
                }
                break;
            case "P":
                if (atomContainer.getConnectedBondsCount(atom)
                    + (includeImplicitH ? getImplicitHydrogenCount(atom) : 0) == 4
                    && atomContainer.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
                    polarizabilitiyFactor = 0;
                }
                break;
            case "S":
                if (atom.getFlag(IChemObject.AROMATIC)) {
                    polarizabilitiyFactor = 3.38;
                } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.SINGLE) {
                    polarizabilitiyFactor = 3.20;/* 3.19 */
                } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
                    if (getNumberOfHydrogen(atomContainer, atom, includeImplicitH) == 0) {
                        polarizabilitiyFactor = 3.51;
                    } else {
                        polarizabilitiyFactor = 3.50;
                    }
                } else {
                    polarizabilitiyFactor = 3.42;
                }
                break;
            case "F":
                polarizabilitiyFactor = 0.296;
                break;
            case "Cl":
                polarizabilitiyFactor = 2.343;
                break;
            case "Br":
                polarizabilitiyFactor = 3.5;
                break;
            case "I":
                polarizabilitiyFactor = 5.79;
                break;
        }
        return polarizabilitiyFactor;
    }

    /**
     *  Gets the numberOfHydrogen attribute of the Polarizability object.
     *
     *@param  atomContainer    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@param  includeImplicitH  whether implicit hydrogens are counted
     *@return       The numberOfHydrogen value
     */
    private int getNumberOfHydrogen(IAtomContainer atomContainer, IAtom atom, boolean includeImplicitH) {
        java.util.List<IBond> bonds = atomContainer.getConnectedBondsList(atom);
        IAtom connectedAtom;
        int hCounter = includeImplicitH ? getImplicitHydrogenCount(atom) : 0;
        for (IBond bond : bonds) {
            connectedAtom = bond.getOther(atom);
            if (connectedAtom.getAtomicNumber() == IElement.H) {
                hCounter += 1;
            }
        }
        return hCounter;
    }
}
