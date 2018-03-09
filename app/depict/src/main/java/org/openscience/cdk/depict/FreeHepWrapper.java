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

import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.freehep.graphicsio.svg.SVGGraphics2D;
import org.freehep.graphicsio.ps.PSGraphics2D;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Internal - wrapper around the FreeHEP vector graphics output that makes things consistent
 * in terms of writing the required headers and footers.
 * @see <a href="http://java.freehep.org/">java.freehep.org</a>
 */
final class FreeHepWrapper {

    private final ByteArrayOutputStream bout;
    private final String                fmt;
    final         Graphics2D            g2;
    private final Dimension dim;

    public FreeHepWrapper(String fmt, double w, double h) {
        this.dim = new Dimension((int) Math.ceil(w), (int) Math.ceil(h));
        try {
            this.g2 = createGraphics2d(this.fmt = fmt,
                                       this.bout = new ByteArrayOutputStream(),
                                       this.dim);
        } catch (IOException e) {
            throw new InstantiationError("Could not create Vector Graphics output: " + e.getMessage());
        }
    }

    private static Graphics2D createGraphics2d(String fmt, OutputStream out, Dimension dim) throws IOException {
        switch (fmt) {
            case Depiction.SVG_FMT:
                SVGGraphics2D svg = new SVGGraphics2D(out, dim);
                svg.setCreator("Chemistry Development Kit (http://www.github.com/cdk/)");
                svg.writeHeader();
                return svg;
            case Depiction.PDF_FMT:
                PDFGraphics2D pdf = new PDFGraphics2D(out, dim);
                pdf.setCreator("Chemistry Development Kit (http://www.github.com/cdk/)");
                Properties props = new Properties();
                props.setProperty(PDFGraphics2D.FIT_TO_PAGE, "false");
                props.setProperty(PDFGraphics2D.PAGE_SIZE, PDFGraphics2D.CUSTOM_PAGE_SIZE);
                props.setProperty(PDFGraphics2D.CUSTOM_PAGE_SIZE, dim.width + ", " + dim.height);
                props.setProperty(PDFGraphics2D.PAGE_MARGINS, "0, 0, 0, 0");
                pdf.setProperties(props);
                pdf.writeHeader();
                return pdf;
            case Depiction.PS_FMT:
                PSGraphics2D eps = new PSGraphics2D(out, dim);
                // For EPS (Encapsulated PostScript) page size has no
                // meaning since this image is supposed to be included
                // in another page.
                Properties eps_props = new Properties();
                eps_props.setProperty(PDFGraphics2D.FIT_TO_PAGE, "false");
                eps.setProperties(eps_props);
                eps.writeHeader();
                return eps;
            default:
                throw new IOException("Unsupported vector format, " + fmt);
        }
    }

    public void dispose() {
        try {
            switch (fmt) {
                case Depiction.SVG_FMT:
                    ((SVGGraphics2D) g2).writeTrailer();
                    ((SVGGraphics2D) g2).closeStream();
                    break;
                case Depiction.PDF_FMT:
                    ((PDFGraphics2D) g2).writeTrailer();
                    ((PDFGraphics2D) g2).closeStream();
                    break;
            }
        } catch (IOException e) {
            // ignored we write to an internal array
        }
        g2.dispose();
    }

    // Documents cited below:
    // [PLDS92] PostScript Language Document Structuring Conventions Specification, Version 3.0, 25 September 1992
    // [EGFF96] J.D.Murray & W. vanPyper, Encyclopedia of Graphics File Formats 2nd ed., O'Reilly & Assoc., 1996

    @Override
    public String toString() {
        String result = new String(bout.toByteArray(), StandardCharsets.UTF_8);
        // we want SVG in mm not pixels!
        if (fmt.equals(Depiction.SVG_FMT)) {
            result = result.replaceAll("\"([-+0-9.]+)px\"", "\"$1mm\"");
        }
        if (fmt.equals(Depiction.PS_FMT)) {
            String nl = System.getProperty("line.separator");
            String split[] = result.split(nl,2);
            if( split.length > 1 && split[0].startsWith("%!PS-") ) {
                String boundingBox;
                if( this.dim != null ) {
                    boundingBox = "%%BoundingBox: 0 0 " +
                        dim.width + " " + dim.height + nl;
                } else {
                    boundingBox = "";
                }
                if(!split[0].contains("EPS") && !boundingBox.equals("")) {
                    split[0] += " EPSF-3.0";
                }
                // EGFF96 (p. 379):
                // "Both the %%PS-Adobe- [sic] and the %%BoundingBox: lines must appear in every EPS file.
                // Ordinary PostScript files may formally be changed into EPS files by adding these two lines
                // to the PostScript header."

                // PLDS92 (p. 29):
                // "The order of some comments in the document is significant, but in a
                // section of the document they may appear in any order. For example, in the
                // header section, %%DocumentResources:, %%Title:, and %%Creator: may
                // appear in any order."
                //
                // Thus, I infer that the "%%BoundingBox:" comment may be added immediately after the
                // "%!PS-..." header line (S.G.).

                result = split[0] + nl +
                    boundingBox +
                    split[1].
                    replaceFirst("(\\d+ ){4}setmargins",
                                 "0 0 0 0 setmargins").
                    replaceFirst("(\\d+ ){2}setpagesize",
                                 dim.width + " " + dim.height +
                                 " setpagesize");
            }
        }
        return result;
    }
}
