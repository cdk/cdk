/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 * 
 *  Contact: cdk-devel@lists.sourceforge.net
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
 * Implements the concept of a bond between two atoms.
 * A bond is considered to be a number of electrons connecting 
 * a two of atoms.
 *
 * @keyword bond
 * @keyword atom
 * @keyword electron
 */
public class Bond extends ElectronContainer implements java.io.Serializable, Cloneable
{
	/** The bond order of this bond. */
	protected double order;

    /** Number of atoms contained by this object. */
    protected int atomCount = 2;
    
	/** A list of atoms participating in this bond. */
	protected Atom[] atoms;

	/** 
     * A descriptor the stereochemical orientation of this bond.
     *
	 * @see org.openscience.cdk.CDKConstants for predefined values
	 *      to be used here.
	 */
	protected int stereo; 
	
	/**
	 * Constructs an empty bond.
	 *
	 */
	public Bond() {
        this(null, null, 0.0, CDKConstants.STEREO_BOND_UNDEFINED);
	}

	/**
	 * Constructs a bond with a single bond order..
     *
     * @param atom1  the first Atom in the bond
     * @param atom2  the second Atom in the bond
     */
    public Bond(Atom atom1, Atom atom2) {
        this(atom1, atom2, 1.0, CDKConstants.STEREO_BOND_UNDEFINED);
    }
    
    /**
	 * Constructs a bond with a given order.
     *
     * @param atom1  the first Atom in the bond
     * @param atom2  the second Atom in the bond
     * @param order  the bond order
     */
    public Bond(Atom atom1, Atom atom2, double order) {
        this(atom1, atom2, order, CDKConstants.STEREO_BOND_UNDEFINED);
    }

	/**
	 * Constructs a bond with a given order and stereo orientation from an array of atoms.
	 *
     * @param atom1  the first Atom in the bond
     * @param atom2  the second Atom in the bond
     * @param order  the bond order
     * @param stereo a descriptor the stereochemical orientation of this bond
     */
    public Bond(Atom atom1, Atom atom2, double order, int stereo) {
        atoms = new Atom[2];
        setAtomAt(atom1, 0);
        setAtomAt(atom2, 1);
        setOrder(order);
        setStereo(stereo);
    }

	/**
	 * Returns the array of atoms making up this bond.
	 *
	 * @return An array of atoms participating in this bond
     *
     * @see    #setAtoms
	 */
    public Atom[] getAtoms() {
        Atom[] returnAtoms = new Atom[getAtomCount()];
        System.arraycopy(this.atoms, 0, returnAtoms, 0, returnAtoms.length);
        return returnAtoms;
    }
	
	/**
	 * Prepares and returns a Vector containing all the 
	 * Atom objects in this bond.
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
	 * Sets the array of atoms making up this bond.
	 *
	 * @param   atoms An array of atoms that forms this bond
     *
     * @see    #getAtoms
	 */
	public void setAtoms(Atom[] atoms)
	{
		this.atoms = atoms;
	}


	/**
	 * Returns the number of Atoms in this Bond.
	 *
	 * @return The number of Atoms in this Bond    
	 */
	public int getAtomCount()
	{
		return atomCount;
	}

	/**
	 * Returns an Atom from this bond.
	 *
	 * @param   position  The position in this bond where the atom is
     * @return            The atom at the specified position
     *
     * @see     #setAtomAt
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
     *
     * @see     #getAtomAt
	 */
	public void setAtomAt(Atom atom, int position)
	{
		atoms[position] = atom;
	}

	/**
	 * Returns the bond order of this bond.
	 * @see org.openscience.cdk.CDKConstants org.openscience.cdk.CDKConstants for predefined values.
	 *
	 * @return The bond order of this bond
     *
     * @see     #setOrder
	 */
	public double getOrder()
	{
		return this.order;
	}


	/**
	 * Sets the bond order of this bond.
	 * @see org.openscience.cdk.CDKConstants org.openscience.cdk.CDKConstants for predefined values.
	 *
	 * @param   order The bond order to be assigned to this bond
     *
     * @see     #getOrder
	 */
	public void setOrder(double order)
	{
		this.order = order;
	}



	/**
	 * Returns the stereo descriptor for this bond. 
	 *
	 * @return The stereo descriptor for this bond
     *
     * @see     #setStereo
	 * @see     org.openscience.cdk.CDKConstants for predefined values.
	 */
	public int getStereo()
	{
		return this.stereo;
	}


	/**
	 * Sets the stereo descriptor for this bond.
	 *
	 * @param   stereo The stereo descriptor to be assigned to this bond.
     *
     * @see     #getStereo
	 * @see     org.openscience.cdk.CDKConstants for predefined values.
	 */
	public void setStereo(int stereo)
	{
		this.stereo = stereo;
	}
	

	/**
	 * Returns the geometric 2D center of the bond.
	 *
	 * @return The geometric 2D center of the bond
	 */
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
	


	/**
	 * Returns the geometric 3D center of the bond.
	 *
	 * @return The geometric 3D center of the bond    
	 */
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
	 * Returns the geometric length of this bond in 2D space.
	 *
	 * @return The geometric length of this bond
	 */
	public double getLength()
	{
		Point2d p1 = getAtomAt(0).getPoint2D();
		Point2d p2 = getAtomAt(1).getPoint2D();
		return p1.distance(p2);
	}


  /**
   * Compares a bond with this bond.
   *
   * @param  object Object of type Bond
   * @return        Return true, if the bond is equal to this bond
   */
  public boolean compare(Object object)
  {
    if (object instanceof Bond)
    {
      Bond bond = (Bond) object;
      for(int i=0; i<atoms.length; i++)
        if (!bond.contains(atoms[i]))
          return false;

      // not important ??!!
      //if (order==bond.order)
      //  return false;
      
      return true;
    }
    return false;
  }


	/**
	 * Clones this bond object. Notice that the references to the 
	 * atom object remain untouched, i.e. you just get a 
	 * new bond object that points to the same two atoms as the old one.
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
		return o;
	}

	/**
	 * Returns a one line string representation of this Container.
     * This method is conform RFC #9.
	 *
	 * @return    The string representation of this Container
	 */
	public String toString() {
		ElectronContainer ec;
		StringBuffer s = new StringBuffer();
        s.append("Bond(");
        s.append(this.hashCode());
        s.append(", #O:" + getOrder());
        s.append(", #S:" + getStereo());
        // s.append("#L:" + getLength() + ", ");
        Atom[] atoms = getAtoms();
        s.append(", #A:" + atoms.length);
        for (int i=0; i < atoms.length; i++) {
            if (atoms[i] == null) {
                s.append(", null");
            } else {
                s.append(", " + atoms[i].toString());
            }
        }
        s.append(")");
		return s.toString();
	}

}
