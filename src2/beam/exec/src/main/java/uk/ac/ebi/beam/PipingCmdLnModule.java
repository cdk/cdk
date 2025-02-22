/*
 * Copyright (c) 2015. John May
 */

package uk.ac.ebi.beam;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * An abstract module providing much of the boiler plate to implement a simple command line module
 * that consumes from one file (or stream) out produces another. <br> To use the module simply
 * extend it and implement the {@link #process(BufferedReader, BufferedWriter, InputCounter,
 * OptionSet)} method.
 */
public abstract class PipingCmdLnModule implements CmdLnModule {

    private final Charset UTF_8 = Charset.forName("UTF-8");

    private final Object stderrlock = new Object();

    private final char[] PROG_BAR_EMPTY = "                              ".toCharArray();
    private final char[] PROG_BAR_FULL  = "==============================".toCharArray();

    private final String name;
    protected final OptionParser optparser = new OptionParser();

    public PipingCmdLnModule(String name) {
        this.name = name;
        this.optparser.accepts("prog-off", "no progress indicator");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String name() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getHelpInfo() {
        StringWriter sw = new StringWriter();
        sw.append("\n").append(name).append(":\n");
        sw.append("\tusage: beam ").append(name).append("{OPTS}").append(" [{in.smi}] [{out.smi}]\n");
        try {
            optparser.printHelpOn(sw);
        } catch (IOException e) {
            throw new InternalError(e.getMessage());
        }
        return sw.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void exec(String[] args) {
        try {
            process(args);
        } catch (IOException e) {
            System.err.println("Execution error: " + e.getMessage());
        }
    }

    // simply to make the try/catch in exec simpler
    private void process(String[] args) throws IOException {

        OptionSet optset = optparser.parse(args);
        List<?> nonopt = optset.nonOptionArguments();
        
        final File fin = nonopt.size() > 0 ? new File(nonopt.get(0).toString()) 
                                           : null;

        try (InputStream in = fin == null ? System.in : new CountingInputStream(fin);
             OutputStream out = nonopt.size() < 2 ? System.out : new FileOutputStream(nonopt.get(1).toString());
             BufferedWriter bwtr = new BufferedWriter(new OutputStreamWriter(out, UTF_8));
             BufferedReader brdr = new BufferedReader(new InputStreamReader(in, UTF_8))) {

            InputCounter nonFileCounter = new InputCounter() {
                @Override public long count() {
                    return 0;
                }

                @Override public long total() {
                    return -1;
                }
            };
            InputCounter inputCounter = fin == null
                    ? nonFileCounter
                    : (CountingInputStream) in;

            process(brdr, bwtr, inputCounter, optset);
        }
    }

    /**
     * Consume input from a buffered reader and produce something in an output stream (usually
     * line-by-line).
     *
     * @param brdr   input reader (UTF-8)
     * @param bwtr   output reader (UTF-8)
     * @param optset options for the module
     * @throws IOException low-level error, SMILES syntax errors etc should normally be skipped and
     *                     usually reported
     */
    abstract void process(final BufferedReader brdr, final BufferedWriter bwtr, final InputCounter inputCounter, OptionSet optset) throws IOException;

    /**
     * Reports a message to standard error prefixing the module name. The syntax is essentially
     * printf, note no new line.
     *
     * @param str  format string
     * @param args the arguments
     */
    protected void report(String str, Object... args) {
        synchronized (stderrlock) {
            System.err.printf("\r[beam:" + name + "] " + str, args);
        }
    }

    /**
     * Access the ID part of the a SMILES line. The delimiter is included allowing direct
     * concatenation to an output SMILES.
     *
     * @param smi input SMILES
     * @return the id, or empty string
     */
    protected static String suffixedId(String smi) {
        for (int i = 0; i < smi.length(); i++) {
            if (smi.charAt(i) == ' ' || smi.charAt(i) == '\t')
                return smi.substring(i);
        }
        return "";
    }

    protected String makeProgStr(long count, long total) {
        if (total <= 0)
            return "";
        final double done = (double) count / (double) total;
        final char[] fill = Arrays.copyOf(PROG_BAR_EMPTY, PROG_BAR_EMPTY.length);
        final int end = (int) Math.ceil(fill.length * done);
        System.arraycopy(PROG_BAR_FULL, 0, fill, 0, end);
        if (end < fill.length)
            fill[end - 1] = '>';
        final String perc = String.format("%.1f%%%%", done * 100);
        return "[" + new String(fill) + "] " + perc;
    }

    protected String makeProgStr(long count, long total, long elapMs) {
        // estimate time remaining
        long milliRemain = (long) ((elapMs / (double) count) * (total - count));
        long secRemain = TimeUnit.MILLISECONDS.toSeconds(milliRemain);
        long minRemain = TimeUnit.SECONDS.toMinutes(secRemain);
        milliRemain -= TimeUnit.SECONDS.toMillis(secRemain);
        secRemain -= TimeUnit.MINUTES.toSeconds(minRemain);

        final String tRemain = String.format(" %dm%ds%sms    ", minRemain, secRemain, milliRemain);
        return makeProgStr(count, total) + tRemain;
    }

    interface InputCounter {
        long count();

        long total();
    }

    private static final class CountingInputStream extends InputStream implements InputCounter {

        private final InputStream delegate;
        private long count  = 0;
        private long marked = 0;
        private final long limit;

        public CountingInputStream(File fin) throws FileNotFoundException {
            this.delegate = new FileInputStream(fin);
            this.limit = fin.length();
        }

        @Override public int read() throws IOException {
            int b = delegate.read();
            if (b >= 0) count++;
            return b;
        }

        @Override public int read(byte[] b, int off, int len) throws IOException {
            int read = delegate.read(b, off, len);
            if (read>=0)
                count += read;
            return read;
        }

        @Override public long skip(long n) throws IOException {
            long skip = delegate.skip(n);
            count += skip;
            return skip;
        }

        @Override public int available() throws IOException {
            return delegate.available();
        }

        @Override public void close() throws IOException {
            delegate.close();
        }

        @Override public synchronized void mark(int readlimit) {
            delegate.mark(readlimit);
            marked = count;
        }

        @Override public synchronized void reset() throws IOException {
            delegate.reset();
            count = marked;
        }

        @Override public boolean markSupported() {
            return delegate.markSupported();
        }

        @Override public long count() {
            return count;
        }

        @Override public long total() {
            return limit;
        }
    }
}
