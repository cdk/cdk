/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.libio.cml;

import org.w3c.dom.Document;

import org.xmlcml.cmlimpl.*;
import org.xmlcml.cml.*;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;

/**
 * Class that provides convertor procedures to
 * convert CDK classes to cml classes/documents.
 *
 * @author        shk3
 * @created       February 19, 2004
 * @cdk.module    libio
 * @keyword       cml
 * @keyword       class convertor
 */
public class Convertor {

  public final static int COORDINATES_3D = 3;
  public final static int COORDINATES_2D = 2;
  private static org.openscience.cdk.tools.LoggingTool logger = new org.openscience.cdk.tools.LoggingTool();


  /**
   *  Converts a cdk atom to cml atom.
   *
   * @param  atom  The atom to convert.
   * @param  ac    An atom container atom is in, null if none. Used for building id.
   * @param  doc   The cmldocument we want this to be part of.
   * @return       The cml atom.
   */
  public static CMLAtom convert(Atom atom, AtomContainer ac, CMLDocument doc) {
    return convert(atom, -1, ac, doc);
  }


  /**
   *  Converts a cdk atom to cml atom.
   *
   * @param  atom  The atom to convert.
   * @param  coordType  2 or 3.
   * @param  ac    An atom container atom is in, null if none. Used for building id.
   * @param  doc   The cmldocument we want this to be part of.
   * @return       The cml atom.
   */
  public static CMLAtom convert(Atom atom, int coordType, AtomContainer ac, CMLDocument doc) {
    if (atom != null) {
      AtomImpl convertedAtom = new AtomImpl(doc);
      if (coordType == COORDINATES_3D || (atom.getPoint3D() != null && coordType != -1)) {
        convertedAtom.setX3(atom.getX3D());
        convertedAtom.setY3(atom.getY3D());
        convertedAtom.setZ3(atom.getZ3D());
      } else if (coordType == COORDINATES_2D || (atom.getPoint2D() != null && coordType != -1)) {
        convertedAtom.setX2(atom.getX2D());
        convertedAtom.setY2(atom.getY2D());
      }
      convertedAtom.setElementType(atom.getSymbol());
      if (atom.getCharge() != 0) {
        convertedAtom.setFormalCharge((int) atom.getCharge());
      }
      if (atom.getHydrogenCount() != 0) {
        convertedAtom.setHydrogenCount(atom.getHydrogenCount() + "");
      }
      if (ac != null) {
        convertedAtom.setId("a" + (ac.getAtomNumber(atom) + 1));
      }
      int massNumber = atom.getMassNumber();
      if (!(atom instanceof PseudoAtom)) {
        try {
          Isotope majorIsotope = IsotopeFactory.getInstance().getMajorIsotope(atom.getSymbol());
          if (majorIsotope != null) {
            int majorMassNumber = majorIsotope.getMassNumber();
            if (massNumber != 0 && massNumber != majorMassNumber) {
              convertedAtom.setIsotope(massNumber);
            }
          } else {
            logger.warn("Could not find major isotope for : " + atom.getSymbol());
          }
        } catch (Exception ex) {
          logger.error("Failed to initiate isotope factory: " + ex.toString());
        }
      }
      return convertedAtom;
    } else {
      return null;
    }
  }


  /**
   *  Converts a cdk bond to cml bond.
   *
   * @param  bond              The bond to convert.
   * @param  ac                An atom container atom is in, null if none. Used for building id and related atoms.
   * @param  doc               The document this should be part of.
   * @return                   Description of the Returned Value.
   * @exception  CMLException  Description of Exception.
   */
  public static CMLBond convert(Bond bond, AtomContainer ac, CMLDocument doc) throws CMLException {
    if (bond != null) {
      BondImpl convertedBond = new BondImpl(doc);
      if (ac != null) {
        convertedBond.setAtomRefs2("a" + ac.getAtomNumber(bond.getAtoms()[0]) + " a" + ac.getAtomNumber(bond.getAtoms()[1]));
        convertedBond.setId("b" + (ac.getBondNumber(bond) + 1));
      }
      convertedBond.setOrder(((int) bond.getOrder()) + "");
      return convertedBond;
    } else {
      return null;
    }
  }


  /**
   *  Converts a cdk molecule to a cml molecule.
   *
   * @param  mol               The molecule to convert.
   * @return                   The converted molecule.
   * @exception  CMLException  Description of Exception
   */
  public static CMLMolecule convert(Molecule mol) throws CMLException {
    return convert(mol, -1);
  }


  /**
   *  Converts a cdk molecule to a cml molecule.
   *
   * @param  mol               The molecule to convert.
   * @param  coordType         2 or 3.
   * @return                   The converted molecule.
   * @exception  CMLException  Description of Exception
   */
  public static CMLMolecule convert(Molecule mol, int coordType) throws CMLException {
    CMLDocumentFactory docfac = DocumentFactoryImpl.newInstance();
    CMLDocument doc = (CMLDocument) docfac.createDocument();
    return convert(mol, coordType, doc);
  }



  /**
   *  Converts a cdk molecule to a cml molecule.
   *
   * @param  mol               The molecule to convert.
   * @param  coordType         2 or 3.
   * @param  doc               The document this will be part of.
   * @return                   The converted molecule.
   * @exception  CMLException  Description of Exception.
   */
  public static CMLMolecule convert(Molecule mol, int coordType, CMLDocument doc) throws CMLException {
    if (mol != null) {
      MoleculeImpl converted = new MoleculeImpl(doc);
      AtomArrayImpl atomarray = new AtomArrayImpl(doc);
      BondArrayImpl bondarray = new BondArrayImpl(doc);

      int NOatoms = mol.getAtomCount();

      // add atoms
      for (int i = 0; i < NOatoms; i++) {
        atomarray.appendAtom(convert(mol.getAtomAt(i), coordType, mol, doc));
      }

      converted.appendAtomArray(atomarray);

      // add bonds
      for (int i = 0; i < mol.getBondCount(); i++) {
        bondarray.appendBond(convert(mol.getBondAt(i), mol, doc));
      }

      converted.appendBondArray(bondarray);
      doc.appendChild(converted);

      return converted;
    } else {
      return null;
    }
  }
}

