/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
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
package org.openscience.cdk.isomorphism;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.invariant.MorganNumbersTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;

import java.util.Arrays;

/**
 * A too simplistic implementation of an isomorphism test for chemical graphs.
 *
 * <p><b>Important:</b> as it uses the MorganNumbersTools it does not take bond
 * order into account.
 *
 * <p>Alternatively, you can use the UniversalIsomorphismTester.
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @author     steinbeck
 * @cdk.created    2001-09-10
 *
 * @cdk.keyword    isomorphism
 *
 * @see        org.openscience.cdk.graph.invariant.MorganNumbersTools
 * @see        org.openscience.cdk.isomorphism.UniversalIsomorphismTester
 */
@TestClass("org.openscience.cdk.isomorphism.IsomorphismTesterTest")
public class IsomorphismTester implements java.io.Serializable
{

	private static final long serialVersionUID = 2499779110996693974L;
	long[] baseTable;
	long[] sortedBaseTable;
	long[] compareTable;
	long[] sortedCompareTable;
	IMolecule base = null;
	IMolecule compare = null;


	/**
	 *  Constructor for the IsomorphismTester object
	 */
	public IsomorphismTester() { }


	/**
	 *  Constructor for the IsomorphismTester object
	 */
	public IsomorphismTester(IMolecule mol) throws NoSuchAtomException
	{
		setBaseTable(mol);
	}


	/**
	 *  Checks whether a given molecule is isomorphic with the one
	 *  that has been assigned to this IsomorphismTester at contruction time
	 *
	 * @param  mol1                     A first molecule to check against the second one
	 * @param  mol2                     A second molecule to check against the first
	 * @return                          True, if the two molecules are isomorphic
	 */
    @TestMethod("testIsIsomorphic_IMolecule_IMolecule")
    public boolean isIsomorphic(IMolecule mol1, IMolecule mol2) {
		setBaseTable(mol1);
		return isIsomorphic(mol2);
	}


	/**
	 *  Checks whether a given molecule is isomorphic with the one 
	 *  that has been assigned to this IsomorphismTester at contruction time
	 *
	 * @param  mol2                     A molecule to check 
	 * @return                          True, if the two molecules are isomorphic 
	 */
    @TestMethod("testIsIsomorphic_IMolecule")
    public boolean isIsomorphic(IMolecule mol2) {
		boolean found;
		IAtom atom1 = null;
		IAtom atom2 = null;
		setCompareTable(mol2);
		for (int f = 0; f < sortedBaseTable.length; f++)
		{
			if (sortedBaseTable[f] != sortedCompareTable[f])
			{
				return false;
			}
		}

		for (int f = 0; f < baseTable.length; f++)
		{
			found = false;
			for (int g = 0; g < compareTable.length; g++)
			{
				if (baseTable[f] == compareTable[g])
				{
					atom1 = base.getAtom(f);
					atom2 = compare.getAtom(g);
					if (!(atom1.getSymbol().equals(atom2.getSymbol())) && 
                          atom1.getHydrogenCount() == atom2.getHydrogenCount())
					{
						return false;
					}
					found = true;
				}
			}
			if (!found)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 *  Sets the BaseTable attribute of the IsomorphismTester object
	 *
	 * @param  mol                      The new BaseTable value
	 */
	private void setBaseTable(IMolecule mol) {
		this.base = mol;
		this.baseTable = MorganNumbersTools.getMorganNumbers(base);
		sortedBaseTable = new long[baseTable.length];
		System.arraycopy(baseTable, 0, sortedBaseTable, 0, baseTable.length);
		Arrays.sort(sortedBaseTable);
	}


	/**
	 *  Sets the CompareTable attribute of the IsomorphismTester object
	 *
	 * @param  mol                      The new CompareTable value
	 */
	private void setCompareTable(IMolecule mol) {
		this.compare = mol;
		this.compareTable = MorganNumbersTools.getMorganNumbers(compare);
		sortedCompareTable = new long[compareTable.length];
		System.arraycopy(compareTable, 0, sortedCompareTable, 0, compareTable.length);
		Arrays.sort(sortedCompareTable);

	}
}

