/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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
        JOEAtom convertedAtom = new JOEAtom();
        if (atom.getPoint3D() != null) {
            convertedAtom.setVector(
                atom.getX3D(),
                atom.getY3D(),
                atom.getZ3D()
            );
        } else {
            convertedAtom.setVector(
                atom.getX2D(),
                atom.getY2D(),
                0.0
            );
        }
        convertedAtom.setAtomicNum(atom.getAtomicNumber());
        return convertedAtom;
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
     * @returns         converted class in JOELib
     **/
    public static Atom convert(JOEAtom atom) {
        Atom convertedAtom = new Atom();
        convertedAtom.setX3D(atom.x());
        convertedAtom.setY3D(atom.y());
        convertedAtom.setZ3D(atom.z());
        convertedAtom.setAtomicNumber(atom.getAtomicNum());
        return convertedAtom;
    }
}
