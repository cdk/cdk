/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.UndoableEdit;

import org.openscience.cdk.controller.IController2DModel;

/**
 * This is an implementation of IUndoRedoHandler, which is used in JCP. It forwards the postEdit to the postEdit in UndoSupport from Controller2dModel
 * 
 * @author shk3
 * @cdk.module controlold
 * @cdk.svnrev  $Revision$
 */
public class JCPUndoRedoHandler implements IUndoRedoHandler {
	IController2DModel c2dm=null;


	/**
	 * Only constructor
	 * 
	 * @param c2dm The Controller2dModel of the current application
	 */
	public JCPUndoRedoHandler(IController2DModel c2dm) {
		this.setC2dm(c2dm);
	}
	
	/* (non-Javadoc)
	 * @see org.openscience.cdk.applications.undoredo.IUndoRedoHandler#postEdit(javax.swing.undo.UndoableEdit)
	 */
	public void postEdit(UndoableEdit edit) {
		c2dm.getUndoSupport().postEdit(edit);
	}

	/**
	 * Set the Controller2dModel of the current application here.
	 * 
	 * @param c2dm The Controller2dModel of the current application
	 */
	public void setC2dm(IController2DModel c2dm) {
		this.c2dm = c2dm;
	}

}
