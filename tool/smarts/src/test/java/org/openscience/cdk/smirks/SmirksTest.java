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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.openscience.cdk.isomorphism.TransformOp.Type.*;

class SmirksTest {

    private static final IChemObjectBuilder BUILDER = SilentChemObjectBuilder.getInstance();
    private static final SmilesParser SMIPAR = new SmilesParser(BUILDER);
    private static final SmilesGenerator SMIGEN = new SmilesGenerator(SmiFlavor.Default | SmiFlavor.UseAromaticSymbols);

    static void assertTransform(String smiles, String smirks, String expected) throws Exception {
        assertTransform(smiles, smirks, new String[] {expected}, Transform.Mode.Exclusive);
    }

    static void assertTransform(String smiles, String smirks, String[] expected, Transform.Mode mode) throws Exception {
        IAtomContainer mol = SMIPAR.parseSmiles(smiles);
        Transform transform = new Transform();
        assertTrue(Smirks.parse(transform, smirks), transform.message());
        Iterable<IAtomContainer> iterable = transform.apply(mol, mode);

        List<String> actualSmiles = new ArrayList<>();
        for (IAtomContainer actual: iterable) {
            actualSmiles.add(SMIGEN.create(actual));
        }

        assertEquals(expected.length, actualSmiles.size(), "The number of expected transforms " + expected.length +
                " and actual transforms " + actualSmiles.size() + " is different.");

        for (int i = 0; i < expected.length; i++) {
            Assertions.assertEquals(
                    expected[i],
                    actualSmiles.get(i),
                    "Applying the transform did not generate the expected molecule");
        }
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
        assertEquals(transform.message(), "Duplicate atom map [C:3] and [O:3]");
    }

    @Test
    void testDuplicateMapIdx_Reactant() {
        Transform transform = new Transform();
        assertFalse(Smirks.parse(transform, "[C:4][O:4]>>[C:4]"));
        assertEquals(transform.message(), "Duplicate atom map [C:4] and [O:4]");
    }

    @Test
    void warnOnUnpairedAtomMap() {
        Transform transform = new Transform();
        assertTrue(Smirks.parse(transform, "[C:3]>>[C:3][O:4]"));
        assertEquals(transform.message(), "Warning - added/removed atoms do not need to be mapped: [O:4]");
    }

    @Test
    void atomTypeHChanges_1() {
        assertAtomTypeOps("[CH1]", "[C]"); // no-op
        assertAtomTypeOps("[CH1]", "[CH0]", new TransformOp(ImplH, 0, 0));
        assertAtomTypeOps("[CH1]", "[CH1]"); // no-op
        assertAtomTypeOps("[CH1]", "[CH2]", new TransformOp(ImplH, 0, 2));
    }

    @Test
    void atomTypeImplHChanges_1() {
        assertAtomTypeOps("[CH1]", "[C]"); // no-op
        assertAtomTypeOps("[CH1]", "[Ch0]", new TransformOp(ImplH, 0, 0));
        assertAtomTypeOps("[CH1]", "[Ch1]"); // no-op
        assertAtomTypeOps("[CH1]", "[Ch2]", new TransformOp(ImplH, 0, 2));
        assertAtomTypeOps("[CH1]", "[C;h2,H3]");
        assertAtomTypeOps("[CH1]", "[C;h3,H3]", new TransformOp(ImplH, 0, 3));
    }

    @Test
    void atomTypeHChanges_2() {
        assertAtomTypeOps("[C;H0,H1]", "[C]"); // no-op
        assertAtomTypeOps("[C;H0,H1]", "[CH0]", new TransformOp(ImplH, 0, 0));
        assertAtomTypeOps("[C;H0,H1]", "[CH1]", new TransformOp(ImplH, 0, 1));
        assertAtomTypeOps("[C;H0,H1]", "[CH2]", new TransformOp(ImplH, 0, 2));
    }

    @Test
    void atomTypeHChanges_3() {
        assertAtomTypeOps("[C;H0,H1]", "[C;H0,H1]"); // no-op
        assertAtomTypeOps("[C;H0,H1]", "[C,H0]"); // conflicting
        assertAtomTypeOps("[C;H0,H1]", "[C;H0]", new TransformOp(ImplH, 0, 0));
        assertAtomTypeOps("[C;H0,H1]", "[C;H1]", new TransformOp(ImplH, 0, 1));
        assertAtomTypeOps("[C;H0,H1]", "[C;H2]", new TransformOp(ImplH, 0, 2));
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

    @Disabled("TODO aromatic flags on atoms/bonds")
    @Test
    public void testImplicitValenceAromatic() throws Exception {
        assertTransform("c1ccccc1C(=O)O",
                        "[OD1:1][H]>>[O:1]Cc1ccccc1",
                        "c1ccccc1C(=O)OCc1ccccc1");
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
    // but atom map 0 => 5, 0 is undef, think this is a bug in Daylight doc (TODO Roger)
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
        assertTransform("C[C@H](O)CC",
                        "[CH1:1][H]>>[*:1]c1ccccc1",
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
                        "[*:1][*@@:2]([*:3])([*:4])[*:5]>>[*:1][*@:2]([*:3])([*:4])[*:5]",
                        "C[C@](Br)(Cl)N");
    }

    @Test
    void testSetStereo_1() throws Exception {
        assertTransform("CC=CC",
                        "[*:1][*:2]=[*:3][*:4]>>[*:1]/[*:2]=[*:3]/[*:4]",
                        "C/C=C/C");
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

    // To Test:
    // [H:1]C>>C[*:1]

    @Test
    void testBondOrderingSwapping_1() throws Exception {
        assertTransform("[Li+:30].[Al+3:32].[O:28]=[C:27]1[O:31][C:2](=[O:29])[CH:3]2[CH2:26][C:17]" +
                "(=[C:8]([C:9]3=[CH:10][CH:11]=[C:12]([O:13][CH3:14])[CH:15]=[CH:16]3)[CH2:7][CH:4]12)[C:18]=4" +
                "[CH:19]=[CH:20][C:21]([O:22][CH3:23])=[CH:24][CH:25]4",
                 "[Al+3;h0X0:32].[C+0;h1:3]1[C+0;h1:4][C+0;h0X3v4:27](=[O+0;h0:28])[O+0;h0X2v2:31][C+0;h0X3v4:2]1=[O+0;h0:29].[Li+;h0X0:30]>>[CH2+0:2]([OH1+0:29])[CH1+0:3][CH1+0:4][CH2+0:27][O+0;h1:28]",
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
                "[CH0D3v4:2][OH1D1v2:17].[NH2D1v3:3]>>[CH0:2][NH1:3]",
                "O=C(C=1NC=2C=CC=CC2C1)NCCCO");
    }

    @Test
    void testReaction_2() throws Exception {
        assertTransform("[O:1]=[C:2]([OH:17])[C:8]=1[NH:16][C:15]=2[CH:14]=[CH:13][CH:12]=[CH:11][C:10]2[CH:9]1.[OH:7][CH2:6][CH2:5][CH2:4][NH2:3]",
                "[N+0;h2D1v3:3][C+0;h2:4].[O+0;h0:1]=[C+0;h0D3v4:2]([O+0;h1D1v2:17])[C+0;h0:8]>>[OH0:1]=[CH0:2]([NH1:3][CH2:4])[CH0:8]",
                "O=C(C=1NC=2C=CC=CC2C1)NCCCO");
    }

    // uses atom properties h, D and v; should the usage of these yield an exception or a warning message?
    @Test
    void testReaction_3() throws Exception {
        assertTransform("[O:21]=[C:20]([NH:29][NH2:28])[C:22]1=[CH:23][CH:24]=[CH:25][N:26]=[CH:27]1." +
                        "[OH:19][CH2:18][CH2:17][CH2:16][CH2:15][CH2:14][CH2:13][CH2:12][CH2:11][CH2:10][CH2:9][CH2:8][CH2:7][CH2:6][CH2:5][CH2:4][CH:2]([CH3:1])[CH3:3]",
                "[N+0;h2D1v3:28][N+0;h1D2v3:29][C+0;h0D3v4:20].[O+0;h1D1v2:19]>>[O+0;h0D2v2:19][C+0;h0D3v4:20]",
                "O=C(C1=CC=CN=C1)OCCCCCCCCCCCCCCCC(C)C");
    }

    @Test
    void testReaction_4() throws Exception {
        assertTransform("[O:21]=[C:20]([NH:29][NH2:28])[C:22]1=[CH:23][CH:24]=[CH:25][N:26]=[CH:27]1." +
                        "[OH:19][CH2:18][CH2:17][CH2:16][CH2:15][CH2:14][CH2:13][CH2:12][CH2:11][CH2:10][CH2:9][CH2:8][CH2:7][CH2:6][CH2:5][CH2:4][CH:2]([CH3:1])[CH3:3]",
                "[N+0;h2D1v3:28][N+0;h1D2v3:29][C+0;h0D3v4:20].[O+0;h1D1v2:19]>>[OH0:19][C:20]",
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
    void testReaction_5_SmirksTransform() throws Exception {
        final String smiles = "[O:21]=[C:20]([NH:29][NH2:28])[C:22]1=[CH:23][CH:24]=[CH:25][N:26]=[CH:27]1." +
                "[OH:19][CH2:18][CH2:17][CH2:16][CH2:15][CH2:14][CH2:13][CH2:12][CH2:11][CH2:10][CH2:9][CH2:8][CH2:7][CH2:6][CH2:5][CH2:4][CH:2]([CH3:1])[CH3:3]";
        final String smirks = "[C+0;h2:18][O+0;h1D1v2:19].[N+0;h2D1v3:28][N+0;h1D2v3:29][C+0;h0D3v4:20](=[O+0;h0:21])[c+0;h0:22]>>[CH2:18][OH0:19][CH0:20](=[OH0:21])[cH0:22]";
        final String expected = "O=C(c1cccnc1)OCCCCCCCCCCCCCCCC(C)C";

        IAtomContainer atomContainer = SMIPAR.parseSmiles(smiles);
        SmirksTransform transform = new SmirksTransform();
        assertTrue(Smirks.parse(transform, smirks), transform.message());
        assertTrue(transform.apply(atomContainer));
        String actual = SMIGEN.create(atomContainer);

        Assertions.assertEquals(
                expected,
                actual,
                "Applying the transform did not generate the expected molecule");
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
                "[C+0;h0D3v4:6](=[O+0;h0:17])([C+0;h2:8])[C+0;h2:10].[c+0;h1:4]:[c+0;h0D3v4:5]([I+0;h0D1v1:18]):[c+0;h0:11]" +
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
        final String[] expectedArray = new String[] {"[N+](=O)([O-])CC[N+](=O)[O-]", "[N+](=O)([O-])CC[N+](=O)[O-]"};
        assertTransform(smiles, smirks, expectedArray[0]);
        assertTransform(smiles, smirks, expectedArray, Transform.Mode.Unique);
        assertTransform(smiles, smirks, expectedArray, Transform.Mode.All);
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

    @Disabled("throws an IllegalArgumentException in org.openscience.cdk.smirks.Smirks.collectBondPairs with the mapped Hs")
    @Test
    void testSwernOxidationMappedHydrogens() throws Exception {
        final String smiles = "C1CC(O)C(O)CC1";
        final String smirks = "[*:1][C:2]([H:3])([O:4][H:5])[C:6]([H:7])([O:8][H:9])[*:10]>>[*:1][C:2](=[O:4])[C:6](=[O:8])[*:10].[H:3][H:5].[H:7][H:9]";
        final String expected = "C1CC(=O)C(=O)CC1";
        assertTransform(smiles, smirks, expected);
    }

    @Test
    void testTwoNonOverlappingMatches() throws Exception {
        final String smiles = "O=CC(C)CCC=O";
        final String smirks = "[C:1]=[O:2]>>[H][C:1][O:2][H]";
        final String[] expectedArray = new String[] {"OCC(C)CCCO", "OCC(C)CCCO"};
        assertTransform(smiles, smirks, expectedArray[0]);
        assertTransform(smiles, smirks, expectedArray, Transform.Mode.Unique);
        assertTransform(smiles, smirks, expectedArray, Transform.Mode.All);
    }

    @Test
    void testTwoOverlappingMatches() throws Exception {
        final String smiles = "O=CCC=O";
        final String smirks = "[C:3][C:1]=[O:2]>>[C:3][C:1]([H])[O:2][H]";
        assertTransform(smiles, smirks, "OCCC=O");
        assertTransform(smiles, smirks, new String[] {"OCCCO", "OCCCO"}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[] {"OCCCO", "OCCCO"}, Transform.Mode.All);
    }

    @Test
    void testDielsAlder() throws Exception {
        final String smiles = "C=CC=C.C=C";
        final String smirks = "[C:1]=[C:2][C:3]=[C:4].[C:5]=[C:6]>>[C:1]1[C:2]=[C:3][C:4][C:5][C:6]1";
        final String expected = "C1C=CCCC1";
        assertTransform(smiles, smirks, expected);
        assertTransform(smiles, smirks, new String[] {expected}, Transform.Mode.Unique);
        assertTransform(smiles, smirks, new String[] {expected}, Transform.Mode.All);
    }

}