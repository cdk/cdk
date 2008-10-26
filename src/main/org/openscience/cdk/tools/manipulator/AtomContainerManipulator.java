/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Symbols;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class with convenience methods that provide methods to manipulate
 * AtomContainer's. For example:
 * <pre>
 * AtomContainerManipulator.replaceAtomByAtom(container, atom1, atom2);
 * </pre>
 * will replace the Atom in the AtomContainer, but in all the ElectronContainer's
 * it participates too.
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @author  Egon Willighagen
 * @cdk.created 2003-08-07
 */
@TestClass("org.openscience.cdk.tools.manipulator.AtomContainerManipulatorTest")
public class AtomContainerManipulator {
	
	/**
	 * Returna an atom in an atomcontainer identified by id
	 * 
	 * @param ac The AtomContainer to search in
	 * @param id The id to search for
	 * @return An atom having id id
	 * @throws CDKException There is no such atom
	 */
    @TestMethod("testGetAtomById_IAtomContainer_String")
    public static IAtom getAtomById(IAtomContainer ac, String id) throws CDKException{
		for(int i=0;i<ac.getAtomCount();i++){
			if(ac.getAtom(i).getID()!=null && ac.getAtom(i).getID().equals(id))
				return ac.getAtom(i);
		}
		throw new CDKException("no suc atom");
	}

    @TestMethod("testReplaceAtom")
    public static boolean replaceAtomByAtom(IAtomContainer container, IAtom atom, IAtom newAtom) {
        if (!container.contains(atom)) {
            // it should complain
	    return false;
        } else {
            container.setAtom(container.getAtomNumber(atom), newAtom);
            for (IElectronContainer eContainer : container.electronContainers()) {
                if (eContainer instanceof IBond) {
                    IBond bond = (IBond) eContainer;
                    if (bond.contains(atom)) {
                        for (int j = 0; j < bond.getAtomCount(); j++) {
                            if (atom.equals(bond.getAtom(j))) {
                                bond.setAtom(newAtom, j);
                            }
                        }
                    }
                } else if (eContainer instanceof ILonePair) {
                    ILonePair lonePair = (ILonePair) eContainer;
                    if (atom.equals(lonePair.getAtom())) {
                        lonePair.setAtom(newAtom);
                    }
                }
            }
            return true;
        }
    }


    /**
     * Get the summed charge of all atoms in an AtomContainer
     * 
     * @param  atomContainer The IAtomContainer to manipulate
     * @return The summed charges of all atoms in this AtomContainer.
     */
    @TestMethod("testGetTotalCharge")
    public static double getTotalCharge(IAtomContainer atomContainer) {
        double charge = 0.0;
        for (IAtom atom : atomContainer.atoms()) {
            // we assume CDKConstant.UNSET is equal to 0
            Double thisCharge = atom.getCharge();
            if (thisCharge != CDKConstants.UNSET)
                charge += thisCharge;
        }
        return charge;
    }

    /**
     * Get the summed exact mass of all atoms in an AtomContainer
     * 
     * @param  atomContainer The IAtomContainer to manipulate
     * @return The summed exact mass of all atoms in this AtomContainer.
     */
    @TestMethod("testGetTotalExactMass_IAtomContainer")
    public static double getTotalExactMass(IAtomContainer atomContainer) {
        double mass = 0.0;
        for (IAtom atom : atomContainer.atoms()) {
            mass += atom.getExactMass();
        }
        return mass;
    }
    
    /**
     * Returns the molecular mass of the IAtomContainer. For the calculation it uses the
     * masses of the isotope mixture using natural abundances.
     * 
     * @param       atomContainer
     * @cdk.keyword mass, molecular
     */
    @TestMethod("testGetNaturalExactMass_IAtomContainer")
    public static double getNaturalExactMass(IAtomContainer atomContainer) {
		 double mass = 0.0;
		 IsotopeFactory factory;
		 try {
			 factory = IsotopeFactory.getInstance(atomContainer.getBuilder());
		 } catch (IOException e) {
			 throw new RuntimeException("Could not instantiate the IsotopeFactory.");
		 }
        for (IAtom atom : atomContainer.atoms()) {
            IElement isotopesElement = atom.getBuilder().newElement(atom.getSymbol());
            mass += factory.getNaturalMass(isotopesElement);
        }
		 return mass;
    }
    /** 
     * Get the summed natural abundance of all atoms in an AtomContainer
     * 
     * @param  atomContainer The IAtomContainer to manipulate
     * @return The summed natural abundance of all atoms in this AtomContainer.
     */
    @TestMethod("testGetTotalNaturalAbundance_IAtomContainer")
    public static double getTotalNaturalAbundance(IAtomContainer atomContainer) {
        double abundance =  1.0;
        for (IAtom iAtom : atomContainer.atoms()) abundance = abundance * iAtom.getNaturalAbundance();
        
    	
        return abundance/Math.pow(100,atomContainer.getAtomCount());
    }
    
    /**
     * @return The summed formal charges of all atoms in this AtomContainer.
     */
    @TestMethod("testGetTotalFormalCharge_IAtomContainer")
    public static int getTotalFormalCharge(IAtomContainer atomContainer) {
        int chargeP = getTotalNegativeFormalCharge(atomContainer);
        int chargeN = getTotalPositiveFormalCharge(atomContainer);

        return chargeP + chargeN;
    }
    /**
     * @return The summed negative formal charges of all atoms in this AtomContainer. 
     */
    @TestMethod("testGetTotalNegativeFormalCharge_IAtomContainer")
    public static int getTotalNegativeFormalCharge(IAtomContainer atomContainer) {
        int charge = 0;
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
        	double chargeI = atomContainer.getAtom(i).getFormalCharge();
        	if(chargeI < 0)
        		charge += chargeI;
        }
        return charge;
    }
    /**
     * @return The summed positive formal charges of all atoms in this AtomContainer. 
     */
    @TestMethod("testGetTotalPositiveFormalCharge_IAtomContainer")
    public static int getTotalPositiveFormalCharge(IAtomContainer atomContainer) {
        int charge = 0;
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
        	double chargeI = atomContainer.getAtom(i).getFormalCharge();
        	if(chargeI > 0)
        		charge += chargeI;
        }
        return charge;
    }

    /**
     * @return The summed implicit hydrogens of all atoms in this AtomContainer.
     */
    @TestMethod("testGetTotalHydrogenCount_IAtomContainer,testGetTotalHydrogenCount_IAtomContainer_zeroImplicit,testGetTotalHydrogenCount_IAtomContainer_nullImplicit,testGetTotalHydrogenCount_ImplicitHydrogens")
    public static int getTotalHydrogenCount(IAtomContainer atomContainer) {
        int hCount = 0;
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            Integer ihcount = atomContainer.getAtom(i).getHydrogenCount();
            if (ihcount != CDKConstants.UNSET)
                hCount += ihcount;
        }
        return hCount;
    }

    /**
     * @return The number of explicit hydrogens on the given IAtom.
     */
    @TestMethod("testCountExplicitH")
    public static int countExplicitHydrogens(IAtomContainer atomContainer, IAtom atom) {
    	int hCount = 0;
        for (IAtom iAtom : atomContainer.getConnectedAtomsList(atom)) {
            IAtom connectedAtom = iAtom;
            if (connectedAtom.getSymbol().equals("H"))
                hCount++;
        }
        return hCount;
    }

    /**
     * Adds explicit hydrogens (without coordinates) to the IAtomContainer,
     * equaling the number of set implicit hydrogens.
     */
    @TestMethod("testConvertImplicitToExplicitHydrogens_IAtomContainer")
    public static void convertImplicitToExplicitHydrogens(IAtomContainer atomContainer) {
        for (IAtom atom : atomContainer.atoms()) {
            if (!atom.getSymbol().equals("H")) {
                Integer hCount = atom.getHydrogenCount();
                if (hCount != null) {
                    for (int i = 0; i < hCount; i++) {
                        IAtom hydrogen = atom.getBuilder().newAtom("H");
                        hydrogen.setAtomTypeName("H");
                        atomContainer.addAtom(hydrogen);
                        atomContainer.addBond(
                                atom.getBuilder().newBond(
                                        atom, hydrogen,
                                        CDKConstants.BONDORDER_SINGLE
                                )
                        );
                    }
                    atom.setHydrogenCount(0);
                }
            }
        }
    }

    /**
     * @return The summed implicit + explicit hydrogens of the given IAtom.
     */
    @TestMethod("testCountH")
    public static int countHydrogens(IAtomContainer atomContainer, IAtom atom) {
        int hCount = atom.getHydrogenCount() == CDKConstants.UNSET ? 0 : atom.getHydrogenCount();
        hCount += countExplicitHydrogens(atomContainer, atom);
        return hCount;
	}

    @TestMethod("testGetAllIDs_IAtomContainer")
    public static List<String> getAllIDs(IAtomContainer mol) {
    	List<String> idList = new ArrayList<String>();
        if (mol != null) {
            if (mol.getID() != null) idList.add(mol.getID());
            for (IAtom atom : mol.atoms()) {
                if (atom.getID() != null) idList.add(atom.getID());
            }

            for (IBond bond : mol.bonds()) {
                if (bond.getID() != null) idList.add(bond.getID());
            }
        }
        return idList;
    }


    /**
     * Produces an AtomContainer without explicit Hs but with H count from one with Hs.
     * The new molecule is a deep copy.
     *
     * @param atomContainer The AtomContainer from which to remove the hydrogens
     * @return              The molecule without Hs.
     * @cdk.keyword         hydrogen, removal
     */
    @TestMethod("testRemoveHydrogens_IAtomContainer")
    public static IAtomContainer removeHydrogens(IAtomContainer atomContainer)
    {
        Map<IAtom, IAtom> map = new HashMap<IAtom,IAtom>();        // maps original atoms to clones.
        List<IAtom> remove = new ArrayList<IAtom>();  // lists removed Hs.

        // Clone atoms except those to be removed.
        IMolecule mol = atomContainer.getBuilder().newMolecule();
        int count = atomContainer.getAtomCount();
        for (int i = 0;
                i < count;
                i++)
        {
            // Clone/remove this atom?
            IAtom atom = atomContainer.getAtom(i);
            if (!atom.getSymbol().equals("H"))
            {
                IAtom clonedAtom = null;
				try {
					clonedAtom = (IAtom) atom.clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                mol.addAtom(clonedAtom);
                map.put(atom, clonedAtom);
            }
            else
            {
                remove.add(atom);   // maintain list of removed H.
            }
        }

        // Clone bonds except those involving removed atoms.
        count = atomContainer.getBondCount();
        for (int i = 0;
                i < count;
                i++)
        {
            // Check bond.
            final IBond bond = atomContainer.getBond(i);
            boolean removedBond = false;
            final int length = bond.getAtomCount();
            for (int k = 0;
                    k < length;
                    k++)
            {
                if (remove.contains(bond.getAtom(k)))
                {
                    removedBond = true;
                    break;
                }
            }

            // Clone/remove this bond?
            if (!removedBond)
                // if (!remove.contains(atoms[0]) && !remove.contains(atoms[1]))
            {
                IBond clone = null;
				try {
					clone = (IBond) atomContainer.getBond(i).clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                assert clone != null;
                clone.setAtoms(new IAtom[]{(IAtom) map.get(bond.getAtom(0)), (IAtom) map.get(bond.getAtom(1))});
                mol.addBond(clone);
            }
        }

        // Recompute hydrogen counts of neighbours of removed Hydrogens.
        for (IAtom aRemove : remove) {
            // Process neighbours.
            for (IAtom iAtom : atomContainer.getConnectedAtomsList(aRemove)) {
                final IAtom neighb = map.get(iAtom);
                  neighb.setHydrogenCount(
                      (neighb.getHydrogenCount() == null ? 0 : neighb.getHydrogenCount())
                      + 1
                  );
            }
        }
        mol.setProperties(atomContainer.getProperties());
        mol.setFlags(atomContainer.getFlags());

        return (mol);
    }
    
	/**
	 * Produces an AtomContainer without explicit Hs but with H count from one with Hs.
	 * Hs bonded to more than one heavy atom are preserved.  The new molecule is a deep copy.
	 *
	 * @return         The mol without Hs.
	 * @cdk.keyword    hydrogen, removal
	 */
    @TestMethod("testRemoveHydrogensPreserveMultiplyBonded")
	public static IAtomContainer removeHydrogensPreserveMultiplyBonded(IAtomContainer ac) {
		List<IAtom> h = new ArrayList<IAtom>();
		// H list.
		List<IAtom> multi_h = new ArrayList<IAtom>();
		// multiply bonded H

		// Find multiply bonded H.
		int count = ac.getBondCount();
		for (int i = 0; i < count; i++) {
            for (IAtom atom : ac.getBond(i).atoms()) {
                if (atom.getSymbol().equals(Symbols.byAtomicNumber[1])) {
                    (h.contains(atom) ? multi_h : h).add(atom);
                }
            }
		}

		return removeHydrogens(ac, multi_h);
	}

	/**
	 * Produces an AtomContainer without explicit Hs (except those listed) but with H count from one with Hs.
	 * The new molecule is a deep copy.
	 *
	 * @param  preserve  a list of H atoms to preserve.
	 * @return           The mol without Hs.
	 * @cdk.keyword      hydrogen, removal
	 */
	private static IAtomContainer removeHydrogens(IAtomContainer ac, List<IAtom> preserve) {
		Map<IAtom,IAtom> map = new HashMap<IAtom,IAtom>();
		// maps original atoms to clones.
		List<IAtom> remove = new ArrayList<IAtom>();
		// lists removed Hs.

		// Clone atoms except those to be removed.
		IMolecule mol = ac.getBuilder().newMolecule();
		int count = ac.getAtomCount();
		for (int i = 0;
				i < count;
				i++) {
			// Clone/remove this atom?
			IAtom atom = ac.getAtom(i);
			if (!atom.getSymbol().equals(Symbols.byAtomicNumber[1]) || preserve.contains(atom)) {
				IAtom a = null;
				try {
					a = (IAtom) atom.clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				a.setHydrogenCount(0);
				mol.addAtom(a);
				map.put(atom, a);
			} else {
				remove.add(atom);
				// maintain list of removed H.
			}
		}

		// Clone bonds except those involving removed atoms.
		count = ac.getBondCount();
		for (int i = 0;
				i < count;
				i++) {
			// Check bond.
			final IBond bond = ac.getBond(i);
			IAtom atom0 = bond.getAtom(0);
			IAtom atom1 = bond.getAtom(1);
			boolean remove_bond = false;
			for (IAtom atom : bond.atoms()){
				if (remove.contains(atom)) {
					remove_bond = true;
					break;
				}
			}

			// Clone/remove this bond?
			if (!remove_bond) {
				// if (!remove.contains(atoms[0]) && !remove.contains(atoms[1]))

				IBond clone = null;
				try {
					clone = (IBond) ac.getBond(i).clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				clone.setAtoms(new IAtom[]{map.get(atom0), map.get(atom1)});
				mol.addBond(clone);
			}
		}

		// Recompute hydrogen counts of neighbours of removed Hydrogens.
		for (IAtom removeAtom : remove) {
			// Process neighbours.
            for (IAtom  neighbor : ac.getConnectedAtomsList(removeAtom)) {
                final IAtom neighb = map.get(neighbor);
				neighb.setHydrogenCount(neighb.getHydrogenCount() + 1);
            }
		}

		return (mol);
	}

	/**
     * Sets a property on all <code>Atom</code>s in the given container.
     */
    public static void setAtomProperties(IAtomContainer container, Object propKey, Object propVal) {
        if (container != null) {
            for (IAtom atom : container.atoms()) {
                atom.setProperty(propKey, propVal);
            }
        }
    }

	/**
	 *  A method to remove ElectronContainerListeners. 
	 *  ElectronContainerListeners are used to detect changes 
	 *  in ElectronContainers (like bonds) and to notifiy
	 *  registered Listeners in the event of a change.
	 *  If an object looses interest in such changes, it should 
	 *  unregister with this AtomContainer in order to improve 
	 *  performance of this class.
	 */
	public static void unregisterElectronContainerListeners(IAtomContainer container)
	{
        for (IElectronContainer electronContainer : container.electronContainers())
            electronContainer.removeListener(container);
	}

	/**
	 *  A method to remove AtomListeners. 
	 *  AtomListeners are used to detect changes 
	 *  in Atom objects within this AtomContainer and to notifiy
	 *  registered Listeners in the event of a change.
	 *  If an object looses interest in such changes, it should 
	 *  unregister with this AtomContainer in order to improve 
	 *  performance of this class.
	 */
	public static void unregisterAtomListeners(IAtomContainer container)
	{
        for (IAtom atom : container.atoms()) atom.removeListener(container);
	}

	/**
	 * Compares this AtomContainer with another given AtomContainer and returns
	 * the Intersection between them. <p>
	 * 
	 * <b>Important Note</b> : This is not the maximum common substructure.
	 *
	 * @param  container1 an AtomContainer object
	 * @param  container2 an AtomContainer object
	 * @return            An AtomContainer containing the intersection between
	 *                    container1 and container2
	 */
    @TestMethod("testGetIntersection_IAtomContainer_IAtomContainer")
    public static IAtomContainer getIntersection(
		IAtomContainer container1, IAtomContainer container2)
	{
		IAtomContainer intersection = container1.getBuilder().newAtomContainer();

		for (int i = 0; i < container1.getAtomCount(); i++)
		{
			if (container2.contains(container1.getAtom(i)))
			{
				intersection.addAtom(container1.getAtom(i));
			}
		}
		for (int i = 0; i < container1.getElectronContainerCount(); i++)
		{
			if (container2.contains(container1.getElectronContainer(i)))
			{
				intersection.addElectronContainer(container1.getElectronContainer(i));
			}
		}
		return intersection;
	}
	
	/**
	 * Constructs an array of Atom objects from an AtomContainer.
	 * @param  container The original AtomContainer.
	 * @return The array of Atom objects.
	 */
    @TestMethod("testGetAtomArray_IAtomContainer")
    public static IAtom[] getAtomArray(IAtomContainer container) {
		IAtom[] ret = new IAtom[container.getAtomCount()];
		for (int i = 0; i < ret.length; ++i) ret[i] = container.getAtom(i);
		return ret;
	}
	
	/**
	 * Constructs an array of Atom objects from a List of Atom objects.
	 * @param  list The original List.
	 * @return The array of Atom objects.
	 */
    @TestMethod("testGetAtomArray_List")
    public static IAtom[] getAtomArray(java.util.List<IAtom> list) {
		IAtom[] ret = new IAtom[list.size()];
		for (int i = 0; i < ret.length; ++i) ret[i] = list.get(i);
		return ret;
	}
	
	/**
	 * Constructs an array of Bond objects from an AtomContainer.
	 * @param  container The original AtomContainer.
	 * @return The array of Bond objects.
	 */
    @TestMethod("testGetBondArray_IAtomContainer")
    public static IBond[] getBondArray(IAtomContainer container) {
		IBond[] ret = new IBond[container.getBondCount()];
		for (int i = 0; i < ret.length; ++i) ret[i] = container.getBond(i);
		return ret;
	}
	
	/**
	 * Constructs an array of Atom objects from a List of Atom objects.
	 * @param  list The original List.
	 * @return The array of Atom objects.
	 */
    @TestMethod("testGetBondArray_List")
    public static IBond[] getBondArray(java.util.List<IBond> list) {
		IBond[] ret = new IBond[list.size()];
		for (int i = 0; i < ret.length; ++i) ret[i] = list.get(i);
		return ret;
	}
	
	/**
	 * Constructs an array of Bond objects from an AtomContainer.
	 * @param  container The original AtomContainer.
	 * @return The array of Bond objects.
	 */
	public static IElectronContainer[] getElectronContainerArray(IAtomContainer container) {
		IElectronContainer[] ret = new IElectronContainer[container.getElectronContainerCount()];
		for (int i = 0; i < ret.length; ++i) ret[i] = container.getElectronContainer(i);
		return ret;
	}
	
	/**
	 * Constructs an array of Atom objects from a List of Atom objects.
	 * @param  list The original List.
	 * @return The array of Atom objects.
	 */
	public static IElectronContainer[] getElectronContainerArray(java.util.List<IElectronContainer> list) {
		IElectronContainer[] ret = new IElectronContainer[list.size()];
		for (int i = 0; i < ret.length; ++i) ret[i] = list.get(i);
		return ret;
	}

	/**
	 * Convenience method to perceive atom types for all <code>IAtom</code>s in the
	 * <code>IAtomContainer</code>, using the <code>CDKAtomTypeMatcher</code>. If the
	 * matcher finds a matching atom type, the <code>IAtom</code> will be configured
	 * to have the same properties as the <code>IAtomType</code>. If no matching atom
	 * type is found, no configuration is performed.
	 * <b>This method overwrites existing values.</b>
	 * 
	 * @param container
	 * @throws CDKException
	 */
    @TestMethod("testPerceiveAtomTypesAndConfigureAtoms")
    public static void percieveAtomTypesAndConfigureAtoms(IAtomContainer container) throws CDKException {
		CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
        for (IAtom atom : container.atoms()) {
            IAtomType matched = matcher.findMatchingAtomType(container, atom);
            if (matched != null) AtomTypeManipulator.configure(atom, matched);
        }
	}

    /**
     * Convenience method to perceive atom types for all <code>IAtom</code>s in the
     * <code>IAtomContainer</code>, using the <code>CDKAtomTypeMatcher</code>. If the
     * matcher finds a matching atom type, the <code>IAtom</code> will be configured
     * to have the same properties as the <code>IAtomType</code>. If no matching atom
     * type is found, no configuration is performed.
     * <b>This method overwrites existing values.</b>
     * 
     * @param container
     * @throws CDKException
     */
      @TestMethod("testPerceiveAtomTypesAndConfigureAtoms")
      public static void percieveAtomTypesAndConfigureUnsetProperties(IAtomContainer container) throws CDKException {
      CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
          for (IAtom atom : container.atoms()) {
              IAtomType matched = matcher.findMatchingAtomType(container, atom);
              if (matched != null) AtomTypeManipulator.configureUnsetProperties(atom, matched);
          }
    }


    /**
     * This method will reset all atom configuration to UNSET.
     *
     * This method is the reverse of {@link #percieveAtomTypesAndConfigureAtoms(org.openscience.cdk.interfaces.IAtomContainer)}
     * and after a call to this method all atoms will be "unconfigured".
     *
     * Note that it is not a complete reversal of {@link #percieveAtomTypesAndConfigureAtoms(org.openscience.cdk.interfaces.IAtomContainer)}
     * since the atomic symbol of the atoms remains unchanged. Also, all the flags that were set
     * by the configuration method (such as IS_HYDROGENBOND_ACCEPTOR or ISAROMATIC) will be set to False.
     *
     * @param container The molecule, whose atoms are to be unconfigured
     * @see #percieveAtomTypesAndConfigureAtoms(org.openscience.cdk.interfaces.IAtomContainer)
     */
    @TestMethod("testClearConfig")
    public static void clearAtomConfigurations(IAtomContainer container) {
        for (IAtom atom : container.atoms()) {
            atom.setAtomTypeName((String) CDKConstants.UNSET);
            atom.setMaxBondOrder((IBond.Order) CDKConstants.UNSET);
            atom.setBondOrderSum((Double) CDKConstants.UNSET);
            atom.setCovalentRadius((Double) CDKConstants.UNSET);
            atom.setValency((Integer) CDKConstants.UNSET);
            atom.setFormalCharge((Integer) CDKConstants.UNSET);
            atom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);
            atom.setFormalNeighbourCount((Integer) CDKConstants.UNSET);
            atom.setFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR, false);
            atom.setFlag(CDKConstants.IS_HYDROGENBOND_DONOR, false);
            atom.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, CDKConstants.UNSET);
            atom.setFlag(CDKConstants.ISAROMATIC, false);
            atom.setProperty("org.openscience.cdk.renderer.color", CDKConstants.UNSET);
            atom.setAtomicNumber((Integer) CDKConstants.UNSET);
            atom.setExactMass((Double) CDKConstants.UNSET);
        }
    }


    /**
	 * Returns the sum of bond orders, where a single bond counts as one
	 * <i>single bond equivalent</i>, a double as two, etc.
	 */
    @TestMethod("testGetSBE")
    public static int getSingleBondEquivalentSum(IAtomContainer container) {
		int sum = 0;
        for (IBond bond : container.bonds()) {
            if (bond.getOrder() == CDKConstants.BONDORDER_SINGLE) {
                sum += 1;
            } else if (bond.getOrder() == CDKConstants.BONDORDER_DOUBLE) {
                sum += 2;
            } else if (bond.getOrder() == CDKConstants.BONDORDER_TRIPLE) {
                sum += 3;
            } else if (bond.getOrder() == CDKConstants.BONDORDER_QUADRUPLE) {
                sum += 4;
            }
        }
		return sum;
	}

    @TestMethod("testGetMaxBondOrder_IAtomContainer")
    public static IBond.Order getMaximumBondOrder(IAtomContainer container) {
		return BondManipulator.getMaximumBondOrder(container.bonds().iterator());
	}
    /**
	 * Returns a set of nodes excluding all the hydrogens.
	 *
	 * @return         The heavyAtoms value
	 * @cdk.keyword    hydrogen, removal
	 */
    @TestMethod("testGetHeavyAtoms_IAtomContainer")
	public static  List<IAtom> getHeavyAtoms(IAtomContainer container) {
		List<IAtom> newAc = new ArrayList<IAtom>();
		for (int f = 0; f < container.getAtomCount(); f++) {
			if (!container.getAtom(f).getSymbol().equals("H")) {
				newAc.add(container.getAtom(f));
			}
		}
		return newAc;
	}
    
    
	/**
	 * Generates a cloned atomcontainer with all atoms being carbon, all bonds being single non-aromatic
	 * @param atomContainer The input atomcontainer
	 * @return The new atomcontainer
	 * @throws CloneNotSupportedException The atomcontainer cannot be cloned
	 */
	public static IAtomContainer createAnyAtomAnyBondAtomContainer(
			IAtomContainer atomContainer) throws CloneNotSupportedException{
			IAtomContainer query = (IAtomContainer) atomContainer.clone();
			for (int i = 0; i < query.getBondCount(); i++) {
				query.getBond(i).setOrder(IBond.Order.SINGLE);
				query.getBond(i).setFlag(CDKConstants.ISAROMATIC, false);
				query.getBond(i).getAtom(0).setSymbol("C");
				query.getBond(i).getAtom(0).setHybridization(null);
				query.getBond(i).getAtom(1).setSymbol("C");
				query.getBond(i).getAtom(1).setHybridization(null);
				query.getBond(i).getAtom(0).setFlag(CDKConstants.ISAROMATIC, false);
				query.getBond(i).getAtom(1).setFlag(CDKConstants.ISAROMATIC, false);
			}
			return query;
	}	
}

