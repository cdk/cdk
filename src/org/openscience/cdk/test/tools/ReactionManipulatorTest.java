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
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test.tools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.tools.ReactionManipulator;

/**
 * @cdkPackage test
 *
 * @author     Egon Willighagen
 * @created    2003-07-23
 */
public class ReactionManipulatorTest extends TestCase {

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
        
        Reaction reversedReaction = ReactionManipulator.reverse(reaction);
        assertEquals(Reaction.FORWARD, reversedReaction.getDirection());
        assertEquals(2, reversedReaction.getProductCount());
        assertEquals(1, reversedReaction.getReactantCount());
        assertEquals(3.0, reversedReaction.getProductCoefficient(water), 0.00001);
    }
}

