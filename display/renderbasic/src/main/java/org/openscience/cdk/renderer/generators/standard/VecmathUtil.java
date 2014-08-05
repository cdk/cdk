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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A collection of static utilities for Java 3D javax.vecmath.* objects.
 * 
 * @author John May
 */
final class VecmathUtil {

    /**
     * Instantiation is disabled.
     */
    private VecmathUtil() {}

    /**
     * Convert a Vecmath (javax.vecmath.*) point to an AWT (java.awt.geom.*)
     * point.
     * 
     * @param point a Vecmath point
     * @return an AWT point
     */
    static Point2D toAwtPoint(Point2d point) {
        return new Point2D.Double(point.x, point.y);
    }

    /**
     * Convert a AWT (java.awt.geom.*) point to a Vecmath (javax.vecmath.*)
     * point.
     * 
     * @param point an AWT point
     * @return a Vecmath point
     */
    static Point2d toVecmathPoint(Point2D point) {
        return new Point2d(point.getX(), point.getY());
    }

    /**
     * Create a unit vector between two points.
     * 
     * @param from start of vector
     * @param to end of vector
     * @return unit vector
     */
    static Vector2d newUnitVector(final Point2d from, final Point2d to) {
        final Vector2d vector = new Vector2d(to.x - from.x, to.y - from.y);
        vector.normalize();
        return vector;
    }

    /**
     * Create unit vectors from one atom to all other provided atoms.
     * 
     * @param fromAtom reference atom (will become 0,0)
     * @param toAtoms list of to atoms
     * @return unit vectors
     */
    static List<Vector2d> newUnitVectors(final IAtom fromAtom, final List<IAtom> toAtoms) {
        final List<Vector2d> unitVectors = new ArrayList<Vector2d>(toAtoms.size());
        for (final IAtom toAtom : toAtoms) {
            unitVectors.add(newUnitVector(fromAtom.getPoint2d(), toAtom.getPoint2d()));
        }
        return unitVectors;
    }

    /**
     * Create a new vector perpendicular (at a right angle) to the provided
     * vector. In 2D, there are two perpendicular vectors, the other
     * perpendicular vector can be obtained by negation.
     * 
     * @param vector reference to which a perpendicular vector is returned
     * @return perpendicular vector
     */
    static Vector2d newPerpendicularVector(final Vector2d vector) {
        return new Vector2d(-vector.y, vector.x);
    }

    /**
     * Scale a vector by a given factor, the input vector is not modified.
     * 
     * @param vector a vector to scale
     * @param factor how much the input vector should be scaled
     * @return scaled vector
     */
    static Vector2d scale(final Tuple2d vector, final double factor) {
        final Vector2d cpy = new Vector2d(vector);
        cpy.scale(factor);
        return cpy;
    }

    /**
     * Sum the components of two vectors, the input is not modified.
     * 
     * @param a first vector
     * @param b second vector
     * @return scaled vector
     */
    static Vector2d sum(final Tuple2d a, final Tuple2d b) {
        return new Vector2d(a.x + b.x, a.y + b.y);
    }

    /**
     * Negate a vector, the input is not modified. Equivalent to
     * {@code scale(vector, -1)}
     * 
     * @param vector a vector to negate
     * @return the negated vector
     */
    static Vector2d negate(final Tuple2d vector) {
        return new Vector2d(-vector.x, -vector.y);
    }

    /**
     * Given vectors for the hypotenuse and adjacent side of a right angled
     * triangle and the length of the opposite side, determine how long the
     * adjacent side size.
     * 
     * @param hypotenuse vector for the hypotenuse
     * @param adjacent vector for the adjacent side
     * @param oppositeLength length of the opposite side of a triangle
     * @return length of the adjacent side
     */
    static double adjacentLength(Vector2d hypotenuse, Vector2d adjacent, double oppositeLength) {
        return Math.tan(hypotenuse.angle(adjacent)) * oppositeLength;
    }

    /**
     * Average a collection of vectors.
     * 
     * @param vectors one or more vectors
     * @return average vector
     */
    static Vector2d average(final Collection<Vector2d> vectors) {
        final Vector2d average = new Vector2d(0, 0);
        for (final Vector2d vector : vectors) {
            average.add(vector);
        }
        average.scale(1d / vectors.size());
        return average;
    }

    /**
     * Given a list of unit vectors, find the vector which is nearest to a
     * provided reference.
     * 
     * @param reference a target vector
     * @param vectors list of vectors
     * @return the nearest vector
     * @throws java.lang.IllegalArgumentException no vectors provided
     */
    static Vector2d getNearestVector(final Vector2d reference, final List<Vector2d> vectors) {

        if (vectors.isEmpty()) throw new IllegalArgumentException("No vectors provided");

        // to find the closest vector we find use the dot product,
        // for the general case (non-unit vectors) one can use the
        // cosine similarity
        Vector2d closest = vectors.get(0);
        double maxProd = reference.dot(closest);

        for (int i = 1; i < vectors.size(); i++) {
            double newProd = reference.dot(vectors.get(i));
            if (newProd > maxProd) {
                maxProd = newProd;
                closest = vectors.get(i);
            }
        }

        return closest;
    }

    /**
     * Given a list of bonds, find the bond which is nearest to a provided
     * reference and return the vector for this bond.
     * 
     * @param reference a target vector
     * @param fromAtom an atom (will be 0,0)
     * @param bonds list of bonds containing 'fromAtom'
     * @return the nearest vector
     */
    static Vector2d getNearestVector(Vector2d reference, IAtom fromAtom, List<IBond> bonds) {

        final List<IAtom> toAtoms = new ArrayList<IAtom>();
        for (IBond bond : bonds) {
            toAtoms.add(bond.getConnectedAtom(fromAtom));
        }

        return getNearestVector(reference, newUnitVectors(fromAtom, toAtoms));
    }
}
