/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk;

import java.io.Serializable;

import javax.vecmath.Vector3d;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.ICrystal;

/**
 * Class representing a molecular crystal.
 * The crystal is described with molecules in fractional
 * coordinates and three cell axes: a,b and c.
 *
 * <p>The crystal is designed to store only the asymmetric atoms.
 * Though this is not enforced, it is assumed by all methods.
 *
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword crystal
 */
public class Crystal extends AtomContainer implements Serializable, ICrystal, Cloneable
{

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide/serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 5919649450390509278L;

	/** The a axis. */
    private Vector3d aAxis;
    /** The b axis. */
    private Vector3d bAxis;
    /** The c axis. */
    private Vector3d cAxis;

    /**
     * Number of symmetry related atoms.
     */
    private Integer zValue = 1;

    /**
     * Number of symmetry related atoms.
     */
    private String spaceGroup = "P1";

    /**
     * Constructs a new crystal with zero length cell axis.
     */
    public Crystal() {
    	super();
        setZeroAxes();
    }

    /**
     * Constructs a new crystal with zero length cell axis
     * and adds the atoms in the AtomContainer as cell content.
     *
     * @param container  the AtomContainer providing the atoms and bonds
     */
    public Crystal(IAtomContainer container) {
        super(container);
        setZeroAxes();
    }

    /**
     * Sets the A unit cell axes in Cartesian coordinates in a 
     * Euclidean space.
     *
     * @param  newAxis the new A axis
     *
     * @see    #getA
     */
    public void setA(Vector3d newAxis) {
        aAxis = newAxis;
	notifyChanged();
    }

    /**
     * Gets the A unit cell axes in Cartesian coordinates
     * as a three element double array.
     *
     * @return a Vector3D representing the A axis
     *
     * @see       #setA
     */
    public Vector3d getA() {
        return aAxis;
    }

    /**
     * Sets the B unit cell axes in Cartesian coordinates.
     *
     * @param  newAxis the new B axis
     *
     * @see    #getB
     */
    public void setB(Vector3d newAxis) {
        bAxis = newAxis;
	notifyChanged();
    }

    /**
     * Gets the B unit cell axes in Cartesian coordinates
     * as a three element double array.
     *
     * @return a Vector3D representing the B axis
     *
     * @see       #setB
     */
    public Vector3d getB() {
        return bAxis;
    }

    /**
     * Sets the C unit cell axes in Cartesian coordinates.
     *
     * @param  newAxis the new C axis
     *
     * @see       #getC
     */
    public void setC(Vector3d newAxis) {
        cAxis = newAxis;
	notifyChanged();
    }

    /**
     * Gets the C unit cell axes in Cartesian coordinates
     * as a three element double array.
     *
     * @return a Vector3D representing the C axis
     *
     * @see       #setC
     */
    public Vector3d getC() {
        return cAxis;
    }

    /**
     * Gets the space group of this crystal.
     *
     * @return the space group of this crystal structure
     *
     * @see       #setSpaceGroup
     */
    public String getSpaceGroup() {
        return spaceGroup;
    }

    /**
     * Sets the space group of this crystal.
     *
     * @param   group  the space group of this crystal structure
     *
     * @see       #getSpaceGroup
     */
    public void setSpaceGroup(String group) {
        spaceGroup = group;
	notifyChanged();
    }

    /**
     * Gets the number of asymmetric parts in the unit cell.
     *
     * @return the number of asymmetric parts in the unit cell
     * @see    #setZ
     */
    public Integer getZ() {
        return zValue;
    }

    /**
     * Sets the number of asymmetric parts in the unit cell.
     *
     * @param   value the number of asymmetric parts in the unit cell
     * @see           #getZ
     */
    public void setZ(Integer value) {
        this.zValue = value;
	notifyChanged();
    }

    /**
     *  Makes a clone of this crystal.
     *
     * @return The cloned crystal.
     */
    public Object clone() throws CloneNotSupportedException {
        Crystal clone = (Crystal)super.clone();
        // clone the axes
        clone.setA(new Vector3d(this.aAxis));
        clone.setB(new Vector3d(this.bAxis));
        clone.setC(new Vector3d(this.cAxis));
        return clone;
    }

    /**
     * Returns a String representation of this crystal.
     */
    public String toString() {
        StringBuffer resultString = new StringBuffer(64);
        resultString.append("Crystal(").append(hashCode());
        if (getSpaceGroup() != null) {
        	resultString.append(", SG=").append(getSpaceGroup());
        }
        if (getZ() > 0) {
        	resultString.append(", Z=").append(getZ());
        }
        if (getA() != null) {
        	resultString.append(", a=(").append(aAxis.x).append(", ").append(aAxis.y).append(", ").append(aAxis.z);
        }
        if (getB() != null) {
        	resultString.append("), b=(").append(bAxis.x).append(", ").append(bAxis.y).append(", ").append(bAxis.z);
        }
        if (getC() != null) {
        	resultString.append("), c=(").append(cAxis.x).append(", ").append(cAxis.y).append(", ").append(cAxis.z);
        }
        resultString.append(", ").append(super.toString());
        return resultString.toString();
    }

    /**
     *  Initializes the unit cell axes to zero length.
     */
    private void setZeroAxes() {
        aAxis = new Vector3d(0.0, 0.0, 0.0);
        bAxis = new Vector3d(0.0, 0.0, 0.0);
        cAxis = new Vector3d(0.0, 0.0, 0.0);
    }

}
