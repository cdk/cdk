/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.io.ChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.qsar.DescriptorEngine;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Command line utility that calculates QSAR descriptor values.
 *
 * @cdk.module applications
 *
 * @author      Egon Willighagen
 * @cdk.created 2004-12-02
 * @cdk.keyword descriptor calculation
 */
public class DescriptorCalculator {

    /** The output format. */
    private String ouputFormat;
    
    private LoggingTool logger;

    public DescriptorCalculator() {
        logger = new LoggingTool(true);
        logger.dumpSystemProperties();

        ouputFormat = "cml";
    }

    public static void main(String[] args) {
        DescriptorCalculator calculator = new DescriptorCalculator();
        
        DescriptorEngine engine = new DescriptorEngine();

        // process options
        String fileToProcess = calculator.parseCommandLineOptions(args);

        IteratingMDLReader reader = null;
        Properties props = new Properties();
        props.setProperty("CMLIDs", "false");
        props.setProperty("NamespacedOutput", "false");
        props.setProperty("XMLDeclaration", "false");
        PropertiesListener propsListener = new PropertiesListener(props);
        try {
            reader = new IteratingMDLReader(
                new FileReader(new File(fileToProcess))
            );
            FileWriter fileWriter = new FileWriter(new File(fileToProcess + ".cml"));
            fileWriter.write("<?xml version=\"1.0\"?>\n");
            fileWriter.write("<list\n");
            fileWriter.write("  xmlns=\"http://www.xml-cml.org/schema/cml2/core\"\n");
            fileWriter.write("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            fileWriter.write("  xsi:schemaLocation=\"http://www.xml-cml.org/schema/cml2/core cmlAll4.4.xsd\">\n");
            while (reader.hasNext()) {
                Molecule molecule = (Molecule)reader.next();
                engine.process(molecule);
                StringWriter stringWriter = new StringWriter();
                CMLWriter writer = new CMLWriter(stringWriter);
                writer.addChemObjectIOListener(propsListener);
                writer.write(molecule);
                writer.close();
                fileWriter.write(stringWriter.toString());
            }
            fileWriter.write("</list>\n");
            fileWriter.close();
        } catch (FileNotFoundException exception) {
            System.err.println("File not found: " + fileToProcess);
            System.exit(-1);
        } catch (IOException exception) {
            System.err.println("IO exception: " + exception.getMessage());
            exception.printStackTrace();
            System.exit(-1);
        } catch (Exception exception) {
            System.err.println("Some exception: " + exception.getMessage());
            exception.printStackTrace();
            System.exit(-1);
        }
        
    }

    /**
     * Parses the options in the command line arguments and returns
     * the index of the first non-option argument.
     */
    private String parseCommandLineOptions(String[] args) {

        Options options = new Options();
        options.addOption("h", "help", false, "give this help page");
        
        CommandLine line = null;
        try {
            CommandLineParser parser = new PosixParser();
            line = parser.parse(options, args);
        } catch (ParseException exception) {
            System.err.println("Unexpected exception: " + exception.toString());
        }
    
        String[] filesToConvert = line.getArgs();
        
        if (filesToConvert.length != 1 || line.hasOption("h")) {
            printHelp(options);
        }
        
        return filesToConvert[0];
    }

    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("DescriptorCalculator", options);
        
        // now report on the supported formats
        System.out.println();
        System.out.println(" OUTPUT FORMATS:");
        System.out.println("  cml    Chemical Markup Language (the default)");
        
        System.exit(0);
    }
}
