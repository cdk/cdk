/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
public class LonePair extends ElectronContainer {

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
     *
     * @see     #setElectronCount
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
    public boolean contains(Atom atom)     {
        return (atom == atom) ? true : false;
    }
}


