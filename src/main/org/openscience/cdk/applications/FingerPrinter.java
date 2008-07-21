/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.applications;

import org.apache.commons.cli.*;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.*;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Command line utility that will generate fingerprints for a set of files.
 *
 * @author Egon Willighagen
 * @cdk.module applications
 * @cdk.svnrev $Revision$
 * @cdk.created 2003-08-14
 */
public class FingerPrinter {

    LoggingTool logger = new LoggingTool();

    private static final List<String> fpTypes = Arrays.asList(new String[]{"std", "extended", "graph", "substruct", "maccs"});
    private String fpType = "std";
    private IFingerprinter fingerprinter;
    private String outputFileName = null;
    private String inputFleName = null;
    private boolean verbose = false;
    private String ifmt = null;

    /*
	 *  This is a command line application            *
	 *  Do not convert these System.out/err.println() *
	 *  to logger statements
	 */

    private void printUsage(Options options) {
        HelpFormatter hformatter = new HelpFormatter();
        hformatter.printHelp("Fingerprinter [OPTIONS] FILE\n\n", "Generate fingerprints from SMI or SDF input\n\n", options, "");
    }

    private void initialize(Options options, String[] args) {
        CommandLine line = null;
        try {
            CommandLineParser parser = new PosixParser();
            line = parser.parse(options, args);
        } catch (ParseException exception) {
            System.err.println("Error parsing command line: " + exception.toString() + "\n\n");
            printUsage(options);
            System.exit(1);
        }

        if (line.hasOption("h") || line.hasOption("help")) {
            printUsage(options);
            System.exit(1);
        }
        if (line.hasOption("v") || line.hasOption("verbose")) {
            verbose = true;
        }
        if (line.hasOption("t") || line.hasOption("type")) {
            String type = line.getOptionValue("t").toLowerCase();
            if (!fpTypes.contains(type)) {
                System.err.println("ERROR: Invalid fingerprint type specified\n");
                printUsage(options);
                System.exit(-1);
            } else fpType = type;
        }
        if (line.hasOption("o") || line.hasOption("out")) {
            outputFileName = line.getOptionValue("o");
        }
        if (line.hasOption("i") || line.hasOption("ifmt")) {
            String fmt = line.getOptionValue("i").toLowerCase();
            if (!fmt.equals("smi") && !fmt.equals("sdf")) {
                System.err.println("ERROR: Invalid input format specified\n");
                printUsage(options);
                System.exit(-1);
            } else ifmt = fmt;
        }

        if (ifmt == null) {
            System.err.println("ERROR: Must specify the input format\n");
            printUsage(options);
            System.exit(-1);
        }
        String[] remainder = line.getArgs();
        if (remainder == null || remainder.length != 1) {
            System.err.println("ERROR: Must specify exactly one input file\n");
            printUsage(options);
            System.exit(-1);
        } else inputFleName = remainder[0];
    }

    private void generateFingerprints() throws IOException, CDKException {
        if (fpType.equals("std")) fingerprinter = new Fingerprinter();
        else if (fpType.equals("extended")) fingerprinter = new ExtendedFingerprinter();
        else if (fpType.equals("graph")) fingerprinter = new GraphOnlyFingerprinter();
        else if (fpType.equals("substruct")) fingerprinter = new SubstructureFingerprinter();
        //else if (fpType.equals("substruct")) fingerprinter = new MACCSFingerprinter();

        DefaultIteratingChemObjectReader reader = null;
        Reader in = new FileReader(new File(inputFleName));
        if (ifmt.equals("smi")) reader = new IteratingSMILESReader(in, NoNotificationChemObjectBuilder.getInstance());
        else if (ifmt.equals("sdf")) reader = new IteratingMDLReader(in, NoNotificationChemObjectBuilder.getInstance());

        int nmol = 0;
        int nskip = 0;

        IMolecule molecule;
        Writer out;
        if (outputFileName == null) out = new BufferedWriter(new OutputStreamWriter(System.out));
        else out = new BufferedWriter(new FileWriter(new File(outputFileName)));

        out.write("CDK Fingerprinter "+fingerprinter.getClass().getName()+" "+fingerprinter.getSize()+" bits\n");
        while (reader.hasNext()) {
            molecule = (IMolecule) reader.next();
            nmol++;
            String title = (String) molecule.getProperty(CDKConstants.TITLE);
            try {
                String fp = fingerprinter.getFingerprint(molecule).toString();
                out.write(title + " " + fp + "\n");
            } catch (Exception e) {
                logger.debug(e);
                System.err.println("ERROR: " + title + " " + e.toString());
                nskip++;
            }
        }
        if (verbose) {
            System.out.println("Processed " + nmol + " molecules, skipped " + nskip + " molecules");
        }
        out.close();
    }

    public static void main(String[] args) {

        FingerPrinter fprinter = new FingerPrinter();

        Options options = new Options();
        options.addOption("h", "help", false, "Give this help page");
        options.addOption("v", "verbose", false, "Verbose output");
        options.addOption(
                OptionBuilder.withLongOpt("type").
                        withArgName("fptype").
                        withDescription("Type of fingerprint [std|extended|graph|substruct|maccs]. Default is 'std'").
                        hasArg().
                        create("t")
        );
        options.addOption(
                OptionBuilder.withLongOpt("out").
                        withArgName("file").
                        withDescription("Output file name. If not specified STDOUT is used").
                        hasArg().
                        create("o")
        );
        options.addOption(
                OptionBuilder.withLongOpt("ifmt").
                        withArgName("format").
                        withDescription("Input file format [smi|sdf]").
                        hasArg().
                        create("i")
        );

        fprinter.initialize(options, args);
        try {
            fprinter.generateFingerprints();
        } catch (FileNotFoundException e) {
            System.err.println("Error in calculating fingerprints\n" + e.toString());
            System.exit(-1);
        } catch (CDKException e) {
            System.err.println("Error in calculating fingerprints\n" + e.toString());
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Error in calculating fingerprints\n" + e.toString());
            System.exit(-1);
        }

    }

}
