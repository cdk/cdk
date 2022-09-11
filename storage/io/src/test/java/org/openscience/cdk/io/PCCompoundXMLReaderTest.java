/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io;

import java.io.InputStream;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * @cdk.module test-io
 */
class PCCompoundXMLReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(PCCompoundXMLReaderTest.class);

    @BeforeAll
    static void setup() throws Exception {
        setSimpleChemObjectReader(new PCCompoundXMLReader(), "cid1145.xml");
    }

    @Test
    void testAccepts() throws Exception {
        PCCompoundXMLReader reader = new PCCompoundXMLReader();
        Assertions.assertTrue(reader.accepts(AtomContainer.class));
    }

    @Test
    void testReading() throws Exception {
        String filename = "cid1145.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        PCCompoundXMLReader reader = new PCCompoundXMLReader(ins);
        IAtomContainer molecule = reader.read(new AtomContainer());
        reader.close();
        Assertions.assertNotNull(molecule);

        // check atom stuff
        Assertions.assertEquals(14, molecule.getAtomCount());
        Assertions.assertEquals("O", molecule.getAtom(0).getSymbol());
        Assertions.assertEquals(Integer.valueOf(-1), molecule.getAtom(0).getFormalCharge());
        Assertions.assertEquals("N", molecule.getAtom(1).getSymbol());
        Assertions.assertEquals(Integer.valueOf(1), molecule.getAtom(1).getFormalCharge());

        // check bond stuff
        Assertions.assertEquals(13, molecule.getBondCount());
        Assertions.assertNotNull(molecule.getBond(3));

        // coordinates
        Assertions.assertNull(molecule.getAtom(0).getPoint3d());
        Point2d point = molecule.getAtom(0).getPoint2d();
        Assertions.assertNotNull(point);
        Assertions.assertEquals(3.7320508956909, point.x, 0.00000001);
        Assertions.assertEquals(0.5, point.y, 0.00000001);
    }

    @Test
    void testReading3DCoords() throws Exception {
        String filename = "cid176.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        PCCompoundXMLReader reader = new PCCompoundXMLReader(ins);
        IAtomContainer molecule = reader.read(new AtomContainer());
        reader.close();
        Assertions.assertNotNull(molecule);

        // check atom stuff
        Assertions.assertEquals(8, molecule.getAtomCount());
        Assertions.assertNull(molecule.getAtom(0).getPoint2d());
        Point3d point = molecule.getAtom(0).getPoint3d();
        Assertions.assertNotNull(point);
        Assertions.assertEquals(-0.9598, point.x, 0.0001);
        Assertions.assertEquals(1.5616, point.y, 0.0001);
        Assertions.assertEquals(1.8714, point.z, 0.0001);
    }
}
