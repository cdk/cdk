package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * @cdk.module control
 * @cdk.svnrev  $Revision: 9162 $
 */
public class AddFuncGroupEdit  extends AbstractUndoableEdit{
	
    private static final long serialVersionUID = -4093867960954400453L;
    
    private IChemModel chemModel;
	private String type;
	private IAtomContainer oldatom;
	private IAtomContainer addedGroup;


	public AddFuncGroupEdit(IChemModel chemModel, IAtomContainer oldatom, IAtomContainer addedGroup, String type) {
		this.chemModel = chemModel;
		this.oldatom=oldatom;
		this.addedGroup=addedGroup;
		this.type = type;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, oldatom.getAtom(0));
		container.add(addedGroup);
		container.remove(oldatom);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, addedGroup.getAtom(0));
		System.err.println("con "+container);
		System.err.println("ac "+addedGroup);
		for (int objects=0; objects<addedGroup.getAtomCount(); objects++) {
			container.removeAtomAndConnectedElectronContainers(addedGroup.getAtom(objects));
		}
		container.add(oldatom);
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