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

import com.google.common.base.Charsets;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.openscience.cdk.depict.DepictionGenerator.AUTOMATIC;

/**
 * Base class of a pre-rendered depiction. The class allows introspection of
 * depiction size (decided at generation time) and serialization to raster
 * and vector graphic formats.
 *
 * @author John May
 */
@SuppressWarnings("PMD.ShortVariable")
public abstract class Depiction {

    /**
     * For converting MM coordinates to PS Point (1/72 inch)
     */
    protected static final double MM_TO_POINT = 2.83464566751;

    /**
     * When no fixed padding value is specified we use margin
     * multiplied by this value.
     */
    protected static final double DEFAULT_PADDING_FACTOR = 2;

    /**
     * Structured Vector Graphics (SVG) format key.
     */
    public static final String SVG_FMT = "svg";

    /**
     * PostScript (PS) format key.
     */
    public static final String PS_FMT = "ps";

    /**
     * Portable Document Format (PDF) format key.
     */
    public static final String PDF_FMT = "pdf";

    /**
     * Joint Photographic Experts Group (JPG) format key.
     */
    public static final String JPG_FMT = "jpg";

    /**
     * Portable Network Graphics (PNG) format key.
     */
    public static final String PNG_FMT = "png";

    /**
     * Graphics Interchange Format (GIF) format key.
     */
    public static final String GIF_FMT = "gif";

    static final double ACS_1996_BOND_LENGTH_MM = 5.08;

    private static final char DOT = '.';

    private final RendererModel model;

    /**
     * Internal method passes in the rendering model parameters.
     *
     * @param model parameters
     */
    Depiction(RendererModel model) {
        this.model = model;
    }

    /**
     * Render the depiction to a Java AWT {@link BufferedImage}.
     *
     * @return AWT buffered image
     */
    public abstract BufferedImage toImg();

    /**
     * Render the image to an SVG image.
     *
     * @return svg XML content
     */
    public final String toSvgStr() {
        return toVecStr(SVG_FMT);
    }

    /**
     * Render the image to an EPS format string.
     *
     * @return eps content
     */
    public final String toEpsStr() {
        return toVecStr(PS_FMT);
    }

    /**
     * Render the image to an PDF format string.
     *
     * @return pdf content
     */
    public final String toPdfStr() {
        return toVecStr(PDF_FMT);
    }

    /**
     * Access the specified padding value or fallback to a provided
     * default.
     *
     * @param defaultPadding default value if the parameter is 'automatic'
     * @return padding
     */
    double getPaddingValue(double defaultPadding) {
        double padding = model.get(RendererModel.Padding.class);
        if (padding == AUTOMATIC)
            padding = defaultPadding;
        return padding;
    }

    /**
     * Access the specified margin value or fallback to a provided
     * default.
     *
     * @param defaultMargin default value if the parameter is 'automatic'
     * @return margin
     */
    double getMarginValue(final double defaultMargin) {
        double margin = model.get(BasicSceneGenerator.Margin.class);
        if (margin == AUTOMATIC)
            margin = defaultMargin;
        return margin;
    }

    /**
     * Internal - implementations should overload this method for vector graphics
     * rendering.
     *
     * @param fmt the vector graphics format
     * @return the vector graphics format string
     */
    abstract String toVecStr(String fmt);

    /**
     * List the available formats that can be rendered.
     *
     * @return supported formats
     */
    public final List<String> listFormats() {
        final List<String> formats = new ArrayList<>();
        formats.add(SVG_FMT);
        formats.add(SVG_FMT.toUpperCase(Locale.ROOT));
        formats.add(PS_FMT);
        formats.add(PS_FMT.toUpperCase(Locale.ROOT));
        formats.add(PDF_FMT);
        formats.add(PDF_FMT.toUpperCase(Locale.ROOT));
        formats.addAll(Arrays.asList(ImageIO.getWriterFormatNames()));
        return formats;
    }

    /**
     * Write the depiction to the provided output stream.
     *
     * @param fmt format
     * @param out output stream
     * @throws IOException depiction could not be written, low level IO problem
     * @see #listFormats()
     */
    public final void writeTo(String fmt, OutputStream out) throws IOException {
        if (fmt.equalsIgnoreCase(SVG_FMT)) {
            out.write(toSvgStr().getBytes(Charsets.UTF_8));
        } else if (fmt.equalsIgnoreCase(PS_FMT)) {
            out.write(toEpsStr().getBytes(Charsets.UTF_8));
        } else if (fmt.equalsIgnoreCase(PDF_FMT)) {
            out.write(toPdfStr().getBytes(Charsets.UTF_8));
        } else {
            ImageIO.write(toImg(), fmt, out);
        }
    }

    /**
     * Write the depiction to the provided output stream.
     *
     * @param fmt  format
     * @param file output destination
     * @throws IOException depiction could not be written, low level IO problem
     * @see #listFormats()
     */
    public final void writeTo(String fmt, File file) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file)) {
            writeTo(fmt, out);
        }
    }

    /**
     * Write the depiction to the provided file path.
     *
     * @param fmt  format
     * @param path output destination path
     * @throws IOException depiction could not be written, low level IO problem
     * @see #listFormats()
     */
    public final void writeTo(String fmt, String path) throws IOException {
        writeTo(fmt, new File(replaceTildeWithHomeDir(ensureSuffix(path, fmt))));
    }

    /**
     * Write the depiction to the provided file path, the format is determined
     * by the path suffix.
     *
     * @param path output destination path
     * @throws IOException depiction could not be written, low level IO problem
     * @see #listFormats()
     */
    public final void writeTo(String path) throws IOException {
        final int i = path.lastIndexOf(DOT);
        if (i < 0 || i + 1 == path.length())
            throw new IOException("Cannot find suffix in provided path: " + path);
        final String fmt = path.substring(i + 1);
        writeTo(fmt, path);
    }

    /**
     * Utility for resolving paths on unix systems that contain tilda for
     * the home directory.
     *
     * @param path the file system path
     * @return normalised path
     */
    private static String replaceTildeWithHomeDir(String path) {
        if (path.startsWith("~/"))
            return System.getProperty("user.home") + path.substring(1);
        return path;
    }

    /**
     * Ensures a suffix on a file output if the path doesn't
     * currently end with it. For example calling
     * {@code writeTo(SVG_FMT, "~/chemical")} would create a file
     * {@code ~/chemical.svg}.
     *
     * @param path   the file system path
     * @param suffix the format suffix
     * @return path with correct suffix
     */
    private static String ensureSuffix(String path, String suffix) {
        if (path.endsWith(DOT + suffix))
            return path;
        return path + DOT + suffix;
    }

    /**
     * Low-level draw method used by other rendering methods.
     *
     * @param visitor    the draw visitor
     * @param bounds     a bound rendering element
     * @param zoom       if the diagram is zoomed at all
     * @param viewBounds the view bounds - the root will be centered in the bounds
     */
    protected final void draw(IDrawVisitor visitor, double zoom, Bounds bounds, Rectangle2D viewBounds) {

        double modelScale = zoom * model.get(BasicSceneGenerator.Scale.class);
        double zoomToFit = Math.min(viewBounds.getWidth() / (bounds.width() * modelScale),
                                    viewBounds.getHeight() / (bounds.height() * modelScale));

        AffineTransform transform = new AffineTransform();
        transform.translate(viewBounds.getCenterX(), viewBounds.getCenterY());
        transform.scale(modelScale, -modelScale);

        // default is shrink only unless specified
        if (model.get(BasicSceneGenerator.FitToScreen.class) || zoomToFit < 1)
            transform.scale(zoomToFit, zoomToFit);

        transform.translate(-(bounds.minX + bounds.maxX) / 2,
                            -(bounds.minY + bounds.maxY) / 2);

        // not always needed
        AWTFontManager fontManager = new AWTFontManager();
        fontManager.setFontForZoom(zoomToFit);

        visitor.setRendererModel(model);
        visitor.setFontManager(fontManager);
        visitor.setTransform(transform);

        // setup up transform
        visitor.visit(bounds.root());
    }

    /**
     * Utility method for recalling a depiction in pixels to one in millimeters.
     *
     * @param bondLength the desired bond length (mm)
     * @return the scaling factor
     */
    final double rescaleForBondLength(double bondLength) {
        return bondLength / model.get(BasicSceneGenerator.BondLength.class);
    }

    protected void svgPrevisit(String fmt, double rescale, SvgDrawVisitor visitor, List<? extends IRenderingElement> elements) {
        visitor.setTransform(AffineTransform.getScaleInstance(rescale, rescale));
        visitor.previsit(elements);
        visitor.setTransform(null);
    }
}
