/* JCPController2DModel.java
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
package org.openscience.cdk.controller;




public class JCPController2DModel 
{
	
    public static int DRAWBOND = 0;
    public static int MOVE = 1;
    public static int SELECT = 2;
    public static int ERASER = 3;
    public static int ELEMENT = 4;
    public static int SYMBOL = 5;
	public static int RING = 6;
    public static int CLEANUP=7;
    public static int FLIP_H=8;
    public static int FLIP_V=9;
    public static int ROTATION=10;
    public static int UP_BOND=11;
    public static int DOWN_BOND=12;
	public static int NORMALIZE=13;
	public static int LASSO=14;
	
	private int drawMode = 14;
	private int ringSize = 6;
	
	private boolean snapToGridAngle = true;
	private int snapAngle = 15;
	private boolean snapToGridCartesian = true;
	private int snapCartesian = 10;	
	
	private int defaultBondLength = 70;
	
	private String defaultElementSymbol = "C";
	private int bondPointerLength = 80;
	private int ringPointerLength = (int)(getDefaultBondLength() / 2);
	
	
	/**
	 * Returns the draw mode
	 *
	 * @return   The draw mode
	 */
	public int getDrawMode()
	{
		return this.drawMode;
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
	 *
	 *
	 * @return     
	 */
	public boolean getSnapToGridAngle()
	{
		return this.snapToGridAngle;
	}


	/**
	 *
	 *
	 * @param   snapToGridAngle  
	 */
	public void setSnapToGridAngle(boolean snapToGridAngle)
	{
		this.snapToGridAngle = snapToGridAngle;
	}

	

	/**
	 *
	 *
	 * @return     
	 */
	public int getSnapAngle()
	{
		return this.snapAngle;
	}


	/**
	 *
	 *
	 * @param   snapAngle  
	 */
	public void setSnapAngle(int snapAngle)
	{
		this.snapAngle = snapAngle;
	}

	

	/**
	 *
	 *
	 * @return     
	 */
	public boolean getSnapToGridCartesian()
	{
		return this.snapToGridCartesian;
	}


	/**
	 *
	 *
	 * @param   snapToGridCartesian  
	 */
	public void setSnapToGridCartesian(boolean snapToGridCartesian)
	{
		this.snapToGridCartesian = snapToGridCartesian;
	}

	

	/**
	 *
	 *
	 * @return     
	 */
	public int getSnapCartesian()
	{
		return this.snapCartesian;
	}


	/**
	 *
	 *
	 * @param   snapCartesian  
	 */
	public void setSnapCartesian(int snapCartesian)
	{
		this.snapCartesian = snapCartesian;
	}

	

	/**
	 *
	 *
	 * @return     
	 */
	public int getRingSize()
	{
		return this.ringSize;
	}


	/**
	 *
	 *
	 * @param   ringSize  
	 */
	public void setRingSize(int ringSize)
	{
		this.ringSize = ringSize;
	}

	

	/**
	 *
	 *
	 * @return     
	 */
	public int getDefaultBondLength()
	{
		return this.defaultBondLength;
	}


	/**
	 *
	 *
	 * @param   defaultBondLength  
	 */
	public void setDefaultBondLength(int defaultBondLength)
	{
		this.defaultBondLength = defaultBondLength;
	}
	

	/**
	 * Allows for adding a CDKChangeListener to this model
	 *
	 * @param   listener  The listener to be added to this model
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
	public int getBondPointerLength()
	{
		return this.bondPointerLength;
	}


	/**
	 *
	 *
	 * @param   pointerVectorLength  
	 */
	public void setBondPointerLength(int bondPointerLength)
	{
		this.bondPointerLength = bondPointerLength;
	}


	/**
	 *
	 *
	 * @return     
	 */
	public int getRingPointerLength()
	{
		return this.ringPointerLength;
	}


	/**
	 *
	 *
	 * @param   pointerVectorLength  
	 */
	public void setRingPointerLength(int ringPointerLength)
	{
		this.ringPointerLength = ringPointerLength;
	}


}
