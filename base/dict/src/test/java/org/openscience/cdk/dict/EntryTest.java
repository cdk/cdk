/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.jupiter.api.AfterEach;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @cdk.module test-dict
 */
public class EntryTest extends AbstractEntryTest {

    @BeforeEach
    public void setTestClass() {
        super.setTestClass(new Entry());
    }

    @AfterEach
    public void testTestedClass() {
        Assertions.assertTrue(super.getTestClass().getClass().getName().endsWith(".Entry"));
    }

    @Test
    public void testConstructor() {
        Entry entry = new Entry();
        Assertions.assertNotNull(entry);
    }

    @Test
    public void testConstructor_String_String() {
        Entry entry = new Entry("testid", "testTerm");
        Assertions.assertNotNull(entry);
        Assertions.assertEquals(entry.getID(), "testid");
        Assertions.assertEquals(entry.getLabel(), "testTerm");
    }

    @Test
    public void testConstructor_String() {
        Entry entry = new Entry("testid");
        Assertions.assertNotNull(entry);
        Assertions.assertEquals(entry.getID(), "testid");
        Assertions.assertEquals(entry.getLabel(), "");
    }

    @Test
    public void testConstructor_IDLowerCasing() {
        Entry entry = new Entry("testID", "testTerm");
        Assertions.assertNotNull(entry);
        Assertions.assertEquals(entry.getID(), "testid");
        Assertions.assertEquals(entry.getLabel(), "testTerm");
    }

}
