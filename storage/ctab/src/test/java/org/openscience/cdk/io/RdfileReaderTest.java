package org.openscience.cdk.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RdfileReaderTest {

    @Test
    void testRdfile_oneRecord_mfmt_mol_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mfmt_mol_dataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        final Map<String,String> expectedData = new LinkedHashMap<>();
        expectedData.put("Identifier", "141");
        expectedData.put("LongMultilineDatum", "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB");

        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_mfmt_mireg_mol_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mfmt_mireg_mol_dataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        final Map<String,String> expectedData = new LinkedHashMap<>();
        expectedData.put("Identifier", "141");
        expectedData.put("LongMultilineDatum", "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB");

        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_mfmt_mereg_mol_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mfmt_mereg_mol_dataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        final Map<String,String> expectedData = new LinkedHashMap<>();
        expectedData.put("Identifier", "141");
        expectedData.put("LongMultilineDatum", "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB");


        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_mireg_mol_noDataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mireg_mol_noDataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        final Map<String,String> expectedData = new LinkedHashMap<>();

        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_mereg_mol_noDataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mereg_mol_noDataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        final Map<String,String> expectedData = new LinkedHashMap<>();

        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rfmt_rxn_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rfmt_rxn_dataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        final Map<String,String> expectedData = new LinkedHashMap<>();
        expectedData.put("Identifier", "141");
        expectedData.put("LongMultilineDatum", "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB");

        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rfmt_rireg_rxn_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rfmt_rireg_rxn_dataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        final Map<String,String> expectedData = new LinkedHashMap<>();
        expectedData.put("Identifier", "141");
        expectedData.put("LongMultilineDatum", "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB");

        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rfmt_rereg_rxn_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rfmt_rereg_rxn_dataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        final Map<String,String> expectedData = new LinkedHashMap<>();
        expectedData.put("Identifier", "141");
        expectedData.put("LongMultilineDatum", "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUVbfkPnBB");

        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rireg_rxn_noDataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rireg_rxn_noDataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        final Map<String,String> expectedData = new LinkedHashMap<>();

        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rereg_rxn_noDataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rereg_rxn_noDataBlock.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        final Map<String,String> expectedData = new LinkedHashMap<>();

        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rereg_rxn_dataBlockWithEmbeddedMolecule() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rereg_rxn_dataBlockWithEmbeddedMolecule.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        final Map<String,String> expectedData = new LinkedHashMap<>();
        expectedData.put("CATALYST_1", "$MFMT\n" +
                "\n" +
                "  Mrv2219  110920222203\n" +
                "\n" +
                "  2  1  0  0  0  0  0  0  0  0999 V2000\n" +
                "   -5.0000    3.0000    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    5.0000   -3.0000    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  2  0  0  0  0\n" +
                "M  END");
        expectedData.put("Identifier", "141");
        expectedData.put("SOLVENT_1", "$MFMT\n" +
                "\n" +
                "  Mrv2219  110920222203\n" +
                "\n" +
                "  3  2  0  0  0  0  0  0  0  0999 V2000\n" +
                "    5.0000   -0.5000    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -5.0000   -3.5000    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.5000    3.5000    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  3  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "M  END");
        expectedData.put("LongMultilineDatum", "5TMSS4aK6Wkq7LOHaHORhMe3PCuJxKBWnUeyf1uxEsWjdYWWNLlV6FPo14G7Jv9lhwzVChf9A" +
                "XjGOe9gSgO0I7u5SEtqJi9492soYNHwgvEsApwkmoRZhwdC9CESO1A3lGUgeGim0s4fhQEdbthmPBTUV" +
                "bfkPnBBS4aK6Wkq7LOHaHORhMe3PCuJxKBqJi9492soYNHwgvESO1A3lGUgeGiMSS4aK6Wkq7LOHaHOG");

        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        rdfileRecordEqualsRdfile(rdfileRecord, rdfFilename, expectedData);

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line1() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line1.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in line 1: Expected the line to exactly contain '$RDFILE 1', but instead found '$RDFILE 2'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line2() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line2.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in line 2: Expected the line to start with '$DATM', but instead found '$FATM    11/09/22 22:12'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line3_mfmt_rireg() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line3_mfmt_rireg.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in line 3: Expected either '$MIREG' or '$MEREG' after '$MFMT', but instead found '$RIREG 7483765'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line3_rfmt_nonsense() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line3_rmft_nonsense.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in line 3: Expected either '$MIREG' or '$MEREG' after '$MFMT', but instead found 'NONSENSE'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line3_notMfmt_notRfmt() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line3_notMfmt_NotRfmt.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in line 3: Expected the line to specify the molecule or reaction identifier, but instead found '$LFMT'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line4_mfmt_rxn() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line4_mfmt_rxn.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in line 4: Expected the line to start with '$MOL', but instead found '$RXN'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line4_rfmt_mol() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line4_rfmt_mol.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in line 4: Expected the line to start with '$RXN', but instead found '$MOL'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line9_mfmt_noMEnd() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line9_mfmt_noMEnd.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in line 9: Expected molfile to end with 'M  END', but instead found 'null'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line10_molfile_lineStartsWithDollarSign() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line10_molfile_lineStartsWithDollarSign.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in line 10: Unexpected character '$' within molfile at start of line '$DTYPE Identifier'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line8_rxnfile_countsLine_oneDigit() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line8_componentCounts_oneDigit.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in line 8: Incorrect formatting of the line that indicates the number of reaction components '  2'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line8_rxnfile_countsLine_oneDigitOneLetter() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line8_componentCounts_oneDigitOneLetter.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in line 8: Incorrect formatting of the line that indicates the number of reaction components '  2  D'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line56_rxnfile_countsDontMatchMolfiles() throws IOException {
        // arrange
        final String rdfFilename = "Rdfile_error_line56_rxnfile_countsDontMatchMolfiles.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in RXN record: The sum of the number of reactants (2), products (1) and agents (1) is not equal to " +
                "the number of Molfile entries 3 in the record."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line57_datumExpected() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line57_datumExpected.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in data block in line 57. Expected line to start with '$DATUM' " +
                "and to either have <= 80 characters or exactly 81 characters and ending with a '+'', but instead found '$datum 141'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line58_dtypeExpected() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line58_dtypeExpected.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in data block in line 58. Expected line to start with '$DTYPE' and be followed by a space and a string, but instead found '$VTYPE LongMultilineDatum'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line58_dtypeValueExpected() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line58_dtypeValueExpected.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
        assertThat(exception.getMessage(), is("Error in data block in line 58. Expected line to start with '$DTYPE' and be followed by a space and a string, but instead found '$DTYPE'."));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_error_line59_plusSignExpected() throws IOException {
        // arrange
        final String rdfFilename = "rdfile_error_line59_plusSignExpected.rdf";
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
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
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act & assert
        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
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
        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));

        // act
        RdfileRecord rdfileRecord = rdfileReader.read();
        rdfileRecord = rdfileReader.read();
        rdfileRecord = rdfileReader.read();
        rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only four records in this RDfile");

        // tear down
        rdfileReader.close();
    }

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

    private void rdfileRecordEqualsRdfile(final RdfileRecord record, final String filename, final Map<String,String> expectedData) throws IOException {
        List<RdfileRecord> records = new ArrayList<>();
        records.add(record);

        List<Map<String,String>> listOfExpectedData = new ArrayList<>();
        listOfExpectedData.add(expectedData);

        rdfileRecordEqualsRdfile(records, filename, listOfExpectedData);
    }

    private void rdfileRecordEqualsRdfile(final List<RdfileRecord> records, final String filename, final List<Map<String,String>> listOfExpectedData) throws IOException {
        final RdfileRecord record = records.get(0);
        final Map<String,String> expectedData = listOfExpectedData.get(0);

//        final Pattern internalRegistryNumberPattern = Pattern.compile("\\A(\\$MFMT( \\$((MIREG)|(MEREG)) (.+))?)|(\\$((MIREG)|(MEREG)) (.+))\\Z");
        final Pattern internalRegistryNumberPattern = Pattern.compile("\\A\\$([MR]FMT )?\\$[MR]IREG (.+)\\Z");
        final Pattern externalRegistryNumberPattern = Pattern.compile("\\A\\$([MR]FMT )?\\$[MR]EREG (.+)\\Z");
        final Pattern newRecordPattern = Pattern.compile("\\A\\$([MR]FMT )?\\$[MR][IE]REG (.+)\\Z");

        List<String> expectedLines = readFile(filename);

        // line 1, line 2: skip
        int indexExpected = 2;
        String line = expectedLines.get(indexExpected);

        // line 3: assert int/ext registry number
        Matcher matcherIntRegNumber = internalRegistryNumberPattern.matcher(line);
        Matcher matcherExtRegNumber = externalRegistryNumberPattern.matcher(line);
        if (matcherIntRegNumber.matches()) {
            Assertions.assertEquals(matcherIntRegNumber.group(2), record.getInternalRegistryNumber(), "");
        } else if (matcherExtRegNumber.matches()) {
            Assertions.assertEquals(matcherExtRegNumber.group(2), record.getExternalRegistryNumber(), "");
        } else if (line.startsWith(RdfileReader.MOLECULE_FMT) || line.startsWith(RdfileReader.REACTION_FMT)) {
            Assertions.assertNull(record.getInternalRegistryNumber(), "");
            Assertions.assertNull(record.getExternalRegistryNumber(), "");
        }

        // line 4: indicates whether record is molfile or rxn
        StringBuilder stringBuilder = new StringBuilder();
        line = expectedLines.get(++indexExpected);
        if (line.startsWith(RdfileReader.MOLFILE_START)) {
            Assertions.assertTrue(record.isMolfile(), "");
        } else if (line.startsWith(RdfileReader.RXNFILE_START)) {
            Assertions.assertTrue(record.isRxnFile());
            stringBuilder.append(line).append(RdfileReader.LINE_SEPARATOR_NEWLINE);
        }

        // from line 5: molfile or rxn
        while (++indexExpected < expectedLines.size()) {
            line = expectedLines.get(indexExpected);

            if (line.startsWith("$") && !line.startsWith(RdfileReader.MOLFILE_START)) {
                indexExpected--;
                break;
            }

            stringBuilder.append(line).append(RdfileReader.LINE_SEPARATOR_NEWLINE);
        }

        // skip data block and read until next record starts


        // assert content
        String[] expectedContent = stringBuilder.toString().split(RdfileReader.LINE_SEPARATOR_NEWLINE);
        String[] actualContent = record.getContent().split(RdfileReader.LINE_SEPARATOR_NEWLINE);
        Assertions.assertEquals(expectedContent.length, actualContent.length, "");
        for (int index = 0; index < actualContent.length; index++) {
            Assertions.assertEquals(expectedContent[index], actualContent[index], "");
        }

        // data block
        Assertions.assertEquals(expectedData.size(), record.getData().size(), "");
        List<String> expectedKeys = new ArrayList<>(expectedData.keySet());
        List<String> actualKeys = new ArrayList<>(record.getData().keySet());
        for(int index = 0; index < expectedKeys.size(); index++) {
            Assertions.assertEquals(expectedKeys.get(index), actualKeys.get(index), "");
            Assertions.assertEquals(expectedData.get(expectedKeys.get(index)), record.getData().get(actualKeys.get(index)), "");
        }
    }
}
