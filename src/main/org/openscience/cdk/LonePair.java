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
 * 
 */
package org.openscience.cdk;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.ILonePair;

import java.io.Serializable;

/**
 * A LonePair is an orbital primarily located with one Atom, containing
 * two electrons.
 *
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword orbital
 * @cdk.keyword lone-pair
 * @cdk.keyword bond
 */
public class LonePair extends ElectronContainer implements Serializable, ILonePair, Cloneable
{

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 51311422004885329L;

	/** Number of electrons in the lone pair. */
    protected final int electronCount = 2;

    /** The atom with which this lone pair is associated. */
    protected IAtom atom;

    /**
     * Constructs an unconnected lone pair.
     *
     */
    public LonePair() {
        this.atom = null;
    }

    /**
     * Constructs an lone pair on an Atom.
     *
     * @param atom  Atom to which this lone pair is connected
     */
    public LonePair(IAtom atom) {
        this.atom = atom;
    }

    /**
     * Returns the number of electrons in a LonePair.
     *
     * @return The number of electrons in a LonePair.
     */
    public Integer getElectronCount() {
        return this.electronCount;
    }

    /**
     * Returns the associated Atom.
     *
     * @return the associated Atom.
     *
     * @see    #setAtom
	 */
	public IAtom getAtom() {
		return this.atom;
	}

	/**
	 * Sets the associated Atom.
	 *
	 * @param atom the Atom this lone pair will be associated with
     *
     * @see    #getAtom
	 */
	public void setAtom(IAtom atom) {
		this.atom = atom;
		notifyChanged();
	}

    /**
     * Returns true if the given atom participates in this lone pair.
     *
     * @param   atom  The atom to be tested if it participates in this bond
     * @return     true if this lone pair is associated with the atom
     */
    public boolean contains(IAtom atom)     {
        return (this.atom == atom);
    }

	/**
	 * Clones this LonePair object, including a clone of the atom for which the
     * lone pair is defined.
	 *
	 * @return    The cloned object
	 */
	public Object clone() throws CloneNotSupportedException {
		LonePair clone = (LonePair) super.clone();
        // clone the Atom
        if (atom != null) {
		    clone.atom = (IAtom)((IAtom)atom).clone(); 
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
            resultString.append(", ").append(atom.toString());
        }
        resultString.append(')');
        return resultString.toString();
    }
}


