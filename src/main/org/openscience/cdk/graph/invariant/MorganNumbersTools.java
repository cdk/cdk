/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *                    2011  Thorsten Flügel <thorsten.fluegel@tu-dortmund.de>
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
package org.openscience.cdk.graph.invariant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Tool for calculating Morgan numbers {@cdk.cite MOR65}.
 *
 * @cdk.module  standard
 * @cdk.githash
 *
 * @author      shk3
 * @cdk.created 2003-06-30
 * @cdk.keyword Morgan number
 */
@TestClass("org.openscience.cdk.graph.invariant.MorganNumbersToolsTest")
public class MorganNumbersTools {

  /**
   * Makes an array containing the morgan numbers of the atoms of atomContainer.
   *
   * @param  atomContainer  The atomContainer to analyse.
   * @return                The morgan numbers value.
   */
  @TestMethod("testGetMorganNumbers_IAtomContainer")
  public static long[] getMorganNumbers(IAtomContainer atomContainer) {
		long[] morganMatrix;
		long[] tempMorganMatrix;
		int N = atomContainer.getAtomCount();
		morganMatrix = new long[N];
		tempMorganMatrix = new long[N];
		@SuppressWarnings("unchecked")
		java.util.List<IAtom>[] atoms = new List[N];
		@SuppressWarnings("unchecked")
		Map<IAtom, Integer>[] atomIndices = new HashMap[N];
		for (int f = 0; f < N; f++) {
			morganMatrix[f] = atomContainer.getConnectedBondsCount(f);
			tempMorganMatrix[f] = atomContainer.getConnectedBondsCount(f);
			atoms[f] = atomContainer.getConnectedAtomsList(atomContainer.getAtom(f));
			atomIndices[f] = new HashMap<IAtom, Integer>();
			for (IAtom atom : atoms[f]) {
				atomIndices[f].put(atom, atomContainer.getAtomNumber(atom));
			}
		}
		for (int e = 0; e < N; e++) {
			for (int f = 0; f < N; f++) {
				morganMatrix[f] = 0;
				for (IAtom atom : atoms[f]) {
					morganMatrix[f] += tempMorganMatrix[atomIndices[f].get(atom)];
				}
			}
			System.arraycopy(morganMatrix, 0, tempMorganMatrix, 0, N);
		}
		return tempMorganMatrix;
  }


  /**
   *  Makes an array containing the morgan numbers+element symbol of the atoms of atomContainer. This method
   *  puts the element symbol before the morgan number, useful for finding out how many different rests are connected to an atom.
   *
   * @param  atomContainer  The atomContainer to analyse.
   * @return                The morgan numbers value.
   */
  @TestMethod("testPhenylamine")
  public static String[] getMorganNumbersWithElementSymbol(IAtomContainer atomContainer) {
    long[] morgannumbers = getMorganNumbers(atomContainer);
    String[] morgannumberswithelement = new String[morgannumbers.length];
    for (int i = 0; i < morgannumbers.length; i++) {
      morgannumberswithelement[i] = atomContainer.getAtom(i).getSymbol() + "-" + morgannumbers[i];
    }
    return (morgannumberswithelement);
  }
}

