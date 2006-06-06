/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-04-19 13:55:06 +0200 (Wed, 19 Apr 2006) $
 * $Revision: 6006 $
 *
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.io;

import java.io.StringReader;
import java.io.StringWriter;

import javax.vecmath.Vector3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Crystal;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.io.ShelXReader;
import org.openscience.cdk.io.ShelXWriter;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module test-io
 */
public class ShelXWriterTest extends CDKTestCase {

    private LoggingTool logger;

    public ShelXWriterTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(ShelXWriterTest.class);
    }

    public void testRoundTrip() {
        Crystal crystal = new Crystal();
        double a = 3.0;
        double b = 5.0;
        double c = 7.0;
        double alpha = 90.0;
        double beta = 110.0;
        double gamma = 100.0;
        Vector3d[] axes = CrystalGeometryTools.notionalToCartesian(a,b,c,alpha,beta,gamma);
        crystal.setA(axes[0]);
        crystal.setB(axes[1]);
        crystal.setC(axes[2]);
        
        // serialazing
        StringWriter sWriter = new StringWriter();
        ShelXWriter resWriter = new ShelXWriter(sWriter);
        try {
			resWriter.write(crystal);
	        resWriter.close();
		} catch (Exception e) {
			logger.debug(e);
			fail(e.getMessage());
		}
        String resContent = sWriter.toString();
        
        // deserialazing
        ShelXReader resReader = new ShelXReader(new StringReader(resContent));
        ICrystal rCrystal = null;
        try {
			rCrystal = (ICrystal)resReader.read(new Crystal());
		} catch (CDKException e) {
			logger.debug(e);
			fail(e.getMessage());
		}
		
		// OK, do checking
		assertNotNull(rCrystal);
		assertEquals(crystal.getA().x, rCrystal.getA().x, 0.001);  
		assertEquals(crystal.getA().y, rCrystal.getA().y, 0.001);  
		assertEquals(crystal.getA().z, rCrystal.getA().z, 0.001);  
		assertEquals(crystal.getB().x, rCrystal.getB().x, 0.001);
		assertEquals(crystal.getB().y, rCrystal.getB().y, 0.001);
		assertEquals(crystal.getB().z, rCrystal.getB().z, 0.001);  
		assertEquals(crystal.getC().x, rCrystal.getC().x, 0.001);  
		assertEquals(crystal.getC().y, rCrystal.getC().y, 0.001);  
		assertEquals(crystal.getC().z, rCrystal.getC().z, 0.001);  
    }
}
