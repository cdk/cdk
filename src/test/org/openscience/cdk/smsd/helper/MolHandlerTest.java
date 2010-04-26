
/* Copyright (C) 2009-2010 Syed Asad Rahman {asad@ebi.ac.uk}
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
package org.openscience.cdk.smsd.helper;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import static org.junit.Assert.*;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * @cdk.module test-smsd
 * @cdk.require java1.6+
 */
public class MolHandlerTest {

    public MolHandlerTest() {
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
     * Test of getMolecule method, of class MolHandler.
     */
    @Test
    public void testGetMolecule() {
        System.out.println("getMolecule");
        MolHandler instance = new MolHandler(new Molecule(), true);
        IAtomContainer result = instance.getMolecule();
        assertNotNull(result);
    }

    /**
     * Test of getRemoveHydrogenFlag method, of class MolHandler.
     */
    @Test
    public void testGetRemoveHydrogenFlag() {
        System.out.println("getRemoveHydrogenFlag");
        MolHandler instance = new MolHandler(new Molecule(), true, true);
        boolean expResult = true;
        boolean result = instance.getRemoveHydrogenFlag();
        assertEquals(expResult, result);
    }

    /**
     * Test of getFragmentedMolecule method, of class MolHandler.
     */
    @Test
    public void testGetFragmentedMolecule() {
        try {
            System.out.println("getFragmentedMolecule");
            String fragmentMolSmiles = "C1=CC=CC=C1.C1=CC2=C(C=C1)C=CC=C2";
            SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
            IAtomContainer molecule = sp.parseSmiles(fragmentMolSmiles);
            MolHandler instance = new MolHandler(molecule, true, true);
            int expResult = 2;
            IAtomContainerSet result = instance.getFragmentedMolecule();
            assertEquals(expResult, result.getAtomContainerCount());
        } catch (InvalidSmilesException ex) {
            Logger.getLogger(MolHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getConnectedFlag method, of class MolHandler.
     */
    @Test
    public void testGetConnectedFlag() {
        try {
            System.out.println("getConnectedFlag");
            String fragmentMolSmiles = "C1=CC=CC=C1.C1=CC2=C(C=C1)C=CC=C2";
            SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
            IAtomContainer molecule = sp.parseSmiles(fragmentMolSmiles);
            MolHandler instance = new MolHandler(molecule, true, true);
            boolean expResult = false;
            boolean result = instance.getConnectedFlag();
            assertEquals(expResult, result);
        } catch (InvalidSmilesException ex) {
            Logger.getLogger(MolHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
