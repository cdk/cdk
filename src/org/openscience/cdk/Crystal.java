/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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

import javax.vecmath.Vector3d;

/**
 * Class representing a molecular crystal.
 * The crystal is described with molecules in fractional
 * coordinates and three cell axes: a,b and c.
 *
 * <p>The crystal is designed to store only the asymetric atoms.
 * Though this is not enforced, it is assumed by all methods.
 *
 * @cdk.module data
 *
 * @cdk.keyword crystal
 */
public class Crystal extends AtomContainer implements java.io.Serializable, org.openscience.cdk.interfaces.ICrystal
{

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
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
    private int zValue = 1;

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
    public Crystal(org.openscience.cdk.interfaces.IAtomContainer container) {
        super(container);
        setZeroAxes();
    }

    /**
     * Adds the atoms in the AtomContainer as cell content. Symmetry related 
     * atoms should not be added unless P1 space group is used.
     */
    public void add(org.openscience.cdk.interfaces.IAtomContainer container) {
        super.add(container);
    }

    /**
     * Adds the atom to the crystal. Symmetry related atoms should
     * not be added unless P1 space group is used.
     */
    public void addAtom(Atom atom) {
        super.addAtom(atom);
    }

    /**
     * Sets the A unit cell axes in carthesian coordinates in a 
     * eucledian space.
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
     * Gets the A unit cell axes in carthesian coordinates
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
     * Sets the B unit cell axes in carthesian coordinates.
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
     * Gets the B unit cell axes in carthesian coordinates
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
     * Sets the C unit cell axes in carthesian coordinates.
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
     * Gets the C unit cell axes in carthesian coordinates
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
     * @return the number of assymetric parts in the unit cell
     * @see    #setZ
     */
    public int getZ() {
        return zValue;
    }

    /**
     * Sets the number of assymmetric parts in the unit cell.
     *
     * @param   value the number of assymetric parts in the unit cell
     * @see           #getZ
     */
    public void setZ(int value) {
        this.zValue = value;
	notifyChanged();
    }

    /**
     *  Makes a clone of this crystal.
     *
     * @return The cloned crystal.
     */
    public Object clone() {
        Crystal clone = null;
        try {
            clone = (Crystal)super.clone();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
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
        StringBuffer resultString = new StringBuffer();
        resultString.append("Crystal{");
        resultString.append("SG=").append(getSpaceGroup()).append(", ");
        resultString.append("Z=").append(getZ()).append(", ");
        resultString.append("a=(").append(aAxis.x).append(", ").append(aAxis.y).append(", ").append(aAxis.z).append("), ");
        resultString.append("b=(").append(bAxis.x).append(", ").append(bAxis.y).append(", ").append(bAxis.z).append("), ");
        resultString.append("c=(").append(cAxis.x).append(", ").append(cAxis.y).append(", ").append(cAxis.z).append("), ");
        resultString.append("#A=").append(getAtomCount()).append("}");
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
