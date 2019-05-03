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
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @cdk.module test-io
 */
public class IteratingPCCompoundXMLReaderTest extends CDKTestCase {

    private ILoggingTool logger = LoggingToolFactory.createLoggingTool(IteratingPCCompoundXMLReaderTest.class);

    @Test
    public void testList() throws Exception {
        String filename = "data/asn/pubchem/aceticAcids38.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingPCCompoundXMLReader reader = new IteratingPCCompoundXMLReader(new InputStreamReader(ins),
                DefaultChemObjectBuilder.getInstance());

        int molCount = 0;
        IAtomContainerSet set = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        while (reader.hasNext()) {
            //        	System.out.println("next molecule found");
            Object object = reader.next();
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof IAtomContainer);
            set.addAtomContainer((IAtomContainer) object);
            molCount++;
        }

        Assert.assertEquals(3, molCount);
        IAtomContainer first = set.getAtomContainer(0);
        Assert.assertEquals(8, first.getAtomCount());
        Assert.assertEquals(7, first.getBondCount());
        Assert.assertNotNull(first.getProperty("IUPAC Name (Traditional)"));
        Assert.assertEquals("acetic acid", first.getProperty("IUPAC Name (Traditional)"));
        Assert.assertNotNull(first.getProperty("InChI"));
        Assert.assertEquals("InChI=1/C2H4O2/c1-2(3)4/h1H3,(H,3,4)/f/h3H", first.getProperty("InChI"));
        Assert.assertNotNull(first.getProperty("InChI"));
        Assert.assertEquals("176", first.getProperty("PubChem CID"));
    }

}
