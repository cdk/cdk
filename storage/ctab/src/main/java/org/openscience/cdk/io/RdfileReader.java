package org.openscience.cdk.io;

import org.openscience.cdk.exception.CDKException;
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

public class RdfileReader implements Closeable, Iterator<RdfileRecord> {
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
    private static final Pattern MDL_CTAB_VERSION = Pattern.compile("999 (V[23]0{3})\\Z");
    private static final Pattern MDL_RXN_VERSION = Pattern.compile("\\A\\$RXN ?(V3000)?\\Z");

    private final BufferedReader bufferedReader;
    private boolean headerRead;
    private String previousLine;
    private int lineCounter;
    private boolean hasNext;
    private RdfileRecord nextRecord;
    private boolean endOfFile;

    public RdfileReader(InputStream in) {
        this(new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)));
    }

    public RdfileReader(Reader reader) {
        bufferedReader = new BufferedReader(reader);
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

        line = nextLine();
        if (line == null) {
            String expected = rdFileRecord.isMolfile() ? MOLFILE_START : RXNFILE_START;
            throw new CDKException("Error in line " + lineCounter + ": Expected this line to start with '" + expected + "', but instead found '" + line + "'.");
        }

        if (rdFileRecord.isMolfile()) {
            // process molfile
            rdFileRecord.setContent(processMolfile(line));
        } else if (rdFileRecord.isRxnFile()) {
            // process rxn
            rdFileRecord.setContent(processRxn(line));
        }

        line = nextLine();
        if (line == null) {
            return rdFileRecord;
        }

        if (line.startsWith(DTYPE)) {
            // process data block
            final Map<String, String> dataMap = processDataBlock(line);
            rdFileRecord.setData(dataMap);
        } else if (isStartOfNewRecord(line)) {
            // next record
            pushBack(line);
        } else {
            // anything else is unexpected here
            throw new CDKException("Error in line " + lineCounter + ": Expected start of data block '" + DTYPE + "' or start of next record, but instead found '" + line + "'.");
        }

        // parse the molecule data into a chemical object
        // decide whether this record has a RXN or a Molfile
        // determine the file version
        // add the data fields as a property to the chemical object

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

    private String processMolfile(String line) throws IOException, CDKException {
        if (!line.startsWith(MOLFILE_START))
            throw new CDKException("Error in line " + lineCounter + ": Expected the line to start with '" + MOLFILE_START + "', but instead found '" + line + "'.");

        final String title = nextLine();
        if (title == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected a line with the title of the molecule, but instead found '" + line + "'.");

        final StringBuilder sb = new StringBuilder();
        sb.append(title).append(LINE_SEPARATOR_NEWLINE);
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

    private String processRxn(String line) throws IOException, CDKException {
        if (!line.startsWith(RXNFILE_START))
            throw new CDKException("Error in line " + lineCounter + ": Expected the line to start with '" + RXNFILE_START + "', but instead found '" + line + "'.");

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
            stringBuilder.append(processMolfile(line));
        }

        if (molfileCounter != numReactionComponents) {
            throw new CDKException("Error in RXN record: The sum of the number of reactants (" + numReactants + "), products (" + numProducts +
                    ") and agents (" + numAgents + ") is not equal to the number of Molfile entries " + molfileCounter + " in the record.");
        }

        return stringBuilder.toString();
    }

    private Map<String, String> processDataBlock(String line) throws CDKException, IOException {
        Map<String, String> dataMap = new LinkedHashMap<>();

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
                    stringBuilder.append(line.substring(0, 80));
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
