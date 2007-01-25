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
package org.openscience.cdk.test.io.cml;

import java.io.StringWriter;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.protein.data.PDBAtom;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading CML 2 files using a few test files
 * in data/cmltest.
 *
 * @cdk.module test-io
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

	public void testCMLWriterBenzene() {
		StringWriter writer = new StringWriter();
        Molecule molecule = MoleculeFactory.makeBenzene();
		try {
			HueckelAromaticityDetector.detectAromaticity(molecule);
		} catch (Exception exception) {
            logger.error("Error while detecting aromaticity: ", exception.getMessage());
            logger.debug(exception);
			fail(exception.getMessage());
		}
        CMLWriter cmlWriter = new CMLWriter(writer);
        
        try {
            cmlWriter.write(molecule);
        } catch (Exception exception) {
            logger.error("Error while creating an CML2 file: ", exception.getMessage());
            logger.debug(exception);
            fail(exception.getMessage());
        }
		logger.debug("****************************** testCMLWriterBenzene()");
        logger.debug(writer.toString());
		logger.debug("******************************");
        assertTrue(writer.toString().indexOf("</molecule>") != -1);
	}
	
	public void testCMLCrystal() {
		StringWriter writer = new StringWriter();
        Crystal crystal = new Crystal();
        Atom silicon = new Atom("Si");
        silicon.setFractionalPoint3d(
        	new Point3d(0.0, 0.0, 0.0)
        );
        crystal.addAtom(silicon);
        crystal.setA(new Vector3d(1.5, 0.0, 0.0));
        crystal.setB(new Vector3d(0.0, 2.0, 0.0));
        crystal.setC(new Vector3d(0.0, 0.0, 1.5));
        CMLWriter cmlWriter = new CMLWriter(writer);
        
        try {
            cmlWriter.write(crystal);
        } catch (Exception exception) {
            logger.error("Error while creating an CML2 file: ", exception.getMessage());
            logger.debug(exception);
            fail(exception.getMessage());
        }
        String cmlContent = writer.toString();
		logger.debug("****************************** testCMLCrystal()");
        logger.debug(cmlContent);
		logger.debug("******************************");
        assertTrue(cmlContent.indexOf("</crystal>") != -1); // the cystal info has to be present
        assertTrue(cmlContent.indexOf("<atom") != -1); // an Atom has to be present
	}
	
    public void testQSARCustomization() {
        StringWriter writer = new StringWriter();
        Molecule molecule = MoleculeFactory.makeBenzene();
        IMolecularDescriptor descriptor = new WeightDescriptor();

        CMLWriter cmlWriter = new CMLWriter(writer);
        try {
            DescriptorValue value = descriptor.calculate(molecule);
            molecule.setProperty(value.getSpecification(), value);
        
            cmlWriter.write(molecule);
        } catch (Exception exception) {
            logger.error("Error while creating an CML2 file: ", exception.getMessage());
            logger.debug(exception);
            fail(exception.getMessage());
        }
        String cmlContent = writer.toString();
        logger.debug("****************************** testQSARCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        assertTrue(cmlContent.indexOf("<property") != -1 &&
        		   cmlContent.indexOf("xmlns:qsar") != -1);
        assertTrue(cmlContent.indexOf("#weight\"") != -1);
    }
    
    public void testReactionCustomization() {
    	StringWriter writer = new StringWriter();
        IReaction reaction = new Reaction();
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
        try {
            cmlWriter.write(reaction);
        } catch (Exception exception) {
            logger.error("Error while creating an CML2 file: ", exception.getMessage());
            logger.debug(exception);
            fail(exception.getMessage());
        }
        String cmlContent = writer.toString();
        logger.debug("****************************** testReactionCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        assertTrue(cmlContent.indexOf("<reaction id=\"reaction1") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"react") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"product") != -1);
        assertTrue(cmlContent.indexOf("<molecule id=\"agent") != -1);
    }
    
    public void testPDBAtomCustomization() {
        StringWriter writer = new StringWriter();
        Molecule molecule = new Molecule();
        PDBAtom atom = new PDBAtom("C");
        atom.setName("CA");
        atom.setResName("PHE");
        molecule.addAtom(atom);
        
        CMLWriter cmlWriter = new CMLWriter(writer);
        try {
            cmlWriter.write(molecule);
        } catch (Exception exception) {
            logger.error("Error while creating an CML2 file: ", exception.getMessage());
            logger.debug(exception);
            fail(exception.getMessage());
        }
        String cmlContent = writer.toString();
        logger.debug("****************************** testPDBAtomCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        assertTrue(cmlContent.indexOf("<scalar dictRef=\"pdb:resName") != -1);
    }
    
}
