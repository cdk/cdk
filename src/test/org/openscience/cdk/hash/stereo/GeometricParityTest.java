package org.openscience.cdk.hash.stereo;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class GeometricParityTest {
    @Test
    public void valueOf() throws Exception {
        GeometricParity odd = GeometricParity.valueOf(-1);
        GeometricParity even = GeometricParity.valueOf(1);
        assertThat(odd.parity(), is(-1));
        assertThat(even.parity(), is(+1));
    }
}
