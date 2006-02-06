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
    
    /**
     * Determines if slow JUnit tests are to be run. You can set this
     * from the command line when running Ant: 
     * <pre>
     *   ant -f build.xml -DrunSlowTests=false test-all
     * </pre>
     * 
     * @return
     */
    public boolean runSlowTests() {
    	if (System.getProperty("runSlowTests", "true").equals("false")) 
    		return false;
    	
    	// else
    	return true;
    }

    /**
     * Determines if JUnit tests for known and unfixed bugs are to be run.
     * This is to aid the 'Open Source JVM Test Suite', so that all bugs that
     * show up in the report are caused by the JVM or the Java library is
     * uses (mostly a Classpath version).
     *  
     * <p>You can set this from the command line when running Ant: 
     * <pre>
     *   ant -f build.xml -DrunKnownBugs=false test-all
     * </pre>
     * 
     * @return
     */
    public boolean runKnownBugs() {
    	if (System.getProperty("runKnownBugs", "true").equals("false")) 
    		return false;
    	
    	// else
    	return true;
    }

    /**
     * Compares two Point2d objects, and asserts that the XY coordinates
     * are identical within the given error.
     * 
     * @param p1    first Point2d
     * @param p2    second Point2d
     * @param error maximal allowed error
     */
    public void assertEquals(Point2d p1, Point2d p2, double error) {
        assertEquals(p1.x, p2.x, error);
        assertEquals(p1.y, p2.y, error);
    }
        
    /**
     * Compares two Point3d objects, and asserts that the XY coordinates
     * are identical within the given error.
     * 
     * @param p1    first Point3d
     * @param p2    second Point3d
     * @param error maximal allowed error
     */
    public void assertEquals(Point3d p1, Point3d p2, double error) {
        assertEquals(p1.x, p2.x, error);
        assertEquals(p1.y, p2.y, error);
        assertEquals(p1.z, p2.z, error);
    }
        
}
