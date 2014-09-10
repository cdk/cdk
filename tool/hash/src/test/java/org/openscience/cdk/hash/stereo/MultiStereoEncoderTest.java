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
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class MultiStereoEncoderTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstruction_Empty() {
        new MultiStereoEncoder(Collections.<StereoEncoder> emptyList());
    }

    @Test
    public void testEncode() throws Exception {

        StereoEncoder a = mock(StereoEncoder.class);
        StereoEncoder b = mock(StereoEncoder.class);

        StereoEncoder encoder = new MultiStereoEncoder(Arrays.asList(a, b));

        long[] current = new long[5];
        long[] next = new long[5];

        when(a.encode(current, next)).thenReturn(true);
        when(b.encode(current, next)).thenReturn(true);

        // configured once
        assertTrue(encoder.encode(current, next));

        // not configured again
        assertFalse(encoder.encode(current, next));

        verify(a, times(1)).encode(current, next);
        verify(b, times(1)).encode(current, next);

    }

    @Test
    public void testReset() throws Exception {
        StereoEncoder a = mock(StereoEncoder.class);
        StereoEncoder b = mock(StereoEncoder.class);

        StereoEncoder encoder = new MultiStereoEncoder(Arrays.asList(a, b));

        long[] current = new long[0];
        long[] next = new long[0];

        when(a.encode(current, next)).thenReturn(true);
        when(b.encode(current, next)).thenReturn(true);

        // configured once
        assertTrue(encoder.encode(current, next));

        // not configured again
        assertFalse(encoder.encode(current, next));

        verify(a, times(1)).encode(current, next);
        verify(b, times(1)).encode(current, next);

        encoder.reset();

        assertTrue(encoder.encode(current, next));

        verify(a, times(2)).encode(current, next);
        verify(b, times(2)).encode(current, next);
    }

}
