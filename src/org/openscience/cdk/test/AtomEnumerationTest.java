/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomEnumeration;

/**
 * Checks the funcitonality of the AtomEnumeration class.
 *
 * @cdkPackage test
 *
 * @see org.openscience.cdk.AtomEnumeration
 */
public class AtomEnumerationTest extends TestCase {

    public AtomEnumerationTest(String name) {
        super(name);
    }

    public void setUp() {}
    
    public void testHasMoreElements() {
	AtomContainer ac = new AtomContainer();
	ac.addAtom(new Atom("C"));
	ac.addAtom(new Atom("C"));
	ac.addAtom(new Atom("C"));
        AtomEnumeration enum = (AtomEnumeration)ac.atoms();
        assertTrue(enum.hasMoreElements());
        Atom a1 = (Atom)enum.nextElement();
        assertTrue(enum.hasMoreElements());
        Atom a2 = (Atom)enum.nextElement();
        assertTrue(enum.hasMoreElements());
        Atom a3 = (Atom)enum.nextElement();
        assertTrue(!enum.hasMoreElements());
    }

    public static Test suite() {
        return new TestSuite(AtomEnumerationTest.class);
    }
}
