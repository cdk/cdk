/* Copyright (c) 2014  Collaborative Drug Discovery, Inc. <alex@collaborativedrug.com>
 *               2014  Mark B Vine (orcid:0000-0002-7794-0426)
 *
 * Implemented by Alex M. Clark, produced by Collaborative Drug Discovery, Inc.
 * Made available to the CDK community under the terms of the GNU LGPL.
 *
 *    http://collaborativedrug.com
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

package org.openscience.cdk.qsar.descriptors.molecular;

import java.util.ArrayList;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;

/**
 *
 * Small ring descriptors: these are based on enumeration of all the small rings (sizes 3 to 9) in a molecule,
 * which can be obtained quickly and deterministically.
 *
 * @cdk.module qsarmolecular
 * @cdk.githash
 *
 * @cdk.dictref qsar-descriptors:smallrings
 * @cdk.keyword smallrings
 * @cdk.keyword descriptor
*/
public class SmallRingDescriptor implements IMolecularDescriptor {

    private static final String[] NAMES = {"nSmallRings", // total number of small rings (of size 3 through 9)
            "nAromRings", // total number of small aromatic rings
            "nRingBlocks", // total number of distinct ring blocks
            "nAromBlocks", // total number of "aromatically connected components"
            "nRings3", "nRings4", "nRings5", "nRings6", "nRings7", "nRings8", "nRings9" // individual breakdown of small rings
                                        };

    private IAtomContainer        mol;
    private int[][]               atomAdj, bondAdj; // precalculated adjacencies
    private int[]                 ringBlock;       // ring block identifier; 0=not in a ring
    private int[][]               smallRings;      // all rings of size 3 through 7
    private int[]                 bondOrder;       // numeric bond order for easy reference
    private boolean[]             bondArom; // aromaticity precalculated
    private boolean[]             piAtom;            // true for all atoms involved in a double bond
    private int[]                 implicitH;         // hydrogens in addition to those encoded

    public SmallRingDescriptor() {}

    @Override
    public void initialise(IChemObjectBuilder builder) {}

    /**
     * Fetch descriptor specification.
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#smallRings", this.getClass()
                        .getName(), "The Chemistry Development Kit");
    }

    /**
     * Set parameters: ignored, there are none.
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {}

    /**
     * Get parameters: returns empty array, there are none.
     */
    @Override
    public Object[] getParameters() {
        return new Object[0];
    }

    /**
     * Returns the names of the descriptors made available by this class.
     */
    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    /**
     * Returns a placeholder with the descriptor size and type.
     */
    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new IntegerArrayResult(NAMES.length);
    }

    /**
     * Get parameters: empty, there are none.
     */
    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    /**
     * Parameter types: there aren't any.
     */
    @Override
    public Object getParameterType(String name) {
        return true;
    }

    /**
     * Performs the calculation: the graph will be analyzed and ring information will be determined and wrapped
     * up into descriptors.
     *
     * @param mol the atoms and bonds that make up the molecular object
     * @return the various ring-based descriptors generated
     */
    @Override
    public DescriptorValue calculate(IAtomContainer mol) {
        this.mol = mol;
        excavateMolecule();

        int nSmallRings = smallRings.length;
        int nAromRings = 0;
        int nRingBlocks = 0;
        int nAromBlocks = countAromaticComponents();
        int nRings3 = 0, nRings4 = 0, nRings5 = 0, nRings6 = 0, nRings7 = 0, nRings8 = 0, nRings9 = 0;

        // count up the rings individually
        for (int[] r : smallRings) {
            final int sz = r.length;
            if (sz == 3)
                nRings3++;
            else if (sz == 4)
                nRings4++;
            else if (sz == 5)
                nRings5++;
            else if (sz == 6)
                nRings6++;
            else if (sz == 7)
                nRings7++;
            else if (sz == 8)
                nRings8++;
            else if (sz == 9) nRings9++;

            boolean aromatic = true;
            for (int n = 0; n < r.length; n++)
                if (!bondArom[findBond(r[n], r[n < sz - 1 ? n + 1 : 0])]) {
                    aromatic = false;
                    break;
                }
            if (aromatic) nAromRings++;
        }

        // # of ring blocks: the highest identifier is the total number of ring systems (0=not in a ring block)
        for (int n = ringBlock.length - 1; n >= 0; n--)
            nRingBlocks = Math.max(nRingBlocks, ringBlock[n]);

        IntegerArrayResult result = new IntegerArrayResult();
        result.add(nSmallRings);
        result.add(nAromRings);
        result.add(nRingBlocks);
        result.add(nAromBlocks);
        result.add(nRings3);
        result.add(nRings4);
        result.add(nRings5);
        result.add(nRings6);
        result.add(nRings7);
        result.add(nRings8);
        result.add(nRings9);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), result, NAMES);
    }

    // analyze the molecule graph, and build up the desired properties
    private void excavateMolecule() {
        final int na = mol.getAtomCount(), nb = mol.getBondCount();

        // build up an index-based neighbour/edge graph
        atomAdj = new int[na][];
        bondAdj = new int[na][];
        bondOrder = new int[nb];
        for (int n = 0; n < mol.getBondCount(); n++) {
            IBond bond = mol.getBond(n);
            if (bond.getAtomCount() != 2) continue; // biconnected bonds only
            int a1 = mol.indexOf(bond.getBegin()), a2 = mol.indexOf(bond.getEnd());

            atomAdj[a1] = appendInteger(atomAdj[a1], a2);
            bondAdj[a1] = appendInteger(bondAdj[a1], n);
            atomAdj[a2] = appendInteger(atomAdj[a2], a1);
            bondAdj[a2] = appendInteger(bondAdj[a2], n);
            if (bond.getOrder() == IBond.Order.SINGLE)
                bondOrder[n] = 1;
            else if (bond.getOrder() == IBond.Order.DOUBLE)
                bondOrder[n] = 2;
            else if (bond.getOrder() == IBond.Order.TRIPLE)
                bondOrder[n] = 3;
            else if (bond.getOrder() == IBond.Order.QUADRUPLE) bondOrder[n] = 4;
            // (look for zero-order bonds later on)
        }
        for (int n = 0; n < na; n++)
            if (atomAdj[n] == null) {
                atomAdj[n] = new int[0];
                bondAdj[n] = atomAdj[n];
            }

        // calculate implicit hydrogens, using a very conservative formula
        implicitH = new int[na];
        final String[] HYVALENCE_EL = {"C", "N", "O", "S", "P"};
        final int[] HYVALENCE_VAL = {4, 3, 2, 2, 3};
        for (int n = 0; n < na; n++) {
            IAtom atom = mol.getAtom(n);
            String el = atom.getSymbol();
            int hy = 0;
            for (int i = 0; i < HYVALENCE_EL.length; i++)
                if (el.equals(HYVALENCE_EL[i])) {
                    hy = HYVALENCE_VAL[i];
                    break;
                }
            if (hy == 0) continue;
            int ch = atom.getFormalCharge();
            if (el.equals("C")) ch = -Math.abs(ch);
            final int unpaired = 0; // (not current available, maybe introduce later)
            hy += ch - unpaired;
            for (int i = 0; i < bondAdj[n].length; i++)
                hy -= bondOrder[bondAdj[n][i]];
            implicitH[n] = Math.max(0, hy);
        }

        markRingBlocks();

        ArrayList<int[]> rings = new ArrayList<>();
        for (int rsz = 3; rsz <= 7; rsz++) {
            int[] path = new int[rsz];
            for (int n = 0; n < na; n++)
                if (ringBlock[n] > 0) {
                    path[0] = n;
                    recursiveRingFind(path, 1, rsz, ringBlock[n], rings);
                }
        }
        smallRings = rings.toArray(new int[rings.size()][]);

        detectStrictAromaticity();
        detectRelaxedAromaticity();
    }

    // assign a ring block ID to each atom (0=not in ring)
    private void markRingBlocks() {
        final int na = mol.getAtomCount();
        ringBlock = new int[na];

        boolean visited[] = new boolean[na];

        int path[] = new int[na + 1], plen = 0;
        while (true) {
            int last, current;

            if (plen == 0) // find an element of a new component to visit
            {
                last = -1;
                for (current = 0; current < na && visited[current]; current++) {
                }
                if (current >= na) break;
            } else {
                last = path[plen - 1];
                current = -1;
                for (int n = 0; n < atomAdj[last].length; n++)
                    if (!visited[atomAdj[last][n]]) {
                        current = atomAdj[last][n];
                        break;
                    }
            }

            if (current >= 0 && plen >= 2) // path is at least 2 items long, so look for any not-previous visited neighbours
            {
                int back = path[plen - 1];
                for (int n = 0; n < atomAdj[current].length; n++) {
                    int join = atomAdj[current][n];
                    if (join != back && visited[join]) {
                        path[plen] = current;
                        for (int i = plen; i == plen || path[i + 1] != join; i--) {
                            int id = ringBlock[path[i]];
                            if (id == 0)
                                ringBlock[path[i]] = last;
                            else if (id != last) {
                                for (int j = 0; j < na; j++)
                                    if (ringBlock[j] == id) ringBlock[j] = last;
                            }
                        }
                    }
                }
            }
            if (current >= 0) // can mark the new one as visited
            {
                visited[current] = true;
                path[plen++] = current;
            } else // otherwise, found nothing and must rewind the path
            {
                plen--;
            }
        }

        // the ring ID's are not necessarily consecutive, so reassign them to 0=none, 1..NBlocks
        int nextID = 0;
        for (int i = 0; i < na; i++)
            if (ringBlock[i] > 0) {
                nextID--;
                for (int j = na - 1; j >= i; j--)
                    if (ringBlock[j] == ringBlock[i]) ringBlock[j] = nextID;
            }
        for (int i = 0; i < na; i++)
            ringBlock[i] = -ringBlock[i];
    }

    // hunt for ring recursively: start with a partially defined path, and go exploring
    private void recursiveRingFind(int[] path, int psize, int capacity, int rblk, ArrayList<int[]> rings) {
        // not enough atoms yet, so look for new possibilities
        if (psize < capacity) {
            int last = path[psize - 1];
            for (int n = 0; n < atomAdj[last].length; n++) {
                int adj = atomAdj[last][n];
                if (ringBlock[adj] != rblk) continue;
                boolean fnd = false;
                for (int i = 0; i < psize; i++)
                    if (path[i] == adj) {
                        fnd = true;
                        break;
                    }
                if (!fnd) {
                    int newPath[] = new int[capacity];
                    if (psize >= 0)
                        System.arraycopy(path, 0, newPath, 0, psize);
                    newPath[psize] = adj;
                    recursiveRingFind(newPath, psize + 1, capacity, rblk, rings);
                }
            }
            return;
        }

        // path is full, so make sure it eats its tail
        int last = path[psize - 1];
        boolean fnd = false;
        for (int n = 0; n < atomAdj[last].length; n++)
            if (atomAdj[last][n] == path[0]) {
                fnd = true;
                break;
            }
        if (!fnd) return;

        // make sure every element in the path has exactly 2 neighbours within the path; otherwise it is spanning a bridge, which
        // is an undesirable ring definition
        for (int aPath : path) {
            int count = 0;
            for (int i = 0; i < atomAdj[aPath].length; i++)
                for (int aPath1 : path)
                    if (atomAdj[aPath][i] == aPath1) {
                        count++;
                        break;
                    }
            if (count != 2) return; // invalid
        }

        // reorder the array (there are 2N possible ordered permutations) then look for duplicates
        int first = 0;
        for (int n = 1; n < psize; n++)
            if (path[n] < path[first]) first = n;
        int fm = (first - 1 + psize) % psize, fp = (first + 1) % psize;
        boolean flip = path[fm] < path[fp];
        if (first != 0 || flip) {
            int newPath[] = new int[psize];
            for (int n = 0; n < psize; n++)
                newPath[n] = path[(first + (flip ? psize - n : n)) % psize];
            path = newPath;
        }

        for (int[] look : rings) {
            boolean same = true;
            for (int i = 0; i < psize; i++)
                if (look[i] != path[i]) {
                    same = false;
                    break;
                }
            if (same) return;
        }

        rings.add(path);
    }

    // aromaticity detection: uses a very narrowly defined algorithm, which detects 6-membered rings with alternating double bonds;
    // rings that are chained together (e.g. anthracene) will also be detected by the extended followup; note that this will NOT mark
    // rings such as thiophene, imidazolium, porphyrins, etc.: these systems will be left in their original single/double bond form
    private void detectStrictAromaticity() {
        final int na = mol.getAtomCount(), nb = mol.getBondCount();
        bondArom = new boolean[nb];

        if (smallRings.length == 0) return;

        piAtom = new boolean[na];
        for (int n = 0; n < nb; n++)
            if (bondOrder[n] == 2) {
                IBond bond = mol.getBond(n);
                piAtom[mol.indexOf(bond.getBegin())] = true;
                piAtom[mol.indexOf(bond.getEnd())] = true;
            }

        ArrayList<int[]> maybe = new ArrayList<>(); // rings which may yet be aromatic
        for (int[] r : smallRings)
            if (r.length == 6) {
                boolean consider = true;
                for (int n = 0; n < 6; n++) {
                    final int a = r[n];
                    if (!piAtom[a]) {
                        consider = false;
                        break;
                    }
                    int b = findBond(a, r[n == 5 ? 0 : n + 1]);
                    if (bondOrder[b] != 1 && bondOrder[b] != 2) {
                        consider = false;
                        break;
                    }
                }
                if (consider) maybe.add(r);
            }

        // keep classifying rings as aromatic until no change; this needs to be done iteratively, for the benefit of highly
        // embedded ring systems, that can't be classified as aromatic until it is known that their neighbours obviously are
        while (true) {
            boolean anyChange = false;

            for (int n = maybe.size() - 1; n >= 0; n--) {
                int[] r = maybe.get(n);
                boolean phase1 = true, phase2 = true; // has to go 121212 or 212121; already arom=either is OK
                for (int i = 0; i < 6; i++) {
                    int b = findBond(r[i], r[i == 5 ? 0 : i + 1]);
                    if (bondArom[b]) continue; // valid for either phase
                    phase1 = phase1 && bondOrder[b] == (2 - (i & 1));
                    phase2 = phase2 && bondOrder[b] == (1 + (i & 1));
                }
                if (!phase1 && !phase2) continue;

                // the ring is deemed aromatic: mark the flags and remove from the maybe list
                for (int i = 0; i < r.length; i++) {
                    bondArom[findBond(r[i], r[i == 5 ? 0 : i + 1])] = true;
                }
                maybe.remove(n);
                anyChange = true;
            }

            if (!anyChange) break;
        }
    }

    // supplement the original 'strict' definition of aromaticity with a more inclusive kind, which includes lone pairs
    private void detectRelaxedAromaticity() {
        final int na = mol.getAtomCount(), nb = mol.getBondCount();

        final int[] ELEMENT_BLOCKS = {0, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 1, 1, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 1, 1, 4, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 1, 1, 4, 4, 4, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
        final int[] ELEMENT_VALENCE = {0, 1, 2, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 10, 11, 12, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 3, 4, 5, 6, 7, 8, 1, 2, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 3, 4, 5, 6, 7, 8, 1, 1, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        // figure out which atoms have a lone pair which is considered valid for aromaticity: if electrons[i]>=2, then it qualifies
        int[] electrons = new int[na];
        for (int n = 0; n < na; n++) {
            IAtom atom = mol.getAtom(n);
            int atno = atom.getAtomicNumber();
            electrons[n] = (ELEMENT_BLOCKS[atno] == 2 ? ELEMENT_VALENCE[atno] : 0) - atom.getFormalCharge()
                    - implicitH[n];
        }
        for (int n = 0; n < nb; n++)
            if (bondOrder[n] > 0) {
                IBond bond = mol.getBond(n);
                electrons[mol.indexOf(bond.getBegin())] -= bondOrder[n];
                electrons[mol.indexOf(bond.getEnd())] -= bondOrder[n];
            }

        // pull out all of the small rings that could be upgraded to aromatic
        ArrayList<int[]> rings = new ArrayList<>();
        for (int[] r : smallRings)
            if (r.length <= 7) {
                boolean alreadyArom = true, isInvalid = false;
                for (int n = 0; n < r.length; n++) {
                    if (!piAtom[r[n]] && electrons[r[n]] < 2) {
                        isInvalid = true;
                        break;
                    }
                    int b = findBond(r[n], r[n < r.length - 1 ? n + 1 : 0]);
                    int bo = bondOrder[b];
                    if (bo != 1 && bo != 2) {
                        isInvalid = true;
                        break;
                    }
                    alreadyArom = alreadyArom && bondArom[b];
                }
                if (!alreadyArom && !isInvalid) rings.add(r);
            }

        // keep processing rings, until no new ones are found
        while (rings.size() > 0) {
            boolean anyChange = false;

            for (int n = 0; n < rings.size(); n++) {
                int[] r = rings.get(n);
                int pairs = 0, maybe = 0;
                for (int i = 0; i < r.length; i++) {
                    int a = r[i];
                    int b1 = findBond(r[i], r[i < r.length - 1 ? i + 1 : 0]);
                    int b2 = findBond(r[i], r[i > 0 ? i - 1 : r.length - 1]);
                    if (bondArom[b1])
                        maybe += 2;
                    else if (bondOrder[b1] == 2)
                        pairs += 2;
                    else if (electrons[a] >= 2 && bondOrder[b2] != 2) pairs += 2;
                }

                // see if there's anything Hueckel (4N+2) buried in there
                boolean arom = false;
                while (maybe >= 0) {
                    if ((pairs + maybe - 2) % 4 == 0) {
                        arom = true;
                        break;
                    }
                    maybe -= 2;
                }
                if (arom) {
                    for (int i = 0; i < r.length; i++) {
                        int a = r[i], b = findBond(r[i], r[i < r.length - 1 ? i + 1 : 0]);
                        bondArom[b] = true;
                    }
                    rings.remove(n);
                    n--;
                    anyChange = true;
                }
            }

            if (!anyChange) break;
        }
    }

    // rebuild the graph using only aromatic bonds, and count the number of non-singleton connected components
    private int countAromaticComponents() {
        final int na = mol.getAtomCount();
        int[][] graph = new int[na][];
        for (int n = 0; n < na; n++) {
            for (int i = 0; i < atomAdj[n].length; i++)
                if (bondArom[bondAdj[n][i]]) graph[n] = appendInteger(graph[n], atomAdj[n][i]);
        }

        final int[] cc = new int[na]; // -1=isolated, so ignore; 0=unassigned; >0=contained in a component
        int first = -1, high = 1;
        for (int n = 0; n < na; n++) {
            if (graph[n] == null)
                cc[n] = -1;
            else if (first < 0) {
                first = n;
                cc[n] = 1;
            }
        }
        if (first < 0) return 0; // all isolated

        while (true) {
            while (first < na && (cc[first] > 0 || cc[first] < 0)) {
                first++;
            }
            if (first >= na) break;

            boolean anything = false;
            for (int i = first; i < na; i++)
                if (cc[i] == 0) {
                    for (int j = 0; j < graph[i].length; j++) {
                        if (cc[graph[i][j]] != 0) {
                            cc[i] = cc[graph[i][j]];
                            anything = true;
                        }
                    }
                }
            if (!anything) cc[first] = ++high;
        }
        return high;
    }

    // convenience function for concatenating an integer
    private int[] appendInteger(int[] a, int v) {
        if (a == null || a.length == 0) return new int[]{v};
        int[] b = new int[a.length + 1];
        System.arraycopy(a, 0, b, 0, a.length);
        b[a.length] = v;
        return b;
    }

    // convenience: scans the atom adjacency to grab the bond index
    private int findBond(int a1, int a2) {
        for (int n = atomAdj[a1].length - 1; n >= 0; n--)
            if (atomAdj[a1][n] == a2) return bondAdj[a1][n];
        return -1;
    }

}
