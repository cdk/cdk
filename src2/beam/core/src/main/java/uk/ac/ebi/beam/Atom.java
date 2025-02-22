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
 * Defines properties of a atom that can be encoded in SMILES. Atoms can be
 * built using the {@link AtomBuilder} class.
 *
 * @author John May
 * @see AtomBuilder
 */
public interface Atom {

    /**
     * The isotope number of the atom. If the isotope is undefined (default) a
     * value -1 is returned.
     *
     * @return isotope number
     */
    int isotope();

    /**
     * The element of the atom.
     *
     * @return element
     */
    Element element();

    /**
     * An label attached to an element (input only). Although invalid via the
     * specification 'CCC[R]' etc can occur in the 'wild'. If found the parser
     * provides an 'Unknown' element and a specified label. Not the labels are
     * never written. By default the label is the element symbol.
     * 
     * @return the label in a bracket atom
     */
    String label();

    /**
     * Whether this atom is aromatic.
     *
     * @return atom is aromatic (true) or aliphatic (false)
     */
    boolean aromatic();

    /**
     * Formal charge of the atom.
     *
     * @return formal charge
     */
    int charge();

    /**
     * Number of hydrogens this atom has. This value defines atoms with an
     * explicit hydrogen count of bracket atoms (e.g. [CH4]).
     *
     * @return hydrogen count
     * @throws IllegalArgumentException thrown if element is part of the organic
     *                                  subset and the number of hydrogens is
     *                                  implied by the bond order sum.
     */
    int hydrogens();

    /**
     * The class of the atom is defined as an integer value. The atom class is
     * specified for bracketed atoms and is prefixed by a colon.
     *
     * <blockquote><pre>
     *     [CH:1](C)([C])[H:2]
     * </pre></blockquote>
     *
     * @return class
     */
    int atomClass();

    /**
     * (internal) Is the atom a member of the organic (aromatic/aliphatic)
     * subset implementation?
     *
     * @return whether the atom is a subset - implementation
     */
    boolean subset();

    /**
     * Access an aromatic form of this atom. If the element can not be aromatic
     * then the same atom is returned.
     *
     * @return the aromatic form of this atom (or if it can't be aromatic just
     *         this atom)
     */
    Atom toAromatic();

    /**
     * Access an aliphatic form of this atom. 
     *
     * @return the aliphatic form of this atom
     */
    Atom toAliphatic();

    /**
     * (internal) the number of hydrogens this atom would have if it were vertex
     * 'u' in the graph 'g'. If the atom is in the organic subset the value is
     * computed - otherwise the labelled hydrogen count is returned.
     *
     * @see Graph#implHCount(int)
     */
    int hydrogens(Graph g, int u);

    /**
     * (internal) The token to write for the atom when generating a SMILES
     * string.
     *
     * @return the atom token
     */
    Generator.AtomToken token();
}
