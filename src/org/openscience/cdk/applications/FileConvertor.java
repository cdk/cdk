/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2003  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.io.program.*;
import org.openscience.cdk.io.listener.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.IDCreator;
import java.io.*;
import java.util.*;
import javax.vecmath.*;

/**
 * Program that converts a file from one format to a file with another format.
 * Supported formats are:
 *   input: CML, MDL MOL/SDF file, PDB, PMP, ShelX, SMILES, XYZ
 *  output: CML, MDL MOL/SDF file, PDB, ShelX, SMILES, SVG, XYZ, Gaussian Input
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 *
 * @keyword command line util
 * @keyword file format
 */
public class FileConvertor {

    private org.openscience.cdk.tools.LoggingTool logger;

    private ChemObjectReader cor;
    private String oformat;
    private ChemObjectWriter cow;

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

    public FileConvertor() {
        logger = new LoggingTool(this.getClass().getName());
        logger.dumpSystemProperties();

        settingListener = new TextGUIListener(level);
        propsListener = null;

        this.level = 0;
        this.oformat = "cml";

        chemObjectNames.add("org.openscience.cdk.Molecule");
        chemObjectNames.add("org.openscience.cdk.SetOfMolecules");
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

                ChemFile content = (ChemFile)cor.read((ChemObject)new ChemFile());
                if (content == null) {
                    return false;
                }

                // create output file
                String ofilename = getOutputFileName(ifilename, this.oformat);
                FileWriter fw = new FileWriter(new File(ofilename));
                cow = getChemObjectWriter(this.oformat, fw);
                if (cow == null) {
                    logger.warn("Format " + oformat + " is an unsupported output format.");
                    System.err.println("Unsupported output format!");
                    return false;
                }
                write(content, ofilename);
                cow.close();

                success = true;
            } else {
                System.out.println("Skipping non-file.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + ifilename + " does not exist!");
        } catch (Exception e) {
            logger.debug(e);
        }
        return success;
    }

    /**
     * actual program
     */
    public static void main(String[] args) {
        String input_format = "";
        String output_format = "";
        File input;
        File output;
        int level = 0; // no questions by default
        LoggingTool logger = new LoggingTool("org.openscience.cdk.applications.FileConvertor.main");
        FileConvertor convertor = new FileConvertor();

        // process options
        int firstNonOptionArgument = convertor.parseCommandLineOptions(args);

        // do conversion(s)
        for (int i=firstNonOptionArgument; i < args.length; i++) {
            String inputFilename = args[i];
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

    private ChemObjectReader getChemObjectReader(File file) throws IOException {
        Reader fileReader = new FileReader(file);
        ReaderFactory factory = new ReaderFactory();
        String format = factory.guessFormat(fileReader);
        // reopen file, to force to start at the beginning
        fileReader.close();
        fileReader = new FileReader(file);

        ChemObjectReader reader = null;
        // construct right reader
        if (format.equals("org.openscience.cdk.io.CMLReader")) {
            reader = new CMLReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.IChIReader")) {
            reader = new IChIReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.MDLReader")) {
            reader = new MDLReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.PDBReader")) {
            reader = new PDBReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.PMPReader")) {
            reader = new PMPReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.ShelXReader")) {
            reader = new ShelXReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.SMILESReader")) {
            reader = new SMILESReader(fileReader);
        } else if (format.equals("org.openscience.cdk.io.XYZReader")) {
            reader = new XYZReader(fileReader);
        }
        if (reader != null) {
            if (settingListener != null) {
                reader.addReaderListener(settingListener);
            }
            if (propsListener != null) {
                reader.addReaderListener(propsListener);
            }
        }
        return reader;
    }

    private ChemObjectWriter getChemObjectWriter(String format, Writer fw) {
        ChemObjectWriter writer = null;
        if (format.equalsIgnoreCase("CML")) {
            writer = new CMLWriter(fw);
        } else if (format.equalsIgnoreCase("MOL")) {
            writer = new MDLWriter(fw);
        } else if (format.equalsIgnoreCase("SMI")) {
            writer = new SMILESWriter(fw);
        } else if (format.equalsIgnoreCase("SHELX")) {
            writer = new ShelXWriter(fw);
        } else if (format.equalsIgnoreCase("SVG")) {
            writer = new SVGWriter(fw);
        } else if (format.equalsIgnoreCase("XYZ")) {
            writer = new XYZWriter(fw);
        } else if (format.equalsIgnoreCase("PDB")) {
            writer = new PDBWriter(fw);
        } else if (format.equalsIgnoreCase("GIN")) {
            writer = new GaussianInputWriter(fw);
        }
        if (writer != null) {
            logger.debug(format + " -> " + writer.getClass().getName());
            if (settingListener != null) {
                writer.addWriterListener(settingListener);
            }
            if (propsListener != null) {
                writer.addWriterListener(propsListener);
            }
        } else {
            logger.debug(format + " -> null");
        }
        return writer;
    }

    private String getOutputFileName(String inputFilename, String outputFormat) {
        String outputFilename = inputFilename.substring(0,inputFilename.lastIndexOf('.'));
        outputFilename = outputFilename.substring(outputFilename.lastIndexOf(File.separatorChar) + 1);
        outputFilename = outputFilename + ".";
        String format = this.oformat;
        if (format.equalsIgnoreCase("CML")) {
            outputFilename = outputFilename + "cml";
        } else if (format.equalsIgnoreCase("MOL")) {
            outputFilename = outputFilename + "mol";
        } else if (format.equalsIgnoreCase("SMI")) {
            outputFilename = outputFilename + "smi";
        } else if (format.equalsIgnoreCase("SHELX")) {
            outputFilename = outputFilename + "res";
        } else if (format.equalsIgnoreCase("SVG")) {
            outputFilename = outputFilename + "svg";
        } else if (format.equalsIgnoreCase("XYZ")) {
            outputFilename = outputFilename + "xyz";
        } else if (format.equalsIgnoreCase("PDB")) {
            outputFilename = outputFilename + "pdb";
        } else if (format.equalsIgnoreCase("GIN")) {
            outputFilename = outputFilename + "in";
        }
        return outputFilename;
    }

    /**
     * Parses the options in the command line arguments and returns
     * the index of the first non-option argument.
     */
    private int parseCommandLineOptions(String[] args) {
        int i = 0;

        // parse options
        if (args.length == 0) {
            printHelp();
            System.exit(0);
        }
        while (args[i].startsWith("-")) {
            // parse option
            String option = args[i];
            logger.debug("Parsing option: " + option);
            if (option.startsWith("--question:") && option.length() > 11) {
                String levelString = option.substring(11);
                if (levelString.equals("none")) {
                    this.level = 0;
                } else if (levelString.equals("fewest")) {
                    this.level = 1;
                } else if (levelString.equals("some")) {
                    this.level = 2;
                } else if (levelString.equals("all")) {
                    this.level = 3;
                } else {
                    System.out.println("Unrecognized question level: " + levelString);
                    System.exit(1);
                }
                settingListener = new TextGUIListener(this.level);
            } else if (option.equals("--help") || option.equals("-h")) {
                printHelp();
                System.exit(0);
            } else if (option.startsWith("-o") && option.length() > 2) {
                this.oformat = option.substring(2);
            } else if (option.startsWith("--outputformat:") && option.length() > 15) {
                this.oformat = option.substring(15);
            } else if (option.startsWith("--listoptions:") && option.length() > 14) {
                String format = option.substring(14);
                listOptionsForFormat(format);
                System.exit(0);
            } else if (option.startsWith("--properties:") && option.length() > 13) {
                String filename = option.substring(13);
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
			} else if (option.equals("--addHydrogens")) {
				this.applyHAdding = true;
			} else if (option.equals("--removeHydrogens")) {
				this.applyHRemoval = true;
			} else if (option.equals("--create2DCoordinates")) {
				this.apply2DCleanup = true;
            } else {
                System.out.println("Unrecognized option: " + args[i]);
                System.exit(1);
            }
            i++;
        } // done parsing options

        // return the index of the first non-option command line arguments
        return i;
    }

    private void printHelp() {
        System.out.println(" FileConverter [OPTIONS] <files>");
        System.out.println();
        System.out.println(" Output files are written to the directory from which the program is run.");
        System.out.println(" The default output format is CML 2.");
        System.out.println();
        System.out.println(" OPTIONS:");
        System.out.println("  --help                            Print this help");
        System.out.println("   -h");
        System.out.println("  --question:[none|fewest|some|all] Ask none|fewest|some|all customization questions");
        System.out.println("  --outputformat:<format>           Output the files in the given format");
        System.out.println("   -o<format>");
        System.out.println("  --listoptions:<format>            Output customizable IOSettings for this Writer");
        System.out.println("  --properties:<file>               Java Properties file with IOSetting values");
        System.out.println();
        System.out.println(" OUTPUT FORMATS:");
        System.out.println("  cml    Chemical Markup Language (the default)");
        System.out.println("  gin    Gaussian Input File");
        System.out.println("  mol    MDL molfile");
        System.out.println("  pdb    PDB");
        System.out.println("  shelx  ShelX");
        System.out.println("  smi    SMILES");
        System.out.println("  svg    Scalable Vector Graphics");
        System.out.println("  xyz    XYZ");
    }

    /**
     * Convert the file <code>ifilename</code>.
     *
     * @param ifilename name of input file
     */
    public void listOptionsForFormat(String format) {
        logger.debug("listing writer options");
        settingListener.setLevel(4); // ask all questions
        settingListener.setInputReader(null); // but don't really ask them
        Molecule dummy = new Molecule();
        dummy.addAtom(new Atom("C"));

        StringWriter writer = new StringWriter();
        cow = getChemObjectWriter(format, writer);
        if (cow == null) {
            logger.warn("Format " + oformat + " is an unsupported output format.");
            System.err.println("Unsupported output format!");
        }
        try {
            cow.write(dummy);
        } catch (CDKException e) {
            logger.error("Could not write ChemFile. FIXME: I should recurse!");
        }
        settingListener.setLevel(this.level);
    }

    /**
    * Since we do not know what kind of ChemObject the Writer supports,
    * and we want to output as much information as possible, use
    * the generalized mechanism below.
    */
    private void write(ChemFile cf, String outputFilename) throws IOException {
        if (compare(new ChemFile(), cow.highestSupportedChemObject()) >= 0) {
            // Can write ChemFile, do so
            try {
                cow.write(cf);
            } catch (CDKException e) {
                logger.error("Could not write ChemFile. FIXME: I should recurse!");
            }
        } else {
            logger.info("Cannot write ChemFile, recursing into ChemSequence's.");
            int count = cf.getChemSequenceCount();
            boolean needMoreFiles =
              (compare(new ChemSequence(), cow.highestSupportedChemObject()) < 0) && (count > 1);
            for (int i=0; i < count; i++) {
                if (needMoreFiles) {
                    cow.close(); // possibly closing empty file
                    String fname = outputFilename + "." + (i+1);
                    FileWriter fw = new FileWriter(new File(fname));
                    cow = getChemObjectWriter(this.oformat, fw);
                }
                write(cf.getChemSequence(i), outputFilename);
            }
        }
    }

    private void write(ChemSequence cs, String outputFilename) throws IOException {
        try {
            cow.write(cs);
        } catch (CDKException e) {
            int count = cs.getChemModelCount();
            boolean needMoreFiles =
              (compare(new ChemModel(), cow.highestSupportedChemObject()) < 0) && (count > 1);
            logger.info("Cannot write ChemSequence, recursing into ChemModel's.");
            for (int i=0; i < count; i++) {
                if (needMoreFiles) {
                    cow.close(); // possibly closing empty file
                    String fname = outputFilename + "." + (i+1);
                    FileWriter fw = new FileWriter(new File(fname));
                    cow = getChemObjectWriter(this.oformat, fw);
                }
                write(cs.getChemModel(i), outputFilename);
            }
        }
    }

    private void write(ChemModel cm, String outputFilename) throws IOException {
        try {
            cow.write(cm);
        } catch (CDKException e) {
            logger.info("Cannot write ChemModel, trying Crystal.");
            Crystal crystal = cm.getCrystal();
            if (crystal != null) {
                write(crystal, outputFilename);
            }
            SetOfMolecules som = cm.getSetOfMolecules();
            if (som != null) {
                write(som, outputFilename);
            }
        }
    }

    private void write(Crystal c, String outputFilename) throws IOException {
        try {
            cow.write(c);
        } catch (CDKException e) {
            logger.error("Cannot write Crystal: " + e.getMessage());
        }
    }

    private void write(SetOfMolecules som, String outputFilename) throws IOException {
        try {
            cow.write(som);
        } catch (CDKException e) {
            int count = som.getMoleculeCount();
            boolean needMoreFiles =
              (compare(new SetOfMolecules(), cow.highestSupportedChemObject()) < 0) && (count > 1);
            logger.info("Cannot write SetOfMolecules, recursing into Molecules's.");
            for (int i=0; i < count; i++) {
                if (needMoreFiles) {
                    cow.close(); // possibly closing empty file
                    String fname = outputFilename + "." + (i+1);
                    FileWriter fw = new FileWriter(new File(fname));
                    cow = getChemObjectWriter(this.oformat, fw);
                }
                write(som.getMolecule(i), outputFilename);
            }
        }
    }

    private void write(Molecule molecule, String outputFilename) throws IOException {
		Molecule writedMol = null;
        try {
            if (cow instanceof CMLWriter) {
                IDCreator.createAtomAndBondIDs(molecule);
            }
            if (applyHAdding) {
				System.out.println("Not implemented yet");
				System.exit(1);
			}
            if (applyHRemoval) {
				System.out.println("Not implemented yet");
				System.exit(1);
			}
            if (apply2DCleanup) {
				logger.info("Creating 2D coordinates");
            	StructureDiagramGenerator sdg = new StructureDiagramGenerator();
	            try {
	                sdg.setMolecule(molecule);
	                sdg.generateCoordinates(new Vector2d(0, 1));
	                writedMol = sdg.getMolecule();
	            } catch (Exception exc) {
	                System.out.println("Could not generate coordinates for this molecule.");
	                System.exit(1);
	            }
			}
			if (writedMol == null) writedMol = molecule;
            cow.write(writedMol);
        } catch (CDKException e) {
            logger.error("Cannot write Molecule: " + e.getMessage());
        }
    }

    /**
     * Returns -1 if first object is 'larger' than second, zero
     * if equal and 1 if second is 'larger'.
     */
    private int compare(ChemObject one, ChemObject two) {
        String oneName = one.getClass().getName();
        int oneIndex   = chemObjectNames.indexOf(oneName);
        String twoName = two.getClass().getName();
        int twoIndex   = chemObjectNames.indexOf(twoName);
        int diff = twoIndex - oneIndex;
        logger.debug("Comparing " + oneName + " and " + twoName + ": " + diff);
        return diff;
    }

}



