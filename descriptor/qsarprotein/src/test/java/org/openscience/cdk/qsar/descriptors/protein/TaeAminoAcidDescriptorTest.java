/* Copyright (C) 2004-2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.qsar.descriptors.protein;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.MolecularDescriptorTest;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.ProteinBuilderTool;

/**
 * TestSuite that runs test for the TAE descriptors
 *
 * @cdk.module test-qsarprotein
 *
 */
public class TaeAminoAcidDescriptorTest extends MolecularDescriptorTest {

    private static IMolecularDescriptor descriptor;

    @BeforeClass
    public static void setUp() {
        descriptor = new TaeAminoAcidDescriptor();
    }

    @Before
    public void setDescriptor() throws Exception {
        super.setDescriptor(TaeAminoAcidDescriptor.class);
    }

    @Test
    public void testTaeAminoAcidDescriptor() throws ClassNotFoundException, CDKException, Exception {
        IBioPolymer pepseq = ProteinBuilderTool.createProtein("ACDEFGH", SilentChemObjectBuilder.getInstance());
        DescriptorValue result = descriptor.calculate(pepseq);

        DoubleArrayResult dar = (DoubleArrayResult) result.getValue();
        Assert.assertEquals(147, dar.length());
    }
}
