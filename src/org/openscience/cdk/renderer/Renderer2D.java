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
public class Renderer2D
{

    private LoggingTool logger;
    
	SSSRFinder sssrf = new SSSRFinder();

	/**
	 *  Description of the Field
	 */
	public Renderer2DModel r2dm;
	Graphics g;
	AtomContainer atomCon;


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


	/**
	 *  triggers the methods to make the molecule fit into the frame and to paint
	 *  it.
	 *
	 *@param  atomCon  Description of the Parameter
	 *@param  g        Description of the Parameter
	 */
	public void paintMolecule(AtomContainer atomCon, Graphics g)
	{
		this.g = g;
		this.atomCon = atomCon;
		RingSet ringSet = new RingSet();
		Vector molecules = null;
		try
		{
			molecules = ConnectivityChecker.partitionIntoMolecules(atomCon);
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
		for (int i = 0; i < molecules.size(); i++)
		{
			ringSet.add(sssrf.findSSSR((Molecule) molecules.elementAt(i)));
		}
		if (r2dm.getPointerVectorStart() != null && r2dm.getPointerVectorEnd() != null)
		{
			paintPointerVector();
		}
		paintBonds(atomCon, ringSet);
		paintAtoms(atomCon);
		if (r2dm.drawNumbers())
		{
			paintNumbers(atomCon.getAtoms(), atomCon.getAtomCount());
		}
		if (r2dm.getSelectRect() != null)
		{
			g.setColor(r2dm.getHighlightColor());
			g.drawPolygon(r2dm.getSelectRect());
		}
		paintLassoLines();
	}


	/**
	 *  Description of the Method
	 */
	private void paintLassoLines()
	{
		Vector points = r2dm.getLassoPoints();
		if (points.size() > 1)
		{
			Point point1 = (Point) points.elementAt(0);
			Point point2;
			for (int i = 1; i < points.size(); i++)
			{
				point2 = (Point) points.elementAt(i);
				g.drawLine(point1.x, point1.y, point2.x, point2.y);
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
	private void paintNumbers(Atom[] atoms, int number)
	{
		for (int i = 0; i < number; i++)
		{
			paintNumber(atoms[i]);
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
	private void paintNumber(Atom atom)
	{
		if (atom.getPoint2D() == null)
		{
			return;
		}
		FontMetrics fm = g.getFontMetrics();
		int fontSize = getScreenSize(g.getFont().getSize());
		int xSymbOffset = (new Integer(fm.stringWidth(atom.getSymbol()) / 2)).intValue();
		int ySymbOffset = (new Integer(fm.getAscent() / 2)).intValue();

		try
		{
			int i = atomCon.getAtomNumber(atom);
//			g.setColor(r2dm.getBackColor());
//			g.fillRect((int)(atom.getPoint2D().x - (xSymbOffset * 1.8)),(int)(atom.getPoint2D().y - (ySymbOffset * 0.8)),(int)fontSize,(int)fontSize);
			g.setColor(r2dm.getForeColor());
			g.drawString(new Integer(i + 1).toString(), (int) (atom.getPoint2D().x + (xSymbOffset)), (int) (atom.getPoint2D().y - (ySymbOffset)));
			g.setColor(r2dm.getBackColor());
			g.drawLine((int) atom.getPoint2D().x, (int) atom.getPoint2D().y, (int) atom.getPoint2D().x, (int) atom.getPoint2D().y);
		} catch (Exception exc)
		{

		}
	}


	/**
	 *  Searches through all the atoms in the given array of atoms, triggers the
	 *  paintColouredAtoms method if the atom has got a certain color and triggers
	 *  the paintAtomSymbol method if the symbol of the atom is not C.
	 *
	 *@param  atomCon  Description of the Parameter
	 */
	private void paintAtoms(AtomContainer atomCon)
	{
		Color atomColor;
		Atom atom;
		for (int i = 0; i < atomCon.getAtomCount(); i++) {
			atom = atomCon.getAtomAt(i);
            paintAtom(atom);
		}
	}

	private void paintAtom(Atom atom) {
        Color atomColor = (Color) r2dm.getColorHash().get(atom);
        if (atom == r2dm.getHighlightedAtom()) {
            atomColor = r2dm.getHighlightColor();
        }
        if (atomColor != null) {
            paintColouredAtom(atom, atomColor);
        } else {
            atomColor = r2dm.getBackColor();
        }
        if (!atom.getSymbol().equals("C")) {
            /*
             *  only show element for non-carbon atoms,
             *  unless (see below)...
             */
            paintAtomSymbol(atom, atomColor);
            paintAtomCharge(atom);
        } else if (atomCon.getBondCount(atom) == 0) {
            // ... unless carbon has no bonds
            paintAtomSymbol(atom, atomColor);
            paintAtomCharge(atom);
        } else if (r2dm.getKekuleStructure()) {
            // ... unless carbon must be drawn because in Kekule mode
            paintAtomSymbol(atom, atomColor);
        } else if (atom.getFormalCharge() != 0) {
            // ... unless carbon is charged
            paintAtomSymbol(atom, atomColor);
            paintAtomCharge(atom);
        }
	}

	/**
	 *  Paints a rectangle of the given color at the position of the given atom.
	 *  For example when the atom is highlighted.
	 *
	 *@param  atom   The atom to be drawn
	 *@param  color  The color of the atom to be drawn
	 */
	private void paintColouredAtom(Atom atom, Color color)
	{
		int atomRadius = r2dm.getAtomRadius();
		g.setColor(color);
        int[] coords = {(int) atom.getX2D() - (atomRadius / 2), 
                        (int) atom.getY2D() - (atomRadius / 2), 
                        atomRadius, atomRadius};
        coords = getScreenCoordinates(coords);
		g.fillRect(coords[0], coords[1], coords[2], coords[3]);
	}

	/**
	 *  Paints the given atom symbol. It first outputs some empty space using the
	 *  background color, slightly larger than the space that the symbol occupies.
	 *  The atom symbol is then printed into the empty space.
	 *
	 *@param  atom       The atom to be drawn
	 *@param  backColor  Description of the Parameter
	 */
	private void paintAtomSymbol(Atom atom, Color backColor) {
		if (atom.getPoint2D() == null) {
			return;
		}
        
		// but first determine symbol
		String symbol = atom.getSymbol();
		// if there are implicit hydrogens, add them to string to display
		int implicitHydrogen = atom.getHydrogenCount();
		if (implicitHydrogen > 0) {
			symbol = symbol + "H";
			if (implicitHydrogen > 1) {
				symbol = symbol + implicitHydrogen;
			}
		}
        
        // draw string:

        /* determine where to put the string, as seen from the atom coordinates
           in model coordinates */
		FontMetrics fm = g.getFontMetrics();        
		int xSymbOffset = (new Integer(fm.stringWidth(symbol.substring(0,1)) / 2)).intValue();
		int ySymbOffset = (new Integer(fm.getAscent() / 2)).intValue();

		// make empty space
		g.setColor(backColor);
        Rectangle2D stringBounds = fm.getStringBounds(symbol, g);
        int[] coords = {(int) (atom.getPoint2D().x - (xSymbOffset * 1.2)), 
                        (int) (atom.getPoint2D().y - (ySymbOffset * 1.2)),
                        (int) (stringBounds.getWidth() * 1.4), 
                        (int) (stringBounds.getHeight() * 1.4) };
        coords = getScreenCoordinates(coords);                
		g.fillRect(coords[0], coords[1], coords[2], coords[3]);

        int[] hCoords = {(int) (atom.getPoint2D().x - xSymbOffset),
				         (int) (atom.getPoint2D().y + ySymbOffset) };
        hCoords = getScreenCoordinates(hCoords);
		g.setColor(r2dm.getForeColor());
        // apply zoom factor to font size
        Font unscaledFont = g.getFont();
        int fontSize = getScreenSize(unscaledFont.getSize());
        g.setFont(unscaledFont.deriveFont((float)fontSize));        
		g.drawString(symbol, hCoords[0], hCoords[1]);
        g.setFont(unscaledFont);
	}

	/**
	 *  Paints the given atom symbol. It first outputs some empty space using the
	 *  background color, slightly larger than the space that the symbol occupies.
	 *  The atom symbol is then printed into the empty space.
	 *
	 *@param  atom       The atom to be drawn
	 *@param  backColor  Description of the Parameter
	 */
	private void paintAtomCharge(Atom atom) {
        FontMetrics fm = g.getFontMetrics();
        int xSymbOffset = (new Integer(fm.stringWidth(atom.getSymbol()) / 2)).intValue();
        int ySymbOffset = (new Integer(fm.getAscent() / 2)).intValue();

            // show formal charge
        if (atom.getFormalCharge() != 0) {
            // print charge in smaller font size
            Font unscaledFont = g.getFont();
            int fontSize = getScreenSize(unscaledFont.getSize() - 1);
            g.setFont(unscaledFont.deriveFont((float)fontSize));        

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
            g.drawString(chargeString, hCoords[0], hCoords[1]);
            
            /** Put circles around + or - sign
            Rectangle2D stringBounds = fm.getStringBounds(chargeString, g);
            int width = (int)stringBounds.getWidth();
            int height = (int)stringBounds.getHeight();
            int[] coords = {(int)atom.getX2D() + xSymbOffset - (width/2), 
                            (int)atom.getY2D() - ySymbOffset - (height/2),
                            (int)stringBounds.getWidth(), 
                            (int)stringBounds.getWidth()};
            coords = getScreenCoordinates(coords);
            g.drawOval(coords[0], coords[1], coords[2], coords[3]); */
            g.setFont(unscaledFont);
        }
    }
    
    
	/**
	 *  Triggers the suitable method to paint each of the given bonds and selects
	 *  the right color.
	 *
	 *@param  ringSet  The set of rings the molecule contains
	 *@param  atomCon  Description of the Parameter
	 */
	private void paintBonds(AtomContainer atomCon, RingSet ringSet)
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
                        paintColouredAtom(bond.getAtomAt(j), bondColor);
                    }
                }
                ring = ringSet.getHeaviestRing(bond);
                if (ring != null)
                {
                    paintRingBond(bond, ring, bondColor);

                } else
                {
                    paintBond(bond, bondColor);
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
	private void paintBond(Bond bond, Color bondColor)
	{
		logger.debug("bond order: " + bond.getOrder());
		logger.debug("bond stereo: " + bond.getStereo());

		if (bond.getAtomAt(0).getPoint2D() == null || bond.getAtomAt(1).getPoint2D() == null)
		{
			return;
		}

        if (bond.getStereo() != CDKConstants.STEREO_BOND_UNDEFINED) {
            // Draw stero information if available
            logger.info("Painting wedge bond");
            if (bond.getStereo() >= CDKConstants.STEREO_BOND_UP) {
                paintWedgeBond(bond, bondColor);
            } else {
                logger.info("Painting it dashed");
                paintDashedWedgeBond(bond, bondColor);
            }
        } else {
            // Draw bond order when no stereo info is available
            if (bond.getOrder() == CDKConstants.BONDORDER_SINGLE) {
                paintSingleBond(bond, bondColor);
            } else if (bond.getOrder() == CDKConstants.BONDORDER_DOUBLE) {
                paintDoubleBond(bond, bondColor);
            } else if (bond.getOrder() == CDKConstants.BONDORDER_TRIPLE) {
                paintTripleBond(bond, bondColor);
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
	private void paintRingBond(Bond bond, Ring ring, Color bondColor)
	{
		if (bond.getOrder() == 1)
		{
			paintSingleBond(bond, bondColor);
		} else if (bond.getOrder() == 2)
		{
			paintSingleBond(bond, bondColor);
			paintInnerBond(bond, ring, bondColor);
		} else if (bond.getOrder() == 1.5 || bond.flags[CDKConstants.ISAROMATIC])
		{
			paintSingleBond(bond, bondColor);
			paintInnerBond(bond, ring, Color.lightGray);
		} else if (bond.getOrder() == 3)
		{
			paintTripleBond(bond, bondColor);
		}
	}


	/**
	 *  Paints the given singlebond.
	 *
	 *@param  bond       The singlebond to be drawn
	 *@param  bondColor  Description of the Parameter
	 */
	private void paintSingleBond(Bond bond, Color bondColor)
	{
		paintOneBond(GeometryTools.getBondCoordinates(bond), bondColor);

	}

	/**
	 *  Paints The given doublebond.
	 *
	 *@param  bond       The doublebond to be drawn
	 *@param  bondColor  Description of the Parameter
	 */
	private void paintDoubleBond(Bond bond, Color bondColor)
	{
		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond), r2dm.getBondDistance() / 2);

		int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
		paintOneBond(newCoords1, bondColor);

		int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
		paintOneBond(newCoords2, bondColor);
	}

	/**
	 *  Paints the given triplebond.
	 *
	 *@param  bond       The triplebond to be drawn
	 *@param  bondColor  Description of the Parameter
	 */
	private void paintTripleBond(Bond bond, Color bondColor)
	{
		paintSingleBond(bond, bondColor);

		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond), (r2dm.getBondWidth() / 2 + r2dm.getBondDistance()));

		int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
		paintOneBond(newCoords1, bondColor);

		int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
		paintOneBond(newCoords2, bondColor);
	}


	/**
	 *  Paints the inner bond of a doublebond that is part of a ring.
	 *
	 *@param  bond       The bond to be drawn
	 *@param  ring       The ring the bond is part of
	 *@param  bondColor  Description of the Parameter
	 */
	private void paintInnerBond(Bond bond, Ring ring, Color bondColor)
	{
		Point2d center = ring.get2DCenter();

		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond), (r2dm.getBondWidth() / 2 + r2dm.getBondDistance()));
		double dist1 = Math.sqrt(Math.pow((coords[0] - center.x), 2) + Math.pow((coords[1] - center.y), 2));
		double dist2 = Math.sqrt(Math.pow((coords[2] - center.x), 2) + Math.pow((coords[3] - center.y), 2));
		if (dist1 < dist2)
		{
			int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
			paintOneBond(shortenBond(newCoords1, ring.getRingSize()), bondColor);
		} else
		{
			int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
			paintOneBond(shortenBond(newCoords2, ring.getRingSize()), bondColor);
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
	private void paintOneBond(int[] coords, Color bondColor)
	{
		g.setColor(bondColor);
		int[] newCoords = GeometryTools.distanceCalculator(coords, r2dm.getBondWidth() / 2);
		int[] xCoords = {newCoords[0], newCoords[2], newCoords[4], newCoords[6]};
		int[] yCoords = {newCoords[1], newCoords[3], newCoords[5], newCoords[7]};
        xCoords = getScreenCoordinates(xCoords);
        yCoords = getScreenCoordinates(yCoords);
		g.fillPolygon(xCoords, yCoords, 4);
	}

	/**
	 *  Paints the given bond as a wedge bond.
	 *
	 *@param  bond       The singlebond to be drawn
	 *@param  bondColor  Description of the Parameter
	 */
	void paintWedgeBond(Bond bond, Color bondColor)
	{
        double wedgeWidth = r2dm.getBondWidth() * 2.0; // this value should be made customazible
        
        int[] coords = GeometryTools.getBondCoordinates(bond);
		g.setColor(bondColor);
		int[] newCoords = GeometryTools.distanceCalculator(coords, wedgeWidth);
        if (bond.getStereo() == CDKConstants.STEREO_BOND_UP) {
            int[] xCoords = {coords[0], newCoords[6], newCoords[4]};
            int[] yCoords = {coords[1], newCoords[7], newCoords[5]};
            xCoords = getScreenCoordinates(xCoords);
            yCoords = getScreenCoordinates(xCoords);
            g.fillPolygon(xCoords, yCoords, 3);
        } else {
            int[] xCoords = {coords[2], newCoords[0], newCoords[2]};
            int[] yCoords = {coords[3], newCoords[1], newCoords[3]};
            xCoords = getScreenCoordinates(xCoords);
            yCoords = getScreenCoordinates(xCoords);
            g.fillPolygon(xCoords, yCoords, 3);
        }
	}

	/**
	 *  Paints the given bond as a dashed wedge bond.
	 *
	 *@param  bond       The singlebond to be drawn
	 *@param  bondColor  Description of the Parameter
	 */
	void paintDashedWedgeBond(Bond bond, Color bondColor)
	{
        logger.debug("Drawing dashed wedge bond");
        
        int[] coords = GeometryTools.getBondCoordinates(bond);
		g.setColor(bondColor);

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
			g.drawLine(lineCoords[0], lineCoords[1], lineCoords[2], lineCoords[3]);
			currentPoint.add(lengthStep);
		}
	}

	/**
	 *  Paints a line between the startpoint and endpoint of the pointervector that
	 *  is stored in the Renderer2DModel.
	 */
	private void paintPointerVector()
	{
		Point startPoint = r2dm.getPointerVectorStart();
		Point endPoint = r2dm.getPointerVectorEnd();
		int[] points = {startPoint.x, startPoint.y, endPoint.x, endPoint.y};
		int[] newCoords = GeometryTools.distanceCalculator(points, r2dm.getBondWidth() / 2);
		int[] xCoords = {newCoords[0], newCoords[2], newCoords[4], newCoords[6]};
		int[] yCoords = {newCoords[1], newCoords[3], newCoords[5], newCoords[7]};
		g.setColor(r2dm.getForeColor());
        // apply zoomFactor
        xCoords = getScreenCoordinates(xCoords);
        yCoords = getScreenCoordinates(yCoords);
		g.fillPolygon(xCoords, yCoords, 4);
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

    public Point getScreenCoordinates(Point p) {
        Point screenCoordinate = new Point();
        double zoomFactor = r2dm.getZoomFactor();
        screenCoordinate.x = (int)((double)p.x * zoomFactor);
        screenCoordinate.y = (int)((double)p.y * zoomFactor);
        return screenCoordinate;
    }

    public int[] getScreenCoordinates(int[] coords) {
        int[] screenCoordinates = new int[coords.length];
        double zoomFactor = r2dm.getZoomFactor();
        for (int i=0; i<coords.length; i++) {
            screenCoordinates[i] = (int)((double)coords[i] * zoomFactor);
        }
        return screenCoordinates;
    }
    
    public int getScreenSize(int size) {
        return (int)((double)size * r2dm.getZoomFactor());
    }
}

