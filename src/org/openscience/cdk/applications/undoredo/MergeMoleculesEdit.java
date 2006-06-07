package org.openscience.cdk.applications.undoredo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

public class MergeMoleculesEdit  extends AbstractUndoableEdit{
	
    private static final long serialVersionUID = -4093867960954400453L;
    
    private IChemModel chemModel;
	private String type;
	private ArrayList undoredoContainer;


	public MergeMoleculesEdit(IChemModel chemModel, ArrayList undoredoContainer, String type) {
		this.chemModel = chemModel;
		this.undoredoContainer = undoredoContainer;
		this.type = type;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		IAtomContainer container = ChemModelManipulator.getAllInOneContainer(chemModel);
		for (int objects=0; objects<undoredoContainer.size(); objects++) {
			Object[] undoObjects = (Object[]) undoredoContainer.get(objects);
			IAtom atom1 = (IAtom) undoObjects[0];
			IAtom atom2 = (IAtom) undoObjects[1];
			IBond[] bonds = (IBond[]) undoObjects[2];
			
			for(int i=0;i<bonds.length;i++){
				if(bonds[i].getAtomAt(0)==atom2)
					bonds[i].setAtomAt(atom1,0);
				if(bonds[i].getAtomAt(1)==atom2)
					bonds[i].setAtomAt(atom1,1);
				if(bonds[i].getAtomAt(0)==bonds[i].getAtomAt(1)){
					container.removeElectronContainer(bonds[i]);
				}
			}
			container.removeAtom(atom2);
		}
//		Molecule molecule = new org.openscience.cdk.Molecule(container);
//		SetOfMolecules moleculeSet = ConnectivityChecker
//				.partitionIntoMolecules(molecule);
//		chemModel.setSetOfMolecules(moleculeSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		IAtomContainer container = ChemModelManipulator.getAllInOneContainer(chemModel);
		for (int objects=0; objects<undoredoContainer.size(); objects++) {
			Object[] undoObjects = (Object[]) undoredoContainer.get(objects);
			IAtom atom1 = (IAtom) undoObjects[0];
			IAtom atom2 = (IAtom) undoObjects[1];
			IBond[] bonds = (IBond[]) undoObjects[2];
			
			container.addAtom(atom2);
			for(int i=0;i<bonds.length;i++){
				if(bonds[i].getAtomAt(0)==atom1)
					bonds[i].setAtomAt(atom2,0);
				if(bonds[i].getAtomAt(1)==atom1)
					bonds[i].setAtomAt(atom2,1);
				if(bonds[i].getAtomAt(0)==bonds[i].getAtomAt(1)){
					container.removeElectronContainer(bonds[i]);
				}
			}
		}
//		Molecule molecule = new org.openscience.cdk.Molecule(container);
//		SetOfMolecules moleculeSet = ConnectivityChecker
//				.partitionIntoMolecules(molecule);
//		chemModel.setSetOfMolecules(moleculeSet);
		
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
