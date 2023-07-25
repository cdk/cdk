/*
 * Copyright (c) 2023 John Mayfield
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

import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


/**
 * Internal - Depiction of multiple reactions, these may be a pathway, synthesis or
 * just some reaction to be displayed together.
 *
 * @author john
 */
final class ReactionSetDepiction extends Depiction {

    private final RendererModel model;
    private Dimensions dimensions;

    // molecule sets and titles
    private final List<ReactionBounds> reactionSetBounds;
    private final Color fgcol;

    ReactionSetDepiction(RendererModel model,
                         List<ReactionBounds> reactionSetBounds,
                         Dimensions dimensions,
                         Color fgcol) {
        super(model);
        this.model = model;
        this.dimensions = dimensions;
        this.fgcol = fgcol;
        this.reactionSetBounds = reactionSetBounds;
    }

    private Dimensions getRequiredSize(List<ReactionDimensions> dimensions,
                                       String fmt,
                                       double spacing) {
        if (dimensions.isEmpty())
            throw new IllegalArgumentException();
        Dimensions total = dimensions.get(0).calcTotalDimensions(Dimensions.AUTOMATIC, fmt);
        double padding = dimensions.get(0).padding;
        for (int i = 1; i < dimensions.size(); i++) {
            Dimensions part = dimensions.get(i).calcTotalDimensions(Dimensions.AUTOMATIC, fmt);
            total = total.add(Math.max(part.w - total.w, 0),
                              spacing + part.h);
        }
        return total;
    }

    private double calcFitting(Dimensions srcDim,
                               Dimensions dstDim,
                               double margin) {
        if (dstDim == Dimensions.AUTOMATIC)
            return 1; // no fitting
        dstDim = dstDim.add(2*-margin, 2*-margin);
        double resize = Math.min(dstDim.w / srcDim.w,
                                 dstDim.h / srcDim.h);
        if (resize > 1 && !model.get(BasicSceneGenerator.FitToScreen.class))
            resize = 1;
        return resize;
    }

    @Override
    public BufferedImage toImg() {
        // format margins and padding for raster images
        final double scale = model.get(BasicSceneGenerator.Scale.class);
        final double zoom = model.get(BasicSceneGenerator.ZoomFactor.class);
        final double margin = getMarginValue(DepictionGenerator.DEFAULT_PX_MARGIN);
        final double padding = getPaddingValue(DEFAULT_PADDING_FACTOR * margin) / (zoom * scale);

        // the spacing between each reaction
        double spacing = padding * zoom * scale;

        for (ReactionBounds bounds : reactionSetBounds)
            bounds.model.set(BasicSceneGenerator.ZoomFactor.class, zoom);
        model.set(BasicSceneGenerator.ZoomFactor.class, zoom);

        List<ReactionDimensions> reactionSetDimensions = new ArrayList<>();
        for (ReactionBounds bounds : reactionSetBounds)
            reactionSetDimensions.add(bounds.getDimensions(padding).scale(zoom * scale));

        Dimensions total           = getRequiredSize(reactionSetDimensions, null, spacing);
        Dimensions totalWithMargin = total.add(2 * margin, 2 * margin);
        double fitting = calcFitting(total, dimensions, margin);

        if (Math.abs(1.0 - fitting) >= 0.01) {
            spacing *= fitting;
            total = total.scale(fitting);
            totalWithMargin = total.add(2*margin, 2*margin);
            reactionSetDimensions.replaceAll(reactionDimensions -> reactionDimensions.scale(fitting));
        }

        Dimensions canvasSize = totalWithMargin;
        if (dimensions != Dimensions.AUTOMATIC)
            canvasSize = dimensions;

        // we use the AWT for vector graphics if though we're raster because
        // fractional strokes can be figured out by interpolation, without
        // when we shrink diagrams bonds can look too bold/chubby
        // create the image for rendering
        final BufferedImage img = new BufferedImage((int) Math.ceil(canvasSize.w), (int) Math.ceil(canvasSize.h),
                                                    BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics2D g2 = img.createGraphics();
        final IDrawVisitor visitor = AWTDrawVisitor.forVectorGraphics(g2);
        visitor.setTransform(AffineTransform.getScaleInstance(1, -1));
        visitor.visit(new RectangleElement(0,
                                           -(int) Math.ceil(canvasSize.h),
                                           (int) Math.ceil(canvasSize.w),
                                           (int) Math.ceil(canvasSize.h),
                                           true,
                                           model.get(BasicSceneGenerator.BackgroundColor.class)));

        double xOffset = margin;
        double yOffset = margin;

        // centering
        if (dimensions != Dimensions.AUTOMATIC) {
            if (dimensions.w > totalWithMargin.w)
                xOffset += (dimensions.w - totalWithMargin.w) / 2;
            if (dimensions.h > totalWithMargin.h)
                yOffset += (dimensions.h - totalWithMargin.h) / 2;
        }

// for debugging the margins
//        visitor.visit(new RectangleElement(xOffset,
//                                           -total.h - yOffset,
//                                           total.w,
//                                           total.h,
//                                           true,
//                                           new Color(0xF1F1C8)));

        for (int i = 0; i < reactionSetBounds.size(); i++) {
            ReactionBounds reactionBounds = reactionSetBounds.get(i);
            ReactionDimensions reactionDimensions = reactionSetDimensions.get(i);
            Dimensions partDims = reactionDimensions.calcTotalDimensions(Dimensions.AUTOMATIC, null);

            ReactionDepiction reactionDepiction = new ReactionDepiction(reactionBounds, null, fgcol);
            reactionDepiction.draw(visitor,
                                   reactionDimensions,
                                   ReactionDepiction.rect(xOffset, yOffset, total.w, partDims.h),
                                   null);
            yOffset += spacing + partDims.h;
        }

        // we created the Graphic2d instance so need to dispose of it
        g2.dispose();
        return img;
    }

    @Override
    String toVecStr(String fmt, String units) {

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

        // px <=> mm/pt
        if (units.equals(Depiction.UNITS_MM)) {
            double rescale = rescaleForBondLength(Depiction.ACS_1996_BOND_LENGTH_MM);
            zoom *= rescale;
        }

        double padding = getPaddingValue(DEFAULT_PADDING_FACTOR * margin) / (zoom * scale);

        // PDF and PS units are in Points (1/72 inch) in FreeHEP so need to adjust for that
        if (fmt.equals(PDF_FMT) || fmt.equals(PS_FMT)) {
            zoom *= MM_TO_POINT;
            margin *= MM_TO_POINT;
            padding *= MM_TO_POINT;
            dimensions = dimensions.scale(MM_TO_POINT);
        }


        // the spacing between each reaction
        double spacing = padding * zoom * scale;

        for (ReactionBounds bounds : reactionSetBounds)
            bounds.model.set(BasicSceneGenerator.ZoomFactor.class, zoom);
        model.set(BasicSceneGenerator.ZoomFactor.class, zoom);

        List<ReactionDimensions> reactionSetDimensions = new ArrayList<>();
        for (ReactionBounds bounds : reactionSetBounds)
            reactionSetDimensions.add(bounds.getDimensions(padding).scale(zoom * scale));

        Dimensions total           = getRequiredSize(reactionSetDimensions, fmt, spacing);
        Dimensions totalWithMargin = total.add(2 * margin, 2 * margin);
        double fitting = calcFitting(total, dimensions, margin);

        if (Math.abs(1.0 - fitting) >= 0.01) {
            spacing *= fitting;
            total = total.scale(fitting);
            totalWithMargin = total.add(2*margin, 2*margin);
            reactionSetDimensions.replaceAll(reactionDimensions -> reactionDimensions.scale(fitting));
        }

        Dimensions canvasSize = totalWithMargin;
        if (dimensions != Dimensions.AUTOMATIC)
            canvasSize = dimensions;

        // create the image for rendering
        FreeHepWrapper wrapper = null;
        if (!fmt.equals(SVG_FMT))
            wrapper = new FreeHepWrapper(fmt, canvasSize.w, canvasSize.h);
        final IDrawVisitor visitor = fmt.equals(SVG_FMT)
                ? new SvgDrawVisitor(canvasSize.w, canvasSize.h, units)
                : AWTDrawVisitor.forVectorGraphics(wrapper.g2);
        if (fmt.equals(SVG_FMT)) {
            svgStyleCache(fmt, scale, zoom, fitting, (SvgDrawVisitor) visitor);
        } else {
            // pdf can handle fraction coords just fine
            ((AWTDrawVisitor) visitor).setRounding(false);
        }

        visitor.setTransform(AffineTransform.getScaleInstance(1, -1));
        visitor.visit(new RectangleElement(0,
                                           -(int) Math.ceil(canvasSize.h),
                                           (int) Math.ceil(canvasSize.w),
                                           (int) Math.ceil(canvasSize.h),
                                           true,
                                           model.get(BasicSceneGenerator.BackgroundColor.class)));

        double xOffset = margin;
        double yOffset = margin;

        // centering
        if (dimensions != Dimensions.AUTOMATIC) {
            if (dimensions.w > totalWithMargin.w)
                xOffset += (dimensions.w - totalWithMargin.w) / 2;
            if (dimensions.h > totalWithMargin.h)
                yOffset += (dimensions.h - totalWithMargin.h) / 2;
        }

// for debugging the margins
//        visitor.visit(new RectangleElement(xOffset,
//                                           -total.h - yOffset,
//                                           total.w,
//                                           total.h,
//                                           true,
//                                           new Color(0xF1F1C8)));

        for (int i = 0; i < reactionSetBounds.size(); i++) {
            ReactionBounds reactionBounds = reactionSetBounds.get(i);
            ReactionDimensions reactionDimensions = reactionSetDimensions.get(i);

            Dimensions partDims = reactionDimensions.calcTotalDimensions(Dimensions.AUTOMATIC, null);

            ReactionDepiction reactionDepiction = new ReactionDepiction(reactionBounds, null, fgcol);
            reactionDepiction.draw(visitor,
                                   reactionDimensions,
                                   ReactionDepiction.rect(xOffset, yOffset, total.w, partDims.h),
                                   fmt);
            yOffset += spacing + partDims.h;
        }

        // reset the modified zoom we stored
        for (ReactionBounds bounds : reactionSetBounds)
            bounds.model.set(BasicSceneGenerator.ZoomFactor.class, zoomBackup);
        model.set(BasicSceneGenerator.ZoomFactor.class, zoomBackup);

        if (wrapper != null) {
            wrapper.dispose();
            return wrapper.toString();
        } else {
            return visitor.toString();
        }
    }

    private void svgStyleCache(String fmt, double scale, double zoom, double fitting, SvgDrawVisitor visitor) {
        for (ReactionBounds rbounds : reactionSetBounds)
            svgPrevisit(fmt, zoom * scale * fitting, visitor, rbounds.getMainRow());
    }
}
