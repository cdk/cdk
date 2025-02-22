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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/** @author John May */
public class UnionFindTest {

    @Test public void connected() {
        UnionFind uf = new UnionFind(100);
        uf.union(1, 5);
        uf.union(7, 9);
        uf.union(7, 5);
        uf.union(10, 11);
        uf.union(11, 50);
        uf.union(15, 1);
        uf.union(15, 50);
        assertTrue(uf.connected(1, 5));
        assertTrue(uf.connected(1, 7));
        assertTrue(uf.connected(1, 9));
        assertTrue(uf.connected(1, 10));
        assertTrue(uf.connected(1, 11));
        assertTrue(uf.connected(1, 15));
        assertTrue(uf.connected(1, 50));        
    }

    @Test public void find() {
        UnionFind uf = new UnionFind(100);
        uf.union(1, 5);
        uf.union(7, 9);
        uf.union(10, 11);
        uf.union(15, 1);
        uf.union(15, 50);
        assertThat(uf.find(1), is(50));
        assertThat(uf.find(5), is(50));
        assertThat(uf.find(7), is(7));
        assertThat(uf.find(8), is(8));
        assertThat(uf.find(10), is(10));
        assertThat(uf.find(11), is(10));
        assertThat(uf.find(15), is(50));
        assertThat(uf.find(50), is(50));
    }       
}
