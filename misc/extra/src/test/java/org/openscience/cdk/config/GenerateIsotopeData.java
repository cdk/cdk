/* Copyright (C) 2026 John Mayfield
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
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class GenerateIsotopeData {



    private static final BigDecimal TEN_MILLION = new BigDecimal(10000000);
    private static final BigDecimal ONE_HUNDRED_MILLION = new BigDecimal(100000000);
    private static final BigDecimal ONE_BILLION = new BigDecimal(1000000000);

    public static long toFixed(int scale, BigDecimal value) {
        if (scale < 4)
            return value.setScale(10, RoundingMode.HALF_UP)
                        .multiply(ONE_BILLION)
                        .stripTrailingZeros()
                        .longValue();
        else if (scale < 40)
            return value.setScale(10, RoundingMode.HALF_UP)
                        .multiply(ONE_HUNDRED_MILLION)
                        .stripTrailingZeros()
                        .longValue();
        else
            return value.setScale(10, RoundingMode.HALF_UP)
                        .multiply(TEN_MILLION)
                        .stripTrailingZeros()
                        .longValue();
    }

    public static BigDecimal toBigDecimal(int scale, long value) {
        BigDecimal tmp = new BigDecimal((value & 0xffffffffL)).setScale(10, RoundingMode.HALF_UP);
        if (scale < 4)
            return tmp.divide(ONE_BILLION, RoundingMode.HALF_UP)
                      .stripTrailingZeros();
        else if (scale < 40)
            return tmp.divide(ONE_HUNDRED_MILLION, RoundingMode.HALF_UP)
                      .stripTrailingZeros();
        else
            return tmp.divide(TEN_MILLION, RoundingMode.HALF_UP)
                      .stripTrailingZeros();
    }

    private static List<List<IIsotope>> getRuns(List<IIsotope> isotopes) {
        // should already be sorted but ensure we are
        isotopes.sort(Comparator.comparingInt(IIsotope::getMassNumber));
        List<List<IIsotope>> runs = new ArrayList<>();
        List<IIsotope> run = new ArrayList<>();
        for (IIsotope isotope : isotopes) {
            if (!run.isEmpty() &&
                run.get(run.size() - 1).getMassNumber() != isotope.getMassNumber() - 1) {
                runs.add(run);
                run = new ArrayList<>();
            }
            run.add(isotope);
        }
        if (!run.isEmpty())
            runs.add(run);
        return runs;
    }

    public static BigDecimal getAvgMass(List<IIsotope> isotopes) {
        BigDecimal summedAbundances = BigDecimal.ZERO;
        BigDecimal summedWeightedAbundances = BigDecimal.ZERO;
        BigDecimal getNaturalMass = BigDecimal.ZERO;
        for (IIsotope isotope : isotopes) {
            BigDecimal abundance = isotope.getProperty(XMLIsotopeFactory.NaturalAbundance);
            BigDecimal mass = isotope.getProperty(XMLIsotopeFactory.ExactMass);
            summedAbundances = summedAbundances.add(abundance);
            summedWeightedAbundances = summedWeightedAbundances.add(abundance.multiply(mass));
            getNaturalMass = summedWeightedAbundances.divide(summedAbundances, RoundingMode.HALF_UP);
        }
        return getNaturalMass;
    }

    public static void main(String[] args) throws IOException {

        // Common Isotopes (counts computed from PubChem Compound ~ 2023)
        List<IAtom> commonIsotopes = Arrays.asList(
                new Atom("D"),	 // 3170299
                new Atom("T"),   // 111355
                new Atom("13C"), // 65899
                new Atom("18F"), // 15915
                new Atom("15N"), // 12169
                new Atom("14C"), // 9989
                new Atom("11C"), // 3785
                new Atom("18O"), // 2781
                new Atom("99Tc"), // 2177
                new Atom("125I"), // 2151
                new Atom("10B"), // 1959
                new Atom("123I"), // 1775
                new Atom("131I"), // 1101
                new Atom("11B"), // 738
                new Atom("1H"), // 655
                new Atom("12C"), // 631
                new Atom("19F"), // 627
                new Atom("124I"), // 523
                new Atom("64Cu"), // 444
                new Atom("17O"), // 419
                new Atom("76Br"), // 412
                new Atom("16O"), //  355
                new Atom("68Ga"), // 278
                new Atom("221At"), // 278
                new Atom("111In"), // 228
                new Atom("35S"), // 197
                new Atom("32P"), // 180
                new Atom("14N"), // 175
                new Atom("177Lu"), // 146
                new Atom("77Br"), // 118
                new Atom("16N") //	104
        );

        for (IAtom atom : commonIsotopes) {
            System.err.println(atom);
        }

        String path = "base/core/src/main/resources/org/openscience/cdk/config/data/isotopes.dat";
        IsotopeFactory fac = XMLIsotopeFactory.getInstance(SilentChemObjectBuilder.getInstance());
        List<IIsotope> allIsotopes = new ArrayList<>(fac.isotopes());
        ByteBuffer bout = ByteBuffer.allocate(50000); // presumes file is < 50kB
        allIsotopes.sort(Comparator.comparingInt((IIsotope o) -> o.getAtomicNumber())
                                   .thenComparingInt(IElement::getAtomicNumber));

        int numNatural = 0;
        Map<Integer,List<IIsotope>> naturalIsotopes = new TreeMap<>();
        Map<Integer,List<IIsotope>> extraIsotopes   = new TreeMap<>();

        for (IIsotope isotope : allIsotopes) {
            if (isotope.getProperty(XMLIsotopeFactory.NaturalAbundance) != null) {
                naturalIsotopes.computeIfAbsent(isotope.getAtomicNumber(), k -> new ArrayList<>())
                               .add(isotope);
                numNatural++;
            } else {
                extraIsotopes.computeIfAbsent(isotope.getAtomicNumber(), k -> new ArrayList<>())
                               .add(isotope);
            }
        }

        StringBuilder sb = generateArrays(fac,
                                          naturalIsotopes,
                                          commonIsotopes);

        System.out.println(sb);

        // the offset table allows quick lookup of elements, and sort the data
        // as it is read
        Integer maxAtomicNum = allIsotopes.get(allIsotopes.size() - 1).getAtomicNumber();
        short[] offsets = new short[maxAtomicNum+2];
        Integer prev = 0;
        for (int i = 0; i < allIsotopes.size(); i++) {
            IIsotope isotope = allIsotopes.get(i);
            if (!isotope.getAtomicNumber().equals(prev))
                offsets[prev+2] = (short)i;
            prev = isotope.getAtomicNumber();
        }

        bout.put((byte)offsets.length);
        for (int offset : offsets) {
            bout.putShort((short) offset);
        }

        bout.putInt(allIsotopes.size());
        /* We write all the natural isotopes (have an abundance) */
        bout.putShort((short)numNatural);
        for (Map.Entry<Integer,List<IIsotope>> e : naturalIsotopes.entrySet()) {
            Integer atomicNum = e.getKey();
            List<IIsotope> isotopes = e.getValue();
            List<List<IIsotope>> runs = getRuns(isotopes);
            for (List<IIsotope> run : runs) {
                bout.put((byte)run.size());
                bout.put(atomicNum.byteValue());
                int massNum = run.get(0).getMassNumber();
                bout.put((byte)(massNum - atomicNum));
                for (IIsotope isotope : run) {
                    putIsotope(isotope, bout);
                }
            }
        }

        /* We write all the extra isotopes (no abundance) */
        for (Map.Entry<Integer,List<IIsotope>> e : extraIsotopes.entrySet()) {
            Integer atomicNum = e.getKey();
            List<IIsotope> isotopes = e.getValue();
            List<List<IIsotope>> runs = getRuns(isotopes);
            for (List<IIsotope> run : runs) {
                bout.put((byte)run.size());
                bout.put(atomicNum.byteValue());
                int massNum = run.get(0).getMassNumber();
                bout.put((byte)(massNum - atomicNum));
                for (IIsotope isotope : run) {
                    putIsotope(isotope, bout);
                }
            }
        }



        System.err.println(bout.position() + " bytes");
        bout.limit(bout.position()).position(0);
        try (FileOutputStream out = new FileOutputStream(path);
             FileChannel fc = out.getChannel()) {
            fc.write(bout);
        }
    }

    private static StringBuilder generateArrays(IsotopeFactory factory,
                                                Map<Integer, List<IIsotope>> naturalIsotopes,
                                                List<IAtom> commonIsotopes) {
        int maxNat = 0;
        for (Integer elem : naturalIsotopes.keySet())
            maxNat = Math.max(elem, maxNat);
        /* Generate Avg Natural Mass Constants */
        String[] avgMass = new String[maxNat];
        Arrays.fill(avgMass, "0");
        for (Map.Entry<Integer,List<IIsotope>> e : naturalIsotopes.entrySet()) {
            avgMass[e.getKey()-1] = getAvgMass(e.getValue()).setScale(5, RoundingMode.HALF_UP).stripTrailingZeros().toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("/* Auto-generated from BODR - see GenerateIsotopeData */\n");
        sb.append("private static final double[] AVG_MASS = new double[]{\n");
        for (int i = 0; i < avgMass.length; i++) {
            String mass = avgMass[i];
            sb.append("    ").append(mass);
            if (i+1 < avgMass.length) sb.append(',');
            sb.append(" // ").append(Elements.ofNumber(i+1).symbol() + "\n");
        }
        sb.append("};\n");

        /* Generate Major Isotopes */
        String[] majorIsotopes = new String[maxNat];
        for (Map.Entry<Integer,List<IIsotope>> e : naturalIsotopes.entrySet()) {
            List<IIsotope> tmp = new ArrayList<>(e.getValue());
            tmp.sort(Comparator.comparingDouble(IIsotope::getNaturalAbundance));
            IIsotope major = tmp.get(tmp.size()-1);
            BigDecimal exactMass = major.getProperty(XMLIsotopeFactory.ExactMass);
            BigDecimal abundance = major.getProperty(XMLIsotopeFactory.NaturalAbundance);
            majorIsotopes[e.getKey() - 1] = String.format("new ImmutableIsotope(%s,%d,%s,%s)",
                                                          major.getSymbol(),
                                                          major.getMassNumber(),
                                                          exactMass.stripTrailingZeros().toPlainString(),
                                                          abundance.stripTrailingZeros().toPlainString());
        }
        sb.append('\n');
        sb.append("/* Auto-generated from BODR - see GenerateIsotopeData */\n");
        sb.append("private static final ImmutableIsotope[] MAJOR_ISOTOPES = new ImmutableIsotope[] {\n");
        for (String major : majorIsotopes) {
            sb.append("    ").append(major).append(",\n");
        }
        sb.setLength(sb.length()-2);
        sb.append('\n');
        sb.append("};\n");
        sb.append("/* Auto-generated from BODR - see GenerateIsotopeData */\n");
        sb.append("private static final ImmutableIsotope[] COMMON_ISOTOPES = new ImmutableIsotope[] {\n");
        commonIsotopes.sort((o1, o2) -> Integer.compare(o1.getAtomicNumber(), o2.getAtomicNumber()));
        for (int i = 0; i < commonIsotopes.size(); i++) {
            IAtom common = commonIsotopes.get(i);
            IIsotope major = factory.getMajorIsotope(common.getAtomicNumber());
            if (major == null || !Objects.equals(major.getMassNumber(), common.getMassNumber())) {
                IIsotope isotope = factory.getIsotope(common.getSymbol(), common.getMassNumber());
                BigDecimal exactMass = isotope.getProperty(XMLIsotopeFactory.ExactMass);
                BigDecimal abundance = isotope.getProperty(XMLIsotopeFactory.NaturalAbundance);
                sb.append("    ").append(String.format("new ImmutableIsotope(%s,%d,%s,%s)",
                                                       isotope.getSymbol(),
                                                       isotope.getMassNumber(),
                                                       exactMass.stripTrailingZeros().toPlainString(),
                                                       abundance != null ? abundance.stripTrailingZeros().toPlainString() : 0));
                if (i+1 < commonIsotopes.size())
                    sb.append(',');
                sb.append(" // ").append(common.getMassNumber()).append("-").append(common.getSymbol());
                sb.append('\n');
            }
        }
        sb.append("};\n");

        return sb;
    }

    private static void putIsotope(IIsotope isotope, ByteBuffer bout) {
        BigDecimal mass = isotope.getProperty(XMLIsotopeFactory.ExactMass);
        mass = mass.stripTrailingZeros();

        long fixedMass = toFixed(isotope.getMassNumber(), mass);
        bout.putInt((int) (fixedMass & 0xffffffffL));

        BigDecimal massIn = toBigDecimal(isotope.getMassNumber(), fixedMass);
        if (!massIn.equals(mass))
            System.err.println("Warning: " + displayIsotope(isotope) + " mass " + reportDiff(massIn, mass));

        BigDecimal abundance = isotope.getProperty(XMLIsotopeFactory.NaturalAbundance);
        if (abundance != null) {
            abundance = abundance.stripTrailingZeros();
            long fixedAbundance = toFixed(100, abundance);
            bout.putInt((int) (fixedAbundance & 0xffffffffL));
            BigDecimal abundanceIn = toBigDecimal(100, fixedAbundance);
            if (!abundanceIn.equals(abundance))
                System.err.println("Warning: " + displayIsotope(isotope) + " abundance " + reportDiff(abundanceIn, abundance));
        }
    }

    private static String displayIsotope(IIsotope isotope) {
        return isotope.getMassNumber() + "-" + isotope.getSymbol();
    }

    private static String reportDiff(BigDecimal massIn, BigDecimal mass) {
        if (mass.toString().startsWith(massIn.toString())) {
            return "loss of precision " + massIn + "(" + mass.toString().substring(massIn.toString().length()) + ")";
        } else {
            return "loss of precision " + massIn + " (stored) != " + mass + " (actual) diff is " + massIn.subtract(mass).abs();
        }
    }
}
