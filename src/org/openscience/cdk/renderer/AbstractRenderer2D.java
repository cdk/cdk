/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;
import org.openscience.cdk.validate.ProblemMarker;

/**
 *  A Renderer class which draws 2D representations of molecules onto a given
 *  graphics objects using information from a Renderer2DModel.
 *
 *  <p>This renderer uses two coordinate systems. One that is a world coordinates
 *  system which is generated from the document coordinates. Additionally, the
 *  screen coordinates make up the second system, and are calculated by applying
 *  a zoom factor to the world coordinates.
 *
 *  <p>The coordinate system used for display has its origin in the left-bottom
 *  corner, with the x axis to the right, and the y axis towards the top of the
 *  screen. The system is thus right handed.
 *
 *  <p>The two main methods are paintMolecule() and paintChemModel(). Others might
 *  not show full rendering, e.g. anti-aliasing.
 *
 *  <p>This modules tries to adhere to guidelines being developed by the IUPAC
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
abstract class AbstractRenderer2D implements MouseMotionListener
{

	final static BasicStroke stroke = new BasicStroke(1.0f);

	protected LoggingTool logger;
	boolean debug = true;
	IsotopeFactory isotopeFactory;
	int[] tooltiparea = null;

	protected Renderer2DModel r2dm;

	int graphicsHeight;
    
    public boolean useScreenSize=true;


	/**
	 *  Constructs a Renderer2D with a default settings model.
	 */
	AbstractRenderer2D()
	{
		this(new Renderer2DModel());
	}


	/**
	 *  Constructs a Renderer2D.
	 *
	 *@param  r2dm  The settings model to use for rendering.
	 */
	AbstractRenderer2D(Renderer2DModel r2dm)
	{
		this.r2dm = r2dm;
		logger = new LoggingTool(this);

	}
	
	protected void setupIsotopeFactory(IChemObject object) {
		if (isotopeFactory == null) {
			try {
				isotopeFactory = IsotopeFactory.getInstance(object.getBuilder());
			} catch (Exception exception) {
				logger.error("Error while instantiating IsotopeFactory");
				logger.warn("Will not be able to display undefault isotopes");
				logger.debug(exception);
			}
		}
	}
	
	/**
	 *  Description of the Method
	 *
	 *@param  graphics  Description of the Parameter
	 */
	protected void customizeRendering(Graphics2D graphics)
	{
		if (r2dm.getUseAntiAliasing())
		{
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		graphics.setStroke(stroke);
	}

	/**
	 *@param  minmax    array of length for with min and max 2D coordinates
	 *@param  caption   Description of the Parameter
	 *@param  side      Description of the Parameter
	 *@param  graphics  Description of the Parameter
	 */
	public void paintBoundingBox(double[] minmax, String caption,
			int side, Graphics2D graphics)
	{
		int[] ints = new int[4];
		ints[0] = (int) minmax[0] - side;
		// min x
		ints[1] = (int) minmax[1] - side;
		// min y
		ints[2] = (int) minmax[2] + side;
		// max x
		ints[3] = (int) minmax[3] + side;
		// max y
		int[] screenCoords = getScreenCoordinates(ints);
		int heigth = screenCoords[1] - screenCoords[3];
		int width = screenCoords[2] - screenCoords[0];
		graphics.drawRect((int) screenCoords[0], (int) screenCoords[3], width, heigth);

		// draw reaction ID
		Font unscaledFont = r2dm.getFont();
		if (unscaledFont == null)
		{
			unscaledFont = graphics.getFont();
		}
		float fontSize = getScreenSize(unscaledFont.getSize());
		graphics.setFont(unscaledFont.deriveFont(fontSize));
		graphics.drawString(caption, (int) screenCoords[0], (int) screenCoords[3]);
		graphics.setFont(unscaledFont);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  graphics  Description of the Parameter
	 */
	public void paintLassoLines(Graphics2D graphics)
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
	 *  Searches through all the atoms in the given array of atoms, triggers the
	 *  paintColouredAtoms method if the atom has got a certain color and triggers
	 *  the paintAtomSymbol method if the symbol of the atom is not C.
	 *
	 *@param  atomCon   Description of the Parameter
	 *@param  graphics  Description of the Parameter
	 */
	public void paintAtoms(IAtomContainer atomCon, Graphics2D graphics)
	{
		IAtom[] atoms = atomCon.getAtoms();
		for (int i = 0; i < atoms.length; i++)
		{
			paintAtom(atomCon, atoms[i], graphics);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  container  Description of the Parameter
	 *@param  atom       Description of the Parameter
	 *@param  graphics   Description of the Parameter
	 */
	public void paintAtom(IAtomContainer container, IAtom atom, Graphics2D graphics)
	{
		logger.debug("Painting atom ");
		Color atomBackColor = r2dm.getAtomBackgroundColor(atom);
		if (atom.equals(r2dm.getHighlightedAtom()))
		{
			paintColouredAtomBackground(atom, r2dm.getHoverOverColor(), graphics);
		}
		if((r2dm.getSelectedPart()!=null && (r2dm.getSelectedPart().contains(r2dm.getHighlightedAtom()) || r2dm.getSelectedPart().contains(r2dm.getHighlightedBond())) && r2dm.getSelectedPart().contains(atom)) || (r2dm.getSelectedPart()!=null && r2dm.getSelectedPart().getAtomCount()==1 && r2dm.getSelectedPart().getAtomAt(0)==atom)){
			paintColouredAtomBackground(atom, r2dm.getSelectedPartColor(), graphics);
		}	
		if(r2dm.getExternalSelectedPart()!=null && r2dm.getExternalSelectedPart().contains(atom)){
			paintColouredAtomBackground(atom, r2dm.getExternalHighlightColor(), graphics);
		}	
		if(r2dm.getMerge().get(atom)!=null || r2dm.getMerge().values().contains(atom)){
			paintColouredAtomBackground(atom, r2dm.getHoverOverColor(),graphics);
		}

		int alignment = GeometryTools.getBestAlignmentForLabel(container, atom);
		boolean drawSymbol = false;
		boolean isRadical = (container.getSingleElectronSum(atom) > 0);
		if (atom instanceof IPseudoAtom)
		{
			drawSymbol = false;
			paintPseudoAtomLabel((IPseudoAtom) atom, atomBackColor, graphics,
					alignment, isRadical);
			return;
		} else if (!atom.getSymbol().equals("C"))
		{
			/*
			 *  only show element for non-carbon atoms,
			 *  unless (see below)...
			 */
			drawSymbol = true;
		} else if (r2dm.getKekuleStructure())
		{
			// ... unless carbon must be drawn because in Kekule mode
			drawSymbol = true;
		} else if (atom.getFormalCharge() != 0)
		{
			// ... unless carbon is charged
			drawSymbol = true;
		} else if (container.getConnectedBonds(atom).length < 1)
		{
			// ... unless carbon is unbonded
			drawSymbol = true;
		} else if (r2dm.getShowEndCarbons() && (container.getConnectedBonds(atom).length == 1))
		{
			drawSymbol = true;
		} else if (atom.getProperty(ProblemMarker.ERROR_MARKER) != null)
		{
			// ... unless carbon is unbonded
			drawSymbol = true;
		} else if (atom.getMassNumber() != 0)
		{
			try
			{
				if (atom.getMassNumber() != IsotopeFactory.getInstance(container.getBuilder()).
						getMajorIsotope(atom.getSymbol()).getMassNumber())
				{
					drawSymbol = true;
				}
			} catch (Exception exception)
			{
			}
			;
		}
		if (r2dm.drawNumbers())
		{
			drawSymbol = true;
		}
		if (drawSymbol || isRadical)
		{
			paintAtomSymbol(atom, atomBackColor, graphics, alignment,
			atom.getProperty("OriginalNumber")!=null ? ((Integer)atom.getProperty("OriginalNumber")).intValue()+1 : container.getAtomNumber(atom) + 1, isRadical);
		}
		if (r2dm.getShowTooltip() && (atom == r2dm.getHighlightedAtom() ||( r2dm.getExternalSelectedPart()!=null &&  r2dm.getExternalSelectedPart().contains(atom))) && r2dm.getToolTipText(atom) != null)
		{
			paintToolTip(atom, graphics, container.getAtomNumber(atom) + 1);
		}
	}


	/**
	 *  Paints a rectangle of the given color at the position of the given atom.
	 *  For example when the atom is highlighted.
	 *
	 *@param  atom      The atom to be drawn
	 *@param  color     The color of the atom to be drawn
	 *@param  graphics  Description of the Parameter
	 */
	public void paintColouredAtomBackground(org.openscience.cdk.interfaces.IAtom atom, Color color, Graphics2D graphics)
	{
		if(r2dm.getRenderingCoordinate(atom)==null)
			return;
		int atomRadius = r2dm.getAtomRadius();
		graphics.setColor(color);
		int[] coords = {(int) r2dm.getRenderingCoordinate(atom).x - (atomRadius / 2),
				(int) r2dm.getRenderingCoordinate(atom).y + (atomRadius / 2)};
		int radius = (int) getScreenSize(atomRadius);
		coords = getScreenCoordinates(coords);
    if(r2dm.getIsCompact())
      graphics.drawRect(coords[0], coords[1], radius, radius);
    else
      graphics.fillRect(coords[0], coords[1], radius, radius);
	}


	/**
	 *  Paints the given atom symbol. It first outputs some empty space using the
	 *  background color, slightly larger than the space that the symbol occupies.
	 *  The atom symbol is then printed into the empty space. <p>
	 *
	 *  The algorithm uses four steps:
	 *  <ol>
	 *    <li> it calculates the widths and heights of all label parts
	 *    <li> it calculates the x's and y's of all label parts
	 *    <li> it creates empty backgrounds for all label parts
	 *    <li> it draws all label parts
	 *  </ol>
	 *
	 *
	 *@param  atom        The atom to be drawn
	 *@param  backColor   Description of the Parameter
	 *@param  graphics    Graphics2D to draw too
	 *@param  alignment   How to align the H's
	 *@param  atomNumber  Number of the atom in the AtomContainer, 0 is not in
	 *      container
	 *@param  isRadical   Description of the Parameter
	 */
	public void paintAtomSymbol(IAtom atom, Color backColor, Graphics2D graphics,
			int alignment, int atomNumber, boolean isRadical)
	{
		if (r2dm.getRenderingCoordinate(atom) == null)
		{
			logger.warn("Cannot draw atom without 2D coordinate");
			return;
		}

		//This is for the compact version just with a square in the right color
		if (r2dm.getIsCompact())
		{
			if (!atom.getSymbol().equals("C"))
			{
				int labelX = (int) (r2dm.getRenderingCoordinate(atom).x - 2);
				int labelY = (int) (r2dm.getRenderingCoordinate(atom).y + 2);
				try{
					AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/jmol_atomtypes.txt",atom.getBuilder());
					factory.configure(atom);
				}catch(Exception ex){
					//We choose black if reading not possible
					logger.debug(ex);
				}
				Color atomColor = r2dm.getAtomColor(atom, Color.BLACK);
				paintEmptySpace(labelX, labelY, 5, 5, 0, atomColor, graphics);
			}
			return;
		}

		// The fonts for calculating geometries
		float subscriptFraction = 0.7f;
		Font normalFont = r2dm.getFont();
		if (normalFont == null)
		{
			normalFont = graphics.getFont();
		}
		int normalFontSize = normalFont.getSize();
		Font subscriptFont = normalFont.deriveFont(
				normalFontSize * subscriptFraction);
		// get drawing fonts
		float normalScreenFontSize = getScreenSize(normalFontSize);
		Font normalScreenFont = normalFont.deriveFont(normalScreenFontSize);
		Font subscriptScreenFont = normalScreenFont.deriveFont(
				normalScreenFontSize * subscriptFraction);

		// STEP 1: calculate widths and heights for all parts in the label

		// calculate SYMBOL width, height
		String atomSymbol = atom.getSymbol();
		if (r2dm.drawNumbers())
		{
			if (atomSymbol.equals("C"))
			{
				atomSymbol = "" + atomNumber;
			} else if (atomNumber != 0 && !atomSymbol.equals(""))
			{
				atomSymbol += "-" + atomNumber;
			}
		}
		if (isRadical)
		{
			logger.debug(" atom is radical, adding \u00B7");
			atomSymbol += "\u00B7";
		}
		graphics.setFont(normalFont);
		FontMetrics fm = graphics.getFontMetrics();
		int atomSymbolW = (new Integer(fm.stringWidth(atomSymbol))).intValue();
		int atomSymbolFirstCharW = (new Integer(fm.stringWidth(atomSymbol.substring(0, 1)))).intValue();
		int atomSymbolH = (new Integer(fm.getAscent())).intValue();
		int atomSymbolXOffset = atomSymbolFirstCharW / 2;
		int atomSymbolYOffset = atomSymbolH / 2;

		// calculate IMPLICIT H width, height
		int implicitHydrogenCount = atom.getHydrogenCount();
		int hSymbolW = 0;
		// unless next condition, this is the default
		int hSymbolH = 0;
		// unless next condition, this is the default
		String hSymbol = "H";
		String hMultiplierString = new Integer(implicitHydrogenCount).toString();
		if (implicitHydrogenCount > 0)
		{
			// fm is identical, don't change
			hSymbolW = (new Integer(fm.stringWidth(hSymbol))).intValue();
			hSymbolH = atomSymbolH;
		}
		graphics.setFont(subscriptFont);
		fm = graphics.getFontMetrics();
		int hMultiplierW = 0;
		int hMultiplierH = 0;
		if (implicitHydrogenCount > 1)
		{
			// fm is identical, don't change
			hMultiplierW = (new Integer(fm.stringWidth(hMultiplierString))).intValue();
			hMultiplierH = (new Integer(fm.getAscent())).intValue();
		}

		// calculate CHARGE width, height
		// font is still subscript, that's fine
		int formalCharge = atom.getFormalCharge();
		int formalChargeW = 0;
		// unless next condition, this is the default
		int formalChargeH = 0;
		String formalChargeString = "";
		// if charge == 0, then don't print anything
		if (formalCharge != 0)
		{
			if (formalCharge > 1)
			{
				formalChargeString = new Integer(formalCharge).toString() + "+";
			} else if (formalCharge > 0)
			{
				formalChargeString = "+";
			} else if (formalCharge < -1)
			{
				formalChargeString = new Integer(formalCharge * -1).toString() + "-";
			} else if (formalCharge < 0)
			{
				formalChargeString = "-";
			}
			graphics.setFont(subscriptFont);
			fm = graphics.getFontMetrics();
			formalChargeW = (new Integer(fm.stringWidth(formalChargeString))).intValue();
			formalChargeH = (new Integer(fm.getAscent())).intValue();
		}

		// calculate ISOTOPE width, height
		// font is still subscript, that's fine
		int atomicMassNumber = atom.getMassNumber();
		int isotopeW = 0;
		// unless next condition, this is the default
		int isotopeH = 0;
		String isotopeString = "";
		if (atomicMassNumber != 0 && isotopeFactory != null)
		{
			IIsotope majorIsotope = isotopeFactory.getMajorIsotope(atom.getSymbol());
			if (majorIsotope != null && atomicMassNumber != majorIsotope.getMassNumber())
			{
				graphics.setFont(subscriptFont);
				fm = graphics.getFontMetrics();
				isotopeString = new Integer(atomicMassNumber).toString();
				isotopeW = (new Integer(fm.stringWidth(isotopeString))).intValue();
				isotopeH = (new Integer(fm.getAscent())).intValue();
			}
		}

		// STEP 2: calculate x's and y's for all parts in the label

		int labelX = 0;
		int labelY = 0;
		if (alignment == 1)
		{
			// left alignment
			labelX = (int) (r2dm.getRenderingCoordinate(atom).x - (atomSymbolXOffset + isotopeW));
		} else
		{
			// right alignment
			labelX = (int) (r2dm.getRenderingCoordinate(atom).x -
					(atomSymbolXOffset + Math.max(isotopeW, hMultiplierW) + hSymbolW));
		}
		// labelY and labelH are the same for both left/right aligned
		labelY = (int) (r2dm.getRenderingCoordinate(atom).y + (atomSymbolYOffset + isotopeH));

		// xy for atom symbol
		int[] atomSymbolCoords = new int[2];
		if (alignment == 1)
		{
			// left alignment
			atomSymbolCoords[0] = labelX + isotopeW;
		} else
		{
			// right alignment
			atomSymbolCoords[0] = labelX + hSymbolW + Math.max(isotopeW, hMultiplierW);
		}
		atomSymbolCoords[1] = labelY - isotopeH - atomSymbolH;

		//Check if this is inside the tooltiptextarea
		int[] tipcoords = getScreenCoordinates(atomSymbolCoords);
		if (tooltiparea != null && tipcoords[0] > tooltiparea[0] && tipcoords[0] < tooltiparea[2] && tipcoords[1] > tooltiparea[1] && tipcoords[1] < tooltiparea[3])
		{
			return;
		}

		// xy for implicit hydrogens
		int[] hSymbolCoords = new int[2];
		if (alignment == 1)
		{
			// left alignment
			hSymbolCoords[0] = labelX + isotopeW + atomSymbolW;
		} else
		{
			// right alignment
			hSymbolCoords[0] = labelX;
		}
		hSymbolCoords[1] = labelY - isotopeH - atomSymbolH;
		// xy for implicit hydrogens multiplier
		int[] hMultiplierCoords = new int[2];
		if (alignment == 1)
		{
			// left alignment
			hMultiplierCoords[0] = labelX + isotopeW + atomSymbolW + hSymbolW;
		} else
		{
			// right alignment
			hMultiplierCoords[0] = labelX + hSymbolW;
		}
		hMultiplierCoords[1] = labelY - isotopeH - atomSymbolH - hMultiplierH / 2;

		// xy for charge
		int[] chargeCoords = new int[2];
		if (alignment == 1)
		{
			// left alignment
			chargeCoords[0] = labelX + isotopeW + atomSymbolW + hSymbolW;
		} else
		{
			// right alignment
			chargeCoords[0] = labelX + hSymbolW + Math.max(isotopeW, hMultiplierW) +
					atomSymbolW;
		}
		chargeCoords[1] = labelY - isotopeH;

		//xy for isotope
		int[] isotopeCoords = new int[2];
		if (alignment == 1)
		{
			// left alignment
			isotopeCoords[0] = labelX;
		} else
		{
			// right alignment
			isotopeCoords[0] = labelX + hSymbolW;
		}
		isotopeCoords[1] = labelY - isotopeH;

		// STEP 3: draw empty backgrounds for all parts in the label

		int border = 2;
		// border for clearing background in pixels

		paintEmptySpace(atomSymbolCoords[0], atomSymbolCoords[1] + atomSymbolH,
				atomSymbolW, atomSymbolH, border, backColor, graphics);
		paintEmptySpace(hSymbolCoords[0], hSymbolCoords[1] + hSymbolH,
				hSymbolW, hSymbolH, border, backColor, graphics);
		paintEmptySpace(hMultiplierCoords[0], hMultiplierCoords[1] + hMultiplierH,
				hMultiplierW, hMultiplierH, border, backColor, graphics);
		paintEmptySpace(chargeCoords[0], chargeCoords[1] + formalChargeH,
				formalChargeW, formalChargeH, border, backColor, graphics);
		paintEmptySpace(isotopeCoords[0], isotopeCoords[1] + isotopeH,
				isotopeW, isotopeH, border, backColor, graphics);

		// STEP 4: draw all parts in the label

		Color atomColor = r2dm.getAtomColor(atom, r2dm.getForeColor());
		{
			// draw SYMBOL

			int[] screenCoords = getScreenCoordinates(atomSymbolCoords);
			graphics.setColor(atomColor);
			graphics.setFont(normalScreenFont);
			graphics.drawString(atomSymbol, screenCoords[0], screenCoords[1]);

			// possibly underline SYMBOL
			if (atom.getProperty(ProblemMarker.ERROR_MARKER) != null ||
					atom.getProperty(ProblemMarker.WARNING_MARKER) != null)
			{
				// RED for error, ORANGE for warnings
				if (atom.getProperty(ProblemMarker.ERROR_MARKER) != null)
				{
					graphics.setColor(Color.red);
				} else if (atom.getProperty(ProblemMarker.WARNING_MARKER) != null)
				{
					graphics.setColor(Color.orange);
				}
				// make zig zag bond
				int symbolLength = atom.getSymbol().length();
				int zigzags = 1 + (2 * symbolLength);
				int spacing = atomSymbolW / zigzags;
				int width = atomSymbolH / 3;
				for (int i = -symbolLength; i <= symbolLength; i++)
				{
					int[] lineCoords = new int[6];
					int halfspacing = spacing / 2;
					lineCoords[0] = atomSymbolCoords[0] + (atomSymbolW / 2) + (i * spacing) - halfspacing;
					lineCoords[1] = atomSymbolCoords[1] - 1 * width;
					lineCoords[2] = lineCoords[0] + halfspacing;
					lineCoords[3] = atomSymbolCoords[1] - 2 * width;
					lineCoords[4] = lineCoords[2] + halfspacing;
					lineCoords[5] = lineCoords[1];
					int[] lineScreenCoords = getScreenCoordinates(lineCoords);
					graphics.drawLine(lineScreenCoords[0], lineScreenCoords[1],
							lineScreenCoords[2], lineScreenCoords[3]);
					graphics.drawLine(lineScreenCoords[2], lineScreenCoords[3],
							lineScreenCoords[4], lineScreenCoords[5]);
				}
			}
		}

		// draw IMPLICIT H's
		if (implicitHydrogenCount > 0 && r2dm.getShowImplicitHydrogens())
		{
			int[] screenCoords = getScreenCoordinates(hSymbolCoords);
			graphics.setColor(atomColor);
			graphics.setFont(normalScreenFont);
			graphics.drawString(hSymbol, screenCoords[0], screenCoords[1]);
			if (implicitHydrogenCount > 1)
			{
				// draw number of hydrogens
				screenCoords = getScreenCoordinates(hMultiplierCoords);
				graphics.setColor(atomColor);
				graphics.setFont(subscriptScreenFont);
				graphics.drawString(hMultiplierString, screenCoords[0], screenCoords[1]);
			}
		}

		// draw CHARGE
		if (formalCharge != 0)
		{
			int[] screenCoords = getScreenCoordinates(chargeCoords);
			graphics.setColor(atomColor);
			graphics.setFont(normalScreenFont);
			graphics.drawString(formalChargeString, screenCoords[0], screenCoords[1]);
		}

		// draw ISOTOPE
		if (isotopeString.length() > 0)
		{
			int[] screenCoords = getScreenCoordinates(isotopeCoords);
			graphics.setColor(atomColor);
			graphics.setFont(subscriptScreenFont);
			graphics.drawString(isotopeString, screenCoords[0], screenCoords[1]);
		}

		// reset old font & color
		graphics.setFont(normalFont);
		graphics.setColor(r2dm.getForeColor());
	}


	/**
	 *  Makes a clear empty space using the background color.
	 *
	 *@param  x          Description of the Parameter
	 *@param  y          Description of the Parameter
	 *@param  w          Description of the Parameter
	 *@param  h          Description of the Parameter
	 *@param  border     Description of the Parameter
	 *@param  backColor  Description of the Parameter
	 *@param  graphics   Description of the Parameter
	 */
	public void paintEmptySpace(int x, int y, int w, int h, int border,
			Color backColor, Graphics2D graphics)
	{
		if (w != 0 && h != 0)
		{
			Color saveColor = graphics.getColor();
			graphics.setColor(backColor);
			int[] coords = {x - border, y + border};
			int[] bounds = {(int) getScreenSize(w + 2 * border),
					(int) getScreenSize(h + 2 * border)};
			int[] screenCoords = getScreenCoordinates(coords);
			graphics.fillRect(screenCoords[0], screenCoords[1],
					bounds[0], bounds[1]);
			graphics.setColor(saveColor);
		}
		// else nothing to make empty
	}


	/**
	 *  Paints the label of the given PseudoAtom, instead of it's symbol.
	 *
	 *@param  atom       The atom to be drawn
	 *@param  backColor  Description of the Parameter
	 *@param  graphics   Description of the Parameter
	 *@param  alignment  Description of the Parameter
	 *@param  isRadical  Description of the Parameter
	 */
	public void paintPseudoAtomLabel(IPseudoAtom atom, Color backColor,
			Graphics2D graphics, int alignment, boolean isRadical)
	{
		if (r2dm.getRenderingCoordinate(atom) == null)
		{
			logger.warn("Cannot draw atom without 2D coordinate");
			return;
		}
		String atomSymbol = atom.getLabel();
		if (atomSymbol == null)
		{
			logger.warn("Cannot draw null symbol: taking symbol as default.");
			atomSymbol = atom.getSymbol();
		}
		if (isRadical)
		{
			logger.debug(" atom is radical, adding \u00B7");
			atomSymbol += "\u00B7";
		}

		// The calculation fonts
		Font normalFont = r2dm.getFont();
		if (normalFont == null)
		{
			normalFont = graphics.getFont();
		}
		int normalFontSize = normalFont.getSize();
		// get drawing fonts
		float normalScreenFontSize = getScreenSize(normalFontSize);
		Font normalScreenFont = normalFont.deriveFont(normalScreenFontSize);

		// calculate SYMBOL width, height
		graphics.setFont(normalFont);
		FontMetrics fm = graphics.getFontMetrics();
		int atomSymbolW = (new Integer(fm.stringWidth(atomSymbol))).intValue();
		int atomSymbolFirstCharW = (new Integer(fm.stringWidth(atomSymbol.substring(0, 1)))).intValue();
		int atomSymbolLastCharW = (new Integer(fm.stringWidth(atomSymbol.substring(atomSymbol.length() - 1)))).intValue();
		int atomSymbolH = (new Integer(fm.getAscent())).intValue();

		int labelX = 0;
		int labelY = 0;
		int labelW = atomSymbolW;
		int labelH = atomSymbolH;

		if (alignment == 1)
		{
			// left alignment
			labelX = (int) (r2dm.getRenderingCoordinate(atom).x - (atomSymbolFirstCharW / 2));
		} else
		{
			// right alignment
			labelX = (int) (r2dm.getRenderingCoordinate(atom).x - (atomSymbolW + atomSymbolLastCharW / 2));
		}
		// labelY and labelH are the same for both left/right aligned
		labelY = (int) (r2dm.getRenderingCoordinate(atom).y - (atomSymbolH / 2));
		{
			// make empty space

			int border = 2;
			// number of pixels
			graphics.setColor(backColor);
			int[] coords = {labelX - border, labelY + labelH + border};
			int[] bounds = {(int) getScreenSize(labelW + 2 * border),
					(int) getScreenSize(labelH + 2 * border)};
			int[] screenCoords = getScreenCoordinates(coords);
			graphics.fillRect(screenCoords[0], screenCoords[1],
					bounds[0], bounds[1]);
		}
		{
			// draw label

			int[] coords = {labelX, labelY};
			int[] screenCoords = getScreenCoordinates(coords);
			graphics.setColor(Color.black);
			graphics.setFont(normalScreenFont);
			graphics.drawString(atomSymbol, screenCoords[0], screenCoords[1]);
		}

		// reset old font & color
		graphics.setFont(normalFont);
		graphics.setColor(r2dm.getForeColor());
	}


	/**
	 *  Triggers the suitable method to paint each of the given bonds and selects
	 *  the right color.
	 *
	 *@param  ringSet   The set of rings the molecule contains
	 *@param  atomCon   Description of the Parameter
	 *@param  graphics  Description of the Parameter
	 */
	public void paintBonds(IAtomContainer atomCon, IRingSet ringSet, Graphics2D graphics)
	{
		Color bondColor;
		IRing ring;
		org.openscience.cdk.interfaces.IBond[] bonds = atomCon.getBonds();
		ArrayList painted_rings = new ArrayList();

		logger.debug("Painting bonds...");
		for (int i = 0; i < bonds.length; i++)
		{
			org.openscience.cdk.interfaces.IBond currentBond = bonds[i];
			bondColor = (Color) r2dm.getColorHash().get(currentBond);
			if (bondColor == null)
			{
				bondColor = r2dm.getForeColor();
			}
			if (currentBond == r2dm.getHighlightedBond() && (r2dm.getSelectedPart()==null || !r2dm.getSelectedPart().contains(currentBond)))
			{
				bondColor = r2dm.getHoverOverColor();
				for (int j = 0; j < currentBond.getAtomCount(); j++)
				{
					paintColouredAtomBackground(currentBond.getAtomAt(j),
							bondColor, graphics);
				}
			}
			ring = RingSetManipulator.getHeaviestRing(ringSet, currentBond);
			if (ring != null)
			{
				logger.debug("Found ring to draw");
				if (ringIsAromatic(ring) && r2dm.getShowAromaticity())
				{
					logger.debug("Ring is aromatic");
					if (r2dm.getShowAromaticityInCDKStyle())
					{
						paintAromaticRingBondCDKStyle(currentBond, ring, bondColor, graphics);
					} else
					{
						if (!painted_rings.contains(ring))
						{
							paintRingRing(ring, bondColor, graphics);
							painted_rings.add(ring);
						}
						paintSingleBond(currentBond, bondColor, graphics);
					}
				} else
				{
					logger.debug("Ring is *not* aromatic");
					paintRingBond(currentBond, ring, bondColor, graphics);
				}
			} else
			{
				logger.debug("Drawing a non-ring bond");
				paintBond(currentBond, bondColor, graphics);
			}
		}
	}


	/**
	 *  A ring is defined aromatic if all atoms are aromatic, -or- all bonds are
	 *  aromatic.
	 *
	 *@param  ring  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	public boolean ringIsAromatic(IRing ring)
	{
		boolean isAromatic = true;
		IAtom[] atoms = ring.getAtoms();
		for (int i = 0; i < atoms.length; i++)
		{
			if (!atoms[i].getFlag(CDKConstants.ISAROMATIC))
			{
				isAromatic = false;
			}
		}
		if (!isAromatic)
		{
			isAromatic = true;
			org.openscience.cdk.interfaces.IBond[] bonds = ring.getBonds();
			for (int i = 0; i < bonds.length; i++)
			{
				if (!bonds[i].getFlag(CDKConstants.ISAROMATIC))
				{
					return false;
				}
			}
		}
		return isAromatic;
	}


	/**
	 *  Triggers the paint method suitable to the bondorder of the given bond.
	 *
	 *@param  bond       The Bond to be drawn.
	 *@param  bondColor  Description of the Parameter
	 *@param  graphics   Description of the Parameter
	 */
	public void paintBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		if (r2dm.getRenderingCoordinate(bond.getAtomAt(0)) == null ||
				r2dm.getRenderingCoordinate(bond.getAtomAt(1)) == null)
		{
			return;
		}

		if (bond.getStereo() != CDKConstants.STEREO_BOND_NONE && bond.getStereo() != CDKConstants.STEREO_BOND_UNDEFINED)
		{
			// Draw stero information if available
			if (bond.getStereo() >= CDKConstants.STEREO_BOND_UP)
			{
				paintWedgeBond(bond, bondColor, graphics);
			} else
			{
				paintDashedWedgeBond(bond, bondColor, graphics);
			}
		} else
		{
			// Draw bond order when no stereo info is available
			if (bond.getOrder() == CDKConstants.BONDORDER_SINGLE)
			{
				paintSingleBond(bond, bondColor, graphics);
			} else if (bond.getOrder() == CDKConstants.BONDORDER_DOUBLE)
			{
				paintDoubleBond(bond, bondColor, graphics);
			} else if (bond.getOrder() == CDKConstants.BONDORDER_TRIPLE)
			{
				paintTripleBond(bond, bondColor, graphics);
			} else
			{
				// paint all other bonds as single bonds
				paintSingleBond(bond, bondColor, graphics);
			}
		}
	}


	/**
	 *  Triggers the paint method suitable to the bondorder of the given bond that
	 *  is part of a ring with CDK's grey inner bonds.
	 *
	 *@param  bond       The Bond to be drawn.
	 *@param  ring       Description of the Parameter
	 *@param  bondColor  Description of the Parameter
	 *@param  graphics   Description of the Parameter
	 */
	public void paintRingBond(org.openscience.cdk.interfaces.IBond bond, IRing ring, Color bondColor, Graphics2D graphics)
	{
		if (bond.getOrder() == 1.0)
		{
			// Added by rstefani (in fact, code copied from paintBond)
			if (bond.getStereo() != CDKConstants.STEREO_BOND_NONE && bond.getStereo() != CDKConstants.STEREO_BOND_UNDEFINED)
			{
				// Draw stero information if available
				if (bond.getStereo() >= CDKConstants.STEREO_BOND_UP)
				{
					paintWedgeBond(bond, bondColor, graphics);
				} else
				{
					paintDashedWedgeBond(bond, bondColor, graphics);
				}
			} else
			{
				// end code by rstefani
				paintSingleBond(bond, bondColor, graphics);
			}
		} else if (bond.getOrder() == 2.0)
		{
			paintSingleBond(bond, bondColor, graphics);
			paintInnerBond(bond, ring, bondColor, graphics);
		} else if (bond.getOrder() == 3.0)
		{
			paintTripleBond(bond, bondColor, graphics);
		} else
		{
			logger.warn("Drawing bond as single even though it has order: ", bond.getOrder());
			paintSingleBond(bond, bondColor, graphics);
		}
	}


	/**
	 *  Draws the ring in an aromatic ring.
	 *
	 *@param  ring       Description of the Parameter
	 *@param  bondColor  Description of the Parameter
	 *@param  graphics   Description of the Parameter
	 */
	public void paintRingRing(IRing ring, Color bondColor, Graphics2D graphics)
	{
		Point2d center = GeometryTools.get2DCenter(ring);

		double[] minmax = GeometryTools.getMinMax(ring);
		double width = (minmax[2] - minmax[0]) * 0.7;
		double height = (minmax[3] - minmax[1]) * 0.7;
		int[] coords = {
				(int) (center.x - (width / 2.0)),
				(int) (center.y + (height / 2.0))
				};
		int[] screenCoords = getScreenCoordinates(coords);
		int ring_width = (int) (width * r2dm.getZoomFactor());
		int ring_height = (int) (height * r2dm.getZoomFactor());

		// Calculate inner oval offset - must be a whole number of pixels > 1.
		int offset = (int) Math.ceil(0.05 * Math.max(ring_width, ring_height));
		int offsetX2 = 2 * offset;

		// Fill outer oval.
		graphics.setColor(bondColor);
		graphics.fillOval(
				screenCoords[0], screenCoords[1],
				ring_width, ring_height);

		// Erase inner oval.
		graphics.setColor(r2dm.getBackColor());
		graphics.fillOval(
				screenCoords[0] + offset,
				screenCoords[1] + offset,
				ring_width - offsetX2,
				ring_height - offsetX2);

		// Reset drawing colour.
		graphics.setColor(bondColor);
	}


	/**
	 *  Paint a Bond in an aromatic ring, using CDK style, meaning grey inner
	 *  bonds.
	 *
	 *@param  bond       Description of the Parameter
	 *@param  ring       Description of the Parameter
	 *@param  bondColor  Description of the Parameter
	 *@param  graphics   Description of the Parameter
	 */
	public void paintAromaticRingBondCDKStyle(org.openscience.cdk.interfaces.IBond bond, IRing ring, Color bondColor, Graphics2D graphics)
	{
		paintSingleBond(bond, bondColor, graphics);
		paintInnerBond(bond, ring, Color.lightGray, graphics);
	}


	/**
	 *  Paints the given single bond.
	 *
	 *@param  bond       The single bond to be drawn
	 *@param  bondColor  Description of the Parameter
	 *@param  graphics   Description of the Parameter
	 */
	public void paintSingleBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		if (GeometryTools.has2DCoordinates(bond))
		{
			paintOneBond(GeometryTools.getBondCoordinates(bond, r2dm.getRenderingCoordinates()), bondColor, graphics);
		}
	}


	/**
	 *  Paints The given double bond.
	 *
	 *@param  bond       The double bond to be drawn
	 *@param  bondColor  Description of the Parameter
	 *@param  graphics   Description of the Parameter
	 */
	public void paintDoubleBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond,r2dm.getRenderingCoordinates()), r2dm.getBondDistance() / 2);

		int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
		paintOneBond(newCoords1, bondColor, graphics);

		int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
		paintOneBond(newCoords2, bondColor, graphics);
	}


	/**
	 *  Paints the given triple bond.
	 *
	 *@param  bond       The triple bond to be drawn
	 *@param  bondColor  Description of the Parameter
	 *@param  graphics   Description of the Parameter
	 */
	public void paintTripleBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		paintSingleBond(bond, bondColor, graphics);

		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond,r2dm.getRenderingCoordinates()), (r2dm.getBondWidth() / 2 + r2dm.getBondDistance()));

		int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
		paintOneBond(newCoords1, bondColor, graphics);

		int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
		paintOneBond(newCoords2, bondColor, graphics);
	}


	/**
	 *  Paints the inner bond of a double bond that is part of a ring.
	 *
	 *@param  bond       The bond to be drawn
	 *@param  ring       The ring the bond is part of
	 *@param  bondColor  Color of the bond
	 *@param  graphics   Description of the Parameter
	 */
	public void paintInnerBond(org.openscience.cdk.interfaces.IBond bond, IRing ring, Color bondColor, Graphics2D graphics)
	{
		Point2d center = GeometryTools.get2DCenter(ring);

		int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond,r2dm.getRenderingCoordinates()), (r2dm.getBondWidth() / 2 + r2dm.getBondDistance()));
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
	protected int[] shortenBond(int[] coords, int edges)
	{
		int xDiff = (coords[0] - coords[2]) / (edges * 2);
		int yDiff = (coords[1] - coords[3]) / (edges * 2);
		int[] newCoords = {coords[0] - xDiff, coords[1] - yDiff, coords[2] + xDiff, coords[3] + yDiff};
		return newCoords;
	}


	/**
	 *  Really paints the bond. It is triggered by all the other paintbond methods
	 *  to draw a polygon as wide as bond width.
	 *
	 *@param  coords
	 *@param  bondColor  Color of the bond
	 *@param  graphics   Description of the Parameter
	 */
	public void paintOneBond(int[] coords, Color bondColor, Graphics2D graphics)
	{
		graphics.setColor(bondColor);
        int[] newCoords = GeometryTools.distanceCalculator(coords, r2dm.getBondWidth() / 2);
		int[] screenCoords = getScreenCoordinates(newCoords);
    int[] xCoords = {screenCoords[0], screenCoords[2], screenCoords[4], screenCoords[6]};
		int[] yCoords = {screenCoords[1], screenCoords[3], screenCoords[5], screenCoords[7]};
        graphics.fillPolygon(xCoords, yCoords, 4);
	}


	/**
	 *  Paints the given bond as a wedge bond.
	 *
	 *@param  bond       The singlebond to be drawn
	 *@param  bondColor  Color of the bond
	 *@param  graphics   Description of the Parameter
	 */
	public void paintWedgeBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		double wedgeWidth = r2dm.getBondWidth() * 2.0;
		// this value should be made customazible

		int[] coords = GeometryTools.getBondCoordinates(bond,r2dm.getRenderingCoordinates());
		int[] screenCoords = getScreenCoordinates(coords);
		graphics.setColor(bondColor);
		int[] newCoords = GeometryTools.distanceCalculator(coords, wedgeWidth);
		int[] newScreenCoords = getScreenCoordinates(newCoords);
		if (bond.getStereo() == CDKConstants.STEREO_BOND_UP)
		{
			int[] xCoords = {screenCoords[0], newScreenCoords[6], newScreenCoords[4]};
			int[] yCoords = {screenCoords[1], newScreenCoords[7], newScreenCoords[5]};
			graphics.fillPolygon(xCoords, yCoords, 3);
		} else
		{
			int[] xCoords = {screenCoords[2], newScreenCoords[0], newScreenCoords[2]};
			int[] yCoords = {screenCoords[3], newScreenCoords[1], newScreenCoords[3]};
			graphics.fillPolygon(xCoords, yCoords, 3);
		}
	}


	/**
	 *  Paints the given bond as a dashed wedge bond.
	 *
	 *@param  bond       The single bond to be drawn
	 *@param  bondColor  Color of the bond
	 *@param  graphics   Description of the Parameter
	 */
	public void paintDashedWedgeBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		graphics.setColor(bondColor);

		double bondLength = GeometryTools.getLength2D(bond, r2dm.getRenderingCoordinates());
		int numberOfLines = (int) (bondLength / 4.0);
		// this value should be made customizable
		double wedgeWidth = r2dm.getBondWidth() * 2.0;
		// this value should be made customazible

		double widthStep = wedgeWidth / (double) numberOfLines;
		Point2d p1 = r2dm.getRenderingCoordinate(bond.getAtomAt(0));
		Point2d p2 = r2dm.getRenderingCoordinate(bond.getAtomAt(1));
		if (bond.getStereo() == CDKConstants.STEREO_BOND_DOWN_INV)
		{
			// draw the wedge bond the other way around
			p1 = r2dm.getRenderingCoordinate(bond.getAtomAt(1));
			p2 = r2dm.getRenderingCoordinate(bond.getAtomAt(0));
		}
		Vector2d lengthStep = new Vector2d(p2);
		lengthStep.sub(p1);
		lengthStep.scale(1.0 / numberOfLines);
		Vector2d p = GeometryTools.calculatePerpendicularUnitVector(p1, p2);

		Point2d currentPoint = new Point2d(p1);
		Point2d q1 = new Point2d();
		Point2d q2 = new Point2d();
		for (int i = 0; i <= numberOfLines; ++i)
		{
			Vector2d offset = new Vector2d(p);
			offset.scale(i * widthStep);
			q1.add(currentPoint, offset);
			q2.sub(currentPoint, offset);
			int[] lineCoords = {(int) q1.x, (int) q1.y, (int) q2.x, (int) q2.y};
			lineCoords = getScreenCoordinates(lineCoords);
			graphics.drawLine(lineCoords[0], lineCoords[1], lineCoords[2], lineCoords[3]);
			currentPoint.add(lengthStep);
		}
	}


	/**
	 *  Paints a line between the start point and end point of the pointer vector
	 *  that is stored in the Renderer2DModel.
	 *
	 *@param  graphics  Description of the Parameter
	 */
	public void paintPointerVector(Graphics2D graphics)
	{
		if (r2dm.getPointerVectorStart() != null)
		{
			if (r2dm.getPointerVectorEnd() != null)
			{
				Point startPoint = r2dm.getPointerVectorStart();
				Point endPoint = r2dm.getPointerVectorEnd();
				int[] points = {startPoint.x, startPoint.y, endPoint.x, endPoint.y};
				int[] newCoords = GeometryTools.distanceCalculator(points, r2dm.getBondWidth() / 2);
				int[] screenCoords = getScreenCoordinates(newCoords);
				int[] xCoords = {screenCoords[0], screenCoords[2], screenCoords[4], screenCoords[6]};
				int[] yCoords = {screenCoords[1], screenCoords[3], screenCoords[5], screenCoords[7]};
				graphics.setColor(r2dm.getForeColor());
				// apply zoomFactor
				graphics.fillPolygon(xCoords, yCoords, 4);
			} else
			{
				logger.warn("Start point of vector was not null, but end was!");
			}
		} else if (r2dm.getPointerVectorEnd() != null)
		{
			logger.warn("End point of vector was not null, but start was!");
		}
	}


	/**
	 *  Returns the Renderer2DModel of this Renderer.
	 *
	 *@return    the Renderer2DModel of this Renderer
	 */
	public Renderer2DModel getRenderer2DModel()
	{
		return this.r2dm;
	}



	/**
	 *  Sets the Renderer2DModel of this Renderer.
	 *
	 *@param  r2dm  the new Renderer2DModel for this Renderer
	 */
	public void setRenderer2DModel(Renderer2DModel r2dm)
	{
		this.r2dm = r2dm;
	}


	/**
	 *  Gets the screenCoordinates attribute of the Renderer2D object
	 *
	 *@param  p  Description of the Parameter
	 *@return    The screenCoordinates value
	 */
	protected Point getScreenCoordinates(Point p)
	{
		graphicsHeight = (int) r2dm.getBackgroundDimension().getHeight();
		//logger.debug("HEIGHT: " + graphicsHeight);
		Point screenCoordinate = new Point();
		double zoomFactor = r2dm.getZoomFactor();
		screenCoordinate.x = (int) ((double) p.x * zoomFactor);
		screenCoordinate.y = graphicsHeight - (int) ((double) p.y * zoomFactor);
        if(useScreenSize){
            screenCoordinate.y=graphicsHeight - screenCoordinate.y;
        }else{
            screenCoordinate.y+=10;
            screenCoordinate.x+=10;
        }
		return screenCoordinate;
	}


	/**
	 *  Expects an array of even length with x's at the uneven indices and y's at
	 *  the even indices.
	 *
	 *@param  coords  Description of the Parameter
	 *@return         The screenCoordinates value
	 */
	public int[] getScreenCoordinates(int[] coords)
	{
        graphicsHeight = (int) r2dm.getBackgroundDimension().getHeight();
		logger.debug("HEIGHT: " + graphicsHeight);
		int[] screenCoordinates = new int[coords.length];
		double zoomFactor = r2dm.getZoomFactor();
		final int coordCount = coords.length / 2;
		for (int i = 0; i < coordCount; i++)
		{
			screenCoordinates[i * 2] = (int) ((double) coords[i * 2] * zoomFactor);
			screenCoordinates[i * 2 + 1] = (int) ((double) coords[i * 2 + 1] * zoomFactor);
            if(useScreenSize){
                screenCoordinates[i * 2 + 1]=graphicsHeight - screenCoordinates[i * 2 + 1];
            }else{
                screenCoordinates[i * 2 + 1]+=10;
                screenCoordinates[i * 2]+=10;
            }
		}
		return screenCoordinates;
	}


	/**
	 *  Gets the screenSize attribute of the Renderer2D object
	 *
	 *@param  size  Description of the Parameter
	 *@return       The screenSize value
	 */
	protected float getScreenSize(int size)
	{
		return (float) size * (float) r2dm.getZoomFactor();
	}


	/**
	 *  Paints the toolTipText for an atom
	 *
	 *@param  atom        The atom.
	 *@param  graphics    The current graphics object.
	 *@param  atomNumber  Description of the Parameter
	 */
	public void paintToolTip(org.openscience.cdk.interfaces.IAtom atom, Graphics2D graphics, int atomNumber)
	{
		tooltiparea = new int[4];
		String text = r2dm.getToolTipText(atom);
		String[] result = text.split("\\n");
		int widestline = 0;
		for (int i = 0; i < result.length; i++)
		{
			String text2 = result[i];
			Font normalFont = r2dm.getFont();
			if (normalFont == null)
			{
				normalFont = graphics.getFont();
			}
			graphics.setFont(normalFont);
			FontMetrics fm = graphics.getFontMetrics();
			int atomSymbolW = (new Integer(fm.stringWidth(text2))).intValue();
			if (atomSymbolW > widestline)
			{
				widestline = atomSymbolW;
			}
		}
		Font normalFont = r2dm.getFont();
		if (normalFont == null)
		{
			normalFont = graphics.getFont();
		}
		graphics.setFont(normalFont);
		FontMetrics fm = graphics.getFontMetrics();
		int[] provcoords = {(int) r2dm.getRenderingCoordinate(atom).x + 10, (int) r2dm.getRenderingCoordinate(atom).y};
		int[] screenCoords = getScreenCoordinates(provcoords);
		for (int i = 0; i < result.length; i++)
		{
			if (i == 0)
			{
				tooltiparea[0] = screenCoords[0];
				tooltiparea[1] = screenCoords[1];
			}
			String text2 = result[i];
			int atomSymbolH = (new Integer(fm.getAscent())).intValue();
			graphics.setColor(Color.YELLOW);
			graphics.fillRect(screenCoords[0], screenCoords[1] + ((atomSymbolH + 4) * i), widestline + 4, atomSymbolH + 4);
			graphics.setColor(Color.BLACK);
			graphics.drawString(text2, screenCoords[0] + 2, screenCoords[1] + atomSymbolH + 2 + ((atomSymbolH + 4) * i));
			if (i == result.length - 1)
			{
				tooltiparea[2] = screenCoords[0] + widestline + 4;
				tooltiparea[3] = screenCoords[1] + ((atomSymbolH + 4) * i) + atomSymbolH + 4;
			}
		}
	}


	/**
	 *  The mouseMoved event (used for atom toolTipTexts).
	 *
	 *@param  e  The event.
	 */
	public void mouseMoved(MouseEvent e)
	{
		if (r2dm.getHighlightedAtom() != null)
		{
			r2dm.setShowTooltip(true);
		} else
		{
			r2dm.setShowTooltip(false);
		}
	}


	/**
	 *  The mouseDragged event (not used currently).
	 *
	 *@param  e  The event.
	 */
	public void mouseDragged(MouseEvent e)
	{
	}
}

