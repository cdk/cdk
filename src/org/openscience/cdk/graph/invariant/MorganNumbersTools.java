/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.graph.invariant;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Tool for calculating Morgan numbers {@cdk.cite MOR65}.
 *
 * @cdk.module standard
 *
 * @author     shk3
 * @cdk.created    2003-06-30
 * @cdk.keyword    Morgan number
 */
public class MorganNumbersTools {

  /**
   *  Makes an array containing the morgan numbers of the atoms of atomContainer.
   *
   * @param  atomContainer  The atomContainer to analyse.
   * @return                The morgan numbers value.
   */
  public static int[] getMorganNumbers(IAtomContainer atomContainer) {
    int[] morganMatrix;
    int[] tempMorganMatrix;
    int N = atomContainer.getAtomCount();
    morganMatrix = new int[N];
    tempMorganMatrix = new int[N];
    java.util.List atoms = null;
    for (int f = 0; f < N; f++) {
      morganMatrix[f] = atomContainer.getConnectedBondsCount(f);
      tempMorganMatrix[f] = atomContainer.getConnectedBondsCount(f);
    }
    for (int e = 0; e < N; e++) {
      for (int f = 0; f < N; f++) {
        morganMatrix[f] = 0;
        atoms = atomContainer.getConnectedAtomsList(atomContainer.getAtom(f));
        for (int g = 0; g < atoms.size(); g++) {
          morganMatrix[f] += tempMorganMatrix[atomContainer.getAtomNumber((IAtom)atoms.get(g))];
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
  public static String[] getMorganNumbersWithElementSymbol(IAtomContainer atomContainer) {
    int[] morgannumbers = getMorganNumbers(atomContainer);
    String[] morgannumberswithelement = new String[morgannumbers.length];
    for (int i = 0; i < morgannumbers.length; i++) {
      morgannumberswithelement[i] = atomContainer.getAtom(i).getSymbol() + "-" + morgannumbers[i];
    }
    return (morgannumberswithelement);
  }
}

