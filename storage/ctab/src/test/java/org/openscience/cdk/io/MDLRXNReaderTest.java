/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@slists.sourceforge.net
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
 *
 */
package org.openscience.cdk.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.InputStream;
import java.util.Iterator;

/**
 * TestCase for the reading MDL RXN files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLRXNReader
 */
class MDLRXNReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLRXNReaderTest.class);

    @BeforeAll
    static void setup() {
        setSimpleChemObjectReader(new MDLRXNReader(), "reaction-1.rxn");
    }

    @Test
    void testAccepts() {
        MDLRXNReader reader = new MDLRXNReader();
        Assertions.assertTrue(reader.accepts(ChemFile.class));
        Assertions.assertTrue(reader.accepts(ChemModel.class));
        Assertions.assertTrue(reader.accepts(Reaction.class));
        Assertions.assertTrue(reader.accepts(ReactionSet.class));
        Assertions.assertFalse(reader.accepts(AtomContainerSet.class));
        Assertions.assertFalse(reader.accepts(IAtomContainer.class));
    }

    @Test
    void testReadReactions1() throws Exception {
        String filename1 = "reaction-1.rxn";
        logger.info("Testing: " + filename1);
        InputStream ins1 = this.getClass().getResourceAsStream(filename1);
        MDLRXNReader reader1 = new MDLRXNReader(ins1);
        IReaction reaction1 = new Reaction();
        reaction1 = reader1.read(reaction1);
        reader1.close();

        Assertions.assertNotNull(reaction1);
        Assertions.assertEquals(2, reaction1.getReactantCount());
        Assertions.assertEquals(1, reaction1.getProductCount());

        IAtomContainerSet educts = reaction1.getReactants();
        // Check Atom symbols of first educt
        String[] atomSymbolsOfEduct1 = {"C", "C", "O", "Cl"};
        for (int i = 0; i < educts.getAtomContainer(0).getAtomCount(); i++) {
            Assertions.assertEquals(atomSymbolsOfEduct1[i], educts.getAtomContainer(0).getAtom(i).getSymbol());
        }

        // Check Atom symbols of second educt
        for (int i = 0; i < educts.getAtomContainer(1).getAtomCount(); i++) {
            Assertions.assertEquals("C", educts.getAtomContainer(1).getAtom(i).getSymbol());
        }

        // Check Atom symbols of first product
        IAtomContainerSet products = reaction1.getProducts();
        String[] atomSymbolsOfProduct1 = {"C", "C", "C", "C", "C", "C", "C", "O", "C"};
        for (int i = 0; i < products.getAtomContainer(0).getAtomCount(); i++) {
            Assertions.assertEquals(atomSymbolsOfProduct1[i], products.getAtomContainer(0).getAtom(i).getSymbol());
        }
    }

    @Test
    void testReadReactions2() throws Exception {
        String filename2 = "reaction-2.rxn";
        logger.info("Testing: " + filename2);
        InputStream ins2 = this.getClass().getResourceAsStream(filename2);
        MDLRXNReader reader2 = new MDLRXNReader(ins2);
        IReaction reaction2 = new Reaction();
        reaction2 = reader2.read(reaction2);
        reader2.close();

        Assertions.assertNotNull(reaction2);
        Assertions.assertEquals(2, reaction2.getReactantCount());
        Assertions.assertEquals(2, reaction2.getProductCount());
    }

    @Test
    void testReadMapping() throws Exception {
        String filename2 = "mappingTest.rxn";
        logger.info("Testing: " + filename2);
        InputStream ins2 = this.getClass().getResourceAsStream(filename2);
        MDLRXNReader reader2 = new MDLRXNReader(ins2);
        IReaction reaction2 = new Reaction();
        reaction2 = reader2.read(reaction2);
        reader2.close();

        Assertions.assertNotNull(reaction2);
        Iterator<IMapping> maps = reaction2.mappings().iterator();
        maps.next();
        Assertions.assertTrue(maps.hasNext());
    }

    /**
     *
     */
    @Test
    void testRDFChemFile() throws Exception {
        String filename = "qsar-reaction-test.rdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLRXNReader reader = new MDLRXNReader(ins);
        IChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);

        Assertions.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReactionCount());
        Assertions.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0)
                                           .getReactantCount());
        Assertions.assertEquals(3, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0)
                                           .getReactants().getAtomContainer(0).getAtomCount());
        Assertions.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0)
                                           .getReactants().getAtomContainer(1).getAtomCount());
        Assertions.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0)
                                           .getProductCount());
        Assertions.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0)
                                           .getProducts().getAtomContainer(0).getAtomCount());
        Assertions.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0)
                                           .getProducts().getAtomContainer(1).getAtomCount());

        Assertions.assertEquals(1, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(1)
                                           .getReactantCount());
        Assertions.assertEquals(3, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(1)
                                           .getReactants().getAtomContainer(0).getAtomCount());
        Assertions.assertEquals(1, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(1)
                                           .getProductCount());
        Assertions.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(1)
                                           .getProducts().getAtomContainer(0).getAtomCount());

    }

    /**
     *
     */
    @Test
    void testRDFModel() throws Exception {
        String filename = "qsar-reaction-test.rdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLRXNReader reader = new MDLRXNReader(ins);
        IChemModel chemModel = reader.read(new ChemModel());
        reader.close();
        Assertions.assertNotNull(chemModel);

        Assertions.assertEquals(2, chemModel.getReactionSet().getReactionCount());
        Assertions.assertEquals(2, chemModel.getReactionSet().getReaction(0).getReactantCount());
        Assertions.assertEquals(3, chemModel.getReactionSet().getReaction(0).getReactants().getAtomContainer(0)
                                            .getAtomCount());
        Assertions.assertEquals(2, chemModel.getReactionSet().getReaction(0).getReactants().getAtomContainer(1)
                                            .getAtomCount());
        Assertions.assertEquals(2, chemModel.getReactionSet().getReaction(0).getProductCount());
        Assertions.assertEquals(2, chemModel.getReactionSet().getReaction(0).getProducts().getAtomContainer(0)
                                            .getAtomCount());
        Assertions.assertEquals(2, chemModel.getReactionSet().getReaction(0).getProducts().getAtomContainer(1)
                                            .getAtomCount());

        Assertions.assertEquals(1, chemModel.getReactionSet().getReaction(1).getReactantCount());
        Assertions.assertEquals(3, chemModel.getReactionSet().getReaction(1).getReactants().getAtomContainer(0)
                                            .getAtomCount());
        Assertions.assertEquals(1, chemModel.getReactionSet().getReaction(1).getProductCount());
        Assertions.assertEquals(2, chemModel.getReactionSet().getReaction(1).getProducts().getAtomContainer(0)
                                            .getAtomCount());

    }

    /**
     *
     */
    @Test
    void testRDFReactioniSet() throws Exception {
        String filename = "qsar-reaction-test.rdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLRXNReader reader = new MDLRXNReader(ins);
        IReactionSet reactionSet = reader.read(new ReactionSet());
        reader.close();
        Assertions.assertNotNull(reactionSet);

        Assertions.assertEquals(2, reactionSet.getReactionCount());
        Assertions.assertEquals(2, reactionSet.getReaction(0).getReactantCount());
        Assertions.assertEquals(3, reactionSet.getReaction(0).getReactants().getAtomContainer(0).getAtomCount());
        Assertions.assertEquals(2, reactionSet.getReaction(0).getReactants().getAtomContainer(1).getAtomCount());
        Assertions.assertEquals(2, reactionSet.getReaction(0).getProductCount());
        Assertions.assertEquals(2, reactionSet.getReaction(0).getProducts().getAtomContainer(0).getAtomCount());
        Assertions.assertEquals(2, reactionSet.getReaction(0).getProducts().getAtomContainer(1).getAtomCount());

        Assertions.assertEquals(1, reactionSet.getReaction(1).getReactantCount());
        Assertions.assertEquals(3, reactionSet.getReaction(1).getReactants().getAtomContainer(0).getAtomCount());
        Assertions.assertEquals(1, reactionSet.getReaction(1).getProductCount());
        Assertions.assertEquals(2, reactionSet.getReaction(1).getProducts().getAtomContainer(0).getAtomCount());
    }

    /**
     * This test checks of different numbering for the same mapping gives the same result.
     */
    @Test
    void testAsadExamples() throws Exception {
        String filename = "output.rxn";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLRXNReader reader = new MDLRXNReader(ins);
        IReactionSet reactionSet = reader.read(new ReactionSet());
        reader.close();
        filename = "output_Cleaned.rxn";
        logger.info("Testing: " + filename);
        ins = this.getClass().getResourceAsStream(filename);
        reader = new MDLRXNReader(ins);
        IReactionSet reactionSet2 = reader.read(new ReactionSet());
        reader.close();
        Assertions.assertEquals(reactionSet.getReaction(0).getMappingCount(), reactionSet2.getReaction(0).getMappingCount());
        for (int i = 0; i < reactionSet.getReaction(0).getMappingCount(); i++) {
            Assertions.assertEquals(getAtomNumber(reactionSet, reactionSet.getReaction(0).getMapping(i).getChemObject(0)), getAtomNumber(reactionSet2, reactionSet2.getReaction(0).getMapping(i).getChemObject(0)));
            Assertions.assertEquals(getAtomNumber(reactionSet, reactionSet.getReaction(0).getMapping(i).getChemObject(1)), getAtomNumber(reactionSet2, reactionSet2.getReaction(0).getMapping(i).getChemObject(1)));
        }
    }

    /**
     * Tells the position of an atom in a reaction. Format is "reaction/product:numberofreaction/product_atomnumber".
     *
     * @param reactionSet The reactionSet in which to search.
     * @param chemObject  The atom to search for.
     * @return The position in the said format.
     * @throws CDKException Atom not found in reactionSet.
     */
    private String getAtomNumber(IReactionSet reactionSet, IChemObject chemObject) throws CDKException {
        for (int i = 0; i < reactionSet.getReaction(0).getReactantCount(); i++) {
            for (int k = 0; k < reactionSet.getReaction(0).getReactants().getAtomContainer(i).getAtomCount(); k++) {
                if (reactionSet.getReaction(0).getReactants().getAtomContainer(i).getAtom(k).equals(chemObject))
                    return "reactant:" + i + "_" + k;
            }
        }
        for (int i = 0; i < reactionSet.getReaction(0).getProductCount(); i++) {
            for (int k = 0; k < reactionSet.getReaction(0).getProducts().getAtomContainer(i).getAtomCount(); k++) {
                if (reactionSet.getReaction(0).getProducts().getAtomContainer(i).getAtom(k).equals(chemObject))
                    return "product:" + i + "_" + k;
            }
        }
        throw new CDKException("not found");
    }
}
