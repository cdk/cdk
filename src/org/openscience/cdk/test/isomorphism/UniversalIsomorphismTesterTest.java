/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CKD) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.test.isomorphism;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * @cdkPackage test
 */
public class UniversalIsomorphismTesterTest extends TestCase
{
	
	boolean standAlone = false;
	
	public UniversalIsomorphismTesterTest(String name)
	{
		super(name);
	}

	
	public static Test suite() {
		return new TestSuite(UniversalIsomorphismTesterTest.class);
	}

	public void test1() throws java.lang.Exception
	{
		AtomContainer mol = MoleculeFactory.makeAlphaPinene();
		AtomContainer frag1 = MoleculeFactory.makeCyclohexene(); //one double bond in ring
		HueckelAromaticityDetector.detectAromaticity(mol);
		HueckelAromaticityDetector.detectAromaticity(frag1);
		
		if(standAlone) {
			System.out.println("Cyclohexene is a subgraph of alpha-Pinen: " + UniversalIsomorphismTester.isSubgraph(mol, frag1));
        } else {
            assertTrue(UniversalIsomorphismTester.isSubgraph(mol, frag1));
        }
	}

	public void test2() throws java.lang.Exception
	{
		AtomContainer mol = MoleculeFactory.makeAlphaPinene();
		AtomContainer frag1 = MoleculeFactory.makeCyclohexane(); // no double bond in ring		
		HueckelAromaticityDetector.detectAromaticity(mol);
		HueckelAromaticityDetector.detectAromaticity(frag1);
        
		if(standAlone){
			System.out.println("Cyclohexane is a subgraph of alpha-Pinen: " + UniversalIsomorphismTester.isSubgraph(mol, frag1));
		} else {
            assertTrue(!UniversalIsomorphismTester.isSubgraph(mol, frag1));
        }
	}

	public void test3() throws java.lang.Exception
	{
		AtomContainer mol = MoleculeFactory.makeIndole();
		AtomContainer frag1 = MoleculeFactory.makePyrrole(); 
		HueckelAromaticityDetector.detectAromaticity(mol);
		HueckelAromaticityDetector.detectAromaticity(frag1);

		if(standAlone) {
			System.out.println("Pyrrole is a subgraph of Indole: " + UniversalIsomorphismTester.isSubgraph(mol, frag1));
        } else {
            assertTrue(UniversalIsomorphismTester.isSubgraph(mol, frag1));
        }
	}
	
	
	
	public static void main(String[] args)
	{
		try{
			UniversalIsomorphismTesterTest uitt = new UniversalIsomorphismTesterTest("UniversalIsomorphismTesterTest");
			uitt.standAlone = true;
			uitt.test1();
			uitt.test2();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
}

