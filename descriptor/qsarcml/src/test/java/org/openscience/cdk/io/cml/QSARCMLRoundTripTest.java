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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
class QSARCMLRoundTripTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(QSARCMLRoundTripTest.class);

    private static Convertor    convertor;

    @BeforeAll
    static void setup() {
        convertor = new Convertor(false, "");
        // not needed QSARCustomizer is on by default
        // convertor.registerCustomizer(new QSARCustomizer());
    }

    // See also CMLRoundTripTool
    static IAtomContainer roundTripMolecule(Convertor convertor, IAtomContainer mol) throws Exception {
        String cmlString;
        Element cmlDOM = convertor.cdkAtomContainerToCMLMolecule(mol);
        cmlString = cmlDOM.toXML();

        IAtomContainer roundTrippedMol;
        logger.debug("CML string: ", cmlString);
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));

        IChemFile file = reader.read(new org.openscience.cdk.ChemFile());
        reader.close();
        Assertions.assertNotNull(file);
        Assertions.assertEquals(1, file.getChemSequenceCount());
        IChemSequence sequence = file.getChemSequence(0);
        Assertions.assertNotNull(sequence);
        Assertions.assertEquals(1, sequence.getChemModelCount());
        IChemModel chemModel = sequence.getChemModel(0);
        Assertions.assertNotNull(chemModel);
        IAtomContainerSet moleculeSet = chemModel.getMoleculeSet();
        Assertions.assertNotNull(moleculeSet);
        Assertions.assertEquals(1, moleculeSet.getAtomContainerCount());
        roundTrippedMol = moleculeSet.getAtomContainer(0);
        Assertions.assertNotNull(roundTrippedMol);

        return roundTrippedMol;
    }

    @Test
    void testDescriptorValue_QSAR() throws Exception {
        IAtomContainer molecule = TestMoleculeFactory.makeBenzene();
        IMolecularDescriptor descriptor = new WeightDescriptor();

        DescriptorValue originalValue;
        originalValue = descriptor.calculate(molecule);
        molecule.setProperty(originalValue.getSpecification(), originalValue);
        IAtomContainer roundTrippedMol = roundTripMolecule(convertor, molecule);

        Assertions.assertEquals(1, roundTrippedMol.getProperties().size());
        Object object = roundTrippedMol.getProperties().keySet().toArray()[0];
        Assertions.assertTrue(object instanceof DescriptorSpecification);
        DescriptorSpecification spec = (DescriptorSpecification) object;
        Assertions.assertEquals(descriptor.getSpecification().getSpecificationReference(), spec.getSpecificationReference());
        Assertions.assertEquals(descriptor.getSpecification().getImplementationIdentifier(), spec.getImplementationIdentifier());
        Assertions.assertEquals(descriptor.getSpecification().getImplementationTitle(), spec.getImplementationTitle());
        Assertions.assertEquals(descriptor.getSpecification().getImplementationVendor(), spec.getImplementationVendor());

        Object value = roundTrippedMol.getProperty(spec);
        Assertions.assertNotNull(value);
        Assertions.assertTrue(value instanceof DescriptorValue);
        DescriptorValue descriptorResult = (DescriptorValue) value;
        Assertions.assertEquals(originalValue.getClass().getName(), descriptorResult.getClass().getName());
        Assertions.assertEquals(originalValue.getValue().toString(), descriptorResult.getValue().toString());
    }

    @Test
    void testQSARCustomization() throws Exception {
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
        Assertions.assertTrue(cmlContent.contains("<property") && cmlContent.contains("xmlns:qsar"));
        Assertions.assertTrue(cmlContent.contains("#weight\""));
    }

}
