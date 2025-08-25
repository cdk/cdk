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
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Internal - depiction of a single reaction. We divide the reaction into two draw steps.
 * The first step draws the main components (reactants and products) whilst the second
 * draws the side components (agents: catalysts, solvents, spectators, etc). Reaction
 * direction is drawn a single headed arrow (forward and backward) or an equilibrium
 * (bidirectional).
 */
final class ReactionDepiction extends Depiction {
    private Dimensions dimensions;
    private final ReactionBounds reactionBounds;
    private final Color fgcol;

    ReactionDepiction(ReactionBounds reactionBounds,
                      Dimensions dimensions,
                      Color fgcol) {
        super(reactionBounds.model);
        this.dimensions = dimensions;
        this.fgcol = fgcol;
        this.reactionBounds = reactionBounds;
    }

    void draw(IDrawVisitor visitor,
              ReactionDimensions required,
              Rectangle2D viewBounds,
              String fmt) {

        final double scale = model.get(BasicSceneGenerator.Scale.class);
        final double zoom = model.get(BasicSceneGenerator.ZoomFactor.class);
        final double padding = required.padding;

        List<Bounds> mainComp = reactionBounds.getMainComponents();
        List<Bounds> sideComps = reactionBounds.aboveArrow;
        int arrowIdx = reactionBounds.getArrowIndex();
        double arrowHeight = reactionBounds.plus.height() * required.scale;

        // work out the required space of the main and side components separately
        // will draw these in two passes (main then side) hence want different offsets for each
        final int nSideCol = required.xOffsetSide.length - 1;
        final int nSideRow = required.yOffsetSide.length - 1;

        // compound the zoom, fitting and scaling into a single value
        double mainCompOffset;

        // shift product x-offset to make room for the arrow / side components
        mainCompOffset = (required.sideDim.h + (nSideRow * padding)) - (required.mainRowHeight() / 2);

        int nCol = required.xOffsets.length - 1;
        int nRow = required.yOffsets.length - 1;

        // MAIN COMPONENTS DRAW
        // x,y base coordinates include the margin and centering (only if fitting to a size)
        final double totalRequiredWidth = Math.max(0, nCol - 1) * padding + required.xOffsets[nCol];
        final double totalRequiredHeight = Math.max(0, nRow - 1) * padding + (!reactionBounds.title.isEmpty() ? padding : 0) + Math.max(mainCompOffset, 0) + required.mainDim.h + Math.max(0, required.titleDim.h);
        double xBase = viewBounds.getX() + (viewBounds.getWidth() - totalRequiredWidth) / 2;
        double yBase = viewBounds.getY() + Math.max(mainCompOffset, 0) + ((viewBounds.getHeight() - totalRequiredHeight) / 2);
        for (int i = 0; i < mainComp.size(); i++) {

            final int row = i / nCol;
            final int col = i % nCol;

            // calc the 'view' bounds:
            //  amount of padding depends on which row or column we are in.
            //  the width/height of this col/row can be determined by the next offset
            double x = xBase + col * padding + required.xOffsets[col];
            double y = yBase + row * padding + required.yOffsets[row];
            double w = (required.xOffsets[col + 1] - required.xOffsets[col]);
            double h = (required.yOffsets[row + 1] - required.yOffsets[row]);

            // intercept arrow draw and make it as big as need
            if (i == arrowIdx && reactionBounds.direction != null) {
                w = (required.xOffsets[i + 1] - required.xOffsets[i]);
                draw(visitor,
                     1, // no zoom since arrows is drawn as big as needed
                     createArrow(reactionBounds.direction, fgcol, w, arrowHeight),
                     rect(x, y, w, h));
                continue;
            }

            // skip empty elements
            final Bounds bounds = mainComp.get(i);
            if (bounds.isEmpty())
                continue;

            draw(visitor, zoom, bounds, rect(x, y, w, h));
        }

        // RXN TITLE DRAW
        if (!reactionBounds.title.isEmpty()) {
            double y = yBase + nRow * padding + required.yOffsets[nRow];
            double h = required.scale * reactionBounds.title.height();
            draw(visitor, zoom, reactionBounds.title, rect(0, y, viewBounds.getWidth(), h));
        }

        // SIDE COMPONENTS DRAW
        xBase += arrowIdx * padding + required.xOffsets[arrowIdx];
        yBase -= mainCompOffset;
        for (int i = 0; i < sideComps.size(); i++) {
            final int row = i / nSideCol;
            final int col = i % nSideCol;

            // calc the 'view' bounds:
            //  amount of padding depends on which row or column we are in.
            //  the width/height of this col/row can be determined by the next offset
            double x = xBase + (col+1) * padding + required.xOffsetSide[col];
            double y = yBase + row * padding + required.yOffsetSide[row];
            double w = (required.xOffsetSide[col + 1] - required.xOffsetSide[col]);
            double h = (required.yOffsetSide[row + 1] - required.yOffsetSide[row]);

            draw(visitor, zoom, sideComps.get(i), rect(x, y, w, h));
        }

        // CONDITIONS DRAW
        if (!reactionBounds.belowArrow.isEmpty()) {
            yBase += mainCompOffset; // back to center
            yBase += 2*padding + arrowHeight;  // now just bellow arrow
            draw(visitor, zoom,
                 reactionBounds.belowArrow.get(0),
                 rect(xBase,
                      yBase,
                      required.condDim.w,
                      required.condDim.h));
        }
    }

    @Override
    public BufferedImage toImg() {

        // format margins and padding for raster images
        final double scale = model.get(BasicSceneGenerator.Scale.class);
        final double zoom = model.get(BasicSceneGenerator.ZoomFactor.class);
        final double margin = getMarginValue(DepictionGenerator.DEFAULT_PX_MARGIN);
        final double padding = getPaddingValue(DEFAULT_PADDING_FACTOR * margin) / (scale * zoom);

        ReactionDimensions reactionDimensions = reactionBounds.getDimensions(padding);

        ReactionDimensions required = reactionBounds.getDimensions(padding).resize(scale * zoom);
        Dimensions total = required.calcTotalDimensions(null);
        Dimensions totalWithMargin = total.add(2 * margin, 2 * margin);
        double fitting = calcFitting(total, dimensions, margin);

        if (Math.abs(1.0 - fitting) >= 0.01) {
            total = total.scale(fitting);
            totalWithMargin = total.add(2*margin, 2*margin);
            required = required.resize(fitting);
        }

        Dimensions canvasSize = totalWithMargin;
        if (dimensions != Dimensions.AUTOMATIC)
            canvasSize = dimensions;

        // create the image for rendering
        final BufferedImage img = new BufferedImage((int) Math.ceil(canvasSize.w), (int) Math.ceil(canvasSize.h),
                                                    BufferedImage.TYPE_4BYTE_ABGR);

        // we use the AWT for vector graphics if though we're raster because
        // fractional strokes can be figured out by interpolation, without
        // when we shrink diagrams bonds can look too bold/chubby
        final Graphics2D g2 = img.createGraphics();
        final IDrawVisitor visitor = AWTDrawVisitor.forVectorGraphics(g2);
        visitor.setTransform(AffineTransform.getScaleInstance(1, -1));
        visitor.visit(new RectangleElement(0, -(int) Math.ceil(canvasSize.h), (int) Math.ceil(canvasSize.w), (int) Math.ceil(canvasSize.h),
                                           true, model.get(BasicSceneGenerator.BackgroundColor.class)));

        double xOffset = margin;
        double yOffset = margin;
        // centering
        if (dimensions != Dimensions.AUTOMATIC) {
            if (dimensions.w > totalWithMargin.w)
                xOffset += (dimensions.w - totalWithMargin.w) / 2;
            if (dimensions.h > totalWithMargin.h)
                yOffset += (dimensions.h - totalWithMargin.h) / 2;
        }
        draw(visitor, required, rect(xOffset, yOffset, total.w, total.h), null);

        // we created the Graphic2d instance so need to dispose of it
        g2.dispose();
        return img;
    }

    @Override
    byte[] toVecBytes(String fmt, String units) {

        // format margins and padding for raster images
        final double scale = model.get(BasicSceneGenerator.Scale.class);

        double margin = getMarginValue(units.equals(Depiction.UNITS_MM)
                                               ? DepictionGenerator.DEFAULT_MM_MARGIN
                                               : DepictionGenerator.DEFAULT_PX_MARGIN);
        // All vector graphics will be written in mm not px to we need to
        // adjust the size of the molecules accordingly. For now the rescaling
        // is fixed to the bond length proposed by ACS 1996 guidelines (~5mm)
        double zoom = model.get(BasicSceneGenerator.ZoomFactor.class);
        double zoomBackup = zoom;

        if (units.equals(Depiction.UNITS_MM))
            zoom *= rescaleForBondLength(Depiction.ACS_1996_BOND_LENGTH_MM);

        double padding = getPaddingValue(DEFAULT_PADDING_FACTOR * margin) / (zoom * scale);

        // PDF and PS units are in Points (1/72 inch) in FreeHEP so need to adjust for that
        if (fmt.equals(PDF_FMT) || fmt.equals(PS_FMT)) {
            zoom *= MM_TO_POINT;
            margin *= MM_TO_POINT;
            padding *= MM_TO_POINT;
            dimensions = dimensions.scale(MM_TO_POINT);
        }

        model.set(BasicSceneGenerator.ZoomFactor.class, zoom);

        ReactionDimensions required = reactionBounds.getDimensions(padding).resize(scale * zoom);
        Dimensions total = required.calcTotalDimensions(fmt);
        Dimensions totalWithMargin = total.add(2 * margin, 2 * margin);
        double fitting = calcFitting(total, dimensions, margin);

        if (Math.abs(1.0 - fitting) >= 0.01) {
            total = total.scale(fitting);
            totalWithMargin = total.add(2*margin, 2*margin);
            required = required.resize(fitting);
        }

        Dimensions canvasSize = totalWithMargin;
        if (dimensions != Dimensions.AUTOMATIC)
            canvasSize = dimensions;

        // create the image for rendering
        FreeHepWrapper wrapper = null;
        final IDrawVisitor visitor;
        if (fmt.equals(SVG_FMT)) {
            visitor = new SvgDrawVisitor(canvasSize.w, canvasSize.h, units);
            svgStyleCache(fmt, scale * zoom * fitting,
                          (SvgDrawVisitor) visitor,
                          reactionBounds.getMainComponents());
        } else {
            wrapper = new FreeHepWrapper(fmt, canvasSize.w, canvasSize.h);
            visitor = AWTDrawVisitor.forVectorGraphics(wrapper.g2);
            ((AWTDrawVisitor) visitor).setRounding(false);
        }

        // background color
        visitor.setTransform(AffineTransform.getScaleInstance(1, -1));
        visitor.visit(new RectangleElement(0,
                                           -(int) Math.ceil(totalWithMargin.h),
                                           (int) Math.ceil(totalWithMargin.w),
                                           (int) Math.ceil(totalWithMargin.h),
                                           true, model.get(BasicSceneGenerator.BackgroundColor.class)));

//        // debug margins
//        visitor.visit(new RectangleElement(margin,
//                                           -total.h - margin,
//                                           total.w,
//                                           total.h,
//                                           true,
//                                           new Color(0xF1F1C8)));

        double xOffset = margin;
        double yOffset = margin;
        // centering
        if (dimensions != Dimensions.AUTOMATIC) {
            if (dimensions.w > totalWithMargin.w)
                xOffset += (dimensions.w - totalWithMargin.w) / 2;
            if (dimensions.h > totalWithMargin.h)
                yOffset += (dimensions.h - totalWithMargin.h) / 2;
        }
        draw(visitor, required, rect(xOffset, yOffset, total.w, total.h), fmt);

        // reset the modified zoom we stored
        model.set(BasicSceneGenerator.ZoomFactor.class, zoomBackup);

        if (wrapper != null) {
            wrapper.dispose();
            return wrapper.getBytes();
        } else {
            return visitor.toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    private double calcFitting(ReactionDimensions srcDim,
                               Dimensions dstDim,
                               String fmt) {
        if (dstDim == Dimensions.AUTOMATIC)
            return 1; // no fitting

        final double zoom = model.get(BasicSceneGenerator.ZoomFactor.class);
        final double padding = srcDim.padding;
        final double firstRowHeight = srcDim.yOffsets[1];

        final int nSideCol = srcDim.xOffsetSide.length - 1;
        final int nSideRow = srcDim.yOffsetSide.length - 1;

        // need padding in calculation
        double mainCompOffset = srcDim.sideDim.h > 0 ? srcDim.sideDim.h + (nSideRow * padding) - (firstRowHeight / 2) : 0;
        if (mainCompOffset < 0)
            mainCompOffset = 0;

        Dimensions required = srcDim.mainDim.add(srcDim.sideDim.w, mainCompOffset)
                                            .add(0, Math.max(0, srcDim.titleDim.h));

        // We take out the padding height of the side components but in reality
        // some of it overlaps, since reactions are normally wider then they are
        // tall we won't normally bit fitting by this param. If do fit by this
        // param we might make the depiction smaller then it needs to be but thats
        // better than cutting bits off
        Dimensions targetDim = dstDim;

        int nCol = srcDim.xOffsets.length - 1;
        int nRow = srcDim.yOffsets.length - 1;

        targetDim = targetDim.add(-((nCol - 1) * padding), -((nRow - 1) * padding))
                             .add(0, srcDim.titleDim.h > 0 ? -padding : 0);

        // PDF and PS are in point to we need to account for that
        if (PDF_FMT.equals(fmt) || PS_FMT.equals(fmt))
            targetDim = targetDim.scale(MM_TO_POINT);

        double resize = Math.min(targetDim.w / required.w,
                                 targetDim.h / required.h);

        if (resize > 1 && !model.get(BasicSceneGenerator.FitToScreen.class))
            resize = 1;
        return resize;
    }



    static Rectangle2D.Double rect(double x, double y, double w, double h) {
        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Create a reaction arrow.
     *
     * @param direction the reaction arrow typ
     * @param color     the color of the arrow
     * @param length  min width
     * @param minHeight min height
     */
    static Bounds createArrow(IReaction.Direction direction,
                              Color color,
                              double length,
                              double minHeight) {
        if (direction == null)
            return new Bounds();
        Path2D path = new Path2D.Double();
        final double headThickness = minHeight / 3;
        final double inset = 0.8;
        final double headLength = minHeight;
        double strokeWidth = minHeight / 14;
        Bounds arrow = new Bounds(0, -headThickness, length, +headThickness);
        switch (direction) {
            case FORWARD:
                arrow.add(new LineElement(0, 0, length - 0.5 * headLength, 0, strokeWidth, color));
                path.moveTo(length, 0);
                path.lineTo(length - headLength, +headThickness);
                path.lineTo(length - inset * headLength, 0);
                path.lineTo(length - headLength, -headThickness);
                path.closePath();
                arrow.add(GeneralPath.shapeOf(path, color));
                break;
            case BACKWARD:
                arrow.add(new LineElement(0.5 * headLength, 0, length, 0, strokeWidth, color));
                path.moveTo(0, 0);
                path.lineTo(minHeight, +headThickness);
                path.lineTo(minHeight - (1 - inset) * minHeight, 0);
                path.lineTo(minHeight, -headThickness);
                path.closePath();
                arrow.add(GeneralPath.shapeOf(path, color));
                break;
            case UNDIRECTED:
                double x1 = headThickness;
                double x2 = length-2*headThickness;
                double y = 0.5 * headThickness;
                arrow.add(new LineElement(x1, -(y), x2, -(y), strokeWidth, color));
                arrow.add(new LineElement(x1, +(y), x2, +(y), strokeWidth, color));
                break;
            case BIDIRECTIONAL: // equilibrium?
                arrow.add(new LineElement(0, +0.5 * headThickness, length - 0.5 * headLength, +0.5 * headThickness, strokeWidth, color));
                path.moveTo(length, 0.5 * headThickness - 0.5 * strokeWidth);
                path.lineTo(length - headLength, 1.5 * headThickness);
                path.lineTo(length - inset * headLength, 0.5 * headThickness - 0.5 * strokeWidth);
                path.closePath();

                arrow.add(new LineElement(0.5 * headLength, -0.5 * headThickness, length, -0.5 * headThickness, strokeWidth, color));
                path.moveTo(0, -0.5 * headThickness + 0.5 * strokeWidth);
                path.lineTo(+headLength, -1.5 * headThickness);
                path.lineTo(inset * headLength, -0.5 * headThickness + 0.5 * strokeWidth);
                path.closePath();
                arrow.add(GeneralPath.shapeOf(path, color));
                break;
            case NO_GO: // crossed arrow
                arrow.add(new LineElement(0, 0, length - 0.5 * headLength, 0, strokeWidth, color));
                path.moveTo(length, 0);
                path.lineTo(length - headLength, +headThickness);
                path.lineTo(length - inset * headLength, 0);
                path.lineTo(length - headLength, -headThickness);
                path.closePath();
                arrow.add(GeneralPath.shapeOf(path, color));
                double cx = length / 2;
                arrow.add(new LineElement(cx - headThickness, -headThickness, cx + headThickness, +headThickness,
                                          strokeWidth, color));
                arrow.add(new LineElement(cx - headThickness, +headThickness, cx + headThickness, -headThickness,
                                          strokeWidth, color));
                break;
            case RETRO_SYNTHETIC: // open arrow
                arrow.add(new LineElement(0, -headThickness, length - 0.5 * headLength, -headThickness, strokeWidth, color));
                arrow.add(new LineElement(0, +headThickness, length - 0.5 * headLength, +headThickness, strokeWidth, color));
                path.moveTo(length - headLength, -2 * headThickness);
                path.lineTo(length, 0);
                path.lineTo(length - headLength, +2 * headThickness);
                arrow.add(GeneralPath.outlineOf(path, strokeWidth, color));
                break;
            case RESONANCE:
                arrow.add(new LineElement(0.5 * headLength, 0, length - 0.5 * headLength, 0, strokeWidth, color));
                path.moveTo(length, 0);
                path.lineTo(length - headLength, +headThickness);
                path.lineTo(length - inset * headLength, 0);
                path.lineTo(length - headLength, -headThickness);
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
