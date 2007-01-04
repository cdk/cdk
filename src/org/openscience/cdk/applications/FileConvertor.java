/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.vecmath.Vector2d;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.rebond.RebondTool;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.CDKSourceCodeWriter;
import org.openscience.cdk.io.HINWriter;
import org.openscience.cdk.io.IChemObjectIO;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.PDBWriter;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.SMILESWriter;
import org.openscience.cdk.io.SVGWriter;
import org.openscience.cdk.io.ShelXWriter;
import org.openscience.cdk.io.XYZWriter;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.io.listener.TextGUIListener;
import org.openscience.cdk.io.program.GaussianInputWriter;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * Program that converts a file from one format to a file with another format.
 * Supported formats are:
 *   input: CML, MDL MOL/SDF file, PDB, PMP, ShelX, SMILES, XYZ
 *  output: CML, MDL MOL/SDF file, PDB, ShelX, SMILES, SVG, XYZ, 
 *          Gaussian Input, CDK source code
 *
 * @cdk.module applications
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 *
 * @cdk.require      java1.4+
 * @cdk.keyword      command line util
 * @cdk.keyword      file format
 * @cdk.builddepends commons-cli-1.0.jar
 */
public class FileConvertor {

/*
 *  This is a command line application           
 *  Do not convert System.out/err.println() 
 *  to logger statements
 */	

    private LoggingTool logger;

    private IChemObjectBuilder builder;
    private IChemObjectReader cor;
    private String oformat;
    private IChemObjectWriter cow;

    private TextGUIListener settingListener;
    private PropertiesListener propsListener;
    private int level;
    private Vector chemObjectNames = new Vector();

	/* The below three processings are applied on the molecular level
	 * only, and the implementation can be found in write(Molecule);
	 */
    private boolean applyHAdding = false;
    private boolean applyHRemoval = false;
    private boolean apply2DCleanup = false;
    private boolean apply3DRebonding = false;

    public FileConvertor() {
        logger = new LoggingTool();
        LoggingTool.configureLog4j();
        logger.dumpSystemProperties();
        
        builder = DefaultChemObjectBuilder.getInstance();

        settingListener = new TextGUIListener(level);
        propsListener = null;

        this.level = 0;
        this.oformat = "cml";

        chemObjectNames.add("org.openscience.cdk.Molecule");
        chemObjectNames.add("org.openscience.cdk.MoleculeSet");
        chemObjectNames.add("org.openscience.cdk.Crystal");
        chemObjectNames.add("org.openscience.cdk.ChemModel");
        chemObjectNames.add("org.openscience.cdk.ChemSequence");
        chemObjectNames.add("org.openscience.cdk.ChemFile");
    }

    /**
     * Convert the file <code>ifilename</code>.
     *
     * @param ifilename name of input file
     */
    public boolean convert(String ifilename) {
        if (this.level > 0) System.out.println();
        boolean success = false;
        try {
            File file = new File(ifilename);
            if (file.isFile()) {
                cor = getChemObjectReader(file);
                if (cor == null) {
                    logger.warn("The format of the input file is not recognized or not supported.");
                    System.err.println("The format of the input file is not recognized or not supported.");
                    return false;
                }

                IChemFile content = (IChemFile)cor.read(builder.newChemFile());
                if (content == null) {
                    return false;
                }

                // apply modifications
                List containersList = ChemFileManipulator.getAllAtomContainers(content);
                AtomTypeFactory factory = AtomTypeFactory.getInstance(
                    "org/openscience/cdk/config/data/jmol_atomtypes.txt",
                    content.getBuilder()
                );
                for (int i=0; i<containersList.size(); i++) {
                	IAtomContainer container = (IAtomContainer)containersList.get(i);
                	java.util.Iterator atoms = container.atoms();
                    if (applyHAdding || applyHRemoval || apply2DCleanup || apply3DRebonding) {
                        while (atoms.hasNext()) {
                        	IAtom atom = (IAtom)atoms.next();
                            if (!(atom instanceof IPseudoAtom)) {
                                try {
                                    factory.configure(atom);
                                } catch (CDKException exception) {
                                    logger.warn("Could not configure atom: ", exception.getMessage());
                                    logger.debug(exception);
                                }
                            }
                        }
                    }
                    if (applyHAdding) {
                        logger.info("Adding Hydrogens...");
                        HydrogenAdder adder = new HydrogenAdder("org.openscience.cdk.tools.ValencyChecker");
                        adder.addExplicitHydrogensToSatisfyValency(
                        	builder.newMolecule(container)
                        );
                    } else if (applyHRemoval) {
                        for (int atomi=0; atomi<container.getAtomCount(); atomi++) {
                            if (container.getAtom(atomi).getSymbol().equals("H")) {
                                container.removeAtomAndConnectedElectronContainers(container.getAtom(atomi));
                            }
                        }
                    }
                    if (apply3DRebonding) {
                        logger.info("Creating bonds from 3D coordinates");
                        RebondTool rebonder = new RebondTool(2.0, 0.5, 0.5);
                        rebonder.rebond(container);
                        SaturationChecker satChecker = new SaturationChecker();
                        satChecker.saturate(container);
                    }
                    if (apply2DCleanup) {
                        logger.info("Creating 2D coordinates");
                        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                        try {
                            sdg.setMolecule(builder.newMolecule(container), false); // false -> don't make clone!
                            sdg.generateCoordinates(new Vector2d(0, 1));
                            container = sdg.getMolecule();
                            logger.debug("###########", container);
                        } catch (Exception exc) {
                            System.out.println("Could not generate coordinates for this molecule.");
                            logger.info("Could not generate coordinates for this molecule: ", exc.getMessage());
                            logger.debug(exc);
                            System.exit(1);
                        }
                    }
                }
                
                // create output file
                String ofilename = getOutputFileName(ifilename, this.oformat);
                FileWriter fileWriter = new FileWriter(new File(ofilename));
                cow = getChemObjectWriter(this.oformat, fileWriter);
                if (cow == null) {
                    logger.warn("Format ", oformat, " is an unsupported output format.");
                    System.err.println("Unsupported output format!");
                    return false;
                }
                write(content, ofilename);
                cow.close();

                success = true;
            } else {
                System.out.println("Skipping non-file.");
            }
        } catch (FileNotFoundException exception) {
            System.out.println("File " + ifilename + " does not exist!");
        } catch (Exception exception) {
            logger.debug(exception);
        }
        return success;
    }

    /**
     * actual program
     */
    public static void main(String[] args) {
        FileConvertor convertor = new FileConvertor();

        // process options
        String[] filesToConvert = convertor.parseCommandLineOptions(args);

        // do conversion(s)
        for (int i=0; i < filesToConvert.length; i++) {
            String inputFilename = filesToConvert[i];
            System.out.print("Converting " + inputFilename + " ... ");
            boolean success = convertor.convert(inputFilename);
            if (success) {
                System.out.println("succeeded!");
            } else {
                System.out.println("failed!");
            }
        }
    }

    // PRIVATE INTERNAL STUFF

    private IChemObjectReader getChemObjectReader(File file) throws IOException {
        InputStream fileReader = new FileInputStream(file);
        IChemObjectReader reader = new ReaderFactory().createReader(fileReader);
        if (reader != null) {
            if (settingListener != null) {
                reader.addChemObjectIOListener(settingListener);
            }
            if (propsListener != null) {
                reader.addChemObjectIOListener(propsListener);
            }
        }
        return reader;
    }

    private IChemObjectWriter getChemObjectWriter(String format, Writer fileWriter) {
        IChemObjectWriter writer = null;
        try {
            if (format.equalsIgnoreCase("MOL")) {
                writer = new MDLWriter(fileWriter);
            } else if (format.equalsIgnoreCase("SMI")) {
                writer = new SMILESWriter(fileWriter);
            } else if (format.equalsIgnoreCase("SHELX")) {
                writer = new ShelXWriter(fileWriter);
            } else if (format.equalsIgnoreCase("SVG")) {
                writer = new SVGWriter(fileWriter);
            } else if (format.equalsIgnoreCase("XYZ")) {
                writer = new XYZWriter(fileWriter);
            } else if (format.equalsIgnoreCase("PDB")) {
                writer = new PDBWriter(fileWriter);
            } else if (format.equalsIgnoreCase("GIN")) {
                writer = new GaussianInputWriter(fileWriter);
            } else if (format.equalsIgnoreCase("CDK")) {
                writer = new CDKSourceCodeWriter(fileWriter);
            } else if (format.equalsIgnoreCase("HIN")) {
                writer = new HINWriter(fileWriter);
            }
            if (writer != null) {
                logger.debug(format + " -> " + writer.getClass().getName());
                if (settingListener != null) {
                    writer.addChemObjectIOListener(settingListener);
                }
                if (propsListener != null) {
                    writer.addChemObjectIOListener(propsListener);
                }
            } else if (format.equalsIgnoreCase("CML")) {
            	Class cmlWriterClass = this.getClass().getClassLoader().loadClass("org.opscience.cdk.io.CMLWriter");
            	if (cmlWriterClass != null) {
                    writer = (IChemObjectWriter)cmlWriterClass.newInstance();
            	}
            	Constructor constructor = writer.getClass().getConstructor(new Class[]{Writer.class});
            	writer = (IChemObjectWriter)constructor.newInstance(new Object[]{fileWriter});
            } else {
                logger.debug(format + " -> null");
            }
        } catch (Exception exception) {
            logger.error("Could not instantiate writer: ", exception.getMessage());
            logger.debug(exception);
        }
        return writer;
    }

    private String getOutputFileName(String inputFilename, String outputFormat) {
        String outputFilename = inputFilename.substring(0,inputFilename.lastIndexOf('.'));
        outputFilename = outputFilename.substring(outputFilename.lastIndexOf(File.separatorChar) + 1);
        outputFilename = outputFilename + ".";
        if (outputFormat.equalsIgnoreCase("CML")) {
            outputFilename = outputFilename + "cml";
        } else if (outputFormat.equalsIgnoreCase("MOL")) {
            outputFilename = outputFilename + "mol";
        } else if (outputFormat.equalsIgnoreCase("SMI")) {
            outputFilename = outputFilename + "smi";
        } else if (outputFormat.equalsIgnoreCase("SHELX")) {
            outputFilename = outputFilename + "res";
        } else if (outputFormat.equalsIgnoreCase("SVG")) {
            outputFilename = outputFilename + "svg";
        } else if (outputFormat.equalsIgnoreCase("XYZ")) {
            outputFilename = outputFilename + "xyz";
        } else if (outputFormat.equalsIgnoreCase("PDB")) {
            outputFilename = outputFilename + "pdb";
        } else if (outputFormat.equalsIgnoreCase("GIN")) {
            outputFilename = outputFilename + "in";
        } else if (outputFormat.equalsIgnoreCase("CDK")) {
            outputFilename = outputFilename + "java.fragment";
        } else if (outputFormat.equalsIgnoreCase("HIN")) {
            outputFilename = outputFilename + "hin";
        }
        return outputFilename;
    }

    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("FileConvertor", options);
        
        // now report on the supported formats
        System.out.println();
        System.out.println(" OUTPUT FORMATS:");
        System.out.println("  cml    Chemical Markup Language (the default)");
        System.out.println("  gin    Gaussian Input File");
        System.out.println("  hin    Hyperchem file");
        System.out.println("  mol    MDL molfile");
        System.out.println("  pdb    PDB");
        System.out.println("  shelx  ShelX");
        System.out.println("  smi    SMILES");
        System.out.println("  svg    Scalable Vector Graphics");
        System.out.println("  xyz    XYZ");
        
        System.exit(0);
    }
    
    /**
     * Parses the options in the command line arguments and returns
     * the index of the first non-option argument.
     */
    private String[] parseCommandLineOptions(String[] args) {

        Options options = new Options();
        options.addOption("h", "help", false, "give this help page");
        options.addOption(
            OptionBuilder.withLongOpt("question").
                          withDescription("level of IO questions [none|fewest|some|all]").
                          withValueSeparator('=').
                          hasArg().
                          create("q")
        );
        options.addOption(
            OptionBuilder.withLongOpt("outputformat").
                          withDescription("see below for supported formats (CML2 is default)").
                          withValueSeparator('=').
                          hasArg().
                          create("o")
        );
        options.addOption(
            OptionBuilder.withLongOpt("listoptions").
                          withDescription("lists the IO questions for the given format").
                          withValueSeparator('=').
                          hasArg().
                          create("l")
        );
        options.addOption(
            OptionBuilder.withLongOpt("properties").
                          withDescription("Java properties file with the IO settings").
                          withValueSeparator('=').
                          hasArg().
                          create("p")
        );
        options.addOption(
            OptionBuilder.withLongOpt("addHydrogens").
                          withDescription("add explicit hydrogens where missing").
                          create("a")
        );
        options.addOption(
            OptionBuilder.withLongOpt("removeHydrogens").
                          withDescription("remove all explicit hydrogens").
                          create("r")
        );
        options.addOption(
            OptionBuilder.withLongOpt("create2DCoordinates").
                          withDescription("create 2D coordinates using a layout algorithm").
                          create("2")
        );
        options.addOption(
            OptionBuilder.withLongOpt("rebondFrom3DCoordinates").
                          withDescription("calculate bonds from 3D coordinates").
                          create("b")
        );
        
        CommandLine line = null;
        try {
            CommandLineParser parser = new PosixParser();
            line = parser.parse(options, args);
        } catch (ParseException exception) {
            System.err.println("Unexpected exception: " + exception.toString());
        }
    
        if (line.hasOption("o")) {
            this.oformat = line.getOptionValue("o");
        }
        if (line.hasOption("q")) {
            String level = line.getOptionValue("q");
            if ("none".equals(level)) {
                settingListener = new TextGUIListener(0);
            } else if ("fewest".equals(level)) {
                settingListener = new TextGUIListener(1);
            } else if ("some".equals(level)) {
                settingListener = new TextGUIListener(2);
            } else if ("all".equals(level)) {
                settingListener = new TextGUIListener(3);
            } else {
                System.out.println("Only supported levels: none, fewest, some, all");
                System.exit(1);
            }
        }
        if (line.hasOption("l")) {
            listOptionsForIOClass(line.getOptionValue("l"));
            System.exit(0);
        }
        if (line.hasOption("p")) {
            String filename = line.getOptionValue("p");
            try {
                File file = new File(filename);
                Properties props = new Properties();
                props.load(new FileInputStream(file));
                propsListener = new PropertiesListener(props);
                settingListener = null;
            } catch (FileNotFoundException exception) {
                System.out.println("Cannot find properties file: " + filename);
                System.exit(1);
            } catch (IOException exception) {
                System.out.println("Cannot read properties file: " + filename);
                System.exit(1);
            }
        }
        if (line.hasOption("a")) {
            this.applyHAdding = true;
        }
        if (line.hasOption("r")) {
            this.applyHRemoval = true;
        }
        if (line.hasOption("2")) {
            this.apply2DCleanup = true;
        }
        if (line.hasOption("b")) {
            this.apply3DRebonding = true;
        }

        String[] filesToConvert = line.getArgs();
        
        if (filesToConvert.length == 0 || line.hasOption("h")) {
            printHelp(options);
        }
        
        return filesToConvert;
    }

    public void listOptionsForIOClass(String ioClassName) {
        logger.debug("listing IOSetting options");
        
        String className = "org.openscience.cdk.io." + ioClassName;
        try {
            Object readerOrWriter = this.getClass().getClassLoader().
                loadClass(className).newInstance();
            IOSetting[] settings = new IOSetting[0];
            if (readerOrWriter instanceof IChemObjectIO) {
                IChemObjectIO ioClass = (IChemObjectIO)readerOrWriter;
                settings = ioClass.getIOSettings();
            } else {
                String message = "This class is not a CDK ChemObjectIO class";
                System.out.println(message);
                logger.error(message);
                return;
            }
            TextGUIListener listener = new TextGUIListener(4); // ask all questions
            listener.setInputReader(null); // but don't really ask them
            for (int i=0; i<settings.length; i++) {
                IOSetting setting = settings[i];
                if (setting != null) {
                    listener.processIOSettingQuestion(setting);
                } else {
                    String message = "This IOSetting is null";
                    System.out.println(message);
                    logger.warn(message);
                }
            }
        } catch (ClassNotFoundException exception) {
            String message = "This Reader/Writer does not exist: " + className;
            System.out.println(message);
            logger.error(message);
            logger.debug(exception);
        } catch (InstantiationException exception) {
            String message = "Could not instantiate the class: " + className;
            System.out.println(message);
            logger.error(message);
            logger.debug(exception);
        } catch (Exception exception) {
            System.out.println("An unknown exception occured: " + exception.toString());
            logger.debug(exception);
        }
    }

    /**
    * Since we do not know what kind of IChemObject the Writer supports,
    * and we want to output as much information as possible, use
    * the generalized mechanism below.
    */
    private void write(IChemFile chemFile, String outputFilename) throws IOException {
        if (cow.accepts(chemFile.getClass())) {
            // Can write ChemFile, do so
            try {
                cow.write(chemFile);
            } catch (CDKException e) {
                logger.error("Could not write ChemFile. FIXME: I should recurse!");
            }
        } else {
            logger.info("Cannot write ChemFile, recursing into ChemSequence's.");
            int count = chemFile.getChemSequenceCount();
            boolean needMoreFiles = (cow.accepts(IChemSequence.class)) && (count > 1);
            for (int i=0; i < count; i++) {
                if (needMoreFiles) {
                    cow.close(); // possibly closing empty file
                    String fname = outputFilename + "." + (i+1);
                    FileWriter fileWriter = new FileWriter(new File(fname));
                    cow = getChemObjectWriter(this.oformat, fileWriter);
                }
                write(chemFile.getChemSequence(i), outputFilename);
            }
        }
    }

    private void write(IChemSequence sequence, String outputFilename) throws IOException {
        try {
            cow.write(sequence);
        } catch (CDKException exception) {
            int count = sequence.getChemModelCount();
            boolean needMoreFiles = (cow.accepts(IChemModel.class)) && (count > 1);
            logger.info("Cannot write ChemSequence, recursing into ChemModel's.");
            for (int i=0; i < count; i++) {
                if (needMoreFiles) {
                    cow.close(); // possibly closing empty file
                    String fname = outputFilename + "." + (i+1);
                    FileWriter fileWriter = new FileWriter(new File(fname));
                    cow = getChemObjectWriter(this.oformat, fileWriter);
                }
                write(sequence.getChemModel(i), outputFilename);
            }
        }
    }

    private void write(IChemModel cm, String outputFilename) throws IOException {
        try {
            cow.write(cm);
        } catch (CDKException exception) {
            logger.info("Cannot write ChemModel, trying Crystal.");
            ICrystal crystal = cm.getCrystal();
            if (crystal != null) {
                write(crystal, outputFilename);
            }
            IMoleculeSet som = cm.getMoleculeSet();
            if (som != null) {
                write(som, outputFilename);
            }
        }
    }

    private void write(ICrystal c, String outputFilename) throws IOException {
        try {
            cow.write(c);
        } catch (CDKException exception) {
            logger.error("Cannot write Crystal: ", exception.getMessage());
        }
    }

    private void write(IMoleculeSet som, String outputFilename) throws IOException {
        try {
	        if (apply2DCleanup) {
				logger.info("Creating 2D coordinates");
				java.util.Iterator mols = som.molecules();
				StructureDiagramGenerator sdg = new StructureDiagramGenerator();
				while (mols.hasNext()) {
					IMolecule molecule = (IMolecule)mols.next();
		            try {
		                sdg.setMolecule(molecule, false); // false -> don't make clone!
		                sdg.generateCoordinates(new Vector2d(0, 1));
		                molecule = sdg.getMolecule();
		            } catch (Exception exception) {
		                System.out.println("Could not generate coordinates for this molecule.");
		                System.exit(1);
		            }
				}
			}
            cow.write(som);
        } catch (CDKException exception) {
            int count = som.getMoleculeCount();
            boolean needMoreFiles = (cow.accepts(IMoleculeSet.class)) && (count > 1);
            logger.info("Cannot write MoleculeSet, recursing into Molecules's.");
            for (int i=0; i < count; i++) {
                if (needMoreFiles) {
                    cow.close(); // possibly closing empty file
                    String fname = outputFilename + "." + (i+1);
                    FileWriter fileWriter = new FileWriter(new File(fname));
                    cow = getChemObjectWriter(this.oformat, fileWriter);
                }
                write(som.getMolecule(i), outputFilename);
            }
        }
    }

    private void write(IMolecule molecule, String outputFilename) throws IOException {
        try {
            cow.write(molecule);
        } catch (CDKException exception) {
            logger.error("Cannot write molecule: ", exception.getMessage());
            logger.debug(exception);
        }
    }
    
}



