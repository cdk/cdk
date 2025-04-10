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

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for fractional PSA descriptor.
 *
 */

class FractionalPSADescriptorTest extends MolecularDescriptorTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(FractionalPSADescriptorTest.class);

    FractionalPSADescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(FractionalPSADescriptor.class);
    }

    @Test
    void testDescriptors() throws Exception {
        String fnmol = "data/cdd/pyridineacid.mol";
        MDLV2000Reader mdl = new MDLV2000Reader(this.getClass().getClassLoader().getResourceAsStream(fnmol));
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        mdl.read(mol);
        mdl.close();

        FractionalPSADescriptor fpsa = new FractionalPSADescriptor();
        DescriptorValue results = fpsa.calculate(mol);

        // note: test currently assumes that just one descriptor is calculated
        String[] names = results.getNames();
        if (names.length != 1 || !names[0].equals("tpsaEfficiency"))
            throw new CDKException("Only expecting 'tpsaEfficiency'");
        DoubleResult value = (DoubleResult) results.getValue();
        double tpsaEfficiency = value.doubleValue();
        final double ANSWER = 0.4036, ANSWER_LO = ANSWER * 0.999, ANSWER_HI = ANSWER * 1.001; // (we can tolerate rounding errors)
        if (tpsaEfficiency < ANSWER_LO || tpsaEfficiency > ANSWER_HI) {
            throw new CDKException("Got " + tpsaEfficiency + ", expected " + ANSWER);
        }
    }

    // included to shutdown the warning messages for not having tests for trivial methods
    @Test
    void nop() throws Exception {}
}
