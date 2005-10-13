package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.Atom;

/**
 * Undo/Redo Edit class for the ChangeCharge actions in AbstarctController2D,
 * containing the methods for undoing and redoing the regarding changes
 * 
 * @author tohel
 * 
 */
public class ChangeChargeEdit extends AbstractUndoableEdit {

	private Atom atom;

	private int formerCharge;

	private int newCharge;

	/**
	 * @param atomInRange
	 *            The atom been changed
	 * @param formerCharge
	 *            The former charge of this atom
	 * @param newCharge
	 *            The new charge of this atom
	 */
	public ChangeChargeEdit(Atom atomInRange, int formerCharge,
			int newCharge) {
		this.atom = atomInRange;
		this.formerCharge = formerCharge;
		this.newCharge = newCharge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		this.atom.setFormalCharge(newCharge);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		this.atom.setFormalCharge(formerCharge);
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
		return "ChangeCharge";
	}
}
