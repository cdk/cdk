/*
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.graph.invariant;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.graph.invariant.EquivalentClassPartitioner;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of the TopologicalEquivalentClass.
 *
 * @cdk.module test-extra
 */
public class EquivalentClassPartitionerTest extends CDKTestCase
{
	AtomContainer C40C3V = null;
	AtomContainer C24D6D = null;
	AtomContainer  C28TD = null;
	public EquivalentClassPartitionerTest(String name)
	{
		super(name);
		
		
	}
	
	
	
	public static Test suite() {
		return new TestSuite(EquivalentClassPartitionerTest.class);
	}

	public void testEquivalent() throws Exception 
	{
		AtomContainer C40C3V = new org.openscience.cdk.AtomContainer();
		C40C3V.addAtom(new Atom("C")); // 1
		C40C3V.addAtom(new Atom("C")); // 2
		C40C3V.addAtom(new Atom("C")); // 3
		C40C3V.addAtom(new Atom("C")); // 4
		C40C3V.addAtom(new Atom("C")); // 5
		C40C3V.addAtom(new Atom("C")); // 6
		C40C3V.addAtom(new Atom("C")); // 7
		C40C3V.addAtom(new Atom("C")); // 8
		C40C3V.addAtom(new Atom("C")); // 9 
		C40C3V.addAtom(new Atom("C")); // 10
		C40C3V.addAtom(new Atom("C")); // 11
		C40C3V.addAtom(new Atom("C")); // 12
		C40C3V.addAtom(new Atom("C")); // 13
		C40C3V.addAtom(new Atom("C")); // 14
		C40C3V.addAtom(new Atom("C")); // 15
		C40C3V.addAtom(new Atom("C")); // 16
		C40C3V.addAtom(new Atom("C")); // 17
		C40C3V.addAtom(new Atom("C")); // 18
		C40C3V.addAtom(new Atom("C")); // 19 
		C40C3V.addAtom(new Atom("C")); // 20
		C40C3V.addAtom(new Atom("C")); // 21
		C40C3V.addAtom(new Atom("C")); // 22
		C40C3V.addAtom(new Atom("C")); // 23
		C40C3V.addAtom(new Atom("C")); // 24
		C40C3V.addAtom(new Atom("C")); // 25
		C40C3V.addAtom(new Atom("C")); // 26
		C40C3V.addAtom(new Atom("C")); // 27
		C40C3V.addAtom(new Atom("C")); // 28
		C40C3V.addAtom(new Atom("C")); // 29
		C40C3V.addAtom(new Atom("C")); // 30
		C40C3V.addAtom(new Atom("C")); // 31
		C40C3V.addAtom(new Atom("C")); // 32
		C40C3V.addAtom(new Atom("C")); // 33
		C40C3V.addAtom(new Atom("C")); // 34
		C40C3V.addAtom(new Atom("C")); // 35
		C40C3V.addAtom(new Atom("C")); // 36
		C40C3V.addAtom(new Atom("C")); // 37
		C40C3V.addAtom(new Atom("C")); // 38
		C40C3V.addAtom(new Atom("C")); // 39
		C40C3V.addAtom(new Atom("C")); // 40

		C40C3V.addBond(0, 1, 1); // 1
		C40C3V.addBond(0, 5, 1); // 2
		C40C3V.addBond(0, 8, 1); // 3
		C40C3V.addBond(1, 2, 1); // 4
		C40C3V.addBond(1, 25, 1); // 5
		C40C3V.addBond(2, 3, 1); // 6
		C40C3V.addBond(2, 6, 1); // 7
		C40C3V.addBond(3, 4, 1); // 8
		C40C3V.addBond(3, 24, 1); // 9
		C40C3V.addBond(4, 7, 1); // 10
		C40C3V.addBond(4, 8, 1); // 11
		C40C3V.addBond(5, 21, 1); // 12
		C40C3V.addBond(5, 28, 1); // 13
		C40C3V.addBond(6, 22, 1); // 14
		C40C3V.addBond(6, 27, 1); // 15
		C40C3V.addBond(7, 20, 1); // 16
		C40C3V.addBond(7, 23, 1); // 17
		C40C3V.addBond(8, 26, 1); // 18
		C40C3V.addBond(9, 12, 1); // 19
		C40C3V.addBond(9, 37, 1); // 20
		C40C3V.addBond(9, 39, 1); // 21
		C40C3V.addBond(10, 14, 1); // 22
		C40C3V.addBond(10, 38, 1); // 23
		C40C3V.addBond(10, 39, 1); // 24
		C40C3V.addBond(11, 13, 1); // 25
		C40C3V.addBond(11, 36, 1); // 26
		C40C3V.addBond(11, 39, 1); // 27
		C40C3V.addBond(12, 35, 1); // 28
		C40C3V.addBond(12, 38, 1); // 29
		C40C3V.addBond(13, 34, 1); // 30
		C40C3V.addBond(13, 37, 1); // 31
		C40C3V.addBond(14, 33, 1); // 32
		C40C3V.addBond(14, 36, 1); // 33
		C40C3V.addBond(15, 29, 1); // 34
		C40C3V.addBond(15, 17, 1); // 35
		C40C3V.addBond(15, 37, 1); // 36
		C40C3V.addBond(16, 19, 1); // 37
		C40C3V.addBond(16, 30, 1); // 38
		C40C3V.addBond(16, 36, 1); // 39
		C40C3V.addBond(17, 20, 1); // 40
		C40C3V.addBond(17, 35, 1); // 41
		C40C3V.addBond(18, 22, 1); // 42
		C40C3V.addBond(18, 32, 1); // 43
		C40C3V.addBond(18, 33, 1); // 44
		C40C3V.addBond(19, 28, 1); // 45
		C40C3V.addBond(19, 34, 1); // 46
		C40C3V.addBond(20, 26, 1); // 47
		C40C3V.addBond(21, 26, 1); // 48
		C40C3V.addBond(21, 29, 1); // 49
		C40C3V.addBond(22, 24, 1); // 50
		C40C3V.addBond(23, 24, 1); // 51
		C40C3V.addBond(23, 31, 1); // 52
		C40C3V.addBond(25, 27, 1); // 53
		C40C3V.addBond(25, 28, 1); // 54
		C40C3V.addBond(27, 30, 1); // 55
		C40C3V.addBond(29, 34, 1); // 56
		C40C3V.addBond(30, 33, 1); // 57
		C40C3V.addBond(31, 32, 1); // 58
		C40C3V.addBond(31, 35, 1); // 59
		C40C3V.addBond(32, 38, 1); // 60
		EquivalentClassPartitioner it = new EquivalentClassPartitioner(C40C3V);
		int equivalentClass[]=it.getTopoEquivClassbyHuXu(C40C3V);
		char[] arrEquivalent=new char[39];
		for(int i=1;i<equivalentClass.length-1;i++)
			arrEquivalent[i-1]=Integer.toString(equivalentClass[i]).charAt(0);
		String strEquivalent=new String(arrEquivalent);
		assertNotNull(equivalentClass);
		assertTrue(equivalentClass[0]==10);//number of Class
		assertTrue(equivalentClass[40]==10);
		assertTrue(strEquivalent.equals("111112221333444556667878222879995555444"));
	}

	public void testFullereneC24D6D() throws Exception
	{
		AtomContainer C24D6D = new org.openscience.cdk.AtomContainer();
		C24D6D.addAtom(new Atom("C")); // 1
		C24D6D.addAtom(new Atom("C")); // 2
		C24D6D.addAtom(new Atom("C")); // 3
		C24D6D.addAtom(new Atom("C")); // 4
		C24D6D.addAtom(new Atom("C")); // 5
		C24D6D.addAtom(new Atom("C")); // 6
		C24D6D.addAtom(new Atom("C")); // 7
		C24D6D.addAtom(new Atom("C")); // 8
		C24D6D.addAtom(new Atom("C")); // 9 
		C24D6D.addAtom(new Atom("C")); // 10
		C24D6D.addAtom(new Atom("C")); // 11
		C24D6D.addAtom(new Atom("C")); // 12
		C24D6D.addAtom(new Atom("C")); // 13
		C24D6D.addAtom(new Atom("C")); // 14
		C24D6D.addAtom(new Atom("C")); // 15
		C24D6D.addAtom(new Atom("C")); // 16
		C24D6D.addAtom(new Atom("C")); // 17
		C24D6D.addAtom(new Atom("C")); // 18
		C24D6D.addAtom(new Atom("C")); // 19 
		C24D6D.addAtom(new Atom("C")); // 20
		C24D6D.addAtom(new Atom("C")); // 21
		C24D6D.addAtom(new Atom("C")); // 22
		C24D6D.addAtom(new Atom("C")); // 23
		C24D6D.addAtom(new Atom("C")); // 24


		C24D6D.addBond(0, 1, 1); // 1
		C24D6D.addBond(0, 5, 1); // 2
		C24D6D.addBond(0, 11, 1); // 3
		C24D6D.addBond(1, 2, 1); // 4
		C24D6D.addBond(1, 10, 1); // 5
		C24D6D.addBond(2, 3, 1); // 6
		C24D6D.addBond(2, 9, 1); // 7
		C24D6D.addBond(3, 4, 1); // 8
		C24D6D.addBond(3, 8, 1); // 9
		C24D6D.addBond(4, 5, 1); // 10
		C24D6D.addBond(4, 7, 1); // 11
		C24D6D.addBond(5, 6, 1); // 12
		C24D6D.addBond(6, 16, 1); // 13
		C24D6D.addBond(6, 17, 1); // 14
		C24D6D.addBond(7, 15, 1); // 15
		C24D6D.addBond(7, 16, 1); // 16
		C24D6D.addBond(8, 14, 1); // 17
		C24D6D.addBond(8, 15, 1); // 18
		C24D6D.addBond(9, 13, 1); // 19
		C24D6D.addBond(9, 14, 1); // 20
		C24D6D.addBond(10, 12, 1); // 21
		C24D6D.addBond(10, 13, 1); // 22
		C24D6D.addBond(11, 12, 1); // 23
		C24D6D.addBond(11, 17, 1); // 24
		C24D6D.addBond(12, 19, 1); // 25
		C24D6D.addBond(13, 20, 1); // 26
		C24D6D.addBond(14, 21, 1); // 27
		C24D6D.addBond(15, 22, 1); // 28
		C24D6D.addBond(16, 23, 1); // 29
		C24D6D.addBond(17, 18, 1); // 30
		C24D6D.addBond(18, 19, 1); // 31
		C24D6D.addBond(18, 23, 1); // 32
		C24D6D.addBond(19, 20, 1); // 33
		C24D6D.addBond(20, 21, 1); // 34
		C24D6D.addBond(21, 22, 1); // 35
		C24D6D.addBond(22, 23, 1); // 36

		EquivalentClassPartitioner it = new EquivalentClassPartitioner(C24D6D);
		int equivalentClass[]=it.getTopoEquivClassbyHuXu(C24D6D);
		char[] arrEquivalent=new char[24];
		for(int i=1;i<equivalentClass.length;i++)
			arrEquivalent[i-1]=Integer.toString(equivalentClass[i]).charAt(0);
		String strEquivalent=new String(arrEquivalent);
		assertNotNull(equivalentClass);
		assertTrue(equivalentClass[0]==2);//number of Class
		assertTrue(strEquivalent.equals("111111222222222222111111"));
	}
	
	public static void main(String[] args) throws Exception
	{
		EquivalentClassPartitionerTest tec = new EquivalentClassPartitionerTest("TopologicalEquivalentClassTest");
		tec.testEquivalent();
		tec.testFullereneC24D6D();
	}
}