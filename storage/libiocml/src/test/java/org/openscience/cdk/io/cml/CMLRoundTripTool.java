/* Copyright (C) 2003-2008  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io.cml;

import java.io.ByteArrayInputStream;

import nu.xom.Element;

import org.junit.Assert;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Helper tool for round tripping CDK classes via CML.
 *
 * @cdk.module  test-libiocml
 * @cdk.require xom-1.0.jar
 * @cdk.require java1.5+
 */
public class CMLRoundTripTool extends CDKTestCase {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(CMLRoundTripTool.class);

    /**
     * Convert a Molecule to CML and back to a Molecule again.
     * Given that CML reading is working, the problem is with the
     * CMLWriter.
     *
     * @see org.openscience.cdk.CMLFragmentsTest
     */
    public static IAtomContainer roundTripMolecule(Convertor convertor, IAtomContainer mol) throws Exception {
        String cmlString = "<!-- failed -->";
        Element cmlDOM = convertor.cdkAtomContainerToCMLMolecule(mol);
        cmlString = cmlDOM.toXML();

        IAtomContainer roundTrippedMol = null;
        logger.debug("CML string: ", cmlString);
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));

        IChemFile file = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
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

    public static IChemModel roundTripChemModel(Convertor convertor, IChemModel model) throws Exception {
        String cmlString = "<!-- failed -->";
        Element cmlDOM = convertor.cdkChemModelToCMLList(model);
        cmlString = cmlDOM.toXML();

        logger.debug("CML string: ", cmlString);
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));
        reader.close();

        IChemFile file = (IChemFile) reader.read(model.getBuilder().newInstance(IChemFile.class));
        Assert.assertNotNull(file);
        Assert.assertEquals(1, file.getChemSequenceCount());
        IChemSequence sequence = file.getChemSequence(0);
        Assert.assertNotNull(sequence);
        Assert.assertEquals(1, sequence.getChemModelCount());
        IChemModel chemModel = sequence.getChemModel(0);
        Assert.assertNotNull(chemModel);

        return chemModel;
    }

    public static IReaction roundTripReaction(Convertor convertor, IReaction reaction) throws Exception {
        String cmlString = "<!-- failed -->";
        Element cmlDOM = convertor.cdkReactionToCMLReaction(reaction);
        cmlString = cmlDOM.toXML();

        IReaction roundTrippedReaction = null;
        logger.debug("CML string: ", cmlString);
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));

        IChemFile file = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();
        Assert.assertNotNull(file);
        Assert.assertEquals(1, file.getChemSequenceCount());
        IChemSequence sequence = file.getChemSequence(0);
        Assert.assertNotNull(sequence);
        Assert.assertEquals(1, sequence.getChemModelCount());
        IChemModel chemModel = sequence.getChemModel(0);
        Assert.assertNotNull(chemModel);
        IReactionSet reactionSet = chemModel.getReactionSet();
        Assert.assertNotNull(reactionSet);
        Assert.assertEquals(1, reactionSet.getReactionCount());
        roundTrippedReaction = reactionSet.getReaction(0);
        Assert.assertNotNull(roundTrippedReaction);

        return roundTrippedReaction;
    }

}
