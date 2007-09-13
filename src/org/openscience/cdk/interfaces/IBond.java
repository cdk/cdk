/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.interfaces;

import java.util.Iterator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

/**
 * Implements the concept of a covalent bond between two or more atoms. A bond is
 * considered to be a number of electrons connecting two ore more atoms.
 *
 * @cdk.module interfaces
 *
 * @author      egonw
 * @cdk.created 2005-08-24
 * @cdk.keyword bond
 * @cdk.keyword atom
 * @cdk.keyword electron
 */
public interface IBond extends IElectronContainer {

	public enum Order {
		EMPTY,
		SINGLE,
		DOUBLE,
		TRIPLE,
		QUADRUPLE
	}
	
	/**
	 *  Returns the Iterator to atoms making up this bond.
	 *
	 *@return    An Iterator to atoms participating in this bond
	 *@see       #setAtoms
	 */
	public Iterator<IAtom> atoms();

	/**
	 * Sets the array of atoms making up this bond.
	 *
	 * @param  atoms  An array of atoms that forms this bond
	 * @see           #atoms
	 */
	public void setAtoms(IAtom[] atoms);

	/**
	 * Returns the number of Atoms in this Bond.
	 *
	 * @return    The number of Atoms in this Bond
	 */
	public int getAtomCount();

	/**
	 * Returns an Atom from this bond.
	 *
	 * @param  position  The position in this bond where the atom is
	 * @return           The atom at the specified position
	 * @see              #setAtom
	 */
	public IAtom getAtom(int position);


	/**
	 * Returns the atom connected to the given atom.
	 *
	 * @param  atom  The atom the bond partner is searched of
	 * @return       the connected atom or null
	 */
	public IAtom getConnectedAtom(IAtom atom);

	/**
	 * Returns true if the given atom participates in this bond.
	 *
	 * @param  atom  The atom to be tested if it participates in this bond
	 * @return       true if the atom participates in this bond
	 */
	public boolean contains(IAtom atom);

	/**
	 * Sets an Atom in this bond.
	 *
	 * @param  atom      The atom to be set
	 * @param  position  The position in this bond where the atom is to be inserted
	 * @see              #getAtom
	 */
	public void setAtom(IAtom atom, int position);

	/**
	 * Returns the bond order of this bond.
	 *
	 * @return The bond order of this bond
	 * @see    org.openscience.cdk.CDKConstants org.openscience.cdk.CDKConstants
	 *         for predefined values.
	 * @see    #setOrder
	 */
	public double getOrder();

	/**
	 * Sets the bond order of this bond.
	 *
	 * @param  order The bond order to be assigned to this bond
	 * @see          org.openscience.cdk.CDKConstants for predefined values.
	 * @see          #getOrder
	 */
	public void setOrder(double order);

	/**
	 * Returns the stereo descriptor for this bond.
	 *
	 * @return    The stereo descriptor for this bond
	 * @see       #setStereo
	 * @see       org.openscience.cdk.CDKConstants for predefined values.
	 */
	public int getStereo();

	/**
	 * Sets the stereo descriptor for this bond.
	 *
	 * @param  stereo  The stereo descriptor to be assigned to this bond.
	 * @see            #getStereo
	 * @see            org.openscience.cdk.CDKConstants for predefined values.
	 */
	public void setStereo(int stereo);

	/**
	 * Returns the geometric 2D center of the bond.
	 *
	 * @return    The geometric 2D center of the bond
	 */
	public Point2d get2DCenter();

	/**
	 * Returns the geometric 3D center of the bond.
	 *
	 * @return    The geometric 3D center of the bond
	 */
	public Point3d get3DCenter();
	
	/**
	 * Compares a bond with this bond.
	 *
	 * @param  object  Object of type Bond
	 * @return         Return true, if the bond is equal to this bond
	 */
	public boolean compare(Object object);

	/**
	 * Checks wether a bond is connected to another one.
	 * This can only be true if the bonds have an Atom in common.
	 *
	 * @param  bond  The bond which is checked to be connect with this one
	 * @return       True, if the bonds share an atom, otherwise false
	 */
	public boolean isConnectedTo(IBond bond);
}

