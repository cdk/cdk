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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Atom Type matcher... TO BE WRITTEN.
 *
 * <p>This class uses the <b>cdk/config/data/cdk_atomtypes.xml</b> 
 * list. If there is not an atom type defined for the tested atom, then NULL 
 * is returned.
 *
 * @author         egonw
 * @cdk.created    2007-07-20
 * @cdk.module     standard
 * @cdk.svnrev  $Revision: 9162 $
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
        if (atom instanceof IPseudoAtom) {
        	return factory.getAtomType("X");
        }
        type = perceiveCarbons(atomContainer, atom);
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
        return type;
    }
    
    private IAtomType perceiveCarbons(IAtomContainer atomContainer, IAtom atom)
    	throws CDKException {
    	if ("C".equals(atom.getSymbol())) {
    		// if hybridization is given, use that
    		if (atom.getHybridization() != CDKConstants.UNSET &&
    			(atom.getFormalCharge() == CDKConstants.UNSET || atom.getFormalCharge() == 0)) {
    			if (atom.getHybridization() == Hybridization.SP2) {
        			return getAtomType("C.sp2");
    			} else if (atom.getHybridization() == Hybridization.SP3) {
    				return getAtomType("C.sp3");
    			} else if (atom.getHybridization() == Hybridization.SP1) {
    				return getAtomType("C.sp");
    			}
    		} else if (atom.getFlag(CDKConstants.ISAROMATIC)) {
    			return getAtomType("C.sp2");
    		} else if (atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() != 0) {
    			if (atom.getFormalCharge() == 1) {
    				if (atomContainer.getConnectedBondsCount(atom) == 0) {
    					return getAtomType("C.plus.sp2");
    				} else {
    					double maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    					if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    						return getAtomType("C.plus.sp2");
    					} else if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    						return getAtomType("C.plus.planar");
    					} 
    				}
    			} else if (atom.getFormalCharge() == -1) {
    				double maxBondOrder = atomContainer.getMaximumBondOrder(atom);
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
        					if (bothNeighborsSP2) return getAtomType("C.minus.planar");
        				}
        				return getAtomType("C.minus.sp3");
        			} else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE &&
                			atomContainer.getConnectedBondsCount(atom) <= 3) {
            				return getAtomType("C.minus.sp2");
        			} else if (maxBondOrder == CDKConstants.BONDORDER_TRIPLE &&
            			atomContainer.getConnectedBondsCount(atom) <= 1) {
        				return getAtomType("C.minus.sp1");
        			}
    			}
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
    				return getAtomType("C.sp");
    			} else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    				// OK, one or two double bonds?
    				Iterator<IBond> bonds = atomContainer.getConnectedBondsList(atom).iterator();
    				int doubleBondCount = 0;
    				while (bonds.hasNext()) {
    					if (bonds.next().getOrder() == CDKConstants.BONDORDER_DOUBLE)
    						doubleBondCount++;
    				}
    				if (doubleBondCount == 2) {
    					return getAtomType("C.sp");
    				} else if (doubleBondCount == 1) {
    					return getAtomType("C.sp2");
    				}
    			} else {
    				return getAtomType("C.sp3");
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
    				double maxBondOrder = atomContainer.getMaximumBondOrder(atom);
        			if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
        				return getAtomType("O.sp2");
        			} else if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
        				return getAtomType("O.planar3");
        			}    				
    			} else if (atom.getHybridization() == Hybridization.SP3) {
    				return getAtomType("O.sp3");
    			} else if (atom.getHybridization() == Hybridization.PLANAR3) {
    				return getAtomType("O.planar3");
    			}
    		} else if (atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() != 0) {
    			if (atom.getFormalCharge() == -1 &&
    			 	atomContainer.getConnectedAtomsCount(atom) <= 1) {
    				return getAtomType("O.minus");
    			} else if (atom.getFormalCharge() == -2 &&
        			       atomContainer.getConnectedAtomsCount(atom) == 0) {
    				return getAtomType("O.minus2");
    			} else if (atom.getFormalCharge() == +1) {
    				if (atomContainer.getConnectedBondsCount(atom) == 0) {
    					return getAtomType("O.plus");
    				}
    				double maxBondOrder = atomContainer.getMaximumBondOrder(atom);
        			if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
        				return getAtomType("O.plus.sp2");
        			} else {
        				return getAtomType("O.plus");
        			}
    			}
    			return null;
    		} else if (atomContainer.getConnectedBondsCount(atom) > 2) {
    			// FIXME: I don't perceive carbons with more than 4 connections yet
    			return null;
    		} else { // OK, use bond order info
    			double maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    			if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    				return getAtomType("O.sp2");
    			} else if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    				int explicitHydrogens = countExplicitHydrogens(atom, atomContainer);
    				int connectedHeavyAtoms = atomContainer.getConnectedBondsCount(atom) - explicitHydrogens; 
    				if (connectedHeavyAtoms == 2) {
    					// a O.sp3 which is expected to take part in an aromatic system
    					if (isRingAtom(atom, atomContainer) && bothNeighborsAreSp2(atom, atomContainer)) {
    						return getAtomType("O.planar3");
    					}
    					return getAtomType("O.sp3");
    				} else {
    					return getAtomType("O.sp3");
    				}
    			}
    		}
    	}
    	return null;
    }

    private boolean bothNeighborsAreSp2(IAtom atom, IAtomContainer atomContainer) {
        boolean bothNeighborsSP2 = true;
        Iterator<IAtom> atoms = atomContainer.getConnectedAtomsList(atom).iterator();
        while (atoms.hasNext() && bothNeighborsSP2) {
            IAtom nextAtom = atoms.next();
            if (!nextAtom.getSymbol().equals("H")) {
            	if (nextAtom.getHybridization() != CDKConstants.UNSET &&
                    nextAtom.getHybridization() == Hybridization.SP2) {
            		// OK, it's SP2
            	} else if (countAttachedDoubleBonds(atomContainer, nextAtom) > 0) {
                    // OK, it's SP2
                } else {
                	bothNeighborsSP2 = false;
                }
            }
        }
        return bothNeighborsSP2;
    }

    private IAtomType perceiveNitrogens(IAtomContainer atomContainer, IAtom atom)
    	throws CDKException {
    	if ("N".equals(atom.getSymbol())) {
    		// if hybridization is given, use that
    		if (atom.getHybridization() != CDKConstants.UNSET &&
    			(atom.getFormalCharge() == CDKConstants.UNSET ||
    	    	 atom.getFormalCharge() == 0)) {
    			if (atom.getHybridization() == Hybridization.SP1) {
    				return getAtomType("N.sp1");
    			} else if (atom.getHybridization() == Hybridization.SP2) {
                    // but an sp2 hyb N might N.sp2 or N.planar3 (pyrrole), so check for the latter
                    int hcount = atom.getHydrogenCount() == null ? 0 : atom.getHydrogenCount();
                    if (isRingAtom(atom, atomContainer) &&
                            atomContainer.getConnectedAtomsCount(atom) + hcount == 3 &&
                            bothNeighborsAreSp2(atom, atomContainer)) return getAtomType("N.planar3");
                    return getAtomType("N.sp2");
                } else if (atom.getHybridization() == Hybridization.SP3) {
                    return getAtomType("N.sp3");
                }
    		} else if (atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() != 0) {
    			if (atom.getFormalCharge() == 1) {
    				double maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    				if (maxBondOrder == CDKConstants.BONDORDER_SINGLE ||
    					atomContainer.getConnectedBondsCount(atom) == 0) {
    					return getAtomType("N.plus");
    				} else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    					int doubleBonds= countAttachedDoubleBonds(atomContainer, atom);
    					if (doubleBonds == 1) {
    						return getAtomType("N.plus.sp2");
    					} else if (doubleBonds == 2) {
    						return getAtomType("N.plus.sp1");
    					}
    				} else if (maxBondOrder == CDKConstants.BONDORDER_TRIPLE) {
    					if (atomContainer.getConnectedBondsCount(atom) == 2) {
    						return getAtomType("N.plus.sp1");
    					}
    				}
    			} else if (atom.getFormalCharge() == -1) {
    				double maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    				if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    					if (atomContainer.getConnectedBondsCount(atom) <= 2) {
    						return getAtomType("N.minus.sp3");
    					}
    				} else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    					if (atomContainer.getConnectedBondsCount(atom) <= 1) {
    						return getAtomType("N.minus.sp2");
    					}
    				}
    			}
    		} else if (atomContainer.getConnectedBondsCount(atom) > 3) {
    			// FIXME: I don't perceive carbons with more than 3 connections yet
    			return null;
    		} else if (atomContainer.getConnectedBondsCount(atom) == 0) {
    			return getAtomType("N.sp3");
    		} else { // OK, use bond order info
    			double maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    			if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    				int explicitHydrogens = countExplicitHydrogens(atom, atomContainer);
    				int connectedHeavyAtoms = atomContainer.getConnectedBondsCount(atom) - explicitHydrogens; 
    				if (connectedHeavyAtoms == 2) {
        				if (isAmide(atom, atomContainer)) {
        					return getAtomType("N.amide");
        				}
    					List<IBond> bonds = atomContainer.getConnectedBondsList(atom);
    					if (bonds.get(0).getFlag(CDKConstants.ISAROMATIC) &&
    						bonds.get(1).getFlag(CDKConstants.ISAROMATIC)) {
    						return getAtomType("N.sp2");
                        } else {
                            // a N.sp3 which is expected to take part in an aromatic system
                            if (isRingAtom(atom, atomContainer) && bothNeighborsAreSp2(atom, atomContainer)) {
                                return getAtomType("N.planar3");
                            }
                            return getAtomType("N.sp3");
                        }
    				} else if (connectedHeavyAtoms == 3) {
    					return getAtomType("N.sp3");
    				} else if (connectedHeavyAtoms == 1) {
        				if (isAmide(atom, atomContainer)) {
        					return getAtomType("N.amide");
        				}
    					return getAtomType("N.sp3");
    				} else if (connectedHeavyAtoms == 0) {
    					return getAtomType("N.sp3");
    				}
    			} else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
    				return getAtomType("N.sp2");
    			} else if (maxBondOrder == CDKConstants.BONDORDER_TRIPLE) {
    				return getAtomType("N.sp1");
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
    		if (atom.getFormalCharge() != CDKConstants.UNSET &&
    			atom.getFormalCharge() != 0) {
    			if (atom.getFormalCharge() == -1 &&
    				neighborcount == 1) {
    				return getAtomType("S.minus");
    			}
    		} else if (neighborcount == 6) {
    			double maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    			if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    				return getAtomType("S.octahedral");
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
					if (bothNeighborsSP2) return getAtomType("S.planar3");
				}
    			return getAtomType("S.3");
    		} else if (neighborcount == 1) {
    			if (atomContainer.getConnectedBondsList(atom).get(0).getOrder() == CDKConstants.BONDORDER_DOUBLE) {
    				return getAtomType("S.2");
    			} else {
    				return getAtomType("S.3");
    			}
    		} else if (neighborcount == 0) {
    			return getAtomType("S.3");
    		} else {
    			// count the number of double bonded oxygens
    			int doubleBondedOxygens = countAttachedDoubleBonds(atomContainer, atom, "O");
    			int doubleBondedNitrogens = countAttachedDoubleBonds(atomContainer, atom, "N");
    			if (doubleBondedOxygens + doubleBondedNitrogens == 2 &&
    				neighborcount == 4){
    				return getAtomType("S.onyl");
    			} else if (doubleBondedOxygens == 1 && neighborcount == 3){
    				return getAtomType("S.inyl");
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
    		double maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    		if (neighborcount == 3) {
    			return getAtomType("P.ine");
    		} else if (neighborcount == 2) {
    			if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
        			return getAtomType("P.ine");
    			} else if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
    				return getAtomType("P.ine");
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
    				return getAtomType("P.ate");
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
    				return getAtomType("H");
    			}
    		} else if (neighborcount == 0) {
    			if (atom.getFormalCharge() == CDKConstants.UNSET || atom.getFormalCharge() == 0) {
    				return getAtomType("H");
    			} else if (atom.getFormalCharge() == 1){
    				return getAtomType("H.plus");
    			} else if (atom.getFormalCharge() == -1){
    				return getAtomType("H.minus");
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
    			return getAtomType("Cl.minus");
    		} else if (atomContainer.getConnectedBondsCount(atom) == 1 ||
      				   atomContainer.getConnectedBondsCount(atom) == 0) {
    			return getAtomType("Cl");
    		} else {
    			int doubleBonds = countAttachedDoubleBonds(atomContainer, atom);
    			if (atomContainer.getConnectedBondsCount(atom) == 3 &&
    				doubleBonds == 2) {
    				return getAtomType("Cl.chlorate");
    			} else if (atomContainer.getConnectedBondsCount(atom) == 4 &&
        			doubleBonds == 3) {
    				return getAtomType("Cl.perchlorate");
    			}
    		}
    	} else if ("Br".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
        			atom.getFormalCharge() == -1)) {
       			return getAtomType("Br.minus");
    		} else if (atomContainer.getConnectedBondsCount(atom) == 1 ||
       				atomContainer.getConnectedBondsCount(atom) == 0) {
    			return getAtomType("Br");
    		}
    	} else if ("F".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
        			atom.getFormalCharge() == -1)) {
       			return getAtomType("F.minus");
    		} else if (atomContainer.getConnectedBondsCount(atom) == 1 ||
    				atomContainer.getConnectedBondsCount(atom) == 0) {
    			return getAtomType("F");
    		}
    	} else if ("I".equals(atom.getSymbol())) {
    		if (atom.getFormalCharge() != CDKConstants.UNSET && 
    		    atom.getFormalCharge() != 0) {
    			if (atom.getFormalCharge() == -1) {
    				return getAtomType("I.minus");
    			} else if (atom.getFormalCharge() == 1) {
    				return getAtomType("I.plus");
    			}
        	} else if (atomContainer.getConnectedBondsCount(atom) == 1 ||
       				   atomContainer.getConnectedBondsCount(atom) == 0) {
        		return getAtomType("I");
        	}
    	}
    	return null;
    }

    private IAtomType perceiveCommonSalts(IAtomContainer atomContainer, IAtom atom) throws CDKException {
    	if ("Na".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +1)) {
    			return getAtomType("Na.plus");
    		}
    	} else if ("Ca".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +2)) {
    			return getAtomType("Ca.2plus");
    		}
    	} else if ("Mg".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +2)) {
    			return getAtomType("Mg.2plus");
    		}
    	} else if ("Fe".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +2)) {
    			return getAtomType("Fe.2plus");
    		}
    	} else if ("Ni".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +2)) {
    			return getAtomType("Ni.2plus");
    		}
    	} else if ("K".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +1)) {
    			return getAtomType("K.plus");
    		}
    	}
    	return null;
    }

    private IAtomType perceiveOrganometallicCenters(IAtomContainer atomContainer, IAtom atom) throws CDKException {
    	if ("Hg".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    			 atom.getFormalCharge() == -1)) {
    			return getAtomType("Hg.minus");
    		}
    	} else if ("Po".equals(atom.getSymbol())) {
    		if (atomContainer.getConnectedBondsCount(atom) == 2) {
    			return getAtomType("Po");
    		}
    	}
    	return null;
    }
    
    private IAtomType perceiveNobelGases(IAtomContainer atomContainer, IAtom atom) throws CDKException {
    	if ("He".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() == CDKConstants.UNSET ||
    				atom.getFormalCharge() == 0)) {
    			return getAtomType("He");
    		}
    	}
    	return null;
    }
    
    private IAtomType perceiveSilicon(IAtomContainer atomContainer, IAtom atom) throws CDKException {
    	if ("Si".equals(atom.getSymbol())) {
    		if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    			atom.getFormalCharge() == 0 &&
    			atomContainer.getConnectedBondsCount(atom) <= 4)) {
    			return getAtomType("Si.sp3");
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
    
}

