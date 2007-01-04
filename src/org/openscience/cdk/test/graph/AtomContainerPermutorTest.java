/* $RCSfile$ 
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 * You should have received a copy of the GNU Lesserf General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.test.graph;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.graph.AtomContainerBondPermutor;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-extra
 */
public class AtomContainerPermutorTest extends CDKTestCase
{
	public AtomContainerPermutorTest(String name) {
		super(name);
	}

	public void setUp() {
		
	}

	public static Test suite() {
		return new TestSuite(AtomContainerPermutorTest.class);
	}

	public void testAtomPermutation() 
	{
		AtomContainer ac = new org.openscience.cdk.AtomContainer();
		AtomContainer result;
		String atoms = new String("");
		ac.addAtom(new Atom("C"));
		ac.addAtom(new Atom("N"));
		ac.addAtom(new Atom("P"));
		ac.addAtom(new Atom("O"));
		ac.addAtom(new Atom("S"));
		ac.addAtom(new Atom("Br"));
		ac.addBond(0, 1, 1.0);
		ac.addBond(1, 2, 1.0);
		ac.addBond(2, 3, 1.0);
		ac.addBond(3, 4, 1.0);
		ac.addBond(4, 5, 1.0);
		AtomContainerAtomPermutor acap = new
		AtomContainerAtomPermutor(ac);
		int counter = 0;
		while(acap.hasNext())
		{
			counter ++;
			atoms = "";
			result = (AtomContainer)acap.next();
			for (int f = 0; f < result.getAtomCount(); f++)
			{
				atoms += result.getAtom(f).getSymbol(); 
			}
		}
		assertEquals(719, counter);
	}

	public void testBondPermutation() 
	{
		AtomContainer ac = new org.openscience.cdk.AtomContainer();
		AtomContainer result;
		String bonds = new String("");
		ac.addAtom(new Atom("C"));
		ac.addAtom(new Atom("N"));
		ac.addAtom(new Atom("P"));
		ac.addAtom(new Atom("O"));
		ac.addAtom(new Atom("S"));
		ac.addAtom(new Atom("Br"));
		ac.addBond(0, 1, 1.0);
		ac.addBond(1, 2, 2.0);
		ac.addBond(2, 3, 3.0);
		ac.addBond(3, 4, 4.0);
		ac.addBond(4, 5, 5.0);
		AtomContainerBondPermutor acap = new
		AtomContainerBondPermutor(ac);
		int counter = 0;
		while(acap.hasNext())
		{
			counter ++;
			bonds = "";
			result = (AtomContainer)acap.next();
			for (int f = 0; f < result.getBondCount(); f++)
			{
				bonds += result.getBond(f).getOrder(); 
			}
			//logger.debug(bonds);
		}
		assertEquals(119, counter);
	}

	
	public static void main(String[] args)
	{
		AtomContainerPermutorTest acpt = new
		AtomContainerPermutorTest("AtomContainerPermutorTest");
		//acpt.testAtomPermutation();
		acpt.testBondPermutation();
	}
}

