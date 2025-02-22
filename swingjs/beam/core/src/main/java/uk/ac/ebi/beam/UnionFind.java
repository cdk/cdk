/*
 * Copyright (c) 2013, European Bioinformatics Institute (EMBL-EBI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */

package uk.ac.ebi.beam;

import java.util.Arrays;

/**
 * Fixed size Union-Find/Disjoint-Set implementation.
 * 
 * <blockquote>
 * UnionFind uf = new UnionFind(11);
 * uf.join(0, 1);
 * uf.join(1, 10);
 * uf.connected(0, 10); // are 0 and 10 joint?
 * uf.find(10);         // id for the set to which '10' belongs
 * </blockquote>
 *
 * @author John May
 */
final class UnionFind {

    /**
     * Each element is either a connected (negative), points to another element.
     * The size of the set is indicated by the size of the negation on the
     * connected.
     */
    final int[] forest;

    /**
     * Create a new UnionFind data structure with enough space for 'n'
     * elements.
     *
     * @param n number of elements
     */
    UnionFind(int n) {
        this.forest = new int[n];
        Arrays.fill(forest, -1);
    }

    /**
     * Find the identifier of the set to which 'u' belongs.
     *
     * @param u an element
     * @return the connected
     */
    int find(int u) {
        return forest[u] < 0 ? u : (forest[u] = find(forest[u]));
    }

    /**
     * Join the sets containing 'u' and 'v'.
     *
     * @param u an element
     * @param v another element
     */
    void union(int u, int v) {

        int uRoot = find(u);
        int vRoot = find(v);

        if (uRoot == vRoot)
            return;
        
        if (forest[uRoot] < forest[vRoot])
            join(vRoot, uRoot);
        else
            join(uRoot, vRoot);
    }

    /**
     * Join two disjoint sets. The larger set is appended onto the smaller set.
     *
     * @param sRoot root of a set (small)
     * @param lRoot root of another set (large)
     */
    private void join(int sRoot, int lRoot) {
        forest[sRoot] = forest[sRoot] + forest[lRoot];
        forest[lRoot] = sRoot;
    }

    /**
     * Are the elements 'u' and 'v' in the same set.
     *
     * @param u an element
     * @param v another element
     * @return the elements are in the same set.
     */
    boolean connected(int u, int v) {
        return find(u) == find(v);
    }

    /**
     * Clear any joint sets - all items are once disjoint and are singletons.
     */
    void clear() {
        for (int i = 0; i < forest.length; i++)
            forest[i] = -1;
    }
}
