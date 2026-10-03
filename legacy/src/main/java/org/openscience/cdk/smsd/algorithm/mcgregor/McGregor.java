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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smsd.helper.BinaryTree;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultMCSPlusAtomMatcher;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;
import org.openscience.cdk.smsd.global.TimeOut;
import org.openscience.cdk.smsd.tools.TimeManager;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Extends caller-provided atom mappings with the legacy McGregor algorithm.
 * Ordinary atoms match by element; ordinary bonds optionally compare strict
 * order and aromaticity. Source atom and bond query predicates are directional.
 * Full mapping-wide stereochemistry, component-group and reaction-map filters
 * are not applied, and molecules are not chemically prepared by this class.
 *
 * <p>Inputs and the mutable result list are borrowed. Each search validates
 * simple two-centre topology and injective, atom-compatible seed pairs before
 * changing its state. Seed connectivity and complete seed bond isomorphism are
 * not required. Existing result entries are trusted flattened index pairs.
 * Callers must not mutate graphs, chemistry or results during a search, and
 * instances must not be shared by concurrent or reentrant searches.</p>
 *
 * <p>The cutoff is configured through {@link org.openscience.cdk.smsd.global.TimeOut}
 * on the searching thread and captured at entry. Every negative cutoff disables
 * it. Checks are cooperative; a predicate callback cannot be preempted. On
 * timeout, previously found mappings remain available and this search publishes
 * its own timeout status on exit. A separate nested search cannot cancel it by
 * changing the thread-local status flag. Chemistry evaluation failures propagate
 * instead of being reported as smaller overlaps. The legacy branching search
 * remains recursive and is not guaranteed
 * safe for arbitrarily deep inputs.</p>
 *
 * <p>The SMSD algorithm is described by Rahman <i>et al.</i>
 * {@cdk.cite SMSD2009}.</p>
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public final class McGregor {

    private              IAtomContainer       source;
    private              IAtomContainer       target;
    private              BinaryTree           last          = null;
    private              BinaryTree           first         = null;
    private              Stack<List<Integer>> bestArcs;
    private              List<Integer>        modifiedARCS;
    private              int                  bestarcsleft;
    private              int                  globalMCSSize;
    private              List<List<Integer>>  mappings;
    private String[] signs;
    private              boolean              newMatrix;
    private              boolean              bondMatch     = false;
    private TimeManager searchTime;
    private double searchTimeout;
    private boolean searchTimedOut;

    /**
     * Constructs a search that extends mappings in the supplied result store.
     * @param source borrowed source graph containing any directional predicates
     * @param target borrowed target graph
     * @param mappings mutable result store of flattened source/target index pairs; existing entries are trusted
     * @param shouldMatchBonds whether ordinary bond order and aromaticity must match
     * @throws NullPointerException if either graph, the result store or an existing result entry is null
     */
    public McGregor(IAtomContainer source, IAtomContainer target, List<List<Integer>> mappings,
                    boolean shouldMatchBonds) {

        setBondMatch(shouldMatchBonds);
        this.source = Objects.requireNonNull(source, "source");
        this.target = Objects.requireNonNull(target, "target");
        signs = new String[Math.max(source.getAtomCount(), target.getAtomCount())];
        for (int i = 0; i < signs.length; i++) signs[i] = "$" + (i + 1);
        this.mappings = Objects.requireNonNull(mappings, "mappings");
        this.bestarcsleft = 0;

        this.globalMCSSize = largestExistingMapping();
        this.modifiedARCS = new ArrayList<>();
        this.bestArcs = new Stack<>();
        this.newMatrix = false;
    }

    /**
     * Constructs a directional query search with ordinary bond matching enabled.
     * @param source borrowed query graph
     * @param target borrowed target graph
     * @param mappings mutable result store of flattened source/target index pairs; existing entries are trusted
     * @throws NullPointerException if either graph, the result store or an existing result entry is null
     */
    public McGregor(IQueryAtomContainer source, IAtomContainer target, List<List<Integer>> mappings) {

        this((IAtomContainer) source, target, mappings, true);
    }

    /**
     * Validates and extends an injective source-to-target seed mapping.
     *
     * The search retains existing maxima and appends equal-size results. Invalid input is rejected before result, deadline or search-state changes.
     * @param largestMappingSize legacy flattened mapping length; two indices per atom pair, with integer division for odd values
     * @param presentMapping source-index to target-index seed, copied before the search
     * @throws IOException retained for compatibility with legacy bond-table preparation
     * @throws RuntimeException if atom or bond predicate evaluation fails
     * @throws IllegalArgumentException if the size or seed is invalid, atom pairs are incompatible, or an input graph is not simple
     * @throws NullPointerException if the seed is null or ordinary element comparison requires an unset non-pseudo atomic number
     */
    public void startMcGregorIteration(int largestMappingSize, Map<Integer, Integer> presentMapping)
            throws IOException {

        if (largestMappingSize < 0) throw new IllegalArgumentException("Mapping size must not be negative");
        double requestedTimeout = TimeOut.getInstance().getTimeOut();
        TimeManager requestedClock = new TimeManager();
        // Inputs are borrowed and may have changed since construction.
        new TargetProperties(source);
        new TargetProperties(target);
        Map<Integer, Integer> seed = validatedSeed(presentMapping);
        if (source.getAtomCount() > signs.length || target.getAtomCount() > signs.length) {
            signs = new String[Math.max(source.getAtomCount(), target.getAtomCount())];
            for (int i = 0; i < signs.length; i++) signs[i] = "$" + (i + 1);
        }
        searchTime = requestedClock;
        searchTimeout = requestedTimeout;
        searchTimedOut = false;
        TimeOut.getInstance().setTimeOutFlag(false);
        try {
            bestArcs.clear();
            if (isTimeOut()) return;
            this.globalMCSSize = Math.max(largestMappingSize / 2, largestExistingMapping());
            List<String> cTab1Copy = McGregorChecks.generateCTabCopy(source);
            List<String> cTab2Copy = McGregorChecks.generateCTabCopy(target);

            //find mapped atoms of both molecules and store these in mappedAtoms
            List<Integer> mappedAtoms = new ArrayList<>();
            for (Map.Entry<Integer, Integer> map : seed.entrySet()) {
                mappedAtoms.add(map.getKey());
                mappedAtoms.add(map.getValue());
            }
            int mappingSize = seed.size();

            List<Integer> iBondNeighborsA = new ArrayList<>();
            List<String> cBondNeighborsA = new ArrayList<>();

            List<Integer> iBondSetA = new ArrayList<>();
            List<String> cBondSetA = new ArrayList<>();

            List<Integer> iBondNeighborsB = new ArrayList<>();
            List<Integer> iBondSetB = new ArrayList<>();
            List<String> cBondNeighborsB = new ArrayList<>();
            List<String> cBondSetB = new ArrayList<>();

            //find unmapped atoms of molecule A

            List<Integer> unmappedAtomsMolA = McGregorChecks.markUnMappedAtoms(true, source, seed);
            int counter = 0;
            int gSetBondNumA = 0;
            int gSetBondNumB = 0;
            int gNeighborBondnumA = 0; //number of remaining molecule A bonds after the clique search, which are neighbors of the MCS_1
            int gNeighborBondNumB = 0; //number of remaining molecule B bonds after the clique search, which are neighbors of the MCS_1

            QueryProcessor queryProcess = new QueryProcessor(cTab1Copy, cTab2Copy, signs, gNeighborBondnumA,
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
            List<Integer> unmappedAtomsMolB = McGregorChecks.markUnMappedAtoms(false, target, seed);

            //Extract bonds which are related with unmapped atoms of molecule B.
            //In case that unmapped atoms are connected with already mapped atoms, the mapped atoms are labelled with
            //new special signs -> the result are two vectors: cBondNeighborsA and int_bonds_molB, which contain those
            //bonds of molecule B, which are relevant for the McGregorBondTypeInSensitive algorithm.
            //The special signs must be transfered to the corresponding atoms of molecule A

            TargetProcessor targetProcess = new TargetProcessor(cTab1Copy, cTab2Copy, signs, gNeighborBondNumB,
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

            McgregorHelper mcGregorHelper = new McgregorHelper(dummy, seed.size(), mappedAtoms,
                    gNeighborBondnumA, gNeighborBondNumB, iBondNeighborsA, iBondNeighborsB, cBondNeighborsA,
                    cBondNeighborsB, gSetBondNumA, gSetBondNumB, iBondSetA, iBondSetB, cBondSetA, cBondSetB);
            iterator(mcGregorHelper);
        } finally {
            TimeOut.getInstance().setTimeOutFlag(isTimeOut());
        }
    }

    /**
     * Builds a seed from compatibility-graph vertices and extends it.
     *
     * Both entry points apply the same seed checks and result lifecycle; they do not require full seed connectivity or bond isomorphism.
     * @param largestMappingSize legacy flattened mapping length; two indices per atom pair, with integer division for odd values
     * @param cliqueVector unique selected compatibility vertex identifiers
     * @param compGraphNodes flattened source-index, target-index, vertex-identifier triples with unique identifiers
     * @throws IOException retained for compatibility with legacy bond-table preparation
     * @throws RuntimeException if atom or bond predicate evaluation fails
     * @throws IllegalArgumentException if the size, triples, selected vertices, resulting seed or input topology is invalid
     * @throws NullPointerException if a list is null or ordinary element comparison requires an unset non-pseudo atomic number
     */
    public void startMcGregorIteration(int largestMappingSize, List<Integer> cliqueVector,
            List<Integer> compGraphNodes) throws IOException {
        Objects.requireNonNull(cliqueVector, "cliqueVector");
        Objects.requireNonNull(compGraphNodes, "compGraphNodes");
        if (compGraphNodes.size() % 3 != 0)
            throw new IllegalArgumentException("Compatibility nodes must contain source, target, vertex triples");
        Map<Integer, Integer> vertexPositions = new LinkedHashMap<>();
        for (int i = 0; i < compGraphNodes.size(); i += 3) {
            integerValue(compGraphNodes.get(i), "Source atom index");
            integerValue(compGraphNodes.get(i + 1), "Target atom index");
            int vertex = integerValue(compGraphNodes.get(i + 2), "Compatibility vertex");
            if (vertexPositions.put(vertex, i) != null)
                throw new IllegalArgumentException("Repeated compatibility vertex: " + vertex);
        }
        Map<Integer, Integer> seed = new TreeMap<>();
        Set<Integer> selectedVertices = new HashSet<>();
        for (Object value : cliqueVector) {
            int vertex = integerValue(value, "Clique vertex");
            if (!selectedVertices.add(vertex))
                throw new IllegalArgumentException("Repeated clique vertex: " + vertex);
            Integer position = vertexPositions.get(vertex);
            if (position == null) throw new IllegalArgumentException("Unknown compatibility vertex: " + vertex);
            int queryIndex = integerValue(compGraphNodes.get(position), "Source atom index");
            int targetIndex = integerValue(compGraphNodes.get(position + 1), "Target atom index");
            if (seed.put(queryIndex, targetIndex) != null)
                throw new IllegalArgumentException("Repeated source atom in seed: " + queryIndex);
        }
        startMcGregorIteration(largestMappingSize, seed);
    }

    private Map<Integer, Integer> validatedSeed(Map<Integer, Integer> presentMapping) {
        Objects.requireNonNull(presentMapping, "presentMapping");
        Map<Integer, Integer> seed = new LinkedHashMap<>();
        Set<Integer> targetIndices = new HashSet<>();
        for (Map.Entry<?, ?> pair : presentMapping.entrySet()) {
            int queryIndex = integerValue(pair.getKey(), "Source atom index");
            int targetIndex = integerValue(pair.getValue(), "Target atom index");
            if (queryIndex < 0 || queryIndex >= source.getAtomCount())
                throw new IllegalArgumentException("Source atom index out of range: " + queryIndex);
            if (targetIndex < 0 || targetIndex >= target.getAtomCount())
                throw new IllegalArgumentException("Target atom index out of range: " + targetIndex);
            if (!targetIndices.add(targetIndex))
                throw new IllegalArgumentException("Repeated target atom in seed: " + targetIndex);
            if (!new DefaultMCSPlusAtomMatcher(source, source.getAtom(queryIndex), isBondMatch())
                    .matches(target, target.getAtom(targetIndex)))
                throw new IllegalArgumentException("Incompatible atom pair: " + queryIndex + " -> " + targetIndex);
            seed.put(queryIndex, targetIndex);
        }
        return seed;
    }

    private static int integerValue(Object value, String description) {
        if (!(value instanceof Integer)) throw new IllegalArgumentException(description + " must be an integer");
        return (Integer) value;
    }

    private int largestExistingMapping() {
        int largest = 0;
        for (List<Integer> mapping : mappings) largest = Math.max(largest, mapping.size() / 2);
        return largest;
    }

    private boolean isTimeOut() {
        if (searchTimedOut) return true;
        if (searchTimeout >= 0 && searchTime != null
                && searchTime.getElapsedTimeInMinutes() > searchTimeout) {
            searchTimedOut = true;
            return true;
        }
        return false;
    }

    private int iterator(McgregorHelper mcGregorHelper) throws IOException {
        if (isTimeOut()) return 0;

        boolean mappingCheckFlag = mcGregorHelper.isMappingCheckFlag();
        int mappedAtomCount = mcGregorHelper.getMappedAtomCount();
        List<Integer> mappedAtoms = new ArrayList<>(mcGregorHelper.getMappedAtomsOrg());
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
        Stack<List<Integer>> bestArcsCopy = new Stack<>();

        bestArcsCopy.addAll(bestArcs);
        while (!bestArcs.empty()) {
            bestArcs.pop();
        }
        searchAndExtendMappings(bestArcsCopy, mcGregorHelper);
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

        while (!bestarcsCopy.empty() && !isTimeOut()) {

            List<Integer> mArcsVector = new ArrayList<>(bestarcsCopy.peek());
            List<Integer> newMapping = findMcGregorMapping(mArcsVector, mcGregorHelper);

            int newMapingSize = newMapping.size() / 2;
            boolean noFurtherMappings = false;
            if (mappedAtomCount == newMapingSize) {
                noFurtherMappings = true;
            }

            List<Integer> newINeighborsA = new ArrayList<>(); //instead of iBondNeighborAtomsA
            List<Integer> newINeighborsB = new ArrayList<>(); //instead of iBondNeighborAtomsB
            List<String> newCNeighborsA = new ArrayList<>(); //instead of cBondNeighborsA
            List<String> newCNeighborsB = new ArrayList<>(); //instead of cBondNeighborsB
            List<Integer> newIBondSetA = new ArrayList<>(); //instead of iBondSetA
            List<Integer> newIBondSetB = new ArrayList<>(); //instead of iBondSetB
            List<String> newCBondSetA = new ArrayList<>(); //instead of cBondSetA
            List<String> newCBondSetB = new ArrayList<>(); //instead of cBondSetB
            //new values for setNumA + setNumB
            //new arrays for iBondSetA + iBondSetB + cBondSetB + cBondSetB

            List<String> cSetACopy = McGregorChecks.generateCSetCopy(setNumA, cBondSetA);
            List<String> cSetBCopy = McGregorChecks.generateCSetCopy(setNumB, cBondSetB);

            //find unmapped atoms of molecule A
            List<Integer> unmappedAtomsMolA =
                    McGregorChecks.markUnMappedAtoms(true, source, newMapping, newMapingSize);

            //The special signs must be transfered to the corresponding atoms of molecule B

            int counter = 0;
            //number of remaining molecule A bonds after the clique search, which aren't neighbors
            int newSetBondNumA = 0; //instead of setNumA
            int newNeighborNumA = 0; //instead of localNeighborBondnumA

            QueryProcessor queryProcess = new QueryProcessor(cSetACopy, cSetBCopy, signs, newNeighborNumA,
                    newSetBondNumA, newINeighborsA, newCNeighborsA, newMapingSize, newIBondSetA, newCBondSetA);

            queryProcess.process(setNumA, setNumB, iBondSetA, iBondSetB, unmappedAtomsMolA, newMapping, counter);

            cSetACopy = queryProcess.getCTab1();
            cSetBCopy = queryProcess.getCTab2();
            newSetBondNumA = queryProcess.getBondNumA();
            newNeighborNumA = queryProcess.getNeighborBondNumA();
            newINeighborsA = queryProcess.getIBondNeighboursA();
            newCNeighborsA = queryProcess.getCBondNeighborsA();

            //find unmapped atoms of molecule B

            List<Integer> unmappedAtomsMolB =
                    McGregorChecks.markUnMappedAtoms(false, target, newMapping, newMapingSize);

            //number of remaining molecule B bonds after the clique search, which aren't neighbors
            int newSetBondNumB = 0; //instead of setNumB
            int newNeighborNumB = 0; //instead of localNeighborBondNumB

            TargetProcessor targetProcess = new TargetProcessor(cSetACopy, cSetBCopy, signs, newNeighborNumB,
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
            McgregorHelper newMH = new McgregorHelper(noFurtherMappings, newMapingSize, newMapping, newNeighborNumA,
                    newNeighborNumB, newINeighborsA, newINeighborsB, newCNeighborsA, newCNeighborsB,
                    newSetBondNumA, newSetBondNumB, newIBondSetA, newIBondSetB, newCBondSetA, newCBondSetB);

            iterator(newMH);
            bestarcsCopy.pop();
        }
    }

    private List<Integer> findMcGregorMapping(List<Integer> mArcs, McgregorHelper mcGregorHelper) {

        int neighborBondNumA = mcGregorHelper.getNeighborBondNumA();
        int neighborBondNumB = mcGregorHelper.getNeighborBondNumB();
        List<Integer> currentMapping = new ArrayList<>(mcGregorHelper.getMappedAtomsOrg());
        List<Integer> additionalMapping = new ArrayList<>();

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
        for (int row = 0; row < neighborBondNumA && !isTimeOut(); row++) {
            for (int column = 0; column < neighborBondNumB; column++) {

                String g1A = cBondNeighborsA.get(row * 4 + 0);
                String g2A = cBondNeighborsA.get(row * 4 + 1);
                String g1B = cBondNeighborsB.get(column * 4 + 0);
                String g2B = cBondNeighborsB.get(column * 4 + 1);

                if (McGregorChecks.isLabelMatch(g1A, g2A, g1B, g2B)) {
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
        if (isTimeOut()) return;
        int neighborBondNumA = mcGregorHelper.getNeighborBondNumA();
        int neighborBondNumB = mcGregorHelper.getNeighborBondNumB();

        int xIndex = xstart;
        int yIndex = ystart;

        List<Integer> tempMArcs = new ArrayList<>(tempMArcsOrg);

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

        List<Integer> posNumList = new ArrayList<>(neighborBondNumA * neighborBondNumB);

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
        if (isTimeOut()) return false;
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
        if (isTimeOut()) return;
        int neighborBondNumA = mcGregorHelper.getNeighborBondNumA();
        int neighborBondNumB = mcGregorHelper.getNeighborBondNumB();


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
     * Returns the borrowed mutable store of computed mappings.
     *
     * This is a live result store. Mutating it affects subsequent searches; it must not be modified during a search.
     * @return the caller-supplied list of flattened source/target index pairs
     */
    public List<List<Integer>> getMappings() {

        return mappings;
    }

    /**
     * Returns the largest retained mapping size in atom pairs.
     * @return largest retained atom-pair count, rather than the flattened list length
     */
    public int getMCSSize() {

        return this.globalMCSSize;
    }

    private void setFinalMappings(List<Integer> mappedAtoms, int mappedAtomCount) {
        try {
            if (mappedAtomCount >= globalMCSSize) {
                if (mappedAtomCount > globalMCSSize) {
                    this.globalMCSSize = mappedAtomCount;
                    mappings.clear();
                }
                mappings.add(mappedAtoms);
            }
        } catch (Exception ex) {
            LoggingToolFactory.createLoggingTool(McGregor.class)
                              .warn("Unexpected Error:", ex);
        }
    }

    private void setArcs(int xIndex, int yIndex, int arcsleft, List<Integer> tempMArcs, McgregorHelper mcGregorHelper) {
        if (isTimeOut()) return;
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
                    addCompatiblePair(atom2MoleculeA, atom2MoleculeB, currentMapping, additionalMapping);
                } else if ((mappedAtom1 == atom1MoleculeA) && (mappedAtom2 == atom2MoleculeB)) {
                    addCompatiblePair(atom2MoleculeA, atom1MoleculeB, currentMapping, additionalMapping);
                } else if ((mappedAtom1 == atom2MoleculeA) && (mappedAtom2 == atom1MoleculeB)) {
                    addCompatiblePair(atom1MoleculeA, atom2MoleculeB, currentMapping, additionalMapping);
                } else if ((mappedAtom1 == atom2MoleculeA) && (mappedAtom2 == atom2MoleculeB)) {
                    addCompatiblePair(atom1MoleculeA, atom1MoleculeB, currentMapping, additionalMapping);
                }
            }//for loop
        }
    }

    private void addCompatiblePair(int queryIndex, int targetIndex, List<Integer> current,
                                   List<Integer> additional) {
        if (!new DefaultMCSPlusAtomMatcher(source, source.getAtom(queryIndex), isBondMatch())
                .matches(target, target.getAtom(targetIndex))) return;
        for (int i = 0; i < current.size(); i += 2) {
            if (current.get(i) == queryIndex || current.get(i + 1) == targetIndex) return;
        }
        for (int i = 0; i < additional.size(); i += 2) {
            if (additional.get(i) == queryIndex || additional.get(i + 1) == targetIndex) return;
        }
        additional.add(queryIndex);
        additional.add(targetIndex);
    }

    /**
     * Reports the legacy arc-matrix selection flag.
     * @return whether the flag is set
     */
    public boolean isNewMatrix() {
        return newMatrix;
    }

    /**
     * Sets the legacy arc-matrix selection flag.
     * @param newMatrix flag value for subsequent arc-matrix processing
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
