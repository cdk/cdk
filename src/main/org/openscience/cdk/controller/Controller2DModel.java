/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.controller;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;


/**
 * @cdk.module  control
 * @cdk.svnrev  $Revision$
 */
public class Controller2DModel implements java.io.Serializable, Cloneable, IController2DModel
{
	
    private static final long serialVersionUID = 9007159812273128989L;
    
	private DrawMode drawMode = DrawMode.DRAWBOND;
	private int ringSize = 6;
	
	private boolean snapToGridAngle = true;
	private int snapAngle = 15;
	
	private boolean snapToGridCartesian = true;
	private int snapCartesian = 10;	
	
	private String defaultElementSymbol = "C";
	private String drawElement = "C";
    private String[] commonElements = { "C", "O", "N", "H", "P", "S" };

	private double bondPointerLength = 20;
	private double ringPointerLength = 20;

    private boolean autoUpdateImplicitHydrogens = false;
	private UndoManager undoManager;
	private UndoableEditSupport undoSupport;
	//for controlling, if the structure or substructural parts might be moved
	private boolean isMovingAllowed = true;
    
    public Controller2DModel() {
        undoManager = new UndoManager();
        undoManager.setLimit(100);
        undoSupport = new UndoableEditSupport();
        undoSupport.addUndoableEditListener(new UndoAdapter(undoManager));
    }
 	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getDrawMode()
     */
	public DrawMode getDrawMode()
	{
		return this.drawMode;
	}

	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getDrawModeString()
     */
	public String getDrawModeString() {
		return this.drawMode.getName();
	}

	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setDrawMode(org.openscience.cdk.controller.Controller2DModel.DrawMode)
     */
	public void setDrawMode(DrawMode drawMode)
	{
		this.drawMode = drawMode;
	}


	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getSnapToGridAngle()
     */
	public boolean getSnapToGridAngle()
	{
		return this.snapToGridAngle;
	}

    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getAutoUpdateImplicitHydrogens()
     */
    public boolean getAutoUpdateImplicitHydrogens() {
        return this.autoUpdateImplicitHydrogens;
    }
    
    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setAutoUpdateImplicitHydrogens(boolean)
     */
    public void setAutoUpdateImplicitHydrogens(boolean update) {
        this.autoUpdateImplicitHydrogens = update;
    }


	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setSnapToGridAngle(boolean)
     */
	public void setSnapToGridAngle(boolean snapToGridAngle)
	{
		this.snapToGridAngle = snapToGridAngle;
	}

	

	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getSnapAngle()
     */
	public int getSnapAngle()
	{
		return this.snapAngle;
	}


	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setSnapAngle(int)
     */
	public void setSnapAngle(int snapAngle)
	{
		this.snapAngle = snapAngle;
	}

	

	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getSnapToGridCartesian()
     */
	public boolean getSnapToGridCartesian()
	{
		return this.snapToGridCartesian;
	}


	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setSnapToGridCartesian(boolean)
     */
	public void setSnapToGridCartesian(boolean snapToGridCartesian)
	{
		this.snapToGridCartesian = snapToGridCartesian;
	}

	

	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getSnapCartesian()
     */
	public int getSnapCartesian()
	{
		return this.snapCartesian;
	}


	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setSnapCartesian(int)
     */
	public void setSnapCartesian(int snapCartesian)
	{
		this.snapCartesian = snapCartesian;
	}

    
	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getRingSize()
     */
	public int getRingSize()
	{
		return this.ringSize;
	}


	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setRingSize(int)
     */
	public void setRingSize(int ringSize)
	{
		this.ringSize = ringSize;
	}

	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getDefaultElementSymbol()
     */
	public String getDefaultElementSymbol() {
		return this.defaultElementSymbol;
	}


	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setDefaultElementSymbol(java.lang.String)
     */
	public void setDefaultElementSymbol(String defaultElementSymbol)
	{
		this.defaultElementSymbol = defaultElementSymbol;
	}

	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getBondPointerLength()
     */
	public double getBondPointerLength()
	{
		return this.bondPointerLength;
	}


	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setBondPointerLength(double)
     */
	public void setBondPointerLength(double bondPointerLength)
	{
		this.bondPointerLength = bondPointerLength;
	}


	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getRingPointerLength()
     */
	public double getRingPointerLength()
	{
		return this.ringPointerLength;
	}

	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setRingPointerLength(double)
     */
	public void setRingPointerLength(double ringPointerLength)
	{
		this.ringPointerLength = ringPointerLength;
	}

    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setCommonElements(java.lang.String[])
     */
    public void setCommonElements(String[] elements) {
        this.commonElements = elements;
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getCommonElements()
     */
    public String[] getCommonElements() {
        return this.commonElements;
    }
    
    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setDrawElement(java.lang.String)
     */
    public void setDrawElement(String element) {
        this.drawElement = element;
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getDrawElement()
     */
    public String getDrawElement() {
        return this.drawElement;
    }
	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getUndoSupport()
     */
	public UndoableEditSupport getUndoSupport() {
		return undoSupport;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setUndoSupport(javax.swing.undo.UndoableEditSupport)
     */
	public void setUndoSupport(UndoableEditSupport undoSupport) {
		this.undoSupport = undoSupport;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#getUndoManager()
     */
	public UndoManager getUndoManager() {
		return undoManager;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setUndoManager(javax.swing.undo.UndoManager)
     */
	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#isMovingAllowed()
     */
	public boolean isMovingAllowed() {
		return isMovingAllowed;
	}

	/* (non-Javadoc)
     * @see org.openscience.cdk.controller.IController2DModel#setMovingAllowed(boolean)
     */
	public void setMovingAllowed(boolean isMovingAllowed) {
		this.isMovingAllowed = isMovingAllowed;
	}
}
