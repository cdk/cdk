/* Copyright (C) 2015  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.depict;

import org.openscience.cdk.renderer.elements.Bounds;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Internal: Immutable value class to help with store diagram dimensions
 * (a tuple width and height). Given some dimensions we can add/subtract,
 * grow/shrink as needed. Utility methods are provided for laying out rendering
 * elements in grids and rows.
 */
@SuppressWarnings("PMD.ShortVariable")
final class Dimensions {

    /**
     * Magic value for automated sizing.
     */
    final static Dimensions AUTOMATIC = new Dimensions(DepictionGenerator.AUTOMATIC,
                                                       DepictionGenerator.AUTOMATIC);

    /**
     * The values.
     */
    final double w, h;

    public Dimensions(double w, double h) {
        this.w = w;
        this.h = h;
    }

    Dimensions add(double w, double h) {
        return new Dimensions(this.w + w, this.h + h);
    }

    Dimensions scale(double coef) {
        return new Dimensions(coef * w, coef * h);
    }

    static Dimensions ofRow(List<Bounds> elems) {
        return ofGrid(elems, 1, elems.size());
    }

    static Dimensions ofCol(List<Bounds> elems) {
        return ofGrid(elems, elems.size(), 1);
    }

    static Dimensions ofGrid(List<Bounds> bounds, int nRow, int nCol) {
        return ofGrid(bounds, new double[nRow + 1], new double[nCol + 1]);
    }

    /**
     * Determine how much space is needed to depiction the bound {@link IRenderingElements} if
     * they were aligned in a grid without padding or margins. The method takes arrays
     * for for the offset which are one item bigger than the size of the gird
     * (e.g. 3x2 would need arrays of length 4 and 2). The arrays are filled with the
     * cumulative width/heights for each grid point allowing easy alignment.
     *
     * @param bounds  bound rendering elements
     * @param yOffset array for col offsets
     * @param xOffset array for row offset
     * @return the dimensions required
     */
    static Dimensions ofGrid(List<Bounds> bounds, double[] yOffset, double[] xOffset) {
        int nRow = yOffset.length - 1;
        int nCol = xOffset.length - 1;

        int nBounds = bounds.size();
        for (int i = 0; i < nBounds; i++) {
            // +1 because first offset is always 0
            int col = 1 + i % nCol;
            int row = 1 + i / nCol;
            final Bounds bound = bounds.get(i);
            if (bound.isEmpty())
                continue;
            double width  = bound.width();
            double height = bound.height();
            if (width > xOffset[col])
                xOffset[col] = width;
            if (height > yOffset[row])
                yOffset[row] = height;
        }

        for (int i = 1; i < yOffset.length; i++)
            yOffset[i] += yOffset[i - 1];
        for (int i = 1; i < xOffset.length; i++)
            xOffset[i] += xOffset[i - 1];

        return new Dimensions(xOffset[nCol], yOffset[nRow]);
    }

    /**
     * Determine grid size (nrow, ncol) that could be used
     * for displaying a given number of elements.
     *
     * @param nElem number of elements
     * @return grid dimensions (integers)
     */
    static Dimension determineGrid(int nElem) {
        switch (nElem) {
            case 0:
                return new Dimension(0, 0);
            case 1:
                return new Dimension(1, 1);
            case 2:
                return new Dimension(2, 1);
            case 3:
                return new Dimension(3, 1);
            case 4:
                return new Dimension(2, 2);
            case 5:
                return new Dimension(3, 2);
            case 6:
                return new Dimension(3, 2);
            case 7:
                return new Dimension(4, 2);
            case 8:
                return new Dimension(4, 2);
            case 9:
                return new Dimension(3, 3);
            default:
                // not great but okay
                int nrow = (int) Math.floor(Math.sqrt(nElem));
                int ncol = (int) Math.ceil(nElem / (double) nrow);
                return new Dimension(ncol, nrow);
        }
    }

    @Override
    public String toString() {
        return Math.ceil(w) + "x" + Math.ceil(h);
    }
}
