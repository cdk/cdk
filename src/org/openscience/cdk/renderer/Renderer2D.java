/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 *
 *  Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.renderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.vecmath.*;
import java.util.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;

/**
 * A Renderer class which draws 2D representations of molecules onto a given
 * graphics objects using information from a Renderer2DModel.
 *
 * <p>This renderer uses two coordinate systems. One that is a world
 * coordinates system which is generated from the document coordinates.
 * Additionally, the screen coordinates make up the second system, and
 * are calculated by applying a zoom factor to the world coordinates.
 *
 * @author     steinbeck
 * @created    October 3, 2002
 * @keyword    viewer, 2D-viewer
 */
public class Renderer2D   {

    private LoggingTool logger;
    
	SSSRFinder sssrf = new SSSRFinder();

	/**
	 *  Description of the Field
	 */
	private Renderer2DModel r2dm;

	/**
	 * Constructs a Renderer2D with a default settings model.
	 */
	public Renderer2D() {
		this(new Renderer2DModel());
	}

	/**
	 * Constructs a Renderer2D.
	 *
	 * @param  r2dm  The settings model to use for rendering.
	 */
	public Renderer2D(Renderer2DModel r2dm)
	{
		this.r2dm = r2dm;
        logger = new LoggingTool(this.getClass().getName());
	}

    public void paintChemModel(ChemModel model, Graphics graphics) {
        if (model.getSetOfReactions() != null) {
            paintSetOfReactions(model.getSetOfReactions(), graphics);
        } else {
            paintMolecule(ChemModelManipulator.getAllInOneContainer(model), graphics);
        }
    }
    
    public void paintSetOfReactions(SetOfReactions reactionSet, Graphics graphics) {
        Reaction[] reactions = reactionSet.getReactions();
        for (int i=0; i<reactions.length; i++) {
            paintReaction(reactions[i], graphics);
        }
    }
    
    public void paintReaction(Reaction reaction, Graphics graphics) {
        paintBoundingBox(ReactionManipulator.getAllInOneContainer(reaction),
                         reaction.getID(), 20, graphics);
        
        // paint reactants content
        AtomContainer reactantContainer = new AtomContainer();
        Molecule[] reactants = reaction.getReactants();
        for (int i=0; i<reactants.length; i++) {
            reactantContainer.add(reactants[i]);
        }
        paintBoundingBox(reactantContainer, "Reactants", 10, graphics);
        paintMolecule(reactantContainer, graphics);

        // paint products content
        AtomContainer productContainer = new AtomContainer();
        Molecule[] products = reaction.getProducts();
        for (int i=0; i<products.length; i++) {
            productContainer.add(products[i]);
        }
        paintBoundingBox(productContainer, "Products", 10, graphics);
        paintMolecule(productContainer, graphics);
    }
    
    private void paintBoundingBox(AtomContainer container, String caption, 
                                  int side, Graphics graphics) {
        double[] minmax = GeometryTools.getMinMax(container);
        int[] ints = new int[4];
        ints[0] = (int)minmax[0] -side;
        ints[1] = (int)minmax[1] -side;
        ints[2] = (int)minmax[2] +side;
        ints[3] = (int)minmax[3] +side;
        int[] screenCoords = getScreenCoordinates(ints);
        int heigth = screenCoords[3] - screenCoords[1]; 
        int width = screenCoords[2] - screenCoords[0]; 
        graphics.drawRect((int)screenCoords[0], (int)screenCoords[1], width, heigth);

        // draw reaction ID
        Font unscaledFont = graphics.getFont();
        int fontSize = getScreenSize(unscaledFont.getSize());
        graphics.setFont(unscaledFont.deriveFont((float)fontSize));
        graphics.drawString(caption, (int)screenCoords[0], (int)screenCoords[1]);
        graphics.setFont(unscaledFont);
    }
    
	/**
	 *  triggers the methods to make the molecule fit into the frame and to paint
	 *  it.
	 *
	 *@param  atomCon  Description of the Parameter
	 *@param  graphics        Description of the Parameter
	 */
	public void paintMolecule(AtomContainer atomCon, Graphics graphics) {
		RingSet ringSet = new RingSet();
		Vector molecules = null;
		try
		{
			molecules = ConnectivityChecker.partitionIntoMolecules(atomCon);
		} catch (Exception exception)
		{
            logger.warn("Could not partition molecule: " + exception.toString());
			exception.printStackTrace();
		}
		for (int i = 0; i < molecules.size(); i++)
		{
			ringSet.add(sssrf.findSSSR((Molecule) molecules.elementAt(i)));
		}
		if (r2dm.getPointerVectorStart() != null && r2dm.getPointerVectorEnd() != null)
		{
			paintPointerVector(graphics);
		}
		paintBonds(atomCon, ringSet, graphics);
		paintAtoms(atomCon, graphics);
		if (r2dm.drawNumbers())
		{
			paintNumbers(atomCon, atomCon.getAtomCount(), graphics);
		}
		if (r2dm.getSelectRect() != null)
		{
		    graphics.setColor(r2dm.getHighlightColor());
		    graphics.drawPolygon(r2dm.getSelectRect());
		}
		paintLassoLines(graphics);
	}


	/**
	 *  Description of the Method
	 */
	private void paintLassoLines(Graphics graphics)
	{
		Vector points = r2dm.getLassoPoints();
		if (points.size() > 1)
		{
			Point point1 = (Point) points.elementAt(0);
			Point point2;
			for (int i = 1; i < points.size(); i++)
			{
				point2 = (Point) points.elementAt(i);
			    graphics.drawLine(point1.x, point1.y, point2.x, point2.y);
				point1 = point2;
			}
		}
	}


	/**
	 *  Draw all numbers of all atoms in the molecule
	 *
	 *@param  atoms   The array of atoms
	 *@param  number  The number of atoms in this array
	 */
	private void paintNumbers(AtomContainer container, int number, Graphics graphics)
	{
        Atom[] atoms = container.getAtoms();
		for (int i = 0; i < number; i++)
		{
			paintNumber(container, atoms[i], graphics);
		}
	}


	/*
	 *  Paints the numbers
	 *
	 *  @param   atom    The atom to be drawn
	 */
	/**
	 *  Description of the Method
	 *
	 *@param  atom  Description of the Parameter
	 */
	private void paintNumber(AtomContainer container, Atom atom, Graphics graphics)
	{
		if (atom.getPoint2D() == null)
		{
			return;
		}
		FontMetrics fm = graphics.getFontMetrics();
		int xSymbOffset = (new Integer(fm.stringWidth(atom.getSymbol()) / 2)).intValue();
		int ySymbOffset = (new Integer(fm.getAscent() / 2)).intValue();

		try
		{
			int i = container.getAtomNumber(atom);
//		    graphics.setColor(r2dm.getBackColor());
//		    graphics.fillRect((int)(atom.getPoint2D().x - (xSymbOffset * 1.8)),(int)(atom.getPoint2D().y - (ySymbOffset * 0.8)),(int)fontSize,(int)fontSize);
		    graphics.setColor(r2dm.getForeColor());
		    graphics.drawString(Integer.toString(i+1), (int) (atom.getPoint2D().x + (xSymbOffset)), (int) (atom.getPoint2D().y - (ySymbOffset)));
		    graphics.setColor(r2dm.getBackColor());
		    graphics.drawLine((int) atom.getPoint2D().x, (int) atom.getPoint2D().y, 
                              (int) atom.getPoint2D().x, (int) atom.getPoint2D().y);
		} catch (Exception exception)
		{
            logger.error("Error while drawing atom number:" + exception.toString());
		}
	}


	/**
	 *  Searches through all the atoms in the given array of atoms, triggers the
	 *  paintColouredAtoms method if the atom has got a certain color and triggers
	 *  the paintAtomSymbol method if the symbol of the atom is not C.
	 *
	 *@param  atomCon  Description of the Parameter
	 */
	private void paintAtoms(AtomContainer atomCon, Graphics graphics)
	{
		Atom atom;
		for (int i = 0; i < atomCon.getAtomCount(); i++) {
			atom = atomCon.getAtomAt(i);
            paintAtom(atomCon, atom, graphics);
		}
	}

	private void paintAtom(AtomContainer container, Atom atom, Graphics graphics) {
        Color atomColor = (Color) r2dm.getColorHash().get(atom);
        if (atom == r2dm.getHighlightedAtom()) {
            atomColor = r2dm.getHighlightColor();
        }
        if (atomColor != null) {
            paintColouredAtom(atom, atomColor, graphics);
        } else {
            atomColor = r2dm.getBackColor();
        }
        int alignment = GeometryTools.getBestAlignmentForLabel(container, atom);
        if (!atom.getSymbol().equals("C")) {
            /*
             *  only show element for non-carbon atoms,
             *  unless (see below)...
             */
            paintAtomSymbol(atom, atomColor, graphics, alignment);
            paintAtomCharge(atom, graphics);
        } else if (r2dm.getKekuleStructure()) {
            // ... unless carbon must be drawn because in Kekule mode
            paintAtomSymbol(atom, atomColor, graphics, alignment);
        } else if (atom.getFormalCharge() != 0) {
            // ... unless carbon is charged
            paintAtomSymbol(atom, atomColor, graphics, alignment);
            paintAtomCharge(atom, graphics);
        } else if (container.getConnectedBonds(atom).length < 1) {
            // ... unless carbon is unbonded
            paintAtomSymbol(atom, atomColor, graphics, alignment);
        } else if (r2dm.getShowEndCarbons() && (container.getConnectedBonds(atom).length == 1)) {
            // ... unless carbon is an methyl, and the user wants those with symbol
            paintAtomSymbol(atom, atomColor, graphics, alignment);
        }
    }

	/**
	 *  Paints a rectangle of the given color at the position of the given atom.
	 *  For example when the atom is highlighted.
	 *
	 *@param  atom   The atom to be drawn
	 *@param  color  The color of the atom to be drawn
	 */
	private void paintColouredAtom(Atom atom, Color color, Graphics graphics)
	{
		int atomRadius = r2dm.getAtomRadius();
	    graphics.setColor(color);
        int[] coords = {(int) atom.getX2D() - (atomRadius / 2), 
                        (int) atom.getY2D() - (atomRadius / 2), 
                        atomRadius, atomRadius};
        coords = getScreenCoordinates(coords);
	    graphics.fillRect(coords[0], coords[1], coords[2], coords[3]);
	}

    /**
	 *  Paints the given atom symbol. It first outputs some empty space using the
	 *  background color, slightly larger than the space that the symbol occupies.
	 *  The atom symbol is then printed into the empty space.
	 *
	 *@param  atom       The atom to be drawn
	 *@param  backColor  Description of the Parameter
	 */
	private void paintAtomSymbol(Atom atom, Color backColor, Graphics graphics, int alignment) {
		if (atom.getPoint2D() == null) {
			return;
		}
        
		// but first determine symbol
		String symbol = atom.getSymbol();
		// if there are implicit hydrogens, add them to string to display
		int implicitHydrogen = atom.getHydrogenCount();
		if (implicitHydrogen > 0) {
			symbol = symbol + "H";
		}
        
        // draw string:

        /* determine where to put the string, as seen from the atom coordinates
           in model coordinates */
		FontMetrics fm = graphics.getFontMetrics();
        // left align
		int xSymbOffset = (new Integer(fm.stringWidth(symbol.substring(0,1)) / 2)).intValue();
        if (alignment == -1) {
            // right align
            xSymbOffset = (new Integer((fm.stringWidth(symbol.substring(symbol.length()-1)) / 2) +
                                       fm.stringWidth(symbol.substring(1)))).intValue();
        }
		int ySymbOffset = (new Integer(fm.getAscent() / 2)).intValue();

		int xSymbOffsetForSubscript = (new Integer(fm.stringWidth(symbol))).intValue();
		int ySymbOffsetForSubscript = (new Integer(fm.getAscent())).intValue();

		// make empty space
	    graphics.setColor(backColor);
        Rectangle2D stringBounds = fm.getStringBounds(symbol, graphics);
        int[] coords = {(int) (atom.getPoint2D().x - (xSymbOffset * 1.2)),
                        (int) (atom.getPoint2D().y - (ySymbOffset * 1.2)),
                        (int) (stringBounds.getWidth() * 1.2),
                        (int) (stringBounds.getHeight() * 1.2) };
        coords = getScreenCoordinates(coords);
	    graphics.fillRect(coords[0], coords[1], coords[2], coords[3]);

        int[] hCoords = {(int) (atom.getPoint2D().x - xSymbOffset),
				         (int) (atom.getPoint2D().y + ySymbOffset) };
        hCoords = getScreenCoordinates(hCoords);
	    graphics.setColor(r2dm.getForeColor());
        // apply zoom factor to font size
        Font unscaledFont = graphics.getFont();
        int fontSize = getScreenSize(unscaledFont.getSize());
        graphics.setFont(unscaledFont.deriveFont((float)fontSize));
        graphics.drawString(symbol, hCoords[0], hCoords[1]);

        if (implicitHydrogen > 1) {
            // draw subscript part
            int[] h2Coords = {(int) (atom.getPoint2D().x - xSymbOffset + xSymbOffsetForSubscript),
                              (int) (atom.getPoint2D().y + ySymbOffsetForSubscript) };
            h2Coords = getScreenCoordinates(h2Coords);
            graphics.setColor(r2dm.getForeColor());
            // apply zoom factor to font size
            unscaledFont = graphics.getFont();
            fontSize = getScreenSize(unscaledFont.getSize())-1;
            graphics.setFont(unscaledFont.deriveFont((float)fontSize));
            graphics.drawString(new Integer(implicitHydrogen).toString(), h2Coords[0], h2Coords[1]);
        }
        
        // reset old font
        graphics.setFont(unscaledFont);
	}

	/**
	 *  Paints the given atom symbol. It first outputs some empty space using the
	 *  background color, slightly larger than the space that the symbol occupies.
	 *  The atom symbol is then printed into the empty space.
	 *
	 *@param  atom       The atom to be drawn
	 *@param  backColor  Description of the Parameter
	 */
	private void paintAtomCharge(Atom atom, Graphics graphics) {
        FontMetrics fm = graphics.getFontMetrics();
        int xSymbOffset = (new Integer(fm.stringWidth(atom.getSymbol()) / 2)).intValue();
        int ySymbOffset = (new Integer(fm.getAscent() / 2)).intValue();

            // show formal charge
        if (atom.getFormalCharge() != 0) {
            // print charge in smaller font size
            Font unscaledFont = graphics.getFont();
            int fontSize = getScreenSize(unscaledFont.getSize() - 1);
            graphics.setFont(unscaledFont.deriveFont((float)fontSize));        

            int charge = atom.getFormalCharge();
            String chargeString = (new Integer(charge)).toString();
            if (charge == 1 ) { 
                chargeString = "+"; 
            } else if (charge > 1 ) {
                chargeString = charge + "+";
            } else if (charge == -1) {
                chargeString = "-"; 
            } else if (charge < -1) {
                chargeString = chargeString.substring(1) + "-";
            }
            // draw string
            int[] hCoords = {(int)atom.getX2D() + xSymbOffset,
                             (int)atom.getY2D() - ySymbOffset};
            hCoords = getScreenCoordinates(hCoords);
            graphics.drawString(chargeString, hCoords[0], hCoords[1]);
            
            /** Put circles around + or - sign
            Rectangle2D stringBounds = fm.getStringBounds(chargeString, graphics);
            int width = (int)stringBounds.getWidth();
            int height = (int)stringBounds.getHeight();
            int[] coords = {(int)atom.getX2D() + xSymbOffset - (width/2), 
                            (int)atom.getY2D() - ySymbOffset - (height/2),
                            (int)stringBounds.getWidth(), 
                            (int)stringBounds.getWidth()};
            coords = getScreenCoordinates(coords);
            graphics.drawOval(coords[0], coords[1], coords[2], coords[3]); */
            graphics.setFont(unscaledFont);
        }
    }
    
    
	/**
	 *  Triggers the suitable method to paint each of the given bonds and selects
	 *  the right color.
	 *
	 *@param  ringSet  The set of rings the molecule contains
	 *@param  atomCon  Description of the Parameter
	 */
	private void paintBonds(AtomContainer atomCon, RingSet ringSet, Graphics graphics)
	{
		Color bondColor;
		Ring ring;
		Bond bond;
		for (int i = 0; i < atomCon.getElectronContainerCount(); i++){
            if (atomCon.getElectronContainerAt(i) instanceof Bond) {
                bond = (Bond)atomCon.getElectronContainerAt(i);
                bondColor = (Color) r2dm.getColorHash().get(bond);
                if (bondColor == null)
                {
                    bondColor = r2dm.getForeColor();
                }
                if (bond == r2dm.getHighlightedBond())
                {
                    bondColor = r2dm.getHighlightColor();
                    for (int j = 0; j < bond.getAtomCount(); j++)
                    {
                        paintColouredAtom(bond.getAtomAt(j), bondColor, graphics);
                    }
                }
                ring = ringSet.getHeaviestRing(bond);
                if (ring != null)
                {
                    paintRingBond(bond, ring, bondColor, graphics);

                } else
                {
                    paintBond(bond, bondColor, graphics);
                }
            }
		}
	}


	/**
	 *  Triggers the paint method suitable to the bondorder of the given bond.
	 *
	 *@param  bond       The Bond to be drawn.
	 *@param  bondColor  Description of the Parameter
	 */
	private void paintBond(Bond bond, Color bondColor, Graphics graphics) {
		if (bond.getAtomAt(0).getPoint2D() == null || bond.getAtomAt(1).getPoint2D() == null) {
			return;
		}

        if (bond.getStereo() != CDKConstants.STEREO_BOND_UNDEFINED) {
            // Draw stero information if available
            logger.info("Painting wedge bond");
            if (bond.getStereo() >= CDKConstants.STEREO_BOND_UP) {
                paintWedgeBond(bond, bondColor, graphics);
            } else {
                logger.info("Painting it dashed");
                paintDashedWedgeBond(bond, bondColor, graphics);
            }
        } else {
            // Draw bond order when no stereo info is available
            if (bond.getOrder() == CDKConstants.BONDORDER_SINGLE) {
                paintSingleBond(bond, bondColor, graphics);
            } else if (bond.getOrder() == CDKConstants.BONDORDER_DOUBLE) {
                paintDoubleBond(bond, bondColor, graphics);
            } else if (bond.getOrder() == CDKConstants.BONDORDER_TRIPLE) {
                paintTripleBond(bond, bondColor, graphics);
            }
		}
	}


	/**
	 *  Triggers the paint method suitable to the bondorder of the given bond that
	 *  is part of a ring.
	 *
	 *@param  bond       The Bond to be drawn.
	 *@param  ring       Description of the Parameter
	 *@param  bondColor  Description of the Parameter
	 */
	private void paintRingBond(Bond bond, Ring ring, Color bondColor, Graphics graphics)
	{
		if (bond.getOrder() == 1)
		{
			paintSingleBond(bond, bondColor, graphics);
		} else if (bond.getOrder() == 2)
		{
			paintSingleBond(bond, bondColor, graphics);
			paintInnerBond(bond, ring, bondColor, graphics);
		} else if (bond.getOrder() == 1.5 || bond.flags[CDKConstants.ISAROMATIC])
		{
			paintSingleBond(bond, bondColor, graphics);
			paintInnerBond(bond, ring, Color.lightGray, graphics);
		} else if (bond.getOrder() == 3)
		{
			paintTripleBond(bond, bondColor, graphics);
		}
	}


	/**
	 *  Paints the given singlebond.
	 *
	 *@param  bond       The singlebond to be drawn
	 *@param  bondColor  Description of the Parameter
	 */
	private void paintSingleBond(Bond bond, Color bondColor, Graphics graphics) {
        if (GeometryTools.has2DCoordinates(bond)) { 
            paintOneBond(GeometryTools.getBondCoordinates(bond), bondColor, graphics);
        }
	}

	/**
	 *  Paints The given doublebond.
	 *
	 *@param  bond       The doublebond to be drawn
	 *@param  bondColor  Description of the Parameter
	 */
	private void paintDoubleBond(Bond bond, Color bondColor, Graphics graphics)
	{
		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond), r2dm.getBondDistance() / 2);

		int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
		paintOneBond(newCoords1, bondColor, graphics);

		int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
		paintOneBond(newCoords2, bondColor, graphics);
	}

	/**
	 *  Paints the given triplebond.
	 *
	 *@param  bond       The triplebond to be drawn
	 *@param  bondColor  Description of the Parameter
	 */
	private void paintTripleBond(Bond bond, Color bondColor, Graphics graphics)
	{
		paintSingleBond(bond, bondColor, graphics);

		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond), (r2dm.getBondWidth() / 2 + r2dm.getBondDistance()));

		int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
		paintOneBond(newCoords1, bondColor, graphics);

		int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
		paintOneBond(newCoords2, bondColor, graphics);
	}


	/**
	 *  Paints the inner bond of a doublebond that is part of a ring.
	 *
	 *@param  bond       The bond to be drawn
	 *@param  ring       The ring the bond is part of
	 *@param  bondColor  Description of the Parameter
	 */
	private void paintInnerBond(Bond bond, Ring ring, Color bondColor, Graphics graphics)
	{
		Point2d center = ring.get2DCenter();

		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond), (r2dm.getBondWidth() / 2 + r2dm.getBondDistance()));
		double dist1 = Math.sqrt(Math.pow((coords[0] - center.x), 2) + Math.pow((coords[1] - center.y), 2));
		double dist2 = Math.sqrt(Math.pow((coords[2] - center.x), 2) + Math.pow((coords[3] - center.y), 2));
		if (dist1 < dist2)
		{
			int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
			paintOneBond(shortenBond(newCoords1, ring.getRingSize()), bondColor, graphics);
		} else
		{
			int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
			paintOneBond(shortenBond(newCoords2, ring.getRingSize()), bondColor, graphics);
		}
	}


	/**
	 *  Calculates the coordinates for the inner bond of a doublebond that is part
	 *  of a ring. It is drawn shorter than a normal bond.
	 *
	 *@param  coords  The original coordinates of the bond
	 *@param  edges   Number of edges of the ring it is part of
	 *@return         The calculated coordinates of the now shorter bond
	 */
	private int[] shortenBond(int[] coords, int edges)
	{
		int xDiff = (coords[0] - coords[2]) / (edges * 2);
		int yDiff = (coords[1] - coords[3]) / (edges * 2);
		int[] newCoords = {coords[0] - xDiff, coords[1] - yDiff, coords[2] + xDiff, coords[3] + yDiff};
		return newCoords;
	}


	/**
	 *  Really paints the bond. It is triggered by all the other paintbond methods
	 *  to draw a polygon as wide as bondwidth.
	 *
	 *@param  coords
	 *@param  bondColor  Description of the Parameter
	 */
	private void paintOneBond(int[] coords, Color bondColor, Graphics graphics)
	{
	    graphics.setColor(bondColor);
		int[] newCoords = GeometryTools.distanceCalculator(coords, r2dm.getBondWidth() / 2);
		int[] xCoords = {newCoords[0], newCoords[2], newCoords[4], newCoords[6]};
		int[] yCoords = {newCoords[1], newCoords[3], newCoords[5], newCoords[7]};
        xCoords = getScreenCoordinates(xCoords);
        yCoords = getScreenCoordinates(yCoords);
	    graphics.fillPolygon(xCoords, yCoords, 4);
	}

	/**
	 *  Paints the given bond as a wedge bond.
	 *
	 *@param  bond       The singlebond to be drawn
	 *@param  bondColor  Description of the Parameter
	 */
	void paintWedgeBond(Bond bond, Color bondColor, Graphics graphics)
	{
        double wedgeWidth = r2dm.getBondWidth() * 2.0; // this value should be made customazible
        
        int[] coords = GeometryTools.getBondCoordinates(bond);
        graphics.setColor(bondColor);
        int[] newCoords = GeometryTools.distanceCalculator(coords, wedgeWidth);
        if (bond.getStereo() == CDKConstants.STEREO_BOND_UP) {
            int[] xCoords = {coords[0], newCoords[6], newCoords[4]};
            int[] yCoords = {coords[1], newCoords[7], newCoords[5]};
            xCoords = getScreenCoordinates(xCoords);
            yCoords = getScreenCoordinates(yCoords);
            graphics.fillPolygon(xCoords, yCoords, 3);
        } else {
            int[] xCoords = {coords[2], newCoords[0], newCoords[2]};
            int[] yCoords = {coords[3], newCoords[1], newCoords[3]};
            xCoords = getScreenCoordinates(xCoords);
            yCoords = getScreenCoordinates(yCoords);
            graphics.fillPolygon(xCoords, yCoords, 3);
        }
	}

	/**
	 *  Paints the given bond as a dashed wedge bond.
	 *
	 *@param  bond       The singlebond to be drawn
	 *@param  bondColor  Description of the Parameter
	 */
	void paintDashedWedgeBond(Bond bond, Color bondColor, Graphics graphics)
	{
        logger.debug("Drawing dashed wedge bond");
        
	    graphics.setColor(bondColor);

		double bondLength = bond.getLength();
		int numberOfLines = (int)(bondLength / 4.0);  // this value should be made customizable
        double wedgeWidth = r2dm.getBondWidth() * 2.0; // this value should be made customazible

		double widthStep = wedgeWidth/(double)numberOfLines;
        Point2d p1 = bond.getAtomAt(0).getPoint2D();
        Point2d p2 = bond.getAtomAt(1).getPoint2D();
        if (bond.getStereo() == CDKConstants.STEREO_BOND_DOWN_INV) {
            // draw the wedge bond the other way around
            p1 = bond.getAtomAt(1).getPoint2D();
            p2 = bond.getAtomAt(0).getPoint2D();
        }
		Vector2d lengthStep = new Vector2d(p2);
		lengthStep.sub(p1);
		lengthStep.scale(1.0/numberOfLines);
		Vector2d p = GeometryTools.calculatePerpendicularUnitVector(p1, p2);

		Point2d currentPoint = new Point2d(p1);
		Point2d q1 = new Point2d();
		Point2d q2 = new Point2d();
		for (int i=0; i <= numberOfLines; ++i) {
			Vector2d offset = new Vector2d(p);
			offset.scale(i*widthStep);
			q1.add(currentPoint, offset);
			q2.sub(currentPoint, offset);
            int[] lineCoords = {(int)q1.x, (int)q1.y, (int)q2.x, (int)q2.y};
            lineCoords = getScreenCoordinates(lineCoords);
		    graphics.drawLine(lineCoords[0], lineCoords[1], lineCoords[2], lineCoords[3]);
			currentPoint.add(lengthStep);
		}
	}

	/**
	 *  Paints a line between the startpoint and endpoint of the pointervector that
	 *  is stored in the Renderer2DModel.
	 */
	private void paintPointerVector(Graphics graphics)
	{
		Point startPoint = r2dm.getPointerVectorStart();
		Point endPoint = r2dm.getPointerVectorEnd();
		int[] points = {startPoint.x, startPoint.y, endPoint.x, endPoint.y};
		int[] newCoords = GeometryTools.distanceCalculator(points, r2dm.getBondWidth() / 2);
		int[] xCoords = {newCoords[0], newCoords[2], newCoords[4], newCoords[6]};
		int[] yCoords = {newCoords[1], newCoords[3], newCoords[5], newCoords[7]};
	    graphics.setColor(r2dm.getForeColor());
        // apply zoomFactor
        xCoords = getScreenCoordinates(xCoords);
        yCoords = getScreenCoordinates(yCoords);
	    graphics.fillPolygon(xCoords, yCoords, 4);
	}

	/**
	 *  Returns the Renderer2DModel of this Renderer
	 *
	 *@return    the Renderer2DModel of this Renderer
	 */
	public Renderer2DModel getRenderer2DModel()
	{
		return this.r2dm;
	}



	/**
	 *  Sets the Renderer2DModel of this Renderer
	 *
	 *@param  r2dm  the new Renderer2DModel for this Renderer
	 */
	public void setRenderer2DModel(Renderer2DModel r2dm)
	{
		this.r2dm = r2dm;
	}

    private Point getScreenCoordinates(Point p) {
        Point screenCoordinate = new Point();
        double zoomFactor = r2dm.getZoomFactor();
        screenCoordinate.x = (int)((double)p.x * zoomFactor);
        screenCoordinate.y = (int)((double)p.y * zoomFactor);
        return screenCoordinate;
    }

    private int[] getScreenCoordinates(int[] coords) {
        int[] screenCoordinates = new int[coords.length];
        double zoomFactor = r2dm.getZoomFactor();
        for (int i=0; i<coords.length; i++) {
            screenCoordinates[i] = (int)((double)coords[i] * zoomFactor);
        }
        return screenCoordinates;
    }
    
    private int getScreenSize(int size) {
        return (int)((double)size * r2dm.getZoomFactor());
    }
}

