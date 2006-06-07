/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2001-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.applications.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;

import javax.swing.JComponent;

import org.openscience.cdk.math.IFunction;
import org.openscience.cdk.renderer.GraphRendererModel;
 
/**
 * This class is a function plotter
 *
 * @cdk.module applications
 *
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.created 2001-07-02
 * @cdk.require swing
 */
public class GraphRenderer extends JComponent
{

    private static final long serialVersionUID = 5626635157860851879L;
	
  private int xpad = 70;
  private int ypad = 50;
  private int numgrid  = 10;

  private double xmax;
  private double xmin;
  private double ymax;
  private double ymin;

  private GraphRendererModel model;

  /**
   * Creates a GraphRenderer
   */
  public GraphRenderer(GraphRendererModel model)
  {
    this.model = model;

    xmax = model.getXMax();
    xmin = model.getXMin();
    ymax = model.getYMax();
    ymin = model.getYMin();
  }

  /**
   * Transform x from the function space to the viewing space
   */
  public int translateX(double x)
  { 
    Dimension size = getSize(); 
    
    double factorX = size.width/(xmax-xmin);
    //double factorY = size.height/(ymax-ymin);
    
    return (int)((x*factorX)-(xmin*factorX));
  }
  
  /**
   * Transform y from the function space to the viewing space
   */
  public int translateY(double y)
  { 
    Dimension size = getSize();
    
    //double factorX = size.width/(xmax-xmin);
    double factorY = size.height/(ymax-ymin);
    
    return (int)((size.height+(ymin*factorY))-(y*factorY));
  }

  public void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D) g;

    int i;

    int height = getSize().height;
    int width = getSize().width;

    double of,nf;
    int nx,ny,ox,oy;
    //int num = 0;

    int graphx = width-2*xpad;
    int graphy = height-2*ypad;
    float gridx = graphx/numgrid;
    float gridy = graphy/numgrid;

    int sign = -1;

    xmax = model.getXMax();
    xmin = model.getXMin();
    ymax = model.getYMax();
    ymin = model.getYMin();

    double dx = (xmax-xmin)/numgrid; 
    double dy = (ymax-ymin)/numgrid;
    String label; 
    DecimalFormat format = new DecimalFormat("0.0");

    double stepx = (xmax-xmin)/width;

    IFunction function;
    Color color;

    g.setColor(getBackground());
    g2.fillRect(0,0,width,height);

    g2.setColor(Color.black);

    FontMetrics fm = g2.getFontMetrics();

    // Draw y title
    AffineTransform at = new AffineTransform();
    at.translate(15,height/2+fm.stringWidth(model.getYTitle())/2);
    at.rotate(-Math.PI / 2.0);
    g2.setTransform(at);
    g2.drawString(model.getYTitle(),0,0);

    // Draw title
    at = new AffineTransform();
    at.translate(width/2-fm.stringWidth(model.getTitle())/2,15);
    g2.setTransform(at);
    g2.drawString(model.getTitle(),0,0);

    // Drwa x title
    at = new AffineTransform();
    at.translate(width/2-fm.stringWidth(model.getXTitle())/2,height-10);
    g2.setTransform(at);
    g2.drawString(model.getXTitle(),0,0);

    g2.setTransform(new AffineTransform());

    g2.setColor(Color.black);

    at = new AffineTransform();
    at.translate(xpad,ypad);
    at.scale(((double)(width-2*xpad))/width,((double)(height-2*ypad))/height);
    g2.setTransform(at);

    if (model.getDisplayMode()==GraphRendererModel.NORMAL)
      for (i=0; i<model.getFunctionsSize(); i++) 
      {
        function = model.getFunction(i);
        color = model.getFunctionColor(i);
        g2.setColor(color);

        ox = translateX(xmin);
        of = function.getValue(xmin, 0d, 0d);
        oy = translateY(of);
        nx = 0; ny = 0;
        for(double x=xmin+stepx; x<=xmax; x+=stepx)
        {
          nx = translateX(x);
          nf = function.getValue(x, 0d, 0d);
          ny = translateY(nf);

          if ((!Double.isNaN(of)) && (!Double.isNaN(nf)))
            g2.drawLine(ox,oy,nx,ny);
        }
        ox = nx;
        oy = ny;
      }

    if (model.getDisplayMode()==GraphRendererModel.BELOWAREA)
      for (i=0; i<model.getFunctionsSize(); i++)
      {
        function = model.getFunction(i);
        color = model.getFunctionColor(i);
        g2.setColor(color);

        for(double x=xmin+stepx; x<=xmax; x+=stepx)
        {
          nx = translateX(x);
          nf = function.getValue(x, 0d, 0d);
          ny = translateY(nf);
        
          if (!Double.isNaN(nf))
            g2.drawLine(nx,height,nx,ny);
        }   
      } 

    if (model.getDisplayMode()==GraphRendererModel.OVERAREA)
      for (i=0; i<model.getFunctionsSize(); i++)
      {
        function = model.getFunction(i);
        color = model.getFunctionColor(i);
        g2.setColor(color);
      
        for(double x=xmin+stepx; x<=xmax; x+=stepx)
        {
          nx = translateX(x);
          nf = function.getValue(x, 0d, 0d);
          ny = translateY(nf);
        
          if (!Double.isNaN(nf))
            g2.drawLine(nx,0,nx,ny);
        }   
      } 

    g2.setColor(Color.black);

    g2.setTransform(new AffineTransform());

    // Draw axis
    g2.drawLine(xpad,ypad,xpad,height-ypad);
    g2.drawLine(xpad,height-ypad,width-xpad,height-ypad);
    g2.drawLine(width-xpad,ypad,width-xpad,height-ypad);

    g2.setColor(Color.gray);
    BasicStroke dash = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_MITER, 10.0f, new float[]{3}, 0.0f);
    g2.setStroke(dash);
    // Draw the horziontal lines
    for(i=0; i<numgrid; i++)
      g2.drawLine(xpad,(int)(ypad+i*gridy),width-xpad,(int)(ypad+i*gridy));
    // Draw the vertical lines
    for(i=1; i<numgrid; i++)
      g2.drawLine((int)(xpad+i*gridx),ypad,(int)(xpad+i*gridx),height-ypad);

    g2.setStroke(new BasicStroke());
    g2.setColor(Color.black);
    // Draw the horziontal ticks
    for(i=0; i<=numgrid; i++)
    {
      g2.drawLine(xpad-3,(int)(ypad+i*gridy),xpad+3,(int)(ypad+i*gridy));
      g2.drawLine(width-xpad-3,(int)(ypad+i*gridy),width-xpad+3,(int)(ypad+i*gridy));
    }

    // Draw the vertical ticks
    sign = -1;
    for(i=0; i<=numgrid; i++)
    {
      g2.drawLine((int)(xpad+i*gridx),height-ypad-3,(int)(xpad+i*gridx),height-ypad+9+(sign*6));
      sign *= -1;
    }

    // Draw the horziontal labels
    for(i=0; i<=numgrid; i++)
    {
      label = format.format(ymin+i*dy);
      g2.drawString(label,xpad-fm.stringWidth(label)-5,(int)(height-ypad-i*gridy+4));
    } 
    
    // Draw the vertical labels
    sign = -1;
    for(i=0; i<=numgrid; i++)
    {
      label = format.format(xmin+i*dx);
      g2.drawString(label,(int)(xpad+i*gridx-fm.stringWidth(label)/2),height-ypad+20+(sign*6));
      sign *= -1;
    }
  }
}
