package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.ISetOfReactions;

public class ClearAllEdit extends AbstractUndoableEdit {
	private IChemModel chemModel;
	private ISetOfMolecules som;
	private ISetOfReactions sor;

	public ClearAllEdit(IChemModel chemModel, ISetOfMolecules som, ISetOfReactions sor) {
		this.chemModel = chemModel;
		this.som=som;
		this.sor=sor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
    	if(chemModel.getSetOfMolecules()!=null)
    		chemModel.getSetOfMolecules().removeAllAtomContainers();
    	if(chemModel.getSetOfReactions()!=null)
    		chemModel.getSetOfReactions().removeAllReactions();	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		if(som!=null)
			chemModel.setSetOfMolecules(som);
		if(sor!=null)
			chemModel.setSetOfReactions(sor);
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

}
