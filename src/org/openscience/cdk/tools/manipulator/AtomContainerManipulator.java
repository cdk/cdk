/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
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
        Vector IDlist = new Vector();
        if (mol != null) {
            if (mol.getID() != null) IDlist.addElement(mol.getID());
            Atom[] atoms = mol.getAtoms();
            for (int i=0; i<atoms.length; i++) {
                Atom atom = atoms[i];
                if (atom.getID() != null) IDlist.addElement(atom.getID());
            }
            Bond[] bonds = mol.getBonds();
            for (int i=0; i<bonds.length; i++) {
                Bond bond = bonds[i];
                if (bond.getID() != null) IDlist.addElement(bond.getID());
            }
        }
        return IDlist;
    }
}

