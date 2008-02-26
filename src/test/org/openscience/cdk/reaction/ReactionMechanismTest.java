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

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.reaction.IReactionMechanism;
import org.openscience.cdk.NewCDKTestCase;

/**
 * Tests for IReactionProcess implementations.
 *
 * @cdk.module test-reaction
 */
public abstract class ReactionMechanismTest extends NewCDKTestCase {
	
	protected static IReactionMechanism reactionMechanism;
	private final static IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();

	 /**
	  * Defining reaction mechanism.
	  * 
	  * @param descriptorClass
	  * @throws Exception
	  */
	public static void setReaction(Class<?> descriptorClass) throws Exception {
		if (ReactionMechanismTest.reactionMechanism == null) {
			Object descriptor = (Object)descriptorClass.newInstance();
			if (!(descriptor instanceof IReactionMechanism)) {
				throw new CDKException("The passed reaction class must be a IReactionMechanism");
			}
			ReactionMechanismTest.reactionMechanism = (IReactionMechanism)descriptor;
		}
	}
	/**
	 * Checks if the initiate method is consistent.
	 * 
	 * @throws Exception 
	 */
	@Test public void testInitiate_IMolecule_ArrayList_ArrayList() throws Exception {
		ArrayList<IAtom> atomList = new ArrayList<IAtom>(); 
		ArrayList<IBond> bondList = new ArrayList<IBond>(); 
		IReaction reaction = reactionMechanism.initiate(builder.newMoleculeSet(), atomList, bondList);
        Assert.assertNotNull(reaction);
	}
}
