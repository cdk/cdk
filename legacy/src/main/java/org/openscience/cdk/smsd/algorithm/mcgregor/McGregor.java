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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smsd.helper.BinaryTree;

/**
 * Class which reports MCS solutions based on the McGregor algorithm
 * published in 1982.
 *
 *  <p>The SMSD algorithm is described in this paper.
 * <font color="#FF0000">please refer Rahman <i>et.al. 2009</i></font>
 *  {@cdk.cite SMSD2009}.
 *  </p>
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 */
public final class McGregor {

    private              IAtomContainer       source        = null;
    private              IAtomContainer       target        = null;
    private              BinaryTree           last          = null;
    private              BinaryTree           first         = null;
    private              Stack<List<Integer>> bestArcs      = null;
    private              List<Integer>        modifiedARCS  = null;
    private              int                  bestarcsleft  = 0;
    private              int                  globalMCSSize = 0;
    private              List<List<Integer>>  mappings      = null;
    /* This should be more or equal to all the atom types */
    private static final String[]             SIGNS         = {"$1", "$2", "$3", "$4", "$5", "$6", "$7", "$8", "$9", "$10", "$11",
                                                               "$12", "$13", "$15", "$16", "$17", "$18", "$19", "$20", "$21", "$22", "$23", "$24", "$25", "$26", "$27",
                                                               "$28", "$29", "$30", "$31", "$32", "$33", "$34", "$35", "$36", "$37", "$38", "$39", "$40", "$41", "$42",
                                                               "$43", "$44", "$45", "$46", "$47", "$48", "$49", "$50", "$51", "$52", "$53", "$54", "$55"};
    private              boolean              newMatrix     = false;
    private              boolean              bondMatch     = false;

    /**
     * Constructor for the McGregor algorithm.
     * @param source
     * @param target
     * @param mappings
     * @param shouldMatchBonds
     */
    public McGregor(IAtomContainer source, IAtomContainer target, List<List<Integer>> mappings,
                    boolean shouldMatchBonds) {

        setBondMatch(shouldMatchBonds);
        this.source = source;
        this.target = target;
        this.mappings = mappings;
        this.bestarcsleft = 0;

        if (!mappings.isEmpty()) {
            this.globalMCSSize = mappings.get(0).size();
        }
        else {
            this.globalMCSSize = 0;
        }
        this.modifiedARCS = new ArrayList<Integer>();
        this.bestArcs = new Stack<List<Integer>>();
        this.newMatrix = false;
    }

    /**
     * Constructor for the McGregor algorithm.
     * @param source
     * @param target
     * @param mappings
     */
    public McGregor(IQueryAtomContainer source, IAtomContainer target, List<List<Integer>> mappings) {

        setBondMatch(true);
        this.source = source;
        this.target = target;
        this.mappings = mappings;
        this.bestarcsleft = 0;

        if (!mappings.isEmpty()) {
            this.globalMCSSize = mappings.get(0).size();
        }
        else {
            this.globalMCSSize = 0;
        }
        this.modifiedARCS = new ArrayList<Integer>();
        this.bestArcs = new Stack<List<Integer>>();
        this.newMatrix = false;
    }

    /**
     * Start McGregor search and extend the mappings if possible.
     * @param largestMappingSize
     * @param presentMapping
     * @throws IOException
     */
    public void startMcGregorIteration(int largestMappingSize, Map<Integer, Integer> presentMapping)
            throws IOException {

        this.globalMCSSize = (largestMappingSize / 2);
        List<String> cTab1Copy = McGregorChecks.generateCTabCopy(source);
        List<String> cTab2Copy = McGregorChecks.generateCTabCopy(target);

        //find mapped atoms of both molecules and store these in mappedAtoms
        List<Integer> mappedAtoms = new ArrayList<Integer>();
        //        System.out.println("\nMapped Atoms");
        for (Map.Entry<Integer, Integer> map : presentMapping.entrySet()) {
            //            System.out.println("i:" + map.getKey() + " j:" + map.getValue());
            mappedAtoms.add(map.getKey());
            mappedAtoms.add(map.getValue());
        }
        int mappingSize = presentMapping.size();

        List<Integer> iBondNeighborsA = new ArrayList<Integer>();
        List<String> cBondNeighborsA = new ArrayList<String>();

        List<Integer> iBondSetA = new ArrayList<Integer>();
        List<String> cBondSetA = new ArrayList<String>();

        List<Integer> iBondNeighborsB = new ArrayList<Integer>();
        List<Integer> iBondSetB = new ArrayList<Integer>();
        List<String> cBondNeighborsB = new ArrayList<String>();
        List<String> cBondSetB = new ArrayList<String>();

        //find unmapped atoms of molecule A

        List<Integer> unmappedAtomsMolA = McGregorChecks.markUnMappedAtoms(true, source, presentMapping);
        int counter = 0;
        int gSetBondNumA = 0;
        int gSetBondNumB = 0;
        int gNeighborBondnumA = 0; //number of remaining molecule A bonds after the clique search, which are neighbors of the MCS_1
        int gNeighborBondNumB = 0; //number of remaining molecule B bonds after the clique search, which are neighbors of the MCS_1

        QueryProcessor queryProcess = new QueryProcessor(cTab1Copy, cTab2Copy, SIGNS, gNeighborBondnumA,
                gSetBondNumA, iBondNeighborsA, cBondNeighborsA, mappingSize, iBondSetA, cBondSetA);

        if (!(source instanceof IQueryAtomContainer)) {
            queryProcess.process(source, target, unmappedAtomsMolA, mappedAtoms, counter);
        } else {
            queryProcess.process((IQueryAtomContainer) source, target, unmappedAtomsMolA, mappedAtoms, counter);
        }

        cTab1Copy = queryProcess.getCTab1();
        cTab2Copy = queryProcess.getCTab2();
        gSetBondNumA = queryProcess.getBondNumA();
        gNeighborBondnumA = queryProcess.getNeighborBondNumA();
        iBondNeighborsA = queryProcess.getIBondNeighboursA();
        cBondNeighborsA = queryProcess.getCBondNeighborsA();

        //find unmapped atoms of molecule B
        List<Integer> unmappedAtomsMolB = McGregorChecks.markUnMappedAtoms(false, target, presentMapping);

        //        System.out.println("unmappedAtomsMolB: " + unmappedAtomsMolB.size());

        //Extract bonds which are related with unmapped atoms of molecule B.
        //In case that unmapped atoms are connected with already mapped atoms, the mapped atoms are labelled with
        //new special signs -> the result are two vectors: cBondNeighborsA and int_bonds_molB, which contain those
        //bonds of molecule B, which are relevant for the McGregorBondTypeInSensitive algorithm.
        //The special signs must be transfered to the corresponding atoms of molecule A

        TargetProcessor targetProcess = new TargetProcessor(cTab1Copy, cTab2Copy, SIGNS, gNeighborBondNumB,
                gSetBondNumB, iBondNeighborsB, cBondNeighborsB, gNeighborBondnumA, iBondNeighborsA,
                cBondNeighborsA);

        targetProcess.process(target, unmappedAtomsMolB, mappingSize, iBondSetB, cBondSetB, mappedAtoms,
                counter);

        cTab1Copy = targetProcess.getCTab1();
        cTab2Copy = targetProcess.getCTab2();
        gSetBondNumB = targetProcess.getBondNumB();
        gNeighborBondNumB = targetProcess.getNeighborBondNumB();
        iBondNeighborsB = targetProcess.getIBondNeighboursB();
        cBondNeighborsB = targetProcess.getCBondNeighborsB();

        boolean dummy = false;

        McgregorHelper mcGregorHelper = new McgregorHelper(dummy, presentMapping.size(), mappedAtoms,
                gNeighborBondnumA, gNeighborBondNumB, iBondNeighborsA, iBondNeighborsB, cBondNeighborsA,
                cBondNeighborsB, gSetBondNumA, gSetBondNumB, iBondSetA, iBondSetB, cBondSetA, cBondSetB);
        iterator(mcGregorHelper);
    }

    /**
     * Start McGregor search and extend the mappings if possible.
     * @param largestMappingSize
     * @param cliqueVector
     * @param compGraphNodes
     * @throws IOException
     */
    public void startMcGregorIteration(int largestMappingSize, List<Integer> cliqueVector,
            List<Integer> compGraphNodes) throws IOException {
        this.globalMCSSize = (largestMappingSize / 2);
        List<String> cTab1Copy = McGregorChecks.generateCTabCopy(source);

        List<String> cTab2Copy = McGregorChecks.generateCTabCopy(target);

        //find mapped atoms of both molecules and store these in mappedAtoms
        List<Integer> mappedAtoms = new ArrayList<Integer>();

        int mappedAtomCount = 0;

        List<Integer> iBondNeighborAtomsA = new ArrayList<Integer>();
        List<String> cBondNeighborsA = new ArrayList<String>();

        List<Integer> iBondSetA = new ArrayList<Integer>();
        List<String> cBondSetA = new ArrayList<String>();

        List<Integer> iBondNeighborAtomsB = new ArrayList<Integer>();
        List<Integer> iBondSetB = new ArrayList<Integer>();
        List<String> cBondNeighborsB = new ArrayList<String>();
        List<String> cBondSetB = new ArrayList<String>();

        int cliqueSize = cliqueVector.size();
        int vecSize = compGraphNodes.size();

        int cliqueNumber = 0;

        for (int a = 0; a < cliqueSize; a++) {
            //go through all clique nodes
            cliqueNumber = cliqueVector.get(a);
            for (int b = 0; b < vecSize; b += 3) {
                //go through all nodes in the compatibility graph
                if (cliqueNumber == compGraphNodes.get(b + 2)) {
                    mappedAtoms.add(compGraphNodes.get(b));
                    mappedAtoms.add(compGraphNodes.get(b + 1));
                    mappedAtomCount++;
                }
            }
        }

        //find unmapped atoms of molecule A
        List<Integer> unmappedAtomsMolA = McGregorChecks.markUnMappedAtoms(true, source, mappedAtoms, cliqueSize);

        int counter = 0;
        int setNumA = 0;
        int setNumB = 0;
        int localNeighborBondnumA = 0; //number of remaining molecule A bonds after the clique search, which are neighbors of the MCS_1
        int localNeighborBondNumB = 0; //number of remaining molecule B bonds after the clique search, which are neighbors of the MCS_1

        //Extract bonds which are related with unmapped atoms of molecule A.
        //In case that unmapped atoms are connected with already mapped atoms, the mapped atoms are labelled with
        //new special signs -> the result are two vectors: cBondNeighborsA and int_bonds_molA, which contain those
        //bonds of molecule A, which are relevant for the McGregorBondTypeInSensitive algorithm.
        //The special signs must be transfered to the corresponding atoms of molecule B

        QueryProcessor queryProcess = new QueryProcessor(cTab1Copy, cTab2Copy, SIGNS, localNeighborBondnumA,
                setNumA, iBondNeighborAtomsA, cBondNeighborsA, cliqueSize, iBondSetA, cBondSetA);

        queryProcess.process(source, target, unmappedAtomsMolA, mappedAtoms, counter);

        cTab1Copy = queryProcess.getCTab1();
        cTab2Copy = queryProcess.getCTab2();
        setNumA = queryProcess.getBondNumA();
        localNeighborBondnumA = queryProcess.getNeighborBondNumA();
        iBondNeighborAtomsA = queryProcess.getIBondNeighboursA();
        cBondNeighborsA = queryProcess.getCBondNeighborsA();

        //find unmapped atoms of molecule B
        List<Integer> unmappedAtomsMolB = McGregorChecks.markUnMappedAtoms(false, target, mappedAtoms, cliqueSize);

        //Extract bonds which are related with unmapped atoms of molecule B.
        //In case that unmapped atoms are connected with already mapped atoms, the mapped atoms are labelled with
        //new special signs -> the result are two vectors: cBondNeighborsA and int_bonds_molB, which contain those
        //bonds of molecule B, which are relevant for the McGregorBondTypeInSensitive algorithm.
        //The special signs must be transfered to the corresponding atoms of molecule A

        TargetProcessor targetProcess = new TargetProcessor(cTab1Copy, cTab2Copy, SIGNS, localNeighborBondNumB,
                setNumB, iBondNeighborAtomsB, cBondNeighborsB, localNeighborBondnumA, iBondNeighborAtomsA,
                cBondNeighborsA);

        targetProcess.process(target, unmappedAtomsMolB, cliqueSize, iBondSetB, cBondSetB, mappedAtoms, counter);

        cTab1Copy = targetProcess.getCTab1();
        cTab2Copy = targetProcess.getCTab2();
        setNumB = targetProcess.getBondNumB();
        localNeighborBondNumB = targetProcess.getNeighborBondNumB();
        iBondNeighborAtomsB = targetProcess.getIBondNeighboursB();
        cBondNeighborsB = targetProcess.getCBondNeighborsB();

        boolean dummy = false;

        McgregorHelper mcGregorHelper = new McgregorHelper(dummy, mappedAtomCount, mappedAtoms, localNeighborBondnumA,
                localNeighborBondNumB, iBondNeighborAtomsA, iBondNeighborAtomsB, cBondNeighborsA, cBondNeighborsB,
                setNumA, setNumB, iBondSetA, iBondSetB, cBondSetA, cBondSetB);
        iterator(mcGregorHelper);

    }

    private int iterator(McgregorHelper mcGregorHelper) throws IOException {

        boolean mappingCheckFlag = mcGregorHelper.isMappingCheckFlag();
        int mappedAtomCount = mcGregorHelper.getMappedAtomCount();
        List<Integer> mappedAtoms = new ArrayList<Integer>(mcGregorHelper.getMappedAtomsOrg());
        int neighborBondNumA = mcGregorHelper.getNeighborBondNumA();
        int neighborBondNumB = mcGregorHelper.getNeighborBondNumB();

        //        //check possible mappings:
        boolean furtherMappingFlag = McGregorChecks.isFurtherMappingPossible(source, target, mcGregorHelper,
                isBondMatch());

        if (neighborBondNumA == 0 || neighborBondNumB == 0 || mappingCheckFlag || !furtherMappingFlag) {
            setFinalMappings(mappedAtoms, mappedAtomCount);
            return 0;
        }

        modifiedARCS.clear();
        int size = neighborBondNumA * neighborBondNumB;
        for (int i = 0; i < size; i++) {
            modifiedARCS.add(i, 0);
        }
        setModifedArcs(mcGregorHelper);
        first = new BinaryTree(-1);
        last = first;
        last.setEqual(null);
        last.setNotEqual(null);
        bestarcsleft = 0;

        startsearch(mcGregorHelper);
        Stack<List<Integer>> bestArcsCopy = new Stack<List<Integer>>();

        bestArcsCopy.addAll(bestArcs);
        while (!bestArcs.empty()) {
            bestArcs.pop();
        }
        searchAndExtendMappings(bestArcsCopy, mcGregorHelper);

        //System.out.println("In the iterator Termination");
        //System.out.println("============+++++++++==============");
        //System.out.println("Mapped Atoms before iterator Over: " + mappedAtoms);
        return 0;
    }

    private void searchAndExtendMappings(Stack<List<Integer>> bestarcsCopy, McgregorHelper mcGregorHelper)
            throws IOException {
        int mappedAtomCount = mcGregorHelper.getMappedAtomCount();

        int setNumA = mcGregorHelper.getSetNumA();
        int setNumB = mcGregorHelper.getsetNumB();
        List<Integer> iBondSetA = mcGregorHelper.getIBondSetA();
        List<Integer> iBondSetB = mcGregorHelper.getIBondSetB();
        List<String> cBondSetA = mcGregorHelper.getCBondSetA();
        List<String> cBondSetB = mcGregorHelper.getCBondSetB();

        while (!bestarcsCopy.empty()) {

            List<Integer> mArcsVector = new ArrayList<Integer>(bestarcsCopy.peek());
            List<Integer> newMapping = findMcGregorMapping(mArcsVector, mcGregorHelper);

            int newMapingSize = newMapping.size() / 2;
            boolean noFurtherMappings = false;
            if (mappedAtomCount == newMapingSize) {
                noFurtherMappings = true;
            }

            List<Integer> newINeighborsA = new ArrayList<Integer>(); //instead of iBondNeighborAtomsA
            List<Integer> newINeighborsB = new ArrayList<Integer>(); //instead of iBondNeighborAtomsB
            List<String> newCNeighborsA = new ArrayList<String>(); //instead of cBondNeighborsA
            List<String> newCNeighborsB = new ArrayList<String>(); //instead of cBondNeighborsB
            List<Integer> newIBondSetA = new ArrayList<Integer>(); //instead of iBondSetA
            List<Integer> newIBondSetB = new ArrayList<Integer>(); //instead of iBondSetB
            List<String> newCBondSetA = new ArrayList<String>(); //instead of cBondSetA
            List<String> newCBondSetB = new ArrayList<String>(); //instead of cBondSetB
            //new values for setNumA + setNumB
            //new arrays for iBondSetA + iBondSetB + cBondSetB + cBondSetB

            List<String> cSetACopy = McGregorChecks.generateCSetCopy(setNumA, cBondSetA);
            List<String> cSetBCopy = McGregorChecks.generateCSetCopy(setNumB, cBondSetB);

            //find unmapped atoms of molecule A
            List<Integer> unmappedAtomsMolA = new ArrayList<Integer>();
            int unmappedNumA = 0;
            boolean atomAIsUnmapped = true;

            for (int a = 0; a < source.getAtomCount(); a++) {
                for (int b = 0; b < newMapingSize; b++) {
                    if (a == newMapping.get(b * 2 + 0)) {
                        atomAIsUnmapped = false;
                    }

                }
                if (atomAIsUnmapped) {
                    unmappedAtomsMolA.add(unmappedNumA++, a);
                }
                atomAIsUnmapped = true;
            }

            //The special signs must be transfered to the corresponding atoms of molecule B

            int counter = 0;
            //number of remaining molecule A bonds after the clique search, which aren't neighbors
            int newSetBondNumA = 0; //instead of setNumA
            int newNeighborNumA = 0; //instead of localNeighborBondnumA

            QueryProcessor queryProcess = new QueryProcessor(cSetACopy, cSetBCopy, SIGNS, newNeighborNumA,
                    newSetBondNumA, newINeighborsA, newCNeighborsA, newMapingSize, newIBondSetA, newCBondSetA);

            queryProcess.process(setNumA, setNumB, iBondSetA, iBondSetB, unmappedAtomsMolA, newMapping, counter);

            cSetACopy = queryProcess.getCTab1();
            cSetBCopy = queryProcess.getCTab2();
            newSetBondNumA = queryProcess.getBondNumA();
            newNeighborNumA = queryProcess.getNeighborBondNumA();
            newINeighborsA = queryProcess.getIBondNeighboursA();
            newCNeighborsA = queryProcess.getCBondNeighborsA();

            //find unmapped atoms of molecule B

            List<Integer> unmappedAtomsMolB = new ArrayList<Integer>();
            int unmappedNumB = 0;
            boolean atomBIsUnmapped = true;

            for (int a = 0; a < target.getAtomCount(); a++) {
                for (int b = 0; b < newMapingSize; b++) {
                    if (a == newMapping.get(b * 2 + 1)) {
                        atomBIsUnmapped = false;
                    }
                }
                if (atomBIsUnmapped) {
                    unmappedAtomsMolB.add(unmappedNumB++, a);
                }
                atomBIsUnmapped = true;
            }

            //number of remaining molecule B bonds after the clique search, which aren't neighbors
            int newSetBondNumB = 0; //instead of setNumB
            int newNeighborNumB = 0; //instead of localNeighborBondNumB

            TargetProcessor targetProcess = new TargetProcessor(cSetACopy, cSetBCopy, SIGNS, newNeighborNumB,
                    newSetBondNumB, newINeighborsB, newCNeighborsB, newNeighborNumA, newINeighborsA,
                    newCNeighborsA);

            targetProcess.process(setNumB, unmappedAtomsMolB, newMapingSize, iBondSetB, cBondSetB, newMapping,
                    counter, newIBondSetB, newCBondSetB);

            cSetACopy = targetProcess.getCTab1();
            cSetBCopy = targetProcess.getCTab2();
            newSetBondNumB = targetProcess.getBondNumB();
            newNeighborNumB = targetProcess.getNeighborBondNumB();
            newINeighborsB = targetProcess.getIBondNeighboursB();
            newCNeighborsB = targetProcess.getCBondNeighborsB();

            //             System.out.println("Mapped Atoms before Iterator2: " + mappedAtoms);
            McgregorHelper newMH = new McgregorHelper(noFurtherMappings, newMapingSize, newMapping, newNeighborNumA,
                    newNeighborNumB, newINeighborsA, newINeighborsB, newCNeighborsA, newCNeighborsB,
                    newSetBondNumA, newSetBondNumB, newIBondSetA, newIBondSetB, newCBondSetA, newCBondSetB);

            iterator(newMH);
            bestarcsCopy.pop();
            //            System.out.println("End of the iterator!!!!");
        }
    }

    private List<Integer> findMcGregorMapping(List<Integer> mArcs, McgregorHelper mcGregorHelper) {

        int neighborBondNumA = mcGregorHelper.getNeighborBondNumA();
        int neighborBondNumB = mcGregorHelper.getNeighborBondNumB();
        List<Integer> currentMapping = new ArrayList<Integer>(mcGregorHelper.getMappedAtomsOrg());
        List<Integer> additionalMapping = new ArrayList<Integer>();

        for (int x = 0; x < neighborBondNumA; x++) {
            for (int y = 0; y < neighborBondNumB; y++) {
                if (mArcs.get(x * neighborBondNumB + y) == 1) {
                    extendMapping(x, y, mcGregorHelper, additionalMapping, currentMapping);
                }
            }
        }

        int additionalMappingSize = additionalMapping.size();
        //add McGregorBondTypeInSensitive mapping to the Clique mapping
        for (int a = 0; a < additionalMappingSize; a += 2) {
            currentMapping.add(additionalMapping.get(a + 0));
            currentMapping.add(additionalMapping.get(a + 1));
        }

        //        remove recurring mappings from currentMapping

        List<Integer> uniqueMapping = McGregorChecks.removeRecurringMappings(currentMapping);
        return uniqueMapping;
    }

    private void setModifedArcs(McgregorHelper mcGregorHelper) {
        int neighborBondNumA = mcGregorHelper.getNeighborBondNumA();
        int neighborBondNumB = mcGregorHelper.getNeighborBondNumB();
        List<Integer> iBondNeighborAtomsA = mcGregorHelper.getiBondNeighborAtomsA();
        List<Integer> iBondNeighborAtomsB = mcGregorHelper.getiBondNeighborAtomsB();
        List<String> cBondNeighborsA = mcGregorHelper.getcBondNeighborsA();
        List<String> cBondNeighborsB = mcGregorHelper.getcBondNeighborsB();
        for (int row = 0; row < neighborBondNumA; row++) {
            for (int column = 0; column < neighborBondNumB; column++) {

                String g1A = cBondNeighborsA.get(row * 4 + 0);
                String g2A = cBondNeighborsA.get(row * 4 + 1);
                String g1B = cBondNeighborsB.get(column * 4 + 0);
                String g2B = cBondNeighborsB.get(column * 4 + 1);

                if (matchGAtoms(g1A, g2A, g1B, g2B)) {
                    int indexI = iBondNeighborAtomsA.get(row * 3 + 0);
                    int indexIPlus1 = iBondNeighborAtomsA.get(row * 3 + 1);

                    IAtom r1A = source.getAtom(indexI);
                    IAtom r2A = source.getAtom(indexIPlus1);
                    IBond reactantBond = source.getBond(r1A, r2A);

                    int indexJ = iBondNeighborAtomsB.get(column * 3 + 0);
                    int indexJPlus1 = iBondNeighborAtomsB.get(column * 3 + 1);

                    IAtom p1B = target.getAtom(indexJ);
                    IAtom p2B = target.getAtom(indexJPlus1);
                    IBond productBond = target.getBond(p1B, p2B);
                    if (McGregorChecks.isMatchFeasible(source, reactantBond, target, productBond, isBondMatch())) {
                        modifiedARCS.set(row * neighborBondNumB + column, 1);
                    }
                }
            }
        }
    }

    private void partsearch(int xstart, int ystart, List<Integer> tempMArcsOrg, McgregorHelper mcGregorHelper) {
        int neighborBondNumA = mcGregorHelper.getNeighborBondNumA();
        int neighborBondNumB = mcGregorHelper.getNeighborBondNumB();

        int xIndex = xstart;
        int yIndex = ystart;

        List<Integer> tempMArcs = new ArrayList<Integer>(tempMArcsOrg);

        if (tempMArcs.get(xstart * neighborBondNumB + ystart) == 1) {

            McGregorChecks.removeRedundantArcs(xstart, ystart, tempMArcs, mcGregorHelper);
            int arcsleft = McGregorChecks.countArcsLeft(tempMArcs, neighborBondNumA, neighborBondNumB);

            //test Best arcs left and skip rest if needed
            if (arcsleft >= bestarcsleft) {
                setArcs(xIndex, yIndex, arcsleft, tempMArcs, mcGregorHelper);
            }
        } else {
            do {
                yIndex++;
                if (yIndex == neighborBondNumB) {
                    yIndex = 0;
                    xIndex++;
                }

            } while ((xIndex < neighborBondNumA) && (tempMArcs.get(xIndex * neighborBondNumB + yIndex) != 1)); //Correction by ASAD set value minus 1

            if (xIndex < neighborBondNumA) {

                partsearch(xIndex, yIndex, tempMArcs, mcGregorHelper);
                tempMArcs.set(xIndex * neighborBondNumB + yIndex, 0);
                partsearch(xIndex, yIndex, tempMArcs, mcGregorHelper);
            } else {
                int arcsleft = McGregorChecks.countArcsLeft(tempMArcs, neighborBondNumA, neighborBondNumB);
                if (arcsleft >= bestarcsleft) {
                    popBestArcs(arcsleft);

                    if (checkmArcs(tempMArcs, neighborBondNumA, neighborBondNumB)) {
                        bestArcs.push(tempMArcs);
                    }

                }
            }
        }
    }

    //The function is called in function partsearch. The function is given indexZ temporary matrix.
    //The function checks whether the temporary matrix is already found by calling the function
    //"verifyNodes". If the matrix already exists the function returns false which means that
    //the matrix will not be stored. Otherwise the function returns true which means that the
    //matrix will be stored in function partsearch.
    private boolean checkmArcs(List<Integer> mArcsT, int neighborBondNumA, int neighborBondNumB) {

        int size = neighborBondNumA * neighborBondNumA;
        List<Integer> posNumList = new ArrayList<Integer>(size);

        for (int i = 0; i < posNumList.size(); i++) {
            posNumList.add(i, 0);
        }

        int yCounter = 0;
        int countEntries = 0;
        for (int x = 0; x < (neighborBondNumA * neighborBondNumB); x++) {
            if (mArcsT.get(x) == 1) {
                posNumList.add(yCounter++, x);
                countEntries++;
            }
        }
        boolean flag = false;

        verifyNodes(posNumList, first, 0, countEntries);
        if (isNewMatrix()) {
            flag = true;
        }

        return flag;

    }

    private boolean verifyNodes(List<Integer> matrix, BinaryTree currentStructure, int index, int fieldLength) {
        if (index < fieldLength) {
            if (matrix.get(index) == currentStructure.getValue() && currentStructure.getEqual() != null) {
                setNewMatrix(false);
                verifyNodes(matrix, currentStructure.getEqual(), index + 1, fieldLength);
            }
            if (matrix.get(index) != currentStructure.getValue()) {
                if (currentStructure.getNotEqual() != null) {
                    verifyNodes(matrix, currentStructure.getNotEqual(), index, fieldLength);
                }

                if (currentStructure.getNotEqual() == null) {
                    currentStructure.setNotEqual(new BinaryTree(matrix.get(index)));
                    currentStructure.getNotEqual().setNotEqual(null);
                    int yIndex = 0;

                    BinaryTree lastOne = currentStructure.getNotEqual();

                    while ((yIndex + index + 1) < fieldLength) {
                        lastOne.setEqual(new BinaryTree(matrix.get(yIndex + index + 1)));
                        lastOne = lastOne.getEqual();
                        lastOne.setNotEqual(null);
                        yIndex++;

                    }
                    lastOne.setEqual(null);
                    setNewMatrix(true);
                }

            }
        }
        return true;
    }

    private void startsearch(McgregorHelper mcGregorHelper) {
        int neighborBondNumA = mcGregorHelper.getNeighborBondNumA();
        int neighborBondNumB = mcGregorHelper.getNeighborBondNumB();

        int size = neighborBondNumA * neighborBondNumB;
        List<Integer> fixArcs = new ArrayList<Integer>(size);//  Initialize fixArcs with 0
        for (int i = 0; i < size; i++) {
            fixArcs.add(i, 0);
        }

        int xIndex = 0;
        int yIndex = 0;

        while ((xIndex < neighborBondNumA) && (modifiedARCS.get(xIndex * neighborBondNumB + yIndex) != 1)) {
            yIndex++;
            if (yIndex == neighborBondNumB) {
                yIndex = 0;
                xIndex++;
            }
        }

        if (xIndex == neighborBondNumA) {
            yIndex = neighborBondNumB - 1;
            xIndex -= 1;
        }

        if (modifiedARCS.get(xIndex * neighborBondNumB + yIndex) == 0) {
            partsearch(xIndex, yIndex, modifiedARCS, mcGregorHelper);
        }

        if (modifiedARCS.get(xIndex * neighborBondNumB + yIndex) != 0) {
            partsearch(xIndex, yIndex, modifiedARCS, mcGregorHelper);
            modifiedARCS.set(xIndex * neighborBondNumB + yIndex, 0);
            partsearch(xIndex, yIndex, modifiedARCS, mcGregorHelper);
        }

    }

    /**
     * Returns computed mappings.
     * @return mappings
     */
    public List<List<Integer>> getMappings() {

        return mappings;
    }

    /**
     * Returns MCS size.
     * @return MCS size
     */
    public int getMCSSize() {

        return this.globalMCSSize;
    }

    private void setFinalMappings(List<Integer> mappedAtoms, int mappedAtomCount) {
        try {
            if (mappedAtomCount >= globalMCSSize) {
                //                    System.out.println("Hello-1");
                if (mappedAtomCount > globalMCSSize) {
                    //                        System.out.println("Hello-2");
                    this.globalMCSSize = mappedAtomCount;
                    //                        System.out.println("best_MAPPING_size: " + globalMCSSize);
                    mappings.clear();
                }
                mappings.add(mappedAtoms);
                //                    System.out.println("mappings " + mappings);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setArcs(int xIndex, int yIndex, int arcsleft, List<Integer> tempMArcs, McgregorHelper mcGregorHelper) {
        int neighborBondNumA = mcGregorHelper.getNeighborBondNumA();
        int neighborBondNumB = mcGregorHelper.getNeighborBondNumB();
        do {
            yIndex++;
            if (yIndex == neighborBondNumB) {
                yIndex = 0;
                xIndex++;

            }
        } //Correction by ASAD set value minus 1
        while ((xIndex < neighborBondNumA) && (tempMArcs.get(xIndex * neighborBondNumB + yIndex) != 1));
        if (xIndex < neighborBondNumA) {

            partsearch(xIndex, yIndex, tempMArcs, mcGregorHelper);
            tempMArcs.set(xIndex * neighborBondNumB + yIndex, 0);
            partsearch(xIndex, yIndex, tempMArcs, mcGregorHelper);

        } else {
            popBestArcs(arcsleft);
            if (checkmArcs(tempMArcs, neighborBondNumA, neighborBondNumB)) {
                bestArcs.push(tempMArcs);
            }
        }
    }

    private void popBestArcs(int arcsleft) {
        if (arcsleft > bestarcsleft) {
            McGregorChecks.removeTreeStructure(first);
            first = last = new BinaryTree(-1);
            last.setEqual(null);
            last.setNotEqual(null);
            while (!bestArcs.empty()) {
                bestArcs.pop();
            }
        }
        bestarcsleft = arcsleft;
    }

    private void extendMapping(int xIndex, int yIndex, McgregorHelper mcGregorHelper, List<Integer> additionalMapping,
            List<Integer> currentMapping) {

        int atom1MoleculeA = mcGregorHelper.getiBondNeighborAtomsA().get(xIndex * 3 + 0);
        int atom2MoleculeA = mcGregorHelper.getiBondNeighborAtomsA().get(xIndex * 3 + 1);
        int atom1MoleculeB = mcGregorHelper.getiBondNeighborAtomsB().get(yIndex * 3 + 0);
        int atom2MoleculeB = mcGregorHelper.getiBondNeighborAtomsB().get(yIndex * 3 + 1);

        IAtom r1A = source.getAtom(atom1MoleculeA);
        IAtom r2A = source.getAtom(atom2MoleculeA);
        IBond reactantBond = source.getBond(r1A, r2A);

        IAtom p1B = target.getAtom(atom1MoleculeB);
        IAtom p2B = target.getAtom(atom2MoleculeB);
        IBond productBond = target.getBond(p1B, p2B);

        //      Bond Order Check Introduced by Asad

        if (McGregorChecks.isMatchFeasible(source, reactantBond, target, productBond, isBondMatch())) {

            for (int indexZ = 0; indexZ < mcGregorHelper.getMappedAtomCount(); indexZ++) {

                int mappedAtom1 = currentMapping.get(indexZ * 2 + 0);
                int mappedAtom2 = currentMapping.get(indexZ * 2 + 1);

                if ((mappedAtom1 == atom1MoleculeA) && (mappedAtom2 == atom1MoleculeB)) {
                    additionalMapping.add(atom2MoleculeA);
                    additionalMapping.add(atom2MoleculeB);
                } else if ((mappedAtom1 == atom1MoleculeA) && (mappedAtom2 == atom2MoleculeB)) {
                    additionalMapping.add(atom2MoleculeA);
                    additionalMapping.add(atom1MoleculeB);
                } else if ((mappedAtom1 == atom2MoleculeA) && (mappedAtom2 == atom1MoleculeB)) {
                    additionalMapping.add(atom1MoleculeA);
                    additionalMapping.add(atom2MoleculeB);
                } else if ((mappedAtom1 == atom2MoleculeA) && (mappedAtom2 == atom2MoleculeB)) {
                    additionalMapping.add(atom1MoleculeA);
                    additionalMapping.add(atom1MoleculeB);
                }
            }//for loop
        }
    }

    private boolean matchGAtoms(String g1A, String g2A, String g1B, String g2B) {
        return (g1A.compareToIgnoreCase(g1B) == 0 && g2A.compareToIgnoreCase(g2B) == 0)
                || (g1A.compareToIgnoreCase(g2B) == 0 && g2A.compareToIgnoreCase(g1B) == 0);
    }

    /**
     * Checks if its a new Matrix.
     * @return the newMatrix
     */
    public boolean isNewMatrix() {
        return newMatrix;
    }

    /**
     * set a new Matrix.
     * @param newMatrix the newMatrix to set
     */
    public void setNewMatrix(boolean newMatrix) {
        this.newMatrix = newMatrix;
    }

    /**
     * Should bonds match
     * @return the bondMatch
     */
    private boolean isBondMatch() {
        return bondMatch;
    }

    /**
     * Should bonds match
     * @param bondMatch the bondMatch to set
     */
    private void setBondMatch(boolean bondMatch) {
        this.bondMatch = bondMatch;
    }
}
