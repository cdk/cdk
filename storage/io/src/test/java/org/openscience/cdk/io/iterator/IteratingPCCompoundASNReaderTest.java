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
 *  */
package org.openscience.cdk.io.iterator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.InputStream;

/**
 * @cdk.module test-io
 */
public class IteratingPCCompoundASNReaderTest extends CDKTestCase {

    private ILoggingTool logger = LoggingToolFactory.createLoggingTool(IteratingPCCompoundASNReaderTest.class);

    @Test
    public void testList() throws Exception {
        String filename = "data/asn/pubchem/list.asn";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingPCCompoundASNReader reader = new IteratingPCCompoundASNReader(ins,
                DefaultChemObjectBuilder.getInstance());

        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof IAtomContainer);
            molCount++;
        }
        reader.close();

        Assert.assertEquals(2, molCount);
    }

}
