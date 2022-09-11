/* Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
 */
package org.openscience.cdk.smsd.filters;

import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.Isomorphism;
import org.openscience.cdk.smsd.interfaces.Algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;

/**
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 *
 * @cdk.module test-smsd
 * @cdk.require java1.6+
 */
class ChemicalFiltersTest {

    public ChemicalFiltersTest() {}

    @BeforeAll
    static void setUpClass() throws Exception {}

    @AfterAll
    static void tearDownClass() throws Exception {}

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    /**
     * Test of sortResultsByStereoAndBondMatch method, of class ChemicalFilters.
     * @throws Exception
     */
    @Test
    void testSortResultsByStereoAndBondMatch() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        Isomorphism smsd = new Isomorphism(Algorithm.DEFAULT, false);
        smsd.init(queryac, target, true, true);
        smsd.setChemFilters(false, false, false);
        Assertions.assertEquals(4, smsd.getAllAtomMapping().size());

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, false, false);
        Assertions.assertEquals(1, smsd1.getAllAtomMapping().size());
    }

    /**
     * Test of sortResultsByFragments method, of class ChemicalFilters.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    void testSortResultsByFragments() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd = new Isomorphism(Algorithm.CDKMCS, false);
        smsd.init(queryac, target, true, true);
        smsd.setChemFilters(false, false, false);
        Assertions.assertEquals(4, smsd.getAllAtomMapping().size());

        Isomorphism smsd1 = new Isomorphism(Algorithm.CDKMCS, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(false, true, false);
        Assertions.assertEquals(2, smsd1.getAllAtomMapping().size());
    }

    /**
     * Test of sortResultsByEnergies method, of class ChemicalFilters.
     * @throws Exception
     */
    @Test
    void testSortResultsByEnergies() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd = new Isomorphism(Algorithm.DEFAULT, true);
        smsd.init(queryac, target, true, true);
        smsd.setChemFilters(false, false, false);
        Assertions.assertEquals(4, smsd.getAllAtomMapping().size());

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(false, false, true);
        Assertions.assertEquals(2, smsd1.getAllAtomMapping().size());
    }

    /**
     * Test of sortMapByValueInAccendingOrder method, of class ChemicalFilters.
     */
    @Test
    void testSortMapByValueInAccendingOrder() {

        Map<Integer, Double> map = new TreeMap<>();
        map.put(1, 3.0);
        map.put(2, 2.0);
        map.put(3, 1.0);
        map.put(4, 4.0);
        Map<Integer, Double> expResult = new TreeMap<>();
        expResult.put(3, 1.0);
        expResult.put(2, 2.0);
        expResult.put(1, 3.0);
        expResult.put(4, 4.0);

        Map<Integer, Double> result = ChemicalFilters.sortMapByValueInAccendingOrder(map);
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of sortMapByValueInDecendingOrder method, of class ChemicalFilters.
     */
    @Test
    void testSortMapByValueInDecendingOrder() {
        Map<Integer, Double> map = new TreeMap<>();
        map.put(1, 3.0);
        map.put(2, 2.0);
        map.put(3, 1.0);
        map.put(4, 4.0);
        Map<Integer, Double> expResult = new TreeMap<>();
        expResult.put(4, 4.0);
        expResult.put(1, 3.0);
        expResult.put(2, 2.0);
        expResult.put(3, 1.0);

        Map<Integer, Double> result = ChemicalFilters.sortMapByValueInDecendingOrder(map);
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getSortedEnergy method, of class ChemicalFilters.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    void testGetSortedEnergy() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(false, false, true);
        Double score = 610.0;
        Assertions.assertEquals(score, smsd1.getEnergyScore(0));
    }

    /**
     * Test of getSortedFragment method, of class ChemicalFilters.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    void testGetSortedFragment() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(false, true, false);
        Integer score = 2;
        Assertions.assertEquals(score, smsd1.getFragmentSize(0));
    }

    /**
     * Test of getStereoMatches method, of class ChemicalFilters.
     * @throws InvalidSmilesException
     * @throws CDKException
     */
    @Test
    void testGetStereoMatches() throws InvalidSmilesException, CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer queryac = sp.parseSmiles("CCCOCC(C)=C");

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, false);
        smsd1.init(queryac, target, true, true);
        smsd1.setChemFilters(true, false, false);
        Integer score = 1048;
        Assertions.assertEquals(score, smsd1.getStereoScore(0));
    }
}
