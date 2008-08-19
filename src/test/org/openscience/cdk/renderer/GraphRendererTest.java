/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
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
 *  */

package org.openscience.cdk.renderer;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.openscience.cdk.applications.swing.GraphRenderer;
import org.openscience.cdk.math.IFunction;
import org.openscience.cdk.math.Matrix;
import org.openscience.cdk.math.Vector;

/**
 * @cdk.module test-extra
 *
 * @author  Stephan Michels <stephan@vern.chem.tu-berlin.de>
 * @cdk.created 2001-07-23
 */
public class GraphRendererTest
{
  public GraphRendererTest()
  {
    JFrame frame = new JFrame("GraphRendererTest");
    frame.getContentPane().setLayout(new BorderLayout());

    GraphRendererModel model = new GraphRendererModel();
    model.setX(-1d, +1d);
    model.setY(0d, +1d);
    model.addFunction(new GaussFunction(), new Color(205,230,205));
    int i = GraphRendererModel.BELOWAREA;
	model.setDisplayMode(i);
    model.setTitle("Gauss function f(x)=e^(-10*x^2)");
    model.setXTitle("X Axis");
    model.setYTitle("Y Axis f(x)");

    GraphRenderer graph = new GraphRenderer(model);
    graph.setBackground(Color.white);

    frame.getContentPane().add(graph, BorderLayout.CENTER);

    //frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setSize(500,500);
    frame.setVisible(true);
  }

  private class GaussFunction implements IFunction
  {
    public double getValue(double x, double y, double z)
    {
      return Math.exp(-10*x*x);
    }

    public Vector getValues(Matrix m)
    {
      Vector result = new Vector(m.columns);
      for(int i=0; i<m.columns; i++)
        result.vector[i] = Math.exp(-10*m.matrix[0][i]*m.matrix[0][i]);
      return result;
    }
  }

}

