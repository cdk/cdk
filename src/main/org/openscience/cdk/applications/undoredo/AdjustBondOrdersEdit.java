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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * Undo/Redo Edit class for the AdjustBondOrdesAction, containing the methods
 * for undoing and redoing the regarding changes
 * 
 * @author tohel
 * @cdk.module control
 * @cdk.svnrev  $Revision$
 */
public class AdjustBondOrdersEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = 1513012471000333600L;
    
    private HashMap changedBondOrders;

	/**
	 * @param changedBonds
	 *            A HashMap containing the changed atoms as key and an Array
	 *            with the former and the changed bondOrder
	 */
	public AdjustBondOrdersEdit(HashMap changedBonds) {
		this.changedBondOrders = changedBonds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		Set keys = changedBondOrders.keySet();
		Iterator it = keys.iterator();
		while (it.hasNext()) {
			IBond bond = (IBond) it.next();
			IBond.Order[] bondOrders = (IBond.Order[]) changedBondOrders.get(bond);
			bond.setOrder(bondOrders[0]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		Set keys = changedBondOrders.keySet();
		Iterator it = keys.iterator();
		while (it.hasNext()) {
			IBond bond = (IBond) it.next();
			IBond.Order[] bondOrders = (IBond.Order[]) changedBondOrders.get(bond);
			bond.setOrder(bondOrders[1]);
		}
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
		return "AdjustBondOrders";
	}
}
