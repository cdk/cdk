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
import org.openscience.cdk.interfaces.IAtomContainer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class BasicMoleculeHashGeneratorTest {

    @Test(expected = NullPointerException.class)
    public void testConstruct_Null() {
        new BasicMoleculeHashGenerator(null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstruct_NullPRNG() {
        new BasicMoleculeHashGenerator(mock(AtomHashGenerator.class), null);
    }

    @Test
    public void testGenerate() {

        AtomHashGenerator atomGenerator = mock(AtomHashGenerator.class);
        Pseudorandom prng = mock(Pseudorandom.class);
        IAtomContainer container = mock(IAtomContainer.class);

        MoleculeHashGenerator generator = new BasicMoleculeHashGenerator(atomGenerator, prng);

        when(atomGenerator.generate(container)).thenReturn(new long[]{1, 1, 1, 1});
        when(prng.next(1L)).thenReturn(1L);

        long hashCode = generator.generate(container);

        verify(atomGenerator, times(1)).generate(container);
        verify(prng, times(3)).next(1L);

        verifyNoMoreInteractions(atomGenerator, container, prng);

        long expected = 2147483647L ^ 1L ^ 1L ^ 1L ^ 1L;

        assertThat(hashCode, is(expected));

    }

    @Test
    public void testGenerate_Rotation() {

        AtomHashGenerator atomGenerator = mock(AtomHashGenerator.class);
        Xorshift xorshift = new Xorshift();
        IAtomContainer container = mock(IAtomContainer.class);

        MoleculeHashGenerator generator = new BasicMoleculeHashGenerator(atomGenerator, new Xorshift());

        when(atomGenerator.generate(container)).thenReturn(new long[]{5L, 5L, 5L, 5L});

        long hashCode = generator.generate(container);

        verify(atomGenerator, times(1)).generate(container);

        verifyNoMoreInteractions(atomGenerator, container);

        long expected = 2147483647L ^ 5L ^ xorshift.next(5L) ^ xorshift.next(xorshift.next(5L))
                ^ xorshift.next(xorshift.next(xorshift.next(5L)));

        assertThat(hashCode, is(expected));

    }

}
