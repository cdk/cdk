/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2007  The JChemPaint project
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
package org.openscience.cdk.applications.jchempaint;

import java.util.EventObject;
import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 *  The model on which JChemPaint opterates. It holds all the models of the
 *  different components used in JChemPaint and provides some methods for their
 *  interoperability.
 *
 * @author        steinbeck
 * @cdk.created       2003-02-17
 * @cdk.module    jchempaint
 * @cdk.svnrev  $Revision$
 */
public class JChemPaintModel implements java.io.Serializable, ICDKChangeListener {

	private static final long serialVersionUID = -6072472241802965261L;
	/**  Description of the Field */
	protected transient EventListenerList changeListeners = new EventListenerList();
	private String title;
	private String[] status = new String[3];
	private String lastAction;
	private String author;
	private String software;
	private String gendate;

	private boolean isModified = false;

	/**  The Model that contains the actual chemistry */
	private IChemModel model;

	/**
	 *  The model for the renderer (which atoms are highlighted, etc...), taken
	 *  from the Chemistry Development Kit (http://cdk.sourceforge.net)
	 */
	private Renderer2DModel rendererModel;

	private Controller2DModel controllerModel;

	/**  Creates an empty JChemPaintModel */
	public JChemPaintModel() {
		this(new org.openscience.cdk.ChemModel());
	}

	/**
	 *  Creates a new JChemPaintModel that contains a ChemModel
	 *
	 * @param  chemModel
	 */
	public JChemPaintModel(IChemModel chemModel) {
		Object modelTitle = chemModel.getProperty(CDKConstants.TITLE);
		if (modelTitle == null) {
			title = JCPLocalizationHandler.getInstance().getString("Untitled-") +
					System.currentTimeMillis();
		}
		else {
			title = modelTitle.toString();
		}
		this.model = chemModel;
		controllerModel = new Controller2DModel();
		rendererModel = new Renderer2DModel();
	}

	/**
	 *  If a model has been restored after an Undo/Redo Operation it should fire a
	 *  change event so that everyone can read it's state.
	 */
	public void activate() {
		fireChange();
	}


	/**
	 *  Sets the status String of the last action.
	 *
	 * @param  s  String
	 */
	public void setLastAction(String s) {
		lastAction = s;
		status[1] = s;
	}


	/**
	 *  Returns a ChangeListener object.
	 *
	 * @return    EventListenerList
	 */
	public EventListenerList getChangeListeners() {
		return changeListeners;
	}


	/**
	 *  Sets a ChangeListener.
	 *
	 * @param  changeListeners
	 */
	public void setChangeListeners(EventListenerList changeListeners) {
		this.changeListeners = changeListeners;
	}


	/*
	 *  Listener notification support methods START here
	 */
	/**
	 *  Adds a ChangeListener.
	 *
	 * @param  x  ChangeListener
	 */
	public void addChangeListener(ChangeListener x) {
		if (changeListeners == null) {
			changeListeners = new EventListenerList();
		}
		changeListeners.add(ChangeListener.class, x);
		// bring it up to date with current state
		x.stateChanged(new ChangeEvent(this));
	}


	/**
	 *  Removes a ChangeListener.
	 *
	 * @param  x  ChangeListener
	 */
	public void removeChangeListener(ChangeListener x) {
		changeListeners.remove(ChangeListener.class, x);
	}


	/**
	 *  Notifies registered listeners of certain changes that have occurred in this
	 *  model.
	 *
	 * @param  source  Description of the Parameter
	 */
	public void fireChange(Object source) {
		// logger.debug("Firering change");

		// Create the event:
		ChangeEvent c = new ChangeEvent(source);
		// Get the listener list
		if (changeListeners == null) {
			changeListeners = new EventListenerList();
		}

		Object[] listeners = changeListeners.getListenerList();
		// Process the listeners last to first
		// List is in pairs, Class and instance
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				ChangeListener cl = (ChangeListener) listeners[i + 1];
				cl.stateChanged(c);
			}
		}
	}

	/**
	 *  Notifies registered listeners of certain changes that have occurred in this
	 *  model.
	 */
	public void fireChange() {
		fireChange(this);
	}

	/*
	 *  Listener notification support methods END here
	 */

	/**
	 *  Returns one of the status strings at the given position
	 *
	 * @param  position
	 * @return the current status
	 */
	public String getStatus(int position) {
		// return this.status[position];
		String status = "";
		// logger.debug("Getting status");
		if (position == 0) {
			// depict editing mode
			status = controllerModel.getDrawModeString();
		}
		else if (position == 1) {
			// depict bruto formula
			IAtomContainer wholeModel = model.getBuilder().newAtomContainer();
        	Iterator containers = ChemModelManipulator.getAllAtomContainers(model).iterator();
        	while (containers.hasNext()) {
        		wholeModel.add((IAtomContainer)containers.next());
        	}
			String formula = new MFAnalyser(wholeModel,true).getHTMLMolecularFormulaWithCharge();
			int impliciths=0;
			for(int i=0;i<wholeModel.getAtomCount();i++){
				if(wholeModel.getAtom(i).getHydrogenCount()==null)
					wholeModel.getAtom(i).setHydrogenCount(new Integer(0));
				impliciths+=wholeModel.getAtom(i).getHydrogenCount().intValue();
			}
			status = "<html>" + formula + (impliciths==0 ? "" : " (of these "+impliciths+" Hs implicit)")+"</html>";
		}
		else if (position == 2) {
			// depict brutto formula of the selected molecule or part of molecule
			if (rendererModel.getSelectedPart() != null) {
				IAtomContainer selectedPart = rendererModel.getSelectedPart();
				String formula = new MFAnalyser(selectedPart,true).getHTMLMolecularFormulaWithCharge();
				status = "<html>" + formula + "</html>";
			}
		}
		return status;
	}


	/**
	 *  Sets one of the status strings at the given position
	 *
	 * @param  status
	 * @param  position
	 */
	public void setStatus(String status, int position) {
		this.status[position] = status;
	}


	/**
	 *  Sets a title for this model.
	 *
	 * @param  text  The string containing the title
	 */
	public void setTitle(String text) {
		title = text;
//        setLastAction("Title changed");
//        fireChange();
	}


	/**
	 *  returns the title of this model
	 *
	 * @return    the title of this model
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Returns true if the content of this model is modified since the last save.
	 *
	 * @return    The modified value
	 */
	public boolean isModified() {
		return this.isModified;
	}

	/** Resets the state of this model to unmodified. */
	public void resetIsModified() {
		this.isModified = false;
	}

	/**
	 * @return the generation date
	 */
	public String getGendate() {
		return this.gendate;
	}


	/**
	 * @param  gendate
	 */
	public void setGendate(String gendate) {
		this.gendate = gendate;
	}


	/**
	 * @return The Software value
	 */
	public String getSoftware() {
		return this.software;
	}


	/**
	 * @param  software
	 */
	public void setSoftware(String software) {
		this.software = software;
	}


	/**
	 * @return the author of this model
	 */
	public String getAuthor() {
		return this.author;
	}


	/**
	 * @param  author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	public void setChemModel(IChemModel chemModel) {
		this.model = chemModel;
		fireChange();
	}
	
	/**
	 *  Returns a ChemModel for this JChemPaintModel
	 *
	 * @return the current ChemModel
	 */
	public IChemModel getChemModel() {
		return model;
	}

	/**
	 *  Returns the ControllerModel
	 *
	 * @return the current ControllerModel
	 */
	public Controller2DModel getControllerModel() {
		return this.controllerModel;
	}


	/**
	 *  Sets the ControllerModel
	 *
	 * @param  controllerModel
	 */
	public void setControllerModel(Controller2DModel controllerModel) {
		this.controllerModel = controllerModel;
	}


	/**
	 *  Returns the RendererModel
	 *
	 * @return the current Renderer2DModel
	 */
	public Renderer2DModel getRendererModel() {
		return this.rendererModel;
	}


	/**
	 *  Sets the RendererModel
	 *
	 * @param  rendererModel
	 */
	public void setRendererModel(Renderer2DModel rendererModel) {
		this.rendererModel = rendererModel;
	}


	/**
	 *  Method to notify this CDKChangeListener if something has changed in another
	 *  object
	 *
	 * @param  e  The EventObject containing information on the nature and source of
	 *      the event
	 */
	public void stateChanged(EventObject e) {
		try {
			// logger.debug("State Change: " + ac.toString());
		} catch (Exception ex) {
			System.out.println(ex.toString());
			ex.printStackTrace();
		}
		fireChange(e.getSource());

		isModified = true;
	}

	public String getLastAction() {
		return lastAction;
	}
}

