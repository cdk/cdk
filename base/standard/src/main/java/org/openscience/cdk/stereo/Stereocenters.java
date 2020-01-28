/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.stereo;

import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.invariant.Canon;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.ringsearch.RingSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;

/**
 * Find atoms which can support stereo chemistry based on the connectivity.
 * Stereocenters are classified as <i>True</i> when they have constitutionally
 * different ligands and <i>Para</i> ("resemble") stereo centers with
 * constitutionally identical ligands. Some examples of para-centers
 * are listed below. Non and potential stereogenic atoms are also indicated. The
 * method partially implements the rules described by {@cdk.cite Razinger93}.
 * Para centers are identified in isolated rings (more common) but are not
 * currently found in fused systems (e.g. decalin), spiro linked 'assemblages'
 * or acyclic interdependent centers. 
 *
 * <b>Accepted Stereo Atoms</b> 
 *
 * This atoms accepted as being potentially stereogenic are those defined
 * in the InChI Technical Manual {@cdk.cite InChITechManual}. These are: 
 *
 * <b>Tetrahedral Stereochemistry:</b>
 * <ul>
 *     <li>Carbon - 4 valent, 4 sigma bonds</li>
 *     <li>Silicon - 4 valent, 4 sigma bonds</li>
 *     <li>Germanium - 4 valent, 4 sigma bonds</li>
 *     <li>Tin - 4 valent, 4 sigma bonds</li>
 *     <li>Nitrogen cation - 4 valent,4 sigma bonds</li>
 *     <li>Phosphorus cation - 4 valent, 4 sigma bonds</li>
 *     <li>Arsenic cation - 4 valent, 4 sigma bonds</li>
 *     <li>Boron anion - 4 valent, 4 sigma bonds</li>
 *     <li>Nitrogen - 5 valent, 3 sigma and 1 pi bond</li>
 *     <li>Phosphorus - 5 valent, 3 sigma and 1 pi bond</li>
 *     <li>Sulphur - 4 valent, 2 sigma and 1 pi bond</li>
 *     <li>Sulphur - 6 valent, 2 sigma and 2 pi bonds</li>
 *     <li>Sulphur Cation - 3 valent, 3 sigma bonds</li>
 *     <li>Sulphur cation - 5 valent, 3 sigma and 1 pi bond</li>
 *     <li>Selenium - 4 valent, 2 sigma and 1 pi bond</li>
 *     <li>Selenium - 6 valent, 2 sigma and 2 pi bonds</li>
 *     <li>Selenium Cation - 3 valent, 3 sigma bonds</li>
 *     <li>Selenium cation - 5 valent, 3 sigma and 1 pi bond</li>
 *     <li>Nitrogen - 3 valent, 3 sigma bonds and in a 3 member ring</li>
 * </ul>
 * <i>N, P, As, S or Se are not stereogenic if they have a terminal H neighbor
 * or if they have 2 neighbors of the same element (O, S, Se, Te, N) which
 * have at least one hydrogen. Consider: {@code P(O)(=O)(OC)OCCC}. Phosphines and
 * arsines are always stereogenic regardless of H neighbors</i>
 * 
 *
 * <b>Double Bond Stereochemistry:</b>
 * The following atoms can appear at either end of a double bond.
 * <ul>
 *     <li>Carbon - 4 valent, 2 sigma and 1 pi bond</li>
 *     <li>Silicon - 4 valent, 2 sigma and 1 pi bond</li>
 *     <li>Germanium - 4 valent, 2 sigma and 1 pi bond</li>
 *     <li>Nitrogen - 3 valent, 1 sigma and 1 pi bond</li>
 *     <li>Nitrogen cation - 4 valent, 2 sigma and 1 pi bond</li>
 * </ul>
 *
 *  <b>Examples of Para Stereocenters</b> <ul> <li>inositol - has 9 stereo
 * isomers, {@code O[C@H]1[C@H](O)[C@@H](O)[C@H](O)[C@H](O)[C@@H]1O
 * myo-inositol}</li> <li>decalin - has 2 stereo isomers, {@code
 * C1CC[C@H]2CCCC[C@H]2C1} (not currently identified)</li> <li>spiro/double-bond
 * linked ring - {@code InChI=1/C14H24/c1-11-3-7-13(8-4-11)14-9-5-12(2)6-10-14/h11-12H,3-10H2,1-2H3/b14-13-/t11-,12-}
 * (not currently identified)</li> <li>An example of a para-center not in a
 * cycle {@code C[C@@H](O)[C@H](C)[C@H](C)O} (not currently identified)</li>
 * </ul>
 *
 * It should be noted that para-centers may not actually have a configuration. A
 * simple example of this is seen that by changing the configuration of one
 * center in {@code C[C@@H](O)[C@H:1](C)[C@H](C)O} removes the central
 * configuration as the ligands are now equivalent {@code
 * C[C@@H](O)[CH:1]](C)[C@@H](C)O}
 *
 * @author John May
 * @cdk.module standard
 * @cdk.githash
 */
public final class Stereocenters {

    /** native CDK structure representation. */
    private final IAtomContainer  container;

    /** adjacency list representation for fast traversal. */
    private final int[][]         g;

    /** lookup bonds by the index of their atoms. */
    private final EdgeToBondMap   bondMap;

    /** the type of stereo center - indexed by atom. */
    private Stereocenter[]  stereocenters;

    /** the stereo elements - indexed by atom. */
    private StereoElement[] elements;

    /** basic cycle information (i.e. is atom/bond cyclic) and cycle systems. */
    private final RingSearch      ringSearch;

    private int numStereoElements;

    private boolean checkSymmetry = false;

    /**
     * Determine the stereocenter atoms in the provided container based on
     * connectivity.
     *
     * <blockquote><pre>{@code
     * IAtomContainer container = ...;
     * Stereocenters  centers   = Stereocenters.of(container);
     * for (int i = 0; i < container.getAtomCount(); i++) {
     *     if (centers.isStereocenter(i)) {
     *
     *     }
     * }
     * }</pre></blockquote>
     *
     * @param container input container
     * @return the stereocenters
     */
    public static Stereocenters of(IAtomContainer container) {
        EdgeToBondMap bondMap = EdgeToBondMap.withSpaceFor(container);
        int[][] g = GraphUtil.toAdjList(container, bondMap);
        Stereocenters stereocenters = new Stereocenters(container, g, bondMap);
        stereocenters.checkSymmetry();
        return stereocenters;
    }

    /**
     * Create a perception method for the provided container, graph
     * representation and bond map.
     *
     * @param container native CDK structure representation
     * @param graph     graph representation (adjacency list)
     * @param bondMap   fast lookup bonds by atom index
     */
    Stereocenters(IAtomContainer container, int[][] graph, EdgeToBondMap bondMap) {
        this.container = container;
        this.bondMap = bondMap;
        this.g = graph;
        this.ringSearch = new RingSearch(container, graph);
        this.elements = new StereoElement[g.length];
        this.stereocenters = new Stereocenter[g.length];
        this.numStereoElements = createElements();
    }

    void checkSymmetry() {
        if (!checkSymmetry) {
            checkSymmetry = true;
            numStereoElements = createElements();
            int[] symmetry = toIntArray(Canon.symmetry(container, g));
            labelTrueCenters(symmetry);
            labelIsolatedPara(symmetry);
        }
    }

    /**
     * Obtain the type of stereo element support for atom at index {@code v}.
     * Supported elements types are:
     *
     * <ul> <li>{@link Type#Bicoordinate} - an central atom involved in a
     * cumulated system (not yet supported)</li> <li>{@link Type#Tricoordinate}
     * - an atom at one end of a geometric (double-bond) stereo bond or
     * cumulated system.</li> <li>{@link Type#Tetracoordinate} - a tetrahedral
     * atom (could also be square planar in future)</li> <li>{@link Type#None} -
     * the atom is not a (supported) stereo element type.</li> </ul>
     *
     * @param v atom index (vertex)
     * @return the type of element
     */
    public Type elementType(final int v) {
        if (stereocenters[v] == Stereocenter.Non || elements[v] == null)
            return Type.None;
        else
            return elements[v].type;
    }

    boolean isSymmetryChecked() {
        return checkSymmetry;
    }

    /**
     * Is the atom be a stereocenter (i.e. True or Para).
     *
     * @param v atom index (vertex)
     * @return the atom at index {@code v} is a stereocenter
     */
    public boolean isStereocenter(int v) {
        return stereocenters[v] == Stereocenter.True || stereocenters[v] == Stereocenter.Para;
    }

    /**
     * Determine the type of stereocenter is the atom at index {@code v}.
     *
     * <ul> <li>{@link Stereocenter#True} - the atom has constitutionally
     * different neighbors</li> <li>{@link Stereocenter#Para} - the atom
     * resembles a stereo centre but has constitutionally equivalent neighbors
     * (e.g. inositol, decalin). The stereocenter depends on the configuration
     * of one or more stereocenters.</li> <li>{@link Stereocenter#Potential} -
     * the atom can supported stereo chemistry but has not be shown to be a true
     * or para center.</li> <li>{@link Stereocenter#Non} - the atom is not a
     * stereocenter (e.g. methane).</li> </ul>
     *
     * @param v atom index.
     * @return the type of stereocenter
     */
    public Stereocenter stereocenterType(int v) {
        return stereocenters[v];
    }

    /**
     * Create {@link org.openscience.cdk.stereo.Stereocenters.StereoElement}
     * instance for atoms which support stereochemistry. Each new element is
     * considered a potential stereo center - any atoms which have not been
     * assigned an element are non stereo centers.
     *
     * @return the number of elements created
     */
    private int createElements() {

        boolean[] tricoordinate = new boolean[g.length];
        int nElements = 0;

        // all atoms we don't define as potential are considered
        // non-stereogenic
        Arrays.fill(stereocenters, Stereocenter.Non);

        VERTICES: for (int i = 0; i < g.length; i++) {

            // determine hydrogen count, connectivity and valence
            int h = container.getAtom(i).getImplicitHydrogenCount();
            int x = g[i].length + h;
            int d = g[i].length;
            int v = h;

            if (x < 2 || x > 4 || h > 1) continue;

            int piNeighbor = 0;
            for (int w : g[i]) {
                if (atomicNumber(container.getAtom(w)) == 1 &&
                    container.getAtom(w).getMassNumber() == null)
                    h++;
                switch (bondMap.get(i, w).getOrder()) {
                    case SINGLE:
                        v++;
                        break;
                    case DOUBLE:
                        v += 2;
                        piNeighbor = w;
                        break;
                    default:
                        // triple, quadruple or unset? can't be a stereo centre
                        continue VERTICES;
                }
            }

            // check the type of stereo chemistry supported
            switch (supportedType(i, v, d, h, x)) {
                case Bicoordinate:
                    stereocenters[i] = Stereocenter.Potential;
                    elements[i] = new Bicoordinate(i, g[i]);
                    nElements++;
                    int u = g[i][0];
                    int w = g[i][1];
                    if (tricoordinate[u]) {
                        stereocenters[u] = Stereocenter.Potential;
                        elements[u] = new Tricoordinate(u, i, g[u]);
                    }
                    if (tricoordinate[w]) {
                        stereocenters[w] = Stereocenter.Potential;
                        elements[w] = new Tricoordinate(w, i, g[w]);
                    }
                    break;
                case Tricoordinate:

                    u = i;
                    w = piNeighbor;

                    tricoordinate[u] = true;

                    if (!tricoordinate[w]) {
                        if (elements[w] != null && elements[w].type == Type.Bicoordinate) {
                            stereocenters[u] = Stereocenter.Potential;
                            elements[u] = new Tricoordinate(u, w, g[u]);
                        }
                        continue;
                    }

                    stereocenters[w] = Stereocenter.Potential;
                    stereocenters[u] = Stereocenter.Potential;
                    elements[u] = new Tricoordinate(u, w, g[u]);
                    elements[w] = new Tricoordinate(w, u, g[w]);
                    nElements++;
                    break;

                case Tetracoordinate:
                    elements[i] = new Tetracoordinate(i, g[i]);
                    stereocenters[i] = Stereocenter.Potential;
                    nElements++;
                    break;

                default:
                    stereocenters[i] = Stereocenter.Non;
            }
        }

        // link up tetracoordinate atoms accross cumulate systems
        for (int v = 0; v < g.length; v++) {
            if (elements[v] != null && elements[v].type == Type.Bicoordinate) {
                int u = elements[v].neighbors[0];
                int w = elements[v].neighbors[1];
                if (elements[u] != null && elements[w] != null && elements[u].type == Type.Tricoordinate
                        && elements[w].type == Type.Tricoordinate) {
                    ((Tricoordinate) elements[u]).other = w;
                    ((Tricoordinate) elements[w]).other = u;
                }
            }
        }

        return nElements;
    }

    /**
     * Labels true stereocenters where all neighbors are constitutionally
     * different. Potential stereocenters where all constitutionally equivalent
     * neighbors are terminal (consider [C@H](C)(C)N) are also eliminated.
     *
     * @param symmetry symmetry classes of the atoms
     */
    private void labelTrueCenters(final int[] symmetry) {

        // auxiliary array, has the symmetry class already been 'visited'
        boolean[] visited = new boolean[symmetry.length + 1];

        for (int v = 0; v < g.length; v++) {
            if (stereocenters[v] == Stereocenter.Potential) {

                int[] ws = elements[v].neighbors;
                int nUnique = 0;
                boolean terminal = true;

                for (final int w : ws) {
                    if (!visited[symmetry[w]]) {
                        visited[symmetry[w]] = true;
                        nUnique++;
                    } else {
                        // is symmetric neighbor non-terminal
                        if (g[w].length > 1) terminal = false;
                    }
                }

                // reset for testing next element
                for (int w : ws)
                    visited[symmetry[w]] = false;

                // neighbors are constitutionally different
                if (nUnique == ws.length)
                    stereocenters[v] = Stereocenter.True;

                // all the symmetric neighbors are terminal then 'v' can not
                // be a stereocenter. There is an automorphism which inverts
                // only this stereocenter
                else if (terminal) stereocenters[v] = Stereocenter.Non;
            }
        }
    }

    /**
     * Labels para stereocenter in isolated rings. Any elements which are now
     * known to not be stereo centers are also eliminated.
     *
     * @param symmetry the symmetry classes of each atom
     */
    private void labelIsolatedPara(int[] symmetry) {

        // auxiliary array, has the symmetry class already been 'visited'
        boolean[] visited = new boolean[symmetry.length + 1];

        for (int[] isolated : ringSearch.isolated()) {

            List<StereoElement> potential = new ArrayList<StereoElement>();
            List<StereoElement> trueCentres = new ArrayList<StereoElement>();
            BitSet cyclic = new BitSet();

            for (int v : isolated) {
                cyclic.set(v);
                if (stereocenters[v] == Stereocenter.Potential)
                    potential.add(elements[v]);
                else if (stereocenters[v] == Stereocenter.True) trueCentres.add(elements[v]);
            }

            // there is only 1 potential and 0 true stereocenters in this cycle
            // the element is not a stereocenter
            if (potential.size() + trueCentres.size() < 2) {
                for (StereoElement element : potential)
                    stereocenters[element.focus] = Stereocenter.Non;
                continue;
            }

            List<StereoElement> paraElements = new ArrayList<StereoElement>();
            for (StereoElement element : potential) {
                if (element.type == Type.Tetracoordinate) {

                    int[] ws = element.neighbors;
                    int nUnique = 0;
                    boolean terminal = true;

                    for (int w : ws) {
                        if (!cyclic.get(w)) {
                            if (!visited[symmetry[w]]) {
                                visited[symmetry[w]] = true;
                                nUnique++;
                            } else {
                                if (g[w].length > 1) terminal = false;
                            }
                        }
                    }

                    // reset for next element
                    for (int w : ws)
                        visited[symmetry[w]] = false;

                    int deg = g[element.focus].length;

                    if (deg == 3 || (deg == 4 && nUnique == 2)) paraElements.add(element);

                    // remove those we know cannot possibly be stereocenters
                    if (deg == 4 && nUnique == 1 && terminal) stereocenters[element.focus] = Stereocenter.Non;
                } else if (element.type == Type.Tricoordinate) {
                    Tricoordinate either = (Tricoordinate) element;
                    if (stereocenters[either.other] == Stereocenter.True) paraElements.add(element);
                }
            }

            if (paraElements.size() + trueCentres.size() >= 2)
                for (StereoElement para : paraElements)
                    stereocenters[para.focus] = Stereocenter.Para;
            else
                for (StereoElement para : paraElements)
                    stereocenters[para.focus] = Stereocenter.Non;
        }
    }

    /**
     * Determine the type of stereo chemistry (if any) which could be supported
     * by the atom at index 'i'. The rules used to define the types of
     * stereochemistry are encoded from the InChI Technical Manual.
     *
     * @param i atom index
     * @param v valence
     * @param h hydrogen
     * @param x connectivity
     * @return type of stereo chemistry
     */
    private Type supportedType(int i, int v, int d, int h, int x) {

        IAtom atom = container.getAtom(i);

        // the encoding a bit daunting and to be concise short variable names
        // are used. these parameters make no distinction between implicit/
        // explicit values and allow complete (and fast) characterisation of
        // the type of stereo atom
        //
        // i: atom index
        // v: valence (bond order sum)
        // h: total hydrogen count
        // x: connected atoms
        // q: formal charge

        int q = charge(atom);

        // more than one hydrogen
        if (checkSymmetry && h > 1)
            return Type.None;

        switch (atomicNumber(atom)) {
            case 0: // stop the nulls on pseudo atoms messing up anything else
                return Type.None;
            case 5: // boron
                return q == -1 && v == 4 && x == 4 ? Type.Tetracoordinate : Type.None;

            case 6: // carbon
                if (v != 4 || q != 0) return Type.None;
                if (x == 2) return Type.Bicoordinate;
                if (x == 3) return Type.Tricoordinate;
                if (x == 4) return Type.Tetracoordinate;
                return Type.None;
            case 7: // nitrogen
                if (x == 2 && v == 3 && d == 2 && q == 0)
                    return Type.Tricoordinate;
                if (x == 3 && v == 4 && q == 1) return Type.Tricoordinate;
                if (x == 4 && h == 0 && (q == 0 && v == 5 || q == 1 && v == 4))
                    return verifyTerminalHCount(i) ? Type.Tetracoordinate : Type.None;
                // note: bridgehead not allowed by InChI but makes sense
                return x == 3 && h == 0 && (isBridgeHead(i) || inThreeMemberRing(i)) ? Type.Tetracoordinate : Type.None;

            case 14: // silicon
                if (v != 4 || q != 0) return Type.None;
                if (x == 3) return Type.Tricoordinate;
                if (x == 4) return Type.Tetracoordinate;
                return Type.None;
            case 15: // phosphorus
                if (x == 4 && (q == 0 && v == 5 && h == 0 || q == 1 && v == 4))
                    return verifyTerminalHCount(i) ? Type.Tetracoordinate : Type.None;
                // note 3 valent phosphorus not documented as accepted
                // by InChI tech manual but tests show it is
                if (x == 3 && q == 0 && v == 3 && h == 0)
                    return verifyTerminalHCount(i) ? Type.Tetracoordinate : Type.None;
            case 16: // sulphur
                if (h > 0) return Type.None;
                if (q == 0 && ((v == 4 && x == 3) || (v == 6 && x == 4)))
                    return verifyTerminalHCount(i) ? Type.Tetracoordinate : Type.None;
                if (q == 1 && ((v == 3 && x == 3) || (v == 5 && x == 4)))
                    return verifyTerminalHCount(i) ? Type.Tetracoordinate : Type.None;
                return Type.None;

            case 32: // germanium
                if (v != 4 || q != 0) return Type.None;
                if (x == 3) return Type.Tricoordinate;
                if (x == 4) return Type.Tetracoordinate;
                return Type.None;
            case 33: // arsenic
                if (x == 4 && q == 1 && v == 4) return verifyTerminalHCount(i) ? Type.Tetracoordinate : Type.None;
                return Type.None;
            case 34: // selenium
                if (h > 0) return Type.None;
                if (q == 0 && ((v == 4 && x == 3) || (v == 6 && x == 4)))
                    return verifyTerminalHCount(i) ? Type.Tetracoordinate : Type.None;
                if (q == 1 && ((v == 3 && x == 3) || (v == 5 && x == 4)))
                    return verifyTerminalHCount(i) ? Type.Tetracoordinate : Type.None;
                return Type.None;

            case 50: // tin
                return q == 0 && v == 4 && x == 4 ? Type.Tetracoordinate : Type.None;
        }

        return Type.None;
    }

    /**
     * Verify that there are is not 2 terminal heavy atoms (of the same element)
     * which have a hydrogen count > 0. This follows the InChI specification
     * that - An atom or positive ion N, P, As, S, or Se is not treated as
     * stereogenic if it has (a) A terminal H atom neighbor or (b) At least two
     * terminal neighbors, XHm and XHn, (n+m>0) connected by any kind of bond,
     * where X is O, S, Se, Te, or N. This avoids the issue that under
     * Cahn-Ingold-Prelog (or canonicalisation) the oxygens in 'P(=O)(O)(*)*'
     * would not be found to be equivalent and a parity/winding would be
     * assigned.
     *
     * @param v the vertex (atom index) to check
     * @return the atom does not have > 2 terminal neighbors with a combined
     *         hydrogen count of > 0
     */
    private boolean verifyTerminalHCount(int v) {

        if (!checkSymmetry)
            return true;

        int[] counts = new int[6];
        int[][] atoms = new int[6][g[v].length];

        boolean found = false;

        // group the 'X' neighbours we care about,
        // N=>1, O=>2, S=>3, etc, anything we don't care
        // about goes in slot 0
        for (int w : g[v]) {
            int idx = indexNeighbor(container.getAtom(w));
            atoms[idx][counts[idx]++] = w;
            found = found || (idx > 0 && counts[idx] > 1);
        }

        if (!found) return true;

        // now we have the neighbours group check out one,
        // N first, O, then S, etc and check if there is at
        // least two terminals atom and the total number of H
        // connected to these terminals is >= 1. i.e. -XHm and -XHn, (n+m>0)
        for (int i = 1; i < counts.length; i++) {
            if (counts[i] < 2) continue;

            int terminalCount  = 0;
            int terminalHCount = 0;

            for (int j = 0; j < counts[i]; j++) {
                int   explHCount = 0;
                int[] ws = g[atoms[i][j]];
                for (int w : g[atoms[i][j]]) {
                    IAtom atom = container.getAtom(w);
                    if (atomicNumber(atom) == 1 && atom.getMassNumber() == null) {
                        explHCount++;
                    }
                }

                final int degree = ws.length - explHCount;
                if (degree == 1) {
                    terminalCount++;
                    terminalHCount += explHCount;
                    IAtom atom = container.getAtom(atoms[i][j]);
                    Integer implHCount = atom.getImplicitHydrogenCount();
                    if (implHCount != null)
                        terminalHCount += implHCount;
                    // O, S, Se, Te, or N with -1 charge is equiv to having a H
                    if (atom.getFormalCharge() == -1)
                        terminalHCount++;
                }
            }

            if (terminalCount > 1 && terminalHCount > 0) return false;
        }

        return true;
    }

    /**
     * Index the atom by element to a number between 0-5. This allows us to
     * quickly count up neighbors we need to and the ignore those we don't
     * (defaults to 0).
     *
     * @param atom an atom to get the element index of
     * @return the element index
     */
    private static int indexNeighbor(IAtom atom) {
        switch (atomicNumber(atom)) {
            case 7: // N
                return 1;
            case 8: // O
                return 2;
            case 16: // S
                return 3;
            case 34: // Se
                return 4;
            case 52: // Te
                return 5;
            default:
                return 0;
        }
    }

    /**
     * Check if the {@code atom} at index {@code v} is a member of a small ring
     * (n=3). This is the only time a 3 valent nitrogen is allowed by InChI to
     * be potentially stereogenic.
     *
     * @param v atom index
     * @return the atom is a member of a 3 member ring
     */
    private boolean inThreeMemberRing(int v) {
        BitSet adj = new BitSet();
        for (int w : g[v])
            adj.set(w);
        // is a neighbors neighbor adjacent?
        for (int w : g[v])
            for (int u : g[w])
                if (adj.get(u)) return true;
        return false;
    }

    private void visitPart(boolean[] visit, IAtom atom) {
        visit[container.indexOf(atom)] = true;
        for (IBond bond : container.getConnectedBondsList(atom)) {
            IAtom nbr = bond.getOther(atom);
            if (!visit[container.indexOf(nbr)])
                visitPart(visit, nbr);
        }
    }

    /**
     * Detects if a bond is a fused-ring bond (e.g. napthalene). A bond is a
     * ring fusion if deleting it creates a new component. Or to put it another
     * way if using a flood-fill we can't visit every atom without going through
     * this bond, not we only check ring bonds.
     *
     * @param bond the bond
     * @return the bond is a fused bond
     */
    private boolean isFusedBond(IBond bond) {
        IAtom     beg    = bond.getBegin();
        IAtom     end    = bond.getEnd();
        if (getRingDegree(container.indexOf(beg)) < 3 &&
            getRingDegree(container.indexOf(end)) < 3)
            return false;
        boolean[] avisit = new boolean[container.getBondCount()];
        avisit[container.indexOf(beg)] = true;
        avisit[container.indexOf(end)] = true;
        int count = 0;
        for (IBond nbond : container.getConnectedBondsList(beg)) {
            IAtom nbr = nbond.getOther(beg);
            if (nbr.equals(end) || !ringSearch.cyclic(nbond))
                continue;
            if (count == 0) {
                count++;
                visitPart(avisit, nbr);
            } else {
                if (!avisit[container.indexOf(nbr)])
                    return true;
            }
        }
        return false;
    }

    private int getRingDegree(int v) {
        int x = 0;
        for (int w : g[v])
            if (ringSearch.cyclic(v, w))
                x++;
        return x;
    }

    private boolean isBridgeHead(int v) {
        if (getRingDegree(v) < 3)
            return false;
        IAtom atom = container.getAtom(v);
        for (IBond bond : container.getConnectedBondsList(atom))
            if (isFusedBond(bond))
                return false;
        return true;
    }

    /**
     * Safely obtain the atomic number of an atom. If the atom has undefined
     * atomic number and is not a pseudo-atom it is considered an error. Pseudo
     * atoms with undefined atomic number default to 0.
     *
     * @param a an atom
     * @return the atomic number of the atom
     */
    private static int atomicNumber(IAtom a) {
        Integer elem = a.getAtomicNumber();
        if (elem != null) return elem;
        if (a instanceof IPseudoAtom) return 0;
        throw new IllegalArgumentException("an atom had an undefieind atomic number");
    }

    /**
     * Safely obtain the formal charge on an atom. If the atom had undefined
     * formal charge it is considered as neutral (0).
     *
     * @param a an atom
     * @return the formal charge
     */
    private static int charge(IAtom a) {
        Integer chg = a.getFormalCharge();
        return chg != null ? chg : 0;
    }

    /**
     * Convert an array of long (64-bit) values to an array of (32-bit)
     * integrals.
     *
     * @param org the original array of values
     * @return the array cast to int values
     */
    private static int[] toIntArray(long[] org) {
        int[] cpy = new int[org.length];
        for (int i = 0; i < cpy.length; i++)
            cpy[i] = (int) org[i];
        return cpy;
    }

    /** Defines the type of a stereocenter. */
    public enum Stereocenter {

        /** Atom is a true stereo-centre. */
        True,

        /** Atom resembles a stereo-centre (para). */
        Para,

        /** Atom is a potential stereo-centre */
        Potential,

        /** Non stereo-centre. */
        Non
    }

    public enum Type {

        /** An atom within a cumulated system. (not yet supported) */
        Bicoordinate,

        /**
         * A potentially stereogenic atom with 3 neighbors - one atom in a
         * geometric centres or cumulated system (allene, cumulene).
         */
        Tricoordinate,

        /**
         * A potentially stereogenic atom with 4 neighbors - tetrahedral
         * centres.
         */
        Tetracoordinate,

        /** A non-stereogenic atom. */
        None
    }

    /**
     * Internal stereo element representation. We need to define the sides of a
     * double bond separately and want to avoid reflection (instanceof) by using
     * a type parameter. We also store the neighbors we need to check for
     * equivalence directly.
     */
    private static abstract class StereoElement {

        int   focus;
        int[] neighbors;
        Type  type;
    }

    /** Represents a tetrahedral stereocenter with 2 neighbors. */
    private static final class Bicoordinate extends StereoElement {

        Bicoordinate(int v, int[] neighbors) {
            this.focus = v;
            this.type = Type.Bicoordinate;
            this.neighbors = Arrays.copyOf(neighbors, neighbors.length);
        }
    }

    /** Represents a tetrahedral stereocenter with 3 or 4 neighbors. */
    private static final class Tetracoordinate extends StereoElement {

        Tetracoordinate(int v, int[] neighbors) {
            this.focus = v;
            this.type = Type.Tetracoordinate;
            this.neighbors = Arrays.copyOf(neighbors, neighbors.length);
        }
    }

    /**
     * Represents one end of a double bond. The element only stores non-double
     * bonded neighbors and also indexes it's {@code other} end.
     */
    private static final class Tricoordinate extends StereoElement {

        int other;

        /**
         * Create a tri-coordinate atom for one end of a double bond. Two
         * elements need to be created which reference each other.
         *
         * @param v         the focus of this end
         * @param w         the double bonded other end of the element
         * @param neighbors the neighbors of v
         */
        Tricoordinate(int v, int w, int[] neighbors) {
            this.focus = v;
            this.other = w;
            this.type = Type.Tricoordinate;
            this.neighbors = new int[neighbors.length - 1];
            int n = 0;

            // remove the other neighbor from neighbors when checking
            // equivalence
            for (int i = 0; i < neighbors.length; i++) {
                if (neighbors[i] != other) this.neighbors[n++] = neighbors[i];
            }
        }
    }
}
