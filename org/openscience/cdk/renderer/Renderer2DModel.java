/* Renderer2DSettings.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.renderer;

import java.awt.*;
import org.openscience.cdk.*;
import org.openscience.cdk.event.*;
import java.util.*;


public class Renderer2DModel
{
	private double scaleFactor = 60;
	
	private double bondWidth = 2;
	
	private double bondDistance = 6;

	private Color backColor = Color.white;

	private Color foreColor = Color.black;
	
	private Color highlightColor = Color.lightGray;
	
	private double highlightRadius = 10;

	private boolean drawNumbers = false;	
	
	private int atomRadius = 8;
	
	private String defaultElementSymbol = "H";
	
	private Atom highlightedAtom = null;
	
	private Bond highlightedBond = null;
	
	private Hashtable colorHash = new Hashtable();
	
	private Vector listeners = new Vector();
	
	private Bond newBond = null;
	
	private Point pointerVectorStart = null;
	
	private Point pointerVectorEnd = null;
	
	private int pointerVectorLength = 80;
	

	/**
	 * Returns the distance between two lines in a double or triple bond
	 *
	 * @return     the distance between two lines in a double or triple bond
	 */
	public double getBondDistance()
	{
		return this.bondDistance;
	}


	/**
	 * Sets the distance between two lines in a double or triple bond
	 *
	 * @param   bondDistance  the distance between two lines in a double or triple bond
	 */
	public void setBondDistance(double bondDistance)
	{
		this.bondDistance = bondDistance;
	}

	

	/**
	 * Returns the thickness of a bond line (XXX what are the dimensions? XXX)
	 *
	 * @return     the thickness of a bond line
	 */
	public double getBondWidth()
	{
		return this.bondWidth;
	}


	/**
	 * Sets the thickness of a bond line (XXX what are the dimensions? XXX)
	 *
	 * @param   bondWidth  the thickness of a bond line
	 */
	public void setBondWidth(double bondWidth)
	{
		this.bondWidth = bondWidth;
	}

	

	/**
	 * A scale factor for the drawing
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
	 * returns the foreground color for the drawing
	 *
	 * @return the foreground color for the drawing    
	 */
	public Color getForeColor()
	{
		return this.foreColor;
	}


	/**
	 *
	 *
	 * @param   foreColor  
	 */
	public void setForeColor(Color foreColor)
	{
		this.foreColor = foreColor;
	}

	

	/**
	 *
	 *
	 * @return     
	 */
	public Color getBackColor()
	{
		return this.backColor;
	}


	/**
	 *
	 *
	 * @param   backColor  
	 */
	public void setBackColor(Color backColor)
	{
		this.backColor = backColor;
	}

	
	public boolean drawNumbers()
	{
		return this.drawNumbers;
	}

	public void setDrawNumbers(boolean drawNumbers)
	{
		this.drawNumbers = drawNumbers;
	}


	/**
	 *
	 *
	 * @return     
	 */
	public Color getHighlightColor()
	{
	return this.highlightColor;
	}


	/**
	 *
	 *
	 * @param   highlightColor  
	 */
	public void setHighlightColor(Color highlightColor)
	{
	this.highlightColor = highlightColor;
	}


	/**
	 *
	 *
	 * @return     
	 */
	public double getHighlightRadius()
	{
	return this.highlightRadius;
	}


	/**
	 *
	 *
	 * @param   highlightRadius  
	 */
	public void setHighlightRadius(double highlightRadius)
	{
	this.highlightRadius = highlightRadius;
	}

	

	/**
	 *
	 *
	 * @return     
	 */
	public int getAtomRadius()
	{
		return this.atomRadius;
	}


	/**
	 *
	 *
	 * @param   atomRadius  
	 */
	public void setAtomRadius(int atomRadius)
	{
		this.atomRadius = atomRadius;
	}

	

	/**
	 *
	 *
	 * @return     
	 */
	public Atom getHighlightedAtom()
	{
		return this.highlightedAtom;
	}


	/**
	 *
	 *
	 * @param   highlghtedAtom  
	 */
	public void setHighlightedAtom(Atom highlightedAtom)
	{
		this.highlightedAtom = highlightedAtom;
		fireChange();		
	}

	

	/**
	 *
	 *
	 * @return     
	 */
	public Bond getHighlightedBond()
	{
		return this.highlightedBond;
	}


	/**
	 *
	 *
	 * @param   highlightedBond  
	 */
	public void setHighlightedBond(Bond highlightedBond)
	{
		this.highlightedBond = highlightedBond;
		fireChange();		
	}

	

	/**
	 *
	 *
	 * @return     
	 */
	public Hashtable getColorHash()
	{
		return this.colorHash;
	}


	/**
	 *
	 *
	 * @param   colorHash  
	 */
	public void setColorHash(Hashtable colorHash)
	{
		this.colorHash = colorHash;
	}
	
	/**
	 *
	 *
	 * @return     
	 */
	public String getDefaultElementSymbol()
	{
		return this.defaultElementSymbol;
	}


	/**
	 *
	 *
	 * @param   elementSymbol  
	 */
	public void setDefaultElementSymbol(String defaultElementSymbol)
	{
		this.defaultElementSymbol = defaultElementSymbol;
	}

	

	/**
	 *
	 *
	 * @return     
	 */
	public Bond getNewBond()
	{
		return this.newBond;
	}


	/**
	 *
	 *
	 * @param   newBond  
	 */
	public void setNewBond(Bond newBond)
	{
		this.newBond = newBond;
		fireChange();
	}

	

	/**
	 *
	 *
	 * @return     
	 */
	public Point getPointerVectorEnd()
	{
		return this.pointerVectorEnd;
	}


	/**
	 *
	 *
	 * @param   pointerVectorEnd  
	 */
	public void setPointerVectorEnd(Point pointerVectorEnd)
	{
		this.pointerVectorEnd = pointerVectorEnd;
		fireChange();
	}

	

	/**
	 *
	 *
	 * @return     
	 */
	public Point getPointerVectorStart()
	{
		return this.pointerVectorStart;
	}


	/**
	 *
	 *
	 * @param   pointerVectorStart  
	 */
	public void setPointerVectorStart(Point pointerVectorStart)
	{
		this.pointerVectorStart = pointerVectorStart;
		fireChange();
	}
	
	

	/**
	 *
	 *
	 * @return     
	 */
	public int getPointerVectorLength()
	{
		return this.pointerVectorLength;
	}


	/**
	 *
	 *
	 * @param   pointerVectorLength  
	 */
	public void setPointerVectorLength(int pointerVectorLength)
	{
		this.pointerVectorLength = pointerVectorLength;
	}

	/**
	 * Adds a change listener to the list of listeners
	 *
	 * @param   listener  The listener added to the list 
	 */
	public void addCDKChangeListener(CDKChangeListener listener)
	{
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
		for (int i = 0; i < listeners.size(); i++)
		{
			((CDKChangeListener)listeners.get(i)).stateChanged(event);
		}
	}
}