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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.openscience.cdk.interfaces.IElement.*;

/**
 * List of isotopes. Data is taken from the <a href="https://github.com/egonw/bodr">Blue Obelisk Data Repository</a>,
 * <a href="https://github.com/egonw/bodr/releases/tag/BODR-10">version 10</a> {@cdk.cite BODR10}.
 * The data set is described in the first Blue Obelisk paper {@cdk.cite Guha2006}.
 *
 * <p>The <code>isotopes.dat</code> file that is used by this class is a binary class
 * of this data, improving loading times over the BODR XML representation. It is created
 * from the original BODR files using tools from the <code>GenerateIsotopeData</code>
 * in 'cdk-extra' tests.
 *
 * @author Egon Willighagen
 * @author John Mayfield
 */
public final class Isotopes implements IsotopeFactory {

    /* Auto-generated from BODR - see GenerateIsotopeData */
    private static final double[] AVG_MASS = new double[]{
            1.00794, // H
            4.0026, // He
            6.94004, // Li
            9.01218, // Be
            10.81103, // B
            12.01074, // C
            14.0067, // N
            15.9994, // O
            18.9984, // F
            20.18005, // Ne
            22.98977, // Na
            24.30505, // Mg
            26.98154, // Al
            28.08538, // Si
            30.97376, // P
            32.06608, // S
            35.45294, // Cl
            39.94768, // Ar
            39.0983, // K
            40.07802, // Ca
            44.95591, // Sc
            47.86675, // Ti
            50.94147, // V
            51.99613, // Cr
            54.93805, // Mn
            55.84515, // Fe
            58.9332, // Co
            58.69335, // Ni
            63.54564, // Cu
            65.39556, // Zn
            69.72307, // Ga
            72.61276, // Ge
            74.9216, // As
            78.95939, // Se
            79.90353, // Br
            83.79932, // Kr
            85.46766, // Rb
            87.61664, // Sr
            88.90585, // Y
            91.22365, // Zr
            92.90638, // Nb
            95.93129, // Mo
            0, // Tc
            101.06495, // Ru
            102.9055, // Rh
            106.41533, // Pd
            107.86815, // Ag
            112.41155, // Cd
            114.81809, // In
            118.71011, // Sn
            121.75979, // Sb
            127.60313, // Te
            126.90447, // I
            131.29248, // Xe
            132.90545, // Cs
            137.32689, // Ba
            138.90545, // La
            140.11573, // Ce
            140.90765, // Pr
            144.23613, // Nd
            0, // Pm
            150.36635, // Sm
            151.96437, // Eu
            157.25212, // Gd
            158.92535, // Tb
            162.49703, // Dy
            164.93032, // Ho
            167.2563, // Er
            168.93421, // Tm
            173.0377, // Yb
            174.96672, // Lu
            178.48497, // Hf
            180.94788, // Ta
            183.84178, // W
            186.20671, // Re
            190.22486, // Os
            192.21606, // Ir
            195.07781, // Pt
            196.96657, // Au
            200.59917, // Hg
            204.38333, // Tl
            207.21691, // Pb
            208.9804, // Bi
            0, // Po
            0, // At
            0, // Rn
            0, // Fr
            0, // Ra
            0, // Ac
            232.03806, // Th
            231.03588, // Pa
            238.02892 // U
    };

    /* Auto-generated from BODR - see GenerateIsotopeData */
    private static final ImmutableIsotope[] MAJOR_ISOTOPES = new ImmutableIsotope[]{
            new ImmutableIsotope(H, 1, 1.007825032, 99.9885),
            new ImmutableIsotope(He, 4, 4.002603254, 99.999863),
            new ImmutableIsotope(Li, 7, 7.01600455, 92.41),
            new ImmutableIsotope(Be, 9, 9.0121822, 100),
            new ImmutableIsotope(B, 11, 11.0093054, 80.1),
            new ImmutableIsotope(C, 12, 12, 98.93),
            new ImmutableIsotope(N, 14, 14.003074, 99.636),
            new ImmutableIsotope(O, 16, 15.99491462, 99.757),
            new ImmutableIsotope(F, 19, 18.99840322, 100),
            new ImmutableIsotope(Ne, 20, 19.99244018, 90.48),
            new ImmutableIsotope(Na, 23, 22.98976928, 100),
            new ImmutableIsotope(Mg, 24, 23.9850417, 78.99),
            new ImmutableIsotope(Al, 27, 26.98153863, 100),
            new ImmutableIsotope(Si, 28, 27.97692653, 92.2297),
            new ImmutableIsotope(P, 31, 30.97376163, 100),
            new ImmutableIsotope(S, 32, 31.972071, 94.93),
            new ImmutableIsotope(Cl, 35, 34.96885268, 75.76),
            new ImmutableIsotope(Ar, 40, 39.96238312, 99.6003),
            new ImmutableIsotope(K, 39, 38.96370668, 93.2581),
            new ImmutableIsotope(Ca, 40, 39.96259098, 96.941),
            new ImmutableIsotope(Sc, 45, 44.9559119, 100),
            new ImmutableIsotope(Ti, 48, 47.9479463, 73.72),
            new ImmutableIsotope(V, 51, 50.9439595, 99.75),
            new ImmutableIsotope(Cr, 52, 51.9405075, 83.789),
            new ImmutableIsotope(Mn, 55, 54.9380451, 100),
            new ImmutableIsotope(Fe, 56, 55.9349375, 91.754),
            new ImmutableIsotope(Co, 59, 58.933195, 100),
            new ImmutableIsotope(Ni, 58, 57.9353429, 68.0769),
            new ImmutableIsotope(Cu, 63, 62.9295975, 69.17),
            new ImmutableIsotope(Zn, 64, 63.9291422, 48.63),
            new ImmutableIsotope(Ga, 69, 68.9255736, 60.108),
            new ImmutableIsotope(Ge, 74, 73.9211778, 36.28),
            new ImmutableIsotope(As, 75, 74.9215965, 100),
            new ImmutableIsotope(Se, 80, 79.9165213, 49.61),
            new ImmutableIsotope(Br, 79, 78.9183371, 50.69),
            new ImmutableIsotope(Kr, 84, 83.911507, 57),
            new ImmutableIsotope(Rb, 85, 84.91178974, 72.17),
            new ImmutableIsotope(Sr, 88, 87.9056121, 82.58),
            new ImmutableIsotope(Y, 89, 88.9058483, 100),
            new ImmutableIsotope(Zr, 90, 89.9047044, 51.45),
            new ImmutableIsotope(Nb, 93, 92.9063781, 100),
            new ImmutableIsotope(Mo, 98, 97.9054082, 24.13),
            null,
            new ImmutableIsotope(Ru, 102, 101.9043493, 31.55),
            new ImmutableIsotope(Rh, 103, 102.905504, 100),
            new ImmutableIsotope(Pd, 106, 105.903486, 27.33),
            new ImmutableIsotope(Ag, 107, 106.905097, 51.839),
            new ImmutableIsotope(Cd, 114, 113.9033585, 28.73),
            new ImmutableIsotope(In, 115, 114.903878, 95.71),
            new ImmutableIsotope(Sn, 120, 119.9021947, 32.58),
            new ImmutableIsotope(Sb, 121, 120.9038157, 57.21),
            new ImmutableIsotope(Te, 130, 129.9062244, 34.08),
            new ImmutableIsotope(I, 127, 126.904473, 100),
            new ImmutableIsotope(Xe, 132, 131.9041535, 26.89),
            new ImmutableIsotope(Cs, 133, 132.9054519, 100),
            new ImmutableIsotope(Ba, 138, 137.9052472, 71.698),
            new ImmutableIsotope(La, 139, 138.9063533, 99.91),
            new ImmutableIsotope(Ce, 140, 139.9054387, 88.45),
            new ImmutableIsotope(Pr, 141, 140.9076528, 100),
            new ImmutableIsotope(Nd, 142, 141.9077233, 27.2),
            null,
            new ImmutableIsotope(Sm, 152, 151.9197324, 26.75),
            new ImmutableIsotope(Eu, 153, 152.9212303, 52.19),
            new ImmutableIsotope(Gd, 158, 157.9241039, 24.84),
            new ImmutableIsotope(Tb, 159, 158.9253468, 100),
            new ImmutableIsotope(Dy, 164, 163.9291748, 28.18),
            new ImmutableIsotope(Ho, 165, 164.9303221, 100),
            new ImmutableIsotope(Er, 166, 165.9302931, 33.61),
            new ImmutableIsotope(Tm, 169, 168.9342133, 100),
            new ImmutableIsotope(Yb, 174, 173.9388621, 31.83),
            new ImmutableIsotope(Lu, 175, 174.9407718, 97.41),
            new ImmutableIsotope(Hf, 180, 179.94655, 35.08),
            new ImmutableIsotope(Ta, 181, 180.9479958, 99.988),
            new ImmutableIsotope(W, 184, 183.9509312, 30.64),
            new ImmutableIsotope(Re, 187, 186.9557531, 62.6),
            new ImmutableIsotope(Os, 192, 191.9614807, 40.78),
            new ImmutableIsotope(Ir, 193, 192.9629264, 62.7),
            new ImmutableIsotope(Pt, 195, 194.9647911, 33.832),
            new ImmutableIsotope(Au, 197, 196.9665687, 100),
            new ImmutableIsotope(Hg, 202, 201.970643, 29.86),
            new ImmutableIsotope(Tl, 205, 204.9744275, 70.476),
            new ImmutableIsotope(Pb, 208, 207.9766521, 52.4),
            new ImmutableIsotope(Bi, 209, 208.9803987, 100),
            null,
            null,
            null,
            null,
            null,
            null,
            new ImmutableIsotope(Th, 232, 232.0380553, 100),
            new ImmutableIsotope(Pa, 231, 231.035884, 100),
            new ImmutableIsotope(U, 238, 238.0507882, 99.2745)
    };
    /* Auto-generated from BODR - see GenerateIsotopeData */
    private static final ImmutableIsotope[] COMMON_ISOTOPES = new ImmutableIsotope[] {
            new ImmutableIsotope(H,2,2.014101778,0.0115), // 2-H
            new ImmutableIsotope(H,3,3.016049278,0), // 3-H
            new ImmutableIsotope(B,10,10.012937,19.9), // 10-B
            new ImmutableIsotope(C,13,13.00335484,1.07), // 13-C
            new ImmutableIsotope(C,14,14.00324199,0), // 14-C
            new ImmutableIsotope(C,11,11.0114336,0), // 11-C
            new ImmutableIsotope(N,15,15.0001089,0.364), // 15-N
            new ImmutableIsotope(N,16,16.0061017,0), // 16-N
            new ImmutableIsotope(O,18,17.999161,0.205), // 18-O
            new ImmutableIsotope(O,17,16.9991317,0.038), // 17-O
            new ImmutableIsotope(F,18,18.000938,0), // 18-F
            new ImmutableIsotope(P,32,31.97390727,0), // 32-P
            new ImmutableIsotope(S,35,34.96903216,0), // 35-S
            new ImmutableIsotope(Cu,64,63.9297642,0), // 64-Cu
            new ImmutableIsotope(Ga,68,67.9279801,0), // 68-Ga
            new ImmutableIsotope(Br,76,75.924541,0), // 76-Br
            new ImmutableIsotope(Br,77,76.921379,0), // 77-Br
            new ImmutableIsotope(Tc,99,98.9062547,0), // 99-Tc
            new ImmutableIsotope(In,111,110.905103,0), // 111-In
            new ImmutableIsotope(I,125,124.9046302,0), // 125-I
            new ImmutableIsotope(I,123,122.905589,0), // 123-I
            new ImmutableIsotope(I,131,130.9061246,0), // 131-I
            new ImmutableIsotope(I,124,123.9062099,0), // 124-I
            new ImmutableIsotope(Lu,177,176.9437581,0), // 177-Lu
            new ImmutableIsotope(At,221,221.01805,0) // 221-At
    };

    public static double average(int elem) {
        if (elem > 0 && elem <= AVG_MASS.length)
            return AVG_MASS[elem - 1];
        return 0d;
    }

    public static IIsotope major(int elem) {
        if (elem > 0 && elem <= MAJOR_ISOTOPES.length)
            return MAJOR_ISOTOPES[elem - 1];
        return null;
    }

    public static List<IIsotope> get(int elem) {
        return Data.INSTANCE.isotopes(elem);
    }

    public static IIsotope get(int elem, int mass) {
        if (mass == 0)
            return null;
        // is it the major isotope?
        IIsotope major = major(elem);
        if (major != null && major.getMassNumber().equals(mass))
            return major;
        // is it a common isotope?
        for (IIsotope isotope : COMMON_ISOTOPES) {
            if (isotope.getMassNumber() == mass)
                return isotope;
            if (isotope.getAtomicNumber() > elem)
                break;
        }
        // check the full data store
        for (IIsotope isotope : get(elem)) {
            if (isotope.getMassNumber() == mass)
                return isotope;
        }
        return null;
    }


    /**
     * Inner singleton, only loaded when required. There is an associated
     * "istopes.dat"
     */
    private enum Data {
        INSTANCE;

        private static final ILoggingTool logger =
                LoggingToolFactory.createLoggingTool(Isotopes.class);

        private IOException exception = null;
        private short[] offsets = new short[0];
        private List<IIsotope> isotopes = Collections.emptyList();

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
         * @param isotopes  where to store the read isotopes.
         * @param buckets   used to insert the isotopes into sorted (atomic num) position
         * @param buffer    input buffer
         * @param abundance abundance is stored
         */
        private static int loadIsotopeset(IIsotope[] isotopes,
                                          short[] buckets,
                                          ByteBuffer buffer,
                                          boolean abundance) {
            int count = buffer.get() & 0xff;
            int atomicNum = buffer.get() & 0xff;
            int massNum = atomicNum + (buffer.get() & 0xff);
            for (int i = 0; i < count; i++) {
                long fixedMass = buffer.getInt() & 0xffffffffL;
                final ImmutableIsotope isotope;
                if (abundance) {
                    long fixedAbundance = buffer.getInt() & 0xffffffffL;
                    isotope = new ImmutableIsotope(atomicNum, massNum,
                                                   toDecimal(massNum, fixedMass),
                                                   toDecimal(100, fixedAbundance));
                } else {
                    isotope = new ImmutableIsotope(atomicNum,
                                                   massNum,
                                                   toDecimal(massNum, fixedMass), 0);
                }
                isotopes[buckets[atomicNum + 1]++] = isotope;
                massNum++;
            }
            return count;
        }

        /**
         * Convert a value stored as a 4-byte integer (represented as a long here
         * to keep things unsigned) to the decimal value. The scaling depends on
         * the mass number.
         *
         * @param massNum mass number
         * @param v       the value to convert
         * @return the decimal value
         */
        private static double toDecimal(int massNum, long v) {
            // could use BigDecimal
            if (massNum < 4)
                return v / 1e9; // 1 billion
            else if (massNum < 40)
                return v / 1e8; // 100 million
            return v / 1e7; // 10 million
        }

        Data() {
            final String configFile = "data/isotopes.dat";
            IsotopeFactory.logger.debug("Loading " + configFile);
            try (InputStream ins = this.getClass().getResourceAsStream(configFile)) {
                if (ins == null) {
                    exception = new IOException("Isotope data: " + configFile + " was (null)");
                } else {
                    int streamSize = ins.available();
                    try (ReadableByteChannel fcIn = Channels.newChannel(ins)) {
                        ByteBuffer bin = ByteBuffer.allocate(streamSize);
                        fcIn.read(bin);
                        ((Buffer) bin).flip(); // cast-required!
                        short[] buckets = new short[bin.get()];
                        for (int i = 0; i < buckets.length; i++)
                            buckets[i] = bin.getShort();

                        int numIsotopes = bin.getInt();
                        int numNatural = bin.getShort() & 0xffff;
                        int numExtra = numIsotopes - numNatural;
                        IIsotope[] tmp = new IIsotope[numIsotopes];
                        while (numNatural > 0)
                            numNatural -= loadIsotopeset(tmp, buckets, bin, true);
                        while (numExtra > 0)
                            numExtra -= loadIsotopeset(tmp, buckets, bin, false);

                        // important we only set the data at the last step, if
                        // something failed the instance is in a wrong but
                        // non-fatal state
                        offsets = buckets;
                        isotopes = Collections.unmodifiableList(Arrays.asList(tmp));
                    }
                }
            } catch (IOException e) {
                exception = e;
            }
        }

        public List<IIsotope> isotopes(int atomicNum) {
            Data data = Data.INSTANCE;
            if (atomicNum > 0 && atomicNum + 1 < data.offsets.length) {
                List<IIsotope> tmp = data.isotopes.subList(data.offsets[atomicNum],
                                                           data.offsets[atomicNum + 1]);
                return Collections.unmodifiableList(tmp);
            }
            return Collections.emptyList();
        }
    }

    @Override
    public List<IIsotope> isotopes() {
        return Data.INSTANCE.isotopes;
    }

    @Override
    public List<IIsotope> isotopes(int atomicNum) {
       return Data.INSTANCE.isotopes(atomicNum);
    }

    @Override
    public double getNaturalMass(int atomicNum) {
        return average(atomicNum);
    }

    @Override
    public IIsotope getMajorIsotope(int atomicNum) {
        return major(atomicNum);
    }

    /**
     * Returns a singleton instance of this class.
     *
     * @return the singleton
     * @throws IOException when reading of the data file did not work
     */
    public static Isotopes getInstance() throws IOException {
        if (Data.INSTANCE.exception != null)
            throw Data.INSTANCE.exception;
        return new Isotopes();
    }

    private static boolean isMajor(IIsotope atom) {
        Integer mass = atom.getMassNumber();
        if (mass == null)
            return false;
        IIsotope major = major(atom.getAtomicNumber());
        if (major == null)
            return false; // no major isotope
        return major.getMassNumber().equals(mass);
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
