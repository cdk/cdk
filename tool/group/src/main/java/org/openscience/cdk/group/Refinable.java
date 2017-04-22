/* Copyright (C) 2017  Gilleain Torrance <gilleain.torrance@gmail.com>
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
package org.openscience.cdk.group;

/**
 * Implementors are graph-like objects that are refinable by the 
 * equitable and discrete partition refiners.
 * 
 * @author maclean
 * @cdk.module group
 *
 */
public interface Refinable {
    
    /**
     * Get the number of vertices in the graph to be refined.
     *
     * @return a count of the vertices in the underlying graph
     */
    public int getVertexCount();
    
    /**
     * Get the connectivity between two vertices as an integer, to allow
     * for multigraphs : so a single edge is 1, a double edge 2, etc. If
     * there is no edge, then 0 should be returned.
     *
     * @param vertexI a vertex of the graph
     * @param vertexJ a vertex of the graph
     * @return the multiplicity of the edge (0, 1, 2, 3, ...)
     */
    public int getConnectivity(int vertexI, int vertexJ);
    
    /**
     * Get the connected indices for the given vertex index.
     * 
     * @param vertexIndex
     * @return
     */
    public int[] getConnectedIndices(int vertexIndex);

    /**
     * @return the maximum connectivity of the refinable
     */
    public int getMaxConnectivity();

}
