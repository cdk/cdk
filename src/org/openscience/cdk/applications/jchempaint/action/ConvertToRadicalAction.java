/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The JChemPaint project
 *
 * Contact: jchempaint-devel@lists.sf.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.event.ActionEvent;

import javax.swing.undo.UndoableEdit;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.interfaces.ChemObject;
import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.undoredo.ConvertToRadicalEdit;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * @cdk.module jchempaint
 */
public class ConvertToRadicalAction extends JCPAction {

    public void actionPerformed(ActionEvent event) {
        logger.debug("Converting to radical: ", type);
        ChemObject object = getSource(event);
        JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
        org.openscience.cdk.interfaces.ChemModel model = jcpmodel.getChemModel();
        if (object != null) {
            if (object instanceof Atom) {
                Atom atom = (Atom)object;
                AtomContainer relevantContainer = ChemModelManipulator.getRelevantAtomContainer(model, atom);
                ElectronContainer electronContainer = new SingleElectron(atom);
                relevantContainer.addElectronContainer(electronContainer);
                UndoableEdit  edit = new ConvertToRadicalEdit(relevantContainer, 
                        electronContainer);
                jcpPanel.getUndoSupport().postEdit(edit);
                logger.info("Added single electron to atom");
                logger.debug("new AC: ", relevantContainer);
            } else {
                logger.error("Object not an Atom! Cannot convert into a radical!");
            }
        } else {
            logger.warn("Cannot convert a null object!");
        }
        jcpmodel.fireChange();
    }
}
