/* $Revision: 5855 $ $Author: egonw $ $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 *
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Atom Type matcher... TO BE WRITTEN.
 *
 * <p>This class uses the <b>cdk/config/data/cdk_atomtypes.xml</b> 
 * list. If there is not an atom type defined for the tested atom, then NULL 
 * is returned.
 *
 * @author         egonw
 * @cdk.created    2007-07-20
 * @cdk.module     core
 */
public class CDKAtomTypeMatcher implements IAtomTypeMatcher {

	private AtomTypeFactory factory;
	
    private static Map<IChemObjectBuilder,CDKAtomTypeMatcher> factories = new Hashtable<IChemObjectBuilder,CDKAtomTypeMatcher>(3); 
    // private static LoggingTool logger = new LoggingTool(CDKAtomTypeMatcher.class);
    
    private CDKAtomTypeMatcher(IChemObjectBuilder builder) {
    	factory = AtomTypeFactory.getInstance(
			"org/openscience/cdk/config/data/cdk_atomtypes.xml",
			builder
		);
    }
    
    public static CDKAtomTypeMatcher getInstance(IChemObjectBuilder builder) {
    	if (!factories.containsKey(builder))
    		factories.put(builder, new CDKAtomTypeMatcher(builder));
    	return factories.get(builder);
    }
    
    public IAtomType findMatchingAtomType(IAtomContainer atomContainer, IAtom atom)
        throws CDKException {
        IAtomType type = null;
        type = perceiveCarbons(atomContainer, atom);
        if (type == null) type = perceiveOxygens(atomContainer, atom);
        if (type == null) type = perceiveNitrogens(atomContainer, atom);
        if (type == null) type = perceiveSulphurs(atomContainer, atom);
        return type;
    }
    
    private IAtomType perceiveCarbons(IAtomContainer atomContainer, IAtom atom)
    	throws CDKException {
    	if ("C".equals(atom.getSymbol())) {
    		// if hybridization is given, use that
    		if (atom.getHybridization() != CDKConstants.UNSET) {
    			if (atom.getHybridization() == CDKConstants.HYBRIDIZATION_SP2) {
        			return factory.getAtomType("C.sp2");
    			} else if (atom.getHybridization() == CDKConstants.HYBRIDIZATION_SP3) {
    				return factory.getAtomType("C.sp3");
    			}
    		} else if (atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() != 0) {
    			// FIXME: I don't perceive charged atoms yet
    			return null;
    		} else if (atomContainer.getConnectedBondsCount(atom) > 4) {
    			// FIXME: I don't perceive carbons with more than 4 connections yet
    			return null;
    		} else { // OK, use bond order info
    			double maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    			if (maxBondOrder > CDKConstants.BONDORDER_TRIPLE) {
    				// WTF??
    				return null;
    			} else if (maxBondOrder == CDKConstants.BONDORDER_TRIPLE) {
    				return factory.getAtomType("C.sp");
    			} else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    				// OK, one or two double bonds?
    				Iterator<IBond> bonds = atomContainer.getConnectedBondsList(atom).iterator();
    				int doubleBondCount = 0;
    				while (bonds.hasNext()) {
    					if (bonds.next().getOrder() == CDKConstants.BONDORDER_DOUBLE)
    						doubleBondCount++;
    				}
    				if (doubleBondCount == 2) {
    					return factory.getAtomType("C.sp");
    				} else if (doubleBondCount == 1) {
    					return factory.getAtomType("C.sp2");
    				}
    			} else {
    				return factory.getAtomType("C.sp3");
    			}
    		}
    	}
    	return null;
    }
    
    private IAtomType perceiveOxygens(IAtomContainer atomContainer, IAtom atom)
    	throws CDKException {
    	if ("O".equals(atom.getSymbol())) {
    		// if hybridization is given, use that
    		if (atom.getHybridization() != CDKConstants.UNSET) {
    			if (atom.getHybridization() == CDKConstants.HYBRIDIZATION_SP2) {
    				return factory.getAtomType("O.sp2");
    			} else if (atom.getHybridization() == CDKConstants.HYBRIDIZATION_SP3) {
    				return factory.getAtomType("O.sp3");
    			}
    		} else if (atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() != 0) {
    			// FIXME: I don't perceive charged atoms yet
    			return null;
    		} else if (atomContainer.getConnectedBondsCount(atom) > 2) {
    			// FIXME: I don't perceive carbons with more than 4 connections yet
    			return null;
    		} else { // OK, use bond order info
    			double maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    			if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    				return factory.getAtomType("O.sp2");
    			} else {
    				return factory.getAtomType("O.sp3");
    			}
    		}
    	}
    	return null;
    }

    private IAtomType perceiveNitrogens(IAtomContainer atomContainer, IAtom atom)
    	throws CDKException {
    	if ("N".equals(atom.getSymbol())) {
    		// if hybridization is given, use that
    		if (atom.getHybridization() != CDKConstants.UNSET) {
    			if (atom.getHybridization() == CDKConstants.HYBRIDIZATION_SP1) {
    				return factory.getAtomType("N.sp1");
    			} else if (atom.getHybridization() == CDKConstants.HYBRIDIZATION_SP2) {
    				return factory.getAtomType("N.sp2");
    			} else if (atom.getHybridization() == CDKConstants.HYBRIDIZATION_SP3) {
    				return factory.getAtomType("N.sp3");
    			}
    		} else if (atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() != 0) {
    			// FIXME: I don't perceive charged atoms yet
    			return null;
    		} else if (atomContainer.getConnectedBondsCount(atom) > 3) {
    			// FIXME: I don't perceive carbons with more than 3 connections yet
    			return null;
    		} else { // OK, use bond order info
    			double maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    			if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    				return factory.getAtomType("N.sp3");
    			} else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    				return factory.getAtomType("N.sp2");
    			} else if (maxBondOrder == CDKConstants.BONDORDER_TRIPLE) {
    				return factory.getAtomType("N.sp1");
    			}
    		}
    	}
    	return null;
    }

    private IAtomType perceiveSulphurs(IAtomContainer atomContainer, IAtom atom)
    	throws CDKException {
    	if ("S".equals(atom.getSymbol())) {
    		int neighborcount = atomContainer.getConnectedBondsCount(atom);
    		if (neighborcount == 2) {
    			return factory.getAtomType("S.3");
    		}
    	}
    	return null;
    }
}

