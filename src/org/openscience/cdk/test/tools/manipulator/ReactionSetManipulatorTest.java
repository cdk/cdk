/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulator;

/**
 * @cdk.module test-standard
 */
public class ReactionSetManipulatorTest extends CDKTestCase {
    
    private IChemObjectBuilder builder;
    
    public ReactionSetManipulatorTest(String name) {
        super(name);
    }
    
    public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
		return new TestSuite(ReactionSetManipulatorTest.class);
	}

	public void testGetAllMolecules_IReactionSet() {
		IReactionSet reactionSet = builder.newReactionSet();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		assertEquals(0, ReactionSetManipulator.getAllMolecules(reactionSet).getMoleculeCount());
		// FIXME: should test something with actual molecules in it
	}
	
}


