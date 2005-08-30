package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.interfaces.ElectronContainer;

/**
 * Undo/Redo Edit class for the ConvertToRadicalAction,containing the methods
 * for undoing and redoing the regarding changes
 * 
 * @author tohel
 * 
 */
public class ConvertToRadicalEdit extends AbstractUndoableEdit {

	private AtomContainer container;

	private ElectronContainer electronContainer;

	/**
	 * @param relevantContainer -
	 *            The container the changes were made
	 * @param electronContainer -
	 *            AtomContainer containing the SingleElectron
	 */
	public ConvertToRadicalEdit(AtomContainer relevantContainer,
			ElectronContainer electronContainer) {
		this.container = relevantContainer;
		this.electronContainer = electronContainer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		container.addElectronContainer(electronContainer);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		container.removeElectronContainer(electronContainer);
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
		return "ConvertToRadical";
	}
}
