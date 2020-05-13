package org.openscience.cdk.hash;

import org.junit.Test;
import org.openscience.cdk.hash.stereo.StereoEncoder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.Iterator;

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
public class BasicAtomHashGeneratorTest {

    @Test
    public void testGenerate() throws Exception {

        AtomHashGenerator seedMock = mock(AtomHashGenerator.class);
        IAtomContainer container = mock(IAtomContainer.class);

        AtomHashGenerator generator = new BasicAtomHashGenerator(seedMock, new Xorshift(), 0);

        when(seedMock.generate(container)).thenReturn(new long[0]);
        when(container.bonds()).thenReturn(new Iterable<IBond>() {

            @Override
            public Iterator<IBond> iterator() {
                return new Iterator<IBond>() {

                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public IBond next() {
                        return null;
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        });

        generator.generate(container);

        verify(seedMock, times(1)).generate(container);
    }

    @Test
    public void testGenerate_ZeroDepth() throws Exception {

        AtomHashGenerator seedMock = mock(AtomHashGenerator.class);
        IAtomContainer container = mock(IAtomContainer.class);

        BasicAtomHashGenerator generator = new BasicAtomHashGenerator(mock(AtomHashGenerator.class), new Xorshift(), 0);

        assertThat(
                generator.generate(new long[]{1L, 1L, 1L}, StereoEncoder.EMPTY, new int[][]{{}, {}, {}},
                        Suppressed.none()), is(new long[]{1L, 1L, 1L}));
    }

    @Test
    public void testGenerate_Disconnected() throws Exception {
        AtomHashGenerator seedMock = mock(AtomHashGenerator.class);
        IAtomContainer container = mock(IAtomContainer.class);

        BasicAtomHashGenerator generator = new BasicAtomHashGenerator(mock(AtomHashGenerator.class), new Xorshift(), 2);
        // there are no neighbours, the values should be rotated
        long expected = generator.distribute(generator.distribute(1));
        assertThat(
                generator.generate(new long[]{1L, 1L, 1L}, StereoEncoder.EMPTY, new int[][]{{}, {}, {}},
                        Suppressed.none()), is(new long[]{expected, expected, expected}));

    }

    @Test
    public void testGenerate_Simple() throws Exception {
        AtomHashGenerator seedMock = mock(AtomHashGenerator.class);
        IAtomContainer container = mock(IAtomContainer.class);

        BasicAtomHashGenerator generator = new BasicAtomHashGenerator(mock(AtomHashGenerator.class), new Xorshift(), 2);

        // first iteration, values are distributed and then neighbours xor'd
        // in. when two neighbout have the same value the second should be
        // rotated
        long[] first = new long[]{generator.distribute(1) ^ 2L, generator.distribute(2L) ^ 1L ^ generator.rotate(1L),
                generator.distribute(1) ^ 2L};

        long[] second = new long[]{generator.distribute(first[0]) ^ first[1],
                generator.distribute(first[1]) ^ first[0] ^ generator.rotate(first[2]),
                generator.distribute(first[2]) ^ first[1]};

        assertThat(generator.generate(new long[]{1L, 2L, 1L}, StereoEncoder.EMPTY, new int[][]{{1}, {0, 2}, {1}},
                Suppressed.none()), is(second));

    }

    @Test
    public void testRotation() throws Exception {

        AtomHashGenerator seedMock = mock(AtomHashGenerator.class);
        IAtomContainer container = mock(IAtomContainer.class);

        BasicAtomHashGenerator generator = new BasicAtomHashGenerator(mock(AtomHashGenerator.class), new Xorshift(), 2);

        int[][] graph = new int[][]{{1, 2, 3}, {0}, {0}, {0}};

        // simulate 3 identical neighbors
        long[] invs = new long[]{21, 31, 31, 31};
        long[] unique = new long[4];
        long[] rotated = new long[4];

        long value = generator.next(graph, 0, invs, unique, rotated);

        assertThat(unique, is(new long[]{31, 0, 0, 0}));
        assertThat(rotated, is(new long[]{generator.rotate(31, 2), 0, 0, 0}));
        assertThat(value, is(generator.distribute(21) ^ 31 ^ generator.rotate(31) ^ generator.rotate(31, 2)));

    }
}
