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
 */
package org.openscience.cdk.io;

import java.io.StringReader;
import java.io.StringWriter;

import javax.vecmath.Vector3d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.interfaces.ICrystal;

/**
 * @cdk.module test-extra
 */
public class ShelXWriterTest extends CDKTestCase {

    @Test
    public void testRoundTrip() throws Exception {
        Crystal crystal = new Crystal();
        double a = 3.0;
        double b = 5.0;
        double c = 7.0;
        double alpha = 90.0;
        double beta = 110.0;
        double gamma = 100.0;
        Vector3d[] axes = CrystalGeometryTools.notionalToCartesian(a, b, c, alpha, beta, gamma);
        crystal.setA(axes[0]);
        crystal.setB(axes[1]);
        crystal.setC(axes[2]);

        // serialazing
        StringWriter sWriter = new StringWriter();
        ShelXWriter resWriter = new ShelXWriter(sWriter);
        resWriter.write(crystal);
        resWriter.close();
        String resContent = sWriter.toString();

        // deserialazing
        ShelXReader resReader = new ShelXReader(new StringReader(resContent));
        ICrystal rCrystal = (ICrystal) resReader.read(new Crystal());

        // OK, do checking
        Assert.assertNotNull(rCrystal);
        Assert.assertEquals(crystal.getA().x, rCrystal.getA().x, 0.001);
        Assert.assertEquals(crystal.getA().y, rCrystal.getA().y, 0.001);
        Assert.assertEquals(crystal.getA().z, rCrystal.getA().z, 0.001);
        Assert.assertEquals(crystal.getB().x, rCrystal.getB().x, 0.001);
        Assert.assertEquals(crystal.getB().y, rCrystal.getB().y, 0.001);
        Assert.assertEquals(crystal.getB().z, rCrystal.getB().z, 0.001);
        Assert.assertEquals(crystal.getC().x, rCrystal.getC().x, 0.001);
        Assert.assertEquals(crystal.getC().y, rCrystal.getC().y, 0.001);
        Assert.assertEquals(crystal.getC().z, rCrystal.getC().z, 0.001);
    }
}
