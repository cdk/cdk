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
package org.openscience.cdk.smsd.algorithm.vflib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.interfaces.AbstractMCSAlgorithmTest;
import org.openscience.cdk.smsd.tools.MolHandler;

/**
 * Unit testing for the {@link VFlibMCSHandler} class.
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 *
 * @cdk.module test-smsd
 * @cdk.require java1.6+
 */
public class VFlibMCSHandlerTest extends AbstractMCSAlgorithmTest {

    public VFlibMCSHandlerTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @BeforeClass
    public static void setMCSAlgorithm() {
        AbstractMCSAlgorithmTest.setMCSAlgorithm(new VFlibMCSHandler());
    }

    /**
     * Test of searchMCS method, of class VFlibMCSHandler.
     */
    @Test
    @Override
    public void testSearchMCS() {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = null;
        try {
            target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        } catch (InvalidSmilesException ex) {
            Logger.getLogger(VFlibMCSHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        IAtomContainer queryac = null;
        try {
            queryac = sp.parseSmiles("Nc1ccccc1");
        } catch (InvalidSmilesException ex) {
            Logger.getLogger(VFlibMCSHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        VFlibMCSHandler smsd1 = new VFlibMCSHandler();
        MolHandler mol1 = new MolHandler(queryac, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        smsd1.set(mol1, mol2);
        smsd1.searchMCS(true);
        assertNotNull(smsd1.getFirstMapping());
    }

    /**
     * Test of set method, of class VFlibMCSHandler.
     * @throws Exception
     */
    @Test
    public void testSet_IAtomContainer_IAtomContainer() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        VFlibMCSHandler smsd1 = new VFlibMCSHandler();
        MolHandler mol1 = new MolHandler(queryac, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        smsd1.set(mol1, mol2);
        smsd1.searchMCS(true);
        assertNotNull(smsd1.getFirstMapping());
    }

    /**
     * Test of set method, of class VFlibMCSHandler.
     * @throws CDKException
     */
    @Test
    public void testSet_String_String() throws CDKException {
        String molfile = "data/mdl/decalin.mol";
        String queryfile = "data/mdl/decalin.mol";
        IAtomContainer query = new AtomContainer();
        IAtomContainer target = new AtomContainer();

        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(molfile);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        reader.read(query);
        ins = this.getClass().getClassLoader().getResourceAsStream(queryfile);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        reader.read(target);

        VFlibMCSHandler smsd1 = new VFlibMCSHandler();
        MolHandler mol1 = new MolHandler(query, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        smsd1.set(mol1, mol2);
        smsd1.searchMCS(true);

        assertNotNull(smsd1.getFirstMapping());
    }

    /**
     * Test of set method, of class VFlibMCSHandler.
     * @throws InvalidSmilesException
     */
    @Test
    public void testSet_MolHandler_MolHandler() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer target1 = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");
        MolHandler source = new MolHandler(queryac, true, true);
        MolHandler target = new MolHandler(target1, true, true);
        VFlibMCSHandler instance = new VFlibMCSHandler();
        instance.set(source, target);
        instance.searchMCS(true);
        assertNotNull(instance.getFirstMapping());
    }

    /**
     * Test of getAllAtomMapping method, of class VFlibMCSHandler.
     * @throws InvalidSmilesException
     */
    @Test
    public void testGetAllAtomMapping() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        VFlibMCSHandler smsd1 = new VFlibMCSHandler();
        MolHandler mol1 = new MolHandler(queryac, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        smsd1.set(mol1, mol2);
        smsd1.searchMCS(true);
        assertNotNull(smsd1.getFirstMapping());

        assertEquals(4, smsd1.getAllAtomMapping().size());
    }

    /**
     * Test of getAllMapping method, of class VFlibMCSHandler.
     * @throws InvalidSmilesException
     */
    @Test
    public void testGetAllMapping() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        VFlibMCSHandler smsd1 = new VFlibMCSHandler();
        MolHandler mol1 = new MolHandler(queryac, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        smsd1.set(mol1, mol2);
        smsd1.searchMCS(true);
        assertNotNull(smsd1.getFirstMapping());

        assertEquals(4, smsd1.getAllMapping().size());
    }

    /**
     * Test of getFirstAtomMapping method, of class VFlibMCSHandler.
     * @throws InvalidSmilesException
     */
    @Test
    public void testGetFirstAtomMapping() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        VFlibMCSHandler smsd1 = new VFlibMCSHandler();
        MolHandler mol1 = new MolHandler(queryac, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        smsd1.set(mol1, mol2);
        smsd1.searchMCS(true);
        assertNotNull(smsd1.getFirstMapping());

        assertEquals(7, smsd1.getFirstAtomMapping().size());
    }

    /**
     * Test of getFirstMapping method, of class VFlibMCSHandler.
     * @throws InvalidSmilesException
     */
    @Test
    public void testGetFirstMapping() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        VFlibMCSHandler smsd1 = new VFlibMCSHandler();
        MolHandler mol1 = new MolHandler(queryac, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        smsd1.set(mol1, mol2);
        smsd1.searchMCS(true);
        assertNotNull(smsd1.getFirstMapping());

        assertEquals(7, smsd1.getFirstMapping().size());
    }
}
