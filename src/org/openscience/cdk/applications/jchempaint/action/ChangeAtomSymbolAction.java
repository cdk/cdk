/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2005  The JChemPaint project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.event.ActionEvent;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.controller.Controller2DModel;


/**
 * changes the atom symbol
 * @cdk.module jchempaint
 * @author     Egon Willighagen
 */
public class ChangeAtomSymbolAction extends JCPAction
{

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
			Atom atomInRange = null;
			ChemObject object = getSource(event);
			logger.debug("Source of call: ", object);
			if (object instanceof Atom)
			{
				atomInRange = (Atom) object;
			} else
			{
				atomInRange = jcpm.getRendererModel().getHighlightedAtom();
			}
			String s = event.getActionCommand();
			String symbol = s.substring(s.indexOf("@") + 1);
			atomInRange.setSymbol(symbol);
			// modify the current atom symbol
			c2dm.setDrawElement(symbol);
			// configure the atom, so that the atomic number matches the symbol
			try
			{
				IsotopeFactory.getInstance().configure(atomInRange);
			} catch (Exception exception)
			{
				logger.error("Error while configuring atom");
				logger.debug(exception);
			}
			jcpm.fireChange();
		}
	}

}

