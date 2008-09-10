/* $RCSfile$
 * $Author: egonw $
 * $Date: 2008-09-10 08:13:15 +0200 (Wed, 10 Sep 2008) $
 * $Revision: 12257 $
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
package org.openscience.cdk.controller;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * An undo adapter for updating the state of the undo components according to
 * the new state of the undo history list Is registered as a listener to the
 * undoSupport which is recieving the undo/redo events.
 * 
 * @author      tohel
 * @cdk.module  control
 * @cdk.svnrev  $Revision: 12257 $
 */
public class UndoAdapter implements UndoableEditListener {

	private UndoManager undoManager;

	/**
	 * @param undoManager
	 *            The undoManager handling the undo/redo history list
	 */
	public UndoAdapter(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.UndoableEditListener#undoableEditHappened(javax.swing.event.UndoableEditEvent)
	 */
	public void undoableEditHappened(UndoableEditEvent arg0) {
		UndoableEdit edit = arg0.getEdit();
		undoManager.addEdit(edit);
	}

}
