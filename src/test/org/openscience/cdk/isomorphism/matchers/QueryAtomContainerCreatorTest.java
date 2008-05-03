/* $Revision$
 * $Author$    
 * $Date$    
 * 
 * Copyright (C) 2008 Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.isomorphism.matchers;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

/**
 * Checks the functionality of the <code>QueryAtomContainerCreator</code>.
 *
 * @cdk.module test-extra
 */
public class QueryAtomContainerCreatorTest extends NewCDKTestCase {
	
	@BeforeClass public static void setUp() {}
	
	/**
	 * @cdk.inchi InChI=1/C8H10/c1-7-5-3-4-6-8(7)2/h3-6H,1-2H3
	 */
    @Test public void test12DimethylBenzene() throws Exception {
    	IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
    	IMolecule molecule = builder.newMolecule();
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addBond(0,1,IBond.Order.SINGLE);
    	molecule.addBond(1,2,IBond.Order.DOUBLE);
    	molecule.addBond(2,3,IBond.Order.SINGLE);
    	molecule.addBond(3,4,IBond.Order.DOUBLE);
    	molecule.addBond(4,5,IBond.Order.SINGLE);
    	molecule.addBond(5,0,IBond.Order.DOUBLE);
    	molecule.addBond(0,6,IBond.Order.SINGLE);
    	molecule.addBond(1,7,IBond.Order.SINGLE);
    	
    	// 2,3-dimethyl-1,3-butadiene matches
    	IMolecule query1 = builder.newMolecule();
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addBond(0,1,IBond.Order.SINGLE);
    	molecule.addBond(1,2,IBond.Order.DOUBLE);
    	molecule.addBond(3,0,IBond.Order.DOUBLE);
    	molecule.addBond(0,4,IBond.Order.SINGLE);
    	molecule.addBond(1,5,IBond.Order.SINGLE);
    	QueryAtomContainer queryContainer1 = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(query1);
    	Assert.assertTrue(UniversalIsomorphismTester.isSubgraph(molecule, queryContainer1));
    	
    	// 2,3-dimethyl-2-butene does not match
    	IMolecule query2 = builder.newMolecule();
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addAtom(builder.newAtom("C"));
    	molecule.addBond(0,1,IBond.Order.DOUBLE);
    	molecule.addBond(1,2,IBond.Order.SINGLE);
    	molecule.addBond(3,0,IBond.Order.SINGLE);
    	molecule.addBond(0,4,IBond.Order.SINGLE);
    	molecule.addBond(1,5,IBond.Order.SINGLE);
    	QueryAtomContainer queryContainer2 = QueryAtomContainerCreator.createSymbolAndBondOrderQueryContainer(query2);
    	Assert.assertFalse(UniversalIsomorphismTester.isSubgraph(molecule, queryContainer2));
    }
	
}
