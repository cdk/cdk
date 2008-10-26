/* $Revision$ $Author$ $Date$
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
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.reaction.type.AdductionProtonLPReaction;

/**
 * Tests for ReactionEngine implementations.
 *
 * @cdk.module test-reaction
 */
public abstract class ReactionEngineTest extends CDKTestCase {
	
	/**
	 *  Constructor for the ReactionEngineTest object.
	 */
	public ReactionEngineTest(){
        super();
	}
	/**
	 * Junit test.
	 * 
	 * @throws Exception 
	 */
	@Test public void testReactionEngine() throws Exception {
		ReactionEngine engine = new AdductionProtonLPReaction();
		Assert.assertNotNull(engine);
	}
	/**
	 * Junit test.
	 * 
	 * @throws Exception 
	 */
	@Test public void testGetParameters() throws Exception {
		ReactionEngine engine = new AdductionProtonLPReaction();
		Assert.assertNotNull(engine.getParameters());
	}
	
	/**
	 * Junit test.
	 * 
	 * @throws Exception 
	 */
	@Test public void testSetParameters_HashMap() throws Exception {
		ReactionEngine engine = new AdductionProtonLPReaction();
		engine.setParameters(engine.getParameters());
	}
}
