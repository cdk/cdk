package org.openscience.cdk.test.pharmacophore;

import org.junit.*;
import org.openscience.cdk.ConformerContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingMDLConformerReader;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.pharmacophore.PharmacophoreAtom;
import org.openscience.cdk.pharmacophore.PharmacophoreMatcher;
import org.openscience.cdk.pharmacophore.PharmacophoreQueryAtom;
import org.openscience.cdk.pharmacophore.PharmacophoreQueryBond;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * @cdk.module test-pcore
 */
public class PharmacophoreMatcherTest {

    public static ConformerContainer conformers = null;

       @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @BeforeClass
    public static void loadConformerData() {
        String filename = "data/mdl/pcoretest1.sdf";
        InputStream ins = PharmacophoreMatcherTest.class.getClassLoader().getResourceAsStream(filename);
        IteratingMDLConformerReader reader = new IteratingMDLConformerReader(ins, DefaultChemObjectBuilder.getInstance());
        if (reader.hasNext()) conformers = (ConformerContainer) reader.next();
    }


    @Test
    public void testMatcherQuery1() throws CDKException {
        Assert.assertNotNull(conformers);

        // make a query
        QueryAtomContainer query = new QueryAtomContainer();

        PharmacophoreQueryAtom o = new PharmacophoreQueryAtom("D", "[OX1]");
        PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("A", "[N]");
        PharmacophoreQueryAtom n2 = new PharmacophoreQueryAtom("A", "[N]");

        query.addAtom(o);
        query.addAtom(n1);
        query.addAtom(n2);

        PharmacophoreQueryBond b1 = new PharmacophoreQueryBond(o, n1, 4.0, 4.5);
        PharmacophoreQueryBond b2 = new PharmacophoreQueryBond(o, n2, 4.0, 5.0);
        PharmacophoreQueryBond b3 = new PharmacophoreQueryBond(n1, n2, 5.4, 5.8);

        query.addBond(b1);
        query.addBond(b2);
        query.addBond(b3);

        PharmacophoreMatcher matcher = new PharmacophoreMatcher(query);
        boolean[] statuses = matcher.matches(conformers);

        Assert.assertEquals(100, statuses.length);

        int[] hits = new int[18];
        int idx = 0;
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i]) hits[idx++] = i;
        }


        int[] expected = {0, 1, 2, 5, 6, 7, 8, 9, 10, 20, 23, 48, 62, 64, 66, 70, 76, 87};
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals("Hit " + i + " didn't match", expected[i], hits[i]);
        }
    }

    @Test
    public void testMatchedAtoms() throws CDKException {
        Assert.assertNotNull(conformers);

        // make a query
        QueryAtomContainer query = new QueryAtomContainer();

        PharmacophoreQueryAtom o = new PharmacophoreQueryAtom("D", "[OX1]");
        PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("A", "[N]");
        PharmacophoreQueryAtom n2 = new PharmacophoreQueryAtom("A", "[N]");

        query.addAtom(o);
        query.addAtom(n1);
        query.addAtom(n2);

        PharmacophoreQueryBond b1 = new PharmacophoreQueryBond(o, n1, 4.0, 4.5);
        PharmacophoreQueryBond b2 = new PharmacophoreQueryBond(o, n2, 4.0, 5.0);
        PharmacophoreQueryBond b3 = new PharmacophoreQueryBond(n1, n2, 5.4, 5.8);

        query.addBond(b1);
        query.addBond(b2);
        query.addBond(b3);

        IAtomContainer conf1 = conformers.get(0);
        PharmacophoreMatcher matcher = new PharmacophoreMatcher(query);
        boolean status = matcher.matches(conf1);
        Assert.assertTrue(status);

        List<List<PharmacophoreAtom>> pmatches = matcher.getMatchingPharmacophoreAtoms();
        Assert.assertEquals(2, pmatches.size());

        List<List<PharmacophoreAtom>> upmatches = matcher.getUniqueMatchingPharmacophoreAtoms();
        Assert.assertEquals(1, upmatches.size());

    }

    @Test(expected = CDKException.class)
    public void testInvalidQuery() throws CDKException {
        QueryAtomContainer query = new QueryAtomContainer();

        PharmacophoreQueryAtom o = new PharmacophoreQueryAtom("D", "[OX1]");
        PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("A", "[N]");
        PharmacophoreQueryAtom n2 = new PharmacophoreQueryAtom("A", "[NX3]");

        query.addAtom(o);
        query.addAtom(n1);
        query.addAtom(n2);

        PharmacophoreQueryBond b1 = new PharmacophoreQueryBond(o, n1, 4.0, 4.5);
        PharmacophoreQueryBond b2 = new PharmacophoreQueryBond(o, n2, 4.0, 5.0);
        PharmacophoreQueryBond b3 = new PharmacophoreQueryBond(n1, n2, 5.4, 5.8);

        query.addBond(b1);
        query.addBond(b2);
        query.addBond(b3);

        PharmacophoreMatcher matcher = new PharmacophoreMatcher(query);
        matcher.matches(conformers);
    }

    @Test
    public void testCNSPcore() throws FileNotFoundException, CDKException {
        String filename = "data/mdl/cnssmarts.sdf";
        InputStream ins = PharmacophoreMatcherTest.class.getClassLoader().getResourceAsStream(filename);
        IteratingMDLReader reader = new IteratingMDLReader(ins,
                DefaultChemObjectBuilder.getInstance());

        QueryAtomContainer query = new QueryAtomContainer();
        PharmacophoreQueryAtom arom = new PharmacophoreQueryAtom("A", "c1ccccc1");
        PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("BasicAmine", "[NX3;h2,h1,H1,H2;!$(NC=O)]");
        PharmacophoreQueryBond b1 = new PharmacophoreQueryBond(arom, n1, 5.0, 7.0);
        query.addAtom(arom);
        query.addAtom(n1);
        query.addBond(b1);

        reader.hasNext();
        IAtomContainer mol = (IAtomContainer) reader.next();
        HueckelAromaticityDetector.detectAromaticity(mol);

        PharmacophoreMatcher matcher = new PharmacophoreMatcher(query);
        boolean status = matcher.matches(mol);
        Assert.assertTrue(status);

        List<List<PharmacophoreAtom>> pmatches = matcher.getMatchingPharmacophoreAtoms();
        Assert.assertEquals(1, pmatches.size());

        List<List<PharmacophoreAtom>> upmatches = matcher.getUniqueMatchingPharmacophoreAtoms();
        Assert.assertEquals(1, upmatches.size());

    }
}
