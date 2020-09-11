package org.openscience.cdk.hash;

import org.junit.Test;
import org.openscience.cdk.hash.stereo.StereoEncoder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.BitSet;
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
public class SuppressedAtomHashGeneratorTest {

    @Test
    public void testGenerate() throws Exception {

        AtomHashGenerator seedMock = mock(AtomHashGenerator.class);
        IAtomContainer container = mock(IAtomContainer.class);

        AtomHashGenerator generator = new SuppressedAtomHashGenerator(seedMock, new Xorshift(),
                AtomSuppression.unsuppressed(), 0);

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

        SuppressedAtomHashGenerator generator = new SuppressedAtomHashGenerator(mock(AtomHashGenerator.class),
                new Xorshift(), AtomSuppression.unsuppressed(), 0);

        assertThat(
                generator.generate(new long[]{1L, 1L, 1L}, StereoEncoder.EMPTY, new int[][]{{}, {}, {}},
                        Suppressed.none()), is(new long[]{1L, 1L, 1L}));

        BitSet suppressed = new BitSet();
        suppressed.set(0);
        suppressed.set(2);

        assertThat(
                generator.generate(new long[]{1L, 1L, 1L}, StereoEncoder.EMPTY, new int[][]{{}, {}, {}},
                        Suppressed.fromBitSet(suppressed)), is(new long[]{0L, 1L, 0L}));
    }

    @Test
    public void testGenerate_Disconnected() throws Exception {
        AtomHashGenerator seedMock = mock(AtomHashGenerator.class);
        IAtomContainer container = mock(IAtomContainer.class);

        SuppressedAtomHashGenerator generator = new SuppressedAtomHashGenerator(mock(AtomHashGenerator.class),
                new Xorshift(), AtomSuppression.unsuppressed(), 2);
        // there are no neighbours, the values should be rotated
        long expected = generator.distribute(generator.distribute(1));
        assertThat(
                generator.generate(new long[]{1L, 1L, 1L}, StereoEncoder.EMPTY, new int[][]{{}, {}, {}},
                        Suppressed.none()), is(new long[]{expected, expected, expected}));
        BitSet suppressed = new BitSet();
        suppressed.set(1);
        assertThat(
                generator.generate(new long[]{1L, 1L, 1L}, StereoEncoder.EMPTY, new int[][]{{}, {}, {}},
                        Suppressed.fromBitSet(suppressed)), is(new long[]{expected, 0L, expected}));

    }

    @Test
    public void testGenerate_Simple() throws Exception {
        AtomHashGenerator seedMock = mock(AtomHashGenerator.class);
        IAtomContainer container = mock(IAtomContainer.class);

        SuppressedAtomHashGenerator generator = new SuppressedAtomHashGenerator(mock(AtomHashGenerator.class),
                new Xorshift(), AtomSuppression.unsuppressed(), 2);

        // no suppression
        {
            // first iteration, values are distributed and then neighbours xor'd
            // in. when two neighbours have the same value the second should be
            // rotated
            long[] first = new long[]{generator.distribute(1) ^ 2L,
                    generator.distribute(2L) ^ 1L ^ generator.rotate(1L), generator.distribute(1) ^ 2L};

            long[] second = new long[]{generator.distribute(first[0]) ^ first[1],
                    generator.distribute(first[1]) ^ first[0] ^ generator.rotate(first[2]),
                    generator.distribute(first[2]) ^ first[1]};

            assertThat(generator.generate(new long[]{1L, 2L, 1L}, StereoEncoder.EMPTY, new int[][]{{1}, {0, 2}, {1}},
                    Suppressed.none()), is(second));
        }
        // vertex '2' supressed
        BitSet suppressed = new BitSet();
        suppressed.set(2);
        {
            long[] first = new long[]{generator.distribute(1) ^ 2L, generator.distribute(2L) ^ 1L, // generator.rotate(1L) not included is '[2]' is suppressed
                    0L,};

            long[] second = new long[]{generator.distribute(first[0]) ^ first[1], // generator.rotate(first[2]) not included is '[2]' is suppressed
                    generator.distribute(first[1]) ^ first[0], 0L};

            assertThat(generator.generate(new long[]{1L, 2L, 1L}, StereoEncoder.EMPTY, new int[][]{{1}, {0, 2}, {1}},
                    Suppressed.fromBitSet(suppressed)), is(second));
        }
    }

    @Test
    public void testRotation() throws Exception {

        AtomHashGenerator seedMock = mock(AtomHashGenerator.class);
        IAtomContainer container = mock(IAtomContainer.class);

        SuppressedAtomHashGenerator generator = new SuppressedAtomHashGenerator(mock(AtomHashGenerator.class),
                new Xorshift(), AtomSuppression.unsuppressed(), 2);

        int[][] graph = new int[][]{{1, 2, 3}, {0}, {0}, {0}};

        // simulate 3 identical neighbors
        long[] invs = new long[]{21, 31, 31, 31};
        long[] unique = new long[4];
        long[] rotated = new long[4];

        // non-suppressed
        {
            long value = generator.next(graph, 0, invs, unique, rotated, Suppressed.none());

            assertThat(unique, is(new long[]{31, 0, 0, 0}));
            assertThat(rotated, is(new long[]{generator.rotate(31, 2), 0, 0, 0}));
            assertThat(value, is(generator.distribute(21) ^ 31 ^ generator.rotate(31) ^ generator.rotate(31, 2)));
        }

        // okay now suppress vertices 1
        {
            BitSet suppressed = new BitSet();
            suppressed.set(1);

            long value = generator.next(graph, 0, invs, unique, rotated, Suppressed.fromBitSet(suppressed));

            assertThat(unique, is(new long[]{31, 0, 0, 0}));
            assertThat(rotated, is(new long[]{generator.rotate(31, 1), 0, 0, 0})); // 31 only encountered twice
            assertThat(value, is(generator.distribute(21) ^ 31 ^ generator.rotate(31)));
        }

        // okay now suppress vertices 1 and 3
        {
            BitSet suppressed = new BitSet();
            suppressed.set(1);
            suppressed.set(3);

            long value = generator.next(graph, 0, invs, unique, rotated, Suppressed.fromBitSet(suppressed));

            assertThat(unique, is(new long[]{31, 0, 0, 0}));
            assertThat(rotated, is(new long[]{31, 0, 0, 0})); // 31 only encountered once and is not rotated
            assertThat(value, is(generator.distribute(21) ^ 31)); // only encountered once
        }

    }
}
