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

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.applications.swing.editor.AtomEditor;
import org.openscience.cdk.applications.swing.editor.ChemObjectEditor;
import org.openscience.cdk.applications.swing.editor.PseudoAtomEditor;
import org.openscience.cdk.applications.swing.editor.ReactionEditor;

import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.dialogs.ChemObjectPropertyDialog;
import org.openscience.cdk.applications.jchempaint.application.JChemPaint;

/**
 * Action for triggering an edit of a ChemObject
 *
 * @cdk.module jchempaint
 * @author E.L. Willighagen <elw38@cam.ac.uk>
 */
public class EditChemObjectPropsAction extends JCPAction {

    public void actionPerformed(ActionEvent event) {
        if (JChemPaint.getInstance().getCurrentModel() != null) {
            JChemPaintModel jcpmodel = JChemPaint.getInstance().getCurrentModel();
            ChemObject object = getSource(event);
            logger.debug("Showing object properties for: ", object);
            ChemObjectEditor editor = null;
            if (object instanceof PseudoAtom) {
                editor = new PseudoAtomEditor();
            } else if (object instanceof Atom) {
                editor = new AtomEditor();
            } else if (object instanceof Reaction) {
                editor = new ReactionEditor();
            }

            if (editor != null) {
                editor.setChemObject(object);
                ChemObjectPropertyDialog frame =
                    new ChemObjectPropertyDialog(jcpmodel, editor);
                frame.pack();
                frame.show();
            }
        }
    }
    
}
