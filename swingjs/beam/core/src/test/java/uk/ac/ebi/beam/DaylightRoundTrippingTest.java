package uk.ac.ebi.beam;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Unit tests ensure round tripping for all examples in the (<a
 * href="http://www.daylight.com/dayhtml/doc/theory/theory.smiles.html">Daylight
 * theory manual</a>)
 *
 * @author John May
 */
public class DaylightRoundTrippingTest {

    // 3. SMILES - A Simplified Chemical Language

    @Test public void ethane() {
        roundTrip("CC");
    }

    @Test public void carbonDioxide() {
        roundTrip("O=C=O");
    }

    @Test public void hydrogenCyanide() {
        roundTrip("C#N");
    }

    @Test public void triethylamine() {
        roundTrip("CCN(CC)CC");
    }

    @Test public void aceticAcid() {
        roundTrip("CC(=O)O");
    }

    @Test public void cyclohexane() {
        roundTrip("C1CCCCC1");
    }

    @Test public void benzene() {
        roundTrip("c1ccccc1");
    }

    @Test public void hydroniumIon() {
        roundTrip("[OH3+]");
    }

    @Test public void deuteriumOxide() {
        roundTrip("[2H]O[2H]");
    }

    @Test public void uranium235() {
        roundTrip("[235U]");
    }

    @Test public void eDifluoroethene() {
        roundTrip("Cl/C=C/F");
    }

    @Test public void zDifluoroethene() {
        roundTrip("F/C=C\\F");
    }

    @Test public void lAlanine() {
        roundTrip("N[C@@H](C)C(=O)O");
    }

    @Test public void dAlanine() {
        roundTrip("N[C@H](C)C(=O)O");
    }

    // 3.2.1 Atoms

    @Test public void methane() {
        roundTrip("C");
    }

    @Test public void phosphine() {
        roundTrip("P");
    }

    @Test public void ammonia() {
        roundTrip("N");
    }

    @Test public void hydrogenSulfide() {
        roundTrip("S");
    }

    @Test public void water() {
        roundTrip("O");
    }

    @Test public void hydrochloricAcid() {
        roundTrip("Cl");
    }

    @Test public void elementalSulfur() {
        roundTrip("[S]");
    }

    @Test public void elementalGold() {
        roundTrip("[Au]");
    }

    @Test public void proton() {
        roundTrip("[H+]");
    }

    @Test public void ironIIcation() {
        roundTrip("[Fe+2]");
    }

    @Test public void hydroxylAnion() {
        roundTrip("[OH-]");
    }

    @Test public void ironIIcation2() {
        roundTrip("[Fe++]", "[Fe+2]");
    }

    @Test public void hydroniumCation() {
        roundTrip("[OH3+]");
    }

    @Test public void ammoniumCation() {
        roundTrip("[NH4+]");
    }

    // 3.2.2 Bonds

    @Test public void formaldehyde() {
        roundTrip("C=O");
    }

    @Test public void ethene() {
        roundTrip("C=C");
    }

    @Test public void dimethylEther() {
        roundTrip("COC");
    }

    @Test public void ethanol() {
        roundTrip("CO");
    }

    @Test public void molecularHydrogen() {
        roundTrip("[H][H]");
    }

    @Test public void _6_hydroxy_1_4_hexadiene() {
        roundTrip("[CH2]=[CH]-[CH2]-[CH]=[CH]-[CH2]-[OH]");
        roundTrip("C=CCC=CCO");
        roundTrip("C=C-C-C=C-C-O");
        roundTrip("OCC=CCC=C");
    }

    // 3.2.4 Branches

    @Test public void triethylamine_2() {
        roundTrip("CCN(CC)CC");
    }

    @Test public void isobutyricAcid() {
        roundTrip("CC(C)C(=O)O");
    }

    @Test public void _3_propyl_4_isopropyl_1_heptene() {
        roundTrip("C=CC(CCC)C(C(C)C)CCC");
    }

    // 3.2.4 Cyclic Structures

    @Test public void cyclohexane_2() {
        roundTrip("C1CCCCC1");
    }

    @Test public void _1_methyl_3_bromo_cyclohexene_1_1() {
        roundTrip("CC1=CC(Br)CCC1");
    }

    @Test public void _1_methyl_3_bromo_cyclohexene_1_2() {
        roundTrip("CC1=CC(CCC1)Br");
    }

    @Test public void cubane() {
        roundTrip("C12C3C4C1C5C4C3C25");
    }

    @Test public void _1_oxan_2_yl_piperidine() {
        roundTrip("O1CCCCC1N1CCCCC1", "O1CCCCC1N2CCCCC2");
    }

    // 3.2.5 Disconnected Structures

    @Test public void sodiumPhenoxide_1() {
        roundTrip("[Na+].[O-]c1ccccc1");
    }

    @Test public void sodiumPhenoxide_2() {
        roundTrip("c1cc([O-].[Na+])ccc1", "c1cc([O-])ccc1.[Na+]");
    }

    @Test public void disconnectedEthane() {
        roundTrip("C1.C1", "CC");
    }

    // 3.3 Isomeric SMILES
    // 3.3.1 Isotopic Specification

    @Test public void carbon_12() {
        roundTrip("[12C]");
    }

    @Test public void carbon_13() {
        roundTrip("[13C]");
    }

    @Test public void carbon_unspecifiedMass() {
        roundTrip("[C]");
    }

    @Test public void c13Methane() {
        roundTrip("[13CH4]");
    }

    // 3.3.2 Configuration Around Double Bonds

    @Test public void difluoroethene_1() {
        roundTrip("F/C=C/F");
    }

    @Test public void difluoroethene_2() {
        roundTrip("F\\C=C\\F");
    }

    @Test public void difluoroethene_3() {
        roundTrip("F/C=C\\F");
    }

    @Test public void difluoroethene_4() {
        roundTrip("F\\C=C/F");
    }

    @Test public void completelySpecified() {
        roundTrip("F/C=C/C=C/C");
    }

    @Test public void partiallySpecified() {
        roundTrip("F/C=C/C=CC");
    }

    // 3.3.3. Configuration Around Tetrahedral Centers

    @Test public void unspecifiedChirality() {
        roundTrip("NC(C)(F)C(=O)O");
    }

    @Test public void specifiedChirality_1() {
        roundTrip("N[C@](C)(F)C(=O)O");
    }

    @Test public void specifiedChirality_2() {
        roundTrip("N[C@@](C)(F)C(=O)O");
    }

    @Test public void lAlanine_1() {
        roundTrip("N[C@@]([H])(C)C(=O)O");
    }

    @Test public void lAlanine_2() {
        roundTrip("N[C@@H](C)C(=O)O");
    }

    @Test public void lAlanine_3() {
        roundTrip("N[C@H](C(=O)O)C");
    }

    @Test public void lAlanine_4() {
        roundTrip("[H][C@](N)(C)C(=O)O");
    }

    @Test public void lAlanine_5() {
        roundTrip("[C@H](N)(C)C(=O)O");
    }

    @Test public void dAlanine_1() {
        roundTrip("N[C@]([H])(C)C(=O)O");
    }

    @Test public void dAlanine_2() {
        roundTrip("N[C@H](C)C(=O)O");
    }

    @Test public void dAlanine_3() {
        roundTrip("N[C@@H](C(=O)O)C");
    }

    @Test public void dAlanine_4() {
        roundTrip("[H][C@@](N)(C)C(=O)O");
    }

    @Test public void dAlanine_5() {
        roundTrip("[C@@H](N)(C)C(=O)O");
    }

    @Test public void methyloxane_1() {
        roundTrip("C[C@H]1CCCCO1");
    }

    @Test public void methyloxane_2() {
        roundTrip("O1CCCC[C@@H]1C");
    }

    // 3.3.4 General Chiral Specification

    // not yet supported

    static void roundTrip(String smi) {
        roundTrip(smi, smi);
    }

    static void roundTrip(String smi, String exp) {
        try {
            assertThat(Generator.generate(Parser.parse(smi)), is(exp));
        } catch (InvalidSmilesException e) {
            fail(e.getMessage());
        }
    }
}
