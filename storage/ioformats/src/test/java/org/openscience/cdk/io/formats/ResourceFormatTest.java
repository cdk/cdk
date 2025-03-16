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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 */
abstract class ResourceFormatTest {

    private IResourceFormat resourceFormat;

    void setResourceFormat(IResourceFormat format) {
        this.resourceFormat = format;
    }

    @Test
    void testResourceFormatSet() {
        Assertions.assertNotNull(resourceFormat, "You must use setResourceFormatSet() to set the resourceFormat object.");
    }

    @Test
    void testGetMIMEType() {
        if (resourceFormat.getMIMEType() == null) {
            // OK, that's fine
        } else {
            Assertions.assertNotSame(0, resourceFormat.getMIMEType().length());
        }
    }

    @Test
    void testGetFormatName() {
        Assertions.assertNotNull(resourceFormat.getFormatName());
        Assertions.assertNotSame(0, resourceFormat.getFormatName().length());
    }

    @Test
    void testGetPreferredNameExtension() {
        if (resourceFormat.getPreferredNameExtension() == null) {
            if (resourceFormat.getNameExtensions() == null || resourceFormat.getNameExtensions().length == 0) {
                // Seems to be current practice
                // FIXME: needs to be discussed
            } else {
                Assertions.fail("This format define file name extensions (getNameExtensions()), but does not provide a prefered extension (getPreferredNameExtension()).");
            }
        } else {
            String prefExtension = resourceFormat.getPreferredNameExtension();
            Assertions.assertNotSame(0, prefExtension.length());
            Assertions.assertNotNull(resourceFormat.getNameExtensions(), "This format defines a preferred file name extension (getPreferredNameExtension()), but does not provide a full list of extensions (getNameExtensions()).");
            String[] allExtensions = resourceFormat.getNameExtensions();
            boolean prefExtInAllExtList = false;
            for (String allExtension : allExtensions) {
                if (allExtension.equals(prefExtension)) prefExtInAllExtList = true;
            }
            Assertions.assertTrue(prefExtInAllExtList, "The preferred extension is not found in the list of all extensions");
        }
    }

    @Test
    void testGetNameExtensions() {
        if (resourceFormat.getNameExtensions() == null) {
            // Seems to be current practice
            // FIXME: needs to be discussed
        } else if (resourceFormat.getNameExtensions().length == 0) {
            // Seems to be current practice
            // FIXME: needs to be discussed
        } else {
            String[] exts = resourceFormat.getNameExtensions();
            for (String extension : exts) {
                Assertions.assertNotNull(extension);
                Assertions.assertNotSame(0, extension.length());
                Assertions.assertFalse(extension.contains(","), "File name extensions should not contain ',' characters");
                Assertions.assertFalse(extension.contains("."), "File name extensions should not contain '.' characters");
            }
        }
    }

    @Test
    void testHashCode() throws IllegalAccessException, InstantiationException {
        IResourceFormat a = resourceFormat.getClass().newInstance();
        IResourceFormat b = resourceFormat.getClass().newInstance();
        assertThat(a.hashCode(), is(b.hashCode()));
    }

    @Test
    void testEquals() throws IllegalAccessException, InstantiationException {
        IResourceFormat a = resourceFormat.getClass().newInstance();
        IResourceFormat b = resourceFormat.getClass().newInstance();
        assertThat(a, is(b));
    }

    @Test
    void testEquals_null() throws IllegalAccessException, InstantiationException {
        IResourceFormat a = resourceFormat.getClass().newInstance();
        Assertions.assertNotNull(a);
    }
}
