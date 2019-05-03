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

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;

import javax.vecmath.Vector2d;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openscience.cdk.renderer.generators.standard.VecmathUtil.average;
import static org.openscience.cdk.renderer.generators.standard.VecmathUtil.newUnitVectors;

/**
 * Enumeration of hydrogen label position for 2D depictions. The best placement of the
 * label can depend on a variety of factors. Currently, the {@link #position(IAtom, List)}
 * method decides the position based on the atom and neighbouring atom coordinates.
 *
 * @author John May
 */
enum HydrogenPosition {
    Right(0, new Vector2d(1, 0)), Left(Math.PI, new Vector2d(-1, 0)), Above(Math.PI / 2, new Vector2d(0, 1)), Below(
            Math.PI + (Math.PI / 2), new Vector2d(0, -1));

    /**
     * When a single atom is displayed in isolation the position defaults to the
     * right unless the element is listed here. This allows us to correctly
     * displayed H2O not OH2 and CH4 not H4C.
     */
    private static final Set<Elements> PREFIXED_H         = new HashSet<Elements>(Arrays.asList(Elements.Oxygen,
                                                                  Elements.Sulfur, Elements.Selenium,
                                                                  Elements.Tellurium, Elements.Fluorine,
                                                                  Elements.Chlorine, Elements.Bromine, Elements.Iodine));

    /**
     * When an atom has a single bond, the position is left or right depending
     * only on this bond. This threshold defines the position at which we flip
     * from positioning hydrogens on the right to positioning them on the left.
     * A positive value favours placing them on the right, a negative on the
     * left.
     */
    private static final double        VERTICAL_THRESHOLD = 0.1;

    /**
     * Tau = 2π.
     */
    private static final double        TAU                = Math.PI + Math.PI;

    /**
     * Direction this position is pointing in radians.
     */
    private final double               direction;
    private final Vector2d             vector;

    /**
     * Internal - create a hydrogen position pointing int he specified direction.
     * @param direction angle of the position in radians
     */
    HydrogenPosition(double direction, Vector2d vector) {
        this.direction = direction;
        this.vector = vector;
    }

    /**
     * Access the directional vector for this hydrogen position.
     *
     * @return the directional vector for this hydrogen position.
     */
    Vector2d vector() {
        return vector;
    }

    /**
     * Determine an appropriate position for the hydrogen label of an atom with
     * the specified neighbors.
     *
     * @param atom the atom to which the hydrogen position is being determined
     * @param neighbors atoms adjacent to the 'atom'
     * @return a hydrogen position
     */
    static HydrogenPosition position(final IAtom atom, final List<IAtom> neighbors) {

        final List<Vector2d> vectors = newUnitVectors(atom, neighbors);

        if (neighbors.size() > 2) {
            return usingAngularExtent(vectors);
        } else if (neighbors.size() > 1) {
            return usingCardinalDirection(average(vectors));
        } else if (neighbors.size() == 1) {
            return vectors.get(0).x > VERTICAL_THRESHOLD ? Left : Right;
        } else {
            return usingDefaultPlacement(atom);
        }
    }

    /**
     * Using the angular extents of vectors, determine the best position for a hydrogen label. The
     * position with the most space is selected first. If multiple positions have the same amount of
     * space, the one where the hydrogen position is most centred is selected. If all position are
     * okay, the priority is Right > Left > Above > Below.
     *
     * @param vectors directional vectors for each bond from an atom
     * @return best hydrogen position
     */
    static HydrogenPosition usingAngularExtent(final List<Vector2d> vectors) {

        double[] extents = VecmathUtil.extents(vectors);
        Arrays.sort(extents);

        Map<HydrogenPosition, OffsetExtent> extentMap = new HashMap<HydrogenPosition, OffsetExtent>();

        for (int i = 0; i < extents.length; i++) {
            final double before = extents[i];
            final double after = extents[(i + 1) % extents.length];

            for (final HydrogenPosition position : values()) {

                // adjust the extents such that this position is '0'
                final double bias = TAU - position.direction;
                double afterBias = after + bias;
                double beforeBias = before + bias;

                // ensure values are 0 <= x < Tau
                if (beforeBias >= TAU) beforeBias -= TAU;
                if (afterBias >= TAU) afterBias -= TAU;

                // we can now determine the extents before and after this
                // hydrogen position
                final double afterExtent = afterBias;
                final double beforeExtent = TAU - beforeBias;

                // the total extent is amount of space between these two bonds
                // when sweeping round. The offset is how close this hydrogen
                // position is to the center of the extent.
                final double totalExtent = afterExtent + beforeExtent;
                final double offset = Math.abs(totalExtent / 2 - beforeExtent);

                // for each position keep the one with the smallest extent this is
                // the most space available without another bond getting in the way
                OffsetExtent offsetExtent = extentMap.get(position);
                if (offsetExtent == null || totalExtent < offsetExtent.extent) {
                    extentMap.put(position, new OffsetExtent(totalExtent, offset));
                }
            }
        }

        // we now have the offset extent for each position that we can sort and prioritise
        Set<Map.Entry<HydrogenPosition, OffsetExtent>> extentEntries = extentMap.entrySet();
        Map.Entry<HydrogenPosition, OffsetExtent> best = null;
        for (Map.Entry<HydrogenPosition, OffsetExtent> e : extentEntries) {
            if (best == null || ExtentPriority.INSTANCE.compare(e, best) < 0) best = e;
        }

        assert best != null;
        return best.getKey();
    }

    /**
     * A simple value class that stores a tuple of an angular extent and an offset.
     */
    private static final class OffsetExtent {

        private final double extent;
        private final double offset;

        /**
         * Internal - create pairing of angular extent and offset.
         * @param extent the angular extent
         * @param offset offset from the centre of the extent
         */
        private OffsetExtent(double extent, double offset) {
            this.extent = extent;
            this.offset = offset;
        }

        /**{@inheritDoc} */
        @Override
        public String toString() {
            return String.format("%.2f, %.2f", extent, offset);
        }
    }

    /**
     * Comparator to prioritise {@link OffsetExtent}s.
     */
    private static enum ExtentPriority implements Comparator<Map.Entry<HydrogenPosition, OffsetExtent>> {
        INSTANCE;

        @Override
        public int compare(Map.Entry<HydrogenPosition, OffsetExtent> a, Map.Entry<HydrogenPosition, OffsetExtent> b) {

            OffsetExtent aExtent = a.getValue();
            OffsetExtent bExtent = b.getValue();

            // if difference in extents is noticeable, favour the one
            // with a larger extent
            double extentDiff = bExtent.extent - aExtent.extent;
            if (Math.abs(extentDiff) > 0.05) return (int) Math.signum(extentDiff);

            // if the difference in offset is noticeable, favour the one
            // with the smaller offset (position is more centered)
            double offsetDiff = bExtent.offset - aExtent.offset;
            if (Math.abs(offsetDiff) > 0.05) return (int) -Math.signum(offsetDiff);

            // favour Right > Left > Above > Below
            return a.getKey().compareTo(b.getKey());
        }
    }

    /**
     * By snapping to the cardinal direction (compass point) of the provided
     * vector, return the position opposite the 'snapped' coordinate.
     *
     * @param opposite position the hydrogen label opposite to this vector
     * @return the position
     */
    static HydrogenPosition usingCardinalDirection(final Vector2d opposite) {
        final double theta = Math.atan2(opposite.y, opposite.x);
        final int direction = (int) Math.round(theta / (Math.PI / 4));

        switch (direction) {
            case -4: // W
            case -3: // SW
                return Right;
            case -2: // S
                return Above;
            case -1: // SE
            case 0: // E
            case 1: // NE
                return Left;
            case 2: // N
                return Below;
            case 3: // NW
            case 4: // W?
                return Right;
        }

        return Right; // never reached
    }

    /**
     * Access the default position of the hydrogen label when the atom has no
     * bonds.
     *
     * @param atom hydrogens will be labelled
     * @return the position
     */
    static HydrogenPosition usingDefaultPlacement(final IAtom atom) {
        if (PREFIXED_H.contains(Elements.ofNumber(atom.getAtomicNumber()))) return Left;
        return Right;
    }
}
