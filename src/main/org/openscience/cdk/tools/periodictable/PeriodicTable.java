/* $Revision: 9167 $ $Author: rajarshi $ $Date: 2007-10-22 01:26:11 +0200 (Mon, 22 Oct 2007) $
 *
 * Copyright (C) 2008  Rajarshi Guha <rajarshi@users.sf.net>
 *               2011  Jonathan Alvarsson <jonalv@users.sf.net>
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
package org.openscience.cdk.tools.periodictable;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

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
 * of {@link org.openscience.cdk.tools.periodictable.PeriodicTableElement}, this utility
 * class is useful when one wants generic properties of elements such
 * as atomic number, VdW radius etc.
 *
 * @author      Rajarshi Guha
 * @cdk.created 2008-06-12
 * @cdk.keyword element
 * @cdk.keyword periodic table
 * @cdk.keyword radius, vanderwaals 
 * @cdk.keyword electronegativity
 * @cdk.module  core
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.tools.periodictable.PeriodicTableTest")
public class PeriodicTable {
    
    private static volatile boolean isInitialized = false;
    private static volatile Map<String, PeriodicTableElement> elements;
    private static volatile Map<Integer, PeriodicTableElement> elementsByNumber;

    private synchronized static void initialize() {
        if (isInitialized) return;

        ElementPTFactory factory;
        try {
            factory = ElementPTFactory.getInstance();
        } catch (IOException e) {
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

    /** 
     * Get the Van der Waals radius for the element in question.
     *
     * @param symbol The symbol of the element
     * @return the van der waals radius
     */
    @TestMethod("testTable")
    public static Double getVdwRadius(String symbol) {
        initialize();
        PeriodicTableElement element = elements.get(symbol);
        if (element == null) return null;
        else return element.getVdwRadius();
    }

    /**
     * Get the covalent radius for an element.
     *
     * @param symbol the symbol of the element
     * @return the covalent radius
     */
    @TestMethod("testTable")
    public static Double getCovalentRadius(String symbol) {
        initialize();
        PeriodicTableElement element = elements.get(symbol);
        if (element == null) return null;
        else return element.getCovalentRadius();
    }

    /**
     * Get the CAS ID for an element.
     *
     * @param symbol the symbol of the element
     * @return the CAS ID
     */
    @TestMethod("testTable")
    public static String getCASId(String symbol) {
        initialize();
        PeriodicTableElement element = elements.get(symbol);
        if (element == null) return null;
        else return element.getCASid();
    }

    /**
     * Get the chemical series for an element.
     *
     * @param symbol the symbol of the element
     * @return the chemical series of the element
     */
    @TestMethod("testTable")
    public static String getChemicalSeries(String symbol) {
        initialize();
        PeriodicTableElement element = elements.get(symbol);
        if (element == null) return null;
        else return element.getChemicalSerie();
    }

    /**
     * Get the group of the element.
     * @param symbol the symbol of the element
     * @return the group
     */
    @TestMethod("testTable")
    public static Integer getGroup(String symbol) {
        initialize();
        PeriodicTableElement element = elements.get(symbol);
        if (element == null) return null;
        else return element.getGroup();
    }

    /**
     * Get the name of the element.
     *
     * @param symbol the symbol of the element
     * @return the name of the element
     */
    @TestMethod("testTable")
    public static String getName(String symbol) {
        initialize();
        PeriodicTableElement element = elements.get(symbol);
        if (element == null) return null;
        else return element.getName();
    }

    /**
     * Get the period of the element.
     *
     * @param symbol the symbol of the element
     * @return the period
     */
    @TestMethod("testTable")
    public static Integer getPeriod(String symbol) {
        initialize();
        PeriodicTableElement element = elements.get(symbol);
        if (element == null) return null;
        else return element.getPeriod();
    }

    /**
     * Get the phase of the element.
     *
     * @param symbol the symbol of the element
     * @return the phase of the element
     */
    @TestMethod("testTable")
    public static String getPhase(String symbol) {
        initialize();
        PeriodicTableElement element = elements.get(symbol);
        if (element == null) return null;
        else return element.getPhase();
    }

    /**
     * Get the atomic number of the element.
     *
     * @param symbol the symbol of the element
     * @return the atomic number
     */
    @TestMethod("testTable")
    public static Integer getAtomicNumber(String symbol) {
        initialize();
        PeriodicTableElement element = elements.get(symbol);
        if (element == null) return null;
        else return element.getAtomicNumber();
    }

    /**
     * Get the Pauling electronegativity of an element.
     *
     * @param symbol the symbol of the element
     * @return the Pauling electronegativity
     */
    @TestMethod("testTable")
    public static Double getPaulingElectronegativity(String symbol) {
        initialize();
        PeriodicTableElement element = elements.get(symbol);
        if (element == null) return null;
        else return element.getPaulingEneg();
    }

    /*
     * Get the symbol for the specified atomic number.
     *
     * @param atomicNumber the atomic number of the element
     * @return the corresponding symbol
     */
    @TestMethod("testTable")
    public static String getSymbol(int atomicNumber) {
        initialize();
        PeriodicTableElement element = elementsByNumber.get(atomicNumber);
        if (element == null) return null;
        else return element.getSymbol();
    }

    /**
     * Return the number of elements currently considered in the periodic table.
     *
     * @return the number of elements in the periodic table
     */
    @TestMethod("testTable")
    public static int getElementCount() {
        initialize();
        return elements.size();
    }
                        
}
