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
import java.util.HashSet;
import java.util.List;
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
    Above, Right, Below, Left;

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
     * Determine an appropriate position for the hydrogen label of an atom with
     * the specified neighbors.
     * 
     * @param atom the atom to which the hydrogen position is being determined
     * @param neighbors atoms adjacent to the 'atom'
     * @return a hydrogen position
     */
    static HydrogenPosition position(final IAtom atom, final List<IAtom> neighbors) {

        final List<Vector2d> vectors = newUnitVectors(atom, neighbors);
        final Vector2d average = average(vectors);

        if (neighbors.size() > 1) {
            // TODO: when average.length() is small cardinal direction doesn't
            // do great, this happens with symmetric bonding and ideally we
            // should 'sweep' around and find the best free or least cluttered
            // position
            return usingCardinalDirection(average);
        } else if (neighbors.size() == 1) {
            return average.x > VERTICAL_THRESHOLD ? Left : Right;
        } else {
            return usingDefaultPlacement(atom);
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
