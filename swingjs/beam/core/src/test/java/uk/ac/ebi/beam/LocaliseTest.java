package uk.ac.ebi.beam;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/** @author John May */
public class LocaliseTest {

    @Test public void furan() throws Exception {
        test("o1cccc1", "O1C=CC=C1");
    }

    @Test public void benzen() throws Exception {
        test("c1ccccc1", "C1=CC=CC=C1");
    }

    @Test public void quinone() throws Exception {
        test("oc1ccc(o)cc1", "O=C1C=CC(=O)C=C1");
        test("O=c1ccc(=O)cc1", "O=C1C=CC(=O)C=C1");
    }

    @Test(expected = InvalidSmilesException.class)
    public void methane() throws Exception {
        test("c", "C"); // note daylight makes it 'CH3' but we say - valence error
    }

    @Test public void ethene() throws Exception {
        test("cc", "C=C");
    }

    @Test(expected = InvalidSmilesException.class)
    public void invalid_acyclic_chain() throws Exception {
        test("ccc", "n/a");
    }

    @Test public void buta_1_3_diene() throws Exception {
        test("cccc", "C=CC=C");
    }

    // some allow lower-case to be radical, this should throw an exception
    @Test(expected = InvalidSmilesException.class)
    public void carbon_radical() throws Exception {
        test("C1CCcCC1", "n/a");
    }

    @Test public void _hexa_1_3_5_triene() throws Exception {
        test("cccccc", "C=CC=CC=C");
    }

    @Test public void _4H_pyran_4_one() throws Exception {
        test("oc1ccocc1", "O=C1C=COC=C1");
    }

    @Test public void pyrole() throws Exception {
        test("[nH]1cccc1", "[NH]1C=CC=C1");
    }
    
    @Test public void CHEMBL385384() throws Exception {
        test("CCc1c(C#N)c(c2ccc(cc2)c3ccc(OC)cc3)c(C(=O)O)n1C",
             "CCC1=C(C#N)C(C2=CC=C(C=C2)C3=CC=C(OC)C=C3)=C(C(=O)O)N1C");
    }

    @Test public void imidazole() throws Exception {
        test("c1c[nH]cn1", "C1=C[NH]C=N1");
    }

    @Test public void benzimidazole() throws Exception {
        test("c1nc2ccccc2[nH]1", "C1=NC2=CC=CC=C2[NH]1");
    }

    @Test public void napthalene() throws Exception {
        test("c1ccc2ccccc2c1", "C1=CC=C2C=CC=CC2=C1");
    }

    @Test public void anthracene() throws Exception {
        test("c1ccc2cc3ccccc3cc2c1", "C1=CC=C2C=C3C=CC=CC3=CC2=C1");
    }

    @Test public void thiophene() throws Exception {
        test("s1cccc1", "S1C=CC=C1");
    }

    @Test public void imidazol_3_ium() throws Exception {
        test("c1c[nH+]c[nH]1", "C1=C[NH+]=C[NH]1");
    }

    @Test public void exocyclic_NO_bond() throws Exception {
        test("Nc1c2nc[nH]c2ncn1=O", "NC1=C2N=C[NH]C2=NC=N1=O");
    }

    @Test public void biphenyl() throws Exception {
        test("c1ccccc1c1ccccc1", "C1=CC=CC=C1C2=CC=CC=C2");
        test("c1ccccc1-c1ccccc1", "C1=CC=CC=C1-C2=CC=CC=C2");
    }

    @Test public void phospho_nitro_ring() throws Exception {
        test("n1pnpnp1", "N1=PN=PN=P1");
    }

    @Test public void phospho_nitro_ring_exocyclic_oxygen() throws Exception {
        test("n1p(O)(O)np(O)(O)np1(O)(O)", "N1=P(O)(O)N=P(O)(O)N=P1(O)O");
    }

    @Test public void hexamethylidenecyclohexane() throws Exception {
        test("cc1c(c)c(c)c(c)c(c)c1c", "C=C1C(=C)C(=C)C(=C)C(=C)C1=C");
        test("C=c1c(=C)c(=C)c(=C)c(=C)c1=C", "C=C1C(=C)C(=C)C(=C)C(=C)C1=C");
    }
    
    @Test(expected = InvalidSmilesException.class)
    public void eMolecules492140() throws Exception {
        test("c1ccc2c(c1)c1[n-]c2/N=c/2\\[n-]c(c3c2cccc3)/N=c/2\\[n-]/c(=N\\c3[n-]/c(=N\\1)/c1ccccc31)/c1c2cccc1.[Cu+4] 492140", "n/a");
    }

    @Test(expected = InvalidSmilesException.class)
    public void tryptophanyl_radical() throws Exception {
        test("NC(Cc1c[n]c2ccccc12)C(O)=O",
             "n/a");
    }

    @Test public void thiophene_oxide() throws Exception {
        test("O=s1cccc1",
             "O=S1C=CC=C1");
    }

    @Test public void trivalentBoronNoPiBonds() throws Exception {
        test("b1(C)ob(C)ob1(C)",
             "B1(C)OB(C)OB1C");
    }

    @Test public void tellurophene() throws Exception {
        test("[Te]1cccc1", "[Te]1C=CC=C1");
        test("[te]1cccc1", "[Te]1C=CC=C1");
    }

    @Test public void porphyrin1() throws Exception {
        test("c1cc2cc3ccc(cc4ccc(cc5ccc(cc1n2)[nH]5)n4)[nH]3",
             "C1=CC2=CC3=CC=C(C=C4C=CC(C=C5C=CC(=CC1=N2)[NH]5)=N4)[NH]3");
    }
    
    @Test public void CHEMBL438024() throws Exception {
        test("COC(=O)C1=C(C)NC(=C(C1c2c(nc3sccn23)c4cc(OC)ccc4OC)C(=O)OC)C",
             "COC(=O)C1=C(C)NC(=C(C1C2=C(N=C3SC=CN23)C4=CC(OC)=CC=C4OC)C(=O)OC)C");
    }

    @Test public void porphyrin2() throws Exception {
        test("c1cc2cc3ccc(cc4ccc(cc5ccc(cc1n2)n5)n4)n3",
             "C1=CC=2C=C3C=CC(C=C4C=CC(C=C5C=CC(C=C1N2)=N5)=N4)=N3");
    }

    // Sulphur with two double bonds
    @Test public void chembl_1188068() throws Exception {
        test("COc1cc2nc(ns(=O)(C)c2cc1OC)N3CCN(CC3)C(=O)c4oc(SC)nn4",
             "COC1=CC2=NC(=NS(=O)(C)=C2C=C1OC)N3CCN(CC3)C(=O)C=4OC(SC)=NN4");
    }

    // Sulphur cation with exo cyclic double bond
    @Test public void chembl_1434989() throws Exception {
        test("[O-][s+]1(=O)[nH]c2c(cc(Cl)c3ccc(Cl)nc23)c4ccccc14",
             "[O-][S+]1(=O)[NH]C2=C(C=C(Cl)C3=CC=C(Cl)N=C23)C4=CC=CC=C14");
    }

    @Test public void chembl_423544() throws Exception {
        test("CCc1n[c]#[c]n1CC2CC(C(=O)O2)(c3ccccc3)c4ccccc4",
             "CCC1=N[C]#[C]N1CC2CC(C(=O)O2)(C3=CC=CC=C3)C4=CC=CC=C4");
    }
    
    @Test public void chembl_422679() throws Exception {
        test("CCO/C(O)=C1\\C(COCCNc2n[s+]([O-])nc2OC)=NC(C)=C(C(=O)OC)C1c1cccc(Cl)c1Cl CHEMBL422679",
             "CCO/C(O)=C1\\C(COCCNC2=N[S+]([O-])N=C2OC)=NC(C)=C(C(=O)OC)C1C3=CC=CC(Cl)=C3Cl");
    }

    @Test public void tropylium() throws Exception {
        test("[cH+]1cccccc1", "[CH+]1C=CC=CC=C1");
    }

    /**
     * Test case from Noel that should fail Kekulization
     */
    @Test(expected = InvalidSmilesException.class)
    public void exocyclicCarbonFiveMemberRing() throws Exception {
        test("c1n(=C)ccc1", "n/a");
    }

    @Test
    public void exocyclicCarbonSixMemberRing() throws Exception {
        test("c1cn(=C)ccc1", "C1=CN(=C)=CC=C1");
    }

    @Test(expected = InvalidSmilesException.class)
    public void pyrole_invalid() throws Exception {
        test("n1cncc1", "n/a");
    }

    @Test(expected = InvalidSmilesException.class)
    public void imidazole_invalid() throws Exception {
        test("c1nc2ccccc2n1", "n/a");
    }

    @Test
    public void mixing_aromatic_and_aliphatic() throws Exception {
        test("c1=cc=cc=c1", "C1=CC=CC=C1");
        test("c-1c-cc-cc1", "C-1=C-C=C-C=C1");
        test("C:1:C:C:C:C:C1", "C1CCCCC1"); // XXX: not handled inplace
    }

    // http://sourceforge.net/mailarchive/forum.php?thread_name=60825b0f0709302037g2d68f2eamdb5ebecf3baea6d1%40mail.gmail.com&forum_name=blueobelisk-smiles
    @Test public void bezene_inconsistent() throws Exception {
        test("c1=ccccc1", "C1=CC=CC=C1");
        test("c1=cc=ccc1", "C1=CC=CC=C1");
        test("c1=cc=cc=c1", "C1=CC=CC=C1");
        test("c1=c:c:c:c:c1", "C1=CC=CC=C1");
        test("c1=c:c=c:c:c1", "C1=CC=CC=C1");
        test("c1=c-c=c:c:c1", "C1=C-C=CC=C1");
    }

    @Test public void fluorene() throws Exception {
        test("C1c2ccccc2-c3ccccc13", "C1C2=CC=CC=C2-C3=CC=CC=C13");
        test("C1c2ccccc2c3ccccc13", "C1C2=CC=CC=C2C3=CC=CC=C13");
    }

    @Test public void hexaoxane() throws Exception {
        test("o1ooooo1", "O1OOOOO1");
    }

    @Test public void pyrole_aliphatic_n() throws Exception {
        test("c1cNcc1", "C1=CNC=C1");
    }

    @Test public void furan_aliphatic_o() throws Exception {
        test("c1cOcc1", "C1=COC=C1");
    }

    @Test public void bo_25756() throws Exception {
        test("Nc1c2c3ccccc3c4cccc(cc1)c24",
             "NC1=C2C3=CC=CC=C3C=4C=CC=C(C=C1)C24");
    }
    
    /* Examples from http://www.daylight.com/dayhtml_tutorials/languages/smiles/smiles_examples.html */

    @Test public void viagra() throws Exception {
        test("CCc1nn(C)c2c(=O)[nH]c(nc12)c3cc(ccc3OCC)S(=O)(=O)N4CCN(C)CC4",
             "CCC1=NN(C)C=2C(=O)[NH]C(=NC12)C3=CC(=CC=C3OCC)S(=O)(=O)N4CCN(C)CC4");
    }

    @Test public void xanax() throws Exception {
        test("Cc1nnc2CN=C(c3ccccc3)c4cc(Cl)ccc4-n12",
             "CC1=NN=C2CN=C(C3=CC=CC=C3)C4=CC(Cl)=CC=C4-N12");
    }

    @Test public void phentermine() throws Exception {
        test("CC(C)(N)Cc1ccccc1",
             "CC(C)(N)CC1=CC=CC=C1");
    }

    @Test public void valium() throws Exception {
        test("CN1C(=O)CN=C(c2ccccc2)c3cc(Cl)ccc13",
             "CN1C(=O)CN=C(C2=CC=CC=C2)C3=CC(Cl)=CC=C13");
    }

    @Test public void ambien() throws Exception {
        test("CN(C)C(=O)Cc1c(nc2ccc(C)cn12)c3ccc(C)cc3",
             "CN(C)C(=O)CC1=C(N=C2C=CC(C)=CN12)C3=CC=C(C)C=C3");
    }

    @Test public void nexium() throws Exception {
        test("COc1ccc2[nH]c(nc2c1)S(=O)Cc3ncc(C)c(OC)c3C",
             "COC1=CC=C2[NH]C(=NC2=C1)S(=O)CC3=NC=C(C)C(OC)=C3C");
    }

    @Test public void vioxx() throws Exception {
        test("CS(=O)(=O)c1ccc(cc1)C2=C(C(=O)OC2)c3ccccc3",
             "CS(=O)(=O)C1=CC=C(C=C1)C2=C(C(=O)OC2)C3=CC=CC=C3");
    }

    @Test public void paxil() throws Exception {
        test("Fc1ccc(cc1)C2CCNCC2COc3ccc4OCOc4c3",
             "FC1=CC=C(C=C1)C2CCNCC2COC3=CC=C4OCOC4=C3");
    }

    @Test public void lipitor() throws Exception {
        test("CC(C)c1c(C(=O)Nc2ccccc2)c(c(c3ccc(F)cc3)n1CC[C@@H]4C[C@@H](O)CC(=O)O4)c5ccccc5",
             "CC(C)C1=C(C(=O)NC2=CC=CC=C2)C(=C(C3=CC=C(F)C=C3)N1CC[C@@H]4C[C@@H](O)CC(=O)O4)C5=CC=CC=C5");
    }

    @Test public void cialis() throws Exception {
        test("CN1CC(=O)N2[C@@H](c3[nH]c4ccccc4c3C[C@@H]2C1=O)c5ccc6OCOc6c5",
             "CN1CC(=O)N2[C@@H](C=3[NH]C4=CC=CC=C4C3C[C@@H]2C1=O)C5=CC=C6OCOC6=C5");
    }

    @Test public void strychnine() throws Exception {
        test("O=C1C[C@H]2OCC=C3CN4CC[C@@]56[C@H]4C[C@H]3[C@H]2[C@H]6N1c7ccccc75",
             "O=C1C[C@H]2OCC=C3CN4CC[C@]56[C@H]4C[C@H]3[C@H]2[C@H]5N1C7=CC=CC=C76");
    }

    @Test public void cocaine() throws Exception {
        test("COC(=O)[C@H]1[C@@H]2CC[C@H](C[C@@H]1OC(=O)c3ccccc3)N2C",
             "COC(=O)[C@H]1[C@@H]2CC[C@H](C[C@@H]1OC(=O)C3=CC=CC=C3)N2C");
    }

    @Test public void quinine() throws Exception {
        test("COc1ccc2nccc([C@@H](O)[C@H]3C[C@@H]4CCN3C[C@@H]4C=C)c2c1",
             "COC1=CC=C2N=CC=C([C@@H](O)[C@H]3C[C@@H]4CCN3C[C@@H]4C=C)C2=C1");
    }

    @Test public void lysergicAcid() throws Exception {
        test("CN1C[C@@H](C=C2[C@H]1Cc3c[nH]c4cccc2c34)C(=O)O",
             "CN1C[C@@H](C=C2[C@H]1CC3=C[NH]C4=CC=CC2=C34)C(=O)O");
    }

    @Test public void LSD() throws Exception {
        test("CCN(CC)C(=O)[C@H]1CN(C)[C@@H]2Cc3c[nH]c4cccc(C2=C1)c34",
             "CCN(CC)C(=O)[C@H]1CN(C)[C@@H]2CC3=C[NH]C4=CC=CC(C2=C1)=C34");
    }

    @Test public void morphine() throws Exception {
        test("CN1CC[C@]23[C@H]4Oc5c3c(C[C@@H]1[C@@H]2C=C[C@@H]4O)ccc5O",
             "CN1CC[C@@]23[C@H]4OC5=C2C(C[C@@H]1[C@@H]3C=C[C@@H]4O)=CC=C5O");
    }

    @Test public void heroin() throws Exception {
        test("CN1CC[C@]23[C@H]4Oc5c3c(C[C@@H]1[C@@H]2C=C[C@@H]4OC(=O)C)ccc5OC(=O)C",
             "CN1CC[C@@]23[C@H]4OC5=C2C(C[C@@H]1[C@@H]3C=C[C@@H]4OC(=O)C)=CC=C5OC(=O)C");
    }

    @Test public void nicotine() throws Exception {
        test("CN1CCC[C@H]1c2cccnc2",
             "CN1CCC[C@H]1C2=CC=CN=C2");
    }

    @Test public void caffeine() throws Exception {
        test("Cn1cnc2n(C)c(=O)n(C)c(=O)c12",
             "CN1C=NC=2N(C)C(=O)N(C)C(=O)C12");
    }

    // N,N-Diallylmelamine 
    @Test public void ncs4420() throws Exception {
        test("[nH2]c1nc(nc(n1)n(Ccc)Ccc)[nH2]",
             "[NH2]C1=NC(=NC(=N1)N(CC=C)CC=C)[NH2]");
    }

    @Test public void carbon_anion() throws Exception {
        test("O=c1cc[cH-]cc1",
             "O=C1C=C[CH-]C=C1");
        test("oc1cc[cH-]cc1",
             "O=C1C=C[CH-]C=C1");
    }
    
    @Test public void sulphur_cation() throws Exception {
        test("CC(C)(C)c1cc2c3[s-](oc2=O)oc(=O)c3c1",
             "CC(C)(C)C1=CC2=C3[S-](OC2=O)OC(=O)C3=C1");
    }
    
    @Test public void nitrogenRadical() throws Exception {
        test("c1cc(c([n+]c1)N)[N+](=O)[O-]",
             "C1=CC(=C([N+]=C1)N)[N+](=O)[O-]");
    }

    @Test public void acyclicValence() throws Exception {
        test("[cH3]cc", "[CH3]C=C");
        test("[cH2+][cH2-]", "[CH2+][CH2-]");
        test("[cH+][cH-]", "[CH+]=[CH-]");
        test("[nH2]cc", "[NH2]C=C");
        test("[oH]cc", "[OH]C=C");
    }
    
    @Test public void smallRingTest_5() throws Exception {
        Graph g = Graph.fromSmiles("C1CCCC1");
        assertTrue(Localise.inSmallRing(g, g.edge(0, 1)));
    }

    @Test public void smallRingTest_7() throws Exception {
        Graph g = Graph.fromSmiles("C1CCCCCC1");
        assertTrue(Localise.inSmallRing(g, g.edge(0, 1)));
    }

    @Test public void smallRingTest_8() throws Exception {
        Graph g = Graph.fromSmiles("C1CCCCCCC1");
        assertFalse(Localise.inSmallRing(g, g.edge(0, 1)));
    }

    @Test public void smallRingTest_linked() throws Exception {
        Graph g = Graph.fromSmiles("C1CCC(CC1)=C1CCCCC1");
        assertFalse(Localise.inSmallRing(g, g.edge(3, 6)));
    }

    @Test public void anyatom() throws Exception {
        test("*1ccccc1", "*1=CC=CC=C1");
    }

    @Test public void aromphos() throws Exception {
      test("O=p1ccccc1", "O=P1=CC=CC=C1");
    }

    static void test(String delocalised, String localised) throws Exception {
        Graph g = Graph.fromSmiles(delocalised);
        Graph h = Localise.localise(g);
        assertThat(h.toSmiles(), is(localised));
    }

    /**
     * Example from Noel's analysis generate with RDKit, the hcount changes before/after
     * kekulization. This was problematic because explicit single bonds were incorrectly
     * present in the SMILES string. Nether the less it should cause a problem.
     * @throws InvalidSmilesException
     */
    @Test public void unchangedHydrogenCount() throws InvalidSmilesException
    {
        String smi = "c12c3c4c5c6c7c-4c4c8c9c%10c%11c%12c%13c%14c%15c%16c%17c%18c%19c%20c(c1c1c%21c%20c%20c%22c%19c%16c%16c%22c%19c%22c%20c=%21c(c8c%22c%10c%19c%12c%14%16)C48C31CC(=N)C=C8)c-%18c1c2c5c2c3c6c(c%11c79)c%13c-3c%15C%17C12";
        Graph  g1   = Graph.fromSmiles(smi);
        Graph  g2   = Graph.fromSmiles(smi).kekule();
        for (int i = 0; i < g1.order(); i++)
            assertThat("Atom idx=" + i + " had a different hydrogen count before/after kekulization",
                       g1.implHCount(i),
                       is(g2.implHCount(i)));
    }


    @Test public void biphenylene() throws IOException
    {
        String smi = "c1cccc2-c3ccccc3-c12";
        assertThat(Graph.fromSmiles(smi).kekule().toSmiles(),
                   is("C1=CC=CC=2-C3=CC=CC=C3-C12"));
    }

}
