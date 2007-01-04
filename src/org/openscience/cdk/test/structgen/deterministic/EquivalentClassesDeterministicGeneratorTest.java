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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.test.structgen.deterministic;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.structgen.deterministic.EquivalentClassesDeterministicGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-extra
 */
public class EquivalentClassesDeterministicGeneratorTest extends CDKTestCase
{
	boolean standAlone = false;
	
	public EquivalentClassesDeterministicGeneratorTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(EquivalentClassesDeterministicGeneratorTest.class);
	}

	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	
	public void testEquivalentClassesDeterministicGenerator()
	{
		Molecule mol = MoleculeFactory.makeAlphaPinene();
		fixCarbonHCount(mol);
		EquivalentClassesDeterministicGenerator ecdg = new EquivalentClassesDeterministicGenerator();
		ecdg.setAtomContainer(mol);
		
		assertTrue(1==1);
	}

	private void fixCarbonHCount(Molecule mol)
	{	
		/* the following line are just a quick fix for this
		   particluar carbon-only molecule until we have a proper 
		   hydrogen count configurator
		 */
		double bondCount = 0;
		org.openscience.cdk.interfaces.IAtom atom;
		 for (int f = 0; f < mol.getAtomCount(); f++)
		{
			atom = mol.getAtom(f);
			bondCount =  mol.getBondOrderSum(atom);
			atom.setHydrogenCount(4 - (int)bondCount - (int)atom.getCharge());
			if (standAlone) System.out.println("Hydrogen count for atom " + f + ": " + atom.getHydrogenCount());
		}
	}
	
	public static void main(String[] args)
	{
		EquivalentClassesDeterministicGeneratorTest ecdgt = new EquivalentClassesDeterministicGeneratorTest("EquivalentClassesDeterministicGeneratorTest");
		ecdgt.setStandAlone(true);
		ecdgt.testEquivalentClassesDeterministicGenerator();
	}	
}

