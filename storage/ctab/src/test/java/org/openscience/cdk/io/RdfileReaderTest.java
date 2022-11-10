package org.openscience.cdk.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RdfileReaderTest {

    @Test
    void testRdfile_oneRecord_mfmt_mol_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mfmt_mol_dataBlock.rdf";
        final List<String> expectedContent = readFile(rdfFilename);
        // remove the first four lines (RdFile header + $MOL)
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);

        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        Assertions.assertNull(rdfileRecord.getInternalRegistryNumber());
        Assertions.assertNull(rdfileRecord.getExternalRegistryNumber());
        Assertions.assertTrue(rdfileRecord.isMolfile());
        Assertions.assertFalse(rdfileRecord.isRxnFile());

        Assertions.assertEquals(expectedContent, Arrays.asList(rdfileRecord.getContent().split(RdfileReader.LINE_SEPARATOR_NEWLINE)));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_mfmt_mireg_mol_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mfmt_mireg_mol_dataBlock.rdf";
        final String internalRegistryNumber = "123456";
        final List<String> expectedContent = readFile(rdfFilename);
        // remove the first four lines (RdFile header + $MOL)
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);

        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        Assertions.assertEquals(internalRegistryNumber, rdfileRecord.getInternalRegistryNumber());
        Assertions.assertNull(rdfileRecord.getExternalRegistryNumber());
        Assertions.assertTrue(rdfileRecord.isMolfile());
        Assertions.assertFalse(rdfileRecord.isRxnFile());

        Assertions.assertEquals(expectedContent, Arrays.asList(rdfileRecord.getContent().split(RdfileReader.LINE_SEPARATOR_NEWLINE)));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_mfmt_mereg_mol_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mfmt_mereg_mol_dataBlock.rdf";
        final String externalRegistryNumber = "789abc";
        final List<String> expectedContent = readFile(rdfFilename);
        // remove the first four lines (RdFile header + $MOL)
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);

        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        Assertions.assertNull(rdfileRecord.getInternalRegistryNumber());
        Assertions.assertEquals(externalRegistryNumber, rdfileRecord.getExternalRegistryNumber());
        Assertions.assertTrue(rdfileRecord.isMolfile());
        Assertions.assertFalse(rdfileRecord.isRxnFile());

        Assertions.assertEquals(expectedContent, Arrays.asList(rdfileRecord.getContent().split(RdfileReader.LINE_SEPARATOR_NEWLINE)));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_mireg_mol_noDataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mireg_mol_noDataBlock.rdf";
        final String internalRegistryNumber = "19283746";
        final List<String> expectedContent = readFile(rdfFilename);
        // remove the first four lines (RdFile header + $MOL)
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);

        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        Assertions.assertEquals(internalRegistryNumber, rdfileRecord.getInternalRegistryNumber());
        Assertions.assertNull(rdfileRecord.getExternalRegistryNumber());
        Assertions.assertTrue(rdfileRecord.isMolfile());
        Assertions.assertFalse(rdfileRecord.isRxnFile());

        Assertions.assertEquals(expectedContent, Arrays.asList(rdfileRecord.getContent().split(RdfileReader.LINE_SEPARATOR_NEWLINE)));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_mereg_mol_noDataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_mereg_mol_noDataBlock.rdf";
        final String externalRegistryNumber = "abcdef1";
        final List<String> expectedContent = readFile(rdfFilename);
        // remove the first four lines (RdFile header + $MOL)
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);

        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        Assertions.assertNull(rdfileRecord.getInternalRegistryNumber());
        Assertions.assertEquals(externalRegistryNumber, rdfileRecord.getExternalRegistryNumber());
        Assertions.assertTrue(rdfileRecord.isMolfile());
        Assertions.assertFalse(rdfileRecord.isRxnFile());

        Assertions.assertEquals(expectedContent, Arrays.asList(rdfileRecord.getContent().split(RdfileReader.LINE_SEPARATOR_NEWLINE)));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rfmt_rxn_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rfmt_rxn_dataBlock.rdf";
        final List<String> expectedContent = readFile(rdfFilename);
        // remove the first three lines (RdFile header)
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);

        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        Assertions.assertNull(rdfileRecord.getInternalRegistryNumber());
        Assertions.assertNull(rdfileRecord.getExternalRegistryNumber());
        Assertions.assertFalse(rdfileRecord.isMolfile());
        Assertions.assertTrue(rdfileRecord.isRxnFile());

        Assertions.assertEquals(expectedContent, Arrays.asList(rdfileRecord.getContent().split(RdfileReader.LINE_SEPARATOR_NEWLINE)));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rfmt_rireg_rxn_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rfmt_rireg_rxn_dataBlock.rdf";
        final List<String> expectedContent = readFile(rdfFilename);
        final String internalRegistryNumber = "7483765";
        // remove the first three lines (RdFile header)
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);

        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        Assertions.assertEquals(internalRegistryNumber, rdfileRecord.getInternalRegistryNumber());
        Assertions.assertNull(rdfileRecord.getExternalRegistryNumber());
        Assertions.assertFalse(rdfileRecord.isMolfile());
        Assertions.assertTrue(rdfileRecord.isRxnFile());

        Assertions.assertEquals(expectedContent, Arrays.asList(rdfileRecord.getContent().split(RdfileReader.LINE_SEPARATOR_NEWLINE)));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rfmt_rereg_rxn_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rfmt_rereg_rxn_dataBlock.rdf";
        final List<String> expectedContent = readFile(rdfFilename);
        final String externalRegistryNumber = "df83623a";
        // remove the first three lines (RdFile header)
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);

        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        Assertions.assertNull(rdfileRecord.getInternalRegistryNumber());
        Assertions.assertEquals(externalRegistryNumber, rdfileRecord.getExternalRegistryNumber());
        Assertions.assertFalse(rdfileRecord.isMolfile());
        Assertions.assertTrue(rdfileRecord.isRxnFile());

        Assertions.assertEquals(expectedContent, Arrays.asList(rdfileRecord.getContent().split(RdfileReader.LINE_SEPARATOR_NEWLINE)));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rireg_rxn_dataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rireg_rxn_noDataBlock.rdf";
        final List<String> expectedContent = readFile(rdfFilename);
        final String internalRegistryNumber = "8547221";
        // remove the first three lines (RdFile header)
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);

        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        Assertions.assertEquals(internalRegistryNumber, rdfileRecord.getInternalRegistryNumber());
        Assertions.assertNull(rdfileRecord.getExternalRegistryNumber());
        Assertions.assertFalse(rdfileRecord.isMolfile());
        Assertions.assertTrue(rdfileRecord.isRxnFile());

        Assertions.assertEquals(expectedContent, Arrays.asList(rdfileRecord.getContent().split(RdfileReader.LINE_SEPARATOR_NEWLINE)));

        // tear down
        rdfileReader.close();
    }

    @Test
    void testRdfile_oneRecord_rereg_rxn_noDataBlock() throws IOException, CDKException {
        // arrange
        final String rdfFilename = "rdfile_oneRecord_rereg_rxn_noDataBlock.rdf";
        final List<String> expectedContent = readFile(rdfFilename);
        final String externalRegistryNumber = "283733fhfa";
        // remove the first three lines (RdFile header)
        expectedContent.remove(0);
        expectedContent.remove(0);
        expectedContent.remove(0);

        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
        // act
        RdfileRecord rdfileRecord = rdfileReader.read();

        // assert
        Assertions.assertNull(rdfileReader.read(), "Expected null as there is only one record in this RDfile");
        Assertions.assertNull(rdfileRecord.getInternalRegistryNumber());
        Assertions.assertEquals(externalRegistryNumber, rdfileRecord.getExternalRegistryNumber());
        Assertions.assertFalse(rdfileRecord.isMolfile());
        Assertions.assertTrue(rdfileRecord.isRxnFile());

        Assertions.assertEquals(expectedContent, Arrays.asList(rdfileRecord.getContent().split(RdfileReader.LINE_SEPARATOR_NEWLINE)));

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

//    @Test
//    void testRdfile_error_line56_rxnfile_dtypeOrNewRecordExpected() throws IOException, CDKException {
//        // arrange
//        final String rdfFilename = "rdfile_error_line56_rxnfile_dtypeOrNewRecordExpected.rdf";
//        RdfileReader rdfileReader = new RdfileReader(RdfileReader.class.getResourceAsStream(rdfFilename));
//
//        // act & assert
//        Exception exception = Assertions.assertThrows(CDKException.class, () -> rdfileReader.read());
//        assertThat(exception.getMessage(), is("Error in line 56: : Expected start of data block '$DTYPE' or start of next record, but instead found 'M  END'."));
//
//        // tear down
//        rdfileReader.close();
//    }

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
}