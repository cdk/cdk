/* Bond.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
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
 * 
 */

package org.openscience.cdk;

import java.util.Vector;
import javax.vecmath.*;

/**
 * Implements the concept of a bond, i.e. a number of electrons connecting 
 * a number of atoms.
 */

public class Bond extends ElectronContainer implements Cloneable
{
	/** The bond order of this bond */
	protected int order;

	/** A list of atoms participating in this bond */
	protected Atom[] atoms;

	/** A descriptor the stereochemical orientation of this bond. 
	  * @see org.openscience.cdk.CDKConstants org.openscience.cdk.CDKConstants for predefined values. 
	  * to be used here.
	  */
	protected int stereo; 
	
	/**
	 * Constructs a bond with a given order from an array of atoms
	 *
	 */
	public Bond(Atom atom1, Atom atom2, int order)
	{
		atoms = new Atom[2];
		setAtomAt(atom1, 0);
		setAtomAt(atom2, 1);
		setOrder(order);
	}

	/**
	 * Constructs a bond with a given order and stereo orientation from an array of atoms
	 *
	 */
	public Bond(Atom atom1, Atom atom2, int order, int stereo)
	{
		atoms = new Atom[2];
		setAtomAt(atom1, 0);
		setAtomAt(atom2, 1);
		setOrder(order);
		setStereo(stereo);
	}


	/**
	 * Returns the array of atoms making up this bond
	 *
	 * @return An array of atoms participating in this bond
	 */
	public Atom[] getAtoms()
	{
		return this.atoms;
	}
	
	/**
	 * Prepares and returns a Vector containing all the 
	 * Atom objects in this bond
	 *
	 * @return A Vector containing all the Atom objects in this AtomContainer    
	 */
	public Vector getAtomsVector()
	{
		Vector atomsVector = new Vector();
		for (int f = 0; f < getAtomCount(); f++)
		{
			atomsVector.addElement(getAtomAt(f));
		}
		return atomsVector;
	}
	
	
	/**
	 * Sets the array of atoms making up this bond
	 *
	 * @param   atoms An array of atoms that forms this bond
	 */
	public void setAtoms(Atom[] atoms)
	{
		this.atoms = atoms;
	}


	/**
	 * Returns the number of Atoms in this Bond
	 *
	 * @return The number of Atoms in this Bond    
	 */
	public int getAtomCount()
	{
		return atoms.length;
	}

	/**
	 * Returns an Atom from this bond.
	 *
	 * @param   position  The position in this bond where the atom is 
	 */
	public Atom getAtomAt(int position)
	{
		return atoms[position];
	}

    

	/**
	 * Returns the atom connected to the given atom.
	 *
	 * @param   atom  The atom the bond partner is searched of
	 * @return     the connected atom or null
	 */
	public Atom getConnectedAtom(Atom atom)
	{
		if (atoms[0] == atom)
		{
			return atoms[1];
		}
		else if (atoms[1] == atom)
		{
			return atoms[0];
		}
		return null;

	}

	/**
	 * Returns true if the given atom participates in this bond.
	 *
	 * @param   atom  The atom to be tested if it participates in this bond
	 * @return     true if the atom participates in this bond
	 */
	public boolean contains(Atom atom)
	{
		if (atoms[0] == atom)
		{
			return true;
		}
		else if (atoms[1] == atom)
		{
			return true;
		}
		return false;
	}
	

	/**
	 * Sets an Atom in this bond.
	 *
	 * @param   atom  The atom to be set
	 * @param   position  The position in this bond where the atom is to be inserted
	 */
	public void setAtomAt(Atom atom, int position)
	{
		atoms[position] = atom;
	}

	/**
	 * Returns the bond order of this bond
	 * @see org.openscience.cdk.CDKConstants org.openscience.cdk.CDKConstants for predefined values.
	 *
	 * @return The bond order of this bond
	 */
	public int getOrder()
	{
		return this.order;
	}


	/**
	 * Sets the bond order of this bond
	 * @see org.openscience.cdk.CDKConstants org.openscience.cdk.CDKConstants for predefined values.
	 *
	 * @param   order The bond order to be assigned to this bond
	 */
	public void setOrder(int order)
	{
		this.order = order;
	}



	/**
	 * Returns the stereo descriptor for this bond. 
	 * @see org.openscience.cdk.CDKConstants org.openscience.cdk.CDKConstants for predefined values.
	 *
	 * @return The stereo descriptor for this bond
	 */
	public int getStereo()
	{
		return this.stereo;
	}


	/**
	 * Sets the stereo descriptor for this bond.
	 * @see org.openscience.cdk.CDKConstants org.openscience.cdk.CDKConstants for predefined values.
	 *
	 * @param   stereo The stereo descriptor to be assigned to this bond.
	 */
	public void setStereo(int stereo)
	{
		this.stereo = stereo;
	}
	
	public Point2d get2DCenter()
	{
		double x = 0, y = 0;
		for (int f = 0; f < getAtomCount(); f++)
		{
			x += getAtomAt(f).getX2D();
			y += getAtomAt(f).getY2D();
		}
		return new Point2d(x / ((double)getAtomCount()), y / ((double)getAtomCount()));
	}
	

	public Point3d get3DCenter()
	{
		double x = 0, y = 0, z = 0;
		for (int f = 0; f < getAtomCount(); f++)
		{
			x += getAtomAt(f).getX3D();
			y += getAtomAt(f).getY3D();
			z += getAtomAt(f).getZ3D();
		}
		return new Point3d(x / getAtomCount(), y / getAtomCount(), z / getAtomCount());
		
	}


	/**
	 * Clones this bond object.
	 *
	 * @return  The cloned object   
	 */
	public Object clone()
	{
		Bond o = null;
		try
		{
			o = (Bond)super.clone();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
		o.atoms = (Atom[])atoms.clone();
		return o;
	}
	
}



