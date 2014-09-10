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
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openscience.cdk.CDKConstants.ISAROMATIC;

/**
 * @author John May
 * @cdk.module test-isomorphism
 */
public class BondMatcherTest {

    @Test
    public void anyMatch() {
        BondMatcher matcher = BondMatcher.forAny();
        IBond bond1 = mock(IBond.class);
        IBond bond2 = mock(IBond.class);
        IBond bond3 = mock(IBond.class);
        assertTrue(matcher.matches(bond1, bond2));
        assertTrue(matcher.matches(bond2, bond1));
        assertTrue(matcher.matches(bond1, bond3));
        assertTrue(matcher.matches(bond1, null));
        assertTrue(matcher.matches(null, null));
    }

    @Test
    public void aromaticMatch() {
        BondMatcher matcher = BondMatcher.forOrder();
        IBond bond1 = mock(IBond.class);
        IBond bond2 = mock(IBond.class);
        when(bond1.getFlag(ISAROMATIC)).thenReturn(true);
        when(bond2.getFlag(ISAROMATIC)).thenReturn(true);
        when(bond1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(bond2.getOrder()).thenReturn(IBond.Order.DOUBLE);
        assertTrue(matcher.matches(bond1, bond2));
        assertTrue(matcher.matches(bond2, bond1));
    }

    @Test
    public void aliphaticMatch() {
        BondMatcher matcher = BondMatcher.forOrder();
        IBond bond1 = mock(IBond.class);
        IBond bond2 = mock(IBond.class);
        when(bond1.getFlag(ISAROMATIC)).thenReturn(false);
        when(bond2.getFlag(ISAROMATIC)).thenReturn(false);
        when(bond1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(bond2.getOrder()).thenReturn(IBond.Order.SINGLE);
        assertTrue(matcher.matches(bond1, bond2));
        assertTrue(matcher.matches(bond2, bond1));
    }

    @Test
    public void aromaticStrictMatch() {
        BondMatcher matcher = BondMatcher.forStrictOrder();
        IBond bond1 = mock(IBond.class);
        IBond bond2 = mock(IBond.class);
        when(bond1.getFlag(ISAROMATIC)).thenReturn(true);
        when(bond2.getFlag(ISAROMATIC)).thenReturn(true);
        when(bond1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(bond2.getOrder()).thenReturn(IBond.Order.DOUBLE);
        assertTrue(matcher.matches(bond1, bond2));
        assertTrue(matcher.matches(bond2, bond1));
    }

    @Test
    public void aliphaticStrictMatch() {
        BondMatcher matcher = BondMatcher.forStrictOrder();
        IBond bond1 = mock(IBond.class);
        IBond bond2 = mock(IBond.class);
        when(bond1.getFlag(ISAROMATIC)).thenReturn(false);
        when(bond2.getFlag(ISAROMATIC)).thenReturn(false);
        when(bond1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(bond2.getOrder()).thenReturn(IBond.Order.SINGLE);
        assertTrue(matcher.matches(bond1, bond2));
        assertTrue(matcher.matches(bond2, bond1));
    }

    @Test
    public void aliphaticMismatch_aromatic() {
        BondMatcher matcher = BondMatcher.forOrder();
        IBond bond1 = mock(IBond.class);
        IBond bond2 = mock(IBond.class);
        when(bond1.getFlag(ISAROMATIC)).thenReturn(true);
        when(bond2.getFlag(ISAROMATIC)).thenReturn(false);
        when(bond1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(bond2.getOrder()).thenReturn(IBond.Order.SINGLE);
        assertTrue(matcher.matches(bond1, bond2));
        assertTrue(matcher.matches(bond2, bond1));
    }

    @Test
    public void aliphaticStrictMismatch_aromatic() {
        BondMatcher matcher = BondMatcher.forStrictOrder();
        IBond bond1 = mock(IBond.class);
        IBond bond2 = mock(IBond.class);
        when(bond1.getFlag(ISAROMATIC)).thenReturn(true);
        when(bond2.getFlag(ISAROMATIC)).thenReturn(false);
        when(bond1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(bond2.getOrder()).thenReturn(IBond.Order.SINGLE);
        assertFalse(matcher.matches(bond1, bond2));
        assertFalse(matcher.matches(bond2, bond1));
    }

    @Test
    public void aliphaticMismatch_order() {
        BondMatcher matcher = BondMatcher.forOrder();
        IBond bond1 = mock(IBond.class);
        IBond bond2 = mock(IBond.class);
        when(bond1.getFlag(ISAROMATIC)).thenReturn(false);
        when(bond2.getFlag(ISAROMATIC)).thenReturn(false);
        when(bond1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(bond2.getOrder()).thenReturn(IBond.Order.DOUBLE);
        assertFalse(matcher.matches(bond1, bond2));
        assertFalse(matcher.matches(bond2, bond1));
    }

    @Test
    public void aliphaticStrictMismatch_order() {
        BondMatcher matcher = BondMatcher.forStrictOrder();
        IBond bond1 = mock(IBond.class);
        IBond bond2 = mock(IBond.class);
        when(bond1.getFlag(ISAROMATIC)).thenReturn(false);
        when(bond2.getFlag(ISAROMATIC)).thenReturn(false);
        when(bond1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(bond2.getOrder()).thenReturn(IBond.Order.DOUBLE);
        assertFalse(matcher.matches(bond1, bond2));
        assertFalse(matcher.matches(bond2, bond1));
    }

    @Test
    public void queryMatch() {
        BondMatcher matcher = BondMatcher.forQuery();
        IQueryBond bond1 = mock(IQueryBond.class);
        IBond bond2 = mock(IBond.class);
        IBond bond3 = mock(IBond.class);
        when(bond1.matches(bond2)).thenReturn(true);
        when(bond1.matches(bond3)).thenReturn(false);
        assertTrue(matcher.matches(bond1, bond2));
        assertFalse(matcher.matches(bond1, bond3));
    }
}
