/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2003-2008  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Helper tool for round tripping CDK classes via CML.
 *
 * @cdk.module  test-libiocml
 * @cdk.require xom-1.0.jar
 * @cdk.require java1.5+
 */
public class CMLRoundTripTool extends CDKTestCase {

    private static LoggingTool logger = new LoggingTool(CMLRoundTripTool.class);
    private static Convertor convertor = new Convertor(true, null);

    /**
     * Convert a Molecule to CML and back to a Molecule again.
     * Given that CML reading is working, the problem is with the
     * CMLWriter.
     *
     * @see org.openscience.cdk.CMLFragmentsTest
     */
    public static IMolecule roundTripMolecule(IMolecule mol) throws Exception {
        String cmlString = "<!-- failed -->";
        Element cmlDOM = convertor.cdkMoleculeToCMLMolecule(mol);
        cmlString = cmlDOM.toXML();
        
        IMolecule roundTrippedMol = null;
        logger.debug("CML string: ", cmlString);
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));

        IChemFile file = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());
        assertNotNull(file);
        assertEquals(1, file.getChemSequenceCount());
        IChemSequence sequence = file.getChemSequence(0);
        assertNotNull(sequence);
        assertEquals(1, sequence.getChemModelCount());
        IChemModel chemModel = sequence.getChemModel(0);
        assertNotNull(chemModel);
        IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
        assertNotNull(moleculeSet);
        assertEquals(1, moleculeSet.getMoleculeCount());
        roundTrippedMol = moleculeSet.getMolecule(0);
        assertNotNull(roundTrippedMol);
        
        return roundTrippedMol;
    }
    
    public static IChemModel roundTripChemModel(IChemModel model) throws Exception {
        String cmlString = "<!-- failed -->";
        Element cmlDOM = convertor.cdkChemModelToCMLList(model);
        cmlString = cmlDOM.toXML();
        
        logger.debug("CML string: ", cmlString);
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));

        IChemFile file = (IChemFile)reader.read(model.getBuilder().newChemFile());
        assertNotNull(file);
        assertEquals(1, file.getChemSequenceCount());
        IChemSequence sequence = file.getChemSequence(0);
        assertNotNull(sequence);
        assertEquals(1, sequence.getChemModelCount());
        IChemModel chemModel = sequence.getChemModel(0);
        assertNotNull(chemModel);
        
        return chemModel;
    }

    public static IReaction roundTripReaction(IReaction reaction) throws Exception {
        String cmlString = "<!-- failed -->";
        Element cmlDOM = convertor.cdkReactionToCMLReaction(reaction);
        cmlString = cmlDOM.toXML();
        
        IReaction roundTrippedReaction = null;
        logger.debug("CML string: ", cmlString);
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));

        IChemFile file = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());
        assertNotNull(file);
        assertEquals(1, file.getChemSequenceCount());
        IChemSequence sequence = file.getChemSequence(0);
        assertNotNull(sequence);
        assertEquals(1, sequence.getChemModelCount());
        IChemModel chemModel = sequence.getChemModel(0);
        assertNotNull(chemModel);
        IReactionSet reactionSet = chemModel.getReactionSet();
        assertNotNull(reactionSet);
        assertEquals(1, reactionSet.getReactionCount());
        roundTrippedReaction = reactionSet.getReaction(0);
        assertNotNull(roundTrippedReaction);
        
        return roundTrippedReaction;
    }

}

