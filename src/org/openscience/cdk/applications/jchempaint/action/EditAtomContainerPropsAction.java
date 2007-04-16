/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2007  E.L. Willighagen <elw38@cam.ac.uk>
 *
 *  Contact: jchempaint-devel@lists.sf.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
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
 */
package org.openscience.cdk.applications.jchempaint.action;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.dialogs.ChemObjectPropertyDialog;
import org.openscience.cdk.applications.swing.editor.AtomContainerEditor;
import org.openscience.cdk.applications.swing.editor.ChemObjectEditor;
import org.openscience.cdk.controller.SimpleController2D;
import org.openscience.cdk.interfaces.IChemObject;

import java.awt.event.ActionEvent;

/**
 * Action for triggering an edit of a IChemObject
 *
 * @author        E.L. Willighagen <elw38@cam.ac.uk>
 * @cdk.module    jchempaint
 */
public class EditAtomContainerPropsAction extends JCPAction {

	private static final long serialVersionUID = 8489495722307245626L;

	public void actionPerformed(ActionEvent event) {
		if (jcpPanel.getJChemPaintModel() != null) {
			JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
			IChemObject object = getSource(event);
			logger.debug("Showing object properties for: ", object);
			ChemObjectEditor editor = new AtomContainerEditor();
			// ok, expect MATCHING_ATOMCONTAINER to be set
			editor.setChemObject(
				(ChemObject)(object.getProperty(SimpleController2D.MATCHING_ATOMCONTAINER))
			);
			ChemObjectPropertyDialog frame =
				new ChemObjectPropertyDialog(jcpmodel, editor);
			frame.pack();
			frame.setVisible(true);
		}
	}

}

