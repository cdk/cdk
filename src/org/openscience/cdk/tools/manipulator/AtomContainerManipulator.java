/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.tools.manipulator;

import java.util.Vector;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.LonePair;

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

    public static void replaceAtomByAtom(AtomContainer container, Atom atom, Atom newAtom) {
        if (!container.contains(atom)) {
            // it should complain
	    return;
        } else {
            container.setAtomAt(container.getAtomNumber(atom), newAtom);
            ElectronContainer[] electronContainers = container.getElectronContainers();
            for (int i=0; i<electronContainers.length; i++) {
                if (electronContainers[i] instanceof Bond) {
                    Bond bond = (Bond)electronContainers[i];
                    if (bond.contains(atom)) {
                        for (int j=0; j<bond.getAtomCount(); j++) {
                            if (atom.equals(bond.getAtomAt(j))) {
                                bond.setAtomAt(newAtom, j);
                            }
                        }
                    }
                } else if (electronContainers[i] instanceof LonePair) {
                    LonePair lonePair = (LonePair)electronContainers[i];
                    if (atom.equals(lonePair.getAtom())) {
                        lonePair.setAtom(newAtom);
                    }
                }
            }
        }
    }


    /**
     * @return The summed charges of all atoms in this AtomContainer.
     */
    public static double getTotalCharge(AtomContainer atomContainer) {
        double charge = 0.0;
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            charge += atomContainer.getAtomAt(i).getCharge();
        }
        return charge;
    }

    /**
     * @return The summed formal charges of all atoms in this AtomContainer.
     */
    public static int getTotalFormalCharge(AtomContainer atomContainer) {
        int charge = 0;
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            charge += atomContainer.getAtomAt(i).getFormalCharge();
        }
        return charge;
    }

    /**
     * @return The summed implicit hydrogens of all atoms in this AtomContainer.
     */
    public static int getTotalHydrogenCount(AtomContainer atomContainer) {
        int hCount = 0;
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            hCount += atomContainer.getAtomAt(i).getHydrogenCount();
        }
        return hCount;
    }

    public static Vector getAllIDs(AtomContainer mol) {
        Vector idList = new Vector();
        if (mol != null) {
            if (mol.getID() != null) idList.addElement(mol.getID());
            Atom[] atoms = mol.getAtoms();
            for (int i=0; i<atoms.length; i++) {
                Atom atom = atoms[i];
                if (atom.getID() != null) idList.addElement(atom.getID());
            }
            Bond[] bonds = mol.getBonds();
            for (int i=0; i<bonds.length; i++) {
                Bond bond = bonds[i];
                if (bond.getID() != null) idList.addElement(bond.getID());
            }
        }
        return idList;
    }


    /**
     * Produces an AtomContainer without explicit Hs but with H count from one with Hs.
     * The new molecule is a deep copy.
     *
     * @param ac The AtomContainer from which to remove the hydrogens
     * @return The mol without Hs.
     * @cdk.keyword hydrogen, removal
     */
    public static AtomContainer removeHydrogens(AtomContainer atomContainer)
    {
        Map map = new HashMap();        // maps original atoms to clones.
        List remove = new ArrayList();  // lists removed Hs.

        // Clone atoms except those to be removed.
        Molecule mol = new Molecule();
        int count = atomContainer.getAtomCount();
        for (int i = 0;
                i < count;
                i++)
        {
            // Clone/remove this atom?
            Atom atom = atomContainer.getAtomAt(i);
            if (!atom.getSymbol().equals("H"))
            {
                Atom clonedAtom = (Atom) atom.clone();
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
            final Bond bond = atomContainer.getBondAt(i);
            Atom[] atoms = bond.getAtoms();
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
                Bond clone = (Bond) atomContainer.getBondAt(i).clone();
                clone.setAtoms(new Atom[]{(Atom) map.get(atoms[0]), (Atom) map.get(atoms[1])});
                mol.addBond(clone);
            }
        }

        // Recompute hydrogen counts of neighbours of removed Hydrogens.
        for (Iterator i = remove.iterator();
                i.hasNext();)
        {
            // Process neighbours.
            for (Iterator n = atomContainer.getConnectedAtomsVector((Atom) i.next()).iterator();
                    n.hasNext();)
            {
                final Atom neighb = (Atom) map.get(n.next());
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
    public static void setAtomProperties(AtomContainer container, Object propKey, Object propVal) {
        if (container != null) {
            Atom[] atoms = container.getAtoms();
            for (int i=0; i<atoms.length; i++) {
                Atom atom = atoms[i];
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
	public static void unregisterElectronContainerListeners(AtomContainer container)
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
	public static void unregisterAtomListeners(AtomContainer container)
	{
		for (int f = 0; f < container.getAtomCount(); f++)
		{
			container.getAtomAt(f).removeListener(container);	
		}
	}

}

