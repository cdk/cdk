/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2008  Egon Willighagen <egonw@users.sf.net>
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

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * AtomTypeMatcher that finds an AtomType by matching the Atom's element symbol.
 * This atom type matcher takes into account formal charge and number of
 * implicit hydrogens, and requires bond orders to be given.
 *
 * <p>This class uses the <b>cdk/config/data/structgen_atomtypes.xml</b> 
 * list. If there is not an atom type defined for the tested atom, then null 
 * is returned.
 *
 * @author         egonw
 * @cdk.created    2006-09-22
 * @cdk.module     structgen
 * @cdk.svnrev  $Revision: 9162 $
 */
public class StructGenAtomTypeGuesser implements IAtomTypeGuesser {

	private static AtomTypeFactory factory = null;
    private LoggingTool logger;
    
	/**
	 * Constructor for the StructGenMatcher object.
	 */
	public StructGenAtomTypeGuesser() {
		logger = new LoggingTool(this);
	}


	/**
	 * Finds the AtomType matching the Atom's element symbol, formal charge and 
     * hybridization state.
	 *
	 * @param  atomContainer  AtomContainer
	 * @param  atom            the target atom
	 * @exception CDKException Exception thrown if something goed wrong
	 * @return                 the matching AtomType
	 */
	public List<IAtomType> possibleAtomTypes(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (factory == null) {
            try {
                factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/structgen_atomtypes.xml",
                          atom.getBuilder());
            } catch (Exception ex1) {
                logger.error(ex1.getMessage());
                logger.debug(ex1);
                throw new CDKException("Could not instantiate the AtomType list!", ex1);
            }
        }

		double bondOrderSum = atomContainer.getBondOrderSum(atom);
		IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
		int charge = atom.getFormalCharge();
		int hcount = atom.getHydrogenCount();

		List<IAtomType> matchingTypes = new ArrayList<IAtomType>();
        IAtomType[] types = factory.getAtomTypes(atom.getSymbol());
        for (IAtomType type : types) {
            logger.debug("   ... matching atom ", atom, " vs ", type);
            if (bondOrderSum - charge + hcount <= type.getBondOrderSum() &&
                !BondManipulator.isHigherOrder(maxBondOrder, type.getMaxBondOrder())) {
                matchingTypes.add(type);
            }
        }
        logger.debug("    No Match");
        
        return matchingTypes;
	}
}

