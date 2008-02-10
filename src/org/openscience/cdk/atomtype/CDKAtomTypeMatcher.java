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
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;

/**
 * Atom Type matcher... TO BE WRITTEN.
 *
 * <p>This class uses the <b>cdk/config/data/cdk_atomtypes.xml</b> 
 * list. If there is not an atom type defined for the tested atom, then NULL 
 * is returned.
 *
 * @author         egonw
 * @cdk.created    2007-07-20
 * @cdk.module     atomtype
 * @cdk.svnrev     $Revision: 9162 $
 * @cdk.bug        1802998
 * @cdk.bug        1851197
 */
public class CDKAtomTypeMatcher implements IAtomTypeMatcher {

	public final static int REQUIRE_NOTHING = 1;
	public final static int REQUIRE_EXPLICIT_HYDROGENS = 2;
	
	private AtomTypeFactory factory;
	private int mode;
	
    private static Map<Integer,Map<IChemObjectBuilder,CDKAtomTypeMatcher>> 
    	factories = new Hashtable<Integer,Map<IChemObjectBuilder,CDKAtomTypeMatcher>>(1); 
    // private static LoggingTool logger = new LoggingTool(CDKAtomTypeMatcher.class);
    
    private CDKAtomTypeMatcher(IChemObjectBuilder builder, int mode) {
    	factory = AtomTypeFactory.getInstance(
			"org/openscience/cdk/config/data/cdk_atomtypes.xml",
			builder
		);
    	this.mode = mode;
    }
    
    public static CDKAtomTypeMatcher getInstance(IChemObjectBuilder builder) {
        return getInstance(builder, REQUIRE_NOTHING);
    }

    public static CDKAtomTypeMatcher getInstance(IChemObjectBuilder builder, int mode) {
    	if (!factories.containsKey(mode))
    		factories.put(mode, new Hashtable<IChemObjectBuilder,CDKAtomTypeMatcher>(1));
    	if (!factories.get(mode).containsKey(builder))
    		factories.get(mode).put(builder, new CDKAtomTypeMatcher(builder, mode));
    	return factories.get(mode).get(builder);
    }
    
    public IAtomType findMatchingAtomType(IAtomContainer atomContainer, IAtom atom)
        throws CDKException {
        IAtomType type = null;
        if (atom instanceof IPseudoAtom) {
        	return factory.getAtomType("X");
        }
        type = perceiveCarbons(atomContainer, atom);
        if (type == null) type = perceiveLithium(atomContainer, atom);
        if (type == null) type = perceiveOxygens(atomContainer, atom);
        if (type == null) type = perceiveNitrogens(atomContainer, atom);
        if (type == null) type = perceiveHydrogens(atomContainer, atom);
        if (type == null) type = perceiveSulphurs(atomContainer, atom);
        if (type == null) type = perceiveHalogens(atomContainer, atom);
        if (type == null) type = perceivePhosphors(atomContainer, atom);
        if (type == null) type = perceiveCommonSalts(atomContainer, atom);
        if (type == null) type = perceiveSilicon(atomContainer, atom);
        if (type == null) type = perceiveOrganometallicCenters(atomContainer, atom);
        if (type == null) type = perceiveNobelGases(atomContainer, atom);
        if (type == null) type = perceiveBorons(atomContainer, atom);
        if (type == null) type = perceiveBeryllium(atomContainer, atom);
        return type;
    }
    
    private IAtomType perceiveBorons(IAtomContainer atomContainer, IAtom atom)
		throws CDKException {
    	if ("B".equals(atom.getSymbol())) {
    		IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    		if (atom.getFormalCharge() != CDKConstants.UNSET &&
    			atom.getFormalCharge() != 0) {
    			if (atom.getFormalCharge() == -1 &&
    				maxBondOrder == IBond.Order.SINGLE &&
    				atomContainer.getConnectedAtomsCount(atom) <= 4) {
    				IAtomType type = getAtomType("B.minus");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		} else if (atomContainer.getConnectedAtomsCount(atom) <= 3) {
    			IAtomType type = getAtomType("B");
				if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	}
    	return null;
    }

    private IAtomType perceiveBeryllium(IAtomContainer atomContainer, IAtom atom)
    	throws CDKException {
	if ("Be".equals(atom.getSymbol())) {
		IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
		if (atom.getFormalCharge() != CDKConstants.UNSET &&
			atom.getFormalCharge() != 0) {
			if (atom.getFormalCharge() == -2 &&
				maxBondOrder == IBond.Order.SINGLE &&
				atomContainer.getConnectedAtomsCount(atom) <= 4) {
				IAtomType type = getAtomType("Be.2minus");
				if (isAcceptable(atom, atomContainer, type)) return type;
			}
		}
	}
	return null;
}

	private IAtomType perceiveCarbons(IAtomContainer atomContainer, IAtom atom)
    	throws CDKException {
    	if ("C".equals(atom.getSymbol())) {
    		// if hybridization is given, use that
    		if (atom.getHybridization() != CDKConstants.UNSET &&
    			(atom.getFormalCharge() == CDKConstants.UNSET || atom.getFormalCharge() == 0)) {
    			if (atom.getHybridization() == Hybridization.SP2) {
    				IAtomType type = getAtomType("C.sp2");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atom.getHybridization() == Hybridization.SP3) {
    				IAtomType type = getAtomType("C.sp3");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atom.getHybridization() == Hybridization.SP1) {
    				IAtomType type = getAtomType("C.sp");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		} else if (atom.getFlag(CDKConstants.ISAROMATIC)) {
    			IAtomType type = getAtomType("C.sp2");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		} else if (atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() != 0) {
    			if (atom.getFormalCharge() == 1) {
    				if (atomContainer.getConnectedBondsCount(atom) == 0) {
    					IAtomType type = getAtomType("C.plus.sp2");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				} else {
    					IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    					if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    						IAtomType type = getAtomType("C.plus.sp2");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					} else if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    						IAtomType type = getAtomType("C.plus.planar");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					} 
    				}
    			} else if (atom.getFormalCharge() == -1) {
    				IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    				if (maxBondOrder == CDKConstants.BONDORDER_SINGLE &&
    						atomContainer.getConnectedBondsCount(atom) <= 3) {
    					if (isRingAtom(atom, atomContainer)) {
    						boolean bothNeighborsSP2 = true;
    						Iterator<IAtom> atoms = atomContainer.getConnectedAtomsList(atom).iterator();
    						while (atoms.hasNext() && bothNeighborsSP2) {
    							IAtom nextAtom = atoms.next();
    							if (!nextAtom.getSymbol().equals("H")) {
    								if (nextAtom.getHybridization() != CDKConstants.UNSET && 
    										nextAtom.getHybridization() != Hybridization.SP2 && 
    										countAttachedDoubleBonds(atomContainer, nextAtom) > 0) {
    									bothNeighborsSP2 = false;
    								}
    							}
    						}
    						if (bothNeighborsSP2) {
    							IAtomType type = getAtomType("C.minus.planar");
        						if (isAcceptable(atom, atomContainer, type)) return type;
    						}
    					}
    					IAtomType type = getAtomType("C.minus.sp3");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				} else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE &&
    						atomContainer.getConnectedBondsCount(atom) <= 3) {
    					IAtomType type = getAtomType("C.minus.sp2");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				} else if (maxBondOrder == CDKConstants.BONDORDER_TRIPLE &&
    						atomContainer.getConnectedBondsCount(atom) <= 1) {
    					IAtomType type = getAtomType("C.minus.sp1");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				}
    			}
    			return null;
    		} else if (atomContainer.getConnectedBondsCount(atom) > 4) {
    			// FIXME: I don't perceive carbons with more than 4 connections yet
    			return null;
    		} else { // OK, use bond order info
    			IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    			if (maxBondOrder == IBond.Order.QUADRUPLE) {
    				// WTF??
    				return null;
    			} else if (maxBondOrder == CDKConstants.BONDORDER_TRIPLE) {
    				IAtomType type = getAtomType("C.sp");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    				// OK, one or two double bonds?
    				Iterator<IBond> bonds = atomContainer.getConnectedBondsList(atom).iterator();
    				int doubleBondCount = 0;
    				while (bonds.hasNext()) {
    					if (bonds.next().getOrder() == CDKConstants.BONDORDER_DOUBLE)
    						doubleBondCount++;
    				}
    				if (doubleBondCount == 2) {
    					IAtomType type = getAtomType("C.sp");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				} else if (doubleBondCount == 1) {
    					IAtomType type = getAtomType("C.sp2");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				}
    			} else {
    				IAtomType type = getAtomType("C.sp3");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		}
    	}
    	return null;
    }

    private IAtomType perceiveOxygens(IAtomContainer atomContainer, IAtom atom)
    throws CDKException {
    	if ("O".equals(atom.getSymbol())) {
    		// if hybridization is given, use that
    		if (atom.getHybridization() != CDKConstants.UNSET &&
    				(atom.getFormalCharge() == CDKConstants.UNSET ||
    						atom.getFormalCharge() == 0)) {
    			if (atom.getHybridization() == Hybridization.SP2) {
    				IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    				if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    					IAtomType type = getAtomType("O.sp2");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				} else if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    					IAtomType type = getAtomType("O.planar3");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				}    				
    			} else if (atom.getHybridization() == Hybridization.SP3) {
    				IAtomType type = getAtomType("O.sp3");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atom.getHybridization() == Hybridization.PLANAR3) {
    				IAtomType type = getAtomType("O.planar3");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		} else if (atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() != 0) {
    			if (atom.getFormalCharge() == -1 &&
    					atomContainer.getConnectedAtomsCount(atom) <= 1) {
    				IAtomType type = getAtomType("O.minus");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atom.getFormalCharge() == -2 &&
    					atomContainer.getConnectedAtomsCount(atom) == 0) {
    				IAtomType type = getAtomType("O.minus2");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atom.getFormalCharge() == +1) {
    				if (atomContainer.getConnectedBondsCount(atom) == 0) {
    					IAtomType type = getAtomType("O.plus");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				}
    				IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    				if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    					IAtomType type = getAtomType("O.plus.sp2");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				} else if (maxBondOrder == CDKConstants.BONDORDER_TRIPLE) {
    					IAtomType type = getAtomType("O.plus.sp1");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				} else {
    					IAtomType type = getAtomType("O.plus");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				}
    			}
    			return null;
    		} else if (atomContainer.getConnectedBondsCount(atom) > 2) {
    			// FIXME: I don't perceive carbons with more than 4 connections yet
    			return null;
    		} else if (atomContainer.getConnectedBondsCount(atom) == 0) {
    			IAtomType type = getAtomType("O.sp3");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		} else { // OK, use bond order info
    			IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    			if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    				IAtomType type = getAtomType("O.sp2");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    				int explicitHydrogens = countExplicitHydrogens(atom, atomContainer);
    				int connectedHeavyAtoms = atomContainer.getConnectedBondsCount(atom) - explicitHydrogens; 
    				if (connectedHeavyAtoms == 2) {
    					// a O.sp3 which is expected to take part in an aromatic system
    					if (isRingAtom(atom, atomContainer) && bothNeighborsAreSp2(atom, atomContainer)) {
    						IAtomType type = getAtomType("O.planar3");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					}
    					IAtomType type = getAtomType("O.sp3");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				} else {
    					IAtomType type = getAtomType("O.sp3");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				}
    			}
    		}
    	}
    	return null;
    }

    private boolean atLeastTwoNeighborsAreSp2(IAtom atom, IAtomContainer atomContainer) {
    	int count = 0;
    	Iterator<IAtom> atoms = atomContainer.getConnectedAtomsList(atom).iterator();
    	while (atoms.hasNext() && (count < 2)) {
    		IAtom nextAtom = atoms.next();
    		if (!nextAtom.getSymbol().equals("H")) {
    			if (nextAtom.getHybridization() != CDKConstants.UNSET &&
    					nextAtom.getHybridization() == Hybridization.SP2) {
    				// OK, it's SP2
    				count++;
    			} else if (countAttachedDoubleBonds(atomContainer, nextAtom) > 0) {
    				// OK, it's SP2
    				count++;
    			} // OK, not SP2
    		}
    	}
    	return count >= 2;
    }

    private boolean bothNeighborsAreSp2(IAtom atom, IAtomContainer atomContainer) {       
    	return atLeastTwoNeighborsAreSp2(atom, atomContainer);
    }

    private IAtomType perceiveNitrogens(IAtomContainer atomContainer, IAtom atom)
    throws CDKException {
    	if ("N".equals(atom.getSymbol())) {
    		// if hybridization is given, use that
    		if (atom.getHybridization() != CDKConstants.UNSET &&
    				(atom.getFormalCharge() == CDKConstants.UNSET ||
    						atom.getFormalCharge() == 0)) {
    			if (atom.getHybridization() == Hybridization.SP1) {
    				IAtomType type = getAtomType("N.sp1");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atom.getHybridization() == Hybridization.SP2) {
    				// but an sp2 hyb N might N.sp2 or N.planar3 (pyrrole), so check for the latter
    				int hcount = atom.getHydrogenCount() == null ? 0 : atom.getHydrogenCount();
    				if (isRingAtom(atom, atomContainer) &&
    						atomContainer.getConnectedAtomsCount(atom) + hcount == 3 &&
    						bothNeighborsAreSp2(atom, atomContainer)) {
    					IAtomType type = getAtomType("N.planar3");
        				if (isAcceptable(atom, atomContainer, type)) return type;
    				}
    				IAtomType type = getAtomType("N.sp2");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atom.getHybridization() == Hybridization.SP3) {
    				IAtomType type = getAtomType("N.sp3");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		} else if (atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() != 0) {
    			if (atom.getFormalCharge() == 1) {
    				IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    				if (maxBondOrder == CDKConstants.BONDORDER_SINGLE ||
    						atomContainer.getConnectedBondsCount(atom) == 0) {
    					if (atom.getHybridization() != CDKConstants.UNSET &&
    							atom.getHybridization() == IAtomType.Hybridization.SP2) {
    						IAtomType type = getAtomType("N.plus.sp2");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					}
    					IAtomType type = getAtomType("N.plus");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				} else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    					int doubleBonds= countAttachedDoubleBonds(atomContainer, atom);
    					if (doubleBonds == 1) {
    						IAtomType type = getAtomType("N.plus.sp2");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					} else if (doubleBonds == 2) {
    						IAtomType type = getAtomType("N.plus.sp1");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					}
    				} else if (maxBondOrder == CDKConstants.BONDORDER_TRIPLE) {
    					if (atomContainer.getConnectedBondsCount(atom) == 2) {
    						IAtomType type = getAtomType("N.plus.sp1");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					}
    				}
    			} else if (atom.getFormalCharge() == -1) {
    				IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    				if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    					if (atomContainer.getConnectedBondsCount(atom) <= 2) {
    						IAtomType type = getAtomType("N.minus.sp3");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					}
    				} else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    					if (atomContainer.getConnectedBondsCount(atom) <= 1) {
    						IAtomType type = getAtomType("N.minus.sp2");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					}
    				}
    			}
    		} else if (atomContainer.getConnectedBondsCount(atom) > 3) {
    			// FIXME: I don't perceive carbons with more than 3 connections yet
    			return null;
    		} else if (atomContainer.getConnectedBondsCount(atom) == 0) {
    			IAtomType type = getAtomType("N.sp3");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		} else { // OK, use bond order info
    			IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    			if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    				boolean isRingAtom = isRingAtom(atom, atomContainer);
    				int explicitHydrogens = countExplicitHydrogens(atom, atomContainer);
    				int connectedHeavyAtoms = atomContainer.getConnectedBondsCount(atom) - explicitHydrogens; 
    				if (connectedHeavyAtoms == 2) {
    					if (!isRingAtom && isAmide(atom, atomContainer)) {
    						IAtomType type = getAtomType("N.amide");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					}
    					List<IBond> bonds = atomContainer.getConnectedBondsList(atom);
    					if (bonds.get(0).getFlag(CDKConstants.ISAROMATIC) &&
    							bonds.get(1).getFlag(CDKConstants.ISAROMATIC)) {
    						IAtomType type = getAtomType("N.sp2");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					} else {
    						// a N.sp3 which is expected to take part in an aromatic system
    						if (isRingAtom && bothNeighborsAreSp2(atom, atomContainer)) {
    							IAtomType type = getAtomType("N.planar3");
    							if (isAcceptable(atom, atomContainer, type)) return type;
    						}
    						IAtomType type = getAtomType("N.sp3");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					}
    				} else if (connectedHeavyAtoms == 3) {
    					if (isRingAtom && bothNeighborsAreSp2(atom, atomContainer)) {
    						IAtomType type = getAtomType("N.planar3");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					}
    					IAtomType type = getAtomType("N.sp3");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				} else if (connectedHeavyAtoms == 1) {
    					if (isAmide(atom, atomContainer)) {
    						IAtomType type = getAtomType("N.amide");
    						if (isAcceptable(atom, atomContainer, type)) return type;
    					}
    					IAtomType type = getAtomType("N.sp3");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				} else if (connectedHeavyAtoms == 0) {
    					IAtomType type = getAtomType("N.sp3");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				}
    			} else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    				IAtomType type = getAtomType("N.sp2");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (maxBondOrder == CDKConstants.BONDORDER_TRIPLE) {
    				IAtomType type = getAtomType("N.sp1");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		}
    	}
    	return null;
    }

    private boolean isRingAtom(IAtom atom, IAtomContainer atomContainer) {
    	SpanningTree st = new SpanningTree(atomContainer);
    	try {
    		return st.getCyclicFragmentsContainer().contains(atom);
    	} catch (NoSuchAtomException exception) {
    		return false;
    	}
    }

    private boolean isAmide(IAtom atom, IAtomContainer atomContainer) {
    	Iterator<IAtom> neighbors = atomContainer.getConnectedAtomsList(atom).iterator();
    	while (neighbors.hasNext()) {
    		IAtom neighbor = neighbors.next(); 
    		if (neighbor.getSymbol().equals("C")) {
    			if (countAttachedDoubleBonds(atomContainer, neighbor, "O") == 1) return true;
    		}
    	}
    	return false;
    }

    private int countExplicitHydrogens(IAtom atom, IAtomContainer atomContainer) {
    	int count = 0;
    	Iterator<IAtom> neighbors = atomContainer.getConnectedAtomsList(atom).iterator();
    	while (neighbors.hasNext()) {
    		if (neighbors.next().getSymbol().equals("H")) {
    			count++;
    		}
    	}
    	return count;
    }

    private IAtomType perceiveSulphurs(IAtomContainer atomContainer, IAtom atom)
    throws CDKException {
    	if ("S".equals(atom.getSymbol())) {
    		List<IBond> neighbors = atomContainer.getConnectedBondsList(atom);
    		int neighborcount = neighbors.size();
    		if (atom.getHybridization() != CDKConstants.UNSET &&
    				(atom.getFormalCharge() == CDKConstants.UNSET ||
    				 atom.getFormalCharge() != 0)) {
    			if (atom.getHybridization() == Hybridization.SP2 &&
    				atom.getFormalCharge() == +1) {
    				IAtomType type = getAtomType("S.plus");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		} else if (atom.getFormalCharge() != CDKConstants.UNSET &&
    		  		   atom.getFormalCharge() != 0) {
    			if (atom.getFormalCharge() == -1 &&
    					neighborcount == 1) {
    				IAtomType type = getAtomType("S.minus");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atom.getFormalCharge() == +1 &&
    					   neighborcount == 2) {
    				IAtomType type = getAtomType("S.plus");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atom.getFormalCharge() == +2 &&
    					neighborcount == 4) {
    				IAtomType type = getAtomType("S.onyl.charged");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} 
    		} else if (neighborcount == 6) {
    			IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    			if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    				IAtomType type = getAtomType("S.octahedral");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		} else if (neighborcount == 2) {
    			if (isRingAtom(atom, atomContainer)) {
    				boolean bothNeighborsSP2 = true;
    				Iterator<IAtom> atoms = atomContainer.getConnectedAtomsList(atom).iterator();
    				while (atoms.hasNext() && bothNeighborsSP2) {
    					IAtom nextAtom = atoms.next();
    					if (!nextAtom.getSymbol().equals("H")) {
    						if (nextAtom.getHybridization() != CDKConstants.UNSET && 
    								nextAtom.getHybridization() != Hybridization.SP2 && 
    								countAttachedDoubleBonds(atomContainer, nextAtom) > 0) {
    							bothNeighborsSP2 = false;
    						}
    					}
    				}
    				if (bothNeighborsSP2) {
    					IAtomType type = getAtomType("S.planar3");
        				if (isAcceptable(atom, atomContainer, type)) return type;
    				}
    			}
    			IAtomType type = getAtomType("S.3");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		} else if (neighborcount == 1) {
    			if (atomContainer.getConnectedBondsList(atom).get(0).getOrder() == CDKConstants.BONDORDER_DOUBLE) {
    				IAtomType type = getAtomType("S.2");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else {
    				IAtomType type = getAtomType("S.3");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		} else if (neighborcount == 0) {
    			IAtomType type = getAtomType("S.3");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		} else {
    			// count the number of double bonded oxygens
    			int doubleBondedOxygens = countAttachedDoubleBonds(atomContainer, atom, "O");
    			int doubleBondedNitrogens = countAttachedDoubleBonds(atomContainer, atom, "N");
    			if (doubleBondedOxygens + doubleBondedNitrogens == 2 &&
    					neighborcount == 4){
    				IAtomType type = getAtomType("S.onyl");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (doubleBondedOxygens == 1 && neighborcount == 3){
    				IAtomType type = getAtomType("S.inyl");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			};
    		}
    	}
    	return null;
    }

    private IAtomType perceivePhosphors(IAtomContainer atomContainer, IAtom atom)
    throws CDKException {
    	if ("P".equals(atom.getSymbol())) {
    		List<IBond> neighbors = atomContainer.getConnectedBondsList(atom);
    		int neighborcount = neighbors.size();
    		IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    		if (neighborcount == 3) {
    			IAtomType type = getAtomType("P.ine");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		} else if (neighborcount == 2) {
    			if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    				IAtomType type = getAtomType("P.ine");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    				IAtomType type = getAtomType("P.ine");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		} else if (neighborcount == 4) {
    			// count the number of double bonded oxygens
    			int doubleBonds = 0;
    			for (int i=neighborcount-1;i>=0;i--) {
    				if (neighbors.get(i).getOrder() == CDKConstants.BONDORDER_DOUBLE) {
    					doubleBonds++;
    				}
    			}
    			if (doubleBonds == 1){
    				IAtomType type = getAtomType("P.ate");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			};
    		}
    	}
    	return null;
    }
    private IAtomType perceiveHydrogens(IAtomContainer atomContainer, IAtom atom)
    throws CDKException {
    	if ("H".equals(atom.getSymbol())) {
    		int neighborcount = atomContainer.getConnectedBondsCount(atom);
    		if (neighborcount == 2) {
    			// FIXME: bridging hydrogen as in B2H6
    			return null;
    		} else if (neighborcount == 1) {
    			if (atom.getFormalCharge() == CDKConstants.UNSET || atom.getFormalCharge() == 0) {
    				IAtomType type = getAtomType("H");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		} else if (neighborcount == 0) {
    			if (atom.getFormalCharge() == CDKConstants.UNSET || atom.getFormalCharge() == 0) {
    				IAtomType type = getAtomType("H");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atom.getFormalCharge() == 1){
    				IAtomType type = getAtomType("H.plus");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atom.getFormalCharge() == -1){
    				IAtomType type = getAtomType("H.minus");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		}
    	}
    	return null;
    }

    private IAtomType perceiveLithium(IAtomContainer atomContainer, IAtom atom)
    	throws CDKException {
    	if ("Li".equals(atom.getSymbol())) {
    		int neighborcount = atomContainer.getConnectedBondsCount(atom);
    		if (neighborcount == 1) {
    			if (atom.getFormalCharge() == CDKConstants.UNSET ||
    				atom.getFormalCharge() == 0) {
    				IAtomType type = getAtomType("Li");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		}
    	}
    	return null;
    }

    private IAtomType perceiveHalogens(IAtomContainer atomContainer, IAtom atom)
    throws CDKException {
    	if ("Cl".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == -1)) {
    			IAtomType type = getAtomType("Cl.minus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		} else if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +3) && atomContainer.getConnectedBondsCount(atom) == 4) {
    			IAtomType type = getAtomType("Cl.perchlorate.charged");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		} else if (atomContainer.getConnectedBondsCount(atom) == 1 ||
    				atomContainer.getConnectedBondsCount(atom) == 0) {
    			IAtomType type = getAtomType("Cl");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		} else {
    			int doubleBonds = countAttachedDoubleBonds(atomContainer, atom);
    			if (atomContainer.getConnectedBondsCount(atom) == 3 &&
    					doubleBonds == 2) {
    				IAtomType type = getAtomType("Cl.chlorate");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atomContainer.getConnectedBondsCount(atom) == 4 &&
    					doubleBonds == 3) {
    				IAtomType type = getAtomType("Cl.perchlorate");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		}
    	} else if ("Br".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == -1)) {
    			IAtomType type = getAtomType("Br.minus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		} else if (atomContainer.getConnectedBondsCount(atom) == 1 ||
    				atomContainer.getConnectedBondsCount(atom) == 0) {
    			IAtomType type = getAtomType("Br");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("F".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == -1)) {
    			IAtomType type = getAtomType("F.minus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		} else if (atomContainer.getConnectedBondsCount(atom) == 1 ||
    				atomContainer.getConnectedBondsCount(atom) == 0) {
    			IAtomType type = getAtomType("F");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("I".equals(atom.getSymbol())) {
    		if (atom.getFormalCharge() != CDKConstants.UNSET && 
    				atom.getFormalCharge() != 0) {
    			if (atom.getFormalCharge() == -1) {
    				IAtomType type = getAtomType("I.minus");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atom.getFormalCharge() == 1) {
    				IAtomType type = getAtomType("I.plus");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			}
    		} else if (atomContainer.getConnectedBondsCount(atom) == 1 ||
    				atomContainer.getConnectedBondsCount(atom) == 0) {
    			IAtomType type = getAtomType("I");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	}
    	return null;
    }

    private IAtomType perceiveCommonSalts(IAtomContainer atomContainer, IAtom atom) throws CDKException {
    	if ("Na".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +1)) {
    			IAtomType type = getAtomType("Na.plus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("Ca".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +2)) {
    			IAtomType type = getAtomType("Ca.2plus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("Mg".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +2)) {
    			IAtomType type = getAtomType("Mg.2plus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("Fe".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +2)) {
    			IAtomType type = getAtomType("Fe.2plus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("Ni".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +2)) {
    			IAtomType type = getAtomType("Ni.2plus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("K".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +1)) {
    			IAtomType type = getAtomType("K.plus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	}
    	return null;
    }

    private IAtomType perceiveOrganometallicCenters(IAtomContainer atomContainer, IAtom atom) throws CDKException {
    	if ("Hg".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == -1)) {
    			IAtomType type = getAtomType("Hg.minus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("Po".equals(atom.getSymbol())) {
    		if (atomContainer.getConnectedBondsCount(atom) == 2) {
    			IAtomType type = getAtomType("Po");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("Zn".equals(atom.getSymbol())) {
    		if (atomContainer.getConnectedBondsCount(atom) == 2 &&
    			(atom.getFormalCharge() == CDKConstants.UNSET ||
    		     atom.getFormalCharge() == 0)) {
    			IAtomType type = getAtomType("Zn");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		} else if (atom.getFormalCharge() != CDKConstants.UNSET ||
      		           atom.getFormalCharge() == 2) {
      		    IAtomType type = getAtomType("Zn.2plus");
      		    if (isAcceptable(atom, atomContainer, type)) return type;
      		}
    	} else if ("Sn".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == 0 &&
    				atomContainer.getConnectedBondsCount(atom) <= 4)) {
    			IAtomType type = getAtomType("Sn.sp3");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("As".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +1 &&
    				atomContainer.getConnectedBondsCount(atom) <= 4)) {
    			IAtomType type = getAtomType("As.plus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	}
    	return null;
    }

    private IAtomType perceiveNobelGases(IAtomContainer atomContainer, IAtom atom) throws CDKException {
    	if ("He".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() == CDKConstants.UNSET ||
    				atom.getFormalCharge() == 0)) {
    			IAtomType type = getAtomType("He");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	}
    	return null;
    }

    private IAtomType perceiveSilicon(IAtomContainer atomContainer, IAtom atom) throws CDKException {
    	if ("Si".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == 0 &&
    				atomContainer.getConnectedBondsCount(atom) <= 4)) {
    			IAtomType type = getAtomType("Si.sp3");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	}
    	return null;
    }

    private int countAttachedDoubleBonds(IAtomContainer container, IAtom atom) {
    	return countAttachedDoubleBonds(container, atom, null);
    }

    /**
     * Count the number of doubly bonded atoms.
     * 
     * @param symbol If not null, then it only counts the double bonded atoms which
     *               match the given symbol.
     * @return
     */
    private int countAttachedDoubleBonds(IAtomContainer container, IAtom atom, String symbol) {
    	// count the number of double bonded oxygens
    	List<IBond> neighbors = container.getConnectedBondsList(atom);
    	int neighborcount = neighbors.size();
    	int doubleBondedAtoms = 0;
    	for (int i=neighborcount-1;i>=0;i--) {
    		if (neighbors.get(i).getOrder() == CDKConstants.BONDORDER_DOUBLE) {
    			IBond bond =  neighbors.get(i);
    			if (bond.getAtomCount() == 2 && bond.contains(atom)) {
    				if (symbol != null) {
    					if (bond.getAtom(0).getSymbol().equals(symbol) ||
    							bond.getAtom(1).getSymbol().equals(symbol)) {
    						doubleBondedAtoms++;
    					}
    				} else {
    					doubleBondedAtoms++;
    				}
    			}
    		}
    	}
    	return doubleBondedAtoms;
    }

    private IAtomType getAtomType(String identifier) throws CDKException {
    	IAtomType type = factory.getAtomType(identifier);
    	type.setValency((Integer)type.getProperty(CDKConstants.PI_BOND_COUNT) +
    			        type.getFormalNeighbourCount());
    	return type;
    }
    
    private boolean isAcceptable(IAtom atom, IAtomContainer container, IAtomType type) {
    	if (mode == REQUIRE_EXPLICIT_HYDROGENS) {
    		// make sure no implicit hydrogens were assumed
    		if (container.getConnectedAtomsCount(atom) != type.getFormalNeighbourCount())
    			return false;
    	}
    	return true;
    }
    
}

