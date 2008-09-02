/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2003-2007  Miguel Howard <miguel@jmol.org>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the
 * beginning of your source code files, and to any copyright notice that
 * you may distribute with programs based on this work.
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
package org.openscience.cdk.graph.rebond;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.Iterator;

/**
 * Provides tools to rebond a molecule from 3D coordinates only.
 * The algorithm uses an efficient algorithm using a
 * Binary Space Partitioning Tree (Bspt). It requires that the 
 * atom types are configured such that the covalent bond radii
 * for all atoms are set. The AtomTypeFactory can be used for this.
 *
 * @cdk.keyword rebonding
 * @cdk.keyword bond, recalculation
 * @cdk.dictref blue-obelisk:rebondFrom3DCoordinates
 * 
 * @author      Miguel Howard
 * @cdk.created 2003-05-23
 * @cdk.module  standard
 * @cdk.svnrev  $Revision$
 *
 * @see org.openscience.cdk.graph.rebond.Bspt
 */
@TestClass("org.openscience.cdk.graph.rebond.RebondToolTest")
public class RebondTool {

  private double maxCovalentRadius;
  private double minBondDistance;
  private double bondTolerance;
    
  private Bspt bspt;
    
  public RebondTool(double maxCovalentRadius, double minBondDistance,
                    double bondTolerance) {
    this.maxCovalentRadius = maxCovalentRadius;
    this.bondTolerance = bondTolerance;
    this.minBondDistance = minBondDistance;    
    this.bspt = null;
  }
    
  /**
   * Rebonding using a Binary Space Partition Tree. Note, that any bonds
   * defined will be deleted first. It assumes the unit of 3D space to
   * be 1 &Acircle;ngstrom.
   */
  @TestMethod("testRebond_IAtomContainer")
  public void rebond(IAtomContainer container) throws CDKException {
    container.removeAllBonds();
    maxCovalentRadius = 0.0;
    // construct a new binary space partition tree
    bspt = new Bspt(3);
    Iterator<IAtom> atoms = container.atoms().iterator();
    while (atoms.hasNext()) {
      IAtom atom = atoms.next();
      double myCovalentRadius = atom.getCovalentRadius();
      if (myCovalentRadius == 0.0) {
          throw new CDKException("Atom(s) does not have covalentRadius defined.");
      }
      if (myCovalentRadius > maxCovalentRadius)
        maxCovalentRadius = myCovalentRadius;
      TupleAtom tupleAtom = new TupleAtom(atom);
      bspt.addTuple(tupleAtom);
    }
    // rebond all atoms
    atoms = container.atoms().iterator();
    while (atoms.hasNext()) {
      bondAtom(container, (IAtom)atoms.next());
    }
  }
    
  /**
   * Rebonds one atom by looking up nearby atom using the binary space partition tree.
   */
  private void bondAtom(IAtomContainer container, IAtom atom) {
    double myCovalentRadius = atom.getCovalentRadius();
    double searchRadius = myCovalentRadius + maxCovalentRadius + bondTolerance;
    Point tupleAtom = new Point(atom.getPoint3d().x, atom.getPoint3d().y, atom.getPoint3d().z);
    for (Bspt.EnumerateSphere e = bspt.enumHemiSphere(tupleAtom, searchRadius); e.hasMoreElements(); ) {
      IAtom atomNear = ((TupleAtom)e.nextElement()).getAtom();
      if (atomNear != atom && container.getBond(atom, atomNear) == null) {
        boolean bonded = isBonded(myCovalentRadius,  atomNear.getCovalentRadius(), e.foundDistance2());
        if (bonded) {
          IBond bond = atom.getBuilder().newBond(atom, atomNear, IBond.Order.SINGLE);
          container.addBond(bond);
        }
      }
    }
  }

  /** 
   * Returns the bond order for the bond. At this moment, it only returns
   * 0 or 1, but not 2 or 3, or aromatic bond order.
   */

  private boolean isBonded(double covalentRadiusA,
                           double covalentRadiusB,
                           double distance2) {
    double maxAcceptable =
      covalentRadiusA + covalentRadiusB + bondTolerance;
    double maxAcceptable2 = maxAcceptable * maxAcceptable;
    double minBondDistance2 = this.minBondDistance*this.minBondDistance;
    if (distance2 < minBondDistance2)
      return false;
      return distance2 <= maxAcceptable2;
  }

    
  class TupleAtom implements Bspt.Tuple {
    IAtom atom;
        
    TupleAtom(IAtom atom) {
      this.atom = atom;
    }
        
    public double getDimValue(int dim) {
      if (dim == 0)
        return atom.getPoint3d().x;
      if (dim == 1)
        return atom.getPoint3d().y;
      return atom.getPoint3d().z;
    }
        
    public IAtom getAtom() {
      return this.atom;
    }
        
    public String toString() {
      return ("<" + atom.getPoint3d().x + "," + atom.getPoint3d().y + "," +
              atom.getPoint3d().z + ">");
    }
  }
    
}
