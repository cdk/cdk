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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.smarts.SmartsResult;
import org.openscience.cdk.test.CDKTestCase;
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
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.smarts.Smarts;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;
import java.util.BitSet;
import java.util.List;

/**
 * @cdk.require java1.4+
 */
class UniversalIsomorphismTesterTest extends CDKTestCase {

    private final boolean                            standAlone = false;
    private UniversalIsomorphismTester uiTester;

    @BeforeEach
    void setUpUITester() {
        uiTester = new UniversalIsomorphismTester();
    }

    @Test
    void testIsSubgraph_IAtomContainer_IAtomContainer() throws java.lang.Exception {
        IAtomContainer mol = TestMoleculeFactory.makeAlphaPinene();
        IAtomContainer frag1 = TestMoleculeFactory.makeCyclohexene(); //one double bond in ring
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(frag1);
        Aromaticity.cdkLegacy().apply(mol);
        Aromaticity.cdkLegacy().apply(frag1);

        if (standAlone) {
            System.out.println("Cyclohexene is a subgraph of alpha-Pinen: " + uiTester.isSubgraph(mol, frag1));
        } else {
            Assertions.assertTrue(uiTester.isSubgraph(mol, frag1));
        }

    }

    /**
     * @cdk.bug 1708336
     */
    @Test
    void testSFBug1708336() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        atomContainer.addAtom(builder.newInstance(IAtom.class, "C"));
        atomContainer.addAtom(builder.newInstance(IAtom.class, "C"));
        atomContainer.addAtom(builder.newInstance(IAtom.class, "N"));
        atomContainer.addBond(0, 1, IBond.Order.SINGLE);
        atomContainer.addBond(1, 2, IBond.Order.SINGLE);
        IQueryAtomContainer query = new QueryAtomContainer(DefaultChemObjectBuilder.getInstance());
        SmartsResult result = Smarts.parseToResult(query, "C*C");
        if (!result.ok())
            Assertions.fail(result.getMessage());

        List<List<RMap>> list = uiTester.getSubgraphMaps(atomContainer, query);

        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void test2() throws java.lang.Exception {
        IAtomContainer mol = TestMoleculeFactory.makeAlphaPinene();
        IAtomContainer frag1 = TestMoleculeFactory.makeCyclohexane(); // no double bond in ring
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(frag1);
        Aromaticity.cdkLegacy().apply(mol);
        Aromaticity.cdkLegacy().apply(frag1);

        if (standAlone) {
            System.out.println("Cyclohexane is a subgraph of alpha-Pinen: " + uiTester.isSubgraph(mol, frag1));
        } else {
            Assertions.assertTrue(!uiTester.isSubgraph(mol, frag1));
        }
    }

    @Test
    void test3() throws java.lang.Exception {
        IAtomContainer mol = TestMoleculeFactory.makeIndole();
        IAtomContainer frag1 = TestMoleculeFactory.makePyrrole();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(frag1);
        Aromaticity.cdkLegacy().apply(mol);
        Aromaticity.cdkLegacy().apply(frag1);

        if (standAlone) {
            System.out.println("Pyrrole is a subgraph of Indole: " + uiTester.isSubgraph(mol, frag1));
        } else {
            Assertions.assertTrue(uiTester.isSubgraph(mol, frag1));
        }
    }

    @Test
    void testBasicQueryAtomContainer() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C"); // acetic acid anhydride
        IAtomContainer SMILESquery = sp.parseSmiles("CC"); // acetic acid anhydride
        QueryAtomContainer query = QueryAtomContainerCreator.createBasicQueryContainer(SMILESquery);

        Assertions.assertTrue(uiTester.isSubgraph(atomContainer, query));
    }

    @Test
    void testGetSubgraphAtomsMaps_IAtomContainer() throws java.lang.Exception {
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
            Assertions.assertEquals(rmap.getId1(), result1[i]);
            Assertions.assertEquals(rmap.getId2(), result2[i]);
        }
    }

    @Test
    void testGetSubgraphMap_IAtomContainer_IAtomContainer() throws Exception {
        String molfile = "decalin.mol";
        String queryfile = "decalin.mol";
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtomContainer temp = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        QueryAtomContainer query1;
        QueryAtomContainer query2;

        InputStream ins = this.getClass().getResourceAsStream(molfile);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        reader.read(mol);
        ins = this.getClass().getResourceAsStream(queryfile);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        reader.read(temp);
        query1 = QueryAtomContainerCreator.createBasicQueryContainer(temp);

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("C1CCCCC1");
        query2 = QueryAtomContainerCreator.createBasicQueryContainer(atomContainer);

        List<RMap> list = uiTester.getSubgraphMap(mol, query1);
        Assertions.assertEquals(11, list.size());

        list = uiTester.getSubgraphMap(mol, query2);
        Assertions.assertEquals(6, list.size());

    }

    /**
     * @cdk.bug 1110537
     */
    @Test
    void testGetOverlaps_IAtomContainer_IAtomContainer() throws Exception {
        String file1 = "5SD.mol";
        String file2 = "ADN.mol";
        IAtomContainer mol1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtomContainer mol2 = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        InputStream ins1 = this.getClass().getResourceAsStream(file1);
        new MDLV2000Reader(ins1, Mode.STRICT).read(mol1);
        InputStream ins2 = this.getClass().getResourceAsStream(file2);
        new MDLV2000Reader(ins2, Mode.STRICT).read(mol2);

        List<IAtomContainer> list = uiTester.getOverlaps(mol1, mol2);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(11, (list.get(0)).getAtomCount());

        list = uiTester.getOverlaps(mol2, mol1);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(11, (list.get(0)).getAtomCount());
    }

    /**
     * @cdk.bug 2944080
     */
    @Test
    void testBug2944080() throws Exception {
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smilesParser.parseSmiles("CCC(CC)(C(=O)NC(=O)NC(C)=O)Br");
        IAtomContainer mol2 = smilesParser.parseSmiles("CCC(=CC)C(=O)NC(N)=O");

        List<IAtomContainer> list = uiTester.getOverlaps(mol1, mol2);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(9, list.get(0).getAtomCount());

        list = uiTester.getOverlaps(mol2, mol1);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(9, list.get(0).getAtomCount());
    }

    /**
     * @cdk.bug 2944080
     */
    @Test
    void testGetSubgraphAtomsMap_2944080() throws Exception {
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smilesParser.parseSmiles("CCC(CC)(C(=O)NC(=O)NC(C)=O)Br");
        IAtomContainer mol2 = smilesParser.parseSmiles("CCCC(=O)NC(N)=O");

        //Test for atom mapping between the mols
        List<RMap> maplist = uiTester.getSubgraphAtomsMap(mol1, mol2);
        Assertions.assertNotNull(maplist);
        Assertions.assertEquals(9, maplist.size());
    }

    /**
     * @cdk.bug 2944080
     */
    @Test
    void testGetSubgraphMap_2944080() throws Exception {
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smilesParser.parseSmiles("CCC(CC)(C(=O)NC(=O)NC(C)=O)Br");
        IAtomContainer mol2 = smilesParser.parseSmiles("CCCC(=O)NC(N)=O");

        //Test for atom mapping between the mols
        List<RMap> maplist = uiTester.getSubgraphMap(mol1, mol2);
        Assertions.assertNotNull(maplist);
        Assertions.assertEquals(8, maplist.size());
    }

    /**
     * @cdk.bug 2944080
     */
    @Test
    void testSearchNoConditions_2944080() throws Exception {
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smilesParser.parseSmiles("CCC(CC)(C(=O)NC(=O)NC(C)=O)Br");
        IAtomContainer mol2 = smilesParser.parseSmiles("CCCC(=O)NC(N)=O");

        //Test for atom mapping between the mols
        List<List<RMap>> maplist = uiTester.search(mol1, mol2, new BitSet(),
                UniversalIsomorphismTester.getBitSet(mol2), false, false);
        Assertions.assertNotNull(maplist);
        Assertions.assertEquals(1, maplist.size());
    }

    /**
     * @cdk.bug 2944080
     */
    @Test
    void testSearch_2944080() throws Exception {
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smilesParser.parseSmiles("CCC(CC)(C(=O)NC(=O)NC(C)=O)Br");
        IAtomContainer mol2 = smilesParser.parseSmiles("CCC(=CC)C(=O)NC(N)=O");

        //Test for atom mapping between the mols
        List<List<RMap>> list = uiTester.search(mol1, mol2, new BitSet(), new BitSet(), true, true);
        Assertions.assertEquals(3, list.size());
        for (List<RMap> first : list) {
            Assertions.assertNotSame(0, first.size());
        }

        list = uiTester.search(mol1, mol2, new BitSet(), new BitSet(), false, false);
        Assertions.assertEquals(1, list.size());
        for (List<RMap> first : list) {
            Assertions.assertNotSame(0, first.size());
        }
    }

    /**
     * @cdk.bug 2944080
     */
    @Test
    void testGetSubgraphAtomsMaps_2944080() throws Exception {
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smilesParser.parseSmiles("CCC(CC)(C(=O)NC(=O)NC(C)=O)Br");
        IAtomContainer mol2 = smilesParser.parseSmiles("CCCC(=O)NC(N)=O");

        List<List<RMap>> list = uiTester.getSubgraphAtomsMaps(mol1, mol2);
        Assertions.assertNotNull(list);
        Assertions.assertNotSame(0, list.size());
        for (List<RMap> first : list) {
            Assertions.assertNotNull(first);
            Assertions.assertNotSame(0, first.size());
        }
    }

    @Test
    void testGetSubgraphAtomsMap_Butane() throws Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makeAlkane(4);
        IAtomContainer mol2 = TestMoleculeFactory.makeAlkane(4);

        // Test for atom mapping between the mols
        List<RMap> maplist = uiTester.getSubgraphAtomsMap(mol2, mol1);
        Assertions.assertNotNull(maplist);
        Assertions.assertEquals(4, maplist.size());

        maplist = uiTester.getSubgraphAtomsMap(mol1, mol2);
        Assertions.assertNotNull(maplist);
        Assertions.assertEquals(4, maplist.size());
    }

    @Test
    void testGetSubgraphAtomsMaps_Butane() throws Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makeAlkane(4);
        IAtomContainer mol2 = TestMoleculeFactory.makeAlkane(4);

        List<List<RMap>> list = uiTester.getSubgraphAtomsMaps(mol1, mol2);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(2, list.size());
        for (List<RMap> first : list) {
            Assertions.assertNotNull(first);
            Assertions.assertEquals(4, first.size());
        }
    }

    /**
     * @cdk.bug 999330
     */
    @Test
    void testSFBug999330() throws Exception {
        String file1 = "5SD.mol";
        String file2 = "ADN.mol";
        IAtomContainer mol1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtomContainer mol2 = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        InputStream ins1 = this.getClass().getResourceAsStream(file1);
        new MDLV2000Reader(ins1, Mode.STRICT).read(mol1);
        InputStream ins2 = this.getClass().getResourceAsStream(file2);
        new MDLV2000Reader(ins2, Mode.STRICT).read(mol2);
        AtomContainerAtomPermutor permutor = new AtomContainerAtomPermutor(mol2);
        mol2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class, permutor.next());

        List<IAtomContainer> list1 = uiTester.getOverlaps(mol1, mol2);
        List<IAtomContainer> list2 = uiTester.getOverlaps(mol2, mol1);
        Assertions.assertEquals(1, list1.size());
        Assertions.assertEquals(1, list2.size());
        Assertions.assertEquals((list1.get(0)).getAtomCount(), (list2.get(0)).getAtomCount());
    }

    @Test
    void testItself() throws Exception {
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
        if (!standAlone) Assertions.assertTrue(matched);
    }

    @Test
    void testIsIsomorph_IAtomContainer_IAtomContainer() throws Exception {
        IAtomContainer ac1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac1.addAtom(new Atom("C"));
        IAtomContainer ac2 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac2.addAtom(new Atom("C"));
        Assertions.assertTrue(uiTester.isIsomorph(ac1, ac2));
        Assertions.assertTrue(uiTester.isSubgraph(ac1, ac2));
    }

    @Test
    void testAnyAtomAnyBondCase() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("O1C=CC=C1");
        IAtomContainer queryac = sp.parseSmiles("C1CCCC1");
        QueryAtomContainer query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(queryac, false);

        Assertions.assertTrue(uiTester.isSubgraph(target, query), "C1CCCC1 should be a subgraph of O1C=CC=C1");
        Assertions.assertTrue(uiTester.isIsomorph(target, query), "C1CCCC1 should be a isomorph of O1C=CC=C1");
    }

    /**
     * @cdk.bug 1633201
     */
    @Test
    void testFirstArgumentMustNotBeAnQueryAtomContainer() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("O1C=CC=C1");
        IAtomContainer queryac = sp.parseSmiles("C1CCCC1");
        QueryAtomContainer query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(queryac, false);

        try {
            uiTester.isSubgraph(query, target);
            Assertions.fail("The UniversalIsomorphism should check when the first arguments is a QueryAtomContainer");
        } catch (Exception e) {
            // OK, it must Assert.fail!
        }
    }

    @Test
    void testSingleAtomMatching() throws Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer target = sp.parseSmiles("C");
        IAtomContainer query = sp.parseSmiles("C");

        UniversalIsomorphismTester tester = new UniversalIsomorphismTester();
        Assertions.assertTrue(tester.isIsomorph(target, query));
        Assertions.assertTrue(tester.isIsomorph(query, target));
    }

    @Test
    void testSingleAtomMismatching() throws Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer target = sp.parseSmiles("C");
        IAtomContainer query = sp.parseSmiles("N");

        UniversalIsomorphismTester tester = new UniversalIsomorphismTester();
        Assertions.assertFalse(tester.isIsomorph(target, query), "Single carbon and nitrogen should not match");
        Assertions.assertFalse(tester.isIsomorph(query, target), "Single nitrogen and carbon should not match");
    }

    /**
     * @cdk.bug 2888845
     * @throws Exception
     */
    @Test
    void testSingleAtomMatching1() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("[H]");
        IAtomContainer queryac = sp.parseSmiles("[H]");
        QueryAtomContainer query = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(queryac);

        List<List<RMap>> matches = uiTester.getIsomorphMaps(target, query);
        Assertions.assertEquals(1, matches.size());
        Assertions.assertEquals(1, matches.get(0).size());
        RMap mapping = matches.get(0).get(0);
        Assertions.assertEquals(0, mapping.getId1());
        Assertions.assertEquals(0, mapping.getId2());
        List<List<RMap>> atomMappings = UniversalIsomorphismTester.makeAtomsMapsOfBondsMaps(matches, target, query);
        Assertions.assertEquals(matches, atomMappings);
    }

    /**
     * @cdk.bug 2888845
     * @throws Exception
     */
    @Test
    void testSingleAtomMatching2() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("CNC");
        IAtomContainer queryac = sp.parseSmiles("C");
        QueryAtomContainer query = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(queryac);

        List<List<RMap>> matches = uiTester.getIsomorphMaps(target, query);
        Assertions.assertEquals(2, matches.size());
        Assertions.assertEquals(1, matches.get(0).size());
        Assertions.assertEquals(1, matches.get(1).size());
        RMap map1 = matches.get(0).get(0);
        RMap map2 = matches.get(1).get(0);

        Assertions.assertEquals(0, map1.getId1());
        Assertions.assertEquals(0, map1.getId2());

        Assertions.assertEquals(2, map2.getId1());
        Assertions.assertEquals(0, map2.getId2());

        List<List<RMap>> atomMappings = UniversalIsomorphismTester.makeAtomsMapsOfBondsMaps(matches, target, query);
        Assertions.assertEquals(matches, atomMappings);
    }

    /**
     * @cdk.bug 2912627
     */
    @Test
    void testSingleAtomMatching3() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("CNC");
        IAtomContainer queryac = sp.parseSmiles("C");

        List<List<RMap>> matches = uiTester.getIsomorphMaps(target, queryac);
        Assertions.assertEquals(2, matches.size());
        Assertions.assertEquals(1, matches.get(0).size());
        Assertions.assertEquals(1, matches.get(1).size());
        RMap map1 = matches.get(0).get(0);
        RMap map2 = matches.get(1).get(0);

        Assertions.assertEquals(0, map1.getId1());
        Assertions.assertEquals(0, map1.getId2());

        Assertions.assertEquals(2, map2.getId1());
        Assertions.assertEquals(0, map2.getId2());

        List<List<RMap>> atomMappings = UniversalIsomorphismTester.makeAtomsMapsOfBondsMaps(matches, target, queryac);
        Assertions.assertEquals(matches, atomMappings);
    }

    @Test
    void testUITTimeoutFix() throws Exception {
        // Load molecules
        String filename = "UITTimeout.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = reader.read(new ChemFile());
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer[] molecules = new IAtomContainer[2];
        for (int j = 0; j < 2; j++) {
            IAtomContainer aAtomContainer = cList.get(j);
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
        Assertions.assertTrue(duration < 2000);
    }

    /**
     * @cdk.bug 3513335
     * @throws Exception
     */
    @Test
    void testUITSymmetricMatch() throws Exception {
        QueryAtomContainer q = new QueryAtomContainer(DefaultChemObjectBuilder.getInstance());
        SmartsResult result = Smarts.parseToResult(q, "C**C");
        if (!result.ok())
            Assertions.fail(result.getMessage());

        //Creating 'SCCS' target molecule
        IAtomContainer target = DefaultChemObjectBuilder.getInstance().newAtomContainer();
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
        Assertions.assertFalse(res, "C**C should not match SCCS");
    }

}
