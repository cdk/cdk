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

import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal - depicts a set of molecules aligned in a grid. This class
 * also handles the degenerate case of a single molecule as a 1x1 grid.
 */
final class MolGridDepiction extends Depiction {

    private final RendererModel model;
    private final Dimensions    dimensions;
    private final int           nCol, nRow;
    private final List<Bounds>  elements;

    public MolGridDepiction(RendererModel model,
                            List<Bounds> molecules,
                            List<Bounds> titles,
                            Dimensions dimensions,
                            int nRow, int nCol) {
        super(model);
        this.model = model;
        this.dimensions = dimensions;

        this.elements = new ArrayList<>();

        // degenerate case is when no title are provided
        if (titles.isEmpty()) {
            elements.addAll(molecules);
        } else {
            assert molecules.size() == titles.size();
            // interweave molecules and titles
            for (int r = 0; r < nRow; r++) {
                final int fromIndex = r * nCol;
                final int toIndex = Math.min(molecules.size(), (r + 1) * nCol);
                if (fromIndex >= toIndex)
                    break;

                final List<Bounds> molsublist = molecules.subList(fromIndex, toIndex);
                // need to pad list
                while (molsublist.size() < nCol)
                    molsublist.add(new Bounds());

                elements.addAll(molsublist);
                elements.addAll(titles.subList(fromIndex, toIndex));
            }
            nRow *= 2;
        }

        this.nCol = nCol;
        this.nRow = nRow;
    }

    @Override
    public BufferedImage toImg() {

        // format margins and padding for raster images
        final double margin  = getMarginValue(DepictionGenerator.DEFAULT_PX_MARGIN);
        final double padding = getPaddingValue(DEFAULT_PADDING_FACTOR * margin);
        final double scale   = model.get(BasicSceneGenerator.Scale.class);
        final double zoom    = model.get(BasicSceneGenerator.ZoomFactor.class);

        // row and col offsets for alignment
        double[] yOffset = new double[nRow + 1];
        double[] xOffset = new double[nCol + 1];

        Dimensions required = Dimensions.ofGrid(elements, yOffset, xOffset)
                                        .scale(scale * zoom);

        final Dimensions total = calcTotalDimensions(margin, padding, required, null);
        final double fitting = calcFitting(margin, padding, required, null);

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

        // x,y base coordinates include the margin and centering (only if fitting to a size)
        final double xBase = margin + (total.w - 2*margin - (nCol-1)*padding - (rescale * xOffset[nCol])) / 2;
        final double yBase = margin + (total.h - 2*margin - (nRow-1)*padding - (rescale * yOffset[nRow])) / 2;

        for (int i = 0; i < elements.size(); i++) {
            final int row = i / nCol;
            final int col = i % nCol;

            // skip empty elements
            final Bounds bounds = this.elements.get(i);
            if (bounds.isEmpty())
                continue;

            // calc the 'view' bounds:
            //  amount of padding depends on which row or column we are in.
            //  the width/height of this col/row can be determined by the next offset
            double x = xBase + col * padding + rescale * xOffset[col];
            double y = yBase + row * padding + rescale * yOffset[row];
            double w = rescale * (xOffset[col+1] - xOffset[col]);
            double h = rescale * (yOffset[row+1] - yOffset[row]);

            draw(visitor, zoom, bounds, rect(x, y, w, h));
        }

        // we created the Graphic2d instance so need to dispose of it
        g2.dispose();
        return img;
    }

    private double calcFitting(double margin, double padding, Dimensions required, String fmt) {
        if (dimensions == Dimensions.AUTOMATIC)
            return 1; // no fitting
        Dimensions targetDim = dimensions;

        // PDF and PS are in point to we need to account for that
        if (PDF_FMT.equals(fmt) || PS_FMT.equals(fmt) || EPS_FMT.equals(fmt))
            targetDim = targetDim.scale(MM_TO_POINT);

        targetDim = targetDim.add(-2 * margin, -2 * margin)
                                         .add(-((nCol - 1) * padding), -((nRow - 1) * padding));
        double resize = Math.min(targetDim.w / required.w,
                                 targetDim.h / required.h);
        if (resize > 1 && !model.get(BasicSceneGenerator.FitToScreen.class))
            resize = 1;
        return resize;
    }

    private Dimensions calcTotalDimensions(double margin, double padding, Dimensions required, String fmt) {
        if (dimensions == Dimensions.AUTOMATIC) {
            return required.add(2 * margin, 2 * margin)
                           .add((nCol - 1) * padding, (nRow - 1) * padding);
        } else {
            // we want all vector graphics dims in MM
            if (PDF_FMT.equals(fmt) || PS_FMT.equals(fmt) || EPS_FMT.equals(fmt))
                return dimensions.scale(MM_TO_POINT);
            else
                return dimensions;
        }
    }

    @Override
    String toVecStr(String fmt, String units) {

        // format margins and padding for raster images
        double margin  = getMarginValue(units.equals(Depiction.UNITS_MM) ? DepictionGenerator.DEFAULT_MM_MARGIN
                                                                         : DepictionGenerator.DEFAULT_PX_MARGIN);
        double padding = getPaddingValue(DEFAULT_PADDING_FACTOR * margin);
        final double scale   = model.get(BasicSceneGenerator.Scale.class);

        double zoom = model.get(BasicSceneGenerator.ZoomFactor.class);

        // All vector graphics will be written in mm not px to we need to
        // adjust the size of the molecules accordingly. For now the rescaling
        // is fixed to the bond length proposed by ACS 1996 guidelines (~5mm)
        if (units.equals(Depiction.UNITS_MM))
            zoom *= rescaleForBondLength(Depiction.ACS_1996_BOND_LENGTH_MM);

        // PDF and PS units are in Points (1/72 inch) in FreeHEP so need to adjust for that
        if (fmt.equals(PDF_FMT) || fmt.equals(PS_FMT) || fmt.equals(EPS_FMT)) {
            zoom    *= MM_TO_POINT;
            margin  *= MM_TO_POINT;
            padding *= MM_TO_POINT;
        }

        // row and col offsets for alignment
        double[] yOffset = new double[nRow+1];
        double[] xOffset = new double[nCol+1];

        Dimensions required    = Dimensions.ofGrid(elements, yOffset, xOffset)
                                           .scale(zoom * scale);

        final Dimensions total = calcTotalDimensions(margin, padding, required, fmt);
        final double fitting   = calcFitting(margin, padding, required, fmt);

        // create the image for rendering
        FreeHepWrapper wrapper = null;
        if (!fmt.equals(SVG_FMT))
            wrapper = new FreeHepWrapper(fmt, total.w, total.h);
        final IDrawVisitor visitor = fmt.equals(SVG_FMT) ? new SvgDrawVisitor(total.w, total.h, units)
                                                         : AWTDrawVisitor.forVectorGraphics(wrapper.g2);

        if (fmt.equals(SVG_FMT)) {
            svgPrevisit(fmt, scale * zoom * fitting, (SvgDrawVisitor) visitor, elements);
        } else {
            // pdf can handle fraction coords just fine
            ((AWTDrawVisitor) visitor).setRounding(false);
        }

        visitor.setTransform(AffineTransform.getScaleInstance(1,-1));
        visitor.visit(new RectangleElement(0, -(int) Math.ceil(total.h), (int) Math.ceil(total.w), (int) Math.ceil(total.h),
                                           true, model.get(BasicSceneGenerator.BackgroundColor.class)));

        // compound the fitting and scaling into a single value
        final double rescale = zoom * fitting * scale;

        // x,y base coordinates include the margin and centering (only if fitting to a size)
        final double xBase = margin + (total.w - 2*margin - (nCol-1)*padding - (rescale * xOffset[nCol])) / 2;
        final double yBase = margin + (total.h - 2*margin - (nRow-1)*padding - (rescale * yOffset[nRow])) / 2;

        for (int i = 0; i < elements.size(); i++) {
            final int row = i / nCol;
            final int col = i % nCol;

            // calc the 'view' bounds:
            //  amount of padding depends on which row or column we are in.
            //  the width/height of this col/row can be determined by the next offset
            double x = xBase + col * padding + rescale * xOffset[col];
            double y = yBase + row * padding + rescale * yOffset[row];
            double w = rescale * (xOffset[col+1] - xOffset[col]);
            double h = rescale * (yOffset[row+1] - yOffset[row]);

            draw(visitor, zoom, elements.get(i), rect(x, y, w, h));
        }

        if (wrapper != null) {
            wrapper.dispose();
            return wrapper.toString();
        } else {
            return visitor.toString();
        }
    }

    private Rectangle2D.Double rect(double x, double y, double w, double h) {
        return new Rectangle2D.Double(x, y, w, h);
    }
}
