/* GraphRendererModel.java
 * 
 * Autor: Stephan Michels 
 * EMail: stephan@vern.chem.tu-berlin.de
 * Datum: 2.7.2001
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.math.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import javax.swing.*;
import java.text.DecimalFormat;
 
public class GraphRendererModel
{
  private double xmin = -1d;
  private double xmax = +1d;
  private double ymin = -1d;
  private double ymax = +1d;

  private String title = "Main title";
  private String xtitle = "X title";
  private String ytitle = "Y title";

  private java.util.Vector functions = new java.util.Vector();
  private java.util.Vector colors = new java.util.Vector();

  public final static int NORMAL = 0;
  public final static int BELOWAREA = 1;
  public final static int OVERAREA = 2;
  private int displaymode = NORMAL;

  public void setX(double xmin, double xmax)
  {
    if (xmin<xmax)
    {
      this.xmin = xmin;
      this.xmax = xmax;
    }
  }

  public void setY(double ymin, double ymax)
  {
    if (ymin<ymax)
    {
      this.ymin = ymin;
      this.ymax = ymax;
    }
  }

  public double getXMin()
  {
    return xmin;
  }

  public double getXMax()
  {
    return xmax;
  }

  public double getYMin()
  {
    return ymin;
  }

  public double getYMax()
  {
    return ymax;
  }

  public void setTitle(String string)
  {
    if (string!=null)
      title = string;
  }

  public String getTitle()
  {
    return title;
  }

  public void setXTitle(String string)
  {
    if (string!=null)
      xtitle = string;
  }

  public String getXTitle()
  {
    return xtitle;
  }

  public void setYTitle(String string)
  {
    if (string!=null)
      ytitle = string;
  }

  public String getYTitle()
  {
    return ytitle;
  }

  public void setDisplayMode(int mode)
  {
    if ((NORMAL<=mode) && (mode<=OVERAREA))
      displaymode = mode;
  }

  public int getDisplayMode()
  {
    return displaymode;
  }

  public void addFunction(Function function)
  {
    if ((function!=null) && (!functions.contains(function)))
    {
      functions.add(function);
      colors.add(Color.black);
    }
  }
  
  public void addFunction(Function function, Color color)
  {
    if ((function!=null) && (!functions.contains(function)))
    {
      functions.add(function);
      colors.add(color);
    }
    else
      colors.setElementAt(color, functions.indexOf(function));
  }

  public int getFunctionsSize()
  {
    return functions.size();
  }

  public Function getFunction(int index)
  {
    return (Function) functions.elementAt(index);
  }

  public Color getFunctionColor(int index)
  {
    return (Color) colors.elementAt(index);
  }
}
