/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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

import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class PetitjeanShapeIndexDescriptorTest extends MolecularDescriptorTest {

    public PetitjeanShapeIndexDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(PetitjeanShapeIndexDescriptor.class);
    }

    @Test
    public void testPetitjeanShapeIndexDescriptor() throws ClassNotFoundException, CDKException, Exception {
        // first molecule is nbutane, second is naphthalene
        String filename = "data/mdl/petitejean.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        DescriptorValue result = descriptor.calculate(ac);
        DoubleArrayResult dar = (DoubleArrayResult) result.getValue();
        Assert.assertEquals(0.5, dar.get(0), 0.00001);
        Assert.assertEquals(0.606477, dar.get(1), 0.000001);

        ac = (IAtomContainer) cList.get(1);
        result = descriptor.calculate(ac);
        dar = (DoubleArrayResult) result.getValue();
        Assert.assertEquals(0.666666, dar.get(0), 0.000001);
        Assert.assertEquals(0.845452, dar.get(1), 0.000001);

    }

    @Test
    public void testPetiteJeanShapeNo3D() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("CCCOCCC(O)=O");
        DescriptorValue result = descriptor.calculate(atomContainer);
        DoubleArrayResult dar = (DoubleArrayResult) result.getValue();
        Assert.assertTrue(Double.isNaN(dar.get(1)));

    }
}
