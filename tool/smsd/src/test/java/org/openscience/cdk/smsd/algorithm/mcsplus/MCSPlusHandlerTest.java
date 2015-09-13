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
package org.openscience.cdk.smsd.algorithm.mcsplus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.Isomorphism;
import org.openscience.cdk.smsd.interfaces.AbstractMCSAlgorithmTest;
import org.openscience.cdk.smsd.interfaces.Algorithm;
import org.openscience.cdk.smsd.tools.MolHandler;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Unit testing for the {@link MCSPlusHandler} class.
 * @author     Syed Asad Rahman
 * @author     egonw
 * @cdk.module test-smsd
 */
public class MCSPlusHandlerTest extends AbstractMCSAlgorithmTest {

    @BeforeClass
    public static void setMCSAlgorithm() {
        AbstractMCSAlgorithmTest.setMCSAlgorithm(new MCSPlusHandler());
    }

    /**
     * Test of searchMCS method, of class MCSPlusHandler.
     */
    @Test
    @Override
    public void testSearchMCS() {
        try {
            SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
            IAtomContainer target = null;
            target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
            IAtomContainer queryac = null;
            queryac = sp.parseSmiles("Nc1ccccc1");
            MCSPlusHandler smsd1 = new MCSPlusHandler();
            MolHandler mol1 = new MolHandler(queryac, true, true);
            MolHandler mol2 = new MolHandler(target, true, true);
            smsd1.set(mol1, mol2);
            smsd1.searchMCS(true);
            assertNotNull(smsd1.getFirstMapping());
        } catch (InvalidSmilesException ex) {
            Logger.getLogger(MCSPlusHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of set method, of class MCSPlusHandler.
     * @throws Exception
     */
    @Test
    public void testSet_IAtomContainer_IAtomContainer() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        MCSPlusHandler smsd1 = new MCSPlusHandler();
        MolHandler mol1 = new MolHandler(queryac, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        smsd1.set(mol1, mol2);
        smsd1.searchMCS(true);
        assertNotNull(smsd1.getFirstMapping());
    }

    /**
     * Test of set method, of class MCSPlusHandler.
     * @throws CDKException
     * @throws IOException
     */
    @Test
    public void testSet_String_String() throws CDKException, IOException {
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

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(query, target, true, true);
        smsd1.setChemFilters(true, true, true);
        double score = 1.0;
        assertEquals(score, smsd1.getTanimotoSimilarity(), 0.0001);
    }

    /**
     * Test of set method, of class MCSPlusHandler.
     * @throws InvalidSmilesException
     */
    @Test
    public void testSet_MolHandler_MolHandler() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

        IAtomContainer target1 = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");
        MolHandler source = new MolHandler(queryac, true, true);
        MolHandler target = new MolHandler(target1, true, true);
        MCSPlusHandler instance = new MCSPlusHandler();
        instance.set(source, target);
        instance.searchMCS(true);
        assertNotNull(instance.getFirstMapping());
    }

    /**
     * Test of getAllAtomMapping method, of class MCSPlusHandler.
     * @throws InvalidSmilesException
     */
    @Test
    @Ignore("Failing but not going to be fixed")
    public void testGetAllAtomMapping() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        sp.kekulise(false);
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(queryac);

        MCSPlusHandler smsd1 = new MCSPlusHandler();
        MolHandler mol1 = new MolHandler(queryac, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        smsd1.set(mol1, mol2);
        smsd1.searchMCS(true);
        assertNotNull(smsd1.getFirstMapping());

        assertEquals(4, smsd1.getAllAtomMapping().size());
    }

    /**
     * Test of getAllMapping method, of class MCSPlusHandler.
     * @throws InvalidSmilesException
     */
    @Test
    @Ignore("Failing but not going to be fixed")
    public void testGetAllMapping() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        sp.kekulise(false);
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(queryac);

        MCSPlusHandler smsd1 = new MCSPlusHandler();
        MolHandler mol1 = new MolHandler(queryac, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        smsd1.set(mol1, mol2);
        smsd1.searchMCS(true);
        assertNotNull(smsd1.getFirstMapping());

        assertEquals(4, smsd1.getAllMapping().size());
    }

    /**
     * Test of getFirstAtomMapping method, of class MCSPlusHandler.
     * @throws InvalidSmilesException
     */
    @Test
    public void testGetFirstAtomMapping() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        MCSPlusHandler smsd1 = new MCSPlusHandler();
        MolHandler mol1 = new MolHandler(queryac, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        smsd1.set(mol1, mol2);
        smsd1.searchMCS(true);
        assertNotNull(smsd1.getFirstMapping());

        assertEquals(7, smsd1.getFirstAtomMapping().size());
    }

    /**
     * Test of getFirstMapping method, of class MCSPlusHandler.
     * @throws InvalidSmilesException
     */
    @Test
    public void testGetFirstMapping() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        MCSPlusHandler smsd1 = new MCSPlusHandler();
        MolHandler mol1 = new MolHandler(queryac, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        smsd1.set(mol1, mol2);
        smsd1.searchMCS(true);
        assertNotNull(smsd1.getFirstMapping());

        assertEquals(7, smsd1.getFirstMapping().size());
    }
}
