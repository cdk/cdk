/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2003  The Jmol Project
 *  Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.geometry;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.invariant.MorganNumbersTools;

/**
 * A set of static utility classes for geometric calculations on Bonds.
 *
 * @author     shk3
 * @created    August 4, 2005
 */
public class BondTools {
  /**
   *  Tells if a certain bond is center of a valid double bond configuration.
   *
   * @param  container  The atomcontainer.
   * @param  bond       The bond.
   * @return            true=is a potential configuration, false=is not.
   */
  private static boolean isValidDoubleBondConfiguration(AtomContainer container, Bond bond) {
    Atom[] atoms = bond.getAtoms();
    Atom[] connectedAtoms = container.getConnectedAtoms(atoms[0]);
    Atom from = null;
    for (int i = 0; i < connectedAtoms.length; i++) {
      if (connectedAtoms[i] != atoms[1]) {
        from = connectedAtoms[i];
      }
    }
    boolean[] array = new boolean[container.getBonds().length];
    for (int i = 0; i < array.length; i++) {
      array[i] = true;
    }
    if (isStartOfDoubleBond(container, atoms[0], from, array) && isEndOfDoubleBond(container, atoms[1], atoms[0], array) && !bond.getFlag(CDKConstants.ISAROMATIC)) {
      return (true);
    } else {
      return (false);
    }
  }


  /**
   *  Says if two atoms are in cis or trans position around a double bond.
   *  The atoms have to be given to the method like this:  firstOuterAtom - firstInnerAtom = secondInnterAtom - secondOuterAtom
   *
   * @param  firstOuterAtom    See above.
   * @param  firstInnerAtom    See above.
   * @param  secondInnerAtom   See above.
   * @param  secondOuterAtom   See above.
   * @param  ac                The atom container the atoms are in.
   * @return                   true=trans, false=cis.
   * @exception  CDKException  The atoms are not in a double bond configuration (no double bond in the middle, same atoms on one side)
   */
  public static boolean isCisTrans(Atom firstOuterAtom, Atom firstInnerAtom, Atom secondInnerAtom, Atom secondOuterAtom, AtomContainer ac) throws CDKException {
    if (!isValidDoubleBondConfiguration(ac, ac.getBond(firstInnerAtom, secondInnerAtom))) {
      throw new CDKException("There is no valid double bond configuration between your inner atoms!");
    }
    boolean firstDirection = isLeft(firstOuterAtom, firstInnerAtom, secondInnerAtom);
    boolean secondDirection = isLeft(secondOuterAtom, secondInnerAtom, firstInnerAtom);
    if (firstDirection == secondDirection) {
      return true;
    } else {
      return false;
    }
  }


  /**
   *  Says if an atom is on the left side of a another atom seen from a certain
   *  atom or not
   *
   * @param  whereIs   The atom the position of which is returned
   * @param  viewFrom  The atom from which to look
   * @param  viewTo    The atom to which to look
   * @return           true=is left, false = is not
   */
  public static boolean isLeft(Atom whereIs, Atom viewFrom, Atom viewTo) {
    double angle = giveAngleBothMethods(viewFrom, viewTo, whereIs, false);
    if (angle < 0) {
      return (false);
    } else {
      return (true);
    }
  }


  /**
   * Returns true if the two atoms are within the distance fudge
   * factor of each other.
   *
   * @param  atom1                Description of Parameter
   * @param  atom2                Description of Parameter
   * @param  distanceFudgeFactor  Description of Parameter
   * @return                      Description of the Returned Value
   * @cdk.keyword                 join-the-dots
   * @cdk.keyword                 bond creation
   */
  public static boolean closeEnoughToBond(Atom atom1, Atom atom2, double distanceFudgeFactor) {

    if (atom1 != atom2) {
      double distanceBetweenAtoms = atom1.getPoint3d().distance(atom2.getPoint3d());
      double bondingDistance = atom1.getCovalentRadius() + atom2.getCovalentRadius();
      if (distanceBetweenAtoms <= (distanceFudgeFactor * bondingDistance)) {
        return true;
      }
    }
    return false;
  }


  /**
   *  Gives the angle between two lines starting at atom from and going to to1
   *  and to2. If bool=false the angle starts from the middle line and goes from
   *  0 to PI or 0 to -PI if the to2 is on the left or right side of the line. If
   *  bool=true the angle goes from 0 to 2PI.
   *
   * @param  from  the atom to view from.
   * @param  to1   first direction to look in.
   * @param  to2   second direction to look in.
   * @param  bool  true=angle is 0 to 2PI, false=angel is -PI to PI.
   * @return       The angle in rad.
   */
  public static double giveAngleBothMethods(Atom from, Atom to1, Atom to2, boolean bool) {
    double[] A = new double[2];
    from.getPoint2d().get(A);
    double[] B = new double[2];
    to1.getPoint2d().get(B);
    double[] C = new double[2];
    to2.getPoint2d().get(C);
    double angle1 = Math.atan2((B[1] - A[1]), (B[0] - A[0]));
    double angle2 = Math.atan2((C[1] - A[1]), (C[0] - A[0]));
    double angle = angle2 - angle1;
    if (angle2 < 0 && angle1 > 0 && angle2 < -(Math.PI / 2)) {
      angle = Math.PI + angle2 + Math.PI - angle1;
    }
    if (angle2 > 0 && angle1 < 0 && angle1 < -(Math.PI / 2)) {
      angle = -Math.PI + angle2 - Math.PI - angle1;
    }
    if (bool && angle < 0) {
      return (2 * Math.PI + angle);
    } else {
      return (angle);
    }
  }


  /**
   *  Says if an atom is the end of a double bond configuration
   *
   * @param  atom                     The atom which is the end of configuration
   * @param  container                The atomContainer the atom is in
   * @param  parent                   The atom we came from
   * @param  doubleBondConfiguration  The array indicating where double bond
   *      configurations are specified (this method ensures that there is
   *      actually the possibility of a double bond configuration)
   * @return                          false=is not end of configuration, true=is
   */
  private static boolean isEndOfDoubleBond(AtomContainer container, Atom atom, Atom parent, boolean[] doubleBondConfiguration) {
    if (container.getBondNumber(atom, parent) == -1 || doubleBondConfiguration.length <= container.getBondNumber(atom, parent) || !doubleBondConfiguration[container.getBondNumber(atom, parent)]) {
      return false;
    }
    int lengthAtom = container.getConnectedAtoms(atom).length + atom.getHydrogenCount();
    int lengthParent = container.getConnectedAtoms(parent).length + parent.getHydrogenCount();
    if (container.getBond(atom, parent) != null) {
      if (container.getBond(atom, parent).getOrder() == CDKConstants.BONDORDER_DOUBLE && (lengthAtom == 3 || (lengthAtom == 2 && atom.getSymbol().equals("N"))) && (lengthParent == 3 || (lengthParent == 2 && parent.getSymbol().equals("N")))) {
        Atom[] atoms = container.getConnectedAtoms(atom);
        Atom one = null;
        Atom two = null;
        for (int i = 0; i < atoms.length; i++) {
          if (atoms[i] != parent && one == null) {
            one = atoms[i];
          } else if (atoms[i] != parent && one != null) {
            two = atoms[i];
          }
        }
        String[] morgannumbers = MorganNumbersTools.getMorganNumbersWithElementSymbol(container);
        if ((one != null && two == null && atom.getSymbol().equals("N") && Math.abs(giveAngleBothMethods(parent, atom, one, true)) > Math.PI / 10) || (!atom.getSymbol().equals("N") && one != null && two != null && !morgannumbers[container.getAtomNumber(one)].equals(morgannumbers[container.getAtomNumber(two)]))) {
          return (true);
        } else {
          return (false);
        }
      }
    }
    return (false);
  }


  /**
   *  Says if an atom is the start of a double bond configuration
   *
   * @param  a                        The atom which is the start of configuration
   * @param  container                The atomContainer the atom is in
   * @param  parent                   The atom we came from
   * @param  doubleBondConfiguration  The array indicating where double bond
   *      configurations are specified (this method ensures that there is
   *      actually the possibility of a double bond configuration)
   * @return                          false=is not start of configuration, true=is
   */
  private static boolean isStartOfDoubleBond(AtomContainer container, Atom a, Atom parent, boolean[] doubleBondConfiguration) {
    int lengthAtom = container.getConnectedAtoms(a).length + a.getHydrogenCount();
    if (lengthAtom != 3 && (lengthAtom != 2 && a.getSymbol() != ("N"))) {
      return (false);
    }
    Atom[] atoms = container.getConnectedAtoms(a);
    Atom one = null;
    Atom two = null;
    boolean doubleBond = false;
    Atom nextAtom = null;
    for (int i = 0; i < atoms.length; i++) {
      if (atoms[i] != parent && container.getBond(atoms[i], a).getOrder() == CDKConstants.BONDORDER_DOUBLE && isEndOfDoubleBond(container, atoms[i], a, doubleBondConfiguration)) {
        doubleBond = true;
        nextAtom = atoms[i];
      }
      if (atoms[i] != nextAtom && one == null) {
        one = atoms[i];
      } else if (atoms[i] != nextAtom && one != null) {
        two = atoms[i];
      }
    }
    String[] morgannumbers = MorganNumbersTools.getMorganNumbersWithElementSymbol(container);
    if (one != null && ((!a.getSymbol().equals("N") && two != null && !morgannumbers[container.getAtomNumber(one)].equals(morgannumbers[container.getAtomNumber(two)]) && doubleBond && doubleBondConfiguration[container.getBondNumber(a, nextAtom)]) || (doubleBond && a.getSymbol().equals("N") && Math.abs(giveAngleBothMethods(nextAtom, a, parent, true)) > Math.PI / 10))) {
      return (true);
    } else {
      return (false);
    }
  }
}


