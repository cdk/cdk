/*
 * Copyright (C) 2023 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.depict;

import java.util.Arrays;

/** Helper class */
class ReactionDimensions {
    // dimensions and spacing of side components
    Dimensions sideDim;
    Dimensions mainDim;
    Dimensions condDim;
    Dimensions titleDim;
    final double padding;
    final double scale;

    double[]   xOffsets, yOffsets;
    double[] xOffsetSide, yOffsetSide;

    ReactionDimensions(Dimensions sideDim,
                       Dimensions mainDim,
                       Dimensions condDim,
                       Dimensions titleDim,
                       double scale,
                       double padding) {
        this.sideDim = sideDim;
        this.mainDim = mainDim;
        this.condDim = condDim;
        this.titleDim = titleDim;
        this.scale = scale;
        this.padding = padding;
    }

    ReactionDimensions(Dimensions sideDim,
                       Dimensions mainDim,
                       Dimensions condDim,
                       Dimensions titleDim,
                       double padding) {
        this(sideDim, mainDim, condDim, titleDim, 1.0, padding);
    }

    private static double[] scale(double[] values, double ammount) {
        double[] cpy = Arrays.copyOf(values, values.length);
        for (int i=0; i<values.length; i++)
            cpy[i] *= ammount;
        return cpy;
    }

    ReactionDimensions scale(double amount) {
        Dimensions sideRequired = sideDim.scale(amount);
        Dimensions mainRequired = mainDim.scale(amount);
        Dimensions condRequired = condDim.scale(amount);
        Dimensions titleRequired = titleDim.scale(amount);

        // important! padding does not get scaled
        ReactionDimensions result = new ReactionDimensions(sideRequired,
                                                           mainRequired,
                                                           condRequired,
                                                           titleRequired,
                                                           scale * amount,
                                                           padding * amount);

        result.xOffsets = scale(xOffsets, amount);
        result.yOffsets = scale(yOffsets, amount);
        result.xOffsetSide = scale(xOffsetSide, amount);
        result.yOffsetSide = scale(yOffsetSide, amount);
        return result;
    }

    double mainRowHeight() {
        return yOffsets[1];
    }

    Dimensions calcTotalDimensions(Dimensions requested, String fmt) {
        if (requested != Dimensions.AUTOMATIC)
            return requested;

        final double firstRowHeight = yOffsets[1];

        final int nSideCol = xOffsetSide.length - 1;
        final int nSideRow = yOffsetSide.length - 1;

        double mainCompOffset = (sideDim.h + padding + ((nSideRow-1) * padding)) - (firstRowHeight / 2);
        if (mainCompOffset < 0)
            mainCompOffset = 0;

        double titleExtra = Math.max(0, titleDim.h);
        if (titleExtra > 0)
            titleExtra += padding;

        int nCol = xOffsets.length - 1;
        int nRow = yOffsets.length - 1;

        double offsetWidth = xOffsets[xOffsets.length - 1];
        return new Dimensions(offsetWidth, mainDim.h)
                      .add(Math.max(0, nCol - 1) * padding, (nRow - 1) * padding)
                      .add(0, mainCompOffset)
                      .add(0, titleExtra);
    }
}
