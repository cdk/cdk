/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * @author tohel
 * 
 */
public class AddAtomsAndBondsEdit extends AbstractUndoableEdit {

	private IChemModel chemModel;

	private IAtomContainer undoRedoContainer;

	private IAtomContainer container;

	private String type;

	/**
	 * @param chemModel
	 * @param undoRedoContainer
	 */
	public AddAtomsAndBondsEdit(IChemModel chemModel,
			IAtomContainer undoRedoContainer, String type) {
		this.chemModel = chemModel;
		this.undoRedoContainer = undoRedoContainer;
		this.container = ChemModelManipulator.getAllInOneContainer(chemModel);
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		for (int i = 0; i < undoRedoContainer.getBondCount(); i++) {
			IBond bond = undoRedoContainer.getBondAt(i);
			container.addBond(bond);
		}
		for (int i = 0; i < undoRedoContainer.getAtomCount(); i++) {
			IAtom atom = undoRedoContainer.getAtomAt(i);
			container.addAtom(atom);
		}
		IMolecule molecule = new org.openscience.cdk.Molecule(container);
		ISetOfMolecules moleculeSet = ConnectivityChecker
				.partitionIntoMolecules(molecule);
		chemModel.setSetOfMolecules(moleculeSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		for (int i = 0; i < undoRedoContainer.getBondCount(); i++) {
			IBond bond = undoRedoContainer.getBondAt(i);
			container.removeElectronContainer(bond);
		}
		for (int i = 0; i < undoRedoContainer.getAtomCount(); i++) {
			IAtom atom = undoRedoContainer.getAtomAt(i);
			container.removeAtom(atom);
		}
		IMolecule molecule = new org.openscience.cdk.Molecule(container);
		ISetOfMolecules moleculeSet = ConnectivityChecker
				.partitionIntoMolecules(molecule);
		chemModel.setSetOfMolecules(moleculeSet);
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
