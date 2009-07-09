/*  Copyright (C) 2008-2009  Gilleain Torrance <gilleain.torrance@gmail.com>
 *                2008-2009  Arvid Berg <goglepox@users.sf.net>
 *                     2009  Stefan Kuhn <shk3@users.sf.net>
 *                     2009  Egon Willighagen <egonw@users.sf.net>
*
*  Contact: cdk-devel@list.sourceforge.net
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public License
*  as published by the Free Software Foundation; either version 2.1
*  of the License, or (at your option) any later version.
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

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.font.IFontManager;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IReactionGenerator;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;

/**
 * A general renderer for {@link IChemModel}s, {@link IReaction}s, and
 * {@link IMolecule}s. The chem object
 * is converted into a 'diagram' made up of {@link IRenderingElement}s. It takes
 * an {@link IDrawVisitor} to do the drawing of the generated diagram. Various
 * display properties can be set using the {@link RendererModel}.<p>
 *
 * This class has several usage patterns. For just painting fit-to-screen do:
 * <pre>
 *   renderer.paintMolecule(molecule, visitor, drawArea)
 * </pre>
 * for painting at a scale determined by the bond length in the RendererModel:
 * <pre>
 *   if (moleculeIsNew) {
 *     renderer.setup(molecule, drawArea);
 *   }
 *   Rectangle diagramSize = renderer.paintMolecule(molecule, visitor);
 *   // ...update scroll bars here
 * </pre>
 * to paint at full screen size, but not resize with each change:
 * <pre>
 *   if (moleculeIsNew) {
 *     renderer.setScale(molecule);
 *     Rectangle diagramBounds = renderer.calculateDiagramBounds(molecule);
 *     renderer.setZoomToFit(diagramBounds, drawArea);
 *     renderer.paintMolecule(molecule, visitor);
 *   } else {
 *     Rectangle diagramSize = renderer.paintMolecule(molecule, visitor);
 *   // ...update scroll bars here
 *   }
 * </pre>
 * finally, if you are scrolling, and have not changed the diagram:
 * <pre>
 *   renderer.repaint(visitor)
 * </pre>
 * will just repaint the previously generated diagram, at the same scale.<p>
 *
 * There are two sets of methods for painting IChemObjects - those that take
 * a Rectangle that represents the desired draw area, and those that return a
 * Rectangle that represents the actual draw area. The first are intended for
 * drawing molecules fitted to the screen (where 'screen' means any drawing
 * area) while the second type of method are for drawing bonds at the length
 * defined by the {@link RendererModel} parameter bondLength.<p>
 *
 * There are two numbers used to transform the model so that it fits on screen.
 * The first is <tt>scale</tt>, which is used to map model coordinates to
 * screen coordinates. The second is <tt>zoom</tt> which is used to, well,
 * zoom the on screen coordinates. If the diagram is fit-to-screen, then the
 * ratio of the bounds when drawn using bondLength and the bounds of
 * the screen is used as the zoom.<p>
 *
 * So, if the bond length on screen is set to 40, and the average bond length
 * of the model is 2 (unitless, but roughly &Aring;ngstrom scale) then the
 * scale will be 20. If the model is 10 units wide, then the diagram drawn at
 * 100% zoom will be 10 * 20 = 200 in width on screen. If the screen is 400
 * pixels wide, then fitting it to the screen will make the zoom 200%. Since the
 * zoom is just a floating point number, 100% = 1 and 200% = 2.
 *
 * @author maclean
 * @cdk.module renderextra
 */
public class Renderer extends AtomContainerRenderer {

	/**
	 * Generators specific to reactions
	 */
	private List<IReactionGenerator> reactionGenerators;
	
    /**
     * A renderer that generates diagrams using the specified
     * generators and manages fonts with the supplied font manager.
     *
     * @param generators
     *            a list of classes that implement the IGenerator interface
     * @param fontManager
     *            a class that manages mappings between zoom and font sizes
     */
	public Renderer(List<IGenerator> generators, IFontManager fontManager) {
        super(generators, fontManager);
    }
	
	public Renderer(List<IGenerator> generators, 
	                List<IReactionGenerator> reactionGenerators, 
	                IFontManager fontManager) {
	    this(generators, fontManager);
        this.reactionGenerators = reactionGenerators;
        this.setup();
	}
	
	/**
	 * Setup the transformations necessary to draw this Chem Model.
	 *
	 * @param chemModel
	 * @param screen
	 */
	public void setup(IChemModel chemModel, Rectangle screen) {
	    this.setScale(chemModel);
	    Rectangle2D bounds = Renderer.calculateBounds(chemModel);
	    this.modelCenter = new Point2d(bounds.getCenterX(), bounds.getCenterY());
	    this.drawCenter = new Point2d(screen.getCenterX(), screen.getCenterY());
	    this.setup();
	}

	/**
	 * Setup the transformations necessary to draw this Reaction Set.
	 *
	 * @param reactionSet
	 * @param screen
	 */
	public void setup(IReactionSet reactionSet, Rectangle screen) {
	    this.setScale(reactionSet);
	    Rectangle2D bounds = Renderer.calculateBounds(reactionSet);
        this.modelCenter = new Point2d(bounds.getCenterX(), bounds.getCenterY());
        this.drawCenter = new Point2d(screen.getCenterX(), screen.getCenterY());
        this.setup();
	}

	/**
	 * Setup the transformations necessary to draw this Reaction.
	 *
	 * @param reaction
	 * @param screen
	 */
	public void setup(IReaction reaction, Rectangle screen) {
        this.setScale(reaction);
        Rectangle2D bounds = Renderer.calculateBounds(reaction);
        this.modelCenter = new Point2d(bounds.getCenterX(), bounds.getCenterY());
        this.drawCenter = new Point2d(screen.getCenterX(), screen.getCenterY());
        this.setup();
    }

    public void reset() {
        modelCenter = new Point2d(0, 0);
        drawCenter = new Point2d(200, 200);
        zoom = 1.0;
        setup();
    }

    /**
     * Determine the overlap of the diagram with the screen, and shift (if
     * necessary) the diagram draw center. It returns a rectangle only because
     * that is a convenient class to hold the four parameters calculated, but it
     * is not a rectangle representing an area...
     *
     * @param screenBounds
     *            the bounds of the screen
     * @param diagramBounds
     *            the bounds of the diagram
     * @return the shape that the screen should be
     */
    public Rectangle shift(Rectangle screenBounds, Rectangle diagramBounds) {
        int screenMaxX  = screenBounds.x + screenBounds.width;
        int screenMaxY  = screenBounds.y + screenBounds.height;
        int diagramMaxX = diagramBounds.x + diagramBounds.width;
        int diagramMaxY = diagramBounds.y + diagramBounds.height;

        int leftOverlap   = screenBounds.x - diagramBounds.x;
        int rightOverlap  = diagramMaxX - screenMaxX;
        int topOverlap    = screenBounds.y - diagramBounds.y;
        int bottomOverlap = diagramMaxY - screenMaxY;

        int dx = 0;
        int dy = 0;
        int w = screenBounds.width;
        int h = screenBounds.height;

        if (leftOverlap > 0) {
            dx = leftOverlap;
        }

        if (rightOverlap > 0) {
            w += rightOverlap;
        }

        if (topOverlap > 0) {
            dy = topOverlap;
        }

        if (bottomOverlap > 0) {
            h += bottomOverlap;
        }

        if (dx != 0 || dy != 0) {
            this.shiftDrawCenter(dx, dy);
        }

        return new Rectangle(dx, dy, w, h);
    }

	/**
	 * Set the scale for an IChemModel. It calculates the average bond length of
	 * the model and calculates the multiplication factor to transform this
	 * to the bond length that is set in the RendererModel.
	 *
	 * @param chemModel
	 */
	public void setScale(IChemModel chemModel) {
	    double bondLength = Renderer.calculateAverageBondLength(chemModel);
	    this.scale = this.calculateScaleForBondLength(bondLength);

	    // store the scale so that other components can access it
	    this.rendererModel.setScale(scale);
	}

	/**
	 * Set the scale for an IReactionSet. It calculates the average bond length
	 * of the model and calculates the multiplication factor to transform this
     * to the bond length that is set in the RendererModel.
     *
	 * @param reactionSet
	 */
	public void setScale(IReactionSet reactionSet) {
        double bondLength = Renderer.calculateAverageBondLength(reactionSet);
        this.scale = this.calculateScaleForBondLength(bondLength);

        // store the scale so that other components can access it
        this.rendererModel.setScale(scale);
    }

    /**
     * Set the scale for an IReaction. It calculates the average bond length
     * of the model and calculates the multiplication factor to transform this
     * to the bond length that is set in the RendererModel.
     * @param reaction
     */
    public void setScale(IReaction reaction) {
        double bondLength = Renderer.calculateAverageBondLength(reaction);
        this.scale = this.calculateScaleForBondLength(bondLength);

        // store the scale so that other components can access it
        this.rendererModel.setScale(scale);
    }

    /**
     * Set the scale for an IMoleculeSet. It calculates the average bond length
     * of the model and calculates the multiplication factor to transform this
     * to the bond length that is set in the RendererModel.
     * @param moleculeSet
     */
    public void setScale(IMoleculeSet moleculeSet) {
        double bondLength = Renderer.calculateAverageBondLength(moleculeSet);
        this.scale = this.calculateScaleForBondLength(bondLength);

        // store the scale so that other components can access it
        this.rendererModel.setScale(scale);
    }

    /**
     * Paint an IChemModel using the IDrawVisitor at a scale determined by the
     * bond length in RendererModel.
     *
     * @param chemModel
     *            the chem model to draw
     * @param drawVisitor
     *            the visitor used to draw with
     * @return the rectangular area that the diagram will occupy on screen
     */
    public Rectangle paintChemModel(
            IChemModel chemModel, IDrawVisitor drawVisitor) {

        IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
        IReactionSet reactionSet = chemModel.getReactionSet();

        if (moleculeSet == null && reactionSet != null) {
            return paintReactionSet(reactionSet, drawVisitor);
        }

        if (moleculeSet != null && reactionSet == null) {
            return paintMoleculeSet(moleculeSet, drawVisitor);
        }

        if (moleculeSet != null && reactionSet != null) {
            Rectangle2D totalBounds = Renderer.calculateBounds(reactionSet);
            totalBounds = totalBounds.createUnion(
                    Renderer.calculateBounds(moleculeSet));
            this.setupTransformNatural(totalBounds);
            ElementGroup diagram = new ElementGroup();
            for (IReaction reaction : reactionSet.reactions()) {
                diagram.add(this.generateDiagram(reaction));
            }
            diagram.add(this.generateDiagram(moleculeSet));
            this.paint(drawVisitor, diagram);

            // the size of the painted diagram is returned
            return this.convertToDiagramBounds(totalBounds);
        }
        return new Rectangle(0, 0, 0, 0);
    }

	public Rectangle paintReactionSet(
            IReactionSet reactionSet, IDrawVisitor drawVisitor) {
        // total up the bounding boxes
        Rectangle2D totalBounds = new Rectangle2D.Double();
        for (IReaction reaction : reactionSet.reactions()) {
            Rectangle2D modelBounds = Renderer.calculateBounds(reaction);
            if (totalBounds == null) {
                totalBounds = modelBounds;
            } else {
                totalBounds = totalBounds.createUnion(modelBounds);
            }
        }

        // setup and draw
        this.setupTransformNatural(totalBounds);
        ElementGroup diagram = new ElementGroup();
        for (IReaction reaction : reactionSet.reactions()) {
            diagram.add(this.generateDiagram(reaction));
        }
        this.paint(drawVisitor, diagram);

        // the size of the painted diagram is returned
        return this.convertToDiagramBounds(totalBounds);
    }

	public Rectangle paintReaction(
	        IReaction reaction, IDrawVisitor drawVisitor) {

        // calculate the bounds
        Rectangle2D modelBounds = Renderer.calculateBounds(reaction);

        // setup and draw
        this.setupTransformNatural(modelBounds);
        IRenderingElement diagram = this.generateDiagram(reaction);
        this.paint(drawVisitor, diagram);

        return this.convertToDiagramBounds(modelBounds);
    }

	public Rectangle paintMoleculeSet(
            IMoleculeSet moleculeSet, IDrawVisitor drawVisitor) {
        // total up the bounding boxes
        Rectangle2D totalBounds = new Rectangle2D.Double();
        for (IAtomContainer molecule : moleculeSet.molecules()) {
            Rectangle2D modelBounds = Renderer.calculateBounds(molecule);
            if (totalBounds == null) {
                totalBounds = modelBounds;
            } else {
                totalBounds = totalBounds.createUnion(modelBounds);
            }
        }

        // setup and draw
        this.setupTransformNatural(totalBounds);
        ElementGroup diagram = new ElementGroup();
        for (IAtomContainer molecule : moleculeSet.molecules()) {
            diagram.add(this.generateDiagram(molecule));
        }
        this.paint(drawVisitor, diagram);

        return this.convertToDiagramBounds(totalBounds);
    }

	/**
     * Paint a ChemModel.
     *
     * @param chemModel
     * @param drawVisitor the visitor that does the drawing
     * @param bounds the bounds of the area to paint on.
     * @param resetCenter
     *     if true, set the modelCenter to the center of the ChemModel's bounds.
     */
    public void paintChemModel(IChemModel chemModel,
            IDrawVisitor drawVisitor, Rectangle2D bounds, boolean resetCenter) {
        // check for an empty model
        IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
        IReactionSet reactionSet = chemModel.getReactionSet();

        // nasty, but it seems that reactions can be read in as ChemModels
        // with BOTH a ReactionSet AND a MoleculeSet...
        if (moleculeSet == null || reactionSet != null) {
            if (reactionSet != null) {
                paintReactionSet(reactionSet, drawVisitor, bounds, resetCenter);
            }
            return;
        }

        // calculate the total bounding box
        Rectangle2D modelBounds = Renderer.calculateBounds(moleculeSet);

        this.setupTransformToFit(bounds, modelBounds,
                Renderer.calculateAverageBondLength(chemModel), resetCenter);

        // generate the elements
        IRenderingElement diagram = this.generateDiagram(moleculeSet);

        // paint it
        this.paint(drawVisitor, diagram);
    }

    /**
     * Paint a set of reactions.
     *
     * @param reaction the reaction to paint
     * @param drawVisitor the visitor that does the drawing
     * @param bounds the bounds on the screen
     * @param resetCenter
     *     if true, set the draw center to be the center of bounds
     */
    public void paintReactionSet(IReactionSet reactionSet,
            IDrawVisitor drawVisitor, Rectangle2D bounds, boolean resetCenter) {

        // total up the bounding boxes
        Rectangle2D totalBounds = null;
        for (IReaction reaction : reactionSet.reactions()) {
            Rectangle2D modelBounds = Renderer.calculateBounds(reaction);
            if (totalBounds == null) {
                totalBounds = modelBounds;
            } else {
                totalBounds = totalBounds.createUnion(modelBounds);
            }
        }

        this.setupTransformToFit(bounds, totalBounds,
                Renderer.calculateAverageBondLength(reactionSet), resetCenter);

        ElementGroup diagram = new ElementGroup();
        for (IReaction reaction : reactionSet.reactions()) {
            diagram.add(this.generateDiagram(reaction));
        }

        // paint them all
        this.paint(drawVisitor, diagram);
    }

    /**
	 * Paint a reaction.
	 *
	 * @param reaction the reaction to paint
	 * @param drawVisitor the visitor that does the drawing
	 * @param bounds the bounds on the screen
	 * @param resetCenter
	 *     if true, set the draw center to be the center of bounds
	 */
	public void paintReaction(IReaction reaction, IDrawVisitor drawVisitor,
            Rectangle2D bounds, boolean resetCenter) {

	    // calculate the bounds
        Rectangle2D modelBounds = Renderer.calculateBounds(reaction);

        this.setupTransformToFit(bounds, modelBounds,
                Renderer.calculateAverageBondLength(reaction), resetCenter);

        // generate the elements
        IRenderingElement diagram = this.generateDiagram(reaction);

        // paint it
        this.paint(drawVisitor, diagram);
    }

	/**
     * Paint a set of molecules.
     *
     * @param reaction the reaction to paint
     * @param drawVisitor the visitor that does the drawing
     * @param bounds the bounds on the screen
     * @param resetCenter
     *     if true, set the draw center to be the center of bounds
     */
    public void paintMoleculeSet(IMoleculeSet molecules,
            IDrawVisitor drawVisitor, Rectangle2D bounds, boolean resetCenter) {

        // total up the bounding boxes
        Rectangle2D totalBounds = null;
        for (IAtomContainer molecule : molecules.molecules()) {
            Rectangle2D modelBounds = Renderer.calculateBounds(molecule);
            if (totalBounds == null) {
                totalBounds = modelBounds;
            } else {
                totalBounds = totalBounds.createUnion(modelBounds);
            }
        }

        this.setupTransformToFit(bounds, totalBounds,
                Renderer.calculateAverageBondLength(molecules), resetCenter);

        ElementGroup diagram = new ElementGroup();
        for (IAtomContainer molecule : molecules.molecules()) {
            diagram.add(this.generateDiagram(molecule));
        }

        this.paint(drawVisitor, diagram);
    }

    /**
     * Repaint using the cached diagram
     *
     * @param drawVisitor the wrapper for the graphics object that draws
     */
    public void repaint(IDrawVisitor drawVisitor) {
        this.paint(drawVisitor, cachedDiagram);
    }

    /**
	 * Given a chem model, calculates the bounding rectangle in screen space.
	 *
	 * @param model the model to draw.
	 * @return a rectangle in screen space.
	 */
	public Rectangle calculateDiagramBounds(IChemModel model) {
        IMoleculeSet moleculeSet = model.getMoleculeSet();
        IReactionSet reactionSet = model.getReactionSet();
        if ((moleculeSet == null && reactionSet == null)) {
            return new Rectangle();
        }

        Rectangle2D moleculeBounds = null;
        Rectangle2D reactionBounds = null;
        if (moleculeSet != null) {
            moleculeBounds = Renderer.calculateBounds(moleculeSet);
        }
        if (reactionSet != null) {
            reactionBounds = Renderer.calculateBounds(reactionSet);
        }

        if (moleculeBounds == null) {
            return this.calculateScreenBounds(reactionBounds);
        } else if (reactionBounds == null) {
            return this.calculateScreenBounds(moleculeBounds);
        } else {
            Rectangle2D allbounds = new Rectangle2D.Double();
            Rectangle2D.union(moleculeBounds, reactionBounds, allbounds);
            return this.calculateScreenBounds(allbounds);
        }
    }

	public Rectangle calculateDiagramBounds(IReactionSet reactionSet) {
        return this.calculateScreenBounds(
                Renderer.calculateBounds(reactionSet));
	}

	public Rectangle calculateDiagramBounds(IReaction reaction) {
        return this.calculateScreenBounds(
                Renderer.calculateBounds(reaction));
	}

	public Rectangle calculateDiagramBounds(IMoleculeSet moleculeSet) {
	    return this.calculateScreenBounds(
	               Renderer.calculateBounds(moleculeSet));
	}

	public static Rectangle2D calculateBounds(IChemModel chemModel) {
        IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
        IReactionSet reactionSet = chemModel.getReactionSet();
        Rectangle2D totalBounds = null;
        if (moleculeSet != null) {
            totalBounds = Renderer.calculateBounds(moleculeSet);
        }

        if (reactionSet != null) {
            if (totalBounds == null) {
                totalBounds = Renderer.calculateBounds(reactionSet);
            } else {
                totalBounds = totalBounds.createUnion(
                        Renderer.calculateBounds(reactionSet));
            }
        }
        return totalBounds;
    }

    public static Rectangle2D calculateBounds(IReactionSet reactionSet) {
        Rectangle2D totalBounds = new Rectangle2D.Double();
        for (IReaction reaction : reactionSet.reactions()) {
            Rectangle2D reactionBounds = Renderer.calculateBounds(reaction);
            if (totalBounds.isEmpty()) {
                totalBounds = reactionBounds;
            } else {
                Rectangle2D.union(totalBounds, reactionBounds, totalBounds);
            }
        }
        return totalBounds;
    }

    public static Rectangle2D calculateBounds(IReaction reaction) {
        // get the participants in the reaction
        IMoleculeSet reactants = reaction.getReactants();
        IMoleculeSet products = reaction.getProducts();
        if (reactants == null || products == null) return null;

        // determine the bounds of everything in the reaction
        Rectangle2D reactantsBounds = Renderer.calculateBounds(reactants);
        return reactantsBounds.createUnion(Renderer.calculateBounds(products));
    }

    public static Rectangle2D calculateBounds(IMoleculeSet moleculeSet) {
        Rectangle2D totalBounds = new Rectangle2D.Double();
        for (int i = 0; i < moleculeSet.getAtomContainerCount(); i++) {
            IAtomContainer ac = moleculeSet.getAtomContainer(i);
            Rectangle2D acBounds = Renderer.calculateBounds(ac);
            if (totalBounds.isEmpty()) {
                totalBounds = acBounds;
            } else {
                Rectangle2D.union(totalBounds, acBounds, totalBounds);
            }
        }
        return totalBounds;
    }

    /**
     *
     *
     *  @param model the model for which to calculate the average bond length
     */
    public static double calculateAverageBondLength(IChemModel model) {

        // empty models have to have a scale
        IMoleculeSet moleculeSet = model.getMoleculeSet();
        if (moleculeSet == null) {
            IReactionSet reactionSet = model.getReactionSet();
            if (reactionSet != null) {
                return Renderer.calculateAverageBondLength(reactionSet);
            }
            return 0.0;
        }

        return Renderer.calculateAverageBondLength(moleculeSet);
    }

    public static double calculateAverageBondLength(IReactionSet reactionSet) {
        double averageBondModelLength = 0.0;
        for (IReaction reaction : reactionSet.reactions()) {
            averageBondModelLength +=
                Renderer.calculateAverageBondLength(reaction);
        }
        return averageBondModelLength / reactionSet.getReactionCount();
    }

    public static double calculateAverageBondLength(IReaction reaction) {

        IMoleculeSet reactants = reaction.getReactants();
        double reactantAverage = 0.0;
        if (reactants != null) {
            reactantAverage =
                Renderer.calculateAverageBondLength(reactants) /
                reactants.getAtomContainerCount();
        }

        IMoleculeSet products = reaction.getProducts();
        double productAverage = 0.0;
        if (products != null) {
            productAverage =
                Renderer.calculateAverageBondLength(products) /
                products.getAtomContainerCount();
        }

        if (productAverage == 0.0 && reactantAverage == 0.0) {
            return 1.0;
        } else {
            return (productAverage + reactantAverage) / 2.0;
        }
    }

    public static double calculateAverageBondLength(IMoleculeSet moleculeSet) {
        double averageBondModelLength = 0.0;
        for (IAtomContainer atomContainer : moleculeSet.molecules()) {
            averageBondModelLength +=
                GeometryTools.getBondLengthAverage(atomContainer);
        }
        return averageBondModelLength / moleculeSet.getAtomContainerCount();
    }

    public RendererModel getRenderer2DModel() {
		return this.rendererModel;
	}

    public Point2d toModelCoordinates(int screenX, int screenY) {
        try {
            double[] dest = new double[2];
            double[] src = new double[] { screenX, screenY };
            transform.inverseTransform(src, 0, dest, 0, 1);
            return new Point2d(dest[0], dest[1]);
        } catch (NoninvertibleTransformException n) {
            return new Point2d(0,0);
        }
    }

    public Point2d toScreenCoordinates(double modelX, double modelY) {
        double[] dest = new double[2];
        transform.transform(new double[] { modelX, modelY }, 0, dest, 0, 1);
        return new Point2d(dest[0], dest[1]);
    }

	public void setModelCenter(double x, double y) {
	    this.modelCenter = new Point2d(x, y);
	    setup();
	}

	public void setDrawCenter(double x, double y) {
        this.drawCenter = new Point2d(x, y);
        setup();
    }

	public void setZoom(double z) {
	    getRenderer2DModel().setZoomFactor( z );
	    zoom = z;
	    setup();
	}

    /**
     * Move the draw center by dx and dy.
     *
     * @param dx
     *            the x shift
     * @param dy
     *            the y shift
     */
	public void shiftDrawCenter(double dx, double dy) {
	    this.drawCenter.set(this.drawCenter.x + dx, this.drawCenter.y + dy);
	    setup();
	}

	public Point2d getDrawCenter() {
        return drawCenter;
    }

    public Point2d getModelCenter() {
        return modelCenter;
    }

    /**
     * Calculate and set the zoom factor needed to completely fit the diagram
     * onto the screen bounds.
     *
     * @param diagramBounds
     * @param drawBounds
     */
    public void setZoomToFit(double drawWidth,
                             double drawHeight,
                             double diagramWidth,
                             double diagramHeight) {

        double m = this.rendererModel.getMargin();

        // determine the zoom needed to fit the diagram to the screen
        double widthRatio  = drawWidth  / (diagramWidth  + (2 * m));
        double heightRatio = drawHeight / (diagramHeight + (2 * m));

        this.zoom = Math.min(widthRatio, heightRatio);

        this.fontManager.setFontForZoom(zoom);

        // record the zoom in the model, so that generators can use it
        this.rendererModel.setZoomFactor(zoom);

    }

    /**
     * The target method for paintChemModel, paintReaction, and paintMolecule.
     *
     * @param drawVisitor
     *            the visitor to draw with
     * @param diagram
     *            the IRenderingElement tree to render
     */
	private void paint(IDrawVisitor drawVisitor,
	                   IRenderingElement diagram) {
	    if (diagram == null) return;

	    // cache the diagram for quick-redraw
	    this.cachedDiagram = diagram;

	    this.fontManager.setFontName(this.rendererModel.getFontName());
	    this.fontManager.setFontStyle(this.rendererModel.getFontStyle());

	    drawVisitor.setFontManager(this.fontManager);
	    drawVisitor.setTransform(this.transform);
	    drawVisitor.setRendererModel(this.rendererModel);
	    diagram.accept(drawVisitor);
	}

	/**
	 * Set the transform for a non-fit to screen paint.
	 *
	 * @param modelBounds
     *            the bounding box of the model
	 */
	private void setupTransformNatural(Rectangle2D modelBounds) {
	    this.zoom = this.rendererModel.getZoomFactor();
        this.fontManager.setFontForZoom(zoom);
        this.setup();
	}

    /**
     * Sets the transformation needed to draw the model on the canvas when
     * the diagram needs to fit the screen.
     *
     * @param screenBounds
     *            the bounding box of the draw area
     * @param modelBounds
     *            the bounding box of the model
     * @param bondLength
     *            the average bond length of the model
     * @param reset
     *            if true, model center will be set to the modelBounds center
     *            and the scale will be re-calculated
     */
	private void setupTransformToFit(Rectangle2D screenBounds,
	                            Rectangle2D modelBounds,
	                            double bondLength,
	                            boolean reset) {

	    if (screenBounds == null) return;

        this.setDrawCenter(
                screenBounds.getCenterX(), screenBounds.getCenterY());

        this.scale = this.calculateScaleForBondLength(bondLength);

        double drawWidth = screenBounds.getWidth();
        double drawHeight = screenBounds.getHeight();

        double diagramWidth = modelBounds.getWidth() * scale;
        double diagramHeight = modelBounds.getHeight() * scale;

        this.setZoomToFit(drawWidth, drawHeight, diagramWidth, diagramHeight);

	    // this controls whether editing a molecule causes it to re-center
	    // with each change or not
	    if (reset || rendererModel.isFitToScreen()) {
            this.setModelCenter(
                    modelBounds.getCenterX(), modelBounds.getCenterY());
        }

	    // set the scale in the renderer model for the generators
	    if (reset) {
	        this.rendererModel.setScale(scale);
	    }

	    this.setup();
	}

	/**
	 * Given a bond length for a model, calculate the scale that will transform
	 * this length to the on screen bond length in RendererModel.
	 *
	 * @param modelBondLength
	 * @param reset
	 * @return
	 */
	private double calculateScaleForBondLength(double modelBondLength) {
	    if (Double.isNaN(modelBondLength) || modelBondLength == 0) {
            return Renderer.DEFAULT_SCALE;
        } else {
            return this.rendererModel.getBondLength() / modelBondLength;
        }
	}

    /**
     * Calculate the bounds of the diagram on screen, given the current scale,
     * zoom, and margin.
     *
     * @param modelBounds
     *            the bounds in model space of the chem object
     * @return the bounds in screen space of the drawn diagram
     */
	private Rectangle convertToDiagramBounds(Rectangle2D modelBounds) {
	    double cx = modelBounds.getCenterX();
        double cy = modelBounds.getCenterY();
        double mw = modelBounds.getWidth();
        double mh = modelBounds.getHeight();

        Point2d mc = this.toScreenCoordinates(cx, cy);

        // special case for 0 or 1 atoms
        if (mw == 0 && mh == 0) {
            return new Rectangle((int)mc.x, (int)mc.y, 0, 0);
        }

        double margin = this.rendererModel.getMargin();
        int w = (int) ((scale * zoom * mw) + (2 * margin));
        int h = (int) ((scale * zoom * mh) + (2 * margin));
        int x = (int) (mc.x - w / 2);
        int y = (int) (mc.y - h / 2);

        return new Rectangle(x, y, w, h);
	}

	private void setup() {

        // set the transform
        try {
            this.transform = new AffineTransform();
            this.transform.translate(this.drawCenter.x, this.drawCenter.y);
            this.transform.scale(this.scale, this.scale);
            this.transform.scale(this.zoom, this.zoom);
            this.transform.translate(-this.modelCenter.x, -this.modelCenter.y);
//            System.err.println(String.format(
//                    "drawCenter=%s scale=%s zoom=%s modelCenter=%s",
//                    this.drawCenter,
//                    this.scale,
//                    this.zoom,
//                    this.modelCenter));
        } catch (NullPointerException npe) {
            // one of the drawCenter or modelCenter points have not been set!
            System.err.println(String.format(
                    "null pointer when setting transform: " +
                    "drawCenter=%s scale=%s zoom=%s modelCenter=%s",
                    this.drawCenter,
                    this.scale,
                    this.zoom,
                    this.modelCenter));
        }
	}

    private IRenderingElement generateDiagram(IReaction reaction) {
	    ElementGroup diagram = new ElementGroup();
	    
	    for (IReactionGenerator generator : this.reactionGenerators) {
	        diagram.add(generator.generate(reaction, rendererModel));
	    }

	    diagram.add(generateDiagram(reaction.getReactants()));
	    diagram.add(generateDiagram(reaction.getProducts()));

	    return diagram;
	}

	private IRenderingElement generateDiagram(IMoleculeSet moleculeSet) {
	    ElementGroup diagram = new ElementGroup();
        for (int i = 0; i < moleculeSet.getAtomContainerCount(); i++) {
            IAtomContainer ac = moleculeSet.getAtomContainer(i);
            for (IGenerator generator : this.generators) {
                diagram.add(generator.generate(ac, this.rendererModel));
            }
        }
        return diagram;
	}

	public List<IGenerator> getGenerators(){
	    return new ArrayList<IGenerator>(generators);
	}
}
