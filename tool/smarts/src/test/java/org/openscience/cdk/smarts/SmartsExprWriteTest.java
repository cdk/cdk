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
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ALIPHATIC_ELEMENT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ALIPHATIC_ORDER;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.AND;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.AROMATIC_ELEMENT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.DEGREE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ELEMENT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.FORMAL_CHARGE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.HAS_IMPLICIT_HYDROGEN;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.HAS_ISOTOPE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.HAS_UNSPEC_ISOTOPE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ISOTOPE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.IS_AROMATIC;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.IS_IN_CHAIN;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.IS_IN_RING;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.OR;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.RING_COUNT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.RING_SMALLEST;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.SINGLE_OR_DOUBLE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.TOTAL_DEGREE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.TOTAL_H_COUNT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.VALENCE;

class SmartsExprWriteTest {

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

    // C&r6 not Cr
    @Test
    void useExplAnd1() {
        Expr expr = new Expr(ALIPHATIC_ELEMENT, 6).and(
            new Expr(RING_SMALLEST, 6)
        );
        assertThat(Smarts.generateAtom(expr), is("[C&r6]"));
    }

    // D&2 not D2
    @Test
    void useExplAnd2() {
        Expr expr = new Expr(DEGREE, 1).and(
            new Expr(ISOTOPE, 2)
        );
        assertThat(Smarts.generateAtom(expr), is("[D&2]"));
    }

    // aromatic or aliphatic
    @Test
    void carbon() {
        Expr expr = new Expr(ELEMENT, 6);
        assertThat(Smarts.generateAtom(expr), is("[#6]"));
    }

    // helium can't be aromatic so we can always use the symbol
    @Test
    void helium() {
        Expr expr = new Expr(ELEMENT, 2);
        assertThat(Smarts.generateAtom(expr), is("[He]"));
    }

    @Test
    void degree() {
        assertThat(Smarts.generateAtom(expr(DEGREE, 1)), is("[D]"));
        assertThat(Smarts.generateAtom(expr(DEGREE, 2)), is("[D2]"));
    }

    // can sometimes write just 'H' but a lot of effort to figure out when
    @Test
    void totalHCount() {
        assertThat(Smarts.generateAtom(expr(TOTAL_H_COUNT, 1)), is("[H1]"));
        assertThat(Smarts.generateAtom(expr(TOTAL_H_COUNT, 2)), is("[H2]"));
    }

    @Test
    void connectivity() {
        assertThat(Smarts.generateAtom(expr(TOTAL_DEGREE, 1)), is("[X]"));
        assertThat(Smarts.generateAtom(expr(TOTAL_DEGREE, 2)), is("[X2]"));
    }

    @Test
    void ringMembership() {
        assertThat(Smarts.generateAtom(expr(IS_IN_RING)), is("[R]"));
        assertThat(Smarts.generateAtom(expr(IS_IN_CHAIN)), is("[!R]"));
    }

    @Test
    void ringCount() {
        assertThat(Smarts.generateAtom(expr(RING_COUNT, 2)), is("[R2]"));
    }

    @Test
    void ringSmallest() {
        assertThat(Smarts.generateAtom(expr(RING_SMALLEST, 4)), is("[r4]"));
    }

    @Test
    void isotopes() {
        assertThat(Smarts.generateAtom(expr(ISOTOPE, 13)), is("[13]"));
        assertThat(Smarts.generateAtom(expr(HAS_UNSPEC_ISOTOPE)), is("[0]"));
        assertThat(Smarts.generateAtom(expr(HAS_ISOTOPE)), is("[!0]"));
    }

    @Test
    void formalCharges() {
        assertThat(Smarts.generateAtom(expr(FORMAL_CHARGE, -2)), is("[-2]"));
        assertThat(Smarts.generateAtom(expr(FORMAL_CHARGE, -1)), is("[-]"));
        assertThat(Smarts.generateAtom(expr(FORMAL_CHARGE, 0)), is("[+0]"));
        assertThat(Smarts.generateAtom(expr(FORMAL_CHARGE, 1)), is("[+]"));
        assertThat(Smarts.generateAtom(expr(FORMAL_CHARGE, 2)), is("[+2]"));
    }

    @Test
    void valence() {
        assertThat(Smarts.generateAtom(expr(VALENCE, 1)), is("[v]"));
        assertThat(Smarts.generateAtom(expr(VALENCE, 2)), is("[v2]"));
    }

    @Test
    void atomicNum() {
        assertThat(Smarts.generateAtom(expr(ELEMENT, 0)), is("[#0]"));
        assertThat(Smarts.generateAtom(expr(ALIPHATIC_ELEMENT, 0)), is("[#0]"));
        assertThat(Smarts.generateAtom(expr(AROMATIC_ELEMENT, 0)), is("[#0]"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, 1)), is("[#1]"));
        assertThat(Smarts.generateAtom(expr(ALIPHATIC_ELEMENT, 1)), is("[#1]"));
        assertThat(Smarts.generateAtom(expr(AROMATIC_ELEMENT, 1)), is("[#1]"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, 2)), is("[He]"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, 3)), is("[Li]"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, 6)), is("[#6]"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, 7)), is("[#7]"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, 8)), is("[#8]"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, 9)), is("F"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, 10)), is("[Ne]"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, 11)), is("[Na]"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, 12)), is("[Mg]"));
        // Ds, Ts and Nh etc write as #<num>
        assertThat(Smarts.generateAtom(expr(ELEMENT, Elements.Darmstadtium.number())),
                   is("[#110]"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, Elements.Tennessine.number())),
                   is("[#117]"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, Elements.Nihonium.number())),
                   is("[#113]"));
    }

    // Ds, Ts and Nh etc can be ambiguous - we write anything above radon as
    // '#<num>'
    @Test
    void atomicNumHighWeightElements() {
        assertThat(Smarts.generateAtom(expr(ELEMENT, Elements.Darmstadtium.number())),
                   is("[#110]"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, Elements.Tennessine.number())),
                   is("[#117]"));
        assertThat(Smarts.generateAtom(expr(ELEMENT, Elements.Nihonium.number())),
                   is("[#113]"));
        assertThat(Smarts.generateAtom(expr(ALIPHATIC_ELEMENT, Elements.Darmstadtium.number())),
                   is("[#110]"));
        assertThat(Smarts.generateAtom(expr(ALIPHATIC_ELEMENT, Elements.Tennessine.number())),
                   is("[#117]"));
        assertThat(Smarts.generateAtom(expr(ALIPHATIC_ELEMENT, Elements.Nihonium.number())),
                   is("[#113]"));
    }

    @Test
    void aromaticElement() {
        assertThat(Smarts.generateAtom(expr(AROMATIC_ELEMENT, 6)), is("c"));
        assertThat(Smarts.generateAtom(expr(AROMATIC_ELEMENT, 7)), is("n"));
    }

    @Test
    void useLowPrecedenceAnd() {
        Expr expr = new Expr(ELEMENT, 8).and(
            new Expr(DEGREE, 2).or(
                new Expr(DEGREE, 1)));
        assertThat(Smarts.generateAtom(expr), is("[#8;D2,D]"));
    }

    @Test
    void useImplAnd() {
        Expr expr = new Expr(AROMATIC_ELEMENT, 7).and(
            new Expr(DEGREE, 2).and(
                new Expr(HAS_IMPLICIT_HYDROGEN)));
        assertThat(Smarts.generateAtom(expr), is("[nD2h]"));
    }

    // logical under a negate needs to be recursive
    @Test
    void usrRecrNot() {
        Expr expr = new Expr(ELEMENT, 9)
            .or(new Expr(ELEMENT, 17))
            .or(new Expr(ELEMENT, 35))
            .negate();
        assertThat(Smarts.generateAtom(expr), is("[!$([F,Cl,Br])]"));
    }

    // or -> and -> or needs to be recursive
    @Test
    void usrRecrOr() {
        Expr expr = or(and(or(expr(ELEMENT, 6),
                              expr(ELEMENT, 7)),
                           expr(IS_IN_RING)),
                       expr(IS_AROMATIC));
        assertThat(Smarts.generateAtom(expr),
                          is("[$([#6,#7])R,a]"));
    }

    @Test
    void singleOrDouble() {
        Expr expr = new Expr(ALIPHATIC_ORDER, 1)
            .or(new Expr(ALIPHATIC_ORDER, 2));
        assertThat(Smarts.generateBond(expr), is("-,="));
    }

    @Test
    void singleOrDoubleInRing() {
        Expr expr = new Expr(IS_IN_RING)
            .and(new Expr(ALIPHATIC_ORDER, 1)
                      .or(new Expr(ALIPHATIC_ORDER, 2)));
        assertThat(Smarts.generateBond(expr), is("@;-,="));
    }

    @Test
    void singleOrDoubleInRing2() {
        Expr expr = new Expr(IS_IN_RING)
            .and(new Expr(SINGLE_OR_DOUBLE));
        assertThat(Smarts.generateBond(expr), is("@;-,="));
    }

    @Test
    void indoleRoundTrip() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "n1ccc2c1cccc2"));
        // CDK choice of data structures lose local arrangement but
        // output is still indole
        assertThat(Smarts.generate(mol), is("n1c2c(cc1)cccc2"));
    }

    @Test
    void indoleWithExprRoundTrip() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "[n;$(*C),$(*OC)]1ccc2c1cccc2"));
        // CDK choice of data structures lose local arrangement but
        // output is still indole
        assertThat(Smarts.generate(mol), is("[n;$(*C),$(*OC)]1c2c(cc1)cccc2"));
    }

    @Test
    void bondTrue() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C~C~N(~O)~O"));
        assertThat(Smarts.generate(mol), is("C~C~N(~O)~O"));
    }

    @Test
    void bondFalse() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C!~C"));
        assertThat(Smarts.generate(mol), is("C!~C"));
    }

    @Test
    void bondInChain() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C!@C"));
        assertThat(Smarts.generate(mol), is("C!@C"));
    }

    @Test
    void bondInRing() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C@C"));
        assertThat(Smarts.generate(mol), is("C@C"));
    }

    @Test
    void tripleBond() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C#C"));
        assertThat(Smarts.generate(mol), is("C#C"));
    }

    @Test
    void notTripleBond() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C!#C"));
        assertThat(Smarts.generate(mol), is("C!#C"));
    }

    @Test
    void aromaticBond() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "[#6]:[#6]"));
        assertThat(Smarts.generate(mol), is("[#6]:[#6]"));
    }

    @Test
    void ringClosureExprs() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C1CCCC-,=1"));
        assertThat(Smarts.generate(mol), is("C1-,=CCCC1"));
    }

    @Test
    void ringClosureExprs2() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C-,=1CCCC1"));
        assertThat(Smarts.generate(mol), is("C1-,=CCCC1"));
    }

    @Test
    void ringClosureExprs3() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C1-,=CCCC1"));
        assertThat(Smarts.generate(mol), is("C1CCCC-,=1"));
    }

    @Test
    void reaction() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "c1ccccc1[NH2]>>c1ccccc1N(~O)~O"));
        assertThat(Smarts.generate(mol), is("c1c(cccc1)[NH2]>>c1c(cccc1)N(~O)~O"));
    }

    @Test
    void reactionWithMaps() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "c1cccc[c:1]1[NH2:2]>>c1cccc[c:1]1[N:2](~O)~O"));
        assertThat(Smarts.generate(mol), is("c1[c:1](cccc1)[NH2:2]>>c1[c:1](cccc1)[N:2](~O)~O"));
    }

    @Test
    void compGrouping() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "([Na+].[Cl-]).c1ccccc1"));
        assertThat(Smarts.generate(mol), is("c1ccccc1.([Na+].[Cl-])"));
    }

    @Test
    void compGroupingOnAgent() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, ">(c1ccccc1[O-].[Na+])>"));
        assertThat(Smarts.generate(mol), is(">(c1c(cccc1)[O-].[Na+])>"));
    }

    @Test
    void atomStereo() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C[C@H](O)CC"));
        assertThat(Smarts.generate(mol), is("C[C@H1](O)CC"));
    }

    private static void swap(Object[] a, int i, int j) {
        Object tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    @Test
    void atomStereoReordered() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C[C@H](O)CC"));
        IBond[] bonds = AtomContainerManipulator.getBondArray(mol);
        swap(bonds, 1, 2);
        mol.setBonds(bonds);
        assertThat(Smarts.generate(mol), is("C[C@@H1](CC)O"));
    }

    @Test
    void atomStereoReordered2() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C[C@H](O)CC"));
        IAtom[] atoms = AtomContainerManipulator.getAtomArray(mol);
        swap(atoms, 0, 1);
        mol.setAtoms(atoms);
        assertThat(Smarts.generate(mol), is("[C@@H1](C)(O)CC"));
    }

    @Test
    void atomStereoReordered3() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "[C@H](C)(O)CC"));
        IAtom[] atoms = AtomContainerManipulator.getAtomArray(mol);
        swap(atoms, 0, 1);
        mol.setAtoms(atoms);
        assertThat(Smarts.generate(mol), is("C[C@@H1](O)CC"));
    }

    @Test
    void atomStereoOrUnspec() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C[C@?H](O)CC"));
        assertThat(Smarts.generate(mol), is("C[CH1@?](O)CC"));
    }

    @Test
    void bondStereoTrans() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C/C=C/C"));
        assertThat(Smarts.generate(mol), is("C/C=C/C"));
        IAtom[] atoms = AtomContainerManipulator.getAtomArray(mol);
        swap(atoms, 0, 1);
        mol.setAtoms(atoms);
        assertThat(Smarts.generate(mol), is("C(\\C)=C/C"));
    }

    @Test
    void bondStereoCisTrans() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C/C=C/,\\C"));
        assertThat(Smarts.generate(mol), is("C/C=C/,\\C"));
    }

    @Test
    void bondStereoCisUnspec() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C/C=C\\?C"));
        assertThat(Smarts.generate(mol), is("C/C=C\\?C"));
        // not trans same as cis/unspec
        mol.removeAllElements();
        Assertions.assertTrue(Smarts.parse(mol, "C/C=C!/C"));
        assertThat(Smarts.generate(mol), is("C/C=C\\?C"));
    }

    @Test
    void bondStereoTransUnspec() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C/?C=C/C"));
        assertThat(Smarts.generate(mol), is("C/C=C/?C"));
        // not cis same as trans/unspec
        mol.removeAllElements();
        Assertions.assertTrue(Smarts.parse(mol, "C/C=C!\\C"));
        assertThat(Smarts.generate(mol), is("C/C=C/?C"));
    }

    // unspecified db can be written as either /?\\? or !/!\\
    @Test
    void bondStereoUnspec() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C/C=C/?\\?C"));
        assertThat(Smarts.generate(mol), is("C/C=C!/!\\C"));
    }

    // here we have one bond symbol shared between two stereo
    // bonds, changing it's affects both stereos
    @Test
    void bondStereoCisThenTrans() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C/C=C\\C=C\\C"));
        assertThat(Smarts.generate(mol), is("C/C=C\\C=C\\C"));
    }

    // make sure we set the bond direction on the correct neighbor
    @Test
    void bondStereoCisThenTransWithNbr() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C/C=C(C)\\C=C\\C"));
        assertThat(Smarts.generate(mol), is("C/C=C(C)\\C=C\\C"));
    }

    @Test
    void bondStereoCisThenTransUnspecWithNbr() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C/C=C(C)\\C=C\\?O"));
        assertThat(Smarts.generate(mol), is("C/C=C(C)\\C=C\\?O"));
        IAtom[] atoms = AtomContainerManipulator.getAtomArray(mol);
        swap(atoms, 0, atoms.length-1);
        mol.setAtoms(atoms);
        assertThat(Smarts.generate(mol), is("O/?C=C/C(=C\\C)C"));
    }

    // this case is tricky, we need to set the non-query bond stereo
    // first then the 'or unspecified' one otherwise we would initially
    // set 'C/C=C(C)/?C=CO' and there is no way to set the other bond
    @Test
    void bondStereoCisThenTransUnspecWithNbrComplex() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C/?C=C(C)\\C=C\\O"));
        assertThat(Smarts.generate(mol), is("C/?C=C(C)/C=C/O"));
    }

    // multiple calls to parse should set the stereo correctly and
    // put the queries in a single atom container
    @Test
    void multipleReads() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Assertions.assertTrue(Smarts.parse(mol, "C/C=C/C"));
        Assertions.assertTrue(Smarts.parse(mol, "C/C=C\\C"));
        assertThat(Smarts.generate(mol), is("C/C=C/C.C/C=C\\C"));
    }

    @Test
    void roundTripStereo() {
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Smarts.parse(mol, "O1.[S@]=1(C)CC");
        assertThat(Smarts.generate(mol), is("O=[S@@](C)CC"));
    }

    @Test
    void ringClosures() {
        String sma = "C12=C3C4=C5C1=C1C6=C7C2=C2C8=C3C3=C9C4=C4C%10=C5C5=C1C1=C6C6=C%11C7=C2C2=C7C8=C3C3=C8C9=C4C4=C9C%10=C5C5=C1C1=C6C6=C%11C2=C2C7=C3C3=C8C4=C4C9=C5C1=C1C6=C2C3=C41";
        QueryAtomContainer mol = new QueryAtomContainer(null);
        Smarts.parse(mol, sma);
        assertThat(Smarts.generate(mol), is("C12C3C4=C5C=2C2=C6C7=C1C1=C8C=3C3=C9C4=C4C%10=C5C5=C2C2=C6C6=C%11C7=C1C1=C7C8=C3C3=C8C9=C4C4=C9C%10=C5C5=C2C2=C6C6=C%11C1=C1C7=C3C3C8=C4C4=C9C5=C2C2C6=C1C=3C4=2"));
    }
}
