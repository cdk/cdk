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
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * TestCase for the Monomer class.
 *
 * @cdk.module test-data
 *
 * @author  Edgar Luttman <edgar@uni-paderborn.de>
 * @cdk.created 2001-08-09
 */
public class MonomerTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
	public MonomerTest(String name) {
		super(name);
	}

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
		return new TestSuite(MonomerTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(MonomerTest.class));
	}

	public void testMonomer() {
		IMonomer oMonomer = builder.newMonomer();
        assertTrue(oMonomer != null);
	}
	
	public void testSetMonomerName_String() {
        IMonomer m = builder.newMonomer();
        m.setMonomerName(new String("TRP279"));
        assertEquals(new String("TRP279"), m.getMonomerName());
	}
    public void testGetMonomerName() {
        testSetMonomerName_String();
    }
    
    public void testSetMonomerType_String() {
        IMonomer oMonomer = builder.newMonomer();
        oMonomer.setMonomerType(new String("TRP"));
        assertEquals(new String("TRP"), oMonomer.getMonomerType());
    }
    public void testGetMonomerType() {
        testSetMonomerType_String();
    }

    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        IMonomer oMonomer = builder.newMonomer();
        oMonomer.setMonomerType(new String("TRP"));
        String description = oMonomer.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue('\n' != description.charAt(i));
            assertTrue('\r' != description.charAt(i));
        }
    }

    public void testClone() throws Exception {
        IMonomer oMonomer = builder.newMonomer();
        Object clone = oMonomer.clone();
        assertTrue(clone instanceof IMonomer);
        assertNotSame(oMonomer, clone);
    }
}
