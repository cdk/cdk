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

import java.util.*;

/**
 *  
 * Base class for all chemical objects that maintain a list of Atoms and
 * Bonds
 *
 * @author     steinbeck 
 * @created    October 2, 2000 
 */
public class AtomContainer extends ChemObject implements Cloneable{

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
	 * Constructs an empty AtomContainer that will contain a certain
	 * number of atoms and bonds.
	 *
	 * @param   atomCount  Number of atoms to be in this container
	 * @param   bondCount  Number of bonds to be in this container
	 */
	public AtomContainer(int atomCount, int bondCount)
	{
		this.atomCount = atomCount;
		this.bondCount = bondCount;
		atoms = new Atom[atomCount];
		bonds = new Bond[bondCount];
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
	 *  Sets the bond at position <code>number</code> . 
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
	 * Returns the position of a given atom in the atoms array 
	 *
	 * @param  atom    The atom to be sought
	 * @return         The Position of the atom in the atoms array. 
	 */

	public int getAtomNumber(Atom atom) throws Exception
	{
		for (int f = 0; f < getAtomCount(); f++)
		{
			if (getAtom(f).equals(atom))
			{
				return f;
			}
		}
		throw new Exception("No such Atom");
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
	 * Returns the bond that connectes the two given atoms.
	 *
	 * @param   a1  The first atom
	 * @param   a2  The second atom
	 * @return     The bond that connectes the two atoms
	 * @exception   Exception  thrown if the two atoms are not connected
	 */
	public Bond getBond(Atom a1, Atom a2) throws Exception
	{
		for (int i = 0; i < getBondCount(); i++)
		{
			if (bonds[i].contains(a1))
			{
				try
				{
					if (bonds[i].getConnectedAtom(a1) == a2)
					{
						return bonds[i];
					}
				}
				catch (Exception exc)
				{
					throw exc;
				}
			}
		}
		System.out.println("atom1  "+a1.toString()+"atom2  "+a2.toString());
		throw new Exception("atoms not connected");
	}

	/**
	 * Returns an array of all atoms connected to the given atom.
	 *
	 * @param   atom  The atom the bond partners are searched of.
	 * @return     The array with the size of connected atoms
	 * @exception   Exception  
	 */
	public Atom[] getConnectedAtoms(Atom atom) throws Exception
	{
		Atom[] conAtoms = new Atom[atom.getDegree()];
		Bond bond;
		int conAtomCount = 0;
		for (int i = 0; i < bondCount; i++)
		{
			bond = bonds[i];
			if (bond.contains(atom))
			{
				try
				{
					conAtoms[conAtomCount] = bond.getConnectedAtom(atom);
					conAtomCount++;
				}
				catch (Exception exc)
				{
					throw exc;
				}
			}
		}
		if (atom.getDegree() != conAtomCount)
		{
			System.out.println("degree:  "+ atom.getDegree()+"  atomcount  "+ conAtomCount);
			System.out.println("atom  "+ atom.toString());
			throw new Exception("wrong number of connected Atoms");
		}
		return conAtoms;
	}
	
	

	/**
	 * Returns an array of all bonds connected to the given atom
	 *
	 * @param   atom  The atom the connected bonds are searched of
	 * @return     The array with the size of connected atoms
	 * @exception   Exception  
	 */
	public Bond[] getConnectedBonds(Atom atom) throws Exception
	{
		Bond[] bonds = new Bond[atom.getDegree()];
		Bond bond;
		int conBondCount = 0;
		for (int i = 0; i < bondCount; i++)
		{
			bond = bonds[i];
			if (bond.contains(atom))
			{
				bonds[conBondCount] = bond;
				conBondCount++;
			}
		}
		if (atom.getDegree() != conBondCount)
		{
			throw new Exception("wrong number of connected Bonds");
		}
		return bonds;
	}
	
	/**
	 *  Adds an atom to this container 
	 *
	 * @param  atom  The atom to be added to this container 
	 */
	public void addAtom(Atom atom)
	{
		if (atomCount + 1 >= atoms.length)
		{
			growAtomArray();
		}
		atoms[atomCount] = atom;
		atomCount++;
	}
	
	
	
	/**
	 *  Adds a bond to this container 
	 *
	 * @param  bond  The bond to added to this container 
	 */
	public void addBond(Bond bond)
	{
		if (bondCount + 1 >= bonds.length)
		{
			growBondArray();
		}
		// are we supposed to check if the atoms forming this bond are
		// already in here and add them if neccessary?
		bonds[bondCount] = bond;
		bondCount++;
		bond.getAtomAt(0).incrementDegree();
		bond.getAtomAt(1).incrementDegree();
	}
	
	

	/**
	 * removes the bond at the given position from this container
	 *
	 * @param   position  The position of the bond in the bonds array
	 */
	public void removeBond(int position)
	{
		Bond bond = bonds[position];
		bond.getAtomAt(0).decrementDegree();
		bond.getAtomAt(1).decrementDegree();
		for (int i = position; i < bondCount; i++)
		{
			bonds[i] = bonds[i + 1];
		}
		bonds[bondCount] = null;
		bondCount--;		
	}
	
	
	/**
	 * removes this bond from this container
	 *
	 * @param   bond  The bond to be removed
	 */
	public void removeBond(Bond bond)
	{
		for (int i = 0; i < bondCount; i++)
		{
			if (bonds[i].equals(bond))
			{
				removeBond(i);
			}
		}
	}
	

	/**
	 *  Adds a bond to this container
	 *
	 * @param   atom1   Order of the first atom of the Bond
	 * @param   atom2   Order of the second atom of the Bond
	 * @param   order   Bondorder
	 * @param   stereo   Stereochemical orientation 
	 */
	public void addBond(int atom1, int atom2, int order, int stereo)
	{
		if (bondCount >= bonds.length)
		{
			growBondArray();
		}
		Bond bond = new Bond(getAtom(atom1), getAtom(atom2), order, stereo);
		addBond(bond);
	}


	 /**
	  *  Adds a bond to this container
	  *
	  * @param   atom1   Order of the first atom of the Bond
	  * @param   atom2   Order of the second atom of the Bond
	  * @param   order   Bondorder
	 */
	public void addBond(int atom1, int atom2, int order)
	{
		if (bondCount >= bonds.length)
		{
			growBondArray();
		}
		Bond bond = new Bond(getAtom(atom1), getAtom(atom2), order);
		addBond(bond);
	}



	/**
	 *  Grows the bond array by a given size 
	 *
	 * @see    org.openscience.cdk.AtomContainer#growArraySize growArraySize 
	 */
	protected void growBondArray()
	{
		growArraySize = bonds.length;
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
		growArraySize = atoms.length;
		Atom[] newatoms = new Atom[atoms.length + growArraySize];
		System.arraycopy(atoms, 0, newatoms, 0, atoms.length);
		atoms = newatoms;
	}


	/**
	 * Returns the number of Atoms in this Container
	 *
	 * @return   The number of Atoms in this Container  
	 */
	public int getAtomCount()
	{
		return this.atomCount;
	}

	/**
	 * Sets the number of atoms in this container
	 *
	 * @param   atomCount  The number of atoms in this container
	 */
	public void setAtomCount(int atomCount)
	{
		this.atomCount = atomCount;
	}	

	/**
	 * Returns the number of Bonds in this Container
	 *
	 * @return  The number of Bonds in this Container   
	 */
	public int getBondCount()
	{
		return this.bondCount;
	}


	/**
	 * Returns a string representation of this Container.
	 *
	 * @return  The string representation of this Container   
	 */
	public String toString()
	{
		Bond bond;
		StringBuffer s = new StringBuffer();
		System.out.println("Atomcount: " + getAtomCount());
		for (int i = 0; i < getAtomCount(); i++)
		{
			s.append(i + ". " + getAtom(i));
		}
		System.out.println("Bondcount: " + getBondCount());
		for (int i = 0; i < getBondCount(); i++)
		{
			bond = getBond(i);
			s.append("Bond: ");
			for (int j = 0; j < bond.getAtomCount(); j++)
			{
				try
				{
					s.append(getAtomNumber(bond.getAtomAt(j)) +  "   ");
				}
				catch(Exception e)
				{
					s.append("Inconsistent Bond Setting");
					e.printStackTrace();
				}
			}
			s.append("\n");
		}
		
		return s.toString();
	}

	/**
	 * Clones this atomContainer object.
	 *
	 * @return  The cloned object   
	 */
	public Object clone()
	{
		AtomContainer o = null;
		try
		{
			o = (AtomContainer)super.clone();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
		o.atoms = (Atom[])o.atoms.clone();
		o.bonds = (Bond[])o.bonds.clone();
		return o;
	}

}



