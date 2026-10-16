/* Copyright (C) 2001-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *               2013,2016  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.config;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * List of isotopes. Data is taken from the <a href="https://github.com/egonw/bodr">Blue Obelisk Data Repository</a>,
 * <a href="https://github.com/egonw/bodr/releases/tag/BODR-10">version 10</a> {@cdk.cite BODR10}.
 * The data set is described in the first Blue Obelisk paper {@cdk.cite Guha2006}.
 *
 * <p>The <code>isotopes.dat</code> file that is used by this class is a binary class
 * of this data, improving loading times over the BODR XML representation. It is created
 * from the original BODR files using tools from the <code>cdk-build-util</code>
 * repository.
 *
 * @author Egon Willighagen
 * @author John Mayfield
 */
public class Isotopes implements IsotopeFactory {
    
    private static final ILoggingTool logger =
            LoggingToolFactory.createLoggingTool(Isotopes.class);

    private static Isotopes myself = null;
    private final List<IIsotope> isotopes;

    /**
     * Returns a singleton instance of this class.
     *
     * @return the singleton
     * @throws IOException when reading of the data file did not work
     */
    public static Isotopes getInstance() throws IOException {
        if (myself == null) myself = new Isotopes();
        return myself;
    }

    private Isotopes() throws IOException {
        String configFile = "org/openscience/cdk/config/data/isotopes.dat";
        try (InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (ins == null) {
                throw new IllegalStateException("Could not load isotope data");
            } else {
                int streamSize = ins.available();
                try (ReadableByteChannel fcIn = Channels.newChannel(ins)) {
                    ByteBuffer bin = ByteBuffer.allocate(streamSize);
                    fcIn.read(bin);
                    ((Buffer) bin).position(0);
                    int numIsotopes = bin.getInt();
                    int numNatural  = bin.getShort() & 0xffff;
                    List<IIsotope> tmp = new ArrayList<>(numIsotopes);
                    while (tmp.size() < numNatural)
                        readIsotopes(tmp, bin, true);
                    while (tmp.size() < numIsotopes)
                        readIsotopes(tmp, bin, false);
                    isotopes = Collections.unmodifiableList(tmp);
                }
            }
        }
    }

    /**
     * Read a run of binary isotope data. The first values says how many
     * numbers there are in this run, then the atomic number and mass are
     * written (mass as a delta from the atomic number). Then the
     * sequence of mass values (written as fixed precision). If
     * abundance information is stored then these are alternate with the
     * exact mass.
     *
     * <pre>{@code
     * {count:n} {elem} {massNum} {mass0} {mass1} {mass2} ..  {massN}
     * {count:n} {elem} {massNum} {mass0} {abun0} {mass1} {abun1} ..  {massN} {abunN}
     * }</pre>
     *
     * @param isotopes where to store the read isotopes.
     * @param buffer input buffer
     * @param abundance abundance is stored
     */
    private void readIsotopes(List<IIsotope> isotopes, ByteBuffer buffer, boolean abundance) {
        int count = buffer.get() & 0xff;
        int atomicNum = buffer.get() & 0xff;
        int massNum = atomicNum + (buffer.get() & 0xff);
        for (int i = 0; i < count; i++) {
            long fixedMass = buffer.getInt() & 0xffffffffL;
            if (abundance) {
                long fixedAbundance = buffer.getInt() & 0xffffffffL;
                isotopes.add(new ImmutableIsotope(atomicNum, massNum,
                                                  toDecimal(massNum, fixedMass),
                                                  toDecimal(100, fixedAbundance)));
            } else {
                isotopes.add(new ImmutableIsotope(atomicNum,
                                                  massNum,
                                                  toDecimal(massNum, fixedMass), 0));
            }
            massNum++;
        }
    }

    /**
     * Convert a value stored as a 4-byte integer (represented as a long here
     * to keep things unsigned) to the decimal value. The scaling depends on
     * the mass number.
     *
     * @param massNum mass number
     * @param v the value to convert
     * @return the decimal value
     */
    private double toDecimal(int massNum, long v) {
        // could use BigDecimal
        if (massNum < 4)
            return v / 1e9; // 1 billion
        else if (massNum < 40)
            return v / 1e8; // 100 million
        return v / 1e7; // 10 million
    }

    @Override
    public List<IIsotope> isotopes() {
        return isotopes;
    }

    private static boolean isMajor(IIsotope atom) {
        Integer mass = atom.getMassNumber();
        if (mass == null)
            return false;
        try {
            Isotopes instance = Isotopes.getInstance();
            IIsotope major = instance.getMajorIsotope(atom.getAtomicNumber());
            if (major == null)
                return false; // no major isotope
            return major.getMassNumber().equals(mass);
        } catch (IOException e) {
            logger.error("Could not load Isotope data: ", e.getMessage());
            return false;
        }
    }

    /**
     * Clear the isotope information from atoms that are major isotopes (e.g.
     * <sup>12</sup>C, <sup>1</sup>H, etc).
     *
     * @param mol the molecule
     */
    public static void clearMajorIsotopes(IAtomContainer mol) {
        for (IAtom atom : mol.atoms())
            if (isMajor(atom)) {
                atom.setMassNumber(null);
                atom.setExactMass(null);
                atom.setNaturalAbundance(null);
            }
    }

    /**
     * Clear the isotope information from istopes that are major (e.g.
     * <sup>12</sup>C, <sup>1</sup>H, etc).
     *
     * @param formula the formula
     */
    public static void clearMajorIsotopes(IMolecularFormula formula) {
        List<IIsotope> majorIsotopes = new ArrayList<>();
        formula.isotopes().forEach(i -> {
            if (isMajor(i)) majorIsotopes.add(i);
        });
        for (IIsotope iso : majorIsotopes) {
            int count = formula.getIsotopeCount(iso);
            formula.removeIsotope(iso);
            if (iso instanceof ImmutableIsotope) {
                iso = formula.getBuilder().newInstance(IIsotope.class, iso.getSymbol());
                iso.setAtomicNumber(iso.getAtomicNumber());
                iso.setMassNumber(null);
                iso.setExactMass(null);
                iso.setNaturalAbundance(null);
            }
            formula.addIsotope(iso, count);
        }
    }
}
