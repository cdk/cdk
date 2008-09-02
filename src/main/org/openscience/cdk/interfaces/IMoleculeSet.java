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
 */
package org.openscience.cdk.interfaces;

/**
 * Represents a set of Molecules.
 * 
 * @cdk.module  interfaces
 * @cdk.svnrev  $Revision$
 * @author      egonw
 * @cdk.created 2005-08-25
 */
public interface IMoleculeSet extends IAtomContainerSet {

    /**
     * Adds an IMolecule to this container.
     *
     * @param  molecule  The molecule to be added to this container 
     */
    public void addMolecule(IMolecule molecule);
    
    /**
     * Adds all molecules in the MoleculeSet to this container.
     *
     * @param  moleculeSet  The MoleculeSet to add
     */
    public void add(IMoleculeSet moleculeSet);
    
    /**
     * Sets the molecules in the IMoleculeSet, removing previously added
     * IMolecule's.
     * 
     * @param molecules New set of molecules
     * @see             #molecules()
     */
    public void setMolecules(IMolecule[] molecules);
    
    /**
     * Returns the array of Molecules of this container.
     *
     * @return    The array of Molecules of this container 
     * @see       #setMolecules(IMolecule[])
     */
    public Iterable<IAtomContainer> molecules();
    
    /**
     * Returns the Molecule at position <code>number</code> in the
     * container.
     *
     * @param  number  The position of the Molecule to be returned. 
     * @return         The Molecule at position <code>number</code> . 
     */
    public IMolecule getMolecule(int number);
    
    
    /**
     * Returns the number of Molecules in this Container.
     *
     * @return     The number of Molecules in this Container
     */
    public int getMoleculeCount();
	
}

