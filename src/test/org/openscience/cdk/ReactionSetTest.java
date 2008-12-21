/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
 * 
 */
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IReactionSetTest;

/**
 * Checks the functionality of the ReactionSet class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.ReactionSet
 */
public class ReactionSetTest extends IReactionSetTest {

    @BeforeClass public static void setUp() {
       	setChemObject(new ReactionSet());
    }

    @Test public void testReactionSet() {
        IReactionSet reactionSet = new ReactionSet();
        Assert.assertNotNull(reactionSet);
    }
    
}
