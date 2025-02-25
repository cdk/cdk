/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.renderer.generators.standard;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Immutable outline of text. The outline is maintained as a Java 2D shape
 * instance and can be transformed. As an immutable instance, transforming the
 * outline creates a new instance.
 *
 * @author John May
 */
final public class TextOutline {
	
    public static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(new AffineTransform(), true, true);
    /**
     * The original text.
     */
    public final String          text;

    /**
     * The original glyphs.
     */
    public final GlyphVector     glyphs;

    /**
     * The outline of the text (untransformed).
     */
    private final Shape           outline;

    /**
     * Transform applied to outline.
     */
    public final AffineTransform transform;

    /**
     * Create an outline of text in provided font.
     *
     * @param text the text to create an outline of
     * @param font the font style, size, and shape that defines the outline
     */
    public TextOutline(final String text, final Font font) {
        this(text, font.createGlyphVector(new FontRenderContext(new AffineTransform(), true, true), text));
    }

    /**
     * Create an outline of text and the glyphs for that text.
     *
     * @param text the text to create an outline of
     * @param glyphs the glyphs for the provided outlined
     */
    TextOutline(String text, GlyphVector glyphs) {
        this(text, glyphs, glyphs.getOutline(), new AffineTransform());
    }

	/**
	 * Internal constructor, requires all attributes.
	 *
	 * @param text      the text
	 * @param glyphs    glyphs of the text
	 * @param outline   the outline of the glyphs
	 * @param transform the transform
	 */
	private TextOutline(String text, GlyphVector glyphs, Shape outline, AffineTransform transform) {
		this.text = text;
		this.glyphs = glyphs;

//// BH testing
//
//StandardGlyphVector g = (StandardGlyphVector) glyphs;
//// For each glyph return posx, posy, advx, advy, visx, visy, visw, vish.
//if (text.length() > 0) {
//	LineMetrics m = g.getFont().getLineMetrics(text, FONT_RENDER_CONTEXT);
//	System.out.println(g.getFont() + "a=" + m.getAscent() + " d=" + m.getDescent() + " h=" + m.getHeight() + "\n");
//	System.out.println(text + " outB " + glyphs.getOutline().getBounds2D()); 
//	System.out.println(text + " outBB " + glyphs.getOutline().getBounds2D().getBounds2D()); // same
//	System.out.println(text + " visB " + glyphs.getVisualBounds()); // also same
//	System.out.println(text + " logB " + glyphs.getLogicalBounds());
//	System.out.println(text + " pos " + Arrays.toString(glyphs.getGlyphPositions(0, text.length(), null)));
//
//
//	
//	
//	
//	// these next two are identical
//	System.out.println("TextOutline " + text + " L " + str(g.getLogicalBounds()));
//	Rectangle2D b = g.getFont().getStringBounds(text, FONT_RENDER_CONTEXT);
//	System.out.println("TextOutline " + text + " F " + str(b));
//}
		
		
		
		this.outline = outline;
		this.transform = transform;
	}

//    private String str(Rectangle2D b) {
//    	return "[" + b.getX() + ", " + b.getX() + ", " + b.getWidth()  + ", " + b.getHeight() + "]";
//    }
	/**
     * The text which the outline displays.
     * @return the text
     */
    String text() {
        return text;
    }

    
    public static class TextShape implements Shape {

    	private Shape outline;
		TextOutline textOutline;

		TextShape(Shape outline, TextOutline to) {
    		this.outline = outline;
    		this.textOutline = to;
    	}
		@Override
		public Rectangle getBounds() {
			return outline.getBounds();
		}

		@Override
		public Rectangle2D getBounds2D() {
			return outline.getBounds2D();
		}

		@Override
		public boolean contains(double x, double y) {
			return outline.contains(x, y);
		}

		@Override
		public boolean contains(Point2D p) {
			return outline.contains(p);
		}

		@Override
		public boolean intersects(double x, double y, double w, double h) {
			return outline.intersects(x, y, w, h);
		}

		@Override
		public boolean intersects(Rectangle2D r) {
			return outline.intersects(r);
		}

		@Override
		public boolean contains(double x, double y, double w, double h) {
			return outline.contains(x, y, w, h);
		}

		@Override
		public boolean contains(Rectangle2D r) {
			return outline.contains(r);
		}

		@Override
		public PathIterator getPathIterator(AffineTransform at) {
			return outline.getPathIterator(at);
		}

		@Override
		public PathIterator getPathIterator(AffineTransform at, double flatness) {
			return outline.getPathIterator(at, flatness);
		}
    	
    }
    /**
     * Access the transformed outline of the text.
     *
     * @return transformed outline
     */
    Shape getOutline() {
        return new TextShape(transform.createTransformedShape(outline), this);
    }

    /**
     * Access the transformed bounds of the outline text.
     *
     * @return transformed bounds
     */
    Rectangle2D getBounds() {
        return transformedBounds(outline);
    }

    /**
     * Access the transformed logical bounds of the outline text.
     *
     * @return logical bounds
     */
    Rectangle2D getLogicalBounds() {
        return transformedBounds(glyphs.getLogicalBounds());
    }

    /**
     * Access the bounds of a shape that have been transformed.
     *
     * @param shape any shape
     * @return the bounds of the shape transformed
     */
    private Rectangle2D transformedBounds(Shape shape) {
        Rectangle2D rectangle2D = shape.getBounds2D();
        Point2D minPoint = new Point2D.Double(rectangle2D.getMinX(), rectangle2D.getMinY());
        Point2D maxPoint = new Point2D.Double(rectangle2D.getMaxX(), rectangle2D.getMaxY());

        transform.transform(minPoint, minPoint);
        transform.transform(maxPoint, maxPoint);

        // may be flipped by transformation
        double minX = Math.min(minPoint.getX(), maxPoint.getX());
        double maxX = Math.max(minPoint.getX(), maxPoint.getX());
        double minY = Math.min(minPoint.getY(), maxPoint.getY());
        double maxY = Math.max(minPoint.getY(), maxPoint.getY());

        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Access the transformed center of the whole outline.
     *
     * @return center of outline
     */
    Point2D getCenter() {
        final Rectangle2D bounds = getBounds();
        return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
    }

    /**
     * Access the transformed center of the first glyph outline.
     *
     * @return center of first glyph outline
     */
    Point2D getFirstGlyphCenter() {
        return getGlyphCenter(0);
    }

    /**
     * Access the transformed center of the last glyph outline.
     *
     * @return center of last glyph outline
     */
    Point2D getLastGlyphCenter() {
        return getGlyphCenter(text.length() - 1);
    }

    /**
     * Determines the transformed centre of a specified glyph.
     *
     * @param index glyph index
     * @return center point
     */
    private Point2D getGlyphCenter(final int index) {

        if (text.length() == 1) return getCenter();

        final Shape glyph = glyphs.getGlyphOutline(index);
        
        
        final Rectangle2D glyphBounds = transformedBounds(glyph);

        return new Point2D.Double(glyphBounds.getCenterX(), glyphBounds.getCenterY());
    }

    /**
     * Add a transformation to the outline.
     *
     * @param nextTransform new transformation
     * @return new text outline
     */
    TextOutline transform(AffineTransform nextTransform) {
        final AffineTransform combinedTransform = new AffineTransform();
        combinedTransform.concatenate(nextTransform);
        combinedTransform.concatenate(transform);
        return new TextOutline(text, glyphs, outline, combinedTransform);
    }

    /**
     * Convenience function to resize the outline and maintain the existing
     * center point.
     *
     * @param scaleX scale x-axis
     * @param scaleY scale y-axis
     * @return resized outline
     */
    TextOutline resize(final double scaleX, final double scaleY) {
        final Point2D center = getCenter();
        final AffineTransform transform = new AffineTransform();
        transform.translate(center.getX(), center.getY());
        transform.scale(scaleX, scaleY);
        transform.translate(-center.getX(), -center.getY());
        return transform(transform);
    }

    /**
     * Convenience function to translate the outline.
     *
     * @param translateX x-axis translation
     * @param translateY y-axis translation
     * @return translated outline
     */
    TextOutline translate(final double translateX, final double translateY) {
        return transform(AffineTransform.getTranslateInstance(translateX, translateY));
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String toString() {
        final Rectangle2D bounds = getBounds();
        final StringBuilder sb = new StringBuilder(25);
        sb.append(text);
        sb.append(" [x=").append(formatDouble(bounds.getX()));
        sb.append(", y=").append(formatDouble(bounds.getY()));
        sb.append(", w=").append(formatDouble(bounds.getWidth()));
        sb.append(", h=").append(formatDouble(bounds.getHeight()));
        sb.append(']');
        return sb.toString();
    }

    /**
     * Format a double - displayed as two decimal places.
     *
     * @param value number value
     * @return string of formatted double
     */
    static String formatDouble(double value) {
        return String.format("%.2f", value);
    }
}
