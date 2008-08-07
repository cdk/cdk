/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * Small customization of ValencyHybridChecker suggested by Todd Martin
 * specially tuned for SMILES parsing.
 *
 * @author       Egon Willighagen
 * @cdk.created  2004-06-12
 * @cdk.keyword  atom, valency
 * @cdk.module   valencycheck
 * @cdk.svnrev  $Revision$
 */
public class SmilesValencyChecker implements IValencyChecker, IDeduceBondOrderTool {

	private String atomTypeList = null;
	protected AtomTypeFactory structgenATF;
	protected LoggingTool logger;

	public SmilesValencyChecker() {
        this("org/openscience/cdk/dict/data/cdk-atom-types.owl");
    }

	public SmilesValencyChecker(String atomTypeList) {
        this.atomTypeList = atomTypeList;
        logger = new LoggingTool(this);
        logger.info("Using configuration file: ", atomTypeList);
    }

	/**
	 * Saturates a molecule by setting appropriate bond orders.
	 *
	 * @cdk.keyword            bond order, calculation
     *
     * @cdk.created 2003-10-03
	 */
    public void saturate(IAtomContainer atomContainer) throws CDKException {
        logger.info("Saturating atomContainer by adjusting bond orders...");
        boolean allSaturated = allSaturated(atomContainer);
        if (!allSaturated) {
            logger.info("Saturating bond orders is needed...");
            IBond[] bonds = new IBond[atomContainer.getBondCount()];
        	for (int i=0; i<bonds.length; i++) bonds[i] = atomContainer.getBond(i);
            boolean succeeded = saturate(bonds, atomContainer);
            if (!succeeded) {
                throw new CDKException("Could not saturate this atomContainer!");
            }
        }
    }

    /**
     * Saturates a set of Bonds in an AtomContainer.
     */
    public boolean saturate(IBond[] bonds, IAtomContainer atomContainer) throws CDKException {
        logger.debug("Saturating bond set of size: ", bonds.length);
        boolean bondsAreFullySaturated = false;
        if (bonds.length > 0) {
        	IBond bond = bonds[0];

            // determine bonds left
            int leftBondCount = bonds.length-1;
            IBond[] leftBonds = new IBond[leftBondCount];
            System.arraycopy(bonds, 1, leftBonds, 0, leftBondCount);

            // examine this bond
            logger.debug("Examining this bond: ", bond);
            if (isSaturated(bond, atomContainer)) {
                logger.debug("OK, bond is saturated, now try to saturate remaining bonds (if needed)");
                bondsAreFullySaturated = saturate(leftBonds, atomContainer);
            } else if (isUnsaturated(bond, atomContainer)) {
                logger.debug("Ok, this bond is unsaturated, and can be saturated");
                // two options now: 
                // 1. saturate this one directly
                // 2. saturate this one by saturating the rest
                logger.debug("Option 1: Saturating this bond directly, then trying to saturate rest");
                // considering organic bonds, the max order is 3, so increase twice
                boolean bondOrderIncreased = saturateByIncreasingBondOrder(bond, atomContainer);
                bondsAreFullySaturated = bondOrderIncreased && saturate(bonds, atomContainer);
                if (bondsAreFullySaturated) {
                    logger.debug("Option 1: worked");
                } else {
                    logger.debug("Option 1: failed. Trying option 2.");
                    logger.debug("Option 2: Saturing this bond by saturating the rest");
                    // revert the increase (if succeeded), then saturate the rest
                    if (bondOrderIncreased) unsaturateByDecreasingBondOrder(bond);
                    bondsAreFullySaturated = saturate(leftBonds, atomContainer) &&
                                             isSaturated(bond, atomContainer);
                    if (!bondsAreFullySaturated) logger.debug("Option 2: failed");
                }
            } else {
                logger.debug("Ok, this bond is unsaturated, but cannot be saturated");
                // try recursing and see if that fixes things
                bondsAreFullySaturated = saturate(leftBonds, atomContainer) &&
                                         isSaturated(bond, atomContainer);
            }
        } else {
            bondsAreFullySaturated = true; // empty is saturated by default
        }
        return bondsAreFullySaturated;
    }

    public boolean unsaturateByDecreasingBondOrder(IBond bond) {
        if (bond.getOrder() != IBond.Order.SINGLE) {
        	bond.setOrder(BondManipulator.decreaseBondOrder(bond.getOrder()));
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Returns whether a bond is unsaturated. A bond is unsaturated if 
     * <b>all</b> Atoms in the bond are unsaturated.
     */
    public boolean isUnsaturated(IBond bond, IAtomContainer atomContainer) throws CDKException {
        logger.debug("isBondUnsaturated?: ", bond);
        IAtom[] atoms = BondManipulator.getAtomArray(bond);
        boolean isUnsaturated = true;
        for (int i=0; i<atoms.length && isUnsaturated; i++) {
            isUnsaturated = isUnsaturated && !isSaturated(atoms[i], atomContainer);
        }
        logger.debug("Bond is unsaturated?: ", isUnsaturated);
        return isUnsaturated;
    }

    /**
     * Tries to saturate a bond by increasing its bond orders by 1.0.
     *
     * @return true if the bond could be increased
     */
    public boolean saturateByIncreasingBondOrder(IBond bond, IAtomContainer atomContainer) throws CDKException {
    	IAtom[] atoms = BondManipulator.getAtomArray(bond);
    	IAtom atom = atoms[0];
    	IAtom partner = atoms[1];
        logger.debug("  saturating bond: ", atom.getSymbol(), "-", partner.getSymbol());
        IAtomType[] atomTypes1 = getAtomTypeFactory(bond.getBuilder()).getAtomTypes(atom.getSymbol());
        IAtomType[] atomTypes2 = getAtomTypeFactory(bond.getBuilder()).getAtomTypes(partner.getSymbol());
        for (int atCounter1=0; atCounter1<atomTypes1.length; atCounter1++) {
            IAtomType aType1 = atomTypes1[atCounter1];
            logger.debug("  condidering atom type: ", aType1);
            if (couldMatchAtomType(atomContainer, atom, aType1)) {
                logger.debug("  trying atom type: ", aType1);
                for (int atCounter2=0; atCounter2<atomTypes2.length; atCounter2++) {
                    IAtomType aType2 = atomTypes2[atCounter2];
                    logger.debug("  condidering partner type: ", aType1);
                    if (couldMatchAtomType(atomContainer, partner, atomTypes2[atCounter2])) {
                        logger.debug("    with atom type: ", aType2);
                        if (BondManipulator.isLowerOrder(bond.getOrder(), aType2.getMaxBondOrder()) &&
                        	BondManipulator.isLowerOrder(bond.getOrder(), aType1.getMaxBondOrder())) {
                        	bond.setOrder(BondManipulator.increaseBondOrder(bond.getOrder()));
                            logger.debug("Bond order now ", bond.getOrder());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns whether a bond is saturated. A bond is saturated if 
     * <b>both</b> Atoms in the bond are saturated.
     */
    public boolean isSaturated(IBond bond, IAtomContainer atomContainer) throws CDKException {
        logger.debug("isBondSaturated?: ", bond);
        IAtom[] atoms = BondManipulator.getAtomArray(bond);
        boolean isSaturated = true;
        for (int i=0; i<atoms.length; i++) {
            logger.debug("isSaturated(Bond, AC): atom I=", i);
            isSaturated = isSaturated && isSaturated(atoms[i], atomContainer);
        }
        logger.debug("isSaturated(Bond, AC): result=", isSaturated);
        return isSaturated;
    }

    /**
     * Determines of all atoms on the AtomContainer are saturated.
     */
	public boolean isSaturated(IAtomContainer container) throws CDKException {
        return allSaturated(container);
    }
	public boolean allSaturated(IAtomContainer ac) throws CDKException {
        logger.debug("Are all atoms saturated?");
        for (int f = 0; f < ac.getAtomCount(); f++) {
            if (!isSaturated(ac.getAtom(f), ac)) {
                return false;
            }
        }
        return true;
    }

	/**
     * Determines if the atom can be of type AtomType. That is, it sees if this
     * AtomType only differs in bond orders, or implicit hydrogen count.
     */
    public boolean couldMatchAtomType(IAtom atom, double bondOrderSum, IBond.Order maxBondOrder, IAtomType type) {
        logger.debug("couldMatchAtomType:   ... matching atom ", atom, " vs ", type);
        int hcount = atom.getHydrogenCount();
        int charge = atom.getFormalCharge();
        if (charge == type.getFormalCharge()) {
            logger.debug("couldMatchAtomType:     formal charge matches...");
//            if (atom.getHybridization() == type.getHybridization()) {
//                logger.debug("couldMatchAtomType:     hybridization is OK...");
                if (bondOrderSum + hcount <= type.getBondOrderSum()) {
                    logger.debug("couldMatchAtomType:     bond order sum is OK...");
                    if (!BondManipulator.isHigherOrder(maxBondOrder, type.getMaxBondOrder())) {
                        logger.debug("couldMatchAtomType:     max bond order is OK... We have a match!");
                        return true;
                    }
                } else {
                    logger.debug("couldMatchAtomType:      no match", "" + 
                        (bondOrderSum + hcount), " > ", "" + type.getBondOrderSum());
                }
//            }
        } else {
            logger.debug("couldMatchAtomType:     formal charge does NOT match...");
        }
        logger.debug("couldMatchAtomType:    No Match");
        return false;
    }

    /** 
     * Calculates the number of hydrogens that can be added to the given atom to fullfil
     * the atom's valency. It will return 0 for PseudoAtoms, and for atoms for which it
     * does not have an entry in the configuration file.
     */
	public int calculateNumberOfImplicitHydrogens(IAtom atom, double bondOrderSum, IBond.Order maxBondOrder, int neighbourCount) 
        throws CDKException {

        int missingHydrogens = 0;
        if (atom instanceof IPseudoAtom) {
            logger.debug("don't figure it out... it simply does not lack H's");
            return 0;
        }
        
        logger.debug("Calculating number of missing hydrogen atoms");
        // get default atom
        IAtomType[] atomTypes = getAtomTypeFactory(atom.getBuilder()).getAtomTypes(atom.getSymbol());
        if (atomTypes.length == 0) {
            logger.warn("Element not found in configuration file: ", atom);
            return 0;
        }

        logger.debug("Found atomtypes: ", atomTypes.length);
        for (int f = 0; f < atomTypes.length; f++) {
            IAtomType type = atomTypes[f];
            if (couldMatchAtomType(atom, bondOrderSum, maxBondOrder, type)) {
                logger.debug("This type matches: ", type);
                int formalNeighbourCount = type.getFormalNeighbourCount();
                if (type.getHybridization() == CDKConstants.UNSET) {
                    missingHydrogens = (int) (type.getBondOrderSum() - bondOrderSum);
                } else if (type.getHybridization() == Hybridization.SP3) {
                	missingHydrogens = formalNeighbourCount - neighbourCount;
                } else if (type.getHybridization() == Hybridization.SP2) {
                	missingHydrogens = formalNeighbourCount - neighbourCount;
                } else if (type.getHybridization() == Hybridization.SP1) {
                	missingHydrogens = formalNeighbourCount - neighbourCount;
                } else {
                	missingHydrogens = (int) (type.getBondOrderSum() - bondOrderSum);
                }
                break;
            }
        }
        
        logger.debug("missing hydrogens: ", missingHydrogens);
        return missingHydrogens;
    }

    /**
     * Checks whether an Atom is saturated by comparing it with known AtomTypes.
     * It returns true if the atom is an PseudoAtom and when the element is not in the list.
     */
	public boolean isSaturated(IAtom atom, IAtomContainer container) throws CDKException {
        if (atom instanceof IPseudoAtom) {
            logger.debug("don't figure it out... it simply does not lack H's");
            return true;
        }

		IAtomType[] atomTypes = getAtomTypeFactory(atom.getBuilder()).getAtomTypes(atom.getSymbol());
        if (atomTypes.length == 0) {
            logger.warn("Missing entry in atom type list for ", atom.getSymbol());
            return true;
        }
        double bondOrderSum = container.getBondOrderSum(atom);
        IBond.Order maxBondOrder = container.getMaximumBondOrder(atom);
        int hcount = atom.getHydrogenCount();
        int charge = atom.getFormalCharge();

        logger.debug("Checking saturation of atom ", atom.getSymbol());
        logger.debug("bondOrderSum: ", bondOrderSum);
        logger.debug("maxBondOrder: ", maxBondOrder);
        logger.debug("hcount: ", hcount);
        logger.debug("charge: ", charge);

        boolean elementPlusChargeMatches = false;
        for (int f = 0; f < atomTypes.length; f++) {
            IAtomType type = atomTypes[f];
            if (couldMatchAtomType(atom, bondOrderSum, maxBondOrder, type)) {
                if (bondOrderSum + hcount == type.getBondOrderSum() && 
                    !BondManipulator.isHigherOrder(maxBondOrder, type.getMaxBondOrder())) {
                    logger.debug("We have a match: ", type);
                    logger.debug("Atom is saturated: ", atom.getSymbol());
                    return true;
                } else {
                    // ok, the element and charge matche, but unfulfilled
                    elementPlusChargeMatches = true;
                }
            } // else: formal charges don't match
        }
        
        if (elementPlusChargeMatches) {
            logger.debug("No, atom is not saturated.");
            return false;
        }
        
        // ok, the found atom was not in the list
        logger.error("Could not find atom type!");
        throw new CDKException("The atom with element " + atom.getSymbol() +
                               " and charge " + charge + " is not found.");
    }

	public int calculateNumberOfImplicitHydrogens(IAtom atom, IAtomContainer container) throws CDKException {
        return this.calculateNumberOfImplicitHydrogens(atom, 
            container.getBondOrderSum(atom),
            container.getMaximumBondOrder(atom),
            container.getConnectedAtomsCount(atom)
        );
    }

    protected AtomTypeFactory getAtomTypeFactory(IChemObjectBuilder builder) throws CDKException {
        if (structgenATF == null) {
            try {
                structgenATF = AtomTypeFactory.getInstance(atomTypeList, builder);
            } catch (Exception exception) {
                logger.debug(exception);
                throw new CDKException("Could not instantiate AtomTypeFactory!", exception);
            }
        }
        return structgenATF;
    }

    /**
     * Determines if the atom can be of type AtomType.
     */
    public boolean couldMatchAtomType(IAtomContainer container, IAtom atom, IAtomType type) {
        double bondOrderSum = container.getBondOrderSum(atom);
        IBond.Order maxBondOrder = container.getMaximumBondOrder(atom);
        return couldMatchAtomType(atom, bondOrderSum, maxBondOrder, type);
    }

}

