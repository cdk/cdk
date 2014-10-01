/* Copyright (c) 2014 Collaborative Drug Discovery, Inc. <alex@collaborativedrug.com>
 *
 * Implemented by Alex M. Clark, produced by Collaborative Drug Discovery, Inc.
 * Made available to the CDK community under the terms of the GNU LGPL.
 *
 *    http://collaborativedrug.com
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

package org.openscience.cdk.qsar.descriptors.molecular;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for small rings descriptor.
 *
 * @cdk.module test-qsarmolecular
 */

public class SmallRingDescriptorTest extends MolecularDescriptorTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(SmallRingDescriptorTest.class);

    public SmallRingDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(SmallRingDescriptor.class);
    }

    @Test
    public void testDescriptors() throws Exception {
        logger.info("CircularFingerprinter test: loading source materials");

        String fnzip = "data/cdd/aromring_validation.zip";
        logger.info("Loading source content: " + fnzip);
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(fnzip);
        validate(in);
        in.close();

        logger.info("CircularFingerprinter test: completed without any problems");
    }

    // included to shutdown the warning messages for not having tests for trivial methods
    @Test
    public void nop() throws Exception {}

    // run through the cases
    private void validate(InputStream in) throws Exception {
        ZipInputStream zip = new ZipInputStream(in);

        // stream the contents form the zipfile: these are all short
        HashMap<String, byte[]> content = new HashMap<String, byte[]>();
        while (true) {
            ZipEntry ze = zip.getNextEntry();
            if (ze == null) break;
            String fn = ze.getName();
            ByteArrayOutputStream buff = new ByteArrayOutputStream();
            while (true) {
                int b = zip.read();
                if (b < 0) break;
                buff.write(b);
            }
            content.put(fn, buff.toByteArray());
        }

        zip.close();

        for (int idx = 1;; idx++) {
            String basefn = String.valueOf(idx);
            while (basefn.length() < 6)
                basefn = "0" + basefn;
            byte[] molBytes = content.get(basefn + ".mol");
            if (molBytes == null) break;

            AtomContainer mol = new AtomContainer();
            MDLV2000Reader mdl = new MDLV2000Reader(new ByteArrayInputStream(molBytes));
            mdl.read(mol);
            mdl.close();

            ByteArrayInputStream rin = new ByteArrayInputStream(content.get(basefn + ".rings"));
            BufferedReader rdr = new BufferedReader(new InputStreamReader(rin));
            String[] bits = rdr.readLine().split(" ");
            rdr.close();
            int wantSmallRings = Integer.parseInt(bits[0]);
            int wantRingBlocks = Integer.parseInt(bits[1]);
            int wantAromRings = Integer.parseInt(bits[2]);
            int wantAromBlocks = Integer.parseInt(bits[3]);

            logger.info("FN=" + basefn + " MOL=" + mol.getAtomCount() + "," + mol.getBondCount() + " nSmallRings="
                    + wantSmallRings + " nRingBlocks=" + wantRingBlocks + " nAromRings=" + wantAromRings
                    + " nAromBlocks=" + wantAromBlocks);

            SmallRingDescriptor descr = new SmallRingDescriptor();
            DescriptorValue results = descr.calculate(mol);
            String[] names = results.getNames();
            IntegerArrayResult values = (IntegerArrayResult) results.getValue();

            int gotSmallRings = 0, gotRingBlocks = 0, gotAromRings = 0, gotAromBlocks = 0;
            for (int n = 0; n < names.length; n++) {
                if (names[n].equals("nSmallRings"))
                    gotSmallRings = values.get(n);
                else if (names[n].equals("nRingBlocks"))
                    gotRingBlocks = values.get(n);
                else if (names[n].equals("nAromRings"))
                    gotAromRings = values.get(n);
                else if (names[n].equals("nAromBlocks")) gotAromBlocks = values.get(n);
            }

            String error = null;
            if (gotSmallRings != wantSmallRings)
                error = "Got " + gotSmallRings + " small rings, expected " + wantSmallRings;
            else if (gotRingBlocks != wantRingBlocks)
                error = "Got " + gotRingBlocks + " ring blocks, expected " + wantRingBlocks;
            else if (gotAromRings != wantAromRings)
                error = "Got " + gotAromRings + " aromatic rings, expected " + wantAromRings;
            else if (gotAromBlocks != wantAromBlocks)
                error = "Got " + gotAromBlocks + " aromatic blocks, expected " + wantAromBlocks;

            if (error != null) {
                StringWriter str = new StringWriter();
                MDLV2000Writer wtr = new MDLV2000Writer(str);
                wtr.write(mol);
                wtr.close();
                error += "\nMolecule:\n" + str.toString();
                throw new CDKException(error);
            }
        }
    }

}
