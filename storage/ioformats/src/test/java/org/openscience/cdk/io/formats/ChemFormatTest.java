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
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.tools.DataFeatures;

/**
 * @cdk.module test-ioformats
 */
abstract public class ChemFormatTest extends ResourceFormatTest {

    private IChemFormat chemFormat;

    public void setChemFormat(IChemFormat format) {
        super.setResourceFormat(format);
        this.chemFormat = format;
    }

    @Test
    public void testChemFormatSet() {
        Assert.assertNotNull("You must use setChemFormat() to set the IChemFormat object.", chemFormat);
    }

    @Ignore("Test cannot be run because it causes a circular dependency cycle")
    public void testGetReaderClassName() throws Exception {
        // two valid output options: NULL and non-zero, existing class
        if (chemFormat.getReaderClassName() != null) {
            String readerClass = chemFormat.getReaderClassName();
            Assert.assertNotSame("Reader Class name String must be of non-zero length", 0, readerClass.length());
            Class<?> reader = Class.forName(readerClass);
            Assert.assertNotNull(reader);
        }
    }

    @Ignore("Test cannot be run because it causes a circular dependency cycle")
    public void testGetWriterClassName() throws Exception {
        // two valid output options: NULL and non-zero, existing class
        if (chemFormat.getWriterClassName() != null) {
            String writerClass = chemFormat.getWriterClassName();
            Assert.assertNotSame("Writer Class name String must be of non-zero length", 0, writerClass.length());
            Class<?> writer = Class.forName(writerClass);
            Assert.assertNotNull(writer);
        }
    }

    @Test
    public void testGetSupportedDataFeatures() {
        int supported = chemFormat.getSupportedDataFeatures();
        Assert.assertTrue(supported >= DataFeatures.NONE);
        Assert.assertTrue(supported <= 1 << 13); // 13 features, so: all summed <= 1<<13
    }

    @Test
    public void testGetRequiredDataFeatures() {
        int required = chemFormat.getRequiredDataFeatures();
        Assert.assertTrue(required >= DataFeatures.NONE);
        Assert.assertTrue(required <= 1 << 13); // 13 features, so: all summed <= 1<<13

        // test that the required features is a subset of the supported features
        int supported = chemFormat.getSupportedDataFeatures();
        Assert.assertTrue(supported - required >= 0);
    }

}
