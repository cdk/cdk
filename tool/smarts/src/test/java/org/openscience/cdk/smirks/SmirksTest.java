/*
 * Copyright (C) 2022 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.smirks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.Transform;
import org.openscience.cdk.isomorphism.TransformOp;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smarts.Smarts;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openscience.cdk.isomorphism.TransformOp.Type.AdjustH;
import static org.openscience.cdk.isomorphism.TransformOp.Type.Charge;
import static org.openscience.cdk.isomorphism.TransformOp.Type.Element;
import static org.openscience.cdk.isomorphism.TransformOp.Type.ImplH;
import static org.openscience.cdk.isomorphism.TransformOp.Type.Mass;
import static org.openscience.cdk.isomorphism.TransformOp.Type.TotalH;

class SmirksTest {

    private static final IChemObjectBuilder BUILDER = SilentChemObjectBuilder.getInstance();
    private static final SmilesParser SMIPAR = new SmilesParser(BUILDER);
    private static final SmilesGenerator SMIGEN = new SmilesGenerator(SmiFlavor.Default | SmiFlavor.UseAromaticSymbols);

    static void assertTransform(String smiles, String smirks, String expected, SmirksOption... options) throws Exception {
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.Exclusive, options);
    }

    static void assertTransform(String smiles, String smirks, Transform transform, String expected, SmirksOption... options) throws Exception {
        assertTransform(smiles, smirks, transform, new String[]{expected}, Transform.Mode.Exclusive, options);
    }

    static void assertTransform(String smiles, String smirks, String[] expected, Transform.Mode mode, SmirksOption... options) throws Exception {
        assertTransform(smiles, smirks, new Transform(), expected, mode, options);
    }

    static void assertTransform(String smiles,
                                String smirks,
                                Transform transform,
                                String[] expected,
                                Transform.Mode mode,
                                SmirksOption... options) throws Exception {
        IAtomContainer mol = SMIPAR.parseSmiles(smiles);
        assertTrue(Smirks.parse(transform, smirks, options), transform.message());
        if (transform.message() != null) {
            StackTraceElement entry = findEntryPoint();
            if (entry != null) {
                System.err.println("Warning: [" + entry + "]\n" + transform.message());
            } else {
                System.err.println("Warning: " + transform.message());
            }
        }
        Iterable<IAtomContainer> iterable = transform.apply(mol, mode);

        List<String> actualSmiles = new ArrayList<>();
        for (IAtomContainer actual : iterable) {
            actualSmiles.add(SMIGEN.create(actual));
        }

        assertEquals(expected.length,
                     actualSmiles.size(),
                     "The number of expected transforms " + expected.length +
                             " and actual transforms " + actualSmiles.size() + " is different:" +
                             actualSmiles);

        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(
                    expected[i],
                    actualSmiles.get(i),
                    "Applying the transform did not generate the expected molecule");
        }
    }

    private static StackTraceElement findEntryPoint() {
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        StackTraceElement entry = null;
        for (StackTraceElement e : elems) {
            if (e.getMethodName().equals("getStackTrace") ||
                    e.getMethodName().equals("assertTransform") ||
                    e.getMethodName().equals("findEntryPoint"))
                continue;
            entry = e;
            break;
        }
        return entry;
    }

    static void assertNoMatch(String smiles, String smirks) throws Exception {
        IAtomContainer mol = SMIPAR.parseSmiles(smiles);
        Transform transform = new Transform();
        Smirks.parse(transform, smirks);
        String actual = SMIGEN.create(mol);
        assertFalse(transform.apply(mol), "Transform should not apply but did, result=" + actual);
    }

    static void assertAtomTypeOps(String lft, String rgt, TransformOp... expected) {
        IAtomContainer qlft = new QueryAtomContainer(null);
        IAtomContainer qrgt = new QueryAtomContainer(null);
        assertTrue(Smarts.parse(qlft, lft), "Invalid SMARTS " + lft);
        assertTrue(Smarts.parse(qrgt, rgt), "Invalid SMARTS " + rgt);
        List<TransformOp> actual = Smirks.atomTypeOps(qlft.getAtom(0), qrgt.getAtom(0));
        Collections.sort(actual);
        Arrays.sort(expected);
        assertArrayEquals(expected, actual.toArray());
    }

    private void assertWarningMesg(String smirks, String expected) {
        Transform transform = new Transform();
        Smirks.parse(transform, smirks, SmirksOption.PEDANTIC);
        assertThat(transform.message(), startsWith(expected));
        // System.err.println("Warning: " + transform.message());
    }

    private void assertNoWarningMesg(String smirks) {
        Transform transform = new Transform();
        Smirks.parse(transform, smirks, SmirksOption.PEDANTIC);
        assertNull(transform.message());
        // System.err.println("Warning: " + transform.message());
    }

    @Test
    void testNullArgument_Transform() {
        assertThrows(NullPointerException.class, () -> Smirks.parse(null, "[C:3]>>[C:3][O:3]"), "NullPointerException expected when passing null as an argument for transform.");
    }

    @Test
    void testNullArgument_Smirks() {
        Transform transform = new Transform();
        assertThrows(NullPointerException.class, () -> Smirks.parse(transform, null), "NullPointerException expected when passing null as an argument for smirks.");
    }

    @Test
    void testDuplicateMapIdx_Product() {
        Transform transform = new Transform();
        assertFalse(Smirks.parse(transform, "[C:3]>>[C:3][O:3]"));
        assertEquals("Duplicate atom map [C:3] and [O:3]", transform.message());
    }

    @Test
    void testDuplicateMapIdx_Reactant() {
        Transform transform = new Transform();
        assertFalse(Smirks.parse(transform, "[C:4][O:4]>>[C:4]"));
        assertEquals("Duplicate atom map [C:4] and [O:4]", transform.message());
    }

    @Test
    void testNegativeMapIdx() {
        final String smirks = "[*:1][N:2](=[O:3])=[O:-4]>>[*:1][N+:2](=[O:3])-[OH:-4]";
        SmirksTransform transform = new SmirksTransform();
        transform.setPrepare(false);
        assertFalse(Smirks.parse(transform, smirks), transform.message());
        assertEquals("Invalid atom expression: map idx should >0", transform.message());
    }

    // JWM: May be better to make it a warning but this is hard because the
    // SMARTS parser doesn't currently have a warning mechanism
    @Test
    void testZeroMapIdx() {
        final String smirks = "[*:1][N:0](=[O:3])=[O:4]>>[*:1][N+:0](=[O:3])-[OH:4]";
        SmirksTransform transform = new SmirksTransform();
        transform.setPrepare(false);
        assertFalse(Smirks.parse(transform, smirks));
        assertEquals("Invalid atom expression: map idx should >0", transform.message());
    }

    // JWM: perhaps a warning on created expressions e.g. H2,H3. X3 is find as can be reversed
    @Test
    void testProductWithUnconsideredAtomExpressions() throws Exception {
        final String smirks = "[C:1][H]>>[CX3:1]O[H]";
        assertTransform("C",
                        smirks,
                        "CO");
    }

    @Test
    void warnOnUnpairedAtomMap() {
        assertWarningMesg("[C:3]>>[C:3][O:4]", "Added/removed atoms do not need to be mapped");
    }

    @Test
    void warnOnWildCardBond() {
        Transform transform = new Transform();
        assertTrue(Smirks.parse(transform, ">>c1ccccc~1"));
        assertThat(transform.message(),
                   containsString("Cannot determine bond order for newly created bond (presumed aromatic single due to attached atoms)\n" +
                                          ">>c1ccccc~1\n" +
                                          "         ^\n"));
        assertTrue(Smirks.parse(transform, ">>c~1ccccc1"));
        assertThat(transform.message(),
                   containsString("Cannot determine bond order for newly created bond (presumed aromatic single due to attached atoms)\n" +
                                          ">>c~1ccccc1\n" +
                                          "   ^\n"));
    }

    @Test
    void atomTypeHChanges_1() {
        assertAtomTypeOps("[CH1]", "[C]"); // no-op
        assertAtomTypeOps("[CH1]", "[CH0]", new TransformOp(AdjustH, 0, -1));
        assertAtomTypeOps("[CH1]", "[CH1]"); // no-op
        assertAtomTypeOps("[CH1]", "[CH2]", new TransformOp(AdjustH, 0, 1));
    }

    @Test
    void atomTypeImplHChanges_1() {
        assertAtomTypeOps("[CH1]", "[C]"); // no-op
        assertAtomTypeOps("[CH1]", "[Ch0]", new TransformOp(ImplH, 0, 0));
        assertAtomTypeOps("[Ch1]", "[Ch1]"); // no-op
        assertAtomTypeOps("[CH1]", "[Ch1]", new TransformOp(ImplH, 0, 1));
        assertAtomTypeOps("[CH1]", "[Ch2]", new TransformOp(ImplH, 0, 2));
        assertAtomTypeOps("[CH1]", "[C;h2,H3]");
        assertAtomTypeOps("[CH1]", "[C;h3H3]", new TransformOp(AdjustH, 0, 2), new TransformOp(ImplH, 0, 3));
        assertAtomTypeOps("[CH1]", "[C;h3H4]", new TransformOp(ImplH, 0, 3), new TransformOp(AdjustH, 0, 3));
        assertAtomTypeOps("[CH1]", "[C;h3,H3]"); // conflicting
    }

    @Test
    void atomTypeHChanges_2() {
        assertAtomTypeOps("[C;H0,H1]", "[C]"); // no-op
        assertAtomTypeOps("[C;H0,H1]", "[CH0]", new TransformOp(TotalH, 0, 0));
        assertAtomTypeOps("[C;H0,H1]", "[CH1]", new TransformOp(TotalH, 0, 1));
        assertAtomTypeOps("[C;H0,H1]", "[CH2]", new TransformOp(TotalH, 0, 2));
    }

    @Test
    void atomTypeHChanges_3() {
        assertAtomTypeOps("[C;H0,H1]", "[C;H0,H1]"); // no-op
        assertAtomTypeOps("[C;H0,H1]", "[C,H0]"); // conflicting
        assertAtomTypeOps("[C;H0,H1]", "[C;H0]", new TransformOp(TotalH, 0, 0));
        assertAtomTypeOps("[C;H0,H1]", "[C;H1]", new TransformOp(TotalH, 0, 1));
        assertAtomTypeOps("[C;H0,H1]", "[C;H2]", new TransformOp(TotalH, 0, 2));
    }

    @Test
    void atomTypeMassChange() {
        assertAtomTypeOps("[12C]", "[12C]");
        assertAtomTypeOps("[13C]", "[13C]");
        assertAtomTypeOps("[12,13;C]", "[12,13;C]");
        assertAtomTypeOps("[12C]", "[C]");
        assertAtomTypeOps("[C]", "[12C]", new TransformOp(Mass, 0, 12));
        assertAtomTypeOps("[C]", "[13C]", new TransformOp(Mass, 0, 13));
        assertAtomTypeOps("[12,13;C]", "[13;C]", new TransformOp(Mass, 0, 13));
        assertAtomTypeOps("[12C]", "[12,13;C]");
        assertAtomTypeOps("[12C]", "[0C]", new TransformOp(Mass, 0, 0));
    }

    @Test
    void atomTypeChargeChange() {
        assertAtomTypeOps("[C+]", "[C+]");
        assertAtomTypeOps("[C+]", "[C]");
        assertAtomTypeOps("[C+]", "[C+0]", new TransformOp(Charge, 0, 0));
        assertAtomTypeOps("[C+]", "[C+1]");
        assertAtomTypeOps("[C+]", "[C+2]", new TransformOp(Charge, 0, 2));
        assertAtomTypeOps("[C]", "[C+]", new TransformOp(Charge, 0, 1));
        assertAtomTypeOps("[C]", "[C+1]", new TransformOp(Charge, 0, 1));
    }

    @Test
    void atomTypeElemChange() {
        assertAtomTypeOps("[C]", "[*]");
        assertAtomTypeOps("[C]", "[C]");
        assertAtomTypeOps("[C]", "[C,N]");
        assertAtomTypeOps("[C]", "[N]", new TransformOp(Element, 0, 7));
        assertAtomTypeOps("[C,N]", "[C]", new TransformOp(Element, 0, 6));
        assertAtomTypeOps("[C,N]", "[N]", new TransformOp(Element, 0, 7));
        assertAtomTypeOps("[C,N]", "[#7]", new TransformOp(Element, 0, 7));
        assertAtomTypeOps("[C,N]", "C", new TransformOp(Element, 0, 6));
        // assertAtomTypeOps("[C,N]", "c", new OpCode(Element, 0, 6, 0, 1));
    }

    // https://www.daylight.com/dayhtml/doc/theory/theory.smirks.html
    @Test
    void testNitroNorm_1() throws Exception {
        assertTransform("c1ccccc1N(=O)=O",
                        "[*:1][N:2](=[O:3])=[O:4]>>[*:1][N+:2](=[O:3])[O-:4]",
                        "c1ccccc1[N+](=O)[O-]");
    }

    @Test
    void shouldReverseNitroNorm() throws Exception {
        assertTransform("c1ccccc1[N+]([O-])=O",
                        "[*:1][N+0:2](=[O:3])=[O+0:4]>>[*:1][N+:2](=[O:3])[O-:4]",
                        "c1ccccc1N(=O)=O",
                        SmirksOption.REVERSE);
    }

    @Test
    void testNitroNorm_2() throws Exception {
        assertTransform("c1ccccc1[N]([O-])=O",
                        "[N:1](-[O-:2])=[O:3]>>[N:1](=[O+0:2])=[O:3]",
                        "c1ccccc1N(=O)=O");
    }

    // The C here is [CH2][CH3]
    @Test
    void testImplicitValence() throws Exception {
        assertTransform("c1ccccc1C(=O)O",
                        "[OD1:1][H]>>[O:1]CC",
                        "c1ccccc1C(=O)OCC");
    }

    @Test
    void testImplicitValence_ExplH() throws Exception {
        assertTransform("c1ccccc1C(=O)O",
                        "[OD1:1][H]>>[OD1:1]C([H])([H])C([H])([H])[H]",
                        "c1ccccc1C(=O)OCC");
    }

    @Test
    void testImplicitValence_VirtH() throws Exception {
        assertTransform("c1ccccc1C(=O)O",
                        "[OD1:1][H]>>[OD1:1][CH2][CH3]",
                        "c1ccccc1C(=O)OCC");
    }

    // explicit hydrogens in SMIRKS, but we remove implicit hydrogens
    @Test
    void testHydrogenAdjustment_1() throws Exception {
        assertTransform("c1ccccc1N(=O)=O",
                        "[c:1][H]>>[*:1]Cl",
                        "c1(c(c(c(c(c1N(=O)=O)Cl)Cl)Cl)Cl)Cl");
    }

    @Test
    void testImplicitValenceAromatic() throws Exception {
        assertTransform("c1ccccc1C(=O)O",
                        "[OD1:1][H]>>[O:1]Cc1ccccc1",
                        "c1ccccc1C(=O)OCc2ccccc2");
    }

    // explicit hydrogens in SMIRKS, but we remove (one) implicit hydrogens
    @Test
    void testHydrogenAdjustment_2() throws Exception {
        assertTransform("C1CCCCC1N(=O)=O",
                        "[C:1][H]>>[*:1]Cl",
                        "C1(C(C(C(C(C1(N(=O)=O)Cl)Cl)Cl)Cl)Cl)Cl");
    }

    // explicit hydrogens in SMIRKS, but we add implicit hydrogens
    @Test
    void testHydrogenAdjustment_3() throws Exception {
        assertTransform("c1(c(c(c(c(c1N(=O)=O)Cl)Cl)Cl)Cl)Cl",
                        "[c:1][Cl]>>[*:1][H]",
                        "c1ccccc1N(=O)=O");
    }

    // remove explicit hydrogens
    @Test
    void testHydrogenAdjustment_4() throws Exception {
        assertTransform("[H]c1c([H])c([H])c([H])c([H])c1N(=O)=O",
                        "[c:1][H]>>[*:1]Cl",
                        "Clc1c(Cl)c(Cl)c(Cl)c(Cl)c1N(=O)=O");
    }

    // each carbon has two explicit atoms, only one should be removed
    @Test
    void testHydrogenAdjustment_5() throws Exception {
        assertTransform("[H]C1([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])C([H])1N(=O)=O",
                        "[C:1][H]>>[*:1]Cl",
                        "ClC1([H])C(Cl)([H])C(Cl)([H])C(Cl)([H])C(Cl)([H])C1(Cl)N(=O)=O");
    }

    // the deuterium or other H should be removed
    @Test
    void testHydrogenAdjustment_6() throws Exception {
        assertTransform("[2H]c1c([H])c([H])c([H])c([H])c1N(=O)=O",
                        "[c:1][H]>>[*:1]Cl",
                        "Clc1c(Cl)c(Cl)c(Cl)c(Cl)c1N(=O)=O");
    }

    // only the deuterium should be removed
    @Test
    void testHydrogenAdjustment_7() throws Exception {
        assertTransform("[2H]c1c([H])c([H])c([H])c([H])c1N(=O)=O",
                        "[c:1][2#1]>>[*:1]Cl",
                        "Clc1c([H])c([H])c([H])c([H])c1N(=O)=O");
    }

    // https://www.daylight.com/dayhtml/doc/theory/theory.smirks.html
    // but atom map 0 => 5, 0 is undef, think this is a bug in Daylight doc
    // the implicit hydrogens are moved as needed
    @Test
    void testHydrogenMovement_1() throws Exception {
        assertTransform("c1ccccc1C(=O)Cl.NCCC",
                        "[C:1](=[O:2])[Cl:3].[H:99][N:4]([H:100])[C:5]>>[C:1](=[O:2])[N:4]([H:100])[C:5].[Cl:3][H:99]",
                        "c1ccccc1C(=O)NCCC.Cl");
    }

    // the deuterium is moved from one atom to another
    @Test
    void testHydrogenMovement_2() throws Exception {
        assertTransform("c1ccccc1C(=O)Cl.[2H]N([H])CCC",
                        "[C:1](=[O:2])[Cl:3].[H:99][N:4]([H:100])[C:5]>>[C:1](=[O:2])[N:4]([H:100])[C:5].[Cl:3][H:99]",
                        "c1ccccc1C(=O)N([H])CCC.Cl[2H]");
    }

    // it is possible to move the implicit hydrogen and keep the 2H in place
    @Test
    void testHydrogenMovement_3() throws Exception {
        assertTransform("c1ccccc1C(=O)Cl.[2H]NCCC",
                        "[C:1](=[O:2])[Cl:3].[H:99][N:4]([H:100])[C:5]>>[C:1](=[O:2])[N:4]([H:100])[C:5].[Cl:3][H:99]",
                        "c1ccccc1C(=O)N([2H])CCC.Cl");
    }

    // if we want we can force the implicit Hydrogen to move even if there is an implicit hydrogen
    @Test
    void testHydrogenMovement_4() throws Exception {
        assertTransform("c1ccccc1C(=O)Cl.[2H]NCCC",
                        "[C:1](=[O:2])[Cl:3].[2#1:99][N:4]([H:100])[C:5]>>[C:1](=[O:2])[N:4]([H:100])[C:5].[Cl:3][H:99]",
                        "c1ccccc1C(=O)NCCC.Cl[2H]");
    }

    // hydrogen moves to a new atom (made up) but make sure ops are done in
    // correct order
    @Test
    void testHydrogenMovement_5() throws Exception {
        assertTransform("Cl[2H]",
                        "[Cl:1][H:2]>>[Cl:1].[H:2]Br",
                        "[Cl].[2H]Br");
    }

    // hydrogens swap
    @Test
    void testHydrogenMovement_6() throws Exception {
        assertTransform("Cl[2H].Br[3H]",
                        "[Cl:1][H:2].[Br:3][H:4]>>[Cl:1][H:4].[Br:3][H:2]",
                        "Cl[3H].[2H]Br");
    }

    // hydrogens swap (implicit)
    @Test
    void testHydrogenMovement_7() throws Exception {
        assertTransform("Cl.Br",
                        "[Cl:1][H:2].[Br:3][H:4]>>[Cl:1][H:4].[Br:3][H:2]",
                        "Cl.Br");
    }

    // @Ignore("when is the XOR filter applied? after match or after changes are successful")
    @Test
    void testMultiEdges() throws Exception {
        assertNoMatch(
                "CC",
                "[*:1].[*:2]>>[*:1]-[*:2]");
//        assertTransform("C1CCCCC1",
//                        "[*:1].[*:2]>>[*:1]-[*:2]",
//                        "[CH2]12[CH2]3[CH2]2[CH2]3CC1");
    }

    @Test
    void testBreakBond() throws Exception {
        assertTransform("C=CC=CC=C",
                        "[CH:1]=[CH:2]>>[CH3:1].[CH3:2]",
                        "C=CC.CC=C");
        assertTransform("C=CC=CC=C",
                        "[C:1]=[C:2]>>[C:1]([H])[H].[C:2]([H])[H]",
                        "C.CC.CC.C");
    }

    // removing one and then adding an atom from a tetrahedral it is desirable
    // to keep the configuration intact
    @Test
    void testKeepStereo_1() throws Exception {
        assertTransform("C[C@](CC)(N)Cl",
                        "[C:1]Cl>>[C:1]Br",
                        "C[C@](CC)(N)Br");
    }

    // removing one and then adding an atom from a tetrahedral it is desirable
    // to keep the configuration intact... even if there is an implicit hydrogen
    @Test
    void testKeepStereo_2() throws Exception {
        assertTransform("C[C@H](CC)Cl",
                        "[C:1]Cl>>[C:1]Br",
                        "C[C@H](CC)Br");
    }

    @Test
    void testKeepStereo_3() throws Exception {
        assertTransform("C/C=C(/C)Cl",
                        "[C:1]Cl>>[C:1]Br",
                        "C/C=C(/C)\\Br");
    }

    @Test
    void testKeepStereo_4() throws Exception {
        assertTransform("C/C=C(C)\\Cl",
                        "[C:1]Cl>>[C:1]Br",
                        "C/C=C(/C)\\Br");
    }

    @Test
    void testKeepStereo_5() throws Exception {
        assertTransform("O=[S@](C)CC",
                        "[S:1]C>>[S:1]Cl",
                        "O=[S@](Cl)CC");
    }

    @Test
    void testKeepStereo_6() throws Exception {
        assertTransform("C[C@H](CC)Cl",
                        "[CH1:1][H]>>[C:1]Br",
                        "C[C@](CC)(Cl)Br");
    }

    @Test
    void testKeepStereo_7() throws Exception {
        assertTransform("CC=[C@]=CCl",
                        "[C:1]Cl>>[C:1]Br",
                        "CC=[C@]=CBr");
    }

    @Test
    void testKeepStereo_8() throws Exception {
        assertTransform("Cl/C=C=C=C/Cl",
                        "[C:1]Cl>>[C:1]Br",
                        "Br/C=C=C=C/Br");
    }

    @Test
    void testKeepStereo_Split1() throws Exception {
        assertTransform("C/C=C/C=C/C",
                        "[CD2H:1]-[CD2H:2]>>[C:1]O.[C:2]O",
                        "C/C=C/O.C(=C/C)\\O");
        assertTransform("C/C=C/C=C\\C",
                        "[CD2H:1]-[CD2H:2]>>[C:1]O.[C:2]O",
                        "C/C=C/O.C(=C\\C)\\O");
        assertTransform("C/C=C/C=C\\C",
                        "[CD2H:1]-[CD2H:2]>>[CH2:1].[CH2:2]",
                        "CC=C.C=CC");
        assertTransform("C/C=C/C=C\\C",
                        "[CD2H:1]-[CD2H:2]>>[C:1][H].[C:2][H]",
                        "CC=C.C=CC");
        assertTransform("C/C=C/C=C\\C",
                        "[CD2H:1]-[CD2H:2]>>[C:1][2H].[C:2][2H]",
                        "C/C=C/[2H].C(=C\\C)\\[2H]");
    }

    @Test
    void testKeepStereo_Split2() throws Exception {
        assertTransform("Br[C@H](Cl)[C@H](Cl)Br",
                        "[CD3H:1]-[CD3H:2]>>[C:1]O.[C:2]O",
                        "Br[C@H](Cl)O.[C@@H](Cl)(Br)O");
    }

    // swapping two atoms we should lose stereochemistry because no information
    // has been given about the order in which these attach
    @Test
    void testRemoveStereo_1() throws Exception {
        assertTransform("C[C@](CC)(N)Cl",
                        "[C:1](N)Cl>>[C:1]([H])Br",
                        "CC(CC)Br");
    }

    @Test
    void testRemoveStereo_2() throws Exception {
        assertTransform("C/C=C(C)\\Cl",
                        "[C:1](C)Cl>>[C:1](N)Br",
                        "CC=C(N)Br");
    }

    @Test
    void testRemoveStereo_3() throws Exception {
        assertTransform("C/C=C(C)\\Cl",
                        "[CH0:1](C)Cl>>[CH2:1]",
                        "CC=C");
    }

    @Test
    void testRemoveStereo_4() throws Exception {
        assertTransform("O=[S@](C)CC",
                        "[S:1](=[O:2])C>>[S:1]([OH:2])([H])Cl",
                        "OS(Cl)CC");
    }

    @Test
    void testRemoveStereo_5() throws Exception {
        assertTransform("O[C@H](C)CC",
                        "[H][CH:1][O:2]>>[C:1]=[OH0:2]",
                        "O=C(C)CC");
    }

    @Test
    void testRemoveStereo_6() throws Exception {
        assertTransform("O[C@H](C)CC",
                        "[CH:1][OH:2]>>[CH0:1]=[OH0:2]",
                        "O=C(C)CC");
    }

    @Test
    void testRemoveStereo_7() throws Exception {
        assertTransform("C/C=C/C",
                        "[CH:1]=[CH:2]>>[CH2:1]-[CH2:2]",
                        "CCCC");
    }

    @Test
    void testRemoveStereo_8() throws Exception {
        assertTransform("C/C=C/O",
                        "[OH:1]-[CH:2]>>[OH0:1]=[CH0:2]",
                        "CC=C=O");
        // but stereo kept here
        assertTransform("C/C=C/O",
                        "[OH:1]-[CH:2]>>C[OH0:1]-[CH:2]",
                        "C/C=C/OC");
    }

    @Test
    void testAtomReuseOptimisation_1() throws Exception {
        assertTransform("COC",
                        "[C:1]O>>[C:1]N",
                        "CN.[CH3]");
    }

    @Test
    void testAtomReuseOptimisation_2() throws Exception {
        assertTransform("COC",
                        "[CH3:1]O>>[CH2:1]=N",
                        "C=N.[CH3]");
    }

    // make sure with the replace H optimisation the bonds to other
    // atoms get broken
    @Test
    void testReplaceH_1() throws Exception {
        assertTransform("[C][H][C]",
                        "[C:1][#1]>>[*:1]Cl",
                        "[C]Cl.[C]");
    }

    // make sure with the replace H optimisation the aromaticity gets set
    // correctly
    @Test
    void testReplaceH_2() throws Exception {
        assertWarningMesg("[CH1:1][H]>>[*:1]c1ccccc1",
                          "Cannot determine bond order for newly created bond");
        // * is both aliphatic and aromatic! so we can't know until we run
        // the pattern if the new bond is aromatic or aliphatic, it must be
        // knowable from the pattern
        assertTransform("C[C@H](O)CC",
                        "[CH1:1][H]>>[*:1]-c1ccccc1",
                        "C[C@](O)(CC)c1ccccc1");
    }

    @Test
    void testReplaceH_3() throws Exception {
        assertTransform("[C][H][C].[BH][H][B]",
                        "[H][C,B:1][#1:2]>>[*:1]([H:2])Cl",
                        "[C][H][C].[B]([H][B])Cl");
    }

    @Test
    void testInvertStereo_1() throws Exception {
        assertTransform("C[C@](Br)(Cl)N",
                        "[C:1][C@:2]([Br:3])([Cl:4])[N:5]>>[C:1][C@@:2]([Br:3])([Cl:4])[N:5]",
                        "C[C@@](Br)(Cl)N");
    }

    @Test
    void testInvertStereo_2() throws Exception {
        assertTransform("C[C@@](Br)(Cl)N",
                        "[C:1][C@@:2]([Br:3])([Cl:4])[N:5]>>[C:1][C@:2]([Br:3])([Cl:4])[N:5]",
                        "C[C@](Br)(Cl)N");
    }

    @Test
    void testInvertStereo_3() throws Exception {
        assertTransform("C[C@@](Br)(Cl)N",
                        "[C:1][C@@:2]([Br:3])([Cl:4])[N:5]>>[C:1][C@@:2]([Cl:4])([Br:3])[N:5]",
                        "C[C@](Br)(Cl)N");
    }

    @Test
    void testInvertStereo_4() throws Exception {
        assertTransform("C[C@@](Br)(Cl)N",
                        "[*:1][*@@:2]([*:3])([*:4])[*:5]>>[*:1]~[*@:2](~[*:3])(~[*:4])~[*:5]",
                        "C[C@](Br)(Cl)N");
    }

    @Test
    void testSetStereo_1() throws Exception {
        assertTransform("CC=CC",
                        "[*:1]-[*:2]=[*:3]-[*:4]>>[*:1]/[*:2]=[*:3]/[*:4]",
                        "C/C=C/C");
        // need to add new Stereo after sorting out old stereo
        assertTransform("CC=CC",
                        "[*:1][*:2]=[*:3][*:4]>>[*:1]/-[*:2]=[*:3]/-[*:4]",
                        "C/C=C/C");
        assertTransform("CC=CC",
                        "[*:1][*:2]=[*:3][*:4]>>[*:1]/[*:2]=[*:3]/[*:4]",
                        "C/C=C/C");
        assertTransform("CC=CC",
                        "[*:1][*:2]=[*:3][*:4]>>[*:1]/[*:2]=[*:3]/[*:4]",
                        new String[]{"C/C=C/C"},
                        Transform.Mode.Unique);
        assertTransform("CC=CC",
                        "[*:1][*:2]=[*:3][*:4]>>[*:1]/[*:2]=[*:3]/[*:4]",
                        new String[]{"C/C=C/C", "C/C=C/C"},
                        Transform.Mode.All);
    }

    @Test
    void testSetStereo_2() throws Exception {
        assertTransform("CC=CC",
                        "[*:1][*:2]=[*:3][*:4]>>[*:1]/[*:2]=[*:3]\\[*:4]",
                        "C/C=C\\C");
    }

    @Test
    void testMolecularHydrogen() throws Exception {
        assertTransform("[H][H].c1ccccc1",
                        "[H][H:1]>>[H+:1]",
                        "[H+].c1ccccc1");
    }


    @Test
    void testDeepCopy() throws Exception {
        assertTransform("CCOCCF", "[O:1][C:2][C:3]>>[OH1:1].[CH3:2][C:3]",
                        new String[]{"CC.OCCF", "CCO.CCF"},
                        Transform.Mode.All);
    }

    // To Test:
    // [H:1]C>>C[*:1]

    @Test
    void testBondOrderingSwapping_1() throws Exception {
        assertTransform("[Li+:30].[Al+3:32].[O:28]=[C:27]1[O:31][C:2](=[O:29])[CH:3]2[CH2:26][C:17]" +
                                "(=[C:8]([C:9]3=[CH:10][CH:11]=[C:12]([O:13][CH3:14])[CH:15]=[CH:16]3)[CH2:7][CH:4]12)[C:18]=4" +
                                "[CH:19]=[CH:20][C:21]([O:22][CH3:23])=[CH:24][CH:25]4",
                        "[Al+3;h0X0].[C+0;h1:3]1[C+0;h1:4][C+0;h0X3v4:27](=[O+0;h0:28])[O+0;h0X2v2][C+0;h0X3v4:2]1=[O+0;h0:29].[Li+;h0X0]>>[CH2+0:2]([OH1+0:29])[CH1+0:3][CH1+0:4][CH2+0:27][O+0;h1:28]",
                        "OCC1CC(=C(CC1CO)C=2C=CC(OC)=CC2)C3=CC=C(OC)C=C3");
    }

    @Test
    void testBondOrderingSwapping_2() throws Exception {
        assertTransform("CCC(C)O",
                        "[H][C:1]-[O:2][H]>>[O:2]=[C:1]",
                        "CCC(C)=O");
    }

    @Test
    void testReaction_1() throws Exception {
        assertTransform("[O:1]=[C:2]([OH:17])[C:8]=1[NH:16][C:15]=2[CH:14]=[CH:13][CH:12]=[CH:11][C:10]2[CH:9]1.[OH:7][CH2:6][CH2:5][CH2:4][NH2:3]",
                        "[CH0D3v4:2][OH1D1v2].[NH2D1v3:3]>>[CH0:2][NH1:3]",
                        "O=C(C=1NC=2C=CC=CC2C1)NCCCO");
    }

    @Test
    void testReaction_2() throws Exception {
        assertTransform("[O:1]=[C:2]([OH:17])[C:8]=1[NH:16][C:15]=2[CH:14]=[CH:13][CH:12]=[CH:11][C:10]2[CH:9]1.[OH:7][CH2:6][CH2:5][CH2:4][NH2:3]",
                        "[N+0;h2D1v3:3][C+0;h2:4].[O+0;h0:1]=[C+0;h0D3v4:2]([O+0;h1D1v2])[C+0;h0:8]>>[OH0:1]=[CH0:2]([NH1:3][CH2:4])[CH0:8]",
                        "O=C(C=1NC=2C=CC=CC2C1)NCCCO");
    }

    // uses atom properties h, D and v; should the usage of these yield an exception or a warning message?
    @Test
    void testReaction_3() throws Exception {
        assertTransform("[O:21]=[C:20]([NH:29][NH2:28])[C:22]1=[CH:23][CH:24]=[CH:25][N:26]=[CH:27]1." +
                                "[OH:19][CH2:18][CH2:17][CH2:16][CH2:15][CH2:14][CH2:13][CH2:12][CH2:11][CH2:10][CH2:9][CH2:8][CH2:7][CH2:6][CH2:5][CH2:4][CH:2]([CH3:1])[CH3:3]",
                        "[N+0;h2D1v3][N+0;h1D2v3][C+0;h0D3v4:20].[O+0;h1D1v2:19]>>[O+0;h0D2v2:19][C+0;h0D3v4:20]",
                        "O=C(C1=CC=CN=C1)OCCCCCCCCCCCCCCCC(C)C");
    }

    @Test
    void testReaction_4() throws Exception {
        assertTransform("[O:21]=[C:20]([NH:29][NH2:28])[C:22]1=[CH:23][CH:24]=[CH:25][N:26]=[CH:27]1." +
                                "[OH:19][CH2:18][CH2:17][CH2:16][CH2:15][CH2:14][CH2:13][CH2:12][CH2:11][CH2:10][CH2:9][CH2:8][CH2:7][CH2:6][CH2:5][CH2:4][CH:2]([CH3:1])[CH3:3]",
                        "[N+0;h2D1v3][N+0;h1D2v3][C+0;h0D3v4:20].[O+0;h1D1v2:19]>>[OH0:19][C:20]",
                        "O=C(C1=CC=CN=C1)OCCCCCCCCCCCCCCCC(C)C");
    }

    @Test
    void testReaction_5_Transform() throws Exception {
        final String smiles = "[O:21]=[C:20]([NH:29][NH2:28])[C:22]1=[CH:23][CH:24]=[CH:25][N:26]=[CH:27]1." +
                "[OH:19][CH2:18][CH2:17][CH2:16][CH2:15][CH2:14][CH2:13][CH2:12][CH2:11][CH2:10][CH2:9][CH2:8][CH2:7][CH2:6][CH2:5][CH2:4][CH:2]([CH3:1])[CH3:3]";
        final String smirks = "[C+0;h2:18][O+0;h1D1v2:19].[N+0;h2D1v3:28][N+0;h1D2v3:29][C+0;h0D3v4:20](=[O+0;h0:21])[c+0;h0:22]>>[CH2:18][OH0:19][CH0:20](=[OH0:21])[cH0:22]";

        // does not match and thus does not modify the molecule because there are aromatic atoms in the smirks
        // successfully matching the smiles would require aromaticity perception to be carried out on the smiles
        assertNoMatch(smiles, smirks);
    }

    @Test
    void testReaction_5_SmirksTransform_setPrepareDefaultsToTrue() throws Exception {
        final String smiles = "[O:21]=[C:20]([NH:29][NH2:28])[C:22]1=[CH:23][CH:24]=[CH:25][N:26]=[CH:27]1." +
                "[OH:19][CH2:18][CH2:17][CH2:16][CH2:15][CH2:14][CH2:13][CH2:12][CH2:11][CH2:10][CH2:9][CH2:8][CH2:7][CH2:6][CH2:5][CH2:4][CH:2]([CH3:1])[CH3:3]";
        final String smirks = "[C+0;h2:18][O+0;h1D1v2:19].[N+0;h2D1v3][N+0;h1D2v3][C+0;h0D3v4:20](=[O+0;h0:21])[c+0;h0:22]>>[CH2:18][OH0:19][CH0:20](=[OH0:21])[cH0:22]";
        final String expected = "O=C(c1cccnc1)OCCCCCCCCCCCCCCCC(C)C";

        assertTransform(smiles, smirks, new SmirksTransform(), expected);
    }

    @Test
    void testReaction_5_SmirksTransform_setPrepareFalse() throws Exception {
        final String smiles = "[O:21]=[C:20]([NH:29][NH2:28])[C:22]1=[CH:23][CH:24]=[CH:25][N:26]=[CH:27]1." +
                "[OH:19][CH2:18][CH2:17][CH2:16][CH2:15][CH2:14][CH2:13][CH2:12][CH2:11][CH2:10][CH2:9][CH2:8][CH2:7][CH2:6][CH2:5][CH2:4][CH:2]([CH3:1])[CH3:3]";
        final String smirks = "[C+0;h2:18][O+0;h1D1v2:19].[N+0;h2D1v3:28][N+0;h1D2v3:29][C+0;h0D3v4:20](=[O+0;h0:21])[c+0;h0:22]>>[CH2:18][OH0:19][CH0:20](=[OH0:21])[cH0:22]";

        IAtomContainer atomContainer = SMIPAR.parseSmiles(smiles);
        SmirksTransform transform = new SmirksTransform();
        transform.setPrepare(false);
        assertTrue(Smirks.parse(transform, smirks), transform.message());

        // does not match and thus does not modify the molecule because there are aromatic atoms in the smirks
        // successfully matching the smiles would require aromaticity perception to be carried out on the smiles
        assertFalse(transform.apply(atomContainer));
    }

    @Test
    void testReaction_6() throws Exception {
        assertTransform("[O:17]=[C:6]1[CH2:10][CH2:9][CH2:8]1.[F:15][C:12]([F:14])([F:13])[c:11]1[cH:16][c:2]([Br:1])[cH:3][cH:4][c:5]1[I:18]",
                        "[C+0;h0D3v4:6](=[O+0;h0:17])([C+0;h2:8])[C+0;h2:10].[c+0;h1:4]:[c+0;h0D3v4:5]([I+0;h0D1v1]):[c+0;h0:11]" +
                                ">>[cH1:4]:[cH0:5]([CH0:6]([OH1:17])([CH2:8])[CH2:10]):[cH0:11]",
                        "OC1(CCC1)c2ccc(cc2C(F)(F)F)Br");
    }

    @Test
    void invalidSmirks_1() {
        final String smirks = "[*:1][N:2](=[O:3])=[O:4]";
        SmirksTransform transform = new SmirksTransform();
        transform.setPrepare(false);
        assertFalse(Smirks.parse(transform, smirks), transform.message());
        assertEquals("SMIRKS was not a reaction!", transform.message());
    }

    @Test
    void testTwoTerminalNitroGroups() throws Exception {
        final String smiles = "N(=O)(=O)CCN(=O)=O";
        final String smirks = "[*:1][N:2](=[O:3])=[O:4]>>[*:1][N+:2](=[O:3])[O-:4]";
        assertTransform(smiles, smirks, "[N+](=O)([O-])CC[N+](=O)[O-]");
        assertTransform(smiles, smirks, new String[]{"[N+](=O)([O-])CCN(=O)=O", "N(=O)(=O)CC[N+](=O)[O-]"},
                        Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[]{
                "[N+](=O)([O-])CCN(=O)=O",
                "[N+]([O-])(=O)CCN(=O)=O",
                "N(=O)(=O)CC[N+](=O)[O-]",
                "N(=O)(=O)CC[N+]([O-])=O"
        }, Transform.Mode.All);
    }

    @Test
    void testKetal() throws Exception {
        final String smiles = "C1CCCCC1=O";
        final String smirks = "[C:1]=O>>[C:1]1OCCO1";
        final String expected = "C1CCCCC12OCCO2";
        assertTransform(smiles, smirks, expected);
    }

    @Test
    void testSwernOxidation() throws Exception {
        final String smiles = "C1CC(O)C(O)CC1";
        final String smirks = "[*:1][C:2]([H:3])([O:4][H:5])[C:6]([H:7])([O:8][H:9])[*:10]>>[*:1][C:2](=[O:4])[C:6](=[O:8])[*:10]";
        final String expected = "C1CC(=O)C(=O)CC1";
        assertTransform(smiles, smirks, expected);
    }

    // @Disabled("throws an IllegalArgumentException in org.openscience.cdk.smirks.Smirks.collectBondPairs with the mapped Hs")
    @Test
    void testSwernOxidationMappedHydrogens() throws Exception {
        final String smiles = "C1CC(O)C(O)CC1";
        final String smirks = "[*:1][C:2]([H:3])([O:4][H:5])[C:6]([H:7])([O:8][H:9])[*:10]>>[*:1][C:2](=[O:4])[C:6](=[O:8])[*:10].[H:3][H:5].[H:7][H:9]";
        final String expected = "C1CC(=O)C(=O)CC1.[H][H].[H][H]";
        assertTransform(smiles, smirks, expected);
    }

    @Test
    void testSwernOxidationMappedHydrogens_ExplH() throws Exception {
        final String smiles = "C1CC(O[2H])C(O[2H])CC1";
        final String smirks = "[*:1][C:2]([H:3])([O:4][H:5])[C:6]([H:7])([O:8][H:9])[*:10]>>[*:1][C:2](=[O:4])[C:6](=[O:8])[*:10].[H:3][H:5].[H:7][H:9]";
        final String expected = "C1CC(=O)C(=O)CC1.[2H][H].[2H][H]";
        assertTransform(smiles, smirks, expected);
    }

    @Test
    void testTwoNonOverlappingMatches() throws Exception {
        final String smiles = "O=CC(C)CCC=O";
        final String smirks = "[C:1]=[O:2]>>[H][C:1][O:2][H]";
        final String[] expectedArray = new String[]{"OCC(C)CCC=O", "O=CC(C)CCCO"};
        assertTransform(smiles, smirks, "OCC(C)CCCO");
        assertTransform(smiles, smirks, expectedArray, Transform.Mode.Unique);
        assertTransform(smiles, smirks, expectedArray, Transform.Mode.All);
    }

    @Test
    void testTwoOverlappingMatches() throws Exception {
        final String smiles = "O=CCC=O";
        final String smirks = "[C:3][C:1]=[O:2]>>[C:3][C:1]([H])[O:2][H]";
        assertTransform(smiles, smirks, "OCCC=O");
        assertTransform(smiles, smirks, new String[]{"OCCC=O", "O=CCCO"}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[]{"OCCC=O", "O=CCCO"}, Transform.Mode.All);
    }

    @Test
    void testDielsAlder() throws Exception {
        final String smiles = "C=CC=C.C=C";
        final String smirks = "[C:1]=[C:2][C:3]=[C:4].[C:5]=[C:6]>>[C:1]1[C:2]=[C:3][C:4][C:5][C:6]1";
        final String expected = "C1C=CCCC1";
        assertTransform(smiles, smirks, expected);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.Unique);
        // there are 4 symmetric matches since there are two paths
        assertTransform(smiles, smirks, new String[]{expected, expected, expected, expected},
                        Transform.Mode.All);
    }

    @Test
    void testAmineNitrogenProtonation_disconnectedGraphs() throws Exception {
        final String smiles = "CC(N)C1CCC(N)CC1.N.CNC(=O)CN";
        final String smirks = "[NH2:2]>>[NH3+:2]";
        final String[] expected = new String[]{"CC([NH3+])C1CCC(N)CC1.N.CNC(=O)CN", "CC(N)C1CCC([NH3+])CC1.N.CNC(=O)CN", "CC(N)C1CCC(N)CC1.N.CNC(=O)C[NH3+]"};
        assertTransform(smiles, smirks, "CC([NH3+])C1CCC([NH3+])CC1.N.CNC(=O)C[NH3+]");
        assertTransform(smiles, smirks, expected, Transform.Mode.Unique);
        assertTransform(smiles, smirks, expected, Transform.Mode.All);
    }

    @Test
    void testAmineNitrogenProtonation_arbitrayAtomMappingClasses() throws Exception {
        final String smiles = "CNC(=O)CN";
        final String smirks = "[*:48][N:42]([H])[H]>>[*:48][N+:42]([H])([H])[H]";
        final String expected = "CNC(=O)C[NH3+]";
        assertTransform(smiles, smirks, "CNC(=O)C[NH3+]");
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.All);
    }

    @Test
    void testAmineNitrogenProtonation_smartsAtomExpressionNotHydrogen() throws Exception {
        final String smiles = "CNC(=O)CN";
        final String smirks = "[CX4:1][NX3:2]([H])[!H:3]>>[*:3][N+:2]([C:1])([H])[H]";
        final String expected = "C[NH2+]C(=O)CN";
        assertTransform(smiles, smirks, expected);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.All);
    }

    @Test
    void testPyridineSymmetry() throws Exception {
        final String smiles = "C1=CC=NC=C1";
        final String smirks = "[C:1][H]>>[C:1]O[H]";
        final String[] expected = new String[]{"C1(=CC=NC=C1)O", "C1=C(C=NC=C1)O", "C1=CC(=NC=C1)O", "C1=CC=NC(=C1)O", "C1=CC=NC=C1O"};
        assertTransform(smiles, smirks, "C1(=C(C(=NC(=C1O)O)O)O)O");
        assertTransform(smiles, smirks, expected, Transform.Mode.Unique);
        assertTransform(smiles, smirks, expected, Transform.Mode.All);
    }

    @Test
    void testAmide_separateComponentsMultipleCombinations() throws Exception {
        final String smiles = "NCC=C.NC1=CC=CC=C1.OC(=O)C1CCCOC1";
        final String smirks = "[O:1]=[C:2][O].[NH2:4]>>[O:1]=[C:2][NH:4]";
        final String[] expected = new String[]{"N(CC=C)C(=O)C1CCCOC1.NC1=CC=CC=C1", "NCC=C.N(C1=CC=CC=C1)C(=O)C2CCCOC2"};
        assertTransform(smiles, smirks, "N(CC=C)C(=O)C1CCCOC1.NC1=CC=CC=C1");
        assertTransform(smiles, smirks, expected, Transform.Mode.Unique);
        assertTransform(smiles, smirks, expected, Transform.Mode.All);
    }

    @Test
    void testAddingCarbon_infiniteLoopPrevention() throws Exception {
        // the transformed molecule also matches the LHS of the smirks, assert that there is no infinite loop of matching
        final String smiles = "CCCCN";
        final String smirks = "[C:1][N:2]>>[C:1]C[N:2]";
        final String[] expected = new String[]{"CCCCCN"};
        assertTransform(smiles, smirks, "CCCCCN");
        assertTransform(smiles, smirks, expected, Transform.Mode.Unique);
        assertTransform(smiles, smirks, expected, Transform.Mode.All);
    }

    @Test
    void testRingFormation_1() throws Exception {
        // the transformed molecule also matches the LHS of the smirks, assert that there is no infinite loop of matching
        final String smiles = "OC1NCCC(F)N1";
        final String smirks = "[O:1][C:2][N:3][C:4]>>[OH0:1]1[C:2][NH0:3]([C:4])CC1";
        final String[] expected = new String[]{"O1C2N(CCC(F)N2)CC1", "O1C2NCCC(F)N2CC1"};
        assertTransform(smiles, smirks, "O1C2N(CCC(F)N2)CC1");
        assertTransform(smiles, smirks, expected, Transform.Mode.Unique);
        assertTransform(smiles, smirks, expected, Transform.Mode.All);
    }

    // the mapped Br atom is present on the LHS only and gets removed when applying the transform
    @Test
    void testMappedAndUnmappedAtoms_1() throws Exception {
        final String smiles = "CCC(Br)=O.CCN";
        final String smirks = "[C:1]C([Br:2])=[O:3].[C:11][N:10]>>[C:1]C(=[O:3])[NH1:10][C:11]";
        final String expected = "CCC(=O)NCC";

        assertWarningMesg(smirks,
                          "Added/removed atoms do not need to be mapped");

        assertTransform(smiles, smirks, "CCC(=O)NCC");
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.All);
    }

    @Test
    void testMappedAndUnmappedAtoms_2() throws Exception {
        final String smiles = "CCC(Br)=O.CCN";
        final String smirks = "[C:1]C(Br)=[O:3].[C:11][N:10]>>[C:1]C(=[O:3])[NH1:10][C:11]";
        final String expected = "CCC(=O)NCC";

        assertTransform(smiles, smirks, "CCC(=O)NCC");
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.All);
    }

    @Test
    void testMappedAndUnmappedAtoms_3() throws Exception {
        final String smiles = "CCC(Br)=O.CCN";
        final String smirks = "[C:1]C([*:2])=[O:3].[C:11][N:10]>>[C:1]C(=[O:3])[NH1:10][C:11]";
        final String expected = "CCC(=O)NCC";

        assertWarningMesg(smirks,
                          "Added/removed atoms do not need to be mapped");

        assertTransform(smiles, smirks, "CCC(=O)NCC");
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.All);
    }

    // the mapped * atom expression is present on the LHS and the RHS
    // the * matches Br on the LHS and consequently BrH is present in the transform result
    @Test
    void testMappedAndUnmappedAtoms_4() throws Exception {
        final String smiles = "CCC(Br)=O.CCN";
        final String smirks = "[C:1]C([*:2])=[O:3].[C:11][N:10]>>[C:1]C(=[O:3])[NH1:10][C:11].[*H1:2]";
        final String expected = "CCC(=O)NCC.Br";

        assertTransform(smiles, smirks, expected);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.All);
    }

    @Test
    void testRingSubstituents_1() throws Exception {
        final String smiles = "NC1CCCCC1Br.OC1CCC(Br)C1";
        final String smirks = "[C:1]1[C:2][C:3][C:4][C:5][C:6]1Br.Br[C:11]1[C:12][C:13][C:14][C:15]1>>[C:1]1[C:2][C:3][C:4][C:5][C:6]1[C:11]2[C:12][C:13][C:14][C:15]2";
        final String expected = "NC1CCCCC1C2CCC(O)C2";

        assertTransform(smiles, smirks, expected);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[]{expected, expected, expected, expected}, Transform.Mode.All);
    }

    // the carbon atom connected to the hydroxyl group does not have a mapping index
    // consequently, application of the transform removes the hydroxyl group
    @Test
    void testRingSubstituents_2() throws Exception {
        final String smiles = "NC1CCCCC1Br.OC1CCC(Br)C1";
        final String smirks = "[C:1]1CCC[C:5][C:6]1Br.Br[C:11]1[C:12]CC[C:15]1>>[C:1]1CCC[C:5][C:6]1[C:11]2[C:12]CC[C:15]2";
        final String expected = "NC1C(CCCC1)C2CCCC2.[OH]";

        assertTransform(smiles, smirks, expected);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[]{expected, expected, expected, expected}, Transform.Mode.All);
    }

    // adapted from Hartenfeller et al. A Collection of Robust Organic Synthesis Reactions for
    //In Silico Molecule Design. J. Chem. Inf. Model. 2011, 51, 3093–3098. doi:10.1021/ci200379p
    @Test
    void testPiperidineIndole() throws Exception {
        final String smiles = "c1cccc2c1C=CN2.C1CC(=O)CCN1";
        final String smirks = "[c;H1:3]1:[c:4]:[c:5]:[c;H1:6]:[c:7]2:[nH:8]:[c:9]:[c;H1:1]:[c:2]:1:2.O=[C:10]1[#6;H2:11][#6;H2:12][N:13][#6;H2:14][#6;H2:15]1" +
                ">>[#6;H2:12]3[#6;H1:11]=[C:10]([c:1]1:[c:9]:[n:8]:[c:7]2:[c:6]:[c:5]:[c:4]:[c:3]:[c:2]:1:2)[#6;H2:15][#6;H2:14][N:13]3";
        final String expected = "c1cccc2c1[cH](c[nH]2)C=3CCNCC3";

        assertTransform(smiles, smirks, new SmirksTransform(), expected);
        assertTransform(smiles, smirks, new SmirksTransform(), new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new SmirksTransform(), new String[]{expected, "c1cccc2c1[cH](c[nH]2)C3=CCNCC3"}, Transform.Mode.All);
    }

    // adapted from Hartenfeller et al. A Collection of Robust Organic Synthesis Reactions for
    //In Silico Molecule Design. J. Chem. Inf. Model. 2011, 51, 3093–3098. doi:10.1021/ci200379p
    @Test
    void testOxadiazole() throws Exception {
        final String smiles = "CC#N.CC(=O)O";
        final String smirks = "[#6:6][C:5]#[#7;D1:4].[#6:1][C:2](=[OD1:3])[OH1]>>[#6:6][C:5]1=[N:4][O:3][C:2]([#6:1])=[NH0]1";
        final String expected = "CC1=NOC(C)=N1";

        assertTransform(smiles, smirks, expected);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.All);
    }

    // adapted from Hartenfeller et al. A Collection of Robust Organic Synthesis Reactions for
    //In Silico Molecule Design. J. Chem. Inf. Model. 2011, 51, 3093–3098. doi:10.1021/ci200379p
    @Test
    void testThiazole() throws Exception {
        final String smiles = "CC(N)=S.CC(I)C(C)=O";
        final String smirks = "[#6:6]-[CH0R0:1](=[OD1])-[CH1;R0:5](-[#6:7])-[*;#17,#35,#53].[NH2:2]-[CH0:3]=[SD1:4]>>[CH1:1]2(-[#6:6])[NH1:2][CH1:3][S:4][C:5]([#6:7])2";
        final String expected = "CC1NC(C(C)S1)C";

        assertTransform(smiles, smirks, expected);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[]{expected}, Transform.Mode.All);
    }

    // adapted from Hartenfeller et al. A Collection of Robust Organic Synthesis Reactions for
    //In Silico Molecule Design. J. Chem. Inf. Model. 2011, 51, 3093–3098. doi:10.1021/ci200379p
    @Test
    void testBenzoxazoleCarboxylicAcid() throws Exception {
        final String smiles = "c1cc(O)c(N)cc1.CC(=O)O";
        final String smirks = "[c;r6:1](-[OH1:2]):[c;r6:3](-[NH2:4]).[#6:6]-[CH0R0:5](=[OD1])-[OH1]>>[c:3]2[c:1][OH0:2][CH1:5]([#6:6])[NH1:4]2";
        final String expected = "c1cc2OC(C)Nc2cc1";

        assertTransform(smiles, smirks, new SmirksTransform(), expected);
        assertTransform(smiles, smirks, new SmirksTransform(), new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new SmirksTransform(), new String[]{expected}, Transform.Mode.All);
    }

    // adapted from Hartenfeller et al. A Collection of Robust Organic Synthesis Reactions for
    //In Silico Molecule Design. J. Chem. Inf. Model. 2011, 51, 3093–3098. doi:10.1021/ci200379p
    @Test
    void testBenzothiazole() throws Exception {
        final String smiles = "CC=O.Nc1ccccc1S";
        final String smirks = "[c;r6:1](-[SH1:2]):[c;r6:3](-[NH2:4]).[#6:6]-[CH1;R0:5](=[OD1])>>[c:3]2[c:1][SH0:2][C:5]([#6:6])[NH1:4]2";
        final String expected = "CC1Sc2ccccc2N1";

        assertTransform(smiles, smirks, new SmirksTransform(), expected);
        assertTransform(smiles, smirks, new SmirksTransform(), new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new SmirksTransform(), new String[]{expected}, Transform.Mode.All);
    }

    // adapted from Hartenfeller et al. A Collection of Robust Organic Synthesis Reactions for
    //In Silico Molecule Design. J. Chem. Inf. Model. 2011, 51, 3093–3098. doi:10.1021/ci200379p
    @Test
    void testPictetSpengler() throws Exception {
        final String smiles = "c1cc(CCN)ccc1.CC(=O)";
        final String smirks = "[cH1:1]1:[c:2](-[CH2:7]-[CH2:8]-[NH2:9]):[c:3]:[c:4]:[c:5]:[c:6]:1.[#6:11]-[CH1;R0:10]=[OD1]>>[cH0:1]12:[c:2](-[CH2:7]-[CH2:8]-[NH1:9]-[C:10]-2(-[#6:11])):[c:3]:[c:4]:[c:5]:[c:6]:1";
        final String expected = "c1c2c(CCNC2C)ccc1";

        assertTransform(smiles, smirks, new SmirksTransform(), expected);
        assertTransform(smiles, smirks, new SmirksTransform(), new String[]{expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new SmirksTransform(), new String[]{expected, "c1cc2CCNC(C)c2cc1"}, Transform.Mode.All);
    }

    @Test
    void testAnyRingBondOnRightHandSide() {
        final String smirks = "[c;r6:1](-[SH1:2]):[c;r6:3](-[NH2:4]).[#6:6]-[CH1;R0:5](=[OD1])>>[c:3]2:[c:1]:[s:2]:[c:5](-[#6:6]):[n:4]@2";
        assertWarningMesg(smirks, "Ignored query bond (implicit: -,:), use '~' to suppress this warning");
    }

    @Test
    void testRetroWittigLike() throws Exception {
        final String smiles = "CCC=Cc1ccccc1";
        final String smirks = "[CH1:1]=[CH1+0:2]>>[CH1:1]=O.[CH2+0:2][P+](c1ccccc1)(C)C";
        final String expected = "CCC=O.C(c1ccccc1)[P+](c2ccccc2)(C)C";

        assertTransform(smiles, smirks, new SmirksTransform(), expected);
        assertTransform(smiles, smirks, new SmirksTransform(),
                        new String[]{expected},
                        Transform.Mode.Unique);
        assertTransform(smiles, smirks, new SmirksTransform(),
                        new String[]{
                                "CCC=O.C(c1ccccc1)[P+](c2ccccc2)(C)C",
                                "CCC[P+](c1ccccc1)(C)C.C(c1ccccc1)=O"},
                        Transform.Mode.All);
    }

    @Test
    void testRetroWittigLikeTriPhenyl() throws Exception {
        final String smiles = "CCC=Cc1ccccc1";
        final String smirks = "[CH1:1]=[CH1+0:2]>>[CH1:1]=O.[CH2+0:2][P+](c1ccccc1)(c2ccccc2)c3ccccc3";
        final String expected = "CCC=O.C(c1ccccc1)[P+](c2ccccc2)(c3ccccc3)c4ccccc4";

        assertTransform(smiles, smirks, new SmirksTransform(), expected);
        assertTransform(smiles, smirks, new SmirksTransform(),
                        new String[]{expected},
                        Transform.Mode.Unique);
        assertTransform(smiles, smirks, new SmirksTransform(),
                        new String[]{
                                "CCC=O.C(c1ccccc1)[P+](c2ccccc2)(c3ccccc3)c4ccccc4",
                                "CCC[P+](c1ccccc1)(c2ccccc2)c3ccccc3.C(c1ccccc1)=O"},
                        Transform.Mode.All);
    }

    // need to check the atoms of a bond to see if the implicit bond definition
    // is aliphatic or aromatic
    @Test
    void testAromaticContext() throws Exception {
        assertTransform("FC(F)(F)c1nn(c(c1)C)C",
                        "[cH0X3v4+0:1]1[cH1X3v4+0:3][cH0X3v4+0:4][nH0X2v3+0:11][nH0X3v3+0:10]1>>[CH0+0:1]([CH2+0:3][CH0+0:4]=O)=O.[NH1+0:10][NH2+0:11]",
                        "FC(F)(F)C(CC(C)=O)=O.NNC");
    }

    @Test
    void testImplicitValenceAromatic5() throws Exception {
        assertTransform("n1c(cccc1C)-n2c(ccc2C)C",
                        "[c;H0v4X3;+0:1]1[c;H1v4X3;+0:3][c;H1v4X3;+0:4][c;H0v4X3;+0:5][n;H0v3X3;+0:13]1>>[H][#6;AH1:3]([#6;AH0;+0:1]=O)[#6;AH1:4]([H])[#6;AH0;+0:5]=O.[H][#7;A:13][H]",
                        "n1c(cccc1C)N.C(CCC(C)=O)(C)=O");
    }

    @Test
    void testAromatic6() throws Exception {
        assertTransform("CCn1c(CCl)nc2cc(C)ccc12",
                        "[cH0+0:1]1[nH0+0:6][cH0+0:7][cH0+0:13][nD2H0+0:14]1>>[CH0+0:1](O)=O.[NH1+0:6][cH0+0:7][cH0+0:13][NH2+0:14]",
                        "CCNc1ccc(cc1N)C.C(CCl)(O)=O");
    }

    @Test
    void testFischerIndole() throws Exception {
        assertTransform("CCOC(=O)c1[nH]c2ccccc2c1Cc1ccccc1",
                        "[#8:9]-[C:8](=[O;D1;H0:10])-[c;H0;D3;+0:5]1:[nH;D2;+0:1]:[c:2]:[c;H0;D3;+0:3](:[c:4]):[c;H0;D3;+0:6]:1-[C:7]>>N-[NH;D2;+0:1]-[c:2]:[cH;D2;+0:3]:[c:4].O=[C;H0;D3;+0:5](-[CH2;D2;+0:6]-[C:7])-[C:8](-[#8:9])=[O;D1;H0:10]",
                        "CCOC(=O)C(CCc1ccccc1)=O.N(c1ccccc1)N");
    }

    @Test
    void testChargeNewAtom() throws Exception {
        assertTransform("ClC(=O)C1=CC=CC=C1",
                        "[#6:1]Cl>>[#6:1][CH2+]",
                        "[CH2+]C(=O)C1=CC=CC=C1");
    }


    @Test
    void testDeleteAtom() throws Exception {
        assertTransform("Cl.c1ccccc1",
                        "Cl>>",
                        "c1ccccc1");
    }

    @Test
    void testDeleteBond() throws Exception {
        assertTransform("BrBr",
                        "[Br:1][Br:2]>>[Br:1].[Br:2]",
                        "[Br].[Br]");
    }

    @Test
    void testSetChange() throws Exception {
        assertTransform("c1ccccc1O",
                        "[OH:1]>>[OH0-:1]",
                        "c1ccccc1[O-]");
    }

    @Test
    void testSetMass() throws Exception {
        assertTransform("c1ccccc1[H]",
                        "[H:1]>>[2H:1]",
                        "c1ccccc1[2H]");
    }

    @Test
    void testSetElem() throws Exception {
        assertTransform("[Pb]",
                        "[Pb:1]>>[Au:1]",
                        "[Au]");
        assertTransform("[Pb]",
                        "[Pb:1]>>[Au:1]",
                        "[Pb]",
                        SmirksOption.IGNORE_SET_ELEM);
    }

    @Test
    void shouldHaveOneSingleBond() throws Exception {
        assertTransform("CC",
                        "([CH3:1].[CH3:2])>>[cH1:1]cccc[cH1:2]",
                        "c1-ccccc1");
        assertNoMatch("CC",
                      "([CH3:1].[CH3:2])>>[cH1:1]1cccc[cH1:2]1");
        assertTransform("CC",
                        "([CH3:1].[CH3:2])>>[cH1:1]1cccc[cH1:2]1",
                        "c1ccccc1",
                        SmirksOption.OVERWRITE_BOND);
    }

    @Test
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
    void testLazyTransform() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Isomeric | SmiFlavor.UseAromaticSymbols);

        String smirks = "[#8H1:1].[#8H1:2].[#8H1:3].[#8H1:4].[#8H1:5].[#8H1:6].[#8H1:7].[#8H1:8]>>[#6H3]-[#6](-[#8H0:5])=O.[#6H3]-[#6](-[#8H0:1])=O.[#6H3]-[#6](-[#8H0:2])=O.[#6H3]-[#6](-[#8H0:6])=O.[#6H3]-[#6](-[#8H0:3])=O.[#6H3]-[#6](-[#8H0:7])=O.[#6H3]-[#6](-[#8H0:4])=O.[#6H3]-[#6](-[#8H0:8])=O";
        String sucrose = "OC[C@H]1O[C@@](CO)(O[C@H]2O[C@H](CO)[C@@H](O)[C@H](O)[C@H]2O)[C@@H](O)[C@@H]1O";
        IAtomContainer mol = smipar.parseSmiles(sucrose);
        Transform tform = Smirks.compile(smirks);

        long t0 = System.nanoTime();
        int i = 0;
        for (IAtomContainer container : tform.apply(mol, Transform.Mode.All)) {
            if (++i >= 10)
                break;
        }
        Assertions.assertEquals(10, i);
    }

    @Test
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
    void testLazyTransformLimit() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Isomeric | SmiFlavor.UseAromaticSymbols);

        String smirks = "[#8H1:1].[#8H1:2].[#8H1:3].[#8H1:4].[#8H1:5].[#8H1:6].[#8H1:7].[#8H1:8]>>[#6H3]-[#6](-[#8H0:5])=O.[#6H3]-[#6](-[#8H0:1])=O.[#6H3]-[#6](-[#8H0:2])=O.[#6H3]-[#6](-[#8H0:6])=O.[#6H3]-[#6](-[#8H0:3])=O.[#6H3]-[#6](-[#8H0:7])=O.[#6H3]-[#6](-[#8H0:4])=O.[#6H3]-[#6](-[#8H0:8])=O";
        String sucrose = "OC[C@H]1O[C@@](CO)(O[C@H]2O[C@H](CO)[C@@H](O)[C@H](O)[C@H]2O)[C@@H](O)[C@@H]1O";
        IAtomContainer mol = smipar.parseSmiles(sucrose);
        Transform tform = Smirks.compile(smirks);

        int i = 0;
        for (IAtomContainer container : tform.apply(mol, Transform.Mode.All, 10)) {
            ++i;
        }
        Assertions.assertEquals(10, i);
    }

    @Test
    void testExclusiveApplyLimit() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Isomeric | SmiFlavor.UseAromaticSymbols);
        String smirks = "[#8H1:1]>>[#8H0-:1]";
        String sucrose = "OC[C@H]1O[C@@](CO)(O[C@H]2O[C@H](CO)[C@@H](O)[C@H](O)[C@H]2O)[C@@H](O)[C@@H]1O";
        IAtomContainer mol = smipar.parseSmiles(sucrose);
        Transform tform = Smirks.compile(smirks);
        tform.apply(mol, 5);
        String smi = smigen.create(mol);
        Assertions.assertEquals(5, countOccurrences(smi, "[O-]"));
    }

    private static int countOccurrences(String smi, String key) {
        int count = 0;
        int idx = 0;
        while ((idx = smi.indexOf(key, idx)) >= 0) {
            idx += key.length();
            count++;
        }
        return count;
    }

    @Test
    void setHydrogenCount() throws Exception {
        assertTransform("[H]C", "[#6:1]>>[#6H0:1]", "[C]");
        assertTransform("[H]C", "[#6:1]>>[#6H0:1]", "[H]C", SmirksOption.IGNORE_TOTAL_H0);
        assertTransform("[H]C", "[#6:1]>>[#6H1:1]", "[H][C]");
        assertTransform("[H]C", "[#6:1]>>[#6H1:1]", "[H][C]");
        assertTransform("[H]C", "[#6:1]>>[#6H2:1]", "[H][CH]");
        assertTransform("[H]C", "[#6:1]>>[#6H2:1]", "[H][CH]", SmirksOption.IGNORE_TOTAL_H0);
        assertTransform("[H]C", "[#6:1]>>[#6H2:1]", "[H]C", SmirksOption.IGNORE_TOTAL_H);
        assertTransform("[H]C", "[#6:1]>>[#6H3:1]", "[H][CH2]");
        assertTransform("[H]C", "[#6:1]>>[#6H4:1]", "[H]C");
        assertTransform("[H]C", "[#6:1]>>[#6H5:1]", "[H][CH4]");

        assertTransform("[H]C[H]", "[#6:1]>>[#6H0:1]", "[C]");
        assertTransform("[H]C[H]", "[#6:1]>>[#6H1:1]", "[C][H]");
        assertTransform("[H]C[H]", "[#6:1]>>[#6H2:1]", "[H][C][H]");
        assertTransform("[H]C[H]", "[#6:1]>>[#6H3:1]", "[H][CH][H]");
        assertTransform("[H]C[H]", "[#6:1]>>[#6H4:1]", "[H]C[H]");
        assertTransform("[H]C[H]", "[#6:1]>>[#6H5:1]", "[H][CH3][H]");

        assertTransform("[H]C", "[#6:1]>>[#6h0:1]", "[H][C]");
        assertTransform("[H]C", "[#6:1]>>[#6h1:1]", "[H][CH]");
        assertTransform("[H]C", "[#6:1]>>[#6h2:1]", "[H][CH2]");
        assertTransform("[H]C", "[#6:1]>>[#6h3:1]", "[H]C");
        assertTransform("[H]C", "[#6:1]>>[#6h4:1]", "[H][CH4]");
        assertTransform("[H]C", "[#6:1]>>[#6h5:1]", "[H][CH5]");
    }

    @Test
    void setHydrogenCountIsotope() throws Exception {
        assertTransform("[2H]C", "[#6:1]>>[#6h0:1]", "[2H][C]");
        assertTransform("[2H]C", "[#6:1]>>[#6h1:1]", "[2H][CH]");
        assertTransform("[2H]C", "[#6:1]>>[#6h2:1]", "[2H][CH2]");
        assertTransform("[2H]C", "[#6:1]>>[#6h3:1]", "[2H]C");
        assertTransform("[2H]C", "[#6:1]>>[#6h4:1]", "[2H][CH4]");
        assertTransform("[2H]C", "[#6:1]>>[#6h5:1]", "[2H][CH5]");
    }

    @Test
    void setHydrogenCountBridging() throws Exception {
        // we can not delete bridging hydrogens by setting the hcnt since this affects
        // the other atoms
        assertNoMatch("[H]B1([H]B([H]1)[H])", "[#5:1]>>[#5H0:1]");
        assertNoMatch("[H]B1([H]B([H]1)[H])", "[#5:1]>>[#5H1:1]");
        assertTransform("[H]B1([H]B([H]1)[H])", "[#5:1]>>[#5H2:1]", "[B]1[H][B][H]1");
        assertTransform("[H]B1([H]B([H]1)[H])", "[#5:1]>>[#5H3:1]", "[H]B1[H]B([H]1)[H]");
        assertTransform("[H]B1([H]B([H]1)[H])", "[#5:1]>>[#5H4:1]", "[H][BH]1[H][BH]([H]1)[H]");
    }

    @Test
    void defaultValences() throws Exception {
        assertTransform("C", "[#6:1]>>[#6H3:1]ClO", "C[ClH]O");
        assertTransform("C", "[#6:1]>>[#6H3:1]Cl=O", "CCl=O");
        assertTransform("C", "[#6:1]>>[#6H3:1]Cl(=O)=O", "CCl(=O)=O");
        assertTransform("C", "[#6:1]>>[#6H3:1]S=O", "CS=O");
        assertTransform("C", "[#6:1]>>[#6H3:1]N(O)O", "CN(O)O");
        assertTransform("C", "[#6:1]>>[#6H3:1]N(O)=O", "CN(O)=O");
    }

    @Test
    void testAssertWarningMesgs() throws Exception {
        assertWarningMesg(">>C:C",
                          "Aromatic bond ':' connected to an aliphatic atom");
        assertWarningMesg("[*:1][*:2]>>[*:1][*:2]",
                          "Ignored query bond (implicit: -,:), use '~' to suppress this warning");
    }

    @Test
    void testValenceWarning() {
        assertWarningMesg("[C:1]>>[C:1]O",
                          "Possible valence change");
        assertNoWarningMesg("[C:1][H]>>[C:1]O");
        assertWarningMesg("[C:1][H]>>[C:1]=O",
                          "Possible valence change");
        assertNoWarningMesg("[CH4:1]>>[CH3:1]O");
    }

    @Test
    void shouldRemoveUnmapped() throws Exception {
        assertTransform("c1ccccc1OCC",
                        "[c:1]O>>[c:1][H]",
                        "c1ccccc1.[CH2]C");
        assertTransform("c1ccccc1OCC",
                        "[c:1]O>>[c:1][H]",
                        "c1ccccc1",
                        SmirksOption.REMOVE_UNMAPPED_FRAGMENTS);
        assertTransform("c1ccccc1OCC",
                        "[C:1]O>>[C:1][H]",
                        "CC",
                        SmirksOption.REMOVE_UNMAPPED_FRAGMENTS);
    }

    @Test
    void shouldRecomputeHydrogenCount() throws Exception {
        assertTransform("c1ccccc1C",
                        "[C:1]>>[C:1][O]",
                        "c1ccccc1[CH3][O]");
        assertTransform("c1ccccc1C",
                        "[C:1]>>[C:1][O]",
                        "c1ccccc1CO",
                        SmirksOption.RECOMPUTE_HYDROGENS);
        assertTransform("c1ccccc1C",
                        "[C:1]>>[C:1]=[O]",
                        "c1ccccc1C=O",
                        SmirksOption.RECOMPUTE_HYDROGENS);
        assertTransform("[CH]c1ccccc1C",
                        "[CH3:1]>>[C:1]=[O]",
                        "[CH]c1ccccc1C=O",
                        SmirksOption.RECOMPUTE_HYDROGENS);
        assertTransform("C",
                        "[C:1]>>[C+:1]",
                        "[CH3+]",
                        SmirksOption.RECOMPUTE_HYDROGENS);
        assertTransform("C",
                        "[C:1]>>[N-:1]",
                        "[NH2-]",
                        SmirksOption.RECOMPUTE_HYDROGENS);
    }

    @Test
    void shouldRecomputeHydrogenCount_Charges() throws Exception {
        assertTransform("C",
                        "[C:1]>>[C+:1]",
                        "[CH3+]",
                        SmirksOption.RECOMPUTE_HYDROGENS);
        assertTransform("C",
                        "[C:1]>>[N-:1]",
                        "[NH2-]",
                        SmirksOption.RECOMPUTE_HYDROGENS);
    }

    @Test
    void shouldRecomputeHydrogenCount_LeaveUnmapped() throws Exception {
        assertTransform("[CH]c1ccccc1C",
                        "[CH3:1]>>[C:1]=[O]",
                        "[CH]c1ccccc1C=O",
                        SmirksOption.RECOMPUTE_HYDROGENS);
    }

    @Test
    void shouldRecomputeHydrogenCount_ExplictH() throws Exception {
        assertTransform("c1ccccc1C",
                        "[CH3:1]>>[C:1][OH0]",
                        "c1ccccc1C[O]",
                        SmirksOption.RECOMPUTE_HYDROGENS);
        assertTransform("c1ccccc1C",
                        "[CH3:1]>>[C:1][OH0]",
                        "c1ccccc1CO",
                        SmirksOption.RECOMPUTE_HYDROGENS,
                        SmirksOption.IGNORE_TOTAL_H0);
        assertTransform("c1ccccc1C",
                        "[CH3:1]>>[C:1][OH2]",
                        "c1ccccc1C[OH2]",
                        SmirksOption.RECOMPUTE_HYDROGENS);
    }

    @Test
    void testPedanticUndefinedBondErrors() throws Exception {
        Transform transform = new Transform();
        Assertions.assertFalse(Smirks.parse(transform,
                                            "[*D1:1].[*D1:2]>>[*:1][*:2]",
                                            SmirksOption.PEDANTIC));
        Assertions.assertEquals("Cannot determine bond order for newly created bond",
                                transform.message());
        Assertions.assertTrue(Smirks.parse(transform,
                                           "[*D1:1].[*D1:2]>>[*:1]-[*:2]",
                                           SmirksOption.PEDANTIC));
    }

    // Note: not perfect - should use the replace atom op-code
    @Test
    void testAddOMe() throws Exception {
        assertTransform("ClC(=O)C1=CC=CC=C1",
                        "[#6:1]Cl>>[#6:1]OC",
                        "C(=O)(C1=CC=CC=C1)OC");
    }
}