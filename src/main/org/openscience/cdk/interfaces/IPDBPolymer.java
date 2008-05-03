/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 *  Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
 * A PDBPolymer is a subclass of a BioPolymer which is supposed to store
 * additional informations about the BioPolymer which are connected to BioPolymers.
 *
 * @cdk.module  interfaces
 * @cdk.svnrev  $Revision$
 *
 * @author      Miguel Rojas <miguel.rojas@uni-koeln.de>
 * @cdk.created 2006-11-20 
 *
 * @cdk.keyword polymer
 * @cdk.keyword biopolymer
 * @cdk.keyword pdbpolymer
 */
public interface IPDBPolymer extends IBioPolymer {
	/**
	 * Adds the atom oAtom without specifying a Monomer or a Strand. Therefore the
	 * atom to this AtomContainer, but not to a certain Strand or Monomer (intended
	 * e.g. for HETATMs).
	 *
	 * @param oAtom  The atom to add
	 */
	public void addAtom(IPDBAtom oAtom);
	
	/**
	 * Adds the atom to a specified Strand and a specified Monomer.
	 * 
	 * @param oAtom    The atom to add
	 * @param oMonomer The monomer the atom belongs to
	 * @param oStrand  The strand the atom belongs to
	 */
	public void addAtom(IPDBAtom oAtom, IMonomer oMonomer, IStrand oStrand);
	
	/**
	 * Adds the PDBStructure structure a this PDBPolymer.
	 *
	 * @param structure  The PDBStructure to add
	 */
	public void addStructure(IPDBStructure structure);
	/**
	 * Returns a Collection containing the PDBStructure in the PDBPolymer.
	 * 
	 * @return Collection containing the PDBStructure in the PDBPolymer
	 */
	public Collection<IPDBStructure> getStructures();
	
}
