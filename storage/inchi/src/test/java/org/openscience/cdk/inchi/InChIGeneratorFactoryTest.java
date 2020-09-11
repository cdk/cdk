/* Copyright (C)      2006  Sam Adams <sea36@users.sf.net>
 *                    2007  Rajarshi Guha <rajarshi.guha@gmail.com>
 *               2010,2012  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.inchi;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDK;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_RET;
import org.junit.Assert;
import org.junit.Test;

/**
 * @cdk.module test-inchi
 */
public class InChIGeneratorFactoryTest {

    @Test
    public void testGetInstance() throws CDKException {
        InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
        Assert.assertNotNull(factory);
    }

    /**
     * Because we are not setting any options, we get an Standard InChI.
     */
    @Test
    public void testGetInChIGenerator_IAtomContainer() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        InChIGenerator gen = InChIGeneratorFactory.getInstance().getInChIGenerator(ac);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1S/ClH/h1H", gen.getInchi());
    }

    /**
     * Because we are setting an options, we get a non-standard InChI.
     */
    @Test
    public void testGetInChIGenerator_IAtomContainer_String() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        InChIGenerator gen = InChIGeneratorFactory.getInstance().getInChIGenerator(ac, "FixedH");
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/ClH/h1H", gen.getInchi());
    }

    /**
     * Because we are setting no option, we get a Standard InChI.
     */
    @Test
    public void testGetInChIGenerator_IAtomContainer_NullString() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        InChIGenerator gen = InChIGeneratorFactory.getInstance().getInChIGenerator(ac, (String) null);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1S/ClH/h1H", gen.getInchi());
    }

    /**
     * Because we are setting an options, we get a non-standard InChI.
     */
    @Test
    public void testGetInChIGenerator_IAtomContainer_List() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        List<INCHI_OPTION> options = new ArrayList<INCHI_OPTION>();
        options.add(INCHI_OPTION.FixedH);
        InChIGenerator gen = InChIGeneratorFactory.getInstance().getInChIGenerator(ac, options);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/ClH/h1H", gen.getInchi());
    }

    /**
     * Because we are setting an options, we get a non-standard InChI.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInChIGenerator_IAtomContainer_NullList() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        InChIGeneratorFactory.getInstance().getInChIGenerator(ac, (List<INCHI_OPTION>) null);
    }

    @Test
    public void testGetInChIToStructure_String_IChemObjectBuilder() throws CDKException {
        InChIToStructure parser = InChIGeneratorFactory.getInstance().getInChIToStructure("InChI=1/ClH/h1H",
                DefaultChemObjectBuilder.getInstance());
        Assert.assertNotNull(parser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetInChIToStructure_String_IChemObjectBuilder_NullString() throws CDKException {
        InChIGeneratorFactory.getInstance().getInChIToStructure("InChI=1/ClH/h1H",
                DefaultChemObjectBuilder.getInstance(), (String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetInChIToStructure_String_IChemObjectBuilder_NullList() throws CDKException {
        InChIGeneratorFactory.getInstance().getInChIToStructure("InChI=1/ClH/h1H",
                DefaultChemObjectBuilder.getInstance(), (List<String>) null);
    }

    /**
     * No options set.
     */
    @Test
    public void testGetInChIToStructure_String_IChemObjectBuilder_List() throws CDKException {
        InChIToStructure parser = InChIGeneratorFactory.getInstance().getInChIToStructure("InChI=1/ClH/h1H",
                DefaultChemObjectBuilder.getInstance(), new ArrayList<String>());
        Assert.assertNotNull(parser);
    }

    @Test
    public void testSMILESConversion_TopologicalCentre() throws CDKException {

        // (2R,3R,4S,5R,6S)-3,5-dimethylheptane-2,4,6-triol
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer container = parser.parseSmiles("C[C@@H](O)[C@@H](C)[C@@H](O)[C@H](C)[C@H](C)O");

        InChIGenerator generator = InChIGeneratorFactory.getInstance().getInChIGenerator(container);

        String expected = "InChI=1S/C9H20O3/c1-5(7(3)10)9(12)6(2)8(4)11/h5-12H,1-4H3/t5-,6-,7-,8+,9-/m1/s1";
        String actual = generator.getInchi();

        Assert.assertEquals("Incorrect InCHI generated for topological centre", expected, actual);

    }

    @Test
    public void dontIgnoreMajorIsotopes() throws CDKException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        InChIGeneratorFactory inchifact = InChIGeneratorFactory.getInstance();
        assertThat(inchifact.getInChIGenerator(smipar.parseSmiles("[12CH4]")).getInchi(),
                   containsString("/i"));
        assertThat(inchifact.getInChIGenerator(smipar.parseSmiles("C")).getInchi(),
                   not(containsString("/i")));
    }

    // InChI only supports cumulenes of length 2 (CC=[C@]=CC) and 3
    // (C/C=C=C=C=C/C) longer ones should be ignored
    @Test
    public void longerExtendedTetrahedralsIgnored() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol  = smipar.parseSmiles("CC=C=C=[C@]=C=C=CC");
        InChIGenerator gen = InChIGeneratorFactory.getInstance().getInChIGenerator(mol);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1S/C9H8/c1-3-5-7-9-8-6-4-2/h3-4H,1-2H3", gen.getInchi());
    }

    /**
     * Tests the aromatic bonds option in the InChI factory class.
     */
    @Test
    public void testInChIGenerator_AromaticBonds() throws CDKException {

        try {
            // create a fairly complex aromatic molecule
            IAtomContainer tetrazole = TestMoleculeFactory.makeTetrazole();
            for (IAtom atom : tetrazole.atoms())
                atom.setImplicitHydrogenCount(null);
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tetrazole);
            Aromaticity.cdkLegacy().apply(tetrazole);

            InChIGeneratorFactory inchiFactory = InChIGeneratorFactory.getInstance();
            inchiFactory.setIgnoreAromaticBonds(false);

            // include aromatic bonds by default
            InChIGenerator genAromaticity1 = inchiFactory.getInChIGenerator(tetrazole);

            // exclude aromatic bonds
            assertFalse(inchiFactory.getIgnoreAromaticBonds());
            inchiFactory.setIgnoreAromaticBonds(true);
            assertTrue(inchiFactory.getIgnoreAromaticBonds());
            InChIGenerator genNoAromaticity = inchiFactory.getInChIGenerator(tetrazole);

            // include aromatic bonds again
            inchiFactory.setIgnoreAromaticBonds(false);
            assertFalse(inchiFactory.getIgnoreAromaticBonds());
            InChIGenerator genAromaticity2 = inchiFactory.getInChIGenerator(tetrazole);

            // with the aromatic bonds included, no InChI can be generated
            Assert.assertEquals("return status was not in error", INCHI_RET.ERROR, genAromaticity1.getReturnStatus());
            Assert.assertEquals("return status was not in error", INCHI_RET.ERROR, genAromaticity2.getReturnStatus());
            // excluding the aromatic bonds gives the normal InChI
            Assert.assertEquals("return status was not okay", INCHI_RET.OKAY, genNoAromaticity.getReturnStatus());
            Assert.assertEquals("InChIs did not match", "InChI=1S/CH2N4/c1-2-4-5-3-1/h1H,(H,2,3,4,5)",
                    genNoAromaticity.getInchi());
        } finally {
            InChIGeneratorFactory.getInstance().setIgnoreAromaticBonds(true);
        }
    }
}
