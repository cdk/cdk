/* Copyright (C) 2005-2006 Markus Leber
 *               2006-2009 Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.algorithm.mcgregor;

import java.util.ArrayList;
import java.util.List;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

/**
 * Partitions source bonds for one McGregor extension step and propagates
 * mapped-atom identity labels to the corresponding graph. Input/output lists
 * are borrowed and mutated in place: integer bond records contain endpoint
 * indices and order, label records contain two endpoint labels and two backups,
 * and atom mappings contain source/target pairs in stable order.
 *
 * <p>Each mapped pair uses its own unique $-prefixed label. Extended mappings
 * must retain the seed prefix, sign arrays must cover all mapped pairs, and
 * graph/table sizes and indices must already be valid. These internal helpers
 * do not prepare chemistry, apply stereo filters or check deadlines. Their
 * mutable instances and tables are not safe for concurrent or reentrant use.</p>
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class QueryProcessor {

    private final List<String>  cTab1Copy;
    private final List<String>  cTab2Copy;
    // Mapping pairs retain their positions when extension appends new pairs.
    private final String[]      signs;
    private int           neighborBondNumA; //number of remaining molecule A bonds after the clique search, which are neighbors of the MCS_1
    private int           setBondNumA; //number of remaining molecule A bonds after the clique search, which aren't neighbors
    private final List<Integer> iBondNeighborsA;
    private final List<String>  cBondNeighborsA;
    private final int           newNeighborNumA;
    private final List<Integer> newINeighborsA;
    private final List<String>  newCNeighborsA;

    /**
     * Constructs a source bond processor over aligned borrowed tables.
     * @param cTab1Copy borrowed mutable source four-entry endpoint-label/backup records
     * @param cTab2Copy borrowed mutable target four-entry endpoint-label/backup records
     * @param signArray unique $-prefixed mapped identity labels indexed by stable atom-pair position
     * @param neighborBondnumA initial source boundary-bond count
     * @param setBondnumA initial source remaining-bond count
     * @param iBondNeighborsA mutable output source endpoint/order triples
     * @param cBondNeighborsA mutable output source endpoint-label/backup records
     * @param mappingSize number of mapped source/target atom pairs
     * @param iBondSetA mutable source remaining-bond endpoint/order triples
     * @param cBondSetA mutable source remaining-bond endpoint-label/backup records
     */
    protected QueryProcessor(List<String> cTab1Copy, List<String> cTab2Copy, String[] signArray,
            int neighborBondnumA, int setBondnumA, List<Integer> iBondNeighborsA, List<String> cBondNeighborsA,
            int mappingSize, List<Integer> iBondSetA, List<String> cBondSetA) {

        this.cTab1Copy = cTab1Copy;
        this.cTab2Copy = cTab2Copy;
        this.signs = signArray;
        this.neighborBondNumA = neighborBondnumA;
        this.setBondNumA = setBondnumA;
        this.iBondNeighborsA = iBondNeighborsA;
        this.cBondNeighborsA = cBondNeighborsA;
        this.newNeighborNumA = mappingSize;
        this.newINeighborsA = iBondSetA;
        this.newCNeighborsA = cBondSetA;
    }

    /**
     * Partitions source bonds and propagates stable mapped-atom labels.
     * @param query simple source graph, including per-atom/per-bond query predicates
     * @param target simple target graph
     * @param unmappedAtomsMolA unmapped source atom indices
     * @param mappedAtoms flattened source/target atom pairs in stable order
     * @param counter legacy compatibility value; labels use stable atom-pair positions
     */
    protected void process(IAtomContainer query, IAtomContainer target, List<Integer> unmappedAtomsMolA,
            List<Integer> mappedAtoms, int counter) {

        boolean bondConsidered = false;
        boolean normalBond = true;

        //        System.out.println("\n" + cTab1Copy + "\n");

        for (int atomIndex = 0; atomIndex < query.getBondCount(); atomIndex++) {

            Integer indexI = query.indexOf(query.getBond(atomIndex).getBegin());
            Integer indexJ = query.indexOf(query.getBond(atomIndex).getEnd());
            Integer order = query.getBond(atomIndex).getOrder() == null
                    ? 0 : query.getBond(atomIndex).getOrder().numeric();

            //            System.out.println(AtomI + "= , =" + AtomJ );
            for (Integer integer : unmappedAtomsMolA) {

                if (integer.equals(indexI)) {
                    normalBond = unMappedAtomsEqualsIndexJ(query, target, atomIndex, counter, mappedAtoms, indexI,
                            indexJ, order);
                    bondConsidered = true;
                } else //Does a ungemaptes atom at second position in the connection occur?
                    if (integer.equals(indexJ)) {
                        normalBond = unMappedAtomsEqualsIndexI(query, target, atomIndex, counter, mappedAtoms, indexI,
                                indexJ, order);
                        bondConsidered = true;
                    }
                if (normalBond && bondConsidered) {
                    markNormalBonds(atomIndex, indexI, indexJ, order);
                    normalBond = true;
                    break;
                }
            }
            bondConsidered = false;
        }
    }

    /**
     * Partitions source bonds and propagates stable mapped-atom labels.
     * @param query simple source graph, including per-atom/per-bond query predicates
     * @param target simple target graph
     * @param unmappedAtomsMolA unmapped source atom indices
     * @param mappedAtoms flattened source/target atom pairs in stable order
     * @param counter legacy compatibility value; labels use stable atom-pair positions
     */
    protected void process(IQueryAtomContainer query, IAtomContainer target, List<Integer> unmappedAtomsMolA,
            List<Integer> mappedAtoms, int counter) {

        process((IAtomContainer) query, target, unmappedAtomsMolA, mappedAtoms, counter);
    }

    /**
     * Partitions source bonds and propagates stable mapped-atom labels.
     * @param setNumA number of source remaining-bond records
     * @param setNumB number of target remaining-bond records
     * @param iBondSetA mutable source remaining-bond endpoint/order triples
     * @param iBondSetB target remaining-bond endpoint/order triples
     * @param unmappedAtomsMolA unmapped source atom indices
     * @param newMapping flattened extended source/target atom pairs preserving the seed prefix
     * @param counter legacy compatibility value; labels use stable atom-pair positions
     */
    protected void process(int setNumA, int setNumB, List<Integer> iBondSetA, List<Integer> iBondSetB,
            List<Integer> unmappedAtomsMolA, List<Integer> newMapping, int counter) {

        //
        //            int newMapingSize,
        //            List<Integer> new_iBondSetA,
        //            List<String> new_cBondSetA,
        boolean bondConsidered = false;
        boolean normalBond = true;

        for (int atomIndex = 0; atomIndex < setNumA; atomIndex++) {
            Integer indexI = iBondSetA.get(atomIndex * 3 + 0);
            Integer indexJ = iBondSetA.get(atomIndex * 3 + 1);
            Integer order = iBondSetA.get(atomIndex * 3 + 2);

            for (Integer unMappedAtomIndex : unmappedAtomsMolA) {
                if (unMappedAtomIndex.equals(indexI)) {
                    normalBond = unMappedAtomsEqualsIndexJ(setNumA, setNumB, iBondSetA, iBondSetB, atomIndex,
                            counter, newMapping, indexI, indexJ, order);
                    bondConsidered = true;
                } else if (unMappedAtomIndex.equals(indexJ)) {
                    normalBond = unMappedAtomsEqualsIndexI(setNumA, setNumB, iBondSetA, iBondSetB, atomIndex,
                            counter, newMapping, indexI, indexJ, order);
                    bondConsidered = true;
                }

                if (normalBond && bondConsidered) {
                    markNormalBonds(atomIndex, indexI, indexJ, order);
                    normalBond = true;
                    break;
                }
            }
            bondConsidered = false;
        }
    }

    private void markNormalBonds(int atomIndex, Integer indexI, Integer indexJ, Integer order) {
        newINeighborsA.add(indexI);
        newINeighborsA.add(indexJ);
        newINeighborsA.add(order);
        newCNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 0));
        newCNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 1));
        newCNeighborsA.add("X");
        newCNeighborsA.add("X");
        setBondNumA++;
    }

    private void step1(int atomIndex, int counter) {
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 0));
        cBondNeighborsA.add(signs[counter]);
        cBondNeighborsA.add("X");
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 1));
    }

    private void step2(int atomIndex) {
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 0));
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 1));
        cBondNeighborsA.add("X");
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 3));
    }

    private void step3(int atomIndex, int counter) {
        cBondNeighborsA.add(signs[counter]);
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 1));
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 0));
        cBondNeighborsA.add("X");
    }

    private void step4(int atomIndex) {
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 0));
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 1));
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 2));
        cBondNeighborsA.add("X");
    }

    private boolean unMappedAtomsEqualsIndexJ(IAtomContainer query, IAtomContainer target, int atomIndex, int counter,
            List<Integer> mappedAtoms, Integer indexI, Integer indexJ, Integer order) {
        boolean normalBond = true;
        for (int c = 0; c < newNeighborNumA; c++) {

            if (mappedAtoms.get(c * 2).equals(indexJ)) {
                setBondNeighbors(indexI, indexJ, order);
                if (cTab1Copy.get(atomIndex * 4 + 3).compareToIgnoreCase("X") == 0) {

                    step1(atomIndex, c);
                    McGregorChecks.changeCharBonds(indexJ, signs[c], query.getBondCount(), query, cTab1Copy);

                    int corAtom = McGregorChecks.searchCorrespondingAtom(newNeighborNumA, indexJ, 1, mappedAtoms);
                    McGregorChecks.changeCharBonds(corAtom, signs[c], target.getBondCount(), target, cTab2Copy);
                } else {
                    step2(atomIndex);
                }
                normalBond = false;
                neighborBondNumA++;
            }
        }
        return normalBond;
    }

    private boolean unMappedAtomsEqualsIndexI(IAtomContainer query, IAtomContainer target, int atomIndex, int counter,
            List<Integer> mappedAtoms, Integer indexI, Integer indexJ, Integer order) {

        boolean normalBond = true;
        for (int c = 0; c < newNeighborNumA; c++) {

            if (mappedAtoms.get(c * 2 + 0).equals(indexI)) {
                setBondNeighbors(indexI, indexJ, order);
                if (cTab1Copy.get(atomIndex * 4 + 2).compareToIgnoreCase("X") == 0) {
                    step3(atomIndex, c);
                    McGregorChecks.changeCharBonds(indexI, signs[c], query.getBondCount(), query, cTab1Copy);

                    int corAtom = McGregorChecks.searchCorrespondingAtom(newNeighborNumA, indexI, 1, mappedAtoms);
                    McGregorChecks.changeCharBonds(corAtom, signs[c], target.getBondCount(), target, cTab2Copy);
                } else {
                    step4(atomIndex);
                }
                normalBond = false;
                neighborBondNumA++;
                //System.out.println("Neighbor");
                //System.out.println(neighborBondNumA);
            }
        }
        return normalBond;
    }

    private boolean unMappedAtomsEqualsIndexJ(int setNumA, int setNumB, List<Integer> iBondSetA,
            List<Integer> iBondSetB, int atomIndex, int counter, List<Integer> newMapping, Integer indexI,
            Integer indexJ, Integer order) {
        boolean normalBond = true;
        for (int c = 0; c < newNeighborNumA; c++) {

            if (newMapping.get(c * 2 + 0).equals(indexJ)) {

                setBondNeighbors(indexI, indexJ, order);
                if (cTab1Copy.get(atomIndex * 4 + 3).compareToIgnoreCase("X") == 0) {
                    step1(atomIndex, c);
                    McGregorChecks.changeCharBonds(indexJ, signs[c], setNumA, iBondSetA, cTab1Copy);
                    int corAtom = McGregorChecks.searchCorrespondingAtom(newNeighborNumA, indexJ, 1, newMapping);
                    McGregorChecks.changeCharBonds(corAtom, signs[c], setNumB, iBondSetB, cTab2Copy);

                } else {
                    step2(atomIndex);
                }
                normalBond = false;
                neighborBondNumA++;
            }
        }
        return normalBond;
    }

    private boolean unMappedAtomsEqualsIndexI(int setNumA, int setNumB, List<Integer> iBondSetA,
            List<Integer> iBondSetB, int atomIndex, int counter, List<Integer> newMapping, Integer indexI,
            Integer indexJ, Integer order) {
        boolean normalBond = true;
        for (int c = 0; c < newNeighborNumA; c++) {

            if (newMapping.get(c * 2 + 0).equals(indexI)) {

                setBondNeighbors(indexI, indexJ, order);
                if (cTab1Copy.get(atomIndex * 4 + 2).compareToIgnoreCase("X") == 0) {
                    step3(atomIndex, c);
                    McGregorChecks.changeCharBonds(indexI, signs[c], setNumA, iBondSetA, cTab1Copy);
                    int corAtom = McGregorChecks.searchCorrespondingAtom(newNeighborNumA, indexI, 1, newMapping);
                    McGregorChecks.changeCharBonds(corAtom, signs[c], setNumB, iBondSetB, cTab2Copy);
                } else {
                    step4(atomIndex);
                }

                normalBond = false;
                neighborBondNumA++;

            }
        }
        return normalBond;
    }

    private void setBondNeighbors(Integer indexI, Integer indexJ, Integer order) {
        iBondNeighborsA.add(indexI);
        iBondNeighborsA.add(indexJ);
        iBondNeighborsA.add(order);
    }

    /**
     * Returns the mutable source bond-label table.
     * @return the borrowed source endpoint-label/backup records
     */
    protected List<String> getCTab1() {
        return this.cTab1Copy;
    }

    /**
     * Returns the mutable target bond-label table.
     * @return the borrowed target endpoint-label/backup records
     */
    protected List<String> getCTab2() {
        return this.cTab2Copy;
    }

    /**
     * Returns the number of source bonds bordering the current mapping.
     * @return source boundary-bond count
     */
    protected int getNeighborBondNumA() {
        return this.neighborBondNumA;
    }

    /**
     * Returns the number of source bonds remaining outside the boundary.
     * @return source remaining-bond count
     */
    protected int getBondNumA() {
        return this.setBondNumA;
    }

    List<Integer> getIBondNeighboursA() {
        return this.iBondNeighborsA;
    }

    List<String> getCBondNeighborsA() {
        return this.cBondNeighborsA;
    }
}
