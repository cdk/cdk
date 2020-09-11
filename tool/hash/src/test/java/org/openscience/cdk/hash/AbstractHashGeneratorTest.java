/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class AbstractHashGeneratorTest {

    @Test(expected = NullPointerException.class)
    public void testConstruction_Null() {
        new AbstractHashGenerator(null);
    }

    @Test
    public void testCopy() throws Exception {
        long[] x = new long[]{2, 1, 3, 2};
        long[] y = AbstractHashGenerator.copy(x);
        assertThat(x, is(y));
        assertThat(x, not(sameInstance(y)));
    }

    @Test
    public void testCopy_SrcDest() throws Exception {
        long[] x = new long[]{42, 23, 1, 72};
        long[] y = new long[4];
        AbstractHashGenerator.copy(x, y);
        assertThat(x, is(y));
        assertThat(x, not(sameInstance(y)));
    }

    @Test
    public void testRotate() throws Exception {
        Pseudorandom pseudorandom = mock(Pseudorandom.class);
        AbstractHashGenerator f = new AbstractHashGenerator(pseudorandom);
        f.rotate(5L);
        verify(pseudorandom, times(1)).next(5L);
    }

    @Test
    public void testRotate_N() throws Exception {
        Pseudorandom pseudorandom = mock(Pseudorandom.class);
        AbstractHashGenerator f = new AbstractHashGenerator(pseudorandom);
        f.rotate(0, 5); // note 0 doesn't rotate...
        verify(pseudorandom, times(5)).next(anyLong());
    }

    @Test
    public void testLowestThreeBits() throws Exception {
        assertThat(AbstractHashGenerator.lowestThreeBits(0L), is(0));
        assertThat(AbstractHashGenerator.lowestThreeBits(1L), is(1));
        assertThat(AbstractHashGenerator.lowestThreeBits(2L), is(2));
        assertThat(AbstractHashGenerator.lowestThreeBits(3L), is(3));
        assertThat(AbstractHashGenerator.lowestThreeBits(4L), is(4));
        assertThat(AbstractHashGenerator.lowestThreeBits(5L), is(5));
        assertThat(AbstractHashGenerator.lowestThreeBits(6L), is(6));
        assertThat(AbstractHashGenerator.lowestThreeBits(7L), is(7));

        // check we don't exceed 7
        assertThat(AbstractHashGenerator.lowestThreeBits(8L), is(0));
        assertThat(AbstractHashGenerator.lowestThreeBits(9L), is(1));
        assertThat(AbstractHashGenerator.lowestThreeBits(10L), is(2));
        assertThat(AbstractHashGenerator.lowestThreeBits(11L), is(3));
        assertThat(AbstractHashGenerator.lowestThreeBits(12L), is(4));
        assertThat(AbstractHashGenerator.lowestThreeBits(13L), is(5));
        assertThat(AbstractHashGenerator.lowestThreeBits(14L), is(6));
        assertThat(AbstractHashGenerator.lowestThreeBits(15L), is(7));
        assertThat(AbstractHashGenerator.lowestThreeBits(16L), is(0));

        // max/min numbers
        assertThat(AbstractHashGenerator.lowestThreeBits(Long.MAX_VALUE), is(7));
        assertThat(AbstractHashGenerator.lowestThreeBits(Long.MIN_VALUE), is(0));
    }

    @Test
    public void testDistribute_AtLeastOnce() throws Exception {
        Pseudorandom pseudorandom = mock(Pseudorandom.class);
        AbstractHashGenerator f = new AbstractHashGenerator(pseudorandom);
        long x = f.distribute(8L); // lowest 3 bits = 0, make sure we rotate 1
        verify(pseudorandom, times(1)).next(anyLong());
        assertThat(x, is(not(8L)));
    }

    @Test
    public void testDistribute() throws Exception {
        Pseudorandom pseudorandom = mock(Pseudorandom.class);
        AbstractHashGenerator f = new AbstractHashGenerator(pseudorandom);
        long x = f.distribute(5L); // lowest 3 bits = 5, rotate 6 times
        verify(pseudorandom, times(6)).next(anyLong());
        assertThat(x, is(not(5L)));
    }

    @Test
    public void testToAdjList() {
        // already tests in ShortestPaths... this method be moved once all
        // pending patches are merged
    }
}
