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

import org.openscience.cdk.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.exception.CDKException;
import java.util.Vector;
import java.io.*;

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
 * @created    2004-01-07
 *
 * @keyword    atom, valency
 */
public class ValencyChecker {

	private AtomTypeFactory structgenATF;

	private LoggingTool logger;

	public ValencyChecker() throws IOException, ClassNotFoundException {
		structgenATF = AtomTypeFactory.getInstance(
            "org/openscience/cdk/config/valency_atomtypes.xml"
        );
		logger = new LoggingTool(this.getClass().getName());
	}

    /**
     * Checks wether an Atom is saturated by comparing it with known AtomTypes.
     */
	public boolean isSaturated(Atom atom, AtomContainer container) throws CDKException {
		AtomType[] atomTypes = structgenATF.getAtomTypes(atom.getSymbol());
        if(atomTypes.length==0)
            throw new CDKException("Missing entry in valency_atomtypes.xml for "+atom.getSymbol());
        double bondOrderSum = container.getBondOrderSum(atom);
        double maxBondOrder = container.getMaximumBondOrder(atom);
        int hcount = atom.getHydrogenCount();
        int charge = atom.getFormalCharge();

        logger.debug("Checking saturation of atom " + atom.getSymbol());
        logger.debug("bondOrderSum: " + bondOrderSum);
        logger.debug("maxBondOrder: " + maxBondOrder);
        logger.debug("hcount: " + hcount);
        logger.debug("charge: " + charge);

        for (int f = 0; f < atomTypes.length; f++) {
            AtomType type = atomTypes[f];
            if (charge == type.getFormalCharge()) {
                if (bondOrderSum + hcount == type.getBondOrderSum() && 
                    maxBondOrder <= type.getMaxBondOrder()) {
                    logger.debug("We have a match!");
                    return true;
                }
            } // else: formal charges don't match
        }
        
        logger.debug("No, atom is not saturated.");
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
        int missingHydrogen = 0;
        if (atom instanceof PseudoAtom) {
            logger.debug("don't figure it out... it simply does not lack H's");
            return 0;
        }
        
        logger.info("Calculating number of missing hydrogen atoms");
        // get default atom
        AtomType[] atomTypes = structgenATF.getAtomTypes(atom.getSymbol());
        if (atomTypes.length == 0) {
            throw new CDKException("Missing entry in valency_atomtypes.xml for "+atom.getSymbol());
        }

        double bondOrderSum = container.getBondOrderSum(atom);
        double maxBondOrder = container.getMaximumBondOrder(atom);
        int hcount = atom.getHydrogenCount();
        int charge = atom.getFormalCharge();
        
        logger.debug("Found atomtypes: " + atomTypes.length);
        for (int f = 0; f < atomTypes.length; f++) {
            AtomType type = atomTypes[f];
            if (charge == type.getFormalCharge()) {
                if (bondOrderSum + hcount <= type.getBondOrderSum() && 
                    maxBondOrder <= type.getMaxBondOrder()) {
                    missingHydrogen = (int) (type.getBondOrderSum() -
                        container.getBondOrderSum(atom));
                }
            } // else: formal charges don't match
        }
        
        return missingHydrogen;
    }
    
}

