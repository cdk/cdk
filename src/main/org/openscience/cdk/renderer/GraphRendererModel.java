/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2001-2007  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.renderer;

import java.awt.Color;

import org.openscience.cdk.math.IFunction;

/**
 * This class handles a set of function for the GraphRenderer
 *
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.svnrev  $Revision$
 * @cdk.created 2001-07-02
 * @cdk.module  qm
 */ 
public class GraphRendererModel
{
  private double xmin = -1d;
  private double xmax = +1d;
  private double ymin = -1d;
  private double ymax = +1d;

  private String title = "Main title"; // Main title
  private String xtitle = "X title"; // Title of the x axis
  private String ytitle = "Y title"; // Title of the y axis

  private java.util.Vector functions = new java.util.Vector();
  private java.util.Vector colors = new java.util.Vector();

  /** Paints the function normal */
  public final static int NORMAL = 0;
  /** Paints the area below the function */
  public final static int BELOWAREA = 1;
  /** Paints the area over the function */
  public final static int OVERAREA = 2;

  private int displaymode = NORMAL;

  /**
   * Sets the function area, which will painted
   */
  public void setX(double xmin, double xmax)
  {
    if (xmin<xmax)
    {
      this.xmin = xmin;
      this.xmax = xmax;
    }
  }

  /**
   * Sets the function area, which will painted
   */
  public void setY(double ymin, double ymax)
  {
    if (ymin<ymax)
    {
      this.ymin = ymin;
      this.ymax = ymax;
    }
  }

  /**
   * Gets the function area, which will painted
   */
  public double getXMin()
  {
    return xmin;
  }

  /**
   * Sets the function area, which will painted
   */
  public double getXMax()
  {
    return xmax;
  }

  /**
   * Sets the function area, which will painted
   */
  public double getYMin()
  {
    return ymin;
  }

  /**
   * Sets the function area, which will painted
   */
  public double getYMax()
  {
    return ymax;
  }

  /**
   * Set the main title
   */
  public void setTitle(String string)
  {
    if (string!=null)
      title = string;
  }

  /**
   * Get the main title
   */
  public String getTitle()
  {
    return title;
  }

  /**
   * Set the title of the x axis
   */
  public void setXTitle(String string)
  {
    if (string!=null)
      xtitle = string;
  }

  /**
   * Get the title of the x axis
   */
  public String getXTitle()
  {
    return xtitle;
  }

  /**
   * Set the title of the y axis
   */
  public void setYTitle(String string)
  {
    if (string!=null)
      ytitle = string;
  }

  /**
   * Get the title of the y axis
   */
  public String getYTitle()
  {
    return ytitle;
  }

  /**
   * Set the display mode
   */
  public void setDisplayMode(int mode)
  {
    if ((NORMAL<=mode) && (mode<=OVERAREA))
      displaymode = mode;
  }

  /**
   * Get the display mode
   */
  public int getDisplayMode()
  {
    return displaymode;
  }

  /**
   * Add a function to the set of functions
   */
  public void addFunction(IFunction function)
  {
    if ((function!=null) && (!functions.contains(function)))
    {
      functions.add(function);
      colors.add(Color.black);
    }
  }
  
  /**
   * Add a function to the set of functions
   */
  public void addFunction(IFunction function, Color color)
  {
    if ((function!=null) && (!functions.contains(function)))
    {
      functions.add(function);
      colors.add(color);
    }
    else
      colors.setElementAt(color, functions.indexOf(function));
  }

  /**
   * Get the count of functions in this set
   */
  public int getFunctionsSize()
  {
    return functions.size();
  }

  /**
   * Get a function from this set
   */
  public IFunction getFunction(int index)
  {
    return (IFunction) functions.elementAt(index);
  }

  /**
   * Get a color from a function in this set
   */
  public Color getFunctionColor(int index)
  {
    return (Color) colors.elementAt(index);
  }
}
