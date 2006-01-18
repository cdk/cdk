/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
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
 *
 */
package org.openscience.cdk.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Mapping;
import org.openscience.cdk.applications.undoredo.AddAtomsAndBondsEdit;
import org.openscience.cdk.applications.undoredo.BondChangeEdit;
import org.openscience.cdk.applications.undoredo.ChangeAtomSymbolEdit;
import org.openscience.cdk.applications.undoredo.ChangeChargeEdit;
import org.openscience.cdk.applications.undoredo.RemoveAtomsAndBondsEdit;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.event.CDKChangeListener;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.Isotope;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.Reaction;
import org.openscience.cdk.interfaces.Ring;
import org.openscience.cdk.interfaces.SetOfMolecules;
import org.openscience.cdk.layout.AtomPlacer;
import org.openscience.cdk.layout.RingPlacer;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.SetOfMoleculesManipulator;

/**
 *  Class that acts on MouseEvents and KeyEvents.
 *
 *@author         steinbeck
 *@author         egonw
 *@cdk.created    2. Mai 2005
 *@cdk.keyword    mouse events
 *@cdk.require    java1.4+
 */
 abstract class AbstractController2D implements MouseMotionListener, MouseListener, KeyListener
{

	private final static int DRAG_UNSET = 0;
	private final static int DRAG_MOVING_SELECTED = 1;
	private final static int DRAG_DRAWING_PROPOSED_BOND = 2;
	private final static int DRAG_DRAWING_PROPOSED_RING = 3;
	private final static int DRAG_MAKING_SQUARE_SELECTION = 4;
	private final static int DRAG_MAKING_LASSO_SELECTION = 5;
	private final static int DRAG_DRAWING_PROPOSED_ATOMATOMMAP = 6;
	
	protected Vector lastAction=null;
	protected JButton moveButton=null;
	
	IAtomContainer atomContainer;
	protected ChemModel chemModel;
	
	Renderer2DModel r2dm;
	Controller2DModel c2dm;
	boolean wasDragged = false;
	boolean isUndoableChange = false;

	private Vector listeners = new Vector();

	private LoggingTool logger;

	private int prevDragCoordX = 0;
	private int prevDragCoordY = 0;
	private boolean draggingSelected = true;

	private int dragMode = DRAG_UNSET;

	private Vector commonElements;
	private HashMap currentCommonElement = new HashMap();
	IAtom lastAtomInRange = null;
	private double shiftX = 0;
	private double shiftY = 0;

	// Helper classes
	HydrogenAdder hydrogenAdder = new HydrogenAdder("org.openscience.cdk.tools.ValencyChecker");


	AbstractController2D()
	{
		logger = new LoggingTool(this);

	}

	AbstractController2D(Controller2DModel c2dm)
	{
		this();
		this.c2dm = c2dm;
		commonElements = new Vector();
		String[] elements = c2dm.getCommonElements();
		for (int i = 0; i < elements.length; i++)
		{
			commonElements.add(elements[i]);
		}

	}

	AbstractController2D(Renderer2DModel r2dm, Controller2DModel c2dm)
	{
		this(c2dm);
		this.r2dm = r2dm;
	}

	
	
	/**
	 *  Gets the controller2DModel attribute of the Controller2D object
	 *
	 *@return    The controller2DModel value
	 */
	public Controller2DModel getController2DModel()
	{
		return c2dm;
	}


	/**
	 *  Gets the undoableChange attribute of the Controller2D object
	 *
	 *@return    The undoableChange value
	 */
	public boolean isUndoableChange()
	{
		return isUndoableChange;
	}


	/**
	 *  Sets the undoableChange attribute of the Controller2D object
	 *
	 *@param  isUndoable  The new undoableChange value
	 */
	public void setUndoableChange(boolean isUndoable)
	{
		this.isUndoableChange = isUndoable;
	}


	/**
	 *  Sets the controller2DModel attribute of the Controller2D object
	 *
	 *@param  model  The new controller2DModel value
	 */
	public void setController2DModel(Controller2DModel model)
	{
		this.c2dm = model;
	}


	/**
	 *  Manages all actions that will be invoked when the mouse is moved.
	 *
	 *@param  event  MouseEvent object
	 */
	public void mouseMoved(MouseEvent event)
	{
		int[] screenCoords = {event.getX(), event.getY()};
		int[] mouseCoords = getWorldCoordinates(screenCoords);
		int mouseX = mouseCoords[0];
		int mouseY = mouseCoords[1];
		highlightNearestChemObject(mouseX, mouseY);
	}


	/**
	 *  Manages all actions that will be invoked when the mouse is dragged.
	 *
	 *@param  event  MouseEvent object
	 */
	public void mouseDragged(MouseEvent event)
	{
		isUndoableChange = false;
		logger.debug("MouseDragged Event Props: mode=", c2dm.getDrawModeString());
		if (logger.isDebugEnabled())
		{
			logger.debug("   trigger=" + event.isPopupTrigger() +
			/*
			 *  ", Button number: " + event.getButton() +
			 */
					", Click count: " + event.getClickCount());
		}

		int[] screenCoords = {event.getX(), event.getY()};
		int[] mouseCoords = getWorldCoordinates(screenCoords);
		int mouseX = mouseCoords[0];
		int mouseY = mouseCoords[1];

		if (!wasDragged)
		{
			prevDragCoordX = mouseX;
			prevDragCoordY = mouseY;
			wasDragged = true;
		}

		if (dragMode == DRAG_DRAWING_PROPOSED_BOND)
		{
			int startX = r2dm.getPointerVectorStart().x;
			int startY = r2dm.getPointerVectorStart().y;
			highlightNearestChemObject(mouseX, mouseY);
			drawProposedBond(startX, startY, mouseX, mouseY);
		} else if (dragMode == DRAG_MAKING_SQUARE_SELECTION)
		{
			int startX = r2dm.getPointerVectorStart().x;
			int startY = r2dm.getPointerVectorStart().y;
			selectRectangularArea(startX, startY, mouseX, mouseY);
		} else if (dragMode == DRAG_DRAWING_PROPOSED_RING)
		{
			int endX = 0;
			int endY = 0;
			double angle = 0;
			double pointerVectorLength = c2dm.getRingPointerLength();
			Point2d center = GeometryTools.get2DCenter(getHighlighted());
			r2dm.setPointerVectorStart(new Point((int) center.x, (int) center.y));
			angle = GeometryTools.getAngle(center.x - mouseX, center.y - mouseY);
			endX = (int) center.x - (int) (Math.cos(angle) * pointerVectorLength);
			endY = (int) center.y - (int) (Math.sin(angle) * pointerVectorLength);
			r2dm.setPointerVectorEnd(new Point(endX, endY));
		} else if (dragMode == DRAG_MAKING_LASSO_SELECTION)
		{
			/*
			 *  Draw polygon in screencoordinates, convert them
			 *  to world coordinates when mouse released
			 */
			r2dm.addLassoPoint(new Point(event.getX(), event.getY()));
		} else if (dragMode == DRAG_MOVING_SELECTED)
		{
			// all these are in model coordinates
			logger.debug("Dragging selected atoms");
			int deltaX = mouseX - prevDragCoordX;
			int deltaY = mouseY - prevDragCoordY;
			moveSelectedAtomsWith(deltaX, deltaY);
			IAtomContainer selected=r2dm.getSelectedPart();
			r2dm.getMerge().clear();
			for(int i=0;i<selected.getAtomCount();i++){
				IAtom inrange=getAtomInRange((int)selected.getAtomAt(i).getPoint2d().x, (int)selected.getAtomAt(i).getPoint2d().y, selected.getAtomAt(i));
				if(inrange!=null && inrange!=selected.getAtomAt(i)){
					r2dm.getMerge().put(selected.getAtomAt(i),inrange);
				}
			}
			/*
			 *  PRESERVE THIS. This notifies the
			 *  the listener responsible for
			 *  undo and redo storage that it
			 *  should not store this change
			 */
			isUndoableChange = false;
			fireChange();
		}

		// make note of current coordinates for next DraggedEvent
		prevDragCoordX = mouseX;
		prevDragCoordY = mouseY;
	}


	/**
	 *  manages all actions that will be invoked when a mouse button is pressed
	 *
	 *@param  event  MouseEvent object
	 */
	public void mousePressed(MouseEvent event)
	{
		isUndoableChange = false;
		int[] screenCoords = {event.getX(), event.getY()};
		int[] mouseCoords = getWorldCoordinates(screenCoords);
		int mouseX = mouseCoords[0];
		int mouseY = mouseCoords[1];

		logger.debug("MousePressed Event Props: mode=", c2dm.getDrawModeString());
		if (logger.isDebugEnabled())
		{
			logger.debug("   trigger=" + event.isPopupTrigger() +
			/*
			 *  ", Button number: " + event.getButton() +
			 */
					", Click count: " + event.getClickCount());
		}

		int startX = 0;
		int startY = 0;
		r2dm.setPointerVectorStart(null);
		r2dm.setPointerVectorEnd(null);
		IAtom atomInRange = getAtomInRange(mouseX, mouseY);
		Bond bondInRange = getBondInRange(mouseX, mouseY);
		if (atomInRange != null)
		{
			startX = (int) atomInRange.getX2d();
			startY = (int) atomInRange.getY2d();
			r2dm.setPointerVectorStart(new Point(startX, startY));
		} else
		{
			r2dm.setPointerVectorStart(new Point(mouseX, mouseY));
		}
		
		if(r2dm.getSelectedPart()!=null && !(r2dm.getSelectedPart().contains(atomInRange) || r2dm.getSelectedPart().contains(bondInRange))){
			r2dm.setSelectedPart(new org.openscience.cdk.AtomContainer());
		}

		if (c2dm.getDrawMode() == Controller2DModel.MOVE)
		{
			selectNearestChemObjectIfNoneSelected(mouseX, mouseY);
			dragMode = DRAG_MOVING_SELECTED;
		} else if (c2dm.getDrawMode() == Controller2DModel.DRAWBOND || c2dm.getDrawMode() == Controller2DModel.DOWN_BOND || c2dm.getDrawMode() == Controller2DModel.UP_BOND)
		{
			if (bondInRange != null && atomInRange == null)
			{
				// make sure we are not dragging a bond
			} else
			{
				dragMode = DRAG_DRAWING_PROPOSED_BOND;
				lastAtomInRange = atomInRange;
			}
		} else if (c2dm.getDrawMode() == Controller2DModel.MAPATOMATOM)
		{
			dragMode = DRAG_DRAWING_PROPOSED_ATOMATOMMAP;
		} else if (c2dm.getDrawMode() == Controller2DModel.SELECT)
		{
			dragMode = DRAG_MAKING_SQUARE_SELECTION;
		} else if (c2dm.getDrawMode() == Controller2DModel.LASSO)
		{
			if(r2dm.getSelectedPart()!=null && (r2dm.getSelectedPart().contains(r2dm.getHighlightedAtom()) || r2dm.getSelectedPart().contains(r2dm.getHighlightedBond()))){
				if(r2dm.getSelectedPart().getAtomCount()>0)
					c2dm.setDrawMode(Controller2DModel.MOVE);
				((JButton)lastAction.get(0)).setBackground(Color.LIGHT_GRAY);
				lastAction.set(0,moveButton);
				moveButton.setBackground(Color.GRAY);
				dragMode = DRAG_MOVING_SELECTED;
			}else{
				dragMode = DRAG_MAKING_LASSO_SELECTION;
			}
		} else if (c2dm.getDrawMode() == Controller2DModel.RING ||
				c2dm.getDrawMode() == Controller2DModel.BENZENERING)
		{
			dragMode = DRAG_DRAWING_PROPOSED_RING;
		}			
	}


	/**
	 *  manages all actions that will be invoked when a mouse button is released
	 *
	 *@param  event  MouseEvent object
	 */
	public void mouseReleased(MouseEvent event)
	{
		isUndoableChange = false;
		logger.debug("MouseReleased Event Props: mode=", c2dm.getDrawModeString());
		if (logger.isDebugEnabled())
		{
			logger.debug("   trigger=" + event.isPopupTrigger(),
					", Click count: " + event.getClickCount());
		}
		if ((event.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
		{
			int[] screenCoords = {event.getX(), event.getY()};
			int[] mouseCoords = getWorldCoordinates(screenCoords);
			int mouseX = mouseCoords[0];
			int mouseY = mouseCoords[1];
			if (c2dm.getDrawMode() == Controller2DModel.SYMBOL)
			{

				IAtom atomInRange = r2dm.getHighlightedAtom();
				if (atomInRange != null)
				{
					if (currentCommonElement.get(atomInRange) == null)
					{
						currentCommonElement.put(atomInRange, new Integer(1));
					}
					int oldCommonElement = ((Integer) currentCommonElement.get(atomInRange)).intValue();
					String symbol = (String) commonElements.elementAt(oldCommonElement);
					if (!(atomInRange.getSymbol().equals(symbol)))
					{
						// only change symbol if needed
                        String formerSymbol = atomInRange.getSymbol();
						atomInRange.setSymbol(symbol);
						// configure the atom, so that the atomic number matches the symbol
						try
						{
							IsotopeFactory.getInstance(atomInRange.getBuilder()).configure(atomInRange);
						} catch (Exception exception)
						{
							logger.error("Error while configuring atom");
							logger.debug(exception);
						}
						// update atom
						IAtomContainer container = getRelevantAtomContainer(chemModel, atomInRange);
						updateAtom(container, atomInRange);

						/*
						 *  PRESERVE THIS. This notifies the
						 *  the listener responsible for
						 *  undo and redo storage that it
						 *  should store this change of an atom symbol
						 */
						isUndoableChange = true;
						/*
						 *  ---
						 */
                        UndoableEdit  edit = new ChangeAtomSymbolEdit(atomInRange, formerSymbol, symbol);
                        c2dm.getUndoSupport().postEdit(edit);
						r2dm.fireChange();
						fireChange();
					}
					oldCommonElement++;
					if (oldCommonElement == commonElements.size())
					{
						oldCommonElement = 0;
					}
					currentCommonElement.put(atomInRange, new Integer(oldCommonElement));
				}
			}
			if (c2dm.getDrawMode() == Controller2DModel.ELEMENT)
			{
				IAtom atomInRange = r2dm.getHighlightedAtom();
				if (atomInRange != null)
				{
					String symbol = c2dm.getDrawElement();
					if (!(atomInRange.getSymbol().equals(symbol)))
					{
						// only change symbol if needed
						String formerSymbol = atomInRange.getSymbol();
						atomInRange.setSymbol(symbol);
						// configure the atom, so that the atomic number matches the symbol
						try
						{
							IsotopeFactory.getInstance(atomInRange.getBuilder()).configure(atomInRange);
						} catch (Exception exception)
						{
							logger.error("Error while configuring atom");
							logger.debug(exception);
						}
						// update atom
						IAtomContainer container = getRelevantAtomContainer(chemModel, atomInRange);
						updateAtom(container, atomInRange);

						/*
						 *  PRESERVE THIS. This notifies the
						 *  the listener responsible for
						 *  undo and redo storage that it
						 *  should store this change of an atom symbol
						 */
//						isUndoableChange = true;
						/*
						 *  ---
						 */
						// undoredo support
            UndoableEdit  edit = new ChangeAtomSymbolEdit(atomInRange, formerSymbol, symbol);
						c2dm.getUndoSupport().postEdit(edit);
						r2dm.fireChange();
						fireChange();
					}
				}else{
					int startX = r2dm.getPointerVectorStart().x;
					int startY = r2dm.getPointerVectorStart().y;
					IAtom newAtom1 = new org.openscience.cdk.Atom(c2dm.getDrawElement(), new Point2d(startX, startY));
					IAtomContainer atomCon = ChemModelManipulator.createNewMolecule(chemModel);
					atomCon.addAtom(newAtom1);
					// update atoms
					updateAtom(atomCon, newAtom1);
					chemModel.getSetOfMolecules().addAtomContainer(atomCon);
					//FIXME undoredo
					r2dm.fireChange();
					fireChange();
				}
			}

			if (c2dm.getDrawMode() == Controller2DModel.INCCHARGE)
			{
				IAtom atomInRange = r2dm.getHighlightedAtom();
				if (atomInRange != null)
				{
					int formerCharge = atomInRange.getFormalCharge();
					atomInRange.setFormalCharge(atomInRange.getFormalCharge() + 1);

					// update atom
					IAtomContainer container = getRelevantAtomContainer(chemModel, atomInRange);
					updateAtom(container, atomInRange);
					//undoredo support
                    UndoableEdit  edit = new ChangeChargeEdit(atomInRange, formerCharge, atomInRange.getFormalCharge());
					c2dm.getUndoSupport().postEdit(edit);
					r2dm.fireChange();
					fireChange();
				}
			}
			if (c2dm.getDrawMode() == Controller2DModel.ENTERELEMENT)
			{
				IAtom atomInRange = r2dm.getHighlightedAtom();
				if (atomInRange != null)
				{
					String x=JOptionPane.showInputDialog(null,"Enter new element symbol");
					try{
						if(Character.isLowerCase(x.toCharArray()[0]))
							x=Character.toUpperCase(x.charAt(0))+x.substring(1);
						IsotopeFactory ifa=IsotopeFactory.getInstance(r2dm.getHighlightedAtom().getBuilder());
						Isotope iso=ifa.getMajorIsotope(x);
						String formerSymbol=r2dm.getHighlightedAtom().getSymbol();
						if(iso!=null)
							r2dm.getHighlightedAtom().setSymbol(x);
						// update atom
						IAtomContainer container = getRelevantAtomContainer(chemModel, atomInRange);
						updateAtom(container, atomInRange);

						/*
						 *  PRESERVE THIS. This notifies the
						 *  the listener responsible for
						 *  undo and redo storage that it
						 *  should store this change of an atom symbol
						 */
//						isUndoableChange = true;
						/*
						 *  ---
						 */
						// undoredo support
            UndoableEdit  edit = new ChangeAtomSymbolEdit(atomInRange, formerSymbol, x);
						c2dm.getUndoSupport().postEdit(edit);
						r2dm.fireChange();
						fireChange();
					}catch(Exception ex){
						logger.debug(ex.getMessage()+" in SELECTELEMENT");
					}
				}
			}
			if (c2dm.getDrawMode() == Controller2DModel.DECCHARGE)
			{
				IAtom atomInRange = r2dm.getHighlightedAtom();
				if (atomInRange != null)
				{
					int formerCharge = atomInRange.getFormalCharge();
					atomInRange.setFormalCharge(atomInRange.getFormalCharge() - 1);
					// update atom
					IAtomContainer container = getRelevantAtomContainer(chemModel, atomInRange);
					updateAtom(container, atomInRange);
					//undoredo support
                    UndoableEdit  edit = new ChangeChargeEdit(atomInRange, formerCharge, atomInRange.getFormalCharge());
					c2dm.getUndoSupport().postEdit(edit);
					r2dm.fireChange();
					fireChange();
				}
			}

			if (c2dm.getDrawMode() == Controller2DModel.MAPATOMATOM)
			{
				handleMapping(wasDragged, r2dm);
			}
      
			if (c2dm.getDrawMode() == Controller2DModel.DRAWBOND || c2dm.getDrawMode() == Controller2DModel.DOWN_BOND || c2dm.getDrawMode() == Controller2DModel.UP_BOND)
			{
        logger.debug("mouseReleased->drawbond");
				IAtom atomInRange;
				IAtom newAtom1 = null;
				IAtom newAtom2 = null;
				Bond newBond = null;
				int startX = r2dm.getPointerVectorStart().x;
				int startY = r2dm.getPointerVectorStart().y;
				Bond bondInRange = r2dm.getHighlightedBond();
				//atomInRange = r2dm.getHighlightedAtom();
				//Bond bondInRange = getBondInRange(mouseX, mouseY);
				/*
				 *  IMPORTANT: I don't use getHighlighteAtom()
				 *  here because of the special case of
				 *  only one atom on the screen.
				 *  In this case, this atom will not detected
				 *  if the mouse hasn't moved after it's creation
				 */
				atomInRange = getAtomInRange(mouseX, mouseY);
				if (bondInRange != null)
				{
					Bond formerBond = (Bond) bondInRange.clone();
					if (c2dm.getDrawMode() == Controller2DModel.DRAWBOND){
            // increase Bond order
            double order = bondInRange.getOrder();
            if (order >= CDKConstants.BONDORDER_TRIPLE)
            {
              bondInRange.setOrder(CDKConstants.BONDORDER_SINGLE);
            } else
            {
              bondInRange.setOrder(order + 1.0);
              // this is tricky as it depends on the fact that the
              // constants are unidistant, i.e. {1.0, 2.0, 3.0}.
            }
            ;
            // update atoms
            org.openscience.cdk.interfaces.IAtom[] atoms = bondInRange.getAtoms();
            IAtomContainer container = getRelevantAtomContainer(chemModel, atoms[0]);
            updateAtoms(container, atoms);
          }else if(c2dm.getDrawMode() == Controller2DModel.DOWN_BOND){
            // toggle bond stereo
            double stereo = bondInRange.getStereo();
            if (stereo == CDKConstants.STEREO_BOND_DOWN)
            {
              bondInRange.setStereo(CDKConstants.STEREO_BOND_DOWN_INV);
            } else if (stereo == CDKConstants.STEREO_BOND_DOWN_INV)
            {
              bondInRange.setStereo(CDKConstants.STEREO_BOND_NONE);
            } else
            {
              bondInRange.setStereo(CDKConstants.STEREO_BOND_DOWN);
            }
          }else{
            // toggle bond stereo
            double stereo = bondInRange.getStereo();
            if (stereo == CDKConstants.STEREO_BOND_UP)
            {
              bondInRange.setStereo(CDKConstants.STEREO_BOND_UP_INV);
            } else if (stereo == CDKConstants.STEREO_BOND_UP_INV)
            {
              bondInRange.setStereo(CDKConstants.STEREO_BOND_NONE);
            } else
            {
              bondInRange.setStereo(CDKConstants.STEREO_BOND_UP);
            }
          }           
					/*
					 *  PRESERVE THIS. This notifies the
					 *  the listener responsible for
					 *  undo and redo storage that it
					 *  should store this change of an atom symbol
					 */
					isUndoableChange = true;
					/*
					 *  ---
					 */
					UndoableEdit  edit = new BondChangeEdit(chemModel, formerBond, bondInRange);
					c2dm.getUndoSupport().postEdit(edit);
				} else
				{
					IAtomContainer undoRedoContainer = new org.openscience.cdk.AtomContainer();
					if (atomInRange != null)
					{
						logger.debug("We had an atom in range");
						newAtom1 = atomInRange;
					} else if (!wasDragged)
					{
						// create a new molecule
						logger.debug("We make a new molecule");
						newAtom1 = new org.openscience.cdk.Atom(c2dm.getDrawElement(), new Point2d(startX, startY));
						IAtomContainer atomCon = ChemModelManipulator.createNewMolecule(chemModel);
						atomCon.addAtom(newAtom1);
						// update atoms
						updateAtom(atomCon, newAtom1);
						undoRedoContainer.add(atomCon);
					}

					if (wasDragged)
					{
						if (dragMode == DRAG_DRAWING_PROPOSED_BOND)
						{
							int endX = r2dm.getPointerVectorEnd().x;
							int endY = r2dm.getPointerVectorEnd().y;
							atomInRange = getAtomInRange(endX, endY);
							IAtomContainer atomCon = getAllInOneContainer(chemModel);
							if (atomInRange != null)
							{
								logger.debug("*** atom in range");

								newAtom2 = atomInRange;
								logger.debug("atomCon.getAtomCount() " + atomCon.getAtomCount());
							} else
							{
								logger.debug("*** new atom");
								newAtom2 = new org.openscience.cdk.Atom(c2dm.getDrawElement(), new Point2d(endX, endY));
								atomCon.addAtom(newAtom2);
								undoRedoContainer.addAtom(newAtom2);
							}
							newAtom1 = lastAtomInRange;
							if (newAtom1 == null)
							{
								newAtom1 = new org.openscience.cdk.Atom(c2dm.getDrawElement(), new Point2d(r2dm.getPointerVectorStart().x, r2dm.getPointerVectorStart().y));
								undoRedoContainer.addAtom(newAtom1);
							}
							if (newAtom1 != newAtom2)
							{
								newBond = new org.openscience.cdk.Bond(newAtom1, newAtom2, 1);
                if(c2dm.getDrawMode() == Controller2DModel.UP_BOND)
                  newBond.setStereo(CDKConstants.STEREO_BOND_UP);
                if(c2dm.getDrawMode() == Controller2DModel.DOWN_BOND)
                  newBond.setStereo(CDKConstants.STEREO_BOND_DOWN);
                logger.debug(newAtom1 + " - " + newAtom2);
								atomCon.addBond(newBond);
								undoRedoContainer.addBond(newBond);
							}

							try
							{
								SetOfMolecules setOfMolecules = ConnectivityChecker.partitionIntoMolecules(atomCon);
								chemModel.setSetOfMolecules(setOfMolecules);
								logger.debug("We have " + setOfMolecules.getAtomContainerCount() + " molecules on screen");
							} catch (Exception exception)
							{
								logger.warn("Could not partition molecule: ", exception.getMessage());
								logger.debug(exception);
								return;
							}

							// update atoms
							updateAtom(atomCon, newAtom2);

							/*
							 *  PRESERVE THIS. This notifies the
							 *  the listener responsible for
							 *  undo and redo storage that it
							 *  should store this change of an atom symbol
							 */
							isUndoableChange = true;
							/*
							 *  ---
							 */
						}
					} else if (atomInRange != null)
					{
						// add a new atom to the current atom in some random
						// direction
						IAtomContainer atomCon = getRelevantAtomContainer(chemModel, atomInRange);
						newAtom2 = new org.openscience.cdk.Atom(c2dm.getDrawElement(), atomInRange.getPoint2d());

						// now create 2D coords for new atom
						double bondLength = r2dm.getBondLength();
						IAtom[] connectedAtoms = atomCon.getConnectedAtoms(atomInRange);
						logger.debug("connectedAtoms.length: " + connectedAtoms.length);
						IAtomContainer placedAtoms = new org.openscience.cdk.AtomContainer();
						//placedAtoms.addAtom(atomInRange);
						for (int i = 0; i < connectedAtoms.length; i++)
						{
							placedAtoms.addAtom(connectedAtoms[i]);
						}
						IAtomContainer unplacedAtoms = new org.openscience.cdk.AtomContainer();
						unplacedAtoms.addAtom(newAtom2);
						AtomPlacer atomPlacer = new AtomPlacer();
						atomPlacer.setMolecule(new org.openscience.cdk.Molecule(atomCon));
						Point2d center2D = GeometryTools.get2DCenter(placedAtoms);
						logger.debug("placedAtoms.getAtomCount(): " + placedAtoms.getAtomCount());
						logger.debug("unplacedAtoms.getAtomCount(): " + unplacedAtoms.getAtomCount());
						if (placedAtoms.getAtomCount() == 1)
						{
							Vector2d bondVector = atomPlacer.getNextBondVector(
									atomInRange, placedAtoms.getAtomAt(0), 
									GeometryTools.get2DCenter(new org.openscience.cdk.Molecule(atomCon)),
									false // FIXME: is this correct? (see SF bug #1367002)
							);
							Point2d atomPoint = new Point2d(atomInRange.getPoint2d());
							bondVector.normalize();
							bondVector.scale(bondLength);
							atomPoint.add(bondVector);
							newAtom2.setPoint2d(atomPoint);

						} else
						{
							atomPlacer.distributePartners(atomInRange, placedAtoms, center2D,
									unplacedAtoms, bondLength);
						}

						// now add the new atom
						atomCon.addAtom(newAtom2);
						undoRedoContainer.addAtom(newAtom2);
						newBond=new org.openscience.cdk.Bond(atomInRange, newAtom2, 1.0);
						atomCon.addBond(newBond);
						undoRedoContainer.addBond(newBond);
						if(c2dm.getDrawMode() == Controller2DModel.UP_BOND)
							newBond.setStereo(CDKConstants.STEREO_BOND_UP);
						if(c2dm.getDrawMode() == Controller2DModel.DOWN_BOND)
							newBond.setStereo(CDKConstants.STEREO_BOND_DOWN);
						// update atoms
						updateAtom(atomCon, atomInRange);
						updateAtom(atomCon, newAtom2);
					}
					UndoableEdit  edit = new AddAtomsAndBondsEdit(chemModel, undoRedoContainer, "Add Bond");
					c2dm.getUndoSupport().postEdit(edit);
				}
				r2dm.fireChange();
				fireChange();
				centerAtom(newAtom1,chemModel);
				centerAtom(newAtom2,chemModel);
			}

			if (c2dm.getDrawMode() == Controller2DModel.SELECT && wasDragged)
			{
				logger.info("User asks to selected atoms");
				IAtomContainer selectedPart = new org.openscience.cdk.AtomContainer();
				r2dm.setSelectedPart(selectedPart);
				r2dm.setSelectedPart(getContainedAtoms(r2dm.getSelectRect()));
				r2dm.setSelectRect(null);
				logger.debug("selected stuff  ", selectedPart);
			}

			if (c2dm.getDrawMode() == Controller2DModel.ERASER)
			{
				IAtomContainer undoRedoContainer = new org.openscience.cdk.AtomContainer();
				String type = null;
				IAtom highlightedAtom = r2dm.getHighlightedAtom();
				Bond highlightedBond = r2dm.getHighlightedBond();
				if (highlightedAtom != null)
				{
					logger.info("User asks to delete an Atom");
					IAtomContainer container = getAllInOneContainer(chemModel);
					logger.debug("Atoms before delete: ", container.getAtomCount());
					ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel, highlightedAtom);
					undoRedoContainer.addAtom(highlightedAtom);
					if (type == null) {
						type = "Remove Atom";
					}
					else {
						type = "Remove Substructure";
					}
					container = getAllInOneContainer(chemModel);
					logger.debug("Atoms before delete: ", container.getAtomCount());
					// update atoms
					IAtom[] atoms = container.getConnectedAtoms(highlightedAtom);
					if (atoms.length > 0)
					{
						IAtomContainer atomCon = getRelevantAtomContainer(chemModel, atoms[0]);
						updateAtoms(atomCon, atoms);
					}
				} else if (highlightedBond != null)
				{
					logger.info("User asks to delete a Bond");
					ChemModelManipulator.removeElectronContainer(chemModel, highlightedBond);
					undoRedoContainer.addBond(highlightedBond);
					if (type == null) {
						type = "Remove Bond";
					}
					else {
						type = "Remove Substructure";
					}
					// update atoms
					org.openscience.cdk.interfaces.IAtom[] atoms = highlightedBond.getAtoms();
					IAtomContainer container = getRelevantAtomContainer(chemModel, atoms[0]);
					updateAtoms(container, atoms);
				} else
				{
					logger.warn("Cannot deleted if nothing is highlighted");
					return;
				}
				/*
				 *  PRESERVE THIS. This notifies the
				 *  the listener responsible for
				 *  undo and redo storage that it
				 *  should store this change of an atom symbol
				 */
				isUndoableChange = true;
				/*
				 *  ---
				 */
				UndoableEdit  edit = new RemoveAtomsAndBondsEdit(chemModel, undoRedoContainer, type);
				c2dm.getUndoSupport().postEdit(edit);
				r2dm.fireChange();
				fireChange();
			}

			if (c2dm.getDrawMode() == Controller2DModel.RING || c2dm.getDrawMode() == Controller2DModel.BENZENERING)
			{
				IAtomContainer undoRedoContainer = new org.openscience.cdk.AtomContainer();
				Ring newRing = null;
				Point2d sharedAtomsCenter;
				Vector2d ringCenterVector;
				double bondLength;
				int pointerMarkX;
				int pointerMarkY;

				double ringRadius;

				double angle;

				double xDiff;

				double yDiff;

				double distance1 = 0;

				double distance2 = 0;
				IAtom firstAtom;
				IAtom secondAtom;
				IAtom spiroAtom;
				Point2d conAtomsCenter = null;
				Point2d newPoint1;
				Point2d newPoint2;

				RingPlacer ringPlacer = new RingPlacer();
				int ringSize = c2dm.getRingSize();
				String symbol = c2dm.getDrawElement();
				IAtomContainer sharedAtoms = getHighlighted();

				if (sharedAtoms.getAtomCount() == 0)
				{
					sharedAtoms = new org.openscience.cdk.AtomContainer();
					newRing = new org.openscience.cdk.Ring(ringSize, symbol);
					if (c2dm.getDrawMode() == Controller2DModel.BENZENERING)
					{
						// make newRing a benzene ring
						Bond[] bonds = newRing.getBonds();
						bonds[0].setOrder(2.0);
						bonds[2].setOrder(2.0);
						bonds[4].setOrder(2.0);
						makeRingAromatic(newRing);
					}
					bondLength = r2dm.getBondLength();
					ringRadius = (bondLength / 2) / Math.sin(Math.PI / c2dm.getRingSize());
					sharedAtomsCenter = new Point2d(mouseX, mouseY - ringRadius);
					firstAtom = newRing.getAtomAt(0);
					firstAtom.setPoint2d(sharedAtomsCenter);
					sharedAtoms.addAtom(firstAtom);
					ringCenterVector = new Vector2d(new Point2d(mouseX, mouseY));
					ringCenterVector.sub(sharedAtomsCenter);
					ringPlacer.placeSpiroRing(newRing, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
					IAtomContainer atomCon = ChemModelManipulator.createNewMolecule(chemModel);
					atomCon.add(newRing);
					undoRedoContainer.add(newRing);
				} else if (sharedAtoms.getAtomCount() == 1)
				{
					spiroAtom = sharedAtoms.getAtomAt(0);
					sharedAtomsCenter = GeometryTools.get2DCenter(sharedAtoms);
					newRing = createAttachRing(sharedAtoms, ringSize, symbol);
					if (c2dm.getDrawMode() == Controller2DModel.BENZENERING)
					{
						// make newRing a benzene ring
						Bond[] bonds = newRing.getBonds();
						bonds[0].setOrder(2.0);
						bonds[2].setOrder(2.0);
						bonds[4].setOrder(2.0);
						makeRingAromatic(newRing);
					}
					bondLength = r2dm.getBondLength();
					conAtomsCenter = getConnectedAtomsCenter(sharedAtoms);
					if (conAtomsCenter.equals(spiroAtom.getPoint2d()))
					{
						ringCenterVector = new Vector2d(0, 1);
					} else
					{
						ringCenterVector = new Vector2d(sharedAtomsCenter);
						ringCenterVector.sub(conAtomsCenter);
					}
					ringPlacer.placeSpiroRing(newRing, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
					// removes the highlighed atom from the ring to add only the new placed
					// atoms to the AtomContainer.
					try
					{
						newRing.removeAtom(spiroAtom);
					} catch (Exception exc)
					{
						logger.error("Could not remove atom from ring");
						logger.debug(exc);
					}
					IAtomContainer atomCon = getRelevantAtomContainer(chemModel, spiroAtom);
					atomCon.add(newRing);
					undoRedoContainer.add(newRing);
				} else if (sharedAtoms.getAtomCount() == 2)
				{
					sharedAtomsCenter = GeometryTools.get2DCenter(sharedAtoms);

					// calculate two points that are perpendicular to the highlighted bond
					// and have a certain distance from the bondcenter
					firstAtom = sharedAtoms.getAtomAt(0);
					secondAtom = sharedAtoms.getAtomAt(1);
					xDiff = secondAtom.getX2d() - firstAtom.getX2d();
					yDiff = secondAtom.getY2d() - firstAtom.getY2d();
					bondLength = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
					angle = GeometryTools.getAngle(xDiff, yDiff);
					newPoint1 = new Point2d((Math.cos(angle + (Math.PI / 2)) * bondLength / 4) + sharedAtomsCenter.x, (Math.sin(angle + (Math.PI / 2)) * bondLength / 4) + sharedAtomsCenter.y);
					newPoint2 = new Point2d((Math.cos(angle - (Math.PI / 2)) * bondLength / 4) + sharedAtomsCenter.x, (Math.sin(angle - (Math.PI / 2)) * bondLength / 4) + sharedAtomsCenter.y);

					if (wasDragged)
					{
						// check which one of the two points is nearest to the endpoint of the pointer
						// vector that was dragged to make the ringCenterVector point into the right direction.
						pointerMarkX = r2dm.getPointerVectorEnd().x;
						pointerMarkY = r2dm.getPointerVectorEnd().y;
						distance1 = -1 * (Math.sqrt(Math.pow(newPoint1.x - pointerMarkX, 2) + Math.pow(newPoint1.y - pointerMarkY, 2)));
						distance2 = -1 * (Math.sqrt(Math.pow(newPoint2.x - pointerMarkX, 2) + Math.pow(newPoint2.y - pointerMarkY, 2)));
						r2dm.setPointerVectorStart(null);
						r2dm.setPointerVectorEnd(null);
					} else
					{
						// check which one of the two points is nearest to the center of the
						// connected atoms to make the ringCenterVector point into the right direction.
						conAtomsCenter = getConnectedAtomsCenter(sharedAtoms);
						distance1 = Math.sqrt(Math.pow(newPoint1.x - conAtomsCenter.x, 2) + Math.pow(newPoint1.y - conAtomsCenter.y, 2));
						distance2 = Math.sqrt(Math.pow(newPoint2.x - conAtomsCenter.x, 2) + Math.pow(newPoint2.y - conAtomsCenter.y, 2));
					}
					ringCenterVector = new Vector2d(sharedAtomsCenter);
					// no ring is attached if the two ditances are equal
					if (distance1 == distance2)
					{
						logger.warn("don't know where to draw the new Ring");
					} else
					{
						if (distance1 < distance2)
						{
							ringCenterVector.sub(newPoint1);
						} else if (distance2 < distance1)
						{
							ringCenterVector.sub(newPoint2);
						}

						IAtomContainer atomCon = getRelevantAtomContainer(chemModel, firstAtom);

						// construct a new Ring that contains the highlighted bond an its two atoms
						newRing = createAttachRing(sharedAtoms, ringSize, symbol);
						if (c2dm.getDrawMode() == Controller2DModel.BENZENERING)
						{
							// make newRing a benzene ring
							Bond existingBond = atomCon.getBond(firstAtom, secondAtom);
							Bond[] bonds = newRing.getBonds();

							if (existingBond.getOrder() == 1.0)
							{
								if (existingBond.getFlag(CDKConstants.ISAROMATIC))
								{
									bonds[2].setOrder(2.0);
									bonds[4].setOrder(2.0);
								} else
								{
									bonds[1].setOrder(2.0);
									bonds[3].setOrder(2.0);
									bonds[5].setOrder(2.0);
								}
							} else
							{
								bonds[2].setOrder(2.0);
								bonds[4].setOrder(2.0);
							}
							makeRingAromatic(newRing);
						}

						// place the new atoms of the new ring to the right position
						ringPlacer.placeFusedRing(newRing, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);

						// removes the highlighed bond and its atoms from the ring to add only
						// the new placed atoms to the AtomContainer.
						try
						{
							newRing.remove(sharedAtoms);
						} catch (Exception exc)
						{
							logger.error("Could not remove atom from ring");
							logger.debug(exc);
						}
						atomCon.add(newRing);
					}
				}
				double highlightRadius = r2dm.getHighlightRadius();
				for (int i = 0; i < newRing.getAtomCount(); i++)
				{
					IAtom atom=newRing.getAtomAt(i);
					centerAtom(atom,chemModel);
					//We make sure atoms don't overlap
					//Solution is a bit crude, we would need to find an unoccupied place (and even the bond display should be optimized)
					IAtom inrange=getAtomInRange((int)atom.getPoint2d().x, (int)atom.getPoint2d().y, atom);
					if(inrange!=null && Math.sqrt(Math.pow(inrange.getX2d() - atom.getPoint2d().x, 2) + Math.pow(inrange.getY2d() - atom.getPoint2d().y, 2)) < highlightRadius/4){
						atom.getPoint2d().x-=highlightRadius/4;
						atom.getPoint2d().y-=highlightRadius/4;
					}
				}
				undoRedoContainer.add(newRing);
				UndoableEdit  edit = new AddAtomsAndBondsEdit(chemModel, undoRedoContainer, "Added Benzene");
				c2dm.getUndoSupport().postEdit(edit);
				r2dm.fireChange();
				fireChange();
			}

			if (c2dm.getDrawMode() == Controller2DModel.LASSO)
			{
				// first deselect all atoms
				r2dm.setSelectedPart(new org.openscience.cdk.AtomContainer());
				// now select new atoms
				if (wasDragged)
				{
					Vector lassoPoints = r2dm.getLassoPoints();
					r2dm.addLassoPoint(new Point((Point) lassoPoints.elementAt(0)));
					int number = lassoPoints.size();
					logger.debug("# lasso points: ", number);
					int[] screenLassoCoords = new int[number * 2];
					Point currentPoint;
					for (int i = 0; i < number; i++)
					{
						currentPoint = (Point) lassoPoints.elementAt(i);
						screenLassoCoords[i * 2] = currentPoint.x;
						screenLassoCoords[i * 2 + 1] = currentPoint.y;
						logger.debug("ScreenLasso.x = ", screenLassoCoords[i * 2]);
						logger.debug("ScreenLasso.y = ", screenLassoCoords[i * 2 + 1]);
					}
					/*
					 *  Convert points to world coordinates as they are
					 *  in screen coordinates in the vector
					 */
					int[] worldCoords = getWorldCoordinates(screenLassoCoords);
					logger.debug("Returned coords: ", worldCoords.length);
					logger.debug("       # points: ", number);
					int[] xPoints = new int[number];
					int[] yPoints = new int[number];
					for (int i = 0; i < number; i++)
					{
						xPoints[i] = worldCoords[i * 2];
						yPoints[i] = worldCoords[i * 2 + 1];
						logger.debug("WorldCoords.x  = ", worldCoords[i * 2]);
						logger.debug("WorldCoords.y  = ", worldCoords[i * 2 + 1]);
						logger.debug("Polygon.x = ", xPoints[i]);
						logger.debug("Polygon.y = ", yPoints[i]);
					}
					Polygon polygon = new Polygon(xPoints, yPoints, number);
					r2dm.setSelectedPart(getContainedAtoms(polygon));
					r2dm.getLassoPoints().removeAllElements();
					r2dm.fireChange();
				} else
				{
					// one atom clicked or one bond clicked
					IChemObject chemObj = getChemObjectInRange(mouseX, mouseY);
					IAtomContainer container = new org.openscience.cdk.AtomContainer();
					if (chemObj instanceof IAtom)
					{
						container.addAtom((IAtom) chemObj);
						logger.debug("selected one atom in lasso mode");
						r2dm.setSelectedPart(container);
					} else if (chemObj instanceof org.openscience.cdk.interfaces.Bond)
					{
						Bond bond = (Bond) chemObj;
						container.addBond(bond);
						logger.debug("selected one bond in lasso mode");
						org.openscience.cdk.interfaces.IAtom[] atoms = bond.getAtoms();
						for (int i = 0; i < atoms.length; i++)
						{
							container.addAtom(atoms[i]);
						}
						r2dm.setSelectedPart(container);
					}
				}
				fireChange();
			}

			if (c2dm.getDrawMode() == Controller2DModel.MOVE)
			{
				if (draggingSelected == false)
				{
					// then it was dragging nearest Bond or Atom
					r2dm.setSelectedPart(new org.openscience.cdk.AtomContainer());
				}
				if(r2dm.getMerge().size()>0){
					Iterator it=r2dm.getMerge().keySet().iterator();
					while(it.hasNext()){
						IAtom atom1=(IAtom)it.next();
						IAtom atom2=(IAtom)r2dm.getMerge().get(atom1);
						int contains1=-1;
						int contains2=-1;
						SetOfMolecules som=chemModel.getSetOfMolecules();
						for(int i=0;i<som.getAtomContainerCount();i++){
							if(chemModel.getSetOfMolecules().getAtomContainer(i).contains(atom1))
								contains1=i;
							if(chemModel.getSetOfMolecules().getAtomContainer(i).contains(atom2))
								contains2=i;
							if(contains1>-1 && contains2>-1)
								break;
						}
						if(contains1>contains2){
							int prov=contains1;
							contains1=contains2;
							contains2=prov;
						}
						if(contains1!=contains2){
							som.getAtomContainer(contains1).add(som.getAtomContainer(contains2));
							som.removeAtomContainer(contains2);
						}
						Bond[] bondson2=som.getAtomContainer(contains1).getConnectedBonds(atom2);
						for(int i=0;i<bondson2.length;i++){
							if(bondson2[i].getAtomAt(0)==atom2)
								bondson2[i].setAtomAt(atom1,0);
							if(bondson2[i].getAtomAt(1)==atom2)
								bondson2[i].setAtomAt(atom1,1);
							if(bondson2[i].getAtomAt(0)==bondson2[i].getAtomAt(1)){
								som.getAtomContainer(contains1).removeElectronContainer(bondson2[i]);
							}
						}
						som.getAtomContainer(contains1).removeAtom(atom2);
					}
					r2dm.getMerge().clear();
				}
			}

			if (wasDragged)
			{
				prevDragCoordX = 0;
				prevDragCoordY = 0;
				wasDragged = false;
			}
			dragMode = DRAG_UNSET;
			r2dm.setPointerVectorStart(null);
			r2dm.setPointerVectorEnd(null);
		}
		if (shiftX != 0 || shiftY != 0)
		{
			for (int i = 0; i < chemModel.getSetOfMolecules().getMoleculeCount(); i++)
			{
				Molecule mol = chemModel.getSetOfMolecules().getMolecules()[i];
				for (int k = 0; k < mol.getAtomCount(); k++)
				{
					mol.getAtomAt(k).setX2d(mol.getAtomAt(k).getX2d() - shiftX);
					mol.getAtomAt(k).setY2d(mol.getAtomAt(k).getY2d() - shiftY);
				}
			}
			r2dm.fireChange();
			fireChange();
		}
		shiftX = 0;
		shiftY = 0;
	}


	/**
	 *  Makes a ring aromatic
	 *
	 *@param  ring  The ring to be made aromatic
	 */
	private void makeRingAromatic(Ring ring)
	{
		IAtom[] atoms = ring.getAtoms();
		for (int i = 0; i < atoms.length; i++)
		{
			atoms[i].setFlag(CDKConstants.ISAROMATIC, true);
		}
		Bond[] bonds = ring.getBonds();
		for (int i = 0; i < bonds.length; i++)
		{
			bonds[i].setFlag(CDKConstants.ISAROMATIC, true);
		}
	}


	/**
	 *  manages all actions that will be invoked when a mouse button is clicked
	 *
	 *@param  e  MouseEvent object
	 */
	public void mouseClicked(MouseEvent e)
	{
		// logger.debug("Mouse clicked");
	}


	/**
	 *  manages all actions that will be invoked when a mouse enters a component
	 *
	 *@param  e  MouseEvent object
	 */
	public void mouseEntered(MouseEvent e)
	{
		// logger.debug("Mouse entered");
	}


	/**
	 *  manages all actions that will be invoked when a mouse exits a component
	 *
	 *@param  e  MouseEvent object
	 */
	public void mouseExited(MouseEvent e)
	{
		// logger.debug("Mouse exited");
	}


	/**
	 *  manages all actions that will be invoked when a key is released
	 *
	 *@param  e  MouseEvent object
	 */
	public void keyReleased(KeyEvent e)
	{
		logger.debug("Key released");
	}


	/**
	 *  manages all actions that will be invoked when a key is typed
	 *
	 *@param  e  MouseEvent object
	 */
	public void keyTyped(KeyEvent e)
	{
		try{
			logger.debug("Key typed");
			if(r2dm.getHighlightedAtom()!=null){
				IsotopeFactory ifa=IsotopeFactory.getInstance(r2dm.getHighlightedAtom().getBuilder());
				Isotope iso=ifa.getMajorIsotope(e.getKeyChar());
				if(iso!=null)
					r2dm.getHighlightedAtom().setSymbol(e.getKeyChar()+"");
			}
		}catch(Exception ex){
			logger.debug("Exception "+ex.getMessage()+" in keyPressed in AbstractController");
		}
	}


	/**
	 *  manages all actions that will be invoked when a key is pressed
	 *
	 *@param  e  MouseEvent object
	 */
	public void keyPressed(KeyEvent e)
	{
		logger.debug("Key pressed");
	}


	/*
	 *  Start of private methods
	 */
	/**
	 *  Updates an array of atoms with respect to its hydrogen count
	 *
	 *@param  container  The AtomContainer to work on
	 *@param  atoms       The Atoms to update
	 */
	private void updateAtoms(IAtomContainer container, org.openscience.cdk.interfaces.IAtom[] atoms)
	{
		for (int i = 0; i < atoms.length; i++)
		{
			updateAtom(container, atoms[i]);
		}
	}


	/**
	 *  Updates an atom with respect to its hydrogen count
	 *
	 *@param  container  The AtomContainer to work on
	 *@param  atom       The Atom to update
	 */
	private void updateAtom(IAtomContainer container, org.openscience.cdk.interfaces.IAtom atom)
	{
		if (c2dm.getAutoUpdateImplicitHydrogens())
		{
			atom.setHydrogenCount(0);
			try
			{
				hydrogenAdder.addImplicitHydrogensToSatisfyValency(container, atom);
			} catch (Exception exception)
			{
				logger.error(exception.getMessage());
				logger.debug(exception);
			}
		}
	}


	/**
	 *  No idea what this does
	 *
	 *@param  angle  Some kind of angle
	 *@return        Don't know what
	 */
	private double snapAngle(double angle)
	{
		double div = (Math.PI / 180) * c2dm.getSnapAngle();
		return (Math.rint(angle / div)) * div;
	}


	/**
	 *  No idea what this does
	 *
	 *@param  position  Some kind of position
	 *@return           Don't know what
	 */
	private int snapCartesian(int position)
	{
		int div = c2dm.getSnapCartesian();
		return (int) (Math.rint(position / div)) * div;
	}


	/**
	 *  Gets the chemObjectInRange attribute of the Controller2D object
	 *
	 *@param  X  Current mouse x
	 *@param  Y  Current mouse x
	 *@return    The chemObjectInRange value
	 */
	public IChemObject getChemObjectInRange(int X, int Y)
	{
		IChemObject objectInRange = getAtomInRange(X, Y);
		if (objectInRange != null)
		{
			// logger.debug("Returning nearest Atom: " + objectInRange);
			return objectInRange;
		}
		objectInRange = getBondInRange(X, Y);
		if (objectInRange != null)
		{
			// logger.debug("Returning nearest Bond: " + objectInRange);
			return objectInRange;
		}
		objectInRange = getReactionInRange(X, Y);
		if (objectInRange != null)
		{
			// logger.debug("Returning nearest Reaction: " + objectInRange);
			return objectInRange;
		}
		/*
		 *  chemModel covers whole of editing window, and if nothing
		 *  more interesting is near, then them model is in range.
		 */
		// logger.debug("Returning nearest ChemModel: " + chemModel);
		return chemModel;
	}


	/**
	 *  Returns an Atom if it is in a certain range of the given point. Used to
	 *  highlight an atom that is near the cursor. <p>
	 *
	 *  <b>Important: the coordinates must be given in world coordinates and not in
	 *  screen coordinates!
	 *
	 *@param  X  The x world coordinate of the point
	 *@param  Y  The y world coordinate of the point
	 *@return    An Atom if it is in a certain range of the given point
	 */
	private IAtom getAtomInRange(int X, int Y)
	{
		return getAtomInRange(X,Y,null);
	}
	
	
	/**
	 *  Returns an Atom if it is in a certain range of the given point. Used to
	 *  highlight an atom that is near the cursor. <p>
	 *
	 *  <b>Important: the coordinates must be given in world coordinates and not in
	 *  screen coordinates!
	 *
	 *@param  X  The x world coordinate of the point
	 *@param  Y  The y world coordinate of the point
	 *@return    An Atom if it is in a certain range of the given point
	 */
	private IAtom getAtomInRange(int X, int Y, IAtom ignore)
	{
		double highlightRadius = r2dm.getHighlightRadius();
		IAtomContainer atomCon = getAllInOneContainer(chemModel);
		if(ignore!=null)
			atomCon.removeAtomAndConnectedElectronContainers(ignore);
		IAtom closestAtom = GeometryTools.getClosestAtom(X, Y, atomCon);
		if (closestAtom != null)
		{
			//logger.debug("getAtomInRange(): An atom is near");
			if (!(Math.sqrt(Math.pow(closestAtom.getX2d() - X, 2) +
					Math.pow(closestAtom.getY2d() - Y, 2)) < highlightRadius))
			{
				closestAtom = null;
			}
		}
		return closestAtom;
	}


	/**
	 *  Returns a Bond if it is in a certain range of the given point. Used to
	 *  highlight a bond that is near the cursor. <p>
	 *
	 *  <b>Important: the coordinates must be given in world coordinates and not in
	 *  screen coordinates!
	 *
	 *@param  X  The x world coordinate of the point
	 *@param  Y  The y world coordinate of the point
	 *@return    An Atom if it is in a certain range of the given point
	 */
	private Bond getBondInRange(int X, int Y)
	{
        double highlightRadius = r2dm.getHighlightRadius();
		IAtomContainer atomCon = getAllInOneContainer(chemModel);
        if (atomCon.getBondCount() != 0) {
            Bond closestBond = GeometryTools.getClosestBond(X, Y, atomCon);
    		if (closestBond == null)
    		{
    			return null;
    		}
    		// logger.debug("closestBond  "+ closestBond);
    		int[] coords = GeometryTools.distanceCalculator(
    				GeometryTools.getBondCoordinates(closestBond), highlightRadius);
    		int[] xCoords = {coords[0], coords[2], coords[4], coords[6]};
    		int[] yCoords = {coords[1], coords[3], coords[5], coords[7]};
    		if ((new Polygon(xCoords, yCoords, 4)).contains(new Point(X, Y)))
    		{
    			return closestBond;
    		}
        }
		return null;
	}

	abstract Reaction getReactionInRange(int X, int Y);

	/**
	 *  Returns an AtomContainer that contains the atom or the the bond with its
	 *  two atoms that are highlighted at the moment.
	 *
	 *@return    An AtomContainer containig the highlighted atom\atoms\bond
	 */
	 IAtomContainer getHighlighted()
	{
		IAtomContainer highlighted = new org.openscience.cdk.AtomContainer();
		IAtom highlightedAtom = r2dm.getHighlightedAtom();
		Bond highlightedBond = r2dm.getHighlightedBond();
		if (highlightedAtom != null)
		{
			highlighted.addAtom(highlightedAtom);
		} else if (highlightedBond != null)
		{
			highlighted.addBond(highlightedBond);
			for (int i = 0; i < highlightedBond.getAtomCount(); i++)
			{
				highlighted.addAtom(highlightedBond.getAtomAt(i));
			}
		}
		logger.debug("sharedAtoms  ", highlighted);
		return highlighted;
	}


	/**
	 *  Constructs a new Ring of a certain size that contains all the atoms and
	 *  bonds of the given AtomContainer and is filled up with new org.openscience.cdk.Atoms and Bonds.
	 *
	 *@param  sharedAtoms  The AtomContainer containing the Atoms and bonds for the
	 *      new Ring
	 *@param  ringSize     The size (number of Atoms) the Ring will have
	 *@param  symbol       The element symbol the new atoms will have
	 *@return              The constructed Ring
	 */
	 Ring createAttachRing(IAtomContainer sharedAtoms, int ringSize, String symbol)
	{
		Ring newRing = new org.openscience.cdk.Ring(ringSize);
		IAtom[] ringAtoms = new org.openscience.cdk.Atom[ringSize];
		for (int i = 0; i < sharedAtoms.getAtomCount(); i++)
		{
			ringAtoms[i] = sharedAtoms.getAtomAt(i);
		}
		for (int i = sharedAtoms.getAtomCount(); i < ringSize; i++)
		{
			ringAtoms[i] = new org.openscience.cdk.Atom(symbol);
		}
		Bond[] bonds = sharedAtoms.getBonds();
		for (int i = 0; i < bonds.length; i++)
		{
			newRing.addBond(bonds[i]);
		}
		for (int i = sharedAtoms.getBondCount(); i < ringSize - 1; i++)
		{
			newRing.addBond(new org.openscience.cdk.Bond(ringAtoms[i], ringAtoms[i + 1], 1));
		}
		newRing.addBond(new org.openscience.cdk.Bond(ringAtoms[ringSize - 1], ringAtoms[0], 1));
		newRing.setAtoms(ringAtoms);
		return newRing;
	}


	/**
	 *  Searches all the atoms attached to the Atoms in the given AtomContainer and
	 *  calculates the center point of them.
	 *
	 *@param  sharedAtoms  The Atoms the attached partners are searched of
	 *@return              The Center Point of all the atoms found
	 */
	 Point2d getConnectedAtomsCenter(IAtomContainer sharedAtoms)
	{
		IAtom currentAtom;
		IAtom[] conAtomsArray;
		IAtomContainer conAtoms = new org.openscience.cdk.AtomContainer();
		IAtomContainer atomCon = getAllInOneContainer(chemModel);
		for (int i = 0; i < sharedAtoms.getAtomCount(); i++)
		{
			currentAtom = sharedAtoms.getAtomAt(i);
			conAtoms.addAtom(currentAtom);
			conAtomsArray = atomCon.getConnectedAtoms(currentAtom);
			for (int j = 0; j < conAtomsArray.length; j++)
			{
				conAtoms.addAtom(conAtomsArray[j]);
			}
		}
		return GeometryTools.get2DCenter(conAtoms);
	}


	/**
	 *  Returns an AtomContainer with all the atoms and bonds that are inside a
	 *  given polygon.
	 *
	 *@param  polygon  The given Polygon
	 *@return          AtomContainer with all atoms and bonds inside the polygon
	 */
	 IAtomContainer getContainedAtoms(Polygon polygon)
	{
		IAtom currentAtom;
		Bond currentBond;
		IAtomContainer selectedPart = new org.openscience.cdk.AtomContainer();
		IAtomContainer atomCon = getAllInOneContainer(chemModel);
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			currentAtom = atomCon.getAtomAt(i);
			logger.debug("Atom: ", currentAtom);
			if (polygon.contains(new Point((int) currentAtom.getX2d(), (int) currentAtom.getY2d())))
			{
				selectedPart.addAtom(currentAtom);
			}
		}
		Bond[] bonds = atomCon.getBonds();
		for (int i = 0; i < bonds.length; i++)
		{
			currentBond = bonds[i];
			for (int j = 0; j < selectedPart.getAtomCount(); j++)
			{
				currentAtom = selectedPart.getAtomAt(j);
				if (selectedPart.contains(currentBond.getConnectedAtom(currentAtom)))
				{
					selectedPart.addBond(currentBond);
					break;
				}
			}
		}
		return selectedPart;
	}


	/**
	 *  This methods corrects for the zoom factor, and thus transforms screen
	 *  coordinates back into world coordinates. IMPORTANT: even coords are taken
	 *  as y coordinates, uneven as x coordinates.
	 *
	 *@param  coords  the array of coordinates to be used
	 *@return         The worldCoordinates value
	 */
	public int[] getWorldCoordinates(int[] coords)
	{
		int[] worldCoords = new int[coords.length];
		int coordCount = coords.length / 2;
		//logger.debug("coord.length: ", coords.length);
		int height = (int) (r2dm.getBackgroundDimension()).getHeight();
		for (int i = 0; i < coordCount; i++)
		{
			worldCoords[i * 2] = (int) ((double) coords[i * 2] / r2dm.getZoomFactor());
			worldCoords[i * 2 + 1] = (int) ((double) (height - coords[i * 2 + 1]) / r2dm.getZoomFactor());
			if (logger.isDebugEnabled())
			{
				//logger.debug("getWorldCoord: " + coords[i * 2] + " -> " + worldCoords[i * 2]);
				//logger.debug("getWorldCoord: " + coords[i * 2 + 1] + " -> " + worldCoords[i * 2 + 1]);
			}
		}
		return worldCoords;
	}


	/**
	 *  Adds a change listener to the list of listeners
	 *
	 *@param  listener  The listener added to the list
	 */

	public void addCDKChangeListener(CDKChangeListener listener)
	{
		listeners.add(listener);
	}


	/**
	 *  Removes a change listener from the list of listeners
	 *
	 *@param  listener  The listener removed from the list
	 */
	public void removeCDKChangeListener(CDKChangeListener listener)
	{
		listeners.remove(listener);
	}


	/**
	 *  Notifies registered listeners of certain changes that have occurred in this
	 *  model.
	 */
	public void fireChange()
	{
		EventObject event = new EventObject(this);
		for (int i = 0; i < listeners.size(); i++)
		{
			((CDKChangeListener) listeners.get(i)).stateChanged(event);
		}
	}


	// ------------ CHEMICAL OPERATIONS -------------- //

	/**
	 *  Highlight the nearest Atom or Bond. <p>
	 *
	 *  FIXME: this needs to be extended for other ChemObjects.
	 *
	 *@param  mouseX  x coordinate in world coordinates (not screen coordinates)
	 *@param  mouseY  y coordinate in world coordinates (not screen coordinates)
	 */
	 void highlightNearestChemObject(int mouseX, int mouseY)
	{
		IChemObject objectInRange = getChemObjectInRange(mouseX, mouseY);
		if (objectInRange instanceof IAtom)
		{
			r2dm.setHighlightedAtom((IAtom) objectInRange);
			r2dm.setHighlightedBond(null);
		} else if (objectInRange instanceof org.openscience.cdk.interfaces.Bond)
		{
			r2dm.setHighlightedBond((Bond) objectInRange);
			r2dm.setHighlightedAtom(null);
		} else
		{
			r2dm.setHighlightedBond(null);
			r2dm.setHighlightedAtom(null);
		}
	}


	/**
	 *  Create a new bond. Possibly connecting the end point to the nearest Atom.
	 *  <p>
	 *
	 *  All coordinates are world coordinates.
	 *
	 *@param  startX  Start X coordinate of the new bond
	 *@param  startY  Start Y coordinate of the new bond
	 *@param  endX  End X coordinate of the new bond
	 *@param  endY  End Y coordinate of the new bond
	 */
	 void createNewBond(int startX, int startY, int endX, int endY)
	{

	}


	/**
	 *  Draws a proposed bond, i.e. shows the bond direction
	 *  during dragging of the mouse
	 *
	 *@param  startX  Start X coordinate of the proposed bond
	 *@param  startY  Start Y coordinate of the proposed bond
	 *@param  mouseX  Current X mouse coordinate
	 *@param  mouseY  Current Y mouse coordinate
	 */
	 void drawProposedBond(int startX, int startY, int mouseX, int mouseY)
	{
		logger.debug("Drawing proposed bond...");
		int endX = 0;
		int endY = 0;
		double pointerVectorLength = c2dm.getBondPointerLength();
		double angle = 0;
		IAtom atomInRange;

		angle = GeometryTools.getAngle(startX - mouseX, startY - mouseY);
		if (c2dm.getSnapToGridAngle())
		{
			angle = snapAngle(angle);
		}
		atomInRange = getAtomInRange(mouseX, mouseY);
		if (atomInRange != null)
		{
			endX = (int) atomInRange.getX2d();
			endY = (int) atomInRange.getY2d();
		} else
		{
			endX = startX - (int) (Math.cos(angle) * pointerVectorLength);
			endY = startY - (int) (Math.sin(angle) * pointerVectorLength);
		}
		logger.debug("End point: " + endX + ", " + endY);
		logger.debug("Start point: " + startX + ", " + startY);
		r2dm.setPointerVectorEnd(new Point(endX, endY));
	}


	/**
	 *  Selects a rectangular area on the screen 
	 *
	 *@param  startX  Start x coordinate
	 *@param  startY  Start y coordinate
	 *@param  mouseX  Current x mouse position
	 *@param  mouseY  Current y mouse position
	 */
	 void selectRectangularArea(int startX, int startY, int mouseX, int mouseY)
	{
		int[] xPoints = {startX, startX, mouseX, mouseX};
		int[] yPoints = {startY, mouseY, mouseY, startY};
		r2dm.setSelectRect(new Polygon(xPoints, yPoints, 4));
	}


	/**
	 *  Move an Atom by the specified change in coordinates.
	 *
	 *@param  deltaX  change in x direction
	 *@param  deltaY  change in y direction
	 */
	 void moveSelectedAtomsWith(int deltaX, int deltaY)
	{
		IAtomContainer container = r2dm.getSelectedPart();
		if (container != null)
		{
			// only move selected atoms if count > 0
			IAtom[] atoms = container.getAtoms();
			for (int i = 0; i < atoms.length; i++)
			{
				IAtom atom = atoms[i];
				atom.setX2d(atom.getX2d() + deltaX);
				atom.setY2d(atom.getY2d() + deltaY);
			}
		}
	}


	/**
	 *  Selects the nearest item on the screen next to the 
	 *  mouse pointer, if none is selected yet
	 *
	 *  @param  mouseX  The current X coordinate of the mouse
	 *  @param  mouseY  The current Y coordinate of the mouse
	 */
	 void selectNearestChemObjectIfNoneSelected(int mouseX, int mouseY)
	{
		IAtomContainer container = r2dm.getSelectedPart();
		if (container == null || (container.getAtomCount() == 0))
		{
			// if no atoms are selected, then temporarily select nearest
			// to make sure to original state is reached again when the
			// mouse is released, the draggingSelected boolean is set
			logger.warn("No atoms selected: temporarily selecting nearest atom/bond");
			draggingSelected = false;
			IAtomContainer selected = new org.openscience.cdk.AtomContainer();
			IAtom atomInRange = getAtomInRange(mouseX, mouseY);
			if (atomInRange != null)
			{
				selected.addAtom(atomInRange);
				r2dm.setSelectedPart(selected);
			} else
			{
				Bond bondInRange = getBondInRange(mouseX, mouseY);
				// because only atoms are dragged, select the atoms
				// in the bond, instead of the bond itself
				if (bondInRange != null)
				{
					org.openscience.cdk.interfaces.IAtom[] atoms = bondInRange.getAtoms();
					for (int i = 0; i < atoms.length; i++)
					{
						selected.addAtom(atoms[i]);
					}
					r2dm.setSelectedPart(selected);
				}
			}
			logger.debug("Selected: ", selected);
			fireChange();
		}
	}


	/**
	 * Centers an atom on a given background dimension
	 *
	 * @param  atom  The Atom to be centered
	 */
	 void centerAtom(IAtom atom, ChemModel chemModel)
	{
		 double smallestx=Integer.MAX_VALUE;
		 double largestx=Integer.MIN_VALUE;
		 double smallesty=Integer.MAX_VALUE;
		 double largesty=Integer.MIN_VALUE;
		 IAtomContainer allinone=SetOfMoleculesManipulator.getAllInOneContainer(chemModel.getSetOfMolecules());
		 for(int i=0;i<allinone.getAtomCount();i++){
			 if(allinone.getAtomAt(i).getPoint2d().x<smallestx)
				 smallestx=allinone.getAtomAt(i).getPoint2d().x;
			 if(allinone.getAtomAt(i).getPoint2d().y<smallesty)
				 smallesty=allinone.getAtomAt(i).getPoint2d().y;
			 if(allinone.getAtomAt(i).getPoint2d().x>largestx)
				 largestx=allinone.getAtomAt(i).getPoint2d().x;
			 if(allinone.getAtomAt(i).getPoint2d().y>largesty)
				 largesty=allinone.getAtomAt(i).getPoint2d().y;
		 }
		 int xstretch=((int)(largestx-smallestx))+20;
		 int ystretch=((int)(largesty-smallesty))+20;
		 if(xstretch<r2dm.getBackgroundDimension().width)
			 xstretch=r2dm.getBackgroundDimension().width;
		 if(ystretch<r2dm.getBackgroundDimension().height)
			 ystretch=r2dm.getBackgroundDimension().height;
		 r2dm.setBackgroundDimension(new Dimension(xstretch,ystretch));
		if (atom == null)
		{
			return;
		}
		if (atom.getX2d() < 0 && shiftX > atom.getX2d() - 10)
		{
			shiftX = atom.getX2d() - 10;
		}
		if (atom.getX2d() > r2dm.getBackgroundDimension().getWidth() && shiftX < atom.getX2d() - r2dm.getBackgroundDimension().getWidth() + 10)
		{
			shiftX = atom.getX2d() - r2dm.getBackgroundDimension().getWidth() + 10;
		}
		if (atom.getY2d() < 0 && shiftY > atom.getY2d() - 10)
		{
			shiftY = atom.getY2d() - 10;
		}
		if (atom.getY2d() > r2dm.getBackgroundDimension().getHeight() && shiftY < atom.getY2d() - r2dm.getBackgroundDimension().getHeight() + 10)
		{
			shiftY = atom.getY2d() - r2dm.getBackgroundDimension().getHeight() + 10;
		}
	}
	
	IAtomContainer getRelevantAtomContainer(ChemModel chemModel, org.openscience.cdk.interfaces.IAtom atom)
	{
		return atomContainer;
	}
	
	IAtomContainer getAllInOneContainer(ChemModel chemModel)
	{
		return atomContainer;
	}

	void handleMapping(boolean wasDragged, Renderer2DModel r2dm)
	{
		logger.debug("Should make new mapping...");
		if (wasDragged)
		{
			int endX = r2dm.getPointerVectorEnd().x;
			int endY = r2dm.getPointerVectorEnd().y;
			IAtom mappedToAtom = getAtomInRange(endX, endY);
			if (mappedToAtom != null)
			{
				int startX = r2dm.getPointerVectorStart().x;
				int startY = r2dm.getPointerVectorStart().y;
				IAtom mappedFromAtom = getAtomInRange(startX, startY);
				if (mappedFromAtom != null)
				{
					Mapping mapping = new Mapping(mappedFromAtom, mappedToAtom);
					logger.debug("Created mapping: ", mapping);
					logger.debug("  between ", mappedFromAtom);
					logger.debug("  and ", mappedToAtom);
					// ok, now figure out if they are in one reaction
					Reaction reaction1 = ChemModelManipulator.getRelevantReaction(chemModel, mappedFromAtom);
					Reaction reaction2 = ChemModelManipulator.getRelevantReaction(chemModel, mappedToAtom);
					if (reaction1 != null && reaction2 != null && reaction1 == reaction2)
					{
						logger.debug("Adding mapping to reaction: ", reaction1.getID());
						((org.openscience.cdk.Reaction)reaction1).addMapping(mapping);
					} else
					{
						logger.warn("Reactions do not match, or one or both are reactions are null");
					}
				} else
				{
					logger.debug("Dragging did not start in atom...");
				}
			} else
			{
				logger.debug("Dragging did not end in atom...");
			}
		} else
		{
			logger.debug("Not dragged in mapping mode");
		}
	}
	
	abstract Reaction getRelevantReaction(ChemModel model, IAtom atom);

	public IAtomContainer getAtomContainer() {
		return atomContainer;
	}

	public void setAtomContainer(IAtomContainer atomContainer) {
		this.atomContainer = atomContainer;
	}

	public ChemModel getChemModel() {
		return chemModel;
	}

	public void setChemModel(ChemModel chemModel) {
		this.chemModel = chemModel;
	}
}

