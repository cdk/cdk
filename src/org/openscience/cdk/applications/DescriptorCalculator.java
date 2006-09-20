/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import nu.xom.Document;
import nu.xom.Serializer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.io.SMILESReader;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.qsar.DescriptorEngine;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.tools.LoggingTool;
import org.xmlcml.cml.element.CMLMolecule;

/**
 * Command line utility that calculates QSAR descriptor values.
 *
 * @cdk.module applications
 *
 * @author           Egon Willighagen
 * @cdk.require      java1.5+
 * @cdk.keyword      command line util descriptor calculation
 * @cdk.builddepends commons-cli-1.0.jar
 * @cdk.created      2004-12-02
 */
public class DescriptorCalculator {

    private LoggingTool logger;
    
    private int molcount;
    private static boolean firstTime;
    private boolean inputIsSMILES;
    private String outputFormat = null;
    private String suffix = null;
    private String descType = null;
    private DescriptorEngine engine;

    public DescriptorCalculator() {
        logger = new LoggingTool(this);
        LoggingTool.configureLog4j();
        logger.dumpSystemProperties();

        inputIsSMILES = false;
        outputFormat = "cml";
        suffix = ".cml";
        firstTime = true;
        molcount = 1;
    }

    private void initEngine() {
    	if ("atomic".equalsIgnoreCase(descType)) {
            engine = new DescriptorEngine(DescriptorEngine.ATOMIC);
    	} else if ("bond".equalsIgnoreCase(descType)) {
            engine = new DescriptorEngine(DescriptorEngine.BOND);
    	} else if ("molecular".equalsIgnoreCase(descType)) {
            engine = new DescriptorEngine(DescriptorEngine.MOLECULAR);
    	} else if (descType == null) {
    		engine = new DescriptorEngine(DescriptorEngine.MOLECULAR);
        } else {
            System.out.println("Not a valid descriptor type: " + descType);
            System.out.println("  Should be either: molecular, atomic, or bond.");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        DescriptorCalculator calculator = new DescriptorCalculator();

        // process options
        String fileToProcess = calculator.parseCommandLineOptions(args);
        
        // create the engine specifying which descriptors to calculate
        calculator.initEngine();

        // calculate descriptors
        calculator.process(fileToProcess);
    }

    private void printCMLHeader(Writer writer) throws IOException {
        writer.write("<?xml version=\"1.0\"?>\n");
        writer.write("<list\n");
        writer.write("  xmlns=\"http://www.xml-cml.org/schema\"\n");
        writer.write("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        writer.write("  xsi:schemaLocation=\"http://www.xml-cml.org/schema/cml2/core cmlAll4.4.xsd\">\n");
        writer.flush();
    }

    private void printCMLMolecule(Writer writer, org.openscience.cdk.interfaces.IMolecule molecule) throws Exception {
        logger.info("Writing output in CML format");
        Convertor convertor = new Convertor(true, null);
        ByteArrayOutputStream stringWriter = new ByteArrayOutputStream();
        CMLMolecule cmlMol = convertor.cdkMoleculeToCMLMolecule(molecule);
        Serializer serializer = new Serializer(stringWriter, "ISO-8859-1");
        serializer.setIndent(2);
        serializer.write(new Document(cmlMol));
        String cmlContent = stringWriter.toString();
        BufferedReader reader = new BufferedReader(new StringReader(cmlContent));
        String line = reader.readLine();
        while (line != null) {
        	if (!line.startsWith("<?xml")) {
        		writer.write(line);
        		writer.write("\n");
        	}
        	line = reader.readLine();
        }
        writer.flush();
    }
    private void printTXTMolecule(Writer writer, org.openscience.cdk.interfaces.IMolecule molecule) throws Exception {
        logger.info("Writing output in TXT format");
        String headerLine = "";
        StringWriter stringWriter = new StringWriter();
        List specList = engine.getDescriptorSpecifications();

        for (Iterator it = specList.iterator(); it.hasNext();) {
            DescriptorSpecification spec = (DescriptorSpecification)it.next();
            String title = spec.getImplementationTitle();

            // the title contains the full class path. We just need
            // the last component
            String[] comps = title.split("\\.");
            title = comps[ comps.length-1 ];

            DescriptorValue value = (DescriptorValue)molecule.getProperty(spec);
            if (value == null) {
                logger.warn("This molecule did not have the "+title+" descriptor calculated for it");
                continue;
            }
                
            IDescriptorResult result = value.getValue();
            if (result instanceof DoubleResult) {
                stringWriter.write(((DoubleResult)result).doubleValue()+" ");
                if (firstTime) headerLine = headerLine + title + " ";
            } else if (result instanceof IntegerResult) {
                stringWriter.write(((IntegerResult)result).intValue()+" ");
                if (firstTime) headerLine = headerLine + title + " ";
            } else if (result instanceof BooleanResult) {
                stringWriter.write(((BooleanResult)result).booleanValue()+" ");
                if (firstTime) headerLine = headerLine + title + " ";
            } else if (result instanceof DoubleArrayResult) {
                for (int i = 0; i < ((DoubleArrayResult)result).size(); i++) {
                    stringWriter.write(((DoubleArrayResult)result).get(i)+" ");
                    if (firstTime) headerLine = headerLine + title + "." + i + " ";
                }
            } else if (result instanceof IntegerArrayResult) {
                for (int i = 0; i < ((IntegerArrayResult)result).size(); i++) {
                    stringWriter.write(((IntegerArrayResult)result).get(i)+" ");
                    if (firstTime) headerLine = headerLine + title + "." + i + " ";
                }
            }
        }
        if (firstTime) {
            writer.write(headerLine+"\n");
            firstTime = false;
        }
        writer.write(stringWriter.toString()+"\n");
        writer.flush();
    }
    
    private void processMolecule(Writer writer, org.openscience.cdk.interfaces.IMolecule molecule) throws Exception {
        boolean engineError = false;
        try {
            engine.process(molecule);
        } catch (Exception exception) {
            logger.error("Exception while generating descriptors for molecule: ", exception.getMessage());
            logger.debug(exception);
            engineError = true;
        }
        if (!engineError) {
            if (outputFormat.equals("cml")) printCMLMolecule(writer, molecule);
            else printTXTMolecule(writer,molecule);
            if (!inputIsSMILES) System.out.print(".");
        } else {
            if (!inputIsSMILES) {
                System.out.println("\nMolecule "+molcount+" failed. Run with -Dcdk.debugging=true and look at the log");
            }
        }
    }
    
    public void process(String toProcess) {
        try {
            Writer writer;
            if (inputIsSMILES) {
                writer = new OutputStreamWriter(System.out);
            } else {
                writer = new FileWriter(new File(toProcess + suffix));
            }

            if (outputFormat.equals("cml")) printCMLHeader(writer);
            
            if (inputIsSMILES) {
                SMILESReader reader = new SMILESReader(
                    new StringReader(toProcess)
                );
                MoleculeSet moleculeSet = (MoleculeSet)reader.read(new MoleculeSet());
                org.openscience.cdk.interfaces.IMolecule[] molecules = moleculeSet.getMolecules();
                for (int i=0; i<molecules.length; i++) {
                    processMolecule(writer, molecules[i]);
                }
            } else {
                IteratingMDLReader reader = new IteratingMDLReader(
                    new FileReader(new File(toProcess)),
                    DefaultChemObjectBuilder.getInstance()
                );
                while (reader.hasNext()) {
                    Molecule molecule = (Molecule)reader.next();
                    processMolecule(writer, molecule);
                    molcount++;
                }
            }
            
            if (outputFormat.equals("cml")) printCMLFooter(writer);
            if (!inputIsSMILES) System.out.println("\n");
        } catch (FileNotFoundException exception) {
            logger.debug(exception);
            System.err.println("File not found: " + toProcess);
            System.exit(-1);
        } catch (IOException exception) {
            logger.debug(exception);
            System.err.println("IO exception: " + exception.getMessage());
            exception.printStackTrace();
            System.exit(-1);
        } catch (Exception exception) {
            logger.debug(exception);
            System.err.println("Some exception: " + exception.getMessage());
            exception.printStackTrace();
            System.exit(-1);
        }
    }
        
    private void printCMLFooter(Writer writer) throws IOException {
        writer.write("</list>\n");
        writer.close();
    }

    /**
     * Parses the options in the command line arguments and returns
     * the index of the first non-option argument.
     */
    private String parseCommandLineOptions(String[] args) {

        Options options = new Options();
        options.addOption("h", "help", false, "give this help page");
        options.addOption("s","smiles", false, "input one SMILES string");
        options.addOption("t","type",true,
                "specify which type of descriptor to calculate. "+
                "Possible values are: molecular, atomic, bond");
        options.addOption("o","output",true,
                "Format in which to output descriptors. Options are 'cml' or'txt' to"+
                "indicate CML output or comma seperated text");
 
        CommandLine line = null;
        try {
            CommandLineParser parser = new PosixParser();
            line = parser.parse(options, args);
        } catch (ParseException exception) {
            System.err.println("Unexpected exception: " + exception.toString());
        }

        if (line.hasOption("s") || line.hasOption("smiles")) {
            inputIsSMILES = true;
        } 
        if (line.hasOption("t") || line.hasOption("type")) {
            String optvalue = line.getOptionValue("t");
            descType = optvalue;
        }
        if (line.hasOption("o") || line.hasOption("output")) {
            String optvalue = line.getOptionValue("o");
            if (!optvalue.equals("cml") && !optvalue.equals("txt")) {
                System.out.println("Invalid output format");
                printHelp(options);
            }
            outputFormat = optvalue;
            if (outputFormat.equals("txt")) suffix = ".txt";
        }
    
        String[] filesToConvert = line.getArgs();
        if (filesToConvert.length != 1 || line.hasOption("h") || line.hasOption("help")) {
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
        System.out.println("  txt    Space seperated text");
        
        System.exit(0);
    }
}
