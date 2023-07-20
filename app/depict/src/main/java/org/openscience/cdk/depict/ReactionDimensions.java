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
    final Dimensions sideDim;
    final Dimensions mainDim;
    final Dimensions condDim;
    final Dimensions titleDim;
    final double padding;

    double[]   xOffsets, yOffsets;
    double[] xOffsetSide, yOffsetSide;

    ReactionDimensions(Dimensions sideDim,
                       Dimensions mainDim,
                       Dimensions condDim,
                       Dimensions titleDim,
                       double padding) {
        this.sideDim = sideDim;
        this.mainDim = mainDim;
        this.condDim = condDim;
        this.titleDim = titleDim;
        this.padding = padding;
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
                                                           padding);
        result.xOffsets = scale(xOffsets, amount);
        result.yOffsets = scale(yOffsets, amount);
        result.xOffsetSide = scale(xOffsetSide, amount);
        result.yOffsetSide = scale(yOffsetSide, amount);
        return result;
    }

    public double mainRowHeight() {
        return yOffsets[1];
    }
}
