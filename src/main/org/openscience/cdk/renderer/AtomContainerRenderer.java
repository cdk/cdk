/*  Copyright (C) 2008-2009  Gilleain Torrance <gilleain.torrance@gmail.com>
 *                2008-2009  Arvid Berg <goglepox@users.sf.net>
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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.font.IFontManager;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator.BondLength;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.FitToScreen;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.FontName;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Margin;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.UsedFontStyle;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.ZoomFactor;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;

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
 */
public class AtomContainerRenderer {
    /**
     * The default scale is used when the model is empty.
     */
    public static final double DEFAULT_SCALE = 30.0;

    protected IFontManager fontManager;

	/**
	 * The renderer model is final as it is not intended to be replaced.
	 */
    protected final RendererModel rendererModel = new RendererModel();

    protected List<IGenerator<IAtomContainer>> generators;
	
    protected AffineTransform transform;

    protected Point2d modelCenter = new Point2d(0, 0); // model

    protected Point2d drawCenter = new Point2d(150, 200); //diagram on screen

    protected double scale = DEFAULT_SCALE;

    protected double zoom = 1.0;

    protected IRenderingElement cachedDiagram;

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
	    this.generators = generators;
        this.fontManager = fontManager;
        for (IGenerator generator : generators)
            rendererModel.registerParameters(generator);
    }
	
	/**
	 * Setup the transformations necessary to draw this Atom Container.
	 *
	 * @param atomContainer
	 * @param screen
	 */
	public void setup(IAtomContainer atomContainer, Rectangle screen) {
        this.setScale(atomContainer);
        Rectangle2D bounds = calculateBounds(atomContainer);
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
     * Set the scale for an IAtomContainer. It calculates the average bond
     * length of the model and calculates the multiplication factor to transform
     * this to the bond length that is set in the RendererModel.
     * @param atomContainer
     */
    public void setScale(IAtomContainer atomContainer) {
        double bondLength = GeometryTools.getBondLengthAverage(atomContainer);
        this.scale = this.calculateScaleForBondLength(bondLength);

        // store the scale so that other components can access it
        this.rendererModel.getRenderingParameter(Scale.class).setValue(scale);
    }

	public Rectangle paint(
			IAtomContainer atomContainer,IDrawVisitor drawVisitor) {
	    // the bounds of the model
        Rectangle2D modelBounds = calculateBounds(atomContainer);

        // setup and draw
        this.setupTransformNatural(modelBounds);
        IRenderingElement diagram = this.generateDiagram(atomContainer);
        this.paint(drawVisitor, diagram);

        return this.convertToDiagramBounds(modelBounds);
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
    public void paintMolecule(IAtomContainer atomContainer,
            IDrawVisitor drawVisitor, Rectangle2D bounds, boolean resetCenter) {

        // the bounds of the model
    	Rectangle2D modelBounds = calculateBounds(atomContainer);

    	this.setupTransformToFit(bounds, modelBounds,
    	        GeometryTools.getBondLengthAverage(atomContainer), resetCenter);

    	// the diagram to draw
    	IRenderingElement diagram = this.generateDiagram(atomContainer);

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

	public Rectangle calculateDiagramBounds(IAtomContainer atomContainer) {
		return this.calculateScreenBounds(
		    calculateBounds(atomContainer));
	}

	public Rectangle calculateScreenBounds(Rectangle2D modelBounds) {
	    double margin = this.rendererModel
	        .getRenderingParameter(Margin.class).getValue();
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

    public static Rectangle2D calculateBounds(IAtomContainer ac) {
        // this is essential, otherwise a rectangle
        // of (+INF, -INF, +INF, -INF) is returned!
        if (ac.getAtomCount() == 0) {
            return new Rectangle2D.Double();
        } else if (ac.getAtomCount() == 1) {
            Point2d p = ac.getAtom(0).getPoint2d();
            return new Rectangle2D.Double(p.x, p.y, 0, 0);
        }

    	double xmin = Double.POSITIVE_INFINITY;
    	double xmax = Double.NEGATIVE_INFINITY;
    	double ymin = Double.POSITIVE_INFINITY;
    	double ymax = Double.NEGATIVE_INFINITY;

    	for (IAtom atom : ac.atoms()) {
    		Point2d p = atom.getPoint2d();
    		xmin = Math.min(xmin, p.x);
    		xmax = Math.max(xmax, p.x);
    		ymin = Math.min(ymin, p.y);
    		ymax = Math.max(ymax, p.y);
    	}
    	double w = xmax - xmin;
    	double h = ymax - ymin;
    	return new Rectangle2D.Double(xmin, ymin, w, h);
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
		this.rendererModel.getRenderingParameter(
		    	ZoomFactor.class).setValue( z );
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

        double m = this.rendererModel
            .getRenderingParameter(Margin.class).getValue();

        // determine the zoom needed to fit the diagram to the screen
        double widthRatio  = drawWidth  / (diagramWidth  + (2 * m));
        double heightRatio = drawHeight / (diagramHeight + (2 * m));

        this.zoom = Math.min(widthRatio, heightRatio);

        this.fontManager.setFontForZoom(zoom);

        // record the zoom in the model, so that generators can use it
        this.rendererModel.getRenderingParameter(
    	    	ZoomFactor.class).setValue(zoom);

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

	    this.fontManager.setFontName(
	    	this.rendererModel.getRenderingParameter(FontName.class).getValue()
	    );
	    this.fontManager.setFontStyle(
	    	this.rendererModel.getRenderingParameter(UsedFontStyle.class)
	    		.getValue()
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
	private void setupTransformNatural(Rectangle2D modelBounds) {
	    this.zoom = this.rendererModel.getRenderingParameter(
	    	ZoomFactor.class).getValue();
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
	    if (reset || rendererModel.getRenderingParameter(FitToScreen.class).getValue()) {
            this.setModelCenter(
                    modelBounds.getCenterX(), modelBounds.getCenterY());
        }

	    // set the scale in the renderer model for the generators
	    if (reset) {
	        this.rendererModel.getRenderingParameter(Scale.class).setValue(scale);
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
            return DEFAULT_SCALE;
        } else {
            return this.rendererModel.getRenderingParameter(BondLength.class)
            	.getValue() / modelBondLength;
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

        double margin = this.rendererModel
            .getRenderingParameter(Margin.class).getValue();
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

	protected IRenderingElement generateDiagram(IAtomContainer ac) {
	    ElementGroup diagram = new ElementGroup();
        for (IGenerator<IAtomContainer> generator : this.generators) {
            diagram.add(generator.generate(ac, this.rendererModel));
        }
        return diagram;
	}

	public List<IGenerator<IAtomContainer>> getGenerators(){
	    return new ArrayList<IGenerator<IAtomContainer>>(generators);
	}
}
