package uk.ac.ebi.beam;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/** @author John May */
public class DaylightDelocalisationTest {

    @Test public void benzene() throws IOException {
        Graph g = Graph.fromSmiles("[CH]1=[CH][CH]=[CH][CH]=[CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{true, true, true, true, true, true}));
    }

    @Test public void azulene() throws IOException {
        Graph g = Graph.fromSmiles("[CH]1=[CH][C]2=[CH][CH]=[CH][CH]=[CH][C]2=[CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{true, true, true, true, true, true, true, true, true, true}));
    }

    @Test public void cyclopenta_b_azepine() throws IOException {
        Graph g = Graph.fromSmiles("[CH]1=[CH][C]2=[N][CH]=[CH][CH]=[CH][C]2=[CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{true, true, true, true, true, true, true, true, true, true}));
    }

    @Test public void sp2_oxygen_cation() throws IOException {
        Graph g = Graph.fromSmiles("[CH]1[NH+]=[CH][C]2=[CH][CH]=[CH][O+]12");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{false, false, false, false, false, false, false, false}));
    }

    @Test public void pyridine() throws IOException {
        Graph g = Graph.fromSmiles("[N]1=[CH][CH]=[CH][CH]=[CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{true, true, true, true, true, true}));
    }

    @Test public void _1_H_pyrole() throws Exception {
        test("N1C=CC=C1", "[nH]1cccc1");
    }

    @Test public void pyridine_n_oxide() throws IOException {
        Graph g = Graph.fromSmiles("[O]=[N]1=[CH][CH]=[CH][CH]=[CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{false, true, true, true, true, true, true}));
    }

    @Test public void pyridine_n_oxide_charge_sep() throws IOException {
        Graph g = Graph.fromSmiles("[O-][N+]1=[CH][CH]=[CH][CH]=[CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{false, true, true, true, true, true, true}));
    }

    @Test public void furan() throws IOException {
        Graph g = Graph.fromSmiles("O1C=CC=C1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{true, true, true, true, true}));
    }

    @Test public void thiophene() throws IOException {
        Graph g = Graph.fromSmiles("[S]1[CH]=[CH][CH]=[CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{true, true, true, true, true}));
    }

    @Test public void cyclopentadienyl_anion() throws IOException {
        Graph g = Graph.fromSmiles("[CH-]1[CH]=[CH][CH]=[CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{true, true, true, true, true}));
    }

    @Test public void cyclodecapentaene() throws IOException {
        Graph g = Graph.fromSmiles("[CH]=1[CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{true, true, true, true, true,
                                                true, true, true, true, true}));
    }

    @Test public void cyclotetradecaheptaene() throws IOException {
        Graph g = Graph.fromSmiles("[CH]=1[CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{true, true, true, true, true, true,
                                                true, true, true, true,
                                                true, true, true, true}));
    }

    @Test public void cyclooctadecanonaene() throws IOException {
        Graph g = Graph.fromSmiles("[CH]=1[CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{true, true, true, true, true, true,
                                                true, true, true, true,
                                                true, true, true, true,
                                                true, true, true, true,
        }));
    }

    @Test public void cyclodocosaundecaene() throws IOException {
        Graph g = Graph.fromSmiles("[CH]=1[CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]=[CH][CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{true, true, true, true, true, true,
                                                true, true, true, true,
                                                true, true, true, true,
                                                true, true, true, true,
                                                true, true, true, true,
        }));
    }

    @Test public void cyclohexa_g_chromen_6_one() throws IOException {
        Graph g = Graph.fromSmiles("[O]=[C]1[CH]=[CH][CH]=[C]2[CH]=[C]3[O][CH]=[CH][CH]=[C]3[CH]=[C]12");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{false,
                                                true, true, true, true, true, true,
                                                true, true, true, true,
                                                true, true, true, true,
        }));
    }

    // n.b. looks like a crown :-)
    @Test public void trioxanetrione() throws IOException {
        Graph g = Graph.fromSmiles("[O]1[O][O][C](=[O])[C](=[O])[C](=[O])1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{true, true, true, true, false, true, false, true, false}));
    }

    @Test public void dimethylidenecyclohexadiene() throws IOException {
        Graph g = Graph.fromSmiles("[CH2]=[C]1[CH]=[CH][C](=[CH2])[CH]=[CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{false, true, true, true, true, false, true, true}));
    }

    @Ignore public void test() throws IOException {
        Graph g = Graph.fromSmiles("[CH2]=[C]1[CH]=[CH][N](=[CH2])=[CH]1");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{
                false, false, true, true, true, true, true, true
        }));
    }

    @Test public void noroborane() throws IOException {
        Graph g = Graph.fromSmiles("[CH2]=[C]1[C]2=[CH][CH]=[C]1[N]=[CH]2");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{
                false, false, true, true, true, true, true, true
        }));
    }

    // http://www.eyesopen.com/docs/toolkits/current/html/OEChem_TK-python/_images/OEAssignAromaticFlags_Table.png
    @Ignore public void openeye_comparison_5() throws IOException {
        Graph g = Graph.fromSmiles("[NH]1[C]2=[CH][CH]=[C]1[CH]=[C]3[CH]=[CH][C]([CH]=[C]4[NH][C]([CH]=[CH]4)=[CH][C]5=[N][C]([CH]=[CH]5)=[CH]2)=[N]3");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{
                true, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true,
        }));
    }

    // same as above but without the hydrogens http://www.eyesopen.com/docs/toolkits/current/html/OEChem_TK-python/_images/OEAssignAromaticFlags_Table.png
    @Test public void porphyrin() throws IOException {
        Graph g = Graph.fromSmiles("C1=CC=2C=C3C=CC(C=C4C=CC(C=C5C=CC(C=C1N2)=N5)=N4)=N3");
        AllCycles d = AllCycles.daylightModel(g);
        assertThat(d.aromatic, is(new boolean[]{
                true, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true,
        }));
    }

    @Test public void limit_all_cycles() throws Exception {
        test("C1=CC=CC=C1", "c1ccccc1", 6);
        test("[CH+]1C=CC=CC=C1", "[CH+]1C=CC=CC=C1", 6);
    }
    
    @Test public void fullerene_c70() throws Exception {
        test("C1=2C3=C4C5=C1C1=C6C7=C5C5=C8C4=C4C9=C3C3=C%10C=2C2=C1C1=C%11C%12=C%13C%14=C%15C%16=C%17C%18=C%19C%20=C%16C%16=C%14C%12=C%12C%14=C%21C%22=C(C%20=C%16%14)C%14=C%19C%16=C(C4=C8C(=C%18%16)C4=C%17C%15=C(C7=C54)C%13=C61)C1=C%14C%22=C(C3=C91)C1=C%21C%12=C%11C2=C%101",
             "c12c3c4c5c1c6c7c8c5c9c%10c4c%11c%12c3c%13c%14c2c%15c6c%16c%17c%18c%19c%20c%21c%22c%23c%24c%25c%26c%22c%27c%20c%18c%28c%29c%30c%31c(c%26c%27%29)c%32c%25c%33c(c%11c%10c(c%24%33)c%34c%23c%21c(c8c9%34)c%19c7%16)c%35c%32c%31c(c%13c%12%35)c%36c%30c%28c%17c%15c%14%36",
             6);
    }

    /* Carbon Examples */

    // carbon gives 1 electron (double bond) (6 * 1) % 4 = 2 
    @Test public void carbon_6_memberRing() throws Exception {
        test("C1=CC=CC=C1", "c1ccccc1");
    }

    // carbon gives 1 electron (double bond) (10 * 1) % 4 = 2
    @Test public void carbon_10_memberRing() throws Exception {
        test("C1=CC=CC=CC=CC=C1", "c1ccccccccc1");
    }

    // carbon anion gives 2 electrons (lone pair) (6 * 1) % 4 = 2
    @Test public void carbon_anion_5_memberRing() throws Exception {
        test("[CH-]1C=CC=C1", "[cH-]1cccc1");
    }

    // carbon anion gives 2 electrons (lone pair) (6 * 1) % 4 = 2
    @Test public void carbon_dianion_5_memberRing() throws Exception {
        test("[CH-2]1C=CC=C1", "[CH-2]1C=CC=C1");
    }

    // carbon cation gives 1 electron (double bond) (5 * 1) % 4 != 2
    @Test public void carbon_cation_5_memberRing() throws Exception {
        test("[CH+]1C=CC=C1", "[CH+]1C=CC=C1");
    }

    // carbon cation (5 valent) 
    @Test public void carbon_cation_5v_5_memberRing() throws Exception {
        test("C=[C+]1=CC(=C)C=C1", "C=[C+]1=CC(=C)C=C1");
    }

    // carbon cation (5 valent) 
    @Test public void carbon_cation_5v_6_memberRing() throws Exception {
        test("C=[C+]1=CC=CC=C1", "C=[C+]1=CC=CC=C1");
        test("[CH2+]1=CC=CC=C1", "[CH2+]1=CC=CC=C1");
    }

    // carbon cation gives 1 electron (double bond) (6 * 1) % 4 = 2, but not Sp2 hybridised?
    @Test public void carbon_cation_6_memberRing() throws Exception {
        test("C1=CC=[C+]C=C1", "c1cc[c+]cc1");
    }

    @Test public void carbon_cation_7_memberRing() throws Exception {
        test("C=1[CH+]C=CC=CC1", "c1[cH+]ccccc1");
    }

    // carbon dication gives 0 electron (4 * 1) % 4 != 2
    @Test public void carbon_dication_5_memberRing() throws Exception {
        test("[C+2]1C=CC=C1", "[C+2]1C=CC=C1");
    }

    // carbon dication gives 1 electron (double bond) (4 * 1) % 4 != 2 but unusual charge
    @Test public void carbon_dication_6_memberRing() throws Exception {
        test("C1=CC=[C+2]C=C1", "C1=CC=[C+2]C=C1");
    }

    // carbon gives 1 electron (double bond) (4 * 1) % 4 != 2 
    @Test public void carbon_5_memberRing_exoCyclic() throws Exception {
        test("O=C1C=CC=C1", "O=C1C=CC=C1");
    }

    // carbon gives 1 electron (double bond) (6 * 1) % 4 = 2 - but the exocyclic electronegatic double bond takes it away 
    @Test public void carbon_7_memberRing_exoCyclic_O() throws Exception {
        test("O=C1C=CC=CC=C1", "O=c1cccccc1");
    }

    // carbon gives 1 electron (double bond) (6 * 1) % 4 = 2 - but the exocyclic electronegatic double bond takes it away 
    @Test public void carbon_7_memberRing_exoCyclic_N() throws Exception {
        test("N=C1C=CC=CC=C1", "N=c1cccccc1");
    }

    // carbon gives 1 electron (double bond) (6 * 1) % 4 = 2 - but the exocyclic electronegatic double bond takes it away 
    @Test public void carbon_7_memberRing_exoCyclic_S() throws Exception {
        test("S=C1C=CC=CC=C1", "S=c1cccccc1");
    }

    // carbon gives 1 electron (double bond) (6 * 1) % 4 = 2 - but the exocyclic electronegatic double bond takes it away 
    @Test public void carbon_7_memberRing_exoCyclic_Se() throws Exception {
        test("[Se]=C1C=CC=CC=C1", "[Se]=c1cccccc1");
    }

    // carbon gives 1 electron (double bond) (6 * 1) % 4 = 2 - but the exocyclic electronegatic double bond takes it away 
    @Test public void carbon_7_memberRing_exoCyclic_P() throws Exception {
        test("P=C1C=CC=CC=C1", "P=c1cccccc1");
    }

    // carbon gives 1 electron (double bond) (6 * 1) % 4 = 2 - but the exocyclic electronegatic double bond takes it away 
    @Test public void carbon_7_memberRing_exoCyclic_As() throws Exception {
        test("[AsH]=C1C=CC=CC=C1", "[AsH]=c1cccccc1");
    }

    // carbon gives 1 electron (double bond) (6 * 1) % 4 = 2 - but the exocyclic electronegatic double bond takes it away 
    @Test public void carbon_7_memberRing_exoCyclic_B() throws Exception {
        test("B=C1C=CC=CC=C1", "B=c1cccccc1");
    }

    // carbon gives 1 electron (double bond) (7 * 1) % 4 != 2  
    @Test public void carbon_7_memberRing_exoCyclic_C() throws Exception {
        test("C=C1C=CC=CC=C1", "C=C1C=CC=CC=C1");
    }     
    
    /* Nitrogen Examples */

    // 2 electrons from the lone-pair
    @Test public void nitrogen_5_memberRing() throws Exception {
        test("N1C=CC=C1", "[nH]1cccc1");
    }

    // 1 electron from the double-bond
    @Test public void nitrogen_6_memberRing() throws Exception {
        test("N=1C=CC=CC1", "n1ccccc1");
    }

    // 0 electrons (Sp3)
    @Test public void nitrogen_cation_5_memberRing() throws Exception {
        test("[NH2+]1C=CC=C1", "[NH2+]1C=CC=C1");
    }

    // 0 electrons (Sp3)
    @Test public void nitrogen_cation_6_memberRing() throws Exception {
        test("[NH2+]1C=CC=C1", "[NH2+]1C=CC=C1");
    }

    // 0 electrons (Sp3) note - 6 electrons in ring thus 4n+2 valid
    @Test public void nitrogen_cation_6_memberRing2() throws Exception {
        test("N1C=C[NH2+]C=C1", "N1C=C[NH2+]C=C1");
    }

    // 0 electrons (Sp2) - 4n+2 not valid
    @Test public void nitrogen_dication_5_memberRing() throws Exception {
        test("[NH+2]1C=CC=C1", "[NH+2]1C=CC=C1");
    }

    // 0 electrons (Sp2) - 4n+2 valid but abnormal charge
    @Test public void nitrogen_dication_6_memberRing() throws Exception {
        test("N1C=C[NH+2]C=C1", "N1C=C[NH+2]C=C1");
    }

    // 2 electrons (lone pair)
    @Test public void nitrogen_anion_5_memberRing() throws Exception {
        test("[N-]1C=CC=C1", "[n-]1cccc1");
    }

    // 2 electrons (lone pair) - 4n+2 not valid, unusual valence
    @Test public void nitrogen_anion_6_memberRing() throws Exception {
        test("[N-]=1C=CC=CC1", "[N-]=1C=CC=CC1");
    }

    // 2 electrons (lone pair) - 4n+2 valid - not aromatic
    @Test public void nitrogen_anion_6_memberRing2() throws Exception {
        test("[N-]1C=CNC=C1", "[N-]1C=CNC=C1");
    }

    // 1 electron (double bond) - 4n+2 valid - but Sp3 - not aromatic
    @Test public void nitrogen_anion_6_memberRing3() throws Exception {
        test("[NH2-]=1C=CC=CC1", "[NH2-]=1C=CC=CC1");
    }

    // 1 electron (double bond) - 4n+2 valid - but not aromatic (2 double bonds?)
    @Test public void nitrogen_anion_6_memberRing_exoCyclic_N() throws Exception {
        test("[N]=1(=N)C=CC=CC1", "[N]=1(=N)C=CC=CC1");
    }

    @Test public void nitrogen_anion_6_memberRing_exoCyclic_O() throws Exception {
        test("N=1(=O)C=CC=CC1", "n1(=O)ccccc1");
    }

    @Test public void nitrogen_anion_6_memberRing_exoCyclic_S() throws Exception {
        test("[N]=1(=S)C=CC=CC1", "[N]=1(=S)C=CC=CC1");
    }

    // okay it doesn't given 2 electron (note 4n+2 valid if case)
    @Test public void nitrogen_2_doubleBond_exocyclic_N() throws Exception {
        test("C=C1C=C[N](=N)=C1", "C=C1C=C[N](=N)=C1");
    }

    // okay it doesn't given 0 electron (note 4n+2 valid if case)
    @Test public void nitrogen_2_doubleBond_exocyclic_O() throws Exception {
        test("N=[N]1=COC=C1", "N=[N]1=COC=C1");
    }

    // 2 electrons from lone pair
    @Test public void nitrogen_anion() throws Exception {
        test("O=C1C=C[N-]C=C1", "O=c1cc[n-]cc1");
    }
    
    /* Oxygen Examples */

    @Test public void oxygen_5_member_ring() throws Exception {
        test("O1C=CC=C1", "o1cccc1");
    }

    // 4n+2 invalid
    @Test public void oxygen_6_member_ring() throws Exception {
        test("C=C1C=COC=C1", "C=C1C=COC=C1");
    }

    // 4n+2 not invalid
    @Test public void oxygen_7_member_ring() throws Exception {
        test("O1C=CC=CC=C1", "O1C=CC=CC=C1");
    }

    @Test public void oxygen_cation_5_member_ring() throws Exception {
        test("[OH+]1C=CC=C1", "[OH+]1C=CC=C1");
    }

    @Test public void oxygen_cation_6_member_ring() throws Exception {
        test("C=C1C=C[OH+]C=C1", "C=C1C=C[OH+]C=C1");
    }

    @Test public void oxygen_cation_7_member_ring() throws Exception {
        test("[OH+]1C=CC=CC=C1", "[OH+]1C=CC=CC=C1");
    }

    @Test public void oxygen_cation_5_member_ring_piBond() throws Exception {
        test("C=C1C=C[O+]=C1", "C=C1C=C[O+]=C1");
    }

    @Test public void oxygen_cation_6_member_ring_piBond() throws Exception {
        test("C1=CC=[O+]C=C1", "c1cc[o+]cc1");
    }

    @Test public void oxygen_cation_7_member_ring_piBond() throws Exception {
        test("C=C1C=CC=C[O+]=C1", "C=C1C=CC=C[O+]=C1");
    }
    
    /* Sulphur Examples */

    @Test public void sulfur_5_member_ring() throws Exception {
        test("S1C=CC=C1", "s1cccc1");
    }

    // 4n+2 invalid
    @Test public void sulfur_6_member_ring() throws Exception {
        test("C=C1C=CSC=C1", "C=C1C=CSC=C1");
    }

    // 4n+2 not invalid
    @Test public void sulfur_7_member_ring() throws Exception {
        test("S1C=CC=CC=C1", "S1C=CC=CC=C1");
    }

    @Test public void sulfur_cation_5_member_ring() throws Exception {
        test("[SH+]1C=CC=C1", "[SH+]1C=CC=C1");
    }

    @Test public void sulfur_cation_6_member_ring() throws Exception {
        test("C=C1C=C[SH+]C=C1", "C=C1C=C[SH+]C=C1");
    }

    @Test public void sulfur_cation_7_member_ring() throws Exception {
        test("[SH+]1C=CC=CC=C1", "[SH+]1C=CC=CC=C1");
    }

    @Test public void sulfur_cation_5_member_ring_piBond() throws Exception {
        test("C=C1C=C[S+]=C1", "C=C1C=C[S+]=C1");
    }

    @Test public void sulfur_cation_6_member_ring_piBond() throws Exception {
        test("C1=CC=[S+]C=C1", "c1cc[s+]cc1");
    }

    @Test public void sulfur_cation_7_member_ring_piBond() throws Exception {
        test("C=C1C=CC=C[S+]=C1", "C=C1C=CC=C[S+]=C1");
    }

    @Test public void nitrogen_3_valent_acyclic() throws Exception {
        test("C=N1C=CC(=C)C=C1", "C=N1C=CC(=C)C=C1");
        test("N=N1C=CC(=C)C=C1", "N=N1C=CC(=C)C=C1");
        test("O=N1C=CC(=C)C=C1", "O=N1C=CC(=C)C=C1");
        test("P=N1C=CC(=C)C=C1", "P=N1C=CC(=C)C=C1");
        test("S=N1C=CC(=C)C=C1", "S=N1C=CC(=C)C=C1");
        // cation 
        test("C=[N+]1C=CC(=C)C=C1", "C=[n+]1ccc(=C)cc1");
        test("N=[N+]1C=CC(=C)C=C1", "N=[n+]1ccc(=C)cc1");
        test("O=[N+]1C=CC(=C)C=C1", "O=[n+]1ccc(=C)cc1");
        test("P=[N+]1C=CC(=C)C=C1", "P=[n+]1ccc(=C)cc1");
        test("S=[N+]1C=CC(=C)C=C1", "S=[n+]1ccc(=C)cc1");
        // anion (abnormal valence) 
        test("C=[N-]1C=CC=C1", "C=[N-]1C=CC=C1");
        test("N=[N-]1C=CC=C1", "N=[N-]1C=CC=C1");
        test("O=[N-]1C=CC=C1", "O=[N-]1C=CC=C1");
        test("P=[N-]1C=CC=C1", "P=[N-]1C=CC=C1");
        test("S=[N-]1C=CC=C1", "S=[N-]1C=CC=C1");
    }

    @Test public void nitrogen_5_valent_acyclic() throws Exception {
        test("C=[N]1=CC=CC=C1", "C=[N]1=CC=CC=C1");
        test("N=[N]1=CC=CC=C1", "N=[N]1=CC=CC=C1");
        test("O=[N]1=CC=CC=C1", "O=[n]1ccccc1");
        test("P=[N]1=CC=CC=C1", "P=[N]1=CC=CC=C1");
        test("S=[N]1=CC=CC=C1", "S=[N]1=CC=CC=C1");
    }

    @Test public void phosphorus_acyclic() throws Exception {
        test("C=P1C=CC(=C)C=C1", "C=P1C=CC(=C)C=C1");
        test("N=P1C=CC(=C)C=C1", "N=P1C=CC(=C)C=C1");
        test("O=P1C=CC(=C)C=C1", "O=P1C=CC(=C)C=C1");
        test("P=P1C=CC(=C)C=C1", "P=P1C=CC(=C)C=C1");
        test("S=P1C=CC(=C)C=C1", "S=P1C=CC(=C)C=C1");
        // cation 
        test("C=[P+]1C=CC(=C)C=C1", "C=[p+]1ccc(=C)cc1");
        test("N=[P+]1C=CC(=C)C=C1", "N=[p+]1ccc(=C)cc1");
        test("O=[P+]1C=CC(=C)C=C1", "O=[p+]1ccc(=C)cc1");
        test("P=[P+]1C=CC(=C)C=C1", "P=[p+]1ccc(=C)cc1");
        test("S=[P+]1C=CC(=C)C=C1", "S=[p+]1ccc(=C)cc1");
        // anion (valid but lone pair not given) 
        test("C=[P-]1C=CC=C1", "C=[P-]1C=CC=C1");
        test("N=[P-]1C=CC=C1", "N=[P-]1C=CC=C1");
        test("O=[P-]1C=CC=C1", "O=[P-]1C=CC=C1");
        test("P=[P-]1C=CC=C1", "P=[P-]1C=CC=C1");
        test("S=[P-]1C=CC=C1", "S=[P-]1C=CC=C1");
    }

    @Test public void sulfur_acyclic() throws Exception {
        test("C=S1C=CC=C1", "C=S1C=CC=C1");
        test("N=S1C=CC=C1", "N=S1C=CC=C1");
        test("O=S1C=CC=C1", "O=s1cccc1");
        test("P=S1C=CC=C1", "P=S1C=CC=C1");
        test("S=S1C=CC=C1", "S=S1C=CC=C1");
        // cation
        test("C=[SH+]1C=CC=C1", "C=[SH+]1C=CC=C1");
        test("N=[SH+]1C=CC=C1", "N=[SH+]1C=CC=C1");
        test("O=[SH+]1C=CC=C1", "O=[SH+]1C=CC=C1");
        test("P=[SH+]1C=CC=C1", "P=[SH+]1C=CC=C1");
        test("S=[SH+]1C=CC=C1", "S=[SH+]1C=CC=C1");
        // anion (note abnormal valence)
        test("C=[S-]1C=CC=C1", "C=[S-]1C=CC=C1");
        test("N=[S-]1C=CC=C1", "N=[S-]1C=CC=C1");
        test("O=[S-]1C=CC=C1", "O=[S-]1C=CC=C1");
        test("P=[S-]1C=CC=C1", "P=[S-]1C=CC=C1");
        test("S=[S-]1C=CC=C1", "S=[S-]1C=CC=C1");
    }
    
    /* Phosphorus Examples */
    /* Arsenic Examples */

    @Test public void arsenic_acyclic() throws Exception {
        test("C=[As+]1C=COC=C1", "C=[As+]1C=COC=C1");
        test("N=[As+]1C=COC=C1", "N=[As+]1C=COC=C1");
        test("O=[As+]1C=COC=C1", "O=[As+]1C=COC=C1");
        test("P=[As+]1C=COC=C1", "P=[As+]1C=COC=C1");
        test("S=[As+]1C=COC=C1", "S=[As+]1C=COC=C1");
        // cation 
        test("C=[As+]1C=CC(=C)C=C1", "C=[As+]1C=CC(=C)C=C1");
        test("N=[As+]1C=CC(=C)C=C1", "N=[As+]1C=CC(=C)C=C1");
        test("O=[As+]1C=CC(=C)C=C1", "O=[As+]1C=CC(=C)C=C1");
        test("P=[As+]1C=CC(=C)C=C1", "P=[As+]1C=CC(=C)C=C1");
        test("S=[As+]1C=CC(=C)C=C1", "S=[As+]1C=CC(=C)C=C1");
    }
    
    /* Selenium Examples */

    // misc

    @Test public void multi_cyclic_components() throws Exception {
        test("CCCCCC[N+]1=C(\\C=C/2\\C(=C(C2=O)C3=CC=C(S3)C4=CC=C(S4)C5=CC=C(S5)C6=C([O-])\\C(=C/C=7SC8=CC=CC=C8[N+]7CCCCCC)\\C6=O)[O-])SC9=CC=CC=C19",
             "CCCCCC[n+]1c(\\C=C/2\\C(=C(C2=O)c3ccc(s3)c4ccc(s4)c5ccc(s5)C6=C([O-])\\C(=C/c7sc8ccccc8[n+]7CCCCCC)\\C6=O)[O-])sc9ccccc19");
    }
    
    /* 
     * Tests referring to compounds from - 
     * http://blueobelisk.shapado.com/questions/aromaticity-perception-differences
     */

    @Test public void bo_6678() throws Exception {
        // note different from daylight due to their use of SSSR
        test("O=C1OC(=O)C2=C3C1=CC=C1C(=O)OC(=O)C(C=C2)=C31",
             "O=C1OC(=O)c2c3c1ccc4C(=O)OC(=O)c(cc2)c34");
    }

    @Test public void bo_8317() throws Exception {
        // note different from daylight due to their use of SSS
        test("O=C1C2=CC=CC=C2C2=C3C1=CC=C1C4=CC=C5C(=O)C6=CC=CC=C6C6=C5C4=C(C=C6)C(C=C2)=C31",
             "O=C1c2ccccc2c3c4c1ccc5c6ccc7C(=O)c8ccccc8c9c7c6c(cc9)c(cc3)c45");
    }

    @Test public void bo_8978() throws Exception {
        test("C1=CC=C2C(=C1)C1=N\\C\\2=N/C2=N/C(=N\\C3=N\\C(=N/C4=N/C(=N\\1)/C1=CC=CC=C41)\\C1=CC=CC=C31)/C1=CC=CC=C21",
             "c1ccc2c(c1)c3nc2nc4nc(nc5nc(nc6nc(n3)c7ccccc67)c8ccccc58)c9ccccc49");
    }

    @Test public void bo_18301() throws Exception {
        // note different from daylight due to their use of SSSR
        test("O=C1C=CC2=C3C1=CC=C1C(=O)C4=CC=CC=C4C(C=C2)=C31",
             "O=C1C=Cc2c3c1ccc4C(=O)c5ccccc5c(cc2)c34");
    }

    @Test public void bo_21963() throws Exception {
        test("O=C1C2=CC=CC3=C2C2=C(C=CC=C12)C=C3",
             "O=c1c2cccc3c2c4c(cccc14)cc3");
    }

    @Test public void bo_25756() throws Exception {
        test("NC1=C2C3=CC=CC=C3C3=CC=CC(C=C1)=C23",
             "Nc1c2c3ccccc3c4cccc(cc1)c24");
    }

    @Test public void bo_39171() throws Exception {
        test("O=C1C(=O)C2=CC3=CC=CC=C3C3=C2C2=C(C=CC=C12)C=C3",
             "O=c1c(=O)c2cc3ccccc3c4c2c5c(cccc15)cc4");
    }

    @Test public void bo_75696() throws Exception {
        test("O=C1NC(=O)C2=CC3=C(C=C12)C(=O)NC3=O",
             "O=c1[nH]c(=O)c2cc3c(cc12)c(=O)[nH]c3=O");
    }

    @Test public void bo_78222() throws Exception {
        test("[O-]S(=O)(=O)OC1=C2C=CC3=C(NC4=CC=C5C6=CC=CC=C6C(=O)C6=C5C4=C3C=C6)C2=C(OS([O-])(=O)=O)C2=CC=CC=C12",
             "[O-]S(=O)(=O)Oc1c2ccc3c([nH]c4ccc5c6ccccc6c(=O)c7c5c4c3cc7)c2c(OS([O-])(=O)=O)c8ccccc18");
    }

    @Test public void bo_83217() throws Exception {
        test("CN(C)C1=CC=[C-]C=C1",
             "CN(C)c1cc[c-]cc1");
    }

    /** Daylight Examples http://www.daylight.com/dayhtml_tutorials/languages/smiles/smiles_examples.html */
    @Ignore("need to kekulize") public void daylightExamples() throws Exception {
        test("CCc1nn(C)c2c(=O)[nH]c(nc12)c3cc(ccc3OCC)S(=O)(=O)N4CCN(C)CC4", "CCc1nn(C)c2c(=O)[nH]c(nc12)c3cc(ccc3OCC)S(=O)(=O)N4CCN(C)CC4");
        test("Cc1nnc2CN=C(c3ccccc3)c4cc(Cl)ccc4-n12", "Cc1nnc2CN=C(c3ccccc3)c4cc(Cl)ccc4-n12");
        test("CC(C)(N)Cc1ccccc1", "CC(C)(N)Cc1ccccc1");
        test("CN1C(=O)CN=C(c2ccccc2)c3cc(Cl)ccc13", "CN1C(=O)CN=C(c2ccccc2)c3cc(Cl)ccc13");
        test("CN(C)C(=O)Cc1c(nc2ccc(C)cn12)c3ccc(C)cc3", "CN(C)C(=O)Cc1c(nc2ccc(C)cn12)c3ccc(C)cc3");
        test("COc1ccc2[nH]c(nc2c1)S(=O)Cc3ncc(C)c(OC)c3C", "COc1ccc2[nH]c(nc2c1)S(=O)Cc3ncc(C)c(OC)c3C");
        test("CS(=O)(=O)c1ccc(cc1)C2=C(C(=O)OC2)c3ccccc3", "CS(=O)(=O)c1ccc(cc1)C2=C(C(=O)OC2)c3ccccc3");
        test("Fc1ccc(cc1)C2CCNCC2COc3ccc4OCOc4c3", "Fc1ccc(cc1)C2CCNCC2COc3ccc4OCOc4c3");
        test("CC(C)c1c(C(=O)Nc2ccccc2)c(c(c3ccc(F)cc3)n1CC[C@@H]4C[C@@H](O)CC(=O)O4)c5ccccc5", "CC(C)c1c(C(=O)Nc2ccccc2)c(c(c3ccc(F)cc3)n1CC[C@@H]4C[C@@H](O)CC(=O)O4)c5ccccc5");
        test("CN1CC(=O)N2[C@@H](c3[nH]c4ccccc4c3C[C@@H]2C1=O)c5ccc6OCOc6c5", "CN1CC(=O)N2[C@@H](c3[nH]c4ccccc4c3C[C@@H]2C1=O)c5ccc6OCOc6c5");
        test("O=C1C[C@H]2OCC=C3CN4CC[C@@]56[C@H]4C[C@H]3[C@H]2[C@H]6N1c7ccccc75", "O=C1C[C@H]2OCC=C3CN4CC[C@@]56[C@H]4C[C@H]3[C@H]2[C@H]6N1c7ccccc75");
        test("COC(=O)[C@H]1[C@@H]2CC[C@H](C[C@@H]1OC(=O)c3ccccc3)N2C", "COC(=O)[C@H]1[C@@H]2CC[C@H](C[C@@H]1OC(=O)c3ccccc3)N2C");
        test("COc1ccc2nccc([C@@H](O)[C@H]3C[C@@H]4CCN3C[C@@H]4C=C)c2c1", "COc1ccc2nccc([C@@H](O)[C@H]3C[C@@H]4CCN3C[C@@H]4C=C)c2c1");
        test("CN1C[C@@H](C=C2[C@H]1Cc3c[nH]c4cccc2c34)C(=O)O", "CN1C[C@@H](C=C2[C@H]1Cc3c[nH]c4cccc2c34)C(=O)O");
        test("CCN(CC)C(=O)[C@H]1CN(C)[C@@H]2Cc3c[nH]c4cccc(C2=C1)c34", "CCN(CC)C(=O)[C@H]1CN(C)[C@@H]2Cc3c[nH]c4cccc(C2=C1)c34");
        test("CN1CC[C@]23[C@H]4Oc5c3c(C[C@@H]1[C@@H]2C=C[C@@H]4O)ccc5O", "CN1CC[C@]23[C@H]4Oc5c3c(C[C@@H]1[C@@H]2C=C[C@@H]4O)ccc5O");
        test("CN1CC[C@]23[C@H]4Oc5c3c(C[C@@H]1[C@@H]2C=C[C@@H]4OC(=O)C)ccc5OC(=O)C", "CN1CC[C@]23[C@H]4Oc5c3c(C[C@@H]1[C@@H]2C=C[C@@H]4OC(=O)C)ccc5OC(=O)C");
        test("CN1CCC[C@H]1c2cccnc2", "CN1CCC[C@H]1c2cccnc2");
        test("Cn1cnc2n(C)c(=O)n(C)c(=O)c12", "Cn1cnc2n(C)c(=O)n(C)c(=O)c12");
        test("C/C(=C\\CO)/C=C/C=C(/C)\\C=C\\C1=C(C)CCCC1(C)C", "C/C(=C\\CO)/C=C/C=C(/C)\\C=C\\C1=C(C)CCCC1(C)C");
    }

    @Test public void non_daylight_aromatic_element() throws Exception {
        test("CC1=CC=C2[Bi](Cl)C3=CC=CC=C3S(=O)(=O)C2=C1",
             "Cc1ccc2[Bi](Cl)c3ccccc3S(=O)(=O)c2c1");
    }
    
    @Test public void acyclic_charge() throws Exception {
        test("[Na+].[Na+].[S-2]",
             "[Na+].[Na+].[S-2]");
    }
    
    private static void test(String org, String exp) throws Exception {
        Graph g = Graph.fromSmiles(org);
        Graph h = AllCycles.daylightModel(g).aromaticForm();
        assertThat(h.toSmiles(), is(exp));
    }

    private static void test(String org, String exp, int lim) throws Exception {
        Graph g = Graph.fromSmiles(org);
        Graph h = AllCycles.daylightModel(g, lim).aromaticForm();
        assertThat(h.toSmiles(), is(exp));
    }

}
