package org.openscience.cdk.hash;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class SeedGeneratorTest {

    @Test(expected = NullPointerException.class)
    public void testConstruct_Null() {
        new SeedGenerator(null);
    }

    @Test
    public void testGenerate() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);

        AtomEncoder encoder = mock(AtomEncoder.class);
        SeedGenerator generator = new SeedGenerator(encoder);

        IAtom c1 = mock(IAtom.class);
        IAtom c2 = mock(IAtom.class);
        IAtom c3 = mock(IAtom.class);
        IAtom c4 = mock(IAtom.class);
        IAtom c5 = mock(IAtom.class);

        when(container.getAtomCount()).thenReturn(5);
        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(c2);
        when(container.getAtom(2)).thenReturn(c3);
        when(container.getAtom(3)).thenReturn(c4);
        when(container.getAtom(4)).thenReturn(c5);

        when(encoder.encode(c1, container)).thenReturn(42);
        when(encoder.encode(c2, container)).thenReturn(42);
        when(encoder.encode(c3, container)).thenReturn(42);
        when(encoder.encode(c4, container)).thenReturn(42);
        when(encoder.encode(c5, container)).thenReturn(42);

        generator.generate(container);

        verify(container, times(1)).getAtomCount();

        verify(container, times(5)).getAtom(anyInt());

        verify(encoder, times(1)).encode(c1, container);
        verify(encoder, times(1)).encode(c2, container);
        verify(encoder, times(1)).encode(c3, container);
        verify(encoder, times(1)).encode(c4, container);
        verify(encoder, times(1)).encode(c5, container);

        verifyNoMoreInteractions(c1, c2, c3, c4, c5, container, encoder);
    }

    @Test
    public void testGenerate_SizeSeeding() throws Exception {

        IAtomContainer m1 = mock(IAtomContainer.class);
        IAtomContainer m2 = mock(IAtomContainer.class);

        AtomEncoder encoder = mock(AtomEncoder.class);
        SeedGenerator generator = new SeedGenerator(encoder);

        IAtom c1 = mock(IAtom.class);
        IAtom c2 = mock(IAtom.class);
        IAtom c3 = mock(IAtom.class);
        IAtom c4 = mock(IAtom.class);
        IAtom c5 = mock(IAtom.class);
        IAtom c6 = mock(IAtom.class);

        when(m1.getAtomCount()).thenReturn(5);
        when(m1.getAtom(0)).thenReturn(c1);
        when(m1.getAtom(1)).thenReturn(c2);
        when(m1.getAtom(2)).thenReturn(c3);
        when(m1.getAtom(3)).thenReturn(c4);
        when(m1.getAtom(4)).thenReturn(c5);

        when(m2.getAtomCount()).thenReturn(6);
        when(m2.getAtom(0)).thenReturn(c1);
        when(m2.getAtom(1)).thenReturn(c2);
        when(m2.getAtom(2)).thenReturn(c3);
        when(m2.getAtom(3)).thenReturn(c4);
        when(m2.getAtom(4)).thenReturn(c5);
        when(m2.getAtom(5)).thenReturn(c6);

        when(encoder.encode(c1, m1)).thenReturn(42);
        when(encoder.encode(c2, m1)).thenReturn(42);
        when(encoder.encode(c3, m1)).thenReturn(42);
        when(encoder.encode(c4, m1)).thenReturn(42);
        when(encoder.encode(c5, m1)).thenReturn(42);

        when(encoder.encode(c1, m2)).thenReturn(42);
        when(encoder.encode(c2, m2)).thenReturn(42);
        when(encoder.encode(c3, m2)).thenReturn(42);
        when(encoder.encode(c4, m2)).thenReturn(42);
        when(encoder.encode(c5, m2)).thenReturn(42);
        when(encoder.encode(c6, m2)).thenReturn(42);

        long[] v1 = generator.generate(m1);
        long[] v2 = generator.generate(m2);

        verify(m1, times(1)).getAtomCount();
        verify(m2, times(1)).getAtomCount();

        verify(m1, times(5)).getAtom(anyInt());
        verify(m2, times(6)).getAtom(anyInt());

        verify(encoder, times(1)).encode(c1, m1);
        verify(encoder, times(1)).encode(c2, m1);
        verify(encoder, times(1)).encode(c3, m1);
        verify(encoder, times(1)).encode(c4, m1);
        verify(encoder, times(1)).encode(c5, m1);

        verify(encoder, times(1)).encode(c1, m2);
        verify(encoder, times(1)).encode(c2, m2);
        verify(encoder, times(1)).encode(c3, m2);
        verify(encoder, times(1)).encode(c4, m2);
        verify(encoder, times(1)).encode(c5, m2);
        verify(encoder, times(1)).encode(c6, m2);

        // check the value were different (due to molecule size)
        assertThat(v1.length, is(5));
        assertThat(v2.length, is(6));
        for (int i = 0; i < v1.length; i++) {
            assertThat(v1[i], is(not(v2[i])));
        }

        verifyNoMoreInteractions(m1, m2, c1, c2, c3, c4, c5, c6, encoder);

    }

}
