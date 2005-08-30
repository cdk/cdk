package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ChemModel;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.SetOfMolecules;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * @author tohel
 * 
 */
public class AddAtomsAndBondsEdit extends AbstractUndoableEdit {

	private ChemModel chemModel;

	private AtomContainer undoRedoContainer;

	private AtomContainer container;

	private String type;

	/**
	 * @param chemModel
	 * @param undoRedoContainer
	 */
	public AddAtomsAndBondsEdit(ChemModel chemModel,
			AtomContainer undoRedoContainer, String type) {
		this.chemModel = chemModel;
		this.undoRedoContainer = undoRedoContainer;
		this.container = ChemModelManipulator.getAllInOneContainer(chemModel);
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		for (int i = 0; i < undoRedoContainer.getBondCount(); i++) {
			Bond bond = undoRedoContainer.getBondAt(i);
			container.addBond(bond);
		}
		for (int i = 0; i < undoRedoContainer.getAtomCount(); i++) {
			Atom atom = undoRedoContainer.getAtomAt(i);
			container.addAtom(atom);
		}
		Molecule molecule = new org.openscience.cdk.Molecule(container);
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
		for (int i = 0; i < undoRedoContainer.getBondCount(); i++) {
			Bond bond = undoRedoContainer.getBondAt(i);
			container.removeElectronContainer(bond);
		}
		for (int i = 0; i < undoRedoContainer.getAtomCount(); i++) {
			Atom atom = undoRedoContainer.getAtomAt(i);
			container.removeAtom(atom);
		}
		Molecule molecule = new org.openscience.cdk.Molecule(container);
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
		return type;
	}

}
