/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Monomer;

/**
 * TestCase for the Monomer class.
 *
 * @cdkPackage test
 *
 * @author  Edgar Luttman <edgar@uni-paderborn.de>
 * @created 2001-08-09
 */
public class MonomerTest extends TestCase {

	public MonomerTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(MonomerTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(MonomerTest.class));
	}

	public void testMonomer() {
		Monomer oMonomer = new Monomer();
                assertTrue(oMonomer != null);
	}
	
	public void testSetMonomerName() {
            Monomer m = new Monomer();
            m.setMonomerName(new String("TRP279"));
            assertEquals(new String("TRP279"), m.getMonomerName());
	}

    public void testSetMonomerType() {
        Monomer oMonomer = new Monomer();
        oMonomer.setMonomerType(new String("TRP"));
        assertEquals(new String("TRP"), oMonomer.getMonomerType());
    }
}
