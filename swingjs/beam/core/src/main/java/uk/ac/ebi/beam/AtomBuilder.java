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

/**
 * A builder for {@link Atom} instantiation.
 *
 * <blockquote><pre>
 *
 * // [C]
 * Atom a = AtomBuilder.aliphatic(Element.Carbon)
 *                     .build();
 *
 * // [CH4]
 * Atom a = AtomBuilder.aliphatic(Element.Carbon)
 *                     .hydrogens(4)
 *                     .build();
 *
 * // [13CH4]
 * Atom a = AtomBuilder.aliphatic(Element.Carbon)
 *                     .hydrogens(4)
 *                     .isotope(13)
 *                     .build();
 *
 * // [CH3-]
 * Atom a = AtomBuilder.aliphatic(Element.Carbon)
 *                     .hydrogens(3)
 *                     .charge(-1)
 *                     .build();
 *
 * // or
 * Atom a = AtomBuilder.aliphatic(Element.Carbon)
 *                     .hydrogens(3)
 *                     .anion()
 *                     .build();
 *
 * // [CH4:1]
 * Atom a = AtomBuilder.aliphatic(Element.Carbon)
 *                     .hydrogens(4)
 *                     .atomClass(1)
 *                     .build();
 * </pre></blockquote>
 *
 * @author John May
 */
public final class AtomBuilder {

    private final Element element;
    private int isotope = -1,
            hCount      = 0,
            charge      = 0,
            atomClass   = 0;
    private boolean aromatic;

    private AtomBuilder(Element element, boolean aromatic) {
        this.element = element;
        this.aromatic = aromatic;
    }

    public static AtomBuilder fromExisting(Atom a) {
        if (a == null)
            throw new NullPointerException("no atom provided");
        return new AtomBuilder(a.element(), a.aromatic())
                .charge(a.charge())
                .hydrogens(a.hydrogens())
                .isotope(a.isotope())
                .atomClass(a.atomClass());
    }

    /**
     * Start building an aliphatic atom of the given element.
     *
     * <blockquote><pre>
     * Atom a = AtomBuilder.aliphatic(Element.Carbon)
     *                     .build();
     * </pre></blockquote>
     *
     * @param e element type
     * @return an atom builder to configure additional properties
     * @throws NullPointerException the element was null
     */
    public static AtomBuilder aliphatic(Element e) {
        if (e == null)
            throw new NullPointerException("no element provided");
        return new AtomBuilder(e, false);
    }

    /**
     * Start building an aromatic atom of the given element.
     *
     * <blockquote><pre>
     * Atom a = AtomBuilder.aromatic(Element.Carbon)
     *                     .build();
     * </pre></blockquote>
     *
     * @param e element type
     * @return an atom builder to configure additional properties
     * @throws NullPointerException     the element was null
     * @throws IllegalArgumentException the element cannot be aromatic
     */
    public static AtomBuilder aromatic(Element e) {
        if (e == null)
            throw new NullPointerException("no element provided");
        if (e == Element.Unknown)
            return new AtomBuilder(e, false);
        if (!e.aromatic(Element.AromaticSpecification.General))
            throw new IllegalArgumentException("The element '" + e + "' cannot be aromatic by the Daylight specification.");
        return new AtomBuilder(e, true);
    }

    /**
     * Start building an aliphatic atom of the given element symbol. If an
     * element of the symbol could not be found then the element type is set to
     * {@link Element#Unknown}.
     *
     * <blockquote><pre>
     * Atom a = AtomBuilder.aliphatic("C")
     *                     .build();
     * </pre></blockquote>
     *
     * @param symbol symbol of an element
     * @return an atom builder to configure additional properties
     * @throws NullPointerException the element was null
     */
    public static AtomBuilder aliphatic(String symbol) {
        if (symbol == null)
            throw new NullPointerException("no symbol provided");
        return aliphatic(ofSymbolOrUnknown(symbol));
    }

    /**
     * Start building an aromatic atom of the given element symbol. If an
     * element of the symbol could not be found then the element type is set to
     * {@link Element#Unknown}.
     *
     * <blockquote><pre>
     * Atom a = AtomBuilder.aromatic("C")
     *                     .build();
     * </pre></blockquote>
     *
     * @param symbol symbol of an element
     * @return an atom builder to configure additional properties
     * @throws NullPointerException     the element was null
     * @throws IllegalArgumentException the element cannot be aromatic
     */
    public static AtomBuilder aromatic(String symbol) {
        if (symbol == null)
            throw new NullPointerException("no symbol provided");
        return aromatic(ofSymbolOrUnknown(symbol));
    }


    /**
     * Start building an aliphatic or aromatic atom of the given element symbol.
     * If an element of the symbol could not be found then the element type is
     * set to {@link Element#Unknown}.
     *
     * <blockquote><pre>
     * Atom a = AtomBuilder.create("C") // aliphatic
     *                     .build();
     * Atom a = AtomBuilder.create("c") // aromatic
     *                     .build();
     * </pre></blockquote>
     *
     * @param symbol symbol of an element - lower case indicates the atom should
     *               be aromatic
     * @return an atom builder to configure additional properties
     * @throws NullPointerException     the element was null
     * @throws IllegalArgumentException the element cannot be aromatic
     */
    public static AtomBuilder create(String symbol) {
        Element e = ofSymbolOrUnknown(symbol);
        if (symbol != null
                && !symbol.isEmpty()
                && Character.isLowerCase(symbol.charAt(0))) {
            if (!e.aromatic())
                throw new IllegalArgumentException("Attempting to create an aromatic atom for an element which cannot be aromatic");
            return new AtomBuilder(e, true);
        }
        return new AtomBuilder(e, false);
    }

    /**
     * Get the element of the given symbol - if no symbol is found then the
     * {@link Element#Unknown} is returned.
     *
     * @param symbol an atom symbol
     * @return the element of the given symbol (or unknown)
     */
    private static Element ofSymbolOrUnknown(String symbol) {
        Element e = Element.ofSymbol(symbol);
        return e != null ? e : Element.Unknown;
    }

    /**
     * Assign the given hydrogen count to the atom which will be created.
     *
     * @param hCount number of hydrogens
     * @return an atom builder to configure additional properties
     */
    public AtomBuilder hydrogens(int hCount) {
        if (hCount < 0)
            throw new IllegalArgumentException("the number of hydrogens must be positive");
        this.hCount = hCount;
        return this;
    }

    /**
     * Assign the given formal charge to the atom which will be created.
     *
     * @param charge formal-charge
     * @return an atom builder to configure additional properties
     */
    public AtomBuilder charge(int charge) {
        this.charge = charge;
        return this;
    }

    /**
     * Assign a formal-charge of -1 to the atom which will be created.
     *
     * @return an atom builder to configure additional properties
     */
    public AtomBuilder anion() {
        return charge(-1);
    }

    /**
     * Assign a formal-charge of +1 to the atom which will be created.
     *
     * @return an atom builder to configure additional properties
     */
    public AtomBuilder cation() {
        return charge(+1);
    }

    /**
     * Assign the isotope number to the atom which will be created. An isotope
     * number of '-1' means unspecified (default).
     *
     * @param isotope isotope number &ge; 0.
     * @return an atom builder to configure additional properties
     */
    public AtomBuilder isotope(int isotope) {
        this.isotope = isotope;
        return this;
    }

    /**
     * Assign the atom class to the atom which will be created. A class of '0'
     * means unspecified (default).
     *
     * @param c atom class 1..n
     * @return an atom builder to configure additional properties
     * @throws IllegalArgumentException the atom class was negative
     */
    public AtomBuilder atomClass(int c) {
        if (c < 0)
            throw new IllegalArgumentException("atom class must be positive");
        this.atomClass = c;
        return this;
    }

    /**
     * Create the atom with the configured attributed.
     *
     * @return an atom
     */
    public Atom build() {
        return new AtomImpl.BracketAtom(isotope,
                                        element,
                                        hCount,
                                        charge,
                                        atomClass,
                                        aromatic);
    }

    /**
     * Access an atom implementation which can be used for all explicit
     * hydrogens.
     *
     * @return an explicit hydrogen to be used in assembly molecules
     */
    public static Atom explicitHydrogen() {
        return AtomImpl.EXPLICIT_HYDROGEN;
    }
}
