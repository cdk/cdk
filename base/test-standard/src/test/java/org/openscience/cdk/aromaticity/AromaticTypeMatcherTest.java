/*
 * Copyright (C) 2024 John Mayfield
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

package org.openscience.cdk.aromaticity;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AromaticTypeMatcherTest {

    private final Set<AromaticType> REMAINING = Collections.synchronizedSet(EnumSet.allOf(AromaticType.class));

    void assertAtomType(String smi, AromaticType expected) throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser sp = new SmilesParser(bldr);
        IAtomContainer mol = sp.parseSmiles(smi);
        Cycles.markRingAtomsAndBonds(mol);
        AromaticType type = AromaticTypeMatcher.getType(mol.getAtom(0));
        Assertions.assertEquals(expected, type);
        REMAINING.remove(type);
    }

    @AfterAll
    void ensureAllTypesTested() {
        Assertions.assertTrue(REMAINING.isEmpty(),
                              "Untested atom types: " + REMAINING);
    }

    @Test
    void boronTypes() throws InvalidSmilesException {
        assertAtomType("B1=NB=NB=N1", AromaticType.B2);
        assertAtomType("B1NBNBN1", AromaticType.B3);
        assertAtomType("B", AromaticType.UNKNOWN);
        assertAtomType("B(N)N", AromaticType.UNKNOWN);
    }

    @Test
    void carbonTypes() throws InvalidSmilesException {
        assertAtomType("[C]1=CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("C1=CC=CC=C1", AromaticType.C3);
        assertAtomType("[C-]1=CC=CC=C1", AromaticType.C2_MINUS);
        assertAtomType("[C+]1=CC=CC=C1", AromaticType.C2_PLUS);
        assertAtomType("[CH-]1CC=CC=C1", AromaticType.C3_MINUS);
        assertAtomType("[CH+]1CC=CC=C1", AromaticType.C3_PLUS);
        assertAtomType("C1(=O)CC=CC=C1", AromaticType.C3_ENEG_EXO);
        assertAtomType("C1(=N)CC=CC=C1", AromaticType.C3_ENEG_EXO);
        assertAtomType("C1(=C)CC=CC=C1", AromaticType.C3_EXO);
        assertAtomType("[C-]1CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("C1(=C)=CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("C1(=O)=CC=CC=C1", AromaticType.UNKNOWN);
    }

    @Test
    void siliconType() throws InvalidSmilesException {
        assertAtomType("[Si]1=CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("[SiH]1=CC=CC=C1", AromaticType.Si3);
        assertAtomType("[Si-]1=CC=CC=C1", AromaticType.Si2_MINUS);
        assertAtomType("[Si+]1=CC=CC=C1", AromaticType.Si2_PLUS);
        assertAtomType("[SiH-]1CC=CC=C1", AromaticType.Si3_MINUS);
        assertAtomType("[SiH+]1CC=CC=C1", AromaticType.Si3_PLUS);
        assertAtomType("[Si]1(=C)CC=CC=C1", AromaticType.Si3_EXO);
        assertAtomType("[Si-]1CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("[Si]1(=C)=CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("[Si]1(=O)=CC=CC=C1", AromaticType.UNKNOWN);
    }

    @Test
    void nitrogenTypes() throws InvalidSmilesException {
        assertAtomType("N1=CC=CC=C1", AromaticType.N2);
        assertAtomType("[N-]1CC=CC=C1", AromaticType.N2_MINUS);
        assertAtomType("N1CC=CC=C1", AromaticType.N3);
        assertAtomType("N1(C)CC=CC=C1", AromaticType.N3);
        assertAtomType("[NH2]1CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("[NH3]1CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("N1(=O)=CC=CC=C1", AromaticType.N3_OXIDE);
        assertAtomType("[N+]1([O-])=CC=CC=C1", AromaticType.N3_OXIDE_PLUS);
        assertAtomType("[N+]1(=O)CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("[NH+]1C=CC=CC=1", AromaticType.N3_PLUS);
    }

    @Test
    void phosphorusType() throws InvalidSmilesException {
        assertAtomType("P1=CC=CC=C1", AromaticType.P2);
        assertAtomType("[P-]1CC=CC=C1", AromaticType.P2_MINUS);
        assertAtomType("P1CC=CC=C1", AromaticType.P3);
        assertAtomType("P1(C)CC=CC=C1", AromaticType.P3);
        assertAtomType("P1(N)(N)=NC=CC=N1", AromaticType.P4);
        assertAtomType("P1(=N)(N)NC=CC=N1", AromaticType.UNKNOWN);
        assertAtomType("[PH2]1CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("[PH3]1CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("P1(=O)=CC=CC=C1", AromaticType.P3_OXIDE);
        assertAtomType("[P+]1([O-])=CC=CC=C1", AromaticType.P3_OXIDE_PLUS);
        assertAtomType("[P+]1(=O)CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("[PH+]1C=CC=CC=1", AromaticType.P3_PLUS);
    }

    @Test
    void oxygenTypes() throws InvalidSmilesException {
        assertAtomType("[O+]1=CC=CC=C1", AromaticType.O2_PLUS);
        assertAtomType("O1CC=CC=C1", AromaticType.O2);
        assertAtomType("[OH]1CC=CC=C1", AromaticType.UNKNOWN);
    }

    @Test
    void sulphurTypes() throws InvalidSmilesException {
        assertAtomType("[S+]1=CC=CC=C1", AromaticType.S2_PLUS);
        assertAtomType("S1CC=CC=C1", AromaticType.S2);
        assertAtomType("S1(=O)CC=CC=C1", AromaticType.S3_OXIDE);
        assertAtomType("[S+]1([O-])CC=CC=C1", AromaticType.S3_OXIDE_PLUS);
        assertAtomType("[S+]1(O)CC=CC=C1", AromaticType.S3_PLUS);
        assertAtomType("[S+]1([O])CC=CC=C1", AromaticType.S3_PLUS);
        assertAtomType("[SH]1CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("S=1=CCCCCC1", AromaticType.S2_CUML);
        assertAtomType("S1=CCCCCC1", AromaticType.S3);
    }

    @Test
    void seleniumTypes() throws InvalidSmilesException {
        assertAtomType("[Se+]1=CC=CC=C1", AromaticType.Se2_PLUS);
        assertAtomType("[Se]1CC=CC=C1", AromaticType.Se2);
        assertAtomType("[Se]1(=O)CC=CC=C1", AromaticType.Se3_OXIDE);
        assertAtomType("[Se+]1([O-])CC=CC=C1", AromaticType.Se3_OXIDE_PLUS);
        assertAtomType("[Se+]1(O)CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("[Se+]1([O])CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("[SeH]1CC=CC=C1", AromaticType.UNKNOWN);
        assertAtomType("[Se]1(C)=CC=CC=C1", AromaticType.Se3);
    }

    @Test
    void arsenicTypes() throws InvalidSmilesException {
        assertAtomType("[As]1=CC=CC=C1", AromaticType.As2);
        assertAtomType("[As-]1CC=CC=C1", AromaticType.As2_MINUS);
        assertAtomType("[AsH]1CC=CC=C1", AromaticType.As3);
        assertAtomType("[As]1(C)CC=CC=C1", AromaticType.As3);
        assertAtomType("[As+]1(C)=CC=CC=C1", AromaticType.As3_PLUS);
    }

    @Test
    void telluriumTypes() throws InvalidSmilesException {
        assertAtomType("[Te]1C=CC=C1", AromaticType.Te2);
        assertAtomType("[Te+]1=CC=CC=C1", AromaticType.Te2_PLUS);
    }
}
