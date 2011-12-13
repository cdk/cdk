
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
package org.openscience.cdk.smsd.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

/**
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * @cdk.module test-smsd
 */
public class AbstractMCSTest {

    public AbstractMCSTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class AbstractMCS.
     * @throws Exception
     */
    @Test
    public void testInit_3args_1() throws Exception {
        System.out.println("init");
        IAtomContainer source = null;
        IAtomContainer target = null;
        boolean removeHydrogen = false;
        AbstractMCS instance = new AbstractMCSImpl();
        instance.init(source, target, removeHydrogen, true);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of init method, of class AbstractMCS.
     * @throws Exception
     */
    @Test
    public void testInit_3args_2() throws Exception {
        System.out.println("init");
        IAtomContainer source = null;
        IAtomContainer target = null;
        boolean removeHydrogen = false;
        AbstractMCS instance = new AbstractMCSImpl();
        instance.init(source, target, removeHydrogen, true);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setChemFilters method, of class AbstractMCS.
     */
    @Test
    public void testSetChemFilters() {
        System.out.println("setChemFilters");
        boolean stereoFilter = false;
        boolean fragmentFilter = false;
        boolean energyFilter = false;
        AbstractMCS instance = new AbstractMCSImpl();
        instance.setChemFilters(stereoFilter, fragmentFilter, energyFilter);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEnergyScore method, of class AbstractMCS.
     */
    @Test
    public void testGetEnergyScore() {
        System.out.println("getEnergyScore");
        int Key = 0;
        AbstractMCS instance = new AbstractMCSImpl();
        Double expResult = null;
        Double result = instance.getEnergyScore(Key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFragmentSize method, of class AbstractMCS.
     */
    @Test
    public void testGetFragmentSize() {
        System.out.println("getFragmentSize");
        int Key = 0;
        AbstractMCS instance = new AbstractMCSImpl();
        Integer expResult = null;
        Integer result = instance.getFragmentSize(Key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProductMolecule method, of class AbstractMCS.
     */
    @Test
    public void testGetProductMolecule() {
        System.out.println("getProductMolecule");
        AbstractMCS instance = new AbstractMCSImpl();
        IAtomContainer expResult = null;
        IAtomContainer result = instance.getProductMolecule();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getReactantMolecule method, of class AbstractMCS.
     */
    @Test
    public void testGetReactantMolecule() {
        System.out.println("getReactantMolecule");
        AbstractMCS instance = new AbstractMCSImpl();
        IAtomContainer expResult = null;
        IAtomContainer result = instance.getReactantMolecule();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStereoScore method, of class AbstractMCS.
     */
    @Test
    public void testGetStereoScore() {
        System.out.println("getStereoScore");
        int Key = 0;
        AbstractMCS instance = new AbstractMCSImpl();
        Integer expResult = null;
        Integer result = instance.getStereoScore(Key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isStereoMisMatch method, of class AbstractMCS.
     */
    @Test
    public void testIsStereoMisMatch() {
        System.out.println("isStereoMisMatch");
        AbstractMCS instance = new AbstractMCSImpl();
        boolean expResult = false;
        boolean result = instance.isStereoMisMatch();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isSubgraph method, of class AbstractMCS.
     */
    @Test
    public void testIsSubgraph() {
        System.out.println("isSubgraph");
        AbstractMCS instance = new AbstractMCSImpl();
        boolean expResult = false;
        boolean result = instance.isSubgraph();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTanimotoSimilarity method, of class AbstractMCS.
     */
    @Test
    public void testGetTanimotoSimilarity() throws Exception {
        System.out.println("getTanimotoSimilarity");
        AbstractMCS instance = new AbstractMCSImpl();
        double expResult = 0.0;
        double result = instance.getTanimotoSimilarity();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEuclideanDistance method, of class AbstractMCS.
     */
    @Test
    public void testGetEuclideanDistance() throws Exception {
        System.out.println("getEuclideanDistance");
        AbstractMCS instance = new AbstractMCSImpl();
        double expResult = 0.0;
        double result = instance.getEuclideanDistance();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllAtomMapping method, of class AbstractMCS.
     */
    @Test
    public void testGetAllAtomMapping() {
        System.out.println("getAllAtomMapping");
        AbstractMCS instance = new AbstractMCSImpl();
        List expResult = null;
        List result = instance.getAllAtomMapping();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllMapping method, of class AbstractMCS.
     */
    @Test
    public void testGetAllMapping() {
        System.out.println("getAllMapping");
        AbstractMCS instance = new AbstractMCSImpl();
        List expResult = null;
        List result = instance.getAllMapping();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFirstAtomMapping method, of class AbstractMCS.
     */
    @Test
    public void testGetFirstAtomMapping() {
        System.out.println("getFirstAtomMapping");
        AbstractMCS instance = new AbstractMCSImpl();
        Map expResult = null;
        Map result = instance.getFirstAtomMapping();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFirstMapping method, of class AbstractMCS.
     */
    @Test
    public void testGetFirstMapping() {
        System.out.println("getFirstMapping");
        AbstractMCS instance = new AbstractMCSImpl();
        Map expResult = null;
        Map result = instance.getFirstMapping();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class AbstractMCSImpl extends AbstractMCS {

        @Override
        public void init(IAtomContainer source, IAtomContainer target, boolean removeHydrogen, boolean cleanMol) throws CDKException {
        }

        @Override
        public void init(IQueryAtomContainer source, IAtomContainer target) throws CDKException {
        }

        @Override
        public void setChemFilters(boolean stereoFilter, boolean fragmentFilter, boolean energyFilter) {
        }

        @Override
        public Double getEnergyScore(int Key) {
            return null;
        }

        @Override
        public Integer getFragmentSize(int Key) {
            return null;
        }

        @Override
        public IAtomContainer getProductMolecule() {
            return null;
        }

        @Override
        public IAtomContainer getReactantMolecule() {
            return null;
        }

        @Override
        public Integer getStereoScore(int Key) {
            return null;
        }

        @Override
        public boolean isStereoMisMatch() {
            return false;
        }

        @Override
        public boolean isSubgraph() {
            return false;
        }

        @Override
        public double getTanimotoSimilarity() throws IOException {
            return 0.0;
        }

        @Override
        public double getEuclideanDistance() throws IOException {
            return 0.0;
        }

        @Override
        public List<Map<IAtom, IAtom>> getAllAtomMapping() {
            return null;
        }

        public List<Map<Integer, Integer>> getAllMapping() {
            return null;
        }

        @Override
        public Map<IAtom, IAtom> getFirstAtomMapping() {
            return null;
        }

        @Override
        public Map<Integer, Integer> getFirstMapping() {
            return null;
        }

        @Override
        public double getBondSensitiveTimeOut() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setBondSensitiveTimeOut(double bondSensitiveTimeOut) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public double getBondInSensitiveTimeOut() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setBondInSensitiveTimeOut(double bondInSensitiveTimeOut) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
