/* Copyright (C) 2005-2006  Markus Leber
 *               2006-2009  Syed Asad Rahman <asad@ebi.ac.uk>
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

import java.util.List;
import java.util.Objects;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Partitions target bonds for one McGregor extension step and propagates
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
public class TargetProcessor {

    private final List<String>  cTab1Copy;
    private final List<String>  cTab2Copy;
    // The same mapping-pair position identifies corresponding source and target atoms.
    private final String[]      signArray;
    //number of remaining molecule A bonds after the clique search, which are
    //neighbors of the MCS
    private int           neighborBondNumB;
    //number of remaining molecule A bonds after the clique search, which aren't
    //neighbors
    private int           setBondNumB;
    private final List<Integer> iBondNeighborsB;
    private final List<String>  cBondNeighborsB;
    private final int           newNeighborNumA;
    private final List<Integer> newINeighborsA;
    private final List<String>  newCNeighborsA;

    /**
     * Constructs a target bond processor over aligned borrowed tables.
     * @param cTab1Copy borrowed mutable source four-entry endpoint-label/backup records
     * @param cTab2Copy borrowed mutable target four-entry endpoint-label/backup records
     * @param signArray unique $-prefixed mapped identity labels indexed by stable atom-pair position
     * @param neighborBondnumB initial target boundary-bond count
     * @param setBondnumB initial target remaining-bond count
     * @param iBondNeighborsB mutable output target endpoint/order triples
     * @param cBondNeighborsB mutable output target endpoint-label/backup records
     * @param newNeighborNumA number of source boundary-bond records for label propagation
     * @param newINeighborsA source boundary-bond endpoint/order triples for label propagation
     * @param newCNeighborsA mutable source boundary-bond endpoint-label/backup records for label propagation
     */
    protected TargetProcessor(List<String> cTab1Copy, List<String> cTab2Copy, String[] signArray,
            int neighborBondnumB, int setBondnumB, List<Integer> iBondNeighborsB, List<String> cBondNeighborsB,
            int newNeighborNumA, List<Integer> newINeighborsA, List<String> newCNeighborsA) {

        this.cTab1Copy = cTab1Copy;
        this.cTab2Copy = cTab2Copy;
        this.signArray = signArray;
        this.neighborBondNumB = neighborBondnumB;
        this.setBondNumB = setBondnumB;
        this.iBondNeighborsB = iBondNeighborsB;
        this.cBondNeighborsB = cBondNeighborsB;
        this.newNeighborNumA = newNeighborNumA;
        this.newCNeighborsA = newCNeighborsA;
        this.newINeighborsA = newINeighborsA;
    }

    /**
     * Partitions target bonds and propagates stable mapped-atom labels.
     * @param target simple target graph
     * @param unmappedAtomsMolB unmapped target atom indices
     * @param mappingSize number of mapped source/target atom pairs
     * @param iBondSetB target remaining-bond endpoint/order triples
     * @param cBondSetB target remaining-bond endpoint-label/backup records
     * @param mappedAtoms flattened source/target atom pairs in stable order
     * @param counter legacy compatibility value; labels use stable atom-pair positions
     */
    protected void process(IAtomContainer target, List<Integer> unmappedAtomsMolB, int mappingSize,
            List<Integer> iBondSetB, List<String> cBondSetB, List<Integer> mappedAtoms, int counter) {

        int unmappedNumB = unmappedAtomsMolB.size();
        boolean bondConsidered = false;
        boolean normalBond = true;

        for (int atomIndex = 0; atomIndex < target.getBondCount(); atomIndex++) {

            Integer indexI = target.indexOf(target.getBond(atomIndex).getBegin());
            Integer indexJ = target.indexOf(target.getBond(atomIndex).getEnd());
            Integer order = target.getBond(atomIndex).getOrder() == null
                    ? 0 : target.getBond(atomIndex).getOrder().numeric();

            for (Integer integer : unmappedAtomsMolB) {
                if (integer.equals(indexI)) {
                    normalBond = unMappedAtomsEqualsIndexI(target, mappingSize, atomIndex, counter, mappedAtoms,
                            indexI, indexJ, order);
                    bondConsidered = true;
                } else if (Objects.equals(integer, indexJ)) {
                    normalBond = unMappedAtomsEqualsIndexJ(target, mappingSize, atomIndex, counter, mappedAtoms,
                            indexI, indexJ, order);
                    bondConsidered = true;
                }

                if (normalBond && bondConsidered) {
                    markNormalBonds(atomIndex, iBondSetB, cBondSetB, indexI, indexJ, order);
                    normalBond = true;
                    break;
                }

            }
            bondConsidered = false;
        }

    }

    /**
     * Partitions target bonds and propagates stable mapped-atom labels.
     * @param setNumB number of target remaining-bond records
     * @param unmappedAtomsMolB unmapped target atom indices
     * @param newMappingSize number of extended atom pairs
     * @param iBondSetB target remaining-bond endpoint/order triples
     * @param cBondSetB target remaining-bond endpoint-label/backup records
     * @param newMapping flattened extended source/target atom pairs preserving the seed prefix
     * @param counter legacy compatibility value; labels use stable atom-pair positions
     * @param newIBondSetB mutable output target remaining-bond endpoint/order triples
     * @param newCBondSetB mutable output target remaining-bond endpoint-label/backup records
     */
    protected void process(int setNumB, List<Integer> unmappedAtomsMolB, int newMappingSize,
            List<Integer> iBondSetB, List<String> cBondSetB, List<Integer> newMapping, int counter,
            List<Integer> newIBondSetB, List<String> newCBondSetB) {

        //The special signs must be transfered to the corresponding atoms of molecule A

        boolean bondConsidered = false;
        boolean normalBond = true;
        for (int atomIndex = 0; atomIndex < setNumB; atomIndex++) {

            Integer indexI = iBondSetB.get(atomIndex * 3 + 0);
            Integer indexJ = iBondSetB.get(atomIndex * 3 + 1);
            Integer order = iBondSetB.get(atomIndex * 3 + 2);

            for (Integer unMappedAtomIndex : unmappedAtomsMolB) {
                if (unMappedAtomIndex.equals(indexI)) {
                    normalBond = unMappedAtomsEqualsIndexI(setNumB, iBondSetB, newMappingSize, atomIndex, counter,
                            newMapping, indexI, indexJ, order);
                    bondConsidered = true;
                } else if (unMappedAtomIndex.equals(indexJ)) {
                    normalBond = unMappedAtomsEqualsIndexJ(setNumB, iBondSetB, newMappingSize, atomIndex, counter,
                            newMapping, indexI, indexJ, order);
                    bondConsidered = true;
                }
                if (normalBond && bondConsidered) {
                    markNormalBonds(atomIndex, newIBondSetB, newCBondSetB, indexI, indexJ, order);
                    normalBond = true;
                    break;
                }

            }
            bondConsidered = false;
        }
    }

    private boolean unMappedAtomsEqualsIndexI(IAtomContainer target, int mappingSize, int atomIndex, int counter,
            List<Integer> mappedAtoms, Integer indexI, Integer indexJ, Integer order) {
        boolean normalBond = true;
        for (int c = 0; c < mappingSize; c++) {
            if (mappedAtoms.get(c * 2 + 1).equals(indexJ)) {
                setBondNeighbors(indexI, indexJ, order);
                if (cTab2Copy.get(atomIndex * 4 + 3).compareToIgnoreCase("X") == 0) {
                    step1(atomIndex, c);
                    McGregorChecks
                            .changeCharBonds(indexJ, signArray[c], target.getBondCount(), target, cTab2Copy);
                    int corAtom = McGregorChecks.searchCorrespondingAtom(mappingSize, indexJ, 2, mappedAtoms);
                    //Commented by Asad
                    McGregorChecks.changeCharBonds(corAtom, signArray[c], newNeighborNumA, newINeighborsA,
                            newCNeighborsA);
                    //                                changeCharBonds(corAtom, signArray[c], query.getBondCount(), query, cTab1Copy);
                } else {
                    step2(atomIndex);
                }
                normalBond = false;
                neighborBondNumB++;
            }
        }
        return normalBond;
    }

    private boolean unMappedAtomsEqualsIndexJ(IAtomContainer target, int mappingSize, int atomIndex, int counter,
            List<Integer> mappedAtoms, Integer indexI, Integer indexJ, Integer order) {
        boolean normalBond = true;
        for (int c = 0; c < mappingSize; c++) {
            if (mappedAtoms.get(c * 2 + 1).equals(indexI)) {
                setBondNeighbors(indexI, indexJ, order);
                if (cTab2Copy.get(atomIndex * 4 + 2).compareToIgnoreCase("X") == 0) {
                    step3(atomIndex, c);
                    McGregorChecks
                            .changeCharBonds(indexI, signArray[c], target.getBondCount(), target, cTab2Copy);
                    int corAtom = McGregorChecks.searchCorrespondingAtom(mappingSize, indexI, 2, mappedAtoms);
                    McGregorChecks.changeCharBonds(corAtom, signArray[c], newNeighborNumA, newINeighborsA,
                            newCNeighborsA);
                    //                                changeCharBonds(corAtom, signArray[c], query.getBondCount(), query, cTab1Copy);
                } else {
                    step4(atomIndex);
                }
                normalBond = false;
                neighborBondNumB++;
            }
        }

        return normalBond;
    }

    private boolean unMappedAtomsEqualsIndexI(int setNumB, List<Integer> iBondSetB, int newMappingSize,
            int atomIndex, int counter, List<Integer> newMapping, Integer indexI, Integer indexJ, Integer order) {
        boolean normalBond = true;
        for (int c = 0; c < newMappingSize; c++) {
            if (newMapping.get(c * 2 + 1).equals(indexJ)) {
                setBondNeighbors(indexI, indexJ, order);
                if (cTab2Copy.get(atomIndex * 4 + 3).compareToIgnoreCase("X") == 0) {
                    step1(atomIndex, c);
                    McGregorChecks.changeCharBonds(indexJ, signArray[c], setNumB, iBondSetB, cTab2Copy);
                    int corAtom = McGregorChecks.searchCorrespondingAtom(newMappingSize, indexJ, 2, newMapping);
                    McGregorChecks.changeCharBonds(corAtom, signArray[c], newNeighborNumA, newINeighborsA,
                            newCNeighborsA);

                } else {
                    step2(atomIndex);
                }

                normalBond = false;
                neighborBondNumB++;

            }
        }
        return normalBond;
    }

    private boolean unMappedAtomsEqualsIndexJ(int setNumB, List<Integer> iBondSetB, int newMappingSize,
            int atomIndex, int counter, List<Integer> newMapping, Integer indexI, Integer indexJ, Integer order) {
        boolean normalBond = true;
        for (int c = 0; c < newMappingSize; c++) {
            if (newMapping.get(c * 2 + 1).equals(indexI)) {
                setBondNeighbors(indexI, indexJ, order);

                if (cTab2Copy.get(atomIndex * 4 + 2).compareToIgnoreCase("X") == 0) {

                    step3(atomIndex, c);
                    McGregorChecks.changeCharBonds(indexI, signArray[c], setNumB, iBondSetB, cTab2Copy);
                    int corAtom = McGregorChecks.searchCorrespondingAtom(newMappingSize, indexI, 2, newMapping);
                    McGregorChecks.changeCharBonds(corAtom, signArray[c], newNeighborNumA, newINeighborsA,
                            newCNeighborsA);
                } else {
                    step4(atomIndex);
                }

                normalBond = false;
                neighborBondNumB++;

            }
        }

        return normalBond;
    }

    private void markNormalBonds(int atomIndex, List<Integer> iBondSetB, List<String> cBondSetB, Integer indexI,
            Integer indexJ, Integer order) {
        iBondSetB.add(indexI);
        iBondSetB.add(indexJ);
        iBondSetB.add(order);
        cBondSetB.add(cTab2Copy.get(atomIndex * 4 + 0));
        cBondSetB.add(cTab2Copy.get(atomIndex * 4 + 1));
        cBondSetB.add("X");
        cBondSetB.add("X");
        setBondNumB++;
    }

    private void setBondNeighbors(Integer indexI, Integer indexJ, Integer order) {
        iBondNeighborsB.add(indexI);
        iBondNeighborsB.add(indexJ);
        iBondNeighborsB.add(order);
    }

    private void step1(int atomIndex, int counter) {
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 0));
        cBondNeighborsB.add(signArray[counter]);
        cBondNeighborsB.add("X");
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 1));
    }

    private void step2(int atomIndex) {
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 0));
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 1));
        cBondNeighborsB.add("X");
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 3));
    }

    private void step3(int atomIndex, int counter) {
        cBondNeighborsB.add(signArray[counter]);
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 1));
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 0));
        cBondNeighborsB.add("X");
    }

    private void step4(int atomIndex) {
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 0));
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 1));
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 2));
        cBondNeighborsB.add("X");
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
     * Returns the number of target bonds bordering the current mapping.
     * @return target boundary-bond count
     */
    protected int getNeighborBondNumB() {
        return this.neighborBondNumB;
    }

    /**
     * Returns the number of target bonds remaining outside the boundary.
     * @return target remaining-bond count
     */
    protected int getBondNumB() {
        return this.setBondNumB;
    }

    List<Integer> getIBondNeighboursB() {
        return this.iBondNeighborsB;
    }

    List<String> getCBondNeighborsB() {
        return this.cBondNeighborsB;
    }
}
