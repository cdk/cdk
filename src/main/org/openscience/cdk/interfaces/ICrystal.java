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

import javax.vecmath.Vector3d;

/**
 * Class representing a molecular crystal.
 * The crystal is described with molecules in fractional
 * coordinates and three cell axes: a,b and c.
 *
 * <p>The crystal is designed to store only the asymetric atoms.
 * Though this is not enforced, it is assumed by all methods.
 *
 * @cdk.module interfaces
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword crystal
 */
public interface ICrystal extends IAtomContainer {
	
    /**
     * Adds the atoms in the AtomContainer as cell content. Symmetry related 
     * atoms should not be added unless P1 space group is used.
     */
    public void add(IAtomContainer container);

    /**
     * Adds the atom to the crystal. Symmetry related atoms should
     * not be added unless P1 space group is used.
     */
    public void addAtom(IAtom atom);

    /**
     * Sets the A unit cell axes in carthesian coordinates in a 
     * eucledian space.
     *
     * @param  newAxis the new A axis
     * @see    #getA
     */
    public void setA(Vector3d newAxis);

    /**
     * Gets the A unit cell axes in carthesian coordinates
     * as a three element double array.
     *
     * @return a Vector3D representing the A axis
     * @see       #setA
     */
    public Vector3d getA();

    /**
     * Sets the B unit cell axes in carthesian coordinates.
     *
     * @param  newAxis the new B axis
     * @see    #getB
     */
    public void setB(Vector3d newAxis);

    /**
     * Gets the B unit cell axes in carthesian coordinates
     * as a three element double array.
     *
     * @return a Vector3D representing the B axis
     * @see       #setB
     */
    public Vector3d getB();

    /**
     * Sets the C unit cell axes in carthesian coordinates.
     *
     * @param  newAxis the new C axis
     * @see       #getC
     */
    public void setC(Vector3d newAxis);

    /**
     * Gets the C unit cell axes in carthesian coordinates
     * as a three element double array.
     *
     * @return a Vector3D representing the C axis
     * @see       #setC
     */
    public Vector3d getC();

    /**
     * Gets the space group of this crystal.
     *
     * @return the space group of this crystal structure
     * @see       #setSpaceGroup
     */
    public String getSpaceGroup();

    /**
     * Sets the space group of this crystal.
     *
     * @param   group  the space group of this crystal structure
     * @see       #getSpaceGroup
     */
    public void setSpaceGroup(String group);

    /**
     * Gets the number of asymmetric parts in the unit cell.
     *
     * @return the number of assymetric parts in the unit cell
     * @see    #setZ
     */
    public Integer getZ();

    /**
     * Sets the number of assymmetric parts in the unit cell.
     *
     * @param   value the number of assymetric parts in the unit cell
     * @see           #getZ
     */
    public void setZ(Integer value);

}
