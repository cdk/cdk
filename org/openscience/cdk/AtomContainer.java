/* AtomContainer.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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
import javax.vecmath.*;

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
	 * Constructs an AtomContainer with 
	 * a copy of the atoms and bonds of another AtomContainer
	 * (A shallow copy, i.e., with the same objects as in the original AtomContainer)
	 *
	 * @param   ac  An AtomContainer to copy the atoms and bonds from 
	 */
	public AtomContainer(AtomContainer ac)
	{
		this();
		this.add(ac);
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
	public void setAtomAt(int number, Atom atom)
	{
		atoms[number] = atom;
	}

	/**
	 *  Sets the bond at position <code>number</code> . 
	 *
	 * @param  number  The position of the bond to be set. 
	 * @param  bond    The bond to be stored at position <code>number</code> 
	 */
	public void setBondAt(int number, Bond bond)
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
		Atom[] returnAtoms = new Atom[getAtomCount()];
		System.arraycopy(this.atoms, 0, returnAtoms, 0, returnAtoms.length);
		return returnAtoms;
	}

	/**
	 *  Returns the array of bonds of this AtomContainer 
	 *
	 * @return    The array of bonds of this AtomContainer 
	 */
	public Bond[] getBonds()
	{
		Bond[] returnBonds = new Bond[getBondCount()];
		System.arraycopy(this.bonds, 0, returnBonds, 0, returnBonds.length);
		return returnBonds;
	}

	/**
	 *  
	 * Returns the atom at position <code>number</code> in the
	 * container
	 *
	 * @param  number  The position of the atom to be returned. 
	 * @return         The atom at position <code>number</code> . 
	 */
	public Atom getAtomAt(int number)
	{
		return atoms[number];
	}


	/**
	 *  
	 * Returns the atom at position 0 in the
	 * container
	 *
	 * @return         The atom at position 0 . 
	 */
	public Atom getFirstAtom()
	{
		return atoms[0];
	}

	/**
	 *  
	 * Returns the atom at the last position in the
	 * container
	 *
	 * @return         The atom at the last position 
	 */
	public Atom getLastAtom()
	{
		return atoms[getAtomCount() - 1];
	}


	/**
	 *  
	 * Returns the position of a given atom in the atoms array 
	 *
	 * @param  atom    The atom to be sought
	 * @return         The Position of the atom in the atoms array. 
	 */

	public int getAtomNumber(Atom atom) throws org.openscience.cdk.exception.NoSuchAtomException
	{
		for (int f = 0; f < getAtomCount(); f++)
		{
			if (getAtomAt(f) == atom)
			{
				return f;
			}
		}
		throw new org.openscience.cdk.exception.NoSuchAtomException("No such Atom");
	}
	

	/**
	 *  
	 * Returns the bond at position <code>number</code> in the
	 * container
	 *
	 * @param  number  The position of the bond to be returned. 
	 * @return         The bond at position <code>number</code> . 
	 */
	public Bond getBondAt(int number)
	{
		return bonds[number];
	}
	

	/**
	 * Returns the bond that connectes the two given atoms.
	 *
	 * @param   a1  The first atom
	 * @param   a2  The second atom
	 * @return     The bond that connectes the two atoms
	 */
	public Bond getBond(Atom a1, Atom a2)
	{
		for (int i = 0; i < getBondCount(); i++)
		{
			if (bonds[i].contains(a1))
			{
				if (bonds[i].getConnectedAtom(a1) == a2)
				{
					return bonds[i];
				}
			}
		}
		return null;
	}


	/**
	 * Returns an array of all atoms connected to the given atom.
	 *
	 * @param   atom  The atom the bond partners are searched of.
	 * @return     The array with the size of connected atoms
	 */
	public Atom[] getConnectedAtoms(Atom atom)
	{
		Vector atomsVec = new Vector();  
		Bond bond;
		for (int i = 0; i < bondCount; i++)
		{
			bond = bonds[i];
			if (bond.contains(atom))
			{
				atomsVec.addElement(bond.getConnectedAtom(atom));
			}
		}
		Atom[] conAtoms = new Atom[atomsVec.size()];
		atomsVec.copyInto(conAtoms);
		return conAtoms;
	}
	
	

	/**
	 * Returns an array of all bonds connected to the given atom
	 *
	 * @param   atom  The atom the connected bonds are searched of
	 * @return     The array with the size of connected atoms
	 */
	public Bond[] getConnectedBonds(Atom atom)
	{
		Vector bondsVec = new Vector();
		Bond bond;
		for (int i = 0; i < bondCount; i++)
		{
			if (bonds[i].contains(atom))
			{
				bondsVec.addElement(bonds[i]);
			}
		}
		Bond[] conBonds = new Bond[bondsVec.size()];
		bondsVec.copyInto(conBonds);
		return conBonds;
	}
	
	/**
	 * Returns the number of connected atoms (degree) to the given atom
	 *
	 * @param   atomnumber   The atomnumber the degree is searched for
	 * @return  The number of connected atoms (degree)
	 */
	public int getDegree(int atomnumber)
	{
		return getDegree(getAtomAt(atomnumber));
	}


	/**
	 * Returns the number of connected atoms (degree) to the given atom
	 *
	 * @param   atom   The atom the degree is searched of
	 * @return     The number of connected atoms (degree)
	 */
	public int getDegree(Atom atom)
	{
		int degree = 0;
		Bond bond;
		for (int i = 0; i < bondCount; i++)
		{
			bond = bonds[i];
			if (bond.contains(atom))
			{
				degree++;
			}
		}
		return degree;
	}
	
   /**
     * Adds all atoms and bonds of a given atomcontainer to this container
     *
     * @param   atomContainer  The atomcontainer to be added
     */
    public void add(AtomContainer atomContainer)
    {
            for (int f = 0; f < atomContainer.getAtomCount(); f++)
            {
				if (!contains(atomContainer.getAtomAt(f)))
				{
					addAtom(atomContainer.getAtomAt(f));
				}
            }
            for (int f = 0; f < atomContainer.getBondCount(); f++)
            {
	            if (!contains(atomContainer.getBondAt(f)))
	            {
                    addBond(atomContainer.getBondAt(f));
	            }
            }
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
	}
	
    /**
     * Removes all atoms and bonds of a given atomcontainer from this container
     *
     * @param   atomContainer  The atomcontainer to be removed
     * @exception   Exception  
     */
    public void remove(AtomContainer atomContainer) throws org.openscience.cdk.exception.NoSuchAtomException
    {
            for (int f = 0; f < atomContainer.getAtomCount(); f++)
            {
                    removeAtom(atomContainer.getAtomAt(f));
            }
            for (int f = 0; f < atomContainer.getBondCount(); f++)
            {
                    removeBond(atomContainer.getBondAt(f));
            }
    }	

	/**
	 * removes the bond at the given position from this container
	 *
	 * @param   position  The position of the bond in the bonds array
	 */
	public Bond removeBond(int position)
	{
		Bond bond = getBondAt(position);
		for (int i = position; i < bondCount - 1; i++)
		{
			bonds[i] = bonds[i + 1];
		}
		bonds[bondCount - 1] = null;
		bondCount--;		
		return bond;
	}
	
	
	/**
	 * removes this bond from this container
	 *
	 * @param   bond  The bond to be removed
	 */
	public Bond removeBond(Bond bond)
	{
		for (int i = 0; i < bondCount; i++)
		{
			if (bonds[i].equals(bond))
			{
				return removeBond(i);
			}
		}
		return null;
	}


	/**
	 * Removes the bond that connects the two given atoms.
	 *
	 * @param   a1  The first atom
	 * @param   a2  The second atom
	 * @return     The bond that connectes the two atoms
	 */
	public Bond removeBond(Atom a1, Atom a2)
	{
		for (int i = 0; i < getBondCount(); i++)
		{
			if (bonds[i].contains(a1))
			{
				if (bonds[i].getConnectedAtom(a1) == a2)
				{
					return removeBond(bonds[i]);
				}
			}
		}
		return null;
	}
	


	/**
	 * Removes the atom at the given position from the AtomContainer
	 * !!! Note that the bonds are unaffected, You also have to take care of 
	 * removeing all bonds to this atom from the container.
	 *
	 * @param   position  The position of the atom to be removed.
	 */
	public void removeAtom(int position)
	{
		for (int i = position; i < atomCount - 1; i++)
		{
			atoms[i] = atoms[i + 1];
		}
		atoms[atomCount - 1] = null;
		atomCount--;		
	}
	

	/**
	 * Removes the given atom from the AtomContainer
	 * !!! Note that the bonds are unaffected, You also have to take care of 
	 * removeing all bonds to this atom from the container.
	 *
	 * @param   atom  The atom to be removed
	 * @exception   org.openscience.cdk.exception.NoSuchAtomException  throws if the atom is not in the container
	 */
	public void removeAtom(Atom atom) throws org.openscience.cdk.exception.NoSuchAtomException
	{
		int position = getAtomNumber(atom);
		removeAtom(position);
	}

	/**
	 * removes all atoms and bond from this container
	 *
	 */
	public void removeAllElements()
	{
		atoms = new Atom[growArraySize];
		bonds = new Bond[growArraySize];
		atomCount = 0;
		bondCount = 0;	
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
		Bond bond = new Bond(getAtomAt(atom1), getAtomAt(atom2), order, stereo);
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
		Bond bond = new Bond(getAtomAt(atom1), getAtomAt(atom2), order);
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
	 * Returns the number of bonds for a given Atom
	 *
	 * @param   atom  The atom
	 * @return     The number of bonds for this atom
	 */
	public int getBondCount(Atom atom)
	{
		int count = 0; 
		for (int i = 0; i < getBondCount(); i++)
		{
			if (bonds[i].contains(atom))
			{
				count++;
			}
		}
		return count;
	}



	/**
	 * Compares this AtomContainer with another given AtomContainer 
	 * and returns the Intersection between them
	 * Important Note: This is not a maximum common substructure
	 *
	 * @return  An AtomContainer containing the Intersection between this AtomContainer and another given one
	 */
	
	public AtomContainer getIntersection(AtomContainer ac)
	{
		AtomContainer intersection = new AtomContainer();
	
		for (int i = 0; i < getAtomCount(); i++)
		{
			if (ac.contains(getAtomAt(i)))
			{
				 intersection.addAtom(getAtomAt(i));
			}
		}
		for (int i = 0; i < getBondCount(); i++)
		{
			if (ac.contains(getBondAt(i)))
			{
				 intersection.addBond(getBondAt(i));
			}
		}
		return intersection;
	}


	/**
	 * True, if the AtomContainer contains the given bond object
	 *
	 * @param   bond  the bond this AtomContainer is searched for
	 * @return  True, if the AtomContainer contains the given bond object   
	 */
	public boolean contains(Bond bond)
	{
		for (int i = 0; i < getBondCount(); i++)
		{
			if (bond == bonds[i])
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * True, if the AtomContainer contains the given atom object
	 *
	 * @param   atom  the atom this AtomContainer is searched for
	 * @return  True, if the AtomContainer contains the given atom object   
	 */
	public boolean contains(Atom atom)
	{
		for (int i = 0; i < getAtomCount(); i++)
		{
			if (atom == atoms[i])
			{
				return true;
			}
		}
		return false;
	}
	

	

	/**
	 * Returns the geometric center of all the atoms in this atomContainer
	 *
	 * @return the geometric center of the atoms in this atomContainer
	 */
	public Point2d get2DCenter()
	{
		double centerX = 0, centerY = 0;
		double counter = 0;
		for (int i = 0; i < getAtomCount(); i++)
		{
			if (atoms[i].getPoint2D() != null)
			{
				centerX += atoms[i].getPoint2D().x;
				centerY += atoms[i].getPoint2D().y;
				counter ++;
			}
		}
		Point2d point = new Point2d(centerX / (counter), centerY / (counter));
		return point;
	}


	/**
	 * Returns a connection matrix representation 
	 * of this AtomContainer
	 *
	 * @return  A connection matrix representation of this AtomContainer
	 */

	public int[][] getConnectionMatrix() throws org.openscience.cdk.exception.NoSuchAtomException
	{
		Bond bond = null;
		int i, j;
		int[][] conMat = new int[getAtomCount()][getAtomCount()];
		for (int f = 0; f < getBondCount(); f++)
		{
			bond = getBondAt(f);
			i = getAtomNumber(bond.getAtomAt(0));
			j = getAtomNumber(bond.getAtomAt(1));
			conMat[i][j] = bond.getOrder();
			conMat[j][i] = bond.getOrder();
		}
		return conMat;
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
		for (int i = 0; i < getAtomCount(); i++)
		{
			s.append(i + ". " + getAtomAt(i));
		}
		for (int i = 0; i < getBondCount(); i++)
		{
			bond = getBondAt(i);
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
		o.atoms = (Atom[])atoms.clone();
		o.bonds = (Bond[])bonds.clone();
		return o;
	}
}










