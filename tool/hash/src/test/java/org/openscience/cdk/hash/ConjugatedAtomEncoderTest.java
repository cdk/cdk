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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author John May
 * @cdk.module test-hash
 */
class ConjugatedAtomEncoderTest {

    @Test
    void testConstruction_Null() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> {
                                    new ConjugatedAtomEncoder(null);
                                });
    }

    @Test
    void testConstruction_Empty() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    new ConjugatedAtomEncoder(Collections.emptyList());
                                });
    }

    /**
     * ensure we can modify the order after we have constructed the conjunction
     */
    @Test
    void testConstruction_Modification() {
        AtomEncoder a = mock(AtomEncoder.class);
        AtomEncoder b = mock(AtomEncoder.class);
        AtomEncoder c = mock(AtomEncoder.class);
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);

        List<AtomEncoder> encoders = new ArrayList<>();
        encoders.add(a);
        encoders.add(b);
        encoders.add(c);
        AtomEncoder encoder = new ConjugatedAtomEncoder(encoders);

        encoders.remove(2); // removing b should not affect the new encoder

        encoder.encode(atom, container);

        InOrder order = inOrder(a, b, c);
        order.verify(a, times(1)).encode(atom, container);
        order.verify(b, times(1)).encode(atom, container);
        order.verify(c, times(1)).encode(atom, container);
        verifyNoMoreInteractions(a, b, c, atom, container);
    }

    @Test
    void testCreate_Null() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> {
                                    ConjugatedAtomEncoder.create(null, new AtomEncoder[0]);
                                });
    }

    @Test
    void testCreate_Null2() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> {
                                    ConjugatedAtomEncoder.create(mock(AtomEncoder.class), null);
                                });
    }

    @Test
    void testEncode_Single() throws Exception {
        AtomEncoder a = mock(AtomEncoder.class);
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);

        AtomEncoder encoder = new ConjugatedAtomEncoder(Arrays.asList(a));

        encoder.encode(atom, container);

        verify(a, times(1)).encode(atom, container);
        verifyNoMoreInteractions(a, atom, container);
    }

    @Test
    void testEncode() throws Exception {
        AtomEncoder a = mock(AtomEncoder.class);
        AtomEncoder b = mock(AtomEncoder.class);
        AtomEncoder c = mock(AtomEncoder.class);
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);

        AtomEncoder encoder = new ConjugatedAtomEncoder(Arrays.asList(a, b, c));

        encoder.encode(atom, container);

        InOrder order = inOrder(a, b, c);
        order.verify(a, times(1)).encode(atom, container);
        order.verify(b, times(1)).encode(atom, container);
        order.verify(c, times(1)).encode(atom, container);
        verifyNoMoreInteractions(a, b, c, atom, container);
    }
}
