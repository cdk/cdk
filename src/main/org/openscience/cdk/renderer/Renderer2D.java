/*  $Revision$ $Author$ $Date$
 *  
 *  Copyright (C) 2002-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
 */
package org.openscience.cdk.renderer;

import java.awt.Graphics2D;
import java.util.Iterator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;

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
 * @cdk.svnrev  $Revision$
 *@cdk.created    2002-10-03
 *@cdk.keyword    viewer, 2D-viewer
 *@see            org.openscience.cdk.renderer.Renderer2DModel
 */
public class Renderer2D extends SimpleRenderer2D implements IRenderer2D
{
	
	int oldbondcount=0;
	int oldatomcount=0;

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


	/* (non-Javadoc)
	 * @see org.openscience.cdk.renderer.IRenderer2D#paintChemModel(org.openscience.cdk.interfaces.IChemModel, java.awt.Graphics2D)
	 */
	public void paintChemModel(IChemModel model, Graphics2D graphics)
	{
		customizeRendering(graphics);
		setupIsotopeFactory(model);
		tooltiparea = null;
		paintPointerVector(graphics);
		if (model.getReactionSet() != null)
		{
			paintReactionSet(model.getReactionSet(), graphics);
		} else
		{
			logger.debug("setOfReactions is null");
		}
		if (model.getMoleculeSet() != null)
		{
			paintMoleculeSet(model.getMoleculeSet(), graphics,false);
		} else
		{
			logger.debug("setOfMolecules is null");
		}
		if(r2dm.getRotateRadius()!=0){
			graphics.setColor(r2dm.getSelectedPartColor());
			int radius=(int)getScreenSize((int)r2dm.getRotateRadius());
			int[] provCoords={(int)r2dm.getRotateCenter()[0],(int)r2dm.getRotateCenter()[1]};
			int[] screenCoords = this.getScreenCoordinates(provCoords);
			graphics.drawOval(screenCoords[0]-radius,screenCoords[1]-radius,radius*2,radius*2);
			graphics.drawLine(screenCoords[0]-radius,screenCoords[1],screenCoords[0]-radius-radius/2,screenCoords[1]-radius/2);
			graphics.drawLine(screenCoords[0]-radius,screenCoords[1],screenCoords[0]-radius+radius/2,screenCoords[1]-radius/2);
			graphics.drawLine(screenCoords[0]+radius,screenCoords[1],screenCoords[0]+radius-radius/2,screenCoords[1]+radius/2);
			graphics.drawLine(screenCoords[0]+radius,screenCoords[1],screenCoords[0]+radius+radius/2,screenCoords[1]+radius/2);
		}
		if(r2dm.getSelectRect()!=null){
			graphics.setColor(r2dm.getSelectedPartColor());
			int[] worldCoords={r2dm.getSelectRect().xpoints[0],r2dm.getSelectRect().ypoints[0],r2dm.getSelectRect().xpoints[1],r2dm.getSelectRect().ypoints[1],r2dm.getSelectRect().xpoints[2],r2dm.getSelectRect().ypoints[2],r2dm.getSelectRect().xpoints[3],r2dm.getSelectRect().ypoints[3]};
			int[] screenCoords = getScreenCoordinates(worldCoords);
			graphics.drawRect(screenCoords[0]<screenCoords[4] ? screenCoords[0] : screenCoords[4],screenCoords[1]<screenCoords[3] ? screenCoords[1] : screenCoords[3],Math.abs(screenCoords[0]-screenCoords[4]), Math.abs(screenCoords[1]-screenCoords[3]));
		}
	}


	/* (non-Javadoc)
	 * @see org.openscience.cdk.renderer.IRenderer2D#paintReactionSet(org.openscience.cdk.interfaces.IReactionSet, java.awt.Graphics2D)
	 */
	public void paintReactionSet(org.openscience.cdk.interfaces.IReactionSet reactionSet, Graphics2D graphics) {
		for (java.util.Iterator iter = reactionSet.reactions(); iter.hasNext();)
		{
			paintReaction((IReaction)iter.next(), graphics);
		}
	}

	public void paintMoleculeSet(IMoleculeSet moleculeSet, Graphics2D graphics) {
		paintMoleculeSet(moleculeSet, graphics, true);		
	}
	
	/* (non-Javadoc)
	 * @see org.openscience.cdk.renderer.IRenderer2D#paintMoleculeSet(org.openscience.cdk.interfaces.IMoleculeSet, java.awt.Graphics2D, boolean)
	 */
	public void paintMoleculeSet(IMoleculeSet moleculeSet, Graphics2D graphics, boolean split) {
		logger.debug("painting set of molecules");
		IMoleculeSet molecules = null;
		if(split){
			// WTF?? why does it need to partition at all?
			IAtomContainer atomContainer = moleculeSet.getBuilder().newAtomContainer();
			Iterator atomCons = moleculeSet.atomContainers();
			while (atomCons.hasNext()) atomContainer.add((IAtomContainer)atomCons.next());
			try
			{
				molecules = ConnectivityChecker.partitionIntoMolecules(atomContainer);
				logger.debug("We have " + molecules.getAtomContainerCount() + " molecules on screen");
			} catch (Exception exception)
			{
				logger.warn("Could not partition molecule: ", exception.getMessage());
				logger.debug(exception);
				return;
			}
		}else{
			molecules=moleculeSet;
		}
		int bondcount=0;
		int atomcount=0;
		for (int i = 0; i < molecules.getAtomContainerCount(); i++){
			IMolecule mol = molecules.getMolecule(i);
			bondcount+=mol.getBondCount();
			atomcount+=mol.getAtomCount();
		}
		boolean redossr=false;
		if(bondcount!=oldbondcount || atomcount!=oldatomcount) // some kind of heuristic?
			redossr=true;
		for (int i = 0; i < molecules.getAtomContainerCount(); i++)
		{
			logger.debug("painting molecule " + i);
			paintMolecule(molecules.getMolecule(i), graphics,false, redossr);
		}
		if(r2dm.getMerge()!=null){
			Iterator iterator =r2dm.getMerge().keySet().iterator();
			while(iterator.hasNext()){
				IAtom atom1=(IAtom)iterator.next();
				int[] coords = { (int)r2dm.getRenderingCoordinate(atom1).x,(int)r2dm.getRenderingCoordinate(atom1).y};
				int[] screenCoords = getScreenCoordinates(coords);
				graphics.setColor(r2dm.getSelectedPartColor());
				graphics.drawOval((int)(screenCoords[0]-r2dm.getBondLength()/2),(int)(screenCoords[1]-r2dm.getBondLength()/2),(int)r2dm.getBondLength(),(int)r2dm.getBondLength());
				
			}
		}
		oldbondcount=bondcount;
		oldatomcount=atomcount;
	}


	/* (non-Javadoc)
	 * @see org.openscience.cdk.renderer.IRenderer2D#paintReaction(org.openscience.cdk.interfaces.IReaction, java.awt.Graphics2D)
	 */
	public void paintReaction(IReaction reaction, Graphics2D graphics) {
		// paint atom atom mappings
		IAtom highlighted = r2dm.getHighlightedAtom();
		if (r2dm.getShowAtomAtomMapping() && highlighted != null)
		{
			logger.debug("Showing atom-atom mapping");
			java.util.Iterator mappings = reaction.mappings();
			//logger.debug(" #mappings: ", mappings.length);
			while (mappings.hasNext())
			{
				IMapping objects = (IMapping)mappings.next();
				IChemObject object0 = objects.getChemObject(0);
				IChemObject object1 = objects.getChemObject(1);
				// only draw mapping when one of the mapped atoms
				// is highlighted
				if (object0 instanceof IAtom && object1 instanceof IAtom)
				{
					logger.debug("    atom1: ", object0);
					logger.debug("    atom1: ", object1);
					logger.debug("    highlighted: ", highlighted);
					if (highlighted == object0 || highlighted == object1)
					{
						IAtom atom1 = (IAtom) object0;
						IAtom atom2 = (IAtom) object1;
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
	
		IAtomContainer reactantContainer = reaction.getBuilder().newAtomContainer();
		IMoleculeSet reactants = reaction.getReactants();
		for (int i = 0; i < reactants.getAtomContainerCount(); i++)
		{
			reactantContainer.add(reactants.getMolecule(i));
		}
		IAtomContainer productContainer = reaction.getBuilder().newAtomContainer();
		IMoleculeSet products = reaction.getProducts();
		for (int i = 0; i < products.getAtomContainerCount(); i++)
		{
			productContainer.add(products.getMolecule(i));
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
		paintMolecule(reactantContainer, graphics,false,true);
	
		// paint products content
		if (r2dm.getShowReactionBoxes()) paintBoundingBox(minmaxProducts, "Products", width, graphics);
		paintMolecule(productContainer, graphics,false,true);
	
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
			if (direction == IReaction.FORWARD)
			{
				graphics.drawLine(screenCoords[0], screenCoords[1],
						screenCoords[2], screenCoords[3]);
				graphics.drawLine(screenCoords[2], screenCoords[3],
						screenCoords[2] - 7, screenCoords[3] - 7);
				graphics.drawLine(screenCoords[2], screenCoords[3],
						screenCoords[2] - 7, screenCoords[3] + 7);
			} else if (direction == IReaction.BACKWARD)
			{
				graphics.drawLine(screenCoords[0], screenCoords[1],
						screenCoords[2], screenCoords[3]);
				graphics.drawLine(screenCoords[0], screenCoords[1],
						screenCoords[0] + 7, screenCoords[1] - 7);
				graphics.drawLine(screenCoords[0], screenCoords[1],
						screenCoords[0] + 7, screenCoords[1] + 7);
			} else if (direction == IReaction.BIDIRECTIONAL)
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

