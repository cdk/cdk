package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.Atom;

/**
 * Undo/Redo Edit class for the ChangeIsotopeAction, containing the methods for
 * undoing and redoing the regarding changes
 * 
 * @author tohel
 * 
 */
public class ChangeIsotopeEdit extends AbstractUndoableEdit {

	private Atom atom;

	private int formerIsotopeNumber;

	private int isotopeNumber;

	/**
	 * @param atom
	 *            The atom been changed
	 * @param formerIsotopeNumber
	 *            The former mass number
	 * @param isotopeNumber
	 *            The new mass number
	 */
	public ChangeIsotopeEdit(Atom atom, int formerIsotopeNumber,
			int isotopeNumber) {
		this.atom = atom;
		this.formerIsotopeNumber = formerIsotopeNumber;
		this.isotopeNumber = isotopeNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		this.atom.setMassNumber(isotopeNumber);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		this.atom.setMassNumber(formerIsotopeNumber);

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
		return "ChangeIsotope";
	}
}
