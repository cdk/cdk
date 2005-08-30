package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

public class BondChangeEdit extends AbstractUndoableEdit {

	private AtomContainer container;

	private Bond newBond;

	private Bond formerBond;

	private Atom[] atoms;

	private ChemModel chemModel;

	public BondChangeEdit(ChemModel chemModel, Bond formerBond, Bond newBond) {
		this.chemModel = chemModel;
		this.formerBond = formerBond;
		this.newBond = newBond;
		atoms = (Atom[]) newBond.getAtoms();
		if (formerBond != null) {
			formerBond.setAtoms(atoms);
		}
		container = ChemModelManipulator.getAllInOneContainer(chemModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		container.removeElectronContainer(formerBond);
		container.addBond(newBond);
		Molecule molecule = new Molecule(container);
		SetOfMolecules moleculeSet = ConnectivityChecker
				.partitionIntoMolecules(molecule);
		chemModel.setSetOfMolecules(moleculeSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		container.removeElectronContainer(newBond);
		container.addBond(formerBond);
		Molecule molecule = new Molecule(container);
		SetOfMolecules moleculeSet = ConnectivityChecker
				.partitionIntoMolecules(molecule);
		chemModel.setSetOfMolecules(moleculeSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#canRedo()
	 */
	public boolean canRedo() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#canUndo()
	 */
	public boolean canUndo() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#getPresentationName()
	 */
	public String getPresentationName() {
		return "Change Bond";
	}
}
