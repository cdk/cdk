/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2001-2003  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.exception.CDKException;
import java.util.Vector;
import java.io.*;

/**
 * Provides methods for checking whether an atoms valences are saturated with
 * respect to a particular atom type.
 *
 * @author     steinbeck
 * @author  Egon Willighagen
 * @created    2001-09-04
 *
 * @keyword    saturation
 * @keyword    atom, valency
 */
public class SaturationChecker
{

	AtomTypeFactory structgenATF;

	private org.openscience.cdk.tools.LoggingTool logger;

	public SaturationChecker() throws IOException, ClassNotFoundException
	{
		structgenATF = AtomTypeFactory.getInstance("org/openscience/cdk/config/structgen_atomtypes.xml");
		logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
	}


	public boolean hasPerfectConfiguration(Atom atom, AtomContainer ac) throws CDKException
	{

		double bondOrderSum = ac.getBondOrderSum(atom);
		double maxBondOrder = ac.getMaximumBondOrder(atom);
		AtomType[] atomTypes = structgenATF.getAtomTypes(atom.getSymbol());
    if(atomTypes.length==0)
      throw new CDKException("Missing entry in structgen_atomtypes.xml for "+atom.getSymbol());
		logger.debug("*** Checking for perfect configuration ***");
		try
		{
			logger.debug("Checking configuration of atom " + ac.getAtomNumber(atom));
			logger.debug("Atom has bondOrderSum = " + bondOrderSum);
			logger.debug("Atom has max = " + bondOrderSum);
		} catch (Exception exc)
		{
		}
		for (int f = 0; f < atomTypes.length; f++)
		{
			if (bondOrderSum == atomTypes[f].getBondOrderSum() && 
                maxBondOrder == atomTypes[f].getMaxBondOrder())
			{
				try
				{
					logger.debug("Atom " + ac.getAtomNumber(atom) + " has perfect configuration");
				} catch (Exception exc)
				{
				}
				return true;
			}
		}
		try
		{
			logger.debug("*** Atom " + ac.getAtomNumber(atom) + " has imperfect configuration ***");
		} catch (Exception exc)
		{
		}
		return false;
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
     * Checks wether an Atom is saturated by comparing it with known AtomTypes.
     */
	public boolean isSaturated(Atom atom, AtomContainer ac) throws CDKException {
		AtomType[] atomTypes = structgenATF.getAtomTypes(atom.getSymbol());
        if(atomTypes.length==0)
            throw new CDKException("Missing entry in structgen_atomtypes.xml for "+atom.getSymbol());
        double bondOrderSum = ac.getBondOrderSum(atom);
        double maxBondOrder = ac.getMaximumBondOrder(atom);
        int hcount = atom.getHydrogenCount();
        int charge = atom.getFormalCharge();
        try {
            logger.debug("*** Checking saturation of atom " + atom.getSymbol() + ac.getAtomNumber(atom) + " ***");
            logger.debug("bondOrderSum: " + bondOrderSum);
            logger.debug("maxBondOrder: " + maxBondOrder);
            logger.debug("hcount: " + hcount);
        } catch (Exception exc) {
            logger.debug(exc);
        }
        for (int f = 0; f < atomTypes.length; f++) {
            if (bondOrderSum - charge + hcount == atomTypes[f].getBondOrderSum() && 
                maxBondOrder <= atomTypes[f].getMaxBondOrder()) {
                    logger.debug("*** Good ! ***");
                    return true;
                }
        }
        logger.debug("*** Bad ! ***");
        return false;
    }

	/**
	 * Checks if the current atom has exceeded its bond order sum value.
	 *
	 * @param  atom The Atom to check
	 * @param  ac   The atomcontainer context
	 * @return      oversaturated or not
	 */
	public boolean isOverSaturated(Atom atom, AtomContainer ac) throws CDKException
	{
		AtomType[] atomTypes = structgenATF.getAtomTypes(atom.getSymbol());
    if(atomTypes.length==0)
      throw new CDKException("Missing entry in structgen_atomtypes.xml for "+atom.getSymbol());
		double bondOrderSum = ac.getBondOrderSum(atom);
		double maxBondOrder = ac.getMaximumBondOrder(atom);
		int hcount = atom.getHydrogenCount();
		int charge = atom.getFormalCharge();
		try
		{
			logger.debug("*** Checking saturation of atom " + ac.getAtomNumber(atom) + " ***");
			logger.debug("bondOrderSum: " + bondOrderSum);
			logger.debug("maxBondOrder: " + maxBondOrder);
			logger.debug("hcount: " + hcount);
		} catch (Exception exc)
		{
		}
		for (int f = 0; f < atomTypes.length; f++)
		{
			if (bondOrderSum - charge + hcount > atomTypes[f].getBondOrderSum())
			{
				logger.debug("*** Good ! ***");
				return true;
			}
		}
		logger.debug("*** Bad ! ***");
		return false;
	}
    
	/**
	 * Returns the currently maximum formable bond order for this atom.
	 *
	 * @param  atom  The atom to be checked
	 * @param  ac    The AtomContainer that provides the context
	 * @return       the currently maximum formable bond order for this atom
	 */
	public double getCurrentMaxBondOrder(Atom atom, AtomContainer ac) throws CDKException
	{
		AtomType[] atomTypes = structgenATF.getAtomTypes(atom.getSymbol());
    if(atomTypes.length==0)
      throw new CDKException("Missing entry in structgen_atomtypes.xml for "+atom.getSymbol());
		double bondOrderSum = ac.getBondOrderSum(atom);
		int hcount = atom.getHydrogenCount();
		double max = 0;
		double current = 0;
		for (int f = 0; f < atomTypes.length; f++)
		{
			current = hcount + bondOrderSum;
			if (atomTypes[f].getBondOrderSum() - current > max)
			{
				max = atomTypes[f].getBondOrderSum() - current;
			}
		}
		return max;
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
    
	/**
	 * Saturates a molecule by setting appropriate bond orders.
	 *
	 * @keyword            bond order, calculation
     *
     * @created 2003-10-03
	 */
    public void newSaturate(AtomContainer atomContainer) throws CDKException {
        logger.info("Saturating atomContainer by adjusting bond orders...");
        boolean allSaturated = allSaturated(atomContainer);
        if (!allSaturated) {
            boolean succeeded = newSaturate(atomContainer.getBonds(), atomContainer);
            if (!succeeded) {
                throw new CDKException("Could not saturate this atomContainer!");
            }
        }
    }

    /**
     * Saturates a set of Bonds in an AtomContainer.
     */
    public boolean newSaturate(Bond[] bonds, AtomContainer atomContainer) throws CDKException {
        logger.debug("Saturating bond set of size: " + bonds.length);
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
                    logger.debug("Recursing with unsaturated bond with #bonds: " + leftBondCount);
                    bondsAreFullySaturated = newSaturate(leftBonds, atomContainer) 
                                             && !isUnsaturated(bond, atomContainer);
                } else {
                    bondsAreFullySaturated = false;
                }

                // ok, did it work? if not, saturate this bond, and recurse
                if (!bondsAreFullySaturated) {
                    logger.debug("First try did not work...");
                    // ok, revert saturating this bond, and recurse again
                    boolean couldSaturate = newSaturate(bond, atomContainer);
                    if (couldSaturate) {
                        if (leftBondCount > 0) {
                            logger.debug("Recursing with saturated bond with #bonds: " + leftBondCount);
                            bondsAreFullySaturated = newSaturate(leftBonds, atomContainer);
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
                    logger.debug("Recursing with #bonds: " + leftBondCount);
                    bondsAreFullySaturated = newSaturate(leftBonds, atomContainer);
                } else {
                    bondsAreFullySaturated = true;
                }
            } else {
                logger.debug("Cannot saturate this bond");
                // but, still recurse (if possible)
                if (leftBondCount > 0) {
                    logger.debug("Recursing with saturated bond with #bonds: " + leftBondCount);
                    bondsAreFullySaturated = newSaturate(leftBonds, atomContainer) 
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
    public boolean newSaturate(Bond bond, AtomContainer atomContainer) throws CDKException {
        Atom[] atoms = bond.getAtoms();
        Atom atom = atoms[0];
        Atom partner = atoms[1];
        logger.debug("  saturating bond: " + atom.getSymbol() + "-" + partner.getSymbol());
        AtomType[] atomTypes1 = structgenATF.getAtomTypes(atom.getSymbol());
        AtomType[] atomTypes2 = structgenATF.getAtomTypes(partner.getSymbol());
        boolean bondOrderIncreased = true;
        while (bondOrderIncreased && !isSaturated(bond, atomContainer)) {
            logger.debug("Can increase bond order");
            bondOrderIncreased = false;
            for (int atCounter1=0; atCounter1<atomTypes1.length&& !bondOrderIncreased; atCounter1++) {
                AtomType aType1 = atomTypes1[atCounter1];
                logger.debug("  condidering atom type: " + aType1);
                if (couldMatchAtomType(atomContainer, atom, aType1)) {
                    logger.debug("  trying atom type: " + aType1);
                    for (int atCounter2=0; atCounter2<atomTypes2.length && !bondOrderIncreased; atCounter2++) {
                        AtomType aType2 = atomTypes2[atCounter2];
                        logger.debug("  condidering partner type: " + aType1);
                        if (couldMatchAtomType(atomContainer, partner, atomTypes2[atCounter2])) {
                            logger.debug("    with atom type: " + aType2);
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
     * Determines if the atom can be of type AtomType.
     */
    public boolean couldMatchAtomType(AtomContainer atomContainer, Atom atom, AtomType atomType) {
        logger.debug("   ... matching atom " + atom.getSymbol() + " vs " + atomType);
        int neighbours = atomContainer.getConnectedAtoms(atom).length;
        if (atomContainer.getBondOrderSum(atom) + atom.getHydrogenCount() < atomType.getBondOrderSum()) {
           logger.debug("    Match!");
           return true;
        }
        logger.debug("    No Match");
        return false;
    }

    /**
     * The method is known to fail for certain compounds. For more information, see
     * cdk.test.limitations package.
     *
     * @see org.openscience.cdk.test.limitations.tools.SaturationCheckerTest
     */
    public void saturate(AtomContainer atomContainer) throws CDKException {
        /* newSaturate(atomContainer);
    }
    public void oldSaturate(AtomContainer atomContainer) throws CDKException { */
		Atom partner = null;
		Atom atom = null;
		Atom[] partners = null;
		AtomType[] atomTypes1 = null;
		AtomType[] atomTypes2 = null;
		Bond bond = null;
		for (int i = 1; i < 4; i++)
		{
			// handle atoms with degree 1 first and then proceed to higher order
			for (int f = 0; f < atomContainer.getAtomCount(); f++)
			{
				atom = atomContainer.getAtomAt(f);
				logger.debug("symbol: " + atom.getSymbol());
				atomTypes1 = structgenATF.getAtomTypes(atom.getSymbol());
        if(atomTypes1.length>0){
          logger.debug("first atom type: " + atomTypes1[0]);
          if (atomContainer.getBondCount(atom) == i)
          {
            if (atom.getFlag(CDKConstants.ISAROMATIC) && atomContainer.getBondOrderSum(atom) < atomTypes1[0].getBondOrderSum() - atom.getHydrogenCount()){
              partners = atomContainer.getConnectedAtoms(atom);
              for (int g = 0; g < partners.length; g++)
              {
                partner = partners[g];
                logger.debug("Atom has " + partners.length + " partners");
                atomTypes2 = structgenATF.getAtomTypes(partner.getSymbol());
                if(atomTypes2.length==0)
                  throw new CDKException("Missing entry in structgen_atomtypes.xml for "+partner.getSymbol());
                if (atomContainer.getBond(partner,atom).getFlag(CDKConstants.ISAROMATIC) && atomContainer.getBondOrderSum(partner) < atomTypes2[0].getBondOrderSum() - partner.getHydrogenCount())
                {
                  logger.debug("Partner has " + atomContainer.getBondOrderSum(partner) + ", may have: " + atomTypes2[0].getBondOrderSum());
                  bond = atomContainer.getBond(atom, partner);
                  logger.debug("Bond order was " + bond.getOrder());
                  bond.setOrder(bond.getOrder() + 1);
                  logger.debug("Bond order now " + bond.getOrder());
                  break;
                }
              }
            }
            if (atomContainer.getBondOrderSum(atom) < atomTypes1[0].getBondOrderSum() - atom.getHydrogenCount())
            {
              logger.debug("Atom has " + atomContainer.getBondOrderSum(atom) + ", may have: " + atomTypes1[0].getBondOrderSum());
              partners = atomContainer.getConnectedAtoms(atom);
              for (int g = 0; g < partners.length; g++)
              {
                partner = partners[g];
                logger.debug("Atom has " + partners.length + " partners");
                atomTypes2 = structgenATF.getAtomTypes(partner.getSymbol());
                if(atomTypes2.length==0)
                  throw new CDKException("Missing entry in structgen_atomtypes.xml for "+partner.getSymbol());
                if (atomContainer.getBondOrderSum(partner) < atomTypes2[0].getBondOrderSum() - partner.getHydrogenCount())
                {
                  logger.debug("Partner has " + atomContainer.getBondOrderSum(partner) + ", may have: " + atomTypes2[0].getBondOrderSum());
                  bond = atomContainer.getBond(atom, partner);
                  logger.debug("Bond order was " + bond.getOrder());
                  bond.setOrder(bond.getOrder() + 1);
                  logger.debug("Bond order now " + bond.getOrder());
                  break;
                }
              }
            }
          }
				}
			}
		}
    }
    
	public void saturateRingSystems(AtomContainer atomContainer) throws CDKException
	{
		RingSet rs = new SSSRFinder().findSSSR((Molecule)atomContainer);
		Vector ringSets = RingPartitioner.partitionRings(rs);
		AtomContainer ac = null;
		Atom atom = null;
		int temp[];
		for (int f = 0; f < ringSets.size(); f++)
		{
			rs = (RingSet)ringSets.elementAt(f);
			ac = rs.getRingSetInAtomContainer();
			temp = new int[ac.getAtomCount()];
			for (int g = 0; g < ac.getAtomCount(); g++)
			{
				atom = ac.getAtomAt(g);
				temp[g] = atom.getHydrogenCount();
				atom.setHydrogenCount(atomContainer.getBondCount(atom) - ac.getBondCount(atom) - temp[g]);
			}
			saturate(ac);
			for (int g = 0; g < ac.getAtomCount(); g++)
			{
				atom = ac.getAtomAt(g);
				atom.setHydrogenCount(temp[g]);
			}
			
		}
	}
	
	/*
	 * Recursivly fixes bond orders in a molecule for 
	 * which only connectivities but no bond orders are know.
	 *
	 *@ param  molecule  The molecule to fix the bond orders for
	 *@ param  bond      The number of the bond to treat in this recursion step
	 *@ return           true if the bond order which was implemented was ok.
	 */
	/*private boolean recursiveBondOrderFix(Molecule molecule, int bondNumber)
	{	

		Atom partner = null;
		Atom atom = null;
		Atom[] partners = null;
		AtomType[] atomTypes1 = null;
		AtomType[] atomTypes2 = null;
		int maxBondOrder = 0;
		int oldBondOrder = 0;
		if (bondNumber < molecule.getBondCount())
		{	
			Bond bond = molecule.getBondAt(f);
		}
		else 
		{
			return true;
		}
		atom = bond.getAtomAt(0);
		partner = bond.getAtomAt(1);
		atomTypes1 = atf.getAtomTypes(atom.getSymbol(), atf.ATOMTYPE_ID_STRUCTGEN);
		atomTypes2 = atf.getAtomTypes(partner.getSymbol(), atf.ATOMTYPE_ID_STRUCTGEN);
		maxBondOrder = Math.min(atomTypes1[0].getMaxBondOrder(), atomTypes2[0].getMaxBondOrder());
		for (int f = 1; f <= maxBondOrder; f++)
		{
			oldBondOrder = bond.getOrder()
			bond.setOrder(f);
			if (!isOverSaturated(atom, molecule) && !isOverSaturated(partner, molecule))
			{
				if (!recursiveBondOrderFix(molecule, bondNumber + 1)) break;
					
			}
			else
			{
				bond.setOrder(oldBondOrder);
				return false;	
			}
		}
		return true;
	}*/

	/**
	 * Calculate the number of missing hydrogens by substracting the number of
	 * bonds for the atom from the expected number of bonds. Charges are included
	 * in the calculation. The number of expected bonds is defined by the AtomType
	 * generated with the AtomTypeFactory.
	 *
	 * @param  atom      Description of the Parameter
	 * @param  container Description of the Parameter
	 * @return           Description of the Return Value
	 * @see              AtomTypeFactory
	 */
	public int calculateMissingHydrogen(Atom atom, AtomContainer container) throws CDKException {
        return calculateMissingHydrogen(atom, container, false);
    }
  
	/**
	 * Calculate the number of missing hydrogens by substracting the number of
	 * bonds for the atom from the expected number of bonds. Charges are included
	 * in the calculation. The number of expected bonds is defined by the AtomType
	 * generated with the AtomTypeFactory.
	 *
	 * @param  atom      Description of the Parameter
	 * @param  container  Description of the Parameter
	 * @param  throwExceptionForUnknowAtom  Should an exception be thrown if an unknown atomtype is found or 0 returned ?
	 * @return           Description of the Return Value
	 * @see              AtomTypeFactory
	 */
	public int calculateMissingHydrogen(Atom atom, AtomContainer container, boolean throwExceptionForUnknowAtom) throws CDKException {
        int missingHydrogen = 0;
        if (atom instanceof PseudoAtom) {
            // don't figure it out... it simply does not lack H's
        } else if (atom.getAtomicNumber() == 1 || atom.getSymbol().equals("H")) {
            missingHydrogen = (int) (1 - container.getBondOrderSum(atom) -
                    atom.getFormalCharge());
        } else {
            logger.info("Calculating number of missing hydrogen atoms");
            // get default atom
            AtomType[] atomTypes = structgenATF.getAtomTypes(atom.getSymbol());
            if(atomTypes.length==0 && throwExceptionForUnknowAtom)
              throw new CDKException("Missing entry in structgen_atomtypes.xml for "+atom.getSymbol());
            logger.debug("Found atomtypes: " + atomTypes.length);
            if (atomTypes.length > 0) {
                AtomType defaultAtom = atomTypes[0];
                logger.debug("DefAtom: " + defaultAtom.toString());
                missingHydrogen = (int) (defaultAtom.getBondOrderSum() -
                    container.getBondOrderSum(atom) +
                    atom.getFormalCharge());
                if (atom.getFlag(CDKConstants.ISAROMATIC)){
                    Bond[] connectedBonds=container.getConnectedBonds(atom);
                    boolean subtractOne=true;
                    for(int i=0;i<connectedBonds.length;i++){
                        if(connectedBonds[i].getOrder()==2 || connectedBonds[i].getOrder()==CDKConstants.BONDORDER_AROMATIC)
                            subtractOne=false;
                    }
                    if(subtractOne)
                        missingHydrogen--;
                }
                logger.debug("Atom: " + atom.getSymbol());
                logger.debug("  max bond order: " + defaultAtom.getBondOrderSum());
                logger.debug("  bond order sum: " + container.getBondOrderSum(atom));
                logger.debug("  charge        : " + atom.getFormalCharge());
            } else {
                logger.warn("Could not find atom type for " + atom.getSymbol());
            }
        }
        return missingHydrogen;
    }

}

