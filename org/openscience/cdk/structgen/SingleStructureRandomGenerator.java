/*
 *  SingleStructureRandomGenerator.java
 *
 *  $RCSfile$    $Author$    $Date$    $Revision$
 *
 *  Copyright (C) 1997-2001  The CDK project
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
import java.util.Vector;
import java.io.*;

/**
 *  Description of the Class
 *
 * @author     steinbeck
 * @created    September 4, 2001
 */
public class SingleStructureRandomGenerator
{
	AtomContainer atomContainer;
	AtomTypeFactory atf;
	static boolean debug = true;


	/**
	 *  Constructor for the SingleStructureRandomGenerator object
	 *
	 * @exception  java.lang.Exception  Description of Exception
	 * @since
	 */
	public SingleStructureRandomGenerator() throws java.lang.Exception
	{
		atf = new AtomTypeFactory();
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
	 *  Returns the currently maximum formable bond order for this atom
	 *
	 * @param  atom  The atom to be checked
	 * @param  ac    The AtomContainer that provides the context
	 * @return       the currently maximum formable bond order for this atom
	 * @since
	 */
	public double getCurrentMaxBondOrder(Atom atom, AtomContainer ac)
	{
		AtomType[] atomTypes = atf.getAtomTypes(atom.getSymbol(), atf.ATOMTYPE_ID_STRUCTGEN);
		double bondOrderSum = ac.getBondOrderSum(atom);
		double maxBondOrder = ac.getHighestCurrentBondOrder(atom);
		int hcount = atom.getHydrogenCount();
		double max = 0;
		double current = 0;
		for (int f = 0; f < atomTypes.length; f++)
		{
			current = hcount + bondOrderSum;
			if (atomTypes[f].getMaxBondOrderSum() - current > max)
			{
				max = atomTypes[f].getMaxBondOrderSum() - current;
			}
		}
		return max;
	}



	/**
	 *  Description of the Method
	 *
	 * @param  atom  Description of Parameter
	 * @param  ac    Description of Parameter
	 * @return       Description of the Returned Value
	 * @since
	 */
	public boolean isSaturated(Atom atom, AtomContainer ac)
	{
		AtomType[] atomTypes = atf.getAtomTypes(atom.getSymbol(), atf.ATOMTYPE_ID_STRUCTGEN);
		double bondOrderSum = ac.getBondOrderSum(atom);
		double maxBondOrder = ac.getHighestCurrentBondOrder(atom);
		int hcount = atom.getHydrogenCount();
		for (int f = 0; f < atomTypes.length; f++)
		{
			if (bondOrderSum >= atomTypes[f].getMaxBondOrderSum() - hcount && maxBondOrder <= atomTypes[f].getMaxBondOrder())
			{
				return true;
			}
		}
		return false;
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Returned Value
	 * @since
	 */
	public AtomContainer generate()
	{
		boolean structureFound = false;
		boolean bondFormed;
		int next;
		double order;
		double max;
		int iteration = 0;
		Atom partner;
		Atom atom;
		do
		{
			iteration++;
			do
			{
				bondFormed = false;
				for (int f = 0; f < atomContainer.getAtomCount(); f++)
				{
					atom = atomContainer.getAtomAt(f);

					if (!hasPerfectConfiguration(atom, atomContainer))
					{
						partner = getAnotherUnsaturatedNode(atom);
						if (partner != null)
						{
							max = Math.min(getCurrentMaxBondOrder(atom, atomContainer), getCurrentMaxBondOrder(partner, atomContainer));
							order = Math.min(Math.max(1.0, (double)Math.round(Math.random() * max)), 3.0);
							atomContainer.addBond(new Bond(atom, partner, order));
							bondFormed = true;
						}
					}
				}
			} while (bondFormed);
			if (new ConnectivityChecker().isConnected(atomContainer) && allSaturated())
			{
				return atomContainer;
			}
		} while (!true);
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
		int next = (int) (Math.random() * atomContainer.getAtomCount());

		for (int f = next; f < atomContainer.getAtomCount(); f++)
		{
			atom = atomContainer.getAtomAt(f);
			if (!isSaturated(atom, atomContainer) && exclusionAtom != atom && !atomContainer.getConnectedAtomsVector(exclusionAtom).contains(atom))
			{
				return atom;
			}
		}
		for (int f = 0; f < next; f++)
		{
			atom = atomContainer.getAtomAt(f);
			if (!isSaturated(atom, atomContainer) && exclusionAtom != atom && !atomContainer.getConnectedAtomsVector(exclusionAtom).contains(atom))
			{
				return atom;
			}
		}
		return null;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  atom  Description of Parameter
	 * @param  ac    Description of Parameter
	 * @return       Description of the Returned Value
	 * @since
	 */
	private boolean hasPerfectConfiguration(Atom atom, AtomContainer ac)
	{
		AtomType[] atomTypes = atf.getAtomTypes(atom.getSymbol(), atf.ATOMTYPE_ID_STRUCTGEN);
		double bondOrderSum = ac.getBondOrderSum(atom);
		double maxBondOrder = ac.getHighestCurrentBondOrder(atom);

		if (debug)
		{
			System.out.println("*** Checking for perfect configuration ***");
			try
			{
				System.out.println("Checking configuration of atom " + ac.getAtomNumber(atom));
				System.out.println("Atom has bondOrderSum = " + bondOrderSum);
				System.out.println("Atom has max = " + bondOrderSum);
			}
			catch (Exception exc)
			{

			}

		}
		for (int f = 0; f < atomTypes.length; f++)
		{
			if (debug)
			{

			}
			if (bondOrderSum == atomTypes[f].getMaxBondOrderSum() && maxBondOrder == atomTypes[f].getMaxBondOrder())
			{
				if (debug)
				{
					try
					{
						System.out.println("Atom " + ac.getAtomNumber(atom) + " has perfect configuration");
					}
					catch (Exception exc)
					{

					}
				}
				return true;
			}
		}
		if (debug)
		{
			try
			{
				System.out.println("*** Atom " + ac.getAtomNumber(atom) + " has imperfect configuration ***");
			}
			catch (Exception exc)
			{

			}
		}
		return false;
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Returned Value
	 * @since
	 */
	private boolean allSaturated()
	{
		for (int f = 0; f < atomContainer.getAtomCount(); f++)
		{
			if (!hasPerfectConfiguration(atomContainer.getAtomAt(f), atomContainer))
			{
				return false;
			}
		}
		return true;
	}


	/**
	 *  Description of the Method
	 *
	 * @since
	 */
	private void sortNodes() { }
}

