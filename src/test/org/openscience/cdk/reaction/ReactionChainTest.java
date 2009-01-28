/* $Revision: 8418 $ $Author: egonw $ $Date: 2007-06-25 22:05:44 +0200 (Mon, 25 Jun 2007) $
 * 
 * Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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
package org.openscience.cdk.reaction;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

/**
 * Tests for IReactionChain implementations.
 *
 * @cdk.module test-reaction
 */
public class ReactionChainTest extends NewCDKTestCase {
	
	private final static IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	
	/**
	 *  Constructor for the ReactionEngineTest object.
	 */
	public ReactionChainTest(){
        super();
	}
	
	/**
	 * Junit test.
	 * 
	 * @throws Exception 
	 */
	@Test public void testReactionChain(){
		IReactionSet chain = new ReactionChain();
		Assert.assertNotNull(chain);
	}
	/**
	 * Junit test.
	 * 
	 * @throws Exception 
	 */
	@Test public void testAddReaction_IReaction_int(){
		ReactionChain chain = new ReactionChain();
		IReaction reaction1 = builder.newReaction();
		reaction1.setID("reaction1");
		IReaction reaction2 = builder.newReaction();
		reaction1.setID("reaction2");
		IReaction reaction3 = builder.newReaction();
		reaction1.setID("reaction3");
		chain.addReaction(reaction1, 0);
		chain.addReaction(reaction2, 1);
		chain.addReaction(reaction3, 2);
		
		Assert.assertNotNull(chain);
		
	}
	/**
	 * Junit test.
	 * 
	 * @throws Exception 
	 */
	@Test public void testGetReactionStep_IReaction(){
		ReactionChain chain = new ReactionChain();
		IReaction reaction1 = builder.newReaction();
		reaction1.setID("reaction1");
		chain.addReaction(reaction1, 0);
		IReaction reaction2 = builder.newReaction();
		reaction1.setID("reaction2");
		IReaction reaction3 = builder.newReaction();
		reaction1.setID("reaction3");
		chain.addReaction(reaction1, 0);
		chain.addReaction(reaction2, 1);
		chain.addReaction(reaction3, 2);
		
		Assert.assertEquals(1,chain.getReactionStep(reaction2));
	}
	
	/**
	 * Junit test.
	 * 
	 * @throws Exception 
	 */
	@Test public void testGetReaction_int(){
		ReactionChain chain = new ReactionChain();
		IReaction reaction1 = builder.newReaction();
		reaction1.setID("reaction1");
		chain.addReaction(reaction1, 0);
		IReaction reaction2 = builder.newReaction();
		reaction1.setID("reaction2");
		IReaction reaction3 = builder.newReaction();
		reaction1.setID("reaction3");
		chain.addReaction(reaction1, 0);
		chain.addReaction(reaction2, 1);
		chain.addReaction(reaction3, 2);
		
		Assert.assertEquals(reaction2,chain.getReaction(1));

		
	}
}
