package org.openscience.cdk.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

/**
 * @author Uli Fechner
 */
class RdfileReaderTest {
    private static final IChemObjectBuilder chemObjectBuilder = SilentChemObjectBuilder.getInstance();

    @Test
    void testRdfile_oneRecord_mfmt_mol_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mfmt_mol_dataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final Map<Object, Object> expectedData = new LinkedHashMap<>();
        final String key_1 = "Identifier";
        final String value_1 = "141";
        expectedData.put(key_1, value_1);
        final String key_2 = "LongMultilineDatum";
        final String value_2 = "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB";
        expectedData.put(key_2, value_2);

        // act
        RdfileRecord rdfileRecord = rdfileReader.doReadNext();

        // assert
        Assertions.assertNull(rdfileReader.doReadNext(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        Assertions.assertNull(rdfileRecord.getReaction());
        IAtomContainer atomContainer = rdfileRecord.getAtomContainer();
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        Assertions.assertEquals(value_1, atomContainer.getProperty(key_1));
        Assertions.assertEquals(value_2, atomContainer.getProperty(key_2));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_mfmt_mireg_mol_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mfmt_mireg_mol_dataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final Map<Object, Object> expectedData = new LinkedHashMap<>();
        final String key_1 = "Identifier";
        final String value_1 = "141";
        expectedData.put(key_1, value_1);
        final String key_2 = "LongMultilineDatum";
        final String value_2 = "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB";
        expectedData.put(key_2, value_2);

        // act
        RdfileRecord rdfileRecord = rdfileReader.doReadNext();

        // assert
        Assertions.assertNull(rdfileReader.doReadNext(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        Assertions.assertNull(rdfileRecord.getReaction());
        IAtomContainer atomContainer = rdfileRecord.getAtomContainer();
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        Assertions.assertEquals(value_1, atomContainer.getProperty(key_1));
        Assertions.assertEquals(value_2, atomContainer.getProperty(key_2));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_mfmt_mereg_mol_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mfmt_mereg_mol_dataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final Map<Object, Object> expectedData = new LinkedHashMap<>();
        final String key_1 = "Identifier";
        final String value_1 = "141";
        expectedData.put(key_1, value_1);
        final String key_2 = "LongMultilineDatum";
        final String value_2 = "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB";
        expectedData.put(key_2, value_2);

        // act
        RdfileRecord rdfileRecord = rdfileReader.doReadNext();

        // assert
        Assertions.assertNull(rdfileReader.doReadNext(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        Assertions.assertNull(rdfileRecord.getReaction());
        IAtomContainer atomContainer = rdfileRecord.getAtomContainer();
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        Assertions.assertEquals(value_1, atomContainer.getProperty(key_1));
        Assertions.assertEquals(value_2, atomContainer.getProperty(key_2));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_mireg_mol_noDataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mireg_mol_noDataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final Map<Object, Object> expectedData = new LinkedHashMap<>();

        // act
        RdfileRecord rdfileRecord = rdfileReader.doReadNext();

        // assert
        Assertions.assertNull(rdfileReader.doReadNext(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        Assertions.assertNull(rdfileRecord.getReaction());
        IAtomContainer atomContainer = rdfileRecord.getAtomContainer();
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_mereg_mol_noDataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mereg_mol_noDataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final Map<Object, Object> expectedData = new LinkedHashMap<>();

        // act
        RdfileRecord rdfileRecord = rdfileReader.doReadNext();

        // assert
        Assertions.assertNull(rdfileReader.doReadNext(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        Assertions.assertNull(rdfileRecord.getReaction());
        IAtomContainer atomContainer = rdfileRecord.getAtomContainer();
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rfmt_rxn_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rfmt_rxn_dataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final Map<Object, Object> expectedData = new LinkedHashMap<>();
        final String key_1 = "Identifier";
        final String value_1 = "141";
        expectedData.put(key_1, value_1);
        final String key_2 = "LongMultilineDatum";
        final String value_2 = "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB";
        expectedData.put(key_2, value_2);

        // act
        RdfileRecord rdfileRecord = rdfileReader.doReadNext();

        // assert
        Assertions.assertNull(rdfileReader.doReadNext(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        Assertions.assertNull(rdfileRecord.getAtomContainer());
        IReaction reaction = rdfileRecord.getReaction();
        Assertions.assertEquals(2, reaction.getReactantCount());
        Assertions.assertEquals(0, reaction.getAgents().getAtomContainerCount());
        Assertions.assertEquals(1, reaction.getProductCount());

        // reactant #1
        IAtomContainer reactant1 = reaction.getReactants().getAtomContainer(0);
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(reactant1);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(reactant1);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        // reactant #2
        IAtomContainer reactant2 = reaction.getReactants().getAtomContainer(1);
        elementFrequencyMap = generateElementFrequencyMap(reactant2);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        bondOrderFrequencyMap = generateBondFrequencyMap(reactant2);
        Assertions.assertEquals(0, bondOrderFrequencyMap.size());
        // product
        IAtomContainer product = reaction.getProducts().getAtomContainer(0);
        Assertions.assertEquals(7, product.getAtomCount());
        elementFrequencyMap = generateElementFrequencyMap(product);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        Assertions.assertEquals(6, elementFrequencyMap.get(6));
        bondOrderFrequencyMap = generateBondFrequencyMap(product);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        // data block
        Assertions.assertEquals(value_1, reaction.getProperty(key_1));
        Assertions.assertEquals(value_2, reaction.getProperty(key_2));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rfmt_rireg_rxn_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rfmt_rireg_rxn_dataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final Map<Object, Object> expectedData = new LinkedHashMap<>();
        final String key_1 = "Identifier";
        final String value_1 = "141";
        expectedData.put(key_1, value_1);
        final String key_2 = "LongMultilineDatum";
        final String value_2 = "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB";
        expectedData.put(key_2, value_2);

        // act
        RdfileRecord rdfileRecord = rdfileReader.doReadNext();

        // assert
        Assertions.assertNull(rdfileReader.doReadNext(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        IReaction reaction = rdfileRecord.getReaction();
        Assertions.assertEquals(2, reaction.getReactantCount());
        Assertions.assertEquals(0, reaction.getAgents().getAtomContainerCount());
        Assertions.assertEquals(1, reaction.getProductCount());

        // reactant #1
        IAtomContainer reactant1 = reaction.getReactants().getAtomContainer(0);
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(reactant1);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(reactant1);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        // reactant #2
        IAtomContainer reactant2 = reaction.getReactants().getAtomContainer(1);
        elementFrequencyMap = generateElementFrequencyMap(reactant2);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        bondOrderFrequencyMap = generateBondFrequencyMap(reactant2);
        Assertions.assertEquals(0, bondOrderFrequencyMap.size());
        // product
        IAtomContainer product = reaction.getProducts().getAtomContainer(0);
        Assertions.assertEquals(7, product.getAtomCount());
        elementFrequencyMap = generateElementFrequencyMap(product);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        Assertions.assertEquals(6, elementFrequencyMap.get(6));
        bondOrderFrequencyMap = generateBondFrequencyMap(product);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        // data block
        Assertions.assertEquals(value_1, reaction.getProperty(key_1));
        Assertions.assertEquals(value_2, reaction.getProperty(key_2));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rfmt_rereg_rxn_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rfmt_rereg_rxn_dataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final Map<Object, Object> expectedData = new LinkedHashMap<>();
        final String key_1 = "Identifier";
        final String value_1 = "141";
        expectedData.put(key_1, value_1);
        final String key_2 = "LongMultilineDatum";
        final String value_2 = "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB";
        expectedData.put(key_2, value_2);

        // act
        RdfileRecord rdfileRecord = rdfileReader.doReadNext();

        // assert
        Assertions.assertNull(rdfileReader.doReadNext(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        IReaction reaction = rdfileRecord.getReaction();
        Assertions.assertEquals(2, reaction.getReactantCount());
        Assertions.assertEquals(0, reaction.getAgents().getAtomContainerCount());
        Assertions.assertEquals(1, reaction.getProductCount());

        // reactant #1
        IAtomContainer reactant1 = reaction.getReactants().getAtomContainer(0);
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(reactant1);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(reactant1);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        // reactant #2
        IAtomContainer reactant2 = reaction.getReactants().getAtomContainer(1);
        elementFrequencyMap = generateElementFrequencyMap(reactant2);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        bondOrderFrequencyMap = generateBondFrequencyMap(reactant2);
        Assertions.assertEquals(0, bondOrderFrequencyMap.size());
        // product
        IAtomContainer product = reaction.getProducts().getAtomContainer(0);
        Assertions.assertEquals(7, product.getAtomCount());
        elementFrequencyMap = generateElementFrequencyMap(product);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        Assertions.assertEquals(6, elementFrequencyMap.get(6));
        bondOrderFrequencyMap = generateBondFrequencyMap(product);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        // data block
        Assertions.assertEquals(value_1, reaction.getProperty(key_1));
        Assertions.assertEquals(value_2, reaction.getProperty(key_2));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rireg_rxn_noDataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rireg_rxn_noDataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final Map<Object, Object> expectedData = new LinkedHashMap<>();

        // act
        RdfileRecord rdfileRecord = rdfileReader.doReadNext();

        // assert
        Assertions.assertNull(rdfileReader.doReadNext(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        IReaction reaction = rdfileRecord.getReaction();
        Assertions.assertEquals(2, reaction.getReactantCount());
        Assertions.assertEquals(0, reaction.getAgents().getAtomContainerCount());
        Assertions.assertEquals(1, reaction.getProductCount());

        // reactant #1
        IAtomContainer reactant1 = reaction.getReactants().getAtomContainer(0);
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(reactant1);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(reactant1);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        // reactant #2
        IAtomContainer reactant2 = reaction.getReactants().getAtomContainer(1);
        elementFrequencyMap = generateElementFrequencyMap(reactant2);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        bondOrderFrequencyMap = generateBondFrequencyMap(reactant2);
        Assertions.assertEquals(0, bondOrderFrequencyMap.size());
        // product
        IAtomContainer product = reaction.getProducts().getAtomContainer(0);
        Assertions.assertEquals(7, product.getAtomCount());
        elementFrequencyMap = generateElementFrequencyMap(product);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        Assertions.assertEquals(6, elementFrequencyMap.get(6));
        bondOrderFrequencyMap = generateBondFrequencyMap(product);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rereg_rxn_noDataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rereg_rxn_noDataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final Map<Object, Object> expectedData = new LinkedHashMap<>();

        // act
        RdfileRecord rdfileRecord = rdfileReader.doReadNext();

        // assert
        Assertions.assertNull(rdfileReader.doReadNext(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        IReaction reaction = rdfileRecord.getReaction();
        Assertions.assertEquals(2, reaction.getReactantCount());
        Assertions.assertEquals(0, reaction.getAgents().getAtomContainerCount());
        Assertions.assertEquals(1, reaction.getProductCount());

        // reactant #1
        IAtomContainer reactant1 = reaction.getReactants().getAtomContainer(0);
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(reactant1);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(reactant1);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        // reactant #2
        IAtomContainer reactant2 = reaction.getReactants().getAtomContainer(1);
        elementFrequencyMap = generateElementFrequencyMap(reactant2);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        bondOrderFrequencyMap = generateBondFrequencyMap(reactant2);
        Assertions.assertEquals(0, bondOrderFrequencyMap.size());
        // product
        IAtomContainer product = reaction.getProducts().getAtomContainer(0);
        Assertions.assertEquals(7, product.getAtomCount());
        elementFrequencyMap = generateElementFrequencyMap(product);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        Assertions.assertEquals(6, elementFrequencyMap.get(6));
        bondOrderFrequencyMap = generateBondFrequencyMap(product);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rereg_rxn_dataBlockWithEmbeddedMolecule() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rereg_rxn_dataBlockWithEmbeddedMolecule.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final Map<Object, Object> expectedData = new LinkedHashMap<>();
        final String key_1 = "CATALYST_1";
        final String value_1 = "$MFMT\n" +
                "\n" +
                "  Mrv2219  110920222203\n" +
                "\n" +
                "  2  1  0  0  0  0  0  0  0  0999 V2000\n" +
                "   -5.0000    3.0000    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    5.0000   -3.0000    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  2  0  0  0  0\n" +
                "M  END";
        expectedData.put(key_1, value_1);
        final String key_2 = "Identifier";
        final String value_2 = "141";
        expectedData.put(key_2, value_2);
        final String key_3 = "SOLVENT_1";
        final String value_3 = "$MFMT\n" +
                "\n" +
                "  Mrv2219  110920222203\n" +
                "\n" +
                "  3  2  0  0  0  0  0  0  0  0999 V2000\n" +
                "    5.0000   -0.5000    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -5.0000   -3.5000    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.5000    3.5000    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  3  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "M  END";
        expectedData.put(key_3, value_3);
        final String key_4 = "LongMultilineDatum";
        final String value_4 = "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUV" +
                "bfkPnBBS4aK6Wkq7LOHaHORhMe3PCuJxKBqJi9492soYNHwgvESO1A3lGUgeGiMSS4aK6Wkq7LOHaHOG";
        expectedData.put(key_4, value_4);

        // act
        RdfileRecord rdfileRecord = rdfileReader.doReadNext();

        // assert
        Assertions.assertNull(rdfileReader.doReadNext(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        IReaction reaction = rdfileRecord.getReaction();
        Assertions.assertEquals(2, reaction.getReactantCount());
        Assertions.assertEquals(0, reaction.getAgents().getAtomContainerCount());
        Assertions.assertEquals(1, reaction.getProductCount());

        // reactant #1
        IAtomContainer reactant1 = reaction.getReactants().getAtomContainer(0);
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(reactant1);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(reactant1);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        // reactant #2
        IAtomContainer reactant2 = reaction.getReactants().getAtomContainer(1);
        elementFrequencyMap = generateElementFrequencyMap(reactant2);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        bondOrderFrequencyMap = generateBondFrequencyMap(reactant2);
        Assertions.assertEquals(0, bondOrderFrequencyMap.size());
        // product
        IAtomContainer product = reaction.getProducts().getAtomContainer(0);
        Assertions.assertEquals(7, product.getAtomCount());
        elementFrequencyMap = generateElementFrequencyMap(product);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        Assertions.assertEquals(6, elementFrequencyMap.get(6));
        bondOrderFrequencyMap = generateBondFrequencyMap(product);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line1() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line1.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in line 1: Expected the line to exactly contain '$RDFILE 1', but instead found '$RDFILE 2'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line2() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line2.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in line 2: Expected the line to start with '$DATM', but instead found '$FATM    11/09/22 22:12'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line3_mfmt_rireg() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line3_mfmt_rireg.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in line 3: Expected either '$MIREG' or '$MEREG' after '$MFMT', but instead found '$RIREG 7483765'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line3_rfmt_nonsense() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line3_rmft_nonsense.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in line 3: Expected either '$MIREG' or '$MEREG' after '$MFMT', but instead found 'NONSENSE'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line3_notMfmt_notRfmt() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line3_notMfmt_NotRfmt.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in line 3: Expected the line to specify the molecule or reaction identifier, but instead found '$LFMT'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line4_mfmt_rxn() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line4_mfmt_rxn.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in line 4: Expected the line to start with '$MOL', but instead found '$RXN'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line4_rfmt_mol() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line4_rfmt_mol.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in line 4: Expected the line to start with '$RXN', but instead found '$MOL'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line9_mfmt_noMEnd() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line9_mfmt_noMEnd.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in line 9: Expected molfile to end with 'M  END', but instead found 'null'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line10_molfile_lineStartsWithDollarSign() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line10_molfile_lineStartsWithDollarSign.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in line 10: Unexpected character '$' within molfile at start of line '$DTYPE Identifier'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line8_rxnfile_countsLine_oneDigit() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line8_componentCounts_oneDigit.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in line 8: Incorrect formatting of the line that indicates the number of reaction components '  2'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line8_rxnfile_countsLine_oneDigitOneLetter() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line8_componentCounts_oneDigitOneLetter.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in line 8: Incorrect formatting of the line that indicates the number of reaction components '  2  D'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line33_inconsistentCtabVersion() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line33_inconsistentCtabVersion.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in line 33: Expected the CTAB version 'V3000' of this Molfile to be the same as the CTAB " +
                "version 'V2000' of the record, but instead found '  1  0  0  0  0  0            999 V3000'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line40_invalidCtabVersion() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line40_invalidCtabVersion.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in line 40: Expected a counts line that ends with a version of " +
                "either 'V2000' or 'V3000', but instead found '  7  7  0  0  0  0            999 V1000'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line56_rxnfile_countsDontMatchMolfiles() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line56_rxnfile_countsDontMatchMolfiles.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in RXN record: The sum of the number of reactants (2), products (1) and agents (1) is not equal to " +
                "the number of Molfile entries 3 in the record."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line57_datumExpected() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line57_datumExpected.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in data block in line 57. Expected line to start with '$DATUM' " +
                "and to either have <= 80 characters or exactly 81 characters and ending with a '+'', but instead found '$datum 141'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line58_dtypeExpected() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line58_dtypeExpected.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in data block in line 58. Expected line to start with '$DTYPE' and be followed by a space and a string, but instead found '$VTYPE LongMultilineDatum'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line58_dtypeValueExpected() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line58_dtypeValueExpected.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in data block in line 58. Expected line to start with '$DTYPE' and be followed by a space and a string, but instead found '$DTYPE'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line59_plusSignExpected() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line59_plusSignExpected.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in data block in line 59. Expected line to start with '$DATUM' and to " +
                "either have <= 80 characters or exactly 81 characters and ending with a '+'', but instead found " +
                "'$DATUM 5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9AG'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line60_datumLineWithMoreThan81Characters() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line60_datumLineWithMoreThan81Characters.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.doReadNext());
        assertThat(exception.getMessage(), is("Error in data block in line 60. Expected multi-line datum with either less than or equal to 80 characters " +
                "or with exactly 81 characters and ending with a '+', but instead found " +
                "'XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVSDF389sdfs'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_fourRecords_mol_mol_rxn_mol() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_fourRecords_mol_mol_rxn_mol.rdf";
        final RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);

        final List<Map<Object, Object>> expectedDataList = new ArrayList<>();
        final Map<Object, Object> expectedData_1 = new LinkedHashMap<>();
        final String key_1 = "Identifier";
        final String value_1 = "141";
        expectedData_1.put(key_1, value_1);
        expectedDataList.add(expectedData_1);
        final Map<Object, Object> expectedData_2 = new LinkedHashMap<>();
        final String key_2 = "Identifier";
        final String value_2 = "142";
        expectedData_2.put(key_2, value_2);
        expectedDataList.add(expectedData_2);
        final Map<Object, Object> expectedData_3 = new LinkedHashMap<>();
        final String key_3 = "Identifier";
        final String value_3 = "211";
        expectedData_3.put(key_3, value_3);
        expectedDataList.add(expectedData_3);
        final Map<Object, Object> expectedData_4 = new LinkedHashMap<>();
        final String key_4 = "Identifier";
        final String value_4 = "143";
        expectedData_4.put(key_4, value_4);
        expectedDataList.add(expectedData_4);

        // act
        final List<RdfileRecord> records = new ArrayList<>();
        records.add(rdfileReader.doReadNext());
        records.add(rdfileReader.doReadNext());
        records.add(rdfileReader.doReadNext());
        records.add(rdfileReader.doReadNext());

        // assert
        Assertions.assertNull(rdfileReader.doReadNext(), "Expected null as there is only four records in this RDfile");
        rdfileRecordEqualsRdfile(records, rdfFilename, expectedDataList);

        // record #1 - molecule
        RdfileRecord rdfileRecord = records.get(0);
        Assertions.assertNull(rdfileRecord.getReaction());
        IAtomContainer atomContainer = rdfileRecord.getAtomContainer();
        Assertions.assertNotNull(atomContainer);
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        Assertions.assertEquals(value_1, atomContainer.getProperty(key_1));

        // record #2 - molecule
        rdfileRecord = records.get(1);
        Assertions.assertNull(rdfileRecord.getReaction());
        atomContainer = rdfileRecord.getAtomContainer();
        Assertions.assertNotNull(atomContainer);
        elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(2, elementFrequencyMap.get(8));
        bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(1, bondOrderFrequencyMap.get(IBond.Order.DOUBLE));
        Assertions.assertEquals(value_2, atomContainer.getProperty(key_2));

        // record #3 - reaction
        rdfileRecord = records.get(2);
        Assertions.assertNull(rdfileRecord.getAtomContainer());
        IReaction reaction = rdfileRecord.getReaction();
        Assertions.assertNotNull(reaction);
        Assertions.assertEquals(2, reaction.getReactantCount());
        Assertions.assertEquals(0, reaction.getAgents().getAtomContainerCount());
        Assertions.assertEquals(1, reaction.getProductCount());
        // reactant #1
        IAtomContainer reactant1 = reaction.getReactants().getAtomContainer(0);
        elementFrequencyMap = generateElementFrequencyMap(reactant1);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        bondOrderFrequencyMap = generateBondFrequencyMap(reactant1);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        // reactant #2
        IAtomContainer reactant2 = reaction.getReactants().getAtomContainer(1);
        elementFrequencyMap = generateElementFrequencyMap(reactant2);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        bondOrderFrequencyMap = generateBondFrequencyMap(reactant2);
        Assertions.assertEquals(0, bondOrderFrequencyMap.size());
        // product
        IAtomContainer product = reaction.getProducts().getAtomContainer(0);
        Assertions.assertEquals(7, product.getAtomCount());
        elementFrequencyMap = generateElementFrequencyMap(product);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        Assertions.assertEquals(6, elementFrequencyMap.get(6));
        bondOrderFrequencyMap = generateBondFrequencyMap(product);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));

        // record #4 - molecule
        rdfileRecord = records.get(3);
        Assertions.assertNull(rdfileRecord.getReaction());
        atomContainer = rdfileRecord.getAtomContainer();
        Assertions.assertNotNull(atomContainer);
        elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(2, elementFrequencyMap.get(8));
        bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(1, bondOrderFrequencyMap.get(IBond.Order.DOUBLE));
        Assertions.assertEquals(value_4, atomContainer.getProperty(key_4));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testIterator_fourRecords_iterationPattern1() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_fourRecords_mol_mol_rxn_mol.rdf";
        final RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final List<RdfileRecord> records = new ArrayList<>();
        final List<Map<Object, Object>> expectedDataList = new ArrayList<>();
        final Map<Object, Object> expectedData_1 = new LinkedHashMap<>();
        expectedData_1.put("Identifier", "141");
        expectedDataList.add(expectedData_1);
        final Map<Object, Object> expectedData_2 = new LinkedHashMap<>();
        expectedData_2.put("Identifier", "142");
        expectedDataList.add(expectedData_2);
        final Map<Object, Object> expectedData_3 = new LinkedHashMap<>();
        expectedData_3.put("Identifier", "211");
        expectedDataList.add(expectedData_3);
        final Map<Object, Object> expectedData_4 = new LinkedHashMap<>();
        expectedData_4.put("Identifier", "143");
        expectedDataList.add(expectedData_4);

        // act & assert
        assertThat(rdfileReader.hasNext(), is(true));
        records.add(rdfileReader.next());
        assertThat(rdfileReader.hasNext(), is(true));
        records.add(rdfileReader.next());
        assertThat(rdfileReader.hasNext(), is(true));
        records.add(rdfileReader.next());
        assertThat(rdfileReader.hasNext(), is(true));
        records.add(rdfileReader.next());
        assertThat(rdfileReader.hasNext(), is(false));
        assertThat(rdfileReader.hasNext(), is(false));
        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> rdfileReader.next(), "expected to have reached end of file");
        assertThat(exception.getMessage(), is("RdfileReader reached end of file."));
        rdfileRecordEqualsRdfile(records, rdfFilename, expectedDataList);

        // tear down
        rdfileReader.close();
    }

    @Test
    void testIterator_fourRecords_iterationPattern2() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_fourRecords_mol_mol_rxn_mol.rdf";
        final RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final List<RdfileRecord> records = new ArrayList<>();
        final List<Map<Object, Object>> expectedDataList = new ArrayList<>();
        final Map<Object, Object> expectedData_1 = new LinkedHashMap<>();
        expectedData_1.put("Identifier", "141");
        expectedDataList.add(expectedData_1);
        final Map<Object, Object> expectedData_2 = new LinkedHashMap<>();
        expectedData_2.put("Identifier", "142");
        expectedDataList.add(expectedData_2);
        final Map<Object, Object> expectedData_3 = new LinkedHashMap<>();
        expectedData_3.put("Identifier", "211");
        expectedDataList.add(expectedData_3);
        final Map<Object, Object> expectedData_4 = new LinkedHashMap<>();
        expectedData_4.put("Identifier", "143");
        expectedDataList.add(expectedData_4);

        // act & assert
        records.add(rdfileReader.next());
        records.add(rdfileReader.next());
        records.add(rdfileReader.next());
        assertThat(rdfileReader.hasNext(), is(true));
        records.add(rdfileReader.next());
        assertThat(rdfileReader.hasNext(), is(false));
        assertThat(rdfileReader.hasNext(), is(false));
        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> rdfileReader.next(), "expected to have reached end of file");
        assertThat(exception.getMessage(), is("RdfileReader reached end of file."));
        rdfileRecordEqualsRdfile(records, rdfFilename, expectedDataList);

        // tear down
        rdfileReader.close();
    }

    @Test
    void testIterator_oneRecord_iterationPattern1() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rfmt_rxn_dataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final Map<Object, Object> expectedData = new LinkedHashMap<>();
        final String key_1 = "Identifier";
        final String value_1 = "141";
        expectedData.put(key_1, value_1);
        final String key_2 = "LongMultilineDatum";
        final String value_2 = "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB";
        expectedData.put(key_2, value_2);

        // act & assert
        assertThat(rdfileReader.hasNext(), is(true));
        assertThat(rdfileReader.hasNext(), is(true));
        RdfileRecord rdfileRecord = rdfileReader.next();
        assertThat(rdfileReader.hasNext(), is(false));
        assertThat(rdfileReader.hasNext(), is(false));
        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> rdfileReader.next(), "expected to have reached end of file");
        assertThat(exception.getMessage(), is("RdfileReader reached end of file."));
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        Assertions.assertNull(rdfileRecord.getAtomContainer());
        IReaction reaction = rdfileRecord.getReaction();
        Assertions.assertEquals(2, reaction.getReactantCount());
        Assertions.assertEquals(0, reaction.getAgents().getAtomContainerCount());
        Assertions.assertEquals(1, reaction.getProductCount());

        // reactant #1
        IAtomContainer reactant1 = reaction.getReactants().getAtomContainer(0);
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(reactant1);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(reactant1);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        // reactant #2
        IAtomContainer reactant2 = reaction.getReactants().getAtomContainer(1);
        elementFrequencyMap = generateElementFrequencyMap(reactant2);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        bondOrderFrequencyMap = generateBondFrequencyMap(reactant2);
        Assertions.assertEquals(0, bondOrderFrequencyMap.size());
        // product
        IAtomContainer product = reaction.getProducts().getAtomContainer(0);
        Assertions.assertEquals(7, product.getAtomCount());
        elementFrequencyMap = generateElementFrequencyMap(product);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        Assertions.assertEquals(6, elementFrequencyMap.get(6));
        bondOrderFrequencyMap = generateBondFrequencyMap(product);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        // data block
        Assertions.assertEquals(value_1, reaction.getProperty(key_1));
        Assertions.assertEquals(value_2, reaction.getProperty(key_2));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testIterator_fourRecords_parsingErrorRecord3() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_fourRecords_parsingErrorRecord3.rdf";
        final RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final List<RdfileRecord> records = new ArrayList<>();
        final List<Map<Object, Object>> expectedDataList = new ArrayList<>();
        final Map<Object, Object> expectedData_1 = new LinkedHashMap<>();
        final String key_1 = "Identifier";
        final String value_1 = "141";
        expectedData_1.put(key_1, value_1);
        expectedDataList.add(expectedData_1);
        final Map<Object, Object> expectedData_2 = new LinkedHashMap<>();
        final String key_2 = "Identifier";
        final String value_2 = "142";
        expectedData_2.put(key_2, value_2);
        expectedDataList.add(expectedData_2);
        final Map<Object, Object> expectedData_3 = new LinkedHashMap<>();
        final String key_3 = "Identifier";
        final String value_3 = "211";
        expectedData_3.put(key_3, value_3);
        expectedDataList.add(expectedData_3);
        final Map<Object, Object> expectedData_4 = new LinkedHashMap<>();
        final String key_4 = "Identifier";
        final String value_4 = "143";
        expectedData_4.put(key_4, value_4);
        expectedDataList.add(expectedData_4);

        // act & assert
        assertThat(rdfileReader.hasNext(), is(true));
        assertThat(rdfileReader.hasNext(), is(true));
        records.add(rdfileReader.next());                   // reads record #1
        assertThat(rdfileReader.hasNext(), is(true));
        assertThat(rdfileReader.hasNext(), is(true));
        records.add(rdfileReader.next());                   // reads record #2
        assertThat(rdfileReader.hasNext(), is(true));
        assertThat(rdfileReader.hasNext(), is(true));
        records.add(rdfileReader.next());                   // reads record #4
        assertThat(rdfileReader.hasNext(), is(false));
        assertThat(rdfileReader.hasNext(), is(false));
        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> rdfileReader.next(), "expected to have reached end of file");
        assertThat(exception.getMessage(), is("RdfileReader reached end of file."));
        records.add(2, null);                 // insert empty element at position #3 that has broken record
        rdfileRecordEqualsRdfile(records, rdfFilename, expectedDataList, new Integer[]{3});

        // record #1 - molecule
        RdfileRecord rdfileRecord = records.get(0);
        Assertions.assertNull(rdfileRecord.getReaction());
        IAtomContainer atomContainer = rdfileRecord.getAtomContainer();
        Assertions.assertNotNull(atomContainer);
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        Assertions.assertEquals(value_1, atomContainer.getProperty(key_1));

        // record #2 - molecule
        rdfileRecord = records.get(1);
        Assertions.assertNull(rdfileRecord.getReaction());
        atomContainer = rdfileRecord.getAtomContainer();
        Assertions.assertNotNull(atomContainer);
        elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(2, elementFrequencyMap.get(8));
        bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(1, bondOrderFrequencyMap.get(IBond.Order.DOUBLE));
        Assertions.assertEquals(value_2, atomContainer.getProperty(key_2));

        // record #4 - molecule
        rdfileRecord = records.get(3);
        Assertions.assertNull(rdfileRecord.getReaction());
        atomContainer = rdfileRecord.getAtomContainer();
        Assertions.assertNotNull(atomContainer);
        elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(2, elementFrequencyMap.get(8));
        bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(1, bondOrderFrequencyMap.get(IBond.Order.DOUBLE));
        Assertions.assertEquals(value_4, atomContainer.getProperty(key_4));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testIterator_fourRecords_parsingErrorRecord3_skipRecordsOnErrorFalse() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_fourRecords_parsingErrorRecord3.rdf";
        final RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder, false);
        final List<RdfileRecord> records = new ArrayList<>();
        final List<Map<Object, Object>> expectedDataList = new ArrayList<>();
        final Map<Object, Object> expectedData_1 = new LinkedHashMap<>();
        final String key_1 = "Identifier";
        final String value_1 = "141";
        expectedData_1.put(key_1, value_1);
        expectedDataList.add(expectedData_1);
        final Map<Object, Object> expectedData_2 = new LinkedHashMap<>();
        final String key_2 = "Identifier";
        final String value_2 = "142";
        expectedData_2.put(key_2, value_2);
        expectedDataList.add(expectedData_2);
        final Map<Object, Object> expectedData_3 = new LinkedHashMap<>();
        expectedDataList.add(expectedData_3);
        final Map<Object, Object> expectedData_4 = new LinkedHashMap<>();
        expectedDataList.add(expectedData_4);

        // act & assert
        assertThat(rdfileReader.hasNext(), is(true));
        assertThat(rdfileReader.hasNext(), is(true));
        records.add(rdfileReader.next());                    // reads record #1
        assertThat(rdfileReader.hasNext(), is(true));
        assertThat(rdfileReader.hasNext(), is(true));
        records.add(rdfileReader.next());                    // reads record #2
        assertThat(rdfileReader.hasNext(), is(false)); // tries to read broken record #3
        assertThat(rdfileReader.hasNext(), is(false));
        // skipRecordsOnError is set to false, so we indicate EOF after first error
        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> rdfileReader.next(), "expected to have reached end of file");
        assertThat(exception.getMessage(), is("RdfileReader reached end of file."));
        records.add(null);                 // insert empty element at position #3 that has broken record
        records.add(null);                 // insert empty element at position #3 that has broken record
        rdfileRecordEqualsRdfile(records, rdfFilename, expectedDataList, new Integer[]{3, 4});

        // record #1 - molecule
        RdfileRecord rdfileRecord = records.get(0);
        Assertions.assertNull(rdfileRecord.getReaction());
        IAtomContainer atomContainer = rdfileRecord.getAtomContainer();
        Assertions.assertNotNull(atomContainer);
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        Assertions.assertEquals(value_1, atomContainer.getProperty(key_1));

        // record #2 - molecule
        rdfileRecord = records.get(1);
        Assertions.assertNull(rdfileRecord.getReaction());
        atomContainer = rdfileRecord.getAtomContainer();
        Assertions.assertNotNull(atomContainer);
        elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(2, elementFrequencyMap.get(8));
        bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(1, bondOrderFrequencyMap.get(IBond.Order.DOUBLE));
        Assertions.assertEquals(value_2, atomContainer.getProperty(key_2));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testIterator_fourRecords_parsingErrorRecord1And4() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_fourRecords_parsingErrorRecord1Record4.rdf";
        final RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename), chemObjectBuilder);
        final List<RdfileRecord> records = new ArrayList<>();
        final List<Map<Object, Object>> expectedDataList = new ArrayList<>();
        final Map<Object, Object> expectedData_1 = new LinkedHashMap<>();
        expectedDataList.add(expectedData_1);
        final Map<Object, Object> expectedData_2 = new LinkedHashMap<>();
        final String key_2 = "Identifier";
        final String value_2 = "142";
        expectedData_2.put(key_2, value_2);
        expectedDataList.add(expectedData_2);
        final Map<Object, Object> expectedData_3 = new LinkedHashMap<>();
        final String key_3 = "Identifier";
        final String value_3 = "211";
        expectedData_3.put(key_3, value_3);
        expectedDataList.add(expectedData_3);
        final Map<Object, Object> expectedData_4 = new LinkedHashMap<>();
        expectedDataList.add(expectedData_4);

        // act & assert
        assertThat(rdfileReader.hasNext(), is(true));
        records.add(rdfileReader.next());                   // reads record #2
        assertThat(rdfileReader.hasNext(), is(true));
        records.add(rdfileReader.next());                   // reads record #3
        assertThat(rdfileReader.hasNext(), is(false));
        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> rdfileReader.next(), "expected to have reached end of file");
        assertThat(exception.getMessage(), is("RdfileReader reached end of file."));
        records.add(0, null);                 // insert empty element at position #1 that had a broken record
        records.add(3, null);                 // insert empty element at position #4 that had a broken record
        rdfileRecordEqualsRdfile(records, rdfFilename, expectedDataList, new Integer[]{1, 4});

        // record #2 - molecule
        RdfileRecord rdfileRecord = records.get(1);
        Assertions.assertNull(rdfileRecord.getReaction());
        IAtomContainer atomContainer = rdfileRecord.getAtomContainer();
        Assertions.assertNotNull(atomContainer);
        Map<Integer,Integer> elementFrequencyMap = generateElementFrequencyMap(atomContainer);
        Assertions.assertEquals(2, elementFrequencyMap.get(8));
        Map<IBond.Order,Integer> bondOrderFrequencyMap = generateBondFrequencyMap(atomContainer);
        Assertions.assertEquals(1, bondOrderFrequencyMap.get(IBond.Order.DOUBLE));
        Assertions.assertEquals(value_2, atomContainer.getProperty(key_2));

        // record #3 - reaction
        rdfileRecord = records.get(2);
        Assertions.assertNull(rdfileRecord.getAtomContainer());
        IReaction reaction = rdfileRecord.getReaction();
        Assertions.assertNotNull(reaction);
        Assertions.assertEquals(2, reaction.getReactantCount());
        Assertions.assertEquals(0, reaction.getAgents().getAtomContainerCount());
        Assertions.assertEquals(1, reaction.getProductCount());
        // reactant #1
        IAtomContainer reactant1 = reaction.getReactants().getAtomContainer(0);
        elementFrequencyMap = generateElementFrequencyMap(reactant1);
        Assertions.assertEquals(7, elementFrequencyMap.get(6));
        bondOrderFrequencyMap = generateBondFrequencyMap(reactant1);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));
        // reactant #2
        IAtomContainer reactant2 = reaction.getReactants().getAtomContainer(1);
        elementFrequencyMap = generateElementFrequencyMap(reactant2);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        bondOrderFrequencyMap = generateBondFrequencyMap(reactant2);
        Assertions.assertEquals(0, bondOrderFrequencyMap.size());
        // product
        IAtomContainer product = reaction.getProducts().getAtomContainer(0);
        Assertions.assertEquals(7, product.getAtomCount());
        elementFrequencyMap = generateElementFrequencyMap(product);
        Assertions.assertEquals(1, elementFrequencyMap.get(35));
        Assertions.assertEquals(6, elementFrequencyMap.get(6));
        bondOrderFrequencyMap = generateBondFrequencyMap(product);
        Assertions.assertEquals(7, bondOrderFrequencyMap.get(IBond.Order.SINGLE));

        // tear down
        rdfileReader.close();
    }

    ///// private helper methods for this test class /////
    private List<String> readFile(String filename) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(filename)));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines;
    }

    private void rdfileRecordEqualsRdfile(final RdfileRecord record, final String filename, final Map<Object, Object> expectedData) throws IOException {
        List<RdfileRecord> records = new ArrayList<>();
        records.add(record);

        List<Map<Object, Object>> expectedDataList = new ArrayList<>();
        expectedDataList.add(expectedData);

        rdfileRecordEqualsRdfile(records, filename, expectedDataList);
    }

    private void rdfileRecordEqualsRdfile(final List<RdfileRecord> records, final String filename, final List<Map<Object, Object>> expectedDataList) throws IOException {
        rdfileRecordEqualsRdfile(records, filename, expectedDataList, new Integer[]{});
    }

    private void rdfileRecordEqualsRdfile(final List<RdfileRecord> records, final String filename, final List<Map<Object, Object>> expectedDataList, final Integer[] recordsToSkip) throws IOException {
        final Pattern registryNumberPattern = Pattern.compile("\\A\\$([MR]FMT ?)?\\$([MR][EI]REG) (.+)\\Z");
        final Pattern newRecordPattern = Pattern.compile("\\A(\\$[MR]FMT)|((\\$[MR]FMT )?\\$[MR][IE]REG (.+))\\Z");
        final Pattern ctabVersionPattern = Pattern.compile("(\\$RXN)|( (V[23]0{3}))\\n");

        List<String> expectedLines = readFile(filename);

        // convert the recordsToSkip to a set
        Set<Integer> recordsToSkipAsSet = new HashSet<>(Arrays.asList(recordsToSkip));

        // assert that number of elements in records, the number of elements in expectedDataList, and the number of records in the file are equal
        int recordCounter = 0;
        for (String expectedLine : expectedLines) {
            Matcher matcher = newRecordPattern.matcher(expectedLine);
            if (matcher.matches()) {
                recordCounter++;
            }
        }
        Assertions.assertEquals(recordCounter, records.size(), "actual (read from file) and expected (list of RdfRecords) don't have the same number of records");
        Assertions.assertEquals(recordCounter, expectedDataList.size(), "actual and expectedDataList don't have the same number of records");

        // line 1, line 2: skip
        int expectedLinesIndex = 2;

        recordCounter = 0;
        // loop over all lines in the file
        for (; expectedLinesIndex < expectedLines.size(); recordCounter++, expectedLinesIndex++) {
            // if this record is in recordsToSkip we fast-forward to the next record
            if (recordsToSkipAsSet.contains(recordCounter + 1)) {
                expectedLinesIndex = skipToNextRecord(expectedLinesIndex, expectedLines);
                continue;
            }

            RdfileRecord record = records.get(recordCounter);
            String line = expectedLines.get(expectedLinesIndex);

            // line 3: assert int/ext registry number
            Matcher matcherIntRegNumber = registryNumberPattern.matcher(line);
            if (matcherIntRegNumber.matches()) {
                if (matcherIntRegNumber.group(2).endsWith("IREG")) {
                    Assertions.assertEquals(matcherIntRegNumber.group(3), record.getInternalRegistryNumber(), "actual and expected don't have the same internal registry number");
                } else if (matcherIntRegNumber.group(2).endsWith("EREG")) {
                    Assertions.assertEquals(matcherIntRegNumber.group(3), record.getExternalRegistryNumber(), "actual and expected don't have the same external registry number");
                } else {
                    Assertions.assertNull(record.getInternalRegistryNumber(), "internal registry number expected to be non-present (null)");
                    Assertions.assertNull(record.getExternalRegistryNumber(), "external registry number expected to be non-present (null)");
                }
            }

            // line 4: indicates whether record is molfile or rxn
            StringBuilder stringBuilder = new StringBuilder();
            line = expectedLines.get(++expectedLinesIndex);
            if (line.startsWith(RdfileReader.MOLFILE_START)) {
                Assertions.assertTrue(record.isMolfile(), "expected record to be molfile");
            } else if (line.startsWith(RdfileReader.RXNFILE_START)) {
                Assertions.assertTrue(record.isRxnFile(), "expected record to be rxnfile");
                stringBuilder.append(line).append(RdfileReader.LINE_SEPARATOR_NEWLINE);
            }

            // from line 5: molfile or rxn
            while (++expectedLinesIndex < expectedLines.size()) {
                line = expectedLines.get(expectedLinesIndex);

                if (line.startsWith("$") && !line.startsWith(RdfileReader.MOLFILE_START)) {
                    expectedLinesIndex--;
                    break;
                }

                stringBuilder.append(line).append(RdfileReader.LINE_SEPARATOR_NEWLINE);
            }

            // skip data block and read until next record starts
            expectedLinesIndex = skipToNextRecord(expectedLinesIndex, expectedLines);

            // figure out the MDL CTAB version
            Matcher matcher = ctabVersionPattern.matcher(stringBuilder.toString());
            List<RdfileRecord.CTAB_VERSION> ctabVersions = new ArrayList<>();
            while(matcher.find()) {
                if (matcher.group(1) != null) {
                    ctabVersions.add(RdfileRecord.CTAB_VERSION.V2000);
                } else if (matcher.group(3) != null) {
                    ctabVersions.add(RdfileRecord.CTAB_VERSION.valueOf(matcher.group(3)));
                }
            }
            assertThat(ctabVersions.size(), greaterThanOrEqualTo(1));
            for (int index = 0; index < ctabVersions.size() - 1; index++) {
                if (!ctabVersions.get(index).equals(ctabVersions.get(index + 1))) {
                    Assertions.fail("All CTAB version strings of a record must indicate the same version: " + ctabVersions);
                }
            }
            assertThat(ctabVersions.get(0), is(record.getCtabVersion()));

            // assert content
            String[] expectedContent = stringBuilder.toString().split(RdfileReader.LINE_SEPARATOR_NEWLINE);
            String[] actualContent = record.getContent().split(RdfileReader.LINE_SEPARATOR_NEWLINE);
            Assertions.assertEquals(expectedContent.length, actualContent.length, "expected content and actual content has different number of lines");
            for (int index = 0; index < actualContent.length; index++) {
                Assertions.assertEquals(expectedContent[index], actualContent[index], "expected and actual content has difference in line " + (line + 1));
            }

            // assert data block
            final Map<Object, Object> expectedData = expectedDataList.get(recordCounter);
            Assertions.assertEquals(expectedData.size(), record.getData().size(), "expected and actual data block differ in size");
            List<Object> expectedKeys = new ArrayList<>(expectedData.keySet());
            List<Object> actualKeys = new ArrayList<>(record.getData().keySet());
            for (int index = 0; index < expectedKeys.size(); index++) {
                Assertions.assertEquals(expectedKeys.get(index), actualKeys.get(index),
                        "expected and actual data key of data item with index " + (index + 1) + " is different");
                Assertions.assertEquals(expectedData.get(expectedKeys.get(index)), record.getData().get(actualKeys.get(index)),
                        "expected and actual data value of data item with index " + (index + 1) + " is different");
            }
        }
    }

    private int skipToNextRecord(int expectedLinesIndex, List<String> expectedLines) {
        final Pattern newRecordPattern = Pattern.compile("\\A(\\$[MR]FMT)|((\\$[MR]FMT )?\\$[MR][IE]REG (.+))\\Z");
        // skip to next record
        while (++expectedLinesIndex < expectedLines.size()) {
            String line = expectedLines.get(expectedLinesIndex);
            Matcher newRecordMatcher = newRecordPattern.matcher(line);
            if (newRecordMatcher.matches()) {
                expectedLinesIndex--;
                break;
            }
        }
        return expectedLinesIndex;
    }

    private Map<Integer,Integer> generateElementFrequencyMap(IAtomContainer atomContainer) {
        Map<Integer,Integer> elementFrequencyMap = new HashMap<>();
        if (atomContainer == null || atomContainer.isEmpty()) {
            return elementFrequencyMap;
        }
        for (IAtom atom: atomContainer.atoms()) {
            elementFrequencyMap.merge(atom.getAtomicNumber(), 1, (v1, v2) -> v1 + v2);
        }
        return elementFrequencyMap;
    }

    private Map<IBond.Order,Integer> generateBondFrequencyMap(IAtomContainer atomContainer) {
        Map<IBond.Order,Integer> bondOrderFrequencyMap = new HashMap<>();
        if (atomContainer == null || atomContainer.isEmpty()) {
            return bondOrderFrequencyMap;
        }
        for (IBond bond: atomContainer.bonds()) {
            bondOrderFrequencyMap.merge(bond.getOrder(), 1, (v1, v2) -> v1 + v2);
        }
        return bondOrderFrequencyMap;
    }
}
