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
package org.openscience.cdk.test.tools.manipulator;

import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * @cdk.module test-standard
 *
 * @author     Egon Willighagen
 * @cdk.created    2003-07-23
 */
public class ReactionManipulatorTest extends CDKTestCase {

	public ReactionManipulatorTest(String name) {
		super(name);
	}

    public void setUp() {}

    public static Test suite() {
        TestSuite suite = new TestSuite(ReactionManipulatorTest.class);
        return suite;
	}

    public void testReverse() {
        Reaction reaction = new Reaction();
        reaction.setDirection(Reaction.BACKWARD);
        Molecule water = new Molecule();
        reaction.addReactant(water, 3.0);
        reaction.addReactant(new Molecule());
        reaction.addProduct(new Molecule());
        
        Reaction reversedReaction = (Reaction)ReactionManipulator.reverse(reaction);
        assertEquals(Reaction.FORWARD, reversedReaction.getDirection());
        assertEquals(2, reversedReaction.getProductCount());
        assertEquals(1, reversedReaction.getReactantCount());
        assertEquals(3.0, reversedReaction.getProductCoefficient(water), 0.00001);
    }
    
    public void testGetAllIDs() {
        Reaction reaction = new Reaction();
        reaction.setID("r1");
        Molecule water = new Molecule();
        water.setID("m1");
        Atom oxygen = new Atom("O");
        oxygen.setID("a1");
        water.addAtom(oxygen);
        reaction.addReactant(water);
        reaction.addProduct(water);
        
        Vector ids = ReactionManipulator.getAllIDs(reaction);
        assertNotNull(ids);
        assertEquals(5, ids.size());
    }
    /**
	 * A unit test suite for JUnit. Test of mapped IAtoms
	 *
	 * @return    The test suite
	 */
    public void testMappingAtoms()throws ClassNotFoundException, CDKException, java.lang.Exception {
    	IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
    	IMolecule reactant = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("[C+]-C=C");
    	IMolecule product = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C=C=C");
    	
    	IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactant.getAtom(0),product.getAtom(0));
        reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactant.getAtom(1),product.getAtom(1));
        reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactant.getAtom(2),product.getAtom(2));
        reaction.addMapping(mapping);
    	
        reaction.addReactant(reactant);
        reaction.addProduct(product);
        
        IAtom mappedAtom = (IAtom)ReactionManipulator.getMappedChemObject(reaction, reactant.getAtom(0));
        assertEquals(mappedAtom, product.getAtom(0));
        
        mappedAtom = (IAtom)ReactionManipulator.getMappedChemObject(reaction, product.getAtom(1));
        assertEquals(mappedAtom, reactant.getAtom(1));
        
        
    }
    /**
	 * A unit test suite for JUnit. Test of mapped IBond
	 *
	 * @return    The test suite
	 */
    public void testMappingBonds()throws ClassNotFoundException, CDKException, java.lang.Exception {
    	IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
    	IMolecule reactant = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("[C+]-C=C");
    	IMolecule product = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C=C=C");
    	
    	IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactant.getAtom(0),product.getAtom(0));
        reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactant.getBond(0),product.getBond(0));
        reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactant.getBond(1),product.getBond(1));
        reaction.addMapping(mapping);
    	
        reaction.addReactant(reactant);
        reaction.addProduct(product);
        
        IBond mappedBond = (IBond)ReactionManipulator.getMappedChemObject(reaction, reactant.getBond(0));
        assertEquals(mappedBond, product.getBond(0));
        
        mappedBond = (IBond)ReactionManipulator.getMappedChemObject(reaction, product.getBond(1));
        assertEquals(mappedBond, reactant.getBond(1));
        
        
    }
}

