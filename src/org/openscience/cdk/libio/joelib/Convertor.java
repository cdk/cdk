/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.libio.joelib;

import org.openscience.cdk.Atom;
import joelib.molecule.JOEAtom;
import org.openscience.cdk.Bond;
import joelib.molecule.JOEBond;
import org.openscience.cdk.Molecule;
import joelib.molecule.JOEMol;
import org.openscience.cdk.exception.NoSuchAtomException;

/**
 * Abstract class that provides convertor procedures to
 * convert CDK classes to JOELib classes and visa versa.
 *
 * JOELib is a Java implementation of the OELib classes and
 * can be found at: http://joelib.sourceforge.net/
 *
 * @author     egonw
 *
 * @keyword    JOELib
 * @keyword    class convertor
 */
public class Convertor {

    /**
     * Converts an org.openscience.cdk.Atom class into a
     * joelib.molecule.JOEAtom class.
     *
     * Conversion includes:
     *   - atomic number
     *   - coordinates
     *
     * @param   atom    class to be converted
     * @returns         converted class in JOELib
     **/
    public static JOEAtom convert(Atom atom) {
        if (atom != null) {
            JOEAtom convertedAtom = new JOEAtom();
            if (atom.getPoint3D() != null) {
                convertedAtom.setVector(
                    atom.getX3D(),
                    atom.getY3D(),
                    atom.getZ3D()
                );
            } else if (atom.getPoint2D() != null) {
                convertedAtom.setVector(
                    atom.getX2D(),
                    atom.getY2D(),
                    0.0
                );
            } else {
                convertedAtom.setVector(0.0, 0.0, 0.0);
            }
            convertedAtom.setAtomicNum(atom.getAtomicNumber());
            return convertedAtom;
        } else {
            return null;
        }
    }

    /**
     * Converts an joelib.molecule.JOEAtom class into a
     * org.openscience.cdk.Atom class.
     *
     * Conversion includes:
     *   - atomic number
     *   - coordinates
     *
     * @param   atom    class to be converted
     * @returns         converted class in CDK
     **/
    public static Atom convert(JOEAtom atom) {
        if (atom != null) {
            Atom convertedAtom = new Atom("C");
            try {
                // try to give the atom the correct symbol
                org.openscience.cdk.tools.IsotopeFactory ef =
                    org.openscience.cdk.tools.IsotopeFactory.getInstance();
                org.openscience.cdk.Element e = ef.getElement(atom.getAtomicNum());
                convertedAtom = new Atom(e.getSymbol());
            } catch (java.lang.Exception e) {
            }
            try {
                // try to give the atom its coordinates
                convertedAtom.setX3D(atom.getVector().x());
                convertedAtom.setY3D(atom.getVector().y());
                convertedAtom.setZ3D(atom.getVector().z());
            } catch (java.lang.Exception e) {
            }
            try {
                // try to give the atom its atomic number
                convertedAtom.setAtomicNumber(atom.getAtomicNum());
            } catch (java.lang.Exception e) {
                // System.out.println("AtomicNumber failed");
            }
            return convertedAtom;
        } else {
            return null;
        }
    }

    /**
     * Converts an org.openscience.cdk.Bond class into a
     * joelib.molecule.JOEBond class.
     *
     * Conversion includes:
     *   - atoms which it conects
     *   - bond order
     *
     * @param   atom    class to be converted
     * @returns         converted class in JOELib
     **/
    public static JOEBond convert(Bond bond) {
        if (bond != null) {
            JOEBond convertedBond = new JOEBond();
            convertedBond.setBegin(convert(bond.getAtomAt(0)));
            convertedBond.setEnd(convert(bond.getAtomAt(1)));
            convertedBond.setBO((int)bond.getOrder());
            return convertedBond;
        } else {
            return null;
        }
    }

    /**
     * Converts an joelib.molecule.JOEBond class into a
     * org.openscience.cdk.Bond class.
     *
     * Conversion includes:
     *   - atoms which it conects
     *   - bond order
     *
     * @param   atom    class to be converted
     * @returns         converted class in CDK
     **/
    public static Bond convert(JOEBond bond) {
        if (bond != null) {
            Bond convertedBond = new Bond(
                                    convert(bond.getBeginAtom()),
                                    convert(bond.getEndAtom()),
                                    (double)bond.getBondOrder());
            return convertedBond;
        } else {
            return null;
        }
    }

    /**
     * Converts an org.openscience.cdk.Molecule class into a
     * joelib.molecule.JOEMol class.
     *
     * Conversion includes:
     *   - atoms
     *   - bonds
     *
     * @param   atom    class to be converted
     * @returns         converted class in JOELib
     **/
    public static JOEMol convert(Molecule mol) {
        if (mol != null) {
            JOEMol converted = new JOEMol();
            int NOatoms = mol.getAtomCount();
            for (int i=0; i<NOatoms; i++) {
                converted.addAtom(convert(mol.getAtomAt(i)));
            }
            try {
                double[][] matrix = mol.getConnectionMatrix();
                for (int i=0; i<NOatoms-1; i++) {
                    for (int j=i+1; j<NOatoms; j++) {
                        if (matrix[i][j] != 0.0) {
                            // atoms i,j are connected
                            /* JOEMol.addBond() needs atom ids [1,...] */
                            converted.addBond(i+1,j+1, (int)matrix[i][j]);
                        } else {
                        }
                    }
                }
            } catch (NoSuchAtomException e) {
                // this is stupid, see bug #590570
            }
            return converted;
        } else {
            return null;
        }
    }

    /**
     * Converts an joelib.molecule.JOEMol class into a
     * org.openscience.cdk.Molecule class.
     *
     * Conversion includes:
     *   - atoms
     *   - bonds
     *
     * @param   atom    class to be converted
     * @returns         converted class in CDK
     **/
    public static Molecule convert(JOEMol mol) {
        if (mol != null) {
            Molecule converted = new Molecule();
            int NOatoms = mol.numAtoms();
            for (int i=1; i<=NOatoms; i++) {
                /* JOEMol.getAtom() needs ids [1,...] */
                JOEAtom a = mol.getAtom(i);
                Atom cdka = convert(a);
                converted.addAtom(cdka);
            }
            int NObonds = mol.numBonds();
            for (int i=1; i<=NObonds; i++) {
                /* JOEMol.getBond() needs ids [0,...] */
                JOEBond b = mol.getBond(i-1);
                /* Molecule.addBond() need atom ids [0,...] */
                converted.addBond(b.getBeginAtomIdx()-1,
                                  b.getEndAtomIdx()-1,
                                  b.getBondOrder());
            }
            return converted;
        } else {
            return null;
        }
    }

}
