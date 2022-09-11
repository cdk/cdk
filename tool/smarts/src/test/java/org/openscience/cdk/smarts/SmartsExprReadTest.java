/*
 * Copyright (c) 2018 John Mayfield <jwmay@users.sf.net>
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

package org.openscience.cdk.smarts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.silent.AtomContainer;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.*;

class SmartsExprReadTest {

    static Expr expr(Expr.Type type) {
        return new Expr(type);
    }

    static Expr expr(Expr.Type type, int val) {
        return new Expr(type, val);
    }

    static Expr and(Expr a, Expr b) {
        return new Expr(AND, a, b);
    }

    static Expr or(Expr a, Expr b) {
        return new Expr(OR, a, b);
    }

    private static Expr getAtomExpr(IAtom atom) {
        return ((QueryAtom) AtomRef.deref(atom)).getExpression();
    }

    private static Expr getBondExpr(IBond bond) {
        return ((QueryBond) BondRef.deref(bond)).getExpression();
    }

    static Expr getAtomExpr(String sma, int flav) {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, sma, flav));
        return getAtomExpr(mol.getAtom(0));
    }

    static Expr getAtomExpr(String sma) {
        return getAtomExpr(sma, Smarts.FLAVOR_LOOSE);
    }

    static Expr getBondExpr(String sma, int flav) {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, sma, flav));
        return getBondExpr(mol.getBond(0));
    }

    static Expr getBondExpr(String sma) {
        return getBondExpr(sma, Smarts.FLAVOR_LOOSE);
    }

    @Test
    void trailingOperator() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertFalse(Smarts.parse(mol, "[a#6,]"));
        Assertions.assertFalse(Smarts.parse(mol, "[a#6;]"));
        Assertions.assertFalse(Smarts.parse(mol, "[a#6&]"));
        Assertions.assertFalse(Smarts.parse(mol, "[a#6!]"));
    }

    @Test
    void leadingOperator() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertFalse(Smarts.parse(mol, "[,a#6]"));
        Assertions.assertFalse(Smarts.parse(mol, "[;a#6]"));
        Assertions.assertFalse(Smarts.parse(mol, "[&a#6]"));
        Assertions.assertTrue(Smarts.parse(mol, "[!a#6]"));
    }

    @Test
    void trailingBondOperator() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertFalse(Smarts.parse(mol, "*-,*"));
        Assertions.assertFalse(Smarts.parse(mol, "*-;*"));
        Assertions.assertFalse(Smarts.parse(mol, "*-&*"));
        Assertions.assertFalse(Smarts.parse(mol, "*-!*"));
    }

    @Test
    void leadingBondOperator() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertFalse(Smarts.parse(mol, "*,-*"));
        Assertions.assertFalse(Smarts.parse(mol, "*;-*"));
        Assertions.assertFalse(Smarts.parse(mol, "*&-*"));
        Assertions.assertTrue(Smarts.parse(mol, "*!-*"));
    }

    @Test
    void opPrecedence1() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[a#6,a#7]"));
        Expr actual = getAtomExpr(mol.getAtom(0));
        Expr expected = or(and(expr(IS_AROMATIC), expr(ELEMENT, 6)),
                           and(expr(IS_AROMATIC), expr(ELEMENT, 7)));
        assertThat(actual, is(expected));
    }

    @Test
    void opPrecedence2() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[a;#6,#7]"));
        Expr actual = getAtomExpr(mol.getAtom(0));
        Expr expected = and(expr(IS_AROMATIC),
                            or(expr(ELEMENT, 6), expr(ELEMENT, 7)));
        assertThat(actual, is(expected));
    }

    @Test
    void opPrecedence3() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[#6,#7;a]"));
        Expr actual = getAtomExpr(mol.getAtom(0));
        Expr expected = and(expr(IS_AROMATIC),
                            or(expr(ELEMENT, 6), expr(ELEMENT, 7)));
        assertThat(actual, is(expected));
    }

    @Test
    void opPrecedence4() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[#6,#7a]"));
        Expr actual = getAtomExpr(mol.getAtom(0));
        Expr expected = or(expr(ELEMENT, 6),
                           and(expr(ELEMENT, 7), expr(IS_AROMATIC)));
        assertThat(actual, is(expected));
    }

    @Test
    void opPrecedence5() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[#6&a,#7]"));
        Expr actual = getAtomExpr(mol.getAtom(0));
        Expr expected = or(expr(ELEMENT, 7),
                           and(expr(ELEMENT, 6), expr(IS_AROMATIC)));
        assertThat(actual, is(expected));
    }

    @Test
    void orList() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[F,Cl,Br,I]"));
        Expr actual = getAtomExpr(mol.getAtom(0));
        Expr expected = or(expr(ELEMENT, 9),
                           or(expr(ELEMENT, 17),
                              or(expr(ELEMENT, 35),
                                 expr(ELEMENT, 53))));
        assertThat(actual, is(expected));
    }

    @Test
    void explicitHydrogen() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[2H+]"));
        Expr actual = getAtomExpr(mol.getAtom(0));
        Expr expected = and(expr(ISOTOPE, 2),
                            and(expr(ELEMENT, 1), expr(FORMAL_CHARGE, 1)));
        assertThat(actual, is(expected));
    }

    @Test
    void explicitHydrogenNeg() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[H-]"));
        Expr actual = getAtomExpr(mol.getAtom(0));
        Expr expected = and(expr(ELEMENT, 1),
                            expr(FORMAL_CHARGE, -1));
        assertThat(actual, is(expected));
    }

    @Test
    void explicitHydrogenWithAtomMap() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[2H+:2]"));
        Expr actual = getAtomExpr(mol.getAtom(0));
        Expr expected = and(expr(ISOTOPE, 2),
                            and(expr(ELEMENT, 1),
                                expr(FORMAL_CHARGE, 1)));
        assertThat(mol.getAtom(0).getProperty(CDKConstants.ATOM_ATOM_MAPPING,
                                              Integer.class),
                   is(2));
        assertThat(actual, is(expected));
    }

    @Test
    void explicitHydrogenWithBadAtomMap() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertFalse(Smarts.parse(mol, "[2H+:]"));
    }

    @Test
    void nonExplicitHydrogen() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[2&H+]"));
        Expr actual = getAtomExpr(mol.getAtom(0));
        Expr expected = and(expr(ISOTOPE, 2),
                            and(expr(TOTAL_H_COUNT, 1),
                                expr(FORMAL_CHARGE, +1)));
        assertThat(actual, is(expected));
    }

    @Test
    void nonExplicitHydrogen2() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[2,H+]"));
        Expr actual = getAtomExpr(mol.getAtom(0));
        Expr expected = or(expr(ISOTOPE, 2),
                           and(expr(TOTAL_H_COUNT, 1),
                               expr(FORMAL_CHARGE, +1)));
        assertThat(actual, is(expected));
    }

    @Test
    void nonExplicitHydrogen3() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[2H1+]"));
        Expr actual = getAtomExpr(mol.getAtom(0));
        Expr expected = and(expr(ISOTOPE, 2),
                            and(expr(TOTAL_H_COUNT, 1),
                                expr(FORMAL_CHARGE, 1)));
        assertThat(actual, is(expected));
    }

    @Test
    void specifiedIsotope() {
        Expr actual   = getAtomExpr("[!0]");
        Expr expected = expr(HAS_ISOTOPE);
        assertThat(actual, is(expected));
    }

    @Test
    void unspecifiedIsotope() {
        Expr actual   = getAtomExpr("[0]");
        Expr expected = expr(HAS_UNSPEC_ISOTOPE);
        assertThat(actual, is(expected));
    }

    @Test
    void ringMembership() {
        Expr actual   = getAtomExpr("[R]");
        Expr expected = expr(IS_IN_RING);
        assertThat(actual, is(expected));
    }

    @Test
    void ringMembership2() {
        Expr actual   = getAtomExpr("[!R0]");
        Expr expected = expr(IS_IN_RING);
        assertThat(actual, is(expected));
    }

    @Test
    void chainMembership() {
        Expr actual   = getAtomExpr("[R0]");
        Expr expected = expr(IS_IN_CHAIN);
        assertThat(actual, is(expected));
    }

    @Test
    void chainMembership2() {
        Expr actual   = getAtomExpr("[!R]");
        Expr expected = expr(IS_IN_CHAIN);
        assertThat(actual, is(expected));
    }

    @Test
    void chainMembership3() {
        Expr actual   = getAtomExpr("[r0]");
        Expr expected = expr(IS_IN_CHAIN);
        assertThat(actual, is(expected));
    }

    @Test
    void chainMembership4() {
        Expr actual   = getAtomExpr("[x0]");
        Expr expected = expr(IS_IN_CHAIN);
        assertThat(actual, is(expected));
    }

    @Test
    void aromatic() {
        Expr actual   = getAtomExpr("[a]");
        Expr expected = expr(IS_AROMATIC);
        assertThat(actual, is(expected));
    }

    @Test
    void aromatic2() {
        Expr actual   = getAtomExpr("[!A]");
        Expr expected = expr(IS_AROMATIC);
        assertThat(actual, is(expected));
    }

    @Test
    void aliphatic() {
        Expr actual   = getAtomExpr("[A]");
        Expr expected = expr(IS_ALIPHATIC);
        assertThat(actual, is(expected));
    }

    @Test
    void aliphatic2() {
        Expr actual   = getAtomExpr("[!a]");
        Expr expected = expr(IS_ALIPHATIC);
        assertThat(actual, is(expected));
    }

    @Test
    void notTrue() {
        Expr actual   = getAtomExpr("[!*]");
        Expr expected = expr(FALSE);
        assertThat(actual, is(expected));
    }

    @Test
    void notNotTrue() {
        Expr actual   = getAtomExpr("[!!*]");
        Expr expected = expr(TRUE);
        assertThat(actual, is(expected));
    }

    @Test
    void ringCountDefault() {
        Expr actual   = getAtomExpr("[R]");
        Expr expected = expr(IS_IN_RING);
        assertThat(actual, is(expected));
    }

    @Test
    void ringCount0() {
        Expr actual   = getAtomExpr("[R0]");
        Expr expected = expr(IS_IN_CHAIN);
        assertThat(actual, is(expected));
    }

    @Test
    void ringCount() {
        Expr actual   = getAtomExpr("[R1]");
        Expr expected = expr(RING_COUNT, 1);
        assertThat(actual, is(expected));
    }

    @Test
    void ringCountOEChem() {
        Expr actual   = getAtomExpr("[R2]", Smarts.FLAVOR_OECHEM);
        Expr expected = expr(RING_BOND_COUNT, 2);
        assertThat(actual, is(expected));
    }

    @Test
    void ringSmallest() {
        Expr actual   = getAtomExpr("[r5]");
        Expr expected = expr(RING_SMALLEST, 5);
        assertThat(actual, is(expected));
    }

    @Test
    void ringSmallestDefault() {
        Expr actual   = getAtomExpr("[r]");
        Expr expected = expr(IS_IN_RING);
        assertThat(actual, is(expected));
    }

    @Test
    void ringSmallestInvalid() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[r0]")); // not in ring
        Assertions.assertFalse(Smarts.parse(mol, "[r1]"));
        Assertions.assertFalse(Smarts.parse(mol, "[r2]"));
        Assertions.assertTrue(Smarts.parse(mol, "[r3]"));
    }

    // make sure not read as C & r
    @Test
    void chromium() {
        Expr actual   = getAtomExpr("[Cr]");
        Expr expected = expr(ELEMENT, Elements.Chromium.number());
        assertThat(actual, is(expected));
    }

    @Test
    void hetero() {
        Expr actual   = getAtomExpr("[#X]");
        Expr expected = expr(IS_HETERO);
        assertThat(actual, is(expected));
    }

    @Test
    void ringSize() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[Z8]", Smarts.FLAVOR_DAYLIGHT));
        Expr actual   = getAtomExpr(mol.getAtom(0));
        Expr expected = expr(RING_SIZE, 8);
        assertThat(actual, is(expected));
    }

    @Test
    void ringSize0() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[Z0]", Smarts.FLAVOR_DAYLIGHT));
        Expr actual   = getAtomExpr(mol.getAtom(0));
        Expr expected = expr(IS_IN_CHAIN);
        assertThat(actual, is(expected));
    }

    @Test
    void ringSizeDefault() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[Z]", Smarts.FLAVOR_DAYLIGHT));
        Expr actual   = getAtomExpr(mol.getAtom(0));
        Expr expected = expr(IS_IN_RING);
        assertThat(actual, is(expected));
    }

    @Test
    void adjacentHeteroCount() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[Z2]", Smarts.FLAVOR_CACTVS));
        Expr actual   = getAtomExpr(mol.getAtom(0));
        Expr expected = expr(ALIPHATIC_HETERO_SUBSTITUENT_COUNT, 2);
        assertThat(actual, is(expected));
    }

    @Test
    void adjacentHetero() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[Z]", Smarts.FLAVOR_CACTVS));
        Expr actual   = getAtomExpr(mol.getAtom(0));
        Expr expected = expr(HAS_ALIPHATIC_HETERO_SUBSTITUENT);
        assertThat(actual, is(expected));
    }

    @Test
    void adjacentHetero0() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertTrue(Smarts.parse(mol, "[Z0]", Smarts.FLAVOR_CACTVS));
        Expr actual   = getAtomExpr(mol.getAtom(0));
        Expr expected = expr(HAS_ALIPHATIC_HETERO_SUBSTITUENT).negate();
        assertThat(actual, is(expected));
    }

    @Test
    void valence() {
        Expr actual   = getAtomExpr("[v4]");
        Expr expected = expr(VALENCE, 4);
        assertThat(actual, is(expected));
    }

    @Test
    void valenceDefault() {
        Expr actual   = getAtomExpr("[v]");
        Expr expected = expr(VALENCE, 1);
        assertThat(actual, is(expected));
    }

    @Test
    void degree() {
        Expr actual   = getAtomExpr("[D4]");
        Expr expected = expr(DEGREE, 4);
        assertThat(actual, is(expected));
    }

    @Test
    void degreeDefault() {
        Expr actual   = getAtomExpr("[D]");
        Expr expected = expr(DEGREE, 1);
        assertThat(actual, is(expected));
    }

    @Test
    void degreeCDKLegacy() {
        Expr actual   = getAtomExpr("[D4]", Smarts.FLAVOR_CDK_LEGACY);
        Expr expected = expr(HEAVY_DEGREE, 4);
        assertThat(actual, is(expected));
    }

    @Test
    void degreeCDKLegacyDefault() {
        Expr actual   = getAtomExpr("[D]", Smarts.FLAVOR_CDK_LEGACY);
        Expr expected = expr(HEAVY_DEGREE, 1);
        assertThat(actual, is(expected));
    }

    @Test
    void connectivity() {
        Expr actual   = getAtomExpr("[X4]");
        Expr expected = expr(TOTAL_DEGREE, 4);
        assertThat(actual, is(expected));
    }

    @Test
    void connectivityDefault() {
        Expr actual   = getAtomExpr("[X]");
        Expr expected = expr(TOTAL_DEGREE, 1);
        assertThat(actual, is(expected));
    }

    @Test
    void totalHCount() {
        Expr actual   = getAtomExpr("[H2]");
        Expr expected = expr(TOTAL_H_COUNT, 2);
        assertThat(actual, is(expected));
    }

    @Test
    void implHCount() {
        Expr actual   = getAtomExpr("[h2]");
        Expr expected = expr(IMPL_H_COUNT, 2);
        assertThat(actual, is(expected));
    }

    @Test
    void hasImplHCount() {
        Expr actual   = getAtomExpr("[h]");
        Expr expected = expr(HAS_IMPLICIT_HYDROGEN);
        assertThat(actual, is(expected));
    }

    @Test
    void ringBondCount() {
        Expr actual   = getAtomExpr("[x2]");
        Expr expected = expr(RING_BOND_COUNT, 2);
        assertThat(actual, is(expected));
    }

    @Test
    void ringBondCount0() {
        Expr actual   = getAtomExpr("[x0]");
        Expr expected = expr(IS_IN_CHAIN);
        assertThat(actual, is(expected));
    }

    @Test
    void ringBondCount1() {
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "[x1]"));
    }

    @Test
    void ringBondCountDefault() {
        Expr actual   = getAtomExpr("[x]");
        Expr expected = expr(IS_IN_RING);
        assertThat(actual, is(expected));
    }

    @Test
    void formalChargeNeg() {
        Expr actual   = getAtomExpr("[-1]");
        Expr expected = expr(FORMAL_CHARGE, -1);
        assertThat(actual, is(expected));
    }

    @Test
    void formalChargeNegNeg() {
        Expr actual   = getAtomExpr("[--]");
        Expr expected = expr(FORMAL_CHARGE, -2);
        assertThat(actual, is(expected));
    }

    @Test
    void formalChargePos() {
        Expr actual   = getAtomExpr("[+]");
        Expr expected = expr(FORMAL_CHARGE, +1);
        assertThat(actual, is(expected));
    }

    @Test
    void formalChargePosPos() {
        Expr actual   = getAtomExpr("[++]");
        Expr expected = expr(FORMAL_CHARGE, +2);
        assertThat(actual, is(expected));
    }

    @Test
    void atomMaps() {
        IAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertFalse(Smarts.parse(mol, "[:10]"));
        Assertions.assertTrue(Smarts.parse(mol, "[*:10]"));
        assertThat(mol.getAtom(0).getProperty(CDKConstants.ATOM_ATOM_MAPPING, Integer.class),
                   is(10));
    }

    @Test
    void periodicTableGroup() {
        Expr actual   = getAtomExpr("[#G16]", Smarts.FLAVOR_MOE);
        Expr expected = expr(PERIODIC_GROUP, 16);
        assertThat(actual, is(expected));
    }

    @Test
    void periodicTableGroupCDKLegacy() {
        Expr actual   = getAtomExpr("[G16]", Smarts.FLAVOR_CDK_LEGACY);
        Expr expected = expr(PERIODIC_GROUP, 16);
        assertThat(actual, is(expected));
    }

    @Test
    void insaturationCactvs() {
        Expr actual   = getAtomExpr("[G1]", Smarts.FLAVOR_CACTVS);
        Expr expected = expr(INSATURATION, 1);
        assertThat(actual, is(expected));
    }

    @Test
    void insaturationCactvsOrMoe() {
        assertThat(getAtomExpr("[i1]", Smarts.FLAVOR_CACTVS),
                   is(expr(INSATURATION, 1)));
        assertThat(getAtomExpr("[i1]", Smarts.FLAVOR_MOE),
                   is(expr(INSATURATION, 1)));
    }

    @Test
    void heteroSubCountCactvs() {
        assertThat(getAtomExpr("[z]", Smarts.FLAVOR_CACTVS),
                   is(expr(HAS_HETERO_SUBSTITUENT)));
        assertThat(getAtomExpr("[z1]", Smarts.FLAVOR_CACTVS),
                   is(expr(HETERO_SUBSTITUENT_COUNT, 1)));
    }

    @Test
    void hybridisationNumber() {
        Expr actual   = getAtomExpr("[^2]", Smarts.FLAVOR_OECHEM);
        Expr expected = expr(HYBRIDISATION_NUMBER, 2);
        assertThat(actual, is(expected));
    }

    @Test
    void hybridisationNumberDaylight() {
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null),
                                            "[^2]",
                                            Smarts.FLAVOR_DAYLIGHT));
    }

    @Test
    void atomStereoLeft() {
        Expr actual   = getAtomExpr("[@]");
        Expr expected = expr(STEREOCHEMISTRY, IStereoElement.LEFT);
        assertThat(actual, is(expected));
    }

    @Test
    void atomStereoRight() {
        Expr actual   = getAtomExpr("[@@]");
        Expr expected = expr(STEREOCHEMISTRY, IStereoElement.RIGHT);
        assertThat(actual, is(expected));
    }

    @Test
    void atomStereoLeftOrUnspec() {
        Expr actual   = getAtomExpr("[@?]");
        Expr expected = or(expr(STEREOCHEMISTRY, IStereoElement.LEFT),
                           expr(STEREOCHEMISTRY, 0));
        assertThat(actual, is(expected));
    }

    @Test
    void atomStereoSimpleLeft() {
        Expr actual   = getAtomExpr("[C@H]");
        assertThat(actual, is(new Expr(ALIPHATIC_ELEMENT, 6)
                                      .and(new Expr(STEREOCHEMISTRY, 1))
                                      .and(new Expr(TOTAL_H_COUNT, 1))));
    }

    @Test
    void badExprs() {
        IAtomContainer mol = new AtomContainer();
        Assertions.assertFalse(Smarts.parse(mol, "*-,*"));
        Assertions.assertFalse(Smarts.parse(mol, "*-;*"));
        Assertions.assertFalse(Smarts.parse(mol, "*-!*"));
        Assertions.assertFalse(Smarts.parse(mol, "*-&*"));
        Assertions.assertFalse(Smarts.parse(mol, "*!*"));
        Assertions.assertFalse(Smarts.parse(mol, "*,*"));
        Assertions.assertFalse(Smarts.parse(mol, "*;*"));
        Assertions.assertFalse(Smarts.parse(mol, "*&*"));
        Assertions.assertFalse(Smarts.parse(mol, "*,-*"));
    }

    @Test
    void singleOrAromatic() {
        Expr actual   = getBondExpr("**");
        Expr expected = expr(SINGLE_OR_AROMATIC);
        assertThat(expected, is(actual));
    }

    @Test
    void singleBond() {
        Expr actual   = getBondExpr("*-*");
        Expr expected = expr(ALIPHATIC_ORDER, 1);
        assertThat(expected, is(actual));
    }

    @Test
    void doubleBond() {
        Expr actual   = getBondExpr("*=*");
        Expr expected = expr(ALIPHATIC_ORDER, 2);
        assertThat(expected, is(actual));
    }

    @Test
    void tripleBond() {
        Expr actual   = getBondExpr("*#*");
        Expr expected = expr(ALIPHATIC_ORDER, 3);
        assertThat(expected, is(actual));
    }

    @Test
    void quadBond() {
        Expr actual   = getBondExpr("*$*");
        Expr expected = expr(ALIPHATIC_ORDER, 4);
        assertThat(expected, is(actual));
    }

    @Test
    void aromaticBond() {
        Expr actual   = getBondExpr("*:*");
        Expr expected = expr(IS_AROMATIC);
        assertThat(expected, is(actual));
    }

    @Test
    void aliphaticBond() {
        Expr actual   = getBondExpr("*!:*");
        Expr expected = expr(IS_ALIPHATIC);
        assertThat(expected, is(actual));
    }

    @Test
    void chainBond() {
        Expr actual   = getBondExpr("*!@*");
        Expr expected = expr(IS_IN_CHAIN);
        assertThat(expected, is(actual));
    }

    @Test
    void anyBond() {
        Expr actual   = getBondExpr("*~*");
        Expr expected = expr(TRUE);
        assertThat(expected, is(actual));
    }

    @Test
    void singleOrDouble() {
        Expr actual = getBondExpr("*-,=*");
        Expr expected = or(expr(ALIPHATIC_ORDER, 1),
                           expr(ALIPHATIC_ORDER, 2));
        assertThat(expected, is(actual));
    }

    @Test
    void operatorPrecedence() {
        Expr actual = getBondExpr("*@;-,=*");
        Expr expected = and(expr(IS_IN_RING),
                            or(expr(ALIPHATIC_ORDER, 1),
                               expr(ALIPHATIC_ORDER, 2)));
        assertThat(expected, is(actual));
    }

    @Test
    void notInRing() {
        Expr actual   = getBondExpr("*!@*");
        Expr expected = expr(IS_IN_CHAIN);
        assertThat(expected, is(actual));
    }

    @Test
    void notAromatic() {
        Expr actual   = getBondExpr("*!:*");
        Expr expected = expr(IS_ALIPHATIC);
        assertThat(expected, is(actual));
    }

    @Test
    void notNotWildcard() {
        Expr actual   = getBondExpr("*!!~*");
        Expr expected = expr(TRUE);
        assertThat(expected, is(actual));
    }

    @Test
    void testAliphaticSymbols() {
        for (Elements e : Elements.values()) {
            int len = e.symbol().length();
            if (len == 1 || len == 2) {
                String             smarts = "[" + e.symbol() + "]";
                QueryAtomContainer mol    = new QueryAtomContainer(null);
                Assertions.assertTrue(Smarts.parse(mol, smarts), smarts);
                Expr expr = getAtomExpr(mol.getAtom(0));
                assertThat(expr, anyOf(is(new Expr(ELEMENT, e.number())),
                                       is(new Expr(ALIPHATIC_ELEMENT, e.number()))));
            }
        }
    }

    @Test
    void testAromaticSymbols() {
        assertThat(getAtomExpr("[b]"), is(new Expr(AROMATIC_ELEMENT, 5)));
        assertThat(getAtomExpr("[c]"), is(new Expr(AROMATIC_ELEMENT, 6)));
        assertThat(getAtomExpr("[n]"), is(new Expr(AROMATIC_ELEMENT, 7)));
        assertThat(getAtomExpr("[o]"), is(new Expr(AROMATIC_ELEMENT, 8)));
        assertThat(getAtomExpr("[al]"), is(new Expr(AROMATIC_ELEMENT, 13)));
        assertThat(getAtomExpr("[si]"), is(new Expr(AROMATIC_ELEMENT, 14)));
        assertThat(getAtomExpr("[p]"), is(new Expr(AROMATIC_ELEMENT, 15)));
        assertThat(getAtomExpr("[s]"), is(new Expr(AROMATIC_ELEMENT, 16)));
        assertThat(getAtomExpr("[as]"), is(new Expr(AROMATIC_ELEMENT, 33)));
        assertThat(getAtomExpr("[se]"), is(new Expr(AROMATIC_ELEMENT, 34)));
        assertThat(getAtomExpr("[sb]"), is(new Expr(AROMATIC_ELEMENT, 51)));
        assertThat(getAtomExpr("[te]"), is(new Expr(AROMATIC_ELEMENT, 52)));
    }

    @Test
    void testBadSymbols() {
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "[L]"));
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "[J]"));
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "[Q]"));
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "[G]"));
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "[T]"));
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "[M]"));
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "[E]"));
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "[t]"));
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "[?]"));
    }

    @Test
    void testRecursive() {
        Assertions.assertTrue(Smarts.parse(new QueryAtomContainer(null), "[$(*OC)]"));
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "[$*OC)]"));
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "[$(*OC]"));
        Assertions.assertTrue(Smarts.parse(new QueryAtomContainer(null), "[$((*[O-].[Na+]))]"));
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "[$([J])]"));
    }

    // recursive SMARTS with single atoms should be 'lifted' up to a single
    // non-recursive expression
    @Test
    void testTrivialRecursive() {
        Expr expr = getAtomExpr("[$(F),$(Cl),$(Br)]");
        assertThat(expr, is(or(expr(ELEMENT, 9),
                               or(expr(ELEMENT, 17),
                                  expr(ELEMENT, 35)))));
    }

    // must always be read/written in SMARTS as recursive but we can lift
    // the expression up to the top level
    @Test
    void testTrivialRecursive2() {
        Expr expr = getAtomExpr("[!$([F,Cl,Br])]");
        assertThat(expr, is(or(expr(ELEMENT, 9),
                                or(expr(ELEMENT, 17),
                                  expr(ELEMENT, 35))).negate()));
    }

    @Test
    void ringOpenCloseInconsistency() {
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "C=1CC-,=1"));
        Assertions.assertFalse(Smarts.parse(new QueryAtomContainer(null), "C=1CC-1"));
    }

    @Test
    void ringOpenCloseConsistency() {
        Assertions.assertTrue(Smarts.parse(new QueryAtomContainer(null), "C-,=1CC-,=1"));
        Assertions.assertTrue(Smarts.parse(new QueryAtomContainer(null), "C!~1CC!~1"));
    }

    @Test
    void degreeRange() {
        Expr expr = getAtomExpr("[D{1-3}]");
        assertThat(expr, is(or(expr(DEGREE, 1),
                               or(expr(DEGREE, 2),
                                  expr(DEGREE, 3)))));
    }

    @Test
    void implHRange() {
        Expr expr = getAtomExpr("[h{1-3}]");
        assertThat(expr, is(or(expr(IMPL_H_COUNT, 1),
                               or(expr(IMPL_H_COUNT, 2),
                                  expr(IMPL_H_COUNT, 3)))));
    }

    @Test
    void totalHCountRange() {
        Expr expr = getAtomExpr("[H{1-3}]");
        assertThat(expr, is(or(expr(TOTAL_H_COUNT, 1),
                               or(expr(TOTAL_H_COUNT, 2),
                                  expr(TOTAL_H_COUNT, 3)))));
    }

    @Test
    void valenceRange() {
        Expr expr = getAtomExpr("[v{1-3}]");
        assertThat(expr, is(or(expr(VALENCE, 1),
                               or(expr(VALENCE, 2),
                                  expr(VALENCE, 3)))));
    }

    @Test
    void ringBondCountRange() {
        Expr expr = getAtomExpr("[x{2-4}]");
        assertThat(expr, is(or(expr(RING_BOND_COUNT, 2),
                               or(expr(RING_BOND_COUNT, 3),
                                  expr(RING_BOND_COUNT, 4)))));
    }

    @Test
    void ringSmallestSizeCountRange() {
        Expr expr = getAtomExpr("[r{5-7}]");
        assertThat(expr, is(or(expr(RING_SMALLEST, 5),
                               or(expr(RING_SMALLEST, 6),
                                  expr(RING_SMALLEST, 7)))));
    }

    @Test
    void supportInsaturatedByDefault() {
        Expr expr = getAtomExpr("[i]");
        assertThat(expr, is(expr(UNSATURATED)));
    }

    @Test
    void supportHGt() {
        Expr expr = getAtomExpr("[H>1]");
        assertThat(expr, is(and(expr(TOTAL_H_COUNT, 0).negate(),
                                expr(TOTAL_H_COUNT, 1).negate())));
    }

    @Test
    void supportHLt() {
        Expr expr = getAtomExpr("[H<2]");
        assertThat(expr, is(or(expr(TOTAL_H_COUNT, 0),
                               expr(TOTAL_H_COUNT, 1))));
    }

    @Test
    void supportDGt() {
        Expr expr = getAtomExpr("[D>1]");
        assertThat(expr, is(and(expr(DEGREE, 0).negate(),
                                expr(DEGREE, 1).negate())));
    }

    @Test
    void supportDLt() {
        Expr expr = getAtomExpr("[D<2]");
        assertThat(expr, is(or(expr(DEGREE, 0),
                               expr(DEGREE, 1))));
    }
}
