package org.openscience.cdk.config;

import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.silent.Isotope;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static void main(String[] args) throws IOException {
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
