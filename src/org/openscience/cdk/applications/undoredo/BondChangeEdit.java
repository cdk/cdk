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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * @cdk.module control
 */
public class BondChangeEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = -2723716036619377584L;

    private IBond newBond;

	private IBond formerBond;

	private IChemModel chemModel;

	public BondChangeEdit(IChemModel chemModel, IBond formerBond, IBond newBond) {
		this.chemModel = chemModel;
		this.formerBond = formerBond;
		this.newBond = newBond;
		if (formerBond != null) {
			formerBond.setAtom(newBond.getAtom(0), 0);
			formerBond.setAtom(newBond.getAtom(1), 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		IAtomContainer container = ChemModelManipulator.getAllInOneContainer(chemModel);
		container.removeBond(formerBond);
		container.addBond(newBond);
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
		System.out.println("BondChangeEdit undo");
		IAtomContainer container = ChemModelManipulator.getAllInOneContainer(chemModel);
		container.removeBond(newBond);
		container.addBond(formerBond);
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
		return "Change Bond";
	}
}
