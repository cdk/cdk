/*
 * Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 *
 * MX Cheminformatics Tools for Java
 *
 * Copyright (c) 2007-2009 Metamolecular, LLC
 *
 * http://metamolecular.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package org.openscience.cdk.smsd.algorithm.vflib.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.smsd.algorithm.matchers.VFAtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.VFBondMatcher;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IEdge;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;

/**
 * Builds a simple undirected query graph with unique atoms and no self-loops
 * or parallel edges. Atom lookup unwraps reference wrappers while the graph
 * retains the caller's original atom objects.
 *
 * <p>This mutable builder is not thread safe. Returned node and edge iterables
 * are read-only live views; nodes, matchers and borrowed atom payloads remain
 * mutable. Callers must not change graph or predicate state during matching.</p>
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class VFQueryBuilder implements IQuery {

    private final List<INode>       nodesList;
    private final List<IEdge>       edgesList;
    private final Map<INode, IAtom> nodeBondMap;
    private final Map<IAtom, INode> atomNodeMap;
    private final List<INode> nodesView;
    private final List<IEdge> edgesView;

    /**
     * Create an empty query graph.
     */
    public VFQueryBuilder() {
        nodesList = new ArrayList<>();
        edgesList = new ArrayList<>();
        nodeBondMap = new HashMap<>();
        atomNodeMap = new HashMap<>();
        nodesView = Collections.unmodifiableList(nodesList);
        edgesView = Collections.unmodifiableList(edgesList);
    }

    /**
     * Return a read-only live view of the query edges.
     * @return edges in insertion order
     */
    @Override
    public Iterable<IEdge> edges() {
        return edgesView;
    }

    /**
     * Return a read-only live view of the query nodes.
     * @return nodes in insertion order
     */
    @Override
    public Iterable<INode> nodes() {
        return nodesView;
    }

    /**
     * Return a node by insertion index.
     * @param index zero-based node index
     * @return node at the index
     * @throws IndexOutOfBoundsException if the index is outside the node list
     */
    @Override
    public INode getNode(int index) {
        return nodesList.get(index);
    }

    /**
     * Return the node for an atom or any wrapper around that atom.
     * @param atom borrowed atom, reference wrapper or null
     * @return associated node, or null if the atom is absent
     */
    public INode getNode(IAtom atom) {
        return atomNodeMap.get(AtomRef.deref(atom));
    }

    /**
     * Return an edge by insertion index.
     * @param index zero-based edge index
     * @return edge at the index
     * @throws IndexOutOfBoundsException if the index is outside the edge list
     */
    @Override
    public IEdge getEdge(int index) {
        return edgesList.get(index);
    }

    /**
     * Return the undirected edge between two nodes in this graph.
     * @param source first endpoint
     * @param target second endpoint
     * @return edge, or null for absent, identical or foreign endpoints
     */
    @Override
    public IEdge getEdge(INode source, INode target) {
        if (source == target || !nodeBondMap.containsKey(source) || !nodeBondMap.containsKey(target)) {
            return null;
        }

        NodeBuilder selected = (NodeBuilder) (source.countNeighbors() <= target.countNeighbors() ? source : target);
        INode opposite = selected == source ? target : source;

        for (IEdge edge : selected.getEdges()) {
            if (edge.getSource() == opposite || edge.getTarget() == opposite) {
                return edge;
            }
        }

        return null;
    }

    /**
     * Add a unique query atom and its matcher.
     * @param matcher directional atom matcher retained by the node
     * @param atom original borrowed atom payload
     * @return newly added node
     * @throws NullPointerException if the matcher or atom is null
     * @throws IllegalArgumentException if the underlying atom is already present
     */
    public INode addNode(VFAtomMatcher matcher, IAtom atom) {
        Objects.requireNonNull(matcher, "Query atom matcher must not be null");
        Objects.requireNonNull(atom, "Query atom must not be null");
        IAtom key = AtomRef.deref(atom);
        if (atomNodeMap.containsKey(key)) {
            throw new IllegalArgumentException("Query atoms must be unique");
        }
        NodeBuilder node = new NodeBuilder(matcher);
        nodesList.add(node);
        nodeBondMap.put(node, atom);
        atomNodeMap.put(key, node);
        return node;
    }

    /**
     * Return the original atom payload associated with a node.
     * @param node query node
     * @return borrowed atom, or null if the node is absent
     */
    @Override
    public IAtom getAtom(INode node) {
        return nodeBondMap.get(node);
    }

    /**
     * Count the nodes in this graph.
     * @return node count
     */
    @Override
    public int countNodes() {
        return nodesList.size();
    }

    /**
     * Count the undirected edges in this graph.
     * @return edge count
     */
    @Override
    public int countEdges() {
        return edgesList.size();
    }

    /**
     * Connect two distinct nodes in this graph with one undirected edge.
     * Validation completes before either endpoint's adjacency is changed.
     * @param source first endpoint belonging to this builder
     * @param target second endpoint belonging to this builder
     * @param matcher directional bond matcher retained by the edge
     * @return newly added edge
     * @throws NullPointerException if an endpoint or the matcher is null
     * @throws IllegalArgumentException for foreign endpoints, self-loops or
     *                                  parallel edges
     */
    public IEdge connect(INode source, INode target, VFBondMatcher matcher) {
        Objects.requireNonNull(source, "Query edge source must not be null");
        Objects.requireNonNull(target, "Query edge target must not be null");
        Objects.requireNonNull(matcher, "Query bond matcher must not be null");
        if (!nodeBondMap.containsKey(source) || !nodeBondMap.containsKey(target)) {
            throw new IllegalArgumentException("Query edge endpoints must belong to this query");
        }
        if (source == target) {
            throw new IllegalArgumentException("Query self-loops are not supported");
        }
        if (getEdge(source, target) != null) {
            throw new IllegalArgumentException("Parallel query edges are not supported");
        }
        NodeBuilder sourceImpl = (NodeBuilder) source;
        NodeBuilder targetImpl = (NodeBuilder) target;
        EdgeBuilder edge = new EdgeBuilder(sourceImpl, targetImpl, matcher);

        sourceImpl.addNeighbor(targetImpl);
        targetImpl.addNeighbor(sourceImpl);

        sourceImpl.addEdge(edge);
        targetImpl.addEdge(edge);

        edgesList.add(edge);
        return edge;
    }
}
