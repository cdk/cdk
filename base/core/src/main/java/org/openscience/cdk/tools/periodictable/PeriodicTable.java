/* Copyright (C) 2008  Rajarshi Guha <rajarshi@users.sf.net>
 *               2011  Jonathan Alvarsson <jonalv@users.sf.net>
 *               2014  Mark B Vine (orcid:0000-0002-7794-0426)
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

import org.openscience.cdk.config.Elements;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static org.openscience.cdk.config.Elements.*;

/**
 * Represents elements of the Periodic Table.  This utility class was
 * previously useful when one wants generic properties of elements such as
 * atomic number, VdW radius etc. The new approach to this is to use the
 * {@link Elements} enumeration.
 *
 * @author Rajarshi Guha
 * @cdk.created 2008-06-12
 * @cdk.keyword element
 * @cdk.keyword periodic table
 * @cdk.keyword radius, vanderwaals
 * @cdk.keyword electronegativity
 * @cdk.module core
 * @cdk.githash
 */
public final class PeriodicTable {

    /** CAS ID Mapping. */
    private static volatile Map<Elements, String> ids;

    /** A lock used for locking CAD ID initialisation. */
    private final static Object                   LOCK = new Object();

    /**
     * Get the Van der Waals radius for the element in question.
     *
     * @param symbol The symbol of the element
     * @return the Van der waals radius
     */
    public static Double getVdwRadius(String symbol) {
        return Elements.ofString(symbol).vdwRadius();
    }

    /**
     * Get the covalent radius for an element.
     *
     * @param symbol the symbol of the element
     * @return the covalent radius
     */
    public static Double getCovalentRadius(String symbol) {
        return Elements.ofString(symbol).covalentRadius();
    }

    /**
     * Get the CAS ID for an element.
     *
     * @param symbol the symbol of the element
     * @return the CAS ID
     */
    public static String getCASId(String symbol) {
        return casIds().get(Elements.ofString(symbol));
    }

    /**
     * Get the chemical series for an element.
     *
     * @param symbol the symbol of the element
     * @return the chemical series of the element
     */
    public static String getChemicalSeries(String symbol) {
        Elements e = Elements.ofString(symbol);
        for (Series s : Series.values())
            if (s.contains(e)) return s.name();
        return null;
    }

    /**
     * Get the group of the element.
     *
     * @param symbol the symbol of the element
     * @return the group
     */
    public static Integer getGroup(String symbol) {
        return Elements.ofString(symbol).group();
    }

    /**
     * Get the name of the element.
     *
     * @param symbol the symbol of the element
     * @return the name of the element
     */
    public static String getName(String symbol) {
        return Elements.ofString(symbol).name();
    }

    /**
     * Get the period of the element.
     *
     * @param symbol the symbol of the element
     * @return the period
     */
    public static Integer getPeriod(String symbol) {
        return Elements.ofString(symbol).period();
    }

    /**
     * Get the phase of the element.
     *
     * @param symbol the symbol of the element
     * @return the phase of the element
     */
    public static String getPhase(String symbol) {
        Elements e = Elements.ofString(symbol);
        for (Phase p : Phase.values())
            if (p.contains(e)) return p.name();
        return null;
    }

    /**
     * Get the atomic number of the element.
     *
     * @param symbol the symbol of the element
     * @return the atomic number
     */
    public static Integer getAtomicNumber(String symbol) {
        return Elements.ofString(symbol).number();
    }

    /**
     * Get the Pauling electronegativity of an element.
     *
     * @param symbol the symbol of the element
     * @return the Pauling electronegativity
     */
    public static Double getPaulingElectronegativity(String symbol) {
        return Elements.ofString(symbol).electronegativity();
    }

    /**
     * Get the symbol for the specified atomic number.
     *
     * @param atomicNumber the atomic number of the element
     * @return the corresponding symbol
     */
    public static String getSymbol(int atomicNumber) {
        return Elements.ofNumber(atomicNumber).symbol();
    }

    /**
     * Return the number of elements currently considered in the periodic
     * table.
     *
     * @return the number of elements in the periodic table
     */
    public static int getElementCount() {
        return Elements.values().length;
    }

    /**
     * Enumeration of chemical series and the elements which are members of each
     * series.
     */
    private enum Series {
        NonMetals(Sulfur, Selenium, Oxygen, Carbon, Phosphorus, Hydrogen, Nitrogen), NobleGasses(Helium, Krypton,
                Xenon, Argon, Radon, Neon), AlkaliMetals(Sodium, Rubidium, Potassium, Caesium, Francium, Lithium), AlkaliEarthMetals(
                Strontium, Radium, Calcium, Magnesium, Barium, Beryllium), Metalloids(Silicon, Arsenic, Tellurium,
                Germanium, Antimony, Polonium, Boron), Halogens(Fluorine, Iodine, Chlorine, Astatine, Bromine), Metals(
                Gallium, Indium, Aluminium, Thallium, Tin, Lead, Bismuth), TransitionMetals(Seaborgium, Hafnium,
                Roentgenium, Iridium, Nickel, Meitnerium, Yttrium, Copper, Rutherfordium, Tungsten, Copernicium,
                Rhodium, Cobalt, Zinc, Platinum, Gold, Cadmium, Manganese, Darmstadtium, Dubnium, Palladium, Vanadium,
                Titanium, Tantalum, Chromium, Molybdenum, Ruthenium, Zirconium, Osmium, Bohrium, Rhenium, Niobium,
                Scandium, Technetium, Hassium, Mercury, Iron, Silver), Lanthanides(Terbium, Samarium, Lutetium,
                Neodymium, Cerium, Europium, Gadolinium, Thulium, Lanthanum, Erbium, Promethium, Holmium, Praseodymium,
                Dysprosium, Ytterbium), Actinides(Fermium, Protactinium, Plutonium, Thorium, Lawrencium, Einsteinium,
                Nobelium, Actinium, Americium, Curium, Berkelium, Mendelevium, Uranium, Californium, Neptunium);

        private final Set<Elements> elements;

        Series(Elements first, Elements... rest) {
            this.elements = EnumSet.of(first, rest);
        }

        boolean contains(Elements e) {
            return elements.contains(e);
        }
    }

    /**
     * Enumeration of matter phases and the chemical elements which are present
     * in each phase.
     */
    private enum Phase {

        Solid(Sulfur, Hafnium, Terbium, Calcium, Gadolinium, Nickel, Cerium, Germanium, Phosphorus, Copper, Polonium,
                Lead, Gold, Iodine, Cadmium, Ytterbium, Manganese, Lithium, Palladium, Vanadium, Chromium, Molybdenum,
                Potassium, Ruthenium, Osmium, Boron, Bismuth, Rhenium, Holmium, Niobium, Praseodymium, Barium,
                Antimony, Thallium, Iron, Silver, Silicon, Caesium, Astatine, Iridium, Francium, Lutetium, Yttrium,
                Rubidium, Lanthanum, Tungsten, Erbium, Selenium, Gallium, Carbon, Rhodium, Uranium, Dysprosium, Cobalt,
                Zinc, Platinum, Protactinium, Titanium, Arsenic, Tantalum, Thorium, Samarium, Europium, Neodymium,
                Zirconium, Radium, Thulium, Sodium, Scandium, Tellurium, Indium, Beryllium, Aluminium, Strontium, Tin,
                Magnesium),

        Liquid(Bromine, Mercury),

        Gas(Fluorine, Oxygen, Xenon, Argon, Chlorine, Helium, Krypton, Hydrogen, Radon, Nitrogen, Neon),

        Synthetic(Fermium, Seaborgium, Plutonium, Roentgenium, Lawrencium, Meitnerium, Einsteinium, Nobelium, Actinium,
                Rutherfordium, Americium, Curium, Bohrium, Berkelium, Promethium, Copernicium, Technetium, Hassium,
                Californium, Mendelevium, Neptunium, Darmstadtium, Dubnium);

        private final Set<Elements> elements;

        Phase(Elements first, Elements... rest) {
            this.elements = EnumSet.of(first, rest);
        }

        boolean contains(Elements e) {
            return elements.contains(e);
        }
    }

    /**
     * Lazily obtain the CAS ID Mapping.
     *
     * @return CAS id mapping
     */
    private static Map<Elements, String> casIds() {
        Map<Elements, String> result = ids;
        if (result == null) {
            synchronized (LOCK) {
                result = ids;
                if (result == null) {
                    ids = result = initCasIds();
                }
            }
        }
        return result;
    }

    /** Obtain the CAS ID Mapping. */
    private static Map<Elements, String> initCasIds() {
        Map<Elements, String> ids = new EnumMap<Elements, String>(Elements.class);
        ids.put(Unknown, "");
        ids.put(Hydrogen, "1333-74-0");
        ids.put(Helium, "7440-59-7");
        ids.put(Lithium, "7439-93-2");
        ids.put(Beryllium, "7440-41-7");
        ids.put(Boron, "7440-42-8");
        ids.put(Carbon, "7440-44-0");
        ids.put(Nitrogen, "7727-37-9");
        ids.put(Oxygen, "7782-44-7");
        ids.put(Fluorine, "7782-41-4");
        ids.put(Neon, "7440-01-9");
        ids.put(Sodium, "7440-23-5");
        ids.put(Magnesium, "7439-95-4");
        ids.put(Aluminium, "7429-90-5");
        ids.put(Silicon, "7440-21-3");
        ids.put(Phosphorus, "7723-14-0");
        ids.put(Sulfur, "7704-34-9");
        ids.put(Chlorine, "7782-50-5");
        ids.put(Argon, "7440-37-1");
        ids.put(Potassium, "7440-09-7");
        ids.put(Calcium, "7440-70-2");
        ids.put(Scandium, "7440-20-2");
        ids.put(Titanium, "7440-32-6");
        ids.put(Vanadium, "7440-62-2");
        ids.put(Chromium, "7440-47-3");
        ids.put(Manganese, "7439-96-5");
        ids.put(Iron, "7439-89-6");
        ids.put(Cobalt, "7440-48-4");
        ids.put(Nickel, "7440-02-0");
        ids.put(Copper, "7440-50-8");
        ids.put(Zinc, "7440-66-6");
        ids.put(Gallium, "7440-55-3");
        ids.put(Germanium, "7440-56-4");
        ids.put(Arsenic, "7440-38-2");
        ids.put(Selenium, "7782-49-2");
        ids.put(Bromine, "7726-95-6");
        ids.put(Krypton, "7439-90-9");
        ids.put(Rubidium, "7440-17-7");
        ids.put(Strontium, "7440-24-6");
        ids.put(Yttrium, "7440-65-5");
        ids.put(Zirconium, "7440-67-7");
        ids.put(Niobium, "7440-03-1");
        ids.put(Molybdenum, "7439-98-7");
        ids.put(Technetium, "7440-26-8");
        ids.put(Ruthenium, "7440-18-8");
        ids.put(Rhodium, "7440-16-6");
        ids.put(Palladium, "7440-05-3");
        ids.put(Silver, "7440-22-4");
        ids.put(Cadmium, "7440-43-9");
        ids.put(Indium, "7440-74-6");
        ids.put(Tin, "7440-31-5");
        ids.put(Antimony, "7440-36-0");
        ids.put(Tellurium, "13494-80-9");
        ids.put(Iodine, "7553-56-2");
        ids.put(Xenon, "7440-63-3");
        ids.put(Caesium, "7440-46-2");
        ids.put(Barium, "7440-39-3");
        ids.put(Lanthanum, "7439-91-0");
        ids.put(Cerium, "7440-45-1");
        ids.put(Praseodymium, "7440-10-0");
        ids.put(Neodymium, "7440-00-8");
        ids.put(Promethium, "7440-12-2");
        ids.put(Samarium, "7440-19-9");
        ids.put(Europium, "7440-53-1");
        ids.put(Gadolinium, "7440-54-2");
        ids.put(Terbium, "7440-27-9");
        ids.put(Dysprosium, "7429-91-6");
        ids.put(Holmium, "7440-60-0");
        ids.put(Erbium, "7440-52-0");
        ids.put(Thulium, "7440-30-4");
        ids.put(Ytterbium, "7440-64-4");
        ids.put(Lutetium, "7439-94-3");
        ids.put(Hafnium, "7440-58-6");
        ids.put(Tantalum, "7440-25-7");
        ids.put(Tungsten, "7440-33-7");
        ids.put(Rhenium, "7440-15-5");
        ids.put(Osmium, "7440-04-2");
        ids.put(Iridium, "7439-88-5");
        ids.put(Platinum, "7440-06-4");
        ids.put(Gold, "7440-57-5");
        ids.put(Mercury, "7439-97-6");
        ids.put(Thallium, "7440-28-0");
        ids.put(Lead, "7439-92-1");
        ids.put(Bismuth, "7440-69-9");
        ids.put(Polonium, "7440-08-6");
        ids.put(Astatine, "7440-08-6");
        ids.put(Radon, "10043-92-2");
        ids.put(Francium, "7440-73-5");
        ids.put(Radium, "7440-14-4");
        ids.put(Actinium, "7440-34-8");
        ids.put(Thorium, "7440-29-1");
        ids.put(Protactinium, "7440-13-3");
        ids.put(Uranium, "7440-61-1");
        ids.put(Neptunium, "7439-99-8");
        ids.put(Plutonium, "7440-07-5");
        ids.put(Americium, "7440-35-9");
        ids.put(Curium, "7440-51-9");
        ids.put(Berkelium, "7440-40-6");
        ids.put(Californium, "7440-71-3");
        ids.put(Einsteinium, "7429-92-7");
        ids.put(Fermium, "7440-72-4");
        ids.put(Mendelevium, "7440-11-1");
        ids.put(Nobelium, "10028-14-5");
        ids.put(Lawrencium, "22537-19-5");
        ids.put(Rutherfordium, "53850-36-5");
        ids.put(Dubnium, "53850-35-4");
        ids.put(Seaborgium, "54038-81-2");
        ids.put(Bohrium, "54037-14-8");
        ids.put(Hassium, "54037-57-9");
        ids.put(Meitnerium, "54038-01-6");
        ids.put(Darmstadtium, "54083-77-1");
        ids.put(Roentgenium, "54386-24-2");
        ids.put(Copernicium, "54084-26-3");
        ids.put(Ununtrium, "");
        ids.put(Flerovium, "54085-16-4");
        ids.put(Ununpentium, "");
        ids.put(Livermorium, "54100-71-9");
        ids.put(Ununseptium, "");
        ids.put(Ununoctium, "");
        return ids;
    }

}
