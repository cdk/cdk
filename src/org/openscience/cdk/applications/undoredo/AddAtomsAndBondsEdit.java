/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * @author tohel
 * @cdk.module control
 */
public class AddAtomsAndBondsEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = -7667903450980188402L;

    private IChemModel chemModel;

	private IAtomContainer undoRedoContainer;

	private String type;

	/**
	 * @param chemModel
	 * @param undoRedoContainer
	 */
	public AddAtomsAndBondsEdit(IChemModel chemModel,
			IAtomContainer undoRedoContainer, String type) {
		this.chemModel = chemModel;
		this.undoRedoContainer = undoRedoContainer;
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		IAtomContainer container = ChemModelManipulator.getAllInOneContainer(chemModel);
		for (int i = 0; i < undoRedoContainer.getBondCount(); i++) {
			IBond bond = undoRedoContainer.getBond(i);
			container.addBond(bond);
		}
		for (int i = 0; i < undoRedoContainer.getAtomCount(); i++) {
			IAtom atom = undoRedoContainer.getAtom(i);
			container.addAtom(atom);
		}
		IMolecule molecule = container.getBuilder().newMolecule(container);
		IMoleculeSet moleculeSet = ConnectivityChecker
				.partitionIntoMolecules(molecule);
		chemModel.setMoleculeSet(moleculeSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		IAtomContainer container = ChemModelManipulator.getAllInOneContainer(chemModel);
		for (int i = 0; i < undoRedoContainer.getBondCount(); i++) {
			IBond bond = undoRedoContainer.getBond(i);
			container.removeBond(bond);
		}
		for (int i = 0; i < undoRedoContainer.getAtomCount(); i++) {
			IAtom atom = undoRedoContainer.getAtom(i);
			container.removeAtom(atom);
		}
		IMolecule molecule = container.getBuilder().newMolecule(container);
		IMoleculeSet moleculeSet = ConnectivityChecker
				.partitionIntoMolecules(molecule);
		chemModel.setMoleculeSet(moleculeSet);
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
