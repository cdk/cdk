/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.layout;

import org.openscience.cdk.controller.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.templates.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import javax.vecmath.Vector2d;
import javax.vecmath.Point2d;
import junit.framework.*;

public class HydrogenPlacerTest extends TestCase {
    
    public HydrogenPlacerTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(HydrogenPlacerTest.class);
    }

    public void testplaceHydrogens2D() throws Exception {
        Molecule dichloromethane = new Molecule();
        Atom carbon = new Atom("C");
        Point2d carbonPos = new Point2d(0.0,0.0);
        carbon.setPoint2D(carbonPos);
        Atom h1 = new Atom("H");
        Point2d h1Pos = new Point2d(1.0,0.0);
        h1.setPoint2D(h1Pos);
        Atom h2 = new Atom("H");
        Point2d h2Pos = new Point2d(-1.0,0.0);
        h2.setPoint2D(h2Pos);
        Atom cl1 = new Atom("Cl");
        Atom cl2 = new Atom("Cl");
        dichloromethane.addAtom(carbon);
        dichloromethane.addAtom(h1);
        dichloromethane.addAtom(h2);
        dichloromethane.addAtom(cl1);
        dichloromethane.addAtom(cl2);
        dichloromethane.addBond(new Bond(carbon, h1));
        dichloromethane.addBond(new Bond(carbon, h2));
        dichloromethane.addBond(new Bond(carbon, cl1));
        dichloromethane.addBond(new Bond(carbon, cl2));

        // generate new coords
        HydrogenPlacer.placeHydrogens2D(dichloromethane, carbon);
        
        // check that previously set coordinates are kept
        assertEquals(carbonPos, carbon.getPoint2D(), 0.01);
        assertEquals(h1Pos, h1.getPoint2D(), 0.01);
        assertEquals(h2Pos, h2.getPoint2D(), 0.01);
    }
    
    private void assertEquals(Point2d p1, Point2d p2, double error) throws Exception {
        assertEquals(p1.x, p2.x, error);
        assertEquals(p1.y, p2.y, error);
    }
    
}

