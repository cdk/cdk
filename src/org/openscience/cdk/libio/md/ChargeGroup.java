package org.openscience.cdk.libio.md;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * A ChargeGroup (CG) is a numbered collection of atoms in an MDMolecule.
 * 
 * A CG is a small set of atoms with total zero or Integer charge.
 * 
 * @author ola
 *
 */
public class ChargeGroup extends AtomContainer{
	private int number;
	private MDMolecule parentMolecule;
	private IAtom switchingAtom;
	
	/**
	 * Constructor to create a ChargeGroup based on an AC, a number, and a MDMolecule
	 * @param container
	 * @param number
	 * @param molecule
	 */
	public ChargeGroup(IAtomContainer container, int number, MDMolecule parentMolecule) {
		super(container);
		this.number=number;
		this.parentMolecule=parentMolecule;
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


	public IAtom getSwitchingAtom() {
		return switchingAtom;
	}


	public void setSwitchingAtom(IAtom switchingAtom) {
		this.switchingAtom = switchingAtom;
	}

}
