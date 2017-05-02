/* Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.pharmacophore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ConformerContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.iterator.IteratingMDLConformerReader;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * @cdk.module test-pcore
 */
public class PharmacophoreMatcherTest {

    public static ConformerContainer conformers = null;

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @BeforeClass
    public static void loadConformerData() {
        String filename = "data/mdl/pcoretest1.sdf";
        InputStream ins = PharmacophoreMatcherTest.class.getClassLoader().getResourceAsStream(filename);
        IteratingMDLConformerReader reader = new IteratingMDLConformerReader(ins,
                SilentChemObjectBuilder.getInstance());
        if (reader.hasNext()) conformers = (ConformerContainer) reader.next();
    }

    @Test
    public void testMatcherQuery1() throws Exception {
        Assert.assertNotNull(conformers);

        // make a query
        PharmacophoreQuery query = new PharmacophoreQuery();

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

        boolean firstTime = true;
        int i = 0;
        boolean[] statuses = new boolean[100];
        for (IAtomContainer conf : conformers) {
            if (firstTime) {
                statuses[i] = matcher.matches(conf, true);
                firstTime = false;
            } else
                statuses[i] = matcher.matches(conf, false);
            i++;
        }

        int[] hits = new int[18];
        int idx = 0;
        for (i = 0; i < statuses.length; i++) {
            if (statuses[i]) hits[idx++] = i;
        }

        int[] expected = {0, 1, 2, 5, 6, 7, 8, 9, 10, 20, 23, 48, 62, 64, 66, 70, 76, 87};
        for (i = 0; i < expected.length; i++) {
            Assert.assertEquals("Hit " + i + " didn't match", expected[i], hits[i]);
        }
    }

    @Test
    public void testMatchedAtoms() throws Exception {
        Assert.assertNotNull(conformers);

        // make a query
        PharmacophoreQuery query = new PharmacophoreQuery();

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

    @Test
    public void testMatchedBonds() throws Exception {
        Assert.assertNotNull(conformers);

        // make a query
        PharmacophoreQuery query = new PharmacophoreQuery();

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

        List<List<IBond>> bMatches = matcher.getMatchingPharmacophoreBonds();
        Assert.assertEquals(2, bMatches.size()); // 2 since we haven't gotten a unique set
        Assert.assertEquals(3, bMatches.get(0).size());

        PharmacophoreBond pbond = (PharmacophoreBond) bMatches.get(0).get(0);
        PharmacophoreAtom patom1 = (PharmacophoreAtom) pbond.getBegin();
        PharmacophoreAtom patom2 = (PharmacophoreAtom) pbond.getEnd();
        Assert.assertEquals("D", patom1.getSymbol());
        Assert.assertEquals("A", patom2.getSymbol());

        List<HashMap<IBond, IBond>> bondMap = matcher.getTargetQueryBondMappings();
        Assert.assertEquals(2, bondMap.size());
        HashMap<IBond, IBond> mapping = bondMap.get(0);
        IBond value = mapping.get(pbond);
        Assert.assertEquals(b1, value);
    }

    @Test(expected = CDKException.class)
    public void testInvalidQuery() throws CDKException {
        PharmacophoreQuery query = new PharmacophoreQuery();
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
        matcher.matches(conformers.get(0));
    }

    @Test
    public void testCNSPcore() throws CDKException, IOException {
        String filename = "data/mdl/cnssmarts.sdf";
        InputStream ins = PharmacophoreMatcherTest.class.getClassLoader().getResourceAsStream(filename);
        IteratingSDFReader reader = new IteratingSDFReader(ins, SilentChemObjectBuilder.getInstance());

        PharmacophoreQuery query = new PharmacophoreQuery();
        PharmacophoreQueryAtom arom = new PharmacophoreQueryAtom("A", "c1ccccc1");
        PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("BasicAmine", "[NX3;h2,h1,H1,H2;!$(NC=O)]");
        PharmacophoreQueryBond b1 = new PharmacophoreQueryBond(arom, n1, 5.0, 7.0);
        query.addAtom(arom);
        query.addAtom(n1);
        query.addBond(b1);

        reader.hasNext();
        IAtomContainer mol = (IAtomContainer) reader.next();
        reader.close();

        PharmacophoreMatcher matcher = new PharmacophoreMatcher(query);
        boolean status = matcher.matches(mol);
        Assert.assertTrue(status);

        List<List<PharmacophoreAtom>> pmatches = matcher.getMatchingPharmacophoreAtoms();
        Assert.assertEquals(1, pmatches.size());

        List<List<PharmacophoreAtom>> upmatches = matcher.getUniqueMatchingPharmacophoreAtoms();
        Assert.assertEquals(1, upmatches.size());
    }

    @Test
    public void testMatchingBonds() throws CDKException, IOException {
        String filename = "data/mdl/cnssmarts.sdf";
        InputStream ins = PharmacophoreMatcherTest.class.getClassLoader().getResourceAsStream(filename);
        IteratingSDFReader reader = new IteratingSDFReader(ins, SilentChemObjectBuilder.getInstance());

        PharmacophoreQuery query = new PharmacophoreQuery();
        PharmacophoreQueryAtom arom = new PharmacophoreQueryAtom("A", "c1ccccc1");
        PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("BasicAmine", "[NX3;h2,h1,H1,H2;!$(NC=O)]");
        PharmacophoreQueryBond b1 = new PharmacophoreQueryBond(arom, n1, 5.0, 7.0);
        query.addAtom(arom);
        query.addAtom(n1);
        query.addBond(b1);

        reader.hasNext();
        IAtomContainer mol = (IAtomContainer) reader.next();
        reader.close();

        PharmacophoreMatcher matcher = new PharmacophoreMatcher(query);
        boolean status = matcher.matches(mol);
        Assert.assertTrue(status);

        List<List<PharmacophoreAtom>> pmatches = matcher.getMatchingPharmacophoreAtoms();
        Assert.assertEquals(1, pmatches.size());

        List<List<PharmacophoreAtom>> upmatches = matcher.getUniqueMatchingPharmacophoreAtoms();
        Assert.assertEquals(1, upmatches.size());

        List<List<IBond>> bmatches = matcher.getMatchingPharmacophoreBonds();
        Assert.assertEquals(1, bmatches.size());
        List<IBond> bmatch = bmatches.get(0);
        Assert.assertEquals(1, bmatch.size());
        PharmacophoreBond pbond = (PharmacophoreBond) bmatch.get(0);
        Assert.assertEquals(5.63, pbond.getBondLength(), 0.01);
    }

    @Test
    public void testAngleMatch1() throws Exception {
        String filename = "data/mdl/cnssmarts.sdf";
        InputStream ins = PharmacophoreMatcherTest.class.getClassLoader().getResourceAsStream(filename);
        IteratingSDFReader reader = new IteratingSDFReader(ins, SilentChemObjectBuilder.getInstance());

        PharmacophoreQuery query = new PharmacophoreQuery();
        PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("BasicAmine", "[NX3;h2,h1,H1,H2;!$(NC=O)]");
        PharmacophoreQueryAtom n2 = new PharmacophoreQueryAtom("BasicAmine", "[NX3;h2,h1,H1,H2;!$(NC=O)]");
        PharmacophoreQueryAtom n3 = new PharmacophoreQueryAtom("BasicAmine", "[NX3;h2,h1,H1,H2;!$(NC=O)]");
        PharmacophoreQueryAngleBond b1 = new PharmacophoreQueryAngleBond(n1, n2, n3, 85, 90);
        query.addAtom(n1);
        query.addAtom(n2);
        query.addAtom(n3);
        query.addBond(b1);

        reader.hasNext();
        IAtomContainer mol = (IAtomContainer) reader.next();
        reader.close();

        PharmacophoreMatcher matcher = new PharmacophoreMatcher(query);
        boolean status = matcher.matches(mol);
        Assert.assertTrue(status);
    }

    @Test
    public void testAngleMatch2() throws Exception {
        String filename = "data/mdl/cnssmarts.sdf";
        InputStream ins = PharmacophoreMatcherTest.class.getClassLoader().getResourceAsStream(filename);
        IteratingSDFReader reader = new IteratingSDFReader(ins, SilentChemObjectBuilder.getInstance());

        PharmacophoreQuery query = new PharmacophoreQuery();
        PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("BasicAmine", "[NX3;h2,h1,H1,H2;!$(NC=O)]");
        PharmacophoreQueryAtom n2 = new PharmacophoreQueryAtom("BasicAmine", "[NX3;h2,h1,H1,H2;!$(NC=O)]");
        PharmacophoreQueryAtom n3 = new PharmacophoreQueryAtom("BasicAmine", "[NX3;h2,h1,H1,H2;!$(NC=O)]");
        PharmacophoreQueryAngleBond b1 = new PharmacophoreQueryAngleBond(n1, n2, n3, 89.14);
        query.addAtom(n1);
        query.addAtom(n2);
        query.addAtom(n3);
        query.addBond(b1);

        reader.hasNext();
        IAtomContainer mol = (IAtomContainer) reader.next();
        reader.close();

        PharmacophoreMatcher matcher = new PharmacophoreMatcher(query);
        boolean status = matcher.matches(mol);
        Assert.assertTrue(status);
    }

    @Test
    public void testAngleMatch3() throws Exception {
        Assert.assertNotNull(conformers);

        // make a query
        PharmacophoreQuery query = new PharmacophoreQuery();

        PharmacophoreQueryAtom o = new PharmacophoreQueryAtom("D", "[OX1]");
        PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("A", "[N]");
        PharmacophoreQueryAtom n2 = new PharmacophoreQueryAtom("A", "[N]");

        query.addAtom(o);
        query.addAtom(n1);
        query.addAtom(n2);
        PharmacophoreQueryAngleBond b1 = new PharmacophoreQueryAngleBond(o, n1, n2, 43, 47);
        query.addBond(b1);

        PharmacophoreMatcher matcher = new PharmacophoreMatcher(query);

        boolean firstTime = true;
        int i = 0;
        boolean[] statuses = new boolean[100];
        for (IAtomContainer conf : conformers) {
            if (firstTime) {
                statuses[i] = matcher.matches(conf, true);
                firstTime = false;
            } else
                statuses[i] = matcher.matches(conf, false);
            i++;
        }
        Assert.assertEquals(100, statuses.length);

        int[] hits = new int[9];
        int idx = 0;
        for (i = 0; i < statuses.length; i++) {
            if (statuses[i]) hits[idx++] = i;
        }

        int[] expected = {0, 6, 32, 33, 48, 54, 60, 62, 69};
        for (i = 0; i < expected.length; i++) {
            Assert.assertEquals("Hit " + i + " didn't match", expected[i], hits[i]);
        }
    }

    @Test
    public void testGetterSetter() {
        PharmacophoreQuery query = new PharmacophoreQuery();
        PharmacophoreQueryAtom arom = new PharmacophoreQueryAtom("A", "c1ccccc1");
        PharmacophoreQueryAtom n1 = new PharmacophoreQueryAtom("BasicAmine", "[NX3;h2,h1,H1,H2;!$(NC=O)]");
        PharmacophoreQueryBond b1 = new PharmacophoreQueryBond(arom, n1, 5.0, 7.0);
        query.addAtom(arom);
        query.addAtom(n1);
        query.addBond(b1);

        PharmacophoreMatcher matcher = new PharmacophoreMatcher();
        matcher.setPharmacophoreQuery(query);
        PharmacophoreQuery retQuery = matcher.getPharmacophoreQuery();
        Assert.assertEquals(2, retQuery.getAtomCount());
        Assert.assertEquals(1, retQuery.getBondCount());
    }

    @Test
    public void multiSmartsQuery() throws IOException, CDKException {

        PharmacophoreQuery query = new PharmacophoreQuery();
        PharmacophoreQueryAtom rings = new PharmacophoreQueryAtom("A", "c1ccccc1|C1CCCC1");
        PharmacophoreQueryAtom o1 = new PharmacophoreQueryAtom("Hd", "[OX1]");
        PharmacophoreQueryBond b1 = new PharmacophoreQueryBond(rings, o1, 3.5, 5.8);
        query.addAtom(rings);
        query.addAtom(o1);
        query.addBond(b1);

        PharmacophoreMatcher matcher = new PharmacophoreMatcher();
        matcher.setPharmacophoreQuery(query);

        String filename = "data/pcore/multismartpcore.sdf";
        InputStream ins = PharmacophoreMatcherTest.class.getClassLoader().getResourceAsStream(filename);
        IteratingSDFReader reader = new IteratingSDFReader(ins, SilentChemObjectBuilder.getInstance());

        IAtomContainer mol = (IAtomContainer) reader.next();
        Assert.assertTrue(matcher.matches(mol));
        Assert.assertEquals(1, matcher.getUniqueMatchingPharmacophoreAtoms().size());
        Assert.assertEquals(2, matcher.getUniqueMatchingPharmacophoreAtoms().get(0).size());

        mol = (IAtomContainer) reader.next();
        Assert.assertTrue(matcher.matches(mol));
        Assert.assertEquals(2, matcher.getUniqueMatchingPharmacophoreAtoms().size());
        Assert.assertEquals(2, matcher.getUniqueMatchingPharmacophoreAtoms().get(0).size());
        Assert.assertEquals(2, matcher.getUniqueMatchingPharmacophoreAtoms().get(1).size());

        mol = (IAtomContainer) reader.next();
        reader.close();
        Assert.assertFalse(matcher.matches(mol));
    }
}
