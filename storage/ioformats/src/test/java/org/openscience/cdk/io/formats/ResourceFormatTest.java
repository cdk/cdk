/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@slists.sourceforge.net
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
 */
package org.openscience.cdk.io.formats;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @cdk.module test-ioformats
 */
abstract public class ResourceFormatTest {

    private IResourceFormat resourceFormat;

    public void setResourceFormat(IResourceFormat format) {
        this.resourceFormat = format;
    }

    @Test
    public void testResourceFormatSet() {
        Assert.assertNotNull("You must use setResourceFormatSet() to set the resourceFormat object.", resourceFormat);
    }

    @Test
    public void testGetMIMEType() {
        if (resourceFormat.getMIMEType() == null) {
            // OK, that's fine
        } else {
            Assert.assertNotSame(0, resourceFormat.getMIMEType().length());
        }
    }

    @Test
    public void testGetFormatName() {
        Assert.assertNotNull(resourceFormat.getFormatName());
        Assert.assertNotSame(0, resourceFormat.getFormatName().length());
    }

    @Test
    public void testGetPreferredNameExtension() {
        if (resourceFormat.getPreferredNameExtension() == null) {
            if (resourceFormat.getNameExtensions() == null || resourceFormat.getNameExtensions().length == 0) {
                // Seems to be current practice
                // FIXME: needs to be discussed
            } else {
                Assert.fail("This format define file name extensions (getNameExtensions()), but does not provide a prefered extension (getPreferredNameExtension()).");
            }
        } else {
            String prefExtension = resourceFormat.getPreferredNameExtension();
            Assert.assertNotSame(0, prefExtension.length());
            Assert.assertNotNull(
                    "This format defines a preferred file name extension (getPreferredNameExtension()), but does not provide a full list of extensions (getNameExtensions()).",
                    resourceFormat.getNameExtensions());
            String[] allExtensions = resourceFormat.getNameExtensions();
            boolean prefExtInAllExtList = false;
            for (int i = 0; i < allExtensions.length; i++) {
                if (allExtensions[i].equals(prefExtension)) prefExtInAllExtList = true;
            }
            Assert.assertTrue("The preferred extension is not found in the list of all extensions", prefExtInAllExtList);
        }
    }

    @Test
    public void testGetNameExtensions() {
        if (resourceFormat.getNameExtensions() == null) {
            // Seems to be current practice
            // FIXME: needs to be discussed
        } else if (resourceFormat.getNameExtensions().length == 0) {
            // Seems to be current practice
            // FIXME: needs to be discussed
        } else {
            String[] exts = resourceFormat.getNameExtensions();
            for (int i = 0; i < exts.length; i++) {
                String extension = exts[i];
                Assert.assertNotNull(extension);
                Assert.assertNotSame(0, extension.length());
                assertFalse("File name extensions should not contain ',' characters", extension.contains(","));
                assertFalse("File name extensions should not contain '.' characters", extension.contains("."));
            }
        }
    }

    @Test
    public void testHashCode() throws IllegalAccessException, InstantiationException {
        IResourceFormat a = resourceFormat.getClass().newInstance();
        IResourceFormat b = resourceFormat.getClass().newInstance();
        assertThat(a.hashCode(), is(b.hashCode()));
    }

    @Test
    public void testEquals() throws IllegalAccessException, InstantiationException {
        IResourceFormat a = resourceFormat.getClass().newInstance();
        IResourceFormat b = resourceFormat.getClass().newInstance();
        assertThat(a, is(b));
    }

    @Test
    public void testEquals_null() throws IllegalAccessException, InstantiationException {
        IResourceFormat a = resourceFormat.getClass().newInstance();
        Assert.assertNotNull(a);
    }
}
