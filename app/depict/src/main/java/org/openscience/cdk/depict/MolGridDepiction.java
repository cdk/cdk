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
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Internal - depicts a set of molecules aligned in a grid. This class
 * also handles the degenerate case of a single molecule as a 1x1 grid.
 */
final class MolGridDepiction extends Depiction {

    private final RendererModel model;
    private final Dimensions    dimensions;
    private final int           nCol, nRow;
    private final List<Bounds>  molecules;
    private final List<Bounds>  titles;

    public MolGridDepiction(RendererModel model,
                            List<Bounds> molecules,
                            List<Bounds> titles,
                            Dimensions dimensions,
                            int nRow, int nCol) {
        super(model);
        this.model = model;
        this.dimensions = dimensions;
        this.nCol = nCol;
        this.nRow = nRow;
        this.molecules = molecules;
        this.titles = titles;
    }

    @Override
    public BufferedImage toImg() {

        // format margins and padding for raster images
        final double margin  = getMarginValue(DepictionGenerator.DEFAULT_PX_MARGIN);
        final double padding = getPaddingValue(2 * margin);
        final double scale   = model.get(BasicSceneGenerator.Scale.class);

        // row and col offsets for alignment
        double[] yOffset = new double[nRow+1];
        double[] xOffset = new double[nCol+1];

        Dimensions required    = Dimensions.ofGrid(molecules, yOffset, xOffset)
                                           .scale(scale);

        final Dimensions total = calcTotalDimensions(margin, padding, required);
        final double fitting   = calcFitting(margin, padding, required);

        // create the image for rendering
        final BufferedImage img = new BufferedImage((int) Math.ceil(total.w), (int) Math.ceil(total.h),
                                                    BufferedImage.TYPE_4BYTE_ABGR);
        // we use the AWT for vector graphics if though we're raster because
        // fractional strokes can be figured out by interpolation, without
        // when we shrink diagrams bonds can look too bold/chubby
        final Graphics2D g2 = img.createGraphics();
        final AWTDrawVisitor visitor = AWTDrawVisitor.forVectorGraphics(g2);

        g2.setBackground(model.get(BasicSceneGenerator.BackgroundColor.class));
        g2.clearRect(0, 0, img.getWidth(), img.getHeight());

        // compound the fitting and scaling into a single value
        final double rescale = fitting * scale;

        // x,y base coordinates include the margin and centering (only if fitting to a size)
        final double xBase = margin + (total.w - 2*margin - (nCol-1)*padding - (rescale * xOffset[nCol])) / 2;
        final double yBase = margin + (total.h - 2*margin - (nRow-1)*padding - (rescale * yOffset[nRow])) / 2;

        for (int i = 0; i < molecules.size(); i++) {
            final int row = i / nRow;
            final int col = i % nCol;

            // calc the 'view' bounds:
            //  amount of padding depends on which row or column we are in.
            //  the width/height of this col/row can be determined by the next offset
            double x = xBase + col * padding + rescale * xOffset[col];
            double y = yBase + row * padding + rescale * yOffset[row];
            double w = rescale * (xOffset[col+1] - xOffset[col]);
            double h = rescale * (yOffset[row+1] - yOffset[row]);

            draw(visitor,
                 molecules.get(i),
                 new Rectangle2D.Double(x, y, w, h));
        }

        // we created the Graphic2d instance so need to dispose of it
        g2.dispose();
        return img;
    }

    private double calcFitting(double margin, double padding, Dimensions required) {
        if (dimensions == Dimensions.AUTOMATIC)
            return 1; // no fitting
        Dimensions targetDim = dimensions.add(-2 * margin, -2 * margin)
                                         .add(-((nCol - 1) * padding), -((nRow - 1) * padding));
        double resize = Math.min(targetDim.w / required.w,
                                 targetDim.h / required.h);
        if (resize > 1 && !model.get(BasicSceneGenerator.FitToScreen.class))
            resize = 1;
        return resize;
    }

    private Dimensions calcTotalDimensions(double margin, double padding, Dimensions required) {
        if (dimensions == Dimensions.AUTOMATIC) {
            return required.add(2 * margin, 2 * margin)
                           .add((nCol - 1) * padding, (nRow - 1) * padding);
        } else {
            return dimensions;
        }
    }

    @Override
    String toVecStr(String fmt) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
