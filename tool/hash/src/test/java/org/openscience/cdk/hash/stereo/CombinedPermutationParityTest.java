package org.openscience.cdk.hash.stereo;

import org.junit.Test;
import org.openscience.cdk.hash.stereo.CombinedPermutationParity;
import org.openscience.cdk.hash.stereo.PermutationParity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class CombinedPermutationParityTest {

    @Test
    public void testParity() throws Exception {
        PermutationParity left = mock(PermutationParity.class);
        PermutationParity right = mock(PermutationParity.class);
        PermutationParity parity = new CombinedPermutationParity(left, right);

        long[] dummy = new long[5];

        when(left.parity(dummy)).thenReturn(-1);
        when(right.parity(dummy)).thenReturn(-1);
        assertThat(parity.parity(dummy), is(1));

        verify(left, times(1)).parity(dummy);
        verify(right, times(1)).parity(dummy);

        when(left.parity(dummy)).thenReturn(-1);
        when(right.parity(dummy)).thenReturn(1);
        assertThat(parity.parity(dummy), is(-1));

        when(left.parity(dummy)).thenReturn(1);
        when(right.parity(dummy)).thenReturn(-1);
        assertThat(parity.parity(dummy), is(-1));

        when(left.parity(dummy)).thenReturn(1);
        when(right.parity(dummy)).thenReturn(1);
        assertThat(parity.parity(dummy), is(1));

        when(left.parity(dummy)).thenReturn(0);
        when(right.parity(dummy)).thenReturn(1);
        assertThat(parity.parity(dummy), is(0));

        when(left.parity(dummy)).thenReturn(1);
        when(right.parity(dummy)).thenReturn(0);
        assertThat(parity.parity(dummy), is(0));

        when(left.parity(dummy)).thenReturn(0);
        when(right.parity(dummy)).thenReturn(-1);
        assertThat(parity.parity(dummy), is(0));

        when(left.parity(dummy)).thenReturn(-1);
        when(right.parity(dummy)).thenReturn(0);
        assertThat(parity.parity(dummy), is(0));
    }
}
