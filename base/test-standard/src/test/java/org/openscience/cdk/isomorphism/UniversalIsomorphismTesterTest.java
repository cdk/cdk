/* Copyright (C) 1997-2007  The Chemistry Development Kit (CKD) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.isomorphism;

import java.io.InputStream;
import java.util.BitSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.isomorphism.matchers.SymbolQueryAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AliphaticSymbolAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * @cdk.module test-standard
 * @cdk.require java1.4+
 */
public class UniversalIsomorphismTesterTest extends CDKTestCase {

    boolean                            standAlone = false;
    private UniversalIsomorphismTester uiTester;

    @Before
    public void setUpUITester() {
        uiTester = new UniversalIsomorphismTester();
    }

    @Test
    public void testIsSubgraph_IAtomContainer_IAtomContainer() throws java.lang.Exception {
        IAtomContainer mol = TestMoleculeFactory.makeAlphaPinene();
        IAtomContainer frag1 = TestMoleculeFactory.makeCyclohexene(); //one double bond in ring
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(frag1);
        Aromaticity.cdkLegacy().apply(mol);
        Aromaticity.cdkLegacy().apply(frag1);

        if (standAlone) {
            System.out.println("Cyclohexene is a subgraph of alpha-Pinen: " + uiTester.isSubgraph(mol, frag1));
        } else {
            Assert.assertTrue(uiTester.isSubgraph(mol, frag1));
        }

    }

    /**
     * @cdk.bug 1708336
     */
    @Test
    public void testSFBug1708336() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        atomContainer.addAtom(builder.newInstance(IAtom.class, "C"));
        atomContainer.addAtom(builder.newInstance(IAtom.class, "C"));
        atomContainer.addAtom(builder.newInstance(IAtom.class, "N"));
        atomContainer.addBond(0, 1, IBond.Order.SINGLE);
        atomContainer.addBond(1, 2, IBond.Order.SINGLE);
        IQueryAtomContainer query = new QueryAtomContainer(DefaultChemObjectBuilder.getInstance());
        IQueryAtom a1 = new SymbolQueryAtom(DefaultChemObjectBuilder.getInstance());
        a1.setSymbol("C");

        AnyAtom a2 = new AnyAtom(DefaultChemObjectBuilder.getInstance());

        IBond b1 = new OrderQueryBond(a1, a2, IBond.Order.SINGLE, DefaultChemObjectBuilder.getInstance());

        IQueryAtom a3 = new SymbolQueryAtom(DefaultChemObjectBuilder.getInstance());
        a3.setSymbol("C");

        IBond b2 = new OrderQueryBond(a2, a3, IBond.Order.SINGLE, DefaultChemObjectBuilder.getInstance());
        query.addAtom(a1);
        query.addAtom(a2);
        query.addAtom(a3);

        query.addBond(b1);
        query.addBond(b2);

        List<List<RMap>> list = uiTester.getSubgraphMaps(atomContainer, query);

        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void test2() throws java.lang.Exception {
        IAtomContainer mol = TestMoleculeFactory.makeAlphaPinene();
        IAtomContainer frag1 = TestMoleculeFactory.makeCyclohexane(); // no double bond in ring
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(frag1);
        Aromaticity.cdkLegacy().apply(mol);
        Aromaticity.cdkLegacy().apply(frag1);

        if (standAlone) {
            System.out.println("Cyclohexane is a subgraph of alpha-Pinen: " + uiTester.isSubgraph(mol, frag1));
        } else {
            Assert.assertTrue(!uiTester.isSubgraph(mol, frag1));
        }
    }

    @Test
    public void test3() throws java.lang.Exception {
        IAtomContainer mol = TestMoleculeFactory.makeIndole();
        IAtomContainer frag1 = TestMoleculeFactory.makePyrrole();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(frag1);
        Aromaticity.cdkLegacy().apply(mol);
        Aromaticity.cdkLegacy().apply(frag1);

        if (standAlone) {
            System.out.println("Pyrrole is a subgraph of Indole: " + uiTester.isSubgraph(mol, frag1));
        } else {
            Assert.assertTrue(uiTester.isSubgraph(mol, frag1));
        }
    }

    @Test
    public void testBasicQueryAtomContainer() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C"); // acetic acid anhydride
        IAtomContainer SMILESquery = sp.parseSmiles("CC"); // acetic acid anhydride
        QueryAtomContainer query = QueryAtomContainerCreator.createBasicQueryContainer(SMILESquery);

        Assert.assertTrue(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    public void testGetSubgraphAtomsMaps_IAtomContainer() throws java.lang.Exception {
        int[] result1 = {6, 5, 7, 8, 0};
        int[] result2 = {3, 4, 2, 1, 0};

        IAtomContainer mol = TestMoleculeFactory.makeIndole();
        IAtomContainer frag1 = TestMoleculeFactory.makePyrrole();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(frag1);
        Aromaticity.cdkLegacy().apply(mol);
        Aromaticity.cdkLegacy().apply(frag1);

        List<List<RMap>> list = uiTester.getSubgraphAtomsMaps(mol, frag1);
        List<RMap> first = list.get(0);
        for (int i = 0; i < first.size(); i++) {
            RMap rmap = first.get(i);
            Assert.assertEquals(rmap.getId1(), result1[i]);
            Assert.assertEquals(rmap.getId2(), result2[i]);
        }
    }

    @Test
    public void testGetSubgraphMap_IAtomContainer_IAtomContainer() throws Exception {
        String molfile = "data/mdl/decalin.mol";
        String queryfile = "data/mdl/decalin.mol";
        IAtomContainer mol = new AtomContainer();
        IAtomContainer temp = new AtomContainer();
        QueryAtomContainer query1 = null;
        QueryAtomContainer query2 = null;

        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(molfile);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        reader.read(mol);
        ins = this.getClass().getClassLoader().getResourceAsStream(queryfile);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        reader.read(temp);
        query1 = QueryAtomContainerCreator.createBasicQueryContainer(temp);

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("C1CCCCC1");
        query2 = QueryAtomContainerCreator.createBasicQueryContainer(atomContainer);

        List<RMap> list = uiTester.getSubgraphMap(mol, query1);
        Assert.assertEquals(11, list.size());

        list = uiTester.getSubgraphMap(mol, query2);
        Assert.assertEquals(6, list.size());

    }

    /**
     * @cdk.bug 1110537
     */
    @Test
    public void testGetOverlaps_IAtomContainer_IAtomContainer() throws Exception {
        String file1 = "data/mdl/5SD.mol";
        String file2 = "data/mdl/ADN.mol";
        IAtomContainer mol1 = new AtomContainer();
        IAtomContainer mol2 = new AtomContainer();

        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(file1);
        new MDLV2000Reader(ins1, Mode.STRICT).read(mol1);
        InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(file2);
        new MDLV2000Reader(ins2, Mode.STRICT).read(mol2);

        List<IAtomContainer> list = uiTester.getOverlaps(mol1, mol2);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(11, ((AtomContainer) list.get(0)).getAtomCount());

        list = uiTester.getOverlaps(mol2, mol1);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(11, ((AtomContainer) list.get(0)).getAtomCount());
    }

    /**
     * @cdk.bug 2944080
     */
    @Test
    public void testBug2944080() throws Exception {
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smilesParser.parseSmiles("CCC(CC)(C(=O)NC(=O)NC(C)=O)Br");
        IAtomContainer mol2 = smilesParser.parseSmiles("CCC(=CC)C(=O)NC(N)=O");

        List<IAtomContainer> list = uiTester.getOverlaps(mol1, mol2);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(9, list.get(0).getAtomCount());

        list = uiTester.getOverlaps(mol2, mol1);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(9, list.get(0).getAtomCount());
    }

    /**
     * @cdk.bug 2944080
     */
    @Test
    public void testGetSubgraphAtomsMap_2944080() throws Exception {
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smilesParser.parseSmiles("CCC(CC)(C(=O)NC(=O)NC(C)=O)Br");
        IAtomContainer mol2 = smilesParser.parseSmiles("CCCC(=O)NC(N)=O");

        //Test for atom mapping between the mols
        List<RMap> maplist = uiTester.getSubgraphAtomsMap(mol1, mol2);
        Assert.assertNotNull(maplist);
        Assert.assertEquals(9, maplist.size());
    }

    /**
     * @cdk.bug 2944080
     */
    @Test
    public void testGetSubgraphMap_2944080() throws Exception {
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smilesParser.parseSmiles("CCC(CC)(C(=O)NC(=O)NC(C)=O)Br");
        IAtomContainer mol2 = smilesParser.parseSmiles("CCCC(=O)NC(N)=O");

        //Test for atom mapping between the mols
        List<RMap> maplist = uiTester.getSubgraphMap(mol1, mol2);
        Assert.assertNotNull(maplist);
        Assert.assertEquals(8, maplist.size());
    }

    /**
     * @cdk.bug 2944080
     */
    @Test
    public void testSearchNoConditions_2944080() throws Exception {
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smilesParser.parseSmiles("CCC(CC)(C(=O)NC(=O)NC(C)=O)Br");
        IAtomContainer mol2 = smilesParser.parseSmiles("CCCC(=O)NC(N)=O");

        //Test for atom mapping between the mols
        List<List<RMap>> maplist = uiTester.search(mol1, mol2, new BitSet(),
                UniversalIsomorphismTester.getBitSet(mol2), false, false);
        Assert.assertNotNull(maplist);
        Assert.assertEquals(1, maplist.size());
    }

    /**
     * @cdk.bug 2944080
     */
    @Test
    public void testSearch_2944080() throws Exception {
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smilesParser.parseSmiles("CCC(CC)(C(=O)NC(=O)NC(C)=O)Br");
        IAtomContainer mol2 = smilesParser.parseSmiles("CCC(=CC)C(=O)NC(N)=O");

        //Test for atom mapping between the mols
        List<List<RMap>> list = uiTester.search(mol1, mol2, new BitSet(), new BitSet(), true, true);
        Assert.assertEquals(3, list.size());
        for (int i = 0; i < list.size(); i++) {
            List<RMap> first = list.get(i);
            Assert.assertNotSame(0, first.size());
        }

        list = uiTester.search(mol1, mol2, new BitSet(), new BitSet(), false, false);
        Assert.assertEquals(1, list.size());
        for (int i = 0; i < list.size(); i++) {
            List<RMap> first = list.get(i);
            Assert.assertNotSame(0, first.size());
        }
    }

    /**
     * @cdk.bug 2944080
     */
    @Test
    public void testGetSubgraphAtomsMaps_2944080() throws Exception {
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smilesParser.parseSmiles("CCC(CC)(C(=O)NC(=O)NC(C)=O)Br");
        IAtomContainer mol2 = smilesParser.parseSmiles("CCCC(=O)NC(N)=O");

        List<List<RMap>> list = uiTester.getSubgraphAtomsMaps(mol1, mol2);
        Assert.assertNotNull(list);
        Assert.assertNotSame(0, list.size());
        for (int i = 0; i < list.size(); i++) {
            List<RMap> first = list.get(i);
            Assert.assertNotNull(first);
            Assert.assertNotSame(0, first.size());
        }
    }

    @Test
    public void testGetSubgraphAtomsMap_Butane() throws Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makeAlkane(4);
        IAtomContainer mol2 = TestMoleculeFactory.makeAlkane(4);

        // Test for atom mapping between the mols
        List<RMap> maplist = uiTester.getSubgraphAtomsMap(mol2, mol1);
        Assert.assertNotNull(maplist);
        Assert.assertEquals(4, maplist.size());

        maplist = uiTester.getSubgraphAtomsMap(mol1, mol2);
        Assert.assertNotNull(maplist);
        Assert.assertEquals(4, maplist.size());
    }

    @Test
    public void testGetSubgraphAtomsMaps_Butane() throws Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makeAlkane(4);
        IAtomContainer mol2 = TestMoleculeFactory.makeAlkane(4);

        List<List<RMap>> list = uiTester.getSubgraphAtomsMaps(mol1, mol2);
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        for (int i = 0; i < list.size(); i++) {
            List<RMap> first = list.get(i);
            Assert.assertNotNull(first);
            Assert.assertEquals(4, first.size());
        }
    }

    /**
     * @cdk.bug 999330
     */
    @Test
    public void testSFBug999330() throws Exception {
        String file1 = "data/mdl/5SD.mol";
        String file2 = "data/mdl/ADN.mol";
        IAtomContainer mol1 = new AtomContainer();
        IAtomContainer mol2 = new AtomContainer();

        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(file1);
        new MDLV2000Reader(ins1, Mode.STRICT).read(mol1);
        InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(file2);
        new MDLV2000Reader(ins2, Mode.STRICT).read(mol2);
        AtomContainerAtomPermutor permutor = new AtomContainerAtomPermutor(mol2);
        mol2 = new AtomContainer((AtomContainer) permutor.next());

        List<IAtomContainer> list1 = uiTester.getOverlaps(mol1, mol2);
        List<IAtomContainer> list2 = uiTester.getOverlaps(mol2, mol1);
        Assert.assertEquals(1, list1.size());
        Assert.assertEquals(1, list2.size());
        Assert.assertEquals(((AtomContainer) list1.get(0)).getAtomCount(),
                ((AtomContainer) list2.get(0)).getAtomCount());
    }

    @Test
    public void testItself() throws Exception {
        String smiles = "C1CCCCCCC1CC";
        QueryAtomContainer query = QueryAtomContainerCreator.createAnyAtomContainer(new SmilesParser(
                DefaultChemObjectBuilder.getInstance()).parseSmiles(smiles), true);
        IAtomContainer ac = new SmilesParser(DefaultChemObjectBuilder.getInstance()).parseSmiles(smiles);
        if (standAlone) {
            System.out.println("AtomCount of query: " + query.getAtomCount());
            System.out.println("AtomCount of target: " + ac.getAtomCount());

        }

        boolean matched = uiTester.isSubgraph(ac, query);
        if (standAlone) System.out.println("QueryAtomContainer matched: " + matched);
        if (!standAlone) Assert.assertTrue(matched);
    }

    @Test
    public void testIsIsomorph_IAtomContainer_IAtomContainer() throws Exception {
        AtomContainer ac1 = new AtomContainer();
        ac1.addAtom(new Atom("C"));
        AtomContainer ac2 = new AtomContainer();
        ac2.addAtom(new Atom("C"));
        Assert.assertTrue(uiTester.isIsomorph(ac1, ac2));
        Assert.assertTrue(uiTester.isSubgraph(ac1, ac2));
    }

    @Test
    public void testAnyAtomAnyBondCase() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("O1C=CC=C1");
        IAtomContainer queryac = sp.parseSmiles("C1CCCC1");
        QueryAtomContainer query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(queryac, false);

        Assert.assertTrue("C1CCCC1 should be a subgraph of O1C=CC=C1", uiTester.isSubgraph(target, query));
        Assert.assertTrue("C1CCCC1 should be a isomorph of O1C=CC=C1", uiTester.isIsomorph(target, query));
    }

    /**
     * @cdk.bug 1633201
     */
    @Test
    public void testFirstArgumentMustNotBeAnQueryAtomContainer() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("O1C=CC=C1");
        IAtomContainer queryac = sp.parseSmiles("C1CCCC1");
        QueryAtomContainer query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(queryac, false);

        try {
            uiTester.isSubgraph(query, target);
            Assert.fail("The UniversalIsomorphism should check when the first arguments is a QueryAtomContainer");
        } catch (Exception e) {
            // OK, it must Assert.fail!
        }
    }

    @Test
    public void testSingleAtomMatching() throws Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer target = sp.parseSmiles("C");
        IAtomContainer query = sp.parseSmiles("C");

        UniversalIsomorphismTester tester = new UniversalIsomorphismTester();
        Assert.assertTrue(tester.isIsomorph(target, query));
        Assert.assertTrue(tester.isIsomorph(query, target));
    }

    @Test
    public void testSingleAtomMismatching() throws Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer target = sp.parseSmiles("C");
        IAtomContainer query = sp.parseSmiles("N");

        UniversalIsomorphismTester tester = new UniversalIsomorphismTester();
        Assert.assertFalse("Single carbon and nitrogen should not match", tester.isIsomorph(target, query));
        Assert.assertFalse("Single nitrogen and carbon should not match", tester.isIsomorph(query, target));
    }

    /**
     * @cdk.bug 2888845
     * @throws Exception
     */
    @Test
    public void testSingleAtomMatching1() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("[H]");
        IAtomContainer queryac = sp.parseSmiles("[H]");
        QueryAtomContainer query = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(queryac);

        List<List<RMap>> matches = uiTester.getIsomorphMaps(target, query);
        Assert.assertEquals(1, matches.size());
        Assert.assertEquals(1, matches.get(0).size());
        RMap mapping = matches.get(0).get(0);
        Assert.assertEquals(0, mapping.getId1());
        Assert.assertEquals(0, mapping.getId2());
        List<List<RMap>> atomMappings = UniversalIsomorphismTester.makeAtomsMapsOfBondsMaps(matches, target, query);
        Assert.assertEquals(matches, atomMappings);
    }

    /**
     * @cdk.bug 2888845
     * @throws Exception
     */
    @Test
    public void testSingleAtomMatching2() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("CNC");
        IAtomContainer queryac = sp.parseSmiles("C");
        QueryAtomContainer query = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(queryac);

        List<List<RMap>> matches = uiTester.getIsomorphMaps(target, query);
        Assert.assertEquals(2, matches.size());
        Assert.assertEquals(1, matches.get(0).size());
        Assert.assertEquals(1, matches.get(1).size());
        RMap map1 = matches.get(0).get(0);
        RMap map2 = matches.get(1).get(0);

        Assert.assertEquals(0, map1.getId1());
        Assert.assertEquals(0, map1.getId2());

        Assert.assertEquals(2, map2.getId1());
        Assert.assertEquals(0, map2.getId2());

        List<List<RMap>> atomMappings = UniversalIsomorphismTester.makeAtomsMapsOfBondsMaps(matches, target, query);
        Assert.assertEquals(matches, atomMappings);
    }

    /**
     * @cdk.bug 2912627
     */
    @Test
    public void testSingleAtomMatching3() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("CNC");
        IAtomContainer queryac = sp.parseSmiles("C");

        List<List<RMap>> matches = uiTester.getIsomorphMaps(target, queryac);
        Assert.assertEquals(2, matches.size());
        Assert.assertEquals(1, matches.get(0).size());
        Assert.assertEquals(1, matches.get(1).size());
        RMap map1 = matches.get(0).get(0);
        RMap map2 = matches.get(1).get(0);

        Assert.assertEquals(0, map1.getId1());
        Assert.assertEquals(0, map1.getId2());

        Assert.assertEquals(2, map2.getId1());
        Assert.assertEquals(0, map2.getId2());

        List<List<RMap>> atomMappings = UniversalIsomorphismTester.makeAtomsMapsOfBondsMaps(matches, target, queryac);
        Assert.assertEquals(matches, atomMappings);
    }

    @Test
    public void testUITTimeoutFix() throws Exception {
        // Load molecules
        String filename = "data/mdl/UITTimeout.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer[] molecules = new IAtomContainer[2];
        for (int j = 0; j < 2; j++) {
            IAtomContainer aAtomContainer = (IAtomContainer) cList.get(j);
            CDKAtomTypeMatcher tmpMatcher = CDKAtomTypeMatcher.getInstance(aAtomContainer.getBuilder());
            CDKHydrogenAdder tmpAdder = CDKHydrogenAdder.getInstance(aAtomContainer.getBuilder());
            for (int i = 0; i < aAtomContainer.getAtomCount(); i++) {
                IAtom tmpAtom = aAtomContainer.getAtom(i);
                IAtomType tmpType = tmpMatcher.findMatchingAtomType(aAtomContainer, tmpAtom);
                AtomTypeManipulator.configure(tmpAtom, tmpType);
                tmpAdder.addImplicitHydrogens(aAtomContainer, tmpAtom);
            }
            AtomContainerManipulator.convertImplicitToExplicitHydrogens(aAtomContainer);
            molecules[j] = aAtomContainer;
        }
        QueryAtomContainer query = QueryAtomContainerCreator.createAnyAtomForPseudoAtomQueryContainer(molecules[1]);
        // test
        long starttime = System.currentTimeMillis();
        uiTester.setTimeout(200);
        uiTester.getSubgraphAtomsMaps(molecules[0], query);
        long duration = System.currentTimeMillis() - starttime;
        // The search must last much longer then two seconds if the timeout not works
        Assert.assertTrue(duration < 2000);
    }

    /**
     * @cdk.bug 3513335
     * @throws Exception
     */
    @Test
    public void testUITSymmetricMatch() throws Exception {
        QueryAtomContainer q = new QueryAtomContainer(DefaultChemObjectBuilder.getInstance());
        //setting atoms
        IQueryAtom a0 = new AliphaticSymbolAtom("C", DefaultChemObjectBuilder.getInstance());
        q.addAtom(a0);
        IQueryAtom a1 = new AnyAtom(DefaultChemObjectBuilder.getInstance());
        q.addAtom(a1);
        IQueryAtom a2 = new AnyAtom(DefaultChemObjectBuilder.getInstance());
        q.addAtom(a2);
        IQueryAtom a3 = new AliphaticSymbolAtom("C", DefaultChemObjectBuilder.getInstance());
        q.addAtom(a3);
        //setting bonds
        org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond b0 = new org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond(
                IBond.Order.SINGLE, DefaultChemObjectBuilder.getInstance());
        b0.setAtoms(new IAtom[]{a0, a1});
        q.addBond(b0);
        org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond b1 = new org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond(
                IBond.Order.SINGLE, DefaultChemObjectBuilder.getInstance());
        b1.setAtoms(new IAtom[]{a1, a2});
        q.addBond(b1);
        org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond b2 = new org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond(
                IBond.Order.SINGLE, DefaultChemObjectBuilder.getInstance());
        b2.setAtoms(new IAtom[]{a2, a3});
        q.addBond(b2);

        //Creating 'SCCS' target molecule
        AtomContainer target = new AtomContainer();
        //atoms
        IAtom ta0 = new Atom("S");
        target.addAtom(ta0);
        IAtom ta1 = new Atom("C");
        target.addAtom(ta1);
        IAtom ta2 = new Atom("C");
        target.addAtom(ta2);
        IAtom ta3 = new Atom("S");
        target.addAtom(ta3);
        //bonds
        IBond tb0 = new Bond();
        tb0.setAtoms(new IAtom[]{ta0, ta1});
        tb0.setOrder(IBond.Order.SINGLE);
        target.addBond(tb0);

        IBond tb1 = new Bond();
        tb1.setAtoms(new IAtom[]{ta1, ta2});
        tb1.setOrder(IBond.Order.SINGLE);
        target.addBond(tb1);

        IBond tb2 = new Bond();
        tb2.setAtoms(new IAtom[]{ta2, ta3});
        tb2.setOrder(IBond.Order.SINGLE);
        target.addBond(tb2);

        //Isomorphism check
        boolean res = uiTester.isSubgraph(target, q);
        Assert.assertFalse("C**C should not match SCCS", res);
    }

}
