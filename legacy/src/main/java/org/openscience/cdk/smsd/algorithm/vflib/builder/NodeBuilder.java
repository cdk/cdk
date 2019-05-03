/* Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.algorithm.vflib.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.smsd.algorithm.matchers.VFAtomMatcher;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IEdge;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;

/**
 * Class for building/storing nodes (atoms) in the graph with atom
 * query capabilities.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class NodeBuilder implements INode {

    private List<INode>   neighborsList;
    private List<IEdge>   edgesList;
    private VFAtomMatcher matcher;

    /**
     * Construct a node for a query atom
     * @param matcher
     */
    protected NodeBuilder(VFAtomMatcher matcher) {
        edgesList = new ArrayList<IEdge>();
        neighborsList = new ArrayList<INode>();
        this.matcher = matcher;
    }

    /** {@inheritDoc} */
    @Override
    public int countNeighbors() {
        return neighborsList.size();
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<INode> neighbors() {
        return Collections.unmodifiableList(neighborsList);
    }

    /** {@inheritDoc} */
    @Override
    public VFAtomMatcher getAtomMatcher() {
        return matcher;
    }

    /** {@inheritDoc} */
    @Override
    public List<IEdge> getEdges() {
        return Collections.unmodifiableList(edgesList);
    }

    /** {@inheritDoc} */
    @Override
    public void addEdge(EdgeBuilder edge) {
        edgesList.add(edge);
    }

    /** {@inheritDoc} */
    @Override
    public void addNeighbor(NodeBuilder node) {
        neighborsList.add(node);
    }
}
