/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.renderer.color.*;
import org.openscience.cdk.validate.ProblemMarker;

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
 * @author     egonw
 * @created    2002-10-03
 * @keyword    viewer, 2D-viewer
 */
public class Renderer2D {
  boolean debug = false;
  SSSRFinder sssrf = new SSSRFinder();

  private LoggingTool logger;
  private IsotopeFactory isotopeFactory;

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
  public Renderer2D(Renderer2DModel r2dm) {
    this.r2dm = r2dm;
    logger = new LoggingTool(this.getClass().getName());

    try {
      isotopeFactory = IsotopeFactory.getInstance();
    } catch (Exception exception) {
      logger.error("Error while instantiating IsotopeFactory");
      logger.warn("Will not be able to display undefault isotopes");
      logger.debug(exception);
    }
  }



  /**
   * Sets the Renderer2DModel of this Renderer.
   *
   * @param  r2dm  the new Renderer2DModel for this Renderer
   */
  public void setRenderer2DModel(Renderer2DModel r2dm) {
    this.r2dm = r2dm;
  }


  /**
   * Returns the Renderer2DModel of this Renderer.
   *
   * @return    the Renderer2DModel of this Renderer
   */
  public Renderer2DModel getRenderer2DModel() {
    return this.r2dm;
  }


  /**
   *  Description of the Method
   *
   * @param  model     Description of Parameter
   * @param  graphics  Description of Parameter
   */
  public void paintChemModel(ChemModel model, Graphics graphics) {
    if (model.getSetOfReactions() != null) {
      paintSetOfReactions(model.getSetOfReactions(), graphics);
    }
    if (model.getSetOfMolecules() != null) {
      paintSetOfMolecules(model.getSetOfMolecules(), graphics);
    }
  }


  /**
   *  Description of the Method
   *
   * @param  reactionSet  Description of Parameter
   * @param  graphics     Description of Parameter
   */
  public void paintSetOfReactions(SetOfReactions reactionSet, Graphics graphics) {
    Reaction[] reactions = reactionSet.getReactions();
    for (int i = 0; i < reactions.length; i++) {
      paintReaction(reactions[i], graphics);
    }
  }


  /**
   *  Description of the Method
   *
   * @param  moleculeSet  Description of Parameter
   * @param  graphics     Description of Parameter
   */
  public void paintSetOfMolecules(SetOfMolecules moleculeSet, Graphics graphics) {
    Molecule[] molecules = moleculeSet.getMolecules();
    for (int i = 0; i < molecules.length; i++) {
      paintMolecule(molecules[i], graphics);
    }
  }


  /**
   *  Description of the Method
   *
   * @param  reaction  Description of Parameter
   * @param  graphics  Description of Parameter
   */
  public void paintReaction(Reaction reaction, Graphics graphics) {
    // calculate some boundaries
    AtomContainer reactantContainer = new AtomContainer();
    Molecule[] reactants = reaction.getReactants();
    for (int i = 0; i < reactants.length; i++) {
      reactantContainer.add(reactants[i]);
    }
    double[] minmaxReactants = GeometryTools.getMinMax(reactantContainer);
    AtomContainer productContainer = new AtomContainer();
    Molecule[] products = reaction.getProducts();
    for (int i = 0; i < products.length; i++) {
      productContainer.add(products[i]);
    }
    double[] minmaxProducts = GeometryTools.getMinMax(productContainer);

    // paint atom atom mappings
    if (r2dm.getShowAtomAtomMapping()) {
      Mapping[] mappings = reaction.getMappings();
      for (int i = 0; i < mappings.length; i++) {
        ChemObject[] objects = mappings[i].getRelatedChemObjects();
        Atom highlighted = r2dm.getHighlightedAtom();
        if (highlighted != null) {
          // only draw mapping when one of the mapped atoms
          // is highlighted
          if (objects[0] instanceof Atom &&
              objects[1] instanceof Atom &&
              (highlighted.equals(objects[0]) ||
              highlighted.equals(objects[1]))) {
            Atom atom1 = (Atom) objects[0];
            Atom atom2 = (Atom) objects[1];
            int[] ints = new int[4];
            ints[0] = (int) (atom1.getPoint2D().x);
            ints[1] = (int) (atom1.getPoint2D().y);
            ints[2] = (int) (atom2.getPoint2D().x);
            ints[3] = (int) (atom2.getPoint2D().y);
            int[] screenCoords = getScreenCoordinates(ints);
            graphics.setColor(r2dm.getAtomAtomMappingLineColor());
            graphics.drawLine(screenCoords[0], screenCoords[1],
                screenCoords[2], screenCoords[3]);
            graphics.setColor(r2dm.getForeColor());
          }
        }
      }
    }

    // paint box around total
    int width = 13;
    double[] minmaxReaction = new double[4];
    minmaxReaction[0] = Math.min(minmaxReactants[0], minmaxProducts[0]);
    minmaxReaction[1] = Math.min(minmaxReactants[1], minmaxProducts[1]);
    minmaxReaction[2] = Math.max(minmaxReactants[2], minmaxProducts[2]);
    minmaxReaction[3] = Math.max(minmaxReactants[3], minmaxProducts[3]);
    String caption = reaction.getID();
    if (reaction.getProperty(CDKConstants.TITLE) != null) {
      caption = reaction.getProperty(CDKConstants.TITLE) +
          " (" + caption + ")";
    }
    paintBoundingBox(minmaxReaction, caption, 2 * width, graphics);

    // paint reactants content
    paintBoundingBox(minmaxReactants, "Reactants", width, graphics);
    paintMolecule(reactantContainer, graphics);

    // paint products content
    paintBoundingBox(minmaxProducts, "Products", width, graphics);
    paintMolecule(productContainer, graphics);

    // paint arrow
    int[] ints = new int[4];
    ints[0] = (int) (minmaxReactants[2]) + width + 5;
    ints[1] = (int) (minmaxReactants[1] + (minmaxReactants[3] - minmaxReactants[1]) / 2);
    ints[2] = (int) (minmaxProducts[0]) - (width + 5);
    ints[3] = ints[1];
    int[] screenCoords = getScreenCoordinates(ints);
    int direction = reaction.getDirection();
    if (direction == Reaction.FORWARD) {
      graphics.drawLine(screenCoords[0], screenCoords[1],
          screenCoords[2], screenCoords[3]);
      graphics.drawLine(screenCoords[2], screenCoords[3],
          screenCoords[2] - 7, screenCoords[3] - 7);
      graphics.drawLine(screenCoords[2], screenCoords[3],
          screenCoords[2] - 7, screenCoords[3] + 7);
    } else if (direction == Reaction.BACKWARD) {
      graphics.drawLine(screenCoords[0], screenCoords[1],
          screenCoords[2], screenCoords[3]);
      graphics.drawLine(screenCoords[0], screenCoords[1],
          screenCoords[0] + 7, screenCoords[1] - 7);
      graphics.drawLine(screenCoords[0], screenCoords[1],
          screenCoords[0] + 7, screenCoords[1] + 7);
    } else if (direction == Reaction.BIDIRECTIONAL) {
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


  /**
   * @param  minmax    array of length for with min and max 2D coordinates
   * @param  caption   Description of Parameter
   * @param  side      Description of Parameter
   * @param  graphics  Description of Parameter
   */
  public void paintBoundingBox(double[] minmax, String caption,
      int side, Graphics graphics) {
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
    int heigth = screenCoords[3] - screenCoords[1];
    int width = screenCoords[2] - screenCoords[0];
    graphics.drawRect((int) screenCoords[0], (int) screenCoords[1], width, heigth);

    // draw reaction ID
    Font unscaledFont = graphics.getFont();
    float fontSize = getScreenSize(unscaledFont.getSize());
    graphics.setFont(unscaledFont.deriveFont(fontSize));
    graphics.drawString(caption, (int) screenCoords[0], (int) screenCoords[1]);
    graphics.setFont(unscaledFont);
  }


  /**
   * Triggers the methods to make the molecule fit into the frame and to paint
   * it.
   *
   * @param  atomCon   Description of the Parameter
   * @param  graphics  Description of the Parameter
   */
  public void paintMolecule(AtomContainer atomCon, Graphics graphics) {
    RingSet ringSet = new RingSet();
    Molecule[] molecules = null;
    try {
      molecules = ConnectivityChecker.partitionIntoMolecules(atomCon).getMolecules();
    } catch (Exception exception) {
      logger.warn("Could not partition molecule: " + exception.toString());
      logger.debug(exception);
      return;
    }
    for (int i = 0; i < molecules.length; i++) {
      ringSet.add(sssrf.findSSSR(molecules[i]));
    }
    if (r2dm.getPointerVectorStart() != null && r2dm.getPointerVectorEnd() != null) {
      paintPointerVector(graphics);
    }
    paintBonds(atomCon, ringSet, graphics);
    paintAtoms(atomCon, graphics);
    if (r2dm.getSelectRect() != null) {
      graphics.setColor(r2dm.getHighlightColor());
      graphics.drawPolygon(r2dm.getSelectRect());
    }
    paintLassoLines(graphics);

  }


  /**
   *  Description of the Method
   *
   * @param  graphics  Description of Parameter
   */
  public void paintLassoLines(Graphics graphics) {
    Vector points = r2dm.getLassoPoints();
    if (points.size() > 1) {
      Point point1 = (Point) points.elementAt(0);
      Point point2;
      for (int i = 1; i < points.size(); i++) {
        point2 = (Point) points.elementAt(i);
        graphics.drawLine(point1.x, point1.y, point2.x, point2.y);
        point1 = point2;
      }
    }
  }


  /**
   * Searches through all the atoms in the given array of atoms, triggers the
   * paintColouredAtoms method if the atom has got a certain color and triggers
   * the paintAtomSymbol method if the symbol of the atom is not C.
   *
   * @param  atomCon   Description of the Parameter
   * @param  graphics  Description of Parameter
   */
  public void paintAtoms(AtomContainer atomCon, Graphics graphics) {
    Atom[] atoms = atomCon.getAtoms();
    for (int i = 0; i < atoms.length; i++) {
      paintAtom(atomCon, atoms[i], graphics);
    }
  }


  /**
   *  Description of the Method
   *
   * @param  container  Description of Parameter
   * @param  atom       Description of Parameter
   * @param  graphics   Description of Parameter
   */
  public void paintAtom(AtomContainer container, Atom atom, Graphics graphics) {
    Color atomBackColor = r2dm.getAtomBackgroundColor(atom);
    if (atom.equals(r2dm.getHighlightedAtom())) {
      paintColouredAtomBackground(atom, atomBackColor, graphics);
    }

    int alignment = GeometryTools.getBestAlignmentForLabel(container, atom);
    boolean drawSymbol = false;
    if (atom instanceof PseudoAtom) {
      drawSymbol = false;
      paintPseudoAtomLabel((PseudoAtom) atom, atomBackColor, graphics, alignment);
    } else if (!atom.getSymbol().equals("C")) {
      /*
       *  only show element for non-carbon atoms,
       *  unless (see below)...
       */
      drawSymbol = true;
    } else if (r2dm.getKekuleStructure()) {
      // ... unless carbon must be drawn because in Kekule mode
      drawSymbol = true;
    } else if (container.getConnectedBonds(atom).length < 1) {
      // ... unless carbon is unbonded
      drawSymbol = true;
    } else if (r2dm.getShowEndCarbons() && (container.getConnectedBonds(atom).length == 1)) {
      drawSymbol = true;
    } else if (atom.getProperty(ProblemMarker.ERROR_MARKER) != null) {
      // ... unless carbon is unbonded
      drawSymbol = true;
    } else if (atom.getMassNumber() != 0) {
      try {
        if (atom.getMassNumber() != IsotopeFactory.getInstance().
            getMajorIsotope(atom.getSymbol()).getMassNumber()) {
          drawSymbol = true;
        }
      } catch (Exception exception) {
      }
      ;
    }
    if (r2dm.drawNumbers()) {
      drawSymbol = true;
    }
    if (drawSymbol) {
      paintAtomSymbol(atom, atomBackColor, graphics, alignment, container.getAtomNumber(atom) + 1);
    }
    if (r2dm.showTooltip && atom == r2dm.getHighlightedAtom() && r2dm.getToolTipText(r2dm.getHighlightedAtom()) != null) {
      paintToolTip(atom, graphics);
    }
  }


  /**
   *  Paints the toolTipText for an atom
   *
   * @param  atom      The atom.
   * @param  graphics  The current graphics object.
   */
  public void paintToolTip(Atom atom, Graphics graphics) {
    String text = r2dm.getToolTipText(r2dm.getHighlightedAtom());
    Font normalFont = graphics.getFont();
    graphics.setFont(normalFont);
    FontMetrics fm = graphics.getFontMetrics();
    int atomSymbolW = (new Integer(fm.stringWidth(text))).intValue();
    int atomSymbolH = (new Integer(fm.getAscent())).intValue();
    graphics.setColor(Color.YELLOW);
    graphics.fillRect((int) atom.getX2D(), (int) atom.getY2D(), atomSymbolW + 4, atomSymbolH + 4);
    graphics.setColor(Color.BLACK);
    graphics.drawString(text, ((int) atom.getX2D()) + 2, ((int) atom.getY2D()) + atomSymbolH + 2);
  }


  /**
   * Paints a rectangle of the given color at the position of the given atom.
   * For example when the atom is highlighted.
   *
   * @param  atom      The atom to be drawn
   * @param  color     The color of the atom to be drawn
   * @param  graphics  Description of Parameter
   */
  public void paintColouredAtomBackground(Atom atom, Color color, Graphics graphics) {
    int atomRadius = r2dm.getAtomRadius();
    graphics.setColor(color);
    int[] coords = {(int) atom.getX2D() - (atomRadius / 2),
        (int) atom.getY2D() - (atomRadius / 2),
        atomRadius, atomRadius};
    coords = getScreenCoordinates(coords);
    graphics.fillRect(coords[0], coords[1], coords[2], coords[3]);
  }


  /**
   * Paints the given atom symbol. It first outputs some empty space using the
   * background color, slightly larger than the space that the symbol occupies.
   * The atom symbol is then printed into the empty space.
   *
   * <p>The algorithm uses four steps:
   * <ol>
   *   <li>it calculates the widths and heights of all label parts
   *   <li>it calculates the x's and y's of all label parts
   *   <li>it creates empty backgrounds for all label parts
   *   <li>it draws all label parts
   * </ol>
   *
   * @param  atom        The atom to be drawn
   * @param  backColor   Description of the Parameter
   * @param  graphics    Graphics to draw too
   * @param  alignment   How to align the H's
   * @param  atomNumber  Number of the atom in the AtomContainer, 0 is not in container
   */
  public void paintAtomSymbol(Atom atom, Color backColor, Graphics graphics, int alignment, int atomNumber) {
    if (atom.getPoint2D() == null) {
      logger.warn("Cannot draw atom without 2D coordinate");
      return;
    }

    // The fonts for calculating geometries
    float subscriptFraction = 0.7f;
    Font normalFont = graphics.getFont();
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
    if (atomSymbol.equals("C")) {
      atomSymbol = "";
    }
    if (r2dm.drawNumbers() && atomNumber != 0 && !atomSymbol.equals("")) {
      atomSymbol += "-" + atomNumber;
    }
    if (r2dm.drawNumbers() && atomSymbol.equals("")) {
      atomSymbol += atomNumber;
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
    if (implicitHydrogenCount > 0) {
      // fm is identical, don't change
      hSymbolW = (new Integer(fm.stringWidth(hSymbol))).intValue();
      hSymbolH = atomSymbolH;
    }
    graphics.setFont(subscriptFont);
    fm = graphics.getFontMetrics();
    int hMultiplierW = 0;
    int hMultiplierH = 0;
    if (implicitHydrogenCount > 1) {
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
    if (formalCharge != 0) {
      if (formalCharge > 1) {
        formalChargeString = new Integer(formalCharge).toString() + "+";
      } else if (formalCharge > 0) {
        formalChargeString = "+";
      } else if (formalCharge < -1) {
        formalChargeString = new Integer(formalCharge * -1).toString() + "-";
      } else if (formalCharge < 0) {
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
    if (atomicMassNumber != 0 && isotopeFactory != null) {
      Isotope majorIsotope = isotopeFactory.getMajorIsotope(atom.getSymbol());
      if (majorIsotope != null && atomicMassNumber != majorIsotope.getMassNumber()) {
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

    if (alignment == 1) {
      // left alignment
      labelX = (int) (atom.getPoint2D().x - (atomSymbolXOffset + isotopeW));
    } else {
      // right alignment
      labelX = (int) (atom.getPoint2D().x -
          (atomSymbolXOffset + Math.max(isotopeW, hMultiplierW) + hSymbolW));
    }
    // labelY and labelH are the same for both left/right aligned
    labelY = (int) (atom.getPoint2D().y - (atomSymbolYOffset + isotopeH));

    // xy for atom symbol
    int[] atomSymbolCoords = new int[2];
    if (alignment == 1) {
      // left alignment
      atomSymbolCoords[0] = labelX + isotopeW;
    } else {
      // right alignment
      atomSymbolCoords[0] = labelX + hSymbolW + Math.max(isotopeW, hMultiplierW);
    }
    atomSymbolCoords[1] = labelY + isotopeH + atomSymbolH;

    // xy for implicit hydrogens
    int[] hSymbolCoords = new int[2];
    if (alignment == 1) {
      // left alignment
      hSymbolCoords[0] = labelX + isotopeW + atomSymbolW;
    } else {
      // right alignment
      hSymbolCoords[0] = labelX;
    }
    hSymbolCoords[1] = labelY + isotopeH + atomSymbolH;
    // xy for implicit hydrogens multiplier
    int[] hMultiplierCoords = new int[2];
    if (alignment == 1) {
      // left alignment
      hMultiplierCoords[0] = labelX + isotopeW + atomSymbolW + hSymbolW;
    } else {
      // right alignment
      hMultiplierCoords[0] = labelX + hSymbolW;
    }
    hMultiplierCoords[1] = labelY + isotopeH + atomSymbolH + hMultiplierH / 2;

    // xy for charge
    int[] chargeCoords = new int[2];
    if (alignment == 1) {
      // left alignment
      chargeCoords[0] = labelX + isotopeW + atomSymbolW + hSymbolW;
    } else {
      // right alignment
      chargeCoords[0] = labelX + hSymbolW + Math.max(isotopeW, hMultiplierW) +
          atomSymbolW;
    }
    chargeCoords[1] = labelY + isotopeH;

    //xy for isotope
    int[] isotopeCoords = new int[2];
    if (alignment == 1) {
      // left alignment
      isotopeCoords[0] = labelX;
    } else {
      // right alignment
      isotopeCoords[0] = labelX + hSymbolW;
    }
    isotopeCoords[1] = labelY + isotopeH;

    // STEP 3: draw empty backgrounds for all parts in the label

    int border = 2;
    // border for clearing background in pixels

    paintEmptySpace(atomSymbolCoords[0], atomSymbolCoords[1] - atomSymbolH,
        atomSymbolW, atomSymbolH, border, backColor, graphics);
    paintEmptySpace(hSymbolCoords[0], hSymbolCoords[1] - hSymbolH,
        hSymbolW, hSymbolH, border, backColor, graphics);
    paintEmptySpace(hMultiplierCoords[0], hMultiplierCoords[1] - hMultiplierH,
        hMultiplierW, hMultiplierH, border, backColor, graphics);
    paintEmptySpace(chargeCoords[0], chargeCoords[1] - formalChargeH,
        formalChargeW, formalChargeH, border, backColor, graphics);
    paintEmptySpace(isotopeCoords[0], isotopeCoords[1] - isotopeH,
        isotopeW, isotopeH, border, backColor, graphics);

    // STEP 4: draw all parts in the label

    Color atomColor = r2dm.getAtomColor(atom);
     {
      // draw SYMBOL

      int[] screenCoords = getScreenCoordinates(atomSymbolCoords);
      graphics.setColor(atomColor);
      graphics.setFont(normalScreenFont);
      graphics.drawString(atomSymbol, screenCoords[0], screenCoords[1]);

      // possibly underline SYMBOL
      if (atom.getProperty(ProblemMarker.ERROR_MARKER) != null ||
          atom.getProperty(ProblemMarker.WARNING_MARKER) != null) {
        // RED for error, ORANGE for warnings
        if (atom.getProperty(ProblemMarker.ERROR_MARKER) != null) {
          graphics.setColor(Color.red);
        } else if (atom.getProperty(ProblemMarker.WARNING_MARKER) != null) {
          graphics.setColor(Color.orange);
        }
        // make zig zag bond
        int symbolLength = atom.getSymbol().length();
        int zigzags = 1 + (2 * symbolLength);
        int spacing = atomSymbolW / zigzags;
        int width = atomSymbolH / 3;
        for (int i = -symbolLength; i <= symbolLength; i++) {
          int[] lineCoords = new int[6];
          int halfspacing = spacing / 2;
          lineCoords[0] = atomSymbolCoords[0] + (atomSymbolW / 2) + (i * spacing) - halfspacing;
          lineCoords[1] = atomSymbolCoords[1] + 1 * width;
          lineCoords[2] = lineCoords[0] + halfspacing;
          lineCoords[3] = atomSymbolCoords[1] + 2 * width;
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
    if (implicitHydrogenCount > 0 && r2dm.getShowImplicitHydrogens()) {
      int[] screenCoords = getScreenCoordinates(hSymbolCoords);
      graphics.setColor(atomColor);
      graphics.setFont(normalScreenFont);
      graphics.drawString(hSymbol, screenCoords[0], screenCoords[1]);
      if (implicitHydrogenCount > 1) {
        // draw number of hydrogens
        screenCoords = getScreenCoordinates(hMultiplierCoords);
        graphics.setColor(atomColor);
        graphics.setFont(subscriptScreenFont);
        graphics.drawString(hMultiplierString, screenCoords[0], screenCoords[1]);
      }
    }

    // draw CHARGE
    if (formalCharge != 0) {
      int[] screenCoords = getScreenCoordinates(chargeCoords);
      graphics.setColor(atomColor);
      graphics.setFont(normalScreenFont);
      graphics.drawString(formalChargeString, screenCoords[0], screenCoords[1]);
    }

    // draw ISOTOPE
    if (isotopeString.length() > 0) {
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
   * Makes a clear empty space using the background color.
   *
   * @param  x          Description of Parameter
   * @param  y          Description of Parameter
   * @param  w          Description of Parameter
   * @param  h          Description of Parameter
   * @param  border     Description of Parameter
   * @param  backColor  Description of Parameter
   * @param  graphics   Description of Parameter
   */
  public void paintEmptySpace(int x, int y, int w, int h, int border,
      Color backColor, Graphics graphics) {
    Color saveColor = graphics.getColor();
    graphics.setColor(backColor);
    int[] coords = {x - border, y - border,
        w + 2 * border, h + 2 * border};
    int[] screenCoords = getScreenCoordinates(coords);
    graphics.fillRect(screenCoords[0], screenCoords[1],
        screenCoords[2], screenCoords[3]);
    graphics.setColor(saveColor);
  }


  /**
   * Paints the label of the given PseudoAtom, instead of it's symbol.
   *
   * @param  atom       The atom to be drawn
   * @param  backColor  Description of the Parameter
   * @param  graphics   Description of Parameter
   * @param  alignment  Description of Parameter
   */
  public void paintPseudoAtomLabel(PseudoAtom atom, Color backColor, Graphics graphics, int alignment) {
    if (atom.getPoint2D() == null) {
      logger.warn("Cannot draw atom without 2D coordinate");
      return;
    }

    // The calculation fonts
    Font normalFont = graphics.getFont();
    int normalFontSize = normalFont.getSize();
    // get drawing fonts
    float normalScreenFontSize = getScreenSize(normalFontSize);
    Font normalScreenFont = normalFont.deriveFont(normalScreenFontSize);

    // calculate SYMBOL width, height
    String atomSymbol = atom.getLabel();
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

    if (alignment == 1) {
      // left alignment
      labelX = (int) (atom.getPoint2D().x - (atomSymbolFirstCharW / 2));
    } else {
      // right alignment
      labelX = (int) (atom.getPoint2D().x - (atomSymbolW + atomSymbolLastCharW / 2));
    }
    // labelY and labelH are the same for both left/right aligned
    labelY = (int) (atom.getPoint2D().y - (atomSymbolH / 2));
     {
      // make empty space

      int border = 2;
      // number of pixels
      graphics.setColor(backColor);
      int[] coords = {labelX - border, labelY - border,
          labelW + 2 * border, labelH + 2 * border};
      int[] screenCoords = getScreenCoordinates(coords);
      graphics.fillRect(screenCoords[0], screenCoords[1],
          screenCoords[2], screenCoords[3]);
    }
     {
      // draw label

      int[] coords = {labelX,
          labelY + atomSymbolH};
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
   * Triggers the suitable method to paint each of the given bonds and selects
   * the right color.
   *
   * @param  ringSet   The set of rings the molecule contains
   * @param  atomCon   Description of the Parameter
   * @param  graphics  Description of Parameter
   */
  public void paintBonds(AtomContainer atomCon, RingSet ringSet, Graphics graphics) {
    Color bondColor;
    Ring ring;
    Bond[] bonds = atomCon.getBonds();
    logger.debug("Painting bonds...");
    for (int i = 0; i < bonds.length; i++) {
      Bond currentBond = bonds[i];
      bondColor = (Color) r2dm.getColorHash().get(currentBond);
      if (bondColor == null) {
        bondColor = r2dm.getForeColor();
      }
      if (currentBond == r2dm.getHighlightedBond()) {
        bondColor = r2dm.getHighlightColor();
        for (int j = 0; j < currentBond.getAtomCount(); j++) {
          paintColouredAtomBackground(currentBond.getAtomAt(j),
              bondColor, graphics);
        }
      }
      ring = ringSet.getHeaviestRing(currentBond);
      if (ring != null) {
        logger.debug("Found ring to draw");
        if (ringIsAromatic(ring) && r2dm.getShowAromaticity()) {
          logger.debug("Ring is aromatic");
          if (r2dm.getShowAromaticityInCDKStyle()) {
            paintAromaticRingBondCDKStyle(currentBond, ring, bondColor, graphics);
          } else {
            // FIXME: don't draw the circle this many times!
            paintRingRing(ring, bondColor, graphics);
            paintSingleBond(currentBond, bondColor, graphics);
          }
        } else {
          logger.debug("Ring is *not* aromatic");
          paintRingBond(currentBond, ring, bondColor, graphics);
        }
      } else {
        logger.debug("Drawing a non-ring bond");
        paintBond(currentBond, bondColor, graphics);
      }
    }
  }


  /**
   * A ring is defined aromatic if all atoms are aromatic,
   * -or- all bonds are aromatic.
   *
   * @param  ring  Description of Parameter
   * @return       Description of the Returned Value
   */
  public boolean ringIsAromatic(Ring ring) {
    boolean isAromatic = true;
    Atom[] atoms = ring.getAtoms();
    for (int i = 0; i < atoms.length; i++) {
      if (!atoms[i].getFlag(CDKConstants.ISAROMATIC)) {
        isAromatic = false;
      }
    }
    if (!isAromatic) {
      isAromatic = true;
      Bond[] bonds = ring.getBonds();
      for (int i = 0; i < bonds.length; i++) {
        if (!bonds[i].getFlag(CDKConstants.ISAROMATIC)) {
          return false;
        }
      }
    }
    return isAromatic;
  }


  /**
   * Triggers the paint method suitable to the bondorder of the given bond.
   *
   * @param  bond       The Bond to be drawn.
   * @param  bondColor  Description of the Parameter
   * @param  graphics   Description of Parameter
   */
  public void paintBond(Bond bond, Color bondColor, Graphics graphics) {
    if (bond.getAtomAt(0).getPoint2D() == null ||
        bond.getAtomAt(1).getPoint2D() == null) {
      return;
    }

    if (bond.getStereo() != CDKConstants.STEREO_BOND_NONE && bond.getStereo() != CDKConstants.STEREO_BOND_UNDEFINED) {
      // Draw stero information if available
      if (bond.getStereo() >= CDKConstants.STEREO_BOND_UP) {
        paintWedgeBond(bond, bondColor, graphics);
      } else {
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
   * Triggers the paint method suitable to the bondorder of the given bond that
   * is part of a ring with CDK's grey inner bonds.
   *
   * @param  bond       The Bond to be drawn.
   * @param  ring       Description of the Parameter
   * @param  bondColor  Description of the Parameter
   * @param  graphics   Description of Parameter
   */
  public void paintRingBond(Bond bond, Ring ring, Color bondColor, Graphics graphics) {
    if (bond.getOrder() == 1.0) {
      paintSingleBond(bond, bondColor, graphics);
    } else if (bond.getOrder() == 2.0) {
      paintSingleBond(bond, bondColor, graphics);
      paintInnerBond(bond, ring, bondColor, graphics);
    } else if (bond.getOrder() == 3.0) {
      paintTripleBond(bond, bondColor, graphics);
    } else {
      logger.warn("Drawing bond as single even though it has order:" + bond.getOrder());
      paintSingleBond(bond, bondColor, graphics);
    }
  }


  /**
   * Draws the ring in an aromatic ring.
   *
   * @param  ring       Description of Parameter
   * @param  bondColor  Description of Parameter
   * @param  graphics   Description of Parameter
   */
  public void paintRingRing(Ring ring, Color bondColor, Graphics graphics) {
    Point2d center = ring.get2DCenter();

    double[] minmax = GeometryTools.getMinMax(ring);
    double width = (minmax[2] - minmax[0]) * 0.7;
    double height = (minmax[3] - minmax[1]) * 0.7;
    int[] coords = {(int) (center.x - (width / 2.0)), (int) (center.y - (height / 2.0))};
    int[] screenCoords = getScreenCoordinates(coords);
    graphics.fillOval(screenCoords[0], screenCoords[1],
        (int) (width * r2dm.getZoomFactor()),
        (int) (height * r2dm.getZoomFactor()));

    double innerWidth = width * 0.9;
    double innerHeight = height * 0.9;
    int[] innerCoords = {(int) (center.x - (innerWidth / 2.0)), (int) (center.y - (innerHeight / 2.0))};
    screenCoords = getScreenCoordinates(innerCoords);
    graphics.setColor(r2dm.getBackColor());
    graphics.fillOval(screenCoords[0], screenCoords[1],
        (int) (innerWidth * r2dm.getZoomFactor()),
        (int) (innerHeight * r2dm.getZoomFactor()));
    graphics.setColor(bondColor);
  }


  /**
   * Paint a Bond in an aromatic ring, using CDK style, meaning grey inner bonds.
   *
   * @param  bond       Description of Parameter
   * @param  ring       Description of Parameter
   * @param  bondColor  Description of Parameter
   * @param  graphics   Description of Parameter
   */
  public void paintAromaticRingBondCDKStyle(Bond bond, Ring ring, Color bondColor, Graphics graphics) {
    paintSingleBond(bond, bondColor, graphics);
    paintInnerBond(bond, ring, Color.lightGray, graphics);
  }


  /**
   * Paints the given single bond.
   *
   * @param  bond       The single bond to be drawn
   * @param  bondColor  Description of the Parameter
   * @param  graphics   Description of Parameter
   */
  public void paintSingleBond(Bond bond, Color bondColor, Graphics graphics) {
    if (GeometryTools.has2DCoordinates(bond)) {
      paintOneBond(GeometryTools.getBondCoordinates(bond), bondColor, graphics);
    }
  }


  /**
   * Paints The given double bond.
   *
   * @param  bond       The double bond to be drawn
   * @param  bondColor  Description of the Parameter
   * @param  graphics   Description of Parameter
   */
  public void paintDoubleBond(Bond bond, Color bondColor, Graphics graphics) {
    int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond), r2dm.getBondDistance() / 2);

    int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
    paintOneBond(newCoords1, bondColor, graphics);

    int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
    paintOneBond(newCoords2, bondColor, graphics);
  }


  /**
   * Paints the given triple bond.
   *
   * @param  bond       The triple bond to be drawn
   * @param  bondColor  Description of the Parameter
   * @param  graphics   Description of Parameter
   */
  public void paintTripleBond(Bond bond, Color bondColor, Graphics graphics) {
    paintSingleBond(bond, bondColor, graphics);

    int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond), (r2dm.getBondWidth() / 2 + r2dm.getBondDistance()));

    int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
    paintOneBond(newCoords1, bondColor, graphics);

    int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
    paintOneBond(newCoords2, bondColor, graphics);
  }


  /**
   * Paints the inner bond of a double bond that is part of a ring.
   *
   * @param  bond       The bond to be drawn
   * @param  ring       The ring the bond is part of
   * @param  bondColor  Color of the bond
   * @param  graphics   Description of Parameter
   */
  public void paintInnerBond(Bond bond, Ring ring, Color bondColor, Graphics graphics) {
    Point2d center = ring.get2DCenter();

    int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond), (r2dm.getBondWidth() / 2 + r2dm.getBondDistance()));
    double dist1 = Math.sqrt(Math.pow((coords[0] - center.x), 2) + Math.pow((coords[1] - center.y), 2));
    double dist2 = Math.sqrt(Math.pow((coords[2] - center.x), 2) + Math.pow((coords[3] - center.y), 2));
    if (dist1 < dist2) {
      int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
      paintOneBond(shortenBond(newCoords1, ring.getRingSize()), bondColor, graphics);
    } else {
      int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
      paintOneBond(shortenBond(newCoords2, ring.getRingSize()), bondColor, graphics);
    }
  }


  /**
   * Really paints the bond. It is triggered by all the other paintbond methods
   * to draw a polygon as wide as bond width.
   *
   * @param  coords
   * @param  bondColor  Color of the bond
   * @param  graphics   Description of Parameter
   */
  public void paintOneBond(int[] coords, Color bondColor, Graphics graphics) {
    graphics.setColor(bondColor);
    int[] newCoords = GeometryTools.distanceCalculator(coords, r2dm.getBondWidth() / 2);
    int[] xCoords = {newCoords[0], newCoords[2], newCoords[4], newCoords[6]};
    int[] yCoords = {newCoords[1], newCoords[3], newCoords[5], newCoords[7]};
    xCoords = getScreenCoordinates(xCoords);
    yCoords = getScreenCoordinates(yCoords);
    graphics.fillPolygon(xCoords, yCoords, 4);
  }


  /**
   * Paints the given bond as a wedge bond.
   *
   * @param  bond       The singlebond to be drawn
   * @param  bondColor  Color of the bond
   * @param  graphics   Description of Parameter
   */
  public void paintWedgeBond(Bond bond, Color bondColor, Graphics graphics) {
    double wedgeWidth = r2dm.getBondWidth() * 2.0;
    // this value should be made customazible

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
   * Paints the given bond as a dashed wedge bond.
   *
   * @param  bond       The single bond to be drawn
   * @param  bondColor  Color of the bond
   * @param  graphics   Description of Parameter
   */
  public void paintDashedWedgeBond(Bond bond, Color bondColor, Graphics graphics) {
    graphics.setColor(bondColor);

    double bondLength = bond.getLength();
    int numberOfLines = (int) (bondLength / 4.0);
    // this value should be made customizable
    double wedgeWidth = r2dm.getBondWidth() * 2.0;
    // this value should be made customazible

    double widthStep = wedgeWidth / (double) numberOfLines;
    Point2d p1 = bond.getAtomAt(0).getPoint2D();
    Point2d p2 = bond.getAtomAt(1).getPoint2D();
    if (bond.getStereo() == CDKConstants.STEREO_BOND_DOWN_INV) {
      // draw the wedge bond the other way around
      p1 = bond.getAtomAt(1).getPoint2D();
      p2 = bond.getAtomAt(0).getPoint2D();
    }
    Vector2d lengthStep = new Vector2d(p2);
    lengthStep.sub(p1);
    lengthStep.scale(1.0 / numberOfLines);
    Vector2d p = GeometryTools.calculatePerpendicularUnitVector(p1, p2);

    Point2d currentPoint = new Point2d(p1);
    Point2d q1 = new Point2d();
    Point2d q2 = new Point2d();
    for (int i = 0; i <= numberOfLines; ++i) {
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
   *  Paints a line between the start point and end point of the pointer vector that
   *  is stored in the Renderer2DModel.
   *
   * @param  graphics  Description of Parameter
   */
  public void paintPointerVector(Graphics graphics) {
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
   *  Gets the screenCoordinates attribute of the Renderer2D object
   *
   * @param  p  Description of Parameter
   * @return    The screenCoordinates value
   */
  private Point getScreenCoordinates(Point p) {
    Point screenCoordinate = new Point();
    double zoomFactor = r2dm.getZoomFactor();
    screenCoordinate.x = (int) ((double) p.x * zoomFactor);
    screenCoordinate.y = (int) ((double) p.y * zoomFactor);
    return screenCoordinate;
  }


  /**
   *  Gets the screenCoordinates attribute of the Renderer2D object
   *
   * @param  coords  Description of Parameter
   * @return         The screenCoordinates value
   */
  private int[] getScreenCoordinates(int[] coords) {
    int[] screenCoordinates = new int[coords.length];
    double zoomFactor = r2dm.getZoomFactor();
    for (int i = 0; i < coords.length; i++) {
      screenCoordinates[i] = (int) ((double) coords[i] * zoomFactor);
    }
    return screenCoordinates;
  }


  /**
   *  Gets the screenSize attribute of the Renderer2D object
   *
   * @param  size  Description of Parameter
   * @return       The screenSize value
   */
  private float getScreenSize(int size) {
    return (float) size * (float) r2dm.getZoomFactor();
  }


  /**
   * Calculates the coordinates for the inner bond of a doublebond that is part
   * of a ring. It is drawn shorter than a normal bond.
   *
   * @param  coords  The original coordinates of the bond
   * @param  edges   Number of edges of the ring it is part of
   * @return         The calculated coordinates of the now shorter bond
   */
  private int[] shortenBond(int[] coords, int edges) {
    int xDiff = (coords[0] - coords[2]) / (edges * 2);
    int yDiff = (coords[1] - coords[3]) / (edges * 2);
    int[] newCoords = {coords[0] - xDiff, coords[1] - yDiff, coords[2] + xDiff, coords[3] + yDiff};
    return newCoords;
  }
}

