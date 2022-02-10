/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.io.cml;

import nu.xom.Element;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

/**
 * @author John Mayfield
 * @cdk.module test-qsarcml
 */
public class QSARCMLRoundTripTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(QSARCMLRoundTripTest.class);

    private static Convertor    convertor;

    @BeforeClass
    public static void setup() {
        convertor = new Convertor(false, "");
        // not needed QSARCustomizer is on by default
        // convertor.registerCustomizer(new QSARCustomizer());
    }

    // See also CMLRoundTripTool
    public static IAtomContainer roundTripMolecule(Convertor convertor, IAtomContainer mol) throws Exception {
        String cmlString = "<!-- failed -->";
        Element cmlDOM = convertor.cdkAtomContainerToCMLMolecule(mol);
        cmlString = cmlDOM.toXML();

        IAtomContainer roundTrippedMol = null;
        logger.debug("CML string: ", cmlString);
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));

        IChemFile file = reader.read(new org.openscience.cdk.ChemFile());
        reader.close();
        Assert.assertNotNull(file);
        Assert.assertEquals(1, file.getChemSequenceCount());
        IChemSequence sequence = file.getChemSequence(0);
        Assert.assertNotNull(sequence);
        Assert.assertEquals(1, sequence.getChemModelCount());
        IChemModel chemModel = sequence.getChemModel(0);
        Assert.assertNotNull(chemModel);
        IAtomContainerSet moleculeSet = chemModel.getMoleculeSet();
        Assert.assertNotNull(moleculeSet);
        Assert.assertEquals(1, moleculeSet.getAtomContainerCount());
        roundTrippedMol = moleculeSet.getAtomContainer(0);
        Assert.assertNotNull(roundTrippedMol);

        return roundTrippedMol;
    }

    @Test
    public void testDescriptorValue_QSAR() throws Exception {
        IAtomContainer molecule = TestMoleculeFactory.makeBenzene();
        IMolecularDescriptor descriptor = new WeightDescriptor();

        DescriptorValue originalValue = null;
        originalValue = descriptor.calculate(molecule);
        molecule.setProperty(originalValue.getSpecification(), originalValue);
        IAtomContainer roundTrippedMol = roundTripMolecule(convertor, molecule);

        Assert.assertEquals(1, roundTrippedMol.getProperties().size());
        Object object = roundTrippedMol.getProperties().keySet().toArray()[0];
        Assert.assertTrue(object instanceof DescriptorSpecification);
        DescriptorSpecification spec = (DescriptorSpecification) object;
        Assert.assertEquals(descriptor.getSpecification().getSpecificationReference(), spec.getSpecificationReference());
        Assert.assertEquals(descriptor.getSpecification().getImplementationIdentifier(),
                spec.getImplementationIdentifier());
        Assert.assertEquals(descriptor.getSpecification().getImplementationTitle(), spec.getImplementationTitle());
        Assert.assertEquals(descriptor.getSpecification().getImplementationVendor(), spec.getImplementationVendor());

        Object value = roundTrippedMol.getProperty(spec);
        Assert.assertNotNull(value);
        Assert.assertTrue(value instanceof DescriptorValue);
        DescriptorValue descriptorResult = (DescriptorValue) value;
        Assert.assertEquals(originalValue.getClass().getName(), descriptorResult.getClass().getName());
        Assert.assertEquals(originalValue.getValue().toString(), descriptorResult.getValue().toString());
    }

    @Test
    public void testQSARCustomization() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainer molecule = TestMoleculeFactory.makeBenzene();
        IMolecularDescriptor descriptor = new WeightDescriptor();

        CMLWriter cmlWriter = new CMLWriter(writer);
        // not needed QSARCustomizer is on by default
        // convertor.registerCustomizer(new QSARCustomizer());
        DescriptorValue value = descriptor.calculate(molecule);
        molecule.setProperty(value.getSpecification(), value);

        cmlWriter.write(molecule);
        String cmlContent = writer.toString();
        logger.debug("****************************** testQSARCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        Assert.assertTrue(cmlContent.contains("<property") && cmlContent.contains("xmlns:qsar"));
        Assert.assertTrue(cmlContent.contains("#weight\""));
    }

}
