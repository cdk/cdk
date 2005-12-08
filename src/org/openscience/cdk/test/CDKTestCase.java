/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.test;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import junit.framework.TestCase;

import org.openscience.cdk.tools.LoggingTool;

/**
 * Super class for <b>all</b> CDK TestCase implementations that ensures that
 * the LoggingTool is configured.
 *
 * @cdk.module test
 */
public class CDKTestCase extends TestCase {

    static {
        LoggingTool.configureLog4j();
    }

    public CDKTestCase() {
        super();
    }
    
    public CDKTestCase(String name) {
        super(name);
    }
    
    public boolean runSlowTests() {
    	if (System.getProperty("runSlowTests", "false").equals("true")) 
    		return true;
    	
    	// else
    	return false;
    }

    public void assertEquals(Point2d p1, Point2d p2, double error) {
        assertEquals(p1.x, p2.x, error);
        assertEquals(p1.y, p2.y, error);
    }
        
    public void assertEquals(Point3d p1, Point3d p2, double error) {
        assertEquals(p1.x, p2.x, error);
        assertEquals(p1.y, p2.y, error);
        assertEquals(p1.z, p2.z, error);
    }
        
}
