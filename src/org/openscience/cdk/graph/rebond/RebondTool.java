/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Jmol project
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.exception.CDKException;

/**
 * Provides tools to rebond a molecule from 3D coordinates only.
 * The algorithm uses an efficient algorithm using a
 * Binary Space Partitioning Tree (Bspt). It requires that the 
 * atom types are configured such that the covalent bond radii
 * for all atoms are set. The AtomTypeFactory can be used for this.
 *
 * @cdk.keyword    rebonding
 * @cdk.keyword    bond, recalculation
 * @cdk.dictref    blue-obelisk:rebondFrom3DCoordinates
 * 
 * @author  Miguel Howard
 * @cdk.created 2003-05-23
 *
 * @see org.openscience.cdk.graph.rebond.Bspt
 */
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
  public void rebond(IAtomContainer container) throws CDKException {
    container.removeAllBonds();
    maxCovalentRadius = 0.0;
    // construct a new binary space partition tree
    bspt = new Bspt(3);
    IAtom[] atoms = container.getAtoms();
    for (int i = atoms.length; --i >= 0; ) {
      IAtom atom = atoms[i];
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
    for (int i = atoms.length; --i >= 0; ) {
      bondAtom(container, atoms[i]);
    }
  }
    
  /**
   * Rebonds one atom by looking up nearby atom using the binary space partition tree.
   */
  private void bondAtom(IAtomContainer container, IAtom atom) {
    double myCovalentRadius = atom.getCovalentRadius();
    double searchRadius = myCovalentRadius + maxCovalentRadius + bondTolerance;
    Point tupleAtom = new Point(atom.getX3d(), atom.getY3d(), atom.getZ3d());
    for (Bspt.EnumerateSphere e = bspt.enumHemiSphere(tupleAtom, searchRadius); e.hasMoreElements(); ) {
      IAtom atomNear = ((TupleAtom)e.nextElement()).getAtom();
      if (atomNear != atom && container.getBond(atom, atomNear) == null) {
        boolean isBonded = isBonded(atom, myCovalentRadius,
                                    atomNear, atomNear.getCovalentRadius(),
                                    e.foundDistance2());
        if (isBonded) {
          IBond bond = new org.openscience.cdk.Bond(atom, atomNear, 1.0);
          container.addBond(bond);
        }
      }
    }
  }

  /** 
   * Returns the bond order for the bond. At this moment, it only returns
   * 0 or 1, but not 2 or 3, or aromatic bond order.
   */
  private boolean isBonded(IAtom atomA, double covalentRadiusA,
                           IAtom atomB, double covalentRadiusB,
                           double distance2) {
    double maxAcceptable =
      covalentRadiusA + covalentRadiusB + bondTolerance;
    double maxAcceptable2 = maxAcceptable * maxAcceptable;
    double minBondDistance2 = this.minBondDistance*this.minBondDistance;
    if (distance2 < minBondDistance2)
      return false;
    if (distance2 <= maxAcceptable2)
      return true;
    return false;
  }
    
  class TupleAtom implements Bspt.Tuple {
    IAtom atom;
        
    TupleAtom(IAtom atom) {
      this.atom = atom;
    }
        
    public double getDimValue(int dim) {
      if (dim == 0)
        return atom.getX3d();
      if (dim == 1)
        return atom.getY3d();
      return atom.getZ3d();
    }
        
    public IAtom getAtom() {
      return this.atom;
    }
        
    public String toString() {
      return ("<" + atom.getX3d() + "," + atom.getY3d() + "," +
              atom.getZ3d() + ">");
    }
  }
    
}
