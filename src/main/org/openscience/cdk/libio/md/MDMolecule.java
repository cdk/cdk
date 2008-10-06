/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2007  Ola Spjuth <ospjuth@users.sf.net>
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
 */
package org.openscience.cdk.libio.md;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @cdk.module libiomd
 * @cdk.svnrev  $Revision$
 */
public class MDMolecule extends Molecule{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3129626782945020908L;

	//List of Residues
   	private List residues;

   	//List of ChargeGroups
   	private List chargeGroups;
	
	public MDMolecule() {
		super();
	}

	public MDMolecule(IAtomContainer container) {
		super(container);
	}

	public List getResidues() {
		return residues;
	}

	public void setResidues(List residues) {
		this.residues = residues;
	}

	/**
	 * Add a Residue to the MDMolecule if not already present
	 * @param residue Residue to add
	 */
	public void addResidue(Residue residue){
		if (residues==null) residues=new ArrayList();

		//Check if exists
		if (residues.contains(residue)){
			System.out.println("Residue: " + residue.getName() + " already present in molecule: " + getID());
			return;
		}
		
		residues.add(residue);
	}

	
	public List getChargeGroups() {
		return chargeGroups;
	}

	public void setChargeGroups(List chargeGroups) {
		this.chargeGroups = chargeGroups;
	}

	/**
	 * Add a ChargeGroup to the MDMolecule if not already present
	 * 
	 * @param chargeGroup {@link ChargeGroup} to add
	 */
	public void addChargeGroup(ChargeGroup chargeGroup){
		if (chargeGroups==null) chargeGroups=new ArrayList();

		//Check if exists
		if (chargeGroups.contains(chargeGroup)){
			System.out.println("Charge group: " + chargeGroup.getNumber() + " already present in molecule: " + getID());
			return;
		}
		
		chargeGroups.add(chargeGroup);
	}
	
}
