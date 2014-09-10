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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * @cdk.module test-io
 */
public class PCCompoundXMLReaderTest extends SimpleChemObjectReaderTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(PCCompoundXMLReaderTest.class);

    @BeforeClass
    public static void setup() throws Exception {
        setSimpleChemObjectReader(new PCCompoundXMLReader(), "data/asn/pubchem/cid1145.xml");
    }

    @Test
    public void testAccepts() throws Exception {
        PCCompoundXMLReader reader = new PCCompoundXMLReader();
        Assert.assertTrue(reader.accepts(AtomContainer.class));
    }

    @Test
    public void testReading() throws Exception {
        String filename = "data/asn/pubchem/cid1145.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        PCCompoundXMLReader reader = new PCCompoundXMLReader(ins);
        IAtomContainer molecule = (IAtomContainer) reader.read(new AtomContainer());
        reader.close();
        Assert.assertNotNull(molecule);

        // check atom stuff
        Assert.assertEquals(14, molecule.getAtomCount());
        Assert.assertEquals("O", molecule.getAtom(0).getSymbol());
        Assert.assertEquals(Integer.valueOf(-1), molecule.getAtom(0).getFormalCharge());
        Assert.assertEquals("N", molecule.getAtom(1).getSymbol());
        Assert.assertEquals(Integer.valueOf(1), molecule.getAtom(1).getFormalCharge());

        // check bond stuff
        Assert.assertEquals(13, molecule.getBondCount());
        Assert.assertNotNull(molecule.getBond(3));

        // coordinates
        Assert.assertNull(molecule.getAtom(0).getPoint3d());
        Point2d point = molecule.getAtom(0).getPoint2d();
        Assert.assertNotNull(point);
        Assert.assertEquals(3.7320508956909, point.x, 0.00000001);
        Assert.assertEquals(0.5, point.y, 0.00000001);
    }

    @Test
    public void testReading3DCoords() throws Exception {
        String filename = "data/asn/pubchem/cid176.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        PCCompoundXMLReader reader = new PCCompoundXMLReader(ins);
        IAtomContainer molecule = (IAtomContainer) reader.read(new AtomContainer());
        reader.close();
        Assert.assertNotNull(molecule);

        // check atom stuff
        Assert.assertEquals(8, molecule.getAtomCount());
        Assert.assertNull(molecule.getAtom(0).getPoint2d());
        Point3d point = molecule.getAtom(0).getPoint3d();
        Assert.assertNotNull(point);
        Assert.assertEquals(-0.9598, point.x, 0.0001);
        Assert.assertEquals(1.5616, point.y, 0.0001);
        Assert.assertEquals(1.8714, point.z, 0.0001);
    }
}
