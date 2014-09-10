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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Intermediate between an
 * {@link org.openscience.cdk.renderer.elements.IRenderingElement} and the atom
 * data. The atom symbol represents a visible atom element with zero of more
 * adjuncts. The adjuncts are hydrogen count, charge, and mass. The atom symbol
 * is immutable and modifying a property or transforming the symbol makes a new
 * instance.
 *
 * @author John May
 */
final class AtomSymbol {

    /**
     * The element symbol.
     */
    private final TextOutline       element;

    /**
     * Adjuncts to the symbol, hydrogen count, charge, and mass.
     */
    private final List<TextOutline> adjuncts;

    /**
     * Annotation adjuncts.
     */
    private final List<TextOutline> annotationAdjuncts;

    /**
     * Desired alignment of the symbol.
     */
    private final SymbolAlignment   alignment;

    /**
     * The convex hull of the entire atom symbol.
     */
    private final ConvexHull        hull;

    /**
     * Alignment of symbol, left aligned symbols are centered on the first
     * character, right aligned on the last character.
     */
    enum SymbolAlignment {
        Left, Center, Right
    }

    /**
     * Create a new atom symbol with the specified adjuncts.
     *
     * @param element the element symbol (e.g. 'N' in 'NH4+')
     * @param adjuncts the adjuncts
     */
    AtomSymbol(TextOutline element, List<TextOutline> adjuncts) {
        this.element = element;
        this.adjuncts = adjuncts;
        this.annotationAdjuncts = new ArrayList<TextOutline>();
        this.alignment = SymbolAlignment.Center;
        this.hull = ConvexHull.ofShapes(getOutlines());
    }

    /**
     * Internal constructor provides the attributes.
     *
     * @param element the element label
     * @param adjuncts the adjunct labels
     * @param alignment left, center, or right alignment
     * @param hull convex hull
     */
    private AtomSymbol(TextOutline element, List<TextOutline> adjuncts, List<TextOutline> annotationAdjuncts,
            SymbolAlignment alignment, ConvexHull hull) {
        this.element = element;
        this.adjuncts = adjuncts;
        this.annotationAdjuncts = annotationAdjuncts;
        this.alignment = alignment;
        this.hull = hull;
    }

    /**
     * Create a new atom symbol (from this symbol) but with the specified
     * alignment.
     *
     * @param alignment element alignment
     * @return new atom symbol
     */
    AtomSymbol alignTo(SymbolAlignment alignment) {
        return new AtomSymbol(element, adjuncts, annotationAdjuncts, alignment, hull);
    }

    /**
     * Include a new annotation adjunct in the atom symbol.
     *
     * @param annotation the new annotation adjunct
     * @return a new AtomSymbol instance including the annotation adjunct
     */
    AtomSymbol addAnnotation(TextOutline annotation) {
        List<TextOutline> newAnnotations = new ArrayList<TextOutline>(annotationAdjuncts);
        newAnnotations.add(annotation);
        return new AtomSymbol(element, adjuncts, newAnnotations, alignment, hull);
    }

    /**
     * Access the center point of the symbol. The center point is determined by
     * the alignment.
     *
     * @return center point
     */
    Point2D getAlignmentCenter() {
        if (alignment == SymbolAlignment.Left) {
            return element.getFirstGlyphCenter();
        } else if (alignment == SymbolAlignment.Right) {
            return element.getLastGlyphCenter();
        } else {
            return element.getCenter();
        }
    }

    /**
     * Access the element outline.
     * @return immutable element outline
     */
    TextOutline elementOutline() {
        return element;
    }

    /**
     * Access the adjunct outlines.
     *
     * @return immutable adjunct outlines
     */
    List<TextOutline> adjunctOutlines() {
        return Collections.unmodifiableList(adjuncts);
    }

    /**
     * Access the Java 2D shape text outlines that display the atom symbol.
     *
     * @return shapes
     */
    List<Shape> getOutlines() {
        List<Shape> shapes = new ArrayList<Shape>();
        shapes.add(element.getOutline());
        for (TextOutline adjunct : adjuncts)
            shapes.add(adjunct.getOutline());
        return shapes;
    }

    /**
     * Access the java.awt.Shape outlines of each annotation adjunct.
     *
     * @return annotation outlines
     */
    List<Shape> getAnnotationOutlines() {
        List<Shape> shapes = new ArrayList<Shape>();
        for (TextOutline adjunct : annotationAdjuncts)
            shapes.add(adjunct.getOutline());
        return shapes;
    }

    /**
     * Access the convex hull of the whole atom symbol.
     *
     * @return convex hull
     */
    ConvexHull getConvexHull() {
        return hull;
    }

    /**
     * Transform the position and orientation of the symbol.
     *
     * @param transform affine transform
     * @return the transformed symbol (new instance)
     */
    AtomSymbol transform(AffineTransform transform) {
        List<TextOutline> transformedAdjuncts = new ArrayList<TextOutline>(adjuncts.size());
        for (TextOutline adjunct : adjuncts)
            transformedAdjuncts.add(adjunct.transform(transform));
        List<TextOutline> transformedAnnAdjuncts = new ArrayList<TextOutline>(adjuncts.size());
        for (TextOutline adjunct : annotationAdjuncts)
            transformedAnnAdjuncts.add(adjunct.transform(transform));
        return new AtomSymbol(element.transform(transform), transformedAdjuncts, transformedAnnAdjuncts, alignment,
                hull.transform(transform));
    }

    /**
     * Convenience function to resize an atom symbol.
     *
     * @param scaleX x-axis scaling
     * @param scaleY y-axis scaling
     * @return the resized symbol (new instance)
     */
    AtomSymbol resize(double scaleX, double scaleY) {
        Point2D center = element.getCenter();
        AffineTransform transform = new AffineTransform();
        transform.translate(center.getX(), center.getY());
        transform.scale(scaleX, scaleY);
        transform.translate(-center.getX(), -center.getY());
        return transform(transform);
    }

    /**
     * Convenience function to center an atom symbol on a specified point. The
     * centering depends on the symbol alignment.
     *
     * @param x x-axis location
     * @param y y-axis location
     * @return the centered symbol (new instance)
     */
    AtomSymbol center(double x, double y) {
        Point2D center = getAlignmentCenter();
        return translate(x - center.getX(), y - center.getY());
    }

    /**
     * Convenience function to translate an atom symbol on a specified point.
     *
     * @param x x-axis location
     * @param y y-axis location
     * @return the translated symbol (new instance)
     */
    AtomSymbol translate(double x, double y) {
        return transform(AffineTransform.getTranslateInstance(x, y));
    }
}
