/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;

/**
 * TestCase for the Reaction class.
 *
 * @cdkPackage test
 *
 * @author  Edgar Luttman <edgar@uni-paderborn.de>
 * @created 2001-08-09
 */
public class ReactionTest extends TestCase {

	public ReactionTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ReactionTest.class);
	}

    public void testReaction() {
        Reaction reaction = new Reaction();
        assertNotNull(reaction);
        assertEquals(0, reaction.getReactantCount());
        assertEquals(0, reaction.getProductCount());
        assertEquals(Reaction.FORWARD, reaction.getDirection());
    }
    
    public void testAddReactant() {
        Reaction reaction = new Reaction();
        Molecule sodiumhydroxide = new Molecule();
        Molecule aceticAcid = new Molecule();
        Molecule water = new Molecule();
        Molecule acetate = new Molecule();
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

    public void testAddReactant_int() {
        Reaction reaction = new Reaction();
        Molecule proton = new Molecule();
        Molecule sulfate = new Molecule();
        reaction.addReactant(proton, 2);
        reaction.addReactant(sulfate, 1);
        assertEquals(2.0, reaction.getReactantCoefficient(proton), 0.00001);
        assertEquals(1.0, reaction.getReactantCoefficient(sulfate), 0.00001);
    }
    
    public void testAddProduct() {
        Reaction reaction = new Reaction();
        Molecule sodiumhydroxide = new Molecule();
        Molecule aceticAcid = new Molecule();
        Molecule water = new Molecule();
        Molecule acetate = new Molecule();
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

    public void testAddProduct_int() {
        Reaction reaction = new Reaction();
        Molecule proton = new Molecule();
        Molecule sulfate = new Molecule();
        reaction.addProduct(proton, 2.0);
        reaction.addProduct(sulfate, 1.0);
        assertEquals(2.0, reaction.getProductCoefficient(proton), 0.00001);
        assertEquals(1.0, reaction.getProductCoefficient(sulfate), 0.00001);
    }
    
    public void testGetReactantCoefficient() {
        Reaction reaction = new Reaction();
        Molecule proton = new Molecule();
        reaction.addReactant(proton, 2.0);
        assertEquals(2.0, reaction.getReactantCoefficient(proton), 0.00001);
        
        assertEquals(-1.0, reaction.getReactantCoefficient(new Molecule()), 0.00001);
    }

    public void testGetProductCoefficient() {
        Reaction reaction = new Reaction();
        Molecule proton = new Molecule();
        reaction.addProduct(proton, 2.0);
        assertEquals(2.0, reaction.getProductCoefficient(proton), 0.00001);

        assertEquals(-1.0, reaction.getProductCoefficient(new Molecule()), 0.00001);
    }
    
	public void testSetReactantCoefficient() {
		Reaction reaction = new Reaction();
        Molecule proton = new Molecule();
		reaction.addReactant(proton, 2.0);
		reaction.setReactantCoefficient(proton, 3.0);
		assertEquals(3.0, reaction.getReactantCoefficient(proton), 0.00001);
	}
	
	public void testSetProductCoefficient() {
		Reaction reaction = new Reaction();
        Molecule proton = new Molecule();
		reaction.addProduct(proton, 2.0);
		reaction.setProductCoefficient(proton, 1.0);
		assertEquals(1.0, reaction.getProductCoefficient(proton), 0.00001);
	}
	
	public void testGetReactantCoefficients() {
        Reaction reaction = new Reaction();
		Molecule ed1 = new Molecule();
		Molecule ed2 = new Molecule();
		reaction.addReactant(ed1, 2);
		reaction.addReactant(ed2, 3);
		double[] ec = reaction.getReactantCoefficients();
		assertEquals(2.0, ec.length, 0.00001);
		assertEquals(reaction.getReactantCoefficient(ed1), ec[0], 0.00001);
		assertEquals(3.0, ec[1], 0.00001);
    }
	
	public void testGetProductCoefficients() {
        Reaction reaction = new Reaction();
		Molecule pr1 = new Molecule();
		Molecule pr2 = new Molecule();
		reaction.addProduct(pr1, 1);
		reaction.addProduct(pr2, 2);
		double[] pc = reaction.getProductCoefficients();
		assertEquals(2.0, pc.length, 0.00001);
		assertEquals(reaction.getProductCoefficient(pr1), pc[0], 0.00001);
		assertEquals(2.0, pc[1], 0.00001);
    }
	
	public void testSetReactantCoefficients() {
        Reaction reaction = new Reaction();
		Molecule ed1 = new Molecule();
		Molecule ed2 = new Molecule();
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
	
	public void testSetProductCoefficients() {
        Reaction reaction = new Reaction();
		Molecule pr1 = new Molecule();
		reaction.addProduct(pr1, 1);
		double[] pc = { 2.0 };
		boolean coeffSet = reaction.setProductCoefficients(pc);
		assertTrue(coeffSet);
		assertEquals(2.0, reaction.getProductCoefficient(pr1), 0.00001);
		double[] pcFalse = { 1.0 , 2.0 };
		assertFalse(reaction.setProductCoefficients(pcFalse));
    }
	
    public void testGetReactants() {
        Reaction reaction = new Reaction();
        Molecule sodiumhydroxide = new Molecule();
        Molecule aceticAcid = new Molecule();
        Molecule water = new Molecule();
        reaction.addReactant(sodiumhydroxide);
        reaction.addReactant(aceticAcid);
        reaction.addReactant(water);
        assertEquals(3, reaction.getReactants().length);
    }
	
    public void testGetProducts() {
        Reaction reaction = new Reaction();
        Molecule sodiumhydroxide = new Molecule();
        Molecule aceticAcid = new Molecule();
        Molecule water = new Molecule();
        reaction.addProduct(sodiumhydroxide);
        reaction.addProduct(aceticAcid);
        reaction.addProduct(water);
        assertEquals(3, reaction.getProducts().length);
    }
    
    public void testSetDirection() {
        Reaction reaction = new Reaction();
        int direction = Reaction.BIDIRECTIONAL;
        reaction.setDirection(direction);
        assertEquals(direction, reaction.getDirection());
    }
}
