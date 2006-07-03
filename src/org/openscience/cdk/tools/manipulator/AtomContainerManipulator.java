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
			if(ac.getAtomAt(i).getID()!=null && ac.getAtomAt(i).getID().equals(id))
				return ac.getAtomAt(i);
		}
		throw new CDKException("no suc atom");
	}

    public static boolean replaceAtomByAtom(IAtomContainer container, IAtom atom, IAtom newAtom) {
        if (!container.contains(atom)) {
            // it should complain
	    return false;
        } else {
            container.setAtomAt(container.getAtomNumber(atom), newAtom);
            IElectronContainer[] electronContainers = container.getElectronContainers();
            for (int i=0; i<electronContainers.length; i++) {
                if (electronContainers[i] instanceof IBond) {
                    IBond bond = (IBond)electronContainers[i];
                    if (bond.contains(atom)) {
                        for (int j=0; j<bond.getAtomCount(); j++) {
                            if (atom.equals(bond.getAtomAt(j))) {
                                bond.setAtomAt(newAtom, j);
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
            charge += atomContainer.getAtomAt(i).getCharge();
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
        	double chargeI = atomContainer.getAtomAt(i).getFormalCharge();
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
        	double chargeI = atomContainer.getAtomAt(i).getFormalCharge();
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
            hCount += atomContainer.getAtomAt(i).getHydrogenCount();
        }
        return hCount;
    }

    public static Vector getAllIDs(IAtomContainer mol) {
        Vector idList = new Vector();
        if (mol != null) {
            if (mol.getID() != null) idList.addElement(mol.getID());
            IAtom[] atoms = mol.getAtoms();
            for (int i=0; i<atoms.length; i++) {
                IAtom atom = atoms[i];
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
            IAtom atom = atomContainer.getAtomAt(i);
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
            final IBond bond = atomContainer.getBondAt(i);
            IAtom[] atoms = bond.getAtoms();
            boolean removedBond = false;
            final int length = atoms.length;
            for (int k = 0;
                    k < length;
                    k++)
            {
                if (remove.contains(atoms[k]))
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
					clone = (IBond) atomContainer.getBondAt(i).clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                clone.setAtoms(new IAtom[]{(IAtom) map.get(atoms[0]), (IAtom) map.get(atoms[1])});
                mol.addBond(clone);
            }
        }

        // Recompute hydrogen counts of neighbours of removed Hydrogens.
        for (Iterator i = remove.iterator();
                i.hasNext();)
        {
            // Process neighbours.
            for (Iterator n = atomContainer.getConnectedAtomsVector((IAtom) i.next()).iterator();
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
            IAtom[] atoms = container.getAtoms();
            for (int i=0; i<atoms.length; i++) {
                IAtom atom = atoms[i];
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
			container.getElectronContainerAt(f).removeListener(container);	
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
			container.getAtomAt(f).removeListener(container);	
		}
	}

}

