/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 *  */
package org.openscience.cdk.renderer;

import java.awt.*;
import java.awt.Polygon;
import org.openscience.cdk.*;
import org.openscience.cdk.event.*;
import java.util.*;


/**
 * Model for Renderer2D that contains settings for drawing objects.
 */
public class Renderer2DModel implements java.io.Serializable, Cloneable 
{
    
    private double scaleFactor = 60.0;
    
    /** Determines how much the image is zoomed into on. */
    private double zoomFactor = 1.0;
	
	private double bondWidth = 2.0;

	private double bondDistance = 6.0;

	private double bondLength = 30.0;

	private Color backColor = Color.white;

	private Color foreColor = Color.black;
	
	private Color highlightColor = Color.lightGray;
	
	private double highlightRadius = 10.0;

	private boolean drawNumbers = false;	
	
	private int atomRadius = 8;
	
	private Atom highlightedAtom = null;
	
	private Bond highlightedBond = null;
	
	private Hashtable colorHash = new Hashtable();
	
	private transient Vector listeners = new Vector();
	
	private Point pointerVectorStart = null;
	
	private Point pointerVectorEnd = null;
	
	private Polygon selectRect = null;
	
	private AtomContainer selectedPart = null;
	
	private Vector lassoPoints = new Vector();
    
    /** Determines wether structures should be drawn as Kekule structures,
     *  thus giving each carbon element explicitely, instead of not displaying
     *  the element symbol. Example C-C-C instead of /\.
     */
    private boolean kekuleStructure = false;

    /** Determines wether methyl carbons' symbols should be drawn explicit
     *  for methyl carbons. Example C/\C instead of /\.
     */
    private boolean showEndCarbons = true;

    private Dimension backgroupDimension = new Dimension(500,400);
    
    public Dimension getBackgroundDimension() {
        return new Dimension((int)((double)backgroupDimension.getWidth() * zoomFactor),
                             (int)((double)backgroupDimension.getHeight() * zoomFactor));
    }
    
    public void setBackgroundDimension(Dimension dim) {
        this.backgroupDimension = dim;
    }
    
	/**
	 * Returns the distance between two lines in a double or triple bond
	 *
	 * @return     the distance between two lines in a double or triple bond
	 */
	public double getBondDistance() {
		return this.bondDistance;
	}


	/**
	 * Sets the distance between two lines in a double or triple bond
	 *
	 * @param   bondDistance  the distance between two lines in a double or triple bond
	 */
	public void setBondDistance(double bondDistance) {
		this.bondDistance = bondDistance;
	}

	

	/**
	 * Returns the thickness of a bond line.
	 *
	 * @return     the thickness of a bond line
	 */
	public double getBondWidth()
	{
		return this.bondWidth;
	}


	/**
	 * Sets the thickness of a bond line.
	 *
	 * @param   bondWidth  the thickness of a bond line
	 */
	public void setBondWidth(double bondWidth)
	{
		this.bondWidth = bondWidth;
	}


	/**
	 * Returns the length of a bond line.
	 *
	 * @return     the length of a bond line
	 */
	public double getBondLength()
	{
		return this.bondLength;
	}


	/**
	 * Sets the length of a bond line.
	 *
	 * @param   bondLength  the length of a bond line
	 */
	public void setBondLength(double bondLength)
	{
		this.bondLength = bondLength;
	}
	

	/**
	 * A scale factor for the drawing.
	 *
	 * @return a scale factor for the drawing
	 */
	public double getScaleFactor()
	{
		return this.scaleFactor;
	}


	/**
	 * Returns the scale factor for the drawing
	 *
	 * @param   scaleFactor  the scale factor for the drawing
	 */
	public void setScaleFactor(double scaleFactor)
	{
		this.scaleFactor = scaleFactor;
	}

	/**
	 * A zoom factor for the drawing.
	 *
	 * @return a zoom factor for the drawing
	 */
	public double getZoomFactor() {
		return this.zoomFactor;
	}


	/**
	 * Returns the zoom factor for the drawing
	 *
	 * @param   scaleZoom  the zoom factor for the drawing
	 */
	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
	}	

	/**
	 * returns the foreground color for the drawing
	 *
	 * @return the foreground color for the drawing    
	 */
	public Color getForeColor()
	{
		return this.foreColor;
	}


	/**
	 * Sets the foreground color with which bonds and atoms are drawn
	 *
	 * @param   foreColor  the foreground color with which bonds and atoms are drawn
	 */
	public void setForeColor(Color foreColor)
	{
		this.foreColor = foreColor;
	}

	

	/**
	 * Returns the background color 
	 *
	 * @return the background color     
	 */
	public Color getBackColor()
	{
		return this.backColor;
	}


	/**
	 * Sets the background color 
	 *
	 * @param   backColor the background color  
	 */
	public void setBackColor(Color backColor)
	{
		this.backColor = backColor;
	}

	
	/**
	 * Returns if the drawing of atom numbers is switched on for this model
	 *
	 * @return  true if the drawing of atom numbers is switched on for this model   
	 */
	public boolean drawNumbers()
	{
		return this.drawNumbers;
	}

	public boolean getKekuleStructure() {
		return this.kekuleStructure;
	}

	public void setKekuleStructure(boolean kekule) {
		this.kekuleStructure = kekule;
	}

    public boolean getShowEndCarbons() {
        return this.showEndCarbons;
    }
    
    public void setShowEndCarbons(boolean showThem) {
        this.showEndCarbons = showThem;
    }
    
	/**
	 * Sets if the drawing of atom numbers is switched on for this model
	 *
	 * @param   drawNumbers  true if the drawing of atom numbers is to be switched on for this model
	 */
	public void setDrawNumbers(boolean drawNumbers)
	{
		this.drawNumbers = drawNumbers;
	}


	/**
	 * Returns the color used for highlighting things in this model 
	 *
	 * @return     the color used for highlighting things in this model 
	 */
	public Color getHighlightColor()
	{
		return this.highlightColor;
	}


	/**
	 * Sets the color used for highlighting things in this model 
	 *
	 * @param   highlightColor  the color to be used for highlighting things in this model 
	 */
	public void setHighlightColor(Color highlightColor)
	{
		this.highlightColor = highlightColor;
	}


	/**
	 * Returns the radius around an atoms, for which the atom is 
	 * marked highlighted if a pointer device is placed within this radius
	 * 
	 * @return The highlight radius for all atoms   
	 */
	public double getHighlightRadius()
	{
		return this.highlightRadius;
	}


	/**
	 * Sets the radius around an atoms, for which the atom is 
	 * marked highlighted if a pointer device is placed within this radius
	 *
	 * @param   highlightRadius  the highlight radius of all atoms
	 */
	public void setHighlightRadius(double highlightRadius)
	{
		this.highlightRadius = highlightRadius;
	}

	

	/**
	 * XXX No idea what this is about
	 *
	 * @return an unknown int    
	 */
	public int getAtomRadius()
	{
		return this.atomRadius;
	}


	/**
	 * XXX No idea what this is about
	 *
	 * @param   atomRadius   XXX No idea what this is about
	 */
	public void setAtomRadius(int atomRadius)
	{
		this.atomRadius = atomRadius;
	}

	

	/**
	 * Returns the atom currently highlighted
	 *
	 * @return the atom currently highlighted    
	 */
	public Atom getHighlightedAtom()
	{
		return this.highlightedAtom;
	}


	/**
	 * Sets the atom currently highlighted
	 *
	 * @param   highlightedAtom The atom to be highlighted  
	 */
	public void setHighlightedAtom(Atom highlightedAtom)
	{
		if ((this.highlightedAtom == null) &&
            (highlightedAtom == null)) {
            // do not do anything, nothing has changed
        } else {
            this.highlightedAtom = highlightedAtom;
            fireChange();
        }
	}

	

	/**
	 * Returns the Bond currently highlighted
	 *
	 * @return the Bond currently highlighted    
	 */
	public Bond getHighlightedBond()
	{
		return this.highlightedBond;
	}


	/**
	 * Sets the Bond currently highlighted
	 *
	 * @param   highlightedBond  The Bond to be currently highlighted
	 */
	public void setHighlightedBond(Bond highlightedBond)
	{
		if ((this.highlightedBond == null) &&
            (highlightedBond == null)) {
            // do not do anything, nothing has changed
        } else {
            this.highlightedBond = highlightedBond;
            fireChange();
        }
	}

	

	/**
	 * Returns the hashtable used for coloring substructures 
	 *
	 * @return the hashtable used for coloring substructures     
	 */
	public Hashtable getColorHash()
	{
		return this.colorHash;
	}


	/**
	 * Sets the hashtable used for coloring substructures 
	 *
	 * @param   colorHash  the hashtable used for coloring substructures 
	 */
	public void setColorHash(Hashtable colorHash)
	{
		this.colorHash = colorHash;
	}
	

	/**
	 * Returns the end of the pointer vector
	 *
	 * @return the end point
	 */
	public Point getPointerVectorEnd()
	{
		return this.pointerVectorEnd;
	}


	/**
	 * Sets the end of a pointer vector
	 *
	 * @param   pointerVectorEnd  
	 */
	public void setPointerVectorEnd(Point pointerVectorEnd)
	{
		this.pointerVectorEnd = pointerVectorEnd;
		fireChange();
	}

	

	/**
	 * Returns the start of a pointer vector
	 *
	 * @return the start point
	 */
	public Point getPointerVectorStart()
	{
		return this.pointerVectorStart;
	}


	/**
	 * Sets the start point of a pointer vector
	 *
	 * @param   pointerVectorStart  
	 */
	public void setPointerVectorStart(Point pointerVectorStart)
	{
		this.pointerVectorStart = pointerVectorStart;
		fireChange();
	}
	
	

	/**
	 * Returns selected rectangular
	 *
	 * @return the selection
	 */
	public Polygon getSelectRect()
	{
		return this.selectRect;
	}


	/**
	 * Sets a selected region
	 *
	 * @param   selectRect  
	 */
	public void setSelectRect(Polygon selectRect)
	{
		this.selectRect = selectRect;
		fireChange();		
	}

	

	/**
	 * Get selected atoms
	 *
	 * @return an atomcontainer with the selected atoms
	 */
	public AtomContainer getSelectedPart()
	{
		return this.selectedPart;
	}


	/**
	 * Sets the selected atoms
	 *
	 * @param   selectedPart  
	 */
	public void setSelectedPart(AtomContainer selectedPart)
	{
		this.selectedPart = selectedPart;
		getColorHash().clear();
		for (int i = 0; i < selectedPart.getAtomCount(); i++)
		{
			getColorHash().put(selectedPart.getAtomAt(i), getHighlightColor());
		}
        Bond[] bonds = selectedPart.getBonds();
		for (int i = 0; i < bonds.length; i++) {
			getColorHash().put(bonds[i], getHighlightColor());
		}		
	}


	/**
	 * Returns a set of points constituating a selected region
	 *
	 * @return a vector with points
	 */
	public Vector getLassoPoints()
	{
		return this.lassoPoints;
	}


	/**
	 * Adds a point to the list of lasso points
	 *
	 * @param   lassoPoints  
	 */
	public void addLassoPoint(Point point)
	{
		this.lassoPoints.addElement(point);
		fireChange();
	}


	/**
	 * Adds a change listener to the list of listeners
	 *
	 * @param   listener  The listener added to the list 
	 */

	public void addCDKChangeListener(CDKChangeListener listener)
	{
		if (listeners == null)
		{
			listeners = new Vector();	
		}
		listeners.add(listener);
	}
	

	/**
	 * Removes a change listener from the list of listeners
	 *
	 * @param   listener  The listener removed from the list 
	 */
	public void removeCDKChangeListener(CDKChangeListener listener)
	{
		listeners.remove(listener);
	}


	/**
	 * Notifies registered listeners of certain changes
	 * that have occurred in this model.
	 */
	public void fireChange()
	{
		EventObject event = new EventObject(this);
		if (listeners == null)
		{
			listeners = new Vector();	
		}
		
		for (int i = 0; i < listeners.size(); i++)
		{
			((CDKChangeListener)listeners.get(i)).stateChanged(event);
		}
	}

	
}
