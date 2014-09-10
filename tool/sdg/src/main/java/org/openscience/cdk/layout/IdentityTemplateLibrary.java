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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.layout;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import javax.vecmath.Point2d;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;
import static java.util.Map.Entry;

/**
 * A library for 2D layout templates that are retrieved based on identity. Such a library is useful
 * for ensure ring systems are laid out in their de facto orientation. Importantly, identity
 * templates means the library size can be very large but still searched in constant time.
 *
 * <pre>{@code
 *
 * // load from a resource file on the classpath
 * IdentityTemplateLibrary lib = IdentityTemplateLibrary.loadFromResource("/data/ring-templates.smi");
 *
 * IAtomContainer container, container2;
 *
 * // add to the library
 * lib.add(container);
 *
 * // assign a layout
 * boolean modified = lib.assignLayout(container2);
 *
 * // store
 * OutputStream out = new FileOutputStream("/tmp/lib.smi");
 * lib.store(out);
 * out.close();
 * }</pre>
 *
 * @author John May
 */
final class IdentityTemplateLibrary {

    private final Map<String, Point2d[]> templateMap = new HashMap<String, Point2d[]>();

    private final SmilesGenerator        smigen      = SmilesGenerator.unique();
    private final ILoggingTool           logger      = LoggingToolFactory.createLoggingTool(getClass());

    private IdentityTemplateLibrary() {}

    /**
     * Create a library entry from an atom container. Note the entry is not added to the library.
     *
     * @param container structure representation
     * @return a new library entry (not stored).
     * @see #add(java.util.Map.Entry)
     */
    Entry<String, Point2d[]> createEntry(final IAtomContainer container) {
        try {

            final int n = container.getAtomCount();
            final int[] ordering = new int[n];
            final String smiles = smigen.create(container, ordering);

            // build point array that is in the canonical output order
            final Point2d[] points = new Point2d[n];
            for (int i = 0; i < n; i++) {
                Point2d point = container.getAtom(i).getPoint2d();

                if (point == null) {
                    logger.warn("Atom at index ", i, " did not have coordinates.");
                    return null;
                }

                points[ordering[i]] = point;
            }

            return new SimpleEntry<String, Point2d[]>(smiles, points);

        } catch (CDKException e) {
            logger.warn("Could not encode container as SMILES: ", e);
        }

        return null;
    }

    /**
     * Create a library entry from a SMILES string with the coordinates suffixed in binary. The
     * entry should be created with {@link #encodeEntry(java.util.Map.Entry)} and not created
     * manually. Note, the entry is not added to the library.
     *
     * @param str input string
     * @return library entry
     */
    static Entry<String, Point2d[]> decodeEntry(String str) {
        final int i = str.indexOf(' ');
        if (i < 0) throw new IllegalArgumentException();
        return new SimpleEntry<String, Point2d[]>(str.substring(0, i), decodeCoordinates(str.substring(i + 1)));
    }

    /**
     * Decode coordinates that have been placed in a byte buffer.
     *
     * @param str the string to decode
     * @return array of coordinates
     */
    static Point2d[] decodeCoordinates(String str) {
        String[] strs = str.split(", ");
        Point2d[] points = new Point2d[strs.length / 2];
        for (int i = 0; i < strs.length; i += 2) {
            points[i / 2] = new Point2d(Double.parseDouble(strs[i]), Double.parseDouble(strs[i + 1]));
        }
        return points;
    }

    /**
     * Encodes an entry in a compact string representation. The encoded entry is a SMILES string
     * with the coordinates suffixed in binary.
     *
     * @param entry the entry to encode
     * @return encoded entry
     */
    static String encodeEntry(Entry<String, Point2d[]> entry) {
        StringBuilder sb = new StringBuilder();
        sb.append(entry.getKey());
        sb.append(' ');
        sb.append(encodeCoordinates(entry.getValue()));
        return sb.toString();
    }

    /**
     * Encode coordinates in a byte buffer.
     *
     * @param points
     * @return
     */
    static String encodeCoordinates(Point2d[] points) {
        StringBuilder sb = new StringBuilder();
        for (Point2d point : points) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(String.format("%.3f", point.x));
            sb.append(", ");
            sb.append(String.format("%.3f", point.y));
        }
        return sb.toString();
    }

    /**
     * Add a created entry to the library.
     *
     * @param entry entry
     */
    void add(Entry<String, Point2d[]> entry) {
        if (entry != null) templateMap.put(entry.getKey(), entry.getValue());
    }

    /**
     * Create an entry for the provided container and add it to the library.
     *
     * @param container structure representation
     */
    void add(IAtomContainer container) {
        add(createEntry(container));
    }

    /**
     * Assign a 2D layout to the atom container using the contents of the library.
     *
     * @param container structure representation
     * @return a layout was assigned
     */
    boolean assignLayout(IAtomContainer container) {

        try {
            // create the library key to lookup an entry, we also store
            // the canonical out ordering
            int n = container.getAtomCount();
            int[] ordering = new int[n];
            String smiles = smigen.create(container, ordering);

            // find the points in the library
            Point2d[] points = templateMap.get(smiles);

            // no matching entry
            if (points == null) return false;

            // set the points
            for (int i = 0; i < n; i++) {
                container.getAtom(i).setPoint2d(new Point2d(points[ordering[i]]));
            }

            return true;
        } catch (CDKException e) {
            return false;
        }
    }

    /**
     * Create an empty template library.
     *
     * @return an empty template library
     */
    static IdentityTemplateLibrary empty() {
        return new IdentityTemplateLibrary();
    }

    /**
     * Load a template library from a resource on the class path.
     *
     * @return loaded template library
     * @throws java.lang.IllegalArgumentException resource not found or could not be loaded
     */
    static IdentityTemplateLibrary loadFromResource(String resource) {
        InputStream in = IdentityTemplateLibrary.class.getResourceAsStream(resource);
        try {
            return load(in);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not load template library from resource " + resource, e);
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                // ignored
            }
        }
    }

    /**
     * Load a template library from an input stream.
     *
     * @return loaded template library
     * @throws java.io.IOException low level IO error
     */
    static IdentityTemplateLibrary load(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        IdentityTemplateLibrary library = new IdentityTemplateLibrary();
        while ((line = br.readLine()) != null) {
            // skip comments
            if (line.charAt(0) == '#') continue;
            library.add(decodeEntry(line));
        }
        return library;
    }

    /**
     * Store a template library to the provided output stream.
     *
     * @param out output stream
     * @throws IOException low level IO error
     */
    void store(OutputStream out) throws IOException {

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

        for (Entry<String, Point2d[]> e : templateMap.entrySet()) {
            bw.write(encodeEntry(e));
            bw.newLine();
        }

        bw.close();
    }

}
