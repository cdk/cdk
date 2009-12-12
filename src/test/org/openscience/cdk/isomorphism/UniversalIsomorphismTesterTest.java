/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CKD) project
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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.isomorphism.matchers.SymbolQueryAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

/**
 * @cdk.module test-standard
 * @cdk.require java1.4+
 */
public class UniversalIsomorphismTesterTest extends CDKTestCase
{

	boolean standAlone = false;

	@Test public void testIsSubgraph_IAtomContainer_IAtomContainer() throws java.lang.Exception
	{
		AtomContainer mol = MoleculeFactory.makeAlphaPinene();
		AtomContainer frag1 = MoleculeFactory.makeCyclohexene(); //one double bond in ring
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(frag1);
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		CDKHueckelAromaticityDetector.detectAromaticity(frag1);

		if(standAlone) {
			System.out.println("Cyclohexene is a subgraph of alpha-Pinen: " + UniversalIsomorphismTester.isSubgraph(mol, frag1));
        } else {
            Assert.assertTrue(UniversalIsomorphismTester.isSubgraph(mol, frag1));
        }


	}

	/**
     * @cdk.bug 1708336
     */
	@Test public void testSFBug1708336() throws Exception {
		DefaultChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer atomContainer = builder.newAtomContainer();
        atomContainer.addAtom(builder.newAtom("C"));
        atomContainer.addAtom(builder.newAtom("C"));
        atomContainer.addAtom(builder.newAtom("N"));
        atomContainer.addBond(0, 1, IBond.Order.SINGLE);
        atomContainer.addBond(1, 2, IBond.Order.SINGLE);
        IQueryAtomContainer query = new QueryAtomContainer();
        IQueryAtom a1 = new SymbolQueryAtom();
        a1.setSymbol("C");

        AnyAtom a2 = new AnyAtom();

        Bond b1 = new OrderQueryBond(a1, a2, IBond.Order.SINGLE);

        IQueryAtom a3 = new SymbolQueryAtom();
        a3.setSymbol("C");

        Bond b2 = new OrderQueryBond(a2, a3, IBond.Order.SINGLE);
        query.addAtom(a1);
        query.addAtom(a2);
        query.addAtom(a3);

        query.addBond(b1);
        query.addBond(b2);

        List list = UniversalIsomorphismTester.getSubgraphMaps(atomContainer, query);

        Assert.assertTrue(list.isEmpty());
	}

	@Test public void test2() throws java.lang.Exception
	{
		AtomContainer mol = MoleculeFactory.makeAlphaPinene();
		AtomContainer frag1 = MoleculeFactory.makeCyclohexane(); // no double bond in ring
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(frag1);
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		CDKHueckelAromaticityDetector.detectAromaticity(frag1);

		if(standAlone){
			System.out.println("Cyclohexane is a subgraph of alpha-Pinen: " + UniversalIsomorphismTester.isSubgraph(mol, frag1));
		} else {
            Assert.assertTrue(!UniversalIsomorphismTester.isSubgraph(mol, frag1));
        }
	}

	@Test public void test3() throws java.lang.Exception
	{
		AtomContainer mol = MoleculeFactory.makeIndole();
		AtomContainer frag1 = MoleculeFactory.makePyrrole();
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(frag1);
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		CDKHueckelAromaticityDetector.detectAromaticity(frag1);

		if(standAlone) {
			System.out.println("Pyrrole is a subgraph of Indole: " + UniversalIsomorphismTester.isSubgraph(mol, frag1));
        } else {
            Assert.assertTrue(UniversalIsomorphismTester.isSubgraph(mol, frag1));
        }
	}

	@Test public void testBasicQueryAtomContainer() throws Exception {
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C"); // acetic acid anhydride
        IAtomContainer SMILESquery = sp.parseSmiles("CC"); // acetic acid anhydride
        QueryAtomContainer query = QueryAtomContainerCreator.createBasicQueryContainer(SMILESquery);

        Assert.assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }

	@Test public void testGetSubgraphAtomsMaps_IAtomContainer() throws java.lang.Exception
	{
		int[] result1 = {6, 5, 7, 8, 0};
		int[] result2 = {3, 4, 2, 1, 0};

		AtomContainer mol = MoleculeFactory.makeIndole();
		AtomContainer frag1 = MoleculeFactory.makePyrrole();
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(frag1);
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		CDKHueckelAromaticityDetector.detectAromaticity(frag1);

		List list = UniversalIsomorphismTester.getSubgraphAtomsMaps(mol, frag1);
		List first = (List)list.get(0);
		for (int i = 0; i < first.size(); i++) {
			RMap rmap = (RMap)first.get(i);
			Assert.assertEquals(rmap.getId1(), result1[i]);
			Assert.assertEquals(rmap.getId2(), result2[i]);
		}
	}

    @Test public void testGetSubgraphMap_IAtomContainer_IAtomContainer() throws Exception
    {
        String molfile = "data/mdl/decalin.mol";
        String queryfile = "data/mdl/decalin.mol";
        Molecule mol = new Molecule();
        Molecule temp = new Molecule();
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

        List list = UniversalIsomorphismTester.getSubgraphMap(mol, query1);
        Assert.assertEquals(11, list.size());

        list = UniversalIsomorphismTester.getSubgraphMap(mol, query2);
        Assert.assertEquals(6, list.size());

    }

    /**
     * @cdk.bug 1110537
     */
    @Test public void testGetOverlaps_IAtomContainer_IAtomContainer()  throws Exception{
        String file1 = "data/mdl/5SD.mol";
        String file2 = "data/mdl/ADN.mol";
        Molecule mol1 = new Molecule();
        Molecule mol2 = new Molecule();

        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(file1);
        new MDLV2000Reader(ins1, Mode.STRICT).read(mol1);
        InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(file2);
        new MDLV2000Reader(ins2, Mode.STRICT).read(mol2);

        List list = UniversalIsomorphismTester.getOverlaps(mol1, mol2);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(11, ((AtomContainer)list.get(0)).getAtomCount());

        list = UniversalIsomorphismTester.getOverlaps(mol2, mol1);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(11, ((AtomContainer)list.get(0)).getAtomCount());
    }

    /**
     * @cdk.bug 1208740
     */
    @Test public void testSFBug1208740() throws Exception {
        String file1 = "data/mdl/bug1208740_1.mol";
        String file2 = "data/mdl/bug1208740_2.mol";
        Molecule mol1 = new Molecule();
        Molecule mol2 = new Molecule();

        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(file1);
        new MDLV2000Reader(ins1, Mode.STRICT).read(mol1);
        InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(file2);
        new MDLV2000Reader(ins2, Mode.STRICT).read(mol2);

        List list = UniversalIsomorphismTester.getOverlaps(mol1, mol2);
        Assert.assertEquals(5, list.size());
        list = UniversalIsomorphismTester.getOverlaps(mol2, mol1);
        Assert.assertEquals(5, list.size());

        // now apply aromaticity detection, then 8 overlaps should be found
        // see cdk-user@list.sf.net on 2005-06-16
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
//		CDKHueckelAromaticityDetector.detectAromaticity(mol1);
		Iterator<IAtom> atoms = mol1.atoms().iterator();
		int i= 1;
		while (atoms.hasNext()) {
			IAtom nextAtom = atoms.next();
			System.out.println(i + ": " + nextAtom.getSymbol() +
				" T:" + nextAtom.getAtomTypeName() +
				" A:" + nextAtom.getFlag(CDKConstants.ISAROMATIC));
			i++;
		}
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
		Assert.assertTrue(CDKHueckelAromaticityDetector.detectAromaticity(mol2));
        list = UniversalIsomorphismTester.getOverlaps(mol1, mol2);
        Assert.assertEquals(8, list.size());
        list = UniversalIsomorphismTester.getOverlaps(mol2, mol1);
        Assert.assertEquals(8, list.size());
    }

    /**
     * @cdk.bug 999330
     */
    @Test public void testSFBug999330() throws Exception {
        String file1 = "data/mdl/5SD.mol";
        String file2 = "data/mdl/ADN.mol";
        Molecule mol1 = new Molecule();
        Molecule mol2 = new Molecule();

        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(file1);
        new MDLV2000Reader(ins1, Mode.STRICT).read(mol1);
        InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(file2);
        new MDLV2000Reader(ins2, Mode.STRICT).read(mol2);
        AtomContainerAtomPermutor permutor = new AtomContainerAtomPermutor(mol2);
        mol2 = new Molecule((AtomContainer)permutor.next());

        List list1 = UniversalIsomorphismTester.getOverlaps(mol1, mol2);
        List list2 = UniversalIsomorphismTester.getOverlaps(mol2, mol1);
        Assert.assertEquals(1, list1.size());
        Assert.assertEquals(1, list2.size());
        Assert.assertEquals(((AtomContainer)list1.get(0)).getAtomCount(),
                     ((AtomContainer)list2.get(0)).getAtomCount());
    }

    @Test public void testItself() throws Exception{
    	String smiles="C1CCCCCCC1CC";
       	QueryAtomContainer query=QueryAtomContainerCreator.createAnyAtomContainer(new SmilesParser(DefaultChemObjectBuilder.getInstance()).parseSmiles(smiles),true);
    	IAtomContainer ac = new SmilesParser(DefaultChemObjectBuilder.getInstance()).parseSmiles(smiles);
       	if (standAlone)
    	{
    		System.out.println("AtomCount of query: " + query.getAtomCount());
    		System.out.println("AtomCount of target: " + ac.getAtomCount());

    	}

       	boolean matched =UniversalIsomorphismTester.isSubgraph(ac,query);
    	if (standAlone) System.out.println("QueryAtomContainer matched: " + matched);
       	if (!standAlone) Assert.assertTrue(matched);
    }

    @Test public void testIsIsomorph_IAtomContainer_IAtomContainer() throws Exception {
        AtomContainer ac1 = new AtomContainer();
        ac1.addAtom(new Atom("C"));
        AtomContainer ac2 = new AtomContainer();
        ac2.addAtom(new Atom("C"));
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(ac1, ac2));
        Assert.assertTrue(UniversalIsomorphismTester.isSubgraph(ac1, ac2));
    }

    @Test public void testAnyAtomAnyBondCase() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("O1C=CC=C1");
        IAtomContainer queryac = sp.parseSmiles("C1CCCC1");
        QueryAtomContainer query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(queryac, false);

        Assert.assertTrue("C1CCCC1 should be a subgraph of O1C=CC=C1", UniversalIsomorphismTester.isSubgraph(target, query));
        Assert.assertTrue("C1CCCC1 should be a isomorph of O1C=CC=C1", UniversalIsomorphismTester.isIsomorph(target, query));
    }

    /**
     * @cdk.bug 1633201
     */
    @Test public void testFirstArgumentMustNotBeAnQueryAtomContainer() throws Exception {
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("O1C=CC=C1");
        IAtomContainer queryac = sp.parseSmiles("C1CCCC1");
        QueryAtomContainer query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(queryac, false);

        try {
        	UniversalIsomorphismTester.isSubgraph(query, target);
        	Assert.fail("The UniversalIsomorphism should check when the first arguments is a QueryAtomContainer");
        } catch (Exception e) {
			// OK, it must Assert.fail!
		}
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

        List<List<RMap>> matches = UniversalIsomorphismTester.getIsomorphMaps(target, query);
        Assert.assertEquals(1,matches.size());
        Assert.assertEquals(1,matches.get(0).size());
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

        List<List<RMap>> matches = UniversalIsomorphismTester.getIsomorphMaps(target, query);
        Assert.assertEquals(2,matches.size());
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

        List<List<RMap>> matches = UniversalIsomorphismTester.getIsomorphMaps(target, queryac);
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

}

