/* Copyright (C) 2003-2007  Christoph Steinbeck
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.layout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @cdk.module  test-sdg
 * @author      steinbeck
 * @cdk.created September 4, 2003
 * @cdk.require java1.4+
 */
public class TemplateHandlerTest extends CDKTestCase {

    public boolean                           standAlone = false;
    private static ILoggingTool              logger     = LoggingToolFactory
                                                                .createLoggingTool(TemplateHandlerTest.class);

    private static SmilesParser              sp         = null;
    private static StructureDiagramGenerator sdg        = null;

    /**
     *  The JUnit setup method
     */
    @BeforeClass
    public static void setUp() throws Exception {
        sdg = new StructureDiagramGenerator();
        sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    }

    /**
     *  A unit test for JUnit
     *
     *@exception  Exception  Description of the Exception
     */
    @Test
    public void testInit() throws Exception {
        TemplateHandler th = new TemplateHandler(DefaultChemObjectBuilder.getInstance());

        Assert.assertEquals(5, th.getTemplateCount());
    }

    @Test
    public void testDetection() throws Exception {
        TemplateHandler th = new TemplateHandler(DefaultChemObjectBuilder.getInstance());
        String smiles = "CC12C3(C6CC6)C4(C)C1C5(C(CC)C)C(C(CC)C)2C(C)3C45CC(C)C";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assert.assertTrue(th.mapTemplates(mol));
    }

    /**
     * Tests if a template matches if just an element is non-carbon.
     */
    @Test
    public void testOtherElements() throws Exception {
        boolean itIsInThere = false;
        TemplateHandler th = new TemplateHandler(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = TestMoleculeFactory.makeSteran();
        itIsInThere = th.mapTemplates(mol);
        Assert.assertTrue(itIsInThere);
        mol.getAtom(0).setSymbol("N");
        itIsInThere = th.mapTemplates(mol);
        Assert.assertTrue(itIsInThere);
    }

    /**
     * Tests if a template matches if just and bond order is changed.
     */
    @Test
    public void testOtherBondOrder() throws Exception {
        boolean itIsInThere = false;
        TemplateHandler th = new TemplateHandler(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = TestMoleculeFactory.makeSteran();
        itIsInThere = th.mapTemplates(mol);
        Assert.assertTrue(itIsInThere);
        mol.getBond(0).setOrder(IBond.Order.DOUBLE);
        itIsInThere = th.mapTemplates(mol);
        Assert.assertTrue(itIsInThere);
    }

    @Test
    public void testAddMolecule() throws Exception {
        logger.debug("***TestAddMolecule***");
        boolean itIsInThere = false;
        TemplateHandler th = new TemplateHandler(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = TestMoleculeFactory.makeAlphaPinene();
        sdg.setMolecule(mol);
        sdg.generateCoordinates();
        mol = sdg.getMolecule();

        String smiles = "C1=C(C)C2CC(C1)C2(C)(C)";
        IAtomContainer smilesMol = sp.parseSmiles(smiles);
        itIsInThere = th.mapTemplates(smilesMol);
        logger.debug("Alpha-Pinene found by templateMapper: " + itIsInThere);
        Assert.assertFalse(itIsInThere);
        th.addMolecule(mol);
        logger.debug("now adding template for alpha-Pinen and trying again.");
        itIsInThere = th.mapTemplates(smilesMol);
        logger.debug("Alpha-Pinene found by templateMapper: " + itIsInThere);
        Assert.assertTrue(itIsInThere);
    }

    @Test
    public void testRemoveMolecule() throws Exception {
        logger.debug("***TestRemoveMolecule***");
        boolean itIsInThere = false;
        TemplateHandler th = new TemplateHandler(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = TestMoleculeFactory.makeAlphaPinene();
        sdg.setMolecule(mol);
        sdg.generateCoordinates();
        mol = sdg.getMolecule();

        String smiles = "C1=C(C)C2CC(C1)C2(C)(C)";
        IAtomContainer smilesMol = sp.parseSmiles(smiles);
        itIsInThere = th.mapTemplates(smilesMol);
        logger.debug("Alpha-Pinene found by templateMapper: " + itIsInThere);
        Assert.assertFalse(itIsInThere);
        th.addMolecule(mol);
        logger.debug("now adding template for alpha-Pinen and trying again.");
        itIsInThere = th.mapTemplates(smilesMol);
        logger.debug("Alpha-Pinene found by templateMapper: " + itIsInThere);
        Assert.assertTrue(itIsInThere);
        logger.debug("now removing template for alpha-Pinen again and trying again.");
        th.removeMolecule(mol);
        itIsInThere = th.mapTemplates(smilesMol);
        logger.debug("Alpha-Pinene found by templateMapper: " + itIsInThere);
        Assert.assertFalse(itIsInThere);

    }

    /**
     * Loads a molecule with two adamantanes and one cubane
     * substructure and tests whether all are found.
     */
    public void getMappedSubstructures_IAtomContainer() throws Exception {
        // Set up molecule reader
        String filename = "data/mdl/diadamantane-cubane.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader molReader = new MDLReader(ins, Mode.STRICT);

        // Read molecule
        IAtomContainer molecule = (IAtomContainer) molReader.read(DefaultChemObjectBuilder.getInstance().newInstance(
                IAtomContainer.class));

        // Map templates
        TemplateHandler th = new TemplateHandler(DefaultChemObjectBuilder.getInstance());
        IAtomContainerSet mappedStructures = th.getMappedSubstructures(molecule);

        // Do the Assert.assertion
        Assert.assertEquals("3 mapped templates", 3, mappedStructures.getAtomContainerCount());
    }

    @Test public void convert() throws IOException {
        TemplateHandler templateHandler = new TemplateHandler(SilentChemObjectBuilder.getInstance());
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        templateHandler.toIdentityTemplateLibrary().store(bout);
        assertThat(new String(bout.toByteArray()), is("C1C2CC3CC1CC(C2)C3 |(-1.07,-1.59,;.38,-1.21,;1.82,-1.59,;1.07,-.29,;-.38,-.67,;-1.82,-.29,;-1.82,1.21,;-.37,1.59,;.38,.29,;1.07,1.21,)|\n"
                                                      + "C12C3C4C1C5C2C3C45 |(.62,-.99,;-.88,-1.12,;-1.23,.43,;.26,.57,;.88,1.12,;1.23,-.43,;-.26,-.57,;-.62,.98,)|\n"
                                                      + "C1C2CC3C4CC5CC(C14)C(C2)C3C5 |(-1.82,2.15,;-.37,2.53,;1.07,2.15,;1.07,.65,;-.38,.27,;-.38,-1.23,;.37,-2.53,;-1.07,-2.15,;-1.07,-.65,;-1.82,.65,;.38,-.27,;.38,1.23,;1.82,-.65,;1.82,-2.15,)|\n"
                                                      + "C1CCC2C(C1)CCC3C4CCCC4CCC23 |(-6.51,.72,;-6.51,-.78,;-5.22,-1.53,;-3.92,-.78,;-3.92,.72,;-5.22,1.47,;-2.62,1.47,;-1.32,.72,;-1.32,-.78,;-.02,-1.53,;1.41,-1.07,;2.29,-2.28,;1.41,-3.49,;-.02,-3.03,;-1.32,-3.78,;-2.62,-3.03,;-2.62,-1.53,)|\n"
                                                      + "C1CCCCCCCCCCCCC1 |(-.04,1.51,;1.26,.76,;1.26,-.74,;2.56,-1.49,;2.56,-2.99,;1.29,-3.72,;1.31,-5.29,;-.09,-5.89,;-1.34,-5.24,;-1.34,-3.74,;-2.63,-2.99,;-2.63,-1.49,;-1.34,-.74,;-1.34,.76,)|\n"));
    }

}
