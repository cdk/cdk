/* Copyright (C) 2007  Rajarshi Guha <>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.smiles.smarts;

import static java.util.Collections.sort;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * JUnit test routines for the SMARTS substructure search.
 *
 * @author Rajarshi Guha
 * @cdk.module test-smarts
 * @cdk.require ant1.6
 */
public class SMARTSQueryToolTest extends CDKTestCase {

    /**
     * @throws CDKException
     * @cdk.bug 2788357
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLexicalError() throws Exception {
        SMARTSQueryTool sqt = new SMARTSQueryTool("Epoxide", DefaultChemObjectBuilder.getInstance());
    }

    @Test
    public void testQueryTool() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C");
        SMARTSQueryTool querytool = new SMARTSQueryTool("O=CO", DefaultChemObjectBuilder.getInstance());

        boolean status = querytool.matches(atomContainer);
        assertTrue(status);

        int nmatch = querytool.countMatches();
        Assert.assertEquals(2, nmatch);

        List<Integer> map1 = new ArrayList<Integer>();
        map1.add(1);
        map1.add(2);
        map1.add(3);

        List<Integer> map2 = new ArrayList<Integer>();
        map2.add(3);
        map2.add(4);
        map2.add(5);

        List<List<Integer>> mappings = querytool.getMatchingAtoms();
        List<Integer> ret1 = mappings.get(0);
        sort(ret1);
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(map1.get(i), ret1.get(i));
        }

        List<Integer> ret2 = mappings.get(1);
        sort(ret2);
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(map2.get(i), ret2.get(i));
        }
    }

    @Test
    public void testQueryToolSingleAtomCase() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("C1CCC12CCCC2");
        SMARTSQueryTool querytool = new SMARTSQueryTool("C", DefaultChemObjectBuilder.getInstance());

        boolean status = querytool.matches(atomContainer);
        assertTrue(status);

        int nmatch = querytool.countMatches();
        Assert.assertEquals(8, nmatch);
    }

    @Test
    public void testQueryToolResetSmarts() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("C1CCC12CCCC2");
        SMARTSQueryTool querytool = new SMARTSQueryTool("C", DefaultChemObjectBuilder.getInstance());

        boolean status = querytool.matches(atomContainer);
        assertTrue(status);

        int nmatch = querytool.countMatches();
        Assert.assertEquals(8, nmatch);

        querytool.setSmarts("CC");
        status = querytool.matches(atomContainer);
        assertTrue(status);

        nmatch = querytool.countMatches();
        Assert.assertEquals(18, nmatch);

        List<List<Integer>> umatch = querytool.getUniqueMatchingAtoms();
        Assert.assertEquals(9, umatch.size());
    }

    @Test
    public void testUniqueQueries() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("c1ccccc1CCCNCCCc1ccccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
        Aromaticity.cdkLegacy().apply(atomContainer);
        SMARTSQueryTool querytool = new SMARTSQueryTool("c1ccccc1", DefaultChemObjectBuilder.getInstance());

        boolean status = querytool.matches(atomContainer);
        assertTrue(status);

        int nmatch = querytool.countMatches();
        Assert.assertEquals(24, nmatch);

        List<List<Integer>> umatch = querytool.getUniqueMatchingAtoms();
        Assert.assertEquals(2, umatch.size());
    }

    @Test
    public void testQuery() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("c12cc(CCN)ccc1c(COC)ccc2");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
        Aromaticity.cdkLegacy().apply(atomContainer);
        SMARTSQueryTool querytool = new SMARTSQueryTool("c12ccccc1cccc2", DefaultChemObjectBuilder.getInstance());

        boolean status = querytool.matches(atomContainer);
        assertTrue(status);

        int nmatch = querytool.countMatches();
        Assert.assertEquals(4, nmatch);

        List<List<Integer>> umatch = querytool.getUniqueMatchingAtoms();
        Assert.assertEquals(1, umatch.size());
    }

    /**
     * Note that we don't test the generated SMILES against the
     * molecule obtained from the factory since the factory derived
     * molecule does not have an explicit hydrogen, which it really should
     * have.
     *
     * @cdk.bug 1985811
     */
    @Test
    public void testIndoleAgainstItself() throws Exception {

        IAtomContainer indole = TestMoleculeFactory.makeIndole();
        addImplicitHydrogens(indole);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(indole);
        Aromaticity.cdkLegacy().apply(indole);
        SmilesGenerator generator = new SmilesGenerator().aromatic();
        String indoleSmiles = generator.create(indole);
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        indole = smilesParser.parseSmiles(indoleSmiles);

        SMARTSQueryTool querytool = new SMARTSQueryTool(indoleSmiles, DefaultChemObjectBuilder.getInstance());
        assertTrue(querytool.matches(indole));
    }

    /**
     * @cdk.bug 2149621
     */
    @Test
    public void testMethane() throws Exception {
        IAtomContainer methane = SilentChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom carbon = methane.getBuilder().newInstance(IAtom.class, Elements.CARBON);
        carbon.setImplicitHydrogenCount(4);
        methane.addAtom(carbon);

        SMARTSQueryTool sqt = new SMARTSQueryTool("CC", DefaultChemObjectBuilder.getInstance());
        boolean matches = sqt.matches(methane);
        assertFalse(matches);

    }

    @Test(expected = NullPointerException.class)
    public void nullAromaticity() {
        SMARTSQueryTool sqt = new SMARTSQueryTool("CC", DefaultChemObjectBuilder.getInstance());
        sqt.setAromaticity(null);
    }

    @Test
    public void setAromaticity() throws Exception {
        SMARTSQueryTool sqt = new SMARTSQueryTool("[a]", DefaultChemObjectBuilder.getInstance());

        IAtomContainer furan = smiles("O1C=CC=C1");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(furan);

        sqt.setAromaticity(new Aromaticity(ElectronDonation.cdk(), Cycles.mcb()));
        assertTrue(sqt.matches(furan, true));

        sqt.setAromaticity(new Aromaticity(ElectronDonation.piBonds(), Cycles.mcb()));
        assertFalse(sqt.matches(furan, true));
    }

    static IAtomContainer smiles(String smi) throws Exception {
        return new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smi);
    }
}
