/*
 *  SaturationChecker.java
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
package org.openscience.cdk.tools;

import org.openscience.cdk.*;
import java.util.Vector;
import java.io.*;

/**
 *  Provides methods for checking whether an atoms valences are saturated with
 *  respect to a particular atom type
 *
 * @author     steinbeck
 * @created    September 4, 2001
 *
 * @keyword saturation
 * @keyword valency
 */
public class SaturationChecker
{
	AtomTypeFactory atf;
	static boolean debug = false;


	/**
	 *  Constructor for the SaturationChecker object
	 *
	 * @exception  java.lang.Exception  Description of Exception
	 * @since
	 */
	public SaturationChecker() throws java.lang.Exception
	{
		atf = new AtomTypeFactory();
	}

	
	/**
	 *  Description of the Method
	 *
	 * @param  atom  Description of Parameter
	 * @param  ac    Description of Parameter
	 * @return       Description of the Returned Value
	 * @since
	 */
	public boolean hasPerfectConfiguration(Atom atom, AtomContainer ac)
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
	public boolean allSaturated(AtomContainer ac)
	{
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			if (!isSaturated(ac.getAtomAt(f), ac))
			{
				return false;
			}
		}
		return true;
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
		if (debug)
		{
			try
			{
				System.out.println("*** Checking saturation of atom " + ac.getAtomNumber(atom) + " ***");
				System.out.println("bondOrderSum: " + bondOrderSum);
				System.out.println("maxBondOrder: " + maxBondOrder);
				System.out.println("hcount: " + hcount);
			}
			catch(Exception exc)
			{
			}
		}
		for (int f = 0; f < atomTypes.length; f++)
		{
			if (bondOrderSum >= atomTypes[f].getMaxBondOrderSum() - hcount && maxBondOrder <= atomTypes[f].getMaxBondOrder())
			{
				if (debug)
				{
					System.out.println("*** Good ! ***");
				}
				return true;
			}
		}
		if (debug)
		{
			System.out.println("*** Bad ! ***");
		}
		return false;
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
}
