/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.graph.invariant;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;

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
  public static int[] getMorganNumbers(AtomContainer atomContainer) {
    int[] morganMatrix;
    int[] tempMorganMatrix;
    int N = atomContainer.getAtomCount();
    morganMatrix = new int[N];
    tempMorganMatrix = new int[N];
    Atom[] atoms = null;
    for (int f = 0; f < N; f++) {
      morganMatrix[f] = atomContainer.getBondCount(f);
      tempMorganMatrix[f] = atomContainer.getBondCount(f);
    }
    for (int e = 0; e < N; e++) {
      for (int f = 0; f < N; f++) {
        morganMatrix[f] = 0;
        atoms = atomContainer.getConnectedAtoms(atomContainer.getAtomAt(f));
        for (int g = 0; g < atoms.length; g++) {
          morganMatrix[f] += tempMorganMatrix[atomContainer.getAtomNumber(atoms[g])];
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
  public static String[] getMorganNumbersWithElementSymbol(AtomContainer atomContainer) {
    int[] morgannumbers = getMorganNumbers(atomContainer);
    String[] morgannumberswithelement = new String[morgannumbers.length];
    for (int i = 0; i < morgannumbers.length; i++) {
      morgannumberswithelement[i] = atomContainer.getAtomAt(i).getSymbol() + "-" + morgannumbers[i];
    }
    return (morgannumberswithelement);
  }
}

