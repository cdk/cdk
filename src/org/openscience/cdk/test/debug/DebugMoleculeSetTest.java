/* $RCSfile$
 * $Author: egonw $    
 * $Date: 2006-04-12 11:16:35 +0200 (Wed, 12 Apr 2006) $    
 * $Revision: 5921 $
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.debug;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.debug.DebugChemObjectBuilder;
import org.openscience.cdk.test.MoleculeSetTest;

/**
 * Checks the funcitonality of the AtomContainer.
 *
 * @cdk.module test-datadebug
 */
public class DebugMoleculeSetTest extends MoleculeSetTest {

    public DebugMoleculeSetTest(String name) {
        super(name);
    }

    public void setUp() {
    	super.builder = DebugChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(DebugMoleculeSetTest.class);
    }

}
