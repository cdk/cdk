package org.openscience.cdk.libio.md;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * A residue is a named, numbered collection of atoms in an MDMolecule.
 * 
 * Residues are used to partition molecules in distinct pieces.
 * 
 * @author ola
 *
 */
public class Residue extends AtomContainer{

	private int number;
	private String name;
	private MDMolecule parentMolecule;
	
	/**
	 * Constructor to create a Residue based on an AC, a number, and a MDMolecule
	 * @param container
	 * @param number
	 * @param molecule
	 */
	public Residue(IAtomContainer container, int number, MDMolecule parentMolecule) {
		super(container);
		this.number=number;
		this.parentMolecule=parentMolecule;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public MDMolecule getParentMolecule() {
		return parentMolecule;
	}

	public void setParentMolecule(MDMolecule parentMolecule) {
		this.parentMolecule = parentMolecule;
	}

}
