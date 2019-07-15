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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

    private final Multimap<String, Point2d[]> templateMap = LinkedListMultimap.create();

    private final SmilesGenerator smigen = SmilesGenerator.unique();
    private final ILoggingTool    logger = LoggingToolFactory.createLoggingTool(getClass());

    private IdentityTemplateLibrary() {
    }

    /**
     * Add one template library to another.
     *
     * @param library another template library
     * @return this library with the other one added in (allows chaining)
     */
    public IdentityTemplateLibrary add(IdentityTemplateLibrary library) {
        this.templateMap.putAll(library.templateMap);
        return this;
    }

    /**
     * Internal - create a canonical SMILES string temporarily adjusting to default
     * hydrogen count. This method may be moved to the SMILESGenerator in future.
     *
     * @param mol molecule
     * @param ordering ordering output
     * @return SMILES
     * @throws CDKException SMILES could be generate
     */
    private String cansmi(IAtomContainer mol, int[] ordering) throws CDKException {

        // backup parts we will strip off
        Integer[] hcntBackup = new Integer[mol.getAtomCount()];

        Map<IAtom, Integer> idxs = new HashMap<>();
        for (int i = 0; i < mol.getAtomCount(); i++) {
            hcntBackup[i] = mol.getAtom(i).getImplicitHydrogenCount();
            idxs.put(mol.getAtom(i), i);
        }

        int[] bondedValence = new int[mol.getAtomCount()];
        for (int i = 0; i < mol.getBondCount(); i++) {
            IBond bond = mol.getBond(i);
            bondedValence[idxs.get(bond.getBegin())] += bond.getOrder().numeric();
            bondedValence[idxs.get(bond.getEnd())] += bond.getOrder().numeric();
        }

        // http://www.opensmiles.org/opensmiles.html#orgsbst
        for (int i = 0; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            atom.setImplicitHydrogenCount(0);
            switch (atom.getAtomicNumber()) {
                case 5: // B
                    if (bondedValence[i] <= 3)
                        atom.setImplicitHydrogenCount(3 - bondedValence[i]);
                    break;
                case 6: // C
                    if (bondedValence[i] <= 4)
                        atom.setImplicitHydrogenCount(4 - bondedValence[i]);
                    break;
                case 7:  // N
                case 15: // P
                    if (bondedValence[i] <= 3)
                        atom.setImplicitHydrogenCount(3 - bondedValence[i]);
                    else if (bondedValence[i] <= 5)
                        atom.setImplicitHydrogenCount(5 - bondedValence[i]);
                    break;
                case 8:  // O
                    if (bondedValence[i] <= 2)
                        atom.setImplicitHydrogenCount(2 - bondedValence[i]);
                    break;
                case 16: // S
                    if (bondedValence[i] <= 2)
                        atom.setImplicitHydrogenCount(2 - bondedValence[i]);
                    else if (bondedValence[i] <= 4)
                        atom.setImplicitHydrogenCount(4 - bondedValence[i]);
                    else if (bondedValence[i] <= 6)
                        atom.setImplicitHydrogenCount(6 - bondedValence[i]);
                    break;
                case 9:  // F
                case 17: // Cl
                case 35: // Br
                case 53: // I
                    if (bondedValence[i] <= 1)
                        atom.setImplicitHydrogenCount(1 - bondedValence[i]);
                    break;
                default:
                    atom.setImplicitHydrogenCount(0);
                    break;
            }
        }

        String smi = null;
        try {
            smi = smigen.create(mol, ordering);
        } finally {
            // restore
            for (int i = 0; i < mol.getAtomCount(); i++)
                mol.getAtom(i).setImplicitHydrogenCount(hcntBackup[i]);
        }

        return smi;
    }

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
            final String smiles = cansmi(container, ordering);

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
        if (str.startsWith("|(")) {
            int end = str.indexOf(')', 2);
            if (end < 0)
                return new Point2d[0];
            String[] strs = str.substring(2, end).split(";");
            Point2d[] points = new Point2d[strs.length];
            for (int i = 0; i < strs.length; i++) {
                String coord = strs[i];
                int first  = coord.indexOf(',');
                int second = coord.indexOf(',', first+1);
                points[i] = new Point2d(Double.parseDouble(coord.substring(0, first)),
                                        Double.parseDouble(coord.substring(first + 1, second)));
            }
            return points;
        } else {
            String[] strs = str.split(", ");
            Point2d[] points = new Point2d[strs.length / 2];
            for (int i = 0; i < strs.length; i += 2) {
                points[i / 2] = new Point2d(Double.parseDouble(strs[i]), Double.parseDouble(strs[i + 1]));
            }
            return points;
        }
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
     * Encode coordinates in a string.
     *
     * @param points points
     * @return extended SMILES format coordinates
     */
    static String encodeCoordinates(Point2d[] points) {
        DecimalFormat fmt = new DecimalFormat(".##", DecimalFormatSymbols.getInstance(Locale.ROOT));
        StringBuilder sb = new StringBuilder();
        sb.append("|(");
        for (Point2d point : points) {
            if (sb.length() > 2) sb.append(";");
            sb.append(fmt.format(point.x));
            sb.append(',');
            sb.append(fmt.format(point.y));
            sb.append(',');
        }
        sb.append(")|");
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
     * Assign a 2D layout to the atom container using the contents of the library. If multiple
     * coordinates are available the first is choosen.
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
            String smiles = cansmi(container, ordering);

            // find the points in the library
            for (Point2d[] points : templateMap.get(smiles)) {
                // set the points
                for (int i = 0; i < n; i++) {
                    container.getAtom(i).setPoint2d(new Point2d(points[ordering[i]]));
                }
                return true;
            }
        } catch (CDKException e) {
            // ignored
        }
        return false;
    }

    /**
     * Get all templated coordinates for the provided molecule. The return collection has
     * coordinates ordered based on the input.
     *
     * @param mol molecule (or fragment) to lookup
     * @return the coordinates
     */
    Collection<Point2d[]> getCoordinates(IAtomContainer mol) {
        try {
            // create the library key to lookup an entry, we also store
            // the canonical out ordering
            int n = mol.getAtomCount();
            int[] ordering = new int[n];
            String smiles = cansmi(mol, ordering);

            final Collection<Point2d[]> coordSet = templateMap.get(smiles);
            final List<Point2d[]> orderedCoordSet = new ArrayList<>(coordSet.size());

            for (Point2d[] coords : coordSet) {
                Point2d[] orderedCoords = new Point2d[coords.length];
                for (int i = 0; i < n; i++) {
                    orderedCoords[i] = new Point2d(coords[ordering[i]]);
                }
                orderedCoordSet.add(orderedCoords);
            }
            return Collections.unmodifiableList(orderedCoordSet);
        } catch (CDKException e) {
            return Collections.emptyList();
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
     * Reorder coordinates.
     *
     * @param coords coordinates
     * @param order permutation
     * @return reordered coordinates
     */
    static Point2d[] reorderCoords(Point2d[] coords, int[] order) {
        Point2d[] neworder = new Point2d[coords.length];
        for (int i = 0; i < order.length; i++)
            neworder[order[i]] = coords[i];
        return neworder;
    }

    /**
     * Update the template library - can be called for safety after
     * each load.
     *
     * @param bldr builder
     */
    void update(IChemObjectBuilder bldr) {
        final SmilesParser smipar = new SmilesParser(bldr);
        Multimap<String,Point2d[]> updated = LinkedListMultimap.create();
        for (Map.Entry<String,Collection<Point2d[]>> e : templateMap.asMap().entrySet()) {
            try {
                IAtomContainer mol = smipar.parseSmiles(e.getKey());
                int[] order = new int[mol.getAtomCount()];
                String key = cansmi(mol, order);
                for (Point2d[] coords : e.getValue()) {
                    updated.put(key, reorderCoords(coords, order));
                }
            } catch (CDKException ex) {
                System.err.println(e.getKey() + " could not be updated: " + ex.getMessage());
            }
        }
        templateMap.clear();
        templateMap.putAll(updated);
    }

    /**
     * Store a template library to the provided output stream.
     *
     * @param out output stream
     * @throws IOException low level IO error
     */
    void store(OutputStream out) throws IOException {

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

        for (Entry<String, Point2d[]> e : templateMap.entries()) {
            bw.write(encodeEntry(e));
            bw.write('\n');
        }

        bw.close();
    }

}
