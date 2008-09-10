/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk;

import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPseudoAtom;

/**
 * Represents the idea of a non-chemical atom-like entity, like Me,
 * R, X, Phe, His, etc.
 *
 * <p>This should be replaced by the mechanism explained in RFC #8.
 *
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 *
 * @see  Atom
 */
public class PseudoAtom extends Atom 
  implements java.io.Serializable, Cloneable, IPseudoAtom 
{

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 1L;

	private String label;
    
    /**
     * Constructs an empty PseudoAtom.
     */
    public PseudoAtom() {
        this("*");
    }
    
    /**
     * Constructs an Atom from a String containing an element symbol.
     *
     * @param   label  The String describing the PseudoAtom
     */
    public PseudoAtom(String label) {
        super("R");
        this.label = label;
        super.fractionalPoint3d = null;
        super.point3d = null;
        super.point2d = null;
        // set these default, unchangeable values
        super.hydrogenCount = 0;
        super.stereoParity = 0;
        super.exactMass = 0.0;
        super.formalCharge = 0;
        super.charge = 0.0;
    }

    /**
     * Constructs an PseudoAtom from a IAtom.
     *
     * @param   element  IAtom from which the PseudoAtom is constructed
     */
    public PseudoAtom(IElement element) {
        super(element);
        if (element instanceof IPseudoAtom) {
            this.label = ((IPseudoAtom)element).getLabel();   	
        } else {
        	super.symbol = "R";
        	this.label = element.getSymbol();
        }
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
    public void setExactMass(Double mass) {
        // exact mass = 0, always
    }

    /**
     * Dummy method: the hydrogen count is 0, final. 
     */
    public void setHydrogenCount(Integer hydrogenCount) {
        // hydrogen count = 0, always
    }

    /**
     * Dummy method: the formal charge is 0, final. 
     */
    public void setFormalCharge(Integer charge) {
        // formal charge = 0, always
    }

    /**
     * Dummy method: the partial charge is 0, final. 
     */
    public void setCharge(Double charge) {
        // partial charge = 0, always
    }
    /**
     * Dummy method: the stereo parity is undefined, final.
     */
    public void setStereoParity(Integer stereoParity) {
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
        description.append(this.hashCode());
        if (getLabel() != null) {
        	description.append(", ").append(getLabel());
        }
        description.append(", ").append(super.toString());
        description.append(')');
        return description.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}





