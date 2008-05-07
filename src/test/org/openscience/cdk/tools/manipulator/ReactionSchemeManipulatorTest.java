/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
 * 
 * Copyright (C) 2008  Miguel Rojas <miguelrojasch@users.sf.net>
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
package org.openscience.cdk.tools.manipulator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionScheme;

/**
 * @cdk.module test-reaction
 */
public class ReactionSchemeManipulatorTest extends NewCDKTestCase {
    
    private IChemObjectBuilder builder;
    
    public ReactionSchemeManipulatorTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
       	builder = DefaultChemObjectBuilder.getInstance();
		
    }


    @Test public void testGetAllMolecules_IReactionScheme() {
		IReactionScheme reactionScheme = builder.newReactionScheme();
		IReaction reaction1 = builder.newReaction();
		reaction1.addProduct(builder.newMolecule());
		IReaction reaction2 = builder.newReaction();
		reaction2.addProduct(builder.newMolecule());
		reactionScheme.addReaction(reaction1); // 1
		reactionScheme.addReaction(reaction2); // 2
		
		Assert.assertEquals(2, ReactionSchemeManipulator.getAllMolecules(reactionScheme).getMoleculeCount());
		
	}
    
    @Test public void testGetAllMolecules_IReactionScheme_IMoleculeSet() {
		IReactionScheme reactionScheme = builder.newReactionScheme();
		IReaction reaction1 = builder.newReaction();
		reaction1.addProduct(builder.newMolecule());
		IReaction reaction2 = builder.newReaction();
		reaction2.addProduct(builder.newMolecule());
		reactionScheme.addReaction(reaction1); // 1
		reactionScheme.addReaction(reaction2); // 2
		
		Assert.assertEquals(2, ReactionSchemeManipulator.getAllMolecules(reactionScheme, builder.newMoleculeSet()).getMoleculeCount());
		
	}
    
    @Test public void testGetAllMolecules_IReactionScheme2() {
    	IReactionScheme reactionScheme = builder.newReactionScheme();
		IReaction reaction1 = builder.newReaction();
		IMolecule molecule = builder.newMolecule();
		reaction1.addProduct(molecule);
		reaction1.addReactant(builder.newMolecule());
		reactionScheme.addReaction(reaction1);
		IReaction reaction2 = builder.newReaction();
		reaction2.addProduct(builder.newMolecule());
		reaction2.addReactant(molecule);
		reactionScheme.addReaction(reaction2);
		
		Assert.assertEquals(3, ReactionSchemeManipulator.getAllMolecules(reactionScheme).getMoleculeCount());
		
	}
    
    @Test public void testGetAllMolecules_IReactionScheme3() {
		IReactionScheme scheme1 = builder.newReactionScheme();

		IReactionScheme scheme11 = builder.newReactionScheme();
		IReaction reaction1 = builder.newReaction();
		IMolecule molecule = builder.newMolecule();
		reaction1.addProduct(molecule);
		reaction1.addReactant(builder.newMolecule());
		scheme11.addReaction(reaction1);
		IReaction reaction2 = builder.newReaction();
		reaction2.addProduct(builder.newMolecule());
		reaction2.addReactant(molecule);
		scheme11.addReaction(reaction2);
		scheme1.add(scheme11);
		
		IReactionScheme scheme12 = builder.newReactionScheme();
		IReaction reaction3 = builder.newReaction();
		reaction3.addProduct(builder.newMolecule());
		reaction3.addReactant(molecule);
		scheme12.addReaction(reaction3);
		scheme1.add(scheme12);
		
		IReaction reaction11 = builder.newReaction();
		reaction11.addProduct(builder.newMolecule());
		scheme1.addReaction(reaction11);
		
		Assert.assertEquals(5, ReactionSchemeManipulator.getAllMolecules(scheme1).getMoleculeCount());
		
	}
    @Test public void testGetAllIDs_IReactionScheme() {
		IReactionScheme scheme1 = builder.newReactionScheme();
		scheme1.setID("scheme1");
		
		IReactionScheme scheme11 = builder.newReactionScheme();
		scheme11.setID("scheme11");
		IReaction reaction1 = builder.newReaction();
		reaction1.setID("reaction1");
		IMolecule molecule = builder.newMolecule();
		reaction1.setID("molecule");
		reaction1.addProduct(molecule);
		reaction1.addReactant(builder.newMolecule());
		scheme11.addReaction(reaction1);
		IReaction reaction2 = builder.newReaction();
		reaction1.setID("reaction2");
		reaction2.addProduct(builder.newMolecule());
		reaction2.addReactant(molecule);
		scheme11.addReaction(reaction2);
		scheme1.add(scheme11);
		
		IReactionScheme scheme12 = builder.newReactionScheme();
		scheme12.setID("scheme12");
		IReaction reaction3 = builder.newReaction();
		reaction3.setID("reaction3");
		reaction3.addProduct(builder.newMolecule());
		reaction3.addReactant(molecule);
		scheme12.addReaction(reaction3);
		scheme1.add(scheme12);
		
		IReaction reaction11 = builder.newReaction();
		reaction11.setID("reaction11");
		reaction11.addProduct(builder.newMolecule());
		scheme1.addReaction(reaction11);
		
		Assert.assertEquals(6, ReactionSchemeManipulator.getAllIDs(scheme1).size());
		
	}
}
