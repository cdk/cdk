package uk.ac.ebi.beam;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** @author John May */
public class FromSubsetAtomsTest {

    @Test public void unknown() throws Exception {
        transform("*", "[*]");
    }

    @Test public void inorganic() throws Exception {
        transform("[Ne]", "[Ne]");
    }

    @Test public void methane() throws Exception {
        transform("C", "[CH4]");
    }

    @Test public void ethane_withAtomClass() throws Exception {
        transform("[CH3:1]C", "[CH3:1][CH3]");
    }

    @Test public void ethanol() throws InvalidSmilesException {
        transform("CCO", "[CH3][CH2][OH]");
    }

    @Test public void stereoSpecification() throws InvalidSmilesException {
        transform("[C@H](N)(O)C", "[C@H]([NH2])([OH])[CH3]");
        transform("[C@@H](N)(O)C", "[C@@H]([NH2])([OH])[CH3]");
    }

    @Test public void noStereoSpecification() throws InvalidSmilesException {
        transform("C(N)(O)C", "[CH]([NH2])([OH])[CH3]");
    }

    @Test public void bracketAtom() {
        // should provide identity of bracket atom
        Atom input = new AtomImpl.BracketAtom(Element.Carbon, 1, 0);
        Atom output = FromSubsetAtoms.fromSubset(input, 0, 0);
        Assert.assertThat(input, CoreMatchers.is(CoreMatchers
                                                         .sameInstance(output)));
    }

    @Test public void aliphatic_carbon() {
        Atom actual = FromSubsetAtoms
                .fromSubset(AtomImpl.AliphaticSubset.Carbon, 3, 0);
        Atom expect = new AtomImpl.BracketAtom(Element.Carbon, 1, 0);
        Assert.assertThat(expect, CoreMatchers.is(actual));
    }

    @Test public void aromatic_carbon() {
        Atom actual = FromSubsetAtoms.fromSubset(AtomImpl.AromaticSubset.Carbon, 2, 0);
        Atom expect = new AtomImpl.BracketAtom(-1, Element.Carbon, 1, 0, 0, true);
        Assert.assertThat(expect, CoreMatchers.is(actual));
    }

    @Test public void indolizine() throws InvalidSmilesException {
        transform("c2cc1cccn1cc2",
                  "[cH]1[cH][c]2[cH][cH][cH][n]2[cH][cH]1");
    }

    @Test public void indolizine_kekule() throws InvalidSmilesException {
        transform("C1=CN2C=CC=CC2=C1",
                  "[CH]1=[CH][N]2[CH]=[CH][CH]=[CH][C]2=[CH]1");
    }

    @Test public void _1H_imidazole() throws InvalidSmilesException {
        transform("[H]n1ccnc1",
                  "[H][n]1[cH][cH][n][cH]1");
    }

    @Test public void _1H_imidazole_kekule() throws InvalidSmilesException {
        transform("[H]N1C=CN=C1",
                  "[H][N]1[CH]=[CH][N]=[CH]1");
    }

    @Test public void cdk_bug_1363882() throws Exception{
        transform("[H]c2c([H])c(c1c(nc(n1([H]))C(F)(F)F)c2Cl)Cl",
                  "[H][c]1[c]([H])[c]([c]2[c]([n][c]([n]2[H])[C]([F])([F])[F])[c]1[Cl])[Cl]");
    }

    @Test public void cdk_bug_1579235() throws Exception{
        transform("c2cc1cccn1cc2",
                  "[cH]1[cH][c]2[cH][cH][cH][n]2[cH][cH]1");
    }

    @Test public void sulphur() throws Exception {
        transform("S([H])[H]",
                  "[S]([H])[H]");
        transform("[H]S([H])[H]",
                  "[H][SH]([H])[H]");
        transform("[H]S([H])([H])[H]",
                  "[H][S]([H])([H])[H]");
        transform("[H]S([H])([H])([H])[H]",
                  "[H][SH]([H])([H])([H])[H]");
        transform("[H]S([H])([H])([H])([H])[H]",
                  "[H][S]([H])([H])([H])([H])[H]");
    }

    @Test public void tricyclazole() throws InvalidSmilesException {
        transform("Cc1cccc2sc3nncn3c12",
                  "[CH3][c]1[cH][cH][cH][c]2[s][c]3[n][n][cH][n]3[c]12");
    }

    @Test public void tricyclazole_kekule() throws InvalidSmilesException {
        transform("CC1=C2N3C=NN=C3SC2=CC=C1",
                  "[CH3][C]1=[C]2[N]3[CH]=[N][N]=[C]3[S][C]2=[CH][CH]=[CH]1");
    }

    @Ignore("bad molecule - should have utility to find/fix this types of errors")
    public void mixingAromaticAndKekule() throws Exception {
        transform("c1=cc=cc=c1",
                  "[cH]1=[cH][cH]=[cH][cH]=[cH]1");
    }

    @Test public void quinone() throws Exception {
        transform("oc1ccc(o)cc1",
                  "[o][c]1[cH][cH][c]([o])[cH][cH]1");
    }

    /** 1-(1H-pyrrol-2-yl)pyrrole */
    @Test public void pyroles() throws Exception {
        transform("c1ccn(c1)-c1ccc[nH]1",
                  "[cH]1[cH][cH][n]([cH]1)-[c]2[cH][cH][cH][nH]2");
    }

    @Test public void cdk_bug_956926() throws InvalidSmilesException {
        transform("[c+]1ccccc1",
                  "[c+]1[cH][cH][cH][cH][cH]1");
    }



    private void transform(String input, String expected) throws
                                                          InvalidSmilesException {
        Graph g = Parser.parse(input);
        ImplicitToExplicit ite = new ImplicitToExplicit();
        FromSubsetAtoms fsa = new FromSubsetAtoms();
        ExplicitToImplicit eti = new ExplicitToImplicit();
        String actual = Generator.generate(eti.apply(
                fsa.apply(
                        ite.apply(g))));
        Assert.assertThat(actual, CoreMatchers.is(expected));
    }

}
