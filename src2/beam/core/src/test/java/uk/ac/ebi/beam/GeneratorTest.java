package uk.ac.ebi.beam;

import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/** @author John May */
public class GeneratorTest {

    @Test public void permuteTH_3_nonRing() throws Exception {
        String input = "C[C@H](N)O";
        Graph g = Parser.parse(input);
        assertThat(Generator.generate(g), is(input));
    }

    @Test public void permuteTH_4_nonRing() throws Exception {
        String input = "C[C@]([H])(N)O";
        Graph g = Parser.parse(input);
        assertThat(Generator.generate(g), is(input));
    }

    @Test public void permuteTH_4_ring() throws Exception {
        String input = "C[C@]12CCCC[C@@]1(C)OCCC2";
        Graph g = Parser.parse(input);
        assertThat(Generator.generate(g), is(input));
    }

    @Ignore public void test() throws InvalidSmilesException {
        System.out.println(randomPermutations("[C@]([H])(N)(C)C(=O)O", 50));
    }

    @Ignore public void test2() throws InvalidSmilesException {
        System.out.println(randomPermutations("[C@H](N)(C)C(=O)O", 50));
    }

    @Ignore public void test3() throws InvalidSmilesException {
        System.out.println(randomPermutations("[C@H]12CCCC[C@@]1(C)OCCC2", 50));
    }

    @Test public void implicitHCentre() throws InvalidSmilesException {

        assertRoundTrip("[C@@H](N)(O)C");

        // permutations
        assertRoundTrip("[C@@H](N)(O)C", new int[]{0, 1, 2, 3}, "[C@@H](N)(O)C");
        assertRoundTrip("[C@@H](N)(O)C", new int[]{0, 1, 3, 2}, "[C@H](N)(C)O");
        assertRoundTrip("[C@@H](N)(O)C", new int[]{0, 2, 1, 3}, "[C@H](O)(N)C");
        assertRoundTrip("[C@@H](N)(O)C", new int[]{0, 2, 3, 1}, "[C@@H](C)(N)O");
        assertRoundTrip("[C@@H](N)(O)C", new int[]{0, 3, 1, 2}, "[C@@H](O)(C)N");
        assertRoundTrip("[C@@H](N)(O)C", new int[]{0, 3, 2, 1}, "[C@H](C)(O)N");

        assertRoundTrip("[C@@H](N)(O)C", new int[]{1, 0, 2, 3}, "N[C@H](O)C");
        assertRoundTrip("[C@@H](N)(O)C", new int[]{1, 0, 3, 2}, "N[C@@H](C)O");

        assertRoundTrip("[C@@H](N)(O)C", new int[]{1, 2, 0, 3}, "O[C@@H](N)C");
        assertRoundTrip("[C@@H](N)(O)C", new int[]{1, 3, 0, 2}, "O[C@H](C)N");

        assertRoundTrip("[C@@H](N)(O)C", new int[]{1, 2, 3, 0}, "C[C@H](N)O");
        assertRoundTrip("[C@@H](N)(O)C", new int[]{1, 3, 2, 0}, "C[C@@H](O)N");

        assertRoundTrip("[C@H](N)(C)O");

        assertRoundTrip("[C@H](N)(C)O", new int[]{0, 1, 2, 3}, "[C@H](N)(C)O");
        assertRoundTrip("[C@H](N)(C)O", new int[]{0, 1, 3, 2}, "[C@@H](N)(O)C");
        assertRoundTrip("[C@H](N)(C)O", new int[]{0, 2, 1, 3}, "[C@@H](C)(N)O");
        assertRoundTrip("[C@H](N)(C)O", new int[]{0, 2, 3, 1}, "[C@H](O)(N)C");
        assertRoundTrip("[C@H](N)(C)O", new int[]{0, 3, 1, 2}, "[C@H](C)(O)N");
        assertRoundTrip("[C@H](N)(C)O", new int[]{0, 3, 2, 1}, "[C@@H](O)(C)N");

        assertRoundTrip("[C@H](N)(C)O", new int[]{1, 0, 2, 3}, "N[C@@H](C)O");
        assertRoundTrip("[C@H](N)(C)O", new int[]{1, 0, 3, 2}, "N[C@H](O)C");

        assertRoundTrip("[C@H](N)(C)O", new int[]{1, 2, 0, 3}, "C[C@H](N)O");
        assertRoundTrip("[C@H](N)(C)O", new int[]{1, 3, 0, 2}, "C[C@@H](O)N");

        assertRoundTrip("[C@H](N)(C)O", new int[]{1, 2, 3, 0}, "O[C@@H](N)C");
        assertRoundTrip("[C@H](N)(C)O", new int[]{1, 3, 2, 0}, "O[C@H](C)N");

        assertRoundTrip("N[C@@H](C)O");
        assertRoundTrip("N[C@@H](C)O");
        assertRoundTrip("N[C@H](O)C");
        assertRoundTrip("O[C@@H](N)C");
        assertRoundTrip("O[C@H](C)N");
        assertRoundTrip("C[C@@H](O)N");
        assertRoundTrip("C[C@H](N)O");
    }

    @Test public void ring_closures1() throws Exception {
        assertRoundTrip("C1=CN=CC2=NC=N[C@@H]21");
    }

    @Test public void ring_closures2() throws Exception {
        assertRoundTrip("C1=CN=CC2=NC=N[C@H]21");
    }

    @Test public void ring_closures3() throws Exception {
        assertRoundTrip("C1=CC(=CC2=NC(=N[C@@H]21)C(F)(F)F)N");
    }

    @Test public void ring_closures4() throws Exception {
        assertRoundTrip("C1=CC(=CC2=NC(=N[C@H]21)C(F)(F)F)N");
    }


    @Test public void lowRingNumberOrder() throws InvalidSmilesException {
        assertRoundTrip("C1=CC2=CC=CC=C2C=C1");
    }

    @Test public void multipleRingNumberOrder() throws InvalidSmilesException {
        assertRoundTrip("C1=CC2=C3C4=C5C(C=CC6=C5C7=C(C=C6)C=CC(C=C2)=C37)=CC=C14");
    }

    @Test public void highRingNumberOrder() throws InvalidSmilesException {
        assertRoundTrip("C1CC2CCC3=C4C2=C5C1CCC6=C5C7=C8C(C=C9CCC%10CCC%11CCC%12=CC(=C3)C(C%13=C8C9=C%10C%11=C%12%13)=C47)=C6");
    }

    @Test public void bondTypeOnFirstAtom1() throws InvalidSmilesException {
        String smi = "C1C=CC=CC=1";
        String exp = "C=1C=CC=CC1";
        assertThat(Generator.generate(Parser.parse(smi)), is(exp));
    }

    @Test public void bondTypeOnFirstAtom2() throws InvalidSmilesException {
        String smi = "C=1C=CC=CC1";
        String exp = "C=1C=CC=CC1";
        assertThat(Generator.generate(Parser.parse(smi)), is(exp));
    }

    @Test public void bondTypeOnFirstAtom3() throws InvalidSmilesException {
        String smi = "C=1C=CC=CC=1";
        String exp = "C=1C=CC=CC1";
        assertThat(Generator.generate(Parser.parse(smi)), is(exp));
    }

    @Test public void directionalBondTypeOnFirstAtom1() throws
                                                        InvalidSmilesException {
        String smi = "C1CCCCCCCCCCC\\C=C/1";
        String exp = "C\\1CCCCCCCCCCC\\C=C1";
        assertThat(Generator.generate(Parser.parse(smi)), is(exp));
    }

    @Test public void directionalBondTypeOnFirstAtom2() throws
                                                        InvalidSmilesException {
        String smi = "C\\1CCCCCCCCCCC\\C=C1";
        String exp = "C\\1CCCCCCCCCCC\\C=C1";
        assertThat(Generator.generate(Parser.parse(smi)), is(exp));
    }

    @Test public void directionalBondTypeOnFirstAtom3() throws
                                                        InvalidSmilesException {
        String smi = "C\\1CCCCCCCCCCC\\C=C/1";
        String exp = "C\\1CCCCCCCCCCC\\C=C1";
        assertThat(Generator.generate(Parser.parse(smi)), is(exp));
    }

    @Test public void reuseNumbering() throws IOException {
        Generator generator = new Generator(Graph.fromSmiles("c1cc1c2ccc2"),
                                            new Generator.ReuseRingNumbering(1));
        assertThat(generator.string(), is("c1cc1c1ccc1"));
    }

    @Test public void sodiumChloride() throws InvalidSmilesException {
        assertRoundTrip("[Na+].[Cl-]");
    }

    @Test public void disconnected() throws InvalidSmilesException {
        assertRoundTrip("CCCC.OOOO.C[CH]C.CNO");
    }
    
    @Test public void extendedTetrhedral_al1() throws Exception {
        Graph g = Graph.fromSmiles("CC=[C]=CC");
        g.addTopology(Topology.extendedTetrahedral(2, new int[]{0, 1, 3, 4}, Configuration.AL1));
        g.setFlags(Graph.HAS_EXT_STRO);
        assertThat(g.toSmiles(), is("CC=[C@]=CC"));
    }

    @Test public void extendedTetrhedral_al2() throws Exception {
        Graph g = Graph.fromSmiles("CC=[C]=CC");
        g.setFlags(Graph.HAS_EXT_STRO);
        g.addTopology(Topology.extendedTetrahedral(2, new int[]{0, 1, 3, 4}, Configuration.AL2));
        assertThat(g.toSmiles(), is("CC=[C@@]=CC"));
    }

    @Test public void extendedTetrhedral_al1_permute_1() throws Exception {
        Graph g = Graph.fromSmiles("CC=[C]=CC");
        g.addTopology(Topology.extendedTetrahedral(2, new int[]{0, 1, 3, 4}, Configuration.AL1));
        g.setFlags(Graph.HAS_EXT_STRO);
        g = g.permute(new int[]{1, 0, 2, 3, 4});
        assertThat(g.toSmiles(), is("C(C)=[C@@]=CC"));
    }

    @Test public void extendedTetrhedral_al1_inv_permute_1() throws Exception {
        Graph g = Graph.fromSmiles("C(C)=[C]=CC");
        g.addTopology(Topology.extendedTetrahedral(2, new int[]{0, 1, 3, 4}, Configuration.AL1));
        g.setFlags(Graph.HAS_EXT_STRO);
        g = g.permute(new int[]{1, 0, 2, 3, 4});
        assertThat(g.toSmiles(), is("CC=[C@@]=CC"));
    }

    @Test public void extendedTetrhedral_al1_permute_2() throws Exception {
        Graph g = Graph.fromSmiles("CC=[C]=CC");
        g.addTopology(Topology.extendedTetrahedral(2, new int[]{0, 1, 3, 4}, Configuration.AL1));
        g.setFlags(Graph.HAS_EXT_STRO);
        g = g.permute(new int[]{4, 3, 2, 1, 0});
        assertThat(g.toSmiles(), is("CC=[C@]=CC"));
    }

    @Test public void extendedTetrhedral_al1_permute_3() throws Exception {
        Graph g = Graph.fromSmiles("CC=[C]=CC");
        g.addTopology(Topology.extendedTetrahedral(2, new int[]{0, 1, 3, 4}, Configuration.AL1));
        g.setFlags(Graph.HAS_EXT_STRO);
        g = g.permute(new int[]{4, 3, 2, 0, 1});
        assertThat(g.toSmiles(), is("C(C)=[C@@]=CC"));
    }

    @Test public void resetRingNumbersBetweenComponents1() throws Exception {
        Graph g = Graph.fromSmiles("C1CC1.C1CC1");
        assertThat(new Generator(g, new Generator.ReuseRingNumbering(1)).string(),
                   is("C1CC1.C1CC1"));
    }

    @Test public void resetRingNumbersBetweenComponents2() throws Exception {
        Graph g = Graph.fromSmiles("C1CC1.C1CC1");
        assertThat(new Generator(g, new Generator.IterativeRingNumbering(1)).string(),
                   is("C1CC1.C1CC1"));
    }

    @Test public void reusingNumbering() throws InvalidSmilesException {
        Generator.RingNumbering rnums = new Generator.ReuseRingNumbering(0);
        for (int i = 0; i < 50; i++) {
            int rnum = rnums.next();
            assertThat(rnum, is(i));
            rnums.use(rnum);
        }
        rnums.free(40);
        rnums.free(20);
        rnums.free(4);
        assertThat(rnums.next(), is(4));
        rnums.use(4);
        assertThat(rnums.next(), is(20));
        rnums.use(20);
        assertThat(rnums.next(), is(40));
        rnums.use(40);
        for (int i = 50; i < 100; i++) {
            int rnum = rnums.next();
            assertThat(rnum, is(i));
            rnums.use(rnum);
        }
    }

    @Test public void iterativeNumbering() throws InvalidSmilesException {
        Generator.RingNumbering rnums = new Generator.IterativeRingNumbering(0);
        for (int i = 0; i < 50; i++) {
            int rnum = rnums.next();
            assertThat(rnum, is(i));
            rnums.use(rnum);
        }
        rnums.free(40);
        rnums.free(25);
        assertThat(rnums.next(), is(50));
        rnums.use(50);
        assertThat(rnums.next(), is(51));
        rnums.use(51);
        assertThat(rnums.next(), is(52));
        rnums.use(52);
        for (int i = 53; i < 100; i++) {
            int rnum = rnums.next();
            assertThat(rnum, is(i));
            rnums.use(rnum);
        }
        rnums.free(20);
        rnums.free(5);
        assertThat(rnums.next(), is(5));
        rnums.use(5);
        assertThat(rnums.next(), is(20));
        rnums.use(20);
        assertThat(rnums.next(), is(25));
        rnums.use(25);
        assertThat(rnums.next(), is(40));
        rnums.use(40);
    }

    @Test(expected = InvalidSmilesException.class)
    public void maxRingNumbers() throws InvalidSmilesException {
        Generator.RingNumbering rnums = new Generator.IterativeRingNumbering(0);
        for (int i = 0; i < 101; i++) {
            int rnum = rnums.next();
            rnums.use(rnum);
        }
    }

    @Test public void alleneStereochemistryWithRingClosures() throws Exception {
        Graph g = Graph.fromSmiles("CC=[C@]=C1OCCCC1");
        Topology topology = g.topologyOf(2);
        assertThat(g.toSmiles(), is("CC=[C@]=C1OCCCC1"));
    }

    static void assertRoundTrip(String smi) throws InvalidSmilesException {
        assertThat(Generator.generate(Parser.parse(smi)), is(smi));
    }

    static void assertRoundTrip(String smi, int[] p, String res) throws
                                                           InvalidSmilesException {
        assertThat(Generator.generate(Parser.parse(smi).permute(p)), is(res));
    }

    @Test public void hExpand() throws Exception {
        assertThat(Graph.fromSmiles("[HH]").toSmiles(),
                   CoreMatchers.is("[H][H]"));
        assertThat(Graph.fromSmiles("[HH2]").toSmiles(),
                   CoreMatchers.is("[H]([H])[H]"));
        assertThat(Graph.fromSmiles("[2HH]").toSmiles(),
                   CoreMatchers.is("[2H][H]"));
    }

    /**
     * Generate random permutations of the molecule.
     *
     * @param input input SMILES
     * @param n     number of generations (how many molecules to produce)
     * @return a single SMILES string of disconnected molecules (input) randomly
     *         permuted
     * @throws InvalidSmilesException the input SMILES was invalid
     */
    private static String randomPermutations(String input, int n) throws
                                                                  InvalidSmilesException {
        Graph g = Parser.parse(input);
        StringBuilder sb = new StringBuilder();
        sb.append(Generator.generate(g));
        for (int i = 0; i < n; i++) {
            sb.append('.');
            int[] p = random(g.order());
            String smi = Generator.generate(g.permute(p));
            g = Parser.parse(smi);
            sb.append(smi);
        }
        return sb.toString();
    }

    static int[] ident(int n) {
        int[] p = new int[n];
        for (int i = 0; i < n; i++)
            p[i] = i;
        return p;
    }

    static int[] random(int n) {
        int[] p = ident(n);
        Random rnd = new Random();
        for (int i = n; i > 1; i--)
            swap(p, i - 1, rnd.nextInt(i));
        return p;
    }

    static int[] inv(int[] p) {
        int[] q = p.clone();
        for (int i = 0; i < p.length; i++)
            q[p[i]] = i;
        return q;
    }

    static void swap(int[] p, int i, int j) {
        int tmp = p[i];
        p[i] = p[j];
        p[j] = tmp;
    }

}
