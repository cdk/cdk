/* $Revision: 9167 $ $Author: rajarshi $ $Date: 2007-10-22 01:26:11 +0200 (Mon, 22 Oct 2007) $
 *
 * Copyright (C) 2008  Rajarshi Guha <rajarshi@users.sf.net>
 *
 * Contact: cdk-devel@lists.sf.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
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
import java.util.List;
import java.util.Map;

/**
 * Represents elements of the Periodic Table.
 * <p/>
 * Though individual elements can be obtained from instances
 * of {@link org.openscience.cdk.PeriodicTableElement}, this utility
 * class is useful when one wants generic properties of elements such
 * as atomic number, VdW radius etc.
 *
 * @author      Rajarshi Guha
 * @cdk.created 2008-06-12
 * @cdk.keyword element
 * @cdk.keyword periodic table
 * @cdk.keyword radius, vanderwaals 
 * @cdk.keyword electronegativity
 * @cdk.module  extra
 * @cdk.svnrev  $Revision: 10123 $
 */
@TestClass("org.openscience.cdk.tools.PeriodicTableTest")
public class PeriodicTable {
    
    private static boolean isInitialized = false;
    private static Map<String, PeriodicTableElement> elements;
    private static Map<Integer, PeriodicTableElement> elementsByNumber;

    private static void initialize() {
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
        List<PeriodicTableElement> tmp = factory.getElements();
        for (PeriodicTableElement element : tmp) {
            elements.put(element.getSymbol(), element);
            elementsByNumber.put(element.getAtomicNumber(), element);
        }

        try {
            readVDW();
            readCovalent();
            readPEneg();
        } catch (IOException e) {
            return;
        }

        isInitialized = true;
    }

    private static void readVDW() throws IOException {
        // now read in the VdW radii
        String filename = "org/openscience/cdk/config/data/radii-vdw.txt";
        InputStream ins = PeriodicTable.class.getClassLoader().getResourceAsStream(filename);
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

     private static void readCovalent() throws IOException {
        // now read in the covalent radi
        String filename = "org/openscience/cdk/config/data/radii-covalent.txt";
        InputStream ins = PeriodicTable.class.getClassLoader().getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));

        for (int i = 0; i < 5; i++) reader.readLine();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] toks = line.split("\\s");
            int atnum = Integer.parseInt(toks[0]);
            double vdw = Double.parseDouble(toks[1]);
            PeriodicTableElement e = elementsByNumber.get(atnum);
            if (e != null) {
                String symbol = e.getSymbol();
                elements.get(symbol).setCovalentRadius(vdw);
            }
        }
    }

    private static void readPEneg() throws IOException {
        // now read in the VdW radii
        String filename = "org/openscience/cdk/config/data/electroneg-pauling.txt";
        InputStream ins = PeriodicTable.class.getClassLoader().getResourceAsStream(filename);
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
        initialize();
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getVdwRadius();
    }

    @TestMethod("testTable")
    public static Double getCovalentRadius(String symbol) {
        initialize();
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getCovalentRadius();
    }

    @TestMethod("testTable")
    public static String getCASId(String symbol) {
        initialize();
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getCASid();
    }

    @TestMethod("testTable")
    public static String getChemicalSeries(String symbol) {
        initialize();
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getChemicalSerie();
    }

    @TestMethod("testTable")
    public static String getGroup(String symbol) {
        initialize();
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getGroup();
    }

    @TestMethod("testTable")
    public static String getName(String symbol) {
        initialize();
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getName();
    }

    @TestMethod("testTable")
    public static String getPeriod(String symbol) {
        initialize();
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getPeriod();
    }

    @TestMethod("testTable")
    public static String getPhase(String symbol) {
        initialize();
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getPhase();
    }

    @TestMethod("testTable")
    public static Integer getAtomicNumber(String symbol) {
        initialize();
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getAtomicNumber();
    }

    @TestMethod("testTable")
    public static Double getPaulingElectronegativity(String symbol) {
        initialize();
        PeriodicTableElement e = elements.get(symbol);
        if (e == null) return null;
        else return e.getPaulingEneg();
    }
}
