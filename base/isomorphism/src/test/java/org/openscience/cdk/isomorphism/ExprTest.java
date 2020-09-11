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

package org.openscience.cdk.isomorphism;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ReactionRole;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.templates.TestMoleculeFactory;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ALIPHATIC_ELEMENT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ALIPHATIC_ORDER;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.AND;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.AROMATIC_ELEMENT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.DEGREE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.DOUBLE_OR_AROMATIC;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ELEMENT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.FALSE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.FORMAL_CHARGE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.HAS_IMPLICIT_HYDROGEN;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.HAS_ISOTOPE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.HAS_UNSPEC_ISOTOPE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.HEAVY_DEGREE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.HETERO_SUBSTITUENT_COUNT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.HYBRIDISATION_NUMBER;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.IMPL_H_COUNT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.INSATURATION;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ISOTOPE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.IS_ALIPHATIC;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.IS_AROMATIC;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.IS_HETERO;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.IS_IN_CHAIN;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.IS_IN_RING;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.NOT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.OR;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.PERIODIC_GROUP;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.REACTION_ROLE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.RECURSIVE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.RING_BOND_COUNT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.RING_COUNT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.RING_SIZE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.RING_SMALLEST;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.SINGLE_OR_AROMATIC;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.SINGLE_OR_DOUBLE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.STEREOCHEMISTRY;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.TOTAL_DEGREE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.TOTAL_H_COUNT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.TRUE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.UNSATURATED;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.VALENCE;

public class ExprTest {

    @Test
    public void testT() {
        Expr  expr = new Expr(TRUE);
        IAtom atom = mock(IAtom.class);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testF() {
        Expr  expr = new Expr(FALSE);
        IAtom atom = mock(IAtom.class);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testAndTT() {
        Expr  expr = new Expr(AND, new Expr(TRUE), new Expr(TRUE));
        IAtom atom = mock(IAtom.class);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testAndTF() {
        Expr  expr = new Expr(AND, new Expr(TRUE), new Expr(FALSE));
        IAtom atom = mock(IAtom.class);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testAndFT() {
        Expr  expr = new Expr(AND, new Expr(FALSE), new Expr(TRUE));
        IAtom atom = mock(IAtom.class);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testOrTT() {
        Expr  expr = new Expr(OR, new Expr(TRUE), new Expr(TRUE));
        IAtom atom = mock(IAtom.class);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testOrTF() {
        Expr  expr = new Expr(OR, new Expr(TRUE), new Expr(FALSE));
        IAtom atom = mock(IAtom.class);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testOrFT() {
        Expr  expr = new Expr(OR, new Expr(FALSE), new Expr(TRUE));
        IAtom atom = mock(IAtom.class);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testOrFF() {
        Expr  expr = new Expr(OR, new Expr(FALSE), new Expr(FALSE));
        IAtom atom = mock(IAtom.class);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testNotF() {
        Expr  expr = new Expr(NOT, new Expr(FALSE), null);
        IAtom atom = mock(IAtom.class);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testNotT() {
        Expr  expr = new Expr(NOT, new Expr(TRUE), null);
        IAtom atom = mock(IAtom.class);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testNotStereo() {
        Expr  expr = new Expr(NOT, new Expr(STEREOCHEMISTRY, 1), null);
        IAtom atom = mock(IAtom.class);
        assertTrue(expr.matches(atom));
        assertTrue(expr.matches(atom, 2));
        assertFalse(expr.matches(atom, 1));
    }

    @Test
    public void testNotStereo3() {
        Expr  expr = new Expr(NOT, new Expr(STEREOCHEMISTRY, 1).or(new Expr(STEREOCHEMISTRY, 0)), null);
        IAtom atom = mock(IAtom.class);
        assertTrue(expr.matches(atom));
        assertTrue(expr.matches(atom, 2));
        assertFalse(expr.matches(atom, 1));
    }

    @Test
    public void testNotStereo4() {
        Expr  expr = new Expr(NOT, new Expr(OR, new Expr(TRUE), new Expr(TRUE)), null);
        IAtom atom = mock(IAtom.class);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testStereoT() {
        Expr  expr = new Expr(STEREOCHEMISTRY, 1);
        IAtom atom = mock(IAtom.class);
        assertTrue(expr.matches(atom, 1));
    }

    @Test
    public void testStereoF() {
        Expr  expr = new Expr(STEREOCHEMISTRY, 1);
        IAtom atom = mock(IAtom.class);
        assertFalse(expr.matches(atom, 2));
    }

    @Test
    public void testIsAromatic() {
        Expr  expr = new Expr(IS_AROMATIC);
        IAtom atom = new Atom();
        atom.setIsAromatic(false);
        assertFalse(expr.matches(atom));
        atom.setIsAromatic(true);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testIsAliphaticT() {
        Expr  expr = new Expr(IS_ALIPHATIC);
        IAtom atom = new Atom();
        atom.setIsAromatic(false);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testIsAliphaticF() {
        Expr  expr = new Expr(IS_ALIPHATIC);
        IAtom atom = new Atom();
        atom.setIsAromatic(true);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testIsHetero() {
        Expr  expr = new Expr(IS_HETERO);
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(1);
        assertFalse(expr.matches(atom));
        when(atom.getAtomicNumber()).thenReturn(6);
        assertFalse(expr.matches(atom));
        when(atom.getAtomicNumber()).thenReturn(8);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHasImplicitHydrogenT() {
        Expr  expr = new Expr(HAS_IMPLICIT_HYDROGEN);
        IAtom atom = mock(IAtom.class);
        when(atom.getImplicitHydrogenCount()).thenReturn(1);
        assertTrue(expr.matches(atom));
        when(atom.getImplicitHydrogenCount()).thenReturn(2);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHasImplicitHydrogenF() {
        Expr  expr = new Expr(HAS_IMPLICIT_HYDROGEN);
        IAtom atom = mock(IAtom.class);
        when(atom.getImplicitHydrogenCount()).thenReturn(0);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHasImplicitHydrogenNull() {
        Expr  expr = new Expr(HAS_IMPLICIT_HYDROGEN);
        IAtom atom = mock(IAtom.class);
        when(atom.getImplicitHydrogenCount()).thenReturn(null);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHasIsotope() {
        Expr  expr = new Expr(HAS_ISOTOPE);
        IAtom atom = mock(IAtom.class);
        when(atom.getMassNumber()).thenReturn(null);
        assertFalse(expr.matches(atom));
        when(atom.getMassNumber()).thenReturn(12);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHasUnspecIsotope() {
        Expr  expr = new Expr(HAS_UNSPEC_ISOTOPE);
        IAtom atom = mock(IAtom.class);
        when(atom.getMassNumber()).thenReturn(12);
        assertFalse(expr.matches(atom));
        when(atom.getMassNumber()).thenReturn(null);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testIsInRing() {
        Expr  expr = new Expr(IS_IN_RING);
        IAtom atom = mock(IAtom.class);
        when(atom.isInRing()).thenReturn(false);
        assertFalse(expr.matches(atom));
        when(atom.isInRing()).thenReturn(true);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testIsInChain() {
        Expr  expr = new Expr(IS_IN_CHAIN);
        IAtom atom = mock(IAtom.class);
        when(atom.isInRing()).thenReturn(false);
        assertTrue(expr.matches(atom));
        when(atom.isInRing()).thenReturn(true);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testUnsaturatedT() {
        Expr  expr = new Expr(UNSATURATED);
        IAtom atom = mock(IAtom.class);
        IBond bond = mock(IBond.class);
        when(bond.getOrder()).thenReturn(IBond.Order.DOUBLE);
        when(atom.bonds()).thenReturn(Collections.singletonList(bond));
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testUnsaturatedF() {
        Expr  expr = new Expr(UNSATURATED);
        IAtom atom = mock(IAtom.class);
        IBond bond = mock(IBond.class);
        when(bond.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(atom.bonds()).thenReturn(Collections.singletonList(bond));
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testElementT() {
        for (int num = 1; num < 54; ++num) {
            Expr  expr = new Expr(ELEMENT, num);
            IAtom atom = mock(IAtom.class);
            when(atom.getAtomicNumber()).thenReturn(num);
            assertTrue(expr.matches(atom));
        }
    }

    @Test
    public void testElementF() {
        for (int num = 1; num < 54; ++num) {
            Expr  expr = new Expr(ELEMENT, num);
            IAtom atom = mock(IAtom.class);
            when(atom.getAtomicNumber()).thenReturn(num + 1);
            assertFalse(expr.matches(atom));
        }
    }

    @Test
    public void testAliphaticElementT() {
        for (int num = 1; num < 54; ++num) {
            Expr  expr = new Expr(ALIPHATIC_ELEMENT, num);
            IAtom atom = mock(IAtom.class);
            when(atom.getAtomicNumber()).thenReturn(num);
            when(atom.isAromatic()).thenReturn(false);
            assertTrue(expr.matches(atom));
        }
    }

    @Test
    public void testAliphaticElementF() {
        for (int num = 1; num < 54; ++num) {
            Expr  expr = new Expr(ALIPHATIC_ELEMENT, num);
            IAtom atom = mock(IAtom.class);
            when(atom.getAtomicNumber()).thenReturn(num);
            when(atom.isAromatic()).thenReturn(true);
            assertFalse(expr.matches(atom));
        }
    }

    @Test
    public void testAliphaticElementFalse2() {
        for (int num = 1; num < 54; ++num) {
            Expr  expr = new Expr(ALIPHATIC_ELEMENT, num);
            IAtom atom = mock(IAtom.class);
            when(atom.getAtomicNumber()).thenReturn(num + 1);
            when(atom.isAromatic()).thenReturn(false);
            assertFalse(expr.matches(atom));
        }
    }

    @Test
    public void testAromaticElementT() {
        for (int num = 1; num < 54; ++num) {
            Expr  expr = new Expr(AROMATIC_ELEMENT, num);
            IAtom atom = mock(IAtom.class);
            when(atom.getAtomicNumber()).thenReturn(num);
            when(atom.isAromatic()).thenReturn(true);
            assertTrue(expr.matches(atom));
        }
    }

    @Test
    public void testAromaticElementF() {
        for (int num = 1; num < 54; ++num) {
            Expr  expr = new Expr(AROMATIC_ELEMENT, num);
            IAtom atom = mock(IAtom.class);
            when(atom.getAtomicNumber()).thenReturn(num);
            when(atom.isAromatic()).thenReturn(false);
            assertFalse(expr.matches(atom));
        }
    }

    @Test
    public void testAromaticElementFalse2() {
        for (int num = 1; num < 54; ++num) {
            Expr  expr = new Expr(AROMATIC_ELEMENT, num);
            IAtom atom = mock(IAtom.class);
            when(atom.getAtomicNumber()).thenReturn(num + 1);
            when(atom.isAromatic()).thenReturn(true);
            assertFalse(expr.matches(atom));
        }
    }

    @Test
    public void testHCountT() {
        Expr  expr = new Expr(IMPL_H_COUNT, 1);
        IAtom atom = mock(IAtom.class);
        when(atom.getImplicitHydrogenCount()).thenReturn(1);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHCountF() {
        Expr  expr = new Expr(IMPL_H_COUNT, 2);
        IAtom atom = mock(IAtom.class);
        when(atom.getImplicitHydrogenCount()).thenReturn(1);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testTotalHCountT() {
        Expr  expr = new Expr(TOTAL_H_COUNT, 3);
        IAtom atom = mock(IAtom.class);
        IAtom h    = mock(IAtom.class);
        IBond b    = mock(IBond.class);
        when(b.getOther(atom)).thenReturn(h);
        when(b.getOther(h)).thenReturn(atom);
        when(atom.getImplicitHydrogenCount()).thenReturn(2);
        when(h.getAtomicNumber()).thenReturn(1);
        when(atom.bonds()).thenReturn(Collections.singletonList(b));
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testTotalHCountF() {
        Expr  expr = new Expr(TOTAL_H_COUNT, 2);
        IAtom atom = mock(IAtom.class);
        IAtom h    = mock(IAtom.class);
        IBond b    = mock(IBond.class);
        when(b.getOther(atom)).thenReturn(h);
        when(b.getOther(h)).thenReturn(atom);
        when(atom.getImplicitHydrogenCount()).thenReturn(2);
        when(h.getAtomicNumber()).thenReturn(1);
        when(atom.bonds()).thenReturn(Collections.singletonList(b));
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testTotalHCountNullImplT() {
        Expr  expr = new Expr(TOTAL_H_COUNT, 1);
        IAtom atom = mock(IAtom.class);
        IAtom h    = mock(IAtom.class);
        IBond b    = mock(IBond.class);
        when(b.getOther(atom)).thenReturn(h);
        when(b.getOther(h)).thenReturn(atom);
        when(atom.getImplicitHydrogenCount()).thenReturn(null);
        when(h.getAtomicNumber()).thenReturn(1);
        when(atom.bonds()).thenReturn(Collections.singletonList(b));
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testTotalHCountImplF() {
        Expr  expr = new Expr(TOTAL_H_COUNT, 1);
        IAtom atom = mock(IAtom.class);
        IAtom h    = mock(IAtom.class);
        IBond b    = mock(IBond.class);
        when(b.getOther(atom)).thenReturn(h);
        when(b.getOther(h)).thenReturn(atom);
        when(atom.getImplicitHydrogenCount()).thenReturn(2);
        when(h.getAtomicNumber()).thenReturn(1);
        when(atom.bonds()).thenReturn(Collections.singletonList(b));
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testDegreeT() {
        Expr  expr = new Expr(DEGREE, 1);
        IAtom atom = mock(IAtom.class);
        when(atom.getBondCount()).thenReturn(1);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testDegreeF() {
        Expr  expr = new Expr(DEGREE, 2);
        IAtom atom = mock(IAtom.class);
        when(atom.getBondCount()).thenReturn(1);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testTotalDegreeT() {
        Expr  expr = new Expr(TOTAL_DEGREE, 1);
        IAtom atom = mock(IAtom.class);
        when(atom.getBondCount()).thenReturn(1);
        when(atom.getImplicitHydrogenCount()).thenReturn(0);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testTotalDegreeF() {
        Expr  expr = new Expr(TOTAL_DEGREE, 1);
        IAtom atom = mock(IAtom.class);
        when(atom.getBondCount()).thenReturn(1);
        when(atom.getImplicitHydrogenCount()).thenReturn(1);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHeavyDegreeT() {
        Expr  expr = new Expr(HEAVY_DEGREE, 0);
        IAtom atom = mock(IAtom.class);
        IAtom h    = mock(IAtom.class);
        IBond b    = mock(IBond.class);
        when(atom.getBondCount()).thenReturn(1);
        when(b.getOther(atom)).thenReturn(h);
        when(b.getOther(h)).thenReturn(atom);
        when(atom.getImplicitHydrogenCount()).thenReturn(2);
        when(h.getAtomicNumber()).thenReturn(1);
        when(atom.bonds()).thenReturn(Collections.singletonList(b));
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHeavyDegreeF() {
        Expr  expr = new Expr(HEAVY_DEGREE, 1);
        IAtom atom = mock(IAtom.class);
        IAtom h    = mock(IAtom.class);
        IBond b    = mock(IBond.class);
        when(atom.getBondCount()).thenReturn(1);
        when(b.getOther(atom)).thenReturn(h);
        when(b.getOther(h)).thenReturn(atom);
        when(atom.getImplicitHydrogenCount()).thenReturn(2);
        when(h.getAtomicNumber()).thenReturn(1);
        when(atom.bonds()).thenReturn(Collections.singletonList(b));
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHeteroSubT() {
        Expr  expr = new Expr(HETERO_SUBSTITUENT_COUNT, 1);
        IAtom atom = mock(IAtom.class);
        IAtom o    = mock(IAtom.class);
        IBond b    = mock(IBond.class);
        when(atom.getBondCount()).thenReturn(1);
        when(b.getOther(atom)).thenReturn(o);
        when(b.getOther(o)).thenReturn(atom);
        when(atom.getImplicitHydrogenCount()).thenReturn(2);
        when(o.getAtomicNumber()).thenReturn(8);
        when(atom.bonds()).thenReturn(Collections.singletonList(b));
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHeteroSubFailFastF() {
        Expr  expr = new Expr(HETERO_SUBSTITUENT_COUNT, 2);
        IAtom atom = mock(IAtom.class);
        IAtom o    = mock(IAtom.class);
        IBond b    = mock(IBond.class);
        when(atom.getBondCount()).thenReturn(1);
        when(b.getOther(atom)).thenReturn(o);
        when(b.getOther(o)).thenReturn(atom);
        when(atom.getImplicitHydrogenCount()).thenReturn(2);
        when(o.getAtomicNumber()).thenReturn(8);
        when(atom.bonds()).thenReturn(Collections.singletonList(b));
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHeteroSubF() {
        Expr  expr = new Expr(HETERO_SUBSTITUENT_COUNT, 1);
        IAtom atom = mock(IAtom.class);
        IAtom c    = mock(IAtom.class);
        IBond b    = mock(IBond.class);
        when(atom.getBondCount()).thenReturn(1);
        when(b.getOther(atom)).thenReturn(c);
        when(b.getOther(c)).thenReturn(atom);
        when(atom.getImplicitHydrogenCount()).thenReturn(2);
        when(c.getAtomicNumber()).thenReturn(6);
        when(atom.bonds()).thenReturn(Collections.singletonList(b));
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testValenceT() {
        Expr  expr = new Expr(VALENCE, 4);
        IAtom a1   = mock(IAtom.class);
        IBond b1   = mock(IBond.class);
        IBond b2   = mock(IBond.class);
        when(a1.getImplicitHydrogenCount()).thenReturn(1);
        when(b1.getOrder()).thenReturn(IBond.Order.DOUBLE);
        when(b2.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(a1.bonds()).thenReturn(Arrays.asList(b1, b2));
        assertTrue(expr.matches(a1));
    }

    @Test
    public void testValenceF() {
        Expr  expr = new Expr(VALENCE, 4);
        IAtom a1   = mock(IAtom.class);
        IBond b1   = mock(IBond.class);
        IBond b2   = mock(IBond.class);
        when(a1.getImplicitHydrogenCount()).thenReturn(1);
        when(b1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(b2.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(a1.bonds()).thenReturn(Arrays.asList(b1, b2));
        assertFalse(expr.matches(a1));
    }

    @Test
    public void testValenceNullOrderT() {
        Expr  expr = new Expr(VALENCE, 4);
        IAtom a1   = mock(IAtom.class);
        IBond b1   = mock(IBond.class);
        IBond b2   = mock(IBond.class);
        when(a1.getImplicitHydrogenCount()).thenReturn(1);
        when(b1.getOrder()).thenReturn(IBond.Order.DOUBLE);
        when(b2.getOrder()).thenReturn(null);
        when(a1.bonds()).thenReturn(Arrays.asList(b1, b2));
        assertFalse(expr.matches(a1));
    }

    @Test
    public void testValenceFailFastF() {
        Expr  expr = new Expr(VALENCE, 2);
        IAtom a1   = mock(IAtom.class);
        when(a1.getImplicitHydrogenCount()).thenReturn(4);
        assertFalse(expr.matches(a1));
    }

    @Test
    public void testIsotopeT() {
        Expr  expr = new Expr(ISOTOPE, 13);
        IAtom atom = mock(IAtom.class);
        when(atom.getMassNumber()).thenReturn(13);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testIsotopeF() {
        Expr  expr = new Expr(ISOTOPE, 12);
        IAtom atom = mock(IAtom.class);
        when(atom.getMassNumber()).thenReturn(13);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testFormalChargeT() {
        Expr  expr = new Expr(FORMAL_CHARGE, -1);
        IAtom atom = mock(IAtom.class);
        when(atom.getFormalCharge()).thenReturn(-1);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testFormalChargeF() {
        Expr  expr = new Expr(FORMAL_CHARGE, -1);
        IAtom atom = mock(IAtom.class);
        when(atom.getFormalCharge()).thenReturn(0);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testRingBondCountT() {
        Expr  expr = new Expr(RING_BOND_COUNT, 3);
        IAtom atom = mock(IAtom.class);
        IBond b1   = mock(IBond.class);
        IBond b2   = mock(IBond.class);
        IBond b3   = mock(IBond.class);
        when(atom.isInRing()).thenReturn(true);
        when(atom.getBondCount()).thenReturn(3);
        when(b1.isInRing()).thenReturn(true);
        when(b2.isInRing()).thenReturn(true);
        when(b3.isInRing()).thenReturn(true);
        when(atom.bonds()).thenReturn(Arrays.asList(b1, b2, b3));
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testRingBondCountF() {
        Expr  expr = new Expr(RING_BOND_COUNT, 3);
        IAtom atom = mock(IAtom.class);
        IBond b1   = mock(IBond.class);
        IBond b2   = mock(IBond.class);
        IBond b3   = mock(IBond.class);
        when(atom.isInRing()).thenReturn(true);
        when(atom.getBondCount()).thenReturn(3);
        when(b1.isInRing()).thenReturn(true);
        when(b2.isInRing()).thenReturn(true);
        when(b3.isInRing()).thenReturn(false);
        when(atom.bonds()).thenReturn(Arrays.asList(b1, b2, b3));
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testRingBondCountNonRingF() {
        Expr  expr = new Expr(RING_BOND_COUNT, 3);
        IAtom atom = mock(IAtom.class);
        when(atom.isInRing()).thenReturn(false);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testRingBondCountLessBondsF() {
        Expr  expr = new Expr(RING_BOND_COUNT, 3);
        IAtom atom = mock(IAtom.class);
        when(atom.isInRing()).thenReturn(true);
        when(atom.getBondCount()).thenReturn(2);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testInsaturationT() {
        Expr  expr = new Expr(INSATURATION, 2);
        IAtom atom = mock(IAtom.class);
        IBond b1   = mock(IBond.class);
        IBond b2   = mock(IBond.class);
        when(b1.getOrder()).thenReturn(IBond.Order.DOUBLE);
        when(b2.getOrder()).thenReturn(IBond.Order.DOUBLE);
        when(atom.bonds()).thenReturn(Arrays.asList(b1, b2));
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testInsaturationF() {
        Expr  expr = new Expr(INSATURATION, 2);
        IAtom atom = mock(IAtom.class);
        IBond b1   = mock(IBond.class);
        IBond b2   = mock(IBond.class);
        when(b1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(b2.getOrder()).thenReturn(IBond.Order.DOUBLE);
        when(atom.bonds()).thenReturn(Arrays.asList(b1, b2));
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testGroupT() {
        Expr expr = new Expr(PERIODIC_GROUP,
                             Elements.Chlorine.group());
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(9);
        assertTrue(expr.matches(atom));
        when(atom.getAtomicNumber()).thenReturn(17);
        assertTrue(expr.matches(atom));
        when(atom.getAtomicNumber()).thenReturn(35);
        assertTrue(expr.matches(atom));
        when(atom.getAtomicNumber()).thenReturn(53);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testGroupF() {
        Expr expr = new Expr(PERIODIC_GROUP,
                             Elements.Chlorine.group());
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(8);
        assertFalse(expr.matches(atom));
        when(atom.getAtomicNumber()).thenReturn(16);
        assertFalse(expr.matches(atom));
        when(atom.getAtomicNumber()).thenReturn(34);
        assertFalse(expr.matches(atom));
        when(atom.getAtomicNumber()).thenReturn(52);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testGroupNull() {
        Expr expr = new Expr(PERIODIC_GROUP,
                             Elements.Chlorine.group());
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(null);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHybridisation0F() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             0);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP1);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp1T() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             1);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP1);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp1F() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             1);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP2);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp2T() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             2);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP2);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp2F() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             2);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP1);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp3T() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             3);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP3);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp3F() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             3);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP1);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp3d1T() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             4);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP3D1);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp3d1F() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             4);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP1);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp3d2T() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             5);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP3D2);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp3d2F() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             5);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP1);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp3d3T() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             6);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP3D3);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp3d3F() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             6);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP1);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp3d4T() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             7);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP3D4);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp3d4F() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             7);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP1);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp3d5T() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             8);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP3D5);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp1Null() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             1);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(null);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testHybridisationSp3d5F() {
        Expr expr = new Expr(HYBRIDISATION_NUMBER,
                             8);
        IAtom atom = mock(IAtom.class);
        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP1);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testReactionRoleT() {
        Expr expr = new Expr(REACTION_ROLE,
                             ReactionRole.Reactant.ordinal());
        IAtom atom = mock(IAtom.class);
        when(atom.getProperty(CDKConstants.REACTION_ROLE)).thenReturn(ReactionRole.Reactant);
        assertTrue(expr.matches(atom));
    }

    @Test
    public void testReactionRoleF() {
        Expr expr = new Expr(REACTION_ROLE,
                             ReactionRole.Reactant.ordinal());
        IAtom atom = mock(IAtom.class);
        when(atom.getProperty(CDKConstants.REACTION_ROLE)).thenReturn(ReactionRole.Product);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testReactionRoleNull() {
        Expr expr = new Expr(REACTION_ROLE,
                             ReactionRole.Reactant.ordinal());
        IAtom atom = mock(IAtom.class);
        when(atom.getProperty(CDKConstants.REACTION_ROLE)).thenReturn(null);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testRingSize6() {
        Expr           expr = new Expr(RING_SIZE, 6);
        IAtomContainer mol  = TestMoleculeFactory.makeNaphthalene();
        Cycles.markRingAtomsAndBonds(mol);
        assertTrue(expr.matches(mol.getAtom(0)));
    }

    @Test
    public void testRingSize10() {
        Expr           expr = new Expr(RING_SIZE, 10);
        IAtomContainer mol  = TestMoleculeFactory.makeNaphthalene();
        Cycles.markRingAtomsAndBonds(mol);
        assertTrue(expr.matches(mol.getAtom(0)));
    }

    @Test
    public void testRingSmallestNonRing() {
        Expr  expr = new Expr(RING_SMALLEST, 6);
        IAtom atom = mock(IAtom.class);
        when(atom.isInRing()).thenReturn(false);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testRingNonRing() {
        Expr  expr = new Expr(RING_SIZE, 6);
        IAtom atom = mock(IAtom.class);
        when(atom.isInRing()).thenReturn(false);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testRingCountNonRing() {
        Expr  expr = new Expr(RING_COUNT, 6);
        IAtom atom = mock(IAtom.class);
        when(atom.isInRing()).thenReturn(false);
        assertFalse(expr.matches(atom));
    }

    @Test
    public void testRingSmallestSize6() {
        Expr           expr = new Expr(RING_SMALLEST, 6);
        IAtomContainer mol  = TestMoleculeFactory.makeNaphthalene();
        Cycles.markRingAtomsAndBonds(mol);
        assertTrue(expr.matches(mol.getAtom(0)));
    }

    @Test
    public void testRingSmallestSize10() {
        Expr           expr = new Expr(RING_SMALLEST, 10);
        IAtomContainer mol  = TestMoleculeFactory.makeNaphthalene();
        Cycles.markRingAtomsAndBonds(mol);
        assertFalse(expr.matches(mol.getAtom(0)));
    }

    @Test
    public void testRingSmallestSize5And6() {
        Expr           expr5 = new Expr(RING_SMALLEST, 5);
        Expr           expr6 = new Expr(RING_SMALLEST, 6);
        IAtomContainer mol   = TestMoleculeFactory.makeIndole();
        Cycles.markRingAtomsAndBonds(mol);
        int numSmall5 = 0;
        int numSmall6 = 0;
        for (IAtom atom : mol.atoms()) {
            if (expr5.matches(atom))
                numSmall5++;
            if (expr6.matches(atom))
                numSmall6++;
        }
        assertThat(numSmall5, is(5));
        assertThat(numSmall6, is(4));
    }

    @Test
    public void testRingSize5And6() {
        Expr           expr5 = new Expr(RING_SIZE, 5);
        Expr           expr6 = new Expr(RING_SIZE, 6);
        IAtomContainer mol   = TestMoleculeFactory.makeIndole();
        Cycles.markRingAtomsAndBonds(mol);
        int numSmall5 = 0;
        int numSmall6 = 0;
        for (IAtom atom : mol.atoms()) {
            if (expr5.matches(atom))
                numSmall5++;
            if (expr6.matches(atom))
                numSmall6++;
        }
        assertThat(numSmall5, is(5));
        assertThat(numSmall6, is(6));
    }

    @Test
    public void testRingCount2() {
        Expr           expr = new Expr(RING_COUNT, 2);
        IAtomContainer mol  = TestMoleculeFactory.makeNaphthalene();
        Cycles.markRingAtomsAndBonds(mol);
        int count = 0;
        for (IAtom atom : mol.atoms()) {
            if (expr.matches(atom))
                count++;
        }
        assertThat(count, is(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonAtomExpr() {
        Expr  expr = new Expr(ALIPHATIC_ORDER, 2);
        IAtom atom = mock(IAtom.class);
        assertFalse(expr.matches(atom));
    }

//    @Ignore("to be added back in later")
//    public void testRecursiveT() {
//        IAtomContainer subexpr = new QueryAtomContainer(null);
//        QueryAtom      qatom   = new QueryAtom(null);
//        qatom.setExpression(new Expr(TRUE));
//        subexpr.addAtom(qatom);
//        Expr           expr = new Expr(RECURSIVE, subexpr);
//        IAtomContainer mol  = TestMoleculeFactory.makeNaphthalene();
//        assertTrue(expr.matches(mol.getAtom(0)));
//    }
//
//    @Ignore("to be added back in later")
//    public void testRecursiveF() {
//        IAtomContainer subexpr = new QueryAtomContainer(null);
//        QueryAtom      qatom   = new QueryAtom(null);
//        qatom.setExpression(new Expr(FALSE));
//        subexpr.addAtom(qatom);
//        Expr           expr = new Expr(RECURSIVE, subexpr);
//        IAtomContainer mol  = TestMoleculeFactory.makeNaphthalene();
//        assertFalse(expr.matches(mol.getAtom(0)));
//    }
//
//    @Ignore("to be added back in later")
//    public void testRecursiveF2() {
//        IAtomContainer subexpr = new QueryAtomContainer(null);
//        QueryAtom      qatom   = new QueryAtom(null);
//        qatom.setExpression(new Expr(ELEMENT, 8));
//        subexpr.addAtom(qatom);
//        Expr           expr = new Expr(RECURSIVE, subexpr);
//        IAtomContainer mol  = TestMoleculeFactory.makeNaphthalene();
//        IAtom          atom = mol.getBuilder().newAtom();
//        atom.setAtomicNumber(8);
//        mol.addAtom(atom);
//        assertFalse(expr.matches(mol.getAtom(0)));
//        assertTrue(expr.matches(mol.getAtom(mol.getAtomCount() - 1)));
//    }

    /* Bond Exprs */

    @Test
    public void testBondT() {
        Expr  expr = new Expr(TRUE);
        IBond bond = mock(IBond.class);
        assertTrue(expr.matches(bond));
    }

    @Test
    public void testBondF() {
        Expr  expr = new Expr(FALSE);
        IBond bond = mock(IBond.class);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testBondAndTT() {
        Expr  expr = new Expr(AND, new Expr(TRUE), new Expr(TRUE));
        IBond bond = mock(IBond.class);
        assertTrue(expr.matches(bond));
    }

    @Test
    public void testBondAndTF() {
        Expr  expr = new Expr(AND, new Expr(TRUE), new Expr(FALSE));
        IBond bond = mock(IBond.class);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testBondAndFT() {
        Expr  expr = new Expr(AND, new Expr(FALSE), new Expr(TRUE));
        IBond bond = mock(IBond.class);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testBondOrTT() {
        Expr  expr = new Expr(OR, new Expr(TRUE), new Expr(TRUE));
        IBond bond = mock(IBond.class);
        assertTrue(expr.matches(bond));
    }

    @Test
    public void testBondOrTF() {
        Expr  expr = new Expr(OR, new Expr(TRUE), new Expr(FALSE));
        IBond bond = mock(IBond.class);
        assertTrue(expr.matches(bond));
    }

    @Test
    public void testBondOrFT() {
        Expr  expr = new Expr(OR, new Expr(FALSE), new Expr(TRUE));
        IBond bond = mock(IBond.class);
        assertTrue(expr.matches(bond));
    }

    @Test
    public void testBondOrFF() {
        Expr  expr = new Expr(OR, new Expr(FALSE), new Expr(FALSE));
        IBond bond = mock(IBond.class);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testBondNotF() {
        Expr  expr = new Expr(NOT, new Expr(FALSE), null);
        IBond bond = mock(IBond.class);
        assertTrue(expr.matches(bond));
    }

    @Test
    public void testBondNotT() {
        Expr  expr = new Expr(NOT, new Expr(TRUE), null);
        IBond bond = mock(IBond.class);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testBondNotStereo() {
        Expr  expr = new Expr(NOT, new Expr(STEREOCHEMISTRY, 1), null);
        IBond bond = mock(IBond.class);
        assertTrue(expr.matches(bond));
        assertTrue(expr.matches(bond, 2));
        assertFalse(expr.matches(bond, 1));
    }

    @Test
    public void testBondNotStereo3() {
        Expr  expr = new Expr(NOT, new Expr(STEREOCHEMISTRY, 1).or(new Expr(STEREOCHEMISTRY, 0)), null);
        IBond bond = mock(IBond.class);
        assertTrue(expr.matches(bond));
        assertTrue(expr.matches(bond, 2));
        assertFalse(expr.matches(bond, 1));
    }

    @Test
    public void testBondNotStereo4() {
        Expr  expr = new Expr(NOT, new Expr(OR, new Expr(TRUE), new Expr(TRUE)), null);
        IBond bond = mock(IBond.class);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testBondStereoT() {
        Expr  expr = new Expr(STEREOCHEMISTRY, 1);
        IBond bond = mock(IBond.class);
        assertTrue(expr.matches(bond, 1));
    }

    @Test
    public void testBondStereoF() {
        Expr  expr = new Expr(STEREOCHEMISTRY, 1);
        IBond bond = mock(IBond.class);
        assertFalse(expr.matches(bond, 2));
    }

    @Test
    public void testBondIsAromaticT() {
        Expr  expr = new Expr(IS_AROMATIC);
        IBond bond = mock(IBond.class);
        when(bond.isAromatic()).thenReturn(true);
        assertTrue(expr.matches(bond));
    }

    @Test
    public void testBondIsAromaticF() {
        Expr  expr = new Expr(IS_AROMATIC);
        IBond bond = mock(IBond.class);
        when(bond.isAromatic()).thenReturn(false);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testBondIsAliphaticT() {
        Expr  expr = new Expr(IS_ALIPHATIC);
        IBond bond = mock(IBond.class);
        when(bond.isAromatic()).thenReturn(false);
        assertTrue(expr.matches(bond));
    }

    @Test
    public void testBondIsAliphaticF() {
        Expr  expr = new Expr(IS_ALIPHATIC);
        IBond bond = mock(IBond.class);
        when(bond.isAromatic()).thenReturn(true);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testBondIsChainT() {
        Expr  expr = new Expr(IS_IN_CHAIN);
        IBond bond = mock(IBond.class);
        when(bond.isInRing()).thenReturn(false);
        assertTrue(expr.matches(bond));
    }

    @Test
    public void testBondIsChainF() {
        Expr  expr = new Expr(IS_IN_CHAIN);
        IBond bond = mock(IBond.class);
        when(bond.isInRing()).thenReturn(true);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testBondIsRingT() {
        Expr  expr = new Expr(IS_IN_RING);
        IBond bond = mock(IBond.class);
        when(bond.isInRing()).thenReturn(true);
        assertTrue(expr.matches(bond));
    }

    @Test
    public void testBondIsRingF() {
        Expr  expr = new Expr(IS_IN_RING);
        IBond bond = mock(IBond.class);
        when(bond.isInRing()).thenReturn(false);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testBondOrderT() {
        Expr  expr = new Expr(ALIPHATIC_ORDER, 2);
        IBond bond = mock(IBond.class);
        when(bond.getOrder()).thenReturn(IBond.Order.DOUBLE);
        assertTrue(expr.matches(bond));
    }

    @Test
    public void testBondOrderF() {
        Expr  expr = new Expr(ALIPHATIC_ORDER, 2);
        IBond bond = mock(IBond.class);
        when(bond.getOrder()).thenReturn(IBond.Order.SINGLE);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testBondOrderAlipF() {
        Expr  expr = new Expr(ALIPHATIC_ORDER, 2);
        IBond bond = mock(IBond.class);
        when(bond.isAromatic()).thenReturn(true);
        when(bond.getOrder()).thenReturn(IBond.Order.DOUBLE);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testBondOrderNullF() {
        Expr  expr = new Expr(ALIPHATIC_ORDER, 2);
        IBond bond = mock(IBond.class);
        when(bond.getOrder()).thenReturn(null);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testSingleOrAromaticT() {
        Expr  expr = new Expr(SINGLE_OR_AROMATIC);
        IBond bond = mock(IBond.class);
        when(bond.isAromatic()).thenReturn(false);
        when(bond.getOrder()).thenReturn(IBond.Order.SINGLE);
        assertTrue(expr.matches(bond));
        when(bond.isAromatic()).thenReturn(true);
        when(bond.getOrder()).thenReturn(null);
        assertTrue(expr.matches(bond));
    }

    @Test
    public void testSingleOrAromaticF() {
        Expr  expr = new Expr(SINGLE_OR_AROMATIC);
        IBond bond = mock(IBond.class);
        when(bond.isAromatic()).thenReturn(false);
        when(bond.getOrder()).thenReturn(IBond.Order.TRIPLE);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testDoubleOrAromaticT() {
        Expr  expr = new Expr(DOUBLE_OR_AROMATIC);
        IBond bond = mock(IBond.class);
        when(bond.isAromatic()).thenReturn(true);
        when(bond.getOrder()).thenReturn(null);
        assertTrue(expr.matches(bond));
        when(bond.isAromatic()).thenReturn(false);
        when(bond.getOrder()).thenReturn(IBond.Order.DOUBLE);
        assertTrue(expr.matches(bond));
        when(bond.isAromatic()).thenReturn(false);
        when(bond.getOrder()).thenReturn(IBond.Order.TRIPLE);
        assertFalse(expr.matches(bond));
    }

    @Test
    public void testSingleOrDoubleT() {
        Expr  expr = new Expr(SINGLE_OR_DOUBLE);
        IBond bond = mock(IBond.class);
        when(bond.getOrder()).thenReturn(IBond.Order.SINGLE);
        assertTrue(expr.matches(bond));
        when(bond.getOrder()).thenReturn(IBond.Order.DOUBLE);
        assertTrue(expr.matches(bond));
        when(bond.getOrder()).thenReturn(IBond.Order.TRIPLE);
        assertFalse(expr.matches(bond));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonBondExpr() {
        Expr  expr = new Expr(RING_COUNT, 1);
        IBond bond = mock(IBond.class);
        expr.matches(bond);
    }

    @Test
    public void testToString() {
        assertThat(new Expr(TRUE).toString(), is("TRUE"));
        assertThat(new Expr(ELEMENT, 8).toString(), is("ELEMENT=8"));
        assertThat(new Expr(ELEMENT, 8).or(new Expr(DEGREE, 3)).toString(), is("OR(ELEMENT=8,DEGREE=3)"));
        assertThat(new Expr(ELEMENT, 8).and(new Expr(DEGREE, 3)).toString(), is("AND(ELEMENT=8,DEGREE=3)"));
        assertThat(new Expr(ELEMENT, 8).negate().toString(), is("NOT(ELEMENT=8)"));
        assertThat(new Expr(RECURSIVE, null).toString(), is("RECURSIVE(...)"));
    }

    @Test public void testNegationOptimizations() {
        assertThat(new Expr(TRUE).negate(), is(new Expr(FALSE)));
        assertThat(new Expr(FALSE).negate(), is(new Expr(TRUE)));
        assertThat(new Expr(IS_IN_RING).negate(), is(new Expr(IS_IN_CHAIN)));
        assertThat(new Expr(IS_IN_CHAIN).negate(), is(new Expr(IS_IN_RING)));
        assertThat(new Expr(IS_ALIPHATIC).negate(), is(new Expr(IS_AROMATIC)));
        assertThat(new Expr(IS_AROMATIC).negate(), is(new Expr(IS_ALIPHATIC)));
        assertThat(new Expr(ELEMENT, 8).negate(),
                   is(new Expr(NOT, new Expr(ELEMENT, 8), null)));
        assertThat(new Expr(NOT, new Expr(ELEMENT, 8), null).negate(),
                   is(new Expr(ELEMENT, 8)));
        assertThat(new Expr(HAS_UNSPEC_ISOTOPE).negate(),
                   is(new Expr(HAS_ISOTOPE)));
        assertThat(new Expr(HAS_ISOTOPE).negate(),
                   is(new Expr(HAS_UNSPEC_ISOTOPE)));
    }

    @Test public void testLeftBalancedOr1() {
        Expr expr1 = new Expr(ELEMENT, 9);
        Expr expr2 = new Expr(ELEMENT, 17).or(new Expr(ELEMENT, 35));
        expr1.or(expr2);
        assertThat(expr1.left().type(), is(ELEMENT));
    }

    @Test public void testLeftBalancedOr2() {
        Expr expr1 = new Expr(ELEMENT, 9);
        Expr expr2 = new Expr(ELEMENT, 17).or(new Expr(ELEMENT, 35));
        expr2.or(expr1);
        assertThat(expr2.left().type(), is(ELEMENT));
    }

    @Test public void testLeftBalancedAnd1() {
        Expr expr1 = new Expr(ELEMENT, 9);
        Expr expr2 = new Expr(DEGREE, 2).and(new Expr(HAS_IMPLICIT_HYDROGEN));
        expr1.and(expr2);
        assertThat(expr1.left().type(), is(ELEMENT));
    }

    @Test public void testLeftBalancedAnd2() {
        Expr expr1 = new Expr(ELEMENT, 9);
        Expr expr2 = new Expr(DEGREE, 2).and(new Expr(HAS_IMPLICIT_HYDROGEN));
        expr2.and(expr1);
        assertThat(expr2.left().type(), is(DEGREE));
        assertThat(expr2.right().type(), is(AND));
        assertThat(expr2.right().left().type(), is(HAS_IMPLICIT_HYDROGEN));
        assertThat(expr2.right().right().type(), is(ELEMENT));
    }

    @Test public void alwaysTrueAnd() {
        assertThat(new Expr(TRUE).and(new Expr(TRUE)),
                   is(new Expr(TRUE)));
    }

    @Test public void alwaysFalseAnd() {
        assertThat(new Expr(FALSE).and(new Expr(TRUE)),
                   is(new Expr(FALSE)));
    }

    @Test public void removeFalseOr() {
        assertThat(new Expr(DEGREE, 2).or(new Expr(FALSE)),
                   is(new Expr(DEGREE, 2)));
        assertThat(new Expr(DEGREE, 2).or(new Expr(TRUE)),
                   is(new Expr(DEGREE, 2)));
        assertThat(new Expr(FALSE).or(new Expr(DEGREE, 2)),
                   is(new Expr(DEGREE, 2)));
        assertThat(new Expr(TRUE).or(new Expr(DEGREE, 2)),
                   is(new Expr(DEGREE, 2)));
    }


}
