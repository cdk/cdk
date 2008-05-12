package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;

/**
 * @cdk.module controlold
 * @cdk.svnrev  $Revision$
 */
public class ClearAllEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = -9022673628051651034L;
    
    private IChemModel chemModel;
	private IMoleculeSet som;
	private IReactionSet sor;

	public ClearAllEdit(IChemModel chemModel, IMoleculeSet som, IReactionSet sor) {
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
    	if(chemModel.getMoleculeSet()!=null)
    		chemModel.getMoleculeSet().removeAllAtomContainers();
    	if(chemModel.getReactionSet()!=null)
    		chemModel.getReactionSet().removeAllReactions();	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		if(som!=null)
			chemModel.setMoleculeSet(som);
		if(sor!=null)
			chemModel.setReactionSet(sor);
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
