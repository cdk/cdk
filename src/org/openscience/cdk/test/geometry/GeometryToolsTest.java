/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test.geometry;

import org.openscience.cdk.*;
import org.openscience.cdk.geometry.*;
import javax.vecmath.*;
import junit.framework.*;

/**
 * This class defines regression tests that should ensure that the source code
 * of the org.openscience.cdk.geometry.GeometryTools is not broken.
 *
 * @cdkPackage test
 *
 * @author     Egon Willighagen
 * @created    2004-01-30
 *
 * @see org.openscience.cdk.geometry.GeometryTools
 */
public class GeometryToolsTest extends TestCase {

    public GeometryToolsTest(String name) {
        super(name);
    }
    
    public void setUp() {}
    
    /**
     * Defines a set of tests that can be used in automatic regression testing
     * with JUnit.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GeometryToolsTest.class);
        return suite;
    }
    
    public void testGetLength() {
        Atom o = new Atom("O", new Point2d(0.0, 0.0));
        Atom c = new Atom("C", new Point2d(1.0, 0.0));
        Bond bond = new Bond(c,o);
        
        assertTrue(1.0 == GeometryTools.getLength2D(bond));
    }
    
}

