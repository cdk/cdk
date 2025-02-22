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

import java.util.BitSet;

import static uk.ac.ebi.beam.Element.AromaticSpecification.Daylight;
import static uk.ac.ebi.beam.Element.Arsenic;
import static uk.ac.ebi.beam.Element.Carbon;
import static uk.ac.ebi.beam.Element.Nitrogen;
import static uk.ac.ebi.beam.Element.Oxygen;
import static uk.ac.ebi.beam.Element.Phosphorus;
import static uk.ac.ebi.beam.Element.Unknown;

/**
 * Defines a model to determine the number of p electrons a particular element
 * in a certain environment donates. There is no universally accepted 'correct'
 * model and different models can produce very different results.
 *
 * @author John May
 */
abstract class ElectronDonation {

    /** The Daylight model implementation. */
    private static final ElectronDonation DAYLIGHT = new Daylight();

    /**
     * The number p electrons dominated by the atom label at vertex 'u' in the
     * 'cycle' of the graph 'g'. Additionally the 'cyclic' bit set indicates the
     * vertices are members of any cycle.
     *
     * Depending on the perception method the 'cycle' may not be known in which
     * case a runtime error will be thrown if it is needed for determining the
     * donation. The cycle will be unknown if the method use the donation model
     * builds up the cycle iteratively counting the number of p electrons.
     *
     * @param u      the vertex under consideration
     * @param g      the graph the vertex is referring to
     * @param cycle  the cycle under consideration
     * @param cyclic all cyclic vertices
     * @return the number of p electrons contributed to if the element or -1 if
     *         the vertex should not be used
     */
    abstract int contribution(int u, Graph g, Cycle cycle, BitSet cyclic);

    /**
     * The Daylight aromatic model (aprox).
     *
     * @return electron donation model
     */
    static ElectronDonation daylight() {
        return DAYLIGHT;
    }

    /** The cyclic vertices. */
    interface Cycle {
        boolean contains(int u);
    }

    /**
     * Daylight donation model - interpreted from various sources and testing
     * the Daylight Depict service, http://www.daylight.com/daycgi/depict.
     */
    private static final class Daylight extends ElectronDonation {

        /** @inheritDoc */
        @Override int contribution(int u, Graph g, Cycle cycle, BitSet cyclic) {

            if (!cyclic.get(u))
                return -1;
            
            Atom    atom = g.atom(u);
            Element elem = atom.element();
        
            // the element isn't allow to be aromatic (Daylight spec)
            if (!elem.aromatic(Daylight) || elem == Unknown)
                return -1;
            
            // count cyclic and acyclic double bonds
            int nCyclic = 0, nAcyclic = 0;
            int deg = g.degree(u) + g.implHCount(u);
            Edge acyclic = null;
            int sum = 0;
            for (final Edge e : g.edges(u)) {
                sum += e.bond().order();
                if (e.bond().order() == 2) {
                    if (!cyclic.get(e.other(u))) {
                        nAcyclic++;
                        acyclic = e;
                    }
                    else {
                        nCyclic++;
                    }
                }
            }

            int charge = atom.charge();
            int valence = sum + g.implHCount(u);

            if (!atom.element().verify(valence, charge))
                return -1;
            if (deg > 3)
                return -1;
            if (nCyclic > 1)
                return -1;
            if (nCyclic == 1 && nAcyclic == 1) {
                // [P|N](=O)(=*)* - note arsenic not allowed 
                if ((elem == Nitrogen || elem == Phosphorus)
                        && g.atom(acyclic.other(u)).element() == Oxygen)
                    return 1;
                return -1;
            } else if (nCyclic == 1 && nAcyclic == 0) {
                // any element (except Arsenic) with a single cyclic double bond
                // contributes 1 p electron
                return elem != Arsenic ? 1 : -1;
            } else if (nCyclic == 0 && nAcyclic == 1) {
                // a cyclic exo-cyclic double bond - how many electrons determine
                // by acyclicContribution()
                return acyclicContribution(atom, g.atom(acyclic.other(u)), charge);
            } else if (nCyclic == 0 && nAcyclic == 0 && charge > -3) {
                // no double bonds - do we have any lone pairs to contribute?
                int v = valence(elem, charge);
                if (v - sum >= 2 && charge <= 0)
                    return 2;
                if (charge == 1 && atom.element() == Carbon)
                    return 0;
            }

            return -1;
        }

        private int valence(Element elem, int q) {
            return valence(Element.ofNumber(elem.atomicNumber() - q));           
        }

        private int valence(Element elem) {
            switch (elem) {
                case Boron:   
                case Aluminum:
                case Gallium:
                    return 3;
                case Carbon:
                case Silicon:  
                case Germanium:
                    return 4;
                case Nitrogen:
                case Phosphorus:
                case Arsenic:
                    return 5;
                case Oxygen:
                case Sulfur:
                case Selenium:
                    return 6;
                case Fluorine:
                case Chlorine:
                case Bromine: 
                    return 7;
            }
            throw new UnsupportedOperationException("Valence not yet handled for element with atomic number " + elem);
        }

        /**
         * For a 'cyclic' atom double bonded to an 'acyclic' atom how many
         * electrons should be donated?
         *
         * @param cyclic  cyclic atom
         * @param acyclic acyclic atom double bonded to the 'cyclic' atom
         * @param charge  charge on the cyclic atom
         * @return number of donated electrons
         */
        int acyclicContribution(Atom cyclic, Atom acyclic, int charge) {
            switch (cyclic.element()) {
                case Carbon:
                    // carbon bonded to any exocyclic element (other than carbon)
                    // gives 1 electron
                    return acyclic.element() != Carbon ? 0 : 1;
                case Nitrogen:
                case Phosphorus:
                    return charge == 1 ? 1 : -1;
                case Sulfur:                    
                    return charge == 0 && acyclic.element() == Oxygen ? 2 : -1;
            }
            return -1;
        }
    }
}
