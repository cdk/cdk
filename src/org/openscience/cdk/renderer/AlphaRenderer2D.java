/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.renderer;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.LoggingTool;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

/**
 * A subclass of Renderer2D that uses masks (Area Class) to make an area
 * erased to background.
 *
 * @cdk.module render
 *
 * @author     akrassavine
 *
 * @cdk.created    2004-02-04
 */
public class AlphaRenderer2D extends Renderer2D {
    
  private LoggingTool logger;

  private Renderer2DModel r2dm = null;
  private Area mask = null;

  public AlphaRenderer2D()
  {
    this(new Renderer2DModel());
  }

  public AlphaRenderer2D(Renderer2DModel r2dm)
  {
    super(r2dm);
    this.r2dm = r2dm;
    logger = new LoggingTool(this);
  }

  public void paintEmptySpace(int x, int y, int width, int height, int border, Color backColor, Graphics2D g)
  {
    int[] coords = { x - border, y + border };
    double[] bounds = { getScreenSize(width + 2 * border), getScreenSize(height + 2 * border)};
    int[] screenCoords = getScreenCoordinates(coords);

    mask.subtract(new Area(new Rectangle2D.Double(screenCoords[0], screenCoords[1], bounds[0], bounds[1])));
}

protected IRingSet getRingSet(IAtomContainer atomContainer)
{
  IRingSet ringSet = atomContainer.getBuilder().newRingSet();
  org.openscience.cdk.interfaces.IMolecule[] molecules = null;

  try
  {
    molecules = ConnectivityChecker.partitionIntoMolecules(atomContainer).getMolecules();
  }

  catch (Exception exception)
  {
    logger.warn("Could not partition molecule: ", exception.getMessage());
    logger.debug(exception);
    return ringSet;
  }

  for (int i = 0; i < molecules.length; i++)
  {
    SSSRFinder sssrf = new SSSRFinder(molecules[i]);

    ringSet.add(sssrf.findSSSR());
  }

  return ringSet;
}

public void paintMolecule(IAtomContainer atomContainer, Graphics2D graphics)
{
  // make the initial mask cover the entire dimension we are going to paint
  mask =
    new Area(new Rectangle2D.Double(0, 0, r2dm.getBackgroundDimension().width, r2dm.getBackgroundDimension().height));

  if (r2dm.getPointerVectorStart() != null && r2dm.getPointerVectorEnd() != null)
  {
    paintPointerVector(graphics);
  }

  paintAtoms(atomContainer, graphics);

  Shape oldClip = graphics.getClip();
  graphics.setClip(mask);
  paintBonds(atomContainer, getRingSet(atomContainer), graphics);
  graphics.setClip(oldClip);

  paintLassoLines(graphics);

  mask = null;
}

}
