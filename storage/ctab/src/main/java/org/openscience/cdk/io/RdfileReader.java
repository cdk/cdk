package org.openscience.cdk.io;

import org.openscience.cdk.exception.CDKException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class RdfileReader implements Closeable {
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

    private final BufferedReader bufferedReader;
    private boolean headerRead = false;
    private String next = null;
    private String prev = null;
    private int lineCounter = 0;

    public RdfileReader(InputStream in) {
        this(new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)));
    }

    public RdfileReader(Reader reader) {
        bufferedReader = new BufferedReader(reader);
    }

    public RdfileRecord read() throws IOException, CDKException {
        final RdfileRecord rdFileRecord = new RdfileRecord();

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
            return null;
        }

        // line 3: molecule and reaction identifiers
        // the molecule identifier can be one of the following:
        // $MFMT [$MIREG internal-regno]
        // $MFMT [$MEREG external-regno]
        // $MIREG internal-regno
        // $MEREG external-regno
        boolean hasMoleculeIdentifier = processMoleculeAndReactionIdentifiers(new CharIter(line), rdFileRecord, MOLECULE_FMT, MOLECULE_INT_REG, MOLECULE_EXT_REG);

        // the reaction identifier can be one of the following:
        // $RFMT [$RIREG internal-regno]
        // $RFMT [$REREG external-regno]
        // $RIREG internal-regno
        // $REREG external-regno
        boolean hasReactionIdentifier = processMoleculeAndReactionIdentifiers(new CharIter(line), rdFileRecord, REACTION_FMT, REACTION_INT_REG, REACTION_EXT_REG);

        if (!hasMoleculeIdentifier && !hasReactionIdentifier) {
            // the molecule or reaction identifier is not properly formatted
            throw new CDKException("Error in line " + lineCounter + ": Expected the line to specify the molecule or reaction identifier, but instead found '" + line + "'.");
        }
        rdFileRecord.setMolfile(hasMoleculeIdentifier);
        rdFileRecord.setRxnFile(hasReactionIdentifier);

        while ((line = nextLine()) != null) {
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
                final String dataBlock = processDataBlock(line);
                rdFileRecord.setDataBlock(dataBlock);
            } else if (isStartOfNewRecord(line)) {
                // next record
                pushBack(line);
                break;
            } else {
                // anything else is unexpected here
                throw new CDKException("Error in line " + lineCounter + ": Expected start of data block '" + DTYPE + "' or start of next record, but instead found '" + line + "'.");
            }
        }

        return rdFileRecord;
    }

    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }

    private String nextLine() throws IOException {
        if (prev != null) {
            next = prev;
            prev = null;
        } else {
            next = bufferedReader.readLine();
            lineCounter++;
        }
        return next;
    }

    private void pushBack(String line) {
        prev = line;
    }

    private boolean processMoleculeAndReactionIdentifiers(CharIter iter, RdfileRecord rdFileRecord, String fmt, String intReg, String extReg) throws CDKException {
        if (iter.consume(fmt)) {
            iter.skipWhiteSpace();
            if (iter.consume(intReg)) {
                iter.skipWhiteSpace();
                rdFileRecord.setInternalRegistryNumber(iter.rest());
            } else if (iter.consume(extReg)) {
                iter.skipWhiteSpace();
                rdFileRecord.setExternalRegistryNumber(iter.rest());
            } else {
                String rest = iter.rest();
                if (!rest.isEmpty()) {
                    throw new CDKException("Error in line " + lineCounter + ": Expected either '" + MOLECULE_INT_REG + "' or '" + MOLECULE_EXT_REG +
                            "' after '" + MOLECULE_FMT + "', but instead found '" + rest + "'.");
                }
            }

            return true;
        } else if (iter.consume(intReg)) {
            iter.skipWhiteSpace();
            rdFileRecord.setInternalRegistryNumber(iter.rest());
            return true;
        } else if (iter.consume(extReg)) {
            iter.skipWhiteSpace();
            rdFileRecord.setExternalRegistryNumber(iter.rest());
            return true;
        }

        return false;
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

    private String processDataBlock(String line) throws CDKException, IOException {
        StringBuilder stringBuilder = new StringBuilder();

        while (line != null) {
            // next record
            if (isStartOfNewRecord(line)) {
                pushBack(line);
                break;
            }

            // process the line that starts with $DTYPE
            if (!line.startsWith(DTYPE)) {
                throw new CDKException("Error in data block in line " + lineCounter + ". Expected line to start with '" + DTYPE + "', but instead found '" + line + "'.");
            }
            stringBuilder.append(line).append("\n");

            // process the line that starts with $DATUM
            line = nextLine();
            if (!line.startsWith(DATUM)) {
                throw new CDKException("Error in data block in line " + lineCounter + ". Expected line to start with '" + DATUM + "', but instead found '" + line + "'.");
            }
            stringBuilder.append(line).append(LINE_SEPARATOR_NEWLINE);

            // multi line datum
            if (line.length() == 81 && line.endsWith("+")) {
                while ((line = nextLine()) != null) {
                    // not a DATUM continuation
                    if (line.charAt(0) == '$') {
                        pushBack(line);
                        break;
                    }

                    stringBuilder.append(line).append(LINE_SEPARATOR_NEWLINE);

                    if (line.length() < 81) {
                        break;
                    }
                }
            }
            line = nextLine();
        }

        return stringBuilder.toString();
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
