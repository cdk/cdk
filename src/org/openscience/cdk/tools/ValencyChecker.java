/*  $RCSfile$
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
package org.openscience.cdk.tools;

import java.io.IOException;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.Bond;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.exception.CDKException;

/**
 * This class is an experimental alternative to the SaturationChecker.
 * The main difference is that this checker uses a different atom type
 * list and takes formal charges into account: it first matches against
 * element and charge, and then browses the list for possible matching
 * types.
 *
 * <p>The atoms are matched against the valency_atomtype.xml list.
 *
 * @author     Egon Willighagen
 * @cdk.created    2004-01-07
 *
 * @cdk.keyword    atom, valency
 */
public class ValencyChecker implements ValencyCheckerInterface {

    private final String atomTypeList = "org/openscience/cdk/config/valency_atomtypes.xml";
    
	private AtomTypeFactory structgenATF;

	private LoggingTool logger;

	public ValencyChecker() throws IOException, ClassNotFoundException {
		structgenATF = AtomTypeFactory.getInstance(atomTypeList);
		logger = new LoggingTool(this);
        logger.info("Using configuration file: ", atomTypeList);
	}

    /**
     * Checks wether an Atom is saturated by comparing it with known AtomTypes.
     * It returns true if the atom is an PseudoAtom and when the element is not in the list.
     */
	public boolean isSaturated(Atom atom, AtomContainer container) throws CDKException {
        if (atom instanceof PseudoAtom) {
            logger.debug("don't figure it out... it simply does not lack H's");
            return true;
        }

		AtomType[] atomTypes = structgenATF.getAtomTypes(atom.getSymbol());
        if (atomTypes.length == 0) {
            logger.warn("Missing entry in atom type list for ", atom.getSymbol());
            return true;
        }
        double bondOrderSum = container.getBondOrderSum(atom);
        double maxBondOrder = container.getMaximumBondOrder(atom);
        int hcount = atom.getHydrogenCount();
        int charge = atom.getFormalCharge();

        logger.debug("Checking saturation of atom " + atom.getSymbol());
        logger.debug("bondOrderSum: " + bondOrderSum);
        logger.debug("maxBondOrder: " + maxBondOrder);
        logger.debug("hcount: " + hcount);
        logger.debug("charge: " + charge);

        boolean elementPlusChargeMatches = false;
        for (int f = 0; f < atomTypes.length; f++) {
            AtomType type = atomTypes[f];
            if (charge == type.getFormalCharge()) {
                if (bondOrderSum + hcount == type.getBondOrderSum() && 
                    maxBondOrder <= type.getMaxBondOrder()) {
                    logger.debug("We have a match!");
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
        throw new CDKException("The atom with element " + atom.getSymbol() +
                               "and charge " + charge + " is not found.");
    }
    
    /**
     * Determines if the atom can be of type AtomType.
     */
    public boolean couldMatchAtomType(AtomContainer container, Atom atom, AtomType type) {
        logger.debug("   ... matching atom ", atom.getSymbol(), " vs ", type);
        double bondOrderSum = container.getBondOrderSum(atom);
        double maxBondOrder = container.getMaximumBondOrder(atom);
        int hcount = atom.getHydrogenCount();
        int charge = atom.getFormalCharge();
        if (charge == type.getFormalCharge()) {
            if (bondOrderSum + hcount <= type.getBondOrderSum() && 
                maxBondOrder <= type.getMaxBondOrder()) {
                logger.debug("    We have a match!");
                return true;
            }
        }
        logger.debug("    No Match");
        return false;
    }

	/**
	 * Calculate the number of missing hydrogens by substracting the number of
	 * bonds for the atom from the expected number of bonds. Charges are included
	 * in the calculation. The number of expected bonds is defined by the AtomType
	 * generated with the AtomTypeFactory.
	 *
	 * @param  atom      Description of the Parameter
	 * @param  container  Description of the Parameter
	 * @return           Description of the Return Value
	 */
	public int calculateMissingHydrogen(Atom atom, AtomContainer container) throws CDKException {
        return this.calculateMissingHydrogen(atom, 
            container.getBondOrderSum(atom),
            container.getMaximumBondOrder(atom)
        );
    }
    
	public int calculateMissingHydrogen(Atom atom) throws CDKException {
        return this.calculateMissingHydrogen(atom, 0,0);
    }

    /** 
     * Calculates the number of hydrogens that can be added to the given atom to fullfil
     * the atom's valency. It will return 0 for PseudoAtoms, and for atoms for which it
     * does not have an entry in the configuration file.
     */
	public int calculateMissingHydrogen(Atom atom, double bondOrderSum, double maxBondOrder) 
        throws CDKException {

        int missingHydrogen = 0;
        if (atom instanceof PseudoAtom) {
            logger.debug("don't figure it out... it simply does not lack H's");
            return 0;
        }
        
        logger.debug("Calculating number of missing hydrogen atoms");
        // get default atom
        AtomType[] atomTypes = structgenATF.getAtomTypes(atom.getSymbol());
        if (atomTypes.length == 0) {
            logger.warn("Element not found in configuration file: ", atom);
            return 0;
        }

        int hcount = atom.getHydrogenCount();
        int charge = atom.getFormalCharge();
        
        logger.debug("Found atomtypes: ", atomTypes.length);
        for (int f = 0; f < atomTypes.length; f++) {
            AtomType type = atomTypes[f];
            if (charge == type.getFormalCharge()) {
                if (bondOrderSum + hcount <= type.getBondOrderSum() && 
                    maxBondOrder <= type.getMaxBondOrder()) {
                    logger.debug("This type matches: ", type);
                    missingHydrogen = (int) (type.getBondOrderSum() - bondOrderSum);
                    break;
                }
            } // else: formal charges don't match
        }
        
        logger.debug("missing hydrogens: " + missingHydrogen);
        return missingHydrogen;
    }
    
	/**
	 * Saturates a molecule by setting appropriate bond orders.
	 *
	 * @cdk.keyword            bond order, calculation
     *
     * @cdk.created 2003-10-03
	 */
    public void saturate(AtomContainer atomContainer) throws CDKException {
        logger.info("Saturating atomContainer by adjusting bond orders...");
        boolean allSaturated = allSaturated(atomContainer);
        if (!allSaturated) {
            boolean succeeded = saturate(atomContainer.getBonds(), atomContainer);
            if (!succeeded) {
                throw new CDKException("Could not saturate this atomContainer!");
            }
        }
    }

    /**
     * Saturates a set of Bonds in an AtomContainer.
     */
    public boolean saturate(Bond[] bonds, AtomContainer atomContainer) throws CDKException {
        logger.debug("Saturating bond set of size: ", bonds.length);
        boolean bondsAreFullySaturated = true;
        if (bonds.length > 0) {
            Bond bond = bonds[0];

            // determine bonds left
            int leftBondCount = bonds.length-1;
            Bond[] leftBonds = new Bond[leftBondCount];
            System.arraycopy(bonds, 1, leftBonds, 0, leftBondCount);

            // examine this bond
            if (isUnsaturated(bond, atomContainer)) {
                // either this bonds should be saturated or not
                
                // try to leave this bond unsaturated and saturate the left bondssaturate this bond
                if (leftBondCount > 0) {
                    logger.debug("Recursing with unsaturated bond with #bonds: ", leftBondCount);
                    bondsAreFullySaturated = saturate(leftBonds, atomContainer) 
                                             && !isUnsaturated(bond, atomContainer);
                } else {
                    bondsAreFullySaturated = false;
                }

                // ok, did it work? if not, saturate this bond, and recurse
                if (!bondsAreFullySaturated) {
                    logger.debug("First try did not work...");
                    // ok, revert saturating this bond, and recurse again
                    boolean couldSaturate = saturate(bond, atomContainer);
                    if (couldSaturate) {
                        if (leftBondCount > 0) {
                            logger.debug("Recursing with saturated bond with #bonds: ", leftBondCount);
                            bondsAreFullySaturated = saturate(leftBonds, atomContainer);
                        } else {
                            bondsAreFullySaturated = true;
                        }
                    } else {
                        bondsAreFullySaturated = false;
                        // no need to recurse, because we already know that this bond
                        // unsaturated does not work
                    }
                }
            } else if (isSaturated(bond, atomContainer)) {
                logger.debug("This bond is already saturated.");
                if (leftBondCount > 0) {
                    logger.debug("Recursing with #bonds: ", leftBondCount);
                    bondsAreFullySaturated = saturate(leftBonds, atomContainer);
                } else {
                    bondsAreFullySaturated = true;
                }
            } else {
                logger.debug("Cannot saturate this bond");
                // but, still recurse (if possible)
                if (leftBondCount > 0) {
                    logger.debug("Recursing with saturated bond with #bonds: " + leftBondCount);
                    bondsAreFullySaturated = saturate(leftBonds, atomContainer) 
                                             && !isUnsaturated(bond, atomContainer);
                } else {
                    bondsAreFullySaturated = !isUnsaturated(bond, atomContainer);
                }
            }
        }
        logger.debug("Is bond set fully saturated?: " + bondsAreFullySaturated);
        logger.debug("Returning to level: " + (bonds.length + 1));
        return bondsAreFullySaturated;
    }
    
    /**
     * Saturate atom by adjusting its bond orders.
     */
    public boolean saturate(Bond bond, AtomContainer atomContainer) throws CDKException {
        Atom[] atoms = bond.getAtoms();
        Atom atom = atoms[0];
        Atom partner = atoms[1];
        logger.debug("  saturating bond: ", atom.getSymbol(), "-", partner.getSymbol());
        AtomType[] atomTypes1 = structgenATF.getAtomTypes(atom.getSymbol());
        AtomType[] atomTypes2 = structgenATF.getAtomTypes(partner.getSymbol());
        boolean bondOrderIncreased = true;
        while (bondOrderIncreased && !isSaturated(bond, atomContainer)) {
            logger.debug("Can increase bond order");
            bondOrderIncreased = false;
            for (int atCounter1=0; atCounter1<atomTypes1.length&& !bondOrderIncreased; atCounter1++) {
                AtomType aType1 = atomTypes1[atCounter1];
                logger.debug("  condidering atom type: ", aType1);
                if (couldMatchAtomType(atomContainer, atom, aType1)) {
                    logger.debug("  trying atom type: ", aType1);
                    for (int atCounter2=0; atCounter2<atomTypes2.length && !bondOrderIncreased; atCounter2++) {
                        AtomType aType2 = atomTypes2[atCounter2];
                        logger.debug("  condidering partner type: ", aType1);
                        if (couldMatchAtomType(atomContainer, partner, atomTypes2[atCounter2])) {
                            logger.debug("    with atom type: ", aType2);
                            if (bond.getOrder() >= aType2.getMaxBondOrder() || 
                                bond.getOrder() >= aType1.getMaxBondOrder()) {
                                logger.debug("Bond order not increased: atoms has reached (or exceeded) maximum bond order for this atom type");
                            } else if (bond.getOrder() < aType2.getMaxBondOrder() &&
                                       bond.getOrder() < aType1.getMaxBondOrder()) {
                                bond.setOrder(bond.getOrder() + 1);
                                logger.debug("Bond order now " + bond.getOrder());
                                bondOrderIncreased = true;
                            }
                        }
                    }
                }
            }
        }
        return isSaturated(bond, atomContainer);
    }
    
    /**
     * Determines of all atoms on the AtomContainer are saturated.
     */
	public boolean isSaturated(AtomContainer container) throws CDKException {
        return allSaturated(container);
    }
	public boolean allSaturated(AtomContainer ac) throws CDKException
	{
        logger.debug("Are all atoms saturated?");
        for (int f = 0; f < ac.getAtomCount(); f++) {
            if (!isSaturated(ac.getAtomAt(f), ac)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns wether a bond is unsaturated. A bond is unsaturated if 
     * <b>both</b> Atoms in the bond are unsaturated.
     */
    public boolean isUnsaturated(Bond bond, AtomContainer atomContainer) throws CDKException {
        Atom[] atoms = bond.getAtoms();
        boolean isUnsaturated = true;
        for (int i=0; i<atoms.length; i++) {
            isUnsaturated = isUnsaturated && !isSaturated(atoms[i], atomContainer);
        }
        return isUnsaturated;
    }
    
    /**
     * Returns wether a bond is saturated. A bond is saturated if 
     * <b>both</b> Atoms in the bond are saturated.
     */
    public boolean isSaturated(Bond bond, AtomContainer atomContainer) throws CDKException {
        Atom[] atoms = bond.getAtoms();
        boolean isSaturated = true;
        for (int i=0; i<atoms.length; i++) {
            isSaturated = isSaturated && isSaturated(atoms[i], atomContainer);
        }
        return isSaturated;
    }
    
    /**
     * Resets the bond orders of all atoms to 1.0.
     */
    public void unsaturate(AtomContainer atomContainer) {
        unsaturate(atomContainer.getBonds());
    }
    
    /**
     * Resets the bond order of the Bond to 1.0.
     */
    public void unsaturate(Bond[] bonds) {
        for (int i = 1; i < bonds.length; i++) {
            bonds[i].setOrder(1.0);
        }
    }
    
}

