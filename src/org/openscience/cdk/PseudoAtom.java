/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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

/**
 * Represents the idea of a non-chemical atom-like entity, like Me,
 * R, X, Phe, His, etc.
 *
 * <p>This should be replaced by the mechanism explained in RFC #8.
 *
 * @cdk.module core
 *
 * @see  Atom
 */
public class PseudoAtom extends Atom implements java.io.Serializable, Cloneable 
{

    private String label;
    
    /**
     * Constructs an empty PseudoAtom.
     */
    public PseudoAtom() {
        this("");
    }
    
    /**
     * Constructs an Atom from a String containing an element symbol.
     *
     * @param   label  The String describing the PseudoAtom
     */
    public PseudoAtom(String label) {
        super("R");
        this.label = label;
        this.fractionalPoint3d = null;
        this.point3d = null;
        this.point2d = null;
        // set these default, unchangable values
        super.setHydrogenCount(0);
        super.setStereoParity(0);
        super.setExactMass(0.0);
        super.setFormalCharge(0);
        super.setCharge(0.0);
    }

    /**
     * Constructs an Atom from a String containing an element symbol.
     *
     * @param   atom  Atom from which the PseudoAtom is constructed
     */
    public PseudoAtom(Atom atom) {
        super("R");
        super.setProperties(atom.getProperties());
        this.fractionalPoint3d = atom.fractionalPoint3d;
        this.point3d = atom.point3d;
        this.point2d = atom.point2d;
        this.label = atom.getSymbol();
        super.setHydrogenCount(atom.getHydrogenCount());
        super.setStereoParity(atom.getStereoParity());
        super.setExactMass(atom.getExactMass());
        super.setFormalCharge(atom.getFormalCharge());
        super.setCharge(atom.getCharge());
    }

    /**
     * Constructs an Atom from an Element and a Point3d.
     *
     * @param   label  The String describing the PseudoAtom
     * @param   point3d         The 3D coordinates of the atom
     */
    public PseudoAtom(String label, javax.vecmath.Point3d point3d) {
        this(label);
        this.point3d = point3d;
    }

    /**
     * Constructs an Atom from an Element and a Point2d.
     *
     * @param   label  The String describing the PseudoAtom
     * @param   point2d         The Point
     */
    public PseudoAtom(String label, javax.vecmath.Point2d point2d) {
        this(label);
        this.point2d = point2d;
    }

    /**
     * Returns the label of this PseudoAtom.
     *
     * @return The label for this PseudoAtom
     * @see    #setLabel
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label of this PseudoAtom.
     *
     * @param label The new label for this PseudoAtom
     * @see   #getLabel
     */
    public void setLabel(String label) {
        this.label = label;
	notifyChanged();
    }

    /**
     * Dummy method: the exact mass is 0, final. 
     */
    public void setExactMass(double mass) {
        // exact mass = 0, always
    }

    /**
     * Dummy method: the hydrogen count is 0, final. 
     */
    public void setHydrogenCount(int hydrogenCount) {
        // hydrogen count = 0, always
    }

    /**
     * Dummy method: the formal charge is 0, final. 
     */
    public void setFormalCharge(int charge) {
        // formal charge = 0, always
    }

    /**
     * Dummy method: the partial charge is 0, final. 
     */
    public void setCharge(double charge) {
        // partial charge = 0, always
    }
    /**
     * Dummy method: the stereo parity is undefined, final.
     */
    public void setStereoParity(int stereoParity) {
        // this is undefined, always
    }

    /**
     * Returns a one line string representation of this Atom.
     * Methods is conform RFC #9.
     *
     * @return  The string representation of this Atom
     */
    public String toString() {
        StringBuffer description = new StringBuffer();
        description.append("PseudoAtom(");
        description.append(this.hashCode() + ", ");
        description.append(getLabel() + ", ");
        description.append(super.toString());
        description.append(")");
        return description.toString();
    }

}





