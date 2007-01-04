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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.LoggingTool;

/**
 * AtomTypeMatcher that finds an AtomType by matching the Atom's element symbol,
 * formal charge and hybridization state.
 *
 * <p>This class uses the <b>cdk/config/data/hybridization_atomtypes.xml</b> 
 * list. If there is not an atom type defined for the tested atom, then null 
 * is returned.
 *
 * @author         egonw
 * @cdk.created    2005-04-15
 * @cdk.module     core
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
	 * @param  atomContainer  AtomContainer
	 * @param  atom            the target atom
	 * @exception CDKException Exception thrown if something goed wrong
	 * @return                 the matching AtomType
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
        for (int i=0; i<types.length; i++) {
            IAtomType type = types[i];
            logger.debug("   ... matching atom ", atom, " vs ", type);
            int charge = atom.getFormalCharge();
            if (charge == type.getFormalCharge()) {
                logger.debug("     formal charge matches...");
                if (atom.getHybridization() == type.getHybridization()) {
                    logger.debug("     hybridization is OK... We have a match!");
                    return type;
                }
            } else {
                logger.debug("     formal charge does NOT match...");
            }
        }
        logger.debug("    No Match");
        
        return null;
	}
}

