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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Internal atom implementations.
 *
 * @author John May
 */
final class AtomImpl {

    static enum AliphaticSubset implements Atom {
        Any(Element.Unknown),
        Boron(Element.Boron),
        Carbon(Element.Carbon),
        Nitrogen(Element.Nitrogen),
        Oxygen(Element.Oxygen),
        Sulfur(Element.Sulfur),
        Phosphorus(Element.Phosphorus),
        Fluorine(Element.Fluorine),
        Chlorine(Element.Chlorine),
        Bromine(Element.Bromine),
        Iodine(Element.Iodine);

        private Element             element;
        private Generator.AtomToken token;

        private static final Map<Element, Atom> atoms = new HashMap<Element, Atom>();

        static {
            for (Atom a : values())
                atoms.put(a.element(), a);
        }

        private AliphaticSubset(Element element) {
            this.element = element;
            this.token = new Generator.SubsetToken(element.symbol());
        }

        @Override public int isotope() {
            return -1;
        }

        @Override public Element element() {
            return element;
        }

        @Override public String label() {
            return element.symbol();
        }

        @Override public boolean aromatic() {
            return false;
        }

        @Override public int charge() {
            return 0;
        }

        @Override public int hydrogens() {
            throw new IllegalArgumentException("use bond order sum to determine implicit hydrogen count");
        }

        @Override public int atomClass() {
            return 0;
        }

        @Override public boolean subset() {
            return true;
        }

        @Override public Atom toAromatic() {
            return element.aromatic() ? AromaticSubset.ofElement(element) : this;
        }

        @Override public Atom toAliphatic() {
            return this;
        }

        @Override public int hydrogens(Graph g, int u) {
            return Element.implicitHydrogenCount(element, g.bondedValence(u));
        }

        @Override public Generator.AtomToken token() {
            return token;
        }

        static Atom ofElement(Element e) {
            Atom a = atoms.get(e);
            if (a == null)
                throw new IllegalArgumentException(e + "can not be an aliphatic subset atom");
            return a;
        }
    }

    static enum AromaticSubset implements Atom {
        Any(Element.Unknown),
        Boron(Element.Boron),
        Carbon(Element.Carbon),
        Nitrogen(Element.Nitrogen),
        Oxygen(Element.Oxygen),
        Sulfur(Element.Sulfur),
        Phosphorus(Element.Phosphorus);

        private       Element             element;
        private final Generator.AtomToken token;

        private static final Map<Element, Atom> atoms = new HashMap<Element, Atom>();

        static {
            for (Atom a : values())
                atoms.put(a.element(), a);
        }

        private AromaticSubset(Element element) {
            this.element = element;
            this.token = new Generator.SubsetToken(element.symbol()
                                                          .toLowerCase(Locale.ENGLISH));
        }

        @Override public String label() {
            return element.symbol();
        }

        @Override public int isotope() {
            return -1;
        }

        @Override public Element element() {
            return element;
        }

        @Override public boolean aromatic() {
            return true;
        }

        @Override public int charge() {
            return 0;
        }

        @Override public int hydrogens() {
            throw new IllegalArgumentException("use bond order sum to determine implicit hydrogen count");
        }

        @Override public int atomClass() {
            return 0;
        }

        @Override public Generator.AtomToken token() {
            return token;
        }

        @Override public boolean subset() {
            return true;
        }

        @Override public Atom toAromatic() {
            return this;
        }

        @Override public Atom toAliphatic() {
            return AliphaticSubset.ofElement(element);
        }

        @Override public int hydrogens(Graph g, int u) {
            int v = g.bondedValence(u);
            
            // no double, triple or quadruple bonds - then for aromatic atoms
            // we increase the bond order sum by '1'
            if (v == g.degree(u))
                return Element.implicitAromHydrogenCount(element, v + 1);

            // note: we only check first valence
            return Element.implicitAromHydrogenCount(element, v);
        }


        static Atom ofElement(Element e) {
            Atom a = atoms.get(e);
            if (a == null)
                throw new IllegalArgumentException(e + "can not be an aromatic subset atom");
            return a;
        }
    }

    static class BracketAtom implements Atom {
        private final Element element;
        private final int     hCount, charge, atomClass, isotope;
        private final boolean aromatic;
        private final String  label;

        public BracketAtom(int isotope, Element element, int hCount, int charge, int atomClass, boolean aromatic) {
            this(isotope, element, element.symbol(), hCount, charge, atomClass, aromatic);
        }
        
        public BracketAtom(int isotope, Element element, String label, int hCount, int charge, int atomClass, boolean aromatic) {
            this.element = element;
            this.label  = label;
            this.hCount = hCount;
            this.charge = charge;
            this.atomClass = atomClass;
            this.isotope = isotope;
            this.aromatic = aromatic;
        }

        public BracketAtom(Element element, int hCount, int charge) {
            this(-1, element, hCount, charge, 0, false);
        }

        public BracketAtom(String label) {
            this(-1, Element.Unknown, label, 0, 0, 0, false);
        }

        @Override public int isotope() {
            return isotope;
        }

        @Override public Element element() {
            return element;
        }

        @Override public boolean aromatic() {
            return aromatic;
        }

        @Override public int charge() {
            return charge;
        }

        @Override public String label() {
            return label;
        }

        @Override public int hydrogens() {
            return hCount;
        }

        @Override public int atomClass() {
            return atomClass;
        }

        @Override public Generator.AtomToken token() {
            return new Generator.BracketToken(this);
        }

        @Override public boolean subset() {
            return false;
        }

        @Override public int hydrogens(Graph g, int u) {
            return hydrogens();
        }

        @Override public Atom toAromatic() {
            return aromatic || !element.aromatic() ? this
                                                   : new BracketAtom(isotope,
                                                                     element,
                                                                     label,
                                                                     hCount,
                                                                     charge,
                                                                     atomClass,
                                                                     true);
        }

        @Override public Atom toAliphatic() {
            return !aromatic ? this 
                             : new BracketAtom(isotope,
                                               element,
                                               label,
                                               hCount,
                                               charge,
                                               atomClass,
                                               false);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BracketAtom that = (BracketAtom) o;

            if (aromatic != that.aromatic) return false;
            if (atomClass != that.atomClass) return false;
            if (charge != that.charge) return false;
            if (hCount != that.hCount) return false;
            if (isotope != that.isotope) return false;
            if (element != that.element) return false;
            if (!label.equals(that.label)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = element != null ? element.hashCode() : 0;
            result = 31 * result + hCount;
            result = 31 * result + charge;
            result = 31 * result + atomClass;
            result = 31 * result + isotope;
            result = 31 * result + (aromatic ? 1 : 0);
            return result;
        }

        @Override public String toString() {
            return "[" + isotope + element.symbol() + "H" + hCount + (
                    charge != 0 ? charge : "") + ":" + atomClass + "]" + (!label.equals(element.symbol()) ? "(" + label + ")" : "");
        }
    }

    static Atom EXPLICIT_HYDROGEN = new BracketAtom(Element.Hydrogen, 0, 0);

    static Atom DEUTERIUM = AtomBuilder.aliphatic(Element.Hydrogen)
                                       .isotope(2)
                                       .build();

    static Atom TRITIUM = AtomBuilder.aliphatic(Element.Hydrogen)
                                     .isotope(3)
                                     .build();

}
