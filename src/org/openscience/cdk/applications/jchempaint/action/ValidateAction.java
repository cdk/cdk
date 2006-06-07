/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2006  The JChemPaint project
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

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.applications.jchempaint.JChemPaintEditorPanel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.dialogs.ValidateFrame;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.validate.BasicValidator;
import org.openscience.cdk.validate.CDKValidator;
import org.openscience.cdk.validate.ProblemMarker;


/**
 * An action opening a validation frame
 * 
 * @cdk.module jchempaint
 * @author     E.L. Willighagen <elw38@cam.ac.uk>
 */
public class ValidateAction extends JCPAction
{

    private static final long serialVersionUID = -3776589605934024224L;
    
    ValidateFrame frame = null;


	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent event)
	{
		logger.debug("detected validate action: ", type);
		if (type.equals("run"))
		{
			IChemObject object = getSource(event);
			if (object == null)
			{
				// called from main menu
				JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();

				org.openscience.cdk.interfaces.IChemModel model = jcpmodel.getChemModel();
				if (model != null)
				{
					runValidate(model);
				} else
				{
					System.out.println("Empty model");
				}
			} else
			{
				// calleb from popup menu
				logger.debug("Validate called from popup menu!");
				runValidate(object);
			}
		} else if (type.equals("clear"))
		{
			clearValidate();
		} else if (type.startsWith("toggle") && type.length() > 6)
		{
			String toggle = type.substring(6);
			try
			{
				JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
				boolean newChecked = !menuItem.isSelected();
				menuItem.setSelected(newChecked);
				if (toggle.equals("Basic"))
				{
					if (newChecked)
					{
						logger.info("Turned on " + toggle);
						JChemPaintEditorPanel.getValidatorEngine().addValidator(new BasicValidator());
					} else
					{
						logger.info("Turned off " + toggle);
						JChemPaintEditorPanel.getValidatorEngine().removeValidator(new BasicValidator());
					}
				} else if (toggle.equals("CDK"))
				{
					if (newChecked)
					{
						logger.info("Turned on " + toggle);
						JChemPaintEditorPanel.getValidatorEngine().addValidator(new CDKValidator());
					} else
					{
						logger.info("Turned off " + toggle);
						JChemPaintEditorPanel.getValidatorEngine().removeValidator(new CDKValidator());
					}
				} else
				{
					logger.error("Don't know what to toggle: " + toggle);
				}
			} catch (ClassCastException exception)
			{
				logger.error("Cannot toggle a non JCheckBoxMenuItem!");
			}
		} else
		{
			logger.error("Unknown command: " + type);
		}
	}


	/**
	 *  Description of the Method
	 */
	private void clearValidate()
	{
		JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
		org.openscience.cdk.interfaces.IChemModel model = jcpmodel.getChemModel();
		org.openscience.cdk.interfaces.IAtom[] atoms = ChemModelManipulator.getAllInOneContainer(model).getAtoms();
		logger.info("Clearing errors on atoms: " + atoms.length);
		for (int i = 0; i < atoms.length; i++)
		{
			ProblemMarker.unmark(atoms[i]);
		}
		jcpmodel.fireChange();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  object  Description of the Parameter
	 */
	private void runValidate(IChemObject object)
	{
		logger.info("Running validation");
		clearValidate();
		if (jcpPanel.getJChemPaintModel() != null)
		{
			frame = new ValidateFrame(jcpPanel);
			frame.validate(object);
			frame.pack();
			frame.show();
		}
	}

}

