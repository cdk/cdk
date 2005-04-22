/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The JChemPaint project
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.io.CDKSourceCodeWriter;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.ChemObjectWriter;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.SMILESWriter;
import org.openscience.cdk.io.SVGWriter;
import org.openscience.cdk.io.listener.SwingGUIListener;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.applications.jchempaint.application.JChemPaint;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.io.JCPFileFilter;
import org.openscience.cdk.applications.jchempaint.io.JCPFileFilterInterface;
import org.openscience.cdk.applications.jchempaint.io.JCPFileView;
import org.openscience.cdk.applications.jchempaint.io.JCPSaveFileFilter;

/**
 * Opens a "Save as" dialog
 *
 * cdk.module jchempaint
 * @author     steinbeck
 * @created    22. April 2005
 */
public class SaveAsAction extends NewAction
{

	/**
	 *  Description of the Field
	 */
	protected ChemObjectWriter cow;
	private FileFilter currentFilter = null;


	/**
	 *  Opens a dialog frame and manages the saving of a file.
	 *
	 *@param  event  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent event)
	{

		JChemPaintModel jcpm = JChemPaint.getInstance().getCurrentModel();
		if (jcpm == null)
		{
			String error = "Nothing to save.";
			JOptionPane.showMessageDialog(JChemPaint.getInstance(), error);
		} else
		{
			saveAs(event);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of the Parameter
	 */
	protected void saveAs(ActionEvent event)
	{

		JChemPaintModel jcpm = JChemPaint.getInstance().getCurrentModel();

		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(JChemPaint.currentWorkDirectory);
		JCPSaveFileFilter.addChoosableFileFilters(chooser);
		if (JChemPaint.currentSaveFileFilter != null)
		{
			chooser.setFileFilter(JChemPaint.currentSaveFileFilter);
		}
		if (currentFilter != null)
		{
			chooser.setFileFilter(currentFilter);
		}
		chooser.setFileView(new JCPFileView());

		int returnVal = chooser.showSaveDialog(JChemPaint.getInstance());
		String type = null;

		ChemObject object = getSource(event);
		currentFilter = chooser.getFileFilter();
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			type = ((JCPFileFilterInterface) currentFilter).getType();
			File outFile = chooser.getSelectedFile();

			ChemModel model = (ChemModel) jcpm.getChemModel();
			if (object == null)
			{
				// called from main menu
				try
				{
					if (type.equals(JCPSaveFileFilter.mol))
					{
						saveAsMol(model, outFile);
					} else if (type.equals(JCPSaveFileFilter.cml))
					{
						saveAsCML2(model, outFile);
					} else if (type.equals(JCPSaveFileFilter.smiles))
					{
						saveAsSMILES(model, outFile);
					} else if (type.equals(JCPSaveFileFilter.svg))
					{
						saveAsSVG(model, outFile);
					} else if (type.equals(JCPSaveFileFilter.cdk))
					{
						saveAsCDKSourceCode(model, outFile);
					} else
					{
						String error = "Cannot save file in this format: " + type;
						logger.error(error);
						JOptionPane.showMessageDialog(JChemPaint.getInstance(), error);
						return;
					}
					jcpm.resetIsModified();
				} catch (Exception exc)
				{
					String error = "Error while writing file: " + exc.getMessage();
					logger.error(error);
					logger.debug(exc);
					JOptionPane.showMessageDialog(JChemPaint.getInstance(), error);
				}

			} else if (object instanceof Reaction)
			{
				try
				{
					if (type.equals(JCPSaveFileFilter.cml))
					{
						saveAsCML2(object, outFile);
					} else
					{
						String error = "Cannot save reaction in this format: " + type;
						logger.error(error);
						JOptionPane.showMessageDialog(JChemPaint.getInstance(), error);
					}
				} catch (Exception exc)
				{
					String error = "Error while writing file: " + exc.getMessage();
					logger.error(error);
					logger.debug(exc);
					JOptionPane.showMessageDialog(JChemPaint.getInstance(), error);
				}
			}
			JChemPaint.currentWorkDirectory = chooser.getCurrentDirectory();
			JChemPaint.currentSaveFileFilter = chooser.getFileFilter();
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  model          Description of the Parameter
	 *@param  outFile        Description of the Parameter
	 *@exception  Exception  Description of the Exception
	 */
	private void saveAsMol(ChemModel model, File outFile) throws Exception
	{
		logger.info("Saving the contents in a MDL molfile file...");
		cow = new MDLWriter(new FileWriter(outFile));
		if (cow != null)
		{
			cow.addChemObjectIOListener(new SwingGUIListener(JChemPaint.getInstance(), 4));
		}
		SetOfMolecules som = model.getSetOfMolecules();
		cow.write(som);
		cow.close();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  object         Description of the Parameter
	 *@param  outFile        Description of the Parameter
	 *@exception  Exception  Description of the Exception
	 */
	private void saveAsCML2(ChemObject object, File outFile) throws Exception
	{
		logger.info("Saving the contents in a CML 2.0 file...");
		cow = new CMLWriter(new FileWriter(outFile));
		if (cow != null)
		{
			cow.addChemObjectIOListener(new SwingGUIListener(JChemPaint.getInstance(), 4));
		}
		cow.write(object);
		cow.close();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  model          Description of the Parameter
	 *@param  outFile        Description of the Parameter
	 *@exception  Exception  Description of the Exception
	 */
	private void saveAsSMILES(ChemModel model, File outFile) throws Exception
	{
		logger.info("Saving the contents in SMILES format...");
		cow = new SMILESWriter(new FileWriter(outFile));
		if (cow != null)
		{
			cow.addChemObjectIOListener(new SwingGUIListener(JChemPaint.getInstance(), 4));
		}
		SetOfMolecules som = model.getSetOfMolecules();
		cow.write(som);
		cow.close();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  model          Description of the Parameter
	 *@param  outFile        Description of the Parameter
	 *@exception  Exception  Description of the Exception
	 */
	private void saveAsCDKSourceCode(ChemModel model, File outFile) throws Exception
	{
		logger.info("Saving the contents as a CDK source code file...");
		cow = new CDKSourceCodeWriter(new FileWriter(outFile));
		if (cow != null)
		{
			cow.addChemObjectIOListener(new SwingGUIListener(JChemPaint.getInstance(), 4));
		}
		AtomContainer ac = ChemModelManipulator.getAllInOneContainer(model);
		if (ac != null)
		{
			cow.write(new Molecule(ac));
			cow.close();
		} else
		{
			System.err.println("AC == null!");
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  model          Description of the Parameter
	 *@param  outFile        Description of the Parameter
	 *@exception  Exception  Description of the Exception
	 */
	protected void saveAsSVG(ChemModel model, File outFile) throws Exception
	{
		logger.info("Saving the contents as a SVG file...");
		cow = new SVGWriter(new FileWriter(outFile));
		if (cow != null)
		{
			cow.addChemObjectIOListener(new SwingGUIListener(JChemPaint.getInstance(), 4));
		}
		AtomContainer ac = (AtomContainer) ChemModelManipulator.getAllInOneContainer(model);
		if (ac != null)
		{
			cow.write((AtomContainer) ac.clone());
			cow.close();
		} else
		{
			System.err.println("AC == null!");
		}
	}
}

