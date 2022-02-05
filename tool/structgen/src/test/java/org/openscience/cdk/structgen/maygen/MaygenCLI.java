/*
 MIT License

 Copyright (c) 2021 Mehmet Aziz Yirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction,
 including without limitation the rights to use, copy, modify, merge, publish, distribute,
 sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or
 substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package org.openscience.cdk.structgen.maygen;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaygenCLI {

    public static final String VERSION = "1.8";
    private static final String FORMULA_TEXT = "formula";
    private static final String OUTPUT_FILE = "outputFile";
    private static final String SDF_COORD = "sdfCoord";

    private Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());

    private final File getFileDir(CommandLine cmd) {
        if (cmd.hasOption(OUTPUT_FILE)) {
            String localFiledir = cmd.getOptionValue(OUTPUT_FILE);
            return localFiledir != null ? new File(localFiledir) : null;
        }
        return null;
    }

    public boolean parseArgs(String[] args) throws ParseException {
        Options options = setupOptions();
        CommandLineParser parser = new DefaultParser();
        boolean helpIsPresent = false;
        try {
            CommandLine cmd = parser.parse(options, args);
            maygen.setFormula(cmd.getOptionValue(FORMULA_TEXT));
            if (!cmd.hasOption(FORMULA_TEXT)) {
                maygen.setFuzzyFormula(cmd.getOptionValue("fuzzyFormula"));
            }
            if (cmd.hasOption("help")
                    || (Objects.isNull(maygen.getFormula()) && Objects.isNull(maygen.getFuzzyFormula()))) {
                displayHelpMessage(options);
                helpIsPresent = true;
            } else {
                if (cmd.hasOption("smi") && !cmd.hasOption("sdf")) {
                    maygen.setConsumer(new SmiOutputConsumer(getFileDir(cmd)));
                }
                if (cmd.hasOption("sdf") || cmd.hasOption(SDF_COORD)) {
                    SdfOutputConsumer sdfout = new SdfOutputConsumer(getFileDir(cmd));
                    if (cmd.hasOption(SDF_COORD))
                        sdfout.setCoordinates(true);
                    maygen.setConsumer(sdfout);
                }

                if (cmd.hasOption("verbose")) maygen.setVerbose(true);
                if (cmd.hasOption("boundaryConditions")) maygen.setBoundary(true);
                if (cmd.hasOption("settingElements")) maygen.setSetElement(true);
                if (cmd.hasOption("tsvoutput")) maygen.setTsvoutput(true);
                if (cmd.hasOption("multithread")) maygen.setMultiThread(true);
            }
        } catch (ParseException e) {
            displayHelpMessage(options);
            throw new ParseException("Problem parsing command line");
        }
        return helpIsPresent;
    }

    public void displayHelpMessage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        String header =
                "\nGenerates molecular structures for a given molecular formula."
                        + "\nThe input is a molecular formula string."
                        + "\n\nFor example 'C2OH4'."
                        + "\n\nIf user wants to store output file in a specific directory, that is needed to be specified."
                        + " It is also possible to generate SMILES instead of an SDF file, but it slows down"
                        + " the generation time. For this, use the '-smi' option."
                        + "\n\n";
        String footer = "\nPlease report issues at https://github.com/MehmetAzizYirik/MAYGEN";
        formatter.printHelp("java -jar MAYGEN-" + VERSION + ".jar", header, options, footer, true);
    }

    public Options setupOptions() {
        Options options = new Options();
        Option formulaOption =
                Option.builder("f")
                        .required(false)
                        .hasArg()
                        .longOpt(FORMULA_TEXT)
                        .desc(FORMULA_TEXT)
                        .build();
        options.addOption(formulaOption);
        Option fuzzyFormulaOption =
                Option.builder("fuzzy")
                        .required(false)
                        .hasArg()
                        .longOpt("fuzzyFormula")
                        .desc("fuzzy formula")
                        .build();
        options.addOption(fuzzyFormulaOption);
        Option settingElements =
                Option.builder("setElements")
                        .required(false)
                        .longOpt("settingElements")
                        .desc("User defined valences")
                        .build();
        options.addOption(settingElements);
        Option verboseOption =
                Option.builder("v")
                        .required(false)
                        .longOpt("verbose")
                        .desc("print message")
                        .build();
        options.addOption(verboseOption);
        Option tvsoutput =
                Option.builder("t")
                        .required(false)
                        .longOpt("tsvoutput")
                        .desc(
                                "Output formula, number of structures and execution time in CSV format."
                                        + " In multithread, the 4th column in the output is the number of threads.")
                        .build();
        options.addOption(tvsoutput);
        Option fileDirectory =
                Option.builder("o")
                        .required(false)
                        .hasArg()
                        .optionalArg(true)
                        .longOpt(OUTPUT_FILE)
                        .desc("Store output file")
                        .build();
        options.addOption(fileDirectory);
        Option boundaryConditions =
                Option.builder("b")
                        .required(false)
                        .longOpt("boundaryConditions")
                        .desc("Setting the boundary conditions option")
                        .build();
        options.addOption(boundaryConditions);
        Option multithread =
                Option.builder("m")
                        .required(false)
                        .longOpt("multithread")
                        .desc("Use multi thread")
                        .build();
        options.addOption(multithread);
        Option smiles =
                Option.builder("smi")
                        .required(false)
                        .longOpt("SMILES")
                        .desc("Output in SMILES format")
                        .build();
        options.addOption(smiles);
        Option sdf =
                Option.builder("sdf")
                        .required(false)
                        .longOpt("SDF")
                        .desc("Output in SDF format")
                        .build();
        options.addOption(sdf);
        Option coordinateOption =
                Option.builder(SDF_COORD)
                        .required(false)
                        .longOpt("coordinates")
                        .desc("Output in SDF format with atom coordinates")
                        .build();
        options.addOption(coordinateOption);
        Option help =
                Option.builder("h")
                        .required(false)
                        .longOpt("help")
                        .desc("Displays help message")
                        .build();
        options.addOption(help);
        return options;
    }

    public void run() throws CDKException, IOException, CloneNotSupportedException {
        maygen.run();
    }

    public static void main(String[] args) {
        MaygenCLI cli = new MaygenCLI();
        try {
            if (!cli.parseArgs(args)) {
                cli.run();
            }
        } catch (Exception ex) {
            if (cli.maygen.getVerbose()) {
                String localFormula = Objects.nonNull(cli.maygen.getFormula()) ? cli.maygen.getFormula() : cli.maygen.getFuzzyFormula();
                Logger.getLogger(Maygen.class.getName())
                        .log(Level.SEVERE, ex, () -> "Formula " + localFormula);
            }
        }
    }
}
