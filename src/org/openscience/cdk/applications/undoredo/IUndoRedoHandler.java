package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.UndoableEdit;

public interface IUndoRedoHandler {
	
	public void postEdit(UndoableEdit edit);

}
