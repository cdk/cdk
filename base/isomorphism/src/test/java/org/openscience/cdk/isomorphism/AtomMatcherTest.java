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

package org.openscience.cdk.isomorphism;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author John May
 * @cdk.module test-isomorphism
 */
public class AtomMatcherTest {

    @Test
    public void anyMatch() throws Exception {
        AtomMatcher matcher = AtomMatcher.forAny();
        IAtom atom1 = mock(IAtom.class);
        IAtom atom2 = mock(IAtom.class);
        IAtom atom3 = mock(IAtom.class);
        when(atom1.getAtomicNumber()).thenReturn(6);
        when(atom2.getAtomicNumber()).thenReturn(7);
        when(atom3.getAtomicNumber()).thenReturn(8);
        assertTrue(matcher.matches(atom1, atom2));
        assertTrue(matcher.matches(atom2, atom1));
        assertTrue(matcher.matches(atom1, atom3));
        assertTrue(matcher.matches(atom3, atom1));
        assertTrue(matcher.matches(atom2, atom3));
        assertTrue(matcher.matches(atom1, null));
        assertTrue(matcher.matches(null, null));
    }

    @Test
    public void elementMatch() throws Exception {
        AtomMatcher matcher = AtomMatcher.forElement();
        IAtom atom1 = mock(IAtom.class);
        IAtom atom2 = mock(IAtom.class);
        when(atom1.getAtomicNumber()).thenReturn(6);
        when(atom2.getAtomicNumber()).thenReturn(6);
        assertTrue(matcher.matches(atom1, atom2));
        assertTrue(matcher.matches(atom2, atom1));
    }

    @Test
    public void elementMismatch() throws Exception {
        AtomMatcher matcher = AtomMatcher.forElement();
        IAtom atom1 = mock(IAtom.class);
        IAtom atom2 = mock(IAtom.class);
        when(atom1.getAtomicNumber()).thenReturn(6);
        when(atom2.getAtomicNumber()).thenReturn(8);
        assertFalse(matcher.matches(atom1, atom2));
        assertFalse(matcher.matches(atom2, atom1));
    }

    @Test
    public void elementPseudo() throws Exception {
        AtomMatcher matcher = AtomMatcher.forElement();
        IAtom atom1 = mock(IPseudoAtom.class);
        IAtom atom2 = mock(IPseudoAtom.class);
        assertTrue(matcher.matches(atom1, atom2));
        assertTrue(matcher.matches(atom2, atom1));
    }

    @Test(expected = NullPointerException.class)
    public void elementError() throws Exception {
        AtomMatcher matcher = AtomMatcher.forElement();
        IAtom atom1 = mock(IAtom.class);
        IAtom atom2 = mock(IAtom.class);
        when(atom1.getAtomicNumber()).thenReturn(null);
        when(atom2.getAtomicNumber()).thenReturn(null);
        matcher.matches(atom1, atom2);
    }

    @Test
    public void queryMatch() throws Exception {
        AtomMatcher matcher = AtomMatcher.forQuery();
        IQueryAtom atom1 = mock(IQueryAtom.class);
        IAtom atom2 = mock(IAtom.class);
        IAtom atom3 = mock(IAtom.class);
        when(atom1.matches(atom2)).thenReturn(true);
        when(atom1.matches(atom3)).thenReturn(false);
        assertTrue(matcher.matches(atom1, atom2));
        assertFalse(matcher.matches(atom1, atom3));
    }
}
