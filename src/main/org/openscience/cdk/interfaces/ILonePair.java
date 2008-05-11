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
 * 
 */
package org.openscience.cdk.interfaces;

/**
 * A LonePair is an orbital primarily located with one Atom, containing
 * two electrons.
 *
 * @cdk.module interfaces
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword orbital
 * @cdk.keyword lone-pair
 * @cdk.keyword bond
 */
public interface ILonePair extends IElectronContainer {

    /**
     * Returns the associated Atom.
     *
     * @return the associated Atom.
     * @see    #setAtom
	 */
	public IAtom getAtom();

	/**
	 * Sets the associated Atom.
	 *
	 * @param atom the Atom this lone pair will be associated with
     * @see    #getAtom
	 */
	public void setAtom(IAtom atom);

    /**
     * Returns true if the given atom participates in this lone pair.
     *
     * @param   atom  The atom to be tested if it participates in this bond
     * @return     true if this lone pair is associated with the atom
     */
    public boolean contains(IAtom atom);

}


