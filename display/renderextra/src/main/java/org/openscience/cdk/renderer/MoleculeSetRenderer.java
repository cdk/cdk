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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.font.IFontManager;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.BondLength;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;

/**
 * A general renderer for {@link IChemModel}s, {@link IReaction}s, and
 * {@link IAtomContainer}s. The chem object
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
 * <p>There are two sets of methods for painting IChemObjects - those that take
 * a Rectangle that represents the desired draw area, and those that return a
 * Rectangle that represents the actual draw area. The first are intended for
 * drawing molecules fitted to the screen (where 'screen' means any drawing
 * area) while the second type of method are for drawing bonds at the length
 * defined by the {@link RendererModel} parameter bondLength.<p>
 *
 * <p>There are two numbers used to transform the model so that it fits on screen.
 * The first is <tt>scale</tt>, which is used to map model coordinates to
 * screen coordinates. The second is <tt>zoom</tt> which is used to, well,
 * zoom the on screen coordinates. If the diagram is fit-to-screen, then the
 * ratio of the bounds when drawn using bondLength and the bounds of
 * the screen is used as the zoom.<p>
 *
 * <p>So, if the bond length on screen is set to 40, and the average bond length
 * of the model is 2 (unitless, but roughly &Aring;ngstrom scale) then the
 * scale will be 20. If the model is 10 units wide, then the diagram drawn at
 * 100% zoom will be 10 * 20 = 200 in width on screen. If the screen is 400
 * pixels wide, then fitting it to the screen will make the zoom 200%. Since the
 * zoom is just a floating point number, 100% = 1 and 200% = 2.
 *
 * @author maclean
 * @cdk.module renderextra
 * @cdk.githash
 */
public class MoleculeSetRenderer extends AbstractRenderer<IAtomContainerSet> implements IRenderer<IAtomContainerSet> {

    private IRenderer<IAtomContainer> atomContainerRenderer;

    /**
     * A renderer that generates diagrams using the specified
     * generators and manages fonts with the supplied font manager.
     *
     * @param generators
     *            a list of classes that implement the IGenerator interface
     * @param fontManager
     *            a class that manages mappings between zoom and font sizes
     */
    public MoleculeSetRenderer(List<IGenerator<IAtomContainer>> generators, IFontManager fontManager) {
        this(new RendererModel(), generators, fontManager);
    }

    public MoleculeSetRenderer(RendererModel rendererModel, List<IGenerator<IAtomContainer>> generators,
            IFontManager fontManager) {
        super(rendererModel);
        for (IGenerator<IAtomContainer> generator : generators) {
            rendererModel.registerParameters(generator);
        }
        this.fontManager = fontManager;
        atomContainerRenderer = new AtomContainerRenderer(rendererModel, generators, fontManager);
        this.generators = Collections.emptyList();
        this.setup();
    }

    /**
     * Setup the transformations necessary to draw this {@link IAtomContainerSet}.
     *
     * @param moleculeSet the {@link IAtomContainerSet} for what to set the scale
     * @param screen      the {@link Rectangle} for which to calculate the scale
     */
    @Override
    public void setup(IAtomContainerSet moleculeSet, Rectangle screen) {
        this.setScale(moleculeSet);
        Rectangle2D bounds = BoundsCalculator.calculateBounds(moleculeSet);
        if (bounds != null) this.modelCenter = new Point2d(bounds.getCenterX(), bounds.getCenterY());
        this.drawCenter = new Point2d(screen.getCenterX(), screen.getCenterY());
        this.setup();
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
    @Override
    public Rectangle shift(Rectangle screenBounds, Rectangle diagramBounds) {
        int screenMaxX = screenBounds.x + screenBounds.width;
        int screenMaxY = screenBounds.y + screenBounds.height;
        int diagramMaxX = diagramBounds.x + diagramBounds.width;
        int diagramMaxY = diagramBounds.y + diagramBounds.height;

        int leftOverlap = screenBounds.x - diagramBounds.x;
        int rightOverlap = diagramMaxX - screenMaxX;
        int topOverlap = screenBounds.y - diagramBounds.y;
        int bottomOverlap = diagramMaxY - screenMaxY;

        int diffx = 0;
        int diffy = 0;
        int width = screenBounds.width;
        int height = screenBounds.height;

        if (leftOverlap > 0) {
            diffx = leftOverlap;
        }

        if (rightOverlap > 0) {
            width += rightOverlap;
        }

        if (topOverlap > 0) {
            diffy = topOverlap;
        }

        if (bottomOverlap > 0) {
            height += bottomOverlap;
        }

        if (diffx != 0 || diffy != 0) {
            this.shiftDrawCenter(diffx, diffy);
        }

        return new Rectangle(diffx, diffy, width, height);
    }

    /**
     * Set the scale for an {@link IAtomContainerSet}. It calculates the average bond length
     * of the model and calculates the multiplication factor to transform this
     * to the bond length that is set in the RendererModel.
     *
     * @param moleculeSet the {@link IAtomContainerSet} for what to set the scale
     */
    @Override
    public void setScale(IAtomContainerSet moleculeSet) {
        double bondLength = AverageBondLengthCalculator.calculateAverageBondLength(moleculeSet);
        double scale = this.calculateScaleForBondLength(bondLength);

        // store the scale so that other components can access it
        this.rendererModel.getParameter(Scale.class).setValue(scale);
    }

    /** {@inheritDoc} */
    @Override
    public Rectangle paint(IAtomContainerSet moleculeSet, IDrawVisitor drawVisitor) {
        // total up the bounding boxes
        Rectangle2D totalBounds = BoundsCalculator.calculateBounds(moleculeSet);

        // setup and draw
        this.setupTransformNatural(totalBounds);

        IRenderingElement diagram = this.generateDiagram(moleculeSet);
        this.paint(drawVisitor, diagram);

        return this.convertToDiagramBounds(totalBounds);
    }

    /**
     * Paint a set of molecules.
     *
     * @param molecules   the {@link IAtomContainerSet} to paint
     * @param drawVisitor the visitor that does the drawing
     * @param bounds      the bounds on the screen
     * @param resetCenter
     *     if true, set the draw center to be the center of bounds
     */
    @Override
    public void paint(IAtomContainerSet molecules, IDrawVisitor drawVisitor, Rectangle2D bounds, boolean resetCenter) {

        // total up the bounding boxes
        Rectangle2D totalBounds = BoundsCalculator.calculateBounds(molecules);

        this.setupTransformToFit(bounds, totalBounds,
                AverageBondLengthCalculator.calculateAverageBondLength(molecules), resetCenter);

        IRenderingElement diagram = this.generateDiagram(molecules);
        this.paint(drawVisitor, diagram);
    }

    /** {@inheritDoc} */
    @Override
    public IRenderingElement generateDiagram(IAtomContainerSet molecules) {
        ElementGroup diagram = new ElementGroup();
        for (IAtomContainer molecule : molecules.atomContainers()) {
            diagram.add(atomContainerRenderer.generateDiagram(molecule));
        }
        return diagram;
    }

    /** {@inheritDoc} */
    @Override
    public Rectangle calculateDiagramBounds(IAtomContainerSet moleculeSet) {
        if (moleculeSet == null) return this.calculateScreenBounds(new Rectangle2D.Double());
        return this.calculateScreenBounds(BoundsCalculator.calculateBounds(moleculeSet));
    }

    /**
     * Given a bond length for a model, calculate the scale that will transform
     * this length to the on screen bond length in RendererModel.
     *
     * @param  modelBondLength the wanted model bond length in screen length
     * @return returns the scale that causes the drawn bond lengths in pixels to match
     *         the given model bond length
     */
    @Override
    public double calculateScaleForBondLength(double modelBondLength) {
        if (Double.isNaN(modelBondLength) || modelBondLength == 0) {
            return rendererModel.getParameter(Scale.class).getDefault();
        } else {
            return this.rendererModel.getParameter(BondLength.class).getValue() / modelBondLength;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<IGenerator<IAtomContainerSet>> getGenerators() {
        return new ArrayList<IGenerator<IAtomContainerSet>>(generators);
    }

}
