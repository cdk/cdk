/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk;

/**
 * A LonePair is an orbital primarily located with one Atom, containing
 * two electrons.
 *
 * @cdk.module data
 *
 * @cdk.keyword orbital
 * @cdk.keyword lone-pair
 * @cdk.keyword bond
 */
public class LonePair extends ElectronContainer implements java.io.Serializable, org.openscience.cdk.interfaces.LonePair
{

    /** Number of electrons in the lone pair. */
    protected final int electronCount = 2;

    /** The atom with which this lone pair is associated. */
    protected org.openscience.cdk.interfaces.Atom atom;

    /**
     * Constructs an unconnected lone pair.
     *
     */
    public LonePair() {
        atom = null;
    }

    /**
     * Constructs an lone pair on an Atom.
     *
     * @param atom  Atom to which this lone pair is connected
     */
    public LonePair(org.openscience.cdk.interfaces.Atom atom) {
        this.atom = atom;
    }

    /**
     * Returns the number of electrons in a LonePair.
     *
     * @return The number of electrons in a LonePair.
     */
    public int getElectronCount() {
        return this.electronCount;
    }

    /**
     * Returns the associated Atom.
     *
     * @return the associated Atom.
     *
     * @see    #setAtom
	 */
	public org.openscience.cdk.interfaces.Atom getAtom() {
		return (Atom)this.atom;
	}

	/**
	 * Sets the associated Atom.
	 *
	 * @param atom the Atom this lone pair will be associated with
     *
     * @see    #getAtom
	 */
	public void setAtom(org.openscience.cdk.interfaces.Atom atom) {
		this.atom = atom;
		notifyChanged();
	}

    /**
     * Returns true if the given atom participates in this lone pair.
     *
     * @param   atom  The atom to be tested if it participates in this bond
     * @return     true if this lone pair is associated with the atom
     */
    public boolean contains(org.openscience.cdk.interfaces.Atom atom)     {
        return (this.atom == atom) ? true : false;
    }

	/**
	 * Clones this LonePair object, including a clone of the atom for which the
     * lone pair is defined.
	 *
	 * @return    The cloned object
	 */
	public Object clone() {
		LonePair clone = null;
		try {
			clone = (LonePair) super.clone();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
        // clone the Atom
        if (atom != null) {
		    clone.atom = (Atom)((Atom)atom).clone(); 
        }
		return clone;
	}

    /**
     * Returns a one line string representation of this LonePair.
     * This method is conform RFC #9.
     *
     * @return    The string representation of this LonePair
     */
    public String toString() {
        StringBuffer resultString = new StringBuffer();
        resultString.append("LonePair(");
        resultString.append(this.hashCode());
        if (atom != null) {
            resultString.append(", ");
            resultString.append(atom.toString());
        }
        resultString.append(")");
        return resultString.toString();
    }
}


