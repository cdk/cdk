/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.atomtype;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.tools.LoggingTool;

/**
 *@author         mfe4
 *@cdk.created    2004-12-02
 *@cdk.module     core
 */
public class HybridizationStateATMatcher implements AtomTypeMatcher {

	private LoggingTool logger;
	double charge = 0;
	int neighboorsCount = 0;

	double maxbondOrder = 0;
	double bondOrderSum = 0;

	double tmp_charge = 0;
	int tmp_neighboorsCount = 0;
	double tmp_maxbondOrder = 0;
	double tmp_bondOrderSum = 0;
	int hybr = 0;

	String symbol = null;
	String atName = null;
	private AtomTypeFactory factory = null;
	private AtomType[] type = null;


	/**
	 * Constructor for the HybridizationStateATMatcher object.
	 */
	public HybridizationStateATMatcher() {
		logger = new LoggingTool(this);
	}


	/**
	 * Assign the hybridization state to a given atom.
	 *
	 *@param  ac                AtomContainer
	 *@param  atom              the target atom
	 *@exception  CDKException  Description of the Exception
	 */
	public AtomType findMatchingAtomType(AtomContainer ac, Atom atom) throws CDKException {
		
		symbol = atom.getSymbol();
		//Hs are included?
		Atom[] neighboors = ac.getConnectedAtoms(atom);
		charge = atom.getFormalCharge();
		neighboorsCount = neighboors.length;
		bondOrderSum = ac.getBondOrderSum(atom);
		maxbondOrder = ac.getMaximumBondOrder(atom);
		try {
			factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/hybridization_atomtypes.xml");

			// take the array of atom types for the given element...
			type = factory.getAtomTypes(symbol);

			// ...and then search the exact atom type with these parameters
			logger.debug("My ATOM TYPE "+symbol+" "+bondOrderSum+" "+maxbondOrder+" "+neighboorsCount);
			for (int i = 0; i < type.length; i++) {
				tmp_maxbondOrder = type[i].getMaxBondOrder();
				tmp_bondOrderSum = type[i].getBondOrderSum();
				tmp_neighboorsCount = type[i].getFormalNeighbourCount();
				tmp_charge = type[i].getFormalCharge();
				logger.debug(i + "ATOM TYPE " + tmp_bondOrderSum + " " + tmp_maxbondOrder + " " + tmp_neighboorsCount);
				if (tmp_maxbondOrder == maxbondOrder && tmp_bondOrderSum == bondOrderSum) {
					if (tmp_neighboorsCount == neighboorsCount) {
						logger.debug("!!!!! ATOM TYPE FOUND");
						atName = type[i].getAtomTypeName();
						return type[i];
					}
				}
			}
		} catch (Exception ex1) {
            logger.error(ex1.getMessage());
			logger.debug(ex1);
			throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString());
		}
		return null;
	}
}

