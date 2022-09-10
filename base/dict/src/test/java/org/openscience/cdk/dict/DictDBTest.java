/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *                    2011  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.dict;

import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of the DictionaryDatabase class.
 *
 * @cdk.module test-dict
 *
 * @see org.openscience.cdk.dict.DictionaryDatabase
 */
public class DictDBTest extends CDKTestCase {

    @Test
    public void testDictionaryDatabase() {
        DictionaryDatabase db = new DictionaryDatabase();
        Assertions.assertTrue(db.hasDictionary("descriptor-algorithms"));
        Assertions.assertTrue(db.hasDictionary("reaction-processes"));
    }

    @Test
    public void testOWLDictionary() {
        DictionaryDatabase db = new DictionaryDatabase();
        Dictionary dict = db.getDictionary("descriptor-algorithms");
        Assertions.assertTrue(dict.size() > 0);
        Assertions.assertNotNull(dict.getNS());
    }

    @Test
    public void testOWLEntry() {
        DictionaryDatabase db = new DictionaryDatabase();
        Dictionary dict = db.getDictionary("descriptor-algorithms");
        Entry entry = dict.getEntry("apol");
        Assertions.assertNotNull(entry);
        Assertions.assertEquals("Atomic Polarizabilities", entry.getLabel());
        String def = entry.getDefinition();
        Assertions.assertNotNull(def);
        Assertions.assertTrue(def.length() > 0);
    }

    @Test
    public void testOWLReactEntry() {
        DictionaryDatabase db = new DictionaryDatabase();
        Dictionary dict = db.getDictionary("reaction-processes");
        Entry entry = dict.getEntry("AdductionProtonLP".toLowerCase());
        Assertions.assertNotNull(entry);
        Assertions.assertEquals("Adduction Proton from Lone Pair Orbitals", entry.getLabel());
        String def = entry.getDefinition();
        Assertions.assertNotNull(def);
        Assertions.assertTrue(def.length() > 0);
    }

    @Test
    public void testListDictionaries() {
        DictionaryDatabase db = new DictionaryDatabase();
        Iterator<String> dbs = db.listDictionaries();
        Assertions.assertNotNull(dbs);
        Assertions.assertTrue(dbs.hasNext());
        while (dbs.hasNext()) {
            String dbName = dbs.next();
            Assertions.assertNotNull(dbName);
            Assertions.assertNotSame(0, dbName.length());
        }
    }

    @Test
    public void testGetDictionaryNames() {
        DictionaryDatabase db = new DictionaryDatabase();
        String[] dbs = db.getDictionaryNames();
        Assertions.assertNotNull(dbs);
        Assertions.assertNotSame(0, dbs.length);
        for (String dbName : dbs) {
            Assertions.assertNotNull(dbName);
            Assertions.assertNotSame(0, dbName.length());
        }
    }

    @Test
    public void testHasDictionary() {
        DictionaryDatabase db = new DictionaryDatabase();
        Iterator<String> dbs = db.listDictionaries();
        Assertions.assertNotNull(dbs);
        Assertions.assertTrue(dbs.hasNext());
        while (dbs.hasNext()) {
            String dbName = dbs.next();
            Assertions.assertTrue(db.hasDictionary(dbName));
        }
    }
}
