package org.openscience.cdk.applications.undoredo;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * An undo adapter for updating the state of the undo components according to
 * the new state of the undo history list Is registered as a listener to the
 * undoSupport which is recieving the undo/redo events.
 * 
 * @author tohel
 * 
 */
public class UndoAdapter implements UndoableEditListener {

	private UndoManager undoManager;

	/**
	 * @param undoManager
	 *            The undoManager handling the undo/redo history list
	 */
	public UndoAdapter(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.UndoableEditListener#undoableEditHappened(javax.swing.event.UndoableEditEvent)
	 */
	public void undoableEditHappened(UndoableEditEvent arg0) {
		UndoableEdit edit = arg0.getEdit();
		undoManager.addEdit(edit);
	}

}
