/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2003  The Chemistry Development Kit (CDK) project
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
 * Base class for entities containing electrons, like bonds, orbitals, lone-pairs.
 *
 * @keyword orbital
 * @keyword lone-pair
 * @keyword bond
 */
public class LonePair extends ElectronContainer implements java.io.Serializable, Cloneable
{

    /** Number of electrons in the lone pair. */
    protected final int electronCount = 2;

    /** The atom with which this lone pair is associated. */
    protected Atom atom;

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
     */
    public LonePair(Atom a) {
        this.atom = a;
    }

    /**
     * Returns the number of electrons in this electron container.
     *
     * @return The number of electrons in this electron container.
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
	public Atom getAtom() {
		return this.atom;
	}

	/**
	 * Sets the associated Atom.
	 *
	 * @param atom the Atom this lone pair will be associated with
     *
     * @see    #getAtom
	 */
	public void setAtom(Atom a) {
		this.atom = a;
	}

    /**
     * Returns true if the given atom participates in this lone pair.
     *
     * @param   atom  The atom to be tested if it participates in this bond
     * @return     true if this lone pair is associated with the atom
     */
    public boolean contains(Atom a)     {
        return (this.atom == a) ? true : false;
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
        s.append("LonePair(");
        s.append(this.hashCode());
        if (atom != null) {
            s.append(", ");
            s.append(atom.toString());
        }
        s.append(")");
        return s.toString();
    }
}


