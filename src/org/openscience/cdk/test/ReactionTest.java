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

import junit.framework.*;
import org.openscience.cdk.*;

/**
 * TestCase for the Monomer class.
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
        
        assertEquals(1, reaction.getReactantCoefficient(aceticAcid));
    }

    public void testAddReactant_int() {
        Reaction reaction = new Reaction();
        Molecule proton = new Molecule();
        Molecule sulfate = new Molecule();
        reaction.addReactant(proton, 2);
        reaction.addReactant(sulfate, 1);
        assertEquals(2, reaction.getReactantCoefficient(proton));
        assertEquals(1, reaction.getReactantCoefficient(sulfate));
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
        
        assertEquals(1, reaction.getProductCoefficient(aceticAcid));
    }

    public void testAddProduct_int() {
        Reaction reaction = new Reaction();
        Molecule proton = new Molecule();
        Molecule sulfate = new Molecule();
        reaction.addProduct(proton, 2);
        reaction.addProduct(sulfate, 1);
        assertEquals(2, reaction.getProductCoefficient(proton));
        assertEquals(1, reaction.getProductCoefficient(sulfate));
    }
    
    public void testGetReactantCoefficient() {
        Reaction reaction = new Reaction();
        Molecule proton = new Molecule();
        reaction.addReactant(proton, 2);
        assertEquals(2, reaction.getReactantCoefficient(proton));
        
        assertEquals(-1, reaction.getReactantCoefficient(new Molecule()));
    }

    public void testGetProductCoefficient() {
        Reaction reaction = new Reaction();
        Molecule proton = new Molecule();
        reaction.addProduct(proton, 2);
        assertEquals(2, reaction.getProductCoefficient(proton));

        assertEquals(-1, reaction.getProductCoefficient(new Molecule()));
    }
}
