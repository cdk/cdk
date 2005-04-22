/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2005  The JChemPaint project
 *
 * Contact: jchempaint-devel@lists.sourceforge.net
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
package org.openscience.jchempaint.application;

import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.controller.Controller2D;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.SetOfMoleculesManipulator;

import org.openscience.jchempaint.dnd.JCPTransferHandler;
import org.openscience.jchempaint.*;

/**
 * A JFrame the contains a JChemPaintPanel. It should contain as little
 * functionality as possible; all viewer/editing stuff should be in the
 * JChemPaintPanel; this class is only for framing it.
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class JChemPaintFrame extends JFrame implements ChangeListener
{

	protected JChemPaintModel jcpm;
	public JChemPaintPanel jcpp;
	public UndoStack undoStack;
	protected EventListenerList modelChangeListeners = null;
	int undoStackSize = 20;

	private HydrogenAdder hydrogenAdder;
	private LoggingTool logger;

	private JPanel workPanel = null;
	private GridBagLayout gridbag = null;
	private GridBagConstraints gridBagConstraints = null;
	private StatusBar statusBar;
	private StatusBar develStatusBar;
	private JMenuBar menubar;
    public JToolBar toolbar2;

	/**
	 *  The constructor method. Manages the JChemPaintFrame.
	 *
	 *@param  jcp   JChemPaint
	 *@param  jcpm  JChemPaintModel
	 */
	public JChemPaintFrame(JChemPaintModel jcpm)
	{
		logger = new LoggingTool(this);
		this.jcpm = jcpm;

		hydrogenAdder = new HydrogenAdder("org.openscience.cdk.tools.ValencyChecker");
		undoStack = new UndoStack(undoStackSize);
		undoStack.push(cloneModel(jcpm));
		jcpm.addChangeListener(this);
		setSize(new Dimension(600, 400));
		setBackground(Color.white);

		jcpp = new JChemPaintEditorPanel(jcpm);
        
		/* setBorder(BorderFactory.createEtchedBorder()); */
		getContentPane().setLayout(new BorderLayout());

		menubar = new JChemPaintMenuBar(jcpp, true);
		getContentPane().add(menubar, BorderLayout.NORTH);
        
		// create the panels and toolbars
		workPanel = new JPanel();
        workPanel.setLayout(new BorderLayout());
        workPanel.setBackground(Color.WHITE);

        boolean addPluginMenu = true;

		jcpp.setupPopupMenus();
		modelChangeListeners = jcpm.getChangeListeners();
		workPanel.add(jcpp, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());
		statusBar = new StatusBar();
        statusPanel.add(statusBar, BorderLayout.NORTH);
        
		// even if not shown, define them anyway
		develStatusBar = new DevelStatusBar();
        logger.info("Showing Developers GUI");
        statusPanel.add(develStatusBar, BorderLayout.SOUTH);

		workPanel.add(statusPanel, BorderLayout.SOUTH);

        getContentPane().add(workPanel, BorderLayout.CENTER);

        //pack();
		//repaint();
		/* jcpp.addMouseMotionListener(jcpp.r2d); */

        /* set the drag-n-drop/copy-paste handler
        this.setTransferHandler(new JCPTransferHandler("JCPFrame")); */
	}


	/**
	 *  Returns the Dimension of the JChemPaintPanel this frame owns.
	 *
	 *@return
	 */
	public Dimension getPanelSize()
	{
		return jcpp.getSize();
	}


	/**
	 *  Gets the jChemPaintPanel attribute of the JChemPaintFrame object
	 *
	 *@return    The jChemPaintPanel value
	 */
	public JChemPaintPanel getJChemPaintPanel()
	{
		return jcpp;
	}


	/**
	 *  Sets the JChemPaintModel.
	 *
	 *@param  model  The new model value
	 */
	public void setModel(JChemPaintModel model)
	{
		this.jcpm = model;
		model.setChangeListeners(modelChangeListeners);
		jcpp.setJChemPaintModel(model);
		jcpp.repaint();
	}


	/**
	 *  Returns the JChemPaintModel.
	 *
	 *@return    JChemPaintModel
	 */
	public JChemPaintModel getModel()
	{
		return jcpm;
	}


	/**
	 *  After certain actions (cleanup action ...) this method will be invoked.
	 *
	 *@param  e  ChangeEvent
	 */
	public void stateChanged(ChangeEvent e)
	{
		String stackStatus = null;
		logger.debug("state changed");
        String title = jcpm.getTitle();
        if (jcpm.isModified()) {
            title = "* " + title; // mark as modified in frame title bar
        }
		setTitle(title);
		ChemModel model = jcpm.getChemModel();
		SetOfMolecules moleculeSet = model.getSetOfMolecules();
		if (moleculeSet != null)
		{
			AtomContainer atomContainer = SetOfMoleculesManipulator.getAllInOneContainer(moleculeSet);

			try
			{
				// update # implicit hydrogrens (this should be more clever, not do it for every atom each time,
				// just like the Controller2D does!)
				Atom[] atoms = atomContainer.getAtoms();
				for (int i = 0; i < atoms.length; i++)
				{
					Atom atom = atoms[i];
					atom.setHydrogenCount(0);
					// reset it
                    logger.debug("hydrogenAdder: ", hydrogenAdder);
					hydrogenAdder.addImplicitHydrogensToSatisfyValency(atomContainer, atom);
				}

				// Just for testing: repartition everything after each change
				SetOfMolecules newMoleculeSet = ConnectivityChecker.partitionIntoMolecules(atomContainer);
				model.setSetOfMolecules(newMoleculeSet);
			} catch (Exception exception)
			{
				// ok, then don't
				logger.error("Error while updating ChemModel in stateChanged: ", exception.getMessage());
				logger.debug(exception);
			}
		}

		if (e.getSource() instanceof org.openscience.cdk.controller.Controller2D)
		{
			if (((Controller2D) e.getSource()).isUndoableChange())
			{
                logger.debug("Storing model on undo stack");
                Object clone = cloneModel(jcpm);
                if (clone == null) {
                    logger.warn("I will not store a null object onto the undo stack.");
                } else {
                    undoStack.push(clone);
                }
				((Controller2D)e.getSource()).setUndoableChange(false);
				stackStatus = "Stacksize: " + undoStack.size();
				logger.debug(statusBar.getStatus(2));
				logger.debug(stackStatus);
				statusBar.setStatus(3, stackStatus);
				
			}
		}
		if (jcpp != null)
		{
			jcpp.setPreferredSize(jcpm.getRendererModel().getBackgroundDimension());
			jcpp.revalidate();
			jcpp.repaint();
		}
	}


	/**
	 *  Clones a JChemPaintModel.
	 *
	 *@param  jcpm  JChemPaintModel
	 *@return       JChemPaintModel
	 */
	public JChemPaintModel cloneModel(JChemPaintModel jcpm)
	{
		Object o = null;

		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bo);
			os.writeObject(jcpm);
			bo.flush();
			os.close();
			os.reset();
			byte[] data = bo.toByteArray();

			ByteArrayInputStream bi = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bi);
			o = ois.readObject();
			ois.close();
		} catch (Exception exc) {
            logger.error("Could not clone this object: ", exc.getMessage());
			logger.debug(exc);
		}

		return (JChemPaintModel) o;
	}



	/**
	 *  Reconstructs the frame after the undo action.
	 */
	public void undo()
	{
		JChemPaintModel model = null;
		Renderer2DModel r2dm = null;
		if (undoStack.size() > 0)
		{
			undoStack.undoShift();
			model = cloneModel((JChemPaintModel) undoStack.getCurrent());
			logger.debug("Retrieved model");
			r2dm = model.getRendererModel();
			r2dm.setSelectedPart(new AtomContainer());
			r2dm.setHighlightedAtom(null);
			r2dm.setHighlightedBond(null);
			setModel(model);
			jcpp.repaint();
		}
	}


	/**
	 *  Reconstructs the frame after the redo action.
	 */
	public void redo()
	{
		JChemPaintModel model = null;
		Renderer2DModel r2dm = null;
		if (undoStack.size() > 0)
		{
			undoStack.redoShift();
			model = cloneModel((JChemPaintModel) undoStack.getCurrent());
			r2dm = model.getRendererModel();
			r2dm.setSelectedPart(new AtomContainer());
			r2dm.setHighlightedAtom(null);
			r2dm.setHighlightedBond(null);
			setModel(model);
			jcpp.repaint();
		}
	}
    
    public StatusBar getStatusBar() {
        return statusBar;
    }

    public StatusBar getDevelStatusBar() {
        return develStatusBar;
    }
}

