package uk.ac.ebi.beam;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/** @author John May */
public class ToSubsetAtomsTest {

    @Test public void unknown() throws Exception {
        transform("[*]", "*");
    }

    @Test public void inorganic() throws Exception {
        transform("[Ne]", "[Ne]");
    }

    @Test public void methane() throws Exception {
        transform("[CH4]", "C");
    }

    @Test public void monovalent_carbon() throws Exception {
        transform("[CH3]", "[CH3]");
    }

    @Test public void divalent_carbon() throws Exception {
        transform("[CH2]", "[CH2]");
    }

    @Test public void trivalent_carbon() throws Exception {
        transform("[CH]", "[CH]");
        transform("[CH1]", "[CH]");
    }

    @Test public void carbon_12() throws Exception {
        // note the isotope is specified and so must be a bracket atom
        transform("[12C]", "[12C]");
    }

    @Test public void carbon_13() throws Exception {
        transform("[13C]", "[13C]");
    }

    @Test public void carbon_14() throws Exception {
        transform("[14C]", "[14C]");
    }

    @Test public void oxidanide() throws Exception {
        transform("[OH-]", "[OH-]");
    }

    @Test public void azanium() throws Exception {
        transform("[NH4+]", "[NH4+]");
    }

    @Test public void ethane_withAtomClass() throws Exception {
        transform("[CH3:1][CH3:0]", "[CH3:1]C");
    }

    @Test public void ethanol() throws InvalidSmilesException {
        transform("[CH3][CH2][OH]", "CCO");
    }

    @Test public void stereoSpecification() throws InvalidSmilesException {
        transform("[C@H]([NH2])([OH])[CH3]", "[C@H](N)(O)C");
        transform("[C@@H]([NH2])([OH])[CH3]", "[C@@H](N)(O)C");
    }

    @Test public void noStereoSpecification() throws InvalidSmilesException {
        transform("[CH]([NH2])([OH])[CH3]", "C(N)(O)C");
    }

    @Test public void tricyclazole() throws Exception {
        transform("[CH3][c]1[cH][cH][cH][c]2[s][c]3[n][n][cH][n]3[c]12",
                  "Cc1cccc2sc3nncn3c12");
    }

    @Test public void pyrole_kekule() throws Exception {
        transform("[NH]1[CH]=[CH]N=[CH]1",
                  "N1C=CN=C1");
    }

    @Test public void pyrole() throws Exception {
        transform("[nH]1[cH][cH][n][cH]1",
                  "[nH]1ccnc1");
    }

    @Test public void zinc_1() throws Exception {
        transform("c1cc(ccc1/C=c\\2/c(=O)o/c(=C\\Cl)/[nH]2)F",
                  "c1cc(ccc1/C=c\\2/c(=O)o/c(=C\\Cl)/[nH]2)F");
    }


    private void transform(String input, String expected) throws
                                                          InvalidSmilesException {
        Graph g = Parser.parse(input);
        ImplicitToExplicit ite = new ImplicitToExplicit();
        ToSubsetAtoms tsa = new ToSubsetAtoms();
        ExplicitToImplicit eti = new ExplicitToImplicit();
        String actual = Generator.generate(eti.apply(
                tsa.apply(
                        ite.apply(g))));
        Assert.assertThat(actual, CoreMatchers.is(expected));
    }

}
