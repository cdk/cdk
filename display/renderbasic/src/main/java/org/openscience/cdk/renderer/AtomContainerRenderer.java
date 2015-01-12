/*  Copyright (C) 2008-2009  Gilleain Torrance <gilleain.torrance@gmail.com>
 *                2008-2009  Arvid Berg <goglepox@users.sf.net>
 *                2009-2010  Egon Willighagen <egonw@users.sf.net>
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
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.font.IFontManager;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.BondLength;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.ZoomFactor;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;

import static java.lang.Math.min;

/**
 * A general renderer for {@link IAtomContainer}s. The chem object
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
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class AtomContainerRenderer extends AbstractRenderer<IAtomContainer> implements IRenderer<IAtomContainer> {

    /**
     * The default scale is used when the model is empty.
     */
    public static final double DEFAULT_SCALE = 30.0;

    /**
     * A renderer that generates diagrams using the specified
     * generators and manages fonts with the supplied font manager.
     *
     * @param generators
     *            a list of classes that implement the IGenerator interface
     * @param fontManager
     *            a class that manages mappings between zoom and font sizes
     */
    public AtomContainerRenderer(List<IGenerator<IAtomContainer>> generators, IFontManager fontManager) {
        this(new RendererModel(), generators, fontManager);
        for (IGenerator<IAtomContainer> generator : generators) {
            rendererModel.registerParameters(generator);
        }
    }

    public AtomContainerRenderer(RendererModel rendererModel, List<IGenerator<IAtomContainer>> generators,
            IFontManager fontManager) {
        super(rendererModel);
        this.generators = generators;
        this.fontManager = fontManager;
    }

    /**
     * Setup the transformations necessary to draw this Atom Container.
     *
     * @param atomContainer the atom container to use in the setup
     * @param screen the area to draw on
     */
    @Override
    public void setup(IAtomContainer atomContainer, Rectangle screen) {
        this.setScale(atomContainer);
        Rectangle2D bounds = BoundsCalculator.calculateBounds(atomContainer);
        this.modelCenter = new Point2d(bounds.getCenterX(), bounds.getCenterY());
        this.drawCenter = new Point2d(screen.getCenterX(), screen.getCenterY());
        this.setup();
    }

    /**
     * Reset the draw center and model center, and set the zoom to 100%.
     */
    public void reset() {
        modelCenter = new Point2d(0, 0);
        drawCenter = new Point2d(200, 200);
        rendererModel.getParameter(ZoomFactor.class).setValue(1.0);
        setup();
    }

    /**
     * Set the scale for an IAtomContainer. It calculates the average bond
     * length of the model and calculates the multiplication factor to transform
     * this to the bond length that is set in the RendererModel.
     *
     * @param atomContainer the atom container that will be drawn
     */
    @Override
    public void setScale(IAtomContainer atomContainer) {
        double bondLength = GeometryUtil.getBondLengthAverage(atomContainer);
        rendererModel.getParameter(Scale.class).setValue(this.calculateScaleForBondLength(bondLength));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle paint(IAtomContainer atomContainer, IDrawVisitor drawVisitor) {
        // the bounds of the model
        Rectangle2D modelBounds = BoundsCalculator.calculateBounds(atomContainer);

        // setup and draw
        this.setupTransformNatural(modelBounds);
        IRenderingElement diagram = generateDiagram(atomContainer);
        this.paint(drawVisitor, diagram);

        return this.convertToDiagramBounds(modelBounds);
    }

    /**
     * Determine an estimated bond length for disconnected structures. The
     * distance between all atoms is measured and then the minimum distance is
     * divided by 1.5. This length is required for scaling renderings.
     *
     * @param container a chemical structure with no bonds at at least 2 atoms
     * @return the estimated bond length
     * @throws IllegalArgumentException the structure had a bond or less than
     *                                  two atoms
     */
    private static double estimatedBondLength(IAtomContainer container) {

        if (container.getBondCount() > 0)
            throw new IllegalArgumentException("structure has at least one bond - disconnected scaling not need");
        if (container.getAtomCount() < 2) throw new IllegalArgumentException("structure must have at least two atoms");

        int nAtoms = container.getAtomCount();
        double minDistance = Integer.MAX_VALUE;

        for (int i = 0; i < nAtoms; i++)
            for (int j = i + 1; j < nAtoms; j++)
                minDistance = min(container.getAtom(i).getPoint2d().distance(container.getAtom(j).getPoint2d()),
                        minDistance);

        return minDistance / 1.5; // non-bonded, if they were they would be closer
    }

    /**
     * Paint a molecule (an IAtomContainer).
     *
     * @param atomContainer the molecule to paint
     * @param drawVisitor the visitor that does the drawing
     * @param bounds the bounds on the screen
     * @param resetCenter
     *     if true, set the draw center to be the center of bounds
     */
    @Override
    public void paint(IAtomContainer atomContainer, IDrawVisitor drawVisitor, Rectangle2D bounds, boolean resetCenter) {

        if (atomContainer.getBondCount() > 0 || atomContainer.getAtomCount() == 1) {
            rendererModel.getParameter(Scale.class).setValue(
                    calculateScaleForBondLength(GeometryUtil.getBondLengthAverage(atomContainer)));
        } else if (atomContainer.getAtomCount() > 1) {
            rendererModel.getParameter(Scale.class).setValue(
                    calculateScaleForBondLength(estimatedBondLength(atomContainer)));
        }

        // the diagram to draw
        IRenderingElement diagram = generateDiagram(atomContainer);

        // the bounds of the model from 'Bounds' elements
        Rectangle2D modelBounds = getBounds(diagram);

        // no bounding elements, use the atom coordinates
        if (modelBounds == null) modelBounds = BoundsCalculator.calculateBounds(atomContainer);

        setupTransformToFit(bounds, modelBounds, resetCenter);

        this.paint(drawVisitor, diagram);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle calculateDiagramBounds(IAtomContainer atomContainer) {
        return calculateScreenBounds(BoundsCalculator.calculateBounds(atomContainer));
    }

    /**
     * Given a bond length for a model, calculate the scale that will transform
     * this length to the on screen bond length in RendererModel.
     *
     * @param modelBondLength the average bond length of the model
     * @return the scale necessary to transform this to a screen bond
     */
    @Override
    public double calculateScaleForBondLength(double modelBondLength) {
        if (Double.isNaN(modelBondLength) || modelBondLength == 0) {
            return DEFAULT_SCALE;
        } else {
            return rendererModel.getParameter(BondLength.class).getValue() / modelBondLength;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IGenerator<IAtomContainer>> getGenerators() {
        return new ArrayList<IGenerator<IAtomContainer>>(generators);
    }

}
