/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2001-2004  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.ChemObject;

/**
 * TestCase for the ChemObject class.
 *
 * @cdkPackage test
 *
 * @author Edgar Luttmann <edgar@uni-paderborn.de>
 * @created 2001-08-09
 */
public class ChemObjectTest extends TestCase {

	public ChemObjectTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ChemObjectTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(ChemObjectTest.class));
	}

    public void testChemObject() {
        ChemObject chemObject = new ChemObject();
        assertNotNull(chemObject);
    }
    
    public void testSetProperty() {
        ChemObject chemObject = new ChemObject();
        String cDescription = new String("description");
        String cProperty = new String("property");
        chemObject.setProperty(cDescription, cProperty);
        assertEquals(cProperty, chemObject.getProperty(cDescription));
    }

    public void testGetProperty() {
        ChemObject chemObject = new ChemObject();
        assertNull(chemObject.getProperty("dummy"));
   }

    public void testRemoveProperty() {
        ChemObject chemObject = new ChemObject();
        String cDescription = new String("description");
        String cProperty = new String("property");
        chemObject.setProperty(cDescription, cProperty);
        assertNotNull(chemObject.getProperty(cDescription));
        chemObject.removeProperty(cDescription);
        assertNull(chemObject.getProperty(cDescription));
    }

    public void testSetID() {
        ChemObject chemObject = new ChemObject();
        String id = "objectX";
        chemObject.setID(id);
        assertEquals(id, chemObject.getID());
    }
    
    public void testClone() {
        ChemObject chemObject = new ChemObject();
        Object clone = chemObject.clone();
        assertTrue(clone instanceof ChemObject);
    }
}
