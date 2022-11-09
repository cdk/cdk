package org.openscience.cdk.io;

import org.openscience.cdk.exception.CDKException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
        final boolean hasMoleculeRecord = processMoleculeAndReactionIdentifiers(new CharIter(line), rdFileRecord, MOLECULE_FMT, MOLECULE_INT_REG, MOLECULE_EXT_REG);

        // the reaction identifier can be one of the following:
        // $RFMT [$RIREG internal-regno]
        // $RFMT [$REREG external-regno]
        // $RIREG internal-regno
        // $REREG external-regno
        final boolean hasReactionRecord = processMoleculeAndReactionIdentifiers(new CharIter(line), rdFileRecord, REACTION_FMT, REACTION_INT_REG, REACTION_EXT_REG);

        if (!hasMoleculeRecord && !hasReactionRecord) {
            // the molecule or reaction identifier is not properly formatted
            throw new CDKException("Error in line" + lineCounter + ": Expected the line to specify the molecule or reaction identifier, but instead found '" + line + "'.");
        }
        while ((line = nextLine()) != null) {
            // next record
            if (line.startsWith(REACTION_FMT) || line.startsWith(MOLECULE_FMT)) {
                pushBack(line);
                break;
            }

            if (hasMoleculeRecord && line.startsWith(MOLFILE_START)) {
                // process molecule
                final RdfileRecord.Molfile molfile = processMolfile(line);
                rdFileRecord.setMolfile(molfile);
            } else if (hasReactionRecord && line.startsWith(RXNFILE_START)){
                // process reaction
                final RdfileRecord.Rxnfile rxnFile = processRxn(line);
                rdFileRecord.setRxnFile(rxnFile);
            } else if (line.startsWith(DTYPE)) {
                // process data block
                final String[] dtypeDatum = processDatum(line);
                rdFileRecord.putData(dtypeDatum[0], dtypeDatum[1]);
            } else {
                throw new CDKException("Error in line " + lineCounter + ": Expected ...");
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

    private boolean processMoleculeAndReactionIdentifiers(CharIter iter, RdfileRecord rdFileRecord, String fmt, String intReg, String extReg) {
        if (iter.consume(fmt)) {
            iter.skipWhiteSpace();
            if (iter.consume(intReg)) {
                iter.skipWhiteSpace();
                rdFileRecord.setInternalRegistryNumber(iter.rest());
            } else if (iter.consume(extReg)) {
                iter.skipWhiteSpace();
                rdFileRecord.setExternalRegistryNumber(iter.rest());
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
    
    private RdfileRecord.Molfile processMolfile(String line) throws IOException, CDKException {
        if (!line.startsWith(MOLFILE_START))
            throw new CDKException("Error in line " + lineCounter + ": Expected the line to start with '" + MOLFILE_START + "', but instead found '" + line + "'.");

        final String title = nextLine();
        if (title == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected a line with the title of the molecule, but instead found '" + line + "'.");

        StringBuilder sb = new StringBuilder();
        sb.append(title).append('\n');
        while ((line = nextLine()) != null) {
            sb.append(line);
            sb.append('\n');
            if (line.startsWith(M_END))
                break;
        }

        return new RdfileRecord.Molfile(title, sb.toString());
    }

    private RdfileRecord.Rxnfile processRxn(String line) throws IOException, CDKException {
        if (!line.startsWith(RXNFILE_START))
            throw new CDKException("Error in line " + lineCounter + ": Expected the line to start with '" + RXNFILE_START + "', but instead found '" + line + "'.");

        final String title = nextLine();
        if (title == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected a line with the title of the reaction, but instead found '" + line + "'.");

        final String header = nextLine();
        if (header == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected the line with the header, but instead found '" + line + "'.");

        final String remark = nextLine();
        if (remark == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected a line with a remark, but instead found '" + line + "'.");

        String counts = nextLine();
        if (counts == null)
            throw new CDKException("Error in line " + lineCounter + ": Expected a line with the counts for the number of reactants, products and agents, but instead found '" + line + "'.");
        int numReactants = readMdlUInt(counts, 0);
        int numProducts = readMdlUInt(counts, 3);
        int numAgents = readMdlUInt(counts, 6);

        List<RdfileRecord.Molfile> reactants = processReactionComponent(numReactants);
        List<RdfileRecord.Molfile> agents = processReactionComponent(numAgents);
        List<RdfileRecord.Molfile> products = processReactionComponent(numProducts);

        return new RdfileRecord.Rxnfile(title, header, remark, reactants, agents, products);
    }

    private List<RdfileRecord.Molfile> processReactionComponent(int numberOfComponents) throws IOException, CDKException {
        final List<RdfileRecord.Molfile> molfiles = new ArrayList<>();

        while (numberOfComponents > 0) {
            String line = nextLine();
            final RdfileRecord.Molfile molfile = processMolfile(line);
            molfiles.add(molfile);
            numberOfComponents--;
        }

        return molfiles;
    }

    private String[] processDatum(String line) throws IOException {
        // process the line that starts with $DTYPE
        CharIter iter = new CharIter(line);
        iter.consume(DTYPE);
        iter.skipWhiteSpace();
        final String dtype = iter.rest();

        // process the line that starts with $DATUM
        line = nextLine();
        iter = new CharIter(line);
        if (!iter.consume(DATUM)) {
            pushBack(line); // not a DATUM line
            return new String[] {dtype, ""};
        }
        iter.skipWhiteSpace();
        String datum = iter.rest();

        // multi line datum
        if (line.length() == 81 && endsWithPlus(line)) {
            StringBuilder sb = new StringBuilder();
            sb.append(datum);
            sb.setLength(sb.length() - 1); // drop '+'
            while ((line = nextLine()) != null) {
                if (line.charAt(0) == '$') {
                    pushBack(line); // not a DATUM continuation
                    break;
                }
                if (line.length() == 81 && endsWithPlus(line)) {
                    sb.append(line);
                    sb.setLength(sb.length() - 1); // drop '+'
                } else {
                    sb.append(line);
                    break;
                }
            }
            datum = sb.toString();
        }

        return new String[] {dtype, datum};
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


    private boolean endsWithPlus(String datum) {
        return datum.charAt(datum.length() - 1) == '+';
    }
}
