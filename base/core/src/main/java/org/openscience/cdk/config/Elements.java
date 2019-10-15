/* 
 * Copyright (C) 2006-2012  Egon Willighagen <egonw@users.sf.net>
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
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
import org.openscience.cdk.interfaces.IElement;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Enumeration of chemical elements. Data is taken from the Blue Obelisk Data
 * Repository, version 3. This enumeration is auto-generated with utilities
 * found in the 'cdk-build-utils' project.
 *
 * @author egonw
 * @author john may
 * @cdk.module core
 * @cdk.githash
 */
public enum Elements {
    Unknown(0, "", 0, 0, null, 0.00, null),
    Hydrogen(1, "H", 1, 1, 0.37, 1.20, 2.20),
    Helium(2, "He", 1, 18, 0.32, 1.40, null),
    Lithium(3, "Li", 2, 1, 1.34, 2.20, 0.98),
    Beryllium(4, "Be", 2, 2, 0.90, 1.90, 1.57),
    Boron(5, "B", 2, 13, 0.82, 1.80, 2.04),
    Carbon(6, "C", 2, 14, 0.77, 1.70, 2.55),
    Nitrogen(7, "N", 2, 15, 0.75, 1.60, 3.04),
    Oxygen(8, "O", 2, 16, 0.73, 1.55, 3.44),
    Fluorine(9, "F", 2, 17, 0.71, 1.50, 3.98),
    Neon(10, "Ne", 2, 18, 0.69, 1.54, null),
    Sodium(11, "Na", 3, 1, 1.54, 2.40, 0.93),
    Magnesium(12, "Mg", 3, 2, 1.30, 2.20, 1.31),
    Aluminium(13, "Al", 3, 13, 1.18, 2.10, 1.61),
    Silicon(14, "Si", 3, 14, 1.11, 2.10, 1.90),
    Phosphorus(15, "P", 3, 15, 1.06, 1.95, 2.19),
    Sulfur(16, "S", 3, 16, 1.02, 1.80, 2.58),
    Chlorine(17, "Cl", 3, 17, 0.99, 1.80, 3.16),
    Argon(18, "Ar", 3, 18, 0.97, 1.88, null),
    Potassium(19, "K", 4, 1, 1.96, 2.80, 0.82),
    Calcium(20, "Ca", 4, 2, 1.74, 2.40, 1.00),
    Scandium(21, "Sc", 4, 3, 1.44, 2.30, 1.36),
    Titanium(22, "Ti", 4, 4, 1.36, 2.15, 1.54),
    Vanadium(23, "V", 4, 5, 1.25, 2.05, 1.63),
    Chromium(24, "Cr", 4, 6, 1.27, 2.05, 1.66),
    Manganese(25, "Mn", 4, 7, 1.39, 2.05, 1.55),
    Iron(26, "Fe", 4, 8, 1.25, 2.05, 1.83),
    Cobalt(27, "Co", 4, 9, 1.26, null, 1.88),
    Nickel(28, "Ni", 4, 10, 1.21, null, 1.91),
    Copper(29, "Cu", 4, 11, 1.38, null, 1.90),
    Zinc(30, "Zn", 4, 12, 1.31, 2.10, 1.65),
    Gallium(31, "Ga", 4, 13, 1.26, 2.10, 1.81),
    Germanium(32, "Ge", 4, 14, 1.22, 2.10, 2.01),
    Arsenic(33, "As", 4, 15, 1.19, 2.05, 2.18),
    Selenium(34, "Se", 4, 16, 1.16, 1.90, 2.55),
    Bromine(35, "Br", 4, 17, 1.14, 1.90, 2.96),
    Krypton(36, "Kr", 4, 18, 1.10, 2.02, 3.00),
    Rubidium(37, "Rb", 5, 1, 2.11, 2.90, 0.82),
    Strontium(38, "Sr", 5, 2, 1.92, 2.55, 0.95),
    Yttrium(39, "Y", 5, 3, 1.62, 2.40, 1.22),
    Zirconium(40, "Zr", 5, 4, 1.48, 2.30, 1.33),
    Niobium(41, "Nb", 5, 5, 1.37, 2.15, 1.60),
    Molybdenum(42, "Mo", 5, 6, 1.45, 2.10, 2.16),
    Technetium(43, "Tc", 5, 7, 1.56, 2.05, 1.90),
    Ruthenium(44, "Ru", 5, 8, 1.26, 2.05, 2.20),
    Rhodium(45, "Rh", 5, 9, 1.35, null, 2.28),
    Palladium(46, "Pd", 5, 10, 1.31, 2.05, 2.20),
    Silver(47, "Ag", 5, 11, 1.53, 2.10, 1.93),
    Cadmium(48, "Cd", 5, 12, 1.48, 2.20, 1.69),
    Indium(49, "In", 5, 13, 1.44, 2.20, 1.78),
    Tin(50, "Sn", 5, 14, 1.41, 2.25, 1.96),
    Antimony(51, "Sb", 5, 15, 1.38, 2.20, 2.05),
    Tellurium(52, "Te", 5, 16, 1.35, 2.10, 2.10),
    Iodine(53, "I", 5, 17, 1.33, 2.10, 2.66),
    Xenon(54, "Xe", 5, 18, 1.30, 2.16, 2.60),
    Caesium(55, "Cs", 6, 1, 2.25, 3.00, 0.79),
    Barium(56, "Ba", 6, 2, 1.98, 2.70, 0.89),
    Lanthanum(57, "La", 6, 3, 1.69, 2.50, 1.10),
    Cerium(58, "Ce", 6, 0, null, 2.48, 1.12),
    Praseodymium(59, "Pr", 6, 0, null, 2.47, 1.13),
    Neodymium(60, "Nd", 6, 0, null, 2.45, 1.14),
    Promethium(61, "Pm", 6, 0, null, 2.43, null),
    Samarium(62, "Sm", 6, 0, null, 2.42, 1.17),
    Europium(63, "Eu", 6, 0, 2.40, 2.40, null),
    Gadolinium(64, "Gd", 6, 0, null, 2.38, 1.20),
    Terbium(65, "Tb", 6, 0, null, 2.37, null),
    Dysprosium(66, "Dy", 6, 0, null, 2.35, 1.22),
    Holmium(67, "Ho", 6, 0, null, 2.33, 1.23),
    Erbium(68, "Er", 6, 0, null, 2.32, 1.24),
    Thulium(69, "Tm", 6, 0, null, 2.30, 1.25),
    Ytterbium(70, "Yb", 6, 0, null, 2.28, null),
    Lutetium(71, "Lu", 6, 0, 1.60, 2.27, 1.27),
    Hafnium(72, "Hf", 6, 4, 1.50, 2.25, 1.30),
    Tantalum(73, "Ta", 6, 5, 1.38, 2.20, 1.50),
    Tungsten(74, "W", 6, 6, 1.46, 2.10, 2.36),
    Rhenium(75, "Re", 6, 7, 1.59, 2.05, 1.90),
    Osmium(76, "Os", 6, 8, 1.28, null, 2.20),
    Iridium(77, "Ir", 6, 9, 1.37, null, 2.20),
    Platinum(78, "Pt", 6, 10, 1.28, 2.05, 2.28),
    Gold(79, "Au", 6, 11, 1.44, 2.10, 2.54),
    Mercury(80, "Hg", 6, 12, 1.49, 2.05, 2.00),
    Thallium(81, "Tl", 6, 13, 1.48, 2.20, 1.62),
    Lead(82, "Pb", 6, 14, 1.47, 2.30, 2.33),
    Bismuth(83, "Bi", 6, 15, 1.46, 2.30, 2.02),
    Polonium(84, "Po", 6, 16, 1.46, null, 2.00),
    Astatine(85, "At", 6, 17, null, null, 2.20),
    Radon(86, "Rn", 6, 18, 1.45, null, null),
    Francium(87, "Fr", 7, 1, null, null, 0.70),
    Radium(88, "Ra", 7, 2, null, null, 0.90),
    Actinium(89, "Ac", 7, 3, null, null, 1.10),
    Thorium(90, "Th", 7, 0, null, 2.40, 1.30),
    Protactinium(91, "Pa", 7, 0, null, null, 1.50),
    Uranium(92, "U", 7, 0, null, 2.30, 1.38),
    Neptunium(93, "Np", 7, 0, null, null, 1.36),
    Plutonium(94, "Pu", 7, 0, null, null, 1.28),
    Americium(95, "Am", 7, 0, null, null, 1.30),
    Curium(96, "Cm", 7, 0, null, null, 1.30),
    Berkelium(97, "Bk", 7, 0, null, null, 1.30),
    Californium(98, "Cf", 7, 0, null, null, 1.30),
    Einsteinium(99, "Es", 7, 0, null, null, 1.30),
    Fermium(100, "Fm", 7, 0, null, null, 1.30),
    Mendelevium(101, "Md", 7, 0, null, null, 1.30),
    Nobelium(102, "No", 7, 0, null, null, 1.30),
    Lawrencium(103, "Lr", 7, 0, null, null, null),
    Rutherfordium(104, "Rf", 7, 4, null, null, null),
    Dubnium(105, "Db", 7, 5, null, null, null),
    Seaborgium(106, "Sg", 7, 6, null, null, null),
    Bohrium(107, "Bh", 7, 7, null, null, null),
    Hassium(108, "Hs", 7, 8, null, null, null),
    Meitnerium(109, "Mt", 7, 9, null, null, null),
    Darmstadtium(110, "Ds", 7, 10, null, null, null),
    Roentgenium(111, "Rg", 7, 11, null, null, null),
    Copernicium(112, "Cn", 7, 12, null, null, null),
    @Deprecated
    Ununtrium(113, "Uut", 7, 13, null, null, null),
    Nihonium(113, "Nh", 7, 13, null, null, null),
    Flerovium(114, "Fl", 7, 14, null, null, null),
    @Deprecated
    Ununpentium(115, "Uup", 7, 15, null, null, null),
    Moscovium(115, "Mc", 7, 15, null, null, null),
    Livermorium(116, "Lv", 7, 16, null, null, null),
    @Deprecated
    Ununseptium(117, "Uus", 7, 17, null, null, null),
    Tennessine(117, "Ts", 7, 17, null, null, null),
    @Deprecated
    Ununoctium(118, "Uuo", 7, 18, null, null, null),
    Oganesson(118, "Og", 7, 18, null, null, null);

    /**
     * Atomic number, periodic table period and group.
     */
    private final int number, period, group;

    /**
     * The symbol of the element.
     */
    private final String symbol;

    /**
     * Covalent radius (<i>r<sub>cov</sub></i>), van der Waals radius
     * (<i>r<sub>w</sub></i>) and Pauling electronegativity.
     */
    private final Double rCov, rW, electronegativity;

    /**
     * An {@link IElement} instance of this element.
     */
    private final IElement instance;

    /**
     * Lookup elements by atomic number.
     */
    static final Elements[] NUMER_MAP = new Elements[119];

    /**
     * Lookup elements by symbol / name.
     */
    static final Map<String, Elements> SYMBOL_MAP = new HashMap<String, Elements>(400);

    static {
        // index elements
        for (final Elements e : values()) {
            NUMER_MAP[e.number] = e;
            SYMBOL_MAP.put(e.symbol.toLowerCase(Locale.ENGLISH), e);
            SYMBOL_MAP.put(e.name().toLowerCase(Locale.ENGLISH), e);
        }

        // recently named elements
        SYMBOL_MAP.put("uub", Copernicium); // 2009
        SYMBOL_MAP.put("ununbium", Copernicium);

        SYMBOL_MAP.put("uuq", Flerovium); // 2012
        SYMBOL_MAP.put("ununquadium", Flerovium);

        SYMBOL_MAP.put("uuh", Livermorium); // 2012
        SYMBOL_MAP.put("ununhexium", Livermorium);

        // 2016
        SYMBOL_MAP.put("uut", Nihonium);
        SYMBOL_MAP.put("uup", Moscovium);
        SYMBOL_MAP.put("uus", Tennessine);
        SYMBOL_MAP.put("uuo", Oganesson);

        // alternative spellings
        SYMBOL_MAP.put("sulphur", Sulfur);
        SYMBOL_MAP.put("cesium", Caesium);
        SYMBOL_MAP.put("aluminum", Aluminium);

    }

    /**
     * Internal constructor.
     *
     * @param number            atomic number
     * @param symbol            symbol
     * @param period            periodic table period
     * @param group             periodic table group
     * @param rCov              covalent radius
     * @param rW                van der Waals radius
     * @param electronegativity pauling electronegativity
     */
    private Elements(int number, String symbol, int period, int group, Double rCov, Double rW, Double electronegativity) {
        this.number = number;
        this.period = period;
        this.group = group;
        this.symbol = symbol;
        this.rCov = rCov;
        this.rW = rW;
        this.electronegativity = electronegativity;
        this.instance = new NaturalElement(symbol, number);
    }

    /**
     * The atomic number of the element. An {@link #Unknown} element
     * has an atomic number of '0'.
     *
     * @return 0 - 116
     */
    public int number() {
        return number;
    }

    /**
     * The element symbol, C for carbon, N for nitrogen, Na for sodium, etc. An
     * {@link #Unknown} element has no symbol.
     *
     * @return the symbol
     */
    public String symbol() {
        return symbol;
    }

    /**
     * Return the period in the periodic table this element belongs to. If
     * the element is {@link #Unknown} it's period is 0.
     *
     * @return a period in the periodic table
     */
    public int period() {
        return period;
    }

    /**
     * Return the group in the periodic table this element belongs to. If
     * the element does not belong to a group then it's group is '0'.
     *
     * @return a group in the periodic table
     */
    public int group() {
        return group;
    }

    /**
     * The covalent radius, <i>r<sub>cov</sub></i>, is a measure of the
     * size of an atom that forms part of one covalent bond.
     *
     * @return covalent radius - null if not available
     * @see <a href="http://en.wikipedia.org/wiki/Covalent_radius">Covalent radius</a>
     */
    public Double covalentRadius() {
        return rCov;
    }

    /**
     * The van der Waals radius, <i>r<sub>w</sub></i>, of an atom is the
     * radius of an imaginary hard sphere which can be used to model the
     * atom.
     *
     * @return van der Waals radius - null if not available
     * @see <a href="http://en.wikipedia.org/wiki/Van_der_Waals_radius">Van de Waals radius</a>
     */
    public Double vdwRadius() {
        return rW;
    }

    /**
     * Electronegativity, symbol χ, is a chemical property that describes
     * the tendency of an atom or a functional group to attract electrons
     * (or electron density) towards itself. This method provides access to the
     * Pauling electronegativity value for a chemical element. If no value is
     * available 'null' is returned.
     *
     * @return Pauling electronegativity - null if not available
     * @see <a href="http://en.wikipedia.org/wiki/Electronegativity#Pauling_electronegativity">Pauling Electronegativity</a>
     */
    public Double electronegativity() {
        return electronegativity;
    }

    /**
     * Access an {@link IElement} instance of the chemical element.
     *
     * @return an instance
     */
    public IElement toIElement() {
        return instance;
    }

    /**
     * Obtain the element with the specified atomic number. If no element had
     * the specified atomic number then {@link #Unknown} is returned.
     *
     * <blockquote><pre>
     *     // carbon
     *     Elements e = Elements.ofNumber(6);
     *
     *     // oxygen
     *     Elements e = Elements.ofNumber(8);
     * </pre></blockquote>
     *
     * @param number atomic number
     * @return an element, or {@link #Unknown}
     */
    public static Elements ofNumber(final int number) {
        if (number < 0 || number > 118) return Unknown;
        return NUMER_MAP[number];
    }

    /**
     * Obtain the element with the specified symbol or name. If no element had
     * the specified symbol or name then {@link #Unknown} is returned. The
     * input is case-insensitive.
     *
     * <blockquote><pre>
     *     // carbon
     *     Elements e = Elements.ofString("c");
     *     Elements e = Elements.ofString("C");
     *     Elements e = Elements.ofString("Carbon");
     *     Elements e = Elements.ofString("carbon");
     * </pre></blockquote>
     *
     * @param str input string
     * @return an element, or {@link #Unknown}
     */
    public static Elements ofString(final String str) {
        if (str == null) return Unknown;
        Elements e = SYMBOL_MAP.get(str.toLowerCase(Locale.ENGLISH));
        if (e == null) return Unknown;
        return e;
    }

    /** These instances are for backards compatability. */
    public final static IElement DUMMY         = Unknown.toIElement();
    public final static IElement HYDROGEN      = Hydrogen.toIElement();
    public final static IElement HELIUM        = Helium.toIElement();
    public final static IElement LITHIUM       = Lithium.toIElement();
    public final static IElement BERYLLIUM     = Beryllium.toIElement();
    public final static IElement BORON         = Boron.toIElement();
    public final static IElement CARBON        = Carbon.toIElement();
    public final static IElement NITROGEN      = Nitrogen.toIElement();
    public final static IElement OXYGEN        = Oxygen.toIElement();
    public final static IElement FLUORINE      = Fluorine.toIElement();
    public final static IElement NEON          = Neon.toIElement();
    public final static IElement SODIUM        = Sodium.toIElement();
    public final static IElement MAGNESIUM     = Magnesium.toIElement();
    public final static IElement ALUMINIUM     = Aluminium.toIElement();
    public final static IElement SILICON       = Silicon.toIElement();
    public final static IElement PHOSPHORUS    = Phosphorus.toIElement();
    public final static IElement SULFUR        = Sulfur.toIElement();
    public final static IElement CHLORINE      = Chlorine.toIElement();
    public final static IElement ARGON         = Argon.toIElement();
    public final static IElement POTASSIUM     = Potassium.toIElement();
    public final static IElement CALCIUM       = Calcium.toIElement();
    public final static IElement SCANDIUM      = Scandium.toIElement();
    public final static IElement TITANIUM      = Titanium.toIElement();
    public final static IElement VANADIUM      = Vanadium.toIElement();
    public final static IElement CHROMIUM      = Chromium.toIElement();
    public final static IElement MANGANESE     = Manganese.toIElement();
    public final static IElement IRON          = Iron.toIElement();
    public final static IElement COBALT        = Cobalt.toIElement();
    public final static IElement NICKEL        = Nickel.toIElement();
    public final static IElement COPPER        = Copper.toIElement();
    public final static IElement ZINC          = Zinc.toIElement();
    public final static IElement GALLIUM       = Gallium.toIElement();
    public final static IElement GERMANIUM     = Germanium.toIElement();
    public final static IElement ARSENIC       = Arsenic.toIElement();
    public final static IElement SELENIUM      = Selenium.toIElement();
    public final static IElement BROMINE       = Bromine.toIElement();
    public final static IElement KRYPTON       = Krypton.toIElement();
    public final static IElement RUBIDIUM      = Rubidium.toIElement();
    public final static IElement STRONTIUM     = Strontium.toIElement();
    public final static IElement YTTRIUM       = Yttrium.toIElement();
    public final static IElement ZIRCONIUM     = Zirconium.toIElement();
    public final static IElement NIOBIUM       = Niobium.toIElement();
    public final static IElement MOLYBDENUM    = Molybdenum.toIElement();
    public final static IElement TECHNETIUM    = Technetium.toIElement();
    public final static IElement RUTHENIUM     = Ruthenium.toIElement();
    public final static IElement RHODIUM       = Rhodium.toIElement();
    public final static IElement PALLADIUM     = Palladium.toIElement();
    public final static IElement SILVER        = Silver.toIElement();
    public final static IElement CADMIUM       = Cadmium.toIElement();
    public final static IElement INDIUM        = Indium.toIElement();
    public final static IElement TIN           = Tin.toIElement();
    public final static IElement ANTIMONY      = Antimony.toIElement();
    public final static IElement TELLURIUM     = Tellurium.toIElement();
    public final static IElement IODINE        = Iodine.toIElement();
    public final static IElement XENON         = Xenon.toIElement();
    public final static IElement CAESIUM       = Caesium.toIElement();
    public final static IElement BARIUM        = Barium.toIElement();
    public final static IElement LANTHANUM     = Lanthanum.toIElement();
    public final static IElement CERIUM        = Cerium.toIElement();
    public final static IElement PRASEODYMIUM  = Praseodymium.toIElement();
    public final static IElement NEODYMIUM     = Neodymium.toIElement();
    public final static IElement PROMETHIUM    = Promethium.toIElement();
    public final static IElement SAMARIUM      = Samarium.toIElement();
    public final static IElement EUROPIUM      = Europium.toIElement();
    public final static IElement GADOLINIUM    = Gadolinium.toIElement();
    public final static IElement TERBIUM       = Terbium.toIElement();
    public final static IElement DYSPROSIUM    = Dysprosium.toIElement();
    public final static IElement HOLMIUM       = Holmium.toIElement();
    public final static IElement ERBIUM        = Erbium.toIElement();
    public final static IElement THULIUM       = Thulium.toIElement();
    public final static IElement YTTERBIUM     = Ytterbium.toIElement();
    public final static IElement LUTETIUM      = Lutetium.toIElement();
    public final static IElement HAFNIUM       = Hafnium.toIElement();
    public final static IElement TANTALUM      = Tantalum.toIElement();
    public final static IElement TUNGSTEN      = Tungsten.toIElement();
    public final static IElement RHENIUM       = Rhenium.toIElement();
    public final static IElement OSMIUM        = Osmium.toIElement();
    public final static IElement IRIDIUM       = Iridium.toIElement();
    public final static IElement PLATINUM      = Platinum.toIElement();
    public final static IElement GOLD          = Gold.toIElement();
    public final static IElement MERCURY       = Mercury.toIElement();
    public final static IElement THALLIUM      = Thallium.toIElement();
    public final static IElement LEAD          = Lead.toIElement();
    public final static IElement BISMUTH       = Bismuth.toIElement();
    public final static IElement POLONIUM      = Polonium.toIElement();
    public final static IElement ASTATINE      = Astatine.toIElement();
    public final static IElement RADON         = Radon.toIElement();
    public final static IElement FRANCIUM      = Francium.toIElement();
    public final static IElement RADIUM        = Radium.toIElement();
    public final static IElement ACTINIUM      = Actinium.toIElement();
    public final static IElement THORIUM       = Thorium.toIElement();
    public final static IElement PROTACTINIUM  = Protactinium.toIElement();
    public final static IElement URANIUM       = Uranium.toIElement();
    public final static IElement NEPTUNIUM     = Neptunium.toIElement();
    public final static IElement PLUTONIUM     = Plutonium.toIElement();
    public final static IElement AMERICIUM     = Americium.toIElement();
    public final static IElement CURIUM        = Curium.toIElement();
    public final static IElement BERKELIUM     = Berkelium.toIElement();
    public final static IElement CALIFORNIUM   = Californium.toIElement();
    public final static IElement EINSTEINIUM   = Einsteinium.toIElement();
    public final static IElement FERMIUM       = Fermium.toIElement();
    public final static IElement MENDELEVIUM   = Mendelevium.toIElement();
    public final static IElement NOBELIUM      = Nobelium.toIElement();
    public final static IElement LAWRENCIUM    = Lawrencium.toIElement();
    public final static IElement RUTHERFORDIUM = Rutherfordium.toIElement();
    public final static IElement DUBNIUM       = Dubnium.toIElement();
    public final static IElement SEABORGIUM    = Seaborgium.toIElement();
    public final static IElement BOHRIUM       = Bohrium.toIElement();
    public final static IElement HASSIUM       = Hassium.toIElement();
    public final static IElement MEITNERIUM    = Meitnerium.toIElement();
    public final static IElement DARMSTADTIUM  = Darmstadtium.toIElement();
    public final static IElement ROENTGENIUM   = Roentgenium.toIElement();
    public final static IElement UNUNBIUM      = Copernicium.toIElement();
    public final static IElement UNUNTRIUM     = Ununtrium.toIElement();
    public final static IElement UNUNQUADIUM   = Flerovium.toIElement();
    public final static IElement FLEROVIUM     = Flerovium.toIElement();
    public final static IElement UNUNPENTIUM   = Ununpentium.toIElement();
    public final static IElement UNUNHEXIUM    = Livermorium.toIElement();
    public final static IElement LIVERMORIUM   = Livermorium.toIElement();

    // Incorrect spelling
    @Deprecated
    public final static IElement PLUTOMNIUM    = PLUTONIUM;

    /**
     * Utility method to determine if an atomic number is a metal.
     * @param atno atomic number
     * @return the atomic number is a metal (or not)
     */
    public static boolean isMetal(int atno) {
        switch (atno) {
            case 0:  // *
            case 1:  // H
            case 2:  // He
            case 6:  // C
            case 7:  // N
            case 8:  // O
            case 9:  // F
            case 10: // Ne
            case 15: // P
            case 16: // S
            case 17: // Cl
            case 18: // Ar
            case 34: // Se
            case 35: // Br
            case 36: // Kr
            case 53: // I
            case 54: // Xe
            case 86: // Rn
                return false;
            case 5:   // B
            case 14:  // Si
            case 32:  // Ge
            case 33:  // As
            case 51:  // Sb
            case 52:  // Te
            case 85:  // At
                return false;
        }
        return true;
    }

    /**
     * Utility method to determine if an atom is a metal.
     *
     * @param atom atom
     * @return the atom is a metal (or not)
     */
    public static boolean isMetal(IAtom atom) {
        return atom.getAtomicNumber() != null &&
               isMetal(atom.getAtomicNumber());
    }
}
