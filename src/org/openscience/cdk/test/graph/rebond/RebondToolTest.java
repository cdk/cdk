/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
 * 
 */
package org.openscience.cdk.test.graph.rebond;

import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.graph.rebond.RebondTool;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the funcitonality of the RebondTool.
 *
 * @cdk.module test
 */
public class RebondToolTest extends CDKTestCase {
    
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
    methane.addAtom(new Atom("H", new Point3d(0.6, 0.6, 0.6)));
    methane.addAtom(new Atom("H", new Point3d(-0.6, -0.6, 0.6)));
    methane.addAtom(new Atom("H", new Point3d(0.6, -0.6, -0.6)));
    methane.addAtom(new Atom("H", new Point3d(-0.6, 0.6, -0.6)));
        
    try {
      // configure atoms
      AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/jmol_atomtypes.txt", 
          methane.getBuilder());
      org.openscience.cdk.interfaces.IAtom[] atoms = methane.getAtoms();
      for (int i=0; i<atoms.length; i++) {
        factory.configure(atoms[i]);
      }
      // rebond
      rebonder.rebond(methane);
    } catch (Exception exception) {
      fail();
            
    }
        
    assertEquals(5, methane.getAtomCount());
    assertEquals(4, methane.getBondCount());
  }
}
