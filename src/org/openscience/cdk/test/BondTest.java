/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
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
 * 
 */

package org.openscience.cdk.test;

import org.openscience.cdk.*;
import java.util.*;
import junit.framework.*;
import javax.vecmath.*;

/**
 * Checks the funcitonality of the Bond class.
 *
 * @see org.openscience.cdk.Bond
 */
public class BondTest extends TestCase {

    public BondTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(BondTest.class);
    }
    
    public void testCompare() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        Bond b2 = new Bond(c, o, 2.0); // same C=O bond
        
        assertTrue(b.compare(b2));
    }
    
    public void testContains() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        
        assertTrue(b.contains(c));
        assertTrue(b.contains(o));
    }
    
    public void testGetAtomCount() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        
        assertTrue(2.0 == b.getAtomCount());
    }
    
    public void testGetAtoms() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        
        Atom[] atoms = b.getAtoms();
        assertTrue(2.0 == atoms.length);
        assertEquals(c, atoms[0]);
        assertEquals(o, atoms[1]);
    }
    
    public void testGetConnectedAtom() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        
        assertEquals(c, b.getConnectedAtom(o));
        assertEquals(o, b.getConnectedAtom(c));
    }
    
    public void testGetOrder() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        
        assertTrue(2.0 == b.getOrder());
        
        b.setOrder(400.0);
        assertTrue(400.0 == b.getOrder());
    }
    
}
