/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
package org.openscience.cdk.test.tools;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import junit.framework.*;

public class SaturationCheckerTest extends TestCase {
	
    SaturationChecker satcheck = null;

	public SaturationCheckerTest(String name) {
		super(name);
	}

	public void setUp() {
        try {
            satcheck = new SaturationChecker();
        } catch (Exception e) {
            fail();
        }
    }

	public static Test suite() {
		return new TestSuite(SaturationCheckerTest.class);
	}

    public void testAllSaturated() {
        // test methane with explicit hydrogen
        Molecule m = new Molecule();
        Atom c = new Atom("C");
        Atom h1 = new Atom("H");
        Atom h2 = new Atom("H");
        Atom h3 = new Atom("H");
        Atom h4 = new Atom("H");
        m.addAtom(c);
        m.addAtom(h1);
        m.addAtom(h2);
        m.addAtom(h3);
        m.addAtom(h4);
        m.addBond(new Bond(c,h1));
        m.addBond(new Bond(c,h2));
        m.addBond(new Bond(c,h3));
        m.addBond(new Bond(c,h4));
        assertTrue(satcheck.allSaturated(m));
        
        // test methane with implicit hydrogen
        m = new Molecule();
        c = new Atom("C");
        c.setHydrogenCount(4);
        m.addAtom(c);
        assertTrue(satcheck.allSaturated(m));
    }
    
    public void testIsSaturated() {
        // test methane with explicit hydrogen
        Molecule m = new Molecule();
        Atom c = new Atom("C");
        Atom h1 = new Atom("H");
        Atom h2 = new Atom("H");
        Atom h3 = new Atom("H");
        Atom h4 = new Atom("H");
        m.addAtom(c);
        m.addAtom(h1);
        m.addAtom(h2);
        m.addAtom(h3);
        m.addAtom(h4);
        m.addBond(new Bond(c,h1));
        m.addBond(new Bond(c,h2));
        m.addBond(new Bond(c,h3));
        m.addBond(new Bond(c,h4));
        assertTrue(satcheck.isSaturated(c, m));
        assertTrue(satcheck.isSaturated(h1, m));
        assertTrue(satcheck.isSaturated(h2, m));
        assertTrue(satcheck.isSaturated(h3, m));
        assertTrue(satcheck.isSaturated(h4, m));
    }
 
    public void testSaturate() {
        // test ethene
        Atom c1 = new Atom("C");
        c1.setHydrogenCount(2);
        Atom c2 = new Atom("C");
        c2.setHydrogenCount(2);
        Bond b = new Bond(c1, c2, 1); // force single bond, saturate() must fix that
        Molecule m = new Molecule();
        m.addAtom(c1);
        m.addAtom(c2);
        m.addBond(b);
        satcheck.saturate(m);
        assertTrue(2.0 == b.getOrder());
    }   
}

