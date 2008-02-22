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
 *  */
package org.openscience.cdk.interfaces;

import java.util.Collection;

/**
 * Subclass of Molecule to store Polymer specific attributes that a Polymer has.
 *
 * @cdk.module  interfaces
 * @cdk.svnrev  $Revision$
 *
 * @author      Edgar Luttmann <edgar@uni-paderborn.de>
 * @author      Martin Eklund <martin.eklund@farmbio.uu.se>
 * @cdk.created 2001-08-06
 * @cdk.keyword polymer
 */
public interface IPolymer extends IMolecule {
	
	/**
	 * Adds the atom oAtom without specifying a Monomer. Therefore the
	 * atom to this AtomContainer, but not to a certain Monomer (intended
	 * e.g. for HETATMs).
	 *
	 * @param oAtom  The atom to add
	 */
	public void addAtom(IAtom oAtom);
	
	/**
	 * Adds the atom oAtom to a specified Monomer.
	 *
	 * @param oAtom  The atom to add
	 * @param oMonomer  The monomer the atom belongs to
	 */
	public void addAtom(IAtom oAtom, IMonomer oMonomer);
	
	/**
	 * Return the number of monomers present in the Polymer.
	 *
	 * @return number of monomers
	 */
	public int getMonomerCount();
	
	/**
	 * Retrieve a Monomer object by specifying its name.
	 *
	 * @param cName  The name of the monomer to look for
	 * @return The Monomer object which was asked for
	 */
	public IMonomer getMonomer(String cName);
	
	/**
	 * Returns a collection of the names of all <code>Monomer</code>s in this
	 * polymer.
	 *
	 * @return a <code>Collection</code> of all the monomer names.
	 */
	public Collection<String> getMonomerNames();
	
	/**
	 * Removes a particular monomer, specified by its name.
	 * 
	 * @param name The name of the monomer to be removed
	 */
	public void removeMonomer(String name);

}
