/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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

/**
 * @cdk.module test-io
 */
abstract public class ResourceFormatTest {
    
    private IResourceFormat resourceFormat;
    
    public void setResourceFormat(IChemFormat format) {
        this.resourceFormat = format;
    }

    @Test public void testResourceFormatSet() {
        Assert.assertNotNull(
            "You must use setResourceFormatSet() to set the resourceFormat object.",
            resourceFormat
        );
    }

    @Test public void testGetMIMEType() {
        if (resourceFormat.getMIMEType() == null) {
            // OK, that's fine
        } else {
            Assert.assertNotSame(0, resourceFormat.getMIMEType().length());
        }
    }
    
    @Test public void testGetFormatName() {
        Assert.assertNotNull(resourceFormat.getFormatName());
        Assert.assertNotSame(0, resourceFormat.getFormatName().length());
    }
    
    @Test public void testGetPreferredNameExtension() {
        if (resourceFormat.getPreferredNameExtension() == null) {
            // Seems to be current practice
            // FIXME: needs to be discussed
        } else {
            Assert.assertNotSame(0, resourceFormat.getPreferredNameExtension().length());
        }
    }
    
    @Test public void testGetNameExtensions() {
        if (resourceFormat.getNameExtensions() == null) {
            // Seems to be current practice
            // FIXME: needs to be discussed
        } else if (resourceFormat.getNameExtensions().length == 0) {
            // Seems to be current practice
            // FIXME: needs to be discussed
        } else {
            String[] exts = resourceFormat.getNameExtensions();
            for (int i=0; i<exts.length; i++) {
                Assert.assertNotNull(exts[i]);
                Assert.assertNotSame(0, exts[i].length());
            }
        }
    }
    
    @Test public void testIsXMLBased() {
        Assert.assertNotNull(resourceFormat.isXMLBased());
    }
    
}
