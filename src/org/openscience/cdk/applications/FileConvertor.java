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
import org.openscience.cdk.exception.*;
import java.io.*;
import java.util.*;

/**
 * Program that converts a file from one format to a file with another format.
 * Supported formats are:
 *   input: CML, MDL MOL/SDF file, PDB, PMP, ShelX, SMILES, XYZ
 *  output: CML, MDL MOL/SDF file, ShelX, SMILES, XYZ, Gaussian Input
 *
 *  @keyword command line util
 *  @keyword file format
 */
public class FileConvertor {

    private org.openscience.cdk.tools.LoggingTool logger;

    private String iformat;
    private String ifilename;
    private ChemObjectReader cor;

    private String oformat;
    private String ofilename;
    private ChemObjectWriter cow;
    
    private WriterListener writerListener;
    private ReaderListener readerListener;

    private int level;
    
    private ChemFile chemFile;

    private Vector chemObjectNames = new Vector();

    /**
     * Constructs a convertor for the file formats <code>iformat</code>
     * and <code>oformat</code>.
     *
     * @param iformat   format of the input
     * @param oformat   format of the output
     */
    public FileConvertor(String iformat, String oformat, int level) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        logger.dumpSystemProperties();

        this.iformat = iformat;
        this.oformat = oformat;
        this.level = level;
        
        readerListener = new TextReaderListener(level);
        writerListener = new TextWriterListener(level);
        
        logger.debug("Input format: " + iformat);
        logger.debug("Output format: " + oformat);
        logger.debug("Question level: " + level);

        chemObjectNames.add("org.openscience.cdk.Molecule");
        chemObjectNames.add("org.openscience.cdk.SetOfMolecules");
        chemObjectNames.add("org.openscience.cdk.Crystal");
        chemObjectNames.add("org.openscience.cdk.ChemModel");
        chemObjectNames.add("org.openscience.cdk.ChemSequence");
        chemObjectNames.add("org.openscience.cdk.ChemFile");
    }

    /**
     * Convert file <code>ifilename</code> into a file <code>ofilename</code>.
     *
     * @param ifilename name of input file
     * @param ofilename name of output file
     */
    public boolean convert(String ifilename, String ofilename) {
        boolean success = true;
        this.ifilename = ifilename;
        this.ofilename = ofilename;
        try {
            File file = new File(ifilename);
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
            cow = getChemObjectWriter(this.oformat, ofilename);
            if (cow == null) {
                logger.warn("Format " + oformat + " is an unsupported output format.");
                System.err.println("Unsupported output format!");
                return false;
            }
            write(content);
            cow.close();
        } catch (FileNotFoundException e) {
            System.out.println("File " + ifilename + " does not exist!");
            success = false;
        } catch (Exception e) {
            success = false;
            logger.error(e.toString());
            e.printStackTrace();
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
    
    int startFromHere = 0;
    if (args.length < 3) {
      System.err.println("syntax: FileConverter [--question:none|fewest|some|all] -o<format> <input> <output>");
      System.exit(1);
    }
    
    // process options
    for (int i=0; i<args.length-3; i++) {
        String option = args[i];
        if (option.startsWith("--question:") && option.length() > 11) {
            String levelString = option.substring(11);
            if (levelString.equals("none")) {
                level = 0;
            } else if (levelString.equals("fewest")) {
                level = 1;
            } else if (levelString.equals("some")) {
                level = 2;
            } else if (levelString.equals("all")) {
                level = 3;
            }
        }
        startFromHere = i + 1;
    }
    if (args[startFromHere].startsWith("-o")) {
        output_format = args[startFromHere].substring(2);
    }
    String ifilename = args[startFromHere+1];
    String ofilename = args[startFromHere+2];
    
    // do conversion
    FileConvertor fc = new FileConvertor(input_format, output_format, level);
    boolean success = fc.convert(ifilename, ofilename);
    if (success) {
        System.out.println("Conversion succeeded!");
    } else {
        System.out.println("Conversion failed!");
        System.exit(1);
    }
  }

    private ChemObjectReader getChemObjectReader(File file) throws IOException {
        Reader fileReader = new FileReader(file);
        ReaderFactory factory = new ReaderFactory();
        String format = factory.guessFormat(fileReader);
        System.out.println("Detected format: " + format);
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
        return reader;
    }

    private ChemObjectWriter getChemObjectWriter(String format, String ofilename) 
            throws IOException {
        ChemObjectWriter writer = null;
        FileWriter fw = new FileWriter(new File(ofilename));
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
            writer.addWriterListener(writerListener);
        }
        return writer;
    }

    /**
    * Since we do not know what kind of ChemObject the Writer supports,
    * and we want to output as much information as possible, use
    * the generalized mechanism below.
    */
    private void write(ChemFile cf) throws IOException {
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
                    String fname = ofilename + "." + (i+1);
                    cow = getChemObjectWriter(this.oformat, fname);
                }
                write(cf.getChemSequence(i));
            }
        }
    }

    private void write(ChemSequence cs) throws IOException {
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
                    String fname = ofilename + "." + (i+1);
                    cow = getChemObjectWriter(this.oformat, fname);
                }
                write(cs.getChemModel(i));
            }
        }
    }

    private void write(ChemModel cm) throws IOException {
        try {
            cow.write(cm);
        } catch (CDKException e) {
            logger.info("Cannot write ChemModel, trying Crystal.");
            Crystal crystal = cm.getCrystal();
            if (crystal != null) {
                write(crystal);
            }
            SetOfMolecules som = cm.getSetOfMolecules();
            if (som != null) {
                write(som);
            }
        }
    }

    private void write(Crystal c) throws IOException {
        try {
            cow.write(c);
        } catch (CDKException e) {
            logger.error("Cannot write Crystal: " + e.getMessage());
        }
    }

    private void write(SetOfMolecules som) throws IOException {
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
                    String fname = ofilename + "." + (i+1);
                    cow = getChemObjectWriter(this.oformat, fname);
                }
                write(som.getMolecule(i));
            }
        }
    }

    private void write(Molecule molecule) throws IOException {
        try {
            cow.write(molecule);
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



