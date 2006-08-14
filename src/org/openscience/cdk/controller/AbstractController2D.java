/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
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
import org.openscience.cdk.applications.undoredo.AddAtomsAndBondsEdit;
import org.openscience.cdk.applications.undoredo.AdjustBondOrdersEdit;
import org.openscience.cdk.applications.undoredo.ChangeAtomSymbolEdit;
import org.openscience.cdk.applications.undoredo.ChangeChargeEdit;
import org.openscience.cdk.applications.undoredo.IUndoRedoHandler;
import org.openscience.cdk.applications.undoredo.MergeMoleculesEdit;
import org.openscience.cdk.applications.undoredo.MoveAtomEdit;
import org.openscience.cdk.applications.undoredo.RemoveAtomsAndBondsEdit;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.geometry.BondTools;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.AtomPlacer;
import org.openscience.cdk.layout.RingPlacer;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.SetOfMoleculesManipulator;

/**
 * Class that acts on MouseEvents and KeyEvents.
 *
 * @author         steinbeck
 * @author         egonw
 * @cdk.created    2005-05-02
 * @cdk.keyword    mouse events
 * @cdk.require    java1.4+
 * @cdk.module     control
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
	private final static int DRAG_ROTATE = 7;
	
	protected Vector lastAction=null;
	protected JButton moveButton=null;
	
	protected IChemModel chemModel;
	
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
	double moveoldX;
	double moveoldY;
	private IUndoRedoHandler undoRedoHandler;
	

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
		//this is the rotate feature
		if(c2dm.isMovingAllowed() && r2dm.getSelectedPart()!=null && r2dm.getHighlightedAtom()==null && r2dm.getHighlightedBond()==null && c2dm.getDrawMode() == Controller2DModel.LASSO){
			double xmin=Double.MAX_VALUE;
			double xmax=Double.MIN_VALUE;
			double ymin=Double.MAX_VALUE;
			double ymax=Double.MIN_VALUE;
			for(int i=0;i<r2dm.getSelectedPart().getAtomCount();i++){
				if(((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(i))).x>xmax)
					xmax=((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(i))).x;
				if(((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(i))).y>ymax)
					ymax=((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(i))).y;
				if(((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(i))).x<xmin)
					xmin=((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(i))).x;
				if(((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(i))).y<ymin)
					ymin=((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(i))).y;
			}
			if(mouseCoords[0]>xmin && mouseCoords[0]<xmax && mouseCoords[1]>ymin && mouseCoords[1]<ymax){
				//ok, we want to rotate
				r2dm.setRotateCenter(xmin+(xmax-xmin)/2,ymin+(ymax-ymin)/2);
				r2dm.setRotateRadius(Math.min((xmax-xmin)/4,(ymax-ymin)/4));
			}
		}else{
			//no rotation
			r2dm.setRotateRadius(0);
		}
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
				IAtom inrange=getAtomInRange((int)((Point2d)r2dm.getRenderingCoordinate(selected.getAtom(i))).x, (int)((Point2d)r2dm.getRenderingCoordinate(selected.getAtom(i))).y, selected.getAtom(i));
				if(inrange!=null && inrange!=selected.getAtom(i)){
					r2dm.getMerge().put(selected.getAtom(i),inrange);
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
		} else if(dragMode==DRAG_ROTATE){
			double angle=BondTools.giveAngleBothMethods(new Point2d(r2dm.getRotateCenter()[0],r2dm.getRotateCenter()[1]),new Point2d(prevDragCoordX,prevDragCoordY),new Point2d(mouseX, mouseY),true);
			Polygon polygon=new Polygon();
			for(int i=0;i<r2dm.getSelectedPart().getAtomCount();i++) {
				polygon.addPoint((int)(((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(i))).x*1000),(int)(((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(i))).y*1000));
			}
			polygon.addPoint((int)(((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(0))).x*1000),(int)(((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(0))).y*1000));
			AffineTransform at=AffineTransform.getRotateInstance(angle,r2dm.getRotateCenter()[0]*1000,r2dm.getRotateCenter()[1]*1000);
			Shape transformedpolygon=at.createTransformedShape(polygon);
			PathIterator pa=transformedpolygon.getPathIterator(null);
			
			for(int i=0;i<r2dm.getSelectedPart().getAtomCount();i++) {
				double[] d=new double[6];
				pa.currentSegment(d);
				r2dm.setRenderingCoordinate(r2dm.getSelectedPart().getAtom(i),new Point2d(d[0]/1000,d[1]/1000));
				pa.next();
			}
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
		IBond bondInRange = getBondInRange(mouseX, mouseY);
		if (atomInRange != null)
		{
			startX = (int) ((Point2d)r2dm.getRenderingCoordinate(atomInRange)).x;
			startY = (int) ((Point2d)r2dm.getRenderingCoordinate(atomInRange)).y;
			r2dm.setPointerVectorStart(new Point(startX, startY));
		} else
		{
			r2dm.setPointerVectorStart(new Point(mouseX, mouseY));
		}
		
		if(r2dm.getSelectedPart()!=null &&
		   !((atomInRange == null) || (atomInRange == null)) &&
		   !(r2dm.getSelectedPart().contains(atomInRange) ||
		   r2dm.getSelectedPart().contains(bondInRange)) && 
		   r2dm.getRotateRadius()==0){
			r2dm.setSelectedPart(atomInRange.getBuilder().newAtomContainer());
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
		} else if (c2dm.getDrawMode() == Controller2DModel.LASSO && r2dm.getRotateRadius()==0)
		{
			if(c2dm.isMovingAllowed() && r2dm.getSelectedPart()!=null && (r2dm.getSelectedPart().contains(r2dm.getHighlightedAtom()) || r2dm.getSelectedPart().contains(r2dm.getHighlightedBond()))){
				if(r2dm.getSelectedPart().getAtomCount()>0)
					c2dm.setDrawMode(Controller2DModel.MOVE);
				if(lastAction!=null){
					((JButton)lastAction.get(0)).setBackground(Color.LIGHT_GRAY);
					lastAction.set(0,moveButton);
					moveButton.setBackground(Color.GRAY);
				}
				dragMode = DRAG_MOVING_SELECTED;
			}
			else{
				dragMode = DRAG_MAKING_LASSO_SELECTION;
			}
		} else if (c2dm.getDrawMode() == Controller2DModel.RING ||
				c2dm.getDrawMode() == Controller2DModel.BENZENERING)
		{
			dragMode = DRAG_DRAWING_PROPOSED_RING;
		} else if(r2dm.getRotateRadius()!=0){
			dragMode = DRAG_ROTATE;
		}
		
		if(dragMode==DRAG_MOVING_SELECTED){
			if (r2dm.getSelectedPart() != null && r2dm.getSelectedPart().getAtom(0) != null) {
				moveoldX=((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(0))).x;
				moveoldY=((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(0))).y;
			}
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
				changeSymbol();
			}
			if (c2dm.getDrawMode() == Controller2DModel.ELEMENT)
			{
				changeElement();
			}

			if (c2dm.getDrawMode() == Controller2DModel.INCCHARGE)
			{
				increaseCharge();
			}
			if (c2dm.getDrawMode() == Controller2DModel.ENTERELEMENT)
			{
				enterElement();
			}
			if (c2dm.getDrawMode() == Controller2DModel.DECCHARGE)
			{
				decreaseCharge();
			}

			if (c2dm.getDrawMode() == Controller2DModel.MAPATOMATOM)
			{
				handleMapping(wasDragged, r2dm);
			}
      
			if (c2dm.getDrawMode() == Controller2DModel.DRAWBOND || c2dm.getDrawMode() == Controller2DModel.DOWN_BOND || c2dm.getDrawMode() == Controller2DModel.UP_BOND)
			{
				drawBond(mouseX, mouseY);
			}

			if (c2dm.getDrawMode() == Controller2DModel.SELECT && wasDragged)
			{
				logger.info("User asks to selected atoms");
				IAtomContainer selectedPart = chemModel.getBuilder().newAtomContainer();
				r2dm.setSelectedPart(selectedPart);
				r2dm.setSelectedPart(getContainedAtoms(r2dm.getSelectRect()));
				r2dm.setSelectRect(null);
				logger.debug("selected stuff  ", selectedPart);
			}

			if (c2dm.getDrawMode() == Controller2DModel.ERASER)
			{
				eraseSelection();
			}

			if (c2dm.getDrawMode() == Controller2DModel.RING || c2dm.getDrawMode() == Controller2DModel.BENZENERING)
			{
				drawRing(mouseX, mouseY);
			}

			if (c2dm.getDrawMode() == Controller2DModel.LASSO && r2dm.getRotateRadius()==0)
			{
				// first deselect all atoms
				r2dm.setSelectedPart(chemModel.getBuilder().newAtomContainer());
				// now select new atoms
				if (wasDragged)
				{
					lassoSelection();

				} else
				{
					singleObjectSelected(mouseX, mouseY);
				}
				fireChange();
			}

			if (wasDragged)
			{
				prevDragCoordX = 0;
				prevDragCoordY = 0;
				wasDragged = false;
			}
			if (dragMode==DRAG_MOVING_SELECTED){
				dragAndDropSelection();
			}
			
			if (c2dm.getDrawMode() == Controller2DModel.MOVE)
			{
				if (draggingSelected == false)
				{					
					// then it was dragging nearest Bond or Atom
					r2dm.setSelectedPart(chemModel.getBuilder().newAtomContainer());
				}
				if(r2dm.getMerge().size()>0){
					mergeMolecules();
					this.updateMoleculeCoordinates();
				}
			} 
			
			dragMode = DRAG_UNSET;
			r2dm.setPointerVectorStart(null);
			r2dm.setPointerVectorEnd(null);
		}
		if (shiftX != 0 || shiftY != 0)
		{
			shiftMolecule();
		}
		shiftX = 0;
		shiftY = 0;
	}


	private void shiftMolecule() {
		for (int i = 0; i < chemModel.getSetOfMolecules().getMoleculeCount(); i++)
		{
			IMolecule mol = chemModel.getSetOfMolecules().getMolecules()[i];
			for (int k = 0; k < mol.getAtomCount(); k++)
			{
				((Point2d)r2dm.getRenderingCoordinate(mol.getAtom(k))).x=((Point2d)r2dm.getRenderingCoordinate(mol.getAtom(k))).x -shiftX;
				((Point2d)r2dm.getRenderingCoordinate(mol.getAtom(k))).y=((Point2d)r2dm.getRenderingCoordinate(mol.getAtom(k))).y -shiftY;
			}
		}
		r2dm.fireChange();
		fireChange();
	}

	private void dragAndDropSelection() {
		double deltaX=0;
		double deltaY=0;
		IAtomContainer undoredoContainer = chemModel.getBuilder().newAtomContainer();
		if(r2dm.getSelectedPart()!=null && r2dm.getSelectedPart().getAtomCount()>0){
			undoredoContainer.add(r2dm.getSelectedPart());
			deltaX=((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(0))).x-moveoldX;
			deltaY=((Point2d)r2dm.getRenderingCoordinate(r2dm.getSelectedPart().getAtom(0))).y-moveoldY;
			
		}else if(r2dm.getHighlightedAtom()!=null){
			deltaX=((Point2d)r2dm.getRenderingCoordinate(r2dm.getHighlightedAtom())).x-moveoldX;
			deltaY=((Point2d)r2dm.getRenderingCoordinate(r2dm.getHighlightedAtom())).y-moveoldY;
			undoredoContainer.addAtom(r2dm.getHighlightedAtom());
		}else if (r2dm.getHighlightedBond()!=null){
			deltaX=((Point2d)r2dm.getRenderingCoordinate(r2dm.getHighlightedBond().getAtom(0))).x-moveoldX;
			deltaY=((Point2d)r2dm.getRenderingCoordinate(r2dm.getHighlightedBond().getAtom(0))).y-moveoldY;	
		}
		UndoableEdit edit = new MoveAtomEdit(undoredoContainer, (int)deltaX, (int)deltaY, r2dm.getRenderingCoordinates());
		undoRedoHandler.postEdit(edit);
	}

	private void mergeMolecules() {
		Iterator it=r2dm.getMerge().keySet().iterator();
		IBond[] bondson2 = null;
		IAtom atom2 = null;
		IAtom atom1 = null;
		ArrayList undoredoContainer = new ArrayList();
		while(it.hasNext()){
			Object[] undoObject = new Object[3];
			atom1=(IAtom)it.next();
			atom2=(IAtom)r2dm.getMerge().get(atom1);
			undoObject[0] = atom1;
			undoObject[1] = atom2;
			IMoleculeSet som=chemModel.getSetOfMolecules();
			IAtomContainer container1 = ChemModelManipulator.getRelevantAtomContainer(chemModel, atom1);
			IAtomContainer container2 = ChemModelManipulator.getRelevantAtomContainer(chemModel, atom2);
			if (container1 != container2) {
				container1.add(container2);
				som.removeAtomContainer(container2);
			}
			bondson2=container1.getConnectedBonds(atom2);
			undoObject[2] = bondson2;
			undoredoContainer.add(undoObject);
			for(int i=0;i<bondson2.length;i++){
				if(bondson2[i].getAtom(0)==atom2)
					bondson2[i].setAtomAt(atom1,0);
				if(bondson2[i].getAtom(1)==atom2)
					bondson2[i].setAtomAt(atom1,1);
				if(bondson2[i].getAtom(0)==bondson2[i].getAtom(1)){
					container1.removeElectronContainer(bondson2[i]);
				}
			}
			container1.removeAtom(atom2);
		}
		UndoableEdit  edit = new MergeMoleculesEdit(chemModel, undoredoContainer, "Molecules merged");
		undoRedoHandler.postEdit(edit);
		r2dm.getMerge().clear();
	}

	private void singleObjectSelected(int mouseX, int mouseY) {
//		 one atom clicked or one bond clicked
		IChemObject chemObj = getChemObjectInRange(mouseX, mouseY);
		IAtomContainer container = chemObj.getBuilder().newAtomContainer();
		if (chemObj instanceof IAtom)
		{
			container.addAtom((IAtom) chemObj);
			logger.debug("selected one atom in lasso mode");
			r2dm.setSelectedPart(container);
		} else if (chemObj instanceof IBond)
		{
			IBond bond = (IBond) chemObj;
			container.addBond(bond);
			logger.debug("selected one bond in lasso mode");
			IAtom[] atoms = bond.getAtoms();
			for (int i = 0; i < atoms.length; i++)
			{
				container.addAtom(atoms[i]);
			}
			r2dm.setSelectedPart(container);
		}
	}

	private void lassoSelection() {
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
	}

	private void drawRing(int mouseX, int mouseY) {
		this.updateMoleculeCoordinates();
		IAtomContainer undoRedoContainer = chemModel.getBuilder().newAtomContainer();
		IRing newRing = null;
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
			sharedAtoms = sharedAtoms.getBuilder().newAtomContainer();
			newRing = sharedAtoms.getBuilder().newRing(ringSize, symbol);
			if (c2dm.getDrawMode() == Controller2DModel.BENZENERING)
			{
				// make newRing a benzene ring
				IBond[] bonds = newRing.getBonds();
				bonds[0].setOrder(2.0);
				bonds[2].setOrder(2.0);
				bonds[4].setOrder(2.0);
				makeRingAromatic(newRing);
			}
			bondLength = r2dm.getBondLength();
			ringRadius = (bondLength / 2) / Math.sin(Math.PI / c2dm.getRingSize());
			sharedAtomsCenter = new Point2d(mouseX, mouseY - ringRadius);
			firstAtom = newRing.getAtom(0);
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
			spiroAtom = sharedAtoms.getAtom(0);
			sharedAtomsCenter = GeometryTools.get2DCenter(sharedAtoms,r2dm.getRenderingCoordinates());
			newRing = createAttachRing(sharedAtoms, ringSize, symbol);
			if (c2dm.getDrawMode() == Controller2DModel.BENZENERING)
			{
				// make newRing a benzene ring
				IBond[] bonds = newRing.getBonds();
				bonds[0].setOrder(2.0);
				bonds[2].setOrder(2.0);
				bonds[4].setOrder(2.0);
				makeRingAromatic(newRing);
			}
			bondLength = r2dm.getBondLength();
			conAtomsCenter = getConnectedAtomsCenter(sharedAtoms);
			if (conAtomsCenter.equals(((Point2d)r2dm.getRenderingCoordinate(spiroAtom))))
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
			IAtomContainer atomCon = ChemModelManipulator.getRelevantAtomContainer(chemModel, spiroAtom);
			atomCon.add(newRing);
			undoRedoContainer.add(newRing);
		} else if (sharedAtoms.getAtomCount() == 2)
		{
			sharedAtomsCenter = GeometryTools.get2DCenter(sharedAtoms,r2dm.getRenderingCoordinates());

			// calculate two points that are perpendicular to the highlighted bond
			// and have a certain distance from the bondcenter
			firstAtom = sharedAtoms.getAtom(0);
			secondAtom = sharedAtoms.getAtom(1);
			xDiff = ((Point2d)r2dm.getRenderingCoordinate(secondAtom)).x - ((Point2d)r2dm.getRenderingCoordinate(firstAtom)).x;
			yDiff = ((Point2d)r2dm.getRenderingCoordinate(secondAtom)).y - ((Point2d)r2dm.getRenderingCoordinate(firstAtom)).y;
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
			//shk3: I removed this condition since it leads to npe and I cannot see why it is necessary.
			//the npe is if you draw a single bond and want to attach a ring to it. Now the ring is in an arbitrary direction, but this is ok
			/*if (distance1 == distance2)
			{
				logger.warn("don't know where to draw the new Ring");
			} else*/
			{
				if (distance1 < distance2)
				{
					ringCenterVector.sub(newPoint1);
				} else if (distance2 < distance1)
				{
					ringCenterVector.sub(newPoint2);
				} else{
					ringCenterVector.sub(newPoint2);
				}

				IAtomContainer atomCon = ChemModelManipulator.getRelevantAtomContainer(chemModel, firstAtom);

				// construct a new Ring that contains the highlighted bond an its two atoms
				newRing = createAttachRing(sharedAtoms, ringSize, symbol);
				if (c2dm.getDrawMode() == Controller2DModel.BENZENERING)
				{
					// make newRing a benzene ring
					IBond existingBond = atomCon.getBond(firstAtom, secondAtom);
					IBond[] bonds = newRing.getBonds();

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
			IAtom atom=newRing.getAtom(i);
			r2dm.setRenderingCoordinate(atom,new Point2d(atom.getPoint2d()));
		}
		for (int i = 0; i < newRing.getAtomCount(); i++)
		{
			IAtom atom=newRing.getAtom(i);
			r2dm.setRenderingCoordinate(atom,new Point2d(atom.getPoint2d()));
			centerAtom(atom,chemModel);
			//We make sure atoms don't overlap
			//Solution is a bit crude, we would need to find an unoccupied place (and even the bond display should be optimized)
			IAtom inrange=getAtomInRange((int)((Point2d)r2dm.getRenderingCoordinate(atom)).x, (int)((Point2d)r2dm.getRenderingCoordinate(atom)).y, atom);
			if(inrange!=null && Math.sqrt(Math.pow(((Point2d)r2dm.getRenderingCoordinate(inrange)).x - ((Point2d)r2dm.getRenderingCoordinate(atom)).x, 2) + Math.pow(((Point2d)r2dm.getRenderingCoordinate(inrange)).y - ((Point2d)r2dm.getRenderingCoordinate(atom)).y, 2)) < highlightRadius/4){
				((Point2d)r2dm.getRenderingCoordinate(atom)).x-=highlightRadius/4;
				((Point2d)r2dm.getRenderingCoordinate(atom)).y-=highlightRadius/4;
			}
		}
		this.updateMoleculeCoordinates();
		this.updateAtoms(ChemModelManipulator.getRelevantAtomContainer(chemModel, newRing.getAtom(0)), newRing.getAtoms());
		undoRedoContainer.add(newRing);
		UndoableEdit  edit = new AddAtomsAndBondsEdit(chemModel, undoRedoContainer, "Added Ring");
		undoRedoHandler.postEdit(edit);
		r2dm.fireChange();
		fireChange();
	}

	private void eraseSelection() {
		IAtomContainer undoRedoContainer = chemModel.getBuilder().newAtomContainer();
		String type = null;
		IAtom highlightedAtom = r2dm.getHighlightedAtom();
		IBond highlightedBond = r2dm.getHighlightedBond();
		if (highlightedAtom != null && (r2dm.getSelectedPart()==null || !r2dm.getSelectedPart().contains(highlightedAtom)))
		{
			logger.info("User asks to delete an Atom");
			IAtomContainer container = ChemModelManipulator.getAllInOneContainer(chemModel);
			logger.debug("Atoms before delete: ", container.getAtomCount());
			ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel, highlightedAtom);
			IElectronContainer[] eContainer = container.getConnectedElectronContainers(highlightedAtom);
			for (int i=0; i<eContainer.length; i++) {
				undoRedoContainer.addBond((IBond) eContainer[i]);
			}
			undoRedoContainer.addAtom(highlightedAtom);
			if (type == null) {
				type = "Remove Atom";
			}
			else {
				type = "Remove Substructure";
			}
			container = ChemModelManipulator.getAllInOneContainer(chemModel);
			logger.debug("Atoms before delete: ", container.getAtomCount());
			// update atoms
			IAtom[] atoms = container.getConnectedAtoms(highlightedAtom);
			if (atoms.length > 0)
			{
				IAtomContainer atomCon = ChemModelManipulator.getRelevantAtomContainer(chemModel, atoms[0]);
				updateAtoms(atomCon, atoms);
			}
		} else if (highlightedBond != null && (r2dm.getSelectedPart()==null || !r2dm.getSelectedPart().contains(highlightedBond)))
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
			IAtom[] atoms = highlightedBond.getAtoms();
			IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atoms[0]);
			updateAtoms(container, atoms);
		} else if(r2dm.getSelectedPart()!=null && (r2dm.getSelectedPart().getAtomCount()>0 || r2dm.getSelectedPart().getBondCount()>0)){
			logger.info("User asks to delete selected part");
			for(int i=0;i<r2dm.getSelectedPart().getAtomCount();i++){
				ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel,r2dm.getSelectedPart().getAtom(i));
				undoRedoContainer.addAtom(r2dm.getSelectedPart().getAtom(i));
			}
			for(int i=0;i<r2dm.getSelectedPart().getBondCount();i++){
				ChemModelManipulator.removeElectronContainer(chemModel,r2dm.getSelectedPart().getBond(i));
				undoRedoContainer.addBond(r2dm.getSelectedPart().getBond(i));
			}
			type = "Remove Substructure";
			// update atoms
			IAtomContainer container = ChemModelManipulator.getAllInOneContainer(chemModel);
			IAtom[] atoms = r2dm.getSelectedPart().getAtoms();
			updateAtoms(container, atoms);
			
		}else
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
		undoRedoHandler.postEdit(edit);
		r2dm.fireChange();
		fireChange();
	}

	private void drawBond(int mouseX, int mouseY) {
		this.updateMoleculeCoordinates();
		logger.debug("mouseReleased->drawbond");
		IAtom atomInRange;
		IAtom newAtom1 = null;
		IAtom newAtom2 = null;
		IBond newBond = null;
		int startX = r2dm.getPointerVectorStart().x;
		int startY = r2dm.getPointerVectorStart().y;
		IBond bondInRange = r2dm.getHighlightedBond();
		

		
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
//			update atoms
			IAtom[] atoms = bondInRange.getAtoms();
			IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atoms[0]);
			updateAtoms(container, atoms);
			HashMap changedBonds = new HashMap();
			double formerBondOrder = bondInRange.getOrder();
			if (c2dm.getDrawMode() == Controller2DModel.DRAWBOND){
				// increase Bond order
				double order = bondInRange.getOrder();
				if (order >= CDKConstants.BONDORDER_TRIPLE)
				{
					bondInRange.setOrder(CDKConstants.BONDORDER_SINGLE);
				} else {
					bondInRange.setOrder(order + 1.0);
					// this is tricky as it depends on the fact that the
					// constants are unidistant, i.e. {1.0, 2.0, 3.0}.
				}
				;
				
				
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
			if (bondInRange.getOrder() != formerBondOrder) {
                double[] bondOrders = new double[2];
                bondOrders[0] = bondInRange.getOrder();
                bondOrders[1] = formerBondOrder;
                changedBonds.put(bondInRange, bondOrders);
            }
			isUndoableChange = true;
			/*
			 *  ---
			 */
			UndoableEdit  edit = new AdjustBondOrdersEdit(changedBonds);
			undoRedoHandler.postEdit(edit);
			updateAtoms(container, atoms);
		} else
		{
			IAtomContainer undoRedoContainer = chemModel.getBuilder().newAtomContainer();
			if (atomInRange != null)
			{
				logger.debug("We had an atom in range");
				newAtom1 = atomInRange;
			} else if (!wasDragged)
			{
				// create a new molecule
				logger.debug("We make a new molecule");
				newAtom1 = undoRedoContainer.getBuilder().newAtom(c2dm.getDrawElement(), new Point2d(startX, startY));
				IAtomContainer atomCon = ChemModelManipulator.createNewMolecule(chemModel);
				atomCon.addAtom(newAtom1);
				r2dm.setRenderingCoordinate(newAtom1,new Point2d(startX, startY));
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
					IAtomContainer atomCon = ChemModelManipulator.getAllInOneContainer(chemModel);
					if (atomInRange != null)
					{
						logger.debug("*** atom in range");

						newAtom2 = atomInRange;
						logger.debug("atomCon.getAtomCount() " + atomCon.getAtomCount());
					} else
					{
						logger.debug("*** new atom");
						newAtom2 = atomCon.getBuilder().newAtom(c2dm.getDrawElement(), new Point2d(endX, endY));
						atomCon.addAtom(newAtom2);
						r2dm.setRenderingCoordinate(newAtom2,new Point2d(endX, endY));
						undoRedoContainer.addAtom(newAtom2);
					}
					newAtom1 = lastAtomInRange;
					if (newAtom1 == null)
					{
						newAtom1 = atomCon.getBuilder().newAtom(c2dm.getDrawElement(), new Point2d(r2dm.getPointerVectorStart().x, r2dm.getPointerVectorStart().y));
						undoRedoContainer.addAtom(newAtom1);
					}
					if (newAtom1 != newAtom2)
					{
						newBond = atomCon.getBuilder().newBond(newAtom1, newAtom2, 1);
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
						IMoleculeSet setOfMolecules = ConnectivityChecker.partitionIntoMolecules(atomCon);
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
				IAtomContainer atomCon = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInRange);
				newAtom2 = atomCon.getBuilder().newAtom(c2dm.getDrawElement(), ((Point2d)r2dm.getRenderingCoordinate(atomInRange)));

				// now create 2D coords for new atom
				double bondLength = r2dm.getBondLength();
				IAtom[] connectedAtoms = atomCon.getConnectedAtoms(atomInRange);
				logger.debug("connectedAtoms.length: " + connectedAtoms.length);
				IAtomContainer placedAtoms = atomCon.getBuilder().newAtomContainer();
				//placedAtoms.addAtom(atomInRange);
				for (int i = 0; i < connectedAtoms.length; i++)
				{
					placedAtoms.addAtom(connectedAtoms[i]);
				}
				IAtomContainer unplacedAtoms = atomCon.getBuilder().newAtomContainer();
				unplacedAtoms.addAtom(newAtom2);
				AtomPlacer atomPlacer = new AtomPlacer();
				atomPlacer.setMolecule(atomCon.getBuilder().newMolecule(atomCon));
				Point2d center2D = GeometryTools.get2DCenter(placedAtoms);
				logger.debug("placedAtoms.getAtomCount(): " + placedAtoms.getAtomCount());
				logger.debug("unplacedAtoms.getAtomCount(): " + unplacedAtoms.getAtomCount());
				if (placedAtoms.getAtomCount() == 1)
				{
					Vector2d bondVector = atomPlacer.getNextBondVector(
							atomInRange, placedAtoms.getAtom(0), 
							GeometryTools.get2DCenter(atomCon.getBuilder().newMolecule(atomCon)),
							true // FIXME: is this correct? (see SF bug #1367002)
					);
					Point2d atomPoint = new Point2d(((Point2d)r2dm.getRenderingCoordinate(atomInRange)));
					bondVector.normalize();
					bondVector.scale(bondLength);
					atomPoint.add(bondVector);
					newAtom2.setPoint2d(atomPoint);
					r2dm.setRenderingCoordinate(newAtom2,atomPoint);

				} else
				{
					atomPlacer.distributePartners(atomInRange, placedAtoms, center2D,
							unplacedAtoms, bondLength);
				}

				// now add the new atom
				atomCon.addAtom(newAtom2);
				undoRedoContainer.addAtom(newAtom2);
				r2dm.setRenderingCoordinate(newAtom2,new Point2d(newAtom2.getPoint2d()));
				newBond= undoRedoContainer.getBuilder().newBond(atomInRange, newAtom2, 1.0);
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
			undoRedoHandler.postEdit(edit);
		}
		r2dm.fireChange();
		fireChange();
		if(newAtom1!=null && r2dm.getRenderingCoordinate(newAtom1)==null)
			r2dm.setRenderingCoordinate(newAtom1,new Point2d(newAtom1.getPoint2d()));
		if(newAtom2!=null)
			r2dm.setRenderingCoordinate(newAtom2,new Point2d(newAtom2.getPoint2d()));
		centerAtom(newAtom1,chemModel);
		centerAtom(newAtom2,chemModel);
		this.updateMoleculeCoordinates();
	}

	private void decreaseCharge() {
		IAtom atomInRange = r2dm.getHighlightedAtom();
		if (atomInRange != null)
		{
			int formerCharge = atomInRange.getFormalCharge();
			atomInRange.setFormalCharge(atomInRange.getFormalCharge() - 1);
			// update atom
			IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInRange);
			updateAtom(container, atomInRange);
			//undoredo support
            UndoableEdit  edit = new ChangeChargeEdit(atomInRange, formerCharge, atomInRange.getFormalCharge());
			undoRedoHandler.postEdit(edit);
			r2dm.fireChange();
			fireChange();
		}
	}

	private void enterElement() {
		IAtom atomInRange = r2dm.getHighlightedAtom();
		if (atomInRange != null)
		{
			String x=JOptionPane.showInputDialog(null,"Enter new element symbol");
			try{
				if(Character.isLowerCase(x.toCharArray()[0]))
					x=Character.toUpperCase(x.charAt(0))+x.substring(1);
				IsotopeFactory ifa=IsotopeFactory.getInstance(r2dm.getHighlightedAtom().getBuilder());
				IIsotope iso=ifa.getMajorIsotope(x);
				String formerSymbol=r2dm.getHighlightedAtom().getSymbol();
				if(iso!=null)
					r2dm.getHighlightedAtom().setSymbol(x);
				// update atom
				IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInRange);
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
				// undoredo support
				UndoableEdit  edit = new ChangeAtomSymbolEdit(atomInRange, formerSymbol, x);
				undoRedoHandler.postEdit(edit);
				r2dm.fireChange();
				fireChange();
			}catch(Exception ex){
				logger.debug(ex.getMessage()+" in SELECTELEMENT");
			}
		}
	}

	private void increaseCharge() {
		IAtom atomInRange = r2dm.getHighlightedAtom();
		if (atomInRange != null)
		{
			int formerCharge = atomInRange.getFormalCharge();
			atomInRange.setFormalCharge(atomInRange.getFormalCharge() + 1);

			// update atom
			IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInRange);
			updateAtom(container, atomInRange);
			//undoredo support
            UndoableEdit  edit = new ChangeChargeEdit(atomInRange, formerCharge, atomInRange.getFormalCharge());
			undoRedoHandler.postEdit(edit);
			r2dm.fireChange();
			fireChange();
		}
	}

	private void changeElement() {
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
				IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInRange);
				updateAtom(container, atomInRange);
				
				/*
				 *  PRESERVE THIS. This notifies the
				 *  the listener responsible for
				 *  undo and redo storage that it
				 *  should store this change of an atom symbol
				 */
//				isUndoableChange = true;
				/*
				 *  ---
				 */
				// undoredo support
				UndoableEdit  edit = new ChangeAtomSymbolEdit(atomInRange, formerSymbol, symbol);
				undoRedoHandler.postEdit(edit);
				r2dm.fireChange();
				fireChange();
			}
		}else{
			int startX = r2dm.getPointerVectorStart().x;
			int startY = r2dm.getPointerVectorStart().y;
			IAtom newAtom1 = chemModel.getBuilder().newAtom(c2dm.getDrawElement(), new Point2d(startX, startY));
			r2dm.setRenderingCoordinate(newAtom1,new Point2d(startX, startY));
			IAtomContainer atomCon = ChemModelManipulator.createNewMolecule(chemModel);
			atomCon.addAtom(newAtom1);
			// update atoms
			updateAtom(atomCon, newAtom1);
			chemModel.getSetOfMolecules().addAtomContainer(atomCon);
			//FIXME undoredo
			IAtomContainer undoRedoContainer= chemModel.getBuilder().newAtomContainer();
			undoRedoContainer.addAtom(newAtom1);
			UndoableEdit  edit = new AddAtomsAndBondsEdit(chemModel, undoRedoContainer, "Add Atom");
			undoRedoHandler.postEdit(edit);
			this.updateMoleculeCoordinates();
			r2dm.fireChange();
			fireChange();
		}
	}

	private void changeSymbol() {
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
				IAtomContainer container = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInRange);
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
                undoRedoHandler.postEdit(edit);
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

	/**
	 *  Makes a ring aromatic
	 *
	 *@param  ring  The ring to be made aromatic
	 */
	private void makeRingAromatic(IRing ring)
	{
		IAtom[] atoms = ring.getAtoms();
		for (int i = 0; i < atoms.length; i++)
		{
			atoms[i].setFlag(CDKConstants.ISAROMATIC, true);
		}
		IBond[] bonds = ring.getBonds();
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
				IIsotope iso=ifa.getMajorIsotope(e.getKeyChar());
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
	private void updateAtoms(IAtomContainer container, IAtom[] atoms)
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
	private void updateAtom(IAtomContainer container, IAtom atom)
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
		IAtom closestAtom = GeometryTools.getClosestAtom(X, Y, chemModel, ignore, r2dm.getRenderingCoordinates());
		if (closestAtom != null)
		{
			//logger.debug("getAtomInRange(): An atom is near");
			if (!(Math.sqrt(Math.pow(r2dm.getRenderingCoordinate(closestAtom).x - X, 2) +
					Math.pow(r2dm.getRenderingCoordinate(closestAtom).y - Y, 2)) < highlightRadius))
			{
				closestAtom = null;
			} else {
				// we got a winner!
				// set the associated AtomContainer, for use by JCP's Molecule Properties action
                // COMMENTED OUT: causes cloning trouble
/*				closestAtom.setProperty(
					SimpleController2D.MATCHING_ATOMCONTAINER,
					ChemModelManipulator.getRelevantAtomContainer(chemModel, closestAtom)
				);*/
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
	private IBond getBondInRange(int X, int Y)
	{
        double highlightRadius = r2dm.getHighlightRadius();
		IAtomContainer atomCon = ChemModelManipulator.getAllInOneContainer(chemModel);
        if (atomCon.getBondCount() != 0) {
            IBond closestBond = GeometryTools.getClosestBond(X, Y, atomCon,r2dm.getRenderingCoordinates());
    		if (closestBond == null)
    		{
    			return null;
    		}
    		// logger.debug("closestBond  "+ closestBond);
    		int[] coords = GeometryTools.distanceCalculator(
    				GeometryTools.getBondCoordinates(closestBond, r2dm.getRenderingCoordinates()), highlightRadius);
    		int[] xCoords = {coords[0], coords[2], coords[4], coords[6]};
    		int[] yCoords = {coords[1], coords[3], coords[5], coords[7]};
    		if ((new Polygon(xCoords, yCoords, 4)).contains(new Point(X, Y)))
    		{
    			return closestBond;
    		}
        }
		return null;
	}

	abstract IReaction getReactionInRange(int X, int Y);

	/**
	 *  Returns an AtomContainer that contains the atom or the the bond with its
	 *  two atoms that are highlighted at the moment.
	 *
	 *@return    An AtomContainer containig the highlighted atom\atoms\bond
	 */
	 IAtomContainer getHighlighted()
	{
		IAtomContainer highlighted = chemModel.getBuilder().newAtomContainer();
		IAtom highlightedAtom = r2dm.getHighlightedAtom();
		IBond highlightedBond = r2dm.getHighlightedBond();
		if (highlightedAtom != null)
		{
			highlighted.addAtom(highlightedAtom);
		} else if (highlightedBond != null)
		{
			highlighted.addBond(highlightedBond);
			for (int i = 0; i < highlightedBond.getAtomCount(); i++)
			{
				highlighted.addAtom(highlightedBond.getAtom(i));
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
	 IRing createAttachRing(IAtomContainer sharedAtoms, int ringSize, String symbol)
	{
		IRing newRing = sharedAtoms.getBuilder().newRing(ringSize);
		IAtom[] ringAtoms = new IAtom[ringSize];
		for (int i = 0; i < sharedAtoms.getAtomCount(); i++)
		{
			ringAtoms[i] = sharedAtoms.getAtom(i);
		}
		for (int i = sharedAtoms.getAtomCount(); i < ringSize; i++)
		{
			ringAtoms[i] = sharedAtoms.getBuilder().newAtom(symbol);
		}
		IBond[] bonds = sharedAtoms.getBonds();
		for (int i = 0; i < bonds.length; i++)
		{
			newRing.addBond(bonds[i]);
		}
		for (int i = sharedAtoms.getBondCount(); i < ringSize - 1; i++)
		{
			newRing.addBond(sharedAtoms.getBuilder().newBond(ringAtoms[i], ringAtoms[i + 1], 1));
		}
		newRing.addBond(sharedAtoms.getBuilder().newBond(ringAtoms[ringSize - 1], ringAtoms[0], 1));
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
		IAtomContainer conAtoms = sharedAtoms.getBuilder().newAtomContainer();
		IAtomContainer atomCon = ChemModelManipulator.getAllInOneContainer(chemModel);
		for (int i = 0; i < sharedAtoms.getAtomCount(); i++)
		{
			currentAtom = sharedAtoms.getAtom(i);
			conAtoms.addAtom(currentAtom);
			conAtomsArray = atomCon.getConnectedAtoms(currentAtom);
			for (int j = 0; j < conAtomsArray.length; j++)
			{
				conAtoms.addAtom(conAtomsArray[j]);
			}
		}
		return GeometryTools.get2DCenter(conAtoms,r2dm.getRenderingCoordinates());
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
		IBond currentBond;
		IAtomContainer selectedPart = chemModel.getBuilder().newAtomContainer();
		IAtomContainer atomCon = ChemModelManipulator.getAllInOneContainer(chemModel);
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			currentAtom = atomCon.getAtom(i);
			logger.debug("Atom: ", currentAtom);
			if (polygon.contains(new Point((int) ((Point2d)r2dm.getRenderingCoordinate(currentAtom)).x, (int) ((Point2d)r2dm.getRenderingCoordinate(currentAtom)).y)))
			{
				selectedPart.addAtom(currentAtom);
			}
		}
		IBond[] bonds = atomCon.getBonds();
		for (int i = 0; i < bonds.length; i++)
		{
			currentBond = bonds[i];
			for (int j = 0; j < selectedPart.getAtomCount(); j++)
			{
				currentAtom = selectedPart.getAtom(j);
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

	public void addCDKChangeListener(ICDKChangeListener listener)
	{
		listeners.add(listener);
	}


	/**
	 *  Removes a change listener from the list of listeners
	 *
	 *@param  listener  The listener removed from the list
	 */
	public void removeCDKChangeListener(ICDKChangeListener listener)
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
			((ICDKChangeListener) listeners.get(i)).stateChanged(event);
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
		} else if (objectInRange instanceof IBond)
		{
			r2dm.setHighlightedBond((IBond) objectInRange);
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
			endX = (int) ((Point2d)r2dm.getRenderingCoordinate(atomInRange)).x;
			endY = (int) ((Point2d)r2dm.getRenderingCoordinate(atomInRange)).y;
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
				atom.setNotification(false);
				atom.setX2d(atom.getPoint2d().x + deltaX);
				atom.setY2d(atom.getPoint2d().y + deltaY);
				atom.setNotification(true);
				((Point2d)r2dm.getRenderingCoordinate(atom)).x+=deltaX;
				((Point2d)r2dm.getRenderingCoordinate(atom)).y+=deltaY;
			}
			r2dm.getSelectedPart().notifyChanged();
		}
	}
	 
	 /**
	 * This updates the coordinates in the point2ds of the atoms with the renderingcoordinates in r2dm. This should be called
	 * after a change of the structure (e. g. adding atoms), since we cannot and do not need to preserve original coordinates after this.
	 */
	private void updateMoleculeCoordinates(){
		    IAtomContainer atomCon = ChemModelManipulator.getAllInOneContainer(chemModel);
			for (int i = 0; i < atomCon.getAtomCount(); i++)
			{
				IAtom currentAtom = atomCon.getAtom(i);
				if(r2dm.getRenderingCoordinate(currentAtom)!=null){
					currentAtom.setPoint2d(new Point2d((Point2d)r2dm.getRenderingCoordinate(currentAtom)));
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
			IAtomContainer selected = chemModel.getBuilder().newAtomContainer();
			IAtom atomInRange = getAtomInRange(mouseX, mouseY);
			if (atomInRange != null)
			{
				selected.addAtom(atomInRange);
				r2dm.setSelectedPart(selected);
			} else
			{
				IBond bondInRange = getBondInRange(mouseX, mouseY);
				// because only atoms are dragged, select the atoms
				// in the bond, instead of the bond itself
				if (bondInRange != null)
				{
					IAtom[] atoms = bondInRange.getAtoms();
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
	 void centerAtom(IAtom atom, IChemModel chemModel)
	{
		 double smallestx=Integer.MAX_VALUE;
		 double largestx=Integer.MIN_VALUE;
		 double smallesty=Integer.MAX_VALUE;
		 double largesty=Integer.MIN_VALUE;
		 IAtomContainer allinone=SetOfMoleculesManipulator.getAllInOneContainer(chemModel.getSetOfMolecules());
		 for(int i=0;i<allinone.getAtomCount();i++){
			 if(((Point2d)r2dm.getRenderingCoordinate(allinone.getAtom(i))).x<smallestx)
				 smallestx=((Point2d)r2dm.getRenderingCoordinate(allinone.getAtom(i))).x;
			 if(((Point2d)r2dm.getRenderingCoordinate(allinone.getAtom(i))).y<smallesty)
				 smallesty=((Point2d)r2dm.getRenderingCoordinate(allinone.getAtom(i))).y;
			 if(((Point2d)r2dm.getRenderingCoordinate(allinone.getAtom(i))).x>largestx)
				 largestx=((Point2d)r2dm.getRenderingCoordinate(allinone.getAtom(i))).x;
			 if(((Point2d)r2dm.getRenderingCoordinate(allinone.getAtom(i))).y>largesty)
				 largesty=((Point2d)r2dm.getRenderingCoordinate(allinone.getAtom(i))).y;
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
		if (((Point2d)r2dm.getRenderingCoordinate(atom)).x < 0 && shiftX > ((Point2d)r2dm.getRenderingCoordinate(atom)).x - 10)
		{
			shiftX = ((Point2d)r2dm.getRenderingCoordinate(atom)).x - 10;
		}
		if (((Point2d)r2dm.getRenderingCoordinate(atom)).x > r2dm.getBackgroundDimension().getWidth() && shiftX < ((Point2d)r2dm.getRenderingCoordinate(atom)).x - r2dm.getBackgroundDimension().getWidth() + 10)
		{
			shiftX = ((Point2d)r2dm.getRenderingCoordinate(atom)).x - r2dm.getBackgroundDimension().getWidth() + 10;
		}
		if (((Point2d)r2dm.getRenderingCoordinate(atom)).y < 0 && shiftY > ((Point2d)r2dm.getRenderingCoordinate(atom)).y - 10)
		{
			shiftY = ((Point2d)r2dm.getRenderingCoordinate(atom)).y - 10;
		}
		if (((Point2d)r2dm.getRenderingCoordinate(atom)).y > r2dm.getBackgroundDimension().getHeight() && shiftY < ((Point2d)r2dm.getRenderingCoordinate(atom)).y - r2dm.getBackgroundDimension().getHeight() + 10)
		{
			shiftY = ((Point2d)r2dm.getRenderingCoordinate(atom)).y - r2dm.getBackgroundDimension().getHeight() + 10;
		}
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
					IMapping mapping = mappedFromAtom.getBuilder().newMapping(mappedFromAtom, mappedToAtom);
					logger.debug("Created mapping: ", mapping);
					logger.debug("  between ", mappedFromAtom);
					logger.debug("  and ", mappedToAtom);
					// ok, now figure out if they are in one reaction
					IReaction reaction1 = ChemModelManipulator.getRelevantReaction(chemModel, mappedFromAtom);
					IReaction reaction2 = ChemModelManipulator.getRelevantReaction(chemModel, mappedToAtom);
					if (reaction1 != null && reaction2 != null && reaction1 == reaction2)
					{
						logger.debug("Adding mapping to reaction: ", reaction1.getID());
						((IReaction)reaction1).addMapping(mapping);
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
	
	abstract IReaction getRelevantReaction(IChemModel model, IAtom atom);


	public IChemModel getChemModel() {
		return chemModel;
	}

	public void setChemModel(IChemModel chemModel) {
		this.chemModel = chemModel;
	}

	public void setUndoRedoHandler(IUndoRedoHandler undoRedoHandler) {
		this.undoRedoHandler = undoRedoHandler;
	}
}

