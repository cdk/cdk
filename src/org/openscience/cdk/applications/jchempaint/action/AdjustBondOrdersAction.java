/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2005  The JChemPaint project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.applications.jchempaint.application.JChemPaint;

/**
 * Triggers the adjustment of BondOrders
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 * @created    22. April 2005
 */
public class AdjustBondOrdersAction extends JCPAction
{

	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent e)
	{
		logger.debug("Adjusting bondorders: ", type);
		if (type.equals("clear"))
		{
			try
			{
				SaturationChecker satChecker = new SaturationChecker();
				ChemModel model = (ChemModel) JChemPaint.getInstance().getCurrentModel().getChemModel();
				AtomContainer[] containers = ChemModelManipulator.getAllAtomContainers(model);
				for (int i = 0; i < containers.length; i++)
				{
					satChecker.unsaturate(containers[i].getBonds());
				}
				JChemPaint.getInstance().getCurrentModel().fireChange();
			} catch (Exception exc)
			{
				String error = "Could not adjust bondorders.";
				logger.error(error);
				logger.debug(exc);
				JOptionPane.showMessageDialog(JChemPaint.getInstance(), error);
			}
		} else
		{
			try
			{
				SaturationChecker satChecker = new SaturationChecker();
				ChemModel model = (ChemModel) JChemPaint.getInstance().getCurrentModel().getChemModel();
				AtomContainer[] containers = ChemModelManipulator.getAllAtomContainers(model);
				for (int i = 0; i < containers.length; i++)
				{
					satChecker.saturate(containers[i]);
				}
				JChemPaint.getInstance().getCurrentModel().fireChange();
			} catch (Exception exc)
			{
				String error = "Could not adjust bondorders.";
				logger.error(error);
				logger.debug(exc);
				JOptionPane.showMessageDialog(JChemPaint.getInstance(), error);
			}
		}
	}
}

