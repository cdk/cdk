/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, version 2.1.
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
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.BondManipulator;

import java.util.List;

/**
 * AtomTypeMatcher that finds an AtomType by matching the Atom's element symbol,
 * formal charge and hybridization state.
 * <p/>
 * <p>This class uses the <b>cdk/config/data/hybridization_atomtypes.xml</b>
 * list. If there is not an atom type defined for the tested atom, then null
 * is returned.
 *
 * @author egonw
 * @cdk.created 2005-04-15
 * @cdk.module atomtype
 * @cdk.svnrev  $Revision$
 */
public class HybridizationMatcher implements IAtomTypeMatcher {

    private static AtomTypeFactory factory = null;
    private LoggingTool logger;

    /**
     * Constructor for the HybridizationMatcher object.
     */
    public HybridizationMatcher() {
        logger = new LoggingTool(this);
    }


    /**
     * Finds the AtomType matching the Atom's element symbol, formal charge and
     * hybridization state.
     *
     * @param atomContainer AtomContainer
     * @param atom          the target atom
     * @return the matching AtomType
     * @throws CDKException Exception thrown if something good wrong
     */
    public IAtomType findMatchingAtomType(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (factory == null) {
            try {
                factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/hybridization_atomtypes.xml",
                        atom.getBuilder());
            } catch (Exception ex1) {
                logger.error(ex1.getMessage());
                logger.debug(ex1);
                throw new CDKException("Could not instantiate the AtomType list!", ex1);
            }
        }

        IAtomType[] types = factory.getAtomTypes(atom.getSymbol());
        for (IAtomType type : types) {
            logger.debug("   ... matching atom ", atom, " vs ", type);

            // if the symbol doesn't match no use carrying on
            if (!atom.getSymbol().equals(type.getSymbol())) continue;

            // if formal charge doesn't match, no use carrying on
            int formalCharge = atom.getFormalCharge();
            if (formalCharge != type.getFormalCharge()) {
                logger.debug("      formal charge does not match.");
                continue;
            }

            // lets see if we can get by with the stored hybridization
            if (atom.getHybridization() != CDKConstants.UNSET) {
                if (atom.getHybridization() == type.getHybridization()) {
                    logger.debug("     hybridization is OK... We have a match!");
                    return type;
                } else {
                    logger.debug("     hybridization does not match");
                    continue;
                }
            }

            // since the hybridization state is not available, we will
            // match against the formal charge, max bond order, neighbor count
            // and symbol and take the matching types hybridization state

            // lets first get the max bond order
            logger.debug("      Evaluating hybridization state");
            List<IBond> connectedBonds = atomContainer.getConnectedBondsList(atom);
            IBond.Order maxBondOrder = BondManipulator.getMaximumBondOrder(connectedBonds);
            double bondOrderSum = BondManipulator.getSingleBondEquivalentSum(connectedBonds);

            // in case the atom has all implicit hydrogens and no explicit bonds,
            // then we can return a max bond order of 1
            if (maxBondOrder == null && atom.getHydrogenCount() != CDKConstants.UNSET) {
                maxBondOrder = IBond.Order.SINGLE;
            }
            // also count the implicit hydrogens with the bond order sum
            if (atom.getHydrogenCount() != CDKConstants.UNSET) {
            	bondOrderSum += atom.getHydrogenCount();
            }



            // we should check max bond order as well, since double bonded
            // carbons have the same state as aromatic carbons
            if (bondOrderSum == type.getBondOrderSum()&& maxBondOrder == type.getMaxBondOrder()) {
                logger.debug("     Match found. Setting the state");
                // set the state on the atom
                atom.setHybridization(type.getHybridization());
                return type;
            } else {
                logger.debug("     No match found.");
            }
        }

        return null;
    }
}

