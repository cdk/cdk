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
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.SetOfMolecules;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

public class BondChangeEdit extends AbstractUndoableEdit {

	private IAtomContainer container;

	private IBond newBond;

	private IBond formerBond;

	private IAtom[] atoms;

	private IChemModel chemModel;

	public BondChangeEdit(IChemModel chemModel, IBond formerBond, IBond newBond) {
		this.chemModel = chemModel;
		this.formerBond = formerBond;
		this.newBond = newBond;
		atoms = (IAtom[]) newBond.getAtoms();
		if (formerBond != null) {
			formerBond.setAtoms(atoms);
		}
		container = ChemModelManipulator.getAllInOneContainer(chemModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		container.removeElectronContainer(formerBond);
		container.addBond(newBond);
		Molecule molecule = new org.openscience.cdk.Molecule(container);
		SetOfMolecules moleculeSet = ConnectivityChecker
				.partitionIntoMolecules(molecule);
		chemModel.setSetOfMolecules(moleculeSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		container.removeElectronContainer(newBond);
		container.addBond(formerBond);
		Molecule molecule = new org.openscience.cdk.Molecule(container);
		SetOfMolecules moleculeSet = ConnectivityChecker
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
		return "Change Bond";
	}
}
