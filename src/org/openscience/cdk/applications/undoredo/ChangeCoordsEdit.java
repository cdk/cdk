/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005-2006  The Chemistry Development Kit (CDK) project
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
import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;

/**
 * Undo/Redo Edit superclass for all edit classes for coordinate changing
 * actions, containing the methods for undoing and redoing the regarding changes
 * 
 * @author tohel
 */
public class ChangeCoordsEdit extends AbstractUndoableEdit {

    private HashMap atomCoordsMap;

    /**
     * @param atomCoordsMap
     *            A HashMap containing the changed atoms as key and an Array
     *            with the former and the changed coordinates as Point2ds
     */
    public ChangeCoordsEdit(HashMap atomCoordsMap) {
        this.atomCoordsMap = atomCoordsMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.UndoableEdit#redo()
     */
    public void redo() throws CannotRedoException {
        Set keys = atomCoordsMap.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            IAtom atom = (IAtom) it.next();
            Point2d[] coords = (Point2d[]) atomCoordsMap.get(atom);
            atom.setNotification(false);
            atom.setPoint2d(coords[0]);
            atom.setNotification(true);
        }
        // if (jcpPanel != null) {
        //    jcpPanel.scaleAndCenterMolecule(jcpPanel.getChemModel());
        // }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.undo.UndoableEdit#undo()
     */
    public void undo() throws CannotUndoException {
        Set keys = atomCoordsMap.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            IAtom atom = (IAtom) it.next();
            Point2d[] coords = (Point2d[]) atomCoordsMap.get(atom);
            atom.setPoint2d(coords[1]);
        }
        // if (jcpPanel != null) {
        //    jcpPanel.scaleAndCenterMolecule(jcpPanel.getChemModel());
        // }
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
        return "ChangeCoords";
    }
}
