/*
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
 * 
 */
package org.openscience.cdk.test.graph.invariant;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.graph.invariant.*;
import java.util.*;
import junit.framework.*;

/**
 * Checks the funcitonality of the TopologicalEquivalentClass
 */
public class EquivalentClassPartitionerTest extends TestCase
{
	AtomContainer pinene_1 = null;
	Molecule pinene_2 = null, pinene_non = null;
	public EquivalentClassPartitionerTest(String name)
	{
		super(name);
		pinene_1 = new AtomContainer();
		pinene_1.addAtom(new Atom("C")); // 1
		pinene_1.addAtom(new Atom("C")); // 2
		pinene_1.addAtom(new Atom("C")); // 3
		pinene_1.addAtom(new Atom("C")); // 4
		pinene_1.addAtom(new Atom("C")); // 5
		pinene_1.addAtom(new Atom("C")); // 6
		pinene_1.addAtom(new Atom("C")); // 7
		pinene_1.addAtom(new Atom("C")); // 8
		pinene_1.addAtom(new Atom("C")); // 9 
		pinene_1.addAtom(new Atom("C")); // 10
		pinene_1.addAtom(new Atom("C")); // 11
		pinene_1.addAtom(new Atom("C")); // 12
		pinene_1.addAtom(new Atom("C")); // 13
		pinene_1.addAtom(new Atom("C")); // 14
		pinene_1.addAtom(new Atom("C")); // 15
		pinene_1.addAtom(new Atom("C")); // 16
		pinene_1.addAtom(new Atom("C")); // 17
		pinene_1.addAtom(new Atom("C")); // 18
		pinene_1.addAtom(new Atom("C")); // 19 
		pinene_1.addAtom(new Atom("C")); // 20
		pinene_1.addAtom(new Atom("C")); // 21
		pinene_1.addAtom(new Atom("C")); // 22
		pinene_1.addAtom(new Atom("C")); // 23
		pinene_1.addAtom(new Atom("C")); // 24
		pinene_1.addAtom(new Atom("C")); // 25
		pinene_1.addAtom(new Atom("C")); // 26
		pinene_1.addAtom(new Atom("C")); // 27
		pinene_1.addAtom(new Atom("C")); // 28
		pinene_1.addAtom(new Atom("C")); // 29
		pinene_1.addAtom(new Atom("C")); // 30
		pinene_1.addAtom(new Atom("C")); // 31
		pinene_1.addAtom(new Atom("C")); // 32
		pinene_1.addAtom(new Atom("C")); // 33
		pinene_1.addAtom(new Atom("C")); // 34
		pinene_1.addAtom(new Atom("C")); // 35
		pinene_1.addAtom(new Atom("C")); // 36
		pinene_1.addAtom(new Atom("C")); // 37
		pinene_1.addAtom(new Atom("C")); // 38
		pinene_1.addAtom(new Atom("C")); // 39
		pinene_1.addAtom(new Atom("C")); // 40
		
		pinene_1.addBond(0, 1, 1); // 1
		pinene_1.addBond(0, 5, 1); // 2
		pinene_1.addBond(0, 8, 1); // 3
		pinene_1.addBond(1, 2, 1); // 4
		pinene_1.addBond(1, 25, 1); // 5
		pinene_1.addBond(2, 3, 1); // 6
		pinene_1.addBond(2, 6, 1); // 7
		pinene_1.addBond(3, 4, 1); // 8
		pinene_1.addBond(3, 24, 1); // 9
		pinene_1.addBond(4, 7, 1); // 10
		pinene_1.addBond(4, 8, 1); // 11
		pinene_1.addBond(5, 21, 1); // 12
		pinene_1.addBond(5, 28, 1); // 13
		pinene_1.addBond(6, 22, 1); // 14
		pinene_1.addBond(6, 27, 1); // 15
		pinene_1.addBond(7, 20, 1); // 16
		pinene_1.addBond(7, 23, 1); // 17
		pinene_1.addBond(8, 26, 1); // 18
		pinene_1.addBond(9, 12, 1); // 19
		pinene_1.addBond(9, 37, 1); // 20
		pinene_1.addBond(9, 39, 1); // 21
		pinene_1.addBond(10, 14, 1); // 22
		pinene_1.addBond(10, 38, 1); // 23
		pinene_1.addBond(10, 39, 1); // 24
		pinene_1.addBond(11, 13, 1); // 25
		pinene_1.addBond(11, 36, 1); // 26
		pinene_1.addBond(11, 39, 1); // 27
		pinene_1.addBond(12, 35, 1); // 28
		pinene_1.addBond(12, 38, 1); // 29
		pinene_1.addBond(13, 34, 1); // 30
		pinene_1.addBond(13, 37, 1); // 31
		pinene_1.addBond(14, 33, 1); // 32
		pinene_1.addBond(14, 36, 1); // 33
		pinene_1.addBond(15, 29, 1); // 34
		pinene_1.addBond(15, 17, 1); // 35
		pinene_1.addBond(15, 37, 1); // 36
		pinene_1.addBond(16, 19, 1); // 37
		pinene_1.addBond(16, 30, 1); // 38
		pinene_1.addBond(16, 36, 1); // 39
		pinene_1.addBond(17, 20, 1); // 40
		pinene_1.addBond(17, 35, 1); // 41
		pinene_1.addBond(18, 22, 1); // 42
		pinene_1.addBond(18, 32, 1); // 43
		pinene_1.addBond(18, 33, 1); // 44
		pinene_1.addBond(19, 28, 1); // 45
		pinene_1.addBond(19, 34, 1); // 46
		pinene_1.addBond(20, 26, 1); // 47
		pinene_1.addBond(21, 26, 1); // 48
		pinene_1.addBond(21, 29, 1); // 49
		pinene_1.addBond(22, 24, 1); // 50
		pinene_1.addBond(23, 24, 1); // 51
		pinene_1.addBond(23, 31, 1); // 52
		pinene_1.addBond(25, 27, 1); // 53
		pinene_1.addBond(25, 28, 1); // 54
		pinene_1.addBond(27, 30, 1); // 55
		pinene_1.addBond(29, 34, 1); // 56
		pinene_1.addBond(30, 33, 1); // 57
		pinene_1.addBond(31, 32, 1); // 58
		pinene_1.addBond(31, 35, 1); // 59
		pinene_1.addBond(32, 38, 1); // 60
	}
	
	
	
	public static Test suite() {
		return new TestSuite(EquivalentClassPartitionerTest.class);
	}

	public void testEquivalent() 
	{
		try
		{
			long lBeforeTime=System.currentTimeMillis();
			EquivalentClassPartitioner it = new EquivalentClassPartitioner(pinene_1);
			int equivalentClass[]=it.getTopoEquivClassbyHuXu(pinene_1);
			long lAfterTime=System.currentTimeMillis();
			long lElapsedTime=lAfterTime-lBeforeTime;
			System.out.println("Time elapsed is "+lElapsedTime+ " milliseconds");
			for(int i=0;i<equivalentClass.length;i++)
				System.out.println("equivalentClass["+i+"]="+equivalentClass[i]);
		}
		catch(Exception exc)
		{
			System.err.println("An Exception");
			fail();
		}
	}
	
	
	public static void main(String[] args)
	{
		EquivalentClassPartitionerTest tec = new EquivalentClassPartitionerTest("TopologicalEquivalentClassTest");
		tec.testEquivalent();
	}
}