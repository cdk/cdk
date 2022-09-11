/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.io;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * @cdk.module test-io
 */
public class ShelXReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(ShelXReaderTest.class);

    @BeforeAll
    public static void setup() throws Exception {
        setSimpleChemObjectReader(new ShelXReader(), "frame_1.res");
    }

    @Test
    public void testAccepts() {
        ShelXReader reader = new ShelXReader();
        Assertions.assertTrue(reader.accepts(ChemFile.class));
        Assertions.assertTrue(reader.accepts(Crystal.class));
    }

    @Test
    public void testReading() throws Exception {
        String filename = "frame_1.res";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ShelXReader reader = new ShelXReader(ins);
        Crystal crystal = reader.read(new Crystal());
        reader.close();
        Assertions.assertNotNull(crystal);
        Assertions.assertEquals(42, crystal.getAtomCount());
        double[] notional = CrystalGeometryTools.cartesianToNotional(crystal.getA(), crystal.getB(), crystal.getC());
        Assertions.assertEquals(7.97103, notional[0], 0.001);
        Assertions.assertEquals(18.77220, notional[1], 0.001);
        Assertions.assertEquals(10.26222, notional[2], 0.001);
        Assertions.assertEquals(90.0000, notional[3], 0.001);
        Assertions.assertEquals(90.0000, notional[4], 0.001);
        Assertions.assertEquals(90.0000, notional[5], 0.001);
    }
}
