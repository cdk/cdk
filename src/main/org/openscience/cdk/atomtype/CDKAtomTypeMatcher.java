/* Copyright (C) 2007-2008  Egon Willighagen <egonw@users.sf.net>
 *                    2011  Nimish Gopal <nimishg@ebi.ac.uk>
 *                    2011  Syed Asad Rahman <asad@ebi.ac.uk>
 *                    2011  Gilleain Torrance <gilleain.torrance@gmail.com>
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
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
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
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;

/**
 * Atom Type matcher that perceives atom types as defined in the CDK atom type list
 * <code>org/openscience/cdk/dict/data/cdk-atom-types.owl</code>. 
 * If there is not an atom type defined for the tested atom, then NULL 
 * is returned.
 *
 * @author         egonw
 * @cdk.created    2007-07-20
 * @cdk.module     core
 * @cdk.githash
 * @cdk.bug        1802998
 */
@TestClass("org.openscience.cdk.atomtype.CDKAtomTypeMatcherTest")
public class CDKAtomTypeMatcher implements IAtomTypeMatcher {

	public final static int REQUIRE_NOTHING = 1;
	public final static int REQUIRE_EXPLICIT_HYDROGENS = 2;
	
	private AtomTypeFactory factory;
	private int mode;
	
    private static Map<Integer,Map<IChemObjectBuilder,CDKAtomTypeMatcher>> 
    	factories = new Hashtable<Integer,Map<IChemObjectBuilder,CDKAtomTypeMatcher>>(1); 
    
    private CDKAtomTypeMatcher(IChemObjectBuilder builder, int mode) {
    	factory = AtomTypeFactory.getInstance(
			"org/openscience/cdk/dict/data/cdk-atom-types.owl",
			builder
		);
    	this.mode = mode;
    }
    
    @TestMethod("testGetInstance_IChemObjectBuilder")
    public static CDKAtomTypeMatcher getInstance(IChemObjectBuilder builder) {
        return getInstance(builder, REQUIRE_NOTHING);
    }

    @TestMethod("testGetInstance_IChemObjectBuilder_int")
    public static CDKAtomTypeMatcher getInstance(IChemObjectBuilder builder, int mode) {
    	if (!factories.containsKey(mode))
    		factories.put(mode, new Hashtable<IChemObjectBuilder,CDKAtomTypeMatcher>(1));
    	if (!factories.get(mode).containsKey(builder))
    		factories.get(mode).put(builder, new CDKAtomTypeMatcher(builder, mode));
    	return factories.get(mode).get(builder);
    }
    
    @TestMethod("testFindMatchingAtomType_IAtomContainer")
    public IAtomType[] findMatchingAtomType(IAtomContainer atomContainer) throws CDKException {
        IAtomType[] types = new IAtomType[atomContainer.getAtomCount()];
        int typeCounter = 0;
        for (IAtom atom : atomContainer.atoms()) {
            types[typeCounter] = findMatchingAtomType(atomContainer, atom);
            typeCounter++;
        }
        return types;
    }

    @TestMethod("testFindMatchingAtomType_IAtomContainer_IAtom")
    public IAtomType findMatchingAtomType(IAtomContainer atomContainer, IAtom atom)
        throws CDKException {
        IAtomType type = null;
        if (atom instanceof IPseudoAtom) {
        	return factory.getAtomType("X");
        }
        if ("C".equals(atom.getSymbol())) {
            type = perceiveCarbons(atomContainer, atom);
        } else if ("Li".equals(atom.getSymbol())) {
            type = perceiveLithium(atomContainer, atom);
        } else if ("O".equals(atom.getSymbol())) {
            type = perceiveOxygens(atomContainer, atom);
        } else if ("N".equals(atom.getSymbol())) {
            type = perceiveNitrogens(atomContainer, atom);
        } else if ("H".equals(atom.getSymbol())) {
            type = perceiveHydrogens(atomContainer, atom);
        } else if ("S".equals(atom.getSymbol())) {
            type = perceiveSulphurs(atomContainer, atom);
        } else if ("P".equals(atom.getSymbol())) {
            type = perceivePhosphors(atomContainer, atom);
        } else if ("Si".equals(atom.getSymbol())) {
            type = perceiveSilicon(atomContainer, atom);
        } else if ("B".equals(atom.getSymbol())) {
            type = perceiveBorons(atomContainer, atom);
        } else if ("Be".equals(atom.getSymbol())) {
            type = perceiveBeryllium(atomContainer, atom);
        } else if ("Cr".equals(atom.getSymbol())) {
            type = perceiveChromium(atomContainer, atom);
        } else if ("Se".equals(atom.getSymbol())) {
            type = perceiveSelenium(atomContainer, atom);
        } else if ("Mo".equals(atom.getSymbol())) {
            type = perceiveMolybdenum(atomContainer, atom);
        } else if ("Rb".equals(atom.getSymbol())) {
            type = perceiveRubidium(atomContainer, atom);
        } else if ("Te".equals(atom.getSymbol())) {
            type = perceiveTellurium(atomContainer, atom);
        } else if ("Cu".equals(atom.getSymbol())) {
            type = perceiveCopper(atomContainer, atom);
        } else if ("Ba".equals(atom.getSymbol())) {
            type = perceiveBarium(atomContainer, atom);
        } else if ("Ga".equals(atom.getSymbol())) {
            type = perceiveGallium(atomContainer, atom);
        } else if ("Ru".equals(atom.getSymbol())) {
            type = perceiveRuthenium(atomContainer, atom);
        } else if ("Zn".equals(atom.getSymbol())) {
            type = perceiveZinc(atomContainer, atom);
        } else if ("Al".equals(atom.getSymbol())) {
            type = perceiveAluminium(atomContainer, atom);
        }  else if ("Ni".equals(atom.getSymbol())) {
            type = perceiveNickel(atomContainer, atom);
        } else if ("Gd".equals(atom.getSymbol())) {
            type = perceiveGadolinum(atomContainer, atom);
        } else if ("Ge".equals(atom.getSymbol())) {
            type = perceiveGermanium(atomContainer, atom);
        } else if ("Co".equals(atom.getSymbol())) {
            type = perceiveCobalt(atomContainer, atom);
        } else if ("Br".equals(atom.getSymbol())) {
            type = perceiveBromine(atomContainer, atom);
        } else if ("V".equals(atom.getSymbol())) {
            type = perceiveVanadium(atomContainer, atom);
        } else if ("Ti".equals(atom.getSymbol())) {
            type = perceiveTitanium(atomContainer, atom);
        } else if ("Sr".equals(atom.getSymbol())) {
            type = perceiveStrontium(atomContainer, atom);
        } else if ("Pb".equals(atom.getSymbol())) {
            type = perceiveLead(atomContainer, atom);
        } else if ("Tl".equals(atom.getSymbol())) {
            type = perceiveThallium(atomContainer, atom);
        } else if ("Sb".equals(atom.getSymbol())) {
            type = perceiveAntimony(atomContainer, atom);
        } else if ("Pt".equals(atom.getSymbol())) {
            type = perceivePlatinum(atomContainer, atom);
        } else if ("Hg".equals(atom.getSymbol())) {
            type = perceiveMercury(atomContainer, atom);
        } else if ("Fe".equals(atom.getSymbol())) {
            type = perceiveIron(atomContainer, atom);
        } else if ("Ra".equals(atom.getSymbol())) {
            type = perceiveRadium(atomContainer, atom);
        } else if ("Au".equals(atom.getSymbol())) {
            type = perceiveGold(atomContainer, atom);
        } else if ("Ag".equals(atom.getSymbol())) {
            type = perceiveSilver(atomContainer, atom);
        } else if ("Cl".equals(atom.getSymbol())) {
            type = perceiveChlorine(atomContainer, atom);
        } else if ("In".equals(atom.getSymbol())) {
            type = perceiveIndium(atomContainer, atom);
        } else if ("Pu".equals(atom.getSymbol())) {
            type = perceivePlutonium(atomContainer, atom);
        } else if ("Th".equals(atom.getSymbol())) {
            type = perceiveThorium(atomContainer, atom);
        } else if ("K".equals(atom.getSymbol())) {
            type = perceivePotassium(atomContainer, atom);
        } else if ("Mn".equals(atom.getSymbol())) {
            type = perceiveManganese(atomContainer, atom);
        } else if ("Mg".equals(atom.getSymbol())) {
            type = perceiveMagnesium(atomContainer, atom);
        } else if ("Na".equals(atom.getSymbol())) {
            type = perceiveSodium(atomContainer, atom);
        } else if ("As".equals(atom.getSymbol())) {
            type = perceiveArsenic(atomContainer, atom);
        } else if ("Cd".equals(atom.getSymbol())) {
            type = perceiveCadmium(atomContainer, atom);
        } else if ("Ca".equals(atom.getSymbol())) {
            type = perceiveCalcium(atomContainer, atom);
        } else {
            if (type == null) type = perceiveHalogens(atomContainer, atom);
            if (type == null) type = perceiveCommonSalts(atomContainer, atom);
            if (type == null) type = perceiveOrganometallicCenters(atomContainer, atom);
            if (type == null) type = perceiveNobelGases(atomContainer, atom);
        }
        return type;
    }
    
    private IAtomType perceiveGallium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
        if (!isCharged(atom) && maxBondOrder == IBond.Order.SINGLE &&
            atomContainer.getConnectedAtomsCount(atom) <= 3) {
            IAtomType type = getAtomType("Ga");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() == 3) {
            IAtomType type = getAtomType("Ga.3plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }

    private IAtomType perceiveGermanium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
        if (!isCharged(atom) && maxBondOrder == IBond.Order.SINGLE &&
            atomContainer.getConnectedAtomsCount(atom) <= 4) {
            IAtomType type = getAtomType("Ge");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        if (atom.getFormalCharge() == 0 && atomContainer.getConnectedAtomsCount(atom) == 3) {
            IAtomType type = getAtomType("Ge.3");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }

    private IAtomType perceiveSelenium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if ("Se".equals(atom.getSymbol())) {
            int doublebondcount = countAttachedDoubleBonds(atomContainer, atom);
            if (atom.getFormalCharge() != CDKConstants.UNSET
                    && atom.getFormalCharge() == 0) {
                if (atomContainer.getConnectedAtomsCount(atom) == 0) {
                	if (atom.getImplicitHydrogenCount() != null && atom.getImplicitHydrogenCount() == 0 ) {
                		IAtomType type = getAtomType("Se.2");
                		if (isAcceptable(atom, atomContainer, type)) return type;
                	} else {
                		IAtomType type = getAtomType("Se.3");
                		if (isAcceptable(atom, atomContainer, type)) return type;
                	}
                } else if (atomContainer.getConnectedAtomsCount(atom) == 1) {

                    if (doublebondcount == 1) {
                        IAtomType type = getAtomType("Se.1");
                        if (isAcceptable(atom, atomContainer, type)) return type;
                    } else if (doublebondcount == 0) {
                        IAtomType type = getAtomType("Se.3");
                        if (isAcceptable(atom, atomContainer, type)) return type;
                    }
                } else if (atomContainer.getConnectedAtomsCount(atom) == 2) {
                    if (doublebondcount == 0) {
                        IAtomType type = getAtomType("Se.3");
                        if (isAcceptable(atom, atomContainer, type)) return type;
                    } else if (doublebondcount == 2) {
                        IAtomType type = getAtomType("Se.sp2.2");
                        if (isAcceptable(atom, atomContainer, type)) return type;
                    }
                } else if (atomContainer.getConnectedAtomsCount(atom) == 3) {
                    IAtomType type = getAtomType("Se.sp3.3");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (atomContainer.getConnectedAtomsCount(atom) == 4) {
                    if (doublebondcount == 2) {
                        IAtomType type = getAtomType("Se.sp3.4");
                        if (isAcceptable(atom, atomContainer, type)) return type;
                    } else if (doublebondcount == 0) {
                        IAtomType type = getAtomType("Se.sp3d1.4");
                        if (isAcceptable(atom, atomContainer, type)) return type;
                    }
                } else if (atomContainer.getConnectedAtomsCount(atom) == 5) {
                    IAtomType type = getAtomType("Se.5");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            } else if ((atom.getFormalCharge() != CDKConstants.UNSET && atom.getFormalCharge() == 4)
                    && atomContainer.getConnectedAtomsCount(atom) == 0) {
                IAtomType type = getAtomType("Se.4plus");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if ((atom.getFormalCharge() != CDKConstants.UNSET && atom.getFormalCharge() == 1)
                    && atomContainer.getConnectedAtomsCount(atom) == 3) {
                IAtomType type = getAtomType("Se.plus.3");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if ((atom.getFormalCharge() != CDKConstants.UNSET && atom.getFormalCharge() == -2)
                    && atomContainer.getConnectedAtomsCount(atom) == 0) {
                IAtomType type = getAtomType("Se.2minus");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        }
        return null;
    }

    private IAtomType perceiveTellurium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
        if (!isCharged(atom) && maxBondOrder == IBond.Order.SINGLE && atomContainer.getConnectedAtomsCount(atom) <= 2) {
            IAtomType type = getAtomType("Te.3");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() == 4) {
            if (atomContainer.getConnectedAtomsCount(atom) == 0) {
                IAtomType type = getAtomType("Te.4plus");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        }
        return null;
    }

	private IAtomType perceiveBorons(IAtomContainer atomContainer, IAtom atom)
		throws CDKException {
	    IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
	    if (atom.getFormalCharge() == -1 && 
	        maxBondOrder == IBond.Order.SINGLE &&
	        atomContainer.getConnectedAtomsCount(atom) <= 4) {
	        IAtomType type = getAtomType("B.minus");
	        if (isAcceptable(atom, atomContainer, type)) return type;
	    } else if (atom.getFormalCharge() == +3
                && atomContainer.getConnectedAtomsCount(atom) == 4) {
            IAtomType type = getAtomType("B.3plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
	    } else if (atomContainer.getConnectedAtomsCount(atom) <= 3) {
	        IAtomType type = getAtomType("B");
	        if (isAcceptable(atom, atomContainer, type)) return type;
	    }
    	return null;
    }

    private IAtomType perceiveBeryllium(IAtomContainer atomContainer, IAtom atom)
    	throws CDKException {
		if (atom.getFormalCharge() == -2 &&
		    atomContainer.getMaximumBondOrder(atom) == IBond.Order.SINGLE &&
		    atomContainer.getConnectedAtomsCount(atom) <= 4) {
		    IAtomType type = getAtomType("Be.2minus");
		    if (isAcceptable(atom, atomContainer, type)) return type;
		} else if (atom.getFormalCharge() == 0 &&
                   atomContainer.getConnectedAtomsCount(atom) == 0) {
            IAtomType type = getAtomType("Be.neutral");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
		return null;
    }

    private IAtomType perceiveCarbonRadicals(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atomContainer.getConnectedBondsCount(atom) == 0) {
            IAtomType type = getAtomType("C.radical.planar");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atomContainer.getConnectedBondsCount(atom) <= 3) {
            IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
            if (maxBondOrder == IBond.Order.SINGLE) {
                IAtomType type = getAtomType("C.radical.planar");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (maxBondOrder == IBond.Order.DOUBLE) {
                IAtomType type = getAtomType("C.radical.sp2");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (maxBondOrder == IBond.Order.TRIPLE) {
                IAtomType type = getAtomType("C.radical.sp1");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        }
        return null;
    }
    
	private IAtomType perceiveCarbons(IAtomContainer atomContainer, IAtom atom)
    	throws CDKException {
	    // if hybridization is given, use that
	    if (hasOneSingleElectron(atomContainer, atom)) {
	        return perceiveCarbonRadicals(atomContainer, atom);
	    } else if (hasHybridization(atom) && !isCharged(atom)) {
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
	    } else if (isCharged(atom)) {
	        if (atom.getFormalCharge() == 1) {
	            if (atomContainer.getConnectedBondsCount(atom) == 0) {
	                IAtomType type = getAtomType("C.plus.sp2");
	                if (isAcceptable(atom, atomContainer, type)) return type;
	            } else {
	                IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
	                if (maxBondOrder == CDKConstants.BONDORDER_TRIPLE) {
	                    IAtomType type = getAtomType("C.plus.sp1");
	                    if (isAcceptable(atom, atomContainer, type)) return type;
	                } else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
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
	                if (isRingAtom(atom, atomContainer) && bothNeighborsAreSp2(atom, atomContainer)) {
	                    IAtomType type = getAtomType("C.minus.planar");
	                    if (isAcceptable(atom, atomContainer, type)) return type;
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
	            int doubleBondCount = countAttachedDoubleBonds(atomContainer, atom);
	            if (doubleBondCount == 2) {
	                IAtomType type = getAtomType("C.sp");
	                if (isAcceptable(atom, atomContainer, type)) return type;
	            } else if (doubleBondCount == 1) {
	                IAtomType type = getAtomType("C.sp2");
	                if (isAcceptable(atom, atomContainer, type)) return type;
	            }
	        } else {
	            if (hasAromaticBond(atomContainer, atom)) {
	                IAtomType type = getAtomType("C.sp2");
	                if (isAcceptable(atom, atomContainer, type)) return type;
	            }
	            IAtomType type = getAtomType("C.sp3");
	            if (isAcceptable(atom, atomContainer, type)) return type;
	        }
	    }
    	return null;
    }

    private boolean hasOneSingleElectron(IAtomContainer atomContainer, IAtom atom) {
	    Iterator<ISingleElectron> singleElectrons = atomContainer.singleElectrons().iterator();
	    while (singleElectrons.hasNext()) {
	    	if (singleElectrons.next().contains(atom)) return true;
	    }
	    return false;
    }

    private int countSingleElectrons(IAtomContainer atomContainer, IAtom atom) {
	    Iterator<ISingleElectron> singleElectrons = atomContainer.singleElectrons().iterator();
	    int count = 0;
	    while (singleElectrons.hasNext()) {
	    	if (singleElectrons.next().contains(atom)) count++;
	    }
	    return count;
    }

    private IAtomType perceiveOxygenRadicals(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atom.getFormalCharge() == 0) {
            if (atomContainer.getConnectedBondsCount(atom) <= 1) {
                IAtomType type = getAtomType("O.sp3.radical");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (atom.getFormalCharge() == +1) {
            if (atomContainer.getConnectedBondsCount(atom) == 0) {
                IAtomType type = getAtomType("O.plus.radical");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (atomContainer.getConnectedBondsCount(atom) <= 2) {
                IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
                if (maxBondOrder == IBond.Order.SINGLE) {
                    IAtomType type = getAtomType("O.plus.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (maxBondOrder == IBond.Order.DOUBLE) {
                    IAtomType type = getAtomType("O.plus.sp2.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            }
        }
        return null;
    }
    
    private boolean isCharged(IAtom atom) {
        return (atom.getFormalCharge() != CDKConstants.UNSET && atom.getFormalCharge() != 0);
    }
    
    private boolean hasHybridization(IAtom atom) {
        return atom.getHybridization() != CDKConstants.UNSET;
    }
    
	private IAtomType perceiveOxygens(IAtomContainer atomContainer, IAtom atom) throws CDKException {
	    if (hasOneSingleElectron(atomContainer, atom)) {
	        return perceiveOxygenRadicals(atomContainer, atom);
	    }
	    
	    // if hybridization is given, use that
	    if (hasHybridization(atom) && !isCharged(atom)) {
	        if (atom.getHybridization() == Hybridization.SP2) {
	            int connectedAtomsCount = atomContainer.getConnectedAtomsCount(atom);
	            if (connectedAtomsCount == 1) {
	                if (isCarboxylate(atom, atomContainer)) {
	                    IAtomType type = getAtomType("O.sp2.co2");
	                    if (isAcceptable(atom, atomContainer, type)) return type;    				        
	                } else {
	                    IAtomType type = getAtomType("O.sp2");
	                    if (isAcceptable(atom, atomContainer, type)) return type;
	                }
	            } else if (connectedAtomsCount == 2) {
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
	    } else if (isCharged(atom)) {
	        if (atom.getFormalCharge() == -1 &&
	                atomContainer.getConnectedAtomsCount(atom) <= 1) {
	            if (isCarboxylate(atom, atomContainer)) {
	                IAtomType type = getAtomType("O.minus.co2");
	                if (isAcceptable(atom, atomContainer, type)) return type;
	            } else {
	                IAtomType type = getAtomType("O.minus");
	                if (isAcceptable(atom, atomContainer, type)) return type;
	            }
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
	            if (isCarboxylate(atom, atomContainer)) {
	                IAtomType type = getAtomType("O.sp2.co2");
	                if (isAcceptable(atom, atomContainer, type)) return type;
	            } else {
	                IAtomType type = getAtomType("O.sp2");
	                if (isAcceptable(atom, atomContainer, type)) return type;
	            }
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
    	return null;
    }

    private boolean isCarboxylate(IAtom atom, IAtomContainer container) {
        // assumes that the oxygen only has one neighbor (C=O, or C-[O-])
        List<IAtom> neighbors = container.getConnectedAtomsList(atom);
        if (neighbors.size() != 1) return false;
        IAtom carbon = neighbors.get(0);
        if (!"C".equals(carbon.getSymbol())) return false;
        
        int oxygenCount = 0;
        int singleBondedNegativeOxygenCount = 0;
        int doubleBondedOxygenCount = 0;
        for (IBond cBond : container.getConnectedBondsList(carbon)) {
            IAtom neighbor = cBond.getConnectedAtom(carbon);
            if ("O".equals(neighbor.getSymbol())) {
                oxygenCount++;
                IBond.Order order = cBond.getOrder();
                Integer charge = neighbor.getFormalCharge();
                if (order == IBond.Order.SINGLE && charge != null && charge == -1) {
                    singleBondedNegativeOxygenCount++;
                } else if (order == IBond.Order.DOUBLE) {
                    doubleBondedOxygenCount++;
                }
            }
        }
        return (oxygenCount == 2) && (singleBondedNegativeOxygenCount == 1) && (doubleBondedOxygenCount == 1);
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
    			} else if (atomContainer.getBond(atom, nextAtom).getFlag(CDKConstants.ISAROMATIC)) {
                    // two aromatic bonds indicate sp2
                    count++;
                } // OK, not SP2
    		}
    	}
    	return count >= 2;
    }

    private boolean bothNeighborsAreSp2(IAtom atom, IAtomContainer atomContainer) {       
    	return atLeastTwoNeighborsAreSp2(atom, atomContainer);
    }

    private IAtomType perceiveNitrogenRadicals(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atomContainer.getConnectedBondsCount(atom) >= 1 &&
                atomContainer.getConnectedBondsCount(atom) <= 2) {
            IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
            if (atom.getFormalCharge() != CDKConstants.UNSET &&
                    atom.getFormalCharge() == +1) {
                if (maxBondOrder == IBond.Order.DOUBLE) {
                    IAtomType type = getAtomType("N.plus.sp2.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (maxBondOrder == IBond.Order.SINGLE) {
                    IAtomType type = getAtomType("N.plus.sp3.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            } else if (atom.getFormalCharge() == CDKConstants.UNSET ||
                    atom.getFormalCharge() == 0) {
                if (maxBondOrder == IBond.Order.SINGLE) {
                    IAtomType type = getAtomType("N.sp3.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (maxBondOrder == IBond.Order.DOUBLE) {
                    IAtomType type = getAtomType("N.sp2.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            }
        } else {
            IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
            if (atom.getFormalCharge() != CDKConstants.UNSET &&
                    atom.getFormalCharge() == +1 && maxBondOrder == IBond.Order.SINGLE) {
                IAtomType type = getAtomType("N.plus.sp3.radical");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        }
        return null;
    }
    private IAtomType perceiveMolybdenum(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0) {
            int neighbors = atomContainer.getConnectedAtomsCount(atom);
            if (neighbors == 4) {
                IAtomType type = getAtomType("Mo.4");
                if (isAcceptable(atom, atomContainer, type)) {
                    return type;
                }
            }
            IAtomType type1 = getAtomType("Mo.metallic");
            if (isAcceptable(atom, atomContainer, type1)) {
                return type1;
            }
        }
        return null;
    }
    private IAtomType perceiveNitrogens(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        // if hybridization is given, use that
        if (hasOneSingleElectron(atomContainer, atom)) {
            return perceiveNitrogenRadicals(atomContainer, atom);
        } else if (hasHybridization(atom) && !isCharged(atom)) {
            if (atom.getHybridization() == Hybridization.SP1) {
                int neighborCount = atomContainer.getConnectedAtomsCount(atom);
                if (neighborCount > 1) {
                    IAtomType type = getAtomType("N.sp1.2");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else {
                    IAtomType type = getAtomType("N.sp1");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            } else if (atom.getHybridization() == Hybridization.SP2) {
            	if (isAmide(atom, atomContainer)) {
                    IAtomType type = getAtomType("N.amide");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (isThioAmide(atom, atomContainer)) {
                    IAtomType type = getAtomType("N.thioamide");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
                // but an sp2 hyb N might N.sp2 or N.planar3 (pyrrole), so check for the latter
            	int neighborCount = atomContainer.getConnectedAtomsCount(atom);
            	if (neighborCount == 4 &&
            	    IBond.Order.DOUBLE == atomContainer.getMaximumBondOrder(atom)) {
            	    IAtomType type = getAtomType("N.oxide");
                    if (isAcceptable(atom, atomContainer, type)) return type;
            	} else
            	if (neighborCount > 1 && bothNeighborsAreSp2(atom, atomContainer)) {
            		IRing ring = getRing(atom, atomContainer);
            		int ringSize = ring == null ? 0 : ring.getAtomCount();
            		if (ring != null && ring.getAtomCount() > 0) {
            			if (neighborCount == 3) {
                            IBond.Order maxOrder = atomContainer.getMaximumBondOrder(atom);
                            if (maxOrder == IBond.Order.DOUBLE) {
                                IAtomType type = getAtomType("N.sp2.3");
                                if (isAcceptable(atom, atomContainer, type)) return type;
                            } else if (maxOrder == IBond.Order.SINGLE) {
                                IAtomType type = getAtomType("N.planar3");
                                if (isAcceptable(atom, atomContainer, type)) return type;
                            }
            			} else if (neighborCount == 2) {
            				IBond.Order maxOrder = atomContainer.getMaximumBondOrder(atom);
            				if (maxOrder == IBond.Order.SINGLE) {
            				    if (atom.getImplicitHydrogenCount() != CDKConstants.UNSET && atom.getImplicitHydrogenCount() == 1) {
            						IAtomType type = getAtomType("N.planar3");
            						if (isAcceptable(atom, atomContainer, type)) return type;
            					} else {
            						IAtomType type = getAtomType("N.sp2");
            						if (isAcceptable(atom, atomContainer, type)) return type;
            					}
            				} else if (maxOrder == IBond.Order.DOUBLE) {
            					IAtomType type = getAtomType("N.sp2");
            					if (isAcceptable(atom, atomContainer, type)) return type;
            				}
            			}
            		}
            	}
                IAtomType type = getAtomType("N.sp2");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (atom.getHybridization() == Hybridization.SP3) {
                IAtomType type = getAtomType("N.sp3");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (atom.getHybridization() == Hybridization.PLANAR3) {
                IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
                if (atomContainer.getConnectedAtomsCount(atom) == 3 &&
                        maxBondOrder == CDKConstants.BONDORDER_DOUBLE &&
                        countAttachedDoubleBonds(atomContainer, atom, "O") == 2) {
                    IAtomType type = getAtomType("N.nitro");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
                IAtomType type = getAtomType("N.planar3");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (isCharged(atom)) {
            if (atom.getFormalCharge() == 1) {
                IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
                if (maxBondOrder == CDKConstants.BONDORDER_SINGLE ||
                        atomContainer.getConnectedBondsCount(atom) == 0) {
                    if (atom.getHybridization() == IAtomType.Hybridization.SP2) {
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
                    if (atomContainer.getConnectedAtomsCount(atom) >= 2 &&
                    		bothNeighborsAreSp2(atom,atomContainer) &&
                    		isRingAtom(atom, atomContainer)) {
                        IAtomType type = getAtomType("N.minus.planar3");
                        if (isAcceptable(atom, atomContainer, type)) return type;
                    } else if (atomContainer.getConnectedBondsCount(atom) <= 2) {
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
            if (atomContainer.getConnectedBondsCount(atom) == 4 &&
                countAttachedDoubleBonds(atomContainer, atom) == 1) {
                IAtomType type = getAtomType("N.oxide");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
            return null;
        } else if (atomContainer.getConnectedBondsCount(atom) == 0) {
            IAtomType type = getAtomType("N.sp3");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else { // OK, use bond order info
            IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
            if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
                if (isAmide(atom, atomContainer)) {
                    IAtomType type = getAtomType("N.amide");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (isThioAmide(atom, atomContainer)) {
                    IAtomType type = getAtomType("N.thioamide");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
                int explicitHydrogens = countExplicitHydrogens(atom, atomContainer);
                int connectedHeavyAtoms = atomContainer.getConnectedBondsCount(atom) - explicitHydrogens;
                if (connectedHeavyAtoms == 2) {
                	List<IBond> bonds = atomContainer.getConnectedBondsList(atom);
                    if (bonds.get(0).getFlag(CDKConstants.ISAROMATIC) &&
                            bonds.get(1).getFlag(CDKConstants.ISAROMATIC)) {
                        Integer hCount = atom.getImplicitHydrogenCount();
                        if (hCount == CDKConstants.UNSET || hCount == 0) {
                            if (atomContainer.getMaximumBondOrder(atom) == CDKConstants.BONDORDER_SINGLE &&
                                    isSingleHeteroAtom(atom, atomContainer)) {
                                IAtomType type = getAtomType("N.planar3");
                                if (isAcceptable(atom, atomContainer, type)) return type;
                            } else {
                                IAtomType type = getAtomType("N.sp2");
                                if (isAcceptable(atom, atomContainer, type)) return type;
                            }
                        } else if (hCount == 1) {
                            IAtomType type = getAtomType("N.planar3");
                            if (isAcceptable(atom, atomContainer, type))
                                return type;
                        }
                	} else if (bothNeighborsAreSp2(atom, atomContainer) && isRingAtom(atom, atomContainer)) {
                		// a N.sp3 which is expected to take part in an aromatic system
                		IAtomType type = getAtomType("N.planar3");
                		if (isAcceptable(atom, atomContainer, type)) return type;
                	} else {
                		IAtomType type = getAtomType("N.sp3");
                		if (isAcceptable(atom, atomContainer, type)) return type;
                	}
                } else if (connectedHeavyAtoms == 3) {
                	if (bothNeighborsAreSp2(atom, atomContainer) && isRingAtom(atom, atomContainer)) {
                		IAtomType type = getAtomType("N.planar3");
                		if (isAcceptable(atom, atomContainer, type)) return type;
                	}
                	IAtomType type = getAtomType("N.sp3");
                	if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (connectedHeavyAtoms == 1) {
                    IAtomType type = getAtomType("N.sp3");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (connectedHeavyAtoms == 0) {
                    IAtomType type = getAtomType("N.sp3");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            } else if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
                if (atomContainer.getConnectedAtomsCount(atom) == 3 &&
                        countAttachedDoubleBonds(atomContainer, atom, "O") == 2) {
                    IAtomType type = getAtomType("N.nitro");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (atomContainer.getConnectedAtomsCount(atom) == 3 &&
                        countAttachedDoubleBonds(atomContainer, atom) > 0) {
                    IAtomType type = getAtomType("N.sp2.3");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
                IAtomType type = getAtomType("N.sp2");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (maxBondOrder == CDKConstants.BONDORDER_TRIPLE) {
                int neighborCount = atomContainer.getConnectedAtomsCount(atom);
                if (neighborCount > 1) {
                    IAtomType type = getAtomType("N.sp1.2");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else {
                    IAtomType type = getAtomType("N.sp1");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            }
        }
    	return null;
    }

    /**
     * Determines whether the bonds (up to two spheres away) are only to non
     * hetroatoms. Currently used in N.planar3 perception of (e.g. pyrrole).
     *
     * @param atom an atom to test
     * @param container container of the atom
     *
     * @return whether the atom's only bonds are to hetroatoms
     * @see #perceiveNitrogens(org.openscience.cdk.interfaces.IAtomContainer, org.openscience.cdk.interfaces.IAtom)
     */
    private boolean isSingleHeteroAtom(IAtom atom, IAtomContainer container) {

        List<IAtom> connected = container.getConnectedAtomsList(atom);

        for (IAtom atom1 : connected) {

            boolean aromatic = container.getBond(atom, atom1).getFlag(CDKConstants.ISAROMATIC);

            // ignoring non-aromatic bonds
            if(!aromatic)
                continue;

            // found a hetroatom - we're not a single hetroatom
            if(!"C".equals(atom1.getSymbol()))
                return false;

            // check the second sphere
            for (IAtom atom2 : container.getConnectedAtomsList(atom1)) {

                if (atom2 != atom
                   && container.getBond(atom1, atom2).getFlag(CDKConstants.ISAROMATIC)
                   && !"C".equals(atom2.getSymbol())) {
                        return false;
                }

            }

        }

        return true;

    }

    private boolean isRingAtom(IAtom atom, IAtomContainer atomContainer) {
    	SpanningTree st = new SpanningTree(atomContainer);
        return st.getCyclicFragmentsContainer().contains(atom);
    }

    private IRing getRing(IAtom atom, IAtomContainer atomContainer) {
    	SpanningTree st = new SpanningTree(atomContainer);
    	try {
    		if (st.getCyclicFragmentsContainer().contains(atom)) {
    			IRingSet set = st.getAllRings();
    			for (int i=0; i<set.getAtomContainerCount(); i++) {
    				IRing ring = (IRing)set.getAtomContainer(i);
    				if (ring.contains(atom)) {
    					return ring;
    				}
    			}
    		}
    	} catch (NoSuchAtomException exception) {
    		return null;
    	}
    	return null;
    }

    private boolean isAmide(IAtom atom, IAtomContainer atomContainer) {
    	List<IAtom> neighbors = atomContainer.getConnectedAtomsList(atom);
    	for (IAtom neighbor : neighbors) {
    		if (neighbor.getSymbol().equals("C")) {
    			if (countAttachedDoubleBonds(atomContainer, neighbor, "O") == 1) return true;
    		}
    	}
    	return false;
    }

    private boolean isThioAmide(IAtom atom, IAtomContainer atomContainer) {
        List<IAtom> neighbors = atomContainer.getConnectedAtomsList(atom);
        for (IAtom neighbor : neighbors) {
            if (neighbor.getSymbol().equals("C")) {
                if (countAttachedDoubleBonds(atomContainer, neighbor, "S") == 1) return true;
            }
        }
        return false;
    }

    private int countExplicitHydrogens(IAtom atom, IAtomContainer atomContainer) {
    	int count = 0;
        for (IAtom aAtom : atomContainer.getConnectedAtomsList(atom)) {
            if (aAtom.getSymbol().equals("H")) {
                count++;
            }
        }
    	return count;
    }
    
    private IAtomType perceiveIron(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if ("Fe".equals(atom.getSymbol())) {
            if (hasOneSingleElectron(atomContainer, atom)) {
                // no idea how to deal with this yet
                return null;
            } else if ((atom.getFormalCharge() != null
                    && atom.getFormalCharge() == 0)) {
                IAtomType type = getAtomType("Fe.metallic");
                if (isAcceptable(atom, atomContainer, type)) {
                    return type;
                }
                int neighbors = atomContainer.getConnectedAtomsCount(atom);
                if (neighbors == 2) {
                    IAtomType type5 = getAtomType("Fe.2");
                    if (isAcceptable(atom, atomContainer, type5)) {
                        return type5;
                    }
                } else if (neighbors == 3) {
                    IAtomType type6 = getAtomType("Fe.3");
                    if (isAcceptable(atom, atomContainer, type6)) {
                        return type6;
                    }
                } else if (neighbors == 4) {
                    IAtomType type7 = getAtomType("Fe.4");
                    if (isAcceptable(atom, atomContainer, type7)) {
                        return type7;
                    }
                } else if (neighbors == 5) {
                    IAtomType type8 = getAtomType("Fe.5");
                    if (isAcceptable(atom, atomContainer, type8)) {
                        return type8;
                    }
                } else if (neighbors == 6) {
                    IAtomType type9 = getAtomType("Fe.6");
                    if (isAcceptable(atom, atomContainer, type9)) {
                        return type9;
                    }
                }
            } else if ((atom.getFormalCharge() != null
                    && atom.getFormalCharge() == 2)) {
                int neighbors = atomContainer.getConnectedAtomsCount(atom);
                if (neighbors <= 1) {
                    IAtomType type = getAtomType("Fe.2plus");
                    if (isAcceptable(atom, atomContainer, type)) {
                        return type;
                    }
                }
            } else if ((atom.getFormalCharge() != null
                    && atom.getFormalCharge() == 1)) {
                int neighbors = atomContainer.getConnectedAtomsCount(atom);

                if (neighbors == 2) {
                    IAtomType type0 = getAtomType("Fe.plus");
                    if (isAcceptable(atom, atomContainer, type0)) {
                        return type0;
                    }
                }
            } else if ((atom.getFormalCharge() != null
                    && atom.getFormalCharge() == 3)) {
                IAtomType type1 = getAtomType("Fe.3plus");
                if (isAcceptable(atom, atomContainer, type1)) {
                    return type1;
                }
            } else if ((atom.getFormalCharge() != null
                    && atom.getFormalCharge() == -2)) {
                IAtomType type2 = getAtomType("Fe.2minus");
                if (isAcceptable(atom, atomContainer, type2)) {
                    return type2;
                }
            } else if ((atom.getFormalCharge() != null
                    && atom.getFormalCharge() == -3)) {
                IAtomType type3 = getAtomType("Fe.3minus");
                if (isAcceptable(atom, atomContainer, type3)) {
                    return type3;
                }
            } else if ((atom.getFormalCharge() != null
                    && atom.getFormalCharge() == -4)) {
                IAtomType type4 = getAtomType("Fe.4minus");
                if (isAcceptable(atom, atomContainer, type4)) {
                    return type4;
                }
            }
        }
        return null;
    }


    private IAtomType perceiveMercury(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if ("Hg".equals(atom.getSymbol())) {
            if (hasOneSingleElectron(atomContainer, atom)) {
                // no idea how to deal with this yet
                return null;
            } else if ((atom.getFormalCharge() != null
                    && atom.getFormalCharge() == -1)) {
                IAtomType type = getAtomType("Hg.minus");
                if (isAcceptable(atom, atomContainer, type)) {
                    return type;
                }
            } else if ((atom.getFormalCharge() != null
                    && atom.getFormalCharge() == 2)) {
                IAtomType type = getAtomType("Hg.2plus");
                if (isAcceptable(atom, atomContainer, type)) {
                    return type;
                }
            } else if ((atom.getFormalCharge() != null
                    && atom.getFormalCharge() == +1)) {
                int neighbors = atomContainer.getConnectedAtomsCount(atom);
                if (neighbors <= 1) {  
                    IAtomType type = getAtomType("Hg.plus");
                    if (isAcceptable(atom, atomContainer, type)) {
                        return type;
                    }
                }
            } else if ((atom.getFormalCharge() != null
                    && atom.getFormalCharge() == 0)) {
                int neighbors = atomContainer.getConnectedAtomsCount(atom);
                if (neighbors == 2) {
                    IAtomType type = getAtomType("Hg.2");
                    if (isAcceptable(atom, atomContainer, type)) {
                        return type;
                    }
                } else if (neighbors == 1) {
                    IAtomType type = getAtomType("Hg.1");
                    if (isAcceptable(atom, atomContainer, type)) {
                        return type;
                    }
                } else if (neighbors == 0) {
                    IAtomType type = getAtomType("Hg.metallic");
                    if (isAcceptable(atom, atomContainer, type)) {
                        return type;
                    }
                }
            }
        }
        return null;
    }

    private IAtomType perceiveSulphurs(IAtomContainer atomContainer, IAtom atom)
    throws CDKException {
        List<IBond> neighbors = atomContainer.getConnectedBondsList(atom);
        IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
        int neighborcount = neighbors.size();
        if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if (atom.getHybridization() != CDKConstants.UNSET &&
                   atom.getHybridization() == Hybridization.SP2 &&
                   atom.getFormalCharge() != CDKConstants.UNSET &&
                   atom.getFormalCharge() == +1) {
            if (neighborcount == 3) {
                IAtomType type = getAtomType("S.inyl.charged");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else {
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
            } else if (atom.getFormalCharge() == +1 &&
                    neighborcount == 3) {
                IAtomType type = getAtomType("S.inyl.charged");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (atom.getFormalCharge() == +2 &&
                    neighborcount == 4) {
                IAtomType type = getAtomType("S.onyl.charged");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (atom.getFormalCharge() == -2 &&
                    neighborcount == 0) {
                IAtomType type = getAtomType("S.2minus");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (neighborcount == 0) {
            if (atom.getFormalCharge() != CDKConstants.UNSET &&
                atom.getFormalCharge() == 0) {
                IAtomType type = getAtomType("S.3");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (neighborcount == 1) {
            if (atomContainer.getConnectedBondsList(atom).get(0).getOrder() == CDKConstants.BONDORDER_DOUBLE) {
                IAtomType type = getAtomType("S.2");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (atomContainer.getConnectedBondsList(atom).get(0).getOrder() == CDKConstants.BONDORDER_SINGLE) {
                IAtomType type = getAtomType("S.3");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (neighborcount == 2) {
            if (isRingAtom(atom, atomContainer) && bothNeighborsAreSp2(atom, atomContainer)) {
                if (countAttachedDoubleBonds(atomContainer, atom) == 2) {
                    IAtomType type = getAtomType("S.inyl.2");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else {
                    IAtomType type = getAtomType("S.planar3");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            } else if (countAttachedDoubleBonds(atomContainer, atom, "O") == 2) {
                IAtomType type = getAtomType("S.oxide");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (countAttachedDoubleBonds(atomContainer, atom) == 2) {
                IAtomType type = getAtomType("S.inyl.2");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (countAttachedDoubleBonds(atomContainer, atom) <= 1) {
                IAtomType type = getAtomType("S.3");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (countAttachedDoubleBonds(atomContainer, atom) == 0
                    && countAttachedSingleBonds(atomContainer, atom) == 2) {
                IAtomType type = getAtomType("S.octahedral");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (neighborcount == 3) {
            int doubleBondedAtoms = countAttachedDoubleBonds(atomContainer, atom);
            if (doubleBondedAtoms == 1) {
                IAtomType type = getAtomType("S.inyl");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (doubleBondedAtoms == 3) {
                IAtomType type = getAtomType("S.trioxide");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (doubleBondedAtoms == 0) {
                IAtomType type = getAtomType("S.anyl");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (neighborcount == 4) {
            // count the number of double bonded oxygens
            int doubleBondedOxygens = countAttachedDoubleBonds(atomContainer, atom, "O");
            int doubleBondedNitrogens = countAttachedDoubleBonds(atomContainer, atom, "N");
            int doubleBondedSulphurs = countAttachedDoubleBonds(atomContainer, atom, "S");
            int countAttachedDoubleBonds = countAttachedDoubleBonds(atomContainer, atom);

            if (doubleBondedOxygens + doubleBondedNitrogens == 2) {
                IAtomType type = getAtomType("S.onyl");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (doubleBondedSulphurs == 1
                    && doubleBondedOxygens == 1) {
                IAtomType type = getAtomType("S.thionyl");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
                IAtomType type = getAtomType("S.anyl");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (doubleBondedOxygens == 1) {
                IAtomType type = getAtomType("S.sp3d1");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (countAttachedDoubleBonds == 2
                    && maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
                IAtomType type = getAtomType("S.sp3.4");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }

        } else if (neighborcount == 5) {

            if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {

                IAtomType type = getAtomType("S.sp3d1");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
                IAtomType type = getAtomType("S.octahedral");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (neighborcount == 6) {
            if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
                IAtomType type = getAtomType("S.octahedral");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        }
        return null;
    }

    private IAtomType perceivePhosphors(IAtomContainer atomContainer, IAtom atom)
    throws CDKException {
        List<IBond> neighbors = atomContainer.getConnectedBondsList(atom);
        int neighborcount = neighbors.size();
        IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
        if (countSingleElectrons(atomContainer, atom) == 3) {
        	IAtomType type = getAtomType("P.se.3");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if (neighborcount == 0) {
            if (atom.getFormalCharge() == null ||
                atom.getFormalCharge().intValue() == 0) {
                IAtomType type = getAtomType("P.ine");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (neighborcount == 1) {
            if (atom.getFormalCharge() == null ||
                atom.getFormalCharge().intValue() == 0) {
                IAtomType type = getAtomType("P.ide");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (neighborcount == 3) {
        	int doubleBonds = countAttachedDoubleBonds(atomContainer, atom);
            if (atom.getFormalCharge() != null &
                atom.getFormalCharge().intValue() == 1) {
                IAtomType type = getAtomType("P.anium");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (doubleBonds == 1) {
            	IAtomType type = getAtomType("P.ate");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else {
                IAtomType type = getAtomType("P.ine");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (neighborcount == 2) {
            if (maxBondOrder == CDKConstants.BONDORDER_DOUBLE) {
                if (atom.getFormalCharge() != null &
                    atom.getFormalCharge().intValue() == 1) {
                    IAtomType type = getAtomType("P.sp1.plus");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else {
                    IAtomType type = getAtomType("P.irane");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            } else if (maxBondOrder == CDKConstants.BONDORDER_SINGLE) {
                IAtomType type = getAtomType("P.ine");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (neighborcount == 4) {
            // count the number of double bonded oxygens
            int doubleBonds = countAttachedDoubleBonds(atomContainer, atom);
            if (atom.getFormalCharge() == 1 && doubleBonds == 0) {
                IAtomType type = getAtomType("P.ate.charged");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (doubleBonds == 1){
                IAtomType type = getAtomType("P.ate");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (neighborcount == 5) {
            if (atom.getFormalCharge() == null ||
                atom.getFormalCharge().intValue() == 0) {
                IAtomType type = getAtomType("P.ane");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        }
    	return null;
    }
    
    private IAtomType perceiveHydrogens(IAtomContainer atomContainer, IAtom atom)
    throws CDKException {
        int neighborcount = atomContainer.getConnectedBondsCount(atom);
        if (hasOneSingleElectron(atomContainer, atom)) {
            if ((atom.getFormalCharge() == CDKConstants.UNSET || atom.getFormalCharge() == 0) &&
                    neighborcount == 0) {
                IAtomType type = getAtomType("H.radical");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
            return null;
        } else if (neighborcount == 2) {
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
    	return null;
    }

    private IAtomType perceiveLithium(IAtomContainer atomContainer, IAtom atom)
    	throws CDKException {
        int neighborcount = atomContainer.getConnectedBondsCount(atom);
        if (neighborcount == 1) {
            if (atom.getFormalCharge() == CDKConstants.UNSET ||
                    atom.getFormalCharge() == 0) {
                IAtomType type = getAtomType("Li");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (neighborcount == 0) {
            if (atom.getFormalCharge() == CDKConstants.UNSET
                    || atom.getFormalCharge() == 0) {
                IAtomType type = getAtomType("Li.neutral");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
            if (atom.getFormalCharge() == CDKConstants.UNSET
                    || atom.getFormalCharge() == +1) {
                IAtomType type = getAtomType("Li.plus");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        }
    	return null;
    }

    private IAtomType perceiveHalogens(IAtomContainer atomContainer, IAtom atom)
    throws CDKException {
    	if ("F".equals(atom.getSymbol())) {
    		if (hasOneSingleElectron(atomContainer, atom)) {
				if (atomContainer.getConnectedBondsCount(atom) == 0) {
					if (atom.getFormalCharge() != CDKConstants.UNSET &&
						atom.getFormalCharge() == +1) {
						IAtomType type = getAtomType("F.plus.radical");
						if (isAcceptable(atom, atomContainer, type)) return type;
					} else if (atom.getFormalCharge() == CDKConstants.UNSET ||
							   atom.getFormalCharge() == 0) {
						IAtomType type = getAtomType("F.radical");
						if (isAcceptable(atom, atomContainer, type)) return type;
					}
				} else if (atomContainer.getConnectedBondsCount(atom) <= 1) {
					IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
					if (maxBondOrder == IBond.Order.SINGLE) {
						IAtomType type = getAtomType("F.plus.radical");
						if (isAcceptable(atom, atomContainer, type)) return type;
					}
				}
				return null;
    		} else if (atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() != 0) {
    			if (atom.getFormalCharge() == -1) {
    				IAtomType type = getAtomType("F.minus");
    				if (isAcceptable(atom, atomContainer, type)) return type;
    			} else if (atom.getFormalCharge() == 1) {
    				IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
    				if (maxBondOrder == IBond.Order.DOUBLE) {
    					IAtomType type = getAtomType("F.plus.sp2");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				}else if (maxBondOrder == IBond.Order.SINGLE){
    					IAtomType type = getAtomType("F.plus.sp3");
    					if (isAcceptable(atom, atomContainer, type)) return type;
    				}
    			}
    		} else if (atomContainer.getConnectedBondsCount(atom) == 1 ||
    				atomContainer.getConnectedBondsCount(atom) == 0) {
    			IAtomType type = getAtomType("F");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("I".equals(atom.getSymbol())) {
    	    return perceiveIodine(atomContainer, atom);
    	}
    		
    	return null;
    }
    private IAtomType perceiveArsenic(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == +1
                && atomContainer.getConnectedBondsCount(atom) <= 4)) {
            IAtomType type = getAtomType("As.plus");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0)) {
            int neighbors = atomContainer.getConnectedAtomsCount(atom);
            if (neighbors == 4) {
                IAtomType type = getAtomType("As.5");
                if (isAcceptable(atom, atomContainer, type)) {
                    return type;
                }
            }
            if (neighbors == 2) {
                IAtomType type = getAtomType("As.2");
                if (isAcceptable(atom, atomContainer, type)) {
                    return type;
                }
            }
            IAtomType type = getAtomType("As");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == +3)) {
            IAtomType type = getAtomType("As.3plus");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == -1)) {
            IAtomType type = getAtomType("As.minus");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        }
        return null;
    }   
     
    private IAtomType perceiveThorium(IAtomContainer atomContainer, IAtom atom)
            throws CDKException {
        if ("Th".equals(atom.getSymbol())) {
            if (atom.getFormalCharge() == 0 && atomContainer.getConnectedBondsCount(atom) == 0) {
                IAtomType type = getAtomType("Th");
                if (isAcceptable(atom, atomContainer, type)) {
                    return type;
                }
            }
        }
        return null;
    }

    private IAtomType perceiveRubidium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            return null;
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == +1) {
            IAtomType type = getAtomType("Rb.plus");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0) {
            IAtomType type = getAtomType("Rb.neutral");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        }
        return null;
    }
    private IAtomType perceiveCommonSalts(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if ("Mg".equals(atom.getSymbol())) {
    		if (hasOneSingleElectron(atomContainer, atom)) {
    			// no idea how to deal with this yet
    			return null;
    		} else if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +2)) {
    			IAtomType type = getAtomType("Mg.2plus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("Co".equals(atom.getSymbol())) {
    		if (hasOneSingleElectron(atomContainer, atom)) {
    			// no idea how to deal with this yet
    			return null;
    		} else if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +2)) {
    			IAtomType type = getAtomType("Co.2plus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		} else if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == +3)) {
    			IAtomType type = getAtomType("Co.3plus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
            } else if ((atom.getFormalCharge() == CDKConstants.UNSET ||
                    atom.getFormalCharge() == 0)) {
                IAtomType type = getAtomType("Co.metallic");
                if (isAcceptable(atom, atomContainer, type)) return type;
    		}
        } else if ("W".equals(atom.getSymbol())) {
            if (hasOneSingleElectron(atomContainer, atom)) {
                // no idea how to deal with this yet
                return null;
            } else if ((atom.getFormalCharge() == CDKConstants.UNSET ||
                    atom.getFormalCharge() == 0)) {
                IAtomType type = getAtomType("W.metallic");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
    	}
    	return null;
    }
    private IAtomType perceiveCopper(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == +2)) {
            IAtomType type = getAtomType("Cu.2plus");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0) {
            int neighbors = atomContainer.getConnectedAtomsCount(atom);
            if (neighbors == 1) {
                IAtomType type = getAtomType("Cu.1");
                if (isAcceptable(atom, atomContainer, type)) {
                    return type;
                }
            } else {
                IAtomType type01 = getAtomType("Cu.metallic");
                if (isAcceptable(atom, atomContainer, type01)) {
                    return type01;
                }
            }
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == +1) {
            IAtomType type02 = getAtomType("Cu.plus");
            if (isAcceptable(atom, atomContainer, type02)) {
                return type02;
            }
        }
        return null;
    }
    private IAtomType perceiveBarium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 2)) {
            IAtomType type = getAtomType("Ba.2plus");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        }
        return null;
    }
    private IAtomType perceiveAluminium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 3) {
            int connectedBondsCount = atomContainer.getConnectedBondsCount(atom);
            if (connectedBondsCount == 0) {
                IAtomType type = getAtomType("Al.3plus");
                if (isAcceptable(atom, atomContainer, type)) {
                    return type;
                }
            }
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0
                && atomContainer.getConnectedBondsCount(atom) == 3) {
            IAtomType type = getAtomType("Al");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == -3
                && atomContainer.getConnectedBondsCount(atom) == 6) {
            IAtomType type = getAtomType("Al.3minus");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        }
        return null;
    }
    private IAtomType perceiveZinc(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if (atomContainer.getConnectedBondsCount(atom) == 0
                && (atom.getFormalCharge() != null
                && atom.getFormalCharge() == 0)) {
            IAtomType type = getAtomType("Zn.metallic");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atomContainer.getConnectedBondsCount(atom) == 0
                && (atom.getFormalCharge() != null
                && atom.getFormalCharge() == 2)) {
            IAtomType type = getAtomType("Zn.2plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atomContainer.getConnectedBondsCount(atom) == 1
                && (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0)) {
            IAtomType type = getAtomType("Zn.1");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atomContainer.getConnectedBondsCount(atom) == 2
                && (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0)) {
            IAtomType type = getAtomType("Zn");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    private IAtomType perceiveChromium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0
                && atomContainer.getConnectedBondsCount(atom) == 6) {
            IAtomType type = getAtomType("Cr");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0
                && atomContainer.getConnectedBondsCount(atom) == 4) {
            IAtomType type = getAtomType("Cr.4");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 6
                && atomContainer.getConnectedBondsCount(atom) == 0) {
            IAtomType type = getAtomType("Cr.6plus");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0
                && atomContainer.getConnectedBondsCount(atom) == 0) {
            IAtomType type = getAtomType("Cr.neutral");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        } else if ("Cr".equals(atom.getSymbol())) {
            if (atom.getFormalCharge() != CDKConstants.UNSET
                    && atom.getFormalCharge() == 3
                    && atomContainer.getConnectedBondsCount(atom) == 0) {
                IAtomType type = getAtomType("Cr.3plus");
                if (isAcceptable(atom, atomContainer, type)) {
                    return type;
                }
            }
        }
        return null;
    }
    private IAtomType perceiveOrganometallicCenters(IAtomContainer atomContainer, IAtom atom) throws CDKException {
    	if ("Po".equals(atom.getSymbol())) {
    		if (hasOneSingleElectron(atomContainer, atom)) {
    			// no idea how to deal with this yet
    			return null;
    		} else if (atomContainer.getConnectedBondsCount(atom) == 2) {
    			IAtomType type = getAtomType("Po");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	}  else if ("Sn".equals(atom.getSymbol())) {
    		if (hasOneSingleElectron(atomContainer, atom)) {
    			// no idea how to deal with this yet
    			return null;
    		} else if ((atom.getFormalCharge() != CDKConstants.UNSET &&
    				atom.getFormalCharge() == 0 &&
    				atomContainer.getConnectedBondsCount(atom) <= 4)) {
    			IAtomType type = getAtomType("Sn.sp3");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("Sc".equals(atom.getSymbol())) {
    		if (atom.getFormalCharge() != CDKConstants.UNSET &&
    			atom.getFormalCharge() == -3 &&
    			atomContainer.getConnectedBondsCount(atom) == 6) {
    			IAtomType type = getAtomType("Sc.3minus");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} 
    	return null;
    }
    private IAtomType perceiveNickel(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == +2)) {
            IAtomType type = getAtomType("Ni.2plus");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0)
                && atomContainer.getConnectedAtomsCount(atom) == 2) {
            IAtomType type = getAtomType("Ni");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0)
                && atomContainer.getConnectedAtomsCount(atom) == 0) {
            IAtomType type = getAtomType("Ni.metallic");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 1)
                && atomContainer.getConnectedAtomsCount(atom) == 1) {
            IAtomType type = getAtomType("Ni.plus");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        }
        return null;
    }
    private IAtomType perceiveNobelGases(IAtomContainer atomContainer, IAtom atom) throws CDKException {
    	if ("He".equals(atom.getSymbol())) {
    		if (hasOneSingleElectron(atomContainer, atom)) {
    			// no idea how to deal with this yet
    			return null;
    		} else if ((atom.getFormalCharge() == CDKConstants.UNSET ||
    				atom.getFormalCharge() == 0)) {
    			IAtomType type = getAtomType("He");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("Ne".equals(atom.getSymbol())) {
    		if (hasOneSingleElectron(atomContainer, atom)) {
    			// no idea how to deal with this yet
    			return null;
    		} else if ((atom.getFormalCharge() == CDKConstants.UNSET ||
    				atom.getFormalCharge() == 0)) {
    			IAtomType type = getAtomType("Ne");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("Ar".equals(atom.getSymbol())) {
    		if (hasOneSingleElectron(atomContainer, atom)) {
    			// no idea how to deal with this yet
    			return null;
    		} else if ((atom.getFormalCharge() == CDKConstants.UNSET ||
    				atom.getFormalCharge() == 0)) {
    			IAtomType type = getAtomType("Ar");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("Kr".equals(atom.getSymbol())) {
    		if (hasOneSingleElectron(atomContainer, atom)) {
    			// no idea how to deal with this yet
    			return null;
    		} else if ((atom.getFormalCharge() == CDKConstants.UNSET ||
    				atom.getFormalCharge() == 0)) {
    			IAtomType type = getAtomType("Kr");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	} else if ("Xe".equals(atom.getSymbol())) {
    		if (hasOneSingleElectron(atomContainer, atom)) {
    			// no idea how to deal with this yet
    			return null;
    		} else if ((atom.getFormalCharge() == CDKConstants.UNSET ||
    				atom.getFormalCharge() == 0)) {
            if (atomContainer.getConnectedBondsCount(atom) == 0) {
                IAtomType type = getAtomType("Xe");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else {
                IAtomType type = getAtomType("Xe.3");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
    		}
    	} else if ("Rn".equals(atom.getSymbol())) {
    		if (hasOneSingleElectron(atomContainer, atom)) {
    			// no idea how to deal with this yet
    			return null;
    		} else if ((atom.getFormalCharge() == CDKConstants.UNSET ||
    				atom.getFormalCharge() == 0)) {
    			IAtomType type = getAtomType("Rn");
    			if (isAcceptable(atom, atomContainer, type)) return type;
    		}
    	}
    	return null;
    }

    private IAtomType perceiveSilicon(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0) {
            if (atomContainer.getConnectedBondsCount(atom) == 2) {
                IAtomType type = getAtomType("Si.2");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (atomContainer.getConnectedBondsCount(atom) == 3) {
                IAtomType type = getAtomType("Si.3");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (atomContainer.getConnectedBondsCount(atom) == 4) {
                IAtomType type = getAtomType("Si.sp3");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == -2) {
            IAtomType type = getAtomType("Si.2minus.6");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveManganese(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if ((atom.getFormalCharge() != null
                && atom.getFormalCharge() == 0)) {
            int neighbors = atomContainer.getConnectedAtomsCount(atom);
            if (neighbors == 2) {
                IAtomType type02 = getAtomType("Mn.2");
                if (isAcceptable(atom, atomContainer, type02)) return type02;
            } else if (neighbors == 0) {
                IAtomType type03 = getAtomType("Mn.metallic");
                if (isAcceptable(atom, atomContainer, type03)) return type03;
            }
        } else if ((atom.getFormalCharge() != null
                && atom.getFormalCharge() == +2)) {
            IAtomType type = getAtomType("Mn.2plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if ((atom.getFormalCharge() != null
                && atom.getFormalCharge() == +3)) {
            IAtomType type = getAtomType("Mn.3plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveSodium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 1)) {
            IAtomType type = getAtomType("Na.plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if ((atom.getFormalCharge() == CDKConstants.UNSET
                || atom.getFormalCharge() == 0)
                && atomContainer.getConnectedAtomsCount(atom) == 1) {
            IAtomType type = getAtomType("Na");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0)
                && atomContainer.getConnectedAtomsCount(atom) == 0) {
            IAtomType type = getAtomType("Na.neutral");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } 
        return null;
    }
    
    private IAtomType perceiveIodine(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            if (atomContainer.getConnectedBondsCount(atom) == 0) {
                if (atom.getFormalCharge() != CDKConstants.UNSET &&
                    atom.getFormalCharge() == +1) {
                    IAtomType type = getAtomType("I.plus.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (atom.getFormalCharge() == CDKConstants.UNSET ||
                           atom.getFormalCharge() == 0) {
                    IAtomType type = getAtomType("I.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            } else if (atomContainer.getConnectedBondsCount(atom) <= 1) {
                IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
                if (maxBondOrder == IBond.Order.SINGLE) {
                    IAtomType type = getAtomType("I.plus.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            }
            return null;
        } else if (atom.getFormalCharge() != CDKConstants.UNSET && 
               atom.getFormalCharge() != 0) {
      if (atom.getFormalCharge() == -1) {
          if (atomContainer.getConnectedAtomsCount(atom) == 0) {
              IAtomType type = getAtomType("I.minus");
              if (isAcceptable(atom, atomContainer, type)) return type;
          } else {
              IAtomType type = getAtomType("I.minus.5");
              if (isAcceptable(atom, atomContainer, type)) return type;
          }
            } else if (atom.getFormalCharge() == 1) {
                IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
                if (maxBondOrder == IBond.Order.DOUBLE) {
                    IAtomType type = getAtomType("I.plus.sp2");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (maxBondOrder == IBond.Order.SINGLE){
                    IAtomType type = getAtomType("I.plus.sp3");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            }
        } else if (atomContainer.getConnectedBondsCount(atom) == 3) {
            int doubleBondCount = countAttachedDoubleBonds(atomContainer, atom);
            if (doubleBondCount == 2) {
                IAtomType type = getAtomType("I.5");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (atom.getFormalCharge() != CDKConstants.UNSET
                    && atom.getFormalCharge() == 0) {
                IAtomType type = getAtomType("I.sp3d2.3");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (atomContainer.getConnectedBondsCount(atom) == 2) {
            IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
            if (maxBondOrder == IBond.Order.DOUBLE) {
                IAtomType type = getAtomType("I.3");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (atomContainer.getConnectedBondsCount(atom) == 1 ||
                atomContainer.getConnectedBondsCount(atom) == 0) {
            IAtomType type = getAtomType("I");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveRuthenium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0) {
            IAtomType type = getAtomType("Ru.6");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == -2) {
            IAtomType type = getAtomType("Ru.2minus.6");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == -3) {
            IAtomType type = getAtomType("Ru.3minus.6");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceivePotassium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET &&
                atom.getFormalCharge() == +1)) {
            IAtomType type = getAtomType("K.plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() == CDKConstants.UNSET
                || atom.getFormalCharge() == 0) {
            int neighbors = atomContainer.getConnectedAtomsCount(atom);
            if (neighbors == 1) {
                IAtomType type = getAtomType("K.neutral");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
            IAtomType type = getAtomType("K.metallic");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceivePlutonium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atom.getFormalCharge() == 0 && atomContainer.getConnectedBondsCount(atom) == 0) {
            IAtomType type = getAtomType("Pu");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveCadmium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == +2)) {
            IAtomType type = getAtomType("Cd.2plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0)) {
            if (atomContainer.getConnectedAtomsCount(atom) == 0) {
                IAtomType type = getAtomType("Cd.metallic");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (atomContainer.getConnectedAtomsCount(atom) == 2) {
                IAtomType type = getAtomType("Cd.2");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        }
        return null;
    }
    
    private IAtomType perceiveIndium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atom.getFormalCharge() == 0 && atomContainer.getConnectedBondsCount(atom) == 3) {
            IAtomType type = getAtomType("In.3");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() == 3 && atomContainer.getConnectedBondsCount(atom) == 0) {
            IAtomType type = getAtomType("In.3plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() == 0 && atomContainer.getConnectedBondsCount(atom) == 1) {
            IAtomType type = getAtomType("In.1");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else {
            IAtomType type = getAtomType("In");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveChlorine(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            if (atomContainer.getConnectedBondsCount(atom) > 1) {
                if (atom.getFormalCharge() != CDKConstants.UNSET
                        && atom.getFormalCharge() == +1) {
                    IAtomType type = getAtomType("Cl.plus.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            } else if (atomContainer.getConnectedBondsCount(atom) == 1) {
                IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
                if (maxBondOrder == IBond.Order.SINGLE) {
                    IAtomType type = getAtomType("Cl.plus.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            } else if (atomContainer.getConnectedBondsCount(atom) == 0
                    && (atom.getFormalCharge() == CDKConstants.UNSET
                    || atom.getFormalCharge() == 0)) {
                IAtomType type = getAtomType("Cl.radical");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (atom.getFormalCharge() == CDKConstants.UNSET
                || atom.getFormalCharge() == 0) {
            int neighborcount = atomContainer.getConnectedBondsCount(atom);
            IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);

            if (maxBondOrder == IBond.Order.DOUBLE) {
                int neighbor = atomContainer.getConnectedAtomsCount(atom);
                if (neighbor == 2) {
                    IAtomType type = getAtomType("Cl.2");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (neighbor == 3) {
                    IAtomType type = getAtomType("Cl.chlorate");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (neighbor == 4) {
                    IAtomType type = getAtomType("Cl.perchlorate");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            } else if (neighborcount <= 1) {
                IAtomType type = getAtomType("Cl");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == -1)) {
            IAtomType type = getAtomType("Cl.minus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() != CDKConstants.UNSET && atom.getFormalCharge() == 1) {
            IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
            if (maxBondOrder == IBond.Order.DOUBLE) {
                IAtomType type = getAtomType("Cl.plus.sp2");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (maxBondOrder == IBond.Order.SINGLE) {
                IAtomType type = getAtomType("Cl.plus.sp3");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == +3) && atomContainer.getConnectedBondsCount(atom) == 4) {
            IAtomType type = getAtomType("Cl.perchlorate.charged");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else {
            int doubleBonds = countAttachedDoubleBonds(atomContainer, atom);
            if (atomContainer.getConnectedBondsCount(atom) == 3
                    && doubleBonds == 2) {
                IAtomType type = getAtomType("Cl.chlorate");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (atomContainer.getConnectedBondsCount(atom) == 4
                    && doubleBonds == 3) {
                IAtomType type = getAtomType("Cl.perchlorate");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        }
        return null;
    }
    
    private IAtomType perceiveSilver(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0)) {
            int neighbors = atomContainer.getConnectedAtomsCount(atom);
            if (neighbors == 1) {
                IAtomType type = getAtomType("Ag.1");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
            IAtomType type = getAtomType("Ag.neutral");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 1)) {
            IAtomType type = getAtomType("Ag.plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveGold(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            return null;
        }
        int neighbors = atomContainer.getConnectedAtomsCount(atom);
        if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0) && neighbors == 1) {
            IAtomType type = getAtomType("Au.1");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveRadium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0)) {
            IAtomType type = getAtomType("Ra.neutral");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveCalcium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if ("Ca".equals(atom.getSymbol())) {
            if (hasOneSingleElectron(atomContainer, atom)) {
                // no idea how to deal with this yet
                return null;
            } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                    && atom.getFormalCharge() == 2 && atomContainer.getConnectedAtomsCount(atom) == 0)) {
                IAtomType type = getAtomType("Ca.2plus");
                if (isAcceptable(atom, atomContainer, type)) {
                    return type;
                }
            } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                    && atom.getFormalCharge() == 0 && atomContainer.getConnectedAtomsCount(atom) == 2)) {
                IAtomType type = getAtomType("Ca.2");
                if (isAcceptable(atom, atomContainer, type)) {
                    return type;
                }
            } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                    && atom.getFormalCharge() == 0 && atomContainer.getConnectedAtomsCount(atom) == 1)) {
                IAtomType type = getAtomType("Ca.1");
                if (isAcceptable(atom, atomContainer, type)) {
                    return type;
                }
            }
        }
        return null;
    }
    
    private IAtomType perceivePlatinum(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET &&
                atom.getFormalCharge() == +2)) {
            int neighbors = atomContainer.getConnectedAtomsCount(atom);
            if (neighbors == 4) {
                IAtomType type = getAtomType("Pt.2plus.4");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else {
                IAtomType type = getAtomType("Pt.2plus");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if ((atom.getFormalCharge() == CDKConstants.UNSET ||
                atom.getFormalCharge() == 0)) {
            int neighbors = atomContainer.getConnectedAtomsCount(atom);
            if (neighbors == 2) {
                IAtomType type = getAtomType("Pt.2");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (neighbors == 4) {
                IAtomType type = getAtomType("Pt.4");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (neighbors == 6) {
                IAtomType type = getAtomType("Pt.6");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        }
        return null;
    }
    
    private IAtomType perceiveAntimony(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET &&
                    atom.getFormalCharge() == 0 &&
                    atomContainer.getConnectedBondsCount(atom) == 3)) {
            IAtomType type = getAtomType("Sb.3");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET && 
                    atom.getFormalCharge() == 0 &&
                    atomContainer.getConnectedBondsCount(atom) == 4)) {
            IAtomType type = getAtomType("Sb.4");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveGadolinum(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atom.getFormalCharge() != CDKConstants.UNSET &&
            atom.getFormalCharge() == +3 &&
            atomContainer.getConnectedBondsCount(atom) == 0) {
            IAtomType type = getAtomType("Gd.3plus");
            if (isAcceptable(atom, atomContainer, type)) {
                return type;
            }
        }
        return null;
    }

    private IAtomType perceiveMagnesium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET &&
                    atom.getFormalCharge() == 0)) {
            int neighbors = atomContainer.getConnectedAtomsCount(atom);
            if (neighbors == 4) {
                IAtomType type = getAtomType("Mg.neutral");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (neighbors == 2) {
                IAtomType type = getAtomType("Mg.neutral.2");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (neighbors == 1) {
                IAtomType type = getAtomType("Mg.neutral.1");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else {
                IAtomType type = getAtomType("Mg.neutral");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET &&
                atom.getFormalCharge() == +2)) {
            IAtomType type = getAtomType("Mg.2plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveThallium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atom.getFormalCharge() != CDKConstants.UNSET &&
            atom.getFormalCharge() == +1 &&
            atomContainer.getConnectedBondsCount(atom) == 0) {
            IAtomType type = getAtomType("Tl.plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() != CDKConstants.UNSET &&
                   atom.getFormalCharge() == 0 &&
                   atomContainer.getConnectedBondsCount(atom) == 0) {
            IAtomType type = getAtomType("Tl");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() != CDKConstants.UNSET &&
                   atom.getFormalCharge() == 0 &&
                   atomContainer.getConnectedBondsCount(atom) == 1) {
            IAtomType type = getAtomType("Tl.1");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveLead(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atom.getFormalCharge() != CDKConstants.UNSET &&
            atom.getFormalCharge() == 0 &&
            atomContainer.getConnectedBondsCount(atom) == 0) {
            IAtomType type = getAtomType("Pb.neutral");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() != CDKConstants.UNSET &&
                   atom.getFormalCharge() == 2 &&
                   atomContainer.getConnectedBondsCount(atom) == 0) {
            IAtomType type = getAtomType("Pb.2plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() != CDKConstants.UNSET &&
                   atom.getFormalCharge() == 0 &&
                   atomContainer.getConnectedBondsCount(atom) == 1) {
            IAtomType type = getAtomType("Pb.1");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveStrontium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 2)) {
            IAtomType type = getAtomType("Sr.2plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveTitanium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atom.getFormalCharge() != CDKConstants.UNSET &&
            atom.getFormalCharge() == -3 &&
            atomContainer.getConnectedBondsCount(atom) == 6) {
            IAtomType type = getAtomType("Ti.3minus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if ((atom.getFormalCharge() == CDKConstants.UNSET ||
                    atom.getFormalCharge() == 0) &&
                   atomContainer.getConnectedBondsCount(atom) == 4) {
            IAtomType type = getAtomType("Ti.sp3");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == 0)
                && atomContainer.getConnectedBondsCount(atom) == 2) {
            IAtomType type = getAtomType("Ti.2");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveVanadium(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (atom.getFormalCharge() != CDKConstants.UNSET &&
                atom.getFormalCharge() == -3 &&
                atomContainer.getConnectedBondsCount(atom) == 6) {
            IAtomType type = getAtomType("V.3minus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() != CDKConstants.UNSET
                && atom.getFormalCharge() == -3
                && atomContainer.getConnectedBondsCount(atom) == 4) {
            IAtomType type = getAtomType("V.3minus.4");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private IAtomType perceiveBromine(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            if (atomContainer.getConnectedBondsCount(atom) == 0) {
                if (atom.getFormalCharge() != CDKConstants.UNSET &&
                        atom.getFormalCharge() == +1) {
                    IAtomType type = getAtomType("Br.plus.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                } else if (atom.getFormalCharge() == CDKConstants.UNSET ||
                        atom.getFormalCharge() == 0) {
                    IAtomType type = getAtomType("Br.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            } else if (atomContainer.getConnectedBondsCount(atom) <= 1) {
                IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
                if (maxBondOrder == IBond.Order.SINGLE) {
                    IAtomType type = getAtomType("Br.plus.radical");
                    if (isAcceptable(atom, atomContainer, type)) return type;
                }
            }
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET &&
                atom.getFormalCharge() == -1)) {
            IAtomType type = getAtomType("Br.minus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atom.getFormalCharge() == 1) {
            IBond.Order maxBondOrder = atomContainer.getMaximumBondOrder(atom);
            if (maxBondOrder == IBond.Order.DOUBLE) {
                IAtomType type = getAtomType("Br.plus.sp2");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }else if (maxBondOrder == IBond.Order.SINGLE){
                IAtomType type = getAtomType("Br.plus.sp3");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if (atomContainer.getConnectedBondsCount(atom) == 1 ||
                atomContainer.getConnectedBondsCount(atom) == 0) {
            IAtomType type = getAtomType("Br");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if (atomContainer.getConnectedBondsCount(atom) == 3) {
            IAtomType type = getAtomType("Br.3");
            if (isAcceptable(atom, atomContainer, type)) return type;
        }
        return null;
    }
    
    private int countAttachedDoubleBonds(IAtomContainer container, IAtom atom, String symbol) {
        return countAttachedBonds(container, atom, IBond.Order.DOUBLE, symbol);
    }
    
    private IAtomType perceiveCobalt(IAtomContainer atomContainer, IAtom atom) throws CDKException {
        if (hasOneSingleElectron(atomContainer, atom)) {
            // no idea how to deal with this yet
            return null;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET &&
                atom.getFormalCharge() == +2)) {
            IAtomType type = getAtomType("Co.2plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if ((atom.getFormalCharge() != CDKConstants.UNSET &&
                atom.getFormalCharge() == +3)) {
            IAtomType type = getAtomType("Co.3plus");
            if (isAcceptable(atom, atomContainer, type)) return type;
        } else if ((atom.getFormalCharge() == CDKConstants.UNSET ||
                atom.getFormalCharge() == 0)) {
            int neighbors = atomContainer.getConnectedAtomsCount(atom);
            if (neighbors == 2) {
                IAtomType type = getAtomType("Co.2");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (neighbors == 4) {
                IAtomType type = getAtomType("Co.4");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (neighbors == 6) {
                IAtomType type = getAtomType("Co.6");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (neighbors == 1) {
                IAtomType type = getAtomType("Co.1");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else {
                IAtomType type = getAtomType("Co.metallic");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        } else if ((atom.getFormalCharge() != null
                && atom.getFormalCharge() == +1)) {
            int neighbors = atomContainer.getConnectedAtomsCount(atom);
            if (neighbors == 2) {
                IAtomType type = getAtomType("Co.plus.2");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (neighbors == 4) {
                IAtomType type = getAtomType("Co.plus.4");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (neighbors == 1) {
                IAtomType type = getAtomType("Co.plus.1");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (neighbors == 6) {
                IAtomType type = getAtomType("Co.plus.6");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else if (neighbors == 5) {
                IAtomType type = getAtomType("Co.plus.5");
                if (isAcceptable(atom, atomContainer, type)) return type;
            } else {
                IAtomType type = getAtomType("Co.plus");
                if (isAcceptable(atom, atomContainer, type)) return type;
            }
        }
        return null;
    }

    private int countAttachedDoubleBonds(IAtomContainer container, IAtom atom) {
    	return countAttachedBonds(container, atom, IBond.Order.DOUBLE, null);
    }
    
    private int countAttachedSingleBonds(IAtomContainer atomContainer, IAtom atom) {
        return countAttachedBonds(atomContainer, atom, IBond.Order.SINGLE, null);
    }

    private boolean hasAromaticBond(IAtomContainer container, IAtom atom) {
        List<IBond> neighbors = container.getConnectedBondsList(atom);
        for (IBond bond : neighbors) {
            if (bond.getFlag(CDKConstants.ISAROMATIC)) return true;
        }
        return false;
    }

    /**
     * Count the number of doubly bonded atoms.
     *
     * @param container the molecule in which to look
     * @param atom the atom being looked at
     * @param order the desired bond order of the attached bonds 
     * @param symbol If not null, then it only counts the double bonded atoms which
     *               match the given symbol.
     * @return the number of doubly bonded atoms
     */
    private int countAttachedBonds(IAtomContainer container, IAtom atom, IBond.Order order, String symbol) {
    	// count the number of double bonded oxygens
    	List<IBond> neighbors = container.getConnectedBondsList(atom);
    	int neighborcount = neighbors.size();
    	int doubleBondedAtoms = 0;
    	for (int i=neighborcount-1;i>=0;i--) {
            IBond bond =  neighbors.get(i);
    		if (bond.getOrder() == order) {
    			if (bond.getAtomCount() == 2 && bond.contains(atom)) {
    				if (symbol != null) {
    				    // if other atom is a sulphur
    					if ((bond.getAtom(0) != atom &&
    					     bond.getAtom(0).getSymbol().equals(symbol)) ||
    						(bond.getAtom(1) != atom &&
    						 bond.getAtom(1).getSymbol().equals(symbol))) {
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
    		int actualContainerCount = container.getConnectedAtomsCount(atom);
    		int requiredContainerCount = type.getFormalNeighbourCount();
    		if (actualContainerCount != requiredContainerCount)
    			return false;
    	} else if (atom.getImplicitHydrogenCount() != CDKConstants.UNSET) {
    		// confirm correct neighbour count
    		int connectedAtoms = container.getConnectedAtomsCount(atom);
    		int hCount = atom.getImplicitHydrogenCount();
    		int actualNeighbourCount =  connectedAtoms + hCount;
    		int requiredNeighbourCount = type.getFormalNeighbourCount();
    		if (actualNeighbourCount > requiredNeighbourCount)
    			return false;
    	}

    	// confirm correct bond orders
    	if (type.getProperty(CDKConstants.PI_BOND_COUNT) != null && container.getMaximumBondOrder(atom).numeric()  > (Integer) type.getProperty(CDKConstants.PI_BOND_COUNT) + 1)
    		return false;
    		
    	// confirm correct valency
    	if (type.getValency() != CDKConstants.UNSET && container.getBondOrderSum(atom) > type.getValency())
    		return false;

    	// confirm correct formal charge
        if (atom.getFormalCharge() != CDKConstants.UNSET &&
            !atom.getFormalCharge().equals(type.getFormalCharge()))
    		return false;

    	return true;
    }
    
    private boolean isHueckelNumber(int electronCount) {
        return (electronCount % 4 == 2) && (electronCount >= 2);
    }
    
}

