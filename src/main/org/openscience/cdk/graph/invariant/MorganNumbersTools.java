/* $Revision$ $Author$ $Date$
 *
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
 */
package org.openscience.cdk.graph.invariant;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Tool for calculating Morgan numbers {@cdk.cite MOR65}.
 *
 * @cdk.module  standard
 * @cdk.svnrev  $Revision$
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
    java.util.List<IAtom> atoms;
    for (int f = 0; f < N; f++) {
      morganMatrix[f] = atomContainer.getConnectedBondsCount(f);
      tempMorganMatrix[f] = atomContainer.getConnectedBondsCount(f);
    }
    for (int e = 0; e < N; e++) {
      for (int f = 0; f < N; f++) {
        morganMatrix[f] = 0;
        atoms = atomContainer.getConnectedAtomsList(atomContainer.getAtom(f));
          for (IAtom atom : atoms) {
              morganMatrix[f] += tempMorganMatrix[atomContainer.getAtomNumber(atom)];
          }
      }
      System.arraycopy(morganMatrix, 0, tempMorganMatrix, 0, N);
    }
    return tempMorganMatrix;
  }


  /**
   *  Makes an array containing the morgan numbers+element symbol of the atoms of atomContainer. This method
   *  puts the element symbol before the morgan number, usefull for finding out how many different rests are connected to an atom.
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

