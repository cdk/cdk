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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

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
 * @author      egonw
 * @cdk.module  core
 * @cdk.githash
 */
public class Isotopes extends IsotopeFactory {

    private static Isotopes myself = null;

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
        isotopes = new HashMap<String, List<IIsotope>>();
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
        int streamSize = ins.available();
        ReadableByteChannel fcIn = Channels.newChannel(ins);
        ByteBuffer bin = ByteBuffer.allocate(streamSize);
        fcIn.read(bin);
        fcIn.close();
        ins.close();
        bin.position(0);
        int isotopeCount = bin.getInt();
        for (int i = 0; i < isotopeCount; i++) {
            int atomicNum = (int) bin.get();
            int massNum = (int) bin.getShort();
            double exactMass = bin.getDouble();
            double natAbund = bin.get() == 1 ? bin.getDouble() : 0.0;
            IIsotope isotope = new BODRIsotope(PeriodicTable.getSymbol(atomicNum), atomicNum, massNum, exactMass,
                    natAbund);
            add(isotope);
        }
        majorIsotopes = new HashMap<String, IIsotope>();
    }

    /**
     * Gets an array of all isotopes known to the IsotopeFactory for the given
     * element symbol.
     *
     *@param  symbol  An element symbol to search for
     *@return         An array of isotopes that matches the given element symbol
     */
    @Override
    public IIsotope[] getIsotopes(String symbol) {
        if (isotopes.get(symbol) == null) return new IIsotope[0];
        ArrayList<IIsotope> list = new ArrayList<IIsotope>();
        for (IIsotope isotope : isotopes.get(symbol)) {
            if (isotope.getSymbol().equals(symbol)) {
                list.add(isotope);
            }
        }
        return list.toArray(new IIsotope[list.size()]);
    }

    /**
     * Gets a array of all isotopes known to the IsotopeFactory.
     *
     * @return         An array of all isotopes
     */
    @Override
    public IIsotope[] getIsotopes() {
        ArrayList<IIsotope> list = new ArrayList<IIsotope>();
        for (String element : isotopes.keySet()) {
            for (IIsotope isotope : isotopes.get(element)) {
                list.add(isotope);
            }
        }
        return list.toArray(new IIsotope[list.size()]);
    }

    /**
     * Gets an array of all isotopes matching the searched exact mass within
     * a certain difference.
     *
     * @param  exactMass  search mass
     * @param  difference mass the isotope is allowed to differ from the search mass
     * @return            An array of all isotopes
     */
    @Override
    public IIsotope[] getIsotopes(double exactMass, double difference) {
        ArrayList<IIsotope> list = new ArrayList<IIsotope>();
        for (String element : isotopes.keySet()) {
            for (IIsotope isotope : isotopes.get(element)) {
                if (Math.abs(isotope.getExactMass() - exactMass) <= difference) {
                    list.add(isotope);
                }
            }
        }
        return list.toArray(new IIsotope[list.size()]);
    }

    /**
     * Get isotope based on element symbol and mass number.
     *
     * @param symbol the element symbol
     * @param massNumber the mass number
     * @return the corresponding isotope
     */
    @Override
    public IIsotope getIsotope(String symbol, int massNumber) {
        if (isotopes.get(symbol) == null) return null;
        for (IIsotope isotope : isotopes.get(symbol)) {
            if (isotope.getSymbol().equals(symbol) && isotope.getMassNumber() == massNumber) {
                return isotope;
            }
        }
        return null;
    }

    /**
     * Get an isotope based on the element symbol and exact mass.
     *
     * @param symbol    the element symbol
     * @param exactMass the mass number
     * @param tolerance allowed difference from provided exact mass
     * @return the corresponding isotope
     */
    @Override
    public IIsotope getIsotope(String symbol, double exactMass, double tolerance) {
        if (isotopes.get(symbol) == null) return null;
        IIsotope ret = null;
        double minDiff = Double.MAX_VALUE;
        for (IIsotope isotope : isotopes.get(symbol)) {
            double diff = Math.abs(isotope.getExactMass() - exactMass);
            if (isotope.getSymbol().equals(symbol) && diff <= tolerance && diff < minDiff) {
                ret = isotope;
                minDiff = diff;
            }
        }
        return ret;
    }

    /**
     * Returns the most abundant (major) isotope with a given atomic number.
     *
     * <p>The isotope's abundance is for atoms with atomic number 60 and smaller
     * defined as a number that is proportional to the 100 of the most abundant
     * isotope. For atoms with higher atomic numbers, the abundance is defined
     * as a percentage.
     *
     * @param  atomicNumber  The atomicNumber for which an isotope is to be returned
     * @return               The isotope corresponding to the given atomic number
     *
     * @see #getMajorIsotope(String symbol)
     */
    @Override
    public IIsotope getMajorIsotope(int atomicNumber) {
        if (isotopes.get(PeriodicTable.getSymbol(atomicNumber)) == null) return null;
        IIsotope major = null;
        for (IIsotope isotope : isotopes.get(PeriodicTable.getSymbol(atomicNumber))) {
            if (isotope.getAtomicNumber() == atomicNumber) {
                if (major == null || isotope.getNaturalAbundance() > major.getNaturalAbundance()) {
                    major = isotope;
                }
            }
        }
        if (major == null) logger.error("Could not find major isotope for: ", atomicNumber);
        return major;
    }

    /**
     *  Returns the most abundant (major) isotope whose symbol equals element.
     *
     *@param  symbol  the symbol of the element in question
     *@return         The Major Isotope value
     */
    @Override
    public IIsotope getMajorIsotope(String symbol) {
        IIsotope major = null;
        if (majorIsotopes.containsKey(symbol)) {
            major = majorIsotopes.get(symbol);
        } else {
            if (isotopes.get(symbol) == null) {
                logger.error("Could not find major isotope for: ", symbol);
                return null;
            }
            for (IIsotope isotope : isotopes.get(symbol)) {
                if (isotope.getSymbol().equals(symbol)) {
                    if (major == null || isotope.getNaturalAbundance() > major.getNaturalAbundance()) {
                        major = isotope;
                    }
                }
            }
            if (major == null) {
                logger.error("Could not find major isotope for: ", symbol);
            } else {
                majorIsotopes.put(symbol, major);
            }
        }
        return major;
    }

}
