/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.applications.undoredo.UndoAdapter;

public class Controller2DModel implements java.io.Serializable, Cloneable
{
	
    public final static int DRAWBOND = 0;
    public final static int MOVE = 1;
    public final static int SELECT = 2;
    public final static int ERASER = 3;
    public final static int ELEMENT = 4;
    public final static int SYMBOL = 5;
	public final static int RING = 6;
    public final static int CLEANUP=7;
    public final static int FLIP_H=8;
    public final static int FLIP_V=9;
    public final static int ROTATION=10;
    public final static int UP_BOND=11;
    public final static int DOWN_BOND=12;
	public final static int NORMALIZE=13;
	public final static int LASSO=14;
	public final static int INCCHARGE=15;
	public final static int DECCHARGE=16;
	public final static int BENZENERING=17;
	public final static int MAPATOMATOM=18;
	public final static int ENTERELEMENT = 19;
	
	private int drawMode = 0;
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
    
    
    public Controller2DModel() {
        undoManager = new UndoManager();
        undoManager.setLimit(100);
        undoSupport = new UndoableEditSupport();
        undoSupport.addUndoableEditListener(new UndoAdapter(undoManager));
    }
 	/**
	 * Returns the draw mode.
	 *
	 * @return   The draw mode
	 */
	public int getDrawMode()
	{
		return this.drawMode;
	}

	/**
	 * Returns the String representation of the draw mode.
	 *
	 * @return   A String
	 */
	public String getDrawModeString() {
        switch (this.drawMode) {
            case DRAWBOND:
                return "Draw";
            case MOVE:
                return "Move";
            case SELECT:
                return "Select";
            case ERASER:
                return "Delete";
            case ELEMENT:
                return "Element";
            case SYMBOL:
                return "Symbol";
            case RING:
                return "Ring";
            case CLEANUP:
                return "Clean";
            case FLIP_H:
            case FLIP_V:
            case ROTATION:
                break;
            case UP_BOND:
                return "Wedge Up";
            case DOWN_BOND:
                return "Wedge Down";
            case NORMALIZE:
                return "Normalize";
            case LASSO:
                return "Select";
            case INCCHARGE:
                return "Increase Charge";
            case DECCHARGE:
                return "Decrease Charge";
            case MAPATOMATOM:
                return "Map Atom-Atom";
        }
		return "";
	}

	/**
	 * Sets the draw mode 
	 *
	 * @param   drawMode  
	 */
	public void setDrawMode(int drawMode)
	{
		this.drawMode = drawMode;
	}


	/**
	 * Returns the snapToGridAngle mode
         *
	 * @return the snapToGridAngle mode
	 */
	public boolean getSnapToGridAngle()
	{
		return this.snapToGridAngle;
	}

    /**
     * Returns true if the number of implicit hydrogens is updated
     * when an Atom is edited.
     */
    public boolean getAutoUpdateImplicitHydrogens() {
        return this.autoUpdateImplicitHydrogens;
    }
    
    /**
     * Sets wether the number of implicit hydrogens is update when an
     * Atom is edited.
     */
    public void setAutoUpdateImplicitHydrogens(boolean update) {
        this.autoUpdateImplicitHydrogens = update;
    }


	/**
	 * Sets the snapToGridAngle mode
	 *
	 * @param   snapToGridAngle
	 */
	public void setSnapToGridAngle(boolean snapToGridAngle)
	{
		this.snapToGridAngle = snapToGridAngle;
	}

	

	/**
	 * Returns the snapAngle mode
	 *
	 * @return the snapAngle mode
	 */
	public int getSnapAngle()
	{
		return this.snapAngle;
	}


	/**
	 * Sets the snapAngle mode
	 *
	 * @param   snapAngle  
	 */
	public void setSnapAngle(int snapAngle)
	{
		this.snapAngle = snapAngle;
	}

	

	/**
	 * Returns the snapToGridCartesian mode
	 *
	 * @return the snapToGridCartesian mode
	 */
	public boolean getSnapToGridCartesian()
	{
		return this.snapToGridCartesian;
	}


	/**
	 * Sets the snapToGridCartesian mode
	 *
	 * @param   snapToGridCartesian  
	 */
	public void setSnapToGridCartesian(boolean snapToGridCartesian)
	{
		this.snapToGridCartesian = snapToGridCartesian;
	}

	

	/**
	 *  Returns the snapCartesian value
	 *
	 * @return the snapCartesian value
	 */
	public int getSnapCartesian()
	{
		return this.snapCartesian;
	}


	/**
	 * Sets the snapCartesian value
	 *
	 * @param   snapCartesian  
	 */
	public void setSnapCartesian(int snapCartesian)
	{
		this.snapCartesian = snapCartesian;
	}

    
	/**
	 * Returns the ring size
	 *
	 * @return the ring size
	 */
	public int getRingSize()
	{
		return this.ringSize;
	}


	/**
	 * Sets the ring size
	 *
	 * @param   ringSize  
	 */
	public void setRingSize(int ringSize)
	{
		this.ringSize = ringSize;
	}

	public String getDefaultElementSymbol() {
		return this.defaultElementSymbol;
	}


	/**
	 * Sets the default element symbol
	 *
	 * @param   defaultElementSymbol  
	 */
	public void setDefaultElementSymbol(String defaultElementSymbol)
	{
		this.defaultElementSymbol = defaultElementSymbol;
	}

	/**
	 * Returns the bond pointer length
	 *
	 * @return the length
	 */
	public double getBondPointerLength()
	{
		return this.bondPointerLength;
	}


	/**
	 * Sets the pointer length
	 *
	 * @param   bondPointerLength  
	 */
	public void setBondPointerLength(double bondPointerLength)
	{
		this.bondPointerLength = bondPointerLength;
	}


	/**
	 * Returns the ring pointer length
	 *
	 * @return the length
	 */
	public double getRingPointerLength()
	{
		return this.ringPointerLength;
	}

	/**
	 * Sets the pointer length
	 *
	 * @param   ringPointerLength  
	 */
	public void setRingPointerLength(double ringPointerLength)
	{
		this.ringPointerLength = ringPointerLength;
	}

    public void setCommonElements(String[] elements) {
        this.commonElements = elements;
    }

    public String[] getCommonElements() {
        return this.commonElements;
    }
    
    public void setDrawElement(String element) {
        this.drawElement = element;
    }

    /**
     * Element symbol that <b>new</b> atoms get by default.
     */
    public String getDrawElement() {
        return this.drawElement;
    }
	public UndoableEditSupport getUndoSupport() {
		return undoSupport;
	}
	public void setUndoSupport(UndoableEditSupport undoSupport) {
		this.undoSupport = undoSupport;
	}
	public UndoManager getUndoManager() {
		return undoManager;
	}
	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

}
