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

import java.awt.Color;


public class Renderer2DModel
{
	private double scaleFactor = 60;
	
	private double bondWidth = 2;
	
	private double bondDistance = 6;

	private Color backColor = Color.white;

	private Color foreColor = Color.black;
	

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
}