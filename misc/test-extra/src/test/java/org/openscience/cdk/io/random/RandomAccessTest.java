/* Copyright (C) 2005-2008  Nina Jeliazkova <nina@acad.bg>
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.io.random;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Test for {@link RandomAccessSDFReader}.
 *
 * @author Nina Jeliazkova &lt;nina@acad.bg&gt;
 * @cdk.module test-extra
 */
public class RandomAccessTest extends CDKTestCase {

    private ILoggingTool logger = LoggingToolFactory.createLoggingTool(RandomAccessTest.class);

    @Test
    public void test() throws Exception {
        String path = "/data/mdl/test2.sdf";
        logger.info("Testing: " + path);
        InputStream in = getClass().getResourceAsStream(path);
        File f = File.createTempFile("tmp", "sdf");

        try {
            // copy data to tmp file
            FileOutputStream out = new FileOutputStream(f);
            try {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                if (out != null) out.close();
            }

            //System.out.println(System.getProperty("user.dir"));
            RandomAccessReader rf = new RandomAccessSDFReader(f, DefaultChemObjectBuilder.getInstance());
            try {
                Assert.assertEquals(6, rf.size());

                String[] mdlnumbers = {"MFCD00000387", "MFCD00000661", "MFCD00000662", "MFCD00000663", "MFCD00000664",
                        "MFCD03453215"};
                //reading backwards - just for the test
                for (int i = rf.size() - 1; i >= 0; i--) {
                    IChemObject m = rf.readRecord(i);
                    Assert.assertEquals(m.getProperty("MDLNUMBER"), mdlnumbers[i]);
                    Assert.assertTrue(m instanceof IAtomContainer);
                    Assert.assertTrue(((IAtomContainer) m).getAtomCount() > 0);
                }
            } finally {
                if (rf != null) rf.close();
            }
        } finally {
            f.delete();
            if (in != null) in.close();
        }
    }
}
