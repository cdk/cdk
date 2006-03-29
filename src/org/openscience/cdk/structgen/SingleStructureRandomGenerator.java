/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.structgen;

import java.util.Random;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.SaturationChecker;

/**
 * Randomly generates a single, connected, correctly bonded structure for 
 * a given molecular formula.
 * To see it working run the graphical 
 * test org.openscience.cdk.test.SingleStructureRandomGeneratorTest
 * and add more structures to the panel using the "More" button. 
 * In order to use this class, use MFAnalyser to get an AtomContainer from 
 * a molecular formula string.
 *
 * <p>Assign hydrogen counts to each heavy atom. The hydrogens should not be
 * in the atom pool but should be assigned implicitly to the heavy atoms in 
 * order to reduce computational cost.
 * Assign this AtomContainer to the  
 * SingleStructureRandomGenerator and retrieve a randomly generated, but correctly bonded
 * structure by using the generate() method. You can then repeatedly call
 * the generate() method in order to retrieve further structures. 
 * 
 * <p>Agenda<ul>
 *  <li>add a method for randomly adding hydrogens to the atoms
 *  <li>add a seed for random generator for reproducability
 * </ul>
 *
 * @author     steinbeck
 * @cdk.created    2001-09-04
 */
public class SingleStructureRandomGenerator
{
	AtomContainer atomContainer;
	SaturationChecker satCheck;
	final static boolean debug = true;
	Random random = null;

	/**
	 *  Constructor for the SingleStructureRandomGenerator object
	 *
	 */
	public SingleStructureRandomGenerator(long seed) throws java.lang.Exception
	{
		satCheck = new SaturationChecker();
		random = new Random(seed);
	}

	/**
	 *  Constructor for the SingleStructureRandomGenerator object
	 */
	public SingleStructureRandomGenerator() throws java.lang.Exception
	{
		this((long)11000);
	}

	
	/**
	 *  Sets the AtomContainer attribute of the SingleStructureRandomGenerator object
	 *
	 * @param  ac  The new AtomContainer value
	 */
	public void setAtomContainer(AtomContainer ac)
	{
		this.atomContainer = ac;
	}

	public Molecule generate() throws CDKException
	{
		boolean structureFound = false;
		boolean bondFormed;
		int next;
		double order;
		double max, cmax1, cmax2;
		int iteration = 0;
		org.openscience.cdk.interfaces.IAtom partner;
		org.openscience.cdk.interfaces.IAtom atom;
		do
		{
			iteration++;
			atomContainer.removeAllElectronContainers();
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
							if (debug) System.out.println("Forming bond of order " + order);
							atomContainer.addBond(new Bond(atom, partner, order));
							bondFormed = true;
						}
					}
				}
			} while (bondFormed);
			//System.out.println("Blaeh");
			if (ConnectivityChecker.isConnected(atomContainer) && satCheck.allSaturated(atomContainer))
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
	 * @return                The AnotherUnsaturatedNode value
	 */
	private org.openscience.cdk.interfaces.IAtom getAnotherUnsaturatedNode(org.openscience.cdk.interfaces.IAtom exclusionAtom) throws CDKException
	{
		org.openscience.cdk.interfaces.IAtom atom;
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

