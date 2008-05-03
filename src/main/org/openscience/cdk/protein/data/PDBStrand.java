/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2001-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.protein.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openscience.cdk.Strand;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMonomer;

/**
 * An entry in the PDB database. It is not just a regular protein, but the
 * regular PDB mix of protein or protein complexes, ligands, water molecules
 * and other species.
 *
 * @cdk.module  pdb
 * @cdk.svnrev  $Revision$
 *
 * @author      Egon Willighagen
 * @cdk.created 2006-04-19
 * @cdk.keyword polymer
 */
public class PDBStrand extends Strand { 

	private static final long serialVersionUID = 8278569309787734236L;

	List sequentialListOfMonomers;
	
	/**
	 * Contructs a new Polymer to store the Monomers.
	 */	
	public PDBStrand() {
		super();
		sequentialListOfMonomers = new ArrayList();
	}
	
	/**
	 * Adds the atom oAtom to a specified Monomer. Additionally, it keeps
	 * record of the iCode.
	 *
	 * @param oAtom  The atom to add
	 * @param oMonomer  The monomer the atom belongs to
	 */
	public void addAtom(IAtom oAtom, IMonomer oMonomer) {
		super.addAtom(oAtom, oMonomer);
		if (!sequentialListOfMonomers.contains(oMonomer.getMonomerName()))
			sequentialListOfMonomers.add(oMonomer.getMonomerName());
	}

	/**
	 * Returns the monomer names in the order in which they were added.
	 * 
	 * @see org.openscience.cdk.interfaces.IPolymer#getMonomerNames()
	 */
	public Collection getMonomerNamesInSequentialOrder() {
		// don't return the original
		return new ArrayList(sequentialListOfMonomers);
	}
	
	public String toString() {
        StringBuffer stringContent = new StringBuffer();
        stringContent.append("PDBPolymer(");
        stringContent.append(this.hashCode()).append(", ");
        stringContent.append(super.toString());
        stringContent.append(")");
        return stringContent.toString();
    }

}
