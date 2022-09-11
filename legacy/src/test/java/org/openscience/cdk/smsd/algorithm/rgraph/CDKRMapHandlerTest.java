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
package org.openscience.cdk.smsd.algorithm.rgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.helper.FinalMappings;

/**
 * @cdk.module test-smsd
 * @author     Syed Asad Rahman
 * @cdk.require java1.5+
 */
class CDKRMapHandlerTest {

    private final CDKRMapHandler handler = new CDKRMapHandler();

    public CDKRMapHandlerTest() {}

    @BeforeAll
    static void setUpClass() throws Exception {}

    @AfterAll
    static void tearDownClass() throws Exception {}

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    /**
     * Test of getSource method, of class CDKRMapHandler.
     */
    @Test
    void testGetSource() {
        IAtomContainer expResult = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        handler.setSource(expResult);
        IAtomContainer result = handler.getSource();
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of setSource method, of class CDKRMapHandler.
     */
    @Test
    void testSetSource() {
        IAtomContainer expResult = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        handler.setSource(expResult);
        IAtomContainer result = handler.getSource();
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getTarget method, of class CDKRMapHandler.
     */
    @Test
    void testGetTarget() {
        IAtomContainer expResult = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        handler.setTarget(expResult);
        IAtomContainer result = handler.getTarget();
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of setTarget method, of class CDKRMapHandler.
     */
    @Test
    void testSetTarget() {
        IAtomContainer expResult = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        handler.setTarget(expResult);
        IAtomContainer result = handler.getTarget();
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of calculateOverlapsAndReduce method, of class CDKRMapHandler.
     */
    @Test
    void testCalculateOverlapsAndReduce() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer Molecule1 = sp.parseSmiles("O1C=CC=C1");
        IAtomContainer Molecule2 = sp.parseSmiles("C1CCCC1");
        CDKRMapHandler instance = new CDKRMapHandler();
        instance.calculateOverlapsAndReduce(Molecule1, Molecule2, true);
        Assertions.assertNotNull(FinalMappings.getInstance());
        Assertions.assertNotEquals(0, FinalMappings.getInstance().getSize());
    }

    /**
     * Test of calculateOverlapsAndReduceExactMatch method, of class CDKRMapHandler.
     */
    @Test
    void testCalculateOverlapsAndReduceExactMatch() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer Molecule1 = sp.parseSmiles("O1C=CC=C1");
        IAtomContainer Molecule2 = sp.parseSmiles("O1C=CC=C1");
        CDKRMapHandler instance = new CDKRMapHandler();
        instance.calculateOverlapsAndReduceExactMatch(Molecule1, Molecule2, true);
        // TODO review the generated test code and remove the default call to fail.
        Assertions.assertNotNull(FinalMappings.getInstance());
        Assertions.assertNotEquals(0, FinalMappings.getInstance().getSize());
    }

    /**
     * Test of getMappings method, of class CDKRMapHandler.
     */
    @Test
    void testGetMappings() throws CDKException {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer Molecule1 = sp.parseSmiles("O1C=CC=C1");
        IAtomContainer Molecule2 = sp.parseSmiles("O1C=CC=C1");
        CDKRMapHandler instance = new CDKRMapHandler();
        instance.calculateOverlapsAndReduceExactMatch(Molecule1, Molecule2, true);
        List<Map<Integer, Integer>> result = instance.getMappings();
        Assertions.assertEquals(2, result.size());
    }

    /**
     * Test of setMappings method, of class CDKRMapHandler.
     */
    @Test
    void testSetMappings() {
        Map<Integer, Integer> map = new TreeMap<>();
        map.put(0, 0);
        map.put(1, 1);

        List<Map<Integer, Integer>> mappings = new ArrayList<>();
        mappings.add(map);
        CDKRMapHandler instance = new CDKRMapHandler();
        instance.setMappings(mappings);
        Assertions.assertNotNull(instance.getMappings());
    }

    /**
     * Test of isTimeoutFlag method, of class CDKRMapHandler.
     */
    @Test
    void testIsTimeoutFlag() {
        CDKRMapHandler instance = new CDKRMapHandler();
        boolean expResult = true;
        instance.setTimeoutFlag(true);
        boolean result = instance.isTimeoutFlag();
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of setTimeoutFlag method, of class CDKRMapHandler.
     */
    @Test
    void testSetTimeoutFlag() {
        boolean timeoutFlag = false;
        CDKRMapHandler instance = new CDKRMapHandler();
        instance.setTimeoutFlag(timeoutFlag);
        Assertions.assertNotSame(true, instance.isTimeoutFlag());
    }
}
