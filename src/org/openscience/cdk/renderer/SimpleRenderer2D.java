/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulator;

/**
 *  A Renderer class which draws 2D representations of molecules onto a given
 *  graphics objects using information from a Renderer2DModel. <p>
 *
 *  This renderer uses two coordinate systems. One that is a world coordinates
 *  system which is generated from the document coordinates. Additionally, the
 *  screen coordinates make up the second system, and are calculated by applying
 *  a zoom factor to the world coordinates. <p>
 *
 *  The coordinate system used for display has its origin in the left-bottom
 *  corner, with the x axis to the right, and the y axis towards the top of the
 *  screen. The system is thus right handed. <p>
 *
 *  The two main methods are paintMolecule() and paintChemModel(). Others might
 *  not show full rendering, e.g. anti-aliasing. <p>
 *
 *  This modules tries to adhere to guidelines being developed by the IUPAC
 *  which results can be found at <a href="http://www.angelfire.com/sc3/iupacstructures/">
 *  http://www.angelfire.com/sc3/iupacstructures/</a> .
 *
 *@author         steinbeck
 *@author         egonw
 *@cdk.module     render
 *@cdk.created    2002-10-03
 *@cdk.keyword    viewer, 2D-viewer
 *
 *@see            org.openscience.cdk.renderer.Renderer2DModel
 */
public class SimpleRenderer2D extends AbstractRenderer2D implements ISimpleRenderer2D
{

	IRingSet ringSet;
	
	/**
	 *  Constructs a Renderer2D with a default settings model.
	 */
	public SimpleRenderer2D()
	{
		super(new Renderer2DModel());
	}


	/**
	 *  Constructs a Renderer2D.
	 *
	 *@param  r2dm  The settings model to use for rendering.
	 */
	public SimpleRenderer2D(Renderer2DModel r2dm)
	{
		super(r2dm);
	}

	public void paintMolecule(IAtomContainer atomCon, Graphics2D graphics, Rectangle2D bounds) {
		paintMolecule(atomCon, graphics, true, true);
	}

	public void paintMolecule(IAtomContainer atomCon, Graphics2D graphics) {
		paintMolecule(atomCon, graphics, true, true);
	}
	
	/* (non-Javadoc)
	 * @see org.openscience.cdk.renderer.ISimpleRenderer#paintMolecule(org.openscience.cdk.interfaces.IAtomContainer, java.awt.Graphics2D, boolean, boolean)
	 */
	public void paintMolecule(IAtomContainer atomCon, Graphics2D graphics, boolean split, boolean redossr) {
        logger.debug("inside paintMolecule()");
        updateRenderingCoordinates(atomCon,r2dm);
		customizeRendering(graphics);
		setupIsotopeFactory(atomCon);
		
		// draw the molecule name
		if (r2dm.getShowMoleculeTitle() && 
		    atomCon.getProperty(CDKConstants.TITLE) != null) {
			double[] minmax = GeometryTools.getMinMax(atomCon,r2dm.getRenderingCoordinates());
			int[] ints = new int[4];
			for (int i=0;i<4;i++) ints[i] = (int)minmax[i];
			int[] screenCoords = getScreenCoordinates(ints);
			graphics.drawString(
			    atomCon.getProperty(CDKConstants.TITLE).toString(), 
			    (int) screenCoords[0], Math.abs(screenCoords[2]-screenCoords[3])
			);
		}
		
		List moleculesList = null;
		if(split){
			try
			{
				moleculesList = AtomContainerSetManipulator.getAllAtomContainers(ConnectivityChecker.partitionIntoMolecules(atomCon));
			} catch (Exception exception)
			{
				logger.warn("Could not partition molecule: ", exception.getMessage());
				logger.debug(exception);
				return;
			}
		}else {
			moleculesList = new ArrayList();
			moleculesList.add(atomCon);
		}
		if(redossr){
			redoSSSR(moleculesList);
		}
        paintBonds(atomCon, ringSet, graphics);
		paintAtoms(atomCon, graphics);
		if (r2dm.getSelectRect() != null)
		{
			graphics.setColor(r2dm.getSelectedPartColor());
			graphics.drawPolygon(r2dm.getSelectRect());
		}
		paintLassoLines(graphics);
	
	}
	
	/* (non-Javadoc)
	 * @see org.openscience.cdk.renderer.ISimpleRenderer#redoSSSR(java.util.List)
	 */
	public void redoSSSR(List moleculesList){
			if(ringSet==null && moleculesList.size() > 0)
				ringSet= ((IAtomContainer)moleculesList.get(0)).getBuilder().newRingSet();
			Iterator iterator = moleculesList.iterator();
			while(iterator.hasNext())
			{
				SSSRFinder sssrf = new SSSRFinder((IAtomContainer)iterator.next());
				ringSet.add(sssrf.findSSSR());
			}
	}

}

