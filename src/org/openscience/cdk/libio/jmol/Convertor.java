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
package org.openscience.cdk.libio.jmol;

import org.openscience.jmol.Atom;
import org.openscience.jmol.BaseAtomType;
import org.openscience.cdk.exception.NoSuchAtomException;

/**
 * Abstract class that provides convertor procedures to
 * convert CDK classes to Jmol classes and visa versa.
 *
 * Jmol is a Java 3D viewer specialized for viewing
 * animations and vibrational modes. It
 * can be found at: http://jmol.sourceforge.net/
 *
 * @author     egonw
 *
 * @keyword    Jmol
 * @keyword    class convertor
 */
public class Convertor {

    /**
     * Converts an org.openscience.cdk.Atom class into a
     * org.openscience.jmol.Atom class.
     *
     * Conversion includes:
     *   - atomic number
     *   - coordinates
     *
     * @param   atom    class to be converted
     * @return          converted class in Jmol
     **/
    public static org.openscience.jmol.Atom convert(org.openscience.cdk.Atom atom) {
        if (atom != null) {
            org.openscience.jmol.Atom convertedAtom = 
                new org.openscience.jmol.Atom(
                    BaseAtomType.get(atom.getSymbol()),
                    atom.getAtomicNumber());
            if (atom.getPoint3D() != null) {
                convertedAtom.setPosition(new javax.vecmath.Point3f(atom.getPoint3D()));
            } else if (atom.getPoint2D() != null) {
                javax.vecmath.Point3f xyz =
                    new javax.vecmath.Point3f(
                        (float)atom.getX2D(),
                        (float)atom.getY2D(),
                        (float)0.0
                    );
                convertedAtom.setPosition(xyz);
            } else {
                javax.vecmath.Point3f xyz = 
                    new javax.vecmath.Point3f((float)0.0, (float)0.0, (float)0.0);
                convertedAtom.setPosition(xyz);
            }
            return convertedAtom;
        } else {
            return null;
        }
    }

    /**
     * Converts an org.openscience.jmol.Atom class into a
     * org.openscience.cdk.Atom class.
     *
     * Conversion includes:
     *   - atomic number
     *   - coordinates
     *
     * @param   atom    class to be converted
     * @return          converted class in CDK
     **/
    public static org.openscience.cdk.Atom convert(org.openscience.jmol.Atom atom) {
        if (atom != null) {
            org.openscience.cdk.Atom convertedAtom = new org.openscience.cdk.Atom("C");
            // try to give the atom the correct symbol
            convertedAtom = new org.openscience.cdk.Atom(atom.getType().getName());
            try {
                // try to give the atom its coordinates
                convertedAtom.setPoint3D(new javax.vecmath.Point3d(atom.getPosition()));
            } catch (java.lang.Exception e) {
            }
            try {
                // try to give the atom its atomic number
                convertedAtom.setAtomicNumber(atom.getType().getAtomicNumber());
            } catch (java.lang.Exception e) {
                // System.out.println("AtomicNumber failed");
            }
            return convertedAtom;
        } else {
            return null;
        }
    }

}
