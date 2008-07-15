/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2001-2008  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.BioPolymer;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.interfaces.IPDBPolymer;
import org.openscience.cdk.interfaces.IPDBStructure;
import org.openscience.cdk.interfaces.IStrand;

/**
 * An entry in the PDB database. It is not just a regular protein, but the
 * regular PDB mix of protein or protein complexes, ligands, water molecules
 * and other species.
 *
 * @cdk.module  data
 * @cdk.svnrev  $Revision$
 *
 * @author      Egon Willighagen
 * @cdk.created 2006-04-19
 * @cdk.keyword polymer
 */
public class PDBPolymer extends BioPolymer implements Cloneable, IPDBPolymer{ 

	private static final long serialVersionUID = 4173552834313952358L;

	List<String> sequentialListOfMonomers;
	List<IPDBStructure> secondaryStructures;
	
	/**
	 * Constructs a new Polymer to store the {@link IMonomer}s.
	 */	
	public PDBPolymer() {
		super();
		sequentialListOfMonomers = new ArrayList<String>();
		secondaryStructures = new ArrayList<IPDBStructure>();
	}
	
	public void addStructure(IPDBStructure structure) {
		secondaryStructures.add(structure);
	}
	
	public Collection<IPDBStructure> getStructures() {
//		don't return the original
		return new ArrayList<IPDBStructure>(secondaryStructures);
	}
	/**
	 * Adds the atom oAtom without specifying a {@link IMonomer} or a Strand. Therefore the
	 * atom to this AtomContainer, but not to a certain Strand or {@link IMonomer} (intended
	 * e.g. for HETATMs).
	 *
	 * @param oAtom  The {@link IPDBAtom} to add
	 */
	public void addAtom(IPDBAtom oAtom){
		super.addAtom(oAtom);
	}

	/**
	 * Adds the atom oAtom to a specified Monomer. Additionally, it keeps
	 * record of the iCode.
	 *
	 * @param oAtom  The IPDBAtom to add
	 * @param oMonomer  The monomer the atom belongs to
	 */
	public void addAtom(IPDBAtom oAtom, IMonomer oMonomer) {
		super.addAtom(oAtom, oMonomer);
		if (!sequentialListOfMonomers.contains(oMonomer.getMonomerName()))
			sequentialListOfMonomers.add(oMonomer.getMonomerName());
	}

	/**
	 * Adds the IPDBAtom oAtom to a specified Monomer of a specified Strand.
	 * Additionally, it keeps record of the iCode.
	 *
	 * @param oAtom  The IPDBAtom to add
	 * @param oMonomer  The monomer the atom belongs to
	 */
	public void addAtom(IPDBAtom oAtom, IMonomer oMonomer, IStrand oStrand) {
		super.addAtom(oAtom, oMonomer, oStrand);
		if (!sequentialListOfMonomers.contains(oMonomer.getMonomerName()))
			sequentialListOfMonomers.add(oMonomer.getMonomerName());
	}
	
	/**
	 * Returns the monomer names in the order in which they were added.
	 * 
	 * @see org.openscience.cdk.interfaces.IPolymer#getMonomerNames()
	 */
	public Collection<String> getMonomerNamesInSequentialOrder() {
		// don't return the original
		return new ArrayList<String>(sequentialListOfMonomers);
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
