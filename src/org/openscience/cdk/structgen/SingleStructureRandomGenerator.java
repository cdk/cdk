/*
 *  SingleStructureRandomGenerator.java
 *
 *  $RCSfile$    $Author$    $Date$    $Revision$
 *
 *  Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 *
 *  Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.structgen;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import java.util.*;
import java.io.*;

/**
 * Randomly generates a single, connected, correctly bonded structure for 
 * a given molecular formula
 * To see it working run the graphical 
 * test org.openscience.cdk.test.SingleStructureRandomGeneratorTest
 * and add more structures to the panel using the "More" button. 
 * In order to use this class, use MFAnalyser to get an AtomContainer from 
 * a molecular formula string.
 * Assign hydrogen counts to each heavy atom. The hydrogens should not be
 * in the atom pool but should be assigned implicitly to the heavy atoms in 
 * order to reduce computational cost.
 * Assign this AtomContainer to the  
 * SingleStructureRandomGenerator and retrieve a randomly generated, but correctly bonded
 * structure by using the generate() method. You can then repeatedly call
 * the generate() method in order to retrieve further structures. 
 * 
 * Agenda: - add a method for randomly adding hydrogens to the atoms
 *         - add a seed for random generator for reproducability
 *
 * @author     steinbeck
 * @created    September 4, 2001
 */
public class SingleStructureRandomGenerator
{
	AtomContainer atomContainer;
	SaturationChecker satCheck;
	static boolean debug = false;
	Random random = null;

	/**
	 *  Constructor for the SingleStructureRandomGenerator object
	 *
	 * @exception  java.lang.Exception  Description of Exception
	 * @since
	 */
	public SingleStructureRandomGenerator(long seed) throws java.lang.Exception
	{
		satCheck = new SaturationChecker();
		random = new Random(seed);
	}

	/**
	 *  Constructor for the SingleStructureRandomGenerator object
	 *
	 * @exception  java.lang.Exception  Description of Exception
	 * @since
	 */
	public SingleStructureRandomGenerator() throws java.lang.Exception
	{
		this((long)11000);
	}

	
	/**
	 *  Sets the AtomContainer attribute of the SingleStructureRandomGenerator object
	 *
	 * @param  ac  The new AtomContainer value
	 * @since
	 */
	public void setAtomContainer(AtomContainer ac)
	{
		this.atomContainer = ac;
	}

	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Returned Value
	 * @since
	 */
	public Molecule generate()
	{
		boolean structureFound = false;
		boolean bondFormed;
		int next;
		double order;
		double max, cmax1, cmax2;
		int iteration = 0;
		Atom partner;
		Atom atom;
		do
		{
			iteration++;
			atomContainer.removeAllBonds();
			do
			{
				bondFormed = false;
				for (int f = 0; f < atomContainer.getAtomCount(); f++)
				{
					atom = atomContainer.getAtomAt(f);

					if (!satCheck.isSaturated(atom, atomContainer))
					{
						partner = getAnotherUnsaturatedNode(atom);
						if (partner != null)
						{
							cmax1 = satCheck.getCurrentMaxBondOrder(atom, atomContainer);
							cmax2 = satCheck.getCurrentMaxBondOrder(partner, atomContainer);
							max = Math.min(cmax1, cmax2);
							order = Math.min(Math.max(1.0, random.nextInt((int)Math.round(max))), 3.0);
							atomContainer.addBond(new Bond(atom, partner, order));
							bondFormed = true;
						}
					}
				}
			} while (bondFormed);
			if (new ConnectivityChecker().isConnected(atomContainer) && satCheck.allSaturated(atomContainer))
			{
				structureFound = true;
			}
		} while (!structureFound && iteration < 20);
		if (debug)
		{
			System.out.println("Structure found after " + iteration + " iterations.");	
		}
		return new Molecule(atomContainer);
	}

	
	/**
	 *  Gets the AnotherUnsaturatedNode attribute of the SingleStructureRandomGenerator object
	 *
	 * @param  exclusionAtom  Description of Parameter
	 * @return                The AnotherUnsaturatedNode value
	 * @since
	 */
	private Atom getAnotherUnsaturatedNode(Atom exclusionAtom)
	{
		Atom atom;
		int next = random.nextInt(atomContainer.getAtomCount());

		for (int f = next; f < atomContainer.getAtomCount(); f++)
		{
			atom = atomContainer.getAtomAt(f);
			if (!satCheck.isSaturated(atom, atomContainer) && exclusionAtom != atom && !atomContainer.getConnectedAtomsVector(exclusionAtom).contains(atom))
			{
				return atom;
			}
		}
		for (int f = 0; f < next; f++)
		{
			atom = atomContainer.getAtomAt(f);
			if (!satCheck.isSaturated(atom, atomContainer) && exclusionAtom != atom && !atomContainer.getConnectedAtomsVector(exclusionAtom).contains(atom))
			{
				return atom;
			}
		}
		return null;
	}
	
	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Returned Value
	 * @since
	 */
	public Object clone()
	{
		Object o = null;
		try
		{
			o = super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			System.err.println("MyObject can't clone");
		}
		return o;
	}
}

