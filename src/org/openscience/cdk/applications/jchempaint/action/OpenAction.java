/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The JChemPaint project
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.ChemObjectReader;
import org.openscience.cdk.io.IChIReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.listener.SwingGUIListener;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.applications.jchempaint.dialogs.CreateCoordinatesForFileDialog;

import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.JChemPaintEditorPanel;
import org.openscience.cdk.applications.jchempaint.io.JCPFileFilter;
import org.openscience.cdk.applications.jchempaint.io.JCPFileView;

/**
 * Shows the open dialog
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 * @created    22. April 2005
 */
public class OpenAction extends JCPAction
{

	private ChemFile chemFile;
	private ChemSequence chemSequence;
	private ChemModel chemModel;
	private FileFilter currentFilter = null;
	private FileFilter currentOpenFileFilter = null;
	private FileFilter currentSaveFileFilter = null;
	


	/**
	 *  Opens an empty JChemPaint frame.
	 *
	 *@param  e  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent e)
	{

		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(jcpPanel.getCurrentWorkDirectory());
		JCPFileFilter.addChoosableFileFilters(chooser);
		if (jcpPanel.getCurrentOpenFileFilter() != null)
		{
			chooser.setFileFilter(jcpPanel.getCurrentOpenFileFilter());
		}
		if (jcpPanel.getLastOpenedFile() != null)
		{
			chooser.setSelectedFile(jcpPanel.getLastOpenedFile());
		}
		if (currentFilter != null)
		{
			chooser.setFileFilter(currentFilter);
		}
		chooser.setFileView(new JCPFileView());

		int returnVal = chooser.showOpenDialog(jcpPanel);
		String type = null;
		ChemObjectReader cor = null;

		currentFilter = chooser.getFileFilter();

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			jcpPanel.setCurrentWorkDirectory(chooser.getCurrentDirectory());
			jcpPanel.setCurrentOpenFileFilter(chooser.getFileFilter());

			File inFile = chooser.getSelectedFile();
			jcpPanel.setLastOpenedFile(inFile);

			/*
			 *  Have the ReaderFactory determine the file format
			 */
			try
			{
				cor = getChemObjectReader(inFile);
			} catch (IOException ioExc)
			{
				logger.warn("IOException while determining file format.");
				logger.debug(ioExc);
			} catch (Exception exc)
			{
				logger.warn("Exception while determining file format.");
				logger.debug(exc);
			}

			if (cor == null)
			{
				// try to determine from user's guess
				try
				{
					FileReader reader = new FileReader(inFile);
					javax.swing.filechooser.FileFilter ff = chooser.getFileFilter();
					if (ff instanceof JCPFileFilter)
					{
						type = ((JCPFileFilter) ff).getType();
					} else
					{
						type = "unknown";
					}

					if (type.equals(JCPFileFilter.cml) || type.equals(JCPFileFilter.xml))
					{
						cor = new CMLReader(reader);
					} else if (type.equals(JCPFileFilter.sdf))
					{
						cor = new MDLReader(reader);
					} else if (type.equals(JCPFileFilter.mol))
					{
						cor = new MDLReader(reader);
					} else if (type.equals(JCPFileFilter.ichi))
					{
						cor = new IChIReader(reader);
					}
				} catch (FileNotFoundException exception)
				{
					logger.warn("File cannot be found.");
					logger.debug(exception);
				}
			}

			if (cor == null)
			{
				JOptionPane.showMessageDialog(jcpPanel, "Could not determine file format.");
				return;
			}

			String error = null;
			ChemModel chemModel = null;
			if (cor.accepts(new ChemFile()))
			{
				// try to read a ChemFile
				try
				{
					chemFile = (ChemFile) cor.read((ChemObject) new ChemFile());
					if (chemFile != null)
					{
						processChemFile(chemFile, inFile);
						return;
					} else
					{
						logger.warn("The object chemFile was empty unexpectedly!");
					}
				} catch (Exception exception)
				{
					error = "Error while reading file: " + exception.getMessage();
          exception.printStackTrace();
					logger.warn(error);
					logger.debug(exception);
				}
			}
			if (error != null)
			{
				JOptionPane.showMessageDialog(jcpPanel, error);
				return;
			}
			if (cor.accepts(new ChemModel()))
			{
				// try to read a ChemModel
				try
				{
					chemModel = (ChemModel) cor.read((ChemObject) new ChemModel());
					if (chemModel != null)
					{
						processChemModel(chemModel, inFile);
						return;
					} else
					{
						logger.warn("The object chemModel was empty unexpectedly!");
					}
					error = null;
					// overwrite previous problems, it worked now
				} catch (Exception exception)
				{
					error = "Error while reading file: " + exception.getMessage();
          exception.printStackTrace();
					logger.error(error);
					logger.debug(exception);
				}
			}
			if (error != null)
			{
				JOptionPane.showMessageDialog(jcpPanel, error);
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  chemFile  Description of the Parameter
	 *@param  input     Description of the Parameter
	 */
	private void processChemFile(ChemFile chemFile, File input)
	{
		logger.info("Information read from file:");

		int chemSequenceCount = chemFile.getChemSequenceCount();
		logger.info("  # sequences: ", chemSequenceCount);

		for (int i = 0; i < chemSequenceCount; i++)
		{
			chemSequence = chemFile.getChemSequence(i);

			int chemModelCount = chemSequence.getChemModelCount();
			logger.info("  # model in seq(" + i + "): ", chemModelCount);

			for (int j = 0; j < chemModelCount; j++)
			{
				chemModel = chemSequence.getChemModel(j);
				processChemModel(chemModel, input);
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  chemModel  Description of the Parameter
	 *@param  input      Description of the Parameter
	 */
	private void processChemModel(ChemModel chemModel, File input)
	{
		// check for bonds
		if (ChemModelManipulator.getAllInOneContainer(chemModel).getBondCount() == 0)
		{
			String error = "Model does not have bonds. Cannot depict contents.";
			logger.warn(error);
			JOptionPane.showMessageDialog(jcpPanel, error);
			return;
		}

		// check for coordinates
		if (!(GeometryTools.has2DCoordinates(ChemModelManipulator.getAllInOneContainer(chemModel))))
		{

			String error = "Model does not have coordinates. Cannot open file.";
			logger.warn(error);

			JOptionPane.showMessageDialog(jcpPanel, error);
			CreateCoordinatesForFileDialog frame = new CreateCoordinatesForFileDialog(chemModel);
			frame.pack();
			frame.show();
			return;
		}
		JChemPaintModel jcpm = new JChemPaintModel(chemModel);
		jcpm.setTitle(input.getName());

		if(jcpPanel.isEmbedded()){
      if(jcpPanel.showWarning()){
        //FIXME not working
        jcpPanel.setJChemPaintModel(jcpm);
        jcpPanel.repaint();
      }
    }else{
      JFrame jcpf = ((JChemPaintEditorPanel)jcpPanel).getNewFrame(jcpm);
      jcpf.show();
      jcpf.pack();
    }
	}


	/**
	 *  Gets the chemObjectReader attribute of the OpenAction object
	 *
	 *@param  file             Description of the Parameter
	 *@return                  The chemObjectReader value
	 *@exception  IOException  Description of the Exception
	 */
	private ChemObjectReader getChemObjectReader(File file) throws IOException
	{
		Reader fileReader = new FileReader(file);
		ReaderFactory factory = new ReaderFactory();
		ChemObjectReader reader = factory.createReader(fileReader);
		if (reader != null)
		{
			reader.addChemObjectIOListener(new SwingGUIListener(jcpPanel, 4));
		}
		return reader;
	}

}

