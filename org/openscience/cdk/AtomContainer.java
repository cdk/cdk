/* AtomContainer.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
 * 
 * Contact: steinbeck@ice.mpg.de, geelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

/**
 *  
 * Base class for all chemical objects that maintain a list of Atoms and
 * Bonds
 *
 * @author     steinbeck 
 * @created    October 2, 2000 
 */
public class AtomContainer extends ChemObject {

	/**
	 *  Number of atoms contained by this object 
	 */
	protected int atomCount;

	/**
	 *  Number of bonds contained by this object 
	 */
	protected int bondCount;

	/**
	 *  
	 * Amount by which the bond and arom arrays grow when elements are added
	 * and the arrays are not large enough for that.
	 */
	protected int growArraySize = 10;

	/**
	 *  Internal array of atoms 
	 */
	protected Atom[] atoms;

	/**
	 *  Internal array of bond 
	 */
	protected Bond[] bonds;

	/**
	 *  Constructs an empty AtomContainer 
	 */
	public AtomContainer()
	{
		atomCount = 0;
		bondCount = 0;
		atoms = new Atom[growArraySize];
		bonds = new Bond[growArraySize];
	}

	/**
	 *  Sets the array of atoms of this AtomContainer 
	 *
	 * @param  atoms  The array of atoms to be assigned to this AtomContainer 
	 */
	public void setAtoms(Atom[] atoms)
	{
		this.atoms = atoms;
	}

	/**
	 *  Sets the array of bonds of this AtomContainer 
	 *
	 * @param  bonds  The array of Bonds to be assigned to this AtomContainer 
	 */
	public void setBonds(Bond[] bonds)
	{
		this.bonds = bonds;
	}

	/**
	 *  Set the atom at position <code>number</code> . 
	 *
	 * @param  number  The position of the atom to be set. 
	 * @param  atom    The atom to be stored at position <code>number</code> 
	 */
	public void setAtom(int number, Atom atom)
	{
		atoms[number] = atom;
	}
	/**
	 *  Set the bond at position <code>number</code> . 
	 *
	 * @param  number  The position of the bond to be set. 
	 * @param  bond    The bond to be stored at position <code>number</code> 
	 */
	public void setBond(int number, Bond bond)
	{
		bonds[number] = bond;
	}
	/**
	 *  Returns the array of atoms of this AtomContainer 
	 *
	 * @return    The array of atoms of this AtomContainer 
	 */
	public Atom[] getAtoms()
	{
		return this.atoms;
	}
	/**
	 *  Returns the array of bonds of this AtomContainer 
	 *
	 * @return    The array of bonds of this AtomContainer 
	 */
	public Bond[] getBonds()
	{
		return this.bonds;
	}
	/**
	 *  
	 * Returns the atom at position <code>number</code> in the
	 * container
	 *
	 * @param  number  The position of the atom to be returned. 
	 * @return         The atom at position <code>number</code> . 
	 */
	public Atom getAtom(int number)
	{
		return atoms[number];
	}
	/**
	 *  
	 * Returns the bond at position <code>number</code> in the
	 * container
	 *
	 * @param  number  The position of the bond to be returned. 
	 * @return         The bond at position <code>number</code> . 
	 */
	public Bond getBond(int number)
	{
		return bonds[number];
	}
	/**
	 *  Gets the ConnectionTable attribute of the AtomContainer object 
	 *
	 * @return    The ConnectionTable value 
	 */
	public int[][] getConnectionTable()
	{
		Atom[] bondatoms;
		int[][] ct = new int[atoms.length][atoms.length];
		for (int f = 0; f < bonds.length; f++)
		{
			bondatoms = bonds[f].getAtoms();
			wireAtoms(ct, atoms, 0, 1);
		}
		return ct;
	}
	/**
	 *  Adds an atom to this container 
	 *
	 * @param  atom  The atom to be added to this container 
	 */
	public void addAtom(Atom atom)
	{
		atomCount++;
		if (atomCount >= atoms.length)
		{
			growAtomArray();
		}
		atoms[atomCount] = atom;
	}
	/**
	 *  Adds a bond to this container 
	 *
	 * @param  bond  The bond to added to this container 
	 */
	public void addBond(Bond bond)
	{
		bondCount++;
		if (bondCount >= bonds.length)
		{
			growBondArray();
		}
		// are we supposed to check if the atoms forming this bond are
		// already in here and add them if neccessary?
		bonds[bondCount] = bond;
	}
	/**
	 *  Grows the bond array by a given size 
	 *
	 * @see    org.openscience.cdk.AtomContainer#growArraySize growArraySize 
	 */
	protected void growBondArray()
	{
		Bond[] newbonds = new Bond[bonds.length + growArraySize];
		System.arraycopy(bonds, 0, newbonds, 0, bonds.length);
		bonds = newbonds;
	}
	/**
	 *  Grows the atom array by a given size 
	 *
	 * @see    org.openscience.cdk.AtomContainer#growArraySize growArraySize 
	 */
	protected void growAtomArray()
	{
		Atom[] newatoms = new Atom[atoms.length + growArraySize];
		System.arraycopy(atoms, 0, newatoms, 0, atoms.length);
		atoms = newatoms;
	}
	/**
	 *  Description of the Method 
	 *
	 * @param  ct     Description of Parameter 
	 * @param  atoms  Description of Parameter 
	 * @param  pos1   Description of Parameter 
	 * @param  pos2   Description of Parameter 
	 */
	protected void wireAtoms(int[][] ct, Atom[] atoms, int pos1, int pos2)
	{
		if (pos1 < atoms.length)
		{
			if (pos2 < atoms.length)
			{
			}
		}
	}
}

