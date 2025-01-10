/*
 * Copyright (C) 2022 Uli Fechner
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.openscience.cdk.io;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Iterating reader for RDFiles.
 * <p>
 * This class facilitates reading RDFiles the specification of which was initially published
 * in {@cdk.cite DAL92} and is now maintained by Daussault Systems {@cdk.cite Dassault20}.
 * </p>
 * An RDFile is composed of
 * <ol>
 *     <li>an RDFile header</li>
 *     <li>one or more records where each record comprises
 *     <ol>
 *         <li>an optional internal or external registry number</li>
 *         <li>a molecule represented as a MolFile in V2000 or V3000 format <b>or</b>
 *         a reaction represented as an RxnFile in V2000 or V3000 format</li>
 *         <li>an optional data block that consists of one or more (data field identifier, data) pairs</li>
 *     </ol>
 *     </li>
 * </ol>
 * Here is an example of how to read an RDF that is expected to only contain molecules:
 * <pre>
 * // read an RDF that is expected to only contain molecules
 * {@code List<IAtomContainer> molecules = new ArrayList<>();}
 * try (RdfileReader rdfileReader = new RdfileReader(new FileReader("molecules.rdf"), SilentChemObjectBuilder.getInstance())) {
 *     while(rdfileReader.hasNext()) {
 *       final RdfileRecord rdfileRecord = rdfileReader.next();
 *       if (rdfileRecord.isMolfile()) {
 *         molecules.add(rdfileRecord.getAtomContainer());
 *      } else {
 *       // create log entry or throw exception as only molecules are expected in this RDF
 *     }
 *   }
 * }
 * </pre>
 * <p>
 * By default, any remaining records are skipped if an error is encountered in a record. This can be changed
 * by using one of the constructors that allows to provide a boolean value for the argument
 * {@code continueOnError} (one takes an {@code #RdfileReader(InputStream,IChemObject,boolean) InputStream},
 * the other one a {@code #RdfileReader(Reader,IChemObject,boolean) Reader}).
 * </p>
 *
 * @see RdfileRecord
 * @author Uli Fechner
 */
public final class RdfileReader implements Closeable, Iterator<RdfileRecord> {
    private static final ILoggingTool LOGGER = LoggingToolFactory.createLoggingTool(RdfileReader.class);
    static final String RDFILE_VERSION_1 = "$RDFILE 1";
    static final String DATM = "$DATM";
    static final String REACTION_FMT = "$RFMT";
    static final String REACTION_INT_REG = "$RIREG";
    static final String REACTION_EXT_REG = "$REREG";
    static final String RXNFILE_START = "$RXN";
    static final String MOLECULE_FMT = "$MFMT";
    static final String MOLECULE_INT_REG = "$MIREG";
    static final String MOLECULE_EXT_REG = "$MEREG";
    static final String MOLFILE_START = "$MOL";
    static final String M_END = "M  END";
    static final String DTYPE = "$DTYPE";
    static final String DATUM = "$DATUM";
    static final String LINE_SEPARATOR_NEWLINE = "\n";
    static final String PLUS_SIGN = "+";
    private static final Pattern DTYPE_KEY = Pattern.compile("\\A\\" + DTYPE + " (.+)\\Z");
    private static final Pattern DATUM_VALUE_FIRSTLINE = Pattern.compile("\\A\\" + DATUM + " (.{73}\\+|.{1,72})\\Z");
    private static final Pattern MDL_CTAB_VERSION = Pattern.compile("(V[23]0{3})\\Z");
    private static final Pattern MDL_RXN_VERSION = Pattern.compile("\\A\\$RXN ?(V30{3})?\\Z");

    private boolean headerRead;
    private String previousLine;
    private int lineCounter;
    private RdfileRecord nextRecord;
    private boolean endOfFile;
    private final boolean continueOnError;
    private final BufferedReader bufferedReader;
    private final IChemObjectBuilder chemObjectBuilder;

    /**
     * Creates a new RdfileReader instance with the given InputStream and IChemObjectBuilder.
     *
     * @param in the InputStream serving the RDfile data
     * @param chemObjectBuilder the IChemObjectBuilder for creating CDK objects
     */
    public RdfileReader(InputStream in, IChemObjectBuilder chemObjectBuilder) {
        this(in, chemObjectBuilder, true);
    }

    /**
     * Creates a new RdfileReader instance with the given InputStream and IChemObjectBuilder.
     * <p>
     * If {@code continueOnError} is {@code true} remaining records are processed when an error
     * is encountered; if {@code false} all remaining records in the file are skipped.
     * </p>
     *
     * @param in the InputStream serving the RDfile data
     * @param chemObjectBuilder the IChemObjectBuilder for creating CDK objects
     * @param continueOnError determines whether to continue processing records in case an error is encountered
     */
    public RdfileReader(InputStream in, IChemObjectBuilder chemObjectBuilder, boolean continueOnError) {
        this(new InputStreamReader(in, StandardCharsets.UTF_8), chemObjectBuilder, continueOnError);
    }

    /**
     * Creates a new RdfileReader instance with the given InputStream and IChemObjectBuilder.
     * <p>
     * If {@code continueOnError} is {@code true} remaining records are processed when an error
     * is encountered; if {@code false} all remaining records in the file are skipped.
     * </p>
     *
     * @param reader the Reader providing the RDfile data
     * @param chemObjectBuilder the IChemObjectBuilder for creating CDK objects
     * @param continueOnError determines whether to continue processing records in case an error is encountered
     */
    public RdfileReader(Reader reader, IChemObjectBuilder chemObjectBuilder, boolean continueOnError) {
        if (reader instanceof BufferedReader) {
            this.bufferedReader = (BufferedReader) reader;
        } else {
            this.bufferedReader = new BufferedReader(reader);
        }

        if (chemObjectBuilder == null)
            throw new NullPointerException("A ChemObjectBuilder must be provided or available on the class path!");
        this.chemObjectBuilder = chemObjectBuilder;
        this.continueOnError = continueOnError;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        if (nextRecord != null)
            return true;

        if (endOfFile)
            return false;

        nextRecord = readNext();
        return nextRecord != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RdfileRecord next() {
        if (nextRecord == null && !endOfFile) {
            nextRecord = readNext();
        }

        if (nextRecord == null) {
            throw new NoSuchElementException("RdfileReader reached end of file.");
        }

        RdfileRecord returnValue = nextRecord;
        nextRecord = null;
        return returnValue;
    }

    RdfileRecord readNext() {
        try {
            return doReadNext();
        } catch (IOException exception) {
            LOGGER.error("I/O error when reading RDfile: " + exception.getMessage());
            // we don't try to recover from an I/O error but fold our sails for this file
            return null;
        } catch (CDKException exception) {
            LOGGER.error("Parsing error when reading RDfile: " + exception.getMessage());

            // if the reader should NOT continue processing the remaining records if a record with an error is
            // encountered we signal EOF to callers upstream
            if (!continueOnError) {
                endOfFile = true;
                return null;
            }

            // a CDKException being thrown due to a parsing error could happen when
            // EITHER a complete record OR part of a record has been read
            // We try to recover from a parsing error of a record by moving on to the next record
            // the rest of this record - if any - is destined for /dev/null
            try {
                String line;
                while ((line = nextLine()) != null) {
                    if (isStartOfNewRecord(line)) {
                        pushBack(line);
                        break;
                    }
                }
            } catch (IOException ioException) {
                LOGGER.error("I/O error when reading RDfile: " + ioException.getMessage());
                // we don't try to recover from an I/O error but fold our sails for this file
                return null;
            }
        }
        return readNext();
    }

    RdfileRecord doReadNext() throws IOException, CDKException {
        if (!headerRead) {
            // line 1: $RDFILE 1
            final String header = nextLine();
            if (header == null || !header.startsWith(RDFILE_VERSION_1))
                throw new CDKException("Error in line " + lineCounter + ": Expected the line to exactly contain '" + RDFILE_VERSION_1 + "', but instead found '" + header + "'.");

            // line 2: $DATM    dd/mm/yy hh:mm
            final String date = nextLine();
            if (date == null || !date.startsWith("$DATM"))
                throw new CDKException("Error in line " + lineCounter + ": Expected the line to start with '" + DATM + "', but instead found '" + date + "'.");

            headerRead = true;
        }

        String line = nextLine();
        if (line == null) {
            endOfFile = true;
            return null;
        }

        // line 3: molecule and reaction identifiers
        // the molecule identifier can be one of the following:
        // $MFMT [$MIREG internal-regno]
        // $MFMT [$MEREG external-regno]
        // $MIREG internal-regno
        // $MEREG external-regno
        RdfileRecord rdFileRecord;
        rdFileRecord = processMoleculeAndReactionIdentifiers(new CharIter(line), MOLECULE_FMT, MOLECULE_INT_REG, MOLECULE_EXT_REG, false);
        if (rdFileRecord == null) {
            // the reaction identifier can be one of the following:
            // $RFMT [$RIREG internal-regno]
            // $RFMT [$REREG external-regno]
            // $RIREG internal-regno
            // $REREG external-regno
            rdFileRecord = processMoleculeAndReactionIdentifiers(new CharIter(line), REACTION_FMT, REACTION_INT_REG, REACTION_EXT_REG, true);
        }

        // the molecule or reaction identifier is not properly formatted
        if (rdFileRecord == null) {
            throw new CDKException("Error in line " + lineCounter + ": Expected the line to specify the molecule or reaction identifier, but instead found '" + line + "'.");
        }

        // line 4: indicates the start of a molfile ($MOL) or an rxnfile ($RNX)
        line = nextLine();
        if (line == null) {
            String expected = rdFileRecord.isMolfile() ? MOLFILE_START : RXNFILE_START;
            throw new CDKException("Error in line " + lineCounter + ": Expected this line to start with '" + expected + "', but instead found '" + line + "'.");
        }

        // read the chemical content into a string
        if (rdFileRecord.isMolfile()) {
            // process molfile
            rdFileRecord.setContent(processMolfile(line, rdFileRecord));
        } else if (rdFileRecord.isRxnFile()) {
            // process rxn
            rdFileRecord.setContent(processRxn(line, rdFileRecord));
        }

        // the next line could be (1) EOF, (2) the start of the data block or (3) the next record
        line = nextLine();
        if (line == null) {
            rdFileRecord.setChemObject(parseChemicalContent(rdFileRecord));
            return rdFileRecord;
        }

        if (line.startsWith(DTYPE)) {
            // process data block
            final Map<Object, Object> dataMap = processDataBlock(line);
            rdFileRecord.setData(dataMap);
        } else if (isStartOfNewRecord(line)) {
            // next record
            pushBack(line);
        } else {
            // anything else is unexpected here
            throw new CDKException("Error in line " + lineCounter + ": Expected start of data block '" + DTYPE + "' or start of next record, but instead found '" + line + "'.");
        }

        rdFileRecord.setChemObject(parseChemicalContent(rdFileRecord));

        return rdFileRecord;
    }

    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }

    private String nextLine() throws IOException {
        if (previousLine != null) {
            final String nextLine = previousLine;
            previousLine = null;
            return nextLine;
        } else {
            lineCounter++;
            return bufferedReader.readLine();
        }
    }

    private void pushBack(String line) {
        previousLine = line;
    }

    private RdfileRecord processMoleculeAndReactionIdentifiers(CharIter iter, String fmt, String intReg, String extReg, boolean isTestForRxn) throws CDKException {
        String intRegNo = null;
        String extRegNo = null;

        if (iter.consume(fmt)) {
            iter.skipWhiteSpace();
            if (iter.consume(intReg)) {
                iter.skipWhiteSpace();
                intRegNo = iter.rest();
            } else if (iter.consume(extReg)) {
                iter.skipWhiteSpace();
                extRegNo = iter.rest();
            } else {
                String rest = iter.rest();
                if (!rest.isEmpty()) {
                    throw new CDKException("Error in line " + lineCounter + ": Expected either '" + MOLECULE_INT_REG + "' or '" + MOLECULE_EXT_REG +
                            "' after '" + MOLECULE_FMT + "', but instead found '" + rest + "'.");
                }
            }

            return new RdfileRecord(intRegNo, extRegNo, isTestForRxn);
        } else if (iter.consume(intReg)) {
            iter.skipWhiteSpace();
            intRegNo = iter.rest();
            return new RdfileRecord(intRegNo, extRegNo, isTestForRxn);
        } else if (iter.consume(extReg)) {
            iter.skipWhiteSpace();
            extRegNo = iter.rest();
            return new RdfileRecord(intRegNo, extRegNo, isTestForRxn);
        }

        return null;
    }

    private String processMolfile(String line, RdfileRecord rdfileRecord) throws IOException, CDKException {
        return processMolfile(line, rdfileRecord, null);
    }

    private String processMolfile(String line, RdfileRecord rdfileRecord, RdfileRecord.CTAB_VERSION ctabVersionRecord) throws IOException, CDKException {
        if (!line.startsWith(MOLFILE_START))
            throw new CDKException("Error in line " + lineCounter + ": Expected the line to start with '" + MOLFILE_START + "', but instead found '" + line + "'.");

        final StringBuilder sb = new StringBuilder();

        line = nextLine();
        // line 1: title (or name)
        if (line == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected a line with the title of the molecule, but instead found '" + line + "'.");
        sb.append(line).append(LINE_SEPARATOR_NEWLINE);
        // line 2: user, program, date, time, dimensions etc.
        line = nextLine();
        if (line == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected a line with program, date and time, dimensions etc., but instead found '" + line + "'.");
        sb.append(line).append(LINE_SEPARATOR_NEWLINE);
        // line 3: comment
        line = nextLine();
        if (line == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected a line with a comment, but instead found '" + line + "'.");
        sb.append(line).append(LINE_SEPARATOR_NEWLINE);
        // line 4: counts
        line = nextLine();
        if (line == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected a line with a comment, but instead found '" + line + "'.");
        // figure out the CTAB version
        Matcher matcher = MDL_CTAB_VERSION.matcher(line);
        if (!matcher.find()) {
            throw new CDKException("Error in line " + lineCounter + ": Expected a counts line that ends with a version of either '" + RdfileRecord.CTAB_VERSION.V2000 +
                    "' or '" + RdfileRecord.CTAB_VERSION.V3000 + "', but instead found '" + line + "'.");
        }
        // the Molfile we are reading may be either (1) the sole molecular content of this RDFile or (2) part of an RXNFile
        // if (2) holds true we already determined a CTAB version for this record and all molecular content of this record must have the same CTAB version
        RdfileRecord.CTAB_VERSION ctabVersion = RdfileRecord.CTAB_VERSION.valueOf(matcher.group(1));
        if (ctabVersionRecord == null) {
            rdfileRecord.setCtabVersion(ctabVersion);
        } else if (ctabVersionRecord != ctabVersion) {
            throw new CDKException("Error in line " + lineCounter + ": Expected the CTAB version '" + ctabVersion +
                    "' of this Molfile to be the same as the CTAB version '" + ctabVersionRecord + "' of the record, but instead found '" + line + "'.");
        }
        sb.append(line).append(LINE_SEPARATOR_NEWLINE);

        while ((line = nextLine()) != null) {
            sb.append(line);
            sb.append(LINE_SEPARATOR_NEWLINE);

            if (line.startsWith(M_END))
                break;

            if (line.startsWith("$"))
                throw new CDKException("Error in line " + lineCounter + ": Unexpected character '$' within molfile at start of line '" + line + "'.");
        }

        final int lastIndexOf = sb.lastIndexOf(M_END);
        if (lastIndexOf == -1 || lastIndexOf != sb.length() - 7) {
            throw new CDKException("Error in line " + lineCounter + ": Expected molfile to end with '" + M_END + "', but instead found '" + line + "'.");
        }

        return sb.toString();
    }

    private String processRxn(String line, RdfileRecord rdfileRecord) throws IOException, CDKException {
        Matcher matcher = MDL_RXN_VERSION.matcher(line);
        if (!matcher.matches()) {
            throw new CDKException("Error in line " + lineCounter + ": Expected the line to start with '" + RXNFILE_START + "', but instead found '" + line + "'.");
        }
        if (matcher.group(1) == null) {
            rdfileRecord.setCtabVersion(RdfileRecord.CTAB_VERSION.V2000);
        } else {
            rdfileRecord.setCtabVersion(RdfileRecord.CTAB_VERSION.V3000);
        }

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(line).append(LINE_SEPARATOR_NEWLINE);

        line = nextLine();
        if (line == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected a line with the title of the reaction, but instead found '" + line + "'.");
        stringBuilder.append(line).append(LINE_SEPARATOR_NEWLINE);

        line = nextLine();
        if (line == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected the line with the header, but instead found '" + line + "'.");
        stringBuilder.append(line).append(LINE_SEPARATOR_NEWLINE);

        line = nextLine();
        if (line == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected a line with a remark, but instead found '" + line + "'.");
        stringBuilder.append(line).append(LINE_SEPARATOR_NEWLINE);

        line = nextLine();
        if (line == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected a line with the counts for the number of reactants, products and agents, but instead found '" + line + "'.");
        final int numReactants = readMdlUInt(line, 0);
        final int numProducts = readMdlUInt(line, 3);
        final int numAgents = readMdlUInt(line, 6);
        if (numReactants == -1 || numProducts == -1) {
            throw new CDKException("Error in line " + lineCounter + ": Incorrect formatting of the line that indicates the number of reaction components '" + line + "'.");
        }
        int numReactionComponents = numReactants + numProducts;
        if (numAgents != -1) {
            numReactionComponents += numAgents;
        }
        stringBuilder.append(line).append(LINE_SEPARATOR_NEWLINE);

        int molfileCounter = 0;
        while ((line = nextLine()) != null) {
            // beginning of data block or next record
            if (line.startsWith(DTYPE) || isStartOfNewRecord(line)) {
                pushBack(line);
                break;
            }

            molfileCounter++;
            stringBuilder.append(MOLFILE_START).append(LINE_SEPARATOR_NEWLINE);
            stringBuilder.append(processMolfile(line, rdfileRecord, rdfileRecord.getCtabVersion()));
        }

        if (molfileCounter != numReactionComponents) {
            throw new CDKException("Error in RXN record: The sum of the number of reactants (" + numReactants + "), products (" + numProducts +
                    ") and agents (" + numAgents + ") is not equal to the number of Molfile entries " + molfileCounter + " in the record.");
        }

        return stringBuilder.toString();
    }

    private Map<Object, Object> processDataBlock(String line) throws CDKException, IOException {
        Map<Object, Object> dataMap = new LinkedHashMap<>();

        while (line != null) {
            // next record
            if (isStartOfNewRecord(line)) {
                pushBack(line);
                break;
            }

            // process $DTYPE
            Matcher dtypeMatcher = DTYPE_KEY.matcher(line);
            if (!dtypeMatcher.matches()) {
                throw new CDKException("Error in data block in line " + lineCounter + ". Expected line to start with '" + DTYPE + "' and be followed by a space and a string, but instead found '" + line + "'.");
            }
            final String key = dtypeMatcher.group(1);

            line = nextLine();
            if (line == null) {
                throw new CDKException("Error in data block in line " + lineCounter + ". Expected line to start with '" + DATUM + "', but instead found '" + line + "'.");
            }

            // process the line that starts with $DATUM
            // a line in a multi line datum might be
            // (1) a line with <= 80 characters
            // (2) a line with 81 characters AND ending with a '+' sign
            StringBuilder stringBuilder = new StringBuilder();
            // first line of $DATUM
            Matcher datumFirstLineMatcher = DATUM_VALUE_FIRSTLINE.matcher(line);
            if (datumFirstLineMatcher.matches()) {
                stringBuilder.append(datumFirstLineMatcher.group(1));
            } else {
                throw new CDKException("Error in data block in line " + lineCounter + ". Expected line to start with '" + DATUM +
                        "' and to either have <= 80 characters or exactly 81 characters and ending with a '+'', but instead found '" + line + "'.");
            }

            // remove trailing plus sign if any
            if (stringBuilder.charAt(stringBuilder.length() - 1) == '+') {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            } else {
                stringBuilder.append(LINE_SEPARATOR_NEWLINE);
            }

            // process all following lines that belong to this $DATUM
            while ((line = nextLine()) != null) {
                // start of either next key-value data item or next record
                if (line.startsWith("$")) {
                    pushBack(line);
                    break;
                }

                if (line.length() <= 80) {
                    stringBuilder.append(line).append(LINE_SEPARATOR_NEWLINE);
                } else if (line.length() == 81 && line.endsWith(PLUS_SIGN)) {
                    stringBuilder.append(line, 0, 80);
                } else {
                    throw new CDKException("Error in data block in line " + lineCounter + ". Expected multi-line datum with either less than or equal to 80 characters " +
                            "or with exactly 81 characters and ending with a '+', but instead found '" + line + "'.");
                }
            }

            // remove trailing newline character sign if any
            if (stringBuilder.charAt(stringBuilder.length() - 1) == '\n') {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }

            dataMap.put(key, stringBuilder.toString());
            line = nextLine();
        }

        return dataMap;
    }

    private IChemObject parseChemicalContent(RdfileRecord rdFileRecord) throws CDKException {
        // parse the molecule data into a chemical object
        IChemObject chemObject;
        // decide whether this record has a RXNfile or a Molfile
        if (rdFileRecord.isMolfile()) {
            // determine the Molfile version
            ISimpleChemObjectReader reader;
            if (rdFileRecord.getCtabVersion() == RdfileRecord.CTAB_VERSION.V2000) {
                reader = new MDLV2000Reader(new StringReader(rdFileRecord.getContent()));
            } else {
                // only other possibility with the specified pattern is 'V3000'
                reader = new MDLV3000Reader(new StringReader(rdFileRecord.getContent()));
            }
            chemObject = reader.read(chemObjectBuilder.newAtomContainer());
        } else {
            // we got a RXN in this record, so let's determine the RXN version
            ISimpleChemObjectReader reader;
            if (rdFileRecord.getCtabVersion() == RdfileRecord.CTAB_VERSION.V2000) {
                reader = new MDLRXNV2000Reader(new StringReader(rdFileRecord.getContent()));
            } else {
                // only other possibility is V3000
                reader = new MDLRXNV3000Reader(new StringReader(rdFileRecord.getContent()));
            }
            chemObject = reader.read(chemObjectBuilder.newReaction());
        }

        // add the data fields as a property to the chemical object
        chemObject.addProperties(rdFileRecord.getData());

        return chemObject;
    }

    private int readMdlUInt(String str, int pos) {
        if (pos + 3 > str.length())
            return -1;
        if (!CharIter.isDigit(str.charAt(pos + 2)))
            return -1;
        int num = str.charAt(pos + 2) - '0';
        if (CharIter.isDigit(str.charAt(pos + 1)))
            num = 10 * (str.charAt(pos + 1) - '0') + num;
        if (CharIter.isDigit(str.charAt(pos)))
            num = 100 * (str.charAt(pos) - '0') + num;
        return num;
    }

    private boolean isStartOfNewRecord(String line) {
        return line.startsWith(REACTION_FMT) || line.startsWith(MOLECULE_FMT)
                || line.startsWith(REACTION_INT_REG) || line.startsWith(REACTION_EXT_REG)
                || line.startsWith(MOLECULE_INT_REG) || line.startsWith(MOLECULE_EXT_REG);
    }
}
