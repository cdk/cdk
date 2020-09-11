/* Copyright (C) 2008 Gilleain Torrance <gilleain.torrance@gmail.com>
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
package org.openscience.cdk.renderer.visitor;

import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ArrowElement;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.MarkedElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.PathElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.elements.TextGroupElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement;
import org.openscience.cdk.renderer.elements.path.Type;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.font.IFontManager;
import org.openscience.cdk.renderer.generators.BasicBondGenerator.WedgeWidth;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.ArrowHeadWidth;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.UseAntiAliasing;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link IDrawVisitor} interface for the AWT
 * widget toolkit, allowing molecules to be rendered with toolkits based on
 * AWT, like the Java reference graphics platform Swing.
 *
 * @cdk.module renderawt
 * @cdk.githash
 */
public class AWTDrawVisitor extends AbstractAWTDrawVisitor {

    /**
     * The font manager cannot be set by the constructor as it needs to
     * be managed by the Renderer.
     */
    private AWTFontManager                  fontManager;

    /**
     * The renderer model cannot be set by the constructor as it needs to
     * be managed by the Renderer.
     */
    private RendererModel                   rendererModel;

    private final Map<Integer, BasicStroke> strokeMap = new HashMap<>();

    /**
     * Returns the current {@link RendererModel}.
     *
     * @return the current model
     */
    public RendererModel getRendererModel() {
        return rendererModel;
    }

    /**
     * Returns the current stroke map.
     *
     * @return a {@link Map} with Integer as keys and {@link BasicStroke}s.
     */
    public Map<Integer, BasicStroke> getStrokeMap() {
        return strokeMap;
    }

    private final float                      minStroke;

    private final boolean                    strokeCache;

    private final Graphics2D                 graphics;

    /**
     * Should we round coordinates to ints to circumvent graphical glitches from AWT.
     */
    private boolean roundCoords = true;

    /**
     * Returns the {@link Graphics2D} for for this visitor.
     *
     * @return the {@link Graphics2D} object
     */
    public Graphics2D getGraphics() {
        return graphics;
    }

    /**
     * Constructs a new {@link IDrawVisitor} using the AWT widget toolkit,
     * taking a {@link Graphics2D} object to which the chemical content
     * is drawn.
     *
     * @param graphics {@link Graphics2D} to which will be drawn
     */
    public AWTDrawVisitor(Graphics2D graphics) {
        this(graphics, true, 1.5f);
    }

    /**
     * Internal constructor.
     *
     * @param graphics the graphics instance
     * @param strokeCache cache strokes internally, only sizes at 0.25 increments are stored
     * @param minStroke the minimum stroke, strokes smaller than this are automatically resized
     */
    private AWTDrawVisitor(Graphics2D graphics, boolean strokeCache, float minStroke) {
        this.graphics = graphics;
        this.fontManager = null;
        this.rendererModel = null;
        this.strokeCache = strokeCache;
        this.minStroke = minStroke;
    }

    /**
     * Create a draw visitor that will be rendering to a vector graphics output. This disables
     * the minimum stroke size and stroke caching when drawing lines.
     *
     * @param g2 graphics environment
     * @return draw visitor
     */
    public static AWTDrawVisitor forVectorGraphics(Graphics2D g2) {
        return new AWTDrawVisitor(g2, false, Float.NEGATIVE_INFINITY);
    }

    /**
     * Set whether we should we round coordinates to ints, this tries to circumvent
     * graphical glitches from AWT where floating points are truncated (e.g. 1.6 -&gt; 1)
     * which causes notable defect such as parallel lines that aren't parallel.
     *
     * @param val rounding mode
     */
    public void setRounding(boolean val) {
        this.roundCoords = val;
    }

    private void visit(ElementGroup elementGroup) {
        elementGroup.visitChildren(this);
    }

    private void visit(LineElement line) {
        Stroke savedStroke = this.graphics.getStroke();

        // scale the stroke by zoom + scale (both included in the AffineTransform)
        float width = (float) (line.width * transform.getScaleX());
        if (width < minStroke) width = minStroke;

        int key = (int) (width * 4); // store 2.25, 2.5, 2.75 etc to separate keys

        if (strokeCache && strokeMap.containsKey(key)) {
            this.graphics.setStroke(strokeMap.get(key));
        } else {
            BasicStroke stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            this.graphics.setStroke(stroke);
            strokeMap.put(key, stroke);
        }

        double[] coordinates = new double[]{line.firstPointX, line.firstPointY, line.secondPointX, line.secondPointY};

        graphics.setColor(line.color);
        transform.transform(coordinates, 0, coordinates, 0, 2);
        if (roundCoords) {
            graphics.drawLine((int) Math.round(coordinates[0]), (int) Math.round(coordinates[1]),
                              (int) Math.round(coordinates[2]), (int) Math.round(coordinates[3]));
        } else {
            graphics.draw(new Line2D.Double(coordinates[0], coordinates[1],
                                            coordinates[2], coordinates[3]));
        }
        graphics.setStroke(savedStroke);
    }

    private void visit(OvalElement oval) {
        this.graphics.setColor(oval.color);
        int radius = scaleX(oval.radius);
        int diameter = scaleX(oval.radius * 2);

        if (oval.fill) {
            this.graphics.fillOval(transformX(oval.xCoord) - radius, transformY(oval.yCoord) - radius, diameter,
                    diameter);
        } else {
            Stroke savedStroke = this.graphics.getStroke();

            // scale the stroke by zoom + scale (both included in the AffineTransform)
            float width = (float) (oval.stroke * transform.getScaleX());
            if (width < minStroke) width = minStroke;

            int key = (int) (width * 4); // store 2.25, 2.5, 2.75 etc to separate keys

            if (strokeCache && strokeMap.containsKey(key)) {
                this.graphics.setStroke(strokeMap.get(key));
            } else {
                BasicStroke stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                this.graphics.setStroke(stroke);
                strokeMap.put(key, stroke);
            }

            this.graphics.drawOval(transformX(oval.xCoord) - radius, transformY(oval.yCoord) - radius, diameter,
                    diameter);
        }
    }

    private int scaleX(double xCoord) {
        return (int) (xCoord * transform.getScaleX());
    }

    private int scaleY(double yCoord) {
        return (int) (yCoord * -transform.getScaleY());
    }

    private int transformX(double xCoord) {
        return (int) transform(xCoord, 1)[0];
    }

    private int transformY(double yCoord) {
        return (int) transform(1, yCoord)[1];
    }

    private double[] transform(double xCoord, double yCoord) {
        double[] result = new double[2];
        transform.transform(new double[]{xCoord, yCoord}, 0, result, 0, 1);
        return result;
    }

    private Color getBackgroundColor() {
        if (rendererModel == null) return new BasicSceneGenerator.BackgroundColor().getDefault();

        return rendererModel.getParameter(BasicSceneGenerator.BackgroundColor.class).getValue();
    }

    private void visit(TextElement textElement) {
        this.graphics.setFont(this.fontManager.getFont());
        Point point = this.getTextBasePoint(textElement.text, textElement.xCoord, textElement.yCoord, graphics);
        Rectangle2D textBounds = this.getTextBounds(textElement.text, textElement.xCoord, textElement.yCoord, graphics);
        this.graphics.setColor(getBackgroundColor());
        this.graphics.fill(textBounds);
        this.graphics.setColor(textElement.color);
        this.graphics.drawString(textElement.text, point.x, point.y);
    }

    private void visit(WedgeLineElement wedge) {
        // make the vector normal to the wedge axis
        Vector2d normal = new Vector2d(wedge.firstPointY - wedge.secondPointY, wedge.secondPointX - wedge.firstPointX);
        normal.normalize();
        normal.scale(rendererModel.getParameter(WedgeWidth.class).getValue()
                / rendererModel.getParameter(Scale.class).getValue());

        // make the triangle corners
        Point2d vertexA = new Point2d(wedge.firstPointX, wedge.firstPointY);
        Point2d vertexB = new Point2d(wedge.secondPointX, wedge.secondPointY);
        Point2d vertexC = new Point2d(vertexB);
        vertexB.add(normal);
        vertexC.sub(normal);
        this.graphics.setColor(wedge.color);
        if (wedge.type == WedgeLineElement.TYPE.DASHED) {
            this.drawDashedWedge(vertexA, vertexB, vertexC);
        } else if (wedge.type == WedgeLineElement.TYPE.WEDGED) {
            this.drawFilledWedge(vertexA, vertexB, vertexC);
        } else if (wedge.type == WedgeLineElement.TYPE.INDIFF) {
            this.drawIndiffWedge(vertexA, vertexB, vertexC);
        }
    }

    private void drawFilledWedge(Point2d vertexA, Point2d vertexB, Point2d vertexC) {
        int[] pointB = this.transformPoint(vertexB.x, vertexB.y);
        int[] pointC = this.transformPoint(vertexC.x, vertexC.y);
        int[] pointA = this.transformPoint(vertexA.x, vertexA.y);

        int[] xCoords = new int[]{pointB[0], pointC[0], pointA[0]};
        int[] yCoords = new int[]{pointB[1], pointC[1], pointA[1]};
        this.graphics.fillPolygon(xCoords, yCoords, 3);
    }

    private void drawDashedWedge(Point2d vertexA, Point2d vertexB, Point2d vertexC) {
        // store the current stroke
        Stroke storedStroke = this.graphics.getStroke();
        this.graphics.setStroke(new BasicStroke(1));

        // calculate the distances between lines
        double distance = vertexB.distance(vertexA);
        double gapFactor = 0.1;
        double gap = distance * gapFactor;
        double numberOfDashes = distance / gap;
        double displacement = 0;

        // draw by interpolating along the edges of the triangle
        for (int i = 0; i < numberOfDashes; i++) {
            Point2d point1 = new Point2d();
            point1.interpolate(vertexA, vertexB, displacement);
            Point2d point2 = new Point2d();
            point2.interpolate(vertexA, vertexC, displacement);
            int[] p1T = this.transformPoint(point1.x, point1.y);
            int[] p2T = this.transformPoint(point2.x, point2.y);
            this.graphics.drawLine(p1T[0], p1T[1], p2T[0], p2T[1]);
            if (distance * (displacement + gapFactor) >= distance) {
                break;
            } else {
                displacement += gapFactor;
            }
        }
        this.graphics.setStroke(storedStroke);
    }

    private void drawIndiffWedge(Point2d vertexA, Point2d vertexB, Point2d vertexC) {
        // store the current stroke
        Stroke storedStroke = this.graphics.getStroke();
        this.graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // calculate the distances between lines
        double distance = vertexB.distance(vertexA);
        double gapFactor = 0.05;
        double gap = distance * gapFactor;
        double numberOfDashes = distance / gap;
        double displacement = 0;

        // draw by interpolating along the edges of the triangle
        Point2d point1 = new Point2d();
        boolean flip = false;
        point1.interpolate(vertexA, vertexB, displacement);
        int[] p1T = this.transformPoint(point1.x, point1.y);
        displacement += gapFactor;
        for (int i = 0; i < numberOfDashes; i++) {
            Point2d point2 = new Point2d();
            if (flip) {
                point2.interpolate(vertexA, vertexC, displacement);
            } else {
                point2.interpolate(vertexA, vertexB, displacement);
            }
            flip = !flip;
            int[] p2T = this.transformPoint(point2.x, point2.y);
            this.graphics.drawLine(p1T[0], p1T[1], p2T[0], p2T[1]);
            if (distance * (displacement + gapFactor) >= distance) {
                break;
            } else {
                p1T = p2T;
                displacement += gapFactor;
            }
        }
        this.graphics.setStroke(storedStroke);
    }

    private void visit(AtomSymbolElement atomSymbol) {
        this.graphics.setFont(this.fontManager.getFont());

        double[] xy = {atomSymbol.xCoord, atomSymbol.yCoord};

        this.transformPoint(xy);

        Rectangle2D bounds = getTextBounds(atomSymbol.text, graphics);

        double w = bounds.getWidth();
        double h = bounds.getHeight();

        double xOffset = bounds.getX();
        double yOffset = bounds.getY() + bounds.getHeight();

        bounds.setRect(xy[0] - (w / 2), xy[1] - (h / 2), w, h);

        double padding = h / 4;
        Shape shape = new RoundRectangle2D.Double(bounds.getX() - (padding / 2), bounds.getY() - (padding / 2),
                bounds.getWidth() + padding, bounds.getHeight() + padding, padding, padding);

        this.graphics.setColor(getBackgroundColor());
        this.graphics.fill(shape);
        this.graphics.setColor(atomSymbol.color);
        this.graphics.drawString(atomSymbol.text, (int) (bounds.getX() - xOffset), (int) (bounds.getY() + h - yOffset));

        int offset = 10; // XXX
        String chargeString;
        if (atomSymbol.formalCharge == 0) {
            return;
        } else if (atomSymbol.formalCharge == 1) {
            chargeString = "+";
        } else if (atomSymbol.formalCharge > 1) {
            chargeString = atomSymbol.formalCharge + "+";
        } else if (atomSymbol.formalCharge == -1) {
            chargeString = "-";
        } else {
            int absCharge = Math.abs(atomSymbol.formalCharge);
            chargeString = absCharge + "-";
        }

        int xCoord = (int) bounds.getCenterX();
        int yCoord = (int) bounds.getCenterY();
        if (atomSymbol.alignment == 1) { // RIGHT
            this.graphics.drawString(chargeString, xCoord + offset, (int) bounds.getMinY());
        } else if (atomSymbol.alignment == -1) { // LEFT
            this.graphics.drawString(chargeString, xCoord - offset, (int) bounds.getMinY());
        } else if (atomSymbol.alignment == 2) { // TOP
            this.graphics.drawString(chargeString, xCoord, yCoord - offset);
        } else if (atomSymbol.alignment == -2) { // BOT
            this.graphics.drawString(chargeString, xCoord, yCoord + offset);
        }

    }

    private void visit(RectangleElement rectangle) {
        this.graphics.setColor(rectangle.color);
        int width = scaleX(rectangle.width);
        int height = scaleY(rectangle.height);
        if (rectangle.filled) {
            this.graphics.fillRect(transformX(rectangle.xCoord), transformY(rectangle.yCoord) - height, width, height);
        } else {
            this.graphics.drawRect(transformX(rectangle.xCoord), transformY(rectangle.yCoord) - height, width, height);
        }
    }

    private void visit(PathElement path) {
        this.graphics.setColor(path.color);
        for (int i = 1; i < path.points.size(); i++) {
            Point2d point1 = path.points.get(i - 1);
            Point2d point2 = path.points.get(i);
            int[] lineStart = this.transformPoint(point1.x, point1.y);
            int[] lineEnd = this.transformPoint(point2.x, point2.y);
            this.graphics.drawLine(lineStart[0], lineStart[1], lineEnd[0], lineEnd[1]);
        }
    }

    private void visit(GeneralPath path) {
        this.graphics.setColor(path.color);
        Path2D cpy = new Path2D.Double();
        cpy.append(getPathIterator(path, transform), false);

        if (path.fill) {
            this.graphics.fill(cpy);
        } else {
            Stroke stroke = this.graphics.getStroke();
            this.graphics.setStroke(new BasicStroke((float) (path.stroke * transform.getScaleX()),
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            this.graphics.draw(cpy);
            this.graphics.setStroke(stroke);
        }
    }

    private static PathIterator getPathIterator(final GeneralPath path, final AffineTransform transform) {
        return new PathIterator() {

            int index;

            private int type(Type type) {
                switch (type) {
                    case MoveTo:
                        return SEG_MOVETO;
                    case LineTo:
                        return SEG_LINETO;
                    case QuadTo:
                        return SEG_QUADTO;
                    case CubicTo:
                        return SEG_CUBICTO;
                    case Close:
                        return SEG_CLOSE;
                    default:
                        return SEG_CLOSE;
                }
            }

            @Override
            public void next() {
                index++;
            }

            @Override
            public boolean isDone() {
                return index >= path.elements.size();
            }

            @Override
            public int getWindingRule() {
                return path.winding;
            }

            @Override
            public int currentSegment(double[] coords) {
                path.elements.get(index).points(coords);
                transform.transform(coords, 0, coords, 0, 3);
                return type(path.elements.get(index).type);
            }

            @Override
            public int currentSegment(float[] coords) {
                double[] src = new double[6];
                path.elements.get(index).points(src);
                transform.transform(src, 0, coords, 0, src.length / 2);
                return type(path.elements.get(index).type());
            }
        };
    }

    private void visit(ArrowElement line) {
        double scale = rendererModel.getParameter(Scale.class).getValue();
        Stroke savedStroke = graphics.getStroke();

        int w = (int) (line.width * scale);
        if (strokeMap.containsKey(w)) {
            graphics.setStroke(strokeMap.get(w));
        } else {
            BasicStroke stroke = new BasicStroke(w);
            graphics.setStroke(stroke);
            strokeMap.put(w, stroke);
        }

        graphics.setColor(line.color);
        int[] a = this.transformPoint(line.startX, line.startY);
        int[] b = this.transformPoint(line.endX, line.endY);
        graphics.drawLine(a[0], a[1], b[0], b[1]);
        double aW = rendererModel.getParameter(ArrowHeadWidth.class).getValue() / scale;
        if (line.direction) {
            int[] c = this.transformPoint(line.startX - aW, line.startY - aW);
            int[] d = this.transformPoint(line.startX - aW, line.startY + aW);
            graphics.drawLine(a[0], a[1], c[0], c[1]);
            graphics.drawLine(a[0], a[1], d[0], d[1]);
        } else {
            int[] c = this.transformPoint(line.endX + aW, line.endY - aW);
            int[] d = this.transformPoint(line.endX + aW, line.endY + aW);
            graphics.drawLine(b[0], b[1], c[0], c[1]);
            graphics.drawLine(b[0], b[1], d[0], d[1]);
        }
        graphics.setStroke(savedStroke);
    }

    private void visit(TextGroupElement textGroup) {
        this.graphics.setFont(this.fontManager.getFont());
        Point point = super.getTextBasePoint(textGroup.text, textGroup.xCoord, textGroup.yCoord, graphics);
        Rectangle2D textBounds = this.getTextBounds(textGroup.text, textGroup.xCoord, textGroup.yCoord, graphics);
        this.graphics.setColor(getBackgroundColor());
        this.graphics.fill(textBounds);
        this.graphics.setColor(textGroup.color);
        this.graphics.drawString(textGroup.text, point.x, point.y);

        int xCoord = (int) textBounds.getCenterX();
        int yCoord = (int) textBounds.getCenterY();
        int xCoord1 = (int) textBounds.getMinX();
        int yCoord1 = (int) textBounds.getMinY();
        int xCoord2 = point.x + (int) textBounds.getWidth();
        int yCoord2 = (int) textBounds.getMaxY();

        int oWidth = xCoord2 - xCoord1;
        int oHeight = yCoord2 - yCoord1;
        for (TextGroupElement.Child child : textGroup.children) {
            int childx;
            int childy;

            switch (child.position) {
                case NE:
                    childx = xCoord2;
                    childy = yCoord1;
                    break;
                case N:
                    childx = xCoord1;
                    childy = yCoord1;
                    break;
                case NW:
                    childx = xCoord1 - oWidth;
                    childy = yCoord1;
                    break;
                case W:
                    childx = xCoord1 - oWidth;
                    childy = point.y;
                    break;
                case SW:
                    childx = xCoord1 - oWidth;
                    childy = yCoord1 + oHeight;
                    break;
                case S:
                    childx = xCoord1;
                    childy = yCoord2 + oHeight;
                    break;
                case SE:
                    childx = xCoord2;
                    childy = yCoord2 + oHeight;
                    break;
                case E:
                    childx = xCoord2;
                    childy = point.y;
                    break;
                default:
                    childx = xCoord;
                    childy = yCoord;
                    break;
            }

            this.graphics.drawString(child.text, childx, childy);
            if (child.subscript != null) {
                Rectangle2D childBounds = getTextBounds(child.text, childx, childy, graphics);
                int scx = (int) (childx + (childBounds.getWidth() * 0.75));
                int scy = (int) (childy + (childBounds.getHeight() / 3));
                Font font = this.graphics.getFont(); // TODO : move to font manager
                Font subscriptFont = font.deriveFont(font.getStyle(), font.getSize() - 2);
                this.graphics.setFont(subscriptFont);
                this.graphics.drawString(child.subscript, scx, scy);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void visit(IRenderingElement element) {
        Color savedColor = this.graphics.getColor();
        if (element instanceof ElementGroup)
            visit((ElementGroup) element);
        else if (element instanceof WedgeLineElement)
            visit((WedgeLineElement) element);
        else if (element instanceof LineElement)
            visit((LineElement) element);
        else if (element instanceof OvalElement)
            visit((OvalElement) element);
        else if (element instanceof TextGroupElement)
            visit((TextGroupElement) element);
        else if (element instanceof AtomSymbolElement)
            visit((AtomSymbolElement) element);
        else if (element instanceof TextElement)
            visit((TextElement) element);
        else if (element instanceof RectangleElement)
            visit((RectangleElement) element);
        else if (element instanceof PathElement)
            visit((PathElement) element);
        else if (element instanceof GeneralPath)
            visit((GeneralPath) element);
        else if (element instanceof ArrowElement)
            visit((ArrowElement) element);
        else if (element instanceof Bounds) {
            visit(((Bounds) element).root());
        } else if (element instanceof MarkedElement) {
            visit(((MarkedElement) element).element());
        } else
            System.err.println("Visitor method for " + element.getClass().getName() + " is not implemented");
        this.graphics.setColor(savedColor);
    }

    /**
     * The font manager must be set by any renderer that uses this class!
     * This manager is needed to keep track of fonts of the right size.
     *
     * @param fontManager the {@link IFontManager} to be used
     */
    @Override
    public void setFontManager(IFontManager fontManager) {
        this.fontManager = (AWTFontManager) fontManager;
    }

    /** {@inheritDoc} */
    @Override
    public void setRendererModel(RendererModel rendererModel) {
        this.rendererModel = rendererModel;
        if (rendererModel.hasParameter(UseAntiAliasing.class)) {
            if (rendererModel.getParameter(UseAntiAliasing.class).getValue()) {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // g.setStroke(new BasicStroke((int)rendererModel.getBondWidth()));
            }
        }
    }

}
