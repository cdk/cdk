/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.io;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.XYZFormat;
import org.openscience.cdk.tools.DataFeatures;

/**
 * TestCase for the writing files.
 *
 * @cdk.module test-io
 */
public class WriterFactoryTest extends CDKTestCase {

    private WriterFactory factory = new WriterFactory();

    @Test
    public void testFormatCount() {
        Assert.assertTrue(factory.formatCount() > 0);
    }

    @Test
    public void testFindChemFormats() {
        IChemFormat[] formats = factory.findChemFormats(DataFeatures.HAS_3D_COORDINATES);
        Assert.assertNotNull(formats);
        Assert.assertTrue(formats.length > 0);
    }

    @Test
    public void testCreateWriter_IChemFormat() {
        IChemFormat format = (IChemFormat) XYZFormat.getInstance();
        IChemObjectWriter writer = factory.createWriter(format);
        Assert.assertNotNull(writer);
        Assert.assertEquals(format.getFormatName(), writer.getFormat().getFormatName());
    }

    @Test
    public void testCustomWriter() {
        WriterFactory factory = new WriterFactory();
        factory.registerWriter(CustomWriter.class);
        IChemObjectWriter writer = factory.createWriter(new CustomFormat());
        Assert.assertNotNull(writer);
        Assert.assertEquals(new CustomWriter().getClass().getName(), writer.getClass().getName());
    }
}
