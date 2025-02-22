/*
 * Copyright (c) 2013, European Bioinformatics Institute (EMBL-EBI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package uk.ac.ebi.beam;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of valid OpenSMILES elements.
 *
 * <h2>Organic subsets</h2> Several of the elements belong to the organic
 * subset. Atoms of an organic element type can be written just as their symbol
 * (see. <a href="http://www.opensmiles.org/opensmiles.html#orgsbst">Organic
 * Subset, OpenSMILES Specification</a>).
 *
 * <ul> <li>{@link #Unknown} (<code>*</code>)</li> <li>{@link #Boron}</li>
 * <li>{@link #Carbon}</li> <li>{@link #Nitrogen}</li> <li>{@link #Oxygen}</li>
 * <li>{@link #Fluorine}</li> <li>{@link #Phosphorus}</li> <li>{@link
 * #Sulfur}</li> <li>{@link #Chlorine}</li> <li>{@link #Bromine}</li> <li>{@link
 * #Iodine}</li> </ul>
 *
 * <h2>Usage</h2>
 *
 * Elements can be created by either using the value directly or by looking up
 * it's symbol. If the element may be aromatic the lower-case symbol can also be
 * used. For example the variable 'e' in the three statements below all have the
 * same value, {@link Element#Carbon}.
 *
 * <blockquote><pre>
 * Element e = Element.Carbon;
 * Element e = Element.ofSymbol("C");
 * Element e = Element.ofSymbol("c");
 * </pre></blockquote>
 *
 * When the symbol is invalid the result wil be null.
 * <blockquote><pre>
 * Element e = Element.ofSymbol("R1"); // e = null
 * </pre></blockquote>
 *
 * The {@link Element#Unknown} element can be used to represent generic/alias
 * atoms.
 * <blockquote><pre>
 * Element e = Element.Unknown;
 * Element e = Element.ofSymbol("*");
 * </pre></blockquote>
 *
 * To access the symbol of an already created element. Use {@link
 * Element#symbol()}.
 *
 * <blockquote><pre>
 * Atom    a = ...;
 * Element e = a.element();
 *
 * String  symbol = e.symbol();
 * </pre></blockquote>
 *
 * @author John May
 * @see <a href="http://www.opensmiles.org/opensmiles.html#inatoms">Atoms,
 *      OpenSMILES Specification</a>
 */
public enum Element {

    /** Unspecified/Unknown element (*) */
    Unknown(0, "*", 0),

    Hydrogen(1, "H"),
    Helium(2, "He"),

    Lithium(3, "Li"),
    Beryllium(4, "Be"),
    Boron(5, "B", 3),
    Carbon(6, "C", 4),
    Nitrogen(7, "N", 3, 5),
    Oxygen(8, "O", 2),
    Fluorine(9, "F", 1),
    Neon(10, "Ne"),

    Sodium(11, "Na"),
    Magnesium(12, "Mg"),
    Aluminum(13, "Al"),
    Silicon(14, "Si"),
    Phosphorus(15, "P", 3, 5),
    Sulfur(16, "S", 2, 4, 6),
    Chlorine(17, "Cl", 1),
    Argon(18, "Ar"),

    Potassium(19, "K"),
    Calcium(20, "Ca"),
    Scandium(21, "Sc"),
    Titanium(22, "Ti"),
    Vanadium(23, "V"),
    Chromium(24, "Cr"),
    Manganese(25, "Mn"),
    Iron(26, "Fe"),
    Cobalt(27, "Co"),
    Nickel(28, "Ni"),
    Copper(29, "Cu"),
    Zinc(30, "Zn"),
    Gallium(31, "Ga"),
    Germanium(32, "Ge"),
    Arsenic(33, "As"),
    Selenium(34, "Se"),
    Bromine(35, "Br", 1),
    Krypton(36, "Kr"),

    Rubidium(37, "Rb"),
    Strontium(38, "Sr"),
    Yttrium(39, "Y"),
    Zirconium(40, "Zr"),
    Niobium(41, "Nb"),
    Molybdenum(42, "Mo"),
    Technetium(43, "Tc"),
    Ruthenium(44, "Ru"),
    Rhodium(45, "Rh"),
    Palladium(46, "Pd"),
    Silver(47, "Ag"),
    Cadmium(48, "Cd"),
    Indium(49, "In"),
    Tin(50, "Sn"),
    Antimony(51, "Sb"),
    Tellurium(52, "Te"),
    Iodine(53, "I", 1),
    Xenon(54, "Xe"),

    Cesium(55, "Cs"),
    Barium(56, "Ba"),
    // f-block (see below)
    Lutetium(71, "Lu"),
    Hafnium(72, "Hf"),
    Tantalum(73, "Ta"),
    Tungsten(74, "W"),
    Rhenium(75, "Re"),
    Osmium(76, "Os"),
    Iridium(77, "Ir"),
    Platinum(78, "Pt"),
    Gold(79, "Au"),
    Mercury(80, "Hg"),
    Thallium(81, "Tl"),
    Lead(82, "Pb"),
    Bismuth(83, "Bi"),
    Polonium(84, "Po"),
    Astatine(85, "At"),
    Radon(86, "Rn"),

    Francium(87, "Fr"),
    Radium(88, "Ra"),
    // f-block (see below)
    Lawrencium(103, "Lr"),
    Rutherfordium(104, "Rf"),
    Dubnium(105, "Db"),
    Seaborgium(106, "Sg"),
    Bohrium(107, "Bh"),
    Hassium(108, "Hs"),
    Meitnerium(109, "Mt"),
    Darmstadtium(110, "Ds"),
    Roentgenium(111, "Rg"),
    Copernicium(112, "Cn"),
    Nihonium(113, "Nh"),
    Flerovium(114, "Fl"),
    Moscovium(115, "Mc"),
    Livermorium(116, "Lv"),
    Tennessine(117, "Ts"),
    Oganesson(118, "Og"),

    Lanthanum(57, "La"),
    Cerium(58, "Ce"),
    Praseodymium(59, "Pr"),
    Neodymium(60, "Nd"),
    Promethium(61, "Pm"),
    Samarium(62, "Sm"),
    Europium(63, "Eu"),
    Gadolinium(64, "Gd"),
    Terbium(65, "Tb"),
    Dysprosium(66, "Dy"),
    Holmium(67, "Ho"),
    Erbium(68, "Er"),
    Thulium(69, "Tm"),
    Ytterbium(70, "Yb"),

    Actinium(89, "Ac"),
    Thorium(90, "Th"),
    Protactinium(91, "Pa"),
    Uranium(92, "U"),
    Neptunium(93, "Np"),
    Plutonium(94, "Pu"),
    Americium(95, "Am"),
    Curium(96, "Cm"),
    Berkelium(97, "Bk"),
    Californium(98, "Cf"),
    Einsteinium(99, "Es"),
    Fermium(100, "Fm"),
    Mendelevium(101, "Md"),
    Nobelium(102, "No");

    /** Atomic number of the elemnt. */
    private final int atomicNumber;

    /** The symbol of the element. */
    private final String symbol;

    /**
     * Default valence information - only present if the atom is part of the
     * organic subset.
     */
    private final int[] valence;

    private final int[] electrons;

    /** Look up of elements by symbol */
    private static final Map<String, Element> elementMap = new HashMap<String, Element>();

    private static final Element[] elements = new Element[119];

    /** Provide verification of valence/charge values. */
    private ElementCheck defaults = ElementCheck.NO_CHECK;

    static {
        for (Element element : values()) {
            elementMap.put(element.symbol().toLowerCase(), element);
            elementMap.put(element.symbol(), element);
            elements[element.atomicNumber] = element;
        }

        // load normal ranges from 'element-defaults.txt' and set for the
        // elements
        for (Map.Entry<String, ElementCheck> e : loadDefaults().entrySet()) {
            elementMap.get(e.getKey()).defaults = e.getValue();
        }
    }

    private Element(int atomicNumber, String symbol) {
        this(atomicNumber, symbol, null);
    }

    private Element(int atomicNumber,
                    String symbol,
                    int... valence) {
        this.atomicNumber = atomicNumber;
        this.symbol = symbol;
        this.valence = valence;
        if (valence != null) {
            this.electrons = new int[valence.length];
            for (int i = 0; i < valence.length; i++) {
                electrons[i] = valence[i] * 2;
            }
        }
        else {
            this.electrons = null;
        }
    }

    /**
     * Access the symbol of the element.
     *
     * @return element symbol
     */
    public String symbol() {
        return symbol;
    }

    /**
     * The atomic number of the element. If the element is unknown '0' is
     * returned.
     *
     * @return atomic number
     */
    public int atomicNumber() {
        return atomicNumber;
    }

    /**
     * Can the element be aromatic. This definition is very loose and includes
     * elements which are not part of the Daylight, OpenSMILES specification. To
     * test if ane element is aromatic by the specification use {@link
     * #aromatic(uk.ac.ebi.beam.Element.AromaticSpecification)}.
     *
     * @return whether the element may be aromatic
     */
    boolean aromatic() {
        return aromatic(AromaticSpecification.General);
    }

    /**
     * Can the element be aromatic in accordance with a given specification.
     *
     * @param spec such {@link uk.ac.ebi.beam.Element.AromaticSpecification#Daylight},
     *             {@link uk.ac.ebi.beam.Element.AromaticSpecification#OpenSmiles}
     * @return the element is accepted as being aromatic by that scheme
     */
    boolean aromatic(AromaticSpecification spec) {
        return spec.contains(this);
    }

    /**
     * Is the element a member of the organic subset and can be written without
     * brackets. If the element is both organic and aromatic is a member of the
     * aromatic subset and can still be written without brackets.
     *
     * @return the element can be written without brackets
     */
    boolean organic() {
        return valence != null;
    }

    /**
     * Determine the number of implied hydrogens an organic (or aromatic) subset
     * atom has based on it's bond order sum. The valances for the organic
     * elements (B, C, N, O, P, S, F, Cl, Br and I) are defined in the
     * OpenSMILES specification.
     *
     * @param v bonded valence
     * @return the number of implied hydrogens
     * @deprecated delegates to #implicitHydrogenCount                                   
     */
    @Deprecated
    int implicitHydrogens(int v) {
        return implicitHydrogenCount(this, v);
    }

    /**
     * @deprecated delegates to #implicitHydrogenCount
     */
    @Deprecated
    int aromaticImplicitHydrogens(int v) {
        return implicitAromHydrogenCount(this, v);
    }

    /**
     * Determine the number of available electrons which could be bonding to
     * implicit hydrogens. This include electrons donated from the hydrogen.
     * <br/>
     *
     * The central carbon of {@code C-C=C} 6 bonded electrons - using SMILES
     * default valence there must be 2 electrons involved in bonding an implicit
     * hydrogen (i.e. there is a single bond to a hydrogen).
     *
     * @param bondElectronSum the sum of the bonded electrons
     * @return number of electrons which could be involved with bonds to
     *         hydrogen
     */
    int availableElectrons(int bondElectronSum) {
        for (final int e : electrons)
            if (bondElectronSum <= e)
                return e - bondElectronSum;
        return 0;
    }

    /**
     * Determine the number of available electrons which could be bonding to
     * implicit hydrogens for an aromatic atom with delocalized bonds. This
     * include electrons donated from the hydrogen. <br/>
     *
     * Instead of checking higher valence states only the lowest is checked. For
     * example nitrogen has valence 3 and 5 but in a delocalized system only the
     * lowest (3) is used. The electrons which would allow bonding of implicit
     * hydrogens in the higher valence states are donated to the aromatic system
     * and thus cannot be <i>reached</i>. Using a generalisation that an
     * aromatic bond as 3 electrons we reached the correct value for multi
     * valence aromatic elements. <br/>
     *
     * <blockquote><pre>
     *     c1c[nH]cn1    the aromatic subset nitrogen is bonded to two aromatic
     *                   nitrogen bond order sum of 3 (6 electrons) there are
     *                   no implicit hydrogens
     *
     *     c1cc2ccccn2c1 the nitrogen has three aromatic bond 4.5 bond order
     *                   (9 electrons) - as we only check the lowest valence
     *                   (3 - 4.5) < 0 so there are 0 implicit hydrogens
     *
     *     c1ccpcc1      the phosphorus has 2 aromatic bond (bond order sum 3)
     *                   and the lowest valence is '3' - there are no implicit
     *                   hydrogens
     *
     *     oc1ccscc1     the sulphur has two aromatic bonds (bond order sum 3)
     *                   the lowest valence is '2' - 3 > 2 so there are no
     *                   implicit hydrogens
     *
     *     oc1ccscc1     the oxygen has a single aromatic bond, the default
     *                   valence of oxygen in the specification is '2' there
     *                   are no hydrogens (2 - 1.5 = 0.5).
     * </pre></blockquote>
     *
     * @param bondElectronSum the sum of the bonded electrons
     * @return number of electrons which could be involved with bonds to
     *         hydrogen
     */
    int availableDelocalisedElectrons(int bondElectronSum) {
        if (bondElectronSum <= electrons[0])
            return electrons[0] - bondElectronSum;
        return 0;
    }

    /**
     * Verify whether the given valence and charge are 'normal' for the
     * element.
     *
     * @param v valence (bond order order sum)
     * @param q charge
     * @return whether the valence and charge are valid
     */
    boolean verify(int v, int q) {
        // table driven verification (see. element-defaults.txt)
        return defaults.verify(v, q);
    }

    /**
     * Given an element symbol, provide the element for that symbol. If no
     * symbol was found then null is returned.
     *
     * @param symbol the element symbol
     * @return element for the symbol, or null if none found
     */
    public static Element ofSymbol(final String symbol) {
        return elementMap.get(symbol);
    }

    /**
     * Access an element by atomic number.
     * 
     * @param elem atomic number
     * @return the element for the atomic number
     */
    public static Element ofNumber(final int elem) {
        return elements[elem];
    }

    /**
     * Read an element and progress the character buffer. If the element was not
     * read then a 'null' element is returned.
     *
     * @param buffer a character buffer
     * @return the element, or null
     */
    static Element read(final CharBuffer buffer) {
        if (!buffer.hasRemaining())
            return null;
        char c = buffer.get();
        if (buffer.hasRemaining() && buffer.next() >= 'a' && buffer
                .next() <= 'z') {
            return elementMap.get(new String(new char[]{c, buffer.get()}));
        }
        return elementMap.get(Character.toString(c));
    }

    static Map<String, ElementCheck> loadDefaults() {
        Map<String, ElementCheck> checks = new HashMap<String, ElementCheck>(200);
        try {
            InputStream in = Element.class.getResourceAsStream("element-defaults.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0 || line.charAt(0) == '-') // empty line or comment
                    continue;
                Map.Entry<String, ElementCheck> entry = load(line);
                checks.put(entry.getKey(), entry.getValue());
            }
            br.close();
        } catch (Exception e) {
            System.err.println("error whilst loading element-defaults.txt: " + e);
        }
        return checks;
    }

    static Map.Entry<String, ElementCheck> load(String line) {
        String[] data = line.split("\\s+");
        String symbol = data[0];
        int electrons = Integer.parseInt(data[3]);
        ValenceCheck valenceCheck = ValenceCheck.parse(data[1], electrons);
        ChargeCheck chargeCheck = ChargeCheck.parse(data[2]);
        return new AbstractMap.SimpleEntry<String, ElementCheck>(symbol,
                                                                 new ElementCheck(valenceCheck, chargeCheck));
    }

    private static final class ElementCheck {
        private final ValenceCheck valenceCheck;
        private final ChargeCheck  chargeCheck;

        private ElementCheck(ValenceCheck valenceCheck, ChargeCheck chargeCheck) {
            this.valenceCheck = valenceCheck;
            this.chargeCheck = chargeCheck;
        }

        boolean verify(int v, int q) {
            return chargeCheck.verify(q) && valenceCheck.verify(v, q);
        }

        private static final ElementCheck NO_CHECK = new ElementCheck(NoValenceCheck.INSTANCE,
                                                                      ChargeCheck.NONE);

        @Override public String toString() {
            return chargeCheck + ", " + valenceCheck;
        }
    }

    private static abstract class ValenceCheck {

        abstract boolean verify(final int v, final int q);

        static ValenceCheck parse(String line, int nElectrons) {
            String[] vs = line.split(",");
            if (vs.length == 1) {
                if (vs[0].equals("n/a")) {
                    return NoValenceCheck.INSTANCE;
                }
                else if (vs[0].charAt(0) == '(') {
                    return new FixedValence(Integer.parseInt(vs[0].substring(1, vs[0].length() - 1)));
                }
                else if (vs[0].charAt(0) == '[') {
                    return new NeutralValence(Integer.parseInt(vs[0].substring(1, vs[0].length() - 1)));
                }
                else {
                    return new ChargeAdjustedValence(Integer.parseInt(vs[0]), nElectrons);
                }
            }
            ValenceCheck[] valences = new ValenceCheck[vs.length];
            for (int i = 0; i < vs.length; i++) {
                valences[i] = parse(vs[i], nElectrons);
            }

            return new MultiValenceCheck(valences);
        }
    }

    private static final class ChargeAdjustedValence extends ValenceCheck {
        private final int valence, nElectrons;

        private ChargeAdjustedValence(int valence, int nElectrons) {
            this.valence = valence;
            this.nElectrons = nElectrons;
        }

        @Override public boolean verify(int v, int q) {
            if (nElectrons == 2 && valence + q > nElectrons - q)  // Group 2 exception
                return v == nElectrons - q;
            return valence + q == v;
        }

        @Override public String toString() {
            return "Charge(" + valence + ")";
        }
    }

    /** A valence check which is only valid at netural charge */
    private static final class NeutralValence extends ValenceCheck {
        private final int valence;

        private NeutralValence(int valence) {
            this.valence = valence;
        }

        @Override public boolean verify(int v, int q) {
            return q == 0 && v == valence;
        }

        @Override public String toString() {
            return "Neutral(" + valence + ")";
        }
    }

    private static final class FixedValence extends ValenceCheck {
        private final int valence;

        private FixedValence(int valence) {
            this.valence = valence;
        }

        @Override public boolean verify(int v, int q) {
            return valence == v;
        }

        @Override public String toString() {
            return "Fixed(" + valence + ")";
        }
    }

    private static final class MultiValenceCheck extends ValenceCheck {

        private final ValenceCheck[] valences;

        private MultiValenceCheck(ValenceCheck[] valences) {
            this.valences = valences;
        }

        @Override public boolean verify(int v, int q) {
            for (ValenceCheck vc : valences) {
                if (vc.verify(v, q)) {
                    return true;
                }
            }
            return false;
        }

        @Override public String toString() {
            return Arrays.toString(valences);
        }
    }

    private static final class NoValenceCheck extends ValenceCheck {
        @Override boolean verify(int v, int q) {
            return true;
        }

        private static final ValenceCheck INSTANCE = new NoValenceCheck();
    }

    private static final class ChargeCheck {

        private final int lo, hi;

        private ChargeCheck(int lo, int hi) {
            this.lo = lo;
            this.hi = hi;
        }

        boolean verify(final int q) {
            return lo <= q && q <= hi;
        }

        static ChargeCheck parse(String range) {
            if (range.equals("n/a"))
                return NONE;
            String[] data = range.split(",");
            int lo = Integer.parseInt(data[0]);
            int hi = Integer.parseInt(data[1]);
            return new ChargeCheck(lo, hi);
        }

        private static final ChargeCheck NONE = new ChargeCheck(Integer.MIN_VALUE, Integer.MAX_VALUE);

        @Override public String toString() {
            return lo + " < q < " + hi;
        }
    }

    /**
     * Stores which elements the Daylight and OpenSMILES specification consider
     * to be aromatic. The General scheme is what might be encountered 'in the
     * wild'.
     */
    enum AromaticSpecification {

        Daylight(Unknown,
                 Carbon,
                 Nitrogen,
                 Oxygen,
                 Sulfur,
                 Phosphorus,
                 Arsenic,
                 Selenium),

        OpenSmiles(Unknown,
                   Boron,
                   Carbon,
                   Nitrogen,
                   Oxygen,
                   Sulfur,
                   Phosphorus,
                   Arsenic,
                   Selenium),

        General(Unknown,
                Boron,
                Carbon,
                Nitrogen,
                Oxygen,
                Sulfur,
                Phosphorus,
                Arsenic,
                Selenium,
                Silicon,
                Germanium,
                Tin,
                Antimony,
                Tellurium,
                Bismuth);

        private EnumSet<Element> elements;

        AromaticSpecification(Element... es) {
            this.elements = EnumSet.noneOf(Element.class);
            for (Element e : es)
                elements.add(e);
        }

        boolean contains(Element e) {
            return elements.contains(e);
        }
    }

    /**
     * Determine the implicit hydrogen count of an organic subset atom
     * given its bonded valence. The number of implied hydrogens an 
     * organic (or aromatic) subset atom has is based on it's bonded
     * valence. The valances for the organic elements (B, C, N, O, P,
     * S, F, Cl, Br and I) are defined in the OpenSMILES specification.
     *
     * @param elem Element
     * @param v    bonded valence
     * @return hydrogen count >= 0
     */
    static int implicitHydrogenCount(final Element elem, final int v) {
        switch (elem) {
            case Boron:
                if (v < 3)  return 3-v;
                break;
            case Carbon:
                if (v < 4)  return 4-v;
                break;
            case Nitrogen:
            case Phosphorus:
                if (v <= 3) return 3-v;
                if (v <  5) return 5-v;
                break;
            case Oxygen:
                if (v < 2)  return 2-v;
                break;
            case Sulfur:
                if (v <= 2) return 2-v;
                if (v <= 4) return 4-v;
                if (v <  6) return 6-v;
                break;
            case Chlorine:
            case Bromine:
            case Iodine:
            case Fluorine:
                if (v < 1)  return 1;
                break;
        }
        return 0;
    }

    /**
     * Determine the implicit hydrogen count of an organic subset atom
     * given its bonded valence. The number of implied hydrogens an 
     * organic (or aromatic) subset atom has is based on it's bonded
     * valence. The valances for the organic elements (B, C, N, O, P,
     * S, F, Cl, Br and I) are defined in the OpenSMILES specification.
     * For aromatic atoms we only check the first level.
     *
     * @param elem Element
     * @param v    bonded valence
     * @return hydrogen count >= 0
     */
    static int implicitAromHydrogenCount(final Element elem, final int v) {
        switch (elem) {
            case Boron: // arom?
                if (v < 3) return 3-v;
                break;
            case Carbon:
                if (v < 4) return 4-v;
                break;
            case Nitrogen:
            case Phosphorus:
                if (v < 3) return 3-v;
                break;
            case Oxygen:
                if (v < 2) return 2-v;
                break;
            case Sulfur:
                if (v < 2) return 2-v;
                break;
        }
        return 0;
    }
}
