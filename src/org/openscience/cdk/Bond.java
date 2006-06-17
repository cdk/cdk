/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

/**
 *  Implements the concept of a covalent bond between two atoms. A bond is
 *  considered to be a number of electrons connecting a two of atoms.
 *
 * @cdk.module data
 *
 * @author     steinbeck
 * @cdk.created    2003-10-02
 * @cdk.keyword    bond
 * @cdk.keyword    atom
 * @cdk.keyword    electron
 */
public class Bond extends ElectronContainer implements java.io.Serializable, IBond
{
	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 7057060562283387384L;

	/**
	 *  The bond order of this bond.
	 */
	protected double order;

	/**
	 *  Number of atoms contained by this object.
	 */
	protected int atomCount = 2;

	/**
	 *  A list of atoms participating in this bond.
	 */
	protected IAtom[] atoms;

	/**
	 *  A descriptor the stereochemical orientation of this bond.
	 *
	 *@see    org.openscience.cdk.CDKConstants for predefined values to be used
	 *      here.
	 */
	protected int stereo;


	/**
	 *  Constructs an empty bond.
	 */
	public Bond() {
		this(null, null, 0.0, CDKConstants.STEREO_BOND_NONE);
	}


	/**
	 *  Constructs a bond with a single bond order..
	 *
	 *@param  atom1  the first Atom in the bond
	 *@param  atom2  the second Atom in the bond
	 */
	public Bond(IAtom atom1, IAtom atom2)
	{
		this(atom1, atom2, 1.0, CDKConstants.STEREO_BOND_NONE);
	}


	/**
	 *  Constructs a bond with a given order.
	 *
	 *@param  atom1  the first Atom in the bond
	 *@param  atom2  the second Atom in the bond
	 *@param  order  the bond order
	 */
	public Bond(IAtom atom1, IAtom atom2, double order)
	{
		this(atom1, atom2, order, CDKConstants.STEREO_BOND_NONE);
	}


	/**
	 *  Constructs a bond with a given order and stereo orientation from an array
	 *  of atoms.
	 *
	 *@param  atom1   the first Atom in the bond
	 *@param  atom2   the second Atom in the bond
	 *@param  order   the bond order
	 *@param  stereo  a descriptor the stereochemical orientation of this bond
	 */
	public Bond(IAtom atom1, IAtom atom2, double order, int stereo)
	{
		atoms = new Atom[2];
		atoms[0] = atom1;
		atoms[1] = atom2;
		this.order = order;
		this.stereo = stereo;
	}


	/**
	 *  Returns the array of atoms making up this bond.
	 *
	 *@return    An array of atoms participating in this bond
	 *@see       #setAtoms
	 */
	public IAtom[] getAtoms()
	{
		IAtom[] returnAtoms = new IAtom[getAtomCount()];
		System.arraycopy(this.atoms, 0, returnAtoms, 0, returnAtoms.length);
		return returnAtoms;
	}

	/**
	 *  Sets the array of atoms making up this bond.
	 *
	 *@param  atoms  An array of atoms that forms this bond
	 *@see           #getAtoms
	 */
	public void setAtoms(IAtom[] atoms)
	{
		this.atoms = atoms;
		notifyChanged();
	}


	/**
	 *  Returns the number of Atoms in this Bond.
	 *
	 *@return    The number of Atoms in this Bond
	 */
	public int getAtomCount()
	{
		return atomCount;
	}


	/**
	 *  Returns an Atom from this bond.
	 *
	 *@param  position  The position in this bond where the atom is
	 *@return           The atom at the specified position
	 *@see              #setAtomAt
	 */
	public IAtom getAtomAt(int position)
	{
		return (IAtom)atoms[position];
	}



	/**
	 *  Returns the atom connected to the given atom.
	 *
	 *@param  atom  The atom the bond partner is searched of
	 *@return       the connected atom or null
	 */
	public IAtom getConnectedAtom(IAtom atom)
	{
		if (atoms[0] == atom)
		{
			return (Atom)atoms[1];
		} else if (atoms[1] == atom)
		{
			return (Atom)atoms[0];
		}
		return null;
	}


	/**
	 *  Returns true if the given atom participates in this bond.
	 *
	 *@param  atom  The atom to be tested if it participates in this bond
	 *@return       true if the atom participates in this bond
	 */
	public boolean contains(IAtom atom)
	{
		if (atoms[0] == atom)
		{
			return true;
		} else if (atoms[1] == atom)
		{
			return true;
		}
		return false;
	}


	/**
	 *  Sets an Atom in this bond.
	 *
	 *@param  atom      The atom to be set
	 *@param  position  The position in this bond where the atom is to be inserted
	 *@see              #getAtomAt
	 */
	public void setAtomAt(IAtom atom, int position)
	{
		atoms[position] = atom;
		notifyChanged();
	}


	/**
	 *  Returns the bond order of this bond.
	 *
	 *@return    The bond order of this bond
	 *@see       org.openscience.cdk.CDKConstants org.openscience.cdk.CDKConstants
	 *      for predefined values.
	 *@see       #setOrder
	 */
	public double getOrder()
	{
		return this.order;
	}


	/**
	 *  Sets the bond order of this bond.
	 *
	 *@param  order  The bond order to be assigned to this bond
	 *@see           org.openscience.cdk.CDKConstants
	 *      org.openscience.cdk.CDKConstants for predefined values.
	 *@see           #getOrder
	 */
	public void setOrder(double order)
	{
		this.order = order;
		notifyChanged();
	}



	/**
	 *  Returns the stereo descriptor for this bond.
	 *
	 *@return    The stereo descriptor for this bond
	 *@see       #setStereo
	 *@see       org.openscience.cdk.CDKConstants for predefined values.
	 */
	public int getStereo()
	{
		return this.stereo;
	}


	/**
	 *  Sets the stereo descriptor for this bond.
	 *
	 *@param  stereo  The stereo descriptor to be assigned to this bond.
	 *@see            #getStereo
	 *@see            org.openscience.cdk.CDKConstants for predefined values.
	 */
	public void setStereo(int stereo)
	{
		this.stereo = stereo;
		notifyChanged();
	}


	/**
	 *  Returns the geometric 2D center of the bond.
	 *
	 *@return    The geometric 2D center of the bond
	 */
	public Point2d get2DCenter()
	{
		double xOfCenter = 0;
		double yOfCenter = 0;
		for (int f = 0; f < getAtomCount(); f++)
		{
			xOfCenter += getAtomAt(f).getX2d();
			yOfCenter += getAtomAt(f).getY2d();
		}
		return new Point2d(xOfCenter / ((double) getAtomCount()), 
                           yOfCenter / ((double) getAtomCount()));
	}



	/**
	 *  Returns the geometric 3D center of the bond.
	 *
	 *@return    The geometric 3D center of the bond
	 */
	public Point3d get3DCenter()
	{
		double xOfCenter = 0;
		double yOfCenter = 0;
		double zOfCenter = 0;
		for (int f = 0; f < getAtomCount(); f++)
		{
			xOfCenter += getAtomAt(f).getX3d();
			yOfCenter += getAtomAt(f).getY3d();
			zOfCenter += getAtomAt(f).getZ3d();
		}
		return new Point3d(xOfCenter / getAtomCount(), 
                           yOfCenter / getAtomCount(), 
                           zOfCenter / getAtomCount());
	}

	/**
	 *  Compares a bond with this bond.
	 *
	 *@param  object  Object of type Bond
	 *@return         Return true, if the bond is equal to this bond
	 */
	public boolean compare(Object object)
	{
		if (object instanceof IBond)
		{
			Bond bond = (Bond) object;
			for (int i = 0; i < atoms.length; i++)
			{
				if (!bond.contains(atoms[i]))
				{
					return false;
				}
			}

			// not important ??!!
			//if (order==bond.order)
			//  return false;

			return true;
		}
		return false;
	}


	/**
	 * Checks wether a bond is connected to another one.
	 * This can only be true if the bonds have an Atom in common.
	 *
	 * @param  bond  The bond which is checked to be connect with this one
	 * @return       True, if the bonds share an atom, otherwise false
	 */
	public boolean isConnectedTo(IBond bond)
	{
		for (int f = 0; f < getAtomCount(); f++)
		{
			if (bond.contains(getAtomAt(f)))
			{
				return true;
			}
		}
		return false;
	}


	/**
	 * Clones this bond object, including clones of the atoms between which the
     * bond is defined.
	 *
	 * @return    The cloned object
	 */
	public Object clone() throws CloneNotSupportedException {
		Bond clone = (Bond)super.clone();
        // clone all the Atoms
        if (atoms != null) {
		    clone.atoms = new IAtom[atoms.length];
            for (int f = 0; f < atoms.length; f++) {
                if (atoms[f] != null) {
                    clone.atoms[f] = (IAtom)((IAtom)atoms[f]).clone();
                }
            }
        }
		return clone;
	}


	/**
	 *  Returns a one line string representation of this Container. This method is
	 *  conform RFC #9.
	 *
	 *@return    The string representation of this Container
	 */
	public String toString() {
		StringBuffer resultString = new StringBuffer();
		resultString.append("Bond(");
		resultString.append(this.hashCode());
		resultString.append(", #O:").append(getOrder());
		resultString.append(", #S:").append(getStereo());
		IAtom[] atoms = getAtoms();
		resultString.append(", #A:").append(atoms.length);
		for (int i = 0; i < atoms.length; i++)
		{
			if (atoms[i] == null)
			{
				resultString.append(", null");
			} else
			{
				resultString.append(", ").append(atoms[i].toString());
			}
		}
		resultString.append(")");
		return resultString.toString();
	}

}

