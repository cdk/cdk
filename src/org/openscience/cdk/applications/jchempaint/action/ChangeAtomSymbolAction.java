/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2006  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
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

import java.awt.event.ActionEvent;

import javax.swing.undo.UndoableEdit;

import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.undoredo.ChangeAtomSymbolEdit;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.controller.Controller2D;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.manipulator.MoleculeSetManipulator;


/**
 * changes the atom symbol
 * @cdk.module jchempaint
 * @author     Egon Willighagen
 */
public class ChangeAtomSymbolAction extends JCPAction
{

	private static final long serialVersionUID = -8502905723573311893L;

	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent event)
	{
		logger.debug("About to change atom type of relevant atom!");
		JChemPaintModel jcpm = jcpPanel.getJChemPaintModel();
		if (jcpm != null)
		{
			Controller2DModel c2dm = jcpm.getControllerModel();
			IAtom atomInRange = null;
			IChemObject object = getSource(event);
			logger.debug("Source of call: ", object);
			if (object instanceof IAtom)
			{
				atomInRange = (IAtom) object;
			} else
			{
				atomInRange = jcpm.getRendererModel().getHighlightedAtom();
			}
            String formerSymbol = atomInRange.getSymbol();
			String s = event.getActionCommand();
			String symbol = s.substring(s.indexOf("@") + 1);
            atomInRange.setSymbol(symbol);
			// modify the current atom symbol
			c2dm.setDrawElement(symbol);
			// configure the atom, so that the atomic number matches the symbol
			try
			{
				IsotopeFactory.getInstance(atomInRange.getBuilder()).configure(atomInRange);
			} catch (Exception exception)
			{
				logger.error("Error while configuring atom");
				logger.debug(exception);
			}
			((Controller2D)jcpPanel.getDrawingPanel().getMouseListeners()[0]).updateAtom(MoleculeSetManipulator.getAllInOneContainer(jcpm.getChemModel().getMoleculeSet()), atomInRange);
            UndoableEdit  edit = new ChangeAtomSymbolEdit(atomInRange, formerSymbol, symbol);
            jcpPanel.getUndoSupport().postEdit(edit);
			jcpm.fireChange();
		}
	}

}

