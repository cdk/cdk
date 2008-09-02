/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2007  Oliver Horlacher <oliver.horlacher@therastrat.com>
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
 *  */
package org.openscience.cdk.graph.invariant;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.InvPair;

import java.util.*;

/**
 * Canonically lables an atom container implementing
 * the algorithm published in David Weininger et.al. {@cdk.cite WEI89}.
 * The Collections.sort() method uses a merge sort which is 
 * stable and runs in n log(n).
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @author   Oliver Horlacher <oliver.horlacher@therastrat.com>
 * @cdk.created  2002-02-26
 *
 * @cdk.keyword canonicalization
 */
@TestClass("org.openscience.cdk.graph.invariant.CanonicalLabelerTest")
public class CanonicalLabeler {

  public CanonicalLabeler() {
  }

  /**
   * Canonicaly label the fragment.  The labels are set as atom property InvPair.CANONICAL_LABEL of type Integer, indicating the canonical order.
   * This is an implementation of the algorithm published in
   * David Weininger et.al. {@cdk.cite WEI89}.
   * 
   * <p>The Collections.sort() method uses a merge sort which is 
   * stable and runs in n log(n).
   * 
   * <p>It is assumed that a chemicaly valid AtomContainer is provided: 
   * this method does not check
   * the correctness of the AtomContainer. Negative H counts will 
   * cause a NumberFormatException to be thrown.
   * @param atomContainer The molecule to label
   */
  @TestMethod("testCanonLabel_IAtomContainer,testSomeMoleculeWithDifferentStartingOrder")
  public synchronized void canonLabel(IAtomContainer atomContainer) {
    if (atomContainer.getAtomCount() == 0)
    	return;
    if (atomContainer.getAtomCount() == 1) {
    	atomContainer.getAtom(0).setProperty(InvPair.CANONICAL_LABEL, 1);
    }
      
    ArrayList vect = createInvarLabel(atomContainer);
    step3(vect, atomContainer);
  }

  /**
   * @param v the invariance pair vector
   */
  private void step2(ArrayList v, IAtomContainer atoms) {
    primeProduct(v, atoms);
    step3(v, atoms);
  }

  /**
   * @param v the invariance pair vector
   */
  private void step3(ArrayList v, IAtomContainer atoms) {
    sortArrayList(v);
    rankArrayList(v);
    if (!isInvPart(v)) {
      step2(v, atoms);
    } else {
      //On first pass save, partitioning as symmetry classes.
        if (((InvPair) v.get(v.size()-1)).getCurr() < v.size()) {
            breakTies(v);
            step2(v, atoms);
        }
        // now apply the ranking
        for (Object aV : v) {
            ((InvPair) aV).commit();
        }
    }
  }

  /**
   * Create initial invariant labeling corresponds to step 1
   *
   * @return ArrayList containting the
   */
  private ArrayList createInvarLabel(IAtomContainer atomContainer) {
    java.util.Iterator atoms = atomContainer.atoms().iterator();
    IAtom a;
    StringBuffer inv;
    ArrayList vect = new ArrayList();
    while(atoms.hasNext()) {
      a = (IAtom)atoms.next();
      inv = new StringBuffer();
      inv.append(atomContainer.getConnectedAtomsList(a).size() +
              (a.getHydrogenCount() == CDKConstants.UNSET ? 0 : a.getHydrogenCount())); //Num connections
      inv.append(atomContainer.getConnectedAtomsList(a).size());                        //Num of non H bonds
      inv.append(a.getAtomicNumber());                                              //Atomic number

      Double charge = a.getCharge();
        if (charge == CDKConstants.UNSET) charge = 0.0;
      if (charge < 0)                                                        //Sign of charge
        inv.append(1);
      else
        inv.append(0);                                                              //Absolute charge
      inv.append((int)Math.abs( (a.getFormalCharge() == CDKConstants.UNSET ? 0.0 : a.getFormalCharge())));                                     //Hydrogen count
      inv.append((a.getHydrogenCount() == CDKConstants.UNSET ? 0 : a.getHydrogenCount()));
      vect.add(new InvPair(Long.parseLong(inv.toString()), a));
    }
    return vect;
  }

  /**
   * Calculates the product of the neighbouring primes.
   *
   * @param v the invariance pair vector
   */
  private void primeProduct(ArrayList v, IAtomContainer atomContainer) {
    Iterator it = v.iterator();
    Iterator n;
    InvPair inv;
    IAtom a;
    long summ;
    while (it.hasNext()) {
      inv = (InvPair) it.next();
      List neighbour = atomContainer.getConnectedAtomsList(inv.getAtom());
      n = neighbour.iterator();
      summ = 1;
      while (n.hasNext()) {
        a = (IAtom) n.next();
        int next = ((InvPair)a.getProperty(InvPair.INVARIANCE_PAIR)).getPrime();
        summ = summ * next;
      }
      inv.setLast(inv.getCurr());
      inv.setCurr(summ);
    }
  }

  /**
   * Sorts the vector according to the current invariance, corresponds to step 3
   *
   * @param v the invariance pair vector
   * @cdk.todo    can this be done in one loop?
   */
  private void sortArrayList(ArrayList v) {
    Collections.sort(v, new Comparator() {
      public int compare(Object o1, Object o2) {
        return (int) (((InvPair) o1).getCurr() - ((InvPair) o2).getCurr());
      }
    });
    Collections.sort(v, new Comparator() {
      public int compare(Object o1, Object o2) {
        return (int) (((InvPair) o1).getLast() - ((InvPair) o2).getLast());
      }
    });
  }

  /**
   * Rank atomic vector, corresponds to step 4.
   *
   *  @param v the invariance pair vector
   */
  private void rankArrayList(ArrayList v) {
    int num = 1;
    int[] temp = new int[v.size()];
    InvPair last = (InvPair) v.get(0);
    Iterator it = v.iterator();
    InvPair curr;
    for (int x = 0; it.hasNext(); x++) {
      curr = (InvPair) it.next();
      if (!last.equals(curr)) {
        num++;
      }
      temp[x] = num;
      last = curr;
    }
    it = v.iterator();
    for (int x = 0; it.hasNext(); x++) {
      curr = (InvPair) it.next();
      curr.setCurr(temp[x]);
      curr.setPrime();
    }
  }

  /**
   * Checks to see if the vector is invariantely partitioned
   *
   * @param v the invariance pair vector
   * @return true if the vector is invariantely partitioned, false otherwise
   */
  private boolean isInvPart(ArrayList v) {
    if (((InvPair) v.get(v.size()-1)).getCurr() == v.size())
      return true;
    Iterator it = v.iterator();
    InvPair curr;
    while (it.hasNext()) {
      curr = (InvPair) it.next();
      if (curr.getCurr() != curr.getLast())
        return false;
    }
    return true;
  }

  /**
   * Break ties. Corresponds to step 7
   *
   * @param v the invariance pair vector
   */
  private void breakTies(ArrayList v) {
    Iterator it = v.iterator();
    InvPair curr;
    InvPair last = null;
    int tie = 0;
    boolean found = false;
    for (int x = 0; it.hasNext(); x++) {
      curr = (InvPair) it.next();
      curr.setCurr(curr.getCurr() * 2);
      curr.setPrime();
      if (x != 0 && !found && curr.getCurr() == last.getCurr()) {
        tie = x - 1;
        found = true;
      }
      last = curr;
    }
    curr = (InvPair) v.get(tie);
    curr.setCurr(curr.getCurr() - 1);
    curr.setPrime();
  }
}
