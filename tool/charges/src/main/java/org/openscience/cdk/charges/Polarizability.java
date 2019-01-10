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
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Calculation of the polarizability of a molecule by the method of Kang and
 * Jhon and Gasteiger based on {@cdk.cite KJ81} and {@cdk.cite GH82}
 * Limitations in parameterization of atoms:
 * H, Csp3, Csp2, Csp2arom, Csp3, Nsp3, Nsp2, Nsp3,
 * P, Osp3 and Osp2. Aromaticity must be calculated beforehand.
 *
 * @author         chhoppe
 * @cdk.githash
 * @cdk.created    2004-11-03
 * @cdk.keyword polarizability
 * @cdk.module     charges
 */
public class Polarizability {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(Polarizability.class);

    /**
     * Constructor for the Polarizability object.
     */
    public Polarizability() {}

    private void addExplicitHydrogens(IAtomContainer container) {
        try {
            CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
            for (IAtom atom : container.atoms()) {
                IAtomType type = matcher.findMatchingAtomType(container, atom);
                AtomTypeManipulator.configure(atom, type);
            }
            CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(container.getBuilder());
            hAdder.addImplicitHydrogens(container);
            AtomContainerManipulator.convertImplicitToExplicitHydrogens(container);
        } catch (Exception ex1) {
            logger.debug("Error in hydrogen addition");
        }
    }

    /**
     *  Gets the polarizabilitiyFactorForAtom.
     *
     *@param  atomContainer    AtomContainer
     *@param  atom  atom for which the factor should become known
     *@return       The polarizabilitiyFactorForAtom value
     */
    public double getPolarizabilitiyFactorForAtom(IAtomContainer atomContainer, IAtom atom) {
        IAtomContainer acH = atomContainer.getBuilder().newInstance(IAtomContainer.class, atomContainer);
        addExplicitHydrogens(acH);
        return getKJPolarizabilityFactor(acH, atom);
    }

    /**
     *  calculates the mean molecular polarizability as described in paper of Kang and Jhorn.
     *
     *@param  atomContainer  AtomContainer
     *@return     polarizabilitiy
     */
    public double calculateKJMeanMolecularPolarizability(IAtomContainer atomContainer) {
        double polarizabilitiy = 0;
        IAtomContainer acH = atomContainer.getBuilder().newInstance(IAtomContainer.class, atomContainer);
        addExplicitHydrogens(acH);
        for (int i = 0; i < acH.getAtomCount(); i++) {
            polarizabilitiy += getKJPolarizabilityFactor(acH, acH.getAtom(i));
        }
        return polarizabilitiy;
    }

    /**
     *  calculate effective atom polarizability.
     *
     * @param  atomContainer                     IAtomContainer
     * @param  atom                   atom for which effective atom polarizability should be calculated
     * @param  influenceSphereCutOff  cut off for spheres which should taken into account for calculation
     * @param addExplicitH if set to true, then explicit H's will be added, otherwise it assumes that they have
     *  been added to the molecule before being called
     * @return polarizabilitiy
     */
    public double calculateGHEffectiveAtomPolarizability(IAtomContainer atomContainer, IAtom atom,
            int influenceSphereCutOff, boolean addExplicitH) {
        double polarizabilitiy = 0;

        IAtomContainer acH;
        if (addExplicitH) {
            acH = atomContainer.getBuilder().newInstance(IAtomContainer.class, atomContainer);
            addExplicitHydrogens(acH);
        } else {
            acH = atomContainer;
        }

        List<IAtom> startAtom = new ArrayList<>(1);
        startAtom.add(0, atom);
        double bond;

        polarizabilitiy += getKJPolarizabilityFactor(acH, atom);
        for (int i = 0; i < acH.getAtomCount(); i++) {
            if (!acH.getAtom(i).equals(atom)) {
                bond = PathTools.breadthFirstTargetSearch(acH, startAtom, acH.getAtom(i), 0, influenceSphereCutOff);
                if (bond == 1) {
                    polarizabilitiy += getKJPolarizabilityFactor(acH, acH.getAtom(i));
                } else {
                    polarizabilitiy += (Math.pow(0.5, bond - 1) * getKJPolarizabilityFactor(acH, acH.getAtom(i)));
                }//if bond==0
            }//if !=atom
        }//for
        return polarizabilitiy;
    }

    /**
     * calculate effective atom polarizability.
     *
     * @param atomContainer         IAtomContainer
     * @param atom                  atom for which effective atom polarizability should be calculated
     * @param addExplicitH          if set to true, then explicit H's will be added, otherwise it assumes that they have
     *                              been added to the molecule before being called
     * @param distanceMatrix        an n x n matrix of topological distances between all the atoms in the molecule.
     *                              if this argument is non-null, then BFS will not be used and instead path lengths will be looked up. This
     *                              form of the method is useful, if it is being called for multiple atoms in the same molecule
     * @return polarizabilitiy
     */
    public double calculateGHEffectiveAtomPolarizability(IAtomContainer atomContainer, IAtom atom,
            boolean addExplicitH, int[][] distanceMatrix) {
        double polarizabilitiy = 0;

        IAtomContainer acH;
        if (addExplicitH) {
            acH = atomContainer.getBuilder().newInstance(IAtomContainer.class, atomContainer);
            addExplicitHydrogens(acH);
        } else {
            acH = atomContainer;
        }
        double bond;
        polarizabilitiy += getKJPolarizabilityFactor(acH, atom);
        for (int i = 0; i < acH.getAtomCount(); i++) {
            if (!acH.getAtom(i).equals(atom)) {
                int atomIndex = atomContainer.indexOf(atom);
                bond = distanceMatrix[atomIndex][i];
                if (bond == 1) {
                    polarizabilitiy += getKJPolarizabilityFactor(acH, acH.getAtom(i));
                } else {
                    polarizabilitiy += (Math.pow(0.5, bond - 1) * getKJPolarizabilityFactor(acH, acH.getAtom(i)));
                }//if bond==0
            }//if !=atom
        }//for
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
        IAtomContainer acH = atomContainer.getBuilder().newInstance(IAtomContainer.class, atomContainer);
        addExplicitHydrogens(acH);
        if (bond.getAtomCount() == 2) {
            polarizabilitiy += getKJPolarizabilityFactor(acH, bond.getBegin());
            polarizabilitiy += getKJPolarizabilityFactor(acH, bond.getEnd());
        }
        return (polarizabilitiy / 2);
    }

    /**
     *  Method which assigns the polarizabilitiyFactors.
     *
     *@param  atomContainer    AtomContainer
     *@param  atom  Atom
     *@return       double polarizabilitiyFactor
     */
    private double getKJPolarizabilityFactor(IAtomContainer atomContainer, IAtom atom) {
        double polarizabilitiyFactor = 0;
        String AtomSymbol;
        AtomSymbol = atom.getSymbol();
        switch (AtomSymbol) {
            case "H":
                polarizabilitiyFactor = 0.387;
                break;
            case "C":
                if (atom.getFlag(CDKConstants.ISAROMATIC)) {
                    polarizabilitiyFactor = 1.230;
                } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.SINGLE) {
                    polarizabilitiyFactor = 1.064;/* 1.064 */
                } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
                    if (getNumberOfHydrogen(atomContainer, atom) == 0) {
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
                if (atomContainer.getConnectedBondsCount(atom) == 4
                    && atomContainer.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
                    polarizabilitiyFactor = 0;
                }
                break;
            case "S":
                if (atom.getFlag(CDKConstants.ISAROMATIC)) {
                    polarizabilitiyFactor = 3.38;
                } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.SINGLE) {
                    polarizabilitiyFactor = 3.20;/* 3.19 */
                } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
                    if (getNumberOfHydrogen(atomContainer, atom) == 0) {
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
     *@return       The numberOfHydrogen value
     */
    private int getNumberOfHydrogen(IAtomContainer atomContainer, IAtom atom) {
        java.util.List<IBond> bonds = atomContainer.getConnectedBondsList(atom);
        IAtom connectedAtom;
        int hCounter = 0;
        for (IBond bond : bonds) {
            connectedAtom = bond.getOther(atom);
            if (connectedAtom.getSymbol().equals("H")) {
                hCounter += 1;
            }
        }
        return hCounter;
    }
}
