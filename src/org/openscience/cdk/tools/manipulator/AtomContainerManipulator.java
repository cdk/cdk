/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMolecule;

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
 *
 * @author  Egon Willighagen
 * @cdk.created 2003-08-07
 */
public class AtomContainerManipulator {
	
	/**
	 * Returna an atom in an atomcontainer identified by id
	 * 
	 * @param ac The AtomContainer to search in
	 * @param id The id to search for
	 * @return An atom having id id
	 * @throws CDKException There is no such atom
	 */
	public static IAtom getAtomById(IAtomContainer ac, String id) throws CDKException{
		for(int i=0;i<ac.getAtomCount();i++){
			if(ac.getAtom(i).getID()!=null && ac.getAtom(i).getID().equals(id))
				return ac.getAtom(i);
		}
		throw new CDKException("no suc atom");
	}

    public static boolean replaceAtomByAtom(IAtomContainer container, IAtom atom, IAtom newAtom) {
        if (!container.contains(atom)) {
            // it should complain
	    return false;
        } else {
            container.setAtom(container.getAtomNumber(atom), newAtom);
            IElectronContainer[] electronContainers = container.getElectronContainers();
            for (int i=0; i<electronContainers.length; i++) {
                if (electronContainers[i] instanceof IBond) {
                    IBond bond = (IBond)electronContainers[i];
                    if (bond.contains(atom)) {
                        for (int j=0; j<bond.getAtomCount(); j++) {
                            if (atom.equals(bond.getAtom(j))) {
                                bond.setAtom(newAtom, j);
                            }
                        }
                    }
                } else if (electronContainers[i] instanceof ILonePair) {
                    ILonePair lonePair = (ILonePair)electronContainers[i];
                    if (atom.equals(lonePair.getAtom())) {
                        lonePair.setAtom(newAtom);
                    }
                }
            }
            return true;
        }
    }


    /**
     * @return The summed charges of all atoms in this AtomContainer.
     */
    public static double getTotalCharge(IAtomContainer atomContainer) {
        double charge = 0.0;
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            charge += atomContainer.getAtom(i).getCharge();
        }
        return charge;
    }

    /**
     * @return The summed formal charges of all atoms in this AtomContainer.
     */
    public static int getTotalFormalCharge(IAtomContainer atomContainer) {
        int chargeP = getTotalNegativeFormalCharge(atomContainer);
        int chargeN = getTotalPositiveFormalCharge(atomContainer);
        int totalCharge = chargeP + chargeN;
        
        return totalCharge;
    }
    /**
     * @return The summed negative formal charges of all atoms in this AtomContainer. 
     */
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
    public static int getTotalHydrogenCount(IAtomContainer atomContainer) {
        int hCount = 0;
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            hCount += atomContainer.getAtom(i).getHydrogenCount();
        }
        return hCount;
    }

    public static Vector getAllIDs(IAtomContainer mol) {
        Vector idList = new Vector();
        if (mol != null) {
            if (mol.getID() != null) idList.addElement(mol.getID());
            java.util.Iterator atoms = mol.atoms();
            while (atoms.hasNext()) {
                IAtom atom = (IAtom)atoms.next();
                if (atom.getID() != null) idList.addElement(atom.getID());
            }
            IBond[] bonds = mol.getBonds();
            for (int i=0; i<bonds.length; i++) {
                IBond bond = bonds[i];
                if (bond.getID() != null) idList.addElement(bond.getID());
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
    public static IAtomContainer removeHydrogens(IAtomContainer atomContainer)
    {
        Map map = new HashMap();        // maps original atoms to clones.
        List remove = new ArrayList();  // lists removed Hs.

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
                clonedAtom.setHydrogenCount(0);
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
                clone.setAtoms(new IAtom[]{(IAtom) map.get(bond.getAtom(0)), (IAtom) map.get(bond.getAtom(1))});
                mol.addBond(clone);
            }
        }

        // Recompute hydrogen counts of neighbours of removed Hydrogens.
        for (Iterator i = remove.iterator();
                i.hasNext();)
        {
            // Process neighbours.
            for (Iterator n = atomContainer.getConnectedAtomsList((IAtom) i.next()).iterator();
                    n.hasNext();)
            {
                final IAtom neighb = (IAtom) map.get(n.next());
                neighb.setHydrogenCount(neighb.getHydrogenCount() + 1);
            }
        }
        mol.setProperties(atomContainer.getProperties());
        mol.setFlags(atomContainer.getFlags());

        return (mol);
    }
    
    /**
     * Sets a property on all <code>Atom</code>s in the given container.
     */
    public static void setAtomProperties(IAtomContainer container, Object propKey, Object propVal) {
        if (container != null) {
            java.util.Iterator atoms = container.atoms();
            while (atoms.hasNext()) {
                IAtom atom = (IAtom)atoms.next();
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
		for (int f = 0; f < container.getElectronContainerCount(); f++)
		{
			container.getElectronContainer(f).removeListener(container);	
		}
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
		for (int f = 0; f < container.getAtomCount(); f++)
		{
			container.getAtom(f).removeListener(container);	
		}
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
	public static IAtom[] getAtomArray(IAtomContainer container) {
		IAtom[] ret = new IAtom[container.getAtomCount()];
		for (int i = 0; i < ret.length; ++i) ret[i] = container.getAtom(i);
		return ret;
	}
	
	/**
	 * Constructs an array of Atom objects from a List of Atom objects.
	 * @param  container The original List.
	 * @return The array of Atom objects.
	 */
	public static IAtom[] getAtomArray(java.util.List list) {
		IAtom[] ret = new IAtom[list.size()];
		for (int i = 0; i < ret.length; ++i) ret[i] = (IAtom)list.get(i);
		return ret;
	}
	
	/**
	 * Constructs an array of Bond objects from an AtomContainer.
	 * @param  container The original AtomContainer.
	 * @return The array of Bond objects.
	 */
	public static IBond[] getBondArray(IAtomContainer container) {
		IBond[] ret = new IBond[container.getBondCount()];
		for (int i = 0; i < ret.length; ++i) ret[i] = container.getBond(i);
		return ret;
	}
	
	/**
	 * Constructs an array of Atom objects from a List of Atom objects.
	 * @param  container The original List.
	 * @return The array of Atom objects.
	 */
	public static IBond[] getBondArray(java.util.List list) {
		IBond[] ret = new IBond[list.size()];
		for (int i = 0; i < ret.length; ++i) ret[i] = (IBond)list.get(i);
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
	 * @param  container The original List.
	 * @return The array of Atom objects.
	 */
	public static IElectronContainer[] getElectronContainerArray(java.util.List list) {
		IElectronContainer[] ret = new IElectronContainer[list.size()];
		for (int i = 0; i < ret.length; ++i) ret[i] = (IElectronContainer)list.get(i);
		return ret;
	}
	
}

