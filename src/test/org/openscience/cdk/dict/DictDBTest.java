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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;

/**
 * Checks the functionality of the DictionaryDatabase class.
 *
 * @cdk.module test-dict
 *
 * @see org.openscience.cdk.dict.DictionaryDatabase
 */
public class DictDBTest extends NewCDKTestCase {

    @Test public void testDictionaryDatabase() {
        DictionaryDatabase db = new DictionaryDatabase();
        Assert.assertTrue(db.hasDictionary("chemical"));
        Assert.assertTrue(db.hasDictionary("elements"));
        Assert.assertTrue(db.hasDictionary("descriptor-algorithms"));
        Assert.assertTrue(db.hasDictionary("reaction-processes"));
    }
    
    @Test public void testOWLDictionary() {
    	DictionaryDatabase db = new DictionaryDatabase();
    	Dictionary dict = db.getDictionary("descriptor-algorithms");
    	Assert.assertTrue(dict.size() > 0);
    	Assert.assertTrue(dict.getNS() != null);
    }
    
    @Test public void testOWLEntry() {
    	DictionaryDatabase db = new DictionaryDatabase();
    	Dictionary dict = db.getDictionary("descriptor-algorithms");
    	Entry entry = dict.getEntry("apol");
    	Assert.assertNotNull(entry);
    	Assert.assertEquals("Atomic Polarizabilities", entry.getLabel());
    	String def = entry.getDefinition();
    	Assert.assertNotNull(def);
    	Assert.assertTrue(def.length() > 0);
    }

    @Test public void testOWLReactEntry() {
    	DictionaryDatabase db = new DictionaryDatabase();
    	Dictionary dict = db.getDictionary("reaction-processes");
    	Entry entry = dict.getEntry("AdductionProtonLP".toLowerCase());
    	Assert.assertNotNull(entry);
    	Assert.assertEquals("Adduction Proton from Lone Pair Orbitals", entry.getLabel());
    	String def = entry.getDefinition();
    	Assert.assertNotNull(def);
    	Assert.assertTrue(def.length() > 0);
    }
    
}
