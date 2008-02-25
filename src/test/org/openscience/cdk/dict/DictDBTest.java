/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
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
package org.openscience.cdk.dict;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.dict.Dictionary;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.dict.Entry;
import org.openscience.cdk.CDKTestCase;

/**
 * Checks the functionality of the DictionaryDatabase class.
 *
 * @cdk.module test-extra
 *
 * @see org.openscience.cdk.dict.DictionaryDatabase
 */
public class DictDBTest extends CDKTestCase {

    public DictDBTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(DictDBTest.class);
    }
    
    public void testDictionaryDatabase() {
        DictionaryDatabase db = new DictionaryDatabase();
        assertTrue(db.hasDictionary("chemical"));
        assertTrue(db.hasDictionary("elements"));
        assertTrue(db.hasDictionary("descriptor-algorithms"));
    }
    
    public void testOWLDictionary() {
    	DictionaryDatabase db = new DictionaryDatabase();
    	Dictionary dict = db.getDictionary("descriptor-algorithms");
    	assertTrue(dict.size() > 0);
        assertTrue(dict.getNS() != null);
    }
    
    public void testOWLEntry() {
    	DictionaryDatabase db = new DictionaryDatabase();
    	Dictionary dict = db.getDictionary("descriptor-algorithms");
    	Entry entry = dict.getEntry("apol");
    	assertNotNull(entry);
    	assertEquals("Atomic Polarizabilities", entry.getLabel());
    	String def = entry.getDefinition();
    	assertNotNull(def);
    	assertTrue(def.length() > 0);
    }
    
}
