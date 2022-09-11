/*
 * Copyright (c) 2017 John Mayfield <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

class AtomRefTest {

    @Test
    void dereferenceNullPointer() {
        Assertions.assertNull(AtomRef.deref(null));
    }

    @Test
    void dereferenceNonPointer() {
        IAtom mock = mock(IAtom.class);
        assertThat(AtomRef.deref(mock), is(sameInstance(mock)));
    }

    @Test
    void dereferencePointer() {
        IAtom mock = mock(IAtom.class);
        IAtom ptr  = new AtomRef(mock);
        assertThat(AtomRef.deref(ptr), is(sameInstance(mock)));
    }

    @Test
    void dereferencePointerPointer() {
        IAtom mock = mock(IAtom.class);
        IAtom ptr  = new AtomRef(new AtomRef(mock));
        assertThat(AtomRef.deref(ptr), is(sameInstance(mock)));
    }
}
