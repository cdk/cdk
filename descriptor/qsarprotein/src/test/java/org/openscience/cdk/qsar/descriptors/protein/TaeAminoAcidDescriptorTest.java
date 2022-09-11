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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
class TaeAminoAcidDescriptorTest extends MolecularDescriptorTest {

    private static IMolecularDescriptor descriptor;

    @BeforeAll
    static void setUp() {
        descriptor = new TaeAminoAcidDescriptor();
    }

    @BeforeEach
    void setDescriptor() throws Exception {
        super.setDescriptor(TaeAminoAcidDescriptor.class);
    }

    @Test
    void testTaeAminoAcidDescriptor() throws Exception {
        IBioPolymer pepseq = ProteinBuilderTool.createProtein("ACDEFGH", SilentChemObjectBuilder.getInstance());
        DescriptorValue result = descriptor.calculate(pepseq);

        DoubleArrayResult dar = (DoubleArrayResult) result.getValue();
        Assertions.assertEquals(147, dar.length());
    }
}
