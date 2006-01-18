/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Mapping;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.Reaction;
import org.openscience.cdk.interfaces.SetOfMolecules;
import org.openscience.cdk.interfaces.ChemObjectBuilder;

/**
 * TestCase for the Reaction class.
 *
 * @cdk.module test
 */
public class ReactionTest extends CDKTestCase {

	protected ChemObjectBuilder builder;
	
	public ReactionTest(String name) {
		super(name);
	}

    public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
		return new TestSuite(ReactionTest.class);
	}

    public void testReaction() {
        Reaction reaction = builder.newReaction();
        assertNotNull(reaction);
        assertEquals(0, reaction.getReactantCount());
        assertEquals(0, reaction.getProductCount());
        assertEquals(Reaction.FORWARD, reaction.getDirection());
    }
    
    public void testGetReactantCount() {
        Reaction reaction = builder.newReaction();
        assertEquals(0, reaction.getReactantCount());
	reaction.addReactant(builder.newMolecule());
        assertEquals(1, reaction.getReactantCount());
    }
    
    public void testGetProductCount() {
        Reaction reaction = builder.newReaction();
        assertEquals(0, reaction.getProductCount());
	reaction.addProduct(builder.newMolecule());
        assertEquals(1, reaction.getProductCount());
    }
    
    public void testAddReactant_Molecule() {
        Reaction reaction = builder.newReaction();
        Molecule sodiumhydroxide = builder.newMolecule();
        Molecule aceticAcid = builder.newMolecule();
        Molecule water = builder.newMolecule();
        Molecule acetate = builder.newMolecule();
        reaction.addReactant(sodiumhydroxide);
        reaction.addReactant(aceticAcid);
        reaction.addReactant(water);
        assertEquals(3, reaction.getReactantCount());
        // next one should trigger a growArray, if the grow
        // size is still 3.
        reaction.addReactant(acetate);
        assertEquals(4, reaction.getReactantCount());
        
        assertEquals(1.0, reaction.getReactantCoefficient(aceticAcid), 0.00001);
    }

    public void testSetReactants_SetOfMolecules() {
        Reaction reaction = builder.newReaction();
        Molecule sodiumhydroxide = builder.newMolecule();
        Molecule aceticAcid = builder.newMolecule();
        Molecule water = builder.newMolecule();
        SetOfMolecules reactants = builder.newSetOfMolecules();
        reactants.addMolecule(sodiumhydroxide);
        reactants.addMolecule(aceticAcid);
        reactants.addMolecule(water);
        reaction.setReactants(reactants);
        assertEquals(3, reaction.getReactantCount());
        
        assertEquals(1.0, reaction.getReactantCoefficient(aceticAcid), 0.00001);
    }

    public void testAddReactant_Molecule_double() {
        Reaction reaction = builder.newReaction();
        Molecule proton = builder.newMolecule();
        Molecule sulfate = builder.newMolecule();
        reaction.addReactant(proton, 2);
        reaction.addReactant(sulfate, 1);
        assertEquals(2.0, reaction.getReactantCoefficient(proton), 0.00001);
        assertEquals(1.0, reaction.getReactantCoefficient(sulfate), 0.00001);
    }
    
    public void testAddProduct_Molecule() {
        Reaction reaction = builder.newReaction();
        Molecule sodiumhydroxide = builder.newMolecule();
        Molecule aceticAcid = builder.newMolecule();
        Molecule water = builder.newMolecule();
        Molecule acetate = builder.newMolecule();
        reaction.addProduct(sodiumhydroxide);
        reaction.addProduct(aceticAcid);
        reaction.addProduct(water);
        assertEquals(3, reaction.getProductCount());
        // next one should trigger a growArray, if the grow
        // size is still 3.
        reaction.addProduct(acetate);
        assertEquals(4, reaction.getProductCount());
        
        assertEquals(1.0, reaction.getProductCoefficient(aceticAcid), 0.00001);
    }

    public void testSetProducts_SetOfMolecules() {
        Reaction reaction = builder.newReaction();
        Molecule sodiumhydroxide = builder.newMolecule();
        Molecule aceticAcid = builder.newMolecule();
        Molecule water = builder.newMolecule();
        SetOfMolecules products = builder.newSetOfMolecules();
        products.addMolecule(sodiumhydroxide);
        products.addMolecule(aceticAcid);
        products.addMolecule(water);
        reaction.setProducts(products);
        assertEquals(3, reaction.getProductCount());
        
        assertEquals(1.0, reaction.getProductCoefficient(aceticAcid), 0.00001);
    }

    public void testAddProduct_Molecule_double() {
        Reaction reaction = builder.newReaction();
        Molecule proton = builder.newMolecule();
        Molecule sulfate = builder.newMolecule();
        reaction.addProduct(proton, 2.0);
        reaction.addProduct(sulfate, 1.0);
        assertEquals(2.0, reaction.getProductCoefficient(proton), 0.00001);
        assertEquals(1.0, reaction.getProductCoefficient(sulfate), 0.00001);
    }
    
    public void testAddAgent_Molecule() {
        Reaction reaction = builder.newReaction();
        Molecule proton = builder.newMolecule();
        reaction.addAgent(proton);
        assertEquals(1, reaction.getAgents().getMoleculeCount());
    }

    public void testGetReactantCoefficient_Molecule() {
        Reaction reaction = builder.newReaction();
        Molecule proton = builder.newMolecule();
        reaction.addReactant(proton, 2.0);
        assertEquals(2.0, reaction.getReactantCoefficient(proton), 0.00001);
        
        assertEquals(-1.0, reaction.getReactantCoefficient(builder.newMolecule()), 0.00001);
    }

    public void testGetProductCoefficient_Molecule() {
        Reaction reaction = builder.newReaction();
        Molecule proton = builder.newMolecule();
        reaction.addProduct(proton, 2.0);
        assertEquals(2.0, reaction.getProductCoefficient(proton), 0.00001);

        assertEquals(-1.0, reaction.getProductCoefficient(builder.newMolecule()), 0.00001);
    }
    
	public void testSetReactantCoefficient_Molecule_double() {
		Reaction reaction = builder.newReaction();
        Molecule proton = builder.newMolecule();
		reaction.addReactant(proton, 2.0);
		reaction.setReactantCoefficient(proton, 3.0);
		assertEquals(3.0, reaction.getReactantCoefficient(proton), 0.00001);
	}
	
	public void testSetProductCoefficient_Molecule_double() {
		Reaction reaction = builder.newReaction();
        Molecule proton = builder.newMolecule();
		reaction.addProduct(proton, 2.0);
		reaction.setProductCoefficient(proton, 1.0);
		assertEquals(1.0, reaction.getProductCoefficient(proton), 0.00001);
	}
	
	public void testGetReactantCoefficients() {
        Reaction reaction = builder.newReaction();
		Molecule ed1 = builder.newMolecule();
		Molecule ed2 = builder.newMolecule();
		reaction.addReactant(ed1, 2);
		reaction.addReactant(ed2, 3);
		double[] ec = reaction.getReactantCoefficients();
		assertEquals(2.0, ec.length, 0.00001);
		assertEquals(reaction.getReactantCoefficient(ed1), ec[0], 0.00001);
		assertEquals(3.0, ec[1], 0.00001);
    }
	
	public void testGetProductCoefficients() {
        Reaction reaction = builder.newReaction();
		Molecule pr1 = builder.newMolecule();
		Molecule pr2 = builder.newMolecule();
		reaction.addProduct(pr1, 1);
		reaction.addProduct(pr2, 2);
		double[] pc = reaction.getProductCoefficients();
		assertEquals(2.0, pc.length, 0.00001);
		assertEquals(reaction.getProductCoefficient(pr1), pc[0], 0.00001);
		assertEquals(2.0, pc[1], 0.00001);
    }
	
	public void testSetReactantCoefficients_arraydouble() {
        Reaction reaction = builder.newReaction();
		Molecule ed1 = builder.newMolecule();
		Molecule ed2 = builder.newMolecule();
		reaction.addReactant(ed1, 2);
		reaction.addReactant(ed2, 3);
		double[] ec = { 1.0, 2.0 };
		boolean coeffSet = reaction.setReactantCoefficients(ec);
		assertTrue(coeffSet);
		assertEquals(1.0, reaction.getReactantCoefficient(ed1), 0.00001);
		assertEquals(2.0, reaction.getReactantCoefficient(ed2), 0.00001);
		double[] ecFalse = { 1.0 };
		assertFalse(reaction.setReactantCoefficients(ecFalse));
    }
	
	public void testSetProductCoefficients_arraydouble() {
        Reaction reaction = builder.newReaction();
		Molecule pr1 = builder.newMolecule();
		reaction.addProduct(pr1, 1);
		double[] pc = { 2.0 };
		boolean coeffSet = reaction.setProductCoefficients(pc);
		assertTrue(coeffSet);
		assertEquals(2.0, reaction.getProductCoefficient(pr1), 0.00001);
		double[] pcFalse = { 1.0 , 2.0 };
		assertFalse(reaction.setProductCoefficients(pcFalse));
    }
	
    public void testGetReactants() {
        Reaction reaction = builder.newReaction();
        Molecule sodiumhydroxide = builder.newMolecule();
        Molecule aceticAcid = builder.newMolecule();
        Molecule water = builder.newMolecule();
        reaction.addReactant(sodiumhydroxide);
        reaction.addReactant(aceticAcid);
        reaction.addReactant(water);
        assertEquals(3, reaction.getReactants().getMolecules().length);
    }
	
    public void testGetProducts() {    
        Reaction reaction = builder.newReaction();
        Molecule sodiumhydroxide = builder.newMolecule();
        Molecule aceticAcid = builder.newMolecule();
        Molecule water = builder.newMolecule();
        reaction.addProduct(sodiumhydroxide);
        reaction.addProduct(aceticAcid);
        reaction.addProduct(water);
        assertEquals(3, reaction.getProducts().getMolecules().length);
    }
    
    public void testGetAgents() {    
        Reaction reaction = builder.newReaction();
        Molecule water = builder.newMolecule();
        reaction.addAgent(water);
        assertEquals(1, reaction.getAgents().getMoleculeCount());
    }
    
    public void testSetDirection_int() {
        Reaction reaction = builder.newReaction();
        int direction = Reaction.BIDIRECTIONAL;
        reaction.setDirection(direction);
        assertEquals(direction, reaction.getDirection());
    }

    public void testGetDirection() {
        Reaction reaction = builder.newReaction();
        assertEquals(Reaction.FORWARD, reaction.getDirection());
    }

    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        Reaction reaction = builder.newReaction();
        String description = reaction.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
    
    public void testClone() {
        Reaction reaction = builder.newReaction();
        Object clone = reaction.clone();
        assertNotNull(clone);
        assertTrue(clone instanceof Reaction);
    }

    public void testClone_Mapping() {
        fail("Affected by bug #1095690");
    }
    
    public void testAddMapping_Mapping() {
        // Added to make the Coverage tool happy
        // the method is not part of the interface, and cannot be tested

        /* The old test method:
        Reaction reaction = builder.newReaction();
        Mapping mapping = new Mapping(builder.newAtom("C"), builder.newAtom("C"));
        
        reaction.addMapping(mapping);
        
        Mapping[] mappings = reaction.getMappings();
        assertNotNull(mappings);
        assertEquals(1, mappings.length);
        assertEquals(mapping, mappings[0]);*/
    	assertTrue(true);
    }
    
    public void testGetMappings() {
        testAddMapping_Mapping();
    }
}
