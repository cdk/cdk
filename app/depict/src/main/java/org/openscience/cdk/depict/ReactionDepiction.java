/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.depict;

import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Internal - depiction of a single reaction. We divide the reaction into two draw steps.
 * The first step draws the main components (reactants and products) whilst the second
 * draws the side components (agents: catalysts, solvents, spectators, etc). Reaction
 * direction is drawn a single headed arrow (forward and backward) or an equilibrium
 * (bidirectional).
 */
final class ReactionDepiction extends Depiction {

    private final RendererModel model;
    private final Dimensions    dimensions;

    // molecule sets and titles
    private final ReactionBounds     reactionBounds;
    private final ReactionDimensions reactionDimensions;

    // arrow info
    private final int                 arrowIdx;
    private final IReaction.Direction direction;
    private final double              arrowHeight;

    private final Color fgcol;

    public ReactionDepiction(RendererModel model,
                             ReactionBounds reactionBounds,
                             IReaction.Direction direction,
                             Dimensions dimensions,
                             Color fgcol) {
        super(model);
        this.model = model;
        this.dimensions = dimensions;
        this.fgcol = fgcol;

        this.reactionBounds = reactionBounds;

        // arrow params
        this.arrowIdx      = reactionBounds.getArrowIndex();
        this.direction     = direction;
        this.arrowHeight   = reactionBounds.plus.height();

        this.reactionDimensions = reactionBounds.getDimensions();
    }

    @Override
    public BufferedImage toImg() {

        List<Bounds> mainComp  = reactionBounds.getMainComponents();
        List<Bounds> sideComps = reactionBounds.aboveArrow;

        // format margins and padding for raster images
        final double scale = model.get(BasicSceneGenerator.Scale.class);
        final double zoom = model.get(BasicSceneGenerator.ZoomFactor.class);
        final double margin = getMarginValue(DepictionGenerator.DEFAULT_PX_MARGIN);
        final double padding = getPaddingValue(DEFAULT_PADDING_FACTOR * margin);

        // work out the required space of the main and side components separately
        // will draw these in two passes (main then side) hence want different offsets for each
        final int nSideCol = reactionDimensions.xOffsetSide.length - 1;
        final int nSideRow = reactionDimensions.yOffsetSide.length - 1;

        Dimensions sideRequired = reactionDimensions.sideDim.scale(scale * zoom);
        Dimensions mainRequired = reactionDimensions.mainDim.scale(scale * zoom);
        Dimensions condRequired = reactionDimensions.condDim.scale(scale * zoom);

        Dimensions titleRequired = new Dimensions(reactionBounds.title.width(),
                                                  reactionBounds.title.height()).scale(scale * zoom);

        final double firstRowHeight = scale * zoom * reactionDimensions.yOffsets[1];
        final Dimensions total = calcTotalDimensions(margin, padding, mainRequired, sideRequired, titleRequired, firstRowHeight, null);
        final double fitting = calcFitting(margin, padding, mainRequired, sideRequired, titleRequired, firstRowHeight, null);


        // create the image for rendering
        final BufferedImage img = new BufferedImage((int) Math.ceil(total.w), (int) Math.ceil(total.h),
                                                    BufferedImage.TYPE_4BYTE_ABGR);

        // we use the AWT for vector graphics if though we're raster because
        // fractional strokes can be figured out by interpolation, without
        // when we shrink diagrams bonds can look too bold/chubby
        final Graphics2D g2 = img.createGraphics();
        final IDrawVisitor visitor = AWTDrawVisitor.forVectorGraphics(g2);
        visitor.setTransform(AffineTransform.getScaleInstance(1,-1));
        visitor.visit(new RectangleElement(0, -(int) Math.ceil(total.h), (int) Math.ceil(total.w), (int) Math.ceil(total.h),
                                           true, model.get(BasicSceneGenerator.BackgroundColor.class)));


        // compound the zoom, fitting and scaling into a single value
        final double rescale = zoom * fitting * scale;
        double mainCompOffset;

        // shift product x-offset to make room for the arrow / side components
        mainCompOffset = fitting * sideRequired.h + nSideRow * padding - fitting * firstRowHeight / 2;
        for (int i = arrowIdx + 1; i < reactionDimensions.xOffsets.length; i++) {
            reactionDimensions.xOffsets[i] += sideRequired.w * 1 / (scale * zoom);
        }

        int nCol = reactionDimensions.xOffsets.length;
        int nRow = reactionDimensions.yOffsets.length;

        // MAIN COMPONENTS DRAW
        // x,y base coordinates include the margin and centering (only if fitting to a size)
        final double totalRequiredWidth = 2 * margin + Math.max(0, nCol - 1) * padding + Math.max(0, nSideCol - 1) * padding + (rescale * reactionDimensions.xOffsets[nCol]);
        final double totalRequiredHeight = 2 * margin + Math.max(0, nRow - 1) * padding + (!reactionBounds.title.isEmpty() ? padding : 0) + Math.max(mainCompOffset, 0) + fitting * mainRequired.h + fitting * Math.max(0, titleRequired.h);
        double xBase = margin + (total.w - totalRequiredWidth) / 2;
        double yBase = margin + Math.max(mainCompOffset, 0) + (total.h - totalRequiredHeight) / 2;
        for (int i = 0; i < mainComp.size(); i++) {

            final int row = i / nCol;
            final int col = i % nCol;

            // calc the 'view' bounds:
            //  amount of padding depends on which row or column we are in.
            //  the width/height of this col/row can be determined by the next offset
            double x = xBase + col * padding + rescale * reactionDimensions.xOffsets[col];
            double y = yBase + row * padding + rescale * reactionDimensions.yOffsets[row];
            double w = rescale * (reactionDimensions.xOffsets[col + 1] - reactionDimensions.xOffsets[col]);
            double h = rescale * (reactionDimensions.yOffsets[row + 1] - reactionDimensions.yOffsets[row]);

            // intercept arrow draw and make it as big as need
            if (i == arrowIdx) {
                w = rescale * (reactionDimensions.xOffsets[i + 1] - reactionDimensions.xOffsets[i]) + Math.max(0, nSideCol - 1) * padding;
                draw(visitor,
                     1, // no zoom since arrows is drawn as big as needed
                     createArrow(direction, fgcol, w, arrowHeight * rescale),
                     rect(x, y, w, h));
                continue;
            }

            // extra padding from the side components
            if (i > arrowIdx)
                x += Math.max(0, nSideCol - 1) * padding;

            // skip empty elements
            final Bounds bounds = mainComp.get(i);
            if (bounds.isEmpty())
                continue;

            draw(visitor, zoom, bounds, rect(x, y, w, h));
        }

        // RXN TITLE DRAW
        if (!reactionBounds.title.isEmpty()) {
            double y = yBase + nRow * padding + rescale * reactionDimensions.yOffsets[nRow];
            double h = rescale * reactionBounds.title.height();
            draw(visitor, zoom, reactionBounds.title, rect(0, y, total.w, h));
        }

        // SIDE COMPONENTS DRAW
        xBase += arrowIdx * padding + rescale * reactionDimensions.xOffsets[arrowIdx];
        yBase -= mainCompOffset + 2*margin;
        for (int i = 0; i < sideComps.size(); i++) {
            final int row = i / nSideCol;
            final int col = i % nSideCol;

            // calc the 'view' bounds:
            //  amount of padding depends on which row or column we are in.
            //  the width/height of this col/row can be determined by the next offset
            double x = xBase + col * padding + rescale * reactionDimensions.xOffsetSide[col];
            double y = yBase + row * padding + rescale * reactionDimensions.yOffsetSide[row];
            double w = rescale * (reactionDimensions.xOffsetSide[col + 1] - reactionDimensions.xOffsetSide[col]);
            double h = rescale * (reactionDimensions.yOffsetSide[row + 1] - reactionDimensions.yOffsetSide[row]);

            draw(visitor, zoom, sideComps.get(i), rect(x, y, w, h));
        }

        // CONDITIONS DRAW
        if (!reactionBounds.belowArrow.isEmpty()) {
            yBase += mainCompOffset;        // back to top
            yBase += (fitting * mainRequired.h) / 2;    // now on center line (arrow)
            yBase += arrowHeight;           // now just bellow
            draw(visitor, zoom,
                 reactionBounds.belowArrow.get(0),
                 rect(xBase,
                      yBase,
                      fitting * condRequired.w, fitting * condRequired.h));
        }

        // reset shared xOffsets
        if (!sideComps.isEmpty()) {
            for (int i = arrowIdx + 1; i < reactionDimensions.xOffsets.length; i++)
                reactionDimensions.xOffsets[i] -= sideRequired.w * 1 / (scale * zoom);
        }

        // we created the Graphic2d instance so need to dispose of it
        g2.dispose();
        return img;
    }

    @Override
    String toVecStr(String fmt, String units) {

        List<Bounds> mainComp  = reactionBounds.getMainComponents();
        List<Bounds> sideComps = reactionBounds.aboveArrow;

        // format margins and padding for raster images
        final double scale = model.get(BasicSceneGenerator.Scale.class);

        double margin = getMarginValue(units.equals(Depiction.UNITS_MM)
                                       ? DepictionGenerator.DEFAULT_MM_MARGIN
                                       : DepictionGenerator.DEFAULT_PX_MARGIN);
        double padding = getPaddingValue(DEFAULT_PADDING_FACTOR * margin);

        // All vector graphics will be written in mm not px to we need to
        // adjust the size of the molecules accordingly. For now the rescaling
        // is fixed to the bond length proposed by ACS 1996 guidelines (~5mm)
        double zoom = model.get(BasicSceneGenerator.ZoomFactor.class);

        if (units.equals(Depiction.UNITS_MM))
            zoom *= rescaleForBondLength(Depiction.ACS_1996_BOND_LENGTH_MM);

        // PDF and PS units are in Points (1/72 inch) in FreeHEP so need to adjust for that
        if (fmt.equals(PDF_FMT) || fmt.equals(PS_FMT)) {
            zoom    *= MM_TO_POINT;
            margin  *= MM_TO_POINT;
            padding *= MM_TO_POINT;
        }

        // work out the required space of the main and side components separately
        // will draw these in two passes (main then side) hence want different offsets for each
        final int nSideCol = reactionDimensions.xOffsetSide.length - 1;
        final int nSideRow = reactionDimensions.yOffsetSide.length - 1;

        Dimensions sideRequired = reactionDimensions.sideDim.scale(scale * zoom);
        Dimensions mainRequired = reactionDimensions.mainDim.scale(scale * zoom);
        Dimensions condRequired = reactionDimensions.condDim.scale(scale * zoom);

        Dimensions titleRequired = new Dimensions(reactionBounds.title.width(), reactionBounds.title.height()).scale(scale * zoom);

        final double firstRowHeight = scale * zoom * reactionDimensions.yOffsets[1];
        final Dimensions total = calcTotalDimensions(margin, padding, mainRequired, sideRequired, titleRequired, firstRowHeight, fmt);
        final double fitting = calcFitting(margin, padding, mainRequired, sideRequired, titleRequired, firstRowHeight, fmt);

        // create the image for rendering
        FreeHepWrapper wrapper = null;
        if (!fmt.equals(SVG_FMT))
            wrapper = new FreeHepWrapper(fmt, total.w, total.h);
        final IDrawVisitor visitor = fmt.equals(SVG_FMT) ? new SvgDrawVisitor(total.w, total.h, units)
                                                         : AWTDrawVisitor.forVectorGraphics(wrapper.g2);
        if (fmt.equals(SVG_FMT)) {
            svgPrevisit(fmt, scale * zoom * fitting, (SvgDrawVisitor) visitor, mainComp);
        } else {
            // pdf can handle fraction coords just fine
            ((AWTDrawVisitor) visitor).setRounding(false);
        }

        // background color
        visitor.setTransform(AffineTransform.getScaleInstance(1, -1));
        visitor.visit(new RectangleElement(0, -(int) Math.ceil(total.h), (int) Math.ceil(total.w), (int) Math.ceil(total.h),
                                           true, model.get(BasicSceneGenerator.BackgroundColor.class)));

        // compound the zoom, fitting and scaling into a single value
        final double rescale = zoom * fitting * scale;
        double mainCompOffset;

        // shift product x-offset to make room for the arrow / side components
        mainCompOffset = fitting * sideRequired.h + nSideRow * padding - fitting * firstRowHeight / 2;
        for (int i = arrowIdx + 1; i < reactionDimensions.xOffsets.length; i++) {
            reactionDimensions.xOffsets[i] += sideRequired.w * 1 / (scale * zoom);
        }

        int nCol = reactionDimensions.xOffsets.length - 1;
        int nRow = reactionDimensions.yOffsets.length - 1;

        // MAIN COMPONENTS DRAW
        // x,y base coordinates include the margin and centering (only if fitting to a size)
        final double totalRequiredWidth = 2 * margin + Math.max(0, nCol - 1) * padding + Math.max(0, nSideCol - 1) * padding + (rescale * reactionDimensions.xOffsets[nCol]);
        final double totalRequiredHeight = 2 * margin + Math.max(0, nRow - 1) * padding + (!reactionBounds.title.isEmpty() ? padding : 0) + Math.max(mainCompOffset, 0) + fitting * mainRequired.h + fitting * Math.max(0, titleRequired.h);
        double xBase = margin + (total.w - totalRequiredWidth) / 2;
        double yBase = margin + Math.max(mainCompOffset, 0) + (total.h - totalRequiredHeight) / 2;
        for (int i = 0; i < mainComp.size(); i++) {

            final int row = i / nCol;
            final int col = i % nCol;

            // calc the 'view' bounds:
            //  amount of padding depends on which row or column we are in.
            //  the width/height of this col/row can be determined by the next offset
            double x = xBase + col * padding + rescale * reactionDimensions.xOffsets[col];
            double y = yBase + row * padding + rescale * reactionDimensions.yOffsets[row];
            double w = rescale * (reactionDimensions.xOffsets[col + 1] - reactionDimensions.xOffsets[col]);
            double h = rescale * (reactionDimensions.yOffsets[row + 1] - reactionDimensions.yOffsets[row]);

            // intercept arrow draw and make it as big as need
            if (i == arrowIdx) {
                w = rescale * (reactionDimensions.xOffsets[i + 1] - reactionDimensions.xOffsets[i]) + Math.max(0, nSideCol - 1) * padding;
                draw(visitor,
                     1, // no zoom since arrows is drawn as big as needed
                     createArrow(direction, fgcol, w, arrowHeight * rescale),
                     rect(x, y, w, h));
                continue;
            }

            // extra padding from the side components
            if (i > arrowIdx)
                x += Math.max(0, nSideCol - 1) * padding;

            // skip empty elements
            final Bounds bounds = mainComp.get(i);
            if (bounds.isEmpty())
                continue;

            draw(visitor, zoom, bounds, rect(x, y, w, h));
        }

        // RXN TITLE DRAW
        if (!reactionBounds.title.isEmpty()) {
            double y = yBase + nRow * padding + rescale * reactionDimensions.yOffsets[nRow];
            double h = rescale * reactionBounds.title.height();
            draw(visitor, zoom, reactionBounds.title, rect(0, y, total.w, h));
        }

        // SIDE COMPONENTS DRAW
        xBase += arrowIdx * padding + rescale * reactionDimensions.xOffsets[arrowIdx];
        yBase -= mainCompOffset + 2*margin;
        for (int i = 0; i < sideComps.size(); i++) {
            final int row = i / nSideCol;
            final int col = i % nSideCol;

            // calc the 'view' bounds:
            //  amount of padding depends on which row or column we are in.
            //  the width/height of this col/row can be determined by the next offset
            double x = xBase + col * padding + rescale * reactionDimensions.xOffsetSide[col];
            double y = yBase + row * padding + rescale * reactionDimensions.yOffsetSide[row];
            double w = rescale * (reactionDimensions.xOffsetSide[col + 1] - reactionDimensions.xOffsetSide[col]);
            double h = rescale * (reactionDimensions.yOffsetSide[row + 1] - reactionDimensions.yOffsetSide[row]);

            draw(visitor, zoom, sideComps.get(i), rect(x, y, w, h));
        }

        // CONDITIONS DRAW
        if (!reactionBounds.belowArrow.isEmpty()) {
            yBase += mainCompOffset;         // back to top
            yBase += (fitting * mainRequired.h) / 2;     // now on center line (arrow)
            yBase += arrowHeight;            // now just bellow
            draw(visitor, zoom, reactionBounds.belowArrow.get(0),
                 rect(xBase,
                      yBase,
                      fitting * condRequired.w, fitting * condRequired.h));
        }

        // reset shared xOffsets
        if (!sideComps.isEmpty()) {
            for (int i = arrowIdx + 1; i < reactionDimensions.xOffsets.length; i++)
                reactionDimensions.xOffsets[i] -= sideRequired.w * 1 / (scale * zoom);
        }

        if (wrapper != null) {
            wrapper.dispose();
            return wrapper.toString();
        } else {
            return visitor.toString();
        }
    }

    private double calcFitting(double margin,
                               double padding,
                               Dimensions mainRequired,
                               Dimensions sideRequired,
                               Dimensions titleRequired,
                               double firstRowHeight,
                               String fmt) {
        if (dimensions == Dimensions.AUTOMATIC)
            return 1; // no fitting

        final int nSideCol = reactionDimensions.xOffsetSide.length - 1;
        final int nSideRow = reactionDimensions.yOffsetSide.length - 1;

        // need padding in calculation
        double mainCompOffset = sideRequired.h > 0 ? sideRequired.h + (nSideRow * padding) - (firstRowHeight / 2) : 0;
        if (mainCompOffset < 0)
            mainCompOffset = 0;

        Dimensions required = mainRequired.add(sideRequired.w, mainCompOffset)
                                          .add(0, Math.max(0, titleRequired.h));

        // We take out the padding height of the side components but in reality
        // some of it overlaps, since reactions are normally wider then they are
        // tall we won't normally bit fitting by this param. If do fit by this
        // param we might make the depiction smaller then it needs to be but thats
        // better than cutting bits off
        Dimensions targetDim = dimensions;

        int nCol = reactionDimensions.xOffsets.length - 1;
        int nRow = reactionDimensions.yOffsets.length - 1;

        targetDim = targetDim.add(-2 * margin, -2 * margin)
                             .add(-((nCol - 1) * padding), -((nRow - 1) * padding))
                             .add(-(nSideCol - 1) * padding, -(nSideRow - 1) * padding)
                             .add(0, titleRequired.h > 0 ? -padding : 0);

        // PDF and PS are in point to we need to account for that
        if (PDF_FMT.equals(fmt) || PS_FMT.equals(fmt))
            targetDim = targetDim.scale(MM_TO_POINT);

        double resize = Math.min(targetDim.w / required.w,
                                 targetDim.h / required.h);

        if (resize > 1 && !model.get(BasicSceneGenerator.FitToScreen.class))
            resize = 1;
        return resize;
    }

    private Dimensions calcTotalDimensions(double margin,
                                           double padding,
                                           Dimensions mainRequired,
                                           Dimensions sideRequired,
                                           Dimensions titleRequired,
                                           double firstRowHeight,
                                           String fmt) {
        if (dimensions == Dimensions.AUTOMATIC) {

            final int nSideCol = reactionDimensions.xOffsetSide.length - 1;
            final int nSideRow = reactionDimensions.yOffsetSide.length - 1;

            double mainCompOffset = sideRequired.h + 4*margin +
                                    (nSideRow * padding) - (firstRowHeight / 2);
            if (mainCompOffset < 0)
                mainCompOffset = 0;

            double titleExtra = Math.max(0, titleRequired.h);
            if (titleExtra > 0)
                titleExtra += padding;

            int nCol = reactionDimensions.xOffsets.length - 1;
            int nRow = reactionDimensions.yOffsets.length - 1;

            return mainRequired.add(2 * margin, 2 * margin)
                               .add(Math.max(0, nCol - 1) * padding, (nRow - 1) * padding)
                               .add(Math.max(0, sideRequired.w), 0)           // side component extra width
                               .add(Math.max(0, nSideCol - 1) * padding, 0) // side component padding
                               .add(0, mainCompOffset)
                               .add(0, titleExtra);

        } else {
            // we want all vector graphics dims in MM
            if (PDF_FMT.equals(fmt) || PS_FMT.equals(fmt))
                return dimensions.scale(MM_TO_POINT);
            else
                return dimensions;
        }
    }

    private Rectangle2D.Double rect(double x, double y, double w, double h) {
        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Create a reaction arrow.
     *
     * @param direction the reaction arrow typ
     * @param color the color of the arrow
     * @param minWidth min width
     * @param minHeight min height
     */
    static Bounds createArrow(IReaction.Direction direction,
                              Color color,
                              double minWidth, double minHeight) {
        Bounds arrow = new Bounds();
        Path2D path = new Path2D.Double();
        final double headThickness = minHeight / 3;
        final double inset         = 0.8;
        final double headLength    = minHeight;
        double strokeWidth = minHeight / 14;
        switch (direction) {
            case FORWARD:
                arrow.add(new LineElement(0, 0, minWidth - 0.5 * headLength, 0, strokeWidth, color));
                path.moveTo(minWidth, 0);
                path.lineTo(minWidth - headLength, +headThickness);
                path.lineTo(minWidth - inset * headLength, 0);
                path.lineTo(minWidth - headLength, -headThickness);
                path.closePath();
                arrow.add(GeneralPath.shapeOf(path, color));
                break;
            case BACKWARD:
                arrow.add(new LineElement(0.5 * headLength, 0, minWidth, 0, strokeWidth, color));
                path.moveTo(0, 0);
                path.lineTo(minHeight, +headThickness);
                path.lineTo(minHeight - (1 - inset) * minHeight, 0);
                path.lineTo(minHeight, -headThickness);
                path.closePath();
                arrow.add(GeneralPath.shapeOf(path, color));
                break;
            case BIDIRECTIONAL: // equilibrium?

                arrow.add(new LineElement(0, +0.5*headThickness, minWidth - 0.5 * headLength, +0.5*headThickness, strokeWidth, color));
                path.moveTo(minWidth, 0.5*headThickness-0.5*strokeWidth);
                path.lineTo(minWidth - headLength, 1.5*headThickness);
                path.lineTo(minWidth - inset * headLength, 0.5*headThickness-0.5*strokeWidth);
                path.closePath();

                arrow.add(new LineElement(0.5 * headLength, -0.5*headThickness, minWidth, -0.5*headThickness, strokeWidth, color));
                path.moveTo(0, -0.5*headThickness+0.5*strokeWidth);
                path.lineTo(+headLength, -1.5*headThickness);
                path.lineTo(inset * headLength, -0.5*headThickness+0.5*strokeWidth);
                path.closePath();
                arrow.add(GeneralPath.shapeOf(path, color));
                break;
            case NO_GO: // crossed arrow
                arrow.add(new LineElement(0, 0, minWidth - 0.5 * headLength, 0, strokeWidth, color));
                path.moveTo(minWidth, 0);
                path.lineTo(minWidth - headLength, +headThickness);
                path.lineTo(minWidth - inset * headLength, 0);
                path.lineTo(minWidth - headLength, -headThickness);
                path.closePath();
                arrow.add(GeneralPath.shapeOf(path, color));
                double cx = minWidth/2;
                arrow.add(new LineElement(cx-headThickness, -headThickness, cx+headThickness, +headThickness,
                                          strokeWidth, color));
                arrow.add(new LineElement(cx-headThickness, +headThickness, cx+headThickness, -headThickness,
                                          strokeWidth, color));
                break;
            case RETRO_SYNTHETIC: // open arrow
                arrow.add(new LineElement(0, -headThickness, minWidth - 0.5 * headLength, -headThickness, strokeWidth, color));
                arrow.add(new LineElement(0, +headThickness, minWidth - 0.5 * headLength, +headThickness, strokeWidth, color));
                path.moveTo(minWidth-headLength, -2*headThickness);
                path.lineTo(minWidth, 0);
                path.lineTo(minWidth-headLength, +2*headThickness);
                arrow.add(GeneralPath.outlineOf(path, strokeWidth, color));
                break;
            case RESONANCE:
                arrow.add(new LineElement(0.5 * headLength, 0, minWidth - 0.5 * headLength, 0, strokeWidth, color));
                path.moveTo(minWidth, 0);
                path.lineTo(minWidth - headLength, +headThickness);
                path.lineTo(minWidth - inset * headLength, 0);
                path.lineTo(minWidth - headLength, -headThickness);
                path.closePath();
                path.moveTo(0, 0);
                path.lineTo(minHeight, +headThickness);
                path.lineTo(minHeight - (1 - inset) * minHeight, 0);
                path.lineTo(minHeight, -headThickness);
                path.closePath();
                arrow.add(GeneralPath.shapeOf(path, color));
                break;
        }

        return arrow;
    }
}
