/* Copyright (C) 2012-2013  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 */
abstract class AbstractDictionaryTest {

    private Dictionary testClass;

    void setTestClass(Dictionary testClass) {
        this.testClass = testClass;
    }

    Dictionary getTestClass() {
        return this.testClass;
    }

    @Test
    void testSetTestClass() {
        Assertions.assertNotNull(this.testClass);
    }

    @Test
    void testNS() {
        Dictionary dict = getTestClass();
        Assertions.assertNotNull(dict);
        Assertions.assertNull(dict.getNS());
        dict.setNS("http://www.namespace.example.org/");
        Assertions.assertEquals("http://www.namespace.example.org/", dict.getNS());
    }

    @Test
    void testAddEntry() {
        Dictionary dict = getTestClass();
        Assertions.assertNotNull(dict);
        Assertions.assertEquals(0, dict.size());
        Assertions.assertFalse(dict.hasEntry("someidentifier"));
        Entry entry = new Entry();
        entry.setID("someidentifier");
        dict.addEntry(entry);
        Assertions.assertEquals(1, dict.size());
        Assertions.assertTrue(dict.hasEntry("someidentifier"));
        Assertions.assertEquals(entry, dict.getEntry("someidentifier"));
    }

}
