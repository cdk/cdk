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
 * 
 */
package org.openscience.cdk.test.graph.rebond;

import org.openscience.cdk.*;
import org.openscience.cdk.graph.rebond.RebondTool;
import junit.framework.*;
import javax.vecmath.Point3d;

/**
 * Checks the funcitonality of the RebondTool.
 */
public class RebondToolTest extends TestCase {
    
    public RebondToolTest(String name) {
        super(name);
    }
    
    public void setUp() {}

    public static Test suite() {
        return new TestSuite(RebondToolTest.class);
    }

    public void testRebond() {
        RebondTool rebonder = new RebondTool(2.0, 0.5, 0.5);
        Molecule methane = new Molecule();
        methane.addAtom(new Atom("C", new Point3d(0.0, 0.0, 0.0)));
        methane.addAtom(new Atom("H", new Point3d(1.0, 1.0, 1.0)));
        methane.addAtom(new Atom("H", new Point3d(-1.0, -1.0, 1.0)));
        methane.addAtom(new Atom("H", new Point3d(1.0, -1.0, -1.0)));
        methane.addAtom(new Atom("H", new Point3d(-1.0, 1.0, -1.0)));
        
        rebonder.rebond(methane);
        assertEquals(5, methane.getAtomCount());
        assertEquals(4, methane.getBondCount());
    }
}
