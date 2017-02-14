/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received rAtomCount copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.Isomorphism;
import org.openscience.cdk.smsd.interfaces.Algorithm;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Unit testing for the {@link SubStructureSearchAlgorithms} class.
 * @author     Syed Asad Rahman
 * @author     egonw
 * @cdk.module test-smsd
 */
public class SubStructureSearchAlgorithmsTest {

    /**
     * Tests if the CDKMCS can be instantiated without throwing exceptions.
     */
    @Test
    public void testSubStructureSearchAlgorithms() {
        Assert.assertNotNull(new Isomorphism(Algorithm.CDKMCS, true));
        Assert.assertNotNull(new Isomorphism(Algorithm.CDKMCS, false));
    }

    /**
     * Test of init method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testInit_3args_1() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, false, false);
        assertNotNull(smsd1.getReactantMolecule());
        assertNotNull(smsd1.getProductMolecule());
    }

    /**
     * Test of init method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testInit_3args_2() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, false, false);
        assertNotNull(smsd1.getReactantMolecule());
        assertNotNull(smsd1.getProductMolecule());
    }

    /**
     * Test of init method, of class SubStructureSearchAlgorithms.
     * @throws Exception
     */
    @Test
    public void testInit_3args_3() throws Exception {
        //        String sourceMolFileName = "";
        //        String targetMolFileName = "";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, false, false);
        assertNotNull(smsd1.getReactantMolecule());
        assertNotNull(smsd1.getProductMolecule());

    }

    /**
     * Test of setChemFilters method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testSetChemFilters() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);
        assertEquals(1, smsd1.getAllAtomMapping().size());
    }

    /**
     * Test of getFragmentSize method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetFragmentSize() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.SubStructure, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(false, true, false);
        Integer score = 2;
        assertEquals(score, smsd1.getFragmentSize(0));
    }

    /**
     * Test of getStereoScore method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetStereoScore() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, false, false);
        Integer score = 1048;
        assertEquals(score, smsd1.getStereoScore(0));
    }

    /**
     * Test of getEnergyScore method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetEnergyScore() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.SubStructure, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(false, false, true);
        Double score = 610.0;
        assertEquals(score, smsd1.getEnergyScore(0));
    }

    /**
     * Test of getFirstMapping method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetFirstMapping() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);

        assertEquals(7, smsd1.getFirstMapping().size());
    }

    /**
     * Test of getAllMapping method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetAllMapping() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        sp.kekulise(false);
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(queryac);
        Aromaticity.cdkLegacy().apply(target);
        Aromaticity.cdkLegacy().apply(queryac);

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);

        assertEquals(2, smsd1.getAllMapping().size());
    }

    /**
     * Test of getFirstAtomMapping method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetFirstAtomMapping() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);

        assertEquals(7, smsd1.getFirstAtomMapping().size());
    }

    /**
     * Test of getAllAtomMapping method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetAllAtomMapping() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        sp.kekulise(false);
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(queryac);
        Aromaticity.cdkLegacy().apply(target);
        Aromaticity.cdkLegacy().apply(queryac);

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);

        assertEquals(2, smsd1.getAllAtomMapping().size());
    }

    /**
     * Test of getReactantMolecule method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetReactantMolecule() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);

        assertEquals(7, smsd1.getReactantMolecule().getAtomCount());
    }

    /**
     * Test of getProductMolecule method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testGetProductMolecule() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);

        assertEquals(20, smsd1.getProductMolecule().getAtomCount());
    }

    /**
     * Test of getTanimotoSimilarity method, of class SubStructureSearchAlgorithms.
     * @throws Exception
     */
    @Test
    public void testGetTanimotoSimilarity() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);

        double score = 0.35;
        assertEquals(score, smsd1.getTanimotoSimilarity(), 0);
    }

    /**
     * Test of isStereoMisMatch method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    public void testIsStereoMisMatch() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);
        assertEquals(false, smsd1.isStereoMisMatch());
    }

    /**
     * Test of isSubgraph method, of class SubStructureSearchAlgorithms.
     * @throws InvalidSmilesException
     */
    @Test
    public void testIsSubgraph() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.SubStructure, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);
        assertEquals(true, smsd1.isSubgraph());
    }

    /**
     * Test of getEuclideanDistance method, of class SubStructureSearchAlgorithms.
     * @throws Exception
     */
    @Test
    public void testGetEuclideanDistance() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.SubStructure, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, true, true);

        double score = 3.605;
        assertEquals(score, smsd1.getEuclideanDistance(), 0.005);

        Isomorphism smsd2 = new Isomorphism(Algorithm.VFLibMCS, true);
        smsd2.init(queryac, target, true, true);
        smsd2.setChemFilters(true, true, true);

        assertEquals(score, smsd2.getEuclideanDistance(), 0.005);
    }
}
