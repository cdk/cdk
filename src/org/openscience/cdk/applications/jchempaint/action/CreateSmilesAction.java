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
import java.awt.Frame;
import javax.swing.JFrame;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.applications.jchempaint.dialogs.TextViewDialog;


/**
 * Creates a SMILES from the current model
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 * @created    22. April 2005
 */
public class CreateSmilesAction extends JCPAction
{

	TextViewDialog dialog = null;
	JFrame frame = null;


	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent e)
	{
		logger.debug("Trying to create smile: ", type);
		/*
		 *  if (jcpPanel.getFrame() != null)
		 *  {
		 *  try
		 *  {
		 *  Frame frame = (Frame)jcpPanel.getFrame();
		 *  }
		 *  catch(Exception exc)
		 *  {
		 *  logger.debug("Could not cast JCP frame to Frame");
		 *  }
		 *  }
		 */
		if (dialog == null)
		{
			dialog = new TextViewDialog(frame, "SMILES", null, false, 40, 2);
		}
		SmilesGenerator generator = new SmilesGenerator();
		String smiles = "";
		try
		{
			ChemModel model = (ChemModel) jcpPanel.getJChemPaintModel().getChemModel();
			AtomContainer container = ChemModelManipulator.getAllInOneContainer(model);
			Molecule molecule = new Molecule(container);
			smiles = generator.createSMILES(molecule);
			dialog.setMessage("Generated SMILES:", smiles);
		} catch (Exception exception)
		{
			String message = "Error while creating SMILES: " + exception.getMessage();
			logger.error(message);
			logger.debug(exception);
			dialog.setMessage("Error", message);
		}
		dialog.show();
	}
}

