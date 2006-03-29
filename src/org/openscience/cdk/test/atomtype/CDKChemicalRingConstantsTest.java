/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.test.atomtype;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.atomtype.CDKChemicalRingConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of the AtomType-MMFF94AtomTypeMatcher.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.atomtype.MMFF94AtomTypeMatcher
 */
public class CDKChemicalRingConstantsTest extends CDKTestCase {

    public CDKChemicalRingConstantsTest (String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(CDKChemicalRingConstantsTest.class);
    }
    
   public void testCDKChemicalRingConstants() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	//System.out.println("**** START CDKChemicalRingConstants TEST ******");
    	assertEquals(3,CDKChemicalRingConstants.IS_IN_RING);
    	assertEquals(6,CDKChemicalRingConstants.FURAN);    	
   }    
}
