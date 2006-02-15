/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Mapping;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.tools.manipulator.SetOfMoleculesManipulator;

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
 *@cdk.bug        834515
 *@see            org.openscience.cdk.renderer.Renderer2DModel
 */
public class Renderer2D extends SimpleRenderer2D
{

	/**
	 *  Constructs a Renderer2D with a default settings model.
	 */
	public Renderer2D()
	{
		super(new Renderer2DModel());
	}


	/**
	 *  Constructs a Renderer2D.
	 *
	 *@param  r2dm  The settings model to use for rendering.
	 */
	public Renderer2D(Renderer2DModel r2dm)
	{
		super(r2dm);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  model     Description of the Parameter
	 *@param  graphics  Description of the Parameter
	 */
	public void paintChemModel(IChemModel model, Graphics2D graphics)
	{
		customizeRendering(graphics);
		tooltiparea = null;
		paintPointerVector(graphics);
		if (model.getSetOfReactions() != null)
		{
			paintSetOfReactions(model.getSetOfReactions(), graphics);
		} else
		{
			logger.debug("setOfReactions is null");
		}
		if (model.getSetOfMolecules() != null)
		{
			paintSetOfMolecules(model.getSetOfMolecules(), graphics,false);
		} else
		{
			logger.debug("setOfMolecules is null");
		}
	
	}


	/**
	 *  Description of the Method
	 *
	 *@param  reactionSet  Description of the Parameter
	 *@param  graphics     Description of the Parameter
	 */
	public void paintSetOfReactions(org.openscience.cdk.interfaces.ISetOfReactions reactionSet, Graphics2D graphics) {
		IReaction[] reactions = reactionSet.getReactions();
		for (int i = 0; i < reactions.length; i++)
		{
			paintReaction(reactions[i], graphics);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  moleculeSet  Description of the Parameter
	 *@param  graphics     Description of the Parameter
	 *@param  split        If true the setOfMolecule will be united and then splitted again in single molecules before painted. Typically not needed a performance killler
	 */
	public void paintSetOfMolecules(org.openscience.cdk.interfaces.ISetOfMolecules moleculeSet, Graphics2D graphics, boolean split) {
		logger.debug("painting set of molecules");
		IMolecule[] molecules = null;
		if(split){
			org.openscience.cdk.interfaces.IAtomContainer atomContainer = SetOfMoleculesManipulator.getAllInOneContainer(moleculeSet);
			try
			{
				molecules = ConnectivityChecker.partitionIntoMolecules(atomContainer).getMolecules();
				logger.debug("We have " + molecules.length + " molecules on screen");
			} catch (Exception exception)
			{
				logger.warn("Could not partition molecule: ", exception.getMessage());
				logger.debug(exception);
				return;
			}
		}else{
			molecules=moleculeSet.getMolecules();
		}
		for (int i = 0; i < molecules.length; i++)
		{
			logger.debug("painting molecule " + i);
			paintMolecule(molecules[i], graphics,false);
		}
		if(r2dm.getMerge()!=null){
			Iterator it=r2dm.getMerge().keySet().iterator();
			while(it.hasNext()){
				IAtom atom1=(IAtom)it.next();
				int[] coords = { (int)atom1.getPoint2d().x,(int)atom1.getPoint2d().y};
				int[] screenCoords = getScreenCoordinates(coords);
				graphics.setColor(Color.MAGENTA);
				graphics.drawOval((int)(screenCoords[0]-r2dm.getBondLength()/2),(int)(screenCoords[1]-r2dm.getBondLength()/2),(int)r2dm.getBondLength(),(int)r2dm.getBondLength());
				
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  reaction  Description of the Parameter
	 *@param  graphics  Description of the Parameter
	 */
	public void paintReaction(IReaction reaction, Graphics2D graphics) {
		// paint atom atom mappings
		IAtom highlighted = r2dm.getHighlightedAtom();
		if (r2dm.getShowAtomAtomMapping() && highlighted != null && 
		    reaction instanceof org.openscience.cdk.Reaction)
		{
			logger.debug("Showing atom-atom mapping");
			IMapping[] mappings = ((org.openscience.cdk.Reaction)reaction).getMappings();
			logger.debug(" #mappings: ", mappings.length);
			for (int i = 0; i < mappings.length; i++)
			{
				IChemObject[] objects = mappings[i].getRelatedChemObjects();
				// only draw mapping when one of the mapped atoms
				// is highlighted
				if (objects[0] instanceof IAtom && objects[1] instanceof IAtom)
				{
					logger.debug("    atom1: ", objects[0]);
					logger.debug("    atom1: ", objects[1]);
					logger.debug("    highlighted: ", highlighted);
					if (highlighted == objects[0] || highlighted == objects[1])
					{
						IAtom atom1 = (IAtom) objects[0];
						IAtom atom2 = (IAtom) objects[1];
						int[] ints = new int[4];
						ints[0] = (int) (atom1.getPoint2d().x);
						ints[1] = (int) (atom1.getPoint2d().y);
						ints[2] = (int) (atom2.getPoint2d().x);
						ints[3] = (int) (atom2.getPoint2d().y);
						int[] screenCoords = getScreenCoordinates(ints);
						graphics.setColor(r2dm.getAtomAtomMappingLineColor());
						logger.debug("Mapping line color", r2dm.getAtomAtomMappingLineColor());
						logger.debug("Mapping line coords atom1.x: ", screenCoords[0]);
						logger.debug("Mapping line coords atom1.y: ", screenCoords[1]);
						logger.debug("Mapping line coords atom2.x: ", screenCoords[2]);
						logger.debug("Mapping line coords atom2.y: ", screenCoords[3]);
						graphics.drawLine(screenCoords[0], screenCoords[1],
								screenCoords[2], screenCoords[3]);
						graphics.setColor(r2dm.getForeColor());
					} else
					{
						logger.debug("  skipping this mapping. Not of hightlighted atom");
					}
				} else
				{
					logger.debug("Not showing a non atom-atom mapping");
				}
			}
		} else
		{
			logger.debug("Not showing atom-atom mapping");
		}
	
		IAtomContainer reactantContainer = new org.openscience.cdk.AtomContainer();
		IMolecule[] reactants = reaction.getReactants().getMolecules();
		for (int i = 0; i < reactants.length; i++)
		{
			reactantContainer.add(reactants[i]);
		}
		IAtomContainer productContainer = new org.openscience.cdk.AtomContainer();
		IMolecule[] products = reaction.getProducts().getMolecules();
		for (int i = 0; i < products.length; i++)
		{
			productContainer.add(products[i]);
		}

		// calculate some boundaries
		double[] minmaxReactants = GeometryTools.getMinMax(reactantContainer);
		double[] minmaxProducts = GeometryTools.getMinMax(productContainer);
		
		// paint box around total
		int width = 13;
		double[] minmaxReaction = new double[4];
		minmaxReaction[0] = Math.min(minmaxReactants[0], minmaxProducts[0]);
		minmaxReaction[1] = Math.min(minmaxReactants[1], minmaxProducts[1]);
		minmaxReaction[2] = Math.max(minmaxReactants[2], minmaxProducts[2]);
		minmaxReaction[3] = Math.max(minmaxReactants[3], minmaxProducts[3]);
		String caption = reaction.getID();
		if (reaction.getProperty(CDKConstants.TITLE) != null)
		{
			caption = reaction.getProperty(CDKConstants.TITLE) +
			" (" + caption + ")";
		} else if (caption == null)
		{
			caption = "r" + reaction.hashCode();
		}
		if (r2dm.getShowReactionBoxes()) paintBoundingBox(minmaxReaction, caption, 2 * width, graphics);
	
		// paint reactants content
		if (r2dm.getShowReactionBoxes()) paintBoundingBox(minmaxReactants, "Reactants", width, graphics);
		paintMolecule(reactantContainer, graphics,false);
	
		// paint products content
		if (r2dm.getShowReactionBoxes()) paintBoundingBox(minmaxProducts, "Products", width, graphics);
		paintMolecule(productContainer, graphics,false);
	
		if (productContainer.getAtomCount() > 0 && reactantContainer.getAtomCount() > 0)
		{
			// paint arrow
			int[] ints = new int[4];
			ints[0] = (int) (minmaxReactants[2]) + width + 5;
			ints[1] = (int) (minmaxReactants[1] + (minmaxReactants[3] - minmaxReactants[1]) / 2);
			ints[2] = (int) (minmaxProducts[0]) - (width + 5);
			ints[3] = ints[1];
			int[] screenCoords = getScreenCoordinates(ints);
			int direction = reaction.getDirection();
			if (direction == org.openscience.cdk.Reaction.FORWARD)
			{
				graphics.drawLine(screenCoords[0], screenCoords[1],
						screenCoords[2], screenCoords[3]);
				graphics.drawLine(screenCoords[2], screenCoords[3],
						screenCoords[2] - 7, screenCoords[3] - 7);
				graphics.drawLine(screenCoords[2], screenCoords[3],
						screenCoords[2] - 7, screenCoords[3] + 7);
			} else if (direction == org.openscience.cdk.Reaction.BACKWARD)
			{
				graphics.drawLine(screenCoords[0], screenCoords[1],
						screenCoords[2], screenCoords[3]);
				graphics.drawLine(screenCoords[0], screenCoords[1],
						screenCoords[0] + 7, screenCoords[1] - 7);
				graphics.drawLine(screenCoords[0], screenCoords[1],
						screenCoords[0] + 7, screenCoords[1] + 7);
			} else if (direction == org.openscience.cdk.Reaction.BIDIRECTIONAL)
			{
				graphics.drawLine(screenCoords[0], screenCoords[1] - 3,
						screenCoords[2], screenCoords[3] - 3);
				graphics.drawLine(screenCoords[0], screenCoords[1] - 3,
						screenCoords[0] + 7, screenCoords[1] - 3 - 7);
				graphics.drawLine(screenCoords[0], screenCoords[1] + 3,
						screenCoords[2], screenCoords[3] + 3);
				graphics.drawLine(screenCoords[2], screenCoords[3] + 3,
						screenCoords[2] - 7, screenCoords[3] + 3 + 7);
			}
		}
	}
}

