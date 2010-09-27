/* Copyright (C) 2008-2009  Gilleain Torrance <gilleain.torrance@gmail.com>
 *               2008-2009  Arvid Berg <goglepox@users.sf.net>
 *               2009-2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@list.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.renderer;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.font.IFontManager;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.FitToScreen;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.FontName;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Margin;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.UsedFontStyle;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.ZoomFactor;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;


/**
 * <p>The base class for all renderers, handling the core aspects of rendering
 * such as the location of the model in 'model space' and the location on
 * the screen to draw the model. It also holds a reference to the list of
 * {@link IGenerator} instances that are used to create the diagram. These
 * generators are accessed through the generateDiagram method.</p>
 * 
 * <p>The terminology 'model space' and 'screen space' refer to the coordinate
 * systems for the model and the drawing, respectively. So the 2D points for
 * atoms in the model might be 1 unit apart (roughly representing &Aring;ngstrom,
 * perhaps) but the circles in the diagram that represent those atoms might be
 * 10 pixels apart on screen. Therefore screen space will be 10 times model
 * space for this example.</p>
 * 
 * <p>The abstract method {@link #calculateScaleForBondLength(double)} is
 * needed to determine the scale. For the model example just given, this would
 * return '10.0' for an input of '10.0', as that is the scale for that desired
 * bond length.</p>
 * 
 * @cdk.module renderbasic
 * @author maclean
 * @cdk.githash
 */
public abstract class AbstractRenderer<T extends IChemObject> {

    /**
     *The renderer model is final as it is not intended to be replaced.
     */
    protected final RendererModel rendererModel = new RendererModel();

    /**
     * Font managers change the font size depending on the zoom.
     */
    protected IFontManager fontManager;

    /**
     * The center point of the model (IMolecule, IMoleculeSet, etc).
     */
    protected Point2d modelCenter = new Point2d(0, 0);

    /**
     * The center of the desired position on screen to draw.
     */
    protected Point2d drawCenter = new Point2d(150, 200);

    /**
     * Generators for diagram elements.
     */
    protected List<IGenerator<T>> generators;

    /**
     * Used when repainting an unchanged model. 
     */
    protected IRenderingElement cachedDiagram;

    /**
     * Converts between model coordinates and screen coordinates.
     */
    protected AffineTransform transform;

    /**
     * The main method of the renderer, that uses each of the generators
     * to create a different set of {@link IRenderingElement}s grouped
     * together into a tree. 
     * 
     * @param object the object of type T to draw
     * @return the diagram as a tree of {@link IRenderingElement}s
     */
    public IRenderingElement generateDiagram(T object) {
        ElementGroup diagram = new ElementGroup();
        for (IGenerator<T> generator : this.generators) {
            diagram.add(generator.generate(object, this.rendererModel));
        }
        return diagram;
    }

    /**
     * Calculate the scale to convert the model bonds into bonds of the length
     * supplied. A standard way to do this is to calculate the average bond 
     * length (mean, or median) in model space, and divide the supplied screen
     * distance by this average to give a scale.
     * 
     * @param bondLength the desired length on screen
     * @return a multiplication factor, or 'scale'
     */
    public abstract double calculateScaleForBondLength(double bondLength);

    /**
     * Converts a bounding rectangle in 'model space' into the equivalent
     * bounds in 'screen space'. Used to determine how much space the model
     * will take up on screen given a particular scale, zoom, and margin.
     * 
     * @param modelBounds the bounds of the model
     * @return the bounds of the diagram as drawn on screen
     */
    public Rectangle calculateScreenBounds(Rectangle2D modelBounds) {
        double scale = rendererModel.getParameter(Scale.class).getValue();
        double zoom = rendererModel.getParameter(ZoomFactor.class).getValue();
        double margin = rendererModel.getParameter(Margin.class).getValue();
        Point2d modelScreenCenter
            = this.toScreenCoordinates(modelBounds.getCenterX(),
                                       modelBounds.getCenterY());
        double w = (scale * zoom * modelBounds.getWidth()) + (2 * margin);
        double h = (scale * zoom * modelBounds.getHeight()) + (2 * margin);
        return new Rectangle((int) (modelScreenCenter.x - w / 2),
                             (int) (modelScreenCenter.y - h / 2),
                             (int) w,
                             (int) h);
    }

    /**
     * Convert a point in screen space into a point in model space.
     * 
     * @param screenX the screen x-coordinate
     * @param screenY the screen y-coordinate
     * @return the equivalent point in model space, or (0,0) if there is an error
     */
    public Point2d toModelCoordinates(double screenX, double screenY) {
        try {
            double[] dest = new double[2];
            double[] src = new double[] { screenX, screenY };
            transform.inverseTransform(src, 0, dest, 0, 1);
            return new Point2d(dest[0], dest[1]);
        } catch (NoninvertibleTransformException n) {
            return new Point2d(0,0);
        }
    }

    /**
     * Convert a point in model space into a point in screen space.
     * 
     * @param modelX the model x-coordinate
     * @param modelY the model y-coordinate
     * @return the equivalent point in screen space
     */
    public Point2d toScreenCoordinates(double modelX, double modelY) {
        double[] dest = new double[2];
        transform.transform(new double[] { modelX, modelY }, 0, dest, 0, 1);
        return new Point2d(dest[0], dest[1]);
    }

    /**
     * Set the position of the center of the model.
     * 
     * @param x the x-coordinate of the model center
     * @param y the y-coordinate of the model center
     */
    public void setModelCenter(double x, double y) {
        this.modelCenter = new Point2d(x, y);
        setup();
    }

    /**
     * Set the point on the screen to draw the diagram.
     * 
     * @param x the x-coordinate of the point to draw at
     * @param y the y-coordinate of the point to draw at
     */

    public void setDrawCenter(double x, double y) {
        this.drawCenter = new Point2d(x, y);
        setup();
    }

    /**
     * Set the zoom, where 1.0 is 100% zoom.
     * 
     * @param zoom the zoom as a double value
     */
    public void setZoom(double zoom) {
        rendererModel.getParameter(ZoomFactor.class).setValue( zoom );
        setup();
    }

    /**
     * Creates the transform using the scale, zoom, drawCenter, and modelCenter.
     * In order to scale (and zoom) all elements in a uniform way, a point is
     * first moved so that the center of the model is at the origin, scaled, 
     * then moved to the correct place on screen.
     */
    protected void setup() {
        double scale = rendererModel.getParameter(Scale.class).getValue();
        double zoom = rendererModel.getParameter(ZoomFactor.class).getValue();
        // set the transform
        try {
            transform = new AffineTransform();
            transform.translate(drawCenter.x, drawCenter.y);
            transform.scale(1,-1); // Converts between CDK Y-up & Java2D Y-down coordinate-systems
            transform.scale(scale, scale);
            transform.scale(zoom, zoom);
            transform.translate(-this.modelCenter.x, -this.modelCenter.y);
        } catch (NullPointerException npe) {
            // one of the drawCenter or modelCenter points have not been set!
            String errorString = "null pointer when setting transform: " +
                                "drawCenter=%s scale=%s zoom=%s modelCenter=%s"; 
            System.err.println(
                    String.format(
                            errorString, drawCenter, scale, zoom, modelCenter));
        }
    }

    /**
     * Get the {@link RendererModel} used by this renderer, which provides
     * access to the various parameters used to generate and draw the diagram. 
     * 
     * @return a reference to the RendererModel
     */
    public RendererModel getRenderer2DModel() {
        return this.rendererModel;
    }

    /**
     * Move the draw center by dx and dy.
     *
     * @param dx the x shift
     * @param dy the y shift
     */
    public void shiftDrawCenter(double dx, double dy) {
        drawCenter.set(drawCenter.x + dx, drawCenter.y + dy);
        setup();
    }

    /**
     * Get the position on screen that the diagram will be drawn.
     *  
     * @return the draw center
     */
    public Point2d getDrawCenter() {
        return drawCenter;
    }

    /**
     * Get the center of the model. 
     * 
     * @return the model center
     */
    public Point2d getModelCenter() {
        return modelCenter;
    }

    /**
     * Calculate and set the zoom factor needed to completely fit the diagram
     * onto the screen bounds.
     *
     * @param drawWidth the width of the area to draw onto
     * @param drawHeight the height of the area to draw onto
     * @param diagramWidth the width of the diagram
     * @param diagramHeight the height of the diagram
     */
    public void setZoomToFit(double drawWidth,
                             double drawHeight,
                             double diagramWidth,
                             double diagramHeight) {

        double m = rendererModel.getParameter(Margin.class).getValue();

        // determine the zoom needed to fit the diagram to the screen
        double widthRatio  = drawWidth  / (diagramWidth  + (2 * m));
        double heightRatio = drawHeight / (diagramHeight + (2 * m));

        double zoom = Math.min(widthRatio, heightRatio);

        this.fontManager.setFontForZoom(zoom);

        // record the zoom in the model, so that generators can use it
        rendererModel.getParameter(ZoomFactor.class).setValue(zoom);

    }

    /**
     * Repaint using the cached diagram.
     *
     * @param drawVisitor the wrapper for the graphics object that draws
     */
    public void repaint(IDrawVisitor drawVisitor) {
        this.paint(drawVisitor, cachedDiagram);
    }

    /**
     * The target method for paintChemModel, paintReaction, and paintMolecule.
     *
     * @param drawVisitor
     *            the visitor to draw with
     * @param diagram
     *            the IRenderingElement tree to render
     */
    protected void paint(IDrawVisitor drawVisitor,
            IRenderingElement diagram) {
        if (diagram == null) return;

        // cache the diagram for quick-redraw
        this.cachedDiagram = diagram;

        fontManager.setFontName(
                rendererModel.getParameter(FontName.class).getValue()
        );
        fontManager.setFontStyle(
                rendererModel.getParameter(UsedFontStyle.class).getValue()
        );

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
    protected void setupTransformNatural(Rectangle2D modelBounds) {
        double zoom = rendererModel.getParameter(ZoomFactor.class).getValue();
        this.fontManager.setFontForZoom(zoom);
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
     * Calculate the bounds of the diagram on screen, given the current scale,
     * zoom, and margin.
     *
     * @param modelBounds the bounds in model space of the chem object
     * @return the bounds in screen space of the drawn diagram
     */
    protected Rectangle convertToDiagramBounds(Rectangle2D modelBounds) {
        double cx = modelBounds.getCenterX();
        double cy = modelBounds.getCenterY();
        double mw = modelBounds.getWidth();
        double mh = modelBounds.getHeight();

        double scale = rendererModel.getParameter(Scale.class).getValue();
        double zoom = rendererModel.getParameter(ZoomFactor.class).getValue();
        
        Point2d mc = this.toScreenCoordinates(cx, cy);

        // special case for 0 or 1 atoms
        if (mw == 0 && mh == 0) {
            return new Rectangle((int)mc.x, (int)mc.y, 0, 0);
        }

        double margin = rendererModel.getParameter(Margin.class).getValue();
        int w = (int) ((scale * zoom * mw) + (2 * margin));
        int h = (int) ((scale * zoom * mh) + (2 * margin));
        int x = (int) (mc.x - w / 2);
        int y = (int) (mc.y - h / 2);

        return new Rectangle(x, y, w, h);
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
    protected void setupTransformToFit(Rectangle2D screenBounds,
            Rectangle2D modelBounds,
            double bondLength,
            boolean reset) {

        if (screenBounds == null) return;

        setDrawCenter(screenBounds.getCenterX(), screenBounds.getCenterY());

        double scale = this.calculateScaleForBondLength(bondLength);

        double drawWidth = screenBounds.getWidth();
        double drawHeight = screenBounds.getHeight();

        double diagramWidth = modelBounds.getWidth() * scale;
        double diagramHeight = modelBounds.getHeight() * scale;

        setZoomToFit(drawWidth, drawHeight, diagramWidth, diagramHeight);

        // this controls whether editing a molecule causes it to re-center
        // with each change or not
        if (reset || rendererModel.getParameter(FitToScreen.class).getValue()) {
            setModelCenter(modelBounds.getCenterX(), modelBounds.getCenterY());
        }

        // set the scale in the renderer model for the generators
        rendererModel.getParameter(Scale.class).setValue(scale);

        setup();
    }

}
