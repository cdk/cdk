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

import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.Bounds;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class to capture all the pieces that can/need be
 * positioned to display a reaction.
 */
final class ReactionBounds {
    RendererModel model;
    List<Bounds> reactants = new ArrayList<>();
    List<Bounds> products = new ArrayList<>();
    List<Bounds> reactantLabels = new ArrayList<>();
    List<Bounds> productLabels = new ArrayList<>();
    List<Bounds> aboveArrow = new ArrayList<>();
    List<Bounds> belowArrow = new ArrayList<>();
    IReaction.Direction direction = IReaction.Direction.FORWARD;
    Bounds plus = null;
    Bounds title = null;

    List<Bounds> getMainRow() {
        List<Bounds> mainRow = new ArrayList<>();
        for (int i = 0; i < reactants.size(); i++) {
            if (i != 0) mainRow.add(plus);
            mainRow.add(reactants.get(i));
        }
        if (direction != null)
            mainRow.add(new Bounds()); // arrow
        for (int i = 0; i < products.size(); i++) {
            if (i != 0) mainRow.add(plus);
            mainRow.add(products.get(i));
        }
        return mainRow;
    }

    boolean hasMainRowLabels() {
        return !reactantLabels.isEmpty() || !productLabels.isEmpty();
    }

    List<Bounds> getMainRowLabels() {
        if (!hasMainRowLabels())
            return Collections.emptyList();
        List<Bounds> labels = new ArrayList<>();
        for (int i = 0; i < reactants.size(); i++) {
            if (i != 0) labels.add(new Bounds());
            if (i < reactantLabels.size())
                labels.add(reactantLabels.get(i));
            else
                labels.add(new Bounds());
        }
        if (direction != null)
            labels.add(new Bounds()); // arrow
        for (int i = 0; i < productLabels.size(); i++) {
            if (i != 0) labels.add(new Bounds());
            labels.add(productLabels.get(i));
        }
        return labels;
    }

    List<Bounds> getMainComponents() {
        List<Bounds> bounds = new ArrayList<>(getMainRow());
        if (hasMainRowLabels())
            bounds.addAll(getMainRowLabels());
        return bounds;
    }

    int getArrowIndex() {
        int numGaps = Math.max(0, reactants.size()-1);
        return reactants.size() + numGaps;
    }

    ReactionDimensions getDimensions(double padding) {

        List<Bounds> mainComp = getMainComponents();

        int nRow;
        int nCol;
        double arrowHeight      = plus.height();
        double arrowHeadLength  = plus.height();
        double minArrowWidth    = direction != null && direction != IReaction.Direction.UNDIRECTED ? 5 * arrowHeight : arrowHeight;

        double[] xOffsets, yOffsets;
        double[] xOffsetSide, yOffsetSide;
        double[] yOffsetBelow;

        if (hasMainRowLabels()) {
            nRow = 2;
            nCol = mainComp.size()/2;
        } else {
            nRow = 1;
            nCol = mainComp.size();
        }

        Dimensions mainDim = Dimensions.ofGrid(mainComp,
                                               yOffsets = new double[nRow + 1],
                                               xOffsets = new double[nCol + 1]);

        // important we flip x/y so things above the arrow get stacked
        Dimension sideGrid = Dimensions.determineGrid(aboveArrow.size());
        Dimensions aboveArrowDim = Dimensions.ofGrid(aboveArrow,
                                                     yOffsetSide = new double[sideGrid.width + 1],
                                                     xOffsetSide = new double[sideGrid.height + 1]);

        Dimensions belowArrowDim = Dimensions.ofGrid(belowArrow,
                                                     yOffsetBelow = new double[belowArrow.size() + 1],
                                                     new double[2]);
        double middleRequired = 2*padding + arrowHeadLength +
                Math.max(aboveArrowDim.w + Math.max(0, xOffsetSide.length-2) * padding,
                         belowArrowDim.w);

        ReactionDimensions result;
        if (middleRequired < minArrowWidth) {

            // the arrow is bigger than the things above/below the arrow,
            // ensure these are centered relative to the arrow

            // adjust x-offset so side components are centered
            double xAdjust = (minArrowWidth - middleRequired) / 2;
            for (int i = 0; i < xOffsetSide.length; i++)
                xOffsetSide[i] += xAdjust;

            // need to recenter agents
            if (belowArrowDim.w > aboveArrowDim.w) {
                for (int i = 0; i < xOffsetSide.length; i++)
                    xOffsetSide[i] += (belowArrowDim.w - aboveArrowDim.w) / 2;
            }

            // update side dims
            Dimensions sideDim = new Dimensions(minArrowWidth, aboveArrowDim.h);
            Dimensions condDim = new Dimensions(minArrowWidth, belowArrowDim.h);
            Dimensions titleDim = new Dimensions(title.width(),
                                                 title.height());

            for (int j=getArrowIndex()+1; j<xOffsets.length; j++)
                xOffsets[j] += minArrowWidth;
            result = new ReactionDimensions(sideDim, mainDim, condDim, titleDim, padding);
        }
        else {

            // above/below is larger than arrow, make the arrow as big as needed

            // need to re-center agents
            if (belowArrowDim.w > aboveArrowDim.w) {
                for (int i = 0; i < xOffsetSide.length; i++)
                    xOffsetSide[i] += (belowArrowDim.w - aboveArrowDim.w) / 2;
            }

            for (int j=getArrowIndex()+1; j<xOffsets.length; j++)
                xOffsets[j] += middleRequired;

            Dimensions sideDim = new Dimensions(aboveArrowDim.w,
                                                aboveArrowDim.h);
            Dimensions condDim = new Dimensions(middleRequired,
                                                belowArrowDim.h);
            Dimensions titleDim = new Dimensions(title.width(),
                                                 title.height());
            result = new ReactionDimensions(sideDim, mainDim, condDim, titleDim, padding);
        }

        result.xOffsets = xOffsets;
        result.yOffsets = yOffsets;
        result.xOffsetAbove = xOffsetSide;
        result.yOffsetAbove = yOffsetSide;
        result.yOffsetBelow = yOffsetBelow;

        return result;
    }


}
