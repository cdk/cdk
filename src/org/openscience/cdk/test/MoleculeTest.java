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

import org.openscience.cdk.*;
import java.util.*;
import junit.framework.*;
import javax.vecmath.*;

/**
 * Checks the funcitonality of the Molecule class.
 *
 * @cdkPackage test
 *
 * @see org.openscience.cdk.Molecule
 */
public class MoleculeTest extends TestCase {

    public MoleculeTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(MoleculeTest.class);
    }
    
    // test constructors
    
    public void testMolecule() {
        Molecule m = new Molecule();
        assertTrue(m != null);
    }
}
