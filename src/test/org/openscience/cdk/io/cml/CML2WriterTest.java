/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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

import java.io.StringWriter;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.ReactionScheme;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.formula.IMolecularFormula;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionScheme;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.libio.cml.PDBAtomCustomizer;
import org.openscience.cdk.libio.cml.QSARCustomizer;
import org.openscience.cdk.nonotify.NNAtom;
import org.openscience.cdk.nonotify.NNCrystal;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.nonotify.NNPDBAtom;
import org.openscience.cdk.nonotify.NNReaction;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading CML 2 files using a few test files
 * in data/cmltest.
 *
 * @cdk.module test-libiocml
 * @cdk.require java1.5+
 */
public class CML2WriterTest extends CDKTestCase {

    private LoggingTool logger;

    public CML2WriterTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(CML2WriterTest.class);
    }

	public void testCMLWriterBenzene() throws Exception {
		StringWriter writer = new StringWriter();
        IMolecule molecule = MoleculeFactory.makeBenzene();
        CDKHueckelAromaticityDetector.detectAromaticity(molecule);
        CMLWriter cmlWriter = new CMLWriter(writer);
        
        cmlWriter.write(molecule);
		logger.debug("****************************** testCMLWriterBenzene()");
        logger.debug(writer.toString());
		logger.debug("******************************");
        assertTrue(writer.toString().indexOf("</molecule>") != -1);
	}
	
	/**
	 * Test example with one explicit carbon, and four implicit hydrogens.
	 * 
	 * @cdk.bug 1655045
	 */
	public void testHydrogenCount() throws Exception {
		StringWriter writer = new StringWriter();
		IMolecule molecule = new NNMolecule(); // methane
		molecule.addAtom(molecule.getBuilder().newAtom(Elements.CARBON));
		molecule.getAtom(0).setHydrogenCount(4);
        CMLWriter cmlWriter = new CMLWriter(writer);
        
        cmlWriter.write(molecule);
		logger.debug("****************************** testHydrogenCount()");
        logger.debug(writer.toString());
		logger.debug("******************************");
        assertTrue(writer.toString().indexOf("hydrogenCount=\"4\"") != -1);
	}
	
	/**
	 * Test example with one explicit carbon, and one implicit hydrogen, and three implicit hydrogens.
	 * 
	 * @cdk.bug 1655045
	 */
	public void testHydrogenCount_2() throws Exception {
		StringWriter writer = new StringWriter();
		IMolecule molecule = new NNMolecule(); // methane
		molecule.addAtom(molecule.getBuilder().newAtom(Elements.CARBON));
		molecule.addAtom(molecule.getBuilder().newAtom(Elements.HYDROGEN));
		molecule.getAtom(0).setHydrogenCount(3);
		molecule.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        CMLWriter cmlWriter = new CMLWriter(writer);
        
        cmlWriter.write(molecule);
		logger.debug("****************************** testHydrogenCount_2()");
        logger.debug(writer.toString());
		logger.debug("******************************");
        assertTrue(writer.toString().indexOf("hydrogenCount=\"4\"") != -1);
	}
	
	public void testCMLCrystal() throws Exception {
		StringWriter writer = new StringWriter();
        ICrystal crystal = new NNCrystal();
        IAtom silicon = new NNAtom("Si");
        silicon.setFractionalPoint3d(
        	new Point3d(0.0, 0.0, 0.0)
        );
        crystal.addAtom(silicon);
        crystal.setA(new Vector3d(1.5, 0.0, 0.0));
        crystal.setB(new Vector3d(0.0, 2.0, 0.0));
        crystal.setC(new Vector3d(0.0, 0.0, 1.5));
        CMLWriter cmlWriter = new CMLWriter(writer);
        
        cmlWriter.write(crystal);
        String cmlContent = writer.toString();
		logger.debug("****************************** testCMLCrystal()");
        logger.debug(cmlContent);
		logger.debug("******************************");
        assertTrue(cmlContent.indexOf("</crystal>") != -1); // the cystal info has to be present
        assertTrue(cmlContent.indexOf("<atom") != -1); // an Atom has to be present
	}
	
    public void testQSARCustomization() throws Exception {
        StringWriter writer = new StringWriter();
        IMolecule molecule = MoleculeFactory.makeBenzene();
        IMolecularDescriptor descriptor = new WeightDescriptor();

        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.registerCustomizer(new QSARCustomizer());
        DescriptorValue value = descriptor.calculate(molecule);
        molecule.setProperty(value.getSpecification(), value);

        cmlWriter.write(molecule);
        String cmlContent = writer.toString();
        logger.debug("****************************** testQSARCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        assertTrue(cmlContent.indexOf("<property") != -1 &&
        		   cmlContent.indexOf("xmlns:qsar") != -1);
        assertTrue(cmlContent.indexOf("#weight\"") != -1);
    }
    
    public void testReactionCustomization() throws Exception {
    	StringWriter writer = new StringWriter();
        IReaction reaction = new NNReaction();
        reaction.setID("reaction1");
        IMolecule reactant = reaction.getBuilder().newMolecule();
        reactant.setID("react");
        IMolecule product = reaction.getBuilder().newMolecule();
        product.setID("product");
        IMolecule agent = reaction.getBuilder().newMolecule();
        agent.setID("agent");
        
        reaction.addReactant(reactant);
        reaction.addProduct(product);
        reaction.addAgent(agent);
        
        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(reaction);
        String cmlContent = writer.toString();
        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        assertTrue(cmlContent.indexOf("<reaction id=\"reaction1") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"react") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"product") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"agent") != -1);
    }
    
    public void testPDBAtomCustomization() throws Exception {
        StringWriter writer = new StringWriter();
        IMolecule molecule = new NNMolecule();
        IPDBAtom atom = new NNPDBAtom("C");
        atom.setName("CA");
        atom.setResName("PHE");
        molecule.addAtom(atom);
        
        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.registerCustomizer(new PDBAtomCustomizer());
        cmlWriter.write(molecule);
        String cmlContent = writer.toString();
        logger.debug("****************************** testPDBAtomCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        assertTrue(cmlContent.indexOf("<scalar dictRef=\"pdb:resName") != -1);
    }
    
    public void testReactionScheme1() throws Exception {
    	StringWriter writer = new StringWriter();
    	IReactionScheme scheme1 = DefaultChemObjectBuilder.getInstance().newReactionScheme();
        scheme1.setID("rs0");
        IReactionScheme scheme2 = scheme1.getBuilder().newReactionScheme();
        scheme2.setID("rs1");
        scheme1.add(scheme2);
        
        IReaction reaction = scheme1.getBuilder().newReaction();
        reaction.setID("r1");
        IMolecule moleculeA = reaction.getBuilder().newMolecule();
        moleculeA.setID("A");
        IMolecule moleculeB = reaction.getBuilder().newMolecule();
        moleculeB.setID("B");
        reaction.addReactant(moleculeA);
        reaction.addProduct(moleculeB);
        
        scheme2.addReaction(reaction);
        
        IReaction reaction2 = reaction.getBuilder().newReaction();
        reaction2.setID("r2");
        IMolecule moleculeC = reaction.getBuilder().newMolecule();
        moleculeC.setID("C");
        reaction2.addReactant(moleculeB);
        reaction2.addProduct(moleculeC);
        
        scheme1.addReaction(reaction2);
                
        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(scheme1);
        String cmlContent = writer.toString();
        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs0") != -1);
        assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs1") != -1);
        assertTrue(cmlContent.indexOf("<reaction id=\"r1") != -1);
        assertTrue(cmlContent.indexOf("<reaction id=\"r2") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"A") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"B") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"C") != -1);
    }
    
    public void testReactionScheme2() throws Exception {
    	StringWriter writer = new StringWriter();
    	ReactionScheme scheme1 = new ReactionScheme();
        scheme1.setID("rs0");
        
        
        IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
        reaction.setID("r1");
        IMolecule moleculeA = reaction.getBuilder().newMolecule();
        moleculeA.setID("A");
        IMolecule moleculeB = reaction.getBuilder().newMolecule();
        moleculeB.setID("B");
        reaction.addReactant(moleculeA);
        reaction.addProduct(moleculeB);
        
        scheme1.addReaction(reaction);
        
        IReaction reaction2 = reaction.getBuilder().newReaction();
        reaction2.setID("r2");
        IMolecule moleculeC = reaction.getBuilder().newMolecule();
        moleculeC.setID("C");
        reaction2.addReactant(moleculeB);
        reaction2.addProduct(moleculeC);
        
        scheme1.addReaction(reaction2);
        
        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(scheme1);
        String cmlContent = writer.toString();
        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs0") != -1);
        assertTrue(cmlContent.indexOf("<reaction id=\"r1") != -1);
        assertTrue(cmlContent.indexOf("<reaction id=\"r2") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"A") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"B") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"C") != -1);
    }
    public void testReactionSchemeWithFormula() throws Exception {
    	StringWriter writer = new StringWriter();
    	ReactionScheme scheme1 = new ReactionScheme();
        scheme1.setID("rs0");
        
        
        IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
        reaction.setID("r1");
        IMolecule moleculeA = reaction.getBuilder().newMolecule();
        moleculeA.setID("A");
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(reaction.getBuilder().newIsotope("C"), 10);
        formula.addIsotope(reaction.getBuilder().newIsotope("H"), 15);
        formula.addIsotope(reaction.getBuilder().newIsotope("N"), 2);
        formula.addIsotope(reaction.getBuilder().newIsotope("O"), 1);
        moleculeA.setProperty(CDKConstants.FORMULA,formula);
        IMolecule moleculeB = reaction.getBuilder().newMolecule();
        moleculeB.setID("B");
        reaction.addReactant(moleculeA);
        reaction.addProduct(moleculeB);
        
        scheme1.addReaction(reaction);
        
        IReaction reaction2 = reaction.getBuilder().newReaction();
        reaction2.setID("r2");
        IMolecule moleculeC = reaction.getBuilder().newMolecule();
        moleculeC.setID("C");
        reaction2.addReactant(moleculeB);
        reaction2.addProduct(moleculeC);
        
        scheme1.addReaction(reaction2);
        
        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(scheme1);
        String cmlContent = writer.toString();
        
        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs0") != -1);
        assertTrue(cmlContent.indexOf("<reaction id=\"r1") != -1);
        assertTrue(cmlContent.indexOf("<reaction id=\"r2") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"A") != -1);
        assertTrue(cmlContent.indexOf("<formula concise=") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"B") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"C") != -1);
    }

    public void testReactionSchemeWithFormula2() throws Exception {
    	StringWriter writer = new StringWriter();
    	ReactionScheme scheme1 = new ReactionScheme();
        scheme1.setID("rs0");
        
        
        IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
        reaction.setID("r1");
        IMolecule moleculeA = reaction.getBuilder().newMolecule();
        moleculeA.setID("A");
        moleculeA.setProperty(CDKConstants.FORMULA,"C 10 H 15 N 2 O 1");
        IMolecule moleculeB = reaction.getBuilder().newMolecule();
        moleculeB.setID("B");
        reaction.addReactant(moleculeA);
        reaction.addProduct(moleculeB);
        
        scheme1.addReaction(reaction);
        
        IReaction reaction2 = reaction.getBuilder().newReaction();
        reaction2.setID("r2");
        IMolecule moleculeC = reaction.getBuilder().newMolecule();
        moleculeC.setID("C");
        reaction2.addReactant(moleculeB);
        reaction2.addProduct(moleculeC);
        
        scheme1.addReaction(reaction2);
        
        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(scheme1);
        String cmlContent = writer.toString();
        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs0") != -1);
        assertTrue(cmlContent.indexOf("<reaction id=\"r1") != -1);
        assertTrue(cmlContent.indexOf("<reaction id=\"r2") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"A") != -1);
        assertTrue(cmlContent.indexOf("<scalar dictRef=\"cdk:molecularProperty") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"B") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"C") != -1);
    }
    /**
     * TODO: introduce concept for ReactionStepList and ReactionStep.
     */
//    public void testReactionStepList() throws Exception {
//    	StringWriter writer = new StringWriter();
//    	ReactionChain chain = new ReactionChain();
//    	chain.setID("rsl1");
//        
//        
//        IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
//        reaction.setID("r1");
//        IMolecule moleculeA = reaction.getBuilder().newMolecule();
//        moleculeA.setID("A");
//        IMolecule moleculeB = reaction.getBuilder().newMolecule();
//        moleculeB.setID("B");
//        reaction.addReactant(moleculeA);
//        reaction.addProduct(moleculeB);
//        
//        chain.addReaction(reaction);
//        
//        IReaction reaction2 = reaction.getBuilder().newReaction();
//        reaction2.setID("r2");
//        IMolecule moleculeC = reaction.getBuilder().newMolecule();
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
//        assertTrue(cmlContent.indexOf("<reactionStepList id=\"rsl1") != -1);
//        assertTrue(cmlContent.indexOf("<reaction id=\"r1") != -1);
//        assertTrue(cmlContent.indexOf("<reaction id=\"r2") != -1);
//        assertTrue(cmlContent.indexOf("<molecule id=\"A") != -1);
//        assertTrue(cmlContent.indexOf("<molecule id=\"B") != -1);
//        assertTrue(cmlContent.indexOf("<molecule id=\"C") != -1);
//    }
//
//    public void testReactionSchemeStepList1() throws Exception {
//    	StringWriter writer = new StringWriter();
//    	ReactionScheme scheme1 = new ReactionScheme();
//        scheme1.setID("rs0");
//        ReactionScheme scheme2 = new ReactionScheme();
//        scheme2.setID("rs1");
//        scheme1.add(scheme2);
//        
//        
//        IReaction reaction1 = DefaultChemObjectBuilder.getInstance().newReaction();
//        reaction1.setID("r1.1");
//        IMolecule moleculeA = reaction1.getBuilder().newMolecule();
//        moleculeA.setID("A");
//        IMolecule moleculeB = reaction1.getBuilder().newMolecule();
//        moleculeB.setID("B");
//        reaction1.addReactant(moleculeA);
//        reaction1.addProduct(moleculeB);
//        
//        scheme2.addReaction(reaction1);
//        
//        IReaction reaction2 = reaction1.getBuilder().newReaction();
//        reaction2.setID("r1.2");
//        IMolecule moleculeC = reaction1.getBuilder().newMolecule();
//        moleculeC.setID("C");
//        reaction2.addReactant(moleculeB);
//        reaction2.addProduct(moleculeC);
//        
//        scheme2.addReaction(reaction2);
//        
//        ReactionChain chain = new ReactionChain();
//    	chain.setID("rsl1");
//        
//        IReaction reaction3 = reaction1.getBuilder().newReaction();
//        reaction3.setID("r2.1");
//        IMolecule moleculeD = reaction1.getBuilder().newMolecule();
//        moleculeD.setID("D");
//        reaction3.addReactant(moleculeA);
//        reaction3.addProduct(moleculeD);
//        
//        chain.addReaction(reaction3,0);
//        
//        IReaction reaction4 = reaction1.getBuilder().newReaction();
//        reaction4.setID("r2.2");
//        IMolecule moleculeE = reaction1.getBuilder().newMolecule();
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
//        assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs0") != -1);
//        assertTrue(cmlContent.indexOf("<reactionScheme id=\"rs1") != -1);
//        assertTrue(cmlContent.indexOf("<reaction id=\"r1") != -1);
//        assertTrue(cmlContent.indexOf("<reaction id=\"r2") != -1);
//        assertTrue(cmlContent.indexOf("<molecule id=\"A") != -1);
//        assertTrue(cmlContent.indexOf("<molecule id=\"B") != -1);
//        assertTrue(cmlContent.indexOf("<molecule id=\"C") != -1);
//        assertTrue(cmlContent.indexOf("<reactionStepList id=\"rsl1") != -1);
//        assertTrue(cmlContent.indexOf("<molecule id=\"D") != -1);
//        assertTrue(cmlContent.indexOf("<molecule id=\"E") != -1);
//    }
}