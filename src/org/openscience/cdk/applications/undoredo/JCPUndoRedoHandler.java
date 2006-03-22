package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.UndoableEdit;

import org.openscience.cdk.controller.Controller2DModel;

public class JCPUndoRedoHandler implements IUndoRedoHandler {
	Controller2DModel c2dm=null;

	public void postEdit(UndoableEdit edit) {
		c2dm.getUndoSupport().postEdit(edit);
	}

	public void setC2dm(Controller2DModel c2dm) {
		this.c2dm = c2dm;
	}

}
