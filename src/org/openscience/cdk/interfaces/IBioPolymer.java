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
import java.util.Map;

/**
 * A BioPolymer is a subclass of a Polymer which is supposed to store
 * additional informations about the Polymer which are connected to BioPolymers.
 *
 * @cdk.module  interfaces
 * @cdk.svnrev  $Revision$
 *
 * @author      Edgar Luttmann <edgar@uni-paderborn.de>
 * @cdk.created 2001-08-06 
 *
 * @cdk.keyword polymer
 * @cdk.keyword biopolymer
 */
public interface IBioPolymer extends IPolymer {

	/**
	 * Adds the atom oAtom without specifying a Monomer or a Strand. Therefore the
	 * atom to this AtomContainer, but not to a certain Strand or Monomer (intended
	 * e.g. for HETATMs).
	 *
	 * @param oAtom  The atom to add
	 */
	public void addAtom(IAtom oAtom);
	
	/**
	 * Adds the atom oAtom to a specified Strand, whereas the Monomer is unspecified. Hence
	 * the atom will be added to a Monomer of type UNKNOWN in the specified Strand.
	 *
	 * @param oAtom   The atom to add
	 * @param oStrand The strand the atom belongs to
	 */
	public void addAtom(IAtom oAtom, IStrand oStrand);
	
	/**
	 * Adds the atom to a specified Strand and a specified Monomer.
	 * 
	 * @param oAtom    The atom to add
	 * @param oMonomer The monomer the atom belongs to
	 * @param oStrand  The strand the atom belongs to
	 */
	public void addAtom(IAtom oAtom, IMonomer oMonomer, IStrand oStrand);
	
	/**
	 * Return the number of monomers present in BioPolymer.
	 *
	 * @return number of monomers
	 */
	public int getMonomerCount();
	
	/**
	 * Retrieve a <code>Monomer</code> object by specifying its name.
	 * 
	 * <p>You have to specify the strand to enable
	 * monomers with the same name in different strands. There is at least one such case: every
	 * strand contains a monomer called "".
	 *
	 * @param  monName    The name of the monomer to look for
	 * @param  strandName The name of the strand to look for
	 * @return            The Monomer object which was asked for
	 */
	public IMonomer getMonomer(String monName, String strandName);
		
	/**
	 * Returns a collection of the names of all <code>Monomer</code>s in this
	 * BioPolymer.
	 *
	 * @return a <code>Collection</code> of all the monomer names.
	 */
	public Collection<String> getMonomerNames();
	
	/**
	 * Return the number of strands present in the BioPolymer.
	 *
	 * @return number of strands
	 */
	public int getStrandCount();
	
	/**
	 * Retrieve a Monomer object by specifying its name.
	 *
	 * @param cName  The name of the monomer to look for
	 * @return The Monomer object which was asked for
	 */
	public IStrand getStrand(String cName);
	
	/**
	 * Returns a collection of the names of all <code>Strand</code>s in this
	 * BioPolymer.
	 *
	 * @return a <code>Collection</code> of all the strand names.
	 */
	public Collection<String> getStrandNames();
	
	/**
	 * Removes a particular strand, specified by its name.
	 * 
	 * @param name  The name of the strand to remove
	 */
	public void removeStrand(String name);
	
	/**
	 * Returns a Map containing the strands in the Polymer.
	 * 
	 * @return hashtable containing the strands in the Polymer
	 */
	public Map<String,IStrand> getStrands();
	
}
