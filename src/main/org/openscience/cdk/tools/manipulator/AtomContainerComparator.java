/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2007  Andreas Schueller <archvile18@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;

/**
 * <p>Compares two IAtomContainers for order with the following criteria with decreasing priority:</p>
 * <ul>
 *   <li>Compare atom count
 *   <li>Compare molecular weight (heavy atoms only)
 *   <li>Compare bond count
 *   <li>Compare sum of bond orders (heavy atoms only)
 * </ul>
 * <p>If no difference can be found with the above criteria, the IAtomContainers are
 * considered equal.</p>
 *
 * @author Andreas Schueller
 * @cdk.created  2007-09-05
 * @cdk.module   standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.tools.manipulator.AtomContainerComparatorTest")
public class AtomContainerComparator implements Comparator {
  
  /** Configure LoggingTool */
  private LoggingTool logger = new LoggingTool(AtomContainerComparator.class);
  
  /** Creates a new instance of AtomContainerComparator */
  public AtomContainerComparator() {
  }

  /*
   * <p>Compares two IAtomContainers for order with the following criteria with decreasing priority:</p>
   * <ul>
   *   <li>Compare atom count
   *   <li>Compare molecular weight (heavy atoms only)
   *   <li>Compare bond count
   *   <li>Compare sum of bond orders (heavy atoms only)
   * </ul>
   * <p>If no difference can be found with the above criteria, the IAtomContainers are
   * considered equal.</p>
   * <p>Returns a negative integer, zero, or a positive integer as the first argument is less than,
   * equal to, or greater than the second.</p>
   * <p>This method is null safe.</p>
   *
   * @param o1 the first IAtomContainer
   * @param o2 the second IAtomContainer
   * @return a negative integer, zero, or a positive integer as the first argument is less than, equal
   *         to, or greater than the second.
   */
    @TestMethod("testCompare_Object_Object")
  public int compare(Object o1, Object o2) {
    // Check for nulls
    if (o1 == null && o2 == null)
      return 0;
    if (o1 == null)
      return -1;
    if (o2 == null)
      return 1;
    
    // Check for correct instances
    if (!(o1 instanceof IAtomContainer) && !(o2 instanceof IAtomContainer))
      return 0;
    if (!(o1 instanceof IAtomContainer))
      return -1;
    if (!(o2 instanceof IAtomContainer))
      return 1;
    
    IAtomContainer atomContainer1 = (IAtomContainer) o1;
    IAtomContainer atomContainer2 = (IAtomContainer) o2;
    
    // 1. Compare atom count
    if (atomContainer1.getAtomCount() > atomContainer2.getAtomCount())
      return 1;
    else if (atomContainer1.getAtomCount() < atomContainer2.getAtomCount())
      return -1;
    else {
      // 2. Atom count equal, compare molecular weight (heavy atoms only)
      double mw1 = 0;
      double mw2 = 0;
      try {
        mw1 = getMolecularWeight(atomContainer1);
        mw2 = getMolecularWeight(atomContainer2);
      } catch (CDKException e) {
        logger.warn("Exception in molecular mass calculation.");
        return 0;
      }
      if (mw1 > mw2)
        return 1;
      else if (mw1 < mw2)
        return -1;
      else {
        // 3. Molecular weight equal, compare bond count
        if (atomContainer1.getBondCount() > atomContainer2.getBondCount())
          return 1;
        else if (atomContainer1.getBondCount() < atomContainer2.getBondCount())
          return -1;
        else {
          // 4. Bond count equal, compare sum of bond orders (heavy atoms only)
          double bondOrderSum1 = AtomContainerManipulator.getSingleBondEquivalentSum(atomContainer1);
          double bondOrderSum2 = AtomContainerManipulator.getSingleBondEquivalentSum(atomContainer2);
          if (bondOrderSum1 > bondOrderSum2)
            return 1;
          else if (bondOrderSum1 < bondOrderSum2)
            return -1;
        }

      }
    }
    // AtomContainers are equal in terms of this comparator
    return 0;
  }
  
  /**
   * Returns the molecular weight (exact mass) of the major isotopes
   * of all heavy atoms of the given IAtomContainer.
   * @param atomContainer an IAtomContainer to calculate the mocular weight for
   * @throws org.openscience.cdk.exception.CDKException if an error occurs with the IsotopeFactory
   * @return the molecularweight (exact mass) of the major isotopes
   *         of all heavy atoms of the given IAtomContainer
   */
  private double getMolecularWeight(IAtomContainer atomContainer) throws CDKException {
      double mw = 0.0;
      try {
          for (IAtom atom : atomContainer.atoms()) {
              if (!atom.getSymbol().equals("H"))
                  mw += IsotopeFactory.getInstance(atomContainer.getBuilder()).getMajorIsotope(atom.getSymbol()).getExactMass();
          }
      } catch (IOException e) {
          throw new CDKException(e.getMessage(), e);
      }
      return mw;
  }
  
}
