/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
 * @see  Atom
 */
public class PseudoAtom extends Atom implements java.io.Serializable, Cloneable 
{

    private String label;
    
    /**
     * Constructs an Atom from a String containing an element symbol.
     *
     * @param   label  The String describing the PseudoAtom
     */
    public PseudoAtom(String label) {
        super("R");
        this.label = label;
        this.fractionalPoint3D = null;
        this.point3D = null;
        this.point2D = null;
        // set these default, unchangable values
        super.setHydrogenCount(0);
        super.setStereoParity(-1);
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
        this.fractionalPoint3D = atom.fractionalPoint3D;
        this.point3D = atom.point3D;
        this.point2D = atom.point2D;
        super.setHydrogenCount(atom.getHydrogenCount());
        super.setStereoParity(atom.getStereoParity());
        super.setExactMass(atom.getExactMass());
        super.setFormalCharge(atom.getFormalCharge());
        super.setCharge(atom.getCharge());
    }

    /**
     * Constructs an Atom from an Element and a Point3D.
     *
     * @param   label  The String describing the PseudoAtom
     * @param   point3D         The 3D coordinates of the atom
     */
    public PseudoAtom(String label, javax.vecmath.Point3d point3D) {
        this(label);
        this.point3D = point3D;
    }

    /**
     * Constructs an Atom from an Element and a Point2D.
     *
     * @param   label  The String describing the PseudoAtom
     * @param   point2D         The Point
     */
    public PseudoAtom(String label, javax.vecmath.Point2d point2D) {
        this(label);
        this.point2D = point2D;
    }

    /**
     * Returns the label of this PseudoAtom. 
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label of this PseudoAtom. 
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Dummy method: the exact mass is 0, final. 
     */
    public void setExactMass(double mass) {
        // hydrogen count = 0, always
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
        // formal charge = 0, always
    }
    /**
     * Dummy method: the stereo parity is undefined, final
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





