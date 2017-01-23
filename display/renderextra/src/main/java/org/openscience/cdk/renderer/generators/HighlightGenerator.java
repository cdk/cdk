/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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

package org.openscience.cdk.renderer.generators;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generate an under/overlaid highlight in structure depictions. The highlight
 * emphasises atoms and bonds. Each atom and bond is optionally assigned an
 * integer identifier. Entities with identifiers are then highlighted using the
 * {@link Palette} to determine the color. The size of the highlight is
 * specified with the {@link HighlightRadius} parameter.
 *
 * 
 * Basic usage:
 * <blockquote><pre>{@code
 * // create with the highlight generator
 * AtomContainerRenderer renderer = ...;
 *
 * IAtomContainer            m   = ...; // input molecule
 * Map<IChemObject, Integer> ids = new HashMap<>();
 *
 * // set atom/bond ids, atoms with no id will not be highlighted, numbering
 * // starts at 0
 * ids.put(m.getAtom(0), 0);
 * ids.put(m.getAtom(1), 0);
 * ids.put(m.getAtom(2), 0);
 * ids.put(m.getAtom(5), 2);
 * ids.put(m.getAtom(6), 1);
 *
 * ids.put(m.getBond(0), 0);
 * ids.put(m.getBond(1), 0);
 * ids.put(m.getBond(3), 1);
 * ids.put(m.getBond(4), 2);
 *
 * // attach ids to the structure
 * m.setProperty(HighlightGenerator.ID_MAP, ids);
 *
 * // draw
 * renderer.paint(m, new AWTDrawVisitor(g2), bounds, true);
 * }</pre></blockquote>
 *
 * By default colours are automatically generated, to assign specific colors
 * a custom {@link Palette} must be used. Here are some examples of setting
 * the palette parameter in the renderer.
 *
 * <blockquote><pre>
 * AtomContainerRenderer renderer = ...;
 *
 * // opaque colors
 * renderer.getRenderer2DModel()
 *         .set(HighlightGenerator.HighlightPalette.class,
 *              HighlightGenerator.createPalette(Color.RED, Color.BLUE, Color.GREEN));
 *
 * // opaque colors (hex)
 * renderer.getRenderer2DModel()
 *         .set(HighlightGenerator.HighlightPalette.class,
 *              HighlightGenerator.createPalette(new Color(0xff0000), Color.BLUE, Color.GREEN));
 *
 * // first color is transparent
 * renderer.getRenderer2DModel()
 *         .set(HighlightGenerator.HighlightPalette.class,
 *              HighlightGenerator.createPalette(new Color(0x88ff0000, true), Color.BLUE, Color.GREEN));
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module renderextra
 * @cdk.githash
 */
public final class HighlightGenerator implements IGenerator<IAtomContainer> {

    /** The atom radius on screen. */
    private final HighlightRadius  highlightRadius  = new HighlightRadius();

    /** Color palette to use. */
    private final HighlightPalette highlightPalette = new HighlightPalette();

    /** Property key. */
    public static final String     ID_MAP           = "cdk.highlight.id";

    /**{@inheritDoc} */
    @Override
    public IRenderingElement generate(IAtomContainer container, RendererModel model) {

        final Map<IChemObject, Integer> highlight = container.getProperty(ID_MAP);

        if (highlight == null) return null;

        final Palette palette = model.getParameter(HighlightPalette.class).getValue();
        final double radius = model.getParameter(HighlightRadius.class).getValue()
                / model.getParameter(BasicSceneGenerator.Scale.class).getValue();

        final Map<Integer, Area> shapes = new HashMap<Integer, Area>();

        for (IAtom atom : container.atoms()) {

            Integer id = highlight.get(atom);

            if (id == null) continue;

            Area area = shapes.get(id);
            Shape shape = createAtomHighlight(atom, radius);

            if (area == null)
                shapes.put(id, new Area(shape));
            else
                area.add(new Area(shape));
        }

        for (IBond bond : container.bonds()) {

            Integer id = highlight.get(bond);

            if (id == null) continue;

            Area area = shapes.get(id);
            Shape shape = createBondHighlight(bond, radius);

            if (area == null)
                shapes.put(id, (area = new Area(shape)));
            else
                area.add(new Area(shape));

            // punch out the area occupied by atoms highlighted with a
            // different color

            IAtom a1 = bond.getAtom(0), a2 = bond.getAtom(1);
            Integer a1Id = highlight.get(a1), a2Id = highlight.get(a2);

            if (a1Id != null && !a1Id.equals(id)) area.subtract(shapes.get(a1Id));
            if (a2Id != null && !a2Id.equals(id)) area.subtract(shapes.get(a2Id));
        }

        // create rendering elements for each highlight shape
        ElementGroup group = new ElementGroup();
        for (Map.Entry<Integer, Area> e : shapes.entrySet()) {
            group.add(GeneralPath.shapeOf(e.getValue(), palette.color(e.getKey())));
        }

        return group;
    }

    /**
     * Create the shape which will highlight the provided atom.
     *
     * @param atom   the atom to highlight
     * @param radius the specified radius
     * @return the shape which will highlight the atom
     */
    private static Shape createAtomHighlight(IAtom atom, double radius) {
        double x = atom.getPoint2d().x;
        double y = atom.getPoint2d().y;

        return new RoundRectangle2D.Double(x - radius, y - radius, 2 * radius, 2 * radius, 2 * radius, 2 * radius);
    }

    /**
     * Create the shape which will highlight the provided bond.
     *
     * @param bond   the bond to highlight
     * @param radius the specified radius
     * @return the shape which will highlight the atom
     */
    private static Shape createBondHighlight(IBond bond, double radius) {

        double x1 = bond.getAtom(0).getPoint2d().x;
        double x2 = bond.getAtom(1).getPoint2d().x;
        double y1 = bond.getAtom(0).getPoint2d().y;
        double y2 = bond.getAtom(1).getPoint2d().y;

        double dx = x2 - x1;
        double dy = y2 - y1;

        double mag = Math.sqrt((dx * dx) + (dy * dy));

        dx /= mag;
        dy /= mag;

        double r2 = radius / 2;

        Shape s = new RoundRectangle2D.Double(x1 - r2, y1 - r2, mag + radius, radius, radius, radius);

        double theta = Math.atan2(dy, dx);

        return AffineTransform.getRotateInstance(theta, x1, y1).createTransformedShape(s);
    }

    /**{@inheritDoc} */
    @Override
    public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(new IGeneratorParameter<?>[]{highlightRadius, highlightPalette});
    }

    /**
     * Create a palette which uses the provided colors.
     *
     * @param colors colors to use in the palette
     * @return a palette to use in highlighting
     */
    public static Palette createPalette(final Color[] colors) {
        return new FixedPalette(colors);
    }

    /**
     * Create a palette which uses the provided colors.
     *
     * @param colors colors to use in the palette
     * @return a palette to use in highlighting
     */
    public static Palette createPalette(final Color color, final Color... colors) {
        Color[] cs = new Color[colors.length + 1];
        cs[0] = color;
        System.arraycopy(colors, 0, cs, 1, colors.length);
        return new FixedPalette(cs);
    }

    /**
     * Create an auto generating palette which will generate colors using the
     * provided parameters.
     *
     * @param saturation color saturation, 0.0 &lt; x &lt; 1.0
     * @param brightness color brightness, 0.0 &lt; x &lt; 1.0
     * @param alpha color alpha (transparency), 0 &lt; x &lt; 255
     * @return a palette to use in highlighting
     */
    public static Palette createAutoPalette(float saturation, float brightness, int alpha) {
        return new AutoGenerated(5, saturation, brightness, alpha);
    }

    /**
     * Create an auto generating palette which will generate colors using the
     * provided parameters.
     *
     * @param saturation color saturation, 0.0 &lt; x &lt; 1.0
     * @param brightness color brightness, 0.0 &lt; x &lt; 1.0
     * @param transparent generate transparent colors, 0 &lt; x &lt; 255
     * @return a palette to use in highlighting
     */
    public static Palette createAutoGenPalette(float saturation, float brightness, boolean transparent) {
        return new AutoGenerated(5, saturation, brightness, transparent ? 200 : 255);
    }

    /**
     * Create an auto generating palette which will generate colors using the
     * provided parameters.
     *
     * @param transparent generate transparent colors
     * @return a palette to use in highlighting
     */
    public static Palette createAutoGenPalette(boolean transparent) {
        return new AutoGenerated(5, transparent ? 200 : 255);
    }

    /**
     * Defines a color palette, the palette should provide a color the specified
     * identifier (id).
     */
    public static interface Palette {

        /**
         * Obtain the color in index, id.
         *
         * @param id the id of the color
         * @return a color
         */
        Color color(int id);
    }

    /**
     * A palette that allows one to define the precise colors of each class. The
     * colors are passed in the constructor.
     */
    private static final class FixedPalette implements Palette {

        /** Colors of the palette. */
        private final Color[] colors;

        /**
         * Create a fixed palette for the specified colors.
         *
         * @param colors the colors in the palette.
         */
        public FixedPalette(Color[] colors) {
            this.colors = Arrays.copyOf(colors, colors.length);
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public Color color(int id) {
            if (id < 0) throw new IllegalArgumentException("id should be positive");
            if (id >= colors.length) throw new IllegalArgumentException("no color has been provided for id=" + id);
            return colors[id];
        }
    }

    /**
     * An automatically generating color palette. The palette use the golden
     * ratio to generate colors with varied hue.
     *
     * @see <a href="http://martin.ankerl.com/2009/12/09/how-to-create-random-colors-programmatically/">Create Random Colors Programmatically</a>
     */
    private static final class AutoGenerated implements Palette {

        /** Golden ratio. */
        private static final float PHI    = 0.618033988749895f;

        /** Starting color - adjust for a different start color. */
        private static final int   offset = 14;

        /** The colors. */
        private Color[]            colors;

        /** Color alpha. */
        private final int          alpha;

        /** The saturation and brightness values. */
        private final float        saturation, brightness;

        /**
         * Create an automatically generating color palette.
         *
         * @param n     pre-generate this many colors
         * @param alpha transparency (0-255)
         */
        public AutoGenerated(int n, int alpha) {
            this(n, 0.45f, 0.95f, alpha);
        }

        /**
         * Create an automatically generating color palette.
         *
         * @param n          pre-generate this many colors
         * @param saturation color saturation (0-1f)
         * @param brightness color brightness (0-1f)
         * @param alpha      transparency (0-255)
         */
        public AutoGenerated(int n, float saturation, float brightness, int alpha) {
            this.colors = new Color[n];
            this.alpha = alpha;
            this.saturation = saturation;
            this.brightness = brightness;
            fill(colors, 0, n - 1);
        }

        /**
         * Fill the indices, from - to inclusive, in the colors array with
         * generated colors.
         *
         * @param colors indexed colors
         * @param from   first index
         * @param to     last index
         */
        private void fill(Color[] colors, int from, int to) {
            if (alpha < 255) {
                for (int i = from; i <= to; i++) {
                    Color c = Color.getHSBColor((offset + i) * PHI, saturation, brightness);
                    colors[i] = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
                }
            } else {
                for (int i = from; i <= to; i++)
                    colors[i] = Color.getHSBColor((offset + i) * PHI, saturation, brightness);
            }
        }

        /**{@inheritDoc} */
        @Override
        public Color color(int id) {
            if (id < 0) throw new IllegalArgumentException("id should be positive");
            if (id >= colors.length) {
                int org = colors.length;
                colors = Arrays.copyOf(colors, id * 2);
                fill(colors, org, colors.length - 1);
            }
            return colors[id];
        }
    }

    /**
     * Magic number with unknown units that defines the radius around an atom,
     * e.g. used for highlighting atoms.
     */
    public static class HighlightRadius extends AbstractGeneratorParameter<Double> {

        /**
         * Returns the default value.
         *
         * @return 10.0
         */
        @Override
        public Double getDefault() {
            return 10.0;
        }
    }

    /** Default color palette. */
    private static final Palette DEFAULT_PALETTE = createAutoGenPalette(true);

    /** Defines the color palette used to provide the highlight colors. */
    public static class HighlightPalette extends AbstractGeneratorParameter<Palette> {

        /**
         * Returns the default value.
         *
         * @return an auto-generating palette
         */
        @Override
        public Palette getDefault() {
            return DEFAULT_PALETTE;
        }
    }
}
