/* Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;

/**
 * Combined TestCase for the reading/writing of mdl and cml files.
 *
 * @cdk.module test-libiocml
 */

public class MDLCMLRoundtripTest {

    public MDLCMLRoundtripTest() {
        super();
    }

    /**
     * @cdk.bug 1649526
     */
    @Test
    public void testBug1649526() throws Exception {
        //Read the original
        String filename = "data/mdl/bug-1649526.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        //Write it as cml
        StringWriter writer = new StringWriter();
        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.write(mol);
        cmlWriter.close();
        //Read this again
        CMLReader cmlreader = new CMLReader(new ByteArrayInputStream(writer.toString().getBytes()));
        IChemFile file = (IChemFile) cmlreader.read(new org.openscience.cdk.ChemFile());
        cmlreader.close();
        //And finally write as mol
        StringWriter writermdl = new StringWriter();
        MDLV2000Writer mdlWriter = new MDLV2000Writer(writermdl);
        mdlWriter.write(file);
        mdlWriter.close();
        String output = writermdl.toString();
        //if there would be 3 instances (as in the bug), the only instance wouldnt't be right at the end
        Assert.assertEquals(2992, output.indexOf("M  END"));
        //there would need some $$$$ to be in
        Assert.assertEquals(-1, output.indexOf("$$$$"));
        //check atom/bond count
        Assert.assertEquals(23, output.indexOf(" 31 33  0  0  0  0"));
    }
}
