/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk;

import javax.vecmath.*;
import java.util.Vector;

/** 
  * Class representing a ring structure in a molecule.
  * A ring is a linear sequence of
  * N atoms interconnected to each other by covalent bonds,
  * such that atom i (1 < i < N) is bonded to
  * atom i-1 and atom i + 1 and atom 1 is bonded to atom N and atom 2.
  *
  * @keyword    ring
  */
public class Ring extends AtomContainer {

	/**
	 * constructs an empty ring.
	 *
	 */
	public Ring() {
		super();
	}
	
	/**
	 * Constructs a ring that will have a certain number of atoms of the given elements.
	 *
	 * @param   ringSize   The number of atoms and bonds the ring will have
	 * @param   elementSymbol   The element of the atoms the ring will have
	 */
	public Ring(int ringSize, String elementSymbol) {
		this(ringSize);
		Atom[] atoms = new Atom[ringSize];
		atoms[0] = new Atom(elementSymbol);
		for (int i = 1; i < ringSize; i++) {
			atoms[i] = new Atom(elementSymbol);
			addElectronContainer(new Bond(atoms[i - 1], atoms[i], 1));
		}
		addElectronContainer(new Bond(atoms[ringSize - 1], atoms[0], 1));
		setAtoms(atoms);
	}
	
		
	/**
	 * Constructs an empty ring that will have a certain size.
	 *
	 * @param   ringSize  The size (number of atoms) the ring will have
	 */

	public Ring(int ringSize) {
		super(ringSize, ringSize);
	}
	

	/**
	 * Returns the number of atoms\edges in this ring.
	 *
	 * @return   The number of atoms\edges in this ring   
	 */

	public int getRingSize() {
		return this.atomCount;
	}
	

	/**
	 * Returns the next bond in order, relative to a given bond and atom.
	 * Example: Let the ring be composed of 0-1, 1-2, 2-3 and 3-0. A request getNextBond(1-2, 2)
	 * will return Bond 2-3.
	 *
	 * @param   bond  A bond for which an atom from a consecutive bond is sought
	 * @param   atom  A atom from the bond above to assign a search direction
	 * @return  The next bond in the order given by the above assignment   
	 */
	public Bond getNextBond(Bond bond, Atom atom)
	{
		Bond tempBond;
		for (int f = 0; f < getElectronContainerCount(); f++) {
            ElectronContainer ec = getElectronContainerAt(f);
            if (ec instanceof Bond) {
                tempBond = (Bond)ec;
                if (tempBond.contains(atom) && bond != tempBond) {
                    return tempBond;
                }
            }
		}
		return null;
	}

	/**
	 * Returns the sum of all bond orders in the ring.
	 *
	 * @return the sum of all bond orders in the ring
	 */
	public int getOrderSum()
	{
		int orderSum = 0;
		Bond tempBond;
		for (int i = 0; i < getElectronContainerCount(); i++) {
            ElectronContainer ec = getElectronContainerAt(i);
            if (ec instanceof Bond) {
                tempBond = (Bond)ec;
                orderSum += tempBond.getOrder();
            }
 		}
		return orderSum;
	}
	
	
	 /**
	  * Convenience method for giving a string representation 
	  * of this ring based on the number of the atom in a given 
	  * molecule.
      *
	  * @param molecule  A molecule to determine an atom number for each ring atom
      * @return          string representation of this ring
	  */
	public String toString(Molecule molecule)
	{
		String str = "";
		for (int f = 0; f < getAtomCount(); f++)
		{
			try
			{
				str += molecule.getAtomNumber(getAtomAt(f)) +  " - ";
			}
			catch(Exception exc)
			{
			
			}
		}
		return str;
	}
}
