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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * @cdk.module test-dict
 */
public abstract class AbstractEntryTest extends CDKTestCase {

    private Entry testClass;

    protected void setTestClass(Entry testClass) {
        this.testClass = testClass;
    }

    protected Entry getTestClass() {
        return this.testClass;
    }

    @Test
    public void testSetTestClass() {
        Assert.assertNotNull(this.testClass);
    }

    @Test
    public void testToString() {
        Entry entry = getTestClass();
        entry.setID("testid");
        entry.setLabel("testTerm");
        Assert.assertNotNull(entry);
        Assert.assertEquals("Entry[testid](testTerm)", entry.toString());
    }

    @Test
    public void testLabel() {
        Entry entry = getTestClass();
        Assert.assertEquals("", entry.getLabel());
        entry.setLabel("label");
        Assert.assertEquals("label", entry.getLabel());
    }

    @Test
    public void testID() {
        Entry entry = getTestClass();
        Assert.assertEquals("", entry.getID());
        entry.setID("identifier");
        Assert.assertEquals("identifier", entry.getID());
    }

    @Test
    public void testDefinition() {
        Entry entry = getTestClass();
        Assert.assertNull(entry.getDefinition());
        entry.setDefinition("This is a definition.");
        Assert.assertEquals("This is a definition.", entry.getDefinition());
    }

    @Test
    public void testDescriptorMetadata() {
        Entry entry = getTestClass();
        Assert.assertNotNull(entry.getDescriptorMetadata());
        List<String> metadata = entry.getDescriptorMetadata();
        Assert.assertEquals(0, metadata.size());
        entry.setDescriptorMetadata("This entry was written by me.");
        metadata = entry.getDescriptorMetadata();
        Assert.assertEquals(1, metadata.size());
    }

    @Test
    public void testDescription() {
        Entry entry = getTestClass();
        Assert.assertNull(entry.getDescription());
        entry.setDescription("This is a description.");
        Assert.assertEquals("This is a description.", entry.getDescription());
    }

    @Test
    public void testClassName() {
        Entry entry = getTestClass();
        Assert.assertNull(entry.getClassName());
        entry.setClassName("org.openscience.cdk.DoesNotExist");
        Assert.assertEquals("org.openscience.cdk.DoesNotExist", entry.getClassName());
    }

    @Test
    public void testRawContent() {
        Entry entry = getTestClass();
        Assert.assertNull(entry.getRawContent());
        Object someObject = new Double(5);
        entry.setRawContent(someObject);
        Assert.assertEquals(someObject, entry.getRawContent());
    }

}
