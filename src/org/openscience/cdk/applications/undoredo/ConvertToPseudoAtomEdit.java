package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Undo/Redo Edit class for the ConvertToPseudoAtomAction, containing the
 * methods for undoing and redoing the regarding changes
 * 
 * @author tohel
 * 
 */
public class ConvertToPseudoAtomEdit extends AbstractUndoableEdit {

	private Atom atom;

	private PseudoAtom pseudoAtom;

	private AtomContainer container;

	/**
	 * @param relevantContainer
	 *            The container containing the atom been changed
	 * @param atom
	 *            The atom been changed
	 * @param pseudo
	 *            The pseudoAtom
	 */
	public ConvertToPseudoAtomEdit(AtomContainer relevantContainer, Atom atom,
			PseudoAtom pseudo) {
		this.atom = atom;
		this.pseudoAtom = pseudo;
		this.container = relevantContainer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		AtomContainerManipulator.replaceAtomByAtom(container, atom, pseudoAtom);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		AtomContainerManipulator.replaceAtomByAtom(container, pseudoAtom, atom);

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
		return "ConvertToPseudoAtom";
	}

}
