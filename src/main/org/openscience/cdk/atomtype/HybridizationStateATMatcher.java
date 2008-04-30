/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2007  Matteo Floris <mfe4@users.sf.net>
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
 *
 */
package org.openscience.cdk.atomtype;

import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.LoggingTool;

/**
 * AtomType matcher that deduces the hybridization state of an atom based on
 * the max bond order, bond order sum and neighbor count properties of the Atom.
 *
 * <p>This class uses the <b>cdk/config/data/hybridization_atomtypes.xml</b> 
 * list. If there is not an atom type defined for the tested atom, then null 
 * is returned.
 *
 * @author         mfe4
 * @cdk.created    2004-12-02
 * @cdk.module     extra
 * @cdk.svnrev     $Revision$
 * @cdk.bug        1713398
 * @cdk.bug        1749179
 */
public class HybridizationStateATMatcher implements IAtomTypeMatcher {

    private LoggingTool logger;
    double charge = 0;
    int neighboorsCount = 0;

    IBond.Order maxbondOrder = IBond.Order.SINGLE;
    double bondOrderSum = 0;

    int hybr = 0;

    String symbol = null;
    String atName = null;
    private AtomTypeFactory factory = null;
    private IAtomType[] type = null;


    /**
     * Constructor for the HybridizationStateATMatcher object.
     */
    public HybridizationStateATMatcher() {
        logger = new LoggingTool(this);
    }


    /**
     * Assign the hybridization state to a given atom.
     *
     * @param  atomContainer  The AtomContainer in which we should look for the given atom
     * @param  atom   The atom whose type we are looking for
     * @exception CDKException Description of the Exception
     * @return                 the matching AtomType
     */
    public IAtomType findMatchingAtomType(IAtomContainer atomContainer, IAtom atom) throws CDKException {

        symbol = atom.getSymbol();
        //Hs are included?
        java.util.List neighboors = atomContainer.getConnectedAtomsList(atom);
        charge = atom.getFormalCharge();
        neighboorsCount = neighboors.size();
        bondOrderSum = atomContainer.getBondOrderSum(atom);
        maxbondOrder = atomContainer.getMaximumBondOrder(atom);
        try {
            factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/hybridization_atomtypes.xml",
                atom.getBuilder());

            // take the array of atom types for the given element...
            type = factory.getAtomTypes(symbol);

            // ...and then search the exact atom type with these parameters
            logger.debug("My ATOM TYPE "+symbol+" "+bondOrderSum+" "+maxbondOrder+" "+neighboorsCount);
            int tmpNeighboorsCount = 0;
            IBond.Order tmpMaxbondOrder = IBond.Order.SINGLE;
            double tmpBondOrderSum = 0;
            for (int i = 0; i < type.length; i++) {
                tmpMaxbondOrder = type[i].getMaxBondOrder();
                tmpBondOrderSum = type[i].getBondOrderSum();
                tmpNeighboorsCount = type[i].getFormalNeighbourCount();
                logger.debug(i + "ATOM TYPE " + tmpBondOrderSum + " " + tmpMaxbondOrder + " " + tmpNeighboorsCount);
                if (tmpMaxbondOrder == maxbondOrder && tmpBondOrderSum == bondOrderSum) {
                    //if (tmpNeighboorsCount == neighboorsCount) {
                    logger.debug("!!!!! ATOM TYPE FOUND");
                    atName = type[i].getAtomTypeName();
                    return type[i];
                    // }
                }
            }
        } catch (Exception ex1) {
            logger.error(ex1.getMessage());
            logger.debug(ex1);
            throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString(), ex1);
        }
        return null;
    }
}

