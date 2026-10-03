/*
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ebi.ac.uk>
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
 * You should have received iIndex copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.mcsplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.IdentityHashMap;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smsd.algorithm.matchers.AtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.BondMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultBondMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultMCSPlusAtomMatcher;
import org.openscience.cdk.smsd.helper.LabelContainer;

/**
 * Generates the legacy compatibility graph of directional source/target atom
 * pairs. A c-edge joins pairs with compatible bonds on both molecules; a d-edge
 * joins pairs with no bond on either molecule. This helper alone does not
 * enumerate connected common subgraphs that permit bond deletion.
 *
 * <p>The helper is mutable and not thread safe. Containers and chemical
 * payloads remain borrowed and must not change during graph generation. Inputs
 * are expected to be simple graphs: loops, parallel/multicentre bonds and
 * foreign endpoints are unsupported. This legacy helper does not perform the
 * complete topology validation of the VF graph compiler and snapshots.</p>
 *
 * <p>Read-only list getters expose live views, while the protected labeled-node
 * getter retains its legacy mutable view. Callers should copy lists needed
 * beyond a rebuild or release of this helper.</p>
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public final class GenerateCompatibilityGraph {

    private List<Integer>  compGraphNodes      = null;
    private List<Integer>  compGraphNodesCZero = null;
    private List<Integer>  cEdges              = null;
    private List<Integer>  dEdges              = null;
    private int            cEdgesSize          = 0;
    private int            dEdgesSize          = 0;
    private IAtomContainer source              = null;
    private IAtomContainer target              = null;
    private boolean        shouldMatchBonds    = false;

    /**
    * Create an uninitialized legacy helper. Graph-building and getter methods
    * require an instance created with the molecular constructor; bond-policy
    * accessors remain usable on this uninitialized instance.
    */
    public GenerateCompatibilityGraph() {

    }

    /**
     * Generate atom-pair nodes and compatibility edges for two molecules.
     * @param source borrowed source molecule containing directional predicates
     * @param target borrowed target molecule
     * @param shouldMatchBonds whether ordinary bond chemistry must match
     * @throws IOException retained by the legacy graph-generation contract
     */
    public GenerateCompatibilityGraph(IAtomContainer source, IAtomContainer target, boolean shouldMatchBonds)
            throws IOException {
        setMatchBond(shouldMatchBonds);
        this.source = source;
        this.target = target;
        compGraphNodes = new ArrayList<>();
        compGraphNodesCZero = new ArrayList<>();
        cEdges = new ArrayList<>();
        dEdges = new ArrayList<>();
        compatibilityGraphNodes();
        compatibilityGraph();

    }

    /**
     * Rebuild compatible atom pairs as source-index/target-index/vertex triples.
     * @return legacy success status, always zero
     * @throws IOException retained by the legacy graph-generation contract
     */
    protected int compatibilityGraphNodes() throws IOException {

        buildNodes(false);
        return 0;
    }

    private int buildNodes(boolean includeLabels) {
        compGraphNodes.clear();
        compGraphNodesCZero.clear();
        int countNodes = 1;
        for (int i = 0; i < source.getAtomCount(); i++) {
            AtomMatcher matcher = new DefaultMCSPlusAtomMatcher(source, source.getAtom(i), shouldMatchBonds);
            for (int j = 0; j < target.getAtomCount(); j++) {
                // Original neighborhoods are not a valid MCS pruning rule:
                // atoms may lose neighbors in the common substructure.
                if (!matcher.matches(target, target.getAtom(j))) continue;
                compGraphNodes.add(i);
                compGraphNodes.add(j);
                compGraphNodes.add(countNodes);
                if (includeLabels) {
                    compGraphNodesCZero.add(i);
                    compGraphNodesCZero.add(j);
                    String symbol = source.getAtom(i).getSymbol();
                    compGraphNodesCZero.add(symbol == null ? 0 : LabelContainer.getInstance().getLabelID(symbol));
                    compGraphNodesCZero.add(countNodes);
                }
                countNodes++;
            }
        }
        return countNodes;
    }

    /**
     * Rebuild c-edges and d-edges from the ordinary atom-pair triples.
     * @return legacy success status, always zero
     * @throws IOException retained by the legacy graph-generation contract
     */
    protected int compatibilityGraph() throws IOException {
        buildEdges(compGraphNodes, 3);
        return 0;
    }

    /**
     * Rebuild compatible atom pairs and the legacy labeled quadruple view.
     * @return next unused vertex identifier after the generated nodes
     * @throws IOException retained by the legacy graph-generation contract
     */
    protected Integer compatibilityGraphNodesIfCEdgeIsZero() throws IOException {
        return buildNodes(true);
    }

    /**
     * Rebuild compatibility edges from the labeled quadruple node view.
     * @return legacy success status, always zero
     * @throws IOException retained by the legacy graph-generation contract
     */
    protected int compatibilityGraphCEdgeZero() throws IOException {
        buildEdges(compGraphNodesCZero, 4);
        return 0;
    }

    private void buildEdges(List<Integer> nodes, int stride) {
        cEdges.clear();
        dEdges.clear();
        Map<IBond, BondMatcher> bondMatchers = new IdentityHashMap<>();
        for (int a = 0; a < nodes.size(); a += stride) {
            int sourceA = nodes.get(a);
            int targetA = nodes.get(a + 1);
            for (int b = a + stride; b < nodes.size(); b += stride) {
                int sourceB = nodes.get(b);
                int targetB = nodes.get(b + 1);
                if (sourceA == sourceB || targetA == targetB) continue;
                IBond sourceBond = source.getBond(source.getAtom(sourceA), source.getAtom(sourceB));
                IBond targetBond = target.getBond(target.getAtom(targetA), target.getAtom(targetB));
                List<Integer> edges;
                if (sourceBond == null && targetBond == null) {
                    edges = dEdges;
                } else if (sourceBond != null && targetBond != null) {
                    BondMatcher matcher = bondMatchers.get(sourceBond);
                    if (matcher == null) {
                        matcher = new DefaultBondMatcher(source, sourceBond, shouldMatchBonds);
                        bondMatchers.put(sourceBond, matcher);
                    }
                    if (!matcher.matches(target, targetBond)) continue;
                    edges = cEdges;
                } else {
                    continue;
                }
                edges.add(nodes.get(a + stride - 1));
                edges.add(nodes.get(b + stride - 1));
            }
        }
        cEdgesSize = cEdges.size();
        dEdgesSize = dEdges.size();
    }

    /**
     * Return the read-only live c-edge endpoint pairs.
     * @return flattened vertex identifier pairs
     */
    public List<Integer> getCEgdes() {
        return Collections.unmodifiableList(cEdges);
    }

    /**
     * Return the read-only live d-edge endpoint pairs.
     * @return flattened vertex identifier pairs
     */
    protected List<Integer> getDEgdes() {
        return Collections.unmodifiableList(dEdges);
    }

    /**
     * Return the read-only live atom-pair node triples.
     * @return flattened source-index/target-index/vertex triples
     */
    protected List<Integer> getCompGraphNodes() {
        return Collections.unmodifiableList(compGraphNodes);
    }

    /**
     * Return the cached size of the flattened c-edge list.
     * @return endpoint integer count, twice the number of edges
     */
    protected int getCEdgesSize() {
        return cEdgesSize;
    }

    /**
     * Return the cached size of the flattened d-edge list.
     * @return endpoint integer count, twice the number of edges
     */
    protected int getDEdgesSize() {
        return dEdgesSize;
    }

    /**
     * Return the legacy mutable live labeled atom-pair view.
     * @return flattened source-index/target-index/label/vertex quadruples
     */
    protected List<Integer> getCompGraphNodesCZero() {
        return compGraphNodesCZero;
    }

    /** Empty c-edge endpoints without resetting their cached size. */
    protected void clearCEgdes() {
        cEdges.clear();
    }

    /** Empty d-edge endpoints without resetting their cached size. */
    protected void clearDEgdes() {
        dEdges.clear();
    }

    /** Empty the ordinary atom-pair triples. */
    protected void clearCompGraphNodes() {
        compGraphNodes.clear();
    }

    /** Empty the legacy labeled atom-pair quadruples. */
    protected void clearCompGraphNodesCZero() {
        compGraphNodesCZero.clear();
    }

    /** Reset the cached c-edge endpoint count without changing the list. */
    protected void resetCEdgesSize() {
        cEdgesSize = 0;
    }

    /** Reset the cached d-edge endpoint count without changing the list. */
    protected void resetDEdgesSize() {
        dEdgesSize = 0;
    }

    /**
     * Release graph list references, leaving this legacy helper uninitialized.
     * Previously returned views retain their backing lists; cached sizes and
     * bond policy are unchanged. Graph access or rebuilding requires initialized
     * lists and is unsupported after this release.
     */
    public void clear() {
        cEdges = null;
        dEdges = null;
        compGraphNodes = null;
        compGraphNodesCZero = null;
    }

    /**
     * Return the ordinary bond matching policy for future graph generation.
     * @return whether ordinary bond chemistry must match
     */
    public boolean isMatchBond() {
        return shouldMatchBonds;
    }

    /**
     * Set the ordinary bond matching policy for subsequent graph rebuilding.
     * Existing encoded edges are unchanged until rebuilt.
     * @param shouldMatchBonds whether ordinary bond chemistry must match
     */
    public void setMatchBond(boolean shouldMatchBonds) {
        this.shouldMatchBonds = shouldMatchBonds;
    }
}
