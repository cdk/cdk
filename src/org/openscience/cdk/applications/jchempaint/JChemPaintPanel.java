/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The JChemPaint project
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
package org.openscience.jchempaint;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.JInternalFrame.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import org.openscience.cdk.*;
import org.openscience.cdk.applications.plugin.*;
import org.openscience.cdk.controller.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.io.listener.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.tools.manipulator.*;
import org.openscience.cdk.validate.*;
import org.openscience.jchempaint.*;
import org.openscience.jchempaint.action.*;
import org.openscience.jchempaint.application.*;
import org.openscience.jchempaint.dialogs.*;
import org.openscience.jchempaint.io.*;

/**
 *  JPanel that contains a full JChemPaint program, either viewer or full
 *  editor.
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 * @created    16. Februar 2005
 */
public abstract class JChemPaintPanel
		 extends JPanel
		 implements ChangeListener
{

	/**
	 *  Description of the Field
	 */
	protected JChemPaintModel jcpm;
	private LoggingTool logger;


	/**
	 *  Constructor for the JChemPaintPanel object
	 */
	public JChemPaintPanel()
	{
		logger = new LoggingTool(this);
		jcpm = new JChemPaintModel();
		setPreferredSize(new Dimension(800, 600));
	}


	/**
	 *  FIXME: must not use JChemPaint
	 */
	public void setupPopupMenus()
	{
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public Image takeSnapshot()
	{
		return null;
	}


	/**
	 *  Sets the jChemPaintModel attribute of the JChemPaintPanel object
	 *
	 *@param  model  The new jChemPaintModel value
	 */
	public void setJChemPaintModel(JChemPaintModel model)
	{
	}


	/**
	 *  Gets the toolBar attribute of the JChemPaintPanel object
	 *
	 *@return    The toolBar value
	 */
	public JToolBar getToolBar()
	{
		return null;
	}


	/**
	 *  Partitions a given String into separate words and writes them into an
	 *  array.
	 *
	 *@param  input  String The String to be cutted into pieces
	 *@return        String[] The array containing the separate words
	 */
	public String[] tokenize(String input)
	{
		Vector v = new Vector();
		StringTokenizer t = new StringTokenizer(input);
		String cmd[];
		while (t.hasMoreTokens())
		{
			v.addElement(t.nextToken());
		}
		cmd = new String[v.size()];
		for (int i = 0; i < cmd.length; i++)
		{
			cmd[i] = (String) v.elementAt(i);
		}
		return cmd;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  file  Description of the Parameter
	 */
	public void showChemFile(Reader file)
	{
		ChemObjectReader cor = null;

		/*
		 *  Have the ReaderFactory determine the file format
		 */
		try
		{
			cor = getChemObjectReader(file);
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
			JOptionPane.showMessageDialog(this, "Could not determine file format.");
			return;
		}

		String error = null;
		ChemFile chemFile = null;
		ChemModel chemModel = null;
		if (cor.accepts(new ChemFile()))
		{
			// try to read a ChemFile
			try
			{
				chemFile = (ChemFile) cor.read((ChemObject) new ChemFile());
				if (chemFile != null)
				{
					processChemFile(chemFile);
					return;
				} else
				{
					logger.warn("The object chemFile was empty unexpectedly!");
				}
			} catch (Exception exception)
			{
				error = "Error while reading file: " + exception.getMessage();
				logger.warn(error);
				logger.debug(exception);
			}
		}
		if (error != null)
		{
			JOptionPane.showMessageDialog(this, error);
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
					processChemModel(chemModel);
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
				logger.error(error);
				logger.debug(exception);
			}
		}
		if (error != null)
		{
			JOptionPane.showMessageDialog(this, error);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  chemFile  Description of the Parameter
	 */
	private void processChemFile(ChemFile chemFile)
	{
		logger.info("Information read from file:");

		int chemSequenceCount = chemFile.getChemSequenceCount();
		logger.info("  # sequences: ", chemSequenceCount);

		for (int i = 0; i < chemSequenceCount; i++)
		{
			ChemSequence chemSequence = chemFile.getChemSequence(i);

			int chemModelCount = chemSequence.getChemModelCount();
			logger.info("  # model in seq(" + i + "): ", chemModelCount);

			for (int j = 0; j < chemModelCount; j++)
			{
				ChemModel chemModel = chemSequence.getChemModel(j);
				processChemModel(chemModel);
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  chemModel  Description of the Parameter
	 */
	private void processChemModel(ChemModel chemModel)
	{
		// check for bonds
		if (ChemModelManipulator.getAllInOneContainer(chemModel).getBondCount() == 0)
		{
			String error = "Model does not have bonds. Cannot depict contents.";
			logger.warn(error);
			JOptionPane.showMessageDialog(this, error);
			return;
		}

		// check for coordinates
		if (!(GeometryTools.has2DCoordinates(ChemModelManipulator.getAllInOneContainer(chemModel))))
		{

			String error = "Model does not have coordinates. Cannot open file.";
			logger.warn(error);

			//JOptionPane.showMessageDialog(jcp, error);
			CreateCoordinatesForFileDialog frame = new CreateCoordinatesForFileDialog(chemModel);
			JChemPaint.getInstance().add(frame);
			frame.pack();
			frame.show();
			return;
		}

		JChemPaintModel jcpm = new JChemPaintModel(chemModel);

		setJChemPaintModel(jcpm);
	}


	/**
	 *  Gets the chemObjectReader attribute of the JChemPaintPanel object
	 *
	 *@param  reader           Description of the Parameter
	 *@return                  The chemObjectReader value
	 *@exception  IOException  Description of the Exception
	 */
	private ChemObjectReader getChemObjectReader(Reader reader) throws IOException
	{
		ReaderFactory factory = new ReaderFactory();
		ChemObjectReader coReader = factory.createReader(reader);
		if (coReader != null)
		{
			coReader.addChemObjectIOListener(new SwingGUIListener(JChemPaint.getInstance(), 4));
		}
		return coReader;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  chemFile  Description of the Parameter
	 */
	public void showChemFile(ChemFile chemFile)
	{
		logger.info("Information read from file:");

		int chemSequenceCount = chemFile.getChemSequenceCount();
		logger.info("  # sequences: " + chemSequenceCount);

		for (int i = 0; i < chemSequenceCount; i++)
		{
			ChemSequence chemSequence = chemFile.getChemSequence(i);

			int chemModelCount = chemSequence.getChemModelCount();
			logger.info("  # model in seq(" + i + "): " + chemModelCount);

			for (int j = 0; j < chemModelCount; j++)
			{
				ChemModel chemModel = chemSequence.getChemModel(j);
				showChemModel(chemModel);
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  chemModel  Description of the Parameter
	 */
	public void showChemModel(ChemModel chemModel)
	{
		// check for bonds
		if (ChemModelManipulator.getAllInOneContainer(chemModel).getBondCount() == 0)
		{
			String error = "Model does not have bonds. Cannot depict contents.";
			logger.warn(error);
			JOptionPane.showMessageDialog(this, error);
			return;
		}

		// check for coordinates
		if (!(GeometryTools.has2DCoordinates(ChemModelManipulator.getAllInOneContainer(chemModel))))
		{

			String error = "Model does not have coordinates. Will ask for coord generation.";
			logger.warn(error);

			CreateCoordinatesForFileDialog frame = new CreateCoordinatesForFileDialog(chemModel);
			JChemPaint.getInstance().add(frame);
			frame.pack();
			frame.show();
			frame.moveToFront();
			return;
		}

		this.jcpm = new JChemPaintModel(chemModel);
	}


	/**
	 *  Gets the chemModel attribute of the JChemPaint object
	 *
	 *@return    The chemModel value
	 */
	public ChemModel getChemModel()
	{
		return jcpm.getChemModel();
	}


	/**
	 *  Gets the chemFile attribute of the JChemPaint object
	 *
	 *@return    The chemFile value
	 */
	public ChemFile getChemFile()
	{
		ChemFile file = new ChemFile();
		ChemSequence sequence = new ChemSequence();
		sequence.addChemModel(getChemModel());
		file.addChemSequence(sequence);
		return file;
	}
}


