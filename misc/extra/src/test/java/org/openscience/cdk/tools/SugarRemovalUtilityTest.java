/*
 * Copyright (c) 2025 Jonas Schaub <jonas.schaub@uni-jena.de>
 *                    Achim Zielesny <achim.zielesny@w-hs.de>
 *                    Christoph Steinbeck <christoph.steinbeck@uni-jena.de>
 *                    Maria Sorokina <>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JUnit test class for testing the functionalities of the SugarRemovalUtility
 * class, i.e. sugar moiety detection and removal.
 * <p>
 *     Identifiers starting with 'CHEMBL' refer to molecules in the
 *     <a href="https://www.ebi.ac.uk/chembl/">ChEMBL database</a>.
 *     <br>Identifiers starting with 'CNP' refer to molecules in the
 *     <a href="https://coconut.naturalproducts.net">COCONUT database</a>.
 *     <br>Identifiers starting with 'CID' refer to molecules in the
 *     <a href="https://pubchem.ncbi.nlm.nih.gov">PubChem database</a>.
 * </p>
 *
 * @author Jonas Schaub
 */
class SugarRemovalUtilityTest {
    /**
     * Example usage code for the class documentation.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void exampleUsage() throws Exception {
        //prepare test molecule
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        //COCONUT DB CNP0220816
        IAtomContainer molecule = smiPar.parseSmiles("CC1=CC(OC2OC(CO)C(O)C(O)C2O)=C2C3=C(CCC3)C(=O)OC2=C1");
        //instantiate sugar removal utility
        SugarRemovalUtility sugarRemovalUtil = new SugarRemovalUtility(SilentChemObjectBuilder.getInstance());
        //remove sugar moieties, note that this changes the molecule instance!
        boolean sugarsWereRemoved = sugarRemovalUtil.removeCircularAndLinearSugars(molecule);
        if (sugarsWereRemoved) {
            //saturate open valences where sugars were situated if needed
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
            CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(molecule);
        }
    }

    /**
     * Tests three molecules with sugar moieties for the correct removal of
     * circular sugar moieties of different sizes, discriminating terminal and
     * non-terminal sugars and detecting their glycosidic bonds.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest0() throws Exception {
        float loadFactor = 0.75f;
        int smilesBeforeAndAfterDeglycosylationMapInitCapacity = (int)(3.0f * (1.0f / loadFactor) + 2.0f);
        Map<String, String> smilesBeforeAndAfterDeglycosylationMap =
                new HashMap<>(smilesBeforeAndAfterDeglycosylationMapInitCapacity, loadFactor);
        smilesBeforeAndAfterDeglycosylationMap.put(
                //CHEMBL56258
                "CC(N)C(=O)NC(CCC(N)=O)C(=O)NOC1OC(O)C(O)C(O)C1O",
                //the circular sugar moiety connected via a glycosidic bond is removed
                "O=C(N)CCC(NC(=O)C(N)C)C(=O)NO");
        smilesBeforeAndAfterDeglycosylationMap.put(
                //CHEMBL168422
                "CCCCCC=CC=CC(O)CC=CC=CC(=O)OC1C(O)C(C2=C(O)C=C(O)C=C2CO)OC(CO)C1OC1OC(C)C(O)C(O)C1OC1OC(O)C(O)C(O)C1O",
                //two terminal circular sugars are removed, the non-terminal one remains
                "O=C(OC1C(O)C(OC(CO)C1O)C=2C(O)=CC(O)=CC2CO)C=CC=CCC(O)C=CC=CCCCCC");
        smilesBeforeAndAfterDeglycosylationMap.put(
                //made-up molecule
                "OC1OC(O)C(O)C1OC1C(OCCCCCCCCCCCCCCCCC)OC(OCCCCCCCCCCC)C(O)C1OC1C(O)C(O)C(O)OC(O)C1O",
                //the terminal heptose and the terminal furanose are removed, the non-terminal pyranose remains
                "OC1C(OCCCCCCCCCCC)OC(OCCCCCCCCCCCCCCCCC)C(O)C1O");
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical);
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectCircularSugarsOnlyWithOGlycosidicBondSetting(true);
        for (String key : smilesBeforeAndAfterDeglycosylationMap.keySet()) {
            IAtomContainer originalMolecule = smiPar.parseSmiles(key);
            Assertions.assertTrue(sugarRemovalUtil.hasCircularSugars(originalMolecule));
            Assertions.assertTrue(sugarRemovalUtil.hasCircularOrLinearSugars(originalMolecule));
            sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
            this.saturate(originalMolecule);
            String smilesAfterDeglycosylation = smiGen.create(originalMolecule);
            Assertions.assertEquals(smilesBeforeAndAfterDeglycosylationMap.get(key), smilesAfterDeglycosylation);
        }
    }

    /**
     * Tests the correct removal of a terminal circular sugar having a glycosidic
     * bond. Also tests that explicit hydrogen atoms in the input molecule do
     * not disturb the sugar removal.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest1() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectCircularSugarsOnlyWithOGlycosidicBondSetting(true);
        originalMolecule = smiPar.parseSmiles(
                //CNP0220816
                "CC1=CC(OC2OC(CO)C(O)C(O)C2O)=C2C3=C(CCC3)C(=O)OC2=C1");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //A simple example, the sugar has a glycosidic bond and is not terminal and therefore removed; the resulting
        // disconnected CH3OH is too small to keep and gets cleared away
        Assertions.assertEquals(
                "O=C1OC=2C=C(C=C(O)C2C3=C1CCC3)C",
                smilesCode);
    }

    /**
     * Test that non-terminal sugars are not removed if the respective option is
     * set and also that they are correctly removed if the setting is changed.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest2() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0151033
                "O=C(OC1C(OCC2=COC(OC(=O)CC(C)C)C3C2CC(O)C3(O)COC(=O)C)OC(CO)C(O)C1O)C=CC4=CC=C(O)C=C4");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //The sugar ring is not terminal and should not be removed, so the molecule remains unchanged
        Assertions.assertEquals(
                "O=C(OC1C(OCC2=COC(OC(=O)CC(C)C)C3C2CC(O)C3(O)COC(=O)C)OC(CO)C(O)C1O)C=CC4=CC=C(O)C=C4",
                smilesCode);
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(false);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now that all sugars are removed, the sugar ring is removed and an unconnected structure remains
        Assertions.assertEquals(
                "O=C(O)C=CC1=CC=C(O)C=C1.O=C(OCC1(O)C(O)CC2C(=COC(OC(=O)CC(C)C)C21)CO)C",
                smilesCode);
    }

    /**
     * A furanose with two attached phosphate groups is tested for the correct
     * functioning of the preservation mode and its interdependency with the
     * discrimination of terminal vs. non-terminal moieties. Also, the exocyclic
     * oxygen ratio threshold setting is tested.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest3() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0125332
                "O=P(O)(O)OCC1OC(OP(=O)(O)O)C(O)C1O");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing is removed, the sugar is terminal because the two phosphate groups are big enough to keep (5 and 6 heavy atoms)
        Assertions.assertEquals("O=P(O)(O)OCC1OC(OP(=O)(O)O)C(O)C1O", smilesCode);
        sugarRemovalUtil.setPreservationModeThresholdSetting(6);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now, one of the phosphate groups is removed because it has only 5 heavy atoms and therefore, the sugar is
        // no longer terminal and also removed
        Assertions.assertEquals("O=P(O)(O)OC", smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //CNP0125332
                "O=P(O)(O)OCC1OC(OP(=O)(O)O)C(O)C1O");
        sugarRemovalUtil.setPreservationModeThresholdSetting(7);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now, both phosphate groups are removed because they are too small and nothing remains of the molecule
        Assertions.assertEquals("", smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //CNP0125332
                "O=P(O)(O)OCC1OC(OP(=O)(O)O)C(O)C1O");
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(false);
        //back to default
        sugarRemovalUtil.setPreservationModeThresholdSetting(5);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now, also non-terminal sugars are removed, which leaves two unconnected phosphate groups in this case
        Assertions.assertEquals("O=P(O)(O)O.O=P(O)(O)OC", smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //CNP0125332
                "O=P(O)(O)OCC1OC(OP(=O)(O)O)C(O)C1O");
        sugarRemovalUtil.setExocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting(0.7);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now, the sugar ring does not have enough oxygen atoms attached to be classified as a sugar and be removed
        // (3 oxygen atoms to 5 atoms in the ring makes a ratio of 3/5 = 0.6)
        Assertions.assertEquals("O=P(O)(O)OCC1OC(OP(=O)(O)O)C(O)C1O", smilesCode);
    }

    /**
     * A molecule with a macrocycle that has a small linear sugar at its center.
     * But this is preserved using the default settings. It only gets removed
     * when allowing for cyclic linear sugars to be removed and lowering the
     * minimum size of linear sugars.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest4() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0426917
                "O=C1OC2C(CCO)CCC3(C=C4C=CCC5C(C=CC(C45)C23)CCCC(C)(CC6=CN=C(N)C=C6)CC=7C=CC=C8C(=O)C9(OC19C(=O)C87)CC(=C(C)CC%10C=%11C=CN=C%12NC(NC)CC(C%12%11)CC%10)CO)NCC");
        Assertions.assertFalse(sugarRemovalUtil.hasLinearSugars(originalMolecule));
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //There is a structure at the center of the molecule that gets detected as a linear sugar having three carbon atoms,
        // but it should not be detected as a linear sugar and not be removed because it is in parts cyclic and too small.
        Assertions.assertEquals(
                "O=C1OC2C(CCO)CCC3(C=C4C=CCC5C(C=CC(C45)C23)CCCC(C)(CC6=CN=C(N)C=C6)CC=7C=CC=C8C(=O)C9(OC19C(=O)C87)CC(=C(C)CC%10C=%11C=CN=C%12NC(NC)CC(C%12%11)CC%10)CO)NCC",
                smilesCode);
        sugarRemovalUtil.setDetectLinearSugarsInRingsSetting(true);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //now too, nothing should happen because it is still too small
        Assertions.assertEquals(
                "O=C1OC2C(CCO)CCC3(C=C4C=CCC5C(C=CC(C45)C23)CCCC(C)(CC6=CN=C(N)C=C6)CC=7C=CC=C8C(=O)C9(OC19C(=O)C87)CC(=C(C)CC%10C=%11C=CN=C%12NC(NC)CC(C%12%11)CC%10)CO)NCC",
                smilesCode);
        sugarRemovalUtil.setLinearSugarCandidateMinSizeSetting(3);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //now, the structure is big enough to be removed, and it is revealed that it is in fact terminal
        // but its removal breaks up the big ring
        Assertions.assertEquals(
                "O=C(C1=CC=CC(=C1)CC(C)(CC2=CN=C(N)C=C2)CCCC3C=CC4C5C(C=CCC35)=CC6(NCC)CCC(CCO)CC46)CCC(=C(C)CC7C=8C=CN=C9NC(NC)CC(C98)CC7)CO",
                smilesCode);
    }

    /**
     * In a prior version of the algorithm, a linear sugar was erroneously
     * detected in the molecule tested here and a methyl group removed. This
     * test was kept to ensure that now, nothing gets detected or removed here.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest5() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles("O=CCC12OC(OC1=O)C3(C)C(C)CCC3(C)C2"); //CNP0243650
        Assertions.assertFalse(sugarRemovalUtil.hasLinearSugars(originalMolecule));
        sugarRemovalUtil.setDetectLinearAcidicSugarsSetting(true);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed here
        Assertions.assertEquals("O=CCC12OC(OC1=O)C3(C)C(C)CCC3(C)C2", smilesCode);
    }

    /**
     * Example for a macrocycle that is partly made up of sugars. It is preserved
     * using the default settings but can be detected and removed when these are
     * altered.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest6() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        //this molecule contains a fused, 8-membered macrocyle that is partly made up of sugars
        originalMolecule = smiPar.parseSmiles(
                //CNP0098894
                "O=C(O)C12OC(OC3=CC=4OCC5C6=C(OC5C4C(=C3)C7=CC=CC(O)=C7)C(OC)=C(OC)C=C6CNC)(CO)C(O)C(O)(NCC1NC)C2O");
        //since linear sugars in cycles should not be removed, this must be false
        Assertions.assertFalse(sugarRemovalUtil.hasLinearSugars(originalMolecule));
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed here because the linear sugar in the macrocycle is cyclic and non-terminal
        Assertions.assertEquals(
                "O=C(O)C12OC(OC3=CC=4OCC5C6=C(OC5C4C(=C3)C7=CC=CC(O)=C7)C(OC)=C(OC)C=C6CNC)(CO)C(O)C(O)(NCC1NC)C2O",
                smilesCode);
        sugarRemovalUtil.setDetectLinearSugarsInRingsSetting(true);
        //now, the linear sugar is detected but still not removed because it is non-terminal
        Assertions.assertTrue(sugarRemovalUtil.hasLinearSugars(originalMolecule));
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed here because the linear sugar in the macrocycle is detected but not terminal
        Assertions.assertEquals(
                "O=C(O)C12OC(OC3=CC=4OCC5C6=C(OC5C4C(=C3)C7=CC=CC(O)=C7)C(OC)=C(OC)C=C6CNC)(CO)C(O)C(O)(NCC1NC)C2O",
                smilesCode);
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(false);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //now, the sugar in the macrocycle is removed and the molecule is disconnected
        Assertions.assertEquals("OC=1C=CC=C(C1)C=2C=CC=C3OCC4C5=C(OC4C32)C(OC)=C(OC)C=C5CNC.NCCNC", smilesCode);
    }

    /**
     * Testing the correct removal or preservation of non-terminal circular
     * sugars and the correctly failed detection of linear sugars inside
     * circular sugars.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest7() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        //two structures connected by a circular sugar moiety
        originalMolecule = smiPar.parseSmiles(
                //CNP0436852
                "O=C(O)C1OC(OC=2C=CC=3C(=O)[C-](C=[O+]C3C2)C4=CC=C(O)C=C4)C(O)(CNCC(CC=5C=NCC5)C(C)C)C(O)C1O");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed here because the circular sugar is not terminal
        Assertions.assertEquals(
                "O=C(O)C1OC(OC=2C=CC=3C(=O)[C-](C=[O+]C3C2)C4=CC=C(O)C=C4)C(O)(CNCC(CC=5C=NCC5)C(C)C)C(O)C1O",
                smilesCode);
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(false);
        sugarRemovalUtil.setDetectLinearSugarsInRingsSetting(true);
        sugarRemovalUtil.removeLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed because only linear sugars are removed and there are none!
        Assertions.assertEquals(
                "O=C(O)C1OC(OC=2C=CC=3C(=O)[C-](C=[O+]C3C2)C4=CC=C(O)C=C4)C(O)(CNCC(CC=5C=NCC5)C(C)C)C(O)C1O",
                smilesCode);
        sugarRemovalUtil.removeCircularSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now, the circular sugar is removed
        Assertions.assertEquals(
                "O=C1C=2C=CC(O)=CC2[O+]=C[C-]1C3=CC=C(O)C=C3.N1=CC(=CC1)CC(CNC)C(C)C",
                smilesCode);
    }

    /**
     * In a prior version of the algorithm, a linear sugar was erroneously
     * detected in the molecule tested here and removed. This test was kept to
     * ensure that now, nothing gets detected or removed here.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest8() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles("O=C1OC(C2=COC=C2)CC3(C)C1CCC4(C)C3C5OC(=O)C4(O)C=C5"); //CNP0235814
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed here although there might be a match for the linear sugar patterns
        Assertions.assertEquals("O=C1OC(C2=COC=C2)CC3(C)C1CCC4(C)C3C5OC(=O)C4(O)C=C5", smilesCode);
    }

    /**
     * In a prior version of the algorithm, a linear sugar was erroneously
     * detected in the molecule tested here and removed. This test was kept to
     * ensure that now, nothing gets detected or removed here.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest9() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0296940
                "O=C1OC2CC3(OC4(O)C(CC5(OC45C(=O)OC)CCCCCCCCCCCCCCCC)C2(O3)C1)CCCCCCCCCCCCCCCC");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed here although there is a small match for the linear sugar patterns
        Assertions.assertEquals(
                "O=C1OC2CC3(OC4(O)C(CC5(OC45C(=O)OC)CCCCCCCCCCCCCCCC)C2(O3)C1)CCCCCCCCCCCCCCCC",
                smilesCode);
    }

    /**
     * Here, an interesting molecule is tested that has two macrocycles that
     * contain in principle a circular sugar which is not isolated. Therefore,
     * the sugar is not recognized as a circular one by the SRU. But the linear
     * sugar removal can be used to deal with such cases after minor adjustment
     * of the default settings.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest10() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        //Interesting case because there are two macrocycles containing in principle a circular sugar that is not isolated
        originalMolecule = smiPar.parseSmiles(
                //CNP0419027
                "O=C1C2=CC=CC3=C2CN1CC(=O)C4=C(O)C5=C6OC7OC(COC(C=CC6=C(OC)C8=C5C=9C(=CC%10CCCC%10C49)CC8)C%11=CNC=%12C=CC(=CC%12%11)CNC)C(O)C(OC#CC3)C7(O)CO");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed here although there is a match for the linear sugar patterns in the said
        // non-isolated sugar cycle in the macrocycle
        Assertions.assertEquals(
                "O=C1C2=CC=CC3=C2CN1CC(=O)C4=C(O)C5=C6OC7OC(COC(C=CC6=C(OC)C8=C5C=9C(=CC%10CCCC%10C49)CC8)C%11=CNC=%12C=CC(=CC%12%11)CNC)C(O)C(OC#CC3)C7(O)CO",
                smilesCode);
        sugarRemovalUtil.setDetectLinearSugarsInRingsSetting(true);
        sugarRemovalUtil.removeLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now, the sugar cycle in the macrocyle is removed because it matches the linear sugar patterns;
        // the macrocycles are broken but the molecule does not get disconnected, so the moiety was terminal
        Assertions.assertEquals(
                "O=C1C2=CC=CC(=C2CN1CC(=O)C3=C(O)C=4C=C(C=CCC5=CNC=6C=CC(=CC65)CNC)C(OC)=C7C4C=8C(=CC9CCCC9C38)CC7)CC#C",
                smilesCode);
    }

    /**
     * Tests the correct removal of two circular sugars and a linear acidic sugar
     * (all terminal) from this molecule.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest11() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectLinearAcidicSugarsSetting(true);
        originalMolecule = smiPar.parseSmiles(
                //CNP0218440
                "O=C(O)CC(OC1OC(CO)C(O)C(O)C1O)(C)CC(=O)OCC=CC2=CC(OC)=C(OC3OC(CO)C(O)C(O)C3O)C(OC)=C2");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //The two circular sugar moieties and one connected linear sugar (a sugar acid) are removed (all terminal)
        Assertions.assertEquals("OC1=C(OC)C=C(C=CC)C=C1OC", smilesCode);
    }

    /**
     * Tests the correct removal of one circular and one linear sugar moiety
     * from this molecule. The latter being a sugar acid.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest12() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectLinearAcidicSugarsSetting(true);
        originalMolecule = smiPar.parseSmiles(
                //CNP0120327
                "O=C(O)CC(O)(C)CC(=O)OCC1=CC=C(OC2OC(CO)C(O)C(O)C2O)C=C1");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //One circular and one linear sugar moiety (a sugar acid) are removed
        Assertions.assertEquals("OC1=CC=C(C=C1)C", smilesCode);
    }

    /**
     * The tested molecule has two linear acidic sugar moieties, one terminal
     * and one non-terminal. The correct removal of the terminal one is tested.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest13() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectLinearAcidicSugarsSetting(true);
        originalMolecule = smiPar.parseSmiles(
                //CNP0420141
                "O=C(O)CC(C(=O)O)C(OCC1C(=C)CCC2C(C)(COC(=O)C(CC(=O)O)C(OCC3C(=C)CCC4C(C)(C)CCCC34C)C(=O)OC)CCCC12C)C(=O)OC");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //One linear sugar moiety is removed (a sugar acid), the other one is not because it is terminal;
        // no other changes are done to the molecule
        Assertions.assertEquals(
                "O=C(O)CC(C(=O)OCC1(C)CCCC2(C)C(C(=C)CCC12)C)C(OCC3C(=C)CCC4C(C)(C)CCCC34C)C(=O)OC",
                smilesCode);
    }

    /**
     * Tests five molecules that illustrate the recurrent removal of terminal
     * linear and circular sugars. They all have circular sugar moieties that
     * only become terminal after the removal of linear sugar moieties (or
     * vice-versa). To ensure the correct removal of all terminal moieties in
     * such cases, the method removeCircularAndLinearSugars() or
     * removeAndReturnCircularAndLinearSugars() does multiple iterations of
     * detection and removal of linear and circular sugars, internally, if only
     * terminal moieties should be removed.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest14() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectLinearAcidicSugarsSetting(true);
        originalMolecule = smiPar.parseSmiles(
                //CNP0260973
                "O=C(O)CC(O)(C)CC(=O)OC1COC(OC2C(O)C(OC(OC3C(O)C(O)C(OC4CC5CCC6C(CCC7(C)C6CC8OC9(OCC(C)CC9)C(C)C87)C5(C)CC4O)OC3CO)C2OC%10OC(CO)C(O)C(O)C%10O)CO)C(O)C1O");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //All sugars get removed although some circular sugars only become terminal after the removal of the linear one,
        // a sugar acid (that was a problem before)
        Assertions.assertEquals("OC1CC2CCC3C(CCC4(C)C3CC5OC6(OCC(C)CC6)C(C)C54)C2(C)CC1O", smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //CNP0250097
                "O=C(O)CC(O)(C)CC(=O)OCC1OC(OCC2OC(OC(=O)C34CCC(C)(C)CC4C5=CCC6C7(C)CCC(O)C(C(=O)OC8OC(CO)C(O)C(O)C8O)(C)C7CCC6(C)C5(C)CC3)C(O)C(OC9OC(CO)C(O)C(O)C9O)C2O)C(OC%10OC(CO)C(O)C(O)C%10O)C(O)C1O");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //All sugars get removed although some circular sugars only become terminal after the removal of the linear one,
        // a sugar acid (that was a problem before)
        Assertions.assertEquals("O=C(O)C1(C)C(O)CCC2(C)C1CCC3(C)C2CC=C4C5CC(C)(C)CCC5(C(=O)O)CCC43C", smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //CNP0428924
                "O=C(O)CC(O)(C)CC(=O)OC1C(O)C(OC2C3=C(O)C(=CC=C3OC2C(=C)CO)C(=O)C)OC(CO)C1O");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //All sugars get removed although the circular sugar only becomes terminal after the removal of the linear one,
        // a sugar acid (that was a problem before)
        Assertions.assertEquals("O=C(C1=CC=C2OC(C(=C)CO)C(O)C2=C1O)C", smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //CNP0428862
                "O=C(O)CC(O)(C)CC(=O)OCC1OC(C=2C(O)=CC(O)=C3C(=O)C=C(OC32)C=4C=CC(O)=C(O)C4)C(O)C(O)C1O");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //All sugars get removed although the circular sugar only become terminal after the removal of the linear one,
        // a sugar acid (that was a problem before)
        Assertions.assertEquals("O=C1C=C(OC=2C=C(O)C=C(O)C12)C=3C=CC(O)=C(O)C3", smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //CNP0063672
                "O=C(O)CC(O)(C)CC(=O)OCC1(O)COC(OC2C(O)C(OC(C)C2OC3OCC(O)C(OC4OCC(O)C(O)C4O)C3O)OC5C(OC(=O)C67CCC(C)(C)CC7C8=CCC9C%10(C)CC(O)C(OC%11OC(CO)C(O)C(O)C%11O)C(C(=O)O)(C)C%10CCC9(C)C8(CO)CC6)OC(C)C(OC(=O)C=CC%12=CC(OC)=C(OC)C(OC)=C%12)C5OC%13OC(C)C(O)C(O)C%13O)C1O");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //All sugars get removed although some circular sugars only become terminal after the removal of the linear one,
        // a sugar acid (that was a problem before), only one non-terminal sugar remains
        Assertions.assertEquals("O=C(OC1C(O)C(O)C(OC(=O)C23CCC(C)(C)CC3C4=CCC5C6(C)CC(O)C(O)C(C(=O)O)(C)C6CCC5(C)C4(CO)CC2)OC1C)C=CC7=CC(OC)=C(OC)C(OC)=C7", smilesCode);
    }

    /**
     * This test illustrates that molecules that are in fact sugar molecules are
     * completely removed by the algorithm. It also illustrates the exemption
     * that single-cycle sugar molecules do not need to have a glycosidic bond to be
     * detected and removed, even if the respective option is turned on. Simply
     * because there is no other structure in the molecule, they can bind to
     * via a glycosidic bond.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest15() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles("O=C(O)C1OC(O)C(O)C(O)C1O"); //CNP0171089
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //This molecule is a sugar and should be completely removed
        Assertions.assertEquals("", smilesCode);
        sugarRemovalUtil.setDetectCircularSugarsOnlyWithOGlycosidicBondSetting(true);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Even with detection of glycosidic bonds, this sugar ring should be removed because there is no other structure
        // it can bind to via a glycosidic bond
        Assertions.assertEquals("", smilesCode);
    }

    /**
     * Tests the correct removal of a single terminal linear sugar moiety in
     * two cases.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest16() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectLinearAcidicSugarsSetting(true);
        originalMolecule = smiPar.parseSmiles("O=C(O)CC(C(=O)O)C(OCC1C(=C)CCC2C(C)(C)CCCC12C)C(=O)O"); //CNP0321675
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //The linear sugar moiety (a sugar acid) is removed
        Assertions.assertEquals("C=C1CCC2C(C)(C)CCCC2(C)C1C", smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //CNP0032409
                "O=C1C=C(OC=2C=C3OC(C)(CCC4CNC(=O)C4)C(OOCC(O)C(O)C(O)C(O)CO)CC3=CC12)C");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //The linear sugar moiety (a hexose attached via a peroxide bond) is removed
        Assertions.assertEquals("O=C1C=C(OC=2C=C3OC(C)(CCC4CNC(=O)C4)C(O)CC3=CC12)C", smilesCode);
    }

    /**
     * Tests the correct removal of a single terminal linear sugar moiety from
     * a molecule. The sugar is a hexose connected to the core via a peroxide bond.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest17() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0163522
                "O=C1C=C(OC=2C1=CC3=C(OC(C)(C)C(OOCC(O)C(O)C(O)C(O)CO)C3)C2[N+]=4C=C5N=CC=C5C4CC)C");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //The linear sugar moiety is removed
        Assertions.assertEquals("O=C1C=C(OC=2C1=CC3=C(OC(C)(C)C(O)C3)C2[N+]=4C=C5N=CC=C5C4CC)C", smilesCode);
    }

    /**
     * Tests two example molecules where a circular sugar moiety is not connected
     * to the core structure via an O-glycosidic bond. Using default settings,
     * they are removed anyway. But if the respective setting is activated,
     * they are preserved.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest18() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectCircularSugarsOnlyWithOGlycosidicBondSetting(true);
        originalMolecule = smiPar.parseSmiles(
                //CNP0162697
                "O=C1C=C(OC2=CC(OC(=O)C3OC(O)C(O)C(O)C3O)=C(O)C(O)=C12)C=4C=CC(O)=CC4");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //The sugar moiety is NOT connected to the core structure via a glycosidic bond, so it is not removed
        Assertions.assertEquals(
                "O=C1C=C(OC2=CC(OC(=O)C3OC(O)C(O)C(O)C3O)=C(O)C(O)=C12)C=4C=CC(O)=CC4",
                smilesCode);
        sugarRemovalUtil.setDetectCircularSugarsOnlyWithOGlycosidicBondSetting(false);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now that this setting is changed, the sugar moiety is removed
        //note: chemically, the carboxy group should be part of the sugar, not of the core
        Assertions.assertEquals(
                "O=COC=1C=C2OC(=CC(=O)C2=C(O)C1O)C=3C=CC(O)=CC3",
                smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //CNP0428862
                "O=C(O)CC(O)(C)CC(=O)OCC1OC(C=2C(O)=CC(O)=C3C(=O)C=C(OC32)C=4C=CC(O)=C(O)C4)C(O)C(O)C1O");
        sugarRemovalUtil.setDetectCircularSugarsOnlyWithOGlycosidicBondSetting(true);
        sugarRemovalUtil.setDetectLinearAcidicSugarsSetting(true);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //The circular sugar moiety is NOT connected to the core structure via a glycosidic bond, so it is not removed;
        // also, the removal of linear sugars leaves the circular sugar untouched
        Assertions.assertEquals("O=C1C=C(OC=2C1=C(O)C=C(O)C2C3OC(CO)C(O)C(O)C3O)C=4C=CC(O)=C(O)C4", smilesCode);
        sugarRemovalUtil.setDetectCircularSugarsOnlyWithOGlycosidicBondSetting(false);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now that this setting is changed, the sugar moiety is removed
        Assertions.assertEquals("O=C1C=C(OC=2C=C(O)C=C(O)C12)C=3C=CC(O)=C(O)C3", smilesCode);
    }

    /**
     * The correct removal of linear acidic sugars is tested. Also, the tested
     * molecule caused an exception before because a linear sugar candidate got
     * removed while too small structures were cleared away. This led to the
     * addition of the second condition for being terminal: that no structures
     * belonging to other sugar candidates must get disconnected and removed in
     * the removal of the investigated moiety.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest19() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectLinearAcidicSugarsSetting(true);
        originalMolecule = smiPar.parseSmiles("O=C(O)CC(O)(C(=O)O)C(C(=O)O)CCCCCCCCCCCCCC"); //CNP0231882
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Only the aliphatic chain remains (in its full length)
        Assertions.assertEquals("CCCCCCCCCCCCCC", smilesCode);
    }

    /**
     * The tested molecule contains 7 circular sugar moieties and 2 linear ones
     * (acidic). All of them are terminal and correctly removed.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest20() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectLinearAcidicSugarsSetting(true);
        originalMolecule = smiPar.parseSmiles(
                //CNP0097028
                "O=CC1(C)C(OC2OC(C(=O)O)C(O)C(OC3OCC(O)C(O)C3O)C2OC4OC(CO)C(O)C(O)C4O)CCC5(C)C6CC=C7C8CC(C)(C)CCC8(C(=O)OC9OC(C)C(OC(=O)CC(O)CC(OC(=O)CC(O)CC(OC%10OC(CO)C(O)C%10O)C(C)CC)C(C)CC)C(O)C9OC%11OC(C)C(OC%12OCC(O)C(O)C%12O)C(O)C%11O)C(O)CC7(C)C6(C)CCC15");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Only the core structure remains
        Assertions.assertEquals(
                "O=CC1(C)C(O)CCC2(C)C1CCC3(C)C2CC=C4C5CC(C)(C)CCC5(C(=O)O)C(O)CC43C",
                smilesCode);
    }

    /**
     * Here, an interesting molecule is tested that has a macrocycle that
     * contains in principle a circular sugar which is not isolated. Therefore,
     * the sugar is not recognized as a circular one by the SRU. But the linear
     * sugar removal can be used to deal with such cases after minor adjustment
     * of the default settings.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest21() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0416117
                "O=C(OCC)CCC1=CC=2C=COC2C=3OCCNCC4(SSCC5(OC(OC31)C(O)C(O)C5O)CO)CCCC4");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //the sugar ring within the macrocycle can be matched by the linear sugar patterns but should not be removed
        Assertions.assertEquals("O=C(OCC)CCC1=CC=2C=COC2C=3OCCNCC4(SSCC5(OC(OC31)C(O)C(O)C5O)CO)CCCC4", smilesCode);
        sugarRemovalUtil.setDetectLinearSugarsInRingsSetting(true);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now, the (circular) sugar moiety within the macrocyle is removed
        Assertions.assertEquals("O=C(OCC)CCC=1C=C(OCCNCC2(SSC)CCCC2)C=3OC=CC3C1", smilesCode);
    }

    /**
     * Tested is a molecule that contains a macrocycle that is in part made of
     * linear sugars. It is preserved using default settings but can be detected
     * and removed after some adjustments to the settings.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest22() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0230833
                "O=C1OC2CC3(OC(CCC3)CC(O)CC)OC(CC4(O)OC(C)(C)CC4C=CCCCCCC(O)(C)C(O)C(OC5OC(C)C(N(C)C)CC5)C(O)C(C)C(O)C(O)(C=C1)C)C2C");
        sugarRemovalUtil.removeLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing gets removed
        Assertions.assertEquals(
                "O=C1OC2CC3(OC(CCC3)CC(O)CC)OC(CC4(O)OC(C)(C)CC4C=CCCCCCC(O)(C)C(O)C(OC5OC(C)C(N(C)C)CC5)C(O)C(C)C(O)C(O)(C=C1)C)C2C",
                smilesCode);
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(false);
        sugarRemovalUtil.removeLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Again, nothing is removed because the linear sugar is contained in a (macro-)cycle
        Assertions.assertEquals(
                "O=C1OC2CC3(OC(CCC3)CC(O)CC)OC(CC4(O)OC(C)(C)CC4C=CCCCCCC(O)(C)C(O)C(OC5OC(C)C(N(C)C)CC5)C(O)C(C)C(O)C(O)(C=C1)C)C2C",
                smilesCode);
        sugarRemovalUtil.setDetectLinearSugarsInRingsSetting(true);
        sugarRemovalUtil.removeLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now, the linear sugar (a small, 4-carbon-membered one) gets removed and disconnects the molecule
        Assertions.assertEquals(
                "O=C(OC1CC2(OC(CCC2)CC(O)CC)OC(CC3(O)OC(C)(C)CC3C=CCCCCC)C1C)C=CC(O)(C)C(O)CC.O1CCCC(N(C)C)C1C",
                smilesCode);
    }

    /**
     * In a prior version of the algorithm, a linear sugar was erroneously
     * detected in the molecule tested here that is completely made up of
     * circular sugars. This test was kept to ensure that now, nothing gets
     * detected or removed here.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest23() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectLinearSugarsInRingsSetting(true);
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(false);
        sugarRemovalUtil.setDetectLinearAcidicSugarsSetting(true);
        originalMolecule = smiPar.parseSmiles(
                //CNP0189711
                "O=C(NC1C(O)OC(CO)C(O)C1OC2OC(CO)C(OC)C(O)C2OC3OC(C)C(O)C(O)C3OC)C");
        sugarRemovalUtil.removeLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed because this molecule has only circular sugars (was a problem before)
        Assertions.assertEquals(
                "O=C(NC1C(O)OC(CO)C(O)C1OC2OC(CO)C(OC)C(O)C2OC3OC(C)C(O)C(O)C3OC)C",
                smilesCode);
    }

    /**
     * The molecule tested here is imaginary and created for demonstration
     * purposes. It contains two circular and two linear sugars (sugar acids),
     * in both cases one terminal and one non-terminal. Using different
     * settings and methods, these are removed in different combinations.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest24() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectCircularSugarsOnlyWithOGlycosidicBondSetting(true);
        sugarRemovalUtil.setDetectLinearAcidicSugarsSetting(true);
        //a molecule containing 2 circular sugars and 2 linear sugars (sugar acids), in both cases 1 terminal and 1 non-terminal
        originalMolecule = smiPar.parseSmiles(
                //imaginary molecule created for demonstration purposes
                "O=C(O)CC(C)(O)CC(=O)OCc1ccc(cc1)OC1C(CO)OC(OC(C(=O)OCc2ccc(OC3OC(CO)CC(O)C3O)cc2)C(O)(CC(C)C)C(=O)OC2CCc3cc4cc(O)c(C)c(O)c4c(O)c3C2=O)C(O)C1O");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //the terminal linear and circular sugar moieties are removed
        Assertions.assertEquals(
                "O=C(OCC1=CC=C(O)C=C1)C(OC2OC(CO)C(OC3=CC=C(C=C3)C)C(O)C2O)C(O)(C(=O)OC4C(=O)C5=C(O)C6=C(O)C(=C(O)C=C6C=C5CC4)C)CC(C)C",
                smilesCode);
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(false);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //all 4 sugar moieties are removed, the two non-terminal sugar moieties included
        Assertions.assertEquals(
                "O=C1C2=C(O)C3=C(O)C(=C(O)C=C3C=C2CCC1)C.OC1=CC=C(C=C1)C.OC1=CC=C(C=C1)C",
                smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //imaginary molecule created for demonstration purposes
                "O=C(O)CC(C)(O)CC(=O)OCc1ccc(cc1)OC1C(CO)OC(OC(C(=O)OCc2ccc(OC3OC(CO)CC(O)C3O)cc2)C(O)(CC(C)C)C(=O)OC2CCc3cc4cc(O)c(C)c(O)c4c(O)c3C2=O)C(O)C1O");
        //back to default setting
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(true);
        sugarRemovalUtil.removeLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //only the terminal linear sugar is removed
        Assertions.assertEquals(
                "O=C(OCC1=CC=C(OC2OC(CO)CC(O)C2O)C=C1)C(OC3OC(CO)C(OC4=CC=C(C=C4)C)C(O)C3O)C(O)(C(=O)OC5C(=O)C6=C(O)C7=C(O)C(=C(O)C=C7C=C6CC5)C)CC(C)C",
                smilesCode);
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(false);
        sugarRemovalUtil.removeLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //the two linear sugar moieties are removed, disconnecting the molecule
        Assertions.assertEquals(
                "O=C1C2=C(O)C3=C(O)C(=C(O)C=C3C=C2CCC1)C.OCC1OC(OC2=CC=C(C=C2)C)C(O)C(O)C1.OCC1OC(O)C(O)C(O)C1OC2=CC=C(C=C2)C",
                smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //imaginary molecule created for demonstration purposes
                "O=C(O)CC(C)(O)CC(=O)OCc1ccc(cc1)OC1C(CO)OC(OC(C(=O)OCc2ccc(OC3OC(CO)CC(O)C3O)cc2)C(O)(CC(C)C)C(=O)OC2CCc3cc4cc(O)c(C)c(O)c4c(O)c3C2=O)C(O)C1O");
        sugarRemovalUtil.removeCircularSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //the two circular sugar moieties are removed, disconnecting the molecule
        Assertions.assertEquals(
                "O=C(O)CC(O)(C)CC(=O)OCC1=CC=C(O)C=C1.O=C(OCC1=CC=C(O)C=C1)C(O)C(O)(C(=O)OC2C(=O)C3=C(O)C4=C(O)C(=C(O)C=C4C=C3CC2)C)CC(C)C",
                smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //imaginary molecule created for demonstration purposes
                "O=C(O)CC(C)(O)CC(=O)OCc1ccc(cc1)OC1C(CO)OC(OC(C(=O)OCc2ccc(OC3OC(CO)CC(O)C3O)cc2)C(O)(CC(C)C)C(=O)OC2CCc3cc4cc(O)c(C)c(O)c4c(O)c3C2=O)C(O)C1O");
        //back to default setting
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(true);
        sugarRemovalUtil.removeCircularSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //only the terminal circular sugar moiety is removed
        Assertions.assertEquals(
                "O=C(O)CC(O)(C)CC(=O)OCC1=CC=C(OC2C(O)C(O)C(OC(C(=O)OCC3=CC=C(O)C=C3)C(O)(C(=O)OC4C(=O)C5=C(O)C6=C(O)C(=C(O)C=C6C=C5CC4)C)CC(C)C)OC2CO)C=C1",
                smilesCode);
    }

    /**
     * Here, three molecules are tested that contain sugar moieties in the
     * form of spiro rings. The discovery of these molecules led to the
     * introduction of the option to include or exclude spiro rings from the
     * circular sugar detection.
     * Also, the preservation of the spiro atom was added because of them.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest25() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();

        originalMolecule = smiPar.parseSmiles("OC1(OCCC21OC3(O)CCOC3(O2)C)C"); //CNP0017501
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed because the sugar is a spiro ring (before, these were not filtered)
        Assertions.assertEquals("OC1(OCCC21OC3(O)CCOC3(O2)C)C", smilesCode);
        sugarRemovalUtil.setDetectSpiroRingsAsCircularSugarsSetting(true);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now that the spiro rings are detected, the sugar ring is removed; BUT the adjacent ring is left intact!
        Assertions.assertEquals("OC12OCOC2(OCC1)C", smilesCode);
        //back to default for the other tests
        sugarRemovalUtil.setDetectSpiroRingsAsCircularSugarsSetting(false);

        originalMolecule = smiPar.parseSmiles("OCC1OC2(OC3C(O)C(OC3(OC2)CO)CO)C(O)C1O"); //CNP0306895
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed because the sugar is a spiro ring (before, these were not filtered)
        Assertions.assertEquals("OCC1OC2(OC3C(O)C(OC3(OC2)CO)CO)C(O)C1O", smilesCode);
        sugarRemovalUtil.setDetectSpiroRingsAsCircularSugarsSetting(true);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now that the spiro rings are detected, the sugar ring is removed; BUT the adjacent ring is left intact!
        Assertions.assertEquals("OCC1OC2(OCCOC2C1O)CO", smilesCode);
        //back to default for the other tests
        sugarRemovalUtil.setDetectSpiroRingsAsCircularSugarsSetting(false);

        originalMolecule = smiPar.parseSmiles("OCC1OC2(OCC3OC(OC2)(CO)C(O)C3O)C(O)C1O"); //CNP0341963
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed because the sugar is a spiro ring (before, these were not filtered)
        Assertions.assertEquals("OCC1OC2(OCC3OC(OC2)(CO)C(O)C3O)C(O)C1O", smilesCode);
        sugarRemovalUtil.setDetectSpiroRingsAsCircularSugarsSetting(true);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now that the spiro rings are detected, the sugar ring is removed; BUT the adjacent ring is left intact!
        Assertions.assertEquals("OCC12OCCOCC(O1)C(O)C2O", smilesCode);
        //back to default for the other tests (if some are added later...)
        sugarRemovalUtil.setDetectSpiroRingsAsCircularSugarsSetting(false);
    }

    /**
     * Tested here are two molecules that consist entirely of one circular and
     * one linear sugar moiety, respectively. Their correct detection and
     * removal ist tested.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest26() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();

        //A molecule consisting entirely of one circular and one linear sugar
        originalMolecule = smiPar.parseSmiles("OCC(O)C(O)C(O)C(O)C1OC(CO)C(O)C(O)C1O"); //CNP0119227
        Assertions.assertEquals(2, sugarRemovalUtil.getNumberOfCircularAndLinearSugars(originalMolecule));
        Assertions.assertEquals(1, sugarRemovalUtil.getNumberOfCircularSugars(originalMolecule));
        Assertions.assertEquals(1, sugarRemovalUtil.getNumberOfLinearSugars(originalMolecule));
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Completely removed
        Assertions.assertEquals("", smilesCode);

        originalMolecule = smiPar.parseSmiles("OCC(O)C(O)C(O)C(O)C1OC(CO)C(O)C(O)C1O"); //CNP0119227
        sugarRemovalUtil.removeCircularSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Only the linear sugar remains
        Assertions.assertEquals("OCC(O)C(O)C(O)CO", smilesCode);

        originalMolecule = smiPar.parseSmiles("OCC(O)C(O)C(O)C(O)C1OC(CO)C(O)C(O)C1O"); //CNP0119227
        sugarRemovalUtil.removeLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Only the circular sugar remains
        Assertions.assertEquals("OCC1OCC(O)C(O)C1O", smilesCode);

        //A molecule consisting entirely of one circular and one linear sugar
        originalMolecule = smiPar.parseSmiles("OCC(O)C(O)C(O)C(O)C(O)C1OC(O)C(O)C(O)C1N"); //CNP0183311
        Assertions.assertEquals(2, sugarRemovalUtil.getNumberOfCircularAndLinearSugars(originalMolecule));
        Assertions.assertEquals(1, sugarRemovalUtil.getNumberOfCircularSugars(originalMolecule));
        Assertions.assertEquals(1, sugarRemovalUtil.getNumberOfLinearSugars(originalMolecule));
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Completely removed
        Assertions.assertEquals("", smilesCode);

        originalMolecule = smiPar.parseSmiles("OCC(O)C(O)C(O)C(O)C(O)C1OC(O)C(O)C(O)C1N"); //CNP0183311
        sugarRemovalUtil.removeCircularSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Only the linear sugar remains
        Assertions.assertEquals("OCC(O)C(O)C(O)C(O)CO", smilesCode);

        originalMolecule = smiPar.parseSmiles("OCC(O)C(O)C(O)C(O)C(O)C1OC(O)C(O)C(O)C1N"); //CNP0183311
        sugarRemovalUtil.removeLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Only the circular sugar remains
        Assertions.assertEquals("OC1OCC(N)C(O)C1O", smilesCode);
    }

    /**
     * Tested here is the special case of a macrocycle that is completely made
     * up of circular sugars. Since all the sugar rings are fused, they are
     * undetectable by the algorithm for circular sugar detection. But the
     * linear sugar detection algorithm could in theory be used to detect and
     * remove the sugar if the settings are adjusted accordingly.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest27() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0346956
                "OCC1OC2OC3C(O)C(O)C(OC3CO)OC4C(O)C(O)C(OC4CO)OC5C(O)C(O)C(OC5CO)OC6C(O)C(O)C(OC6CO)OC7C(O)C(O)C(OC7CO)OC1C(O)C2O");
        sugarRemovalUtil.removeCircularSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //The circular sugars do not get recognized as such because they are fused in the macrocycle
        Assertions.assertEquals(
                "OCC1OC2OC3C(O)C(O)C(OC3CO)OC4C(O)C(O)C(OC4CO)OC5C(O)C(O)C(OC5CO)OC6C(O)C(O)C(OC6CO)OC7C(O)C(O)C(OC7CO)OC1C(O)C2O",
                smilesCode);
        sugarRemovalUtil.setDetectLinearSugarsInRingsSetting(true);
        sugarRemovalUtil.removeLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //But the removal of linear sugars (also in cycles) also does not work because the algorithm to split ether bonds
        // (the glycosidic bonds between the sugars) protects bonds in cycles and therefore, the whole molecule gets returned
        // as one linear candidate that is too big
        Assertions.assertEquals(
                "OCC1OC2OC3C(O)C(O)C(OC3CO)OC4C(O)C(O)C(OC4CO)OC5C(O)C(O)C(OC5CO)OC6C(O)C(O)C(OC6CO)OC7C(O)C(O)C(OC7CO)OC1C(O)C2O",
                smilesCode);
        sugarRemovalUtil.setLinearSugarCandidateMaxSizeSetting(36);
        sugarRemovalUtil.removeLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Only now, with the linear sugar maximum size vastly increased, the sugar molecule can be removed
        Assertions.assertEquals("", smilesCode);
    }

    /**
     * This test ensures that wildcard symbols (*) in the input SMILES strings
     * that are parsed into molecules do not disturb the sugar detection and
     * removal.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest28() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles("O(*)C1OC(CN)CCC1N");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed because the cycle has not enough oxygen atoms attached
        Assertions.assertEquals("*OC1OC(CN)CCC1N", smilesCode);
    }

    /**
     * The tested molecule has a terminal circular sugar moiety and a terminal
     * linear sugar moiety. Both are correctly removed.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest29() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0254143
                "O=C(O)C1=CC(O)C(O)C(OC(=O)C2C(=CC=3C=C(O)C(OC4OC(CO)C(O)C(O)C4O)=CC3C2C5=CC=C(O)C(O)=C5)C(=O)OCC(O)C(O)C(O)C(O)C(O)CO)C1");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Both, the terminal circular sugar and the terminal linear sugar are removed
        Assertions.assertEquals(
                "O=CC1=CC=2C=C(O)C(O)=CC2C(C3=CC=C(O)C(O)=C3)C1C(=O)OC4CC(=CC(O)C4O)C(=O)O",
                smilesCode);
    }

    /**
     * In the tested molecule, there is a terminal circular sugar and a linear
     * sugar that only becomes terminal after the circular sugar moiety is
     * removed. It is also just big enough to reach the default minimum size for
     * linear sugars. Both moieties are correctly removed using the default settings.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest30() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0320551
                "O=C1N=C2C(=NC=3C=C(C(=CC3N2CC(O)C(O)C(O)COC4OC(CO)C(O)C(O)C4O)C)C)C(=O)N1");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //The circular sugar and the linear sugar are removed
        Assertions.assertEquals("O=C1N=C2C(=NC=3C=C(C(=CC3N2C)C)C)C(=O)N1", smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //CNP0320551
                "O=C1N=C2C(=NC=3C=C(C(=CC3N2CC(O)C(O)C(O)COC4OC(CO)C(O)C(O)C4O)C)C)C(=O)N1");
        sugarRemovalUtil.setLinearSugarCandidateMinSizeSetting(5);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //The linear sugar is not removed because it is too small
        Assertions.assertEquals("O=C1N=C2C(=NC=3C=C(C(=CC3N2CC(O)C(O)C(O)CO)C)C)C(=O)N1", smilesCode);
    }

    /**
     * the tested molecule has one terminal and one non-terminal linear sugar
     * moiety. They can both be removed if the settings are adjusted.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest31() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0321545
                "O=C(OCC(O)C(O)C(O)C(O)COC1=C(OC2=CC(O)=CC(OCC(O)C(O)C(O)C(O)CO)=C2C1)C=3C=C(O)C(O)=C(O)C3)C=CC4=CC=C(O)C=C4");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Only the one terminal linear sugar is removed
        Assertions.assertEquals(
                "O=C(OCC(O)C(O)C(O)C(O)COC1=C(OC2=CC(O)=CC=C2C1)C=3C=C(O)C(O)=C(O)C3)C=CC4=CC=C(O)C=C4",
                smilesCode);
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(false);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now, both linear sugar moieties are removed
        Assertions.assertEquals(
                "O=CC=CC1=CC=C(O)C=C1.OC1=CC=C2C(OC(=CC2)C=3C=C(O)C(O)=C(O)C3)=C1",
                smilesCode);
    }

    /**
     * The tested molecule has two terminal circular sugar moieties and a
     * terminal linear sugar moiety. All are correctly removed.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest32() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0149497
                "O=C(OCC(O)C(O)C(O)CO)C1(C)CC2=C3C=CC4C5(C)CCC(OC6OC(C)C(O)C(OC7OC(CO)C(O)C(O)C7O)C6O)C(C)(CO)C5CCC4(C)C3(C)CC(O)C2(CO)CC1");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //the two terminal circular sugars and the terminal linear sugar are removed
        Assertions.assertEquals(
                "O=CC1(C)CC2=C3C=CC4C5(C)CCC(O)C(C)(CO)C5CCC4(C)C3(C)CC(O)C2(CO)CC1",
                smilesCode);
    }

    /**
     * The tested molecule has 14 terminal circular sugar moieties. All are
     * correctly removed.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void specificTest33() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0083402
                "CC(CCC=C(C)CCC=C(C)CCC=C(C)CCC=C(C)C)CCOP(=O)(O)OP(=O)(O)OC1C(C(C(C(O1)CO)OC2C(C(C(C(O2)CO)OC3C(C(C(C(O3)COC4C(C(C(C(O4)COC5C(C(C(C(O5)CO)OC6C(C(C(C(O6)CO)O)O)O)O)O)O)OC7C(C(C(C(O7)CO)OC8C(C(C(C(O8)CO)O)O)O)O)O)O)O)OC9C(C(C(C(O9)CO)O)O)OC1C(C(C(C(O1)CO)O)O)OC1C(C(C(C(O1)CO)O)OC1C(C(C(C(O1)CO)O)OC1C(C(C(C(O1)CO)O)O)OC1C(C(C(C(O1)CO)O)O)O)O)O)O)O)NC(=O)C)O)NC(=O)C");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //All 14 circular sugar moieties are correctly removed
        Assertions.assertEquals(
                "O=P(O)(O)OP(=O)(O)OCCC(C)CCC=C(C)CCC=C(C)CCC=C(C)CCC=C(C)C",
                smilesCode);
    }

    /**
     * The tested molecule has three interlinked linear sugar moieties that
     * illustrate the splitting of ether, ester, and peroxide bonds to separate
     * such polymers into distinct sugar moieties during detection.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    void specificTest34() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical);
        IAtomContainer originalMolecule;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        //the molecule has three interlinked linear sugars, illustrating why splitting ether, ester, and peroxide bonds
        // makes sense in some cases, such as this one
        originalMolecule = smiPar.parseSmiles(
                //CNP0138295
                "O=CC(O)C(O)C(O)C(O)COC(O)(C(O)COC(=O)C(O)C(O)C(O)C(O)COC1=CC=CC=2C(=O)C3=CC(=CC(O)=C3C(=O)C12)C)C(O)C(O)C=O");
        List<IAtomContainer> candidates = sugarRemovalUtil.getLinearSugarCandidates(originalMolecule);
        List<String> actualList = new ArrayList<>(3);
        for (IAtomContainer candidate : candidates) {
            actualList.add(smiGen.create(candidate));
        }
        List<String> expectedList = new ArrayList<>(3);
        expectedList.add("O=CC(O)C(O)C(O)C(O)C[O]");
        expectedList.add("O=CC(O)C(O)[C](O)C(O)C[O]");
        expectedList.add("O=[C]C(O)C(O)C(O)C(O)C[O]");
        Assertions.assertEquals(expectedList, actualList);
    }

    /**
     * This test illustrates a known problem with the linear sugar detection
     * using the SRU: In a few cases, the detection of a linear sugar candidate
     * can fail if the circular sugars are removed from the molecule. The
     * particular linear sugar is in most cases part of a ring and detected when
     * the molecule still has its original structure.
     * After removing the circular sugars, the linear sugar is not detected
     * anymore. This is due to the linear sugar patterns not matching anymore
     * without the adjunct circular sugar. The test illustrates this problem
     * based on three molecules where this effect occurs.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    void specificTest35() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectLinearSugarsInRingsSetting(true);
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(false);
        sugarRemovalUtil.setPreservationModeSetting(SugarRemovalUtility.PreservationMode.ALL);
        float loadFactor = 0.75f;
        int testMoleculesCNPtoSMILESMapInitCapacity = (int)(4.0f * (1.0f / loadFactor) + 2.0f);
        HashMap<String, String> testMoleculesCNPtoSMILESMap = new HashMap<>(testMoleculesCNPtoSMILESMapInitCapacity, loadFactor);
        //in these molecules, a small linear sugar inside a ring is detected, respectively; it is not detected anymore after removing
        // all circular sugars from the respective molecule, even though the structure is still there
        testMoleculesCNPtoSMILESMap.put("CNP0102508",
                "O=C(O)C=1C(=N)C(=O)CC(O)(C1O)C2OC(COC(=O)C)C(OC(=O)C3NC(=S)SC3C)C(OC4OC(C)C(O)(C(OC(=O)C(C)C)C)C(OC)C4)C2O");
        testMoleculesCNPtoSMILESMap.put("CNP0427619",
                "O=C(C=CC1=CC=C(O)C=C1)C=2C(=O)C(C(=O)C(O)(C2O)C3OC(CO)C(O)C(O)C3O)C(O)C4OCC(O)C(O)C4O");
        testMoleculesCNPtoSMILESMap.put("CNP0268646",
                "O=C(O)C=1C(=O)C(O)(CC(=O)C1N)C2OC(COC(=O)C)C(OC(=O)C(N=C(S)SCC(N=C(O)C)C(=O)O)C(SCC(N=C(O)C)C(=O)O)C)C(OC3OC(C)C(O)(C(OC(=O)C(C)CC)C)C(OC)C3)C2O");
        for (String cnpKey : testMoleculesCNPtoSMILESMap.keySet()) {
            IAtomContainer originalMolecule = smiPar.parseSmiles(testMoleculesCNPtoSMILESMap.get(cnpKey));
            List<IAtomContainer> candidates = sugarRemovalUtil.getLinearSugarCandidates(originalMolecule);
            Assertions.assertEquals(1, candidates.size());
            sugarRemovalUtil.removeCircularSugars(originalMolecule);
            candidates = sugarRemovalUtil.getLinearSugarCandidates(originalMolecule);
            Assertions.assertEquals(0, candidates.size());
        }
    }

    /**
     * The tested molecule contains a non-terminal spiro circular sugar. Using
     * default settings, nothing should be detected and removed, since spiro
     * sugars are not detected and non-terminal sugars not removed. It must also
     * be noted that in a previous version of the algorithm implementation, a
     * linear sugar was detected inside the spiro sugar ring. This should not
     * happen. Now, potential circular sugar candidates filtered from the linear
     * sugar candidates in linear sugar detection always include the spiro
     * circular sugar candidates, regardless of the current setting regarding
     * their detection in circular sugar detection.
     *
     *  @throws Exception if anything goes wrong
     */
    @Test
    void specificTest36() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectLinearSugarsInRingsSetting(true);
        originalMolecule = smiPar.parseSmiles(
                //CNP0008636
                "O=C(O)CC1C(=CC2CCC=CC1C2OC3OC(CO)C4(OC(=C5N=C([CH-]C5=C4)C(C)C)CCCO)C(O)C3(O)CC)C(=O)OC");
        //spiro rings are excluded
        Assertions.assertFalse(sugarRemovalUtil.hasCircularSugars(originalMolecule));
        //no linear sugar is detected inside the spiro ring
        Assertions.assertFalse(sugarRemovalUtil.hasLinearSugars(originalMolecule));
        sugarRemovalUtil.removeCircularSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //nothing is removed due to the above reasons and because the circular spiro sugar is non-terminal
        Assertions.assertEquals(
                "O=C(O)CC1C(=CC2CCC=CC1C2OC3OC(CO)C4(OC(=C5N=C([CH-]C5=C4)C(C)C)CCCO)C(O)C3(O)CC)C(=O)OC",
                smilesCode);
        sugarRemovalUtil.setDetectSpiroRingsAsCircularSugarsSetting(true);
        //still no linear sugar is detected inside the spiro ring
        Assertions.assertFalse(sugarRemovalUtil.hasLinearSugars(originalMolecule));
        //now, spiro sugar rings are detected
        Assertions.assertTrue(sugarRemovalUtil.hasCircularSugars(originalMolecule));
        sugarRemovalUtil.removeCircularSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //still, nothing is removed because the circular spiro sugar is non-terminal
        Assertions.assertEquals(
                "O=C(O)CC1C(=CC2CCC=CC1C2OC3OC(CO)C4(OC(=C5N=C([CH-]C5=C4)C(C)C)CCCO)C(O)C3(O)CC)C(=O)OC",
                smilesCode);
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(false);
        sugarRemovalUtil.removeCircularSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //now, the non-terminal spiro sugar ring is removed
        Assertions.assertEquals(
                "O=C(O)CC1C(=CC2CCC=CC1C2O)C(=O)OC.OCCCC=1OCC=C2[CH-]C(=NC21)C(C)C",
                smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //through manipulation of the SMILES string, the spiro sugar ring was turned into a pseudo sugar (O->C)
                "O=C(O)CC1C(=CC2CCC=CC1C2OC3CC(CO)C4(OC(=C5N=C([CH-]C5=C4)C(C)C)CCCO)C(O)C3(O)CC)C(=O)OC");
        //now, a linear sugar can be detected inside the spiro ring because as a pseudo sugar, the ring does not get
        // filtered from the linear sugar candidates
        Assertions.assertTrue(sugarRemovalUtil.hasLinearSugars(originalMolecule));
        smilesCode = smiGen.create(sugarRemovalUtil.getLinearSugarCandidates(originalMolecule).get(0));
        Assertions.assertEquals("[O][C]C(O)[C](O)[CH][O]", smilesCode);
    }

    /**
     * This test illustrates the option to allow the detection of sugar-like
     * circular moieties with keto groups, that was added in the Sugar Removal
     * Utility version 1.2
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    void specificTest37() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        originalMolecule = smiPar.parseSmiles(
                //CNP0310964
                "O=C(OC1C2=C(O)C=3C(=O)C=4C=CC=C(O)C4C(=O)C3C(O)=C2C(OC5OC(C)C(OC6OC(C)C(OC7OC(C(=O)CC7O)C)C(O)C6)C(N(C)C)C5)CC1(O)CC)C");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing is removed because the terminal sugar-like moiety has a keto group and the option to detect those is turned off
        Assertions.assertEquals(
                "O=C(OC1C2=C(O)C=3C(=O)C=4C=CC=C(O)C4C(=O)C3C(O)=C2C(OC5OC(C)C(OC6OC(C)C(OC7OC(C(=O)CC7O)C)C(O)C6)C(N(C)C)C5)CC1(O)CC)C",
                smilesCode);
        sugarRemovalUtil.setDetectCircularSugarsWithKetoGroupsSetting(true);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now, two sugar moieties are removed; the third sugar-like moiety does not have enough exocyclic oxygen atoms
        Assertions.assertEquals(
                "O=C1C=2C=CC=C(O)C2C(=O)C=3C(O)=C4C(=C(O)C13)C(OC(=O)C)C(O)(CC)CC4OC5OC(C)C(O)C(N(C)C)C5",
                smilesCode);
        sugarRemovalUtil.setExocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting(0.3);
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Now, with the exocyclic oxygen ratio threshold lowered, this moiety is also removed
        Assertions.assertEquals(
                "O=C1C=2C=CC=C(O)C2C(=O)C=3C(O)=C4C(=C(O)C13)C(OC(=O)C)C(O)(CC)CC4O",
                smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //through manipulation of the SMILES string, the keto group was transformed into a double bound carbon atom (O -> C)
                "O=C(OC1C2=C(O)C=3C(=O)C=4C=CC=C(O)C4C(=O)C3C(O)=C2C(OC5OC(C)C(OC6OC(C)C(OC7OC(C(=C)CC7O)C)C(O)C6)C(N(C)C)C5)CC1(O)CC)C");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //Nothing should be removed because the double bound carbon atom does not qualify as a keto group
        Assertions.assertEquals(
                "O=C(OC1C2=C(O)C=3C(=O)C=4C=CC=C(O)C4C(=O)C3C(O)=C2C(OC5OC(C)C(OC6OC(C)C(OC7OC(C(=C)CC7O)C)C(O)C6)C(N(C)C)C5)CC1(O)CC)C",
                smilesCode);
    }

    /**
     * Tests the protected routine to split ether, ester and peroxide bonds.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void testEtherEsterPeroxideSplittingExtraction() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        originalMolecule = smiPar.parseSmiles(
                //CNP0138295
                "O=CC(O)C(O)C(O)C(O)COC(O)(C(O)COC(=O)C(O)C(O)C(O)C(O)COC1=CC=CC=2C(=O)C3=CC(=CC(O)=C3C(=O)C12)C)C(O)C(O)C=O");
        List<IAtomContainer> beforeSplittingList = new ArrayList<>(1);
        beforeSplittingList.add(originalMolecule);
        SugarRemovalUtility sru = this.getSugarRemovalUtilityV1200DefaultSettings();
        List<IAtomContainer> afterSplittingList = sru.splitEtherEsterAndPeroxideBondsExtraction(beforeSplittingList);
        List<String> smilesAfterSplittingList = new ArrayList<>(3);
        for (IAtomContainer fragment : afterSplittingList) {
            String smilesCode = smiGen.create(fragment);
            smilesAfterSplittingList.add(smilesCode);
        }
        List<String> expectedList = new ArrayList<>(3);
        expectedList.add("O=CC(O)C(O)C(O)C(O)C[O]");
        expectedList.add("O=CC(O)C(O)[C](O)C(O)C[O]");
        //note: This structure is not split because one of the carbon atoms surrounding the ether oxygen is aromatic
        expectedList.add("O=[C]C(O)C(O)C(O)C(O)COC1=CC=CC=2C(=O)C3=CC(=CC(O)=C3C(=O)C12)C");
        Assertions.assertEquals(expectedList, smilesAfterSplittingList);
    }

    /**
     * Tests the correct handling of input molecules that consist of multiple
     * disconnected parts.
     *
     * @throws Exception if anything goes wrong or an AssertionError occurs
     */
    @Test
    void testDisconnectedStructures() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator((SmiFlavor.Canonical));
        IAtomContainer originalMolecule;
        String smilesCode;
        SugarRemovalUtility sugarRemovalUtil = this.getSugarRemovalUtilityV1200DefaultSettings();
        sugarRemovalUtil.setDetectCircularSugarsOnlyWithOGlycosidicBondSetting(true);
        originalMolecule = smiPar.parseSmiles(
                //CNP0220816 and CNP0218440
                "CC1=CC(OC2OC(CO)C(O)C(O)C2O)=C2C3=C(CCC3)C(=O)OC2=C1" +
                        ".O=C(O)CC(OC1OC(CO)C(O)C(O)C1O)(C)CC(=O)OCC=CC2=CC(OC)=C(OC3OC(CO)C(O)C(O)C3O)C(OC)=C2");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //the sugar moieties are correctly removed from both parts
        Assertions.assertEquals(
                "O=C1OC=2C=C(C=C(O)C2C3=C1CCC3)C.O=C(O)CC(O)(C)CC(=O)OCC=CC1=CC(OC)=C(O)C(OC)=C1",
                smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //CNP0220816 plus an unconnected propane
                "CC1=CC(OC2OC(CO)C(O)C(O)C2O)=C2C3=C(CCC3)C(=O)OC2=C1.CCC");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //the sugar is removed and the propane is also still there
        Assertions.assertEquals(
                "O=C1OC=2C=C(C=C(O)C2C3=C1CCC3)C.CCC",
                smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //CNP0119227, a molecule consisting entirely of one circular and one linear sugar,
                // plus an unconnected propane
                "OCC(O)C(O)C(O)C(O)C1OC(CO)C(O)C(O)C1O.CCC");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //the sugar is removed completely but the propane is still there
        Assertions.assertEquals(
                "CCC",
                smilesCode);

        originalMolecule = smiPar.parseSmiles(
                //just the propane
                "CCC");
        sugarRemovalUtil.removeCircularAndLinearSugars(originalMolecule);
        this.saturate(originalMolecule);
        smilesCode = smiGen.create(originalMolecule);
        //the propane is still there
        Assertions.assertEquals(
                "CCC",
                smilesCode);
    }

    /**
     * Perceives atom types and adds implicit hydrogen atoms to open valences of
     * the given molecule.
     *
     * @param molecule molecule to saturate
     * @throws CDKException if atom type perception or hydrogen atom addition
     *                      fails
     */
    protected void saturate(IAtomContainer molecule) throws CDKException {
        if (!molecule.isEmpty()) {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
            CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(molecule);
        }
    }

    /**
     * Returns a SugarRemovalUtility class object whose sugar removal settings
     * correspond to the default values in version 1.2.0.0 of the class. Should
     * the default settings change, the tests do not have to be re-written,
     * because of this method.
     *
     * @return a SugarRemovalUtility object with version 1.2.0.0 default settings
     */
    protected SugarRemovalUtility getSugarRemovalUtilityV1200DefaultSettings() {
        SugarRemovalUtility sugarRemovalUtil = new SugarRemovalUtility(SilentChemObjectBuilder.getInstance());
        sugarRemovalUtil.setDetectCircularSugarsOnlyWithOGlycosidicBondSetting(false);
        sugarRemovalUtil.setRemoveOnlyTerminalSugarsSetting(true);
        sugarRemovalUtil.setPreservationModeSetting(SugarRemovalUtility.PreservationMode.HEAVY_ATOM_COUNT);
        sugarRemovalUtil.setPreservationModeThresholdSetting(5);
        sugarRemovalUtil.setDetectCircularSugarsOnlyWithEnoughExocyclicOxygenAtomsSetting(true);
        sugarRemovalUtil.setExocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting(0.5);
        sugarRemovalUtil.setDetectLinearSugarsInRingsSetting(false);
        sugarRemovalUtil.setLinearSugarCandidateMinSizeSetting(4);
        sugarRemovalUtil.setLinearSugarCandidateMaxSizeSetting(7);
        sugarRemovalUtil.setDetectLinearAcidicSugarsSetting(false);
        sugarRemovalUtil.setDetectSpiroRingsAsCircularSugarsSetting(false);
        sugarRemovalUtil.setDetectCircularSugarsWithKetoGroupsSetting(false);
        return sugarRemovalUtil;
    }
}
