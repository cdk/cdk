/*
 * Copyright (c) 2013, European Bioinformatics Institute (EMBL-EBI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package uk.ac.ebi.beam;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Unit tests ensure round tripping for examples in the (<a
 * href="http://www.opensmiles.org/opensmiles.htmll">OpenSMILES
 * specification</a>)
 *
 * @author John May
 */
public class OpenSMILESSpecificationTest {

    // Atoms

    // Atomic Symbol

    @Test public void uranium() {
        roundTrip("[U]");
    }

    @Test public void lead() {
        roundTrip("[Pb]");
    }

    @Test public void helium() {
        roundTrip("[He]");
    }

    @Test public void unknown() {
        roundTrip("[*]");
    }

    // Hydrogens

    @Test public void methane() {
        roundTrip("[CH4]");
    }

    @Test public void hydrochloricAcid1() {
        roundTrip("[ClH]");
    }

    @Test public void hydrochloricAcid2() {
        roundTrip("[ClH1]", "[ClH]");
    }

    // Charge

    @Test public void chloride_anion() {
        roundTrip("[Cl-]");
    }

    @Test public void hydroxyl_anion1() {
        roundTrip("[OH1-]", "[OH-]");
    }

    @Test public void hydroxyl_anion2() {
        roundTrip("[OH-1]", "[OH-]");
    }

    @Test public void chloride_cation1() {
        roundTrip("[Cu+2]");
    }

    @Test public void chloride_cation2() {
        roundTrip("[Cu++]", "[Cu+2]");
    }

    // Isotopes

    @Test public void methane_c13() {
        roundTrip("[13CH4]");
    }

    @Test public void deuterium() {
        roundTrip("[13CH4]");
    }

    @Test public void uranium238() {
        roundTrip("[238U]");
    }

    // Organic subset
    // see. ElementTest

    // Wildcard

    @Test public void orthoSubstitutedPhenol() {
        roundTrip("Oc1c(*)cccc1");
    }

    // Atom Class

    @Test public void methane_atomClass2() {
        roundTrip("[CH4:2]");
    }

    // Bonds

    @Test public void ethane() {
        roundTrip("CC");
    }

    @Test public void ethanol() {
        roundTrip("CCO");
    }

    @Test public void n_butylamine1() {
        roundTrip("NCCCC");
    }

    @Test public void n_butylamine2() {
        roundTrip("CCCCN");
    }

    @Test public void ethene() {
        roundTrip("C=C");
    }

    @Test public void hydrogenCyanide() {
        roundTrip("C#N");
    }

    @Test public void _2_butyne() {
        roundTrip("CC#CC");
    }

    @Test public void propanol() {
        roundTrip("CCC=O");
    }

    @Test public void octachlorodirhenate_III() {
        roundTrip("[Rh-](Cl)(Cl)(Cl)(Cl)$[Rh-](Cl)(Cl)(Cl)Cl");
    }

    @Test public void ethane_explict_single_bond() {
        roundTrip("C-C");
    }

    @Test public void ethanol_explict_single_bonds() {
        roundTrip("C-C-O");
    }

    @Test public void butene_explict_single_bonds() {
        roundTrip("C-C=C-C");
    }

    // Branches

    @Test public void _2_ethyl_1_butanol() {
        roundTrip("CCC(CC)CO");
    }

    @Test public void _2_4_dimethyl_3_penthanone() {
        roundTrip("CC(C)C(=O)C(C)C");
    }

    @Test public void _2_propyl_3_isopropyl_1_propanol() {
        roundTrip("OCC(CCC)C(C(C)C)CCC");
    }

    @Test public void thiosulfate() {
        roundTrip("OS(=O)(=S)O");
    }

    @Test public void C22H46() {
        roundTrip("C(C(C(C(C(C(C(C(C(C(C(C(C(C(C(C(C(C(C(C(C))))))))))))))))))))C",
                  "C(CCCCCCCCCCCCCCCCCCCC)C");
    }

    // Rings

    @Test public void cyclohexane() {
        roundTrip("C1CCCCC1");
    }

    @Test public void perhydroisoquinoline() {
        roundTrip("N1CC2CCCC2CC1");
    }

    @Test public void cyclohexene1() {
        roundTrip("C=1CCCCC=1",
                  "C=1CCCCC1");
    }

    @Test public void cyclohexene2() {
        roundTrip("C1CCCCC=1",
                  "C=1CCCCC1");
    }

    @Test(expected = InvalidSmilesException.class)
    public void cyclohexene_invalid() throws IOException {
        Graph.fromSmiles("C-1CCCCC=1");
    }

    @Test public void cyclohexene_preferred() {
        roundTrip("C=1CCCCC1");
    }

    @Test public void dicyclohexyl_reusing_rnums() {
        roundTrip("C1CCCCC1C1CCCCC1",
                  "C1CCCCC1C2CCCCC2");
    }

    @Test public void dicyclohexyl_unique_rnums() {
        roundTrip("C1CCCCC1C2CCCCC2");
    }

    @Test public void cyclohexane_rnum0() {
        roundTrip("C0CCCCC0",
                  "C1CCCCC1");
    }

    @Test public void cyclohexane_2digit_rnum() {
        roundTrip("C%25CCCCC%25",
                  "C1CCCCC1");
    }

    @Test public void max_rnum_99() {
        roundTrip("C%123CCCCC%12CCC3",
                  "C12CCCCC1CCC2");
    }

    @Test public void mix_2digit_rnums_0() {
        roundTrip("C0CCCCC%0",
                  "C1CCCCC1");
    }

    @Test public void mix_2digit_rnums_1() {
        roundTrip("C1CCCCC%01",
                  "C1CCCCC1");
    }

    @Test public void spiro_5_5_undecane() {
        roundTrip("C12(CCCCC1)CCCCC2");
    }

    @Test(expected = InvalidSmilesException.class)
    public void multi_edge_1() throws IOException {
        Graph.fromSmiles("C12CCCCC12");
    }

    @Test(expected = InvalidSmilesException.class)
    public void multi_edge_2() throws IOException {
        Graph.fromSmiles("C12C2CCC1");
    }

    @Test(expected = InvalidSmilesException.class)
    public void loop() throws IOException {
        Graph.fromSmiles("C11");
    }

    // Aromaticity

    @Test public void benzene() {
        roundTrip("c1ccccc1");
    }

    @Test public void benzene_kekule() {
        roundTrip("C1=CC=CC=C1");
    }

    @Test public void indane() {
        roundTrip("c1ccc2CCCc2c1");
    }

    @Test public void indane_kekule() {
        roundTrip("C1=CC=CC(CCC2)=C12",
                  "C1=CC=CC=2CCCC21"); // input wasn't a DFS
    }

    @Test public void furan() {
        roundTrip("c1occc1");
    }

    @Test public void furan_kekule() {
        roundTrip("C1OC=CC=1",
                  "C=1OC=CC1"); // ring bond on open
    }

    @Test public void cyclobutadiene() {
        roundTrip("c1ccc1");
    }

    @Test public void cyclobutadiene_kekule() {
        roundTrip("C1=CC=C1");
    }

    @Test public void biphenyl() {
        roundTrip("c1ccccc1-c2ccccc2");
    }

    // More about Hydrogen

    @Test public void methane_implicit() {
        roundTrip("C");
    }

    @Test public void methane_atomProperty() {
        roundTrip("[CH4]");
    }

    @Test public void methane_explicit() {
        roundTrip("[H]C([H])([H])[H]");
    }

    @Test public void methane_some_explicit() {
        roundTrip("[H][CH2][H]");
    }

    @Test public void deuteroethane() {
        roundTrip("[2H][CH2]C");
    }

    // Disconnected

    @Test public void sodiumChloride() {
        roundTrip("[Na+].[Cl-]");
    }

    @Test public void phenol_and_2_amino_ethanol() {
        roundTrip("Oc1ccccc1.NCCO");
    }

    @Test public void diammoniumThiosulfate() {
        roundTrip("[NH4+].[NH4+].[O-]S(=O)(=O)[S-]");
    }

    @Test public void phenol_2_amino_ethanol_1() {
        roundTrip("c1cc(O.NCCO)ccc1",
                  "c1cc(O)ccc1.NCCO"); // non-DFS input
    }

    @Test public void phenol_2_amino_ethanol_2() {
        roundTrip("Oc1cc(.NCCO)ccc1",
                  "Oc1ccccc1.NCCO"); // non-DFS input
    }

    @Test(expected = InvalidSmilesException.class)
    public void dot_ring_bond() throws IOException {
        Graph.fromSmiles("C.1CCCCC.1");
    }

    @Test public void ethane_using_dot() {
        roundTrip("C1.C1", "CC");
    }

    @Test public void _1_bromo_2_3_dichlorobenzene() {
        roundTrip("c1c2c3c4cc1.Br2.Cl3.Cl4",
                  "c1c(c(c(cc1)Cl)Cl)Br"); // non-DFS
    }

    // Stereo chemistry

    @Test public void tetrahedral_anticlockwise() {
        roundTrip("N[C@](Br)(O)C");
    }

    @Test public void tetrahedral_clockwise() {
        roundTrip("N[C@@](Br)(O)C");
    }

    @Test public void tetrahedral_equivalent() {
        // we can show all these SMILES are equivalent if we change the order
        // of the vertices
        roundTrip("N[C@](Br)(O)C", new int[]{3, 1, 0, 2, 4}, "Br[C@](O)(N)C");
        roundTrip("Br[C@](O)(N)C", new int[]{2, 1, 0, 4, 3}, "O[C@](Br)(C)N");
        roundTrip("O[C@](Br)(C)N", new int[]{3, 1, 0, 2, 4}, "Br[C@](C)(O)N");
        roundTrip("Br[C@](C)(O)N", new int[]{2, 1, 0, 4, 3}, "C[C@](Br)(N)O");
        roundTrip("C[C@](Br)(N)O", new int[]{3, 1, 0, 2, 4}, "Br[C@](N)(C)O");
        roundTrip("Br[C@](N)(C)O", new int[]{2, 1, 4, 0, 3}, "C[C@@](Br)(O)N");
        roundTrip("C[C@@](Br)(O)N", new int[]{4, 1, 0, 3, 2}, "Br[C@@](N)(O)C");
        roundTrip("Br[C@@](N)(O)C", new int[]{2, 0, 4, 3, 1}, "[C@@](C)(Br)(O)N");
        roundTrip("[C@@](C)(Br)(O)N", new int[]{0, 4, 1, 3, 2}, "[C@@](Br)(N)(O)C");
    }

    @Test public void tetrahedral_equivalent_2() {
        roundTrip("FC1C[C@](Br)(Cl)CCC1",
                  new int[]{7, 6, 8, 0, 1, 2, 5, 4, 3},
                  "[C@]1(Br)(Cl)CCCC(F)C1");
    }

    @Test public void tetrahedral_3_neighbors() {
        roundTrip("N[C@H](O)C");
    }

    @Test public void trans_difluoroethane_1() {
        roundTrip("F/C=C/F");
    }

    @Test public void trans_difluoroethane_2() {
        roundTrip("F\\C=C\\F");
    }

    @Test public void trans_difluoroethane_3() {
        roundTrip("C(\\F)=C/F");
        roundTrip("C(\\\\F)=C/F", new int[]{1, 0, 2, 3}, "F/C=C/F");

    }

    @Test public void cis_difluoroethane_1() {
        roundTrip("F/C=C\\F");
    }

    @Test public void cis_difluoroethane_2() {
        roundTrip("F\\C=C/F");
    }

    @Test public void cis_difluoroethane_3() {
        roundTrip("C(/F)=C/F");
        roundTrip("C(/F)=C/F", new int[]{1, 0, 2, 3}, "F\\C=C/F");
    }

    // C/C(\F)=C/F - see AddUpDownBonds

    @Test public void trans_difluoro_implied() {
        roundTrip("F/C(CC)=C/F");
    }

    @Test public void extended_cistrans_1() {
        roundTrip("F/C=C=C=C/F");
    }

    @Test public void extended_cistrans_2() {
        roundTrip("F\\C=C=C=C\\F");
    }

    // other stereo not yet supported

    @Test public void conjugated() {
        roundTrip("F/C=C/C/C=C\\C");
    }

    @Test public void conjugated_partial() {
        roundTrip("F/C=C/CC=CC");
    }

    @Test public void partial_tetrahedral() {
        roundTrip("N1[C@H](Cl)[C@@H](Cl)C(Cl)CC1");
    }

    // Parsing termination
    @Test public void terminate_on_space() {
        roundTrip("CCO ethanol", "CCO");
    }

    @Test public void terminate_on_tab() {
        roundTrip("CCO\tethanol", "CCO");
    }

    @Test public void terminate_on_newline() {
        roundTrip("CCO\nethanol", "CCO");
    }

    @Test public void terminate_on_carriage_return() {
        roundTrip("CCO\r\nethanol", "CCO");
    }

    // Normalisation - part of the functions but we can test we can read/write them

    @Test public void ethanol_norm_1() {
        roundTrip("CCO");
    }

    @Test public void ethanol_norm_2() {
        roundTrip("OCC");
    }

    @Test public void ethanol_norm_3() {
        roundTrip("C(O)C");
    }

    @Test public void ethanol_norm_4() {
        roundTrip("[CH3][CH2][OH]");
    }

    @Test public void ethanol_norm_5() {
        roundTrip("[H][C]([H])([H])C([H])([H])[O][H]");
    }

    // Standard form - again only that we can read/write is tested here

    @Test public void ethane_right() {
        roundTrip("CC");
    }

    @Test public void ethane_wrong() {
        roundTrip("[CH3][CH3]");
    }

    @Test public void leave_off_digit_on_single_charge() {
        roundTrip("[CH3-1]", "[CH3-]");
    }

    @Test public void leave_off_digit_on_single_hydrogen() {
        roundTrip("C[13CH1](C)C", "C[13CH](C)C");
    }

    @Test(expected = InvalidSmilesException.class)
    public void write_atom_properties_in_order_1() throws
                                                   IOException {
        Parser.strict("[C-H3]");    // this is accepted by daylight but doesn't match OpenSMILES grammar
    }

    @Test(expected = InvalidSmilesException.class)
    public void write_atom_properties_in_order_2() throws
                                                   IOException {
        Parser.strict("C[CH@](Br)Cl");  // this is accepted by daylight but doesn't match OpenSMILES grammar
    }

    @Test public void methanide_wrong() {
        roundTrip("[H][C-]([H])[H]");
    }

    @Test public void methanide_right() {
        roundTrip("[CH3-]");
    }

    // Bonds

    @Test public void ethane_bonds_wrong() {
        roundTrip("C-C");
    }

    @Test public void ethane_bonds_right() {
        roundTrip("CC");
    }

    @Test public void benzene_bonds_wrong() {
        roundTrip("c:1:c:c:c:c:c:1",
                  "c:1:c:c:c:c:c1");
    }

    @Test public void benzene_bonds_right() {
        roundTrip("c1ccccc1");
    }

    @Test public void biphenyl_wrong() {
        roundTrip("c1ccccc1c2ccccc2");
    }

    @Test public void biphenyl_right() {
        roundTrip("c1ccccc1-c2ccccc2");
    }

    // Cycles

    @Test public void rnum_reuse_1() {
        roundTrip("c1ccccc1C1CCCC1",
                  "c1ccccc1C2CCCC2");
    }

    @Test public void rnum_reuse_2() {
        roundTrip("c0ccccc0C1CCCC1",
                  "c1ccccc1C2CCCC2");
    }

    // avoid ring closures on double bond - nice idea but not valid to implement
    @Test public void avoid_ring_closures_on_double_bond() {
        roundTrip("CC=1CCCCC=1",
                  "CC=1CCCCC1");
    }

    // avoid closing/openning 2 rings on a single atom - yeah good luck :-)
    @Test public void avoid_starting_ringsystem_on_two_digits() {
        roundTrip("C12(CCCCC1)CCCCC2",
                  "C12(CCCCC1)CCCCC2");
    }

    @Test public void use_simple_digits() {
        roundTrip("C%01CCCCC%01",
                  "C1CCCCC1");
    }

    // starting branches

    @Test public void start_on_terminal_wrong() {
        roundTrip("c1cc(CO)ccc1");
    }

    @Test public void start_on_terminal_right() {
        roundTrip("OCc1ccccc1");
    }

    @Test public void short_branches_wrong() {
        roundTrip("CC(CCCCCC)C");
    }

    @Test public void short_branched_right() {
        roundTrip("CC(C)CCCCCC");
    }

    @Test public void start_on_hetroatom_wrong() {
        roundTrip("CCCO");
    }

    @Test public void start_on_hetroatom_right() {
        roundTrip("OCCC");
    }

    @Test public void only_use_dot_for_disconnected() {
        roundTrip("C1.C1", "CC");
    }

    @Test public void write_aromatic_form_wrong() {
        roundTrip("C1=CC=CC=C1");
    }

    @Test public void write_aromatic_form_right() {
        roundTrip("c1ccccc1");
    }

    @Test public void remove_chiral_markings_wrong() {
        roundTrip("Br[C@H](Br)C");
    }

    @Test public void remove_chiral_markings_right() {
        roundTrip("BrC(Br)C");
    }

    @Test public void remove_directional_markings_wrong() {
        roundTrip("F/C(/F)=C/F");
    }

    @Test public void remove_directional_markings_right() {
        roundTrip("FC(F)=CF");
    }

    // Non-standard forms

    @Test public void extra_paratheses_1() {
        roundTrip("C((C))O", "C(C)O");
    }

    @Test(expected = InvalidSmilesException.class)
    public void extra_paratheses_2() throws IOException {
        Graph.fromSmiles("(N1CCCC1)");
    }

    @Test public void misplaced_dots_1() {
        roundTrip(".CCO", "CCO");
    }

    @Test public void misplaced_dots_2() {
        roundTrip("CCO.", "CCO");
    }

    @Test(expected = InvalidSmilesException.class)
    public void mismatch_ring() throws InvalidSmilesException {
        Parser.parse("C1CCC");
    }

    // invalid cis/trans - semantics
    // conflict cis/trans - semantics (see. functions)

    @Test public void D_for_h2() {
        roundTrip("D[CH3]",
                  "[2H][CH3]");
    }

    @Test public void T_for_h3() {
        roundTrip("T[CH3]",
                  "[3H][CH3]");
    }

    // lowercase for Sp2 - stupid :)

    // Extensions

    @Test public void nope_not_illegal() {
        roundTrip("C/1=C/C=C\\C=C/C=C\\1",
                  "C/1=C/C=C\\C=C/C=C1");
    }

    @Test public void atom_based_db_stereo_trans_1() {
        roundTrip("F[C@@H]=[C@H]F");
    }

    @Test public void atom_based_db_stereo_trans_2() {
        roundTrip("F[C@H]=[C@@H]F");
    }

    @Test public void atom_based_db_stereo_cis_1() {
        roundTrip("F[C@H]=[C@H]F");
    }

    @Test public void atom_based_db_stereo_cis_2() {
        roundTrip("F[C@@H]=[C@@H]F");
    }

    @Test public void cyclooctatetraene() {
        roundTrip("[C@H]1=[C@@H][C@@H]=[C@@H][C@@H]=[C@@H][C@@H]=[C@@H]1");
    }

    static void roundTrip(String smi) {
        roundTrip(smi, smi);
    }

    static void roundTrip(String smi, int[] p, String exp) {
        try {
            assertThat(Generator.generate(Parser.parse(smi)
                                                .permute(p)), is(exp));
        } catch (InvalidSmilesException e) {
            fail(e.getMessage());
        }
    }

    static void roundTrip(String smi, String exp) {
        try {
            assertThat(Generator.generate(Parser.parse(smi)), is(exp));
        } catch (InvalidSmilesException e) {
            fail(e.getMessage());
        }
    }

}
