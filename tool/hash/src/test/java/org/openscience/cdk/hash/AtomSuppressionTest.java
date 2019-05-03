/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class AtomSuppressionTest {

    @Test
    public void unsuppressed() throws Exception {
        AtomSuppression suppression = AtomSuppression.unsuppressed();
        IAtomContainer container = mock(IAtomContainer.class);
        Suppressed suppressed = suppression.suppress(container);
        assertFalse(suppressed.contains(0));
        assertFalse(suppressed.contains(1));
        assertFalse(suppressed.contains(2));
        assertFalse(suppressed.contains(3));
        assertFalse(suppressed.contains(4));
    }

    @Test
    public void anyHydrogens() throws Exception {
        AtomSuppression suppression = AtomSuppression.anyHydrogens();
        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(5);

        IAtom carbon = mock(IAtom.class);
        IAtom hydrogen = mock(IAtom.class);

        when(carbon.getSymbol()).thenReturn("C");
        when(hydrogen.getSymbol()).thenReturn("H");

        when(container.getAtom(0)).thenReturn(carbon);
        when(container.getAtom(1)).thenReturn(hydrogen);
        when(container.getAtom(2)).thenReturn(carbon);
        when(container.getAtom(3)).thenReturn(carbon);
        when(container.getAtom(4)).thenReturn(hydrogen);

        Suppressed suppressed = suppression.suppress(container);
        assertFalse(suppressed.contains(0));
        assertTrue(suppressed.contains(1));
        assertFalse(suppressed.contains(2));
        assertFalse(suppressed.contains(3));
        assertTrue(suppressed.contains(4));
    }

    @Test
    public void anyPseudos() throws Exception {
        AtomSuppression suppression = AtomSuppression.anyPseudos();
        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(5);

        IAtom carbon = mock(IAtom.class);
        IAtom pseudo = mock(IPseudoAtom.class);

        when(container.getAtom(0)).thenReturn(carbon);
        when(container.getAtom(1)).thenReturn(pseudo);
        when(container.getAtom(2)).thenReturn(carbon);
        when(container.getAtom(3)).thenReturn(carbon);
        when(container.getAtom(4)).thenReturn(pseudo);

        Suppressed suppressed = suppression.suppress(container);
        assertFalse(suppressed.contains(0));
        assertTrue(suppressed.contains(1));
        assertFalse(suppressed.contains(2));
        assertFalse(suppressed.contains(3));
        assertTrue(suppressed.contains(4));
    }
}
