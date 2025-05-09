/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@slists.sourceforge.net
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
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.test.io.ChemObjectIOTest;

/**
 * TestCase for the writer XYZ files using one test file.
 *
 *
 * @see org.openscience.cdk.io.XYZWriter
 */
class XYZWriterTest extends ChemObjectIOTest {

    @BeforeAll
    static void setup() throws Exception {
        setChemObjectIO(new XYZWriter());
    }

    @Test
    void testAccepts() throws Exception {
        XYZWriter reader = new XYZWriter();
        Assertions.assertTrue(reader.accepts(AtomContainer.class));
    }

    @Test
    void testWriting() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1.0, 2.0, 3.0));
        IAtom atom2 = new Atom("C");
        atom2.setPoint3d(new Point3d(1.0, 2.0, 3.0));
        molecule.addAtom(atom1);
        molecule.addAtom(atom2);

        XYZWriter xyzWriter = new XYZWriter(writer);
        xyzWriter.write(molecule);
        xyzWriter.close();
        writer.close();

        String output = writer.toString();
        //        logger.debug(output);
        // count lines
        int lineCount = 0;
        BufferedReader reader = new BufferedReader(new StringReader(output));
        while (reader.readLine() != null)
            lineCount++;
        Assertions.assertEquals(4, lineCount);
    }

    /**
     * @cdk.bug 2215774
     */
    @Test
    void testWriting_Point2d() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1.0, 2.0));
        molecule.addAtom(atom1);

        XYZWriter xyzWriter = new XYZWriter(writer);
        xyzWriter.write(molecule);
        xyzWriter.close();
        writer.close();

        String output = writer.toString();
        Assertions.assertTrue(output.contains("0.000000\t 0.000000\t 0.000000"));
    }

    /**
     * @cdk.bug 2215775
     */
    @Test
    void testSixDecimalOuput() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1.0, 2.0, 3.0));
        molecule.addAtom(atom1);
        IAtom atom2 = new Atom("C");
        atom2.setPoint3d(new Point3d(-1.5, -2.0, 0.0));
        molecule.addAtom(atom2);

        XYZWriter xyzWriter = new XYZWriter(writer);
        xyzWriter.write(molecule);
        xyzWriter.close();
        writer.close();

        String output = writer.toString();
        Assertions.assertTrue(output.contains("1.000000"));
        Assertions.assertTrue(output.contains("2.000000"));
        Assertions.assertTrue(output.contains("3.000000"));
    }

}
