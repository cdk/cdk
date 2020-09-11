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

package org.openscience.cdk.hash.stereo;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class GeometryEncoderTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstruction_Empty() {
        new GeometryEncoder(new int[0], mock(PermutationParity.class), mock(GeometricParity.class));
    }

    @Test
    public void testConstruction_Singleton() {
        PermutationParity permutation = mock(PermutationParity.class);
        GeometricParity geometric = mock(GeometricParity.class);

        when(permutation.parity(any(long[].class))).thenReturn(-1);
        when(geometric.parity()).thenReturn(+1);

        StereoEncoder encoder = new GeometryEncoder(1, permutation, geometric);
        long[] prev = new long[3];
        long[] result = new long[3];
        Arrays.fill(prev, 1);
        Arrays.fill(result, 1);

        // check returned true
        assertTrue(encoder.encode(prev, result));

        // check only the value at index '1' was changed
        assertThat(result, is(new long[]{1, 15543053, 1}));
    }

    @Test
    public void testEncode_Clockwise() throws Exception {

        PermutationParity permutation = mock(PermutationParity.class);
        GeometricParity geometric = mock(GeometricParity.class);

        when(permutation.parity(any(long[].class))).thenReturn(-1);
        when(geometric.parity()).thenReturn(+1);

        StereoEncoder encoder = new GeometryEncoder(new int[]{1}, permutation, geometric);
        long[] prev = new long[3];
        long[] result = new long[3];
        Arrays.fill(prev, 1);
        Arrays.fill(result, 1);

        // check returned true
        assertTrue(encoder.encode(prev, result));

        // check only the value at index '1' was changed
        assertThat(result, is(new long[]{1, 15543053, 1}));

    }

    @Test
    public void testEncode_Clockwise_Alt() throws Exception {

        PermutationParity permutation = mock(PermutationParity.class);
        GeometricParity geometric = mock(GeometricParity.class);

        when(permutation.parity(any(long[].class))).thenReturn(+1);
        when(geometric.parity()).thenReturn(-1);

        StereoEncoder encoder = new GeometryEncoder(new int[]{1}, permutation, geometric);
        long[] prev = new long[3];
        long[] result = new long[3];
        Arrays.fill(prev, 1);
        Arrays.fill(result, 1);

        // check returned true
        assertTrue(encoder.encode(prev, result));

        // check only the value at index '1' was changed
        assertThat(result, is(new long[]{1, 15543053, 1}));

    }

    @Test
    public void testEncode_Clockwise_Two() throws Exception {

        PermutationParity permutation = mock(PermutationParity.class);
        GeometricParity geometric = mock(GeometricParity.class);

        when(permutation.parity(any(long[].class))).thenReturn(-1);
        when(geometric.parity()).thenReturn(+1);

        StereoEncoder encoder = new GeometryEncoder(new int[]{1, 3}, permutation, geometric);
        long[] prev = new long[6];
        long[] result = new long[6];
        Arrays.fill(prev, 1);
        Arrays.fill(result, 1);

        // check returned true
        assertTrue(encoder.encode(prev, result));

        // check only the value at index '1' was changed
        assertThat(result, is(new long[]{1, 15543053, 1, 15543053, 1, 1}));

    }

    @Test
    public void testEncode_Anticlockwise() throws Exception {

        PermutationParity permutation = mock(PermutationParity.class);
        GeometricParity geometric = mock(GeometricParity.class);

        when(permutation.parity(any(long[].class))).thenReturn(+1);
        when(geometric.parity()).thenReturn(+1);

        StereoEncoder encoder = new GeometryEncoder(new int[]{1}, permutation, geometric);
        long[] prev = new long[3];
        long[] result = new long[3];
        Arrays.fill(prev, 1);
        Arrays.fill(result, 1);

        // check returned true
        assertTrue(encoder.encode(prev, result));

        // check only the value at index '1' was changed
        assertThat(result, is(new long[]{1, 15521419, 1}));

    }

    @Test
    public void testEncode_Anticlockwise_Alt() throws Exception {

        PermutationParity permutation = mock(PermutationParity.class);
        GeometricParity geometric = mock(GeometricParity.class);

        when(permutation.parity(any(long[].class))).thenReturn(-1);
        when(geometric.parity()).thenReturn(-1);

        StereoEncoder encoder = new GeometryEncoder(new int[]{1}, permutation, geometric);
        long[] prev = new long[3];
        long[] result = new long[3];
        Arrays.fill(prev, 1);
        Arrays.fill(result, 1);

        // check returned true
        assertTrue(encoder.encode(prev, result));

        // check only the value at index '1' was changed
        assertThat(result, is(new long[]{1, 15521419, 1}));

    }

    @Test
    public void testEncode_Anticlockwise_Two() throws Exception {

        PermutationParity permutation = mock(PermutationParity.class);
        GeometricParity geometric = mock(GeometricParity.class);

        when(permutation.parity(any(long[].class))).thenReturn(+1);
        when(geometric.parity()).thenReturn(+1);

        StereoEncoder encoder = new GeometryEncoder(new int[]{1, 3}, permutation, geometric);
        long[] prev = new long[6];
        long[] result = new long[6];
        Arrays.fill(prev, 1);
        Arrays.fill(result, 1);

        // check returned true
        assertTrue(encoder.encode(prev, result));

        // check only the value at index '1' was changed
        assertThat(result, is(new long[]{1, 15521419, 1, 15521419, 1, 1}));

    }

    @Test
    public void testEncode_NoGeometry() {
        PermutationParity permutation = mock(PermutationParity.class);
        GeometricParity geometric = mock(GeometricParity.class);

        when(permutation.parity(any(long[].class))).thenReturn(+1);
        when(geometric.parity()).thenReturn(0);

        StereoEncoder encoder = new GeometryEncoder(new int[]{1, 3}, permutation, geometric);
        long[] prev = new long[6];
        long[] result = new long[6];
        Arrays.fill(prev, 1);
        Arrays.fill(result, 1);

        // check returned true. the permutation was okay, but no geometry, this
        // will never change
        assertTrue(encoder.encode(prev, result));

        // check no values modified
        assertThat(result, is(new long[]{1, 1, 1, 1, 1, 1}));
    }

    @Test
    public void testEncode_NoPermutation() {
        PermutationParity permutation = mock(PermutationParity.class);
        GeometricParity geometric = mock(GeometricParity.class);

        when(permutation.parity(any(long[].class))).thenReturn(0);
        when(geometric.parity()).thenReturn(+1);

        StereoEncoder encoder = new GeometryEncoder(new int[]{1, 3}, permutation, geometric);
        long[] prev = new long[6];
        long[] result = new long[6];
        Arrays.fill(prev, 1);
        Arrays.fill(result, 1);

        // check returned false, the permutation changes for each cycle
        assertFalse(encoder.encode(prev, result));

        // check no values modified
        assertThat(result, is(new long[]{1, 1, 1, 1, 1, 1}));

        // geometric parity should not be called
        verify(geometric, never()).parity();
    }

    @Test
    public void testReset() throws Exception {
        // no method body to test
    }
}
