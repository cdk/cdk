/**
 * Created by IntelliJ IDEA.
 * User: rguha
 * Date: Jun 12, 2008
 * Time: 1:51:33 PM
 * To change this template use File | Settings | File Templates.
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.PeriodicTableElement;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.ElementPTFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;

/**
 * Represents elements of the Periodic Table.
 * <p/>
 * Though individual elements can be obtained from instances
 * of {@link org.openscience.cdk.PeriodicTableElement}, this utility
 * class is useful when one wants generic properties of elements such
 * as atomic number, VdW radius etc.
 *
 * @author Rajarshi Guha
 * @cdk.created June 12 2008
 * @cdk.keyword element
 * @cdk.keyword periodic table, vanderwaals, radius, electronegativity
 * @cdk.module extra
 * @cdk.svnrev $Revision: 10123 $
 */

@TestClass("org.openscience.cdk.tools.PeriodicTableTest")
public class PeriodicTable {
    boolean isInitialized = false;

    private static PeriodicTable ourInstance = new PeriodicTable();
    private static HashMap<String, PeriodicTableElement> elements;
    private HashMap<Integer, PeriodicTableElement> elementsByNumber;

    private PeriodicTable() {
        if (isInitialized) return;

        ElementPTFactory factory;
        try {
            factory = ElementPTFactory.getInstance();
        } catch (IOException e) {
            elements = null;
            return;
        } catch (ClassNotFoundException e) {
            elements = null;
            return;
        }

        elements = new HashMap<String, PeriodicTableElement>();
        elementsByNumber = new HashMap<Integer, PeriodicTableElement>();
        Vector<PeriodicTableElement> tmp = factory.getElements();
        for (PeriodicTableElement element : tmp) {
            elements.put(element.getSymbol(), element);
            elementsByNumber.put(element.getAtomicNumber(), element);
        }

        try {
            readVDW();
            readPEneg();
        } catch (IOException e) {
            return;
        }

        isInitialized = true;
    }

    private void readVDW() throws IOException {
        // now read in the VdW radii
        String filename = "org/openscience/cdk/config/data/radii-vdw.txt";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));

        for (int i = 0; i < 6; i++) reader.readLine();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] toks = line.split("\\s");
            int atnum = Integer.parseInt(toks[0]);
            double vdw = Double.parseDouble(toks[1]);
            PeriodicTableElement e = elementsByNumber.get(atnum);
            if (e != null) {
                String symbol = e.getSymbol();
                if (vdw == 2) elements.get(symbol).setVdwRadius((Double) CDKConstants.UNSET);
                else elements.get(symbol).setVdwRadius(vdw);
            }
        }
    }

    private void readPEneg() throws IOException {
        // now read in the VdW radii
        String filename = "org/openscience/cdk/config/data/electroneg-pauling.txt";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));

        for (int i = 0; i < 6; i++) reader.readLine();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] toks = line.split("\\s");
            int atnum = Integer.parseInt(toks[0]);
            double eneg = Double.parseDouble(toks[1]);
            PeriodicTableElement e = elementsByNumber.get(atnum);
            if (e != null) {
                String symbol = e.getSymbol();
                elements.get(symbol).setPaulingEneg(eneg);
            }
        }
    }

    @TestMethod("testTable")
    public static Double getVdwRadius(String symbol) {
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getVdwRadius();
    }

    @TestMethod("testTable")
    public static String getCASId(String symbol) {
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getCASid();
    }

    @TestMethod("testTable")
    public static String getChemicalSeries(String symbol) {
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getChemicalSerie();
    }

    @TestMethod("testTable")
    public static String getGroup(String symbol) {
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getGroup();
    }

    @TestMethod("testTable")
    public static String getName(String symbol) {
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getName();
    }

    @TestMethod("testTable")
    public static String getPeriod(String symbol) {
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getPeriod();
    }

    @TestMethod("testTable")
    public static String getPhase(String symbol) {
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getPhase();
    }

    @TestMethod("testTable")
    public static Integer getAtomicNumber(String symbol) {
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getAtomicNumber();
    }

    @TestMethod("testTable")
    public static Double getPaulingElectronegativity(String symbol) {
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getPaulingEneg();
    }
}
