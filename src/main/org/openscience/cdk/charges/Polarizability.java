/*  $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Calculation of the polarizability of a molecule by the method of Kang and
 * Jhon and Gasteiger based on {@cdk.cite KJ81} and {@cdk.cite GH82}
 * Limitations in parameterization of atoms:
 * H, Csp3, Csp2, Csp2arom, Csp3, Nsp3, Nsp2, Nsp3,
 * P, Osp3 and Osp2. Aromaticity must be calculated on beforehand.
 *
 * @author         chhoppe
 * @cdk.svnrev  $Revision$
 * @cdk.created    2004-11-03
 * @cdk.keyword polarizability
 * @cdk.module     charges
 */
@TestClass("org.openscience.cdk.charges.PolarizabilityTest")
public class Polarizability {
    private LoggingTool logger;

    /**
     * Constructor for the Polarizability object
     */
    public Polarizability() {
        logger = new LoggingTool(this);
    }

    private void addExplicitHydrogens(IAtomContainer container) {
        try {
        	CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
        	Iterator<IAtom> atoms = container.atoms().iterator();
        	while (atoms.hasNext()) {
        		IAtom atom = atoms.next();
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
     *  Gets the polarizabilitiyFactorForAtom
     *
     *@param  atomContainer    AtomContainer
     *@param  atom  atom for which the factor should become known
     *@return       The polarizabilitiyFactorForAtom value
     */
	@TestMethod("testGetPolarizabilitiyFactorForAtom_IAtomContainer_IAtom")
    public double getPolarizabilitiyFactorForAtom(IAtomContainer atomContainer, IAtom atom) {
        IAtomContainer acH = atomContainer.getBuilder().newAtomContainer(atomContainer);
        addExplicitHydrogens(acH);
        return getKJPolarizabilityFactor(acH, atom);
    }


    /**
     *  calculates the mean molecular polarizability as described in paper of Kang and Jhorn
     *
     *@param  atomContainer  AtomContainer
     *@return     polarizabilitiy
     */
	@TestMethod("testCalculateKJMeanMolecularPolarizability")
    public double calculateKJMeanMolecularPolarizability(IAtomContainer atomContainer) {
        double polarizabilitiy = 0;
        Molecule acH = new Molecule(atomContainer);
        addExplicitHydrogens(acH);
        for (int i = 0; i < acH.getAtomCount(); i++) {
            polarizabilitiy += getKJPolarizabilityFactor(acH, acH.getAtom(i));
        }
        return polarizabilitiy;
    }


    /**
     *  calculate effective atom polarizability
     *
     * @param  atomContainer                     IAtomContainer
     * @param  atom                   atom for which effective atom polarizability should be calculated
     * @param  influenceSphereCutOff  cut off for spheres whoch should taken into account for calculation
     * @param addExplicitH if set to true, then explicit H's will be added, otherwise it assumes that they have
     *  been added to the molecule before being called
     * @return polarizabilitiy
     */
	@TestMethod("testCalculateGHEffectiveAtomPolarizability_IAtomContainer_IAtom_Int_Boolean")
    public double calculateGHEffectiveAtomPolarizability(IAtomContainer atomContainer,IAtom atom,
                                                         int influenceSphereCutOff,
                                                         boolean addExplicitH) {
        double polarizabilitiy = 0;        

        Molecule acH;
        if (addExplicitH) {
            acH = new Molecule(atomContainer);
            addExplicitHydrogens(acH);
        } else {
            acH = (Molecule) atomContainer;
        }

        List<IAtom> startAtom = new ArrayList<IAtom>(1);
        startAtom.add(0, atom);
        double bond;        

        polarizabilitiy += getKJPolarizabilityFactor(acH, atom);
        for (int i = 0; i < acH.getAtomCount(); i++) {
            if (acH.getAtom(i) != atom) {
                bond = PathTools.breadthFirstTargetSearch(acH,
                        startAtom, acH.getAtom(i), 0, influenceSphereCutOff);
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
     * calculate effective atom polarizability
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
	@TestMethod("testCalculateGHEffectiveAtomPolarizability_IAtomContainer_IAtom_Boolean_IntInt")
    public double calculateGHEffectiveAtomPolarizability(IAtomContainer atomContainer,IAtom atom,
                                                         boolean addExplicitH,
                                                         int[][] distanceMatrix) {
        double polarizabilitiy = 0;

        Molecule acH;
        if (addExplicitH) {
            acH = new Molecule(atomContainer);
            addExplicitHydrogens(acH);
        } else {
            acH = (Molecule) atomContainer;
        }

        List<IAtom> startAtom = new ArrayList<IAtom>(1);
        startAtom.add(0, atom);
        double bond;

        polarizabilitiy += getKJPolarizabilityFactor(acH, atom);
        for (int i = 0; i < acH.getAtomCount(); i++) {
            if (acH.getAtom(i) != atom) {
                int atomIndex = atomContainer.getAtomNumber(atom);
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
     *  calculate bond polarizability
     *
     *@param  atomContainer    AtomContainer
     *@param  bond  Bond bond for which the polarizabilitiy should be calculated
     *@return       polarizabilitiy
     */
	@TestMethod("testCalculateBondPolarizability_IAtomContainer_IBond")
    public double calculateBondPolarizability(IAtomContainer atomContainer, IBond bond) {
        double polarizabilitiy = 0;
        Molecule acH = new Molecule(atomContainer);
        addExplicitHydrogens(acH);
        if (bond.getAtomCount() == 2) {
            polarizabilitiy += getKJPolarizabilityFactor(acH, bond.getAtom(0));
            polarizabilitiy += getKJPolarizabilityFactor(acH, bond.getAtom(1));
        }
        return (polarizabilitiy / 2);
    }


    /**
     *  Method which assigns the polarizabilitiyFactors
     *
     *@param  atomContainer    AtomContainer
     *@param  atom  Atom
     *@return       double polarizabilitiyFactor
     */
    private double getKJPolarizabilityFactor(IAtomContainer atomContainer, IAtom atom) {
        double polarizabilitiyFactor = 0;
        String AtomSymbol;
        AtomSymbol = atom.getSymbol();
        if (AtomSymbol.equals("H")) {
            polarizabilitiyFactor = 0.387;
        } else if (AtomSymbol.equals("C")) {
            if (atom.getFlag(CDKConstants.ISAROMATIC)) {
                polarizabilitiyFactor = 1.230;
            } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.SINGLE) {
                polarizabilitiyFactor = 1.064;/*1.064*/
            } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
                if (getNumberOfHydrogen(atomContainer, atom) == 0) {
                    polarizabilitiyFactor = 1.382;
                } else {
                    polarizabilitiyFactor = 1.37;
                }
            } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.TRIPLE ||
            		atomContainer.getMaximumBondOrder(atom) == IBond.Order.QUADRUPLE) {
                polarizabilitiyFactor = 1.279;
            }
        } else if (AtomSymbol.equals("N")) {
            if (atom.getCharge() != CDKConstants.UNSET && atom.getCharge() < 0) {
                polarizabilitiyFactor = 1.090;
            } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.SINGLE) {
                polarizabilitiyFactor = 1.094;
            } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
                polarizabilitiyFactor = 1.030;
            } else {
                polarizabilitiyFactor = 0.852;
            }
        } else if (AtomSymbol.equals("O")) {
            if (atom.getCharge() != CDKConstants.UNSET && atom.getCharge() == -1) {
                polarizabilitiyFactor = 1.791;
            } else if (atom.getCharge() != CDKConstants.UNSET && atom.getCharge() == 1) {
                polarizabilitiyFactor = 0.422;
            } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.SINGLE) {
                polarizabilitiyFactor = 0.664;
            } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
                polarizabilitiyFactor = 0.460;
            }
        } else if (AtomSymbol.equals("P")) {
            if (atomContainer.getConnectedBondsCount(atom) == 4 && 
            	atomContainer.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
                polarizabilitiyFactor = 0;
            }
        } else if (AtomSymbol.equals("S")) {
            if (atom.getFlag(CDKConstants.ISAROMATIC)) {
                polarizabilitiyFactor = 3.38;
            } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.SINGLE) {
                polarizabilitiyFactor = 3.20;/*3.19*/
            } else if (atomContainer.getMaximumBondOrder(atom) == IBond.Order.DOUBLE) {
                if (getNumberOfHydrogen(atomContainer, atom) == 0) {
                    polarizabilitiyFactor = 3.51;
                } else {
                    polarizabilitiyFactor = 3.50;
                }
            } else {
                polarizabilitiyFactor = 3.42;
            }
        }else if (AtomSymbol.equals("F")) {
            polarizabilitiyFactor = 0.296;
        }else if (AtomSymbol.equals("Cl")) {
            polarizabilitiyFactor = 2.343;
        } else if (AtomSymbol.equals("Br")) {
            polarizabilitiyFactor = 3.5;
        } else if (AtomSymbol.equals("I")) {
            polarizabilitiyFactor = 5.79;
        }
        return polarizabilitiyFactor;
    }


    /**
     *  Gets the numberOfHydrogen attribute of the Polarizability object
     *
     *@param  atomContainer    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The numberOfHydrogen value
     */
    private int getNumberOfHydrogen(IAtomContainer atomContainer, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List<IBond> bonds = atomContainer.getConnectedBondsList(atom);
        org.openscience.cdk.interfaces.IAtom connectedAtom;
        int hCounter = 0;
        for (IBond bond : bonds) {
            connectedAtom = bond.getConnectedAtom(atom);
            if (connectedAtom.getSymbol().equals("H")) {
                hCounter += 1;
            }
        }
        return hCounter;
    }
}

