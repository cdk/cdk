/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.controller;

import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.*;
import org.openscience.cdk.event.*;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;
import org.openscience.cdk.tools.IsotopeFactory;
import org.openscience.cdk.tools.HydrogenAdder;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.vecmath.*;

/**
 * Class that acts on MouseEvents and KeyEvents.
 *
 * @author       steinbeck
 * @author       egonw
 * @cdk.keyword  mouse events
 * @cdk.requires java1.4
 */
public class Controller2D implements MouseMotionListener, MouseListener, KeyListener {

    private static final int DRAG_UNSET = 0;
    private static final int DRAG_MOVING_SELECTED = 1;
    private static final int DRAG_DRAWING_PROPOSED_BOND = 2;
    private static final int DRAG_DRAWING_PROPOSED_RING = 3;
    private static final int DRAG_MAKING_SQUARE_SELECTION = 4;
    private static final int DRAG_MAKING_LASSO_SELECTION = 5;
    private static final int DRAG_DRAWING_PROPOSED_ATOMATOMMAP = 6;
    
	Renderer2DModel r2dm;
	ChemModel chemModel;
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

	// Helper classes
	HydrogenAdder hydrogenAdder = new HydrogenAdder("org.openscience.cdk.tools.ValencyChecker");


	/**
	 *  Constructs a controller that performs operations on the AtomContainer when
	 *  actions are detected from the MouseEvents.
	 *
	 *@param  chemModel  Description of the Parameter
	 *@param  r2dm       Description of the Parameter
	 *@param  c2dm       Description of the Parameter
	 */
	public Controller2D(ChemModel chemModel, Renderer2DModel r2dm, Controller2DModel c2dm)
	{
		this.chemModel = chemModel;
		this.r2dm = r2dm;
		this.c2dm = c2dm;

		logger = new LoggingTool(this);

		commonElements = new Vector();
		String[] elements = c2dm.getCommonElements();
		for (int i = 0; i < elements.length; i++)
		{
			commonElements.add(elements[i]);
		}
	}


	/**
	 *  Constructor for the Controller2D object
	 *
	 *@param  chemModel  Description of the Parameter
	 *@param  r2dm       Description of the Parameter
	 */
	public Controller2D(ChemModel chemModel, Renderer2DModel r2dm)
	{
		this(chemModel, r2dm, new Controller2DModel());
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
	 *  Sets the chemModel attribute of the Controller2D object
	 *
	 *@param  chemModel  The new chemModel value
	 */
	public void setChemModel(ChemModel chemModel)
	{
		this.chemModel = chemModel;
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
        if (logger.isDebugEnabled()) {
            logger.debug("   trigger=" + event.isPopupTrigger() +
				/* ", Button number: " + event.getButton() + */
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


		if (dragMode == DRAG_DRAWING_PROPOSED_BOND) {
			int startX = r2dm.getPointerVectorStart().x;
			int startY = r2dm.getPointerVectorStart().y;
			drawProposedBond(startX, startY, mouseX, mouseY);
		} else if (dragMode == DRAG_MAKING_SQUARE_SELECTION) {
			int startX = r2dm.getPointerVectorStart().x;
			int startY = r2dm.getPointerVectorStart().y;
			selectRectangularArea(startX, startY, mouseX, mouseY);
		} else if (dragMode == DRAG_DRAWING_PROPOSED_RING) {
			int endX = 0;
			int endY = 0;
			double angle = 0;
			double pointerVectorLength = c2dm.getRingPointerLength();
			Point2d center = getHighlighted().get2DCenter();
			r2dm.setPointerVectorStart(new Point((int) center.x, (int) center.y));
			angle = GeometryTools.getAngle(center.x - mouseX, center.y - mouseY);
			endX = (int) center.x - (int) (Math.cos(angle) * pointerVectorLength);
			endY = (int) center.y - (int) (Math.sin(angle) * pointerVectorLength);
			r2dm.setPointerVectorEnd(new Point(endX, endY));
		} else if (dragMode == DRAG_MAKING_LASSO_SELECTION) {
			/*
			 *  Draw polygon in screencoordinates, convert them
			 *  to world coordinates when mouse released
			 */
			r2dm.addLassoPoint(new Point(event.getX(), event.getY()));
		} else if (dragMode == DRAG_MOVING_SELECTED) {
			// all these are in model coordinates
			logger.debug("Dragging selected atoms");
			int deltaX = mouseX - prevDragCoordX;
			int deltaY = mouseY - prevDragCoordY;
			moveSelectedAtomsWith(deltaX, deltaY);
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
        if (logger.isDebugEnabled()) {
            logger.debug("   trigger=" + event.isPopupTrigger() +
				/* ", Button number: " + event.getButton() + */
				", Click count: " + event.getClickCount());
        }

        int startX = 0;
        int startY = 0;
        r2dm.setPointerVectorStart(null);
        r2dm.setPointerVectorEnd(null);
        Atom atomInRange = getAtomInRange(mouseX, mouseY);
        Bond bondInRange = getBondInRange(mouseX, mouseY);
        if (atomInRange != null) {
            startX = (int) atomInRange.getX2d();
            startY = (int) atomInRange.getY2d();
            r2dm.setPointerVectorStart(new Point(startX, startY));
        } else {
            r2dm.setPointerVectorStart(new Point(mouseX, mouseY));
        }
        
        if (c2dm.getDrawMode() == c2dm.MOVE) {
            selectNearestChemObjectIfNoneSelected(mouseX, mouseY);
            dragMode = DRAG_MOVING_SELECTED;
        } else if (c2dm.getDrawMode() == c2dm.DRAWBOND) {
            if (bondInRange != null && atomInRange == null) {
                // make sure we are not dragging a bond
            } else {
                dragMode = DRAG_DRAWING_PROPOSED_BOND;
            }
        } else if (c2dm.getDrawMode() == c2dm.MAPATOMATOM ) {
            dragMode = DRAG_DRAWING_PROPOSED_ATOMATOMMAP;
        } else if (c2dm.getDrawMode() == c2dm.SELECT) {
            dragMode = DRAG_MAKING_SQUARE_SELECTION;
        } else if (c2dm.getDrawMode() == c2dm.LASSO) {
            dragMode = DRAG_MAKING_LASSO_SELECTION;
        } else if (c2dm.getDrawMode() == c2dm.RING ||
                   c2dm.getDrawMode() == c2dm.BENZENERING) {
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
        if (logger.isDebugEnabled()) {
            logger.debug("   trigger=" + event.isPopupTrigger() +
				/* ", Button number: " + event.getButton() + */
				", Click count: " + event.getClickCount());
        }

        if ((event.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
			int[] screenCoords = {event.getX(), event.getY()};
			int[] mouseCoords = getWorldCoordinates(screenCoords);
			int mouseX = mouseCoords[0];
			int mouseY = mouseCoords[1];

			if (c2dm.getDrawMode() == c2dm.SYMBOL)
			{

				Atom atomInRange = r2dm.getHighlightedAtom();
				if (atomInRange != null)
				{
					String symbol = c2dm.getDrawElement();
					atomInRange.setSymbol(symbol);
					// configure the atom, so that the atomic number matches the symbol
					try
					{
						IsotopeFactory.getInstance().configure(atomInRange);
					} catch (Exception exception)
					{
						logger.error("Error while configuring atom");
						logger.debug(exception);
					}
					// update atom
					AtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInRange);
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
					r2dm.fireChange();
					fireChange();
				}
			}

			if (c2dm.getDrawMode() == c2dm.INCCHARGE)
			{
				Atom atomInRange = r2dm.getHighlightedAtom();
				if (atomInRange != null)
				{
					atomInRange.setFormalCharge(atomInRange.getFormalCharge() + 1);

					// update atom
					AtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInRange);
					updateAtom(container, atomInRange);

					/*
					 *  PRESERVE THIS. This notifies the
					 *  the listener responsible for
					 *  undo and redo storage that it
					 *  should store this change of an atom charge
					 */
					isUndoableChange = true;
					/*
					 *  ---
					 */
					r2dm.fireChange();
					fireChange();
				}
			}
			if (c2dm.getDrawMode() == c2dm.DECCHARGE)
			{
				Atom atomInRange = r2dm.getHighlightedAtom();
				if (atomInRange != null)
				{
					atomInRange.setFormalCharge(atomInRange.getFormalCharge() - 1);
					// update atom
					AtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInRange);
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
					r2dm.fireChange();
					fireChange();
				}
			}

            if (c2dm.getDrawMode() == c2dm.MAPATOMATOM) {
                logger.debug("Should make new mapping...");
				if (wasDragged) {
                    int endX = r2dm.getPointerVectorEnd().x;
                    int endY = r2dm.getPointerVectorEnd().y;
                    Atom mappedToAtom = getAtomInRange(endX, endY);
                    if (mappedToAtom != null) {
                        int startX = r2dm.getPointerVectorStart().x;
                        int startY = r2dm.getPointerVectorStart().y;
                        Atom mappedFromAtom = getAtomInRange(startX, startY);
                        if (mappedFromAtom != null) {
                            Mapping mapping = new Mapping(mappedFromAtom, mappedToAtom);
                            logger.debug("Created mapping: ", mapping);
                            logger.debug("  between ", mappedFromAtom);
                            logger.debug("  and ", mappedToAtom);
                            // ok, now figure out if they are in one reaction
                            Reaction reaction1 = ChemModelManipulator.getRelevantReaction(chemModel, mappedFromAtom);
                            Reaction reaction2 = ChemModelManipulator.getRelevantReaction(chemModel, mappedToAtom);
                            if (reaction1 != null && reaction2 != null && reaction1 == reaction2) {
                                logger.debug("Adding mapping to reaction: ", reaction1.getID());
                                reaction1.addMapping(mapping);
                            } else {
                                logger.warn("Reactions do not match, or one or both are reactions are null");
                            }
                        } else {
                            logger.debug("Dragging did not start in atom...");
                        }
                    } else {
                        logger.debug("Dragging did not end in atom...");
                    }
                } else {
                    logger.debug("Not dragged in mapping mode");
                }
            }
            
			if (c2dm.getDrawMode() == c2dm.DRAWBOND)
			{
				Atom atomInRange;
				Atom newAtom1;
				Atom newAtom2;
				Bond newBond;
				int startX = r2dm.getPointerVectorStart().x;
				int startY = r2dm.getPointerVectorStart().y;
				Bond bondInRange = r2dm.getHighlightedBond();
				atomInRange = r2dm.getHighlightedAtom();

				if (bondInRange != null)
				{
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
					Atom[] atoms = bondInRange.getAtoms();
					AtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atoms[0]);
					updateAtoms(container, atoms);
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
				} else
				{
					if (atomInRange != null)
					{
						newAtom1 = atomInRange;
					} else
					{
						// create a new molecule
						newAtom1 = new Atom(c2dm.getDrawElement(), new Point2d(startX, startY));
						AtomContainer atomCon = ChemModelManipulator.createNewMolecule(chemModel);
						atomCon.addAtom(newAtom1);
						// update atoms
						updateAtom(atomCon, newAtom1);

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

					if (wasDragged) {
                        if (dragMode == DRAG_DRAWING_PROPOSED_BOND) {
                            int endX = r2dm.getPointerVectorEnd().x;
                            int endY = r2dm.getPointerVectorEnd().y;
                            atomInRange = getAtomInRange(endX, endY);
                            AtomContainer atomCon = null;
                            if (atomInRange != null)
                            {
                                newAtom2 = atomInRange;
                                atomCon = ChemModelManipulator.getRelevantAtomContainer(chemModel, newAtom2);
                            } else
                            {
                                newAtom2 = new Atom(c2dm.getDrawElement(), new Point2d(endX, endY));
                                atomCon = ChemModelManipulator.getRelevantAtomContainer(chemModel, newAtom1);
                                atomCon.addAtom(newAtom2);
                            }
                            newBond = new Bond(newAtom1, newAtom2, 1);
                            atomCon.addBond(newBond);
                            
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
						AtomContainer atomCon = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInRange);
						newAtom2 = new Atom(c2dm.getDrawElement(), atomInRange.getPoint2d());

						// now create 2D coords for new atom
						double bondLength = r2dm.getBondLength();
						Atom[] connectedAtoms = atomCon.getConnectedAtoms(atomInRange);
						AtomContainer placedAtoms = new AtomContainer();
                        placedAtoms.addAtom(atomInRange);
						for (int i = 0; i < connectedAtoms.length; i++) {
							placedAtoms.addAtom(connectedAtoms[i]);
						}
						AtomContainer unplacedAtoms = new AtomContainer();
						unplacedAtoms.addAtom(newAtom2);
						AtomPlacer atomPlacer = new AtomPlacer();
						atomPlacer.setMolecule(new Molecule(atomCon));
						Point2d center2D = placedAtoms.get2DCenter();
						atomPlacer.distributePartners(atomInRange, placedAtoms, center2D,
								unplacedAtoms, bondLength);

						// now add the new atom
						atomCon.addAtom(newAtom2);
						atomCon.addBond(new Bond(atomInRange, newAtom2, 1.0));

						// update atoms
						updateAtom(atomCon, atomInRange);
						updateAtom(atomCon, newAtom2);
					}
				}
				r2dm.fireChange();
				fireChange();
			}

			if (c2dm.getDrawMode() == c2dm.UP_BOND)
			{
				Bond bondInRange = r2dm.getHighlightedBond();

				if (bondInRange != null)
				{
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
					;
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
				} else
				{
					logger.warn("No bond in range!");
				}
				r2dm.fireChange();
				fireChange();
			}

			if (c2dm.getDrawMode() == c2dm.DOWN_BOND)
			{
				logger.info("Toggling stereo bond down");
				Bond bondInRange = r2dm.getHighlightedBond();

				if (bondInRange != null)
				{
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
					;
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
				} else
				{
					logger.warn("No bond in range!");
				}
				r2dm.fireChange();
				fireChange();
			}

			if (c2dm.getDrawMode() == c2dm.SELECT && wasDragged)
			{
				logger.info("User asks to selected atoms");
				AtomContainer selectedPart = new AtomContainer();
				r2dm.setSelectedPart(selectedPart);
				r2dm.setSelectedPart(getContainedAtoms(r2dm.getSelectRect()));
				r2dm.setSelectRect(null);
				logger.debug("selected stuff  ", selectedPart);
			}

			if (c2dm.getDrawMode() == c2dm.ERASER)
			{
				Atom highlightedAtom = r2dm.getHighlightedAtom();
				Bond highlightedBond = r2dm.getHighlightedBond();
				if (highlightedAtom != null)
				{
					logger.info("User asks to delete an Atom");
					AtomContainer container = ChemModelManipulator.getAllInOneContainer(chemModel);
					logger.debug("Atoms before delete: ", container.getAtomCount());
					ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel, highlightedAtom);
					container = ChemModelManipulator.getAllInOneContainer(chemModel);
					logger.debug("Atoms before delete: ", container.getAtomCount());
					// update atoms
					Atom[] atoms = container.getConnectedAtoms(highlightedAtom);
                    if (atoms.length > 0) {
                        AtomContainer atomCon = ChemModelManipulator.getRelevantAtomContainer(chemModel, atoms[0]);
                        updateAtoms(atomCon, atoms);
                    }
				} else if (highlightedBond != null)
				{
					logger.info("User asks to delete a Bond");
					ChemModelManipulator.removeElectronContainer(chemModel, highlightedBond);
					// update atoms
					Atom[] atoms = highlightedBond.getAtoms();
					AtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atoms[0]);
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
				r2dm.fireChange();
				fireChange();
			}

			if (c2dm.getDrawMode() == c2dm.RING || c2dm.getDrawMode() == c2dm.BENZENERING)
			{
				Ring newRing;
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
				Atom firstAtom;
				Atom secondAtom;
				Atom spiroAtom;
				Point2d conAtomsCenter = null;
				Point2d newPoint1;
				Point2d newPoint2;

				RingPlacer ringPlacer = new RingPlacer();
				int ringSize = c2dm.getRingSize();
				String symbol = c2dm.getDrawElement();
				AtomContainer sharedAtoms = getHighlighted();

				if (sharedAtoms.getAtomCount() == 0)
				{
					sharedAtoms = new AtomContainer();
					newRing = new Ring(ringSize, symbol);
					if (c2dm.getDrawMode() == c2dm.BENZENERING)
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
					AtomContainer atomCon = ChemModelManipulator.createNewMolecule(chemModel);
					atomCon.add(newRing);
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
				} else if (sharedAtoms.getAtomCount() == 1)
				{
					spiroAtom = sharedAtoms.getAtomAt(0);
					sharedAtomsCenter = sharedAtoms.get2DCenter();
					newRing = createAttachRing(sharedAtoms, ringSize, symbol);
					if (c2dm.getDrawMode() == c2dm.BENZENERING)
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
					AtomContainer atomCon = ChemModelManipulator.getRelevantAtomContainer(chemModel, spiroAtom);
					atomCon.add(newRing);
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
				} else if (sharedAtoms.getAtomCount() == 2)
				{
					sharedAtomsCenter = sharedAtoms.get2DCenter();

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

						AtomContainer atomCon = ChemModelManipulator.getRelevantAtomContainer(chemModel, firstAtom);

						// construct a new Ring that contains the highlighted bond an its two atoms
						newRing = createAttachRing(sharedAtoms, ringSize, symbol);
						if (c2dm.getDrawMode() == c2dm.BENZENERING)
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
				r2dm.fireChange();
				fireChange();
			}

			if (c2dm.getDrawMode() == c2dm.LASSO)
			{
				// first deselect all atoms
				r2dm.setSelectedPart(new AtomContainer());
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
					ChemObject chemObj = getChemObjectInRange(mouseX, mouseY);
					AtomContainer container = new AtomContainer();
					if (chemObj instanceof Atom)
					{
						container.addAtom((Atom) chemObj);
						logger.debug("selected one atom in lasso mode");
						r2dm.setSelectedPart(container);
					} else if (chemObj instanceof Bond)
					{
						Bond bond = (Bond) chemObj;
						container.addBond(bond);
						logger.debug("selected one bond in lasso mode");
						Atom[] atoms = bond.getAtoms();
						for (int i = 0; i < atoms.length; i++)
						{
							container.addAtom(atoms[i]);
						}
						r2dm.setSelectedPart(container);
					}
				}
				fireChange();
			}

			if (c2dm.getDrawMode() == c2dm.MOVE)
			{
				if (draggingSelected == false) {
					// then it was dragging nearest Bond or Atom
					r2dm.setSelectedPart(new AtomContainer());
				}
			}

			if (wasDragged) {
				prevDragCoordX = 0;
				prevDragCoordY = 0;
				wasDragged = false;
			}
            dragMode = DRAG_UNSET;
            r2dm.setPointerVectorStart(null);
            r2dm.setPointerVectorEnd(null);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  ring  Description of the Parameter
	 */
	private void makeRingAromatic(Ring ring)
	{
		Atom[] atoms = ring.getAtoms();
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
		logger.debug("Key typed");
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
	 *  Description of the Method
	 *
	 *@param  container  Description of the Parameter
	 *@param  atoms      Description of the Parameter
	 */
	private void updateAtoms(AtomContainer container, Atom[] atoms)
	{
		for (int i = 0; i < atoms.length; i++)
		{
			updateAtom(container, atoms[i]);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  container  Description of the Parameter
	 *@param  atom       Description of the Parameter
	 */
	private void updateAtom(AtomContainer container, Atom atom)
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
	 *  Description of the Method
	 *
	 *@param  angle  Description of the Parameter
	 *@return        Description of the Return Value
	 */
	private double snapAngle(double angle)
	{
		double div = (Math.PI / 180) * c2dm.getSnapAngle();
		return (Math.rint(angle / div)) * div;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  position  Description of the Parameter
	 *@return           Description of the Return Value
	 */
	private int snapCartesian(int position)
	{
		int div = c2dm.getSnapCartesian();
		return (int) (Math.rint(position / div)) * div;
	}


	/**
	 *  Gets the chemObjectInRange attribute of the Controller2D object
	 *
	 *@param  X  Description of the Parameter
	 *@param  Y  Description of the Parameter
	 *@return    The chemObjectInRange value
	 */
	protected ChemObject getChemObjectInRange(int X, int Y)
	{
		ChemObject objectInRange = getAtomInRange(X, Y);
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
	private Atom getAtomInRange(int X, int Y)
	{
		double highlightRadius = r2dm.getHighlightRadius();
		AtomContainer atomCon = ChemModelManipulator.getAllInOneContainer(chemModel);
		Atom closestAtom = GeometryTools.getClosestAtom(X, Y, atomCon);
		if (closestAtom != null)
		{
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
		AtomContainer atomCon = ChemModelManipulator.getAllInOneContainer(chemModel);
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
		return null;
	}


	/**
	 *  Returns a Reaction if the coordinate is within the reaction 'window'.
	 *
	 *@param  X  The x world coordinate of the point
	 *@param  Y  The y world coordinate of the point
	 *@return    A Reaction if it is in a certain range of the given point
	 */
	private Reaction getReactionInRange(int X, int Y)
	{
		SetOfReactions reactionSet = chemModel.getSetOfReactions();
		if (reactionSet != null)
		{
			// process reaction by reaction
			Reaction[] reactions = reactionSet.getReactions();
			for (int i = 0; i < reactions.length; i++)
			{
				AtomContainer atomContainer = ReactionManipulator.getAllInOneContainer(reactions[i]);
				double[] minmax = GeometryTools.getMinMax(atomContainer);
				if ((X <= minmax[2]) && (X >= minmax[0]) &&
						(Y <= minmax[3]) && (Y >= minmax[1]))
				{
					// cursor in in reaction bounding box
					return reactions[i];
				}
			}
		}
		return null;
	}


	/**
	 *  Returns an AtomContainer that contains the atom or the the bond with its
	 *  two atoms that are highlighted at the moment.
	 *
	 *@return    An AtomContainer containig the highlighted atom\atoms\bond
	 */
	private AtomContainer getHighlighted()
	{
		AtomContainer highlighted = new AtomContainer();
		Atom highlightedAtom = r2dm.getHighlightedAtom();
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
	 *  bonds of the given AtomContainer and is filled up with new Atoms and Bonds.
	 *
	 *@param  sharedAtoms  The AtomContainer containing the Atoms and bonds for the
	 *      new Ring
	 *@param  ringSize     The size (number of Atoms) the Ring will have
	 *@param  symbol       The element symbol the new atoms will have
	 *@return              The constructed Ring
	 */
	private Ring createAttachRing(AtomContainer sharedAtoms, int ringSize, String symbol)
	{
		Ring newRing = new Ring(ringSize);
		Atom[] ringAtoms = new Atom[ringSize];
		for (int i = 0; i < sharedAtoms.getAtomCount(); i++)
		{
			ringAtoms[i] = sharedAtoms.getAtomAt(i);
		}
		for (int i = sharedAtoms.getAtomCount(); i < ringSize; i++)
		{
			ringAtoms[i] = new Atom(symbol);
		}
		Bond[] bonds = sharedAtoms.getBonds();
		for (int i = 0; i < bonds.length; i++)
		{
			newRing.addBond(bonds[i]);
		}
		for (int i = sharedAtoms.getBondCount(); i < ringSize - 1; i++)
		{
			newRing.addBond(new Bond(ringAtoms[i], ringAtoms[i + 1], 1));
		}
		newRing.addBond(new Bond(ringAtoms[ringSize - 1], ringAtoms[0], 1));
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
	private Point2d getConnectedAtomsCenter(AtomContainer sharedAtoms)
	{
		Atom currentAtom;
		Atom[] conAtomsArray;
		AtomContainer conAtoms = new AtomContainer();
		AtomContainer atomCon = ChemModelManipulator.getAllInOneContainer(chemModel);
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
		return conAtoms.get2DCenter();
	}


	/**
	 *  Returns an AtomContainer with all the atoms and bonds that are inside a
	 *  given polygon.
	 *
	 *@param  polygon  The given Polygon
	 *@return          AtomContainer with all atoms and bonds inside the polygon
	 */
	private AtomContainer getContainedAtoms(Polygon polygon)
	{
		Atom currentAtom;
		Bond currentBond;
		AtomContainer selectedPart = new AtomContainer();
		AtomContainer atomCon = ChemModelManipulator.getAllInOneContainer(chemModel);
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
	 *@param  coords  Description of the Parameter
	 *@return         The worldCoordinates value
	 */
	protected int[] getWorldCoordinates(int[] coords)
	{
		int[] worldCoords = new int[coords.length];
		int coordCount = coords.length / 2;
		logger.debug("coord.length: ", coords.length);
		int height = (int) (r2dm.getBackgroundDimension()).getHeight();
		for (int i = 0; i < coordCount; i++)
		{
			worldCoords[i * 2] = (int) ((double) coords[i * 2] / r2dm.getZoomFactor());
			worldCoords[i * 2 + 1] = (int) ((double) (height - coords[i * 2 + 1]) / r2dm.getZoomFactor());
            if (logger.isDebugEnabled()) {
                logger.debug("getWorldCoord: " + coords[i * 2] + " -> " + worldCoords[i * 2]);
                logger.debug("getWorldCoord: " + coords[i * 2 + 1] + " -> " + worldCoords[i * 2 + 1]);
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
	private void highlightNearestChemObject(int mouseX, int mouseY)
	{
		ChemObject objectInRange = getChemObjectInRange(mouseX, mouseY);
		if (objectInRange instanceof Atom)
		{
			r2dm.setHighlightedAtom((Atom) objectInRange);
			r2dm.setHighlightedBond(null);
		} else if (objectInRange instanceof Bond)
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
	 *@param  startX  Description of the Parameter
	 *@param  startY  Description of the Parameter
	 *@param  endX    Description of the Parameter
	 *@param  endY    Description of the Parameter
	 */
	private void createNewBond(int startX, int startY, int endX, int endY)
	{

	}


	/**
	 *  Description of the Method
	 *
	 *@param  startX  Description of the Parameter
	 *@param  startY  Description of the Parameter
	 *@param  mouseX  Description of the Parameter
	 *@param  mouseY  Description of the Parameter
	 */
	private void drawProposedBond(int startX, int startY, int mouseX, int mouseY)
	{
		logger.debug("Drawing proposed bond...");
		int endX = 0;
		int endY = 0;
		double pointerVectorLength = c2dm.getBondPointerLength();
		double angle = 0;
		Atom atomInRange;

		angle = GeometryTools.getAngle(startX - mouseX, startY - mouseY);
		if (c2dm.getSnapToGridAngle()) {
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
	 *  Description of the Method
	 *
	 *@param  startX  Description of the Parameter
	 *@param  startY  Description of the Parameter
	 *@param  mouseX  Description of the Parameter
	 *@param  mouseY  Description of the Parameter
	 */
	private void selectRectangularArea(int startX, int startY, int mouseX, int mouseY)
	{
		int[] xPoints = {startX, startX, mouseX, mouseX};
		int[] yPoints = {startY, mouseY, mouseY, startY};
		r2dm.setSelectRect(new Polygon(xPoints, yPoints, 4));
	}


	/**
	 *  Move an Atom by the specified change in coordinates.
	 *
	 *@param  deltaX  Description of the Parameter
	 *@param  deltaY  Description of the Parameter
	 */
	private void moveSelectedAtomsWith(int deltaX, int deltaY)
	{
		AtomContainer container = r2dm.getSelectedPart();
		if (container != null)
		{
			// only move selected atoms if count > 0
			Atom[] atoms = container.getAtoms();
			for (int i = 0; i < atoms.length; i++)
			{
				Atom atom = atoms[i];
				atom.setX2d(atom.getX2d() + deltaX);
				atom.setY2d(atom.getY2d() + deltaY);
			}
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@param  mouseX  Description of the Parameter
	 *@param  mouseY  Description of the Parameter
	 */
	private void selectNearestChemObjectIfNoneSelected(int mouseX, int mouseY)
	{
		AtomContainer container = r2dm.getSelectedPart();
		if (container == null || (container.getAtomCount() == 0))
		{
			// if no atoms are selected, then temporarily select nearest
			// to make sure to original state is reached again when the
			// mouse is released, the draggingSelected boolean is set
			logger.warn("No atoms selected: temporarily selecting nearest atom/bond");
			draggingSelected = false;
			AtomContainer selected = new AtomContainer();
			Atom atomInRange = getAtomInRange(mouseX, mouseY);
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
					Atom[] atoms = bondInRange.getAtoms();
					for (int i = 0; i < atoms.length; i++)
					{
						selected.addAtom(atoms[i]);
					}
					r2dm.setSelectedPart(selected);
				}
			}
			logger.debug("Selected: ", selected);
			/*
			 *  PRESERVE THIS. This notifies the
			 *  the listener responsible for
			 *  undo and redo storage that it
			 *  should not store this change
			 */
			isUndoableChange = false;
			/*
			 *  ---
			 */
			fireChange();
		}
	}
}

