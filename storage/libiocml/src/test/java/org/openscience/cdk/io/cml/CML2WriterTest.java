/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.io.cml;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.ReactionScheme;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionScheme;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.AtomContainerSet;
import org.openscience.cdk.silent.ChemModel;
import org.openscience.cdk.silent.Crystal;
import org.openscience.cdk.silent.Reaction;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestCase for the reading CML 2 files using a few test files
 * in data/cmltest.
 *
 * @cdk.module test-libiocml
 * @cdk.require java1.5+
 */
public class CML2WriterTest extends CDKTestCase {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(CML2WriterTest.class);

    @Test
    public void testCMLWriterBenzene() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainer molecule = TestMoleculeFactory.makeBenzene();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        Aromaticity.cdkLegacy().apply(molecule);
        CMLWriter cmlWriter = new CMLWriter(writer);

        cmlWriter.write(molecule);
        cmlWriter.close();
        logger.debug("****************************** testCMLWriterBenzene()");
        logger.debug(writer.toString());
        logger.debug("******************************");
        Assert.assertTrue(writer.toString().indexOf("</molecule>") != -1);
    }

    /**
     * Test example with one explicit carbon, and four implicit hydrogens.
     *
     * @cdk.bug 1655045
     */
    @Test
    public void testHydrogenCount() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainer molecule = new AtomContainer(); // methane
        molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class, Elements.CARBON));
        molecule.getAtom(0).setImplicitHydrogenCount(4);
        CMLWriter cmlWriter = new CMLWriter(writer);

        cmlWriter.write(molecule);
        cmlWriter.close();
        logger.debug("****************************** testHydrogenCount()");
        logger.debug(writer.toString());
        logger.debug("******************************");
        Assert.assertTrue(writer.toString().indexOf("hydrogenCount=\"4\"") != -1);
    }

    @Test
    public void testNullFormalCharge() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainer molecule = new AtomContainer(); // methane
        molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class, Elements.CARBON));
        molecule.getAtom(0).setFormalCharge(null);
        CMLWriter cmlWriter = new CMLWriter(writer);

        cmlWriter.write(molecule);
        cmlWriter.close();
        logger.debug("****************************** testNullFormalCharge()");
        logger.debug(writer.toString());
        logger.debug("******************************");
        Assert.assertFalse(writer.toString().contains("formalCharge"));
    }

    /**
    * Test example with one explicit carbon, writing of MassNumber
    *
    */
    @Test
    public void testMassNumber() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainer mol = new AtomContainer();
        Atom atom = new Atom("C");
        atom.setMassNumber(new Integer(12));
        mol.addAtom(atom);
        CMLWriter cmlWriter = new CMLWriter(writer);

        cmlWriter.write(mol);
        cmlWriter.close();
        logger.debug("****************************** testMAssNumber()");
        logger.debug(writer.toString());
        logger.debug("******************************");
        Assert.assertTrue(writer.toString().indexOf("isotopeNumber=\"12\"") != -1);
    }

    /**
     * Test example with one explicit carbon, and one implicit hydrogen, and three implicit hydrogens.
     *
     * @cdk.bug 1655045
     */
    @Test
    public void testHydrogenCount_2() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainer molecule = new AtomContainer(); // methane
        molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class, Elements.CARBON));
        molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class, Elements.HYDROGEN));
        molecule.getAtom(0).setImplicitHydrogenCount(3);
        molecule.addBond(0, 1, Order.SINGLE);
        CMLWriter cmlWriter = new CMLWriter(writer);

        cmlWriter.write(molecule);
        cmlWriter.close();
        logger.debug("****************************** testHydrogenCount_2()");
        logger.debug(writer.toString());
        logger.debug("******************************");
        Assert.assertTrue(writer.toString().indexOf("hydrogenCount=\"4\"") != -1);
    }

    @Test
    public void testCMLCrystal() throws Exception {
        StringWriter writer = new StringWriter();
        ICrystal crystal = new Crystal();
        IAtom silicon = new Atom("Si");
        silicon.setFractionalPoint3d(new Point3d(0.0, 0.0, 0.0));
        crystal.addAtom(silicon);
        crystal.setA(new Vector3d(1.5, 0.0, 0.0));
        crystal.setB(new Vector3d(0.0, 2.0, 0.0));
        crystal.setC(new Vector3d(0.0, 0.0, 1.5));
        CMLWriter cmlWriter = new CMLWriter(writer);

        cmlWriter.write(crystal);
        cmlWriter.close();
        String cmlContent = writer.toString();
        logger.debug("****************************** testCMLCrystal()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        Assert.assertTrue(cmlContent.indexOf("</crystal>") != -1); // the cystal info has to be present
        Assert.assertTrue(cmlContent.indexOf("<atom") != -1); // an Atom has to be present
    }

    @Test
    public void testReactionCustomization() throws Exception {
        StringWriter writer = new StringWriter();
        IReaction reaction = new Reaction();
        reaction.setID("reaction1");
        IAtomContainer reactant = reaction.getBuilder().newInstance(IAtomContainer.class);
        reactant.setID("react");
        IAtomContainer product = reaction.getBuilder().newInstance(IAtomContainer.class);
        product.setID("product");
        IAtomContainer agent = reaction.getBuilder().newInstance(IAtomContainer.class);
        agent.setID("agent");

        reaction.addReactant(reactant);
        reaction.addProduct(product);
        reaction.addAgent(agent);

        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(reaction);
        cmlWriter.close();
        String cmlContent = writer.toString();
        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        Assert.assertTrue(cmlContent.indexOf("<reaction id=\"reaction1") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"react") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"product") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"agent") != -1);
    }

    @Test
    public void testReactionScheme1() throws Exception {
        StringWriter writer = new StringWriter();
        IReactionScheme scheme1 = DefaultChemObjectBuilder.getInstance().newInstance(IReactionScheme.class);
        scheme1.setID("rs0");
        IReactionScheme scheme2 = scheme1.getBuilder().newInstance(IReactionScheme.class);
        scheme2.setID("rs1");
        scheme1.add(scheme2);

        IReaction reaction = scheme1.getBuilder().newInstance(IReaction.class);
        reaction.setID("r1");
        IAtomContainer moleculeA = reaction.getBuilder().newInstance(IAtomContainer.class);
        moleculeA.setID("A");
        IAtomContainer moleculeB = reaction.getBuilder().newInstance(IAtomContainer.class);
        moleculeB.setID("B");
        reaction.addReactant(moleculeA);
        reaction.addProduct(moleculeB);

        scheme2.addReaction(reaction);

        IReaction reaction2 = reaction.getBuilder().newInstance(IReaction.class);
        reaction2.setID("r2");
        IAtomContainer moleculeC = reaction.getBuilder().newInstance(IAtomContainer.class);
        moleculeC.setID("C");
        reaction2.addReactant(moleculeB);
        reaction2.addProduct(moleculeC);

        scheme1.addReaction(reaction2);

        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(scheme1);
        cmlWriter.close();
        String cmlContent = writer.toString();
        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        Assert.assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs0") != -1);
        Assert.assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs1") != -1);
        Assert.assertTrue(cmlContent.indexOf("<reaction id=\"r1") != -1);
        Assert.assertTrue(cmlContent.indexOf("<reaction id=\"r2") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"A") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"B") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"C") != -1);
    }

    @Test
    public void testReactionScheme2() throws Exception {
        StringWriter writer = new StringWriter();
        ReactionScheme scheme1 = new ReactionScheme();
        scheme1.setID("rs0");

        IReaction reaction = DefaultChemObjectBuilder.getInstance().newInstance(IReaction.class);
        reaction.setID("r1");
        IAtomContainer moleculeA = reaction.getBuilder().newInstance(IAtomContainer.class);
        moleculeA.setID("A");
        IAtomContainer moleculeB = reaction.getBuilder().newInstance(IAtomContainer.class);
        moleculeB.setID("B");
        reaction.addReactant(moleculeA);
        reaction.addProduct(moleculeB);

        scheme1.addReaction(reaction);

        IReaction reaction2 = reaction.getBuilder().newInstance(IReaction.class);
        reaction2.setID("r2");
        IAtomContainer moleculeC = reaction.getBuilder().newInstance(IAtomContainer.class);
        moleculeC.setID("C");
        reaction2.addReactant(moleculeB);
        reaction2.addProduct(moleculeC);

        scheme1.addReaction(reaction2);

        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(scheme1);
        cmlWriter.close();
        String cmlContent = writer.toString();
        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        Assert.assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs0") != -1);
        Assert.assertTrue(cmlContent.indexOf("<reaction id=\"r1") != -1);
        Assert.assertTrue(cmlContent.indexOf("<reaction id=\"r2") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"A") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"B") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"C") != -1);
    }

    @Test
    public void testReactionSchemeWithFormula() throws Exception {
        StringWriter writer = new StringWriter();
        ReactionScheme scheme1 = new ReactionScheme();
        scheme1.setID("rs0");

        IReaction reaction = DefaultChemObjectBuilder.getInstance().newInstance(IReaction.class);
        reaction.setID("r1");
        IAtomContainer moleculeA = reaction.getBuilder().newInstance(IAtomContainer.class);
        moleculeA.setID("A");
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(reaction.getBuilder().newInstance(IIsotope.class, "C"), 10);
        formula.addIsotope(reaction.getBuilder().newInstance(IIsotope.class, "H"), 15);
        formula.addIsotope(reaction.getBuilder().newInstance(IIsotope.class, "N"), 2);
        formula.addIsotope(reaction.getBuilder().newInstance(IIsotope.class, "O"), 1);
        moleculeA.setProperty(CDKConstants.FORMULA, formula);
        IAtomContainer moleculeB = reaction.getBuilder().newInstance(IAtomContainer.class);
        moleculeB.setID("B");
        reaction.addReactant(moleculeA);
        reaction.addProduct(moleculeB);

        scheme1.addReaction(reaction);

        IReaction reaction2 = reaction.getBuilder().newInstance(IReaction.class);
        reaction2.setID("r2");
        IAtomContainer moleculeC = reaction.getBuilder().newInstance(IAtomContainer.class);
        moleculeC.setID("C");
        reaction2.addReactant(moleculeB);
        reaction2.addProduct(moleculeC);

        scheme1.addReaction(reaction2);

        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(scheme1);
        cmlWriter.close();
        String cmlContent = writer.toString();

        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        Assert.assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs0") != -1);
        Assert.assertTrue(cmlContent.indexOf("<reaction id=\"r1") != -1);
        Assert.assertTrue(cmlContent.indexOf("<reaction id=\"r2") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"A") != -1);
        Assert.assertTrue(cmlContent.indexOf("<formula concise=") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"B") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"C") != -1);
    }

    @Test
    public void testReactionSchemeWithFormula2() throws Exception {
        StringWriter writer = new StringWriter();
        ReactionScheme scheme1 = new ReactionScheme();
        scheme1.setID("rs0");

        IReaction reaction = DefaultChemObjectBuilder.getInstance().newInstance(IReaction.class);
        reaction.setID("r1");
        IAtomContainer moleculeA = reaction.getBuilder().newInstance(IAtomContainer.class);
        moleculeA.setID("A");
        moleculeA.setProperty(CDKConstants.FORMULA, "C 10 H 15 N 2 O 1");
        IAtomContainer moleculeB = reaction.getBuilder().newInstance(IAtomContainer.class);
        moleculeB.setID("B");
        reaction.addReactant(moleculeA);
        reaction.addProduct(moleculeB);

        scheme1.addReaction(reaction);

        IReaction reaction2 = reaction.getBuilder().newInstance(IReaction.class);
        reaction2.setID("r2");
        IAtomContainer moleculeC = reaction.getBuilder().newInstance(IAtomContainer.class);
        moleculeC.setID("C");
        reaction2.addReactant(moleculeB);
        reaction2.addProduct(moleculeC);

        scheme1.addReaction(reaction2);

        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(scheme1);
        cmlWriter.close();
        String cmlContent = writer.toString();
        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        Assert.assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs0") != -1);
        Assert.assertTrue(cmlContent.indexOf("<reaction id=\"r1") != -1);
        Assert.assertTrue(cmlContent.indexOf("<reaction id=\"r2") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"A") != -1);
        Assert.assertTrue(cmlContent.indexOf("<scalar dictRef=\"cdk:molecularProperty") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"B") != -1);
        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"C") != -1);
    }

    @Test
    public void testChemModeID() throws Exception {
        StringWriter writer = new StringWriter();
        IChemModel chemModel = new ChemModel();
        chemModel.setID("cm0");

        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(chemModel);
        cmlWriter.close();
        String cmlContent = writer.toString();
        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        Assert.assertTrue(cmlContent.indexOf("<list convention=\"cdk:model\" id=\"cm0") != -1);
    }

    @Test
    public void testMoleculeSetID() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainerSet moleculeSet = new AtomContainerSet();
        moleculeSet.setID("ms0");

        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(moleculeSet);
        cmlWriter.close();
        String cmlContent = writer.toString();
        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        Assert.assertTrue(cmlContent.indexOf("<moleculeList convention=\"cdk:moleculeSet\" id=\"ms0") != -1);
    }

    @Test
    public void testReactionProperty() throws Exception {
        StringWriter writer = new StringWriter();
        IReaction reaction = DefaultChemObjectBuilder.getInstance().newInstance(IReaction.class);
        reaction.setID("r1");
        reaction.setProperty("blabla", "blabla2");
        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(reaction);
        cmlWriter.close();
        String cmlContent = writer.toString();
        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        Assert.assertTrue(cmlContent.indexOf("<scalar dictRef=\"cdk:reactionProperty") != -1);
    }

    /**
     * TODO: introduce concept for ReactionStepList and ReactionStep.
     */
    //    @Test public void testReactionStepList() throws Exception {
    //    	StringWriter writer = new StringWriter();
    //    	ReactionChain chain = new ReactionChain();
    //    	chain.setID("rsl1");
    //
    //
    //        IReaction reaction = DefaultChemObjectBuilder.getInstance().newInstance(IReaction.class);
    //        reaction.setID("r1");
    //        IAtomContainer moleculeA = reaction.getNewBuilder().newInstance(IAtomContainer.class);
    //        moleculeA.setID("A");
    //        IAtomContainer moleculeB = reaction.getNewBuilder().newInstance(IAtomContainer.class);
    //        moleculeB.setID("B");
    //        reaction.addReactant(moleculeA);
    //        reaction.addProduct(moleculeB);
    //
    //        chain.addReaction(reaction);
    //
    //        IReaction reaction2 = reaction.getNewBuilder().newInstance(IReaction.class);
    //        reaction2.setID("r2");
    //        IAtomContainer moleculeC = reaction.getNewBuilder().newInstance(IAtomContainer.class);
    //        moleculeC.setID("C");
    //        reaction2.addReactant(moleculeB);
    //        reaction2.addProduct(moleculeC);
    //
    //        chain.addReaction(reaction2);
    //
    //        CMLWriter cmlWriter = new CMLWriter(writer);
    //        cmlWriter.write(chain);
    //        String cmlContent = writer.toString();
    //        logger.debug("****************************** testReactionCustomization()");
    //        logger.debug(cmlContent);
    //        logger.debug("******************************");
    //        Assert.assertTrue(cmlContent.indexOf("<reactionStepList id=\"rsl1") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<reaction id=\"r1") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<reaction id=\"r2") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"A") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"B") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"C") != -1);
    //    }
    //
    //    @Test public void testReactionSchemeStepList1() throws Exception {
    //    	StringWriter writer = new StringWriter();
    //    	ReactionScheme scheme1 = new ReactionScheme();
    //        scheme1.setID("rs0");
    //        ReactionScheme scheme2 = new ReactionScheme();
    //        scheme2.setID("rs1");
    //        scheme1.add(scheme2);
    //
    //
    //        IReaction reaction1 = DefaultChemObjectBuilder.getInstance().newInstance(IReaction.class);
    //        reaction1.setID("r1.1");
    //        IAtomContainer moleculeA = reaction1.getNewBuilder().newInstance(IAtomContainer.class);
    //        moleculeA.setID("A");
    //        IAtomContainer moleculeB = reaction1.getNewBuilder().newInstance(IAtomContainer.class);
    //        moleculeB.setID("B");
    //        reaction1.addReactant(moleculeA);
    //        reaction1.addProduct(moleculeB);
    //
    //        scheme2.addReaction(reaction1);
    //
    //        IReaction reaction2 = reaction1.getNewBuilder().newInstance(IReaction.class);
    //        reaction2.setID("r1.2");
    //        IAtomContainer moleculeC = reaction1.getNewBuilder().newInstance(IAtomContainer.class);
    //        moleculeC.setID("C");
    //        reaction2.addReactant(moleculeB);
    //        reaction2.addProduct(moleculeC);
    //
    //        scheme2.addReaction(reaction2);
    //
    //        ReactionChain chain = new ReactionChain();
    //    	chain.setID("rsl1");
    //
    //        IReaction reaction3 = reaction1.getNewBuilder().newInstance(IReaction.class);
    //        reaction3.setID("r2.1");
    //        IAtomContainer moleculeD = reaction1.getNewBuilder().newInstance(IAtomContainer.class);
    //        moleculeD.setID("D");
    //        reaction3.addReactant(moleculeA);
    //        reaction3.addProduct(moleculeD);
    //
    //        chain.addReaction(reaction3,0);
    //
    //        IReaction reaction4 = reaction1.getNewBuilder().newInstance(IReaction.class);
    //        reaction4.setID("r2.2");
    //        IAtomContainer moleculeE = reaction1.getNewBuilder().newInstance(IAtomContainer.class);
    //        moleculeE.setID("E");
    //        reaction4.addReactant(moleculeD);
    //        reaction4.addProduct(moleculeE);
    //
    //        chain.addReaction(reaction4,1);
    //
    ////        scheme1.add((IReactionSet)chain);
    //
    //        CMLWriter cmlWriter = new CMLWriter(writer);
    //        cmlWriter.write(scheme1);
    //        String cmlContent = writer.toString();
    //        logger.debug("****************************** testReactionCustomization()");
    //        logger.debug(cmlContent);
    //        logger.debug("******************************");
    //        Assert.assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs0") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs1") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<reaction id=\"r1") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<reaction id=\"r2") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"A") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"B") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"C") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<reactionStepList id=\"rsl1") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"D") != -1);
    //        Assert.assertTrue(cmlContent.indexOf("<molecule id=\"E") != -1);
    //    }

    @Test
    public void writeIsClosed() throws IOException {
        Writer mock = Mockito.mock(Writer.class);
        new CMLWriter(mock).close();
        Mockito.verify(mock).close();
    }
}
