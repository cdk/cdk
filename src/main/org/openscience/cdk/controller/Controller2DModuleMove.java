/* $Revision: 7636 $ $Author: nielsout $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Niels Out <nielsout@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.controller;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.renderer.IJava2DRenderer;


/**
 * Demo IController2DModule.
 * -write picture to file on doubleclick
 * -show atom name on hove-over
 * -drags atoms around (click near atom and move mouse)
 * 
 * @author Niels Out
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.module  control
 */
public class Controller2DModuleMove implements IController2DModule {

	private IChemModelRelay chemObjectRelay;
	/*private IViewEventRelay eventRelay;
	public void setEventRelay(IViewEventRelay relay) {
		this.eventRelay = relay;
	}*/
	
	public void mouseClickedDouble(Point2d worldCoord) {
		// TODO Auto-generated method stub
		try {
			//try to write the image to a file
			int width = 400, height = 400;
			
		  System.out.println("\tstarting..\n");
	      // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels
		  BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	      System.out.println("bi created\n");

	      Graphics2D ig2 = bi.createGraphics();
	      System.out.println("ig2 created\n");
	      
	      IJava2DRenderer renderer = chemObjectRelay.getIJava2DRenderer();

	      ig2.setColor(renderer.getRenderer2DModel().getBackColor());
	      ig2.fillRect(0, 0, width, height);
 
	      Rectangle2D rectangle = new Rectangle2D.Double();
	      rectangle.setFrame(0, 0, width, height);
	      IChemModel chemModel = chemObjectRelay.getIChemModel();
	      //FIXME: render all AtomContainers in this MoleculeSet
	      IAtomContainer atomC = chemModel.getMoleculeSet().getAtomContainer(0);
	      renderer.paintMolecule(atomC, ig2, rectangle);
	      System.out.println("renderer.paintMolecule done\n");

	      ImageIO.write(bi, "PNG", new File("c:\\tmp\\yourImageName.PNG"));
	      System.out.println("writing output file to 'c:\\tmp\\yourImageName.PNG' done\n");

	    } catch (IOException ie) {
	      ie.printStackTrace();
	    }
	}

	public void mouseClickedDown(Point2d worldCoord) {
		// TODO Auto-generated method stub
		
	}

	public void mouseClickedUp(Point2d worldCoord) {
		// TODO Auto-generated method stub
		
	}

	public void mouseDrag(Point2d worldCoordFrom, Point2d worldCoordTo) {
		// TODO Auto-generated method stub
		System.out.println("mousedrag at DumpClosestObject shizzle");
		System.out.println("From: " + worldCoordFrom.x + "/" + worldCoordFrom.y + " to " +
				worldCoordTo.x + "/" + worldCoordTo.y);
		
		if (chemObjectRelay != null) {
			IAtom atom = chemObjectRelay.getClosestAtom(worldCoordFrom);
			if (atom != null) {
				System.out.println("Dragging atom: " + atom);
				double offsetX = worldCoordFrom.x - atom.getPoint2d().x;
				double offsetY = worldCoordFrom.y - atom.getPoint2d().y;
				Point2d atomCoord = new Point2d(worldCoordTo.x - offsetX, worldCoordTo.y - offsetY);
				
				atom.setPoint2d(atomCoord);
				chemObjectRelay.updateView();
				
			}
		} else {
			System.out.println("chemObjectRelay is NULL!");
		}
	}

	public void mouseEnter(Point2d worldCoord) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExit(Point2d worldCoord) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMove(Point2d worldCoord) {
		if (chemObjectRelay != null) {
			IAtom atom = chemObjectRelay.getClosestAtom(worldCoord);
			if (atom != null) {
				//System.out.println("Found atom: " + atom);
			}
		} else {
			System.out.println("chemObjectRelay is NULL!");
		}
	}

	public void setChemModelRelay(IChemModelRelay relay) {
		this.chemObjectRelay = relay;
	}
	
}
