package org.openscience.cdk.libio.md;

import java.util.ArrayList;
import java.util.HashMap;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;

public class MDMolecule extends Molecule{

	//List of Residues
   	private ArrayList<Residue> residues;

   	//List of ChargeGroups
   	private ArrayList<ChargeGroup> chargeGroups;
	
	public MDMolecule() {
		super();
	}

	public MDMolecule(IAtomContainer container) {
		super(container);
	}

	public ArrayList<Residue> getResidues() {
		return residues;
	}

	public void setResidues(ArrayList<Residue> residues) {
		this.residues = residues;
	}

	/**
	 * Add a Residue to the MDMolecule if not already present
	 * @param residue Residue to add
	 */
	public void addResidue(Residue residue){
		if (residues==null) residues=new ArrayList<Residue>();

		//Check if exists
		if (residues.contains(residue)){
			System.out.println("Residue: " + residue.getName() + " already present in molecule: " + getID());
			return;
		}
		
		residues.add(residue);
	}

	
	public ArrayList<ChargeGroup> getChargeGroups() {
		return chargeGroups;
	}

	public void setChargeGroups(ArrayList<ChargeGroup> chargeGroups) {
		this.chargeGroups = chargeGroups;
	}

	/**
	 * Add a ChargeGroup to the MDMolecule if not already present
	 * @param residue Residue to add
	 */
	public void addChargeGroup(ChargeGroup chargeGroup){
		if (chargeGroups==null) chargeGroups=new ArrayList<ChargeGroup>();

		//Check if exists
		if (chargeGroups.contains(chargeGroup)){
			System.out.println("Charge group: " + chargeGroup.getNumber() + " already present in molecule: " + getID());
			return;
		}
		
		chargeGroups.add(chargeGroup);
	}
	
}
